package hrds.g.biz.releasemanage;

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
import hrds.commons.codes.InterfaceType;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.UserState;
import hrds.commons.codes.UserType;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Interface_info;
import hrds.commons.entity.Interface_use;
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.g.biz.bean.InterfaceUseInfo;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.noggit.JSONUtil;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "接口发布管理测试类", author = "dhw", createdate = "2020/4/10 11:09")
public class ReleaseManageActionTest extends WebBaseTestCase {

	private static final String SYSDATE = DateUtil.getSysDate();
	private static final String ENDATE = "20991231";
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
			// 3.造
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

	@Method(desc = "搜索管理用户信息", logicStep = "1.正确的数据访问1，该方法只有一种可能")
	@Test
	public void searchUserInfo() {
		// 1.正确的数据访问1
		bodyString = new HttpClient().buildSession()
				.post(getActionUrl("searchUserInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		for (int i = 0; i < result.getRowCount(); i++) {
			long user_id = result.getLong(i, "user_id");
			if (user_id == USER_ID + i) {
				assertThat(result.getString(i, "user_name"), is("接口测试用户-dhw" + i));
			}
		}
	}

	@Method(desc = "根据接口类型查看接口信息", logicStep = "1.正确的数据访问1，数据正确" +
			"2.错误的数据访问1,interface_type不存在，该方法只有两种可能")
	@Test
	public void searchInterfaceInfoByType() {
		// 1.正确的数据访问1
		bodyString = new HttpClient().buildSession()
				.addData("interface_type", InterfaceType.ShuJuLei.getCode())
				.post(getActionUrl("searchInterfaceInfoByType")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		List<Interface_info> interfaceInfos = ar.getDataForEntityList(Interface_info.class);
		assertThat(interfaceInfos.get(0).getInterface_code(), is("01-123"));
		assertThat(interfaceInfos.get(0).getInterface_name(), is("表使用权限查询接口"));
		assertThat(interfaceInfos.get(0).getUrl(), is("tableUsePermissions"));
		assertThat(interfaceInfos.get(0).getInterface_state(), is(IsFlag.Shi.getCode()));
		assertThat(interfaceInfos.get(0).getUser_id(), is(1001L));
		// 2.错误的数据访问1,interface_type不存在
		bodyString = new HttpClient().buildSession()
				.addData("interface_type", "100")
				.post(getActionUrl("viewInterfaceTypeInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "根据接口类型查看接口信息", logicStep = "1.正确的数据访问1，数据正确" +
			"2.错误的数据访问1，user_id为空" +
			"3.错误的数据访问2，interface_id为空" +
			"4.错误的数据访问3，start_use_date为空" +
			"5.错误的数据访问4，use_valid_date为空")
	@Test
	public void saveInterfaceUseInfo() {
		// 1.正确的数据访问，数据正确
		InterfaceUseInfo[] interfaceUseInfos = new InterfaceUseInfo[3];
		InterfaceUseInfo interfaceUseInfo = new InterfaceUseInfo();
		for (int i = 0; i < interfaceUseInfos.length; i++) {
			interfaceUseInfo.setInterface_id((long) (103 + i));
			interfaceUseInfo.setStart_use_date(SYSDATE);
			interfaceUseInfo.setUse_valid_date(ENDATE);
			interfaceUseInfos[i] = interfaceUseInfo;
		}
		bodyString = new HttpClient().buildSession()
				.addJson(JsonUtil.toJson(interfaceUseInfos))
				.addData("user_id", new long[]{USER_ID, USER_ID + 1})
				.addData("interface_note", "新增接口使用信息测试")
				.addData("classify_name", "dhw-cs")
				.post(getActionUrl("saveInterfaceUseInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Interface_use> interface_uses = SqlOperator.queryList(db, Interface_use.class,
					"select * from " + Interface_use.TableName + " where user_id in(?,?) " +
							" order by interface_id",
					USER_ID, USER_ID + 1);
			assertThat(interface_uses.size(), is(16));
			for (Interface_use interface_use : interface_uses) {
				Long user_id = interface_use.getUser_id();
				if (user_id == USER_ID) {
					assertThat(interface_use.getUser_id(), is(USER_ID));
				} else {
					assertThat(interface_use.getUser_id(), is(USER_ID + 1));
				}
				assertThat(interface_use.getCreate_id(), is(USER_ID));
				assertThat(interface_use.getClassify_name(), is("dhw-cs"));
				assertThat(interface_use.getInterface_note(), is("新增接口使用信息测试"));
				assertThat(interface_use.getStart_use_date(), is(SYSDATE));
				assertThat(interface_use.getUse_valid_date(), is(ENDATE));
			}
		}
		// 2.错误的数据访问1，user_id为空
		bodyString = new HttpClient().buildSession()
				.addData("user_id", "")
				.addData("interfaceUseInfos", JSONUtil.toJSON(interfaceUseInfos))
				.addData("interface_note", "新增接口使用信息测试")
				.addData("classify_name", "dhw-cs")
				.post(getActionUrl("saveInterfaceUseInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，interface_id为空
		bodyString = new HttpClient().buildSession()
				.addData("user_id", new long[]{USER_ID, USER_ID + 1})
				.addData("interfaceUseInfos", JSONUtil.toJSON(interfaceUseInfos))
				.addData("interface_note", "新增接口使用信息测试")
				.addData("classify_name", "dhw-cs")
				.post(getActionUrl("saveInterfaceUseInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，start_use_date为空
		bodyString = new HttpClient().buildSession()
				.addData("user_id", new long[]{USER_ID, USER_ID + 1})
				.addData("interfaceUseInfos", JSONUtil.toJSON(interfaceUseInfos))
				.addData("interface_note", "新增接口使用信息测试")
				.addData("start_use_date", "")
				.addData("classify_name", "dhw-cs")
				.post(getActionUrl("saveInterfaceUseInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问4，use_valid_date为空
		bodyString = new HttpClient().buildSession()
				.addData("user_id", new long[]{USER_ID, USER_ID + 1})
				.addData("interfaceUseInfos", JSONUtil.toJSON(interfaceUseInfos))
				.addData("interface_note", "新增接口使用信息测试")
				.addData("classify_name", "dhw-cs")
				.post(getActionUrl("saveInterfaceUseInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
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
			SqlOperator.execute(db, "DELETE FROM " + Interface_use.TableName + " WHERE user_id in(?,?)"
					, USER_ID, USER_ID + 1);
			SqlOperator.commitTransaction(db);
		}
	}
}
