package hrds.g.biz.usermanage;

import com.alibaba.fastjson.TypeReference;
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
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class InterfaceUserManageActionTest extends WebBaseTestCase {

	private static final Type LISTTYPE = new TypeReference<List<Map<String, Object>>>() {
	}.getType();
	private static String bodyString;
	private static ActionResult ar;
	// 用户ID
	private static final long USER_ID = 8886L;
	// 部门ID
	private static final long DEP_ID = 8886L;
	private static final int USERROWS = 2;

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

			// 提交事务
			SqlOperator.commitTransaction(db);
		}
		String responseValue = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("password", "1")
				.post("http://127.0.0.1:8888/A/action/hrds/a/biz/login/login").getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
	}

	@Method(desc = "查询接口用户信息测试方法", logicStep = "1.正确的数据访问1，user_name为空" +
			"2.正确的数据访问1，user_name不为空" +
			"3.该方法只有两种可能")
	@Test
	public void selectUserInfo() {
		//1.正确的数据访问1，user_name为空
		bodyString = new HttpClient()
				.post(getActionUrl("selectUserInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		assertThat("接口测试用户-dhw0", is(result.getString(0, "user_name")));
		assertThat("123@163.com", is(result.getString(0, "user_email")));
		assertThat("1", is(result.getString(0, "user_password")));
		assertThat("接口测试用户-dhw0", is(result.getString(0, "user_remark")));
		// 2.正确的数据访问1，user_name不为空
		bodyString = new HttpClient().addData("user_name", "接口测试用户-dhw0")
				.post(getActionUrl("selectUserInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		result = ar.getDataForResult();
		for (int i = 0; i < result.getRowCount(); i++) {
			assertThat("接口测试用户-dhw" + i, is(result.getString(0, "user_name")));
			assertThat("123@163.com", is(result.getString(0, "user_email")));
			assertThat("1", is(result.getString(0, "user_password")));
			assertThat("接口测试用户-dhw" + i, is(result.getString(0, "user_remark")));
		}
	}

	@Method(desc = "添加接口用户", logicStep = "1.正确的数据访问1，user_name不为空查询信息" +
			"2.错误的数据访问1，user_name为空" +
			"3.错误的数据访问2，user_name为空格" +
			"4.错误的数据访问3，user_password为空" +
			"5.错误的数据访问4，user_password为空格" +
			"6.错误的数据访问5，user_email为空" +
			"7.错误的数据访问6，user_email为空格" +
			"8.错误的数据访问6，user_email不合法")
	@Test
	public void addUser() {
		//1.正确的数据访问1，数据都有效
		bodyString = new HttpClient()
				.addData("user_name", "新增接口用户测试")
				.addData("user_password", "1")
				.addData("user_email", "123@qq.com")
				.addData("user_remark", "新增接口用户测试")
				.post(getActionUrl("addUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Sys_user sys_user = SqlOperator.queryOneObject(db, Sys_user.class,
					"select user_name,user_id,user_password,user_email,user_remark from "
							+ Sys_user.TableName + " where create_id=? and user_type=? and user_name=?",
					USER_ID, UserType.RESTYongHu.getCode(), "新增接口用户测试")
					.orElseThrow(() -> new BusinessException("sql查询错误，必须有一条数据"));
			assertThat(sys_user.getUser_name(), is("新增接口用户测试"));
			assertThat(sys_user.getUser_password(), is("1"));
			assertThat(sys_user.getUser_email(), is("123@qq.com"));
			assertThat(sys_user.getUser_remark(), is("新增接口用户测试"));
		}
		//2.错误的数据访问1，user_name为空
		bodyString = new HttpClient()
				.addData("user_name", "")
				.addData("user_password", "1")
				.addData("user_email", "123@qq.com")
				.addData("user_remark", "新增接口用户测试")
				.post(getActionUrl("addUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		//3.错误的数据访问2，user_name为空格
		bodyString = new HttpClient()
				.addData("user_name", " ")
				.addData("user_password", "1")
				.addData("user_email", "123@qq.com")
				.addData("user_remark", "新增接口用户测试")
				.post(getActionUrl("addUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		//4.错误的数据访问3，user_password为空
		bodyString = new HttpClient()
				.addData("user_name", "新增接口用户测试")
				.addData("user_password", "")
				.addData("user_email", "123@qq.com")
				.addData("user_remark", "新增接口用户测试")
				.post(getActionUrl("addUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		//5.错误的数据访问4，user_password为空格
		bodyString = new HttpClient()
				.addData("user_name", "新增接口用户测试")
				.addData("user_password", " ")
				.addData("user_email", "123@qq.com")
				.addData("user_remark", "新增接口用户测试")
				.post(getActionUrl("addUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		//6.错误的数据访问5，user_email为空
		bodyString = new HttpClient()
				.addData("user_name", "新增接口用户测试")
				.addData("user_password", "111")
				.addData("user_email", "")
				.addData("user_remark", "新增接口用户测试")
				.post(getActionUrl("addUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		//7.错误的数据访问6，user_email为空格
		bodyString = new HttpClient()
				.addData("user_name", "新增接口用户测试")
				.addData("user_password", "111")
				.addData("user_email", " ")
				.addData("user_remark", "新增接口用户测试")
				.post(getActionUrl("addUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		//8.错误的数据访问6，user_email不合法
		bodyString = new HttpClient()
				.addData("user_name", "新增接口用户测试")
				.addData("user_password", "111")
				.addData("user_email", "123")
				.addData("user_remark", "新增接口用户测试")
				.post(getActionUrl("addUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "更新接口用户", logicStep = "1.正确的数据访问1，user_name不为空查询信息" +
			"2.错误的数据访问1，user_name为空" +
			"3.错误的数据访问2，user_name为空格" +
			"4.错误的数据访问3，user_password为空" +
			"5.错误的数据访问4，user_password为空格" +
			"6.错误的数据访问5，user_email为空" +
			"7.错误的数据访问6，user_email为空格" +
			"8.错误的数据访问6，user_email不合法")
	@Test
	public void updateUser() {
		//1.正确的数据访问1，数据都有效
		bodyString = new HttpClient()
				.addData("user_id", USER_ID)
				.addData("user_name", "更新接口用户测试")
				.addData("user_password", "111")
				.addData("user_email", "123456@qq.com")
				.addData("user_remark", "更新接口用户测试")
				.post(getActionUrl("updateUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Sys_user sys_user = SqlOperator.queryOneObject(db, Sys_user.class,
					"select user_name,user_id,user_password,user_email,user_remark from "
							+ Sys_user.TableName + " where create_id=? and user_type=? and user_name=?",
					USER_ID, UserType.RESTYongHu.getCode(), "更新接口用户测试")
					.orElseThrow(() -> new BusinessException("sql查询错误，必须有一条数据"));
			assertThat(sys_user.getUser_name(), is("更新接口用户测试"));
			assertThat(sys_user.getUser_password(), is("111"));
			assertThat(sys_user.getUser_email(), is("123456@qq.com"));
			assertThat(sys_user.getUser_remark(), is("更新接口用户测试"));
			assertThat(sys_user.getUser_id(), is(USER_ID));
		}
		//2.错误的数据访问1，user_name为空
		bodyString = new HttpClient()
				.addData("user_id", USER_ID)
				.addData("user_name", "")
				.addData("user_password", "1")
				.addData("user_email", "123@qq.com")
				.addData("user_remark", "新增接口用户测试")
				.post(getActionUrl("updateUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		//3.错误的数据访问2，user_name为空格
		bodyString = new HttpClient()
				.addData("user_id", USER_ID)
				.addData("user_name", " ")
				.addData("user_password", "1")
				.addData("user_email", "123@qq.com")
				.addData("user_remark", "新增接口用户测试")
				.post(getActionUrl("updateUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		//4.错误的数据访问3，user_password为空
		bodyString = new HttpClient()
				.addData("user_id", USER_ID)
				.addData("user_name", "新增接口用户测试")
				.addData("user_password", "")
				.addData("user_email", "123@qq.com")
				.addData("user_remark", "新增接口用户测试")
				.post(getActionUrl("updateUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		//5.错误的数据访问4，user_password为空格
		bodyString = new HttpClient()
				.addData("user_id", USER_ID)
				.addData("user_name", "新增接口用户测试")
				.addData("user_password", " ")
				.addData("user_email", "123@qq.com")
				.addData("user_remark", "新增接口用户测试")
				.post(getActionUrl("updateUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		//6.错误的数据访问5，user_email为空
		bodyString = new HttpClient()
				.addData("user_id", USER_ID)
				.addData("user_name", "新增接口用户测试")
				.addData("user_password", "111")
				.addData("user_email", "")
				.addData("user_remark", "新增接口用户测试")
				.post(getActionUrl("updateUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		//7.错误的数据访问6，user_email为空格
		bodyString = new HttpClient()
				.addData("user_id", USER_ID)
				.addData("user_name", "新增接口用户测试")
				.addData("user_password", "111")
				.addData("user_email", " ")
				.addData("user_remark", "新增接口用户测试")
				.post(getActionUrl("updateUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		//8.错误的数据访问7，user_email不合法
		bodyString = new HttpClient()
				.addData("user_id", USER_ID)
				.addData("user_name", "新增接口用户测试")
				.addData("user_password", "111")
				.addData("user_email", "123")
				.addData("user_remark", "新增接口用户测试")
				.post(getActionUrl("updateUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		//9.错误的数据访问8，user_id为空
		bodyString = new HttpClient()
				.addData("user_id", "")
				.addData("user_name", "更新接口用户测试")
				.addData("user_password", "111")
				.addData("user_email", "123@163.com")
				.addData("user_remark", "更新接口用户测试")
				.post(getActionUrl("updateUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		//10.错误的数据访问9，user_id为空格
		bodyString = new HttpClient()
				.addData("user_id", " ")
				.addData("user_name", "更新接口用户测试")
				.addData("user_password", "111")
				.addData("user_email", "123@163.com")
				.addData("user_remark", "更新接口用户测试")
				.post(getActionUrl("updateUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		//9.错误的数据访问8，user_id不存在
		bodyString = new HttpClient()
				.addData("user_id", "111")
				.addData("user_name", "更新接口用户测试")
				.addData("user_password", "111")
				.addData("user_email", "123@163.com")
				.addData("user_remark", "更新接口用户测试")
				.post(getActionUrl("updateUser")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "删除接口用户", logicStep = "1.正确的数据访问1，数据都有效" +
			"2.该方法只有一种情况")
	@Test
	public void deleteUser() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 删除前确定有一条数据
			if (SqlOperator.queryNumber(db, "select count(*) from " + Sys_user.TableName +
					" where user_id=?", USER_ID).orElseThrow(() -> new BusinessException("sql查询错误")) != 1) {
				throw new BusinessException("必须有一条数据要被删除");
			}
			//1.正确的数据访问1，数据都有效
			bodyString = new HttpClient()
					.addData("user_id", USER_ID)
					.post(getActionUrl("deleteUser")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败！"));
			assertThat(ar.isSuccess(), is(true));
			// 确定此条数据已被删除
			if (SqlOperator.queryNumber(db, "select count(*) from " + Sys_user.TableName +
					" where user_id=?", USER_ID).orElseThrow(() -> new BusinessException("sql查询错误")) != 0) {
				throw new BusinessException("此条数据没有被删除");
			}
		}
	}

	@Method(desc = "删除接口用户", logicStep = "1.正确的数据访问1，数据都有效" +
			"2.错误的数据访问1，user_id不存在" +
			"3.该方法只有正确错误两种情况")
	@Test
	public void selectUserById() {
		//1.正确的数据访问1，数据都有效
		bodyString = new HttpClient()
				.addData("user_id", USER_ID)
				.post(getActionUrl("selectUserById")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> dataForMap = ar.getDataForMap();
		assertThat("接口测试用户-dhw0", is(dataForMap.get("user_name")));
		assertThat("123@163.com", is(dataForMap.get("user_email")));
		assertThat("1", is(dataForMap.get("user_password")));
		assertThat("接口测试用户-dhw0", is(dataForMap.get("user_remark")));
		//2.错误的数据访问1，user_id不存在
		bodyString = new HttpClient()
				.addData("user_id", "111")
				.post(getActionUrl("selectUserById")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().size(), is(0));
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

			SqlOperator.commitTransaction(db);
		}
	}
}
