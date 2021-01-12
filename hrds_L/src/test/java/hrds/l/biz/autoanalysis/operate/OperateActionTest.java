package hrds.l.biz.autoanalysis.operate;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.AutoFetchStatus;
import hrds.commons.codes.AutoTemplateStatus;
import hrds.commons.codes.AutoValueType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
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
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "自主取数操作测试类", author = "dhw", createdate = "2021/1/12 14:57")
public class OperateActionTest extends WebBaseTestCase {

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
	private String TEMPLATE_SQL = "SELECT I_ITEM_SK,I_ITEM_ID,I_ITEM_DESC FROM DHW_TPCDS_ITEM"
			+ " WHERE I_CURRENT_PRICE BETWEEN 76 AND 76 + 30 AND I_ITEM_SK > 0";
	private String FETCH_SQL = "SELECT I_ITEM_SK,I_ITEM_ID,I_ITEM_DESC" +
			" FROM (SELECT * FROM DHW_TPCDS_ITEM WHERE I_CURRENT_PRICE BETWEEN 76 AND 76 + 30"
			+ "AND I_ITEM_SK > 0)  TEMP_TABLE ";
	private long FETCH_SUM_ID = PrimayKeyGener.getNextId();

	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.初始化用户信息
			Sys_user sys_user = getSys_user();
			assertThat("初始化自主取数测试用户信息成功", sys_user.add(db), is(1));
			// 2.初始化auto_tp_info表测试数据
			List<Auto_tp_info> autoTpInfoList = getAuto_tp_infos();
			autoTpInfoList.forEach(auto_tp_info ->
					assertThat(Auto_tp_info.TableName + "表测试数据初始化成功",
							auto_tp_info.add(db), is(1)));
			// 3.初始化auto_fetch_sum表测试数据
			List<Auto_fetch_sum> auto_fetch_sums = getAuto_fetch_sums();
			auto_fetch_sums.forEach(auto_fetch_sum ->
					assertThat(Auto_fetch_sum.TableName + "表测试数据初始化成功",
							auto_fetch_sum.add(db), is(1)));
			// 4.初始化auto_tp_res_set表测试数据
			List<Auto_tp_res_set> auto_tp_res_sets = getAuto_tp_res_sets();
			auto_tp_res_sets.forEach(auto_tp_res_set -> assertThat(Auto_tp_res_set.TableName +
					"表初始化测试数据成功", auto_tp_res_set.add(db), is(1)));
			// 5.初始化auto_tp_cond_info表测试数据
			List<Auto_tp_cond_info> auto_tp_cond_infos = getAuto_tp_cond_infos();
			auto_tp_cond_infos.forEach(auto_tp_cond_info -> assertThat(Auto_tp_cond_info.TableName +
					"表初始化测试数据成功", auto_tp_cond_info.add(db), is(1)));
			// 6.初始化auto_fetch_cond表测试数据
			List<Auto_fetch_cond> auto_fetch_conds = getAuto_fetch_conds();
			auto_fetch_conds.forEach(auto_fetch_cond -> assertThat(Auto_fetch_cond.TableName +
					"表初始化测试数据成功", auto_fetch_cond.add(db), is(1)));
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

	private List<Auto_fetch_cond> getAuto_fetch_conds() {
		List<Auto_fetch_cond> auto_fetch_conds = new ArrayList<>();
		for (Auto_tp_cond_info auto_tp_cond_info : getAuto_tp_cond_infos()) {
			Auto_fetch_cond auto_fetch_cond = new Auto_fetch_cond();
			auto_fetch_cond.setFetch_sum_id(FETCH_SUM_ID);
			auto_fetch_cond.setTemplate_cond_id(auto_tp_cond_info.getTemplate_cond_id());
			auto_fetch_cond.setFetch_cond_id(PrimayKeyGener.getNextId());
			auto_fetch_cond.setCond_value(auto_tp_cond_info.getPre_value());
			auto_fetch_conds.add(auto_fetch_cond);
		}
		return auto_fetch_conds;
	}

