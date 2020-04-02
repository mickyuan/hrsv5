package hrds.agent.job.biz.utils;

import com.alibaba.fastjson.JSONObject;
import hrds.agent.job.biz.bean.JobInfo;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.TaskStatusInfo;

import java.io.File;
import java.io.IOException;

public class ProductFileUtil {
	//可从配置文件中加载，可移动至常量类
	public static final String JOB_FILE_SUFFIX = ".job";
	public static final String TASK_FILE_SUFFIX = ".task";
	public static final String SIGNAL_FILE_SUFFIX = ".flg";
	public static final String STATUS_FILE_SUFFIX = ".status";
	public static final String META_FILE_SUFFIX = ".meta";
	//以下为工作目录
	private static final String ROOT_PATH = ProductFileUtil.getWorkRootPath();
	public static final String TASK_ROOT_PATH = ROOT_PATH + File.separatorChar + "task";
	public static final String TASKCONF_ROOT_PATH = ROOT_PATH + File.separatorChar + "taskconf";

	private ProductFileUtil() {
	}

	/**
	 * 获得作业描述文件路径
	 *
	 * @param taskId 任务编号
	 * @param jobId  作业编号
	 * @return java.lang.String   作业描述文件路径
	 * @author 13616
	 * @date 2019/7/30 23:01
	 */
	public static String getJobFilePath(String taskId, String jobId) {

		return (ProductFileUtil.TASK_ROOT_PATH + File.separatorChar + taskId +
				File.separatorChar + jobId + ProductFileUtil.JOB_FILE_SUFFIX);
	}

	/**
	 * 获得作业meta文件路径
	 *
	 * @param taskId 任务编号
	 * @param jobId  作业编号
	 * @return java.lang.String   作业meta文件路径
	 * @date 2019/7/30 23:01
	 */
	public static String getMetaFilePath(String taskId, String jobId) {

		return (ProductFileUtil.TASK_ROOT_PATH + File.separatorChar +
				taskId + File.separatorChar + jobId + ProductFileUtil.META_FILE_SUFFIX);
	}

	/**
	 * 创建meta文件，并写入内容
	 *
	 * @param filePath meta文件地址
	 * @param content  文件内容
	 * @return boolean    是否创建文件成功
	 * @author 13616
	 * @date 2019/8/7 11:37
	 */
	public static boolean createMetaFileWithContent(String filePath, String content) {

		return FileUtil.createFile(filePath, content);
	}

	/**
	 * 获取任务信号文件路径
	 *
	 * @param taskId 任务编号
	 * @return java.lang.String   任务信号文件路径
	 * @author 13616
	 * @date 2019/7/30 23:04
	 */
	public static String getTaskSignalFilePath(String taskId) {

		return ProductFileUtil.TASK_ROOT_PATH + File.separatorChar + taskId +
				ProductFileUtil.SIGNAL_FILE_SUFFIX;
	}

	/**
	 * 获取作业下数据文件存放路径
	 *
	 * @param job 作业对象
	 * @return java.lang.String   对应作业下数据文件存放路径
	 * @author 13616
	 * @date 2019/7/30 23:04
	 */
	public static String getDataFilePathByJobID(JobInfo job) {

		return (ProductFileUtil.TASK_ROOT_PATH + File.separatorChar + job.getTaskId() +
				File.separatorChar + job.getJobId() + File.separatorChar + "datafile");
	}

	/**
	 * @Description: 获取作业下LOBs目录路径
	 * @Param: [job]
	 * @return: java.lang.String
	 * @Author: WangZhengcheng
	 * @Date: 2019/8/27
	 */
	public static String getLOBsPathByJobID(JobInfo job) {

		return (ProductFileUtil.getDataFilePathByJobID(job) + File.separatorChar + "LOBs");
	}

