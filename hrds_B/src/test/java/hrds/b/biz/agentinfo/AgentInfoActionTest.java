package hrds.b.biz.agentinfo;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * agent增删改测试类
 *
 * @author dhw
 * @date 2019-09-18 10:49:51
 */
public class AgentInfoActionTest extends WebBaseTestCase {
	// 初始化登录用户ID
	private static final long UserId = 5555L;
	// 初始化创建用户ID
	private static final long CreateId = 1000L;
	// 初始化登录用户ID，更新agent时更新数据采集用户
	private static final long UserId2 = 5556L;
	// 测试部门ID dep_id,测试第一部门
	private static final long DepId1 = -200000001L;
	// 测试部门ID dep_id 测试第二部门
	private static final long DepId2 = -200000011L;
	// 测试数据源 SourceId
	private static final long SourceId = -100000000L;
	// 测试数据源 SourceId，agent存在，数据源被删了
	private static final long SourceId2 = -100000001L;
	// 测试数据库 agent_id
	private static final long DBAgentId = -200000060L;
	// 测试数据库 agent_id，agent存在，数据源被删了
	private static final long DBAgentId2 = -200000061L;
	// 测试数据库 agent_id，更新agent时更新数据采集用户
	private static final long DBAgentId3 = -200000062L;
	// 测试数据库 agent_id，数据源对应的agent下有任务，不能删除
	private static final long DBAgentId4 = -200000064L;
	// 测试数据库 agent_id，用于测试正常删除的agent_id
	private static final long DBAgentId5 = -200000065L;
	// 测试数据文件 agent_id
	private static final long DFAgentId = -200000066L;
	// 测试非结构化 agent_id
	private static final long UnsAgentId = -200000067L;
	// 测试半结构化 agent_id
	private static final long SemiAgentId = -200000068L;
	// 测试FTP agent_id
	private static final long FTPAgentId = -200000069L;
	// 测试agent_down_info agent_id
	private static final long DownId = -300000000L;
	// 测试 分类ID，classify_id
	private static final long ClassifyId = -400000000L;
	// 测试 数据库设置ID，DatabaseId
	private static final long DatabaseId = -500000000L;


