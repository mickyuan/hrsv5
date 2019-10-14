package hrds.agent.job.biz.core;

import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;

/**
 * ClassName: AbstractJobStage <br/>
 * Function: 作业阶段接口适配器，请每种类型任务的每个阶段继承该类 <br/>
 * Reason: 提供setNextStage()和getNextStage()的默认实现,这两个方法的作用是设置和返回责任链中当前环节的下一环节
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public abstract class AbstractJobStage implements JobStageInterface {

	protected static final String TERMINATED_MSG = "脚本执行完成";
	protected static final String FAILD_MSG = "脚本执行失败";
	protected JobStageInterface nextStage;

	@Method(desc = "设置当前阶段的下一处理阶段，该方法在AbstractJobStage抽象类中做了默认实现，请每种类型任务的每个阶段实现类不要覆盖该方法", logicStep = "")
	@Param(name = "stage", desc = "stage代表下一阶段", range = "JobStageInterface的实例，也就是JobStageInterface的具体实现类对象")
	@Override
	public void setNextStage(JobStageInterface stage) {
		this.nextStage = stage;
	}

	@Method(desc = "获得当前阶段的下一处理阶段，该方法在AbstractJobStage抽象类中做了默认实现，请每种类型任务的每个阶段实现类不要覆盖该方法", logicStep = "")
	@Return(desc = "当前处理阶段的下一个阶段", range = "JobStageInterface的实例，也就是JobStageInterface的具体实现类对象")
	@Override
	public JobStageInterface getNextStage() {
		return nextStage;
	}
}