	/**
	 * 获取工作目录的路径
	 *
	 * @return java.lang.String   工作目录
	 * @author 13616
	 * @date 2019/7/30 23:08
	 */
	public static String getWorkRootPath() {
		try {
			return new File("").getCanonicalFile().getParent();
		} catch (IOException e) {
			throw new IllegalStateException("识别环境目录失败：" + e.getMessage());
		}
	}

	/**
	 * 获得项目所在目录路径，若是jar包，则为jar包所在目录
	 *
	 * @return java.lang.String   项目所在目录路径
	 * @author 13616
	 * @date 2019/7/30 23:22
	 */
	public static String getProjectPath() {
		try {
			return new File("").getCanonicalFile().getPath();
		} catch (IOException e) {
			throw new IllegalStateException("识别环境目录失败：" + e.getMessage());
		}
	}

	/**
	 * 获取任务目录所在路径
	 *
	 * @param taskId 任务编号
	 * @return java.lang.String   任务目录路径
	 * @author 13616
	 * @date 2019/7/30 23:30
	 */
	public static String getTaskPath(String taskId) {

		return (ProductFileUtil.TASK_ROOT_PATH + File.separatorChar + taskId);
	}

	/**
	 * 获取任务配置文件所在路径
	 *
	 * @param taskId 任务编号
	 * @return java.lang.String
	 * @author 13616
	 * @date 2019/7/30 23:32
	 */
	public static String getTaskConfPath(String taskId) {

		return (ProductFileUtil.TASKCONF_ROOT_PATH + File.separatorChar + taskId +
				ProductFileUtil.TASK_FILE_SUFFIX);
	}

	/**
	 * 获取任务状态文件路径
	 *
	 * @param taskId 任务编号
	 * @return java.lang.String   任务状态文件路径
	 * @author 13616
	 * @date 2019/7/30 23:33
	 */
	public static String getTaskStatusFilePath(String taskId) {

		return (ProductFileUtil.TASK_ROOT_PATH + File.separatorChar + taskId +
				ProductFileUtil.STATUS_FILE_SUFFIX);
	}

	/**
	 * 获取作业状态文件路径
	 *
	 * @param taskId 任务编号
	 * @param jobId  作业编号
	 * @return java.lang.String   作业状态文件路径
	 * @author 13616
	 * @date 2019/7/31 11:19
	 */
	public static String getJobStatusFilePath(String taskId, String jobId) {

		return (ProductFileUtil.TASK_ROOT_PATH + File.separatorChar + taskId + File.separatorChar +
				jobId + ProductFileUtil.STATUS_FILE_SUFFIX);
	}

	/**
	 * 创建任务/作业状态文件
	 *
	 * @param filePath 作业/作业状态文件路径
	 * @param context  文件内容
	 * @return boolean    是否创建成功
	 * @author 13616
	 * @date 2019/7/30 23:34
	 */
	public static boolean createStatusFile(String filePath, String context) {

		return FileUtil.createFile(filePath, context);
	}

	/**
	 * 获取任务状态信息
	 *
	 * @param taskId 任务编号
	 * @return com.beyondsoft.agent.beans.TaskStatusInfo
	 * @author 13616
	 * @date 2019/7/31 14:36
	 */
	public static TaskStatusInfo getTaskStatusInfo(String taskId) {
		String taskStatusFile = ProductFileUtil.getTaskStatusFilePath(taskId);
		return JSONObject.parseObject(FileUtil.readFile2String(new File(taskStatusFile)), TaskStatusInfo.class);
	}

	/**
	 * 获取作业状态信息
	 *
	 * @param taskId 任务编号
	 * @param jobId  作业编号
	 * @return com.beyondsoft.agent.beans.JobStatusInfo
	 * @author 13616
	 * @date 2019/7/31 14:40
	 */
	public static JobStatusInfo getJobStatusInfo(String taskId, String jobId) {
		String jobStatusFile = ProductFileUtil.getJobStatusFilePath(taskId, jobId);
		return JSONObject.parseObject(FileUtil.readFile2String(new File(jobStatusFile)), JobStatusInfo.class);
	}
}
