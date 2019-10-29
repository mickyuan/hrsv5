package hrds.b.biz.agent.objectcollect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * 对象采集接口测试用例
 */
public class ObjectCollectActionTest extends WebBaseTestCase {

	private static String bodyString;
	private static ActionResult ar;
	//向object_collect表中初始化的数据条数
	private static final long OBJECT_COLLECT_ROWS = 2L;
	//向object_collect_task表中初始化的数据条数
	private static final long OBJECT_COLLECT_TASK_ROWS = 10L;
	//向object_storage表中初始化的数据条数
	private static final long OBJECT_STORAGE_ROWS = 10L;
	//向object_collect_struct表中初始化的数据条数
	private static final long OBJECT_COLLECT_STRUCT_ROWS = 10L;
	//Agent信息表id
	private static final long AGENT_ID = 10000001L;
	//用户id
	private static final long USER_ID = 9001L;
	//部门ID
	private static final long DEPT_ID = 9002L;
	//对象采集设置表id
	private static final long ODC_ID = 20000001L;
	//对象采集对应信息表任务
	private static final long OCS_ID = 30000001L;
	//对象采集存储设置表存储编号
	private static final long OBJ_STID = 40000001L;
	//对象采集结构信息表结构信息id
	private static final long STRUCT_ID = 50000001L;

