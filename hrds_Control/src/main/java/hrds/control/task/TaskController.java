package hrds.control.task;

import java.io.Serializable;
import java.util.*;

import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.control.beans.*;
import hrds.control.constans.JobEffectiveFlag;
import hrds.control.core.MetaInfoInterface;
import hrds.entity.Etl_job_def;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * ClassName: TaskControl <br/>
 * Function: 任务控制类. <br/>
 * Reason: 提供给进程池执行的对象实现，用于分配任务和作业的控制类. <br/>
 * Note: 该类实现过程中，务必不要引用主进程的静态资源，否则会导致主进程意外结束后，进程池中的进程永远不会被回收<br/>
 * Date: 2019年7月26日 下午5:49:25 <br/>
 *
 * @author 13616
 * @version	1.0
 * @since JDK 1.8
 */
public class TaskController implements Runnable, Serializable {
	//TODO 存在多进程写日志的问题，静态化将无法写出日志
	private static final Logger logger = LogManager.getLogger();
	private static final long serialVersionUID = 1L;
	//任务触发毫秒数，每日触发一次
	private final static Long TRIGGER_TIME = 24 * 60 * 60 * 1000L;

	private final TaskInfo task;

	public TaskController(TaskInfo task) {
		this.task = task;
	}

	@Override
	public void run() {

		String taskId = task.getTaskId();
//		logger.info("开始执行任务，任务编号为：{}", taskId);
//		//创建任务状态文件
//		String taskStatusFile = ProductFileUtil.getTaskStatusFilePath(taskId);
//		TaskStatusInfo taskStatus = new TaskStatusInfo();
//		taskStatus.setTaskId(taskId);
//		//若任务状态为running，则该任务不再被识别为需要执行的任务
//		taskStatus.setRunStatus(RunStatusConstant.RUNNING.getCode());
//		taskStatus.setStartDate(DateUtil.getLocalDateByChar8());
//		taskStatus.setStartTime(DateUtil.getLocalTimeByChar6());
//		//TODO 如何更新作业概况还需要设计
//		if(!ProductFileUtil.createStatusFile(taskStatusFile, JSONObject.toJSONString(taskStatus))) {
//			throw new IllegalStateException("无法创建任务状态文件：" + taskId);
//		}
//		//识别jobs
//		String taskPath = ProductFileUtil.getTaskPath(taskId);
//		List<JobInfo> jobs = getReadyJob(taskPath);
//		Timer timer = new Timer();//将任务分配给timer，由timer执行作业
//		//TODO 去掉Timer
//		AgentTimerTask timerTask = new AgentTimerTask(jobs, task.getJob_param(), task.getDatabase_param(), taskStatusFile);//定义任务
//		if(String.valueOf(RunTypeConstant.RUN_ONTIME.getCode()).equals(task.getJob_param().getRun_way())) {
//			timer.schedule(timerTask, new Date(), TRIGGER_TIME);//设置任务的执行，指定时间执行，每日触发一次
//		}else {
//			timer.schedule(timerTask, 0);//立即执行的作业
//		}

		logger.info("任务执行完成，任务编号为：{}", taskId);
	}

	/**
	 * 根据系统编号获取需要立即启动的作业信息，此处会判断作业触发方式、
	 * 是否达成触发条件、资源是否足够启动作业、作业依赖等条件。
	 * @author Tiger.Wang
	 * @date 2019/8/31
	 * @param taskId 作业编号，其实是调度系统编号（etl_sys_cd）
	 * @return java.util.List<hrds.entity.Etl_job_def>
	 */
	private List<Etl_job_def> getReadyJob(String taskId) {

		List<Etl_job_def> readyJobs = new ArrayList<>();

		List<Etl_job_def> allJobs = getAllJob(taskId);
		for(Etl_job_def job : allJobs) {
			//TODO 此处的F在[ETL调度类型]代码项中不存在？
			if(job.getDisp_type().equals("F")) {
				//TODO ETL_JOB改为ETL_JOB_CUR
				//TODO 此处不甚理解，1、ETL_JOB（作业调度表）、ETL_JOB_DEF（作业定义表）有什么区别，
				//TODO 为什么getFJob方法里会去查这两张表，对于要调度的作业来说难道不应该统一在一张表中吗？
				//TODO 引出另外的一个问题，jobFrequencyMap声明为全局私有静态意义何在
//				int ii = getFJob(job.getEtl_job(), taskId, "def");
//				if( ii != 1 ) {
//					jobFrequencyMap.put(job.getEtl_job(), job);
//				}
			}
		}

		return readyJobs;
	}

