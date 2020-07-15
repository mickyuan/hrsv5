package hrds.k.biz.dm.ruleresults;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.collection.bean.DbConfBean;
import hrds.commons.entity.Dq_definition;
import hrds.commons.entity.Dq_index3record;
import hrds.commons.entity.Dq_result;
import hrds.commons.exception.BusinessException;
import hrds.testbase.LoadGeneralTestData;
import hrds.testbase.WebBaseTestCase;
import org.junit.*;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class RuleResultsActionTest extends WebBaseTestCase {

    //当前线程的id
    private static long THREAD_ID = Thread.currentThread().getId() * 1000000;
    //获取模拟登陆的URL
    private static final String LOGIN_URL = agentInitConfig.getString("login_url");
    //登录用户id
    private static final long USER_ID = agentInitConfig.getLong("general_oper_user_id");
    //登录用户密码
    private static final long PASSWORD = agentInitConfig.getLong("general_password");
    //初始化加载通用测试数据
    private static final LoadGeneralTestData loadGeneralTestData = new LoadGeneralTestData();
    //获取配置的通用存储层id
    private static final long DSL_ID = loadGeneralTestData.getData_store_layers().get(0).getDsl_id();

    @Method(desc = "初始化测试用例依赖表数据", logicStep = "初始化测试用例依赖表数据")
    @Before
    public void before() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //初始化 Dq_definition
            Dq_definition dq_definition = new Dq_definition();
            dq_definition.setReg_num(-1000L + THREAD_ID);
            dq_definition.setReg_name("hll_测试规则配置信息" + THREAD_ID);
            dq_definition.setApp_updt_dt(DateUtil.getSysDate());
            dq_definition.setApp_updt_ti(DateUtil.getSysTime());
            dq_definition.setIs_saveindex1("1");
            dq_definition.setIs_saveindex2("1");
            dq_definition.setIs_saveindex3("1");
            dq_definition.setCase_type("SQL" + THREAD_ID);
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            dq_definition.setReg_num(-1001L + THREAD_ID);
            dq_definition.setReg_name("hll_测试检索规则名称" + THREAD_ID);
            dq_definition.setApp_updt_dt(DateUtil.getSysDate());
            dq_definition.setApp_updt_ti(DateUtil.getSysTime());
            dq_definition.setRule_src("hll_测试检索规则来源" + THREAD_ID);
            dq_definition.setRule_tag("hll_测试检索规则标签" + THREAD_ID);
            dq_definition.setIs_saveindex1("1");
            dq_definition.setIs_saveindex2("1");
            dq_definition.setIs_saveindex3("1");
            dq_definition.setCase_type("SQL" + THREAD_ID);
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            //初始化 Dq_result
            Dq_result dq_result = new Dq_result();
            dq_result.setTask_id(-1001L + THREAD_ID);
            dq_result.setIs_saveindex1("1");
            dq_result.setIs_saveindex2("1");
            dq_result.setIs_saveindex3("1");
            dq_result.setCase_type("SQL" + THREAD_ID);
            dq_result.setVerify_date("20200101");
            dq_result.setStart_date("20200101");
            dq_result.setExec_mode("MAN");
            dq_result.setReg_num(-1001L + THREAD_ID);
            dq_result.add(db);
            dq_result.setTask_id(-1002L + THREAD_ID);
            dq_result.setIs_saveindex1("1");
            dq_result.setIs_saveindex2("1");
            dq_result.setIs_saveindex3("1");
            dq_result.setCase_type("SQL" + THREAD_ID);
            dq_result.setVerify_date("20200202");
            dq_result.setStart_date("20200202");
            dq_result.setExec_mode("MAN");
            dq_result.setVerify_result("0");
            dq_result.setReg_num(-1001L + THREAD_ID);
            dq_result.add(db);
            //提交所有数据库执行操作
            SqlOperator.commitTransaction(db);
            //根据初始化的 Sys_user 用户模拟登陆
            String bodyString = new HttpClient()
                    .addData("user_id", USER_ID)
                    .addData("password", PASSWORD)
                    .post(LOGIN_URL).getBodyString();
            ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("模拟登陆失败!"));
            assertThat(ar.isSuccess(), is(true));
        }
    }

    @Method(desc = "测试案例执行完成后清理测试数据", logicStep = "测试案例执行完成后清理测试数据")
    @After
    public void after() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            long num;
            //删除 Dq_definition 表测试数据
            SqlOperator.execute(db, "delete from " + Dq_definition.TableName + " where reg_num=?", -1000L + THREAD_ID);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dq_definition.TableName +
                    " where reg_num=?", -1000L + THREAD_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_definition 表此条数据删除后,记录数应该为0", num, is(0L));
            SqlOperator.execute(db, "delete from " + Dq_definition.TableName + " where reg_num=?", -1001L + THREAD_ID);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dq_definition.TableName +
                    " where reg_num=?", -1001L + THREAD_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_definition 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Dq_result 表测试数据
            SqlOperator.execute(db, "delete from " + Dq_result.TableName + " where task_id=?", -1001L + THREAD_ID);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dq_result.TableName +
                    " where task_id=?", -1001L + THREAD_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_result 表此条数据删除后,记录数应该为0", num, is(0L));
            SqlOperator.execute(db, "delete from " + Dq_result.TableName + " where task_id=?", -1002L + THREAD_ID);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dq_result.TableName +
                    " where task_id=?", -1002L + THREAD_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_result 表此条数据删除后,记录数应该为0", num, is(0L));
            //提交数据库操作
            SqlOperator.commitTransaction(db);
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
        assertThat(Long.parseLong(ar.getDataForMap().get("totalSize").toString()) >= 2, is(true));
    }

    @Method(desc = "检索规则结果信息", logicStep = "检索规则结果信息")
    @Test
    public void searchRuleResultInfos() {
        String bodyString;
        ActionResult ar;
        //条件为空
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
        assertThat(Long.parseLong(ar.getDataForMap().get("totalSize").toString()) >= 1, is(true));
        //开始日期
        bodyString = new HttpClient()
                .addData("start_date", "20200202")
                .post(getActionUrl("searchRuleResultInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(Long.parseLong(ar.getDataForMap().get("totalSize").toString()) >= 1, is(true));
        //规则来源rule_src
        bodyString = new HttpClient()
                .addData("rule_src", "测试检索规则来源")
                .post(getActionUrl("searchRuleResultInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(Long.parseLong(ar.getDataForMap().get("totalSize").toString()) >= 2, is(true));
        //规则标签
        bodyString = new HttpClient()
                .addData("rule_tag", "测试检索规则标签")
                .post(getActionUrl("searchRuleResultInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(Long.parseLong(ar.getDataForMap().get("totalSize").toString()) >= 2, is(true));
        //规则名称
        bodyString = new HttpClient()
                .addData("reg_name", "测试检索规则名称")
                .post(getActionUrl("searchRuleResultInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(Long.parseLong(ar.getDataForMap().get("totalSize").toString()) >= 2, is(true));
        //规则编号
        bodyString = new HttpClient()
                .addData("reg_num", -1001L + THREAD_ID)
                .post(getActionUrl("searchRuleResultInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(Long.parseLong(ar.getDataForMap().get("totalSize").toString()) >= 2, is(true));
    }

    @Method(desc = "规则执行详细信息", logicStep = "规则执行详细信息")
    @Test
    public void getRuleDetectDetail() {
        String bodyString;
        ActionResult ar;
        //任务id存在
        bodyString = new HttpClient()
                .addData("task_id", -1001L + THREAD_ID)
                .post(getActionUrl("getRuleDetectDetail")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("case_type"), is("SQL" + THREAD_ID));
        //任务id不存在
        bodyString = new HttpClient()
                .addData("task_id", -9001L + THREAD_ID)
                .post(getActionUrl("getRuleDetectDetail")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(false));
    }

    @Method(desc = "规则执行详细历史信息", logicStep = "规则执行详细历史信息")
    @Test
    public void getRuleExecuteHistoryInfo() {
        String bodyString;
        ActionResult ar;
        //规则编号存在
        bodyString = new HttpClient()
                .addData("reg_num", -1001L + THREAD_ID)
                .post(getActionUrl("getRuleExecuteHistoryInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(Long.parseLong(ar.getDataForMap().get("totalSize").toString()) >= 2, is(true));
        //规则编号不存在
        bodyString = new HttpClient()
                .addData("reg_num", -9001L + THREAD_ID)
                .post(getActionUrl("getRuleExecuteHistoryInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(true));
    }

    @Method(desc = "规则执行详细历史信息", logicStep = "规则执行详细历史信息")
    @Test
    public void exportIndicator3Results() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //根据数据层获取存储层配置信息
            List<Map<String, Object>> dataStoreConfBean =
                    SqlOperator.queryList(db, "select * from data_store_layer_attr where dsl_id = ?", DSL_ID);
            //获取存储层配置Map信息
            Map<String, String> dbConfigMap = ConnectionTool.getLayerMap(dataStoreConfBean);
            //根据存储层配置Map信息设置 DbConfBean
            DbConfBean dbConfBean = new DbConfBean();
            dbConfBean.setDatabase_drive(dbConfigMap.get("database_driver"));
            dbConfBean.setJdbc_url(dbConfigMap.get("jdbc_url"));
            dbConfBean.setUser_name(dbConfigMap.get("user_name"));
            dbConfBean.setDatabase_pad(dbConfigMap.get("database_pwd"));
            dbConfBean.setDatabase_type(dbConfigMap.get("database_type"));
            //设置表名
            String dqc_table_name = "dqc_" + THREAD_ID;
            //初始化存储层下的数表
            createDataTable(db, dbConfBean, dqc_table_name);
            //初始化 Dq_result
            Dq_result dq_result = new Dq_result();
            dq_result.setTask_id(THREAD_ID);
            dq_result.setTarget_tab(String.valueOf(THREAD_ID));
            dq_result.setIs_saveindex1("1");
            dq_result.setIs_saveindex2("1");
            dq_result.setIs_saveindex3("1");
            dq_result.setCase_type("SQL" + THREAD_ID);
            dq_result.setVerify_date("20200101");
            dq_result.setStart_date("20200101");
            dq_result.setExec_mode("MAN");
            dq_result.setReg_num(THREAD_ID);
            dq_result.add(db);
            //初始化 Dq_index3record
            Dq_index3record di3 = new Dq_index3record();
            di3.setRecord_id(THREAD_ID);
            di3.setTable_name(dqc_table_name);
            di3.setRecord_date(DateUtil.getSysDate());
            di3.setRecord_time(DateUtil.getSysTime());
            di3.setTask_id(THREAD_ID);
            di3.setDsl_id(DSL_ID);
            di3.add(db);
            //提交db操作
            db.commit();
            //条件正确
            String bodyString = new HttpClient()
                    .addData("task_id", THREAD_ID)
                    .post(getActionUrl("exportIndicator3Results")).getBodyString();
            assertThat(bodyString, is(notNullValue()));
            //条件为空
            bodyString = new HttpClient()
                    .post(getActionUrl("exportIndicator3Results")).getBodyString();
            ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                    "获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            //清理存储层数表
            cleanUpDataTable(db, dbConfBean, dqc_table_name);
            //清理 Dq_result
            dq_result.delete(db);
            //清理 Dq_index3record
            di3.delete(db);
            //提交db操作
            db.commit();
        }
    }

    @Method(desc = "存储层创建数表", logicStep = "存储层创建数表")
    private static void createDataTable(DatabaseWrapper db, DbConfBean dbConfBean, String dqc_table_name) {
        //使用存储层配置自定义Bean创建存储层链接
        DatabaseWrapper dbDataConn = null;
        try {
            dbDataConn = ConnectionTool.getDBWrapper(dbConfBean);
            //创建数表
            int i = dbDataConn.ExecDDL("CREATE TABLE IF NOT EXISTS " + dqc_table_name + " (id int, name varchar(20))");
            if (i != 0) {
                throw new BusinessException("表已经存在! table_name: " + dqc_table_name);
            }
            //导入数据
            dbDataConn.execute("INSERT INTO dqc_" + THREAD_ID + " VALUES (1,'a'),(2,'b')");
            //提交db操作
            dbDataConn.commit();
        } catch (Exception e) {
            if (null != dbDataConn) {
                dbDataConn.rollback();
            }
            e.printStackTrace();
            throw new BusinessException("创建存储层数表发生异常!" + e.getMessage());
        } finally {
            if (null != dbDataConn) {
                dbDataConn.close();
            }
        }
    }

    @Method(desc = "清理存储层数表", logicStep = "清理存储层数表")
    private static void cleanUpDataTable(DatabaseWrapper db, DbConfBean dbConfBean, String dqc_table_name) {
        //使用存储层配置自定义Bean创建存储层链接
        DatabaseWrapper dbDataConn = null;
        try {
            dbDataConn = ConnectionTool.getDBWrapper(dbConfBean);
            //删除数表
            int i = dbDataConn.ExecDDL("DROP TABLE " + dqc_table_name);
            if (i != 0) {
                throw new BusinessException("表已经不存在! table_name: " + dqc_table_name);
            }
            //提交db操作
            dbDataConn.commit();
        } catch (Exception e) {
            if (null != dbDataConn) {
                dbDataConn.rollback();
            }
            e.printStackTrace();
            throw new BusinessException("删除存储层数表发生异常!" + e.getMessage());
        } finally {
            if (null != dbDataConn) {
                dbDataConn.close();
            }
        }
    }
}