	private List<Auto_tp_cond_info> getAuto_tp_cond_infos() {
		List<Auto_tp_cond_info> autoTpCondInfoList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Auto_tp_cond_info autoTpCondInfo = new Auto_tp_cond_info();
			autoTpCondInfo.setTemplate_cond_id(PrimayKeyGener.getNextId());
			autoTpCondInfo.setTemplate_id(TEMPLATE_ID);
			autoTpCondInfo.setIs_dept_id(IsFlag.Fou.getCode());
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

	private List<Auto_tp_res_set> getAuto_tp_res_sets() {
		List<Auto_tp_res_set> autoTpResSets = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			Auto_tp_res_set auto_tp_res_set = new Auto_tp_res_set();
			auto_tp_res_set.setTemplate_id(TEMPLATE_ID);
			auto_tp_res_set.setTemplate_res_id(PrimayKeyGener.getNextId());
			auto_tp_res_set.setSource_table_name("dhw_tpcds_item");
			auto_tp_res_set.setIs_dese(IsFlag.Fou.getCode());
			auto_tp_res_set.setCreate_date(DateUtil.getSysDate());
			auto_tp_res_set.setCreate_time(DateUtil.getSysTime());
			auto_tp_res_set.setCreate_user(TEST_USER_ID);
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

	private List<Auto_fetch_sum> getAuto_fetch_sums() {
		List<Auto_fetch_sum> auto_fetch_sums = new ArrayList<>();
		for (int i = 0; i < AutoFetchStatus.values().length; i++) {
			Auto_fetch_sum auto_fetch_sum = new Auto_fetch_sum();
			auto_fetch_sum.setTemplate_id(TEMPLATE_ID);
			auto_fetch_sum.setFetch_sum_id(FETCH_SUM_ID + i);
			auto_fetch_sum.setFetch_name("自主取数取数汇总测试" + i + THREAD_ID);
			if (i == 0) {
				auto_fetch_sum.setFetch_status(AutoFetchStatus.BianJi.getCode());
			} else {
				auto_fetch_sum.setFetch_status(AutoFetchStatus.WanCheng.getCode());
			}
			auto_fetch_sum.setCreate_user(TEST_USER_ID);
			auto_fetch_sum.setCreate_date(DateUtil.getSysDate());
			auto_fetch_sum.setCreate_time(DateUtil.getSysTime());
			auto_fetch_sum.setFetch_desc("自主取数取数汇总测试");
			auto_fetch_sum.setFetch_sql(FETCH_SQL);
			auto_fetch_sums.add(auto_fetch_sum);
		}
		return auto_fetch_sums;
	}

	private List<Auto_tp_info> getAuto_tp_infos() {
		List<Auto_tp_info> autoTpInfoList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Auto_tp_info auto_tp_info = new Auto_tp_info();
			auto_tp_info.setTemplate_id(TEMPLATE_ID + i);
			auto_tp_info.setTemplate_sql(TEMPLATE_SQL);
			auto_tp_info.setTemplate_desc("自主取数已发布模板");
			auto_tp_info.setTemplate_status(AutoTemplateStatus.FaBu.getCode());
			auto_tp_info.setTemplate_name("自主取数已发布模板测试" + i + THREAD_ID);
			auto_tp_info.setData_source(IsFlag.Shi.getCode());
			auto_tp_info.setCreate_user(TEST_USER_ID);
			auto_tp_info.setCreate_date(DateUtil.getSysDate());
			auto_tp_info.setCreate_time(DateUtil.getSysTime());
			autoTpInfoList.add(auto_tp_info);
		}
		return autoTpInfoList;
	}

	private Sys_user getSys_user() {
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
		return sys_user;
	}

	@Method(desc = "查询自主取数模板信息", logicStep = "1.正确的数据访问1，数据都有效")
	@Test
	public void getAccessTemplateInfo() {
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.post(getActionUrl("getAccessTemplateInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		List<Map<String, Object>> templateList = JsonUtil.toObject(JsonUtil.toJson(ar.getData()),
				new TypeReference<List<Map<String, Object>>>() {
				}.getType());
		checkAutoTpAndFetchSum(templateList);
	}

	private void checkAutoTpAndFetchSum(List<Map<String, Object>> templateList) {
		for (Map<String, Object> map : templateList) {
			String template_id = map.get("template_id").toString();
			if (template_id.equals(String.valueOf(TEMPLATE_ID))) {
				assertThat(map.get("template_name").toString(), is("自主取数已发布模板测试0" + THREAD_ID));
				assertThat(map.get("template_desc").toString(), is("自主取数已发布模板"));
				assertThat(map.get("create_user").toString(), is(String.valueOf(TEST_USER_ID)));
			} else if (template_id.equals(String.valueOf(TEMPLATE_ID + 1))) {
				assertThat(map.get("template_name").toString(), is("自主取数已发布模板测试1" + THREAD_ID));
				assertThat(map.get("template_desc").toString(), is("自主取数已发布模板"));
				assertThat(map.get("create_user").toString(), is(String.valueOf(TEST_USER_ID)));
			}
		}
	}

	@Method(desc = "模糊查询自主取数模板信息", logicStep = "1.正确的数据访问1，template_name存在" +
			"2.正确的数据访问2，template_name不存在")
	@Test
	public void getAccessTemplateInfoByName() {
		// 1.正确的数据访问1，template_name存在
		String bodyString = new HttpClient()
				.addData("template_name", "自主取数已发布模板测试")
				.post(getActionUrl("getAccessTemplateInfoByName"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		List<Map<String, Object>> templateList = JsonUtil.toObject(JsonUtil.toJson(ar.getData()),
				new TypeReference<List<Map<String, Object>>>() {
				}.getType());
		checkAutoTpAndFetchSum(templateList);
		// 2.正确的数据访问2，template_name不存在
		bodyString = new HttpClient()
				.addData("template_name", "-1111")
				.post(getActionUrl("getAccessTemplateInfoByName"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		templateList = JsonUtil.toObject(JsonUtil.toJson(ar.getData()),
				new TypeReference<List<Map<String, Object>>>() {
				}.getType());
		assertThat(templateList.size(), is(0));
	}

	@Method(desc = "根据模板ID查询自主取数模板信息", logicStep = "1.正确的数据访问1，template_name存在" +
			"2.错误的数据访问1，template_id不存在")
	@Test
	public void getAccessTemplateInfoById() {
		// 1.正确的数据访问1，template_name存在
		String bodyString = new HttpClient()
				.addData("template_id", TEMPLATE_ID)
				.post(getActionUrl("getAccessTemplateInfoById"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Auto_tp_info auto_tp_info = JsonUtil.toObjectSafety(
				JsonUtil.toJson(ar.getDataForMap()), Auto_tp_info.class)
				.orElseThrow(() -> new BusinessException("转换实体失败"));
		assertThat(auto_tp_info.getTemplate_name(), is("自主取数已发布模板测试0" + THREAD_ID));
		assertThat(auto_tp_info.getTemplate_desc(), is("自主取数已发布模板"));
		// 2.错误的数据访问1，template_id不存在
		bodyString = new HttpClient()
				.addData("template_id", "-111111")
				.post(getActionUrl("getAccessTemplateInfoById"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().isEmpty(), is(true));
	}

	@Method(desc = "获取自主取数结果字段", logicStep = "1.正确的数据访问1，template_id存在" +
			"2.错误的数据访问1，template_id不存在")
	@Test
	public void getAccessResultFields() {
		// 1.正确的数据访问1，template_id存在
		String bodyString = new HttpClient()
				.addData("template_id", TEMPLATE_ID)
				.post(getActionUrl("getAccessResultFields"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		List<Auto_tp_res_set> auto_tp_res_sets = ar.getDataForEntityList(Auto_tp_res_set.class);
		List<String> resShowColumnList = auto_tp_res_sets.stream()
				.map(Auto_tp_res_set::getRes_show_column).collect(Collectors.toList());
		assertThat(resShowColumnList.contains("i_item_id"), is(true));
		assertThat(resShowColumnList.contains("i_item_sk"), is(true));
		assertThat(resShowColumnList.contains("i_item_desc"), is(true));
		// 2.错误的数据访问1，template_id不存在
		bodyString = new HttpClient()
				.addData("template_id", "-1111")
				.post(getActionUrl("getAccessResultFields"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		auto_tp_res_sets = ar.getDataForEntityList(Auto_tp_res_set.class);
		assertThat(auto_tp_res_sets.size(), is(0));
	}

	@Method(desc = "获取自主取数结果字段", logicStep = "1.正确的数据访问1，template_id存在" +
			"2.错误的数据访问1，template_id不存在")
	@Test
	public void getAutoAccessFilterCond() {
		// 1.正确的数据访问1，template_id存在
		String bodyString = new HttpClient()
				.addData("template_id", TEMPLATE_ID)
				.post(getActionUrl("getAutoAccessFilterCond"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		List<Auto_tp_cond_info> auto_tp_cond_infos = ar.getDataForEntityList(Auto_tp_cond_info.class);
		checkAutoTpCondInfo(auto_tp_cond_infos);
		// 2.错误的数据访问1，template_id不存在
		bodyString = new HttpClient()
				.addData("template_id", "-1111")
				.post(getActionUrl("getAutoAccessFilterCond"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		auto_tp_cond_infos = ar.getDataForEntityList(Auto_tp_cond_info.class);
		assertThat(auto_tp_cond_infos.size(), is(0));
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

	@Method(desc = "获取自主取数选择历史信息", logicStep = "1.正确的数据访问1，template_id存在" +
			"2.错误的数据访问1，template_id不存在" +
			"")
	@Test
	public void getAccessSelectHistory() {
		// 1.正确的数据访问1，template_id存在
		String bodyString = new HttpClient()
				.addData("template_id", TEMPLATE_ID)
				.post(getActionUrl("getAccessSelectHistory"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		List<Auto_fetch_sum> auto_fetch_sums = ar.getDataForEntityList(Auto_fetch_sum.class);
		List<String> fetchNameList = auto_fetch_sums.stream().map(Auto_fetch_sum::getFetch_name)
				.collect(Collectors.toList());
		assertThat(fetchNameList.contains("自主取数取数汇总测试0" + THREAD_ID), is(true));
		assertThat(fetchNameList.contains("自主取数取数汇总测试1" + THREAD_ID), is(true));
		assertThat(fetchNameList.contains("自主取数取数汇总测试2" + THREAD_ID), is(true));
		// 2.错误的数据访问1，template_id不存在
		bodyString = new HttpClient()
				.addData("template_id", "-1111")
				.post(getActionUrl("getAccessSelectHistory"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		auto_fetch_sums = ar.getDataForEntityList(Auto_fetch_sum.class);
		assertThat(auto_fetch_sums.size(), is(0));
	}

	@Method(desc = "通过选择历史情况获取之前的条件", logicStep = "")
	@Test
	public void getAccessCondFromHistory() {
		// 1.正确的数据访问1，template_id存在
		String bodyString = new HttpClient()
				.addData("template_id", TEMPLATE_ID)
				.post(getActionUrl("getAccessCondFromHistory"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
	}

	@Test
	public void getAccessResultFromHistory() {
	}

	@Test
	public void getAutoAccessQueryResult() {
	}

	@Test
	public void saveAutoAccessInfoToQuery() {
	}

	@Test
	public void saveAutoAccessInfo() {
	}

	@Test
	public void getAccessSql() {
	}

	@Test
	public void getMyAccessInfo() {
	}

	@Test
	public void getMyAccessInfoByName() {
	}

	@Test
	public void getMyAccessInfoById() {
	}

	@Test
	public void getAccessResultByNumber() {
	}

	@Test
	public void getVisualComponentInfo() {
	}

	@Test
	public void getTAutoDataTableName() {
	}

	@Test
	public void getColumnByName() {
	}

	@Test
	public void getVisualComponentInfoById() {
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
			// 3.删除auto_fetch_sum表测试数据
			getAuto_fetch_sums().forEach(auto_fetch_sum ->
					SqlOperator.execute(db,
							"delete from " + Auto_fetch_sum.TableName + " where template_id=?",
							auto_fetch_sum.getTemplate_id()));
			num = SqlOperator.queryNumber(db,
					"select count(*) from " + Auto_fetch_sum.TableName + " where template_id =?",
					TEMPLATE_ID)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(0L));
			// 4.删除auto_tp_res_set表测试数据
			SqlOperator.execute(db, "delete from " + Auto_tp_res_set.TableName + " where template_id=?",
					TEMPLATE_ID);
			num = SqlOperator.queryNumber(db,
					"select count(*) from " + Auto_tp_res_set.TableName + " where template_id =?",
					TEMPLATE_ID)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(0L));
			// 5.删除auto_tp_cond_info表测试数据
			SqlOperator.execute(db, "delete from " + Auto_tp_cond_info.TableName + " where template_id=?",
					TEMPLATE_ID);
			num = SqlOperator.queryNumber(db,
					"select count(*) from " + Auto_tp_cond_info.TableName + " where template_id =?",
					TEMPLATE_ID)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(0L));
			// 5.删除auto_tp_cond_info表测试数据
			SqlOperator.execute(db, "delete from " + Auto_fetch_cond.TableName + " where fetch_sum_id=?",
					FETCH_SUM_ID);
			num = SqlOperator.queryNumber(db,
					"select count(*) from " + Auto_fetch_cond.TableName + " where fetch_sum_id =?",
					FETCH_SUM_ID)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(0L));
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
	}
}