package hrds.control.task;

import java.time.LocalDate;

import hrds.commons.codes.*;
import hrds.commons.entity.Etl_job_resource_rela;
import hrds.commons.entity.Etl_sys;
import hrds.control.utils.DateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.entity.Etl_job_def;
import hrds.commons.entity.Etl_resource;
import hrds.control.task.TaskManager;

import static org.junit.Assert.assertFalse;

/**
 * @ClassName: hrds.control.task.TaskManagerTest
 * @Description: 用于测试TaskManager类
 * @Author: Tiger.Wang
 * @Date: 2019/9/2 14:06
 * @Since: JDK 1.8
 **/
public class TaskManagerTest {

	private static final Logger logger = LogManager.getLogger();
	private TaskManager taskManager;

	private static final String syscode = "110";

	@Before
	public void before() {
		//TODO 问题1，对于不同的构造参数，应该如何测试
		taskManager = TaskManager.newInstance(syscode, LocalDate.now(), false, false);
		//TODO 使用实体新增数据不用手动提交？
		try(DatabaseWrapper db = new DatabaseWrapper()) {

			Etl_sys etlSys = new Etl_sys();
			etlSys.setEtl_sys_cd(syscode);
			etlSys.setEtl_sys_name("测试1");
			etlSys.setEtl_serv_ip("127.0.0.1");
			etlSys.setEtl_serv_port("8088");
			etlSys.setUser_id("1001");
			etlSys.setCurr_bath_date(LocalDate.now().format(DateUtil.DATE));
			etlSys.setBath_shift_time(LocalDate.now().plusDays(1).format(DateUtil.DATE));
			etlSys.setSys_run_status(Job_Status.STOP.getCode());
			etlSys.setUser_name("smk");
			etlSys.setUser_pwd("q1w2e3");
			etlSys.add(db);

			for(int i = 0 ; i < 5 ; i++) {
				//作业定义表
				Etl_job_def etlJobDef = new Etl_job_def();
				etlJobDef.setEtl_sys_cd(syscode);
				etlJobDef.setEtl_job(String.valueOf(i));
				etlJobDef.setSub_sys_cd(syscode);
				etlJobDef.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
				etlJobDef.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
				etlJobDef.setDisp_type(Dispatch_Type.TPLUS0.getCode());
				etlJobDef.setCom_exe_num(i);
				etlJobDef.setDisp_offset(i);
				etlJobDef.setJob_priority(i);
				etlJobDef.setJob_priority_curr(i);
				etlJobDef.setPro_type(Pro_Type.JAVA.getCode());
				etlJobDef.add(db);

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

			Etl_job_def etlJobDef = new Etl_job_def();
			etlJobDef.setEtl_sys_cd(syscode);
			etlJobDef.setEtl_job(String.valueOf(99));
			etlJobDef.setSub_sys_cd(syscode);
			etlJobDef.setJob_eff_flag(Job_Effective_Flag.VIRTUAL.getCode());
			etlJobDef.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
			etlJobDef.setDisp_type(Dispatch_Type.TPLUS1.getCode());
			etlJobDef.setCom_exe_num(1);
			etlJobDef.setDisp_offset(1);
			etlJobDef.setJob_priority(1);
			etlJobDef.setJob_priority_curr(1);
			etlJobDef.setPro_type(Pro_Type.JAVA.getCode());
			etlJobDef.add(db);

			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(syscode);
			etlJobResourceRela.setEtl_job(etlJobDef.getEtl_job());
			etlJobResourceRela.setResource_type("type" + 99);
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

			num = SqlOperator.execute(db, "DELETE FROM etl_sys WHERE etl_sys_cd = ? ", syscode);
			logger.info("清理etl_sys_rela表{}条数据", num);

			SqlOperator.commitTransaction(db);
		}
	}

	@Test
	public void initEtlSystem() {
		//TODO 问题2，对于类中不为public的方法不应该测试？ 问题3，对于类中为public的方法，无返回值，无法获取内部状态，
		// 应该如何测试？
		taskManager.initEtlSystem();
	}

	@Test
	public void loadReadyJob() {
		//TODO 问题4，对于需要内存数据的taskManager.loadReadyJob()方法，该类不对外提供内存表操作，只能这样进行测试？
		taskManager.initEtlSystem();
		taskManager.loadReadyJob();
	}

	@Test
	public void publishReadyJob() {
		//TODO 这个测试用例需要trigger来执行任务，并且有执行结果后，该程序才能继续往下走。
		taskManager.initEtlSystem();
		boolean hasFrequancy = taskManager.loadReadyJob();
		boolean isAuthShif = taskManager.publishReadyJob(hasFrequancy);

		assertFalse("测试当前作业已经结束时，系统是否自动日切", isAuthShif);
	}

	@Test
	public void handleJob2Run() {
	}

	@Test
	public void handleSys2Rerun() {
	}

	@Test
	public void handleSys2Pause() {
	}

	@Test
	public void handleSys2Resume() {
	}

	@Test
	public void handleJob2Stop() {
	}

	@Test
	public void handleJob2Rerun() {
	}

	@Test
	public void handleJob2ChangePriority() {
	}

	@Test
	public void handleJob2Skip() {
	}

	@Test
	public void handleSysDayShift() {
	}

	@Test
	public void isSysPause() {
	}

	@Test
	public void closeSysPause() {
	}

	@Test
	public void openSysPause() {
	}
}
