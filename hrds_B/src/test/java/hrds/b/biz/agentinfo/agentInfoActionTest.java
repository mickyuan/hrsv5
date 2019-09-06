package hrds.b.biz.agentinfo;

import fd.ng.core.conf.AppinfoConf;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class agentInfoActionTest extends WebBaseTestCase {
	private static final int Init_Rows = 10; // 向表中初始化的数据条数。

	@Before
	public void before() {
		// 初始化测试用例数据
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Object[]> aiParams = new ArrayList<>();
			// 初始化data_source表信息
			for (Long i = 0L; i < Init_Rows; i++) {
				Long agent_id = i - 300;
				Long source_id = i - 300;
				String create_date = DateUtil.getSysDate();
				String create_time = DateUtil.getSysTime();
				Long user_id = 1001L;
				String agent_name = "数据库agent";
				String agent_type = AgentType.ShuJuKu.getCode();
				String agent_ip = "10.71.4.51";
				String agent_port = "34567";
				String agent_status = AgentStatus.YiLianJie.getCode();
				// agent_info表信息
				Object[] aiObjects = {agent_id, agent_name, agent_type, agent_ip, agent_port, agent_status, create_date
						, create_time, source_id, user_id};
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
		// 用户登录
		String responseValue = new HttpClient()
				.buildSession()
				.addData("username", "admin")
				.addData("password", "admin")
				.post(getUrlActionPattern() + "/" + AppinfoConf.AppBasePackage.replace(".", "/")
						+ "/biz/zauth/loginAtSession")
				.getBodyString();
		ActionResult ar = JsonUtil.toObject(responseValue, ActionResult.class);
		assertThat("用户登录", ar.isSuccess(), is(true));
	}

	@After
	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			for (Long i = 0L; i < Init_Rows; i++) {
				// 测试完成后删除agent_info表测试数据
				SqlOperator.execute(db, "delete from " + Agent_info.TableName + "  where " +
						"agent_id=?", i - 300);
				SqlOperator.commitTransaction(db);
				Long aiNum = SqlOperator.queryNumber(db, "select count(1) from " + Agent_info.TableName + " where agent_id=?", i)
						.orElseThrow(() -> new RuntimeException("count fail!"));

				assertThat("此条记录删除后，数据为0", aiNum, is(0L));
			}
		}
	}

	@Test
	public void saveAgent() {
		Long agent_id = -300L;
		String agent_name = "数据库agent";
		String agent_type = AgentType.ShuJuKu.getCode();
		String agent_ip = "10.71.4.51";
		String agent_port = "3456";
		String agent_status = AgentStatus.YiLianJie.getCode();
		String create_date = DateUtil.getSysDate();
		String create_time = DateUtil.getSysTime();
		Long source_id = -300L;
		Long user_id = 1001L;
		String agentResult = new HttpClient().addData("agent_id", agent_id)
				.addData("agent_name", agent_name)
				.addData("agent_type", agent_type)
				.addData("agent_ip", agent_ip)
				.addData("agent_port", agent_port)
				.addData("agent_status", agent_status)
				.addData("create_date", create_date)
				.addData("create_time", create_time)
				.addData("source_id", source_id)
				.addData("user_id	", user_id)
				.post(getActionUrl("saveAgent")).getBodyString();
		ActionResult ar = JsonUtil.toObject(agentResult, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getCode(), is(200));

		// 验证DB里面的数据是否正确
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Map<String, Object> result = SqlOperator.queryOneObject(db,
					"select * from " + Agent_info.TableName + " where agent_id=?", new BigDecimal(agent_id));
			String new_agent_name = (String) result.get("agent_name");
			String new_agent_type = (String) result.get("agent_type");
			String new_agent_ip = (String) result.get("agent_ip");
			String new_agent_port = (String) result.get("agent_port");
			String new_agent_status = (String) result.get("agent_status");
			String new_create_date = (String) result.get("create_date");
			String new_create_time = (String) result.get("create_time");
			Long new_source_id = Long.valueOf(String.valueOf(result.get("source_id")));
			Long new_user_id = Long.valueOf(String.valueOf(result.get("user_id")));

			assertThat(agent_name, is(new_agent_name));
			assertThat(agent_type, is(new_agent_type));
			assertThat(agent_ip, is(new_agent_ip));
			assertThat(agent_port, is(new_agent_port));
			assertThat(agent_status, is(new_agent_status));
			assertThat(create_date, is(new_create_date));
			assertThat(create_time, is(new_create_time));
			assertThat(source_id, is(new_source_id));
			assertThat(user_id, is(new_user_id));
		}
	}

	@Test
	public void isPortOccupied() {
		String agent_ip = "10.71.4.51";
		int agent_port = 34567;
		String bodyString = new HttpClient().addData("agent_ip", agent_ip).addData("agent_port", agent_port)
				.post(getActionUrl("isPortOccupied")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		System.out.println(bodyString);
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getData(), is(false));
	}

	@Test
	public void searchAgent() {
		Long agent_id = -299L;
		String agent_type = AgentType.ShuJuKu.getCode();
		String bodyString = new HttpClient().addData("agent_id", agent_id).addData("agent_type", agent_type)
				.post(getActionUrl("searchAgent")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
	}

	@Test
	public void deleteAgent() {
		Long agent_id = -298L;
		String agent_type = AgentType.ShuJuKu.getCode();
		String bodyString = new HttpClient().addData("agent_id", agent_id).addData("agent_type", agent_type)
				.post(getActionUrl("deleteAgent")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
	}
}
