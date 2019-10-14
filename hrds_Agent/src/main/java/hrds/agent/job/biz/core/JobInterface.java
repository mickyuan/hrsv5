package hrds.agent.job.biz.core;


import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.JobStatusInfo;

/**
 * ClassName: JobInterface <br/>
 * Function: 采集作业顶层接口. <br/>
 * Reason: 数据库直连,DB文件,半结构化文件
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public interface JobInterface extends MetaInfoInterface {
	@Method(desc = "作业阶段构造，执行作业方法，实现类覆盖该方法，在该方法中构造作业的各个阶段，并执行作业", logicStep = "")
	@Return(desc = "封装有作业状态信息的实体类对象", range = "JobStatusInfo类对象，不会为null")
	JobStatusInfo runJob();
}

