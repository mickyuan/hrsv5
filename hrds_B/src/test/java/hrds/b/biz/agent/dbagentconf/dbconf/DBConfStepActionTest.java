package hrds.b.biz.agent.dbagentconf.dbconf;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.b.biz.agent.bean.DBConnectionProp;
import hrds.commons.codes.AgentStatus;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_job_classify;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Database_set;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "DBConfStepAction单元测试类", author = "WangZhengcheng")
public class DBConfStepActionTest extends WebBaseTestCase{

	//测试数据用户ID
	private static final long TEST_USER_ID = -9997L;
	//source_id
	private static final long SOURCE_ID = 1L;
	private static final long FIRST_DB_AGENT_ID = 7001L;
	private static final long SECOND_DB_AGENT_ID = 7002L;
	private static final long FIRST_CLASSIFY_ID = 10086L;
	private static final long SECOND_CLASSIFY_ID = 10010L;
	private static final long THIRD_CLASSIFY_ID = 12306L;

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
	 *      4、collect_job_classify表：有3条数据，classify_id为10086L、10010L、12306L，agent_id分别为7001L、7002L，7001L,user_id为-9997L
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

	/**
	 * 测试数据库直连采集，根据databaseId进行查询并在页面上回显数据源配置信息
	 *
	 * 正确数据访问1：传入is_sendok字段为1的databaseId
	 * 错误的数据访问1：传入is_sendok字段为0的databaseId
	 * 错误的数据访问2：传入不存在的databaseId
	 * 错误的测试用例未达到三组:两组错误的测试用例已经可以覆盖所有可能出现的错误情况
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getDBConfInfo(){
		long wrongDatabaseId = 1003L;
		//正确数据访问1：传入is_sendok字段为1的databaseId
		String rightString = new HttpClient()
				.addData("databaseId", 1002L)
				.post(getActionUrl("getDBConfInfo")).getBodyString();

		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		/*
		List<Object> data = (List<Object>) rightResult.getData();
		assertThat("根据测试数据，查询到的数据库配置信息应该有" + data.size() + "条", data.size(), is(1));
		*/
		Result data = rightResult.getDataForResult();
		assertThat("根据测试数据，查询到的数据库配置信息应该有" + data.getRowCount() + "条", data.getRowCount(), is(1));

		//错误的数据访问1：传入is_sendok字段为0的databaseId
		String firWrongString = new HttpClient()
				.addData("databaseId", 1001L)
				.post(getActionUrl("getDBConfInfo")).getBodyString();

