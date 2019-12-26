package hrds.agent.job.biz.core.dbstage.service;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.DataStoreConfBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.utils.ColumnTool;
import hrds.agent.trans.biz.ConnectionTool;
import hrds.commons.codes.FileFormat;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.utils.Constant;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.vector.*;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.orc.OrcFile;
import org.apache.orc.Reader;
import org.apache.orc.RecordReader;
import org.apache.orc.TypeDescription;
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
				+ collectTableBean.getEltDate(), dataStoreConfBean);
	}

	private void createTodayTable(TableBean tableBean, String todayTableName, DataStoreConfBean dataStoreConfBean) {
		List<String> columns = StringUtil.split(tableBean.getColumnMetaInfo(), CollectTableHandleParse.STRSPLIT);
		List<String> types = StringUtil.split(tableBean.getColTypeMetaInfo(), CollectTableHandleParse.STRSPLIT);
		//获取连接
		try (DatabaseWrapper db = ConnectionTool.getDBWrapper(dataStoreConfBean.getData_store_layer_attr())) {
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
			//执行建表语句
			db.execute(sql.toString());
		}
	}

	@Method(desc = "执行读取文件batch提交到数据库的方法", logicStep = "")
	@Override
	public Long call() {
		long count = 0L;
		DatabaseWrapper db = null;
		try {
			//1.获取数据库的连接
			db = ConnectionTool.getDBWrapper(dataStoreConfBean.getData_store_layer_attr());
			//2.开启事务
			db.beginTrans();
			List<String> columnList = StringUtil.split(tableBean.getColumnMetaInfo(), CollectTableHandleParse.STRSPLIT);
			List<String> typeList = StringUtil.split(tableBean.getColTypeMetaInfo(), CollectTableHandleParse.STRSPLIT);
			//3.拼接batch插入数据库的sql
			String batchSql = getBatchSql(columnList, collectTableBean.getHbase_name());
			//TODO 根据存储期限去修改表名称
			//4.根据卸数问价类型读取文件插入到数据库
			if (FileFormat.CSV.getCode().equals(collectTableBean.getDbfile_format())) {
				count = readCsvToDataBase(db, columnList, typeList, batchSql);
			} else if (FileFormat.PARQUET.getCode().equals(collectTableBean.getDbfile_format())) {
				count = readParquetToDataBase(db, columnList, typeList, batchSql);
			} else if (FileFormat.ORC.getCode().equals(collectTableBean.getDbfile_format())) {
				count = readOrcToDataBase(db, columnList, typeList, batchSql);
			} else if (FileFormat.SEQUENCEFILE.getCode().equals(collectTableBean.getDbfile_format())) {
				count = readSequenceToDataBase(db, columnList, typeList, batchSql);
			} else if (FileFormat.DingChang.getCode().equals(collectTableBean.getDbfile_format())) {
				//分隔符为空
				if (StringUtil.isEmpty(collectTableBean.getDatabase_separatorr())) {
					count = readDingChangToDataBase(db, columnList, typeList, batchSql);
				} else {
					count = readFeiDingChangToDataBase(db, columnList, typeList, batchSql);
				}
			} else if (FileFormat.FeiDingChang.getCode().equals(collectTableBean.getDbfile_format())) {
				count = readFeiDingChangToDataBase(db, columnList, typeList, batchSql);
			} else {
				throw new AppSystemException("不支持的卸数文件格式");
			}
			//5.提交事务
			db.commit();
		} catch (Exception e) {
			if (db != null)
				db.rollback();
		} finally {
			if (db != null)
				db.close();
		}
		//6.返回batch插入数据库的数据量
		return count;
	}

	private long readFeiDingChangToDataBase(DatabaseWrapper db, List<String> columnList,
	                                        List<String> typeList, String batchSql) {
		long num = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileAbsolutePath)))) {
			List<Object[]> pool = new ArrayList<>();// 存储全量插入信息的list
			int limit = 50000;
			String line;
			Object[] objs;
			while ((line = reader.readLine()) != null) {
				num++;
				objs = new Object[columnList.size()];// 存储全量插入信息的list
				List<String> valueList = getDingChangValueList(line, tableBean.getColLengthInfo());
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

	private List<String> getDingChangValueList(String line, String colLengthInfo) {
		List<String> valueList = new ArrayList<>();
		List<String> lengthList = StringUtil.split(colLengthInfo, CollectTableHandleParse.STRSPLIT);
		for (String len : lengthList) {
			int length = Integer.parseInt(len);
			valueList.add(line.substring(0, length));
			line = line.substring(length);
		}
		return valueList;
	}

	private long readDingChangToDataBase(DatabaseWrapper db, List<String> columnList, List<String> typeList,
	                                     String batchSql) {
		long num = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileAbsolutePath)))) {
			List<Object[]> pool = new ArrayList<>();// 存储全量插入信息的list
			int limit = 50000;
			String line;
			Object[] objs;
			while ((line = reader.readLine()) != null) {
				num++;
				objs = new Object[columnList.size()];// 存储全量插入信息的list
				List<String> valueList = StringUtil.split(line, String.valueOf(Constant.DATADELIMITER));
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
				List<String> valueList = StringUtil.split(value.toString(), String.valueOf(Constant.DATADELIMITER));
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

	private long readOrcToDataBase(DatabaseWrapper db, List<String> columnList, List<String> typeList,
	                               String batchSql) throws Exception {
		RecordReader rows = null;
		long num = 0L;
		try {
			Path testFilePath = new Path(fileAbsolutePath);
			Configuration configuration = new Configuration();
			Reader reader = OrcFile.createReader(testFilePath, OrcFile.readerOptions(configuration));
			TypeDescription readSchema = ColumnTool.getTypeDescription(tableBean.getColumnMetaInfo(), tableBean.getColTypeMetaInfo());
			Reader.Options readerOptions = new Reader.Options(configuration).schema(readSchema);
			//TODO 了解一下hadoop谓词下推
			rows = reader.rows(readerOptions);
			VectorizedRowBatch batch = readSchema.createRowBatch();
			List<Object[]> pool = new ArrayList<>();// 存储全量插入信息的list
			Object[] objs;
			while (rows.nextBatch(batch)) {
				for (int i = 0; i < batch.size; i++) {
					num++;
					objs = new Object[columnList.size()];// 存储全量插入信息的list
					for (int j = 0; j < columnList.size(); j++) {
						objs[j] = getOrcValue(batch.cols[j], typeList.get(j), i);
					}
					pool.add(objs);
				}
				doBatch(batchSql, pool, num, db);
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

	private Object getOrcValue(ColumnVector columnVector, String columns_type, int rowCount) {
		if (columns_type.contains("INT")) {
			LongColumnVector longColumnVector = (LongColumnVector) columnVector;
			return longColumnVector.vector[rowCount];
		} else if (columns_type.contains("DECIMAL")) {
			DecimalColumnVector longColumnVector = (DecimalColumnVector) columnVector;
			return longColumnVector.vector[rowCount];
		} else if (columns_type.contains("DOUBLE")) {
			DoubleColumnVector doubleColumnVector = (DoubleColumnVector) columnVector;
			return doubleColumnVector.vector[rowCount];
		} else {
			BytesColumnVector structColumnVector = (BytesColumnVector) columnVector;
			byte[] bytes = structColumnVector.vector[rowCount];
			return new String(bytes);
		}
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
				objs[j] = getValue(typeList.get(j), line.getString(columnList.get(j), 0));
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
			csvReader.read();
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
		if ("BOOLEAN".equalsIgnoreCase(type)) {
			// 如果取出的值为null则给空字符串
			str = tmpValue == null ? null : Boolean.parseBoolean(tmpValue);
		} else if ("CHAR".equalsIgnoreCase(type) || "VARCHAR".equalsIgnoreCase(type)
				|| "CLOB".equalsIgnoreCase(type)) {
			// 如果取出的值为null则给空字符串
			str = tmpValue == null ? "" : tmpValue;
			//TODO 这里还有好多类型需要支持
		} else {
			// 如果取出的值为null则给空字符串
			str = tmpValue == null ? null : new BigDecimal(tmpValue);
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

	private String getBatchSql(List<String> columns, String hbase_name) {
		//拼接插入的sql
		StringBuilder sbAdd = new StringBuilder();
		sbAdd.append("insert into ").append(hbase_name).append("(");
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
