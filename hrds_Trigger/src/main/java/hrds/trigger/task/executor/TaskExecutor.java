package hrds.trigger.task.executor;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.Pro_Type;
import hrds.commons.entity.Etl_job_cur;
import hrds.commons.exception.AppSystemException;
import hrds.trigger.task.helper.TaskSqlHelper;
import hrds.trigger.utils.ProcessUtil;

/**
 * ClassName: TaskExecutor<br>
 * Description: <br>
 * Author: Tiger.Wang<br>
 * Date: 2019/10/23 14:54<br>
 * Since: JDK 1.8
 **/
public class TaskExecutor {

	private static final Logger logger = LogManager.getLogger();

	private static final String PARAM_SEPARATOR = "@"; //参数分隔符
	private static final String LOGNAME_SEPARATOR = "_"; //日志文件名分隔符
	private static final String LOG_SUFFIX = ".log"; //日志文件后缀
	private static final String YARN_JOBSUFFIX = "_hyren";//yarn作业名后缀

	private static final String CRAWL_FLAG = "CRAWL";
	private static final String SHELL_COMMANDLINE= "bash";
	private static final String YARN_COMMANDLINE = "sh";
	private static final String JAVA_COMMANDLINE = "java";
	private static final String PERL_COMMANDLINE = "perl";
	private static final String PYTHON_COMMANDLINE = "python";

	public final static int PROGRAM_DONE_FLAG = 0; //进程执行正常结束标识
	public final static int PROGRAM_ERROR_FLAG = 1; //进程执行异常结束标识
	public final static int PROGRAM_FAILED_FLAG = -1; //进程执行[失败、被杀死]结束标识

	public static Etl_job_cur executeEtlJob(Etl_job_cur etlJobCur) throws IOException, InterruptedException {

		String etlSysCode = etlJobCur.getEtl_sys_cd();
		String etlJob = etlJobCur.getEtl_job();
		String proType = etlJobCur.getPro_type();
		if(Pro_Type.DBTRAN.getCode().equals(proType)) {
			//调用资源库中的Trans
			try {
				etlJobCur.setJob_return_val(runTran(etlJobCur));
			}catch(KettleException e) {
				etlJobCur.setJob_return_val(TaskExecutor.PROGRAM_ERROR_FLAG);
				logger.warn("{} execute error!" + e, etlJob);
			}
		}else if(Pro_Type.DBJOB.getCode().equals(proType)) {
			//调用资源库中的Job
			try {
				etlJobCur.setJob_return_val(runJob(etlJobCur));
			}catch(KettleException e) {
				etlJobCur.setJob_return_val(TaskExecutor.PROGRAM_ERROR_FLAG);
				logger.warn("{} execute error!" + e, etlJob);
			}
		}else {
			String[] cmds = TaskExecutor.getCommend(etlJobCur);
			logger.info("{} 作业开始执行，指令为 {}", etlJob, Arrays.toString(cmds));
			Process process = Runtime.getRuntime().exec(cmds);
			if(Pro_Type.Yarn.getCode().equals(proType)) {
				//根据Name获取appid，并存入数据库中1
				String yarnName = cmds[2];
				String appId = ProcessUtil.getAppID(yarnName);
				etlJobCur.setJob_process_id(appId);
				TaskSqlHelper.updateEtlJobProcessId(appId, etlSysCode, etlJob);
				String logDirc = TaskExecutor.getLogPath(etlJob, etlJobCur.getLog_dic(),
						etlJobCur.getCurr_bath_date(), false);
				String logDircErr = TaskExecutor.getLogPath(etlJob, etlJobCur.getLog_dic(),
						etlJobCur.getCurr_bath_date(), true);
				new WatchThread(process.getInputStream(), logDirc).start();
				new WatchThread(process.getErrorStream(), logDircErr).start();
				//作业为完成（成功、失败、结束）都退出，只有作业是运行中的这里会继续获取状态
//				if(status == 1 || status == 0 || status ==2) {
//				}
				etlJobCur.setJob_return_val(ProcessUtil.getStatusOnYarn(appId));
			}else {
				String appId = String.valueOf(ProcessUtil.getPid(process));
				etlJobCur.setJob_process_id(appId);
				TaskSqlHelper.updateEtlJobProcessId(appId, etlSysCode, etlJob);

				String logDirc = TaskExecutor.getLogPath(etlJob, etlJobCur.getLog_dic(),
						etlJobCur.getCurr_bath_date(), false);
				String logDircErr = TaskExecutor.getLogPath(etlJob, etlJobCur.getLog_dic(),
						etlJobCur.getCurr_bath_date(), true);
				new WatchThread(process.getInputStream(), logDirc).start();
				new WatchThread(process.getErrorStream(), logDircErr).start();
				etlJobCur.setJob_return_val(process.waitFor());
			}

		}

		logger.info("{} 任务执行结束，返回值 {}", etlJob, etlJobCur.getJob_return_val());

		return etlJobCur;
	}

