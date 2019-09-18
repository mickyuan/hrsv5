package hrds.b.biz.agent.ftpcollect;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.*;
import hrds.commons.entity.Ftp_collect;
import hrds.testbase.WebBaseTestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * description: FtpCollectAction类测试用例 <br>
 * date: 2019/9/18 14:16 <br>
 * author: zxz <br>
 * version: 5.0 <br>
 */
public class FtpCollectActionTest extends WebBaseTestCase {
	private static final Logger logger = LogManager.getLogger();
	private static String bodyString;
	private static ActionResult ar;
	private static final long FTP_COLLECT_ROWS = 5L; // 向表中初始化的数据条数。
	private static final long AGENT_ID = 10000001L;
	private static final long FTP_ID = 20000001L;

	/**
	 * description: 测试用例初始化参数 <br>
	 * date: 2019/9/18 14:19 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@BeforeClass
	public static void beforeTest() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
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
	}

	/**
	 * description: 查询Ftp采集测试，ftp_id正确情况 <br>
	 * date: 2019/9/18 15:14 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@Test
	public void searchFtp_collectTest1() {
		bodyString = new HttpClient()
				.addData("ftp_id", FTP_ID)
				.post(getActionUrl("searchFtp_collect")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
		logger.info(ar.getData().toString());
	}

	/**
	 * description: 查询Ftp采集测试，ftp_id不正确情况 <br>
	 * date: 2019/9/18 15:14 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@Test
	public void searchFtp_collectTest2() {
		bodyString = new HttpClient()
				.addData("ftp_id", FTP_ID + 100)
				.post(getActionUrl("searchFtp_collect")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.getMessage(), is("根据ftp_id:" + (FTP_ID + 100) + "查询不到ftp_collect表信息"));
	}

	/**
	 * description: 保存ftp采集测试，新增正常逻辑 <br>
	 * date: 2019/9/18 15:36 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@Test
	public void saveFtp_collectTest1() {
		bodyString = new HttpClient()
				.addData("ftp_number", "测试ftp采集编号")
				.addData("ftp_name", "测试ftp采集")
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
				.addData("is_unzip", IsFlag.Shi.getCode())
				.addData("reduce_type", ReduceType.ZIP.getCode())
				.addData("agent_id", AGENT_ID)
				.post(getActionUrl("saveFtp_collect")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) count from " +
					"ftp_collect where agent_id = ?", AGENT_ID);
			assertThat("添加数据成功", optionalLong.getAsLong(), is(FTP_COLLECT_ROWS + 1));
		}
	}

	/**
	 * description: 保存ftp采集测试，新增任务名称重复逻辑 <br>
	 * date: 2019/9/18 15:36 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@Test
	public void saveFtp_collectTest2() {
		bodyString = new HttpClient()
				.addData("ftp_number", "测试ftp采集编号" )
				.addData("ftp_name", "测试ftp采集"+ 1)
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
				.addData("is_unzip", IsFlag.Shi.getCode())
				.addData("reduce_type", ReduceType.ZIP.getCode())
				.addData("agent_id", AGENT_ID)
				.post(getActionUrl("saveFtp_collect")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("ftp采集任务名称重复"));
	}

	/**
	 * description: 保存ftp采集测试，编辑逻辑 <br>
	 * date: 2019/9/18 15:36 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@Test
	public void saveFtp_collectTest3() {
		bodyString = new HttpClient()
				.addData("ftp_id", FTP_ID + 1)
				.addData("ftp_number", "测试ftp采集编号")
				.addData("ftp_name", "测试ftp采集1111")
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
				.addData("is_unzip", IsFlag.Shi.getCode())
				.addData("reduce_type", ReduceType.ZIP.getCode())
				.addData("agent_id", AGENT_ID)
				.post(getActionUrl("saveFtp_collect")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) count from " +
					"ftp_collect where agent_id = ? and ftp_name = ?", AGENT_ID,"测试ftp采集1111");
			assertThat("添加数据成功", optionalLong.getAsLong(), is(1L));
		}
	}

	/**
	 * description: 保存ftp采集测试，编辑任务名称重复逻辑 <br>
	 * date: 2019/9/18 15:36 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@Test
	public void saveFtp_collectTest4() {
		bodyString = new HttpClient()
				.addData("ftp_id", FTP_ID+2)
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
				.addData("ftp_model", IsFlag.Shi.getCode())
				.addData("run_way", ExecuteWay.MingLingChuFa.getCode())
				.addData("is_sendok", IsFlag.Fou.getCode())
				.addData("is_unzip", IsFlag.Shi.getCode())
				.addData("reduce_type", ReduceType.ZIP.getCode())
				.addData("agent_id", AGENT_ID)
				.post(getActionUrl("saveFtp_collect")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("更新后的ftp采集任务名称重复"));
	}

	/**
	 * description: 测试用例清理数据 <br>
	 * date: 2019/9/18 14:20 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 *
	 * @param
	 * @return void
	 */
	@AfterClass
	public static void afterTest() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			SqlOperator.execute(db, "DELETE FROM ftp_collect WHERE agent_id = ?", AGENT_ID);
			SqlOperator.commitTransaction(db);
		}
	}
}
