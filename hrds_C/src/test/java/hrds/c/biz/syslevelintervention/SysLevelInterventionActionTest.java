package hrds.c.biz.syslevelintervention;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
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

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SysLevelInterventionActionTest extends WebBaseTestCase {

	//请填写测试用户需要做登录验证的A项目的登录验证的接口
	private static final String LOGIN_URL = testInitConfig.getString("login_url");
	// 已经存在的用户ID,用于模拟登录
	private static final long USER_ID = testInitConfig.getLong("user_id");
	private static final String PASSWORD = testInitConfig.getString("password");
	//主键ID
	private long nextId = PrimayKeyGener.getNextId();
	//当前线程的id
	private long THREAD_ID = Thread.currentThread().getId() + nextId;
	// 初始化工程编号
	private final String EtlSysCd = "xtgyglcs" + THREAD_ID;
	private final String EtlSysCd2 = "xtgyglcs2" + THREAD_ID;
	private final String EtlSysCd3 = "xtgyglcs3" + THREAD_ID;
	// 初始化任务编号
	private final String SubSysCd = "xtgyrwcs" + THREAD_ID;
	private final String EventId = "time1" + THREAD_ID;
	private final String HisEventId = "time2" + THREAD_ID;
	private final String HisEventId2 = "time3" + THREAD_ID;

	@Before
	public void before() {
		// 初始化作业干预测试数据
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 构造etl_sys表测试数据
			Etl_sys etl_sys = new Etl_sys();
			for (int i = 0; i < 3; i++) {
				if (i == 0) {
					etl_sys.setEtl_sys_cd(EtlSysCd);
					etl_sys.setEtl_sys_name("dhwcs" + THREAD_ID);
				} else if (i == 1) {
					etl_sys.setEtl_sys_cd(EtlSysCd2);
					etl_sys.setEtl_sys_name("dhwcs2" + THREAD_ID);
				} else {
					etl_sys.setEtl_sys_cd(EtlSysCd3);
					etl_sys.setEtl_sys_name("dhwcs3" + THREAD_ID);
				}
				etl_sys.setUser_id(USER_ID);
				etl_sys.setCurr_bath_date(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString());
				assertThat("测试数据etl_sys初始化", etl_sys.add(db), is(1));
			}
			// 构造etl_sub_sys_list表测试数据
			Etl_sub_sys_list etl_sub_sys_list = new Etl_sub_sys_list();
			etl_sub_sys_list.setSub_sys_cd(SubSysCd);
			etl_sub_sys_list.setEtl_sys_cd(EtlSysCd);
			etl_sub_sys_list.setSub_sys_desc("任务测试");
			etl_sub_sys_list.setComments("测试");
			assertThat("测试数据data_source初始化", etl_sub_sys_list.add(db), is(1));
			// 构造etl_job_curr表测试数据
			Etl_job_cur etl_job_cur = new Etl_job_cur();
			etl_job_cur.setEtl_sys_cd(EtlSysCd);
			etl_job_cur.setEtl_job("[NOTHING]");
			etl_job_cur.setPro_dic("/home/hyshf/dhw");
			etl_job_cur.setEtl_job_desc("测试作业定义");
			etl_job_cur.setPro_para("1");
			etl_job_cur.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			etl_job_cur.setToday_disp(Today_Dispatch_Flag.YES.getCode());
			etl_job_cur.setCurr_bath_date(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString());
			etl_job_cur.setPro_name("zy.shell");
			etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
			etl_job_cur.setSub_sys_cd(SubSysCd);
			etl_job_cur.setDisp_type(Dispatch_Type.TPLUS0.getCode());
			etl_job_cur.setDisp_freq(Dispatch_Frequency.PinLv.getCode());
			etl_job_cur.setExe_frequency(1L);
			etl_job_cur.setExe_num(1);
			etl_job_cur.setStar_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
					+ " " + DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
			etl_job_cur.setEnd_time("2020-12-31 10:30:30");
			etl_job_cur.setJob_disp_status(Job_Status.ERROR.getCode());
			assertThat("测试数据etl_job_cur初始化", etl_job_cur.add(db), is(1));
			// 构造etl_job_hand表测试数据
			Etl_job_hand etl_job_hand = new Etl_job_hand();
			etl_job_hand.setEtl_sys_cd(EtlSysCd);
			etl_job_hand.setEtl_job("[NOTHING]");
			etl_job_hand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			etl_job_hand.setHand_status(Meddle_status.FALSE.getCode());
			etl_job_hand.setEvent_id(EventId);
			etl_job_hand.setEtl_hand_type(Meddle_type.SYS_ORIGINAL.getCode());
			assertThat("测试数据etl_job_hand初始化", etl_job_hand.add(db), is(1));
			// 构造etl_job_hand_his表测试数据
			Etl_job_hand_his etl_job_hand_his = new Etl_job_hand_his();
			for (int i = 0; i < 2; i++) {
				etl_job_hand_his.setEtl_sys_cd(EtlSysCd);
				etl_job_hand_his.setEtl_job("[NOTHING]");
				if (i == 0) {
					etl_job_hand_his.setMain_serv_sync(Main_Server_Sync.YES.getCode());
					etl_job_hand_his.setHand_status(Meddle_status.ERROR.getCode());
					etl_job_hand_his.setEvent_id(HisEventId);
					etl_job_hand_his.setEtl_hand_type(Meddle_type.SYS_ORIGINAL.getCode());
				} else {
					etl_job_hand_his.setEtl_sys_cd(EtlSysCd);
					etl_job_hand_his.setEtl_job("[NOTHING]");
					etl_job_hand_his.setMain_serv_sync(Main_Server_Sync.YES.getCode());
					etl_job_hand_his.setHand_status(Meddle_status.DONE.getCode());
					etl_job_hand_his.setEvent_id(HisEventId2);
					etl_job_hand_his.setEtl_hand_type(Meddle_type.SYS_SHIFT.getCode());
				}
				assertThat("测试数据etl_job_hand_his初始化", etl_job_hand_his.add(db), is(1));
			}
			SqlOperator.commitTransaction(db);
		}
		// 模拟用户登录
		String bodyString = new HttpClient()
				.buildSession()
				.addData("user_id", USER_ID)
				.addData("password", PASSWORD)
				.post(LOGIN_URL)
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat("用户登录", ar.isSuccess(), is(true));
	}

	@After
	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 测试完成后删除Etl_sub_sys表测试数据
			SqlOperator.execute(db,
					"delete from " + Etl_sub_sys_list.TableName + " where etl_sys_cd in(?,?,?)",
					EtlSysCd, EtlSysCd2, EtlSysCd3);
			// 判断Etl_sub_sys数据是否被删除
			long num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_sub_sys_list.TableName + " where etl_sys_cd in(?,?,?)",
					EtlSysCd, EtlSysCd2, EtlSysCd3)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完成后删除etl_sys表测试数据
			SqlOperator.execute(db,
					"delete from " + Etl_sys.TableName + " where etl_sys_cd in(?,?,?)",
					EtlSysCd, EtlSysCd2, EtlSysCd3);
			// 判断etl_sys数据是否被删除
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_sys.TableName + " where etl_sys_cd in(?,?,?)",
					EtlSysCd, EtlSysCd2, EtlSysCd3).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除Etl_job_hand表测试数据
			SqlOperator.execute(db,
					"delete from " + Etl_job_hand.TableName + " where etl_sys_cd in(?,?,?)",
					EtlSysCd, EtlSysCd2, EtlSysCd3);
			// 判断Etl_job_hand数据是否被删除
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_job_hand.TableName + " where etl_sys_cd in(?,?,?)",
					EtlSysCd, EtlSysCd2, EtlSysCd3)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除Etl_job_hand_his表测试数据
			SqlOperator.execute(db,
					"delete from " + Etl_job_hand_his.TableName + " where etl_sys_cd in(?,?,?)",
					EtlSysCd, EtlSysCd2, EtlSysCd3);
			// 判断Etl_job_hand_his数据是否被删除
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_job_hand_his.TableName + " where etl_sys_cd in(?,?,?)",
					EtlSysCd, EtlSysCd2, EtlSysCd3)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除Etl_job_cur表测试数据
			SqlOperator.execute(db,
					"delete from " + Etl_job_cur.TableName + " where etl_sys_cd in(?,?,?)",
					EtlSysCd, EtlSysCd2, EtlSysCd3);
			// 判断Etl_job_cur数据是否被删除
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_job_cur.TableName + " where etl_sys_cd in(?,?,?)",
					EtlSysCd, EtlSysCd2, EtlSysCd3)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
	}

	@Method(desc = "查询系统级干预作业信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在")
	@Test
	public void searchSystemBatchConditions() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.post(getActionUrl("searchSystemBatchConditions"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> etlJobCurr = ar.getDataForMap();
		assertThat(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString(),
				is(etlJobCurr.get("curr_bath_date")));
		List<Map<String, Object>> etlJobCurrList = (List<Map<String, Object>>) etlJobCurr.get("etlJobCurrList");
		assertThat(String.valueOf(0), is(etlJobCurrList.get(0).get("stop_num").toString()));
		assertThat(String.valueOf(0), is(etlJobCurrList.get(0).get("done_num").toString()));
		assertThat(String.valueOf(0), is(etlJobCurrList.get(0).get("alarm_num").toString()));
		assertThat(String.valueOf(1), is(etlJobCurrList.get(0).get("error_num").toString()));
		assertThat(String.valueOf(0), is(etlJobCurrList.get(0).get("running_num").toString()));
		assertThat(String.valueOf(0), is(etlJobCurrList.get(0).get("waiting_num").toString()));
		assertThat(String.valueOf(0), is(etlJobCurrList.get(0).get("pending_num").toString()));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "zygycs")
				.post(getActionUrl("searchSystemBatchConditions"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "查询系统级当前干预情况",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在")
	@Test
	public void searchSysLevelCurrInterventionInfo() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.post(getActionUrl("searchSysLevelCurrInterventionInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		List<Map<String, Object>> currInterventionMap = (List<Map<String, Object>>) ar.getData();
		assertThat(EtlSysCd, is(currInterventionMap.get(0).get("etl_sys_cd")));
		assertThat(EventId, is(currInterventionMap.get(0).get("event_id")));
		assertThat(Meddle_status.FALSE.getCode(), is(currInterventionMap.get(0).get("hand_status")));
		assertThat(Meddle_type.SYS_ORIGINAL.getCode(), is(currInterventionMap.get(0).get("etl_hand_type")));
		assertThat("[NOTHING]", is(currInterventionMap.get(0).get("etl_job")));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "zygycs")
				.post(getActionUrl("searchSysLevelCurrInterventionInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "分页查询系统级历史干预情况",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在")
	@Test
	public void searchSysLeverHisInterventionByPage() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("currPage", 1)
				.addData("pageSize", 3)
				.post(getActionUrl("searchSysLeverHisInterventionByPage"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> hisIntervention = ar.getDataForMap();
		List<Map<String, Object>> handHisList = (List<Map<String, Object>>) hisIntervention.get("handHisList");
		assertThat(handHisList.size(), is(2));
		for (Map<String, Object> map : handHisList) {
			String his_event_id = map.get("event_id").toString();
			if (his_event_id.equals(HisEventId)) {
				assertThat(EtlSysCd, is(map.get("etl_sys_cd")));
				assertThat(Meddle_status.ERROR.getCode(), is(map.get("hand_status")));
				assertThat(Meddle_type.SYS_ORIGINAL.getCode(), is(map.get("etl_hand_type")));
				assertThat("[NOTHING]", is(map.get("etl_job")));
			} else if (his_event_id.equals(HisEventId2)) {
				assertThat(EtlSysCd, is(map.get("etl_sys_cd")));
				assertThat(Meddle_status.DONE.getCode(), is(map.get("hand_status")));
				assertThat(Meddle_type.SYS_SHIFT.getCode(), is(map.get("etl_hand_type")));
				assertThat("[NOTHING]", is(map.get("etl_job")));
			}
		}
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("currPage", 1)
				.addData("pageSize", 3)
				.post(getActionUrl("searchSysLeverHisInterventionByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
	}

	@Method(desc = "系统级干预操作",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.正常的数据访问2，数据都正常，非重跑或续跑" +
					"3.错误的数据访问1，etl_sys_cd不存在" +
					"4.错误的数据访问2，etl_hand_type不存在" +
					"5.错误的数据访问3，工程下有作业正在干预")
	@Test
	public void sysLevelInterventionOperate() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常，重跑或续跑
			String bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd2)
					.addData("etl_hand_type", Meddle_type.SYS_ORIGINAL.getCode())
					.addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
					.post(getActionUrl("sysLevelInterventionOperate"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 验证数据的正确性
			Map<String, Object> etlJobHand = SqlOperator.queryOneObject(db, "select * from "
							+ Etl_job_hand.TableName + " where etl_sys_cd=? and etl_hand_type=? ", EtlSysCd2,
					Meddle_type.SYS_ORIGINAL.getCode());
			assertThat(EtlSysCd2, is(etlJobHand.get("etl_sys_cd")));
			assertThat(Meddle_status.TRUE.getCode(), is(etlJobHand.get("hand_status")));
			assertThat(Meddle_type.SYS_ORIGINAL.getCode(), is(etlJobHand.get("etl_hand_type")));
			assertThat(Main_Server_Sync.YES.getCode(), is(etlJobHand.get("main_serv_sync")));
			assertThat("[NOTHING]", is(etlJobHand.get("etl_job")));
			assertThat(EtlSysCd2 + "," + DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()),
					is(etlJobHand.get("pro_para")));
			// 2.正常的数据访问2，数据都正常，非重跑或续跑
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd3)
					.addData("etl_hand_type", Meddle_type.SYS_STOP.getCode())
					.addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
					.post(getActionUrl("sysLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 验证数据的正确性
			etlJobHand = SqlOperator.queryOneObject(db, "select * from "
							+ Etl_job_hand.TableName + " where etl_sys_cd=? and etl_hand_type=? ", EtlSysCd3,
					Meddle_type.SYS_STOP.getCode());
			assertThat(EtlSysCd3, is(etlJobHand.get("etl_sys_cd")));
			assertThat(Meddle_status.TRUE.getCode(), is(etlJobHand.get("hand_status")));
			assertThat(Meddle_type.SYS_STOP.getCode(), is(etlJobHand.get("etl_hand_type")));
			assertThat(Main_Server_Sync.YES.getCode(), is(etlJobHand.get("main_serv_sync")));
			assertThat("[NOTHING]", is(etlJobHand.get("etl_job")));
			assertThat(EtlSysCd3 + "," + DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()),
					is(etlJobHand.get("pro_para")));
			// 3.错误的数据访问1，etl_sys_cd不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", "xtjgycs")
					.addData("etl_hand_type", Meddle_type.SYS_ORIGINAL.getCode())
					.addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
					.post(getActionUrl("sysLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问2，etl_hand_type不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("etl_hand_type", "abc")
					.addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
					.post(getActionUrl("sysLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 5.错误的数据访问3，工程下有作业正在干预
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("etl_hand_type", Meddle_type.SYS_RESUME.getCode())
					.addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
					.post(getActionUrl("sysLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}
}
