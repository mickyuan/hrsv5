package hrds.control.task;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.*;

import fd.ng.core.utils.DateUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.control.task.helper.TaskSqlHelper;

/**
 * 用于测试TaskManager类，注意，该类需要配合trigger进行测试，请不要单独测试该类。
 * @ClassName: hrds.control.task.TaskManagerTest
 * @Author: Tiger.Wang
 * @Date: 2019/9/2 14:06
 * @Since: JDK 1.8
 **/
public class TaskManagerTest {

	private static final Logger logger = LogManager.getLogger();

	public static final String syscode = "110";
	private static final String currBathDate = LocalDate.now().format(DateUtil.DATE_DEFAULT);

	private static final String SLEEP1S_SHELL = "HelloWord.sh";
	private static final String SLEEP1M_SHELL = "HelloWordWaitLongTime.sh";
	private static final String FAUIL_SHELL = "HelloWordFailure.sh";

	private static TaskManager taskManager;
	private static List<Etl_job_def> etlJobDefs = new ArrayList<>();

	@Before
	public void before() {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			for(int i = 0 ; i < 5 ; i++) {
				//作业定义表
				Etl_job_def etlJobDef = new Etl_job_def();
				etlJobDef.setEtl_sys_cd(syscode);
				etlJobDef.setEtl_job(String.valueOf(i));
				etlJobDef.setSub_sys_cd(syscode);
				etlJobDef.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
				etlJobDef.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
				etlJobDef.setDisp_type(Dispatch_Type.TPLUS0.getCode());
				etlJobDef.setCom_exe_num(0);
				etlJobDef.setDisp_offset(i);
				etlJobDef.setJob_priority(i);
				etlJobDef.setJob_priority_curr(i);
				etlJobDef.setCurr_bath_date(currBathDate);
				etlJobDef.setPro_type(Pro_Type.SHELL.getCode());
				etlJobDef.setPro_dic("/mnt/d/");
				etlJobDef.setPro_name(SLEEP1S_SHELL);
				etlJobDef.setLog_dic("D:\\");
//				etlJobDef.setDisp_time("235959");
//				etlJobDef.setExe_frequency(1);
				etlJobDef.add(db);
				etlJobDefs.add(etlJobDef);

				Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
				etlJobResourceRela.setEtl_sys_cd(syscode);
				etlJobResourceRela.setEtl_job(String.valueOf(i));
				etlJobResourceRela.setResource_type("type" + i);
				etlJobResourceRela.setResource_req(i);
				etlJobResourceRela.add(db);

				//资源表
				Etl_resource resource = new Etl_resource();
				resource.setEtl_sys_cd(syscode);
				resource.setResource_type(etlJobResourceRela.getResource_type());
				resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				resource.setResource_max(10);
				resource.add(db);
			}
			//虚作业
			Etl_job_def etlJobDef = new Etl_job_def();
			etlJobDef.setEtl_sys_cd(syscode);
			etlJobDef.setEtl_job("98");
			etlJobDef.setSub_sys_cd(syscode);
			etlJobDef.setJob_eff_flag(Job_Effective_Flag.VIRTUAL.getCode());
			etlJobDef.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS1.getCode());
			etlJobDef.setCom_exe_num(0);
			etlJobDef.setDisp_offset(1);
			etlJobDef.setJob_priority(1);
			etlJobDef.setJob_priority_curr(1);
			etlJobDef.setPro_type(Pro_Type.SHELL.getCode());
			etlJobDef.setCurr_bath_date(currBathDate);
//			etlJobDef.setExe_frequency(1);
			etlJobDef.add(db);
			etlJobDefs.add(etlJobDef);

			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job("98");
			etlJobResourceRela.setResource_type("type98");
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			SqlOperator.commitTransaction(db);
		}

		//TODO 问题1，对于不同的构造参数，应该如何测试
		taskManager = new TaskManager(syscode, currBathDate, false, false);
	}

	@After
	public void after() {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			int num = SqlOperator.execute(db, "DELETE FROM etl_resource WHERE etl_sys_cd = ? ", syscode);
			logger.info("清理etl_resource表{}条数据", num);

			num = SqlOperator.execute(db, "DELETE FROM etl_job_def WHERE etl_sys_cd = ? ", syscode);
			logger.info("清理etl_job_def表{}条数据", num);

			num = SqlOperator.execute(db, "DELETE FROM etl_job_resource_rela WHERE etl_sys_cd = ? ", syscode);
			logger.info("清理etl_job_resource_rela表{}条数据", num);

			num = SqlOperator.execute(db, "DELETE FROM etl_job_cur WHERE etl_sys_cd = ? ", syscode);
			logger.info("清理etl_job_cur表{}条数据", num);

			num = SqlOperator.execute(db, "DELETE FROM etl_job_disp_his WHERE etl_sys_cd = ? ", syscode);
			logger.info("清理etl_job_disp_his表{}条数据", num);

			num = SqlOperator.execute(db,
					"DELETE FROM etl_job_hand WHERE etl_sys_cd = ?", syscode);
			logger.info("清理etl_job_hand表{}条数据", num);

			num = SqlOperator.execute(db,
					"DELETE FROM etl_job_hand_his WHERE etl_sys_cd = ?", syscode);
			logger.info("清理etl_job_hand_his表{}条数据", num);

			SqlOperator.commitTransaction(db);
		}
	}

	@BeforeClass
	public static void beforeSomething() {

		try(DatabaseWrapper db = new DatabaseWrapper()) {
			Etl_sys etlSys = new Etl_sys();
			etlSys.setEtl_sys_cd(syscode);
			etlSys.setEtl_sys_name("测试1");
			etlSys.setEtl_serv_ip("127.0.0.1");
			etlSys.setEtl_serv_port("8088");
			etlSys.setUser_id("1001");
			etlSys.setCurr_bath_date(DateUtil.getDateTime(DateUtil.DATETIME_DEFAULT));
			etlSys.setBath_shift_time(LocalDate.now().plusDays(1).format(DateUtil.DATE_DEFAULT));
			etlSys.setSys_run_status(Job_Status.STOP.getCode());
			etlSys.setUser_name("smk");
			etlSys.setUser_pwd("q1w2e3");
			etlSys.add(db);
			SqlOperator.commitTransaction(db);
		}
	}

	@AfterClass
	public static void finallySomething() {

		try(DatabaseWrapper db = new DatabaseWrapper()) {
			int num = SqlOperator.execute(db, "DELETE FROM etl_sys WHERE etl_sys_cd = ? ", syscode);
			SqlOperator.commitTransaction(db);
			logger.info("清理etl_sys_rela表{}条数据", num);
		}

		TaskSqlHelper.closeDbConnector();
	}

	@Test
	public void publishReadyJob() {

		//TODO 这个测试用例需要trigger来执行任务，并且有执行结果后，该程序才能继续往下走。
		taskManager.initEtlSystem();
		taskManager.loadReadyJob();
		taskManager.publishReadyJob();

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			for (Etl_job_def etlJobDef : etlJobDefs) {

				Etl_job_cur etlJobCur = SqlOperator.queryOneObject(db, Etl_job_cur.class,
						"SELECT * FROM etl_job_cur WHERE etl_sys_cd = ? AND etl_job = ?",
						etlJobDef.getEtl_sys_cd(), etlJobDef.getEtl_job())
						.orElseThrow(() -> new AppSystemException("无法在etl_job_cur查询到数据："
								+ etlJobDef.getEtl_job()));

				assertEquals("测试当前作业已经结束时，作业是否为完成状态" + etlJobDef.getEtl_job(),
						Job_Status.DONE.getCode(), etlJobCur.getJob_disp_status());
			}
		}
	}

	@Test
	public void handleJob2Run() {

		//作业干预类型为[直接触发]的作业，仅在作业还未运行过（未来才会第一次运行）时才能使用。
		try(DatabaseWrapper db = new DatabaseWrapper()) {

			//正确的数据访问1、干预已存在的作业，且该作业还未执行。
			String handleEtlJob = "JobRun";
			Etl_job_def etlJobDef = new Etl_job_def();
			etlJobDef.setEtl_sys_cd(syscode);
			etlJobDef.setEtl_job(handleEtlJob);
			etlJobDef.setSub_sys_cd(syscode);
			etlJobDef.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			etlJobDef.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS1.getCode());
			etlJobDef.setCom_exe_num(0);
			etlJobDef.setDisp_time("000001");
			etlJobDef.setDisp_offset(1);    //偏移量跟DAILY没关系
			etlJobDef.setJob_priority(100);
			etlJobDef.setJob_priority_curr(100);
			etlJobDef.setCurr_bath_date(currBathDate);
			etlJobDef.setPro_type(Pro_Type.SHELL.getCode());
			etlJobDef.setPro_dic("/mnt/d/");
			etlJobDef.setPro_name(SLEEP1S_SHELL);
			etlJobDef.setLog_dic("D:\\");
			etlJobDef.setExe_frequency(1);
			etlJobDef.add(db);

			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job(handleEtlJob);
			etlJobResourceRela.setResource_type(handleEtlJob + 100);
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			Etl_resource resource = new Etl_resource();
			resource.setEtl_sys_cd(syscode);
			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			resource.setResource_max(10);
			resource.add(db);

			Etl_job_hand etlJobHand = new Etl_job_hand();
			etlJobHand.setEvent_id(PrimayKeyGener.getNextId());
			etlJobHand.setEtl_sys_cd(syscode);
			etlJobHand.setEtl_job(handleEtlJob);
			etlJobHand.setPro_para(syscode + "," + handleEtlJob + "," + currBathDate);
			etlJobHand.setEtl_hand_type("JT");
			etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
			etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			etlJobHand.add(db);

			//错误的数据访问1、干预不存在的作业。
			String noExitEtlJob = "NoExitEtlJob";
			etlJobHand.setEvent_id(PrimayKeyGener.getNextId());
			etlJobHand.setEtl_job(noExitEtlJob);
			etlJobHand.setPro_para(syscode + "," + noExitEtlJob + "," + currBathDate);
			etlJobHand.add(db);

			//错误的数据访问2、干预设置错误的作业，该干预不允许对已执行的作业操作
			String errorStatusEtlJob = "errorStatusEtlJob";
			etlJobDef.setEtl_job(errorStatusEtlJob);
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS0.getCode());
			etlJobDef.add(db);

			etlJobResourceRela.setEtl_job(errorStatusEtlJob);
			etlJobResourceRela.setResource_type(errorStatusEtlJob + 100);
			etlJobResourceRela.add(db);

			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.add(db);

			Thread thread = new Thread(() -> {

				logger.info("--------------- 沉睡4秒 ---------------");
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				etlJobHand.setEvent_id(PrimayKeyGener.getNextId());
				etlJobHand.setEtl_job(errorStatusEtlJob);
				etlJobHand.setPro_para(syscode + "," + errorStatusEtlJob + "," + currBathDate);
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);
			});

			thread.start();

			//错误的数据访问3、干预设置错误的作业，该干预不允许[干预参数]错误
			String errorParaEtlJob = "errorParaEtlJob";
			etlJobDef.setEtl_job(errorParaEtlJob);
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS0.getCode());
			etlJobDef.add(db);

			etlJobResourceRela.setEtl_job(errorParaEtlJob);
			etlJobResourceRela.setResource_type(errorParaEtlJob + 100);
			etlJobResourceRela.add(db);

			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.add(db);

			etlJobHand.setEvent_id(PrimayKeyGener.getNextId());
			etlJobHand.setEtl_job(errorParaEtlJob);
			etlJobHand.setPro_para(syscode + "," + errorParaEtlJob + "," + currBathDate + ",1");
			etlJobHand.add(db);

			SqlOperator.commitTransaction(db);

			taskManager.initEtlSystem();
			taskManager.loadReadyJob();
			taskManager.publishReadyJob();

			Etl_job_disp_his etlJobDispHis = SqlOperator.queryOneObject(db, Etl_job_disp_his.class,
					"SELECT * FROM etl_job_disp_his WHERE etl_sys_cd = ? AND etl_job = ?"
					, syscode, handleEtlJob).orElseThrow(() ->
					new AppSystemException("测试作业干预类型为[直接触发]的作业失败"));

			assertEquals("测试作业干预类型为[直接触发]的作业，在正确数据情况下作业是否完成",
					Job_Status.DONE.getCode(), etlJobDispHis.getJob_disp_status());

			Etl_job_hand_his etlJobHandHis = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ?"
					, syscode, handleEtlJob).orElseThrow(() ->
					new AppSystemException("测试作业干预类型为[直接触发]的作业失败"));

			assertEquals("测试作业干预类型为[直接触发]的作业，在正确数据情况下是否干预完成",
					Meddle_status.DONE.getCode(), etlJobHandHis.getHand_status());

			List<Etl_job_hand_his> etlJobHandHiss = SqlOperator.queryList(db,
					Etl_job_hand_his.class, "SELECT * FROM etl_job_hand_his " +
							"WHERE etl_sys_cd = ? AND (etl_job = ? OR etl_job = ? OR etl_job = ?)",
					syscode, noExitEtlJob, errorStatusEtlJob, errorParaEtlJob);

			for(Etl_job_hand_his etlJobDispHis1 : etlJobHandHiss) {
				assertEquals("测试作业干预类型为[直接触发]的作业，在错误数据情况下是否干预错误",
						Meddle_status.ERROR.getCode(), etlJobDispHis1.getHand_status());
			}
		}
	}

	@Test
	public void handleSys2Rerun() {

		//TODO 系统级干预跟etl_job无关，该字段不应该作为主键
		//作业干预类型为[系统重跑]的作业，若想成功干预，必须先系统暂停。
		try(DatabaseWrapper db = new DatabaseWrapper()) {

			String errorStatusEtlJob = "errorStatusEtlJob";
			String handleEtlJob = "handleEtlJob";
			Thread thread = new Thread(() -> {
				logger.info("--------------- 沉睡4秒 ---------------");
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//错误的数据访问1、进行干预时，系统不暂停，干预将会失败。
				Etl_job_hand etlJobHand = new Etl_job_hand();
				etlJobHand.setEtl_job(errorStatusEtlJob);
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_hand_type("SO");
				etlJobHand.setEvent_id(PrimayKeyGener.getNextId());
				etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
				etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etlJobHand.add(db);

				//正确的数据访问1、进行干预时，系统暂停，干预会成功。
				etlJobHand.setEvent_id(PrimayKeyGener.getNextId());
				etlJobHand.setEtl_job(handleEtlJob);
				etlJobHand.setEtl_hand_type("SP");
				etlJobHand.add(db);

				etlJobHand.setEvent_id(PrimayKeyGener.getNextId());
				etlJobHand.setEtl_hand_type("SO");
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);
			});

			thread.start();

			taskManager.initEtlSystem();
			taskManager.loadReadyJob();
			taskManager.publishReadyJob();

			List<Etl_job_hand_his> etlJobHandHiss = SqlOperator.queryList(db,
					Etl_job_hand_his.class, "SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? " +
							"AND etl_job = ?", syscode, handleEtlJob);

			for(Etl_job_hand_his etlJobHandHis : etlJobHandHiss) {
				assertEquals("测试作业干预类型为[系统重跑]的作业，在正确数据情况下是否干预完成",
						Meddle_status.DONE.getCode(), etlJobHandHis.getHand_status());
			}

			Etl_job_hand_his etlJobHandHis = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ?"
					, syscode, errorStatusEtlJob).orElseThrow(() ->
					new AppSystemException("测试作业干预类型为[直接触发]的作业失败"));

			assertEquals("测试作业干预类型为[系统重跑]的作业，在错误数据情况下是否干预错误",
					Meddle_status.ERROR.getCode(), etlJobHandHis.getHand_status());
		}
	}

	@Test
	public void handleSys2Pause() {

		//作业干预类型为[系统暂停]的作业，含义为：
		// 1、对于作业状态为[运行中]的作业，会设置该作业状态为[停止]，但是[运行中]的作业已经发布到redis中，
		//    对于已发布的作业，该干预无效；
		// 2、对于作业状态为[挂起]的作业，会设置该作业状态为[停止]，该作业不再执行。
		//系统级别的干预应该无作业名
		try(DatabaseWrapper db = new DatabaseWrapper()) {
			//正确的数据访问1、
			String handleEtlJob = "SystemPause";
			Etl_job_def etlJobDef = new Etl_job_def();
			etlJobDef.setEtl_sys_cd(syscode);
			etlJobDef.setEtl_job(handleEtlJob);
			etlJobDef.setSub_sys_cd(syscode);
			etlJobDef.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			etlJobDef.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS0.getCode());
			etlJobDef.setCom_exe_num(0);
			etlJobDef.setDisp_time("000001");
			etlJobDef.setDisp_offset(1);    //TODO 偏移量跟DAILY没关系
			etlJobDef.setJob_priority(100);
			etlJobDef.setJob_priority_curr(100);
			etlJobDef.setCurr_bath_date(currBathDate);
			etlJobDef.setPro_type(Pro_Type.SHELL.getCode());
			etlJobDef.setPro_dic("/mnt/d/");
			etlJobDef.setPro_name(SLEEP1S_SHELL);
			etlJobDef.setLog_dic("D:\\");
			etlJobDef.setExe_frequency(1);
			etlJobDef.add(db);

			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job(handleEtlJob);
			etlJobResourceRela.setResource_type(handleEtlJob + 100);
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			Etl_resource resource = new Etl_resource();
			resource.setEtl_sys_cd(syscode);
			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			resource.setResource_max(10);
			resource.add(db);

			SqlOperator.commitTransaction(db);

			Thread thread = new Thread(() -> {
				logger.info("--------------- 沉睡4秒 ---------------");
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Etl_job_hand etlJobHand = new Etl_job_hand();
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_job(handleEtlJob);
				etlJobHand.setPro_para(syscode + "," + handleEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("SP");
				etlJobHand.setEvent_id(PrimayKeyGener.getNextId());
				etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
				etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etlJobHand.add(db);

				etlJobHand.setEtl_hand_type("SR");
				etlJobHand.setEvent_id(PrimayKeyGener.getNextId());
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);
			});

			thread.start();

			taskManager.initEtlSystem();
			taskManager.loadReadyJob();
			taskManager.publishReadyJob();

			Etl_job_disp_his etlJobDispHis = SqlOperator.queryOneObject(db, Etl_job_disp_his.class,
					"SELECT * FROM etl_job_disp_his WHERE etl_sys_cd = ? AND etl_job = ?"
					, syscode, handleEtlJob).orElseThrow(() ->
					new AppSystemException("测试作业干预类型为[系统暂停]的作业失败"));

			assertEquals("测试作业干预类型为[系统续跑]的作业，特定的作业是否已经完成，作业名为："
					+ handleEtlJob, Job_Status.DONE.getCode(), etlJobDispHis.getJob_disp_status());
		}
	}

	@Test
	public void handleSys2Resume() {
		//因为在handleSys2Pause方法中进行了测试，此处不再测试
	}

	@Test
	public void handleJob2Stop() {
		//作业干预类型为[系统重跑]的作业。
		//TODO 此方法无法测试，原因：
		// 1、作业停止涉及到指令：kill -9，windows下没这个指令；
		// 2、指定停止的作业应该为长作业，意思为，该干预必须在作业执行中触发（不能初始化数据）
		String handleEtlJob = "100";
		try(DatabaseWrapper db = new DatabaseWrapper()) {
			//作业定义表
			Etl_job_def etlJobDef = new Etl_job_def();
			etlJobDef.setEtl_sys_cd(syscode);
			etlJobDef.setEtl_job(handleEtlJob);
			etlJobDef.setSub_sys_cd(syscode);
			etlJobDef.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			etlJobDef.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS0.getCode());
			etlJobDef.setCom_exe_num(0);
			etlJobDef.setJob_priority(100);
			etlJobDef.setJob_priority_curr(100);
			etlJobDef.setCurr_bath_date(currBathDate);
			etlJobDef.setPro_type(Pro_Type.SHELL.getCode());
			etlJobDef.setPro_dic("/mnt/d/");
			etlJobDef.setPro_name(SLEEP1M_SHELL);
			etlJobDef.setLog_dic("D:\\");
			etlJobDef.add(db);

			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job(handleEtlJob);
			etlJobResourceRela.setResource_type(handleEtlJob + 100);
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			Etl_resource resource = new Etl_resource();
			resource.setEtl_sys_cd(syscode);
			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			resource.setResource_max(10);
			resource.add(db);

			SqlOperator.commitTransaction(db);
		}

		taskManager.initEtlSystem();
		taskManager.loadReadyJob();

		Thread thread = new Thread(() -> {
			try(DatabaseWrapper db = new DatabaseWrapper()) {
				logger.info("--------------- 沉睡10秒 ---------------");
				Thread.sleep(10000);

				Etl_job_hand etlJobHand = new Etl_job_hand();
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_job(handleEtlJob);
				etlJobHand.setPro_para(syscode + "," + handleEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JS");
				etlJobHand.setEvent_id(PrimayKeyGener.getNextId());
				etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
				etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etlJobHand.add(db);
				SqlOperator.commitTransaction(db);

				Thread.sleep(10000);
				//为了让测试通过，配合作业重跑使用
				etlJobHand.setEtl_hand_type("JR");
				etlJobHand.setEvent_id(PrimayKeyGener.getNextId());
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		thread.start();

		taskManager.publishReadyJob();
	}

	@Test
	public void handleJob2Rerun() {
		//测试停止的作业
		String handleStopEtlJob = "100";
		//测试错误的作业
		String handleErrorEtlJob = "99";
		try(DatabaseWrapper db = new DatabaseWrapper()) {
			//作业定义表
			Etl_job_def etlJobDef = new Etl_job_def();
			etlJobDef.setEtl_sys_cd(syscode);
			etlJobDef.setEtl_job(handleStopEtlJob);
			etlJobDef.setSub_sys_cd(syscode);
			etlJobDef.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			etlJobDef.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS0.getCode());
			etlJobDef.setCom_exe_num(0);
			etlJobDef.setJob_priority(100);
			etlJobDef.setJob_priority_curr(100);
			etlJobDef.setCurr_bath_date(currBathDate);
			etlJobDef.setPro_type(Pro_Type.SHELL.getCode());
			etlJobDef.setPro_dic("/mnt/d/");
			etlJobDef.setPro_name(SLEEP1M_SHELL);
			etlJobDef.setLog_dic("D:\\");
			etlJobDef.add(db);

			etlJobDef.setEtl_job(handleErrorEtlJob);
			etlJobDef.setPro_name(FAUIL_SHELL);
			etlJobDef.add(db);

			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job(handleStopEtlJob);
			etlJobResourceRela.setResource_type(handleStopEtlJob + 100);
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			Etl_resource resource = new Etl_resource();
			resource.setEtl_sys_cd(syscode);
			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			resource.setResource_max(10);
			resource.add(db);

			etlJobResourceRela.setEtl_job(handleErrorEtlJob);
			etlJobResourceRela.setResource_type(handleErrorEtlJob + 100);
			etlJobResourceRela.add(db);

			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.add(db);

			SqlOperator.commitTransaction(db);
		}

		taskManager.initEtlSystem();
		taskManager.loadReadyJob();

		Thread thread = new Thread(() -> {
			try(DatabaseWrapper db = new DatabaseWrapper()) {
				logger.info("--------------- 沉睡10秒 ---------------");
				Thread.sleep(10000);

				Etl_job_hand etlJobHand = new Etl_job_hand();
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_job(handleStopEtlJob);
				etlJobHand.setPro_para(syscode + "," + handleStopEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JS");
				etlJobHand.setEvent_id(PrimayKeyGener.getNextId());
				etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
				etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etlJobHand.add(db);

				//为了该测试用例能执行结束，现在直接修改etl_job_cur表，正确逻辑是不允许修改这张表，只能等第二天执行
				SqlOperator.execute(db, "UPDATE etl_job_cur SET pro_name = ? " +
						"WHERE etl_sys_cd = ? AND etl_job = ?", "HelloWord.sh", syscode, handleErrorEtlJob);

				SqlOperator.commitTransaction(db);

				Thread.sleep(10000);

				etlJobHand.setEtl_job(handleErrorEtlJob);
				etlJobHand.setPro_para(syscode + "," + handleErrorEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JR");
				etlJobHand.setEvent_id(PrimayKeyGener.getNextId());
				etlJobHand.add(db);

				etlJobHand.setEtl_job(handleStopEtlJob);
				etlJobHand.setPro_para(syscode + "," + handleStopEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JR");
				etlJobHand.setEvent_id(PrimayKeyGener.getNextId());
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		thread.start();

		taskManager.publishReadyJob();
	}

	@Test
	public void handleJob2ChangePriority() {

		String handleEtlJob = etlJobDefs.get(0).getEtl_job();
		try(DatabaseWrapper db = new DatabaseWrapper()) {
			Etl_job_hand etlJobHand = new Etl_job_hand();
			etlJobHand.setEtl_sys_cd(syscode);
			etlJobHand.setEtl_job(handleEtlJob);
			etlJobHand.setPro_para(syscode + "," + handleEtlJob + "," + currBathDate + "," + 98);
			etlJobHand.setEtl_hand_type("JP");
			etlJobHand.setEvent_id(PrimayKeyGener.getNextId());
			etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
			etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			etlJobHand.add(db);

			SqlOperator.commitTransaction(db);
		}

		taskManager.initEtlSystem();
		taskManager.loadReadyJob();
		taskManager.publishReadyJob();
	}

	@Test
	public void handleJob2Skip() {

		String handleEtlJob = "JobSkip";

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			Etl_job_def etlJobDef = new Etl_job_def();
			etlJobDef.setEtl_sys_cd(syscode);
			etlJobDef.setEtl_job(handleEtlJob);
			etlJobDef.setSub_sys_cd(syscode);
			etlJobDef.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			etlJobDef.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS1.getCode());
			etlJobDef.setCom_exe_num(0);
			etlJobDef.setDisp_time("172200");
			etlJobDef.setDisp_offset(1);
			etlJobDef.setJob_priority(100);
			etlJobDef.setJob_priority_curr(100);
			etlJobDef.setCurr_bath_date(currBathDate);
			etlJobDef.setPro_type(Pro_Type.SHELL.getCode());
			etlJobDef.setPro_dic("/mnt/d/");
			etlJobDef.setPro_name(SLEEP1S_SHELL);
			etlJobDef.setLog_dic("D:\\");
			etlJobDef.setExe_frequency(1);
			etlJobDef.add(db);

			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job(handleEtlJob);
			etlJobResourceRela.setResource_type(handleEtlJob + 100);
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			Etl_resource resource = new Etl_resource();
			resource.setEtl_sys_cd(syscode);
			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			resource.setResource_max(10);
			resource.add(db);

			SqlOperator.commitTransaction(db);
		}

		taskManager.initEtlSystem();
		taskManager.loadReadyJob();

		Thread thread = new Thread(() -> {
			try(DatabaseWrapper db = new DatabaseWrapper()) {
				logger.info("--------------- 沉睡10秒 ---------------");
				Thread.sleep(10000);

				Etl_job_hand etlJobHand = new Etl_job_hand();
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_job(handleEtlJob);
				etlJobHand.setPro_para(syscode + "," + handleEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JJ");
				etlJobHand.setEvent_id(PrimayKeyGener.getNextId());
				etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
				etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		thread.start();

		taskManager.publishReadyJob();
	}

	//@Test
	public void handleSysDayShift() {
		//TODO 系统干预日切待确认
		String handleEtlJob = "SysDayShift";

		try(DatabaseWrapper db = new DatabaseWrapper()) {
			logger.info("--------------- 沉睡100秒 ---------------");
//			Thread.sleep(100000);

			Etl_job_hand etlJobHand = new Etl_job_hand();
			etlJobHand.setEtl_sys_cd(syscode);
			etlJobHand.setEtl_job(handleEtlJob);
			etlJobHand.setPro_para(syscode + "," + handleEtlJob + "," + currBathDate);
			etlJobHand.setEtl_hand_type("SF");
			etlJobHand.setEvent_id(PrimayKeyGener.getNextId());
			etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
			etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			etlJobHand.add(db);

			SqlOperator.commitTransaction(db);
		}
//		catch (InterruptedException e) {
//			e.printStackTrace();
//		}

//		Thread thread = new Thread(() -> {
//
//		});
//
//		thread.start();

//		taskManager.initEtlSystem();
//		taskManager.loadReadyJob();
//		taskManager.publishReadyJob();
//
//		if(taskManager.getSysDateShiftFlag()){
//			taskManager.loadReadyJob();
//			taskManager.publishReadyJob();
//		}
	}
}
