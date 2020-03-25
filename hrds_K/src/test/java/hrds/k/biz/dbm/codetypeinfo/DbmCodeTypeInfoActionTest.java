package hrds.k.biz.dbm.codetypeinfo;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Dbm_code_type_info;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Sys_user;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "代码项分类管理测试类", author = "BY-HLL", createdate = "2020/3/11 0011 下午 03:22")
public class DbmCodeTypeInfoActionTest extends WebBaseTestCase {

    //测试数据的用户ID
    private static final long USER_ID = -1000L;
    //测试数据的部门ID
    private static final long DEP_ID = -1000000001L;

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
            //测试删除数据
            dbm_code_type_info.setCode_type_id(-1000L);
            dbm_code_type_info.setCode_type_name("测试删除代码分类hll");
            dbm_code_type_info.setCode_status(IsFlag.Fou.getCode());
            dbm_code_type_info.setCreate_user(String.valueOf(USER_ID));
            dbm_code_type_info.setCreate_date(DateUtil.getSysDate());
            dbm_code_type_info.setCreate_time(DateUtil.getSysTime());
            dbm_code_type_info.add(db);
            //测试修改数据
            dbm_code_type_info.setCode_type_id(-1001L);
            dbm_code_type_info.setCode_type_name("测试修改代码分类hll");
            dbm_code_type_info.setCode_status(IsFlag.Fou.getCode());
            dbm_code_type_info.setCreate_user(String.valueOf(USER_ID));
            dbm_code_type_info.setCreate_date(DateUtil.getSysDate());
            dbm_code_type_info.setCreate_time(DateUtil.getSysTime());
            dbm_code_type_info.add(db);
            //测试查询数据
            dbm_code_type_info.setCode_type_id(-1002L);
            dbm_code_type_info.setCode_type_name("测试查询代码分类hll");
            dbm_code_type_info.setCode_status(IsFlag.Fou.getCode());
            dbm_code_type_info.setCreate_user(String.valueOf(USER_ID));
            dbm_code_type_info.setCreate_date(DateUtil.getSysDate());
            dbm_code_type_info.setCreate_time(DateUtil.getSysTime());
            dbm_code_type_info.add(db);
            //测试批量删除数据
            dbm_code_type_info.setCode_type_id(-2000L);
            dbm_code_type_info.setCode_type_name("测试批量删除代码分类hll-2000");
            dbm_code_type_info.setCode_status(IsFlag.Fou.getCode());
            dbm_code_type_info.setCreate_user(String.valueOf(USER_ID));
            dbm_code_type_info.setCreate_date(DateUtil.getSysDate());
            dbm_code_type_info.setCreate_time(DateUtil.getSysTime());
            dbm_code_type_info.add(db);
            dbm_code_type_info.setCode_type_id(-2001L);
            dbm_code_type_info.setCode_type_name("测试批量删除代码分类hll-2001");
            dbm_code_type_info.setCode_status(IsFlag.Fou.getCode());
            dbm_code_type_info.setCreate_user(String.valueOf(USER_ID));
            dbm_code_type_info.setCreate_date(DateUtil.getSysDate());
            dbm_code_type_info.setCreate_time(DateUtil.getSysTime());
            dbm_code_type_info.add(db);
            dbm_code_type_info.setCode_type_id(-2002L);
            dbm_code_type_info.setCode_type_name("测试批量删除代码分类hll-2002");
            dbm_code_type_info.setCode_status(IsFlag.Fou.getCode());
            dbm_code_type_info.setCreate_user(String.valueOf(USER_ID));
            dbm_code_type_info.setCreate_date(DateUtil.getSysDate());
            dbm_code_type_info.setCreate_time(DateUtil.getSysTime());
            dbm_code_type_info.add(db);
            //测试检索数据
            dbm_code_type_info.setCode_type_id(-3000L);
            dbm_code_type_info.setCode_type_name("测试检索代码分类hll-3000");
            dbm_code_type_info.setCode_status(IsFlag.Shi.getCode());
            dbm_code_type_info.setCreate_user(String.valueOf(USER_ID));
            dbm_code_type_info.setCreate_date(DateUtil.getSysDate());
            dbm_code_type_info.setCreate_time(DateUtil.getSysTime());
            dbm_code_type_info.add(db);
            dbm_code_type_info.setCode_type_id(-3001L);
            dbm_code_type_info.setCode_type_name("测试检索代码分类hll-3001");
            dbm_code_type_info.setCode_status(IsFlag.Fou.getCode());
            dbm_code_type_info.setCreate_user(String.valueOf(USER_ID));
            dbm_code_type_info.setCreate_date(DateUtil.getSysDate());
            dbm_code_type_info.setCreate_time(DateUtil.getSysTime());
            dbm_code_type_info.add(db);
            dbm_code_type_info.setCode_type_id(-3002L);
            dbm_code_type_info.setCode_type_name("测试检索代码分类hll-3002");
            dbm_code_type_info.setCode_status(IsFlag.Fou.getCode());
            dbm_code_type_info.setCreate_user(String.valueOf(USER_ID));
            dbm_code_type_info.setCreate_date(DateUtil.getSysDate());
            dbm_code_type_info.setCreate_time(DateUtil.getSysTime());
            dbm_code_type_info.add(db);
            //测试发布数据
            dbm_code_type_info.setCode_type_id(-4000L);
            dbm_code_type_info.setCode_type_name("测试发布代码分类hll-4000");
            dbm_code_type_info.setCode_status(IsFlag.Fou.getCode());
            dbm_code_type_info.setCreate_user(String.valueOf(USER_ID));
            dbm_code_type_info.setCreate_date(DateUtil.getSysDate());
            dbm_code_type_info.setCreate_time(DateUtil.getSysTime());
            dbm_code_type_info.add(db);
            //测试批量发布数据
            dbm_code_type_info.setCode_type_id(-5000L);
            dbm_code_type_info.setCode_type_name("测试发布代码分类hll-5000");
            dbm_code_type_info.setCode_status(IsFlag.Fou.getCode());
            dbm_code_type_info.setCreate_user(String.valueOf(USER_ID));
            dbm_code_type_info.setCreate_date(DateUtil.getSysDate());
            dbm_code_type_info.setCreate_time(DateUtil.getSysTime());
            dbm_code_type_info.add(db);
            dbm_code_type_info.setCode_type_id(-5001L);
            dbm_code_type_info.setCode_type_name("测试发布代码分类hll-5000");
            dbm_code_type_info.setCode_status(IsFlag.Fou.getCode());
            dbm_code_type_info.setCreate_user(String.valueOf(USER_ID));
            dbm_code_type_info.setCreate_date(DateUtil.getSysDate());
            dbm_code_type_info.setCreate_time(DateUtil.getSysTime());
            dbm_code_type_info.add(db);
            dbm_code_type_info.setCode_type_id(-5002L);
            dbm_code_type_info.setCode_type_name("测试发布代码分类hll-5000");
            dbm_code_type_info.setCode_status(IsFlag.Fou.getCode());
            dbm_code_type_info.setCreate_user(String.valueOf(USER_ID));
            dbm_code_type_info.setCreate_date(DateUtil.getSysDate());
            dbm_code_type_info.setCreate_time(DateUtil.getSysTime());
            dbm_code_type_info.add(db);
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
            SqlOperator.execute(db, "delete from " + Dbm_code_type_info.TableName + " where code_type_id=?",
                    -1001L);
            SqlOperator.execute(db, "delete from " + Dbm_code_type_info.TableName + " where code_type_id=?",
                    -1002L);
            SqlOperator.execute(db, "delete from " + Dbm_code_type_info.TableName + " where code_type_id=?",
                    -2000L);
            SqlOperator.execute(db, "delete from " + Dbm_code_type_info.TableName + " where code_type_id=?",
                    -2001L);
            SqlOperator.execute(db, "delete from " + Dbm_code_type_info.TableName + " where code_type_id=?",
                    -2002L);
            SqlOperator.execute(db, "delete from " + Dbm_code_type_info.TableName + " where code_type_id=?",
                    -3000L);
            SqlOperator.execute(db, "delete from " + Dbm_code_type_info.TableName + " where code_type_id=?",
                    -3001L);
            SqlOperator.execute(db, "delete from " + Dbm_code_type_info.TableName + " where code_type_id=?",
                    -3002L);
            SqlOperator.execute(db, "delete from " + Dbm_code_type_info.TableName + " where code_type_id=?",
                    -4000L);
            SqlOperator.execute(db, "delete from " + Dbm_code_type_info.TableName + " where code_type_id=?",
                    -5000L);
            SqlOperator.execute(db, "delete from " + Dbm_code_type_info.TableName + " where code_type_id=?",
                    -5001L);
            SqlOperator.execute(db, "delete from " + Dbm_code_type_info.TableName + " where code_type_id=?",
                    -5002L);
            SqlOperator.execute(db, "delete from " + Dbm_code_type_info.TableName + " where code_type_name=?",
                    "测试新增代码类hll");
            SqlOperator.commitTransaction(db);
            long codeItemNum;
            codeItemNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_type_info.TableName +
                    " where code_type_id=?", -1000L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_code_type_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
            codeItemNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_type_info.TableName +
                    " where code_type_id=?", -1001L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_code_type_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
            codeItemNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_type_info.TableName +
                    " where code_type_id=?", -1002L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_code_type_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
            codeItemNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_type_info.TableName +
                    " where code_type_id=?", -2000L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_code_type_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
            codeItemNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_type_info.TableName +
                    " where code_type_id=?", -2001L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_code_type_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
            codeItemNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_type_info.TableName +
                    " where code_type_id=?", -2002L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_code_type_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
            codeItemNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_type_info.TableName +
                    " where code_type_id=?", -3000L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_code_type_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
            codeItemNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_type_info.TableName +
                    " where code_type_id=?", -3001L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_code_type_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
            codeItemNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_type_info.TableName +
                    " where code_type_id=?", -3002L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_code_type_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
            codeItemNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_type_info.TableName +
                    " where code_type_id=?", -4000L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_code_type_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
            codeItemNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_type_info.TableName +
                    " where code_type_id=?", -5000L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_code_type_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
            codeItemNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_type_info.TableName +
                    " where code_type_id=?", -5001L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_code_type_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
            codeItemNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_type_info.TableName +
                    " where code_type_id=?", -5002L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_code_type_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
            codeItemNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_type_info.TableName +
                    " where code_type_name=?", "测试新增代码类hll").orElseThrow(() ->
                    new RuntimeException("count fail!"));
            assertThat("Dbm_code_type_info 表此条数据删除后,记录数应该为0", codeItemNum, is(0L));
        }
    }

    @Method(desc = "添加代码分类信息测试方法",
            logicStep = "添加代码分类信息")
    @Test
    public void addDbmCodeTypeInfo() {
        //1.添加代码项信息
        String bodyString = new HttpClient()
                .addData("code_type_name", "测试新增代码类hll")
                .addData("code_status", IsFlag.Fou.getCode())
                .post(getActionUrl("addDbmCodeTypeInfo")).getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查新增的数据是否正确
                Result rs = SqlOperator.queryResult(db, "select * from " + Dbm_code_type_info.TableName +
                        " where code_type_name=?", "测试新增代码类hll");
                assertThat(rs.getString(0, "code_type_name"), is("测试新增代码类hll"));
                assertThat(rs.getString(0, "code_status"), is("0"));
            }
        }
    }

    @Method(desc = "删除代码分类信息测试方法",
            logicStep = "删除代码分类信息")
    @Test
    public void deleteDbmCodeTypeInfo() {
        //1.删除代码项信息
        String bodyString = new HttpClient()
                .addData("code_type_id", -1000L)
                .post(getActionUrl("deleteDbmCodeTypeInfo")).getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                OptionalLong number = SqlOperator.queryNumber(db,
                        "select count(*) from " + Dbm_code_type_info.TableName + " where code_type_id=?",
                        -1000L);
                assertThat("表 Dbm_code_type_info 数据删除成功", number.orElse(-1), is(0L));
            }
        }
    }

    @Method(desc = "修改代码分类信息测试方法",
            logicStep = "修改代码分类信息")
    @Test
    public void updateDbmCodeTypeInfo() {
        //1.修改代码项信息
        String bodyString = new HttpClient()
                .addData("code_type_id", -1001L)
                .addData("code_type_name", -1001L)
                .addData("code_encode", "-1001L")
                .addData("code_remark", "-1001L")
                .addData("code_status", IsFlag.Shi.getCode())
                .addData("create_user", String.valueOf(USER_ID))
                .post(getActionUrl("updateDbmCodeTypeInfo")).getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查新增的数据是否正确
                Result rs = SqlOperator.queryResult(db, "select * from " + Dbm_code_type_info.TableName +
                        " where code_type_id=?", -1001L);
                assertThat(rs.getString(0, "code_type_name"), is("-1001"));
                assertThat(rs.getString(0, "code_encode"), is("-1001L"));
                assertThat(rs.getString(0, "code_remark"), is("-1001L"));
                assertThat(rs.getString(0, "code_status"), is("1"));
                assertThat(rs.getLong(0, "create_user"), is(USER_ID));
            }
        }
    }

    @Method(desc = "查询代码分类信息测试方法",
            logicStep = "查询代码分类信息")
    @Test
    public void getDbmCodeTypeInfo() {
        //1.查询代码项信息
        String bodyString = new HttpClient()
                .post(getActionUrl("getDbmCodeTypeInfo")).getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("totalSize"), is(11));
        }
    }

    @Method(desc = "查询代码分类信息测试方法",
            logicStep = "查询代码分类信息")
    @Test
    public void getDbmCodeTypeIdAndNameInfo() {
        //1.查询代码项信息
        String bodyString = new HttpClient()
                .post(getActionUrl("getDbmCodeTypeIdAndNameInfo")).getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查新增的数据是否正确
                Result rs = SqlOperator.queryResult(db, "select code_type_id,code_type_name from "
                        + Dbm_code_type_info.TableName + " where create_user=?", "-1000");
                assertThat(rs.getRowCount(), is(13));
            }
        }
    }

    @Method(desc = "根据Id获取代码分类信息测试方法",
            logicStep = "根据Id获取代码分类信息")
    @Test
    public void getDbmCodeTypeInfoById() {
        //1.id查询代码项信息
        String bodyString = new HttpClient()
                .addData("code_type_id", -1002L)
                .post(getActionUrl("getDbmCodeTypeInfoById")).getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("code_type_name"), is("测试查询代码分类hll"));
            assertThat(ar.getDataForMap().get("code_status"), is("0"));
        }
    }

    @Method(desc = "根据发布状态获取代码分类信息测试方法",
            logicStep = "根据发布状态获取代码分类信息")
    @Test
    public void getDbmCodeTypeInfoByStatus() {
        //1.id查询代码项信息
        String bodyString = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", 10)
                .addData("code_status", IsFlag.Fou.getCode())
                .post(getActionUrl("getDbmCodeTypeInfoByStatus")).getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("totalSize"), is(12));
        }
    }

    @Method(desc = "检索代码项分类信息测试方法",
            logicStep = "检索代码项分类信息")
    @Test
    public void searchDbmCodeTypeInfo() {
        //1.检索代码项分类信息(检索条件和发布状态)
        String bodyString = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", 10)
                .addData("search_cond", "测试检索")
                .addData("status", "1")
                .post(getActionUrl("searchDbmCodeTypeInfo")).getBodyString();
        ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("totalSize"), is(1));
        }
        //2.检索代码项分类信息(检索条件)
        String bodyString1 = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", 10)
                .addData("search_cond", "测试检索")
                .post(getActionUrl("searchDbmCodeTypeInfo")).getBodyString();
        ActionResult ar1 = JsonUtil.toObjectSafety(bodyString1, ActionResult.class).orElse(null);
        if (ar1 != null) {
            assertThat(ar1.isSuccess(), is(true));
            assertThat(ar1.getDataForMap().get("totalSize"), is(3));
        }
        //2.检索代码项分类信息(检索条件)
        String bodyString2 = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", 10)
                .addData("status", "0")
                .post(getActionUrl("searchDbmCodeTypeInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString2, ActionResult.class).ifPresent(ar2 ->
                assertThat(ar2.isSuccess(), is(false)));
    }

    @Method(desc = "根据代码分类id发布代码分类测试方法",
            logicStep = "根据代码分类id发布代码分类")
    @Test
    public void releaseDbmCodeTypeInfoById() {
        //1.id发布代码分类信息
        String bodyString = new HttpClient()
                .addData("code_type_id", -4000L)
                .post(getActionUrl("releaseDbmCodeTypeInfoById")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(true)));
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //检查发布的数据是否正确
            Result rs = SqlOperator.queryResult(db, "select * from " + Dbm_code_type_info.TableName +
                    " where code_type_id=?", -4000L);
            assertThat(rs.getString(0, "code_status"), is("1"));
        }
    }

    @Method(desc = "根据代码分类id数组批量发布代码分类测试类",
            logicStep = "根据代码分类id数组批量发布代码分类")
    @Test
    public void batchReleaseDbmCodeTypeInfo() {
        //1.id数组批量发布代码分类信息
        String bodyString = new HttpClient()
                .addData("code_type_id_s", new long[]{-5000L, -5001L, -5002L})
                .post(getActionUrl("batchReleaseDbmCodeTypeInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(true)));
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //检查发布的数据是否正确
            List<Dbm_code_type_info> dbm_code_type_infos = SqlOperator.queryList(db, Dbm_code_type_info.class,
                    "select * from " + Dbm_code_type_info.TableName + " where code_type_id in (-5000,-5001,-5002)");
            dbm_code_type_infos.forEach(dbm_code_type_info ->
                    assertThat(dbm_code_type_info.getCode_status(), is("1")));
        }
    }

    @Method(desc = "根据id数组批量删除测试方法",
            logicStep = "根据id数组批量删除")
    @Test
    public void batchDeleteDbmCodeTypeInfo() {
        //1.id数组批量删除代码分类信息
        String bodyString = new HttpClient()
                .addData("code_type_id_s", new long[]{-2000L, -2001L, -2002L})
                .post(getActionUrl("batchDeleteDbmCodeTypeInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(true)));
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //检查删除的数据
            OptionalLong number = SqlOperator.queryNumber(db,
                    "select count(*) from " + Dbm_code_type_info.TableName + " where code_type_name=?",
                    "'%测试批量删除%'");
            assertThat("表 Dbm_code_type_info 数据删除成功", number.orElse(-1), is(0L));
        }
    }
}
