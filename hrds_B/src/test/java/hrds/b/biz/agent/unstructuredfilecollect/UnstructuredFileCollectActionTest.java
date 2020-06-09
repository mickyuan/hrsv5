package hrds.b.biz.agent.unstructuredfilecollect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.File_collect_set;
import hrds.commons.entity.File_source;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * UnstructuredFileCollectAction类测试用例
 * date: 2019/9/12 16:03
 * author: zxz
 */
public class UnstructuredFileCollectActionTest extends WebBaseTestCase {
	private static String bodyString;
	private static ActionResult ar;
	//请填写测试用户需要做登录验证的A项目的登录验证的接口
	private static final String LOGIN_URL = "http://172.168.0.9:8888/A/action/hrds/a/biz/login/login";
	//	private static final String AGENT_PORT = "55661";
//	private static final String AGENT_IP = "172.168.0.9";
	//当前线程的id
	private String id = String.valueOf(Thread.currentThread().getId());
	// 向file_collect_set表中初始化的数据条数
	private static final long FILE_COLLECT_SET_ROWS = 2L;
	// 向file_source表中初始化的数据条数
	private static final int FILE_SOURCE_ROWS = 10;
	// 请填写已有的已经部署并且启动的一个agent的agent_id
	private static final long AGENT_ID = 1000000027L;
	//部署的agent所在机器的操作系统 填写linux或windows
	private static final String OS_NAME = "linux";
	// 文件采集设置表id
	private final long FCS_ID = Long.parseLong(id + "0000001");
	// 数据源id
//	private final long SOURCE_ID = Long.parseLong(id + "0000001");
//	//用户id
//	private final long USER_ID = Long.parseLong(id + "999");
//	//部门ID
//	private final long DEPT_ID = Long.parseLong(id + "999");
	//一个已经存在的用户id
	private static final long USER_ID = 2001L;
	//上面用户id所对应的用户名
	private static final String PASSWORD = "1";

