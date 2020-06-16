package hrds.a.biz.syspara;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Sys_para;
import hrds.commons.entity.Sys_user;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SysParaActionTest extends WebBaseTestCase {
	//测试数据的用户ID
	private static final long USER_ID = -1000L;
	//测试数据的部门ID
	private static final long DEP_ID = -1000000001L;
	//测试数据用户名
	private static final String USER_NAME = "超级管理员init-hll";

	private static String bodyString;
	private static ActionResult ar;

	@Method(desc = "初始化测试用例依赖表数据",
			logicStep = "1.初始化数据" +
					"1-1.初始化 Sys_user 表数据 (初始化一个超级管理员)" +
					"1-2.初始化 Department_info 表数据" +
					"1-3.初始化 Sys_para 表数据" +
					"1-3-1.初始化测试删除参数的数据" +
					"1-3-2.初始化测试修改参数的数据" +
					"1-3-3.初始化测试查询参数的数据" +
					"2.提交所有数据库执行操作" +
					"3.用户模拟登陆" +
					"测试数据:" +
					"* 1.sys_para 表中有3条测试数据" +
					"   para_id:-1000 para_name:测试删除配置参数 para_value:测试删除配置参数" +
					"   para_id:-1001 para_name:测试修改配置参数 para_value:测试修改配置参数" +
					"   para_id:-1002 para_name:测试查询配置参数 para_value:测试查询配置参数")
	@BeforeClass
	public static void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1.初始化数据
			//1-1.初始化 Sys_user 表数据 (初始化一个超级管理员)
			Sys_user sys_user = new Sys_user();
			sys_user.setUser_id(USER_ID);
			sys_user.setCreate_id(USER_ID);
			sys_user.setDep_id(DEP_ID);
			sys_user.setRole_id("1001");
			sys_user.setUser_name(USER_NAME);
			sys_user.setUser_password("111111");
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
			dept.setDep_id(DEP_ID);
			dept.setDep_name("测试系统参数类部门init-hll");
			dept.setCreate_date(DateUtil.getSysDate());
			dept.setCreate_time(DateUtil.getSysTime());
			dept.setDep_remark("测试系统参数类部门init-hll");
			dept.add(db);
			//1-3.初始化 Sys_para 表数据
			Sys_para sys_para = new Sys_para();
			//1-3-1.初始化测试删除参数的数据
			sys_para.setPara_id(-1000L);
			sys_para.setPara_name("测试删除配置参数init-hll");
			sys_para.setPara_value("测试删除配置参数init-hll");
			sys_para.setPara_type("server.properties");
			sys_para.add(db);
			//1-3-2.初始化测试修改参数的数据
			sys_para.setPara_id(-1001L);
			sys_para.setPara_name("测试修改配置参数init-hll");
			sys_para.setPara_value("测试修改配置参数init-hll");
			sys_para.setPara_type("server.properties");
			sys_para.add(db);
			//1-3-3.初始化测试查询参数的数据
			sys_para.setPara_id(-1002L);
			sys_para.setPara_name("测试查询配置参数init-hll");
			sys_para.setPara_value("测试查询配置参数init-hll");
			sys_para.setPara_type("server.properties");
			sys_para.add(db);
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
					"1-2.删除 Department_info 表测试数据" +
					"1-3.删除 Sys_para 表测试数据")
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
					"delete from " + Department_info.TableName + " where dep_id=?", DEP_ID);
			SqlOperator.commitTransaction(db);
			long deptDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Department_info.TableName + " where dep_id=?", DEP_ID
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("Department_info 表此条数据删除后,记录数应该为0", deptDataNum, is(0L));
			//1-3.删除 Sys_para 表测试数据
			SqlOperator.execute(db, "delete from " + Sys_para.TableName + " where para_id=?", -1000L);
			SqlOperator.execute(db, "delete from " + Sys_para.TableName + " where para_id=?", -1001L);
			SqlOperator.execute(db, "delete from " + Sys_para.TableName + " where para_id=?", -1002L);
			SqlOperator.execute(db, "delete from " + Sys_para.TableName + " where para_id=?", -1003L);
			SqlOperator.execute(db, "delete from " + Sys_para.TableName + " where para_name=?",
					"测试新增配置参数init-hll");
			SqlOperator.commitTransaction(db);
			long spDataNum;
			spDataNum = SqlOperator.queryNumber(db, "select count(1) from " + Sys_para.TableName +
					" where para_id=?", -1000L).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("Sys_para 表此条数据删除后,记录数应该为0", spDataNum, is(0L));
			spDataNum = SqlOperator.queryNumber(db, "select count(1) from " + Sys_para.TableName +
					" where para_id=?", -1001L).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("Sys_para 表此条数据删除后,记录数应该为0", spDataNum, is(0L));
			spDataNum = SqlOperator.queryNumber(db, "select count(1) from " + Sys_para.TableName +
					" where para_id=?", -1002L).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("Sys_para 表此条数据删除后,记录数应该为0", spDataNum, is(0L));
			spDataNum = SqlOperator.queryNumber(db, "select count(1) from " + Sys_para.TableName +
					" where para_id=?", -1003L).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("Sys_para 表此条数据删除后,记录数应该为0", spDataNum, is(0L));
		}
	}

	@Method(desc = "模糊查询获取系统参数信息的测试方法",
			logicStep = "模糊查询获取系统参数信息的测试方法" +
					"1.正确数据访问" +
					"1-1.查询所有配置参数")
	@Test
	public void getSysPara() {
		//1.正确数据访问
		//1-1.查询所有配置参数
		bodyString = new HttpClient()
				.post(getActionUrl("getSysPara")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//1-2.模糊查询
		bodyString = new HttpClient()
				.addData("paraName", "测试查询配置参数")
				.post(getActionUrl("getSysPara")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
	}

	@Method(desc = "删除系统参数测试方法",
			logicStep = "1.正确数据访问" +
					"1-1.删除参数id存在" +
					"2.错误数据访问" +
					"2-1.删除参数id不存在")
	@Test
	public void deleteSysPara() {
		//1.正确数据访问
		//1-1.删除参数id存在
		bodyString = new HttpClient()
				.addData("para_id", -1000L)
				.addData("para_name", "测试删除配置参数init-hll")
				.post(getActionUrl("deleteSysPara")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1-1-1.检查 Sys_para 参数是否新增成功
			OptionalLong number = SqlOperator.queryNumber(db, "select count(*) from " + Sys_para.TableName +
					" where para_id=?", -1000L);
			assertThat("检查 Sys_para 表数据，表 Sys_para 数据删除成功", number.getAsLong(), is(0L));
		}
		//错误数据访问
		//2-1.删除参数id不存在
		bodyString = new HttpClient()
				.addData("para_id", -999L)
				.addData("para_name", "测试删除配置参数init-hll")
				.post(getActionUrl("deleteSysPara")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "修改系统参数测试方法",
			logicStep = "1.正确数据访问" +
					"1-1.修改参数存在" +
					"1-1-1.检查 Sys_para 参数是否修改成功" +
					"1-1-2.检查数据更新是否正确" +
					"2.错误数据访问" +
					"2-1.修改参数不存在")
	@Test
	public void updateSysPara() {
		//1.正确数据访问
		//1-1.修改参数id存在
		bodyString = new HttpClient()
				.addData("para_id", -1001L)
				.addData("para_name", "测试修改配置参数init-hll")
				.addData("para_value", "修改+测试修改配置参数init-hll")
				.post(getActionUrl("updateSysPara")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1-1-1.检查 Sys_para 参数是否修改成功
			OptionalLong number = SqlOperator.queryNumber(db, "select count(*) from " + Sys_para.TableName +
					" where para_id=?", -1001L);
			assertThat("检查 Sys_para 表数据，表 Sys_para 数据修改成功", number.getAsLong(), is(1L));
			//1-1-2.检查数据更新是否正确
			Result rs = SqlOperator.queryResult(db, "select * from " + Sys_para.TableName + " where para_id=?",
					-1001L);
			assertThat(rs.getLong(0, "para_id"), is(-1001L));
			assertThat(rs.getString(0, "para_value"), is("修改+测试修改配置参数init-hll"));
		}
		//2.错误数据访问
		//2-1.修改参数id不存在
		bodyString = new HttpClient()
				.addData("para_id", -9001L)
				.addData("para_name", "测试修改配置参数init-hll")
				.addData("para_value", "修改+测试修改配置参数init-hll")
				.post(getActionUrl("updateSysPara")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "新增系统参数测试方法",
			logicStep = "1.正确数据访问" +
					"1-1.正确添加数据" +
					"1-1-1.检查 Sys_para 参数是否新增成功" +
					"1-1-2.检查新增的数据是否正确" +
					"2.错误数据访问" +
					"2-1.参数名为空" +
					"2-2.参数值为空" +
					"2-3.参数类型为空")
	@Test
	public void addSysPara() {
		//1.正确数据访问
		//1-1.正确添加数据
		bodyString = new HttpClient()
				.addData("para_name", "测试新增配置参数init-hll")
				.addData("para_value", "测试新增配置参数init-hll")
				.addData("para_type", "server.properties")
				.post(getActionUrl("addSysPara")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1-1-1.检查 Sys_para 参数是否新增成功
			OptionalLong number = SqlOperator.queryNumber(db, "select count(*) from " + Sys_para.TableName +
					" where para_name=?", "测试新增配置参数init-hll");
			assertThat("检查 Sys_para 表数据，表 Sys_para 数据修改成功", number.getAsLong(), is(1L));
			//1-1-2.检查新增的数据是否正确
			Result rs = SqlOperator.queryResult(db, "select * from " + Sys_para.TableName + " where para_name=?",
					"测试新增配置参数init-hll");
			assertThat(rs.getString(0, "para_value"), is("测试新增配置参数init-hll"));
			assertThat(rs.getString(0, "para_value"), is("测试新增配置参数init-hll"));
		}
		//2.错误数据访问
		//2-1.参数名为空
		bodyString = new HttpClient()
				.addData("para_value", "测试新增配置参数init-hll")
				.addData("para_type", "server.properties")
				.post(getActionUrl("addSysPara")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));
		//2-2.参数值为空
		bodyString = new HttpClient()
				.addData("para_name", "测试新增配置参数init-hll")
				.addData("para_type", "server.properties")
				.post(getActionUrl("addSysPara")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));
		//2-3.参数类型为空
		bodyString = new HttpClient()
				.addData("para_name", "测试新增配置参数init-hll")
				.addData("para_value", "测试新增配置参数init-hll")
				.post(getActionUrl("addSysPara")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));
	}
}
