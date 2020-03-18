package hrds.c.biz.etlmonitor;

import fd.ng.core.annotation.Method;
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
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class MonitorActionTest extends WebBaseTestCase {

	// 初始化登录用户ID
	private static final long UserId = 6666L;
	// 初始化创建用户ID
	private static final long CreateId = 1000L;
	// 测试部门ID dep_id,测试作业调度部门
	private static final long DepId = 1000011L;
	// 初始化工程编号
	private static final String EtlSysCd = "zyjkcs";
	// 初始化任务编号
	private static final String SubSysCd = "zyjkrwcs";
	private static final String SubSysCd2 = "zyjkrwcs2";

	@Before
	public void before() {
		// 初始化作业干预测试数据
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.构造sys_user表测试数据
			Sys_user sysUser = new Sys_user();
			sysUser.setUser_id(UserId);
			sysUser.setCreate_id(CreateId);
			sysUser.setDep_id(DepId);
			sysUser.setCreate_date(DateUtil.getSysDate());
			sysUser.setCreate_time(DateUtil.getSysTime());
			sysUser.setRole_id("1001");
			sysUser.setUser_name("作业监控功能测试");
			sysUser.setUser_password("1");
			sysUser.setUser_type(UserType.CaiJiYongHu.getCode());
			sysUser.setUseris_admin("1");
			sysUser.setUsertype_group("02,03,04,08");
			sysUser.setUser_state(IsFlag.Shi.getCode());
			int num = sysUser.add(db);
			assertThat("测试数据sys_user数据初始化", num, is(1));
			// 2.构造department_info部门表测试数据
			Department_info department_info = new Department_info();
			department_info.setDep_id(DepId);
			department_info.setDep_name("测试作业调度部门");
			department_info.setCreate_date(DateUtil.getSysDate());
			department_info.setCreate_time(DateUtil.getSysTime());
			department_info.setDep_remark("测试");
			num = department_info.add(db);
			assertThat("测试数据department_info初始化", num, is(1));
			// 3.构造etl_sys表测试数据
			Etl_sys etl_sys = new Etl_sys();
			etl_sys.setEtl_sys_cd(EtlSysCd);
			etl_sys.setEtl_sys_name("作业调度监控测试");
			etl_sys.setUser_id(UserId);
			etl_sys.setCurr_bath_date(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString());
			num = etl_sys.add(db);
			assertThat("测试数据etl_sys初始化", num, is(1));
			// 4.构造etl_sub_sys_list表测试数据
			Etl_sub_sys_list etl_sub_sys_list = new Etl_sub_sys_list();
			for (int i = 0; i < 2; i++) {
				if (i == 0) {
					etl_sub_sys_list.setSub_sys_cd(SubSysCd);
					etl_sub_sys_list.setSub_sys_desc("监控任务");
				} else {
					etl_sub_sys_list.setSub_sys_cd(SubSysCd2);
					etl_sub_sys_list.setSub_sys_desc("监控任务2");
				}
				etl_sub_sys_list.setEtl_sys_cd(EtlSysCd);
				etl_sub_sys_list.setComments("监控任务测试");
				num = etl_sub_sys_list.add(db);
				assertThat("测试数据data_source初始化", num, is(1));
			}
			// 5.构造etl_job_curr表测试数据
			for (int i = 1; i < 8; i++) {
				Etl_job_cur etl_job_cur = new Etl_job_cur();
				etl_job_cur.setEtl_job("监控作业测试" + i);
				etl_job_cur.setPro_dic("/home/hyshf/dhw");
				etl_job_cur.setEtl_job_desc("监控测试作业定义" + i);
				etl_job_cur.setPro_para("1");
				etl_job_cur.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
				etl_job_cur.setToday_disp(Today_Dispatch_Flag.YES.getCode());
				etl_job_cur.setDisp_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()) + " " +
						DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
				etl_job_cur.setCurr_bath_date(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString());
				switch (i) {
					case 1:
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
						break;
					case 2:
						etl_job_cur.setEtl_sys_cd(EtlSysCd);
						etl_job_cur.setPro_name("zy.jar");
						etl_job_cur.setPro_type(Pro_Type.JAVA.getCode());
						etl_job_cur.setSub_sys_cd(SubSysCd);
						etl_job_cur.setDisp_type(Dispatch_Type.TPLUS1.getCode());
						etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
						etl_job_cur.setJob_disp_status(Job_Status.DONE.getCode());
						etl_job_cur.setCurr_st_time("2019-12-17 14:42:37");
						etl_job_cur.setCurr_end_time("2019-12-17 14:43:37");
						break;
					case 3:
						etl_job_cur.setEtl_sys_cd(EtlSysCd);
						etl_job_cur.setPro_name("zy.shell");
						etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
						etl_job_cur.setSub_sys_cd(SubSysCd);
						etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
						etl_job_cur.setJob_disp_status(Job_Status.RUNNING.getCode());
						etl_job_cur.setCurr_st_time("2019-12-17 16:42:37");
						etl_job_cur.setCurr_end_time("2019-12-17 16:43:37");
						break;
					case 4:
						etl_job_cur.setEtl_sys_cd(EtlSysCd);
						etl_job_cur.setPro_name("zy.py");
						etl_job_cur.setPro_type(Pro_Type.PYTHON.getCode());
						etl_job_cur.setSub_sys_cd(SubSysCd);
						etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_cur.setDisp_freq(Dispatch_Frequency.WEEKLY.getCode());
						etl_job_cur.setJob_disp_status(Job_Status.WAITING.getCode());
						etl_job_cur.setCurr_st_time("2019-12-17 17:42:37");
						etl_job_cur.setCurr_end_time("2019-12-17 17:43:37");
						break;
					case 5:
						etl_job_cur.setEtl_sys_cd(EtlSysCd);
						etl_job_cur.setPro_name("zy.bat");
						etl_job_cur.setPro_type(Pro_Type.BAT.getCode());
						etl_job_cur.setSub_sys_cd(SubSysCd);
						etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_cur.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
						etl_job_cur.setJob_disp_status(Job_Status.PENDING.getCode());
						etl_job_cur.setCurr_st_time("2019-12-17 19:42:37");
						etl_job_cur.setCurr_end_time("2019-12-17 19:43:37");
						break;
					case 6:
						etl_job_cur.setEtl_sys_cd(EtlSysCd);
						etl_job_cur.setPro_name("zy.bat");
						etl_job_cur.setPro_type(Pro_Type.BAT.getCode());
						etl_job_cur.setSub_sys_cd(SubSysCd);
						etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
						etl_job_cur.setJob_disp_status(Job_Status.STOP.getCode());
						etl_job_cur.setCurr_st_time("2019-12-17 21:42:37");
						etl_job_cur.setCurr_end_time("2019-12-17 21:43:37");
						break;
					case 7:
						etl_job_cur.setEtl_sys_cd(EtlSysCd);
						etl_job_cur.setPro_name("zy.bat");
						etl_job_cur.setPro_type(Pro_Type.BAT.getCode());
						etl_job_cur.setSub_sys_cd(SubSysCd2);
						etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
						etl_job_cur.setJob_disp_status(Job_Status.STOP.getCode());
						etl_job_cur.setCurr_st_time("2019-12-17 23:42:37");
						etl_job_cur.setCurr_end_time("2019-12-17 23:43:37");
						break;
				}
				num = etl_job_cur.add(db);
				assertThat("测试数据etl_job_curr初始化", num, is(1));
			}
			// 6.Etl_job_resource_rela表测试数据初始化
			Etl_job_resource_rela resourceRelation = new Etl_job_resource_rela();
			for (int i = 0; i < 3; i++) {
				resourceRelation.setEtl_sys_cd(EtlSysCd);
				if (i == 0) {
					resourceRelation.setResource_type("resource");
					resourceRelation.setEtl_job("监控作业测试1");
				} else if (i == 1) {
					resourceRelation.setResource_type("resource2");
					resourceRelation.setEtl_job("监控作业测试2");
				} else {
					resourceRelation.setResource_type("resource3");
					resourceRelation.setEtl_job("监控作业测试3");
				}
				resourceRelation.setResource_req(1);
				num = resourceRelation.add(db);
				assertThat("测试数据Etl_job_resource_rela初始化", num, is(1));
			}
			// 7.构造etl_job_def表测试数据
			for (int i = 1; i < 8; i++) {
				Etl_job_def etl_job_def = new Etl_job_def();
				etl_job_def.setEtl_sys_cd(EtlSysCd);
				etl_job_def.setEtl_job("监控作业测试" + i);
				etl_job_def.setPro_dic("/home/hyshf/dhw");
				etl_job_def.setEtl_job_desc("监控测试作业定义" + i);
				etl_job_def.setPro_para("1");
				etl_job_def.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
				etl_job_def.setToday_disp(Today_Dispatch_Flag.YES.getCode());
				etl_job_def.setCurr_bath_date(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString());
				etl_job_def.setDisp_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()) + " " +
						DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
				switch (i) {
					case 1:
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
						break;
					case 2:
						etl_job_def.setPro_name("zy.jar");
						etl_job_def.setPro_type(Pro_Type.JAVA.getCode());
						etl_job_def.setSub_sys_cd(SubSysCd);
						etl_job_def.setDisp_type(Dispatch_Type.TPLUS1.getCode());
						etl_job_def.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
						etl_job_def.setJob_disp_status(Job_Status.DONE.getCode());
						break;
					case 3:
						etl_job_def.setPro_name("zy.shell");
						etl_job_def.setPro_type(Pro_Type.SHELL.getCode());
						etl_job_def.setSub_sys_cd(SubSysCd);
						etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_def.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
						etl_job_def.setJob_disp_status(Job_Status.RUNNING.getCode());
						break;
					case 4:
						etl_job_def.setPro_name("zy.py");
						etl_job_def.setPro_type(Pro_Type.PYTHON.getCode());
						etl_job_def.setSub_sys_cd(SubSysCd);
						etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_def.setDisp_freq(Dispatch_Frequency.WEEKLY.getCode());
						etl_job_def.setJob_disp_status(Job_Status.WAITING.getCode());
						break;
					case 5:
						etl_job_def.setPro_name("zy.bat");
						etl_job_def.setPro_type(Pro_Type.BAT.getCode());
						etl_job_def.setSub_sys_cd(SubSysCd);
						etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_def.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
						etl_job_def.setJob_disp_status(Job_Status.PENDING.getCode());
						break;
					case 6:
						etl_job_def.setPro_name("zy.bat");
						etl_job_def.setPro_type(Pro_Type.BAT.getCode());
						etl_job_def.setSub_sys_cd(SubSysCd);
						etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_def.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
						etl_job_def.setJob_disp_status(Job_Status.STOP.getCode());
						break;
					case 7:
						etl_job_def.setPro_name("zy.bat");
						etl_job_def.setPro_type(Pro_Type.BAT.getCode());
						etl_job_def.setSub_sys_cd(SubSysCd2);
						etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_def.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
						etl_job_def.setJob_disp_status(Job_Status.STOP.getCode());
						break;
				}
				num = etl_job_def.add(db);
				assertThat("测试数据etl_job_def初始化", num, is(1));
			}
			// 7.构造etl_dependency测试数据
			Etl_dependency etlDependency = new Etl_dependency();
			for (int i = 0; i < 4; i++) {
				etlDependency.setEtl_sys_cd(EtlSysCd);
				etlDependency.setPre_etl_sys_cd(EtlSysCd);
				if (i == 0) {
					etlDependency.setEtl_job("监控作业测试3");
					etlDependency.setPre_etl_job("监控作业测试2");
				} else if (i == 1) {
					etlDependency.setEtl_job("监控作业测试3");
					etlDependency.setPre_etl_job("监控作业测试4");
				} else if (i == 2) {
					etlDependency.setEtl_job("监控作业测试4");
					etlDependency.setPre_etl_job("监控作业测试5");
				} else if (i == 3) {
					etlDependency.setEtl_job("监控作业测试5");
					etlDependency.setPre_etl_job("监控作业测试3");
				}
				etlDependency.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etlDependency.setStatus(Status.TRUE.getCode());
				num = etlDependency.add(db);
				assertThat("测试数据Etl_dependency初始化", num, is(1));
			}
			// 8.构造etl_job_disp_his测试数据
			for (int i = 1; i < 8; i++) {
				Etl_job_disp_his etl_job_disp_his = new Etl_job_disp_his();
				etl_job_disp_his.setEtl_job("监控作业测试" + i);
				etl_job_disp_his.setPro_dic("/home/hyshf/dhw");
				etl_job_disp_his.setEtl_job_desc("监控测试作业定义" + i);
				etl_job_disp_his.setPro_para("1");
				etl_job_disp_his.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
				etl_job_disp_his.setToday_disp(Today_Dispatch_Flag.YES.getCode());
				etl_job_disp_his.setCurr_bath_date(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
						.toString());
				switch (i) {
					case 1:
						etl_job_disp_his.setEtl_sys_cd(EtlSysCd);
						etl_job_disp_his.setPro_name("zy.shell");
						etl_job_disp_his.setPro_type(Pro_Type.SHELL.getCode());
						etl_job_disp_his.setSub_sys_cd(SubSysCd);
						etl_job_disp_his.setDisp_freq(Dispatch_Frequency.PinLv.getCode());
						etl_job_disp_his.setExe_frequency(1L);
						etl_job_disp_his.setExe_num(1);
						etl_job_disp_his.setStar_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
								+ " " + DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
						etl_job_disp_his.setEnd_time("2020-12-31 10:30:30");
						etl_job_disp_his.setJob_disp_status(Job_Status.ERROR.getCode());
						etl_job_disp_his.setCurr_st_time("2019-12-17 11:42:37");
						etl_job_disp_his.setCurr_end_time("2019-12-17 11:43:37");
						break;
					case 2:
						etl_job_disp_his.setEtl_sys_cd(EtlSysCd);
						etl_job_disp_his.setPro_name("zy.jar");
						etl_job_disp_his.setPro_type(Pro_Type.JAVA.getCode());
						etl_job_disp_his.setSub_sys_cd(SubSysCd);
						etl_job_disp_his.setDisp_type(Dispatch_Type.TPLUS1.getCode());
						etl_job_disp_his.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
						etl_job_disp_his.setJob_disp_status(Job_Status.DONE.getCode());
						etl_job_disp_his.setCurr_st_time("2019-12-17 14:42:37");
						etl_job_disp_his.setCurr_end_time("2019-12-17 14:43:37");
						break;
					case 3:
						etl_job_disp_his.setEtl_sys_cd(EtlSysCd);
						etl_job_disp_his.setPro_name("zy.shell");
						etl_job_disp_his.setPro_type(Pro_Type.SHELL.getCode());
						etl_job_disp_his.setSub_sys_cd(SubSysCd);
						etl_job_disp_his.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_disp_his.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
						etl_job_disp_his.setJob_disp_status(Job_Status.RUNNING.getCode());
						etl_job_disp_his.setCurr_st_time("2019-12-17 16:42:37");
						etl_job_disp_his.setCurr_end_time("2019-12-17 16:43:37");
						break;
					case 4:
						etl_job_disp_his.setEtl_sys_cd(EtlSysCd);
						etl_job_disp_his.setPro_name("zy.py");
						etl_job_disp_his.setPro_type(Pro_Type.PYTHON.getCode());
						etl_job_disp_his.setSub_sys_cd(SubSysCd);
						etl_job_disp_his.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_disp_his.setDisp_freq(Dispatch_Frequency.WEEKLY.getCode());
						etl_job_disp_his.setJob_disp_status(Job_Status.WAITING.getCode());
						etl_job_disp_his.setCurr_st_time("2019-12-17 17:42:37");
						etl_job_disp_his.setCurr_end_time("2019-12-17 17:43:37");
						break;
					case 5:
						etl_job_disp_his.setEtl_sys_cd(EtlSysCd);
						etl_job_disp_his.setPro_name("zy.bat");
						etl_job_disp_his.setPro_type(Pro_Type.BAT.getCode());
						etl_job_disp_his.setSub_sys_cd(SubSysCd);
						etl_job_disp_his.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_disp_his.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
						etl_job_disp_his.setJob_disp_status(Job_Status.PENDING.getCode());
						etl_job_disp_his.setCurr_st_time("2019-12-17 19:42:37");
						etl_job_disp_his.setCurr_end_time("2019-12-17 19:43:37");
						break;
					case 6:
						etl_job_disp_his.setEtl_sys_cd(EtlSysCd);
						etl_job_disp_his.setPro_name("zy.bat");
						etl_job_disp_his.setPro_type(Pro_Type.BAT.getCode());
						etl_job_disp_his.setSub_sys_cd(SubSysCd);
						etl_job_disp_his.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_disp_his.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
						etl_job_disp_his.setJob_disp_status(Job_Status.STOP.getCode());
						etl_job_disp_his.setCurr_st_time("2019-12-17 21:42:37");
						etl_job_disp_his.setCurr_end_time("2019-12-17 21:43:37");
						break;
					case 7:
						etl_job_disp_his.setEtl_sys_cd(EtlSysCd);
						etl_job_disp_his.setPro_name("zy.bat");
						etl_job_disp_his.setPro_type(Pro_Type.BAT.getCode());
						etl_job_disp_his.setSub_sys_cd(SubSysCd2);
						etl_job_disp_his.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_disp_his.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
						etl_job_disp_his.setJob_disp_status(Job_Status.STOP.getCode());
						etl_job_disp_his.setCurr_st_time("2019-12-17 23:42:37");
						etl_job_disp_his.setCurr_end_time("2019-12-17 23:43:37");
						break;
				}
				num = etl_job_disp_his.add(db);
				assertThat("测试数据etl_job_disp_hisr初始化", num, is(1));
			}
			// 8.构造etl_resource测试数据
			Etl_resource etl_resource = new Etl_resource();
			for (int i = 0; i < 5; i++) {
				if (i == 0) {
					etl_resource.setResource_type("resource");
					etl_resource.setResource_used(1);
				} else if (i == 1) {
					etl_resource.setResource_type("resource2");
					etl_resource.setResource_used(2);
				} else if (i == 2) {
					etl_resource.setResource_type("resource3");
					etl_resource.setResource_used(3);
				} else if (i == 3) {
					etl_resource.setResource_type("Thrift");
					etl_resource.setResource_used(4);
				} else if (i == 4) {
					etl_resource.setResource_type("Yarn");
					etl_resource.setResource_used(5);
				}
				etl_resource.setResource_max(10);
				etl_resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etl_resource.setEtl_sys_cd(EtlSysCd);
				num = etl_resource.add(db);
				assertThat("测试数据etl_resource初始化", num, is(1));
			}
			// 9.提交事务
			SqlOperator.commitTransaction(db);
		}
		// 10.模拟用户登录
		String responseValue = new HttpClient()
				.buildSession()
				.addData("user_id", UserId)
				.addData("password", "1")
				.post("http://127.0.0.1:8088/A/action/hrds/a/biz/login/login")
				.getBodyString();
		Optional<ActionResult> ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class);
		assertThat("用户登录", ar.get().isSuccess(), is(true));
	}

	@After
	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.测试完成后删除sys_user表测试数据
			SqlOperator.execute(db, "delete from " + Sys_user.TableName + " where user_id=?", UserId);
			// 判断sys_user数据是否被删除
			long num = SqlOperator.queryNumber(db, "select count(1) from " + Sys_user.TableName +
					"  where user_id=?", UserId).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 2.测试完成后删除Etl_sub_sys_list表测试数据
			SqlOperator.execute(db, "delete from " + Etl_sub_sys_list.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_sub_sys数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_sub_sys_list.TableName +
					"  where etl_sys_cd=?", EtlSysCd).orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 3.测试完成后删除etl_sys表测试数据
			SqlOperator.execute(db, "delete from " + Etl_sys.TableName + " where etl_sys_cd=?", EtlSysCd);
			// 判断etl_sys数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_sys.TableName +
					"  where etl_sys_cd=?", EtlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 4.测试完删除department_info表测试数据
			SqlOperator.execute(db, "delete from " + Department_info.TableName + " where dep_id=?",
					DepId);
			// 判断department_info数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Department_info.TableName +
					"  where dep_id=?", DepId).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 5.测试完删除Etl_job_hand表测试数据
			SqlOperator.execute(db, "delete from " + Etl_job_hand.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_job_hand数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_hand.TableName +
					"  where etl_sys_cd=?", EtlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 6.测试完删除Etl_job_cur表测试数据
			SqlOperator.execute(db, "delete from " + Etl_job_cur.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_job_cur数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_cur.TableName +
					"  where etl_sys_cd=?", EtlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 7.测试完删除Etl_job_resource_rela表测试数据
			SqlOperator.execute(db, "delete from " + Etl_job_resource_rela.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_job_cur数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_resource_rela.TableName +
					"  where etl_sys_cd=?", EtlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 8.测试完删除Etl_job_resource_rela表测试数据
			SqlOperator.execute(db, "delete from " + Etl_job_def.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_job_cur数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_def.TableName +
					"  where etl_sys_cd=?", EtlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 9.测试完删除Etl_job_resource_rela表测试数据
			SqlOperator.execute(db, "delete from " + Etl_dependency.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_job_cur数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_dependency.TableName +
					"  where etl_sys_cd=?", EtlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 10.测试完删除etl_job_disp_his表测试数据
			SqlOperator.execute(db, "delete from " + Etl_job_disp_his.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断etl_job_disp_his数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_disp_his.TableName +
					"  where etl_sys_cd=?", EtlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 11.测试完删除Etl_resource表测试数据
			SqlOperator.execute(db, "delete from " + Etl_resource.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_resource数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_resource.TableName +
					"  where etl_sys_cd=?", EtlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 12.提交事务
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
		assertThat(systemOperationStatus.get("etl_sys_cd"), is(EtlSysCd));
		assertThat(systemOperationStatus.get("waiting").toString(), is(String.valueOf(1)));
		assertThat(systemOperationStatus.get("pending").toString(), is(String.valueOf(1)));
		assertThat(systemOperationStatus.get("runing").toString(), is(String.valueOf(1)));
		assertThat(systemOperationStatus.get("error").toString(), is(String.valueOf(1)));
		assertThat(systemOperationStatus.get("done").toString(), is(String.valueOf(1)));
		assertThat(systemOperationStatus.get("suspension").toString(), is(String.valueOf(2)));
		assertThat(systemOperationStatus.get("sys_name"), is("zyjkcs(作业调度监控测试)"));
		assertThat(systemOperationStatus.get("curr_bath_date"), is(DateUtil.parseStr2DateWith8Char(
				DateUtil.getSysDate()).toString()));
		List<Map<String, Object>> currBatchInfoList = (List<Map<String, Object>>)
				systemOperationStatus.get("systemOperationStatus");
		for (Map<String, Object> currBatchInfo : currBatchInfoList) {
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
				.addData("etl_sys_cd", "jkdqplcs")
				.post(getActionUrl("monitorCurrentBatchInfo"))
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
				.addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
				.post(getActionUrl("searchMonitorJobStateBySubCd"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		List<Map<String, Object>> etlJobCurrList = (List<Map<String, Object>>) ar.getData();
		for (Map<String, Object> etlJobCurrInfo : etlJobCurrList) {
			String etl_job = etlJobCurrInfo.get("etl_job").toString();
			assertThat(etlJobCurrInfo.get("etl_sys_cd"), is(EtlSysCd));
			assertThat(etlJobCurrInfo.get("curr_bath_date"), is(DateUtil.parseStr2DateWith8Char(
					DateUtil.getSysDate()).toString()));
			if (etl_job.equals("监控作业测试1")) {
				assertThat(etlJobCurrInfo.get("job_disp_status"), is(Job_Status.ERROR.getCode()));
				assertThat(etlJobCurrInfo.get("sub_sys_cd"), is(SubSysCd));
				assertThat(etlJobCurrInfo.get("curr_st_time"), is("2019-12-17 11:42:37"));
				assertThat(etlJobCurrInfo.get("curr_end_time"), is("2019-12-17 11:43:37"));
			} else if (etl_job.equals("监控作业测试2")) {
				assertThat(etlJobCurrInfo.get("sub_sys_cd"), is(SubSysCd));
				assertThat(etlJobCurrInfo.get("job_disp_status"), is(Job_Status.DONE.getCode()));
				assertThat(etlJobCurrInfo.get("curr_st_time"), is("2019-12-17 14:42:37"));
				assertThat(etlJobCurrInfo.get("curr_end_time"), is("2019-12-17 14:43:37"));
			} else if (etl_job.equals("监控作业测试3")) {
				assertThat(etlJobCurrInfo.get("sub_sys_cd"), is(SubSysCd));
				assertThat(etlJobCurrInfo.get("job_disp_status"), is(Job_Status.RUNNING.getCode()));
				assertThat(etlJobCurrInfo.get("curr_st_time"), is("2019-12-17 16:42:37"));
				assertThat(etlJobCurrInfo.get("curr_end_time"), is("2019-12-17 16:43:37"));
			} else if (etl_job.equals("监控作业测试4")) {
				assertThat(etlJobCurrInfo.get("sub_sys_cd"), is(SubSysCd));
				assertThat(etlJobCurrInfo.get("job_disp_status"), is(Job_Status.WAITING.getCode()));
				assertThat(etlJobCurrInfo.get("curr_st_time"), is("2019-12-17 17:42:37"));
				assertThat(etlJobCurrInfo.get("curr_end_time"), is("2019-12-17 17:43:37"));
			} else if (etl_job.equals("监控作业测试5")) {
				assertThat(etlJobCurrInfo.get("sub_sys_cd"), is(SubSysCd));
				assertThat(etlJobCurrInfo.get("job_disp_status"), is(Job_Status.PENDING.getCode()));
				assertThat(etlJobCurrInfo.get("curr_st_time"), is("2019-12-17 19:42:37"));
				assertThat(etlJobCurrInfo.get("curr_end_time"), is("2019-12-17 19:43:37"));
			} else if (etl_job.equals("监控作业测试6")) {
				assertThat(etlJobCurrInfo.get("sub_sys_cd"), is(SubSysCd));
				assertThat(etlJobCurrInfo.get("job_disp_status"), is(Job_Status.STOP.getCode()));
				assertThat(etlJobCurrInfo.get("curr_st_time"), is("2019-12-17 21:42:37"));
				assertThat(etlJobCurrInfo.get("curr_end_time"), is("2019-12-17 21:43:37"));
			}
		}
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "jkdqplcs")
				.addData("sub_sys_cd", SubSysCd)
				.addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
				.post(getActionUrl("searchMonitorJobStateBySubCd"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", "jkdqrwzycs")
				.addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
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
		for (Map<String, Object> jobDispHisInfo : etlJobHisList) {
			String sub_sys_cd = jobDispHisInfo.get("sub_sys_cd").toString();
			assertThat(jobDispHisInfo.get("curr_bath_date"), is(DateUtil.parseStr2DateWith8Char(
					DateUtil.getSysDate()).toString()));
			if (sub_sys_cd.equals(SubSysCd)) {
				assertThat(jobDispHisInfo.get("curr_end_time"), is("2019-12-17 21:43:37"));
				assertThat(jobDispHisInfo.get("curr_st_time"), is("2019-12-17 11:42:37"));
				assertThat(jobDispHisInfo.get("desc_sys"), is("zyjkrwcs(监控任务)"));
			} else if (sub_sys_cd.equals(SubSysCd2)) {
				assertThat(jobDispHisInfo.get("curr_end_time"), is("2019-12-17 23:43:37"));
				assertThat(jobDispHisInfo.get("curr_st_time"), is("2019-12-17 23:42:37"));
				assertThat(jobDispHisInfo.get("desc_sys"), is("zyjkrwcs2(监控任务2)"));
			}
		}
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
				.addData("etl_job", "监控作业测试1")
				.post(getActionUrl("monitorCurrJobInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> currJobInfo = ar.getDataForMap();
		assertThat(currJobInfo.get("etl_sys_cd"), is(EtlSysCd));
		assertThat(currJobInfo.get("com_exe_num").toString(), is(String.valueOf(0)));
		assertThat(currJobInfo.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
		assertThat(currJobInfo.get("disp_offset").toString(), is(String.valueOf(0)));
		assertThat(currJobInfo.get("exe_num").toString(), is(String.valueOf(1)));
		assertThat(currJobInfo.get("pro_para"), is("1"));
		assertThat(currJobInfo.get("overtime_val").toString(), is(String.valueOf(0)));
		assertThat(currJobInfo.get("job_disp_status"), is(Job_Status.ERROR.getCode()));
		assertThat(currJobInfo.get("job_priority").toString(), is(String.valueOf(0)));
		assertThat(currJobInfo.get("pro_dic"), is("/home/hyshf/dhw"));
		assertThat(currJobInfo.get("overlength_val").toString(), is(String.valueOf(0)));
		assertThat(currJobInfo.get("pro_type"), is(Pro_Type.SHELL.getCode()));
		assertThat(currJobInfo.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
		assertThat(currJobInfo.get("exe_frequency").toString(), is(String.valueOf(1)));
		assertThat(currJobInfo.get("job_priority_curr").toString(), is(String.valueOf(0)));
		assertThat(currJobInfo.get("job_return_val").toString(), is(String.valueOf(0)));
		assertThat(currJobInfo.get("sub_sys_cd"), is("zyjkrwcs"));
		assertThat(currJobInfo.get("etl_job_desc"), is("监控测试作业定义1"));
		assertThat(currJobInfo.get("disp_freq"), is(Dispatch_Frequency.PinLv.getCode()));
		assertThat(currJobInfo.get("curr_bath_date"), is(DateUtil.parseStr2DateWith8Char(
				DateUtil.getSysDate()).toString()));
		assertThat(currJobInfo.get("etl_job"), is("监控作业测试1"));
		assertThat(currJobInfo.get("pro_name"), is("zy.shell"));
		Map<String, Object> resourceRelation = (Map<String, Object>) currJobInfo.get("resourceRelation");
		assertThat(resourceRelation.get("resource_type").toString(), is("resource"));
		assertThat(resourceRelation.get("resource_req").toString(), is(String.valueOf(1)));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "jkzycs")
				.addData("etl_job", "监控作业测试1")
				.post(getActionUrl("monitorCurrJobInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，etl_job不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", "监控作业测试")
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
				.addData("etl_job", "监控作业测试1")
				.addData("start_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
				.post(getActionUrl("monitorHistoryJobInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> historyJob = ar.getDataForMap();
		assertThat(historyJob.get("job_disp_status"), is(""));
		assertThat(historyJob.get("curr_bath_date"),
				is(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString()));
		assertThat(historyJob.get("curr_end_time"), is("2019-12-17 11:43:37"));
		assertThat(historyJob.get("sub_sys_cd"), is("监控任务(zyjkrwcs)"));
		assertThat(historyJob.get("curr_st_time"), is("2019-12-17 11:42:37"));
		assertThat(historyJob.get("etl_job"), is("监控作业测试1"));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "jklszycs")
				.addData("etl_job", "监控作业测试1")
				.addData("start_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
				.post(getActionUrl("monitorHistoryJobInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，etl_job不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", "监控作业测试")
				.addData("start_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
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
				.addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
						.toString())
				.post(getActionUrl("searchMonitorHisBatchJobBySubCd"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		List<Map<String, Object>> historyBatchJobList = (List<Map<String, Object>>) ar.getData();
		for (Map<String, Object> historyBatchJob : historyBatchJobList) {
			assertThat(historyBatchJob.get("etl_sys_cd"), is(EtlSysCd));
			assertThat(historyBatchJob.get("sub_sys_cd"), is(SubSysCd));
			assertThat(historyBatchJob.get("curr_bath_date"), is(DateUtil.parseStr2DateWith8Char(
					DateUtil.getSysDate()).toString()));
			if (historyBatchJob.get("etl_job").toString().equals("监控作业测试1")) {
				assertThat(historyBatchJob.get("job_disp_status"), is(Job_Status.ERROR.getCode()));
				assertThat(historyBatchJob.get("etl_job_desc"), is("监控测试作业定义1"));
				assertThat(historyBatchJob.get("curr_end_time"), is("2019-12-17 11:43:37"));
				assertThat(historyBatchJob.get("curr_st_time"), is("2019-12-17 11:42:37"));
			} else if (historyBatchJob.get("etl_job").toString().equals("监控作业测试2")) {
				assertThat(historyBatchJob.get("job_disp_status"), is(Job_Status.DONE.getCode()));
				assertThat(historyBatchJob.get("etl_job_desc"), is("监控测试作业定义2"));
				assertThat(historyBatchJob.get("curr_end_time"), is("2019-12-17 14:43:37"));
				assertThat(historyBatchJob.get("curr_st_time"), is("2019-12-17 14:42:37"));
			} else if (historyBatchJob.get("etl_job").toString().equals("监控作业测试3")) {
				assertThat(historyBatchJob.get("job_disp_status"), is(Job_Status.RUNNING.getCode()));
				assertThat(historyBatchJob.get("etl_job_desc"), is("监控测试作业定义3"));
				assertThat(historyBatchJob.get("curr_end_time"), is("2019-12-17 16:43:37"));
				assertThat(historyBatchJob.get("curr_st_time"), is("2019-12-17 16:42:37"));
			} else if (historyBatchJob.get("etl_job").toString().equals("监控作业测试4")) {
				assertThat(historyBatchJob.get("job_disp_status"), is(Job_Status.WAITING.getCode()));
				assertThat(historyBatchJob.get("etl_job_desc"), is("监控测试作业定义4"));
				assertThat(historyBatchJob.get("curr_end_time"), is("2019-12-17 17:43:37"));
				assertThat(historyBatchJob.get("curr_st_time"), is("2019-12-17 17:42:37"));
			} else if (historyBatchJob.get("etl_job").toString().equals("监控作业测试5")) {
				assertThat(historyBatchJob.get("job_disp_status"), is(Job_Status.PENDING.getCode()));
				assertThat(historyBatchJob.get("etl_job_desc"), is("监控测试作业定义5"));
				assertThat(historyBatchJob.get("curr_end_time"), is("2019-12-17 19:43:37"));
				assertThat(historyBatchJob.get("curr_st_time"), is("2019-12-17 19:42:37"));
			} else if (historyBatchJob.get("etl_job").toString().equals("监控作业测试6")) {
				assertThat(historyBatchJob.get("job_disp_status"), is(Job_Status.STOP.getCode()));
				assertThat(historyBatchJob.get("etl_job_desc"), is("监控测试作业定义6"));
				assertThat(historyBatchJob.get("curr_end_time"), is("2019-12-17 21:43:37"));
				assertThat(historyBatchJob.get("curr_st_time"), is("2019-12-17 21:42:37"));
			}
		}
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "jklsplzycs")
				.addData("sub_sys_cd", SubSysCd)
				.addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
						.toString())
				.post(getActionUrl("searchMonitorHisBatchJobBySubCd"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，sub_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", "jklsplzycs")
				.addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
						.toString())
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
				.addData("etl_job", "监控作业测试3")
				.post(getActionUrl("monitorJobDependencyInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> jobDependencyInfo = ar.getDataForMap();
		assertThat(jobDependencyInfo.get("id"), is("0"));
		assertThat(jobDependencyInfo.get("topic"), is("监控作业测试3"));
		assertThat(jobDependencyInfo.get("aid"), is("999"));
		List<Map<String, Object>> downJobInfoList = (List<Map<String, Object>>) jobDependencyInfo.get("children");
		for (Map<String, Object> jobInfo : downJobInfoList) {
			String topic = jobInfo.get("topic").toString();
			if (topic.equals("监控作业测试4")) {
				assertThat(jobInfo.get("id"), is("监控作业测试4"));
				assertThat(jobInfo.get("etl_job"), is("监控作业测试3"));
				assertThat(jobInfo.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(jobInfo.get("direction"), is("left"));
				assertThat(jobInfo.get("pre_etl_job"), is("监控作业测试4"));
			} else if (topic.equals("监控作业测试5")) {
				assertThat(jobInfo.get("id"), is("监控作业测试5"));
				assertThat(jobInfo.get("etl_job"), is("监控作业测试5"));
				assertThat(jobInfo.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(jobInfo.get("direction"), is("right"));
				assertThat(jobInfo.get("pre_etl_job"), is("监控作业测试3"));
			} else if (topic.equals("监控作业测试2")) {
				assertThat(jobInfo.get("id"), is("监控作业测试2"));
				assertThat(jobInfo.get("etl_job"), is("监控作业测试3"));
				assertThat(jobInfo.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(jobInfo.get("direction"), is("left"));
				assertThat(jobInfo.get("pre_etl_job"), is("监控作业测试2"));
			}
		}
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "jkylcs")
				.addData("etl_job", "监控作业测试3")
				.post(getActionUrl("monitorJobDependencyInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，etl_job不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", "监控作业测试")
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
			if (jobRunInfo.get("etl_job").toString().equals("监控作业测试3(监控测试作业定义3)")) {
				assertThat(jobRunInfo.get("job_disp_status"), is("R"));
				assertThat(jobRunInfo.get("sub_sys_cd"), is("zyjkrwcs(监控任务)"));
				assertThat(jobRunInfo.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(jobRunInfo.get("resource_req").toString(), is(String.valueOf(1)));
				assertThat(jobRunInfo.get("resource_type"), is("resource3"));
				assertThat(jobRunInfo.get("curr_st_time"), is("2019-12-17 16:42:37"));
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
		String s = null;
		boolean blank = StringUtil.isBlank(null);
		boolean notBlank = StringUtil.isNotBlank(s);
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.post(getActionUrl("monitorAllProjectChartsData"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
	}

}
