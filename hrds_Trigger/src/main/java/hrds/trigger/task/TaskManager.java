package hrds.trigger.task;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.Job_Status;
import hrds.commons.codes.Main_Server_Sync;
import hrds.commons.codes.Meddle_status;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.trigger.beans.EtlJobParaAnaly;
import hrds.trigger.task.executor.TaskExecutor;
import hrds.trigger.task.helper.RedisHelper;
import hrds.trigger.task.helper.TaskSqlHelper;

/**
 * ClassName: TaskManager<br>
 * Description: trigger程序核心逻辑的管理类，用于管理系统/作业的状态，并且提供作业执行入口。<br>
 * Author: Tiger.Wang<br>
 * Date: 2019/10/23 11:56<br>
 * Since: JDK 1.8
 **/
public class TaskManager {

	private static final Logger logger = LogManager.getLogger();

	private final String etlSysCode;
	private final String strRunningJob;	//存入redis中的键值（标识需要马上执行的作业）
	private final String strFinishedJob;	//存入redis中的键值（标识已经停止的作业）

	private final static String RUNNINGJOBFLAG = "RunningJob";
	private final static String FINISHEDJOBFLAG = "FinishedJob";
	private final static String REDISCONTENTSEPARATOR = "@";	//redis字符内容分隔符
	private final static String REDISHANDLE = "Handle"; //redis已干预标识
	private final static String ERRORJOBMSG = "作业执行失败";
	/**干预类型，作业直接触发JT（JOB_TRIGGER）标识*/
	private final static String JT = "JT";

	private static final RedisHelper REDIS = RedisHelper.getInstance();
	private static final ExecutorService executeThread = Executors.newCachedThreadPool();

	public TaskManager(String etlSysCode) {

		this.etlSysCode = etlSysCode;
		this.strRunningJob = etlSysCode + RUNNINGJOBFLAG;
		this.strFinishedJob = etlSysCode + FINISHEDJOBFLAG;
	}

	/**
	 * 用于判断Trigger程序是否应该继续执行。主要判断点：<br>
	 * 1、调度系统编号所表示的调度系统是否存在；<br>
	 * 2、系统的运行状态是否是[停止]状态。
	 * @author Tiger.Wang
	 * @date 2019/10/23
	 * @return boolean
	 *          含义：Trigger程序是否应该继续执行。
	 *          取值范围：true/false。
	 */
	public boolean checkSysGoRun() {

		try {
			//1、调度系统编号所表示的调度系统是否存在；
			Etl_sys etlSys = TaskSqlHelper.getEltSysBySysCode(etlSysCode);
			//2、系统的运行状态是否是[停止]状态。
			if(Job_Status.STOP.getCode().equals(etlSys.getSys_run_status())) {
				logger.warn("----- 调度系统编号为{}的系统已是停止状态，系统停止 -----", etlSysCode);
				return false;
			}

			return true;
		}catch (AppSystemException e) {
			logger.error("没有对应的调度系统，调度系统编号为{}", etlSysCode);
			return false;
		}
	}

	/**
	 * 用于对redis中的作业数据进行分析，将其转换为EtlJobParaAnaly对象，主要逻辑点：<br>
	 * 1、从redis中lpop1个作业描述字符串，并将其按固定分隔符分割；<br>
	 * 2、根据分割后的字符，使用代表作业的作业标识，去数据库中获取完整作业信息；<br>
	 * 3、将作业信息封装为EtlJobParaAnaly对象。
	 * @author Tiger.Wang
	 * @date 2019/10/23
	 * @return hrds.trigger.beans.EtlJobParaAnaly
	 *          含义：代表一个作业。
	 *          取值范围：不会为null，hasEtlJob字段不会为null。
	 */
	public EtlJobParaAnaly getEtlJob() {

		EtlJobParaAnaly etlJobParaAnaly = new EtlJobParaAnaly();
		etlJobParaAnaly.setHasEtlJob(false);
		//1、从redis中lpop1个作业描述字符串，并将其按固定分隔符分割；
		long runningListSize = REDIS.llen(strRunningJob);
		if(runningListSize < 1) {
			logger.info("------ 没有被登记的可运行作业 ------");
			return etlJobParaAnaly;
		}

		String runningJobStr = REDIS.lpop(strRunningJob);
		if(StringUtil.isEmpty(runningJobStr)) {
			logger.info("------ 未被分配到可运行作业 ------");
			return etlJobParaAnaly;
		}

		//2、根据分割后的字符，使用代表作业的作业标识，去数据库中获取完整作业信息；
		//下标0表示一个作业的作业标识、下标1表示一个作业的跑批日期，下标2标识一个作业是否存在干预
		String[] jobKey = runningJobStr.split(REDISCONTENTSEPARATOR);
		if (jobKey.length < 2 || jobKey.length > 3){
			logger.warn("------ 错误参数的可运行作业：{} ------", Arrays.toString(jobKey));
			return etlJobParaAnaly;
		}

		//3、将作业信息封装为EtlJobParaAnaly对象。
		String etlJob = jobKey[0];
		String currBathDate = jobKey[1];
		try {
			Etl_job_cur etlJobCur = TaskSqlHelper.getEtlJob(etlSysCode, etlJob, currBathDate);
			if(jobKey.length == 3) etlJobParaAnaly.setHasHandle(REDISHANDLE.equals(jobKey[2]));
			etlJobParaAnaly.setEtlJobCur(etlJobCur);
			etlJobParaAnaly.setHasEtlJob(true);
			return etlJobParaAnaly;
		}catch (AppSystemException e) {
			logger.warn("{} 作业不存在", etlJob);
			return etlJobParaAnaly;
		}
	}