	/**
	 * 为每个方法测试用例初始化参数//FIXME 初始化的数据，要有详细说明，参考王正诚的写法
	 * <p>
	 * 1.造sys_user表数据，用于模拟用户登录。
	 * 2.造部门表数据，用于模拟用户登录
	 * 3.造agent_down_info表的数据，添加非结构化采集需要获取Agent所在机器的基本信息
	 * 4.造file_collect_set表数据，初始化条数可调整，默认为2条，fcs_id为20000001和20000002
	 * 5.造file_source表数据，初始化条数可调整，默认为10条，fcs_id为20000001
	 * 6.模拟用户登录
	 */
	@Before
	public void beforeTest() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1.造sys_user表数据，用于模拟用户登录。
//			Sys_user user = new Sys_user();
//			user.setUser_id(USER_ID);
//			user.setCreate_id(USER_ID);
//			user.setRole_id(USER_ID);
//			user.setUser_name("测试用户(9001)");
//			user.setUser_password("1");
//			user.setUseris_admin(IsFlag.Shi.getCode());
//			user.setUser_state(IsFlag.Shi.getCode());
//			user.setCreate_date(DateUtil.getSysDate());
//			user.setCreate_time(DateUtil.getSysTime());
//			user.setToken("0");
//			user.setValid_time("0");
//			user.setDep_id(DEPT_ID);
//			assertThat("初始化数据成功", user.add(db), is(1));
//			//2.造部门表数据，用于模拟用户登录
//			Department_info deptInfo = new Department_info();
//			deptInfo.setDep_id(DEPT_ID);
//			deptInfo.setDep_name("测试系统参数类部门init-zxz");
//			deptInfo.setCreate_date(DateUtil.getSysDate());
//			deptInfo.setCreate_time(DateUtil.getSysTime());
//			deptInfo.setDep_remark("测试系统参数类部门init-zxz");
//			assertThat("初始化数据成功", deptInfo.add(db), is(1));
			//3.造agent_info表的数据，初始化数据可调整，默认为2条，agent_id为10000001和10000002
//			Agent_down_info agent_down_info = new Agent_down_info();
//			agent_down_info.setDown_id(PrimayKeyGener.getNextId());
//			agent_down_info.setUser_id(USER_ID);
//			agent_down_info.setAgent_id(AGENT_ID);
//			agent_down_info.setAgent_ip(AGENT_IP);
//			agent_down_info.setAgent_port(AGENT_PORT);
//			agent_down_info.setAgent_type(AgentType.ShuJuKu.getCode());
//			agent_down_info.setAgent_name("非结构化采集Agent");
//			agent_down_info.setSave_dir("/aaa/ccc/");
//			agent_down_info.setLog_dir("/aaa/ccc/log");
//			agent_down_info.setDeploy(IsFlag.Shi.getCode());
//			agent_down_info.setAgent_context("/agent");
//			agent_down_info.setAgent_pattern("/receive/*");
//			agent_down_info.setRemark("测试用例清除数据专用列");
//			assertThat("初始化数据成功", agent_down_info.add(db), is(1));
//			Agent_info agent_info = new Agent_info();
//			agent_info.setUser_id(USER_ID);
//			agent_info.setAgent_id(AGENT_ID);
//			agent_info.setAgent_ip(AGENT_IP);
//			agent_info.setAgent_port(AGENT_PORT);
//			agent_info.setAgent_type(AgentType.ShuJuKu.getCode());
//			agent_info.setAgent_name("非结构化采集Agent");
//			agent_info.setAgent_type(AgentType.WenJianXiTong.getCode());
//			agent_info.setAgent_status(AgentStatus.YiLianJie.getCode());
//			agent_info.setSource_id(SOURCE_ID);
//			agent_info.setCreate_date(DateUtil.getSysDate());
//			agent_info.setCreate_time(DateUtil.getSysTime());
//			assertThat("初始化数据成功", agent_info.add(db), is(1));
			//4.造file_collect_set表数据，初始化条数可调整，默认为2条，fcs_id为20000001和20000002
			for (int i = 0; i < FILE_COLLECT_SET_ROWS; i++) {
				File_collect_set file_collect_set = new File_collect_set();
				file_collect_set.setFcs_id(FCS_ID + i);
				file_collect_set.setFcs_name(id + "zxzwjcj" + i);
				file_collect_set.setHost_name("zhuxi");
				file_collect_set.setSystem_type("Windows 10");
				file_collect_set.setIs_sendok(IsFlag.Fou.getCode());
				file_collect_set.setIs_solr(IsFlag.Shi.getCode());
				file_collect_set.setAgent_id(AGENT_ID);
				file_collect_set.setRemark(id + "测试用例专用数据标识");
				assertThat("初始化数据成功", file_collect_set.add(db), is(1));
			}
			//5.造file_source表数据，初始化条数可调整，默认为10条，fcs_id为20000001
			for (int i = 0; i < FILE_SOURCE_ROWS; i++) {
				File_source file_source = new File_source();
				file_source.setFile_source_id(String.valueOf(new Random().nextInt(Integer.MAX_VALUE)));
				file_source.setAgent_id(AGENT_ID);
				file_source.setFcs_id(FCS_ID);
				file_source.setFile_source_path(id + "/aaa/bbb/" + i);
				file_source.setIs_audio(IsFlag.Shi.getCode());
				file_source.setIs_image(IsFlag.Shi.getCode());
				file_source.setIs_office(IsFlag.Shi.getCode());
				file_source.setIs_other(IsFlag.Shi.getCode());
				file_source.setIs_pdf(IsFlag.Shi.getCode());
				file_source.setIs_text(IsFlag.Shi.getCode());
				file_source.setIs_video(IsFlag.Shi.getCode());
				file_source.setIs_compress(IsFlag.Shi.getCode());
				file_source.setFile_remark(id + "测试用例专用数据标识");
				assertThat("初始化数据成功", file_source.add(db), is(1));
			}
			db.commit();
		}
		//6.模拟用户登录
		String responseValue = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("password", PASSWORD)
				.post(LOGIN_URL).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
	}

	/**
	 * 测试searchFileCollectTest方法
	 * <p>
	 * 1.当agent_id不为空，fcs_id为空时
	 * 2.当agent_id和fcs_id都不为空时
	 * 3.当agent_id不为空，且agent_id在表中不存在时
	 * 4.当agent_id和fcs_id都不为空时，且fcs_id在表中不存在时
	 * 5.当agent_id不为空，且agent服务没有启动时
	 */
	@Test
	public void searchFileCollectTest() {
		//1.当agent_id不为空，fcs_id为空时
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.post(getActionUrl("searchFileCollect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(StringUtil.isBlank(ar.getDataForMap().get("osName").toString()), is(false));

		//2.当agent_id和fcs_id都不为空时
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("fcs_id", FCS_ID)
				.post(getActionUrl("searchFileCollect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(StringUtil.isBlank(ar.getDataForMap().get("file_collect_set_info").toString()), is(false));

		//3.当agent_id不为空，且agent_id在表中不存在时
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID - 100)
				.post(getActionUrl("searchFileCollect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		//这里会抛异常，因为异常信息可能会改变，所以直接判断返回false
		assertThat(ar.isSuccess(), is(false));

		//4.当agent_id和fcs_id都不为空时，且fcs_id在表中不存在时
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("fcs_id", FCS_ID - 100)
				.post(getActionUrl("searchFileCollect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));

		//5.当agent_id不为空，且agent服务没有启动时
//		bodyString = new HttpClient()
//				.addData("agent_id", AGENT_ID + 1)
//				.post(getActionUrl("searchFileCollect")).getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败！"));
//		assertThat(ar.isSuccess(), is(false));
	}

	/**
	 * 测试addFileCollect保存文件采集新增逻辑
	 * <p>
	 * 1.添加一条有效数据
	 * 2.添加一条任务名称相同的数据
	 * 3.添加一条is_sendok值有问题的数据
	 * 4.添加一条is_solr值有问题的数据
	 */
	@Test
	public void addFileCollectTest() {
		//1.添加一条有效数据
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("fcs_name", id + "zxzwjcjy" + FILE_COLLECT_SET_ROWS)
				.addData("host_name", "zhuxi11")
				.addData("system_type", "Windows10")
				.addData("is_sendok", IsFlag.Fou.getCode())
				.addData("is_solr", IsFlag.Shi.getCode())
				.addData("remark", id + "测试用例专用数据标识")
				.post(getActionUrl("addFileCollect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long count = SqlOperator.queryNumber(db, "select count(1) count from "
							+ File_collect_set.TableName + " WHERE agent_id = ? AND fcs_name = ?", AGENT_ID,
					id + "zxzwjcjy" + FILE_COLLECT_SET_ROWS).orElseThrow(()
					-> new BusinessException("查询得到的数据必须有且只有一条"));
			assertThat("添加数据成功", count, is(1L));
			Result result = SqlOperator.queryResult(db, "select * from "
							+ File_collect_set.TableName + " where fcs_name = ?",
					id + "zxzwjcjy" + FILE_COLLECT_SET_ROWS);
			assertThat("添加数据成功", result.getString(0, "system_type")
					, is("Windows10"));
			assertThat("添加数据成功", result.getString(0, "host_name")
					, is("zhuxi11"));
			assertThat("添加数据成功", result.getString(0, "is_sendok")
					, is(IsFlag.Fou.getCode()));
		}

		//2.添加一条任务名称相同的数据
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("fcs_name", id + "zxzwjcj0")
				.addData("host_name", "zhuxi")
				.addData("system_type", "Windows 10")
				.addData("is_sendok", IsFlag.Fou.getCode())
				.addData("is_solr", IsFlag.Shi.getCode())
				.addData("remark", id + "测试用例专用数据标识")
				.post(getActionUrl("addFileCollect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));

		//3.添加一条is_sendok值有问题的数据
//		bodyString = new HttpClient()
//				.addData("agent_id", AGENT_ID)
//				.addData("fcs_name", "zxzwjcj666")
//				.addData("host_name", "zhuxi")
//				.addData("system_type", "Windows 10")
//				.addData("is_sendok", IsFlag.Shi.getCode())
//				.addData("is_solr", IsFlag.Shi.getCode())
//				.post(getActionUrl("addFileCollect")).getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败！"));
//		assertThat(ar.isSuccess(), is(false));

		//4.添加一条is_solr值有问题的数据
//		bodyString = new HttpClient()
//				.addData("agent_id", AGENT_ID)
//				.addData("fcs_name", "zxzwjcj1")
//				.addData("host_name", "zhuxi")
//				.addData("system_type", "Windows 10")
//				.addData("is_sendok", IsFlag.Fou.getCode())
//				.addData("is_solr", "cccc")
//				.post(getActionUrl("addFileCollect")).getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败！"));
//		assertThat(ar.isSuccess(), is(false));
	}

	/**
	 * 测试updateFileCollect保存文件采集新增逻辑
	 * <p>
	 * 1.更新一条有效数据
	 * 2.更新一条任务名称和其他任务相同的数据
	 * 3.更新一条is_sendok值有问题的数据
	 * 4.更新一条is_solr值有问题的数据
	 */
	@Test
	public void updateFileCollectTest() {
		//1.更新一条有效数据
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("fcs_id", FCS_ID)
				.addData("fcs_name", id + "zxzwjcj666")
				.addData("host_name", "zhuxi11")
				.addData("system_type", "Windows10")
				.addData("is_sendok", IsFlag.Fou.getCode())
				.addData("is_solr", IsFlag.Shi.getCode())
				.addData("remark", id + "测试用例专用数据标识")
				.post(getActionUrl("updateFileCollect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long count = SqlOperator.queryNumber(db, "select count(1) count from "
					+ File_collect_set.TableName + " where fcs_name = ?", id + "zxzwjcj666")
					.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
			assertThat("更新数据成功", count, is(1L));
			Result result = SqlOperator.queryResult(db, "select * from "
					+ File_collect_set.TableName + " where fcs_name = ?", id + "zxzwjcj666");
			assertThat("更新数据成功", result.getString(0, "system_type")
					, is("Windows10"));
			assertThat("更新数据成功", result.getString(0, "host_name")
					, is("zhuxi11"));
			assertThat("更新数据成功", result.getString(0, "is_sendok")
					, is(IsFlag.Fou.getCode()));
		}

		//2.更新一条任务名称和其他任务相同的数据
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("fcs_id", FCS_ID)
				.addData("fcs_name", id + "zxzwjcj1")
				.addData("host_name", "zhuxi")
				.addData("system_type", "Windows 10")
				.addData("is_sendok", IsFlag.Fou.getCode())
				.addData("is_solr", IsFlag.Shi.getCode())
				.addData("remark", id + "测试用例专用数据标识")
				.post(getActionUrl("updateFileCollect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));

		//3.更新一条is_sendok值有问题的数据
//		bodyString = new HttpClient()
//				.addData("agent_id", AGENT_ID)
//				.addData("fcs_id", FCS_ID)
//				.addData("fcs_name", "zxzwjcj666")
//				.addData("host_name", "zhuxi")
//				.addData("system_type", "Windows 10")
//				.addData("is_sendok", IsFlag.Shi.getCode())
//				.addData("is_solr", IsFlag.Shi.getCode())
//				.post(getActionUrl("updateFileCollect")).getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败！"));
//		assertThat(ar.isSuccess(), is(false));

		//4.更新一条is_solr值有问题的数据
//		bodyString = new HttpClient()
//				.addData("agent_id", AGENT_ID)
//				.addData("fcs_id", FCS_ID)
//				.addData("fcs_name", "zxzwjcj1")
//				.addData("host_name", "zhuxi")
//				.addData("system_type", "Windows 10")
//				.addData("is_sendok", IsFlag.Fou.getCode())
//				.addData("is_solr", "cccc")
//				.post(getActionUrl("updateFileCollect")).getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败！"));
//		assertThat(ar.isSuccess(), is(false));
	}

	/**
	 * 测试searchFileSource根据文件系统设置表的id查询源文件设置表
	 * 1.测试一个正确的FCS_ID查询数据
	 * 2.测试使用一个错误的fcs_id查询数据
	 * 注：此方法没有写到四个及以上的测试用例是因为此方法只是一个查询方法，只有正确和错误两种情况
	 */
	@Test
	public void searchFileSourceTest() {
		//1.测试一个正确的FCS_ID查询数据
		bodyString = new HttpClient()
				.addData("fcs_id", FCS_ID)
				.post(getActionUrl("searchFileSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		//验证数据
		assertThat(ar.getDataForResult().getString(0, "is_office"), is(IsFlag.Shi.getCode()));
		assertThat(ar.getDataForResult().getString(0, "is_text"), is(IsFlag.Shi.getCode()));
		assertThat(ar.getDataForResult().getString(0, "file_source_path").contains("/aaa")
				, is(true));

		//2.测试使用一个错误的fcs_id查询数据
		bodyString = new HttpClient()
				.addData("fcs_id", 100000077L)
				.post(getActionUrl("searchFileSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForResult().isEmpty(), is(true));
	}

	/**
	 * selectPathTest选择文件夹测试，agent_id正确，不指定文件夹测试
	 * <p>
	 * 1.一个正确的agent_id，没有路径
	 * 2.一个错误的agent_id，没有路径
	 * 3.一个正确的agent_id，正确的路径
	 * 4.一个正确的agent_id，错误的路径
	 */
	@Test
	public void selectPathTest() {
		String selectPath = getActionUrl("selectPath");
		//1.一个正确的agent_id，没有路径
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.post(selectPath).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForEntityList(Map.class).isEmpty(), is(false));

		//2.一个错误的agent_id，没有路径
		bodyString = new HttpClient()
				.addData("agent_id", 100000099L)
				.post(getActionUrl("selectPath")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));

		//3.一个正确的agent_id，正确的路径
		if ("windows".equals(OS_NAME)) {
			bodyString = new HttpClient()
					.addData("agent_id", AGENT_ID)
					.addData("path", "D:/")
					.post(getActionUrl("selectPath")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败！"));
			assertThat(ar.isSuccess(), is(true));
			assertThat(ar.getDataForEntityList(Map.class).isEmpty(), is(false));
			System.out.println("===========" + ar.getData().toString());
		}
		//一个正确的agent_id，正确的路径
		if ("linux".equals(OS_NAME)) {
			bodyString = new HttpClient()
					.addData("agent_id", AGENT_ID)
					.addData("path", "/")
					.post(getActionUrl("selectPath")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败！"));
			assertThat(ar.isSuccess(), is(true));
			assertThat(ar.getDataForEntityList(Map.class).isEmpty(), is(false));
		}

		//4.一个正确的agent_id，错误的路径
		bodyString = new HttpClient()
				.addData("agent_id", AGENT_ID)
				.addData("path", "/aaa/asda/sdwqeqwewq/sad/asasdw")
				.post(getActionUrl("selectPath")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForEntityList(Map.class).isEmpty(), is(true));
	}

	/**
	 * saveFileSourceTest保存源文件路径测试，使用FCS_ID保存
	 * <p>
	 * 1.使用FCS_ID，更新逻辑的保存源文件设置表的测试
	 * 2.不使用FCS_ID，添加逻辑的保存源文件设置表的测试
	 * 3.FCS_ID为空的测试
	 * 4.同一个非结构化采集请选择重复的文件路径
	 */
	@Test
	public void saveFileSourceTest() {
		//1.使用FCS_ID，更新逻辑的保存源文件设置表的测试
		JSONArray array = new JSONArray();
		for (int i = 0; i < FILE_SOURCE_ROWS; i++) {
			JSONObject object = new JSONObject();
			object.put("agent_id", AGENT_ID);
			object.put("fcs_id", FCS_ID);
			object.put("file_source_path", id + "/aaa/bbb/100/" + i);
			object.put("is_audio", IsFlag.Shi.getCode());
			object.put("is_image", IsFlag.Shi.getCode());
			object.put("is_compress", IsFlag.Shi.getCode());
			object.put("is_office", IsFlag.Shi.getCode());
			object.put("is_other", IsFlag.Shi.getCode());
			object.put("is_pdf", IsFlag.Shi.getCode());
			object.put("is_text", IsFlag.Shi.getCode());
			object.put("is_video", IsFlag.Shi.getCode());
			object.put("file_remark", id + "测试用例专用数据标识");
			array.add(object);
		}
		bodyString = new HttpClient()
				.addData("file_sources_array", array.toJSONString())
				.post(getActionUrl("saveFileSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long optionalLong = SqlOperator.queryNumber(db, "select count(1) count from "
					+ File_source.TableName + " where agent_id = ? AND fcs_id = ?", AGENT_ID, FCS_ID).orElseThrow(() ->
					new BusinessException("查询得到的数据必须有且只有一条"));
			assertThat("校验数据量正确", optionalLong, is(10L));
			Result result = SqlOperator.queryResult(db, "select * from " +
							File_collect_set.TableName + " where fcs_id = ? and is_sendok = ?"
					, FCS_ID, IsFlag.Shi.getCode());
			assertThat("校验更新file_collect_set表数据量正确", result.getString(0
					, "is_sendok"), is(IsFlag.Shi.getCode()));
		}

		//2.不使用FCS_ID，添加逻辑的保存源文件设置表的测试
		array.clear();
		for (int i = 0; i < FILE_SOURCE_ROWS; i++) {
			JSONObject object = new JSONObject();
			object.put("agent_id", AGENT_ID);
			object.put("fcs_id", FCS_ID + 1);
			object.put("file_source_path", id + "/aaa/bbb/" + i);
			object.put("is_audio", IsFlag.Shi.getCode());
			object.put("is_image", IsFlag.Shi.getCode());
			object.put("is_office", IsFlag.Shi.getCode());
			object.put("is_compress", IsFlag.Shi.getCode());
			object.put("is_other", IsFlag.Shi.getCode());
			object.put("is_pdf", IsFlag.Shi.getCode());
			object.put("is_text", IsFlag.Shi.getCode());
			object.put("is_video", IsFlag.Shi.getCode());
			object.put("file_remark", id + "测试用例专用数据标识");
			array.add(object);
		}
		bodyString = new HttpClient()
				.addData("file_sources_array", array.toJSONString())
				.post(getActionUrl("saveFileSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long optionalLong = SqlOperator.queryNumber(db, "select count(1) count from "
							+ File_source.TableName + " where agent_id = ? AND fcs_id = ?", AGENT_ID,
					FCS_ID + 1).orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
			assertThat("校验数据量正确", optionalLong, is(10L));
			Result result = SqlOperator.queryResult(db, "select * from " +
							File_collect_set.TableName + " where fcs_id = ? and is_sendok = ?"
					, FCS_ID + 1, IsFlag.Shi.getCode());
			assertThat("校验更新file_collect_set表数据量正确", result.getString(0
					, "is_sendok"), is(IsFlag.Shi.getCode()));
		}

		//3.FCS_ID为空的测试
		array.clear();
		for (int i = 0; i < FILE_SOURCE_ROWS; i++) {
			JSONObject object = new JSONObject();
			object.put("agent_id", AGENT_ID);
			object.put("file_source_path", id + "/aaa/bbb/" + i);
			object.put("is_audio", IsFlag.Shi.getCode());
			object.put("is_image", IsFlag.Shi.getCode());
			object.put("is_office", IsFlag.Shi.getCode());
			object.put("is_other", IsFlag.Shi.getCode());
			object.put("is_compress", IsFlag.Shi.getCode());
			object.put("is_pdf", IsFlag.Shi.getCode());
			object.put("is_text", IsFlag.Shi.getCode());
			object.put("is_video", IsFlag.Shi.getCode());
			object.put("file_remark", id + "测试用例专用数据标识");
			array.add(object);
		}
		bodyString = new HttpClient()
				.addData("file_sources_array", array.toJSONString())
				.post(getActionUrl("saveFileSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));

		//4.同一个非结构化采集请选择重复的文件路径
		array.clear();
		for (int i = 0; i < FILE_SOURCE_ROWS; i++) {
			JSONObject object = new JSONObject();
			object.put("agent_id", AGENT_ID);
			object.put("fcs_id", FCS_ID + 1);
			object.put("file_source_path", id + "/aaa/bbb/");
			object.put("is_audio", IsFlag.Shi.getCode());
			object.put("is_image", IsFlag.Shi.getCode());
			object.put("is_office", IsFlag.Shi.getCode());
			object.put("is_other", IsFlag.Shi.getCode());
			object.put("is_compress", IsFlag.Shi.getCode());
			object.put("is_pdf", IsFlag.Shi.getCode());
			object.put("is_text", IsFlag.Shi.getCode());
			object.put("is_video", IsFlag.Shi.getCode());
			object.put("file_remark", id + "测试用例专用数据标识");
			array.add(object);
		}
		bodyString = new HttpClient()
				.addData("file_sources_array", array.toJSONString())
				.post(getActionUrl("saveFileSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}


	/**
	 * 测试用例清理数据
	 * <p>
	 * 1.清理sys_user表中造的数据
	 * 2.清理Department_info表中造的数据
	 * 3.清理agent_down_info表中造的数据
	 * 4.清理file_collect_set表中造的数据
	 * 5.清理file_source表中造的数据
	 */
	@After
	public void afterTest() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1.清理sys_user表中造的数据
//			SqlOperator.execute(db, "DELETE FROM " + Sys_user.TableName + " WHERE user_id = ?"
//					, USER_ID);
//			//2.清理Department_info表中造的数据
//			SqlOperator.execute(db, "DELETE FROM " + Department_info.TableName + " WHERE dep_id = ?"
//					, DEPT_ID);
			//3.清理agent_down_info表中造的数据
//			SqlOperator.execute(db, "DELETE FROM " + Agent_down_info.TableName + " WHERE remark = ?"
//					, "测试用例清除数据专用列");
//			//3.清理agent_info表中造的数据
//			SqlOperator.execute(db, "DELETE FROM " + Agent_info.TableName + " WHERE user_id = ?"
//					, USER_ID);
			//4.清理file_collect_set表中造的数据
			SqlOperator.execute(db, "DELETE FROM " + File_collect_set.TableName
					+ " WHERE remark = ?", id + "测试用例专用数据标识");
			//5.清理file_source表中造的数据
			SqlOperator.execute(db, "DELETE FROM " + File_source.TableName + " WHERE file_remark = ?",
					id + "测试用例专用数据标识");
			SqlOperator.commitTransaction(db);
		}
	}

}
