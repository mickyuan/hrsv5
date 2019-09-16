package hrds.control.task.helper;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.Job_Status;
import hrds.commons.codes.Main_Server_Sync;
import hrds.commons.codes.Meddle_status;
import hrds.commons.codes.Pro_Type;
import hrds.commons.entity.Etl_job_cur;
import hrds.commons.entity.Etl_job_hand;
import hrds.commons.entity.Etl_job_hand_his;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.control.task.TaskManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * ClassName: TaskJobHandleHelper
 * Description: 用于处理任务/作业干预的类。
 * Author: Tiger.Wang
 * Date: 2019/9/9 10:18
 * Since: JDK 1.8
 **/
public class TaskJobHandleHelper {

	private static final Logger logger = LogManager.getLogger();

	/**干预类型，作业直接触发JT（JOB_TRIGGER）标识*/
	private static final String JT = "JT";
	/**干预类型，系统重跑SO（SYS_ORIGINAL）标识*/
	private static final String SO = "SO";
	/**干预类型，系统暂停SP（SYS_PAUSE）标识*/
	private static final String SP = "SP";
	/**干预类型，系统续跑SR（SYS_RESUME）标识*/
	private static final String SR = "SR";
	/**干预类型，作业停止JS（JOB_STOP）标识*/
	private static final String JS = "JS";
	/**干预类型，作业重跑JR（JOB_RERUN）标识*/
	private static final String JR = "JR";
	/**干预类型，作业临时调整优先级JP（JOB_PRIORITY）标识*/
	private static final String JP = "JP";
	/**干预类型，作业跳过JJ（JOB_JUMP）标识*/
	private static final String JJ = "JJ";
	/**干预类型，系统停止SS（SYS_STOP）标识*/
	private static final String SS = "SS";
	/**干预类型，分组级暂停标识*/
	private static final String GP = "GP";
	/**干预类型，分组级重跑，从源头开始标识*/
	private static final String GC = "GC";
	/**干预类型，系统重跑标识*/
	private static final String GR = "GR";
	private static final String SB = "SB";
	/**干预类型，系统日切SF（SYS_SHIFT）标识*/
	private static final String SF = "SF";

	private static final String DEFAULT_BATH_DATE = "0";    //默认跑批日期

	private static final String PARAERROR = "干预类型不支持或参数错误"; //参数错误信息
	private static final String NOEXITSERROR = "任务不存在"; //任务不存在错误信息
	private static final String STATEERROR = "当前状态不允许执行此操作"; //状态异常错误信息
	private static final String JOBSTOPERROR = "任务停止失败"; //状态异常错误信息

	private static final String KILL9COMMANDLINE = "kill -9";
	private static final int DEFAULT_MILLISECONDS = 5000;   //默认尝试作业停止毫秒数

	private final TaskManager taskManager;

	/**
	 * TaskJobHandleHelper类构造器，构造器私有化，不允许外部构造，请使用newInstance方法构造。
	 * @author Tiger.Wang
	 * @date 2019/9/9
	 * @param taskManager   TaskManager类
	 */
	private TaskJobHandleHelper(TaskManager taskManager) {

		this.taskManager = taskManager;
	}

	/**
	 * 获取TaskJobHandleHelper实例。
	 * @author Tiger.Wang
	 * @date 2019/9/9
	 * @param taskManager   TaskManager类
	 * @return hrds.control.task.helper.TaskJobHandleHelper
	 */
	public static TaskJobHandleHelper newInstance(TaskManager taskManager) {

		return new TaskJobHandleHelper(taskManager);
	}

