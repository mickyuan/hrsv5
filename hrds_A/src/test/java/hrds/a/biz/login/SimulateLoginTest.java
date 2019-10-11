package hrds.a.biz.login;

import fd.ng.core.conf.AppinfoConf;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.test.junit.TestCaseLog;
import fd.ng.web.action.ActionResult;
import fd.ng.web.util.RequestUtil;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Sys_para;
import hrds.commons.entity.Sys_user;
import hrds.commons.utils.ActionUtil;
import hrds.commons.utils.User;
import hrds.testbase.WebBaseTestCase;
import okhttp3.Cookie;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @program: hrsv5
 * @description: 你好
 * @author: xchao
 * @create: 2019-09-27 10:24
 */
public class SimulateLoginTest extends WebBaseTestCase {

	private final long testUser = 10L;
	private final String testUserPwd = "1";
	@Before
	public void before() {
		//	  1 : 插入用户信息数据
		try (DatabaseWrapper db = new DatabaseWrapper()) {

			Sys_user user = new Sys_user();
			user.setUser_id(testUser);
			user.setCreate_id(testUser);
			user.setRole_id(testUser);
			user.setUser_name("测试用户(-1000)");
			user.setUser_password(testUserPwd);
			user.setUseris_admin(IsFlag.Shi.getCode());
			user.setUser_state(IsFlag.Shi.getCode());
			user.setCreate_date(DateUtil.getSysDate());
			user.setCreate_time(DateUtil.getSysTime());
			user.setToken("0");
			user.setValid_time("0");
			user.setDep_id(testUser);

			int add = user.add(db);
			assertThat("用户测试数据初始化失败", add, is(1));

			//	    2 : 插入部门信息数据
			Department_info di = new Department_info();
			di.setDep_id(testUser);
			di.setDep_name("测试部门(-1000)");
			di.setCreate_date(DateUtil.getSysDate());
			di.setCreate_time(DateUtil.getSysTime());

			int depNum = di.add(db);
			assertThat("部门测试数据初始化失败", depNum, is(1));
			//	    3 : 提交此次数据
			SqlOperator.commitTransaction(db);


			String responseValue = new HttpClient().buildSession()
					.addData("username", testUser)
					.addData("password", testUserPwd)
					.post(getUrlActionPattern() + "/" + AppinfoConf.AppBasePackage.replace(".", "/")
							+ "/a/biz/login/login").getBodyString();
			Optional<ActionResult> actionResult = JsonUtil.toObjectSafety(responseValue, ActionResult.class);
			ActionResult ar = actionResult.get();
			assertThat(ar.isSuccess(), is(true));
			TestCaseLog.println(String.format("用户 %s 登录", testUser));
		}
	}

	@Test
	public  void getUser(){
		User cookic = getCookic();
		assertThat(cookic.getUserName(), is("测试用户(-1000)"));
		assertThat(cookic.getUserId(), is(testUser));
	}
	@After
	public void after() {

		TestCaseLog.println("开始清理初始化的测试数据 ... ...");
		try (DatabaseWrapper db = new DatabaseWrapper()) {

			// 1 : 先删除测试的用户
			SqlOperator.execute(db, "delete from " + Sys_user.TableName + " WHERE user_id = ?", testUser);
			SqlOperator.execute(
					db, "delete from " + Department_info.TableName + " WHERE dep_id = ?", testUser);
			SqlOperator.commitTransaction(db);

			// 2 : 检查测试的用户是否被删除掉
			long userNum =
					SqlOperator.queryNumber(
							db, "select count(1) from " + Sys_user.TableName + " WHERE user_id = ?", testUser)
							.orElseThrow(() -> new RuntimeException("初始测试数据未删除掉,请检查"));
			assertThat("用户测试数据已被删除", userNum, is(0L));

			// 3 : 检查测试的部门数据是否被删除掉
			long depNum = SqlOperator.queryNumber(
					db,
					"select count(1) from " + Department_info.TableName + " WHERE dep_id = ?",
					testUser)
					.orElseThrow(() -> new RuntimeException("初始测试数据未删除掉,请检查"));
			assertThat("部门测试数据已被删除", depNum, is(0L));
		}
	}
}
