package hrds.k.biz.dbm.normbasic;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Dbm_normbasic;
import hrds.commons.entity.Dbm_sort_info;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Sys_user;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "标准管理测试类", author = "BY-HLL", createdate = "2020/3/11 0011 下午 03:22")
public class DbmNormbasicActionTest extends WebBaseTestCase {

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
            Dbm_sort_info dbm_sort_info = new Dbm_sort_info();
            dbm_sort_info.setSort_id(-1000L);
            dbm_sort_info.setParent_id(0L);
            dbm_sort_info.setSort_level_num(0L);
            dbm_sort_info.setSort_name("测试标准分类hll");
            dbm_sort_info.setSort_remark("测试标准分类hll");
            dbm_sort_info.setSort_status(IsFlag.Shi.getCode());
            dbm_sort_info.setCreate_user(String.valueOf(USER_ID));
            dbm_sort_info.setCreate_date(DateUtil.getSysDate());
            dbm_sort_info.setCreate_time(DateUtil.getSysTime());
            dbm_sort_info.add(db);
            //初始化 Dbm_normbasic 测试数据
            Dbm_normbasic dbm_normbasic = new Dbm_normbasic();
            //测试删除
            dbm_normbasic.setBasic_id(-1000L);
            dbm_normbasic.setSort_id(-1000L);
            dbm_normbasic.setNorm_cname("测试删除标准hll");
            dbm_normbasic.setNorm_ename("标准英文名");
            dbm_normbasic.setNorm_aname("标准别名");
            dbm_normbasic.setDbm_domain("标准值域");
            dbm_normbasic.setCol_len(10L);
            dbm_normbasic.setDecimal_point(2L);
            dbm_normbasic.setFormulator("标准制定人");
            dbm_normbasic.setNorm_status(IsFlag.Shi.getCode());
            dbm_normbasic.setCreate_user(String.valueOf(USER_ID));
            dbm_normbasic.setCreate_date(DateUtil.getSysDate());
            dbm_normbasic.setCreate_time(DateUtil.getSysTime());
            dbm_normbasic.add(db);
            //测试修改
            dbm_normbasic.setBasic_id(-2000L);
            dbm_normbasic.setSort_id(-1000L);
            dbm_normbasic.setNorm_cname("测试修改标准hll");
            dbm_normbasic.setNorm_ename("标准英文名");
            dbm_normbasic.setNorm_aname("标准别名");
            dbm_normbasic.setDbm_domain("标准值域");
            dbm_normbasic.setCol_len(10L);
            dbm_normbasic.setDecimal_point(2L);
            dbm_normbasic.setFormulator("标准制定人");
            dbm_normbasic.setNorm_status(IsFlag.Shi.getCode());
            dbm_normbasic.setCreate_user(String.valueOf(USER_ID));
            dbm_normbasic.setCreate_date(DateUtil.getSysDate());
            dbm_normbasic.setCreate_time(DateUtil.getSysTime());
            dbm_normbasic.add(db);
            //测试查询
            dbm_normbasic.setBasic_id(-3000L);
            dbm_normbasic.setSort_id(-1000L);
            dbm_normbasic.setNorm_cname("测试查询标准hll");
            dbm_normbasic.setNorm_ename("标准英文名");
            dbm_normbasic.setNorm_aname("标准别名");
            dbm_normbasic.setDbm_domain("标准值域");
            dbm_normbasic.setCol_len(10L);
            dbm_normbasic.setDecimal_point(2L);
            dbm_normbasic.setFormulator("标准制定人");
            dbm_normbasic.setNorm_status(IsFlag.Shi.getCode());
            dbm_normbasic.setCreate_user(String.valueOf(USER_ID));
            dbm_normbasic.setCreate_date(DateUtil.getSysDate());
            dbm_normbasic.setCreate_time(DateUtil.getSysTime());
            dbm_normbasic.add(db);
            //测试批量删除
            dbm_normbasic.setBasic_id(-4000L);
            dbm_normbasic.setSort_id(-1000L);
            dbm_normbasic.setNorm_cname("测试批量删除标准hll");
            dbm_normbasic.setNorm_ename("标准英文名");
            dbm_normbasic.setNorm_aname("标准别名");
            dbm_normbasic.setDbm_domain("标准值域");
            dbm_normbasic.setCol_len(10L);
            dbm_normbasic.setDecimal_point(2L);
            dbm_normbasic.setFormulator("标准制定人");
            dbm_normbasic.setNorm_status(IsFlag.Shi.getCode());
            dbm_normbasic.setCreate_user(String.valueOf(USER_ID));
            dbm_normbasic.setCreate_date(DateUtil.getSysDate());
            dbm_normbasic.setCreate_time(DateUtil.getSysTime());
            dbm_normbasic.add(db);
            dbm_normbasic.setBasic_id(-4001L);
            dbm_normbasic.setSort_id(-1000L);
            dbm_normbasic.setNorm_cname("测试批量删除标准hll");
            dbm_normbasic.setNorm_ename("标准英文名");
            dbm_normbasic.setNorm_aname("标准别名");
            dbm_normbasic.setDbm_domain("标准值域");
            dbm_normbasic.setCol_len(10L);
            dbm_normbasic.setDecimal_point(2L);
            dbm_normbasic.setFormulator("标准制定人");
            dbm_normbasic.setNorm_status(IsFlag.Shi.getCode());
            dbm_normbasic.setCreate_user(String.valueOf(USER_ID));
            dbm_normbasic.setCreate_date(DateUtil.getSysDate());
            dbm_normbasic.setCreate_time(DateUtil.getSysTime());
            dbm_normbasic.add(db);
            //测试检索
            dbm_normbasic.setBasic_id(-5000L);
            dbm_normbasic.setSort_id(-1000L);
            dbm_normbasic.setNorm_cname("测试检索标准hll");
            dbm_normbasic.setNorm_ename("标准英文名");
            dbm_normbasic.setNorm_aname("标准别名");
            dbm_normbasic.setDbm_domain("标准值域");
            dbm_normbasic.setCol_len(10L);
            dbm_normbasic.setDecimal_point(2L);
            dbm_normbasic.setFormulator("标准制定人");
            dbm_normbasic.setNorm_status(IsFlag.Shi.getCode());
            dbm_normbasic.setCreate_user(String.valueOf(USER_ID));
            dbm_normbasic.setCreate_date(DateUtil.getSysDate());
            dbm_normbasic.setCreate_time(DateUtil.getSysTime());
            dbm_normbasic.add(db);
            dbm_normbasic.setBasic_id(-5001L);
            dbm_normbasic.setSort_id(-1000L);
            dbm_normbasic.setNorm_cname("测试检索标准hll");
            dbm_normbasic.setNorm_ename("标准英文名");
            dbm_normbasic.setNorm_aname("标准别名");
            dbm_normbasic.setDbm_domain("标准值域");
            dbm_normbasic.setCol_len(10L);
            dbm_normbasic.setDecimal_point(2L);
            dbm_normbasic.setFormulator("标准制定人");
            dbm_normbasic.setNorm_status(IsFlag.Fou.getCode());
            dbm_normbasic.setCreate_user(String.valueOf(USER_ID));
            dbm_normbasic.setCreate_date(DateUtil.getSysDate());
            dbm_normbasic.setCreate_time(DateUtil.getSysTime());
            dbm_normbasic.add(db);
            //测试发布
            dbm_normbasic.setBasic_id(-6000L);
            dbm_normbasic.setSort_id(-1000L);
            dbm_normbasic.setNorm_cname("测试发布标准hll");
            dbm_normbasic.setNorm_ename("标准英文名");
            dbm_normbasic.setNorm_aname("标准别名");
            dbm_normbasic.setDbm_domain("标准值域");
            dbm_normbasic.setCol_len(10L);
            dbm_normbasic.setDecimal_point(2L);
            dbm_normbasic.setFormulator("标准制定人");
            dbm_normbasic.setNorm_status(IsFlag.Shi.getCode());
            dbm_normbasic.setCreate_user(String.valueOf(USER_ID));
            dbm_normbasic.setCreate_date(DateUtil.getSysDate());
            dbm_normbasic.setCreate_time(DateUtil.getSysTime());
            dbm_normbasic.add(db);
            //测试批量发布
            dbm_normbasic.setBasic_id(-7000L);
            dbm_normbasic.setSort_id(-1000L);
            dbm_normbasic.setNorm_cname("测试批量发布标准hll");
            dbm_normbasic.setNorm_ename("标准英文名");
            dbm_normbasic.setNorm_aname("标准别名");
            dbm_normbasic.setDbm_domain("标准值域");
            dbm_normbasic.setCol_len(10L);
            dbm_normbasic.setDecimal_point(2L);
            dbm_normbasic.setFormulator("标准制定人");
            dbm_normbasic.setNorm_status(IsFlag.Fou.getCode());
            dbm_normbasic.setCreate_user(String.valueOf(USER_ID));
            dbm_normbasic.setCreate_date(DateUtil.getSysDate());
            dbm_normbasic.setCreate_time(DateUtil.getSysTime());
            dbm_normbasic.add(db);
            dbm_normbasic.setBasic_id(-7001L);
            dbm_normbasic.setSort_id(-1000L);
            dbm_normbasic.setNorm_cname("测试批量发布标准hll");
            dbm_normbasic.setNorm_ename("标准英文名");
            dbm_normbasic.setNorm_aname("标准别名");
            dbm_normbasic.setDbm_domain("标准值域");
            dbm_normbasic.setCol_len(10L);
            dbm_normbasic.setDecimal_point(2L);
            dbm_normbasic.setFormulator("标准制定人");
            dbm_normbasic.setNorm_status(IsFlag.Fou.getCode());
            dbm_normbasic.setCreate_user(String.valueOf(USER_ID));
            dbm_normbasic.setCreate_date(DateUtil.getSysDate());
            dbm_normbasic.setCreate_time(DateUtil.getSysTime());
            dbm_normbasic.add(db);
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
            long dsiNum = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_sort_info.TableName +
                    " where sort_id =?", -1000L).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_sort_info 表此条数据删除后,记录数应该为0", dsiNum, is(0L));
            //删除 Dbm_normbasic 测试数据
            SqlOperator.execute(db,
                    "delete from " + Dbm_normbasic.TableName + " where basic_id=?", -1000L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_normbasic.TableName + " where basic_id=?", -2000L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_normbasic.TableName + " where basic_id=?", -3000L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_normbasic.TableName + " where basic_id=?", -4000L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_normbasic.TableName + " where basic_id=?", -4001L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_normbasic.TableName + " where basic_id=?", -5000L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_normbasic.TableName + " where basic_id=?", -5001L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_normbasic.TableName + " where basic_id=?", -6000L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_normbasic.TableName + " where basic_id=?", -7000L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_normbasic.TableName + " where basic_id=?", -7001L);
            SqlOperator.execute(db,
                    "delete from " + Dbm_normbasic.TableName + " where norm_cname=?", "测试新增标准hll");
            SqlOperator.commitTransaction(db);
            long num;
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_normbasic.TableName +
                    " where basic_id in (-1000, -2000, -3000, -4000, -4001, -5000, -5001, -6000, -7000, -7002)")
                    .orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_normbasic 表数据删除后,记录数应该为0", num, is(0L));
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_normbasic.TableName +
                    " where norm_cname in ('测试新增标准hll')")
                    .orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_normbasic 表数据删除后,记录数应该为0", num, is(0L));
        }
    }

    @Method(desc = "添加标准测试方法",
            logicStep = "添加标准")
    @Test
    public void addDbmNormbasicInfo() {
        //1.添加标准信息
        //1-1.正确添加
        bodyString = new HttpClient()
                .addData("sort_id", -1000L)
                .addData("norm_cname", "测试新增标准hll")
                .addData("norm_ename", "标准英文名")
                .addData("norm_aname", "标准别名")
                .addData("dbm_domain", "标准值域")
                .addData("col_len", 10)
                .addData("decimal_point", 2)
                .addData("formulator", "标准制定人")
                .addData("norm_status", IsFlag.Fou.getCode())
                .post(getActionUrl("addDbmNormbasicInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查新增的数据是否正确
                Optional<Dbm_normbasic> dbm_normbasic = SqlOperator.queryOneObject(db, Dbm_normbasic.class,
                        "select * from " + Dbm_normbasic.TableName + " where norm_cname = '测试新增标准hll'");
                if (dbm_normbasic.orElse(null) != null) {
                    assertThat(dbm_normbasic.get().getSort_id(), is(-1000L));
                    assertThat(dbm_normbasic.get().getNorm_cname(), is("测试新增标准hll"));
                    assertThat(dbm_normbasic.get().getNorm_ename(), is("标准英文名"));
                    assertThat(dbm_normbasic.get().getNorm_aname(), is("标准别名"));
                    assertThat(dbm_normbasic.get().getDbm_domain(), is("标准值域"));
                    assertThat(dbm_normbasic.get().getCol_len(), is(10L));
                    assertThat(dbm_normbasic.get().getDecimal_point(), is(2L));
                    assertThat(dbm_normbasic.get().getFormulator(), is("标准制定人"));
                    assertThat(dbm_normbasic.get().getNorm_status(), is("0"));
                }
            }
        }
        //1-2.标准中文名为空
        bodyString = new HttpClient()
                .addData("sort_id", -1000L)
                .addData("norm_ename", "标准英文名")
                .addData("norm_aname", "标准别名")
                .addData("dbm_domain", "标准值域")
                .addData("col_len", 10)
                .addData("decimal_point", 2)
                .addData("formulator", "标准制定人")
                .addData("norm_status", IsFlag.Fou.getCode())
                .post(getActionUrl("addDbmNormbasicInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(false)));
        //1-3.标准英文名为空
        bodyString = new HttpClient()
                .addData("sort_id", -1000L)
                .addData("norm_cname", "标准中文名")
                .addData("norm_aname", "标准别名")
                .addData("dbm_domain", "标准值域")
                .addData("col_len", 10)
                .addData("decimal_point", 2)
                .addData("formulator", "标准制定人")
                .addData("norm_status", IsFlag.Fou.getCode())
                .post(getActionUrl("addDbmNormbasicInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(false)));
        //1-4.标准别名为空
        bodyString = new HttpClient()
                .addData("sort_id", -1000L)
                .addData("norm_cname", "标准中文名")
                .addData("norm_ename", "标准英文名")
                .addData("dbm_domain", "标准值域")
                .addData("col_len", 10)
                .addData("decimal_point", 2)
                .addData("formulator", "标准制定人")
                .addData("norm_status", IsFlag.Fou.getCode())
                .post(getActionUrl("addDbmNormbasicInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(false)));
        //1-5.标准值域为空
        bodyString = new HttpClient()
                .addData("sort_id", -1000L)
                .addData("norm_cname", "标准中文名")
                .addData("norm_ename", "标准英文名")
                .addData("norm_aname", "标准别名")
                .addData("col_len", 10)
                .addData("decimal_point", 2)
                .addData("formulator", "标准制定人")
                .addData("norm_status", IsFlag.Fou.getCode())
                .post(getActionUrl("addDbmNormbasicInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(false)));
        //1-6.标准字段长度为空
        bodyString = new HttpClient()
                .addData("sort_id", -1000L)
                .addData("norm_cname", "标准中文名")
                .addData("norm_ename", "标准英文名")
                .addData("norm_aname", "标准别名")
                .addData("dbm_domain", "标准值域")
                .addData("decimal_point", 2)
                .addData("formulator", "标准制定人")
                .addData("norm_status", IsFlag.Fou.getCode())
                .post(getActionUrl("addDbmNormbasicInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(false)));
        //1-7.标准小数长度为空
        bodyString = new HttpClient()
                .addData("sort_id", -1000L)
                .addData("norm_cname", "标准中文名")
                .addData("norm_ename", "标准英文名")
                .addData("norm_aname", "标准别名")
                .addData("dbm_domain", "标准值域")
                .addData("col_len", 10)
                .addData("formulator", "标准制定人")
                .addData("norm_status", IsFlag.Fou.getCode())
                .post(getActionUrl("addDbmNormbasicInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(false)));
        //1-8.标准制定人为空
        bodyString = new HttpClient()
                .addData("sort_id", -1000L)
                .addData("norm_cname", "标准中文名")
                .addData("norm_ename", "标准英文名")
                .addData("norm_aname", "标准别名")
                .addData("dbm_domain", "标准值域")
                .addData("col_len", 10)
                .addData("decimal_point", 2)
                .addData("norm_status", IsFlag.Fou.getCode())
                .post(getActionUrl("addDbmNormbasicInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(false)));
        //1-9.标准发布状态为空
        bodyString = new HttpClient()
                .addData("sort_id", -1000L)
                .addData("norm_cname", "标准中文名")
                .addData("norm_ename", "标准英文名")
                .addData("norm_aname", "标准别名")
                .addData("dbm_domain", "标准值域")
                .addData("col_len", 10)
                .addData("decimal_point", 2)
                .addData("formulator", "标准制定人")
                .post(getActionUrl("addDbmNormbasicInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(false)));
    }

    @Method(desc = "删除标准信息测试方法",
            logicStep = "删除标准信息")
    @Test
    public void deleteDbmNormbasicInfo() {
        //1.id删除分类信息
        bodyString = new HttpClient()
                .addData("basic_id", -1000L)
                .post(getActionUrl("deleteDbmNormbasicInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                OptionalLong number = SqlOperator.queryNumber(db,
                        "select count(*) from " + Dbm_normbasic.TableName + " where basic_id=?", -1000L);
                assertThat("表 Dbm_normbasic 数据删除成功", number.orElse(-1), is(0L));
            }
        }
    }

    @Method(desc = "修改标准测试方法",
            logicStep = "修改标准")
    @Test
    public void updateDbmNormbasicInfo() {
        //1.修改标准信息
        //1-1.正确修改
        bodyString = new HttpClient()
                .addData("basic_id", -2000L)
                .addData("sort_id", -1000L)
                .addData("norm_cname", "测试修改标准hll-2000")
                .addData("norm_ename", "标准英文名-2000")
                .addData("norm_aname", "标准别名-2000")
                .addData("dbm_domain", "标准值域-2000")
                .addData("col_len", 20)
                .addData("decimal_point", 4)
                .addData("formulator", "标准制定人-2000")
                .addData("norm_status", IsFlag.Shi.getCode())
                .post(getActionUrl("updateDbmNormbasicInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查新增的数据是否正确
                Optional<Dbm_normbasic> dbm_normbasic = SqlOperator.queryOneObject(db, Dbm_normbasic.class,
                        "select * from " + Dbm_normbasic.TableName + " where basic_id = ?", -2000L);
                if (dbm_normbasic.orElse(null) != null) {
                    assertThat(dbm_normbasic.get().getSort_id(), is(-1000L));
                    assertThat(dbm_normbasic.get().getNorm_cname(), is("测试修改标准hll-2000"));
                    assertThat(dbm_normbasic.get().getNorm_ename(), is("标准英文名-2000"));
                    assertThat(dbm_normbasic.get().getNorm_aname(), is("标准别名-2000"));
                    assertThat(dbm_normbasic.get().getDbm_domain(), is("标准值域-2000"));
                    assertThat(dbm_normbasic.get().getCol_len(), is(20L));
                    assertThat(dbm_normbasic.get().getDecimal_point(), is(4L));
                    assertThat(dbm_normbasic.get().getFormulator(), is("标准制定人-2000"));
                    assertThat(dbm_normbasic.get().getNorm_status(), is("1"));
                }
            }
        }
        //1-2.标准编号不存在
        bodyString = new HttpClient()
                .addData("basic_id", -20000L)
                .addData("sort_id", -1000L)
                .addData("norm_cname", "测试修改标准hll-2000")
                .addData("norm_ename", "标准英文名-2000")
                .addData("norm_aname", "标准别名-2000")
                .addData("dbm_domain", "标准值域-2000")
                .addData("col_len", 20)
                .addData("decimal_point", 4)
                .addData("formulator", "标准制定人-2000")
                .addData("norm_status", IsFlag.Shi.getCode())
                .post(getActionUrl("updateDbmNormbasicInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(false)));
        //1-2.标准所属分类为空
        bodyString = new HttpClient()
                .addData("basic_id", -2000L)
                .addData("norm_cname", "测试修改标准hll-2000")
                .addData("norm_ename", "标准英文名-2000")
                .addData("norm_aname", "标准别名-2000")
                .addData("dbm_domain", "标准值域-2000")
                .addData("col_len", 20)
                .addData("decimal_point", 4)
                .addData("formulator", "标准制定人-2000")
                .addData("norm_status", IsFlag.Shi.getCode())
                .post(getActionUrl("updateDbmNormbasicInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(false)));
        //1-3.标准所属中文名为空
        bodyString = new HttpClient()
                .addData("basic_id", -2000L)
                .addData("sort_id", -1000L)
                .addData("norm_ename", "标准英文名-2000")
                .addData("norm_aname", "标准别名-2000")
                .addData("dbm_domain", "标准值域-2000")
                .addData("col_len", 20)
                .addData("decimal_point", 4)
                .addData("formulator", "标准制定人-2000")
                .addData("norm_status", IsFlag.Shi.getCode())
                .post(getActionUrl("updateDbmNormbasicInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(false)));
        //1-4.标准所属英文名为空
        bodyString = new HttpClient()
                .addData("basic_id", -2000L)
                .addData("sort_id", -1000L)
                .addData("norm_cname", "测试修改标准hll-2000")
                .addData("norm_aname", "标准别名-2000")
                .addData("dbm_domain", "标准值域-2000")
                .addData("col_len", 20)
                .addData("decimal_point", 4)
                .addData("formulator", "标准制定人-2000")
                .addData("norm_status", IsFlag.Shi.getCode())
                .post(getActionUrl("updateDbmNormbasicInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(false)));
        //1-4.标准所属别名为空
        bodyString = new HttpClient()
                .addData("basic_id", -2000L)
                .addData("sort_id", -1000L)
                .addData("norm_cname", "测试修改标准hll-2000")
                .addData("norm_ename", "标准英文名-2000")
                .addData("dbm_domain", "标准值域-2000")
                .addData("col_len", 20)
                .addData("decimal_point", 4)
                .addData("formulator", "标准制定人-2000")
                .addData("norm_status", IsFlag.Shi.getCode())
                .post(getActionUrl("updateDbmNormbasicInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(false)));
        //1-5.标准所属值域为空
        bodyString = new HttpClient()
                .addData("basic_id", -2000L)
                .addData("sort_id", -1000L)
                .addData("norm_cname", "测试修改标准hll-2000")
                .addData("norm_ename", "标准英文名-2000")
                .addData("norm_aname", "标准别名-2000")
                .addData("col_len", 20)
                .addData("decimal_point", 4)
                .addData("formulator", "标准制定人-2000")
                .addData("norm_status", IsFlag.Shi.getCode())
                .post(getActionUrl("updateDbmNormbasicInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(false)));
        //1-6.标准字段长度为空
        bodyString = new HttpClient()
                .addData("basic_id", -2000L)
                .addData("sort_id", -1000L)
                .addData("norm_cname", "测试修改标准hll-2000")
                .addData("norm_ename", "标准英文名-2000")
                .addData("norm_aname", "标准别名-2000")
                .addData("dbm_domain", "标准值域-2000")
                .addData("decimal_point", 4)
                .addData("formulator", "标准制定人-2000")
                .addData("norm_status", IsFlag.Shi.getCode())
                .post(getActionUrl("updateDbmNormbasicInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(false)));
        //1-7.标准小数长度为空
        bodyString = new HttpClient()
                .addData("basic_id", -2000L)
                .addData("sort_id", -1000L)
                .addData("norm_cname", "测试修改标准hll-2000")
                .addData("norm_ename", "标准英文名-2000")
                .addData("norm_aname", "标准别名-2000")
                .addData("dbm_domain", "标准值域-2000")
                .addData("col_len", 20)
                .addData("formulator", "标准制定人-2000")
                .addData("norm_status", IsFlag.Shi.getCode())
                .post(getActionUrl("updateDbmNormbasicInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(false)));
        //1-7.标准制定人为空
        bodyString = new HttpClient()
                .addData("basic_id", -2000L)
                .addData("sort_id", -1000L)
                .addData("norm_cname", "测试修改标准hll-2000")
                .addData("norm_ename", "标准英文名-2000")
                .addData("norm_aname", "标准别名-2000")
                .addData("dbm_domain", "标准值域-2000")
                .addData("col_len", 20)
                .addData("decimal_point", 4)
                .addData("norm_status", IsFlag.Shi.getCode())
                .post(getActionUrl("updateDbmNormbasicInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(false)));
        //1-8.标准发布状态为空
        bodyString = new HttpClient()
                .addData("basic_id", -2000L)
                .addData("sort_id", -1000L)
                .addData("norm_cname", "测试修改标准hll-2000")
                .addData("norm_ename", "标准英文名-2000")
                .addData("norm_aname", "标准别名-2000")
                .addData("dbm_domain", "标准值域-2000")
                .addData("col_len", 20)
                .addData("decimal_point", 4)
                .addData("formulator", "标准制定人-2000")
                .post(getActionUrl("updateDbmNormbasicInfo")).getBodyString();
        JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                assertThat(ar.isSuccess(), is(false)));
    }

    @Method(desc = "获取所有标准信息",
            logicStep = "获取所有标准信息")
    @Test
    public void getDbmNormbasicInfo() {
        bodyString = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", 3)
                .post(getActionUrl("getDbmNormbasicInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("totalSize"), is(8));
        }
    }

    @Method(desc = "获取所有标准信息(只获取basic_id,norm_cname)测试方法",
            logicStep = "获取所有标准信息(只获取basic_id,norm_cname)")
    @Test
    public void getDbmNormbasicIdAndNameInfo() {
        //获取所有标准的id和中文名
        bodyString = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", 6)
                .post(getActionUrl("getDbmNormbasicIdAndNameInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("totalSize"), is(8));
        }
    }

    @Method(desc = "根据Id获取标准信息测试方法",
            logicStep = "根据Id获取标准信息")
    @Test
    public void getDbmNormbasicInfoById() {
        //id获取标准信息
        bodyString = new HttpClient()
                .addData("basic_id", -3000)
                .post(getActionUrl("getDbmNormbasicInfoById")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            //检查数据正确性
            assertThat(ar.getDataForMap().get("sort_id"), is(-1000));
            assertThat(ar.getDataForMap().get("norm_cname"), is("测试查询标准hll"));
            assertThat(ar.getDataForMap().get("norm_ename"), is("标准英文名"));
            assertThat(ar.getDataForMap().get("norm_aname"), is("标准别名"));
            assertThat(ar.getDataForMap().get("dbm_domain"), is("标准值域"));
            assertThat(ar.getDataForMap().get("col_len"), is(10));
            assertThat(ar.getDataForMap().get("decimal_point"), is(2));
            assertThat(ar.getDataForMap().get("formulator"), is("标准制定人"));
            assertThat(ar.getDataForMap().get("norm_status"), is("1"));
            assertThat(ar.getDataForMap().get("create_user"), is("-1000"));
        }
    }

    @Method(desc = "根据sort_id获取标准信息测试方法",
            logicStep = "根据sort_id获取标准信息")
    @Test
    public void getDbmNormbasicInfoBySortId() {
        //sort_id获取标准信息
        bodyString = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", 6)
                .addData("sort_id", -1000L)
                .post(getActionUrl("getDbmNormbasicInfoBySortId")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("totalSize"), is(10));
        }
    }

    @Method(desc = "根据发布状态获取标准信息测试方法",
            logicStep = "根据发布状态获取标准信息")
    @Test
    public void getDbmNormbasicByStatus() {
        //norm_status获取标准信息
        bodyString = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", 6)
                .addData("norm_status", 0)
                .post(getActionUrl("getDbmNormbasicByStatus")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("totalSize"), is(2));
        }
    }

    @Method(desc = "检索标准信息测试方法",
            logicStep = "检索标准信息")
    @Test
    public void searchDbmNormbasic() {
        //检索标准信息(检索条件+状态+sort_id)
        bodyString = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", 6)
                .addData("search_cond", "测试检索")
                .addData("status", 0)
                .addData("sort_id", -1000)
                .post(getActionUrl("searchDbmNormbasic")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("totalSize"), is(1));
        }
        //检索标准信息(检索条件)
        bodyString = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", 6)
                .addData("search_cond", "测试检索")
                .post(getActionUrl("searchDbmNormbasic")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("totalSize"), is(2));
        }
        //检索标准信息(状态)
        bodyString = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", 6)
                .addData("status", "1")
                .post(getActionUrl("searchDbmNormbasic")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("totalSize"), is(6));
        }
        //检索标准信息(sort_id)
        bodyString = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", 6)
                .addData("sort_id", -1000L)
                .post(getActionUrl("searchDbmNormbasic")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            assertThat(ar.getDataForMap().get("totalSize"), is(8));
        }
    }

    @Method(desc = "根据标准id发布标准测试方法",
            logicStep = "根据标准id发布标准")
    @Test
    public void releaseDbmNormbasicById() {
        //id发布标准信息
        bodyString = new HttpClient()
                .addData("basic_id", -6000L)
                .post(getActionUrl("releaseDbmNormbasicById")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查新增的数据是否正确
                Optional<Dbm_normbasic> dbm_normbasic = SqlOperator.queryOneObject(db, Dbm_normbasic.class,
                        "select * from " + Dbm_normbasic.TableName + " where basic_id = -6000");
                if (dbm_normbasic.orElse(null) != null) {
                    assertThat(dbm_normbasic.get().getSort_id(), is(-1000L));
                    assertThat(dbm_normbasic.get().getNorm_cname(), is("测试发布标准hll"));
                    assertThat(dbm_normbasic.get().getNorm_ename(), is("标准英文名"));
                    assertThat(dbm_normbasic.get().getNorm_aname(), is("标准别名"));
                    assertThat(dbm_normbasic.get().getDbm_domain(), is("标准值域"));
                    assertThat(dbm_normbasic.get().getCol_len(), is(10L));
                    assertThat(dbm_normbasic.get().getDecimal_point(), is(2L));
                    assertThat(dbm_normbasic.get().getFormulator(), is("标准制定人"));
                    assertThat(dbm_normbasic.get().getNorm_status(), is("1"));
                }
            }
        }
    }

    @Method(desc = "根据标准id数组批量发布标准测试方法",
            logicStep = "根据标准id数组批量发布标准")
    @Test
    public void batchReleaseDbmNormbasic() {
        //1.id数组发布标准信息
        bodyString = new HttpClient()
                .addData("basic_id_s", new long[]{-7000L, -7001L})
                .post(getActionUrl("batchReleaseDbmNormbasic")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查发布的数据是否正确
                List<Dbm_normbasic> dbm_normbasics = SqlOperator.queryList(db, Dbm_normbasic.class,
                        "select * from " + Dbm_normbasic.TableName + " where basic_id in (-7000,-7001)");
                dbm_normbasics.forEach(dbm_normbasic -> assertThat(dbm_normbasic.getNorm_status(), is("1")));
            }
        }
    }

    @Method(desc = "根据标准id数组批量删除标准测试方法",
            logicStep = "根据标准id数组批量删除标准")
    @Test
    public void batchDeleteDbmNormbasic() {
        //1.id数组删除标准信息
        bodyString = new HttpClient()
                .addData("basic_id_s", new long[]{-4000L, -4001L})
                .post(getActionUrl("batchDeleteDbmNormbasic")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                //检查发布的数据是否正确
                OptionalLong number = SqlOperator.queryNumber(db,
                        "select count(*) from " + Dbm_normbasic.TableName + " where basic_id in(-4000,-4001)");
                assertThat("表 Dbm_sort_info 数据删除成功", number.orElse(-1), is(0L));
            }
        }
    }
}