	/**
	 * 根据系统编号获取作业信息，无效作业不会被查询出来
	 * @author Tiger.Wang
	 * @date 2019/8/31
	 * @return java.util.List<hrds.entity.Etl_job_def>
	 */
	private List<Etl_job_def> getAllJob(String taskId) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			return SqlOperator.queryList(db, Etl_job_def.class,
							"SELECT * FROM etl_job_def WHERE etl_sys_cd = ? AND job_eff_flag != ?",
							taskId, JobEffectiveFlag.NO.getCode());
		}
	}

	/**
	 *
	 * ClassName: AgentTimerTask <br/>
	 * Function: Timer任务实现类. <br/>
	 * Reason: 使用Timer来分配作业. <br/>
	 * Date: 2019年8月1日 下午5:49:25 <br/>
	 *
	 * @author 13616
	 * @version	1.0
	 * @since JDK 1.7
	 */
	private class AgentTimerTask extends TimerTask implements MetaInfoInterface {

		private final List<JobInfo> jobs;
		private final JobParamBean jobParam;
		private final DBConfigBean dbConfig;
		private final String taskStatusFile;
		//TODO 此处还应该构造执行时间对象，用来描述执行周期（不需要执行日期时间）
		private AgentTimerTask(List<JobInfo> jobs, JobParamBean jobParam, DBConfigBean dbConfig, String taskStatusFile) {
			this.jobs = jobs;
			this.jobParam = jobParam;
			this.dbConfig = dbConfig;
			this.taskStatusFile = taskStatusFile;
		}
		@Override
		public void run() {
			//TODO 周期性任务，代码待补充
//			Calendar calendar = Calendar.getInstance();
//			int day = calendar.get(Calendar.DAY_OF_MONTH);
//			if(day != 30) {	//每月30日执行一次
//				return;
//			}
			//TODO 这里要考虑作业的并行、串行执行，对并行任务开启线程执行，对串行任务等待前置作业
			//TODO 此处考虑线程池，可防止作业无法提交的问题，也可以用于获得所有作业执行完毕的标识
			for(JobInfo job : jobs) {
				JobThread jobThread = new JobThread(job, jobParam, dbConfig, taskStatusFile);
				jobThread.start();
			}
		}

		@Override
		public List<MetaInfoBean> getMetaInfoGroup() {
			throw new UnsupportedOperationException("还未支持的操作");
		}

		@Override
		public MetaInfoBean getMetaInfo() {
			throw new UnsupportedOperationException("还未支持的操作");
		}
	}

	/**
	 *
	 * ClassName: JobThread <br/>
	 * Function: 作业执行类. <br/>
	 * Reason: 用于作业的执行，每个作业一个线程. <br/>
	 * Date: 2019年8月1日 下午5:49:25 <br/>
	 *
	 * @author 13616
	 * @version	1.0
	 * @since JDK 1.7
	 */
	private class JobThread extends Thread {

		private final JobInfo jobInfo;
		private final JobParamBean jobParam;
		private final DBConfigBean dbConfig;
		private final String taskStatusFile;

		private JobThread(JobInfo job, JobParamBean jobParam, DBConfigBean dbConfig, String taskStatusFile) {

			this.jobInfo = job;
			this.jobParam = jobParam;
			this.dbConfig = dbConfig;
			this.taskStatusFile = taskStatusFile;
		}

		@Override
		public void run() {

			String jobId = jobInfo.getJobId();
			logger.info("开始执行作业，作业编号为：{}", jobId);
//
//			String taskId = task.getTaskId();
//			JobStatusInfo jobStatus = new JobStatusInfo();
//			jobStatus.setJobId(jobId);
//			jobStatus.setRunStatus(RunStatusConstant.RUNNING.getCode());
//			jobStatus.setStartDate(DateUtil.getLocalDateByChar8());
//			jobStatus.setStartTime(DateUtil.getLocalTimeByChar6());
//			String statusFilePath = ProductFileUtil.getJobStatusFilePath(taskId, jobId);
//			//记录作业为运行中状态
//			if(!ProductFileUtil.createStatusFile(statusFilePath, JSONObject.toJSONString(jobStatus))) {
//				throw new IllegalStateException("无法创建作业状态文件：" + taskId + "，作业编号：" + jobId);
//			}
//			JobInterface job = JobFactory.newInstance(jobInfo, dbConfig, jobParam, statusFilePath, jobStatus);
//
//			//写meta文件
//			MetaInfoBean mateInfo = job.getMetaInfo();
//			String metaFile = ProductFileUtil.getMetaFilePath(taskId, jobId);
//			ProductFileUtil.createMetaFileWithContent(metaFile, JSONObject.toJSONString(mateInfo));
//
//			jobStatus = job.runJob();
//
//			//记录作业为执行结束状态
//			jobStatus.setRunStatus(RunStatusConstant.COMPLETE.getCode());
//			jobStatus.setEndDate(DateUtil.getLocalDateByChar8());
//			jobStatus.setEndTime(DateUtil.getLocalTimeByChar6());
//			if(!ProductFileUtil.createStatusFile(statusFilePath, JSONObject.toJSONString(jobStatus))) {
//				throw new IllegalStateException("无法创建作业状态文件：" + taskId + "，作业编号：" + jobId);
//			}
			logger.info("作业执行结束，作业编号为：{}", jobId);
		}
	}
}