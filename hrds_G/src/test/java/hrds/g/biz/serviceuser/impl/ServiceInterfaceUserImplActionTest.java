package hrds.g.biz.serviceuser.impl;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
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

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "服务接口测试类", author = "dhw", createdate = "2020/4/20 16:12")
public class ServiceInterfaceUserImplActionTest extends WebBaseTestCase {

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
	}

	@Test
	public void getToken() {
		// 1.正确的数据访问1，数据有效
		bodyString = new HttpClient().buildSession()
				.addData("user_id", "2001")
				.addData("user_password", "1")
				.post(getActionUrl("getToken")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("token").toString(), is(notNullValue()));
	}

	@Test
	public void tableUsePermissions() {
	}

	@Test
	public void generalQuery() {
	}

	@Test
	public void tableStructureQuery() {
	}

	@Test
	public void fileAttributeSearch() {
	}

	@Test
	public void sqlInterfaceSearch() {
	}

	@Test
	public void rowKeySearch() {
	}

	@Test
	public void uuidDownload() {
	}

	@Test
	public void getIpAndPort() {
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
