package hrds.agent.job.biz.core;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.MetaInfoBean;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.concurrent.Callable;

@DocClass(desc = "meta信息接口类，任务/作业提供meta信息，实现该接口", author = "WangZhengcheng")
public interface MetaInfoInterface extends Callable<JobStatusInfo> {

	@Method(desc = "获得meta信息，提供多于1个meta", logicStep = "")
	@Return(desc = "存放Meta信息的Bean对象的List集合", range = "不会为null")
	List<MetaInfoBean> getMetaInfoGroup();

	@Method(desc = "获得meta信息，提供1个meta", logicStep = "")
	@Return(desc = "存放Meta信息的Bean对象", range = "不会为null")
	MetaInfoBean getMetaInfo();
}