	/**
	 * 以线程方式启动一个作业的执行，主要是用于管理一个作业生命周期中各种状态的更新，
	 * 作业结束后会释放资源，若作业是以干预的途径启动，会更新干预信息。主要逻辑点：<br>
	 * 1、将待执行的作业更新至运行中状态中，并开始执行作业；<br>
	 * 2、作业执行完后，更新作业状态，包括：更新调度历史信息、更新作业状态、推送结束标识到redis；<br>
	 * 3、作业执行完后，如果作业是干预途径来启动，修改干预信息。
	 * @author Tiger.Wang
	 * @date 2019/10/25
	 * @param etlJobCur
	 *          含义：表示一个已登记，并且需要执行的作业。
	 *          取值范围：不能为null。
	 * @param hasHandle
	 *          含义：标识一个需要执行的作业，是否是通过干预途径而来的。
	 *          取值范围：true/false。
	 */
	public void runEtlJob(final Etl_job_cur etlJobCur, final boolean hasHandle) {

		executeThread.execute(() -> {

			String etlJob = etlJobCur.getEtl_job();
			try {
				String currDateTime = DateUtil.getDateTime(DateUtil.DATETIME_DEFAULT);
				etlJobCur.setCurr_st_time(currDateTime);
				//1、将待执行的作业更新至运行中状态中，并开始执行作业；
				TaskSqlHelper.updateEtlJob2Running(etlSysCode, etlJob, currDateTime);

				logger.info("{} 作业开始执行，开始执行时间为 {}", etlJob, currDateTime);
				Etl_job_cur etlJobCurResult = TaskExecutor.executeEtlJob(etlJobCur);

				//进程返回0，意味着正常结束 TODO 注意，目前仅支持作业的[正确结束、异常结束]两种状态
				if(TaskExecutor.PROGRAM_DONE_FLAG == etlJobCurResult.getJob_return_val()) {
					etlJobCurResult.setJob_disp_status(Job_Status.DONE.getCode());
				}else {
					logger.warn("{} 作业异常结束", etlJob);
					etlJobCurResult.setJob_disp_status(Job_Status.ERROR.getCode());
				}

				currDateTime = DateUtil.getDateTime(DateUtil.DATETIME_DEFAULT);
				etlJobCurResult.setCurr_end_time(currDateTime);
				etlJobCurResult.setLast_exe_time(currDateTime);

				//2、作业执行完后，更新作业状态，包括：更新调度历史信息、更新作业状态、推送结束标识到redis；
				freedEtlJob(etlJobCurResult);
			}catch(IOException | InterruptedException e) {
				logger.warn("{} 作业异常结束并修改作业状态", etlJobCur.getEtl_job());
				e.printStackTrace();
				etlJobCur.setJob_disp_status(Job_Status.ERROR.getCode());
				String currDateTime = DateUtil.getDateTime(DateUtil.DATETIME_DEFAULT);
				etlJobCur.setCurr_end_time(currDateTime);
				etlJobCur.setLast_exe_time(currDateTime);
				freedEtlJob(etlJobCur);
			}catch(Exception e) {
				logger.warn("errorHappens", e);
			}

			//TODO 此处是否有问题（连带着control），对于任何干预，干预的状态应该与作业本身的执行状态无关，
			// 即"干预成功了，但是作业执行成功/失败了"，trigger不应该理会干预的的状态问题。
			//3、作业执行完后，如果作业是干预途径来启动，修改干预信息。
			if(hasHandle) {

				Optional<Etl_job_hand> etlJobHandOptional =
						TaskSqlHelper.getEtlJobHandle(etlSysCode, etlJob, JT);
				if(!etlJobHandOptional.isPresent()) {
					logger.warn("{} 该作业的干预无法查询到，干预处理将会忽略", etlJob);
					return;
				}

				Etl_job_hand etlJobHand = etlJobHandOptional.get();
				if(Job_Status.DONE.getCode().equals(etlJobCur.getJob_disp_status())) {
					etlJobHand.setHand_status(Meddle_status.DONE.getCode());
					etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				}else if(Job_Status.ERROR.getCode().equals(etlJobCur.getJob_disp_status())) {
					etlJobHand.setHand_status(Meddle_status.ERROR.getCode());
					etlJobHand.setMain_serv_sync(Main_Server_Sync.NO.getCode());
					etlJobHand.setWarning(ERRORJOBMSG);
				}

				TaskManager.updateHandle(etlJobHand);
			}
		});
	}