	private static int runTran(Etl_job_cur etlJobCur) throws KettleException {
		//TODO 还未清楚是什么
		//初始化环境
		KettleEnvironment.init();
		//创建DB资源库
		KettleDatabaseRepository repository = new KettleDatabaseRepository();

		//从作业定义处取得资源库信息
		String jobName = etlJobCur.getEtl_job();
		String proName = etlJobCur.getPro_name();
		String dic = getDic(etlJobCur.getPro_dic());
		String[] propArr = getParaArr(etlJobCur.getPro_para());

		// 判断是否已经设置资源库信息
		if( propArr.length < 4 ) {
			logger.error(jobName + " Repository info is not enough!");
			return 1;
		}

		//定义一个数据库类型及参数（数据库连接名称，资源库类型，连接方式，IP，数据库名，端口，用户名，密码）
		String ketUrl = propArr[0];
		String ketDB = propArr[1];
		String ketUser = propArr[2];
		String ketPassword = propArr[3];
		DatabaseMeta databaseMeta = new DatabaseMeta("kettle", "Oracle",
				"jdbc", ketUrl, ketDB, "1521", ketUser, ketPassword);
		//选择资源库(ID,名称，描述)
		KettleDatabaseRepositoryMeta kettleDatabaseRepositoryMeta =
				new KettleDatabaseRepositoryMeta("kettle", "kettle",
				"Transformation description", databaseMeta);
		repository.init(kettleDatabaseRepositoryMeta);
		//连接资源库（用户名，密码）
		repository.connect("admin", "admin");
		//获取作业的目录"/test/lyy"
		RepositoryDirectoryInterface directoryInterface = repository.findDirectory(dic);
		//repository.loadRepositoryDirectoryTree(directoryInterface);
		//选择转换
		TransMeta transformationMeta = ((Repository)repository)
				.loadTransformation(proName, directoryInterface, null, true, null);
		Trans trans = new Trans(transformationMeta);
		if( propArr.length > 4 ) {
			String[] parms = new String[propArr.length - 4];
			System.arraycopy(propArr, propArr.length - 3, parms, 0, propArr.length - 4);
			trans.execute(parms);
		}else {
			trans.execute(null);
		}

		trans.waitUntilFinished();//等待直到数据结束
		if( trans.getErrors() > 0 ) {
			logger.error(jobName + " execute error.");
			return TaskExecutor.PROGRAM_ERROR_FLAG;
		}else {
			logger.info(jobName + " execute success.");
			return TaskExecutor.PROGRAM_DONE_FLAG;
		}
	}

	private static int runJob(Etl_job_cur etlJobCur) throws KettleException {
		//TODO 还未清楚是什么
		//初始化环境
		KettleEnvironment.init();
		//创建DB资源库
		KettleDatabaseRepository repository = new KettleDatabaseRepository();

		//从作业定义处取得资源库信息
		String jobName = etlJobCur.getEtl_job();
		String proName = etlJobCur.getPro_name();
		String dic = getDic(etlJobCur.getPro_dic());
		String[] propArr = getParaArr(etlJobCur.getPro_para());

		// 判断是否已经设置资源库信息
		if( propArr.length < 4 ) {
			logger.error(jobName + " Repository info is not enough!");
			return 1;
		}

		//定义一个数据库类型及参数（数据库连接名称，资源库类型，连接方式，IP，数据库名，端口，用户名，密码）
		String ketUrl = propArr[0];
		String ketDB = propArr[1];
		String ketUser = propArr[2];
		String ketPassword = propArr[3];
		DatabaseMeta databaseMeta = new DatabaseMeta("kettle", "Oracle", "jdbc", ketUrl, ketDB, "1521", ketUser, ketPassword);
		//选择资源库(ID,名称，描述)
		KettleDatabaseRepositoryMeta kettleDatabaseRepositoryMeta = new KettleDatabaseRepositoryMeta("kettle", "kettle",
				"Transformation description", databaseMeta);
		repository.init(kettleDatabaseRepositoryMeta);
		//连接资源库（用户名，密码）
		repository.connect("admin", "admin");
		//获取作业的目录"/test/lyy"
		RepositoryDirectoryInterface directoryInterface = repository.findDirectory(dic);
		repository.loadRepositoryDirectoryTree(directoryInterface);
		//选择转换
		JobMeta jobMeta = repository.loadJob(proName, directoryInterface, null, null);//从资源库中加载一个job
		//Job(Repository repository,JobMeta jobMeta),KettleDatabaseRepository是Repository的实现类
		Job job = new Job(repository, jobMeta);
		for(int i = 4; i < propArr.length;) {
			job.setParameterValue(propArr[i], propArr[i + 1]);
			i = i + 2;
		}
		job.start();
		job.waitUntilFinished();//等待直到数据结束
		if(job.getErrors() > 0) {
			logger.error(jobName + " execute error.");
			return TaskExecutor.PROGRAM_ERROR_FLAG;
		}else {
			logger.info(jobName + " execute success.");
			return TaskExecutor.PROGRAM_DONE_FLAG;
		}
	}

