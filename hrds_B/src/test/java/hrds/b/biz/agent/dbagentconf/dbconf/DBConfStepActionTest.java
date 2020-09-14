package hrds.b.biz.agent.dbagentconf.dbconf;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.b.biz.agent.bean.DBConnectionProp;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Collect_job_classify;
import hrds.commons.entity.Database_set;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "DBConfStepAction单元测试类", author = "WangZhengcheng")
public class DBConfStepActionTest extends WebBaseTestCase {


	private static final InitAndDestDataForDBConf initData = new InitAndDestDataForDBConf();
	//测试数据用户ID
	private final long TEST_USER_ID = initData.baseInitData.TEST_USER_ID;
	private final long threadId = initData.baseInitData.threadId;
	//source_id
	private final long SOURCE_ID = initData.baseInitData.SOURCE_ID;
	private final long FIRST_DB_AGENT_ID = initData.baseInitData.FIRST_DB_AGENT_ID;
	private final long SECOND_DB_AGENT_ID = initData.baseInitData.SECOND_DB_AGENT_ID;
	private final long FIRST_CLASSIFY_ID = initData.baseInitData.FIRST_CLASSIFY_ID;
	private final long SECOND_CLASSIFY_ID = initData.baseInitData.SECOND_CLASSIFY_ID;
	private final long THIRD_CLASSIFY_ID = initData.THIRD_CLASSIFY_ID;

	/**
	 * 为每个方法的单元测试初始化测试数据
	 * <p>
	 * 1、构建data_source表测试数据
	 * 2、构建agent_info表测试数据
	 * 3、构建database_set表测试数据，只构建数据库直连采集
	 * 4、构建collect_job_classify表测试数据
	 * 5、插入数据
	 * <p>
	 * 测试数据：
	 * 1、data_source表：有1条数据，source_id为1
	 * 2、Agent_info表：有2条数据,全部是数据库采集Agent，agent_id分别为7001，7002,source_id为1
	 * 3、database_set表：有2条数据,database_id为1001,1002,
	 * agent_id分别为7001,7002，1001的classifyId是10086，1002的classifyId是10010
	 * 1001设置完成并发送成功(is_sendok)
	 * 4、collect_job_classify表：有3条数据，
	 * classify_id为10086L、10010L、12306L，
	 * agent_id分别为7001L、7002L，7001L,user_id为9997L
	 */
	@Before
	public void before() {
		initData.before();
		//模拟登陆
		ActionResult actionResult = login();
		assertThat("模拟登陆", actionResult.isSuccess(), is(true));
	}

