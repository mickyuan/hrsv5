package hrds.k.biz.dbm.sortinfo;

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
import hrds.commons.entity.Dbm_sort_info;
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

@DocClass(desc = "标准分类管理测试类", author = "BY-HLL", createdate = "2020/3/11 0011 下午 03:21")
public class DbmSortInfoActionTest extends WebBaseTestCase {

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
            //初始化 Dbm_sort_info 数据
            //测试删除数据
            Dbm_sort_info dbm_sort_info = new Dbm_sort_info();
            dbm_sort_info.setSort_id(-1000L);
            dbm_sort_info.setParent_id(0L);
            dbm_sort_info.setSort_level_num(0L);
            dbm_sort_info.setSort_name("测试删除标准分类hll");
            dbm_sort_info.setSort_remark("测试删除标准分类hll");
            dbm_sort_info.setSort_status(IsFlag.Shi.getCode());
            dbm_sort_info.setCreate_user(String.valueOf(USER_ID));
            dbm_sort_info.setCreate_date(DateUtil.getSysDate());
            dbm_sort_info.setCreate_time(DateUtil.getSysTime());
            dbm_sort_info.add(db);
            //测试修改数据
            dbm_sort_info.setSort_id(-2000L);
            dbm_sort_info.setParent_id(0L);
            dbm_sort_info.setSort_level_num(0L);
            dbm_sort_info.setSort_name("测试修改标准分类hll");
            dbm_sort_info.setSort_remark("测试修改标准分类hll");
            dbm_sort_info.setSort_status(IsFlag.Shi.getCode());
            dbm_sort_info.setCreate_user(String.valueOf(USER_ID));
            dbm_sort_info.setCreate_date(DateUtil.getSysDate());
            dbm_sort_info.setCreate_time(DateUtil.getSysTime());
            dbm_sort_info.add(db);
            //测试查询数据
            dbm_sort_info.setSort_id(-3000L);
            dbm_sort_info.setParent_id(0L);
            dbm_sort_info.setSort_level_num(0L);
            dbm_sort_info.setSort_name("测试查询标准分类hll");
            dbm_sort_info.setSort_remark("测试查询标准分类hll");
            dbm_sort_info.setSort_status(IsFlag.Shi.getCode());
            dbm_sort_info.setCreate_user(String.valueOf(USER_ID));
            dbm_sort_info.setCreate_date(DateUtil.getSysDate());
            dbm_sort_info.setCreate_time(DateUtil.getSysTime());
            dbm_sort_info.add(db);
            //测试批量删除
            dbm_sort_info.setSort_id(-4000L);
            dbm_sort_info.setParent_id(0L);
            dbm_sort_info.setSort_level_num(0L);
            dbm_sort_info.setSort_name("测试批量删除标准分类hll4000");
            dbm_sort_info.setSort_remark("测试批量删除标准分类hll4000");
            dbm_sort_info.setSort_status(IsFlag.Shi.getCode());
            dbm_sort_info.setCreate_user(String.valueOf(USER_ID));
            dbm_sort_info.setCreate_date(DateUtil.getSysDate());
            dbm_sort_info.setCreate_time(DateUtil.getSysTime());
            dbm_sort_info.add(db);
            dbm_sort_info.setSort_id(-4001L);
            dbm_sort_info.setParent_id(0L);
            dbm_sort_info.setSort_level_num(0L);
            dbm_sort_info.setSort_name("测试批量删除标准分类hll4001");
            dbm_sort_info.setSort_remark("测试批量删除标准分类hll4001");
            dbm_sort_info.setSort_status(IsFlag.Fou.getCode());
            dbm_sort_info.setCreate_user(String.valueOf(USER_ID));
            dbm_sort_info.setCreate_date(DateUtil.getSysDate());
            dbm_sort_info.setCreate_time(DateUtil.getSysTime());
            dbm_sort_info.add(db);
            dbm_sort_info.setSort_id(-4002L);
            dbm_sort_info.setParent_id(0L);
            dbm_sort_info.setSort_level_num(0L);
            dbm_sort_info.setSort_name("测试批量删除标准分类hll4002");
            dbm_sort_info.setSort_remark("测试批量删除标准分类hll4002");
            dbm_sort_info.setSort_status(IsFlag.Shi.getCode());
            dbm_sort_info.setCreate_user(String.valueOf(USER_ID));
            dbm_sort_info.setCreate_date(DateUtil.getSysDate());
            dbm_sort_info.setCreate_time(DateUtil.getSysTime());
            dbm_sort_info.add(db);
            //测试检索数据
            dbm_sort_info.setSort_id(-5000L);
            dbm_sort_info.setParent_id(0L);
            dbm_sort_info.setSort_level_num(0L);
            dbm_sort_info.setSort_name("测试检索标准分类hll5000");
            dbm_sort_info.setSort_remark("测试检索标准分类hll5000");
            dbm_sort_info.setSort_status(IsFlag.Shi.getCode());
            dbm_sort_info.setCreate_user(String.valueOf(USER_ID));
            dbm_sort_info.setCreate_date(DateUtil.getSysDate());
            dbm_sort_info.setCreate_time(DateUtil.getSysTime());
            dbm_sort_info.add(db);
            dbm_sort_info.setSort_id(-5001L);
            dbm_sort_info.setParent_id(0L);
            dbm_sort_info.setSort_level_num(0L);
            dbm_sort_info.setSort_name("测试检索标准分类hll5001");
            dbm_sort_info.setSort_remark("测试检索标准分类hll5001");
            dbm_sort_info.setSort_status(IsFlag.Fou.getCode());
            dbm_sort_info.setCreate_user(String.valueOf(USER_ID));
            dbm_sort_info.setCreate_date(DateUtil.getSysDate());
            dbm_sort_info.setCreate_time(DateUtil.getSysTime());
            dbm_sort_info.add(db);
            dbm_sort_info.setSort_id(-5002L);
            dbm_sort_info.setParent_id(0L);
            dbm_sort_info.setSort_level_num(0L);
            dbm_sort_info.setSort_name("测试检索标准分类hll5002");
            dbm_sort_info.setSort_remark("测试检索标准分类hll5002");
            dbm_sort_info.setSort_status(IsFlag.Fou.getCode());
            dbm_sort_info.setCreate_user(String.valueOf(USER_ID));
            dbm_sort_info.setCreate_date(DateUtil.getSysDate());
            dbm_sort_info.setCreate_time(DateUtil.getSysTime());
            dbm_sort_info.add(db);
            //测试发布数据
            dbm_sort_info.setSort_id(-6000L);
            dbm_sort_info.setParent_id(0L);
            dbm_sort_info.setSort_level_num(0L);
            dbm_sort_info.setSort_name("测试发布标准分类hll6000");
            dbm_sort_info.setSort_remark("测试发布标准分类hll6000");
            dbm_sort_info.setSort_status(IsFlag.Fou.getCode());
            dbm_sort_info.setCreate_user(String.valueOf(USER_ID));
            dbm_sort_info.setCreate_date(DateUtil.getSysDate());
            dbm_sort_info.setCreate_time(DateUtil.getSysTime());
            dbm_sort_info.add(db);
            //测试批量发布
            dbm_sort_info.setSort_id(-7000L);
            dbm_sort_info.setParent_id(0L);
            dbm_sort_info.setSort_level_num(0L);
            dbm_sort_info.setSort_name("测试批量发布标准分类hll7000");
            dbm_sort_info.setSort_remark("测试批量发布标准分类hll7000");
            dbm_sort_info.setSort_status(IsFlag.Fou.getCode());
            dbm_sort_info.setCreate_user(String.valueOf(USER_ID));
            dbm_sort_info.setCreate_date(DateUtil.getSysDate());
            dbm_sort_info.setCreate_time(DateUtil.getSysTime());
            dbm_sort_info.add(db);
            dbm_sort_info.setSort_id(-7001L);
            dbm_sort_info.setParent_id(-7000L);
            dbm_sort_info.setSort_level_num(0L);
            dbm_sort_info.setSort_name("测试批量发布标准分类hll7001");
            dbm_sort_info.setSort_remark("测试批量发布标准分类hll7001");
            dbm_sort_info.setSort_status(IsFlag.Fou.getCode());
            dbm_sort_info.setCreate_user(String.valueOf(USER_ID));
            dbm_sort_info.setCreate_date(DateUtil.getSysDate());
            dbm_sort_info.setCreate_time(DateUtil.getSysTime());
            dbm_sort_info.add(db);
            dbm_sort_info.setSort_id(-7002L);
            dbm_sort_info.setParent_id(-7000L);
            dbm_sort_info.setSort_level_num(0L);
            dbm_sort_info.setSort_name("测试批量发布标准分类hll7002");
            dbm_sort_info.setSort_remark("测试批量发布标准分类hll7002");
            dbm_sort_info.setSort_status(IsFlag.Fou.getCode());
            dbm_sort_info.setCreate_user(String.valueOf(USER_ID));
            dbm_sort_info.setCreate_date(DateUtil.getSysDate());
            dbm_sort_info.setCreate_time(DateUtil.getSysTime());
            dbm_sort_info.add(db);
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
            //删除 Dbm_sort_info 测试数据
            SqlOperator.execute(db,
                    "delete from " + Dbm_sort_info.TableName + " where sort_id=?", -1000L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_sort_info.TableName + " where sort_id=?", -2000L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_sort_info.TableName + " where sort_id=?", -3000L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_sort_info.TableName + " where sort_id=?", -4000L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_sort_info.TableName + " where sort_id=?", -4001L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_sort_info.TableName + " where sort_id=?", -4002L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_sort_info.TableName + " where sort_id=?", -5000L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_sort_info.TableName + " where sort_id=?", -5001L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_sort_info.TableName + " where sort_id=?", -5002L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_sort_info.TableName + " where sort_id=?", -6000L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_sort_info.TableName + " where sort_id=?", -7000L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_sort_info.TableName + " where sort_id=?", -7001L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_sort_info.TableName + " where sort_id=?", -7002L);
            SqlOperator.execute(db, "delete from " + Dbm_sort_info.TableName + " where sort_name=?",
                    "测试新增标准分类hll");
            SqlOperator.commitTransaction(db);
            long dsiNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_sort_info.TableName +
                    " where sort_id in(-1000,-2000,-3000,-4000,-4001,-4002,-5000,-5001,-5002,-6000,-7000,-7001,-7002)")
                    .orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_sort_info 表此条数据删除后,记录数应该为0", dsiNum, is(0L));
        }
    }

    @Method(desc = "添加标准分类测试方法",
            logicStep = "添加标准分类")
    @Test
    public void addDbmSortInfo() {
        //1.添加代码项信息
        //1-1.正确添加
        bodyString = new HttpClient()
                .addData("parent_id", 0L)
                .addData("sort_level_num", 0L)
                .addData("sort_name", "测试新增标准分类hll")
                .addData("sort_status", IsFlag.Fou.getCode())
                .post(getActionUrl("addDbmSortInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查新增的数据是否正确
                Result rs = SqlOperator.queryResult(db, "select * from " + Dbm_sort_info.TableName +
                        " where sort_name=?", "测试新增标准分类hll");
                assertThat(rs.getLong(0, "parent_id"), is(0L));
                assertThat(rs.getLong(0, "sort_level_num"), is(0L));
                assertThat(rs.getString(0, "sort_name"), is("测试新增标准分类hll"));
                assertThat(rs.getString(0, "sort_status"), is("0"));
            }
        }
        //1-1.分类名为空
        bodyString = new HttpClient()
                .addData("parent_id", 0L)
                .addData("sort_level_num", 0L)
                .addData("sort_status", IsFlag.Fou.getCode())
                .post(getActionUrl("addDbmSortInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(false));
            assertThat(ar.getMessage(), is("标准分类名称为空!null"));
        }
        //1-2.分类名重复
        bodyString = new HttpClient()
                .addData("parent_id", 0L)
                .addData("sort_level_num", 0L)
                .addData("sort_name", "测试新增标准分类hll")
                .addData("sort_status", IsFlag.Fou.getCode())
                .post(getActionUrl("addDbmSortInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(false));
            assertThat(ar.getMessage(), is("分类名称已经存在!测试新增标准分类hll"));
        }
        //1-2.发布状态为空
        bodyString = new HttpClient()
                .addData("parent_id", 0L)
                .addData("sort_level_num", 0L)
                .addData("sort_name", "测试新增标准分类hll-1")
                .post(getActionUrl("addDbmSortInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(false));
            assertThat(ar.getMessage(), is("分类状态为空!null"));
        }
    }

    @Method(desc = "删除分类信息测试方法",
            logicStep = "删除分类信息")
    @Test
    public void deleteDbmSortInfo() {
        //1.id删除分类信息
        bodyString = new HttpClient()
                .addData("sort_id", -1000L)
                .post(getActionUrl("deleteDbmSortInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                OptionalLong number = SqlOperator.queryNumber(db,
                        "select count(*) from " + Dbm_sort_info.TableName + " where sort_id=?", -1000L);
                assertThat("表 Dbm_sort_info 数据删除成功", number.orElse(-1), is(0L));
            }
        }
    }

    @Method(desc = "修改分类信息测试方法",
            logicStep = "修改分类信息")
    @Test
    public void updateDbmSortInfo() {
        //1.修改分类信息
        bodyString = new HttpClient()
                .addData("sort_id", -2000L)
                .addData("parent_id", 0)
                .addData("sort_level_num", 1)
                .addData("sort_name", "-2000L")
                .addData("sort_remark", "-2000L")
                .addData("sort_status", IsFlag.Fou.getCode())
                .addData("create_user", String.valueOf(USER_ID))
                .post(getActionUrl("updateDbmSortInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查新增的数据是否正确
                Result rs = SqlOperator.queryResult(db, "select * from " + Dbm_sort_info.TableName +
                        " where sort_id=?", -2000L);
                assertThat(rs.getLong(0, "parent_id"), is(0L));
                assertThat(rs.getLong(0, "sort_level_num"), is(1L));
                assertThat(rs.getString(0, "sort_name"), is("-2000L"));
                assertThat(rs.getString(0, "sort_remark"), is("-2000L"));
                assertThat(rs.getString(0, "sort_status"), is("0"));
                assertThat(rs.getLong(0, "create_user"), is(USER_ID));
            }
        }
    }

    @Method(desc = "获取所有分类信息测试方法",
            logicStep = "获取所有分类信息")
    @Test
    public void getDbmSortInfo() {
        //1.查询标准分类信息
        bodyString = new HttpClient()
                .post(getActionUrl("getDbmSortInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("totalSize"), is(10));
        }
    }

    @Method(desc = "根据Id获取分类信息测试方法",
            logicStep = "根据Id获取分类信息")
    @Test
    public void getDbmSortInfoById() {
        //1.id查询标准分类信息
        bodyString = new HttpClient()
                .addData("sort_id", -3000L)
                .post(getActionUrl("getDbmSortInfoById")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("parent_id"), is(0));
            assertThat(ar.getDataForMap().get("sort_level_num"), is(0));
            assertThat(ar.getDataForMap().get("sort_name"), is("测试查询标准分类hll"));
            assertThat(ar.getDataForMap().get("sort_remark"), is("测试查询标准分类hll"));
            assertThat(ar.getDataForMap().get("sort_status"), is("1"));
        }
    }

    @Method(desc = "根据发布状态获取分类信息测试方法",
            logicStep = "根据发布状态获取分类信息")
    @Test
    public void getDbmSortInfoByStatus() {
        //1.发布状态查询标准分类信息
        bodyString = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", 2)
                .addData("sort_status", IsFlag.Fou.getCode())
                .post(getActionUrl("getDbmSortInfoByStatus")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("totalSize"), is(5));
        }
    }

    @Method(desc = "检索分类信息测试方法",
            logicStep = "检索分类信息")
    @Test
    public void searchDbmSortInfo() {
        //1.发布状态查询标准分类信息
        //1-1.正常检索(检索条件)
        bodyString = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", 3)
                .addData("search_cond", "测试检索标准分类")
                .addData("status", IsFlag.Fou.getCode())
                .post(getActionUrl("searchDbmSortInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().size(), is(2));
        }
        //1-2.正常检索(检索条件加发布状态)
        bodyString = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", 3)
                .addData("search_cond", "测试检索标准分类")
                .addData("status", IsFlag.Shi.getCode())
                .post(getActionUrl("searchDbmSortInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("totalSize"), is(1));
        }
        //1-3.检索条件为空
        bodyString = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", 3)
                .addData("search_cond", "")
                .addData("status", IsFlag.Fou.getCode())
                .post(getActionUrl("searchDbmSortInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(false));
            assertThat(ar.getMessage(), is("搜索条件不能为空!"));
        }
    }

    @Method(desc = "获取所有根分类信息测试方法",
            logicStep = "获取所有根分类信息")
    @Test
    public void getDbmRootSortInfo() {
        //1.查询标准分类根分类信息
        bodyString = new HttpClient()
                .post(getActionUrl("getDbmRootSortInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("totalSize"), is(11));
        }
    }

    @Method(desc = "根据分类id获取所有子分类信息测试方法",
            logicStep = "根据分类id获取所有子分类信息")
    @Test
    public void getDbmSubSortInfo() {
        //1.id查询标准分类子分类信息
        bodyString = new HttpClient()
                .addData("sort_id", -7000L)
                .post(getActionUrl("getDbmSubSortInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("totalSize"), is(2));
        }
    }

    @Method(desc = "根据标准分类id发布标准分类测试方法",
            logicStep = "根据标准分类id发布标准分类")
    @Test
    public void releaseDbmSortInfoById() {
        //1.id发布标准分类子分类信息
        bodyString = new HttpClient()
                .addData("sort_id", -7000L)
                .post(getActionUrl("releaseDbmSortInfoById")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查发布的数据是否正确
                Result rs = SqlOperator.queryResult(db, "select * from " + Dbm_sort_info.TableName +
                        " where sort_id=?", -7000L);
                assertThat(rs.getString(0, "sort_status"), is("1"));
            }
        }
    }

    @Method(desc = "根据标准分类id数组批量发布标准分类测试方法",
            logicStep = "根据标准分类id数组批量发布标准分类")
    @Test
    public void batchReleaseDbmSortInfo() {
        //1.id数组发布标准分类子分类信息
        bodyString = new HttpClient()
                .addData("sort_id_s", new long[]{-7001L, -7002L})
                .post(getActionUrl("batchReleaseDbmSortInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查发布的数据是否正确
                List<Dbm_sort_info> dbm_sort_infos = SqlOperator.queryList(db, Dbm_sort_info.class,
                        "select * from " + Dbm_sort_info.TableName + " where sort_id in(-7001, -7002)");
                dbm_sort_infos.forEach(dbm_sort_info -> assertThat(dbm_sort_info.getSort_status(), is("1")));
            }
        }
    }

    @Method(desc = "根据标准分类id数组批量删除标准分类测试方法",
            logicStep = "根据标准分类id数组批量删除标准分类")
    @Test
    public void batchDeleteDbmSortInfo() {
        //1.id数组删除标准分类子分类信息
        bodyString = new HttpClient()
                .addData("sort_id_s", new long[]{-4000L, -4001L, -4002L})
                .post(getActionUrl("batchDeleteDbmSortInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查删除的数据
                OptionalLong number = SqlOperator.queryNumber(db,
                        "select count(*) from " + Dbm_sort_info.TableName + " where sort_id in(-4000,-4001,-4002)");
                assertThat("表 Dbm_sort_info 数据删除成功", number.orElse(-1), is(0L));
            }
        }
    }
}
