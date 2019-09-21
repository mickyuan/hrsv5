package hrds.control.task;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.control.beans.EtlJobBean;
import hrds.control.beans.EtlJobDefBean;
import hrds.control.beans.WaitFileJobInfo;
import hrds.control.constans.ControlConfigure;
import hrds.control.task.helper.*;
import hrds.control.utils.DateUtil;

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
	//TODO 这个名字叫什么我不知道，假如不能使用标识，那么使用LocalDateTime对象
	private static final String DEFAULT_DATETIME = "2000-12-31 23:59:59";
	//TODO 目的是解决按照频率，一天调度多次的问题，应该除掉该标识，和DEFAULT_DATETIME做一样的处理
	private static final long zclong = 999999999999999999L;
	public static final int MAXPRIORITY = 99;    //作业的最大优先级
	public static final int MINPRIORITY = 1;    //作业的最小优先级
	public static final int DEFAULT_PRIORITY = 5;  //默认作业优先级

	//FIXME 以下每个map的key和value都要详细说明
	//调度作业定义表
	private static final Map<String, EtlJobDefBean> jobDefineMap = new HashMap<>();
	//调度作业的时间依赖表
	private static final Map<String, String> jobTimeDependencyMap = new HashMap<>();
	//调度作业间关系依赖表
	private static final Map<String, List<String>> jobDependencyMap = new HashMap<>();
	//待调度作业表
	private static final Map<String, Map<String, EtlJobBean>> jobExecuteMap = new HashMap<>();
	//监视文件作业列表
	private static final List<WaitFileJobInfo> waitFileJobList = new ArrayList<>();
	//需要等待调度作业
	private static final List<EtlJobBean> jobWaitingList = new ArrayList<>();
	//系统资源表，例如：定义了资源A数量为20，意味着本工程下所有归属为A的作业，一共能同时启动20个进程
	private static final Map<String, Etl_resource> sysResourceMap = new HashMap<>();
	private static final RedisHelper REDIS = RedisHelper.getInstance();
	private static final NotifyMessageHelper NOTIFY = NotifyMessageHelper.getInstance();
	private final TaskJobHandleHelper handleHelper;

	private LocalDate bathDate;   //当前批次日期
	private final String etlSysCd;  //调度系统编号 //FIXME 改名：etlSysCode 并且说明值是来自什么。这些变量都要说明
	private final boolean isResumeRun;  //系统续跑标识
	private final boolean isAutoShift;  //自动日切标识
	private final boolean isNeedSendSMS;    //作业发送警告信息标识
	private static boolean isSysPause = false;  //系统是否暂停标识
	private static boolean isSysJobShift = false;   //系统日切干预标识
	private boolean sysRunning = false; //系统运行标识，每次启动时初始为false
	private CheckWaitFileThread thread; //用于检测作业类型为WF的线程

	private final static String RUNNINGJOBFLAG = "RunningJob";
	private final static String FINISHEDJOBFLAG = "FinishedJob";
	private final String strRunningJob;	//存入redis中的键值（标识需要马上执行的作业）
	private final String strFinishedJob;	//存入redis中的键值（标识已经停止的作业）

	private final static String REDISCONTENTSEPARATOR = "@";	//redis字符内容分隔符
	private final static String REDISHANDLE = "Handle"; //redis已干预标识
	public final static String PARASEPARATOR = ",";	//参数分隔符
	private final static long LOCKMILLISECONDS= 1000;    //系统暂停时间间隔

	private volatile boolean isLock = false;  //同步标志位
	private static final int SLEEPMILLIS = 3000; //每次运行时间间隔（毫秒数）

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
	public static TaskManager newInstance(String strSystemCode, LocalDate bathDate,
	                                      boolean isResumeRun, boolean isAutoShift) {
//FIXME 为什么要搞这么个方法，直接new不就完了吗
		return new TaskManager(strSystemCode, bathDate, isResumeRun, isAutoShift);
	}

	/**
	 * TaskManager类构造器，构造器私有化，使用newInstance方法获得静态实例，不允许外部构造。注意，此处会初始化系统资源。
	 * @author Tiger.Wang
	 * @date 2019/8/30
	 * @param bathDate	跑批批次日期
	 * @param etlSysCd	调度系统代码
	 * @param isResumeRun	是否续跑
	 * @param isAutoShift	是否自动日切
	 */
	private TaskManager(String etlSysCd, LocalDate bathDate, boolean isResumeRun, boolean isAutoShift) {

		this.etlSysCd = etlSysCd;
		this.bathDate = bathDate;
		this.isResumeRun = isResumeRun;
		this.isAutoShift = isAutoShift;

		this.strRunningJob = etlSysCd + RUNNINGJOBFLAG;
		this.strFinishedJob = etlSysCd + FINISHEDJOBFLAG;

		this.isNeedSendSMS = ControlConfigure.NotifyConfig.isNeedSendSMS;

		//FIXME 为什么这么搞？把未完成的this传给别人再返回给自己，这是搞什么？这个方法里面干的事情，拿出去按逻辑顺序调用
		this.handleHelper = TaskJobHandleHelper.newInstance(this);
	}

	/**
	 * 初始化系统资源。
	 * @note	1、清理redis中的数据；
	 * 			2、启动监控WaitFile线程；
	 * 			3、初始化系统资源，加载资源进内存Map。
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 */
	 public void initEtlSystem() {

		//1、清理redis中的数据。
		REDIS.deleteByKey(strRunningJob, strFinishedJob);
		//2、启动监控WaitFile线程。
		thread = new CheckWaitFileThread();
		thread.start();
		//3、初始化系统资源，加载资源进内存Map。
		List<Etl_resource> resources = TaskSqlHelper.getEtlSystemResources(etlSysCd);
		for(Etl_resource resource : resources) {
			String resourceType = resource.getResource_type();
			int maxCount = resource.getResource_max();
			int usedCount = 0;
			//FIXME 为什么要新搞一个，用已有的不行吗？
			// 而且，为什么需要这个循环，用 getEtlSystemResources 直接返回 sysResourceMap 不就行了吗
			Etl_resource newResource = new Etl_resource();
			newResource.setResource_type(resourceType);
			newResource.setResource_max(maxCount);
			newResource.setResource_used(usedCount);
			sysResourceMap.put(resourceType, newResource);
			logger.info("{}'s maxCount {}", resourceType, maxCount);
			logger.info("{}'s usedCount {}", resourceType, usedCount);
		}
	}

	/**
	 * 分析并加载需要立即启动的作业信息。注意，此方法会将虚作业发送到redis。<br>
	 * @note	1、清理内存Map（Map）：作业定义表（Map）、作业间关系依赖表（Map）、待调度作业表（Map）；<br>
	 * 			2、获取所有作业定义表（db）的作业信息；<br>
	 * 			3、分析并加载作业定义信息，将作业加载进作业定义表（Map）；<br>
	 * 			4、分析并加载作业依赖信息，将作业加载进调度作业间关系依赖表（Map）；<br>
	 * 			5、分析并加载作业信息，将作业加载进待调度作业表（Map）。<br>
	 * @return 该批次作业中，是否有按照频率调度的作业
	 * @author Tiger.Wang
	 * @date 2019/8/31
	 */
	public boolean loadReadyJob() {

		//1、清理内存：作业定义map、作业间关系依赖map、待调度作业map。
		jobDefineMap.clear();
		jobTimeDependencyMap.clear();
		jobDependencyMap.clear();
		//2、获取所有作业定义表的作业信息。
		List<EtlJobDefBean> jobs = TaskSqlHelper.getAllDefJob(etlSysCd);
		//3、分析并加载作业定义信息，将作业加载进作业定义表（内存Map）
		boolean hasFrequancy = loadJobDefine(jobs);
		//4、分析并加载作业依赖信息。
		loadJobDependency();
		//5、分析并加载作业信息，将作业加载进待调度作业表（内存Map）
		loadExecuteJob(jobs, hasFrequancy);

		return hasFrequancy;
	}

	/**
	 * 用于处理内存map中登记的作业。注意，此方法会将内存map中符合执行条件的作业全部发送到redis中。
	 * 该方法会持续运行，直到有系统干预日切或自动日切，该方法才会中断运行，若进行自动日切，则该方法会在内部
	 * 计算下一批次时间，并且使用该时间，你也可以选择使用TaskJobHelper类中的getNextBathDate计算出下一批次时间，
	 * 然后再次构造该类，再使用loadReadyJob方法和publishReadyJob方法进行自动日切，使程序继续执行。
	 * @note    1、定期去检查执行中的作业，防止通信异常时没有同步作业状态，时间间隔为[200*线程睡眠毫秒数];
	 *          2、检测干预信号，执行干预请求；
	 *          3、判断系统是否需要暂停；
	 *          4、对等待执行的作业按优先级排序；
	 *          5、判断每个待执行作业是否够资源启动，如果资源足够则登记到redis，否则提示作业优先度并继续等待；
	 *          6、将已经执行的作业从等待执行列表中移除；
	 *          7、检查当前跑批日期的作业是否已全部完成，根据日切标识决定该程序的生命是否要结束。
	 * @return 当前跑批已经结束，是否进行自动日切
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @param hasFrequancy  该批次作业中，是否有按照频率调度的作业
	 */
	public boolean publishReadyJob(boolean hasFrequancy) {
		//TODO 此处较原版改动：不再使用isSysShift，该变量含义不为系统干预日切，仅用作系统停止，
		// 不需要使用该标识，直接break或者return即可；
		//boolean isSysShift = false;	//系统干预日切标志
		int checkCount = 0;	//用于检查执行中的作业，防止通信异常时没有同步作业状态
		boolean handleErrorEtlJob = false;  //是否强制干预错误执行错误的作业

		while(true) {	//若未检测到系统干越日切，则会一直运行，除非运行过程中触发了结束条件
			//1、定期去检查执行中的作业，防止通信异常时没有同步作业状态，时间间隔为[200*线程睡眠毫秒数]
			if( checkCount == 200 ) {
				checkReadyJob();
				checkCount = 0;
			}
			checkCount++;
			//检查完成的作业并开启后续达成依赖的作业
			checkFinishedJob();
			//每次Sleep结束后查看是否有达到调度时间的作业
			checkTimeDependencyJob();
			//判断系统是否已经停止，可能不需要判断
			Etl_sys etlSys = TaskSqlHelper.getEltSysBySysCode(etlSysCd);
			if(Job_Status.STOP.getCode().equals(etlSys.getSys_run_status())) {
				sysRunning = false;
//				isSysShift = true;
				if(null != thread && thread.isAlive()) { thread.stopThread(); }
				logger.warn("------------- 系统干预，{} 调度停止 -----------------", etlSysCd);
				return false;
			}
			//2、检测干预信号，执行干预请求
			List<Etl_job_hand> handles = TaskSqlHelper.getEtlJobHands(etlSysCd);
			if(handles.size() != 0) {
				handleHelper.doHandle(handles);
				//执行过干预后查看是否有系统日切干预
				if(isSysJobShift) {
					bathDate = TaskJobHelper.getNextBathDate(bathDate);
					logger.info("{} 系统日切干预完成，下一批次为 {}", etlSysCd, bathDate);
					isSysJobShift = false;
					return true;
				}
			}
			//检查资源阀值是否有变动，更新sysResourceMap
			updateSysUsedResource();
			//3、判断系统是否需要暂停
			while(isLock) {
				System.out.println("Lock is true, Please wait.");
				try {
					Thread.sleep(LOCKMILLISECONDS);
				}
				catch(InterruptedException ignored) {}
			}
			//isLock设置为true又马上置为false的原因为：
			// 在这个时间段内，主线程某个代码段与CheckWaitFileThread线程中的某个代码段的运行互斥
			if(!isLock) isLock = true;
			//4、对等待执行的作业按优先级排序
			if(jobWaitingList.size() > 1) {
				//按照优先级排序
				logger.info("{} 作业等待队列中有带执行的作业，将进行排序", etlSysCd);
				Collections.sort(jobWaitingList);
			}
			//5、判断每个待执行作业是否够资源启动，如果资源足够则登记到redis，否则提示作业优先度并继续等待
			List<EtlJobBean> removeList = new ArrayList<>();
			for(EtlJobBean waitingJob : jobWaitingList) {
				String etlJob = waitingJob.getEtl_job();
				if(!Job_Status.WAITING.getCode().equals(waitingJob.getJob_disp_status())) {
					//被干预执行起来的作业
					removeList.add(waitingJob);
				}
				//判断系统资源是否足够
				if(checkJobResource(etlJob)) {
					//资源足够，作业执行，扣除资源
					decreaseResource(etlJob);
					waitingJob.setJob_disp_status(Job_Status.RUNNING.getCode());
					waitingJob.setJobStartTime(DateUtil.getNowDateTime2Milli());
					//更新调度作业状态为运行中
					TaskSqlHelper.updateEtlJobDispStatus(waitingJob.getJob_disp_status(), etlSysCd,
							etlJob, waitingJob.getCurr_bath_date());
					//将需要立即执行的作业登记到redis中
					String runningJob = etlJob + REDISCONTENTSEPARATOR + waitingJob.getCurr_bath_date();
					REDIS.rpush(strRunningJob, runningJob);

					removeList.add(waitingJob);
				}else {
					//资源不足够，优先度+1，最多+5，然后继续等待
					EtlJobDefBean etlJobDef = jobDefineMap.get(etlJob);
					if(etlJobDef != null && etlJobDef.getJob_priority() + DEFAULT_PRIORITY >
							waitingJob.getJob_priority_curr()) {

						waitingJob.setJob_priority_curr(waitingJob.getJob_priority_curr() + MINPRIORITY);

						TaskSqlHelper.updateEtlJobCurrPriority(waitingJob.getJob_priority_curr(), etlSysCd,
								waitingJob.getEtl_job(), waitingJob.getCurr_bath_date());
					}
				}
			}

			//6、将已经执行的作业从等待执行列表中移除
			for(Etl_job_cur tempJob : removeList) {
				jobWaitingList.remove(tempJob);
			}

			isLock = false;
			//判断等待执行列表的作业是否全部完成
			if(jobWaitingList.size() == 0) {

				/**
				 * xchao 2019年7月20日 11:37:49
				 * 如果没有等待的作业，判断作业是否有失败的作业，
				 * 如果有，强制性将错误的作业执行一次
				 * TODO 如果干预或历史干预中有，不在强制执行
				 * *******************************************开始
				 */
				if(!handleErrorEtlJob) {
					boolean isDoneError = checkAllJobFinishedORError(bathDate);
					logger.info("检查是不是需要干预，{}", isDoneError);
					if(isDoneError) {
						insertErrorJob2Handle(bathDate);
						handleErrorEtlJob = true;
					}
				}

				//7、检查当前跑批日期的作业是否已全部完成，根据日切标识决定该程序的生命是否要结束
				if(checkAllJobFinished(bathDate)) {

					/*xchao--一天调度多次的作业2017年8月15日 16:25:51 添加按秒、分钟、小时进行执行
					 * 	添加且jobFrequencyMap.size()==0才会退出
					 * 判断是否要自动日切
					 * */
					removeExecuteJobs(bathDate);

					if(!isAutoShift && !hasFrequancy) {
						//将系统的状态置为S
						TaskSqlHelper.updateEtlSysRunStatus(etlSysCd, Job_Status.STOP.getCode());

						logger.info("不需要做自动日切，退出！");
						thread.stopThread();
						return false;
					}else if(isAutoShift) {
						handleErrorEtlJob = false;
						/*xchao--一天调度多次的作业2017年8月15日 16:25:51 添加按秒、分钟、小时进行执行
						 *	如果需要日且，日期的时候不在查询作业为F类型的
						 * */
						if(hasFrequancy) {
							TaskSqlHelper.deleteEtlJobWithoutFrequency(etlSysCd,
									bathDate.format(DateUtil.DATE_DEFAULT), Job_Status.DONE.getCode());
						}else {
							TaskSqlHelper.deleteEtlJobByJobStatus(etlSysCd,
									bathDate.format(DateUtil.DATE_DEFAULT), Job_Status.DONE.getCode());
						}

						bathDate = TaskJobHelper.getNextBathDate(bathDate);
						logger.info("所有要执行的任务都为done，批量结束 {}，进行自动日切", bathDate);
						//TODO 此处较原版改动：原版这里为break，意味着当前调度进行自动日切。此处改为return，意味着外层
						// 需要判断该返回值，再进行初始化下一批次作业的操作
						return true;
					}
				}
			}
			try {
				Thread.sleep(SLEEPMILLIS);
				logger.info("还有任务未执行完");
			}
			catch(InterruptedException e) {
				logger.warn("系统出现异常：{}，但是继续执行", e.getMessage());
			}
		}
	}

