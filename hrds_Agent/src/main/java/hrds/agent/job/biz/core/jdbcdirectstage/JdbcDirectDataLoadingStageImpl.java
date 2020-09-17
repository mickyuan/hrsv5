package hrds.agent.job.biz.core.jdbcdirectstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.DataStoreConfBean;
import hrds.agent.job.biz.bean.StageParamInfo;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.dfstage.DFDataLoadingStageImpl;
import hrds.agent.job.biz.core.dfstage.DFUploadStageImpl;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.Store_type;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.StorageTypeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@DocClass(desc = "数据库直连采集加载阶段", author = "zxz")
public class JdbcDirectDataLoadingStageImpl extends AbstractJobStage {
	private final static Logger LOGGER = LoggerFactory.getLogger(JdbcDirectUploadStageImpl.class);
	//数据采集表对应的存储的所有信息
	private final CollectTableBean collectTableBean;

	public JdbcDirectDataLoadingStageImpl(CollectTableBean collectTableBean) {
		this.collectTableBean = collectTableBean;
	}

	@Method(desc = "数据库直连采集加载阶段处理逻辑，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null，StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		long startTime = System.currentTimeMillis();
		LOGGER.info("------------------表" + collectTableBean.getTable_name()
				+ "数据库直连采集加载阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, collectTableBean.getTable_id(),
				StageConstant.DATALOADING.getCode());
		try {
			if (!JdbcDirectUnloadDataStageImpl.doAllSupportExternal(collectTableBean.getDataStoreConfBean())) {
				LOGGER.info("表" + collectTableBean.getHbase_name()
						+ "存储层不支持外部表，数据加载阶段不用做任何操作");
			} else {
				List<DataStoreConfBean> dataStoreConfBeanList = collectTableBean.getDataStoreConfBean();
				for (DataStoreConfBean dataStoreConfBean : dataStoreConfBeanList) {
					String todayTableName = collectTableBean.getHbase_name() + "_" + 1;
					String hdfsFilePath = DFUploadStageImpl.getUploadHdfsPath(collectTableBean);
					//根据存储类型上传到目的地
					if (Store_type.DATABASE.getCode().equals(dataStoreConfBean.getStore_type())) {
						//通过外部表加载数据到todayTableName
						DFDataLoadingStageImpl.createExternalTableLoadData(todayTableName, collectTableBean, dataStoreConfBean,
								stageParamInfo.getTableBean(), stageParamInfo.getFileNameArr());
					} else if (Store_type.HIVE.getCode().equals(dataStoreConfBean.getStore_type())) {
						//设置hive的默认类型
						dataStoreConfBean.getData_store_connect_attr().put(StorageTypeKey.database_type,
								DatabaseType.Hive.getCode());
						//通过load方式加载数据到hive
						DFDataLoadingStageImpl.createHiveTableLoadData(todayTableName, hdfsFilePath, dataStoreConfBean,
								stageParamInfo.getTableBean(), collectTableBean);
					} else if (Store_type.HBASE.getCode().equals(dataStoreConfBean.getStore_type())) {
						//根据文件类型bulkload加载数据进hbase
						DFDataLoadingStageImpl.bulkloadLoadDataToHbase(todayTableName, hdfsFilePath, collectTableBean.getEtlDate(),
								dataStoreConfBean, stageParamInfo.getTableBean(), collectTableBean);
					} else if (Store_type.SOLR.getCode().equals(dataStoreConfBean.getStore_type())) {
						LOGGER.warn("数据库直连采集数据加载进Solr没有实现");
					} else if (Store_type.ElasticSearch.getCode().equals(dataStoreConfBean.getStore_type())) {
						LOGGER.warn("数据库直连采集数据加载进ElasticSearch没有实现");
					} else if (Store_type.MONGODB.getCode().equals(dataStoreConfBean.getStore_type())) {
						LOGGER.warn("数据库直连采集数据加载进MONGODB没有实现");
					} else {
						//TODO 上面的待补充。
						throw new AppSystemException("表" + collectTableBean.getHbase_name()
								+ "不支持的存储类型");
					}
					LOGGER.info("数据成功进入库" + dataStoreConfBean.getDsl_name() + "下的表"
							+ collectTableBean.getHbase_name());
				}
			}
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------表" + collectTableBean.getHbase_name()
					+ "数据库直连采集数据加载阶段成功------------------执行时间为："
					+ (System.currentTimeMillis() - startTime) / 1000 + "，秒");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error("表" + collectTableBean.getHbase_name()
					+ "数据库直连采集数据加载阶段失败：", e);
		}
		//结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, collectTableBean
				, AgentType.ShuJuKu.getCode());
		return stageParamInfo;
	}

	@Override
	public int getStageCode() {
		return StageConstant.DATALOADING.getCode();
	}
}
