package hrds.trigger.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.trigger.beans.EtlJobParaAnaly;
import hrds.trigger.task.helper.RedisHelper;
import hrds.trigger.task.helper.TaskSqlHelper;

public class TaskManagerTest {

	private static final Logger logger = LogManager.getLogger();

	private static final String etlSysCode = "110";
	private static final String PRO_DIR = "/tmp/";
	private static final String currBathDate = LocalDate.now().format(DateUtil.DATE_DEFAULT);
	private static final long SLEEP_TIME = 1000;   //程序循环执行间隔时间

	private static final String SLEEP1S_SHELL = "EtlSleep1S.sh";
	private static final String SLEEP1M_SHELL = "EtlSleep1M.sh";
	private static final String FAUIL_SHELL = "EtlFailure.sh";

	private static final String SLEEP1S_SHELL_PATH = PRO_DIR + SLEEP1S_SHELL;
	private static final String SLEEP1M_SHELL_PATH = PRO_DIR + SLEEP1M_SHELL;
	private static final String FAUIL_SHELL_PATH = PRO_DIR + FAUIL_SHELL;

	private static final RedisHelper REDIS = RedisHelper.getInstance();

	private static final String strRunningJob = etlSysCode + "RunningJob";//存入redis中的键值（标识需要马上执行的作业）
	private static final String strFinishedJob = etlSysCode + "FinishedJob";//存入redis中的键值（标识已经停止的作业）
	private final static String REDISHANDLE = "Handle"; //redis已干预标识
	private final static String REDISCONTENTSEPARATOR = "@";	//redis字符内容分隔符

	private static TaskManager taskManager;

	@Before
	public void before() {

		taskManager = new TaskManager(etlSysCode);
	}

	@BeforeClass
	public static void beforeSomething() throws IOException {

		File file = new File(SLEEP1S_SHELL_PATH);
		if(file.exists() && !file.delete()){
			throw new AppSystemException("初始化运行环境失败");
		}
		if(!file.createNewFile()) throw new AppSystemException("初始化运行环境失败");
		Files.write(file.toPath(), "#!/bin/bash\nsleep 1s\nexit 0".getBytes());

		file = new File(SLEEP1M_SHELL_PATH);
		if(file.exists() && !file.delete()){
			throw new AppSystemException("初始化运行环境失败");
		}
		if(!file.createNewFile()) throw new AppSystemException("初始化运行环境失败");
		Files.write(file.toPath(), "#!/bin/bash\nsleep 1m\nexit 0".getBytes());

		file = new File(FAUIL_SHELL_PATH);
		if(file.exists() && !file.delete()){
			throw new AppSystemException("初始化运行环境失败");
		}
		if(!file.createNewFile()) throw new AppSystemException("初始化运行环境失败");
		Files.write(file.toPath(), "#!/bin/bash\nexit -1".getBytes());

		try(DatabaseWrapper db = new DatabaseWrapper()) {
			Etl_sys etlSys = new Etl_sys();
			etlSys.setEtl_sys_cd(etlSysCode);
			etlSys.setEtl_sys_name("测试2");
			etlSys.setEtl_serv_ip("");
			etlSys.setEtl_serv_port("");
			etlSys.setUser_id("1001");
			etlSys.setCurr_bath_date(DateUtil.getDateTime());
			etlSys.setBath_shift_time(LocalDate.now().plusDays(1).format(DateUtil.DATE_DEFAULT));
			etlSys.setSys_run_status(Job_Status.RUNNING.getCode());
			etlSys.setUser_name("");
			etlSys.setUser_pwd("");
			etlSys.add(db);
			SqlOperator.commitTransaction(db);
		}
	}

