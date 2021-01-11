package hrds.l.biz.autoanalysis.manage;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.AutoTemplateStatus;
import hrds.commons.codes.AutoValueType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Auto_tp_cond_info;
import hrds.commons.entity.Auto_tp_info;
import hrds.commons.entity.Auto_tp_res_set;
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.ParallerTestUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.l.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "自主取数管理测试类", author = "dhw", createdate = "2021/1/11 9:57")
public class ManageActionTest extends WebBaseTestCase {
	//请填写测试用户需要做登录验证的A项目的登录验证的接口
	private static final String LOGIN_URL = ParallerTestUtil.TESTINITCONFIG.getString("login_url");
	// 已经存在的用户ID,用于模拟登录
	private static final long SYS_USER_ID = ParallerTestUtil.TESTINITCONFIG.getLong("user_id");
	private static final String PASSWORD = ParallerTestUtil.TESTINITCONFIG.getString("password");
	// 已经存在的部门ID
	private static final String DEP_ID = ParallerTestUtil.TESTINITCONFIG.getString("dep_id");
	//当前线程的id
	private long THREAD_ID = Thread.currentThread().getId() * 1000000;
	// 模板信息表主键ID
	private long TEMPLATE_ID = PrimayKeyGener.getNextId() + THREAD_ID;
	// 测试用户
	private long TEST_USER_ID = SYS_USER_ID + THREAD_ID;
	private String TEMPLATE_SQL = "SELECT i_item_sk,i_item_id,i_item_desc FROM dhw_tpcds_item " +
			" WHERE i_current_price BETWEEN 76 AND 76 + 30 and i_item_sk > 0";

	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.初始化用户信息
			//构建用户登录信息
			Sys_user sys_user = new Sys_user();
			sys_user.setUser_id(TEST_USER_ID);
			sys_user.setCreate_id(TEST_USER_ID);
			sys_user.setDep_id(DEP_ID);
			sys_user.setRole_id("1001");
			sys_user.setUser_name("自主取数测试用户");
			sys_user.setUser_password(PASSWORD);
			// 0：管理员，1：操作员
			sys_user.setUseris_admin(IsFlag.Fou.getCode());
			sys_user.setUser_type("00");
			sys_user.setUsertype_group(null);
			sys_user.setLogin_ip("127.0.0.1");
			sys_user.setLogin_date(DateUtil.getSysDate());
			sys_user.setUser_state("1");
			sys_user.setCreate_date(DateUtil.getSysDate());
			sys_user.setCreate_time(DateUtil.getSysTime());
			sys_user.setUpdate_date(DateUtil.getSysDate());
			sys_user.setUpdate_time(DateUtil.getSysTime());
			assertThat("初始化自主取数测试用户信息成功", sys_user.add(db), is(1));
			// 3.初始化Auto_tp_info表测试数据
			List<Auto_tp_info> autoTpInfoList = getAuto_tp_infos();
			autoTpInfoList.forEach(autoTpInfo -> assertThat(Auto_tp_info.TableName + "表初始化测试数据成功",
					autoTpInfo.add(db), is(1)));
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
		// 模拟用户登录
		String responseValue = new HttpClient().buildSession()
				.addData("user_id", TEST_USER_ID)
				.addData("password", PASSWORD)
				.post(LOGIN_URL)
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
	}

	private List<Auto_tp_info> getAuto_tp_infos() {
		List<Auto_tp_info> autoTpInfoList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			Auto_tp_info auto_tp_info = new Auto_tp_info();
			auto_tp_info.setTemplate_id(TEMPLATE_ID + i);
			if (i == 0) {
				auto_tp_info.setTemplate_status(AutoTemplateStatus.BianJi.getCode());
			} else if (i == 1) {
				auto_tp_info.setTemplate_status(AutoTemplateStatus.FaBu.getCode());
			} else {
				auto_tp_info.setTemplate_status(AutoTemplateStatus.ZhuXiao.getCode());
			}
			auto_tp_info.setTemplate_name("自主取数模板测试" + i + THREAD_ID);
			auto_tp_info.setCreate_date(DateUtil.getSysDate());
			auto_tp_info.setCreate_time(DateUtil.getSysTime());
			auto_tp_info.setData_source(IsFlag.Shi.getCode());
			auto_tp_info.setCreate_user(TEST_USER_ID);
			auto_tp_info.setTemplate_desc("自主取数模板测试");
			auto_tp_info.setTemplate_sql(TEMPLATE_SQL);
			autoTpInfoList.add(auto_tp_info);
		}
		return autoTpInfoList;
	}

	@Method(desc = "获取自主分析模板配置信息", logicStep = "1.正确的数据访问1" +
			"备注：只有一种可能因为没有条件")
	@Test
	public void getTemplateConfInfo() {
		// 1.正确的数据访问1
		String bodyString = new HttpClient()
				.post(getActionUrl("getTemplateConfInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		checkAutoTpInfo(ar);
	}

	private void checkAutoTpInfo(ActionResult ar) {
		List<Auto_tp_info> autoTpInfos = ar.getDataForEntityList(Auto_tp_info.class);
		for (Auto_tp_info auto_tp_info : autoTpInfos) {
			if (auto_tp_info.getTemplate_id() == TEMPLATE_ID) {
				assertThat(auto_tp_info.getTemplate_status(), is(AutoTemplateStatus.BianJi.getCode()));
				assertThat(auto_tp_info.getTemplate_name(), is("自主取数模板测试" + 0 + THREAD_ID));
				checkAutoTpInfoParam(auto_tp_info);
			} else if (auto_tp_info.getTemplate_id() == TEMPLATE_ID + 1) {
				assertThat(auto_tp_info.getTemplate_status(), is(AutoTemplateStatus.FaBu.getCode()));
				assertThat(auto_tp_info.getTemplate_name(), is("自主取数模板测试" + 1 + THREAD_ID));
				checkAutoTpInfoParam(auto_tp_info);
			}
		}
	}

	private void checkAutoTpInfoParam(Auto_tp_info auto_tp_info) {
		assertThat(auto_tp_info.getTemplate_desc(), is("自主取数模板测试"));
		assertThat(auto_tp_info.getData_source(), is(IsFlag.Shi.getCode()));
		assertThat(auto_tp_info.getCreate_user(), is(TEST_USER_ID));
		assertThat(auto_tp_info.getTemplate_sql(), is(TEMPLATE_SQL));
	}

	@Method(desc = "根据模板名称获取自主分析模板配置信息", logicStep = "1.正确的数据访问1，模板名称存在" +
			"2.正确的数据访问2，模板名称不存在" +
			"备注：只有两种情况")
	@Test
	public void getTemplateConfInfoByName() {
		// 1.正确的数据访问1，模板名称存在
		String bodyString = new HttpClient()
				.addData("template_name", "自主取数模板测试")
				.post(getActionUrl("getTemplateConfInfoByName"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		checkAutoTpInfo(ar);
		// 2.正确的数据访问2，模板名称不存在
		bodyString = new HttpClient()
				.addData("template_name", "-dhwcs")
				.post(getActionUrl("getTemplateConfInfoByName"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		List<Auto_tp_info> auto_tp_infos = ar.getDataForEntityList(Auto_tp_info.class);
		assertThat(auto_tp_infos.size(), is(0));


	}

	@Method(desc = "获取自主取数树数据", logicStep = "1.正确的数据访问1" +
			"备注：只有一种情况")
	@Test
	public void getAutoAnalysisTreeData() {
		// 1.正确的数据访问1
		String bodyString = new HttpClient()
				.post(getActionUrl("getAutoAnalysisTreeData"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
	}

	@Method(desc = "校验sql", logicStep = "1.正确的数据访问1，sql正确" +
			"2.错误的数据访问1，sql不正确" +
			"备注：只有两种情况")
	@Test
	public void verifySqlIsLegal() {
		// 1.正确的数据访问1，sql正确
		String bodyString = new HttpClient().addData("template_sql", TEMPLATE_SQL)
				.post(getActionUrl("verifySqlIsLegal"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		// 2.错误的数据访问1，sql不正确
		bodyString = new HttpClient().addData("template_sql", "select aaa from bbb")
				.post(getActionUrl("verifySqlIsLegal"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "生成模板参数", logicStep = "1.正确的数据访问1，sql正确" +
			"2.错误的数据访问1，sql不正确")
	@Test
	public void generateTemplateParam() {
		// 1.正确的数据访问1，sql正确
		String bodyString = new HttpClient().addData("template_sql", TEMPLATE_SQL)
				.post(getActionUrl("generateTemplateParam"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Map<String, Object> sqlParamMap = ar.getDataForMap();
		List<Auto_tp_cond_info> autoTpCondInfoList =
				JsonUtil.toObject(sqlParamMap.get("autoTpCondInfo").toString(),
						new TypeReference<List<Auto_tp_cond_info>>() {
						}.getType());
		checkAutoTpCondInfo(autoTpCondInfoList);
		List<Auto_tp_res_set> autoTpResSetList =
				JsonUtil.toObject(sqlParamMap.get("autoTpResSetInfo").toString(),
						new TypeReference<List<Auto_tp_res_set>>() {
						}.getType());
		checkAutoTpResSet(autoTpResSetList);
		// 2.错误的数据访问1，sql不正确
		bodyString = new HttpClient().addData("template_sql", "select aaa from bbb")
				.post(getActionUrl("generateTemplateParam"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
	}

	private void checkAutoTpResSet(List<Auto_tp_res_set> autoTpResSetList) {
		for (Auto_tp_res_set auto_tp_res_set : autoTpResSetList) {
			String column_en_name = auto_tp_res_set.getColumn_en_name();
			switch (column_en_name) {
				case "i_item_id":
					assertThat(auto_tp_res_set.getColumn_cn_name(), is("商品标识"));
					assertThat(auto_tp_res_set.getRes_show_column(), is("i_item_id"));
					assertThat(auto_tp_res_set.getSource_table_name(), is("dhw_tpcds_item"));
					break;
				case "i_item_desc":
					assertThat(auto_tp_res_set.getColumn_cn_name(), is("商品描述"));
					assertThat(auto_tp_res_set.getRes_show_column(), is("i_item_desc"));
					assertThat(auto_tp_res_set.getSource_table_name(), is("dhw_tpcds_item"));
					break;
				case "i_item_sk":
					assertThat(auto_tp_res_set.getColumn_cn_name(), is("商品编号"));
					assertThat(auto_tp_res_set.getRes_show_column(), is("i_item_sk"));
					assertThat(auto_tp_res_set.getSource_table_name(), is("dhw_tpcds_item"));
					break;
			}
		}
	}

	private void checkAutoTpCondInfo(List<Auto_tp_cond_info> autoTpCondInfoList) {
		for (Auto_tp_cond_info autoTpCondInfo : autoTpCondInfoList) {
			String cond_para_name = autoTpCondInfo.getCond_para_name();
			if (cond_para_name.equals("i_item_sk")) {
				assertThat(autoTpCondInfo.getCond_en_column(), is("i_item_sk"));
				assertThat(autoTpCondInfo.getValue_type(), is(AutoValueType.ZiFuChuan.getCode()));
				assertThat(autoTpCondInfo.getCond_cn_column(), is("i_item_sk"));
				assertThat(autoTpCondInfo.getPre_value(), is("0"));
				assertThat(autoTpCondInfo.getIs_required(), is(IsFlag.Shi.getCode()));
				assertThat(autoTpCondInfo.getCon_relation(), is(">"));
				assertThat(autoTpCondInfo.getValue_size(), is("64"));
			} else if (cond_para_name.equals("i_current_price")) {
				assertThat(autoTpCondInfo.getCond_en_column(), is("i_current_price"));
				assertThat(autoTpCondInfo.getValue_type(), is(AutoValueType.ZiFuChuan.getCode()));
				assertThat(autoTpCondInfo.getCond_cn_column(), is("i_current_price"));
				assertThat(autoTpCondInfo.getPre_value(), is("76,76 + 30"));
				assertThat(autoTpCondInfo.getIs_required(), is(IsFlag.Shi.getCode()));
				assertThat(autoTpCondInfo.getCon_relation(), is("BETWEEN"));
				assertThat(autoTpCondInfo.getValue_size(), is("64"));
			}
		}
	}

	@Method(desc = "保存模板配置页面的信息包括模板内容,条件参数和结果设置", logicStep = "1.正确的数据访问1,数据都有效")
	@Test
	public void saveTemplateConfInfo() {
		// 1.正确的数据访问1,数据都有效
		List<Auto_tp_cond_info> autoTpCondInfos = getAuto_tp_cond_infos();
		List<Auto_tp_res_set> autoTpResSets = getAuto_tp_res_sets();
		Auto_tp_info auto_tp_info = new Auto_tp_info();
		auto_tp_info.setTemplate_name("新增自主取数模板测试" + THREAD_ID);
		auto_tp_info.setCreate_date(DateUtil.getSysDate());
		auto_tp_info.setCreate_time(DateUtil.getSysTime());
		auto_tp_info.setData_source(IsFlag.Shi.getCode());
		auto_tp_info.setCreate_user(TEST_USER_ID);
		auto_tp_info.setTemplate_desc("自主取数模板测试");
		auto_tp_info.setTemplate_sql(TEMPLATE_SQL);
		String bodyString = new HttpClient()
				.addData("autoTpCondInfos", JsonUtil.toJson(autoTpCondInfos))
				.addData("autoTpResSets", JsonUtil.toJson(autoTpResSets))
				.addData("auto_tp_info", auto_tp_info)
				.post(getActionUrl("saveTemplateConfInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Auto_tp_info autoTpInfo = SqlOperator.queryOneObject(db, Auto_tp_info.class,
					"select * from " + Auto_tp_info.TableName + " where template_name=?",
					auto_tp_info.getTemplate_name())
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(autoTpInfo.getTemplate_desc(), is(auto_tp_info.getTemplate_desc()));
			assertThat(autoTpInfo.getTemplate_sql(), is(auto_tp_info.getTemplate_sql()));
			assertThat(autoTpInfo.getData_source(), is(auto_tp_info.getData_source()));
			assertThat(autoTpInfo.getCreate_user(), is(auto_tp_info.getCreate_user()));
			for (Auto_tp_cond_info autoTpCondInfo : getAuto_tp_cond_infos()) {
				Auto_tp_cond_info auto_tp_cond_info = SqlOperator.queryOneObject(db, Auto_tp_cond_info.class,
						"select * from " + Auto_tp_cond_info.TableName + " where cond_para_name=?",
						autoTpCondInfo.getCond_para_name())
						.orElseThrow(() -> new BusinessException("sql查询错误"));
				assertThat(autoTpCondInfo.getCond_para_name(), is(auto_tp_cond_info.getCond_para_name()));
				assertThat(autoTpCondInfo.getValue_size(), is(auto_tp_cond_info.getValue_size()));
				assertThat(autoTpCondInfo.getCon_relation(), is(auto_tp_cond_info.getCon_relation()));
				assertThat(autoTpCondInfo.getCond_cn_column(), is(auto_tp_cond_info.getCond_cn_column()));
				assertThat(autoTpCondInfo.getCond_en_column(), is(auto_tp_cond_info.getCond_en_column()));
				assertThat(autoTpCondInfo.getIs_required(), is(auto_tp_cond_info.getIs_required()));
				assertThat(autoTpCondInfo.getPre_value(), is(auto_tp_cond_info.getPre_value()));
			}
			for (Auto_tp_res_set autoTpResSet : getAuto_tp_res_sets()) {
				Auto_tp_res_set auto_tp_res_set = SqlOperator.queryOneObject(db, Auto_tp_res_set.class,
						"select * from " + Auto_tp_res_set.TableName + " where column_en_name=?",
						autoTpResSet.getColumn_en_name())
						.orElseThrow(() -> new BusinessException("sql查询错误"));
				assertThat(autoTpResSet.getSource_table_name(), is(auto_tp_res_set.getSource_table_name()));
				assertThat(autoTpResSet.getRes_show_column(), is(auto_tp_res_set.getRes_show_column()));
				assertThat(autoTpResSet.getColumn_cn_name(), is(auto_tp_res_set.getColumn_cn_name()));
				assertThat(autoTpResSet.getColumn_en_name(), is(auto_tp_res_set.getColumn_en_name()));
				assertThat(autoTpResSet.getColumn_type(), is(auto_tp_res_set.getColumn_type()));
			}
		}

	}

	private List<Auto_tp_res_set> getAuto_tp_res_sets() {
		List<Auto_tp_res_set> autoTpResSets = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			Auto_tp_res_set auto_tp_res_set = new Auto_tp_res_set();
			auto_tp_res_set.setSource_table_name("dhw_tpcds_item");
			if (i == 0) {
				auto_tp_res_set.setColumn_en_name("i_item_id");
				auto_tp_res_set.setColumn_cn_name("商品标识");
				auto_tp_res_set.setRes_show_column("i_item_id");
				auto_tp_res_set.setColumn_type(AutoValueType.ZiFuChuan.getCode());
			} else if (i == 1) {
				auto_tp_res_set.setColumn_en_name("i_item_sk");
				auto_tp_res_set.setColumn_cn_name("商品编号");
				auto_tp_res_set.setRes_show_column("i_item_sk");
				auto_tp_res_set.setColumn_type(AutoValueType.ShuZhi.getCode());
			} else {
				auto_tp_res_set.setColumn_en_name("i_item_desc");
				auto_tp_res_set.setColumn_cn_name("商品描述");
				auto_tp_res_set.setRes_show_column("i_item_desc");
				auto_tp_res_set.setColumn_type(AutoValueType.ZiFuChuan.getCode());
			}
			autoTpResSets.add(auto_tp_res_set);
		}
		return autoTpResSets;
	}

	private List<Auto_tp_cond_info> getAuto_tp_cond_infos() {
		List<Auto_tp_cond_info> autoTpCondInfoList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Auto_tp_cond_info autoTpCondInfo = new Auto_tp_cond_info();
			autoTpCondInfo.setValue_size("64");
			autoTpCondInfo.setIs_required(IsFlag.Shi.getCode());
			autoTpCondInfo.setValue_type(AutoValueType.ZiFuChuan.getCode());
			if (i == 0) {
				autoTpCondInfo.setCond_cn_column("i_item_id");
				autoTpCondInfo.setCond_en_column("i_item_id");
				autoTpCondInfo.setPre_value("0");
				autoTpCondInfo.setCond_para_name("i_item_id");
				autoTpCondInfo.setCon_relation(">");
			} else {
				autoTpCondInfo.setCond_cn_column("i_current_price");
				autoTpCondInfo.setCond_en_column("i_current_price");
				autoTpCondInfo.setPre_value("76,76 + 30");
				autoTpCondInfo.setCond_para_name("i_current_price");
				autoTpCondInfo.setCon_relation("BETWEEN");
			}
			autoTpCondInfoList.add(autoTpCondInfo);
		}
		return autoTpCondInfoList;
	}

	@Method(desc = "根据模板ID获取自主取数模板配置信息", logicStep = "1.正确的数据访问1，模板ID存在" +
			"2.正确的数据访问2，模板ID不存在")
	@Test
	public void getAutoTpInfoById() {
		// 1.正确的数据访问1，模板ID存在
		String bodyString = new HttpClient().addData("template_id", TEMPLATE_ID)
				.post(getActionUrl("getAutoTpInfoById"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Auto_tp_info auto_tp_info = JsonUtil.toObjectSafety(JsonUtil.toJson(ar.getDataForMap()),
				Auto_tp_info.class).orElseThrow(() -> new BusinessException("转换实体失败"));
		assertThat(auto_tp_info.getTemplate_status(), is(AutoTemplateStatus.BianJi.getCode()));
		assertThat(auto_tp_info.getTemplate_name(), is("自主取数模板测试" + 0 + THREAD_ID));
		checkAutoTpInfoParam(auto_tp_info);
		// 2.正确的数据访问2，模板ID不存在
		bodyString = new HttpClient().addData("template_id", "-111")
				.post(getActionUrl("getAutoTpInfoById"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().isEmpty(), is(true));
	}

	@Method(desc = "", logicStep = "")
	@Test
	public void getAutoTpCondInfoById() {
	}

	@Test
	public void getAutoTpResSetById() {
	}

	@Test
	public void updateTemplateConfInfo() {
	}

	@Method(desc = "数据预览", logicStep = "1.正确的数据访问1，showNum为空" +
			"2.正确的数据访问2，showNum不为空")
	@Test
	public void getPreviewData() {
		// 1.正确的数据访问1，showNum为空
		String bodyString = new HttpClient()
				.addData("template_sql", TEMPLATE_SQL)
				.post(getActionUrl("getPreviewData"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		// 2.正确的数据访问2，showNum不为空
		bodyString = new HttpClient()
				.addData("template_sql", TEMPLATE_SQL)
				.addData("showNum", 100)
				.post(getActionUrl("getPreviewData"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		List<Map> data = ar.getDataForEntityList(Map.class);
		assertThat(data.size() <= 100, is(true));
	}

	@Method(desc = "发布自主取数模板", logicStep = "1.正确的数据访问1，模板ID存在" +
			"2.错误的数据访问1，模板ID不存在")
	@Test
	public void releaseAutoAnalysisTemplate() {
		// 1.正确的数据访问1，模板ID存在
		String bodyString = new HttpClient()
				.addData("template_id", TEMPLATE_ID)
				.post(getActionUrl("releaseAutoAnalysisTemplate"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Auto_tp_info auto_tp_info = SqlOperator.queryOneObject(db, Auto_tp_info.class,
					"select * from " + Auto_tp_info.TableName + " where template_id=?"
					, TEMPLATE_ID).orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(auto_tp_info.getTemplate_status(), is(AutoTemplateStatus.FaBu.getCode()));
		}
		// 2.错误的数据访问1，模板ID不存在
		bodyString = new HttpClient()
				.addData("template_id", "-111")
				.post(getActionUrl("releaseAutoAnalysisTemplate"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "删除自主取数模板", logicStep = "1.正确的数据访问1，模板ID存在" +
			"2.错误的数据访问1，模板ID不存在")
	@Test
	public void deleteAutoAnalysisTemplate() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正确的数据访问1，模板ID存在
			String bodyString = new HttpClient()
					.addData("template_id", TEMPLATE_ID)
					.post(getActionUrl("deleteAutoAnalysisTemplate"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败"));
			assertThat(ar.isSuccess(), is(true));
			Auto_tp_info auto_tp_info = SqlOperator.queryOneObject(db, Auto_tp_info.class,
					"select * from " + Auto_tp_info.TableName + " where template_id=?"
					, TEMPLATE_ID).orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(auto_tp_info.getTemplate_status(), is(AutoTemplateStatus.ZhuXiao.getCode()));
			// 2.错误的数据访问1，模板ID不存在
			bodyString = new HttpClient()
					.addData("template_id", "-111")
					.post(getActionUrl("releaseAutoAnalysisTemplate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@After
	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.删除sys_user测试数据
			SqlOperator.execute(db,
					"delete from " + Sys_user.TableName + " where user_id=?", TEST_USER_ID);
			long num = SqlOperator.queryNumber(db,
					"select count(*) from " + Sys_user.TableName + " where user_id =?",
					TEST_USER_ID)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(0L));
			// 2.删除auto_tp_info表测试数据
			getAuto_tp_infos().forEach(auto_tp_info ->
					SqlOperator.execute(db,
							"delete from " + Auto_tp_info.TableName + " where template_id=?",
							auto_tp_info.getTemplate_id()));
			num = SqlOperator.queryNumber(db,
					"select count(*) from " + Auto_tp_info.TableName + " where template_id in (?,?,?)",
					TEMPLATE_ID, TEMPLATE_ID + 1, TEMPLATE_ID + 2)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(0L));
			// 删除新增模板测试数据
			SqlOperator.execute(db, "delete from " + Auto_tp_info.TableName + " where template_name=?",
					"新增自主取数模板测试" + THREAD_ID);
			num = SqlOperator.queryNumber(db,
					"select count(*) from " + Auto_tp_info.TableName + " where template_name =?",
					"新增自主取数模板测试" + THREAD_ID)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(0L));
			SqlOperator.execute(db,
					"delete from " + Auto_tp_cond_info.TableName + " where cond_para_name in(?,?)",
					"i_item_id", "i_current_price");
			num = SqlOperator.queryNumber(db,
					"select count(*) from " + Auto_tp_cond_info.TableName + " where cond_para_name in(?,?)",
					"i_item_id", "i_current_price")
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(0L));
			SqlOperator.execute(db,
					"delete from " + Auto_tp_res_set.TableName + " where column_en_name in(?,?,?)",
					"i_item_id", "i_item_sk", "i_item_desc");
			num = SqlOperator.queryNumber(db,
					"select count(*) from " + Auto_tp_res_set.TableName + " where column_en_name in(?,?,?)",
					"i_item_id", "i_item_sk", "i_item_desc")
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(0L));
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
	}
}