package hrds.g.biz.interfaceusemonitor.datatableuseinfo;

import com.alibaba.fastjson.TypeReference;
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
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Sys_user;
import hrds.commons.entity.Sysreg_parameter_info;
import hrds.commons.entity.Table_use_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "表使用信息测试类（接口监控）", author = "dhw", createdate = "2020/5/15 10:02")
public class DataTableUseInfoActionTest extends WebBaseTestCase {

	private static String bodyString;
	private static ActionResult ar;
	// 用户ID
	private static final long USER_ID = 8886L;
	// 部门ID
	private static final long DEP_ID = 8886L;
	private static final int USERROWS = 2;
	// 接口使用ID
	private static final long USE_ID = 10000003L;
	// 接口表参数信息ID
	private static final long PARAM_ID = 20000002L;

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
			// 3.造table_use_info表测试数据
			Table_use_info table_use_info = new Table_use_info();
			table_use_info.setTable_blsystem(DataSourceType.DCL.getCode());
			table_use_info.setUser_id(USER_ID);
			table_use_info.setTable_note("监控监控表使用信息测试");
			table_use_info.setSysreg_name("fdc01_dhw_test");
			table_use_info.setOriginal_name("dhw_test");
			table_use_info.setUse_id(USE_ID);
			table_use_info.add(db);
			// 4.造sysreg_parameter_info表测试数据
			Sysreg_parameter_info sysreg_parameter_info = new Sysreg_parameter_info();
			sysreg_parameter_info.setParameter_id(PARAM_ID);
			sysreg_parameter_info.setTable_ch_column("PARA_NAME");
			sysreg_parameter_info.setTable_en_column("PARA_NAME");
			sysreg_parameter_info.setRemark("监控测试");
			sysreg_parameter_info.setIs_flag(IsFlag.Fou.getCode());
			sysreg_parameter_info.setUse_id(USE_ID);
			sysreg_parameter_info.setUser_id(USER_ID);
			sysreg_parameter_info.add(db);
			// 提交事务
			SqlOperator.commitTransaction(db);
		}

		bodyString = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("password", "1")
				.post("http://127.0.0.1:8888/A/action/hrds/a/biz/login/login").
						getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).
				orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
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
			// 3.清理Table_use_info表数据
			SqlOperator.execute(db, "DELETE FROM " + Table_use_info.TableName + " WHERE user_id =?"
					, USER_ID);
			// 3.清理Sysreg_parameter_info表数据
			SqlOperator.execute(db, "DELETE FROM " + Sysreg_parameter_info.TableName + " WHERE user_id =?"
					, USER_ID);
			SqlOperator.commitTransaction(db);
		}
	}

	@Method(desc = "查询数据表信息（接口使用监控）", logicStep = "1.正确的数据访问1,数据都有效" +
			"备注，该方法只有一种可能")
	@Test
	public void searchTableData() {
		// 1.正确的数据访问1,数据都有效
		bodyString = new HttpClient().buildSession()
				.post(getActionUrl("searchTableData")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		for (int i = 0; i < result.getRowCount(); i++) {
			long use_id = result.getLong(i, "use_id");
			if (use_id == USE_ID) {
				assertThat(result.getString(i, "sysreg_name"), is("fdc01_dhw_test"));
				assertThat(result.getString(i, "original_name"), is("dhw_test"));
				assertThat(result.getString(i, "user_name"), is("接口测试用户-dhw0"));
			}
		}
	}

	@Method(desc = "查询数据表信息（接口使用监控）", logicStep = "1.正确的数据访问1,数据都有效" +
			"2.的数据访问2,user_id不存在" +
			"3.错误的数据访问1,user_id为空")
	@Test
	public void searchTableDataById() {
		// 1.正确的数据访问1,数据都有效
		bodyString = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.post(getActionUrl("searchTableDataById")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		for (int i = 0; i < result.getRowCount(); i++) {
			long use_id = result.getLong(i, "use_id");
			if (use_id == USE_ID) {
				assertThat(result.getString(i, "sysreg_name"), is("fdc01_dhw_test"));
				assertThat(result.getString(i, "original_name"), is("dhw_test"));
				assertThat(result.getString(i, "user_name"), is("接口测试用户-dhw0"));
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
		bodyString = new HttpClient().buildSession()
				.addData("use_id", USE_ID)
				.post(getActionUrl("searchFieldInfoById")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Type type = new TypeReference<List<Map<String, String>>>() {
		}.getType();
		List<Map<String, String>> list = JsonUtil.toObject(ar.getData().toString(), type);
		StringBuilder sb = new StringBuilder();
		list.forEach(map -> {
			String table_column_name = map.get("table_column_name");
			sb.append(table_column_name).append(Constant.METAINFOSPLIT);
		});
		assertThat(sb.deleteCharAt(sb.length() - 1).toString(), is("PARA_NAME^PARA_VALUE^PARA_TYPE^PARA_ID"));
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
		assertThat(ar.getData().toString(), is(""));
	}

	@Method(desc = "根据表使用ID删除数据表信息（接口使用监控）", logicStep = "1.正确的数据访问1,数据都有效" +
			"2.错误的数据访问1,use_id为空" +
			"备注：只有两种情况")
	@Test
	public void deleteDataTableUseInfo() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 确认删除的数据存在
			long num = SqlOperator.queryNumber(db, "select count(*) from " + Table_use_info.TableName
					+ " where use_id=?", USE_ID).orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(1L));
			num = SqlOperator.queryNumber(db, "select count(*) from " + Sysreg_parameter_info.TableName
					+ " where use_id=?", USE_ID).orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(1L));
			// 1.正确的数据访问1,数据都有效
			bodyString = new HttpClient().buildSession()
					.addData("use_id", USE_ID)
					.post(getActionUrl("deleteDataTableUseInfo")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败"));
			assertThat(ar.isSuccess(), is(true));
			// 确认数据已删除
			num = SqlOperator.queryNumber(db, "select count(*) from " + Table_use_info.TableName
					+ " where use_id=?", USE_ID).orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(0L));
			num = SqlOperator.queryNumber(db, "select count(*) from " + Sysreg_parameter_info.TableName
					+ " where use_id=?", USE_ID).orElseThrow(() -> new BusinessException("sql查询错误"));
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