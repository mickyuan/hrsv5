package hrds.agent.job.biz.core;

/**
 * ClassName: AbstractJobStage <br/>
 * Function: 作业阶段接口适配器. <br/>
 * Reason: 提供setNextStage()和getNextStage()的默认实现,这两个方法的作用是设置和返回责任链中当前环节的下一环节
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public abstract class AbstractJobStage implements JobStageInterface{

    protected static final String TERMINATED_MSG = "脚本执行完成";
    protected static final String FAILD_MSG = "脚本执行失败";
    protected JobStageInterface nextStage;

    @Override
    public void setNextStage(JobStageInterface stage) {
        this.nextStage = stage;
    }

    @Override
    public JobStageInterface getNextStage() {
        return nextStage;
    }
}
