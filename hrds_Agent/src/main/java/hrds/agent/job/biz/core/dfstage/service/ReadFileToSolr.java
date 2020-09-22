package hrds.agent.job.biz.core.dfstage.service;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.MD5Util;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.DataStoreConfBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.StorageType;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.hadoop.solr.ISolrOperator;
import hrds.commons.hadoop.solr.SolrFactory;
import hrds.commons.hadoop.solr.SolrParam;
import hrds.commons.utils.Constant;
import hrds.commons.utils.StorageTypeKey;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.orc.OrcFile;
import org.apache.orc.Reader;
import org.apache.orc.RecordReader;
import org.apache.orc.TypeDescription;
import org.apache.orc.mapred.OrcMapredRecordReader;
import org.apache.orc.mapred.OrcStruct;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@DocClass(desc = "读取卸数文件到数据库", author = "zxz", createdate = "2019/12/17 15:43")
public class ReadFileToSolr implements Callable<Long> {
	//打印日志
	private static final Logger LOGGER = LogManager.getLogger();
	//卸数到本地的文件绝对路径
	private final String fileAbsolutePath;
	//数据采集表对应的存储的所有信息
	private final CollectTableBean collectTableBean;
	//数据库采集表对应的meta信息
	private final TableBean tableBean;
	//文件对应存储的目的地信息
	private final DataStoreConfBean dataStoreConfBean;
	//是否是追加
	private final boolean is_append;
	//跑批日期
	private final String etl_date;

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
	public ReadFileToSolr(String fileAbsolutePath, TableBean tableBean, CollectTableBean collectTableBean,
						  DataStoreConfBean dataStoreConfBean) {
		this.fileAbsolutePath = fileAbsolutePath;
		this.collectTableBean = collectTableBean;
		this.dataStoreConfBean = dataStoreConfBean;
		this.tableBean = tableBean;
		this.is_append = StorageType.ZhuiJia.getCode().equals(collectTableBean.getStorage_type());
		this.etl_date = collectTableBean.getEtlDate();
	}

	@Method(desc = "执行读取文件batch提交到数据库的方法", logicStep = "")
	@Override
	public Long call() {
		long count;
		//获取solr的配置
		String configPath = FileNameUtils.normalize(Constant.STORECONFIGPATH
				+ dataStoreConfBean.getDsl_name() + File.separator, true);
		Map<String, String> data_store_connect_attr = dataStoreConfBean.getData_store_connect_attr();
		SolrParam solrParam = new SolrParam();
		solrParam.setSolrZkUrl(data_store_connect_attr.get(StorageTypeKey.solr_zk_url));
		solrParam.setCollection(data_store_connect_attr.get(StorageTypeKey.collection));
		try (ISolrOperator os = SolrFactory.getInstance(JobConstant.SOLRCLASSNAME, solrParam, configPath);
			 SolrClient server = os.getServer()) {
			List<String> columnList = StringUtil.split(tableBean.getColumnMetaInfo(), Constant.METAINFOSPLIT);
			//取值的应该根据原来的类型来
			List<String> sourceTypeList = StringUtil.split(tableBean.getColTypeMetaInfo(), Constant.METAINFOSPLIT);
			//4.根据卸数问价类型读取文件插入到数据库
			//文件编码
			String file_code = tableBean.getFile_code();
			//文件类型
			String file_format = tableBean.getFile_format();
			//列分隔符
			String column_separator = tableBean.getColumn_separator();
			//是否要重新算md5
			boolean isMd5 = !columnList.contains(Constant.MD5NAME);
			//是否包含表头
			String is_header = tableBean.getIs_header();
			if (FileFormat.CSV.getCode().equals(file_format)) {
				count = readCsvToSolr(server, columnList, sourceTypeList, file_code, is_header, isMd5);
			} else if (FileFormat.PARQUET.getCode().equals(file_format)) {
				count = readParquetToSolr(server, columnList, sourceTypeList, isMd5);
			} else if (FileFormat.ORC.getCode().equals(file_format)) {
				count = readOrcToSolr(server, columnList, sourceTypeList, isMd5);
			} else if (FileFormat.SEQUENCEFILE.getCode().equals(file_format)) {
				count = readSequenceToSolr(server, columnList, sourceTypeList, isMd5);
			} else if (FileFormat.DingChang.getCode().equals(file_format)) {
				//分隔符为空
				if (StringUtil.isEmpty(column_separator)) {
					count = readDingChangToSolr(server, columnList, sourceTypeList, file_code, is_header, isMd5);
				} else {
					count = readFeiDingChangToSolr(server, columnList, sourceTypeList,
							column_separator, file_code, is_header, isMd5);
				}
			} else if (FileFormat.FeiDingChang.getCode().equals(file_format)) {
				count = readFeiDingChangToSolr(server, columnList, sourceTypeList,
						column_separator, file_code, is_header, isMd5);
			} else {
				throw new AppSystemException("不支持的卸数文件格式");
			}
		} catch (Exception e) {
			count = -1L;
			LOGGER.error("数据库采集读文件上传到数据库异常", e);
		}
		//6.返回batch插入数据库的数据量
		return count;
	}