	@After
	public void after() {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			int num = SqlOperator.execute(db, "DELETE FROM etl_resource WHERE etl_sys_cd = ? ", etlSysCode);
			logger.info("清理etl_resource表{}条数据", num);

			num = SqlOperator.execute(db, "DELETE FROM etl_job_resource_rela WHERE etl_sys_cd = ? ", etlSysCode);
			logger.info("清理etl_job_resource_rela表{}条数据", num);

			num = SqlOperator.execute(db, "DELETE FROM etl_job_cur WHERE etl_sys_cd = ? ", etlSysCode);
			logger.info("清理etl_job_cur表{}条数据", num);

			num = SqlOperator.execute(db, "DELETE FROM etl_job_disp_his WHERE etl_sys_cd = ? ", etlSysCode);
			logger.info("清理etl_job_disp_his表{}条数据", num);

			num = SqlOperator.execute(db,
					"DELETE FROM etl_job_hand_his WHERE etl_sys_cd = ?", etlSysCode);
			logger.info("清理etl_job_hand_his表{}条数据", num);

			num = SqlOperator.execute(db,
					"DELETE FROM etl_job_hand WHERE etl_sys_cd = ?", etlSysCode);
			logger.info("清理etl_job_hand表{}条数据", num);

			SqlOperator.commitTransaction(db);
		}
	}

	@AfterClass
	public static void finallySomething() {

		new File(SLEEP1S_SHELL_PATH).deleteOnExit();
		new File(SLEEP1M_SHELL_PATH).deleteOnExit();
		new File(FAUIL_SHELL_PATH).deleteOnExit();

		try(DatabaseWrapper db = new DatabaseWrapper()) {
			int num = SqlOperator.execute(db, "DELETE FROM etl_sys WHERE etl_sys_cd = ? ", etlSysCode);
			SqlOperator.commitTransaction(db);
			logger.info("清理etl_sys_rela表{}条数据", num);
		}

		TaskSqlHelper.closeDbConnector();

		REDIS.close();
	}

	/**
	 * 使用1个能执行成功的作业、1个能执行失败的作业、1个能执行一分钟的作业、
	 * 1个通过干预途径来启动的能执行成功的作业，以此测试在shell程序类型下，
	 * trigger程序的核心逻辑是否执行正确。期望结果：<br>
	 * 1、1个能执行成功的作业，在redis中存在该作业的结束标识，etl_job_cur表中该作业调度状态为完成（D）；<br>
	 * 2、1个能执行失败的作业，在redis中存在该作业的结束标识，etl_job_cur表中该作业调度状态为错误（E）；<br>
	 * 3、1个能执行一分钟的作业，在redis中存在该作业的结束标识，etl_job_cur表中该作业调度状态为完成（D）；<br>
	 * 4、1个通过干预途径来启动的能执行成功的作业，在redis中存在该作业的结束标识，etl_job_cur表中该作业调度状态为完成（D）。
	 * @author Tiger.Wang
	 * @date 2019/10/28
	 */
	@Test
	public void runEtlJob() {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			//资源表
			Etl_resource resource = new Etl_resource();
			resource.setEtl_sys_cd(etlSysCode);
			resource.setResource_type("type");
			resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			resource.setResource_max(100);
			resource.add(db);

			//1个能执行成功的作业
			String sleep1sEtlJob = "Sleep1sEtlJob";
			Etl_job_cur etlJobDef = new Etl_job_cur();
			etlJobDef.setEtl_sys_cd(etlSysCode);
			etlJobDef.setEtl_job(sleep1sEtlJob);
			etlJobDef.setSub_sys_cd(etlSysCode);
			etlJobDef.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			etlJobDef.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS0.getCode());
			etlJobDef.setCom_exe_num(0);
			etlJobDef.setDisp_offset(0);
			etlJobDef.setJob_priority(1);
			etlJobDef.setJob_priority_curr(1);
			etlJobDef.setCurr_bath_date(currBathDate);
			etlJobDef.setPro_type(Pro_Type.SHELL.getCode());
			etlJobDef.setPro_dic(PRO_DIR);
			etlJobDef.setPro_name(SLEEP1S_SHELL);
			etlJobDef.setLog_dic(FileUtil.TEMP_DIR_NAME);
			etlJobDef.add(db);

			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(etlSysCode);
			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(resource.getResource_type());
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			String sleep1sRunningJob = sleep1sEtlJob + REDISCONTENTSEPARATOR + currBathDate;
			REDIS.rpush(strRunningJob, sleep1sRunningJob);

			//1个能执行失败的作业
			String failureEtlJob = "FailureEtlJob";
			etlJobDef.setEtl_job(failureEtlJob);
			etlJobDef.setPro_name(FAUIL_SHELL);
			etlJobDef.add(db);

			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.add(db);

			String failureRunningJob = failureEtlJob + REDISCONTENTSEPARATOR + currBathDate;
			REDIS.rpush(strRunningJob, failureRunningJob);

			//1个能执行一分钟的作业
			String sleep1mEtlJob = "Sleep1mEtlJob";
			etlJobDef.setEtl_job(sleep1mEtlJob);
			etlJobDef.setPro_name(SLEEP1M_SHELL);
			etlJobDef.add(db);

			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.add(db);

			String sleep1mRunningJob = sleep1mEtlJob + REDISCONTENTSEPARATOR + currBathDate;
			REDIS.rpush(strRunningJob, sleep1mRunningJob);

			//1个通过干预途径来启动的能执行成功的作业
			String handledEtlJob = "HandledEtlJob";
			etlJobDef.setEtl_job(handledEtlJob);
			etlJobDef.setPro_name(SLEEP1S_SHELL);
			etlJobDef.setJob_disp_status(Job_Status.ERROR.getCode());
			etlJobDef.add(db);

			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.add(db);

			Etl_job_disp_his etlJobDispHis = TaskManagerTest.etlJobCur2EtlJobDispHis(etlJobDef);
			etlJobDispHis.add(db);

			Etl_job_hand etlJobHand = new Etl_job_hand();
			etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
			etlJobHand.setEtl_sys_cd(etlSysCode);
			etlJobHand.setEtl_job(handledEtlJob);
			etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			etlJobHand.setHand_status(Meddle_status.DONE.getCode());
			etlJobHand.setEtl_hand_type("JT");
			etlJobHand.add(db);

			String handledRunningJob = handledEtlJob + REDISCONTENTSEPARATOR + currBathDate +
							REDISCONTENTSEPARATOR + REDISHANDLE;
			REDIS.rpush(strRunningJob, handledRunningJob);

			SqlOperator.commitTransaction(db);

			Thread thread = new Thread(() -> {
				logger.info("--------------- 沉睡70秒后，系统停止 ---------------");
				try {
					Thread.sleep(70000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				SqlOperator.execute(db, "UPDATE etl_sys SET sys_run_status = ? " +
						"WHERE etl_sys_cd = ?", Job_Status.STOP.getCode(), etlSysCode);
				SqlOperator.commitTransaction(db);
			});

			thread.start();

			while(taskManager.checkSysGoRun()) {

				EtlJobParaAnaly etlJobParaAnaly = taskManager.getEtlJob();
				if (etlJobParaAnaly.isHasEtlJob()) {
					taskManager.runEtlJob(etlJobParaAnaly.getEtlJobCur(),
							etlJobParaAnaly.isHasHandle());
				}
				Thread.sleep(SLEEP_TIME);
			}

			List<String>  redisFinished = new ArrayList<>();
			redisFinished.add(REDIS.lpop(strFinishedJob));
			redisFinished.add(REDIS.lpop(strFinishedJob));
			redisFinished.add(REDIS.lpop(strFinishedJob));
			redisFinished.add(REDIS.lpop(strFinishedJob));

			assertTrue("测试1个能执行成功的作业，redis中是否存在结束标识",
					redisFinished.contains(sleep1sRunningJob));

			assertTrue("测试1个能执行失败的作业，redis中是否存在结束标识",
					redisFinished.contains(failureRunningJob));

			assertTrue("测试1个能执行一分钟的作业，redis中是否存在结束标识",
					redisFinished.contains(sleep1mRunningJob));

			handledRunningJob = handledEtlJob + REDISCONTENTSEPARATOR + currBathDate;
			assertTrue("1个通过干预途径来启动的能执行成功的作业，redis中是否存在结束标识",
					redisFinished.contains(handledRunningJob));

			//1、1个能执行成功的作业，在redis中存在该作业的结束标识，etl_job_cur表中该作业调度状态为完成（D）；
			Etl_job_cur etlJobCur = SqlOperator.queryOneObject(db, Etl_job_cur.class,
					"SELECT * FROM etl_job_cur WHERE etl_sys_cd = ? AND etl_job = ?",
					etlSysCode, sleep1sEtlJob)
					.orElseThrow(() -> new AppSystemException("无法在etl_job_cur中找到作业"));

			assertEquals("测试1个能执行成功的作业，状态是否为完成", Job_Status.DONE.getCode(),
					etlJobCur.getJob_disp_status());

			//2、1个能执行失败的作业，在redis中存在该作业的结束标识，etl_job_cur表中该作业调度状态为错误（E）；
			etlJobCur = SqlOperator.queryOneObject(db,
					Etl_job_cur.class, "SELECT * FROM etl_job_cur " +
							"WHERE etl_sys_cd = ? AND etl_job = ?", etlSysCode, failureEtlJob)
					.orElseThrow(() -> new AppSystemException("无法在etl_job_cur中找到作业"));

			assertEquals("测试1个能执行失败的作业，状态是否为错误", Job_Status.ERROR.getCode(),
					etlJobCur.getJob_disp_status());

			//3、1个能执行一分钟的作业，在redis中存在该作业的结束标识，etl_job_cur表中该作业调度状态为完成（D）；
			etlJobCur = SqlOperator.queryOneObject(db,
					Etl_job_cur.class, "SELECT * FROM etl_job_cur " +
							"WHERE etl_sys_cd = ? AND etl_job = ?", etlSysCode, sleep1mEtlJob)
					.orElseThrow(() -> new AppSystemException("无法在etl_job_cur中找到作业"));

			assertEquals("测试1个能执行一分钟的作业，状态是否为完成", Job_Status.DONE.getCode(),
					etlJobCur.getJob_disp_status());

			//4、1个通过干预途径来启动的能执行成功的作业，在redis中存在该作业的结束标识，etl_job_cur表中该作业调度状态为完成（D）。
			etlJobCur = SqlOperator.queryOneObject(db,
					Etl_job_cur.class, "SELECT * FROM etl_job_cur " +
							"WHERE etl_sys_cd = ? AND etl_job = ?", etlSysCode, handledEtlJob)
					.orElseThrow(() -> new AppSystemException("无法在etl_job_cur中找到作业"));

			assertEquals("1个通过干预途径来启动的能执行成功的作业，状态是否为完成",
					Job_Status.DONE.getCode(), etlJobCur.getJob_disp_status());

		}catch (InterruptedException e) {
			e.printStackTrace();
		}
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
	private static Etl_job_disp_his etlJobCur2EtlJobDispHis(Etl_job_cur etlJobCur) {

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