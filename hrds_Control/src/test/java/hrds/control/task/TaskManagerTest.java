package hrds.control.task;

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
import org.junit.runners.MethodSorters;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.control.task.helper.TaskSqlHelper;

/**
 * 用于测试TaskManager类，注意，该类需要配合trigger进行测试，请不要单独测试该类。
 * 该类也无法在Windows环境下测试，除非安装好Windows的shell环境，并且PRO_DIR变量改为/mnt/d/，
 * 同时将3个shell手动创建到D://目录下，以及调整TaskJobHandleHelper类中667行部分。
 * 此测试类还需要数据库环境、redis环境。
 * @ClassName: hrds.control.task.TaskManagerTest
 * @Author: Tiger.Wang
 * @Date: 2019/9/2 14:06
 * @Since: JDK 1.8
 **/
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TaskManagerTest {

	private static final Logger logger = LogManager.getLogger();

	public static final String syscode = "110";
	private static final String PRO_DIR = "/tmp/";
	private static final String currBathDate = LocalDate.now().format(DateUtil.DATE_DEFAULT);

	private static TaskManager taskManagerAutoShift;
//	private static TaskManager taskManagerNoShift;

	private static final String SLEEP1S_SHELL = "EtlSleep1S.sh";
	private static final String SLEEP1M_SHELL = "EtlSleep1M.sh";
	private static final String FAUIL_SHELL = "EtlFailure.sh";

	private static final String SLEEP1S_SHELL_PATH = PRO_DIR + SLEEP1S_SHELL;
	private static final String SLEEP1M_SHELL_PATH = PRO_DIR + SLEEP1M_SHELL;
	private static final String FAUIL_SHELL_PATH = PRO_DIR + FAUIL_SHELL;

	private static List<Etl_job_def> etlJobDefs = new ArrayList<>();

	/**
	 * 初始化5个能正常执行的作业，1个虚作业。
	 * @author Tiger.Wang
	 * @date 2019/10/11
	 */
	@Before
	public void before() {

		taskManagerAutoShift = new TaskManager(syscode, currBathDate, false, true);
//		taskManagerNoShift = new TaskManager(syscode, currBathDate, false, false);

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
				etlJobDef.setPro_dic(PRO_DIR);
				etlJobDef.setPro_name(SLEEP1S_SHELL);
				etlJobDef.setLog_dic(FileUtil.TEMP_DIR_NAME);
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

			SqlOperator.commitTransaction(db);
		}
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

			num = SqlOperator.execute(db,
					"DELETE FROM etl_dependency WHERE etl_sys_cd = ?", syscode);
			logger.info("清理etl_dependency表{}条数据", num);

			SqlOperator.commitTransaction(db);
		}
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
			etlSys.setEtl_sys_cd(syscode);
			etlSys.setEtl_sys_name("测试1");
			etlSys.setEtl_serv_ip("");
			etlSys.setEtl_serv_port("");
			etlSys.setUser_id("1001");
			etlSys.setCurr_bath_date(DateUtil.getDateTime());
			etlSys.setBath_shift_time(LocalDate.now().plusDays(1).format(DateUtil.DATE_DEFAULT));
			etlSys.setSys_run_status(Job_Status.STOP.getCode());
			etlSys.setUser_name("");
			etlSys.setUser_pwd("");
			etlSys.add(db);
			SqlOperator.commitTransaction(db);
		}
	}

	@AfterClass
	public static void finallySomething() {

		new File(SLEEP1S_SHELL_PATH).deleteOnExit();
		new File(SLEEP1M_SHELL_PATH).deleteOnExit();
		new File(FAUIL_SHELL_PATH).deleteOnExit();

		try(DatabaseWrapper db = new DatabaseWrapper()) {
			int num = SqlOperator.execute(db, "DELETE FROM etl_sys WHERE etl_sys_cd = ? ", syscode);
			SqlOperator.commitTransaction(db);
			logger.info("清理etl_sys_rela表{}条数据", num);
		}

		TaskSqlHelper.closeDbConnector();
	}

	/**
	 * 测试程序在无干预、自动日切情况下的调度系统核心执行逻辑。
	 * @author Tiger.Wang
	 * @date 2019/10/11
	 */
	@Test
	public void apublishReadyJobAutoShift() {

		publishReadyJob(taskManagerAutoShift);
	}

//	/**
//	 * 测试程序在无干预、不自动日切情况下的调度系统核心执行逻辑。
//	 * @author Tiger.Wang
//	 * @date 2019/10/11
//	 */
//	@Test
//	public void apublishReadyJobNoShift() {
//
//		publishReadyJob(taskManagerNoShift);
//	}

	/**
	 * 测试程序在自动日切情况下的调度系统核心中[作业直接触发]干预的执行逻辑。
	 * @author Tiger.Wang
	 * @date 2019/10/12
	 */
	@Test
	public void bhandleJob2RunAutoShift() {

		handleJob2Run(taskManagerAutoShift);
	}

//	/**
//	 * 测试程序在无自动日切情况下的调度系统核心中[作业直接触发]干预的执行逻辑。
//	 * @author Tiger.Wang
//	 * @date 2019/10/12
//	 */
//	@Test
//	public void bhandleJob2RunNoShift() {
//
//		handleJob2Run(taskManagerNoShift);
//	}

	/**
	 * 测试程序在自动日切情况下的调度系统核心中[系统重跑]干预的执行逻辑。
	 * @author Tiger.Wang
	 * @date 2019/10/12
	 */
	@Test
	public void chandleSys2RerunAutoShift() {

		handleSys2Rerun(taskManagerAutoShift);
	}

//	/**
//	 * 测试程序在无自动日切情况下的调度系统核心中[系统重跑]干预的执行逻辑。
//	 * @author Tiger.Wang
//	 * @date 2019/10/12
//	 */
//	@Test
//	public void chandleSys2RerunNoShift() {
//
//		handleSys2Rerun(taskManagerNoShift);
//	}

	/**
	 * 测试程序在自动日切情况下的调度系统核心中[系统暂停]干预的执行逻辑。
	 * @author Tiger.Wang
	 * @date 2019/10/12
	 */
	@Test
	public void dhandleSys2PauseAutoShift() {

		handleSys2Pause(taskManagerAutoShift);
	}

//	/**
//	 * 测试程序在无自动日切情况下的调度系统核心中[系统暂停]干预的执行逻辑。
//	 * @author Tiger.Wang
//	 * @date 2019/10/12
//	 */
//	@Test
//	public void dhandleSys2PauseNoShift() {
//
//		handleSys2Pause(taskManagerNoShift);
//	}

	/**
	 * 测试程序在自动日切情况下的调度系统核心中[系统续跑]干预的执行逻辑。
	 * @author Tiger.Wang
	 * @date 2019/10/12
	 */
	@Test
	public void ehandleSys2ResumeAutoShift() {

		handleSys2Resume(taskManagerAutoShift);
	}

//	/**
//	 * 测试程序在无自动日切情况下的调度系统核心中[系统续跑]干预的执行逻辑。
//	 * @author Tiger.Wang
//	 * @date 2019/10/12
//	 */
//	@Test
//	public void ehandleSys2ResumeNoShift() {
//
//		handleSys2Resume(taskManagerNoShift);
//	}

	/**
	 * 测试程序在自动日切情况下的调度系统核心中[系统停止]干预的执行逻辑。
	 * @author Tiger.Wang
	 * @date 2019/10/12
	 */
	@Test
	public void zhandleSys2StopAutoShift() {

		handleSys2Stop(taskManagerAutoShift);
	}

//	/**
//	 * 测试程序在无自动日切情况下的调度系统核心中[系统停止]干预的执行逻辑。
//	 * @author Tiger.Wang
//	 * @date 2019/10/12
//	 */
//	@Test
//	public void zhandleSys2StopNoShift() {
//
//		handleSys2Stop(taskManagerNoShift);
//	}

	/**
	 * 测试程序在自动日切情况下的调度系统核心中[作业停止]干预的执行逻辑。
	 * @author Tiger.Wang
	 * @date 2019/10/12
	 */
	@Test
	public void fhandleJob2StopAutoShift() {

		handleJob2Stop(taskManagerAutoShift);
	}

//	/**
//	 * 测试程序在无自动日切情况下的调度系统核心中[作业停止]干预的执行逻辑。
//	 * @author Tiger.Wang
//	 * @date 2019/10/12
//	 */
//	@Test
//	public void fhandleJob2StopNoShift() {
//
//		handleJob2Stop(taskManagerNoShift);
//	}

	/**
	 * 测试程序在自动日切情况下的调度系统核心中[作业重跑]干预的执行逻辑。
	 * @author Tiger.Wang
	 * @date 2019/10/12
	 */
	@Test
	public void ghandleJob2RerunAutoShift() {

		handleJob2Rerun(taskManagerAutoShift);
	}

//	/**
//	 * 测试程序在无自动日切情况下的调度系统核心中[作业重跑]干预的执行逻辑。
//	 * @author Tiger.Wang
//	 * @date 2019/10/12
//	 */
//	@Test
//	public void ghandleJob2RerunNoShift() {
//
//		handleJob2Rerun(taskManagerNoShift);
//	}

	/**
	 * 测试程序在自动日切情况下的调度系统核心中[作业优先级调整]干预的执行逻辑。
	 * @author Tiger.Wang
	 * @date 2019/10/12
	 */
	@Test
	public void hhandleJob2ChangePriorityAutoShift() {

		handleJob2ChangePriority(taskManagerAutoShift);
	}

//	/**
//	 * 测试程序在无自动日切情况下的调度系统核心中[作业优先级调整]干预的执行逻辑。
//	 * @author Tiger.Wang
//	 * @date 2019/10/12
//	 */
//	@Test
//	public void hhandleJob2ChangePriorityNoShift() {
//
//		handleJob2ChangePriority(taskManagerNoShift);
//	}

	/**
	 * 测试程序在自动日切情况下的调度系统核心中[作业跳过]干预的执行逻辑。
	 * @author Tiger.Wang
	 * @date 2019/10/12
	 */
	@Test
	public void ihandleJob2SkipAutoShift() {

		handleJob2Skip(taskManagerAutoShift);
	}