	/**
	 * 根据干预信息集合，对集合内的每一项进行识别及执行作业干预。
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @param handles   Etl_job_hand对象，表示干预信息集合
	 */
	public void doHandle(List<Etl_job_hand> handles) {

		for(Etl_job_hand handle : handles) {

			switch (handle.getEtl_hand_type()) {
				case JT:handleRunning(handle);break;    //作业触发
				case SO:handleSysRerun(handle);break;   //系统重跑
				case SP:handleSysPause(handle);break;   //系统暂停
				case SR:handleSysResume(handle);break;  //系统续跑
				case SS:handleSysStopAll(handle);break; //系统停止
				case JS:handleJobStop(handle);break;    //作业停止
				case JR:handleJobRerun(handle);break;   //作业重跑
				case JP:;break; //TODO 该干预方式，etl_job_hand表中缺少必要数据，跟干预参数有关，我要去确认一下
				case JJ:handleJobskip(handle);break;    //作业跳过
				case SB:;break; //TODO 原版代码，跟   SS 处理逻辑一模一样
				case GP:;break;
				case GC:;break;
				case GR:;break;
				case SF:handleSysShift(handle);break;   //系统日切
				default:break;
			}
		}
	}

	/**
	 * 用于干预类型为：作业直接触发JT（JOB_TRIGGER）的处理。注意，此方法会更新调度作业干预表信息。
	 * @note 1、解析调度作业干预表的参数信息；
	 *       2、检查该次干预的作业是否存在；
	 *       3、对作业状态为挂起、等待的作业进行干预。
	 * @author Tiger.Wang
	 * @date 2019/9/9
	 * @param handle    Etl_job_hand，表示干预信息
	 */
	private void handleRunning(Etl_job_hand handle) {

		//1、解析调度作业干预表的参数信息。
		Optional<Etl_job_cur> etlJobOptional = analyzeParameter(handle.getEtl_hand_type(), handle.getPro_para());
		if(!etlJobOptional.isPresent()) {
			logger.warn("{}任务分析参数异常，{}", handle.getEtl_job(), PARAERROR);
			return;
		}
		//2、检查该次干预的作业是否存在。
		String etlSysCd = handle.getEtl_sys_cd();
		String etlJobStr = handle.getEtl_job();
		String currBathDate = etlJobOptional.get().getCurr_bath_date();
		Etl_job_cur etlJobCur;
		try{
			etlJobCur = TaskSqlHelper.getEtlJob(etlSysCd, etlJobStr, currBathDate);
		}catch (AppSystemException e) {
			handle.setWarning(NOEXITSERROR);
			updateErrorHandle(handle);
			throw new AppSystemException("根据调度系统编号、调度作业标识、当前跑批日期获取调度作业信息失败" + etlSysCd);
		}
		/*
		 * 3、对作业状态为挂起、等待的作业进行干预。
		 *      一、若作业状态为挂起、等待中，则更新干预状态为运行中，则调用TaskManager的干预接口，进行干预；
		 *      二、否则，更新调度作业干预表，该次干预出现异常。
		 */
		if(Job_Status.PENDING.getCode().equals(etlJobCur.getJob_disp_status()) ||
				Job_Status.WAITING.getCode().equals(etlJobCur.getJob_disp_status())) {
			//更新调度作业干预表（etl_job_hand）。
			handle.setHand_status(Meddle_status.RUNNING.getCode());
			handle.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			handle.setEnd_time(DateUtil.getDateTime(hrds.control.utils.DateUtil.DATETIME));
			TaskSqlHelper.updateEtlJobHandle(handle);
			//修改系统日切干预标识
			taskManager.handleJob2Run(currBathDate, etlJobStr);
		}else {
			logger.warn("{}  {}，{}", etlJobStr, currBathDate, NOEXITSERROR);
			handle.setWarning(STATEERROR);
			updateErrorHandle(handle);
		}
	}

