package hrds.agent.job.biz.core.dbstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.db.conf.Dbtype;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.DataStoreConfBean;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.dbstage.increasement.IncreasementByMpp;
import hrds.agent.job.biz.core.dbstage.increasement.IncreasementBySpark;
import hrds.agent.job.biz.core.dbstage.increasement.JDBCIncreasement;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.agent.trans.biz.ConnectionTool;
import hrds.commons.codes.StorageType;
import hrds.commons.codes.Store_type;
import hrds.commons.exception.AppSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@DocClass(desc = "数据库直连采集计算增量阶段", author = "WangZhengcheng")
public class DBCalIncrementStageImpl extends AbstractJobStage {
	private final static Logger LOGGER = LoggerFactory.getLogger(DBUploadStageImpl.class);
	//数据采集表对应的存储的所有信息
	private CollectTableBean collectTableBean;
	//数据库采集表对应的meta信息
	private TableBean tableBean;

	public DBCalIncrementStageImpl(TableBean tableBean, CollectTableBean collectTableBean) {
		this.collectTableBean = collectTableBean;
		this.tableBean = tableBean;
	}

	@Method(desc = "数据库直连采集计算增量阶段处理逻辑，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageStatusInfo handleStage() {
		LOGGER.info("------------------数据库直连采集增量计算阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, collectTableBean.getTable_id(),
				StageConstant.DATALOADING.getCode());
		try {
			List<DataStoreConfBean> dataStoreConfBeanList = collectTableBean.getDataStoreConfBean();
			for (DataStoreConfBean dataStoreConfBean : dataStoreConfBeanList) {
				//根据存储类型上传到目的地
				if (Store_type.DATABASE.getCode().equals(dataStoreConfBean.getStore_type())) {
					JDBCIncreasement increasement;
					try (DatabaseWrapper db = ConnectionTool.getDBWrapper(
							dataStoreConfBean.getData_store_connect_attr())) {
						if (StorageType.ZengLiang.getCode().equals(collectTableBean.getStorage_type())) {
							//数据库类型的做增量目前分为两种，一种是传统数据库，另一种是hive库（hive库不支持update）
							if (Dbtype.HIVE.equals(db.getDbtype())) {
								increasement = new IncreasementBySpark(tableBean, collectTableBean.getHbase_name(),
										collectTableBean.getEtlDate(), db, dataStoreConfBean.getDtcs_name());
							} else {
								increasement = new IncreasementByMpp(tableBean, collectTableBean.getHbase_name(),
										collectTableBean.getEtlDate(), db, dataStoreConfBean.getDtcs_name());
							}
							//计算增量
							increasement.calculateIncrement();
							//合并增量表
							increasement.mergeIncrement();
						} else if (StorageType.ZhuiJia.getCode().equals(collectTableBean.getStorage_type())) {
							increasement = new IncreasementByMpp(tableBean, collectTableBean.getHbase_name(),
									collectTableBean.getEtlDate(), db, dataStoreConfBean.getDtcs_name());
							//追加
							increasement.append();
						} else if (StorageType.TiHuan.getCode().equals(collectTableBean.getStorage_type())) {
							increasement = new IncreasementByMpp(tableBean, collectTableBean.getHbase_name(),
									collectTableBean.getEtlDate(), db, dataStoreConfBean.getDtcs_name());
							//替换
							increasement.replace();
						} else {
							throw new AppSystemException("请选择正确的存储方式！");
						}
					}
				} else if (Store_type.HBASE.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else if (Store_type.SOLR.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else if (Store_type.ElasticSearch.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else if (Store_type.MONGODB.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else {
					//TODO 上面的待补充。
					throw new AppSystemException("不支持的存储类型");
				}
			}
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------数据库直连采集增量阶段成功------------------");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error("数据库直连采集增量阶段失败：", e.getMessage());
		}
		return statusInfo;
	}
}
