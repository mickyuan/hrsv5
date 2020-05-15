package hrds.g.biz.interfaceusemonitor.interfaceuseinfo;

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
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Interface_use;
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "监控接口使用信息测试类", author = "dhw", createdate = "2020/5/14 17:00")
public class InterfaceUseInfoActionTest extends WebBaseTestCase {

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
			SqlOperator.commitTransaction(db);
		}
	}

	@Method(desc = "查询接口监控信息（接口使用监控）", logicStep = "1.正确的数据访问1,数据都有效" +
			"2.错误的数据访问1，interface_use_id为空" +
			"3.错误的数据访问2，interface_use_id不存在" +
			"4.错误的数据访问3，use_state为空" +
			"5.错误的数据访问4，use_state不存在")
	@Test
	public void interfaceDisableEnable() {
		// 1.正确的数据访问1,数据都有效
		bodyString = new HttpClient().buildSession()
				.addData("interface_use_id", INTERFACE_USE_ID)
				.addData("use_state", InterfaceState.QiYong.getCode())
				.post(getActionUrl("interfaceDisableEnable")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Interface_use interface_use = SqlOperator.queryOneObject(db, Interface_use.class,
					"select interface_use_id,use_state from " + Interface_use.TableName
							+ " where interface_use_id=?", INTERFACE_USE_ID)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(interface_use.getInterface_use_id(), is(INTERFACE_USE_ID));
			assertThat(interface_use.getUse_state(), is(InterfaceState.QiYong.getCode()));
		}
		// 2.错误的数据访问1，interface_use_id为空
		bodyString = new HttpClient().buildSession()
				.addData("interface_use_id", "")
				.addData("use_state", InterfaceState.QiYong.getCode())
				.post(getActionUrl("interfaceDisableEnable")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，interface_use_id不存在
		bodyString = new HttpClient().buildSession()
				.addData("interface_use_id", "123456")
				.addData("use_state", InterfaceState.QiYong.getCode())
				.post(getActionUrl("interfaceDisableEnable")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，use_state为空
		bodyString = new HttpClient().buildSession()
				.addData("interface_use_id", INTERFACE_USE_ID)
				.addData("use_state", "")
				.post(getActionUrl("interfaceDisableEnable")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问4，use_state不存在
		bodyString = new HttpClient().buildSession()
				.addData("interface_use_id", INTERFACE_USE_ID)
				.addData("use_state", "6")
				.post(getActionUrl("interfaceDisableEnable")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "查询接口监控信息（接口使用监控）", logicStep = "1.正确的数据访问1,该方法只有一种可能")
	@Test
	public void searchInterfaceInfo() {
		// 1.正确的数据访问1
		bodyString = new HttpClient().buildSession()
				.post(getActionUrl("searchInterfaceInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		assertThat(result.getLong(0, "interface_use_id"), is(INTERFACE_USE_ID));
		assertThat(result.getString(0, "interface_name"), is("表使用权限查询接口"));
		assertThat(result.getString(0, "interface_code"), is("01-123"));
		assertThat(result.getString(0, "user_name"), is("接口测试用户-dhw0"));
		assertThat(result.getString(0, "use_state"), is(UserState.ZhengChang.getCode()));
	}

	@Method(desc = "根据用户ID或有效日期查询接口监控信息（接口使用监控）",
			logicStep = "1.正确的数据访问1,uer_id不为空，use_valid_date为空" +
					"2.正确的数据访问2,uer_id为空，use_valid_date不为空" +
					"3.正确的数据访问3,uer_id不为空，use_valid_date不为空" +
					"4.正确的数据访问4,uer_id为空，use_valid_date为空")
	@Test
	public void searchInterfaceInfoByIdOrDate() {
		// 1.正确的数据访问1,uer_id不为空，use_valid_date为空
		bodyString = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.post(getActionUrl("searchInterfaceInfoByIdOrDate")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		assertThat(result.getLong(0, "interface_use_id"), is(INTERFACE_USE_ID));
		assertThat(result.getString(0, "interface_name"), is("表使用权限查询接口"));
		assertThat(result.getString(0, "interface_code"), is("01-123"));
		assertThat(result.getString(0, "user_name"), is("接口测试用户-dhw0"));
		assertThat(result.getString(0, "use_state"), is(UserState.ZhengChang.getCode()));
		// 2.正确的数据访问2,uer_id为空，use_valid_date不为空
		bodyString = new HttpClient().buildSession()
				.addData("use_valid_date", ENDATE)
				.post(getActionUrl("searchInterfaceInfoByIdOrDate")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		result = ar.getDataForResult();
		assertThat(result.getLong(0, "interface_use_id"), is(INTERFACE_USE_ID));
		assertThat(result.getString(0, "interface_name"), is("表使用权限查询接口"));
		assertThat(result.getString(0, "interface_code"), is("01-123"));
		assertThat(result.getString(0, "user_name"), is("接口测试用户-dhw0"));
		assertThat(result.getString(0, "use_state"), is(UserState.ZhengChang.getCode()));
		// 3.正确的数据访问3,uer_id不为空，use_valid_date不为空
		bodyString = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("use_valid_date", ENDATE)
				.post(getActionUrl("searchInterfaceInfoByIdOrDate")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		result = ar.getDataForResult();
		assertThat(result.getLong(0, "interface_use_id"), is(INTERFACE_USE_ID));
		assertThat(result.getString(0, "interface_name"), is("表使用权限查询接口"));
		assertThat(result.getString(0, "interface_code"), is("01-123"));
		assertThat(result.getString(0, "user_name"), is("接口测试用户-dhw0"));
		assertThat(result.getString(0, "use_state"), is(UserState.ZhengChang.getCode()));
		// 4.正确的数据访问4,uer_id为空，use_valid_date为空
		bodyString = new HttpClient().buildSession()
				.post(getActionUrl("searchInterfaceInfoByIdOrDate")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		result = ar.getDataForResult();
		assertThat(result.getLong(0, "interface_use_id"), is(INTERFACE_USE_ID));
		assertThat(result.getString(0, "interface_name"), is("表使用权限查询接口"));
		assertThat(result.getString(0, "interface_code"), is("01-123"));
		assertThat(result.getString(0, "user_name"), is("接口测试用户-dhw0"));
		assertThat(result.getString(0, "use_state"), is(UserState.ZhengChang.getCode()));
	}

	@Method(desc = "根据用户ID或有效日期查询接口监控信息（接口使用监控）",
			logicStep = "1.正确的数据访问1,uer_id不为空，use_valid_date为空" +
					"2.错误的数据访问1,interface_use_id为空" +
					"备注：此方法只有两种情况")
	@Test
	public void deleteInterfaceUseInfo() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 删除前确定有一条数据
			if (SqlOperator.queryNumber(db, "select count(*) from " + Interface_use.TableName +
					" where interface_use_id=?", INTERFACE_USE_ID).orElseThrow(() -> new BusinessException("sql查询错误")) != 1) {
				throw new BusinessException("必须有一条数据要被删除");
			}
			// 1.正确的数据访问1,数据都有效
			bodyString = new HttpClient().buildSession()
					.addData("interface_use_id", INTERFACE_USE_ID)
					.post(getActionUrl("deleteInterfaceUseInfo")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败"));
			assertThat(ar.isSuccess(), is(true));
			// 确定此条数据已被删除
			if (SqlOperator.queryNumber(db, "select count(*) from " + Interface_use.TableName +
					" where interface_use_id=?", INTERFACE_USE_ID).orElseThrow(() -> new BusinessException("sql查询错误")) != 0) {
				throw new BusinessException("此条数据没有被删除");
			}
			// 2.错误的数据访问1,interface_use_id为空
			bodyString = new HttpClient().buildSession()
					.addData("interface_use_id", "")
					.post(getActionUrl("deleteInterfaceUseInfo")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "根据接口使用ID获取相应的信息（接口使用监控）",
			logicStep = "1.正确的数据访问1,数据都有效" +
					"2.错误的数据访问1,interface_use_id为空" +
					"3.错误的数据访问2,interface_use_id不存在")
	@Test
	public void searchInterfaceUseInfoById() {
		// 1.正确的数据访问1,数据都有效
		bodyString = new HttpClient().buildSession()
				.addData("interface_use_id", INTERFACE_USE_ID)
				.post(getActionUrl("searchInterfaceUseInfoById")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("interface_use_id").toString(), is(String.valueOf(INTERFACE_USE_ID)));
		assertThat(dataForMap.get("use_valid_date"), is(ENDATE));
		assertThat(dataForMap.get("start_use_date"), is(SYSDATE));
		// 2.错误的数据访问1,interface_use_id为空
		bodyString = new HttpClient().buildSession()
				.addData("interface_use_id", "")
				.post(getActionUrl("searchInterfaceUseInfoById")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2,interface_use_id不存在
		bodyString = new HttpClient().buildSession()
				.addData("interface_use_id", "1243124")
				.post(getActionUrl("searchInterfaceUseInfoById")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		assertThat(dataForMap.size(), is(0));
	}

	@Method(desc = "更新接口使用信息（接口使用监控）",
			logicStep = "1.正确的数据访问1,数据都有效" +
					"2.正确的数据访问2，use_valid_date格式10位" +
					"3.正确的数据访问3，use_valid_date格式10位" +
					"4.错误的数据访问1，interface_use_id为空" +
					"5.错误的数据访问2，start_use_date为空" +
					"6.错误的数据访问3，use_valid_date为空")
	@Test
	public void updateInterfaceUseInfo() {
		// 1.正确的数据访问1,数据都有效
		bodyString = new HttpClient().buildSession()
				.addData("interface_use_id", INTERFACE_USE_ID)
				.addData("start_use_date", SYSDATE)
				.addData("use_valid_date", ENDATE)
				.post(getActionUrl("updateInterfaceUseInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Interface_use interface_use = SqlOperator.queryOneObject(db, Interface_use.class,
					"select interface_use_id,start_use_date,use_valid_date from "
							+ Interface_use.TableName + " where interface_use_id=?", INTERFACE_USE_ID)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(interface_use.getInterface_use_id(), is(INTERFACE_USE_ID));
			assertThat(interface_use.getStart_use_date(), is(SYSDATE));
			assertThat(interface_use.getUse_valid_date(), is(ENDATE));
		}
		// 2.正确的数据访问2，use_valid_date格式10位
		bodyString = new HttpClient().buildSession()
				.addData("interface_use_id", INTERFACE_USE_ID)
				.addData("start_use_date", DateUtil.parseStr2DateWith8Char(SYSDATE).toString())
				.addData("use_valid_date", ENDATE)
				.post(getActionUrl("updateInterfaceUseInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		// 3.正确的数据访问3，use_valid_date格式10位
		bodyString = new HttpClient().buildSession()
				.addData("interface_use_id", INTERFACE_USE_ID)
				.addData("start_use_date", SYSDATE)
				.addData("use_valid_date", DateUtil.parseStr2DateWith8Char(ENDATE).toString())
				.post(getActionUrl("updateInterfaceUseInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		// 4.错误的数据访问1，interface_use_id为空
		bodyString = new HttpClient().buildSession()
				.addData("interface_use_id", "")
				.addData("start_use_date", SYSDATE)
				.addData("use_valid_date", ENDATE)
				.post(getActionUrl("updateInterfaceUseInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问2，start_use_date为空
		bodyString = new HttpClient().buildSession()
				.addData("interface_use_id", INTERFACE_USE_ID)
				.addData("start_use_date", "")
				.addData("use_valid_date", ENDATE)
				.post(getActionUrl("updateInterfaceUseInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
		// 6.错误的数据访问3，use_valid_date为空
		bodyString = new HttpClient().buildSession()
				.addData("interface_use_id", INTERFACE_USE_ID)
				.addData("start_use_date", SYSDATE)
				.addData("use_valid_date", "")
				.post(getActionUrl("updateInterfaceUseInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
	}
}