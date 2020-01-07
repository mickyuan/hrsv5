package hrds.agent.job.biz.core.dfstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.StageParamInfo;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;

@DocClass(desc = "数据文件采集，计算增量阶段实现", author = "WangZhengcheng")
public class DFCalIncrementStageImpl extends AbstractJobStage {

	@Method(desc = "数据文件采集，计算增量阶段实现，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		return null;
	}

	@Override
	public int getStageCode(){
		return StageConstant.CALINCREMENT.getCode();
	}
}
