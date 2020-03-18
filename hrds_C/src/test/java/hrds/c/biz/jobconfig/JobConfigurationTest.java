package hrds.c.biz.jobconfig;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.netclient.http.SubmitMediaType;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "作业调度配置管理测试类", author = "dhw", createdate = "2019/11/8 9:18")
public class JobConfigurationTest extends WebBaseTestCase {

	// 作业系统参数变量名称前缀
	private static final String PREFIX = "!";
	// 初始化工程编号
	private static final String EtlSysCd = "zypzglcs";
	// 初始化任务编号
	private static final String SubSysCd = "zypzrwcs";
	private static final String SubSysCd2 = "zypzrwcs2";
	private static final String SubSysCd3 = "myrwcs";
	private static final String SubSysCd4 = "myrwcs2";
	private static final String SubSysCd5 = "myrwcs3";
	// 初始化模板ID
	private static final long EtlTempId = 20000L;
	private static final long EtlTempId2 = 20001L;
	// 初始化模板ID参数
	private static final long EtlTempParaId = 30000L;
	// 初始化登录用户ID
	private static final long UserId = 6666L;
	// 初始化创建用户ID
	private static final long CreateId = 1000L;
	// 测试部门ID dep_id,测试作业调度部门
	private static final long DepId = 1000011L;
	// 初始化系统参数编号
	private static final String ParaCd = PREFIX + "startDate";
	private static final String ParaCd2 = PREFIX + "endDate";
	private static final String ParaCd3 = PREFIX + "delParaCd1";
	private static final String ParaCd4 = PREFIX + "delParaCd2";
	private static final String ParaCd5 = PREFIX + "delParaCd3";
	// 初始化测试系统时间
	private static final String SysDate = DateUtil.getSysDate();

