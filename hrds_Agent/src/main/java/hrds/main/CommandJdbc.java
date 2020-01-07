package hrds.main;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.agent.job.biz.core.DataBaseJobImpl;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

@DocClass(desc = "作业调度数据库采集程序入口", author = "zxz", createdate = "2020/1/3 10:38")
public class CommandJdbc {
	public static void main(String[] args) throws Exception {
		String database_id = args[0];
		String taskInfo = FileUtil.readFile2String(new File(Constant.MESSAGEFILE
				+ database_id));
		//对配置信息解压缩并反序列化为SourceDataConfBean对象
		SourceDataConfBean sourceDataConfBean = JSONObject.parseObject(taskInfo, SourceDataConfBean.class);
		ExecutorService executor = null;
		try {
			//初始化当前任务需要保存的文件的根目录
			String[] paths = {Constant.JOBINFOPATH, Constant.JDBCUNLOADFOLDER};
			FileUtil.initPath(sourceDataConfBean.getDatabase_id(), paths);
			//1.获取json数组转成File_source的集合
			List<CollectTableBean> collectTableBeanList = sourceDataConfBean.getCollectTableBeanArray();
			//此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
			//TODO Runtime.getRuntime().availableProcessors()此处不能用这个,因为可能同时又多个数据库采集同时进行
			executor = Executors.newFixedThreadPool(1);
			List<Future<JobStatusInfo>> list = new ArrayList<>();
			//2.校验对象的值是否正确
			for (CollectTableBean collectTableBean : collectTableBeanList) {
				//为了确保多个线程之间的值不互相干涉，复制对象的值。
				SourceDataConfBean sourceDataConfBean1 = JSONObject.parseObject(
						JSONObject.toJSONString(sourceDataConfBean), SourceDataConfBean.class);
				//XXX 多线程执行
				//TODO 使用公共方法校验所有传入参数的对象的值的合法性
				//TODO Agent这个参数该怎么接，是统一封装成工厂需要的参数吗？
				//XXX 程序运行存储信息。
				DataBaseJobImpl fileCollectJob = new DataBaseJobImpl(sourceDataConfBean1, collectTableBean);
				//TODO 这个状态是不是可以在这里
				Future<JobStatusInfo> submit = executor.submit(fileCollectJob);
				list.add(submit);
			}
			//3.打印每个线程执行情况
			JobStatusInfoUtil.printJobStatusInfo(list);
		} catch (RejectedExecutionException e) {
			throw new AppSystemException("采集选择文件夹个数大于最大线程个数和队列个数的和!");
		} finally {
			if (executor != null)
				executor.shutdown();
		}
	}
}
