package hrds.a.biz.sysuser;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Sys_user;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "系统用户管理测试类", author = "BY-HLL", createdate = "2019/10/17 0017 下午 03:46")
public class SysUserActionTest extends WebBaseTestCase {
	//测试数据的用户ID
	private static final long USER_ID = -1000L;
	//测试数据的部门ID
	private static final long DEP_ID = -1000000001L;
	//测试数据用户密码
	private static final String USER_PASSWORD = "111111";

	private static String bodyString;
	private static ActionResult ar;

	@Method(desc = "初始化测试用例依赖表数据",
			logicStep = "1.初始化数据" +
					"1-1.初始化 Sys_user 表数据" +
					"1-1-1.初始化一个超级管理员 USER_ID" +
					"1-1-2.初始化一个供测试删除的用户 USER_ID + 1" +
					"1-1-3.初始化一个供测试查询的用户 USER_ID + 2" +
					"1-1-4.初始化一个供测试修改的用户 USER_ID + 3" +
					"2.提交所有数据库执行操作" +
					"3.用户模拟登陆" +
					"测试数据:" +
					"* 1.sys_user 初始化后表中有4条测试数据 user_id:-1000 user_password:111111, user_id:-999 " +
					"user_password:111111," +
					" user_id:-998 user_password:111111, user_id:-997 user_password:111111")
	@BeforeClass
	public static void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1.初始化数据
			//1-1.初始化 Sys_user 表数据
			Sys_user sys_user = new Sys_user();
			//1-1-1.初始化一个超级管理员 USER_ID
			sys_user.setUser_id(USER_ID);
			sys_user.setCreate_id(USER_ID);
			sys_user.setDep_id(DEP_ID);
			sys_user.setRole_id("1001");
			sys_user.setUser_name("测试超级管理员init-hll");
			sys_user.setUser_password(USER_PASSWORD);
			// 0：管理员，1：操作员
			sys_user.setUseris_admin("0");
			sys_user.setUser_type("00");
			sys_user.setUsertype_group(null);
			sys_user.setLogin_ip("127.0.0.1");
			sys_user.setLogin_date("20191001");
			sys_user.setUser_state("1");
			sys_user.setCreate_date(DateUtil.getSysDate());
			sys_user.setCreate_time(DateUtil.getSysTime());
			sys_user.setUpdate_date(DateUtil.getSysDate());
			sys_user.setUpdate_time(DateUtil.getSysTime());
			sys_user.setToken("0");
			sys_user.setValid_time("0");
			sys_user.add(db);
			//1-1-2.初始化一个供测试删除的用户 USER_ID + 1
			sys_user.setUser_id(USER_ID + 1);
			sys_user.setCreate_id(USER_ID);
			sys_user.setDep_id(DEP_ID);
			sys_user.setRole_id("1001");
			sys_user.setUser_name("测试删除用户init-hll");
			sys_user.setUser_password(USER_PASSWORD);
			// 0：管理员，1：操作员
			sys_user.setUseris_admin("0");
			sys_user.setUser_type("00");
			sys_user.setUsertype_group(null);
			sys_user.setLogin_ip("127.0.0.1");
			sys_user.setLogin_date("20191001");
			sys_user.setUser_state("1");
			sys_user.setCreate_date(DateUtil.getSysDate());
			sys_user.setCreate_time(DateUtil.getSysTime());
			sys_user.setUpdate_date(DateUtil.getSysDate());
			sys_user.setUpdate_time(DateUtil.getSysTime());
			sys_user.setToken("0");
			sys_user.setValid_time("0");
			sys_user.add(db);
			//1-1-3.初始化一个供测试查询的用户 USER_ID + 2
			sys_user.setUser_id(USER_ID + 2);
			sys_user.setCreate_id(USER_ID);
			sys_user.setDep_id(DEP_ID);
			sys_user.setRole_id("1001");
			sys_user.setUser_name("测试查询用户init-hll");
			sys_user.setUser_password(USER_PASSWORD);
			// 0：管理员，1：操作员
			sys_user.setUseris_admin("0");
			sys_user.setUser_type("02");
			sys_user.setUsertype_group("02,03,04,08,09,12,14,15,17,19,23,24,26");
			sys_user.setLogin_ip("127.0.0.1");
			sys_user.setLogin_date("20191001");
			sys_user.setUser_state("1");
			sys_user.setCreate_date(DateUtil.getSysDate());
			sys_user.setCreate_time(DateUtil.getSysTime());
			sys_user.setUpdate_date(DateUtil.getSysDate());
			sys_user.setUpdate_time(DateUtil.getSysTime());
			sys_user.setToken("0");
			sys_user.setValid_time("0");
			sys_user.add(db);
			//1-1-4.初始化一个供测试修改的用户 USER_ID + 3
			sys_user.setUser_id(USER_ID + 3);
			sys_user.setCreate_id(USER_ID);
			sys_user.setDep_id(DEP_ID);
			sys_user.setRole_id("1001");
			sys_user.setUser_name("测试修改用户init-hll");
			sys_user.setUser_password(USER_PASSWORD);
			// 0：管理员，1：操作员
			sys_user.setUseris_admin("0");
			sys_user.setUser_type("02");
			sys_user.setUsertype_group("02,03,04,08,09,12,14,15,17,19,23,24,26");
			sys_user.setLogin_ip("127.0.0.1");
			sys_user.setLogin_date("20191001");
			sys_user.setUser_state("1");
			sys_user.setCreate_date(DateUtil.getSysDate());
			sys_user.setCreate_time(DateUtil.getSysTime());
			sys_user.setUpdate_date(DateUtil.getSysDate());
			sys_user.setUpdate_time(DateUtil.getSysTime());
			sys_user.setToken("0");
			sys_user.setValid_time("0");
			sys_user.add(db);
			//1-2.初始化 Department_info 表数据
			Department_info dept = new Department_info();
			dept.setDep_id(DEP_ID);
			dept.setDep_name("测试系统参数类部门init-hll");
			dept.setCreate_date(DateUtil.getSysDate());
			dept.setCreate_time(DateUtil.getSysTime());
			dept.setDep_remark("测试系统参数类部门init-hll");
			dept.add(db);
			//2.提交所有数据库执行操作
			SqlOperator.commitTransaction(db);
			//3.用户模拟登陆
			bodyString = new HttpClient()
					.addData("user_id", USER_ID)
					.addData("password", "111111")
					.post("http://127.0.0.1:8099/A/action/hrds/a/biz/login/login").getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
			assertThat(ar.isSuccess(), is(true));
		}
	}

	@Method(desc = "测试案例执行完成后清理测试数据",
			logicStep = "1.删除测试数据" +
					"1-1.删除 sys_user 表测试数据" +
					"1-2.删除 Department_info 表测试数据")
	@AfterClass
	public static void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//删除测试数据
			//1.删除 sys_user 表测试数据
			SqlOperator.execute(db,
					"delete from " + Sys_user.TableName + " where user_id=?", USER_ID);
			//删除测试过程中生成的用户数据
			SqlOperator.execute(db,
					"delete from " + Sys_user.TableName + " where user_name=?", "测试删除用户init-hll");
			SqlOperator.execute(db,
					"delete from " + Sys_user.TableName + " where user_name=?", "测试查询用户init-hll");
			SqlOperator.execute(db,
					"delete from " + Sys_user.TableName + " where user_name=?", "测试修改用户init-hll");
			SqlOperator.execute(db,
					"delete from " + Sys_user.TableName + " where user_name=?", "测试添加用户init-hll");
			SqlOperator.commitTransaction(db);
			long suDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Sys_user.TableName + " where user_id=?",
					USER_ID
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("data_source 表此条数据删除后,记录数应该为0", suDataNum, is(0L));
			//1-2.删除 Department_info 表测试数据
			SqlOperator.execute(db,
					"delete from " + Department_info.TableName + " where dep_id=?", DEP_ID);
			SqlOperator.commitTransaction(db);
			long deptDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Department_info.TableName + " where dep_id=?", DEP_ID
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("Department_info 表此条数据删除后,记录数应该为0", deptDataNum, is(0L));
		}
	}

	@Method(desc = "新增系统用户测试方法",
			logicStep = "1.正确数据访问" +
					"1-1.新增用户" +
					"1-1-1.检查 sys_user 管理员是否新增成功" +
					"2.错误数据访问" +
					"2-1.部门id不存在")
	@Test
	public void saveSysUser() {
		//1.正确数据访问
		//1-1.新增用户
		bodyString = new HttpClient()
				.addData("dep_id", DEP_ID)
				.addData("user_name", "测试添加用户init-hll")
				.addData("user_password", "111111")
				//useris_admin 0：管理员，1：操作员
				.addData("useris_admin", 0)
				//user_type 用户默认功能，"00"：超级管理员功能代码项，"01"-"26"：功能菜单代码项
				.addData("user_type", "01")
				//usertype_group 功能菜单
				.addData("usertype_group", "01,04,07,10,11,13,16,18,20,25")
				.post(getActionUrl("saveSysUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1-1-1.检查 sys_user 管理员是否新增成功
			OptionalLong number = SqlOperator.queryNumber(db, "select count(*) from " + Sys_user.TableName +
					" where user_name=?", "测试添加用户init-hll");
			assertThat("检查 sys_user 表数据，表 sys_user 数据新增成功", number.getAsLong(), is(1L));
		}
		//2.错误数据访问
		//2-1.部门id不存在
		bodyString = new HttpClient()
				.addData("dep_id", 9999999999L)
				.addData("user_name", "测试添加用户init-hll")
				.addData("user_password", "111111")
				//useris_admin 0：管理员，1：操作员
				.addData("useris_admin", 1)
				//user_type 用户默认功能，"00"：超级管理员功能代码项，"01"-"26"：功能菜单代码项
				.addData("user_type", "02")
				//usertype_group 功能菜单
				.addData("usertype_group", "02,03,04,08,09,12,14,15,19,21,22,23,24,26")
				.post(getActionUrl("addSysUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "修改系统用户信息测试类",
			logicStep = "1.正确数据访问" +
					"1-1.修改已经存在的数据" +
					"2.错误数据访问" +
					"2-1.修改不存在的数据")
	@Test
	public void updateSysUser() {
		//1.正确数据访问
		//1-1.修改已经存在的数据
		bodyString = new HttpClient()
				.addData("user_id", USER_ID + 3)
				.addData("dep_id", -DEP_ID)
				//useris_admin 0：管理员，1：操作员
				.addData("useris_admin", 0)
				//user_state 账户状态 1：正常 0：未启用
				.addData("user_state", 0)
				//usertype_group 功能菜单
				.addData("usertype_group", "02")
				.post(getActionUrl("updateSysUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1-1-1.检查 sys_user 数据是否修改成功
			OptionalLong number = SqlOperator.queryNumber(db, "select count(*) from " + Sys_user.TableName +
					" where user_id=?", USER_ID + 3);
			assertThat("检查 sys_user 表数据是否修改成功", number.getAsLong(), is(1L));
			//1-1-2.检查数据更新是否正确
			Result rs = SqlOperator.queryResult(db, "select * from " + Sys_user.TableName + " where user_id=?",
					USER_ID + 3);
			assertThat(rs.getString(0, "useris_admin"), is("0"));
			assertThat(rs.getString(0, "user_state"), is("0"));
			assertThat(rs.getString(0, "usertype_group"), is("02"));
		}
		//2.错误数据访问
		//2-1.修改不存在的数据
		bodyString = new HttpClient()
				.addData("user_id", 9999L)
				.post(getActionUrl("updateSysUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "获取单个用户信息的测试方法",
			logicStep = "1.正确数据访问" +
					"1-1.用户id存在" +
					"2.错误数据访问" +
					"2-1.用户id不存在")
	@Test
	public void getSysUserByUserId() {
		//1.正确数据访问
		//1-1.用户id存在
		bodyString = new HttpClient()
				.addData("user_id", USER_ID)
				.post(getActionUrl("getSysUserByUserId")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//校验结果信息
		assertThat(ar.getDataForMap().get("user_id"), is(-1000));
		assertThat(ar.getDataForMap().get("create_id"), is(-1000));
		assertThat(ar.getDataForMap().get("dep_id"), is(-1000000001));
		assertThat(ar.getDataForMap().get("role_id"), is(1001));
		assertThat(ar.getDataForMap().get("user_name"), is("测试超级管理员init-hll"));
		assertThat(ar.getDataForMap().get("user_password"), is("111111"));
		assertThat(ar.getDataForMap().get("useris_admin"), is("0"));
		//2.错误数据访问
		//2-1.用户id不存在
		bodyString = new HttpClient()
				.addData("user_id", 9999L)
				.post(getActionUrl("getUserByUserId")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "获取所有系统用户列表（不包含超级管理员）",
			logicStep = "1.请求结果" +
					"2.校验结果信息")
	@Test
	public void getSysUserInfo() {
		//1.正确数据访问
		//请求结果
		bodyString = new HttpClient()
				.post(getActionUrl("getSysUserInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
	}

	@Method(desc = "删除系统用户测试方法",
			logicStep = "1.正确数据访问" +
					"1-1.删除已经存在的数据" +
					"2.错误数据访问" +
					"2-1.删除不存在的数据")
	@Test
	public void deleteSysUser() {
		//1.正确数据访问
		//1-1.删除已经存在的数据
		bodyString = new HttpClient()
				.addData("user_id", USER_ID + 1)
				.post(getActionUrl("deleteSysUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1-1-1.检查 sys_user 数据是否删除成功
			OptionalLong number = SqlOperator.queryNumber(db, "select count(*) from " + Sys_user.TableName +
					" where user_id=?", USER_ID + 1);
			assertThat("检查 sys_user 表数据，表 sys_user 数据删除成功", number.getAsLong(), is(0L));
		}
		//2.错误数据访问
		//2-1.删除不存在的数据
		bodyString = new HttpClient()
				.addData("user_id", 9999L)
				.post(getActionUrl("deleteSysUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "获取部门信息和用户功能菜单信息",
			logicStep = "1.正确数据访问" +
					"1-1.获取功能菜单" +
					"1-2.校验管理员功能菜单" +
					"1-3.校验操作员功能菜单")
	@Test
	public void getDepartmentInfoAndUserFunctionMenuInfo() {
		//1.正确数据访问
		//1-1.获取管理员功能菜单 0:管理员功能菜单
		bodyString = new HttpClient()
				.post(getActionUrl("getDepartmentInfoAndUserFunctionMenuInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//1-2.校验操作员功能菜单
		List<Map<String, Object>> managerFunctionMenuList = (List<Map<String, Object>>)
				ar.getDataForMap().get("managerFunctionMenuList");
		for (int i = 0; i < managerFunctionMenuList.size(); i++) {
			assertThat(managerFunctionMenuList.size(), is(10));
		}
		//1-3.校验操作员功能菜单
		List<Map<String, Object>> operatorFunctionMenuList = (List<Map<String, Object>>)
				ar.getDataForMap().get("operatorFunctionMenuList");
		for (int i = 0; i < operatorFunctionMenuList.size(); i++) {
			assertThat(operatorFunctionMenuList.size(), is(14));
		}
	}

	@Method(desc = "获取编辑的用户信息",
			logicStep = "1.正确数据访问" +
					"1-1.用户id存在" +
					"2.错误数据访问" +
					"2-1.用户id不存在")
	@Test
	public void editSysUserFunction() {
		//1.正确数据访问
		//1-1.用户id存在
		bodyString = new HttpClient()
				.addData("userId", USER_ID)
				.post(getActionUrl("editSysUserFunction")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		Map<String, Object> editSysUserInfo = (Map<String, Object>) ar.getDataForMap().get("editSysUserInfo");
		assertThat(editSysUserInfo.get("user_id"), is(-1000));
		assertThat(editSysUserInfo.get("create_id"), is(-1000));
		assertThat(editSysUserInfo.get("user_name"), is("测试超级管理员init-hll"));
	}
}
