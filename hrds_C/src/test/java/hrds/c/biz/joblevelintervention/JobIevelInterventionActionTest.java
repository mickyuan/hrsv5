package hrds.c.biz.joblevelintervention;

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

    private static final String EventId = "xtgysjh";
    private static final String EventId2 = "xtgysjh2";
    private static final String EventId3 = "xtgysjh3";
    private static final String EventId4 = "xtgysjh4";
    private static final String EventId5 = "xtgysjh5";
    private static final String EventId6 = "xtgysjh6";

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
            for (int i = 1; i < 7; i++) {
                Etl_job_cur etl_job_cur = new Etl_job_cur();
                etl_job_cur.setEtl_sys_cd(EtlSysCd);
                etl_job_cur.setEtl_job("测试作业"+i);
                etl_job_cur.setPro_dic("/home/hyshf/dhw");
                etl_job_cur.setEtl_job_desc("测试作业定义" + i);
                etl_job_cur.setPro_para("1");
                etl_job_cur.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
                etl_job_cur.setToday_disp(Today_Dispatch_Flag.YES.getCode());
                etl_job_cur.setCurr_bath_date(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()).toString());
                switch (i) {
                    case 1:
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
                        break;
                    case 2:
                        etl_job_cur.setPro_name("zy.class");
                        etl_job_cur.setPro_type(Pro_Type.JAVA.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd);
                        etl_job_cur.setDisp_type(Dispatch_Type.TPLUS1.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.DONE.getCode());
                        break;
                    case 3:
                        etl_job_cur.setPro_name("zy.shell");
                        etl_job_cur.setPro_type(Pro_Type.SHELL.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.RUNNING.getCode());
                        break;
                    case 4:
                        etl_job_cur.setPro_name("zy.py");
                        etl_job_cur.setPro_type(Pro_Type.PYTHON.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd2);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.WEEKLY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.WAITING.getCode());
                        break;
                    case 5:
                        etl_job_cur.setPro_name("zy.bat");
                        etl_job_cur.setPro_type(Pro_Type.BAT.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd2);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.MONTHLY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.PENDING.getCode());
                        break;
                    case 6:
                        etl_job_cur.setPro_name("zy.bat");
                        etl_job_cur.setPro_type(Pro_Type.BAT.getCode());
                        etl_job_cur.setSub_sys_cd(SubSysCd2);
                        etl_job_cur.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
                        etl_job_cur.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
                        etl_job_cur.setJob_disp_status(Job_Status.STOP.getCode());
                        break;
                }
                num = etl_job_cur.add(db);
                assertThat("测试数据etl_job_cur初始化", num, is(1));
            }
            // 6.构造etl_job_hand表测试数据
            Etl_job_hand etl_job_hand = new Etl_job_hand();
            for (int i = 1; i < 7; i++) {
                etl_job_hand.setEtl_sys_cd(EtlSysCd);
                etl_job_hand.setEtl_job("[NOTHING]");
                etl_job_hand.setMain_serv_sync(Main_Server_Sync.YES.getCode());
                switch (i) {
                    case 1:
                        etl_job_hand.setHand_status(Meddle_status.FALSE.getCode());
                        etl_job_hand.setEvent_id(EventId);
                        etl_job_hand.setEtl_hand_type(Meddle_type.SYS_ORIGINAL.getCode());
                        break;
                    case 2:
                        etl_job_hand.setEvent_id(EventId2);
                        etl_job_hand.setHand_status(Meddle_status.TRUE.getCode());
                        etl_job_hand.setEtl_hand_type(Meddle_type.SYS_PAUSE.getCode());
                        break;
                    case 3:
                        etl_job_hand.setHand_status(Meddle_status.DONE.getCode());
                        etl_job_hand.setEvent_id(EventId3);
                        etl_job_hand.setEtl_hand_type(Meddle_type.SYS_RESUME.getCode());
                        break;
                    case 4:
                        etl_job_hand.setHand_status(Meddle_status.RUNNING.getCode());
                        etl_job_hand.setEvent_id(EventId4);
                        etl_job_hand.setEtl_hand_type(Meddle_type.SYS_SHIFT.getCode());
                        break;
                    case 5:
                        etl_job_hand.setHand_status(Meddle_status.ERROR.getCode());
                        etl_job_hand.setEvent_id(EventId5);
                        etl_job_hand.setEtl_hand_type(Meddle_type.SYS_STOP.getCode());
                        break;
                    case 6:
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
                etl_job_hand_his.setEtl_job("[NOTHING]");
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
                .addData("user_id", "2001")
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
            // 8.提交事务
            SqlOperator.commitTransaction(db);
        }
    }

    @Test
    public void searchJobLevelIntervention(){
        String bodyString = new HttpClient()
                .addData("etl_sys_cd", EtlSysCd2)
                .post(getActionUrl("searchJobLevelIntervention"))
                .getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
        assertThat(ar.isSuccess(), is(true));
    }
    @Test
    public void searchJobLevelCurrInterventionByPage(){
        String bodyString = new HttpClient()
                .addData("etl_sys_cd", EtlSysCd2)
                .post(getActionUrl("searchJobLevelCurrInterventionByPage"))
                .getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
        assertThat(ar.isSuccess(), is(true));
    }
    @Test
    public void searchJobLeverHisInterventionByPage(){
        String bodyString = new HttpClient()
                .addData("etl_sys_cd", EtlSysCd2)
                .post(getActionUrl("searchJobLeverHisInterventionByPage"))
                .getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
        assertThat(ar.isSuccess(), is(true));
    }
    @Test
    public void jobLevelInterventionOperate(){
        String bodyString = new HttpClient()
                .addData("etl_sys_cd", EtlSysCd2)
                .post(getActionUrl("jobLevelInterventionOperate"))
                .getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
        assertThat(ar.isSuccess(), is(true));
    }

    @Test
    public void batchJobLevelInterventionOperate(){
        List<Map<String,String> > list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Map<String,String> map = new HashMap<>();
            if (i==0) {
                map.put("etl_job","nantong");
                map.put("curr_bath_date","2019-12-12");
            }else {
                map.put("etl_job","JOJO");
                map.put("curr_bath_date","2019-12-12");
            }
            list.add(map);
        }
        String bodyString = new HttpClient()
                .addData("etl_sys_cd", "A1")
                .addData("batchEtlJob", JsonUtil.toJson(list))
                .addData("etl_hand_type", Meddle_type.JOB_TRIGGER.getCode())
                .addData("job_priority", 0)
                .post(getActionUrl("batchJobLevelInterventionOperate"))
                .getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
                .orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
        assertThat(ar.isSuccess(), is(true));
    }

}
