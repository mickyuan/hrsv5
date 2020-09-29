package hrds.agent.job.biz.core.objectstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.dfstage.DFCalIncrementStageImpl;
import hrds.agent.job.biz.core.dfstage.DFDataLoadingStageImpl;
import hrds.agent.job.biz.core.increasement.Increasement;
import hrds.agent.job.biz.core.increasement.JDBCIncreasement;
import hrds.agent.job.biz.core.increasement.impl.HBaseIncreasementByHive;
import hrds.agent.job.biz.core.increasement.impl.IncreasementBySpark;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.StorageType;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.StorageTypeKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@DocClass(desc = "半结构化对象采集计算增量阶段", author = "zxz")
public class ObjectCalIncrementStageImpl extends AbstractJobStage {
	//打印日志
	private static final Logger LOGGER = LogManager.getLogger();
	//数据采集表对应的存储的所有信息
	private final ObjectTableBean objectTableBean;

	public ObjectCalIncrementStageImpl(ObjectTableBean objectTableBean) {
		this.objectTableBean = objectTableBean;
	}

	@Method(desc = "半结构化对象采集计算增量阶段处理逻辑，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		long startTime = System.currentTimeMillis();
		LOGGER.info("------------------表" + objectTableBean.getEn_name()
				+ "半结构化对象采集计算增量阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, objectTableBean.getOcs_id(),
				StageConstant.CALINCREMENT.getCode());
		try {
			TableBean tableBean = stageParamInfo.getTableBean();
			List<DataStoreConfBean> dataStoreConfBeanList = objectTableBean.getDataStoreConfBean();
			for (DataStoreConfBean dataStoreConfBean : dataStoreConfBeanList) {
				Increasement increase = null;
				try {
					if (Store_type.HIVE.getCode().equals(dataStoreConfBean.getStore_type())) {
						//设置hive的默认类型
						dataStoreConfBean.getData_store_connect_attr().put(StorageTypeKey.database_type,
								DatabaseType.Hive.getCode());
						DatabaseWrapper db = ConnectionTool.getDBWrapper(dataStoreConfBean.getData_store_connect_attr());
						increase = new IncreasementBySpark(tableBean, objectTableBean.getEn_name(),
								objectTableBean.getEtlDate(), db, dataStoreConfBean.getDsl_name());
						LOGGER.info("----------------------------替换--------------------------------");
						//替换
						increase.replace();
					} else if (Store_type.HBASE.getCode().equals(dataStoreConfBean.getStore_type())) {
						increase = DFCalIncrementStageImpl.getHBaseIncreasement(tableBean, objectTableBean.getEn_name()
								, objectTableBean.getEtlDate(), dataStoreConfBean);
						LOGGER.info("----------------------------替换--------------------------------");
						//替换
						increase.replace();
					}
				} catch (Exception e) {
					if (increase != null) {
						//报错删除当次跑批数据
						increase.restore(StorageType.TiHuan.getCatCode());
					}
					throw new AppSystemException("计算增量失败");
				} finally {
					if (increase != null)
						increase.close();
				}
			}
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------表" + objectTableBean.getEn_name()
					+ "半结构化对象采集计算增量阶段成功------------------执行时间为："
					+ (System.currentTimeMillis() - startTime) / 1000 + "，秒");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), "执行失败");
			LOGGER.error("表" + objectTableBean.getEn_name()
					+ "半结构化对象采集计算增量阶段失败：", e);
		}
		//结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, objectTableBean
				, AgentType.DuiXiang.getCode());
		return stageParamInfo;
	}

	@Override
	public int getStageCode() {
		return StageConstant.CALINCREMENT.getCode();
	}
}
