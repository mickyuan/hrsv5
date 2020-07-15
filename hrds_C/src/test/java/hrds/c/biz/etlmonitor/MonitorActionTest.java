package hrds.c.biz.etlmonitor;

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
import hrds.commons.utils.Constant;
import hrds.commons.utils.ParallerTestUtil;
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


public class MonitorActionTest extends WebBaseTestCase {

	//请填写测试用户需要做登录验证的A项目的登录验证的接口
	private static final String LOGIN_URL = ParallerTestUtil.TESTINITCONFIG.getString("login_url");
	// 已经存在的用户ID,用于模拟登录
	private static final long USER_ID = ParallerTestUtil.TESTINITCONFIG.getLong("user_id");
	private static final String PASSWORD = ParallerTestUtil.TESTINITCONFIG.getString("password");
	//主键ID
	private long nextId = PrimayKeyGener.getNextId();
	//当前线程的id
	private long THREAD_ID = Thread.currentThread().getId() + nextId;
	// 初始化工程编号
	private final String EtlSysCd = "zyjkcs" + THREAD_ID;
	// 初始化作业名称
	private final String etl_job = "jkcsjob";
	// 初始化资源类型
	private final String resourceType = "jkcsResource";
	// 初始化任务编号
	private final String SubSysCd = "zyjkrwcs";
	private final String SubSysCd2 = "zyjkrwcs2";
	// 初始化开始，结束时间
	private final String CURR_ST_TIME = DateUtil.getDateTime();
	private final String CURR_END_TIME = Constant.MAXDATE + " 000000";
	private final String CURR_BATH_TIME = DateUtil.getSysDate();

	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 构造etl_sys表测试数据
			Etl_sys etl_sys = getEtl_sys();
			assertThat("测试数据etl_sys初始化", etl_sys.add(db), is(1));
			// 构造etl_sub_sys_list表测试数据
			List<Etl_sub_sys_list> etlSubSysListList = getEtl_sub_sys_lists();
			etlSubSysListList.forEach(etl_sub_sys_list ->
					assertThat("测试数据data_source初始化", etl_sub_sys_list.add(db), is(1))
			);
			// 构造etl_job_curr表测试数据
			List<Etl_job_cur> etlJobCurList = getEtl_job_curs();
			etlJobCurList.forEach(etl_job_cur ->
					assertThat("测试数据etl_job_curr初始化", etl_job_cur.add(db), is(1))
			);
			// 初始化Etl_job_resource_rela表测试数据
			List<Etl_job_resource_rela> etlJobResourceRelas = getEtl_job_resource_relas();
			etlJobResourceRelas.forEach(etl_job_resource_rela ->
					assertThat("测试数据Etl_job_resource_rela初始化", etl_job_resource_rela.add(db), is(1))
			);
			// 构造etl_job_def表测试数据
			List<Etl_job_def> etlJobDefList = getEtl_job_defs();
			etlJobDefList.forEach(etl_job_def ->
					assertThat("测试数据etl_job_def初始化", etl_job_def.add(db), is(1))
			);
			// 构造etl_dependency测试数据
			List<Etl_dependency> etlDependencyList = getEtl_dependencies();
			etlDependencyList.forEach(etl_dependency ->
					assertThat("测试数据Etl_dependency初始化", etl_dependency.add(db), is(1))
			);
			// 构造etl_job_disp_his测试数据
			List<Etl_job_disp_his> etlJobDispHisList = getEtl_job_disp_his();
			etlJobDispHisList.forEach(etl_job_disp_his ->
					assertThat("测试数据etl_job_disp_his初始化", etl_job_disp_his.add(db), is(1))
			);
			// 构造etl_resource测试数据
			Etl_resource etl_resource = getEtl_resource();
			assertThat("测试数据etl_resource初始化", etl_resource.add(db), is(1));
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
		// 模拟用户登录
		String responseValue = new HttpClient()
				.buildSession()
				.addData("user_id", USER_ID)
				.addData("password", PASSWORD)
				.post(LOGIN_URL)
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat("用户登录", ar.isSuccess(), is(true));
	}

	private Etl_resource getEtl_resource() {
		Etl_resource etl_resource = new Etl_resource();
		etl_resource.setResource_type(resourceType);
		etl_resource.setResource_used(0);
		etl_resource.setResource_max(10);
		etl_resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
		etl_resource.setEtl_sys_cd(EtlSysCd);
		return etl_resource;
	}

	private List<Etl_job_disp_his> getEtl_job_disp_his() {
		List<Etl_job_disp_his> etlJobDispHisList = new ArrayList<>();
		for (int i = 1; i < 8; i++) {
			Etl_job_disp_his etl_job_disp_his = new Etl_job_disp_his();
			etl_job_disp_his.setEtl_sys_cd(EtlSysCd);
			etl_job_disp_his.setEtl_job(etl_job + i);
			etl_job_disp_his.setPro_dic("/home/hyshf/dhw");
			etl_job_disp_his.setEtl_job_desc("监控测试作业定义" + i);
			etl_job_disp_his.setPro_para("1");
			etl_job_disp_his.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			etl_job_disp_his.setToday_disp(Today_Dispatch_Flag.YES.getCode());
			etl_job_disp_his.setCurr_bath_date(DateUtil.getSysDate());
			if (i == 1) {
				etl_job_disp_his.setPro_name("zy.shell");
				etl_job_disp_his.setPro_type(Pro_Type.SHELL.getCode());
				etl_job_disp_his.setSub_sys_cd(SubSysCd);
				etl_job_disp_his.setDisp_freq(Dispatch_Frequency.PinLv.getCode());
				etl_job_disp_his.setExe_frequency(1L);
				etl_job_disp_his.setExe_num(1);
				etl_job_disp_his.setStar_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
						+ " " + DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
				etl_job_disp_his.setEnd_time(DateUtil.getDateTime());
				etl_job_disp_his.setJob_disp_status(Job_Status.ERROR.getCode());
				etl_job_disp_his.setCurr_st_time(CURR_ST_TIME);
				etl_job_disp_his.setCurr_end_time(CURR_END_TIME);
			} else if (i == 2) {
				etl_job_disp_his.setEtl_sys_cd(EtlSysCd);
				etl_job_disp_his.setPro_name("zy.jar");
				etl_job_disp_his.setPro_type(Pro_Type.JAVA.getCode());
				etl_job_disp_his.setSub_sys_cd(SubSysCd);
				etl_job_disp_his.setDisp_type(Dispatch_Type.TPLUS1.getCode());
				etl_job_disp_his.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
				etl_job_disp_his.setJob_disp_status(Job_Status.DONE.getCode());
				etl_job_disp_his.setCurr_st_time(CURR_ST_TIME);
				etl_job_disp_his.setCurr_end_time(CURR_END_TIME);
			} else if (i == 3) {
				etl_job_disp_his.setPro_name("zy.shell");
				etl_job_disp_his.setPro_type(Pro_Type.SHELL.getCode());
				etl_job_disp_his.setSub_sys_cd(SubSysCd);
				etl_job_disp_his.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_disp_his.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
				etl_job_disp_his.setJob_disp_status(Job_Status.RUNNING.getCode());
				etl_job_disp_his.setCurr_st_time(CURR_ST_TIME);
				etl_job_disp_his.setCurr_end_time(CURR_END_TIME);
			} else if (i == 4) {
				etl_job_disp_his.setPro_name("zy.py");
				etl_job_disp_his.setPro_type(Pro_Type.PYTHON.getCode());
				etl_job_disp_his.setSub_sys_cd(SubSysCd);
				etl_job_disp_his.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_disp_his.setDisp_freq(Dispatch_Frequency.WEEKLY.getCode());
				etl_job_disp_his.setJob_disp_status(Job_Status.WAITING.getCode());
				etl_job_disp_his.setCurr_st_time(CURR_ST_TIME);
				etl_job_disp_his.setCurr_end_time(CURR_END_TIME);
			} else if (i == 5) {
				etl_job_disp_his.setPro_name("zy.bat");
				etl_job_disp_his.setPro_type(Pro_Type.BAT.getCode());
				etl_job_disp_his.setSub_sys_cd(SubSysCd);
				etl_job_disp_his.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_disp_his.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
				etl_job_disp_his.setJob_disp_status(Job_Status.PENDING.getCode());
				etl_job_disp_his.setCurr_st_time(CURR_ST_TIME);
				etl_job_disp_his.setCurr_end_time(CURR_END_TIME);
			} else if (i == 6) {
				etl_job_disp_his.setPro_name("zy.bat");
				etl_job_disp_his.setPro_type(Pro_Type.BAT.getCode());
				etl_job_disp_his.setSub_sys_cd(SubSysCd);
				etl_job_disp_his.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_disp_his.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
				etl_job_disp_his.setJob_disp_status(Job_Status.STOP.getCode());
				etl_job_disp_his.setCurr_st_time(CURR_ST_TIME);
				etl_job_disp_his.setCurr_end_time(CURR_END_TIME);
			} else {
				etl_job_disp_his.setPro_name("zy.bat");
				etl_job_disp_his.setPro_type(Pro_Type.BAT.getCode());
				etl_job_disp_his.setSub_sys_cd(SubSysCd2);
				etl_job_disp_his.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_disp_his.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
				etl_job_disp_his.setJob_disp_status(Job_Status.STOP.getCode());
				etl_job_disp_his.setCurr_st_time(CURR_ST_TIME);
				etl_job_disp_his.setCurr_end_time(CURR_END_TIME);
			}
			etlJobDispHisList.add(etl_job_disp_his);
		}
		return etlJobDispHisList;
	}

	private List<Etl_dependency> getEtl_dependencies() {
		List<Etl_dependency> etlDependencyList = new ArrayList<>();
		for (int i = 1; i < 5; i++) {
			Etl_dependency etlDependency = new Etl_dependency();
			etlDependency.setEtl_sys_cd(EtlSysCd);
			etlDependency.setPre_etl_sys_cd(EtlSysCd);
			if (i == 1) {
				etlDependency.setEtl_job(etl_job + 3);
				etlDependency.setPre_etl_job(etl_job + 2);
			} else if (i == 2) {
				etlDependency.setEtl_job(etl_job + 3);
				etlDependency.setPre_etl_job(etl_job + 4);
			} else if (i == 3) {
				etlDependency.setEtl_job(etl_job + 4);
				etlDependency.setPre_etl_job(etl_job + 5);
			} else {
				etlDependency.setEtl_job(etl_job + 5);
				etlDependency.setPre_etl_job(etl_job + 3);
			}
			etlDependency.setMain_serv_sync(Main_Server_Sync.YES.getCode());
			etlDependency.setStatus(Status.TRUE.getCode());
			etlDependencyList.add(etlDependency);
		}
		return etlDependencyList;
	}

	private List<Etl_job_def> getEtl_job_defs() {
		List<Etl_job_def> etlJobDefList = new ArrayList<>();
		for (int i = 1; i < 8; i++) {
			Etl_job_def etl_job_def = new Etl_job_def();
			etl_job_def.setEtl_sys_cd(EtlSysCd);
			etl_job_def.setEtl_job(etl_job + i);
			etl_job_def.setPro_dic("/home/hyshf/dhw");
			etl_job_def.setEtl_job_desc(etl_job + i);
			etl_job_def.setPro_para("1");
			etl_job_def.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			etl_job_def.setToday_disp(Today_Dispatch_Flag.YES.getCode());
			etl_job_def.setCurr_bath_date(CURR_BATH_TIME);
			if (i == 1) {
				etl_job_def.setPro_name("zy.shell");
				etl_job_def.setPro_type(Pro_Type.SHELL.getCode());
				etl_job_def.setSub_sys_cd(SubSysCd);
				etl_job_def.setDisp_type(Dispatch_Frequency.PinLv.getCode());
				etl_job_def.setDisp_freq(Dispatch_Frequency.PinLv.getCode());
				etl_job_def.setExe_frequency(1);
				etl_job_def.setExe_num(1);
				etl_job_def.setStar_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
						+ " " + DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
				etl_job_def.setEnd_time("2020-12-31 10:30:30");
				etl_job_def.setJob_disp_status(Job_Status.ERROR.getCode());
			} else if (i == 2) {
				etl_job_def.setPro_name("zy.jar");
				etl_job_def.setPro_type(Pro_Type.JAVA.getCode());
				etl_job_def.setSub_sys_cd(SubSysCd);
				etl_job_def.setDisp_type(Dispatch_Type.TPLUS1.getCode());
				etl_job_def.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
				etl_job_def.setJob_disp_status(Job_Status.DONE.getCode());
			} else if (i == 3) {
				etl_job_def.setPro_name("zy.shell");
				etl_job_def.setPro_type(Pro_Type.SHELL.getCode());
				etl_job_def.setSub_sys_cd(SubSysCd);
				etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_def.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
				etl_job_def.setJob_disp_status(Job_Status.RUNNING.getCode());
			} else if (i == 4) {
				etl_job_def.setPro_name("zy.py");
				etl_job_def.setPro_type(Pro_Type.PYTHON.getCode());
				etl_job_def.setSub_sys_cd(SubSysCd);
				etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_def.setDisp_freq(Dispatch_Frequency.WEEKLY.getCode());
				etl_job_def.setJob_disp_status(Job_Status.WAITING.getCode());
			} else if (i == 5) {
				etl_job_def.setPro_name("zy.bat");
				etl_job_def.setPro_type(Pro_Type.BAT.getCode());
				etl_job_def.setSub_sys_cd(SubSysCd);
				etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_def.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
				etl_job_def.setJob_disp_status(Job_Status.PENDING.getCode());
			} else if (i == 6) {
				etl_job_def.setPro_name("zy.bat");
				etl_job_def.setPro_type(Pro_Type.BAT.getCode());
				etl_job_def.setSub_sys_cd(SubSysCd);
				etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_def.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
				etl_job_def.setJob_disp_status(Job_Status.STOP.getCode());
			} else {
				etl_job_def.setPro_name("zy.bat");
				etl_job_def.setPro_type(Pro_Type.BAT.getCode());
				etl_job_def.setSub_sys_cd(SubSysCd2);
				etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_def.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
				etl_job_def.setJob_disp_status(Job_Status.STOP.getCode());
			}
			etlJobDefList.add(etl_job_def);
		}
		return etlJobDefList;
	}

	private List<Etl_job_resource_rela> getEtl_job_resource_relas() {
		List<Etl_job_resource_rela> etlJobResourceRelas = new ArrayList<>();
		for (int i = 1; i < 4; i++) {
			Etl_job_resource_rela resourceRelation = new Etl_job_resource_rela();
			resourceRelation.setEtl_sys_cd(EtlSysCd);
			resourceRelation.setResource_type(resourceType);
			resourceRelation.setEtl_job(etl_job + i);
			resourceRelation.setResource_req(1);
			etlJobResourceRelas.add(resourceRelation);
		}
		return etlJobResourceRelas;
	}

	private List<Etl_job_cur> getEtl_job_curs() {
		List<Etl_job_cur> etlJobCurList = new ArrayList<>();
		for (int i = 1; i < 8; i++) {
			Etl_job_cur etl_job_cur = new Etl_job_cur();
			etl_job_cur.setEtl_job(etl_job + i);
			etl_job_cur.setPro_dic("/home/hyshf/dhw");
			etl_job_cur.setEtl_job_desc(etl_job + i);
			etl_job_cur.setPro_para("1");
			etl_job_cur.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
			etl_job_cur.setToday_disp(Today_Dispatch_Flag.YES.getCode());
			etl_job_cur.setDisp_time(DateUtil.getSysTime());
			etl_job_cur.setCurr_bath_date(CURR_BATH_TIME);
			if (i == 1) {
				etl_job_cur.setEtl_sys_cd(EtlSysCd);
				etl_job_cur.setPro_name("zy.shell");
				etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
				etl_job_cur.setSub_sys_cd(SubSysCd);
				etl_job_cur.setDisp_freq(Dispatch_Frequency.PinLv.getCode());
				etl_job_cur.setDisp_type(Dispatch_Frequency.PinLv.getCode());
				etl_job_cur.setExe_frequency(1L);
				etl_job_cur.setExe_num(1);
				etl_job_cur.setStar_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
						+ " " + DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
				etl_job_cur.setEnd_time("2020-12-31 10:30:30");
				etl_job_cur.setJob_disp_status(Job_Status.ERROR.getCode());
				etl_job_cur.setCurr_st_time("2019-12-17 11:42:37");
				etl_job_cur.setCurr_end_time("2019-12-17 11:43:37");
			} else if (i == 2) {
				etl_job_cur.setEtl_sys_cd(EtlSysCd);
				etl_job_cur.setPro_name("zy.jar");
				etl_job_cur.setPro_type(Pro_Type.JAVA.getCode());
				etl_job_cur.setSub_sys_cd(SubSysCd);
				etl_job_cur.setDisp_type(Dispatch_Type.TPLUS1.getCode());
				etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
				etl_job_cur.setJob_disp_status(Job_Status.DONE.getCode());
				etl_job_cur.setCurr_st_time("2019-12-17 14:42:37");
				etl_job_cur.setCurr_end_time("2019-12-17 14:43:37");
			} else if (i == 3) {
				etl_job_cur.setEtl_sys_cd(EtlSysCd);
				etl_job_cur.setPro_name("zy.shell");
				etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
				etl_job_cur.setSub_sys_cd(SubSysCd);
				etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
				etl_job_cur.setJob_disp_status(Job_Status.RUNNING.getCode());
				etl_job_cur.setCurr_st_time(CURR_ST_TIME);
				etl_job_cur.setCurr_end_time(CURR_END_TIME);
			} else if (i == 4) {
				etl_job_cur.setEtl_sys_cd(EtlSysCd);
				etl_job_cur.setPro_name("zy.py");
				etl_job_cur.setPro_type(Pro_Type.PYTHON.getCode());
				etl_job_cur.setSub_sys_cd(SubSysCd);
				etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_cur.setDisp_freq(Dispatch_Frequency.WEEKLY.getCode());
				etl_job_cur.setJob_disp_status(Job_Status.WAITING.getCode());
				etl_job_cur.setCurr_st_time(CURR_ST_TIME);
				etl_job_cur.setCurr_end_time(CURR_END_TIME);
			} else if (i == 5) {
				etl_job_cur.setEtl_sys_cd(EtlSysCd);
				etl_job_cur.setPro_name("zy.bat");
				etl_job_cur.setPro_type(Pro_Type.BAT.getCode());
				etl_job_cur.setSub_sys_cd(SubSysCd);
				etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_cur.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
				etl_job_cur.setJob_disp_status(Job_Status.PENDING.getCode());
				etl_job_cur.setCurr_st_time(CURR_ST_TIME);
				etl_job_cur.setCurr_end_time(CURR_END_TIME);
			} else if (i == 6) {
				etl_job_cur.setEtl_sys_cd(EtlSysCd);
				etl_job_cur.setPro_name("zy.bat");
				etl_job_cur.setPro_type(Pro_Type.BAT.getCode());
				etl_job_cur.setSub_sys_cd(SubSysCd);
				etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
				etl_job_cur.setJob_disp_status(Job_Status.STOP.getCode());
				etl_job_cur.setCurr_st_time(CURR_ST_TIME);
				etl_job_cur.setCurr_end_time(CURR_END_TIME);
			} else {
				etl_job_cur.setEtl_sys_cd(EtlSysCd);
				etl_job_cur.setPro_name("zy.bat");
				etl_job_cur.setPro_type(Pro_Type.BAT.getCode());
				etl_job_cur.setSub_sys_cd(SubSysCd2);
				etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
				etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
				etl_job_cur.setJob_disp_status(Job_Status.STOP.getCode());
				etl_job_cur.setCurr_st_time(CURR_ST_TIME);
				etl_job_cur.setCurr_end_time(CURR_END_TIME);
			}
			etlJobCurList.add(etl_job_cur);
		}
		return etlJobCurList;
	}

	private List<Etl_sub_sys_list> getEtl_sub_sys_lists() {
		List<Etl_sub_sys_list> etlSubSysListList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Etl_sub_sys_list etl_sub_sys_list = new Etl_sub_sys_list();
			if (i == 0) {
				etl_sub_sys_list.setSub_sys_cd(SubSysCd);
				etl_sub_sys_list.setSub_sys_desc("监控任务");
			} else {
				etl_sub_sys_list.setSub_sys_cd(SubSysCd2);
				etl_sub_sys_list.setSub_sys_desc("监控任务2");
			}
			etl_sub_sys_list.setEtl_sys_cd(EtlSysCd);
			etl_sub_sys_list.setComments("监控任务测试");
			etlSubSysListList.add(etl_sub_sys_list);
		}
		return etlSubSysListList;
	}

	private Etl_sys getEtl_sys() {
		Etl_sys etl_sys = new Etl_sys();
		etl_sys.setEtl_sys_cd(EtlSysCd);
		etl_sys.setEtl_sys_name("作业调度监控测试");
		etl_sys.setUser_id(USER_ID);
		etl_sys.setCurr_bath_date(CURR_BATH_TIME);
		return etl_sys;
	}

	@After
	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 测试完成后删除Etl_sub_sys_list表测试数据
			SqlOperator.execute(db,
					"delete from " + Etl_sub_sys_list.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_sub_sys数据是否被删除
			long num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_sub_sys_list.TableName + " where etl_sys_cd=?",
					EtlSysCd)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));

			// 测试完成后删除etl_sys表测试数据
			SqlOperator.execute(db,
					"delete from " + Etl_sys.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断etl_sys数据是否被删除
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_sys.TableName + " where etl_sys_cd=?",
					EtlSysCd)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除Etl_job_hand表测试数据
			SqlOperator.execute(db,
					"delete from " + Etl_job_hand.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_job_hand数据是否被删除
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_job_hand.TableName + " where etl_sys_cd=?",
					EtlSysCd)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除Etl_job_cur表测试数据
			SqlOperator.execute(db,
					"delete from " + Etl_job_cur.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_job_cur数据是否被删除
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_job_cur.TableName + " where etl_sys_cd=?",
					EtlSysCd)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除Etl_job_resource_rela表测试数据
			SqlOperator.execute(db,
					"delete from " + Etl_job_resource_rela.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_job_cur数据是否被删除
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_job_resource_rela.TableName + " where etl_sys_cd=?",
					EtlSysCd)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除Etl_job_resource_rela表测试数据
			SqlOperator.execute(db,
					"delete from " + Etl_job_def.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_job_cur数据是否被删除
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_job_def.TableName + " where etl_sys_cd=?",
					EtlSysCd)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除Etl_job_resource_rela表测试数据
			SqlOperator.execute(db,
					"delete from " + Etl_dependency.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_job_cur数据是否被删除
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_dependency.TableName + " where etl_sys_cd=?",
					EtlSysCd)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除etl_job_disp_his表测试数据
			SqlOperator.execute(db,
					"delete from " + Etl_job_disp_his.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断etl_job_disp_his数据是否被删除
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_job_disp_his.TableName + " where etl_sys_cd=?",
					EtlSysCd)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除Etl_resource表测试数据
			SqlOperator.execute(db,
					"delete from " + Etl_resource.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_resource数据是否被删除
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Etl_resource.TableName + " where etl_sys_cd=?",
					EtlSysCd)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
	}

	@Method(desc = "监控当前批量情况",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在")
	@Test
	public void monitorCurrentBatchInfo() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.post(getActionUrl("monitorCurrentBatchInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<String, Object> systemOperationStatus = ar.getDataForMap();
		assertThat(systemOperationStatus.get("waiting").toString(), is(String.valueOf(1)));
		assertThat(systemOperationStatus.get("pending").toString(), is(String.valueOf(1)));
		assertThat(systemOperationStatus.get("runing").toString(), is(String.valueOf(1)));
		assertThat(systemOperationStatus.get("error").toString(), is(String.valueOf(1)));
		assertThat(systemOperationStatus.get("done").toString(), is(String.valueOf(1)));
		assertThat(systemOperationStatus.get("suspension").toString(), is(String.valueOf(2)));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "jkdqplcs")
				.post(getActionUrl("monitorCurrentBatchInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Test
	public void monitorCurrentBatchInfoByTask() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.post(getActionUrl("monitorCurrentBatchInfoByTask"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		List<Map> currBatchInfoList = ar.getDataForEntityList(Map.class);
		for (Map currBatchInfo : currBatchInfoList) {
			assertThat(currBatchInfo.get("etl_sys_cd"), is(EtlSysCd));
			if (currBatchInfo.get("sub_sys_cd").toString().equals(SubSysCd)) {
				assertThat(currBatchInfo.get("sub_sys_desc"), is("监控任务(zyjkrwcs)"));
				assertThat(currBatchInfo.get("waiting").toString(), is(String.valueOf(1)));
				assertThat(currBatchInfo.get("pending").toString(), is(String.valueOf(1)));
				assertThat(currBatchInfo.get("runing").toString(), is(String.valueOf(1)));
				assertThat(currBatchInfo.get("error").toString(), is(String.valueOf(1)));
				assertThat(currBatchInfo.get("done").toString(), is(String.valueOf(1)));
				assertThat(currBatchInfo.get("suspension").toString(), is(String.valueOf(1)));
			} else if (currBatchInfo.get("sub_sys_cd").toString().equals(SubSysCd2)) {
				assertThat(currBatchInfo.get("sub_sys_desc"), is("监控任务2(zyjkrwcs2)"));
				assertThat(currBatchInfo.get("waiting").toString(), is(String.valueOf(0)));
				assertThat(currBatchInfo.get("pending").toString(), is(String.valueOf(0)));
				assertThat(currBatchInfo.get("runing").toString(), is(String.valueOf(0)));
				assertThat(currBatchInfo.get("error").toString(), is(String.valueOf(0)));
				assertThat(currBatchInfo.get("done").toString(), is(String.valueOf(0)));
				assertThat(currBatchInfo.get("suspension").toString(), is(String.valueOf(1)));
			}
		}

		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "jkdqplcs_dhw")
				.post(getActionUrl("monitorCurrentBatchInfoByTask"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "监控当前系统运行任务下的作业信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，sub_sys_cd不存在")
	@Test
	public void searchMonitorJobStateBySubCd() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", SubSysCd)
				.addData("curr_bath_date", CURR_BATH_TIME)
				.post(getActionUrl("searchMonitorJobStateBySubCd"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		List<Etl_job_cur> etlJobCurrList = ar.getDataForEntityList(Etl_job_cur.class);
		assertThat("初始化6条数据", etlJobCurrList.size(), is(6));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "jkdqplcs")
				.addData("sub_sys_cd", SubSysCd)
				.addData("curr_bath_date", CURR_BATH_TIME)
				.post(getActionUrl("searchMonitorJobStateBySubCd"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", "jkdqrwzycs")
				.addData("curr_bath_date", CURR_BATH_TIME)
				.post(getActionUrl("searchMonitorJobStateBySubCd"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "监控历史批量信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在")
	@Test
	public void monitorHistoryBatchInfo() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.post(getActionUrl("monitorHistoryBatchInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		List<Map<String, Object>> etlJobHisList = (List<Map<String, Object>>) ar.getData();
		assertThat("初始化两条数据", etlJobHisList.size(), is(2));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "jklspl")
				.post(getActionUrl("monitorHistoryBatchInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "监控当前作业信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，etl_job不存在")
	@Test
	public void monitorCurrJobInfo() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", etl_job + 1)
				.post(getActionUrl("monitorCurrJobInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> currJobInfo = ar.getDataForMap();
		assertThat(currJobInfo.get("com_exe_num").toString(), is(String.valueOf(0)));
		assertThat(currJobInfo.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
		assertThat(currJobInfo.get("disp_offset").toString(), is(String.valueOf(0)));
		assertThat(currJobInfo.get("exe_num").toString(), is(String.valueOf(1)));
		assertThat(currJobInfo.get("pro_para"), is("1"));
		assertThat(currJobInfo.get("job_disp_status"), is(Job_Status.ERROR.getCode()));
		assertThat(currJobInfo.get("job_priority").toString(), is(String.valueOf(0)));
		assertThat(currJobInfo.get("pro_dic"), is("/home/hyshf/dhw"));
		assertThat(currJobInfo.get("overlength_val").toString(), is(String.valueOf(0)));
		assertThat(currJobInfo.get("pro_type"), is(Pro_Type.SHELL.getCode()));
		assertThat(currJobInfo.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
		assertThat(currJobInfo.get("sub_sys_cd"), is(SubSysCd));
		assertThat(currJobInfo.get("disp_freq"), is(Dispatch_Frequency.PinLv.getCode()));
		assertThat(currJobInfo.get("curr_bath_date"), is(CURR_BATH_TIME));
		assertThat(currJobInfo.get("etl_job"), is(etl_job + 1));
		assertThat(currJobInfo.get("pro_name"), is("zy.shell"));
		Map<String, Object> resourceRelation = (Map<String, Object>) currJobInfo.get("resourceRelation");
		assertThat(resourceRelation.get("resource_type").toString(), is(resourceType));
		assertThat(resourceRelation.get("resource_req").toString(), is(String.valueOf(1)));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "jkzycs")
				.addData("etl_job", etl_job + 1)
				.post(getActionUrl("monitorCurrJobInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，etl_job不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", etl_job)
				.post(getActionUrl("monitorCurrJobInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "监控历史作业信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，etl_job不存在")
	@Test
	public void monitorHistoryJobInfo() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", etl_job + 1)
				.addData("start_date", CURR_BATH_TIME)
				.post(getActionUrl("monitorHistoryJobInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		List<Map> historyJob = ar.getDataForEntityList(Map.class);
		assertThat(historyJob.get(0).get("curr_bath_date"), is(CURR_BATH_TIME));
		assertThat(historyJob.get(0).get("curr_end_time"), is(CURR_END_TIME));
		assertThat(historyJob.get(0).get("sub_sys_cd"), is("监控任务(zyjkrwcs)"));
		assertThat(historyJob.get(0).get("curr_st_time"), is(CURR_ST_TIME));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "jklszycs")
				.addData("etl_job", etl_job + 1)
				.addData("start_date", CURR_BATH_TIME)
				.post(getActionUrl("monitorHistoryJobInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，etl_job不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", etl_job)
				.addData("start_date", CURR_BATH_TIME)
				.post(getActionUrl("monitorHistoryJobInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "监控依赖作业全作业搜索",
			logicStep = "1.正常的数据访问1，数据都正常,该方法返回的是数据格式为xml类型的数据，就不做数据验证了" +
					"2.错误的数据访问1，etl_sys_cd不存在")
	@Test
	public void monitorBatchEtlJobDependencyInfo() {
		// 1.正常的数据访问1，数据都正常,该方法返回的是数据格式为xml类型的数据，就不做数据验证了
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.post(getActionUrl("monitorBatchEtlJobDependencyInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "jkblzyylcs")
				.post(getActionUrl("monitorBatchEtlJobDependencyInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "查询监控历史批量作业信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，sub_sys_cd不存在")
	@Test
	public void searchMonitorHisBatchJobBySubCd() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", SubSysCd)
				.addData("curr_bath_date", CURR_BATH_TIME)
				.post(getActionUrl("searchMonitorHisBatchJobBySubCd"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		List<Map<String, Object>> historyBatchJobList = (List<Map<String, Object>>) ar.getData();
		for (Map<String, Object> historyBatchJob : historyBatchJobList) {
			assertThat(historyBatchJob.get("curr_end_time"), is(CURR_END_TIME));
			assertThat(historyBatchJob.get("curr_st_time"), is(CURR_ST_TIME));
			if (historyBatchJob.get("etl_job").toString().equals(etl_job + 1)) {
				assertThat(historyBatchJob.get("job_disp_status"), is(Job_Status.ERROR.getCode()));
			} else if (historyBatchJob.get("etl_job").toString().equals(etl_job + 2)) {
				assertThat(historyBatchJob.get("job_disp_status"), is(Job_Status.DONE.getCode()));
			} else if (historyBatchJob.get("etl_job").toString().equals(etl_job + 3)) {
				assertThat(historyBatchJob.get("job_disp_status"), is(Job_Status.RUNNING.getCode()));
			} else if (historyBatchJob.get("etl_job").toString().equals(etl_job + 4)) {
				assertThat(historyBatchJob.get("job_disp_status"), is(Job_Status.WAITING.getCode()));
			} else if (historyBatchJob.get("etl_job").toString().equals(etl_job + 5)) {
				assertThat(historyBatchJob.get("job_disp_status"), is(Job_Status.PENDING.getCode()));
			} else if (historyBatchJob.get("etl_job").toString().equals(etl_job + 6)) {
				assertThat(historyBatchJob.get("job_disp_status"), is(Job_Status.STOP.getCode()));
			}
		}
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "jklsplzycs")
				.addData("sub_sys_cd", SubSysCd)
				.addData("curr_bath_date", CURR_BATH_TIME)
				.post(getActionUrl("searchMonitorHisBatchJobBySubCd"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，sub_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", "jklsplzycs")
				.addData("curr_bath_date", CURR_BATH_TIME)
				.post(getActionUrl("searchMonitorHisBatchJobBySubCd"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "监控作业依赖信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，etl_job不存在")
	@Test
	public void monitorJobDependencyInfo() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", etl_job + 3)
				.post(getActionUrl("monitorJobDependencyInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> jobDependencyInfo = ar.getDataForMap();
		assertThat(jobDependencyInfo.get("id"), is("0"));
		assertThat(jobDependencyInfo.get("name"), is(etl_job + 3));
		assertThat(jobDependencyInfo.get("aid"), is("999"));
		// 校验作业依赖是否正确
		List<Map<String, Object>> downJobInfoList = (List<Map<String, Object>>) jobDependencyInfo.get("children");
		for (Map<String, Object> jobInfo : downJobInfoList) {
			String name = jobInfo.get("name").toString();
			if (name.equals(etl_job + 4)) {
				assertThat(jobInfo.get("id"), is(etl_job + 4));
				assertThat(jobInfo.get("etl_job"), is(etl_job + 3));
				assertThat(jobInfo.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(jobInfo.get("direction"), is("left"));
				assertThat(jobInfo.get("pre_etl_job"), is(etl_job + 4));
			} else if (name.equals(etl_job + 5)) {
				assertThat(jobInfo.get("id"), is(etl_job + 5));
				assertThat(jobInfo.get("etl_job"), is(etl_job + 5));
				assertThat(jobInfo.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(jobInfo.get("direction"), is("right"));
				assertThat(jobInfo.get("pre_etl_job"), is(etl_job + 3));
			} else if (name.equals(etl_job + 2)) {
				assertThat(jobInfo.get("id"), is(etl_job + 2));
				assertThat(jobInfo.get("etl_job"), is(etl_job + 3));
				assertThat(jobInfo.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(jobInfo.get("direction"), is("left"));
				assertThat(jobInfo.get("pre_etl_job"), is(etl_job + 2));
			}
		}
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "jkylcs")
				.addData("etl_job", etl_job + 3)
				.post(getActionUrl("monitorJobDependencyInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，etl_job不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", etl_job)
				.post(getActionUrl("monitorJobDependencyInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "监控系统资源状况",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在")
	@Test
	public void monitorSystemResourceInfo() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.post(getActionUrl("monitorSystemResourceInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> monitorSysResource = ar.getDataForMap();
		List<Map<String, Object>> etlResourceList = (List<Map<String, Object>>)
				monitorSysResource.get("etlResourceList");
		for (Map<String, Object> etlResource : etlResourceList) {
			assertThat(etlResource.get("etl_sys_cd"), is(EtlSysCd));
			if (etlResource.get("resource_type").toString().equals("resource")) {
				assertThat(etlResource.get("resource_max").toString(), is(String.valueOf(10)));
				assertThat(etlResource.get("resource_used").toString(), is(String.valueOf(1)));
				assertThat(etlResource.get("free").toString(), is(String.valueOf(9)));
			} else if (etlResource.get("resource_type").toString().equals("resource2")) {
				assertThat(etlResource.get("resource_max").toString(), is(String.valueOf(10)));
				assertThat(etlResource.get("resource_used").toString(), is(String.valueOf(2)));
				assertThat(etlResource.get("free").toString(), is(String.valueOf(8)));
			} else if (etlResource.get("resource_type").toString().equals("resource3")) {
				assertThat(etlResource.get("resource_max").toString(), is(String.valueOf(10)));
				assertThat(etlResource.get("resource_used").toString(), is(String.valueOf(3)));
				assertThat(etlResource.get("free").toString(), is(String.valueOf(7)));
			} else if (etlResource.get("resource_type").toString().equals("yarn")) {
				assertThat(etlResource.get("resource_max").toString(), is(String.valueOf(10)));
				assertThat(etlResource.get("resource_used").toString(), is(String.valueOf(5)));
				assertThat(etlResource.get("free").toString(), is(String.valueOf(5)));
			} else if (etlResource.get("resource_type").toString().equals("Thrift")) {
				assertThat(etlResource.get("resource_max").toString(), is(String.valueOf(10)));
				assertThat(etlResource.get("resource_used").toString(), is(String.valueOf(4)));
				assertThat(etlResource.get("free").toString(), is(String.valueOf(6)));
			}
		}
		List<Map<String, Object>> jobRunList = (List<Map<String, Object>>)
				monitorSysResource.get("jobRunList");
		for (Map<String, Object> jobRunInfo : jobRunList) {
			if (jobRunInfo.get("etl_job").toString().equals("监控测试3(监控测试作业定义3)")) {
				assertThat(jobRunInfo.get("job_disp_status"), is("R"));
				assertThat(jobRunInfo.get("sub_sys_cd"), is("zyjkrwcs(监控任务)"));
				assertThat(jobRunInfo.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(jobRunInfo.get("resource_req").toString(), is(String.valueOf(1)));
				assertThat(jobRunInfo.get("resource_type"), is("resource3"));
				assertThat(jobRunInfo.get("curr_st_time"), is(CURR_ST_TIME));
			}
		}
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "jkxtzycs")
				.post(getActionUrl("monitorSystemResourceInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "监控所有项目图表数据",
			logicStep = "1.正常的数据访问1，数据都正常,该方法只有一种情况")
	@Test
	public void monitorAllProjectChartsData() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.post(getActionUrl("monitorAllProjectChartsData"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
	}

}
