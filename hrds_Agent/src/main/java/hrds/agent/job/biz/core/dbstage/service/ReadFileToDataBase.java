package hrds.agent.job.biz.core.dbstage.service;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.DataStoreConfBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.dbstage.increasement.JDBCIncreasement;
import hrds.agent.job.biz.utils.DataTypeTransform;
import hrds.agent.trans.biz.ConnectionTool;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.FileFormat;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.hadoop.utils.HSqlExecute;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.orc.OrcFile;
import org.apache.orc.Reader;
import org.apache.orc.RecordReader;
import org.apache.orc.TypeDescription;
import org.apache.orc.mapred.OrcMapredRecordReader;
import org.apache.orc.mapred.OrcStruct;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@DocClass(desc = "读取卸数文件到数据库", author = "zxz", createdate = "2019/12/17 15:43")
public class ReadFileToDataBase implements Callable<Long> {
	private final static Logger LOGGER = LoggerFactory.getLogger(ReadFileToDataBase.class);
	//卸数到本地的文件绝对路径
	private String fileAbsolutePath;
	//数据采集表对应的存储的所有信息
	private CollectTableBean collectTableBean;
	//数据库采集表对应的meta信息
	private TableBean tableBean;
	//文件对应存储的目的地信息
	private DataStoreConfBean dataStoreConfBean;

	/**
	 * 读取文件到数据库构造方法
	 *
	 * @param fileAbsolutePath  String
	 *                          含义：本地文件绝对路径
	 * @param tableBean         TableBean
	 *                          含义：文件对应的表结构信息
	 * @param collectTableBean  CollectTableBean
	 *                          含义：文件对应的卸数信息
	 * @param dataStoreConfBean DataStoreConfBean
	 *                          含义：文件需要上传到表对应的存储信息
	 */
	public ReadFileToDataBase(String fileAbsolutePath, TableBean tableBean, CollectTableBean collectTableBean,
	                          DataStoreConfBean dataStoreConfBean) {
		this.fileAbsolutePath = fileAbsolutePath;
		this.collectTableBean = collectTableBean;
		this.dataStoreConfBean = dataStoreConfBean;
		this.tableBean = tableBean;
		createTodayTable(tableBean, collectTableBean.getHbase_name() + "_"
				+ collectTableBean.getEtlDate(), dataStoreConfBean);
	}

	private void createTodayTable(TableBean tableBean, String todayTableName, DataStoreConfBean dataStoreConfBean) {
		List<String> columns = StringUtil.split(tableBean.getColumnMetaInfo(), CollectTableHandleParse.STRSPLIT);
		List<String> types = DataTypeTransform.tansform(StringUtil.split(tableBean.getColTypeMetaInfo(),
				CollectTableHandleParse.STRSPLIT), dataStoreConfBean.getDsl_name());
		//获取连接
		try (DatabaseWrapper db = ConnectionTool.getDBWrapper(dataStoreConfBean.getData_store_connect_attr())) {
			List<String> sqlList = new ArrayList<>();
			//拼接建表语句
			StringBuilder sql = new StringBuilder(120); //拼接创表sql语句
			sql.append("CREATE TABLE ");
			sql.append(todayTableName);
			sql.append("(");
			for (int i = 0; i < columns.size(); i++) {
				sql.append(columns.get(i)).append(" ").append(types.get(i)).append(",");
			}
			//将最后的逗号删除
			sql.deleteCharAt(sql.length() - 1);
			sql.append(")");
			JDBCIncreasement.dropTableIfExists(todayTableName, db, sqlList);
			sqlList.add(sql.toString());
			//执行建表语句
			HSqlExecute.executeSql(sqlList, db);
		}
	}

