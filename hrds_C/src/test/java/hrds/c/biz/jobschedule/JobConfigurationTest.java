package hrds.c.biz.jobschedule;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mapdb.DB;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
@DocClass(desc = "作业调度配置管理测试类", author = "dhw", createdate = "2019/11/8 9:18")
public class JobConfigurationTest extends WebBaseTestCase {

    // 初始化工程编号
    private static final String etlSysCd = "-10000";
    // 初始化任务编号
    private static final String subSysCd = "-10000";
    // 初始化模板ID
    private static final long etlTempId = -20000L;
    // 初始化模板ID参数
    private static final long etlTempParaId = -30000L;
    // 初始化登录用户ID
    private static final long userId = 6666L;
    // 初始化创建用户ID
    private static final long createId = 1000L;
    // 测试部门ID dep_id,测试作业调度部门
    private static final long depId = -1000011L;

    @Before
    public void before() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            // 1.构造sys_user表测试数据
            Sys_user sysUser = new Sys_user();
            sysUser.setUser_id(userId);
            sysUser.setCreate_id(createId);
            sysUser.setDep_id(depId);
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
            // 创建department_info表实体对象
            Department_info department_info = new Department_info();
            department_info.setDep_id(depId);
            department_info.setDep_name("测试作业调度部门");
            department_info.setCreate_date(DateUtil.getSysDate());
            department_info.setCreate_time(DateUtil.getSysTime());
            department_info.setDep_remark("测试");
            num = department_info.add(db);
            assertThat("测试数据department_info初始化", num, is(1));
            // 3.构造etl_sys表测试数据
            Etl_sys etl_sys = new Etl_sys();
            etl_sys.setEtl_sys_cd(etlSysCd);
            etl_sys.setEtl_sys_name("dhwcs");
            etl_sys.setUser_id(userId);
            num = etl_sys.add(db);
            assertThat("测试数据etl_sys初始化", num, is(1));
            // 4.构造etl_sub_sys_list表测试数据
            Etl_sub_sys_list etl_sub_sys_list = new Etl_sub_sys_list();
            etl_sub_sys_list.setEtl_sys_cd(etlSysCd);
            etl_sub_sys_list.setSub_sys_cd(subSysCd);
            num = etl_sub_sys_list.add(db);
            assertThat("测试数据data_source初始化", num, is(1));
            // 5.构造etl_job_def表测试数据
            for (int i = 0; i < 6; i++) {
                Etl_job_def etl_job_def = new Etl_job_def();
                etl_job_def.setEtl_sys_cd(etlSysCd);
                etl_job_def.setSub_sys_cd(subSysCd);
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
                        etl_job_def.setDisp_type(Dispatch_Type.TPLUS0.getCode());
                        etl_job_def.setDisp_freq(Dispatch_Frequency.PinLv.getCode());
                        etl_job_def.setExe_frequency(1);
                        etl_job_def.setExe_num(1);
                        etl_job_def.setStar_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
                                + " " + DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
                        etl_job_def.setEnd_time("2020-12-31 10:30:30");
                        break;
                    case 1:
                        etl_job_def.setDisp_type(Dispatch_Type.TPLUS1.getCode());
                        etl_job_def.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
                        break;
                    case 2:
                        etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_def.setDisp_freq(Dispatch_Frequency.WEEKLY.getCode());
                        etl_job_def.setJob_disp_status(IsFlag.Shi.getCode());
                        break;
                    case 3:
                        etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_def.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_def.setJob_disp_status(IsFlag.Shi.getCode());
                        break;
                    case 4:
                        etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_def.setDisp_freq(Dispatch_Frequency.TENDAYS.getCode());
                        etl_job_def.setJob_disp_status(IsFlag.Shi.getCode());
                        break;
                    case 5:
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
            etl_resource.setEtl_sys_cd(etlSysCd);
            etl_resource.setResource_type("resource");
            etl_resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
            etl_resource.setResource_max(5);
            num = etl_resource.add(db);
            assertThat("测试数据etl_resource初始化", num, is(1));
            // 7.构造etl_resource表测试数据
            Etl_job_resource_rela resourceRelation = new Etl_job_resource_rela();
            resourceRelation.setEtl_sys_cd(etlSysCd);
            resourceRelation.setResource_req(1);
            resourceRelation.setResource_type("resource");
            resourceRelation.setEtl_job("测试作业3");
            num = resourceRelation.add(db);
            assertThat("测试数据Etl_job_resource_rela初始化", num, is(1));
            // 7.构造etl_resource表测试数据
            Etl_job_temp etl_job_temp = new Etl_job_temp();
            etl_job_temp.setEtl_temp_id(etlTempId);
            etl_job_temp.setEtl_temp_type("上传作业模板");
            etl_job_temp.setPro_dic("/home/hyshf/zymb");
            etl_job_temp.setPro_name("上传模板");
            num = etl_job_temp.add(db);
            assertThat("测试数据Etl_job_temp初始化", num, is(1));
            // 7.构造etl_job_temp_para表测试数据
            Etl_job_temp_para etl_job_temp_para = new Etl_job_temp_para();
            etl_job_temp_para.setEtl_temp_para_id(etlTempParaId);
            etl_job_temp_para.setEtl_temp_id(etlTempId);
            etl_job_temp_para.setEtl_job_para_size("0");
            etl_job_temp_para.setEtl_job_pro_para("是否压缩");
            etl_job_temp_para.setEtl_pro_para_sort(1L);
            etl_job_temp_para.setEtl_para_type("text");
            num = etl_job_temp_para.add(db);
            assertThat("测试数据etl_job_temp_para初始化", num, is(1));
            // 8.构造etl_job_temp_para表测试数据
            Etl_para etl_para = new Etl_para();
            etl_para.setEtl_sys_cd(etlSysCd);
            etl_para.setPara_cd("#date");
            etl_para.setPara_val("#txdate-1");
            num = etl_para.add(db);
            assertThat("测试数据etl_job_temp_para初始化", num, is(1));
            // 9.构造etl_job_temp_para表测试数据
            Etl_dependency etlDependency = new Etl_dependency();
            etlDependency.setEtl_sys_cd(etlSysCd);
            etlDependency.setPre_etl_sys_cd(etlSysCd);
            etlDependency.setEtl_job("测试作业0");
            etlDependency.setPre_etl_job("测试作业1");
            etlDependency.setMain_serv_sync(Main_Server_Sync.YES.getCode());
            etlDependency.setStatus(IsFlag.Shi.getCode());
            num = etlDependency.add(db);
            assertThat("测试数据etl_job_temp_para初始化", num, is(1));
            // 提交事务
            SqlOperator.commitTransaction(db);
        }
        // 29.模拟用户登录
        String responseValue = new HttpClient()
                .buildSession()
                .addData("user_id", userId)
                .addData("password", "1")
                .post("http://127.0.0.1:8099/A/action/hrds/a/biz/login/login")
                .getBodyString();
        Optional<ActionResult> ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class);
        assertThat("用户登录", ar.get().isSuccess(), is(true));
    }

    @After
    public void after() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            // 1.测试完成后删除sys_user表测试数据
            SqlOperator.execute(db, "delete from " + Sys_user.TableName + " where user_id=?", userId);
            // 判断sys_user数据是否被删除
            long num = SqlOperator.queryNumber(db, "select count(1) from " + Sys_user.TableName +
                    "  where user_id=?", userId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            // 2.测试完成后删除Etl_sub_sys表测试数据
            SqlOperator.execute(db, "delete from " + Etl_sub_sys_list.TableName + " where etl_sys_cd=?",
                    etlSysCd);
            // 判断Etl_sub_sys数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_sub_sys_list.TableName +
                    "  where etl_sys_cd=?", etlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            // 3.测试完成后删除etl_sys表测试数据
            SqlOperator.execute(db, "delete from " + Etl_sys.TableName + " where etl_sys_cd=?", etlSysCd);
            // 判断etl_sys数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_sys.TableName +
                    "  where etl_sys_cd=?", etlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            // 4.测试完成后删除Etl_job_temp表测试数据
            SqlOperator.execute(db, "delete from " + Etl_job_temp.TableName + " where etl_temp_id=?",
                    etlTempId);
            // 判断Etl_job_temp数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_temp.TableName +
                    "  where etl_temp_id=?", etlTempId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            // 5.测试完成后删除Etl_job_temp_para表测试数据
            SqlOperator.execute(db, "delete from " + Etl_job_temp_para.TableName + " where " +
                    "etl_temp_para_id=?", etlTempParaId);
            // 判断Etl_job_temp_para数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_temp_para.TableName +
                    "  where etl_temp_para_id=?", etlTempParaId).orElseThrow(() ->
                    new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            // 6.测试完成后删除etl_job_def表测试数据
            SqlOperator.execute(db, "delete from " + Etl_job_def.TableName + " where etl_sys_cd=?",
                    etlSysCd);
            // 判断etl_job_def数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_def.TableName +
                    "  where etl_sys_cd=?", etlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            // 7.测试完成后删除Etl_resource表测试数据
            SqlOperator.execute(db, "delete from " + Etl_resource.TableName + " where etl_sys_cd=?",
                    etlSysCd);
            // 判断Etl_resource数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_resource.TableName +
                    "  where etl_sys_cd=?", etlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            // 8.测试完成后删除Etl_job_resource_rela表测试数据
            SqlOperator.execute(db, "delete from " + Etl_job_resource_rela.TableName +
                    " where etl_sys_cd=?", etlSysCd);
            // 判断Etl_job_resource_rela数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_resource_rela.TableName +
                    "  where etl_sys_cd=?", etlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            // 9.测试完成后删除Etl_para表测试数据
            SqlOperator.execute(db, "delete from " + Etl_para.TableName + " where etl_sys_cd=?",
                    etlSysCd);
            // 判断Etl_para数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_para.TableName +
                    "  where etl_sys_cd=?", etlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            // 10.测试完成后删除Etl_dependency表测试数据
            SqlOperator.execute(db, "delete from " + Etl_dependency.TableName + " where etl_sys_cd=?",
                    etlSysCd);
            // 判断Etl_dependency数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_dependency.TableName +
                    "  where etl_sys_cd=?", etlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
        }
    }

    @Test
    public void getTaskInfoByPage() {
        // 1.正常的数据访问1，数据都正常
        String bodyString = new HttpClient().post(getActionUrl("getTaskInfoByPage"))
                .getBodyString();
        Optional<ActionResult> ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
    }
}

