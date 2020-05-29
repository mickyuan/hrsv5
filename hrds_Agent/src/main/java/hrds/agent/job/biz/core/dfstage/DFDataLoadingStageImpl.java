package hrds.agent.job.biz.core.dfstage;

import com.jcraft.jsch.Session;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.constant.DataTypeConstant;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.increasement.impl.IncreasementByMpp;
import hrds.agent.job.biz.utils.DataTypeTransform;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.agent.job.biz.utils.TypeTransLength;
import hrds.commons.codes.*;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.utils.HSqlExecute;
import hrds.commons.utils.Constant;
import hrds.commons.utils.StorageTypeKey;
import hrds.commons.utils.jsch.SFTPChannel;
import hrds.commons.utils.jsch.SFTPDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据文件采集，数据加载阶段实现", author = "WangZhengcheng")
public class DFDataLoadingStageImpl extends AbstractJobStage {
	private final static Logger LOGGER = LoggerFactory.getLogger(DFDataLoadingStageImpl.class);
	//数据采集表对应的存储的所有信息
	private CollectTableBean collectTableBean;

	public DFDataLoadingStageImpl(CollectTableBean collectTableBean) {
		this.collectTableBean = collectTableBean;
	}

	@Method(desc = "数据文件采集，数据加载阶段实现，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		long startTime = System.currentTimeMillis();
		LOGGER.info("------------------表" + collectTableBean.getHbase_name()
				+ "DB文件采集数据加载阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, collectTableBean.getTable_id(),
				StageConstant.DATALOADING.getCode());
		try {
			if (UnloadType.ZengLiangXieShu.getCode().equals(collectTableBean.getUnload_type())) {
				LOGGER.info("表" + collectTableBean.getHbase_name()
						+ "增量卸数数据加载阶段不用做任何操作");
			} else if (UnloadType.QuanLiangXieShu.getCode().equals(collectTableBean.getUnload_type())) {
				List<DataStoreConfBean> dataStoreConfBeanList = collectTableBean.getDataStoreConfBean();
				for (DataStoreConfBean dataStoreConfBean : dataStoreConfBeanList) {
					//根据存储类型上传到目的地
					if (Store_type.DATABASE.getCode().equals(dataStoreConfBean.getStore_type())) {
						//传统数据库有两种情况，支持外部表和不支持外部表
						if (IsFlag.Shi.getCode().equals(dataStoreConfBean.getIs_hadoopclient())) {
							//支持外部表
							String todayTableName = collectTableBean.getHbase_name() + "_" + 1;
							//通过外部表加载数据到todayTableName
							createExternalTableLoadData(todayTableName, collectTableBean, dataStoreConfBean,
									stageParamInfo.getTableBean(), stageParamInfo.getFileNameArr());
						} else if (IsFlag.Fou.getCode().equals(dataStoreConfBean.getIs_hadoopclient())) {
							//没有客户端，则表示为数据库类型在upload时已经装载数据了，直接跳过
							continue;
						} else {
							throw new AppSystemException("表" + collectTableBean.getHbase_name()
									+ "错误的是否标识");
						}
					} else if (Store_type.HIVE.getCode().equals(dataStoreConfBean.getStore_type())) {
						//hive库有两种情况，有客户端和没有客户端
						if (IsFlag.Shi.getCode().equals(dataStoreConfBean.getIs_hadoopclient())) {
							//有客户端
							String todayTableName = collectTableBean.getHbase_name() + "_" + 1;
							String hdfsFilePath = DFUploadStageImpl.getUploadHdfsPath(collectTableBean);
							//通过load方式加载数据到hive
							createHiveTableLoadData(todayTableName, hdfsFilePath, dataStoreConfBean,
									stageParamInfo.getTableBean());
						} else if (IsFlag.Fou.getCode().equals(dataStoreConfBean.getIs_hadoopclient())) {
							//没有客户端，则表示为数据库类型在upload时已经装载数据了，直接跳过
							continue;
						} else {
							throw new AppSystemException("表" + collectTableBean.getHbase_name()
									+ "错误的是否标识");
						}
					} else if (Store_type.HBASE.getCode().equals(dataStoreConfBean.getStore_type())) {
						LOGGER.warn("DB文件采集数据加载进HBASE没有实现");
					} else if (Store_type.SOLR.getCode().equals(dataStoreConfBean.getStore_type())) {
						LOGGER.warn("DB文件采集数据加载进SOLR没有实现");
					} else if (Store_type.ElasticSearch.getCode().equals(dataStoreConfBean.getStore_type())) {
						LOGGER.warn("DB文件采集数据加载进ElasticSearch没有实现");
					} else if (Store_type.MONGODB.getCode().equals(dataStoreConfBean.getStore_type())) {
						LOGGER.warn("DB文件采集数据加载进MONGODB没有实现");
					} else {
						//TODO 上面的待补充。
						throw new AppSystemException("表" + collectTableBean.getHbase_name()
								+ "不支持的存储类型");
					}
					LOGGER.info("数据成功进入库" + dataStoreConfBean.getDsl_name() + "下的表"
							+ collectTableBean.getHbase_name());
				}
			} else {
				throw new AppSystemException("表" + collectTableBean.getHbase_name()
						+ "DB文件采集指定的数据抽取卸数方式类型不正确");
			}
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------表" + collectTableBean.getHbase_name()
					+ "DB文件采集数据加载阶段成功------------------执行时间为："
					+ (System.currentTimeMillis() - startTime) / 1000 + "，秒");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error("表" + collectTableBean.getHbase_name()
					+ "DB文件采集数据加载阶段失败：", e);
		}
		//结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, collectTableBean
				, AgentType.DBWenJian.getCode());
		return stageParamInfo;
	}

	private void createExternalTableLoadData(String todayTableName, CollectTableBean collectTableBean
			, DataStoreConfBean dataStoreConfBean, TableBean tableBean, String[] fileNameArr) {
		Map<String, String> data_store_connect_attr = dataStoreConfBean.getData_store_connect_attr();
		String database_type = data_store_connect_attr.get(StorageTypeKey.database_type);
		List<String> sqlList = new ArrayList<>();
		Session session = null;
		DatabaseWrapper db = null;
		try {
			db = ConnectionTool.getDBWrapper(dataStoreConfBean.getData_store_connect_attr());
			//获取操作远程的对象
			session = SFTPChannel.getJSchSession(new SFTPDetails(data_store_connect_attr.get(StorageTypeKey.sftp_host),
					data_store_connect_attr.get(StorageTypeKey.sftp_user), data_store_connect_attr.
					get(StorageTypeKey.sftp_pwd), data_store_connect_attr.get(StorageTypeKey.sftp_port)), 0);
			String file_format = tableBean.getFile_format();
			//备份表上次执行进数的数据
			backupToDayTable(todayTableName, db);
			if (DatabaseType.Oracle10g.getCode().equals(database_type) ||
					DatabaseType.Oracle9i.getCode().equals(database_type)) {
				String tmpTodayTableName = todayTableName + "t";
				if (FileFormat.FeiDingChang.getCode().equals(file_format)) {
					//如果表已存在则删除
					IncreasementByMpp.dropTableIfExists(tmpTodayTableName, db, sqlList);
					//固定分隔符的文件
					//创建外部临时表，这里表名不能超过30个字符，需要
					sqlList.add(createOracleExternalTable(tmpTodayTableName, tableBean, fileNameArr,
							dataStoreConfBean.getDsl_name(), data_store_connect_attr.get(StorageTypeKey.external_directory)));
				} else {//TODO 这里判断逻辑需要增加多种文件格式支持外部表形式
					throw new AppSystemException("表" + collectTableBean.getHbase_name()
							+ "oracle数据库外部表进数目前只支持非定长文件进数");
				}
				//如果表已存在则删除
				IncreasementByMpp.dropTableIfExists(todayTableName, db, sqlList);
				sqlList.add(" CREATE TABLE " + todayTableName + " parallel (degree 4) nologging  " +
						"AS SELECT * FROM  " + tmpTodayTableName);
				//4.执行sql语句
				HSqlExecute.executeSql(sqlList, db);
				//判断是否有bad文件，有则抛异常
				String bad_files = SFTPChannel.execCommandByJSch(session,
						"ls " + data_store_connect_attr.get(StorageTypeKey.external_root_path)
								+ tmpTodayTableName.toUpperCase() + "*bad");
				if (!StringUtil.isEmpty(bad_files)) {
					throw new AppSystemException("表" + collectTableBean.getHbase_name()
							+ "你所生成的文件无法load到Oracle数据库，请查看数据库服务器下的bad文件"
							+ bad_files + "及其相关错误日志");
				} else {
					LOGGER.info("表" + collectTableBean.getHbase_name()
							+ "oracle数据库进数成功");
				}
			} else if (DatabaseType.Postgresql.getCode().equals(database_type)) {
				//数据库服务器上文件所在路径
				String uploadServerPath = DFUploadStageImpl.getUploadServerPath(collectTableBean,
						data_store_connect_attr.get(StorageTypeKey.external_root_path));
				if (FileFormat.CSV.getCode().equals(file_format)) {
					//创建外部临时表
					createPostgresqlExternalTable(todayTableName, tableBean, fileNameArr,
							dataStoreConfBean.getDsl_name(), uploadServerPath, sqlList, db);
				} else {//TODO 这里判断逻辑需要增加多种文件格式支持外部表形式
					throw new AppSystemException("表" + collectTableBean.getHbase_name()
							+ "oracle数据库外部表进数目前只支持Csv文件进数");
				}
				//4.执行sql语句
				HSqlExecute.executeSql(sqlList, db);
			} else {
				//其他支持外部表的数据库 TODO 这里的逻辑后面可能需要不断补充
				throw new AppSystemException(dataStoreConfBean.getDsl_name() + "数据库暂不支持外部表的形式入库");
			}
			//执行成功，清除上传到服务器上的文件，清除oracle外部表加载数据的日志
			//清除上传到服务器上的文件
			clearTemporaryFile(database_type, collectTableBean,
					data_store_connect_attr.get(StorageTypeKey.external_root_path), session, fileNameArr);
			//清除oracle外部表的日志
			clearTemporaryLog(database_type, todayTableName,
					data_store_connect_attr.get(StorageTypeKey.external_root_path), session);
			//根据表存储期限备份每张表存储期限内进数的数据
			backupPastTable(collectTableBean, db);
		} catch (Exception e) {
			if (db != null) {
				//执行失败，恢复上次进数的数据
				recoverBackupToDayTable(todayTableName, db);
			}
			throw new AppSystemException("表" + collectTableBean.getHbase_name()
					+ "执行数据库" + dataStoreConfBean.getDsl_name() + "外部表加载数据的sql报错", e);
		} finally {
			//清除中间表
			clearTemporaryTable(database_type, fileNameArr, todayTableName, db, dataStoreConfBean.getDsl_name());
			if (session != null)
				session.disconnect();
			if (db != null)
				db.close();
		}
	}

	private void clearTemporaryFile(String database_type, CollectTableBean collectTableBean,
	                                String external_root_path, Session session, String[] fileNameArr) throws Exception {
		//获取数据库抽取任务生成的文件名前缀
		String past_hbase_name = fileNameArr[0].split(collectTableBean.getTable_name())[0]
				+ collectTableBean.getTable_name();
		if (FileUtil.isSysDir(external_root_path)) {
			throw new AppSystemException("请不要删除系统目录下的文件" + external_root_path);
		}
		if (DatabaseType.Oracle10g.getCode().equals(database_type) ||
				DatabaseType.Oracle9i.getCode().equals(database_type)) {
			//删除大字段文件
			String lobs_file = "find " + external_root_path + " -name \"LOBs_" + past_hbase_name
					+ "_*\" | xargs rm -rf 'LOBs_" + past_hbase_name + "_*'";
			SFTPChannel.execCommandByJSch(session, lobs_file);
		} else if (DatabaseType.Postgresql.getCode().equals(database_type)) {
			//数据库服务器上文件所在路径
			external_root_path = DFUploadStageImpl.getUploadServerPath(collectTableBean,
					external_root_path);
		}
		//删除数据文件
		for (String fileName : fileNameArr) {
			SFTPChannel.execCommandByJSch(session,
					"rm -rf " + external_root_path + fileName);
		}
	}

	private void clearTemporaryLog(String database_type, String todayTableName,
	                               String external_root_path, Session session) throws Exception {
		if (FileUtil.isSysDir(external_root_path)) {
			throw new AppSystemException("请不要删除系统目录下的文件" + external_root_path);
		}
		String tmpTodayTableName = todayTableName + "t";
		if (DatabaseType.Oracle10g.getCode().equals(database_type) ||
				DatabaseType.Oracle9i.getCode().equals(database_type)) {
			//删除数据文件
			SFTPChannel.execCommandByJSch(session,
					"rm -rf " + external_root_path + tmpTodayTableName.toUpperCase() + "*log");
		}
	}

	private void clearTemporaryTable(String database_type, String[] fileNameArr,
	                                 String todayTableName, DatabaseWrapper db, String dsl_name) {
		List<String> sqlList = new ArrayList<>();
		if (DatabaseType.Oracle10g.getCode().equals(database_type) ||
				DatabaseType.Oracle9i.getCode().equals(database_type)) {
			//oracle数据库只创建了一个临时表
			//如果表已存在则删除
			IncreasementByMpp.dropTableIfExists(todayTableName + "t", db, sqlList);
		} else if (DatabaseType.Postgresql.getCode().equals(database_type)) {
			for (int i = 0; i < fileNameArr.length; i++) {
				String table_name = todayTableName + "_tmp" + i;
				IncreasementByMpp.dropTableIfExists(table_name, db, sqlList);
			}
		} else {
			//其他支持外部表的数据库 TODO 这里的逻辑后面可能需要不断补充
			throw new AppSystemException(dsl_name + "数据库暂不支持外部表的形式入库");
		}
		//4.执行sql语句
		HSqlExecute.executeSql(sqlList, db);
	}

	private void createPostgresqlExternalTable(String todayTableName, TableBean tableBean, String[] fileNameArr
			, String dsl_name, String uploadServerPath, List<String> sqlList, DatabaseWrapper db) {
		List<String> columnList = StringUtil.split(tableBean.getColumnMetaInfo(), Constant.METAINFOSPLIT);
		List<String> typeList = DataTypeTransform.tansform(StringUtil.split(
				tableBean.getColTypeMetaInfo().toUpperCase(), Constant.METAINFOSPLIT), dsl_name);
		boolean is_header = IsFlag.Shi.getCode().equalsIgnoreCase(tableBean.getIs_header());
		//拼接需要插入表的所有字段
		StringBuilder insertColumns = new StringBuilder();
		for (String col : columnList) {
			insertColumns.append(col).append(",");
		}
		insertColumns.delete(insertColumns.length() - 1, insertColumns.length());
		//创建内部表
		StringBuilder createTodayTable = new StringBuilder();
		createTodayTable.append("create table ").append(createTodayTable);
		createTodayTable.append("(");
		for (int i = 0; i < columnList.size(); i++) {
			createTodayTable.append(columnList.get(i)).append(" ").append(typeList.get(i)).append(",");
		}
		createTodayTable.deleteCharAt(createTodayTable.length() - 1); //将最后的逗号删除
		createTodayTable.append(" )");
		IncreasementByMpp.dropTableIfExists(todayTableName, db, sqlList);
		sqlList.add(createTodayTable.toString());
		for (int i = 0; i < fileNameArr.length; i++) {
			String table_name = todayTableName + "_tmp" + i;
			IncreasementByMpp.dropTableIfExists(table_name, db, sqlList);
			StringBuilder createExternalTable = new StringBuilder();
			createExternalTable.append("CREATE FOREIGN TABLE ");
			createExternalTable.append(table_name);
			createExternalTable.append("(");
			for (int j = 0; j < columnList.size(); j++) {
				createExternalTable.append(columnList.get(j)).append(" ").append(typeList.get(j)).append(",");
			}
			createExternalTable.deleteCharAt(createExternalTable.length() - 1); //将最后的逗号删除
			createExternalTable.append(") SERVER pg_file_server OPTIONS (filename '");
			createExternalTable.append(uploadServerPath).append(fileNameArr[i]);
			createExternalTable.append("', FORMAT 'csv',header '").append(is_header)
					.append("',DELIMITER '").append(tableBean.getColumn_separator()).append("' ,null '')");
			sqlList.add(createExternalTable.toString());
			sqlList.add("INSERT INTO " + todayTableName + "(" + insertColumns + ") SELECT "
					+ insertColumns + " FROM " + table_name);
		}
	}

	/*
	 *拼接创建oracle外部表的sql
	 */
	private String createOracleExternalTable(String tmpTodayTableName, TableBean tableBean,
	                                         String[] fileNameArr, String dsl_name, String external_directory) {
		List<String> columnList = StringUtil.split(tableBean.getColumnMetaInfo(), Constant.METAINFOSPLIT);
		List<String> typeList = DataTypeTransform.tansform(StringUtil.split(tableBean.getColTypeMetaInfo().toUpperCase(),
				Constant.METAINFOSPLIT), dsl_name);
		StringBuilder sql = new StringBuilder();
		sql.append("create table ").append(tmpTodayTableName);
		sql.append("(");
		for (int i = 0; i < columnList.size(); i++) {
			sql.append(columnList.get(i)).append(" ").append(typeList.get(i)).append(",");
		}
		sql.deleteCharAt(sql.length() - 1); //将最后的逗号删除
		sql.append(" )ORGANIZATION external ");
		sql.append(" ( ");
		sql.append(" type oracle_loader ");
		sql.append(" DEFAULT DIRECTORY ").append(external_directory);
		sql.append(" ACCESS PARAMETERS( ");
		sql.append(" records delimited by newline");
		sql.append(" fields terminated by '").append(tableBean.getColumn_separator()).append("' ");
//            sql.append(" optionally enclosed by '\"' ");
		sql.append(" missing field values are null  ");
		//有大字段加类型转换取大字段的值
		sql.append(getTransformsSqlForLobs(columnList, typeList, dsl_name, external_directory));
		sql.append(" ) ");
		sql.append(" location ");
		sql.append(" ( ");
		for (String fileName : fileNameArr) {
			sql.append("'").append(fileName).append("'").append(",");
		}
		sql.delete(sql.length() - 1, sql.length());
		sql.append(" )) REJECT LIMIT UNLIMITED ");
		return sql.toString();
	}

	private String getTransformsSqlForLobs(List<String> columns, List<String> types,
	                                       String dsl_name, String external_directory) {
		StringBuilder sb = new StringBuilder(1024);
		if (types.contains("BLOB") || types.contains("CLOB")) {
			sb.append("(");
			for (int i = 0; i < types.size(); i++) {
				if ("BLOB".equals(types.get(i))) {
					sb.append(columns.get(i)).append("_hylobs").append(" ").append("CHAR(100)").append(",");
				} else if ("CLOB".equals(types.get(i))) {
					sb.append(columns.get(i)).append(" ").append("CHAR(1000000)").append(",");
				} else {
					sb.append(columns.get(i)).append(" ").append("CHAR(").
							append(TypeTransLength.getLength(types.get(i), dsl_name)).append("),");
				}
			}
			sb.deleteCharAt(sb.length() - 1); //将最后的逗号删除
			sb.append(")");
			if (types.contains("BLOB")) {
				sb.append(" COLUMN TRANSFORMS ( ");
				for (int i = 0; i < types.size(); i++) {
					if ("BLOB".equals(types.get(i))) {
						sb.append(columns.get(i)).append(" from ").append(" LOBFILE (").append(columns.get(i))
								.append("_hylobs").append(")");
						sb.append(" from (").append(external_directory).append(")").append(" BLOB ").append(",");
					}
				}
				sb.deleteCharAt(sb.length() - 1); //将最后的逗号删除
				sb.append(" ) ");
			}
		}
		return sb.toString();
	}

	@Override
	public int getStageCode() {
		return StageConstant.DATALOADING.getCode();
	}

	private void createHiveTableLoadData(String todayTableName, String hdfsFilePath,
	                                     DataStoreConfBean dataStoreConfBean, TableBean tableBean) {
		try (DatabaseWrapper db = ConnectionTool.getDBWrapper(dataStoreConfBean.getData_store_connect_attr())) {
			List<String> sqlList = new ArrayList<>();
			//1.如果表已存在则删除
			sqlList.add("DROP TABLE IF EXISTS " + todayTableName);
			//2.创建表
			String file_format = tableBean.getFile_format();
			if (FileFormat.SEQUENCEFILE.getCode().equals(file_format) || FileFormat.PARQUET.getCode()
					.equals(file_format) || FileFormat.ORC.getCode().equals(file_format)) {
				sqlList.add(genHiveLoadColumnar(todayTableName, file_format,
						dataStoreConfBean.getDsl_name(), tableBean));
			} else if (FileFormat.FeiDingChang.getCode().equals(file_format)) {
				sqlList.add(genHiveLoad(todayTableName, tableBean, tableBean.getColumn_separator()));
			} else if (FileFormat.CSV.getCode().equals(file_format)) {
				sqlList.add(genHiveLoadCsv(todayTableName, tableBean));
			} else {
				throw new AppSystemException("暂不支持定长或者其他类型直接加载到hive表");
			}
			//3.加载数据
			sqlList.add("load data inpath '" + hdfsFilePath + "' into table " + todayTableName);
			//4.执行sql语句
			HSqlExecute.executeSql(sqlList, db);
		} catch (Exception e) {
			throw new AppSystemException("执行hive加载数据的sql报错", e);
		}
	}

	private String genHiveLoadCsv(String todayTableName, TableBean tableBean) {
		StringBuilder sql = new StringBuilder(120);
		List<String> columnList = StringUtil.split(tableBean.getColumnMetaInfo(), Constant.METAINFOSPLIT);
		sql.append("CREATE TABLE IF NOT EXISTS ").append(todayTableName).append(" (");
		for (String column : columnList) {
			sql.append("`").append(column).append("` ").append(" string,");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(") ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.OpenCSVSerde' stored as TEXTFILE");
		if (IsFlag.Shi.getCode().equals(tableBean.getIs_header())) {
			//包含表头，跳过第一行
			sql.append("tblproperties (\"skip.header.line.count\"=\"1\")");
		}
		return sql.toString();
	}

	private String getColumnarFileHiveStored(String fileExtension) {
		if (FileFormat.PARQUET.getCode().equals(fileExtension)) {
			return "parquet";
		} else if (FileFormat.ORC.getCode().equals(fileExtension)) {
			return "orc";
		} else if (FileFormat.SEQUENCEFILE.getCode().equals(fileExtension)) {
			return "sequencefile";
		} else {
			throw new IllegalArgumentException(fileExtension);
		}
	}

	/**
	 * 创建hive外部表加载列式存储文件
	 */
	private String genHiveLoadColumnar(String todayTableName, String file_format,
	                                   String dsl_name, TableBean tableBean) {
		String hiveStored = getColumnarFileHiveStored(file_format);
		String type;
		StringBuilder sql = new StringBuilder(120);
		sql.append("CREATE TABLE IF NOT EXISTS ").append(todayTableName).append(" (");
		List<String> columnList = StringUtil.split(tableBean.getColumnMetaInfo(), Constant.METAINFOSPLIT);
		List<String> typeList = DataTypeTransform.tansform(StringUtil.split(tableBean.getColTypeMetaInfo(),
				Constant.METAINFOSPLIT), dsl_name);
		for (int i = 0; i < columnList.size(); i++) {
			//Parquet  不支持decimal 类型
			if (FileFormat.PARQUET.getCode().equals(file_format)) {
				String typeLower = typeList.get(i).toLowerCase();
				if (typeLower.contains(DataTypeConstant.DECIMAL.getMessage())
						|| typeLower.contains(DataTypeConstant.NUMERIC.getMessage())
						|| typeLower.contains(DataTypeConstant.DOUBLE.getMessage())) {
					type = DataTypeConstant.DOUBLE.getMessage().toUpperCase();
				} else {
					type = typeList.get(i);
				}
			} else {
				type = typeList.get(i);
			}
			sql.append("`").append(columnList.get(i)).append("` ").append(type).append(",");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(") stored as ").append(hiveStored);
		return sql.toString();
	}


	/**
	 * 创建hive外部表加载行式存储文件
	 */
	private String genHiveLoad(String todayTableName, TableBean tableBean, String database_separatorr) {
		StringBuilder sql = new StringBuilder(120);
		List<String> columnList = StringUtil.split(tableBean.getColumnMetaInfo(), Constant.METAINFOSPLIT);
		sql.append("CREATE TABLE IF NOT EXISTS ").append(todayTableName).append(" (");
		for (String column : columnList) {
			sql.append("`").append(column).append("` ").append(" string,");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(") ROW FORMAT DELIMITED FIELDS TERMINATED BY  '").append(database_separatorr)
				.append("' stored as textfile ");
		return sql.toString();
	}
}
