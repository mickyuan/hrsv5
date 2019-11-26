package hrds.agent.trans.biz.unstructuredFileCollect;

import com.alibaba.fastjson.JSONArray;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import hrds.agent.job.biz.bean.FileCollectParamBean;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.core.FileCollectJobImpl;
import hrds.agent.job.biz.core.FtpCollectJobImpl;
import hrds.commons.base.AgentBaseAction;
import hrds.commons.entity.File_source;
import hrds.commons.exception.AppSystemException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@DocClass(desc = "接收页面参数，执行非结构化文件采集作业", author = "zxz", createdate = "2019/10/28 14:26")
public class FileCollectJob extends AgentBaseAction {
	//打印日志
	private static final Log log = LogFactory.getLog(FtpCollectJobImpl.class);
	//每个文件采集，存放队列的集合
	public static final ConcurrentMap<String, ArrayBlockingQueue<String>> mapQueue = new ConcurrentHashMap<>();

	@Method(desc = "文件采集和前端交互的接口",
			logicStep = "1.获取json数组转成File_source的集合" +
					"2.校验对象的值是否正确" +
					"3.使用JobFactory工厂类调用后台方法")
	@Param(name = "fileCollectParamBean", desc = "文件采集需要的参数实体bean",
			isBean = true, range = "所有这张表不能为空的字段的值必须有，为空则会抛异常，" +
			"file_source_array对应的表File_source这个实体不能为空的字段的值必须有，为空则会抛异常")
	public void execute(FileCollectParamBean fileCollectParamBean) {
		ThreadPoolExecutor executor = null;
		try {
			//1.获取json数组转成File_source的集合
			List<File_source> fileSourceList = JSONArray.parseArray(fileCollectParamBean.getFile_source_array(),
					File_source.class);
			//使用多线程按照文件夹采集，核心线程5个，最大线程10个，队列里面50个，超出会报错
			executor = new ThreadPoolExecutor(5, 10,
					5L, TimeUnit.MINUTES, new LinkedBlockingQueue<>(50));
			List<Future<JobStatusInfo>> list = new ArrayList<>();
			//2.校验对象的值是否正确
			for (File_source file_source : fileSourceList) {
				//XXX 多线程执行
				//TODO 使用公共方法校验所有传入参数的对象的值的合法性
				//TODO Agent这个参数该怎么接，是统一封装成工厂需要的参数吗？
				//XXX 程序运行存储信息。
				FileCollectJobImpl fileCollectJob = new FileCollectJobImpl(fileCollectParamBean, file_source);
				//TODO 这个状态是不是可以在这里
				Future<JobStatusInfo> submit = executor.submit(fileCollectJob);
				list.add(submit);
			}
			//3.打印每个线程执行情况
			for (Future<JobStatusInfo> statusInfoFuture : list) {
				try {
					JobStatusInfo jobStatusInfo = statusInfoFuture.get();
					log.info("作业执行情况：" + jobStatusInfo.toString());
				} catch (Exception e) {
					log.error(e);
				}
			}
		} catch (RejectedExecutionException e) {
			throw new AppSystemException("采集选择文件夹个数大于最大线程个数和队列个数的和!");
		} finally {
			if (executor != null)
				executor.shutdown();
		}
	}

}