	private long readFeiDingChangToSolr(SolrClient server, List<String> columnList,
										List<String> typeList, String dataDelimiter,
										String database_code, String is_header, boolean isMd5) {
		long num = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileAbsolutePath),
				DataBaseCode.ofValueByCode(database_code)))) {
			String line;
			if (IsFlag.Shi.getCode().equals(is_header)) {
				//判断包含表头，先读取表头
				line = reader.readLine();
				if (line != null) {
					LOGGER.info("读取到表头为：" + line);
				}
			}
			List<SolrInputDocument> docs = new ArrayList<>();// 存储全量插入信息的list
			while ((line = reader.readLine()) != null) {
				num++;
				List<String> valueList = StringUtil.split(line, dataDelimiter);
				batchToSolr(server, docs, columnList, typeList, valueList, isMd5, num);
			}
			if (docs.size() != 0) {
				doBatch(server, docs, num);
			}
		} catch (Exception e) {
			throw new AppSystemException("bash插入数据库失败", e);
		}
		return num;
	}

	private long readDingChangToSolr(SolrClient server, List<String> columnList, List<String> typeList,
									 String database_code, String is_header, boolean isMd5) {
		database_code = DataBaseCode.ofValueByCode(database_code);
		long num = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileAbsolutePath),
				database_code))) {
			String line;
			List<String> lengthStrList = StringUtil.split(tableBean.getColLengthInfo(),
					Constant.METAINFOSPLIT);
			List<Integer> lengthList = new ArrayList<>();
			for (String lengthStr : lengthStrList) {
				lengthList.add(Integer.parseInt(lengthStr));
			}
			if (IsFlag.Shi.getCode().equals(is_header)) {
				//判断包含表头，先读取表头
				line = reader.readLine();
				if (line != null) {
					LOGGER.info("读取到表头为：" + line);
				}
			}
			List<SolrInputDocument> docs = new ArrayList<>();// 存储全量插入信息的list
			while ((line = reader.readLine()) != null) {
				num++;
				List<String> valueList = ReadFileToDataBase.getDingChangValueList(line, lengthList, database_code);
				batchToSolr(server, docs, columnList, typeList, valueList, isMd5, num);
			}
			if (docs.size() != 0) {
				doBatch(server, docs, num);
			}
		} catch (Exception e) {
			throw new AppSystemException("bash插入数据库失败", e);
		}
		return num;
	}

	private void batchToSolr(SolrClient server, List<SolrInputDocument> docs, List<String> columnList,
							 List<String> typeList, List<String> valueList, boolean isMd5, long num) {
		SolrInputDocument doc = new SolrInputDocument();
		StringBuilder sb = new StringBuilder();
		doc.addField("table-name", collectTableBean.getHbase_name());
		for (int j = 0; j < columnList.size(); j++) {
			if (isMd5) {
				Object value = ReadFileToDataBase.getValue(typeList.get(j), valueList.get(j), null);
				sb.append(value);
				doc.addField("tf-" + columnList.get(j), value);
			} else {
				if (Constant.MD5NAME.equalsIgnoreCase(columnList.get(j))) {
					if (is_append) {
						doc.addField("id", collectTableBean.getHbase_name() + "_"
								+ ReadFileToDataBase.getValue(typeList.get(j), valueList.get(j), null) + "_" + etl_date);
					} else {
						doc.addField("id", collectTableBean.getHbase_name() + "_"
								+ ReadFileToDataBase.getValue(typeList.get(j), valueList.get(j), null));
					}

				} else {
					doc.addField("tf-" + columnList.get(j), ReadFileToDataBase.
							getValue(typeList.get(j), valueList.get(j), null));
				}
			}
		}
		if (isMd5) {
			if (is_append) {
				doc.addField("id", collectTableBean.getHbase_name() + "_"
						+ MD5Util.md5String(sb.toString()) + "_" + etl_date);
			} else {
				doc.addField("id", collectTableBean.getHbase_name() + "_"
						+ MD5Util.md5String(sb.toString()));
			}
		}
		docs.add(doc);
		if (num % JobConstant.BUFFER_ROW == 0) {
			doBatch(server, docs, num);
		}
	}

	private long readSequenceToSolr(SolrClient server, List<String> columnList,
									List<String> typeList, boolean isMd5) throws Exception {
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
			List<SolrInputDocument> docs = new ArrayList<>();// 存储全量插入信息的list
			while (sfr.next(key, value)) {
				String str = value.toString();
				//XXX SequenceFile不指定分隔符，页面也不允许其指定分隔符，使用hive默认的\001隐藏字符做分隔符
				//XXX 这样只要创建hive映射外部表时使用store as sequencefile hive会自动解析。batch方式使用默认的去解析
				List<String> valueList = StringUtil.split(str, Constant.SEQUENCEDELIMITER);
				num++;
				batchToSolr(server, docs, columnList, typeList, valueList, isMd5, num);
			}
			if (docs.size() != 0) {
				doBatch(server, docs, num);
			}
		} finally {
			if (sfr != null)
				sfr.close();
		}
		return num;
	}

	private long readOrcToSolr(SolrClient server, List<String> columnList,
							   List<String> typeList, boolean isMd5) throws Exception {
		RecordReader rows = null;
		long num = 0L;
		try {
			Reader reader = OrcFile.createReader(new Path(fileAbsolutePath), OrcFile.readerOptions(
					ConfigReader.getConfiguration()));
			rows = reader.rows();
			TypeDescription schema = reader.getSchema();
			List<TypeDescription> children = schema.getChildren();
			VectorizedRowBatch batch = schema.createRowBatch();
			int numberOfChildren = children.size();
			List<SolrInputDocument> docs = new ArrayList<>();// 存储全量插入信息的list
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
					SolrInputDocument doc = new SolrInputDocument();
					StringBuilder sb = new StringBuilder();
					doc.addField("table-name", collectTableBean.getHbase_name());
					for (int i = 0; i < result.getNumFields(); i++) {
						if (isMd5) {
							Object value = ReadFileToDataBase.getValue(typeList.get(i), result.getFieldValue(i), null);
							sb.append(value);
							doc.addField("tf-" + columnList.get(i), value);
						} else {
							if (Constant.MD5NAME.equalsIgnoreCase(columnList.get(i))) {
								if (is_append) {
									doc.addField("id", collectTableBean.getHbase_name() + "_"
											+ ReadFileToDataBase.getValue(typeList.get(i), result.getFieldValue(i), null)
											+ "_" + etl_date);
								} else {
									doc.addField("id", collectTableBean.getHbase_name() + "_"
											+ ReadFileToDataBase.getValue(typeList.get(i), result.getFieldValue(i), null));
								}
							} else {
								doc.addField("tf-" + columnList.get(i),
										ReadFileToDataBase.getValue(typeList.get(i), result.getFieldValue(i), null));
							}
						}
					}
					if (isMd5) {
						if (is_append) {
							doc.addField("id", collectTableBean.getHbase_name() + "_"
									+ MD5Util.md5String(sb.toString()) + "_" + etl_date);
						} else {
							doc.addField("id", collectTableBean.getHbase_name() + "_"
									+ MD5Util.md5String(sb.toString()));
						}
					}
					docs.add(doc);
				}
				doBatch(server, docs, num);
			}
			if (docs.size() != 0) {
				doBatch(server, docs, num);
			}
		} finally {
			if (rows != null)
				rows.close();
		}
		return num;
	}

	private long readParquetToSolr(SolrClient server, List<String> columnList,
								   List<String> typeList, boolean isMd5) {
		ParquetReader<Group> build = null;
		try {
			long num = 0;
			GroupReadSupport readSupport = new GroupReadSupport();
			ParquetReader.Builder<Group> reader = ParquetReader.builder(readSupport, new Path(fileAbsolutePath));
			build = reader.build();
			Group line;
			List<SolrInputDocument> docs = new ArrayList<>();// 存储全量插入信息的list
			while ((line = build.read()) != null) {
				num++;
				SolrInputDocument doc = new SolrInputDocument();
				StringBuilder sb = new StringBuilder();
				doc.addField("table-name", collectTableBean.getHbase_name());
				for (int j = 0; j < columnList.size(); j++) {
					if (isMd5) {
						Object value = ReadFileToDataBase.getParquetValue(typeList.get(j), line, columnList.get(j));
						sb.append(value);
						doc.addField("tf-" + columnList.get(j), value);
					} else {
						if (Constant.MD5NAME.equalsIgnoreCase(columnList.get(j))) {
							if (is_append) {
								doc.addField("id", collectTableBean.getHbase_name() + "_"
										+ ReadFileToDataBase.getParquetValue(typeList.get(j), line,
										columnList.get(j)) + "_" + etl_date);
							} else {
								doc.addField("id", collectTableBean.getHbase_name() + "_"
										+ ReadFileToDataBase.getParquetValue(typeList.get(j), line, columnList.get(j)));
							}
						} else {
							doc.addField("tf-" + columnList.get(j),
									ReadFileToDataBase.getParquetValue(typeList.get(j), line, columnList.get(j)));
						}
					}
				}
				if (isMd5) {
					if (is_append) {
						doc.addField("id", collectTableBean.getHbase_name() + "_"
								+ MD5Util.md5String(sb.toString()) + "_" + etl_date);
					} else {
						doc.addField("id", collectTableBean.getHbase_name() + "_"
								+ MD5Util.md5String(sb.toString()));
					}
				}
				docs.add(doc);
				if (num % JobConstant.BUFFER_ROW == 0) {
					doBatch(server, docs, num);
				}
			}
			if (docs.size() != 0) {
				doBatch(server, docs, num);
			}
			return num;
		} catch (Exception e) {
			throw new AppSystemException("读取parquet文件失败", e);
		} finally {
			try {
				if (build != null)
					build.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private long readCsvToSolr(SolrClient server, List<String> columnList, List<String> typeList,
							   String database_code, String is_header, boolean isMd5) {
		//TODO 分隔符应该使用传进来的，懒得找了，测试的时候一起改吧
		long num = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileAbsolutePath),
				DataBaseCode.ofValueByCode(database_code)));
			 CsvListReader csvReader = new CsvListReader(reader,
					 CsvPreference.EXCEL_PREFERENCE)) {
			List<String> lineList;
			if (IsFlag.Shi.getCode().equals(is_header)) {
				//判断包含表头，先读取表头
				lineList = csvReader.read();
				if (lineList != null) {
					LOGGER.info("读取到表头为：" + lineList.toString());
				}
			}
			List<SolrInputDocument> docs = new ArrayList<>();// 存储全量插入信息的list
			while ((lineList = csvReader.read()) != null) {
				num++;
				batchToSolr(server, docs, columnList, typeList, lineList, isMd5, num);
			}
			if (docs.size() != 0) {
				doBatch(server, docs, num);
			}
		} catch (Exception e) {
			throw new AppSystemException("bash插入数据库失败", e);
		}
		return num;
	}

	private void doBatch(SolrClient server, List<SolrInputDocument> docs, long num) {
		try {
			server.add(docs);
			server.commit();
			LOGGER.info("本次batch插入" + docs.size());
			LOGGER.info("数据库已插入" + num + "条！");
			// 插入成功，清空集合
			docs.clear();
		} catch (Exception e) {
			throw new AppSystemException("提交数据到solr异常", e);
		}
	}

}
