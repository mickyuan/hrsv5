package hrds.b.biz.agent.dbagentconf;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.*;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_job_classify;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Database_set;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @description: DBConfStepAction单元测试类
 * @author: WangZhengcheng
 * @create: 2019-09-05 18:06
 **/
public class DBConfStepActionTest extends WebBaseTestCase{

	//测试数据用户ID
	private static final long TEST_USER_ID = -9997L;
	//source_id
	private static final long SOURCE_ID = 1L;
	private static final long FIRST_DB_AGENT_ID = 7001L;
	private static final long SECOND_DB_AGENT_ID = 7002L;
	private static final long FIRST_CLASSIFY_ID = 10086L;
	private static final long SECOND_CLASSIFY_ID = 10010L;

	/**
	 * 为每个方法的单元测试初始化测试数据
	 *
	 * 1、构建data_source表测试数据
	 * 2、构建agent_info表测试数据
	 * 3、构建database_set表测试数据，只构建数据库直连采集
	 * 4、构建collect_job_classify表测试数据
	 * 5、插入数据
	 *
	 * 测试数据：
	 *      1、data_source表：有1条数据，source_id为1
	 *      2、Agent_info表：有2条数据,全部是数据库采集Agent，agent_id分别为7001，7002,source_id为1
	 *      3、database_set表：有2条数据,database_id为1001,1002, agent_id分别为7001,7002，1001的classifyId是10086，1002的classifyId是10010
	 *      1001设置完成并发送成功(is_sendok)
	 *
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Before
	public void before() {
		//1、构建data_source表测试数据
		Data_source dataSource = new Data_source();
		dataSource.setSource_id(SOURCE_ID);
		dataSource.setDatasource_number("ds_");
		dataSource.setDatasource_name("wzctest_");
		dataSource.setDatasource_remark("wzctestremark_");
		dataSource.setCreate_date(DateUtil.getSysDate());
		dataSource.setCreate_time(DateUtil.getSysTime());
		dataSource.setCreate_user_id(TEST_USER_ID);

		//2、构建agent_info表测试数据
		List<Agent_info> agents = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			String agentType = null;
			long agentId = 0L;
			switch (i) {
				case 1:
					agentType = AgentType.ShuJuKu.getCode();
					agentId = FIRST_DB_AGENT_ID;
					break;
				case 2:
					agentType = AgentType.ShuJuKu.getCode();
					agentId = SECOND_DB_AGENT_ID;
					break;
			}
			Agent_info agentInfo = new Agent_info();
			agentInfo.setAgent_id(agentId);
			agentInfo.setAgent_name("agent_" + i);
			agentInfo.setAgent_type(agentType);
			agentInfo.setAgent_ip("127.0.0.1");
			agentInfo.setAgent_port("55555");
			agentInfo.setAgent_status(AgentStatus.WeiLianJie.getCode());
			agentInfo.setCreate_date(DateUtil.getSysDate());
			agentInfo.setCreate_time(DateUtil.getSysTime());
			agentInfo.setUser_id(TEST_USER_ID);
			agentInfo.setSource_id(SOURCE_ID);

			agents.add(agentInfo);
		}

		//3、构建database_set表测试数据
		List<Database_set> databases = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			long agentId = i % 2 == 0 ? FIRST_DB_AGENT_ID : SECOND_DB_AGENT_ID;
			long classifyId = i % 2 == 0 ? FIRST_CLASSIFY_ID : SECOND_CLASSIFY_ID;
			long id = i % 2 == 0 ? 1001L : 1002L;
			String isSendOk = i % 2 == 0 ? IsFlag.Shi.getCode() : IsFlag.Fou.getCode();
			String databaseType = i % 2 == 0 ? DatabaseType.Postgresql.getCode() : DatabaseType.DB2.getCode();
			Database_set databaseSet = new Database_set();
			databaseSet.setDatabase_id(id);
			databaseSet.setAgent_id(agentId);
			databaseSet.setDatabase_number("dbtest" + i);
			databaseSet.setDb_agent(IsFlag.Shi.getCode());
			databaseSet.setIs_load(IsFlag.Shi.getCode());
			databaseSet.setIs_hidden(IsFlag.Shi.getCode());
			databaseSet.setIs_sendok(isSendOk);
			databaseSet.setData_extract_type(DataExtractType.ShuJuChouQuJiRuKu.getCode());
			databaseSet.setIs_header(IsFlag.Shi.getCode());
			databaseSet.setClassify_id(classifyId);
			databaseSet.setTask_name("wzcTaskName" + i);
			databaseSet.setDatabase_type(databaseType);

			databases.add(databaseSet);
		}

		//4、构建collect_job_classify表测试数据
		List<Collect_job_classify> classifies = new ArrayList<>();
		for(int i = 0; i < 2; i++){
			Collect_job_classify classify = new Collect_job_classify();
			long classifyId = i % 2 == 0 ? FIRST_CLASSIFY_ID : SECOND_CLASSIFY_ID;
			long agentId = i % 2 == 0 ? FIRST_DB_AGENT_ID : SECOND_DB_AGENT_ID;
			classify.setClassify_id(classifyId);
			classify.setClassify_num("wzc_test_classify_num" + i);
			classify.setClassify_name("wzc_test_classify_name" + i);
			classify.setUser_id(TEST_USER_ID);
			classify.setAgent_id(agentId);

			classifies.add(classify);
		}

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
			assertThat("采集任务分类表测试数据初始化", databases.size(), is(2));

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 测试数据库直连采集，根据databaseId进行查询并在页面上回显数据源配置信息
	 *
	 * 正确数据访问1：传入正确的userId和is_sendok字段为1的databaseId(正确)
	 * 错误的数据访问1：传入正确的userId和is_sendok字段为0的databaseId(正确)
	 * 错误的数据访问2：传入正确的userId和错误的databaseId
	 * 错误的数据访问3：传入错误的userId和正确的databaseId
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getDBConfInfo(){
		long wrongUserId = 3333L;
		long wrongDatabaseId = 1003L;
		//正确数据访问1：传入正确的userId和is_sendok字段为1的databaseId
		String rightString = new HttpClient()
				.addData("databaseId", 1001)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getDBConfInfo")).getBodyString();

		ActionResult rightResult = JsonUtil.toObject(rightString, ActionResult.class);
		assertThat(rightResult.isSuccess(), is(true));
		List<Result> dataForEntityList = rightResult.getDataForEntityList(Result.class);
		assertThat("根据测试数据，查询到的数据源信息应该有" + dataForEntityList.size() + "条", dataForEntityList.size(), is(1));

		//错误的数据访问1：传入正确的userId和is_sendok字段为0的databaseId
		String firWrongString = new HttpClient()
				.addData("databaseId", 1002)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getDBConfInfo")).getBodyString();

		ActionResult firWrongResult = JsonUtil.toObject(firWrongString, ActionResult.class);
		assertThat(firWrongResult.isSuccess(), is(false));

		//错误的数据访问2：传入正确的userId和错误的databaseId
		String secWrongString = new HttpClient()
				.addData("databaseId", wrongDatabaseId)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getDBConfInfo")).getBodyString();
		ActionResult secWrongResult = JsonUtil.toObject(secWrongString, ActionResult.class);
		assertThat(secWrongResult.isSuccess(), is(false));

		//错误的数据访问3：传入错误的userId和正确的databaseId
		String thiWrongString = new HttpClient()
				.addData("databaseId", 1001)
				.addData("userId", wrongUserId)
				.post(getActionUrl("getDBConfInfo")).getBodyString();
		ActionResult thiWrongResult = JsonUtil.toObject(thiWrongString, ActionResult.class);
		assertThat(thiWrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试根据数据库类型和端口获得数据库连接url等信息
	 *
	 * 正确数据访问1：构建mysql数据库访问场景，断言得到的数据是否正确
	 * 错误的数据访问1：构建dbType不在DatabaseType代码项中的code值，断言得到的数据是否正确
	 * 错误访问场景不足的原因：该方法调用只需要一个参数
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getJDBCDriver(){
		String wrongDBType = "123";
		//正确数据访问1：构建mysql数据库访问场景，断言得到的数据是否正确
		String rightString = new HttpClient()
				.addData("dbType", DatabaseType.MYSQL.getCode())
				.post(getActionUrl("getJDBCDriver")).getBodyString();
		ActionResult rightResult = JsonUtil.toObject(rightString, ActionResult.class);
		assertThat(rightResult.isSuccess(), is(true));
		Map<Object, Object> dataForMap = rightResult.getDataForMap();
		assertThat("根据测试数据，查询到的数据源信息应该有" + dataForMap.size() + "条", dataForMap.size(), is(1));

		//错误的数据访问1：构建dbType不在DatabaseType代码项中的code值，断言得到的数据是否正确
		String wrongString = new HttpClient()
				.addData("dbType", wrongDBType)
				.post(getActionUrl("getJDBCDriver")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObject(wrongString, ActionResult.class);
		assertThat(wrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试根据classifyId判断当前分类是否被使用，如果被使用，则不能编辑，否则，可以编辑
	 *
	 * 正确数据访问1：传入正确的userId和正确的classifyId
	 * 错误的数据访问1：传入正确的userId和错误的classifyId
	 * 错误的数据访问2：传入错误的userId和正确的classifyId
	 * 错误的数据访问3：传入错误的userId和错误的classifyId
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void checkClassifyId(){
		long wrongUserId = 3333L;
		long wrongClassifyId = 10000L;
		//正确数据访问1：传入正确的userId和正确的classifyId
		String rightString = new HttpClient()
				.addData("classifyId", FIRST_CLASSIFY_ID)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("checkClassifyId")).getBodyString();
		ActionResult rightResult = JsonUtil.toObject(rightString, ActionResult.class);
		assertThat(rightResult.isSuccess(), is(true));
		List<Object> data = (List<Object>) rightResult.getData();
		boolean checkResult = (boolean) data.get(0);
		assertThat(checkResult, is(true));

		//错误的数据访问1：传入正确的userId和错误的classifyId
		String wrongUserString = new HttpClient()
				.addData("classifyId", FIRST_CLASSIFY_ID)
				.addData("userId", wrongUserId)
				.post(getActionUrl("checkClassifyId")).getBodyString();
		ActionResult wrongUserResult = JsonUtil.toObject(wrongUserString, ActionResult.class);
		assertThat(wrongUserResult.isSuccess(), is(false));

		//错误的数据访问2：传入错误的userId和正确的classifyId
		String wrongClassifyString = new HttpClient()
				.addData("classifyId", wrongClassifyId)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("checkClassifyId")).getBodyString();
		ActionResult wrongClassifyResult = JsonUtil.toObject(wrongClassifyString, ActionResult.class);
		assertThat(wrongClassifyResult.isSuccess(), is(false));

		//错误的数据访问3：传入错误的userId和错误的classifyId
		String bothWrongString = new HttpClient()
				.addData("classifyId", wrongClassifyId)
				.addData("userId", wrongUserId)
				.post(getActionUrl("checkClassifyId")).getBodyString();
		ActionResult bothWrongResult = JsonUtil.toObject(bothWrongString, ActionResult.class);
		assertThat(bothWrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试根据sourceId获取分类信息
	 *
	 * 正确数据访问1：传入正确的userId和正确的sourceId
	 * 错误的数据访问1：传入错误的userId和正确的sourceId
	 * 错误的数据访问2：传入正确的userId和错误的sourceId
	 * 错误的数据访问3：传入错误的userId和错误的sourceId
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getClassifyInfo(){
		long wrongUserId = 3333L;
		long wrongSourceId = 2L;
		//正确数据访问1：传入正确的userId和正确的sourceId
		String rightString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getClassifyInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObject(rightString, ActionResult.class);
		assertThat(rightResult.isSuccess(), is(true));
		//错误的数据访问1：传入错误的userId和正确的sourceId
		String wrongUserIdString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("userId", wrongUserId)
				.post(getActionUrl("getClassifyInfo")).getBodyString();
		ActionResult wrongUserResult = JsonUtil.toObject(wrongUserIdString, ActionResult.class);
		assertThat(wrongUserResult.isSuccess(), is(true));
		List<Object> data = (List<Object>) wrongUserResult.getData();
		assertThat("传入错误的userId和正确的sourceId,获得的数据为" + data.size() + "条", data.size(), is(0));
		//错误的数据访问2：传入正确的userId和错误的sourceId
		String wrongSourceString = new HttpClient()
				.addData("sourceId", wrongSourceId)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getClassifyInfo")).getBodyString();
		ActionResult wrongSourceResult = JsonUtil.toObject(wrongSourceString, ActionResult.class);
		assertThat(wrongSourceResult.isSuccess(), is(true));
		List<Object> wrongSourceData = (List<Object>) wrongSourceResult.getData();
		assertThat("传入正确的userId和错误的sourceId,获得的数据为" + wrongSourceData.size() + "条", wrongSourceData.size(), is(0));

		//错误的数据访问3：传入错误的userId和错误的sourceId
		String bothWrongString = new HttpClient()
				.addData("sourceId", wrongSourceId)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getClassifyInfo")).getBodyString();
		ActionResult bothWrongResult = JsonUtil.toObject(bothWrongString, ActionResult.class);
		assertThat(bothWrongResult.isSuccess(), is(true));
		List<Object> bothWrongData = (List<Object>) bothWrongResult.getData();
		assertThat("传入错误的userId和错误的sourceId,获得的数据为" + bothWrongData.size() + "条", bothWrongData.size(), is(0));
	}

	/**
	 * 测试保存采集任务分类信息
	 *
	 * 正确数据访问1：
	 *      构造正确数据，执行插入操作
	 *      验证DB里面的数据是否正确
	 * 错误的数据访问1：
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveClassifyInfo(){

	}

	/**
	 * 测试更新采集任务分类信息
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void updateClassifyInfo(){

	}

	/**
	 * 测试删除采集任务分类信息
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteClassifyInfo(){

	}

	/**
	 * 测试保存数据库采集Agent数据库配置信息
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveDbConf(){

	}

	/**
	 * 测试测试连接功能
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void testConnection(){

	}

	/**
	 * 在测试用例执行完之后，删除测试数据
	 *
	 * 1、删除数据源表(data_source)测试数据
	 * 2、删除Agent信息表(agent_info)测试数据
	 * 3、删除database_set表测试数据
	 * 4、提交事务后，对数据表中的数据进行检查，断言删除是否成功
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@After
	public void after(){
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long beforeDataSources = SqlOperator.queryNumber(db, "select count(1) from " + Data_source.TableName + " WHERE create_user_id = ?", TEST_USER_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("删除前的数据源数据数据有:" + beforeDataSources + "条", beforeDataSources, is(1L));

			long beforeAgents = SqlOperator.queryNumber(db, "select count(1) from " + Agent_info.TableName + " WHERE user_id = ?", TEST_USER_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("删除前的Agent数据有:" + beforeAgents + "条", beforeAgents, is(2L));

			long beforeDataSourceSetsOne = SqlOperator.queryNumber(db, "select count(1) from " + Database_set.TableName + " WHERE agent_id = ?", FIRST_DB_AGENT_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			long beforeDataSourceSetsTwo = SqlOperator.queryNumber(db, "select count(1) from " + Database_set.TableName + " WHERE agent_id = ?", SECOND_DB_AGENT_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("删除前的数据库设置表数据有:" + (beforeDataSourceSetsOne + beforeDataSourceSetsTwo) + "条", beforeDataSourceSetsOne + beforeDataSourceSetsTwo, is(2L));

			long beforeCollectJobClassifyNum = SqlOperator.queryNumber(db, "select count(1) from " + Collect_job_classify.TableName + " WHERE user_id = ?", TEST_USER_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("删除前的采集作业分类表数据有:" + beforeCollectJobClassifyNum + "条", beforeCollectJobClassifyNum, is(2L));

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
