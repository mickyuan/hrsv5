package hrds.k.biz.dm.variableconfig;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.entity.Dq_sys_cfg;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class VariableConfigActionTest extends WebBaseTestCase {

    //当前线程的id
    private static long THREAD_ID = Thread.currentThread().getId() * 1000000;
    //获取模拟登陆的URL
    private static final String LOGIN_URL = agentInitConfig.getString("login_url");
    //登录用户id
    private static final long USER_ID = agentInitConfig.getLong("general_oper_user_id");
    //登录用户密码
    private static final long PASSWORD = agentInitConfig.getLong("general_password");

    @Method(desc = "初始化测试用例依赖表数据", logicStep = "初始化测试用例依赖表数据")
    @BeforeClass
    public static void before() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //初始化 Dq_sys_cfg
            Dq_sys_cfg dq_sys_cfg = new Dq_sys_cfg();
            dq_sys_cfg.setSys_var_id(-1001L + THREAD_ID);
            dq_sys_cfg.setVar_name("hll_测试删除变量名" + THREAD_ID);
            dq_sys_cfg.setVar_value("hll_测试删除变量值" + THREAD_ID);
            dq_sys_cfg.setApp_updt_dt("20190101");
            dq_sys_cfg.setApp_updt_ti(DateUtil.getSysTime());
            dq_sys_cfg.setUser_id(USER_ID);
            dq_sys_cfg.add(db);
            dq_sys_cfg.setSys_var_id("-1002" + THREAD_ID);
            dq_sys_cfg.setVar_name("hll_测试修改变量名" + THREAD_ID);
            dq_sys_cfg.setVar_value("hll_测试修改变量值" + THREAD_ID);
            dq_sys_cfg.setApp_updt_dt("20190101");
            dq_sys_cfg.setApp_updt_ti(DateUtil.getSysTime());
            dq_sys_cfg.setUser_id(USER_ID);
            dq_sys_cfg.add(db);
            dq_sys_cfg.setSys_var_id("-1003" + THREAD_ID);
            dq_sys_cfg.setVar_name("hll_测试查询变量名" + THREAD_ID);
            dq_sys_cfg.setVar_value("hll_测试查询变量值" + THREAD_ID);
            dq_sys_cfg.setApp_updt_dt("20190101");
            dq_sys_cfg.setApp_updt_ti(DateUtil.getSysTime());
            dq_sys_cfg.setUser_id(USER_ID);
            dq_sys_cfg.add(db);
            dq_sys_cfg.setSys_var_id("-1014" + THREAD_ID);
            dq_sys_cfg.setVar_name("hll_测试检索变量名_1014" + THREAD_ID);
            dq_sys_cfg.setVar_value("hll_测试检索变量名_1014" + THREAD_ID);
            dq_sys_cfg.setApp_updt_dt("20190101");
            dq_sys_cfg.setApp_updt_ti(DateUtil.getSysTime());
            dq_sys_cfg.setUser_id(USER_ID);
            dq_sys_cfg.add(db);
            dq_sys_cfg.setSys_var_id("-1024" + THREAD_ID);
            dq_sys_cfg.setVar_name("hll_测试检索变量名_1024" + THREAD_ID);
            dq_sys_cfg.setVar_value("hll_测试检索变量名_1024" + THREAD_ID);
            dq_sys_cfg.setApp_updt_dt("20190101");
            dq_sys_cfg.setApp_updt_ti(DateUtil.getSysTime());
            dq_sys_cfg.setUser_id(USER_ID);
            dq_sys_cfg.add(db);
            dq_sys_cfg.setSys_var_id("-1034" + THREAD_ID);
            dq_sys_cfg.setVar_name("hll_测试检索变量值_1034" + THREAD_ID);
            dq_sys_cfg.setVar_value("hll_测试检索变量值_1034" + THREAD_ID);
            dq_sys_cfg.setApp_updt_dt("20190101");
            dq_sys_cfg.setApp_updt_ti(DateUtil.getSysTime());
            dq_sys_cfg.setUser_id(USER_ID);
            dq_sys_cfg.add(db);
            dq_sys_cfg.setSys_var_id("-1044" + THREAD_ID);
            dq_sys_cfg.setVar_name("hll_测试检索开始时间_1044" + THREAD_ID);
            dq_sys_cfg.setVar_value("hll_测试检索开始时间_1044" + THREAD_ID);
            dq_sys_cfg.setApp_updt_dt("20200202");
            dq_sys_cfg.setApp_updt_ti(DateUtil.getSysTime());
            dq_sys_cfg.setUser_id(USER_ID);
            dq_sys_cfg.add(db);
            dq_sys_cfg.setSys_var_id("-1054" + THREAD_ID);
            dq_sys_cfg.setVar_name("hll_测试检索结束时间_1044" + THREAD_ID);
            dq_sys_cfg.setVar_value("hll_测试检索结束时间_1044" + THREAD_ID);
            dq_sys_cfg.setApp_updt_dt("20200101");
            dq_sys_cfg.setApp_updt_ti(DateUtil.getSysTime());
            dq_sys_cfg.setUser_id(USER_ID);
            dq_sys_cfg.add(db);
            //提交所有数据库执行操作
            SqlOperator.commitTransaction(db);
            //模拟登陆
            String bodyString = new HttpClient().buildSession()
                    .addData("user_id", USER_ID)
                    .addData("password", PASSWORD)
                    .post(LOGIN_URL).getBodyString();
            ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("模拟登陆失败!"));
            assertThat(ar.isSuccess(), is(true));
        }
    }

    @Method(desc = "测试案例执行完成后清理测试数据", logicStep = "测试案例执行完成后清理测试数据")
    @AfterClass
    public static void after() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            long num;
            //删除 Dq_sys_cfg
            SqlOperator.execute(db, "delete from " + Dq_sys_cfg.TableName + " where user_id=?", USER_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dq_sys_cfg.TableName +
                    " where user_id =?", USER_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_sys_cfg 表此条数据删除后,记录数应该为0", num, is(0L));
        }
    }

    @Method(desc = "添加变量配置数据信息", logicStep = "添加变量配置数据信息")
    @Test
    public void addVariableConfigDat() {
        String bodyString;
        ActionResult ar;
        //获取所有变量配置数据信息
        bodyString = new HttpClient()
                .addData("var_name", "hll_测试添加变量名_9999" + THREAD_ID)
                .addData("var_value", "hll_测试添加变量值_9999" + THREAD_ID)
                .post(getActionUrl("addVariableConfigDat")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //是否新增成功
            long number = SqlOperator.queryNumber(db,
                    "select count(*) from " + Dq_sys_cfg.TableName + " where var_name=?",
                    "hll_测试添加变量名_9999" + THREAD_ID)
                    .orElseThrow(() -> (new BusinessException("统计sql执行出错!")));
            assertThat(number, is(1L));
            //检查新增的数据是否正确
            Dq_sys_cfg dq_sys_cfg = SqlOperator.queryOneObject(db, Dq_sys_cfg.class,
                    "select * from " + Dq_sys_cfg.TableName + " where var_name=?",
                    "hll_测试添加变量名_9999" + THREAD_ID).orElseThrow(() -> (new BusinessException("统计sql执行出错!")));
            assertThat(dq_sys_cfg.getVar_value(), is("hll_测试添加变量值_9999" + THREAD_ID));
        }
    }

    @Method(desc = "删除变量配置数据", logicStep = "删除变量配置数据")
    @Test
    public void deleteVariableConfigData() {
        String bodyString;
        ActionResult ar;
        bodyString = new HttpClient()
                .addData("sys_var_id_s", new long[]{-1001L + THREAD_ID})
                .post(getActionUrl("deleteVariableConfigData")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> (new BusinessException("统计sql执行出错!")));
        assertThat(ar.isSuccess(), is(true));
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //检查删除的数据
            long number = SqlOperator.queryNumber(db,
                    "select count(*) from " + Dq_sys_cfg.TableName + " where sys_var_id in(?)",
                    -1001L + THREAD_ID).orElseThrow(() -> (new BusinessException("统计sql执行出错!")));
            assertThat("表 Dq_sys_cfg 数据删除成功", number, is(0L));
        }
    }

    @Method(desc = "修改变量配置数据信息", logicStep = "修改变量配置数据信息")
    @Test
    public void updateVariableConfigData() {
        String bodyString;
        ActionResult ar;
        //获取所有变量配置数据信息
        bodyString = new HttpClient()
                .addData("sys_var_id", "-1002" + THREAD_ID)
                .addData("var_name", "hll_测试修改变量名" + THREAD_ID)
                .addData("var_value", "hll_测试修改变量值_修改后" + THREAD_ID)
                .post(getActionUrl("updateVariableConfigData")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //是否修改成功
            long number = SqlOperator.queryNumber(db,
                    "select count(*) from " + Dq_sys_cfg.TableName + " where var_name=?", "hll_测试修改变量名" + THREAD_ID)
                    .orElseThrow(() -> (new BusinessException("统计sql执行出错!")));
            assertThat(number, is(1L));
            //检查修改的数据是否正确
            Dq_sys_cfg dq_sys_cfg = SqlOperator.queryOneObject(db, Dq_sys_cfg.class,
                    "select * from " + Dq_sys_cfg.TableName + " where var_name=?",
                    "hll_测试修改变量名" + THREAD_ID).orElseThrow(() -> (new BusinessException("统计sql执行出错!")));
            assertThat(dq_sys_cfg.getVar_value(), is("hll_测试修改变量值_修改后" + THREAD_ID));
        }
    }

    @Method(desc = "获取所有变量配置数据信息", logicStep = "获取所有变量配置数据信息(变量不会有很多,这里不做分页)")
    @Test
    public void getVariableConfigDataInfos() {
        String bodyString;
        ActionResult ar;
        //获取所有变量配置数据信息
        bodyString = new HttpClient()
                .post(getActionUrl("getVariableConfigDataInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForEntityList(Dq_sys_cfg.class).size() >= 8, is(true));
    }

    @Method(desc = "获取变量配置数据信息", logicStep = "获取变量配置数据信息")
    @Test
    public void getVariableConfigDataInfo() {
        String bodyString;
        ActionResult ar;
        //获取变量配置信息
        bodyString = new HttpClient()
                .addData("sys_var_id", "-1003" + THREAD_ID)
                .post(getActionUrl("getVariableConfigDataInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取返回ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("var_name"), is("hll_测试查询变量名" + THREAD_ID));
        assertThat(ar.getDataForMap().get("var_value"), is("hll_测试查询变量值" + THREAD_ID));
    }

    @Method(desc = "搜索变量信息", logicStep = "搜索变量信息")
    @Test
    public void searchVariableConfigData() {
        String bodyString;
        ActionResult ar;
        //获取搜索变量信息根据 var_name
        bodyString = new HttpClient()
                .addData("var_name", "测试检索变量名")
                .post(getActionUrl("searchVariableConfigData")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForEntityList(Dq_sys_cfg.class).size(), is(2));
        //获取搜索变量信息根据 var_value
        bodyString = new HttpClient()
                .addData("var_value", "测试检索变量值")
                .post(getActionUrl("searchVariableConfigData")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForEntityList(Dq_sys_cfg.class).size() >= 1, is(true));
        //获取搜索变量信息根据 start_date
        bodyString = new HttpClient()
                .addData("start_date", "20200101")
                .post(getActionUrl("searchVariableConfigData")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForEntityList(Dq_sys_cfg.class).size() >= 2, is(true));
        //获取搜索变量信息根据 end_date
        bodyString = new HttpClient()
                .addData("end_date", "20200101")
                .post(getActionUrl("searchVariableConfigData")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForEntityList(Dq_sys_cfg.class).size() >= 7, is(true));
    }
}