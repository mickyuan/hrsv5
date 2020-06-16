package hrds.agent.trans.biz.ftpcollect;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.commons.base.AgentBaseAction;
import hrds.commons.entity.Ftp_collect;
import hrds.commons.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DocClass(desc = "接收页面定义的参数执行ftp采集", author = "zxz", createdate = "2019/10/10 16:29")
public class FtpCollectJob extends AgentBaseAction {
	private final static Logger LOGGER = LoggerFactory.getLogger(FtpCollectJob.class);

	@Method(desc = "ftp采集和前端交互的接口",
			logicStep = "1.获取参数，校验对象的值是否正确" +
					"2.使用JobFactory工厂类调用后台方法")
	@Param(name = "taskInfo", desc = "Ftp采集设置表对象的json格式的字符串",
			range = "所有这张表不能为空的字段的值必须有，为空则会抛异常")
	public void execute(String taskInfo) {
		LOGGER.info("获取到的ftp采集信息" + taskInfo);
		//对配置信息解压缩并反序列化为Ftp_collect对象
		Ftp_collect ftp_collect = JSONObject.parseObject(taskInfo, Ftp_collect.class);
		//将页面传递过来的压缩信息写文件
		FileUtil.createFile(Constant.MESSAGEFILE + ftp_collect.getFtp_id(), taskInfo);
		//1.获取参数，校验对象的值是否正确
		//TODO 使用公共方法校验ftp_collect对象的值得合法性
		//此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
//		ExecutorService pool = Executors.newFixedThreadPool(1);
//		JobInterface job = new FtpCollectJobImpl(ftp_collect);
//		Future<JobStatusInfo> statusInfoFuture = pool.submit(job);
//		JobStatusInfo jobStatusInfo = statusInfoFuture.get();
//		LOGGER.info("作业执行情况：" + jobStatusInfo.toString());
//		return jobStatusInfo;
	}
}