	/**
	 * 测试用例初始化参数
	 * <p>
	 * 1.造sys_user表数据，用于模拟用户登录
	 * 2.造部门表数据，用于模拟用户登录
	 * 3.造agent_down_info表数据，默认为1条，AGENT_ID为10000001
	 * 4.造Object_collect表数据，默认为2条,ODC_ID为20000001---20000002
	 * 5.造object_collect_task表数据，默认为10条,OCS_ID为30000001---30000010
	 * 6.造object_storage表数据，默认为10条,OBJ_STID为40000001---40000010
	 * 7.造object_collect_struct表数据，默认为10条,STRUCT_ID为50000001---50000010
	 * 8.模拟用户登录
	 */
	@Before
	public void beforeTest() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1.造sys_user表数据，用于模拟用户登录
			Sys_user user = new Sys_user();
			user.setUser_id(USER_ID);
			user.setCreate_id(USER_ID);
			user.setRole_id(USER_ID);
			user.setUser_name("测试用户(9001)");
			user.setUser_password("1");
			user.setUseris_admin(IsFlag.Shi.getCode());
			user.setUser_state(IsFlag.Shi.getCode());
			user.setCreate_date(DateUtil.getSysDate());
			user.setCreate_time(DateUtil.getSysTime());
			user.setToken("0");
			user.setValid_time("0");
			user.setDep_id(DEPT_ID);
			assertThat("初始化数据成功", user.add(db), is(1));
			//2.造部门表数据，用于模拟用户登录
			Department_info deptInfo = new Department_info();
			deptInfo.setDep_id(DEPT_ID);
			deptInfo.setDep_name("测试系统参数类部门init-zxz");
			deptInfo.setCreate_date(DateUtil.getSysDate());
			deptInfo.setCreate_time(DateUtil.getSysTime());
			deptInfo.setDep_remark("测试系统参数类部门init-zxz");
			assertThat("初始化数据成功", deptInfo.add(db), is(1));
			//3.造agent_down_info表数据，默认为1条，AGENT_ID为10000001
			Agent_down_info agent_info = new Agent_down_info();
			agent_info.setDown_id(PrimayKeyGener.getNextId());
			agent_info.setUser_id(USER_ID);
			agent_info.setAgent_id(AGENT_ID);
			agent_info.setAgent_ip("127.0.0.1");
			agent_info.setAgent_port("56000");
			agent_info.setAgent_type(AgentType.ShuJuKu.getCode());
			agent_info.setAgent_name("非结构化采集Agent");
			agent_info.setSave_dir("/aaa/ccc/");
			agent_info.setLog_dir("/aaa/ccc/log");
			agent_info.setDeploy(IsFlag.Shi.getCode());
			agent_info.setAgent_context("/agent");
			agent_info.setAgent_pattern("/receive/*");
			agent_info.setRemark("测试用例清除数据专用列");
			assertThat("初始化数据成功", agent_info.add(db), is(1));
			//4.造Object_collect表数据，默认为2条,ODC_ID为20000001---20000002
			for (int i = 0; i < OBJECT_COLLECT_ROWS; i++) {
				Object_collect object_collect = new Object_collect();
				object_collect.setOdc_id(ODC_ID + i);
				object_collect.setObject_collect_type(ObjectCollectType.HangCaiJi.getCode());
				object_collect.setObj_number("测试对象采集编号");
				object_collect.setObj_collect_name("测试对象采集名称" + i);
				object_collect.setSystem_name("Windows 10");
				object_collect.setHost_name("zhuxi");
				object_collect.setLocal_time(DateUtil.getDateTime());
				object_collect.setServer_date(DateUtil.getSysDate());
				object_collect.setS_date(DateUtil.getSysDate());
				object_collect.setE_date(DateUtil.getSysDate());
				object_collect.setDatabase_code(DataBaseCode.UTF_8.getCode());
				object_collect.setRun_way(ExecuteWay.MingLingChuFa.getCode());
				object_collect.setFile_path("/aaaa/ccc/ddd");
				object_collect.setIs_sendok(IsFlag.Fou.getCode());
				object_collect.setAgent_id(AGENT_ID);
				assertThat("初始化数据成功", object_collect.add(db), is(1));
			}
			//5.造object_collect_task表数据，默认为10条,OCS_ID为30000001---30000010
			for (int i = 0; i < OBJECT_COLLECT_TASK_ROWS; i++) {
				Object_collect_task object_collect_task = new Object_collect_task();
				object_collect_task.setOcs_id(OCS_ID + i);
				object_collect_task.setAgent_id(AGENT_ID);
				object_collect_task.setEn_name("aaa" + i);
				object_collect_task.setZh_name("测试aaa" + i);
				object_collect_task.setCollect_data_type(CollectDataType.JSON.getCode());
				object_collect_task.setDatabase_code(DataBaseCode.UTF_8.getCode());
				object_collect_task.setOdc_id(ODC_ID);
				assertThat("初始化数据成功", object_collect_task.add(db), is(1));
			}
			//6.造object_storage表数据，默认为10条,OBJ_STID为40000001---40000010
			for (int i = 0; i < OBJECT_STORAGE_ROWS; i++) {
				Object_storage object_storage = new Object_storage();
				object_storage.setObj_stid(OBJ_STID + i);
				object_storage.setIs_hbase(IsFlag.Fou.getCode());
				object_storage.setIs_hdfs(IsFlag.Shi.getCode());
				object_storage.setOcs_id(OCS_ID + i + 3);
				object_storage.setRemark("zxz测试用例清除表object_storage专用");
				assertThat("初始化数据成功", object_storage.add(db), is(1));
			}
			//7.造object_collect_struct表数据，默认为10条,STRUCT_ID为50000001---50000010
			for (int i = 0; i < OBJECT_COLLECT_STRUCT_ROWS; i++) {
				Object_collect_struct object_collect_struct = new Object_collect_struct();
				object_collect_struct.setStruct_id(STRUCT_ID + i);
				object_collect_struct.setOcs_id(OCS_ID);
				object_collect_struct.setColl_name("testcol" + i);
				object_collect_struct.setData_desc("测试对象中文描述" + i);
				object_collect_struct.setStruct_type(ObjectDataType.ZiFuChuan.getCode());
				assertThat("初始化数据成功", object_collect_struct.add(db), is(1));
			}
			SqlOperator.commitTransaction(db);
		}
		//8.模拟用户登录
		String responseValue = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("password", "1")
				.post("http://127.0.0.1:8099/A/action/hrds/a/biz/login/login").getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
	}

	/**
	 * searchObjectCollect方法测试用例
	 * <p>
	 * 1.agent_id不为空，odc_id为空查询信息，agent_id为正确造数据的值
	 * 2.agent_id不为空，odc_id为空查询信息，agent_id为不正确的值
	 * 3.agent_id不为空，odc_id也不为空查询信息，agent_id和odc_id为正确造数据的值
	 * 4.agent_id不为空，odc_id也不为空查询信息，agent_id为正确造数据的值和odc_id为不正确的值
	 */
	@Test
	public void searchObjectCollectTest() {
		//1.agent_id不为空，odc_id为空查询信息，agent_id为正确造数据的值
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.post(getActionUrl("searchObjectCollect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(StringUtil.isBlank(ar.getDataForMap().get("osName").toString()), is(false));

		//2.agent_id不为空，odc_id为空查询信息，agent_id为不正确的值
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID + 111)
				.post(getActionUrl("searchObjectCollect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));

		//3.agent_id不为空，odc_id也不为空查询信息，agent_id和odc_id为正确造数据的值
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("odc_id", ODC_ID)
				.post(getActionUrl("searchObjectCollect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("localdate"), is(DateUtil.getSysDate()));
		assertThat(StringUtil.isBlank(ar.getDataForMap().get("object_collect_info").toString()), is(false));

		//4.agent_id不为空，odc_id也不为空查询信息，agent_id为正确造数据的值和odc_id为不正确的值
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("odc_id", ODC_ID + 111)
				.post(getActionUrl("searchObjectCollect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	/**
	 * addObjectCollect保存对象采集设置表测试用例
	 * <p>
	 * 1.添加一个正确的半结构化采集设置表
	 * 2.添加一个半结构化采集，但ftp采集任务名称重复
	 * 3.添加一个半结构化采集，但是file_path格式不正确
	 * 4.添加一个半结构化采集，但是database_code格式不正确
	 */
	@Test
	public void addObjectCollectTest() {
		//1.添加一个正确的半结构化采集设置表
		bodyString = new HttpClient()
				.addData("object_collect_type", ObjectCollectType.HangCaiJi.getCode())
				.addData("obj_number", "qqwwtt")
				.addData("obj_collect_name", "测试对象采集名称1112")
				.addData("system_name", "Windows 10")
				.addData("host_name", "zhuxi")
				.addData("local_time", DateUtil.getDateTime())
				.addData("server_date", DateUtil.getSysDate())
				.addData("s_date", DateUtil.getSysDate())
				.addData("e_date", DateUtil.getSysDate())
				.addData("database_code", DataBaseCode.UTF_8.getCode())
				.addData("run_way", ExecuteWay.MingLingChuFa.getCode())
				.addData("file_path", "/aaaa/ccc/ddd")
				.addData("is_sendok", IsFlag.Fou.getCode())
				.addData("agent_id", AGENT_ID)
				.post(getActionUrl("addObjectCollect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long optionalLong = SqlOperator.queryNumber(db, "select count(1) count from "
					+ Object_collect.TableName + " WHERE agent_id = ?", AGENT_ID).orElseThrow(
					() -> new BusinessException("该查询有且仅有一条数据"));
			assertThat("校验object_collect表数据量正确", optionalLong, is(OBJECT_COLLECT_ROWS + 1));
			Object_collect collect = SqlOperator.queryOneObject(db, Object_collect.class, "select * from "
					+ Object_collect.TableName + " WHERE obj_collect_name = ?", "测试对象采集名称1112")
					.orElseThrow(() -> new BusinessException("测试用例异常"));
			assertThat("校验object_collect表数据量正确", collect.getFile_path()
					, is("/aaaa/ccc/ddd"));
			assertThat("校验object_collect表数据量正确", collect.getObj_number()
					, is("qqwwtt"));
		}

		//2.添加一个半结构化采集，但ftp采集任务名称重复
		bodyString = new HttpClient()
				.addData("object_collect_type", ObjectCollectType.HangCaiJi.getCode())
				.addData("obj_number", "qqww")
				.addData("obj_collect_name", "测试对象采集名称1")
				.addData("system_name", "Windows 10")
				.addData("host_name", "zhuxi")
				.addData("local_time", DateUtil.getDateTime())
				.addData("server_date", DateUtil.getSysDate())
				.addData("s_date", DateUtil.getSysDate())
				.addData("e_date", DateUtil.getSysDate())
				.addData("database_code", DataBaseCode.UTF_8.getCode())
				.addData("run_way", ExecuteWay.MingLingChuFa.getCode())
				.addData("file_path", "/aaaa/ccc/ddd")
				.addData("is_sendok", IsFlag.Fou.getCode())
				.addData("agent_id", AGENT_ID)
				.post(getActionUrl("addObjectCollect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));

//		//3.添加一个半结构化采集，但是file_path格式不正确
//		bodyString = new HttpClient()
//				.addData("object_collect_type", ObjectCollectType.HangCaiJi.getCode())
//				.addData("obj_number", "qqwwttr")
//				.addData("obj_collect_name", "测试对象采集名称qwerr222")
//				.addData("system_name", "Windows 10")
//				.addData("host_name", "zhuxi")
//				.addData("local_time", DateUtil.getDateTime())
//				.addData("server_date", DateUtil.getSysDate())
//				.addData("s_date", DateUtil.getSysDate())
//				.addData("e_date", DateUtil.getSysDate())
//				.addData("database_code", DataBaseCode.UTF_8.getCode())
//				.addData("run_way", ExecuteWay.MingLingChuFa.getCode())
//				.addData("file_path", "adsad,cacsadwqwq/qweqdsaa\\asdas\\sad")
//				.addData("is_sendok", IsFlag.Fou.getCode())
//				.addData("agent_id", AGENT_ID)
//				.post(getActionUrl("addObjectCollect")).getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败！"));
//		assertThat(ar.isSuccess(), is(false));
//
//		//4.添加一个半结构化采集，但是database_code格式不正确
//		bodyString = new HttpClient()
//				.addData("object_collect_type", ObjectCollectType.HangCaiJi.getCode())
//				.addData("obj_number", "qqwwttyy")
//				.addData("obj_collect_name", "测试对象采集名称qwerr")
//				.addData("system_name", "Windows 10")
//				.addData("host_name", "zhuxi")
//				.addData("local_time", DateUtil.getDateTime())
//				.addData("server_date", DateUtil.getSysDate())
//				.addData("s_date", DateUtil.getSysDate())
//				.addData("e_date", DateUtil.getSysDate())
//				.addData("database_code", "UTF-8")
//				.addData("run_way", ExecuteWay.MingLingChuFa.getCode())
//				.addData("file_path", "/aaaa/ccc/ddd")
//				.addData("is_sendok", IsFlag.Fou.getCode())
//				.addData("agent_id", AGENT_ID)
//				.post(getActionUrl("addObjectCollect")).getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败！"));
//		assertThat(ar.isSuccess(), is(false));
	}

	/**
	 * updateObjectCollect更新对象采集设置表测试用例
	 * <p>
	 * 1.更新一个正确的半结构化采集设置表
	 * 2.更新一个半结构化采集，但ftp采集任务名称重复和其他任务名称重复
	 * 3.更新一个半结构化采集，但是run_way格式不正确
	 * 4.更新一个半结构化采集，但是obj_number格式不正确
	 */
	@Test
	public void updateObjectCollectTest() {
		//1.更新一个正确的半结构化采集设置表
		bodyString = new HttpClient()
				.addData("odc_id", ODC_ID)
				.addData("object_collect_type", ObjectCollectType.HangCaiJi.getCode())
				.addData("obj_number", "hahahahxianshi")
				.addData("obj_collect_name", "测试对象采集编号0")
				.addData("system_name", "Windows 10")
				.addData("host_name", "zhuxi")
				.addData("local_time", DateUtil.getDateTime())
				.addData("server_date", DateUtil.getSysDate())
				.addData("s_date", DateUtil.getSysDate())
				.addData("e_date", DateUtil.getSysDate())
				.addData("database_code", DataBaseCode.UTF_8.getCode())
				.addData("run_way", ExecuteWay.MingLingChuFa.getCode())
				.addData("file_path", "/aaaa/ccc/ddd")
				.addData("is_sendok", IsFlag.Fou.getCode())
				.addData("agent_id", AGENT_ID)
				.post(getActionUrl("updateObjectCollect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long optionalLong = SqlOperator.queryNumber(db, "select count(1) count from "
					+ Object_collect.TableName + " where agent_id = ? ", AGENT_ID)
					.orElseThrow(() -> new BusinessException("该查询有且仅有一条数据"));
			assertThat("校验object_collect表数据量正确", optionalLong, is(OBJECT_COLLECT_ROWS));
			Object_collect collect = SqlOperator.queryOneObject(db, Object_collect.class, "select * from "
					+ Object_collect.TableName + " WHERE obj_collect_name = ?", "测试对象采集编号0")
					.orElseThrow(() -> new BusinessException("测试用例异常"));
			assertThat("校验object_collect表数据量正确", collect.getFile_path()
					, is("/aaaa/ccc/ddd"));
			assertThat("校验object_collect表数据量正确", collect.getObj_number()
					, is("hahahahxianshi"));
		}

		//2.更新一个半结构化采集，但ftp采集任务名称重复和其他任务名称重复
		bodyString = new HttpClient()
				.addData("odc_id", ODC_ID)
				.addData("object_collect_type", ObjectCollectType.HangCaiJi.getCode())
				.addData("obj_number", "ttyy")
				.addData("obj_collect_name", "测试对象采集名称1")
				.addData("system_name", "Windows 10")
				.addData("host_name", "zhuxi")
				.addData("local_time", DateUtil.getDateTime())
				.addData("server_date", DateUtil.getSysDate())
				.addData("s_date", DateUtil.getSysDate())
				.addData("e_date", DateUtil.getSysDate())
				.addData("database_code", DataBaseCode.UTF_8.getCode())
				.addData("run_way", ExecuteWay.MingLingChuFa.getCode())
				.addData("file_path", "/aaaa/ccc/ddd")
				.addData("is_sendok", IsFlag.Fou.getCode())
				.addData("agent_id", AGENT_ID)
				.post(getActionUrl("updateObjectCollect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));

//		//3.更新一个半结构化采集，但是run_way格式不正确
//		bodyString = new HttpClient()
//				.addData("odc_id", ODC_ID)
//				.addData("object_collect_type", ObjectCollectType.HangCaiJi.getCode())
//				.addData("obj_number", "ttyy77")
//				.addData("obj_collect_name", "测试对象采集名称1")
//				.addData("system_name", "Windows 10")
//				.addData("host_name", "zhuxi")
//				.addData("local_time", DateUtil.getDateTime())
//				.addData("server_date", DateUtil.getSysDate())
//				.addData("s_date", DateUtil.getSysDate())
//				.addData("e_date", DateUtil.getSysDate())
//				.addData("database_code", DataBaseCode.UTF_8.getCode())
//				.addData("run_way", "我必须是代码项")
//				.addData("file_path", "/aaaa/ccc/ddd")
//				.addData("is_sendok", IsFlag.Fou.getCode())
//				.addData("agent_id", AGENT_ID)
//				.post(getActionUrl("updateObjectCollect")).getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败！"));
//		assertThat(ar.isSuccess(), is(false));
//
//		//4.更新一个半结构化采集，但是obj_number格式不正确
//		bodyString = new HttpClient()
//				.addData("odc_id", ODC_ID)
//				.addData("object_collect_type", ObjectCollectType.HangCaiJi.getCode())
//				.addData("obj_number", "我不能是中文")
//				.addData("obj_collect_name", "测试对象采集名称1")
//				.addData("system_name", "Windows 10")
//				.addData("host_name", "zhuxi")
//				.addData("local_time", DateUtil.getDateTime())
//				.addData("server_date", DateUtil.getSysDate())
//				.addData("s_date", DateUtil.getSysDate())
//				.addData("e_date", DateUtil.getSysDate())
//				.addData("database_code", DataBaseCode.UTF_8.getCode())
//				.addData("run_way", ExecuteWay.MingLingChuFa.getCode())
//				.addData("file_path", "/aaaa/ccc/ddd")
//				.addData("is_sendok", IsFlag.Fou.getCode())
//				.addData("agent_id", AGENT_ID)
//				.post(getActionUrl("updateObjectCollect")).getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败！"));
//		assertThat(ar.isSuccess(), is(false));
	}

	/**
	 * searchObjectCollectTask根据对象采集id查询对象采集对应信息表
	 * <p>
	 * 1.使用正确的odc_id查询OBJECT_COLLECT_TASK表
	 * 2.使用错误的odc_id查询OBJECT_COLLECT_TASK表
	 * 注：此方法没有写到四个及以上的测试用例是因为此方法只是一个查询方法，只有正确和错误两种情况
	 */
	@Test
	public void searchObjectCollectTaskTest() {
		//1.使用正确的odc_id查询OBJECT_COLLECT_TASK表
		bodyString = new HttpClient()
				.addData("odc_id", ODC_ID)
				.post(getActionUrl("searchObjectCollectTask")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForResult().getRowCount(), is(Integer.parseInt(OBJECT_COLLECT_TASK_ROWS + "")));
		assertThat(ar.getDataForResult().getString(0, "database_code")
				, is(DataBaseCode.UTF_8.getCode()));

		//2.使用错误的odc_id查询OBJECT_COLLECT_TASK表
		bodyString = new HttpClient()
				.addData("odc_id", "27266381")
				.post(getActionUrl("searchObjectCollectTask")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForResult().isEmpty(), is(true));
	}

	/**
	 * deleteObjectCollectTask删除对象采集任务测试用例
	 * <p>
	 * 1.使用ocs_id为30000007删除object_collect_task表，ocs_id为30000001对应的对象采集存储设置下有数据
	 * 2.使用ocs_id为30000001删除object_collect_task表，ocs_id为30000007对应的对象采集结构信息表
	 * 3.使用ocs_id为30000002删除object_collect_task表
	 * 4.使用不存在的ocs_id删除object_collect_task表
	 * 注：此方法没有写到四个及以上的测试用例是因为此方法只是一个查询方法，只有正确和错误两种情况
	 */
	@Test
	public void deleteObjectCollectTaskTest() {
		//1.使用ocs_id为30000007删除object_collect_task表，ocs_id为30000001对应的对象采集存储设置下有数据
		bodyString = new HttpClient()
				.addData("ocs_id", OCS_ID + 6)
				.post(getActionUrl("deleteObjectCollectTask")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));

		//2.使用ocs_id为30000001删除object_collect_task表，ocs_id为30000007对应的对象采集结构信息表
		bodyString = new HttpClient()
				.addData("ocs_id", OCS_ID)
				.post(getActionUrl("deleteObjectCollectTask")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));

		//3.使用ocs_id为30000002删除object_collect_task表
		bodyString = new HttpClient()
				.addData("ocs_id", OCS_ID + 1)
				.post(getActionUrl("deleteObjectCollectTask")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long optionalLong = SqlOperator.queryNumber(db, "select count(1) count from "
					+ Object_collect_task.TableName + " where agent_id = ? ", AGENT_ID)
					.orElseThrow(() -> new BusinessException("该查询有且仅有一条数据"));
			assertThat("校验Object_collect_task表数据量正确", optionalLong
					, is(OBJECT_COLLECT_TASK_ROWS - 1));
		}


		//4.使用不存在的ocs_id删除object_collect_task表
		bodyString = new HttpClient()
				.addData("ocs_id", OCS_ID + 100)
				.post(getActionUrl("deleteObjectCollectTask")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	/**
	 * saveObjectCollectTask保存对象采集对应信息表测试用例
	 * <p>
	 * 1.保存对象采集对应信息表，ocs_id不为空，走编辑逻辑，更新数据
	 * 2.保存对象采集对应信息表，ocs_id为空，走新增逻辑，插入数据
	 * 3.保存对象采集对应信息表，en_name名称重复
	 * 4.保存对象采集对应信息表，en_name格式不正确
	 */
	@Test
	public void saveObjectCollectTaskTest() {
		//1.保存对象采集对应信息表，ocs_id不为空，走编辑逻辑，更新数据
		JSONArray array = new JSONArray();
		for (int i = 0; i < OBJECT_COLLECT_TASK_ROWS; i++) {
			JSONObject object = new JSONObject();
			object.put("ocs_id", OCS_ID + i);
			object.put("en_name", "aaaTestzzuuiqyqiw" + i);
			object.put("zh_name", "测试用例使用");
			object.put("remark", IsFlag.Shi.getCode());
			object.put("collect_data_type", CollectDataType.JSON.getCode());
			object.put("odc_id", ODC_ID);
			object.put("database_code", DataBaseCode.UTF_8.getCode());
			object.put("agent_id", AGENT_ID);
			array.add(object);
		}
		bodyString = new HttpClient()
				.addData("object_collect_task_array", array.toJSONString())
				.post(getActionUrl("saveObjectCollectTask")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long optionalLong = SqlOperator.queryNumber(db, "select count(1) count from "
					+ Object_collect_task.TableName + " where agent_id = ?", AGENT_ID).orElseThrow(() ->
					new BusinessException("查询得到的数据必须有且只有一条"));
			assertThat("校验数据量正确", optionalLong, is(OBJECT_COLLECT_TASK_ROWS));
			Result result = SqlOperator.queryResult(db, "select * from " +
							Object_collect_task.TableName + " where en_name = ? "
					, "aaaTestzzuuiqyqiw1");
			assertThat("校验Object_collect_task表数据正确", result.getString(0
					, "collect_data_type"), is(CollectDataType.JSON.getCode()));
		}

		//2.保存对象采集对应信息表，ocs_id为空，走新增逻辑，插入数据
		array.clear();
		for (int i = 20; i < OBJECT_COLLECT_TASK_ROWS + 20; i++) {
			JSONObject object = new JSONObject();
			object.put("en_name", "gggggyyyyyyyytttr887921" + i);
			object.put("zh_name", "测试用例使用");
			object.put("remark", IsFlag.Shi.getCode());
			object.put("collect_data_type", CollectDataType.JSON.getCode());
			object.put("odc_id", ODC_ID);
			object.put("database_code", DataBaseCode.UTF_8.getCode());
			object.put("agent_id", AGENT_ID);
			array.add(object);
		}
		bodyString = new HttpClient()
				.addData("object_collect_task_array", array.toJSONString())
				.post(getActionUrl("saveObjectCollectTask")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long optionalLong = SqlOperator.queryNumber(db, "select count(1) count from "
					+ Object_collect_task.TableName + " where agent_id = ?", AGENT_ID).orElseThrow(() ->
					new BusinessException("查询得到的数据必须有且只有一条"));
			assertThat("校验数据量正确", optionalLong, is(OBJECT_COLLECT_TASK_ROWS * 2));
			Result result = SqlOperator.queryResult(db, "select * from " +
							Object_collect_task.TableName + " where en_name = ? "
					, "gggggyyyyyyyytttr88792120");
			assertThat("校验Object_collect_task表数据正确", result.getLong(0
					, "odc_id"), is(ODC_ID));
			assertThat("校验Object_collect_task表数据正确", result.getString(0
					, "database_code"), is(DataBaseCode.UTF_8.getCode()));
		}

		//3.保存对象采集对应信息表，en_name名称重复
		array.clear();
		for (int i = 20; i < OBJECT_COLLECT_TASK_ROWS + 20; i++) {
			JSONObject object = new JSONObject();
			object.put("en_name", "gggggyyyyyyyytttr887921");
			object.put("zh_name", "测试用例使用");
			object.put("remark", IsFlag.Shi.getCode());
			object.put("collect_data_type", CollectDataType.JSON.getCode());
			object.put("odc_id", ODC_ID);
			object.put("database_code", DataBaseCode.UTF_8.getCode());
			object.put("agent_id", AGENT_ID);
			array.add(object);
		}
		bodyString = new HttpClient()
				.addData("object_collect_task_array", array.toJSONString())
				.post(getActionUrl("saveObjectCollectTask")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));

//		//4.保存对象采集对应信息表，en_name格式不正确
//		array.clear();
//		for (int i = 20; i < OBJECT_COLLECT_TASK_ROWS + 20; i++) {
//			JSONObject object = new JSONObject();
//			object.put("en_name", "英文名称为中文" + i);
//			object.put("zh_name", "测试用例使用");
//			object.put("remark", IsFlag.Shi.getCode());
//			object.put("collect_data_type", CollectDataType.JSON.getCode());
//			object.put("odc_id", ODC_ID);
//			object.put("database_code", DataBaseCode.UTF_8.getCode());
//			object.put("agent_id", AGENT_ID);
//			array.add(object);
//		}
//		bodyString = new HttpClient()
//				.addData("object_collect_task_array", array.toJSONString())
//				.post(getActionUrl("saveObjectCollectTask")).getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败！"));
//		assertThat(ar.isSuccess(), is(false));
	}

	/**
	 * searchObject_collect_struct查询对象采集结构信息表测试用例
	 * <p>
	 * 1.测试一个正确的ocs_id查询数据
	 * 2.测试使用一个错误的ocs_id查询数据
	 * 注：此方法没有写到四个及以上的测试用例是因为此方法只是一个查询方法，只有正确和错误两种情况
	 */
	@Test
	public void searchObject_collect_structTest() {
		//1.测试一个正确的ocs_id查询数据
		bodyString = new HttpClient()
				.addData("ocs_id", OCS_ID)
				.post(getActionUrl("searchObject_collect_struct")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		//验证数据
		assertThat(ar.getDataForResult().getRowCount(), is(Integer.parseInt(OBJECT_COLLECT_STRUCT_ROWS + "")));
		assertThat(ar.getDataForResult().getString(0, "struct_type")
				, is(ObjectDataType.ZiFuChuan.getCode()));

		//2.测试使用一个错误的ocs_id查询数据
		bodyString = new HttpClient()
				.addData("ocs_id", 7826112L)
				.post(getActionUrl("searchObject_collect_struct")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForResult().isEmpty(), is(true));
	}

	/**
	 * deleteObject_collect_struct删除对象采集结构信息表测试用例
	 * <p>
	 * 1.测试一个正确的struct_id，删除对象采集结构信息表
	 * 2.测试一个错误的struct_id，删除对象采集结构信息表
	 * 注：此方法没有写到四个及以上的测试用例是因为此方法只是一个查询方法，只有正确和错误两种情况
	 */
	@Test
	public void deleteObject_collect_structTest() {
		//1.测试一个正确的struct_id，删除对象采集结构信息表
		bodyString = new HttpClient()
				.addData("struct_id", STRUCT_ID)
				.post(getActionUrl("deleteObject_collect_struct")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		//验证数据
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long optionalLong = SqlOperator.queryNumber(db, "select count(1) count from "
					+ Object_collect_struct.TableName + " where ocs_id = ?", OCS_ID).orElseThrow(() ->
					new BusinessException("查询得到的数据必须有且只有一条"));
			assertThat("校验数据量正确", optionalLong, is(OBJECT_COLLECT_STRUCT_ROWS - 1));
		}

		//2.测试一个错误的struct_id，删除对象采集结构信息表
		bodyString = new HttpClient()
				.addData("struct_id", 7826112L)
				.post(getActionUrl("deleteObject_collect_struct")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}


	/**
	 * saveObject_collect_struct保存对象采集对应结构信息表测试用例
	 * <p>
	 * 1.保存对象采集对应结构信息表，struct_id不为空，走编辑逻辑，更新数据
	 * 2.保存对象采集对应结构信息表，struct_id为空，走新增逻辑，插入数据
	 * 3.保存对象采集对应结构信息表，struct_id不为空，同一个struct_id,en_name名称重复,更新没问题
	 * 4.保存对象采集对应结构信息表，en_name名称重复
	 * 5.保存对象采集对应结构信息表，en_name格式不正确
	 */
	@Test
	public void saveObject_collect_structTest() {
		//1.保存对象采集对应结构信息表，ocs_id不为空，走编辑逻辑，更新数据
		JSONArray array = new JSONArray();
		for (int i = 0; i < OBJECT_COLLECT_STRUCT_ROWS; i++) {
			JSONObject object = new JSONObject();
			object.put("struct_id", STRUCT_ID + i);
			object.put("coll_name", "aaaTestzzuuiqyqiw" + i);
			object.put("remark", "测试用例使用" + i);
			object.put("ocs_id", OCS_ID);
			object.put("struct_type", ObjectDataType.ZiFuChuan.getCode());
			object.put("data_desc", "对象采集对应结构信息表coll_name描述" + i);
			array.add(object);
		}
		bodyString = new HttpClient()
				.addData("object_collect_struct_array", array.toJSONString())
				.post(getActionUrl("saveObject_collect_struct")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long optionalLong = SqlOperator.queryNumber(db, "select count(1) count from "
					+ Object_collect_struct.TableName + " where ocs_id = ?", OCS_ID).orElseThrow(() ->
					new BusinessException("查询得到的数据必须有且只有一条"));
			assertThat("校验数据量正确", optionalLong, is(OBJECT_COLLECT_STRUCT_ROWS));
			Result result = SqlOperator.queryResult(db, "select * from " +
							Object_collect_struct.TableName + " where coll_name = ? "
					, "aaaTestzzuuiqyqiw1");
			assertThat("校验Object_collect_task表数据正确", result.getString(0
					, "remark"), is("测试用例使用1"));
		}

		//2.保存对象采集对应结构信息表，ocs_id为空，走新增逻辑，插入数据
		array.clear();
		for (int i = 20; i < OBJECT_COLLECT_STRUCT_ROWS + 20; i++) {
			JSONObject object = new JSONObject();
			object.put("coll_name", "gggggyyyyyyyytttr88792120" + i);
			object.put("remark", "测试用例使用");
			object.put("ocs_id", OCS_ID);
			object.put("struct_type", ObjectDataType.ZiFuChuan.getCode());
			object.put("data_desc", "对象采集对应结构信息表coll_name描述" + i);
			array.add(object);
		}
		bodyString = new HttpClient()
				.addData("object_collect_struct_array", array.toJSONString())
				.post(getActionUrl("saveObject_collect_struct")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long optionalLong = SqlOperator.queryNumber(db, "select count(1) count from "
					+ Object_collect_struct.TableName + " where ocs_id = ?", OCS_ID).orElseThrow(() ->
					new BusinessException("查询得到的数据必须有且只有一条"));
			assertThat("校验数据量正确", optionalLong, is(OBJECT_COLLECT_STRUCT_ROWS * 2));
			Result result = SqlOperator.queryResult(db, "select * from " +
							Object_collect_struct.TableName + " where coll_name = ? "
					, "gggggyyyyyyyytttr8879212020");
			assertThat("校验Object_collect_task表数据正确", result.getLong(0
					, "ocs_id"), is(OCS_ID));
			assertThat("校验Object_collect_task表数据正确", result.getString(0
					, "struct_type"), is(ObjectDataType.ZiFuChuan.getCode()));
		}

		//3.保存对象采集对应结构信息表，struct_id不为空，同一个struct_id,en_name名称重复,更新没问题
		array.clear();
		for (int i = 0; i < OBJECT_COLLECT_STRUCT_ROWS; i++) {
			JSONObject object = new JSONObject();
			object.put("struct_id", STRUCT_ID + i);
			object.put("coll_name", "aaaTestzzuuiqyqiw" + i);
			object.put("remark", "测试用例使用");
			object.put("ocs_id", OCS_ID);
			object.put("struct_type", ObjectDataType.ShuZu.getCode());
			object.put("data_desc", "对象采集对应结构信息表coll_name描述" + i);
			array.add(object);
		}
		bodyString = new HttpClient()
				.addData("object_collect_struct_array", array.toJSONString())
				.post(getActionUrl("saveObject_collect_struct")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long optionalLong = SqlOperator.queryNumber(db, "select count(1) count from "
					+ Object_collect_struct.TableName + " where ocs_id = ?", OCS_ID).orElseThrow(() ->
					new BusinessException("查询得到的数据必须有且只有一条"));
			assertThat("校验数据量正确", optionalLong, is(OBJECT_COLLECT_STRUCT_ROWS * 2));
			Result result = SqlOperator.queryResult(db, "select * from " +
							Object_collect_struct.TableName + " where coll_name = ? "
					, "aaaTestzzuuiqyqiw1");
			assertThat("校验Object_collect_task表数据正确", result.getLong(0
					, "ocs_id"), is(OCS_ID));
			assertThat("校验Object_collect_task表数据正确", result.getString(0
					, "struct_type"), is(ObjectDataType.ShuZu.getCode()));
		}

		//4.保存对象采集对应结构信息表，coll_name名称重复
		array.clear();
		for (int i = 20; i < OBJECT_COLLECT_STRUCT_ROWS + 20; i++) {
			JSONObject object = new JSONObject();
			object.put("coll_name", "aaaTestzzuuiqyqiw");
			object.put("remark", "测试用例使用");
			object.put("ocs_id", OCS_ID);
			object.put("struct_type", ObjectDataType.ZiFuChuan.getCode());
			object.put("data_desc", "对象采集对应结构信息表coll_name描述" + i);
			array.add(object);
		}
		bodyString = new HttpClient()
				.addData("object_collect_struct_array", array.toJSONString())
				.post(getActionUrl("saveObject_collect_struct")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));

//		//5.保存对象采集对应结构信息表，en_name格式不正确
//		array.clear();
//		for (int i = 20; i < OBJECT_COLLECT_STRUCT_ROWS + 20; i++) {
//			JSONObject object = new JSONObject();
//			object.put("struct_id", STRUCT_ID + i);
//			object.put("coll_name", "我是中文，哈哈哈，不正确"+i);
//			object.put("remark", "测试用例使用");
//			object.put("ocs_id", OCS_ID);
//			object.put("struct_type", ObjectDataType.ZiFuChuan.getCode());
//			object.put("data_desc", "对象采集对应结构信息表coll_name描述"+i);
//			array.add(object);
//		}
//		bodyString = new HttpClient()
//				.addData("object_collect_struct_array", array.toJSONString())
//				.post(getActionUrl("saveObject_collect_struct")).getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败！"));
//		assertThat(ar.isSuccess(), is(false));
	}

	/**
	 * searchObject_storage根据对象采集id查询对象采集任务存储设置测试用例
	 * <p>
	 * 1.测试一个正确的odc_id查询数据
	 * 2.测试使用一个错误的odc_id查询数据
	 * 注：此方法没有写到四个及以上的测试用例是因为此方法只是一个查询方法，只有正确和错误两种情况
	 */
	@Test
	public void searchObject_storageTest() {
		//1.测试一个正确的odc_id查询数据
		bodyString = new HttpClient()
				.addData("odc_id", ODC_ID)
				.post(getActionUrl("searchObject_storage")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		//验证数据
		assertThat(ar.getDataForResult().getRowCount(), is(Integer.parseInt(OBJECT_COLLECT_TASK_ROWS + "")));
		assertThat(ar.getDataForResult().getString(0, "is_hbase"), is(IsFlag.Fou.getCode()));
		assertThat(ar.getDataForResult().getString(0, "is_hdfs"), is(IsFlag.Shi.getCode()));

		//2.测试使用一个错误的odc_id查询数据
		bodyString = new HttpClient()
				.addData("odc_id", 7826112L)
				.post(getActionUrl("searchObject_storage")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	/**
	 * saveObject_storage保存对象采集存储设置表测试用例
	 * <p>
	 * 1.保存对象采集存储设置表，obj_stid不为空，走编辑逻辑，更新数据
	 * 2.保存对象采集存储设置表，obj_stid为空，走新增逻辑，插入数据
	 * 3.保存对象采集存储设置表，is_hbase格式不正确
	 * 4.更新对象采集存储设置表，is_hdfs格式不正确
	 */
	@Test
	public void saveObject_storageTest() {
		//1.保存对象采集存储设置表，obj_stid不为空，走编辑逻辑，更新数据
		JSONArray array = new JSONArray();
		for (int i = 0; i < OBJECT_STORAGE_ROWS; i++) {
			JSONObject object = new JSONObject();
			object.put("obj_stid", OBJ_STID + i);
			object.put("is_hbase", IsFlag.Shi.getCode());
			object.put("is_hdfs", IsFlag.Fou.getCode());
			object.put("remark", "zxz测试用例清除表object_storage专用");
			object.put("ocs_id", OCS_ID + i);
			array.add(object);
		}
		bodyString = new HttpClient()
				.addData("object_storage_array", array.toJSONString())
				.addData("odc_id", ODC_ID)
				.post(getActionUrl("saveObject_storage")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long optionalLong = SqlOperator.queryNumber(db, "select count(1) count from "
							+ Object_storage.TableName + " where remark = ?"
					, "zxz测试用例清除表object_storage专用").orElseThrow(() ->
					new BusinessException("查询得到的数据必须有且只有一条"));
			assertThat("校验数据量正确", optionalLong, is(OBJECT_STORAGE_ROWS));
			Result result = SqlOperator.queryResult(db, "select * from " +
							Object_storage.TableName + " where remark = ? "
					, "zxz测试用例清除表object_storage专用");
			assertThat("校验Object_storage表数据正确", result.getString(0
					, "is_hbase"), is(IsFlag.Shi.getCode()));
			assertThat("校验Object_storage表数据正确", result.getString(0
					, "is_hdfs"), is(IsFlag.Fou.getCode()));
			Result result2 = SqlOperator.queryResult(db, "select * from " +
					Object_collect.TableName + " where odc_id = ? ", ODC_ID);
			assertThat("校验Object_collect表数据正确", result2.getString(0
					, "is_sendok"), is(IsFlag.Shi.getCode()));
		}

		//2.保存对象采集存储设置表，obj_stid为空，走新增逻辑，插入数据
		array.clear();
		JSONObject obj = new JSONObject();
		obj.put("is_hbase", IsFlag.Shi.getCode());
		obj.put("is_hdfs", IsFlag.Shi.getCode());
		obj.put("remark", "zxz测试用例清除表object_storage专用");
		obj.put("ocs_id", 77666111L);
		array.add(obj);
		bodyString = new HttpClient()
				.addData("object_storage_array", array.toJSONString())
				.addData("odc_id", ODC_ID)
				.post(getActionUrl("saveObject_storage")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long optionalLong = SqlOperator.queryNumber(db, "select count(1) count from "
							+ Object_storage.TableName + " where remark = ?"
					, "zxz测试用例清除表object_storage专用").orElseThrow(() ->
					new BusinessException("查询得到的数据必须有且只有一条"));
			assertThat("校验数据量正确", optionalLong, is(OBJECT_STORAGE_ROWS + 1));
			Result result = SqlOperator.queryResult(db, "select * from " +
							Object_storage.TableName + " where remark = ? AND ocs_id = ?"
					, "zxz测试用例清除表object_storage专用", 77666111L);
			assertThat("校验Object_storage表数据正确", result.getString(0
					, "is_hbase"), is(IsFlag.Shi.getCode()));
			assertThat("校验Object_storage表数据正确", result.getString(0
					, "is_hdfs"), is(IsFlag.Shi.getCode()));
			Result result2 = SqlOperator.queryResult(db, "select * from " +
					Object_collect.TableName + " where odc_id = ? ", ODC_ID);
			assertThat("校验Object_collect表数据正确", result2.getString(0
					, "is_sendok"), is(IsFlag.Shi.getCode()));
		}

		//3.保存对象采集存储设置表，is_hbase格式不正确
		array.clear();
		for (int i = 20; i < OBJECT_COLLECT_STRUCT_ROWS + 20; i++) {
			JSONObject object = new JSONObject();
			object.put("is_hbase", "shi");
			object.put("is_hdfs", IsFlag.Fou.getCode());
			object.put("remark", "zxz测试用例清除表object_storage专用");
			object.put("ocs_id", OCS_ID + i);
			array.add(object);
		}
		bodyString = new HttpClient()
				.addData("object_storage_array", array.toJSONString())
				.addData("odc_id", ODC_ID)
				.post(getActionUrl("saveObject_storage")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));

//		//4.更新对象采集存储设置表，is_hdfs格式不正确
//		array.clear();
//		for (int i = 20; i < OBJECT_COLLECT_STRUCT_ROWS + 20; i++) {
//			JSONObject object = new JSONObject();
//			object.put("obj_stid", OBJ_STID + i);
//			object.put("is_hbase", IsFlag.Shi.getCode());
//			object.put("is_hdfs", "fou");
//			object.put("remark", "zxz测试用例清除表object_storage专用");
//			object.put("ocs_id", OCS_ID + i);
//			array.add(object);
//		}
//		bodyString = new HttpClient()
//				.addData("object_storage_array", array.toJSONString())
//				.addData("odc_id", ODC_ID)
//				.post(getActionUrl("saveObject_storage")).getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败！"));
//		assertThat(ar.isSuccess(), is(false));
	}

	/**
	 * 测试用例清理数据
	 * <p>
	 * 1.清理sys_user表中造的数据
	 * 2.清理Department_info表中造的数据
	 * 3.删除测试用例造的agent_down_info表数据，默认为1条，AGENT_ID为10000001
	 * 4.删除测试用例造的Object_collect表数据，默认为2条,ODC_ID为20000001---20000002
	 * 5.删除测试用例造的object_collect_task表数据，默认为10条,OCS_ID为30000001---30000010
	 * 6.删除测试用例造的object_storage表数据，默认为10条,OBJ_STID为40000001---40000010
	 * 7.删除测试用例造的object_collect_struct表数据，默认为10条,STRUCT_ID为50000001---50000010
	 */
	@After
	public void afterTest() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1.清理sys_user表中造的数据
			SqlOperator.execute(db, "DELETE FROM " + Sys_user.TableName + " WHERE user_id = ?"
					, USER_ID);
			//2.清理Department_info表中造的数据
			SqlOperator.execute(db, "DELETE FROM " + Department_info.TableName + " WHERE dep_id = ?"
					, DEPT_ID);
			//3.删除测试用例造的agent_down_info表数据，默认为1条，AGENT_ID为10000001
			SqlOperator.execute(db, "DELETE FROM " + Agent_down_info.TableName + " WHERE remark = ?"
					, "测试用例清除数据专用列");
			//4.删除测试用例造的Object_collect表数据，默认为2条,ODC_ID为20000001---20000002
			SqlOperator.execute(db, "DELETE FROM " + Object_collect.TableName + " WHERE agent_id = ?"
					, AGENT_ID);
			//5.删除测试用例造的object_collect_task表数据，默认为10条,OCS_ID为30000001---30000010
			SqlOperator.execute(db, "DELETE FROM " + Object_collect_task.TableName
					+ " WHERE agent_id = ?", AGENT_ID);
			//6.删除测试用例造的object_storage表数据，默认为10条,OBJ_STID为40000001---40000010
			SqlOperator.execute(db, "DELETE FROM " + Object_storage.TableName
					+ " WHERE remark = ?", "zxz测试用例清除表object_storage专用");
			//7.删除测试用例造的object_collect_struct表数据，默认为10条,STRUCT_ID为50000001---50000010
			SqlOperator.execute(db, "DELETE FROM " + Object_collect_struct.TableName
					+ " WHERE ocs_id = ?", OCS_ID);
			SqlOperator.commitTransaction(db);
		}
	}
}
