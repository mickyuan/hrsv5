package hrds.agent.job.biz.core.dbstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.StageParamInfo;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.AgentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DocClass(desc = "数据库抽数数据登记阶段", author = "WangZhengcheng")
public class DBDataRegistrationStageImpl extends AbstractJobStage {
	private final static Logger LOGGER = LoggerFactory.getLogger(DBUnloadDataStageImpl.class);
	private CollectTableBean collectTableBean;

	public DBDataRegistrationStageImpl(CollectTableBean collectTableBean) {
		this.collectTableBean = collectTableBean;
	}

	@Method(desc = "数据库抽数数据登记阶段处理逻辑，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		long startTime = System.currentTimeMillis();
		LOGGER.info("------------------表" + collectTableBean.getTable_name()
				+ "数据库抽数数据登记阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, collectTableBean.getTable_id(),
				StageConstant.DATAREGISTRATION.getCode());
		try {
			//数据库抽取不做登记，避免跟db文件采集冲突
//			Data_store_reg data_store_reg = new Data_store_reg();
//			data_store_reg.setAgent_id(collectTableBean.getAgent_id());
//			data_store_reg.setDatabase_id(collectTableBean.getDatabase_id());
//			data_store_reg.setTable_id(collectTableBean.getTable_id());
//			data_store_reg.setSource_id(collectTableBean.getSource_id());
//			data_store_reg.setCollect_type(AgentType.ShuJuKu.getCode());
//			data_store_reg.setFile_size(stageParamInfo.getFileSize());
//			//TODO 下面这个可为空吧
//			data_store_reg.setHyren_name(collectTableBean.getHbase_name());
//			data_store_reg.setTable_name(collectTableBean.getTable_name());
//			data_store_reg.setOriginal_name(collectTableBean.getTable_ch_name());
//			data_store_reg.setOriginal_update_date(DateUtil.getSysDate());
//			data_store_reg.setOriginal_update_time(DateUtil.getSysTime());
//			data_store_reg.setStorage_date(DateUtil.getSysDate());
//			data_store_reg.setStorage_time(DateUtil.getSysTime());
//			JSONObject metaInfoObj = new JSONObject();
//			metaInfoObj.put("records", stageParamInfo.getRowCount());
//			metaInfoObj.put("mr", "n");
//			TableBean tableBean = stageParamInfo.getTableBean();
//			metaInfoObj.put("column", tableBean.getColumnMetaInfo());
//			metaInfoObj.put("length", tableBean.getColLengthInfo());
//			metaInfoObj.put("fileSize", stageParamInfo.getFileSize());
//			metaInfoObj.put("tableName", collectTableBean.getTable_name());
//			metaInfoObj.put("type", tableBean.getColTypeMetaInfo());
//			data_store_reg.setMeta_info(metaInfoObj.toJSONString());
//			CommunicationUtil.addDataStoreReg(data_store_reg, collectTableBean.getDatabase_id());
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------表" + collectTableBean.getTable_name()
					+ "数据库抽数数据登记阶段成功------------------执行时间为："
					+ (System.currentTimeMillis() - startTime) / 1000 + "，秒");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error("表" + collectTableBean.getTable_name()
					+ "数据库抽数数据登记阶段失败：", e);
		}
		//结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, collectTableBean
				, AgentType.ShuJuKu.getCode());
		return stageParamInfo;
	}

	@Override
	public int getStageCode() {
		return StageConstant.DATAREGISTRATION.getCode();
	}
}