	/**
	 * 初始化测试用例数据
	 * <p>
	 * 1.构造数据源data_source表测试数据
	 * 2.构造agent_info表测试数据
	 * 3.构造agent_down_info表测试数据
	 * 4.构造database_set表测试数据
	 * 5.构造sys_user表测试数据
	 * 6.构造department_info部门表测试数据
	 * 7.提交事务
	 * 8.模拟用户登录
	 * <p>
	 * 测试数据：
	 * 1.agent_info表：有7条数据,agent_id有五种，数据库agent,数据文件agent,非结构化agent,半结构化agent,
	 * FTP agent,分别为DBAgentId,DBAgentId2，DBAgentId3，DBAgentId4，DBAgentId5，DFAgentId,
	 * UnsAgentId,SemiAgentId，FTPAgentId
	 * 2.data_source表，有2条数据，SourceId为SourceId，SourceId2
	 * 3.agent_down_info表，有1条数据，down_id为DownId,agent_id为DBAgentId
	 * 4.database_set表，有1条数据，database_id为DatabaseId
	 * 5.sys_user表，有1条数据，user_id为UserId
	 * 6.department_info表，有2条数据，dep_id为DepId1，DepId2
	 */
	@BeforeClass
	public static void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.构造数据源data_source表测试数据
			// 创建data_source表实体对象
			Data_source data_source = new Data_source();
			// 封装data_source表数据
			data_source.setSource_id(SourceId);
			data_source.setDatasource_number("init");
			data_source.setDatasource_name("dsName");
			data_source.setCreate_date(DateUtil.getSysDate());
			data_source.setCreate_time(DateUtil.getSysTime());
			data_source.setCreate_user_id(UserId);
			data_source.setSource_remark("数据源详细描述");
			data_source.setDatasource_remark("备注");
			// 初始化data_source表信息
			int num = data_source.add(db);
			assertThat("测试数据data_source初始化", num, is(1));
			// 2.构造agent_info表测试数据
			Agent_info agent_info = new Agent_info();
			for (int i = 0; i < 9; i++) {
				// 封装agent_info数据
				agent_info.setCreate_date(DateUtil.getSysDate());
				agent_info.setCreate_time(DateUtil.getSysTime());
				agent_info.setAgent_ip("10.71.4.51");
				agent_info.setAgent_port("34567");
				// 初始化不同类型的agent
				if (i == 0) {
					// 数据库 agent
					agent_info.setUser_id(UserId);
					agent_info.setSource_id(SourceId);
					agent_info.setAgent_id(DBAgentId);
					agent_info.setAgent_type(AgentType.ShuJuKu.getCode());
					agent_info.setAgent_name("sjkAgent");
				} else if (i == 1) {
					// 数据文件 Agent
					agent_info.setUser_id(UserId);
					agent_info.setSource_id(SourceId);
					agent_info.setAgent_id(DFAgentId);
					agent_info.setAgent_type(AgentType.DBWenJian.getCode());
					agent_info.setAgent_name("DFAgent");
				} else if (i == 2) {
					// 非结构化 Agent
					agent_info.setUser_id(UserId);
					agent_info.setSource_id(SourceId);
					agent_info.setAgent_id(UnsAgentId);
					agent_info.setAgent_type(AgentType.WenJianXiTong.getCode());
					agent_info.setAgent_name("UnsAgent");
				} else if (i == 3) {
					// 半结构化 Agent
					agent_info.setUser_id(UserId);
					agent_info.setSource_id(SourceId);
					agent_info.setAgent_id(SemiAgentId);
					agent_info.setAgent_type(AgentType.FTP.getCode());
					agent_info.setAgent_name("SemiAgent");
				} else if (i == 4) {
					// FTP Agent
					agent_info.setUser_id(UserId);
					agent_info.setSource_id(SourceId);
					agent_info.setAgent_id(FTPAgentId);
					agent_info.setAgent_type(AgentType.FTP.getCode());
					agent_info.setAgent_name("FTPAgent");
				} else if (i == 5) {
					// 测试SourceId被删除，agent还存在
					agent_info.setUser_id(UserId);
					agent_info.setSource_id(SourceId2);
					agent_info.setAgent_id(DBAgentId2);
					agent_info.setAgent_type(AgentType.ShuJuKu.getCode());
					agent_info.setAgent_name("sjkAgent2");
				} else if (i == 6) {
					// 测试更新agent时切换数据采集用户
					agent_info.setUser_id(UserId2);
					agent_info.setSource_id(SourceId);
					agent_info.setAgent_id(DBAgentId3);
					agent_info.setAgent_type(AgentType.ShuJuKu.getCode());
					agent_info.setAgent_name("sjkAgent3");
				} else if (i == 8) {
					// 测试更新agent时切换数据采集用户
					agent_info.setUser_id(UserId);
					agent_info.setSource_id(SourceId);
					agent_info.setAgent_id(DBAgentId4);
					agent_info.setAgent_type(AgentType.ShuJuKu.getCode());
					agent_info.setAgent_name("sjkAgent4");
				} else {
					agent_info.setUser_id(UserId);
					agent_info.setSource_id(SourceId);
					agent_info.setAgent_id(DBAgentId5);
					agent_info.setAgent_type(AgentType.ShuJuKu.getCode());
					agent_info.setAgent_name("sjkAgent5");
				}
				// 初始化agent不同的连接状态
				if (i < 2) {
					// 已连接
					agent_info.setAgent_status(AgentStatus.YiLianJie.getCode());
				} else if (i >= 2 && i < 4) {
					// 未连接
					agent_info.setAgent_status(AgentStatus.WeiLianJie.getCode());
				} else {
					// 正在运行
					agent_info.setAgent_status(AgentStatus.ZhengZaiYunXing.getCode());
				}
				// 初始化agent_info数据
				int aiNum = agent_info.add(db);
				assertThat("测试agent_info数据初始化", aiNum, is(1));
			}
			// 3.构造agent_down_info表测试数据
			Agent_down_info agent_down_info = new Agent_down_info();
			agent_down_info.setDown_id(DownId);
			agent_down_info.setAgent_id(DFAgentId);
			agent_down_info.setAgent_name("DFAgent");
			agent_down_info.setAgent_ip("10.71.4.51");
			agent_down_info.setAgent_port("34567");
			agent_down_info.setAgent_type(AgentType.DBWenJian.getCode());
			agent_down_info.setDeploy(IsFlag.Fou.getCode());
			agent_down_info.setLog_dir("/home/hyshf/sjkAgent_34567/log/");
			agent_down_info.setPasswd("hyshf");
			agent_down_info.setUser_id(UserId);
			agent_down_info.setAi_desc("agent部署");
			agent_down_info.setRemark("备注");
			agent_down_info.setUser_name("hyshf");
			agent_down_info.setSave_dir("/home/hyshf/sjkAgent_34567/");
			agent_down_info.setAgent_context("/agent");
			agent_down_info.setAgent_pattern("/hrds/agent/trans/biz/AgentServer/getSystemFileInfo");
			// 初始化agent_down_info表数据
			agent_down_info.add(db);
			// 4.构造database_set表测试数据
			Database_set databaseSet = new Database_set();
			databaseSet.setDatabase_id(DatabaseId);
			databaseSet.setAgent_id(DBAgentId4);
			databaseSet.setClassify_id(ClassifyId);
			databaseSet.setDatabase_code(DataBaseCode.UTF_8.getCode());
			databaseSet.setDatabase_drive("org.postgresql.Driver");
			databaseSet.setDatabase_ip("10.71.4.51");
			databaseSet.setDatabase_name("数据库采集测试");
			databaseSet.setDatabase_number("-500000000");
			databaseSet.setDatabase_pad("hrsdxg");
			databaseSet.setDatabase_port("34567");
			databaseSet.setDbfile_format(FileFormat.CSV.getCode());
			databaseSet.setIs_sendok(IsFlag.Fou.getCode());
			databaseSet.setDatabase_type(DatabaseType.Postgresql.getCode());
			databaseSet.setTask_name("数据库测试");
			databaseSet.setJdbc_url("jdbc:postgresql://10.71.4.52:31001/hrsdxgtest");
			databaseSet.setDb_agent(IsFlag.Shi.getCode());
			// 初始化数据库设置database_set表数据
			databaseSet.add(db);
			// 5.构造sys_user表测试数据
			Sys_user sysUser = new Sys_user();
			sysUser.setUser_id(UserId);
			sysUser.setCreate_id(CreateId);
			sysUser.setDep_id(DepId1);
			sysUser.setCreate_date(DateUtil.getSysDate());
			sysUser.setCreate_time(DateUtil.getSysTime());
			sysUser.setRole_id("1001");
			sysUser.setUser_name("testUser");
			sysUser.setUser_password("1");
			sysUser.setUser_type(UserType.CaiJiYongHu.getCode());
			sysUser.setUseris_admin(IsFlag.Shi.toString());
			sysUser.setUsertype_group("02,03,04,08");
			sysUser.setUser_state(IsFlag.Shi.getCode());
			sysUser.add(db);
			// 6.构造department_info部门表测试数据
			// 创建department_info表实体对象
			Department_info department_info = new Department_info();
			for (int i = 0; i < 2; i++) {
				if (i == 0) {
					department_info.setDep_id(DepId1);
					department_info.setDep_name("测试第一部门");
				} else {
					department_info.setDep_id(DepId2);
					department_info.setDep_name("测试第二部门");
				}
				department_info.setCreate_date(DateUtil.getSysDate());
				department_info.setCreate_time(DateUtil.getSysTime());
				department_info.setDep_remark("测试");
				int diNum = department_info.add(db);
				assertThat("测试数据department_info初始化", diNum, is(1));
			}

