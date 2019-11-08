package hrds.b.biz.agent.ftpcollect;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.*;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Ftp_collect;
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * FtpCollectAction类测试用例
 * date: 2019/9/18 14:16
 * author: zxz
 */
public class FtpCollectActionTest extends WebBaseTestCase {
	private static String bodyString;
	private static ActionResult ar;
	// 向ftp_collect表中初始化的数据条数
	private static final long FTP_COLLECT_ROWS = 5L;
	//Agent_id
	private static final long AGENT_ID = 10000001L;
	//ftp采集设置表id
	private static final long FTP_ID = 20000001L;
	//用户id
	private static final long USER_ID = 9001L;
	//部门ID
	private static final long DEPT_ID = 9002L;

	/**
	 * 测试用例初始化参数
	 * <p>
	 * 1.造sys_user表数据，用于模拟用户登录
	 * 2.造部门表数据，用于模拟用户登录
	 * 3.造ftp_collect表数据，默认为5条,ftp_id为20000001---20000005
	 * 4.模拟用户登录
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
			//3.造ftp_collect表数据，默认为5条,ftp_id为20000001---20000005
			for (int i = 0; i < FTP_COLLECT_ROWS; i++) {
				Ftp_collect ftp_collect = new Ftp_collect();
				ftp_collect.setFtp_id(20000001L + i);
				ftp_collect.setFtp_number("测试ftp采集编号" + i);
				ftp_collect.setFtp_name("测试ftp采集" + i);
				ftp_collect.setStart_date(DateUtil.getSysDate());
				ftp_collect.setEnd_date(DateUtil.getSysDate());
				ftp_collect.setFtp_ip("127.0.0.1");
				ftp_collect.setFtp_port("33333");
				ftp_collect.setFtp_username("zzzz");
				ftp_collect.setFtp_password("1111111");
				ftp_collect.setFtp_dir("/ccc/fff/" + i);
				ftp_collect.setLocal_path("/uuu/ddd/" + i);
				ftp_collect.setFtp_rule_path(FtpRule.AnShiJian.getCode());
				ftp_collect.setIs_read_realtime(IsFlag.Shi.getCode());
				ftp_collect.setRealtime_interval(1000L);
				ftp_collect.setChild_file_path("/aaa/bbb/" + i);
				ftp_collect.setChild_time(TimeType.Hour.getCode());
				ftp_collect.setFile_suffix("dat");
				ftp_collect.setFtp_model(IsFlag.Shi.getCode());
				ftp_collect.setRun_way(ExecuteWay.MingLingChuFa.getCode());
				ftp_collect.setIs_sendok(IsFlag.Fou.getCode());
				ftp_collect.setIs_unzip(IsFlag.Shi.getCode());
				ftp_collect.setReduce_type(ReduceType.ZIP.getCode());
				ftp_collect.setAgent_id(AGENT_ID);
				assertThat("初始化数据成功", ftp_collect.add(db), is(1));
			}
			SqlOperator.commitTransaction(db);
		}
		//4.模拟用户登录
		String responseValue = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("password", "1")
				.post("http://127.0.0.1:8099/A/action/hrds/a/biz/login/login").getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
	}

	/**
	 * searchFtp_collect查询Ftp采集测试，ftp_id正确情况
	 * <p>
	 * 1.使用正确的FTP_ID查询ftp_collect表
	 * 2.使用错误的FTP_ID查询ftp_collect表
	 * 注：此方法没有写到四个及以上的测试用例是因为此方法只是一个查询方法，只有正确和错误两种情况
	 */
	@Test
	public void searchFtp_collectTest() {
		//1.使用正确的FTP_ID查询ftp_collect表
		bodyString = new HttpClient()
				.addData("ftp_id", FTP_ID)
				.post(getActionUrl("searchFtp_collect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("ftp_dir"), is("/ccc/fff/0"));
		assertThat(ar.getDataForMap().get("child_file_path"), is("/aaa/bbb/0"));
		assertThat(ar.getDataForMap().get("local_path"), is("/uuu/ddd/0"));

		//2.使用错误的FTP_ID查询ftp_collect表
		bodyString = new HttpClient()
				.addData("ftp_id", FTP_ID + 100)
				.post(getActionUrl("searchFtp_collect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	/**
	 * addFtp_collect新增ftp采集测试
	 * 1.添加一个正确的ftp采集
	 * 2.添加一个ftp采集，但ftp采集任务名称重复
	 * 3.添加一个ftp采集，但是ftp_dir格式不正确
	 * 4.添加一个ftp采集，但是ftp_ip格式不正确
	 */
	@Test
	public void addFtp_collectTest() {
		//1.添加一个正确的ftp采集
		bodyString = new HttpClient()
				.addData("ftp_number", "测试ftp采集编号")
				.addData("ftp_name", "测试ftp采集718900")
				.addData("start_date", DateUtil.getSysDate())
				.addData("end_date", DateUtil.getSysDate())
				.addData("ftp_ip", "127.0.0.1")
				.addData("ftp_port", "33333")
				.addData("ftp_username", "zzzz")
				.addData("ftp_password", "1111111")
				.addData("ftp_dir", "/ccc/fff/")
				.addData("local_path", "/uuu/ddd/")
				.addData("ftp_rule_path", FtpRule.AnShiJian.getCode())
				.addData("child_file_path", "/aaa/bbb/")
				.addData("child_time", TimeType.Hour.getCode())
				.addData("is_read_realtime", IsFlag.Shi.getCode())
				.addData("realtime_interval", 1000L)
				.addData("file_suffix", "dat")
				.addData("ftp_model", IsFlag.Shi.getCode())
				.addData("run_way", ExecuteWay.MingLingChuFa.getCode())
				.addData("is_sendok", IsFlag.Fou.getCode())
				.addData("is_unzip", IsFlag.Shi.getCode())
				.addData("reduce_type", ReduceType.ZIP.getCode())
				.addData("agent_id", AGENT_ID)
				.post(getActionUrl("addFtp_collect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long optionalLong = SqlOperator.queryNumber(db, "select count(1) count from " +
					Ftp_collect.TableName + " where agent_id = ?", AGENT_ID).orElseThrow(()
					-> new BusinessException("查询得到的数据必须有且只有一条"));
			assertThat("添加数据成功", optionalLong, is(FTP_COLLECT_ROWS + 1));
			Ftp_collect collect = SqlOperator.queryOneObject(db, Ftp_collect.class, "select * from "
					+ Ftp_collect.TableName + " where ftp_name = ?", "测试ftp采集718900").orElseThrow(()
					-> new BusinessException("查询得到的数据必须"));
			assertThat("添加数据成功", collect.getChild_file_path(), is("/aaa/bbb/"));
			assertThat("添加数据成功", collect.getAgent_id(), is(AGENT_ID));
			assertThat("添加数据成功", collect.getFtp_username(), is("zzzz"));
		}

		//2.添加一个ftp采集，但ftp采集任务名称重复
		bodyString = new HttpClient()
				.addData("ftp_number", "测试ftp采集编号")
				.addData("ftp_name", "测试ftp采集" + 1)
				.addData("start_date", DateUtil.getSysDate())
				.addData("end_date", DateUtil.getSysDate())
				.addData("ftp_ip", "127.0.0.1")
				.addData("ftp_port", "33333")
				.addData("ftp_username", "zzzz")
				.addData("ftp_password", "1111111")
				.addData("ftp_dir", "/ccc/fff/")
				.addData("local_path", "/uuu/ddd/")
				.addData("ftp_rule_path", FtpRule.AnShiJian.getCode())
				.addData("is_read_realtime", IsFlag.Shi.getCode())
				.addData("realtime_interval", 1000L)
				.addData("child_file_path", "/aaa/bbb/")
				.addData("child_time", TimeType.Hour.getCode())
				.addData("file_suffix", "dat")
				.addData("ftp_model", IsFlag.Shi.getCode())
				.addData("run_way", ExecuteWay.MingLingChuFa.getCode())
				.addData("is_sendok", IsFlag.Fou.getCode())
				.addData("is_unzip", IsFlag.Shi.getCode())
				.addData("reduce_type", ReduceType.ZIP.getCode())
				.addData("agent_id", AGENT_ID)
				.post(getActionUrl("addFtp_collect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));

		//3.添加一个ftp采集，但是ftp_dir格式不正确
//		bodyString = new HttpClient()
//				.addData("ftp_number", "测试ftp采集编号")
//				.addData("ftp_name", "测试ftp采集1111")
//				.addData("start_date", DateUtil.getSysDate())
//				.addData("end_date", DateUtil.getSysDate())
//				.addData("ftp_ip", "127.0.0.1")
//				.addData("ftp_port", "33333")
//				.addData("ftp_username", "zzzz")
//				.addData("ftp_password", "1111111")
//				.addData("ftp_dir", "sajdlsakdj,asdw,,.www")
//				.addData("local_path", "/uuu/ddd/")
//				.addData("ftp_rule_path", FtpRule.AnShiJian.getCode())
//				.addData("child_file_path", "/aaa/bbb/")
//				.addData("child_time", TimeType.Hour.getCode())
//				.addData("file_suffix", "dat")
//				.addData("ftp_model", IsFlag.Shi.getCode())
//				.addData("run_way", ExecuteWay.MingLingChuFa.getCode())
//				.addData("is_sendok", IsFlag.Fou.getCode())
//				.addData("is_unzip", IsFlag.Shi.getCode())
//				.addData("reduce_type", ReduceType.ZIP.getCode())
//				.addData("agent_id", AGENT_ID)
//				.post(getActionUrl("addFtp_collect")).getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败！"));
//		assertThat(ar.isSuccess(), is(false));

		//4.添加一个ftp采集，但是ftp_ip格式不正确
//		bodyString = new HttpClient()
//				.addData("ftp_number", "测试ftp采集编号")
//				.addData("ftp_name", "测试ftp采集" + 3)
//				.addData("start_date", DateUtil.getSysDate())
//				.addData("end_date", DateUtil.getSysDate())
//				.addData("ftp_ip", "127.0.0.1901.211.223")
//				.addData("ftp_port", "33333")
//				.addData("ftp_username", "zzzz")
//				.addData("ftp_password", "1111111")
//				.addData("ftp_dir", "/ccc/fff/")
//				.addData("local_path", "/uuu/ddd/")
//				.addData("ftp_rule_path", FtpRule.AnShiJian.getCode())
//				.addData("child_file_path", "/aaa/bbb/")
//				.addData("child_time", TimeType.Hour.getCode())
//				.addData("file_suffix", "dat")
//				.addData("ftp_model", IsFlag.Shi.getCode())
//				.addData("run_way", ExecuteWay.MingLingChuFa.getCode())
//				.addData("is_sendok", IsFlag.Fou.getCode())
//				.addData("is_unzip", IsFlag.Shi.getCode())
//				.addData("reduce_type", ReduceType.ZIP.getCode())
//				.addData("agent_id", AGENT_ID)
//				.post(getActionUrl("addFtp_collect")).getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败！"));
//		assertThat(ar.isSuccess(), is(false));
	}

	/**
	 * updateFtp_collect更新ftp采集测试
	 * <p>
	 * 1.更新ftp_id为20000002的ftp采集
	 * 2.更新一个ftp采集，但ftp采集任务名称重复
	 * 3.更新一个ftp采集，但是ftp_port格式不正确
	 * 4.更新一个ftp采集，但是is_unzip格式不正确
	 */
	@Test
	public void updateFtp_collectTest() {
		//1.更新ftp_id为20000002的ftp采集
		bodyString = new HttpClient()
				.addData("ftp_id", FTP_ID + 1)
				.addData("ftp_number", "测试ftp采集编号")
				.addData("ftp_name", "测试ftp采集11118879")
				.addData("start_date", DateUtil.getSysDate())
				.addData("end_date", DateUtil.getSysDate())
				.addData("ftp_ip", "127.0.0.1")
				.addData("ftp_port", "33333")
				.addData("ftp_username", "zzzz")
				.addData("ftp_password", "1111111")
				.addData("ftp_dir", "/ccc/fff/")
				.addData("local_path", "/uuu/ddd/")
				.addData("is_read_realtime", IsFlag.Shi.getCode())
				.addData("realtime_interval", 1000L)
				.addData("ftp_rule_path", FtpRule.AnShiJian.getCode())
				.addData("child_file_path", "/aaa/bbb/")
				.addData("child_time", TimeType.Hour.getCode())
				.addData("file_suffix", "dat")
				.addData("ftp_model", IsFlag.Shi.getCode())
				.addData("run_way", ExecuteWay.MingLingChuFa.getCode())
				.addData("is_sendok", IsFlag.Fou.getCode())
				.addData("is_unzip", IsFlag.Shi.getCode())
				.addData("reduce_type", ReduceType.ZIP.getCode())
				.addData("agent_id", AGENT_ID)
				.post(getActionUrl("updateFtp_collect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long optionalLong = SqlOperator.queryNumber(db, "select count(1) count from " +
					Ftp_collect.TableName + " where agent_id = ?", AGENT_ID).orElseThrow(()
					-> new BusinessException("查询得到的数据必须有且只有一条"));
			assertThat("更新数据成功", optionalLong, is(FTP_COLLECT_ROWS));
			Ftp_collect collect = SqlOperator.queryOneObject(db, Ftp_collect.class, "select * from "
					+ Ftp_collect.TableName + " where ftp_name = ?", "测试ftp采集11118879").orElseThrow(()
					-> new BusinessException("查询得到的数据必须"));
			assertThat("更新数据成功", collect.getChild_file_path(), is("/aaa/bbb/"));
			assertThat("更新数据成功", collect.getAgent_id(), is(AGENT_ID));
			assertThat("更新数据成功", collect.getFtp_username(), is("zzzz"));
		}

		//2.更新一个ftp采集，但ftp采集任务名称重复
		bodyString = new HttpClient()
				.addData("ftp_id", FTP_ID + 2)
				.addData("ftp_number", "测试ftp采集编号")
				.addData("ftp_name", "测试ftp采集" + 3)
				.addData("start_date", DateUtil.getSysDate())
				.addData("end_date", DateUtil.getSysDate())
				.addData("ftp_ip", "127.0.0.1")
				.addData("ftp_port", "33333")
				.addData("ftp_username", "zzzz")
				.addData("ftp_password", "1111111")
				.addData("ftp_dir", "/ccc/fff/")
				.addData("local_path", "/uuu/ddd/")
				.addData("ftp_rule_path", FtpRule.AnShiJian.getCode())
				.addData("child_file_path", "/aaa/bbb/")
				.addData("child_time", TimeType.Hour.getCode())
				.addData("file_suffix", "dat")
				.addData("is_read_realtime", IsFlag.Shi.getCode())
				.addData("realtime_interval", 1000L)
				.addData("ftp_model", IsFlag.Shi.getCode())
				.addData("run_way", ExecuteWay.MingLingChuFa.getCode())
				.addData("is_sendok", IsFlag.Fou.getCode())
				.addData("is_unzip", IsFlag.Shi.getCode())
				.addData("reduce_type", ReduceType.ZIP.getCode())
				.addData("agent_id", AGENT_ID)
				.post(getActionUrl("updateFtp_collect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));

//		//3.更新一个ftp采集，但是ftp_port格式不正确
//		bodyString = new HttpClient()
//				.addData("ftp_id", FTP_ID + 2)
//				.addData("ftp_number", "测试ftp采集编号")
//				.addData("ftp_name", "测试ftp采集" + 3)
//				.addData("start_date", DateUtil.getSysDate())
//				.addData("end_date", DateUtil.getSysDate())
//				.addData("ftp_ip", "127.0.0.1")
//				.addData("ftp_port", "qweweq")
//				.addData("ftp_username", "zzzz")
//				.addData("ftp_password", "1111111")
//				.addData("ftp_dir", "/ccc/fff/")
//				.addData("local_path", "/uuu/ddd/")
//				.addData("ftp_rule_path", FtpRule.AnShiJian.getCode())
//				.addData("child_file_path", "/aaa/bbb/")
//				.addData("child_time", TimeType.Hour.getCode())
//				.addData("file_suffix", "dat")
//				.addData("ftp_model", IsFlag.Shi.getCode())
//				.addData("run_way", ExecuteWay.MingLingChuFa.getCode())
//				.addData("is_sendok", IsFlag.Fou.getCode())
//				.addData("is_unzip", IsFlag.Shi.getCode())
//				.addData("reduce_type", ReduceType.ZIP.getCode())
//				.addData("agent_id", AGENT_ID)
//				.post(getActionUrl("updateFtp_collect")).getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败！"));
//		assertThat(ar.isSuccess(), is(false));
//
//		//4.更新一个ftp采集，但是is_unzip格式不正确
//		bodyString = new HttpClient()
//				.addData("ftp_id", FTP_ID + 2)
//				.addData("ftp_number", "测试ftp采集编号")
//				.addData("ftp_name", "测试ftp采集" + 3)
//				.addData("start_date", DateUtil.getSysDate())
//				.addData("end_date", DateUtil.getSysDate())
//				.addData("ftp_ip", "127.0.0.1")
//				.addData("ftp_port", "33333")
//				.addData("ftp_username", "zzzz")
//				.addData("ftp_password", "1111111")
//				.addData("ftp_dir", "/ccc/fff/")
//				.addData("local_path", "/uuu/ddd/")
//				.addData("ftp_rule_path", FtpRule.AnShiJian.getCode())
//				.addData("child_file_path", "/aaa/bbb/")
//				.addData("child_time", TimeType.Hour.getCode())
//				.addData("file_suffix", "dat")
//				.addData("ftp_model", IsFlag.Shi.getCode())
//				.addData("run_way", ExecuteWay.MingLingChuFa.getCode())
//				.addData("is_sendok", IsFlag.Fou.getCode())
//				.addData("is_unzip", "ccc")
//				.addData("reduce_type", ReduceType.ZIP.getCode())
//				.addData("agent_id", AGENT_ID)
//				.post(getActionUrl("updateFtp_collect")).getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
//				-> new BusinessException("连接失败！"));
//		assertThat(ar.isSuccess(), is(false));
	}

	/**
	 * 测试用例清理数据
	 * <p>
	 * 1.清理sys_user表中造的数据
	 * 2.清理Department_info表中造的数据
	 * 3.清理ftp_collect表中造的数据
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
			//3.清理ftp_collect表中造的数据
			SqlOperator.execute(db, "DELETE FROM " + Ftp_collect.TableName
					+ " WHERE agent_id = ?", AGENT_ID);
			SqlOperator.commitTransaction(db);
		}
	}
}