	private static String[] getCommend(Etl_job_cur etlJobCur) {

		String jobName = etlJobCur.getEtl_job();
		//目的是验证日期格式
		LocalDate.parse(etlJobCur.getCurr_bath_date(), DateUtil.DATE_DEFAULT);
		String currBathDate = etlJobCur.getCurr_bath_date();
		String proType = etlJobCur.getPro_type();
		String proName = etlJobCur.getPro_name();
		String proDic = TaskExecutor.getDic(etlJobCur.getPro_dic());
		String logDic = TaskExecutor.getDic(etlJobCur.getLog_dic());
		String[] propArr = TaskExecutor.getParaArr(etlJobCur.getPro_para());
		String[] commands;
		if(Pro_Type.SHELL.getCode().equals(proType)) {
			commands = new String[propArr.length + 4];
			commands[0] = SHELL_COMMANDLINE;
			commands[1] = proDic + proName;
			System.arraycopy(propArr, 0, commands, 2, propArr.length);
		}else if(Pro_Type.Yarn.getCode().equals(proType)) {
			//添加yarn的作业调度方式，目前我们yarn的调度方式只支持shell的凡是调度，现不支持直接使用jar的方式进行调度
			commands = new String[propArr.length + 4];
			commands[0] = YARN_COMMANDLINE;
			commands[1] = proDic + proName;
			commands[2] = UUID.randomUUID() + YARN_JOBSUFFIX;
			//这里需要生产一个唯一的yarn的name名称，shell必须要求第一个参数是作业名称，强制要求，否则我们获取不到
			System.arraycopy(propArr, 0, commands, 3, propArr.length);
		}else if(Pro_Type.JAVA.getCode().equals(proType)) {
			commands = new String[propArr.length + 4];
			commands[0] = JAVA_COMMANDLINE;
			commands[1] = proDic + proName;
			System.arraycopy(propArr, 0, commands, 2, propArr.length);
		}else if(Pro_Type.BAT.getCode().equals(proType)) {
			commands = new String[propArr.length + 3];
			commands[0] = proDic + proName;
			System.arraycopy(propArr, 0, commands, 1, propArr.length);
		}else if(Pro_Type.PERL.getCode().equals(proType)) {
			commands = new String[propArr.length + 4];
			commands[0] = PERL_COMMANDLINE;
			commands[1] = proDic + proName;
			System.arraycopy(propArr, 0, commands, 2, propArr.length);
		}else if(Pro_Type.PYTHON.getCode().equals(proType)) {
			commands = new String[propArr.length + 4];
			commands[0] = PYTHON_COMMANDLINE;
			commands[1] = proDic + proName;
			System.arraycopy(propArr, 0, commands, 2, propArr.length);
		}else if(CRAWL_FLAG.equals(proType)) {
			commands = new String[propArr.length + 3];
			commands[0] = proDic + proName;
			System.arraycopy(propArr, 0, commands, 1, propArr.length);
		}else {
			throw new AppSystemException("暂不支持的程序类型：" + proType);
		}

		if(!StringUtil.isEmpty(logDic)) {
			commands[commands.length - 2] = TaskExecutor.getLogPath(jobName, logDic,
					currBathDate, false);
			commands[commands.length - 1] = TaskExecutor.getLogPath(jobName, logDic,
					currBathDate, true);
		}

		return commands;
	}

	private static String getDic(String dicPath) {

		File file = new File(dicPath);
		if(!file.exists() && file.mkdirs()) throw new AppSystemException("无法创建目录：" + dicPath);

		return dicPath;
	}

	private static String[] getParaArr(String para) {

		if(StringUtil.isEmpty(para)) return new String[] {};

		return para.split(PARAM_SEPARATOR);
	}

	private static String getLogPath(String proName, String logDic, String currDate,
	                                 boolean isErrorLog) {

		String logPath = logDic + File.separator + proName + LOGNAME_SEPARATOR + currDate;

		if(isErrorLog) {
			logPath = logPath + "error" + LOG_SUFFIX;
		}else {
			logPath = logPath + LOG_SUFFIX;
		}

		return logPath;
	}
}