		ActionResult firWrongResult = JsonUtil.toObjectSafety(firWrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(firWrongResult.isSuccess(), is(false));

		//错误的数据访问2：传入不存在的databaseId
		String secWrongString = new HttpClient()
				.addData("databaseId", wrongDatabaseId)
				.post(getActionUrl("getDBConfInfo")).getBodyString();
		ActionResult secWrongResult = JsonUtil.toObjectSafety(secWrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(secWrongResult.isSuccess(), is(false));
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
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		/*
		Map<String, String> data = (Map<String, String>) rightResult.getData();
		assertThat("根据测试数据，查询到的数据库信息应该有" + data.size() + "组键值对", data.size(), is(4));
		assertThat( data.get("jdbcPrefix"), is("jdbc:mysql://"));
		assertThat( data.get("jdbcIp"), is(":"));
		assertThat( data.get("jdbcPort"), is("/"));
		assertThat( data.get("jdbcBase"), is("?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull"));
		*/
		List<DBConnectionProp> data = rightResult.getDataForEntityList(DBConnectionProp.class);
		assertThat("根据测试数据，查询到的数据库连接信息应该有" + data.size() + "条", data.size(), is(1));
		assertThat( data.get(0).getUrlPrefix(), is("jdbc:mysql://"));
		assertThat( data.get(0).getIpPlaceholder(), is(":"));
		assertThat( data.get(0).getPortPlaceholder(), is("/"));
		assertThat( data.get(0).getUrlSuffix(), is("?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull"));

		//错误的数据访问1：构建dbType不在DatabaseType代码项中的code值，断言得到的数据是否正确
		String wrongString = new HttpClient()
				.addData("dbType", wrongDBType)
				.post(getActionUrl("getJDBCDriver")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试根据classifyId判断当前分类是否被使用，如果被使用，则不能编辑，否则，可以编辑
	 *
	 * 正确数据访问1：传入正确的已被database_set表使用的classifyId
	 * 正确数据访问2：传入正确的未被database_set表使用的classifyId
	 * 错误的数据访问1：传入错误的classifyId
	 * 错误访问场景不足的原因：该方法调用只需要一个参数
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void checkClassifyId(){
		//正确数据访问1：传入正确的已被database_set表使用的classifyId
		String usedClassifyIdString = new HttpClient()
				.addData("classifyId", FIRST_CLASSIFY_ID)
				.post(getActionUrl("checkClassifyId")).getBodyString();
		ActionResult usedClassifyIdResult = JsonUtil.toObjectSafety(usedClassifyIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(usedClassifyIdResult.isSuccess(), is(true));
		boolean usedClassifyResult = (boolean) usedClassifyIdResult.getData();
		assertThat(usedClassifyResult, is(false));

		//正确数据访问2：传入正确的未被database_set表使用的classifyId
		String unusedClassifyIdString = new HttpClient()
				.addData("classifyId", THIRD_CLASSIFY_ID)
				.post(getActionUrl("checkClassifyId")).getBodyString();
		ActionResult unusedClassifyIdResult = JsonUtil.toObjectSafety(unusedClassifyIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(unusedClassifyIdResult.isSuccess(), is(true));
		boolean checkResult = (boolean) unusedClassifyIdResult.getData();
		assertThat(checkResult, is(true));

		//错误的数据访问1：错误的classifyId
		String wrongUserString = new HttpClient()
				.addData("classifyId", FIRST_CLASSIFY_ID)
				.post(getActionUrl("checkClassifyId")).getBodyString();
		ActionResult wrongUserResult = JsonUtil.toObjectSafety(wrongUserString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongUserResult.isSuccess(), is(false));
	}

	/**
	 * 测试根据sourceId获取分类信息
	 *
	 * 正确数据访问1：传入正确的sourceId
	 * 错误的数据访问1：传入错误的sourceId
	 * 错误访问场景不足的原因：该方法调用只需要一个参数
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getClassifyInfo(){
		long wrongSourceId = 2L;
		//正确数据访问1：传入正确的userId和正确的sourceId
		String rightString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("getClassifyInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		/*
		List<Object> data = (List<Object>) rightResult.getData();
		assertThat("查询得到的分类信息数据有" + data.size() + "条", data.size(), is(3));
		*/
		List<Collect_job_classify> data = rightResult.getDataForEntityList(Collect_job_classify.class);
		assertThat("查询得到的分类信息数据有" + data.size() + "条", data.size(), is(3));


		//错误的数据访问2：传入错误的sourceId
		String wrongSourceString = new HttpClient()
				.addData("sourceId", wrongSourceId)
				.post(getActionUrl("getClassifyInfo")).getBodyString();
		ActionResult wrongSourceResult = JsonUtil.toObjectSafety(wrongSourceString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongSourceResult.isSuccess(), is(true));
		/*
		List<Object> wrongSourceData = (List<Object>) wrongSourceResult.getData();
		assertThat("传入错误的sourceId,获得的数据为" + wrongSourceData.size() + "条", wrongSourceData.size(), is(0));
		*/
		List<Collect_job_classify> wrongSourceData = wrongSourceResult.getDataForEntityList(Collect_job_classify.class);
		assertThat("传入错误的sourceId,获得的数据为" + wrongSourceData.size() + "条", wrongSourceData.size(), is(0));
	}

	/**
	 * 测试保存采集任务分类信息
	 *
	 * 正确数据访问1：
	 *      构造正确数据，执行插入操作
	 *      验证DB里面的数据是否正确
	 *      再次执行插入操作，由于分类编号不能重复，因此插入操作失败
	 *      以上全部执行成功，表示插入数据成功，删除刚刚插入的数据
	 * 错误的数据访问1：不传入classify_num
	 * 错误的数据访问2：不传入classify_name
	 * 错误的数据访问3：不传入user_id
	 * 错误的数据访问4：不传入Agent_id
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveClassifyInfo(){
		//正确数据访问1：
		//构造正确数据，执行插入操作
		String classifyNum = "12138";
		String  classifyName = "wzc_test_classify_name";
		String rightString = new HttpClient()
				.addData("classify_num", classifyNum)
				.addData("classify_name", classifyName)
				.addData("user_id", TEST_USER_ID)
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("saveClassifyInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		//验证DB里面的数据是否正确
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Collect_job_classify classify = SqlOperator.queryOneObject(db, Collect_job_classify.class, "select * from " + Collect_job_classify.TableName + " where classify_num = ? ", classifyNum).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat(classify.getClassify_name(), is(classifyName));
		}

		//再次执行插入操作，由于分类编号不能重复，因此插入操作失败
		String repeatString = new HttpClient()
				.addData("classify_num", classifyNum)
				.addData("classify_name", classifyName)
				.addData("user_id", TEST_USER_ID)
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("saveClassifyInfo")).getBodyString();
		ActionResult repeatResult = JsonUtil.toObjectSafety(repeatString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(repeatResult.isSuccess(), is(false));

		//以上全部执行成功，表示插入数据成功，删除刚刚插入的数据
		try(DatabaseWrapper db = new DatabaseWrapper()){
			int collectJobClassify = SqlOperator.execute(db, "delete from " + Collect_job_classify.TableName + " WHERE classify_num = ?", classifyNum);
			long collectJobClassifyNum = SqlOperator.queryNumber(db, "select count(1) from " + Collect_job_classify.TableName + " WHERE classify_num = ?", classifyNum)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的采集作业分类表数据有:" + collectJobClassify + "条", collectJobClassifyNum, is(0L));
			SqlOperator.commitTransaction(db);
		}

		//错误的数据访问1：不传入classify_num
		String withoutClassifyNum = new HttpClient()
				.addData("classify_name", classifyName)
				.addData("user_id", TEST_USER_ID)
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("saveClassifyInfo")).getBodyString();
		ActionResult withoutClassifyNumResult = JsonUtil.toObjectSafety(withoutClassifyNum, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(withoutClassifyNumResult.isSuccess(), is(false));

		//错误的数据访问2：不传入classify_name
		String withoutClassifyName = new HttpClient()
				.addData("classify_num", classifyNum)
				.addData("user_id", TEST_USER_ID)
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("saveClassifyInfo")).getBodyString();
		ActionResult withoutClassifyNameResult = JsonUtil.toObjectSafety(withoutClassifyName, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(withoutClassifyNameResult.isSuccess(), is(false));

		//错误的数据访问3：不传入user_id
		String withoutUserId = new HttpClient()
				.addData("classify_num", classifyNum)
				.addData("classify_name", classifyName)
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("saveClassifyInfo")).getBodyString();
		ActionResult withoutUserIdResult = JsonUtil.toObjectSafety(withoutUserId, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(withoutUserIdResult.isSuccess(), is(false));

		//错误的数据访问4：不传入Agent_id
		String withoutAgentId = new HttpClient()
				.addData("classify_num", classifyNum)
				.addData("classify_name", classifyName)
				.addData("user_id", TEST_USER_ID)
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("saveClassifyInfo")).getBodyString();
		ActionResult withoutAgentIdResult = JsonUtil.toObjectSafety(withoutAgentId, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(withoutAgentIdResult.isSuccess(), is(false));
	}

	/**
	 * 测试更新采集任务分类信息
	 *
	 * 正确数据访问1：
	 *      构造正确的数据，执行更新操作
	 *      验证操作的正确性
	 * 错误的数据访问1：构造的数据中，分类编号重复
	 * 错误的数据访问2：构造的数据中，缺少classify_id
	 * 错误的数据访问3：构造的数据中，缺少classify_num
	 * 错误的数据访问4：构造的数据中，缺少user_id
	 * 错误的数据访问5：构造的数据中，缺少Agent_id
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void updateClassifyInfo(){
		//正确数据访问1：
		//构造正确的数据，执行更新操作
		String classifyNum = "wzc_test_update_classify_num";
		String classifyName = "wzc_test_update_classify_name";
		String rightString = new HttpClient()
				.addData("classify_id", FIRST_CLASSIFY_ID)
				.addData("classify_num", classifyNum)
				.addData("classify_name", classifyName)
				.addData("user_id", TEST_USER_ID)
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("updateClassifyInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		//验证DB里面的数据是否正确
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Collect_job_classify classify = SqlOperator.queryOneObject(db, Collect_job_classify.class, "select * from " + Collect_job_classify.TableName + " where classify_num = ? ", classifyNum).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat(classify.getClassify_name(), is(classifyName));
		}

		//错误的数据访问1：构造的数据中，分类编号重复
		String repeatClassifyNum = "wzc_test_classify_num1";
		String repeatClassifyNumString = new HttpClient()
				.addData("classify_id", SECOND_CLASSIFY_ID)
				.addData("classify_num", repeatClassifyNum)
				.addData("classify_name", classifyName)
				.addData("user_id", TEST_USER_ID)
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("updateClassifyInfo")).getBodyString();
		ActionResult repeatClassifyNumResult = JsonUtil.toObjectSafety(repeatClassifyNumString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(repeatClassifyNumResult.isSuccess(), is(false));

		//错误的数据访问2：构造的数据中，缺少classify_id
		String withoutClassifyIdString = new HttpClient()
				.addData("classify_num", classifyNum)
				.addData("classify_name", classifyName)
				.addData("user_id", TEST_USER_ID)
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("updateClassifyInfo")).getBodyString();
		ActionResult withoutClassifyIdResult = JsonUtil.toObjectSafety(withoutClassifyIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(withoutClassifyIdResult.isSuccess(), is(false));

		//错误的数据访问3：构造的数据中，缺少classify_num
		String withoutClassifyNumString = new HttpClient()
				.addData("classify_id", SECOND_CLASSIFY_ID)
				.addData("classify_name", classifyName)
				.addData("user_id", TEST_USER_ID)
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("updateClassifyInfo")).getBodyString();
		ActionResult withoutClassifyNumResult = JsonUtil.toObjectSafety(withoutClassifyNumString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(withoutClassifyNumResult.isSuccess(), is(false));

		//错误的数据访问4：构造的数据中，缺少user_id
		String withoutUserIdString = new HttpClient()
				.addData("classify_id", SECOND_CLASSIFY_ID)
				.addData("classify_num", repeatClassifyNum)
				.addData("classify_name", classifyName)
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("updateClassifyInfo")).getBodyString();
		ActionResult withoutUserIdResult = JsonUtil.toObjectSafety(withoutUserIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(withoutUserIdResult.isSuccess(), is(false));

		//错误的数据访问5：构造的数据中，缺少Agent_id
		String withoutAgentIdtring = new HttpClient()
				.addData("classify_id", SECOND_CLASSIFY_ID)
				.addData("classify_num", repeatClassifyNum)
				.addData("classify_name", classifyName)
				.addData("user_id", TEST_USER_ID)
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("updateClassifyInfo")).getBodyString();
		ActionResult withoutAgentIdResult = JsonUtil.toObjectSafety(withoutAgentIdtring, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(withoutAgentIdResult.isSuccess(), is(false));
	}

	/**
	 * 测试删除采集任务分类信息
	 *
	 * 正确数据访问1：
	 *      删除前，确认待删除数据是否存在
	 *      构建正确的但是被databse_set表使用过的classify_id，执行删除操作，操作应该失败
	 *      删除后，确认数据是否被删除
	 * 正确的数据访问2：
	 *      删除前，确认待删除数据是否存在
	 *      构建正确的但是未被databse_set表使用过的classify_id，执行删除操作，操作应该成功
	 *      删除后，确认数据是否被删除
	 * 错误的数据访问1：传入错误的classifyId
	 * 错误的测试用例未达到三组:deleteClassifyInfo()方法只有一个入参
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteClassifyInfo(){
		try(DatabaseWrapper db = new DatabaseWrapper()){
			//正确数据访问1：
			//删除前，确认待删除数据是否存在
			OptionalLong before = SqlOperator.queryNumber(db, "select count(1) from collect_job_classify where classify_id = ?", FIRST_CLASSIFY_ID);
			assertThat("删除操作前，collect_job_classify表中的确存在这样一条数据", before.orElse(Long.MIN_VALUE), is(1L));

			//构建正确的但是被databse_set表使用过的classify_id，执行删除操作，操作应该失败
			String bodyString = new HttpClient()
					.addData("classifyId", FIRST_CLASSIFY_ID)
					.post(getActionUrl("deleteClassifyInfo")).getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败!"));
			assertThat(ar.isSuccess(), is(false));

			//删除后，确认数据是否被删除
			OptionalLong after = SqlOperator.queryNumber(db, "select count(1) from collect_job_classify where classify_id = ?", FIRST_CLASSIFY_ID);
			assertThat("因classify_id被databse_set表使用过，删除操作没有执行成功", after.orElse(Long.MIN_VALUE), is(1L));
		}

		try(DatabaseWrapper db = new DatabaseWrapper()){
			//正确数据访问1：
			//删除前，确认待删除数据是否存在
			OptionalLong before = SqlOperator.queryNumber(db, "select count(1) from collect_job_classify where classify_id = ?", THIRD_CLASSIFY_ID);
			assertThat("删除操作前，collect_job_classify表中的确存在这样一条数据", before.orElse(Long.MIN_VALUE), is(1L));

			//构建正确的但是未被databse_set表使用过的classify_id，执行删除操作，操作应该成功
			String bodyString = new HttpClient()
					.addData("classifyId", THIRD_CLASSIFY_ID)
					.post(getActionUrl("deleteClassifyInfo")).getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败!"));
			assertThat(ar.isSuccess(), is(true));

			//删除后，确认数据是否被删除
			OptionalLong after = SqlOperator.queryNumber(db, "select count(1) from collect_job_classify where classify_id = ?", THIRD_CLASSIFY_ID);
			assertThat("删除操作后，collect_job_classify表中这样一条数据没有了", after.orElse(Long.MIN_VALUE), is(0L));
		}

		//错误的数据访问1：传入错误的classifyId
		long wrongClassifyId = 12345L;
		String wrongClassifyIdString = new HttpClient()
				.addData("classifyId", wrongClassifyId)
				.post(getActionUrl("deleteClassifyInfo")).getBodyString();
		ActionResult wrongClassifyIdResult = JsonUtil.toObjectSafety(wrongClassifyIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongClassifyIdResult.isSuccess(), is(false));
	}

	/**
	 * 测试保存数据库采集Agent数据库配置信息
	 *
	 * 正确数据访问1：
	 *      构造正确数据，执行插入操作
	 *      验证DB里面的数据是否正确
	 * 正确数据访问2：
	 *      构造正确数据，执行更新操作
	 *      验证DB里面的数据是否正确
	 *
	 * 错误的数据访问1：新增数据时，缺少classfy_id
	 * 错误的数据访问2：新增数据时，缺少database_type
	 * 错误的数据访问3：新增数据时，输入了取值范围异常的database_type
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveDbConf(){
		//正确数据访问1：
		//构造正确数据，执行插入操作
		String jdbcURL = "jdbc:postgresql://127.0.0.1:31001/wzc_test_saveDbConf_database_name";
		String insertString = new HttpClient()
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("task_name", "wzc_test_saveDbConf_task_name")
				.addData("database_name", "wzc_test_saveDbConf_database_name")
				.addData("database_pad", "wzc_test_saveDbConf_database_pad")
				.addData("database_drive", "org.postgresql.Driver")
				.addData("database_type", DatabaseType.ApacheDerby.getCode())
				.addData("user_name", "wzc_test_saveDbConf_user_name")
				.addData("database_ip", "127.0.0.1")
				.addData("database_port", "31001")
				.addData("classify_id", FIRST_CLASSIFY_ID)
				.addData("jdbc_url", jdbcURL)
				.post(getActionUrl("saveDbConf")).getBodyString();
		ActionResult insertRuselt = JsonUtil.toObjectSafety(insertString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(insertRuselt.isSuccess(), is(true));
		Integer returnValue = (Integer) insertRuselt.getData();
		assertThat("该方法的返回值不为空", returnValue != null, is(true));

		//验证DB里面的数据是否正确
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Database_set classify = SqlOperator.queryOneObject(db, Database_set.class, "select * from " + Database_set.TableName + " where agent_id = ? and database_type = ?", FIRST_DB_AGENT_ID, DatabaseType.ApacheDerby.getCode()).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat(classify.getDatabase_name(), is("wzc_test_saveDbConf_database_name"));
			assertThat(classify.getTask_name(), is("wzc_test_saveDbConf_task_name"));
			assertThat(classify.getUser_name(), is("wzc_test_saveDbConf_user_name"));
			assertThat(classify.getDatabase_pad(), is("wzc_test_saveDbConf_database_pad"));
			assertThat(classify.getDatabase_ip(), is("127.0.0.1"));
			assertThat(classify.getDatabase_port(), is("31001"));
		}

		//经过以上步骤后，表名本次新增操作已经成功，要把本次新增的数据删除掉
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			int databaseSet = SqlOperator.execute(db, "delete from " + Database_set.TableName + " WHERE task_name = ?", "wzc_test_saveDbConf_task_name");
			long databaseSetNum = SqlOperator.queryNumber(db, "select count(1) from " + Database_set.TableName + " WHERE task_name = ?", "wzc_test_saveDbConf_task_name")
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的原系统数据库设置表数据有:" + databaseSet + "条", databaseSet, is(1));
			assertThat(databaseSetNum, is(0L));
			SqlOperator.commitTransaction(db);
		}

		//正确数据访问2：
		//构造正确数据，执行更新操作
		String updateJDBCURL = "jdbc:postgresql://127.0.0.1:31001/wzc_test_saveDbConf_update_database_name";
		String updateString = new HttpClient()
				.addData("database_id", 1001L)
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("task_name", "wzc_test_saveDbConf_update_task_name")
				.addData("database_name", "wzc_test_saveDbConf_update_database_name")
				.addData("database_pad", "wzc_test_saveDbConf_update_database_pad")
				.addData("database_drive", "org.postgresql.Driver")
				.addData("database_type", DatabaseType.ApacheDerby.getCode())
				.addData("user_name", "wzc_test_saveDbConf_update_user_name")
				.addData("database_ip", "127.0.0.1")
				.addData("database_port", "31001")
				.addData("classify_id", FIRST_CLASSIFY_ID)
				.addData("jdbc_url", updateJDBCURL)
				.post(getActionUrl("saveDbConf")).getBodyString();
		ActionResult updateRuselt = JsonUtil.toObjectSafety(updateString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(updateRuselt.isSuccess(), is(true));

		//验证DB里面的数据是否正确
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Database_set classify = SqlOperator.queryOneObject(db, Database_set.class, "select * from " + Database_set.TableName + " where database_id = ?", 1001L).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat(classify.getDatabase_name(), is("wzc_test_saveDbConf_update_database_name"));
			assertThat(classify.getTask_name(), is("wzc_test_saveDbConf_update_task_name"));
			assertThat(classify.getUser_name(), is("wzc_test_saveDbConf_update_user_name"));
			assertThat(classify.getDatabase_pad(), is("wzc_test_saveDbConf_update_database_pad"));
		}

		//错误的数据访问1：新增数据时，缺少classfy_id
		String withoutClassfyIdString = new HttpClient()
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("task_name", "wzc_test_saveDbConf_task_name")
				.addData("database_name", "wzc_test_saveDbConf_database_name")
				.addData("database_pad", "wzc_test_saveDbConf_database_pad")
				.addData("database_drive", "org.postgresql.Driver")
				.addData("database_type", DatabaseType.ApacheDerby.getCode())
				.addData("user_name", "wzc_test_saveDbConf_user_name")
				.addData("database_ip", "127.0.0.1")
				.addData("database_port", "31001")
				.addData("jdbc_url", jdbcURL)
				.post(getActionUrl("saveDbConf")).getBodyString();
		ActionResult withoutClassfyIdRuselt = JsonUtil.toObjectSafety(withoutClassfyIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(withoutClassfyIdRuselt.isSuccess(), is(false));

		//错误的数据访问2：新增数据时，缺少database_type
		String withoutDatabaseTypeString = new HttpClient()
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("task_name", "wzc_test_saveDbConf_task_name")
				.addData("database_name", "wzc_test_saveDbConf_database_name")
				.addData("database_pad", "wzc_test_saveDbConf_database_pad")
				.addData("database_drive", "org.postgresql.Driver")
				.addData("user_name", "wzc_test_saveDbConf_user_name")
				.addData("database_ip", "127.0.0.1")
				.addData("database_port", "31001")
				.addData("classify_id", FIRST_CLASSIFY_ID)
				.addData("jdbc_url", jdbcURL)
				.post(getActionUrl("saveDbConf")).getBodyString();
		ActionResult withoutDatabaseTypeRuselt = JsonUtil.toObjectSafety(withoutDatabaseTypeString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(withoutDatabaseTypeRuselt.isSuccess(), is(false));

		//错误的数据访问3：新增数据时，输入了取值范围异常的database_type
		String wrongDatabaseType = "15";
		String wrongDatabaseTypeString = new HttpClient()
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("task_name", "wzc_test_saveDbConf_task_name")
				.addData("database_name", "wzc_test_saveDbConf_database_name")
				.addData("database_pad", "wzc_test_saveDbConf_database_pad")
				.addData("database_type", wrongDatabaseType)
				.addData("database_drive", "org.postgresql.Driver")
				.addData("user_name", "wzc_test_saveDbConf_user_name")
				.addData("database_ip", "127.0.0.1")
				.addData("database_port", "31001")
				.addData("classify_id", FIRST_CLASSIFY_ID)
				.addData("jdbc_url", jdbcURL)
				.post(getActionUrl("saveDbConf")).getBodyString();
		ActionResult wrongoutDatabaseTypeRuselt = JsonUtil.toObjectSafety(wrongDatabaseTypeString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongoutDatabaseTypeRuselt.isSuccess(), is(false));
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
