package hrds.a.biz.codemaintenance;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.AgentStatus;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.OperationType;
import hrds.commons.entity.Hyren_code_info;
import hrds.commons.entity.Orig_code_info;
import hrds.commons.entity.Orig_syso_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.ParallerTestUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "码值维护测试类", author = "dhw", createdate = "2020/7/14 17:23")
public class CodeMaintenanceActionTest extends WebBaseTestCase {
	//请填写测试用户需要做登录验证的A项目的登录验证的接口
	private static final String LOGIN_URL = ParallerTestUtil.TESTINITCONFIG.getString("login_url");
	// 已经存在的用户ID,用于模拟登录
	private static final long USER_ID = ParallerTestUtil.TESTINITCONFIG.getLong("user_id");
	private static final String PASSWORD = ParallerTestUtil.TESTINITCONFIG.getString("password");
	//当前线程的id
	private final long THREAD_ID = Thread.currentThread().getId();
	private final long Orig_id = PrimayKeyGener.getNextId();

	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 初始化统一编码测试数据
			List<Hyren_code_info> hyrenCodeInfoList = getHyren_code_infos();
			hyrenCodeInfoList.forEach(hyren_code_info ->
					assertThat(Hyren_code_info.TableName + "表测试数据初始化成功", hyren_code_info.add(db), is(1))
			);
			// 初始化源系统信息表测试数据
			List<Orig_syso_info> orig_syso_infos = getOrig_syso_infos();
			orig_syso_infos.forEach(orig_syso_info ->
					assertThat(Orig_syso_info.TableName + "表测试数据初始化成功", orig_syso_info.add(db), is(1))
			);
			// 初始化源系统编码表测试数据
			List<Orig_code_info> orig_code_infos = getOrig_code_infos();
			orig_code_infos.forEach(orig_code_info ->
					assertThat(Orig_code_info.TableName + "表测试数据初始化成功", orig_code_info.add(db), is(1))
			);
			// 提交事务
			SqlOperator.commitTransaction(db);
		}

		// 模拟用户登录
		String bodyString = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("password", PASSWORD)
				.post(LOGIN_URL)
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));

		assertThat("用户登录", ar.isSuccess(), is(true));
	}

	private List<Orig_syso_info> getOrig_syso_infos() {
		List<Orig_syso_info> orig_syso_infos = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Orig_syso_info orig_syso_info = new Orig_syso_info();
			orig_syso_info.setOrig_sys_code("dhw" + i + THREAD_ID);
			if (i == 0) {
				orig_syso_info.setOrig_sys_name("财务系统");
			} else {
				orig_syso_info.setOrig_sys_name("房屋系统");
			}
			orig_syso_infos.add(orig_syso_info);
		}
		return orig_syso_infos;
	}

	private List<Orig_code_info> getOrig_code_infos() {
		List<Orig_code_info> orig_code_infos = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			OperationType[] operationTypes = OperationType.values();
			if (i == 0) {
				for (int j = 0; j < operationTypes.length; j++) {
					OperationType operationType = operationTypes[j];
					Orig_code_info orig_code_info = new Orig_code_info();
					orig_code_info.setOrig_id(Orig_id + i + j);
					orig_code_info.setOrig_sys_code("dhw0" + THREAD_ID);
					orig_code_info.setCode_classify(OperationType.CodeName + THREAD_ID);
					orig_code_info.setCode_value(operationType.getCode());
					orig_code_info.setOrig_value(operationType.getCode() + THREAD_ID);
					orig_code_infos.add(orig_code_info);
				}
			} else {
				for (int j = 0; j < operationTypes.length; j++) {
					OperationType operationType = operationTypes[j];
					Orig_code_info orig_code_info = new Orig_code_info();
					orig_code_info.setOrig_sys_code("dhw1" + THREAD_ID);
					orig_code_info.setOrig_id(Orig_id + i + j + operationTypes.length);
					orig_code_info.setCode_classify(OperationType.CodeName + THREAD_ID);
					orig_code_info.setCode_value(operationType.getCode());
					orig_code_info.setOrig_value(operationType.getCode() + THREAD_ID);
					orig_code_infos.add(orig_code_info);
				}
			}
		}
		return orig_code_infos;
	}

	private List<Hyren_code_info> getHyren_code_infos() {
		List<Hyren_code_info> hyrenCodeInfoList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			if (i == 0) {
				for (AgentType agentType : AgentType.values()) {
					Hyren_code_info hyren_code_info = new Hyren_code_info();
					hyren_code_info.setCode_classify(AgentType.CodeName + THREAD_ID);
					hyren_code_info.setCode_classify_name("agent类别");
					hyren_code_info.setCode_type_name(agentType.getValue());
					hyren_code_info.setCode_value(agentType.getCode());
					hyrenCodeInfoList.add(hyren_code_info);
				}
			} else {
				for (OperationType operationType : OperationType.values()) {
					Hyren_code_info hyren_code_info = new Hyren_code_info();
					hyren_code_info.setCode_classify(OperationType.CodeName + THREAD_ID);
					hyren_code_info.setCode_classify_name("operationType类型");
					hyren_code_info.setCode_type_name(operationType.getValue());
					hyren_code_info.setCode_value(operationType.getCode());
					hyrenCodeInfoList.add(hyren_code_info);
				}
			}
		}
		return hyrenCodeInfoList;
	}

	@Test
	public void getCodeInfo() {
		String bodyString = new HttpClient()
				.post(getActionUrl("getCodeInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		List<Hyren_code_info> hyrenCodeInfos = ar.getDataForEntityList(Hyren_code_info.class);
		List<String> codeTypeList = new ArrayList<>();
		List<String> codeValueList = new ArrayList<>();
		for (AgentType agentType : AgentType.values()) {
			codeTypeList.add(agentType.getValue());
			codeValueList.add(agentType.getCode());
		}
		for (OperationType operationType : OperationType.values()) {
			codeTypeList.add(operationType.getValue());
			codeValueList.add(operationType.getCode());
		}

		List<String> codeClassifyList = new ArrayList<>();
		List<String> codeClassifyNameList = new ArrayList<>();
		for (Hyren_code_info hyrenCodeInfo : hyrenCodeInfos) {
			codeClassifyList.add(hyrenCodeInfo.getCode_classify());
			codeClassifyNameList.add(hyrenCodeInfo.getCode_classify_name());
			assertThat(codeTypeList.contains(hyrenCodeInfo.getCode_type_name()), is(true));
			assertThat(codeValueList.contains(hyrenCodeInfo.getCode_value()), is(true));
		}
		assertThat(codeClassifyList.contains(AgentType.CodeName + THREAD_ID), is(true));
		assertThat(codeClassifyList.contains(OperationType.CodeName + THREAD_ID), is(true));
		assertThat(codeClassifyNameList.contains("agent类别"), is(true));
		assertThat(codeClassifyNameList.contains("operationType类型"), is(true));
	}

	@Test
	public void saveCodeInfo() {
		List<Hyren_code_info> hyren_code_infos = new ArrayList<>();
		List<String> codeTypeList = new ArrayList<>();
		List<String> codeValueList = new ArrayList<>();
		for (AgentStatus agentStatus : AgentStatus.values()) {
			Hyren_code_info hyren_code_info = new Hyren_code_info();
			hyren_code_info.setCode_classify(AgentStatus.CodeName + THREAD_ID);
			hyren_code_info.setCode_classify_name("agent状态");
			hyren_code_info.setCode_type_name(agentStatus.getValue());
			hyren_code_info.setCode_value(agentStatus.getCode());
			hyren_code_infos.add(hyren_code_info);
			codeTypeList.add(agentStatus.getValue());
			codeValueList.add(agentStatus.getCode());
		}
		String bodyString = new HttpClient()
				.addData("hyren_code_infos", JsonUtil.toJson(hyren_code_infos))
				.post(getActionUrl("saveCodeInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Hyren_code_info> hyrenCodeInfoList = SqlOperator.queryList(db, Hyren_code_info.class,
					"select * from " + Hyren_code_info.TableName + " where code_classify=?",
					AgentStatus.CodeName + THREAD_ID);
			for (Hyren_code_info hyrenCodeInfo : hyrenCodeInfoList) {
				assertThat(hyrenCodeInfo.getCode_classify(), is(AgentStatus.CodeName + THREAD_ID));
				assertThat(hyrenCodeInfo.getCode_classify_name(), is("agent状态"));
				assertThat(codeTypeList.contains(hyrenCodeInfo.getCode_type_name()), is(true));
				assertThat(codeValueList.contains(hyrenCodeInfo.getCode_value()), is(true));
			}
		}
		// 2.错误的数据访问1，code_classify为空
		List<Hyren_code_info> hyren_code_infos2 = new ArrayList<>();
		for (AgentStatus agentStatus : AgentStatus.values()) {
			Hyren_code_info hyren_code_info = new Hyren_code_info();
			hyren_code_info.setCode_classify_name("agent状态");
			hyren_code_info.setCode_type_name(agentStatus.getValue());
			hyren_code_info.setCode_value(agentStatus.getCode());
			hyren_code_infos2.add(hyren_code_info);
		}
		bodyString = new HttpClient()
				.addData("hyren_code_infos", JsonUtil.toJson(hyren_code_infos2))
				.post(getActionUrl("saveCodeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，code_classify_name为空
		List<Hyren_code_info> hyren_code_infos3 = new ArrayList<>();
		for (AgentStatus agentStatus : AgentStatus.values()) {
			Hyren_code_info hyren_code_info = new Hyren_code_info();
			hyren_code_info.setCode_classify(AgentStatus.CodeName + THREAD_ID);
			hyren_code_info.setCode_type_name(agentStatus.getValue());
			hyren_code_info.setCode_value(agentStatus.getCode());
			hyren_code_infos3.add(hyren_code_info);
		}
		bodyString = new HttpClient()
				.addData("hyren_code_infos", JsonUtil.toJson(hyren_code_infos3))
				.post(getActionUrl("saveCodeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，code_type_name为空
		List<Hyren_code_info> hyren_code_infos4 = new ArrayList<>();
		for (AgentStatus agentStatus : AgentStatus.values()) {
			Hyren_code_info hyren_code_info = new Hyren_code_info();
			hyren_code_info.setCode_classify(AgentStatus.CodeName + THREAD_ID);
			hyren_code_info.setCode_classify_name("agent状态");
			hyren_code_info.setCode_value(agentStatus.getCode());
			hyren_code_infos4.add(hyren_code_info);
		}
		bodyString = new HttpClient()
				.addData("hyren_code_infos", JsonUtil.toJson(hyren_code_infos4))
				.post(getActionUrl("saveCodeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问4，code_value为空
		List<Hyren_code_info> hyren_code_infos5 = new ArrayList<>();
		for (AgentStatus agentStatus : AgentStatus.values()) {
			Hyren_code_info hyren_code_info = new Hyren_code_info();
			hyren_code_info.setCode_classify(AgentStatus.CodeName + THREAD_ID);
			hyren_code_info.setCode_type_name(agentStatus.getValue());
			hyren_code_info.setCode_value(agentStatus.getValue());
			hyren_code_infos5.add(hyren_code_info);
		}
		bodyString = new HttpClient()
				.addData("hyren_code_infos", JsonUtil.toJson(hyren_code_infos5))
				.post(getActionUrl("saveCodeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 6.错误的数据访问5，code_classify已存在
		List<Hyren_code_info> hyren_code_infos1 = getHyren_code_infos();
		bodyString = new HttpClient()
				.addData("hyren_code_infos", JsonUtil.toJson(hyren_code_infos1))
				.post(getActionUrl("saveCodeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Test
	public void updateCodeInfo() {
		List<Hyren_code_info> hyren_code_infos = new ArrayList<>();
		List<String> codeTypeList = new ArrayList<>();
		List<String> codeValueList = new ArrayList<>();
		for (AgentType agentType : AgentType.values()) {
			Hyren_code_info hyren_code_info = new Hyren_code_info();
			hyren_code_info.setCode_classify(AgentType.CodeName + THREAD_ID);
			hyren_code_info.setCode_classify_name("agent类别2");
			hyren_code_info.setCode_type_name(agentType.getValue() + THREAD_ID);
			hyren_code_info.setCode_value(agentType.getCode() + THREAD_ID);
			hyren_code_infos.add(hyren_code_info);
			codeTypeList.add(hyren_code_info.getCode_type_name());
			codeValueList.add(hyren_code_info.getCode_value());
		}
		String bodyString = new HttpClient()
				.addData("hyren_code_infos", JsonUtil.toJson(hyren_code_infos))
				.post(getActionUrl("updateCodeInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Hyren_code_info> hyrenCodeInfoList = SqlOperator.queryList(db, Hyren_code_info.class,
					"select * from " + Hyren_code_info.TableName + " where code_classify=?",
					AgentType.CodeName + THREAD_ID);
			for (Hyren_code_info hyrenCodeInfo : hyrenCodeInfoList) {
				assertThat(hyrenCodeInfo.getCode_classify(), is(AgentType.CodeName + THREAD_ID));
				assertThat(hyrenCodeInfo.getCode_classify_name(), is("agent类别2"));
				assertThat(codeTypeList.contains(hyrenCodeInfo.getCode_type_name()), is(true));
				assertThat(codeValueList.contains(hyrenCodeInfo.getCode_value()), is(true));
			}
		}
		// 2.错误的数据访问1，code_classify为空
		List<Hyren_code_info> hyren_code_infos2 = new ArrayList<>();
		for (AgentType agentType : AgentType.values()) {
			Hyren_code_info hyren_code_info = new Hyren_code_info();
			hyren_code_info.setCode_classify_name("agent类别2");
			hyren_code_info.setCode_type_name(agentType.getValue() + THREAD_ID);
			hyren_code_info.setCode_value(agentType.getCode() + THREAD_ID);
			hyren_code_infos2.add(hyren_code_info);
		}
		bodyString = new HttpClient()
				.addData("hyren_code_infos", JsonUtil.toJson(hyren_code_infos2))
				.post(getActionUrl("updateCodeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，code_classify_name为空
		List<Hyren_code_info> hyren_code_infos3 = new ArrayList<>();
		for (AgentType agentType : AgentType.values()) {
			Hyren_code_info hyren_code_info = new Hyren_code_info();
			hyren_code_info.setCode_classify(AgentType.CodeName + THREAD_ID);
			hyren_code_info.setCode_type_name(agentType.getValue() + THREAD_ID);
			hyren_code_info.setCode_value(agentType.getCode() + THREAD_ID);
			hyren_code_infos3.add(hyren_code_info);
		}
		bodyString = new HttpClient()
				.addData("hyren_code_infos", JsonUtil.toJson(hyren_code_infos3))
				.post(getActionUrl("updateCodeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，code_type_name为空
		List<Hyren_code_info> hyren_code_infos4 = new ArrayList<>();
		for (AgentType agentType : AgentType.values()) {
			Hyren_code_info hyren_code_info = new Hyren_code_info();
			hyren_code_info.setCode_classify(AgentType.CodeName + THREAD_ID);
			hyren_code_info.setCode_classify_name("agent类别2");
			hyren_code_info.setCode_value(agentType.getCode() + THREAD_ID);
			hyren_code_infos4.add(hyren_code_info);
		}
		bodyString = new HttpClient()
				.addData("hyren_code_infos", JsonUtil.toJson(hyren_code_infos4))
				.post(getActionUrl("updateCodeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问5，code_value为空
		List<Hyren_code_info> hyren_code_infos5 = new ArrayList<>();
		for (AgentType agentType : AgentType.values()) {
			Hyren_code_info hyren_code_info = new Hyren_code_info();
			hyren_code_info.setCode_classify(AgentType.CodeName + THREAD_ID);
			hyren_code_info.setCode_classify_name("agent类别2");
			hyren_code_info.setCode_type_name(agentType.getValue() + THREAD_ID);
			hyren_code_infos5.add(hyren_code_info);
		}
		bodyString = new HttpClient()
				.addData("hyren_code_infos", JsonUtil.toJson(hyren_code_infos5))
				.post(getActionUrl("updateCodeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Test
	public void deleteCodeInfo() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 删除前确认数据存在
			long num = SqlOperator.queryNumber(db,
					"select count(*) from " + Hyren_code_info.TableName + " where code_classify=?",
					AgentType.CodeName + THREAD_ID)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(5L));
			// 1.正确的数据访问1.数据有效
			String bodyString = new HttpClient()
					.addData("code_classify", AgentType.CodeName + THREAD_ID)
					.post(getActionUrl("deleteCodeInfo"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后确认数据已删除
			num = SqlOperator.queryNumber(db,
					"select count(*) from " + Hyren_code_info.TableName + " where code_classify=?",
					AgentType.CodeName + THREAD_ID)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(0L));
			// 2.错误的数据访问1.code_classify正在被使用
			bodyString = new HttpClient()
					.addData("code_classify", OperationType.CodeName + THREAD_ID)
					.post(getActionUrl("deleteCodeInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Test
	public void getOrigSysInfo() {
		// 1.正确的数据访问1.数据有效
		String bodyString = new HttpClient()
				.post(getActionUrl("getOrigSysInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		List<Orig_syso_info> origSysoInfos = ar.getDataForEntityList(Orig_syso_info.class);
		List<String> orig_sys_codeList = new ArrayList<>();
		List<String> orig_sys_nameList = new ArrayList<>();
		for (Orig_syso_info origSysoInfo : origSysoInfos) {
			orig_sys_codeList.add(origSysoInfo.getOrig_sys_code());
			orig_sys_nameList.add(origSysoInfo.getOrig_sys_name());
		}
		for (Orig_syso_info orig_syso_info : origSysoInfos) {
			assertThat(orig_sys_codeList.contains(orig_syso_info.getOrig_sys_code()), is(true));
			assertThat(orig_sys_nameList.contains(orig_syso_info.getOrig_sys_name()), is(true));
		}
	}

	@Test
	public void addOrigSysInfo() {
		Orig_syso_info orig_syso_info = new Orig_syso_info();
		orig_syso_info.setOrig_sys_code("addOrigSys" + THREAD_ID);
		orig_syso_info.setOrig_sys_name("新增源系统信息测试");
		// 1.正确的数据访问1.数据有效
		String bodyString = new HttpClient()
				.addData("orig_syso_info", orig_syso_info)
				.post(getActionUrl("addOrigSysInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Orig_syso_info origSysoInfo = SqlOperator.queryOneObject(db, Orig_syso_info.class,
					"select * from " + Orig_syso_info.TableName + " where orig_sys_code=?",
					orig_syso_info.getOrig_sys_code())
					.orElseThrow(() -> new BusinessException("sql查询错误或映射实体失败"));
			assertThat(origSysoInfo.getOrig_sys_name(), is(orig_syso_info.getOrig_sys_name()));
			// 测试完删除新增测试数据
			deleteAddOrigSysoInfo(orig_syso_info, db);
		}
		// 2.错误的数据访问1.源系统编码已存在
		orig_syso_info = new Orig_syso_info();
		orig_syso_info.setOrig_sys_code("dhw0" + THREAD_ID);
		orig_syso_info.setOrig_sys_name("财务系统");
		bodyString = new HttpClient()
				.addData("orig_syso_info", orig_syso_info)
				.post(getActionUrl("addOrigSysInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	private void deleteAddOrigSysoInfo(Orig_syso_info orig_syso_info, DatabaseWrapper db) {
		SqlOperator.execute(db,
				"delete from " + Orig_syso_info.TableName + " where orig_sys_code=?",
				orig_syso_info.getOrig_sys_code());
		long num = SqlOperator.queryNumber(db,
				"select count(*) from " + Orig_syso_info.TableName + " where orig_sys_code=?",
				orig_syso_info.getOrig_sys_code())
				.orElseThrow(() -> new BusinessException("sql查询错误"));
		assertThat(num, is(0L));
		db.commit();
	}

	@Test
	public void getOrigCodeInfo() {
		// 1.正确的数据访问1.数据有效
		String bodyString = new HttpClient()
				.addData("orig_sys_code", "dhw0" + THREAD_ID)
				.post(getActionUrl("getOrigCodeInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		List<String> codeTypeList = new ArrayList<>();
		List<String> codeValueList = new ArrayList<>();
		List<String> origValueList = new ArrayList<>();
		for (OperationType operationType : OperationType.values()) {
			codeTypeList.add(operationType.getValue());
			codeValueList.add(operationType.getCode());
			origValueList.add(operationType.getCode() + THREAD_ID);
		}
		for (int i = 0; i < result.getRowCount(); i++) {
			assertThat(codeTypeList.contains(result.getString(i, "code_type_name")), is(true));
			assertThat(codeValueList.contains(result.getString(i, "code_value")), is(true));
			assertThat(origValueList.contains(result.getString(i, "orig_value")), is(true));
			assertThat(result.getString(i, "code_classify"), is("OperationType1"));
			assertThat(result.getString(i, "code_classify_name"), is("operationType类型"));
		}
	}

	@Test
	public void getCodeInfoByCodeClassify() {
		// 1.正确的数据访问1.数据有效
		String bodyString = new HttpClient()
				.addData("code_classify", OperationType.CodeName + THREAD_ID)
				.post(getActionUrl("getCodeInfoByCodeClassify"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		List<Hyren_code_info> hyrenCodeInfos = ar.getDataForEntityList(Hyren_code_info.class);
		List<String> codeTypeList = new ArrayList<>();
		List<String> codeValueList = new ArrayList<>();
		for (OperationType operationType : OperationType.values()) {
			codeTypeList.add(operationType.getValue());
			codeValueList.add(operationType.getCode());
		}
		for (Hyren_code_info hyren_code_info : hyrenCodeInfos) {
			assertThat(codeTypeList.contains(hyren_code_info.getCode_type_name()), is(true));
			assertThat(codeValueList.contains(hyren_code_info.getCode_value()), is(true));
			assertThat(hyren_code_info.getCode_classify(), is("OperationType1"));
			assertThat(hyren_code_info.getCode_classify_name(), is("operationType类型"));
		}
		// 2.错误的数据访问1，code_classify不存在
		bodyString = new HttpClient()
				.addData("code_classify", "aaa")
				.post(getActionUrl("getCodeInfoByCodeClassify"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));

	}

	@Test
	public void addOrigCodeInfo() {
		List<Orig_code_info> origCodeInfos = new ArrayList<>();
		List<String> origValueList = new ArrayList<>();
		List<String> codeValueList = new ArrayList<>();
		for (AgentType agentType : AgentType.values()) {
			Orig_code_info orig_code_info = new Orig_code_info();
			orig_code_info.setCode_classify(AgentType.CodeName + THREAD_ID);
			orig_code_info.setOrig_value(agentType.getCode() + THREAD_ID);
			orig_code_info.setCode_value(agentType.getCode());
			origCodeInfos.add(orig_code_info);
			codeValueList.add(orig_code_info.getCode_value());
			origValueList.add(orig_code_info.getOrig_value());
		}
		// 1.正确的数据访问1.数据有效
		String bodyString = new HttpClient()
				.addData("orig_code_infos", JsonUtil.toJson(origCodeInfos))
				.addData("orig_sys_code", "dhw0" + THREAD_ID)
				.post(getActionUrl("addOrigCodeInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Orig_code_info> origCodeInfoList = SqlOperator.queryList(db, Orig_code_info.class,
					"select * from " + Orig_code_info.TableName
							+ " where orig_sys_code=? and code_classify=?",
					"dhw0" + THREAD_ID, AgentType.CodeName + THREAD_ID);
			for (Orig_code_info orig_code_info : origCodeInfoList) {
				assertThat(origValueList.contains(orig_code_info.getOrig_value()), is(true));
				assertThat(codeValueList.contains(orig_code_info.getCode_value()), is(true));
			}
		}
		// 2.错误的数据访问1.orig_sys_code不存在
		bodyString = new HttpClient()
				.addData("orig_code_infos", JsonUtil.toJson(origCodeInfos))
				.addData("orig_sys_code", "aaa")
				.post(getActionUrl("addOrigCodeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2.Code_classify不存在
		origCodeInfos = new ArrayList<>();
		for (AgentType agentType : AgentType.values()) {
			Orig_code_info orig_code_info = new Orig_code_info();
			orig_code_info.setCode_classify("aaa");
			orig_code_info.setOrig_value(agentType.getCode() + THREAD_ID);
			orig_code_info.setCode_value(agentType.getCode());
			origCodeInfos.add(orig_code_info);
		}
		bodyString = new HttpClient()
				.addData("orig_code_infos", JsonUtil.toJson(origCodeInfos))
				.addData("orig_sys_code", "dhw0" + THREAD_ID)
				.post(getActionUrl("addOrigCodeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3.orig_value为空
		origCodeInfos = new ArrayList<>();
		for (AgentType agentType : AgentType.values()) {
			Orig_code_info orig_code_info = new Orig_code_info();
			orig_code_info.setCode_classify(AgentType.CodeName + THREAD_ID);
			orig_code_info.setCode_value(agentType.getCode());
			origCodeInfos.add(orig_code_info);
		}
		bodyString = new HttpClient()
				.addData("orig_code_infos", JsonUtil.toJson(origCodeInfos))
				.addData("orig_sys_code", "dhw0" + THREAD_ID)
				.post(getActionUrl("addOrigCodeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问4.code_value为空
		origCodeInfos = new ArrayList<>();
		for (AgentType agentType : AgentType.values()) {
			Orig_code_info orig_code_info = new Orig_code_info();
			orig_code_info.setCode_classify(AgentType.CodeName + THREAD_ID);
			orig_code_info.setOrig_value(agentType.getCode() + THREAD_ID);
			origCodeInfos.add(orig_code_info);
		}
		bodyString = new HttpClient()
				.addData("orig_code_infos", JsonUtil.toJson(origCodeInfos))
				.addData("orig_sys_code", "dhw0" + THREAD_ID)
				.post(getActionUrl("addOrigCodeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Test
	public void updateOrigCodeInfo() {
		List<Orig_code_info> origCodeInfos = new ArrayList<>();
		List<String> origValueList = new ArrayList<>();
		OperationType[] operationTypes = OperationType.values();
		for (int j = 0; j < operationTypes.length; j++) {
			OperationType operationType = operationTypes[j];
			Orig_code_info orig_code_info = new Orig_code_info();
			orig_code_info.setOrig_id(Orig_id + j);
			orig_code_info.setCode_classify(OperationType.CodeName + THREAD_ID);
			orig_code_info.setOrig_value(operationType.getCode() + THREAD_ID * 10);
			origCodeInfos.add(orig_code_info);
			origValueList.add(orig_code_info.getOrig_value());
		}
		// 1.正确的数据访问1.数据有效
		String bodyString = new HttpClient()
				.addData("orig_code_infos", JsonUtil.toJson(origCodeInfos))
				.addData("orig_sys_code", "dhw0" + THREAD_ID)
				.post(getActionUrl("updateOrigCodeInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Orig_code_info> origCodeInfoList = SqlOperator.queryList(db, Orig_code_info.class,
					"select * from " + Orig_code_info.TableName
							+ " where orig_sys_code=? and code_classify=?",
					"dhw0" + THREAD_ID, OperationType.CodeName + THREAD_ID);
			for (Orig_code_info orig_code_info : origCodeInfoList) {
				assertThat(origValueList.contains(orig_code_info.getOrig_value()), is(true));
			}
		}
		// 2.错误的数据访问1.orig_sys_code不存在
		bodyString = new HttpClient()
				.addData("orig_code_infos", JsonUtil.toJson(origCodeInfos))
				.addData("orig_sys_code", "aaa")
				.post(getActionUrl("updateOrigCodeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2.code_classify不存在
		origCodeInfos = new ArrayList<>();
		for (int j = 0; j < operationTypes.length; j++) {
			OperationType operationType = operationTypes[j];
			Orig_code_info orig_code_info = new Orig_code_info();
			orig_code_info.setOrig_id(Orig_id + j);
			orig_code_info.setOrig_value(operationType.getCode() + THREAD_ID * 10);
			origCodeInfos.add(orig_code_info);
		}
		bodyString = new HttpClient()
				.addData("orig_code_infos", JsonUtil.toJson(origCodeInfos))
				.addData("orig_sys_code", "dhw0" + THREAD_ID)
				.post(getActionUrl("updateOrigCodeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3.orig_value为空
		origCodeInfos = new ArrayList<>();
		for (int j = 0; j < operationTypes.length; j++) {
			Orig_code_info orig_code_info = new Orig_code_info();
			orig_code_info.setOrig_id(Orig_id + j);
			orig_code_info.setCode_classify(OperationType.CodeName + THREAD_ID);
			origCodeInfos.add(orig_code_info);
		}
		bodyString = new HttpClient()
				.addData("orig_code_infos", JsonUtil.toJson(origCodeInfos))
				.addData("orig_sys_code", "dhw0" + THREAD_ID)
				.post(getActionUrl("updateOrigCodeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问4.orig_id为空
		origCodeInfos = new ArrayList<>();
		for (OperationType operationType : operationTypes) {
			Orig_code_info orig_code_info = new Orig_code_info();
			orig_code_info.setCode_classify(OperationType.CodeName + THREAD_ID);
			orig_code_info.setOrig_value(operationType.getCode() + THREAD_ID * 10);
			origCodeInfos.add(orig_code_info);
		}
		bodyString = new HttpClient()
				.addData("orig_code_infos", JsonUtil.toJson(origCodeInfos))
				.addData("orig_sys_code", "dhw0" + THREAD_ID)
				.post(getActionUrl("updateOrigCodeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Test
	public void deleteOrigCodeInfo() {
		// 1.正确的数据访问1.数据有效
		// 删除前查询确认数据存在
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long num = SqlOperator.queryNumber(db,
					"select count(*) from " + Orig_code_info.TableName
							+ " where code_classify=? and orig_sys_code=?",
					OperationType.CodeName + THREAD_ID, "dhw0" + THREAD_ID)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(3L));
			String bodyString = new HttpClient()
					.addData("code_classify", OperationType.CodeName + THREAD_ID)
					.addData("orig_sys_code", "dhw0" + THREAD_ID)
					.post(getActionUrl("deleteOrigCodeInfo"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询确认数据已删除
			num = SqlOperator.queryNumber(db,
					"select count(*) from " + Orig_code_info.TableName
							+ " where code_classify=? and orig_sys_code=?",
					OperationType.CodeName + THREAD_ID, "dhw0" + THREAD_ID)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(num, is(0L));
			// 2.错误的数据访问1，code_classify不存在
			bodyString = new HttpClient()
					.addData("code_classify", "aaa")
					.addData("orig_sys_code", "dhw0" + THREAD_ID)
					.post(getActionUrl("deleteOrigCodeInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，code_classify不存在
			bodyString = new HttpClient()
					.addData("code_classify", OperationType.CodeName + THREAD_ID)
					.addData("orig_sys_code", "aaa")
					.post(getActionUrl("deleteOrigCodeInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Test
	public void getAllCodeClassify() {
		// 1.正确的数据访问1.数据有效
		String bodyString = new HttpClient()
				.post(getActionUrl("getAllCodeClassify"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		List<String> codeClassifyList = ar.getDataForEntityList(String.class);
		assertThat(codeClassifyList.contains(OperationType.CodeName + THREAD_ID), is(true));
		assertThat(codeClassifyList.contains(AgentType.CodeName + THREAD_ID), is(true));
	}

	@Test
	public void getOrigCodeInfoByCode() {
		// 1.正确的数据访问1，数据都有效
		String rightString = new HttpClient()
				.addData("code_classify", OperationType.CodeName + THREAD_ID)
				.addData("orig_sys_code", "dhw0" + THREAD_ID)
				.post(getActionUrl("getOrigCodeInfoByCode"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(rightString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败!"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		List<String> codeTypeList = new ArrayList<>();
		List<String> codeValueList = new ArrayList<>();
		List<String> origValueList = new ArrayList<>();
		for (OperationType operationType : OperationType.values()) {
			codeTypeList.add(operationType.getValue());
			codeValueList.add(operationType.getCode());
			origValueList.add(operationType.getCode() + THREAD_ID);
		}
		for (int i = 0; i < result.getRowCount(); i++) {
			assertThat(codeTypeList.contains(result.getString(i, "code_type_name")), is(true));
			assertThat(codeValueList.contains(result.getString(i, "code_value")), is(true));
			assertThat(origValueList.contains(result.getString(i, "orig_value")), is(true));
			assertThat(result.getString(i, "code_classify"), is("OperationType1"));
			assertThat(result.getString(i, "code_classify_name"), is("operationType类型"));
		}
		// 2.错误的数据访问1，code_classify不存在
		rightString = new HttpClient()
				.addData("code_classify", "aaa")
				.addData("orig_sys_code", "dhw0" + THREAD_ID)
				.post(getActionUrl("getOrigCodeInfoByCode"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(rightString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败!"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，orig_sys_code不存在
		rightString = new HttpClient()
				.addData("code_classify", OperationType.CodeName + THREAD_ID)
				.addData("orig_sys_code", "aaa")
				.post(getActionUrl("getOrigCodeInfoByCode"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(rightString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败!"));
		assertThat(ar.isSuccess(), is(false));
	}


	@After
	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 删除Hyren_code_info表测试数据
			SqlOperator.execute(db,
					"delete from " + Hyren_code_info.TableName + " where code_classify in(?,?,?)",
					AgentType.CodeName + THREAD_ID, AgentStatus.CodeName + THREAD_ID,
					OperationType.CodeName + THREAD_ID);
			if (SqlOperator.queryNumber(db,
					"select count(*) from " + Hyren_code_info.TableName + " where code_classify in(?,?,?)",
					AgentType.CodeName + THREAD_ID, AgentStatus.CodeName + THREAD_ID,
					OperationType.CodeName + THREAD_ID)
					.orElseThrow(() -> new BusinessException("sql查询错误")) != 0) {
				throw new BusinessException(Hyren_code_info.TableName + "表测试数据删除失败");
			}
			// 删除Orig_syso_info表测试数据
			for (Orig_syso_info orig_syso_info : getOrig_syso_infos()) {
				SqlOperator.execute(db,
						"delete from " + Orig_syso_info.TableName + " where orig_sys_code =?",
						orig_syso_info.getOrig_sys_code());
				if (SqlOperator.queryNumber(db,
						"select count(*) from " + Orig_syso_info.TableName + " where orig_sys_code =?",
						orig_syso_info.getOrig_sys_code())
						.orElseThrow(() -> new BusinessException("sql查询错误")) != 0) {
					throw new BusinessException(Hyren_code_info.TableName + "表测试数据删除失败");
				}
				// 删除Orig_syso_info表测试数据
				SqlOperator.execute(db,
						"delete from " + Orig_code_info.TableName + " where orig_sys_code =?",
						orig_syso_info.getOrig_sys_code());
				if (SqlOperator.queryNumber(db,
						"select count(*) from " + Orig_code_info.TableName + " where orig_sys_code =?",
						orig_syso_info.getOrig_sys_code())
						.orElseThrow(() -> new BusinessException("sql查询错误")) != 0) {
					throw new BusinessException(Hyren_code_info.TableName + "表测试数据删除失败");
				}
			}
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
	}
}
