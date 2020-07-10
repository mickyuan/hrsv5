package hrds.b.biz.agent.ftpcollect;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.*;
import hrds.commons.entity.Ftp_collect;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * FtpCollectAction类测试用例
 * date: 2019/9/18 14:16
 * author: zxz
 */
public class FtpCollectActionTest extends WebBaseTestCase {
	private static final Logger LOGGER = LoggerFactory.getLogger(FtpCollectActionTest.class);
	//请填写测试用户需要做登录验证的A项目的登录验证的接口
	private static final String LOGIN_URL = Constant.TESTINITCONFIG.getString("login_url", "");
	// 请填写已有的已经部署并且启动的一个agent的agent_id
	private static final long AGENT_ID = Constant.TESTINITCONFIG.getLong("agent_id", 0L);
	//一个已经存在的用户id
	private static final long USER_ID = Constant.TESTINITCONFIG.getLong("user_id", 0L);
	//上面用户id所对应的密码
	private static final String PASSWORD = Constant.TESTINITCONFIG.getString("password", "");
	//当前线程的id
	private String id = String.valueOf(Thread.currentThread().getId());
	//ftp采集设置表id
	private final long FTP_ID = PrimayKeyGener.getNextId();

	/**
	 * 测试用例初始化参数
	 * <p>
	 * 1.造ftp_collect表数据，默认为5条,ftp_id为20000001---20000005
	 * 2.模拟用户登录
	 */
	@Before
	public void beforeTest() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1.造ftp_collect表数据
			Ftp_collect ftp_collect = new Ftp_collect();
			ftp_collect.setFtp_id(FTP_ID);
			ftp_collect.setFtp_number(id + "zxzftpcj_csylzybs");
			ftp_collect.setFtp_name(id + "zxzwjcj_csylzync");
			ftp_collect.setStart_date(DateUtil.getSysDate());
			ftp_collect.setEnd_date(DateUtil.getSysDate());
			ftp_collect.setFtp_ip("127.0.0.1");
			ftp_collect.setFtp_port("33333");
			ftp_collect.setFtp_username("zzzz");
			ftp_collect.setFtp_password("1111111");
			ftp_collect.setFtp_dir("/ccc/fff/zxzftpcj_csylzybs");
			ftp_collect.setLocal_path("/uuu/ddd/zxzftpcj_csylzybs");
			ftp_collect.setFtp_rule_path(FtpRule.AnShiJian.getCode());
			ftp_collect.setIs_read_realtime(IsFlag.Shi.getCode());
			ftp_collect.setRealtime_interval(1000L);
			ftp_collect.setChild_file_path("/aaa/bbb/zxzftpcj_csylzybs");
			ftp_collect.setChild_time(TimeType.Hour.getCode());
			ftp_collect.setFile_suffix("dat");
			ftp_collect.setFtp_model(IsFlag.Shi.getCode());
			ftp_collect.setRun_way(ExecuteWay.MingLingChuFa.getCode());
			ftp_collect.setIs_sendok(IsFlag.Fou.getCode());
			ftp_collect.setIs_unzip(IsFlag.Shi.getCode());
			ftp_collect.setReduce_type(ReduceType.ZIP.getCode());
			ftp_collect.setAgent_id(AGENT_ID);
			ftp_collect.setRemark(id + "FtpCollectActionTest测试用例专用数据标识");
			assertThat("初始化数据成功", ftp_collect.add(db), is(1));
			SqlOperator.commitTransaction(db);
		} catch (Exception e) {
			LOGGER.error("测试用例初始化数据错误", e);
		}
		//2.模拟用户登录
		String responseValue = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("password", PASSWORD)
				.post(LOGIN_URL).getBodyString();
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
		ActionResult ar;
		String bodyString;
		//1.使用正确的FTP_ID查询ftp_collect表
		bodyString = new HttpClient()
				.addData("ftp_id", FTP_ID)
				.post(getActionUrl("searchFtp_collect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("ftp_dir"), is("/ccc/fff/zxzftpcj_csylzybs"));
		assertThat(ar.getDataForMap().get("child_file_path"), is("/aaa/bbb/zxzftpcj_csylzybs"));
		assertThat(ar.getDataForMap().get("local_path"), is("/uuu/ddd/zxzftpcj_csylzybs"));

		//2.使用错误的FTP_ID查询ftp_collect表
		bodyString = new HttpClient()
				.addData("ftp_id", PrimayKeyGener.getNextId())
				.post(getActionUrl("searchFtp_collect")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	/**
	 * addFtp_collect新增ftp采集测试
	 * 1.添加一个正确的ftp采集
	 * 2.添加一个ftp采集，但ftp采集任务名称重复
	 * 3.添加一个ftp采集，但是ftp_name为空
	 * 4.添加一个ftp采集，但是ftp_ip格式不正确
	 */
	@Test
	public void addFtp_collectTest() {
		ActionResult ar;
		String bodyString;
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1.添加一个正确的ftp采集
			bodyString = new HttpClient()
					.addData("ftp_number", id + "zxzftpcj_csylzybs_youxiao")
					.addData("ftp_name", id + "zxzwjcj_csylzync_youxiao")
					.addData("start_date", DateUtil.getSysDate())
					.addData("end_date", DateUtil.getSysDate())
					.addData("ftp_ip", "127.0.0.1")
					.addData("ftp_port", "33333")
					.addData("ftp_username", "zzzz")
					.addData("ftp_password", "1111111")
					.addData("ftp_dir", "/ccc/fff/zxzftpcj_csylzybs_youxiao")
					.addData("local_path", "/uuu/ddd/zxzftpcj_csylzybs_youxiao")
					.addData("ftp_rule_path", FtpRule.AnShiJian.getCode())
					.addData("child_file_path", "/aaa/bbb/zxzftpcj_csylzybs_youxiao")
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
					.addData("remark", id + "FtpCollectActionTest测试用例专用数据标识")
					.post(getActionUrl("addFtp_collect")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败！"));
			assertThat(ar.isSuccess(), is(true));

			long optionalLong = SqlOperator.queryNumber(db, "select count(1) count from " +
							Ftp_collect.TableName + " where ftp_number = ? and ftp_name = ?",
					id + "zxzftpcj_csylzybs_youxiao", id + "zxzwjcj_csylzync_youxiao").orElseThrow(()
					-> new BusinessException("查询得到的数据必须有且只有一条"));
			assertThat("添加数据成功", optionalLong, is(1L));
			Ftp_collect collect = SqlOperator.queryOneObject(db, Ftp_collect.class, "select * from "
							+ Ftp_collect.TableName + " where ftp_number = ? and ftp_name = ?",
					id + "zxzftpcj_csylzybs_youxiao", id + "zxzwjcj_csylzync_youxiao").orElseThrow(()
					-> new BusinessException("查询得到的数据必须"));
			assertThat("添加数据成功", collect.getChild_file_path(),
					is("/aaa/bbb/zxzftpcj_csylzybs_youxiao"));
			assertThat("添加数据成功", collect.getAgent_id(), is(AGENT_ID));
			assertThat("添加数据成功", collect.getFtp_username(), is("zzzz"));

			//2.添加一个ftp采集，但ftp采集任务名称重复
			bodyString = new HttpClient()
					.addData("ftp_number", id + "zxzftpcj_csylzybs")
					.addData("ftp_name", id + "zxzwjcj_csylzync")
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
					.addData("remark", id + "FtpCollectActionTest测试用例专用数据标识")
					.post(getActionUrl("addFtp_collect")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败！"));
			assertThat(ar.isSuccess(), is(false));

			//3.添加一个ftp采集，但是ftp_name为空
			bodyString = new HttpClient()
					.addData("ftp_number", id + "zxzftpcj_csylzybs_direrror")
					.addData("ftp_name", "")
					.addData("start_date", DateUtil.getSysDate())
					.addData("end_date", DateUtil.getSysDate())
					.addData("ftp_ip", "127.0.0.1")
					.addData("ftp_port", "33333")
					.addData("ftp_username", "zzzz")
					.addData("ftp_password", "1111111")
					.addData("ftp_dir", "/uuu/ddd/")
					.addData("local_path", "/uuu/ddd/")
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
					.addData("remark", id + "FtpCollectActionTest测试用例专用数据标识")
					.post(getActionUrl("addFtp_collect")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败！"));
			assertThat(ar.isSuccess(), is(false));

			//4.添加一个ftp采集，但是ftp_ip格式不正确
			bodyString = new HttpClient()
					.addData("ftp_number", id + "zxzftpcj_csylzybs_iperror")
					.addData("ftp_name", id + "zxzwjcj_csylzync_iperror")
					.addData("start_date", DateUtil.getSysDate())
					.addData("end_date", DateUtil.getSysDate())
					.addData("ftp_ip", "127.0.0.1901.211.223")
					.addData("ftp_port", "33333")
					.addData("ftp_username", "zzzz")
					.addData("ftp_password", "1111111")
					.addData("ftp_dir", "/ccc/fff/")
					.addData("local_path", "/uuu/ddd/")
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
					.addData("remark", id + "FtpCollectActionTest测试用例专用数据标识")
					.post(getActionUrl("addFtp_collect")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	/**
	 * updateFtp_collect更新ftp采集测试
	 * <p>
	 * 1.更新ftp_id为20000002的ftp采集
	 * 2.更新一个ftp采集，但ftp采集任务名称重复
	 * 3.更新一个ftp采集，但是ftp_port格式不正确
	 * 4.更新一个ftp采集，但是ftp_number为空
	 */
	@Test
	public void updateFtp_collectTest() {
		ActionResult ar;
		String bodyString;
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1.更新ftp_id为20000002的ftp采集
			bodyString = new HttpClient()
					.addData("ftp_id", FTP_ID)
					.addData("ftp_number", id + "zxzftpcj_csylzybs_gengxin")
					.addData("ftp_name", id + "zxzwjcj_csylzync_gengxin")
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
					.addData("child_file_path", "/aaa/bbb/zxzftpcj_csylzybs_gengxin")
					.addData("child_time", TimeType.Hour.getCode())
					.addData("file_suffix", "dat")
					.addData("ftp_model", IsFlag.Shi.getCode())
					.addData("run_way", ExecuteWay.MingLingChuFa.getCode())
					.addData("is_sendok", IsFlag.Fou.getCode())
					.addData("is_unzip", IsFlag.Shi.getCode())
					.addData("reduce_type", ReduceType.ZIP.getCode())
					.addData("agent_id", AGENT_ID)
					.addData("remark", id + "FtpCollectActionTest测试用例专用数据标识")
					.post(getActionUrl("updateFtp_collect")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败！"));
			assertThat(ar.isSuccess(), is(true));

			long optionalLong = SqlOperator.queryNumber(db, "select count(1) count from " +
							Ftp_collect.TableName + " where ftp_number = ? and ftp_name = ?",
					id + "zxzftpcj_csylzybs_gengxin", id + "zxzwjcj_csylzync_gengxin").orElseThrow(()
					-> new BusinessException("查询得到的数据必须有且只有一条"));
			assertThat("添加数据成功", optionalLong, is(1L));
			Ftp_collect collect = SqlOperator.queryOneObject(db, Ftp_collect.class, "select * from "
					+ Ftp_collect.TableName + " where ftp_id = ? ", FTP_ID).orElseThrow(()
					-> new BusinessException("查询得到的数据必须"));
			assertThat("更新数据成功", collect.getChild_file_path(),
					is("/aaa/bbb/zxzftpcj_csylzybs_gengxin"));
			assertThat("更新数据成功", collect.getAgent_id(), is(AGENT_ID));
			assertThat("更新数据成功", collect.getFtp_name(), is(id + "zxzwjcj_csylzync_gengxin"));

			//2.更新一个ftp采集，但ftp采集任务名称重复
			//造ftp_collect表数据，用于制造ftp采集任务名称重复
			long ftp_id = PrimayKeyGener.getNextId();
			Ftp_collect ftp_collect = new Ftp_collect();
			ftp_collect.setFtp_id(ftp_id);
			ftp_collect.setFtp_number(id + "zxzftpcj_csylzybs_chongfu");
			ftp_collect.setFtp_name(id + "zxzwjcj_csylzync_chongfu");
			ftp_collect.setStart_date(DateUtil.getSysDate());
			ftp_collect.setEnd_date(DateUtil.getSysDate());
			ftp_collect.setFtp_ip("127.0.0.1");
			ftp_collect.setFtp_port("33333");
			ftp_collect.setFtp_username("zzzz");
			ftp_collect.setFtp_password("1111111");
			ftp_collect.setFtp_dir("/ccc/fff/zxzftpcj_csylzybs");
			ftp_collect.setLocal_path("/uuu/ddd/zxzftpcj_csylzybs");
			ftp_collect.setFtp_rule_path(FtpRule.AnShiJian.getCode());
			ftp_collect.setIs_read_realtime(IsFlag.Shi.getCode());
			ftp_collect.setRealtime_interval(1000L);
			ftp_collect.setChild_file_path("/aaa/bbb/zxzftpcj_csylzybs");
			ftp_collect.setChild_time(TimeType.Hour.getCode());
			ftp_collect.setFile_suffix("dat");
			ftp_collect.setFtp_model(IsFlag.Shi.getCode());
			ftp_collect.setRun_way(ExecuteWay.MingLingChuFa.getCode());
			ftp_collect.setIs_sendok(IsFlag.Fou.getCode());
			ftp_collect.setIs_unzip(IsFlag.Shi.getCode());
			ftp_collect.setReduce_type(ReduceType.ZIP.getCode());
			ftp_collect.setAgent_id(AGENT_ID);
			ftp_collect.setRemark(id + "FtpCollectActionTest测试用例专用数据标识");
			assertThat("初始化数据成功", ftp_collect.add(db), is(1));
			db.commit();
			bodyString = new HttpClient()
					.addData("ftp_id", FTP_ID)
					.addData("ftp_number", id + "zxzwjcj_csylzync_chongfu")
					.addData("ftp_name", id + "zxzwjcj_csylzync_chongfu")
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
					.addData("remark", id + "FtpCollectActionTest测试用例专用数据标识")
					.post(getActionUrl("updateFtp_collect")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败！"));
			assertThat(ar.isSuccess(), is(false));

			//3.更新一个ftp采集，但是ftp_port格式不正确
			bodyString = new HttpClient()
					.addData("ftp_id", FTP_ID)
					.addData("ftp_number", id + "zxzftpcj_csylzybs_porterror")
					.addData("ftp_name", id + "zxzwjcj_csylzync_porterror")
					.addData("start_date", DateUtil.getSysDate())
					.addData("end_date", DateUtil.getSysDate())
					.addData("ftp_ip", "127.0.0.1")
					.addData("ftp_port", "778776")
					.addData("ftp_username", "zzzz")
					.addData("ftp_password", "1111111")
					.addData("ftp_dir", "/ccc/fff/")
					.addData("local_path", "/uuu/ddd/")
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
					.addData("remark", id + "FtpCollectActionTest测试用例专用数据标识")
					.post(getActionUrl("updateFtp_collect")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败！"));
			assertThat(ar.isSuccess(), is(false));

			//4.更新一个ftp采集，但是ftp_number为空
			bodyString = new HttpClient()
					.addData("ftp_id", FTP_ID)
					.addData("ftp_number", "")
					.addData("ftp_name", id + "zxzwjcj_csylzync_unziperror")
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
					.addData("ftp_model", IsFlag.Shi.getCode())
					.addData("run_way", ExecuteWay.MingLingChuFa.getCode())
					.addData("is_sendok", IsFlag.Fou.getCode())
					.addData("is_unzip", "ccc")
					.addData("reduce_type", ReduceType.ZIP.getCode())
					.addData("agent_id", AGENT_ID)
					.addData("remark", id + "FtpCollectActionTest测试用例专用数据标识")
					.post(getActionUrl("updateFtp_collect")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	/**
	 * 测试用例清理数据
	 * <p>
	 * 1.清理ftp_collect表中造的数据
	 */
	@After
	public void afterTest() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1.清理ftp_collect表中造的数据
			SqlOperator.execute(db, "DELETE FROM " + Ftp_collect.TableName
					+ " WHERE remark = ?", id + "FtpCollectActionTest测试用例专用数据标识");
			SqlOperator.commitTransaction(db);
		} catch (Exception e) {
			LOGGER.error("测试用例清理初始化数据，测试用例中产生的数据错误", e);
		}
	}
}
