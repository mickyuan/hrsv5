package hrds.k.biz.dm.metadatamanage;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.IsFlag;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.k.biz.dm.metadatamanage.bean.DqTableColumnBean;
import hrds.k.biz.dm.metadatamanage.bean.DqTableInfoBean;
import hrds.testbase.LoadGeneralTestData;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    //初始化通用 failure_table_id
    private static long FAILURE_TABLE_ID = PrimayKeyGener.getNextId();
    //初始化通用 file_id
    private static long FILE_ID = PrimayKeyGener.getNextId();
    //初始化通用 table_id
    private static long TABLE_ID = PrimayKeyGener.getNextId();
    //初始化通用 datatable_id
    private static long DATATABLE_ID = PrimayKeyGener.getNextId();


    @Method(desc = "初始化测试用例依赖表数据", logicStep = "初始化测试用例依赖表数据")
    @Before
    public void before() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
//            //初始化通用存储层数据
//            List<Data_store_layer> data_store_layers = loadGeneralTestData.getData_store_layers();
//            data_store_layers.forEach(data_store_layer -> data_store_layer.add(db));
//            //初始化通用存储层配置信息
//            loadGeneralTestData.getData_store_layer_attrs().forEach(data_store_layer_attr -> data_store_layer_attr.add(db));
//            //初始化 Data_source
//            loadGeneralTestData.getData_source().add(db);
//            //初始化 Agent_info
//            loadGeneralTestData.getAgent_info().add(db);
//            //初始化 Database_set
//            loadGeneralTestData.getDatabase_set().add(db);
//            //初始化 Dq_failure_table, 该表的mete字段信息由Data_store_reg实体生成
//            // 初始化测试DCL层恢复表依赖数据
//            Data_store_reg data_store_reg = new Data_store_reg();
//            data_store_reg.setFile_id(String.valueOf(FILE_ID + THREAD_ID));
//            data_store_reg.setCollect_type(AgentType.DBWenJian.getCode());
//            data_store_reg.setOriginal_update_date(DateUtil.getSysDate());
//            data_store_reg.setOriginal_update_time(DateUtil.getSysTime());
//            data_store_reg.setOriginal_name("dcl_restore_table" + THREAD_ID);
//            data_store_reg.setHyren_name("hyren_test_restore_table" + THREAD_ID);
//            data_store_reg.setStorage_date(DateUtil.getSysDate());
//            data_store_reg.setStorage_time(DateUtil.getSysTime());
//            data_store_reg.setFile_size(10000L);
//            data_store_reg.setAgent_id(loadGeneralTestData.getAgent_info().getAgent_id());
//            data_store_reg.setSource_id(loadGeneralTestData.getData_source().getSource_id());
//            data_store_reg.setDatabase_id(loadGeneralTestData.getDatabase_set().getDatabase_id());
//            data_store_reg.setTable_id(TABLE_ID + THREAD_ID);
//            Dq_failure_table dq_failure_table = new Dq_failure_table();
//            dq_failure_table.setFailure_table_id(FAILURE_TABLE_ID + THREAD_ID + 1);
//            dq_failure_table.setFile_id(data_store_reg.getTable_id().toString());
//            dq_failure_table.setTable_cn_name(data_store_reg.getOriginal_name());
//            dq_failure_table.setTable_en_name(data_store_reg.getHyren_name());
//            dq_failure_table.setTable_source(DataSourceType.DCL.getCode());
//            dq_failure_table.setTable_meta_info(JsonUtil.toJson(data_store_reg));
//            //remark存储的是该表存储的数据层id,多个以","分隔
//            dq_failure_table.setRemark(data_store_layers.get(0).getDsl_id().toString());
//            dq_failure_table.add(db);
//            // 初始化测试DML层回复表依赖数据
//            Dm_datatable dm_datatable = new Dm_datatable();
//            dm_datatable.setDatatable_id(DATATABLE_ID + THREAD_ID);
//            dm_datatable.setDatatable_cn_name("dml_restore_table" + THREAD_ID);
//            //提交所有数据库执行操作
//            SqlOperator.commitTransaction(db);
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
//            //清理通用存储层数据 Data_store_layer
//            loadGeneralTestData.getData_store_layers().forEach(data_store_layer -> {
//                SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id=?",
//                        data_store_layer.getDsl_id());
//                long num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer.TableName + " where" +
//                        " dsl_id=?", data_store_layer.getDsl_id()).orElseThrow(() -> new RuntimeException("count fail!"));
//                assertThat("Data_store_layer 表此条数据删除后,记录数应该为0", num, is(0L));
//            });
//            //清理通用存储层配置数据 Data_store_layer_attr
//            loadGeneralTestData.getData_store_layer_attrs().forEach(data_store_layer_attr -> {
//                SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName + " where dsl_id=?" +
//                                " and storage_property_key=?", data_store_layer_attr.getDsl_id(),
//                        data_store_layer_attr.getStorage_property_key());
//                long num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_attr.TableName +
//                                " where dsl_id=? and storage_property_key=?", data_store_layer_attr.getDsl_id(),
//                        data_store_layer_attr.getStorage_property_key()).orElseThrow(() -> new RuntimeException("count fail!"));
//                assertThat("Data_store_layer_attr 表此条数据删除后,记录数应该为0", num, is(0L));
//            });
//            //清理 Data_source
//            Data_source data_source = loadGeneralTestData.getData_source();
//            if (null != data_source) {
//                SqlOperator.execute(db, "delete from " + Data_source.TableName + " where source_id=?",
//                        data_source.getSource_id());
//                long num = SqlOperator.queryNumber(db, "select count(1) from " + Data_source.TableName + " where" +
//                        " source_id=?", data_source.getSource_id()).orElseThrow(() -> new RuntimeException("count fail!"));
//                assertThat("Data_source 表此条数据删除后,记录数应该为0", num, is(0L));
//            }
//            //清理 Agent_info
//            Agent_info agent_info = loadGeneralTestData.getAgent_info();
//            if (null != agent_info) {
//                SqlOperator.execute(db, "delete from " + Agent_info.TableName + " where agent_id=?",
//                        agent_info.getAgent_id());
//                long num = SqlOperator.queryNumber(db, "select count(1) from " + Agent_info.TableName + " where" +
//                        " agent_id=?", agent_info.getAgent_id()).orElseThrow(() -> new RuntimeException("count fail!"));
//                assertThat("Agent_info 表此条数据删除后,记录数应该为0", num, is(0L));
//            }
//            //清理 Database_set
//            Database_set database_set = loadGeneralTestData.getDatabase_set();
//            if (null != database_set) {
//                SqlOperator.execute(db, "delete from " + Database_set.TableName + " where database_id=?",
//                        database_set.getDatabase_id());
//                long num = SqlOperator.queryNumber(db, "select count(1) from " + Database_set.TableName + " where" +
//                        " database_id=?", database_set.getDatabase_id()).orElseThrow(() -> new RuntimeException("count fail!"));
//                assertThat("Database_set 表此条数据删除后,记录数应该为0", num, is(0L));
//            }
//            //清理 Dq_failure_table
//            SqlOperator.execute(db, "delete from " + Dq_failure_table.TableName + " where failure_table_id=?",
//                    FAILURE_TABLE_ID + 1);
//            SqlOperator.execute(db, "delete from " + Dq_failure_table.TableName + " where failure_table_id=?",
//                    FAILURE_TABLE_ID + 2);
//            long num = SqlOperator.queryNumber(db, "select count(1) from " + Dq_failure_table.TableName + " where" +
//                    " failure_table_id in (?,?)", FAILURE_TABLE_ID + 1, FAILURE_TABLE_ID + 2)
//                    .orElseThrow(() -> new RuntimeException("count  fail!"));
//            assertThat("Dq_rule_def 表此条数据删除后,记录数应该为0", num, is(0L));
            //提交数据库操作
            db.commit();
        }
    }

    @Test
    public void getMDMTreeData() {
//        String bodyString;
//        ActionResult ar;
//        //获取元数据管理树节点数据
//        bodyString = new HttpClient()
//                .post(getActionUrl("getMDMTreeData")).getBodyString();
//        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
//                "获取返回的ActionResult信息失败!"));
//        assertThat(ar.isSuccess(), is(true));
    }

    @Test
    public void getDRBTreeData() {
//        String bodyString;
//        ActionResult ar;
//        //获取元数据管理树节点数据
//        bodyString = new HttpClient()
//                .post(getActionUrl("getDRBTreeData")).getBodyString();
//        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
//                "获取返回的ActionResult信息失败!"));
//        assertThat(ar.isSuccess(), is(true));
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
    }

    @Test
    public void tableSetToInvalid() {
    }

    @Test
    public void removeCompletelyTable() {
    }

    @Method(desc = "根据存储层id获取存储层配置信息", logicStep = "根据存储层id获取存储层配置信息")
    @Test
    public void getStorageLayerConfInfo() {
        long dsl_id = 728646839099719680L;
        String bodyString = new HttpClient()
                .addData("dsl_id", dsl_id)
                .post(getActionUrl("getStorageLayerConfInfo")).getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                -> new BusinessException("获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(true));
    }

    @Method(desc = "数据管控-创建表", logicStep = "数据管控-创建表")
    @Test
    public void createTable() {
        //获取配置的通用存储层id
        Long dsl_id = loadGeneralTestData.getData_store_layers().get(0).getDsl_id();
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
                .addData("dsl_id", dsl_id)
                .addData("dqTableInfoBean", dqTableInfoBean)
                .addData("dqTableColumnBeans", JsonUtil.toJson(dqTableColumnBeans))
                .post(getActionUrl("createTable")).getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                -> new BusinessException("获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        //2 错误数据创建,存储层id为空
        bodyString = new HttpClient()
                .addData("dqTableInfoBean", dqTableInfoBean)
                .addData("dqTableColumnBeans", JsonUtil.toJson(dqTableColumnBeans))
                .post(getActionUrl("createTable")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                -> new BusinessException("获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(false));
        //3 错误数据创建,自定义表实体Bean为空
        bodyString = new HttpClient()
                .addData("dsl_id", dsl_id)
                .addData("dqTableColumnBeans", JsonUtil.toJson(dqTableColumnBeans))
                .post(getActionUrl("createTable")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                -> new BusinessException("获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(false));
        //4 错误数据创建,自定义表字段实体Bean[]为空
        bodyString = new HttpClient()
                .addData("dsl_id", dsl_id)
                .addData("dqTableInfoBean", dqTableInfoBean)
                .post(getActionUrl("createTable")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
                -> new BusinessException("获取返回的ActionResult信息失败!"));
        assertThat(ar.isSuccess(), is(false));
    }
}