package hrds.g.biz.serviceuser;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.netserver.conf.HttpServerConf;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.InterfaceState;
import hrds.commons.codes.InterfaceType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.PropertyParaValue;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "服务接口用户测试类", author = "dhw", createdate = "2020/5/15 13:48")
public class ServiceUserActionTest extends WebBaseTestCase {

	//请填写测试用户需要做登录验证的A项目的登录验证的接口
	private static final String LOGIN_URL = agentInitConfig.getString("login_url");
	// 已经存在的用户ID,用于模拟登录
	private static final long USER_ID = agentInitConfig.getLong("user_id");
	private static final String PASSWORD = agentInitConfig.getString("password");
	//当前线程的id
	private long THREAD_ID = Thread.currentThread().getId() * 1000000;

	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 3.造interface_use表测试数据
			Interface_use interface_use = new Interface_use();
			interface_use.setUse_valid_date(Constant.MAXDATE);
			interface_use.setInterface_use_id(THREAD_ID);
			interface_use.setClassify_name("dhwcs" + THREAD_ID);
			interface_use.setInterface_id(104L);
			interface_use.setInterface_note("接口监控测试");
			interface_use.setUse_state(InterfaceState.JinYong.getCode());
			interface_use.setUser_id(USER_ID);
			interface_use.setCreate_id(USER_ID);
			interface_use.setTheir_type(InterfaceType.ShuJuLei.getCode());
			interface_use.setInterface_code("01-123");
			interface_use.setUrl("tableUsePermissions");
			interface_use.setInterface_name("表使用权限查询接口");
			interface_use.setStart_use_date(DateUtil.getSysDate());
			interface_use.setUser_name("接口测试用户-dhw");
			interface_use.add(db);
			// 4.造table_use_info表测试数据
			Table_use_info table_use_info = new Table_use_info();
			table_use_info.setTable_blsystem(DataSourceType.DCL.getCode());
			table_use_info.setUser_id(USER_ID);
			table_use_info.setTable_note("监控监控表使用信息测试");
			table_use_info.setSysreg_name("fdc01_dhw_test");
			table_use_info.setOriginal_name("dhw_test");
			table_use_info.setUse_id(THREAD_ID);
			table_use_info.add(db);
			// 5.造sysreg_parameter_info表测试数据
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
				sysreg_parameter_info.setRemark("接口测试dhw");
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
			// 1.清理interface_use表数据
			SqlOperator.execute(db,
					"DELETE FROM " + Interface_use.TableName + " WHERE interface_use_id =?", THREAD_ID);
			// 2.清理Table_use_info表数据
			SqlOperator.execute(db,
					"DELETE FROM " + Table_use_info.TableName + " WHERE use_id =?", THREAD_ID);
			// 3.清理Sysreg_parameter_info表数据
			for (int i = 0; i < 2; i++) {
				SqlOperator.execute(db,
						"DELETE FROM " + Sysreg_parameter_info.TableName + " WHERE parameter_id =?",
						THREAD_ID + i);
			}
			SqlOperator.commitTransaction(db);
		} catch (Exception e) {
			db.rollback();
		} finally {
			db.close();
		}
	}

	@Method(desc = "查询接口信息", logicStep = "1.正确的数据访问1,数据都有效" +
			"2.正确的数据访问2,interface_name不为空" +
			"3.正确的数据访问3,interface_name为空" +
			"4.正确的数据访问4,interface_name不存在")
	@Test
	public void searchInterfaceInfo() {
		// 1.正确的数据访问1,数据都有效
		String bodyString = new HttpClient().buildSession()
				.post(getActionUrl("searchInterfaceInfo")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		assertThat(result.getLong(0, "interface_use_id"), is(THREAD_ID));
		assertThat(result.getString(0, "interface_name"), is("表使用权限查询接口"));
		assertThat(result.getString(0, "url"), is("tableUsePermissions"));
		assertThat(result.getString(0, "start_use_date"), is(DateUtil.getSysDate()));
		assertThat(result.getString(0, "use_valid_date"), is(Constant.MAXDATE));
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
		String bodyString = new HttpClient().buildSession()
				.post(getActionUrl("searchDataTableInfo")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		assertThat(result.getLong(0, "use_id"), is(THREAD_ID));
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
		String bodyString = new HttpClient().buildSession()
				.addData("use_id", THREAD_ID)
				.post(getActionUrl("searchColumnInfoById")).getBodyString();
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
		assertThat(ar.getDataForEntityList(Sysreg_parameter_info.class).isEmpty(), is(true));
	}

	@Method(desc = "获取当前用户请求ip端口", logicStep = "1.正确的数据访问1,数据都有效" +
			"备注：该方法只有一种可能")
	@Test
	public void getIpAndPort() {
		// 1.正确的数据访问1,数据都有效
		String bodyString = new HttpClient().buildSession()
				.addData("use_id", THREAD_ID)
				.post(getActionUrl("getIpAndPort")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getData().toString(), is(PropertyParaValue.getString("hyren_host", "127.0.0.1")
				+ ":" + HttpServerConf.getHttpServer().getHttpPort()));
	}
}