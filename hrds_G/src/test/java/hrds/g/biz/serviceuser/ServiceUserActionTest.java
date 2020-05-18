package hrds.g.biz.serviceuser;

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
import hrds.commons.codes.*;
import hrds.commons.entity.*;
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

@DocClass(desc = "服务接口用户测试类", author = "dhw", createdate = "2020/5/15 13:48")
public class ServiceUserActionTest extends WebBaseTestCase {

	private static final String SYSDATE = DateUtil.getSysDate();
	private static final String ENDATE = "20991231";
	private static String bodyString;
	private static ActionResult ar;
	// 用户ID
	private static final long USER_ID = 8886L;
	// 部门ID
	private static final long DEP_ID = 8886L;
	private static final int USERROWS = 2;
	// 接口使用ID
	private static final long INTERFACE_USE_ID = 100000001L;
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
			// 3.造interface_use表测试数据
			Interface_use interface_use = new Interface_use();
			interface_use.setUse_valid_date(ENDATE);
			interface_use.setInterface_use_id(INTERFACE_USE_ID);
			interface_use.setClassify_name("jkjkcs");
			interface_use.setInterface_id(104L);
			interface_use.setInterface_note("接口监控测试");
			interface_use.setUse_state(InterfaceState.JinYong.getCode());
			interface_use.setUser_id(USER_ID);
			interface_use.setCreate_id(USER_ID);
			interface_use.setTheir_type(InterfaceType.ShuJuLei.getCode());
			interface_use.setInterface_code("01-123");
			interface_use.setUrl("tableUsePermissions");
			interface_use.setInterface_name("表使用权限查询接口");
			interface_use.setStart_use_date(SYSDATE);
			interface_use.setUser_name("接口测试用户-dhw0");
			interface_use.add(db);
			// 4.造table_use_info表测试数据
			Table_use_info table_use_info = new Table_use_info();
			table_use_info.setTable_blsystem(DataSourceType.DCL.getCode());
			table_use_info.setUser_id(USER_ID);
			table_use_info.setTable_note("监控监控表使用信息测试");
			table_use_info.setSysreg_name("fdc01_dhw_test");
			table_use_info.setOriginal_name("dhw_test");
			table_use_info.setUse_id(INTERFACE_USE_ID);
			table_use_info.add(db);
			// 5.造sysreg_parameter_info表测试数据
			Sysreg_parameter_info sysreg_parameter_info = new Sysreg_parameter_info();
			sysreg_parameter_info.setParameter_id(PARAM_ID);
			sysreg_parameter_info.setTable_ch_column("PARA_NAME");
			sysreg_parameter_info.setTable_en_column("PARA_NAME");
			sysreg_parameter_info.setRemark("监控测试");
			sysreg_parameter_info.setIs_flag(IsFlag.Fou.getCode());
			sysreg_parameter_info.setUse_id(INTERFACE_USE_ID);
			sysreg_parameter_info.setUser_id(USER_ID);
			sysreg_parameter_info.add(db);
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
		bodyString = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("password", "1")
				.post("http://127.0.0.1:8888/A/action/hrds/a/biz/login/login").getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
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
			// 3.清理interface_use表数据
			SqlOperator.execute(db, "DELETE FROM " + Interface_use.TableName + " WHERE user_id =?"
					, USER_ID);
			// 4.清理Table_use_info表数据
			SqlOperator.execute(db, "DELETE FROM " + Table_use_info.TableName + " WHERE user_id =?"
					, USER_ID);
			// 5.清理Sysreg_parameter_info表数据
			SqlOperator.execute(db, "DELETE FROM " + Sysreg_parameter_info.TableName + " WHERE user_id =?"
					, USER_ID);
			SqlOperator.commitTransaction(db);
		}
	}

	@Method(desc = "查询接口信息", logicStep = "1.正确的数据访问1,数据都有效" +
			"2.正确的数据访问2,interface_name不为空" +
			"3.正确的数据访问3,interface_name为空" +
			"4.正确的数据访问4,interface_name不存在")
	@Test
	public void searchInterfaceInfo() {
		// 1.正确的数据访问1,数据都有效
		bodyString = new HttpClient().buildSession()
				.post(getActionUrl("searchInterfaceInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		assertThat(result.getLong(0, "interface_use_id"), is(INTERFACE_USE_ID));
		assertThat(result.getString(0, "interface_name"), is("表使用权限查询接口"));
		assertThat(result.getString(0, "url"), is("tableUsePermissions"));
		assertThat(result.getString(0, "start_use_date"), is(SYSDATE));
		assertThat(result.getString(0, "use_valid_date"), is(ENDATE));
		// 2.正确的数据访问2,interface_name不为空
		bodyString = new HttpClient().buildSession()
				.addData("interface_name", "tableUsePermissions")
				.post(getActionUrl("searchInterfaceInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		// 3.正确的数据访问3,interface_name为空
		bodyString = new HttpClient().buildSession()
				.addData("interface_name", "")
				.post(getActionUrl("searchInterfaceInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		// 4.正确的数据访问4,interface_name不存在
		bodyString = new HttpClient().buildSession()
				.addData("interface_name", "aaa")
				.post(getActionUrl("searchInterfaceInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForResult().isEmpty(), is(true));
	}

	@Method(desc = "查询数据表信息", logicStep = "1.正确的数据访问1,数据都有效" +
			"2.正确的数据访问2,sysreg_name不为空" +
			"3.正确的数据访问3,sysreg_name为空" +
			"4.正确的数据访问4,sysreg_name不存在")
	@Test
	public void searchDataTableInfo() {
		// 1.正确的数据访问1,数据都有效
		bodyString = new HttpClient().buildSession()
				.post(getActionUrl("searchDataTableInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		assertThat(result.getLong(0, "use_id"), is(INTERFACE_USE_ID));
		assertThat(result.getString(0, "sysreg_name"), is("fdc01_dhw_test"));
		assertThat(result.getString(0, "original_name"), is("dhw_test"));
		// 2.正确的数据访问2,sysreg_name不为空
		bodyString = new HttpClient().buildSession()
				.addData("sysreg_name", "fdc01_dhw_test")
				.post(getActionUrl("searchDataTableInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		// 3.正确的数据访问3,sysreg_name为空
		bodyString = new HttpClient().buildSession()
				.addData("sysreg_name", "")
				.post(getActionUrl("searchDataTableInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		// 4.正确的数据访问4,sysreg_name不存在
		bodyString = new HttpClient().buildSession()
				.addData("sysreg_name", "aaa")
				.post(getActionUrl("searchDataTableInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForResult().isEmpty(), is(true));
	}

	@Method(desc = "根据表使用ID查询当前用户对应的列信息", logicStep = "1.正确的数据访问1,数据都有效" +
			"2.错误的数据访问1,use_id为空" +
			"3.错误的数据访问2,use_id不存在")
	@Test
	public void searchColumnInfoById() {
		// 1.正确的数据访问1,数据都有效
		bodyString = new HttpClient().buildSession()
				.addData("use_id", INTERFACE_USE_ID)
				.post(getActionUrl("searchColumnInfoById")).getBodyString();
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
				.post(getActionUrl("searchColumnInfoById")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2,use_id不存在
		bodyString = new HttpClient().buildSession()
				.addData("use_id", "12333")
				.post(getActionUrl("searchColumnInfoById")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getData().toString(), is(""));
	}

	@Method(desc = "获取当前用户请求ip端口", logicStep = "1.正确的数据访问1,数据都有效" +
			"备注：该方法只有一种可能")
	@Test
	public void getIpAndPort() {
		// 1.正确的数据访问1,数据都有效
		bodyString = new HttpClient().buildSession()
				.addData("use_id", INTERFACE_USE_ID)
				.post(getActionUrl("getIpAndPort")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("ipAndPort"), is("10.71.4.57:8091"));
	}
}