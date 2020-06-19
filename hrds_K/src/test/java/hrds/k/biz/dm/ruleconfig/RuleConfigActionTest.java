package hrds.k.biz.dm.ruleconfig;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.Pro_Type;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RuleConfigActionTest extends WebBaseTestCase {

    //测试数据的用户ID
    private static final long USER_ID = -1000L;
    //测试数据的部门ID
    private static final long DEP_ID = -1000L;
    //测试数据的文件id
    private static final String FILE_ID = "-1000";
    //测试数据库设置id
    private static final long DATABASE_ID = -1000L;
    //测试数据表id
    private static final long TABLE_ID = -1000L;

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
            dq_definition.setReg_num(-1000L);
            dq_definition.setReg_name("hll-测试删除规则");
            dq_definition.setApp_updt_dt(DateUtil.getSysDate());
            dq_definition.setApp_updt_ti(DateUtil.getSysTime());
            dq_definition.setIs_saveindex1("1");
            dq_definition.setIs_saveindex2("1");
            dq_definition.setIs_saveindex3("1");
            dq_definition.setCase_type("SQL");
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            dq_definition.setReg_num(-1010L);
            dq_definition.setReg_name("hll-测试批量删除规则-1");
            dq_definition.setApp_updt_dt(DateUtil.getSysDate());
            dq_definition.setApp_updt_ti(DateUtil.getSysTime());
            dq_definition.setIs_saveindex1("1");
            dq_definition.setIs_saveindex2("1");
            dq_definition.setIs_saveindex3("1");
            dq_definition.setCase_type("SQL");
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            dq_definition.setReg_num(-1011L);
            dq_definition.setReg_name("hll-测试批量删除规则-2");
            dq_definition.setApp_updt_dt(DateUtil.getSysDate());
            dq_definition.setApp_updt_ti(DateUtil.getSysTime());
            dq_definition.setIs_saveindex1("1");
            dq_definition.setIs_saveindex2("1");
            dq_definition.setIs_saveindex3("1");
            dq_definition.setCase_type("SQL");
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            dq_definition.setReg_num(-1020L);
            dq_definition.setReg_name("hll-测试修改规则");
            dq_definition.setApp_updt_dt(DateUtil.getSysDate());
            dq_definition.setApp_updt_ti(DateUtil.getSysTime());
            dq_definition.setIs_saveindex1("1");
            dq_definition.setIs_saveindex2("1");
            dq_definition.setIs_saveindex3("1");
            dq_definition.setCase_type("SQL");
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            dq_definition.setReg_num(-1030L);
            dq_definition.setReg_name("hll-测试查询规则");
            dq_definition.setApp_updt_dt(DateUtil.getSysDate());
            dq_definition.setApp_updt_ti(DateUtil.getSysTime());
            dq_definition.setIs_saveindex1("1");
            dq_definition.setIs_saveindex2("1");
            dq_definition.setIs_saveindex3("1");
            dq_definition.setCase_type("SQL");
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            dq_definition.setReg_num(-1040L);
            dq_definition.setReg_name("hll_测试检索规则名1040");
            dq_definition.setTarget_tab("hll_测试检索表1040");
            dq_definition.setRule_tag("hll_测试检索标签1040");
            dq_definition.setRule_src("hll_测试检索规则来源1041");
            dq_definition.setApp_updt_dt(DateUtil.getSysDate());
            dq_definition.setApp_updt_ti(DateUtil.getSysTime());
            dq_definition.setIs_saveindex1("1");
            dq_definition.setIs_saveindex2("1");
            dq_definition.setIs_saveindex3("1");
            dq_definition.setCase_type("SQL1040");
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            dq_definition.setReg_num(-1041L);
            dq_definition.setReg_name("hll_测试检索规则名1041");
            dq_definition.setTarget_tab("hll_测试检索表1041");
            dq_definition.setRule_tag("hll_测试检索标签1041");
            dq_definition.setRule_src("hll_测试检索规则来源1041");
            dq_definition.setApp_updt_dt(DateUtil.getSysDate());
            dq_definition.setApp_updt_ti(DateUtil.getSysTime());
            dq_definition.setIs_saveindex1("1");
            dq_definition.setIs_saveindex2("1");
            dq_definition.setIs_saveindex3("1");
            dq_definition.setCase_type("SQL1041");
            dq_definition.setUser_id(USER_ID);
            dq_definition.add(db);
            //初始化 Dq_rule_def
            Dq_rule_def dq_rule_def = new Dq_rule_def();
            dq_rule_def.setCase_type("hll_case_type");
            dq_rule_def.add(db);
            //初始化 Etl_sys
            Etl_sys etl_sys = new Etl_sys();
            etl_sys.setEtl_sys_cd("-1051");
            etl_sys.setEtl_sys_name("hll_测试工程名_1051_-1030");
            etl_sys.setUser_id(USER_ID);
            etl_sys.add(db);
            //初始化 Etl_job_def
            Etl_job_def etl_job_def = new Etl_job_def();
            etl_job_def.setEtl_job("hll_测试查询规则调度信息_1051_-1030");
            etl_job_def.setEtl_sys_cd("-1051");
            etl_job_def.setSub_sys_cd("-1052");
            etl_job_def.setPro_type(Pro_Type.SHELL.getCode());
            etl_job_def.add(db);
            //初始化 Etl_job_cur
            Etl_job_cur etl_job_cur = new Etl_job_cur();
            etl_job_cur.setEtl_job("hll_测试查询规则调度信息_1051_-1030");
            etl_job_cur.setEtl_sys_cd("-1051");
            etl_job_cur.setSub_sys_cd("-1052");
            etl_job_cur.add(db);
            //初始化 Etl_sub_sys_list
            Etl_sub_sys_list etl_sub_sys_list = new Etl_sub_sys_list();
            etl_sub_sys_list.setSub_sys_cd("-1151");
            etl_sub_sys_list.setEtl_sys_cd("-1051");
            etl_sub_sys_list.add(db);
            //初始化 Data_store_reg
            Data_store_reg data_store_reg = new Data_store_reg();
            data_store_reg.setFile_id(FILE_ID);
            data_store_reg.setCollect_type("4");
            data_store_reg.setOriginal_update_date(DateUtil.getSysDate());
            data_store_reg.setOriginal_update_time(DateUtil.getSysTime());
            data_store_reg.setOriginal_name("hll_测试表中文名");
            data_store_reg.setTable_name("hll_table");
            data_store_reg.setHyren_name("hyren_hll_table");
            data_store_reg.setStorage_date(DateUtil.getSysDate());
            data_store_reg.setStorage_time(DateUtil.getSysTime());
            data_store_reg.setFile_size(0L);
            data_store_reg.setAgent_id(-1000L);
            data_store_reg.setSource_id(-1000L);
            data_store_reg.setDatabase_id(DATABASE_ID);
            data_store_reg.setTable_id(TABLE_ID);
            data_store_reg.add(db);
            //初始化 Table_info
            Table_info table_info = new Table_info();
            table_info.setTable_id(TABLE_ID);
            table_info.setTable_name("hll_table");
            table_info.setTable_ch_name("hll_测试表中文名");
            table_info.setRec_num_date(DateUtil.getSysDate());
            table_info.setDatabase_id(DATABASE_ID);
            table_info.setValid_s_date(DateUtil.getSysDate());
            table_info.setValid_e_date("99991231");
            table_info.setIs_md5("1");
            table_info.setIs_register("0");
            table_info.setIs_customize_sql("0");
            table_info.setIs_parallel("0");
            table_info.setIs_user_defined("0");
            table_info.add(db);
            //初始化 Table_column
            Table_column table_column = new Table_column();
            table_column.setColumn_id(-1001L);
            table_column.setIs_primary_key("0");
            table_column.setColumn_name("hll_table_1001");
            table_column.setTable_id(TABLE_ID);
            table_column.setValid_s_date(DateUtil.getSysDate());
            table_column.setValid_e_date("99991231");
            table_column.setIs_alive("0");
            table_column.setIs_new("0");
            table_column.add(db);
            table_column.setColumn_id(-1002L);
            table_column.setIs_primary_key("0");
            table_column.setColumn_name("hll_table_1002");
            table_column.setTable_id(TABLE_ID);
            table_column.setValid_s_date(DateUtil.getSysDate());
            table_column.setValid_e_date("99991231");
            table_column.setIs_alive("0");
            table_column.setIs_new("0");
            table_column.add(db);
            table_column.setColumn_id(-1003L);
            table_column.setIs_primary_key("0");
            table_column.setColumn_name("hll_table_1003");
            table_column.setTable_id(TABLE_ID);
            table_column.setValid_s_date(DateUtil.getSysDate());
            table_column.setValid_e_date("99991231");
            table_column.setIs_alive("0");
            table_column.setIs_new("0");
            table_column.add(db);
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
            //删除 Dq_rule_def 表测试数据
            SqlOperator.execute(db, "delete from " + Dq_rule_def.TableName + " where case_type=?", "hll_case_type");
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dq_rule_def.TableName +
                    " where case_type =?", "hll_case_type").orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_rule_def 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Etl_sys 表测试数据
            SqlOperator.execute(db, "delete from " + Etl_sys.TableName + " where etl_sys_cd=?", "-1051");
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_sys.TableName +
                    " where etl_sys_cd =?", "-1051").orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Etl_sys 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Etl_job_def 表测试数据
            SqlOperator.execute(db, "delete from " + Etl_job_def.TableName + " where etl_job=? and etl_sys_cd=?",
                    "hll_测试查询规则调度信息_1051_-1030", "-1051");
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_def.TableName + " where etl_job=? and" +
                    " etl_sys_cd=?", "hll_测试查询规则调度信息_1051", "-1051").orElseThrow(()
                    -> new RuntimeException("count fail!"));
            assertThat("Etl_job_def 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Etl_job_cur 表测试数据
            SqlOperator.execute(db, "delete from " + Etl_job_cur.TableName + " where etl_job=? and etl_sys_cd=?",
                    "hll_测试查询规则调度信息_1051_-1030", "-1051");
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_cur.TableName + " where etl_job=? and" +
                    " etl_sys_cd=?", "hll_测试查询规则调度信息_1051_-1030", "-1051").orElseThrow(()
                    -> new RuntimeException("count fail!"));
            assertThat("Etl_job_cur 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Etl_sub_sys_list 表测试数据
            SqlOperator.execute(db, "delete from " + Etl_sub_sys_list.TableName + " where sub_sys_cd=? and etl_sys_cd=?",
                    "-1151", "-1051");
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_sub_sys_list.TableName + " where sub_sys_cd=? and" +
                    " etl_sys_cd=?", "-1151", "-1051").orElseThrow(()
                    -> new RuntimeException("count fail!"));
            assertThat("Etl_sub_sys_list 表此条数据删除后,记录数应该为0", num, is(0L));
            //清理测试用例运行时产生的表数据 Etl_para
            SqlOperator.execute(db, "delete from " + Etl_para.TableName + " where etl_sys_cd=?", "-1051");
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_para.TableName + " where etl_sys_cd=?",
                    "-1051").orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Etl_para 表此条数据删除后,记录数应该为0", num, is(0L));
            //清理测试用例运行时产生的表数据 Etl_resource
            SqlOperator.execute(db, "delete from " + Etl_resource.TableName + " where etl_sys_cd=?", "-1051");
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_resource.TableName + " where etl_sys_cd=?",
                    "-1051").orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Etl_resource 表此条数据删除后,记录数应该为0", num, is(0L));
            //清理测试用例运行时产生的表数据 Etl_job_def
            SqlOperator.execute(db, "delete from " + Etl_job_def.TableName + " where etl_sys_cd=?", "-1051");
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_def.TableName + " where etl_sys_cd=?" +
                    " and sub_sys_cd=?", "-1051", "-1151").orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Etl_job_def 表此条数据删除后,记录数应该为0", num, is(0L));
            //清理测试用例运行时产生的表数据 Etl_job_resource_rela
            SqlOperator.execute(db, "delete from " + Etl_job_resource_rela.TableName + " where etl_sys_cd=?", "-1051");
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_job_resource_rela.TableName
                    + " where etl_sys_cd=?", "-1051").orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Etl_job_resource_rela 表此条数据删除后,记录数应该为0", num, is(0L));

            //删除 Data_store_reg 表测试数据
            SqlOperator.execute(db, "delete from " + Data_store_reg.TableName + " where file_id=?", FILE_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_reg.TableName + " where file_id=?"
                    , FILE_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Data_store_reg 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Table_info 表测试数据
            SqlOperator.execute(db, "delete from " + Table_info.TableName + " where table_id=?", TABLE_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " where table_id=?"
                    , TABLE_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Table_info 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Table_column 表测试数据
            SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id=?", TABLE_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where table_id=?"
                    , TABLE_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Table_column 表此条数据删除后,记录数应该为0", num, is(0L));
        } catch (Exception e) {
            db.rollback();
        } finally {
            db.close();
        }
    }

    @Method(desc = "添加规则配置数据", logicStep = "添加规则配置数据")
    @Test
    public void addDqDefinition() {
        String bodyString;
        ActionResult ar;
        bodyString = new HttpClient()
                .addData("reg_name", "hll-测试添加规则")
                .addData("case_type", "SQL")
                .addData("user_id", USER_ID)
                .post(getActionUrl("addDqDefinition")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("测试添加规则失败!"));
        assertThat(ar.isSuccess(), is(true));
        DatabaseWrapper db = new DatabaseWrapper();
        try {
            //是否新增成功
            long number = SqlOperator.queryNumber(db, "select count(*) from " + Dq_definition.TableName +
                    " where reg_name=?", "hll-测试添加规则").orElseThrow(() -> (new BusinessException("统计sql执行出错!")));
            assertThat(number, is(1L));
            //检查新增的数据是否正确
            Dq_definition dq_definition = SqlOperator.queryOneObject(db, Dq_definition.class,
                    "select * from " + Dq_definition.TableName + " where reg_name=?",
                    "hll-测试添加规则").orElseThrow(() -> (new BusinessException("统计sql执行出错!")));
            assertThat(dq_definition.getReg_name(), is("hll-测试添加规则"));
        } catch (Exception e) {
            db.rollback();
        } finally {
            db.close();
        }
    }

    @Method(desc = "删除规则配置数据", logicStep = "删除规则配置数据")
    @Test
    public void deleteDqDefinition() {
        String bodyString;
        ActionResult ar;
        //规则存在
        bodyString = new HttpClient()
                .addData("reg_num", -1000L)
                .post(getActionUrl("deleteDqDefinition")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("删除规则信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        DatabaseWrapper db = new DatabaseWrapper();
        try {
            long number = SqlOperator.queryNumber(db,
                    "select count(*) from " + Dq_definition.TableName + " where reg_name=?", "hll-测试添加规则")
                    .orElseThrow(() -> (new BusinessException("统计sql执行出错!")));
            assertThat("Dq_definition 数据删除成功", number, is(0L));
        } catch (Exception e) {
            db.rollback();
        } finally {
            db.close();
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
                .addData("reg_num", new long[]{-1010L, -1011L})
                .post(getActionUrl("releaseDeleteDqDefinition")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查删除的数据
                OptionalLong number = SqlOperator.queryNumber(db,
                        "select count(*) from " + Dq_definition.TableName + " where reg_num in(-1010, -1011)");
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
                .addData("reg_num", -1020L)
                .addData("reg_name", "hll-测试修改规则_修改后")
                .addData("case_type", "SQL_修改后")
                .addData("user_id", USER_ID)
                .post(getActionUrl("updateDqDefinition")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("更新规则信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        DatabaseWrapper db = new DatabaseWrapper();
        try {
            //检查新增的数据是否正确
            Dq_definition dq_definition = SqlOperator.queryOneObject(db, Dq_definition.class,
                    "select * from " + Dq_definition.TableName + " where reg_name=?", "hll-测试修改规则_修改后")
                    .orElseThrow(() -> (new BusinessException("获取测试修改规则后的数据失败!")));
            assertThat(dq_definition.getReg_name(), is("hll-测试修改规则_修改后"));
            assertThat(dq_definition.getCase_type(), is("SQL_修改后"));
        } catch (Exception e) {
            db.rollback();
        } finally {
            db.close();
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
        assertThat(ar.getDataForMap().get("totalSize"), is(4));
    }

    @Method(desc = "获取规则信息", logicStep = "获取规则信息")
    @Test
    public void getDqDefinition() {
        String bodyString;
        ActionResult ar;
        bodyString = new HttpClient()
                .addData("reg_num", -1030L)
                .post(getActionUrl("getDqDefinition")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取规则信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("reg_num"), is(-1030));
        assertThat(ar.getDataForMap().get("reg_name"), is("hll-测试查询规则"));
        assertThat(ar.getDataForMap().get("case_type"), is("SQL"));
    }

    @Method(desc = "获取表字段信息列表", logicStep = "获取表字段信息列表")
    @Test
    public void getColumnsByTableName() {
        String bodyString;
        ActionResult ar;
        bodyString = new HttpClient()
                .addData("table_name", "hyren_hll_table")
                .post(getActionUrl("getColumnsByTableName")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则类型数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForResult().getRowCount(), is(3));
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
        //无法根据数据进行结果校验,因为查询的数据表存在其他数据
        assertThat(ar.isSuccess(), is(true));
    }

    @Method(desc = "保存作业信息", logicStep = "保存作业信息")
    @Test
    public void saveETLJob() {
        String bodyString;
        ActionResult ar;
        bodyString = new HttpClient()
                .addData("pro_id", "-1051")
                .addData("task_id", "-1151")
                .addData("reg_num", -1020L)
                .post(getActionUrl("saveETLJob")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则类型数据失败!"));
        assertThat(ar.isSuccess(), is(true));
    }

    @Method(desc = "搜索规则信息", logicStep = "搜索规则信息")
    @Test
    public void searchDqDefinitionInfos() {
        String bodyString;
        ActionResult ar;
        //根据规则编号检索
        bodyString = new HttpClient()
                .addData("reg_num", 104L)
                .post(getActionUrl("searchDqDefinitionInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则检索数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("totalSize"), is(2));
        //根据目标表名检索
        bodyString = new HttpClient()
                .addData("target_tab", "hll_测试检索表")
                .post(getActionUrl("searchDqDefinitionInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则检索数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("totalSize"), is(2));
        //根据规则标签检索
        bodyString = new HttpClient()
                .addData("rule_tag", "hll_测试检索标签")
                .post(getActionUrl("searchDqDefinitionInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则检索数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("totalSize"), is(2));
        //根据规则标签检索
        bodyString = new HttpClient()
                .addData("reg_name", "hll_测试检索规则名")
                .post(getActionUrl("searchDqDefinitionInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则检索数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("totalSize"), is(2));
        //根据规则来源检索
        bodyString = new HttpClient()
                .addData("rule_src", "hll_测试检索规则来源")
                .post(getActionUrl("searchDqDefinitionInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则检索数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("totalSize"), is(2));
        //根据规则类型检索
        bodyString = new HttpClient()
                .addData("case_type", "SQL1040")
                .post(getActionUrl("searchDqDefinitionInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则检索数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("totalSize"), is(1));
    }

    @Method(desc = "手动执行规则", logicStep = "手动执行规则")
    @Test
    public void manualExecution() {
        //手动执行规则是针对已入库数据进行规则检查
    }

    @Test
    public void getCheckIndex3() {
        //获取检查结果3的数据需要根据配置的数据存储层获取
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
        assertThat(ar.getDataForEntityList(Etl_sys.class).get(0).getEtl_sys_cd(), is("-1051"));
        assertThat(ar.getDataForEntityList(Etl_sys.class).get(0).getEtl_sys_name(), is("hll_测试工程名_1051_-1030"));
    }

    @Method(desc = "获取作业某个工程下的任务信息", logicStep = "获取作业某个工程下的任务信息")
    @Test
    public void getTaskInfo() {
        String bodyString;
        ActionResult ar;
        bodyString = new HttpClient()
                .addData("etl_sys_cd", "-1051")
                .post(getActionUrl("getTaskInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取作业某个工程下的任务信息数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForResult().getString(0, "etl_sys_cd"), is("-1051"));
        assertThat(ar.getDataForResult().getString(0, "sub_sys_cd"), is("-1151"));
    }

    @Method(desc = "查看规则调度状态", logicStep = "查看规则调度状态")
    @Test
    public void viewRuleSchedulingStatus() {
        String bodyString;
        ActionResult ar;
        //查看规则调度状态
        bodyString = new HttpClient()
                .addData("reg_num", -1030L)
                .post(getActionUrl("viewRuleSchedulingStatus")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取规则调度状态数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForResult().getString(0, "etl_job"), is("hll_测试查询规则调度信息_1051_-1030"));
    }

    @Test
    public void specifySqlCheck() {
        //检查表是针对已入库数据进行检查
    }

    @Test
    public void errDataSqlCheck() {
        //检查表是针对已入库数据进行检查
    }
}