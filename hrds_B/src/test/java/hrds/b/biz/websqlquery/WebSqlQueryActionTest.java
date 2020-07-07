package hrds.b.biz.websqlquery;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class WebSqlQueryActionTest extends WebBaseTestCase {

    //当前线程的id
    private static long THREAD_ID = Thread.currentThread().getId() * 1000000;
    //获取模拟登陆的URL
    private static final String LOGIN_URL = agentInitConfig.getString("login_url",
            "http://127.0.0.1:8888/A/action/hrds/a/biz/login/login");
    //登录用户id
    private static final long USER_ID = agentInitConfig.getLong("general_oper_user_id", 2001);
    //登录用户密码
    private static final String PASSWORD = agentInitConfig.getString("general_password", "1");

    @Method(desc = "初始化测试用例依赖表数据", logicStep = "初始化测试用例依赖表数据")
    @BeforeClass
    public static void before() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //加载通用测试数据
            //提交数据库操作
            db.commit();
            //模拟登陆
            String bodyString = new HttpClient().buildSession()
                    .addData("user_id", USER_ID)
                    .addData("password", PASSWORD)
                    .post(LOGIN_URL).getBodyString();
            JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                    assertThat(ar.isSuccess(), is(true)));
        }
    }

    @Method(desc = "测试案例执行完成后清理测试数据", logicStep = "清理测试数据")
    @AfterClass
    public static void after() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //清理通用测试数据
            //提交数据库操作
            db.commit();
        }
    }

    @Test
    public void queryDataBasedOnTableName() {
        //查询数据存储层数据
        String bodyString;
        ActionResult ar;
        //1.获取登录用户所在部门包含文件采集任务的数据源信息,有返回结果 检查返回结果是否是预期值
        bodyString = new HttpClient()
                .addData("tableName", "warehouse")
                .post(getActionUrl("queryDataBasedOnTableName")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
        //2.查询表名不存在
        bodyString = new HttpClient()
                .addData("tableName", "not_warehouse")
                .post(getActionUrl("queryDataBasedOnTableName")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(false));
    }

    @Test
    public void queryDataBasedOnSql() {
        //查询数据存储层数据
        String bodyString;
        ActionResult ar;
        //1.获取登录用户所在部门包含文件采集任务的数据源信息,有返回结果 检查返回结果是否是预期值
        bodyString = new HttpClient()
                .addData("querySQL", "select * from warehouse")
                .post(getActionUrl("queryDataBasedOnSql")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
        //2.查询sql表名不存在
        bodyString = new HttpClient()
                .addData("querySQL", "select * from not_warehouse")
                .post(getActionUrl("queryDataBasedOnSql")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(false));
    }
}
