package hrds.k.biz.dbmcodeiteminfo;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.entity.Dbm_code_item_info;
import hrds.commons.entity.Dbm_code_type_info;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Sys_user;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
            sysUser.setUser_name("init-hll");
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
            departmentInfo.setDep_name("init-hll");
            departmentInfo.setCreate_date(DateUtil.getSysDate());
            departmentInfo.setCreate_time(DateUtil.getSysTime());
            departmentInfo.add(db);
            //初始化 Dbm_code_type_info 数据
            Dbm_code_type_info dbm_code_type_info = new Dbm_code_type_info();
            dbm_code_type_info.setCode_type_id(-1000L);
            dbm_code_type_info.setCode_type_name("测试代码类init-hll");
            dbm_code_type_info.setCode_status("1");
            dbm_code_type_info.setCreate_user("测试代码类init-hll");
            dbm_code_type_info.setCreate_date(DateUtil.getSysDate());
            dbm_code_type_info.setCreate_time(DateUtil.getSysTime());
            dbm_code_type_info.add(db);
            //初始化 Dbm_code_item_info 数据
            Dbm_code_item_info dbm_code_item_info = new Dbm_code_item_info();
            // 测试删除数据
            dbm_code_item_info.setCode_item_id(-1000L);
            dbm_code_item_info.setCode_item_name("测试删除代码项init-hll");
            dbm_code_item_info.setDbm_level("测试删除代码项init-hll");
            dbm_code_item_info.setCode_type_id(-1000L);
            dbm_code_item_info.add(db);
            // 测试修改数据
            dbm_code_item_info.setCode_item_id(-1001L);
            dbm_code_item_info.setCode_item_name("测试修改代码项init-hll");
            dbm_code_item_info.setDbm_level("测试修改代码项init-hll");
            dbm_code_item_info.setCode_type_id(-1000L);
            dbm_code_item_info.add(db);
            // 测试查询数据
            dbm_code_item_info.setCode_item_id(-1002L);
            dbm_code_item_info.setCode_item_name("测试查询代码项init-hll");
            dbm_code_item_info.setDbm_level("测试查询代码项init-hll");
            dbm_code_item_info.setCode_type_id(-1000L);
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
            SqlOperator.execute(db, "delete from " + Dbm_code_item_info.TableName + " where code_item_name=?",
                    "测试新增代码项init-hll");
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
                    " where code_item_name=?", "测试新增代码项init-hll").orElseThrow(() ->
                    new RuntimeException("count fail!"));
            assertThat("Dbm_code_item_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
        }
    }

    @Method(desc = "",
            logicStep = "")
    @Test
    public void addDbmCodeItemInfo() {
    }

    @Method(desc = "",
            logicStep = "")
    @Test
    public void deleteDbmCodeItemInfo() {
    }

    @Method(desc = "",
            logicStep = "")
    @Test
    public void updateDbmCodeItemInfo() {
    }

    @Method(desc = "",
            logicStep = "")
    @Test
    public void getDbmCodeItemInfoByCodeTypeId() {
    }

    @Method(desc = "",
            logicStep = "")
    @Test
    public void getDbmSortInfoById() {
        //1.根据代码项id获取代码项信息
        bodyString = new HttpClient()
                .addData("code_item_id", -1002L)
                .post(getActionUrl("getDbmCodeItemInfoById")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
        }
    }
}
