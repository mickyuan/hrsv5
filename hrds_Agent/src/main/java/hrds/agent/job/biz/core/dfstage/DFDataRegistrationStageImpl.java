package hrds.agent.job.biz.core.dfstage;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.StageParamInfo;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.utils.CommunicationUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.CollectType;
import hrds.commons.entity.Source_file_attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DocClass(desc = "数据文件采集，数据登记阶段实现", author = "WangZhengcheng")
public class DFDataRegistrationStageImpl extends AbstractJobStage {
	private final static Logger LOGGER = LoggerFactory.getLogger(DFDataRegistrationStageImpl.class);
	private CollectTableBean collectTableBean;

	public DFDataRegistrationStageImpl(CollectTableBean collectTableBean) {
		this.collectTableBean = collectTableBean;
	}

	@Method(desc = "数据文件采集，数据登记阶段实现，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		LOGGER.info("------------------DB文件采集数据登记阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, collectTableBean.getTable_id(),
				StageConstant.DATAREGISTRATION.getCode());
		try {
			Source_file_attribute file_attribute = new Source_file_attribute();
			file_attribute.setAgent_id(collectTableBean.getAgent_id());
			file_attribute.setCollect_set_id(collectTableBean.getDatabase_id());
			file_attribute.setFile_id(collectTableBean.getTable_id());
			file_attribute.setSource_id(collectTableBean.getSource_id());
			file_attribute.setCollect_type(CollectType.ShuJuKuCaiJi.getCode());
			file_attribute.setFile_size(stageParamInfo.getFileSize());
			//TODO 下面这个可为空吧
			file_attribute.setFile_suffix("");
			file_attribute.setHbase_name(collectTableBean.getHbase_name());
			file_attribute.setTable_name(collectTableBean.getTable_name());
			file_attribute.setOriginal_name(collectTableBean.getTable_ch_name());
			file_attribute.setOriginal_update_date(DateUtil.getSysDate());
			file_attribute.setOriginal_update_time(DateUtil.getSysTime());
			file_attribute.setSource_path("");
			file_attribute.setIs_in_hbase("");
			file_attribute.setFile_md5("");
			file_attribute.setFile_avro_path("");
			file_attribute.setFile_avro_block("");
			file_attribute.setIs_big_file("");
			file_attribute.setFile_type("");
			file_attribute.setStorage_date(DateUtil.getSysDate());
			file_attribute.setStorage_time(DateUtil.getSysTime());
			file_attribute.setFolder_id("");
			JSONObject metaInfoObj = new JSONObject();
			metaInfoObj.put("records", stageParamInfo.getRowCount());
			metaInfoObj.put("mr", "n");
			TableBean tableBean = stageParamInfo.getTableBean();
			metaInfoObj.put("column", tableBean.getColumnMetaInfo());
			metaInfoObj.put("length", tableBean.getColLengthInfo());
			metaInfoObj.put("fileSize", stageParamInfo.getFileSize());
			metaInfoObj.put("tableName", collectTableBean.getTable_name());
			metaInfoObj.put("type", tableBean.getColTypeMetaInfo());
			file_attribute.setMeta_info(metaInfoObj.toJSONString());
			CommunicationUtil.addSourceFileAttribute(file_attribute, collectTableBean.getDatabase_id());
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------DB文件采集数据登记阶段成功------------------");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error("DB文件采集数据登记阶段失败：", e);
		}
		//结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, collectTableBean
				, CollectType.DBWenJianCaiJi.getCode());
		return stageParamInfo;
	}

	@Override
	public int getStageCode() {
		return StageConstant.DATAREGISTRATION.getCode();
	}
}
