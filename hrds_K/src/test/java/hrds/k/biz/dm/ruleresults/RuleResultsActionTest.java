package hrds.k.biz.dm.ruleresults;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Dq_definition;
import hrds.commons.entity.Dq_result;
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RuleResultsActionTest extends WebBaseTestCase {

    //测试数据的用户ID
    private static final long USER_ID = -1000L;
    //测试数据的部门ID
    private static final long DEP_ID = -1000L;
    //测试规则配置ID
    private static final long REG_NUM = -1000L;

    @Method(desc = "初始化测试用例依赖表数据", logicStep = "初始化测试用例依赖表数据")
    @BeforeClass
    public static void before() {
        DatabaseWrapper db = new DatabaseWrapper();
        try {
            //初始化 Sys_user 数据
            Sys_user sysUser = new Sys_user();
            sysUser.setUser_id(USER_ID);
            sysUser.setCreate_id(1000L);
            sysUser.setDep_id(DEP_ID);
            sysUser.setRole_id(1001L);
            sysUser.setUser_name("hll");
            sysUser.setUser_password("111111");
            sysUser.setUseris_admin("0");
            sysUser.setUser_type("02");
            sysUser.setUsertype_group("37");
            sysUser.setUser_state("0");
            sysUser.setCreate_date(DateUtil.getSysDate());
            sysUser.setToken("0");
            sysUser.setValid_time(DateUtil.getSysTime());
            sysUser.add(db);
            //初始化 Department_info 数据
            Department_info departmentInfo = new Department_info();
            departmentInfo.setDep_id(DEP_ID);
            departmentInfo.setDep_name("hll");
            departmentInfo.setCreate_date(DateUtil.getSysDate());
            departmentInfo.setCreate_time(DateUtil.getSysTime());
            departmentInfo.add(db);
            //初始化 Dq_definition
            Dq_definition dq_definition = new Dq_definition();
            dq_definition.setReg_num(REG_NUM);
            dq_definition.setReg_name("hll_测试规则配置信息");
            dq_definition.setApp_updt_dt(DateUtil.getSysDate());
            dq_definition.setApp_updt_ti(DateUtil.getSysTime());
            dq_definition.setIs_saveindex1("1");
            dq_definition.setIs_saveindex2("1");
            dq_definition.setIs_saveindex3("1");
            dq_definition.setCase_type("SQL");
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            dq_definition.setReg_num(-1001L);
            dq_definition.setReg_name("hll_测试检索规则名称");
            dq_definition.setApp_updt_dt(DateUtil.getSysDate());
            dq_definition.setApp_updt_ti(DateUtil.getSysTime());
            dq_definition.setRule_src("hll_测试检索规则来源");
            dq_definition.setTarget_tab("hll_测试检索规则标签");
            dq_definition.setIs_saveindex1("1");
            dq_definition.setIs_saveindex2("1");
            dq_definition.setIs_saveindex3("1");
            dq_definition.setCase_type("SQL");
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            //初始化 Dq_result
            Dq_result dq_result = new Dq_result();
            dq_result.setTask_id(-1001L);
            dq_result.setIs_saveindex1("1");
            dq_result.setIs_saveindex2("1");
            dq_result.setIs_saveindex3("1");
            dq_result.setCase_type("SQL");
            dq_result.setReg_num(REG_NUM);
            dq_result.add(db);
            dq_result.setTask_id(-1002L);
            dq_result.setIs_saveindex1("1");
            dq_result.setIs_saveindex2("1");
            dq_result.setIs_saveindex3("1");
            dq_result.setCase_type("SQL");
            dq_result.setVerify_date("20200202");
            dq_result.setStart_date("20200202");
            dq_result.setExec_mode("MAN");
            dq_result.setVerify_result("0");
            dq_result.add(db);
            //提交所有数据库执行操作
            SqlOperator.commitTransaction(db);
            //根据初始化的 Sys_user 用户模拟登陆
            String bodyString = new HttpClient()
                    .addData("user_id", USER_ID)
                    .addData("password", "111111")
                    .post("http://127.0.0.1:8888/A/action/hrds/a/biz/login/login").getBodyString();
            JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                    assertThat(ar.isSuccess(), is(true)));
        } catch (Exception e) {
            db.rollback();
        } finally {
            db.close();
        }
    }

    @Method(desc = "测试案例执行完成后清理测试数据", logicStep = "测试案例执行完成后清理测试数据")
    @AfterClass
    public static void after() {
        DatabaseWrapper db = new DatabaseWrapper();
        try {
            long num;
            //删除 Sys_user 表测试数据
            SqlOperator.execute(db, "delete from " + Sys_user.TableName + " where user_id=?", USER_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Sys_user.TableName +
                    " where user_id =?", USER_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("sys_user 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Department_info 表测试数据
            SqlOperator.execute(db, "delete from " + Department_info.TableName + " where dep_id=?", DEP_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Department_info.TableName +
                    " where dep_id =?", DEP_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("department_info 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Dq_definition 表测试数据
            SqlOperator.execute(db, "delete from " + Dq_definition.TableName + " where user_id=?", USER_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dq_definition.TableName +
                    " where user_id =?", USER_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_definition 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Dq_result 表测试数据
            SqlOperator.execute(db, "delete from " + Dq_result.TableName + " where task_id=?", -1001L);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dq_result.TableName +
                    " where task_id=?", -1001L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_result 表此条数据删除后,记录数应该为0", num, is(0L));
            SqlOperator.execute(db, "delete from " + Dq_result.TableName + " where task_id=?", -1002L);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dq_result.TableName +
                    " where task_id=?", -1002L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_result 表此条数据删除后,记录数应该为0", num, is(0L));
        } catch (Exception e) {
            db.rollback();
        } finally {
            db.close();
        }
    }

    @Method(desc = "获取规则检查结果信息", logicStep = "获取规则检查结果信息")
    @Test
    public void getRuleResultInfos() {
        String bodyString;
        ActionResult ar;
        bodyString = new HttpClient()
                .post(getActionUrl("getRuleResultInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("totalSize"), is(2));
    }

    @Method(desc = "检索规则结果信息", logicStep = "检索规则结果信息")
    @Test
    public void searchRuleResultInfos() {
        String bodyString;
        ActionResult ar;
        //任务id
        bodyString = new HttpClient()
                .post(getActionUrl("searchRuleResultInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(false));
        //检查日期
        bodyString = new HttpClient()
                .addData("verify_date", "20200202")
                .post(getActionUrl("searchRuleResultInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        //TODO 根据其他条件搜索

    }

    @Method(desc = "规则执行详细信息", logicStep = "规则执行详细信息")
    @Test
    public void getRuleDetectDetail() {
        String bodyString;
        ActionResult ar;
        bodyString = new HttpClient()
                .addData("task_id", -1001L)
                .post(getActionUrl("getRuleDetectDetail")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("reg_num"), is(-1000));
        assertThat(ar.getDataForMap().get("case_type"), is("SQL"));
    }

    @Test
    public void getRuleExecuteHistoryInfo() {
    }
}