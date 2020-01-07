package hrds.agent.job.biz.core;


import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.StageParamInfo;

@DocClass(desc = "作业阶段接口，作业中每个阶段处理使用责任链模式", author = "WangZhengcheng")
public interface JobStageInterface {

	@Method(desc = "实现具体阶段处理逻辑，处理完成后，无论成功还是失败，将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null")
	//TODO:待每个阶段都实现之后，接口抛的异常要重新定义，不能直接抛顶层异常，修改此处，会影响到JobStageController.handleStageByOrder()和DataBaseJobImpl.runJob()
	StageParamInfo handleStage(StageParamInfo stageParamInfo) throws Exception;

	@Method(desc = "设置当前阶段的下一处理阶段，该方法在AbstractJobStage抽象类中做了默认实现", logicStep = "")
	@Param(name = "stage", desc = "stage代表下一阶段", range = "JobStageInterface的实例，也就是JobStageInterface的具体实现类对象")
	void setNextStage(JobStageInterface stage);

	@Method(desc = "获得当前阶段的下一处理阶段，该方法在AbstractJobStage抽象类中做了默认实现", logicStep = "")
	@Return(desc = "当前处理阶段的下一个阶段", range = "JobStageInterface的实例，也就是JobStageInterface的具体实现类对象")
	JobStageInterface getNextStage();

	@Method(desc = "获得当前阶段的阶段编号", logicStep = "")
	@Return(desc = "当前阶段的阶段编号", range = "不为空")
	int getStageCode();
}