//-----------------------------分析并加载需要立即启动的作业信息用（loadReadyJob方法）start--------------------------------
	/**
	 * 根据作业列表，判断每个作业触发方式、是否达成触发条件、资源是否足够、作业依赖等条件；<br>
	 * 此处会维护jobDefineMap全局变量，用于存放作业信息；<br>
	 * 此处会维护jobTimeDependencyMap全局变量，用于存放触发类型为频率的作业，key为作业标识，value为触发时间。<br>
	 * @note	1、判断每个作业是否需要立即执行；
	 *          2、为作业设置需要的资源。
	 * @author Tiger.Wang
	 * @date 2019/9/3
	 * @param jobs	某个工程内配置的所有作业集合
	 * @return boolean	是否有按频率调度的ETL调度频率类型作业
	 */
	private boolean loadJobDefine(List<EtlJobDefBean> jobs) {

		boolean hasFrequancy = false;

		for(EtlJobDefBean job : jobs) {
			//getEtl_job()方法获取任务名（任务标识）
			String etlJobId = job.getEtl_job();
			String etlDispType = job.getDisp_type();

			/*
			 * 1、判断每个作业是否需要立即执行:
			 * 		一、若作业为T+0、T+1调度方式，在以频率为频率类型的情况下，则要检查该作业是否到触发条件，
			 *          在不是以频率为频率类型的情况下，则认为该作业有时间依赖，并记录；
			 *		二、若作业为依赖调度方式，且在以频率为频率类型的情况下，则要检查该作业是否到触发条件。
			 */
			if(Dispatch_Type.TPLUS0.getCode().equals(etlDispType) ||
					Dispatch_Type.TPLUS1.getCode().equals(etlDispType)) {
				if(Dispatch_Frequency.PinLv.getCode().equals(job.getDisp_freq())) {
					//此处较原版改动：原版是getFJob(tempJob.getStrEtlJob(), strSystemCode, "def")，
					//在该方法中不再queryIsAllDoneDef，因为job变量本身就代表着job_def的作业信息，没必要再次查询
					if(checkEtlDefJob(job) && !hasFrequancy) hasFrequancy = true;
				}
				//如果作业的调度触发方式为T+0、T+1定时触发时，将记录作业触发时间
				jobTimeDependencyMap.put(etlJobId, job.getDisp_time());
			}else if(Dispatch_Type.DEPENDENCE.getCode().equals(etlDispType)) {
				//如果作业的调度触发方式为依赖触发，且以频率调度，则要检查该作业是否到触发条件，TODO 此处好像没有这么简单
				if(Dispatch_Frequency.PinLv.getCode().equals(job.getDisp_freq())) {
					if(checkEtlDefJob(job) && !hasFrequancy) hasFrequancy = true;
				}
			}

			//2、为作业设置需要的资源。
			List<Etl_job_resource_rela> jobNeedResources = TaskSqlHelper.getJobNeedResources(etlSysCd, etlJobId);
			job.setJobResources(jobNeedResources);

			jobDefineMap.put(etlJobId, job);
		}

		return hasFrequancy;
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

		List<Etl_dependency> etlDependencies = TaskSqlHelper.getJobDependencyBySysCode(etlSysCd);
		for(Etl_dependency etlDependency : etlDependencies) {
			String etlJobId = etlDependency.getEtl_job();
			//1、判断每个依赖作业是否在作业定义表中，若该依赖作业不在调度范围内，则认为该作业不作为依赖作业
			if(!jobDefineMap.containsKey(etlJobId)) {
				continue;
			}
			//TODO 此处要做什么
			if(etlJobId.equals("EDW_TRAN_PDATA_T09_OB_DIM_INFO_H_S28")) {
				etlJobId = "EDW_TRAN_PDATA_T09_OB_DIM_INFO_H_S28";
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
	 * 加载需要执行的作业，分析并识别作业是否需要立即调度，如果需要立即调度，则将该作业加载进待调度作业表（内存Map）。
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
		 * 		（三）、检查并计算出作业定义信息中，达到执行条件的作业，将该作业登记到内存Map及etl_job表；
		 * 		（四）、更新作业调度表中的作业调度状态；
		 * 		（五）、检查并登记作业到作业依赖表；
		 * 		（六）、检查并登记作业到作业等待表。
		 * 二、若系统不在运行，调度系统以续跑方式启动。主要行为如下：
		 * 		（一）、更新该批次作业运行状态；
		 * 		（二）、更新该批次作业调度状态；
		 * 		（三）、加载符合运行条件作业进待调度作业表（内存Map）；
		 * 		（四）、清空该批次作业的已使用资源。
		 * 三、若系统不在运行，调度系统不以续跑方式启动。主要行为如下：
		 * 		（一）、更新该系统的跑批日期及系统运行状态。在干预日切及自动日切的情况下，批量日期会增加；
		 * 		（二）、清理掉已登记的作业；
		 * 		（三）、计算当前调度日期可以执行的作业；
		 * 		（四）、清空该批次作业的已使用资源；
		 * 		（五）、更新作业的调度状态。
		 */
		if(sysRunning) {	//如果系统在运行中
			//修改ETL_SYS的[批量日期]为日切后的的批量日期
			TaskSqlHelper.updateEtlSysBathDate(etlSysCd, strBathDate);
			//清理ETL_JOB，范围限定为：该系统、该批量日期、非作业类型为T+0且按频率调度的作业。
			TaskSqlHelper.deleteEtlJobByBathDate(etlSysCd, strBathDate);
			//计算当前调度日期可以执行的作业
			loadCanDoJobWithNoResume(jobs, hasFrequancy);
			//将作业的状态都置为Pending
			TaskSqlHelper.updateEtjJobWithDispStatus(Job_Status.PENDING.getCode(), etlSysCd, strBathDate);
			//检查作业的依赖
			checkJobDependency(strBathDate);
			//初始化需要加入到等待列表的作业（内存Map）
			initWaitingJob(strBathDate);
		}else {	//若系统不在运行
			if(isResumeRun) {	//调度系统需要续跑
				//修改ETL_SYS该系统的 [状态] 为运行中
				TaskSqlHelper.updateEtlSysRunStatus(etlSysCd, Job_Status.RUNNING.getCode());
				//修改ETL_JOB表 非PENDING/DONE的 [作业状态] 为PENDING，范围限定为：该系统、该批量日期、且当日调度的作业。
				TaskSqlHelper.updateEtjJobByResumeRun(etlSysCd, strBathDate);
				//取得ETL_JOB表中当前调度日期前已经存在的作业
				loadExecuteJobWithRunning(etlSysCd, strBathDate);
				//将资源表都清空，参数：0的含义为清空该工程下的已使用资源
				TaskSqlHelper.updateEtlResourceUsed(etlSysCd, 0);
			}else {	//调度系统不需要续跑
				//修改ETL_SYS该系统的 [状态] 为运行中，并且登记当前跑批日期
				TaskSqlHelper.updateEtlSysRunStatusAndBathDate(etlSysCd, strBathDate, Job_Status.RUNNING.getCode());
				//清理ETL_JOB，因为在系统第一次运行，且不是续跑的情况下，需要清理掉已登记的作业。
				TaskSqlHelper.deleteEtlJobBySysCode(etlSysCd);
				//计算当前调度日期可以执行的作业
				loadCanDoJobWithNoResume(jobs, hasFrequancy);
				//将资源表都清空，参数：0的含义为清空该工程下的已使用资源
				TaskSqlHelper.updateEtlResourceUsed(etlSysCd, 0);
				//将作业的状态都置为Pending
				TaskSqlHelper.updateEtjJobWithDispStatus(Job_Status.PENDING.getCode(), etlSysCd, strBathDate);
			}

			//调度系统初期化时，将所有作业的依赖关系都初期化
			checkJobDependency("");
			initWaitingJob("");
			sysRunning = true;	//调度系统在运行完一次后，必然进入运行中的状态
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
				TaskJobHelper.transformProgramDir(job);	//替换作业程序目录
				TaskJobHelper.transformLogDir(job);		//替换作业日志目录
				TaskJobHelper.transformProName(job);	//替换作业程序名称
				TaskJobHelper.transformProPara(job);	//替换作业程序参数
				//TODO 此处按照原版写，原版没有在这写逻辑
				if(hasFrequancy && Dispatch_Frequency.PinLv.getCode().equals(strDispFreq)) {

				}else {
					Etl_job_cur etlJob = new Etl_job_cur();
					try {
						BeanUtils.copyProperties(etlJob, job);
					} catch (IllegalAccessException | InvocationTargetException e) {
						throw new AppSystemException("将Etl_job_def转换为Etl_job发生异常：" + e.getMessage());
					}
					TaskSqlHelper.insertIntoJobTable(etlJob);
				}
				job.setToday_disp(Today_Dispatch_Flag.YES.getCode());
				//计算调度作业的下一批次作业日期
				executeJob.setStrNextDate(TaskJobHelper.getNextExecuteDate(bathDate, strDispFreq));
				//FIXME 这个表主键是两个，这里用其中一个做key，要说明为什么可行。
				// 另外：
				// getEtl_job 要把表里该字段名字改为 etl_job_id，且该表的字段前缀etl是否可以删除。
				// 两个后缀是sys_cd的字段，改名字为sys_code
				executeJobMap.put(executeJob.getEtl_job(), executeJob);
			}else {
				job.setToday_disp(Today_Dispatch_Flag.NO.getCode());
			}
		}
		//TODO 有问题，为什么空的executeJobMap也要登记。（还未到调度时间的作业不应该登记）
		//3、将作业登记到jobExecuteMap内存中
		jobExecuteMap.put(strBathDate, executeJobMap);
	}

	/**
	 * 加载能马上运行的作业。注意，此处会维护jobExecuteMap全局变量，此方法仅在调度服务在“续跑”状态下使用。
	 *
	 * @author Tiger.Wang
	 * @date 2019/9/4
	 * @param strSystemCode	调度系统编号
	 * @param strBathDate	跑批日期（yyyy-MM-dd）
	 */
	private void loadExecuteJobWithRunning(String strSystemCode, String strBathDate) {

		List<Etl_job_cur> currentJobs= TaskSqlHelper.getEtlJobs(strSystemCode, strBathDate);
		for(Etl_job_cur job : currentJobs) {
			EtlJobBean executeJob = new EtlJobBean();
			executeJob.setEtl_job(job.getEtl_job());
			executeJob.setJob_disp_status(job.getJob_disp_status());
			executeJob.setCurr_bath_date(job.getCurr_bath_date());
			executeJob.setJob_priority_curr(job.getJob_priority_curr());
			executeJob.setPro_type(job.getPro_type());
			executeJob.setExe_num(job.getExe_num());
			executeJob.setCom_exe_num(job.getCom_exe_num());
			executeJob.setEnd_time(job.getEnd_time());
			String strCurrBathDate = job.getCurr_bath_date();
			//计算的执行作业的下一批次作业日期
			LocalDate currBathDate = LocalDate.parse(strCurrBathDate, DateUtil.DATE);
			executeJob.setStrNextDate(TaskJobHelper.getNextExecuteDate(currBathDate, job.getDisp_freq()));
			//将执行作业加入执行作业表
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

		for(String strBathDate : jobExecuteMap.keySet()) {
			//如果调度系统日切时，只检查当前调度日期作业的依赖作业
			if(!strCurrBathDate.isEmpty() && !strCurrBathDate.equals(strBathDate)) {
				continue;
			}

			LocalDate currBathDate = LocalDate.parse(strBathDate, DateUtil.DATE_DEFAULT);
			Map<String, EtlJobBean> jobMap = jobExecuteMap.get(strBathDate);

			for(String strJobName : jobMap.keySet()) {
				//取得作业的定义
				Etl_job_def jobDefine = jobDefineMap.get(strJobName);
				if(null == jobDefine) {
					continue;
				}
				//取得执行作业
				EtlJobBean job = jobMap.get(strJobName);

				//1.判断前一批次作业是否已经完成
				String strPreDate = TaskJobHelper.getPreExecuteDate(currBathDate, jobDefine.getDisp_freq());
				job.setPreDateFlag(checkJobFinished(strPreDate, strJobName));

				String dispType = jobDefine.getDisp_type();
				/*
				 * 判断执行作业的调度触发方式。
				 * 一、若作业调度方式为依赖触发，则检查jobDependencyMap内存Map中是否有该作业相关依赖，并设置依赖调度标识；
				 * 二、若作业调度方式为定时T+1触发，则计算出执行日期时间，并设置依赖调度标识；
				 * 三、若作业调度方式为定时T+0触发，则计算出执行日期时间，并设置依赖调度标识；
				 */
				if(Dispatch_Type.DEPENDENCE.getCode().equals(dispType)) {    //依赖触发
					if(Dispatch_Frequency.PinLv.getCode().equals(jobDefine.getDisp_freq())) {
						job.setExecuteTime(zclong);
						job.setDependencyFlag(false);
					}else {
						//取得依赖作业列表
						List<String> dependencyJobList = jobDependencyMap.get(strJobName);
						if(dependencyJobList == null) {
							//依赖作业列表没有，可以直接调度
							job.setDependencyFlag(true);
						}else {
							//判断已经完成的依赖作业个数
							int finishedDepJobCount = 0;
							for(String s : dependencyJobList) {
								if(checkJobFinished(strBathDate, s)) {
									++finishedDepJobCount;
								}
							}
							job.setDoneDependencyJobCount(finishedDepJobCount);
							//判断依赖的作业是否已经全部完成
							if(finishedDepJobCount == dependencyJobList.size()) {
								//已经全部完成，可以准备调度此作业
								job.setDependencyFlag(true);
							}else {
								job.setDependencyFlag(false);
							}
						}
						job.setExecuteTime(0L);
					}
				}else if(Dispatch_Type.TPLUS1.getCode().equals(dispType)) {
					if(Dispatch_Frequency.PinLv.getCode().equals(jobDefine.getDisp_freq())) {
						job.setExecuteTime(zclong);
						job.setDependencyFlag(false);
					}else {
						//定时T+1触发
						String strDispTime = jobTimeDependencyMap.get(strJobName);
						if (null == strDispTime) {
							job.setExecuteTime(
									currBathDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
						}else {
							job.setExecuteTime(TaskJobHelper.getExecuteTimeByTPlus1(
									strBathDate + " " + strDispTime).atZone(ZoneId.systemDefault()).
									toInstant().toEpochMilli());
						}
						job.setDependencyFlag(false);
					}
				}else if(Dispatch_Type.TPLUS0.getCode().equals(dispType)) {
					if(Dispatch_Frequency.PinLv.getCode().equals(jobDefine.getDisp_freq())) {
						job.setExecuteTime(zclong);
						job.setDependencyFlag(false);
					}else {
						//定时准点触发
						String strDispTime = jobTimeDependencyMap.get(strJobName);
						if (null == strDispTime) {
							job.setExecuteTime(currBathDate.atStartOfDay(
									ZoneId.systemDefault()).toInstant().toEpochMilli());
						}else {
							job.setExecuteTime(LocalDateTime.parse(strBathDate + " " + strDispTime,
									DateUtil.DATETIME).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
						}
						job.setDependencyFlag(false);
					}
				}else {
					//其他触发方式，暂不支持
					throw new AppSystemException("目前仅支持的调度类型：" +
							Dispatch_Type.DEPENDENCE.getValue() + " " +
							Dispatch_Type.TPLUS1.getValue() + " " +
							Dispatch_Type.TPLUS0.getValue());
				}

				logger.info("{}'s executeTime={}", strJobName, job.getExecuteTime());
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

		for(String strBathDate : jobExecuteMap.keySet()) {

			if(!strCurrBathDate.isEmpty() && !strCurrBathDate.equals(strBathDate)) {
				continue;
			}
			Map<String, EtlJobBean> jobMap = jobExecuteMap.get(strBathDate);
			for(String strJobName : jobMap.keySet()) {
				EtlJobBean exeJob = jobMap.get(strJobName);
				String etlJob = exeJob.getEtl_job();
				String currBathDate = exeJob.getCurr_bath_date();
				Etl_job_def jobDefine = jobDefineMap.get(strJobName);
				if(null == jobDefine) {
					continue;
				}
				//判断执行作业的作业状态
				if(Job_Status.PENDING.getCode().equals(exeJob.getJob_disp_status())) {
					//作业状态为Pending,检查前置作业是否已完成
					//判断前一批次作业是否已经完成
					if(!exeJob.isPreDateFlag()) {
						//前一批次作业未完成，作业状态不能置为Waiting
						continue;
					}
					String dispType = jobDefine.getDisp_type();
					//判断作业调度触发方式是否满足
					if(Dispatch_Type.DEPENDENCE.getCode().equals(dispType)) {
						//依赖触发,判断依赖作业是否已经完成
						if(!exeJob.isDependencyFlag()) {
							//依赖作业未完成，作业状态不能置为Waiting
							continue;
						}
					}else if(Dispatch_Type.TPLUS1.getCode().equals(dispType) ||
							Dispatch_Type.TPLUS0.getCode().equals(dispType)) {
						//定时触发,判断作业调度时间是否已经达到
						if(LocalDateTime.now().compareTo(
								DateUtil.timestamp2DateTime(exeJob.getExecuteTime())) < 0) {
							//作业调度时间未到,作业状态不能置为Waiting
							continue;
						}
					}else{
						//其他触发方式，暂不支持
						throw new AppSystemException("目前仅支持的调度类型：" +
								Dispatch_Type.DEPENDENCE.getValue() + " " +
								Dispatch_Type.TPLUS1.getValue() + " " +
								Dispatch_Type.TPLUS0.getValue());
					}

					//xchao--2017年8月15日 16:25:51 添加按秒、分钟、小时进行执行
					if(Dispatch_Frequency.PinLv.getCode().equals(jobDefine.getDisp_freq()) &&
							!checkEtlJob(exeJob)) {
						continue;
					}

					//判断作业的作业有效标志
					if(Job_Effective_Flag.VIRTUAL.getCode().equals(jobDefine.getJob_eff_flag())) {
						//如果是虚作业的话，直接将作业状态置为Done
						exeJob.setJob_disp_status(Job_Status.RUNNING.getCode());
						handleVirtualJob(etlJob, currBathDate);
						continue;
					}else{
						//将Pending状态置为Waiting
						exeJob.setJob_disp_status(Job_Status.WAITING.getCode());
						TaskSqlHelper.updateEtlJobDispStatus(Job_Status.WAITING.getCode(), etlSysCd, etlJob,
								currBathDate);
					}

				}else if(!Job_Status.WAITING.getCode().equals(exeJob.getJob_disp_status())) {
					//Pending和Waiting状态外的作业暂不处理
					continue;
				}

				if(Pro_Type.WF.getCode().equals(exeJob.getPro_type())) {
					addWaitFileJobToList(exeJob);
				}else{
					jobWaitingList.add(exeJob);
				}
			}
		}
	}

	/**
	 * 将ETL作业类型为"WF"的作业，加入waitFileJobList内存Map中
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param exeJob	作业对象
	 */
	private void addWaitFileJobToList(EtlJobBean exeJob) {

		Etl_job_cur etlJob = TaskSqlHelper.getEtlJob(etlSysCd, exeJob.getEtl_job());

		String strEtlJob = exeJob.getEtl_job();
		String currBathDate = exeJob.getCurr_bath_date();

		//更新作业的状态到Running
		TaskSqlHelper.updateEtlJobDispStatus(Job_Status.RUNNING.getCode(), etlSysCd, strEtlJob, currBathDate);

		//添加作业执行时间
		etlJob.setCurr_st_time(fd.ng.core.utils.DateUtil.getDateTime(DateUtil.DATETIME));
		TaskSqlHelper.updateEtlJobRunTime(etlJob.getCurr_st_time(), etlSysCd, strEtlJob);

		WaitFileJobInfo waitFileJob = new WaitFileJobInfo();
		waitFileJob.setStrJobName(strEtlJob);
		waitFileJob.setStrBathDate(currBathDate);
		String waitFilePath = exeJob.getPro_dic() + exeJob.getPro_name();
		waitFileJob.setWaitFilePath(waitFilePath);

		waitFileJobList.add(waitFileJob);
		logger.info("WaitFilePath=[{}]", waitFilePath);
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
		if( exeedNum >= exeNum ) {	//已执行的次数>=总执行次数，作业不再执行
			return false;
		}
		String endTime = job.getEnd_time();	//19位日期加时间字符串    yyyy-MM-dd HH:mm:ss
		LocalDateTime endDateTime = LocalDateTime.parse(endTime, DateUtil.DATETIME);
		//若当前系统日期大于或等于结束日期，则作业不再执行
		return LocalDateTime.now().compareTo(endDateTime) < 0;
	}

	/**
	 * 检验作业调度表中的作业根据小时、分钟的频率是否能够认为为需要马上执行的作业。
	 * @author Tiger.Wang
	 * @date 2019/9/5
	 * @param exeJob	EtlJobBean对象，表示一个待调度（执行）的作业
	 * @return boolean	若认定为需要马上执行则返回true，否则false
	 */
	private boolean checkEtlJob(EtlJobBean exeJob) {

		Etl_job_cur etlJob = TaskSqlHelper.getEtlJob(exeJob.getEtl_sys_cd(), exeJob.getEtl_job());;
		EtlJobDefBean job = new EtlJobDefBean();
		job.setExe_num(etlJob.getExe_num());
		job.setCom_exe_num(etlJob.getCom_exe_num());
		job.setEnd_time(etlJob.getEnd_time());

		if(!checkEtlDefJob(job)) { return false; }

		LocalDateTime currDateTime = LocalDateTime.now();
		LocalDateTime nextExeDateTime = DateUtil.timestamp2DateTime(Long.parseLong(job.getLast_exe_time())
				+ (job.getExe_frequency() * 1000));
		//当前时间<=下一次执行时间，作业不执行
		return currDateTime.compareTo(nextExeDateTime) > 0;
	}

	/**
	 * 根据调度频率类型、偏移量、跑批日期等判断调度日期是否要调度该作业。
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

		//TODO 因为jdk8的withDayOfMonth等方法不支持nDispOffset为负数，
		// 也没找到解决办法，暂无法换成jdk8新日期时间的使用方法
		//FIXME 这是要达到什么目的？原版是怎么做的？
		ZoneId zoneId = ZoneId.systemDefault();
		ZonedDateTime zdt = currDate.atStartOfDay(zoneId);
		Calendar cal = Calendar.getInstance();
		cal.setTime(Date.from(zdt.toInstant()));
		/*
		 * 此处判断作业的调度频率类型，主要行为如下：
		 * 一、若该作业为每日调度，则该作业需要马上调度；
		 * 二、若该作业为每月、每周、每年调度，则根据偏移量计算该作业是否需要马上调度；
		 * 三、若该作业为频率调度，则根据该作业的开始执行时间计算该作业是否需要马上调度。
		 */
		//FIXME 下面每段if都要注释说明在干什么
		if(frequancy.endsWith(Dispatch_Frequency.DAILY.getCode())) {
			return true;
		}else if(frequancy.equals(Dispatch_Frequency.MONTHLY.getCode())) {
			int x = cal.get(Calendar.DAY_OF_MONTH);
			if(nDispOffset < 0) {
				cal.add(Calendar.MONTH, 1);
			}
			cal.set(Calendar.DAY_OF_MONTH, nDispOffset + 1);
			int y = cal.get(Calendar.DAY_OF_MONTH);
			return x == y;
		}else if(frequancy.equals(Dispatch_Frequency.WEEKLY.getCode())) {
			int x = cal.get(Calendar.DAY_OF_WEEK);
			if(nDispOffset <= 0) {
				cal.add(Calendar.WEEK_OF_MONTH, 0);
			}
			cal.set(Calendar.DAY_OF_WEEK, nDispOffset + 1);
			int y = cal.get(Calendar.DAY_OF_WEEK);
			return x == y;
		}else if(frequancy.equals(Dispatch_Frequency.YEARLY.getCode())) {
			int x = cal.get(Calendar.DAY_OF_YEAR);
			if(nDispOffset < 0) {
				cal.add(Calendar.YEAR, 1);
			}
			cal.set(Calendar.DAY_OF_YEAR, nDispOffset + 1);
			int y = cal.get(Calendar.DAY_OF_YEAR);
			return x == y;
		}else if(frequancy.equals(Dispatch_Frequency.PinLv.getCode())) {
			//xchao--2017年8月15日 16:25:51 添加按秒、分钟、小时进行执行
			if( com_exe_num < exe_num ) {
				LocalDateTime startDateTime = DateUtil.parseStr2DateTime(star_time);
				LocalDateTime endDateTime = DateUtil.parseStr2DateTime(end_time);
				LocalDateTime currDateTime = LocalDateTime.now();
				long currMilli = currDateTime.atZone(zoneId).toInstant().toEpochMilli();
				long startCurrMilli = startDateTime.atZone(zoneId).toInstant().toEpochMilli() - currMilli;
				long endCurrMilli = endDateTime.atZone(zoneId).toInstant().toEpochMilli() - currMilli;
				return startCurrMilli <= 0 && endCurrMilli >= 0;
			}
		}

		return false;
	}

	/**
	 * 判断作业是否已经完成
	 *
	 * @param currBathDate  调度日期
	 * @param jobName   作业名
	 * @return  是否已经完成
	 */
	private boolean checkJobFinished(String currBathDate, String jobName) {

		//判断调度日期是否存在，不存在返回true
		if(!jobExecuteMap.containsKey(currBathDate)) {
			return true;
		}
		//判断调度日期的作业表中是否存在作业，不存在返回true
		Map<String, EtlJobBean> jobMap = jobExecuteMap.get(currBathDate);
		if(!jobMap.containsKey(jobName)) {
			return false;
		}
		//判断作业状态是否为Done
		return Job_Status.DONE.getCode().equals(jobMap.get(jobName).getJob_disp_status());
	}

	/**
	 * 用于对作业类型为WF的作业，进行作业完成的操作。
	 * @note 1、验证当前作业是否已经登记到，表示执行的数据库表中，若已登记则获取该作业信息；
	 *       2、将Etl_job_cur对象复制成Etl_job_disp_his对象；
	 *       3、使用Etl_job_disp_his对象更新etl_job_disp_his表；
	 *       4、使用Etl_job_cur对象更新etl_job_cur表；
	 *       5、根据调度作业标识、当前跑批日期，更新已经结束的作业信息。
	 * @author Tiger.Wang
	 * @date 2019/9/16
	 * @param waitJobInfo   WaitFileJobInfo对象，标识一个作业类型为WF的作业
	 */
	private void waitFileJobFinished(WaitFileJobInfo waitJobInfo) {
		//1、验证当前作业是否已经登记到，表示执行的数据库表中，若已登记则获取该作业信息；
		Etl_job_cur etlJobCur = TaskSqlHelper.getEtlJob(etlSysCd, waitJobInfo.getStrJobName(),
				waitJobInfo.getStrBathDate());
		etlJobCur.setJob_disp_status(Job_Status.DONE.getCode());
		etlJobCur.setJob_return_val(0);
		//FIXME curr_end_time为什么是毫秒？ 为什么不用fd里面的DataUtil？
		etlJobCur.setCurr_end_time(String.valueOf(DateUtil.getNowDateTime2Milli()));
		//2、将Etl_job_cur对象复制成Etl_job_disp_his对象；
		Etl_job_disp_his etlJobDispHis = new Etl_job_disp_his();
		try {
			//FIXME 死循环里，不允许用这种耗时操作。用set进行逐个明确赋值！另外，为什么要赋值到his对象？
			BeanUtils.copyProperties(etlJobDispHis, etlJobCur);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new AppSystemException("将Etl_job_def转换为Etl_job发生异常：" + e.getMessage());
		}
		//3、使用Etl_job_disp_his对象更新etl_job_disp_his表；
		TaskSqlHelper.insertIntoEtlJobDispHis(etlJobDispHis); //FIXME 为什么不用etlJobCur。这两个对象明明一样
		//4、使用Etl_job_cur对象更新etl_job_cur表；
		TaskSqlHelper.updateEtlJobFinished(etlJobCur);
		//5、根据调度作业标识、当前跑批日期，更新已经结束的作业信息。
		updateFinishedJob(waitJobInfo.getStrJobName(), waitJobInfo.getStrBathDate());

		logger.info("{} 作业正常结束", etlJobCur.getEtl_job());
	}
//------------------------------分析并加载需要立即启动的作业信息用（loadReadyJob方法）end---------------------------------

//-------------------------------分析并处理需要立即启动的作业（publishReadyJob方法）start----------------------------------
	/**
	 * 检查调度中作业的状态，防止通信异常时没有同步作业状态。注意，此处主要是根据[待调度作业表（内存Map）]
	 * 与作业表的作业调度状态来推送该作业到redis（登记作业信息）
	 * @author Tiger.Wang
	 * @date 2019/9/6
	 */
	private void checkReadyJob() {

		for(String strBathDate : jobExecuteMap.keySet()) {
			Map<String, EtlJobBean> jobMap = jobExecuteMap.get(strBathDate);

			for(String strJobName : jobMap.keySet()) {
				EtlJobBean job = jobMap.get(strJobName);    //取得执行作业
				//如果是等待文件作业的话不监视
				if(Pro_Type.WF.getCode().equals(job.getPro_type())) {
					continue;
				}
				/*
				 * 此处检查作业状态为运行中的作业，主要行为如下：
				 * 一、内存Map的作业在作业表中无法查询出，则跳过检查；
				 * 二、作业状态为结束、错误的作业，则认为该作业已经结束，并登记到redis中；
				 * 三、除作业状态为结束、错误的作业，则当该作业开始执行时间是默认时间时（2000-12-31 23:59:59），
				 *     若该作业超过10分钟还未运行，则再次登记到redis中。
				 */
				if(Job_Status.RUNNING.getCode().equals(job.getJob_disp_status())) {
					//如果该执行作业状态是R的话，从DB中取得该作业信息
					String etlJob = job.getEtl_job();
					String currBathDate = job.getCurr_bath_date();
					//检查作业状态，如果作业已经完成，将作业加入redisDB
					Etl_job_cur jobInfo = TaskSqlHelper.getEtlJob(etlSysCd, job.getEtl_job(),
							job.getCurr_bath_date());
					String jobStatus = jobInfo.getJob_disp_status();
					if(Job_Status.DONE.getCode().equals(jobStatus) ||
							Job_Status.ERROR.getCode().equals(jobStatus)) {
						logger.warn("{} 检测到执行完成", etlJob);
						String finishedJob = etlJob + REDISCONTENTSEPARATOR + currBathDate;
						REDIS.rpush(strFinishedJob, finishedJob);
						continue;
					}
					//检查作业开始时间（yyyy-MM-dd HH:mm:ss）
					LocalDateTime currStTime = LocalDateTime.parse(jobInfo.getCurr_st_time(), DateUtil.DATETIME);
					LocalDateTime localDateTime = LocalDateTime.parse(DEFAULT_DATETIME, DateUtil.DATETIME);
					//如果作业开始时间还是默认时间(2000-12-31 23:59:59)，代表作业还没有被开始处理
					if(currStTime.equals(localDateTime)) {
						//TODO 此处是10分钟？
						//判断作业调度开始时间是否超过10分钟，超过的话将会被重新调度
						if(DateUtil.getNowDateTime2Milli() - job.getJobStartTime() > 120000) {
							logger.warn("{} 被再次执行", etlJob);
							String runningJob = etlJob + REDISCONTENTSEPARATOR + currBathDate;
							REDIS.rpush(strRunningJob, runningJob);
						}
					}
				}
			}
		}
	}

	/**
	 * 从redis中获取已经完成的作业，同步及更新这些作业的状态。注意，此处会从redis中获取数据，以及更新作业状态到作业表中。
	 * @author Tiger.Wang
	 * @date 2019/9/6
	 */
	private void checkFinishedJob() {
		//TODO 把redis的数据改成有含义的数据组织方式，考虑存map数据，取的时候转bean
		//判断是否有新的作业完成
		long finishedListSize = REDIS.llen(strFinishedJob);
		for(int i = 0; i < finishedListSize; ++i) {

			String finishJobString = REDIS.lpop(strFinishedJob);
			String[] jobKey = finishJobString.split(REDISCONTENTSEPARATOR);
			if( jobKey.length != 2 ) {
				continue;
			}
			//更新作业状态
			updateFinishedJob(jobKey[0], jobKey[1]);
		}
	}

	/**
	 * 根据调度作业标识、当前跑批日期，更新已经结束的作业信息。
	 * 注意，此处会更新作业状态，也会将要执行的作业加入到jobWaitingList内存Map中。
	 * @note    1、检查该作业是否存在于数据库中以及内存Map中；
	 *          2、释放该作业的资源；
	 *          3、更新内存Map（jobExecuteMap）中作业的作业状态，将作业的状态设为现在的作业状态；
	 *          4、若当前作业是完成状态，则修改依赖于此作业（更新内存MapjobExecuteMap）的状态；
	 *          5、若当前作业是完成状态，则根据该作业的下一批次执行时间设置此作业；
	 *          6、如果作业不是正常完成，发送警告消息（短信息）。
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @param jobName   调度作业标识
	 * @param currBathDate  当前跑批日期
	 */
	private void updateFinishedJob(String jobName, String currBathDate) {
		//1、检查该作业是否存在于数据库中以及内存map中；
		Etl_job_cur jobInfo = TaskSqlHelper.getEtlJob(etlSysCd, jobName, currBathDate);
		if(!jobExecuteMap.containsKey(currBathDate)) {
			throw new AppSystemException("无法在数据库中找到作业" + jobName);
		}

		EtlJobBean exeJobInfo = jobExecuteMap.get(currBathDate).get(jobName);
		if(null == exeJobInfo) {
			throw new AppSystemException("无法在内存表jobExecuteMap中找到作业" + jobName);
		}
		//如果该作业状态已经是完成状态时，跳过
		if(Job_Status.DONE.getCode().equals(exeJobInfo.getJob_disp_status())) {
			return;
		}

		Etl_job_def finishedJobDefine = jobDefineMap.get(jobName);
		//2、释放该作业的资源
		if(!Pro_Type.WF.getCode().equals(exeJobInfo.getPro_type()) &&
				Job_Status.RUNNING.getCode().equals(exeJobInfo.getJob_disp_status()) &&
				!Job_Effective_Flag.VIRTUAL.getCode().equals(finishedJobDefine.getJob_eff_flag())) {
			//释放资源
			increaseResource(jobName);
		}
		//3、更新内存Map（jobExecuteMap）中作业的作业状态，将作业的状态设为现在的作业状态
		String jobStatus = jobInfo.getJob_disp_status();
		logger.info("{} {} 作业完成！作业状态为 {}", jobName, currBathDate, jobStatus);
		exeJobInfo.setJob_disp_status(jobStatus);

		//依赖作业已经完成个数、依赖作业标志、作业状态
		if(Job_Status.DONE.getCode().equals(jobStatus)) {
			//4、若当前作业是完成状态，则修改依赖于此作业（更新内存MapjobExecuteMap）的状态；
			for (String strJobName : jobDependencyMap.keySet()) {
				//取得依赖于此作业的作业信息
				List<String> depJobList = jobDependencyMap.get(strJobName);
				if (depJobList.contains(exeJobInfo.getEtl_job())) {
					EtlJobBean nextJobInfo = jobExecuteMap.get(currBathDate).get(strJobName);
					if (null == nextJobInfo) {
						continue;
					}
					//修改依赖作业已经完成的个数
					nextJobInfo.setDoneDependencyJobCount(nextJobInfo.getDoneDependencyJobCount() + 1);

					String nextEtlJob = nextJobInfo.getEtl_job();
					logger.info("{} 总依赖数 {},目前达成 {}"
							, nextEtlJob, depJobList.size(), nextJobInfo.getDoneDependencyJobCount());

					//判断是否所有依赖作业已经全部完成
					if (nextJobInfo.getDoneDependencyJobCount() != depJobList.size()) {
						continue;
					}
					//如果所有依赖作业已经全部完成，修改依赖作业标志位
					nextJobInfo.setDependencyFlag(true);

					String nextEtlJobCurrBathDate = nextJobInfo.getCurr_bath_date();
					//判断前一天的作业是否也已经完成
					if (nextJobInfo.isPreDateFlag()) {
						EtlJobDefBean jobDefine = jobDefineMap.get(nextEtlJob);
						if (jobDefine == null) {
							continue;
						}

						//如果作业是等待状态则修改状态
						if (Job_Status.PENDING.getCode().equals(nextJobInfo.getJob_disp_status())) {
							//判断是否为虚作业
							if (Job_Effective_Flag.VIRTUAL.getCode().equals(jobDefine.getJob_eff_flag())) {
								//如果是虚作业，直接完成
								nextJobInfo.setJob_disp_status(Job_Status.RUNNING.getCode());
								handleVirtualJob(nextEtlJob, nextEtlJobCurrBathDate);
							} else {
								//将这个作业的状态更新成Waiting,并将这个作业加入JobWaitingList
								nextJobInfo.setJob_disp_status(Job_Status.WAITING.getCode());
								TaskSqlHelper.updateEtlJobDispStatus(Job_Status.WAITING.getCode(), etlSysCd,
										nextEtlJob, nextEtlJobCurrBathDate);

								if (Pro_Type.WF.getCode().equals(nextJobInfo.getPro_type())) {
									addWaitFileJobToList(nextJobInfo);
								} else {
									jobWaitingList.add(nextJobInfo);
								}
							}
						}
					}
				}
			}

			//5、若当前作业是完成状态，则根据该作业的下一批次执行时间设置此作业
			String strNextDate = exeJobInfo.getStrNextDate();
			if(jobExecuteMap.containsKey(strNextDate)) {
				EtlJobBean nextJobInfo = jobExecuteMap.get(strNextDate).get(exeJobInfo.getEtl_job());
				if( null != nextJobInfo ) {
					nextJobInfo.setPreDateFlag(true);
					String nextEtlJob = nextJobInfo.getEtl_job();
					String nextEtlJobCurrBathDate = nextJobInfo.getCurr_bath_date();
					if(nextJobInfo.isDependencyFlag()) {
						EtlJobDefBean jobDefine = jobDefineMap.get(nextEtlJob);
						if( jobDefine == null ) {
							throw new AppSystemException("无法在内存表jobDefineMap中找到作业" + jobName);
						}
						String nextEtlJobStatus = nextJobInfo.getJob_disp_status();
						//如果作业是等待状态则修改状态
						if(Job_Status.PENDING.getCode().equals(nextEtlJobStatus)) {
							//判断是否为虚作业
							if(Job_Effective_Flag.VIRTUAL.getCode().equals(jobDefine.getJob_eff_flag())) {
								//如果是虚作业,直接完成
								nextJobInfo.setJob_disp_status(Job_Status.RUNNING.getCode());
								handleVirtualJob(nextEtlJob, nextEtlJobCurrBathDate);
							}
							else {
								//将这个作业的状态更新成Waiting,并将这个作业加入JobWaitingList
								nextJobInfo.setJob_disp_status(Job_Status.WAITING.getCode());
								TaskSqlHelper.updateEtlJobDispStatus(Job_Status.WAITING.getCode(), etlSysCd,
										nextEtlJob, nextEtlJobCurrBathDate);

								if(Pro_Type.WF.getCode().equals(nextJobInfo.getPro_type())) {
									addWaitFileJobToList(nextJobInfo);
								}
								else {
									jobWaitingList.add(nextJobInfo);
								}
							}
						}
					}
				}
			}
		}
		else {
			//6、如果作业不是正常完成，发送警告消息（短信息）
			if(isNeedSendSMS) {
				String message = currBathDate + " " + jobName + "调度失败!";
				NOTIFY.sendMsg(message);
			}
		}
	}

	/**
	 * 根据作业标识，为该作业增加资源。注意，此处会更新作业资源表信息。
	 * @author Tiger.Wang
	 * @date 2019/9/6
	 * @param etlJobKey   作业主键
	 */
	private void increaseResource(String etlJobKey) {

		EtlJobDefBean jobDefine = jobDefineMap.get(etlJobKey);
		if( null == jobDefine ) {
			throw new AppSystemException("无法在内存表jobDefineMap中找到作业" + etlJobKey);
		}
		List<Etl_job_resource_rela> resources = jobDefine.getJobResources();
		for(Etl_job_resource_rela resource : resources) {
			String resourceType = resource.getResource_type();
			int needCount = resource.getResource_req();
			logger.info("Resource {} need {} {}", etlJobKey, resourceType, needCount);
			Etl_resource etlResource = sysResourceMap.get(resourceType);
			logger.info("Before increase, {} used {}", resourceType, etlResource.getResource_used());

			int resourceNum = etlResource.getResource_used() - needCount;
			TaskSqlHelper.updateEtlResourceUsedByResourceType(etlSysCd, resourceType, resourceNum);

			etlResource.setResource_used(resourceNum);
			logger.info("After increase, {} used {}", resourceType, etlResource.getResource_used());
		}
	}

	/**
	 * 检查定时触发的作业是否达到执行时间。注意，此方法会使用jobExecuteMap内存Map，维护jobWaitingList内存Map，也会修改
	 * 作业信息表。此处会根据作业状态、作业开始执行时间，决定是否更新作业状态或者加入到作业等待表（jobWaitingList）。
	 * @author Tiger.Wang
	 * @date 2019/9/6
	 */
	private void checkTimeDependencyJob() {

		for (String strBathDate : jobExecuteMap.keySet()) {
			Map<String, EtlJobBean> jobMap = jobExecuteMap.get(strBathDate);
			Iterator<String> jobIter = jobMap.keySet().iterator();
			long time = DateUtil.getNowDateTime2Milli();
			logger.info("CurrentTime={}", time);
			while (jobIter.hasNext()) {

				String strJobName = jobIter.next();
				EtlJobBean exeJob = jobMap.get(strJobName);
				/*
				 * 此处主要行为如下：
				 * 一、若作业状态为[挂起]，则会检查该作业的前置作业是否已经完成、调度时间是否已经到达，若检查通过则会更新该
				 *     作业状态，并且认为该作业可以立即执行；
				 * 二、若该作业可以立即执行，当作业类型为[等待文件]时，登记到waitFileJobList（内存Map），
				 *     否则登记到jobWaitingList（内存Map）。
				 */
				if (Job_Status.PENDING.getCode().equals(exeJob.getJob_disp_status())) {
					//作业状态为Pending,检查前置作业是否已完成
					//判断前一批次作业是否已经完成
					if (!exeJob.isPreDateFlag()) {
						//前一批次作业未完成，作业状态不能置为Waiting
						continue;
					}
					//xchao--2017年8月15日 16:25:51 添加按秒、分钟、小时进行执行
					if (exeJob.getExecuteTime() == zclong) {
						if (!checkEtlJob(exeJob)) {
							continue;
						}
					}
					//判断作业触发时间是否为0l,不为0表示定时触发
					else if (exeJob.getExecuteTime() != 0L) {
						//定时触发,判断作业调度时间是否已经达到
						if (time < exeJob.getExecuteTime()) {
							//作业调度时间未到,作业状态不能置为Waiting
							continue;
						}
					} else {
						//其他触发方式，不处理
						continue;
					}

					//将Pending状态置为Waiting
					logger.info("{}'s executeTime={}, can run!", strJobName, exeJob.getExecuteTime());
					exeJob.setJob_disp_status(Job_Status.WAITING.getCode());
					TaskSqlHelper.updateEtlJobDispStatus(Job_Status.WAITING.getCode(), etlSysCd, exeJob.getEtl_job(),
							exeJob.getCurr_bath_date());
				}
				/*xchao--不放一天调度多次的作业2017年8月15日 16:25:51 添加按秒、分钟、小时进行执行
				 * 		如果是成功的作业，且执行时间为999999999999999999l表示为按频率执行
				 * 		再次检查是否达到执行的要求
				 * */
				else if (exeJob.getExecuteTime() == zclong) {
					if (!checkEtlJob(exeJob)) {
						continue;
					}
				} else {
					//Pending状态外的作业不处理
					continue;
				}

				if (Pro_Type.WF.getCode().equals(exeJob.getPro_type())) {
					addWaitFileJobToList(exeJob);
				} else {
					jobWaitingList.add(exeJob);
				}
			}
		}
	}

	/**
	 * 对外提供的干预接口，根据当前跑批日期、调度作业标识，直接调起指定的作业。
	 * 注意，该方法会更新作业状态信息，并会将作业登记到redis中。该方法仅修改内存Map（map）作业状态。
	 * @note 1、根据当前跑批日期、调度作业标识在map中检测是否存在该作业；
	 *       2、判断map中的作业状态（触发的作业必须在挂起、等待状态，否则干预不成功）。
	 * @author Tiger.Wang
	 * @date 2019/9/9
	 * @param currbathDate   当前跑批日期
	 * @param etlJob    调度作业标识
	 */
	public void handleJob2Run(String currbathDate, String etlJob) {

		//1、根据当前跑批日期、调度作业标识在map中检测是否存在该作业。
		Map<String, EtlJobBean> jobMap = jobExecuteMap.get(currbathDate);
		if(null == jobMap) {
			logger.warn("在进行直接调度干预时，根据{} {}无法找到作业信息", etlJob, currbathDate);
			return;
		}
		EtlJobBean exeJobInfo = jobMap.get(etlJob);
		if(null == exeJobInfo) {
			logger.warn("在进行直接调度干预时，{} {}作业不存在调度列表中", etlJob, currbathDate);
			return;
		}
		Etl_job_def jobDefine = jobDefineMap.get(etlJob);
		if(null == jobDefine) {
			logger.warn("在进行直接调度干预时，{} {}作业不存在定义列表中", etlJob, currbathDate);
			return;
		}

		/*
		 * 2、判断map中的作业状态（触发的作业必须在挂起、等待状态，否则干预不成功）。
		 *    一、判断map中对应作业的作业状态，若作业状态不为挂起、等待，则不允许干预；
		 *    二、若作业状态为挂起、等待，在作业为虚作业的情况下直接登记到redis中并且作业状态为已完成，
		 *        否则将作业设置为运行中，再将该作业登记到redis中。
		 */
		if(Job_Status.PENDING.getCode().equals(exeJobInfo.getJob_disp_status()) ||
				Job_Status.WAITING.getCode().equals(exeJobInfo.getJob_disp_status())) {
			etlJob = exeJobInfo.getEtl_job();
			String currBathDate = exeJobInfo.getCurr_bath_date();
			if(Job_Effective_Flag.VIRTUAL.getCode().equals(jobDefine.getJob_eff_flag())) {
				exeJobInfo.setJob_disp_status(Job_Status.RUNNING.getCode());
				//如果是虚作业的话，直接将作业状态置为Done
				handleVirtualJob(etlJob, currBathDate);
			}else {
				//将这个作业的状态更新成Waiting,并将这个作业加入JobWaitingList
				exeJobInfo.setJob_disp_status(Job_Status.WAITING.getCode());
				TaskSqlHelper.updateEtlJobDispStatus(Job_Status.WAITING.getCode(), etlSysCd, etlJob, currBathDate);

				if(Pro_Type.WF.getCode().equals(exeJobInfo.getPro_type())) {
					addWaitFileJobToList(exeJobInfo);
				}else {
					//扣除资源
					decreaseResource(etlJob);
					//更新作业的状态到Running
					exeJobInfo.setJob_disp_status(Job_Status.RUNNING.getCode());
					exeJobInfo.setJobStartTime(DateUtil.getNowDateTime2Milli());
					TaskSqlHelper.updateEtlJobDispStatus(Job_Status.RUNNING.getCode(), etlSysCd, etlJob, currBathDate);
					//加入要执行的列表
					String runningJob = etlJob + REDISCONTENTSEPARATOR + currBathDate +
							REDISCONTENTSEPARATOR + REDISHANDLE;
					REDIS.rpush(strRunningJob, runningJob);
				}
			}
		}
	}

	/**
	 * 对外提供的干预接口，用于重新运行调度系统。注意，该方法会重置待调度作业表（map）中的作业状态，重置的作业内容包括：
	 * 上一个依赖是否完成、作业状态、依赖作业是否完成、已完成的依赖作业总数，重置调度作业定义表（map）中的当前作业优先级。
	 * 该方法仅修改内存Map（map）作业状态。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 */
	public void handleSys2Rerun() {

		for (String strBathDate : jobExecuteMap.keySet()) {
			Map<String, EtlJobBean> jobMap = jobExecuteMap.get(strBathDate);
			for (String strJobName : jobMap.keySet()) {
				EtlJobBean exeJobInfo = jobMap.get(strJobName);
				exeJobInfo.setPreDateFlag(false);
				exeJobInfo.setJob_disp_status(Job_Status.PENDING.getCode());
				exeJobInfo.setDependencyFlag(false);
				exeJobInfo.setDoneDependencyJobCount(0);
				EtlJobDefBean jobDefine = jobDefineMap.get(strJobName);
				if (jobDefine != null) {
					exeJobInfo.setJob_priority_curr(jobDefine.getJob_priority());
				}
			}
		}

		checkJobDependency("");
		initWaitingJob("");
	}

	/**
	 * 对外提供的干预接口，用于暂停调度系统。注意，此方法会修改jobExecuteMap中作业的状态，也会清理waitFileJobList。
	 * 该方法仅修改内存Map（map）作业状态，不直接操作运行中的作业。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 */
	public void handleSys2Pause() {

		for (String strBathDate : jobExecuteMap.keySet()) {
			Map<String, EtlJobBean> jobMap = jobExecuteMap.get(strBathDate);
			for (String strJobName : jobMap.keySet()) {
				EtlJobBean exeJobInfo = jobMap.get(strJobName);
				if (Job_Status.PENDING.getCode().equals(exeJobInfo.getJob_disp_status()) ||
						Job_Status.WAITING.getCode().equals(exeJobInfo.getJob_disp_status())) {
					exeJobInfo.setJob_disp_status(Job_Status.STOP.getCode());
				}
				if (Pro_Type.WF.getCode().equals(exeJobInfo.getPro_type()) &&
						Job_Status.RUNNING.getCode().equals(exeJobInfo.getJob_disp_status())) {
					exeJobInfo.setJob_disp_status(Job_Status.STOP.getCode());
				}
			}
		}

		waitFileJobList.clear();
	}

	/**
	 * 对外提供的干预接口，用于续跑调度系统。注意，此处会修改jobExecuteMap中的作业信息，
	 * 重新加载作业到等待表（waitFileJobList）。该方法仅修改内存Map（map）作业状态。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 */
	public void handleSys2Resume() {

		for (String strBathDate : jobExecuteMap.keySet()) {

			Map<String, EtlJobBean> jobMap = jobExecuteMap.get(strBathDate);

			for (String strJobName : jobMap.keySet()) {
				EtlJobBean exeJobInfo = jobMap.get(strJobName);

				if (Job_Status.STOP.getCode().equals(exeJobInfo.getJob_disp_status()) ||
						Job_Status.ERROR.getCode().equals(exeJobInfo.getJob_disp_status())) {
					exeJobInfo.setJob_disp_status(Job_Status.PENDING.getCode());
					EtlJobDefBean jobDefine = jobDefineMap.get(strJobName);
					if (jobDefine != null) {
						exeJobInfo.setJob_priority_curr(jobDefine.getJob_priority());
					}
				}
			}
		}

		initWaitingJob("");
	}

	/**
	 * 对外提供的干预接口，用于停止作业状态为[挂起]和[等待]的作业。
	 * 注意，该方法仅修改内存Map（map）作业状态，不直接操作运行中的作业。
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @param currbathDate  当前跑批日期
	 * @param etlJob    调度作业标识
	 */
	public void handleJob2Stop(String currbathDate, String etlJob) {

		Map<String, EtlJobBean> jobMap = jobExecuteMap.get(currbathDate);
		if( null == jobMap ) {
			return;
		}

		EtlJobBean exeJobInfo = jobMap.get(etlJob);
		if( null == exeJobInfo ) {
			return;
		}
		if(Job_Status.PENDING.getCode().equals(exeJobInfo.getJob_disp_status()) ||
				Job_Status.WAITING.getCode().equals(exeJobInfo.getJob_disp_status())) {
			exeJobInfo.setJob_disp_status(Job_Status.STOP.getCode());
			TaskSqlHelper.updateEtlJobDispStatus(Job_Status.STOP.getCode(), etlSysCd, etlJob, currbathDate);
		}
	}

	/**
	 * 对外提供的干预接口，用于进行作业重跑。注意，该方法会更新调度作业表，以及会维护jobWaitingList内存Map。
	 * @note    1、检查干预的作业是否在内存Map（jobExecuteMap、jobDefineMap）中有登记；
	 *          2、干预作业状态为停止、错误、完成的作业；
	 *          3、设置干预的作业优先级为最大优先级；
	 *          4、将作业加入等待调度作业内存Map（jobWaitingList）。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param currbathDate  当前跑批日期
	 * @param etlJob    调度作业标识
	 */
	public void handleJob2Rerun(String currbathDate, String etlJob) {

		//1、检查干预的作业是否在内存Map（jobExecuteMap、jobDefineMap）中有登记；
		Map<String, EtlJobBean> jobMap = jobExecuteMap.get(currbathDate);
		if(null == jobMap) {
			logger.warn("在进行作业重跑干预时，根据{} {}无法找到作业信息", etlJob, currbathDate);
			return;
		}
		EtlJobBean exeJobInfo = jobMap.get(etlJob);
		if(null == exeJobInfo) {
			logger.warn("在进行作业重跑干预时，{} {}作业不存在调度列表中", etlJob, currbathDate);
			return;
		}
		Etl_job_def jobDefine = jobDefineMap.get(etlJob);
		if(null == jobDefine) {
			logger.warn("在进行作业重跑干预时，{} {}作业不存在定义列表中", etlJob, currbathDate);
			return;
		}
		/*
		 * 2、干预作业状态为停止、错误、完成的作业；
		 *      一、若被干预的作业上一批次作业还未完成，则将该作业设置为挂起状态；
		 *      二、若该作业不是立即执行，且未到执行时间，则将该作业设置为挂起状态；
		 *      三、若该作业的依赖作业还未完成，则将该作业设置为挂起状态；
		 *      四、若该作业是虚作业，则将该作业设置为完成状态；
		 *      五、若该作业不是虚作业，则将该作业加入等待调度作业内存Map（jobWaitingList）。
		 */
		if(Job_Status.STOP.getCode().equals(exeJobInfo.getJob_disp_status()) ||
				Job_Status.ERROR.getCode().equals(exeJobInfo.getJob_disp_status()) ||
				Job_Status.DONE.getCode().equals(exeJobInfo.getJob_disp_status())) {
			//3、设置干预的作业优先级为最大优先级；
			exeJobInfo.setJob_priority_curr(MAXPRIORITY);
			TaskSqlHelper.updateEtlJobCurrPriority(exeJobInfo.getJob_priority_curr(), etlSysCd, etlJob, currbathDate);

			if(!exeJobInfo.isPreDateFlag()) {
				exeJobInfo.setJob_disp_status(Job_Status.PENDING.getCode());
				TaskSqlHelper.updateEtlJobDispStatus(exeJobInfo.getJob_disp_status(), etlSysCd, etlJob, currbathDate);
				return;
			}
			if(0 != exeJobInfo.getExecuteTime()) {
				if(DateUtil.getNowDateTime2Milli() < exeJobInfo.getExecuteTime()) {
					exeJobInfo.setJob_disp_status(Job_Status.PENDING.getCode());
					TaskSqlHelper.updateEtlJobDispStatus(exeJobInfo.getJob_disp_status(), etlSysCd, etlJob,
							currbathDate);
				}else {
					//处理虚作业。
					if(Job_Effective_Flag.VIRTUAL.getCode().equals(jobDefine.getJob_eff_flag())) {
						exeJobInfo.setJob_disp_status(Job_Status.RUNNING.getCode());
						handleVirtualJob(etlJob, currbathDate);
					}else {
						//将这个作业的状态更新成Waiting,并将这个作业加入JobWaitingList
						exeJobInfo.setJob_disp_status(Job_Status.WAITING.getCode());
						TaskSqlHelper.updateEtlJobDispStatus(exeJobInfo.getJob_disp_status(), etlSysCd, etlJob,
								currbathDate);
						if(Pro_Type.WF.getCode().equals(exeJobInfo.getPro_type())) {
							addWaitFileJobToList(exeJobInfo);
						}else {
							jobWaitingList.add(exeJobInfo);
						}
					}
				}
			}else {
				if((!exeJobInfo.isDependencyFlag())) {
					exeJobInfo.setJob_disp_status(Job_Status.PENDING.getCode());
					TaskSqlHelper.updateEtlJobDispStatus(exeJobInfo.getJob_disp_status(), etlSysCd, etlJob,
							currbathDate);
				}else {
					//处理虚作业。
					if(Job_Effective_Flag.VIRTUAL.getCode().equals(jobDefine.getJob_eff_flag())) {
						exeJobInfo.setJob_disp_status(Job_Status.RUNNING.getCode());
						handleVirtualJob(etlJob, currbathDate);
					}else {
						//将这个作业的状态更新成Waiting,并将这个作业加入JobWaitingList
						exeJobInfo.setJob_disp_status(Job_Status.WAITING.getCode());
						TaskSqlHelper.updateEtlJobDispStatus(exeJobInfo.getJob_disp_status(), etlSysCd, etlJob,
								currbathDate);
						if(Pro_Type.WF.getCode().equals(exeJobInfo.getPro_type())) {
							addWaitFileJobToList(exeJobInfo);
						}else {
							jobWaitingList.add(exeJobInfo);
						}
					}
				}
			}
		}
	}

	/**
	 * 对外提供的干预接口，用于进行作业优先级调整。注意，该方法会更新jobExecuteMap内存Map中的作业信息，
	 * 也会更新数据库中[作业调度表etl_job_cur]的作业信息。
	 * @author Tiger.Wang
	 * @date 2019/9/17
	 * @param currBathDate  当前跑批日期
	 * @param etlJob    调度作业标识
	 * @param priority  作业优先级
	 */
	public void handleJob2ChangePriority(String currBathDate, String etlJob, int priority) {

		Map<String, EtlJobBean> jobMap = jobExecuteMap.get(currBathDate);
		if( null == jobMap ) {
			return;
		}

		EtlJobBean exeJobInfo = jobMap.get(etlJob);
		if( null == exeJobInfo ) {
			return;
		}

		exeJobInfo.setJob_priority_curr(priority);

		TaskSqlHelper.updateEtlJobCurrPriority(priority, etlSysCd, etlJob, currBathDate);
	}

	/**
	 * 对外提供的干预接口，用于辅助完成[作业跳过]干预。注意，该方法会扣除被干预作业的资源。
	 * @note    1、检查干预的作业是否在内存Map（jobExecuteMap、jobDefineMap）中有登记；
	 *          2、扣除被干预作业的作业类型不是[等待文件]及[虚作业]的使用资源；
	 *          3、被干预作业的作业状态设置为完成，推送完成消息到redis。
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @param currbathDate  当前跑批日期
	 * @param etlJob    调度作业标识
	 */
	public void handleJob2Skip(String currbathDate, String etlJob) {

		//1、检查干预的作业是否在内存Map（jobExecuteMap、jobDefineMap）中有登记；
		Map<String, EtlJobBean> jobMap = jobExecuteMap.get(currbathDate);
		if(null == jobMap) {
			return;
		}
		EtlJobBean exeJobInfo = jobMap.get(etlJob);
		if(null == exeJobInfo) {
			return;
		}

		EtlJobDefBean jobDefine = jobDefineMap.get(etlJob);
		if(null == jobDefine) {
			return;
		}
		//2、扣除被干预作业的作业类型不是[等待文件]及[虚作业]的使用资源；
		exeJobInfo.setJob_disp_status(Job_Status.RUNNING.getCode());
		if(!Pro_Type.WF.getCode().equals(exeJobInfo.getPro_type()) &&
				!Job_Effective_Flag.VIRTUAL.getCode().equals(jobDefine.getJob_eff_flag())) {
			//如果不是WF作业或者不是虚作业时，扣除资源
			decreaseResource(etlJob);
		}
		//3、被干预作业的作业状态设置为完成，推送完成消息到redis（借用虚作业处理逻辑）
		handleVirtualJob(exeJobInfo.getEtl_job(), exeJobInfo.getCurr_bath_date());
	}

	/**
	 * 对外提供的干预接口，用于干预系统日切
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @return boolean
	 */
	public void handleSysDayShift() { isSysJobShift = true; }

	/**
	 * 对外接口，获取系统是否暂停状态
	 * @author Tiger.Wang
	 * @date 2019/9/9
	 * @return boolean  暂停返回true，否则false
	 */
	public boolean isSysPause() { return isSysPause; }

	/**
	 * 对外接口，用于关闭（取消）系统暂停
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 */
	public void closeSysPause() { isSysPause = false; }

	/**
	 * 对外接口，用于打开（开启）系统暂停
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 */
	public void openSysPause() { isSysPause = true; }

	/**
	 * 根据作业标识，为该作业减少资源。注意，此处会更新作业资源表信息。
	 * @param etlJob 作业信息
	 */
	private void decreaseResource(String etlJob) {

		EtlJobDefBean jobDefine = jobDefineMap.get(etlJob);
		if( null == jobDefine ) {
			throw new AppSystemException("无法在内存表jobDefineMap中找到作业" + etlJob);
		}
		List<Etl_job_resource_rela> resources = jobDefine.getJobResources();
		for(Etl_job_resource_rela resource : resources) {
			String resourceType = resource.getResource_type();
			int needCount = resource.getResource_req();
			logger.info("{} need {} {}", etlJob, resourceType, needCount);

			Etl_resource etlResource = sysResourceMap.get(resourceType);
			logger.info("Before decrease, {} used {}", resourceType, etlResource.getResource_used());

			int resourceNum = etlResource.getResource_used() + needCount;
			//更新作业资源表信息
			TaskSqlHelper.updateEtlResourceUsedByResourceType(etlSysCd, resourceType, resourceNum);
			etlResource.setResource_used(resourceNum);
			logger.info("After decrease, {} used {}", resourceType, etlResource.getResource_used());
		}
	}

	/**
	 * 比较作业定义表jobDefineMap（内存Map）中的作业的使用资源与系统作业表sysResourceMap（内存Map），检查是否满足使用资源；
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @param etlJob    调度作业标识
	 * @return boolean  是否满足使用资源
	 */
	private boolean checkJobResource(String etlJob) {

		EtlJobDefBean jobDefine = jobDefineMap.get(etlJob);
		logger.info("检测资源：{}", etlJob);
		if( null == jobDefine ) {
			return true;
		}
		List<Etl_job_resource_rela> resources = jobDefine.getJobResources();
		for(Etl_job_resource_rela resource : resources) {
			String resourceType = resource.getResource_type();
			int needCount = resource.getResource_req();
			logger.info("{} need {} {}", etlJob, resourceType, needCount);

			Etl_resource etlResource = sysResourceMap.get(resourceType);
			logger.info("{} maxCount is {}", resourceType, etlResource.getResource_max());
			logger.info("{} usedCount is {}", resourceType, etlResource.getResource_used());

			if(etlResource.getResource_max() < etlResource.getResource_used() + needCount) {
				logger.info("{}'s resource is not enougt", etlJob);
				return false;
			}
		}

		return true;
	}

	/**
	 * 根据跑批日期，检查该日期下的作业是否全部完成
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @param bathDate  跑批日期
	 * @return boolean  作业是否全部完成
	 */
	private boolean checkAllJobFinished(LocalDate bathDate) {

		//取得所有该调度日期的作业
		Map<String, EtlJobBean> jobMap = jobExecuteMap.get(bathDate.format(DateUtil.DATE_DEFAULT));
		if(jobMap == null) {
			return false;
		}
		//判断该调度日期的作业是否状态全部为"D"
		for (String s : jobMap.keySet()) {
			EtlJobBean exeJobInfo = jobMap.get(s);
			/*
			 * xchao--2017年8月15日 16:25:51 添加按秒、分钟、小时进行执行
			 * 不把执行时间为9999999的设置为完成不完成
			 */
			if (exeJobInfo.getExecuteTime() == zclong) {
				continue;
			}
			if (!Job_Status.DONE.getCode().equals(exeJobInfo.getJob_disp_status())) {
				return false;
			}
		}
		//全部为[完成]状态时，返回true
		return true;
	}

	/**
	 * xchao 2019年7月25日 11:19:59
	 *   判断当天调度作业是否已经全部是完成和失败
	 * @param bathDate 当前跑批日期   （yyyy-MM-dd）
	 * @return  当天调度作业是否已经全部是完成和失败
	 */
	private boolean checkAllJobFinishedORError(LocalDate bathDate) {

		//取得所有该调度日期的作业
		Map<String, EtlJobBean> jobMap = jobExecuteMap.get(bathDate.format(DateUtil.DATE_DEFAULT));
		if( null == jobMap ) {
			return false;
		}

		//判断该调度日期的作业是否状态全部为"D"
		Iterator<String> jobIter = jobMap.keySet().iterator();
		logger.info("内存表中存在 {} 个作业", jobMap.size());
		Set<String> status = new HashSet<>();
		while( jobIter.hasNext() ) {
			EtlJobBean exeJobInfo = jobMap.get(jobIter.next());
			status.add(exeJobInfo.getJob_disp_status());
		}
		logger.info("它们的调度状态种类有 {}", status.toString());

		return !status.contains(Job_Status.RUNNING.getCode()) && status.contains(Job_Status.ERROR.getCode());
	}

	/**
	 * 该方法根据当前跑批日期参数查询出执行错误的作业，将这些作业以系统干预的方式重跑。
	 * @note    1、查询出执行错误的作业；
	 *          2、没执行错误的作业登记到系统干预表中。
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @param bathDate  当前跑批日期
	 */
	private void insertErrorJob2Handle(LocalDate bathDate) {

		String localDateTime = LocalDateTime.now().format(DateUtil.DATETIME);

		Etl_job_hand etlJobHand = new Etl_job_hand();
		etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
		etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
		etlJobHand.setEtl_hand_type(Meddle_type.JOB_RERUN.getCode());
		etlJobHand.setSt_time(localDateTime);
		etlJobHand.setEnd_time(localDateTime);
		etlJobHand.setEvent_id(localDateTime);

		List<Etl_job_cur> etlJobCurs = TaskSqlHelper.getEtlJobsByJobStatus(etlSysCd, Job_Status.ERROR.getCode());
		for(Etl_job_cur etlJobCur : etlJobCurs) {
			etlJobHand.setEtl_job(etlJobCur.getEtl_job());
			etlJobHand.setPro_para(etlSysCd + PARASEPARATOR + etlJobCur.getEtl_job() +
					PARASEPARATOR + bathDate.format(DateUtil.DATE_DEFAULT));

			TaskSqlHelper.insertIntoEtlJobHand(etlJobHand);
			logger.info("该作业发生了错误，需要重跑 {}", etlJobCur.getEtl_job());
		}
	}

	/**
	 * 该方法将jobDefineMap（内存Map）已不存在、jobExecuteMap（内存Map）中不为频率调度的作业从jobExecuteMap中移除。
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @param bathDate  当前跑批日期
	 */
	private void removeExecuteJobs(LocalDate bathDate) {

		Map<String, EtlJobBean> jobMap = jobExecuteMap.get(bathDate.format(DateUtil.DATE_DEFAULT));

		Iterator<String> jobIter = jobMap.keySet().iterator();
		while( jobIter.hasNext() ) {
			String strJobName = jobIter.next();
			EtlJobDefBean jobDefine = jobDefineMap.get(strJobName);
			if( null == jobDefine ) {
				jobIter.remove();
				continue;
			}

			if(!Dispatch_Frequency.PinLv.getCode().equals(jobDefine.getDisp_freq())) {
				//使用iterator.remove();删除当前map中的值，防止ConcurrentModificationException异常
				jobIter.remove();
			}
		}

		if(jobMap.size() == 0){
			jobExecuteMap.remove(bathDate.format(DateUtil.DATE_DEFAULT));
		}
	}

	/**
	 * 更新调度系统使用的资源。注意，该方法会更新sysResourceMap（内存Map）的作业使用资源。
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 */
	private void updateSysUsedResource() {

		//取得系统所有资源信息
		List<Etl_resource> resources = TaskSqlHelper.getEtlSystemResources(etlSysCd);
		for(Etl_resource etlResource : resources) {
			String resourceType = etlResource.getResource_type();
			int resourceMax =etlResource.getResource_max();
			Etl_resource etlResourceMap = sysResourceMap.get(resourceType);
			if(null != etlResourceMap && etlResourceMap.getResource_max() != resourceMax) {
				etlResourceMap.setResource_max(resourceMax);
				logger.info("{}'s maxCount change to {}", resourceType, etlResourceMap.getResource_max());
			}
		}
	}
//--------------------------------分析并处理需要立即启动的作业（publishReadyJob方法）end-----------------------------------
	/**
	 * 处理虚作业问题，该方法会更新虚作业信息及推送数据到redis。
	 * @note    1、更新作业表中，虚作业的状态及其它信息；
	 *          2、将虚作业标识为完成作业，并推送给redis。
	 * @author Tiger.Wang
	 * @date 2019/9/6
	 * @param etlJob    调度作业标识
	 * @param currBathDate  跑批日期
	 */
	private void handleVirtualJob(String etlJob, String currBathDate) {

		//1、更新作业表中，虚作业的状态及其它信息
		String localDateTime = fd.ng.core.utils.DateUtil.getDateTime(DateUtil.DATETIME);
		TaskSqlHelper.updateVirtualJob(etlSysCd, etlJob, currBathDate, localDateTime, localDateTime);

		//2、将虚作业标识为完成作业，并推送给redis
		String finishedJob = etlJob + REDISCONTENTSEPARATOR + currBathDate;
		REDIS.rpush(strFinishedJob, finishedJob);
	}

	/**
	 * 用于检测作业类型为WF的作业，以线程的方式监控信号文件是否已经到达。
	 * @author Tiger.Wang
	 * @date 2019/9/16
	 */
	private class CheckWaitFileThread extends Thread {

		private static final long CHECKMILLISECONDS = 60000;
		private volatile boolean run = true;

		/**
		 * 用于监控信号文件是否已经到达。注意，此处会维护waitFileJobList内存Map，
		 * 也会每隔一定时间（CHECKMILLISECONDS变量）检查一次信号文件是否已经到达。
		 * @author Tiger.Wang
		 * @date 2019/9/16
		 */
		public void run() {

			try {
				while(run) {
					while (isLock) {
						logger.info("Wait file lock is true.Please wait");
						try{
							Thread.sleep(LOCKMILLISECONDS);
						}catch (InterruptedException ignored) {
						}
					}
					//TODO 为什么设置为true马上又设置为false
					isLock = true;
					List<WaitFileJobInfo> jobList = new ArrayList<>(waitFileJobList);
					//FIXME 为什么不用waitFileJobList来初始化checkList？
					List<WaitFileJobInfo> checkList = new ArrayList<>(jobList);
					List<WaitFileJobInfo> finishedJobList = new ArrayList<>();
					for (WaitFileJobInfo jobInfo : checkList) {
						File file = new File(jobInfo.getWaitFilePath());
						if (file.exists()) {
							waitFileJobFinished(jobInfo);
							finishedJobList.add(jobInfo);
							logger.info("{} 文件已经等到。", jobInfo.getStrJobName());
						}
					}
					isLock = false;
					//TODO 这里的remove没问题？
					for (WaitFileJobInfo waitFileJobInfo : finishedJobList) {
						jobList.remove(waitFileJobInfo);
					}

					try {
						Thread.sleep(CHECKMILLISECONDS);
					}catch (InterruptedException ignored) {
					}
				}
			} catch (Exception ex) {
				logger.error("CheckWaitFileThread exception happened! {}", ex.getMessage());
			}

			logger.info("CheckWaitFileThread Stop!");
		}

		/**
		 * 停止信号文件监控线程
		 * @author Tiger.Wang
		 * @date 2019/9/16
		 */
		void stopThread(){

			logger.info("CheckWaitFileThread stop!");
			run = false;
		}
	}
}
