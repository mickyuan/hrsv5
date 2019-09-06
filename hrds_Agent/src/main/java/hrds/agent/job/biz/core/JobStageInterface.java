package hrds.agent.job.biz.core;


import hrds.agent.job.biz.bean.StageStatusInfo;

/**
 * ClassName: JobStageInterface <br/>
 * Function: 作业阶段接口，作业中每个阶段处理使用责任链模式. <br/>
 * Reason:
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public interface JobStageInterface {

	//具体阶段处理逻辑,TODO:待每个阶段都实现之后，接口抛的异常要重新定义，不能直接抛顶层异常，修改此处，会影响到JobStageController.handleStageByOrder()和DataBaseJobImpl.runJob()
	StageStatusInfo handleStage() throws Exception;

	//设置下一处理阶段
	void setNextStage(JobStageInterface stage);

	//获得下一处理阶段
	JobStageInterface getNextStage();
}
