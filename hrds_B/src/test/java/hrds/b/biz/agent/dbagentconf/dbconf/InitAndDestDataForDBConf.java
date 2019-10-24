package hrds.b.biz.agent.dbagentconf.dbconf;

import fd.ng.core.annotation.DocClass;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.b.biz.agent.dbagentconf.InitBaseData;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_job_classify;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Database_set;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "为DBConfStepAction单元测试类初始化和销毁数据", author = "WangZhengcheng")
public class InitAndDestDataForDBConf {
	//测试数据用户ID
	private static final long TEST_USER_ID = -9997L;
	private static final long FIRST_DB_AGENT_ID = 7001L;
	private static final long SECOND_DB_AGENT_ID = 7002L;
	private static final long THIRD_CLASSIFY_ID = 12306L;

	public static void before(){
		//1、构建data_source表测试数据
		Data_source dataSource = InitBaseData.buildDataSourceData();

		//2、构建agent_info表测试数据
		List<Agent_info> agents = InitBaseData.buildAgentInfosData();

		//3、构建database_set表测试数据
		List<Database_set> databases = InitBaseData.buildDbSetData();

		//4、构建collect_job_classify表测试数据
		List<Collect_job_classify> classifies = InitBaseData.buildClassifyData();
		Collect_job_classify thridClassify = new Collect_job_classify();
		thridClassify.setClassify_id(THIRD_CLASSIFY_ID);
		thridClassify.setAgent_id(FIRST_DB_AGENT_ID);
		thridClassify.setClassify_num("wzc_test_classify_num3");
		thridClassify.setClassify_name("wzc_test_classify_name3");
		thridClassify.setUser_id(TEST_USER_ID);
		classifies.add(thridClassify);

		//插入数据
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//插入数据源表(data_source)测试数据
			int dataSourceCount = dataSource.add(db);
			assertThat("数据源测试数据初始化", dataSourceCount, is(1));

			//插入Agent信息表(agent_info)测试数据
			for(Agent_info agentInfo : agents){
				agentInfo.add(db);
			}
			assertThat("Agent测试数据初始化", agents.size(), is(2));

			//插入database_set表测试数据
			for(Database_set databaseSet : databases){
				databaseSet.add(db);
			}
			assertThat("数据库设置测试数据初始化", databases.size(), is(2));

			//插入collect_job_classify表测试数据
			for(Collect_job_classify classify : classifies){
				classify.add(db);
			}
			assertThat("采集任务分类表测试数据初始化", classifies.size(), is(3));

			SqlOperator.commitTransaction(db);
		}
	}

	public static void after(){
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1、删除数据源表(data_source)测试数据
			int deleteSourceNum = SqlOperator.execute(db, "delete from " + Data_source.TableName + " WHERE create_user_id = ?", TEST_USER_ID);
			//2、删除Agent信息表(agent_info)测试数据
			int deleteAgentNum = SqlOperator.execute(db, "delete from " + Agent_info.TableName + " WHERE user_id = ?", TEST_USER_ID);
			//3、删除database_set表测试数据
			int deleteDsNumOne = SqlOperator.execute(db, "delete from " + Database_set.TableName + " WHERE agent_id = ?", FIRST_DB_AGENT_ID);
			int deleteDsNumTwo = SqlOperator.execute(db, "delete from " + Database_set.TableName + " WHERE agent_id = ?", SECOND_DB_AGENT_ID);
			//4、删除collect_job_classify表测试数据
			int deleteCJCNum = SqlOperator.execute(db, "delete from " + Collect_job_classify.TableName + " WHERE user_id = ?", TEST_USER_ID);

			SqlOperator.commitTransaction(db);

			long dataSources = SqlOperator.queryNumber(db, "select count(1) from " + Data_source.TableName + " WHERE create_user_id = ?", TEST_USER_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的数据源数据有:" + deleteSourceNum + "条", dataSources, is(0L));

			long agents = SqlOperator.queryNumber(db, "select count(1) from " + Agent_info.TableName + " WHERE user_id = ?", TEST_USER_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的Agent数据有:" + deleteAgentNum + "条", agents, is(0L));

			long dataSourceSetsOne = SqlOperator.queryNumber(db, "select count(1) from " + Database_set.TableName + " WHERE agent_id = ?", FIRST_DB_AGENT_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			long dataSourceSetsTwo = SqlOperator.queryNumber(db, "select count(1) from " + Database_set.TableName + " WHERE agent_id = ?", SECOND_DB_AGENT_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的数据库设置表数据有:" + (deleteDsNumOne + deleteDsNumTwo) + "条", dataSourceSetsOne + dataSourceSetsTwo, is(0L));

			long collectJobClassifyNum = SqlOperator.queryNumber(db, "select count(1) from " + Collect_job_classify.TableName + " WHERE user_id = ?", TEST_USER_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的采集作业分类表数据有:" + deleteCJCNum + "条", collectJobClassifyNum, is(0L));
		}
	}
}
