package hrds.c.biz.jobschedule;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
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
import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "作业调度配置管理测试类", author = "dhw", createdate = "2019/11/8 9:18")
public class JobConfigurationTest extends WebBaseTestCase {

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
    // 初始化模板ID参数
    private static final long EtlTempParaId = 30000L;
    // 初始化登录用户ID
    private static final long UserId = 6666L;
    // 初始化创建用户ID
    private static final long CreateId = 1000L;
    // 测试部门ID dep_id,测试作业调度部门
    private static final long DepId = 1000011L;
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
            for (int i = 0; i < 6; i++) {
                Etl_job_def etl_job_def = new Etl_job_def();
                etl_job_def.setEtl_sys_cd(EtlSysCd);
                etl_job_def.setEtl_job("测试作业" + i);
                etl_job_def.setPro_type(Pro_Type.SHELL.getCode());
                etl_job_def.setPro_name(i + "aaa.shell");
                etl_job_def.setPro_dic("/home/hyshf/dhw");
                etl_job_def.setEtl_job_desc("测试作业");
                etl_job_def.setPro_para("1");
                etl_job_def.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
                etl_job_def.setToday_disp(IsFlag.Shi.getCode());
                switch (i) {
                    case 0:
                        etl_job_def.setSub_sys_cd(SubSysCd);
                        etl_job_def.setDisp_type(Dispatch_Type.TPLUS0.getCode());
                        etl_job_def.setDisp_freq(Dispatch_Frequency.PinLv.getCode());
                        etl_job_def.setExe_frequency(1);
                        etl_job_def.setExe_num(1);
                        etl_job_def.setStar_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
                                + " " + DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
                        etl_job_def.setEnd_time("2020-12-31 10:30:30");
                        break;
                    case 1:
                        etl_job_def.setSub_sys_cd(SubSysCd);
                        etl_job_def.setDisp_type(Dispatch_Type.TPLUS1.getCode());
                        etl_job_def.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
                        break;
                    case 2:
                        etl_job_def.setSub_sys_cd(SubSysCd);
                        etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_def.setDisp_freq(Dispatch_Frequency.WEEKLY.getCode());
                        etl_job_def.setJob_disp_status(IsFlag.Shi.getCode());
                        break;
                    case 3:
                        etl_job_def.setSub_sys_cd(SubSysCd2);
                        etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_def.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_def.setJob_disp_status(IsFlag.Shi.getCode());
                        break;
                    case 4:
                        etl_job_def.setSub_sys_cd(SubSysCd2);
                        etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_def.setDisp_freq(Dispatch_Frequency.TENDAYS.getCode());
                        etl_job_def.setJob_disp_status(IsFlag.Shi.getCode());
                        break;
                    case 5:
                        etl_job_def.setSub_sys_cd(SubSysCd2);
                        etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_def.setDisp_freq(Dispatch_Frequency.YEARLY.getCode());
                        etl_job_def.setJob_disp_status(IsFlag.Shi.getCode());
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
            etl_job_temp.setEtl_temp_id(EtlTempId);
            etl_job_temp.setEtl_temp_type("上传作业模板");
            etl_job_temp.setPro_dic("/home/hyshf/zymb");
            etl_job_temp.setPro_name("上传模板");
            num = etl_job_temp.add(db);
            assertThat("测试数据Etl_job_temp初始化", num, is(1));
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
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    etl_para.setPara_cd("#starDate");
                    etl_para.setPara_val(SysDate);
                } else {
                    etl_para.setPara_cd("#endDate");
                    etl_para.setPara_val("99991231");
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
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            Etl_sub_sys_list subSysList = SqlOperator.queryOneObject(db, Etl_sub_sys_list.class,
                    "select * from " + Etl_sub_sys_list.TableName + " where etl_sys_cd=? and sub_sys_cd=?"
                    , EtlSysCd, "addSubSys1").orElseThrow(() ->
                    new BusinessException("sql查询错误或映射错误！"));
            assertThat(subSysList.getEtl_sys_cd(), is(EtlSysCd));
            assertThat(subSysList.getSub_sys_cd(), is("addSubSys1"));
            assertThat(subSysList.getSub_sys_desc(), is("addSubSys1"));
            assertThat(subSysList.getComments(), is("新增任务测试1"));
        }
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

    @Test
    public void batchDeleteEtlSubSys() {
        // 1.正常的数据访问1，数据都正常
        // 删除前查询数据库，确认预期删除的数据存在
        try (DatabaseWrapper db = new DatabaseWrapper()) {
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

    @Method(desc = "分页查询作业系统参数，此方法只有三种情况",
            logicStep = "1.正常的数据访问1，数据都正常,para_cd 为空" +
                    "2.正常的数据访问2，数据都正常，para_cd不为空" +
                    "3.错误的数据访问1，工程编号不存在")
    @Test
    public void searchEtlParaByPage() {
        // 1.正常的数据访问1，数据都正常,para_cd 为空
        String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
                .addData("currPage", 1)
                .addData("pageSize", 5)
                .post(getActionUrl("searchEtlParaByPage"))
                .getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                .orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
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
                .orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
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
                .orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
        assertThat(ar.isSuccess(), is(false));
    }

    @Method(desc = "验证作业系统参数的正确性", logicStep = "该方法不需要测试")
    @Param(name = "etlParaList", desc = "参数信息的集合", range = "不为空")
    private void checkEtlParaData(List<Map<String, Object>> etlParaList) {
        for (Map<String, Object> etlParaMap : etlParaList) {
            // TODO 不确定原表是否有数据，所以只测自己造的测试数据
            if (etlParaMap.get("para_cd").equals("#starDate") &&
                    etlParaMap.get("etl_sys_cd").equals("zypzglcs")) {
                assertThat(etlParaMap.get("para_type"), is(ParamType.CanShu.getCode()));
                assertThat(etlParaMap.get("para_val"), is(SysDate));
            } else if (etlParaMap.get("para_cd").equals("#endDate") &&
                    etlParaMap.get("etl_sys_cd").equals("zypzglcs")) {
                assertThat(etlParaMap.get("para_type"), is(ParamType.CanShu.getCode()));
                assertThat(etlParaMap.get("para_val"), is("99991231"));
            }
        }
    }

}