	/**
	 * 用于干预类型为：系统重跑SO（SYS_ORIGINAL）标识的处理。注意，此方法会更新调度作业干预表信息。
	 * @note 1、检查调度系统状态是否已暂停，以及是否还存在未完成或未停止的作业；
	 *       2、将所有作业置为挂起状态（PENDING）；
	 *       3、重新设置内存表（map）的作业状态；
	 *       4、取消调度系统暂停状态；
	 *       5、更新调度作业干预表，干预完成。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param handle    Etl_job_hand，表示干预信息
	 */
	private void handleSysRerun(Etl_job_hand handle) {

		//1、检查调度系统状态是否已暂停，以及是否还存在未完成或未停止的作业
		if(!taskManager.isSysPause()) {
			logger.warn("在进行重跑干预时，[{}]不是暂停状态，{}", handle.getEtl_job(), STATEERROR);
			handle.setWarning(STATEERROR);
			updateErrorHandle(handle);
			return;
		}
		String etlSysCd = handle.getEtl_sys_cd();
		List<Etl_job_cur> etlJobs = TaskSqlHelper.getReadyEtlJobs(etlSysCd);
		if(0 != etlJobs.size()) {
			// 有未完成或停止的job
			logger.warn("在进行重跑干预时，[{}]有未完成或未停止的作业，{}", handle.getEtl_job(), STATEERROR);
			handle.setWarning(STATEERROR);
			updateErrorHandle(handle);
			return;
		}
		//2、将所有作业置为挂起状态（PENDING）
		TaskSqlHelper.updateEtlJobToPending(etlSysCd);
		//3、重新设置内存表（map）的作业状态
		taskManager.handleSys2Rerun();
		//4、取消调度系统暂停状态
		taskManager.closeSysPause();
		//5、更新调度作业干预表信息
		updateDoneHandle(handle);
	}

