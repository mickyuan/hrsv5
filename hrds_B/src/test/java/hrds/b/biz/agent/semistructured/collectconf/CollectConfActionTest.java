package hrds.b.biz.agent.semistructured.collectconf;

import com.alibaba.fastjson.JSON;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.ObjectCollectType;
import hrds.commons.codes.UpdateType;
import hrds.commons.entity.Object_collect;
import hrds.commons.entity.Object_collect_task;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "半结构化采集配置测试类", author = "dhw", createdate = "2020/6/17 10:53")
public class CollectConfActionTest extends WebBaseTestCase {

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
	private static final File DICTINARYFILE = FileUtil.getFile(System.getProperty("user_dir")
			+ "/hrds_B/src/test/java/hrds/b/biz/agent/semistructured/dictionary");
	// 无数据字典时的数据日期
	private static final String DATA_DATE = "20200601";
	//对象采集设置表id
	private static final long ODC_ID = PrimayKeyGener.getNextId();
	//获取当前线程ID
	private static final long THREAD_ID = Thread.currentThread().getId() * 1000000;

	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 造Object_collect表数据
			List<Object_collect> objectCollectList = getObject_collects();
			objectCollectList.forEach(object_collect ->
					assertThat(Object_collect.TableName + "表初始化测试数据成功", object_collect.add(db), is(1))
			);
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
		// 模拟用户登录
		String responseValue = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("password", PASSWORD)
				.post(LOGIN_URL)
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));

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
			object_collect.setFile_path(DICTINARYFILE.getAbsolutePath());
			object_collect.setIs_sendok(IsFlag.Fou.getCode());
			object_collect.setAgent_id(AGENT_ID);
			object_collect.setIs_dictionary(IsFlag.Shi.getCode());
			if (i == 0) {
				// 无数据字典
				object_collect.setIs_dictionary(IsFlag.Fou.getCode());
				object_collect.setData_date(DATA_DATE);
				object_collect.setFile_suffix("dat");
			} else {
				// 有数据字典
				object_collect.setIs_dictionary(IsFlag.Shi.getCode());
				object_collect.setFile_path(DICTINARYFILE.getAbsolutePath());
				object_collect.setData_date("");
				object_collect.setFile_suffix("json");
			}
			objectCollectList.add(object_collect);
		}
		return objectCollectList;
	}

	@Method(desc = "获取新增半结构化采集配置信息",
			logicStep = "1.正确的数据访问1，agent_id为正确造数据的值" +
					"2.错误的数据访问1，agent_id为空" +
					"3.错误的数据访问2，agent_id不存在" +
					"备注：该方法只有三种情况")
	@Test
	public void getAddObjectCollectConf() {
		// 1.正确的数据访问1，agent_id为正确造数据的值
		String bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.post(getActionUrl("getAddObjectCollectConf"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("osName"), is(notNullValue()));
		assertThat(ar.getDataForMap().get("userName"), is(notNullValue()));
		assertThat(ar.getDataForMap().get("localDate"), is(DateUtil.getSysDate()));
		assertThat(ar.getDataForMap().get("agentdate"), is(DateUtil.getSysDate()));
		// 2.错误的数据访问1，agent_id为空
		bodyString = new HttpClient()
				.addData("agent_id", "")
				.post(getActionUrl("getAddObjectCollectConf"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，agent_id不存在
		bodyString = new HttpClient()
				.addData("agent_id", "111")
				.post(getActionUrl("getAddObjectCollectConf"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "根据对象采集id获取半结构化采集配置信息（编辑任务时数据回显）",
			logicStep = "1.正确的数据访问1，odc_id为正确造数据的值" +
					"2.错误的数据访问1，odc_id为空" +
					"3.错误的数据访问2，odc_id不存在" +
					"备注：该方法只有三种情况")
	@Test
	public void getObjectCollectConfById() {
		// 1.正确的数据访问1，odc_id为正确造数据的值
		String bodyString = new HttpClient()
				.addData("odc_id", ODC_ID)
				.post(getActionUrl("getObjectCollectConfById"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		Object_collect object_collect = JSON.parseObject(JSON.toJSONString(ar.getDataForMap()),
				Object_collect.class);
		// 验证数据的正确性
		assertThat(object_collect.getSystem_name(), is(AGENT_OS_NAME));
		assertThat(object_collect.getObject_collect_type(), is(ObjectCollectType.HangCaiJi.getCode()));
		assertThat(object_collect.getObj_collect_name(), is("测试对象采集任务名称"));
		assertThat(object_collect.getFile_path(), is(DICTINARYFILE.getAbsolutePath()));
		assertThat(object_collect.getAgent_id(), is(AGENT_ID));
		assertThat(object_collect.getIs_dictionary(), is(IsFlag.Fou.getCode()));
		assertThat(object_collect.getData_date(), is(DATA_DATE));
		assertThat(object_collect.getOdc_id(), is(ODC_ID));
		assertThat(object_collect.getDatabase_code(), is(DataBaseCode.UTF_8.getCode()));
		assertThat(object_collect.getFile_suffix(), is("json"));
		assertThat(object_collect.getObj_number(), is("测试对象采集编号0" + THREAD_ID));
		// 2.错误的数据访问1，odc_id为空
		bodyString = new HttpClient()
				.addData("odc_id", "")
				.post(getActionUrl("getObjectCollectConfById"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，odc_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", "123")
				.post(getActionUrl("getObjectCollectConfById"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "查看表", logicStep = "1.正确的数据访问1，数据都有效，数据字典不存在" +
			"2.正确的数据访问2，数据都有效，数据字典存在" +
			"3.错误的数据访问1，agent_id不存在" +
			"4.错误的数据访问2，file_path不存在" +
			"5.错误的数据访问3，is_dictionary不存在" +
			"6.错误的数据访问4，file_suffix不存在" +
			"7.错误的数据访问5，当是否存在数据字典是否的时候，数据日期为空" +
			"8.错误的数据访问6，当是否存在数据字典是否的时候，指定目录下没有文件后缀名为aaa的文件" +
			"9.错误的数据访问7，当是否存在数据字典是否的时候，指定日期目录下没有文件")
	@Test
	public void viewTable() {
		// 1.正确的数据访问1，数据都有效，数据字典不存在
		String bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("file_path", DICTINARYFILE.getAbsolutePath())
				.addData("is_dictionary", IsFlag.Fou.getCode())
				.addData("data_date", DATA_DATE)
				.addData("file_suffix", "dat")
				.post(getActionUrl("viewTable"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		List<Object_collect_task> objectCollectTaskList = ar.getDataForEntityList(Object_collect_task.class);
		assertThat(objectCollectTaskList.size(), is(1));
		objectCollectTaskList.forEach(object_collect_task -> {
			assertThat(object_collect_task.getEn_name(), is("no_dictionary"));
			assertThat(object_collect_task.getZh_name(), is("no_dictionary"));
			assertThat(object_collect_task.getFirstline(), is(notNullValue()));
		});
		// 2.正确的数据访问2，数据都有效，数据字典存在
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("file_path", DICTINARYFILE.getAbsolutePath())
				.addData("is_dictionary", IsFlag.Shi.getCode())
				.addData("file_suffix", "json")
				.post(getActionUrl("viewTable"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		objectCollectTaskList = ar.getDataForEntityList(Object_collect_task.class);
		objectCollectTaskList.forEach(object_collect_task -> {
			assertThat(object_collect_task.getEn_name(), is("t_executedpersons"));
			assertThat(object_collect_task.getZh_name(), is("t_executedpersons"));
			assertThat(object_collect_task.getUpdatetype(), is(UpdateType.DirectUpdate.getCode()));
			assertThat(object_collect_task.getFirstline(), is(nullValue()));
		});
		// 3.错误的数据访问1，agent_id不存在
		bodyString = new HttpClient()
				.addData("agent_id", "111")
				.addData("file_path", DICTINARYFILE.getAbsolutePath())
				.addData("is_dictionary", IsFlag.Shi.getCode())
				.addData("file_suffix", "json")
				.post(getActionUrl("viewTable")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问2，file_path不存在
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("file_path", "/aaa")
				.addData("is_dictionary", IsFlag.Shi.getCode())
				.addData("file_suffix", "json")
				.post(getActionUrl("viewTable")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问3，is_dictionary不存在
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("file_path", DICTINARYFILE.getAbsolutePath())
				.addData("is_dictionary", "2")
				.addData("file_suffix", "json")
				.post(getActionUrl("viewTable")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 6.错误的数据访问4，file_suffix为空
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("file_path", DICTINARYFILE.getAbsolutePath())
				.addData("is_dictionary", IsFlag.Fou.getCode())
				.addData("file_suffix", "")
				.post(getActionUrl("viewTable")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 7.错误的数据访问5，当是否存在数据字典是否的时候，数据日期为空
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("file_path", DICTINARYFILE.getAbsolutePath())
				.addData("is_dictionary", IsFlag.Fou.getCode())
				.addData("file_suffix", "json")
				.post(getActionUrl("viewTable")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 8.错误的数据访问6，当是否存在数据字典是否的时候，指定目录下没有文件后缀名为aaa的文件
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("file_path", DICTINARYFILE.getAbsolutePath())
				.addData("is_dictionary", IsFlag.Fou.getCode())
				.addData("data_date", DATA_DATE)
				.addData("file_suffix", "txt")
				.post(getActionUrl("viewTable")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 9.错误的数据访问7，当是否存在数据字典是否的时候，指定日期目录下没有文件
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("file_path", DICTINARYFILE.getAbsolutePath())
				.addData("is_dictionary", IsFlag.Fou.getCode())
				.addData("data_date", "20200101")
				.addData("file_suffix", "dat")
				.post(getActionUrl("viewTable")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "保存半结构化文件采集页面信息到对象采集设置表对象，同时返回对象采集id",
			logicStep = "")
	@Test
	public void saveObjectCollect() {
		// 1.正确的数据访问1，新增半结构化采集配置,无数据字典
		String bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("file_path", DICTINARYFILE.getAbsolutePath())
				.addData("is_dictionary", IsFlag.Fou.getCode())
				.addData("data_date", "20200601")
				.addData("file_suffix", "dat")
				.addData("object_collect_type", ObjectCollectType.HangCaiJi.getCode())
				.addData("obj_number", "test_dhw1")
				.addData("obj_collect_name", "test_dhw1")
				.addData("system_name", "windows 10")
				.addData("host_name", "mine")
				.addData("local_time", DateUtil.getDateTime())
				.addData("server_date", DateUtil.getDateTime())
				.addData("s_date", DateUtil.getSysDate())
				.addData("e_date", Constant.MAXDATE)
				.addData("database_code", DataBaseCode.UTF_8.getCode())
				.addData("is_sendok", IsFlag.Fou.getCode())
				.post(getActionUrl("saveObjectCollect"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证数据是否正常入库
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Object_collect object_collect = SqlOperator.queryOneObject(db, Object_collect.class,
					"select * from " + Object_collect.TableName + " where agent_id=? and obj_number=?",
					AGENT_ID, "test_dhw1")
					.orElseThrow(() -> new BusinessException("sql查询错误或映射实体失败！"));
			assertThat(object_collect.getHost_name(), is("mine"));
			assertThat(object_collect.getSystem_name(), is("windows 10"));
			assertThat(object_collect.getObj_number(), is("test_dhw1"));
			assertThat(object_collect.getFile_suffix(), is("dat"));
			assertThat(object_collect.getDatabase_code(), is(DataBaseCode.UTF_8.getCode()));
			assertThat(object_collect.getOdc_id(), is(Long.valueOf(ar.getData().toString())));
			assertThat(object_collect.getIs_dictionary(), is(IsFlag.Fou.getCode()));
			assertThat(object_collect.getFile_path(), is(DICTINARYFILE.getAbsolutePath()));
			assertThat(object_collect.getObj_collect_name(), is("test_dhw1"));
			assertThat(object_collect.getObject_collect_type(), is(ObjectCollectType.HangCaiJi.getCode()));
			assertThat(object_collect.getData_date(), is(DATA_DATE));
			assertThat(object_collect.getIs_sendok(), is(IsFlag.Fou.getCode()));
			assertThat(object_collect.getE_date(), is(Constant.MAXDATE));
			assertThat(object_collect.getS_date(), is(DateUtil.getSysDate()));
		}
		// 2.正确的数据访问1，新增半结构化采集配置,有数据字典
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("file_path", DICTINARYFILE.getAbsolutePath())
				.addData("is_dictionary", IsFlag.Shi.getCode())
				.addData("file_suffix", "json")
				.addData("object_collect_type", ObjectCollectType.HangCaiJi.getCode())
				.addData("obj_number", "test_dhw2")
				.addData("obj_collect_name", "test_dhw2")
				.addData("system_name", "windows 10")
				.addData("host_name", "mine")
				.addData("local_time", DateUtil.getDateTime())
				.addData("server_date", DateUtil.getDateTime())
				.addData("s_date", DateUtil.getSysDate())
				.addData("e_date", Constant.MAXDATE)
				.addData("database_code", DataBaseCode.UTF_8.getCode())
				.addData("is_sendok", IsFlag.Fou.getCode())
				.post(getActionUrl("saveObjectCollect"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证数据是否正常入库
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Object_collect object_collect = SqlOperator.queryOneObject(db, Object_collect.class,
					"select * from " + Object_collect.TableName + " where agent_id=? and obj_number=?",
					AGENT_ID, "test_dhw2")
					.orElseThrow(() -> new BusinessException("sql查询错误或映射实体失败！"));
			assertThat(object_collect.getHost_name(), is("mine"));
			assertThat(object_collect.getSystem_name(), is("windows 10"));
			assertThat(object_collect.getObj_number(), is("test_dhw2"));
			assertThat(object_collect.getFile_suffix(), is("json"));
			assertThat(object_collect.getDatabase_code(), is(DataBaseCode.UTF_8.getCode()));
			assertThat(object_collect.getOdc_id(), is(Long.valueOf(ar.getData().toString())));
			assertThat(object_collect.getIs_dictionary(), is(IsFlag.Shi.getCode()));
			assertThat(object_collect.getFile_path(), is(DICTINARYFILE.getAbsolutePath()));
			assertThat(object_collect.getObj_collect_name(), is("test_dhw2"));
			assertThat(object_collect.getObject_collect_type(), is(ObjectCollectType.HangCaiJi.getCode()));
			assertThat(object_collect.getIs_sendok(), is(IsFlag.Fou.getCode()));
			assertThat(object_collect.getE_date(), is(Constant.MAXDATE));
			assertThat(object_collect.getS_date(), is(DateUtil.getSysDate()));
		}
		// 3.正确的数据访问3，编辑半结构化采集配置
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("file_path", DICTINARYFILE.getAbsolutePath())
				.addData("is_dictionary", IsFlag.Shi.getCode())
				.addData("file_suffix", "json")
				.addData("object_collect_type", ObjectCollectType.HangCaiJi.getCode())
				.addData("obj_number", "test_dhw2")
				.addData("obj_collect_name", "test_dhw2")
				.addData("system_name", "windows 10")
				.addData("host_name", "mine")
				.addData("local_time", DateUtil.getDateTime())
				.addData("server_date", DateUtil.getDateTime())
				.addData("s_date", DateUtil.getSysDate())
				.addData("e_date", Constant.MAXDATE)
				.addData("database_code", DataBaseCode.UTF_8.getCode())
				.addData("is_sendok", IsFlag.Fou.getCode())
				.post(getActionUrl("saveObjectCollect"))
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
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
	}
}