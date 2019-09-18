package hrds.b.biz.agentinfo;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.AgentStatus;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.UserType;
import hrds.commons.entity.Agent_info;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * agent增删改测试类
 *
 * @author dhw
 * @date 2019-09-18 10:49:51
 */
public class AgentInfoActionTest extends WebBaseTestCase {
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
						"source_id=?", -30);
				SqlOperator.execute(db, "delete from " + Agent_info.TableName + "  where " +
						"source_id=?", -27);
				SqlOperator.execute(db, "delete from " + Agent_info.TableName + "  where " +
						"source_id=?", -25);
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
	 * 参数8：user_id String
	 * 含义：数据采集用户
	 * 取值范围：不为空
	 *
	 * <p>
	 * 1.新增，数据都不为空且为有效数据
	 * 2.编辑，数据都不为空且为有效数据
	 * 3.新增，agent_name为空
	 * 4.新增，agent_name为空格
	 * 5.新增，agent_type为空
	 * 6.新增，agent_type为空格
	 * 7.新增，agent_type不合法（不在取值范围内）
	 * 8.新增，agent_ip为空
	 * 9.新增，agent_ip为空格
	 * 10.新增，agent_ip不合法（不是有效的ip）
	 * 11.新增，agent_port为空
	 * 12.新增，agent_port为空格
	 * 13.新增，agent_port不合法（不是有效的端口）
	 * 14.新增，source_id为空
	 * 15.新增，source_id为空格
	 * 16.新增，source_id不合法（长度超过10）
	 * 17.新增，user_id为空
	 * 18.新增，user_id为空格
	 */
	@Test
	public void saveAgent() {
		// 1.新增，数据都不为空且为有效数据
		String bodyString = new HttpClient()
				.addData("agent_name", "数据库agent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "3456")
				.addData("source_id", -30L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));

		// 2.编辑，数据都不为空且为有效数据
		bodyString = new HttpClient()
				.addData("agent_id", -27L)
				.addData("agent_name", "DB文件agent")
				.addData("agent_type", AgentType.DBWenJian.getCode())
				.addData("agent_ip", "10.71.4.55")
				.addData("agent_port", "34567")
				.addData("source_id", -27L)
				.addData("user_id", UserId)
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
				.addData("user_id", UserId)
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
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("agent_name不为空以及不为空格，agent_name= "));

		// 5.新增，agent_type为空
		bodyString = new HttpClient()
				.addData("agent_name", "db文件Agent")
				.addData("agent_type", "")
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3457")
				.addData("source_id", -30L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.getMessage(), is("agent_type不为空以及不为空格，agent_type="));

		// 6.新增，agent_type为空格
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", " ")
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3457")
				.addData("source_id", -30L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("agent_type不为空以及不为空格，agent_type= "));
		// 7.新增，agent_type不合法（不在取值范围内）
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", "6")
				.addData("agent_ip", "10.71.4.52")
				.addData("agent_port", "3457")
				.addData("source_id", -30L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("agent_type不合法，不是规定类型，agent_type=6"));

		// 8.新增，agent_ip为空
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "")
				.addData("agent_port", "3457")
				.addData("source_id", -30L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("agent_ip不为空以及不为空格，agent_ip="));

		// 9.新增，agent_ip为空格
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", " ")
				.addData("agent_port", "3458")
				.addData("source_id", -30L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("agent_ip不为空以及不为空格，agent_ip= "));

		// 10.新增，agent_ip不合法（不是有效的ip）
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "127.1.2.300")
				.addData("agent_port", "3458")
				.addData("source_id", -30L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("agent_ip不是一个合法的ip地址," +
				"agent_ip=127.1.2.300"));

		// 11.新增，agent_port为空
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "")
				.addData("source_id", -30L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("agent_ip不为空以及不为空格，agent_ip="));

		// 12.新增，agent_port为空格
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", " ")
				.addData("source_id", -30L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("agent_ip不为空以及不为空格，agent_ip= "));

		// 13.新增，agent_port不合法（不是有效的端口）
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "1000")
				.addData("source_id", -30L)
				.addData("user_id", UserId)
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("agent_port端口不是有效的端口，不在取值范围内，" +
				"agent_port=1000"));

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
		assertThat(ar.getMessage(), is("source_id不为空且不为空格，长度也不能超过10，" +
				"source_id="));

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
		assertThat(ar.getMessage(), is("source_id不为空且不为空格，长度也不能超过10，" +
				"source_id= "));

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
		assertThat(ar.getMessage(), is("source_id不为空且不为空格，长度也不能超过10，" +
				"source_id=10000000009"));

		// 17.新增，user_id为空
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "4568")
				.addData("source_id", -25L)
				.addData("user_id", "")
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("user_id不为空以及不为空格，user_id="));

		// 18.新增，user_id为空格
		bodyString = new HttpClient()
				.addData("agent_name", "sjkAgent")
				.addData("agent_type", AgentType.ShuJuKu.getCode())
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "4568")
				.addData("source_id", -25L)
				.addData("user_id", " ")
				.post(getActionUrl("saveAgent")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("user_id不为空以及不为空格，user_id= "));
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
	 * 2.查询agent_info表数据，agent_id是一个不存在的数据
	 * 3.查询agent_info表数据，agent_type是一个不存在的数据
	 */
	@Test
	public void searchAgent() {
		// 1.查询agent_info表数据，agent_id,agent_type都不为空，正常删除
		String bodyString = new HttpClient().addData("agent_id", -29L)
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
	 * 1.删除agent_info表数据，正常删除
	 * 2.删除agent_info表数据，agent_id是一个不存在的数据
	 * 3.删除agent_info表数据，agent_type是一个不存在的数据
	 */
	@Test
	public void deleteAgent() {
		// 1.删除agent_info表数据，正常删除
		String bodyString = new HttpClient().addData("agent_id", -28L)
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
