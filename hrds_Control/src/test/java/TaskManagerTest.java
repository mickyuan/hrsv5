import java.time.LocalDate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.Job_Effective_Flag;
import hrds.commons.codes.Main_Server_Sync;
import hrds.commons.codes.Pro_Type;
import hrds.commons.entity.Etl_job_def;
import hrds.commons.entity.Etl_resource;
import hrds.control.task.TaskManager;

/**
 * @ClassName: TaskManagerTest
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
		taskManager = TaskManager.newInstance(false, syscode, LocalDate.now(),
				false, false);
		//TODO 使用实体新增数据不用手动提交？
		try(DatabaseWrapper db = new DatabaseWrapper()) {
			for(int i = 0 ; i < 5 ; i++) {
				//资源表
				Etl_resource resource = new Etl_resource();
				resource.setEtl_sys_cd(syscode);
				resource.setResource_type(resource.getResource_type() + i);
				resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				resource.add(db);
				//作业定义表
				Etl_job_def etlJobDef = new Etl_job_def();
				etlJobDef.setEtl_sys_cd(syscode);
				etlJobDef.setEtl_job(String.valueOf(i));
				etlJobDef.setSub_sys_cd(syscode);
				etlJobDef.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
				etlJobDef.setCom_exe_num(i);
				etlJobDef.setDisp_offset(i);
				etlJobDef.setJob_priority(i);
				etlJobDef.setJob_priority_curr(i);
				etlJobDef.setPro_type(Pro_Type.JAVA.getCode());
				etlJobDef.add(db);
			}

			Etl_job_def etlJobDef = new Etl_job_def();
			etlJobDef.setEtl_sys_cd(syscode);
			etlJobDef.setEtl_job(String.valueOf(99));
			etlJobDef.setSub_sys_cd(syscode);
			etlJobDef.setJob_eff_flag(Job_Effective_Flag.VIRTUAL.getCode());
			etlJobDef.setCom_exe_num(1);
			etlJobDef.setDisp_offset(1);
			etlJobDef.setJob_priority(1);
			etlJobDef.setJob_priority_curr(1);
			etlJobDef.setPro_type(Pro_Type.JAVA.getCode());
			etlJobDef.add(db);
		}
	}

	@After
	public void after() {
		try(DatabaseWrapper db = new DatabaseWrapper()) {

			int num = SqlOperator.execute(db, "DELETE FROM etl_resource WHERE etl_sys_cd = ? ", syscode);
			logger.info("清理etl_resource表{}条数据", num);

			num = SqlOperator.execute(db, "DELETE FROM etl_job_def WHERE etl_sys_cd = ? ", syscode);
			logger.info("清理etl_job_def表{}条数据", num);

			SqlOperator.commitTransaction(db);
		}
	}

	@Test
	public void initEtlSystemTest() {
		//TODO 问题2，对于类中不为public的方法不应该测试？ 问题3，对于类中为public的方法，无返回值，无法获取内部状态，
		// 应该如何测试？
		taskManager.initEtlSystem();
	}

	@Test
	public void loadReadyJobTest() {
		//TODO 问题4，对于需要内存数据的taskManager.loadReadyJob()方法，该类不对外提供内存表操作，只能这样进行测试？
		taskManager.initEtlSystem();
		taskManager.loadReadyJob();
	}
}
