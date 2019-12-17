package hrds.agent.trans.biz.ftpcollect;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.core.FtpCollectJobImpl;
import hrds.agent.job.biz.core.JobInterface;
import hrds.commons.base.AgentBaseAction;
import hrds.commons.entity.Ftp_collect;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@DocClass(desc = "接收页面定义的参数执行ftp采集", author = "zxz", createdate = "2019/10/10 16:29")
public class FtpCollectJob extends AgentBaseAction {

	@Method(desc = "ftp采集和前端交互的接口",
			logicStep = "1.获取参数，校验对象的值是否正确" +
					"2.使用JobFactory工厂类调用后台方法")
	@Param(name = "ftp_collect", desc = "Ftp采集设置表对象",
			isBean = true, range = "所有这张表不能为空的字段的值必须有，为空则会抛异常")
	public JobStatusInfo execute(Ftp_collect ftp_collect) throws Exception {
		//1.获取参数，校验对象的值是否正确
		//TODO 使用公共方法校验ftp_collect对象的值得合法性
		//此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
		ExecutorService pool = Executors.newFixedThreadPool(1);
		JobInterface job = new FtpCollectJobImpl(ftp_collect);
		Future<JobStatusInfo> submit = pool.submit(job);
		return submit.get();
	}
}