	@Method(desc = "初始化测试用例数据", logicStep = "1.构造sys_user表测试数据" +
			"2.构造department_info部门表测试数据" +
			"3.构造etl_sys表测试数据" +
			"4.构造etl_sub_sys_list表测试数据" +
			"5.构造etl_job_def表测试数据" +
			"6.构造etl_resource表测试数据" +
			"7.构造Etl_job_resource_rela表测试数据" +
			"8.构造etl_job_temp表测试数据" +
			"9.构造etl_job_temp_para表测试数据" +
			"10.构造etl_para表测试数据" +
			"11.构造Etl_dependency表测试数据" +
			"12.提交事务" +
			"13.模拟用户登录" +
			"测试数据：" +
			"1.sys_user表，有1条数据，user_id为UserId" +
			"2.department_info表，有1条数据，dep_id为DepId" +
			"3.etl_sys表，有1条数据，etl_sys_cd为EtlSysCd" +
			"4.etl_sub_sys_list，有1条数据，sub_sys_cd为SubSysCd" +
			"5.etl_job_def表，有6条数据，etl_sys_cd为EtlSysCd" +
			"6.etl_resource表，有1条数据，etl_sys_cd为EtlSysCd" +
			"7.Etl_job_resource_rela表，有1条数据，etl_sys_cd为EtlSysCd" +
			"8.etl_job_temp表，有1条数据，etl_temp_id为EtlTempId" +
			"9.etl_job_temp_para表，有1条数据，etl_temp_para_id为EtlTempParaId" +
			"10.etl_para表，有1条数据，etl_sys_cd为EtlSysCd" +
			"11.Etl_dependency表，有1条数据，etl_sys_cd为EtlSysCd")
	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.构造sys_user表测试数据
			Sys_user sysUser = new Sys_user();
			sysUser.setUser_id(UserId);
			sysUser.setCreate_id(CreateId);
			sysUser.setDep_id(DepId);
			sysUser.setCreate_date(DateUtil.getSysDate());
			sysUser.setCreate_time(DateUtil.getSysTime());
			sysUser.setRole_id("1001");
			sysUser.setUser_name("作业配置功能测试");
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
			etl_sys.setEtl_sys_name("dhwcs");
			etl_sys.setUser_id(UserId);
			num = etl_sys.add(db);
			assertThat("测试数据etl_sys初始化", num, is(1));
			// 4.构造etl_sub_sys_list表测试数据
			Etl_sub_sys_list etl_sub_sys_list = new Etl_sub_sys_list();
			for (int i = 1; i <= 5; i++) {
				if (i == 1) {
					etl_sub_sys_list.setSub_sys_cd(SubSysCd);
				} else if (i == 2) {
					etl_sub_sys_list.setSub_sys_cd(SubSysCd2);
				} else if (i == 3) {
					etl_sub_sys_list.setSub_sys_cd(SubSysCd3);
				} else if (i == 4) {
					etl_sub_sys_list.setSub_sys_cd(SubSysCd4);
				} else {
					etl_sub_sys_list.setSub_sys_cd(SubSysCd5);
				}
				etl_sub_sys_list.setEtl_sys_cd(EtlSysCd);
				etl_sub_sys_list.setSub_sys_desc("任务测试" + i);
				etl_sub_sys_list.setComments("测试" + i);
				num = etl_sub_sys_list.add(db);
				assertThat("测试数据data_source初始化", num, is(1));
			}
			// 5.构造etl_job_def表测试数据
			for (int i = 0; i < 11; i++) {
				Etl_job_def etl_job_def = new Etl_job_def();
				etl_job_def.setEtl_sys_cd(EtlSysCd);
				etl_job_def.setEtl_job("测试作业" + i);
				etl_job_def.setPro_dic("/home/hyshf/dhw");
				etl_job_def.setEtl_job_desc("测试作业定义" + i);
				etl_job_def.setPro_para("1");
				etl_job_def.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
				etl_job_def.setToday_disp(Today_Dispatch_Flag.YES.getCode());
				switch (i) {
					case 0:
						etl_job_def.setPro_name("zy.shell");
						etl_job_def.setPro_type(Pro_Type.SHELL.getCode());
						etl_job_def.setSub_sys_cd(SubSysCd);
						etl_job_def.setDisp_freq(Dispatch_Frequency.PinLv.getCode());
						etl_job_def.setDisp_type(Dispatch_Frequency.PinLv.getCode());
						etl_job_def.setExe_frequency(1);
						etl_job_def.setExe_num(1);
						etl_job_def.setStar_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
								+ " " + DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
						etl_job_def.setEnd_time("2020-12-31 10:30:30");
						etl_job_def.setJob_disp_status(Job_Status.RUNNING.getCode());
						break;
					case 1:
						etl_job_def.setPro_name("zy.class");
						etl_job_def.setPro_type(Pro_Type.JAVA.getCode());
						etl_job_def.setSub_sys_cd(SubSysCd);
						etl_job_def.setDisp_type(Dispatch_Type.TPLUS1.getCode());
						etl_job_def.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
						etl_job_def.setJob_disp_status(Job_Status.DONE.getCode());
						break;
					case 2:
						etl_job_def.setPro_name("zy");
						etl_job_def.setPro_type(Pro_Type.WF.getCode());
						etl_job_def.setSub_sys_cd(SubSysCd);
						etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_def.setDisp_freq(Dispatch_Frequency.WEEKLY.getCode());
						etl_job_def.setJob_disp_status(Job_Status.STOP.getCode());
						break;
					case 3:
						etl_job_def.setPro_name("zy");
						etl_job_def.setPro_type(Pro_Type.Yarn.getCode());
						etl_job_def.setSub_sys_cd(SubSysCd2);
						etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_def.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
						etl_job_def.setJob_disp_status(Job_Status.ERROR.getCode());
						break;
					case 4:
						etl_job_def.setPro_name("zy");
						etl_job_def.setPro_type(Pro_Type.Thrift.getCode());
						etl_job_def.setSub_sys_cd(SubSysCd2);
						etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_def.setDisp_freq(Dispatch_Frequency.TENDAYS.getCode());
						etl_job_def.setJob_disp_status(Job_Status.RUNNING.getCode());
						break;
					case 5:
						etl_job_def.setPro_name("zy.bat");
						etl_job_def.setPro_type(Pro_Type.BAT.getCode());
						etl_job_def.setSub_sys_cd(SubSysCd2);
						etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_def.setDisp_freq(Dispatch_Frequency.YEARLY.getCode());
						etl_job_def.setJob_disp_status(Job_Status.WAITING.getCode());
						break;
					case 6:
						etl_job_def.setPro_name("zycs.shell");
						etl_job_def.setPro_type(Pro_Type.SHELL.getCode());
						etl_job_def.setSub_sys_cd(SubSysCd2);
						etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_def.setDisp_freq(Dispatch_Frequency.YEARLY.getCode());
						etl_job_def.setJob_disp_status(Job_Status.PENDING.getCode());
						break;
					case 7:
						etl_job_def.setPro_name("zycs.bat");
						etl_job_def.setPro_type(Pro_Type.BAT.getCode());
						etl_job_def.setSub_sys_cd(SubSysCd2);
						etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_def.setDisp_freq(Dispatch_Frequency.YEARLY.getCode());
						etl_job_def.setJob_disp_status(Job_Status.DONE.getCode());
						break;
					case 8:
						etl_job_def.setPro_name("zy.perl");
						etl_job_def.setPro_type(Pro_Type.PERL.getCode());
						etl_job_def.setSub_sys_cd(SubSysCd2);
						etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_def.setDisp_freq(Dispatch_Frequency.YEARLY.getCode());
						etl_job_def.setJob_disp_status(Job_Status.ERROR.getCode());
						break;
					case 9:
						etl_job_def.setPro_name("zy.py");
						etl_job_def.setPro_type(Pro_Type.PYTHON.getCode());
						etl_job_def.setSub_sys_cd(SubSysCd2);
						etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
						etl_job_def.setDisp_freq(Dispatch_Frequency.YEARLY.getCode());
						etl_job_def.setJob_disp_status(Job_Status.STOP.getCode());
						break;
					case 10:
						etl_job_def.setPro_name("dszy.shell");
						etl_job_def.setPro_type(Pro_Type.SHELL.getCode());
						etl_job_def.setSub_sys_cd(SubSysCd2);
						etl_job_def.setDisp_type(Dispatch_Type.TPLUS1.getCode());
						etl_job_def.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
						etl_job_def.setJob_disp_status(Job_Status.STOP.getCode());
						break;
				}
				num = etl_job_def.add(db);
				assertThat("测试数据etl_job_def初始化", num, is(1));
			}
			// 6.构造etl_resource表测试数据
			Etl_resource etl_resource = new Etl_resource();
			for (int i = 0; i < 3; i++) {
				etl_resource.setEtl_sys_cd(EtlSysCd);
				if (i == 0) {
					etl_resource.setResource_type("resource");
					etl_resource.setResource_max(5);
					etl_resource.setResource_used(1);
				} else if (i == 1) {
					etl_resource.setResource_type("resource2");
					etl_resource.setResource_max(10);
					etl_resource.setResource_used(2);
				} else {
					etl_resource.setResource_type("resource3");
					etl_resource.setResource_max(15);
					etl_resource.setResource_used(3);
				}
				etl_resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				num = etl_resource.add(db);
				assertThat("测试数据etl_resource初始化", num, is(1));
			}
			// 7.构造Etl_job_resource_rela表测试数据
			Etl_job_resource_rela resourceRelation = new Etl_job_resource_rela();
			for (int i = 0; i < 3; i++) {
				resourceRelation.setEtl_sys_cd(EtlSysCd);
				if (i == 0) {
					resourceRelation.setResource_type("resource");
					resourceRelation.setEtl_job("测试作业3");
				} else if (i == 1) {
					resourceRelation.setResource_type("resource2");
					resourceRelation.setEtl_job("测试作业4");
				} else {
					resourceRelation.setResource_type("resource3");
					resourceRelation.setEtl_job("测试作业5");
				}
				resourceRelation.setResource_req(1);
				num = resourceRelation.add(db);
				assertThat("测试数据Etl_job_resource_rela初始化", num, is(1));
			}
			// 8.构造etl_job_temp表测试数据
			Etl_job_temp etl_job_temp = new Etl_job_temp();
			for (int i = 1; i <= 2; i++) {
				if (i == 1) {
					etl_job_temp.setEtl_temp_id(EtlTempId);
					etl_job_temp.setEtl_temp_type("作业模板" + i);
					etl_job_temp.setPro_name("upload.shell");
				} else {
					etl_job_temp.setEtl_temp_id(EtlTempId2);
					etl_job_temp.setEtl_temp_type("作业模板" + i);
					etl_job_temp.setPro_name("download.shell");
				}
				etl_job_temp.setPro_dic("/home/hyshf/zymb");
				num = etl_job_temp.add(db);
				assertThat("测试数据Etl_job_temp初始化", num, is(1));
			}
			// 9.构造etl_job_temp_para表测试数据
			Etl_job_temp_para etl_job_temp_para = new Etl_job_temp_para();
			etl_job_temp_para.setEtl_temp_para_id(EtlTempParaId);
			etl_job_temp_para.setEtl_temp_id(EtlTempId);
			etl_job_temp_para.setEtl_job_para_size("0");
			etl_job_temp_para.setEtl_job_pro_para("是否压缩");
			etl_job_temp_para.setEtl_pro_para_sort(1L);
			etl_job_temp_para.setEtl_para_type("text");
			num = etl_job_temp_para.add(db);
			assertThat("测试数据etl_job_temp_para初始化", num, is(1));
			// 10.构造etl_para表测试数据
			Etl_para etl_para = new Etl_para();
			for (int i = 0; i < 5; i++) {
				if (i == 0) {
					etl_para.setPara_cd(ParaCd);
					etl_para.setPara_val(SysDate);
				} else if (i == 1) {
					etl_para.setPara_cd(ParaCd2);
					etl_para.setPara_val("99991231");
				} else if (i == 2) {
					etl_para.setPara_cd(ParaCd3);
					etl_para.setPara_val(IsFlag.Fou.getCode());
				} else if (i == 3) {
					etl_para.setPara_cd(ParaCd4);
					etl_para.setPara_val(IsFlag.Shi.getCode());
				} else {
					etl_para.setPara_cd(ParaCd5);
					etl_para.setPara_val(IsFlag.Shi.getCode());
				}
				etl_para.setEtl_sys_cd(EtlSysCd);
				etl_para.setPara_type(ParamType.CanShu.getCode());
				num = etl_para.add(db);
				assertThat("测试数据etl_job_temp_para初始化", num, is(1));
			}
			// 11.构造Etl_dependency表测试数据
			Etl_dependency etlDependency = new Etl_dependency();
			for (int i = 0; i < 4; i++) {
				etlDependency.setEtl_sys_cd(EtlSysCd);
				etlDependency.setPre_etl_sys_cd(EtlSysCd);
				if (i == 0) {
					etlDependency.setEtl_job("测试作业6");
					etlDependency.setPre_etl_job("测试作业10");
				} else if (i == 1) {
					etlDependency.setEtl_job("测试作业5");
					etlDependency.setPre_etl_job("测试作业7");
				} else if (i == 2) {
					etlDependency.setEtl_job("测试作业5");
					etlDependency.setPre_etl_job("测试作业8");
				} else if (i == 3) {
					etlDependency.setEtl_job("测试作业8");
					etlDependency.setPre_etl_job("测试作业1");
				}
				etlDependency.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etlDependency.setStatus(Status.TRUE.getCode());
				num = etlDependency.add(db);
				assertThat("测试数据etl_job_temp_para初始化", num, is(1));
			}
			// 12.提交事务
			SqlOperator.commitTransaction(db);
		}
		// 13.模拟用户登录
		String responseValue = new HttpClient()
				.buildSession()
				.addData("user_id", UserId)
				.addData("password", "1")
				.post("http://127.0.0.1:8088/A/action/hrds/a/biz/login/login")
				.getBodyString();
		Optional<ActionResult> ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class);
		assertThat("用户登录", ar.get().isSuccess(), is(true));
	}

	@Method(desc = "测试完删除测试数据", logicStep = "1.测试完成后删除sys_user表测试数据" +
			"2.测试完成后删除Etl_sub_sys表测试数据" +
			"3.测试完成后删除etl_sys表测试数据" +
			"4.测试完成后删除Etl_job_temp表测试数据" +
			"5.测试完成后删除Etl_job_temp_para表测试数据" +
			"6.测试完成后删除etl_job_def表测试数据" +
			"7.测试完成后删除Etl_resource表测试数据" +
			"8.测试完成后删除Etl_job_resource_rela表测试数据" +
			"9.测试完成后删除Etl_para表测试数据" +
			"10.完成后删除Etl_dependency表测试数据" +
			"11.测试完删除department_info表测试数据" +
			"12.单独删除新增数据，因为新增数据主键是自动生成的，所以要通过其他方式删除" +
			"13.提交事务")
	@After
	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.测试完成后删除sys_user表测试数据
			SqlOperator.execute(db, "delete from " + Sys_user.TableName + " where user_id=?", UserId);
			// 判断sys_user数据是否被删除
			long num = SqlOperator.queryNumber(db, "select count(1) from " + Sys_user.TableName +
					"  where user_id=?", UserId).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 2.测试完成后删除Etl_sub_sys表测试数据
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
			// 4.测试完成后删除Etl_job_temp表测试数据
			SqlOperator.execute(db, "delete from " + Etl_job_temp.TableName + " where etl_temp_id=?",
					EtlTempId);
			// 判断Etl_job_temp数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_temp.TableName +
					"  where etl_temp_id=?", EtlTempId).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Etl_job_temp.TableName + " where etl_temp_id=?",
					EtlTempId2);
			// 判断Etl_job_temp数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_temp.TableName +
					"  where etl_temp_id=?", EtlTempId2).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 5.测试完成后删除Etl_job_temp_para表测试数据
			SqlOperator.execute(db, "delete from " + Etl_job_temp_para.TableName + " where " +
					"etl_temp_para_id=?", EtlTempParaId);
			// 判断Etl_job_temp_para数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_temp_para.TableName +
					"  where etl_temp_para_id=?", EtlTempParaId).orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 6.测试完成后删除etl_job_def表测试数据
			SqlOperator.execute(db, "delete from " + Etl_job_def.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断etl_job_def数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_def.TableName +
					"  where etl_sys_cd=?", EtlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 7.测试完成后删除Etl_resource表测试数据
			SqlOperator.execute(db, "delete from " + Etl_resource.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_resource数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_resource.TableName +
					"  where etl_sys_cd=?", EtlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Etl_resource.TableName + " where resource_type=?",
					"resource");
			// 判断Etl_resource数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_resource.TableName +
					"  where resource_type=?", "resource").orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 8.测试完成后删除Etl_job_resource_rela表测试数据
			SqlOperator.execute(db, "delete from " + Etl_job_resource_rela.TableName +
					" where etl_sys_cd=?", EtlSysCd);
			// 判断Etl_job_resource_rela数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_resource_rela.TableName +
					"  where etl_sys_cd=?", EtlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 9.测试完成后删除Etl_para表测试数据
			SqlOperator.execute(db, "delete from " + Etl_para.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_para数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_para.TableName +
					"  where etl_sys_cd=?", EtlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 10.测试完成后删除Etl_dependency表测试数据
			SqlOperator.execute(db, "delete from " + Etl_dependency.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 判断Etl_dependency数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_dependency.TableName +
					"  where etl_sys_cd=?", EtlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 11.测试完删除department_info表测试数据
			SqlOperator.execute(db, "delete from " + Department_info.TableName + " where dep_id=?",
					DepId);
			// 判断department_info数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Department_info.TableName +
					"  where dep_id=?", DepId).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 13.提交事务
			SqlOperator.commitTransaction(db);
		}
	}

	@Method(desc = "分页查询作业调度某工程任务信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.正确的数据访问2，sub_sys_cd为空" +
					"3.错误的数据访问1，etl_sys_cd不合法，currPage,pageSize（为空有默认值），" +
					"sub_sys_cd可为空所以错误数据只有一种情况")
	@Test
	public void searchEtlSubSysByPage() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", "zy")
				.addData("currPage", 1)
				.addData("pageSize", 2)
				.post(getActionUrl("searchEtlSubSysByPage"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<String, Object> dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("totalSize"), is(2));
		List<Map<String, Object>> etlSubSysList = (List<Map<String, Object>>) dataForMap.get("etlSubSysList");
		for (Map<String, Object> etlSubSys : etlSubSysList) {
			if (etlSubSys.get("sub_sys_cd").equals(SubSysCd)) {
				assertThat(etlSubSys.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(etlSubSys.get("comments"), is("测试1"));
				assertThat(etlSubSys.get("sub_sys_desc"), is("任务测试1"));
			} else {
				assertThat(etlSubSys.get("sub_sys_cd"), is(SubSysCd2));
				assertThat(etlSubSys.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(etlSubSys.get("comments"), is("测试2"));
				assertThat(etlSubSys.get("sub_sys_desc"), is("任务测试2"));
			}
		}
		// 2.正确的数据访问2，sub_sys_cd为空
		bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("currPage", 1)
				.addData("pageSize", 2)
				.post(getActionUrl("searchEtlSubSysByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("totalSize"), is(5));
		etlSubSysList = (List<Map<String, Object>>) dataForMap.get("etlSubSysList");
		for (Map<String, Object> etlSubSys : etlSubSysList) {
			if (etlSubSys.get("sub_sys_cd").equals(SubSysCd)) {
				assertThat(etlSubSys.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(etlSubSys.get("comments"), is("测试1"));
				assertThat(etlSubSys.get("sub_sys_desc"), is("任务测试1"));
			} else if (etlSubSys.get("sub_sys_cd").equals(SubSysCd2)) {
				assertThat(etlSubSys.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(etlSubSys.get("comments"), is("测试2"));
				assertThat(etlSubSys.get("sub_sys_desc"), is("任务测试2"));
			} else if (etlSubSys.get("sub_sys_cd").equals(SubSysCd3)) {
				assertThat(etlSubSys.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(etlSubSys.get("comments"), is("测试3"));
				assertThat(etlSubSys.get("sub_sys_desc"), is("任务测试3"));
			} else if (etlSubSys.get("sub_sys_cd").equals(SubSysCd4)) {
				assertThat(etlSubSys.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(etlSubSys.get("comments"), is("测试4"));
				assertThat(etlSubSys.get("sub_sys_desc"), is("任务测试4"));
			} else {
				assertThat(etlSubSys.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(etlSubSys.get("sub_sys_cd"), is(SubSysCd5));
				assertThat(etlSubSys.get("comments"), is("测试5"));
				assertThat(etlSubSys.get("sub_sys_desc"), is("任务测试5"));
			}
		}
		// 3.错误的数据访问1，etl_sys_cd不合法，currPage,pageSize（为空有默认值），sub_sys_cd可为空所以错误数据只有一种情况
		bodyString = new HttpClient().addData("etl_sys_cd", "cs01")
				.addData("currPage", 1)
				.addData("pageSize", 2)
				.post(getActionUrl("searchEtlSubSysByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "根据工程编号，任务编号查询任务信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，sub_sys_cd不存在")
	@Test
	public void searchEtlSubSysById() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", SubSysCd)
				.post(getActionUrl("searchEtlSubSysById"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<String, Object> dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("sub_sys_cd"), is(SubSysCd));
		assertThat(dataForMap.get("etl_sys_cd"), is(EtlSysCd));
		assertThat(dataForMap.get("comments"), is("测试1"));
		assertThat(dataForMap.get("sub_sys_desc"), is("任务测试1"));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient().addData("etl_sys_cd", "etlSysCd")
				.addData("sub_sys_cd", SubSysCd)
				.post(getActionUrl("searchEtlSubSysById"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，sub_sys_cd不存在
		bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", "subSysCd1")
				.post(getActionUrl("searchEtlSubSysById"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "新增保存任务",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd为空" +
					"3.错误的数据访问2，etl_sys_cd为空格" +
					"4.错误的数据访问3，etl_sys_cd为不存在的数据" +
					"5.错误的数据访问4，sub_sys_cd为空" +
					"6.错误的数据访问5，sub_sys_cd为空格" +
					"7.错误的数据访问6，sub_sys_cd为已存在的数据" +
					"8.错误的数据访问7，sub_sys_desc为空" +
					"9.错误的数据访问8，sub_sys_desc为空格")
	@Test
	public void saveEtlSubSys() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", "addSubSys1")
					.addData("sub_sys_desc", "addSubSys1")
					.addData("comments", "新增任务测试1")
					.post(getActionUrl("saveEtlSubSys"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			Etl_sub_sys_list subSysList = SqlOperator.queryOneObject(db, Etl_sub_sys_list.class,
					"select * from " + Etl_sub_sys_list.TableName + " where etl_sys_cd=? and sub_sys_cd=?"
					, EtlSysCd, "addSubSys1").orElseThrow(() ->
					new BusinessException("sql查询错误或映射错误！"));
			assertThat(subSysList.getEtl_sys_cd(), is(EtlSysCd));
			assertThat(subSysList.getSub_sys_cd(), is("addSubSys1"));
			assertThat(subSysList.getSub_sys_desc(), is("addSubSys1"));
			assertThat(subSysList.getComments(), is("新增任务测试1"));
			// 2.错误的数据访问1，etl_sys_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", "")
					.addData("sub_sys_cd", SubSysCd)
					.addData("sub_sys_desc", "addSubSys2")
					.addData("comments", "新增任务测试1")
					.post(getActionUrl("saveEtlSubSys"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，etl_sys_cd为空格
			bodyString = new HttpClient().addData("etl_sys_cd", " ")
					.addData("sub_sys_cd", SubSysCd)
					.addData("sub_sys_desc", "addSubSys3")
					.addData("comments", "新增任务测试1")
					.post(getActionUrl("saveEtlSubSys"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，etl_sys_cd为不存在的数据
			bodyString = new HttpClient().addData("etl_sys_cd", "cwcs1")
					.addData("sub_sys_cd", SubSysCd)
					.addData("sub_sys_desc", "addSubSys4")
					.addData("comments", "新增任务测试1")
					.post(getActionUrl("saveEtlSubSys"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 5.错误的数据访问4，sub_sys_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", "")
					.addData("sub_sys_desc", "addSubSys5")
					.addData("comments", "新增任务测试1")
					.post(getActionUrl("saveEtlSubSys"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 6.错误的数据访问5，sub_sys_cd为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", " ")
					.addData("sub_sys_desc", "addSubSys6")
					.addData("comments", "新增任务测试1")
					.post(getActionUrl("saveEtlSubSys"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 7.错误的数据访问6，sub_sys_cd为已存在的数据
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("sub_sys_desc", "addSubSys7")
					.addData("comments", "新增任务测试1")
					.post(getActionUrl("saveEtlSubSys"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 8.错误的数据访问7，sub_sys_desc为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("sub_sys_desc", "addSubSys8")
					.addData("comments", "新增任务测试1")
					.post(getActionUrl("saveEtlSubSys"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 9.错误的数据访问8，sub_sys_desc为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("sub_sys_desc", " ")
					.addData("comments", "新增任务测试1")
					.post(getActionUrl("saveEtlSubSys"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "更新保存任务",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd为空" +
					"3.错误的数据访问2，etl_sys_cd为空格" +
					"4.错误的数据访问3，etl_sys_cd为不存在的数据" +
					"5.错误的数据访问4，sub_sys_cd为空" +
					"6.错误的数据访问5，sub_sys_cd为空格" +
					"7.错误的数据访问6，sub_sys_cd为不存在的数据" +
					"8.错误的数据访问7，sub_sys_desc为空" +
					"9.错误的数据访问8，sub_sys_desc为空格")
	@Test
	public void updateEtlSubSys() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", SubSysCd)
				.addData("sub_sys_desc", "upSubSys1")
				.addData("comments", "编辑任务测试1")
				.post(getActionUrl("updateEtlSubSys"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Etl_sub_sys_list subSysList = SqlOperator.queryOneObject(db, Etl_sub_sys_list.class,
					"select * from " + Etl_sub_sys_list.TableName + " where etl_sys_cd=? and sub_sys_cd=?"
					, EtlSysCd, SubSysCd).orElseThrow(() ->
					new BusinessException("sql查询错误或映射错误！"));
			assertThat(subSysList.getEtl_sys_cd(), is(EtlSysCd));
			assertThat(subSysList.getSub_sys_cd(), is(SubSysCd));
			assertThat(subSysList.getSub_sys_desc(), is("upSubSys1"));
			assertThat(subSysList.getComments(), is("编辑任务测试1"));
		}
		// 2.错误的数据访问1，etl_sys_cd为空
		bodyString = new HttpClient().addData("etl_sys_cd", "")
				.addData("sub_sys_cd", SubSysCd)
				.addData("sub_sys_desc", "upSubSys2")
				.addData("comments", "编辑任务测试2")
				.post(getActionUrl("updateEtlSubSys"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，etl_sys_cd为空格
		bodyString = new HttpClient().addData("etl_sys_cd", " ")
				.addData("sub_sys_cd", SubSysCd)
				.addData("sub_sys_desc", "upSubSys3")
				.addData("comments", "编辑任务测试3")
				.post(getActionUrl("updateEtlSubSys"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，etl_sys_cd为不存在的数据
		bodyString = new HttpClient().addData("etl_sys_cd", "cwcs1")
				.addData("sub_sys_cd", SubSysCd)
				.addData("sub_sys_desc", "upSubSys4")
				.addData("comments", "编辑任务测试4")
				.post(getActionUrl("saveEtlSubSys"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问4，sub_sys_cd为空
		bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", "")
				.addData("sub_sys_desc", "upSubSys5")
				.addData("comments", "编辑任务测试5")
				.post(getActionUrl("updateEtlSubSys"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 6.错误的数据访问5，sub_sys_cd为空格
		bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", " ")
				.addData("sub_sys_desc", "upSubSys6")
				.addData("comments", "编辑任务测试6")
				.post(getActionUrl("updateEtlSubSys"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 7.错误的数据访问6，sub_sys_cd为不存在的数据
		bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", "uprwcs")
				.addData("sub_sys_desc", "upSubSys7")
				.addData("comments", "编辑任务测试7")
				.post(getActionUrl("updateEtlSubSys"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 8.错误的数据访问7，sub_sys_desc为空
		bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", SubSysCd)
				.addData("sub_sys_desc", "")
				.addData("comments", "编辑任务测试8")
				.post(getActionUrl("updateEtlSubSys"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 9.错误的数据访问8，sub_sys_desc为空格
		bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", SubSysCd)
				.addData("sub_sys_desc", " ")
				.addData("comments", "编辑任务测试9")
				.post(getActionUrl("updateEtlSubSys"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "根据工程编号，任务编号删除任务信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，sub_sys_cd不存在" +
					"4.错误的数据访问3，工程任务下还有作业")
	@Test
	public void deleteEtlSubSys() {
		// 1.正常的数据访问1，数据都正常
		// 删除前查询数据库，确认预期删除的数据存在
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Etl_sub_sys_list.TableName + " where etl_sys_cd=? and sub_sys_cd=?", EtlSysCd, SubSysCd3);
			assertThat("删除操作前，Etl_sub_sys_list表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(1L));
			String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd3)
					.post(getActionUrl("deleteEtlSubSys"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期数据已删除
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Etl_sub_sys_list.TableName + " where etl_sys_cd=? and sub_sys_cd=?", EtlSysCd, SubSysCd3);
			assertThat("删除操作后，确认这条数据已删除", optionalLong.orElse(Long.MIN_VALUE), is(0L));
			// 2.错误的数据访问1，etl_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", "sccs1")
					.addData("sub_sys_cd", SubSysCd)
					.post(getActionUrl("deleteEtlSubSys"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，sub_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", "sccs1")
					.post(getActionUrl("deleteEtlSubSys"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，工程任务下还有作业
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.post(getActionUrl("deleteEtlSubSys"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "批量删除任务信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，sub_sys_cd不存在" +
					"4.错误的数据访问3，sub_sys_cd下有作业(只要有一个下有作业就应该报错）")
	@Test
	public void batchDeleteEtlSubSys() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			// 删除前查询数据库，确认预期删除的数据存在
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
							Etl_sub_sys_list.TableName + " where etl_sys_cd=? and sub_sys_cd in (?,?)",
					EtlSysCd, SubSysCd5, SubSysCd4);
			assertThat("删除操作前，Etl_sub_sys_list表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(2L));
			String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", new String[]{SubSysCd5, SubSysCd4})
					.post(getActionUrl("batchDeleteEtlSubSys"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期数据已删除
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
							Etl_sub_sys_list.TableName + " where etl_sys_cd=? and sub_sys_cd in (?,?)",
					EtlSysCd, SubSysCd5, SubSysCd4);
			assertThat("删除操作后，确认这条数据已删除", optionalLong.
					orElse(Long.MIN_VALUE), is(0L));
			// 2.错误的数据访问1，etl_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", "sccs1")
					.addData("sub_sys_cd", new String[]{SubSysCd3, SubSysCd4})
					.post(getActionUrl("batchDeleteEtlSubSys"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，sub_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", new String[]{"sccs1"})
					.post(getActionUrl("batchDeleteEtlSubSys"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，sub_sys_cd下有作业(只要有一个下有作业就应该报错）
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", new String[]{SubSysCd, SubSysCd3})
					.post(getActionUrl("batchDeleteEtlSubSys"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "查询作业模板信息", logicStep = "1.正常的数据访问1，数据都正常，该方法只有一种情况")
	@Test
	public void searchEtlJobTemplate() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.post(getActionUrl("searchEtlJobTemplate"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		Result etlJobTemplate = ar.getDataForResult();
		for (int i = 0; i < etlJobTemplate.getRowCount(); i++) {
			if (etlJobTemplate.getLong(i, "etl_temp_id") == EtlTempId) {
				assertThat(etlJobTemplate.getString(i, "etl_temp_type"), is("作业模板1"));
				assertThat(etlJobTemplate.getString(i, "pro_dic"), is("/home/hyshf/zymb"));
				assertThat(etlJobTemplate.getString(i, "pro_name"), is("upload.shell"));
			} else {
				assertThat(etlJobTemplate.getString(i, "etl_temp_type"), is("作业模板2"));
				assertThat(etlJobTemplate.getString(i, "pro_dic"), is("/home/hyshf/zymb"));
				assertThat(etlJobTemplate.getString(i, "pro_name"), is("download.shell"));
			}
		}
	}

	@Method(desc = "关联查询作业模板表和作业模板参数表获取作业模板信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_temp_id不存在")
	@Test
	public void searchEtlJobTempAndParam() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_temp_id", EtlTempId)
				.post(getActionUrl("searchEtlJobTempAndParam"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		Type type = new TypeReference<List<Map<String, Object>>>() {
		}.getType();
		List<Map<String, Object>> etlJobTempAndPara = JsonUtil.toObject(ar.getData().toString(), type);
		for (Map<String, Object> map : etlJobTempAndPara) {
			assertThat(map.get("etl_temp_id").toString(), is(String.valueOf(EtlTempId)));
			assertThat(map.get("etl_temp_para_id").toString(), is(String.valueOf(EtlTempParaId)));
			assertThat(map.get("etl_temp_type"), is("作业模板1"));
			assertThat(map.get("pro_dic"), is("/home/hyshf/zymb"));
			assertThat(map.get("pro_name"), is("upload.shell"));
			assertThat(map.get("etl_para_type").toString(), is("text"));
			assertThat(map.get("etl_job_para_size").toString(), is("0"));
			assertThat(map.get("etl_pro_para_sort").toString(), is(String.valueOf(1)));
			assertThat(map.get("etl_job_pro_para").toString(), is("是否压缩"));
		}
		// 2.错误的数据访问1，etl_temp_id不存在
		bodyString = new HttpClient()
				.addData("etl_temp_id", 1)
				.post(getActionUrl("searchEtlJobTempAndParam"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		etlJobTempAndPara = JsonUtil.toObject(ar.getData().toString(), type);
		assertThat(etlJobTempAndPara.isEmpty(), is(true));
	}

	@Method(desc = "保存作业模板信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd为空" +
					"3.错误的数据访问2，etl_sys_cd为空格" +
					"4.错误的数据访问3，etl_sys_cd不存在" +
					"5.错误的数据访问4，sub_sys_cd为空" +
					"6.错误的数据访问5，sub_sys_cd为空格" +
					"7.错误的数据访问6，sub_sys_cd不存在" +
					"8.错误的数据访问7，etl_job为空" +
					"9.错误的数据访问8，etl_job为空格" +
					"10.错误的数据访问9，etl_job已存在" +
					"11.错误的数据访问10，etl_temp_id为空" +
					"12.错误的数据访问11，etl_temp_id为空格" +
					"13.错误的数据访问12，etl_temp_id不存在" +
					"14.错误的数据访问13，etl_job_temp_para为空15.错误的数据访问" +
					"15，etl_job_temp_para为空格")
	@Test
	public void saveEtlJobTemp() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", SubSysCd)
				.addData("etl_job", "模板作业")
				.addData("etl_temp_id", EtlTempId)
				.addData("etl_job_temp_para", new String[]{"0,1"})
				.post(getActionUrl("saveEtlJobTemp"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Map<String, Object> etlJobDef = SqlOperator.queryOneObject(db, "select * from " +
					Etl_job_def.TableName + " where etl_job=? and etl_sys_cd=?", "模板作业", EtlSysCd);
			assertThat(etlJobDef.get("etl_sys_cd"), is(EtlSysCd));
			assertThat(etlJobDef.get("sub_sys_cd"), is(SubSysCd));
			assertThat(etlJobDef.get("etl_job"), is("模板作业"));
			assertThat(etlJobDef.get("pro_para"), is("0,1"));
			assertThat(etlJobDef.get("pro_type"), is(Pro_Type.SHELL.getCode()));
			assertThat(etlJobDef.get("disp_type"), is(Dispatch_Type.DEPENDENCE.getCode()));
			assertThat(etlJobDef.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
			assertThat(etlJobDef.get("disp_freq"), is(Dispatch_Frequency.DAILY.getCode()));
			assertThat(etlJobDef.get("main_serv_sync"), is(Main_Server_Sync.YES.getCode()));
			assertThat(etlJobDef.get("pro_dic"), is("/home/hyshf/zymb"));
			assertThat(etlJobDef.get("pro_name"), is("upload.shell"));
			assertThat(etlJobDef.get("log_dic"), is("/home/hyshf/zymb"));
		}
		// 2.错误的数据访问1，etl_sys_cd为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "")
				.addData("sub_sys_cd", SubSysCd)
				.addData("etl_job", "模板作业")
				.addData("etl_temp_id", EtlTempId)
				.addData("etl_job_temp_para", new String[]{"0,1"})
				.post(getActionUrl("saveEtlJobTemp"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，etl_sys_cd为空格
		bodyString = new HttpClient()
				.addData("etl_sys_cd", " ")
				.addData("sub_sys_cd", SubSysCd)
				.addData("etl_job", "模板作业")
				.addData("etl_temp_id", EtlTempId)
				.addData("etl_job_temp_para", new String[]{"0,1"})
				.post(getActionUrl("saveEtlJobTemp"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "mbzybccs")
				.addData("sub_sys_cd", SubSysCd)
				.addData("etl_job", "模板作业")
				.addData("etl_temp_id", EtlTempId)
				.addData("etl_job_temp_para", new String[]{"0,1"})
				.post(getActionUrl("saveEtlJobTemp"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问4，sub_sys_cd为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", "")
				.addData("etl_job", "模板作业")
				.addData("etl_temp_id", EtlTempId)
				.addData("etl_job_temp_para", new String[]{"0,1"})
				.post(getActionUrl("saveEtlJobTemp"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 6.错误的数据访问5，sub_sys_cd为空格
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", " ")
				.addData("etl_job", "模板作业")
				.addData("etl_temp_id", EtlTempId)
				.addData("etl_job_temp_para", new String[]{"0,1"})
				.post(getActionUrl("saveEtlJobTemp"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 7.错误的数据访问6，sub_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", "mozycsrw")
				.addData("etl_job", "模板作业")
				.addData("etl_temp_id", EtlTempId)
				.addData("etl_job_temp_para", new String[]{"0,1"})
				.post(getActionUrl("saveEtlJobTemp"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 8.错误的数据访问7，etl_job为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", SubSysCd)
				.addData("etl_job", "")
				.addData("etl_temp_id", EtlTempId)
				.addData("etl_job_temp_para", new String[]{"0,1"})
				.post(getActionUrl("saveEtlJobTemp"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 9.错误的数据访问8，etl_job为空格
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", SubSysCd)
				.addData("etl_job", " ")
				.addData("etl_temp_id", EtlTempId)
				.addData("etl_job_temp_para", new String[]{"0,1"})
				.post(getActionUrl("saveEtlJobTemp"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 10.错误的数据访问9，etl_job已存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", SubSysCd)
				.addData("etl_job", "测试作业1")
				.addData("etl_temp_id", EtlTempId)
				.addData("etl_job_temp_para", new String[]{"0,1"})
				.post(getActionUrl("saveEtlJobTemp"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 11.错误的数据访问10，etl_temp_id为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", SubSysCd)
				.addData("etl_job", "模板作业测试")
				.addData("etl_temp_id", "")
				.addData("etl_job_temp_para", new String[]{"0,1"})
				.post(getActionUrl("saveEtlJobTemp"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 12.错误的数据访问11，etl_temp_id为空格
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", SubSysCd)
				.addData("etl_job", "模板作业测试")
				.addData("etl_temp_id", " ")
				.addData("etl_job_temp_para", new String[]{"0,1"})
				.post(getActionUrl("saveEtlJobTemp"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 13.错误的数据访问12，etl_temp_id不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", SubSysCd)
				.addData("etl_job", "模板作业测试")
				.addData("etl_temp_id", 1)
				.addData("etl_job_temp_para", new String[]{"0,1"})
				.post(getActionUrl("saveEtlJobTemp"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 14.错误的数据访问13，etl_job_temp_para为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", SubSysCd)
				.addData("etl_job", "模板作业测试")
				.addData("etl_temp_id", "")
				.addData("etl_job_temp_para", "")
				.post(getActionUrl("saveEtlJobTemp"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 15.错误的数据访问14，etl_job_temp_para为空格
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", SubSysCd)
				.addData("etl_job", "模板作业测试")
				.addData("etl_temp_id", " ")
				.addData("etl_job_temp_para", " ")
				.post(getActionUrl("saveEtlJobTemp"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "分页查询作业定义信息",
			logicStep = "1.正常的数据访问1，数据都正常,都不为空" +
					"2.正确的数据访问2，etl_job、pro_type、pro_name、sub_sys_cd为空" +
					"3.正确的数据访问3，数据都正常，pro_type不为空" +
					"4.正确的数据访问4，数据都正常，etl_job不为空" +
					"5.正确的数据访问4，数据都正常，pro_name不为空" +
					"6.正确的数据访问4，数据都正常，sub_sys_cd不为空" +
					"7.错误的数据访问1，etl_sys_cd不存在")
	@Test
	public void searchEtlJobDefByPage() {
		// 1.正常的数据访问1，数据都正常,都不为空
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("pro_type", Pro_Type.SHELL.getCode())
				.addData("etl_job", "测试作业0")
				.addData("pro_name", "zy.shell")
				.addData("sub_sys_cd", SubSysCd)
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlJobDefByPage"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> etlJobDef = ar.getDataForMap();
		List<Map<String, Object>> etlJobDefList = (List<Map<String, Object>>) etlJobDef.get("etlJobDefList");
		for (Map<String, Object> map : etlJobDefList) {
			assertThat(map.get("etl_job"), is("测试作业0"));
			assertThat(map.get("pro_type"), is(Pro_Type.SHELL.getCode()));
			assertThat(map.get("pro_name"), is("zy.shell"));
			assertThat(map.get("sub_sys_cd"), is(SubSysCd));
		}
		// 2.正确的数据访问2，etl_job、pro_type、pro_name、sub_sys_cd为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlJobDefByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		etlJobDef = ar.getDataForMap();
		etlJobDefList = (List<Map<String, Object>>) etlJobDef.get("etlJobDefList");
		for (Map<String, Object> map : etlJobDefList) {
			String etl_job = map.get("etl_job").toString();
			if (etl_job.equals("测试作业0")) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("disp_type"), is(Dispatch_Frequency.PinLv.getCode()));
				assertThat(map.get("pro_name"), is("zy.shell"));
				assertThat(map.get("disp_freq"), is(Dispatch_Frequency.PinLv.getCode()));
				assertThat(map.get("etl_job_desc"), is("测试作业定义0"));
				assertThat(map.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
				assertThat(map.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
				assertThat(map.get("etl_sys_name"), is("dhwcs"));
			} else if (etl_job.equals("测试作业1")) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("disp_type"), is(Dispatch_Type.TPLUS1.getCode()));
				assertThat(map.get("pro_name"), is("zy.class"));
				assertThat(map.get("disp_freq"), is(Dispatch_Frequency.MONTHLY.getCode()));
				assertThat(map.get("etl_job_desc"), is("测试作业定义1"));
				assertThat(map.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
				assertThat(map.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
				assertThat(map.get("etl_sys_name"), is("dhwcs"));
			} else if (etl_job.equals("测试作业2")) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("disp_type"), is(Dispatch_Type.DEPENDENCE.getCode()));
				assertThat(map.get("pro_name"), is("zy"));
				assertThat(map.get("disp_freq"), is(Dispatch_Frequency.WEEKLY.getCode()));
				assertThat(map.get("etl_job_desc"), is("测试作业定义2"));
				assertThat(map.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
				assertThat(map.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
				assertThat(map.get("etl_sys_name"), is("dhwcs"));
			} else if (etl_job.equals("测试作业3")) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("disp_type"), is(Dispatch_Type.DEPENDENCE.getCode()));
				assertThat(map.get("pro_name"), is("zy"));
				assertThat(map.get("disp_freq"), is(Dispatch_Frequency.DAILY.getCode()));
				assertThat(map.get("etl_job_desc"), is("测试作业定义3"));
				assertThat(map.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
				assertThat(map.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
				assertThat(map.get("etl_sys_name"), is("dhwcs"));
			} else if (etl_job.equals("测试作业4")) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("disp_type"), is(Dispatch_Type.DEPENDENCE.getCode()));
				assertThat(map.get("pro_name"), is("zy"));
				assertThat(map.get("disp_freq"), is(Dispatch_Frequency.TENDAYS.getCode()));
				assertThat(map.get("etl_job_desc"), is("测试作业定义4"));
				assertThat(map.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
				assertThat(map.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
				assertThat(map.get("etl_sys_name"), is("dhwcs"));
			}
		}
		// 3.正确的数据访问3，数据都正常，pro_type不为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("pro_type", Pro_Type.SHELL.getCode())
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlJobDefByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		etlJobDef = ar.getDataForMap();
		etlJobDefList = (List<Map<String, Object>>) etlJobDef.get("etlJobDefList");
		for (Map<String, Object> map : etlJobDefList) {
			String etl_job = map.get("etl_job").toString();
			if (etl_job.equals("测试作业0")) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("disp_type"), is(Dispatch_Frequency.PinLv.getCode()));
				assertThat(map.get("pro_name"), is("zy.shell"));
				assertThat(map.get("disp_freq"), is(Dispatch_Frequency.PinLv.getCode()));
				assertThat(map.get("etl_job_desc"), is("测试作业定义0"));
				assertThat(map.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
				assertThat(map.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
				assertThat(map.get("etl_sys_name"), is("dhwcs"));
				assertThat(map.get("pro_type"), is(Pro_Type.SHELL.getCode()));
			} else if (etl_job.equals("测试作业1")) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("disp_type"), is(Dispatch_Type.DEPENDENCE.getCode()));
				assertThat(map.get("pro_name"), is("zy.shell"));
				assertThat(map.get("disp_freq"), is(Dispatch_Frequency.YEARLY.getCode()));
				assertThat(map.get("etl_job_desc"), is("测试作业定义6"));
				assertThat(map.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
				assertThat(map.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
				assertThat(map.get("etl_sys_name"), is("dhwcs"));
				assertThat(map.get("pro_type"), is(Pro_Type.SHELL.getCode()));
			}
		}
		// 4.正确的数据访问4，数据都正常，etl_job不为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", "5")
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlJobDefByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		etlJobDef = ar.getDataForMap();
		etlJobDefList = (List<Map<String, Object>>) etlJobDef.get("etlJobDefList");
		for (Map<String, Object> map : etlJobDefList) {
			assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
			assertThat(map.get("etl_job"), is("测试作业5"));
			assertThat(map.get("disp_type"), is(Dispatch_Type.DEPENDENCE.getCode()));
			assertThat(map.get("pro_name"), is("zy.bat"));
			assertThat(map.get("disp_freq"), is(Dispatch_Frequency.YEARLY.getCode()));
			assertThat(map.get("etl_job_desc"), is("测试作业定义5"));
			assertThat(map.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
			assertThat(map.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
			assertThat(map.get("etl_sys_name"), is("dhwcs"));
			assertThat(map.get("pro_type"), is(Pro_Type.BAT.getCode()));
		}
		// 5.正确的数据访问5，数据都正常，pro_name不为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("pro_name", "class")
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlJobDefByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		etlJobDef = ar.getDataForMap();
		etlJobDefList = (List<Map<String, Object>>) etlJobDef.get("etlJobDefList");
		for (Map<String, Object> map : etlJobDefList) {
			assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
			assertThat(map.get("etl_job"), is("测试作业1"));
			assertThat(map.get("disp_type"), is(Dispatch_Type.TPLUS1.getCode()));
			assertThat(map.get("pro_name"), is("zy.class"));
			assertThat(map.get("disp_freq"), is(Dispatch_Frequency.MONTHLY.getCode()));
			assertThat(map.get("etl_job_desc"), is("测试作业定义1"));
			assertThat(map.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
			assertThat(map.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
			assertThat(map.get("etl_sys_name"), is("dhwcs"));
			assertThat(map.get("pro_type"), is(Pro_Type.JAVA.getCode()));
		}
		// 6.正确的数据访问6，数据都正常，sub_sys_cd不为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("sub_sys_cd", SubSysCd2)
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlJobDefByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		etlJobDef = ar.getDataForMap();
		etlJobDefList = (List<Map<String, Object>>) etlJobDef.get("etlJobDefList");
		for (Map<String, Object> map : etlJobDefList) {
			assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
			String etl_job = map.get("etl_job").toString();
			if (etl_job.equals("测试作业3")) {
				assertThat(map.get("disp_type"), is(Dispatch_Type.DEPENDENCE.getCode()));
				assertThat(map.get("pro_name"), is("zy"));
				assertThat(map.get("disp_freq"), is(Dispatch_Frequency.DAILY.getCode()));
				assertThat(map.get("etl_job_desc"), is("测试作业定义3"));
				assertThat(map.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
				assertThat(map.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
				assertThat(map.get("etl_sys_name"), is("dhwcs"));
				assertThat(map.get("pro_type"), is(Pro_Type.Yarn.getCode()));
				assertThat(map.get("sub_sys_cd"), is(SubSysCd2));
			} else if (etl_job.equals("测试作业4")) {
				assertThat(map.get("disp_type"), is(Dispatch_Type.DEPENDENCE.getCode()));
				assertThat(map.get("pro_name"), is("zy"));
				assertThat(map.get("disp_freq"), is(Dispatch_Frequency.TENDAYS.getCode()));
				assertThat(map.get("etl_job_desc"), is("测试作业定义4"));
				assertThat(map.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
				assertThat(map.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
				assertThat(map.get("etl_sys_name"), is("dhwcs"));
				assertThat(map.get("pro_type"), is(Pro_Type.Thrift.getCode()));
				assertThat(map.get("sub_sys_cd"), is(SubSysCd2));
			} else if (etl_job.equals("测试作业5")) {
				assertThat(map.get("disp_type"), is(Dispatch_Type.DEPENDENCE.getCode()));
				assertThat(map.get("pro_name"), is("zy.bat"));
				assertThat(map.get("disp_freq"), is(Dispatch_Frequency.YEARLY.getCode()));
				assertThat(map.get("etl_job_desc"), is("测试作业定义5"));
				assertThat(map.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
				assertThat(map.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
				assertThat(map.get("etl_sys_name"), is("dhwcs"));
				assertThat(map.get("pro_type"), is(Pro_Type.BAT.getCode()));
				assertThat(map.get("sub_sys_cd"), is(SubSysCd2));
			} else if (etl_job.equals("测试作业6")) {
				assertThat(map.get("disp_type"), is(Dispatch_Type.DEPENDENCE.getCode()));
				assertThat(map.get("pro_name"), is("zycs.shell"));
				assertThat(map.get("disp_freq"), is(Dispatch_Frequency.YEARLY.getCode()));
				assertThat(map.get("etl_job_desc"), is("测试作业定义6"));
				assertThat(map.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
				assertThat(map.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
				assertThat(map.get("etl_sys_name"), is("dhwcs"));
				assertThat(map.get("pro_type"), is(Pro_Type.SHELL.getCode()));
				assertThat(map.get("sub_sys_cd"), is(SubSysCd2));
			} else if (etl_job.equals("测试作业7")) {
				assertThat(map.get("disp_type"), is(Dispatch_Type.DEPENDENCE.getCode()));
				assertThat(map.get("pro_name"), is("zycs.bat"));
				assertThat(map.get("disp_freq"), is(Dispatch_Frequency.YEARLY.getCode()));
				assertThat(map.get("etl_job_desc"), is("测试作业定义7"));
				assertThat(map.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
				assertThat(map.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
				assertThat(map.get("etl_sys_name"), is("dhwcs"));
				assertThat(map.get("pro_type"), is(Pro_Type.BAT.getCode()));
				assertThat(map.get("sub_sys_cd"), is(SubSysCd2));
			}
		}
		// 7.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "zycxcs")
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlJobDefByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "查询作业名称信息",
			logicStep = "1.正确的数据访问1，数据都正确" +
					"2.错误的数据访问1，etl_sys_cd不存在")
	@Test
	public void searchEtlJob() {
		// 1.正确的数据访问1，数据都正确
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.post(getActionUrl("searchEtlJob"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		List<String> etlJobList = (List<String>) ar.getData();
		assertThat(etlJobList.contains("测试作业0"), is(true));
		assertThat(etlJobList.contains("测试作业1"), is(true));
		assertThat(etlJobList.contains("测试作业2"), is(true));
		assertThat(etlJobList.contains("测试作业3"), is(true));
		assertThat(etlJobList.contains("测试作业4"), is(true));
		assertThat(etlJobList.contains("测试作业5"), is(true));
		assertThat(etlJobList.contains("测试作业6"), is(true));
		assertThat(etlJobList.contains("测试作业7"), is(true));
		assertThat(etlJobList.contains("测试作业8"), is(true));
		assertThat(etlJobList.contains("测试作业9"), is(true));
		assertThat(etlJobList.contains("测试作业10"), is(true));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "zycxcs")
				.post(getActionUrl("searchEtlJobDef"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "根据工程编号、作业名称查询作业定义信息",
			logicStep = "1.正确的数据访问1，数据都正确" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，etl_job不存在")
	@Test
	public void searchEtlJobDefById() {
		// 1.正确的数据访问1，数据都正确
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", "测试作业0")
				.post(getActionUrl("searchEtlJobDefById"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<String, Object> etlJobDef = ar.getDataForMap();
		assertThat(etlJobDef.get("etl_sys_cd"), is(EtlSysCd));
		assertThat(etlJobDef.get("com_exe_num").toString(), is(String.valueOf(0)));
		assertThat(etlJobDef.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
		assertThat(etlJobDef.get("disp_offset").toString(), is(String.valueOf(0)));
		assertThat(etlJobDef.get("exe_num").toString(), is(String.valueOf(1)));
		assertThat(etlJobDef.get("pro_para"), is("1"));
		assertThat(etlJobDef.get("overtime_val").toString(), is(String.valueOf(0)));
		assertThat(etlJobDef.get("job_disp_status"), is(Job_Status.RUNNING.getCode()));
		assertThat(etlJobDef.get("disp_type"), is(Dispatch_Frequency.PinLv.getCode()));
		assertThat(etlJobDef.get("job_priority").toString(), is(String.valueOf(0)));
		assertThat(etlJobDef.get("pro_dic"), is("/home/hyshf/dhw"));
		assertThat(etlJobDef.get("overlength_val").toString(), is(String.valueOf(0)));
		assertThat(etlJobDef.get("pro_type"), is(Pro_Type.SHELL.getCode()));
		assertThat(etlJobDef.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
		assertThat(etlJobDef.get("exe_frequency").toString(), is(String.valueOf(1)));
		assertThat(etlJobDef.get("job_priority_curr").toString(), is(String.valueOf(0)));
		assertThat(etlJobDef.get("job_return_val").toString(), is(String.valueOf(0)));
		assertThat(etlJobDef.get("sub_sys_cd"), is(SubSysCd));
		assertThat(etlJobDef.get("etl_job_desc"), is("测试作业定义0"));
		assertThat(etlJobDef.get("disp_freq"), is(Dispatch_Frequency.PinLv.getCode()));
		assertThat(etlJobDef.get("etl_job"), is("测试作业0"));
		assertThat(etlJobDef.get("pro_name"), is("zy.shell"));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", "测试作业0")
				.post(getActionUrl("searchEtlJobDefById"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		// 3.错误的数据访问1，etl_job不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", "测试作业")
				.post(getActionUrl("searchEtlJobDefById"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "新增保存作业信息",
			logicStep = "1.正确的数据访问1，数据都正确，调度频率为频率" +
					"2.正确的数据访问2，作业调度方式为定时" +
					"3.正确的数据访问3，调度方式为依赖" +
					"4.错误的数据访问1，etl_sys_cd为空" +
					"5.错误的数据访问2，etl_sys_cd为空格" +
					"6.错误的数据访问3，etl_sys_cd不存在" +
					"7.错误的数据访问4，sub_sys_cd为空" +
					"8.错误的数据访问5，sub_sys_cd为空格" +
					"9.错误的数据访问6，sub_sys_cd不存在" +
					"10.错误的数据访问7，etl_job为空" +
					"11.错误的数据访问8，etl_job为空" +
					"12.错误的数据访问9，etl_job已存在" +
					"13.错误的数据访问10，pro_type为空" +
					"14.错误的数据访问11，pro_type为空格" +
					"15.错误的数据访问12，pro_type不存在" +
					"16.错误的数据访问13，pro_name为空" +
					"17.错误的数据访问14，pro_name为空格" +
					"18.错误的数据访问15，etl_job_desc为空" +
					"19.错误的数据访问16，etl_job_desc为空格" +
					"20.错误的数据访问17，pro_dic为空" +
					"21.错误的数据访问18，pro_dic为空格" +
					"22.错误的数据访问19，log_dic为空" +
					"23.错误的数据访问20，log_dic为空格" +
					"24.错误的数据访问21，disp_freq为空" +
					"25.错误的数据访问22，disp_freq为空格" +
					"26.错误的数据访问23，disp_freq不存在" +
					"27.错误的数据访问24，job_eff_flag为空" +
					"28.错误的数据访问25，job_eff_flag为空格" +
					"29.错误的数据访问26，job_eff_flag不存在" +
					"30.错误的数据访问27，today_disp不存在")
	@Test
	public void saveEtlJobDef() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正确的数据访问1，数据都正确，调度频率为频率
			String dateTime = DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()) + " "
					+ DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime());
			String bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addEtlJob1")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			Map<String, Object> addEtlJob = SqlOperator.queryOneObject(db, "select * from "
					+ Etl_job_def.TableName + " where etl_sys_cd=? and etl_job=?", EtlSysCd, "addEtlJob1");
			assertThat(addEtlJob.get("etl_sys_cd"), is(EtlSysCd));
			assertThat(addEtlJob.get("sub_sys_cd"), is(SubSysCd));
			assertThat(addEtlJob.get("etl_job"), is("addEtlJob1"));
			assertThat(addEtlJob.get("etl_job_desc"), is("新增作业测试"));
			assertThat(addEtlJob.get("pro_type"), is(Pro_Type.SHELL.getCode()));
			assertThat(addEtlJob.get("pro_name"), is("add.shell"));
			assertThat(addEtlJob.get("pro_para"), is("0@1"));
			assertThat(addEtlJob.get("pro_dic"), is("/home/hyshf/etl/"));
			assertThat(addEtlJob.get("log_dic"), is("/home/hyshf/etl/log"));
			assertThat(addEtlJob.get("disp_freq"), is(Dispatch_Frequency.PinLv.getCode()));
			assertThat(addEtlJob.get("exe_frequency"), is(1));
			assertThat(addEtlJob.get("exe_num"), is(1));
			assertThat(addEtlJob.get("end_time"), is("2099-12-31 17:39:20"));
			assertThat(addEtlJob.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
			assertThat(addEtlJob.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
			assertThat(addEtlJob.get("comments"), is("频率作业测试"));
			// 2.正确的数据访问2，作业调度方式为定时
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addEtlJob2")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.DAILY.getCode())
					.addData("disp_offset", 0)
					.addData("disp_time", DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()).toString())
					.addData("job_priority", 0)
					.addData("comments", "定时作业测试")
					.addData("disp_type", Dispatch_Type.TPLUS1.getCode())
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 3.正确的数据访问3，调度方式为依赖
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addEtlJob3")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.DAILY.getCode())
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("disp_type", Dispatch_Type.DEPENDENCE.getCode())
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "依赖作业测试")
					.addData("pre_etl_job", new String[]{"测试作业4", "测试作业7"})
					.addData("status", Status.TRUE.getCode())
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 4.错误的数据访问1，etl_sys_cd为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", "")
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addEtlJob4")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 5.错误的数据访问2，etl_sys_cd为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", " ")
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addEtlJob5")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 6.错误的数据访问3，etl_sys_cd不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", "zybccs")
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addEtlJob6")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 7.错误的数据访问4，sub_sys_cd为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", "")
					.addData("etl_job", "addEtlJob7")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 8.错误的数据访问5，sub_sys_cd为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", " ")
					.addData("etl_job", "addEtlJob8")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 9.错误的数据访问6，sub_sys_cd不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", "xzzyrwcs")
					.addData("etl_job", "addEtlJob9")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 10.错误的数据访问7，etl_job为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", "")
					.addData("etl_job", " ")
					.addData("etl_job_desc", "新增作业测试10")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 11.错误的数据访问8，etl_job为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", " ")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 12.错误的数据访问9，etl_job已存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "测试作业0")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 13.错误的数据访问10，pro_type为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob13")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", "")
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 14.错误的数据访问11，pro_type为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob14")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", " ")
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 15.错误的数据访问12，pro_type不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob15")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", "abc")
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 16.错误的数据访问13，pro_name为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob16")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 17.错误的数据访问14，pro_name为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob17")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", " ")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 18.错误的数据访问15，etl_job_desc为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob18")
					.addData("etl_job_desc", "")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 19.错误的数据访问16，etl_job_desc为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob18")
					.addData("etl_job_desc", " ")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 20.错误的数据访问17，pro_dic为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob20")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 21.错误的数据访问18，pro_dic为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob21")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", " ")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 22.错误的数据访问19，log_dic为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob20")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 23.错误的数据访问20，log_dic为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob21")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", " ")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 24.错误的数据访问21，disp_freq为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob26")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log/")
					.addData("disp_freq", "")
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 25.错误的数据访问22，disp_freq为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob21")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log/")
					.addData("disp_freq", " ")
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 26.错误的数据访问23，disp_freq不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob21")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log/")
					.addData("disp_freq", "a")
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 27.错误的数据访问24，job_eff_flag为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob26")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log/")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", "")
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 28.错误的数据访问25，job_eff_flag为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob21")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log/")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", " ")
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 29.错误的数据访问26，job_eff_flag不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob21")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log/")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", "a")
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 30.错误的数据访问27，today_disp不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob21")
					.addData("etl_job_desc", "新增作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log/")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", "a")
					.addData("comments", "频率作业测试")
					.post(getActionUrl("saveEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "更新保存作业信息",
			logicStep = "1.正确的数据访问1，数据都正确，调度频率改变为频率" +
					"2.正确的数据访问2，数据都正确，调度频率不为频率,依赖-->依赖" +
					"3.正确的数据访问3，数据都正确，调度频率不为频率,依赖-->定时" +
					"4.正确的数据访问4，数据都正确，调度频率不为频率,定时-->依赖" +
					"5.错误的数据访问1，etl_sys_cd为空" +
					"6.错误的数据访问2，etl_sys_cd为空格" +
					"7.错误的数据访问3，etl_sys_cd不存在" +
					"8.错误的数据访问4，sub_sys_cd为空" +
					"9.错误的数据访问5，sub_sys_cd为空格" +
					"10.错误的数据访问6，sub_sys_cd不存在" +
					"11.错误的数据访问7，etl_job为空" +
					"12.错误的数据访问8，etl_job为空" +
					"13.错误的数据访问9，etl_job不存在" +
					"14.错误的数据访问10，pro_type为空" +
					"15.错误的数据访问11，pro_type为空格" +
					"16.错误的数据访问12，pro_type不存在" +
					"17.错误的数据访问13，pro_name为空" +
					"18.错误的数据访问14，pro_name为空格" +
					"19.错误的数据访问15，etl_job_desc为空" +
					"20.错误的数据访问16，etl_job_desc为空格" +
					"21.错误的数据访问17，pro_dic为空" +
					"22.错误的数据访问18，pro_dic为空格" +
					"23.错误的数据访问19，log_dic为空" +
					"24.错误的数据访问20，log_dic为空格" +
					"25.错误的数据访问21，disp_freq为空" +
					"26.错误的数据访问22，disp_freq为空格" +
					"27.错误的数据访问23，disp_freq不存在" +
					"28.错误的数据访问24，job_eff_flag为空" +
					"29.错误的数据访问25，job_eff_flag为空格" +
					"30.错误的数据访问26，job_eff_flag不存在" +
					"31.错误的数据访问27，today_disp不存在")
	@Test
	public void updateEtlJobDef() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正确的数据访问1，数据都正确，调度频率改变为频率
			String dateTime = DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()) + " "
					+ DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime());
			String bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd2)
					.addData("etl_job", "测试作业1")
					.addData("etl_job_desc", "更新作业测试1")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "upzy.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("old_disp_freq", Dispatch_Frequency.MONTHLY.getCode())
					.addData("old_dispatch_type", Dispatch_Type.DEPENDENCE.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "更新频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			Map<String, Object> upEtlJob = SqlOperator.queryOneObject(db, "select * from "
					+ Etl_job_def.TableName + " where etl_sys_cd=? and etl_job=?", EtlSysCd, "测试作业1");
			assertThat(upEtlJob.get("etl_sys_cd"), is(EtlSysCd));
			assertThat(upEtlJob.get("sub_sys_cd"), is(SubSysCd2));
			assertThat(upEtlJob.get("etl_job"), is("测试作业1"));
			assertThat(upEtlJob.get("etl_job_desc"), is("更新作业测试1"));
			assertThat(upEtlJob.get("pro_type"), is(Pro_Type.SHELL.getCode()));
			assertThat(upEtlJob.get("pro_name"), is("upzy.shell"));
			assertThat(upEtlJob.get("pro_para"), is("0@1"));
			assertThat(upEtlJob.get("pro_dic"), is("/home/hyshf/etl/"));
			assertThat(upEtlJob.get("log_dic"), is("/home/hyshf/etl/log"));
			assertThat(upEtlJob.get("disp_freq"), is(Dispatch_Frequency.PinLv.getCode()));
			assertThat(upEtlJob.get("exe_frequency"), is(1));
			assertThat(upEtlJob.get("exe_num"), is(1));
			assertThat(upEtlJob.get("end_time"), is("2099-12-31 17:39:20"));
			assertThat(upEtlJob.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
			assertThat(upEtlJob.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
			assertThat(upEtlJob.get("comments"), is("更新频率作业测试"));
			// 2.正确的数据访问2，数据都正确，调度频率不为频率,依赖-->依赖
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd2)
					.addData("etl_job", "测试作业6")
					.addData("old_pre_etl_job", new String[]{"测试作业10"})
					.addData("pre_etl_job", "测试作业8")
					.addData("etl_job_desc", "更新作业测试2")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "upzycs.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.DAILY.getCode())
					.addData("old_disp_freq", Dispatch_Frequency.YEARLY.getCode())
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("disp_type", Dispatch_Type.DEPENDENCE.getCode())
					.addData("old_dispatch_type", Dispatch_Type.DEPENDENCE.getCode())
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("status", Job_Effective_Flag.YES.getCode())
					.addData("comments", "更新依赖作业测试")
					.addData("pre_etl_job", new String[]{"测试作业0", "测试作业3"})
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			upEtlJob = SqlOperator.queryOneObject(db, "select * from "
					+ Etl_job_def.TableName + " where etl_sys_cd=? and etl_job=?", EtlSysCd, "测试作业6");
			assertThat(upEtlJob.get("etl_sys_cd"), is(EtlSysCd));
			assertThat(upEtlJob.get("sub_sys_cd"), is(SubSysCd2));
			assertThat(upEtlJob.get("etl_job"), is("测试作业6"));
			assertThat(upEtlJob.get("etl_job_desc"), is("更新作业测试2"));
			assertThat(upEtlJob.get("pro_type"), is(Pro_Type.SHELL.getCode()));
			assertThat(upEtlJob.get("pro_name"), is("upzycs.shell"));
			assertThat(upEtlJob.get("pro_para"), is("0@1"));
			assertThat(upEtlJob.get("pro_dic"), is("/home/hyshf/etl/"));
			assertThat(upEtlJob.get("log_dic"), is("/home/hyshf/etl/log"));
			assertThat(upEtlJob.get("disp_freq"), is(Dispatch_Frequency.DAILY.getCode()));
			assertThat(upEtlJob.get("disp_type"), is(Dispatch_Type.DEPENDENCE.getCode()));
			assertThat(upEtlJob.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
			assertThat(upEtlJob.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
			assertThat(upEtlJob.get("comments"), is("更新依赖作业测试"));
			List<Map<String, Object>> upEtlDepJobList = SqlOperator.queryList(db, "select * from "
					+ Etl_dependency.TableName + " where etl_sys_cd=? and etl_job=? " +
					"and pre_etl_job in(?,?)", EtlSysCd, "测试作业6", "测试作业0", "测试作业3");
			assertThat(upEtlDepJobList.size(), is(2));
			List<Map<String, Object>> upEtlDepJobList2 = SqlOperator.queryList(db, "select * from "
					+ Etl_dependency.TableName + " where etl_sys_cd=? and etl_job=? " +
					"and pre_etl_job in(?)", EtlSysCd, "测试作业6", "测试作业10");
			assertThat(upEtlDepJobList2.size(), is(0));
			// 3.正确的数据访问3，数据都正确，调度频率不为频率,依赖-->定时
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd2)
					.addData("etl_job", "测试作业5")
					.addData("old_pre_etl_job", new String[]{"测试作业7"})
					.addData("etl_job_desc", "更新作业测试3")
					.addData("pro_type", Pro_Type.BAT.getCode())
					.addData("pro_name", "upzyds.bat")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.DAILY.getCode())
					.addData("old_disp_freq", Dispatch_Frequency.YEARLY.getCode())
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("disp_type", Dispatch_Type.TPLUS1.getCode())
					.addData("old_dispatch_type", Dispatch_Type.DEPENDENCE.getCode())
					.addData("disp_time", DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()).toString())
					.addData("job_priority", 0)
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("status", Job_Effective_Flag.YES.getCode())
					.addData("comments", "更新依赖作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			upEtlJob = SqlOperator.queryOneObject(db, "select * from " + Etl_job_def.TableName +
					" where etl_sys_cd=? and etl_job=?", EtlSysCd, "测试作业5");
			assertThat(upEtlJob.get("etl_sys_cd"), is(EtlSysCd));
			assertThat(upEtlJob.get("sub_sys_cd"), is(SubSysCd2));
			assertThat(upEtlJob.get("etl_job"), is("测试作业5"));
			assertThat(upEtlJob.get("etl_job_desc"), is("更新作业测试3"));
			assertThat(upEtlJob.get("pro_type"), is(Pro_Type.BAT.getCode()));
			assertThat(upEtlJob.get("pro_name"), is("upzyds.bat"));
			assertThat(upEtlJob.get("pro_para"), is("0@1"));
			assertThat(upEtlJob.get("pro_dic"), is("/home/hyshf/etl/"));
			assertThat(upEtlJob.get("log_dic"), is("/home/hyshf/etl/log"));
			assertThat(upEtlJob.get("disp_freq"), is(Dispatch_Frequency.DAILY.getCode()));
			assertThat(upEtlJob.get("disp_type"), is(Dispatch_Type.TPLUS1.getCode()));
			assertThat(upEtlJob.get("job_priority").toString(), is(String.valueOf(0)));
			assertThat(upEtlJob.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
			assertThat(upEtlJob.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
			assertThat(upEtlJob.get("comments"), is("更新依赖作业测试"));
			upEtlDepJobList = SqlOperator.queryList(db, "select * from " + Etl_dependency.TableName +
							" where etl_sys_cd=? and etl_job=? and pre_etl_job in(?)", EtlSysCd,
					"测试作业5", "测试作业7");
			assertThat(upEtlDepJobList.isEmpty(), is(true));
			// 4.正确的数据访问4，数据都正确，调度频率不为频率,定时-->依赖
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd2)
					.addData("etl_job", "测试作业10")
					.addData("pre_etl_job", new String[]{"测试作业9", "测试作业8"})
					.addData("etl_job_desc", "更新作业测试4")
					.addData("pro_type", Pro_Type.BAT.getCode())
					.addData("pro_name", "upzyyl.bat")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.DAILY.getCode())
					.addData("old_disp_freq", Dispatch_Frequency.MONTHLY.getCode())
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("disp_type", Dispatch_Type.DEPENDENCE.getCode())
					.addData("old_dispatch_type", Dispatch_Type.TPLUS1.getCode())
					.addData("job_priority", 1)
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("status", Job_Effective_Flag.YES.getCode())
					.addData("comments", "更新依赖作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			upEtlJob = SqlOperator.queryOneObject(db, "select * from " + Etl_job_def.TableName +
					" where etl_sys_cd=? and etl_job=?", EtlSysCd, "测试作业10");
			assertThat(upEtlJob.get("etl_sys_cd"), is(EtlSysCd));
			assertThat(upEtlJob.get("sub_sys_cd"), is(SubSysCd2));
			assertThat(upEtlJob.get("etl_job"), is("测试作业10"));
			assertThat(upEtlJob.get("etl_job_desc"), is("更新作业测试4"));
			assertThat(upEtlJob.get("pro_type"), is(Pro_Type.BAT.getCode()));
			assertThat(upEtlJob.get("pro_name"), is("upzyyl.bat"));
			assertThat(upEtlJob.get("pro_para"), is("0@1"));
			assertThat(upEtlJob.get("pro_dic"), is("/home/hyshf/etl/"));
			assertThat(upEtlJob.get("log_dic"), is("/home/hyshf/etl/log"));
			assertThat(upEtlJob.get("disp_freq"), is(Dispatch_Frequency.DAILY.getCode()));
			assertThat(upEtlJob.get("disp_type"), is(Dispatch_Type.DEPENDENCE.getCode()));
			assertThat(upEtlJob.get("job_priority").toString(), is(String.valueOf(1)));
			assertThat(upEtlJob.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
			assertThat(upEtlJob.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
			assertThat(upEtlJob.get("comments"), is("更新依赖作业测试"));
			upEtlDepJobList = SqlOperator.queryList(db, "select * from " + Etl_dependency.TableName +
							" where etl_sys_cd=? and etl_job=? and pre_etl_job in(?,?)", EtlSysCd,
					"测试作业10", "测试作业9", "测试作业8");
			assertThat(upEtlDepJobList.size(), is(2));
			// 5.错误的数据访问1，etl_sys_cd为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", "")
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addEtlJob4")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 6.错误的数据访问2，etl_sys_cd为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", " ")
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addEtlJob5")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 7.错误的数据访问3，etl_sys_cd不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", "zybccs")
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addEtlJob6")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 8.错误的数据访问4，sub_sys_cd为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", "")
					.addData("etl_job", "addEtlJob7")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 9.错误的数据访问5，sub_sys_cd为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", " ")
					.addData("etl_job", "addEtlJob8")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 10.错误的数据访问6，sub_sys_cd不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", "xzzyrwcs")
					.addData("etl_job", "addEtlJob9")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 11.错误的数据访问7，etl_job为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", "")
					.addData("etl_job", " ")
					.addData("etl_job_desc", "更新作业测试10")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 12.错误的数据访问8，etl_job为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", " ")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 13.错误的数据访问9，etl_job不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "测试作业abc")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 14.错误的数据访问10，pro_type为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob13")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", "")
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 15.错误的数据访问11，pro_type为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob14")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", " ")
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 16.错误的数据访问12，pro_type不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob15")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", "abc")
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 17.错误的数据访问13，pro_name为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob16")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 18.错误的数据访问14，pro_name为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob17")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", " ")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 19.错误的数据访问15，etl_job_desc为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob18")
					.addData("etl_job_desc", "")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 20.错误的数据访问16，etl_job_desc为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob18")
					.addData("etl_job_desc", " ")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 21.错误的数据访问17，pro_dic为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob20")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 22.错误的数据访问18，pro_dic为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob21")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", " ")
					.addData("log_dic", "/home/hyshf/etl/log")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 23.错误的数据访问19，log_dic为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob20")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 24.错误的数据访问20，log_dic为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob21")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", " ")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 25.错误的数据访问21，disp_freq为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob26")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log/")
					.addData("disp_freq", "")
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 26.错误的数据访问22，disp_freq为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob21")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log/")
					.addData("disp_freq", " ")
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 27.错误的数据访问23，disp_freq不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob21")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log/")
					.addData("disp_freq", "a")
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 28.错误的数据访问24，job_eff_flag为空
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob26")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log/")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", "")
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 29.错误的数据访问25，job_eff_flag为空格
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob21")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log/")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", " ")
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 30.错误的数据访问26，job_eff_flag不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob21")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log/")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", "a")
					.addData("today_disp", Today_Dispatch_Flag.YES.getCode())
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 31.错误的数据访问27，today_disp不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("sub_sys_cd", SubSysCd)
					.addData("etl_job", "addJob21")
					.addData("etl_job_desc", "更新作业测试")
					.addData("pro_type", Pro_Type.SHELL.getCode())
					.addData("pro_name", "add.shell")
					.addData("pro_para", "0@1")
					.addData("pro_dic", "/home/hyshf/etl/")
					.addData("log_dic", "/home/hyshf/etl/log/")
					.addData("disp_freq", Dispatch_Frequency.PinLv.getCode())
					.addData("exe_frequency", 1)
					.addData("exe_num", 1)
					.addData("star_time", dateTime)
					.addData("end_time", "2099-12-31 17:39:20")
					.addData("job_eff_flag", Job_Effective_Flag.YES.getCode())
					.addData("today_disp", "a")
					.addData("comments", "频率作业测试")
					.post(getActionUrl("updateEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "删除Etl作业定义信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，etl_job不存在")
	@Test
	public void deleteEtlJobDef() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			// 删除前查询数据库，确认预期删除的数据存在
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Etl_job_def.TableName + " where etl_sys_cd=? and etl_job=?", EtlSysCd, "测试作业0");
			assertThat("删除操作前，Etl_job_def表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(1L));
			String bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业0")
					.post(getActionUrl("deleteEtlJobDef"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期删除的数据删除成功
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Etl_job_def.TableName + " where etl_sys_cd=? and etl_job=?", EtlSysCd, "测试作业0");
			assertThat("删除操作后，确认这条数据已删除", optionalLong.orElse(Long.MIN_VALUE), is(0L));
			// 2.错误的数据访问1，etl_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", "sccs1")
					.addData("etl_job", "测试作业3")
					.post(getActionUrl("deleteEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，etl_job不存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "cssczy")
					.post(getActionUrl("deleteEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "批量删除Etl作业定义信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，etl_job不存在")
	@Test
	public void batchDeleteEtlJobDef() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			// 删除前查询数据库，确认预期删除的数据存在
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
							Etl_job_def.TableName + " where etl_sys_cd=? and etl_job in(?,?)",
					EtlSysCd, "测试作业0", "测试作业1");
			assertThat("删除操作前，Etl_job_def表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(2L));
			String bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", new String[]{"测试作业0", "测试作业1"})
					.post(getActionUrl("batchDeleteEtlJobDef"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期删除的数据删除成功
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
							Etl_job_def.TableName + " where etl_sys_cd=? and etl_job in(?,?)",
					EtlSysCd, "测试作业0", "测试作业1");
			assertThat("删除操作后，确认这条数据已删除", optionalLong.orElse(Long.MIN_VALUE), is(0L));
			// 2.错误的数据访问1，etl_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", "sccs1")
					.addData("etl_job", new String[]{"测试作业3", "测试作业4"})
					.post(getActionUrl("batchDeleteEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，etl_job不存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", new String[]{"cssczy", "测试作业5"})
					.post(getActionUrl("batchDeleteEtlJobDef"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "分页查询etl资源定义信息，此方法只有三种情况",
			logicStep = "1.正常的数据访问1，数据都正常,para_cd 为空" +
					"2.正常的数据访问2，数据都正常,resource_type不为空" +
					"3.错误的数据访问1，etl_sys_cd不存在")
	@Test
	public void searchEtlResourceByPage() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlResourceByPage"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> resourceMap = ar.getDataForMap();
		// 验证查询数据的正确性
		List<Map<String, Object>> etlResource = (List<Map<String, Object>>) resourceMap.get("etlResourceList");
		for (Map<String, Object> map : etlResource) {
			String resource_type = map.get("resource_type").toString();
			if ("resource".equals(resource_type)) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("resource_used").toString(), is(String.valueOf(1)));
				assertThat(map.get("main_serv_sync"), is(Main_Server_Sync.YES.getCode()));
				assertThat(map.get("resource_max").toString(), is(String.valueOf(5)));
			} else if ("resource2".equals(resource_type)) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("resource_used").toString(), is(String.valueOf(2)));
				assertThat(map.get("main_serv_sync"), is(Main_Server_Sync.YES.getCode()));
				assertThat(map.get("resource_max").toString(), is(String.valueOf(10)));
			} else if ("resource3".equals(resource_type)) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("resource_used").toString(), is(String.valueOf(3)));
				assertThat(map.get("main_serv_sync"), is(Main_Server_Sync.YES.getCode()));
				assertThat(map.get("resource_max").toString(), is(String.valueOf(15)));
			}
		}
		// 2.正常的数据访问2，数据都正常,resource_type不为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("resource_type", "2")
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlResourceByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		resourceMap = ar.getDataForMap();
		// 验证查询数据的正确性
		etlResource = (List<Map<String, Object>>) resourceMap.get("etlResourceList");
		for (Map<String, Object> map : etlResource) {
			assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
			assertThat(map.get("resource_type").toString(), is("resource2"));
			assertThat(map.get("resource_used").toString(), is(String.valueOf(2)));
			assertThat(map.get("main_serv_sync"), is(Main_Server_Sync.YES.getCode()));
			assertThat(map.get("resource_max").toString(), is(String.valueOf(10)));
		}
		// 3.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "zydycs")
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlResourceByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "根据工程编号、变量名称查询作业系统参数，此方法只有三种情况",
			logicStep = "1.正常的数据访问1" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问1，resource_type不存在")
	@Test
	public void searchEtlResource() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("resource_type", "resource")
				.post(getActionUrl("searchEtlResource"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证查询数据的正确性
		Map<String, Object> dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("etl_sys_cd"), is(EtlSysCd));
		assertThat(dataForMap.get("resource_type"), is("resource"));
		assertThat(dataForMap.get("resource_used").toString(), is(String.valueOf(1)));
		assertThat(dataForMap.get("main_serv_sync"), is(Main_Server_Sync.YES.getCode()));
		assertThat(dataForMap.get("resource_max").toString(), is(String.valueOf(5)));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "searchEtlResource")
				.addData("resource_type", "resource")
				.post(getActionUrl("searchEtlResource"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，para_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("resource_type", "zydycs")
				.post(getActionUrl("searchEtlResource"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "查询资源类型",
			logicStep = "1.正确的数据访问1，数据都正确" +
					"2.错误的数据访问1，etl_sys_cd不存在,该方法只有两种情况")
	@Test
	public void searchEtlResourceType() {
		// 1.正确的数据访问1，数据都正确
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.post(getActionUrl("searchEtlResourceType"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		List<String> resourceTypeList = (List<String>) ar.getData();
		assertThat(resourceTypeList.contains("resource"), is(true));
		assertThat(resourceTypeList.contains("resource2"), is(true));
		assertThat(resourceTypeList.contains("resource3"), is(true));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "zycxcs")
				.post(getActionUrl("searchEtlResourceType"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "新增保存etl资源定义信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd为空" +
					"3.错误的数据访问2，etl_sys_cd为空格" +
					"4.错误的数据访问3，etl_sys_cd不存在" +
					"5.错误的数据访问4，resource_type为空" +
					"6.错误的数据访问5，resource_type为空格" +
					"7.错误的数据访问6，resource_type已存在" +
					"8.错误的数据访问7，resource_max为空" +
					"9.错误的数据访问8，resource_max为空格")
	@Test
	public void saveEtlResource() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("resource_type", "addResourceType1")
					.addData("resource_max", 10)
					.post(getActionUrl("saveEtlResource"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			Etl_resource etl_resource = SqlOperator.queryOneObject(db, Etl_resource.class,
					"select * from " + Etl_resource.TableName + " where etl_sys_cd=? and resource_type=?"
					, EtlSysCd, "addResourceType1").orElseThrow(() ->
					new BusinessException("sql查询错误或映射错误！"));
			assertThat(EtlSysCd, is(etl_resource.getEtl_sys_cd()));
			assertThat("addResourceType1", is(etl_resource.getResource_type()));
			assertThat(10, is(etl_resource.getResource_max()));
			// 2.错误的数据访问1，etl_sys_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", "")
					.addData("resource_type", "addResourceType1")
					.addData("resource_max", 10)
					.post(getActionUrl("saveEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，etl_sys_cd为空格
			bodyString = new HttpClient().addData("etl_sys_cd", "")
					.addData("resource_type", "addResourceType1")
					.addData("resource_max", 10)
					.post(getActionUrl("saveEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，etl_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", "bczydy")
					.addData("resource_type", "addResourceType1")
					.addData("resource_max", 10)
					.post(getActionUrl("saveEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 5.错误的数据访问4，resource_type为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("resource_type", "")
					.addData("resource_max", 10)
					.post(getActionUrl("saveEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 6.错误的数据访问5，resource_type为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("resource_type", " ")
					.addData("resource_max", 10)
					.post(getActionUrl("saveEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 7.错误的数据访问6，resource_type已存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("resource_type", "resource")
					.addData("resource_max", 10)
					.post(getActionUrl("saveEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 8.错误的数据访问7，resource_max为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("resource_type", "addResource2")
					.addData("resource_max", "")
					.post(getActionUrl("saveEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 9.错误的数据访问8，resource_max为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("resource_type", "addResource3")
					.addData("resource_max", " ")
					.post(getActionUrl("saveEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}

	}

	@Method(desc = "新增保存etl资源定义信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd为空" +
					"3.错误的数据访问2，etl_sys_cd为空格" +
					"4.错误的数据访问3，etl_sys_cd不存在" +
					"5.错误的数据访问4，resource_type为空" +
					"6.错误的数据访问5，resource_type为空格" +
					"7.错误的数据访问6，resource_type已存在" +
					"8.错误的数据访问7，resource_max为空" +
					"9.错误的数据访问8，resource_max为空格")
	@Test
	public void updateEtlResource() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("resource_type", "resource")
					.addData("resource_max", 20)
					.post(getActionUrl("updateEtlResource"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			Etl_resource etl_resource = SqlOperator.queryOneObject(db, Etl_resource.class,
					"select * from " + Etl_resource.TableName + " where etl_sys_cd=? and resource_type=?"
					, EtlSysCd, "resource").orElseThrow(() ->
					new BusinessException("sql查询错误或映射错误！"));
			assertThat(EtlSysCd, is(etl_resource.getEtl_sys_cd()));
			assertThat("resource", is(etl_resource.getResource_type()));
			assertThat(20, is(etl_resource.getResource_max()));
			// 2.错误的数据访问1，etl_sys_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", "")
					.addData("resource_type", "upResourceType1")
					.addData("resource_max", 10)
					.post(getActionUrl("updateEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，etl_sys_cd为空格
			bodyString = new HttpClient().addData("etl_sys_cd", "")
					.addData("resource_type", "upResourceType1")
					.addData("resource_max", 10)
					.post(getActionUrl("updateEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，etl_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", "bczydy")
					.addData("resource_type", "upResourceType1")
					.addData("resource_max", 10)
					.post(getActionUrl("updateEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 5.错误的数据访问4，resource_type为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("resource_type", "")
					.addData("resource_max", 10)
					.post(getActionUrl("updateEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 6.错误的数据访问5，resource_type为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("resource_type", " ")
					.addData("resource_max", 10)
					.post(getActionUrl("updateEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 7.错误的数据访问6，resource_type不存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("resource_type", "resourceType")
					.addData("resource_max", 10)
					.post(getActionUrl("updateEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 8.错误的数据访问7，resource_max为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("resource_type", "upResource2")
					.addData("resource_max", "")
					.post(getActionUrl("updateEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 9.错误的数据访问8，resource_max为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("resource_type", "upResource3")
					.addData("resource_max", " ")
					.post(getActionUrl("updateEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}

	}

	@Method(desc = "删除作业资源定义",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，resource_type不存在")
	@Test
	public void deleteEtlResource() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			// 删除前查询数据库，确认预期删除的数据存在
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Etl_resource.TableName + " where etl_sys_cd=? and resource_type=?", EtlSysCd, "resource");
			assertThat("删除操作前，Etl_resource表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(1L));
			String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("resource_type", "resource")
					.post(getActionUrl("deleteEtlResource"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期数据已删除
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " + Etl_resource.TableName
					+ " where etl_sys_cd=? and resource_type=?", EtlSysCd, "resource");
			assertThat("删除操作后，确认这条数据已删除", optionalLong.
					orElse(Long.MIN_VALUE), is(0L));
			// 2.错误的数据访问1，etl_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", "sccs1")
					.addData("resource_type", "resource")
					.post(getActionUrl("deleteEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，resource_type不存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("resource_type", "resourceType")
					.post(getActionUrl("deleteEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "批量删除作业资源定义",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，resource_type不存在")
	@Test
	public void batchDeleteEtlResource() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			// 删除前查询数据库，确认预期删除的数据存在
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
							Etl_resource.TableName + " where etl_sys_cd=? and resource_type in (?,?)",
					EtlSysCd, "resource", "resource2");
			assertThat("删除操作前，Etl_resource表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(2L));
			String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("resource_type", new String[]{"resource", "resource2"})
					.post(getActionUrl("batchDeleteEtlResource"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期数据已删除
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " + Etl_resource.TableName
					+ " where etl_sys_cd=? and resource_type in (?,?)", EtlSysCd, "resource", "resource2");
			assertThat("删除操作后，确认这条数据已删除", optionalLong.
					orElse(Long.MIN_VALUE), is(0L));
			// 2.错误的数据访问1，etl_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", "sccs1")
					.addData("resource_type", "resource")
					.post(getActionUrl("batchDeleteEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，resource_type不存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("resource_type", new String[]{"resourceType", "resource3"})
					.post(getActionUrl("batchDeleteEtlResource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "分页查询作业资源分配信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.正确的数据访问2，etl_job不为空" +
					"3.正确的数据访问3，etl_job不为空" +
					"4.错误的数据访问1，etl_sys_cd不存在")
	@Test
	public void searchEtlJobResourceRelaByPage() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlJobResourceRelaByPage"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证查询数据的正确性
		Map<String, Object> dataForMap = ar.getDataForMap();
		List<Map<String, Object>> jobResourceRelation = (List<Map<String, Object>>) dataForMap.
				get("jobResourceRelation");
		for (Map<String, Object> map : jobResourceRelation) {
			String etl_job = map.get("etl_job").toString();
			if ("测试作业3".equals(etl_job)) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("resource_type"), is("resource"));
				assertThat(map.get("resource_req").toString(), is(String.valueOf(1)));
			} else if ("测试作业4".equals(etl_job)) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("resource_type"), is("resource2"));
				assertThat(map.get("resource_req").toString(), is(String.valueOf(1)));
			}
			if ("测试作业5".equals(etl_job)) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("resource_type"), is("resource3"));
				assertThat(map.get("resource_req").toString(), is(String.valueOf(1)));
			}
		}
		// 2.正确的数据访问2，etl_job不为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", "3")
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlJobResourceRelaByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		jobResourceRelation = (List<Map<String, Object>>) dataForMap.
				get("jobResourceRelation");
		for (Map<String, Object> map : jobResourceRelation) {
			if ("测试作业3".equals(map.get("etl_job").toString())) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("resource_type"), is("resource"));
				assertThat(map.get("resource_req").toString(), is(String.valueOf(1)));
			}
		}
		// 3.正确的数据访问3，etl_job不为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("resource_type", "2")
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlJobResourceRelaByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		jobResourceRelation = (List<Map<String, Object>>) dataForMap.
				get("jobResourceRelation");
		for (Map<String, Object> map : jobResourceRelation) {
			if ("resource2".equals(map.get("resource_type").toString())) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("etl_job"), is("测试作业4"));
				assertThat(map.get("resource_req").toString(), is(String.valueOf(1)));
			}
		}
		// 4.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient().addData("etl_sys_cd", "zysycs")
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlJobResourceRelaByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "根据工程编号、作业名称查询作业资源分配情况",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.正确的数据访问1，etl_sys_cd不存在" +
					"3.正确的数据访问2，etl_job不存在")
	@Test
	public void searchEtlJobResourceRela() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", "测试作业3")
				.post(getActionUrl("searchEtlJobResourceRela"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证查询数据的正确性
		Map<String, Object> dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("etl_sys_cd"), is(EtlSysCd));
		assertThat(dataForMap.get("etl_job"), is("测试作业3"));
		assertThat(dataForMap.get("resource_type"), is("resource"));
		assertThat(dataForMap.get("resource_req").toString(), is(String.valueOf(1)));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient().addData("etl_sys_cd", "zysycs")
				.addData("etl_job", "测试作业3")
				.post(getActionUrl("searchEtlJobResourceRela"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，etl_job不存在
		bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", "测试作业")
				.post(getActionUrl("searchEtlJobResourceRela"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "新增保存资源分配信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd为空" +
					"3.错误的数据访问2，etl_sys_cd为空格" +
					"4.错误的数据访问3，etl_sys_cd不存在" +
					"5.错误的数据访问4，etl_job为空" +
					"6.错误的数据访问5，etl_job为空格" +
					"7.错误的数据访问6，etl_job已经分配过资源" +
					"8.错误的数据访问7，resource_type为空" +
					"9.错误的数据访问8，resource_type为空格" +
					"10.错误的数据访问9，resource_req为空" +
					"11.错误的数据访问10，resource_req为空格" +
					"12.错误的数据访问11，resource_req大于资源阈值")
	@Test
	public void saveEtlJobResourceRela() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业0")
					.addData("resource_type", "resource")
					.addData("resource_req", 1)
					.post(getActionUrl("saveEtlJobResourceRela"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			Etl_job_resource_rela resourceRela = SqlOperator.queryOneObject(db, Etl_job_resource_rela.class,
					"select * from " + Etl_job_resource_rela.TableName + " where etl_sys_cd=? and etl_job=?"
					, EtlSysCd, "测试作业0").orElseThrow(() ->
					new BusinessException("sql查询错误或映射错误！"));
			assertThat(EtlSysCd, is(resourceRela.getEtl_sys_cd()));
			assertThat("测试作业0", is(resourceRela.getEtl_job()));
			assertThat("resource", is(resourceRela.getResource_type()));
			assertThat(1, is(resourceRela.getResource_req()));
			// 2.错误的数据访问1，etl_sys_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", "")
					.addData("etl_job", "测试作业1")
					.addData("resource_type", "resource")
					.addData("resource_req", 2)
					.post(getActionUrl("saveEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，etl_sys_cd为空格
			bodyString = new HttpClient().addData("etl_sys_cd", " ")
					.addData("etl_job", "测试作业2")
					.addData("resource_type", "resource2")
					.addData("resource_req", 3)
					.post(getActionUrl("saveEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，etl_sys_cd为不存在的数据
			bodyString = new HttpClient().addData("etl_sys_cd", "xtcscs1")
					.addData("etl_job", "测试作业3")
					.addData("resource_type", "resource3")
					.addData("resource_req", 4)
					.post(getActionUrl("saveEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 5.错误的数据访问4，etl_job为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "")
					.addData("resource_type", "resource")
					.addData("resource_req", 2)
					.post(getActionUrl("saveEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 6.错误的数据访问5，etl_job为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", " ")
					.addData("resource_type", "resource2")
					.addData("resource_req", 3)
					.post(getActionUrl("saveEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 7.错误的数据访问6，etl_job已经分配过资源
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业3")
					.addData("resource_type", "resource3")
					.addData("resource_req", 4)
					.post(getActionUrl("saveEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 8.错误的数据访问7，resource_type为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业2")
					.addData("resource_type", "")
					.addData("resource_req", 2)
					.post(getActionUrl("saveEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 9.错误的数据访问8，resource_type为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业2")
					.addData("resource_type", " ")
					.addData("resource_req", 3)
					.post(getActionUrl("saveEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 10.错误的数据访问7，resource_req为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业2")
					.addData("resource_type", "resource")
					.addData("resource_req", "")
					.post(getActionUrl("saveEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 11.错误的数据访问10，resource_req为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业2")
					.addData("resource_type", "resource")
					.addData("resource_req", " ")
					.post(getActionUrl("saveEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 12.错误的数据访问11，resource_req大于资源阈值
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业2")
					.addData("resource_type", "resource")
					.addData("resource_req", 20)
					.post(getActionUrl("saveEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "新增保存资源分配信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd为空" +
					"3.错误的数据访问2，etl_sys_cd为空格" +
					"4.错误的数据访问3，etl_sys_cd不存在" +
					"5.错误的数据访问4，etl_job为空" +
					"6.错误的数据访问5，etl_job为空格" +
					"7.错误的数据访问6，resource_type为空" +
					"8.错误的数据访问7，resource_type为空格" +
					"9.错误的数据访问8，resource_req为空" +
					"10.错误的数据访问9，resource_req为空格" +
					"11.错误的数据访问10，resource_req大于资源阈值")
	@Test
	public void updateEtlJobResourceRela() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业3")
					.addData("resource_type", "resource")
					.addData("resource_req", 2)
					.post(getActionUrl("updateEtlJobResourceRela"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			Etl_job_resource_rela resourceRela = SqlOperator.queryOneObject(db, Etl_job_resource_rela.class,
					"select * from " + Etl_job_resource_rela.TableName + " where etl_sys_cd=? and etl_job=?"
					, EtlSysCd, "测试作业3").orElseThrow(() ->
					new BusinessException("sql查询错误或映射错误！"));
			assertThat(EtlSysCd, is(resourceRela.getEtl_sys_cd()));
			assertThat("测试作业3", is(resourceRela.getEtl_job()));
			assertThat("resource", is(resourceRela.getResource_type()));
			assertThat(2, is(resourceRela.getResource_req()));
			// 2.错误的数据访问1，etl_sys_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", "")
					.addData("etl_job", "测试作业4")
					.addData("resource_type", "resource")
					.addData("resource_req", 2)
					.post(getActionUrl("updateEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，etl_sys_cd为空格
			bodyString = new HttpClient().addData("etl_sys_cd", " ")
					.addData("etl_job", "测试作业4")
					.addData("resource_type", "resource2")
					.addData("resource_req", 3)
					.post(getActionUrl("updateEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，etl_sys_cd为不存在的数据
			bodyString = new HttpClient().addData("etl_sys_cd", "xtcscs1")
					.addData("etl_job", "测试作业4")
					.addData("resource_type", "resource3")
					.addData("resource_req", 4)
					.post(getActionUrl("updateEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 5.错误的数据访问4，etl_job为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "")
					.addData("resource_type", "resource")
					.addData("resource_req", 2)
					.post(getActionUrl("updateEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 6.错误的数据访问5，etl_job为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", " ")
					.addData("resource_type", "resource2")
					.addData("resource_req", 3)
					.post(getActionUrl("updateEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 7.错误的数据访问6，resource_type为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业4")
					.addData("resource_type", "")
					.addData("resource_req", 2)
					.post(getActionUrl("updateEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 8.错误的数据访问7，resource_type为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业4")
					.addData("resource_type", " ")
					.addData("resource_req", 3)
					.post(getActionUrl("updateEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 9.错误的数据访问8，resource_req为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业5")
					.addData("resource_type", "resource")
					.addData("resource_req", "")
					.post(getActionUrl("updateEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 10.错误的数据访问9，resource_req为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业5")
					.addData("resource_type", "resource")
					.addData("resource_req", " ")
					.post(getActionUrl("updateEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 11.错误的数据访问10，resource_req大于资源阈值
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业5")
					.addData("resource_type", "resource")
					.addData("resource_req", 20)
					.post(getActionUrl("updateEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "删除Etl作业资源关系",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，etl_job不存在")
	@Test
	public void deleteEtlJobResourceRela() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			// 删除前查询数据库，确认预期删除的数据存在
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
							Etl_job_resource_rela.TableName + " where etl_sys_cd=? and etl_job=?",
					EtlSysCd, "测试作业3");
			assertThat("删除操作前，Etl_job_resource_rela表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(1L));
			String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业3")
					.post(getActionUrl("deleteEtlJobResourceRela"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期数据已删除
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_resource_rela.TableName
					+ " where etl_sys_cd=? and etl_job=?", EtlSysCd, "测试作业3");
			assertThat("删除操作后，确认这条数据已删除", optionalLong.
					orElse(Long.MIN_VALUE), is(0L));
			// 2.错误的数据访问1，etl_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", "sccs1")
					.addData("etl_job", "测试作业3")
					.post(getActionUrl("deleteEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，etl_job不存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业")
					.post(getActionUrl("deleteEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "批量删除Etl作业资源关系",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，etl_job不存在")
	@Test
	public void batchDeleteEtlJobResourceRela() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			// 删除前查询数据库，确认预期删除的数据存在
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
							Etl_job_resource_rela.TableName + " where etl_sys_cd=? and etl_job in(?,?)",
					EtlSysCd, "测试作业3", "测试作业4");
			assertThat("删除操作前，Etl_job_resource_rela表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(2L));
			String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", new String[]{"测试作业3", "测试作业4"})
					.post(getActionUrl("batchDeleteEtlJobResourceRela"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期数据已删除
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_resource_rela.TableName
					+ " where etl_sys_cd=? and etl_job in(?,?)", EtlSysCd, "测试作业3", "测试作业4");
			assertThat("删除操作后，确认这条数据已删除", optionalLong.
					orElse(Long.MIN_VALUE), is(0L));
			// 2.错误的数据访问1，etl_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", "sccs1")
					.addData("etl_job", new String[]{"测试作业3", "测试作业4"})
					.post(getActionUrl("batchDeleteEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，etl_job不存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("etl_job", new String[]{"测试作业3", "测试作业"})
					.post(getActionUrl("batchDeleteEtlJobResourceRela"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "分页查询作业系统参数，此方法只有三种情况",
			logicStep = "1.正常的数据访问1，数据都正常,para_cd 为空" +
					"2.正常的数据访问2，数据都正常，para_cd不为空" +
					"3.错误的数据访问1，工程编号不存在")
	@Test
	public void searchEtlParaByPage() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlParaByPage"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证查询数据的正确性
		Map<String, Object> dataForMap = ar.getDataForMap();
		List<Map<String, Object>> etlParaList = (List<Map<String, Object>>) dataForMap.get("etlParaList");
		checkEtlParaData(etlParaList);
		// 2.正常的数据访问2，数据都正常，para_cd不为空
		bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("para_cd", "Date")
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlParaByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证查询数据的正确性
		dataForMap = ar.getDataForMap();
		etlParaList = (List<Map<String, Object>>) dataForMap.get("etlParaList");
		checkEtlParaData(etlParaList);
		// 3.错误的数据访问1，工程编号不存在
		bodyString = new HttpClient().addData("etl_sys_cd", "100")
				.addData("para_cd", "Date")
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlParaByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "根据工程编号、变量名称查询作业系统参数，此方法只有三种情况",
			logicStep = "1.正常的数据访问1" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问1，para_cd不存在")
	@Test
	public void searchEtlPara() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("para_cd", ParaCd)
				.post(getActionUrl("searchEtlPara"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证查询数据的正确性
		Map<String, Object> dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("para_type"), is(ParamType.CanShu.getCode()));
		assertThat(dataForMap.get("para_val"), is(SysDate));
		assertThat(dataForMap.get("etl_sys_cd"), is(EtlSysCd));
		assertThat(dataForMap.get("para_cd"), is(ParaCd));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient().addData("etl_sys_cd", "searchEtlPara")
				.addData("para_cd", ParaCd)
				.post(getActionUrl("searchEtlPara"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，para_cd不存在
		bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("para_cd", "paraCdCs")
				.post(getActionUrl("searchEtlPara"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "新增保存作业系统参数",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd为空" +
					"3.错误的数据访问2，etl_sys_cd为空格" +
					"4.错误的数据访问3，etl_sys_cd为不存在的数据" +
					"5.错误的数据访问4，para_cd为空" +
					"6.错误的数据访问5，para_cd为空格" +
					"7.错误的数据访问6，para_cd为已存在的数据" +
					"8.错误的数据访问7，para_type为空" +
					"9.错误的数据访问7，para_type为空格" +
					"10.错误的数据访问9，para_type为不合法的，不存在的代码项" +
					"11.错误的数据访问10，para_val为空格" +
					"12.错误的数据访问11，para_val为空格")
	@Test
	public void saveEtlPara() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", "addParaCd1")
					.addData("para_type", ParamType.CanShu.getCode())
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "新增系统参数测试1")
					.post(getActionUrl("saveEtlPara"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			Etl_para etlPara = SqlOperator.queryOneObject(db, Etl_para.class,
					"select * from " + Etl_para.TableName + " where etl_sys_cd=? and para_cd=?"
					, EtlSysCd, PREFIX + "addParaCd1").orElseThrow(() ->
					new BusinessException("sql查询错误或映射错误！"));
			assertThat(EtlSysCd, is(etlPara.getEtl_sys_cd()));
			assertThat(PREFIX + "addParaCd1", is(etlPara.getPara_cd()));
			assertThat(ParamType.CanShu.getCode(), is(etlPara.getPara_type()));
			assertThat(IsFlag.Shi.getCode(), is(etlPara.getPara_val()));
			assertThat("新增系统参数测试1", is(etlPara.getPara_desc()));
			// 2.错误的数据访问1，etl_sys_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", "")
					.addData("para_cd", "addParaCd2")
					.addData("para_type", ParamType.CanShu.getCode())
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "新增系统参数测试2")
					.post(getActionUrl("saveEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，etl_sys_cd为空格
			bodyString = new HttpClient().addData("etl_sys_cd", " ")
					.addData("para_cd", "addParaCd3")
					.addData("para_type", ParamType.CanShu.getCode())
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "新增系统参数测试3")
					.post(getActionUrl("saveEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，etl_sys_cd为不存在的数据
			bodyString = new HttpClient().addData("etl_sys_cd", "xtcscs1")
					.addData("para_cd", "addParaCd4")
					.addData("para_type", ParamType.CanShu.getCode())
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "新增系统参数测试4")
					.post(getActionUrl("saveEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 5.错误的数据访问4，para_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", "")
					.addData("para_type", ParamType.CanShu.getCode())
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "新增系统参数测试5")
					.post(getActionUrl("saveEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 6.错误的数据访问5，para_cd为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", " ")
					.addData("para_type", ParamType.CanShu.getCode())
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "新增系统参数测试6")
					.post(getActionUrl("saveEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 7.错误的数据访问6，para_cd为已存在的数据
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", "startDate")
					.addData("para_type", ParamType.CanShu.getCode())
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "新增系统参数测试7")
					.post(getActionUrl("saveEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 8.错误的数据访问7，para_type为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", "addParaCd8")
					.addData("para_type", "")
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "新增系统参数测试8")
					.post(getActionUrl("saveEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 9.错误的数据访问8，para_type为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", "addParaCd9")
					.addData("para_type", " ")
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "新增系统参数测试9")
					.post(getActionUrl("saveEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 10.错误的数据访问9，para_type为不合法的，不存在的代码项
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", "addParaCd9")
					.addData("para_type", "date")
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "新增系统参数测试9")
					.post(getActionUrl("saveEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 11.错误的数据访问10，para_val为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", "addParaCd9")
					.addData("para_type", " ")
					.addData("para_val", "")
					.addData("para_desc", "新增系统参数测试9")
					.post(getActionUrl("saveEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 12.错误的数据访问11，para_val为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", "addParaCd9")
					.addData("para_type", " ")
					.addData("para_val", " ")
					.addData("para_desc", "新增系统参数测试9")
					.post(getActionUrl("saveEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "更新保存作业系统参数",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd为空" +
					"3.错误的数据访问2，etl_sys_cd为空格" +
					"4.错误的数据访问3，etl_sys_cd为不存在的数据" +
					"5.错误的数据访问4，para_cd为空" +
					"6.错误的数据访问5，para_cd为空格" +
					"7.错误的数据访问6，para_cd为不存在的数据" +
					"8.错误的数据访问7，para_type为空" +
					"9.错误的数据访问7，para_type为空格" +
					"10.错误的数据访问9，para_type为不合法的，不存在的代码项" +
					"11.错误的数据访问10，para_val为空格" +
					"12.错误的数据访问11，para_val为空格")
	@Test
	public void updateEtlPara() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", ParaCd)
					.addData("para_type", ParamType.CanShu.getCode())
					.addData("para_val", IsFlag.Fou.getCode())
					.addData("para_desc", "编辑系统参数测试1")
					.post(getActionUrl("updateEtlPara"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			Etl_para etlPara = SqlOperator.queryOneObject(db, Etl_para.class,
					"select * from " + Etl_para.TableName + " where etl_sys_cd=? and para_cd=?"
					, EtlSysCd, ParaCd).orElseThrow(() ->
					new BusinessException("sql查询错误或映射错误！"));
			assertThat(EtlSysCd, is(etlPara.getEtl_sys_cd()));
			assertThat(ParaCd, is(etlPara.getPara_cd()));
			assertThat(ParamType.CanShu.getCode(), is(etlPara.getPara_type()));
			assertThat(IsFlag.Fou.getCode(), is(etlPara.getPara_val()));
			assertThat("编辑系统参数测试1", is(etlPara.getPara_desc()));
			// 2.错误的数据访问1，etl_sys_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", "")
					.addData("para_cd", "upParaCd2")
					.addData("para_type", ParamType.CanShu.getCode())
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "编辑系统参数测试2")
					.post(getActionUrl("updateEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，etl_sys_cd为空格
			bodyString = new HttpClient().addData("etl_sys_cd", " ")
					.addData("para_cd", "upParaCd3")
					.addData("para_type", ParamType.CanShu.getCode())
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "编辑系统参数测试3")
					.post(getActionUrl("updateEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，etl_sys_cd为不存在的数据
			bodyString = new HttpClient().addData("etl_sys_cd", "xtcscs1")
					.addData("para_cd", "upParaCd4")
					.addData("para_type", ParamType.CanShu.getCode())
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "编辑系统参数测试4")
					.post(getActionUrl("updateEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 5.错误的数据访问4，para_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", "")
					.addData("para_type", ParamType.CanShu.getCode())
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "编辑系统参数测试5")
					.post(getActionUrl("updateEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 6.错误的数据访问5，para_cd为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", " ")
					.addData("para_type", ParamType.CanShu.getCode())
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "编辑系统参数测试6")
					.post(getActionUrl("updateEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 7.错误的数据访问6，para_cd为不存在的数据
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", "upParaCd")
					.addData("para_type", ParamType.CanShu.getCode())
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "编辑系统参数测试7")
					.post(getActionUrl("updateEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 8.错误的数据访问7，para_type为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", "upParaCd8")
					.addData("para_type", "")
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "编辑系统参数测试8")
					.post(getActionUrl("updateEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 9.错误的数据访问8，para_type为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", "upParaCd9")
					.addData("para_type", " ")
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "编辑系统参数测试9")
					.post(getActionUrl("updateEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 10.错误的数据访问9，para_type为不合法的，不存在的代码项
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", "upParaCd9")
					.addData("para_type", "date")
					.addData("para_val", IsFlag.Shi.getCode())
					.addData("para_desc", "编辑系统参数测试9")
					.post(getActionUrl("updateEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 11.错误的数据访问10，para_val为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", "upParaCd9")
					.addData("para_type", " ")
					.addData("para_val", "")
					.addData("para_desc", "编辑系统参数测试9")
					.post(getActionUrl("updateEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 12.错误的数据访问11，para_val为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", "upParaCd9")
					.addData("para_type", " ")
					.addData("para_val", " ")
					.addData("para_desc", "编辑系统参数测试9")
					.post(getActionUrl("updateEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "批量删除作业系统参数",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，para_cd不存在")
	@Test
	public void batchDeleteEtlPara() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			// 删除前查询数据库，确认预期删除的数据存在
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
							Etl_para.TableName + " where etl_sys_cd=? and para_cd in (?,?)",
					EtlSysCd, ParaCd3, ParaCd4);
			assertThat("删除操作前，Etl_para表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(2L));
			String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", new String[]{ParaCd3, ParaCd4})
					.post(getActionUrl("batchDeleteEtlPara"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期数据已删除
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
							Etl_para.TableName + " where etl_sys_cd=? and para_cd in (?,?)",
					EtlSysCd, ParaCd3, ParaCd4);
			assertThat("删除操作后，确认这条数据已删除", optionalLong.
					orElse(Long.MIN_VALUE), is(0L));
			// 2.错误的数据访问1，etl_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", "sccs1")
					.addData("sub_sys_cd", new String[]{ParaCd, ParaCd2})
					.post(getActionUrl("batchDeleteEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，para_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", new String[]{"sccs1", ParaCd3})
					.post(getActionUrl("batchDeleteEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "删除作业系统参数",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，para_cd不存在")
	@Test
	public void deleteEtlPara() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			// 删除前查询数据库，确认预期删除的数据存在
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Etl_para.TableName + " where etl_sys_cd=? and para_cd=?", EtlSysCd, ParaCd5);
			assertThat("删除操作前，Etl_para表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(1L));
			String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", ParaCd5)
					.post(getActionUrl("deleteEtlPara"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期数据已删除
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " + Etl_para.TableName
					+ " where etl_sys_cd=? and para_cd=?", EtlSysCd, ParaCd5);
			assertThat("删除操作后，确认这条数据已删除", optionalLong.
					orElse(Long.MIN_VALUE), is(0L));
			// 2.错误的数据访问1，etl_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", "sccs1")
					.addData("sub_sys_cd", new String[]{ParaCd, ParaCd2})
					.post(getActionUrl("deleteEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，para_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("para_cd", "sccs2")
					.post(getActionUrl("deleteEtlPara"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "验证作业系统参数的正确性", logicStep = "该方法不需要测试")
	@Param(name = "etlParaList", desc = "参数信息的集合", range = "不为空")
	private void checkEtlParaData(List<Map<String, Object>> etlParaList) {
		for (Map<String, Object> etlParaMap : etlParaList) {
			// TODO 不确定原表是否有数据，所以只测自己造的测试数据
			if (etlParaMap.get("para_cd").equals(ParaCd) &&
					etlParaMap.get("etl_sys_cd").equals("zypzglcs")) {
				assertThat(etlParaMap.get("para_type"), is(ParamType.CanShu.getCode()));
				assertThat(etlParaMap.get("para_val"), is(SysDate));
			} else if (etlParaMap.get("para_cd").equals(ParaCd2) &&
					etlParaMap.get("etl_sys_cd").equals("zypzglcs")) {
				assertThat(etlParaMap.get("para_type"), is(ParamType.CanShu.getCode()));
				assertThat(etlParaMap.get("para_val"), is("99991231"));
			}
		}
	}

	@Method(desc = "分页查询作业依赖信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.正常的数据访问1，数据都正常,pre_etl_job不为空" +
					"3.正常的数据访问3，数据都正常,etl_job不为空" +
					"4.错误的数据访问1，etl_sys_cd不存在")
	@Test
	public void searchEtlDependencyByPage() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlDependencyByPage"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证查询数据的正确性
		Map<String, Object> dataForMap = ar.getDataForMap();
		List<Map<String, Object>> dependencyList = (List<Map<String, Object>>) dataForMap.get("etlDependencyList");
		for (Map<String, Object> map : dependencyList) {
			String etl_job = map.get("etl_job").toString();
			String pre_etl_job = map.get("pre_etl_job").toString();
			if (etl_job.equals("测试作业6") && pre_etl_job.equals("测试作业10")) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("pre_etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("status"), is(Status.TRUE.getCode()));
			} else if (etl_job.equals("测试作业5") && pre_etl_job.equals("测试作业7")) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("pre_etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("status"), is(Status.TRUE.getCode()));
			} else if (etl_job.equals("测试作业5") && pre_etl_job.equals("测试作业8")) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("pre_etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("status"), is(Status.TRUE.getCode()));
			} else if (etl_job.equals("测试作业8") && pre_etl_job.equals("测试作业1")) {
				assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("pre_etl_sys_cd"), is(EtlSysCd));
				assertThat(map.get("status"), is(Status.TRUE.getCode()));
			}
		}
		// 2.正常的数据访问1，数据都正常,pre_etl_job不为空
		bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("pre_etl_job", "测试作业7")
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlDependencyByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证查询数据的正确性
		dataForMap = ar.getDataForMap();
		dependencyList = (List<Map<String, Object>>) dataForMap.get("etlDependencyList");
		for (Map<String, Object> map : dependencyList) {
			assertThat(map.get("etl_job"), is("测试作业5"));
			assertThat(map.get("pre_etl_job"), is("测试作业7"));
			assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
			assertThat(map.get("pre_etl_sys_cd"), is(EtlSysCd));
			assertThat(map.get("status"), is(Status.TRUE.getCode()));
		}
		// 3.正常的数据访问3，数据都正常,etl_job不为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", "测试作业6")
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlDependencyByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证查询数据的正确性
		dataForMap = ar.getDataForMap();
		dependencyList = (List<Map<String, Object>>) dataForMap.get("etlDependencyList");
		for (Map<String, Object> map : dependencyList) {
			assertThat(map.get("etl_job"), is("测试作业6"));
			assertThat(map.get("pre_etl_job"), is("测试作业10"));
			assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
			assertThat(map.get("pre_etl_sys_cd"), is(EtlSysCd));
			assertThat(map.get("status"), is(Status.TRUE.getCode()));
		}
		// 4.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "ylcs")
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchEtlDependencyByPage"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "根据工程编号查询作业依赖信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问1，etl_job不存在" +
					"4.错误的数据访问3，pre_etl_job不存在")
	@Test
	public void searchEtlDependency() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("etl_job", "测试作业6")
				.addData("pre_etl_job", "测试作业10")
				.post(getActionUrl("searchEtlDependency"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证查询数据的正确性
		Map<String, Object> etlDependency = ar.getDataForMap();
		assertThat(etlDependency.get("etl_sys_cd"), is(EtlSysCd));
		assertThat(etlDependency.get("pre_etl_sys_cd"), is(EtlSysCd));
		assertThat(etlDependency.get("etl_job"), is("测试作业6"));
		assertThat(etlDependency.get("pre_etl_job"), is("测试作业10"));
		assertThat(etlDependency.get("status"), is(Status.TRUE.getCode()));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient().addData("etl_sys_cd", "ylzycs")
				.addData("etl_job", "测试作业6")
				.addData("pre_etl_job", "测试作业10")
				.post(getActionUrl("searchEtlDependency"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问1，etl_job不存在
		bodyString = new HttpClient().addData("etl_sys_cd", "ylzycs")
				.addData("etl_job", "测试作业")
				.addData("pre_etl_job", "测试作业10")
				.post(getActionUrl("searchEtlDependency"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，pre_etl_job不存在
		bodyString = new HttpClient().addData("etl_sys_cd", "ylzycs")
				.addData("etl_job", "测试作业6")
				.addData("pre_etl_job", "测试作业")
				.post(getActionUrl("searchEtlDependency"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "新增保存作业依赖",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd为空" +
					"3.错误的数据访问2，etl_sys_cd为空格" +
					"4.错误的数据访问3，etl_sys_cd不存在" +
					"5.错误的数据访问4，pre_etl_sys_cd为空" +
					"6.错误的数据访问5，pre_etl_sys_cd为空格" +
					"7.错误的数据访问6，pre_etl_sys_cd不存在" +
					"8.错误的数据访问7，etl_job为空" +
					"9.错误的数据访问8，etl_job为空格" +
					"10.错误的数据访问9，pre_etl_job为空" +
					"11.错误的数据访问10，etl_job为空格" +
					"12.错误的数据访问11，status为空" +
					"13.错误的数据访问12，status为空格" +
					"14.错误的数据访问13，status不存在" +
					"15.错误的数据访问14，作业依赖已存在")
	@Test
	public void saveEtlDependency() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", Status.TRUE.getCode())
					.post(getActionUrl("saveEtlDependency"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			Etl_dependency etlDependency = SqlOperator.queryOneObject(db, Etl_dependency.class,
					"select * from " + Etl_dependency.TableName + " where etl_sys_cd=? and etl_job=? " +
							" and pre_etl_job=?", EtlSysCd, "测试作业1", "测试作业0").orElseThrow(() ->
					new BusinessException("sql查询错误或映射错误！"));
			assertThat(EtlSysCd, is(etlDependency.getEtl_sys_cd()));
			assertThat(EtlSysCd, is(etlDependency.getPre_etl_sys_cd()));
			assertThat("测试作业1", is(etlDependency.getEtl_job()));
			assertThat("测试作业0", is(etlDependency.getPre_etl_job()));
			assertThat(Status.TRUE.getCode(), is(etlDependency.getStatus()));
			// 2.错误的数据访问1，etl_sys_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", "")
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", Status.TRUE.getCode())
					.post(getActionUrl("saveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，etl_sys_cd为空格
			bodyString = new HttpClient().addData("etl_sys_cd", " ")
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", Status.TRUE.getCode())
					.post(getActionUrl("saveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，etl_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", "xzylcs")
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", Status.TRUE.getCode())
					.post(getActionUrl("saveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 5.错误的数据访问4，pre_etl_sys_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", "")
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", Status.TRUE.getCode())
					.post(getActionUrl("saveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 6.错误的数据访问5，pre_etl_sys_cd为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", " ")
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", Status.TRUE.getCode())
					.post(getActionUrl("saveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 7.错误的数据访问1，pre_etl_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", "sycgbh")
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", Status.TRUE.getCode())
					.post(getActionUrl("saveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 8.错误的数据访问7，etl_job为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", Status.TRUE.getCode())
					.post(getActionUrl("saveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 9.错误的数据访问8，etl_job为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", " ")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", Status.TRUE.getCode())
					.post(getActionUrl("saveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 10.错误的数据访问9，pre_etl_job为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "")
					.addData("status", Status.TRUE.getCode())
					.post(getActionUrl("saveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 11.错误的数据访问10，pre_etl_job为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", " ")
					.addData("status", Status.TRUE.getCode())
					.post(getActionUrl("saveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 12.错误的数据访问11，status为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", "")
					.post(getActionUrl("saveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 13.错误的数据访问12，status为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", "")
					.post(getActionUrl("saveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 14.错误的数据访问13，status不合法
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", 2)
					.post(getActionUrl("saveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 15.错误的数据访问14，作业依赖已存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业6")
					.addData("pre_etl_job", "测试作业10")
					.addData("status", Status.TRUE.getCode())
					.post(getActionUrl("saveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "更新保存作业依赖",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，作业名称改变" +
					"3.错误的数据访问2，etl_sys_cd为空" +
					"4.错误的数据访问3，etl_sys_cd为空格" +
					"5.错误的数据访问4，etl_sys_cd不存在" +
					"6.错误的数据访问5，pre_etl_sys_cd为空" +
					"7.错误的数据访问6，pre_etl_sys_cd为空格" +
					"8.错误的数据访问7，pre_etl_sys_cd不存在" +
					"9.错误的数据访问8，etl_job为空" +
					"10.错误的数据访问9，etl_job为空格" +
					"11.错误的数据访问10，pre_etl_job为空" +
					"12.错误的数据访问11，etl_job为空格" +
					"13.错误的数据访问12，status为空" +
					"14.错误的数据访问13，status为空格" +
					"15.错误的数据访问14，status不存在")
	@Test
	public void updateEtlDependency() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问2，数据都正常,作业名称未改变，上游作业名称改变
			String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业5")
					.addData("pre_etl_job", "测试作业2")
					.addData("status", Status.TRUE.getCode())
					.addData("oldEtlJob", "测试作业5")
					.addData("oldPreEtlJob", "测试作业8")
					.post(getActionUrl("updateEtlDependency"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			Etl_dependency etlDependency = SqlOperator.queryOneObject(db, Etl_dependency.class,
					"select * from " + Etl_dependency.TableName + " where etl_sys_cd=? and etl_job=? " +
							" and pre_etl_job=?", EtlSysCd, "测试作业5", "测试作业2").orElseThrow(() ->
					new BusinessException("sql查询错误或映射错误！"));
			assertThat(EtlSysCd, is(etlDependency.getEtl_sys_cd()));
			assertThat(EtlSysCd, is(etlDependency.getPre_etl_sys_cd()));
			assertThat("测试作业5", is(etlDependency.getEtl_job()));
			assertThat("测试作业2", is(etlDependency.getPre_etl_job()));
			assertThat(Status.TRUE.getCode(), is(etlDependency.getStatus()));
			// 2.错误的数据访问1，作业名称改变
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业7")
					.addData("status", Status.TRUE.getCode())
					.addData("oldEtlJob", "测试作业5")
					.addData("oldPreEtlJob", "测试作业7")
					.post(getActionUrl("updateEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，etl_sys_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", "")
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", Status.TRUE.getCode())
					.addData("oldEtlJob", "测试作业6")
					.addData("oldPreEtlJob", "测试作业10")
					.post(getActionUrl("updateEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，etl_sys_cd为空格
			bodyString = new HttpClient().addData("etl_sys_cd", " ")
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", Status.TRUE.getCode())
					.addData("oldEtlJob", "测试作业6")
					.addData("oldPreEtlJob", "测试作业10")
					.post(getActionUrl("updateEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 5.错误的数据访问4，etl_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", "xzylcs")
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", Status.TRUE.getCode())
					.addData("oldEtlJob", "测试作业6")
					.addData("oldPreEtlJob", "测试作业10")
					.post(getActionUrl("updateEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 6.错误的数据访问5，pre_etl_sys_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", "")
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", Status.TRUE.getCode())
					.addData("oldEtlJob", "测试作业6")
					.addData("oldPreEtlJob", "测试作业10")
					.post(getActionUrl("updateEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 7.错误的数据访问6，pre_etl_sys_cd为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", " ")
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", Status.TRUE.getCode())
					.addData("oldEtlJob", "测试作业6")
					.addData("oldPreEtlJob", "测试作业10")
					.post(getActionUrl("updateEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 8.错误的数据访问7，pre_etl_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", "sycgbh")
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", Status.TRUE.getCode())
					.addData("oldEtlJob", "测试作业6")
					.addData("oldPreEtlJob", "测试作业10")
					.post(getActionUrl("updateEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 9.错误的数据访问8，etl_job为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", Status.TRUE.getCode())
					.addData("oldEtlJob", "测试作业6")
					.addData("oldPreEtlJob", "测试作业10")
					.post(getActionUrl("updateEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 10.错误的数据访问9，etl_job为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", " ")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", Status.TRUE.getCode())
					.addData("oldEtlJob", "测试作业6")
					.addData("oldPreEtlJob", "测试作业10")
					.post(getActionUrl("updateEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 11.错误的数据访问10，pre_etl_job为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "")
					.addData("status", Status.TRUE.getCode())
					.addData("oldEtlJob", "测试作业6")
					.addData("oldPreEtlJob", "测试作业10")
					.post(getActionUrl("updateEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 12.错误的数据访问11，pre_etl_job为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", " ")
					.addData("status", Status.TRUE.getCode())
					.addData("oldEtlJob", "测试作业6")
					.addData("oldPreEtlJob", "测试作业10")
					.post(getActionUrl("updateEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 13.错误的数据访问12，status为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", "")
					.addData("oldEtlJob", "测试作业6")
					.addData("oldPreEtlJob", "测试作业10")
					.post(getActionUrl("updateEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 14.错误的数据访问123，status为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", "")
					.addData("oldEtlJob", "测试作业6")
					.addData("oldPreEtlJob", "测试作业10")
					.post(getActionUrl("updateEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 15.错误的数据访问14，status不合法
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业1")
					.addData("pre_etl_job", "测试作业0")
					.addData("status", 2)
					.addData("oldEtlJob", "测试作业6")
					.addData("oldPreEtlJob", "测试作业10")
					.post(getActionUrl("updateEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "批量新增保存作业依赖",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd为空" +
					"3.错误的数据访问2，etl_sys_cd为空格" +
					"4.错误的数据访问3，etl_sys_cd不存在" +
					"5.错误的数据访问4，pre_etl_sys_cd为空" +
					"6.错误的数据访问5，pre_etl_sys_cd为空格" +
					"7.错误的数据访问6，pre_etl_sys_cd不存在" +
					"8.错误的数据访问7，status为空" +
					"9.错误的数据访问8，status为空格" +
					"10.错误的数据访问9，status不存在" +
					"11.错误的数据访问10，sub_sys_cd为空" +
					"12.错误的数据访问11，sub_sys_cd为空格" +
					"13.错误的数据访问12，sub_sys_cd不存在" +
					"14.错误的数据访问13，pre_sub_sys_cd为空" +
					"15.错误的数据访问14，pre_sub_sys_cd为空格" +
					"16.错误的数据访问15，pre_sub_sys_cd不存在")
	@Test
	public void batchSaveEtlDependency() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			String bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("status", Status.FALSE.getCode())
					.addData("sub_sys_cd", SubSysCd)
					.addData("pre_sub_sys_cd", SubSysCd2)
					.post(getActionUrl("batchSaveEtlDependency"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			List<Etl_dependency> dependencyList = SqlOperator.queryList(db, Etl_dependency.class,
					"select * from " + Etl_dependency.TableName + " where etl_sys_cd=? and etl_job=?" +
							" order by etl_job", EtlSysCd, "测试作业2");
			// SubSysCd下有3个作业，两个定时，一个依赖，SubSysCd2下有8个作业全都是依赖，所以如果依赖成功应该会有8个
			assertThat(dependencyList.size(), is(8));
			// 2.错误的数据访问1，etl_sys_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", "")
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("status", Status.TRUE.getCode())
					.addData("sub_sys_cd", SubSysCd)
					.addData("pre_sub_sys_cd", SubSysCd2)
					.post(getActionUrl("batchSaveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，etl_sys_cd为空格
			bodyString = new HttpClient().addData("etl_sys_cd", " ")
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("status", Status.TRUE.getCode())
					.addData("sub_sys_cd", SubSysCd)
					.addData("pre_sub_sys_cd", SubSysCd2)
					.post(getActionUrl("batchSaveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，etl_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", "plbcylcs")
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("status", Status.TRUE.getCode())
					.addData("sub_sys_cd", SubSysCd)
					.addData("pre_sub_sys_cd", SubSysCd2)
					.post(getActionUrl("batchSaveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 5.错误的数据访问4，pre_etl_sys_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", "")
					.addData("status", Status.TRUE.getCode())
					.addData("sub_sys_cd", SubSysCd)
					.addData("pre_sub_sys_cd", SubSysCd2)
					.post(getActionUrl("batchSaveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 6.错误的数据访问5，pre_etl_sys_cd为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", " ")
					.addData("status", Status.TRUE.getCode())
					.addData("sub_sys_cd", SubSysCd)
					.addData("pre_sub_sys_cd", SubSysCd2)
					.post(getActionUrl("batchSaveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 7.错误的数据访问6，pre_etl_sys_cd不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", "plbcylcs")
					.addData("status", Status.TRUE.getCode())
					.addData("sub_sys_cd", SubSysCd)
					.addData("pre_sub_sys_cd", SubSysCd2)
					.post(getActionUrl("batchSaveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 8.错误的数据访问7，status为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("status", "")
					.addData("sub_sys_cd", SubSysCd)
					.addData("pre_sub_sys_cd", SubSysCd2)
					.post(getActionUrl("batchSaveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 9.错误的数据访问8，status为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("status", " ")
					.addData("sub_sys_cd", SubSysCd)
					.addData("pre_sub_sys_cd", SubSysCd2)
					.post(getActionUrl("batchSaveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 10.错误的数据访问9，status不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("status", 5)
					.addData("sub_sys_cd", SubSysCd)
					.addData("pre_sub_sys_cd", SubSysCd2)
					.post(getActionUrl("batchSaveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 11.错误的数据访问10，sub_sys_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("status", Status.TRUE.getCode())
					.addData("sub_sys_cd", "")
					.addData("pre_sub_sys_cd", SubSysCd2)
					.post(getActionUrl("batchSaveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 12.错误的数据访问11，sub_sys_cd为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("status", Status.TRUE.getCode())
					.addData("sub_sys_cd", " ")
					.addData("pre_sub_sys_cd", SubSysCd2)
					.post(getActionUrl("batchSaveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 13.错误的数据访问12，sub_sys_cd为不存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("status", Status.TRUE.getCode())
					.addData("sub_sys_cd", "rw")
					.addData("pre_sub_sys_cd", SubSysCd2)
					.post(getActionUrl("batchSaveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 14.错误的数据访问13，pre_sub_sys_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("status", Status.TRUE.getCode())
					.addData("sub_sys_cd", "")
					.addData("pre_sub_sys_cd", SubSysCd2)
					.post(getActionUrl("batchSaveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 15.错误的数据访问14，pre_sub_sys_cd为空格
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("status", Status.TRUE.getCode())
					.addData("sub_sys_cd", SubSysCd)
					.addData("pre_sub_sys_cd", " ")
					.post(getActionUrl("batchSaveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 15.错误的数据访问14，pre_sub_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("status", Status.TRUE.getCode())
					.addData("sub_sys_cd", SubSysCd)
					.addData("pre_sub_sys_cd", "syrw")
					.post(getActionUrl("batchSaveEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "删除作业依赖",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，etl_job不存在" +
					"4.错误的数据访问3，pre_etl_job不存在")
	@Test
	public void deleteEtlDependency() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			// 删除前查询数据库，确认预期删除的数据存在
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
							Etl_dependency.TableName + " where etl_sys_cd=? and etl_job=? and pre_etl_job=?",
					EtlSysCd, "测试作业6", "测试作业10");
			assertThat("删除操作前，data_source表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(1L));
			String bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业6")
					.addData("pre_etl_job", "测试作业10")
					.post(getActionUrl("deleteEtlDependency"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期数据已删除
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " + Etl_dependency.TableName
					+ " where etl_sys_cd=? and etl_job=? and pre_etl_job=?", EtlSysCd, "测试作业6", "测试作业10");
			assertThat("删除操作后，确认这条数据已删除", optionalLong.
					orElse(Long.MIN_VALUE), is(0L));
			// 2.错误的数据访问1，etl_sys_cd不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", "sccs1")
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业6")
					.addData("pre_etl_job", "测试作业10")
					.post(getActionUrl("deleteEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，etl_job不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业")
					.addData("pre_etl_job", "测试作业10")
					.post(getActionUrl("deleteEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，pre_etl_job不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("etl_job", "测试作业5")
					.addData("pre_etl_job", "测试作业")
					.post(getActionUrl("deleteEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "批量删除作业依赖",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，etl_job不存在" +
					"4.错误的数据访问3，pre_etl_job不存在")
	@Test
	public void batchDeleteEtlDependency() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 存在的依赖
			List<Object> list = new ArrayList<>();
			for (int i = 0; i < 2; i++) {
				Map<String, String> jobMap = new HashMap<>();
				if (i == 0) {
					jobMap.put("etl_job", "测试作业6");
					jobMap.put("pre_etl_job", "测试作业10");
				} else {
					jobMap.put("etl_job", "测试作业5");
					jobMap.put("pre_etl_job", "测试作业7");
				}
				list.add(jobMap);
			}
			// 不存在的依赖
			List<Object> list2 = new ArrayList<>();
			for (int i = 0; i < 2; i++) {
				Map<String, String> jobMap = new HashMap<>();
				if (i == 0) {
					jobMap.put("etl_job", "测试作业6");
					jobMap.put("pre_etl_job", "测试作业1");
				} else {
					jobMap.put("etl_job", "测试作业5");
					jobMap.put("pre_etl_job", "测试作业2");
				}
				list2.add(jobMap);
			}
			// 1.正常的数据访问1，数据都正常
			// 删除前查询数据库，确认预期删除的数据存在
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
							Etl_dependency.TableName + " where etl_sys_cd=? and etl_job=? and pre_etl_job=?",
					EtlSysCd, "测试作业6", "测试作业10");
			assertThat("删除操作前，Etl_dependency表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(1L));
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
							Etl_dependency.TableName + " where etl_sys_cd=? and etl_job=? and pre_etl_job=?",
					EtlSysCd, "测试作业5", "测试作业7");
			assertThat("删除操作前，Etl_dependency表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(1L));
			String bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("batchEtlJob", JsonUtil.toJson(list))
					.post(getActionUrl("batchDeleteEtlDependency"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期数据已删除
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " + Etl_dependency.TableName
					+ " where etl_sys_cd=? and etl_job=? and pre_etl_job=?", EtlSysCd, "测试作业6", "测试作业10");
			assertThat("删除操作后，确认这条数据已删除", optionalLong.
					orElse(Long.MIN_VALUE), is(0L));
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
							Etl_dependency.TableName + " where etl_sys_cd=? and etl_job=? and pre_etl_job=?",
					EtlSysCd, "测试作业5", "测试作业7");
			assertThat("删除操作后，确认这条数据已删除", optionalLong.
					orElse(Long.MIN_VALUE), is(0L));
			// 2.错误的数据访问1，etl_sys_cd不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", "sccs1")
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("batchEtlJob", JsonUtil.toJson(list))
					.post(getActionUrl("batchDeleteEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，batchEtlJob不存在
			bodyString = new HttpClient()
					.addData("etl_sys_cd", EtlSysCd)
					.addData("pre_etl_sys_cd", EtlSysCd)
					.addData("batchEtlJob", JsonUtil.toJson(list2))
					.post(getActionUrl("batchDeleteEtlDependency"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "上传Excel文件",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，文件不存在")
	@Test
	public void uploadExcelFile() {
		File file = FileUtil.getFile("src//main//java//upload//Etl_resource.xlsx");
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.reset(SubmitMediaType.MULTIPART)
				.addFile("file", file)
				.post(getActionUrl("uploadExcelFile"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		// 校验数据正确性
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Etl_resource> etlResourceList = SqlOperator.queryList(db, Etl_resource.class,
					"select * from " + Etl_resource.TableName + " where etl_sys_cd=?", "drcs");
			for (Etl_resource etl_resource : etlResourceList) {
				assertThat(etl_resource.getResource_used(), is(3));
				assertThat(etl_resource.getResource_max(), is(10));
				assertThat(etl_resource.getResource_type(), is("resource"));
				assertThat(etl_resource.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
			}
		}
		// 2.错误的数据访问1，文件不存在
		bodyString = new HttpClient()
				.addData("file", "c:\\Etl_resources.xlsx")
				.post(getActionUrl("uploadExcelFile"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));


	}

	@Method(desc = "生成excel文件",
			logicStep = "1.正确的数据访问1，数据都有效" +
					"2.错误的数据访问1，etl_sys_cd不存在" +
					"3.错误的数据访问2，tableName不存在")
	@Test
	public void generateExcelTest() {
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("tableName", "etl_sub_sys_list")
				.post(getActionUrl("generateExcel")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getData().toString(),is("etl_sub_sys_list.xlsx"));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient().addData("etl_sys_cd", "dlfsjl")
				.addData("tableName", "etl_sub_sys_list")
				.post(getActionUrl("generateExcel")).getBodyString();
		 ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，tableName不存在
		bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
				.addData("tableName", "aaa")
				.post(getActionUrl("generateExcel")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}
}

