package hrds.a.biz.department;

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

import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "部门管理测试类", author = "BY-HLL", createdate = "2019/10/18 0018 下午 03:46")
public class DepartmentActionTest extends WebBaseTestCase {
	//测试数据的用户ID
	private static final long USER_ID = -1000L;
	//测试数据用户名
	private static final String USER_NAME = "超级管理员init-hll";
	//测试数据用户密码
	private static final String USER_PASSWORD = "111111";

	private static String bodyString;
	private static ActionResult ar;

	@Method(desc = "初始化测试用例依赖表数据",
			logicStep = "1.初始化数据" +
					"1-1.初始化 Sys_user 表数据 (初始化一个超级管理员)" +
					"1-2.初始化 Department_info 表数据" +
					"1-2-1.初始化测试删除部门的数据" +
					"1-2-2.初始化测试修改部门的数据" +
					"1-2-3.初始化测试查询部门的数据" +
					"1-2-4.初始化模拟登陆依赖部门的数据" +
					"2.提交所有数据库执行操作" +
					"3.用户模拟登陆" +
					"测试数据:" +
					"* 1.sys_user 表中有3条测试数据 user_id:-1000 user_password:111111," +
					" user_id:-1000 user_password:111111," +
					" user_id:-1000 user_password:111111 ")
	@BeforeClass
	public static void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1.初始化数据
			//1-1.初始化 Sys_user 表数据 (初始化一个超级管理员)
			Sys_user sys_user = new Sys_user();
			sys_user.setUser_id(USER_ID);
			sys_user.setCreate_id(USER_ID);
			sys_user.setDep_id(-1000000004L);
			sys_user.setRole_id("1001");
			sys_user.setUser_name(USER_NAME);
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
			//1-2.初始化 Department_info 表数据
			Department_info dept = new Department_info();
			//1-2-1.初始化测试删除部门的数据
			dept.setDep_id(-1000000001L);
			dept.setDep_name("测试删除部门init-hll");
			dept.setCreate_date(DateUtil.getSysDate());
			dept.setCreate_time(DateUtil.getSysTime());
			dept.setDep_remark("测试删除部门init-hll");
			dept.add(db);
			//1-2-2.初始化测试修改部门的数据
			dept.setDep_id(-1000000002L);
			dept.setDep_name("测试修改部门init-hll");
			dept.setCreate_date(DateUtil.getSysDate());
			dept.setCreate_time(DateUtil.getSysTime());
			dept.setDep_remark("测试修改部门init-hll");
			dept.add(db);
			//1-2-3.初始化测试查询部门的数据
			dept.setDep_id(-1000000003L);
			dept.setDep_name("测试查询部门init-hll");
			dept.setCreate_date(DateUtil.getSysDate());
			dept.setCreate_time(DateUtil.getSysTime());
			dept.setDep_remark("测试查询部门init-hll");
			dept.add(db);
			//1-2-4.初始化模拟登陆依赖的部门
			dept.setDep_id(-1000000004L);
			dept.setDep_name("测试查询部门init-hll");
			dept.setCreate_date(DateUtil.getSysDate());
			dept.setCreate_time(DateUtil.getSysTime());
			dept.setDep_remark("测试查询部门init-hll");
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
			//1-1.删除 sys_user 表测试数据
			SqlOperator.execute(db,
					"delete from " + Sys_user.TableName + " where user_id=?", USER_ID);
			SqlOperator.commitTransaction(db);
			long suDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Sys_user.TableName + " where user_id=?",
					USER_ID
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("Sys_user 表此条数据删除后,记录数应该为0", suDataNum, is(0L));
			//1-2.删除 Department_info 表测试数据
			SqlOperator.execute(db,
					"delete from " + Department_info.TableName + " where dep_name=?",
					"测试删除部门init-hll");
			SqlOperator.execute(db,
					"delete from " + Department_info.TableName + " where dep_name=?",
					"修改+测试修改部门init-hll");
			SqlOperator.execute(db,
					"delete from " + Department_info.TableName + " where dep_name=?",
					"测试查询部门init-hll");
			SqlOperator.execute(db,
					"delete from " + Department_info.TableName + " where dep_name=?",
					"测试添加部门init-hll");
			SqlOperator.execute(db,
					"delete from " + Department_info.TableName + " where dep_name=?",
					"测试修改部门init-hll");
			SqlOperator.commitTransaction(db);
			long deptDataNum;
			deptDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Department_info.TableName + " where dep_name=?",
					"测试删除部门init-hll"
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("Department_info 表此条数据删除后,记录数应该为0", deptDataNum, is(0L));
			deptDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Department_info.TableName + " where dep_name=?",
					"修改+测试修改部门init-hll"
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("Department_info 表此条数据删除后,记录数应该为0", deptDataNum, is(0L));
			deptDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Department_info.TableName + " where dep_name=?",
					"测试查询部门init-hll"
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("Department_info 表此条数据删除后,记录数应该为0", deptDataNum, is(0L));
			deptDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Department_info.TableName + " where dep_name=?",
					"测试添加部门init-hll"
			).orElseThrow(() -> new RuntimeException("count fail!"));
			deptDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Department_info.TableName + " where dep_name=?",
					"测试修改部门init-hll"
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("Department_info 表此条数据删除后,记录数应该为0", deptDataNum, is(0L));
		}
	}

	@Method(desc = "新增部门测试方法",
			logicStep = "1.正确数据访问" +
					"1-1.新增部门")
	@Test
	public void addDepartmentInfo() {
		//1.正确数据访问
		//1-1.新增部门，部门名称不重复
		bodyString = new HttpClient()
				.addData("dep_name", "测试添加部门init-hll")
				.addData("dep_remark", "测试添加部门init-hll")
				.post(getActionUrl("addDepartmentInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1-1-1.检查 Department_info 部门是否新增成功
			OptionalLong number = SqlOperator.queryNumber(db, "select count(*) from " + Department_info.TableName +
					" where dep_name=?", "测试添加部门init-hll");
			assertThat("检查 Department_info 表数据，表 Department_info 数据新增成功",
					number.getAsLong(), is(1L));
		}
		//2.错误数据访问
		//2-1.部门名称重复
		bodyString = new HttpClient()
				.addData("dep_name", "测试添加部门init-hll")
				.addData("dep_remark", "测试添加部门init-hll")
				.post(getActionUrl("addDepartmentInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));

	}

	@Method(desc = "删除部门信息测试类",
			logicStep = "1.正确数据访问" +
					"1-1.等待删除的数据源id存在" +
					"2.错误数据访问" +
					"2-1.待删除的数据源id不存在")
	@Test
	public void deleteDepartmentInfo() {
		//1.正确数据访问
		//1-1.等待删除的数据源id存在
		bodyString = new HttpClient()
				.addData("dep_id", -1000000001L)
				.post(getActionUrl("deleteDepartmentInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1-1-1.检查 Department_info 部门是否新增成功
			OptionalLong number = SqlOperator.queryNumber(db, "select count(*) from " + Department_info.TableName +
					" where dep_id=?", -1000000001L);
			assertThat("检查 Department_info 表数据，表 Department_info 数据删除成功",
					number.getAsLong(), is(0L));
		}
		//2.错误数据访问
		//2-1.待删除的数据源id不存在
		bodyString = new HttpClient()
				.addData("dep_id", 9000000001L)
				.post(getActionUrl("deleteDepartmentInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));

	}

	@Method(desc = "修改部门信息测试类",
			logicStep = "1.正确数据访问" +
					"1-1.部门id存在" +
					"2.错误数据访问" +
					"2-1.部门id不存在")
	@Test
	public void updateDepartmentInfo() {
		//1.正确数据访问
		//1-1.数据源id存在
		bodyString = new HttpClient()
				.addData("dep_id", -1000000002L)
				.addData("dep_name", "修改+测试修改部门init-hll")
				.addData("dep_remark", "修改+测试修改部门init-hll")
				.post(getActionUrl("updateDepartmentInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1-1-1.检查 Sys_para 参数是否修改成功
			OptionalLong number = SqlOperator.queryNumber(db, "select count(*) from " + Department_info.TableName +
					" where dep_id=?", -1000000002L);
			assertThat("检查表 DepartmentInfo 数据修改成功", number.getAsLong(), is(1L));
			//1-1-2.检查数据更新是否正确
			Result rs = SqlOperator.queryResult(db, "select * from " + Department_info.TableName +
					" where dep_id=?", -1000000002L);
			assertThat(rs.getLong(0, "dep_id"), is(-1000000002L));
			assertThat(rs.getString(0, "dep_name"), is("修改+测试修改部门init-hll"));
		}
		//2.错误数据访问
		//2-1.部门id不存在
		bodyString = new HttpClient()
				.addData("dep_id", 9000000001L)
				.addData("dep_name", "测试部门init-hll")
				.addData("dep_remark", "测试部门init-hll")
				.post(getActionUrl("deleteDepartmentInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
	}

	@Method(desc = "获取所有部门信息测试方法",
			logicStep = "无参数" +
					"校验结果集")
	@Test
	public void getDepartmentInfo() {
		//无参数
		bodyString = new HttpClient()
				.post(getActionUrl("getDepartmentInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
	}
}
