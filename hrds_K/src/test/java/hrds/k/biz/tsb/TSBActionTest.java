package hrds.k.biz.tsb;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.DbmMode;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TSBActionTest extends WebBaseTestCase {
    //测试数据的用户ID
    private static final long USER_ID = -1000L;
    //测试数据的部门ID
    private static final long DEP_ID = -1000L;
    //测试数据的数据源ID
    private static final long SOURCE_ID = -1000L;
    //测试数据的AGENT_ID
    private static final long AGENT_ID = -1000L;
    //测试数据的文件采集任务初始化ID，用来初始化数据
    private static final long FCS_ID = -1000L;
    //测试数据的TABLE_ID
    private static final long TABLE_ID = -1000L;
    //测试数据的文件源属性ID
    private static final String FILE_ID = "-1000";
    //检测记录id
    private static final String DETECT_ID = "-1000L";
    //检测表id
    private static final long DBM_TABLE_ID = -1000L;
    //检测表字段id
    private static final long COL_ID = -1000L;
    //检测表结果id
    private static final String RESULT_ID = "-1000L";


    private static String bodyString;
    private static ActionResult ar;

    @Method(desc = "初始化测试用例依赖表数据",
            logicStep = "初始化测试用例依赖表数据"
    )
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
            hrds.commons.entity.Data_source dataSource = new Data_source();
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
            //初始化 Agent_info 数据
            Agent_info agentInfo = new Agent_info();
            agentInfo.setAgent_id(AGENT_ID);
            agentInfo.setAgent_name("测试查询Agent-hll");
            agentInfo.setAgent_type(AgentType.ShuJuKu.getCode());
            agentInfo.setAgent_ip("127.0.0.1");
            agentInfo.setAgent_port("8888");
            agentInfo.setAgent_status("1");
            agentInfo.setCreate_date(DateUtil.getSysDate());
            agentInfo.setCreate_time(DateUtil.getSysTime());
            agentInfo.setSource_id(SOURCE_ID);
            agentInfo.setUser_id(USER_ID);
            agentInfo.add(db);
            //初始化 Source_file_attribute 数据
            Source_file_attribute sourceFileAttribute = new Source_file_attribute();
            sourceFileAttribute.setFile_id(FILE_ID);
            sourceFileAttribute.setIs_in_hbase("0");
            sourceFileAttribute.setSeqencing(0L);
            sourceFileAttribute.setCollect_type(AgentType.ShuJuKu.getCode());
            sourceFileAttribute.setOriginal_name("init-hll");
            sourceFileAttribute.setOriginal_update_date(DateUtil.getSysDate());
            sourceFileAttribute.setOriginal_update_time(DateUtil.getSysTime());
            sourceFileAttribute.setTable_name("tsb_name_hll_test");
            sourceFileAttribute.setHbase_name("init-hll");
            sourceFileAttribute.setMeta_info("init-hll");
            sourceFileAttribute.setStorage_date(DateUtil.getSysDate());
            sourceFileAttribute.setStorage_time(DateUtil.getSysTime());
            sourceFileAttribute.setFile_size(1024L);
            sourceFileAttribute.setFile_type("");
            sourceFileAttribute.setFile_suffix("init-hll");
            sourceFileAttribute.setSource_path("init-hll");
            sourceFileAttribute.setFile_md5("init-hll");
            sourceFileAttribute.setFile_avro_path("init-hll");
            sourceFileAttribute.setFile_avro_block(1024L);
            sourceFileAttribute.setIs_big_file("0");
            sourceFileAttribute.setIs_cache("0");
            sourceFileAttribute.setFolder_id(10L);
            sourceFileAttribute.setAgent_id(AGENT_ID);
            sourceFileAttribute.setSource_id(SOURCE_ID);
            sourceFileAttribute.setCollect_set_id(FCS_ID);
            sourceFileAttribute.add(db);
            //初始化 Table_info 数据
            Table_info table_info = new Table_info();
            table_info.setTable_id(TABLE_ID);
            table_info.setTable_name("tsb_name_hll_test");
            table_info.setTable_ch_name("表结构对标测试hll");
            table_info.setDatabase_id(FCS_ID);
            table_info.setValid_s_date("20200202");
            table_info.setValid_e_date("99991231");
            table_info.setIs_user_defined(IsFlag.Fou.getCode());
            table_info.setIs_md5(IsFlag.Shi.getCode());
            table_info.setIs_register(IsFlag.Fou.getCode());
            table_info.setIs_parallel(IsFlag.Fou.getCode());
            table_info.add(db);
            //初始化 Table_column 数据
            Table_column table_column = new Table_column();
            table_column.setColumn_id(-1000L);
            table_column.setIs_get(IsFlag.Shi.getCode());
            table_column.setIs_primary_key(IsFlag.Fou.getCode());
            table_column.setColumn_name("id");
            table_column.setColumn_type("int8");
            table_column.setColumn_ch_name("id");
            table_column.setTable_id(TABLE_ID);
            table_column.setValid_s_date("20200202");
            table_column.setValid_e_date("99991231");
            table_column.setIs_alive(IsFlag.Shi.getCode());
            table_column.setIs_new(IsFlag.Fou.getCode());
            table_column.add(db);
            table_column.setColumn_id(-1001L);
            table_column.setIs_get(IsFlag.Shi.getCode());
            table_column.setIs_primary_key(IsFlag.Fou.getCode());
            table_column.setColumn_name("name");
            table_column.setColumn_type("varchar(512)");
            table_column.setColumn_ch_name("姓名");
            table_column.setTable_id(TABLE_ID);
            table_column.setValid_s_date("20200202");
            table_column.setValid_e_date("99991231");
            table_column.setIs_alive(IsFlag.Shi.getCode());
            table_column.setIs_new(IsFlag.Fou.getCode());
            table_column.add(db);
            table_column.setColumn_id(-1002L);
            table_column.setIs_get(IsFlag.Shi.getCode());
            table_column.setIs_primary_key(IsFlag.Fou.getCode());
            table_column.setColumn_name("height");
            table_column.setColumn_type("numeric(10,0)");
            table_column.setColumn_ch_name("身高");
            table_column.setTable_id(TABLE_ID);
            table_column.setValid_s_date("20200202");
            table_column.setValid_e_date("99991231");
            table_column.setIs_alive(IsFlag.Shi.getCode());
            table_column.setIs_new(IsFlag.Fou.getCode());
            table_column.add(db);
            //初始化检测记录表信息
            Dbm_normbm_detect dbm_normbm_detect = new Dbm_normbm_detect();
            dbm_normbm_detect.setDetect_id(DETECT_ID);
            dbm_normbm_detect.setDetect_name("-1000L");
            dbm_normbm_detect.setSource_type("DCL");
            dbm_normbm_detect.setIs_import(IsFlag.Fou.getCode());
            dbm_normbm_detect.setDetect_status(IsFlag.Shi.getCode());
            dbm_normbm_detect.setDbm_mode(DbmMode.BiaoJieGouDuiBiao.getCode());
            dbm_normbm_detect.setCreate_user("-1000L");
            dbm_normbm_detect.setDetect_sdate(DateUtil.getSysDate());
            dbm_normbm_detect.setDetect_stime(DateUtil.getSysTime());
            dbm_normbm_detect.setDetect_edate(DateUtil.getSysDate());
            dbm_normbm_detect.setDetect_etime(DateUtil.getSysTime());
            dbm_normbm_detect.add(db);
            //初始化检测表数据
            Dbm_dtable_info dbm_dtable_info = new Dbm_dtable_info();
            dbm_dtable_info.setDbm_tableid(DBM_TABLE_ID);
            dbm_dtable_info.setTable_cname("-1000L");
            dbm_dtable_info.setTable_ename("-1000L");
            dbm_dtable_info.setIs_external(IsFlag.Fou.getCode());
            dbm_dtable_info.setTable_remark("-1000L");
            dbm_dtable_info.setDetect_id(DETECT_ID);
            dbm_dtable_info.setTable_id(TABLE_ID);
            dbm_dtable_info.setSource_id(SOURCE_ID);
            dbm_dtable_info.setAgent_id(AGENT_ID);
            dbm_dtable_info.setDatabase_id(FCS_ID);
            dbm_dtable_info.add(db);
            //初始化检测表字段信息
            Dbm_dtcol_info dbm_dtcol_info = new Dbm_dtcol_info();
            dbm_dtcol_info.setCol_id(COL_ID);
            dbm_dtcol_info.setCol_cname("-1000L");
            dbm_dtcol_info.setCol_ename("-1000L");
            dbm_dtcol_info.setCol_remark("-1000L");
            dbm_dtcol_info.setData_type("varchar");
            dbm_dtcol_info.setData_len(10L);
            dbm_dtcol_info.setDecimal_point(2L);
            dbm_dtcol_info.setIs_key(IsFlag.Fou.getCode());
            dbm_dtcol_info.setIs_null(IsFlag.Shi.getCode());
            dbm_dtcol_info.setDbm_tableid(DBM_TABLE_ID);
            dbm_dtcol_info.setDetect_id(DETECT_ID);
            dbm_dtcol_info.setColumn_id(COL_ID);
            dbm_dtcol_info.setDatabase_id(FCS_ID);
            dbm_dtcol_info.setAgent_id(AGENT_ID);
            dbm_dtcol_info.setSource_id(SOURCE_ID);
            dbm_dtcol_info.add(db);
            //初始化检测结果信息
            Dbm_normbmd_result dbm_normbmd_result = new Dbm_normbmd_result();
            dbm_normbmd_result.setResult_id(RESULT_ID);
            dbm_normbmd_result.setCol_similarity(new BigDecimal("0.99"));
            dbm_normbmd_result.setRemark_similarity(new BigDecimal("0.99"));
            dbm_normbmd_result.setDetect_id(DETECT_ID);
            dbm_normbmd_result.setCol_id(COL_ID);
            dbm_normbmd_result.setBasic_id(-1000L);
            dbm_normbmd_result.setIs_artificial(IsFlag.Shi.getCode());
            dbm_normbmd_result.setIs_tag(IsFlag.Shi.getCode());
            dbm_normbmd_result.add(db);
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
            //删除 Agent_info 表测试数据
            SqlOperator.execute(db, "delete from " + Agent_info.TableName + " where agent_id=?", AGENT_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Agent_info.TableName + " where agent_id=?",
                    AGENT_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("agent_info 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Source_file_attribute 表测试数据
            SqlOperator.execute(db, "delete from " + Source_file_attribute.TableName + " where file_id=?", FILE_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Source_file_attribute.TableName +
                    " where agent_id=?", AGENT_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Source_file_attribute 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Table_info 表测试数据
            SqlOperator.execute(db, "delete from " + Table_info.TableName + " where table_id=?", TABLE_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName +
                    " where table_id=?", TABLE_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Table_info 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Table_column 表测试数据
            SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id=?", TABLE_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName +
                    " where table_id=?", TABLE_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Table_column 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Dbm_normbm_detect 表测试数据
            SqlOperator.execute(db, "delete from " + Dbm_normbm_detect.TableName + " where detect_id=?", DETECT_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_normbm_detect.TableName +
                    " where detect_id=?", DETECT_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_normbm_detect 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Dbm_dtable_info 表测试数据
            SqlOperator.execute(db, "delete from " + Dbm_dtable_info.TableName + " where dbm_tableid=?", DBM_TABLE_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_dtable_info.TableName +
                    " where dbm_tableid=?", DBM_TABLE_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_dtable_info 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Dbm_dtcol_info 表测试数据
            SqlOperator.execute(db, "delete from " + Dbm_dtcol_info.TableName + " where col_id=?", COL_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_dtcol_info.TableName +
                    " where col_id=?", COL_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_dtcol_info 表此条数据删除后,记录数应该为0", num, is(0L));
            //删除 Dbm_normbmd_result 表测试数据
            SqlOperator.execute(db, "delete from " + Dbm_normbmd_result.TableName + " where result_id=?", RESULT_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_normbmd_result.TableName +
                    " where result_id=?", RESULT_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_normbmd_result 表此条数据删除后,记录数应该为0", num, is(0L));
        }
    }

    @Method(desc = "获取树菜单测试方法",
            logicStep = "获取树菜单测试方法")
    @Test
    public void getTSBTreeData() {
        bodyString = new HttpClient()
                .addData("tree_source", "dataBenchmarking")
                .post(getActionUrl("getTSBTreeData")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取树信息失败!"));
        assertThat(ar.isSuccess(), is(true));
    }

    @Method(desc = "获取表字段信息列表测试方法",
            logicStep = "获取表字段信息列表")
    @Test
    public void getColumnByFileId() {
        bodyString = new HttpClient()
                .addData("data_layer", "DCL")
                .addData("table_type", "01")
                .addData("file_id", "-1000")
                .post(getActionUrl("getColumnByFileId")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
                new BusinessException("获取表字段信息失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForResult().getRowCount(), is(3));
    }

    @Method(desc = "请求对标接口测试方法",
            logicStep = "请求对标接口测试方法(因为要调用对标接口,测试用例无法实现)")
    @Test
    public void predictBenchmarking() {

    }

    @Method(desc = "获取预测结果信息测试方法",
            logicStep = "获取预测结果信息测试方法(依赖 predictBenchmarking())")
    @Test
    public void getPredictResult() {
    }

    @Method(desc = "获取预测结果信息测试方法",
            logicStep = "获取预测结果信息测试方法(依赖 getPredictResult())")
    @Test
    public void saveTSBConfData() {
    }
}
