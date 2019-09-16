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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class agentInfoActionTest extends WebBaseTestCase {
	private static final int Init_Rows = 10; // 向表中初始化的数据条数。
	// 初始化登录用户ID
	private static final long UserId = 5555;

	@Before
	public void before() {
		// 初始化测试用例数据
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Object[]> aiParams = new ArrayList<>();
			// 初始化data_source表信息
			for (Long i = 0L; i < Init_Rows; i++) {
				Long agent_id = i - 30;
				Long source_id = i - 30;
				String create_date = DateUtil.getSysDate();
				String create_time = DateUtil.getSysTime();

				String agent_name = "数据库agent";
				String agent_type = null;
				if (i < 2) {
					agent_type = AgentType.ShuJuKu.getCode();
				} else if (i >= 2 && i < 4) {
					agent_type = AgentType.DBWenJian.getCode();
				} else if (i >= 4 && i < 6) {
					agent_type = AgentType.WenJianXiTong.getCode();
				} else if (i >= 6 && i < 8) {
					agent_type = AgentType.FTP.getCode();
				} else {
					agent_type = AgentType.DuiXiang.getCode();
				}
				String agent_ip = "10.71.4.51";
				String agent_port = "34567";
				String agent_status = null;
				if (i < 3) {
					agent_status = AgentStatus.YiLianJie.getCode();
				} else if (i >= 3 && i < 7) {
					agent_status = AgentStatus.WeiLianJie.getCode();
				} else {
					agent_status = AgentStatus.ZhengZaiYunXing.getCode();
				}
				// agent_info表信息
				Object[] aiObjects = {agent_id, agent_name, agent_type, agent_ip, agent_port, agent_status, create_date
						, create_time, source_id, UserId};
				aiParams.add(aiObjects);
			}
			// 初始化agent_info表信息
			int[] aiNum = SqlOperator.executeBatch(db,
					"insert into " + Agent_info.TableName + "  values(?, ?,?, ?,?, ?,?, ?,?, ?)",
					aiParams
			);
			assertThat("测试数据初始化", aiNum.length, is(Init_Rows));

			SqlOperator.commitTransaction(db);
		}
		// 5.模拟用户登录
		//String responseValue = new HttpClient()
		//		.buildSession()
		//		.addData("username", UserId)
		//		.addData("password", "111111")
		//		.post("http://127.0.0.1:8099/A/action/hrds/a/biz/login/login")
		//		.getBodyString();
		//ActionResult ar = JsonUtil.toObject(responseValue, ActionResult.class);
		//assertThat("用户登录", ar.getCode(), is(220));
	}

	@After
	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			for (Long i = 0L; i < Init_Rows; i++) {
				// 测试完成后删除agent_info表测试数据
				SqlOperator.execute(db, "delete from " + Agent_info.TableName + "  where " +
						"agent_id=?", i - 30);
				SqlOperator.commitTransaction(db);
				Long aiNum = SqlOperator.queryNumber(db, "select count(1) from " + Agent_info.TableName + " where agent_id=?", i)
						.orElseThrow(() -> new RuntimeException("count fail!"));

				assertThat("此条记录删除后，数据为0", aiNum, is(0L));

				// 测试完成后删除agent_info表新增测试数据，因为新增时agent_id为自动生成
				SqlOperator.execute(db, "delete from " + Agent_info.TableName + "  where " +
						"source_id=?", i - 30);
				SqlOperator.commitTransaction(db);

			}
		}
	}

	/**
	 * 保存agent_info表数据
	 * <p>
	 * 参数1：agent_name String
	 * 含义：agent名称
	 * 取值范围：不为空以及不为空格
	 * 参数2：agent_type String
	 * 含义：agent类型
	 * 取值范围：1:数据库Agent,2:文件系统Agent,3:FtpAgent,4:数据文件Agent,5:对象Agent
	 * 参数3：agent_ip String
	 * 含义：agent地址
	 * 取值范围：服务器ip
	 * 参数4:agent_port String
	 * 含义：agent端口
	 * 取值范围：1024-65535
	 * 参数5：agent_status String
	 * 含义：agent状态
	 * 取值范围：1：已连接，2：未连接，3：正在运行
	 * 参数6：source_id long
	 * 含义：agent_info外键ID
	 * 取值范围：不为空以及不为空格，长度不超过10
	 * 参数7：agent_id long
	 * 含义：agent_info主键ID
	 * 取值范围：不为空及不为空格，长度不超过10
	 *
	 * <p>
	 * 1.新增，数据都不为空且为有效数据
	 * 2.编辑，数据都不为空且为有效数据
	 */
	@Test
	public void saveAgent() {
		// 1.新增，数据都不为空且为有效数据
		String bodyString = new HttpClient()
				.addData("agent_name", "数据库agent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "3456")
				.addData("agent_status", AgentStatus.YiLianJie.getCode())
				.addData("source_id", -30L)
				.post(getActionUrl("saveAgent")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));

		// 2.编辑，数据都不为空且为有效数据
		bodyString = new HttpClient()
				.addData("agent_id", -27L)
				.addData("agent_name", "DB文件agent")
				.addData("agent_type", AgentType.DBWenJian.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "3458")
				.addData("source_id", -27L)
				.addData("user_id",UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));

		// 3.新增，agent_name为空
		bodyString = new HttpClient()
				.addData("agent_name", "")
				.addData("agent_type", AgentType.DBWenJian.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3457")
				.addData("source_id", -30L)
				.addData("user_id",UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.getMessage(), is("agent_name不为空以及不为空格，agent_name="));

		// 4.新增，agent_name为空格
		bodyString = new HttpClient()
				.addData("agent_name", " ")
				.addData("agent_type", AgentType.DBWenJian.getCode())
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3457")
				.addData("source_id", -30L)
				.addData("user_id",UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("agent_name不为空以及不为空格，agent_name= "));
	}

	/**
	 * 根据agent_id,agent_type查询agent_info信息
	 * <p>
	 * 参数：agent_id long
	 * 含义：agent_info表主键
	 * 取值范围：不为空且不为空格
	 * 参数：agent_type String
	 * 含义：agent类型
	 * 取值范围：1:数据库Agent,2:文件系统Agent,3:FtpAgent,4:数据文件Agent,5:对象Agent
	 * <p>
	 * 1.查询agent_info表数据，agent_id,agent_type都不为空，正常删除
	 * 2.查询agent_info表数据，agent_id为空,agent_type不为空
	 * 3.查询agent_info表数据，agent_id不为空,agent_type为空
	 * 4.查询agent_info表数据，agent_id为空格,agent_type不为空格
	 * 5.查询agent_info表数据，agent_id不为空格,agent_type为空格
	 * 6.查询agent_info表数据，agent_id不合法,agent_type合法
	 * 7.查询agent_info表数据，agent_id合法,agent_type不合法
	 */
	@Test
	public void searchAgent() {
		// 1.查询agent_info表数据，agent_id,agent_type都不为空，正常删除
		String bodyString = new HttpClient().addData("agent_id", -29L)
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.post(getActionUrl("searchAgent")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));

		// 2.查询agent_info表数据，agent_id为空,agent_type不为空
		bodyString = new HttpClient().addData("agent_id", "")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.post(getActionUrl("searchAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 3.查询agent_info表数据，agent_id不为空,agent_type为空
		bodyString = new HttpClient().addData("agent_id", -29L)
				.addData("agent_type", "")
				.post(getActionUrl("searchAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 4.查询agent_info表数据，agent_id为空格,agent_type不为空格
		bodyString = new HttpClient().addData("agent_id", " ")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.post(getActionUrl("searchAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 5.查询agent_info表数据，agent_id不为空格,agent_type为空格
		bodyString = new HttpClient().addData("agent_id", -28L)
				.addData("agent_type", " ")
				.post(getActionUrl("searchAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 6.查询agent_info表数据，agent_id不合法,agent_type合法
		bodyString = new HttpClient().addData("agent_id", "10000000003")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.post(getActionUrl("searchAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 7.查询agent_info表数据，agent_id合法,agent_type不合法
		bodyString = new HttpClient().addData("agent_id", -29L)
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
	 * 1.删除agent_info表数据，agent_id,agent_type都不为空，正常删除
	 * 2.删除agent_info表数据，agent_id为空,agent_type不为空
	 * 3.删除agent_info表数据，agent_id不为空,agent_type为空
	 * 4.删除agent_info表数据，agent_id为空格,agent_type不为空格
	 * 5.删除agent_info表数据，agent_id不为空格,agent_type为空格
	 * 6.删除agent_info表数据，agent_id不合法,agent_type合法
	 * 7.删除agent_info表数据，agent_id合法,agent_type不合法
	 */
	@Test
	public void deleteAgent() {
		// 1.删除agent_info表数据，agent_id,agent_type都不为空，正常删除
		String bodyString = new HttpClient().addData("agent_id", -28L)
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.post(getActionUrl("deleteAgent")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));

		//2.删除agent_info表数据，agent_id为空,agent_type不为空
		bodyString = new HttpClient().addData("agent_id", "")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.post(getActionUrl("deleteAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 3.删除agent_info表数据，agent_id不为空,agent_type为空
		bodyString = new HttpClient().addData("agent_id", -28L)
				.addData("agent_type", "")
				.post(getActionUrl("deleteAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 4.删除agent_info表数据，agent_id为空格,agent_type不为空格
		bodyString = new HttpClient().addData("agent_id", " ")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.post(getActionUrl("deleteAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 5.删除agent_info表数据，agent_id不为空格,agent_type为空格
		bodyString = new HttpClient().addData("agent_id", -28L)
				.addData("agent_type", " ")
				.post(getActionUrl("deleteAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 6.删除agent_info表数据，agent_id不合法(长度超过10）,agent_type合法
		bodyString = new HttpClient().addData("agent_id", "10000000004")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.post(getActionUrl("deleteAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 7.删除agent_info表数据，agent_id合法,agent_type不合法
		bodyString = new HttpClient().addData("agent_id", -28L)
				.addData("agent_type", "6")
				.post(getActionUrl("deleteAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
	}
}
