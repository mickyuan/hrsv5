package hrds.c.biz.joblevelintervention;

import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
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

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class JobIevelInterventionActionTest extends WebBaseTestCase {
    // 初始化登录用户ID
    private static final long UserId = 6666L;
    // 初始化创建用户ID
    private static final long CreateId = 1000L;
    // 测试部门ID dep_id,测试作业调度部门
    private static final long DepId = 1000011L;
    // 初始化工程编号
    private static final String EtlSysCd = "zygyglcs";
    private static final String EtlSysCd2 = "zygyglcs2";
    private static final String EtlSysCd3 = "zygyglcs3";
    // 初始化任务编号
    private static final String SubSysCd = "zygyrwcs";
    private static final String SubSysCd2 = "zygyrwcs2";
    private static final String SubSysCd3 = "myrwcs";
    private static final String SubSysCd4 = "myrwcs2";
    private static final String SubSysCd5 = "myrwcs3";

    private static final String EventId = "zygysjh";
    private static final String EventId2 = "zygysjh2";
    private static final String EventId3 = "zygysjh3";
    private static final String EventId4 = "zygysjh4";
    private static final String EventId5 = "zygysjh5";
    private static final String EventId6 = "zygysjh6";

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
            sysUser.setUser_name("作业干预功能测试");
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
            for (int i = 0; i < 3; i++) {
                if (i == 0) {
                    etl_sys.setEtl_sys_cd(EtlSysCd);
                    etl_sys.setEtl_sys_name("dhwcs");
                } else if (i == 1) {
                    etl_sys.setEtl_sys_cd(EtlSysCd2);
                    etl_sys.setEtl_sys_name("dhwcs2");
                } else {
                    etl_sys.setEtl_sys_cd(EtlSysCd3);
                    etl_sys.setEtl_sys_name("dhwcs3");
                }
                etl_sys.setUser_id(UserId);
                etl_sys.setCurr_bath_date(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString());
                num = etl_sys.add(db);
                assertThat("测试数据etl_sys初始化", num, is(1));
            }
            // 4.构造etl_sub_sys_list表测试数据
            Etl_sub_sys_list etl_sub_sys_list = new Etl_sub_sys_list();
            for (int i = 1; i <= 5; i++) {
                switch (i) {
                    case 1:
                        etl_sub_sys_list.setSub_sys_cd(SubSysCd);
                        break;
                    case 2:
                        etl_sub_sys_list.setSub_sys_cd(SubSysCd2);
                        break;
                    case 3:
                        etl_sub_sys_list.setSub_sys_cd(SubSysCd3);
                        break;
                    case 4:
                        etl_sub_sys_list.setSub_sys_cd(SubSysCd4);
                        break;
                    case 5:
                        etl_sub_sys_list.setSub_sys_cd(SubSysCd5);
                        break;
                }
                etl_sub_sys_list.setEtl_sys_cd(EtlSysCd);
                etl_sub_sys_list.setSub_sys_desc("任务测试" + i);
                etl_sub_sys_list.setComments("测试" + i);
                num = etl_sub_sys_list.add(db);
                assertThat("测试数据data_source初始化", num, is(1));
            }
            // 5.构造etl_job_curr表测试数据
            for (int i = 1; i < 20; i++) {
                Etl_job_cur etl_job_cur = new Etl_job_cur();
                etl_job_cur.setEtl_job("测试作业" + i);
                etl_job_cur.setPro_dic("/home/hyshf/dhw");
                etl_job_cur.setEtl_job_desc("测试作业定义" + i);
                etl_job_cur.setPro_para("1");
                etl_job_cur.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
                etl_job_cur.setToday_disp(Today_Dispatch_Flag.YES.getCode());
                etl_job_cur.setCurr_bath_date(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString());
                switch (i) {
                    case 1:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd);
                        etl_job_cur.setPro_name("zy.shell");
                        etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd);
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.PinLv.getCode());
                        etl_job_cur.setExe_frequency(1L);
                        etl_job_cur.setExe_num(1);
                        etl_job_cur.setStar_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
                                + " " + DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
                        etl_job_cur.setEnd_time("2020-12-31 10:30:30");
                        etl_job_cur.setJob_disp_status(Job_Status.ERROR.getCode());
                        break;
                    case 2:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd);
                        etl_job_cur.setPro_name("zy.jar");
                        etl_job_cur.setPro_type(Pro_Type.JAVA.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd);
                        etl_job_cur.setDisp_type(Dispatch_Type.TPLUS1.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.DONE.getCode());
                        break;
                    case 3:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd);
                        etl_job_cur.setPro_name("zy.shell");
                        etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.RUNNING.getCode());
                        break;
                    case 4:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd);
                        etl_job_cur.setPro_name("zy.py");
                        etl_job_cur.setPro_type(Pro_Type.PYTHON.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd2);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.WEEKLY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.WAITING.getCode());
                        break;
                    case 5:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd);
                        etl_job_cur.setPro_name("zy.bat");
                        etl_job_cur.setPro_type(Pro_Type.BAT.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd2);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.PENDING.getCode());
                        break;
                    case 6:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd);
                        etl_job_cur.setPro_name("zy.bat");
                        etl_job_cur.setPro_type(Pro_Type.BAT.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd2);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.STOP.getCode());
                        break;
                    case 7:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd2);
                        etl_job_cur.setPro_name("zy2.shell");
                        etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd2);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.ERROR.getCode());
                        break;
                    case 8:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd2);
                        etl_job_cur.setPro_name("zy2.shell");
                        etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.PENDING.getCode());
                        break;
                    case 9:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd2);
                        etl_job_cur.setPro_name("zy2.bat");
                        etl_job_cur.setPro_type(Pro_Type.BAT.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd2);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.STOP.getCode());
                        break;
                    case 10:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd2);
                        etl_job_cur.setPro_name("zy2.bat");
                        etl_job_cur.setPro_type(Pro_Type.BAT.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd2);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.PENDING.getCode());
                        break;
                    case 11:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd2);
                        etl_job_cur.setPro_name("zy2.shell");
                        etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd2);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.RUNNING.getCode());
                        break;
                    case 12:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd2);
                        etl_job_cur.setPro_name("zy2.shell");
                        etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd2);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.DONE.getCode());
                        break;
                    case 13:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd2);
                        etl_job_cur.setPro_name("zy2.shell");
                        etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd2);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.RUNNING.getCode());
                        break;
                    case 14:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd2);
                        etl_job_cur.setPro_name("zy2.shell");
                        etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd2);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.PENDING.getCode());
                        break;
                    case 15:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd2);
                        etl_job_cur.setPro_name("zy2.shell");
                        etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd2);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.RUNNING.getCode());
                        break;
                    case 16:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd2);
                        etl_job_cur.setPro_name("zy2.shell");
                        etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd2);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.ERROR.getCode());
                        break;
                    case 17:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd2);
                        etl_job_cur.setPro_name("zy2.shell");
                        etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd2);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.ERROR.getCode());
                        break;
                    case 18:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd2);
                        etl_job_cur.setPro_name("zy2.shell");
                        etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd2);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.PENDING.getCode());
                        break;
                    case 19:
                        etl_job_cur.setEtl_sys_cd(EtlSysCd2);
                        etl_job_cur.setPro_name("zy2.shell");
                        etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd2);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.PENDING.getCode());
                        break;
                }
                num = etl_job_cur.add(db);
                assertThat("测试数据etl_job_cur初始化", num, is(1));
            }
            // 6.构造etl_job_hand表测试数据
            Etl_job_hand etl_job_hand = new Etl_job_hand();
            for (int i = 1; i < 7; i++) {
                etl_job_hand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
                switch (i) {
                    case 1:
                        etl_job_hand.setEtl_sys_cd(EtlSysCd);
                        etl_job_hand.setEtl_job("测试作业1");
                        etl_job_hand.setHand_status(Meddle_status.FALSE.getCode());
                        etl_job_hand.setEvent_id(EventId);
                        etl_job_hand.setEtl_hand_type(Meddle_type.SYS_ORIGINAL.getCode());
                        break;
                    case 2:
                        etl_job_hand.setEtl_sys_cd(EtlSysCd);
                        etl_job_hand.setEtl_job("测试作业2");
                        etl_job_hand.setEvent_id(EventId2);
                        etl_job_hand.setHand_status(Meddle_status.TRUE.getCode());
                        etl_job_hand.setEtl_hand_type(Meddle_type.SYS_PAUSE.getCode());
                        break;
                    case 3:
                        etl_job_hand.setEtl_sys_cd(EtlSysCd);
                        etl_job_hand.setEtl_job("测试作业3");
                        etl_job_hand.setHand_status(Meddle_status.DONE.getCode());
                        etl_job_hand.setEvent_id(EventId3);
                        etl_job_hand.setEtl_hand_type(Meddle_type.SYS_RESUME.getCode());
                        break;
                    case 4:
                        etl_job_hand.setEtl_sys_cd(EtlSysCd);
                        etl_job_hand.setEtl_job("测试作业4");
                        etl_job_hand.setHand_status(Meddle_status.RUNNING.getCode());
                        etl_job_hand.setEvent_id(EventId4);
                        etl_job_hand.setEtl_hand_type(Meddle_type.SYS_SHIFT.getCode());
                        break;
                    case 5:
                        etl_job_hand.setEtl_sys_cd(EtlSysCd);
                        etl_job_hand.setEtl_job("测试作业5");
                        etl_job_hand.setHand_status(Meddle_status.ERROR.getCode());
                        etl_job_hand.setEvent_id(EventId5);
                        etl_job_hand.setEtl_hand_type(Meddle_type.SYS_STOP.getCode());
                        break;
                    case 6:
                        etl_job_hand.setEtl_sys_cd(EtlSysCd);
                        etl_job_hand.setEtl_job("测试作业6");
                        etl_job_hand.setHand_status(Meddle_status.RUNNING.getCode());
                        etl_job_hand.setEvent_id(EventId6);
                        etl_job_hand.setEtl_hand_type(Meddle_type.SYS_STOP.getCode());
                        break;
                }
                num = etl_job_hand.add(db);
                assertThat("测试数据etl_job_hand初始化", num, is(1));
            }
            // 7.构造etl_job_hand_his表测试数据
            Etl_job_hand_his etl_job_hand_his = new Etl_job_hand_his();
            for (int i = 1; i < 6; i++) {
                etl_job_hand_his.setEtl_sys_cd(EtlSysCd);
                etl_job_hand_his.setEtl_job("测试作业" + i);
                etl_job_hand_his.setMain_serv_sync(Main_Server_Sync.YES.getCode());
                switch (i) {
                    case 1:
                        etl_job_hand_his.setHand_status(Meddle_status.FALSE.getCode());
                        etl_job_hand_his.setEvent_id(EventId);
                        etl_job_hand_his.setEtl_hand_type(Meddle_type.SYS_ORIGINAL.getCode());
                        break;
                    case 2:
                        etl_job_hand_his.setEvent_id(EventId2);
                        etl_job_hand_his.setHand_status(Meddle_status.TRUE.getCode());
                        etl_job_hand_his.setEtl_hand_type(Meddle_type.SYS_PAUSE.getCode());
                        break;
                    case 3:
                        etl_job_hand_his.setHand_status(Meddle_status.DONE.getCode());
                        etl_job_hand_his.setEvent_id(EventId3);
                        etl_job_hand_his.setEtl_hand_type(Meddle_type.SYS_RESUME.getCode());
                        break;
                    case 4:
                        etl_job_hand_his.setHand_status(Meddle_status.RUNNING.getCode());
                        etl_job_hand_his.setEvent_id(EventId4);
                        etl_job_hand_his.setEtl_hand_type(Meddle_type.SYS_SHIFT.getCode());
                        break;
                    case 5:
                        etl_job_hand_his.setHand_status(Meddle_status.ERROR.getCode());
                        etl_job_hand_his.setEvent_id(EventId5);
                        etl_job_hand_his.setEtl_hand_type(Meddle_type.SYS_STOP.getCode());
                        break;
                }
                num = etl_job_hand_his.add(db);
                assertThat("测试数据etl_job_hand_his初始化", num, is(1));
            }
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
            SqlOperator.execute(db, "delete from " + Etl_sys.TableName + " where etl_sys_cd=?", EtlSysCd2);
            // 判断etl_sys数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_sys.TableName +
                    "  where etl_sys_cd=?", EtlSysCd2).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            SqlOperator.execute(db, "delete from " + Etl_sys.TableName + " where etl_sys_cd=?", EtlSysCd3);
            // 判断etl_sys数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_sys.TableName +
                    "  where etl_sys_cd=?", EtlSysCd3).orElseThrow(() -> new RuntimeException("count fail!"));
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
            SqlOperator.execute(db, "delete from " + Etl_job_hand.TableName + " where etl_sys_cd=?",
                    EtlSysCd2);
            // 判断Etl_job_hand数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_hand.TableName +
                    "  where etl_sys_cd=?", EtlSysCd2).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            SqlOperator.execute(db, "delete from " + Etl_job_hand.TableName + " where etl_sys_cd=?",
                    EtlSysCd3);
            // 判断Etl_job_hand数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_hand.TableName +
                    "  where etl_sys_cd=?", EtlSysCd3).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            // 6.测试完删除Etl_job_hand_his表测试数据
            SqlOperator.execute(db, "delete from " + Etl_job_hand_his.TableName + " where etl_sys_cd=?",
                    EtlSysCd);
            // 判断Etl_job_hand_his数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_hand_his.TableName +
                    "  where etl_sys_cd=?", EtlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            SqlOperator.execute(db, "delete from " + Etl_job_hand_his.TableName + " where etl_sys_cd=?",
                    EtlSysCd2);
            // 判断Etl_job_hand_his数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_hand_his.TableName +
                    "  where etl_sys_cd=?", EtlSysCd2).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            SqlOperator.execute(db, "delete from " + Etl_job_hand_his.TableName + " where etl_sys_cd=?",
                    EtlSysCd3);
            // 判断Etl_job_hand_his数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_hand_his.TableName +
                    "  where etl_sys_cd=?", EtlSysCd3).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            // 7.测试完删除Etl_job_cur表测试数据
            SqlOperator.execute(db, "delete from " + Etl_job_cur.TableName + " where etl_sys_cd=?",
                    EtlSysCd);
            // 判断Etl_job_cur数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_cur.TableName +
                    "  where etl_sys_cd=?", EtlSysCd).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            SqlOperator.execute(db, "delete from " + Etl_job_cur.TableName + " where etl_sys_cd=?",
                    EtlSysCd2);
            // 判断Etl_job_cur数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_cur.TableName +
                    "  where etl_sys_cd=?", EtlSysCd2).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            SqlOperator.execute(db, "delete from " + Etl_job_cur.TableName + " where etl_sys_cd=?",
                    EtlSysCd3);
            // 判断Etl_job_cur数据是否被删除
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_cur.TableName +
                    "  where etl_sys_cd=?", EtlSysCd3).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            // 8.提交事务
            SqlOperator.commitTransaction(db);
        }
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
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
        assertThat(ar.isSuccess(), is(true));
        Map<Object, Object> map = ar.getDataForMap();
        assertThat(map.get("totalSize").toString(), is(String.valueOf("6")));
        List<Map<String, Object>> etlJobInfoList = (List<Map<String, Object>>) map.get("etlJobInfoList");
        for (Map<String, Object> etlJobInfo : etlJobInfoList) {
            String etl_job = etlJobInfo.get("etl_job").toString();
            if (etl_job.equals("测试作业1")) {
                assertThat(etlJobInfo.get("etl_sys_cd"), is(EtlSysCd));
                assertThat(etlJobInfo.get("job_disp_status"), is(Job_Status.ERROR.getCode()));
                assertThat(etlJobInfo.get("subsysname"), is("任务测试1(zygyrwcs)"));
                assertThat(etlJobInfo.get("curr_bath_date"), is(DateUtil.parseStr2DateWith8Char(
                        DateUtil.getSysDate()).toString()));
            } else if (etl_job.equals("测试作业2")) {
                assertThat(etlJobInfo.get("etl_sys_cd"), is(EtlSysCd));
                assertThat(etlJobInfo.get("job_disp_status"), is(Job_Status.DONE.getCode()));
                assertThat(etlJobInfo.get("subsysname"), is("任务测试1(zygyrwcs)"));
                assertThat(etlJobInfo.get("curr_bath_date"), is(DateUtil.parseStr2DateWith8Char(
                        DateUtil.getSysDate()).toString()));
            } else if (etl_job.equals("测试作业3")) {
                assertThat(etlJobInfo.get("etl_sys_cd"), is(EtlSysCd));
                assertThat(etlJobInfo.get("job_disp_status"), is(Job_Status.RUNNING.getCode()));
                assertThat(etlJobInfo.get("subsysname"), is("任务测试1(zygyrwcs)"));
                assertThat(etlJobInfo.get("curr_bath_date"), is(DateUtil.parseStr2DateWith8Char(
                        DateUtil.getSysDate()).toString()));
            } else if (etl_job.equals("测试作业4")) {
                assertThat(etlJobInfo.get("etl_sys_cd"), is(EtlSysCd));
                assertThat(etlJobInfo.get("job_disp_status"), is(Job_Status.WAITING.getCode()));
                assertThat(etlJobInfo.get("subsysname"), is("任务测试2(zygyrwcs2)"));
                assertThat(etlJobInfo.get("curr_bath_date"), is(DateUtil.parseStr2DateWith8Char(
                        DateUtil.getSysDate()).toString()));
            } else if (etl_job.equals("测试作业5")) {
                assertThat(etlJobInfo.get("etl_sys_cd"), is(EtlSysCd));
                assertThat(etlJobInfo.get("job_disp_status"), is(Job_Status.PENDING.getCode()));
                assertThat(etlJobInfo.get("subsysname"), is("任务测试2(zygyrwcs2)"));
                assertThat(etlJobInfo.get("curr_bath_date"), is(DateUtil.parseStr2DateWith8Char(
                        DateUtil.getSysDate()).toString()));
            }
        }
        // 2.正确的数据访问2，etl_job不为空
        bodyString = new HttpClient()
                .addData("etl_sys_cd", EtlSysCd)
                .addData("etl_job", "1")
                .post(getActionUrl("searchJobLevelIntervention"))
                .getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
        assertThat(ar.isSuccess(), is(true));
        map = ar.getDataForMap();
        assertThat(map.get("totalSize").toString(), is(String.valueOf(1)));
        etlJobInfoList = (List<Map<String, Object>>) map.get("etlJobInfoList");
        assertThat(etlJobInfoList.get(0).get("etl_sys_cd"), is(EtlSysCd));
        assertThat(etlJobInfoList.get(0).get("job_disp_status"), is(Job_Status.ERROR.getCode()));
        assertThat(etlJobInfoList.get(0).get("subsysname"), is("任务测试1(zygyrwcs)"));
        assertThat(etlJobInfoList.get(0).get("curr_bath_date"), is(DateUtil.parseStr2DateWith8Char(
                DateUtil.getSysDate()).toString()));
        // 3.正确的数据访问3，sub_sys_desc不为空
        bodyString = new HttpClient()
                .addData("etl_sys_cd", EtlSysCd)
                .addData("sub_sys_desc", "1")
                .post(getActionUrl("searchJobLevelIntervention"))
                .getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
        assertThat(ar.isSuccess(), is(true));
        map = ar.getDataForMap();
        assertThat(map.get("totalSize").toString(), is(String.valueOf(3)));
        etlJobInfoList = (List<Map<String, Object>>) map.get("etlJobInfoList");
        for (Map<String, Object> etlJobInfo : etlJobInfoList) {
            if (etlJobInfo.get("etl_job").toString().equals("测试作业1")) {
                assertThat(etlJobInfo.get("etl_sys_cd"), is(EtlSysCd));
                assertThat(etlJobInfo.get("job_disp_status"), is(Job_Status.ERROR.getCode()));
                assertThat(etlJobInfo.get("subsysname"), is("任务测试1(zygyrwcs)"));
                assertThat(etlJobInfo.get("curr_bath_date"), is(DateUtil.parseStr2DateWith8Char(
                        DateUtil.getSysDate()).toString()));
            } else if (etlJobInfo.get("etl_job").toString().equals("测试作业2")) {
                assertThat(etlJobInfo.get("etl_sys_cd"), is(EtlSysCd));
                assertThat(etlJobInfo.get("job_disp_status"), is(Job_Status.DONE.getCode()));
                assertThat(etlJobInfo.get("subsysname"), is("任务测试1(zygyrwcs)"));
                assertThat(etlJobInfo.get("curr_bath_date"), is(DateUtil.parseStr2DateWith8Char(
                        DateUtil.getSysDate()).toString()));
            } else if (etlJobInfo.get("etl_job").toString().equals("测试作业3")) {
                assertThat(etlJobInfo.get("etl_sys_cd"), is(EtlSysCd));
                assertThat(etlJobInfo.get("job_disp_status"), is(Job_Status.RUNNING.getCode()));
                assertThat(etlJobInfo.get("subsysname"), is("任务测试1(zygyrwcs)"));
                assertThat(etlJobInfo.get("curr_bath_date"), is(DateUtil.parseStr2DateWith8Char(
                        DateUtil.getSysDate()).toString()));
            }
        }
        // 4.正确的数据访问4,job_status不为空
        bodyString = new HttpClient()
                .addData("etl_sys_cd", EtlSysCd)
                .addData("job_status", Job_Status.ERROR.getCode())
                .post(getActionUrl("searchJobLevelIntervention"))
                .getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
        assertThat(ar.isSuccess(), is(true));
        map = ar.getDataForMap();
        assertThat(map.get("totalSize").toString(), is(String.valueOf(1)));
        etlJobInfoList = (List<Map<String, Object>>) map.get("etlJobInfoList");
        assertThat(etlJobInfoList.get(0).get("etl_sys_cd"), is(EtlSysCd));
        assertThat(etlJobInfoList.get(0).get("job_disp_status"), is(Job_Status.ERROR.getCode()));
        assertThat(etlJobInfoList.get(0).get("subsysname"), is("任务测试1(zygyrwcs)"));
        assertThat(etlJobInfoList.get(0).get("curr_bath_date"), is(DateUtil.parseStr2DateWith8Char(
                DateUtil.getSysDate()).toString()));
        // 5.错误的数据访问1，etl_sys_cd不存在
        bodyString = new HttpClient()
                .addData("etl_sys_cd", "zyjgycs")
                .post(getActionUrl("searchJobLevelIntervention"))
                .getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
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
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
        assertThat(ar.isSuccess(), is(true));
        Map<Object, Object> map = ar.getDataForMap();
        assertThat(map.get("totalSize").toString(), is(String.valueOf(6)));
        List<Map<String, Object>> currInterventionList = (List<Map<String, Object>>)
                map.get("currInterventionList");
        // 验证数据的正确性
        checkInterventionData(currInterventionList);
        // 2.错误的数据访问1，etl_sys_cd不存在
        bodyString = new HttpClient()
                .addData("etl_sys_cd", "zyjgycs")
                .post(getActionUrl("searchJobLevelCurrInterventionByPage"))
                .getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
        assertThat(ar.isSuccess(), is(false));
    }

    @Method(desc = "验证数据的正确性",
            logicStep = "验证数据的正确性")
    private void checkInterventionData(List<Map<String, Object>> currInterventionList) {
        // 验证数据的正确性
        for (Map<String, Object> currIntervention : currInterventionList) {
            String event_id = currIntervention.get("event_id").toString();
            if (event_id.equals(EventId)) {
                assertThat(currIntervention.get("etl_sys_cd"), is(EtlSysCd));
                assertThat(currIntervention.get("hand_status"), is(Meddle_status.FALSE.getCode()));
                assertThat(currIntervention.get("etl_hand_type"), is(Meddle_type.SYS_ORIGINAL.getCode()));
                assertThat(currIntervention.get("etl_job"), is("测试作业1"));
                assertThat(currIntervention.get("subsysname"), is("任务测试1(zygyrwcs)"));
            } else if (event_id.equals(EventId2)) {
                assertThat(currIntervention.get("etl_sys_cd"), is(EtlSysCd));
                assertThat(currIntervention.get("hand_status"), is(Meddle_status.TRUE.getCode()));
                assertThat(currIntervention.get("etl_hand_type"), is(Meddle_type.SYS_PAUSE.getCode()));
                assertThat(currIntervention.get("etl_job"), is("测试作业2"));
                assertThat(currIntervention.get("subsysname"), is("任务测试1(zygyrwcs)"));
            } else if (event_id.equals(EventId3)) {
                assertThat(currIntervention.get("etl_sys_cd"), is(EtlSysCd));
                assertThat(currIntervention.get("hand_status"), is(Meddle_status.DONE.getCode()));
                assertThat(currIntervention.get("etl_hand_type"), is(Meddle_type.SYS_RESUME.getCode()));
                assertThat(currIntervention.get("etl_job"), is("测试作业3"));
                assertThat(currIntervention.get("subsysname"), is("任务测试1(zygyrwcs)"));
            } else if (event_id.equals(EventId4)) {
                assertThat(currIntervention.get("etl_sys_cd"), is(EtlSysCd));
                assertThat(currIntervention.get("hand_status"), is(Meddle_status.RUNNING.getCode()));
                assertThat(currIntervention.get("etl_hand_type"), is(Meddle_type.SYS_SHIFT.getCode()));
                assertThat(currIntervention.get("etl_job"), is("测试作业4"));
                assertThat(currIntervention.get("subsysname"), is("任务测试2(zygyrwcs2)"));
            } else if (event_id.equals(EventId5)) {
                assertThat(currIntervention.get("etl_sys_cd"), is(EtlSysCd));
                assertThat(currIntervention.get("hand_status"), is(Meddle_status.ERROR.getCode()));
                assertThat(currIntervention.get("etl_hand_type"), is(Meddle_type.SYS_STOP.getCode()));
                assertThat(currIntervention.get("etl_job"), is("测试作业5"));
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
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
        assertThat(ar.isSuccess(), is(true));
        Map<Object, Object> map = ar.getDataForMap();
        assertThat(map.get("totalSize").toString(), is(String.valueOf(5)));
        List<Map<String, Object>> handHisList = (List<Map<String, Object>>)
                map.get("handHisList");
        checkInterventionData(handHisList);
        // 2.错误的数据访问1，etl_sys_cd不存在
        bodyString = new HttpClient()
                .addData("etl_sys_cd", "zyjgycs")
                .post(getActionUrl("searchJobLeverHisInterventionByPage"))
                .getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
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
                    .addData("etl_sys_cd", EtlSysCd2)
                    .addData("etl_job", "测试作业7")
                    .addData("etl_hand_type", Meddle_type.JOB_JUMP.getCode())
                    .addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
                    .post(getActionUrl("jobLevelInterventionOperate"))
                    .getBodyString();
            ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(true));
            Etl_job_hand etlJobHand = SqlOperator.queryOneObject(db, Etl_job_hand.class, "select * from "
                            + Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job=? and etl_hand_type=?",
                    EtlSysCd2, "测试作业7", Meddle_type.JOB_JUMP.getCode())
                    .orElseThrow(() -> new BusinessException("sql查询错误！"));
            assertThat(etlJobHand.getEtl_sys_cd(), is(EtlSysCd2));
            assertThat(etlJobHand.getEtl_job(), is("测试作业7"));
            assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_JUMP.getCode()));
            assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
            assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
            assertThat(etlJobHand.getPro_para(), is(EtlSysCd2 + ",测试作业7,"
                    + DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString()));
            // 2.正确的数据访问2，数据都正确,job_priority不为空，干预类型为临时调整优先级
            bodyString = new HttpClient()
                    .addData("etl_sys_cd", EtlSysCd2)
                    .addData("etl_job", "测试作业8")
                    .addData("etl_hand_type", Meddle_type.JOB_PRIORITY.getCode())
                    .addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
                    .addData("job_priority", 99)
                    .post(getActionUrl("jobLevelInterventionOperate"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(true));
            etlJobHand = SqlOperator.queryOneObject(db, Etl_job_hand.class, "select * from "
                            + Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job=? and etl_hand_type=?",
                    EtlSysCd2, "测试作业8", Meddle_type.JOB_PRIORITY.getCode())
                    .orElseThrow(() -> new BusinessException("sql查询错误！"));
            assertThat(etlJobHand.getEtl_sys_cd(), is(EtlSysCd2));
            assertThat(etlJobHand.getEtl_job(), is("测试作业8"));
            assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_PRIORITY.getCode()));
            assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
            assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
            assertThat(etlJobHand.getPro_para(), is(EtlSysCd2 + ",测试作业8,"
                    + DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString() + ",99"));
            // 3.正确的数据访问3，干预类型为重跑
            bodyString = new HttpClient()
                    .addData("etl_sys_cd", EtlSysCd2)
                    .addData("etl_job", "测试作业9")
                    .addData("etl_hand_type", Meddle_type.JOB_RERUN.getCode())
                    .addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
                    .post(getActionUrl("jobLevelInterventionOperate"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(true));
            etlJobHand = SqlOperator.queryOneObject(db, Etl_job_hand.class, "select * from "
                            + Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job=? and etl_hand_type=?",
                    EtlSysCd2, "测试作业9", Meddle_type.JOB_RERUN.getCode())
                    .orElseThrow(() -> new BusinessException("sql查询错误！"));
            assertThat(etlJobHand.getEtl_sys_cd(), is(EtlSysCd2));
            assertThat(etlJobHand.getEtl_job(), is("测试作业9"));
            assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_RERUN.getCode()));
            assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
            assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
            assertThat(etlJobHand.getPro_para(), is(EtlSysCd2 + ",测试作业9,"
                    + DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString()));
            // 4.正确的数据访问4，干预类型为停止
            bodyString = new HttpClient()
                    .addData("etl_sys_cd", EtlSysCd2)
                    .addData("etl_job", "测试作业11")
                    .addData("etl_hand_type", Meddle_type.JOB_STOP.getCode())
                    .addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
                    .post(getActionUrl("jobLevelInterventionOperate"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(true));
            etlJobHand = SqlOperator.queryOneObject(db, Etl_job_hand.class, "select * from "
                            + Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job=? and etl_hand_type=?",
                    EtlSysCd2, "测试作业11", Meddle_type.JOB_STOP.getCode())
                    .orElseThrow(() -> new BusinessException("sql查询错误！"));
            assertThat(etlJobHand.getEtl_sys_cd(), is(EtlSysCd2));
            assertThat(etlJobHand.getEtl_job(), is("测试作业11"));
            assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_STOP.getCode()));
            assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
            assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
            assertThat(etlJobHand.getPro_para(), is(EtlSysCd2 + ",测试作业11,"
                    + DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString()));
            // 5.正确的数据访问5，干预类型为强制执行
            bodyString = new HttpClient()
                    .addData("etl_sys_cd", EtlSysCd2)
                    .addData("etl_job", "测试作业10")
                    .addData("etl_hand_type", Meddle_type.JOB_TRIGGER.getCode())
                    .addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
                    .post(getActionUrl("jobLevelInterventionOperate"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(true));
            etlJobHand = SqlOperator.queryOneObject(db, Etl_job_hand.class, "select * from "
                            + Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job=? and etl_hand_type=?",
                    EtlSysCd2, "测试作业10", Meddle_type.JOB_TRIGGER.getCode())
                    .orElseThrow(() -> new BusinessException("sql查询错误！"));
            assertThat(etlJobHand.getEtl_sys_cd(), is(EtlSysCd2));
            assertThat(etlJobHand.getEtl_job(), is("测试作业10"));
            assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_TRIGGER.getCode()));
            assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
            assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
            assertThat(etlJobHand.getPro_para(), is(EtlSysCd2 + ",测试作业10,"
                    + DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString()));
            // 6.错误的数据访问1，etl_sys_cd不存在
            bodyString = new HttpClient()
                    .addData("etl_sys_cd", "zyjgycs")
                    .addData("etl_job", "测试作业10")
                    .addData("etl_hand_type", Meddle_type.JOB_TRIGGER.getCode())
                    .addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
                    .post(getActionUrl("jobLevelInterventionOperate"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(false));
            // 7.错误的数据访问2，工程下有作业正在干预
            bodyString = new HttpClient()
                    .addData("etl_sys_cd", EtlSysCd)
                    .addData("etl_job", "测试作业1")
                    .addData("etl_hand_type", Meddle_type.JOB_TRIGGER.getCode())
                    .addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
                    .post(getActionUrl("jobLevelInterventionOperate"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(false));
            // 8.错误的数据访问3，作业状态为完成或错误或停止的不可以停止
            bodyString = new HttpClient()
                    .addData("etl_sys_cd", EtlSysCd2)
                    .addData("etl_job", "测试作业12")
                    .addData("etl_hand_type", Meddle_type.JOB_STOP.getCode())
                    .addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
                    .post(getActionUrl("jobLevelInterventionOperate"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(false));
            // 9.错误的数据访问4，作业状态为运行或完成的不可以跳过
            bodyString = new HttpClient()
                    .addData("etl_sys_cd", EtlSysCd2)
                    .addData("etl_job", "测试作业13")
                    .addData("etl_hand_type", Meddle_type.JOB_JUMP.getCode())
                    .addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
                    .post(getActionUrl("jobLevelInterventionOperate"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(false));
            // 10.错误的数据访问5，作业状态为挂起或运行或等待的不可以重跑
            bodyString = new HttpClient()
                    .addData("etl_sys_cd", EtlSysCd2)
                    .addData("etl_job", "测试作业14")
                    .addData("etl_hand_type", Meddle_type.JOB_RERUN.getCode())
                    .addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
                    .post(getActionUrl("jobLevelInterventionOperate"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(false));
            // 11.错误的数据访问6，作业状态为完成、错误、运行、停止不可以强制执行
            bodyString = new HttpClient()
                    .addData("etl_sys_cd", EtlSysCd2)
                    .addData("etl_job", "测试作业13")
                    .addData("etl_hand_type", Meddle_type.JOB_TRIGGER.getCode())
                    .addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
                    .post(getActionUrl("jobLevelInterventionOperate"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(false));
            // 12.错误的数据访问7，作业状态为运行不可以临时调整优先级
            bodyString = new HttpClient()
                    .addData("etl_sys_cd", EtlSysCd2)
                    .addData("etl_job", "测试作业15")
                    .addData("etl_hand_type", Meddle_type.JOB_PRIORITY.getCode())
                    .addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString())
                    .post(getActionUrl("jobLevelInterventionOperate"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
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
            List<Map<String, String>> list = getEtlJobList(new String[]{"测试作业8", "测试作业10"});
            // 1.正确的数据访问1.数据都正确,干预类型为强制执行
            String bodyString = new HttpClient()
                    .addData("etl_sys_cd", EtlSysCd2)
                    .addData("batchEtlJob", JsonUtil.toJson(list))
                    .addData("etl_hand_type", Meddle_type.JOB_TRIGGER.getCode())
                    .post(getActionUrl("batchJobLevelInterventionOperate"))
                    .getBodyString();
            ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(true));
            List<Etl_job_hand> jobHandList = SqlOperator.queryList(db, Etl_job_hand.class, "select * from "
                            + Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job in(?,?) " +
                            " and etl_hand_type=?", EtlSysCd2, "测试作业10", "测试作业8",
                    Meddle_type.JOB_TRIGGER.getCode());
            assertThat(jobHandList.size(), is(2));
            for (Etl_job_hand etlJobHand : jobHandList) {
                assertThat(etlJobHand.getEtl_sys_cd(), is(EtlSysCd2));
                assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_TRIGGER.getCode()));
                assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
                assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
                if (etlJobHand.getEtl_job().equals("测试作业10")) {
                    assertThat(etlJobHand.getPro_para(), is(EtlSysCd2 + ",测试作业10,"
                            + DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString()));
                } else if (etlJobHand.getEtl_job().equals("测试作业8")) {
                    assertThat(etlJobHand.getPro_para(), is(EtlSysCd2 + ",测试作业8,"
                            + DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString()));
                }
            }
            // 2.正确的数据访问2，干预类型为停止
            list = getEtlJobList(new String[]{"测试作业11", "测试作业13"});
            bodyString = new HttpClient()
                    .addData("etl_sys_cd", EtlSysCd2)
                    .addData("batchEtlJob", JsonUtil.toJson(list))
                    .addData("etl_hand_type", Meddle_type.JOB_STOP.getCode())
                    .post(getActionUrl("batchJobLevelInterventionOperate"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(true));
            jobHandList = SqlOperator.queryList(db, Etl_job_hand.class, "select * from "
                            + Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job in(?,?) " +
                            " and etl_hand_type=?", EtlSysCd2, "测试作业11", "测试作业13",
                    Meddle_type.JOB_STOP.getCode());
            assertThat(jobHandList.size(), is(2));
            for (Etl_job_hand etlJobHand : jobHandList) {
                assertThat(etlJobHand.getEtl_sys_cd(), is(EtlSysCd2));
                assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_STOP.getCode()));
                assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
                assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
                if (etlJobHand.getEtl_job().equals("测试作业11")) {
                    assertThat(etlJobHand.getPro_para(), is(EtlSysCd2 + ",测试作业11,"
                            + DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString()));
                } else if (etlJobHand.getEtl_job().equals("测试作业13")) {
                    assertThat(etlJobHand.getPro_para(), is(EtlSysCd2 + ",测试作业13,"
                            + DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString()));
                }
            }
            // 3.正确的数据访问3，干预类型为跳过
            list = getEtlJobList(new String[]{"测试作业7", "测试作业9"});
            bodyString = new HttpClient()
                    .addData("etl_sys_cd", EtlSysCd2)
                    .addData("batchEtlJob", JsonUtil.toJson(list))
                    .addData("etl_hand_type", Meddle_type.JOB_JUMP.getCode())
                    .post(getActionUrl("batchJobLevelInterventionOperate"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(true));
            jobHandList = SqlOperator.queryList(db, Etl_job_hand.class, "select * from "
                            + Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job in(?,?) " +
                            " and etl_hand_type=?", EtlSysCd2, "测试作业7", "测试作业9",
                    Meddle_type.JOB_JUMP.getCode());
            assertThat(jobHandList.size(), is(2));
            for (Etl_job_hand etlJobHand : jobHandList) {
                assertThat(etlJobHand.getEtl_sys_cd(), is(EtlSysCd2));
                assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_JUMP.getCode()));
                assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
                assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
                if (etlJobHand.getEtl_job().equals("测试作业7")) {
                    assertThat(etlJobHand.getPro_para(), is(EtlSysCd2 + ",测试作业7,"
                            + DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString()));
                } else if (etlJobHand.getEtl_job().equals("测试作业9")) {
                    assertThat(etlJobHand.getPro_para(), is(EtlSysCd2 + ",测试作业9,"
                            + DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString()));
                }
            }
            // 4.正确的数据访问4，干预类型为重跑
            list = getEtlJobList(new String[]{"测试作业16", "测试作业17"});
            bodyString = new HttpClient()
                    .addData("etl_sys_cd", EtlSysCd2)
                    .addData("batchEtlJob", JsonUtil.toJson(list))
                    .addData("etl_hand_type", Meddle_type.JOB_RERUN.getCode())
                    .post(getActionUrl("batchJobLevelInterventionOperate"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(true));
            jobHandList = SqlOperator.queryList(db, Etl_job_hand.class, "select * from "
                            + Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job in(?,?) " +
                            " and etl_hand_type=?", EtlSysCd2, "测试作业16", "测试作业17",
                    Meddle_type.JOB_RERUN.getCode());
            assertThat(jobHandList.size(), is(2));
            for (Etl_job_hand etlJobHand : jobHandList) {
                assertThat(etlJobHand.getEtl_sys_cd(), is(EtlSysCd2));
                assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_RERUN.getCode()));
                assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
                assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
                if (etlJobHand.getEtl_job().equals("测试作业16")) {
                    assertThat(etlJobHand.getPro_para(), is(EtlSysCd2 + ",测试作业16,"
                            + DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString()));
                } else if (etlJobHand.getEtl_job().equals("测试作业17")) {
                    assertThat(etlJobHand.getPro_para(), is(EtlSysCd2 + ",测试作业17,"
                            + DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString()));
                }
            }
            // 5.正确的数据访问5，干预类型为临时调整优先级
            list = getEtlJobList(new String[]{"测试作业18", "测试作业19"});
            bodyString = new HttpClient()
                    .addData("etl_sys_cd", EtlSysCd2)
                    .addData("batchEtlJob", JsonUtil.toJson(list))
                    .addData("etl_hand_type", Meddle_type.JOB_PRIORITY.getCode())
                    .addData("job_priority", 99)
                    .post(getActionUrl("batchJobLevelInterventionOperate"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(true));
            jobHandList = SqlOperator.queryList(db, Etl_job_hand.class, "select * from "
                            + Etl_job_hand.TableName + " where etl_sys_cd=? and etl_job in(?,?) " +
                            " and etl_hand_type=?", EtlSysCd2, "测试作业18", "测试作业19",
                    Meddle_type.JOB_PRIORITY.getCode());
            assertThat(jobHandList.size(), is(2));
            for (Etl_job_hand etlJobHand : jobHandList) {
                assertThat(etlJobHand.getEtl_sys_cd(), is(EtlSysCd2));
                assertThat(etlJobHand.getEtl_hand_type(), is(Meddle_type.JOB_PRIORITY.getCode()));
                assertThat(etlJobHand.getHand_status(), is(Meddle_status.TRUE.getCode()));
                assertThat(etlJobHand.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
                if (etlJobHand.getEtl_job().equals("测试作业18")) {
                    assertThat(etlJobHand.getPro_para(), is(EtlSysCd2 + ",测试作业18,"
                            + DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString() + ",99"));
                } else if (etlJobHand.getEtl_job().equals("测试作业19")) {
                    assertThat(etlJobHand.getPro_para(), is(EtlSysCd2 + ",测试作业19,"
                            + DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString() + ",99"));
                }
            }
            // 6.错误的数据访问1，etl_sys_cd不存在
            list = getEtlJobList(new String[]{"测试作业1", "测试作业2"});
            bodyString = new HttpClient()
                    .addData("etl_sys_cd", "zyjgycs")
                    .addData("batchEtlJob", JsonUtil.toJson(list))
                    .addData("etl_hand_type", Meddle_type.JOB_TRIGGER.getCode())
                    .post(getActionUrl("batchJobLevelInterventionOperate"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(false));
            // 7.错误的数据访问2，工程下有作业正在干预
            list = getEtlJobList(new String[]{"测试作业2", "测试作业3"});
            bodyString = new HttpClient()
                    .addData("etl_sys_cd", EtlSysCd)
                    .addData("batchEtlJob", JsonUtil.toJson(list))
                    .addData("etl_hand_type", Meddle_type.JOB_TRIGGER.getCode())
                    .post(getActionUrl("batchJobLevelInterventionOperate"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                    .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
            assertThat(ar.isSuccess(), is(false));
        }
    }

    @Method(desc = "获取存放etl_job,curr_bath_date为key,对应值为value的集合",
            logicStep = "封装etl_job,curr_bath_date的值")
    @Param(name = "batchEtlJob", desc = "存放作业名称的数组", range = "无限制")
    @Return(desc = "返回封装etl_job,curr_bath_date的集合", range = "无限制")
    private List<Map<String, String>> getEtlJobList(String[] batchEtlJob) {
        // 封装etl_job,curr_bath_date的值
        List<Map<String, String>> list = new ArrayList<>();
        for (String etl_job : batchEtlJob) {
            Map<String, String> map = new HashMap<>();
            map.put("etl_job", etl_job);
            map.put("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString());
            list.add(map);
        }
        return list;
    }

}
