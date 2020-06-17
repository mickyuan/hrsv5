package hrds.k.biz.dm;


import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DataManageActionTest extends WebBaseTestCase {

    //测试数据的用户ID
    private static final long USER_ID = -1000L;
    //测试数据的部门ID
    private static final long DEP_ID = -1000L;
    //测试数据的数据源ID
    private static final long SOURCE_ID = -1000L;
    //测试数据的存储层ID
    private static final long DSL_ID = -1000L;
    //测试数据的数据存储关系ID
    private static final long STORAGE_ID = -1000L;

    @Method(desc = "初始化测试用例依赖表数据", logicStep = "初始化测试用例依赖表数据")
    @BeforeClass
    public static void before() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
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
            //初始化 Data_source 数据
            Data_source dataSource = new Data_source();
            dataSource.setSource_id(SOURCE_ID);
            dataSource.setDatasource_number("init-hll");
            dataSource.setDatasource_name("init-hll");
            dataSource.setSource_remark("init-hll");
            dataSource.setCreate_date(DateUtil.getSysDate());
            dataSource.setCreate_time(DateUtil.getSysTime());
            dataSource.setCreate_user_id(USER_ID);
            dataSource.add(db);
            //初始化 Source_relation_dep 数据
            Source_relation_dep sourceRelationDep = new Source_relation_dep();
            sourceRelationDep.setDep_id(DEP_ID);
            sourceRelationDep.setSource_id(SOURCE_ID);
            sourceRelationDep.add(db);
            //初始化 Data_store_layer 数据
            Data_store_layer data_store_layer = new Data_store_layer();
            data_store_layer.setDsl_id(DSL_ID);
            data_store_layer.setDsl_name("hll测试存储层名");
            data_store_layer.setStore_type("1");
            data_store_layer.setIs_hadoopclient("0");
            data_store_layer.add(db);
            //初始化 Table_storage_info
            Table_storage_info table_storage_info = new Table_storage_info();
            table_storage_info.setStorage_id(STORAGE_ID);
            table_storage_info.setFile_format("1");
            table_storage_info.setStorage_type("3");
            table_storage_info.setIs_zipper("0");
            table_storage_info.setStorage_time(7L);
            table_storage_info.setHyren_name("hll测试登记表名");
            table_storage_info.add(db);
            //初始化 Data_relation_table
            Dtab_relation_store dtab_relation_store = new Dtab_relation_store();
            dtab_relation_store.setDsl_id(DSL_ID);
            dtab_relation_store.setTab_id(STORAGE_ID);
            dtab_relation_store.setData_source("DCL");
            dtab_relation_store.add(db);
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
            dq_result.setTask_id(-1001L);
            dq_result.setVerify_result("1");
            dq_result.setIs_saveindex1("1");
            dq_result.setIs_saveindex2("1");
            dq_result.setIs_saveindex3("1");
            dq_result.setCase_type("SQL");
            dq_result.setReg_num(-1000L);
            dq_result.add(db);
            dq_result.setTask_id(-1002L);
            dq_result.setVerify_result("2");
            dq_result.setIs_saveindex1("1");
            dq_result.setIs_saveindex2("1");
            dq_result.setIs_saveindex3("1");
            dq_result.setCase_type("SQL");
            dq_result.setReg_num(-1000L);
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
        }
    }

    @Method(desc = "测试案例执行完成后清理测试数据",
            logicStep = "测试案例执行完成后清理测试数据")
    @AfterClass
    public static void after() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
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
            //删除 Data_source 表测试数据
            SqlOperator.execute(db, "delete from " + Data_source.TableName + " where source_id=?", SOURCE_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Data_source.TableName +
                    " where source_id =?", SOURCE_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Data_source 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Source_relation_dep 表测试数据
            SqlOperator.execute(db, "delete from " + Source_relation_dep.TableName + " where source_id=?", SOURCE_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Source_relation_dep.TableName +
                    " where source_id=?", DEP_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("source_relation_dep 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Data_store_layer 表测试数据
            SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id=?", DSL_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer.TableName +
                    " where dsl_id=?", DSL_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Data_store_layer 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Table_storage_info 表测试数据
            SqlOperator.execute(db, "delete from " + Table_storage_info.TableName + " where storage_id=?", STORAGE_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Table_storage_info.TableName +
                    " where storage_id=?", STORAGE_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Table_storage_info 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Dtab_relation_store 表测试数据
            SqlOperator.execute(db, "delete from " + Dtab_relation_store.TableName + " where storage_id=? and" +
                    " dsl_id=?", STORAGE_ID, DSL_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dtab_relation_store.TableName +
                    " where storage_id=? and dsl_id=?", STORAGE_ID, DSL_ID).orElseThrow(()
                    -> new RuntimeException("count fail!"));
            assertThat("Dtab_relation_store 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Dq_result 表测试数据
            SqlOperator.execute(db, "delete from " + Dq_result.TableName + " where reg_num=?", -1000L);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dq_result.TableName + " where reg_num=?",
                    -1000L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_result 表此条数据删除后,记录数应该为0", num, is(0L));
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
                .post(getActionUrl("getTableStatistics")).getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取数据管控首页各种规则统计信息失败!"));
        //无法根据数据进行结果校验,因为统计的数据表存在其他数据
        assertThat(ar.isSuccess(), is(true));
    }
}