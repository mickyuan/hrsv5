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
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.UserState;
import hrds.commons.codes.UserType;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Interface_use_log;
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "接口响应时间测试类", author = "dhw", createdate = "2020/4/10 9:36")
public class InterfaceIndexActionTest extends WebBaseTestCase {

	private static String bodyString;
	private static ActionResult ar;
	// 用户ID
	private static final long USER_ID = 6661L;
	// 部门ID
	private static final long DEP_ID = 5551L;
	private static final int USERROWS = 2;
	// 接口使用ID
	private static final long INTERFACE_USE_ID = 3331L;
	// 接口使用日志ID
	private static final long LOG_ID = 2221L;

	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.造sys_user表数据，用于模拟登录
			Sys_user user = new Sys_user();
			for (int i = 0; i < USERROWS; i++) {
				user.setUser_id(USER_ID + i);
				user.setCreate_id(USER_ID);
				user.setDep_id(DEP_ID);
				user.setRole_id("1001");
				user.setUser_name("接口测试用户-dhw" + i);
				user.setUser_password("1");
				// 0：管理员，1：操作员
				user.setUseris_admin(IsFlag.Shi.getCode());
				user.setUser_type(UserType.RESTYongHu.getCode());
				user.setUsertype_group(UserType.RESTYongHu.getCode() + "," + UserType.CaijiGuanLiYuan.getCode());
				user.setLogin_ip("127.0.0.1");
				user.setLogin_date("20191001");
				user.setUser_state(UserState.ZhengChang.getCode());
				user.setCreate_date(DateUtil.getSysDate());
				user.setCreate_time(DateUtil.getSysTime());
				user.setUpdate_date(DateUtil.getSysDate());
				user.setUpdate_time(DateUtil.getSysTime());
				user.setToken("0");
				user.setValid_time("0");
				user.setUser_email("123@163.com");
				user.setUser_remark("接口测试用户-dhw" + i);
				assertThat("初始化数据成功", user.add(db), is(1));
			}
			//2.造部门表数据，用于模拟用户登录
			Department_info deptInfo = new Department_info();
			deptInfo.setDep_id(DEP_ID);
			deptInfo.setDep_name("测试接口部门init-dhw");
			deptInfo.setCreate_date(DateUtil.getSysDate());
			deptInfo.setCreate_time(DateUtil.getSysTime());
			deptInfo.setDep_remark("测试接口部门init-dhw");
			assertThat("初始化数据成功", deptInfo.add(db), is(1));
			// 3.造接口使用日志表数据
			Interface_use_log interface_use_log = new Interface_use_log();
			interface_use_log.setInterface_use_id(INTERFACE_USE_ID);
			interface_use_log.setLog_id(LOG_ID);
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
			interface_use_log.add(db);
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
		bodyString = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("password", "1")
				.post("http://127.0.0.1:8088/A/action/hrds/a/biz/login/login").getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
	}

	@Method(desc = "查询接口响应时间", logicStep = "1.正确的数据访问1，数据有效" +
			"2.该方法只有一种情况")
	@Test
	public void interfaceResponseTimeTest() {
		// 1.正确的数据访问1，数据有效
		bodyString = new HttpClient().buildSession()
				.post(getActionUrl("interfaceResponseTime")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		assertThat(result.getString(0, "interface_name"), is("单表普通查询接口"));
		assertThat(result.getLong(0, "interface_use_id"), is(INTERFACE_USE_ID));
		assertThat(result.getLong(0, "avg"), is(1L));
		assertThat(result.getLong(0, "min"), is(1L));
		assertThat(result.getLong(0, "max"), is(1L));
	}

	@After
	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1.清理sys_user表中造的数据
			SqlOperator.execute(db, "DELETE FROM " + Sys_user.TableName + " WHERE create_id = ?"
					, USER_ID);
			//2.清理Department_info表中造的数据
			SqlOperator.execute(db, "DELETE FROM " + Department_info.TableName + " WHERE dep_id = ?"
					, DEP_ID);
			//3.清理Interface_use_log表中造的数据
			SqlOperator.execute(db, "DELETE FROM " + Interface_use_log.TableName + " WHERE log_id = ?"
					, LOG_ID);

			SqlOperator.commitTransaction(db);
		}
	}
}
