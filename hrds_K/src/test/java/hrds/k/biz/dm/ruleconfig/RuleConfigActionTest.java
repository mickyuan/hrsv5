package hrds.k.biz.dm.ruleconfig;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.Pro_Type;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.testbase.LoadGeneralTestData;
import hrds.testbase.WebBaseTestCase;
import org.junit.*;

import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RuleConfigActionTest extends WebBaseTestCase {

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
    //测试数据库设置id
    private static final long TABLE_ID = agentInitConfig.getArray("tpcds_table_info_s").getMap(13).getLong("table_id");

    @Method(desc = "初始化测试用例依赖表数据", logicStep = "初始化测试用例依赖表数据")
    @Before
    public void before() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //初始化 Dq_definition
            Dq_definition dq_definition = new Dq_definition();
            dq_definition.setReg_num(-1000L + THREAD_ID);
            dq_definition.setReg_name("hll-测试删除规则" + THREAD_ID);
            dq_definition.setApp_updt_dt(DateUtil.getSysDate());
            dq_definition.setApp_updt_ti(DateUtil.getSysTime());
            dq_definition.setIs_saveindex1("1");
            dq_definition.setIs_saveindex2("1");
            dq_definition.setIs_saveindex3("1");
            dq_definition.setCase_type("SQL" + THREAD_ID);
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            dq_definition.setReg_num(-1010L + THREAD_ID);
            dq_definition.setReg_name("hll-测试批量删除规则-1" + THREAD_ID);
            dq_definition.setApp_updt_dt(DateUtil.getSysDate());
            dq_definition.setApp_updt_ti(DateUtil.getSysTime());
            dq_definition.setIs_saveindex1("1");
            dq_definition.setIs_saveindex2("1");
            dq_definition.setIs_saveindex3("1");
            dq_definition.setCase_type("SQL" + THREAD_ID);
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            dq_definition.setReg_num(-1011L + THREAD_ID);
            dq_definition.setReg_name("hll-测试批量删除规则-2" + THREAD_ID);
            dq_definition.setApp_updt_dt(DateUtil.getSysDate());
            dq_definition.setApp_updt_ti(DateUtil.getSysTime());
            dq_definition.setIs_saveindex1("1");
            dq_definition.setIs_saveindex2("1");
            dq_definition.setIs_saveindex3("1");
            dq_definition.setCase_type("SQL" + THREAD_ID);
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            dq_definition.setReg_num(-1020L + THREAD_ID);
            dq_definition.setReg_name("hll-测试修改规则" + THREAD_ID);
            dq_definition.setApp_updt_dt(DateUtil.getSysDate());
            dq_definition.setApp_updt_ti(DateUtil.getSysTime());
            dq_definition.setIs_saveindex1("1");
            dq_definition.setIs_saveindex2("1");
            dq_definition.setIs_saveindex3("1");
            dq_definition.setCase_type("SQL" + THREAD_ID);
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            dq_definition.setReg_num(-1030L + THREAD_ID);
            dq_definition.setReg_name("hll-测试查询规则" + THREAD_ID);
            dq_definition.setApp_updt_dt(DateUtil.getSysDate());
            dq_definition.setApp_updt_ti(DateUtil.getSysTime());
            dq_definition.setIs_saveindex1("1");
            dq_definition.setIs_saveindex2("1");
            dq_definition.setIs_saveindex3("1");
            dq_definition.setCase_type("SQL" + THREAD_ID);
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            dq_definition.setReg_num(-1040L + THREAD_ID);
            dq_definition.setReg_name("hll_测试检索规则名1040" + THREAD_ID);
            dq_definition.setTarget_tab("hll_测试检索表1040" + THREAD_ID);
            dq_definition.setRule_tag("hll_测试检索标签" + THREAD_ID);
            dq_definition.setRule_src("hll_测试检索规则来源1041" + THREAD_ID);
            dq_definition.setApp_updt_dt(DateUtil.getSysDate());
            dq_definition.setApp_updt_ti(DateUtil.getSysTime());
            dq_definition.setIs_saveindex1("1");
            dq_definition.setIs_saveindex2("1");
            dq_definition.setIs_saveindex3("1");
            dq_definition.setCase_type("SQL1040" + THREAD_ID);
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            dq_definition.setReg_num(-1041L + THREAD_ID);
            dq_definition.setReg_name("hll_测试检索规则名1041" + THREAD_ID);
            dq_definition.setTarget_tab("hll_测试检索表1041" + THREAD_ID);
            dq_definition.setRule_tag("hll_测试检索标签" + THREAD_ID);
            dq_definition.setRule_src("hll_测试检索规则来源1041" + THREAD_ID);
            dq_definition.setApp_updt_dt(DateUtil.getSysDate());
            dq_definition.setApp_updt_ti(DateUtil.getSysTime());
            dq_definition.setIs_saveindex1("1");
            dq_definition.setIs_saveindex2("1");
            dq_definition.setIs_saveindex3("1");
            dq_definition.setCase_type("SQL1041" + THREAD_ID);
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            dq_definition.setReg_num(-1050L + THREAD_ID);
            dq_definition.setReg_name("hll_测试手动执行规则名1041" + THREAD_ID);
            dq_definition.setTarget_tab("call_center");
            dq_definition.setSpecify_sql("SELECT COUNT(1) AS index1,COUNT(1) FROM call_center T1 WHERE cc_rec_start_date NOT IN ('1998-01-01','2000-01-02'); SELECT COUNT(1) AS index2 FROM call_center");
            dq_definition.setIs_saveindex1(IsFlag.Fou.getCode());
            dq_definition.setIs_saveindex2(IsFlag.Fou.getCode());
            dq_definition.setIs_saveindex3(IsFlag.Fou.getCode());
            dq_definition.setCase_type("COL ENUM");
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            //初始化 Dq_rule_def
            Dq_rule_def dq_rule_def = new Dq_rule_def();
            dq_rule_def.setCase_type("hll_case_type" + THREAD_ID);
            dq_rule_def.add(db);
            //初始化 Etl_sys
            Etl_sys etl_sys = new Etl_sys();
            etl_sys.setEtl_sys_cd("-1051" + THREAD_ID);
            etl_sys.setEtl_sys_name("hll_测试工程名_1051_-1030" + THREAD_ID);
            etl_sys.setUser_id(USER_ID);
            etl_sys.add(db);
            //初始化 Etl_job_def
            Etl_job_def etl_job_def = new Etl_job_def();
            etl_job_def.setEtl_job("hll_测试查询规则调度信息_1051_" + (-1030 + THREAD_ID));
            etl_job_def.setEtl_sys_cd("-1051" + THREAD_ID);
            etl_job_def.setSub_sys_cd("-1052" + THREAD_ID);
            etl_job_def.setPro_type(Pro_Type.SHELL.getCode());
            etl_job_def.add(db);
            //初始化 Etl_job_cur
            Etl_job_cur etl_job_cur = new Etl_job_cur();
            etl_job_cur.setEtl_job("hll_测试查询规则调度信息_1051_" + (-1030 + THREAD_ID));
            etl_job_cur.setEtl_sys_cd("-1051" + THREAD_ID);
            etl_job_cur.setSub_sys_cd("-1052" + THREAD_ID);
            etl_job_cur.add(db);
            //初始化 Etl_sub_sys_list
            Etl_sub_sys_list etl_sub_sys_list = new Etl_sub_sys_list();
            etl_sub_sys_list.setSub_sys_cd("-1151" + THREAD_ID);
            etl_sub_sys_list.setEtl_sys_cd("-1051" + THREAD_ID);
            etl_sub_sys_list.add(db);
            //初始化 Table_column
            Table_column table_column = new Table_column();
            table_column.setColumn_id(-1001L + THREAD_ID);
            table_column.setIs_primary_key("0");
            table_column.setColumn_name("r_reason_sk_" + THREAD_ID);
            table_column.setTable_id("14");
            table_column.setValid_s_date(DateUtil.getSysDate());
            table_column.setValid_e_date("99991231");
            table_column.setIs_alive("0");
            table_column.setIs_new("0");
            table_column.add(db);
            table_column.setColumn_id(-1002L + THREAD_ID);
            table_column.setIs_primary_key("0");
            table_column.setColumn_name("r_reason_id_" + THREAD_ID);
            table_column.setTable_id("14");
            table_column.setValid_s_date(DateUtil.getSysDate());
            table_column.setValid_e_date("99991231");
            table_column.setIs_alive("0");
            table_column.setIs_new("0");
            table_column.add(db);
            table_column.setColumn_id(-1003L + THREAD_ID);
            table_column.setIs_primary_key("0");
            table_column.setColumn_name("r_reason_desc_" + THREAD_ID);
            table_column.setTable_id("14");
            table_column.setValid_s_date(DateUtil.getSysDate());
            table_column.setValid_e_date("99991231");
            table_column.setIs_alive("0");
            table_column.setIs_new("0");
            table_column.add(db);
            table_column.setColumn_id(-1004L + THREAD_ID);
            table_column.setIs_primary_key("0");
            table_column.setColumn_name("remark_" + THREAD_ID);
            table_column.setTable_id("14");
            table_column.setValid_s_date(DateUtil.getSysDate());
            table_column.setValid_e_date("99991231");
            table_column.setIs_alive("0");
            table_column.setIs_new("0");
            table_column.add(db);
            //提交所有数据库执行操作
            SqlOperator.commitTransaction(db);
            //模拟登陆
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
            SqlOperator.execute(db, "delete from " + Dq_definition.TableName + " where reg_num in (?,?,?,?,?,?,?,?)",
                    (-1000L + THREAD_ID), (-1010L + THREAD_ID), (-1011L + THREAD_ID),
                    (-1020L + THREAD_ID), (-1030L + THREAD_ID), (-1040L + THREAD_ID),
                    (-1041L + THREAD_ID), (-1050L + THREAD_ID));
            SqlOperator.execute(db, "delete from " + Dq_definition.TableName + " where reg_name=?",
                    "hll-测试添加规则" + THREAD_ID);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dq_definition.TableName +
                    " where reg_name=?", "hll-测试添加规则" + THREAD_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_definition 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Dq_rule_def 表测试数据
            SqlOperator.execute(db, "delete from " + Dq_rule_def.TableName + " where case_type=?", "hll_case_type" + THREAD_ID);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dq_rule_def.TableName +
                    " where case_type =?", "hll_case_type" + THREAD_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_rule_def 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Etl_sys 表测试数据
            SqlOperator.execute(db, "delete from " + Etl_sys.TableName + " where etl_sys_cd=?", "-1051" + THREAD_ID);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_sys.TableName +
                    " where etl_sys_cd =?", "-1051" + THREAD_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Etl_sys 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Etl_job_def 表测试数据
            SqlOperator.execute(db, "delete from " + Etl_job_def.TableName + " where etl_job=? and etl_sys_cd=?",
                    "hll_测试查询规则调度信息_1051_-1030" + THREAD_ID, "-1051" + THREAD_ID);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_def.TableName + " where etl_job=? and" +
                    " etl_sys_cd=?", "hll_测试查询规则调度信息_1051" + THREAD_ID, "-1051" + THREAD_ID).orElseThrow(()
                    -> new RuntimeException("count fail!"));
            assertThat("Etl_job_def 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Etl_job_cur 表测试数据
            SqlOperator.execute(db, "delete from " + Etl_job_cur.TableName + " where etl_job=? and etl_sys_cd=?",
                    "hll_测试查询规则调度信息_1051_" + (-1030 + THREAD_ID), "-1051" + THREAD_ID);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_cur.TableName + " where etl_job=? and" +
                    " etl_sys_cd=?", "hll_测试查询规则调度信息_1051_-1030" + THREAD_ID, "-1051" + THREAD_ID).orElseThrow(()
                    -> new RuntimeException("count fail!"));
            assertThat("Etl_job_cur 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Etl_sub_sys_list 表测试数据
            SqlOperator.execute(db, "delete from " + Etl_sub_sys_list.TableName + " where sub_sys_cd=? and etl_sys_cd=?",
                    "-1151" + THREAD_ID, "-1051" + THREAD_ID);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_sub_sys_list.TableName + " where sub_sys_cd=? and" +
                    " etl_sys_cd=?", "-1151" + THREAD_ID, "-1051" + THREAD_ID).orElseThrow(()
                    -> new RuntimeException("count fail!"));
            assertThat("Etl_sub_sys_list 表此条数据删除后,记录数应该为0", num, is(0L));
            //清理测试用例运行时产生的表数据 Etl_para
            SqlOperator.execute(db, "delete from " + Etl_para.TableName + " where etl_sys_cd=?", "-1051" + THREAD_ID);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_para.TableName + " where etl_sys_cd=?",
                    "-1051" + THREAD_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Etl_para 表此条数据删除后,记录数应该为0", num, is(0L));
            //清理测试用例运行时产生的表数据 Etl_resource
            SqlOperator.execute(db, "delete from " + Etl_resource.TableName + " where etl_sys_cd=?", "-1051" + THREAD_ID);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_resource.TableName + " where etl_sys_cd=?",
                    "-1051" + THREAD_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Etl_resource 表此条数据删除后,记录数应该为0", num, is(0L));
            //清理测试用例运行时产生的表数据 Etl_job_def
            SqlOperator.execute(db, "delete from " + Etl_job_def.TableName + " where etl_sys_cd=?", "-1051" + THREAD_ID);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_def.TableName + " where etl_sys_cd=?" +
                    " and sub_sys_cd=?", "-1051" + THREAD_ID, "-1151" + THREAD_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Etl_job_def 表此条数据删除后,记录数应该为0", num, is(0L));
            //清理测试用例运行时产生的表数据 Etl_job_resource_rela
            SqlOperator.execute(db, "delete from " + Etl_job_resource_rela.TableName + " where etl_sys_cd=?", "-1051" + THREAD_ID);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_resource_rela.TableName
                    + " where etl_sys_cd=?", "-1051" + THREAD_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Etl_job_resource_rela 表此条数据删除后,记录数应该为0", num, is(0L));
            //清理测试用例运行后产生的数据 Dq_result
            SqlOperator.execute(db, "delete from " + Dq_result.TableName + " where reg_num=?", (-1050 + THREAD_ID));
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dq_result.TableName
                    + " where reg_num=?", (-1050 + THREAD_ID)).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_result 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Table_column 表测试数据
            SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id=?", TABLE_ID);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where table_id=?"
                    , TABLE_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Table_column 表此条数据删除后,记录数应该为0", num, is(0L));
            //提交数据库操作
            SqlOperator.commitTransaction(db);
        }
    }

    @Method(desc = "添加规则配置数据", logicStep = "添加规则配置数据")
    @Test
    public void addDqDefinition() {
        String bodyString;
        ActionResult ar;
        //FIXME  不正确的数据
        bodyString = new HttpClient()
                .addData("reg_name", "hll-测试添加规则" + THREAD_ID)
                .addData("case_type", "SQL")
                .addData("user_id", USER_ID)
                .post(getActionUrl("addDqDefinition")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("测试添加规则失败!"));
        assertThat(ar.isSuccess(), is(true));
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //检查新增的数据是否正确
            Dq_definition dq_definition = SqlOperator.queryOneObject(db, Dq_definition.class,
                    "select * from " + Dq_definition.TableName + " where reg_name=?",
                    "hll-测试添加规则" + THREAD_ID).orElseThrow(() -> (new BusinessException("统计sql执行出错!")));
            assertThat(dq_definition.getReg_name(), is("hll-测试添加规则" + THREAD_ID));
        }
    }

    @Method(desc = "删除规则配置数据", logicStep = "删除规则配置数据")
    @Test
    public void deleteDqDefinition() {
        String bodyString;
        ActionResult ar;
        //规则存在
        bodyString = new HttpClient()
                .addData("reg_num", -1000L + THREAD_ID)
                .post(getActionUrl("deleteDqDefinition")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("删除规则信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            long number = SqlOperator.queryNumber(db,
                    "select count(*) from " + Dq_definition.TableName + " where reg_name=?", "hll-测试删除规则" + THREAD_ID)
                    .orElseThrow(() -> (new BusinessException("统计sql执行出错!")));
            assertThat("Dq_definition 数据删除成功", number, is(0L));
        }
        //规则不存在
        bodyString = new HttpClient()
                .addData("reg_num", -9000L)
                .post(getActionUrl("deleteDqDefinition")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("删除规则信息失败!"));
        assertThat(ar.isSuccess(), is(true));
    }

    @Method(desc = "删除规则(批量)", logicStep = "删除规则(批量)")
    @Test
    public void releaseDeleteDqDefinition() {
        String bodyString;
        ActionResult ar;
        bodyString = new HttpClient()
                .addData("reg_num", new long[]{-1010L + THREAD_ID, -1011L + THREAD_ID})
                .post(getActionUrl("releaseDeleteDqDefinition")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查删除的数据
                OptionalLong number = SqlOperator.queryNumber(db,
                        "select count(*) from " + Dq_definition.TableName + " where reg_num in(?, ?)",
                        -1010L + THREAD_ID, -1011L + THREAD_ID);
                assertThat("表 Dq_definition 数据删除成功", number.orElse(-1), is(0L));
            }
        }
    }

    @Method(desc = "更新规则", logicStep = "更新规则")
    @Test
    public void updateDqDefinition() {
        String bodyString;
        ActionResult ar;
        bodyString = new HttpClient()
                .addData("reg_num", -1020L + THREAD_ID)
                .addData("reg_name", "hll-测试修改规则_修改后")
                .addData("case_type", "SQL_修改后")
                .addData("user_id", USER_ID)
                .post(getActionUrl("updateDqDefinition")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("更新规则信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //检查新增的数据是否正确
            Dq_definition dq_definition = SqlOperator.queryOneObject(db, Dq_definition.class,
                    "select * from " + Dq_definition.TableName + " where reg_name=?", "hll-测试修改规则_修改后")
                    .orElseThrow(() -> (new BusinessException("获取测试修改规则后的数据失败!")));
            assertThat(dq_definition.getReg_name(), is("hll-测试修改规则_修改后"));
            assertThat(dq_definition.getCase_type(), is("SQL_修改后"));
        }
    }

    @Method(desc = "获取规则信息列表", logicStep = "获取规则信息列表")
    @Test
    public void getDqDefinitionInfos() {
        String bodyString;
        ActionResult ar;
        bodyString = new HttpClient()
                .post(getActionUrl("getDqDefinitionInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取规则信息列表失败!"));
        assertThat(ar.isSuccess(), is(true));
        //FIXME 大于0有问题需要校验实际条数
        assertThat(Long.parseLong(ar.getDataForMap().get("totalSize").toString()) >= 6, is(true));
    }

    @Method(desc = "获取规则信息", logicStep = "获取规则信息")
    @Test
    public void getDqDefinition() {
        String bodyString;
        ActionResult ar;
        bodyString = new HttpClient()
                .addData("reg_num", -1030 + THREAD_ID)
                .post(getActionUrl("getDqDefinition")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取规则信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("reg_name"), is("hll-测试查询规则" + THREAD_ID));
        assertThat(ar.getDataForMap().get("case_type"), is("SQL" + THREAD_ID));
    }

    @Method(desc = "获取表字段信息列表", logicStep = "获取表字段信息列表")
    @Test
    public void getColumnsByTableName() {
        String bodyString;
        ActionResult ar;
        //表名存在
        bodyString = new HttpClient()
                .addData("table_name", "reason")
                .post(getActionUrl("getColumnsByTableName")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则类型数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForResult().getRowCount(), is(4));
        //表名不存在
        bodyString = new HttpClient()
                .addData("table_name", "not_reason")
                .post(getActionUrl("getColumnsByTableName")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则类型数据失败!"));
        assertThat(ar.isSuccess(), is(false));
    }

    @Method(desc = "获取规则类型数据测试方法", logicStep = "获取规则类型数据测试方法")
    @Test
    public void getDqRuleDef() {
        String bodyString;
        ActionResult ar;
        //获取规则类型数据
        bodyString = new HttpClient()
                .post(getActionUrl("getDqRuleDef")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则类型数据失败!"));
        //无法根据数据进行结果校验,因为查询的数据表存在其他数据
        assertThat(ar.isSuccess(), is(true));
    }

    @Method(desc = "获取规则类型数据测试方法", logicStep = "获取规则类型数据测试方法")
    @Test
    public void getDqHelpInfo() {
        String bodyString;
        ActionResult ar;
        bodyString = new HttpClient()
                .post(getActionUrl("getDqHelpInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则类型数据失败!"));
        assertThat(ar.isSuccess(), is(true));
    }

    @Method(desc = "保存作业信息", logicStep = "保存作业信息")
    @Test
    public void saveETLJob() {
        String bodyString;
        ActionResult ar;
        //reg_num存在
        bodyString = new HttpClient()
                .addData("pro_id", "-1051" + THREAD_ID)
                .addData("task_id", "-1151" + THREAD_ID)
                .addData("reg_num", -1020L + THREAD_ID)
                .post(getActionUrl("saveETLJob")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则类型数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        //FIXME 校验保存结果
        //reg_num不存在
        bodyString = new HttpClient()
                .addData("pro_id", "-1051" + THREAD_ID)
                .addData("task_id", "-1151" + THREAD_ID)
                .addData("reg_num", 1020L + THREAD_ID)
                .post(getActionUrl("saveETLJob")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则类型数据失败!"));
        assertThat(ar.isSuccess(), is(false));
    }

    @Method(desc = "搜索规则信息", logicStep = "搜索规则信息")
    @Test
    public void searchDqDefinitionInfos() {
        String bodyString;
        ActionResult ar;
        //根据规则编号检索
        bodyString = new HttpClient()
                .addData("reg_num", 99)
                .post(getActionUrl("searchDqDefinitionInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则检索数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(Long.parseLong(ar.getDataForMap().get("totalSize").toString()) >= 7, is(true));
        //根据目标表名检索
        bodyString = new HttpClient()
                .addData("target_tab", "hll_测试检索表")
                .post(getActionUrl("searchDqDefinitionInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则检索数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(Long.parseLong(ar.getDataForMap().get("totalSize").toString()) >= 2, is(true));
        //根据规则标签检索
        bodyString = new HttpClient()
                .addData("rule_tag", "hll_测试检索标签")
                .post(getActionUrl("searchDqDefinitionInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则检索数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(Long.parseLong(ar.getDataForMap().get("totalSize").toString()) >= 2, is(true));
        //根据规则标签检索
        bodyString = new HttpClient()
                .addData("reg_name", "hll_测试检索规则名")
                .post(getActionUrl("searchDqDefinitionInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则检索数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(Long.parseLong(ar.getDataForMap().get("totalSize").toString()) >= 2, is(true));
        //根据规则来源检索
        bodyString = new HttpClient()
                .addData("rule_src", "hll_测试检索规则来源")
                .post(getActionUrl("searchDqDefinitionInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则检索数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(Long.parseLong(ar.getDataForMap().get("totalSize").toString()) >= 2, is(true));
        //根据规则类型检索
        bodyString = new HttpClient()
                .addData("case_type", "SQL1040" + THREAD_ID)
                .post(getActionUrl("searchDqDefinitionInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则检索数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(Long.parseLong(ar.getDataForMap().get("totalSize").toString()) >= 1, is(true));
    }

    @Method(desc = "手动执行规则", logicStep = "手动执行规则")
    @Test
    public void manualExecution() {
        //手动执行规则是针对已入库数据进行规则检查
        String bodyString;
        ActionResult ar;
        //规则id不存在
        bodyString = new HttpClient()
                .addData("reg_num", 1050L + THREAD_ID)
                .addData("verify_date", "20200202")
                .post(getActionUrl("manualExecution")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取返回的ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(false));
        //规则id存在
        bodyString = new HttpClient()
                .addData("reg_num", -1050L + THREAD_ID)
                .addData("verify_date", "20200202")
                .post(getActionUrl("manualExecution")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取返回的ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
    }

    @Method(desc = "获取作业工程信息", logicStep = "获取作业工程信息")
    @Test
    public void getProInfos() {
        String bodyString;
        ActionResult ar;
        bodyString = new HttpClient()
                .post(getActionUrl("getProInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取作业工程信息数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForEntityList(Etl_sys.class).get(0).getEtl_sys_cd(), is("-1051" + THREAD_ID));
        assertThat(ar.getDataForEntityList(Etl_sys.class).get(0).getEtl_sys_name(), is("hll_测试工程名_1051_-1030" + THREAD_ID));
    }

    @Method(desc = "获取作业某个工程下的任务信息", logicStep = "获取作业某个工程下的任务信息")
    @Test
    public void getTaskInfo() {
        String bodyString;
        ActionResult ar;
        bodyString = new HttpClient()
                .addData("etl_sys_cd", "-1051" + THREAD_ID)
                .post(getActionUrl("getTaskInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取作业某个工程下的任务信息数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForResult().getString(0, "etl_sys_cd"), is("-1051" + THREAD_ID));
        assertThat(ar.getDataForResult().getString(0, "sub_sys_cd"), is("-1151" + THREAD_ID));
    }

    @Method(desc = "查看规则调度状态", logicStep = "查看规则调度状态")
    @Test
    public void viewRuleSchedulingStatus() {
        String bodyString;
        ActionResult ar;
        //查看规则调度状态
        bodyString = new HttpClient()
                .addData("reg_num", -1030 + THREAD_ID)
                .post(getActionUrl("viewRuleSchedulingStatus")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则调度状态数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForResult().getString(0, "etl_job"), is("hll_测试查询规则调度信息_1051_" + (-1030 + THREAD_ID)));
    }

    @Method(desc = "指定SQL（校验SQL）检查", logicStep = "指定SQL（校验SQL）检查")
    @Test
    public void specifySqlCheck() {
        String bodyString;
        ActionResult ar;
        //FIXME 其他类型校验加上
        //指定sql检查 枚举
        bodyString = new HttpClient()
                .addData("specify_sql", "SELECT COUNT(1) AS index1,COUNT(1) FROM call_center T1 WHERE" +
                        " cc_rec_start_date NOT IN ('1998-01-01','2000-01-02'); SELECT COUNT(1) AS index2 FROM " +
                        "call_center;")
                .post(getActionUrl("specifySqlCheck")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则调度状态数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("check_is_success"), is("success"));
        //指定sql对应的表不存在
        bodyString = new HttpClient()
                .addData("specify_sql", "SELECT COUNT(1) AS index1,COUNT(1) FROM not_call_center T1 WHERE" +
                        " cc_rec_start_date NOT IN ('1998-01-01','2000-01-02');" +
                        " SELECT COUNT(1) AS index2 FROM not_call_center")
                .post(getActionUrl("specifySqlCheck")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则调度状态数据失败!"));
        assertThat(ar.isSuccess(), is(false));
    }

    @Test
    public void errDataSqlCheck() {
        //检查表是针对已入库数据进行检查
        String bodyString;
        ActionResult ar;
        //指定sql检查
        bodyString = new HttpClient()
                .addData("err_data_sql", "SELECT cc_rec_start_date FROM call_center T1 WHERE" +
                        " cc_rec_start_date NOT IN ('1998-01-01','2000-01-02')")
                .post(getActionUrl("errDataSqlCheck")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则调度状态数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("check_is_success"), is("success"));
        //指定sql对应的表不存在
        bodyString = new HttpClient()
                .addData("err_data_sql", "SELECT cc_rec_start_date FROM not_call_center T1 WHERE" +
                        " cc_rec_start_date NOT IN ('1998-01-01','2000-01-02')")
                .post(getActionUrl("errDataSqlCheck")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则调度状态数据失败!"));
        assertThat(ar.isSuccess(), is(false));
    }
}