	/**
	 * 使用于在作业执行完成后，更新作业为完成状态。主要逻辑点：<br>
	 * 1、记录作业调度历史；<br>
	 * 2、更新此作业所登记的信息（etl_job_cur表）为结束状态；<br>
	 * 3、更新此作业所定义的信息（etl_job_def表）的最近执行日期时间；<br>
	 * 4、以结束标识推送该作业到redis。
	 * @author Tiger.Wang
	 * @date 2019/10/25
	 * @param etlJobCur
	 *          含义：表示一个执行结束的作业。
	 *          取值范围：不能为null。
	 */
	private void freedEtlJob(Etl_job_cur etlJobCur) {

		//1、记录作业调度历史；
		TaskSqlHelper.insertIntoEltJobDispHis(TaskManager.etlJobCur2EtlJobDispHis(etlJobCur));
		//2、更新此作业所登记的信息（etl_job_cur表）为结束状态；
		TaskSqlHelper.updateEtlJob2Complete(etlJobCur.getJob_disp_status(),
				etlJobCur.getCurr_end_time(), etlJobCur.getJob_return_val(),
				etlJobCur.getLast_exe_time(), etlJobCur.getEtl_sys_cd(), etlJobCur.getEtl_job(),
				etlJobCur.getCurr_bath_date());
		//3、更新此作业所定义的信息（etl_job_def表）的最近执行日期时间；
		TaskSqlHelper.updateEtlJobDefLastExeTime(etlJobCur.getLast_exe_time(),
				etlJobCur.getEtl_sys_cd(), etlJobCur.getEtl_job());

		//4、以结束标识推送该作业到redis。
		String finishedJob =
				etlJobCur.getEtl_job() + REDISCONTENTSEPARATOR + etlJobCur.getCurr_bath_date();
		REDIS.rpush(strFinishedJob, finishedJob);
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
	private static void updateHandle(final Etl_job_hand etlJobHand) {

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

	/**
	 * 将表示已登记作业的Etl_job_cur对象，转为表示作业调度历史的Etl_job_disp_his对象；
	 * @author Tiger.Wang
	 * @date 2019/10/25
	 * @param etlJobCur
	 *          含义：表示已登记的一个作业。
	 *          取值范围：不能为null。
	 * @return hrds.commons.entity.Etl_job_disp_his
	 *          含义：表示一个作业的调度历史。
	 *          取值范围：不会为null。
	 */
	private static Etl_job_disp_his etlJobCur2EtlJobDispHis(final Etl_job_cur etlJobCur) {

		Etl_job_disp_his etlJobDispHis = new Etl_job_disp_his();
		etlJobDispHis.setEtl_sys_cd(etlJobCur.getEtl_sys_cd());
		etlJobDispHis.setEtl_job(etlJobCur.getEtl_job());
		etlJobDispHis.setCurr_bath_date(etlJobCur.getCurr_bath_date());
		etlJobDispHis.setSub_sys_cd(etlJobCur.getSub_sys_cd());
		etlJobDispHis.setEtl_job_desc(etlJobCur.getEtl_job_desc());
		etlJobDispHis.setPro_type(etlJobCur.getPro_type());
		etlJobDispHis.setPro_dic(etlJobCur.getPro_dic());
		etlJobDispHis.setPro_name(etlJobCur.getPro_name());
		etlJobDispHis.setPro_para(etlJobCur.getPro_para());
		etlJobDispHis.setLog_dic(etlJobCur.getLog_dic());
		etlJobDispHis.setDisp_freq(etlJobCur.getDisp_freq());
		etlJobDispHis.setDisp_offset(etlJobCur.getDisp_offset());
		etlJobDispHis.setDisp_type(etlJobCur.getDisp_type());
		etlJobDispHis.setDisp_time(etlJobCur.getDisp_time());
		etlJobDispHis.setJob_eff_flag(etlJobCur.getJob_eff_flag());
		etlJobDispHis.setJob_priority(etlJobCur.getJob_priority());
		etlJobDispHis.setJob_disp_status(etlJobCur.getJob_disp_status());
		etlJobDispHis.setCurr_st_time(etlJobCur.getCurr_st_time());
		etlJobDispHis.setCurr_end_time(etlJobCur.getCurr_end_time());
		etlJobDispHis.setOverlength_val(etlJobCur.getOverlength_val());
		etlJobDispHis.setOvertime_val(etlJobCur.getOvertime_val());
		etlJobDispHis.setComments(etlJobCur.getComments());
		etlJobDispHis.setToday_disp(etlJobCur.getToday_disp());
		etlJobDispHis.setExe_frequency(etlJobCur.getExe_frequency());
		etlJobDispHis.setExe_num(etlJobCur.getExe_num());
		etlJobDispHis.setCom_exe_num(etlJobCur.getCom_exe_num());
		etlJobDispHis.setLast_exe_time(etlJobCur.getLast_exe_time());
		etlJobDispHis.setStar_time(etlJobCur.getStar_time());
		etlJobDispHis.setEnd_time(etlJobCur.getEnd_time());

		return etlJobDispHis;
	}
}
