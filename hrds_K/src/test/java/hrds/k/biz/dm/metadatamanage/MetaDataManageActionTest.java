package hrds.k.biz.dm.metadatamanage;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.*;
import hrds.commons.collection.DeleteDataTable;
import hrds.commons.collection.bean.DbConfBean;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.k.biz.commons.StorageLayerOperationTools;
import hrds.k.biz.dm.metadatamanage.bean.ColumnInfoBean;
import hrds.k.biz.dm.metadatamanage.bean.DqTableColumnBean;
import hrds.k.biz.dm.metadatamanage.bean.DqTableInfoBean;
import hrds.testbase.LoadGeneralTestData;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MetaDataManageActionTest extends WebBaseTestCase {

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
    //初始化通用 failure_table_id
    private static long FAILURE_TABLE_ID = PrimayKeyGener.getNextId();
    //初始化通用 file_id
    private static long FILE_ID = PrimayKeyGener.getNextId();
    //初始化通用 table_id
    private static long TABLE_ID = PrimayKeyGener.getNextId();
    //初始化通用 column_id
    private static long COLUMN_ID = PrimayKeyGener.getNextId();
    //初始化通用储存编号 storage_id
    private static long STORAGE_ID = PrimayKeyGener.getNextId();
    //初始化通用数据抽取定义id
    private static long DED_ID = PrimayKeyGener.getNextId();
    //初始化集市通用id data_mart_id
    private static long DATA_MART_ID = PrimayKeyGener.getNextId();
    //初始化集市通用分类id category_id
    private static long CATEGORY_ID = PrimayKeyGener.getNextId();
    //初始化集市通用数据表id datatable_id
    private static long DATATABLE_ID = PrimayKeyGener.getNextId();
    //初始化管控检测结果任务id
    private static long TASK_ID = PrimayKeyGener.getNextId();
    //初始化管控检测结果指标3记录id
    private static long RECORD_ID = PrimayKeyGener.getNextId();
    //初始化自定义表id
    private static long UDL_TABLE_ID = PrimayKeyGener.getNextId();


    @Method(desc = "初始化测试用例依赖表数据", logicStep = "初始化测试用例依赖表数据")
    @Before
    public void before() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //提交所有数据库执行操作
            db.commit();
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
            //获取DbConfBean
            DbConfBean dbConfBean = StorageLayerOperationTools.getDbConfBean(db, DSL_ID);
            //1.清理测试恢复表依赖数据
            // Dq_failure_table
            SqlOperator.execute(db, "delete from " + Dq_failure_table.TableName + " where failure_table_id in (?,?,?,?)",
                    FAILURE_TABLE_ID + 1, FAILURE_TABLE_ID + 2, FAILURE_TABLE_ID + 3, FAILURE_TABLE_ID + 4);
            long num = SqlOperator.queryNumber(db,
                    "select count(1) from " + Dq_failure_table.TableName + " where failure_table_id in (?,?,?,?)",
                    FAILURE_TABLE_ID + 1, FAILURE_TABLE_ID + 2, FAILURE_TABLE_ID + 3, FAILURE_TABLE_ID + 4)
                    .orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_failure_table 表此条数据删除后,记录数应该为0", num, is(0L));
            //清理存储层下创建的数表
            StorageLayerOperationTools.dropDataTable(db, dbConfBean, "test_create_table" + THREAD_ID);
            StorageLayerOperationTools.dropDataTable(db, dbConfBean, "dcl_restore_table" + THREAD_ID);
            StorageLayerOperationTools.dropDataTable(db, dbConfBean, "dml_restore_table" + THREAD_ID);
            StorageLayerOperationTools.dropDataTable(db, dbConfBean, "dql_restore_table" + THREAD_ID);
            StorageLayerOperationTools.dropDataTable(db, dbConfBean, "udl_restore_table" + THREAD_ID);
            //清理测试删除表依赖数据
            // Dq_failure_table
            SqlOperator.execute(db, "delete from " + Dq_failure_table.TableName + " where failure_table_id in (?,?,?,?,?)",
                    FAILURE_TABLE_ID + 11, FAILURE_TABLE_ID + 12, FAILURE_TABLE_ID + 13, FAILURE_TABLE_ID + 14,
                    FAILURE_TABLE_ID + 15);
            num = SqlOperator.queryNumber(db,
                    "select count(1) from " + Dq_failure_table.TableName + " where failure_table_id in (?,?,?,?,?)",
                    FAILURE_TABLE_ID + 11, FAILURE_TABLE_ID + 12, FAILURE_TABLE_ID + 13, FAILURE_TABLE_ID + 14,
                    FAILURE_TABLE_ID + 15).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_failure_table 表此条数据删除后,记录数应该为0", num, is(0L));
            // Table_storage_info
            SqlOperator.execute(db, "delete from " + Table_storage_info.TableName + " where storage_id in (?,?,?,?,?)",
                    STORAGE_ID + THREAD_ID + 1, STORAGE_ID + THREAD_ID + 2, STORAGE_ID + THREAD_ID + 3,
                    STORAGE_ID + THREAD_ID + 4, STORAGE_ID + THREAD_ID + 5);
            num = SqlOperator.queryNumber(db,
                    "select count(1) from " + Table_storage_info.TableName + " where storage_id in (?,?,?,?,?)",
                    STORAGE_ID + THREAD_ID + 1, STORAGE_ID + THREAD_ID + 2, STORAGE_ID + THREAD_ID + 3,
                    STORAGE_ID + THREAD_ID + 4, STORAGE_ID + THREAD_ID + 5).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Table_storage_info 表此条数据删除后,记录数应该为0", num, is(0L));
            //清理存储层下创建的数表
            StorageLayerOperationTools.dropDataTable(db, dbConfBean, "dcl_remove_db_table" + THREAD_ID);
            StorageLayerOperationTools.dropDataTable(db, dbConfBean, "dcl_remove_obj_table" + THREAD_ID);
            StorageLayerOperationTools.dropDataTable(db, dbConfBean, "dml_remove_table" + THREAD_ID);
            StorageLayerOperationTools.dropDataTable(db, dbConfBean, "dqc_remove_table" + THREAD_ID);
            StorageLayerOperationTools.dropDataTable(db, dbConfBean, "udl_remove_table" + THREAD_ID);
            //2.清理测试创建表依赖数据
            // Dq_table_info
            SqlOperator.execute(db, "delete from " + Dq_table_info.TableName + " where table_name in (?)",
                    "test_create_table" + THREAD_ID);
            num = SqlOperator.queryNumber(db,
                    "select count(1) from " + Dq_table_info.TableName + " where table_name in (?)",
                    "test_create_table" + THREAD_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_table_info 表此条数据删除后,记录数应该为0", num, is(0L));
            //3.清理测试保存元数据依赖数据
            // Data_store_reg
            SqlOperator.execute(db, "delete from " + Data_store_reg.TableName + " where table_id in (?)",
                    TABLE_ID + THREAD_ID);
            num = SqlOperator.queryNumber(db,
                    "select count(1) from " + Data_store_reg.TableName + " where table_id in (?)",
                    TABLE_ID + THREAD_ID)
                    .orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Data_store_reg 表此条数据删除后,记录数应该为0", num, is(0L));
            // Table_info
            SqlOperator.execute(db, "delete from " + Table_info.TableName + " where table_id in (?)",
                    TABLE_ID + THREAD_ID);
            num = SqlOperator.queryNumber(db,
                    "select count(1) from " + Table_info.TableName + " where table_id in (?)",
                    TABLE_ID + THREAD_ID)
                    .orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Table_info 表此条数据删除后,记录数应该为0", num, is(0L));
            // Table_column
            SqlOperator.execute(db, "delete from " + Table_column.TableName + " where column_id in (?,?)",
                    COLUMN_ID + THREAD_ID + 1, COLUMN_ID + THREAD_ID + 2);
            num = SqlOperator.queryNumber(db,
                    "select count(1) from " + Table_column.TableName + " where column_id in (?,?)",
                    COLUMN_ID + THREAD_ID + 1, COLUMN_ID + THREAD_ID + 2)
                    .orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Table_info 表此条数据删除后,记录数应该为0", num, is(0L));
            // Dm_datatable
            SqlOperator.execute(db, "delete from " + Dm_datatable.TableName + " where datatable_id in (?)",
                    DATATABLE_ID + THREAD_ID);
            num = SqlOperator.queryNumber(db,
                    "select count(1) from " + Dm_datatable.TableName + " where datatable_id in (?)",
                    DATATABLE_ID + THREAD_ID)
                    .orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dm_datatable 表此条数据删除后,记录数应该为0", num, is(0L));
            // Datatable_field_info
            SqlOperator.execute(db, "delete from " + Datatable_field_info.TableName + " where datatable_field_id in (?,?)",
                    COLUMN_ID + THREAD_ID + 1, COLUMN_ID + THREAD_ID + 2);
            num = SqlOperator.queryNumber(db,
                    "select count(1) from " + Datatable_field_info.TableName + " where datatable_field_id in (?,?)",
                    COLUMN_ID + THREAD_ID + 1, COLUMN_ID + THREAD_ID + 2)
                    .orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Table_info 表此条数据删除后,记录数应该为0", num, is(0L));
            // Dq_table_info
            SqlOperator.execute(db, "delete from " + Dq_table_info.TableName + " where table_id in (?)",
                    UDL_TABLE_ID + THREAD_ID);
            num = SqlOperator.queryNumber(db,
                    "select count(1) from " + Dq_table_info.TableName + " where table_id in (?)",
                    UDL_TABLE_ID + THREAD_ID)
                    .orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_table_info 表此条数据删除后,记录数应该为0", num, is(0L));
            // Dq_table_column
            SqlOperator.execute(db, "delete from " + Dq_table_column.TableName + " where field_id in (?,?)",
                    COLUMN_ID + THREAD_ID + 1, COLUMN_ID + THREAD_ID + 2);
            num = SqlOperator.queryNumber(db,
                    "select count(1) from " + Dq_table_column.TableName + " where field_id in (?,?)",
                    COLUMN_ID + THREAD_ID + 1, COLUMN_ID + THREAD_ID + 2)
                    .orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_table_column 表此条数据删除后,记录数应该为0", num, is(0L));
            //提交数据库操作
            db.commit();
        }
    }

    @Test
    public void getMDMTreeData() {
        String bodyString;
        ActionResult ar;
        //获取元数据管理树节点数据
        bodyString = new HttpClient()
                .post(getActionUrl("getMDMTreeData")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(true));
    }

    @Test
    public void getDRBTreeData() {
        String bodyString;
        ActionResult ar;
        //获取元数据管理树节点数据
        bodyString = new HttpClient()
                .post(getActionUrl("getDRBTreeData")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(true));
    }

    @Test
    public void getMDMTableColumnInfo() {
    }

    @Method(desc = "根据回收站表id获取表字段信息", logicStep = "根据回收站表id获取表字段信息")
    @Test
    public void getDRBTableColumnInfo() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //加载测试获取回收站DCL表字段信息数据
            Dq_failure_table dcl_dft = loadTestDCLGetDRBTableColumnInfo(db);
            //提交数据库操作
            db.commit();
            //正确参数,保存DCL层表元数据
            String bodyString = new HttpClient()
                    .addData("failure_table_id", dcl_dft.getFailure_table_id())
                    .post(getActionUrl("getDRBTableColumnInfo")).getBodyString();
            ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(true));
        }
    }

    @Test
    public void saveMetaData() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //加载测试保存DCL层元数据表的数据
            Data_store_reg data_store_reg = loadTestSaveDCLMetaData(db);
            //加载测试保存DML层元数据表的数据
            Dm_datatable dm_datatable = loadTestSaveDMLMetaData(db);
            //加载测试保存UDL层元数据表的数据
            Dq_table_info dq_table_info = loadTestSaveUDLMetaData(db);
            //提交数据库操作
            db.commit();
            //设置保存元字段信息
            List<ColumnInfoBean> columnInfoBeans = new ArrayList<>();
            ColumnInfoBean columnInfoBean = new ColumnInfoBean();
            columnInfoBean.setColumn_id(COLUMN_ID + THREAD_ID + 1);
            columnInfoBean.setColumn_name("save_meta_column_name_1");
            columnInfoBean.setColumn_ch_name("保存元字段名_1_保存后");
            columnInfoBeans.add(columnInfoBean);
            columnInfoBean = new ColumnInfoBean();
            columnInfoBean.setColumn_id(COLUMN_ID + THREAD_ID + 2);
            columnInfoBean.setColumn_name("save_meta_column_name_2");
            columnInfoBean.setColumn_ch_name("保存元字段名_2_保存后");
            columnInfoBeans.add(columnInfoBean);
            //正确参数,保存DCL层表元数据
            String bodyString = new HttpClient()
                    .addData("data_layer", DataSourceType.DCL.getCode())
                    .addData("file_id", data_store_reg.getFile_id())
                    .addData("table_id", data_store_reg.getTable_id())
                    .addData("table_ch_name", "测试DCL保存元信息" + THREAD_ID + "保存后")
                    .addData("columnInfoBeans", JsonUtil.toJson(columnInfoBeans))
                    .post(getActionUrl("saveMetaData")).getBodyString();
            ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(true));
            //错误参数,保存DCL层表元数据 file_id不存在
            bodyString = new HttpClient()
                    .addData("data_layer", DataSourceType.DCL.getCode())
                    .addData("file_id", "")
                    .addData("table_id", data_store_reg.getTable_id())
                    .addData("table_ch_name", "测试DCL保存元信息" + THREAD_ID + "保存后")
                    .addData("columnInfoBeans", JsonUtil.toJson(columnInfoBeans))
                    .post(getActionUrl("saveMetaData")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            //错误参数,保存DCL层表元数据 table_id不存在
            bodyString = new HttpClient()
                    .addData("data_layer", DataSourceType.DCL.getCode())
                    .addData("file_id", data_store_reg.getFile_id())
                    .addData("table_id", "")
                    .addData("table_ch_name", "测试DCL保存元信息" + THREAD_ID + "保存后")
                    .addData("columnInfoBeans", JsonUtil.toJson(columnInfoBeans))
                    .post(getActionUrl("saveMetaData")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            //错误参数,保存DCL层表元数据 columnInfoBeans不存在
            bodyString = new HttpClient()
                    .addData("data_layer", DataSourceType.DCL.getCode())
                    .addData("file_id", data_store_reg.getFile_id())
                    .addData("table_id", data_store_reg.getTable_id())
                    .addData("table_ch_name", "测试DCL保存元信息" + THREAD_ID + "保存后")
                    .post(getActionUrl("saveMetaData")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            //正确参数,保存DML层表元数据
            bodyString = new HttpClient()
                    .addData("data_layer", DataSourceType.DML.getCode())
                    .addData("file_id", dm_datatable.getDatatable_id())
                    .addData("table_id", dm_datatable.getDatatable_id())
                    .addData("table_ch_name", "测试DML保存元信息" + THREAD_ID + "保存后")
                    .addData("columnInfoBeans", JsonUtil.toJson(columnInfoBeans))
                    .post(getActionUrl("saveMetaData")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(true));
            //错误参数,保存DML层表元数据 table_id不存在 保存DML层元数据不需要file_id
            bodyString = new HttpClient()
                    .addData("data_layer", DataSourceType.DML.getCode())
                    .addData("file_id", dm_datatable.getDatatable_id())
                    .addData("table_id", -dm_datatable.getDatatable_id())
                    .addData("table_ch_name", "测试DML保存元信息" + THREAD_ID + "保存后")
                    .addData("columnInfoBeans", JsonUtil.toJson(columnInfoBeans))
                    .post(getActionUrl("saveMetaData")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            //错误参数,保存DML层表元数据 columnInfoBeans不存在 保存DML层元数据不需要file_id
            bodyString = new HttpClient()
                    .addData("data_layer", DataSourceType.DML.getCode())
                    .addData("file_id", dm_datatable.getDatatable_id())
                    .addData("table_id", dm_datatable.getDatatable_id())
                    .addData("table_ch_name", "测试DML保存元信息" + THREAD_ID + "保存后")
                    .addData("columnInfoBeans", "")
                    .post(getActionUrl("saveMetaData")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            //正确参数,保存DQC层表元数据,DQC层表结构不允许编辑
            //正确参数,保存UDL层表元数据
            bodyString = new HttpClient()
                    .addData("data_layer", DataSourceType.UDL.getCode())
                    .addData("file_id", dq_table_info.getTable_id())
                    .addData("table_id", dq_table_info.getTable_id())
                    .addData("table_ch_name", "测试UDL保存元信息" + THREAD_ID + "保存后")
                    .addData("columnInfoBeans", JsonUtil.toJson(columnInfoBeans))
                    .post(getActionUrl("saveMetaData")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(true));
            //错误参数,保存DML层表元数据 table_id不存在 保存UDL层元数据不需要file_id
            bodyString = new HttpClient()
                    .addData("data_layer", DataSourceType.UDL.getCode())
                    .addData("file_id", dm_datatable.getDatatable_id())
                    .addData("table_id", -dm_datatable.getDatatable_id())
                    .addData("table_ch_name", "测试UDL保存元信息" + THREAD_ID + "保存后")
                    .addData("columnInfoBeans", JsonUtil.toJson(columnInfoBeans))
                    .post(getActionUrl("saveMetaData")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            //错误参数,保存DML层表元数据 table_id不存在 保存UDL层元数据不需要file_id
            bodyString = new HttpClient()
                    .addData("data_layer", DataSourceType.UDL.getCode())
                    .addData("file_id", dm_datatable.getDatatable_id())
                    .addData("table_id", dm_datatable.getDatatable_id())
                    .addData("table_ch_name", "测试UDL保存元信息" + THREAD_ID + "保存后")
                    .addData("columnInfoBeans", "")
                    .post(getActionUrl("saveMetaData")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
        }
    }

    @Test
    public void restoreDRBTable() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //加载测试恢复DCL层的表数据
            Data_store_reg data_store_reg = loadTestRestoreDCLDqFailureTable(db);
            //加载测试恢复DML层的表数据
            Dm_datatable dm_datatable = loadTestRestoreDMLDqFailureTable(db);
            //加载测试恢复DQC层的表数据
            Dq_index3record dq_index3record = loadTestRestoreDQCDqFailureTable(db);
            //加载测试恢复UDL层的表数据
            Dq_table_info dq_table_info = loadTestRestoreUDLDqFailureTable(db);
            //提交db操作
            db.commit();
            //正确参数,恢复回收站DCL层表
            String bodyString = new HttpClient()
                    .addData("data_layer", DataSourceType.DCL.getCode())
                    .addData("file_id", FAILURE_TABLE_ID + THREAD_ID + 1)
                    .post(getActionUrl("restoreDRBTable")).getBodyString();
            ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(true));
            //校验DCL表恢复的数据
            Data_store_reg dsr = Dbo.queryOneObject(db, Data_store_reg.class, "SELECT * FROM " +
                    Data_store_reg.TableName + " WHERE file_id=?", data_store_reg.getFile_id()).orElseThrow(()
                    -> new BusinessException("获取DCL表恢复的数据失败!"));
            assertThat(dsr.getFile_id(), is(data_store_reg.getFile_id()));
            assertThat(dsr.getTable_name(), is(data_store_reg.getTable_name()));
            assertThat(dsr.getHyren_name(), is(data_store_reg.getHyren_name()));
            assertThat(dsr.getTable_id(), is(data_store_reg.getTable_id()));
            //错误参数,恢复回收站DCL层表(错误存储层代码)
            bodyString = new HttpClient()
                    .addData("data_layer", "ZZZ")
                    .addData("file_id", data_store_reg.getFile_id())
                    .post(getActionUrl("restoreDRBTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                    "获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            //错误参数,恢复回收站DCL层表(错误文件登记id)
            bodyString = new HttpClient()
                    .addData("data_layer", DataSourceType.DCL.getCode())
                    .addData("file_id", "zzz")
                    .post(getActionUrl("restoreDRBTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                    "获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            //正确参数,恢复回收站DML层表
            bodyString = new HttpClient()
                    .addData("data_layer", DataSourceType.DML.getCode())
                    .addData("file_id", FAILURE_TABLE_ID + THREAD_ID + 2)
                    .post(getActionUrl("restoreDRBTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                    "获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(true));
            //校验DML表恢复的数据
            Dm_datatable dd = Dbo.queryOneObject(db, Dm_datatable.class, "SELECT * FROM " + Dm_datatable.TableName +
                    " WHERE datatable_id=?", dm_datatable.getDatatable_id()).orElseThrow(()
                    -> new BusinessException("获取DML表恢复的数据失败!"));
            assertThat(dm_datatable.getDatatable_id(), is(dd.getDatatable_id()));
            assertThat(dm_datatable.getDatatable_en_name(), is(dd.getDatatable_en_name()));
            assertThat(dm_datatable.getDatatable_cn_name(), is(dd.getDatatable_cn_name()));
            //错误参数,恢复回收站DML层表(错误存储层代码)
            bodyString = new HttpClient()
                    .addData("data_layer", "ZZZ")
                    .addData("file_id", dm_datatable.getDatatable_id())
                    .post(getActionUrl("restoreDRBTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                    "获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            //错误参数,恢复回收站DML层表(错误文件登记id)
            bodyString = new HttpClient()
                    .addData("data_layer", DataSourceType.DML.getCode())
                    .addData("file_id", "zzz")
                    .post(getActionUrl("restoreDRBTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                    "获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            //恢复回收站DQC层表
            bodyString = new HttpClient()
                    .addData("data_layer", DataSourceType.DQC.getCode())
                    .addData("file_id", FAILURE_TABLE_ID + THREAD_ID + 3)
                    .post(getActionUrl("restoreDRBTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                    "获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(true));
            //校验DQC表恢复的数据
            Dq_index3record di3 = Dbo.queryOneObject(db, Dq_index3record.class, "SELECT * FROM " +
                    Dq_index3record.TableName + " WHERE record_id=?", dq_index3record.getRecord_id()).orElseThrow(()
                    -> new BusinessException("获取DQC表恢复的数据失败!"));
            assertThat(di3.getRecord_id(), is(dq_index3record.getRecord_id()));
            assertThat(di3.getTable_name(), is(dq_index3record.getTable_name()));
            assertThat(di3.getDsl_id(), is(dq_index3record.getDsl_id()));
            //错误参数,恢复回收站DML层表(错误存储层代码)
            bodyString = new HttpClient()
                    .addData("data_layer", "ZZZ")
                    .addData("file_id", dq_index3record.getRecord_id())
                    .post(getActionUrl("restoreDRBTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                    "获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            //错误参数,恢复回收站DML层表(错误文件登记id)
            bodyString = new HttpClient()
                    .addData("data_layer", DataSourceType.DQC.getCode())
                    .addData("file_id", "zzz")
                    .post(getActionUrl("restoreDRBTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                    "获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            //恢复回收站UDL层表
            bodyString = new HttpClient()
                    .addData("data_layer", DataSourceType.UDL.getCode())
                    .addData("file_id", FAILURE_TABLE_ID + THREAD_ID + 4)
                    .post(getActionUrl("restoreDRBTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                    "获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(true));
            Dq_table_info dti = Dbo.queryOneObject(db, Dq_table_info.class, "SELECT * FROM " + Dq_table_info.TableName +
                    " WHERE table_id=?", dq_table_info.getTable_id()).orElseThrow(()
                    -> new BusinessException("获取UDL表恢复的数据失败!"));
            assertThat(dti.getTable_id(), is(dq_table_info.getTable_id()));
            assertThat(dti.getTable_name(), is(dq_table_info.getTable_name()));
            assertThat(dti.getCh_name(), is(dq_table_info.getCh_name()));
            //错误参数,恢复回收站DML层表(错误存储层代码)
            bodyString = new HttpClient()
                    .addData("data_layer", "ZZZ")
                    .addData("file_id", dq_table_info.getTable_id())
                    .post(getActionUrl("restoreDRBTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                    "获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            //错误参数,恢复回收站DML层表(错误文件登记id)
            bodyString = new HttpClient()
                    .addData("data_layer", DataSourceType.DQC.getCode())
                    .addData("file_id", "zzz")
                    .post(getActionUrl("restoreDRBTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                    "获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            // 获取DbConfBean
            DbConfBean dbConfBean = StorageLayerOperationTools.getDbConfBean(db, DSL_ID);
            //清理接口产生的表数据和对应存储层下的数表
            data_store_reg.delete(db);
            StorageLayerOperationTools.dropDataTable(db, dbConfBean, data_store_reg.getHyren_name());
            dm_datatable.delete(db);
            StorageLayerOperationTools.dropDataTable(db, dbConfBean, dm_datatable.getDatatable_en_name());
            dq_index3record.delete(db);
            StorageLayerOperationTools.dropDataTable(db, dbConfBean, dq_index3record.getTable_name());
            dq_table_info.delete(db);
            StorageLayerOperationTools.dropDataTable(db, dbConfBean, dq_table_info.getTable_name());
            //提交db操作
            db.commit();
        }
    }

    @Test
    public void tableSetToInvalid() {
    }

    @Test
    public void removeCompletelyTable() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //加载测试彻底删除DCL层的表数据
            loadTestRemoveDCLDqFailureTable(db);
            //加载测试彻底删除DML层的表数据
            loadTestRemoveDMLDqFailureTable(db);
            //加载测试彻底删除DQC层的表数据
            loadTestRemoveDQCDqFailureTable(db);
            //加载测试彻底删除UDL层的表数据
            loadTestRemoveUDLDqFailureTable(db);
            //提交db操作
            db.commit();
            //正确参数,删除回收站DCL层表(DB)
            String bodyString = new HttpClient()
                    .addData("file_id", FAILURE_TABLE_ID + THREAD_ID + 11)
                    .post(getActionUrl("removeCompletelyTable")).getBodyString();
            ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(true));
            //错误参数,删除回收站DCL层表(DB) file_id不存在
            bodyString = new HttpClient()
                    .addData("file_id", FAILURE_TABLE_ID + THREAD_ID - 11)
                    .post(getActionUrl("removeCompletelyTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            //正确参数,删除回收站DCL层表(OBJ)
            bodyString = new HttpClient()
                    .addData("file_id", FAILURE_TABLE_ID + THREAD_ID + 12)
                    .post(getActionUrl("removeCompletelyTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(true));
            //错误参数,删除回收站DCL层表(OBJ) file_id不存在
            bodyString = new HttpClient()
                    .addData("file_id", FAILURE_TABLE_ID + THREAD_ID - 12)
                    .post(getActionUrl("removeCompletelyTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            //正确参数,删除回收站DML层表
            bodyString = new HttpClient()
                    .addData("file_id", FAILURE_TABLE_ID + THREAD_ID + 13)
                    .post(getActionUrl("removeCompletelyTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(true));
            //错误参数,删除回收站DML层表 file_id不存在
            bodyString = new HttpClient()
                    .addData("file_id", FAILURE_TABLE_ID + THREAD_ID - 13)
                    .post(getActionUrl("removeCompletelyTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            //正确参数,删除回收站DQC层表
            bodyString = new HttpClient()
                    .addData("file_id", FAILURE_TABLE_ID + THREAD_ID + 14)
                    .post(getActionUrl("removeCompletelyTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(true));
            //错误参数,删除回收站DQC层表 file_id不存在
            bodyString = new HttpClient()
                    .addData("file_id", FAILURE_TABLE_ID + THREAD_ID - 14)
                    .post(getActionUrl("removeCompletelyTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
            //正确参数,删除回收站UDL层表
            bodyString = new HttpClient()
                    .addData("file_id", FAILURE_TABLE_ID + THREAD_ID + 15)
                    .post(getActionUrl("removeCompletelyTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(true));
            //错误参数,删除回收站UDL层表 file_id不存在
            bodyString = new HttpClient()
                    .addData("file_id", FAILURE_TABLE_ID + THREAD_ID - 15)
                    .post(getActionUrl("removeCompletelyTable")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                    -> new BusinessException("获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(false));
        }
    }

    @Method(desc = "根据存储层id获取存储层配置信息", logicStep = "根据存储层id获取存储层配置信息")
    @Test
    public void getStorageLayerConfInfo() {
        //存储层id存在
        String bodyString = new HttpClient()
                .addData("dsl_id", DSL_ID)
                .post(getActionUrl("getStorageLayerConfInfo")).getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                -> new BusinessException("获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        //存储层id不存在
        bodyString = new HttpClient()
                .addData("dsl_id", -DSL_ID)
                .post(getActionUrl("getStorageLayerConfInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                -> new BusinessException("获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(false));
    }

    @Method(desc = "数据管控-创建表", logicStep = "数据管控-创建表")
    @Test
    public void createTable() {
        //设置创建表信息
        DqTableInfoBean dqTableInfoBean = new DqTableInfoBean();
        dqTableInfoBean.setTable_name("test_create_table" + THREAD_ID);
        dqTableInfoBean.setCh_name("测试创建表" + THREAD_ID);
        dqTableInfoBean.setTable_space("");
        dqTableInfoBean.setIs_trace(IsFlag.Fou.getCode());
        dqTableInfoBean.setDq_remark("测试创建表备注" + THREAD_ID);
        //设置表字段信息
        List<DqTableColumnBean> dqTableColumnBeans = new ArrayList<>();
        DqTableColumnBean dqTableColumnBean;
        dqTableColumnBean = new DqTableColumnBean();
        dqTableColumnBean.setField_ch_name("字段中文名1_" + THREAD_ID);
        dqTableColumnBean.setColumn_name("column_name_1_" + THREAD_ID);
        dqTableColumnBean.setColumn_type("int");
        dqTableColumnBean.setIs_null(IsFlag.Shi.getCode());
        dqTableColumnBeans.add(dqTableColumnBean);
        dqTableColumnBean = new DqTableColumnBean();
        dqTableColumnBean.setField_ch_name("字段中文名2_" + THREAD_ID);
        dqTableColumnBean.setColumn_name("column_name_2_" + THREAD_ID);
        dqTableColumnBean.setColumn_type("boolean");
        dqTableColumnBean.setIs_null(IsFlag.Shi.getCode());
        dqTableColumnBeans.add(dqTableColumnBean);
        dqTableColumnBean = new DqTableColumnBean();
        dqTableColumnBean.setField_ch_name("字段中文名3_" + THREAD_ID);
        dqTableColumnBean.setColumn_name("column_name_3_" + THREAD_ID);
        dqTableColumnBean.setColumn_type("char");
        dqTableColumnBean.setColumn_length("5");
        dqTableColumnBean.setIs_null(IsFlag.Shi.getCode());
        dqTableColumnBeans.add(dqTableColumnBean);
        dqTableColumnBean = new DqTableColumnBean();
        dqTableColumnBean.setField_ch_name("字段中文名4_" + THREAD_ID);
        dqTableColumnBean.setColumn_name("column_name_4_" + THREAD_ID);
        dqTableColumnBean.setColumn_type("varchar");
        dqTableColumnBean.setColumn_length("10");
        dqTableColumnBean.setIs_null(IsFlag.Shi.getCode());
        dqTableColumnBeans.add(dqTableColumnBean);
        dqTableColumnBean = new DqTableColumnBean();
        dqTableColumnBean.setField_ch_name("字段中文名5_" + THREAD_ID);
        dqTableColumnBean.setColumn_name("column_name_5_" + THREAD_ID);
        dqTableColumnBean.setColumn_type("text");
        dqTableColumnBean.setIs_null(IsFlag.Shi.getCode());
        dqTableColumnBeans.add(dqTableColumnBean);
        dqTableColumnBean = new DqTableColumnBean();
        dqTableColumnBean.setField_ch_name("字段中文名6_" + THREAD_ID);
        dqTableColumnBean.setColumn_name("column_name_6_" + THREAD_ID);
        dqTableColumnBean.setColumn_type("bigint");
        dqTableColumnBean.setIs_null(IsFlag.Shi.getCode());
        dqTableColumnBeans.add(dqTableColumnBean);
        dqTableColumnBean = new DqTableColumnBean();
        dqTableColumnBean.setField_ch_name("字段中文名7_" + THREAD_ID);
        dqTableColumnBean.setColumn_name("column_name_7_" + THREAD_ID);
        dqTableColumnBean.setColumn_type("numeric");
        dqTableColumnBean.setColumn_length("10,0");
        dqTableColumnBean.setIs_null(IsFlag.Shi.getCode());
        dqTableColumnBeans.add(dqTableColumnBean);
        //1 正确数据创建
        String bodyString = new HttpClient()
                .addData("dsl_id", DSL_ID)
                .addData("dqTableInfoBean", dqTableInfoBean)
                .addData("dqTableColumnBeans", JsonUtil.toJson(dqTableColumnBeans))
                .post(getActionUrl("createTable")).getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                -> new BusinessException("获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        //2 错误数据创建,存储层id为空
        bodyString = new HttpClient()
                .addData("dsl_id", -DSL_ID)
                .addData("dqTableInfoBean", dqTableInfoBean)
                .addData("dqTableColumnBeans", JsonUtil.toJson(dqTableColumnBeans))
                .post(getActionUrl("createTable")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                -> new BusinessException("获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(false));
        //3 错误数据创建,自定义表实体Bean为空
        bodyString = new HttpClient()
                .addData("dsl_id", DSL_ID)
                .addData("dqTableColumnBeans", JsonUtil.toJson(dqTableColumnBeans))
                .post(getActionUrl("createTable")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                -> new BusinessException("获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(false));
        //4 错误数据创建,自定义表字段实体Bean[]为空
        bodyString = new HttpClient()
                .addData("dsl_id", DSL_ID)
                .addData("dqTableInfoBean", dqTableInfoBean)
                .post(getActionUrl("createTable")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                -> new BusinessException("获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(false));
        //校验并清理接口产生的表数据
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //校验 Dq_table_info
            Dq_table_info dti = SqlOperator.queryOneObject(db, Dq_table_info.class,
                    "select * from " + Dq_table_info.TableName + " where table_name=?",
                    "test_create_table" + THREAD_ID).orElseThrow(() -> (new BusinessException("统计sql执行出错!")));
            assertThat(dti.getTable_name(), is("test_create_table" + THREAD_ID));
            //校验 Dtab_relation_store
            Dtab_relation_store dtrs = SqlOperator.queryOneObject(db, Dtab_relation_store.class,
                    "select * from " + Dtab_relation_store.TableName + " where dsl_id=? and tab_id=? and data_source=?",
                    DSL_ID, dti.getTable_id(), StoreLayerDataSource.UD.getCode()).orElseThrow(()
                    -> (new BusinessException("统计sql执行出错!")));
            assertThat(dtrs.getDsl_id(), is(DSL_ID));
            assertThat(dtrs.getTab_id(), is(dti.getTable_id()));
            assertThat(dtrs.getData_source(), is(StoreLayerDataSource.UD.getCode()));
            assertThat(dtrs.getIs_successful(), is(JobExecuteState.WanCheng.getCode()));
            //校验 Dq_table_column
            List<Dq_table_column> dtcs = SqlOperator.queryList(db, Dq_table_column.class,
                    "select * from " + Dq_table_column.TableName + " where table_id=?", dti.getTable_id());
            assertThat(dtcs.size(), is(dqTableColumnBeans.size()));
            //清理接口运行后产生的表数据
            dti.delete(db);
            dtrs.delete(db);
            dtcs.forEach(dtc -> dtc.delete(db));
            //清理对应存储层中数据
            DeleteDataTable.dropTableByDataLayer(dti.getTable_name(), db, DSL_ID);
            //提交数据库操作
            db.commit();
        }
    }

    @Method(desc = "获取测试恢复DCL层失效表的数据", logicStep = "获取测试恢复DCL层失效表的数据")
    private Data_store_reg loadTestRestoreDCLDqFailureTable(DatabaseWrapper db) {
        // 初始化测试DCL层恢复表依赖数据
        Data_store_reg dsr = new Data_store_reg();
        dsr.setFile_id(String.valueOf(FILE_ID + THREAD_ID));
        dsr.setCollect_type(AgentType.DBWenJian.getCode());
        dsr.setOriginal_update_date(DateUtil.getSysDate());
        dsr.setOriginal_update_time(DateUtil.getSysTime());
        dsr.setOriginal_name("测试贴源恢复表" + THREAD_ID);
        dsr.setHyren_name("dcl_restore_table" + THREAD_ID);
        dsr.setStorage_date(DateUtil.getSysDate());
        dsr.setStorage_time(DateUtil.getSysTime());
        dsr.setFile_size(10000L);
        dsr.setAgent_id(loadGeneralTestData.getAgent_info().getAgent_id());
        dsr.setSource_id(loadGeneralTestData.getData_source().getSource_id());
        dsr.setDatabase_id(loadGeneralTestData.getDatabase_set().getDatabase_id());
        dsr.setTable_id(TABLE_ID + THREAD_ID);
        //初始化 Dq_failure_table对象的测试数据
        Dq_failure_table dft = new Dq_failure_table();
        dft.setFailure_table_id(FAILURE_TABLE_ID + THREAD_ID + 1);
        dft.setFile_id(dsr.getTable_id().toString());
        dft.setTable_cn_name(dsr.getOriginal_name());
        dft.setTable_en_name(dsr.getHyren_name());
        dft.setTable_source(DataSourceType.DCL.getCode());
        dft.setTable_meta_info(JsonUtil.toJson(dsr));
        dft.setDsl_id(DSL_ID);
        dft.setData_source(StoreLayerDataSource.DB.getCode());
        dft.add(db);
        //获取DbConfBean
        DbConfBean dbConfBean = StorageLayerOperationTools.getDbConfBean(db, DSL_ID);
        //设置表名
        String table_name = Constant.DQC_INVALID_TABLE + dsr.getHyren_name();
        //初始化对应存储层下的数表
        StorageLayerOperationTools.createDataTable(db, dbConfBean, table_name);
        //返回初始化的对象信息
        return dsr;
    }

    @Method(desc = "加载测试恢复DML层失效表的数据", logicStep = "加载测试恢复DML层失效表的数据")
    private Dm_datatable loadTestRestoreDMLDqFailureTable(DatabaseWrapper db) {
        //初始化测试DML层恢复表依赖数据
        Dm_datatable dm = new Dm_datatable();
        dm.setDatatable_id(DATATABLE_ID + THREAD_ID);
        dm.setData_mart_id(DATA_MART_ID);
        dm.setDatatable_cn_name("测试集市恢复表" + THREAD_ID);
        dm.setDatatable_en_name("dml_restore_table" + THREAD_ID);
        dm.setDatatable_create_date(DateUtil.getSysDate());
        dm.setDatatable_create_time(DateUtil.getSysTime());
        dm.setDatatable_due_date("99991231");
        dm.setDdlc_date(DateUtil.getSysDate());
        dm.setDdlc_time(DateUtil.getSysTime());
        dm.setDatac_date(DateUtil.getSysDate());
        dm.setDatac_time(DateUtil.getSysTime());
        dm.setDatatable_lifecycle(IsFlag.Shi.getCode());
        dm.setSoruce_size(new BigDecimal(10));
        dm.setEtl_date(DateUtil.getSysDate());
        dm.setSql_engine("3");
        dm.setStorage_type("3");
        dm.setTable_storage(IsFlag.Fou.getCode());
        dm.setRepeat_flag(IsFlag.Fou.getCode());
        dm.setCategory_id(CATEGORY_ID);
        //初始化 Dq_failure_table对象的测试数据
        Dq_failure_table dft = new Dq_failure_table();
        dft.setFailure_table_id(FAILURE_TABLE_ID + THREAD_ID + 2);
        dft.setFile_id(dm.getDatatable_id());
        dft.setTable_cn_name(dm.getDatatable_cn_name());
        dft.setTable_en_name(dm.getDatatable_en_name());
        dft.setTable_source(DataSourceType.DML.getCode());
        dft.setTable_meta_info(JsonUtil.toJson(dm));
        dft.setDsl_id(DSL_ID);
        dft.setData_source(StoreLayerDataSource.DM.getCode());
        dft.add(db);
        //获取DbConfBean
        DbConfBean dbConfBean = StorageLayerOperationTools.getDbConfBean(db, DSL_ID);
        //设置表名
        String table_name = Constant.DQC_INVALID_TABLE + dm.getDatatable_en_name();
        //初始化对应存储层下的数表
        StorageLayerOperationTools.createDataTable(db, dbConfBean, table_name);
        //返回初始化的对象信息
        return dm;
    }

    @Method(desc = "加载测试恢复DQC层失效表的数据", logicStep = "加载测试恢复DQC层失效表的数据")
    private Dq_index3record loadTestRestoreDQCDqFailureTable(DatabaseWrapper db) {
        //初始化测试DQC层恢复表依赖数据
        Dq_index3record di3 = new Dq_index3record();
        di3.setRecord_id(RECORD_ID + THREAD_ID);
        di3.setTable_name("dqc_restore_table" + THREAD_ID);
        di3.setTable_col("id,name");
        di3.setRecord_date(DateUtil.getSysDate());
        di3.setRecord_time(DateUtil.getSysTime());
        di3.setTask_id(TASK_ID);
        di3.setDsl_id(DSL_ID);
        //初始化 Dq_failure_table对象的测试数据
        Dq_failure_table dft = new Dq_failure_table();
        dft.setFailure_table_id(FAILURE_TABLE_ID + THREAD_ID + 3);
        dft.setFile_id(di3.getRecord_id());
        dft.setTable_cn_name(di3.getTable_name());
        dft.setTable_en_name(di3.getTable_name());
        dft.setTable_source(DataSourceType.DQC.getCode());
        dft.setTable_meta_info(JsonUtil.toJson(di3));
        dft.setDsl_id(DSL_ID);
        dft.setData_source(StoreLayerDataSource.DQ.getCode());
        dft.add(db);
        //获取DbConfBean
        DbConfBean dbConfBean = StorageLayerOperationTools.getDbConfBean(db, DSL_ID);
        //设置表名
        String table_name = Constant.DQC_INVALID_TABLE + di3.getTable_name();
        //初始化对应存储层下的数表
        StorageLayerOperationTools.createDataTable(db, dbConfBean, table_name);
        //返回初始化的对象信息
        return di3;
    }

    @Method(desc = "加载测试恢复UDL层失效表的数据", logicStep = "加载测试恢复UDL层失效表的数据")
    private Dq_table_info loadTestRestoreUDLDqFailureTable(DatabaseWrapper db) {
        //初始化测试UDL层恢复表依赖数据
        Dq_table_info dti = new Dq_table_info();
        dti.setTable_id(UDL_TABLE_ID + THREAD_ID);
        dti.setTable_space("");
        dti.setTable_name("udl_restore_table" + THREAD_ID);
        dti.setCh_name("测试自定义层恢复表" + THREAD_ID);
        dti.setCreate_date(DateUtil.getSysDate());
        dti.setEnd_date("99991231");
        dti.setIs_trace(IsFlag.Fou.getCode());
        dti.setCreate_id(USER_ID);
        //初始化 Dq_failure_table对象的测试数据
        Dq_failure_table dft = new Dq_failure_table();
        dft.setFailure_table_id(FAILURE_TABLE_ID + THREAD_ID + 4);
        dft.setFile_id(dti.getTable_id());
        dft.setTable_cn_name(dti.getCh_name());
        dft.setTable_en_name(dti.getTable_name());
        dft.setTable_source(DataSourceType.UDL.getCode());
        dft.setTable_meta_info(JsonUtil.toJson(dti));
        dft.setDsl_id(DSL_ID);
        dft.setData_source(StoreLayerDataSource.UD.getCode());
        dft.add(db);
        //获取DbConfBean
        DbConfBean dbConfBean = StorageLayerOperationTools.getDbConfBean(db, DSL_ID);
        //设置表名
        String table_name = Constant.DQC_INVALID_TABLE + dti.getTable_name();
        //初始化对应存储层下的数表
        StorageLayerOperationTools.createDataTable(db, dbConfBean, table_name);
        //返回初始化的对象信息
        return dti;
    }

    @Method(desc = "加载测试删除DCL层失效表的数据", logicStep = "加载测试是删除DCL层失效表的数据")
    private void loadTestRemoveDCLDqFailureTable(DatabaseWrapper db) {
        //初始化删除数据结合列表
        List<Dq_failure_table> dq_failure_tables = new ArrayList<>();
        //初始化测试DCL层删除(DB)表依赖数据
        Data_store_reg data_store_reg = new Data_store_reg();
        data_store_reg.setFile_id(String.valueOf(FILE_ID + THREAD_ID + 1));
        data_store_reg.setCollect_type(AgentType.DBWenJian.getCode());
        data_store_reg.setOriginal_update_date(DateUtil.getSysDate());
        data_store_reg.setOriginal_update_time(DateUtil.getSysTime());
        data_store_reg.setOriginal_name("测试贴源层删除DB表" + THREAD_ID);
        data_store_reg.setTable_name("dcl_remove_db_table" + THREAD_ID);
        data_store_reg.setHyren_name("dcl_remove_db_table" + THREAD_ID);
        data_store_reg.setStorage_date(DateUtil.getSysDate());
        data_store_reg.setStorage_time(DateUtil.getSysTime());
        data_store_reg.setFile_size(10000L);
        data_store_reg.setAgent_id(loadGeneralTestData.getAgent_info().getAgent_id());
        data_store_reg.setSource_id(loadGeneralTestData.getData_source().getSource_id());
        data_store_reg.setDatabase_id(loadGeneralTestData.getDatabase_set().getDatabase_id());
        data_store_reg.setTable_id(TABLE_ID + 1);
        //初始化 Table_storage_info 的数据(DB)
        Table_storage_info table_storage_info = new Table_storage_info();
        table_storage_info.setStorage_id(STORAGE_ID + THREAD_ID + 1);
        table_storage_info.setFile_format(FileFormat.FeiDingChang.getCode());
        table_storage_info.setStorage_type(StorageType.TiHuan.getCode());
        table_storage_info.setIs_zipper(IsFlag.Fou.getCode());
        table_storage_info.setStorage_time(7L);
        table_storage_info.setHyren_name(data_store_reg.getHyren_name());
        table_storage_info.setTable_id(data_store_reg.getTable_id());
        table_storage_info.add(db);
        //初始化 Table_info (DB)
        Table_info table_info = new Table_info();
        table_info.setTable_id(data_store_reg.getTable_id());
        table_info.setTable_name(data_store_reg.getTable_name());
        table_info.setTable_ch_name(data_store_reg.getOriginal_name());
        table_info.setRec_num_date(data_store_reg.getStorage_date());
        table_info.setDatabase_id(loadGeneralTestData.getDatabase_set().getDatabase_id());
        table_info.setValid_s_date(DateUtil.getSysDate());
        table_info.setValid_e_date("99991231");
        table_info.setIs_md5(IsFlag.Fou.getCode());
        table_info.setIs_register(IsFlag.Fou.getCode());
        table_info.setIs_customize_sql(IsFlag.Fou.getCode());
        table_info.setIs_parallel(IsFlag.Fou.getCode());
        table_info.setIs_user_defined(IsFlag.Fou.getCode());
        table_info.add(db);
        //初始化 Data_extraction_def 的数据(DB)
        Data_extraction_def data_extraction_def = new Data_extraction_def();
        data_extraction_def.setDed_id(DED_ID + THREAD_ID + 1);
        data_extraction_def.setTable_id(data_store_reg.getTable_id());
        data_extraction_def.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
        data_extraction_def.setIs_header(IsFlag.Shi.getCode());
        data_extraction_def.setDatabase_code(DataBaseCode.UTF_8.getCode());
        data_extraction_def.setDbfile_format(FileFormat.FeiDingChang.getCode());
        data_extraction_def.setIs_archived(IsFlag.Fou.getCode());
        data_extraction_def.add(db);
        //初始化 Dq_failure_table对象的测试数据(DB)
        Dq_failure_table dq_failure_table = new Dq_failure_table();
        dq_failure_table.setFailure_table_id(FAILURE_TABLE_ID + THREAD_ID + 11);
        dq_failure_table.setFile_id(data_store_reg.getTable_id().toString());
        dq_failure_table.setTable_cn_name(data_store_reg.getOriginal_name());
        dq_failure_table.setTable_en_name(data_store_reg.getHyren_name());
        dq_failure_table.setTable_source(DataSourceType.DCL.getCode());
        dq_failure_table.setTable_meta_info(JsonUtil.toJson(data_store_reg));
        dq_failure_table.setDsl_id(DSL_ID);
        dq_failure_table.setData_source(StoreLayerDataSource.DB.getCode());
        dq_failure_table.add(db);
        dq_failure_tables.add(dq_failure_table);
        //初始化测试DCL层删除(OBJ)表依赖数据
        data_store_reg = new Data_store_reg();
        data_store_reg.setFile_id(String.valueOf(FILE_ID + THREAD_ID + 2));
        data_store_reg.setCollect_type(AgentType.DuiXiang.getCode());
        data_store_reg.setOriginal_update_date(DateUtil.getSysDate());
        data_store_reg.setOriginal_update_time(DateUtil.getSysTime());
        data_store_reg.setOriginal_name("测试贴源层删除OBJ表" + THREAD_ID);
        data_store_reg.setTable_name("dcl_remove_obj_table" + THREAD_ID);
        data_store_reg.setHyren_name("dcl_remove_obj_table" + THREAD_ID);
        data_store_reg.setStorage_date(DateUtil.getSysDate());
        data_store_reg.setStorage_time(DateUtil.getSysTime());
        data_store_reg.setFile_size(10000L);
        data_store_reg.setAgent_id(loadGeneralTestData.getAgent_info().getAgent_id());
        data_store_reg.setSource_id(loadGeneralTestData.getData_source().getSource_id());
        data_store_reg.setDatabase_id(loadGeneralTestData.getDatabase_set().getDatabase_id());
        data_store_reg.setTable_id(TABLE_ID + 2);
        //初始化 Table_storage_info的数据(OBJ)
        table_storage_info = new Table_storage_info();
        table_storage_info.setStorage_id(STORAGE_ID + THREAD_ID + 2);
        table_storage_info.setFile_format(FileFormat.FeiDingChang.getCode());
        table_storage_info.setStorage_type(StorageType.TiHuan.getCode());
        table_storage_info.setIs_zipper(IsFlag.Fou.getCode());
        table_storage_info.setStorage_time(7L);
        table_storage_info.setHyren_name(data_store_reg.getHyren_name());
        table_storage_info.setTable_id(data_store_reg.getTable_id());
        table_storage_info.add(db);
        //初始化 Table_info (OBJ)
        Object_collect_task object_collect_task = new Object_collect_task();
        object_collect_task.setOcs_id(data_store_reg.getTable_id());
        object_collect_task.setAgent_id(loadGeneralTestData.getAgent_info().getAgent_id());
        object_collect_task.setEn_name(data_store_reg.getHyren_name());
        object_collect_task.setZh_name(data_store_reg.getOriginal_name());
        object_collect_task.setCollect_data_type(CollectDataType.XML.getCode());
        object_collect_task.setDatabase_code(DataBaseCode.UTF_8.getCode());
        object_collect_task.setUpdatetype(UpdateType.DirectUpdate.getCode());
        object_collect_task.add(db);
        //初始化 Data_extraction_def 的数据(OBJ)
        data_extraction_def = new Data_extraction_def();
        data_extraction_def.setDed_id(DED_ID + THREAD_ID + 2);
        data_extraction_def.setTable_id(data_store_reg.getTable_id());
        data_extraction_def.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
        data_extraction_def.setIs_header(IsFlag.Shi.getCode());
        data_extraction_def.setDatabase_code(DataBaseCode.UTF_8.getCode());
        data_extraction_def.setDbfile_format(FileFormat.FeiDingChang.getCode());
        data_extraction_def.setIs_archived(IsFlag.Fou.getCode());
        data_extraction_def.add(db);
        //初始化 Dq_failure_table对象的测试数据(OBJ)
        dq_failure_table = new Dq_failure_table();
        dq_failure_table.setFailure_table_id(FAILURE_TABLE_ID + THREAD_ID + 12);
        dq_failure_table.setFile_id(data_store_reg.getTable_id().toString());
        dq_failure_table.setTable_cn_name(data_store_reg.getOriginal_name());
        dq_failure_table.setTable_en_name(data_store_reg.getHyren_name());
        dq_failure_table.setTable_source(DataSourceType.DCL.getCode());
        dq_failure_table.setTable_meta_info(JsonUtil.toJson(data_store_reg));
        dq_failure_table.setDsl_id(DSL_ID);
        dq_failure_table.setData_source(StoreLayerDataSource.OBJ.getCode());
        dq_failure_table.add(db);
        dq_failure_tables.add(dq_failure_table);
        //获取DbConfBean
        DbConfBean dbConfBean = StorageLayerOperationTools.getDbConfBean(db, DSL_ID);
        //初始化对应存储层下的数表
        dq_failure_tables.forEach(dft -> {
            //设置表名
            String table_name = Constant.DQC_INVALID_TABLE + dft.getTable_en_name();
            StorageLayerOperationTools.createDataTable(db, dbConfBean, table_name);
        });
    }

    @Method(desc = "加载测试删除DML层失效表的数据", logicStep = "加载测试是删除失效表的数据")
    private void loadTestRemoveDMLDqFailureTable(DatabaseWrapper db) {
        //初始化测试DML层删除表依赖数据
        Dm_datatable dm = new Dm_datatable();
        dm.setDatatable_id(DATATABLE_ID + THREAD_ID);
        dm.setData_mart_id(DATA_MART_ID);
        dm.setDatatable_cn_name("测试集市删除表" + THREAD_ID);
        dm.setDatatable_en_name("dml_remove_table" + THREAD_ID);
        dm.setDatatable_create_date(DateUtil.getSysDate());
        dm.setDatatable_create_time(DateUtil.getSysTime());
        dm.setDatatable_due_date("99991231");
        dm.setDdlc_date(DateUtil.getSysDate());
        dm.setDdlc_time(DateUtil.getSysTime());
        dm.setDatac_date(DateUtil.getSysDate());
        dm.setDatac_time(DateUtil.getSysTime());
        dm.setDatatable_lifecycle(IsFlag.Shi.getCode());
        dm.setSoruce_size(new BigDecimal(10));
        dm.setEtl_date(DateUtil.getSysDate());
        dm.setSql_engine("3");
        dm.setStorage_type("3");
        dm.setTable_storage(IsFlag.Fou.getCode());
        dm.setRepeat_flag(IsFlag.Fou.getCode());
        dm.setCategory_id(CATEGORY_ID);
        //初始化 Dq_failure_table对象的测试数据
        Dq_failure_table dft = new Dq_failure_table();
        dft.setFailure_table_id(FAILURE_TABLE_ID + THREAD_ID + 13);
        dft.setFile_id(dm.getDatatable_id());
        dft.setTable_cn_name(dm.getDatatable_cn_name());
        dft.setTable_en_name(dm.getDatatable_en_name());
        dft.setTable_source(DataSourceType.DML.getCode());
        dft.setTable_meta_info(JsonUtil.toJson(dm));
        dft.setDsl_id(DSL_ID);
        dft.setData_source(StoreLayerDataSource.DM.getCode());
        dft.add(db);
        //获取DbConfBean
        DbConfBean dbConfBean = StorageLayerOperationTools.getDbConfBean(db, DSL_ID);
        //设置表名
        String table_name = Constant.DQC_INVALID_TABLE + dm.getDatatable_en_name();
        //初始化对应存储层下的数表
        StorageLayerOperationTools.createDataTable(db, dbConfBean, table_name);
    }

    @Method(desc = "加载测试删除DQC层失效表的数据", logicStep = "加载测试是删除失效表的数据")
    private void loadTestRemoveDQCDqFailureTable(DatabaseWrapper db) {
        //初始化测试DQC层删除表依赖数据
        Dq_index3record di3 = new Dq_index3record();
        di3.setRecord_id(RECORD_ID + THREAD_ID);
        di3.setTable_name("dqc_remove_table" + THREAD_ID);
        di3.setTable_col("id,name");
        di3.setRecord_date(DateUtil.getSysDate());
        di3.setRecord_time(DateUtil.getSysTime());
        di3.setTask_id(TASK_ID);
        di3.setDsl_id(DSL_ID);
        //初始化 Dq_failure_table对象的测试数据
        Dq_failure_table dft = new Dq_failure_table();
        dft.setFailure_table_id(FAILURE_TABLE_ID + THREAD_ID + 14);
        dft.setFile_id(di3.getRecord_id());
        dft.setTable_cn_name(di3.getTable_name());
        dft.setTable_en_name(di3.getTable_name());
        dft.setTable_source(DataSourceType.DQC.getCode());
        dft.setTable_meta_info(JsonUtil.toJson(di3));
        dft.setDsl_id(DSL_ID);
        dft.setData_source(StoreLayerDataSource.DQ.getCode());
        dft.add(db);
        //获取DbConfBean
        DbConfBean dbConfBean = StorageLayerOperationTools.getDbConfBean(db, DSL_ID);
        //设置表名
        String table_name = Constant.DQC_INVALID_TABLE + di3.getTable_name();
        //初始化对应存储层下的数表
        StorageLayerOperationTools.createDataTable(db, dbConfBean, table_name);
    }

    @Method(desc = "加载测试删除UDL层失效表的数据", logicStep = "加载测试是删除失效表的数据")
    private void loadTestRemoveUDLDqFailureTable(DatabaseWrapper db) {
        //初始化测试UDL层删除表依赖数据
        Dq_table_info dti = new Dq_table_info();
        dti.setTable_id(UDL_TABLE_ID + THREAD_ID);
        dti.setTable_space("");
        dti.setTable_name("udl_restore_table" + THREAD_ID);
        dti.setCh_name("测试自定义层恢复表" + THREAD_ID);
        dti.setCreate_date(DateUtil.getSysDate());
        dti.setEnd_date("99991231");
        dti.setIs_trace(IsFlag.Fou.getCode());
        dti.setCreate_id(USER_ID);
        //初始化 Dq_failure_table对象的测试数据
        Dq_failure_table dft = new Dq_failure_table();
        dft.setFailure_table_id(FAILURE_TABLE_ID + THREAD_ID + 15);
        dft.setFile_id(dti.getTable_id());
        dft.setTable_cn_name(dti.getCh_name());
        dft.setTable_en_name(dti.getTable_name());
        dft.setTable_source(DataSourceType.UDL.getCode());
        dft.setTable_meta_info(JsonUtil.toJson(dti));
        dft.setDsl_id(DSL_ID);
        dft.setData_source(StoreLayerDataSource.UD.getCode());
        dft.add(db);
        //获取DbConfBean
        DbConfBean dbConfBean = StorageLayerOperationTools.getDbConfBean(db, DSL_ID);
        //设置表名
        String table_name = Constant.DQC_INVALID_TABLE + dti.getTable_name();
        //初始化对应存储层下的数表
        StorageLayerOperationTools.createDataTable(db, dbConfBean, table_name);
    }

    @Method(desc = "加载测试保存DCL层元数据表的数据", logicStep = "加载测试保存元数据表的数据")
    private Data_store_reg loadTestSaveDCLMetaData(DatabaseWrapper db) {
        //初始化测试保存DCL层元数据表
        // Data_store_reg
        Data_store_reg dsr = new Data_store_reg();
        dsr.setFile_id(String.valueOf(FILE_ID + THREAD_ID + 1));
        dsr.setCollect_type(AgentType.DBWenJian.getCode());
        dsr.setOriginal_update_date(DateUtil.getSysDate());
        dsr.setOriginal_update_time(DateUtil.getSysTime());
        dsr.setTable_name("dcl_save_meta_table" + THREAD_ID);
        dsr.setOriginal_name("测试DCL保存元信息" + THREAD_ID);
        dsr.setHyren_name("dcl_save_meta_table" + THREAD_ID);
        dsr.setStorage_date(DateUtil.getSysDate());
        dsr.setStorage_time(DateUtil.getSysTime());
        dsr.setFile_size(10000L);
        dsr.setAgent_id(loadGeneralTestData.getAgent_info().getAgent_id());
        dsr.setSource_id(loadGeneralTestData.getData_source().getSource_id());
        dsr.setDatabase_id(loadGeneralTestData.getDatabase_set().getDatabase_id());
        dsr.setTable_id(TABLE_ID + THREAD_ID);
        dsr.add(db);
        // Table_info
        Table_info table_info = new Table_info();
        table_info.setTable_id(dsr.getTable_id());
        table_info.setTable_name(dsr.getTable_name());
        table_info.setTable_ch_name(dsr.getOriginal_name());
        table_info.setRec_num_date(dsr.getStorage_date());
        table_info.setDatabase_id(loadGeneralTestData.getDatabase_set().getDatabase_id());
        table_info.setValid_s_date(DateUtil.getSysDate());
        table_info.setValid_e_date("99991231");
        table_info.setIs_md5(IsFlag.Fou.getCode());
        table_info.setIs_register(IsFlag.Fou.getCode());
        table_info.setIs_customize_sql(IsFlag.Fou.getCode());
        table_info.setIs_parallel(IsFlag.Fou.getCode());
        table_info.setIs_user_defined(IsFlag.Fou.getCode());
        table_info.add(db);
        // Table_column
        Table_column table_column = new Table_column();
        table_column.setColumn_id(COLUMN_ID + THREAD_ID + 1);
        table_column.setIs_primary_key(IsFlag.Fou.getCode());
        table_column.setColumn_name("save_meta_column_name_1");
        table_column.setColumn_ch_name("save_meta_column_name_1_ch");
        table_column.setTable_id(table_info.getTable_id());
        table_column.setValid_s_date(DateUtil.getSysDate());
        table_column.setValid_e_date("99991231");
        table_column.setIs_alive(IsFlag.Fou.getCode());
        table_column.setIs_new(IsFlag.Fou.getCode());
        table_column.add(db);
        table_column = new Table_column();
        table_column.setColumn_id(COLUMN_ID + THREAD_ID + 2);
        table_column.setIs_primary_key(IsFlag.Fou.getCode());
        table_column.setColumn_name("save_meta_column_name_2");
        table_column.setColumn_ch_name("save_meta_column_name_2_ch");
        table_column.setTable_id(table_info.getTable_id());
        table_column.setValid_s_date(DateUtil.getSysDate());
        table_column.setValid_e_date("99991231");
        table_column.setIs_alive(IsFlag.Fou.getCode());
        table_column.setIs_new(IsFlag.Fou.getCode());
        table_column.add(db);
        return dsr;
    }

    @Method(desc = "加载测试保存DML层元数据表的数据", logicStep = "加载测试保存元数据表的数据")
    private Dm_datatable loadTestSaveDMLMetaData(DatabaseWrapper db) {
        //初始化测试保存DML层元数据表
        // Dm_datatable
        Dm_datatable dm = new Dm_datatable();
        dm.setDatatable_id(DATATABLE_ID + THREAD_ID);
        dm.setData_mart_id(DATA_MART_ID);
        dm.setDatatable_cn_name("测试DML保存元信息" + THREAD_ID);
        dm.setDatatable_en_name("dml_save_meta_table" + THREAD_ID);
        dm.setDatatable_create_date(DateUtil.getSysDate());
        dm.setDatatable_create_time(DateUtil.getSysTime());
        dm.setDatatable_due_date("99991231");
        dm.setDdlc_date(DateUtil.getSysDate());
        dm.setDdlc_time(DateUtil.getSysTime());
        dm.setDatac_date(DateUtil.getSysDate());
        dm.setDatac_time(DateUtil.getSysTime());
        dm.setDatatable_lifecycle(IsFlag.Shi.getCode());
        dm.setSoruce_size(new BigDecimal(10));
        dm.setEtl_date(DateUtil.getSysDate());
        dm.setSql_engine("3");
        dm.setStorage_type("3");
        dm.setTable_storage(IsFlag.Fou.getCode());
        dm.setRepeat_flag(IsFlag.Fou.getCode());
        dm.setCategory_id(CATEGORY_ID);
        dm.add(db);
        // Datatable_field_info
        Datatable_field_info dfi = new Datatable_field_info();
        dfi.setDatatable_field_id(COLUMN_ID + THREAD_ID + 1);
        dfi.setDatatable_id(dm.getDatatable_id());
        dfi.setField_cn_name("save_meta_column_name_1_cn");
        dfi.setField_en_name("save_meta_column_name_1");
        dfi.setField_type("varchar");
        dfi.setField_process(ProcessType.YingShe.getCode());
        dfi.setField_seq(1L);
        dfi.add(db);
        dfi = new Datatable_field_info();
        dfi.setDatatable_field_id(COLUMN_ID + THREAD_ID + 2);
        dfi.setDatatable_id(dm.getDatatable_id());
        dfi.setField_cn_name("save_meta_column_name_2_cn");
        dfi.setField_en_name("save_meta_column_name_2");
        dfi.setField_type("varchar");
        dfi.setField_process(ProcessType.YingShe.getCode());
        dfi.setField_seq(2L);
        dfi.add(db);
        return dm;
    }

    @Method(desc = "加载测试保存UDL层元数据表的数据", logicStep = "加载测试保存元数据表的数据")
    private Dq_table_info loadTestSaveUDLMetaData(DatabaseWrapper db) {
        //初始化测试保存UDL层元数据表
        // Dq_table_info
        Dq_table_info dti = new Dq_table_info();
        dti.setTable_id(UDL_TABLE_ID + THREAD_ID);
        dti.setTable_space("");
        dti.setTable_name("udl_save_meta_table" + THREAD_ID);
        dti.setCh_name("测试UDL保存元信息" + THREAD_ID);
        dti.setCreate_date(DateUtil.getSysDate());
        dti.setEnd_date("99991231");
        dti.setIs_trace(IsFlag.Fou.getCode());
        dti.setCreate_id(USER_ID);
        dti.add(db);
        // Dq_table_column
        Dq_table_column dtc = new Dq_table_column();
        dtc.setField_id(COLUMN_ID + THREAD_ID + 1);
        dtc.setField_ch_name("save_meta_column_name_1_cn");
        dtc.setColumn_name("save_meta_column_name_1");
        dtc.setColumn_type("int");
        dtc.setIs_null(IsFlag.Shi.getCode());
        dtc.setTable_id(dti.getTable_id());
        dtc.add(db);
        dtc = new Dq_table_column();
        dtc.setField_id(COLUMN_ID + THREAD_ID + 2);
        dtc.setField_ch_name("save_meta_column_name_2_cn");
        dtc.setColumn_name("save_meta_column_name_2");
        dtc.setColumn_type("varchar");
        dtc.setColumn_length("100");
        dtc.setIs_null(IsFlag.Shi.getCode());
        dtc.setTable_id(dti.getTable_id());
        dtc.add(db);
        return dti;
    }

    @Method(desc = "加载测试获取回收站DCL表字段信息数据", logicStep = "加载测试获取回收站表字段信息数据")
    private Dq_failure_table loadTestDCLGetDRBTableColumnInfo(DatabaseWrapper db) {
        //初始化测试获取回收站DCL表字段信息数据
        // Data_store_reg
        Data_store_reg dsr = new Data_store_reg();
        dsr.setFile_id(String.valueOf(FILE_ID + THREAD_ID));
        dsr.setCollect_type(AgentType.DBWenJian.getCode());
        dsr.setOriginal_update_date(DateUtil.getSysDate());
        dsr.setOriginal_update_time(DateUtil.getSysTime());
        dsr.setTable_name("dcl_drb_table_name" + THREAD_ID);
        dsr.setOriginal_name("dcl_drb_table_name_zh" + THREAD_ID);
        dsr.setHyren_name("dcl_save_meta_table_h" + THREAD_ID);
        dsr.setStorage_date(DateUtil.getSysDate());
        dsr.setStorage_time(DateUtil.getSysTime());
        dsr.setFile_size(10000L);
        dsr.setAgent_id(loadGeneralTestData.getAgent_info().getAgent_id());
        dsr.setSource_id(loadGeneralTestData.getData_source().getSource_id());
        dsr.setDatabase_id(loadGeneralTestData.getDatabase_set().getDatabase_id());
        dsr.setTable_id(TABLE_ID + THREAD_ID);
        // Table_info
        Table_info table_info = new Table_info();
        table_info.setTable_id(TABLE_ID + THREAD_ID);
        table_info.setTable_name(dsr.getHyren_name());
        table_info.setTable_ch_name(dsr.getOriginal_name());
        table_info.setRec_num_date(DateUtil.getSysDate());
        table_info.setDatabase_id(loadGeneralTestData.getDatabase_set().getDatabase_id());
        table_info.setValid_s_date(DateUtil.getSysDate());
        table_info.setValid_e_date("99991231");
        table_info.setIs_md5(IsFlag.Fou.getCode());
        table_info.setIs_register(IsFlag.Fou.getCode());
        table_info.setIs_customize_sql(IsFlag.Fou.getCode());
        table_info.setIs_parallel(IsFlag.Fou.getCode());
        table_info.setIs_user_defined(IsFlag.Fou.getCode());
        table_info.add(db);
        // Table_column
        Table_column table_column = new Table_column();
        table_column.setColumn_id(COLUMN_ID + THREAD_ID + 1);
        table_column.setIs_primary_key(IsFlag.Fou.getCode());
        table_column.setColumn_name("column_name_1");
        table_column.setColumn_type("int");
        table_column.setColumn_ch_name("column_name_1_ch");
        table_column.setTable_id(table_info.getTable_id());
        table_column.setValid_s_date(DateUtil.getSysDate());
        table_column.setValid_e_date("99991231");
        table_column.setIs_alive(IsFlag.Fou.getCode());
        table_column.setIs_new(IsFlag.Fou.getCode());
        table_column.add(db);
        table_column = new Table_column();
        table_column.setColumn_id(COLUMN_ID + THREAD_ID + 2);
        table_column.setIs_primary_key(IsFlag.Fou.getCode());
        table_column.setColumn_name("column_name_2");
        table_column.setColumn_type("varchar(10)");
        table_column.setColumn_ch_name("column_name_2_ch");
        table_column.setTable_id(table_info.getTable_id());
        table_column.setValid_s_date(DateUtil.getSysDate());
        table_column.setValid_e_date("99991231");
        table_column.setIs_alive(IsFlag.Fou.getCode());
        table_column.setIs_new(IsFlag.Fou.getCode());
        table_column.add(db);
        // Dq_failure_table
        Dq_failure_table dfi = new Dq_failure_table();
        dfi.setFailure_table_id(FAILURE_TABLE_ID + THREAD_ID);
        dfi.setFile_id(table_info.getTable_id());
        dfi.setTable_en_name(table_info.getTable_name());
        dfi.setTable_source(DataSourceType.DCL.getCode());
        dfi.setTable_meta_info(JsonUtil.toJson(dsr));
        dfi.setDsl_id(DSL_ID);
        dfi.setData_source(StoreLayerDataSource.DB.getCode());
        dfi.add(db);
        return dfi;
    }
}