//	/**
//	 * 测试程序在无自动日切情况下的调度系统核心中[作业跳过]干预的执行逻辑。
//	 * @author Tiger.Wang
//	 * @date 2019/10/12
//	 */
//	@Test
//	public void ihandleJob2SkipNoShift() {
//
//		handleJob2Skip(taskManagerNoShift);
//	}

	/**
	 * 使用5个能执行成功、1个虚作业、1个能执行失败、1个能执行成功且上一作业执行成功的依赖作业、
	 * 1个能执行成功且上一作业执行失败的依赖作业、1个缺少执行资源且能执行成功的作业，
	 * 以此来测试调度系统核心逻辑的执行情况。测试结果期望：<br>
	 * 1、5个能执行成功的作业，在etl_job_disp_his表中的作业状态为完成（D）；<br>
	 * 2、在非自动日切时，1个虚作业，在etl_job_cur表中的作业状态为完成（D）；<br>
	 * 3、1个执行失败的作业，在etl_job_disp_his表中存在作业状态为错误（E）；
	 * 4、1个能执行成功且上一作业执行成功的依赖作业，在etl_job_disp_his表中的作业状态为完成（D）；<br>
	 * 5、1个能执行成功且上一作业执行失败的依赖作业，在etl_job_disp_his表中的作业状态为完成（D）；<br>
	 * 6、1个缺少执行资源且能执行成功的作业，在etl_job_disp_his表中的作业状态为完成（D）。
	 * @author Tiger.Wang
	 * @date 2019/10/11
	 * @param taskManager
	 *          含义：表示调度系统的核心处理逻辑对象。
	 *          取值范围：不能为null。
	 */
	private void publishReadyJob(TaskManager taskManager) {

		//TODO 这个测试用例需要trigger来执行任务，并且有执行结果后，该程序才能继续往下走。
		// 缺依赖作业的测试
		try(DatabaseWrapper db = new DatabaseWrapper()) {

			//1个能执行成功且上一作业执行成功的依赖作业
			String dependencySuccessEtlJob = "DependencySuccessEtlJob";
			Etl_job_def etlJobDef = new Etl_job_def();
			etlJobDef.setEtl_sys_cd(syscode);
			etlJobDef.setSub_sys_cd(syscode);
			etlJobDef.setEtl_job(dependencySuccessEtlJob);
			etlJobDef.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			etlJobDef.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			etlJobDef.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
			etlJobDef.setCom_exe_num(0);
			etlJobDef.setJob_priority(100);
			etlJobDef.setJob_priority_curr(100);
			etlJobDef.setCurr_bath_date(currBathDate);
			etlJobDef.setPro_type(Pro_Type.SHELL.getCode());
			etlJobDef.setPro_name(SLEEP1S_SHELL);
			etlJobDef.setPro_dic(PRO_DIR);
			etlJobDef.setLog_dic(FileUtil.TEMP_DIR_NAME);
			etlJobDef.add(db);

			Etl_dependency etlDependency = new Etl_dependency();
			etlDependency.setEtl_sys_cd(syscode);
			etlDependency.setEtl_job(dependencySuccessEtlJob);
			etlDependency.setPre_etl_job(etlJobDefs.get(0).getEtl_job());
			etlDependency.setPre_etl_sys_cd(syscode);
			etlDependency.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			etlDependency.setStatus(Status.TRUE.getCode());
			etlDependency.add(db);

			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(etlJobDef.getEtl_job() + 100);
			etlJobResourceRela.setResource_req(2);
			etlJobResourceRela.add(db);

			Etl_resource resource = new Etl_resource();
			resource.setEtl_sys_cd(syscode);
			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			resource.setResource_max(3);
			resource.add(db);

			//错误的数据访问1、测试程序接受到执行错误的作业
			String errorEtlJob = "ErrorEtlJob";
			etlJobDef.setEtl_job(errorEtlJob);
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS0.getCode());
			etlJobDef.setPro_name(FAUIL_SHELL);
			etlJobDef.add(db);

			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(etlJobDef.getEtl_job() + 100);
			etlJobResourceRela.setResource_req(2);
			etlJobResourceRela.add(db);

			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.setResource_max(3);
			resource.add(db);

			//1个能执行成功且上一作业执行失败的依赖作业
			String dependencyFailureEtlJob = "dependencyFailureEtlJob";
			etlJobDef.setEtl_job(dependencyFailureEtlJob);
			etlJobDef.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
			etlJobDef.setPro_name(SLEEP1S_SHELL);
			etlJobDef.add(db);

			etlDependency.setEtl_job(dependencyFailureEtlJob);
			etlDependency.setPre_etl_job(errorEtlJob);
			etlDependency.add(db);

			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.add(db);

			//1个缺少执行资源且能执行成功的作业
			String absenceEtlJob = "AbsenceEtlJob";
			etlJobDef.setEtl_job(absenceEtlJob);
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS0.getCode());
			etlJobDef.add(db);

			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.add(db);

			//虚作业
			String virtualEtlJob = "VirtualEtlJob";
			Etl_job_def virtualEtlJobDef = new Etl_job_def();
			virtualEtlJobDef.setEtl_sys_cd(syscode);
			virtualEtlJobDef.setEtl_job(virtualEtlJob);
			virtualEtlJobDef.setSub_sys_cd(syscode);
			virtualEtlJobDef.setJob_eff_flag(Job_Effective_Flag.VIRTUAL.getCode());
			virtualEtlJobDef.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			virtualEtlJobDef.setDisp_type(Dispatch_Type.TPLUS1.getCode());
			virtualEtlJobDef.setCom_exe_num(0);
			virtualEtlJobDef.setDisp_offset(1);
			virtualEtlJobDef.setJob_priority(1);
			virtualEtlJobDef.setJob_priority_curr(1);
			virtualEtlJobDef.setPro_type(Pro_Type.SHELL.getCode());
			virtualEtlJobDef.setCurr_bath_date(currBathDate);
