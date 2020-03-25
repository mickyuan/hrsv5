package hrds.k.biz.dbm.codeiteminfo;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.entity.*;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "代码项管理测试类", author = "BY-HLL", createdate = "2020/3/11 0011 下午 03:23")
public class DbmCodeItemInfoActionTest extends WebBaseTestCase {

    //测试数据的用户ID
    private static final long USER_ID = -1000L;
    //测试数据的部门ID
    private static final long DEP_ID = -1000000001L;

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
            //初始化 Dbm_code_type_info 数据
            Dbm_code_type_info dbm_code_type_info = new Dbm_code_type_info();
            dbm_code_type_info.setCode_type_id(-1000L);
            dbm_code_type_info.setCode_type_name("测试代码类hll");
            dbm_code_type_info.setCode_status("1");
            dbm_code_type_info.setCreate_user("测试代码类hll");
            dbm_code_type_info.setCreate_date(DateUtil.getSysDate());
            dbm_code_type_info.setCreate_time(DateUtil.getSysTime());
            dbm_code_type_info.add(db);
            //初始化 Dbm_code_item_info 数据
            Dbm_code_item_info dbm_code_item_info = new Dbm_code_item_info();
            // 测试删除数据
            dbm_code_item_info.setCode_item_id(-1000L);
            dbm_code_item_info.setCode_item_name("测试删除代码项hll");
            dbm_code_item_info.setDbm_level("测试删除代码项hll");
            dbm_code_item_info.setCode_type_id(-1000L);
            dbm_code_item_info.add(db);
            // 测试修改数据
            dbm_code_item_info.setCode_item_id(-1001L);
            dbm_code_item_info.setCode_item_name("测试修改代码项hll");
            dbm_code_item_info.setDbm_level("测试修改代码项hll");
            dbm_code_item_info.setCode_type_id(-1000L);
            dbm_code_item_info.add(db);
            // 测试查询数据
            dbm_code_item_info.setCode_item_id(-1002L);
            dbm_code_item_info.setCode_item_name("测试查询代码项hll");
            dbm_code_item_info.setDbm_level("测试查询代码项hll");
            dbm_code_item_info.setCode_type_id(-1000L);
            dbm_code_item_info.add(db);
            // 测试批量删除
            dbm_code_item_info.setCode_item_id(-2000L);
            dbm_code_item_info.setCode_item_name("测试批量删除hll-2000");
            dbm_code_item_info.setDbm_level("测试查询代码项hll");
            dbm_code_item_info.setCode_type_id(-1001L);
            dbm_code_item_info.add(db);
            dbm_code_item_info.setCode_item_id(-2001L);
            dbm_code_item_info.setCode_item_name("测试批量删除hll-2001");
            dbm_code_item_info.setDbm_level("测试查询代码项hll");
            dbm_code_item_info.setCode_type_id(-1001L);
            dbm_code_item_info.add(db);
            dbm_code_item_info.setCode_item_id(-2002L);
            dbm_code_item_info.setCode_item_name("测试批量删除hll-2002");
            dbm_code_item_info.setDbm_level("测试查询代码项hll");
            dbm_code_item_info.setCode_type_id(-1001L);
            dbm_code_item_info.add(db);
            // 测试检索
            dbm_code_item_info.setCode_item_id(-3001L);
            dbm_code_item_info.setCode_item_name("测试检索hll-3001");
            dbm_code_item_info.setDbm_level("测试查询代码项hll");
            dbm_code_item_info.setCode_type_id(-1001L);
            dbm_code_item_info.add(db);
            dbm_code_item_info.setCode_item_id(-3002L);
            dbm_code_item_info.setCode_item_name("测试检索hll-3002");
            dbm_code_item_info.setDbm_level("测试查询代码项hll");
            dbm_code_item_info.setCode_type_id(-1001L);
            dbm_code_item_info.add(db);
            //提交所有数据库执行操作
            SqlOperator.commitTransaction(db);
            //根据初始化的 Sys_user 用户模拟登陆
            bodyString = new HttpClient()
                    .addData("user_id", USER_ID)
                    .addData("password", "111111")
                    .post("http://127.0.0.1:8888/A/action/hrds/a/biz/login/login").getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
            if (ar != null) {
                assertThat(ar.isSuccess(), is(true));
            }
        }
    }

    @Method(desc = "测试案例执行完成后清理测试数据",
            logicStep = "测试案例执行完成后清理测试数据")
    @AfterClass
    public static void after() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //删除 Sys_user 表测试数据
            SqlOperator.execute(db,
                    "delete from " + Sys_user.TableName + " where user_id=?", USER_ID);
            SqlOperator.commitTransaction(db);
            long suNum = SqlOperator.queryNumber(db,
                    "select count(1) from " + Sys_user.TableName + " where user_id =?",
                    USER_ID
            ).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("sys_user 表此条数据删除后,记录数应该为0", suNum, is(0L));
            //删除 Department_info 表测试数据
            SqlOperator.execute(db,
                    "delete from " + Department_info.TableName + " where dep_id=?", DEP_ID);
            SqlOperator.commitTransaction(db);
            long depNum = SqlOperator.queryNumber(db,
                    "select count(1) from " + Department_info.TableName + " where dep_id =?",
                    DEP_ID
            ).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("department_info 表此条数据删除后,记录数应该为0", depNum, is(0L));
            //删除 Dbm_code_type_info 数据
            SqlOperator.execute(db, "delete from " + Dbm_code_type_info.TableName + " where code_type_id=?",
                    -1000L);
            SqlOperator.commitTransaction(db);
            long typeNum = SqlOperator.queryNumber(db,
                    "select count(1) from " + Dbm_code_type_info.TableName + " where code_type_id =?",
                    -1000L
            ).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("department_info 表此条数据删除后,记录数应该为0", typeNum, is(0L));
            //删除 Dbm_code_item_info 数据
            SqlOperator.execute(db, "delete from " + Dbm_code_item_info.TableName + " where code_item_id=?",
                    -1000L);
            SqlOperator.execute(db, "delete from " + Dbm_code_item_info.TableName + " where code_item_id=?",
                    -1001L);
            SqlOperator.execute(db, "delete from " + Dbm_code_item_info.TableName + " where code_item_id=?",
                    -1002L);
            SqlOperator.execute(db, "delete from " + Dbm_code_item_info.TableName + " where code_item_id=?",
                    -2000L);
            SqlOperator.execute(db, "delete from " + Dbm_code_item_info.TableName + " where code_item_id=?",
                    -2001L);
            SqlOperator.execute(db, "delete from " + Dbm_code_item_info.TableName + " where code_item_id=?",
                    -2002L);
            SqlOperator.execute(db, "delete from " + Dbm_code_item_info.TableName + " where code_item_name=?",
                    "测试新增代码项hll");
            SqlOperator.execute(db, "delete from " + Dbm_code_item_info.TableName + " where code_item_id=?",
                    -3001L);
            SqlOperator.execute(db, "delete from " + Dbm_code_item_info.TableName + " where code_item_id=?",
                    -3002L);
            SqlOperator.commitTransaction(db);
            long codeItemNum;
            codeItemNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_item_info.TableName +
                    " where code_item_id=?", -1000L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_code_item_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
            codeItemNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_item_info.TableName +
                    " where code_item_id=?", -1001L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_code_item_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
            codeItemNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_item_info.TableName +
                    " where code_item_id=?", -1002L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_code_item_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
            codeItemNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_item_info.TableName +
                    " where code_item_name=?", "测试新增代码项hll").orElseThrow(() ->
                    new RuntimeException("count fail!"));
            assertThat("Dbm_code_item_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
        }
    }

    @Method(desc = "添加代码项信息测试方法",
            logicStep = "添加代码项信息")
    @Test
    public void addDbmCodeItemInfo() {
        //1.添加代码项信息
        bodyString = new HttpClient()
                .addData("code_item_name", "测试新增代码项hll")
                .addData("dbm_level", "测试新增代码项hll")
                .addData("code_type_id", -1000L)
                .post(getActionUrl("addDbmCodeItemInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查新增的数据是否正确
                Result rs = SqlOperator.queryResult(db, "select * from " + Dbm_code_item_info.TableName +
                        " where code_item_name=?", "测试新增代码项hll");
                assertThat(rs.getString(0, "code_item_name"), is("测试新增代码项hll"));
                assertThat(rs.getString(0, "dbm_level"), is("测试新增代码项hll"));
                assertThat(rs.getLong(0, "code_type_id"), is(-1000L));
            }
        }
    }

    @Method(desc = "删除加代码项信息测试方法",
            logicStep = "删除加代码项信息测试")
    @Test
    public void deleteDbmCodeItemInfo() {
        //1.删除代码项信息
        bodyString = new HttpClient()
                .addData("code_item_id", -1000L)
                .post(getActionUrl("deleteDbmCodeItemInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查新增的数据是否正确
                OptionalLong number = SqlOperator.queryNumber(db,
                        "select count(*) from " + Dbm_code_item_info.TableName + " where code_item_id=?",
                        -1000L);
                assertThat("表 Dbm_code_item_info 数据删除成功", number.orElse(-1), is(0L));
            }
        }
    }

    @Method(desc = "修改代码项信息测试方法",
            logicStep = "修改代码项信息")
    @Test
    public void updateDbmCodeItemInfo() {
        //1.修改代码项信息
        bodyString = new HttpClient()
                .addData("code_item_id", -1001L)
                .addData("code_item_name", -1001L)
                .addData("dbm_level", -1001L)
                .addData("code_type_id", -1000L)
                .post(getActionUrl("updateDbmCodeItemInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查新增的数据是否正确
                Result rs = SqlOperator.queryResult(db, "select * from " + Dbm_code_item_info.TableName +
                        " where code_item_id=?", -1001L);
                assertThat(rs.getString(0, "code_item_name"), is("-1001"));
                assertThat(rs.getString(0, "dbm_level"), is("-1001"));
                assertThat(rs.getLong(0, "code_type_id"), is(-1000L));
            }
        }
    }

    @Method(desc = "查询代码项信息测试方法",
            logicStep = "查询代码项信息")
    @Test
    public void getDbmCodeItemInfoByCodeTypeId() {
        //1.查询代码项信息
        bodyString = new HttpClient()
                .addData("code_type_id", -1000L)
                .post(getActionUrl("getDbmCodeItemInfoByCodeTypeId")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查新增的数据是否正确
                Result rs = SqlOperator.queryResult(db, "select * from " + Dbm_code_item_info.TableName +
                        " where code_type_id=?", -1000L);
                assertThat(rs.getRowCount(), is(3));
            }
        }
    }

    @Method(desc = "根据代码项id获取代码项信息测试方法",
            logicStep = "根据代码项id获取代码项信息")
    @Test
    public void getDbmCodeItemInfoById() {
        //1.根据代码项id获取代码项信息
        bodyString = new HttpClient()
                .addData("code_item_id", -1002L)
                .post(getActionUrl("getDbmCodeItemInfoById")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("code_item_name"), is("测试查询代码项hll"));
            assertThat(ar.getDataForMap().get("dbm_level"), is("测试查询代码项hll"));
            assertThat(ar.getDataForMap().get("code_item_id"), is(-1002));
            assertThat(ar.getDataForMap().get("code_type_id"), is(-1000));
        }
    }

    @Method(desc = "批量删除代码项测试方法",
            logicStep = "批量删除代码项")
    @Test
    public void batchDeleteDbmCodeItemInfo() {
        //1.删除代码项信息
        bodyString = new HttpClient()
                .addData("code_item_id_s", new long[]{-2000L, -2001L, -2002L})
                .post(getActionUrl("batchDeleteDbmCodeItemInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查删除的数据
                OptionalLong number = SqlOperator.queryNumber(db,
                        "select count(*) from " + Dbm_code_item_info.TableName + " where code_item_name=?",
                        "'%测试批量删除%'");
                assertThat("表 Dbm_code_item_info 数据删除成功", number.orElse(-1), is(0L));
            }
        }
    }

    @Method(desc = "代码项检索测试方法",
            logicStep = "代码项检索")
    @Test
    public void searchDbmCodeItemInfo() {
        //1.删除代码项信息
        bodyString = new HttpClient()
                .addData("search_cond", "测试检索hll")
                .addData("code_type_id", "-1000")
                .post(getActionUrl("searchDbmCodeItemInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            //检查检索的数据
            assertThat(ar.getDataForMap().size(), is(2));
        }
    }
}
