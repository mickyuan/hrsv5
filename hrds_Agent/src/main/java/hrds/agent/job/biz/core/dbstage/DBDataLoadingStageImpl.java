package hrds.agent.job.biz.core.dbstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.DataStoreConfBean;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.dbstage.increasement.JDBCIncreasement;
import hrds.agent.job.biz.core.dbstage.service.CollectTableHandleParse;
import hrds.agent.job.biz.utils.DataTypeTransform;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.agent.trans.biz.ConnectionTool;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.Store_type;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@DocClass(desc = "数据库直连采集数据加载阶段", author = "WangZhengcheng")
public class DBDataLoadingStageImpl extends AbstractJobStage {
	private final static Logger LOGGER = LoggerFactory.getLogger(DBUploadStageImpl.class);
	//数据采集表对应的存储的所有信息
	private CollectTableBean collectTableBean;
	//数据库采集表对应的meta信息
	private TableBean tableBean;

	public DBDataLoadingStageImpl(TableBean tableBean, CollectTableBean collectTableBean) {
		this.collectTableBean = collectTableBean;
		this.tableBean = tableBean;
	}

	@Method(desc = "数据库直连采集数据加载阶段处理逻辑，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null，StageStatusInfo实体类对象")
	@Override
	public StageStatusInfo handleStage() {
		LOGGER.info("------------------数据库直连采集数据加载阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, collectTableBean.getTable_id(),
				StageConstant.CALINCREMENT.getCode());
		try {
			List<DataStoreConfBean> dataStoreConfBeanList = collectTableBean.getDataStoreConfBean();
			for (DataStoreConfBean dataStoreConfBean : dataStoreConfBeanList) {
				//根据存储类型上传到目的地
				if (Store_type.DATABASE.getCode().equals(dataStoreConfBean.getStore_type())) {
					//数据库类型在upload时已经装载数据了，直接跳过
					continue;
				} else if (Store_type.HIVE.getCode().equals(dataStoreConfBean.getStore_type())) {
					//hive库有两种情况，有客户端和没有客户端
					if (IsFlag.Shi.getCode().equals(dataStoreConfBean.getIs_hadoopclient())) {
						//有客户端
						String todayTableName = collectTableBean.getHbase_name() + "_" + collectTableBean.getEtlDate();
						String hdfsFilePath = DBUploadStageImpl.getUploadHdfsPath(collectTableBean);
						//通过load方式加载数据到hive
						createTableLoadData(todayTableName, hdfsFilePath, dataStoreConfBean);
					} else if (IsFlag.Fou.getCode().equals(dataStoreConfBean.getIs_hadoopclient())) {
						//没有客户端，在upload时已经装载数据了，直接跳过
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
			LOGGER.info("------------------数据库直连采集数据加载阶段成功------------------");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error("数据库直连采集数据加载阶段失败：", e.getMessage());
		}
		return statusInfo;
	}

	private void createTableLoadData(String todayTableName, String hdfsFilePath, DataStoreConfBean dataStoreConfBean) {
		try (DatabaseWrapper db = ConnectionTool.getDBWrapper(dataStoreConfBean.getData_store_connect_attr())) {
			List<String> sqlList = new ArrayList<>();
			//1.创建表
			String file_format = collectTableBean.getDbfile_format();
			if (FileFormat.SEQUENCEFILE.getCode().equals(file_format) || FileFormat.PARQUET.getCode()
					.equals(file_format) || FileFormat.ORC.getCode().equals(file_format)) {
				sqlList.add(genHiveLoadColumnar(todayTableName, file_format, dataStoreConfBean.getDtcs_name()));
			} else if (FileFormat.FeiDingChang.getCode().equals(file_format) || FileFormat.CSV.getCode()
					.equals(file_format)) {
				sqlList.add(genHiveLoad(todayTableName));
			} else {
				throw new AppSystemException("暂不支持定长或者其他类型加载到hive表");
			}
			//2.加载数据
			sqlList.add("load data inpath '" + hdfsFilePath + "' into table " + todayTableName);
			//3.执行sql语句
			JDBCIncreasement.executeSql(sqlList, db);
		}
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
	private String genHiveLoadColumnar(String todayTableName, String file_format, String dtcs_name) {
		String hiveStored = getColumnarFileHiveStored(file_format);
		String type;
		StringBuilder sql = new StringBuilder(120);
		sql.append("CREATE TABLE IF NOT EXISTS ").append(todayTableName).append(" (");
		List<String> columnList = StringUtil.split(tableBean.getColumnMetaInfo(), CollectTableHandleParse.STRSPLIT);
		List<String> typeList = DataTypeTransform.tansform(StringUtil.split(tableBean.getColTypeMetaInfo(),
				CollectTableHandleParse.STRSPLIT), dtcs_name);
		for (int i = 0; i < columnList.size(); i++) {
			//Parquet  不支持decimal 类型
			if (FileFormat.PARQUET.toString().equals(file_format)) {
				type = (typeList.get(i).contains("DECIMAL")) ? "DOUBLE" : typeList.get(i);
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
	private String genHiveLoad(String todayTableName) {
		StringBuilder sql = new StringBuilder(120);
		List<String> columnList = StringUtil.split(tableBean.getColumnMetaInfo(), CollectTableHandleParse.STRSPLIT);
		sql.append("CREATE TABLE IF NOT EXISTS ").append(todayTableName).append(" (");
		for (String column : columnList) {
			sql.append("`").append(column).append("` ").append(" string,");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(") ROW FORMAT DELIMITED FIELDS TERMINATED BY  '" + Constant.DATADELIMITER + "' stored as textfile ");
		return sql.toString();
	}
}
