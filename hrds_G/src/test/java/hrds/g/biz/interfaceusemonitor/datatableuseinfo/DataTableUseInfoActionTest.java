package hrds.g.biz.interfaceusemonitor.datatableuseinfo;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.UserState;
import hrds.commons.codes.UserType;
import hrds.commons.entity.Sys_user;
import hrds.commons.entity.Sysreg_parameter_info;
import hrds.commons.entity.Table_use_info;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "表使用信息测试类（接口监控）", author = "dhw", createdate = "2020/5/15 10:02")
public class DataTableUseInfoActionTest extends WebBaseTestCase {

	//请填写测试用户需要做登录验证的A项目的登录验证的接口
	private static final String LOGIN_URL = agentInitConfig.getString("login_url");
	// 已经存在的用户ID,用于模拟登录
	private static final long USER_ID = agentInitConfig.getLong("user_id");
	private static final String PASSWORD = agentInitConfig.getString("password");
	private static final String DEP_ID = agentInitConfig.getString("dep_id");
	//当前线程的id
	private long THREAD_ID = Thread.currentThread().getId() * 1000000;

	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.造sys_user表数据，用于模拟登录
			Sys_user user = new Sys_user();
			user.setUser_id(THREAD_ID);
			user.setCreate_id(USER_ID);
			user.setDep_id(DEP_ID);
			user.setRole_id("1001");
			user.setUser_name("接口测试用户-dhw" + THREAD_ID);
			user.setUser_password(PASSWORD);
			// 0：管理员，1：操作员
			user.setUseris_admin(IsFlag.Shi.getCode());
			user.setUser_type(UserType.RESTYongHu.getCode());
			user.setUsertype_group(UserType.RESTYongHu.getCode() + "," + UserType.CaijiGuanLiYuan.getCode());
			user.setLogin_ip("127.0.0.1");
			user.setLogin_date(DateUtil.getSysDate());
			user.setUser_state(UserState.ZhengChang.getCode());
			user.setCreate_date(DateUtil.getSysDate());
			user.setCreate_time(DateUtil.getSysTime());
			user.setUpdate_date(DateUtil.getSysDate());
			user.setUpdate_time(DateUtil.getSysTime());
			user.setToken("0");
			user.setValid_time("0");
			user.setUser_email("123@163.com");
			user.setUser_remark("接口测试用户-dhw");
			assertThat("初始化数据成功", user.add(db), is(1));
			// 2.造table_use_info表测试数据
			Table_use_info table_use_info = new Table_use_info();
			table_use_info.setUse_id(THREAD_ID);
			table_use_info.setTable_blsystem(DataSourceType.DCL.getCode());
			table_use_info.setUser_id(THREAD_ID);
			table_use_info.setTable_note("监控监控表使用信息测试");
			table_use_info.setSysreg_name("fdc01_dhw_test");
			table_use_info.setOriginal_name("dhw_test");
			table_use_info.add(db);
			// 4.造sysreg_parameter_info表测试数据
			Sysreg_parameter_info sysreg_parameter_info = new Sysreg_parameter_info();
			for (int i = 0; i < 2; i++) {
				sysreg_parameter_info.setParameter_id(THREAD_ID + i);
				if (i == 0) {
					sysreg_parameter_info.setTable_ch_column("PARA_NAME");
					sysreg_parameter_info.setTable_en_column("PARA_NAME");
				} else {
					sysreg_parameter_info.setTable_ch_column("PARA_VALUE");
					sysreg_parameter_info.setTable_en_column("PARA_VALUE");
				}
				sysreg_parameter_info.setRemark("监控测试");
				sysreg_parameter_info.setIs_flag(IsFlag.Fou.getCode());
				sysreg_parameter_info.setUse_id(THREAD_ID);
				sysreg_parameter_info.setUser_id(USER_ID);
				sysreg_parameter_info.add(db);
			}
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

	@After
	public void after() {
		DatabaseWrapper db = new DatabaseWrapper();
		try {
			//1.清理sys_user表中造的数据
			SqlOperator.execute(db,
					"DELETE FROM " + Sys_user.TableName + " WHERE user_id = ?"
					, THREAD_ID);
			// 2.清理Table_use_info表数据
			SqlOperator.execute(db,
					"DELETE FROM " + Table_use_info.TableName + " WHERE use_id =?"
					, THREAD_ID);
			// 3.清理Sysreg_parameter_info表数据
			for (int i = 0; i < 2; i++) {
				SqlOperator.execute(db,
						"DELETE FROM " + Sysreg_parameter_info.TableName + " WHERE parameter_id =?"
						, THREAD_ID + i);
			}
			SqlOperator.commitTransaction(db);
		} catch (Exception e) {
			db.rollback();
		} finally {
			db.close();
		}
	}

