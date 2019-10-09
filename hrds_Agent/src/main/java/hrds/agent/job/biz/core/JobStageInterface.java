package hrds.agent.job.biz.core;


import hrds.agent.job.biz.bean.StageStatusInfo;

/**
 * ClassName: JobStageInterface <br/>
 * Function: 作业阶段接口，作业中每个阶段处理使用责任链模式 <br/>
 * Reason:
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public interface JobStageInterface {

	/**
	 * 实现具体阶段处理逻辑，处理完成后，无论成功还是失败，将相关状态信息封装到StageStatusInfo对象中返回
	 * TODO:待每个阶段都实现之后，接口抛的异常要重新定义，不能直接抛顶层异常，修改此处，会影响到JobStageController.handleStageByOrder()和DataBaseJobImpl.runJob()
	 *
	 * @Param: 无
	 *
	 * @return: StageStatusInfo
	 *          含义：StageStatusInfo是保存每个阶段状态信息的实体类
	 *          取值范围：不会为null
	 *
	 * */
	StageStatusInfo handleStage() throws Exception;

	/**
	 * 设置当前阶段的下一处理阶段，该方法在AbstractJobStage抽象类中做了默认实现
	 *
	 * @Param: stage JobStageInterface
	 *         含义：stage代表下一阶段
	 *         取值范围：JobStageInterface的实例，也就是JobStageInterface的具体实现类对象
	 *
	 * @return: 无
	 *
	 * */
	void setNextStage(JobStageInterface stage);

	/**
	 * 获得当前阶段的下一处理阶段，该方法在AbstractJobStage抽象类中做了默认实现
	 *
	 * @Param: 无
	 *
	 * @return: JobStageInterface
	 *          含义：当前处理阶段的下一个阶段
	 *          取值范围：JobStageInterface的实例，也就是JobStageInterface的具体实现类对象
	 *
	 * */
	JobStageInterface getNextStage();
}
