package hrds.b.biz.agent.resourcerecod.register;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.AgentStatus;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_job_classify;
import hrds.commons.entity.Database_set;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.ParallerTestUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.testbase.WebBaseTestCase;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@DocClass(desc = "贴源登记管理", author = "Mr.Lee", createdate = "2020-07-06 10:02")
public class ResourceRecodingActionTest extends WebBaseTestCase {

	/**
	 * 进程ID号
	 */
	private final long THREAD_ID = Thread.currentThread().getId();
	/**
	 * 获取测试用户ID
	 */
	private final long USER_ID = ParallerTestUtil.TESTINITCONFIG.getLong("user_id", 0);
	/**
	 * 获取存储层ID主键
	 */
	private final long DSL_ID = ParallerTestUtil.TESTINITCONFIG.getLong("dsl_id", 0);
	/**
	 * 获取存储层ID主键
	 */
	private final long SOURCE_ID = ParallerTestUtil.TESTINITCONFIG.getLong("source_id", 0);
	/**
	 * Agent ID
	 */
	private final long AGENT_ID = PrimayKeyGener.getNextId();
	/**
	 * 第一个采集任务ID(配置未完成的)
	 */
	private final long FIRST_DATABASE_ID = PrimayKeyGener.getNextId();
	/**
	 * 第二个采集任务ID(配置完成的)
	 */
	private final long SECOND_DATABASE_ID = PrimayKeyGener.getNextId();
	/**
	 * 分类ID
	 */
	private final long CLASSIFY_ID = PrimayKeyGener.getNextId();
	/**
	 * 数据库连接驱动
	 */
	private final String DATABASE_DRIVE = "org.postgresql.Driver";
	/**
	 * 数据库连接名称
	 */
	private final String DATABASE_NAME = "tpcds";
	/**
	 * 数据库连接密码
	 */
	private final String DATABASE_PWD = "tpcds";
	/**
	 * 数据库连接用户名
	 */
	private final String USER_NAME = "tpcds";
	/**
	 * 数据库连接IP
	 */
	private final String IP = "127.0.0.1";
	/**
	 * 数据库连接端口
	 */
	private final String PORT = "31001";
	/**
	 * 数据库连接JDBC_URL
	 */
	private final String JDBC_URL = "jdbc:postgresql://127.0.0.1:31001/hrsdxg";

