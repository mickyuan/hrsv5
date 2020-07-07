package hrds.b.biz.agent.semistructured.collectfileconf;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.*;
import hrds.commons.entity.Object_collect;
import hrds.commons.entity.Object_collect_struct;
import hrds.commons.entity.Object_collect_task;
import hrds.commons.entity.Object_handle_type;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "半结构化采集文件配置类测试", author = "dhw", createdate = "2020/7/7 17:02")
public class CollectFileConfActionTest extends WebBaseTestCase {
	//请填写测试用户需要做登录验证的A项目的登录验证的接口
	private static final String LOGIN_URL = agentInitConfig.getString("login_url");
	// 已经存在的用户ID,用于模拟登录
	private static final long USER_ID = agentInitConfig.getLong("user_id");
	// 已经存在的用户密码,用于模拟登录
	private static final String PASSWORD = agentInitConfig.getString("password");
	// agent所在机器的操作系统linux|windows
	private static final String AGENT_OS_NAME = agentInitConfig.getString("agent_os_name");
	// 已经部署过得agent
	private static final long AGENT_ID = agentInitConfig.getLong("agent_id");
	// 数据字典目录
	private final String filepath = FileUtil.getFile(
			"src/test/java/hrds/b/biz/agent/semistructured/dictionary").getAbsolutePath();
	//对象采集设置表id
	private final long ODC_ID = PrimayKeyGener.getNextId();
	// 对象采集任务编号
	private final long OCS_ID = PrimayKeyGener.getNextId();
	//获取当前线程ID
	private final long THREAD_ID = Thread.currentThread().getId() * 1000000;

	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 造Object_collect表数据
			List<Object_collect> objectCollectList = getObject_collects();
			objectCollectList.forEach(object_collect ->
					assertThat(Object_collect.TableName + "表初始化测试数据成功",
							object_collect.add(db), is(1))
			);
			// 造object_collect_task表测试数据
			List<Object_collect_task> objectCollectTaskList = getObject_collect_tasks();
			objectCollectTaskList.forEach(object_collect_task ->
					assertThat(Object_collect_task.TableName + "表初始化测试数据成功",
							object_collect_task.add(db), is(1))
			);
			// 造object_collect_struct表测试数据
			List<Object_collect_struct> objectCollectStructList = getObject_collect_structs();
			objectCollectStructList.forEach(object_collect_struct ->
					assertThat(Object_collect_struct.TableName + "表初始化测试数据成功",
							object_collect_struct.add(db), is(1))
			);
			// 提交事务
			SqlOperator.commitTransaction(db);

		}
		// 模拟用户登录
		String responseValue = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("password", PASSWORD)
				.post(LOGIN_URL).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
	}

	private List<Object_collect_struct> getObject_collect_structs() {
		List<Object_collect_struct> objectCollectStructList = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			Object_collect_struct object_collect_struct = new Object_collect_struct();
			object_collect_struct.setStruct_id(PrimayKeyGener.getNextId());
			object_collect_struct.setOcs_id(OCS_ID + 1);
			if (i == 0) {
				object_collect_struct.setColumn_name("case_number");
				object_collect_struct.setData_desc("case_number");
				object_collect_struct.setIs_operate(IsFlag.Shi.getCode());
				object_collect_struct.setColumnposition("fields,case_number");
			} else if (i == 1) {
				object_collect_struct.setColumn_name("ops_flag");
				object_collect_struct.setData_desc("ops_flag");
				object_collect_struct.setIs_operate(IsFlag.Fou.getCode());
				object_collect_struct.setColumnposition("fields,ops_flag");
			} else if (i == 2) {
				object_collect_struct.setColumn_name("status");
				object_collect_struct.setData_desc("status");
				object_collect_struct.setIs_operate(IsFlag.Fou.getCode());
				object_collect_struct.setColumnposition("fields,status");
			} else {
				object_collect_struct.setColumn_name("operate");
				object_collect_struct.setData_desc("operate");
				object_collect_struct.setIs_operate(IsFlag.Fou.getCode());
				object_collect_struct.setColumnposition("fields,operate");
			}
			object_collect_struct.setColumn_type("decimal(38,18)");
			objectCollectStructList.add(object_collect_struct);
		}
		return objectCollectStructList;
	}

	private List<Object_collect_task> getObject_collect_tasks() {
		List<Object_collect_task> objectCollectTaskList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			Object_collect_task object_collect_task = new Object_collect_task();
			object_collect_task.setOcs_id(OCS_ID + i);
			object_collect_task.setDatabase_code(DataBaseCode.UTF_8.getCode());
			object_collect_task.setCollect_data_type(CollectDataType.JSON.getCode());
			object_collect_task.setUpdatetype(UpdateType.DirectUpdate.getCode());
			if (i == 0) {
				object_collect_task.setOdc_id(ODC_ID);
				object_collect_task.setEn_name("no_dictionary");
				object_collect_task.setZh_name("no_dictionary");
				object_collect_task.setFirstline("[{\"date\":\"2019-08-10 0:01:51\",\"operate\":\"UPDATE\"," +
						"\"pos\":\"330617011-bin.008703-886163518\"," +
						"\"identity\":\"870c0361-c7e7-4e65-9109-6583822f22e2\"," +
						"\"host\":\"10.2.6.151\",\"id\":2191991496,\"pk\":\"_id\"," +
						"\"fields\":[{\"eid\":\"562efa91-798e-48fe-aba6-dce42cf8dbe6\"," +
						"\"created_time\":\"1565147701350\",\"case_relation\":\"0\"," +
						"\"amount\":\"2372862\",\"case_date\":\"2019-08-06\",\"u_tags_update\":\"0\"," +
						"\"p_eid\":\"\",\"pid\":\"\",\"type\":\"E\",\"court\":\"上海市宝山区人民法院\"," +
						"\"url\":\"http://zhixing.court.gov.cn/search/\",\"number\":\"91330421753****961P\"," +
						"\"last_update_time\":\"1565366511596\",\"ename\":\"浙江昱辉阳光能源有限公司\"," +
						"\"u_tags\":\"0\",\"name\":\"浙江昱辉阳光能源有限公司\",\"case_id\":\"53846440\"," +
						"\"row_update_time\":\"2019-08-10 00:01:51\",\"p_ename\":\"\"," +
						"\"case_number\":\"（2019）沪0113执4156号\",\"_id\":\"5d4a4235ebf551133e0042b8\"," +
						"\"ops_flag\":\"8\",\"status\":\"0\"},{\"eid\":\"562efa91-798e-48fe-aba6-dce42cf8dbe6\"," +
						"\"created_time\":\"1565147701350\",\"case_relation\":\"0\",\"amount\":\"2372862\"," +
						"\"case_date\":\"2019-08-06\",\"u_tags_update\":\"0\",\"p_eid\":\"\",\"pid\":\"\"," +
						"\"type\":\"E\",\"court\":\"上海市宝山区人民法院\",\"url\":\"http://zhixing.court.gov.cn/search/\"," +
						"\"number\":\"91330421753****961P\",\"last_update_time\":\"1565366511596\"," +
						"\"ename\":\"浙江昱辉阳光能源有限公司\",\"u_tags\":\"0\",\"name\":\"浙江昱辉阳光能源有限公司\"," +
						"\"case_id\":\"53846440\",\"row_update_time\":\"2019-08-10 00:01:51\",\"p_ename\":\"\"," +
						"\"case_number\":\"（2019）沪0113执4156号\",\"_id\":\"5d4a4235ebf551133e0042b8\"," +
						"\"ops_flag\":\"8\",\"status\":\"0\"}],\"db\":\"db_enterprise_other\"," +
						"\"table\":\"no_dictionary\",\"sql\":\"\"}]");
			} else if (i == 1) {
				object_collect_task.setOdc_id(ODC_ID + 1);
				object_collect_task.setZh_name("t_executedpersons");
				object_collect_task.setEn_name("t_executedpersons");
				object_collect_task.setFirstline("");
			} else {
				object_collect_task.setOdc_id(ODC_ID + 1);
				object_collect_task.setZh_name("t_executedpersons2");
				object_collect_task.setEn_name("t_executedpersons2");
				object_collect_task.setFirstline("");
			}
			object_collect_task.setAgent_id(AGENT_ID);
			objectCollectTaskList.add(object_collect_task);
		}
		return objectCollectTaskList;
	}

	private List<Object_collect> getObject_collects() {
		List<Object_collect> objectCollectList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Object_collect object_collect = new Object_collect();
			object_collect.setOdc_id(ODC_ID + i);
			object_collect.setObject_collect_type(ObjectCollectType.HangCaiJi.getCode());
			object_collect.setObj_number("dhwtest" + i + THREAD_ID);
			object_collect.setObj_collect_name("测试对象采集任务名称");
			object_collect.setSystem_name(AGENT_OS_NAME);
			object_collect.setHost_name("mine");
			object_collect.setLocal_time(DateUtil.getDateTime());
			object_collect.setServer_date(DateUtil.getSysDate());
			object_collect.setS_date(DateUtil.getSysDate());
			object_collect.setE_date(Constant.MAXDATE);
			object_collect.setDatabase_code(DataBaseCode.UTF_8.getCode());
			object_collect.setFile_path(filepath);
			object_collect.setIs_sendok(IsFlag.Fou.getCode());
			object_collect.setAgent_id(AGENT_ID);
			object_collect.setIs_dictionary(IsFlag.Shi.getCode());
			if (i == 0) {
				// 无数据字典
				object_collect.setIs_dictionary(IsFlag.Fou.getCode());
				// 无数据字典时的数据日期
				object_collect.setData_date("20200601");
				object_collect.setFile_suffix("dat");
			} else {
				// 有数据字典
				object_collect.setIs_dictionary(IsFlag.Shi.getCode());
				object_collect.setFile_path(filepath);
				object_collect.setData_date("");
				object_collect.setFile_suffix("json");
			}
			objectCollectList.add(object_collect);
		}
		return objectCollectList;
	}

	@Method(desc = "根据对象采集id查询对象采集对应信息的合集(采集文件配置）",
			logicStep = "1.正确的数据访问1，无数据字典" +
					"2.正确的数据访问2，有数据字典" +
					"3.错误的数据访问1，odc_id不存在")
	@Test
	public void searchObjectCollectTask() {
		// 1.正确的数据访问1，无数据字典
		String bodyString = new HttpClient()
				.addData("odc_id", ODC_ID)
				.post(getActionUrl("searchObjectCollectTask"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("is_dictionary"), is(IsFlag.Fou.getCode()));
		List<Object_collect_task> objectCollectTasks =
				JsonUtil.toObject(dataForMap.get("tableInfo").toString(),
						new TypeReference<List<Object_collect_task>>() {
						}.getType());
		assertThat(objectCollectTasks.get(0).getFirstline(), is(notNullValue()));
		assertThat(objectCollectTasks.get(0).getEn_name(), is("no_dictionary"));
		assertThat(objectCollectTasks.get(0).getZh_name(), is("no_dictionary"));
		// 2.正确的数据访问2，有数据字典
		bodyString = new HttpClient()
				.addData("odc_id", ODC_ID + 1)
				.post(getActionUrl("searchObjectCollectTask"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("is_dictionary"), is(IsFlag.Shi.getCode()));
		objectCollectTasks =
				JsonUtil.toObject(dataForMap.get("tableInfo").toString(),
						new TypeReference<List<Object_collect_task>>() {
						}.getType());
		List<String> tableNameList = new ArrayList<>();
		for (Object_collect_task objectCollectTask : objectCollectTasks) {
			tableNameList.add(objectCollectTask.getEn_name());
		}
		assertThat(tableNameList.contains("t_executedpersons"), is(true));
		assertThat(tableNameList.contains("t_executedpersons2"), is(true));
		// 3.错误的数据访问1，odc_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", "123141")
				.post(getActionUrl("searchObjectCollectTask"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "保存采集文件设置信息", logicStep = "1.正确的数据访问1，数据都有效" +
			"2.正确的数据访问1，odc_id不存在" +
			"3.正确的数据访问2，agent_id不存在" +
			"4.正确的数据访问3，update_type不存在" +
			"5.正确的数据访问4，collect_data_type不存在" +
			"6.正确的数据访问5，update_type不存在")
	@Test
	public void saveObjectCollectTask() {
		List<Object_collect_task> objectCollectTaskList = new ArrayList<>();
		for (int i = 1; i < 3; i++) {
			Object_collect_task object_collect_task = new Object_collect_task();
			object_collect_task.setOcs_id(OCS_ID + i);
			if (i == 1) {
				object_collect_task.setEn_name("t_executedpersons");
				object_collect_task.setZh_name("t_executedpersons");
			} else {
				object_collect_task.setEn_name("t_executedpersons2");
				object_collect_task.setZh_name("t_executedpersons2");
			}
			object_collect_task.setUpdatetype(UpdateType.IncrementUpdate.getCode());
			object_collect_task.setCollect_data_type(CollectDataType.XML.getCode());
			object_collect_task.setDatabase_code(DataBaseCode.ISO_8859_1.getCode());
			objectCollectTaskList.add(object_collect_task);
		}
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("odc_id", ODC_ID + 1)
				.addData("agent_id", AGENT_ID)
				.addData("objectCollectTasks", JsonUtil.toJson(objectCollectTaskList))
				.post(getActionUrl("saveObjectCollectTask"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Object_collect_task> objectCollectTasks = SqlOperator.queryList(db, Object_collect_task.class,
					"select * from " + Object_collect_task.TableName + " where odc_id=?",
					ODC_ID + 1);
			for (Object_collect_task objectCollectTask : objectCollectTasks) {
				if (objectCollectTask.getOcs_id() == OCS_ID + 1) {
					assertThat(objectCollectTask.getEn_name(), is("t_executedpersons"));
					assertThat(objectCollectTask.getZh_name(), is("t_executedpersons"));
				} else {
					assertThat(objectCollectTask.getEn_name(), is("t_executedpersons2"));
					assertThat(objectCollectTask.getZh_name(), is("t_executedpersons2"));
				}
				assertThat(objectCollectTask.getDatabase_code(), is(DataBaseCode.ISO_8859_1.getCode()));
				assertThat(objectCollectTask.getCollect_data_type(), is(CollectDataType.XML.getCode()));
				assertThat(objectCollectTask.getUpdatetype(), is(UpdateType.IncrementUpdate.getCode()));
			}
		}
		// 2.正确的数据访问1，odc_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", "123")
				.addData("agent_id", AGENT_ID)
				.addData("objectCollectTasks", JsonUtil.toJson(objectCollectTaskList))
				.post(getActionUrl("saveObjectCollectTask"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.正确的数据访问2，agent_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", ODC_ID + 1)
				.addData("agent_id", "123")
				.addData("objectCollectTasks", JsonUtil.toJson(objectCollectTaskList))
				.post(getActionUrl("saveObjectCollectTask"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.正确的数据访问3，update_type不存在
		objectCollectTaskList = new ArrayList<>();
		for (int i = 1; i < 3; i++) {
			Object_collect_task object_collect_task = new Object_collect_task();
			object_collect_task.setOcs_id(OCS_ID + i);
			if (i == 1) {
				object_collect_task.setEn_name("t_executedpersons");
				object_collect_task.setZh_name("t_executedpersons");
			} else {
				object_collect_task.setEn_name("t_executedpersons2");
				object_collect_task.setZh_name("t_executedpersons2");
			}
			object_collect_task.setUpdatetype("-1");
			object_collect_task.setCollect_data_type(CollectDataType.XML.getCode());
			object_collect_task.setDatabase_code(DataBaseCode.ISO_8859_1.getCode());
			objectCollectTaskList.add(object_collect_task);
		}
		bodyString = new HttpClient()
				.addData("odc_id", ODC_ID + 1)
				.addData("agent_id", AGENT_ID)
				.addData("objectCollectTasks", JsonUtil.toJson(objectCollectTaskList))
				.post(getActionUrl("saveObjectCollectTask"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 5.正确的数据访问4，collect_data_type不存在
		objectCollectTaskList = new ArrayList<>();
		for (int i = 1; i < 3; i++) {
			Object_collect_task object_collect_task = new Object_collect_task();
			object_collect_task.setOcs_id(OCS_ID + i);
			if (i == 1) {
				object_collect_task.setEn_name("t_executedpersons");
				object_collect_task.setZh_name("t_executedpersons");
			} else {
				object_collect_task.setEn_name("t_executedpersons2");
				object_collect_task.setZh_name("t_executedpersons2");
			}
			object_collect_task.setUpdatetype(UpdateType.DirectUpdate.getCode());
			object_collect_task.setCollect_data_type("-1");
			object_collect_task.setDatabase_code(DataBaseCode.ISO_8859_1.getCode());
			objectCollectTaskList.add(object_collect_task);
		}
		bodyString = new HttpClient()
				.addData("odc_id", ODC_ID + 1)
				.addData("agent_id", AGENT_ID)
				.addData("objectCollectTasks", JsonUtil.toJson(objectCollectTaskList))
				.post(getActionUrl("saveObjectCollectTask"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 6.正确的数据访问5，update_type不存在
		objectCollectTaskList = new ArrayList<>();
		for (int i = 1; i < 3; i++) {
			Object_collect_task object_collect_task = new Object_collect_task();
			object_collect_task.setOcs_id(OCS_ID + i);
			if (i == 1) {
				object_collect_task.setEn_name("t_executedpersons");
				object_collect_task.setZh_name("t_executedpersons");
			} else {
				object_collect_task.setEn_name("t_executedpersons2");
				object_collect_task.setZh_name("t_executedpersons2");
			}
			object_collect_task.setUpdatetype(UpdateType.DirectUpdate.getCode());
			object_collect_task.setCollect_data_type(CollectDataType.JSON.getCode());
			object_collect_task.setDatabase_code("-1");
			objectCollectTaskList.add(object_collect_task);
		}
		bodyString = new HttpClient()
				.addData("odc_id", ODC_ID + 1)
				.addData("agent_id", AGENT_ID)
				.addData("objectCollectTasks", JsonUtil.toJson(objectCollectTaskList))
				.post(getActionUrl("saveObjectCollectTask"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "有数据字典时获取半结构化采集列结构信息", logicStep = "1.正确的数据访问1，数据都有效" +
			"2.错误的数据访问1，odc_id不存在" +
			"3.错误的数据访问2，ocs_id不存在" +
			"4.错误的数据访问3，en_name不存在" +
			"5.错误的数据访问4，当前采集任务数据字典不存在")
	@Test
	public void getObjectCollectStruct() {
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("odc_id", ODC_ID + 1)
				.addData("ocs_id", OCS_ID + 1)
				.addData("en_name", "t_executedpersons2")
				.post(getActionUrl("getObjectCollectStruct"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		List<Object_collect_struct> objectCollectStructs = ar.getDataForEntityList(Object_collect_struct.class);
		for (Object_collect_struct objectCollectStruct : objectCollectStructs) {
			String column_name = objectCollectStruct.getColumn_name();
			if ("case_number".equals(column_name)) {
				assertThat(objectCollectStruct.getData_desc(), is("case_number"));
				assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Shi.getCode()));
				assertThat(objectCollectStruct.getColumnposition(), is("fields,case_number"));
				assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
			} else if ("ops_flag".equals(column_name)) {
				assertThat(objectCollectStruct.getData_desc(), is("ops_flag"));
				assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Fou.getCode()));
				assertThat(objectCollectStruct.getColumnposition(), is("fields,ops_flag"));
				assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
			} else if ("status".equals(column_name)) {
				assertThat(objectCollectStruct.getData_desc(), is("status"));
				assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Fou.getCode()));
				assertThat(objectCollectStruct.getColumnposition(), is("fields,status"));
				assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
			} else {
				assertThat(objectCollectStruct.getData_desc(), is("operate"));
				assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Fou.getCode()));
				assertThat(objectCollectStruct.getColumnposition(), is("fields,operate"));
				assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
			}
		}
		// 2.错误的数据访问1，odc_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", "123")
				.addData("ocs_id", OCS_ID + 1)
				.addData("en_name", "t_executedpersons2")
				.post(getActionUrl("getObjectCollectStruct"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，ocs_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", ODC_ID + 1)
				.addData("ocs_id", "123")
				.addData("en_name", "t_executedpersons2")
				.post(getActionUrl("getObjectCollectStruct"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，en_name不存在
		bodyString = new HttpClient()
				.addData("odc_id", ODC_ID + 1)
				.addData("ocs_id", OCS_ID + 1)
				.addData("en_name", "aaa")
				.post(getActionUrl("getObjectCollectStruct"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问4，当前采集任务数据字典不存在
		bodyString = new HttpClient()
				.addData("odc_id", ODC_ID)
				.addData("ocs_id", OCS_ID)
				.addData("en_name", "no_dictionary")
				.post(getActionUrl("getObjectCollectStruct"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "没有数据字典时获取采集列结构(树展示)", logicStep = "1.正确的数据访问1，数据都有效" +
			"2.错误的数据访问1，odc_id不存在" +
			"3.错误的数据访问2，ocs_id不存在" +
			"4.错误的数据访问3，odc_id为有数据字典任务")
	@Test
	public void getFirstLineTreeInfo() {
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("odc_id", ODC_ID)
				.addData("ocs_id", OCS_ID)
				.post(getActionUrl("getFirstLineTreeInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat("解析第一行数据存在", ar.getData(), is(notNullValue()));
		// 2.错误的数据访问1，odc_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", "123")
				.addData("ocs_id", OCS_ID)
				.post(getActionUrl("getFirstLineTreeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，ocs_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", ODC_ID)
				.addData("ocs_id", "123")
				.post(getActionUrl("getFirstLineTreeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，odc_id为有数据字典任务
		bodyString = new HttpClient()
				.addData("odc_id", ODC_ID + 1)
				.addData("ocs_id", OCS_ID + 1)
				.post(getActionUrl("getFirstLineTreeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "无数据字典时查询采集列结构信息(与getFirstLineTreeInfo一起使用)测试",
			logicStep = "1.正确的数据访问1，数据都有效")
	@Test
	public void getObjectCollectStructById() {
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("ocs_id", OCS_ID + 1)
				.post(getActionUrl("getObjectCollectStructById"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		List<Object_collect_struct> objectCollectStructs = ar.getDataForEntityList(Object_collect_struct.class);
		for (Object_collect_struct objectCollectStruct : objectCollectStructs) {
			assertThat(objectCollectStruct.getOcs_id(), is(OCS_ID + 1));
			String column_name = objectCollectStruct.getColumn_name();
			if ("case_number".equals(column_name)) {
				assertThat(objectCollectStruct.getData_desc(), is("case_number"));
				assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Shi.getCode()));
				assertThat(objectCollectStruct.getColumnposition(), is("fields,case_number"));
				assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
			} else if ("ops_flag".equals(column_name)) {
				assertThat(objectCollectStruct.getData_desc(), is("ops_flag"));
				assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Fou.getCode()));
				assertThat(objectCollectStruct.getColumnposition(), is("fields,ops_flag"));
				assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
			} else if ("status".equals(column_name)) {
				assertThat(objectCollectStruct.getData_desc(), is("status"));
				assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Fou.getCode()));
				assertThat(objectCollectStruct.getColumnposition(), is("fields,status"));
				assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
			} else {
				assertThat(objectCollectStruct.getData_desc(), is("operate"));
				assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Fou.getCode()));
				assertThat(objectCollectStruct.getColumnposition(), is("fields,operate"));
				assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
			}
		}
	}

	@Method(desc = "获取有数据字典时操作码表（采集数据处理类型对应表）信息）",
			logicStep = "1.正确的数据访问1，数据都有效" +
					"2.错误的数据访问1，odc_id不存在" +
					"3.错误的数据访问2，en_name不存在" +
					"4.错误的数据访问3，odc_id对应的任务数据字典不存在")
	@Test
	public void searchObjectHandleType() {
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("odc_id", ODC_ID + 1)
				.addData("en_name", "t_executedpersons")
				.post(getActionUrl("searchObjectHandleType"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		List<Object_handle_type> objectHandleTypes = ar.getDataForEntityList(Object_handle_type.class);
		assertThat(objectHandleTypes.size(), is(3));
		for (Object_handle_type objectHandleType : objectHandleTypes) {
			if (OperationType.INSERT == OperationType.ofEnumByCode(objectHandleType.getHandle_type())) {
				assertThat(objectHandleType.getHandle_value(), is("INSERT"));
			} else if (OperationType.UPDATE == OperationType.ofEnumByCode(objectHandleType.getHandle_type())) {
				assertThat(objectHandleType.getHandle_value(), is("UPDATE"));
			} else if (OperationType.DELETE == OperationType.ofEnumByCode(objectHandleType.getHandle_type())) {
				assertThat(objectHandleType.getHandle_value(), is("DELETE"));
			}
		}
		// 2.错误的数据访问1，odc_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", "123")
				.addData("en_name", "t_executedpersons")
				.post(getActionUrl("searchObjectHandleType"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，en_name不存在
		bodyString = new HttpClient()
				.addData("odc_id", ODC_ID + 1)
				.addData("en_name", "aaa")
				.post(getActionUrl("searchObjectHandleType"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，odc_id对应的任务数据字典不存在
		bodyString = new HttpClient()
				.addData("odc_id", ODC_ID)
				.addData("en_name", "no_dictionary")
				.post(getActionUrl("searchObjectHandleType"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "保存操作码表（采集数据处理类型对应表）信息", logicStep = "1.正确的数据访问1，数据都有效" +
			"2.错误的数据访问1，ocs_id不存在")
	@Test
	public void saveObjectHandleType() {
		List<Object_handle_type> objectHandleTypes = new ArrayList<>();
		OperationType[] operationTypes = OperationType.values();
		for (OperationType operationType : operationTypes) {
			Object_handle_type object_handle_type = new Object_handle_type();
			object_handle_type.setHandle_type(operationType.getCode());
			object_handle_type.setHandle_value(operationType.getValue());
			objectHandleTypes.add(object_handle_type);
		}
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("ocs_id", OCS_ID + 1)
				.addData("objectHandleTypes", JsonUtil.toJson(objectHandleTypes))
				.post(getActionUrl("saveObjectHandleType"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Object_handle_type> handleTypeList = Dbo.queryList(db, Object_handle_type.class,
					"select * from " + Object_handle_type.TableName + " where ocs_id=?",
					OCS_ID + 1);
			for (Object_handle_type objectHandleType : handleTypeList) {
				if (OperationType.INSERT == OperationType.ofEnumByCode(objectHandleType.getHandle_type())) {
					assertThat(objectHandleType.getHandle_value(), is("INSERT"));
				} else if (OperationType.UPDATE == OperationType.ofEnumByCode(objectHandleType.getHandle_type())) {
					assertThat(objectHandleType.getHandle_value(), is("UPDATE"));
				} else if (OperationType.DELETE == OperationType.ofEnumByCode(objectHandleType.getHandle_type())) {
					assertThat(objectHandleType.getHandle_value(), is("DELETE"));
				}
			}
		}
		// 2.错误的数据访问1，ocs_id不存在
		bodyString = new HttpClient()
				.addData("ocs_id", "123")
				.addData("objectHandleTypes", JsonUtil.toJson(objectHandleTypes))
				.post(getActionUrl("saveObjectHandleType"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "获取对象采集树节点信息", logicStep = "")
	@Test
	public void getObjectCollectTreeInfo() {
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("odc_id", ODC_ID)
				.addData("ocs_id", OCS_ID)
				.addData("location", "fields")
				.post(getActionUrl("getObjectCollectTreeInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getData(), is(notNullValue()));
	}

	@Method(desc = "保存对象采集结构信息（采集列结构）测试", logicStep = "1.正确的数据访问1，数据都有效" +
			"2.错误的数据访问1，odc_id不存在" +
			"3.错误的数据访问1，ocs_id不存在" +
			"4.错误的数据访问3，操作字段不为一个")
	@Test
	public void saveObjectCollectStruct() {
		List<Object_collect_struct> objectCollectStructList = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			Object_collect_struct object_collect_struct = new Object_collect_struct();
			if (i == 0) {
				object_collect_struct.setColumn_name("case_number");
				object_collect_struct.setData_desc("case_number");
				object_collect_struct.setIs_operate(IsFlag.Shi.getCode());
				object_collect_struct.setColumnposition("fields,case_number");
			} else if (i == 1) {
				object_collect_struct.setColumn_name("ops_flag");
				object_collect_struct.setData_desc("ops_flag");
				object_collect_struct.setIs_operate(IsFlag.Fou.getCode());
				object_collect_struct.setColumnposition("fields,ops_flag");
			} else if (i == 2) {
				object_collect_struct.setColumn_name("status");
				object_collect_struct.setData_desc("status");
				object_collect_struct.setIs_operate(IsFlag.Fou.getCode());
				object_collect_struct.setColumnposition("fields,status");
			} else {
				object_collect_struct.setColumn_name("operate");
				object_collect_struct.setData_desc("operate");
				object_collect_struct.setIs_operate(IsFlag.Fou.getCode());
				object_collect_struct.setColumnposition("fields,operate");
			}
			object_collect_struct.setColumn_type("decimal(38,18)");
			objectCollectStructList.add(object_collect_struct);
		}
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("odc_id", ODC_ID + 1)
				.addData("ocs_id", OCS_ID + 1)
				.addData("objectCollectStructs", JsonUtil.toJson(objectCollectStructList))
				.post(getActionUrl("saveObjectCollectStruct"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Object_collect_struct> objectCollectStructs =
					SqlOperator.queryList(db, Object_collect_struct.class,
							"select * from " + Object_collect_struct.TableName + " where ocs_id=?",
							OCS_ID + 1);
			assertThat(objectCollectStructList.size(), is(4));
			for (Object_collect_struct objectCollectStruct : objectCollectStructs) {
				String column_name = objectCollectStruct.getColumn_name();
				if ("case_number".equals(column_name)) {
					assertThat(objectCollectStruct.getData_desc(), is("case_number"));
					assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Shi.getCode()));
					assertThat(objectCollectStruct.getColumnposition(), is("fields,case_number"));
					assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
				} else if ("ops_flag".equals(column_name)) {
					assertThat(objectCollectStruct.getData_desc(), is("ops_flag"));
					assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Fou.getCode()));
					assertThat(objectCollectStruct.getColumnposition(), is("fields,ops_flag"));
					assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
				} else if ("status".equals(column_name)) {
					assertThat(objectCollectStruct.getData_desc(), is("status"));
					assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Fou.getCode()));
					assertThat(objectCollectStruct.getColumnposition(), is("fields,status"));
					assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
				} else {
					assertThat(objectCollectStruct.getData_desc(), is("operate"));
					assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Fou.getCode()));
					assertThat(objectCollectStruct.getColumnposition(), is("fields,operate"));
					assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
				}
			}
		}
		// 2.错误的数据访问1，odc_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", "123")
				.addData("ocs_id", OCS_ID + 1)
				.addData("objectCollectStructs", JsonUtil.toJson(objectCollectStructList))
				.post(getActionUrl("saveObjectCollectStruct"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，ocs_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", ODC_ID + 1)
				.addData("ocs_id", "123")
				.addData("objectCollectStructs", JsonUtil.toJson(objectCollectStructList))
				.post(getActionUrl("saveObjectCollectStruct"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，操作字段不为一个
		objectCollectStructList = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			Object_collect_struct object_collect_struct = new Object_collect_struct();
			if (i == 0) {
				object_collect_struct.setColumn_name("case_number");
				object_collect_struct.setData_desc("case_number");
				object_collect_struct.setIs_operate(IsFlag.Shi.getCode());
				object_collect_struct.setColumnposition("fields,case_number");
			} else if (i == 1) {
				object_collect_struct.setColumn_name("ops_flag");
				object_collect_struct.setData_desc("ops_flag");
				object_collect_struct.setIs_operate(IsFlag.Shi.getCode());
				object_collect_struct.setColumnposition("fields,ops_flag");
			} else if (i == 2) {
				object_collect_struct.setColumn_name("status");
				object_collect_struct.setData_desc("status");
				object_collect_struct.setIs_operate(IsFlag.Fou.getCode());
				object_collect_struct.setColumnposition("fields,status");
			} else {
				object_collect_struct.setColumn_name("operate");
				object_collect_struct.setData_desc("operate");
				object_collect_struct.setIs_operate(IsFlag.Fou.getCode());
				object_collect_struct.setColumnposition("fields,operate");
			}
			object_collect_struct.setColumn_type("decimal(38,18)");
			objectCollectStructList.add(object_collect_struct);
		}
		bodyString = new HttpClient()
				.addData("odc_id", ODC_ID + 1)
				.addData("ocs_id", OCS_ID + 1)
				.addData("objectCollectStructs", JsonUtil.toJson(objectCollectStructList))
				.post(getActionUrl("saveObjectCollectStruct"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@After
	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 删除测试用例造的Object_collect表数据
			SqlOperator.execute(db,
					"DELETE FROM " + Object_collect.TableName + " WHERE agent_id = ?"
					, AGENT_ID);
			long num = SqlOperator.queryNumber(db,
					"select count (*) from " + Object_collect.TableName + " where agent_id=?",
					AGENT_ID)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(Object_collect.TableName + "表测试数据已删除", num, is(0L));
			// 删除测试用例造的Object_collect_task表数据
			SqlOperator.execute(db,
					"DELETE FROM " + Object_collect_task.TableName + " WHERE odc_id in(?,?)"
					, ODC_ID, ODC_ID + 1);
			num = SqlOperator.queryNumber(db,
					"select count (*) from " + Object_collect_task.TableName + " where odc_id in(?,?)",
					ODC_ID, ODC_ID + 1)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(Object_collect_task.TableName + "表测试数据已删除", num, is(0L));
			// 删除测试用例造的Object_collect_task表数据
			SqlOperator.execute(db,
					"DELETE FROM " + Object_collect_struct.TableName + " WHERE ocs_id =?"
					, OCS_ID + 1);
			num = SqlOperator.queryNumber(db,
					"select count (*) from " + Object_collect_struct.TableName + " where ocs_id =?",
					OCS_ID + 1)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(Object_collect_struct.TableName + "表测试数据已删除", num, is(0L));
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
	}
}