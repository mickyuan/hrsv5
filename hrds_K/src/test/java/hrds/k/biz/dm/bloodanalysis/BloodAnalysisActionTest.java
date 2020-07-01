package hrds.k.biz.dm.bloodanalysis;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.DataSourceType;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BloodAnalysisActionTest extends WebBaseTestCase {

    //当前线程的id
    private static long THREAD_ID = Thread.currentThread().getId() * 1000000;
    //获取模拟登陆的URL
    private static final String LOGIN_URL = agentInitConfig.getString("login_url");
    //登录用户id
    private static final long USER_ID = agentInitConfig.getLong("general_oper_user_id");
    //登录用户密码
    private static final long PASSWORD = agentInitConfig.getLong("general_password");
    //测试数据的集市ID
    private static final long DATA_MART_ID = THREAD_ID;
    //测试数据的集市表ID
    private static final long DATA_TABLE_ID = THREAD_ID;
    //测试数据的数据表已选数据源ID
    private static final long OWN_SOURCE_TABLE_ID = THREAD_ID;
    //测试数据的数据源表字段ID
    private static final long OWN_FIELD_ID = THREAD_ID;
    //测试数据的结果映射表ID
    private static final long ETL_ID = THREAD_ID;

    @Method(desc = "初始化测试用例依赖表数据", logicStep = "初始化测试用例依赖表数据")
    @BeforeClass
    public static void before() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //初始化 Dm_datatable
            Dm_datatable dm_datatable = new Dm_datatable();
            dm_datatable.setDatatable_id(DATA_TABLE_ID);
            dm_datatable.setData_mart_id(DATA_MART_ID);
            dm_datatable.setDatatable_cn_name("hll测试表");
            dm_datatable.setDatatable_en_name("hll_test_table_1000" + THREAD_ID);
            dm_datatable.setDatatable_create_date(DateUtil.getSysDate());
            dm_datatable.setDatatable_create_time(DateUtil.getSysTime());
            dm_datatable.setDatatable_due_date("99991231");
            dm_datatable.setDdlc_date(DateUtil.getSysDate());
            dm_datatable.setDdlc_time(DateUtil.getSysTime());
            dm_datatable.setDatac_date(DateUtil.getSysDate());
            dm_datatable.setDatac_time(DateUtil.getSysTime());
            dm_datatable.setDatatable_lifecycle("1");
            dm_datatable.setSoruce_size("1");
            dm_datatable.setEtl_date("99991231");
            dm_datatable.setStorage_type("3");
            dm_datatable.setTable_storage("0");
            dm_datatable.setRepeat_flag("0");
            dm_datatable.setCategory_id("0");
            dm_datatable.add(db);
            //初始化 Dm_datatable_source
            Dm_datatable_source dm_datatable_source = new Dm_datatable_source();
            dm_datatable_source.setOwn_dource_table_id(OWN_SOURCE_TABLE_ID);
            dm_datatable_source.setDatatable_id(DATA_TABLE_ID);
            dm_datatable_source.setOwn_source_table_name("hll_test_source_table_1000" + THREAD_ID);
            dm_datatable_source.setSource_type(DataSourceType.DCL.getCode());
            dm_datatable_source.add(db);
            //初始化 Own_source_field
            Own_source_field own_source_field = new Own_source_field();
            own_source_field.setOwn_field_id(OWN_FIELD_ID);
            own_source_field.setOwn_dource_table_id(OWN_SOURCE_TABLE_ID);
            own_source_field.setField_name("hll_test_source_field_1");
            own_source_field.setField_type("varchar(10)");
            own_source_field.add(db);
            //初始化 Dm_etlmap_info
            Dm_etlmap_info dm_etlmap_info = new Dm_etlmap_info();
            dm_etlmap_info.setEtl_id(ETL_ID);
            dm_etlmap_info.setDatatable_id(DATA_TABLE_ID);
            dm_etlmap_info.setOwn_dource_table_id(OWN_SOURCE_TABLE_ID);
            dm_etlmap_info.setTargetfield_name("hll_test_target_field_1" + THREAD_ID);
            dm_etlmap_info.setSourcefields_name("hll_test_source_field_1" + THREAD_ID);
            dm_etlmap_info.add(db);
            //提交所有数据库执行操作
            SqlOperator.commitTransaction(db);
            //根据初始化的 Sys_user 用户模拟登陆
            String bodyString = new HttpClient().buildSession()
                    .addData("user_id", USER_ID)
                    .addData("password", PASSWORD)
                    .post(LOGIN_URL).getBodyString();
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
            //删除 Dm_datatable 表测试数据
            SqlOperator.execute(db, "delete from " + Dm_datatable.TableName + " where datatable_id=?", DATA_TABLE_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dm_datatable.TableName +
                    " where datatable_id =?", DATA_TABLE_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dm_datatable 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Dm_datatable_source 表测试数据
            SqlOperator.execute(db, "delete from " + Dm_datatable_source.TableName + " where own_dource_table_id=?",
                    OWN_SOURCE_TABLE_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dm_datatable_source.TableName +
                    " where own_dource_table_id =?", OWN_SOURCE_TABLE_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dm_datatable_source 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Own_source_field 表测试数据
            SqlOperator.execute(db, "delete from " + Own_source_field.TableName + " where own_field_id=?", OWN_FIELD_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Own_source_field.TableName +
                    " where own_field_id =?", OWN_FIELD_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Own_source_field 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Dm_etlmap_info 表测试数据
            SqlOperator.execute(db, "delete from " + Dm_etlmap_info.TableName + " where etl_id=?", ETL_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dm_etlmap_info.TableName +
                    " where etl_id =?", ETL_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dm_etlmap_info 表此条数据删除后,记录数应该为0", num, is(0L));
        }
    }

    @Method(desc = "获取表血缘关系测试方法", logicStep = "获取表血缘关系测试方法")
    @Test
    public void getTableBloodRelationship() {
        String bodyString;
        ActionResult ar;
        //search_type 0:表查看,1:字段查看,IsFlag代码项设置; search_relationship 0:影响,1:血缘,IsFlag代码项设置
        //表查看-影响关系
        bodyString = new HttpClient()
                .addData("table_name", "hll_test_table_1000" + THREAD_ID)
                .addData("search_type", "0")
                .addData("search_relationship", "0")
                .post(getActionUrl("getTableBloodRelationship")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取血缘分析结果数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("name"), is("hll_test_table_1000" + THREAD_ID));
        //表查看-血缘关系
        bodyString = new HttpClient()
                .addData("table_name", "hll_test_table_1000" + THREAD_ID)
                .addData("search_type", "0")
                .addData("search_relationship", "1")
                .post(getActionUrl("getTableBloodRelationship")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取血缘分析结果数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("name"), is("hll_test_table_1000" + THREAD_ID));
        //字段查看-影响关系
        bodyString = new HttpClient()
                .addData("table_name", "hll_test_table_1000" + THREAD_ID)
                .addData("search_type", "1")
                .addData("search_relationship", "0")
                .post(getActionUrl("getTableBloodRelationship")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取血缘分析结果数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("name"), is("hll_test_table_1000" + THREAD_ID));
        //字段查看-血缘关系
        bodyString = new HttpClient()
                .addData("table_name", "hll_test_table_1000" + THREAD_ID)
                .addData("search_type", "1")
                .addData("search_relationship", "1")
                .post(getActionUrl("getTableBloodRelationship")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取血缘分析结果数据失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("name"), is("hll_test_table_1000" + THREAD_ID));
    }

    @Method(desc = "模糊搜索表名测试方法", logicStep = "模糊搜索表名测试方法")
    @Test
    public void fuzzySearchTableName() {
        String bodyString;
        ActionResult ar;
        //检索类型是 1: 血缘
        bodyString = new HttpClient()
                .addData("table_name", "hll_test_table")
                .addData("search_relationship", "1")
                .post(getActionUrl("fuzzySearchTableName")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("模糊搜索表名失败!"));
        assertThat(ar.isSuccess(), is(true));
        List<String> list1 = ar.getDataForEntityList(String.class);
        assertThat(list1.get(0), is("hll_test_table_1000" + THREAD_ID));
        //检索类型是 0: 影响
        bodyString = new HttpClient()
                .addData("table_name", "hll_test_source_table_1000" + THREAD_ID)
                .addData("search_relationship", "0")
                .post(getActionUrl("fuzzySearchTableName")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("模糊搜索表名失败!"));
        assertThat(ar.isSuccess(), is(true));
        List<String> list2 = ar.getDataForEntityList(String.class);
        assertThat(list2.get(0), is("hll_test_source_table_1000" + THREAD_ID));
        //检索类型是 3: 不存在的检索类型
        bodyString = new HttpClient()
                .addData("table_name", "hll")
                .addData("search_relationship", "3")
                .post(getActionUrl("fuzzySearchTableName")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("模糊搜索表名失败!"));
        assertThat(ar.isSuccess(), is(false));
    }
}