	/**
	 * 用于干预类型为：系统暂停SP（SYS_PAUSE）标识的处理。注意，此方法会更新调度作业干预表信息。
	 * @note 1、检查调度系统是否已经是暂停状态；
	 *       2、修改内存表（map）中的作业状态；
	 *       3、将调度作业表中的所有作业的作业状态标识为[停止]；
	 *       4、停止所有已经在运行中的作业；
	 *       5、暂停调度系统的运行；
	 *       6、更新调度作业干预表，干预完成。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param handle    Etl_job_hand，表示干预信息
	 */
	private void handleSysPause(Etl_job_hand handle) {

		//1、检查调度系统是否已经是暂停状态
		if(taskManager.isSysPause()){
			logger.warn("在进行系统暂停干预时，[{}]已经是暂停状态，{}", handle.getEtl_job(), STATEERROR);
			handle.setWarning(STATEERROR);
			updateErrorHandle(handle);
			return;
		}
		Optional<Etl_job_cur> etlJobOptional = analyzeParameter(handle.getEtl_hand_type(), handle.getPro_para());
		if(!etlJobOptional.isPresent()) {
			logger.warn("{}任务分析参数异常，{}", handle.getEtl_job(), PARAERROR);
			return;
		}
		//2、修改内存表（map）中的作业状态
		taskManager.handleSys2Pause();
		//3、将调度作业表中的所有作业的作业状态标识为[停止]，TODO 此处较原版改动：不再使用reputMap(map)
		TaskSqlHelper.updateReadyEtlJobStatus(handle.getEtl_sys_cd(), Job_Status.STOP.getCode());
		//4、停止所有已经在运行中的作业
		List<Etl_job_cur> etlJobs = TaskSqlHelper.getEtlJobsByJobStatus(handle.getEtl_sys_cd(),
				Job_Status.RUNNING.getCode());
		if(etlJobs.size() != 0) {
			stopRunningJobs(etlJobs);
			try {
				do {
					Thread.sleep(DEFAULT_MILLISECONDS);
				} while (!checkJobsIsStop(etlJobs));
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		//5、暂停调度系统的运行
		taskManager.openSysPause();
		//6、更新调度作业干预表，干预完成
		updateDoneHandle(handle);
	}

	/**
	 * 用于干预类型为：系统续跑SR（SYS_RESUME）标识的处理。注意，此方法会更新调度作业干预表信息。
	 * @note 1、判断当前状态是否是暂停状态，不是暂停状态时不能重跑；
	 *       2、将STOP/ERROR作业置为挂起状态（PENDING）；
	 *       3、重新设置内存表（map）的作业状态；
	 *       4、取消调度系统暂停状态；
	 *       5、更新调度作业干预表信息。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param handle    Etl_job_hand，表示干预信息
	 */
	private void handleSysResume(Etl_job_hand handle) {

		//1、判断当前状态是否是暂停状态，不是暂停状态时不能重跑
		if(!taskManager.isSysPause()) {
			logger.warn("在进行续跑干预时，[{}]不是暂停状态，{}", handle.getEtl_job(), STATEERROR);
			handle.setWarning(STATEERROR);
			updateErrorHandle(handle);
			return;
		}

		String etlSysCd = handle.getEtl_sys_cd();
		List<Etl_job_cur> etlJobs = TaskSqlHelper.getReadyEtlJobs(etlSysCd);
		if(0 != etlJobs.size()) {
			// 有未完成或停止的job
			logger.warn("在进行续跑干预时，[{}]有未完成或未停止的作业，{}", handle.getEtl_job(), STATEERROR);
			handle.setWarning(STATEERROR);
			updateErrorHandle(handle);
			return;
		}
		//2、将STOP/ERROR作业置为挂起状态（PENDING）
		TaskSqlHelper.updateEtlJobToPendingInResume(etlSysCd, Job_Status.PENDING.getCode());
		//3、重新设置内存表（map）的作业状态
		taskManager.handleSys2Resume();
		//4、取消调度系统暂停状态
		taskManager.closeSysPause();
		//5、更新调度作业干预表信息
		updateDoneHandle(handle);
	}

	/**
	 * 用于干预类型为：系统停止SS（SYS_STOP）标识的处理。注意，此方法会更新调度作业干预表信息。
	 * @note 1、将作业状态为PENDING/WAITING的作业置为STOP；
	 *       2、停止全部Running作业；
	 *       3、更新ETL_SYS运行状态为STOP；
	 *       4、更新干预表，干预完成。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param handle    Etl_job_hand，表示干预信息
	 */
	private void handleSysStopAll(Etl_job_hand handle) {

		//1、将作业状态为PENDING/WAITING的作业置为STOP
		//TODO 此处有问题dao.stopAllJob(reputMap(map));为什么需要reputMap(map)
		TaskSqlHelper.updateReadyEtlJobsDispStatus(handle.getEtl_sys_cd(), Job_Status.STOP.getCode());
		//2、停止全部Running作业
		List<Etl_job_cur> etlJobs = TaskSqlHelper.getEtlJobsByJobStatus(handle.getEtl_sys_cd(),
				Job_Status.RUNNING.getCode());
		if(0 != etlJobs.size()) {
			stopRunningJobs(etlJobs);
			try {
				do {
					Thread.sleep(DEFAULT_MILLISECONDS);
				} while (!checkJobsIsStop(etlJobs));
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		//3、更新ETL_SYS运行状态为STOP
		TaskSqlHelper.updateEtlSysRunStatus(handle.getEtl_sys_cd(), Job_Status.STOP.getCode());
		//4、更新干预表，干预完成
		updateDoneHandle(handle);
	}

	/**
	 * 用于干预类型为：作业停止JS（JOB_STOP）标识的处理。
	 * @note    1、检查该次干预的参数是否正确、该作业是否已经登记；
	 *          2、若干预的作业已经在运行中，则要结束该作业，并更新作业表的作业状态；
	 *          3、若干预的作业还未运行（挂起和等待中），则更新内存表（map）的作业状态；
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @param handle    Etl_job_hand，表示干预信息
	 */
	private void handleJobStop(Etl_job_hand handle) {

		//1、检查该次干预的参数是否正确、该作业是否已经登记；
		Optional<Etl_job_cur> etlJobOptional = analyzeParameter(handle.getEtl_hand_type(), handle.getPro_para());
		if(!etlJobOptional.isPresent()) {
			logger.warn("{}任务分析参数异常，{}", handle.getEtl_job(), PARAERROR);
			return;
		}
		String etlSysCd = handle.getEtl_sys_cd();
		String etlJobStr = handle.getEtl_job();
		String currBathDate = etlJobOptional.get().getCurr_bath_date();
		Etl_job_cur etlJob;
		try{
			etlJob = TaskSqlHelper.getEtlJob(etlSysCd, etlJobStr, currBathDate);
		}catch (AppSystemException e) {
			handle.setWarning(NOEXITSERROR);
			updateErrorHandle(handle);
			throw new AppSystemException("根据调度系统编号、调度作业标识、当前跑批日期获取调度作业信息失败" + etlSysCd);
		}
		//2、若干预的作业已经在运行中，则要结束该作业，并更新作业表的作业状态；
		if(Job_Status.RUNNING.getCode().equals(etlJob.getJob_disp_status())){
			//Running状态job停止，将干预的信息置为Running
			updateRunningHandle(handle);
			//TODO 此处较原版改动：stopJob(job)改为closeProcessById(etlJob.getJob_process_id(), etlJob.getPro_type())
			// 因为这两个方法干的事情是一样的
			//关闭作业进程（停止作业）
			if(closeProcessById(etlJob.getJob_process_id(), etlJob.getPro_type())) {
				updateDoneHandle(handle);
			}else {
				handle.setWarning(JOBSTOPERROR);
				updateErrorHandle(handle);
			}

			try {
				do {
					Thread.sleep(DEFAULT_MILLISECONDS);
				}
				while(!checkJobsIsStop(Collections.singletonList(etlJob)));
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
			//停止后的作业状态设为stop
			TaskSqlHelper.updateEtlJobDispStatus(Job_Status.STOP.getCode(),
					etlJob.getEtl_sys_cd(), etlJob.getEtl_job());
		}else if(Job_Status.PENDING.getCode().equals(etlJob.getJob_disp_status()) ||
				Job_Status.WAITING.getCode().equals(etlJob.getJob_disp_status())) {
			//3、若干预的作业还未运行（挂起和等待中），则更新内存表（map）的作业状态；
			taskManager.handleJob2Stop(etlJob.getCurr_bath_date(), handle.getEtl_job());
			updateDoneHandle(handle);
		}else {
			//否则干预失败，记录失败原因
			handle.setWarning(STATEERROR);
			updateErrorHandle(handle);
		}
	}

	/**
	 * 用于干预类型为：作业重跑JR（JOB_RERUN）标识的处理
	 * @note    1、检查该次干预的参数是否正确、该作业是否已经登记；
	 *          2、干预作业状态为停止、错误、完成的作业。
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @param handle     Etl_job_hand，表示干预信息
	 */
	private void handleJobRerun(Etl_job_hand handle) {

		//1、检查该次干预的参数是否正确、该作业是否已经登记；
		Optional<Etl_job_cur> etlJobOptional = analyzeParameter(handle.getEtl_hand_type(), handle.getPro_para());
		if(!etlJobOptional.isPresent()) {
			logger.warn("{} 任务分析参数异常，{}", handle.getEtl_job(), PARAERROR);
			return;
		}
		String etlSysCd = handle.getEtl_sys_cd();
		String etlJobStr = handle.getEtl_job();
		String currBathDate = etlJobOptional.get().getCurr_bath_date();
		Etl_job_cur etlJob;
		try{
			etlJob = TaskSqlHelper.getEtlJob(etlSysCd, etlJobStr, currBathDate);
		}catch (AppSystemException e) {
			handle.setWarning(NOEXITSERROR);
			updateErrorHandle(handle);
			throw new AppSystemException("根据调度系统编号、调度作业标识、当前跑批日期获取调度作业信息失败" + etlSysCd);
		}
		//2、干预作业状态为停止、错误、完成的作业。
		if(Job_Status.STOP.getCode().equals(etlJob.getJob_disp_status()) ||
				Job_Status.ERROR.getCode().equals(etlJob.getJob_disp_status()) ||
				Job_Status.DONE.getCode().equals(etlJob.getJob_disp_status())) {
			taskManager.handleJob2Rerun(currBathDate, etlJobStr);
			updateDoneHandle(handle);
		}else {
			//否则干预失败，记录失败原因
			handle.setWarning(STATEERROR);
			updateErrorHandle(handle);
		}
	}

	private void handleJobPriority(Etl_job_hand handle) {

		//1、检查该次干预的参数是否正确、该作业是否已经登记；
		Optional<Etl_job_cur> etlJobOptional = analyzeParameter(handle.getEtl_hand_type(), handle.getPro_para());
		if(!etlJobOptional.isPresent()) {
			logger.warn("{} 任务分析参数异常，{}", handle.getEtl_job(), PARAERROR);
			return;
		}
		String etlSysCd = handle.getEtl_sys_cd();
		String etlJobStr = handle.getEtl_job();
		String currBathDate = etlJobOptional.get().getCurr_bath_date();
		Etl_job_cur etlJob;
		try{
			etlJob = TaskSqlHelper.getEtlJob(etlSysCd, etlJobStr, currBathDate);
		}catch (AppSystemException e) {
			handle.setWarning(NOEXITSERROR);
			updateErrorHandle(handle);
			throw new AppSystemException("根据调度系统编号、调度作业标识、当前跑批日期获取调度作业信息失败" + etlSysCd);
		}
		//TODO etl_job_hand表中没有priority，该干预似乎不使用？
		// 判断设置的优先度是否符合范围(1~99)
//		int priority = handle.getpInteger.parseInt((String)map.get("priority"));
//		if( priority < 1 || priority > 99 ) {
//			updateErrorHandle(map, "优先度不正确");
//			return;
//		}

		if(Job_Status.RUNNING.getCode().equals(etlJob.getJob_disp_status())) {
			handle.setWarning(STATEERROR);
			updateErrorHandle(handle);
		}else {
			//TODO 此处缺少main.HandleJobPriorityChange，因为上一个TODO要先解决
			updateDoneHandle(handle);
		}
	}

	/**
	 * 用于干预类型为：作业跳过JJ（JOB_JUMP）标识的处理。
	 * @note    1、检查该次干预的参数是否正确、该作业是否已经登记；
	 *          2、干预作业状态除[运行中]和[已完成]的作业。
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @param handle    Etl_job_hand，表示干预信息
	 */
	private void handleJobskip(Etl_job_hand handle) {

		//1、检查该次干预的参数是否正确、该作业是否已经登记；
		Optional<Etl_job_cur> etlJobOptional = analyzeParameter(handle.getEtl_hand_type(), handle.getPro_para());
		if(!etlJobOptional.isPresent()) {
			logger.warn("{} 任务分析参数异常，{}", handle.getEtl_job(), PARAERROR);
			return;
		}
		String etlSysCd = handle.getEtl_sys_cd();
		String etlJobStr = handle.getEtl_job();
		String currBathDate = etlJobOptional.get().getCurr_bath_date();
		Etl_job_cur etlJob;
		try{
			etlJob = TaskSqlHelper.getEtlJob(etlSysCd, etlJobStr, currBathDate);
		}catch (AppSystemException e) {
			handle.setWarning(NOEXITSERROR);
			updateErrorHandle(handle);
			throw new AppSystemException("根据调度系统编号、调度作业标识、当前跑批日期获取调度作业信息失败" + etlSysCd);
		}
		//2、干预作业状态除[运行中]和[已完成]的作业。
		if(Job_Status.RUNNING.getCode().equals(etlJob.getJob_disp_status()) ||
				Job_Status.DONE.getCode().equals(etlJob.getJob_disp_status())) {
			handle.setWarning(STATEERROR);
			updateErrorHandle(handle);
		}else {
			taskManager.handleJob2Skip(currBathDate, etlJobStr);
			updateDoneHandle(handle);
		}
	}

	private void handleGroupPause(Etl_job_hand handle) {

//		List<Map<String, Object>> gpMap = handleMapper.queryGroupJobs(map);
//		for(Map<String, Object> gp : gpMap) {
//			Map<String, Object> job = handleMapper.queryJob(gp);
//			if( job.get("job_disp_status").equals("R") ) {
//				stopJob(job);
//			}
//			else {
//				job.put("job_disp_status", "P");
//				dao.stopJobStatus(job);
//			}
//		}
//
//		updateDoneHandle(handle);
	}

	/**
	 * 用于干预类型：系统日切SF（SYS_SHIFT）标识的处理。
	 * @note    1、系统日切干预；
	 *          2、更新调度作业干预表，干预完成。
	 * @author Tiger.Wang
	 * @date 2019/9/11
	 * @param handle    Etl_job_hand，表示干预信息
	 */
	private void handleSysShift(Etl_job_hand handle) {
		//系统日切干预
		taskManager.handleSysDayShift();
		//更新干预表
		updateDoneHandle(handle);
	}

	/**
	 * 该方法根据调度作业列表，检查作业列表是否都已经运行完成。注意，此处会再次查询数据库来获取最新的作业状态。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param etlJobs   调度作业列表
	 * @return boolean  作业是否已经全部运行完成
	 */
	private boolean checkJobsIsStop(List<Etl_job_cur> etlJobs) {

		//TODO 此处有个问题，查询出来的作业是否都有当前跑批日期curr_bath_date，此处使用curr_bath_date查询
		for(Etl_job_cur job : etlJobs) {
			try{
				job = TaskSqlHelper.getEtlJob(job.getEtl_sys_cd(), job.getEtl_job(), job.getCurr_bath_date());
			}catch (AppSystemException e) {
				throw new AppSystemException("在检查作业是否为停止状态时发生异常，该作业不存在：" + job.getEtl_job());
			}
			if(Job_Status.RUNNING.getCode().equals(job.getJob_disp_status())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 该方法根据调度作业列表来结束作业。注意，该方法会在系统级别结束（杀死）作业，同时会更新调度作业状态到停止状态。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param etlJobs   调度作业列表
	 */
	private void stopRunningJobs(List<Etl_job_cur> etlJobs) {

		for(Etl_job_cur etlJob : etlJobs) {
			closeProcessById(etlJob.getJob_process_id(), etlJob.getPro_type());
			TaskSqlHelper.updateEtlJobDispStatus(Job_Status.STOP.getCode(), etlJob.getEtl_sys_cd(),
					etlJob.getEtl_job());
		}
	}

	/**
	 * 根据进程编号关闭进程（结束作业）。注意，此方法会判断作业类型来觉得结束作业的方式。
	 * @note 1、当作业类型为Yarn时，意味着该任务在yarn上运行，使用杀死yarn作业的方式来结束作业；
	 *       2、当作业类型不为Yarn时，意味着该任务在本地系统上运行，使用linux质量来结束作业。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param processId 进程编号
	 * @param proType   作业类型
	 * @return boolean  作业是否成功结束
	 */
	private boolean closeProcessById(String processId, String proType) {

		if(Pro_Type.Yarn.getCode().equals(proType)) {
			logger.info("Will close job, process id is {}", processId);
			try {
				//TODO 此处缺少YarnUtil.killApplicationByid(processId)，待补充
				return true;
			}
			catch(Exception e) {
				e.printStackTrace();
				return false;
			}
		}else {
			if(StringUtil.isEmpty(processId)) return true;

			String cmd = KILL9COMMANDLINE + " " + processId;
			try {
				Runtime.getRuntime().exec(cmd); //执行命令
			}
			catch(IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}

	/**
	 * 根据任务/作业的干预类型，解析参数字符串，得出某个作业的跑批日期及优先级。
	 * 注意，此处返回的Etl_job只包含当前跑批日期（curr_bath_date）及作业优先级（priority）。
	 * @author Tiger.Wang
	 * @date 2019/9/9
	 * @param handleType    干预类型
	 * @param paraStr   参数字符串
	 * @return java.util.Optional<hrds.commons.entity.Etl_job>  作业对象
	 */
	private Optional<Etl_job_cur> analyzeParameter(String handleType, String paraStr) {

		if(StringUtil.isEmpty(paraStr)) {
			return Optional.empty();
		}
		String[] paraArray = paraStr.split(TaskManager.PARASEPARATOR);
		Etl_job_cur etlJob = new Etl_job_cur();
		switch (handleType){
			case JT:
			case JS:
			case JR:
			case JJ:
				if(3 == paraArray.length){
					etlJob.setCurr_bath_date(paraArray[2]);
				}else {
					etlJob.setCurr_bath_date(DEFAULT_BATH_DATE);
				}
				break;
			case JP:
				if(4 == paraArray.length){
					etlJob.setCurr_bath_date(paraArray[2]);
					etlJob.setJob_priority(paraArray[3]);
				}else {
					etlJob.setCurr_bath_date(DEFAULT_BATH_DATE);
					etlJob.setJob_priority(TaskManager.DEFAULT_PRIORITY);
				}
				break;
			case SS:
			case SP:
			case SO:
			case SR:
				if(2 == paraArray.length) {
					etlJob.setCurr_bath_date(paraArray[1]);
				}
				else {
					etlJob.setCurr_bath_date(DEFAULT_BATH_DATE);
				}
				break;
			default:
				return Optional.empty();
		}

		return Optional.of(etlJob);
	}

	/**
	 * 当任务/作业干预失败时，使用此方法来更新干预状态。
	 * @author Tiger.Wang
	 * @date 2019/9/9
	 * @param etlJobHand    Etl_job_hand对象
	 */
	private void updateErrorHandle(Etl_job_hand etlJobHand) {

		etlJobHand.setHand_status(Meddle_status.ERROR.getCode());
		etlJobHand.setMain_serv_sync(Main_Server_Sync.NO.getCode());
		updateHandle(etlJobHand);
	}

	/**
	 * 当任务/作业干预完成时，使用此方法来更新干预状态
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param etlJobHand    Etl_job_hand对象
	 */
	private void updateDoneHandle(Etl_job_hand etlJobHand) {

		etlJobHand.setHand_status(Meddle_status.DONE.getCode());
		etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
		updateHandle(etlJobHand);
	}

	/**
	 * 当任务/作业需要干预为运行中时，使用此方法来更新干预状态
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param etlJobHand    Etl_job_hand对象
	 */
	private void updateRunningHandle(Etl_job_hand etlJobHand) {

		etlJobHand.setHand_status(Meddle_status.RUNNING.getCode());
		etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
		updateHandle(etlJobHand);
	}

	/**
	 * 该方法用于更新调度作业干预信息。注意：1、此方法会更新调度作业干预表，以及记录干预历史；
	 * 2、该方法依赖于传入的etlJobHand参数，会直接使用该对象，这意味着你可以为该对象设置任意的值来更新数据到数据库中。
	 * @note    1、更新调度作业干预表（etl_job_hand）；
	 *          2、调度作业干预历史表（etl_job_hand_his）中新增一条记录；
	 *          3、删除调度作业干预表（etl_job_hand）信息。
	 * @author Tiger.Wang
	 * @date 2019/9/10
	 * @param etlJobHand    Etl_job_hand对象，表示一个作业干预
	 */
	private void updateHandle(Etl_job_hand etlJobHand) {

		etlJobHand.setEnd_time(DateUtil.getDateTime(hrds.control.utils.DateUtil.DATETIME));
		//TODO 此处第三步既然要删除，为什么第一步要更新
		//1、更新调度作业干预表（etl_job_hand）。
		TaskSqlHelper.updateEtlJobHandle(etlJobHand);
		//2、调度作业干预历史表（etl_job_hand_his）中新增一条记录。
		Etl_job_hand_his etlJobHandHis = new Etl_job_hand_his();
		etlJobHandHis.setEvent_id(PrimayKeyGener.getNextId());
		etlJobHandHis.setEtl_sys_cd(etlJobHand.getEtl_sys_cd());
		etlJobHandHis.setEtl_job(etlJobHand.getEtl_job());
		etlJobHandHis.setEtl_hand_type(etlJobHand.getEtl_hand_type());
		etlJobHandHis.setPro_para(etlJobHand.getPro_para());
		etlJobHandHis.setHand_status(etlJobHand.getHand_status());
		etlJobHandHis.setSt_time(etlJobHand.getSt_time());
		etlJobHandHis.setEnd_time(etlJobHand.getEnd_time());
		etlJobHandHis.setWarning(etlJobHand.getWarning());
		etlJobHandHis.setMain_serv_sync(etlJobHand.getMain_serv_sync());
		TaskSqlHelper.insertIntoEtlJobHandleHistory(etlJobHandHis);
		//3、删除调度干预表（etl_job_hand）信息。
		TaskSqlHelper.deleteEtlJobHand(etlJobHand.getEtl_sys_cd(), etlJobHand.getEtl_job(),
				etlJobHand.getEtl_hand_type());
	}
}
