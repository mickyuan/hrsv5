package hrds.c.biz.jobschedule;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
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
            for (int i = 0; i < 10; i++) {
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
                        etl_job_def.setDisp_type(Dispatch_Type.TPLUS0.getCode());
                        etl_job_def.setDisp_freq(Dispatch_Frequency.PinLv.getCode());
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
                }
                num = etl_job_def.add(db);
                assertThat("测试数据etl_job_def初始化", num, is(1));
            }
            // 6.构造etl_resource表测试数据
            Etl_resource etl_resource = new Etl_resource();
            etl_resource.setEtl_sys_cd(EtlSysCd);
            etl_resource.setResource_type("resource");
            etl_resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
            etl_resource.setResource_max(5);
            num = etl_resource.add(db);
            assertThat("测试数据etl_resource初始化", num, is(1));
            // 7.构造Etl_job_resource_rela表测试数据
            Etl_job_resource_rela resourceRelation = new Etl_job_resource_rela();
            resourceRelation.setEtl_sys_cd(EtlSysCd);
            resourceRelation.setResource_req(1);
            resourceRelation.setResource_type("resource");
            resourceRelation.setEtl_job("测试作业3");
            num = resourceRelation.add(db);
            assertThat("测试数据Etl_job_resource_rela初始化", num, is(1));
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
            etlDependency.setEtl_sys_cd(EtlSysCd);
            etlDependency.setPre_etl_sys_cd(EtlSysCd);
            etlDependency.setEtl_job("测试作业0");
            etlDependency.setPre_etl_job("测试作业1");
            etlDependency.setMain_serv_sync(Main_Server_Sync.YES.getCode());
            etlDependency.setStatus(IsFlag.Shi.getCode());
            num = etlDependency.add(db);
            assertThat("测试数据etl_job_temp_para初始化", num, is(1));
            // 12.提交事务
            SqlOperator.commitTransaction(db);
        }
        // 13.模拟用户登录
        String responseValue = new HttpClient()
                .buildSession()
                .addData("user_id", UserId)
                .addData("password", "1")
                .post("http://127.0.0.1:8099/A/action/hrds/a/biz/login/login")
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
                    "3.正确的数据访问1，etl_sys_cd不合法，currPage,pageSize（为空有默认值），" +
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
        assertThat(dataForMap.get("etl_sys_cd"), is(EtlSysCd));
        assertThat(dataForMap.get("totalSize"), is(2));
        assertThat(dataForMap.get("etl_sys_name"), is("dhwcs"));
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
        assertThat(dataForMap.get("etl_sys_cd"), is(EtlSysCd));
        assertThat(dataForMap.get("totalSize"), is(5));
        assertThat(dataForMap.get("etl_sys_name"), is("dhwcs"));
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
        // 3.正确的数据访问1，etl_sys_cd不合法，currPage,pageSize（为空有默认值），sub_sys_cd可为空所以错误数据只有一种情况
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
    public void searchEtlSubSys() {
        // 1.正常的数据访问1，数据都正常
        String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
                .addData("sub_sys_cd", SubSysCd)
                .post(getActionUrl("searchEtlSubSys"))
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
                .post(getActionUrl("searchEtlSubSys"))
                .getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                .orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
        assertThat(ar.isSuccess(), is(false));
        // 3.错误的数据访问2，sub_sys_cd不存在
        bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
                .addData("sub_sys_cd", "subSysCd1")
                .post(getActionUrl("searchEtlSubSys"))
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
            assertThat("删除操作前，data_source表中的确存在这样一条数据", optionalLong.
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
            assertThat("删除操作后，确认这条数据已删除", optionalLong.
                    orElse(Long.MIN_VALUE), is(0L));
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

    @Method(desc = "方法描述",
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
            assertThat("删除操作前，data_source表中的确存在这样一条数据", optionalLong.
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

    @Method(desc = "根据模板ID查询作业模板信息",
            logicStep = "1.正常的数据访问1，数据都正常" +
                    "2.错误的数据访问1，etl_temp_id不存在，该方法只有两种情况")
    @Test
    public void searchEtlJobTemplateById() {
        // 1.正常的数据访问1，数据都正常
        String bodyString = new HttpClient()
                .addData("etl_temp_id", EtlTempId)
                .post(getActionUrl("searchEtlJobTemplateById"))
                .getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
        assertThat(ar.isSuccess(), is(true));
        Map<Object, Object> etlJobTemplate = ar.getDataForMap();
        assertThat(etlJobTemplate.get("etl_temp_id").toString(), is(String.valueOf(EtlTempId)));
        assertThat(etlJobTemplate.get("etl_temp_type"), is("作业模板1"));
        assertThat(etlJobTemplate.get("pro_dic"), is("/home/hyshf/zymb"));
        assertThat(etlJobTemplate.get("pro_name"), is("upload.shell"));
        // 2.错误的数据访问1，etl_temp_id不存在
        bodyString = new HttpClient()
                .addData("etl_temp_id", 1)
                .post(getActionUrl("searchEtlJobTemplateById"))
                .getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
        assertThat(ar.isSuccess(), is(false));
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
                .addData("etl_job_temp_para", "0,1")
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
            assertThat(etlJobDef.get("pro_para"), is("0@1"));
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
                .addData("etl_job_temp_para", "0,1")
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
                .addData("etl_job_temp_para", "0,1")
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
                .addData("etl_job_temp_para", "0,1")
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
                .addData("etl_job_temp_para", "0,1")
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
                .addData("etl_job_temp_para", "0,1")
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
                .addData("etl_job_temp_para", "0,1")
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
                .addData("etl_job_temp_para", "0,1")
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
                .addData("etl_job_temp_para", "0,1")
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
                .addData("etl_job_temp_para", "0,1")
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
                .addData("etl_job_temp_para", "0,1")
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
                .addData("etl_job_temp_para", "0,1")
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
                .addData("etl_job_temp_para", "0,1")
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
        assertThat(etlJobDef.get("etl_sys_name"), is("dhwcs"));
        assertThat(etlJobDef.get("etl_sys_cd"), is(EtlSysCd));
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
        assertThat(etlJobDef.get("etl_sys_name"), is("dhwcs"));
        assertThat(etlJobDef.get("etl_sys_cd"), is(EtlSysCd));
        etlJobDefList = (List<Map<String, Object>>) etlJobDef.get("etlJobDefList");
        for (Map<String, Object> map : etlJobDefList) {
            String etl_job = map.get("etl_job").toString();
            if (etl_job.equals("测试作业0")) {
                assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
                assertThat(map.get("disp_type"), is(Dispatch_Type.TPLUS0.getCode()));
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
        assertThat(etlJobDef.get("etl_sys_name"), is("dhwcs"));
        assertThat(etlJobDef.get("etl_sys_cd"), is(EtlSysCd));
        etlJobDefList = (List<Map<String, Object>>) etlJobDef.get("etlJobDefList");
        for (Map<String, Object> map : etlJobDefList) {
            String etl_job = map.get("etl_job").toString();
            if (etl_job.equals("测试作业0")) {
                assertThat(map.get("etl_sys_cd"), is(EtlSysCd));
                assertThat(map.get("disp_type"), is(Dispatch_Type.TPLUS0.getCode()));
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
        assertThat(etlJobDef.get("etl_sys_name"), is("dhwcs"));
        assertThat(etlJobDef.get("etl_sys_cd"), is(EtlSysCd));
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
        assertThat(etlJobDef.get("etl_sys_name"), is("dhwcs"));
        assertThat(etlJobDef.get("etl_sys_cd"), is(EtlSysCd));
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
        assertThat(etlJobDef.get("etl_sys_name"), is("dhwcs"));
        assertThat(etlJobDef.get("etl_sys_cd"), is(EtlSysCd));
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

    @Method(desc = "根据工程编号、作业名称查询作业定义信息",
            logicStep = "1.正确的数据访问1，数据都正确" +
                    "2.错误的数据访问1，etl_sys_cd不存在" +
                    "3.错误的数据访问1，etl_job不存在")
    @Test
    public void searchEtlJobDef() {
        // 1.正确的数据访问1，数据都正确
        String bodyString = new HttpClient()
                .addData("etl_sys_cd", EtlSysCd)
                .addData("etl_job", "测试作业8")
                .post(getActionUrl("searchEtlJobDef"))
                .getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
        assertThat(ar.isSuccess(), is(true));
        Map<Object, Object> etlJobDef = ar.getDataForMap();
        assertThat(etlJobDef.get("disp_type"), is(Dispatch_Type.DEPENDENCE.getCode()));
        assertThat(etlJobDef.get("pro_name"), is("zy.perl"));
        assertThat(etlJobDef.get("disp_freq"), is(Dispatch_Frequency.YEARLY.getCode()));
        assertThat(etlJobDef.get("etl_job_desc"), is("测试作业定义8"));
        assertThat(etlJobDef.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
        assertThat(etlJobDef.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
        assertThat(etlJobDef.get("pro_type"), is(Pro_Type.PERL.getCode()));
        assertThat(etlJobDef.get("sub_sys_cd"), is(SubSysCd2));
        assertThat(etlJobDef.get("com_exe_num").toString(), is(String.valueOf(0)));
        assertThat(etlJobDef.get("disp_offset").toString(), is(String.valueOf(0)));
        assertThat(etlJobDef.get("exe_num").toString(), is(String.valueOf(0)));
        assertThat(etlJobDef.get("pro_para").toString(), is("1"));
        assertThat(etlJobDef.get("overtime_val").toString(), is(String.valueOf(0)));
        assertThat(etlJobDef.get("job_disp_status"), is(Job_Status.ERROR.getCode()));
        assertThat(etlJobDef.get("pro_dic"), is("/home/hyshf/dhw"));
        // 2.错误的数据访问1，etl_sys_cd不存在
        bodyString = new HttpClient()
                .addData("etl_sys_cd", "zycxcs")
                .addData("etl_job", "测试作业8")
                .post(getActionUrl("searchEtlJobDef"))
                .getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
        assertThat(ar.isSuccess(), is(false));
        // 3.错误的数据访问1，etl_job不存在
        bodyString = new HttpClient()
                .addData("etl_sys_cd", EtlSysCd)
                .addData("etl_job", "测试作业")
                .post(getActionUrl("searchEtlJobDef"))
                .getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
        assertThat(ar.isSuccess(), is(false));
    }

    @Method(desc = "新增保存作业信息",
            logicStep = "方法步骤")
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
            // 2.作业调度方式为定时
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
                    .post(getActionUrl("saveEtlJobDef"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(true));
        }
    }

    @Test
    public void updateEtlJobDef() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            // 1.正确的数据访问1，数据都正确，调度频率改变为频率
            String dateTime = DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()) + " "
                    + DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime());
            String bodyString = new HttpClient()
                    .addData("etl_sys_cd", EtlSysCd)
                    .addData("sub_sys_cd", SubSysCd)
                    .addData("etl_job", "测试作业1")
                    .addData("etl_job_desc", "更新作业测试1")
                    .addData("pro_type", Pro_Type.SHELL.getCode())
                    .addData("pro_name", "upzy.shell")
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
                    .addData("comments", "更新频率作业测试")
                    .post(getActionUrl("updateEtlJobDef"))
                    .getBodyString();
            ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(true));
            Map<String, Object> addEtlJob = SqlOperator.queryOneObject(db, "select * from "
                    + Etl_job_def.TableName + " where etl_sys_cd=? and etl_job=?", EtlSysCd, "测试作业1");
            assertThat(addEtlJob.get("etl_sys_cd"), is(EtlSysCd));
            assertThat(addEtlJob.get("sub_sys_cd"), is(SubSysCd));
            assertThat(addEtlJob.get("etl_job"), is("测试作业1"));
            assertThat(addEtlJob.get("etl_job_desc"), is("更新作业测试1"));
            assertThat(addEtlJob.get("pro_type"), is(Pro_Type.SHELL.getCode()));
            assertThat(addEtlJob.get("pro_name"), is("upzy.shell"));
            assertThat(addEtlJob.get("pro_para"), is("0@1"));
            assertThat(addEtlJob.get("pro_dic"), is("/home/hyshf/etl/"));
            assertThat(addEtlJob.get("log_dic"), is("/home/hyshf/etl/log"));
            assertThat(addEtlJob.get("disp_freq"), is(Dispatch_Frequency.PinLv.getCode()));
            assertThat(addEtlJob.get("exe_frequency"), is(1));
            assertThat(addEtlJob.get("exe_num"), is(1));
            assertThat(addEtlJob.get("end_time"), is("2099-12-31 17:39:20"));
            assertThat(addEtlJob.get("job_eff_flag"), is(Job_Effective_Flag.YES.getCode()));
            assertThat(addEtlJob.get("today_disp"), is(Today_Dispatch_Flag.YES.getCode()));
            assertThat(addEtlJob.get("comments"), is("更新频率作业测试"));
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
        assertThat(dataForMap.get("etl_sys_name"), is("dhwcs"));
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
        assertThat(dataForMap.get("etl_sys_name"), is("dhwcs"));
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
                    "3.错误的数据访问1，工程编号不存在" +
                    "3.错误的数据访问2，para_cd不存在")
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
            assertThat("删除操作前，data_source表中的确存在这样一条数据", optionalLong.
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
            assertThat("删除操作前，data_source表中的确存在这样一条数据", optionalLong.
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

}