			// 7.提交事务
			SqlOperator.commitTransaction(db);
		}
		// 8.模拟用户登录
		String responseValue = new HttpClient()
				.buildSession()
				.addData("user_id", UserId)
				.addData("password", "1")
				.post("http://127.0.0.1:8088/A/action/hrds/a/biz/login/login")
				.getBodyString();
		Optional<ActionResult> ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class);
		assertThat("用户登录", ar.get().isSuccess(), is(true));
	}

	/**
	 * 测试完删除测试数据
	 * <p>
	 * 1.测试完成后删除data_source表数据库agent测试数据
	 * 2.判断data_source数据是否被删除
	 * 3.测试完成后删除agent_info表测试数据
	 * 4.判断agent_info数据是否被删除
	 * 5.测试完删除database_set表测试数据
	 * 6.判断database_set表数据是否被删除
	 * 7.测试完删除sys_user表测试数据
	 * 8.判断sys_user表数据是否被删除
	 * 9.测试完删除department_info表测试数据
	 * 10.判断department_info表数据是否被删除
	 * 11.测试完删除data_source表测试数据
	 * 12.判断data_source表数据是否被删除
	 * 13.单独删除新增数据，因为新增数据主键是自动生成的，所以要通过其他方式删除
	 * 14.提交事务
	 */
	@AfterClass
	public static void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.测试完成后删除data_source表数据库agent测试数据
			SqlOperator.execute(db, "delete from data_source where source_id=?", SourceId);
			// 2.判断data_source数据是否被删除
			long num = SqlOperator.queryNumber(db,
					"select count(1) from data_source where source_id=?", SourceId)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 3.测试完成后删除agent_info表数据库agent测试数据
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", DBAgentId);
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", DBAgentId2);
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", DBAgentId3);
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", DBAgentId4);
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", DBAgentId5);
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", DFAgentId);
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", UnsAgentId);
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", SemiAgentId);
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", FTPAgentId);
			// 4.判断agent_info表数据是否被删除
			long DBNum = SqlOperator.queryNumber(db, "select count(1) from  agent_info " +
					" where  agent_id=?", DBAgentId).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", DBNum, is(0L));
			long DBNum2 = SqlOperator.queryNumber(db, "select count(1) from  agent_info " +
					" where  agent_id=?", DBAgentId2).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", DBNum2, is(0L));
			long DBNum3 = SqlOperator.queryNumber(db, "select count(1) from  agent_info " +
					" where  agent_id=?", DBAgentId3).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", DBNum3, is(0L));
			long DBNum4 = SqlOperator.queryNumber(db, "select count(1) from  agent_info " +
					" where  agent_id=?", DBAgentId4).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", DBNum4, is(0L));
			long DBNum5 = SqlOperator.queryNumber(db, "select count(1) from  agent_info " +
					" where  agent_id=?", DBAgentId5).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", DBNum5, is(0L));
			long DFNum = SqlOperator.queryNumber(db, "select count(1) from agent_info" +
					" where  agent_id=?", DFAgentId).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", DFNum, is(0L));
			long UnsNum = SqlOperator.queryNumber(db, "select count(1) from agent_info " +
					" where agent_id=?", UnsAgentId).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", UnsNum, is(0L));
			long SemiNum = SqlOperator.queryNumber(db, "select count(1) from agent_info " +
					" where agent_id=?", SemiAgentId).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", SemiNum, is(0L));
			long FTPNum = SqlOperator.queryNumber(db, "select count(1) from agent_info " +
					" where agent_id=?", FTPAgentId).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", FTPNum, is(0L));
			// 3.删除agent_down_info表测试数据
			SqlOperator.execute(db, "delete from agent_down_info where down_id=?", DownId);
			// 4.判断agent_down_info表数据是否被删除
			long adiNum = SqlOperator.queryNumber(db, "select count(1) from agent_down_info " +
					" where down_id=?", DownId).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", adiNum, is(0L));
			// 5.测试完删除database_set表测试数据
			SqlOperator.execute(db, "delete from database_set where database_id=?", DatabaseId);
			// 6.判断database_set表数据是否被删除
			long dsNum = SqlOperator.queryNumber(db, "select count(1) from  database_set " +
					" where database_id=?", DatabaseId).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", dsNum, is(0L));
			// 7.测试完删除sys_user表测试数据
			SqlOperator.execute(db, "delete from sys_user where user_id=?", UserId);
			// 8.判断sys_user表数据是否被删除
			long userNum = SqlOperator.queryNumber(db, "select count(1) from sys_user " +
					" where user_id=?", UserId).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", userNum, is(0L));
			// 9.测试完成后删除department_info表测试数据
			SqlOperator.execute(db, "delete from department_info where dep_id=?", DepId1);
			SqlOperator.execute(db, "delete from department_info where dep_id=?", DepId2);
			// 10.判断department_info表数据是否被删除
			long diNum = SqlOperator.queryNumber(db, "select count(1) from department_info "
					+ " where dep_id=?", DepId1).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			long diNum2 = SqlOperator.queryNumber(db, "select count(1) from department_info "
					+ " where dep_id=?", DepId2).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", diNum, is(0L));
			assertThat("此条记录删除后，数据为0", diNum2, is(0L));
			// 11.测试完删除data_source表测试数据
			SqlOperator.execute(db, "delete from data_source where source_id=?", SourceId);
			SqlOperator.execute(db, "delete from data_source where source_id=?", SourceId2);
			// 12.判断data_source表数据是否被删除
			long sourceNum = SqlOperator.queryNumber(db, "select count(1) from data_source where " +
					" source_id=?", SourceId).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条记录删除后，数据为0", sourceNum, is(0L));
			long sourceNum2 = SqlOperator.queryNumber(db, "select count(1) from data_source where " +
					" source_id=?", SourceId2).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条记录删除后，数据为0", sourceNum2, is(0L));
			// 13.单独删除新增数据，因为新增数据主键是自动生成的，所以要通过其他方式删除
			SqlOperator.execute(db, "delete from agent_info where source_id=?", SourceId);
			SqlOperator.execute(db, "delete from agent_info where source_id=?", SourceId2);
			// 14.提交事务
			SqlOperator.commitTransaction(db);
		}
	}

	@Test
	public void searchDatasourceAndAgentInfo() {
		// 1.正确的数组访问1，新增数据库agent信息,数据都有效
		String bodyString = new HttpClient()
				.addData("source_id", SourceId)
				.addData("datasource_name", "dsName")
				.post(getActionUrl("searchDatasourceAndAgentInfo")).getBodyString();
		Optional<ActionResult> ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(true));
		Map<Object, Object> agentInfoMap = ar.get().getDataForMap();
		List<Map<String, Object>> sjkAgent = (List<Map<String, Object>>) agentInfoMap.get("sjkAgent");
		for (Map<String, Object> map : sjkAgent) {
			//assertThat(map.get(""));
		}
		List<Map<String, Object>> DBAgent = (List<Map<String, Object>>) agentInfoMap.get("DBAgent");
		List<Map<String, Object>> fileAgent = (List<Map<String, Object>>) agentInfoMap.get("fileAgent");
		List<Map<String, Object>> dxAgent = (List<Map<String, Object>>) agentInfoMap.get("dxAgent");
		List<Map<String, Object>> ftpAgent = (List<Map<String, Object>>) agentInfoMap.get("ftpAgent");
		assertThat(agentInfoMap.get("datasource_name").toString(), is("dsName"));
		assertThat(agentInfoMap.get("source_id").toString(), is(String.valueOf(SourceId)));
		assertThat(agentInfoMap.get("connection").toString(), is(AgentStatus.YiLianJie.getCode()));
		assertThat(agentInfoMap.get("sjk").toString(), is(AgentType.ShuJuKu.getCode()));
		assertThat(agentInfoMap.get("DB").toString(), is(AgentType.DBWenJian.getCode()));
		assertThat(agentInfoMap.get("dx").toString(), is(AgentType.DuiXiang.getCode()));
		assertThat(agentInfoMap.get("file").toString(), is(AgentType.WenJianXiTong.getCode()));
		assertThat(agentInfoMap.get("ftp").toString(), is(AgentType.FTP.getCode()));

	}

	/**
	 * 新增agent_info表数据
	 * <p>
	 * 1.正确的数组访问1，新增数据库agent信息,数据都有效
	 * 2.正确的数组访问2，新增数据文件agent信息,数据都有效
	 * 3.正确的数组访问3，新增非结构化agent信息,数据都有效
	 * 4.正确的数组访问4，新增半结构化agent信息,数据都有效
	 * 5.正确的数组访问5，新增FTP agent信息,数据都有效
	 * 6.错误的数据访问1，新增agent信息,agent_name为空
	 * 7.错误的数据访问2，新增agent信息,agent_name为空格
	 * 8.错误的数据访问3，新增agent信息,agent_type为空
	 * 9.错误的数据访问4，新增agent信息,agent_type为空格
	 * 10.错误的数据访问5，新增agent信息,agent_ip为空
	 * 11.错误的数据访问6，新增agent信息,agent_ip为空格
	 * 12.错误的数据访问7，新增agent信息,agent_ip不合法（不是有效的ip）
	 * 13.错误的数据访问7，新增agent信息,agent_port为空
	 * 14.错误的数据访问8，新增agent信息,agent_port为空格
	 * 15.错误的数据访问10，新增agent信息,agent_port不合法（不是有效的端口）
	 * 16.错误的数据访问10，新增agent信息,SourceId为空格
	 * 17.错误的数据访问11，新增agent信息,SourceId为空
	 * 18.错误的数据访问13，新增agent信息,user_id为空
	 * 19.错误的数据访问14，新增agent信息,user_id为空格
	 * 20.错误的数据访问15，新增agent信息,端口被占用
	 * 21.错误的数据访问16，新增agent信息,agent对应的数据源下相同的IP地址中包含相同的端口
	 * 22.错误的数据访问17，新增agent信息,agent对应的数据源已不存在不可新增
	 */
	@Test
	public void saveAgent() {
		// 1.正确的数组访问1，新增数据库agent信息,数据都有效
		String bodyString = new HttpClient()
				.addData("agent_name", "sjkAddAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3456")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		Optional<ActionResult> ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(true));
		// 验证新增数据是否成功
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 判断agent_info表数据是否新增成功
			OptionalLong number = SqlOperator.queryNumber(db, "select count(*) from " +
							" agent_info where source_id=? and agent_type=? and agent_name=?",
					SourceId, AgentType.ShuJuKu.getCode(), "sjkAddAgent");
			assertThat("添加agent_info数据成功", number.getAsLong(), is(1L));
		}
		// 2.正确的数组访问2，新增数据文件agent信息,数据都有效
		bodyString = new HttpClient()
				.addData("agent_name", "DFAddAgent")
				.addData("agent_type", AgentType.DBWenJian.getCode())
				.addData("agent_ip", "10.71.4.53")
				.addData("agent_port", "3457")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(true));
		// 验证新增数据是否成功
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 判断agent_info表数据是否新增成功
			OptionalLong number = SqlOperator.queryNumber(db, "select count(*) from " +
							" agent_info where source_id=? and agent_type=? and agent_name=?",
					SourceId, AgentType.DBWenJian.getCode(), "DFAddAgent");
			assertThat("添加agent_info数据成功", number.getAsLong(), is(1L));
		}
		// 3.正确的数组访问3，新增非结构化agent信息,数据都有效
		bodyString = new HttpClient()
				.addData("agent_name", "UnsAddAgent")
				.addData("agent_type", AgentType.WenJianXiTong.getCode())
				.addData("agent_ip", "10.71.4.53")
				.addData("agent_port", "3458")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(true));
		// 验证新增数据是否成功
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 判断agent_info表数据是否新增成功
			OptionalLong number = SqlOperator.queryNumber(db, "select count(*) from " +
							" agent_info where source_id=? and agent_type=? and agent_name=?",
					SourceId, AgentType.WenJianXiTong.getCode(), "UnsAddAgent");
			assertThat("添加agent_info数据成功", number.getAsLong(), is(1L));
		}
		// 4.正确的数组访问4，新增半结构化agent信息,数据都有效
		bodyString = new HttpClient()
				.addData("agent_name", "SemiAddAgent")
				.addData("agent_type", AgentType.DuiXiang.getCode())
				.addData("agent_ip", "10.71.4.53")
				.addData("agent_port", "3459")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(true));
		// 验证新增数据是否成功
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 判断agent_info表数据是否新增成功
			OptionalLong number = SqlOperator.queryNumber(db, "select count(*) from " +
							" agent_info where source_id=? and agent_type=? and agent_name=?",
					SourceId, AgentType.DuiXiang.getCode(), "SemiAddAgent");
			assertThat("添加agent_info数据成功", number.getAsLong(), is(1L));
		}
		// 5.正确的数组访问5，新增FTP agent信息,数据都有效
		bodyString = new HttpClient()
				.addData("agent_name", "ftpAddAgent")
				.addData("agent_type", AgentType.FTP.getCode())
				.addData("agent_ip", "10.71.4.53")
				.addData("agent_port", "3460")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(true));
		// 验证新增数据是否成功
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 判断agent_info表数据是否新增成功
			OptionalLong number = SqlOperator.queryNumber(db, "select count(*) from " +
							" agent_info where source_id=? and agent_type=? and agent_name=?",
					SourceId, AgentType.FTP.getCode(), "ftpAddAgent");
			assertThat("添加agent_info数据成功", number.getAsLong(), is(1L));
		}
		// 6.错误的数据访问1，新增agent信息,agent_name为空
		bodyString = new HttpClient()
				.addData("agent_name", "")
				.addData("agent_type", AgentType.DBWenJian.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3457")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		// 7.错误的数据访问2，新增agent信息,agent_name为空格
		bodyString = new HttpClient()
				.addData("agent_name", " ")
				.addData("agent_type", AgentType.DBWenJian.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3457")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 8.错误的数据访问3，新增agent信息,agent_type为空
		bodyString = new HttpClient()
				.addData("agent_name", "db文件Agent")
				.addData("agent_type", "")
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3457")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		// 9.错误的数据访问4，新增agent信息,agent_type为空格
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", " ")
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3457")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 10.错误的数据访问5，新增agent信息,agent_ip为空
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "")
				.addData("agent_port", "3457")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 11.错误的数据访问6，新增agent信息,agent_ip为空格
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", " ")
				.addData("agent_port", "3458")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 12.错误的数据访问7，新增agent信息,agent_ip不合法（不是有效的ip）
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "127.1.2.300")
				.addData("agent_port", "3458")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 13.错误的数据访问8，新增agent信息,agent_port为空
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 14.错误的数据访问9，新增agent信息,agent_port为空格
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", " ")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 15.错误的数据访问10，新增agent信息,agent_port不合法（不是有效的端口）
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "1000")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 16.错误的数据访问11，新增agent信息,SourceId为空
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "")
				.addData("source_id", "")
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 17.错误的数据访问12，新增agent信息,SourceId为空格
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", " ")
				.addData("source_id", " ")
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 18.错误的数据访问13，新增agent信息,user_id为空
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "4568")
				.addData("source_id", SourceId)
				.addData("user_id", "")
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 19.错误的数据访问14，新增agent信息,user_id为空格
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "4568")
				.addData("source_id", SourceId)
				.addData("user_id", " ")
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 20.错误的数据访问15，新增agent信息,端口被占用
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "34567")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 21.错误的数据访问16，新增agent信息,agent对应的数据源下相同的IP地址中包含相同的端口
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3456")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 22.错误的数据访问17，新增agent信息,agent对应的数据源已不存在不可新增
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3461")
				.addData("source_id", SourceId2)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
	}

	/**
	 * 更新agent_info表数据
	 * <p>
	 * 1.正确的数组访问1，更新数据库agent信息,数据都有效
	 * 2.正确的数组访问2，更新数据文件agent信息,数据都有效
	 * 3.正确的数组访问3，更新非结构化agent信息,数据都有效
	 * 4.正确的数组访问4，更新半结构化agent信息,数据都有效
	 * 5.正确的数组访问5，更新FTP agent信息,数据都有效
	 * 6.错误的数据访问1，更新agent信息,agent_name为空
	 * 7.错误的数据访问2，更新agent信息,agent_name为空格
	 * 8.错误的数据访问3，更新agent信息,agent_type为空
	 * 9.错误的数据访问4，更新agent信息,agent_type为空格
	 * 10.错误的数据访问5，更新agent信息,agent_ip为空
	 * 11.错误的数据访问6，更新agent信息,agent_ip为空格
	 * 12.错误的数据访问7，更新agent信息,agent_ip不合法（不是有效的ip）
	 * 13.错误的数据访问7，更新agent信息,agent_port为空
	 * 14.错误的数据访问8，更新agent信息,agent_port为空格
	 * 15.错误的数据访问10，更新agent信息,agent_port不合法（不是有效的端口）
	 * 16.错误的数据访问10，更新agent信息,SourceId为空格
	 * 17.错误的数据访问11，更新agent信息,SourceId为空
	 * 18.错误的数据访问13，更新agent信息,user_id为空
	 * 19.错误的数据访问14，更新agent信息,user_id为空格
	 * 20.错误的数据访问15，更新agent信息,端口被占用
	 * 21.错误的数据访问16，更新agent信息,agent对应的数据源下相同的IP地址中包含相同的端口
	 * 22.错误的数据访问17，更新agent信息,agent对应的数据源已不存在不可新增
	 * <p>
	 * 可更新字段：
	 * agent_ip   String
	 * 含义：agent所在服务器ip
	 * 取值范围：合法IP地址
	 * agent_port String
	 * 含义：agent连接端口
	 * 取值范围：1024-65535
	 * user_id    Long
	 * 含义：数据采集用户ID,定义为Long目的是判null
	 * 取值范围：四位数字，新增用户时自动生成
	 * agent_name String
	 * 含义：agent名称
	 * 取值范围：不为空以及空格
	 */
	@Test
	public void updateAgent() {
		// 1.正确的数据访问1，更新agent信息，数据都不为空且为有效数据
		String bodyString = new HttpClient()
				.addData("agent_id", DBAgentId)
				.addData("agent_name", "sjkUpAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "45678")
				.addData("source_id", SourceId)
				.addData("user_id", UserId2)
				.post(getActionUrl("updateAgent")).getBodyString();
		Optional<ActionResult> ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(true));
		// 验证更新数据是否成功
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 判断agent_info表数据是否更新成功
			Optional<Agent_info> sjkAgent = SqlOperator.queryOneObject(db, Agent_info.class,
					"select * from agent_info where source_id=? and agent_type=? and " +
							" agent_name=?", SourceId, AgentType.ShuJuKu.getCode(), "sjkUpAgent");
			assertThat("更新agent_info数据成功", sjkAgent.get().getAgent_id(), is(DBAgentId));
			assertThat("更新agent_info数据成功", sjkAgent.get().getAgent_name(), is("sjkUpAgent"));
			assertThat("更新agent_info数据成功", sjkAgent.get().getAgent_ip(), is("10.71.4.52"));
			assertThat("更新agent_info数据成功", sjkAgent.get().getAgent_port(), is("45678"));
			assertThat("更新agent_info数据成功", sjkAgent.get().getUser_id(), is(UserId2));
		}
		// 2.正确的数组访问2，更新数据文件agent信息,数据都有效
		bodyString = new HttpClient()
				.addData("agent_id", DFAgentId)
				.addData("agent_name", "DFUpAgent")
				.addData("agent_type", AgentType.DBWenJian.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "45679")
				.addData("source_id", SourceId)
				.addData("user_id", UserId2)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(true));
		// 验证更新数据是否成功
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 判断agent_info表数据是否更新成功
			Optional<Agent_info> dFileAgent = SqlOperator.queryOneObject(db, Agent_info.class,
					"select * from agent_info where source_id=? and agent_type=? and " +
							" agent_name=?", SourceId, AgentType.DBWenJian.getCode(),
					"DFUpAgent");
			assertThat("更新agent_info数据成功", dFileAgent.get().getAgent_id(),
					is(DFAgentId));
			assertThat("更新agent_info数据成功", dFileAgent.get().getAgent_name(),
					is("DFUpAgent"));
			assertThat("更新agent_info数据成功", dFileAgent.get().getAgent_ip(),
					is("10.71.4.52"));
			assertThat("更新agent_info数据成功", dFileAgent.get().getAgent_port(),
					is("45679"));
			assertThat("更新agent_info数据成功", dFileAgent.get().getUser_id(),
					is(UserId2));
		}
		// 3.正确的数组访问3，更新非结构化agent信息,数据都有效
		bodyString = new HttpClient()
				.addData("agent_id", UnsAgentId)
				.addData("agent_name", "UnsUpAgent")
				.addData("agent_type", AgentType.WenJianXiTong.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "45680")
				.addData("source_id", SourceId)
				.addData("user_id", UserId2)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(true));
		// 验证更新数据是否成功
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 判断agent_info表数据是否更新成功
			Optional<Agent_info> unsUpAgent = SqlOperator.queryOneObject(db, Agent_info.class,
					"select * from agent_info where source_id=? and agent_type=? and " +
							" agent_name=?", SourceId, AgentType.WenJianXiTong.getCode(),
					"UnsUpAgent");
			assertThat("更新agent_info数据成功", unsUpAgent.get().getAgent_id(),
					is(UnsAgentId));
			assertThat("更新agent_info数据成功", unsUpAgent.get().getAgent_name(),
					is("UnsUpAgent"));
			assertThat("更新agent_info数据成功", unsUpAgent.get().getAgent_ip(),
					is("10.71.4.52"));
			assertThat("更新agent_info数据成功", unsUpAgent.get().getAgent_port(),
					is("45680"));
			assertThat("更新agent_info数据成功", unsUpAgent.get().getUser_id(),
					is(UserId2));
		}
		// 4.正确的数组访问4，更新半结构化agent信息,数据都有效
		bodyString = new HttpClient()
				.addData("agent_id", SemiAgentId)
				.addData("agent_name", "SemiUpAgent")
				.addData("agent_type", AgentType.DuiXiang.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "45681")
				.addData("source_id", SourceId)
				.addData("user_id", UserId2)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(true));
		// 验证更新数据是否成功
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 判断agent_info表数据是否更新成功
			Optional<Agent_info> semiUpAgent = SqlOperator.queryOneObject(db, Agent_info.class,
					"select * from agent_info where source_id=? and agent_type=? and " +
							" agent_name=?", SourceId, AgentType.DuiXiang.getCode(),
					"SemiUpAgent");
			Optional<Agent_info> sjkAgent = semiUpAgent;
			assertThat("更新agent_info数据成功", semiUpAgent.get().getAgent_id(),
					is(SemiAgentId));
			assertThat("更新agent_info数据成功", semiUpAgent.get().getAgent_name(),
					is("SemiUpAgent"));
			assertThat("更新agent_info数据成功", semiUpAgent.get().getAgent_ip(),
					is("10.71.4.52"));
			assertThat("更新agent_info数据成功", semiUpAgent.get().getAgent_port(),
					is("45681"));
			assertThat("更新agent_info数据成功", semiUpAgent.get().getUser_id(),
					is(UserId2));
		}
		// 5.正确的数组访问5，更新FTP agent信息,数据都有效
		bodyString = new HttpClient()
				.addData("agent_id", FTPAgentId)
				.addData("agent_name", "ftpUpAgent")
				.addData("agent_type", AgentType.FTP.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "45682")
				.addData("source_id", SourceId)
				.addData("user_id", UserId2)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(true));
		// 验证更新数据是否成功
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 判断agent_info表数据是否更新成功
			Optional<Agent_info> ftpUpAgent = SqlOperator.queryOneObject(db, Agent_info.class,
					"select * from agent_info where source_id=? and agent_type=? and " +
							" agent_name=?", SourceId, AgentType.FTP.getCode(),
					"ftpUpAgent");
			assertThat("更新agent_info数据成功", ftpUpAgent.get().getAgent_id(),
					is(FTPAgentId));
			assertThat("更新agent_info数据成功", ftpUpAgent.get().getAgent_name(),
					is("ftpUpAgent"));
			assertThat("更新agent_info数据成功", ftpUpAgent.get().getAgent_ip(),
					is("10.71.4.52"));
			assertThat("更新agent_info数据成功", ftpUpAgent.get().getAgent_port(),
					is("45682"));
			assertThat("更新agent_info数据成功", ftpUpAgent.get().getUser_id(),
					is(UserId2));
		}
		// 6.错误的数据访问1，更新agent信息,agent_name为空
		bodyString = new HttpClient()
				.addData("agent_id", DBAgentId)
				.addData("agent_name", "")
				.addData("agent_type", AgentType.DBWenJian.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3457")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		// 7.错误的数据访问2，更新agent信息,agent_name为空格
		bodyString = new HttpClient()
				.addData("agent_id", DBAgentId)
				.addData("agent_name", " ")
				.addData("agent_type", AgentType.DBWenJian.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3457")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 8.错误的数据访问3，更新agent信息,agent_type为空
		bodyString = new HttpClient()
				.addData("agent_id", DBAgentId)
				.addData("agent_name", "db文件Agent")
				.addData("agent_type", "")
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3457")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		// 9.错误的数据访问4，更新agent信息,agent_type为空格
		bodyString = new HttpClient()
				.addData("agent_id", DBAgentId)
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", " ")
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3457")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 10.错误的数据访问5，更新agent信息,agent_ip为空
		bodyString = new HttpClient()
				.addData("agent_id", DBAgentId)
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "")
				.addData("agent_port", "3457")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 11.错误的数据访问6，更新agent信息,agent_ip为空格
		bodyString = new HttpClient()
				.addData("agent_id", DBAgentId)
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", " ")
				.addData("agent_port", "3458")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 12.错误的数据访问7，更新agent信息,agent_ip不合法（不是有效的ip）
		bodyString = new HttpClient()
				.addData("agent_id", DBAgentId)
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "127.1.2.300")
				.addData("agent_port", "3458")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 13.错误的数据访问8，更新agent信息,agent_port为空
		bodyString = new HttpClient()
				.addData("agent_id", DBAgentId)
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 14.错误的数据访问9，更新agent信息,agent_port为空格
		bodyString = new HttpClient()
				.addData("agent_id", DBAgentId)
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", " ")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 15.错误的数据访问10，更新agent信息,agent_port不合法（不是有效的端口）
		bodyString = new HttpClient()
				.addData("agent_id", DBAgentId)
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "1000")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 16.错误的数据访问11，更新agent信息,sourceId为空
		bodyString = new HttpClient()
				.addData("agent_id", DBAgentId)
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "")
				.addData("source_id", "")
				.addData("user_id", UserId)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 17.错误的数据访问12，更新agent信息,sourceId为空格
		bodyString = new HttpClient()
				.addData("agent_id", DBAgentId)
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", " ")
				.addData("source_id", " ")
				.addData("user_id", UserId)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 18.错误的数据访问13，更新agent信息,user_id为空
		bodyString = new HttpClient()
				.addData("agent_id", DBAgentId)
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "4568")
				.addData("source_id", SourceId)
				.addData("user_id", "")
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 19.错误的数据访问14，更新agent信息,user_id为空格
		bodyString = new HttpClient()
				.addData("agent_id", DBAgentId)
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "4568")
				.addData("source_id", SourceId)
				.addData("user_id", " ")
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 20.错误的数据访问15，更新agent信息,端口被占用
		bodyString = new HttpClient()
				.addData("agent_id", DBAgentId)
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "34567")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 21.错误的数据访问16，更新agent信息,agent对应的数据源下相同的IP地址中包含相同的端口
		bodyString = new HttpClient()
				.addData("agent_id", DBAgentId)
				.addData("agent_name", "sjkUpAgent2")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "45678")
				.addData("source_id", SourceId)
				.addData("user_id", UserId)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
		// 22.错误的数据访问17，更新agent信息,agent对应的数据源已不存在不可更新
		bodyString = new HttpClient()
				.addData("agent_id", DBAgentId)
				.addData("agent_name", "sjkUpAgent3")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "45689")
				.addData("source_id", SourceId2)
				.addData("user_id", UserId)
				.post(getActionUrl("updateAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(false));
	}

	/**
	 * 根据agent_id,agent_type查询agent_info信息,此方法只会有3种可能
	 * <p>
	 * 1.查询agent_info表数据，agent_id,agent_type都不为空，正常删除
	 * 2.2.错误的数据访问1，查询agent_info表数据，agent_id是一个不存在的数据
	 * 3.3.错误的数据访问2，查询agent_info表数据，agent_type是一个合法的数据
	 */
	@Test
	public void searchAgent() {
		// 1.正常的数据访问1，查询agent_info表数据，数据都有效
		String bodyString = new HttpClient().addData("agent_id", DBAgentId)
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.post(getActionUrl("searchAgent")).getBodyString();
		Optional<ActionResult> ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(true));
		List<Agent_info> agentInfoList = ar.get().getDataForEntityList(Agent_info.class);
		for (Agent_info agent_info : agentInfoList) {
			Long user_id = agent_info.getUser_id();
			String agent_name = agent_info.getAgent_name();
			assertThat("查询数据成功", agent_info.getAgent_id(), is(DBAgentId));
			assertThat("查询数据成功", agent_info.getAgent_ip(), is("10.71.4.51"));
			assertThat("查询数据成功", agent_info.getAgent_port(), is("34567"));
			assertThat("查询数据成功", agent_info.getAgent_name(), is("sjkAgent"));
			assertThat("查询数据成功", agent_info.getAgent_type(), is(AgentType.ShuJuKu.getCode()));
			assertThat("查询数据成功", agent_info.getSource_id(), is(SourceId));
			assertThat("查询数据成功", agent_info.getUser_id(), is(UserId));
		}
		// 2.错误的数据访问1，查询agent_info表数据，agent_id是一个不存在的数据
		bodyString = new HttpClient().addData("agent_id", -1000000009L)
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.post(getActionUrl("searchAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(true));
		assertThat(ar.get().getDataForResult().getRowCount(), is(0));

		// 3.错误的数据访问2，查询agent_info表数据，agent_type是一个合法的数据
		bodyString = new HttpClient().addData("agent_id", SourceId)
				.addData("agent_type", "6")
				.post(getActionUrl("searchAgent")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
		assertThat(ar.get().isSuccess(), is(true));
		assertThat(ar.get().getDataForResult().getRowCount(), is(0));
	}

	/**
	 * 根据agent_id,agent_type删除agent_info信息
	 * <p>
	 * 1.正确的数据访问1，删除agent_info表数据，正常删除,agent类型有5种，这里只测一种（其他除了类型都一样）
	 * 2.错误的数据访问1，删除agent_info表数据，agent已部署不能删除
	 * 3.错误的数据访问2,删除agent_info表数据，此数据源对应的agent下有任务，不能删除
	 * 4.错误的数据访问3，删除agent_info表数据，agent_id是一个不存在的数据
	 * 5.错误的数据访问4，删除agent_info表数据，agent_type是一个不存在的数据
	 */
	@Test
	public void deleteAgent() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {

			// 1.正确的数据访问1，删除agent_info表数据，正常删除,agent类型有5种，这里只测一种（其他除了类型都一样）
			// 删除前查询数据库，确认预期删除的数据存在
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
							" agent_info where agent_id = ? and agent_type=?", DBAgentId5,
					AgentType.ShuJuKu.getCode());
			assertThat("删除操作前，保证agent_info表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(1L));
			String bodyString = new HttpClient().addData("agent_id", DBAgentId5)
					.addData("agent_type", AgentType.ShuJuKu.getCode())
					.post(getActionUrl("deleteAgent")).getBodyString();
			Optional<ActionResult> ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
			assertThat(ar.get().isSuccess(), is(true));
			// 删除后查询数据库，确认预期删除的数据存在
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
							" agent_info where agent_id = ? and agent_type=?", DBAgentId5,
					AgentType.ShuJuKu.getCode());
			assertThat("删除操作后，确认该条数据被删除", optionalLong.orElse(Long.MIN_VALUE),
					is(0L));
			// 2.错误的数据访问1，删除agent_info表数据，agent已部署不能删除
			bodyString = new HttpClient().addData("agent_id", DFAgentId)
					.addData("agent_type", AgentType.DBWenJian.getCode())
					.post(getActionUrl("deleteAgent")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
			assertThat(ar.get().isSuccess(), is(false));
			// 3.错误的数据访问2,删除agent_info表数据，此数据源对应的agent下有任务，不能删除
			bodyString = new HttpClient().addData("agent_id", DBAgentId4)
					.addData("agent_type", AgentType.ShuJuKu.getCode())
					.post(getActionUrl("deleteAgent")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
			assertThat(ar.get().isSuccess(), is(false));
			// 4.错误的数据访问3，删除agent_info表数据，agent_type是一个不存在的数据
			bodyString = new HttpClient().addData("agent_id", -1000000009L)
					.addData("agent_type", AgentType.ShuJuKu.getCode())
					.post(getActionUrl("deleteAgent")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
			assertThat(ar.get().isSuccess(), is(false));
			// 5.错误的数据访问4，删除agent_info表数据，agent_id是一个不存在的数据
			bodyString = new HttpClient().addData("agent_id", DBAgentId)
					.addData("agent_type", "6")
					.post(getActionUrl("deleteAgent")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
			assertThat(ar.get().isSuccess(), is(false));
		}
	}
}
