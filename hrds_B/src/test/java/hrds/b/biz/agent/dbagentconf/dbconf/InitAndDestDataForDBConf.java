package hrds.b.biz.agent.dbagentconf.dbconf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import fd.ng.core.annotation.DocClass;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.b.biz.agent.dbagentconf.BaseInitData;
import hrds.commons.entity.Agent_down_info;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_job_classify;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Department_info;
import hrds.commons.utils.key.PrimayKeyGener;
import java.util.ArrayList;
import java.util.List;

@DocClass(desc = "为DBConfStepAction单元测试类初始化和销毁数据", author = "WangZhengcheng")
public class InitAndDestDataForDBConf {

	//测试数据用户ID
	public final BaseInitData baseInitData = new BaseInitData();
	public final long THIRD_CLASSIFY_ID = PrimayKeyGener.getNextId();

	public void before() {
		//1、构造sys_user表测试数据
//		Sys_user user = baseInitData.buildSysUserData();

		//2、构造department_info表测试数据
		Department_info departmentInfo = baseInitData.buildDeptInfoData();

		//3、构建data_source表测试数据
		Data_source dataSource = baseInitData.buildDataSourceData();

		//4、构建agent_info表测试数据
		List<Agent_info> agents = baseInitData.buildAgentInfosData();

		//5、构建database_set表测试数据
		List<Database_set> databases = baseInitData.buildDbSetData();

		//6、构建collect_job_classify表测试数据
		List<Collect_job_classify> classifies = baseInitData.buildClassifyData();
		Collect_job_classify thridClassify = new Collect_job_classify();
		thridClassify.setClassify_id(THIRD_CLASSIFY_ID);
		thridClassify.setAgent_id(baseInitData.FIRST_DB_AGENT_ID);
		thridClassify.setClassify_num("wzc_test_classify_num" + baseInitData.threadId);
		thridClassify.setClassify_name("wzc_test_classify_name" + baseInitData.threadId);
		thridClassify.setUser_id(baseInitData.TEST_USER_ID);
		classifies.add(thridClassify);

		//7、由于该Action类的测试连接功能需要与agent端交互，所以需要配置一条agent_down_info表的记录，用于找到http访问的完整url
		Agent_down_info agentDownInfo = baseInitData.initAgentDownInfo();

		//插入数据
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//插入用户表(sys_user)测试数据
//			int userCount = user.add(db);
//			assertThat("用户表测试数据初始化", userCount, is(1));

			//插入部门表(department_info)测试数据
			int deptCount = departmentInfo.add(db);
			assertThat("部门表测试数据初始化", deptCount, is(1));

			//插入数据源表(data_source)测试数据
			int dataSourceCount = dataSource.add(db);
			assertThat("数据源测试数据初始化", dataSourceCount, is(1));

			//插入Agent信息表(agent_info)测试数据
			int agentInfoCount = 0;
			for (Agent_info agentInfo : agents) {
				int count = agentInfo.add(db);
				agentInfoCount += count;
			}
			assertThat("Agent测试数据初始化", agentInfoCount, is(2));

			//插入database_set表测试数据
			int databaseSetCount = 0;
			for (Database_set databaseSet : databases) {
				int count = databaseSet.add(db);
				databaseSetCount += count;
			}
			assertThat("数据库设置测试数据初始化", databaseSetCount, is(2));

			//插入collect_job_classify表测试数据
			int classifyCount = 0;
			for (Collect_job_classify classify : classifies) {
				int count = classify.add(db);
				classifyCount += count;
			}
			assertThat("采集任务分类表测试数据初始化", classifyCount, is(3));

			//插入agent_down_info表测试数据
			int agentDownInfoCount = agentDownInfo.add(db);
			assertThat("agent_down_info表测试数据初始化", agentDownInfoCount, is(1));

			SqlOperator.commitTransaction(db);
		}

	}

	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1、删除用户表(sys_user)测试数据
//			SqlOperator.execute(db, "delete from " + Sys_user.TableName + " WHERE user_id = ?", baseInitData.TEST_USER_ID);
			//2、删除部门表(department_info)测试数据
			SqlOperator
				.execute(db, "delete from " + Department_info.TableName + " WHERE dep_id = ?", baseInitData.TEST_DEPT_ID);
			//3、删除数据源表(data_source)测试数据
			SqlOperator.execute(db, "delete from " + Data_source.TableName + " WHERE source_id = ?",
				baseInitData.SOURCE_ID);
			//4、删除Agent信息表(agent_info)测试数据
			SqlOperator.execute(db, "delete from " + Agent_info.TableName + " WHERE agent_id in (?,?)",
				baseInitData.FIRST_DB_AGENT_ID, baseInitData.SECOND_DB_AGENT_ID);
			//5、删除database_set表测试数据
			SqlOperator.execute(db, "delete from " + Database_set.TableName + " WHERE agent_id = ?",
				baseInitData.FIRST_DB_AGENT_ID);
			SqlOperator.execute(db, "delete from " + Database_set.TableName + " WHERE agent_id = ?",
				baseInitData.SECOND_DB_AGENT_ID);
			//6、删除collect_job_classify表测试数据
			SqlOperator.execute(db, "delete from " + Collect_job_classify.TableName + " WHERE classify_id in (?,?,?)",
				baseInitData.FIRST_CLASSIFY_ID, baseInitData.SECOND_CLASSIFY_ID, THIRD_CLASSIFY_ID);
			//7、删除agent_down_info表测试数据
			SqlOperator.execute(db, "delete from " + Agent_down_info.TableName + " WHERE down_id = ?",
				baseInitData.AGENT_DOWN_INFO_ID);

			SqlOperator.commitTransaction(db);
		}
	}
}
