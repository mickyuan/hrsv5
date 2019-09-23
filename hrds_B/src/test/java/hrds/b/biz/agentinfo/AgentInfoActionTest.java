package hrds.b.biz.agentinfo;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.AgentStatus;
import hrds.commons.codes.AgentType;
import hrds.commons.entity.Agent_info;
import hrds.testbase.WebBaseTestCase;
import org.junit.*;

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
	// 测试数据源 source_id
	private static final long Source_Id = -1000000000L;
	// 测试数据库 agent_id
	private static final long DB_Agent_Id = -2000000001L;
	// 测试数据文件 agent_id
	private static final long DF_Agent_Id = -2000000002L;
	// 测试非结构化 agent_id
	private static final long Uns_Agent_Id = -2000000003L;
	// 测试半结构化 agent_id
	private static final long Semi_Agent_Id = -2000000004L;
	// 测试FTP agent_id
	private static final long FTP_Agent_Id = -2000000005L;

	/**
	 * 初始化测试用例数据
	 * <p>
	 * 1.构造agent_info表测试数据
	 * 2.提交事务
	 * 3.模拟用户登录
	 * <p>
	 * 测试数据：
	 * 1.agent_info表：有5条数据,agent_id有五种，数据库agent,数据文件agent,非结构化agent,半结构化agent,
	 * FTP agent,分别为-2000000001到-2000000005
	 */
	@BeforeClass
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.构造agent_info表测试数据
			Agent_info agent_info = new Agent_info();
			for (int i = 0; i < 5; i++) {
				// 封装agent_info数据
				agent_info.setSource_id(Source_Id);
				agent_info.setCreate_date(DateUtil.getSysDate());
				agent_info.setCreate_time(DateUtil.getSysTime());
				agent_info.setAgent_ip("10.71.4.51");
				agent_info.setUser_id(UserId);
				agent_info.setAgent_port("34567");
				// 初始化不同类型的agent
				if (i == 1) {
					// 数据库 agent
					agent_info.setAgent_id(DB_Agent_Id);
					agent_info.setAgent_type(AgentType.ShuJuKu.getCode());
					agent_info.setAgent_name("sjkAgent");
				} else if (i == 2) {
					// 数据文件 Agent
					agent_info.setAgent_id(DF_Agent_Id);
					agent_info.setAgent_type(AgentType.DBWenJian.getCode());
					agent_info.setAgent_name("DFAgent");

				} else if (i == 3) {
					// 非结构化 Agent
					agent_info.setAgent_id(Uns_Agent_Id);
					agent_info.setAgent_type(AgentType.WenJianXiTong.getCode());
					agent_info.setAgent_name("UnsAgent");
				} else if (i == 4) {
					// 半结构化 Agent
					agent_info.setAgent_id(Semi_Agent_Id);
					agent_info.setAgent_type(AgentType.FTP.getCode());
					agent_info.setAgent_name("SemiAgent");
				} else {
					// FTP Agent
					agent_info.setAgent_id(FTP_Agent_Id);
					agent_info.setAgent_type(AgentType.DuiXiang.getCode());
					agent_info.setAgent_name("FTPAgent");
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
			// 2.提交事务
			SqlOperator.commitTransaction(db);
		}
		// 3.模拟用户登录
		//String responseValue = new HttpClient()
		//		.buildSession()
		//		.addData("username", UserId)
		//		.addData("password", "111111")
		//		.post("http://127.0.0.1:8099/A/action/hrds/a/biz/login/login")
		//		.getBodyString();
		//ActionResult ar = JsonUtil.toObject(responseValue, ActionResult.class);
		//assertThat("用户登录", ar.getCode(), is(220));
	}

	/**
	 * 测试完删除测试数据
	 * <p>
	 * 1.测试完成后删除agent_info表测试数据
	 * 2.判断agent_info数据是否被删除
	 * 3.单独删除新增数据，因为新增数据主键是自动生成的，所以要通过其他方式删除
	 * 4.提交事务
	 */
	@AfterClass
	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.测试完成后删除agent_info表数据库agent测试数据
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", DB_Agent_Id);
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", DF_Agent_Id);
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", Uns_Agent_Id);
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", Semi_Agent_Id);
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", FTP_Agent_Id);
			// 2.判断agent_info表数据是否被删除
			long DBNum = SqlOperator.queryNumber(db, "select count(1) from  agent_info " +
					" where  agent_id=?", DB_Agent_Id).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", DBNum, is(0L));
			long DFNum = SqlOperator.queryNumber(db, "select count(1) from agent_info" +
					" where  agent_id=?", DF_Agent_Id).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", DFNum, is(0L));
			long UnsNum = SqlOperator.queryNumber(db, "select count(1) from agent_info " +
					" where agent_id=?", Uns_Agent_Id).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", UnsNum, is(0L));
			long SemiNum = SqlOperator.queryNumber(db, "select count(1) from agent_info " +
					" where agent_id=?", Semi_Agent_Id).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", SemiNum, is(0L));
			long FTPNum = SqlOperator.queryNumber(db, "select count(1) from agent_info " +
					" where agent_id=?", FTP_Agent_Id).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", FTPNum, is(0L));
			// 3.单独删除新增数据，因为新增数据主键是自动生成的，所以要通过其他方式删除
			SqlOperator.execute(db, "delete from agent_info where source_id=?", Source_Id);
			// 4.提交事务
			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 保存agent_info表数据
	 *
	 * <p>
	 */
	@Test
	public void saveAgent() {
		// 1.正确的数组访问1，新增agent信息,数据都有效
		String bodyString = new HttpClient()
				.addData("agent_name", "数据库agent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3456")
				.addData("source_id", Source_Id)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
		// 验证新增数据是否成功
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 判断data_source表数据是否新增成功
			OptionalLong number = SqlOperator.queryNumber(db, "select count(*) from " +
							" agent_info where source_id=? and agent_type=? and agent_name=?",
					Source_Id, AgentType.ShuJuKu.getCode(), "数据库agent");
			assertThat("添加agent_info数据成功", number.getAsLong(), is(1L));
		}

		// 3.新增，agent_name为空
		bodyString = new HttpClient()
				.addData("agent_name", "")
				.addData("agent_type", AgentType.DBWenJian.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3457")
				.addData("source_id", -1000000012L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);

		// 4.新增，agent_name为空格
		bodyString = new HttpClient()
				.addData("agent_name", " ")
				.addData("agent_type", AgentType.DBWenJian.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3457")
				.addData("source_id", -1000000013L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 5.新增，agent_type为空
		bodyString = new HttpClient()
				.addData("agent_name", "db文件Agent")
				.addData("agent_type", "")
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3457")
				.addData("source_id", -1000000014L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);

		// 6.新增，agent_type为空格
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", " ")
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3457")
				.addData("source_id", -1000000015L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		// 7.新增，agent_type不合法（不在取值范围内）
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", "6")
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3457")
				.addData("source_id", -1000000016L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 8.新增，agent_ip为空
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "")
				.addData("agent_port", "3457")
				.addData("source_id", -1000000017L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 9.新增，agent_ip为空格
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", " ")
				.addData("agent_port", "3458")
				.addData("source_id", -1000000018L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 10.新增，agent_ip不合法（不是有效的ip）
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "127.1.2.300")
				.addData("agent_port", "3458")
				.addData("source_id", -1000000019L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 11.新增，agent_port为空
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "")
				.addData("source_id", -1000000020L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 12.新增，agent_port为空格
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", " ")
				.addData("source_id", -1000000021L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 13.新增，agent_port不合法（不是有效的端口）
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "1000")
				.addData("source_id", -1000000022L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 14.新增，source_id为空
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "")
				.addData("source_id", "")
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 15.新增，source_id为空格
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", " ")
				.addData("source_id", " ")
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 16.新增，source_id不合法（长度超过10）
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "4567")
				.addData("source_id", "10000000009")
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 17.新增，user_id为空
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "4568")
				.addData("source_id", -1000000023L)
				.addData("user_id", "")
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 18.新增，user_id为空格
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "4568")
				.addData("source_id", -1000000024L)
				.addData("user_id", " ")
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
	}

	@Test
	public void updateAgent() {
		// 2.编辑，数据都不为空且为有效数据
		String bodyString = new HttpClient()
				.addData("agent_id", -100000001L)
				.addData("agent_name", "DB文件agent")
				.addData("agent_type", AgentType.DBWenJian.getCode())
				.addData("agent_ip", "10.71.4.55")
				.addData("agent_port", "34567")
				.addData("source_id", -1000000001L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
	}

	/**
	 * 根据agent_id,agent_type查询agent_info信息
	 * <p>
	 * 1.查询agent_info表数据，agent_id,agent_type都不为空，正常删除
	 * 2.查询agent_info表数据，agent_id是一个不存在的数据
	 * 3.查询agent_info表数据，agent_type是一个不存在的数据
	 */
	@Test
	public void searchAgent() {
		// 1.查询agent_info表数据，agent_id,agent_type都不为空，正常删除
		String bodyString = new HttpClient().addData("agent_id", -2900000000L)
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.post(getActionUrl("searchAgent")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));

		// 2.查询agent_info表数据，agent_id是一个不存在的数据
		bodyString = new HttpClient().addData("agent_id", "")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.post(getActionUrl("searchAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 3.查询agent_info表数据，agent_type是一个不存在的数据
		bodyString = new HttpClient().addData("agent_id", -2900000000L)
				.addData("agent_type", "6")
				.post(getActionUrl("searchAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
	}

	/**
	 * 根据agent_id,agent_type删除agent_info信息
	 * <p>
	 * 参数：agent_id long
	 * 含义：agent_info表主键
	 * 取值范围：不为空且不为空格
	 * 参数：agent_type String
	 * 含义：agent类型
	 * 取值范围：1:数据库Agent,2:文件系统Agent,3:FtpAgent,4:数据文件Agent,5:对象Agent
	 * <p>
	 * 1.删除agent_info表数据，正常删除
	 * 2.删除agent_info表数据，agent_id是一个不存在的数据
	 * 3.删除agent_info表数据，agent_type是一个不存在的数据
	 */
	@Test
	public void deleteAgent() {
		// 1.删除agent_info表数据，正常删除
		String bodyString = new HttpClient().addData("agent_id", -2800000000L)
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.post(getActionUrl("deleteAgent")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
		// 2.删除agent_info表数据，agent_id是一个不存在的数据
		bodyString = new HttpClient().addData("agent_id", -10000000000L)
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.post(getActionUrl("deleteAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		// 3.删除agent_info表数据，agent_type是一个不存在的数据
		bodyString = new HttpClient().addData("agent_id", -27L)
				.addData("agent_type", "6")
				.post(getActionUrl("deleteAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
	}
}
