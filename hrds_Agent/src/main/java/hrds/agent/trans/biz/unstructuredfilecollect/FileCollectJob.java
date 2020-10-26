package hrds.agent.trans.biz.unstructuredfilecollect;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import hrds.agent.job.biz.bean.FileCollectParamBean;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.FileCollectJobImpl;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.commons.base.AgentBaseAction;
import hrds.commons.entity.File_source;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.PackUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@DocClass(desc = "接收页面参数，执行非结构化文件采集作业", author = "zxz", createdate = "2019/10/28 14:26")
public class FileCollectJob extends AgentBaseAction {
	//每个文件采集，存放队列的集合
	public static final ConcurrentMap<String, ArrayBlockingQueue<String>> mapQueue = new ConcurrentHashMap<>();
	//打印日志
	private static final Logger log = LogManager.getLogger();


	@Method(desc = "文件采集和前端交互生成配置文件的接口",
			logicStep = "1.将页面传递过来的压缩信息解压写文件")
	@Param(name = "fileCollectTaskInfo", desc = "文件采集需要的参数实体bean的json对象字符串",
			range = "所有这张表不能为空的字段的值必须有，为空则会抛异常，" +
					"file_sourceList对应的表File_source这个实体不能为空的字段的值必须有，为空则会抛异常")
	public void execute(String fileCollectTaskInfo) {
		FileCollectParamBean fileCollectParamBean = JSONObject.parseObject(
				PackUtil.unpackMsg(fileCollectTaskInfo).get("msg"), FileCollectParamBean.class);
		//将页面传递过来的压缩信息解压写文件
		FileUtil.createFile(JobConstant.MESSAGEFILE + fileCollectParamBean.getFcs_id(),
				PackUtil.unpackMsg(fileCollectTaskInfo).get("msg"));
	}

	@Method(desc = "文件采集和前端交互的接口",
			logicStep = "1.将页面传递过来的压缩信息解压写文件" +
					"2.初始化当前任务需要保存的文件的根目录" +
					"3.获取json数组转成File_source的集合" +
					"4.多线程执行文件采集" +
					"5.打印每个线程执行情况")
	@Param(name = "fileCollectTaskInfo", desc = "文件采集需要的参数实体bean的json对象字符串",
			range = "所有这张表不能为空的字段的值必须有，为空则会抛异常，" +
					"file_sourceList对应的表File_source这个实体不能为空的字段的值必须有，为空则会抛异常")
	public void executeImmediately(String fileCollectTaskInfo) {
		FileCollectParamBean fileCollectParamBean = JSONObject.parseObject(
				PackUtil.unpackMsg(fileCollectTaskInfo).get("msg"), FileCollectParamBean.class);
		//1.将页面传递过来的压缩信息解压写文件
		FileUtil.createFile(JobConstant.MESSAGEFILE + fileCollectParamBean.getFcs_id(),
				PackUtil.unpackMsg(fileCollectTaskInfo).get("msg"));
		ExecutorService executor = null;
		try {
			//2.初始化当前任务需要保存的文件的根目录
			String[] paths = {Constant.MAPDBPATH + File.separator + fileCollectParamBean.getFcs_id(),
					Constant.JOBINFOPATH + File.separator + fileCollectParamBean.getFcs_id()
					, Constant.FILEUNLOADFOLDER + File.separator + fileCollectParamBean.getFcs_id()};
			FileUtil.initPath(paths);
			//3.获取json数组转成File_source的集合
			List<File_source> fileSourceList = fileCollectParamBean.getFile_sourceList();
			//此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
			executor = Executors.newFixedThreadPool(JobConstant.AVAILABLEPROCESSORS);
			List<Future<JobStatusInfo>> list = new ArrayList<>();
			//4.多线程执行文件采集
			for (File_source file_source : fileSourceList) {
				//为了确保两个线程之间的值不互相干涉，复制对象的值。
				FileCollectParamBean fileCollectParamBean1 = JSONObject.parseObject(
						JSONObject.toJSONString(fileCollectParamBean), FileCollectParamBean.class);
				//多线程执行
				FileCollectJobImpl fileCollectJob = new FileCollectJobImpl(fileCollectParamBean1, file_source);
				Future<JobStatusInfo> submit = executor.submit(fileCollectJob);
				list.add(submit);
			}
			//5.打印每个线程执行情况
			log.info(list);
		} catch (Exception e) {
			throw new AppSystemException("执行文件采集失败!", e);
		} finally {
			if (executor != null)
				executor.shutdown();
		}
	}

	public static void main(String[] args) {
		String taskId = args[0];
		String taskInfo = FileUtil.readFile2String(new File(JobConstant.MESSAGEFILE
				+ taskId));
		FileCollectParamBean fileCollectParamBean = JSONObject.parseObject(
				taskInfo, FileCollectParamBean.class);
		//1.将页面传递过来的压缩信息解压写文件
		ExecutorService executor = null;
		try {
			//2.初始化当前任务需要保存的文件的根目录
			String[] paths = {Constant.MAPDBPATH + File.separator + fileCollectParamBean.getFcs_id(),
					Constant.JOBINFOPATH + File.separator + fileCollectParamBean.getFcs_id()
					, Constant.FILEUNLOADFOLDER + File.separator + fileCollectParamBean.getFcs_id()};
			FileUtil.initPath(paths);
			//3.获取json数组转成File_source的集合
			List<File_source> fileSourceList = fileCollectParamBean.getFile_sourceList();
			//此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
			executor = Executors.newFixedThreadPool(JobConstant.AVAILABLEPROCESSORS);
			List<Future<JobStatusInfo>> list = new ArrayList<>();
			//4.多线程执行文件采集
			for (File_source file_source : fileSourceList) {
				//为了确保两个线程之间的值不互相干涉，复制对象的值。
				FileCollectParamBean fileCollectParamBean1 = JSONObject.parseObject(
						JSONObject.toJSONString(fileCollectParamBean), FileCollectParamBean.class);
				//多线程执行
				FileCollectJobImpl fileCollectJob = new FileCollectJobImpl(fileCollectParamBean1, file_source);
				Future<JobStatusInfo> submit = executor.submit(fileCollectJob);
				list.add(submit);
			}
			//5.打印每个线程执行情况
			log.info(list);
		} catch (Exception e) {
			throw new AppSystemException("执行文件采集失败!", e);
		} finally {
			if (executor != null)
				executor.shutdown();
		}
	}

}
