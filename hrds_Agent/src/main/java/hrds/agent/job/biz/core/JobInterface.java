package hrds.agent.job.biz.core;


import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.JobStatusInfo;

@DocClass(desc = "采集作业顶层接口，数据库直连,DB文件,半结构化文件", author = "WangZhengcheng")
public interface JobInterface extends MetaInfoInterface {
	@Method(desc = "作业阶段构造，执行作业方法，实现类覆盖该方法，在该方法中构造作业的各个阶段，并执行作业", logicStep = "")
	@Return(desc = "封装有作业状态信息的实体类对象", range = "JobStatusInfo类对象，不会为null")
	JobStatusInfo runJob();
}