	@Method(desc = "查询数据表信息（接口使用监控）", logicStep = "1.正确的数据访问1,数据都有效" +
			"备注，该方法只有一种可能")
	@Test
	public void searchTableData() {
		// 1.正确的数据访问1,数据都有效
		String bodyString = new HttpClient().buildSession()
				.post(getActionUrl("searchTableData")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		for (int i = 0; i < result.getRowCount(); i++) {
			if (result.getLong(i, "use_id") == THREAD_ID) {
				assertThat(result.getString(i, "sysreg_name"), is("fdc01_dhw_test"));
				assertThat(result.getString(i, "original_name"), is("dhw_test"));
				assertThat(result.getString(i, "user_name"), is("接口测试用户-dhw" + THREAD_ID));
			}
		}
	}

	@Method(desc = "查询数据表信息（接口使用监控）", logicStep = "1.正确的数据访问1,数据都有效" +
			"2.的数据访问2,user_id不存在" +
			"3.错误的数据访问1,user_id为空")
	@Test
	public void searchTableDataById() {
		// 1.正确的数据访问1,数据都有效
		String bodyString = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.post(getActionUrl("searchTableDataById")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		for (int i = 0; i < result.getRowCount(); i++) {
			if (result.getLong(i, "use_id") == THREAD_ID) {
				assertThat(result.getString(i, "sysreg_name"), is("fdc01_dhw_test"));
				assertThat(result.getString(i, "original_name"), is("dhw_test"));
				assertThat(result.getString(i, "user_name"), is("接口测试用户-dhw" + THREAD_ID));
			}
		}
		// 2.的数据访问2,user_id不存在
		bodyString = new HttpClient().buildSession()
				.addData("user_id", "4321")
				.post(getActionUrl("searchTableDataById")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		result = ar.getDataForResult();
		assertThat(result.isEmpty(), is(true));
		// 3.错误的数据访问1,user_id为空
		bodyString = new HttpClient().buildSession()
				.addData("user_id", "")
				.post(getActionUrl("searchTableDataById")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "查询数据表信息（接口使用监控）", logicStep = "1.正确的数据访问1,数据都有效" +
			"2.错误的数据访问1,use_id为空" +
			"3.错误的数据访问2,user_id不存在")
	@Test
	public void searchFieldInfoById() {
		// 1.正确的数据访问1,数据都有效
		String bodyString = new HttpClient().buildSession()
				.addData("use_id", THREAD_ID)
				.post(getActionUrl("searchFieldInfoById")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		List<Sysreg_parameter_info> parameterInfos = ar.getDataForEntityList(Sysreg_parameter_info.class);
		parameterInfos.forEach(parameter_info -> {
			if (parameter_info.getParameter_id() == THREAD_ID) {
				assertThat(parameter_info.getTable_ch_column(), is("PARA_NAME"));
				assertThat(parameter_info.getTable_en_column(), is("PARA_NAME"));
			} else {
				assertThat(parameter_info.getTable_ch_column(), is("PARA_VALUE"));
				assertThat(parameter_info.getTable_en_column(), is("PARA_VALUE"));
			}
		});
		// 2.错误的数据访问1,use_id为空
		bodyString = new HttpClient().buildSession()
				.addData("use_id", "")
				.post(getActionUrl("searchFieldInfoById")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2,use_id不存在
		bodyString = new HttpClient().buildSession()
				.addData("use_id", "12333")
				.post(getActionUrl("searchFieldInfoById")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForEntityList(Sysreg_parameter_info.class).isEmpty(), is(true));
	}

	@Method(desc = "根据表使用ID删除数据表信息（接口使用监控）", logicStep = "1.正确的数据访问1,数据都有效" +
			"2.错误的数据访问1,use_id为空" +
			"备注：只有两种情况")
	@Test
	public void deleteDataTableUseInfo() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 确认删除的数据存在
			long num = SqlOperator.queryNumber(db,
					"select count(*) from " + Table_use_info.TableName + " where use_id=?",
					THREAD_ID).orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(1L));
			num = SqlOperator.queryNumber(db,
					"select count(*) from " + Sysreg_parameter_info.TableName + " where use_id=?",
					THREAD_ID).orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(2L));
			// 1.正确的数据访问1,数据都有效
			String bodyString = new HttpClient().buildSession()
					.addData("use_id", THREAD_ID)
					.post(getActionUrl("deleteDataTableUseInfo")).getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败"));
			assertThat(ar.isSuccess(), is(true));
			// 确认数据已删除
			num = SqlOperator.queryNumber(db,
					"select count(*) from " + Table_use_info.TableName + " where use_id=?",
					THREAD_ID).orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(0L));
			num = SqlOperator.queryNumber(db,
					"select count(*) from " + Sysreg_parameter_info.TableName + " where use_id=?",
					THREAD_ID).orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(0L));
			// 2.错误的数据访问1,use_id为空
			bodyString = new HttpClient().buildSession()
					.addData("use_id", "")
					.post(getActionUrl("deleteDataTableUseInfo")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败"));
			assertThat(ar.isSuccess(), is(false));
		}
	}
}