	/**
	 * 测试数据库直连采集，根据databaseId进行查询并在页面上回显数据源配置信息
	 * <p>
	 * 正确数据访问1：传入is_sendok字段为1的databaseId
	 * 错误的数据访问1：传入is_sendok字段为0的databaseId 错误的数据访问2：传入不存在的databaseId
	 * 错误的测试用例未达到三组:两组错误的测试用例已经可以覆盖所有可能出现的错误情况
	 */
	@Test
	public void getDBConfInfo() {
		//正确数据访问1：传入is_sendok字段为1的databaseId
		String rightString = new HttpClient()
				.addData("databaseId", initData.baseInitData.SECOND_DATABASE_SET_ID)
				.post(getActionUrl("getDBConfInfo")).getBodyString();

		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Result data = rightResult.getDataForResult();
		assertThat("根据测试数据，查询到的数据库配置信息应该有" + data.getRowCount() + "条", data.getRowCount(), is(1));
		assertThat(data.getLong(0, "database_id"), is(initData.baseInitData.SECOND_DATABASE_SET_ID));
		assertThat(data.getLong(0, "agent_id"), is(SECOND_DB_AGENT_ID));
		assertThat(data.getString(0, "database_number"), is("cs02" + initData.baseInitData.threadId));
		assertThat(data.getString(0, "task_name"), is("wzcTaskName" + initData.baseInitData.threadId));
		assertThat(data.getString(0, "database_name"), is("postgresql"));
		assertThat(data.getString(0, "database_drive"), is("org.postgresql.Driver"));
		assertThat(data.getString(0, "database_type"), is("11"));
		assertThat(data.getString(0, "user_name"), is("hrsdxg"));
		assertThat(data.getString(0, "database_ip"), is("127.0.0.1"));
		assertThat(data.getString(0, "database_port"), is("8888"));
		assertThat(data.getString(0, "db_agent"), is(IsFlag.Fou.getCode()));
		assertThat(data.getString(0, "is_sendok"), is(IsFlag.Fou.getCode()));
		assertThat(data.getString(0, "jdbc_url"), is("jdbc:postgresql://127.0.0.1:8888/postgresql"));
		assertThat(data.getLong(0, "classify_id"), is(SECOND_CLASSIFY_ID));
		assertThat(data.getString(0, "classify_num"), is("wzc_test_classify_num1"));
		assertThat(data.getString(0, "classify_name"), is("wzc_test_classify_name1"));


		//错误的数据访问1：传入不存在的databaseId
		String secWrongString = new HttpClient()
				.addData("databaseId", 1003L + threadId)
				.post(getActionUrl("getDBConfInfo")).getBodyString();
		ActionResult secWrongResult = JsonUtil.toObjectSafety(secWrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(secWrongResult.isSuccess(), is(true));
		assertThat(secWrongResult.getDataForResult().getRowCount(), is(0));

	}

//	/**
//	 * 测试通过对数据库IP和端口号进行分组筛选数据库直连采集配置信息
//	 * <p>
//	 * 正确数据访问1：构造一批IP和端口号相同的数据，直接访问方法，断言返回结果是否正确 错误的测试用例未达到三组 : 所有返回结果均根据实际情况
//	 */
//	@Test
//	public void getHisConnection() {
//
//		//正确数据访问1：构造一批IP和端口号相同的数据，直接访问方法，断言返回结果是否正确
//		try (DatabaseWrapper db = new DatabaseWrapper()) {
//			//创建一个存放数据库采集任务配置信息的主键
//			Long[] databases = new Long[2];
//			String database_name = null;
//			String user_name = null;
//			String database_port = null;
//			String database_pad = null;
//			String database_ip = null;
//			Long agent_id = PrimayKeyGener.getNextId();
//			for (int i = 0; i < 2; i++) {
//				database_name = i % 2 == 0 ? "postgresql" : "mysql";
//				user_name = i % 2 == 0 ? "postgresql" : "mysql";
//				database_port = i % 2 == 0 ? "8888" : "32001";
//				database_pad = i % 2 == 0 ? "postgresql" : "mysql";
//				database_ip = i % 2 == 0 ? "127.0.0.1" : "192.168.123.0";
//				Database_set database_set = new Database_set();
//				databases[i] = PrimayKeyGener.getNextId();
//				database_set.setDatabase_id(databases[i]);
//				database_set.setAgent_id(agent_id);
//				database_set.setDatabase_number("lqcscs" + i + initData.baseInitData.threadId);
//				database_set.setTask_name("lqcscs" + i + initData.baseInitData.threadId);
//				database_set.setDatabase_name(database_name);
//				database_set.setDatabase_pad(database_pad);
//				database_set.setUser_name(user_name);
//				database_set.setDatabase_port(database_port);
//				database_set.setDatabase_ip(database_ip);
//				database_set.setDb_agent(IsFlag.Fou.getCode());
//				database_set.setIs_sendok(IsFlag.Shi.getCode());
//				database_set.setClassify_id(SECOND_CLASSIFY_ID);
//				database_set.add(db);
//			}
//
//			String rightString = new HttpClient()
//				.addData("agentId", agent_id)
//				.post(getActionUrl("getHisConnection")).getBodyString();
//
//			ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败!"));
//			assertThat(rightResult.isSuccess(), is(true));
//
//			Result rightData = rightResult.getDataForResult();
//			assertThat("获得的数据有两条", rightData.getRowCount(), is(2));
//			for (int i = 0; i < rightData.getRowCount(); i++) {
//				if (rightData.getString(i, "database_port").equalsIgnoreCase("8888") && rightData.getString(i, "database_ip")
//					.equalsIgnoreCase("127.0.0.1")) {
//					assertThat("数据库名称为postgresql", rightData.getString(i, "database_name"), is("postgresql"));
//					assertThat("数据库密码为postgresql", rightData.getString(i, "database_pad"), is("postgresql"));
//					assertThat("数据库用户名", rightData.getString(i, "user_name"), is("postgresql"));
//				} else if (rightData.getString(i, "database_port").equalsIgnoreCase("32001") && rightData
//					.getString(i, "database_ip").equalsIgnoreCase("192.168.123.0")) {
//					assertThat("数据库名称为mysql", rightData.getString(i, "database_name"), is("mysql"));
//					assertThat("数据库密码为mysql", rightData.getString(i, "database_pad"), is("mysql"));
//					assertThat("数据库用户名", rightData.getString(i, "user_name"), is("mysql"));
//				} else {
//					assertThat("获得的数据不符合预期，IP地址和端口号如下" + rightData.getString(i, "database_ip") + rightData
//						.getString(i, "database_port"), true, is(false));
//				}
//			}
//
//			//删除模拟的数据结果
//			Assembler assembler = Assembler.newInstance().addSql("delete from " + Database_set.TableName + " where")
//				.addORParam("database_id", databases, "");
//			int execute = SqlOperator.execute(db, assembler.sql(), assembler.params());
//			SqlOperator.execute(db, "DELETE FROM" + Database_set.TableName + " WHERE agent_id = ?", agent_id);
//			assertThat("删除的数据量和创建的不一致", execute, is(databases.length));
//			SqlOperator.commitTransaction(db);
//		}
//	}

	/**
	 * 测试根据数据库类型和端口获得数据库连接url等信息
	 * <p>
	 * 正确数据访问1：构建mysql数据库访问场景，断言得到的数据是否正确 正确数据访问2：构建taradata数据库访问场景，携带端口号，断言得到的数据是否正确 正确数据访问3：构建taradata数据库访问场景，不携带端口号，断言得到的数据是否正确
	 * 正确数据访问1：构建mysql数据库访问场景，断言得到的数据是否正确 错误的数据访问1：构建dbType不在DatabaseType代码项中的code值，断言得到的数据是否正确 错误访问场景不足的原因：该方法调用只需要一个参数
	 */
	@Test
	public void getDBConnectionProp() {
		String wrongDBType = "123";
		//正确数据访问1：构建mysql数据库访问场景，断言得到的数据是否正确
		String rightString = new HttpClient()
				.addData("dbType", DatabaseType.MYSQL.getCode())
				.post(getActionUrl("getDBConnectionProp")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		DBConnectionProp dbConnectionProp = JSONObject.parseObject(rightResult.getData().toString(), DBConnectionProp.class);

		assertThat("根据测试数据，查询到的数据库连接信息应该有1条", true, is(true));
		assertThat(dbConnectionProp.getUrlPrefix(), is("jdbc:mysql://"));
		assertThat(dbConnectionProp.getIpPlaceholder(), is(":"));
		assertThat(dbConnectionProp.getPortPlaceholder(), is("/"));
		assertThat(dbConnectionProp.getUrlSuffix(), is("?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull"));

		//正确数据访问2：构建taradata数据库访问场景，携带端口号，断言得到的数据是否正确
		String rightStringTwo = new HttpClient()
				.addData("dbType", DatabaseType.TeraData.getCode())
				.addData("port", "8080")
				.post(getActionUrl("getDBConnectionProp")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		DBConnectionProp dbConnectionPropTwo = JSONObject
				.parseObject(rightResultTwo.getData().toString(), DBConnectionProp.class);

		assertThat("根据测试数据，查询到的数据库连接信息应该有1条", true, is(true));
		assertThat(dbConnectionPropTwo.getUrlPrefix(), is("jdbc:teradata://"));
		assertThat(dbConnectionPropTwo.getIpPlaceholder(), is("/TMODE=TERA,CHARSET=ASCII,CLIENT_CHARSET=cp936,DATABASE="));
		assertThat(dbConnectionPropTwo.getPortPlaceholder(), is(""));
		assertThat(dbConnectionPropTwo.getUrlSuffix(), is(",lob_support=off,DBS_PORT="));

		//正确数据访问3：构建taradata数据库访问场景，不携带端口号，断言得到的数据是否正确
		String rightStringThree = new HttpClient()
				.addData("dbType", DatabaseType.TeraData.getCode())
				.post(getActionUrl("getDBConnectionProp")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));

		DBConnectionProp dbConnectionPropThree = JSONObject
				.parseObject(rightResultThree.getData().toString(), DBConnectionProp.class);

		assertThat("根据测试数据，查询到的数据库连接信息应该有1条", true, is(true));
		assertThat(dbConnectionPropThree.getUrlPrefix(), is("jdbc:teradata://"));
		assertThat(dbConnectionPropThree.getIpPlaceholder(), is("/TMODE=TERA,CHARSET=ASCII,CLIENT_CHARSET=cp936,DATABASE="));
		assertThat(dbConnectionPropThree.getPortPlaceholder(), is(""));
		assertThat(dbConnectionPropThree.getUrlSuffix(), is(",lob_support=off"));

		//错误的数据访问1：构建dbType不在DatabaseType代码项中的code值，断言得到的数据是否正确
		String wrongString = new HttpClient()
				.addData("dbType", wrongDBType)
				.post(getActionUrl("getDBConnectionProp")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试根据数据库类型获取数据库驱动
	 * <p>
	 * 正确数据访问1：构建mysql数据库访问场景，断言得到的数据是否正确 错误的数据访问1：构建dbType不在DatabaseType代码项中的code值，断言得到的数据是否正确 错误访问场景不足的原因：该方法调用只需要一个参数
	 */
	@Test
	public void getJDBCDriver() {
		//正确数据访问1：构建mysql数据库访问场景，断言得到的数据是否正确
		String rightString = new HttpClient()
				.addData("dbType", DatabaseType.MYSQL.getCode())
				.post(getActionUrl("getJDBCDriver")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		String rightData = (String) rightResult.getData();

		assertThat(rightData, is("com.mysql.jdbc.Driver"));

		//错误的数据访问1：构建dbType不在DatabaseType代码项中的code值，断言得到的数据是否正确
		String wrongString = new HttpClient()
				.addData("dbType", 123)
				.post(getActionUrl("getJDBCDriver")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试根据classifyId判断当前分类是否被使用，如果被使用，则不能编辑，否则，可以编辑
	 * <p>
	 * 正确数据访问1：传入正确的已被database_set表使用的classifyId 正确数据访问2：传入正确的未被database_set表使用的classifyId 错误的数据访问1：传入错误的classifyId
	 * 错误访问场景不足的原因：该方法调用只需要一个参数
	 */
	@Test
	public void checkClassifyId() {
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
		ActionResult unusedClassifyIdResult = JsonUtil.toObjectSafety(unusedClassifyIdString, ActionResult.class)
				.orElseThrow(()
						-> new BusinessException("连接失败!"));
		assertThat(unusedClassifyIdResult.isSuccess(), is(true));
		boolean checkResult = (boolean) unusedClassifyIdResult.getData();
		assertThat(checkResult, is(true));

		//错误的数据访问1：错误的classifyId
		String wrongClassifyIdString = new HttpClient()
				.addData("classifyId", 999999L)
				.post(getActionUrl("checkClassifyId")).getBodyString();
		ActionResult wrongClassifyIdStringResult = JsonUtil.toObjectSafety(wrongClassifyIdString, ActionResult.class)
				.orElseThrow(()
						-> new BusinessException("连接失败!"));
		assertThat(wrongClassifyIdStringResult.isSuccess(), is(false));
	}

	/**
	 * 测试根据sourceId获取分类信息
	 * <p>
	 * 正确数据访问1：传入正确的sourceId 错误的数据访问1：传入错误的sourceId 错误访问场景不足的原因：该方法调用只需要一个参数
	 */
	@Test
	public void getClassifyInfo() {
		//正确数据访问1：传入正确的userId和正确的sourceId
		String rightString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("getClassifyInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		List<Collect_job_classify> data = rightResult.getDataForEntityList(Collect_job_classify.class);
		assertThat("查询得到的分类信息数据有" + data.size() + "条", data.size(), is(3));

		//错误的数据访问2：传入错误的sourceId
		String wrongSourceString = new HttpClient()
				.addData("sourceId", 2L)
				.post(getActionUrl("getClassifyInfo")).getBodyString();
		ActionResult wrongSourceResult = JsonUtil.toObjectSafety(wrongSourceString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongSourceResult.isSuccess(), is(true));
		List<Collect_job_classify> wrongSourceData = wrongSourceResult.getDataForEntityList(Collect_job_classify.class);
		assertThat("传入错误的sourceId,获得的数据为" + wrongSourceData.size() + "条", wrongSourceData.size(), is(0));
	}

	/**
	 * 测试保存采集任务分类信息
	 * <p>
	 * 正确数据访问1： 构造正确数据，执行插入操作 验证DB里面的数据是否正确 再次执行插入操作，由于分类编号不能重复，因此插入操作失败 以上全部执行成功，表示插入数据成功，删除刚刚插入的数据 错误的数据访问1：不传入classify_num
	 * 错误的数据访问2：不传入classify_name 错误的数据访问3：不传入Agent_id
	 */
	@Test
	public void saveClassifyInfo() {
		//正确数据访问1：
		//构造正确数据，执行插入操作
		String classifyNum = "12138" + initData.baseInitData.threadId;
		String classifyName = "q1w2" + initData.baseInitData.threadId;
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
			Collect_job_classify classify = SqlOperator.queryOneObject(db, Collect_job_classify.class,
					"select * from " + Collect_job_classify.TableName + " where classify_num = ? ", classifyNum)
					.orElseThrow(() -> new BusinessException("未能找到分类对象"));
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
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			int collectJobClassify = SqlOperator
					.execute(db, "delete from " + Collect_job_classify.TableName + " WHERE classify_num = ?", classifyNum);
			long collectJobClassifyNum = SqlOperator
					.queryNumber(db, "select count(1) from " + Collect_job_classify.TableName + " WHERE classify_num = ?",
							classifyNum)
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
		ActionResult withoutClassifyNumResult = JsonUtil.toObjectSafety(withoutClassifyNum, ActionResult.class)
				.orElseThrow(()
						-> new BusinessException("连接失败!"));
		assertThat(withoutClassifyNumResult.isSuccess(), is(false));

		//错误的数据访问2：不传入classify_name
		String withoutClassifyName = new HttpClient()
				.addData("classify_num", classifyNum)
				.addData("user_id", TEST_USER_ID)
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("saveClassifyInfo")).getBodyString();
		ActionResult withoutClassifyNameResult = JsonUtil.toObjectSafety(withoutClassifyName, ActionResult.class)
				.orElseThrow(()
						-> new BusinessException("连接失败!"));
		assertThat(withoutClassifyNameResult.isSuccess(), is(false));

		//错误的数据访问3：不传入Agent_id
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
	 * <p>
	 * 正确数据访问1： 构造正确的数据，执行更新操作 验证操作的正确性 错误的数据访问1：构造的数据中，分类编号重复 错误的数据访问2：构造的数据中，缺少classify_id 错误的数据访问3：构造的数据中，缺少classify_num
	 * 错误的数据访问4：构造的数据中，缺少user_id 错误的数据访问5：构造的数据中，缺少Agent_id
	 */
	@Test
	public void updateClassifyInfo() {
		//正确数据访问1：
		//构造正确的数据，执行更新操作
		String classifyNum = "qweras" + initData.baseInitData.threadId;
		String classifyName = "qweras" + initData.baseInitData.threadId;
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
			Collect_job_classify classify = SqlOperator.queryOneObject(db, Collect_job_classify.class,
					"select * from " + Collect_job_classify.TableName + " where classify_id = ? ", FIRST_CLASSIFY_ID)
					.orElseThrow(() -> new BusinessException("未能找到分类对象"));
			assertThat(classify.getClassify_name(), is(classifyName));
		}

		//错误的数据访问1：构造的数据中，分类编号重复
		String repeatClassifyNumString = new HttpClient()
				.addData("classify_id", SECOND_CLASSIFY_ID)
				.addData("classify_num", classifyNum)
				.addData("classify_name", classifyName)
				.addData("user_id", TEST_USER_ID)
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("updateClassifyInfo")).getBodyString();
		ActionResult repeatClassifyNumResult = JsonUtil.toObjectSafety(repeatClassifyNumString, ActionResult.class)
				.orElseThrow(()
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
		ActionResult withoutClassifyIdResult = JsonUtil.toObjectSafety(withoutClassifyIdString, ActionResult.class)
				.orElseThrow(()
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
		ActionResult withoutClassifyNumResult = JsonUtil.toObjectSafety(withoutClassifyNumString, ActionResult.class)
				.orElseThrow(()
						-> new BusinessException("连接失败!"));
		assertThat(withoutClassifyNumResult.isSuccess(), is(false));

		//错误的数据访问4：构造的数据中，缺少user_id
//		String withoutUserIdString = new HttpClient()
//			.addData("classify_id", SECOND_CLASSIFY_ID)
//			.addData("classify_num", "233432")
//			.addData("classify_name", classifyName)
//			.addData("agent_id", FIRST_DB_AGENT_ID)
//			.addData("sourceId", SOURCE_ID)
//			.post(getActionUrl("updateClassifyInfo")).getBodyString();
//		ActionResult withoutUserIdResult = JsonUtil.toObjectSafety(withoutUserIdString, ActionResult.class).orElseThrow(()
//			-> new BusinessException("连接失败!"));
//		assertThat(withoutUserIdResult.isSuccess(), is(false));

		//错误的数据访问5：构造的数据中，缺少Agent_id
		String withoutAgentIdtring = new HttpClient()
				.addData("classify_id", SECOND_CLASSIFY_ID)
				.addData("classify_num", "123")
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
	 * <p>
	 * 正确数据访问1： 删除前，确认待删除数据是否存在 构建正确的但是被databse_set表使用过的classify_id，执行删除操作，操作应该失败 删除后，确认数据是否被删除 正确的数据访问2： 删除前，确认待删除数据是否存在
	 * 构建正确的但是未被databse_set表使用过的classify_id，执行删除操作，操作应该成功 删除后，确认数据是否被删除 错误的数据访问1：传入错误的classifyId
	 * 错误的测试用例未达到三组:deleteClassifyInfo()方法只有一个入参
	 */
	@Test
	public void deleteClassifyInfo() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//正确数据访问1：
			//删除前，确认待删除数据是否存在
			long before = SqlOperator
					.queryNumber(db, "select count(1) from collect_job_classify where classify_id = ?", FIRST_CLASSIFY_ID)
					.orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("删除操作前，collect_job_classify表中的确存在这样一条数据", before, is(1L));

			//构建正确的但是被databse_set表使用过的classify_id，执行删除操作，操作应该失败
			String bodyString = new HttpClient()
					.addData("classifyId", FIRST_CLASSIFY_ID)
					.post(getActionUrl("deleteClassifyInfo")).getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败!"));
			assertThat(ar.isSuccess(), is(false));

			//删除后，确认数据是否被删除
			long after = SqlOperator
					.queryNumber(db, "select count(1) from collect_job_classify where classify_id = ?", FIRST_CLASSIFY_ID)
					.orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("因classify_id被databse_set表使用过，删除操作没有执行成功", after, is(1L));
		}

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//正确数据访问1：
			//删除前，确认待删除数据是否存在
			long before = SqlOperator
					.queryNumber(db, "select count(1) from collect_job_classify where classify_id = ?", THIRD_CLASSIFY_ID)
					.orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("删除操作前，collect_job_classify表中的确存在这样一条数据", before, is(1L));

			//构建正确的但是未被databse_set表使用过的classify_id，执行删除操作，操作应该成功
			String bodyString = new HttpClient()
					.addData("classifyId", THIRD_CLASSIFY_ID)
					.post(getActionUrl("deleteClassifyInfo")).getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败!"));
			assertThat(ar.isSuccess(), is(true));

			//删除后，确认数据是否被删除
			long after = SqlOperator
					.queryNumber(db, "select count(1) from collect_job_classify where classify_id = ?", THIRD_CLASSIFY_ID)
					.orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("删除操作后，collect_job_classify表中这样一条数据没有了", after, is(0L));
		}

		//错误的数据访问1：传入错误的classifyId
		long wrongClassifyId = 12345L;
		String wrongClassifyIdString = new HttpClient()
				.addData("classifyId", wrongClassifyId)
				.post(getActionUrl("deleteClassifyInfo")).getBodyString();
		ActionResult wrongClassifyIdResult = JsonUtil.toObjectSafety(wrongClassifyIdString, ActionResult.class)
				.orElseThrow(()
						-> new BusinessException("连接失败!"));
		assertThat(wrongClassifyIdResult.isSuccess(), is(false));
	}

	/**
	 * 测试保存数据库采集Agent数据库配置信息
	 * <p>
	 * 正确数据访问1： 构造正确数据，执行插入操作 验证DB里面的数据是否正确 正确数据访问2： 构造正确数据，执行更新操作 验证DB里面的数据是否正确
	 * <p>
	 * 错误的数据访问1：新增数据时，缺少classfy_id 错误的数据访问2：新增数据时，缺少database_type 错误的数据访问3：新增数据时，输入了取值范围异常的database_type
	 * 错误的数据访问4：新增数据时，缺少数据库驱动 错误的数据访问5：新增数据时，缺少数据库名称 错误的数据访问6：新增数据时，缺少数据库IP 错误的数据访问7：新增数据时，缺少数据库端口号 错误的数据访问8：新增数据时，缺少用户名
	 * 错误的数据访问9：新增数据时，缺少数据库密码 错误的数据访问10：新增数据时，缺少JDBCURL 错误的数据访问10：新增数据时，缺少agent_id 错误的数据访问11：新增数据时，作业编号重复
	 */
	@Test
	public void saveDbConf() {
		//正确数据访问1：
		//构造正确数据，执行插入操作
		String jdbcURL = "jdbc:postgresql://127.0.0.1:31001/wzc_test_saveDbConf_database_name";
		String insertString = new HttpClient()
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("task_name", "wzc_test_saveDbConf_task_name" + initData.baseInitData.threadId)
				.addData("database_name", "wzc_test_saveDbConf_database_name" + initData.baseInitData.threadId)
				.addData("database_number", "1001" + initData.baseInitData.threadId)
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
		//验证DB里面的数据是否正确
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Database_set classify = SqlOperator.queryOneObject(db, Database_set.class,
					"select * from " + Database_set.TableName + " where database_id = ? and database_type = ?",
					Long.parseLong(insertRuselt.getData().toString()),
					DatabaseType.ApacheDerby.getCode()).orElseThrow(() -> new BusinessException("未获取到数据库采集任务"));
			assertThat(classify.getDatabase_name(),
					is("wzc_test_saveDbConf_database_name" + initData.baseInitData.threadId));
			assertThat(classify.getTask_name(), is("wzc_test_saveDbConf_task_name" + initData.baseInitData.threadId));
			assertThat(classify.getUser_name(), is("wzc_test_saveDbConf_user_name"));
			assertThat(classify.getDatabase_pad(), is("wzc_test_saveDbConf_database_pad"));
			assertThat(classify.getDatabase_ip(), is("127.0.0.1"));
			assertThat(classify.getDatabase_port(), is("31001"));
		}

		//经过以上步骤后，表名本次新增操作已经成功，要把本次新增的数据删除掉
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			int databaseSet = SqlOperator.execute(db, "delete from " + Database_set.TableName + " WHERE task_name = ?",
					"wzc_test_saveDbConf_task_name" + initData.baseInitData.threadId);
			long databaseSetNum = SqlOperator
					.queryNumber(db, "select count(1) from " + Database_set.TableName + " WHERE task_name = ?",
							"wzc_test_saveDbConf_task_name" + initData.baseInitData.threadId)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的原系统数据库设置表数据有:" + databaseSet + "条", databaseSet, is(1));
			assertThat(databaseSetNum, is(0L));
			SqlOperator.commitTransaction(db);
		}

		//正确数据访问2：
		//构造正确数据，执行更新操作
		String updateJDBCURL = "jdbc:postgresql://127.0.0.1:31001/wzc_test_saveDbConf_update_database_name";
		String updateString = new HttpClient()
				.addData("database_id", initData.baseInitData.FIRST_DATABASE_SET_ID)
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("task_name", "wzc_test_saveDbConf_update_task_name" + initData.baseInitData.threadId)
				.addData("database_number", "1001" + initData.baseInitData.threadId)
				.addData("database_name", "wzc_test_saveDbConf_update_database_name" + initData.baseInitData.threadId)
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
			Database_set classify = SqlOperator
					.queryOneObject(db, Database_set.class, "select * from " + Database_set.TableName + " where database_id = ?",
							initData.baseInitData.FIRST_DATABASE_SET_ID).orElseThrow(() -> new BusinessException("未获取到数据库采集任务"));
			assertThat(classify.getDatabase_name(),
					is("wzc_test_saveDbConf_update_database_name" + initData.baseInitData.threadId));
			assertThat(classify.getTask_name(), is("wzc_test_saveDbConf_update_task_name" + initData.baseInitData.threadId));
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
		ActionResult withoutClassfyIdRuselt = JsonUtil.toObjectSafety(withoutClassfyIdString, ActionResult.class)
				.orElseThrow(()
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
		ActionResult withoutDatabaseTypeRuselt = JsonUtil.toObjectSafety(withoutDatabaseTypeString, ActionResult.class)
				.orElseThrow(()
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
		ActionResult wrongoutDatabaseTypeRuselt = JsonUtil.toObjectSafety(wrongDatabaseTypeString, ActionResult.class)
				.orElseThrow(()
						-> new BusinessException("连接失败!"));
		assertThat(wrongoutDatabaseTypeRuselt.isSuccess(), is(false));

		//错误的数据访问4：新增数据时，缺少数据库驱动
		String updateStringOne = new HttpClient()
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("task_name", "wzc_test_saveDbConf_update_task_name")
				.addData("database_number", "1001")
				.addData("database_name", "wzc_test_saveDbConf_update_database_name")
				.addData("database_pad", "wzc_test_saveDbConf_update_database_pad")
				.addData("database_type", DatabaseType.ApacheDerby.getCode())
				.addData("user_name", "wzc_test_saveDbConf_update_user_name")
				.addData("database_ip", "127.0.0.1")
				.addData("database_port", "31001")
				.addData("classify_id", FIRST_CLASSIFY_ID)
				.addData("jdbc_url", updateJDBCURL)
				.post(getActionUrl("saveDbConf")).getBodyString();
		ActionResult wrongRuseltOne = JsonUtil.toObjectSafety(updateStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongRuseltOne.isSuccess(), is(false));

		//错误的数据访问5：新增数据时，缺少数据库名称
		String updateStringTwo = new HttpClient()
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("task_name", "wzc_test_saveDbConf_update_task_name")
				.addData("database_number", "1001")
				.addData("database_pad", "wzc_test_saveDbConf_update_database_pad")
				.addData("database_drive", "org.postgresql.Driver")
				.addData("database_type", DatabaseType.ApacheDerby.getCode())
				.addData("user_name", "wzc_test_saveDbConf_update_user_name")
				.addData("database_ip", "127.0.0.1")
				.addData("database_port", "31001")
				.addData("classify_id", FIRST_CLASSIFY_ID)
				.addData("jdbc_url", updateJDBCURL)
				.post(getActionUrl("saveDbConf")).getBodyString();
		ActionResult wrongRuseltTwo = JsonUtil.toObjectSafety(updateStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongRuseltTwo.isSuccess(), is(false));

		//错误的数据访问6：新增数据时，缺少数据库IP
		String wrongStringThree = new HttpClient()
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("task_name", "wzc_test_saveDbConf_update_task_name")
				.addData("database_number", "1001")
				.addData("database_name", "wzc_test_saveDbConf_update_database_name")
				.addData("database_pad", "wzc_test_saveDbConf_update_database_pad")
				.addData("database_drive", "org.postgresql.Driver")
				.addData("database_type", DatabaseType.ApacheDerby.getCode())
				.addData("user_name", "wzc_test_saveDbConf_update_user_name")
				.addData("database_port", "31001")
				.addData("classify_id", FIRST_CLASSIFY_ID)
				.addData("jdbc_url", updateJDBCURL)
				.post(getActionUrl("saveDbConf")).getBodyString();
		ActionResult wrongRuseltThree = JsonUtil.toObjectSafety(wrongStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongRuseltThree.isSuccess(), is(false));

		//错误的数据访问7：新增数据时，缺少数据库端口号
		String wrongStringFour = new HttpClient()
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("task_name", "wzc_test_saveDbConf_update_task_name")
				.addData("database_number", "1001")
				.addData("database_name", "wzc_test_saveDbConf_update_database_name")
				.addData("database_pad", "wzc_test_saveDbConf_update_database_pad")
				.addData("database_drive", "org.postgresql.Driver")
				.addData("database_type", DatabaseType.ApacheDerby.getCode())
				.addData("user_name", "wzc_test_saveDbConf_update_user_name")
				.addData("database_ip", "127.0.0.1")
				.addData("classify_id", FIRST_CLASSIFY_ID)
				.addData("jdbc_url", updateJDBCURL)
				.post(getActionUrl("saveDbConf")).getBodyString();
		ActionResult wrongRuseltFour = JsonUtil.toObjectSafety(wrongStringFour, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongRuseltFour.isSuccess(), is(false));

		//错误的数据访问8：新增数据时，缺少用户名
		String wrongStringFive = new HttpClient()
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("task_name", "wzc_test_saveDbConf_update_task_name")
				.addData("database_number", "1001")
				.addData("database_name", "wzc_test_saveDbConf_update_database_name")
				.addData("database_pad", "wzc_test_saveDbConf_update_database_pad")
				.addData("database_drive", "org.postgresql.Driver")
				.addData("database_type", DatabaseType.ApacheDerby.getCode())
				.addData("database_ip", "127.0.0.1")
				.addData("database_port", "31001")
				.addData("classify_id", FIRST_CLASSIFY_ID)
				.addData("jdbc_url", updateJDBCURL)
				.post(getActionUrl("saveDbConf")).getBodyString();
		ActionResult wrongRuseltFive = JsonUtil.toObjectSafety(wrongStringFive, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongRuseltFive.isSuccess(), is(false));

		//错误的数据访问9：新增数据时，缺少数据库密码
		String wrongStringSix = new HttpClient()
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("task_name", "wzc_test_saveDbConf_update_task_name")
				.addData("database_number", "1001")
				.addData("database_name", "wzc_test_saveDbConf_update_database_name")
				.addData("database_drive", "org.postgresql.Driver")
				.addData("database_type", DatabaseType.ApacheDerby.getCode())
				.addData("user_name", "wzc_test_saveDbConf_update_user_name")
				.addData("database_ip", "127.0.0.1")
				.addData("database_port", "31001")
				.addData("classify_id", FIRST_CLASSIFY_ID)
				.addData("jdbc_url", updateJDBCURL)
				.post(getActionUrl("saveDbConf")).getBodyString();
		ActionResult wrongRuseltSix = JsonUtil.toObjectSafety(wrongStringSix, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongRuseltSix.isSuccess(), is(false));

		//错误的数据访问10：新增数据时，缺少JDBCURL
		String wrongStringSeven = new HttpClient()
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("task_name", "wzc_test_saveDbConf_update_task_name")
				.addData("database_number", "1001")
				.addData("database_name", "wzc_test_saveDbConf_update_database_name")
				.addData("database_pad", "wzc_test_saveDbConf_update_database_pad")
				.addData("database_drive", "org.postgresql.Driver")
				.addData("database_type", DatabaseType.ApacheDerby.getCode())
				.addData("user_name", "wzc_test_saveDbConf_update_user_name")
				.addData("database_ip", "127.0.0.1")
				.addData("database_port", "31001")
				.addData("classify_id", FIRST_CLASSIFY_ID)
				.post(getActionUrl("saveDbConf")).getBodyString();
		ActionResult wrongRuseltSeven = JsonUtil.toObjectSafety(wrongStringSeven, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongRuseltSeven.isSuccess(), is(false));

		//11、错误的数据访问10：新增数据时，缺少agent_id
		String wrongStringEight = new HttpClient()
				.addData("task_name", "wzc_test_saveDbConf_task_name")
				.addData("database_name", "wzc_test_saveDbConf_database_name")
				.addData("database_number", "1001")
				.addData("database_pad", "wzc_test_saveDbConf_database_pad")
				.addData("database_drive", "org.postgresql.Driver")
				.addData("database_type", DatabaseType.ApacheDerby.getCode())
				.addData("user_name", "wzc_test_saveDbConf_user_name")
				.addData("database_ip", "127.0.0.1")
				.addData("database_port", "31001")
				.addData("classify_id", FIRST_CLASSIFY_ID)
				.addData("jdbc_url", jdbcURL)
				.post(getActionUrl("saveDbConf")).getBodyString();
		ActionResult wrongRuseltEight = JsonUtil.toObjectSafety(wrongStringEight, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongRuseltEight.isSuccess(), is(false));

		//12、错误的数据访问11：新增数据时，作业编号重复
		String wrongStringNine = new HttpClient()
				.addData("agent_id", FIRST_DB_AGENT_ID)
				.addData("task_name", "wzc_test_saveDbConf_task_name")
				.addData("database_name", "wzc_test_saveDbConf_database_name")
				.addData("database_number", "cs02" + initData.baseInitData.threadId)
				.addData("database_pad", "wzc_test_saveDbConf_database_pad")
				.addData("database_drive", "org.postgresql.Driver")
				.addData("database_type", DatabaseType.ApacheDerby.getCode())
				.addData("user_name", "wzc_test_saveDbConf_user_name")
				.addData("database_ip", "127.0.0.1")
				.addData("database_port", "31001")
				.addData("classify_id", FIRST_CLASSIFY_ID)
				.addData("jdbc_url", jdbcURL)
				.post(getActionUrl("saveDbConf")).getBodyString();
		ActionResult wrongRuseltNine = JsonUtil.toObjectSafety(wrongStringNine, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongRuseltNine.isSuccess(), is(false));
	}


	/**
	 * 测试测试连接功能
	 * <p>
	 * 正确数据访问1：使用47.103.83.1的数据库测试连接，数据库类型为postgresql 错误的数据访问1：使用47.103.83.1的数据库测试连接，但是使用错误的密码
	 */
	@Test
	public void testConnection() {
//		//正确数据访问1：使用47.103.83.1的数据库测试连接
//		String rightString = new HttpClient()
//				.addData("agent_id", FIRST_DB_AGENT_ID)
//				.addData("database_drive", "org.postgresql.Driver")
//				.addData("jdbc_url", "jdbc:postgresql://192.168.1.100:32001/hrsdxg")
//				.addData("user_name", "hrsdxg")
//				.addData("database_pad", "hrsdxg")
//				.addData("database_type", DatabaseType.Postgresql.getCode())
//				.post(getActionUrl("testConnection")).getBodyString();
//		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败!"));
//		assertThat(rightResult.isSuccess(), is(true));
//
//		//错误的数据访问1：使用47.103.83.1的数据库测试连接，但是使用错误URL
//		String wrongString = new HttpClient()
//				.addData("agent_id", FIRST_DB_AGENT_ID)
//				.addData("database_drive", "org.postgresql.Driver")
//				.addData("jdbc_url", "jdbc:postgresql://47.103.83.1:32001/hrsdx224")
//				.addData("user_name", "hrsdxg")
//				.addData("database_pad", "hrsdxg")
//				.addData("database_type", DatabaseType.Postgresql.getCode())
//				.post(getActionUrl("testConnection")).getBodyString();
//		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败!"));
//		assertThat(wrongResult.isSuccess(), is(false));
	}

	/**
	 * 在测试用例执行完之后，删除测试数据
	 * <p>
	 * 1、删除数据源表(data_source)测试数据 2、删除Agent信息表(agent_info)测试数据 3、删除database_set表测试数据 4、提交事务后，对数据表中的数据进行检查，断言删除是否成功
	 */
	@After
	public void after() {
		initData.after();
	}
}
