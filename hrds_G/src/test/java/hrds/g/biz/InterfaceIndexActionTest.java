package hrds.g.biz;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.entity.Interface_use_log;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "接口响应时间测试类", author = "dhw", createdate = "2020/4/10 9:36")
public class InterfaceIndexActionTest extends WebBaseTestCase {

	//请填写测试用户需要做登录验证的A项目的登录验证的接口
	private static final String LOGIN_URL = agentInitConfig.getString("login_url");
	// 已经存在的用户ID,用于模拟登录
	private static final long USER_ID = agentInitConfig.getLong("user_id");
	private static final String PASSWORD = agentInitConfig.getString("password");
	//当前线程的id
	private long THREAD_ID = Thread.currentThread().getId() * 1000000;

	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.造接口使用日志表数据
			Interface_use_log interface_use_log = new Interface_use_log();
			interface_use_log.setInterface_use_id(THREAD_ID);
			interface_use_log.setLog_id(THREAD_ID);
			interface_use_log.setResponse_time(1L);
			interface_use_log.setUser_id(USER_ID);
			interface_use_log.setBrowser_type("chrome");
			interface_use_log.setBrowser_version("80.0.3987.122");
			interface_use_log.setInterface_name("单表普通查询接口");
			interface_use_log.setUser_name("接口测试用户-dhw0");
			interface_use_log.setProtocol("3.13.1");
			interface_use_log.setSystem_type("windows");
			interface_use_log.setRequest_type("httpclient");
			interface_use_log.setRequest_stime(DateUtil.getSysTime());
			interface_use_log.setRequest_etime(DateUtil.getSysTime());
			interface_use_log.setRequest_state("NORMAL");
			interface_use_log.setRequest_mode("post");
			interface_use_log.setRequest_info("");
			interface_use_log.setRemoteaddr("127.0.0.1");
			assertThat("初始化接口日志信息表测试数据成功", interface_use_log.add(db), is(1));
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
		// 模拟用户登录
		String bodyString = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("password", PASSWORD)
				.post(LOGIN_URL).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
	}

	@Method(desc = "查询接口响应时间", logicStep = "1.正确的数据访问1，数据有效" +
			"2.该方法只有一种情况")
	@Test
	public void interfaceResponseTimeTest() {
		// 1.正确的数据访问1，数据有效
		String bodyString = new HttpClient().buildSession()
				.post(getActionUrl("interfaceResponseTime")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		assertThat(result.getString(0, "interface_name"), is("单表普通查询接口"));
		assertThat(result.getLong(0, "interface_use_id"), is(THREAD_ID));
		assertThat(result.getLong(0, "avg"), is(1L));
		assertThat(result.getLong(0, "min"), is(1L));
		assertThat(result.getLong(0, "max"), is(1L));
	}

	@After
	public void after() {
		DatabaseWrapper db = new DatabaseWrapper();
		try {
			//1.清理Interface_use_log表中造的数据
			SqlOperator.execute(db, "DELETE FROM " + Interface_use_log.TableName + " WHERE log_id = ?"
					, THREAD_ID);
			SqlOperator.commitTransaction(db);
		} catch (Exception e) {
			db.rollback();
		} finally {
			db.close();
		}
	}
}
