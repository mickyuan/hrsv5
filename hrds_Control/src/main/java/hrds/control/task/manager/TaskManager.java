package hrds.control.task.manager;

import hrds.control.task.TaskController;
import net.viktorc.pp4j.api.JavaProcessExecutorService;
import net.viktorc.pp4j.api.JavaProcessOptions.*;
import net.viktorc.pp4j.impl.JavaProcessPoolExecutor;
import net.viktorc.pp4j.impl.SimpleJavaProcessOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * ClassName: TaskManager <br/>
 * Function: 用于管理任务/作业，对任务/作业进行初始化、发布任务的管理类 <br/>
 * Date: 2019/7/30 16:58 <br/>
 *
 * Author Tiger.Wang
 * Version 1.0
 * Since JDK 1.8
 **/
public class TaskManager {

	private static final Logger logger = LogManager.getLogger();
	//日期时间格式，用于对任务的执行日期时间进行解析
	private final static String DATE_TIME_FORMAT = "yyyyMMdd hhmmss";
	private final JavaProcessExecutorService jvmPool;	//进程池

	private final LocalDate bathDate;
	private final String strSystemCode;
	private final boolean isResumeRun;
	private final boolean isAutoShift;

	/**
	 * 静态工厂，用于构造TaskManager实例
	 * @author Tiger.Wang
	 * @date 2019/8/30
	 * @param bathDate	跑批批次日期
	 * @param strSystemCode	调度系统代码
	 * @param isResumeRun	是否续跑
	 * @param isAutoShift	是否自动日切
	 * @return hrds.agent.control.task.manager.TaskManager
	 */
	public static TaskManager newInstance(LocalDate bathDate, String strSystemCode, boolean isResumeRun, boolean isAutoShift) {

		return new TaskManager(bathDate, strSystemCode, isResumeRun, isAutoShift);
	}

	/**
	 * TaskManager类构造器，构造器私有化，使用newInstance方法获得静态实例，不允许外部构造。该构造器会初始化进程池。
	 * @author Tiger.Wang
	 * @date 2019/8/30
	 * @param bathDate	跑批批次日期
	 * @param strSystemCode	调度系统代码
	 * @param isResumeRun	是否续跑
	 * @param isAutoShift	是否自动日切
	 */
	private TaskManager(LocalDate bathDate, String strSystemCode, boolean isResumeRun, boolean isAutoShift) {

//		initWorkDir();
		this.bathDate = bathDate;
		this.strSystemCode = strSystemCode;
		this.isResumeRun = isResumeRun;
		this.isAutoShift = isAutoShift;

		try {
			this.jvmPool = new JavaProcessPoolExecutor(new SimpleJavaProcessOptions(JVMArch.BIT_64, JVMType.CLIENT,
					40, 256, 256, 3000),
							2, 20, 2, null, false);
		}
		catch(InterruptedException e) {
			throw new IllegalStateException("初始化进程池失败：" + e.getMessage());
		}
	}

	private void initWorkDir() {
		//检查自己在工作目录下是否有可读、可写、可执行权限
//		if( !FileUtil.checkDirWithAllAuth(ProductFileUtil.getWorkRootPath()) ) {
//			throw new IllegalStateException("环境初始化失败：" + ProductFileUtil.getWorkRootPath());
//		}
//		//接来下创建工作目录
//		FileUtil.createDir(ProductFileUtil.TASK_ROOT_PATH);
//		FileUtil.createDir(ProductFileUtil.TASKCONF_ROOT_PATH);
	}

