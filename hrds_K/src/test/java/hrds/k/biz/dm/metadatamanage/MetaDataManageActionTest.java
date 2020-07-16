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
            //清理 Dq_failure_table
            //清理测试DCL层恢复表依赖数据
            SqlOperator.execute(db, "delete from " + Dq_failure_table.TableName + " where failure_table_id=?",
                    FAILURE_TABLE_ID + 1);
            //清理测试DML层回复表依赖数据
            SqlOperator.execute(db, "delete from " + Dq_failure_table.TableName + " where failure_table_id=?",
                    FAILURE_TABLE_ID + 2);
            long num = SqlOperator.queryNumber(db, "select count(1) from " + Dq_failure_table.TableName + " where" +
                    " failure_table_id in (?,?)", FAILURE_TABLE_ID + 1, FAILURE_TABLE_ID + 2)
                    .orElseThrow(() -> new RuntimeException("count  fail!"));
            assertThat("Dq_rule_def 表此条数据删除后,记录数应该为0", num, is(0L));
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

    @Test
    public void getDRBTableColumnInfo() {
    }

    @Test
    public void saveMetaData() {
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
            StorageLayerOperationTools.cleanUpDataTable(db, dbConfBean, data_store_reg.getHyren_name());
            dm_datatable.delete(db);
            StorageLayerOperationTools.cleanUpDataTable(db, dbConfBean, dm_datatable.getDatatable_en_name());
            dq_index3record.delete(db);
            StorageLayerOperationTools.cleanUpDataTable(db, dbConfBean, dq_index3record.getTable_name());
            dq_table_info.delete(db);
            StorageLayerOperationTools.cleanUpDataTable(db, dbConfBean, dq_table_info.getTable_name());
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
            List<Dq_failure_table> dcl_dfts = loadTestRemoveDCLDqFailureTable(db);
            //加载测试彻底删除DML层的表数据

            // 获取DbConfBean
            DbConfBean dbConfBean = StorageLayerOperationTools.getDbConfBean(db, DSL_ID);
            //清理接口产生的表数据和对应存储层下的数表
            dcl_dfts.forEach(dq_failure_table -> {
                dq_failure_table.delete(db);
                String table_name = Constant.DQC_INVALID_TABLE + dq_failure_table.getTable_en_name();
                StorageLayerOperationTools.cleanUpDataTable(db, dbConfBean, table_name);
            });

        }
    }

    @Method(desc = "根据存储层id获取存储层配置信息", logicStep = "根据存储层id获取存储层配置信息")
    @Test
    public void getStorageLayerConfInfo() {
        String bodyString = new HttpClient()
                .addData("dsl_id", DSL_ID)
                .post(getActionUrl("getStorageLayerConfInfo")).getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                -> new BusinessException("获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(true));
    }

    @Method(desc = "数据管控-创建表", logicStep = "数据管控-创建表")
    @Test
    public void createTable() {
        //设置创建表信息
        DqTableInfoBean dqTableInfoBean = new DqTableInfoBean();
        dqTableInfoBean.setTable_name("test_create_table_" + THREAD_ID);
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
                    "test_create_table_" + THREAD_ID).orElseThrow(() -> (new BusinessException("统计sql执行出错!")));
            assertThat(dti.getTable_name(), is("test_create_table_" + THREAD_ID));
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
        dm.setData_mart_id(DATA_MART_ID + THREAD_ID);
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

    private List<Dq_failure_table> loadTestRemoveDCLDqFailureTable(DatabaseWrapper db) {
        //初始化返回的结果集对象
        List<Dq_failure_table> dfts = new ArrayList<>();
        //初始化测试DCL层删除(DB)表依赖数据
        Data_store_reg dsr = new Data_store_reg();
        dsr.setFile_id(String.valueOf(FILE_ID + THREAD_ID + 1));
        dsr.setCollect_type(AgentType.DBWenJian.getCode());
        dsr.setOriginal_update_date(DateUtil.getSysDate());
        dsr.setOriginal_update_time(DateUtil.getSysTime());
        dsr.setOriginal_name("测试贴源层删除DB表" + THREAD_ID);
        dsr.setHyren_name("dcl_remove_db_table" + THREAD_ID);
        dsr.setStorage_date(DateUtil.getSysDate());
        dsr.setStorage_time(DateUtil.getSysTime());
        dsr.setFile_size(10000L);
        dsr.setAgent_id(loadGeneralTestData.getAgent_info().getAgent_id());
        dsr.setSource_id(loadGeneralTestData.getData_source().getSource_id());
        dsr.setDatabase_id(loadGeneralTestData.getDatabase_set().getDatabase_id());
        dsr.setTable_id(TABLE_ID + 1);
        //初始化 Dq_failure_table对象的测试数据(DB)
        Dq_failure_table dft = new Dq_failure_table();
        dft.setFailure_table_id(FAILURE_TABLE_ID + +THREAD_ID + 11);
        dft.setFile_id(dsr.getTable_id().toString());
        dft.setTable_cn_name(dsr.getOriginal_name());
        dft.setTable_en_name(dsr.getHyren_name());
        dft.setTable_source(DataSourceType.DCL.getCode());
        dft.setTable_meta_info(JsonUtil.toJson(dsr));
        dft.setDsl_id(DSL_ID);
        dft.setData_source(StoreLayerDataSource.DB.getCode());
        dft.add(db);
        dfts.add(dft);
        //初始化测试DCL层删除(OBJ)表依赖数据
        dsr = new Data_store_reg();
        dsr.setFile_id(String.valueOf(FILE_ID + THREAD_ID + 2));
        dsr.setCollect_type(AgentType.DuiXiang.getCode());
        dsr.setOriginal_update_date(DateUtil.getSysDate());
        dsr.setOriginal_update_time(DateUtil.getSysTime());
        dsr.setOriginal_name("测试贴源层删除OBJ表" + THREAD_ID);
        dsr.setHyren_name("dcl_remove_obj_table" + THREAD_ID);
        dsr.setStorage_date(DateUtil.getSysDate());
        dsr.setStorage_time(DateUtil.getSysTime());
        dsr.setFile_size(10000L);
        dsr.setAgent_id(loadGeneralTestData.getAgent_info().getAgent_id());
        dsr.setSource_id(loadGeneralTestData.getData_source().getSource_id());
        dsr.setDatabase_id(loadGeneralTestData.getDatabase_set().getDatabase_id());
        dsr.setTable_id(TABLE_ID + 2);
        //初始化 Dq_failure_table对象的测试数据(DB)
        dft = new Dq_failure_table();
        dft.setFailure_table_id(FAILURE_TABLE_ID + THREAD_ID + 12);
        dft.setFile_id(dsr.getTable_id().toString());
        dft.setTable_cn_name(dsr.getOriginal_name());
        dft.setTable_en_name(dsr.getHyren_name());
        dft.setTable_source(DataSourceType.DCL.getCode());
        dft.setTable_meta_info(JsonUtil.toJson(dsr));
        dft.setDsl_id(DSL_ID);
        dft.setData_source(StoreLayerDataSource.OBJ.getCode());
        dft.add(db);
        dfts.add(dft);
        //获取DbConfBean
        DbConfBean dbConfBean = StorageLayerOperationTools.getDbConfBean(db, DSL_ID);
        //初始化对应存储层下的数表
        dfts.forEach(dq_failure_table -> {
            //设置表名
            String table_name = Constant.DQC_INVALID_TABLE + dq_failure_table.getTable_en_name();
            StorageLayerOperationTools.createDataTable(db, dbConfBean, table_name);
        });
        //返回初始化的对象信息
        return dfts;
    }
}