	@Method(desc = "执行读取文件batch提交到数据库的方法", logicStep = "")
	@Override
	public Long call() {
		long count;
		DatabaseWrapper db = null;
		try {
			//1.获取数据库的连接
			db = ConnectionTool.getDBWrapper(dataStoreConfBean.getData_store_connect_attr());
			//2.开启事务
			db.beginTrans();
			List<String> columnList = StringUtil.split(tableBean.getColumnMetaInfo(), CollectTableHandleParse.STRSPLIT);
			List<String> typeList = DataTypeTransform.tansform(StringUtil.split(tableBean.getColTypeMetaInfo(),
					CollectTableHandleParse.STRSPLIT), dataStoreConfBean.getDsl_name());
			//3.拼接batch插入数据库的sql
			String batchSql = getBatchSql(columnList, collectTableBean.getHbase_name() + "_"
					+ collectTableBean.getEtlDate());
			//TODO 根据存储期限去修改表名称
			//4.根据卸数问价类型读取文件插入到数据库
			if (FileFormat.CSV.getCode().equals(collectTableBean.getDbfile_format())) {
				count = readCsvToDataBase(db, columnList, typeList, batchSql);
			} else if (FileFormat.PARQUET.getCode().equals(collectTableBean.getDbfile_format())) {
				count = readParquetToDataBase(db, columnList, typeList, batchSql);
			} else if (FileFormat.ORC.getCode().equals(collectTableBean.getDbfile_format())) {
				count = readOrcToDataBase(db, typeList, batchSql);
			} else if (FileFormat.SEQUENCEFILE.getCode().equals(collectTableBean.getDbfile_format())) {
				count = readSequenceToDataBase(db, columnList, typeList, batchSql);
			} else if (FileFormat.DingChang.getCode().equals(collectTableBean.getDbfile_format())) {
				//分隔符为空
				if (StringUtil.isEmpty(collectTableBean.getDatabase_separatorr())) {
					count = readDingChangToDataBase(db, columnList, typeList, batchSql,
							collectTableBean.getDatabase_code());
				} else {
					count = readFeiDingChangToDataBase(db, columnList, typeList, batchSql,
							collectTableBean.getDatabase_separatorr());
				}
			} else if (FileFormat.FeiDingChang.getCode().equals(collectTableBean.getDbfile_format())) {
				count = readFeiDingChangToDataBase(db, columnList, typeList, batchSql,
						collectTableBean.getDatabase_separatorr());
			} else {
				throw new AppSystemException("不支持的卸数文件格式");
			}
			//5.提交事务
			db.commit();
		} catch (Exception e) {
			count = -1L;
			if (db != null)
				db.rollback();
			LOGGER.error("数据库采集读文件上传到数据库异常", e);
		} finally {
			if (db != null)
				db.close();
		}
		//6.返回batch插入数据库的数据量
		return count;
	}