	/**
	 * 获取已经准备好执行的任务列表
	 * @author   13616
	 * @date     2019/7/30 17:53
	 * @return   java.util.List<com.beyondsoft.agent.beans.TaskInfo>
	 */
//	public List<TaskInfo> getReadyTask() {
		//TODO 如果要实现任务下线程的方式启动，则需要在jar（job）中实现一套接口，用于调度系统线程方式调起任务
		//第一步，读取每个任务信息
//		List<File> files = FileUtil.getAllFilesByFileSuffix(ProductFileUtil.TASKCONF_ROOT_PATH, ProductFileUtil.TASK_FILE_SUFFIX);
//		List<TaskInfo> tasks = new ArrayList<>();
//		try {
//			for(File file : files) {
//				String taskStr = FileUtil.readFile2String(file);
//				TaskInfo task = JSONObject.parseObject(taskStr, TaskInfo.class);
//				//第二步，判断每个作业触发方式，以及执行时间
//				//第三步，如果是按时触发，则需要判断执行时间是否已到；如果是信号文件触发，则需要判断信号文件是否存在；如果是立即执行，则无条件
//				if( checkReadyTask(task) ) {
//					tasks.add(task);
//				}
//			}
//		}
//		catch(IllegalArgumentException | IOException | ParseException e) {
//			LOGGER.warn("在识别任务时出现异常：" + e.getMessage());
//		}

//		return tasks;
//	}

	/**
	 * 对任务对象进行识别，判别是否为需要执行的任务
	 * @author   13616
	 * @date     2019/7/30 17:54
	 * @param task TaskInfo对象
	 * @return   boolean，是否为需要执行的任务
	 */
//	private boolean checkReadyTask(TaskInfo task) throws ParseException {

//		if( null == task ) {
//			return false;
//		}
//		if( StringUtils.isEmpty(task.getTaskId()) ) {
//			return false;
//		}
//		TaskStatusInfo taskStatusInfo = ProductFileUtil.getTaskStatusInfo(task.getTaskId());
//		if( RunStatusConstant.WAITING.getCode() != taskStatusInfo.getRunStatus() ) {
//			return false;
//		}
//		String runType = task.getJob_param().getRun_way();
//		//若作业类型为按时启动
//		if( String.valueOf(RunTypeConstant.RUN_NOW.getCode()).equals(runType) ) {
//			return true;
//		}
//		else if( String.valueOf(RunTypeConstant.RUN_ONTIME.getCode()).equals(runType) ) {
////			return true;
//			String dateTime = task.getJobstartdate() + " " + task.getJobstarttime();
//			//作业日期时间
//			Date jobDate = DateUtils.parseDate(dateTime, DATE_TIME_FORMAT);
//			//系统日期时间
//			Date currentDate = new Date();
//			//作业日期小于当前系统日期
//			return jobDate.compareTo(currentDate) <= 0;
//		}
//		else if( String.valueOf(RunTypeConstant.RUN_FILE_SIGNAL.getCode()).equals(runType) ) {
////			String signalFile = ProductFileUtil.getTaskSignalFilePath(task.getTaskId());
////			return new File(signalFile).exists();
//		}
//		else {
//			throw new IllegalArgumentException("无法识别的作业启动类型：" + runType);
//		}
//		return true;
//	}
	
	/**
	 * 执行任务的接口
	 * @author   13616
	 * @date     2019/7/30 17:54
	 * @param tasks	List<TaskInfo>，任务列表
	 */
//	public void executeTask(List<TaskInfo> tasks) {
//
//		for(TaskInfo task : tasks) {
//			//第一步，为每组任务开启一个子进程，为每组任务下的作业启动为该子进程下的一个线程
//			//第二步，根据各作业的执行顺序（穿行、并行）来分配线程
//			//第三步，根据不同的作业类型，从工厂中获得具体作业实现（如果符合设计模式，则使用设计模式），并且开始执行
//			jvmPool.execute(new TaskController(task));
//		}
//	}

//	public List<JobInfo> monitorJobByProjectId(String[] projectId) {
//		//监控应该没这么简单，目前还没仔细考虑
//
//		//第一步，为每个任务编号到每个作业状态目录中去查询
//		//第二步，记录每个作业的运行状态
//		//第三步，返回结果
//
//		return new ArrayList<>();
//	}
}