package hrds.c.biz.joblevelintervention;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.c.biz.bean.JobHandBean;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class JobLevelInterventionActionTest extends WebBaseTestCase {
	//请填写测试用户需要做登录验证的A项目的登录验证的接口
	private static final String LOGIN_URL = testInitConfig.getString("login_url");
	//一个已经存在的用户id
	private static final long USER_ID = testInitConfig.getLong("user_id");
	//上面用户id所对应的密码
	private static final String PASSWORD = testInitConfig.getString("password");
	//当前线程的id
	private final long THREAD_ID = Thread.currentThread().getId();
	// 初始化工程编号
	private final String EtlSysCd = "zygycs" + THREAD_ID;
	// 初始化任务编号
	private final String SubSysCd = "zygyrwcs";
	private final String SubSysCd2 = "zygyrwcs2";

	private final String EventId = "zygysjh";
	private final String EventId2 = "zygysjh2";
	private final String EventId3 = "zygysjh3";
	private final String EventId4 = "zygysjh4";
	private final String EventId5 = "zygysjh5";

	@Before
	public void before() {
		// 初始化作业干预测试数据
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 构造etl_sys表测试数据
			Etl_sys etl_sys = getEtl_sys();
			assertThat("测试数据etl_sys初始化", etl_sys.add(db), is(1));
			// 构造etl_sub_sys_list表测试数据
			List<Etl_sub_sys_list> subSysLists = getEtl_sub_sys_lists();
			subSysLists.forEach(etl_sub_sys_list ->
					assertThat("测试数据etl_sub_sys_list初始化", etl_sub_sys_list.add(db), is(1))
			);
			// 构造etl_job_curr表测试数据
			List<Etl_job_cur> etlJobCurList = getEtl_job_curs();
			etlJobCurList.forEach(etl_job_cur ->
					assertThat("测试数据etl_job_cur初始化", etl_job_cur.add(db), is(1))
			);
			// 构造etl_job_hand表测试数据
			List<Etl_job_hand> etlJobHandList = getEtl_job_hands();
			etlJobHandList.forEach(etl_job_hand ->
					assertThat("测试数据etl_job_hand初始化", etl_job_hand.add(db), is(1))
			);
			// 构造etl_job_hand_his表测试数据
			List<Etl_job_hand_his> etlJobHandHisList = getEtl_job_hand_his();
			etlJobHandHisList.forEach(etl_job_hand_his ->
					assertThat("测试数据etl_job_hand_his初始化", etl_job_hand_his.add(db), is(1))

			);
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

	private List<Etl_job_hand_his> getEtl_job_hand_his() {
		List<Etl_job_hand_his> etlJobHandHisList = new ArrayList<>();
		for (int i = 1; i < 6; i++) {
			Etl_job_hand_his etl_job_hand_his = new Etl_job_hand_his();
			etl_job_hand_his.setEtl_sys_cd(EtlSysCd);
			etl_job_hand_his.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			if (i == 1) {
				etl_job_hand_his.setEtl_job("测试作业" + i + THREAD_ID);
				etl_job_hand_his.setHand_status(Meddle_status.FALSE.getCode());
				etl_job_hand_his.setEvent_id(EventId);
				etl_job_hand_his.setEtl_hand_type(Meddle_type.JOB_RERUN.getCode());
			} else if (i == 2) {
				etl_job_hand_his.setEtl_job("测试作业" + i + THREAD_ID);
				etl_job_hand_his.setEvent_id(EventId2);
				etl_job_hand_his.setHand_status(Meddle_status.TRUE.getCode());
				etl_job_hand_his.setEtl_hand_type(Meddle_type.JOB_JUMP.getCode());
			} else if (i == 3) {
				etl_job_hand_his.setEtl_job("测试作业" + i + THREAD_ID);
				etl_job_hand_his.setHand_status(Meddle_status.DONE.getCode());
				etl_job_hand_his.setEvent_id(EventId3);
				etl_job_hand_his.setEtl_hand_type(Meddle_type.JOB_TRIGGER.getCode());
			} else if (i == 4) {
				etl_job_hand_his.setEtl_job("cszy" + i + THREAD_ID);
				etl_job_hand_his.setHand_status(Meddle_status.RUNNING.getCode());
				etl_job_hand_his.setEtl_hand_type(Meddle_type.JOB_PRIORITY.getCode());
				etl_job_hand_his.setEvent_id(EventId4);
			} else {
				etl_job_hand_his.setEtl_job("cszy" + i + THREAD_ID);
				etl_job_hand_his.setHand_status(Meddle_status.ERROR.getCode());
				etl_job_hand_his.setEvent_id(EventId5);
				etl_job_hand_his.setEtl_hand_type(Meddle_type.JOB_STOP.getCode());
			}
			etlJobHandHisList.add(etl_job_hand_his);
		}
		return etlJobHandHisList;
	}

	private List<Etl_job_hand> getEtl_job_hands() {
		List<Etl_job_hand> etlJobHandList = new ArrayList<>();
		for (int i = 1; i < 6; i++) {
			Etl_job_hand etl_job_hand = new Etl_job_hand();
			etl_job_hand.setEtl_sys_cd(EtlSysCd);
			etl_job_hand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			if (i == 1) {
				etl_job_hand.setEtl_job("测试作业" + i + THREAD_ID);
				etl_job_hand.setHand_status(Meddle_status.FALSE.getCode());
				etl_job_hand.setEvent_id(EventId);
				etl_job_hand.setEtl_hand_type(Meddle_type.JOB_RERUN.getCode());
			} else if (i == 2) {
				etl_job_hand.setEtl_job("测试作业" + i + THREAD_ID);
				etl_job_hand.setEvent_id(EventId2);
				etl_job_hand.setHand_status(Meddle_status.TRUE.getCode());
				etl_job_hand.setEtl_hand_type(Meddle_type.JOB_JUMP.getCode());
			} else if (i == 3) {
				etl_job_hand.setEtl_job("测试作业" + i + THREAD_ID);
				etl_job_hand.setHand_status(Meddle_status.DONE.getCode());
				etl_job_hand.setEtl_hand_type(Meddle_type.JOB_TRIGGER.getCode());
				etl_job_hand.setEvent_id(EventId3);
			} else if (i == 4) {
				etl_job_hand.setEtl_job("cszy" + i + THREAD_ID);
				etl_job_hand.setHand_status(Meddle_status.RUNNING.getCode());
				etl_job_hand.setEvent_id(EventId4);
				etl_job_hand.setEtl_hand_type(Meddle_type.JOB_PRIORITY.getCode());
			} else {
				etl_job_hand.setEtl_job("cszy" + i + THREAD_ID);
				etl_job_hand.setHand_status(Meddle_status.ERROR.getCode());
				etl_job_hand.setEvent_id(EventId5);
				etl_job_hand.setEtl_hand_type(Meddle_type.JOB_STOP.getCode());
			}
			etlJobHandList.add(etl_job_hand);
		}
		return etlJobHandList;
	}

	private List<Etl_job_cur> getEtl_job_curs() {
		List<Etl_job_cur> etlJobCurList = new ArrayList<>();
		for (int i = 1; i < 11; i++) {
			Etl_job_cur etl_job_cur = new Etl_job_cur();
			etl_job_cur.setPro_dic("/home/hyshf/dhw");
			etl_job_cur.setEtl_job_desc("测试作业");
			etl_job_cur.setPro_para("1");
			etl_job_cur.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			etl_job_cur.setToday_disp(Today_Dispatch_Flag.YES.getCode());
			etl_job_cur.setCurr_bath_date(DateUtil.getSysDate());
			if (i == 1) {
				etl_job_cur.setEtl_sys_cd(EtlSysCd);
				etl_job_cur.setEtl_job("测试作业" + i + THREAD_ID);
				etl_job_cur.setPro_name("zy.shell");
				etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
				etl_job_cur.setSub_sys_cd(SubSysCd);
				etl_job_cur.setDisp_freq(Dispatch_Frequency.PinLv.getCode());
				etl_job_cur.setExe_frequency(1L);
				etl_job_cur.setExe_num(1);
				etl_job_cur.setStar_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
						+ " " + DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
				etl_job_cur.setEnd_time("2020-12-31 10:30:30");
				etl_job_cur.setJob_disp_status(Job_Status.RUNNING.getCode());
			} else if (i == 2) {
				etl_job_cur.setEtl_sys_cd(EtlSysCd);
				etl_job_cur.setEtl_job("测试作业" + i + THREAD_ID);
				etl_job_cur.setPro_name("zy.jar");
				etl_job_cur.setPro_type(Pro_Type.JAVA.getCode());
				etl_job_cur.setSub_sys_cd(SubSysCd);
				etl_job_cur.setDisp_type(Dispatch_Type.TPLUS1.getCode());
				etl_job_cur.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
				etl_job_cur.setJob_disp_status(Job_Status.DONE.getCode());
			} else if (i == 3) {
				etl_job_cur.setEtl_sys_cd(EtlSysCd);
				etl_job_cur.setEtl_job("测试作业" + i + THREAD_ID);
				etl_job_cur.setPro_name("zy.shell");
				etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
				etl_job_cur.setSub_sys_cd(SubSysCd);
				etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
				etl_job_cur.setJob_disp_status(Job_Status.ERROR.getCode());
			} else if (i == 4) {
				etl_job_cur.setEtl_sys_cd(EtlSysCd);
				etl_job_cur.setEtl_job("cszy" + i + THREAD_ID);
				etl_job_cur.setPro_name("zy.py");
				etl_job_cur.setPro_type(Pro_Type.PYTHON.getCode());
				etl_job_cur.setSub_sys_cd(SubSysCd2);
				etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_cur.setDisp_freq(Dispatch_Frequency.WEEKLY.getCode());
				etl_job_cur.setJob_disp_status(Job_Status.WAITING.getCode());
			} else if (i == 5) {
				etl_job_cur.setEtl_job("cszy" + i + THREAD_ID);
				etl_job_cur.setEtl_sys_cd(EtlSysCd);
				etl_job_cur.setPro_name("zy.bat");
				etl_job_cur.setPro_type(Pro_Type.BAT.getCode());
				etl_job_cur.setSub_sys_cd(SubSysCd2);
				etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_cur.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
				etl_job_cur.setJob_disp_status(Job_Status.PENDING.getCode());
			} else if (i == 6) {
				etl_job_cur.setEtl_job("gyzy" + i + THREAD_ID);
				etl_job_cur.setEtl_sys_cd(EtlSysCd);
				etl_job_cur.setPro_name("zygy.shell");
				etl_job_cur.setPro_type(Pro_Type.BAT.getCode());
				etl_job_cur.setSub_sys_cd(SubSysCd2);
				etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_cur.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
				etl_job_cur.setJob_disp_status(Job_Status.RUNNING.getCode());
			} else if (i == 7) {
				etl_job_cur.setEtl_job("gyzy" + i + THREAD_ID);
				etl_job_cur.setEtl_sys_cd(EtlSysCd);
				etl_job_cur.setPro_name("zygy.shell");
				etl_job_cur.setPro_type(Pro_Type.BAT.getCode());
				etl_job_cur.setSub_sys_cd(SubSysCd2);
				etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_cur.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
				etl_job_cur.setJob_disp_status(Job_Status.WAITING.getCode());
			} else if (i == 8) {
				etl_job_cur.setEtl_job("gyzy" + i + THREAD_ID);
				etl_job_cur.setEtl_sys_cd(EtlSysCd);
				etl_job_cur.setPro_name("zygy.shell");
				etl_job_cur.setPro_type(Pro_Type.BAT.getCode());
				etl_job_cur.setSub_sys_cd(SubSysCd2);
				etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_cur.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
				etl_job_cur.setJob_disp_status(Job_Status.ERROR.getCode());
			} else if (i == 9) {
				etl_job_cur.setEtl_job("gyzy" + i + THREAD_ID);
				etl_job_cur.setEtl_sys_cd(EtlSysCd);
				etl_job_cur.setPro_name("zygy.shell");
				etl_job_cur.setPro_type(Pro_Type.BAT.getCode());
				etl_job_cur.setSub_sys_cd(SubSysCd2);
				etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_cur.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
				etl_job_cur.setJob_disp_status(Job_Status.DONE.getCode());
			} else {
				etl_job_cur.setEtl_job("gyzy" + i + THREAD_ID);
				etl_job_cur.setEtl_sys_cd(EtlSysCd);
				etl_job_cur.setPro_name("zygy.shell");
				etl_job_cur.setPro_type(Pro_Type.BAT.getCode());
				etl_job_cur.setSub_sys_cd(SubSysCd2);
				etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_cur.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
				etl_job_cur.setJob_disp_status(Job_Status.WAITING.getCode());
			}
			etlJobCurList.add(etl_job_cur);
		}
		return etlJobCurList;
	}

	private List<Etl_sub_sys_list> getEtl_sub_sys_lists() {
		List<Etl_sub_sys_list> subSysLists = new ArrayList<>();
		for (int i = 1; i < 3; i++) {
			Etl_sub_sys_list etl_sub_sys_list = new Etl_sub_sys_list();
			if (i == 1) {
				etl_sub_sys_list.setSub_sys_cd(SubSysCd);
			} else {
				etl_sub_sys_list.setSub_sys_cd(SubSysCd2);
			}
			etl_sub_sys_list.setEtl_sys_cd(EtlSysCd);
			etl_sub_sys_list.setSub_sys_desc("任务测试" + i);
			etl_sub_sys_list.setComments("测试" + i);
			subSysLists.add(etl_sub_sys_list);
		}
		return subSysLists;
	}

	private Etl_sys getEtl_sys() {
		Etl_sys etl_sys = new Etl_sys();
		etl_sys.setEtl_sys_cd(EtlSysCd);
		etl_sys.setEtl_sys_name("dhwcs");
		etl_sys.setUser_id(USER_ID);
		etl_sys.setCurr_bath_date(DateUtil.getSysDate());
		return etl_sys;
	}

	@Method(desc = "查询作业级干预作业情况",
			logicStep = "1.正确的数据访问1，数据都正确" +
					"2.正确的数据访问2，etl_job不为空" +
					"3.正确的数据访问3，sub_sys_desc不为空" +
					"4.正确的数据访问4，job_status不为空" +
					"5.错误的数据访问1，etl_sys_cd不存在")
	@Test
	public void searchJobLevelIntervention() {
		// 1.正确的数据访问1，数据都正确
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.post(getActionUrl("searchJobLevelIntervention"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> map = ar.getDataForMap();
		List<Map<String, Object>> etlJobInfoList = (List<Map<String, Object>>) map.get("etlJobInfoList");
		for (Map<String, Object> etlJobInfo : etlJobInfoList) {
			String etl_job = etlJobInfo.get("etl_job").toString();
			if (etl_job.equals("测试作业1" + THREAD_ID)) {
				assertThat(etlJobInfo.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(etlJobInfo.get("job_disp_status"), is(Job_Status.RUNNING.getCode()));
				assertThat(etlJobInfo.get("subsysname"), is("任务测试1(zygyrwcs)"));
				assertThat(etlJobInfo.get("curr_bath_date"), is(DateUtil.getSysDate()));
			} else if (etl_job.equals("测试作业2" + THREAD_ID)) {
				assertThat(etlJobInfo.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(etlJobInfo.get("job_disp_status"), is(Job_Status.DONE.getCode()));
				assertThat(etlJobInfo.get("subsysname"), is("任务测试1(zygyrwcs)"));
				assertThat(etlJobInfo.get("curr_bath_date"), is(DateUtil.getSysDate()));
			} else if (etl_job.equals("测试作业3" + THREAD_ID)) {
				assertThat(etlJobInfo.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(etlJobInfo.get("job_disp_status"), is(Job_Status.ERROR.getCode()));
				assertThat(etlJobInfo.get("subsysname"), is("任务测试1(zygyrwcs)"));
				assertThat(etlJobInfo.get("curr_bath_date"), is(DateUtil.getSysDate()));
			} else if (etl_job.equals("cszy4" + THREAD_ID)) {
				assertThat(etlJobInfo.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(etlJobInfo.get("job_disp_status"), is(Job_Status.WAITING.getCode()));
				assertThat(etlJobInfo.get("subsysname"), is("任务测试2(zygyrwcs2)"));
				assertThat(etlJobInfo.get("curr_bath_date"), is(DateUtil.getSysDate()));
			} else if (etl_job.equals("cszy5" + THREAD_ID)) {
				assertThat(etlJobInfo.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(etlJobInfo.get("job_disp_status"), is(Job_Status.PENDING.getCode()));
				assertThat(etlJobInfo.get("subsysname"), is("任务测试2(zygyrwcs2)"));
				assertThat(etlJobInfo.get("curr_bath_date"), is(DateUtil.getSysDate()));
			}
		}
		// 2.正确的数据访问2，etl_job不为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", "测试作业1" + THREAD_ID)
				.post(getActionUrl("searchJobLevelIntervention"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败！！"));
		assertThat(ar.isSuccess(), is(true));
		map = ar.getDataForMap();
		etlJobInfoList = (List<Map<String, Object>>) map.get("etlJobInfoList");
		assertThat(etlJobInfoList.get(0).get("job_disp_status"), is(Job_Status.RUNNING.getCode()));
		assertThat(etlJobInfoList.get(0).get("subsysname"), is("任务测试1(zygyrwcs)"));
		assertThat(etlJobInfoList.get(0).get("curr_bath_date"), is(DateUtil.getSysDate()));
		// 3.正确的数据访问3，sub_sys_desc不为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_desc", "任务测试1")
				.post(getActionUrl("searchJobLevelIntervention"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败！！"));
		assertThat(ar.isSuccess(), is(true));
		map = ar.getDataForMap();
		etlJobInfoList = (List<Map<String, Object>>) map.get("etlJobInfoList");
		for (Map<String, Object> etlJobInfo : etlJobInfoList) {
			if (etlJobInfo.get("etl_job").toString().equals("测试作业1" + THREAD_ID)) {
				assertThat(etlJobInfo.get("job_disp_status"), is(Job_Status.RUNNING.getCode()));
				assertThat(etlJobInfo.get("subsysname"), is("任务测试1(zygyrwcs)"));
				assertThat(etlJobInfo.get("curr_bath_date"), is(DateUtil.getSysDate()));
			} else if (etlJobInfo.get("etl_job").toString().equals("测试作业2" + THREAD_ID)) {
				assertThat(etlJobInfo.get("job_disp_status"), is(Job_Status.DONE.getCode()));
				assertThat(etlJobInfo.get("subsysname"), is("任务测试1(zygyrwcs)"));
				assertThat(etlJobInfo.get("curr_bath_date"), is(DateUtil.getSysDate()));
			} else if (etlJobInfo.get("etl_job").toString().equals("测试作业3" + THREAD_ID)) {
				assertThat(etlJobInfo.get("job_disp_status"), is(Job_Status.ERROR.getCode()));
				assertThat(etlJobInfo.get("subsysname"), is("任务测试1(zygyrwcs)"));
				assertThat(etlJobInfo.get("curr_bath_date"), is(DateUtil.getSysDate()));
			}
		}
		// 4.正确的数据访问4,job_status不为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("job_status", Job_Status.ERROR.getCode())
				.post(getActionUrl("searchJobLevelIntervention"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败！！"));
		assertThat(ar.isSuccess(), is(true));
		map = ar.getDataForMap();
		etlJobInfoList = (List<Map<String, Object>>) map.get("etlJobInfoList");
		assertThat(etlJobInfoList.size(), is(2));
		// 5.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "zyjgycs")
				.post(getActionUrl("searchJobLevelIntervention"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败！！"));
		assertThat(ar.isSuccess(), is(false));

	}

	@Method(desc = "查询作业级当前干预情况",
			logicStep = "1.正确的数据访问1，数据都正确" +
					"2.错误的数据访问1，etl_sys_cd不存在")
	@Test
	public void searchJobLevelCurrInterventionByPage() {
		// 1.正确的数据访问1，数据都正确
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.post(getActionUrl("searchJobLevelCurrInterventionByPage"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> map = ar.getDataForMap();
		List<Map<String, Object>> currInterventionList = (List<Map<String, Object>>) map.get("currInterventionList");
		// 验证数据的正确性
		checkInterventionData(currInterventionList);
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "zyjgycs")
				.post(getActionUrl("searchJobLevelCurrInterventionByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "验证数据的正确性",
			logicStep = "验证数据的正确性")
	private void checkInterventionData(List<Map<String, Object>> currInterventionList) {
		// 验证数据的正确性
		for (Map<String, Object> currIntervention : currInterventionList) {
			String event_id = currIntervention.get("event_id").toString();
			if (event_id.equals(EventId)) {
				assertThat(currIntervention.get("hand_status"), is(Meddle_status.FALSE.getCode()));
				assertThat(currIntervention.get("etl_hand_type"), is(Meddle_type.JOB_RERUN.getCode()));
				assertThat(currIntervention.get("etl_job"), is("测试作业1" + THREAD_ID));
				assertThat(currIntervention.get("subsysname"), is("任务测试1(zygyrwcs)"));
			} else if (event_id.equals(EventId2)) {
				assertThat(currIntervention.get("hand_status"), is(Meddle_status.TRUE.getCode()));
				assertThat(currIntervention.get("etl_hand_type"), is(Meddle_type.JOB_JUMP.getCode()));
				assertThat(currIntervention.get("etl_job"), is("测试作业2" + THREAD_ID));
				assertThat(currIntervention.get("subsysname"), is("任务测试1(zygyrwcs)"));
			} else if (event_id.equals(EventId3)) {
				assertThat(currIntervention.get("hand_status"), is(Meddle_status.DONE.getCode()));
				assertThat(currIntervention.get("etl_hand_type"), is(Meddle_type.JOB_TRIGGER.getCode()));
				assertThat(currIntervention.get("etl_job"), is("测试作业3" + THREAD_ID));
				assertThat(currIntervention.get("subsysname"), is("任务测试1(zygyrwcs)"));
			} else if (event_id.equals(EventId4)) {
				assertThat(currIntervention.get("hand_status"), is(Meddle_status.RUNNING.getCode()));
				assertThat(currIntervention.get("etl_hand_type"), is(Meddle_type.JOB_PRIORITY.getCode()));
				assertThat(currIntervention.get("etl_job"), is("cszy4" + THREAD_ID));
				assertThat(currIntervention.get("subsysname"), is("任务测试2(zygyrwcs2)"));
			} else if (event_id.equals(EventId5)) {
				assertThat(currIntervention.get("hand_status"), is(Meddle_status.ERROR.getCode()));
				assertThat(currIntervention.get("etl_hand_type"), is(Meddle_type.JOB_STOP.getCode()));
				assertThat(currIntervention.get("etl_job"), is("cszy5" + THREAD_ID));
				assertThat(currIntervention.get("subsysname"), is("任务测试2(zygyrwcs2)"));
			}
		}
	}

	@Method(desc = "分页查询作业级历史干预情况",
			logicStep = "1.正确的数据访问1，数据都正确" +
					"2.错误的数据访问1，etl_sys_cd不存在")
	@Test
	public void searchJobLeverHisInterventionByPage() {
		// 1.正确的数据访问1，数据都正确
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.post(getActionUrl("searchJobLeverHisInterventionByPage"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> map = ar.getDataForMap();
		List<Map<String, Object>> handHisList = (List<Map<String, Object>>) map.get("handHisList");
		checkInterventionData(handHisList);
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "zyjgycs")
				.post(getActionUrl("searchJobLeverHisInterventionByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "作业级干预操作",
			logicStep = "1.正确的数据访问1，数据都正确,干预类型为跳过" +
					"2.正确的数据访问2，数据都正确,job_priority不为空，干预类型为临时调整优先级" +
					"3.正确的数据访问3，干预类型为重跑" +
					"4.正确的数据访问4，干预类型为停止" +
					"5.正确的数据访问5，干预类型为强制执行" +
					"6.错误的数据访问1，etl_sys_cd不存在" +
					"7.错误的数据访问2，工程下有作业正在干预" +
					"8.错误的数据访问3，作业状态为完成或错误或停止的不可以停止" +
					"9.错误的数据访问4，作业状态为运行或完成的不可以跳过" +
					"10.错误的数据访问5，作业状态为挂起或运行或等待的不可以重跑" +
					"11.错误的数据访问6，作业状态为完成、错误、运行、停止不可以强制执行" +
					"12.错误的数据访问7，作业状态为运行不可以临时调整优先级")
	@Test
	public void jobLevelInterventionOperate() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正确的数据访问1，数据都正确,干预类型为跳过
			String bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "gyzy7" + THREAD_ID)
					.addData("etl_hand_type", Meddle_type.JOB_JUMP.getCode())
					.addData("curr_bath_date", DateUtil.getSysDate())
					.post(getActionUrl("jobLevelInterventionOperate"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(true));
			Etl_job_hand etlJobHand = SqlOperator.queryOneObject(db, Etl_job_hand.class, "select * from "
							+ Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job=? and etl_hand_type=?",
					EtlSysCd, "gyzy7" + THREAD_ID, Meddle_type.JOB_JUMP.getCode())
					.orElseThrow(() -> new BusinessException("sql查询错误！"));
			assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_JUMP.getCode()));
			assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
			assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
			assertThat(etlJobHand.getPro_para(), is(EtlSysCd + ",gyzy7" + THREAD_ID + ","
					+ DateUtil.getSysDate()));
			// 2.正确的数据访问2，数据都正确,job_priority不为空，干预类型为临时调整优先级
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "gyzy8" + THREAD_ID)
					.addData("etl_hand_type", Meddle_type.JOB_PRIORITY.getCode())
					.addData("curr_bath_date", DateUtil.getSysDate())
					.addData("job_priority", 99)
					.post(getActionUrl("jobLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(true));
			etlJobHand = SqlOperator.queryOneObject(db, Etl_job_hand.class, "select * from "
							+ Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job=? and etl_hand_type=?",
					EtlSysCd, "gyzy8" + THREAD_ID, Meddle_type.JOB_PRIORITY.getCode())
					.orElseThrow(() -> new BusinessException("sql查询错误！"));
			assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_PRIORITY.getCode()));
			assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
			assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
			assertThat(etlJobHand.getPro_para(), is(EtlSysCd + ",gyzy8" + THREAD_ID + ","
					+ DateUtil.getSysDate() + ",99"));
			// 3.正确的数据访问3，干预类型为重跑
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "gyzy9" + THREAD_ID)
					.addData("etl_hand_type", Meddle_type.JOB_RERUN.getCode())
					.addData("curr_bath_date", DateUtil.getSysDate())
					.post(getActionUrl("jobLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(true));
			etlJobHand = SqlOperator.queryOneObject(db, Etl_job_hand.class, "select * from "
							+ Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job=? and etl_hand_type=?",
					EtlSysCd, "gyzy9" + THREAD_ID, Meddle_type.JOB_RERUN.getCode())
					.orElseThrow(() -> new BusinessException("sql查询错误！"));
			assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_RERUN.getCode()));
			assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
			assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
			assertThat(etlJobHand.getPro_para(), is(EtlSysCd + ",gyzy9" + THREAD_ID + ","
					+ DateUtil.getSysDate()));
			// 4.正确的数据访问4，干预类型为停止
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "gyzy6" + THREAD_ID)
					.addData("etl_hand_type", Meddle_type.JOB_STOP.getCode())
					.addData("curr_bath_date", DateUtil.getSysDate())
					.post(getActionUrl("jobLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(true));
			etlJobHand = SqlOperator.queryOneObject(db, Etl_job_hand.class, "select * from "
							+ Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job=? and etl_hand_type=?",
					EtlSysCd, "gyzy6" + THREAD_ID, Meddle_type.JOB_STOP.getCode())
					.orElseThrow(() -> new BusinessException("sql查询错误！"));
			assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_STOP.getCode()));
			assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
			assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
			assertThat(etlJobHand.getPro_para(), is(EtlSysCd + ",gyzy6" + THREAD_ID + "," + DateUtil.getSysDate()));
			// 5.正确的数据访问5，干预类型为强制执行
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "gyzy10" + THREAD_ID)
					.addData("etl_hand_type", Meddle_type.JOB_TRIGGER.getCode())
					.addData("curr_bath_date", DateUtil.getSysDate())
					.post(getActionUrl("jobLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(true));
			etlJobHand = SqlOperator.queryOneObject(db, Etl_job_hand.class, "select * from "
							+ Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job=? and etl_hand_type=?",
					EtlSysCd, "gyzy10" + THREAD_ID, Meddle_type.JOB_TRIGGER.getCode())
					.orElseThrow(() -> new BusinessException("sql查询错误！"));
			assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_TRIGGER.getCode()));
			assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
			assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
			assertThat(etlJobHand.getPro_para(), is(EtlSysCd + ",gyzy10" + THREAD_ID + "," + DateUtil.getSysDate()));
			// 6.错误的数据访问1，etl_sys_cd不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", "zyjgycs")
					.addData("etl_job", "测试作业1" + THREAD_ID)
					.addData("etl_hand_type", Meddle_type.JOB_TRIGGER.getCode())
					.addData("curr_bath_date", DateUtil.getSysDate())
					.post(getActionUrl("jobLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 7.错误的数据访问2，工程下有作业正在干预
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1" + THREAD_ID)
					.addData("etl_hand_type", Meddle_type.JOB_TRIGGER.getCode())
					.addData("curr_bath_date", DateUtil.getSysDate())
					.post(getActionUrl("jobLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 8.错误的数据访问3，作业状态为完成或错误或停止的不可以停止
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业2" + THREAD_ID)
					.addData("etl_hand_type", Meddle_type.JOB_STOP.getCode())
					.addData("curr_bath_date", DateUtil.getSysDate())
					.post(getActionUrl("jobLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 9.错误的数据访问4，作业状态为运行或完成的不可以跳过
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1" + THREAD_ID)
					.addData("etl_hand_type", Meddle_type.JOB_JUMP.getCode())
					.addData("curr_bath_date", DateUtil.getSysDate())
					.post(getActionUrl("jobLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 10.错误的数据访问5，作业状态为挂起或运行或等待的不可以重跑
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "cszy5" + THREAD_ID)
					.addData("etl_hand_type", Meddle_type.JOB_RERUN.getCode())
					.addData("curr_bath_date", DateUtil.getSysDate())
					.post(getActionUrl("jobLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 11.错误的数据访问6，作业状态为完成、错误、运行、停止不可以强制执行
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业3" + THREAD_ID)
					.addData("etl_hand_type", Meddle_type.JOB_TRIGGER.getCode())
					.addData("curr_bath_date", DateUtil.getSysDate())
					.post(getActionUrl("jobLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 12.错误的数据访问7，作业状态为运行不可以临时调整优先级
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1" + THREAD_ID)
					.addData("etl_hand_type", Meddle_type.JOB_PRIORITY.getCode())
					.addData("curr_bath_date", DateUtil.getSysDate())
					.post(getActionUrl("jobLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "作业批量干预",
			logicStep = "1.正确的数据访问1.数据都正确,干预类型为强制执行" +
					"2.正确的数据访问2，干预类型为停止" +
					"3.正确的数据访问3，干预类型为跳过" +
					"4.正确的数据访问4，干预类型为重跑" +
					"5.正确的数据访问5，干预类型为临时调整优先级" +
					"6.错误的数据访问1，etl_sys_cd不存在" +
					"7.错误的数据访问2，工程下有作业正在干预")
	@Test
	public void batchJobLevelInterventionOperate() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<JobHandBean> list = getEtlJobList(new String[]{"测试作业8", "测试作业10"},
					Meddle_type.JOB_TRIGGER.getCode(), EtlSysCd);
			// 1.正确的数据访问1.数据都正确,干预类型为强制执行
			String bodyString = new HttpClient()
					.addData("jobHandBeans", JsonUtil.toJson(list))
					.post(getActionUrl("batchJobLevelInterventionOperate"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(true));
			List<Etl_job_hand> jobHandList = SqlOperator.queryList(db, Etl_job_hand.class,
					"select * from " + Etl_job_hand.TableName
							+ " where etl_sys_cd=? and etl_job in(?,?) and etl_hand_type=?",
					EtlSysCd, "测试作业10", "测试作业8", Meddle_type.JOB_TRIGGER.getCode());
			assertThat(jobHandList.size(), is(2));
			for (Etl_job_hand etlJobHand : jobHandList) {
				assertThat(etlJobHand.getEtl_sys_cd(), is(EtlSysCd));
				assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_TRIGGER.getCode()));
				assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
				assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
				if (etlJobHand.getEtl_job().equals("测试作业10")) {
					assertThat(etlJobHand.getPro_para(), is(EtlSysCd + ",测试作业10,"
							+ DateUtil.getSysDate()));
				} else if (etlJobHand.getEtl_job().equals("测试作业8")) {
					assertThat(etlJobHand.getPro_para(), is(EtlSysCd + ",测试作业8,"
							+ DateUtil.getSysDate()));
				}
			}
			// 2.正确的数据访问2，干预类型为停止
			list = getEtlJobList(new String[]{"测试作业11", "测试作业13"}, Meddle_type.JOB_STOP.getCode(), EtlSysCd);
			bodyString = new HttpClient()
					.addData("jobHandBeans", JsonUtil.toJson(list))
					.post(getActionUrl("batchJobLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(true));
			jobHandList = SqlOperator.queryList(db, Etl_job_hand.class, "select * from "
							+ Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job in(?,?) " +
							" and etl_hand_type=?", EtlSysCd, "测试作业11", "测试作业13",
					Meddle_type.JOB_STOP.getCode());
			assertThat(jobHandList.size(), is(2));
			for (Etl_job_hand etlJobHand : jobHandList) {
				assertThat(etlJobHand.getEtl_sys_cd(), is(EtlSysCd));
				assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_STOP.getCode()));
				assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
				assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
				if (etlJobHand.getEtl_job().equals("测试作业11")) {
					assertThat(etlJobHand.getPro_para(), is(EtlSysCd + ",测试作业11,"
							+ DateUtil.getSysDate()));
				} else if (etlJobHand.getEtl_job().equals("测试作业13")) {
					assertThat(etlJobHand.getPro_para(), is(EtlSysCd + ",测试作业13,"
							+ DateUtil.getSysDate()));
				}
			}
			// 3.正确的数据访问3，干预类型为跳过
			list = getEtlJobList(new String[]{"测试作业7", "测试作业9"}, Meddle_type.JOB_JUMP.getCode(), EtlSysCd);
			bodyString = new HttpClient()
					.addData("jobHandBeans", JsonUtil.toJson(list))
					.post(getActionUrl("batchJobLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(true));
			jobHandList = SqlOperator.queryList(db, Etl_job_hand.class, "select * from "
							+ Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job in(?,?) " +
							" and etl_hand_type=?", EtlSysCd, "测试作业7", "测试作业9",
					Meddle_type.JOB_JUMP.getCode());
			assertThat(jobHandList.size(), is(2));
			for (Etl_job_hand etlJobHand : jobHandList) {
				assertThat(etlJobHand.getEtl_sys_cd(), is(EtlSysCd));
				assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_JUMP.getCode()));
				assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
				assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
				if (etlJobHand.getEtl_job().equals("测试作业7")) {
					assertThat(etlJobHand.getPro_para(), is(EtlSysCd + ",测试作业7,"
							+ DateUtil.getSysDate()));
				} else if (etlJobHand.getEtl_job().equals("测试作业9")) {
					assertThat(etlJobHand.getPro_para(), is(EtlSysCd + ",测试作业9,"
							+ DateUtil.getSysDate()));
				}
			}
			// 4.正确的数据访问4，干预类型为重跑
			list = getEtlJobList(new String[]{"测试作业16", "测试作业17"}, Meddle_type.JOB_RERUN.getCode(), EtlSysCd);
			bodyString = new HttpClient()
					.addData("jobHandBeans", JsonUtil.toJson(list))
					.post(getActionUrl("batchJobLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(true));
			jobHandList = SqlOperator.queryList(db, Etl_job_hand.class, "select * from "
							+ Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job in(?,?) " +
							" and etl_hand_type=?", EtlSysCd, "测试作业16", "测试作业17",
					Meddle_type.JOB_RERUN.getCode());
			assertThat(jobHandList.size(), is(2));
			for (Etl_job_hand etlJobHand : jobHandList) {
				assertThat(etlJobHand.getEtl_sys_cd(), is(EtlSysCd));
				assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_RERUN.getCode()));
				assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
				assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
				if (etlJobHand.getEtl_job().equals("测试作业16")) {
					assertThat(etlJobHand.getPro_para(), is(EtlSysCd + ",测试作业16,"
							+ DateUtil.getSysDate()));
				} else if (etlJobHand.getEtl_job().equals("测试作业17")) {
					assertThat(etlJobHand.getPro_para(), is(EtlSysCd + ",测试作业17,"
							+ DateUtil.getSysDate()));
				}
			}
			// 5.正确的数据访问5，干预类型为临时调整优先级
			list = getEtlJobList(new String[]{"测试作业18", "测试作业19"}, Meddle_type.JOB_PRIORITY.getCode(),
					EtlSysCd);
			bodyString = new HttpClient()
					.addData("jobHandBeans", JsonUtil.toJson(list))
					.addData("job_priority", 99)
					.post(getActionUrl("batchJobLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(true));
			jobHandList = SqlOperator.queryList(db, Etl_job_hand.class, "select * from "
							+ Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job in(?,?) " +
							" and etl_hand_type=?", EtlSysCd, "测试作业18", "测试作业19",
					Meddle_type.JOB_PRIORITY.getCode());
			assertThat(jobHandList.size(), is(2));
			for (Etl_job_hand etlJobHand : jobHandList) {
				assertThat(etlJobHand.getEtl_sys_cd(), is(EtlSysCd));
				assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_PRIORITY.getCode()));
				assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
				assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
				if (etlJobHand.getEtl_job().equals("测试作业18")) {
					assertThat(etlJobHand.getPro_para(), is(EtlSysCd + ",测试作业18,"
							+ DateUtil.getSysDate() + ",99"));
				} else if (etlJobHand.getEtl_job().equals("测试作业19")) {
					assertThat(etlJobHand.getPro_para(), is(EtlSysCd + ",测试作业19,"
							+ DateUtil.getSysDate() + ",99"));
				}
			}
			// 6.错误的数据访问1，etl_sys_cd不存在
			List<JobHandBean> list2 = new ArrayList<>();
			for (String etl_job : new String[]{"测试作业18", "测试作业19"}) {
				JobHandBean jobHandBean = new JobHandBean();
				jobHandBean.setEtl_job(etl_job);
				jobHandBean.setEtl_sys_cd("aaa");
				jobHandBean.setEtl_hand_type(Meddle_type.JOB_TRIGGER.getCode());
				jobHandBean.setCurr_bath_date(DateUtil.getSysDate());
				list2.add(jobHandBean);
			}
			bodyString = new HttpClient()
					.addData("jobHandBeans", JsonUtil.toJson(list2))
					.post(getActionUrl("batchJobLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 7.错误的数据访问2，工程下有作业正在干预
			list = getEtlJobList(new String[]{"测试作业1" + THREAD_ID, "测试作业3" + THREAD_ID},
					Meddle_type.JOB_TRIGGER.getCode(), EtlSysCd);
			bodyString = new HttpClient()
					.addData("jobHandBeans", JsonUtil.toJson(list))
					.post(getActionUrl("batchJobLevelInterventionOperate"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("连接失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "获取干预作业数据集合", logicStep = "封装干预作业数据")
	private List<JobHandBean> getEtlJobList(String[] batchEtlJob, String etl_hand_type, String etlSysCd) {
		// 封装干预作业数据
		List<JobHandBean> list = new ArrayList<>();
		for (String etl_job : batchEtlJob) {
			JobHandBean jobHandBean = new JobHandBean();
			jobHandBean.setEtl_job(etl_job);
			jobHandBean.setEtl_sys_cd(etlSysCd);
			jobHandBean.setEtl_hand_type(etl_hand_type);
			jobHandBean.setCurr_bath_date(DateUtil.getSysDate());
			list.add(jobHandBean);
		}
		return list;
	}

	@After
	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 测试完成后删除Etl_sub_sys表测试数据
			SqlOperator.execute(db, "delete from " + Etl_sub_sys_list.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_sub_sys数据是否被删除
			long num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_sub_sys_list.TableName + " where etl_sys_cd=?",
					EtlSysCd)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完成后删除etl_sys表测试数据
			SqlOperator.execute(db, "delete from " + Etl_sys.TableName + " where etl_sys_cd=?", EtlSysCd);
			// 判断Etl_sub_sys数据是否被删除
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_sys.TableName + " where etl_sys_cd=?",
					EtlSysCd)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除Etl_job_hand表测试数据
			SqlOperator.execute(db, "delete from " + Etl_job_hand.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_sub_sys数据是否被删除
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_job_hand.TableName + " where etl_sys_cd=?",
					EtlSysCd)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除Etl_job_hand_his表测试数据
			SqlOperator.execute(db, "delete from " + Etl_job_hand_his.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_sub_sys数据是否被删除
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_job_hand_his.TableName + " where etl_sys_cd=?",
					EtlSysCd)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除Etl_job_cur表测试数据
			SqlOperator.execute(db, "delete from " + Etl_job_cur.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_sub_sys数据是否被删除
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_job_cur.TableName + " where etl_sys_cd=?",
					EtlSysCd)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
	}
}
