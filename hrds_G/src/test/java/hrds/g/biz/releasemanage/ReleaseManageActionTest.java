package hrds.g.biz.releasemanage;

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
import hrds.commons.entity.Interface_info;
import hrds.commons.entity.Interface_use;
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.noggit.JSONUtil;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "接口发布管理测试类", author = "dhw", createdate = "2020/4/10 11:09")
public class ReleaseManageActionTest extends WebBaseTestCase {

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
			user.setUser_remark("接口测试用户-dhw");
			assertThat("初始化数据成功", user.add(db), is(1));
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

	@Method(desc = "搜索管理用户信息", logicStep = "1.正确的数据访问1，该方法只有一种可能")
	@Test
	public void searchUserInfo() {
		// 1.正确的数据访问1
		String bodyString = new HttpClient().buildSession()
				.post(getActionUrl("searchUserInfo")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		for (int i = 0; i < result.getRowCount(); i++) {
			if (result.getLong(i, "user_id") == THREAD_ID) {
				assertThat(result.getString(i, "user_name"), is("接口测试用户-dhw" + THREAD_ID));
			}
		}
	}

	@Method(desc = "根据接口类型查看接口信息", logicStep = "1.正确的数据访问1，数据正确" +
			"2.错误的数据访问1,interface_type不存在，该方法只有两种可能")
	@Test
	public void searchInterfaceInfoByType() {
		// 1.正确的数据访问1
		String bodyString = new HttpClient().buildSession()
				.addData("interface_type", InterfaceType.ShuJuLei.getCode())
				.post(getActionUrl("searchInterfaceInfoByType")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
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
		Interface_use[] interfaceUses = new Interface_use[2];
		for (int i = 0; i < 2; i++) {
			Interface_use interface_use = new Interface_use();
			if (i == 0) {
				interface_use.setInterface_id(104L);
				interface_use.setInterface_code("01-123");
				interface_use.setInterface_name("表使用权限查询接口");
				interface_use.setUrl("tableUsePermissions");
			} else {
				interface_use.setInterface_id(105L);
				interface_use.setInterface_code("01-124");
				interface_use.setInterface_name("单表普通查询接口");
				interface_use.setUrl("generalQuery");
			}
			interface_use.setStart_use_date(DateUtil.getSysDate());
			interface_use.setUse_valid_date(Constant.MAXDATE);
			interfaceUses[i] = interface_use;
		}
		String bodyString = new HttpClient().buildSession()
				.addData("interfaceUses", JsonUtil.toJson(interfaceUses))
				.addData("userIds", new long[]{USER_ID, THREAD_ID})
				.addData("interface_note", "新增接口使用信息测试")
				.addData("classify_name", "dhw-cs")
				.post(getActionUrl("saveInterfaceUseInfo")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Interface_use> interface_uses = SqlOperator.queryList(db, Interface_use.class,
					"select * from " + Interface_use.TableName
							+ " where user_id in(?,?) order by interface_id",
					USER_ID, THREAD_ID);
			assertThat(interface_uses.size(), is(4));
			for (Interface_use interface_use : interface_uses) {
				Long user_id = interface_use.getUser_id();
				if (user_id == USER_ID) {
					assertThat(interface_use.getUser_id(), is(USER_ID));
				} else {
					assertThat(interface_use.getUser_id(), is(THREAD_ID));
				}
				assertThat(interface_use.getCreate_id(), is(USER_ID));
				assertThat(interface_use.getClassify_name(), is("dhw-cs"));
				assertThat(interface_use.getInterface_note(), is("新增接口使用信息测试"));
				assertThat(interface_use.getStart_use_date(), is(DateUtil.getSysDate()));
				assertThat(interface_use.getUse_valid_date(), is(Constant.MAXDATE));
			}
		}
		// 2.错误的数据访问1，user_id为空
		bodyString = new HttpClient().buildSession()
				.addData("userIds", "")
				.addData("interfaceUses", JSONUtil.toJSON(interfaceUses))
				.addData("interface_note", "新增接口使用信息测试")
				.addData("classify_name", "dhw-cs")
				.post(getActionUrl("saveInterfaceUseInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
		Interface_use[] interfaceUses2 = new Interface_use[2];
		for (int i = 0; i < 2; i++) {
			Interface_use interface_use = new Interface_use();
			if (i == 0) {
				interface_use.setInterface_id(104L);
				interface_use.setInterface_code("01-123");
				interface_use.setInterface_name("表使用权限查询接口");
				interface_use.setUrl("tableUsePermissions");
			} else {
				interface_use.setInterface_code("01-124");
				interface_use.setInterface_name("单表普通查询接口");
				interface_use.setUrl("generalQuery");
			}
			interface_use.setStart_use_date(DateUtil.getSysDate());
			interface_use.setUse_valid_date(Constant.MAXDATE);
			interfaceUses2[i] = interface_use;
		}
		// 3.错误的数据访问2，interface_id为空
		bodyString = new HttpClient().buildSession()
				.addData("interfaceUses", JsonUtil.toJson(interfaceUses2))
				.addData("userIds", new long[]{USER_ID, THREAD_ID})
				.addData("interface_note", "新增接口使用信息测试")
				.addData("classify_name", "dhw-cs")
				.post(getActionUrl("saveInterfaceUseInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，start_use_date为空
		bodyString = new HttpClient().buildSession()
				.addData("userIds", new long[]{USER_ID, THREAD_ID})
				.addData("interfaceUses", JSONUtil.toJSON(interfaceUses))
				.addData("interface_note", "新增接口使用信息测试")
				.addData("classify_name", "dhw-cs")
				.post(getActionUrl("saveInterfaceUseInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问4，use_valid_date为空
		bodyString = new HttpClient().buildSession()
				.addData("userIds", new long[]{USER_ID, THREAD_ID})
				.addData("interfaceUses", JSONUtil.toJSON(interfaceUses))
				.addData("interface_note", "新增接口使用信息测试")
				.addData("classify_name", "dhw-cs")
				.post(getActionUrl("saveInterfaceUseInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
	}

	@After
	public void after() {
		DatabaseWrapper db = new DatabaseWrapper();
		try {
			//1.清理sys_user表中造的数据
			SqlOperator.execute(db, "DELETE FROM " + Sys_user.TableName + " WHERE user_id = ?"
					, THREAD_ID);
			// 2.清理interface_use表数据
			SqlOperator.execute(db, "DELETE FROM " + Interface_use.TableName + " WHERE user_id in(?,?)"
					, USER_ID, USER_ID + 1);
			SqlOperator.commitTransaction(db);
		} catch (Exception e) {
			db.rollback();
		} finally {
			db.close();
		}
	}
}
