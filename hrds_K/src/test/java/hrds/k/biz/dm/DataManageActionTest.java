package hrds.k.biz.dm;


import fd.ng.core.annotation.Method;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.entity.Dq_result;
import hrds.commons.exception.BusinessException;
import hrds.testbase.LoadGeneralTestData;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DataManageActionTest extends WebBaseTestCase {

    //当前线程的id
    private static long THREAD_ID = Thread.currentThread().getId() * 1000000;
    //获取模拟登陆的URL
    private static final String LOGIN_URL = agentInitConfig.getString("login_url");
    //登录用户id
    private static final long USER_ID = agentInitConfig.getLong("general_oper_user_id");
    //登录用户密码
    private static final long PASSWORD = agentInitConfig.getLong("general_password");
    //初始化加载通用测试数据
    private static final LoadGeneralTestData loadGeneralTestData = new LoadGeneralTestData(THREAD_ID);

    @Method(desc = "初始化测试用例依赖表数据", logicStep = "初始化测试用例依赖表数据")
    @BeforeClass
    public static void before() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //初始化通用测试数据
            loadGeneralTestData.execute(db);
            //初始化 Dq_result
            Dq_result dq_result = new Dq_result();
            dq_result.setTask_id(-1000L);
            dq_result.setVerify_result("0");
            dq_result.setIs_saveindex1("1");
            dq_result.setIs_saveindex2("1");
            dq_result.setIs_saveindex3("1");
            dq_result.setCase_type("SQL");
            dq_result.setReg_num(-1000L);
            dq_result.add(db);
            //提交所有数据库执行操作
            SqlOperator.commitTransaction(db);
            //根据初始化的 Sys_user 用户模拟登陆
            String bodyString = new HttpClient().buildSession()
                    .addData("user_id", USER_ID)
                    .addData("password", PASSWORD)
                    .post(LOGIN_URL).getBodyString();
            ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("模拟登陆失败!"));
            assertThat(ar.isSuccess(), is(true));
        }
    }

    @Method(desc = "测试案例执行完成后清理测试数据",
            logicStep = "测试案例执行完成后清理测试数据")
    @AfterClass
    public static void after() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //清理通用数据
            loadGeneralTestData.cleanUp(db);
            //删除 Dq_result 表测试数据
            SqlOperator.execute(db, "delete from " + Dq_result.TableName + " where reg_num=?", -1000L);
            long num = SqlOperator.queryNumber(db, "select count(1) from " + Dq_result.TableName + " where reg_num=?",
                    -1000L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_result 表此条数据删除后,记录数应该为0", num, is(0L));
            //提交数据库操作
            SqlOperator.commitTransaction(db);
        }
    }

    @Method(desc = "获取表统计信息", logicStep = "获取表统计信息")
    @Test
    public void getTableStatistics() {
        String bodyString = new HttpClient()
                .addData("statistics_layer_num", "3")
                .post(getActionUrl("getTableStatistics")).getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取数据管控首页表统计信息失败!"));
        //无法根据数据进行结果校验,因为统计的数据表存在其他数据
        assertThat(ar.isSuccess(), is(true));
    }

    @Method(desc = "获取各种规则统计结果", logicStep = "获取各种规则统计结果")
    @Test
    public void getRuleStatistics() {
        String bodyString = new HttpClient()
                .post(getActionUrl("getRuleStatistics")).getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取数据管控首页各种规则统计信息失败!"));
        //无法根据数据进行结果校验,因为统计的数据表存在其他数据
        assertThat(ar.isSuccess(), is(true));
    }
}