//			virtualEtlJobDef.setExe_frequency(1);
			virtualEtlJobDef.add(db);

			etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job(virtualEtlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type("type" + virtualEtlJobDef.getEtl_job());
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			SqlOperator.commitTransaction(db);

			//当有错误作业时，需要干预，以让调度程序能结束
			Thread thread = new Thread(() -> {

				logger.info("--------------- 沉睡20秒 ---------------");
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Etl_job_hand etlJobHand = new Etl_job_hand();
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_job(errorEtlJob);
				etlJobHand.setPro_para(syscode + "," + errorEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JJ");
				etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
				etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);
			});

			thread.start();

			//正确的数据（程序初始化的数据）访问1、测试能正常执行的作业，
			taskManager.initEtlSystem();
			taskManager.loadReadyJob();
			taskManager.publishReadyJob();

			//1、5个能执行成功的作业在etl_job_disp_his表中的作业状态为完成（D）；
			for (Etl_job_def etlJobDefInit : etlJobDefs) {

				Etl_job_disp_his etlJobDispHis = SqlOperator.queryOneObject(db, Etl_job_disp_his.class,
						"SELECT * FROM etl_job_disp_his WHERE etl_sys_cd = ? AND etl_job = ?",
						etlJobDefInit.getEtl_sys_cd(), etlJobDefInit.getEtl_job())
						.orElseThrow(() -> new AppSystemException("无法在etl_job_disp_his查询到数据："
								+ etlJobDefInit.getEtl_job()));

				assertEquals("测试当前作业已经结束时，作业是否为完成状态" +
								etlJobDefInit.getEtl_job(),
						Job_Status.DONE.getCode(), etlJobDispHis.getJob_disp_status());
			}

			//TODO 在自动日切情况下，etl_job_cur表会被清空
			if(!taskManager.needDailyShift()) {
				//2、在非自动日切时，1个虚作业在etl_job_cur表中的作业状态为完成（D）；
				Etl_job_cur etlJobCur = SqlOperator.queryOneObject(db, Etl_job_cur.class,
						"SELECT * FROM etl_job_cur WHERE etl_sys_cd = ? AND etl_job = ?",
						virtualEtlJobDef.getEtl_sys_cd(), virtualEtlJobDef.getEtl_job())
						.orElseThrow(() -> new AppSystemException("无法在etl_job_cur查询到数据：" +
								virtualEtlJobDef.getEtl_job()));

				assertEquals("测试当前作业已经结束时，作业是否为完成状态" + etlJobCur.getEtl_job(),
						Job_Status.DONE.getCode(), etlJobCur.getJob_disp_status());
			}

			//3、1个执行失败的作业在etl_job_disp_his表中存在作业状态为错误（E）。
			//etl_job_cur表中的作业状态为完成（D）的原因是进行了干预。
			long etlJobDispHisNum = SqlOperator.queryNumber(db, "SELECT COUNT(*) FROM " +
							"etl_job_disp_his WHERE etl_sys_cd = ? AND etl_job = ? AND " +
							"job_disp_status = ?", syscode, errorEtlJob, Job_Status.ERROR.getCode())
					.orElseThrow(() -> new AppSystemException("etl_job_disp_his查询到数据：" +
							errorEtlJob));

			assertTrue("测试当前作业已经结束时，作业状态是否为错误（E）"
					+ errorEtlJob, etlJobDispHisNum > 0);

			//4、1个能执行成功且上一作业执行成功的依赖作业，在etl_job_disp_his表中的作业状态为完成（D）；
			Etl_job_disp_his etlJobDispHis = SqlOperator.queryOneObject(db, Etl_job_disp_his.class,
					"SELECT * FROM etl_job_disp_his WHERE etl_sys_cd = ? AND etl_job = ?",
					syscode, dependencySuccessEtlJob).orElseThrow(() ->
					new AppSystemException("无法在etl_job_disp_his查询到数据：" + dependencySuccessEtlJob));

			assertEquals("测试当前作业已经结束时，作业是否为完成状态" + dependencySuccessEtlJob,
					Job_Status.DONE.getCode(), etlJobDispHis.getJob_disp_status());

			//5、1个能执行成功且上一作业执行失败的依赖作业，在etl_job_disp_his表中的作业状态为完成（D）；
			//状态为完成的原因是，对失败的作业进行了跳过干预，否则该作业应该为挂起（P）
			etlJobDispHis = SqlOperator.queryOneObject(db, Etl_job_disp_his.class,
					"SELECT * FROM etl_job_disp_his WHERE etl_sys_cd = ? AND etl_job = ?",
					syscode, dependencyFailureEtlJob).orElseThrow(() ->
					new AppSystemException("无法在etl_job_disp_his查询到数据：" + dependencyFailureEtlJob));

			assertEquals("测试当前作业已经结束时，作业是否为完成状态" + dependencyFailureEtlJob,
					Job_Status.DONE.getCode(), etlJobDispHis.getJob_disp_status());

			//6、1个缺少执行资源且能执行成功的作业，在etl_job_disp_his表中的作业状态为完成（D）。
			etlJobDispHis = SqlOperator.queryOneObject(db, Etl_job_disp_his.class,
					"SELECT * FROM etl_job_disp_his WHERE etl_sys_cd = ? AND etl_job = ?",
					syscode, absenceEtlJob).orElseThrow(() ->
					new AppSystemException("无法在etl_job_disp_his查询到数据：" + absenceEtlJob));

			assertEquals("测试当前作业已经结束时，作业是否为完成状态" + absenceEtlJob,
					Job_Status.DONE.getCode(), etlJobDispHis.getJob_disp_status());
		}
	}

	/**
	 * 使用1个T+1的作业、1个不存在的作业、1个已执行完成的作业、1个[干预参数]设置错误的作业，
	 * 以此来测试调度系统核心逻辑中作业干预类型为[作业直接触发]的功能。测试结果期望：<br>
	 * 1、1个T+1的作业，在etl_job_disp_his表中作业状态为完成（D），在etl_job_hand_his表中干预状态为完成（D）；<br>
	 * 2、1个不存在的作业、1个已执行完成的作业、1个[干预参数]设置错误的作业，在etl_job_hand_his表中干预状态为失败（E）。
	 * @author Tiger.Wang
	 * @date 2019/10/11
	 * @param taskManager
	 *          含义：表示调度系统的核心处理逻辑对象。
	 *          取值范围：不能为null。
	 */
	private void handleJob2Run(TaskManager taskManager) {

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
			etlJobDef.setPro_dic(PRO_DIR);
			etlJobDef.setPro_name(SLEEP1S_SHELL);
			etlJobDef.setLog_dic(FileUtil.TEMP_DIR_NAME);
			etlJobDef.setExe_frequency(1);
			etlJobDef.add(db);

			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(etlJobDef.getEtl_job() + 100);
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			Etl_resource resource = new Etl_resource();
			resource.setEtl_sys_cd(syscode);
			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			resource.setResource_max(10);
			resource.add(db);

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

			SqlOperator.commitTransaction(db);

			String noExitEtlJob = "NoExitEtlJob";
			String errorStatusEtlJob = etlJobDefs.get(0).getEtl_job();
			Thread thread = new Thread(() -> {

				logger.info("--------------- 沉睡4秒 ---------------");
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Etl_job_hand etlJobHand = new Etl_job_hand();
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_job(handleEtlJob);
				etlJobHand.setPro_para(syscode + "," + handleEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JT");
				etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
				etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etlJobHand.add(db);

				//错误的数据访问1、干预不存在的作业。
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(noExitEtlJob);
				etlJobHand.setPro_para(syscode + "," + noExitEtlJob + "," + currBathDate);
				etlJobHand.add(db);

				//错误的数据访问2、干预设置错误的作业，不允许对已执行完成的作业进行干预
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(errorStatusEtlJob);
				etlJobHand.setPro_para(syscode + "," + errorStatusEtlJob + "," + currBathDate);
				etlJobHand.add(db);

				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(errorParaEtlJob);
				etlJobHand.setPro_para(syscode + "," + errorParaEtlJob + "," + currBathDate + ",1");
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);
			});

			thread.start();

			taskManager.initEtlSystem();
			taskManager.loadReadyJob();
			taskManager.publishReadyJob();

			//1、1个T+1的作业，在etl_job_disp_his表中作业状态为完成（D），在etl_job_hand_his表中干预状态为完成（D）；
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

			//2、1个不存在的作业、1个已执行完成的作业、1个[干预参数]设置错误的作业，在etl_job_hand_his表中干预状态为失败（E）。
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

	/**
	 * 使用1个不能执行成功的作业、5个初始化的能执行成功的作业，
	 * 以此来测试调度系统核心逻辑中作业干预类型为[系统重跑]的功能。测试结果期望：<br>
	 * 1、5个初始化的能正常执行的作业，干预结束后，在etl_job_disp_his表中，所有作业都执行了2次；<br>
	 * 2、1个不能正常执行的作业，干预结束后，在etl_job_disp_his表中，作业执行了3次；<br>
	 * 3、1个会失败的干预，当错误的干预信息干预结束后，在etl_job_hand_his表中的干预状态为错误（E）；<br>
	 * 4、1个会成功的干预，当正确的干预信息干预结束后，在etl_job_hand_his表中的干预状态为成功（D）。
	 * @author Tiger.Wang
	 * @date 2019/10/12
	 * @param taskManager
	 *          含义：表示调度系统的核心处理逻辑对象。
	 *          取值范围：不能为null。
	 */
	private void handleSys2Rerun(TaskManager taskManager) {

		//TODO 系统级干预跟etl_job无关，该字段不应该作为主键
		//作业干预类型为[系统重跑]的作业，若想成功干预，必须先系统暂停。
		try(DatabaseWrapper db = new DatabaseWrapper()) {

			String failEtlJob = "failEtlJob";
			Etl_job_def failEtlJobDef = new Etl_job_def();
			failEtlJobDef.setEtl_sys_cd(syscode);
			failEtlJobDef.setEtl_job(failEtlJob);
			failEtlJobDef.setSub_sys_cd(syscode);
			failEtlJobDef.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			failEtlJobDef.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			failEtlJobDef.setDisp_type(Dispatch_Type.TPLUS0.getCode());
			failEtlJobDef.setCom_exe_num(0);
			failEtlJobDef.setDisp_time("000001");
			failEtlJobDef.setJob_priority(100);
			failEtlJobDef.setJob_priority_curr(100);
			failEtlJobDef.setCurr_bath_date(currBathDate);
			failEtlJobDef.setPro_type(Pro_Type.SHELL.getCode());
			failEtlJobDef.setPro_dic(PRO_DIR);
			failEtlJobDef.setPro_name(FAUIL_SHELL);
			failEtlJobDef.setLog_dic(FileUtil.TEMP_DIR_NAME);
			failEtlJobDef.setExe_frequency(1);
			failEtlJobDef.add(db);

			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job(failEtlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(failEtlJobDef.getEtl_job() + 100);
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			Etl_resource resource = new Etl_resource();
			resource.setEtl_sys_cd(syscode);
			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			resource.setResource_max(10);
			resource.add(db);

			//当有错误作业时，需要干预，以让调度程序能结束
			Thread thread = new Thread(() -> {

				logger.info("--------------- 沉睡40秒 ---------------");
				try {
					Thread.sleep(40000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Etl_job_hand etlJobHand = new Etl_job_hand();
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_job(failEtlJobDef.getEtl_job());
				etlJobHand.setPro_para(syscode + "," + failEtlJobDef.getEtl_job() + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JJ");
				etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
				etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);
			});

			thread.start();

			String errorStatusHandleEtlJob = "errorSysRerun";
			String handleEtlJob = "sysRerun";
			//错误的数据访问1、进行干预时，系统不暂停，干预将会失败。
			Etl_job_hand etlJobHand = new Etl_job_hand();
			etlJobHand.setEtl_job(errorStatusHandleEtlJob);
			etlJobHand.setEtl_sys_cd(syscode);
			etlJobHand.setEtl_hand_type("SO");
			etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
			etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
			etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			etlJobHand.add(db);

			//正确的数据访问1、进行干预时，系统暂停，干预会成功。
			etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
			etlJobHand.setEtl_job(handleEtlJob);
			etlJobHand.setEtl_hand_type("SP");
			etlJobHand.add(db);

			etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
			etlJobHand.setEtl_hand_type("SO");
			etlJobHand.add(db);

			SqlOperator.commitTransaction(db);

			taskManager.initEtlSystem();
			taskManager.loadReadyJob();
			taskManager.publishReadyJob();

			//TODO 此处有问题，期望应该是：未执行过但已经在等待执行的作业，经过干预后，应该只会执行一次，
			// 该问题跟TaskManager类中代码1689、1730行一致。导致此干预功能测试的点不能全覆盖，也
			// 没法“模拟用户操作”（程序沉睡一定的时间），如果这么做了，那么执行结果将不稳定，无法进行断言
			//1、5个初始化的能正常执行的作业，干预结束后，在etl_job_disp_his表中，所有作业都执行了2次；
			for(Etl_job_def etlJobDef : etlJobDefs) {

				long etlJobdispHisNum = SqlOperator.queryNumber(db, "SELECT COUNT(*) FROM " +
								"etl_job_disp_his WHERE etl_sys_cd  = ? AND etl_job = ? AND " +
								"job_disp_status = ?" , syscode, etlJobDef.getEtl_job(),
						Job_Status.DONE.getCode()).orElseThrow(() -> new AppSystemException(
								"测试作业干预类型为[系统重跑]的作业失败"));

				assertEquals("测试作业干预类型为[系统重跑]的作业，在错误数据情况下是否干预错误",
						2, etlJobdispHisNum);
			}

			//2、1个不能正常执行的作业，干预结束后，在etl_job_disp_his表中，作业执行了3次；
			long failEtlJobdispHisNum = SqlOperator.queryNumber(db, "SELECT COUNT(*) FROM " +
							"etl_job_disp_his WHERE etl_sys_cd  = ? AND etl_job = ? AND " +
							"job_disp_status = ?" , syscode, failEtlJobDef.getEtl_job(),
					Job_Status.ERROR.getCode()).orElseThrow(() -> new AppSystemException(
					"测试作业干预类型为[系统重跑]的作业失败"));

			assertEquals("测试作业干预类型为[系统重跑]的作业，在正确数据情况下是否干预错误",
					3, failEtlJobdispHisNum);

			//3、1个会失败的干预，当错误的干预信息干预结束后，在etl_job_hand_his表中的干预状态为错误（E）；
			Etl_job_hand_his etlJobHandHis = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, errorStatusHandleEtlJob, "SO")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[系统重跑]的作业失败"));

			assertEquals("测试作业干预类型为[系统重跑]的作业，在错误数据情况下是否干预错误",
					Meddle_status.ERROR.getCode(), etlJobHandHis.getHand_status());

			//4、1个会成功的干预，当正确的干预信息干预结束后，在etl_job_hand_his表中的干预状态为成功（D）。
			etlJobHandHis = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, handleEtlJob, "SO")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[系统重跑]的作业失败"));

			assertEquals("测试作业干预类型为[系统重跑]的作业，在错误数据情况下是否干预错误",
					Meddle_status.DONE.getCode(), etlJobHandHis.getHand_status());
		}
	}

	/**
	 * 使用初始化的5个能运行成功的作业、1个能运行1分钟且成功的作业，配合[系统续跑]干预，
	 * 以此来测试调度系统核心逻辑中作业干预类型为[系统暂停]的功能。测试结果期望：<br>
	 * 1、1个能运行1分钟并成功的作业，干预结束后，在etl_job_disp_his表中有两条数据，调度状态为[错误、完成]；<br>
	 * 2、5个能运行成功的作业，干预结束后，在etl_job_disp_his表中的调度状态都为完成；<br>
	 * 3、1个会失败的干预，当错误的干预信息干预结束后，在etl_job_hand_his表中的干预状态为错误（E）；<br>
	 * 4、1个会成功的干预，当正确的干预信息干预结束后，在etl_job_hand_his表中的干预状态为成功（D）。
	 * @author Tiger.Wang
	 * @date 2019/10/12
	 * @param taskManager
	 *          含义：表示调度系统的核心处理逻辑对象。
	 *          取值范围：不能为null。
	 */
	private void handleSys2Pause(TaskManager taskManager) {

		//作业干预类型为[系统暂停]的作业，含义为：
		// 1、对于作业状态为[运行中]的作业，会设置该作业状态为[停止]，但是[运行中]的作业已经发布到redis中，
		//    对于已发布的作业，该干预无效；
		// 2、对于作业状态为[挂起]的作业，会设置该作业状态为[停止]，该作业不再执行。
		try(DatabaseWrapper db = new DatabaseWrapper()) {

			//正确的数据访问1、进行干预时，执行能运行1分钟的作业，干预能看到效果并干预成功。
			String handleEtlJob = "systemPause";
			Etl_job_def waitLongTimeEtlJob = new Etl_job_def();
			waitLongTimeEtlJob.setEtl_sys_cd(syscode);
			waitLongTimeEtlJob.setEtl_job(handleEtlJob);
			waitLongTimeEtlJob.setSub_sys_cd(syscode);
			waitLongTimeEtlJob.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			waitLongTimeEtlJob.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			waitLongTimeEtlJob.setDisp_type(Dispatch_Type.TPLUS0.getCode());
			waitLongTimeEtlJob.setCom_exe_num(0);
			waitLongTimeEtlJob.setDisp_time("000001");
			waitLongTimeEtlJob.setDisp_offset(1);    //偏移量跟DAILY没关系
			waitLongTimeEtlJob.setJob_priority(100);
			waitLongTimeEtlJob.setJob_priority_curr(100);
			waitLongTimeEtlJob.setCurr_bath_date(currBathDate);
			waitLongTimeEtlJob.setPro_type(Pro_Type.SHELL.getCode());
			waitLongTimeEtlJob.setPro_dic(PRO_DIR);
			waitLongTimeEtlJob.setPro_name(SLEEP1M_SHELL);
			waitLongTimeEtlJob.setLog_dic(FileUtil.TEMP_DIR_NAME);
			waitLongTimeEtlJob.setExe_frequency(1);
			waitLongTimeEtlJob.add(db);

			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job(waitLongTimeEtlJob.getEtl_job());
			etlJobResourceRela.setResource_type(waitLongTimeEtlJob.getEtl_job() + 100);
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			Etl_resource resource = new Etl_resource();
			resource.setEtl_sys_cd(syscode);
			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			resource.setResource_max(10);
			resource.add(db);

			SqlOperator.commitTransaction(db);

			String errorStatusHandleEtlJob = "errorStatusHandleEtlJob";
			Thread thread = new Thread(() -> {
				logger.info("--------------- 沉睡20秒 ---------------");
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Etl_job_hand etlJobHand = new Etl_job_hand();
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_job(handleEtlJob);
				etlJobHand.setEtl_hand_type("SP");
				etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
				etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etlJobHand.add(db);

				//错误的数据访问1、进行干预时，若系统已经暂停，干预将会失败。
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(errorStatusHandleEtlJob);
				etlJobHand.setEtl_hand_type("SP");
				etlJobHand.add(db);

				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(handleEtlJob);
				etlJobHand.setEtl_hand_type("SR");
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);
			});

			thread.start();

			taskManager.initEtlSystem();
			taskManager.loadReadyJob();
			taskManager.publishReadyJob();

			//1、1个能运行1分钟并成功的作业，干预结束后，在etl_job_disp_his表中有两条数据，调度状态为[错误、完成]
			long waitLongTimeHisNum = SqlOperator.queryNumber(db,
					"SELECT COUNT(*) FROM etl_job_disp_his WHERE etl_sys_cd = ? " +
							"AND etl_job = ? AND (job_disp_status = ? OR job_disp_status = ?)",
					syscode, handleEtlJob, Job_Status.ERROR.getCode(),
					Job_Status.DONE.getCode()).orElseThrow(() -> new AppSystemException(
							"测试作业干预类型为[系统暂停]的作业失败"));

			assertEquals("测试作业干预类型为[系统暂停]的作业，特定的作业执行结果是否符合期望，作业名为："
					+ handleEtlJob, 2, waitLongTimeHisNum);

			//TODO 此处有问题，期望应该是：未执行过但已经在等待执行的作业，经过干预后，该作业将不再执行，
			// 该问题跟TaskManager类中代码1689、1730行一致。导致此干预功能测试的点不能全覆盖，也
			// 没法“模拟用户操作”（程序沉睡一定的时间），如果这么做了，那么执行结果将不稳定，无法进行断言
			//2、5个能运行成功的作业，干预结束后，在etl_job_disp_his表中的调度状态都为完成；
			for(Etl_job_def etlJobDef : etlJobDefs) {

				Etl_job_disp_his etlJobDispHis = SqlOperator.queryOneObject(db,
						Etl_job_disp_his.class, "SELECT * FROM etl_job_disp_his WHERE etl_sys_cd = ? " +
								"AND etl_job = ?", syscode, etlJobDef.getEtl_job())
						.orElseThrow(() -> new AppSystemException("测试作业干预类型为[系统暂停]的作业失败"));

				assertEquals("测试作业干预类型为[系统暂停]的作业，特定的作业是否已经完成，作业名为："
						+ handleEtlJob, Job_Status.DONE.getCode(), etlJobDispHis.getJob_disp_status());
			}

			//3、1个会失败的干预，当错误的干预信息干预结束后，在etl_job_hand_his表中的干预状态为错误（E）；
			Etl_job_hand_his etlJobHandHis = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, errorStatusHandleEtlJob, "SP")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[系统暂停]的作业失败"));

			assertEquals("测试作业干预类型为[系统暂停]的作业，在错误数据情况下是否干预错误",
					Meddle_status.ERROR.getCode(), etlJobHandHis.getHand_status());

			//4、1个会成功的干预，当正确的干预信息干预结束后，在etl_job_hand_his表中的干预状态为成功（D）。
			etlJobHandHis = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, handleEtlJob, "SP")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[系统暂停]的作业失败"));

			assertEquals("测试作业干预类型为[系统暂停]的作业，在错误数据情况下是否干预错误",
					Meddle_status.DONE.getCode(), etlJobHandHis.getHand_status());
		}
	}

	/**
	 * 使用1个能运行1分钟且成功的作业、1个能运行失败的作业，
	 * 以此来测试调度系统核心逻辑中作业干预类型为[系统续跑]的功能。测试结果期望：<br>
	 * 1、1个能运行1分钟的作业，干预结束后，在etl_job_disp_his表中有两条数据，状态分别是[错误、完成]；<br>
	 * 2、1个能运行错误的作业，干预结束后，在etl_job_disp_his表中有三条数据，状态都为错误；<br>
	 * 3、1个会失败的干预，当错误的干预信息干预结束后，在etl_job_hand_his表中的干预状态为错误（E）；<br>
	 * 4、1个会成功的干预，当正确的干预信息干预结束后，在etl_job_hand_his表中的干预状态为成功（D）。
	 * @author Tiger.Wang
	 * @date 2019/10/14
	 * @param taskManager
	 *          含义：表示调度系统的核心处理逻辑对象。
	 *          取值范围：不能为null。
	 */
	private void handleSys2Resume(TaskManager taskManager) {

		//TODO 该干预没法容灾（断点续跑）！
		try(DatabaseWrapper db = new DatabaseWrapper()) {

			String minuteEtlJob = "1MinuteEtlJob";
			Etl_job_def etlJobDef = new Etl_job_def();
			etlJobDef.setEtl_sys_cd(syscode);
			etlJobDef.setEtl_job(minuteEtlJob);
			etlJobDef.setSub_sys_cd(syscode);
			etlJobDef.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			etlJobDef.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS0.getCode());
			etlJobDef.setCom_exe_num(0);
			etlJobDef.setDisp_time("000001");
			etlJobDef.setDisp_offset(1);
			etlJobDef.setJob_priority(100);
			etlJobDef.setJob_priority_curr(100);
			etlJobDef.setCurr_bath_date(currBathDate);
			etlJobDef.setPro_type(Pro_Type.SHELL.getCode());
			etlJobDef.setPro_dic(PRO_DIR);
			etlJobDef.setPro_name(SLEEP1M_SHELL);
			etlJobDef.setLog_dic(FileUtil.TEMP_DIR_NAME);
			etlJobDef.setExe_frequency(1);
			etlJobDef.add(db);

			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(etlJobDef.getEtl_job() + 100);
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			Etl_resource resource = new Etl_resource();
			resource.setEtl_sys_cd(syscode);
			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			resource.setResource_max(10);
			resource.add(db);

			String failureEtlJob = "failureEtlJob";
			etlJobDef.setJob_priority(90);
			etlJobDef.setJob_priority_curr(90);
			etlJobDef.setEtl_job(failureEtlJob);
			etlJobDef.setPro_name(FAUIL_SHELL);
			etlJobDef.add(db);

			etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(etlJobDef.getEtl_job() + 100);
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			resource = new Etl_resource();
			resource.setEtl_sys_cd(syscode);
			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			resource.setResource_max(10);
			resource.add(db);

			SqlOperator.commitTransaction(db);

			String errorStatusHandleEtlJob = "errorStatusHandleEtlJob";
			String handleEtlJob = "systemResume";
			Thread thread = new Thread(() -> {
				logger.info("--------------- 沉睡20秒 ---------------");
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				//错误的数据访问1、进行干预时，若系统不是暂停状态，干预将会失败。
				Etl_job_hand etlJobHand = new Etl_job_hand();
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_job(errorStatusHandleEtlJob);
				etlJobHand.setEtl_hand_type("SR");
				etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
				etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etlJobHand.add(db);

				//正确的数据访问1、进行干预时，系统已是暂停状态（等价于没有未完成或未停止的作业），
				// 此时能运行1分钟的作业及执行失败的作业，干预能看到效果并干预成功。
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(handleEtlJob);
				etlJobHand.setEtl_hand_type("SP");
				etlJobHand.add(db);

				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(handleEtlJob);
				etlJobHand.setEtl_hand_type("SR");
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);

				//TODO 这里有个奇怪现象，在trigger中，已经触发了系统续跑的情况下（此时失败的作业第二次执行），
				// failureEtlJob会等1MinuteEtlJob执行完后，才会分配线程及进程执行
				logger.info("--------------- 沉睡90秒 ---------------");
				try {
					Thread.sleep(90000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_job(failureEtlJob);
				etlJobHand.setPro_para(syscode + "," + failureEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JJ");
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);
			});

			thread.start();

			taskManager.initEtlSystem();
			taskManager.loadReadyJob();
			taskManager.publishReadyJob();

			//1、1个能运行1分钟的作业，干预结束后，在etl_job_disp_his表中有两条数据，状态分别是[错误、完成]；
			long waitLongTimeHisNum = SqlOperator.queryNumber(db, "SELECT COUNT(*) " +
							"FROM etl_job_disp_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND (job_disp_status = ? OR job_disp_status = ?)", syscode,
					minuteEtlJob, Job_Status.ERROR.getCode(), Job_Status.DONE.getCode())
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[系统续跑]的作业失败"));

			assertEquals("测试作业干预类型为[系统续跑]的作业，特定的作业执行结果是否符合期望，作业名为："
					+ minuteEtlJob, 2, waitLongTimeHisNum);

			//2、1个能运行错误的作业，干预结束后，在etl_job_disp_his表中有三条数据，状态都为错误；
			long failureHisNum = SqlOperator.queryNumber(db, "SELECT COUNT(*) " +
							"FROM etl_job_disp_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND job_disp_status = ?", syscode, failureEtlJob,
					Job_Status.ERROR.getCode()).orElseThrow(() -> new AppSystemException(
							"测试作业干预类型为[系统续跑]的作业失败"));

			assertEquals("测试作业干预类型为[系统续跑]的作业，特定的作业执行结果是否符合期望，作业名为："
					+ failureEtlJob, 3, failureHisNum);

			//3、1个会失败的干预，当错误的干预信息干预结束后，在etl_job_hand_his表中的干预状态为错误（E）；
			Etl_job_hand_his etlJobHandHis = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, errorStatusHandleEtlJob, "SR")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[系统续跑]的作业失败"));

			assertEquals("测试作业干预类型为[系统续跑]的作业，在错误数据情况下是否干预错误",
					Meddle_status.ERROR.getCode(), etlJobHandHis.getHand_status());

			//4、1个会成功的干预，当正确的干预信息干预结束后，在etl_job_hand_his表中的干预状态为成功（D）。
			etlJobHandHis = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, handleEtlJob, "SR")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[系统续跑]的作业失败"));

			assertEquals("测试作业干预类型为[系统续跑]的作业，在错误数据情况下是否干预错误",
					Meddle_status.DONE.getCode(), etlJobHandHis.getHand_status());
		}
	}

	/**
	 * 使用1个能运行1分钟且成功的作业、1个T+1的作业，
	 * 以此来测试调度系统核心逻辑中作业干预类型为[系统停止]的功能。测试结果期望：<br>
	 * 1、1、1个能执行一分钟的作业，干预结束后，在etl_job_disp_his表中，作业状态为错误（E）；<br>
	 * 2、1个T+1的作业，干预结束后，在etl_job_cur表中，作业状态为停止（S）；<br>
	 * 3、干预结束后，在etl_sys表中，该调度系统的运行状态为停止（S）；<br>
	 * 4、1个会成功的干预，干预结束后（若干预失败，程序将不会结束），在etl_job_hand_his表中的干预状态为成功（D）。
	 * @author Tiger.Wang
	 * @date 2019/10/14
	 * @param taskManager
	 *          含义：表示调度系统的核心处理逻辑对象。
	 *          取值范围：不能为null。
	 */
	private void handleSys2Stop(TaskManager taskManager) {

		//作业干预类型为[系统停止]的作业。
		try(DatabaseWrapper db = new DatabaseWrapper()) {

			String waitLongTimeEtLJob = "waitLongTimeEtLJob";
			Etl_job_def waitLongTimeEtlJob = new Etl_job_def();
			waitLongTimeEtlJob.setEtl_sys_cd(syscode);
			waitLongTimeEtlJob.setEtl_job(waitLongTimeEtLJob);
			waitLongTimeEtlJob.setSub_sys_cd(syscode);
			waitLongTimeEtlJob.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			waitLongTimeEtlJob.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			waitLongTimeEtlJob.setDisp_type(Dispatch_Type.TPLUS0.getCode());
			waitLongTimeEtlJob.setCom_exe_num(0);
			waitLongTimeEtlJob.setDisp_time("000001");
			waitLongTimeEtlJob.setDisp_offset(1);    //偏移量跟DAILY没关系
			waitLongTimeEtlJob.setJob_priority(100);
			waitLongTimeEtlJob.setJob_priority_curr(100);
			waitLongTimeEtlJob.setCurr_bath_date(currBathDate);
			waitLongTimeEtlJob.setPro_type(Pro_Type.SHELL.getCode());
			waitLongTimeEtlJob.setPro_dic(PRO_DIR);
			waitLongTimeEtlJob.setPro_name(SLEEP1M_SHELL);
			waitLongTimeEtlJob.setLog_dic(FileUtil.TEMP_DIR_NAME);
			waitLongTimeEtlJob.setExe_frequency(1);
			waitLongTimeEtlJob.add(db);

			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job(waitLongTimeEtlJob.getEtl_job());
			etlJobResourceRela.setResource_type(waitLongTimeEtlJob.getEtl_job() + 100);
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			Etl_resource resource = new Etl_resource();
			resource.setEtl_sys_cd(syscode);
			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			resource.setResource_max(10);
			resource.add(db);

			String tPlus1EtLJob = "T+1EtlJob";
			waitLongTimeEtlJob.setEtl_job(tPlus1EtLJob);
			waitLongTimeEtlJob.setDisp_type(Dispatch_Type.TPLUS1.getCode());
			waitLongTimeEtlJob.add(db);

			etlJobResourceRela.setEtl_job(waitLongTimeEtlJob.getEtl_job());
			etlJobResourceRela.setResource_type(waitLongTimeEtlJob.getEtl_job() + 100);
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			resource.setResource_max(10);
			resource.add(db);

			SqlOperator.commitTransaction(db);

			String handleEtlJob = "SystemStop";
			Thread thread = new Thread(() -> {
				logger.info("--------------- 沉睡30秒 ---------------");
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Etl_job_hand etlJobHand = new Etl_job_hand();
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_job(handleEtlJob);
				etlJobHand.setEtl_hand_type("SS");
				etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
				etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);
			});

			thread.start();

			taskManager.initEtlSystem();
			taskManager.loadReadyJob();
			taskManager.publishReadyJob();

			//1、1个能执行一分钟的作业，干预结束后，在etl_job_disp_his表中，作业状态为错误（E）；
			Etl_job_disp_his waitLongTimeDispHis = SqlOperator.queryOneObject(db,
					Etl_job_disp_his.class, "SELECT * FROM etl_job_disp_his " +
							"WHERE etl_sys_cd = ? AND etl_job = ?", syscode, waitLongTimeEtLJob)
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[系统停止]的作业失败"));

			assertEquals("测试作业干预类型为[系统停止]的作业，特定的作业执行结果是否符合期望，作业名为："
					+ waitLongTimeEtLJob, Job_Status.ERROR.getCode(), waitLongTimeDispHis.getJob_disp_status());

			//2、1个T+1的作业，干预结束后，在etl_job_cur表中，作业状态为停止（S）；
			if(!taskManager.needDailyShift()) {
				Etl_job_cur t1EtlJob = SqlOperator.queryOneObject(db,
						Etl_job_cur.class, "SELECT * FROM etl_job_cur " +
								"WHERE etl_sys_cd = ? AND etl_job = ?", syscode, tPlus1EtLJob)
						.orElseThrow(() -> new AppSystemException("测试作业干预类型为[系统停止]的作业失败"));

				assertEquals("测试作业干预类型为[系统停止]的作业，特定的作业执行结果是否符合期望，作业名为："
						+ waitLongTimeEtLJob, Job_Status.STOP.getCode(), t1EtlJob.getJob_disp_status());
			}

			//3、干预结束后，在etl_sys表中，该调度系统的运行状态为停止（S）；
			Etl_sys etlSys = SqlOperator.queryOneObject(db, Etl_sys.class,
					"SELECT * FROM etl_sys WHERE etl_sys_cd = ?", syscode)
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[系统停止]的作业失败"));

			assertEquals("测试作业干预类型为[系统停止]的作业，调度系统是否停止，系统编号为："
					+ syscode, Job_Status.STOP.getCode(), etlSys.getSys_run_status());

			//4、1个会成功的干预，干预结束后（若干预失败，程序将不会结束），在etl_job_hand_his表中的干预状态为成功（D）。
			Etl_job_hand_his etlJobHandHis = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, handleEtlJob, "SS")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[系统停止]的作业失败"));

			assertEquals("测试作业干预类型为[系统停止]的作业，在错误数据情况下是否干预错误",
					Meddle_status.DONE.getCode(), etlJobHandHis.getHand_status());
		}

	}

	/**
	 * 使用1个能运行1分钟且成功的作业、1个能执行失败的作业、1个T+1的作业、1个已执行完成的作业，
	 * 以此来测试调度系统核心逻辑中作业干预类型为[作业停止]的功能。测试结果期望：<br>
	 * 1、1个能执行一分钟的作业，干预结束后，在etl_job_disp_his表中作业状态为错误（E）；<br>
	 * 2、1个T+1的作业，干预结束后，在etl_job_cur表中作业状态为完成（D）；<br>
	 * 3、1个成功的干预，1个能执行一分钟的作业，在etl_job_hand_his表中干预状态为完成（D）；<br>
	 * 4、1个成功的干预，1个T+1的作业，在etl_job_hand_his表中干预状态为完成（D）；<br>
	 * 5、1个失败的干预，在干预参数信息错误时，etl_job_hand_his表中干预状态为错误（E）；<br>
	 * 6、1个失败的干预，在干预已经执行失败或停止的作业时，etl_job_hand_his表中干预状态为错误（E）；<br>
	 * 7、1个失败的干预，在干预已经执行成功的作业时，etl_job_hand_his表中干预状态为错误（E）；
	 * @author Tiger.Wang
	 * @date 2019/10/14
	 * @param taskManager
	 *          含义：表示调度系统的核心处理逻辑对象。
	 *          取值范围：不能为null。
	 */
	private void handleJob2Stop(TaskManager taskManager) {

		//作业干预类型为[作业停止]的作业。
		try(DatabaseWrapper db = new DatabaseWrapper()) {

			String waitLongTimeEtlJob = "waitLongTimeEtlJob";
			Etl_job_def etlJobDef = new Etl_job_def();
			etlJobDef.setEtl_sys_cd(syscode);
			etlJobDef.setEtl_job(waitLongTimeEtlJob);
			etlJobDef.setSub_sys_cd(syscode);
			etlJobDef.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			etlJobDef.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS0.getCode());
			etlJobDef.setCom_exe_num(0);
			etlJobDef.setJob_priority(100);
			etlJobDef.setJob_priority_curr(100);
			etlJobDef.setCurr_bath_date(currBathDate);
			etlJobDef.setPro_type(Pro_Type.SHELL.getCode());
			etlJobDef.setPro_dic(PRO_DIR);
			etlJobDef.setPro_name(SLEEP1M_SHELL);
			etlJobDef.setLog_dic(FileUtil.TEMP_DIR_NAME);
			etlJobDef.add(db);

			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(etlJobDef.getEtl_job() + 100);
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			Etl_resource resource = new Etl_resource();
			resource.setEtl_sys_cd(syscode);
			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			resource.setResource_max(10);
			resource.add(db);

			String failureEtlJob = "failureEtlJob";
			etlJobDef.setEtl_job(failureEtlJob);
			etlJobDef.setPro_name(FAUIL_SHELL);
			etlJobDef.add(db);

			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(etlJobDef.getEtl_job() + 100);
			etlJobResourceRela.add(db);

			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.add(db);

			String t1EtlJob = "T+1EtlJob";
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS1.getCode());
			etlJobDef.setDisp_time("000001");
			etlJobDef.setEtl_job(t1EtlJob);
			etlJobDef.setPro_name(SLEEP1S_SHELL);
			etlJobDef.add(db);

			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(etlJobDef.getEtl_job() + 100);
			etlJobResourceRela.add(db);

			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.add(db);

			SqlOperator.commitTransaction(db);

			String errorParaEtlJob = etlJobDefs.get(0).getEtl_job();
			String doneEtlJob = etlJobDefs.get(1).getEtl_job();
			Thread thread = new Thread(() -> {
				logger.info("--------------- 沉睡20秒 ---------------");
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				//错误的数据访问1、进行干预时，干预参数信息错误，干预将会失败。
				Etl_job_hand etlJobHand = new Etl_job_hand();
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_job(errorParaEtlJob);
				etlJobHand.setPro_para(syscode + "," + errorParaEtlJob + "," + currBathDate +
						"," + "abcd");
				etlJobHand.setEtl_hand_type("JS");
				etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
				etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etlJobHand.add(db);

				//正确的数据访问1、进行干预时，干预能运行1分钟的作业、T+1的作业，干预能看到效果并干预成功。
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(waitLongTimeEtlJob);
				etlJobHand.setPro_para(syscode + "," + waitLongTimeEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JS");
				etlJobHand.add(db);

				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(t1EtlJob);
				etlJobHand.setPro_para(syscode + "," + t1EtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JS");
				etlJobHand.add(db);

				//错误的数据访问2、进行干预时，干预已经运行失败或停止的作业，干预将会失败。
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(failureEtlJob);
				etlJobHand.setPro_para(syscode + "," + failureEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JS");
				etlJobHand.add(db);

				//错误的数据访问3、进行干预时，干预已经运行成功的作业，干预将会失败。
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(doneEtlJob);
				etlJobHand.setPro_para(syscode + "," + doneEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JS");
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);

				logger.info("--------------- 沉睡90秒 ---------------");
				try {
					Thread.sleep(90000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				//借助[作业跳过]干预，以退出程序。
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_job(failureEtlJob);
				etlJobHand.setPro_para(syscode + "," + failureEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JJ");
				etlJobHand.add(db);

				etlJobHand.setEtl_job(waitLongTimeEtlJob);
				etlJobHand.setPro_para(syscode + "," + waitLongTimeEtlJob + "," + currBathDate);
				etlJobHand.add(db);

				etlJobHand.setEtl_job(t1EtlJob);
				etlJobHand.setPro_para(syscode + "," + t1EtlJob + "," + currBathDate);
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);
			});

			thread.start();

			taskManager.initEtlSystem();
			taskManager.loadReadyJob();
			taskManager.publishReadyJob();

			//1、1个能执行一分钟的作业，干预结束后，在etl_job_disp_his表中作业状态为错误（E）；
			Etl_job_disp_his waitLongTimeEtlJobDispHis = SqlOperator.queryOneObject(db,
					Etl_job_disp_his.class, "SELECT * FROM etl_job_disp_his " +
							"WHERE etl_sys_cd = ? AND etl_job = ?", syscode, waitLongTimeEtlJob)
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业停止]的作业失败"));

			assertEquals("测试作业干预类型为[系统停止]的作业，在正确数据情况下是否干预成功",
					Job_Status.ERROR.getCode(), waitLongTimeEtlJobDispHis.getJob_disp_status());

			if(!taskManager.needDailyShift()) {
				//2、1个T+1的作业，干预结束后，在etl_job_cur表中作业状态为完成（D）；
				Etl_job_cur t1EtlJobCur = SqlOperator.queryOneObject(db,
						Etl_job_cur.class, "SELECT * FROM etl_job_cur " +
								"WHERE etl_sys_cd = ? AND etl_job = ?", syscode, t1EtlJob)
						.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业停止]的作业失败"));

				assertEquals("测试作业干预类型为[系统停止]的作业，在正确数据情况下是否干预成功",
						Job_Status.DONE.getCode(), t1EtlJobCur.getJob_disp_status());
			}

			//3、1个成功的干预，1个能执行一分钟的作业，在etl_job_hand_his表中干预状态为完成（D）；
			long waitLongTimeEtlJobNum = SqlOperator.queryNumber(db,
					"SELECT COUNT(*) FROM etl_job_hand_his WHERE etl_sys_cd = ? " +
							"AND etl_job = ? AND etl_hand_type = ? AND hand_status = ?",
					syscode, waitLongTimeEtlJob, "JS", Meddle_status.DONE.getCode())
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业停止]的作业失败"));

			assertEquals("测试作业干预类型为[系统停止]的作业，在正确数据情况下是否干预成功",
					1, waitLongTimeEtlJobNum);

			//4、1个成功的干预，1个T+1的作业，在etl_job_hand_his表中干预状态为完成（D）；
			long t1EtlJobNum = SqlOperator.queryNumber(db,
					"SELECT COUNT(*) FROM etl_job_hand_his WHERE etl_sys_cd = ? " +
							"AND etl_job = ? AND etl_hand_type = ? AND hand_status = ?",
					syscode, t1EtlJob, "JS", Meddle_status.DONE.getCode())
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业停止]的作业失败"));

			assertEquals("测试作业干预类型为[系统停止]的作业，在正确数据情况下是否干预成功",
					1, t1EtlJobNum);

			//5、1个失败的干预，在干预参数信息错误时，etl_job_hand_his表中干预状态为错误（E）；
			long errorParaEtlJobNum = SqlOperator.queryNumber(db,
					"SELECT COUNT(*) FROM etl_job_hand_his WHERE etl_sys_cd = ? " +
							"AND etl_job = ? AND etl_hand_type = ? AND hand_status = ?",
					syscode, errorParaEtlJob, "JS", Meddle_status.ERROR.getCode())
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业停止]的作业失败"));

			assertEquals("测试作业干预类型为[系统停止]的作业，在错误数据情况下是否干预失败",
					1, errorParaEtlJobNum);

			//6、1个失败的干预，在干预已经执行失败或停止的作业时，etl_job_hand_his表中干预状态为错误（E）；
			long failureEtlJobNums = SqlOperator.queryNumber(db,
					"SELECT COUNT(*) FROM etl_job_hand_his WHERE etl_sys_cd = ? " +
							"AND etl_job = ? AND etl_hand_type = ? AND hand_status = ?",
					syscode, failureEtlJob, "JS", Meddle_status.ERROR.getCode())
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业停止]的作业失败"));

			assertEquals("测试作业干预类型为[系统停止]的作业，在错误数据情况下是否干预失败",
					1, failureEtlJobNums);

			//7、1个失败的干预，在干预已经执行成功的作业时，etl_job_hand_his表中干预状态为错误（E）；
			long doneEtlJobNum = SqlOperator.queryNumber(db,
					"SELECT COUNT(*) FROM etl_job_hand_his WHERE etl_sys_cd = ? " +
							"AND etl_job = ? AND etl_hand_type = ? AND hand_status = ?",
					syscode, doneEtlJob, "JS", Meddle_status.ERROR.getCode())
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业停止]的作业失败"));

			assertEquals("测试作业干预类型为[系统停止]的作业，在错误数据情况下是否干预失败",
					1, doneEtlJobNum);
		}
	}

	/**
	 * 使用1个能运行1分钟且成功的作业、1个已停止的作业、1个能执行失败的作业、1个T+1的作业，
	 * 以此来测试调度系统核心逻辑中作业干预类型为[作业重跑]的功能。测试结果期望：<br>
	 * 1、1个已经停止的作业，干预结束后，在etl_job_disp_his表中有两条数据，调度状态分别为[错误、完成]，
	 * 	  1个已经失败的作业，干预结束后，在etl_job_disp_his表中有三条数据，调度状态都为错误，
	 * 	  1个已经结束的作业，干预结束后，在etl_job_disp_his表中有两条数据，调度状态都为完成；<br>
	 * 2、1个成功的干预，干预1个已经停止的作业、已经失败的作业、已经结束的作业，
	 *    干预结束后，etl_job_hand_his表中干预状态为完成（D）；<br>
	 * 3、1个失败的干预，在干预参数信息错误时，etl_job_hand_his表中干预状态为错误（E）；<br>
	 * 4、1个失败的干预，干预1个运行中的作业、挂起的作业，干预结束后，etl_job_hand_his表中干预状态为错误（E）；<br>
	 * 5、1个失败的干预，干预1个不存在的作业，干预结束后，etl_job_hand_his表中干预状态为错误（E）。
	 * @author Tiger.Wang
	 * @date 2019/10/15
	 * @param taskManager
	 *          含义：表示调度系统的核心处理逻辑对象。
	 *          取值范围：不能为null。
	 */
	private void handleJob2Rerun(TaskManager taskManager) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			String waitLongTimeEtlJob = "waitLongTimeEtlJob";
			Etl_job_def etlJobDef = new Etl_job_def();
			etlJobDef.setEtl_sys_cd(syscode);
			etlJobDef.setEtl_job(waitLongTimeEtlJob);
			etlJobDef.setSub_sys_cd(syscode);
			etlJobDef.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			etlJobDef.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS0.getCode());
			etlJobDef.setCom_exe_num(0);
			etlJobDef.setJob_priority(100);
			etlJobDef.setJob_priority_curr(100);
			etlJobDef.setCurr_bath_date(currBathDate);
			etlJobDef.setPro_type(Pro_Type.SHELL.getCode());
			etlJobDef.setPro_dic(PRO_DIR);
			etlJobDef.setPro_name(SLEEP1M_SHELL);
			etlJobDef.setLog_dic(FileUtil.TEMP_DIR_NAME);
			etlJobDef.add(db);

			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(etlJobDef.getEtl_job() + 100);
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			Etl_resource resource = new Etl_resource();
			resource.setEtl_sys_cd(syscode);
			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			resource.setResource_max(10);
			resource.add(db);

			String stopedEtlJob = "stopedEtlJob";
			etlJobDef.setEtl_job(stopedEtlJob);
			etlJobDef.add(db);

			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(etlJobDef.getEtl_job() + 100);
			etlJobResourceRela.add(db);

			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.add(db);

			String failureEtlJob = "failureEtlJob";
			etlJobDef.setEtl_job(failureEtlJob);
			etlJobDef.setPro_name(FAUIL_SHELL);
			etlJobDef.add(db);

			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(etlJobDef.getEtl_job() + 100);
			etlJobResourceRela.add(db);

			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.add(db);

			String t1EtlJob = "T+1EtlJob";
			etlJobDef.setEtl_job(t1EtlJob);
			etlJobDef.setPro_name(SLEEP1S_SHELL);
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS1.getCode());
			etlJobDef.setDisp_time("000001");
			etlJobDef.add(db);

			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(etlJobDef.getEtl_job() + 100);
			etlJobResourceRela.add(db);

			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.add(db);

			SqlOperator.commitTransaction(db);

			String doneEtlJob = etlJobDefs.get(0).getEtl_job();
			String errorParaEtlJob = etlJobDefs.get(1).getEtl_job();
			String noExitEtlJob = "NoExitEtlJob";
			Thread thread = new Thread(() -> {
				logger.info("--------------- 沉睡20秒 ---------------");
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				//正确的数据访问1、进行干预时，干预已经停止的、已经失败的、已经结束的作业，干预能看到效果并干预成功。
				//配合[作业停止]干预来实现“已经停止”的作业
				Etl_job_hand etlJobHand = new Etl_job_hand();
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_job(stopedEtlJob);
				etlJobHand.setPro_para(syscode + "," + stopedEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JS");
				etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
				etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etlJobHand.add(db);

				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(failureEtlJob);
				etlJobHand.setPro_para(syscode + "," + failureEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JR");
				etlJobHand.add(db);

				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(doneEtlJob);
				etlJobHand.setPro_para(syscode + "," + doneEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JR");
				etlJobHand.add(db);

				//错误的数据访问1、进行干预时，干预参数信息错误，干预将会失败；
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(errorParaEtlJob);
				etlJobHand.setPro_para(syscode + "," + errorParaEtlJob);
				etlJobHand.setEtl_hand_type("JR");
				etlJobHand.add(db);

				//错误的数据访问2、进行干预时，干预运行中的、挂起的作业，干预将会失败；
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(waitLongTimeEtlJob);
				etlJobHand.setPro_para(syscode + "," + waitLongTimeEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JR");
				etlJobHand.add(db);

				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(t1EtlJob);
				etlJobHand.setPro_para(syscode + "," + t1EtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JR");
				etlJobHand.add(db);

				//错误的数据访问3、进行干预时，干预不存在的作业，干预将会失败。
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(noExitEtlJob);
				etlJobHand.setPro_para(syscode + "," + noExitEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JR");
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);

				logger.info("--------------- 沉睡4秒 ---------------");
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(stopedEtlJob);
				etlJobHand.setPro_para(syscode + "," + stopedEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JR");
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);

				logger.info("--------------- 沉睡90秒 ---------------");
				try {
					Thread.sleep(90000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				//借助[系统停止]干预，以退出程序。
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_job(failureEtlJob);
				etlJobHand.setPro_para(syscode + "," + failureEtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JJ");
				etlJobHand.add(db);

				etlJobHand.setEtl_job(t1EtlJob);
				etlJobHand.setPro_para(syscode + "," + t1EtlJob + "," + currBathDate);
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);
			});

			thread.start();

			taskManager.initEtlSystem();
			taskManager.loadReadyJob();
			taskManager.publishReadyJob();

			//1、1个已经停止的作业，干预结束后，在etl_job_disp_his表中有两条数据，调度状态分别为[错误、完成]，
			//   1个已经失败的作业，干预结束后，在etl_job_disp_his表中有三条数据，调度状态都为错误，
			//   1个已经结束的作业，干预结束后，在etl_job_disp_his表中有两条数据，调度状态都为完成；
			long stopedEtlJobNum = SqlOperator.queryNumber(db,
					"SELECT COUNT(*) FROM etl_job_disp_his WHERE etl_sys_cd = ? " +
							"AND etl_job = ? AND (job_disp_status = ? OR job_disp_status = ?)",
					syscode, stopedEtlJob, Job_Status.ERROR.getCode(),
					Job_Status.DONE.getCode()).orElseThrow(() ->
					new AppSystemException("测试作业干预类型为[作业停止]的作业失败"));

			assertEquals("测试作业干预类型为[作业停止]的作业，在正确数据情况下是否干预成功",
					2, stopedEtlJobNum);

			long errorEtlJobNum = SqlOperator.queryNumber(db,
					"SELECT COUNT(*) FROM etl_job_disp_his WHERE etl_sys_cd = ? " +
							"AND etl_job = ? AND job_disp_status = ?",
					syscode, failureEtlJob, Job_Status.ERROR.getCode())
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业停止]的作业失败"));

			assertEquals("测试作业干预类型为[作业停止]的作业，在正确数据情况下是否干预成功",
					3, errorEtlJobNum);

			long doneEtlJobNum = SqlOperator.queryNumber(db ,
					"SELECT COUNT(*) FROM etl_job_disp_his WHERE etl_sys_cd = ? " +
							"AND etl_job = ? AND job_disp_status = ?",
					syscode, doneEtlJob, Job_Status.DONE.getCode())
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业停止]的作业失败"));

			assertEquals("测试作业干预类型为[作业停止]的作业，在正确数据情况下是否干预成功",
					2, doneEtlJobNum);

			//2、1个成功的干预，干预1个已经停止的作业、已经失败的作业、已经结束的作业，
			// 干预结束后，etl_job_hand_his表中干预状态为完成（D）；
			Etl_job_hand_his stopedHand = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, stopedEtlJob, "JR")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业停止]的作业失败"));

			assertEquals("测试作业干预类型为[作业停止]的作业，在正确数据情况下是否干预成功",
					Meddle_status.DONE.getCode(), stopedHand.getHand_status());

			long failedHandNum = SqlOperator.queryNumber(db , "SELECT COUNT(*) FROM " +
					"etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ? AND hand_status = ?",
					syscode, failureEtlJob, "JR", Meddle_status.DONE.getCode())
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业停止]的作业失败"));

			assertEquals("测试作业干预类型为[作业停止]的作业，在正确数据情况下是否干预成功",
					2, failedHandNum);

			Etl_job_hand_his doneHand = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, doneEtlJob, "JR")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业停止]的作业失败"));

			assertEquals("测试作业干预类型为[作业停止]的作业，在正确数据情况下是否干预成功",
					Meddle_status.DONE.getCode(), doneHand.getHand_status());

			//3、1个失败的干预，在干预参数信息错误时，etl_job_hand_his表中干预状态为错误（E）；
			Etl_job_hand_his errorParaHand = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, errorParaEtlJob, "JR")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业停止]的作业失败"));

			assertEquals("测试作业干预类型为[作业停止]的作业，在错误数据情况下是否干预失败",
					Meddle_status.ERROR.getCode(), errorParaHand.getHand_status());

			//4、1个失败的干预，干预1个运行中的作业、挂起的作业，干预结束后，etl_job_hand_his表中干预状态为错误（E）；
			Etl_job_hand_his runningJobHand = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, waitLongTimeEtlJob, "JR")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业停止]的作业失败"));

			assertEquals("测试作业干预类型为[作业停止]的作业，在错误数据情况下是否干预失败",
					Meddle_status.ERROR.getCode(), runningJobHand.getHand_status());

			//5、1个失败的干预，干预1个不存在的作业，干预结束后，etl_job_hand_his表中干预状态为错误（E）。
			Etl_job_hand_his noExitJobHand = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, noExitEtlJob, "JR")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业停止]的作业失败"));

			assertEquals("测试作业干预类型为[作业停止]的作业，在错误数据情况下是否干预失败",
					Meddle_status.ERROR.getCode(), noExitJobHand.getHand_status());
		}
	}

	/**
	 * 使用1个能运行1分钟的作业、1个已执行完成的作业、1个T+1的作业，
	 * 以此来测试调度系统核心逻辑中作业干预类型为[作业优先级调整]的功能。测试结果期望：<br>
	 * 1、1个T+1的作业、已执行结束的作业，在干预结束后，在etl_job_cur表中job_priority_curr字段为干预参数中的优先级参数；<br>
	 * 2、1个成功的干预，当干预T+1的作业、已执行结束的作业时，在etl_job_hand_his表中，干预状态为完成；<br>
	 * 3、1个失败的干预，当干预优先级超出范围时，在etl_job_hand_his表中，干预状态为错误；<br>
	 * 4、1个失败的干预，当干预调度状态为[运行中]的作业时，在etl_job_hand_his表中，干预状态为错误。<br>
	 * @author Tiger.Wang
	 * @date 2019/10/15
	 * @param taskManager
	 *          含义：表示调度系统的核心处理逻辑对象。
	 *          取值范围：不能为null。
	 */
	private void handleJob2ChangePriority(TaskManager taskManager) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			String waitLongTimeEtlJob = "waitLongTimeEtlJob";
			Etl_job_def etlJobDef = new Etl_job_def();
			etlJobDef.setEtl_sys_cd(syscode);
			etlJobDef.setEtl_job(waitLongTimeEtlJob);
			etlJobDef.setSub_sys_cd(syscode);
			etlJobDef.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			etlJobDef.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS0.getCode());
			etlJobDef.setCom_exe_num(0);
			etlJobDef.setJob_priority(100);
			etlJobDef.setJob_priority_curr(100);
			etlJobDef.setCurr_bath_date(currBathDate);
			etlJobDef.setPro_type(Pro_Type.SHELL.getCode());
			etlJobDef.setPro_dic(PRO_DIR);
			etlJobDef.setPro_name(SLEEP1M_SHELL);
			etlJobDef.setLog_dic(FileUtil.TEMP_DIR_NAME);
			etlJobDef.add(db);

			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(etlJobDef.getEtl_job() + 100);
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			Etl_resource resource = new Etl_resource();
			resource.setEtl_sys_cd(syscode);
			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			resource.setResource_max(10);
			resource.add(db);

			String t1EtlJob = "T+1EtlJob";
			etlJobDef.setEtl_job(t1EtlJob);
			etlJobDef.setPro_name(SLEEP1S_SHELL);
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS1.getCode());
			etlJobDef.setDisp_time("000001");
			etlJobDef.add(db);

			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(etlJobDef.getEtl_job() + 100);
			etlJobResourceRela.add(db);

			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.add(db);

			SqlOperator.commitTransaction(db);
			String doneEtlJob = etlJobDefs.get(0).getEtl_job();
			String errorParaEtlJob = etlJobDefs.get(1).getEtl_job();

			int priority_right = 50;
			int priority_failure = 200;
			Thread thread = new Thread(() -> {
				logger.info("--------------- 沉睡10秒 ---------------");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				//正确的数据访问1、进行干预时，干预调度状态不为[运行中]的作业，干预能看到效果并干预成功。
				Etl_job_hand etlJobHand = new Etl_job_hand();
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_job(t1EtlJob);
				etlJobHand.setPro_para(syscode + "," + t1EtlJob + "," + currBathDate + "," + priority_right);
				etlJobHand.setEtl_hand_type("JP");
				etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
				etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etlJobHand.add(db);

				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(doneEtlJob);
				etlJobHand.setPro_para(syscode + "," + doneEtlJob + "," + currBathDate + ","
						+ priority_right);
				etlJobHand.add(db);

				//错误的数据访问1、进行干预时，干预优先级超出范围，干预将会失败；
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(errorParaEtlJob);
				etlJobHand.setPro_para(syscode + "," + errorParaEtlJob + "," + currBathDate + ","
						+ priority_failure);
				etlJobHand.add(db);

				//错误的数据访问2、进行干预时，干预调度状态为[运行中]的作业，干预将会失败；
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(waitLongTimeEtlJob);
				etlJobHand.setPro_para(syscode + "," + waitLongTimeEtlJob + "," + currBathDate + ","
						+ priority_right);
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);

				logger.info("--------------- 沉睡90秒 ---------------");
				try {
					Thread.sleep(90000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				//借助[作业跳过]干预，以退出程序。
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_job(t1EtlJob);
				etlJobHand.setPro_para(syscode + "," + t1EtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JJ");
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);
			});

			thread.start();

			taskManager.initEtlSystem();
			taskManager.loadReadyJob();
			taskManager.publishReadyJob();

			//1、1个T+1的作业、已执行结束的作业，当干预结束后，在etl_job_cur表中job_priority_curr字段为干预参数中的优先级参数；
			if(!taskManager.needDailyShift()) {
				Etl_job_cur t1EtlJonCur = SqlOperator.queryOneObject(db, Etl_job_cur.class,
						"SELECT * FROM etl_job_cur WHERE etl_sys_cd = ? AND etl_job = ?",
						syscode, t1EtlJob).orElseThrow(() ->
						new AppSystemException("测试作业干预类型为[作业优先级调整]的作业失败"));

				assertEquals("测试作业干预类型为[作业优先级调整]的作业，在正确数据情况下是否干预成功",
						priority_right, (int) t1EtlJonCur.getJob_priority_curr());

				Etl_job_cur doneEtlJobCur = SqlOperator.queryOneObject(db, Etl_job_cur.class,
						"SELECT * FROM etl_job_cur WHERE etl_sys_cd = ? AND etl_job = ?",
						syscode, doneEtlJob).orElseThrow(() ->
						new AppSystemException("测试作业干预类型为[作业优先级调整]的作业失败"));

				assertEquals("测试作业干预类型为[作业优先级调整]的作业，在正确数据情况下是否干预成功",
						priority_right, (int) doneEtlJobCur.getJob_priority_curr());
			}

			//2、1个成功的干预，当干预T+1的作业、已执行结束的作业时，在etl_job_hand_his表中，干预状态为完成；
			Etl_job_hand_his t1JobHand = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, t1EtlJob, "JP")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业优先级调整]的作业失败"));

			assertEquals("测试作业干预类型为[作业优先级调整]的作业，在正确数据情况下是否干预成功",
					Meddle_status.DONE.getCode(), t1JobHand.getHand_status());

			Etl_job_hand_his doneJobHand = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, doneEtlJob, "JP")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业优先级调整]的作业失败"));

			assertEquals("测试作业干预类型为[作业优先级调整]的作业，在正确数据情况下是否干预成功",
					Meddle_status.DONE.getCode(), doneJobHand.getHand_status());

			//3、1个失败的干预，当干预优先级超出范围时，在etl_job_hand_his表中，干预状态为错误；
			Etl_job_hand_his errorJobHand = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, errorParaEtlJob, "JP")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业优先级调整]的作业失败"));

			assertEquals("测试作业干预类型为[作业优先级调整]的作业，在错误数据情况下是否干预失败",
					Meddle_status.ERROR.getCode(), errorJobHand.getHand_status());

			//4、1个失败的干预，当干预调度状态为[运行中]的作业时，在etl_job_hand_his表中，干预状态为错误。
			Etl_job_hand_his runningJobHand = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, waitLongTimeEtlJob, "JP")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业优先级调整]的作业失败"));

			assertEquals("测试作业干预类型为[作业优先级调整]的作业，在错误数据情况下是否干预失败",
					Meddle_status.ERROR.getCode(), runningJobHand.getHand_status());
		}
	}

	/**
	 * 使用1个能运行1分钟的作业、1个会执行错误的作业、1个T+1的作业、1个已执行完成的作业，
	 * 以此来测试调度系统核心逻辑中作业干预类型为[作业跳过]的功能。测试结果期望：<br>
	 * 1、1个T+1的作业、执行错误的作业，当干预结束后，在etl_job_cur表中调度状态为完成；<br>
	 * 2、1个成功的干预，当干预T+1的作业、执行错误的作业时，在etl_job_hand_his表中，干预状态为完成；<br>
	 * 3、1个失败的干预，当干预能执行一分钟的作业、已完成的作业时，在etl_job_hand_his表中，干预状态为错误；<br>
	 * 4、1个失败的干预，当干预参数错误时，在etl_job_hand_his表中，干预状态为错误。<br>
	 * @author Tiger.Wang
	 * @date 2019/10/15
	 * @param taskManager
	 *          含义：表示调度系统的核心处理逻辑对象。
	 *          取值范围：不能为null。
	 */
	private void handleJob2Skip(TaskManager taskManager) {

		try(DatabaseWrapper db = new DatabaseWrapper()) {

			String waitLongTimeEtlJob = "waitLongTimeEtlJob";
			Etl_job_def etlJobDef = new Etl_job_def();
			etlJobDef.setEtl_sys_cd(syscode);
			etlJobDef.setEtl_job(waitLongTimeEtlJob);
			etlJobDef.setSub_sys_cd(syscode);
			etlJobDef.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			etlJobDef.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS0.getCode());
			etlJobDef.setCom_exe_num(0);
			etlJobDef.setJob_priority(100);
			etlJobDef.setJob_priority_curr(100);
			etlJobDef.setCurr_bath_date(currBathDate);
			etlJobDef.setPro_type(Pro_Type.SHELL.getCode());
			etlJobDef.setPro_dic(PRO_DIR);
			etlJobDef.setPro_name(SLEEP1M_SHELL);
			etlJobDef.setLog_dic(FileUtil.TEMP_DIR_NAME);
			etlJobDef.add(db);

			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(etlJobDef.getEtl_job() + 100);
			etlJobResourceRela.setResource_req(1);
			etlJobResourceRela.add(db);

			Etl_resource resource = new Etl_resource();
			resource.setEtl_sys_cd(syscode);
			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			resource.setResource_max(10);
			resource.add(db);

			String failureEtlJob = "failureEtlJob";
			etlJobDef.setEtl_job(failureEtlJob);
			etlJobDef.setPro_name(FAUIL_SHELL);
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS0.getCode());
			etlJobDef.add(db);

			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(etlJobDef.getEtl_job() + 100);
			etlJobResourceRela.add(db);

			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.add(db);

			String t1EtlJob = "T+1EtlJob";
			etlJobDef.setEtl_job(t1EtlJob);
			etlJobDef.setPro_name(SLEEP1S_SHELL);
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS1.getCode());
			etlJobDef.setDisp_time("000001");
			etlJobDef.add(db);

			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type(etlJobDef.getEtl_job() + 100);
			etlJobResourceRela.add(db);

			resource.setResource_type(etlJobResourceRela.getResource_type());
			resource.add(db);

			SqlOperator.commitTransaction(db);
			String doneEtlJob = etlJobDefs.get(0).getEtl_job();
			String errorParaEtlJob = etlJobDefs.get(1).getEtl_job();

			Thread thread = new Thread(() -> {
				logger.info("--------------- 沉睡10秒 ---------------");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				//正确的数据访问1、进行干预时，干预调度状态不为[运行中、已完成]的作业，干预能看到效果并干预成功。
				Etl_job_hand etlJobHand = new Etl_job_hand();
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_sys_cd(syscode);
				etlJobHand.setEtl_job(t1EtlJob);
				etlJobHand.setPro_para(syscode + "," + t1EtlJob + "," + currBathDate);
				etlJobHand.setEtl_hand_type("JJ");
				etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
				etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etlJobHand.add(db);

				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(failureEtlJob);
				etlJobHand.setPro_para(syscode + "," + failureEtlJob + "," + currBathDate);
				etlJobHand.add(db);

				//错误的数据访问1、进行干预时，干预调度状态为[运行中、已完成]的作业，干预将会失败。
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(waitLongTimeEtlJob);
				etlJobHand.setPro_para(syscode + "," + waitLongTimeEtlJob + "," + currBathDate);
				etlJobHand.add(db);

				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(doneEtlJob);
				etlJobHand.setPro_para(syscode + "," + doneEtlJob + "," + currBathDate);
				etlJobHand.add(db);

				//错误的数据访问2、进行干预时，干预参数错误，干预将会失败；
				etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
				etlJobHand.setEtl_job(errorParaEtlJob);
				etlJobHand.setPro_para(syscode + "," + errorParaEtlJob);
				etlJobHand.add(db);

				SqlOperator.commitTransaction(db);
			});

			thread.start();

			taskManager.initEtlSystem();
			taskManager.loadReadyJob();
			taskManager.publishReadyJob();

			//1、1个T+1的作业、执行错误的作业，当干预结束后，在etl_job_cur表中调度状态为完成；
			if(!taskManager.needDailyShift()) {
				Etl_job_cur t1EtlJonCur = SqlOperator.queryOneObject(db, Etl_job_cur.class,
						"SELECT * FROM etl_job_cur WHERE etl_sys_cd = ? AND etl_job = ?",
						syscode, t1EtlJob).orElseThrow(() ->
						new AppSystemException("测试作业干预类型为[作业跳过]的作业失败"));

				assertEquals("测试作业干预类型为[作业跳过]的作业，在正确数据情况下是否干预成功",
						Job_Status.DONE.getCode(), t1EtlJonCur.getJob_disp_status());

				Etl_job_cur failureEtlJobCur = SqlOperator.queryOneObject(db, Etl_job_cur.class,
						"SELECT * FROM etl_job_cur WHERE etl_sys_cd = ? AND etl_job = ?",
						syscode, failureEtlJob).orElseThrow(() ->
						new AppSystemException("测试作业干预类型为[作业跳过]的作业失败"));

				assertEquals("测试作业干预类型为[作业跳过]的作业，在正确数据情况下是否干预成功",
						Job_Status.DONE.getCode(), failureEtlJobCur.getJob_disp_status());
			}

			//2、1个成功的干预，当干预T+1的作业、执行错误的作业时，在etl_job_hand_his表中，干预状态为完成；
			Etl_job_hand_his t1JobHand = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, t1EtlJob, "JJ")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业跳过]的作业失败"));

			assertEquals("测试作业干预类型为[作业跳过]的作业，在正确数据情况下是否干预成功",
					Meddle_status.DONE.getCode(), t1JobHand.getHand_status());

			Etl_job_hand_his failureJobHand = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, failureEtlJob, "JJ")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业跳过]的作业失败"));

			assertEquals("测试作业干预类型为[作业跳过]的作业，在正确数据情况下是否干预成功",
					Meddle_status.DONE.getCode(), failureJobHand.getHand_status());

			//3、1个失败的干预，当干预能执行一分钟的作业、已完成的作业时，在etl_job_hand_his表中，干预状态为错误；
			Etl_job_hand_his minuteJobHand = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, waitLongTimeEtlJob, "JJ")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业跳过]的作业失败"));

			assertEquals("测试作业干预类型为[作业跳过]的作业，在错误数据情况下是否干预失败",
					Meddle_status.ERROR.getCode(), minuteJobHand.getHand_status());

			Etl_job_hand_his doneJobHand = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, doneEtlJob, "JJ")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业跳过]的作业失败"));

			assertEquals("测试作业干预类型为[作业跳过]的作业，在错误数据情况下是否干预失败",
					Meddle_status.ERROR.getCode(), doneJobHand.getHand_status());

			//4、1个失败的干预，当干预参数错误时，在etl_job_hand_his表中，干预状态为错误。
			Etl_job_hand_his errorJobHand = SqlOperator.queryOneObject(db, Etl_job_hand_his.class,
					"SELECT * FROM etl_job_hand_his WHERE etl_sys_cd = ? AND etl_job = ? " +
							"AND etl_hand_type = ?", syscode, errorParaEtlJob, "JJ")
					.orElseThrow(() -> new AppSystemException("测试作业干预类型为[作业跳过]的作业失败"));

			assertEquals("测试作业干预类型为[作业跳过]的作业，在错误数据情况下是否干预失败",
					Meddle_status.ERROR.getCode(), errorJobHand.getHand_status());
		}
	}

	//@Test
//	public void handleSysDayShift() {
//		//TODO 系统干预日切待确认
//		String handleEtlJob = "SysDayShift";
//
//		try(DatabaseWrapper db = new DatabaseWrapper()) {
//			logger.info("--------------- 沉睡100秒 ---------------");
////			Thread.sleep(100000);
//
//			Etl_job_hand etlJobHand = new Etl_job_hand();
//			etlJobHand.setEtl_sys_cd(syscode);
//			etlJobHand.setEtl_job(handleEtlJob);
//			etlJobHand.setPro_para(syscode + "," + handleEtlJob + "," + currBathDate);
//			etlJobHand.setEtl_hand_type("SF");
//			etlJobHand.setEvent_id(String.valueOf(PrimayKeyGener.getNextId()));
//			etlJobHand.setHand_status(Meddle_status.TRUE.getCode());
//			etlJobHand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
//			etlJobHand.add(db);
//
//			SqlOperator.commitTransaction(db);
//		}
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
//	}
}
