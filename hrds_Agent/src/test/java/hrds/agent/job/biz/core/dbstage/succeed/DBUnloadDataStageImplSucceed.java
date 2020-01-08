package hrds.agent.job.biz.core.dbstage.succeed;

import hrds.agent.job.biz.bean.StageParamInfo;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;

public class DBUnloadDataStageImplSucceed extends AbstractJobStage{

	private static final String JOB_ID = "100101";

	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		//阶段开始，设置开始内容
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, JOB_ID, StageConstant.UNLOADDATA.getCode());

		//模拟执行过程
		System.out.println("-------------------执行卸数阶段----------------------------");

		//阶段结束，设置结束内容，执行成功
		JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "卸数执行成功");
		stageParamInfo.setStatusInfo(statusInfo);
		return stageParamInfo;
	}

	@Override
	public int getStageCode(){
		return StageConstant.UNLOADDATA.getCode();
	}
}
