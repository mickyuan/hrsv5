package hrds.agent.job.biz.core.dfstage;

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
import hrds.agent.job.biz.core.service.JdbcCollectTableHandleParse;
import hrds.agent.job.biz.utils.DataTypeTransform;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.*;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.utils.HSqlExecute;
import hrds.commons.utils.Constant;
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
		LOGGER.info("------------------DB文件采集数据加载阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, collectTableBean.getTable_id(),
				StageConstant.DATALOADING.getCode());
		try {
			List<DataStoreConfBean> dataStoreConfBeanList = collectTableBean.getDataStoreConfBean();
			for (DataStoreConfBean dataStoreConfBean : dataStoreConfBeanList) {
				//根据存储类型上传到目的地
				if (Store_type.DATABASE.getCode().equals(dataStoreConfBean.getStore_type())) {
					//传统数据库有两种情况，支持外部表和不支持外部表
					if (IsFlag.Shi.getCode().equals(dataStoreConfBean.getIs_hadoopclient())) {
						//支持外部表
						String todayTableName = collectTableBean.getHbase_name() + "_" + collectTableBean.getEtlDate();
						//通过外部表加载数据到todayTableName
						createExternalTableLoadData(todayTableName, collectTableBean, dataStoreConfBean,
								stageParamInfo.getTableBean(), stageParamInfo.getFileNameArr());
					} else if (IsFlag.Fou.getCode().equals(dataStoreConfBean.getIs_hadoopclient())) {
						//没有客户端，则表示为数据库类型在upload时已经装载数据了，直接跳过
						continue;
					} else {
						throw new AppSystemException("错误的是否标识");
					}
				} else if (Store_type.HIVE.getCode().equals(dataStoreConfBean.getStore_type())) {
					//hive库有两种情况，有客户端和没有客户端
					if (IsFlag.Shi.getCode().equals(dataStoreConfBean.getIs_hadoopclient())) {
						//有客户端
						String todayTableName = collectTableBean.getHbase_name() + "_" + collectTableBean.getEtlDate();
						String hdfsFilePath = DFUploadStageImpl.getUploadHdfsPath(collectTableBean);
						//通过load方式加载数据到hive
						createHiveTableLoadData(todayTableName, hdfsFilePath, dataStoreConfBean,
								stageParamInfo.getTableBean());
					} else if (IsFlag.Fou.getCode().equals(dataStoreConfBean.getIs_hadoopclient())) {
						//没有客户端，则表示为数据库类型在upload时已经装载数据了，直接跳过
						continue;
					} else {
						throw new AppSystemException("错误的是否标识");
					}
				} else if (Store_type.HBASE.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else if (Store_type.SOLR.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else if (Store_type.ElasticSearch.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else if (Store_type.MONGODB.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else {
					//TODO 上面的待补充。
					throw new AppSystemException("不支持的存储类型");
				}
				LOGGER.info("数据成功进入库" + dataStoreConfBean.getDsl_name() + "下的表"
						+ collectTableBean.getHbase_name());
			}
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------DB文件采集数据加载阶段成功------------------");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error("DB文件采集数据加载阶段失败：", e);
		}
		//结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, collectTableBean
				, CollectType.DBWenJianCaiJi.getCode());
		return stageParamInfo;
	}

	private void createExternalTableLoadData(String todayTableName, CollectTableBean collectTableBean
			, DataStoreConfBean dataStoreConfBean, TableBean tableBean, String[] fileNameArr) {
		Map<String, String> data_store_connect_attr = dataStoreConfBean.getData_store_connect_attr();
		List<String> sqlList = new ArrayList<>();
		try (DatabaseWrapper db = ConnectionTool.getDBWrapper(dataStoreConfBean.getData_store_connect_attr())) {
			String database_type = data_store_connect_attr.get(StorageTypeKey.database_type);
			if (DatabaseType.Oracle10g.getCode().equals(database_type) ||
					DatabaseType.Oracle9i.getCode().equals(database_type)) {
				//创建外部临时表
				String tmpTodayTableName = todayTableName + "_tmp";
				sqlList.add(createOracleExternalTable(tmpTodayTableName, tableBean, fileNameArr,
						dataStoreConfBean.getDsl_name()));
				sqlList.add(" CREATE TABLE " + todayTableName + " parallel (degree 4) nologging  " +
						"AS SELECT * FROM  " + tmpTodayTableName);
				//4.执行sql语句
				HSqlExecute.executeSql(sqlList, db);
			} else if (DatabaseType.Postgresql.getCode().equals(database_type)) {
				//数据库服务器上文件所在路径
				String uploadServerPath = DFUploadStageImpl.getUploadServerPath(collectTableBean,
						data_store_connect_attr.get(StorageTypeKey.external_root_path));
				//TODO
			} else {
				//其他支持外部表的数据库 TODO 这里的逻辑后面可能需要不断补充
				throw new AppSystemException(dataStoreConfBean.getDsl_name() + "数据库暂不支持外部表的形式入库");
			}
		} catch (Exception e) {
			throw new AppSystemException("执行数据库" + dataStoreConfBean.getDsl_name() + "外部表加载数据的sql报错", e);
		}
	}

	/*
	 *拼接创建oracle外部表的sql
	 */
	private String createOracleExternalTable(String tmpTodayTableName, TableBean tableBean,
	                                         String[] fileNameArr, String dsl_name) {
		List<String> columnList = StringUtil.split(tableBean.getColumnMetaInfo(), JdbcCollectTableHandleParse.STRSPLIT);
		List<String> typeList = DataTypeTransform.tansform(StringUtil.split(tableBean.getColTypeMetaInfo().toUpperCase(),
				JdbcCollectTableHandleParse.STRSPLIT), dsl_name);
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
		sql.append(" DEFAULT DIRECTORY ").append(Constant.HYSHF_DCL);
		sql.append(" ACCESS PARAMETERS( ");
		sql.append(" records delimited by newline");
		sql.append(" fields terminated by '").append(tableBean.getColumn_separator()).append("' ");
//            sql.append(" optionally enclosed by '\"' ");
		sql.append(" missing field values are null  ");
		sql.append(getTransformsSqlForLobs(columnList, typeList));//有大字段加类型转换取大字段的值
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

	private String getTransformsSqlForLobs(List<String> columns, List<String> types) {
		StringBuilder sb = new StringBuilder(1024);
		if (types.contains("BLOB") || types.contains("CLOB")) {
			sb.append("(");
			for (int i = 0; i < types.size(); i++) {
				if ("BLOB".equals(types.get(i))) {
					sb.append(columns.get(i)).append("_hylobs").append(" ").append("CHAR(100)").append(",");
				} else if ("CLOB".equals(types.get(i))) {
					sb.append(columns.get(i)).append(" ").append("CHAR(1000000)").append(",");
				} else {
					sb.append(columns.get(i)).append(" ").append(types.get(i)).append(",");
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
						sb.append(" from (").append(Constant.HYSHF_DCL).append(")").append(" BLOB ").append(",");
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
				throw new AppSystemException("暂不支持定长或者其他类型加载到hive表");
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
		List<String> columnList = StringUtil.split(tableBean.getColumnMetaInfo(), JdbcCollectTableHandleParse.STRSPLIT);
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
		List<String> columnList = StringUtil.split(tableBean.getColumnMetaInfo(), JdbcCollectTableHandleParse.STRSPLIT);
		List<String> typeList = DataTypeTransform.tansform(StringUtil.split(tableBean.getColTypeMetaInfo(),
				JdbcCollectTableHandleParse.STRSPLIT), dsl_name);
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
		List<String> columnList = StringUtil.split(tableBean.getColumnMetaInfo(), JdbcCollectTableHandleParse.STRSPLIT);
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