	@Before
	public void before() {

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//初始化Agent信息
			Agent_info agent_info = new Agent_info();
			agent_info.setAgent_id(AGENT_ID);
			agent_info.setAgent_ip(IP);
			agent_info.setAgent_port(PORT);
			agent_info.setAgent_name("lqcs" + THREAD_ID);
			agent_info.setAgent_status(AgentStatus.WeiLianJie.getCode());
			agent_info.setAgent_type(AgentType.ShuJuKu.getCode());
			agent_info.setSource_id(SOURCE_ID);
			agent_info.setUser_id(USER_ID);
			agent_info.setCreate_date(DateUtil.getSysDate());
			agent_info.setCreate_time(DateUtil.getSysTime());
			agent_info.add(db);
			//初始化分类信息
			Collect_job_classify classify = new Collect_job_classify();
			classify.setClassify_id(CLASSIFY_ID);
			classify.setClassify_num("cs" + THREAD_ID);
			classify.setClassify_name("lqcs" + THREAD_ID);
			classify.setUser_id(USER_ID);
			classify.setAgent_id(AGENT_ID);
			classify.add(db);
			//初始化一个配置未完成的采集任务
			Database_set database_set = new Database_set();
			database_set.setDatabase_id(FIRST_DATABASE_ID);
			database_set.setAgent_id(AGENT_ID);
			database_set.setDatabase_number("F" + THREAD_ID);
			database_set.setTask_name("FCS" + THREAD_ID);
			database_set.setDatabase_name(DATABASE_NAME);
			database_set.setDatabase_pad(DATABASE_PWD);
			database_set.setDatabase_drive(DATABASE_DRIVE);
			database_set.setDatabase_type(DatabaseType.Postgresql.getCode());
			database_set.setUser_name(USER_NAME);
			database_set.setDatabase_ip(IP);
			database_set.setDatabase_port(PORT);
			database_set.setDb_agent(IsFlag.Fou.getCode());
			database_set.setIs_sendok(IsFlag.Fou.getCode());
			database_set.setIs_reg(IsFlag.Shi.getCode());
			database_set.setDsl_id(DSL_ID);
			database_set.setClassify_id(CLASSIFY_ID);
			database_set.setJdbc_url(JDBC_URL);
			database_set.add(db);

			//模拟一个配置完成的采集任务
			database_set.setDatabase_id(SECOND_DATABASE_ID);
			database_set.setAgent_id(AGENT_ID);
			database_set.setDatabase_number("S" + THREAD_ID);
			database_set.setTask_name("SCS" + THREAD_ID);
			database_set.setDatabase_name(DATABASE_NAME);
			database_set.setDatabase_pad(DATABASE_PWD);
			database_set.setDatabase_drive(DATABASE_DRIVE);
			database_set.setDatabase_type(DatabaseType.Postgresql.getCode());
			database_set.setUser_name(USER_NAME);
			database_set.setDatabase_ip(IP);
			database_set.setDatabase_port(PORT);
			database_set.setDb_agent(IsFlag.Fou.getCode());
			database_set.setIs_sendok(IsFlag.Shi.getCode());
			database_set.setIs_reg(IsFlag.Shi.getCode());
			database_set.setDsl_id(DSL_ID);
			database_set.setClassify_id(CLASSIFY_ID);
			database_set.setJdbc_url(JDBC_URL);
			database_set.add(db);
			//提交事务
			db.commit();
			//登陆是否成功
			ActionResult login = login();
			assertThat(login.isSuccess(), is(true));
		} catch (Exception e) {
			throw e;
		}
	}

	@After
	public void after() {

		try (DatabaseWrapper db = new DatabaseWrapper()) {

			//删除Agent测试数据信息
			int execute = SqlOperator
				.execute(db, "DELETE FROM " + Agent_info.TableName + " WHERE agent_id = ?", AGENT_ID);
			//断言删除的条数是否和初始化的一致
			assertThat(execute, is(1));

			//删除分类测试数据信息
			execute = SqlOperator
				.execute(db, "DELETE FROM " + Collect_job_classify.TableName + " WHERE classify_id = ?", CLASSIFY_ID);
			//断言删除的条数是否和初始化的一致
			assertThat(execute, is(1));

			//删除初始化任务数据
			execute = SqlOperator
				.execute(db, "DELETE FROM " + Database_set.TableName + " WHERE database_id in (?,?)", FIRST_DATABASE_ID,
					SECOND_DATABASE_ID);
			//断言删除的条数是否和初始化的一致
			assertThat(execute, is(2));

			db.commit();
		} catch (Exception e) {
			throw e;
		}
	}

	@Test
	public void getInitStorageData() {

		//请求一次数据
		String bodyString = new HttpClient().addData("source_id", SOURCE_ID).addData("agent_id", AGENT_ID)
			.post(getActionUrl("getInitStorageData")).getBodyString();
		ActionResult actionResult = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
			.orElseThrow(() -> new RuntimeException("数据转换错误"));
		assertThat(actionResult.isSuccess(), is(true));
		List<Database_set> dataForEntityList = actionResult.getDataForEntityList(Database_set.class);
		//检查当前为配置完成的任务是否和初始化的数据信息一致
		dataForEntityList.forEach(database_set -> {
			assertThat("采集任务ID不一致", database_set.getDatabase_id(), is(FIRST_DATABASE_ID));
			assertThat("采集分类编号不一致", database_set.getClassify_id(), is(CLASSIFY_ID));
			assertThat("Agent ID不一致", database_set.getAgent_id(), is(AGENT_ID));
			assertThat("采集任务编号不一致", database_set.getDatabase_number(), is("F" + THREAD_ID));
			assertThat("采集任务名称不一致", database_set.getTask_name(), is("FCS" + THREAD_ID));
			assertThat("数据库连接库名成不一致", database_set.getDatabase_name(), is(DATABASE_NAME));
			assertThat("数据库连接密码不一致", database_set.getDatabase_pad(), is(DATABASE_PWD));
			assertThat("数据库连接Driver不一致", database_set.getDatabase_drive(), is(DATABASE_DRIVE));
			assertThat("数据库连接类型不一致", database_set.getDatabase_type(), is(DatabaseType.Postgresql.getCode()));
			assertThat("数据库连接用户名不一致", database_set.getUser_name(), is(USER_NAME));
			assertThat("数据库连接IP不一致", database_set.getDatabase_ip(), is(IP));
			assertThat("数据库连接端口不一致", database_set.getDatabase_port(), is(PORT));
			assertThat("DBAgent不一致", database_set.getDb_agent(), is(IsFlag.Fou.getCode()));
			assertThat("贴源登记不一致", database_set.getIs_reg(), is(IsFlag.Shi.getCode()));
			assertThat("存储层不一致", database_set.getDsl_id(), is(DSL_ID));
			assertThat("数据库连接URL不一致", database_set.getJdbc_url(), is(JDBC_URL));
		});
	}

	@Test
	public void editStorageData() {
		String bodyString = new HttpClient().addData("databaseId", SECOND_DATABASE_ID).post(getActionUrl("editStorageData"))
			.getBodyString();
		ActionResult actionResult = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
			.orElseThrow(() -> new RuntimeException("接口返回数据处理异常"));
		assertThat(actionResult.isSuccess(), is(true));

		List<Database_set> dataForEntityList = actionResult.getDataForEntityList(Database_set.class);
		dataForEntityList.forEach(database_set -> {
			assertThat("采集任务ID不一致", database_set.getDatabase_id(), is(SECOND_DATABASE_ID));
			assertThat("采集分类编号不一致", database_set.getClassify_id(), is(CLASSIFY_ID));
			assertThat("Agent ID不一致", database_set.getAgent_id(), is(AGENT_ID));
			assertThat("采集任务编号不一致", database_set.getDatabase_number(), is("S" + THREAD_ID));
			assertThat("采集任务名称不一致", database_set.getTask_name(), is("SCS" + THREAD_ID));
			assertThat("数据库连接库名成不一致", database_set.getDatabase_name(), is(DATABASE_NAME));
			assertThat("数据库连接密码不一致", database_set.getDatabase_pad(), is(DATABASE_PWD));
			assertThat("数据库连接Driver不一致", database_set.getDatabase_drive(), is(DATABASE_DRIVE));
			assertThat("数据库连接类型不一致", database_set.getDatabase_type(), is(DatabaseType.Postgresql.getCode()));
			assertThat("数据库连接用户名不一致", database_set.getUser_name(), is(USER_NAME));
			assertThat("数据库连接IP不一致", database_set.getDatabase_ip(), is(IP));
			assertThat("数据库连接端口不一致", database_set.getDatabase_port(), is(PORT));
			assertThat("DBAgent不一致", database_set.getDb_agent(), is(IsFlag.Fou.getCode()));
			assertThat("贴源登记不一致", database_set.getIs_reg(), is(IsFlag.Shi.getCode()));
			assertThat("存储层不一致", database_set.getDsl_id(), is(DSL_ID));
			assertThat("数据库连接URL不一致", database_set.getJdbc_url(), is(JDBC_URL));
		});

		//模拟错误的任务ID
		bodyString = new HttpClient().addData("databaseId", -123).post(getActionUrl("editStorageData"))
			.getBodyString();
		actionResult = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
			.orElseThrow(() -> new RuntimeException("接口返回数据处理异常"));
		assertThat(actionResult.isSuccess(), is(false));
	}

	/**
	 * 模拟保存的数据进行保存 1: 模拟没有设置数据库类型数据情况 2: 模拟没有设置分类信息数据情况 3: 模拟没有设置作业编号数据情况 4: 模拟没有设置数据库驱动数据情况 5: 模拟没有设置数据库名称数据情况
	 * 6:模拟没有设置数据库IP地址数据情况 7: 模拟没有设置数据库端口号数据情况 8: 模拟没有设置数据库用户名数据情况 9: 模拟没有设置数据库密码数据情况 10: 模拟没有设置数据库连接URL数据情况 11:
	 * 模拟没有设置Agent信息数据情况 12: 模拟没有设置存储层信息数据情况 13: 模拟任务名称重复情况 14: 模拟作业编号重复情况 15: 模拟一条正确的数据集 16: 查询模拟的数据结果集 17: 校验数据集的否和模拟的一致
	 */
	@Test
	public void saveRegisterData() {

		//1: 模拟没有设置数据库类型数据情况
		Database_set database_set = initDataBaseData("");
		database_set.setDatabase_type("");
		String saveRegisterData = new HttpClient().addData("databaseSet", database_set)
			.post(getActionUrl("saveRegisterData"))
			.getBodyString();
		ActionResult actionResult = JsonUtil.toObjectSafety(saveRegisterData, ActionResult.class)
			.orElseThrow(() -> new RuntimeException("接口返回的数据处理异常"));
		assertThat(actionResult.isSuccess(), is(false));

		//2: 模拟没有设置分类信息数据情况
		database_set = initDataBaseData("classify_id");
		saveRegisterData = new HttpClient().addData("databaseSet", database_set).post(getActionUrl("saveRegisterData"))
			.getBodyString();
		actionResult = JsonUtil.toObjectSafety(saveRegisterData, ActionResult.class)
			.orElseThrow(() -> new RuntimeException("接口返回的数据处理异常"));
		assertThat(actionResult.isSuccess(), is(false));

		//3: 模拟没有设置作业编号数据情况
		database_set = initDataBaseData("");
		database_set.setDatabase_number("");
		saveRegisterData = new HttpClient().addData("databaseSet", database_set).post(getActionUrl("saveRegisterData"))
			.getBodyString();
		actionResult = JsonUtil.toObjectSafety(saveRegisterData, ActionResult.class)
			.orElseThrow(() -> new RuntimeException("接口返回的数据处理异常"));
		assertThat(actionResult.isSuccess(), is(false));
		//4: 模拟没有设置数据库驱动数据情况
		database_set = initDataBaseData("");
		database_set.setDatabase_drive("");
		saveRegisterData = new HttpClient().addData("databaseSet", database_set).post(getActionUrl("saveRegisterData"))
			.getBodyString();
		actionResult = JsonUtil.toObjectSafety(saveRegisterData, ActionResult.class)
			.orElseThrow(() -> new RuntimeException("接口返回的数据处理异常"));
		assertThat(actionResult.isSuccess(), is(false));
		//5: 模拟没有设置数据库名称数据情况
//		database_set = initDataBaseData("");
//		database_set.setDatabase_name("");
//		saveRegisterData = new HttpClient().addData("databaseSet", database_set).post(getActionUrl("saveRegisterData"))
//			.getBodyString();
//		actionResult = JsonUtil.toObjectSafety(saveRegisterData, ActionResult.class)
//			.orElseThrow(() -> new RuntimeException("接口返回的数据处理异常"));
//		assertThat(actionResult.isSuccess(), is(false));
//		//6: 模拟没有设置数据库IP地址数据情况
//		database_set = initDataBaseData("");
//		database_set.setDatabase_ip("");
//		saveRegisterData = new HttpClient().addData("databaseSet", database_set).post(getActionUrl("saveRegisterData"))
//			.getBodyString();
//		actionResult = JsonUtil.toObjectSafety(saveRegisterData, ActionResult.class)
//			.orElseThrow(() -> new RuntimeException("接口返回的数据处理异常"));
//		assertThat(actionResult.isSuccess(), is(false));
//		//7: 模拟没有设置数据库端口号数据情况
//		database_set = initDataBaseData("");
//		database_set.setDatabase_port("");
//		saveRegisterData = new HttpClient().addData("databaseSet", database_set).post(getActionUrl("saveRegisterData"))
//			.getBodyString();
//		actionResult = JsonUtil.toObjectSafety(saveRegisterData, ActionResult.class)
//			.orElseThrow(() -> new RuntimeException("接口返回的数据处理异常"));
//		assertThat(actionResult.isSuccess(), is(false));
		//8: 模拟没有设置数据库用户名数据情况
		database_set = initDataBaseData("");
		database_set.setUser_name("");
		saveRegisterData = new HttpClient().addData("databaseSet", database_set).post(getActionUrl("saveRegisterData"))
			.getBodyString();
		actionResult = JsonUtil.toObjectSafety(saveRegisterData, ActionResult.class)
			.orElseThrow(() -> new RuntimeException("接口返回的数据处理异常"));
		assertThat(actionResult.isSuccess(), is(false));
		//9: 模拟没有设置数据库密码数据情况
		database_set = initDataBaseData("");
		database_set.setDatabase_pad("");
		saveRegisterData = new HttpClient().addData("databaseSet", database_set).post(getActionUrl("saveRegisterData"))
			.getBodyString();
		actionResult = JsonUtil.toObjectSafety(saveRegisterData, ActionResult.class)
			.orElseThrow(() -> new RuntimeException("接口返回的数据处理异常"));
		assertThat(actionResult.isSuccess(), is(false));
		//10: 模拟没有设置数据库连接URL数据情况
		database_set = initDataBaseData("");
		database_set.setJdbc_url("");
		saveRegisterData = new HttpClient().addData("databaseSet", database_set).post(getActionUrl("saveRegisterData"))
			.getBodyString();
		actionResult = JsonUtil.toObjectSafety(saveRegisterData, ActionResult.class)
			.orElseThrow(() -> new RuntimeException("接口返回的数据处理异常"));
		assertThat(actionResult.isSuccess(), is(false));
		//11: 模拟没有设置Agent信息数据情况
		database_set = initDataBaseData("agent_id");
		saveRegisterData = new HttpClient().addData("databaseSet", database_set).post(getActionUrl("saveRegisterData"))
			.getBodyString();
		actionResult = JsonUtil.toObjectSafety(saveRegisterData, ActionResult.class)
			.orElseThrow(() -> new RuntimeException("接口返回的数据处理异常"));
		assertThat(actionResult.isSuccess(), is(false));
		//12: 模拟没有设置存储层信息数据情况
		database_set = initDataBaseData("dsl_id");
		saveRegisterData = new HttpClient().addData("databaseSet", database_set).post(getActionUrl("saveRegisterData"))
			.getBodyString();
		actionResult = JsonUtil.toObjectSafety(saveRegisterData, ActionResult.class)
			.orElseThrow(() -> new RuntimeException("接口返回的数据处理异常"));
		assertThat(actionResult.isSuccess(), is(false));
		//13: 模拟任务名称重复情况
		database_set = initDataBaseData("");
		database_set.setTask_name("FCS" + THREAD_ID);
		saveRegisterData = new HttpClient().addData("databaseSet", database_set).post(getActionUrl("saveRegisterData"))
			.getBodyString();
		actionResult = JsonUtil.toObjectSafety(saveRegisterData, ActionResult.class)
			.orElseThrow(() -> new RuntimeException("接口返回的数据处理异常"));
		assertThat(actionResult.isSuccess(), is(false));
		//14: 模拟作业编号重复情况
		database_set = initDataBaseData("");
		database_set.setDatabase_number("F" + THREAD_ID);
		saveRegisterData = new HttpClient().addData("databaseSet", database_set).post(getActionUrl("saveRegisterData"))
			.getBodyString();
		actionResult = JsonUtil.toObjectSafety(saveRegisterData, ActionResult.class)
			.orElseThrow(() -> new RuntimeException("接口返回的数据处理异常"));
		assertThat(actionResult.isSuccess(), is(false));
		//15: 模拟一条正确的数据集
		database_set = initDataBaseData("");
		saveRegisterData = new HttpClient().addData("databaseSet", database_set).post(getActionUrl("saveRegisterData"))
			.getBodyString();
		actionResult = JsonUtil.toObjectSafety(saveRegisterData, ActionResult.class)
			.orElseThrow(() -> new RuntimeException("接口返回的数据处理异常"));
		assertThat(actionResult.isSuccess(), is(true));
		//插入数据后生成的主键ID信息
		long database_id = Long.parseLong(actionResult.getData().toString());
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//16: 查询模拟的数据结果集
			Database_set databaseQuery = SqlOperator
				.queryOneObject(db, Database_set.class, "SELECT * FROM " + Database_set.TableName + " WHERE database_id =?",
					database_id).orElseThrow(() -> new BusinessException("SQL查询异常"));
			//17: 校验数据集的否和模拟的一致
			assertThat("采集分类编号不一致", databaseQuery.getClassify_id(), is(CLASSIFY_ID));
			assertThat("Agent ID不一致", databaseQuery.getAgent_id(), is(AGENT_ID));
			assertThat("采集任务编号不一致", databaseQuery.getDatabase_number(), is("T" + THREAD_ID));
			assertThat("采集任务名称不一致", databaseQuery.getTask_name(), is("TCS" + THREAD_ID));
			assertThat("数据库连接库名成不一致", databaseQuery.getDatabase_name(), is(DATABASE_NAME));
			assertThat("数据库连接密码不一致", databaseQuery.getDatabase_pad(), is(DATABASE_PWD));
			assertThat("数据库连接Driver不一致", databaseQuery.getDatabase_drive(), is(DATABASE_DRIVE));
			assertThat("数据库连接类型不一致", databaseQuery.getDatabase_type(), is(DatabaseType.Postgresql.getCode()));
			assertThat("数据库连接用户名不一致", databaseQuery.getUser_name(), is(USER_NAME));
			assertThat("数据库连接IP不一致", databaseQuery.getDatabase_ip(), is(IP));
			assertThat("数据库连接端口不一致", databaseQuery.getDatabase_port(), is(PORT));
			assertThat("DBAgent不一致", databaseQuery.getDb_agent(), is(IsFlag.Fou.getCode()));
			assertThat("贴源登记不一致", databaseQuery.getIs_reg(), is(IsFlag.Shi.getCode()));
			assertThat("存储层不一致", databaseQuery.getDsl_id(), is(DSL_ID));
			assertThat("数据库连接URL不一致", databaseQuery.getJdbc_url(), is(JDBC_URL));

			int execute = SqlOperator
				.execute(db, "DELETE FROM " + Database_set.TableName + " WHERE database_id = ?", database_id);
			assertThat(execute, is(1));
			SqlOperator.commitTransaction(db);
		}

	}

	/**
	 * 更新贴源数据的配置信息 1: 获取初始化的数据 2: 修改数据后,并发送到接口进行保存 2-1: 修改为已存在作业编号,更新失败 2-2: 修改为已存在任务名称,更新失败 3: 发送正确的修改数据 4:
	 * 校验数据集的否和更新后的数据一致
	 */
	@Test
	public void updateRegisterData() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1: 获取初始化的数据
			Database_set database_set = getDatabaseSetData(db);
			//2-1: 修改为已存在作业编号,更新失败
			database_set.setDatabase_number("F" + THREAD_ID);
			String bodyString = new HttpClient().addData("databaseSet", database_set)
				.post(getActionUrl("updateRegisterData"))
				.getBodyString();
			ActionResult actionResult = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("解析返回的数据"));
			assertThat(actionResult.isSuccess(), is(false));
			//2-2: 修改为已存在任务名称,更新失败
			database_set = getDatabaseSetData(db);
			database_set.setTask_name("FCS" + THREAD_ID);
			bodyString = new HttpClient().addData("databaseSet", database_set)
				.post(getActionUrl("updateRegisterData"))
				.getBodyString();
			actionResult = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("解析返回的数据"));
			assertThat(actionResult.isSuccess(), is(false));
			//3: 发送正确的修改数据
			database_set = getDatabaseSetData(db);
			database_set.setDatabase_number("L" + THREAD_ID);
			database_set.setTask_name("LCS" + THREAD_ID);
			database_set.setDatabase_ip("111.111.111.1111");
			database_set.setDatabase_port("12345");
			bodyString = new HttpClient().addData("databaseSet", database_set)
				.post(getActionUrl("updateRegisterData"))
				.getBodyString();
			actionResult = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("解析返回的数据"));
			assertThat(actionResult.isSuccess(), is(true));

			database_set = getDatabaseSetData(db);
			//4: 校验数据集的否和更新后的数据一致
			assertThat("采集任务编号不一致", database_set.getDatabase_number(), is("L" + THREAD_ID));
			assertThat("采集任务名称不一致", database_set.getTask_name(), is("LCS" + THREAD_ID));
			assertThat("数据库连接IP不一致", database_set.getDatabase_ip(), is("111.111.111.1111"));
			assertThat("数据库连接端口不一致", database_set.getDatabase_port(), is("12345"));
		}
	}

	Database_set initDataBaseData(String columnName) {
		Database_set database_set = new Database_set();
		if (!columnName.equals("agent_id")) {
			database_set.setAgent_id(AGENT_ID);
		}
		if (!columnName.equals("dsl_id")) {
			database_set.setDsl_id(DSL_ID);
		}
		if (!columnName.equals("classify_id")) {
			database_set.setClassify_id(CLASSIFY_ID);
		}

		database_set.setDatabase_number("T" + THREAD_ID);
		database_set.setTask_name("TCS" + THREAD_ID);
		database_set.setDatabase_name(DATABASE_NAME);
		database_set.setDatabase_pad(DATABASE_PWD);
		database_set.setDatabase_drive(DATABASE_DRIVE);
		database_set.setDatabase_type(DatabaseType.Postgresql.getCode());
		database_set.setUser_name(USER_NAME);
		database_set.setDatabase_ip(IP);
		database_set.setDatabase_port(PORT);
		database_set.setDb_agent(IsFlag.Fou.getCode());
		database_set.setIs_sendok(IsFlag.Fou.getCode());
		database_set.setIs_reg(IsFlag.Shi.getCode());
		database_set.setJdbc_url(JDBC_URL);

		return database_set;
	}

	Database_set getDatabaseSetData(DatabaseWrapper db) {
		return SqlOperator
			.queryOneObject(db, Database_set.class, "SELECT * FROM " + Database_set.TableName + " WHERE database_id = ?",
				SECOND_DATABASE_ID).orElseThrow(() -> new BusinessException("SQL查询异常错误"));
	}
}