	private long readFeiDingChangToDataBase(DatabaseWrapper db, List<String> columnList,
	                                        List<String> typeList, String batchSql, String dataDelimiter) {
		long num = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileAbsolutePath)))) {
			List<Object[]> pool = new ArrayList<>();// 存储全量插入信息的list
			int limit = 50000;
			String line;
			Object[] objs;
			while ((line = reader.readLine()) != null) {
				num++;
				objs = new Object[columnList.size()];// 存储全量插入信息的list
				List<String> valueList = StringUtil.split(line, String.valueOf(dataDelimiter));
				for (int j = 0; j < columnList.size(); j++) {
					objs[j] = getValue(typeList.get(j), valueList.get(j));
				}
				pool.add(objs);
				if (num % limit == 0) {
					doBatch(batchSql, pool, num, db);
				}
			}
			if (pool.size() != 0) {
				doBatch(batchSql, pool, num, db);
			}
		} catch (Exception e) {
			throw new AppSystemException("bash插入数据库失败", e);
		}
		return num;
	}

	private List<String> getDingChangValueList(String line, String colLengthInfo, String database_code)
			throws Exception {
		String code = DataBaseCode.ofValueByCode(database_code);
		List<String> valueList = new ArrayList<>();
		List<String> lengthList = StringUtil.split(colLengthInfo, CollectTableHandleParse.STRSPLIT);
		byte[] bytes = line.getBytes(code);
		int begin = 0;
		for (String len : lengthList) {
			int length = Integer.parseInt(len);
			byte[] byteTmp = new byte[length];
			System.arraycopy(bytes, begin, byteTmp, 0, length);
			begin += length;
			valueList.add(new String(byteTmp, code));
		}
		return valueList;
	}

	private long readDingChangToDataBase(DatabaseWrapper db, List<String> columnList, List<String> typeList,
	                                     String batchSql, String database_code) {
		long num = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileAbsolutePath)))) {
			List<Object[]> pool = new ArrayList<>();// 存储全量插入信息的list
			int limit = 50000;
			String line;
			Object[] objs;
			while ((line = reader.readLine()) != null) {
				num++;
				objs = new Object[columnList.size()];// 存储全量插入信息的list
				List<String> valueList = getDingChangValueList(line, tableBean.getColLengthInfo(), database_code);
				for (int j = 0; j < columnList.size(); j++) {
					objs[j] = getValue(typeList.get(j), valueList.get(j));
				}
				pool.add(objs);
				if (num % limit == 0) {
					doBatch(batchSql, pool, num, db);
				}
			}
			if (pool.size() != 0) {
				doBatch(batchSql, pool, num, db);
			}
		} catch (Exception e) {
			throw new AppSystemException("bash插入数据库失败", e);
		}
		return num;
	}

	private long readSequenceToDataBase(DatabaseWrapper db, List<String> columnList, List<String> typeList,
	                                    String batchSql) throws Exception {
		Configuration conf = ConfigReader.getConfiguration();
		conf.setBoolean("fs.hdfs.impl.disable.cache", true);
		conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
		conf.set("dfs.client.block.write.replace-datanode-on-failure.enable", "true");
		conf.set("fs.defaultFS", "file:///");
		long num = 0L;
		SequenceFile.Reader sfr = null;
		try {
			SequenceFile.Reader.Option optionFile = SequenceFile.Reader.file((new Path(fileAbsolutePath)));
			sfr = new SequenceFile.Reader(conf, optionFile);
			NullWritable key = NullWritable.get();
			Text value = new Text();
			List<Object[]> pool = new ArrayList<>();// 存储全量插入信息的list
			int limit = 50000;
			Object[] objs;
			while (sfr.next(key, value)) {
				String str = value.toString();
				//XXX SequenceFile不指定分隔符，页面也不允许其指定分隔符，使用hive默认的\001隐藏字符做分隔符
				//XXX 这样只要创建hive映射外部表时使用store as sequencefile hive会自动解析。batch方式使用默认的去解析
				List<String> valueList = StringUtil.split(str, JobConstant.SEQUENCEDELIMITER);
				objs = new Object[columnList.size()];// 存储全量插入信息的list
				for (int j = 0; j < columnList.size(); j++) {
					objs[j] = getValue(typeList.get(j), valueList.get(j));
				}
				num++;
				pool.add(objs);
				if (num % limit == 0) {
					doBatch(batchSql, pool, num, db);
				}
			}
			if (pool.size() != 0) {
				doBatch(batchSql, pool, num, db);
			}
		} finally {
			if (sfr != null)
				sfr.close();
		}
		return num;
	}

	private long readOrcToDataBase(DatabaseWrapper db, List<String> typeList,
	                               String batchSql) throws Exception {
		RecordReader rows = null;
		long num = 0L;
		try {
			List<Object[]> pool = new ArrayList<>();// 存储全量插入信息的list
			Object[] objs;
			Reader reader = OrcFile.createReader(new Path(fileAbsolutePath), OrcFile.readerOptions(
					ConfigReader.getConfiguration()));
			rows = reader.rows();
			TypeDescription schema = reader.getSchema();
			List<TypeDescription> children = schema.getChildren();
			VectorizedRowBatch batch = schema.createRowBatch();
			int numberOfChildren = children.size();
			while (rows.nextBatch(batch)) {
				for (int r = 0; r < batch.size; r++) {
					OrcStruct result = new OrcStruct(schema);
					for (int i = 0; i < numberOfChildren; ++i) {
						OrcMapredRecordReader.nextValue(batch.cols[i], r,
								children.get(i), result.getFieldValue(i));
						result.setFieldValue(i, OrcMapredRecordReader.nextValue(batch.cols[i], r,
								children.get(i), result.getFieldValue(i)));
					}
					num++;
					objs = new Object[result.getNumFields()];// 存储全量插入信息的list
					for (int i = 0; i < result.getNumFields(); i++) {
						objs[i] = getValue(typeList.get(i), result.getFieldValue(i).toString());
					}
					pool.add(objs);
					doBatch(batchSql, pool, num, db);
				}
			}
			if (pool.size() != 0) {
				doBatch(batchSql, pool, num, db);
			}
		} finally {
			if (rows != null)
				rows.close();
		}
		return num;
	}

	private long readParquetToDataBase(DatabaseWrapper db, List<String> columnList, List<String> typeList,
	                                   String batchSql) throws Exception {
		GroupReadSupport readSupport = new GroupReadSupport();
		ParquetReader.Builder<Group> reader = ParquetReader.builder(readSupport, new Path(fileAbsolutePath));
		ParquetReader<Group> build = reader.build();
		Group line;
		long num = 0;
		List<Object[]> pool = new ArrayList<>();// 存储全量插入信息的list
		int limit = 50000;
		Object[] objs;
		while ((line = build.read()) != null) {
			objs = new Object[columnList.size()];// 存储全量插入信息的list
			for (int j = 0; j < columnList.size(); j++) {
				objs[j] = getParquetValue(typeList.get(j), line, columnList.get(j));
			}
			num++;
			pool.add(objs);
			if (num % limit == 0) {
				doBatch(batchSql, pool, num, db);
			}
		}
		if (pool.size() != 0) {
			doBatch(batchSql, pool, num, db);
		}
		return num;
	}

	private Object getParquetValue(String type, Group line, String column) {
		Object str;
		if (type.contains("BOOLEAN")) {
			// 如果取出的值为null则给空字符串
			str = line.getBoolean(column, 0);
		} else if (type.contains("CHAR") || type.contains("CLOB")) {
			// 如果取出的值为null则给空字符串
			str = line.getString(column, 0);
		} else if (type.contains("INT")) {
			str = line.getInteger(column, 0);
		} else if (type.contains("FLOAT")) {
			str = line.getFloat(column, 0);
		} else if (type.contains("DOUBLE") || type.contains("DECIMAL") || type.contains("NUMERIC")) {
			str = line.getDouble(column, 0);
		} else {
			// 如果取出的值为null则给空字符串
			str = line.getString(column, 0);
			//TODO 这里应该有好多类型需要支持，然后在else里面报错
		}
		return str;
	}

	private long readCsvToDataBase(DatabaseWrapper db, List<String> columnList, List<String> typeList,
	                               String batchSql) {
		//TODO 分隔符应该使用传进来的，懒得找了，测试的时候一起改吧
		long num = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileAbsolutePath),
				StandardCharsets.UTF_8)); CsvListReader csvReader = new CsvListReader(reader,
				CsvPreference.EXCEL_PREFERENCE)) {
			List<Object[]> pool = new ArrayList<>();// 存储全量插入信息的list
			int limit = 50000;
			List<String> lineList;
			Object[] objs;
			while ((lineList = csvReader.read()) != null) {
				objs = new Object[columnList.size()];// 存储全量插入信息的list
				for (int j = 0; j < columnList.size(); j++) {
					objs[j] = getValue(typeList.get(j), lineList.get(j));
				}
				num++;
				pool.add(objs);
				if (num % limit == 0) {
					doBatch(batchSql, pool, num, db);
				}
			}
			if (pool.size() != 0) {
				doBatch(batchSql, pool, num, db);
			}
		} catch (Exception e) {
			throw new AppSystemException("bash插入数据库失败", e);
		}
		return num;
	}

	private Object getValue(String type, String tmpValue) {
		Object str;
		if (type.contains("BOOLEAN")) {
			// 如果取出的值为null则给空字符串
			str = tmpValue == null ? null : Boolean.parseBoolean(tmpValue.trim());
		} else if (type.contains("CHAR") || type.contains("CLOB")) {
			// 如果取出的值为null则给空字符串
			str = tmpValue == null ? "" : tmpValue;
		} else if (type.contains("BIGINT") || type.contains("DECIMAL") || type.contains("DOUBLE")
				|| type.contains("NUMERIC") || type.contains("INT8") || type.contains("INT4")) {
			// 如果取出的值为null则给空字符串
			str = tmpValue == null ? null : new BigDecimal(tmpValue.trim());
		} else {
			// 如果取出的值为null则给空字符串
			str = tmpValue == null ? "" : tmpValue;
			//TODO 这里应该有好多类型需要支持，然后在else里面报错
		}
		return str;
	}

	private void doBatch(String batchSql, List<Object[]> pool, long num, DatabaseWrapper db) {
		int[] ints = db.execBatch(batchSql, pool);
		for (int i : ints) {
			if (i != 1) {
				throw new AppSystemException("批量插入数据出现错误,退出");
			}
		}
		LOGGER.info("数据库已插入" + num + "条！");
		pool.clear();// 插入成功，清空集合
	}

	private String getBatchSql(List<String> columns, String todayTableName) {
		//拼接插入的sql
		StringBuilder sbAdd = new StringBuilder();
		sbAdd.append("insert into ").append(todayTableName).append("(");
		for (String column : columns) {
			sbAdd.append(column).append(",");
		}
		sbAdd.deleteCharAt(sbAdd.length() - 1);
		sbAdd.append(") values(");
		for (int i = 0; i < columns.size(); i++) {
			if (i != columns.size() - 1) {
				sbAdd.append("?").append(",");
			} else {
				sbAdd.append("?");
			}
		}
		sbAdd.append(")");
		return sbAdd.toString();
	}
}
