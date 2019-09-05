package hrds.control.task;

import fd.ng.core.utils.StringUtil;
import hrds.codes.*;
import hrds.control.beans.EtlJobDefBean;
import hrds.control.beans.EtlJobBean;
import hrds.control.beans.WaitFileJobInfo;
import hrds.control.task.helper.RedisHelper;
import hrds.control.task.helper.TaskJobHelper;
import hrds.control.task.helper.TaskSqlHelper;
import hrds.control.utils.DateUtil;
import hrds.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

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
	private final static String DEFAULT_DATETIME = "2000-12-31 23:59:59";
	// 调度作业定义表
	private static Map<String, Etl_job_def> jobDefineMap = new HashMap<>();
	// 调度作业的时间依赖表
	private static Map<String, String> jobTimeDependencyMap = new HashMap<>();
	// 调度作业间关系依赖表
	private static Map<String, List<String>> jobDependencyMap = new HashMap<>();
	// 待调度作业表
	private static Map<String, Map<String, EtlJobBean>> jobExecuteMap = new HashMap<>();
	// 监视文件作业列表
	private static List<WaitFileJobInfo> waitFileJobList = new ArrayList<>();
	// 需要等待调度作业
	private static List<EtlJobBean> jobWaitingList = new ArrayList<>();
	//TODO 这个名字叫什么我不知道
	private static final long zclong = 999999999999999999l;
	private static final Calendar calendar = Calendar.getInstance();

	private boolean sysRunning;
	private final LocalDate bathDate;
	private final String etlSysCd;
	private final boolean isResumeRun;
	private final boolean isAutoShift;

	public final static String REDISCONTENTSEPARATOR = "@";	//redis字符内容分隔符
	private final static String RUNNINGJOBFLAG = "RunningJob";
	private final static String FINISHEDJOBFLAG = "FinishedJob";
	private final String strRunningJob;	//存入redis中的键值（标识需要马上执行的作业）
	private final String strFinishedJob;//存入redis中的键值（标识已经停止的作业）

	private final static RedisHelper REDIS = RedisHelper.getInstance();

	/**
	 * 静态工厂，用于构造TaskManager实例
	 * @author Tiger.Wang
	 * @date 2019/8/30
	 * @param sysRunning	系统是否在运行中
	 * @param bathDate	跑批批次日期
	 * @param strSystemCode	调度系统代码
	 * @param isResumeRun	是否续跑
	 * @param isAutoShift	是否自动日切
	 * @return hrds.agent.control.task.manager.TaskManager
	 */
	public static TaskManager newInstance(boolean sysRunning, String strSystemCode, LocalDate bathDate, boolean isResumeRun, boolean isAutoShift) {

		return new TaskManager(sysRunning, strSystemCode, bathDate, isResumeRun, isAutoShift);
	}

	/**
	 * TaskManager类构造器，构造器私有化，使用newInstance方法获得静态实例，不允许外部构造。
	 * @author Tiger.Wang
	 * @date 2019/8/30
	 * @param sysRunning	系统是否在运行中
	 * @param bathDate	跑批批次日期
	 * @param etlSysCd	调度系统代码
	 * @param isResumeRun	是否续跑
	 * @param isAutoShift	是否自动日切
	 */
	private TaskManager(boolean sysRunning, String etlSysCd, LocalDate bathDate, boolean isResumeRun, boolean isAutoShift) {

		this.sysRunning = sysRunning;
		this.etlSysCd = etlSysCd;
		this.bathDate = bathDate;
		this.isResumeRun = isResumeRun;
		this.isAutoShift = isAutoShift;

		strRunningJob = etlSysCd + RUNNINGJOBFLAG;
		strFinishedJob = etlSysCd + FINISHEDJOBFLAG;
	}

	/**
	 * 根据系统编号加载需要立即启动的作业信息；<br>
	 * @note	1、获取所有作业定义表的作业信息；<br>
	 * 			2、分析并加载作业，将作业加载进内存表；<br>
	 * 			3、分析并加载作业依赖信息。
	 * @author Tiger.Wang
	 * @date 2019/8/31
	 */
	private void loadReadyJob() {

		//TODO 此处有问题，如果每3秒调用一次该方法（意味着查询一次数据库来获取作业信息），
		// 那么如果每次clear，是否还需要这两个全局变量
		jobDefineMap.clear();
		jobTimeDependencyMap.clear();
		//1、获取所有作业定义表的作业信息。
		List<EtlJobDefBean> jobs = TaskSqlHelper.getAllDefJob(etlSysCd);
		//2、分析并加载作业定义信息，将作业加载进作业定义表（内存表）
		boolean hasFrequancy = loadJobDefine(jobs);
		//3、分析并加载作业依赖信息。
		loadJobDependency();
		//4、分析并加载作业信息，将作业加载进待调度作业表（内存表）
		loadExecuteJob(jobs, hasFrequancy);
	}

	/**
	 * 根据作业列表，判断每个作业触发方式、是否达成触发条件、资源是否足够、作业依赖等条件；<br>
	 * 此处会维护jobDefineMap全局变量，用于存放作业信息；<br>
	 * 此处会维护jobTimeDependencyMap全局变量，用于存放触发类型为频率的作业，key为作业标识，value为触发时间。<br>
	 * @note	1、判断每个作业是否需要立即执行；
	 * 			2、为作业设置需要的资源；
	 * @author Tiger.Wang
	 * @date 2019/9/3
	 * @param jobs	作业列表
	 * @return boolean	是否有按频率调度的ETL调度频率类型作业
	 */
	private boolean loadJobDefine(List<EtlJobDefBean> jobs) {

		for(EtlJobDefBean job : jobs) {
			//getEtl_job()方法获取任务名（任务标识）
			String etlJobId = job.getEtl_job();
			String etlDispType = job.getDisp_type();
			//TODO 此处较原版改动：1、disp_type中不再有"F"类型，通过判断disp_type为T+0，disp_freq为"F"类型来完成；
			// 2、原版的getFJob(job.getEtl_job(), taskId, "def")改为checkFrquencyJob(job)，且全局变量jobFrequencyMap去除
			/*
			 * 1、判断每个作业是否需要立即执行:
			 * 		一、若作业为T+0调度方式，在以频率为频率类型的情况下，则认为按频率调度的ETL调度频率类型作业
			 * 			的结论，在不是以频率为频率类型的情况下，则认为该作业有时间依赖，并记录；
			 *		二、若作业为T+1调度方式，则认为该作业有时间依赖，并记录。
			 */
			if(Dispatch_Type.ZTIMING.getCode().equals(etlDispType)) {
				//TODO 此处有疑问，难道T+1不会有按小时、分钟触发
				//TODO 此处较原版改动：在Dispatch_Type为ZTIMING与TIMING时都设置jobTimeDependencyMap，但是在ZTIMING时，
				// 调度频率类型为PinLv，则会检测是否有频率类型作业，此处取消了jobFrequencyMap全局变量
				if(Dispatch_Frequency.PinLv.getCode().equals(job.getDisp_freq())) {
					//TODO 此处较原版改动：原版是getFJob(tempJob.getStrEtlJob(), strSystemCode, "def")，
					// 在该方法中不再queryIsAllDoneDef，因为job变量本身就代表着job_def的作业信息，没必要再次查询
					return checkEtlDefJob(job);
				}
				// 如果作业的调度触发方式为T+0定时触发时，将作业及触发时间记录
				jobTimeDependencyMap.put(etlJobId, job.getDisp_time());
			}else if(Dispatch_Type.TIMING.getCode().equals(etlDispType)) {
				// 如果作业的调度触发方式为T+1定时触发时，将作业及触发时间记录
				jobTimeDependencyMap.put(etlJobId, job.getDisp_time());
			}

			jobDefineMap.put(etlJobId, job);
			//2、为作业设置需要的资源。
			List<Etl_job_resource_rela> jobNeedResources = TaskSqlHelper.getJobNeedResources(etlSysCd, etlJobId);
			if(null != jobNeedResources) {
				job.setJobResources(jobNeedResources);
			}
		}

		return false;
	}

	/**
	 * 根据系统编号加载作业依赖关系，此处会使用jobDefineMap全局变量进行判断，所以请注意调用顺序。<br>
	 * 此处会维护jobDependencyMap全局变量，key为作业标识，value为该作业的依赖作业列表。<br>
	 * @note	1、判断每个依赖作业是否在作业定义表中；
	 * 			2、设置作业依赖到jobDependencyMap内存中。
	 * @author Tiger.Wang
	 * @date 2019/9/3
	 */
	private void loadJobDependency() {
		//TODO 为什么需要这个全局变量，每次都clear()
		jobDependencyMap.clear();
		List<Etl_dependency> etlDependencies = TaskSqlHelper.getJobDependencyBySysCode(etlSysCd);
		for(Etl_dependency etlDependency : etlDependencies) {
			String etlJobId = etlDependency.getEtl_job();
			//1、判断每个依赖作业是否在作业定义表中，若该依赖作业不在调度范围内，则认为该作业不作为依赖作业
			if(!jobDefineMap.containsKey(etlJobId)) {
				continue;
			}
			//依赖作业标识，若不存在依赖作业，则认为该祖业不作为依赖作业
			String preEtlJob = etlDependency.getPre_etl_job();
			if(StringUtil.isEmpty(preEtlJob)) {
				continue;
			}
			preEtlJob = preEtlJob.trim();
			/*
			 * 2、设置作业依赖到jobDependencyMap内存中：
			 * 	一、若作业依赖已经设置，在作业依赖列表中不存在该依赖作业的情况下，将该依赖作业加入作业依赖列表；
			 * 	二、若作业依赖未设置，则为该作业设置依赖作业，此时会为该作业新建作业依赖列表。
			 */
			if(jobDependencyMap.containsKey(etlJobId)) {
				List<String> dependenies = jobDependencyMap.get(etlJobId);
				if(!dependenies.contains(preEtlJob)) {
					dependenies.add(preEtlJob);
				}
			}else {
				List<String> dependenies = new ArrayList<>();
				dependenies.add(preEtlJob);
				jobDependencyMap.put(etlJobId, dependenies);
			}
		}
	}

	/**
	 * 加载需要执行的作业，分析并识别作业是否需要立即调度，如果需要立即调度，则将该作业加载进待调度作业表（内存表）。
	 * 注意，该方法会对ETL_SYS表、ETL_JOB表、etl_resource表有修改和删除操作。
	 *
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param jobs	作业定义信息集合
	 * @param hasFrequancy	该批次作业是否有调度类型为每日调度，频率类型为“频率”的作业
	 */
	private void loadExecuteJob(List<EtlJobDefBean> jobs, boolean hasFrequancy) {

		String strBathDate = bathDate.format(DateUtil.DATE_DEFAULT);
		/*
		 * 一、若系统在运行中，主要行为如下：
		 * 		（一）、更新该系统的跑批日期。在干预日切及自动日切的情况下，批量日期会增加；
		 * 		（二）、清理已经登记的作业（清空etl_job表），但不会清空作业类型为T+0且按频率调度的作业；
		 * 		（三）、检查并计算出作业定义信息中，达到执行条件的作业，将该作业登记到内存表及etl_job表；
		 * 		（四）、更新作业调度表中的作业调度状态；
		 * 		（五）、检查并登记作业到作业依赖表；
		 * 		（六）、检查并登记作业到作业等待表。
		 * 二、若系统不在运行，调度系统以续跑方式启动。主要行为如下：
		 * 		（一）、更新该批次作业运行状态；
		 * 		（二）、更新该批次作业调度状态；
		 * 		（三）、加载符合运行条件作业进待调度作业表（内存表）；
		 * 		（四）、清空该批次作业的已使用资源。
		 * 三、若系统不在运行，调度系统不以续跑方式启动。主要行为如下：
		 * 		（一）、更新该系统的跑批日期及系统运行状态。在干预日切及自动日切的情况下，批量日期会增加；
		 * 		（二）、清理掉已登记的作业；
		 * 		（三）、计算当前调度日期可以执行的作业；
		 * 		（四）、清空该批次作业的已使用资源；
		 * 		（五）、更新作业的调度状态。
		 */
		if(sysRunning) {	//如果系统在运行中
			// 修改ETL_SYS的[批量日期]为日切后的的批量日期
			TaskSqlHelper.updateEtlSysBathDate(etlSysCd, strBathDate);
			// 清理ETL_JOB，范围限定为：该系统、该批量日期、非作业类型为T+0且按频率调度的作业。
			TaskSqlHelper.deleteEtlSysByBathDate(etlSysCd, strBathDate);
			// 计算当前调度日期可以执行的作业
			loadCanDoJobWithNoResume(jobs, hasFrequancy);
			// 将作业的状态都置为Pending
			TaskSqlHelper.updateEtjJobWithDispStatus(Job_Status.PENDING.getCode(), etlSysCd, strBathDate);
			// 检查作业的依赖
			checkJobDependency(strBathDate);
			// 初始化需要加入到等待列表的作业（内存表）
			initWaitingJob(strBathDate);
		}else {	//若系统不在运行
			if(isResumeRun) {	//调度系统需要续跑
				// 修改ETL_SYS该系统的 [状态] 为运行中
				TaskSqlHelper.updateEtlSysRunStatus(etlSysCd, Job_Status.RUNNING.getCode());
				// 修改ETL_JOB表 非PENDING/DONE的 [作业状态] 为PENDING，范围限定为：该系统、该批量日期、且当日调度的作业。
				TaskSqlHelper.updateEtjJobByResumeRun(etlSysCd, strBathDate);
				// 取得ETL_JOB表中当前调度日期前已经存在的作业
				loadExecuteJobWithRunning(etlSysCd, strBathDate);
				// 将资源表都清空，参数：0的含义为清空该工程下的已使用资源
				TaskSqlHelper.updateEtlResourceUsed(etlSysCd, 0);
			}else {	//调度系统不需要续跑
				// 修改ETL_SYS该系统的 [状态] 为运行中，并且登记当前跑批日期
				TaskSqlHelper.updateEtlSysRunStatusAndBathDate(etlSysCd, strBathDate, Job_Status.RUNNING.getCode());
				// 清理ETL_JOB，因为在系统第一次运行，且不是续跑的情况下，需要清理掉已登记的作业。
				TaskSqlHelper.deleteEtlSysBySysCode(etlSysCd);
				// 计算当前调度日期可以执行的作业
				loadCanDoJobWithNoResume(jobs, hasFrequancy);
				// 将资源表都清空，参数：0的含义为清空该工程下的已使用资源
				TaskSqlHelper.updateEtlResourceUsed(etlSysCd, 0);
				// 将作业的状态都置为Pending
				TaskSqlHelper.updateEtjJobWithDispStatus(Job_Status.PENDING.getCode(), etlSysCd, strBathDate);
			}

			// 调度系统初期化时，将所有作业的依赖关系都初期化
			checkJobDependency("");
			initWaitingJob("");
			sysRunning = true;	// 调度系统在运行完一次后，必然进入运行中的状态
		}
	}

	/**
	 * 加载能马上运行的作业。注意，此处会维护jobExecuteMap全局变量、隐式的为jobs设置参数、更新etl_job表，
	 * 此方法仅在调度服务在“非续跑”状态下使用。
	 * @note 1、检查每个作业是否需要马上执行；
	 * 		 2、为每个作业设置参数；
	 * 		 3、将作业登记到jobExecuteMap内存中。
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param jobs	作业集合
	 * @param hasFrequancy	该批次作业是否有每日按“频率”调度
	 */
	private void loadCanDoJobWithNoResume(List<EtlJobDefBean> jobs, boolean hasFrequancy) {

		String strBathDate = bathDate.format(DateUtil.DATE_DEFAULT);
		Map<String, EtlJobBean> executeJobMap = new HashMap<>();
		for(EtlJobDefBean job : jobs) {
			String curr_st_time = job.getCurr_st_time();
			if(StringUtil.isEmpty(curr_st_time)) {
				job.setCurr_st_time(DEFAULT_DATETIME);
			}
			EtlJobBean executeJob = new EtlJobBean();
			executeJob.setEtl_job(job.getEtl_job());
			executeJob.setJob_disp_status(Job_Status.PENDING.getCode());
			executeJob.setCurr_bath_date(strBathDate);
			executeJob.setJob_priority_curr(job.getJob_priority());
			executeJob.setPro_type(job.getPro_type());
			executeJob.setExe_num(job.getExe_num());
			executeJob.setCom_exe_num(job.getCom_exe_num());
			executeJob.setEnd_time(job.getEnd_time());

			String strDispFreq = job.getDisp_freq();
			/*
			 * 1、检查每个作业是否需要马上执行
			 * 一、根据调度频率类型、偏移量、跑批日期等判断调度日期是否要调度该作业；
			 * 二、如果认为该作业需要调度，则检查并转换作业参数：作业程序目录、作业日志目录、作业程序名称、作业程序参数。
			 * 	   如果该作业不为“频率”类型，则将该作业登记到作业表（etl_job）中，最后为该作业计算下一次执行时间；
			 * 三、如果认为该作业不需要调度，则设置该作业“今天是否调度”标识为否。
			 */
			if(checkDispFrequency(strDispFreq, job.getDisp_offset(), bathDate, job.getExe_num(),
					job.getCom_exe_num(), job.getStar_time(), job.getEnd_time())) {
				//2、为每个作业设置参数
				job.setCurr_bath_date(strBathDate);
				job.setMain_serv_sync(Main_Server_Sync.NO.getCode());
				TaskJobHelper.transformProgramDir(job);	// 替换作业程序目录
				TaskJobHelper.transformLogDir(job);		// 替换作业日志目录
				TaskJobHelper.transformProName(job);	// 替换作业程序名称
				TaskJobHelper.transformProPara(job);	// 替换作业程序参数
				//TODO 此处按照原版写，原版没有在这写逻辑
				if( hasFrequancy && (strDispFreq).equals(Dispatch_Frequency.PinLv.getCode()) ) {

				}else {
					Etl_job etlJob = TaskJobHelper.etlJobDefCopy2EltJob(job);
					TaskSqlHelper.insertIntoJobTable(etlJob);
				}
				job.setToday_disp(Today_Dispatch_Flag.YES.getCode());
				// 计算调度作业的下一批次作业日期
				executeJob.setStrNextDate(TaskJobHelper.getNextExecuteDate(bathDate, strDispFreq));
				executeJobMap.put(executeJob.getEtl_job(), executeJob);
			}else {
				job.setToday_disp(Today_Dispatch_Flag.NO.getCode());
			}
		}
		//TODO 有问题，为什么空的executeJobMap也要登记
		//3、将作业登记到jobExecuteMap内存中
		jobExecuteMap.put(strBathDate, executeJobMap);
	}

	/**
	 * 加载能马上运行的作业。注意，此处会维护jobExecuteMap全局变量，此方法仅在调度服务在“非运行”状态下使用。
	 *
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param strSystemCode	调度系统编号
	 * @param strBathDate	跑批日期（yyyy-MM-dd）
	 */
	private void loadExecuteJobWithRunning(String strSystemCode, String strBathDate) {

		List<Etl_job> currentJobs= TaskSqlHelper.getEtlJobs(strSystemCode, strBathDate);
		for(Etl_job job : currentJobs) {
			EtlJobBean executeJob = new EtlJobBean();
			executeJob.setEtl_job(job.getEtl_job());
			executeJob.setJob_disp_status(job.getJob_disp_status());
			executeJob.setCurr_bath_date(job.getCurr_bath_date());
			executeJob.setJob_priority_curr(job.getJob_priority_curr());
			executeJob.setPro_type(job.getPro_type());
			executeJob.setExe_num(job.getExe_num());
			executeJob.setCom_exe_num(job.getCom_exe_num());
			executeJob.setEnd_time(job.getEnd_time());
			//TODO 此处可考虑验证日期格式，若像原版一样将字符串转日期对象，再将日期对象转字符串没任何意义，
			// 因为在将字符串转日期对象时，必须给定一个日期表达式，若字符串不符合表达式，会要么抛异常，要么对象是错的
			String strCurrBathDate = job.getCurr_bath_date();
			// 计算的执行作业的下一批次作业日期
			LocalDate currBathDate = LocalDate.parse(strCurrBathDate, DateUtil.DATE);
			executeJob.setStrNextDate(TaskJobHelper.getNextExecuteDate(currBathDate, job.getDisp_freq()));
			// 将执行作业加入执行作业表
			if(jobExecuteMap.containsKey(strCurrBathDate)) {
				jobExecuteMap.get(strCurrBathDate).put(executeJob.getEtl_job(), executeJob);
			}else {
				Map<String, EtlJobBean> jobMap = new HashMap<>();
				jobMap.put(executeJob.getEtl_job(), executeJob);
				jobExecuteMap.put(strCurrBathDate, jobMap);
			}
		}
	}

	/**
	 * 检查待执行作业间的依赖作业。注意，当传入的值为空字符串时，则每个在jobExecuteMap中的作业都会检查，
	 * 该方法会修改jobExecuteMap中的作业信息。
	 *
	 * 1.判断前一批次作业是否已经完成
	 * 2.作业依赖时，依赖作业是否已经已经完成
	 * 3.时间依赖时，计算出执行时间
	 * @param strCurrBathDate 当前调度日期
	 */
	private void checkJobDependency(String strCurrBathDate) {

		Iterator<String> dateIter = jobExecuteMap.keySet().iterator();
		while( dateIter.hasNext() ) {
			String strBathDate = dateIter.next();
			// 如果调度系统日切时，只检查当前调度日期作业的依赖作业
			if( !strCurrBathDate.isEmpty() && !strCurrBathDate.equals(strBathDate) ) {
				continue;
			}

			LocalDate currBathDate = LocalDate.parse(strBathDate, DateUtil.DATETIME);
			Map<String, EtlJobBean> jobMap = jobExecuteMap.get(strBathDate);

			Iterator<String> jobIter = jobMap.keySet().iterator();
			while( jobIter.hasNext() ) {
				String strJobName = jobIter.next();
				// 取得作业的定义
				Etl_job_def jobDefine = jobDefineMap.get(strJobName);
				if( null == jobDefine ) {
					continue;
				}
				// 取得执行作业
				EtlJobBean job = jobMap.get(strJobName);

				// 1.判断前一批次作业是否已经完成
				String strPreDate = TaskJobHelper.getPreExecuteDate(currBathDate, jobDefine.getDisp_freq());
				job.setPreDateFlag(checkJobFinished(strPreDate, strJobName));

				String dispType = jobDefine.getDisp_type();
				/*
				 * 判断执行作业的调度触发方式。
				 * 一、若作业调度方式为依赖触发，则检查jobDependencyMap内存表中是否有该作业相关依赖，并设置依赖调度标识；
				 * 二、若作业调度方式为定时T+1触发，则计算出执行日期时间，并设置依赖调度标识；
				 * 三、若作业调度方式为定时T+0触发，则计算出执行日期时间，并设置依赖调度标识；
				 */
				if(Dispatch_Type.DEPENDENCE.getCode().equals(dispType)) {	//依赖触发
					// 取得依赖作业列表
					List<String> dependencyJobList = jobDependencyMap.get(strJobName);
					if( dependencyJobList == null ) {
						// 依赖作业列表没有，可以直接调度
						job.setDependencyFlag(true);
					}else {
						// 判断已经完成的依赖作业个数
						int finishedDepJobCount = 0;
						for(int i = 0; i < dependencyJobList.size(); ++i) {
							if( checkJobFinished(strBathDate, dependencyJobList.get(i)) ) {
								++finishedDepJobCount;
							}
						}
						job.setDoneDependencyJobCount(finishedDepJobCount);
						// 判断依赖的作业是否已经全部完成
						if( finishedDepJobCount == dependencyJobList.size() ) {
							// 已经全部完成，可以准备调度此作业
							job.setDependencyFlag(true);
						}else {
							job.setDependencyFlag(false);
						}
					}
					job.setExecuteTime(0L);
				} else if(Dispatch_Type.TIMING.getCode().equals(dispType)) {
					// 定时T+1触发
					String strDispTime = jobTimeDependencyMap.get(strJobName);
					if( null == strDispTime ) {
						job.setExecuteTime(currBathDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
					}else {
						//TODO 应该使用JDK8的新日期类来完成日期时间计算
						Date executeDate = TaskJobHelper.getTtypeExecuteTime(strBathDate + " " + strDispTime);
						job.setExecuteTime(executeDate.getTime());
					}
					job.setDependencyFlag(false);
					logger.info("{}'s executeTime={}", strJobName, job.getExecuteTime());
				} else if(Dispatch_Type.ZTIMING.getCode().equals(dispType)) {
					//TODO 此处较原版改动：disp_type中不再有"F"类型，而是T+0时，频率类型为"频率"，
					// 以此逻辑来判断，T+0加上日切才是每天都跑？
					if(Dispatch_Frequency.PinLv.getCode().equals(jobDefine.getDisp_freq())) {
						job.setExecuteTime(zclong);
						job.setDependencyFlag(false);
					}else {
						// 定时准点触发
						String strDispTime = jobTimeDependencyMap.get(strJobName);
						if( null == strDispTime ) {
							job.setExecuteTime(currBathDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
						}else {
							Date executeDate = TaskJobHelper.getZtypeExecuteTime(strBathDate + " " + strDispTime);
							job.setExecuteTime(executeDate.getTime());
						}
						job.setDependencyFlag(false);
						logger.info("{}'s executeTime={}", strJobName, job.getExecuteTime());
					}
				}
			}
		}
	}

	/**
	 * 将执行作业表中，前置依赖都满足的作业状态置为Waiting。注意， 当传入的值为空字符串时，
	 * 则每个在jobExecuteMap中的作业等会检查，该方法也会将虚作业登记到redis中。
	 *
	 * @param strCurrBathDate 当前调度日期
	 */
	private void initWaitingJob(String strCurrBathDate) {

		Iterator<String> dateIter = jobExecuteMap.keySet().iterator();
		while(dateIter.hasNext()) {
			String strBathDate = dateIter.next();
			if(!strCurrBathDate.isEmpty() && !strCurrBathDate.equals(strBathDate)) {
				continue;
			}

			Map<String, EtlJobBean> jobMap = jobExecuteMap.get(strBathDate);
			Iterator<String> jobIter = jobMap.keySet().iterator();
			while(jobIter.hasNext()) {
				String strJobName = jobIter.next();
				EtlJobBean exeJob = jobMap.get(strJobName);
				String etlJob = exeJob.getEtl_job();
				String currBathDate = exeJob.getCurr_bath_date();
				Etl_job_def jobDefine = jobDefineMap.get(strJobName);
				if(null == jobDefine) {
					continue;
				}
				// 判断执行作业的作业状态
				if(Job_Status.PENDING.getCode().equals(exeJob.getJob_disp_status())) {
					// 作业状态为Pending,检查前置作业是否已完成
					// 判断前一批次作业是否已经完成
					if(!exeJob.isPreDateFlag()) {
						// 前一批次作业未完成，作业状态不能置为Waiting
						continue;
					}
					String dispType = jobDefine.getDisp_type();
					// 判断作业调度触发方式是否满足
					if(Dispatch_Type.DEPENDENCE.getCode().equals(dispType)) {
						// 依赖触发,判断依赖作业是否已经完成
						if(!exeJob.isDependencyFlag()) {
							// 依赖作业未完成，作业状态不能置为Waiting
							continue;
						}
					}
					else if(Dispatch_Type.TIMING.getCode().equals(dispType)) {
						// 定时触发,判断作业调度时间是否已经达到
						if(calendar.getTime().getTime() < exeJob.getExecuteTime()) {
							// 作业调度时间未到,作业状态不能置为Waiting
							continue;
						}
					}
					else if(Dispatch_Type.ZTIMING.getCode().equals(dispType)) {
						// 定时触发,判断作业调度时间是否已经达到
						if(Dispatch_Frequency.PinLv.getCode().equals(jobDefine.getDisp_freq())) {
							//xchao--2017年8月15日 16:25:51 添加按秒、分钟、小时进行执行
							//TODO 此处较原版改动：原版代码getFJob(exeJob.getEtl_job(), strSystemCode, "job")
							// 不再根据作业标识、调度系统编号查询一次数据库，因为该作业是从内存中取出的，只需要为该作业设置
							// 足够的值，即可对该作业进行检验。
							if(!checkEtlJob(exeJob)) {
								continue;
							}
						}else if(calendar.getTime().getTime() < exeJob.getExecuteTime()) {
							// 作业调度时间未到,作业状态不能置为Waiting
							continue;
						}
					}else {
						// 其他触发方式，暂不支持
						continue;
					}

					// 判断作业的作业有效标志
					if(Job_Effective_Flag.VIRTUAL.getCode().equals(jobDefine.getJob_eff_flag())) {
						// 如果是虚作业的话，直接将作业状态置为Done
						exeJob.setJob_disp_status(Job_Status.RUNNING.getCode());

						String localDateTime = fd.ng.core.utils.DateUtil.getDateTime(DateUtil.DATETIME);
						TaskSqlHelper.updateVirtualJob(etlSysCd, etlJob, currBathDate, localDateTime, localDateTime);

						// 将虚作业加入redis完成作业列表
						String finishedJob = etlJob + REDISCONTENTSEPARATOR + currBathDate;
						REDIS.rpush(strFinishedJob, finishedJob);
						continue;
					}
					else {
						// 将Pending状态置为Waiting
						TaskSqlHelper.updateEtlJobDispStatus(Job_Status.WAITING.getCode(), etlSysCd, etlJob,
								currBathDate);
					}
				}
				else if(!Job_Status.WAITING.getCode().equals(exeJob.getJob_disp_status())) {
					// Pending和Waiting状态外的作业暂不处理
					continue;
				}

				if(Pro_Type.WF.getCode().equals(exeJob.getPro_type())) {
					addWaitFileJobToList(exeJob);
				}
				else {
					jobWaitingList.add(exeJob);
				}
			}
		}
	}

	/**
	 * 将ETL作业类型为"WF"的作业，加入waitFileJobList内存表中
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param exeJob	作业对象
	 */
	private void addWaitFileJobToList(EtlJobBean exeJob) {

		Map<String, Object> jobKeyMap = new HashMap<>();
		Etl_job etlJob = TaskSqlHelper.getEtlJob(etlSysCd, exeJob.getEtl_job(), exeJob.getCurr_bath_date()).
				orElse(null);

		if(null == etlJob) {
			logger.error("没有找到作业！EtlJob=[%s],BathDate=[%s]", exeJob.getEtl_job(), exeJob.getCurr_bath_date());
			return;
		}

		String strEtlJob = exeJob.getEtl_job();
		String currBathDate = exeJob.getCurr_bath_date();

		// 更新作业的状态到Running
		TaskSqlHelper.updateEtlJobDispStatus(Job_Status.RUNNING.getCode(), etlSysCd, strEtlJob, currBathDate);

		// 添加作业执行时间
		etlJob.setCurr_st_time(fd.ng.core.utils.DateUtil.getDateTime(DateUtil.DATETIME));
		TaskSqlHelper.updateEtlJobRunTime(etlJob.getCurr_st_time(), etlSysCd, strEtlJob);

		WaitFileJobInfo waitFileJob = new WaitFileJobInfo();
		waitFileJob.setStrJobName(strEtlJob);
		waitFileJob.setStrBathDate(currBathDate);
		String waitFilePath = exeJob.getPro_dic() + exeJob.getPro_name();
		waitFileJob.setWaitFilePath(waitFilePath);

		waitFileJobList.add(waitFileJob);
		logger.info("WaitFilePath=[" + waitFilePath + "]");
	}

	/**
	 * 检验作业定义表中的作业根据小时、分钟的频率是否能够认为为需要马上执行的作业。
	 * @author Tiger.Wang
	 * @date 2019/9/2
	 * @param job	EtlJobDefBean对象，代表一个作业
	 * @return boolean	若认定为需要马上执行则返回true，否则false
	 */
	private boolean checkEtlDefJob(EtlJobDefBean job) {

		int exeNum = job.getExe_num();
		int exeedNum = job.getCom_exe_num();	//已经执行次数
		if( exeedNum >= exeNum ) {	//已执行的次数>=总执行测试，作业不再执行
			return false;
		}
		String endTime = job.getEnd_time();	//19位日期加时间字符串    yyyy-MM-dd HH:mm:ss
		LocalDateTime endDateTime = DateUtil.parseStr2DateTime(endTime);
		if(LocalDateTime.now().compareTo(endDateTime) > -1) {	//若当前系统日期大于或等于结束日期，则作业不再执行
			return false;
		}

		return true;
	}

	/**
	 * 检验作业调度表中的作业根据小时、分钟的频率是否能够认为为需要马上执行的作业。
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param exeJob	EtlJobBean对象，表示一个待调度（执行）的作业
	 * @return boolean	若认定为需要马上执行则返回true，否则false
	 */
	private boolean checkEtlJob(EtlJobBean exeJob) {

		EtlJobDefBean job = new EtlJobDefBean();
		job.setExe_num(exeJob.getExe_num());
		job.setCom_exe_num(exeJob.getCom_exe_num());
		job.setEnd_time(exeJob.getEnd_time());

		return checkEtlDefJob(job);
	}

	/**
	 * 根据调度频率类型、偏移量、跑批日期等判断调度日期是否要调度该作业
	 * @author Tiger.Wang
	 * @date 2019/9/3
	 * @param frequancy	作业调度频率类型
	 * @param nDispOffset	调度偏移量
	 * @param currDate	当前跑批日期
	 * @param exe_num	执行次数
	 * @param com_exe_num	已经执行次数
	 * @param star_time	开始执行时间（yyyy-MM-dd HH:mm:ss）
	 * @param end_time	结束执行时间（yyyy-MM-dd HH:mm:ss）
	 * @return boolean	是否要调度该作业
	 */
	private boolean checkDispFrequency(String frequancy, int nDispOffset, LocalDate currDate, int exe_num,
									   int com_exe_num, String star_time, String end_time) {

		//TODO 此处要用jdk8的LocalDate来进行日期计算，此处没仔细看
		ZoneId zoneId = ZoneId.systemDefault();
		ZonedDateTime zdt = currDate.atStartOfDay(zoneId);
		Calendar cal = Calendar.getInstance();
		cal.setTime(Date.from(zdt.toInstant()));
		//TODO 此处较原版改动：多个if改为if else if的形式
		/*
		 * 此处判断作业的调度频率类型，主要行为如下：
		 * 一、若该作业为每日调度，则该作业需要马上调度；
		 * 二、若该作业为每月、每周、每年调度，则根据偏移量计算该作业是否需要马上调度；
		 * 三、若该作业为频率调度，则根据该作业的开始执行时间计算该作业是否需要马上调度。
		 */
		if(frequancy.endsWith(Dispatch_Frequency.DAILY.getCode())) {
			return true;
		}else if(frequancy.equals(Dispatch_Frequency.MONTHLY.getCode())) {
			int x = cal.get(Calendar.DAY_OF_MONTH);
			if(nDispOffset < 0) {
				cal.add(Calendar.MONTH, 1);
			}
			cal.set(Calendar.DAY_OF_MONTH, nDispOffset + 1);
			int y = cal.get(Calendar.DAY_OF_MONTH);
			if(x == y) {
				return true;
			}
		}else if(frequancy.equals(Dispatch_Frequency.WEEKLY.getCode())) {
			int x = cal.get(Calendar.DAY_OF_WEEK);
			if(nDispOffset <= 0) {
				cal.add(Calendar.WEEK_OF_MONTH, 0);
			}
			cal.set(Calendar.DAY_OF_WEEK, nDispOffset + 1);
			int y = cal.get(Calendar.DAY_OF_WEEK);
			if(x == y) {
				return true;
			}
		}else if(frequancy.equals(Dispatch_Frequency.YEARLY.getCode())) {
			int x = cal.get(Calendar.DAY_OF_YEAR);
			if(nDispOffset < 0) {
				cal.add(Calendar.YEAR, 1);
			}
			cal.set(Calendar.DAY_OF_YEAR, nDispOffset + 1);
			int y = cal.get(Calendar.DAY_OF_YEAR);
			if(x == y) {
				return true;
			}
		}else if(frequancy.equals(Dispatch_Frequency.PinLv.getCode())) {	//xchao--2017年8月15日 16:25:51 添加按秒、分钟、小时进行执行
			if( com_exe_num < exe_num ) {
				LocalDateTime startDateTime = DateUtil.parseStr2DateTime(star_time);
				LocalDateTime endDateTime = DateUtil.parseStr2DateTime(end_time);
				LocalDateTime currDateTime = LocalDateTime.now();
				long currMilli = currDateTime.atZone(zoneId).toInstant().toEpochMilli();
				long startCurrMilli = startDateTime.atZone(zoneId).toInstant().toEpochMilli() - currMilli;
				long endCurrMilli = endDateTime.atZone(zoneId).toInstant().toEpochMilli() - currMilli;
				if( startCurrMilli <= 0 && endCurrMilli >= 0 ) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 判断作业是否已经完成
	 *
	 * @param currBathDate
	 * 			调度日期
	 * @param jobName
	 * 			作业名
	 * @return
	 * 		是否已经完成
	 */
	private boolean checkJobFinished(String currBathDate, String jobName) {

		// 判断调度日期是否存在，不存在返回true
		if(!jobExecuteMap.containsKey(currBathDate)) {
			return true;
		}
		// 判断调度日期的作业表中是否存在作业，不存在返回true
		Map<String, EtlJobBean> jobMap = jobExecuteMap.get(currBathDate);
		if(!jobMap.containsKey(jobName)) {
			return false;
		}
		// 判断作业状态是否为Done
		if(Job_Status.DONE.getCode().equals(jobMap.get(jobName).getJob_disp_status())) {
			return true;
		}

		return false;
	}
}