package hrds.control.task.helper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import hrds.control.utils.YarnUtil;

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
//	/**干预类型，分组级暂停标识*/
//	private static final String GP = "GP";
//	/**干预类型，分组级重跑，从源头开始标识*/
//	private static final String GC = "GC";
//	/**干预类型，系统重跑标识*/
//	private static final String GR = "GR";
	private static final String SB = "SB";
	/**干预类型，系统日切SF（SYS_SHIFT）标识*/
	private static final String SF = "SF";

//	private static final String DEFAULT_BATH_DATE = "0";    //默认跑批日期

	private static final String PARAERROR = "干预参数错误"; //参数错误信息
	private static final String NOEXITSERROR = "作业不存在"; //任务不存在错误信息
	private static final String NOSUPPORT = "不支持的干预类型"; //干预类型不存在错误信息
	private static final String STATEERROR = "当前状态不允许执行此操作"; //状态异常错误信息
	private static final String JOBSTOPERROR = "作业停止失败"; //状态异常错误信息
	private static final String PRIORITYERROR = "作业优先级设置超过范围"; //作业优先级异常

	private static final String KILL9COMMANDLINE = "kill -9";
	private static final int DEFAULT_MILLISECONDS = 5000;   //默认尝试作业停止毫秒数

	private final TaskManager taskManager;

	/**
	 * TaskJobHandleHelper类构造器。<br>
	 * 1.初始化类变量。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param taskManager
	 *          含义：TaskManager类实例，意味着这两个类共同使用，不可分割。
	 *          取值范围：不能为null。
	 */
	public TaskJobHandleHelper(TaskManager taskManager) {

		//1.初始化类变量。
		this.taskManager = taskManager;
	}

	/**
	 * 根据干预信息集合，对集合内的每一项进行识别及执行作业干预。<br>
	 * 1.根据传入的系统/作业干预信息，实行干预。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param handles
	 *          含义：表示干预信息集合。
	 *          取值范围：不能为null。
	 */
	public void doHandle(List<Etl_job_hand> handles) {

		//1.根据传入的系统/作业干预信息，实行干预。
		for(Etl_job_hand handle : handles) {
			logger.info("检测到作业干预，作业名为 {}，干预类型为 {}",
					handle.getEtl_job(), handle.getEtl_hand_type());
			switch (handle.getEtl_hand_type()) {
				case JT:handleRunning(handle);break;    //作业触发
				case SO:handleSysRerun(handle);break;   //系统重跑
				case SP:handleSysPause(handle);break;   //系统暂停
				case SR:handleSysResume(handle);break;  //系统续跑
				case SB:                                //TODO 原版代码，跟   SS 处理逻辑一模一样
				case SS:handleSysStopAll(handle);break; //系统停止
				case JS:handleJobStop(handle);break;    //作业停止
				case JR:handleJobRerun(handle);break;   //作业重跑
				case JP:handleJobPriority(handle);break;//作业临时调整优先级
				case JJ:handleJobskip(handle);break;    //作业跳过
//				case GP:break;
//				case GC:break;
//				case GR:break;
				case SF:handleSysShift(handle);break;   //系统日切
				default:{
					//TODO 较原版改动：增加了以下代码块，响应不支持的干预类型问题
					logger.warn("{}  {}，{}", handle.getEtl_job(), handle.getEtl_hand_type(), NOSUPPORT);
					handle.setWarning(NOSUPPORT);
					updateErrorHandle(handle);
				} break;
			}
		}
	}

	/**
	 * 用于干预类型为：作业直接触发JT（JOB_TRIGGER）的处理。注意，
	 * 此方法会更新调度作业干预表信息。<br>
	 * 1.解析调度作业干预表的参数信息；<br>
	 * 2.检查该次干预的作业是否存在；<br>
	 * 3.对作业状态为挂起、等待的作业进行干预。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param handle
	 *          含义：表示一次干预。
	 *          取值范围：不能为null。
	 */
	private void handleRunning(Etl_job_hand handle) {

		//1.解析调度作业干预表的参数信息。
		Optional<Etl_job_cur> etlJobOptional = analyzeParameter(handle.getEtl_hand_type(),
				handle.getPro_para());
		if(!etlJobOptional.isPresent()) {
			logger.warn("{}任务分析参数异常，{}", handle.getEtl_job(), PARAERROR);
			handle.setWarning(PARAERROR);
			updateErrorHandle(handle);
			return;
		}
		//2.检查该次干预的作业是否存在。
		String etlSysCd = handle.getEtl_sys_cd();
		String etlJobStr = handle.getEtl_job();
		String currBathDate = etlJobOptional.get().getCurr_bath_date();
		Etl_job_cur etlJobCur;
		try{
			etlJobCur = TaskSqlHelper.getEtlJob(etlSysCd, etlJobStr, currBathDate);
		}catch(AppSystemException e) {
			handle.setWarning(NOEXITSERROR);
			updateErrorHandle(handle);
			return;
		}
		/*
		 * 3.对作业状态为挂起、等待的作业进行干预。
		 *      一、若作业状态为挂起、等待中，则更新干预状态为运行中，则调用TaskManager的干预接口，进行干预；
		 *      二、否则，更新调度作业干预表，该次干预出现异常。
		 */
		if(Job_Status.PENDING.getCode().equals(etlJobCur.getJob_disp_status()) ||
				Job_Status.WAITING.getCode().equals(etlJobCur.getJob_disp_status())) {
			//更新调度作业干预表（etl_job_hand）。
			handle.setHand_status(Meddle_status.RUNNING.getCode());
			handle.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			handle.setEnd_time(DateUtil.getDateTime(DateUtil.DATETIME_DEFAULT));
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
	 * 用于干预类型为：系统重跑SO（SYS_ORIGINAL）标识的处理。注意，此方法会更新调度作业干预表信息。<br>
	 * 1.检查调度系统状态是否已暂停，以及是否还存在未完成或未停止的作业；<br>
	 * 2.将所有作业置为挂起状态（PENDING）；<br>
	 * 3.重新设置内存表（map）的作业状态；<br>
	 * 4.取消调度系统暂停状态；<br>
	 * 5.更新调度作业干预表，干预完成。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param handle
	 *          含义：表示一次干预。
	 *          取值范围：不能为null。
	 */
	private void handleSysRerun(Etl_job_hand handle) {

		String etlSysCd = handle.getEtl_sys_cd();
		//1.检查调度系统状态是否已暂停，以及是否还存在未完成或未停止的作业
		if(!taskManager.isSysPause()) {
			logger.warn("在进行重跑干预时，系统[{}]不是暂停状态，{}", etlSysCd, STATEERROR);
			handle.setWarning(STATEERROR);
			updateErrorHandle(handle);
			return;
		}
		List<Etl_job_cur> etlJobs = TaskSqlHelper.getReadyEtlJobs(etlSysCd);
		if(0 != etlJobs.size()) {
			// 有未完成或停止的job
			logger.warn("在进行重跑干预时，[{}]有未完成或未停止的作业，{}",
					handle.getEtl_job(), STATEERROR);
			handle.setWarning(STATEERROR);
			updateErrorHandle(handle);
			return;
		}
		//2.将所有作业置为挂起状态（PENDING）
		TaskSqlHelper.updateEtlJobToPending(etlSysCd);
		//3.重新设置内存表（map）的作业状态
		taskManager.handleSys2Rerun();
		//4.取消调度系统暂停状态
		taskManager.closeSysPause();
		//5.更新调度作业干预表信息
		updateDoneHandle(handle);
	}

	/**
	 * 用于干预类型为：系统暂停SP（SYS_PAUSE）标识的处理。注意，此方法会更新调度作业干预表信息。<br>
	 * 1.检查调度系统是否已经是暂停状态；<br>
	 * 2.修改内存表（map）中的作业状态；<br>
	 * 3.将调度作业表中的所有作业的作业状态标识为[停止]；<br>
	 * 4.停止所有已经在运行中的作业；<br>
	 * 5.暂停调度系统的运行；<br>
	 * 6.更新调度作业干预表，干预完成。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param handle
	 *          含义：表示一次干预。
	 *          取值范围：不能为null。
	 */
	private void handleSysPause(Etl_job_hand handle) {

		//1.检查调度系统是否已经是暂停状态
		if(taskManager.isSysPause()){
			logger.warn("在进行系统暂停干预时，[{}]已经是暂停状态，{}",
					handle.getEtl_job(), STATEERROR);
			handle.setWarning(STATEERROR);
			updateErrorHandle(handle);
			return;
		}
		//2.修改内存表（map）中的作业状态
		taskManager.handleSys2Pause();
		//3.将调度作业表中的所有作业的作业状态为[等待、挂起]的作业置为[停止]，
		TaskSqlHelper.updateReadyEtlJobStatus(handle.getEtl_sys_cd(), Job_Status.STOP.getCode());
		//4.停止所有已经在运行中的作业
		List<Etl_job_cur> etlJobs = TaskSqlHelper.getEtlJobsByJobStatus(handle.getEtl_sys_cd(),
				Job_Status.RUNNING.getCode());
		if(etlJobs.size() != 0) {
			stopRunningJobs(etlJobs);
			try {
				do {
					Thread.sleep(DEFAULT_MILLISECONDS);
				}while(checkJobsNotStop(etlJobs));
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		//5.开启调度系统的运行
		taskManager.openSysPause();
		//6.更新调度作业干预表，干预完成
		updateDoneHandle(handle);
	}

	/**
	 * 用于干预类型为：系统续跑SR（SYS_RESUME）标识的处理。注意，此方法会更新调度作业干预表信息。<br>
	 * 1.判断当前状态是否是暂停状态，不是暂停状态时不能重跑；<br>
	 * 2.将STOP/ERROR作业置为挂起状态（PENDING）；<br>
	 * 3.重新设置内存表（map）的作业状态；<br>
	 * 4.取消调度系统暂停状态；<br>
	 * 5.更新调度作业干预表信息。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param handle
	 *          含义：表示一次干预。
	 *          取值范围：不能为null。
	 */
	private void handleSysResume(Etl_job_hand handle) {

		//1.判断当前状态是否是暂停状态，不是暂停状态时不能重跑
		if(!taskManager.isSysPause()) {
			logger.warn("在进行续跑干预时，[{}]不是暂停状态，{}",
					handle.getEtl_job(), STATEERROR);
			handle.setWarning(STATEERROR);
			updateErrorHandle(handle);
			return;
		}

		String etlSysCd = handle.getEtl_sys_cd();
		List<Etl_job_cur> etlJobs = TaskSqlHelper.getReadyEtlJobs(etlSysCd);
		if(0 != etlJobs.size()) {
			// 有未完成或停止的job
			logger.warn("在进行续跑干预时，[{}]有未完成或未停止的作业，{}",
					handle.getEtl_job(), STATEERROR);
			handle.setWarning(STATEERROR);
			updateErrorHandle(handle);
			return;
		}
		//2.将STOP/ERROR作业置为挂起状态（PENDING）
		TaskSqlHelper.updateEtlJobToPendingInResume(etlSysCd, Job_Status.PENDING.getCode());
		//3.重新设置内存表（map）的作业状态
		taskManager.handleSys2Resume();
		//4.取消调度系统暂停状态
		taskManager.closeSysPause();
		//5.更新调度作业干预表信息
		updateDoneHandle(handle);
	}

	/**
	 * 用于干预类型为：系统停止SS（SYS_STOP）标识的处理。注意，此方法会更新调度作业干预表信息。<br>
	 * 1.将作业状态为PENDING/WAITING的作业置为STOP；<br>
	 * 2.停止全部Running作业；<br>
	 * 3.更新ETL_SYS运行状态为STOP；<br>
	 * 4.更新干预表，干预完成。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param handle
	 *          含义：表示一次干预。
	 *          取值范围：不能为null。
	 */
	private void handleSysStopAll(Etl_job_hand handle) {

		//1.将作业状态为PENDING/WAITING的作业置为STOP
		TaskSqlHelper.updateReadyEtlJobsDispStatus(handle.getEtl_sys_cd(),
				Job_Status.STOP.getCode());
		//2.停止全部Running作业
		List<Etl_job_cur> etlJobs = TaskSqlHelper.getEtlJobsByJobStatus(handle.getEtl_sys_cd(),
				Job_Status.RUNNING.getCode());
		if(0 != etlJobs.size()) {
			stopRunningJobs(etlJobs);
			try {
				do {
					Thread.sleep(DEFAULT_MILLISECONDS);
				}while (checkJobsNotStop(etlJobs));
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		//3.更新ETL_SYS运行状态为STOP
		TaskSqlHelper.updateEtlSysRunStatus(handle.getEtl_sys_cd(), Job_Status.STOP.getCode());
		//4.更新干预表，干预完成
		updateDoneHandle(handle);
	}

	/**
	 * 用于干预类型为：作业停止JS（JOB_STOP）标识的处理。<br>
	 * 1.检查该次干预的参数是否正确、该作业是否已经登记；<br>
	 * 2.若干预的作业已经在运行中，则要结束该作业，并更新作业表的作业状态；<br>
	 * 3.若干预的作业还未运行（挂起和等待中），则更新内存表（map）及数据库的作业状态；
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param handle
	 *          含义：表示一次干预。
	 *          取值范围：不能为null。
	 */
	private void handleJobStop(Etl_job_hand handle) {

		//1.检查该次干预的参数是否正确、该作业是否已经登记；
		Optional<Etl_job_cur> etlJobOptional = analyzeParameter(handle.getEtl_hand_type(),
				handle.getPro_para());
		if(!etlJobOptional.isPresent()) {
			logger.warn("{}任务分析参数异常，{}", handle.getEtl_job(), PARAERROR);
			handle.setWarning(PARAERROR);
			updateErrorHandle(handle);
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
			return;
		}
		//2.若干预的作业已经在运行中，则要结束该作业，并更新作业表的作业状态；
		if(Job_Status.RUNNING.getCode().equals(etlJob.getJob_disp_status())) {
			//Running状态job停止，将干预的信息置为Running
			updateRunningHandle(handle);
			//关闭作业进程（停止作业），TODO 作业（非yarn）若不与trigger在一台机器，是否无法停止
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
				while(checkJobsNotStop(Collections.singletonList(etlJob)));
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
			//停止后的作业状态设为stop
			TaskSqlHelper.updateEtlJobDispStatus(Job_Status.STOP.getCode(),
					etlJob.getEtl_sys_cd(), etlJob.getEtl_job());
		}else if(Job_Status.PENDING.getCode().equals(etlJob.getJob_disp_status()) ||
				Job_Status.WAITING.getCode().equals(etlJob.getJob_disp_status())) {
			//3.若干预的作业还未运行（挂起和等待中），则更新内存表（map）及数据库的作业状态；
			taskManager.handleJob2Stop(etlJob.getCurr_bath_date(), handle.getEtl_job());
			updateDoneHandle(handle);
		}else {
			//否则干预失败，记录失败原因
			handle.setWarning(STATEERROR);
			updateErrorHandle(handle);
		}
	}

	/**
	 * 用于干预类型为：作业重跑JR（JOB_RERUN）标识的处理。<br>
	 * 1.检查该次干预的参数是否正确、该作业是否已经登记；<br>
	 * 2.干预作业状态为停止、错误、完成的作业。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param handle
	 *          含义：表示一次干预。
	 *          取值范围：不能为null。
	 */
	private void handleJobRerun(Etl_job_hand handle) {

		//1.检查该次干预的参数是否正确、该作业是否已经登记；
		Optional<Etl_job_cur> etlJobOptional = analyzeParameter(handle.getEtl_hand_type(),
				handle.getPro_para());
		if(!etlJobOptional.isPresent()) {
			logger.warn("{} 任务分析参数异常，{}", handle.getEtl_job(), PARAERROR);
			handle.setWarning(PARAERROR);
			updateErrorHandle(handle);
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
			return;
		}
		//2.干预作业状态为停止、错误、完成的作业。
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

	/**
	 * 用于干预类型为：作业临时调整优先级JP（JOB_PRIORITY）标识的处理。<br>
	 * 1.检查该次干预的参数是否正确、该作业是否已经登记；<br>
	 * 2.检查该次干预所指定的作业优先级是否在合法范围内；<br>
	 * 3.若被干预的作业不在运行中，则更新内存Map以及更新数据库中的作业状态。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param handle
	 *          含义：表示一次干预。
	 *          取值范围：不能为null。
	 */
	private void handleJobPriority(Etl_job_hand handle) {

		//1.检查该次干预的参数是否正确、该作业是否已经登记；
		Optional<Etl_job_cur> etlJobOptional = analyzeParameter(handle.getEtl_hand_type(),
				handle.getPro_para());
		if(!etlJobOptional.isPresent()) {
			logger.warn("{} 任务分析参数异常，{}", handle.getEtl_job(), PARAERROR);
			handle.setWarning(PARAERROR);
			updateErrorHandle(handle);
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
			return;
		}
		//2.检查该次干预所指定的作业优先级是否在合法范围内；
		int priority = etlJobOptional.get().getJob_priority();
		if( priority < TaskManager.MINPRIORITY || priority > TaskManager.MAXPRIORITY ) {
			handle.setWarning(PRIORITYERROR);
			updateErrorHandle(handle);
			return;
		}
		//3.若被干预的作业不在运行中，则更新内存Map以及更新数据库中的作业状态。
		if(Job_Status.RUNNING.getCode().equals(etlJob.getJob_disp_status())) {
			handle.setWarning(STATEERROR);
			updateErrorHandle(handle);
		}else {
			taskManager.handleJob2ChangePriority(currBathDate, etlJobStr, priority);
			updateDoneHandle(handle);
		}
	}

	/**
	 * 用于干预类型为：作业跳过JJ（JOB_JUMP）标识的处理。<br>
	 * 1.检查该次干预的参数是否正确、该作业是否已经登记；<br>
	 * 2.干预作业状态除[运行中]和[已完成]的作业。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param handle
	 *          含义：表示一次干预。
	 *          取值范围：不能为null。
	 */
	private void handleJobskip(Etl_job_hand handle) {

		//1.检查该次干预的参数是否正确、该作业是否已经登记；
		Optional<Etl_job_cur> etlJobOptional = analyzeParameter(handle.getEtl_hand_type(),
				handle.getPro_para());
		if(!etlJobOptional.isPresent()) {
			logger.warn("{} 任务分析参数异常，{}", handle.getEtl_job(), PARAERROR);
			handle.setWarning(PARAERROR);
			updateErrorHandle(handle);
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
			return;
		}
		//2.干预作业状态除[运行中]和[已完成]的作业。
		if(Job_Status.RUNNING.getCode().equals(etlJob.getJob_disp_status()) ||
				Job_Status.DONE.getCode().equals(etlJob.getJob_disp_status())) {
			handle.setWarning(STATEERROR);
			updateErrorHandle(handle);
		}else {
			taskManager.handleJob2Skip(currBathDate, etlJobStr);
			updateDoneHandle(handle);
		}
	}

	/**
	 * 用于干预类型：系统日切SF（SYS_SHIFT）标识的处理。<br>
	 * 1.系统日切干预；<br>
	 * 2.更新调度作业干预表，干预完成。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param handle
	 *          含义：表示一次干预。
	 *          取值范围：不能为null。
	 */
	private void handleSysShift(Etl_job_hand handle) {

		//1.系统日切干预
		taskManager.handleSysDayShift();
		//2.更新干预表
		updateDoneHandle(handle);
	}

	/**
	 * 该方法根据调度作业列表，检查作业列表是否都已经运行完成。注意，此处会再次查询数据库来获取最新的作业状态。<br>
	 * 1.检查每个作业是否已经运行完成。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlJobs
	 *          含义：调度作业列表。
	 *          取值范围：不能为null。
	 * @return boolean
	 *          含义：作业是否已经全部运行完成。
	 *          取值范围：true/false。
	 */
	private boolean checkJobsNotStop(List<Etl_job_cur> etlJobs) {

		//1.检查每个作业是否已经运行完成。
		for(Etl_job_cur job : etlJobs) {
			try{
				job = TaskSqlHelper.getEtlJob(job.getEtl_sys_cd(), job.getEtl_job(),
						job.getCurr_bath_date());
			}catch (AppSystemException e) {
				throw new AppSystemException("在检查作业是否为停止状态时发生异常，该作业不存在：" +
						job.getEtl_job());
			}
			if(Job_Status.RUNNING.getCode().equals(job.getJob_disp_status())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 该方法根据调度作业列表来结束作业。注意，该方法会在系统级别结束（杀死）作业，同时会更新调度作业状态到停止状态。<br>
	 * 1.杀死传入的作业集合中每个作业；<br>
	 * 2.每个被杀死的作业更新数据库作业状态为[停止]。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlJobs
	 *          含义：调度作业列表。
	 *          取值范围：不能为null。
	 */
	private void stopRunningJobs(List<Etl_job_cur> etlJobs) {

		for(Etl_job_cur etlJob : etlJobs) {
			//1.杀死传入的作业集合中每个作业；
			closeProcessById(etlJob.getJob_process_id(), etlJob.getPro_type());
			//2.每个被杀死的作业更新数据库作业状态为[停止]。
			TaskSqlHelper.updateEtlJobDispStatus(Job_Status.STOP.getCode(), etlJob.getEtl_sys_cd(),
					etlJob.getEtl_job());
		}
	}

	/**
	 * 根据进程编号关闭进程（结束作业）。注意，此方法会判断作业类型来觉得结束作业的方式。<br>
	 * 1、当作业类型为Yarn时，意味着该任务在yarn上运行，使用杀死yarn作业的方式来结束作业；<br>
	 * 2、当作业类型不为Yarn时，意味着该任务在本地系统上运行，使用linux指令来结束作业。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param processId
	 *          含义：进程编号。
	 *          取值范围：不能为null。
	 * @param proType
	 *          含义：作业类型。
	 *          取值范围：Pro_Type枚举值，不能为null。
	 * @return boolean
	 *          含义：作业是否成功结束。
	 *          取值范围：true/false。
	 */
	private boolean closeProcessById(String processId, String proType) {

		//1、当作业类型为Yarn时，意味着该任务在yarn上运行，使用杀死yarn作业的方式来结束作业；
		if(Pro_Type.Yarn.getCode().equals(proType)) {
			try {
				YarnUtil.killApplicationByid(processId);
			}
			catch(Exception e) {
				e.printStackTrace();
				return false;
			}
		}else {
			//2、当作业类型不为Yarn时，意味着该任务在本地系统上运行，使用linux指令来结束作业。
			if(StringUtil.isEmpty(processId)) return true;

			String cmd = KILL9COMMANDLINE + " " + processId;
//			String cmd = "taskkill -PID " + processId + " -F";
			try {
				Runtime.getRuntime().exec(cmd); //执行命令
			}
			catch(IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		logger.info("作业关闭成功，进程号为 {}", processId);
		return true;
	}

	/**
	 * 根据任务/作业的干预类型，解析参数字符串，得出某个作业的跑批日期及优先级。
	 * 注意，此处返回的Etl_job只包含当前跑批日期（curr_bath_date）及作业优先级（priority）。<br>
	 * 1.根据不同的干预类型，进行不同的参数解析方式。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param handleType
	 *          含义：干预类型。
	 *          取值范围：不能为null。
	 * @param paraStr
	 *          含义：参数字符串。
	 *          取值范围：不能为null。
	 * @return java.util.Optional<hrds.commons.entity.Etl_job_cur>
	 *          含义：表示一个作业。
	 *          取值范围：只使用对象的curr_bath_date、job_priority属性。
	 */
	private Optional<Etl_job_cur> analyzeParameter(String handleType, String paraStr) {

		if(StringUtil.isEmpty(paraStr)) {
			return Optional.empty();
		}
		String[] paraArray = paraStr.split(TaskManager.PARASEPARATOR);
		Etl_job_cur etlJob = new Etl_job_cur();
		//1.根据不同的干预类型，进行不同的参数解析方式。
		switch (handleType){
			case JT:
			case JS:
			case JR:
			case JJ:
				if(3 == paraArray.length){
					etlJob.setCurr_bath_date(paraArray[2]);
				}else {
					//TODO 此处较原版改动：不再设置当前跑批日期，而直接返回空，原因有2点：
					// 1、概念上来说，若干预时参数错误，意味着这次干预不是本人的意愿，应该返回参数错误；
					// 2、若参数错误而设置默认跑批日期，意味着该跑批日期在作业登记表中不存在，
					//    含义为该作业不存在，但实际是“作业存在，只是干预时参数写错了”。
//					etlJob.setCurr_bath_date(DEFAULT_BATH_DATE);
					return Optional.empty();
				}
				break;
			case JP:
				if(4 == paraArray.length){
					etlJob.setCurr_bath_date(paraArray[2]);
					etlJob.setJob_priority(paraArray[3]);
				}else {
//					etlJob.setCurr_bath_date(DEFAULT_BATH_DATE);
//					etlJob.setJob_priority(TaskManager.DEFAULT_PRIORITY);
					return Optional.empty();
				}
				break;
			case SS:
			case SP:
			case SO:
			case SR:
				if(2 == paraArray.length) {
					etlJob.setCurr_bath_date(paraArray[1]);
				}else {
//					etlJob.setCurr_bath_date(DEFAULT_BATH_DATE);
					return Optional.empty();
				}
				break;
			default:
				return Optional.empty();
		}

		return Optional.of(etlJob);
	}

	/**
	 * 当任务/作业干预失败时，使用此方法来更新干预状态。<br>
	 * 1.更新干预状态。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlJobHand
	 *          含义：表示一次干预的信息。
	 *          取值范围：不能为null。
	 */
	private void updateErrorHandle(Etl_job_hand etlJobHand) {

		//1.更新干预状态。
		etlJobHand.setHand_status(Meddle_status.ERROR.getCode());
		etlJobHand.setMain_serv_sync(Main_Server_Sync.NO.getCode());
		updateHandle(etlJobHand);
	}

	/**
	 * 当任务/作业干预完成时，使用此方法来更新干预状态。<br>
	 * 1.更新干预状态。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlJobHand
	 *          含义：表示一次干预的信息。
	 *          取值范围：不能为null。
	 */
	private void updateDoneHandle(Etl_job_hand etlJobHand) {

		//1.更新干预状态。
		etlJobHand.setHand_status(Meddle_status.DONE.getCode());
		etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
		updateHandle(etlJobHand);
	}

	/**
	 * 当任务/作业需要干预为运行中时，使用此方法来更新干预状态。<br>
	 * 1.更新干预状态。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlJobHand
	 *          含义：表示一次干预的信息。
	 *          取值范围：不能为null。
	 */
	private void updateRunningHandle(Etl_job_hand etlJobHand) {

		//1.更新干预状态。
		etlJobHand.setHand_status(Meddle_status.RUNNING.getCode());
		etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
		TaskSqlHelper.updateEtlJobHandle(etlJobHand);
	}

	/**
	 * 该方法用于更新调度作业干预信息。注意：1、此方法会更新调度作业干预表，以及记录干预历史；
	 * 2、该方法依赖于传入的etlJobHand参数，会直接使用该对象，这意味着你可以为该对象设置任意的值来更新数据到数据库中。<br>
	 * 1.更新调度作业干预表（etl_job_hand）；<br>
	 * 2.调度作业干预历史表（etl_job_hand_his）中新增一条记录；<br>
	 * 3.删除调度作业干预表（etl_job_hand）信息。
	 * @author Tiger.Wang
	 * @date 2019/10/9
	 * @param etlJobHand
	 *          含义：表示一个作业干预。
	 *          取值范围：不能为null。
	 */
	private void updateHandle(Etl_job_hand etlJobHand) {

		etlJobHand.setEnd_time(DateUtil.getDateTime(DateUtil.DATETIME_DEFAULT));
		//TODO 此处第三步既然要删除，为什么第一步要更新
		//1.更新调度作业干预表（etl_job_hand）。
		TaskSqlHelper.updateEtlJobHandle(etlJobHand);
		//2.调度作业干预历史表（etl_job_hand_his）中新增一条记录。
		Etl_job_hand_his etlJobHandHis = new Etl_job_hand_his();
		etlJobHandHis.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
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
		//3.删除调度干预表（etl_job_hand）信息。
		TaskSqlHelper.deleteEtlJobHand(etlJobHand.getEtl_sys_cd(), etlJobHand.getEtl_job(),
				etlJobHand.getEtl_hand_type());
	}
}
