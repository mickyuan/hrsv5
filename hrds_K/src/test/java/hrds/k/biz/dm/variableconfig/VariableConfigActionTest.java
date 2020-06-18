package hrds.k.biz.dm.variableconfig;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Dq_sys_cfg;
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class VariableConfigActionTest extends WebBaseTestCase {

    //测试数据的用户ID
    private static final long USER_ID = -1000L;
    //测试数据的部门ID
    private static final long DEP_ID = -1000L;

    @Method(desc = "初始化测试用例依赖表数据", logicStep = "初始化测试用例依赖表数据")
    @BeforeClass
    public static void before() {
        DatabaseWrapper db = new DatabaseWrapper();
        try {
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
            //初始化 Dq_sys_cfg
            Dq_sys_cfg dq_sys_cfg = new Dq_sys_cfg();
            dq_sys_cfg.setSys_var_id("-1001");
            dq_sys_cfg.setVar_name("hll_测试删除变量名");
            dq_sys_cfg.setVar_value("hll_测试删除变量值");
            dq_sys_cfg.setApp_updt_dt("20190101");
            dq_sys_cfg.setApp_updt_ti(DateUtil.getSysTime());
            dq_sys_cfg.setUser_id(USER_ID);
            dq_sys_cfg.add(db);
            dq_sys_cfg.setSys_var_id("-1002");
            dq_sys_cfg.setVar_name("hll_测试修改变量名");
            dq_sys_cfg.setVar_value("hll_测试修改变量值");
            dq_sys_cfg.setApp_updt_dt("20190101");
            dq_sys_cfg.setApp_updt_ti(DateUtil.getSysTime());
            dq_sys_cfg.setUser_id(USER_ID);
            dq_sys_cfg.add(db);
            dq_sys_cfg.setSys_var_id("-1003");
            dq_sys_cfg.setVar_name("hll_测试查询变量名");
            dq_sys_cfg.setVar_value("hll_测试查询变量值");
            dq_sys_cfg.setApp_updt_dt("20190101");
            dq_sys_cfg.setApp_updt_ti(DateUtil.getSysTime());
            dq_sys_cfg.setUser_id(USER_ID);
            dq_sys_cfg.add(db);
            dq_sys_cfg.setSys_var_id("-1014");
            dq_sys_cfg.setVar_name("hll_测试检索变量名_1014");
            dq_sys_cfg.setVar_value("hll_测试检索变量名_1014");
            dq_sys_cfg.setApp_updt_dt("20190101");
            dq_sys_cfg.setApp_updt_ti(DateUtil.getSysTime());
            dq_sys_cfg.setUser_id(USER_ID);
            dq_sys_cfg.add(db);
            dq_sys_cfg.setSys_var_id("-1024");
            dq_sys_cfg.setVar_name("hll_测试检索变量名_1024");
            dq_sys_cfg.setVar_value("hll_测试检索变量名_1024");
            dq_sys_cfg.setApp_updt_dt("20190101");
            dq_sys_cfg.setApp_updt_ti(DateUtil.getSysTime());
            dq_sys_cfg.setUser_id(USER_ID);
            dq_sys_cfg.add(db);
            dq_sys_cfg.setSys_var_id("-1034");
            dq_sys_cfg.setVar_name("hll_测试检索变量值_1034");
            dq_sys_cfg.setVar_value("hll_测试检索变量值_1034");
            dq_sys_cfg.setApp_updt_dt("20190101");
            dq_sys_cfg.setApp_updt_ti(DateUtil.getSysTime());
            dq_sys_cfg.setUser_id(USER_ID);
            dq_sys_cfg.add(db);
            dq_sys_cfg.setSys_var_id("-1044");
            dq_sys_cfg.setVar_name("hll_测试检索开始时间_1044");
            dq_sys_cfg.setVar_value("hll_测试检索开始时间_1044");
            dq_sys_cfg.setApp_updt_dt("20200202");
            dq_sys_cfg.setApp_updt_ti(DateUtil.getSysTime());
            dq_sys_cfg.setUser_id(USER_ID);
            dq_sys_cfg.add(db);
            dq_sys_cfg.setSys_var_id("-1054");
            dq_sys_cfg.setVar_name("hll_测试检索结束时间_1044");
            dq_sys_cfg.setVar_value("hll_测试检索结束时间_1044");
            dq_sys_cfg.setApp_updt_dt("20200101");
            dq_sys_cfg.setApp_updt_ti(DateUtil.getSysTime());
            dq_sys_cfg.setUser_id(USER_ID);
            dq_sys_cfg.add(db);
            //提交所有数据库执行操作
            SqlOperator.commitTransaction(db);
            //根据初始化的 Sys_user 用户模拟登陆
            String bodyString = new HttpClient()
                    .addData("user_id", USER_ID)
                    .addData("password", "111111")
                    .post("http://127.0.0.1:8888/A/action/hrds/a/biz/login/login").getBodyString();
            JsonUtil.toObjectSafety(bodyString, ActionResult.class).ifPresent(ar ->
                    assertThat(ar.isSuccess(), is(true)));
        } catch (Exception e) {
            db.rollback();
        } finally {
            db.close();
        }
    }

    @Method(desc = "测试案例执行完成后清理测试数据", logicStep = "测试案例执行完成后清理测试数据")
    @AfterClass
    public static void after() {
        DatabaseWrapper db = new DatabaseWrapper();
        try {
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
            //删除 Dq_sys_cfg
            SqlOperator.execute(db, "delete from " + Dq_sys_cfg.TableName + " where user_id=?", USER_ID);
            SqlOperator.commitTransaction(db);
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dq_sys_cfg.TableName +
                    " where user_id =?", USER_ID).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dq_sys_cfg 表此条数据删除后,记录数应该为0", num, is(0L));
        } catch (Exception e) {
            db.rollback();
        } finally {
            db.close();
        }
    }

    @Method(desc = "添加变量配置数据信息", logicStep = "添加变量配置数据信息")
    @Test
    public void addVariableConfigDat() {
        String bodyString;
        ActionResult ar;
        //获取所有变量配置数据信息
        bodyString = new HttpClient()
                .addData("var_name", "hll_测试添加变量名_9999")
                .addData("var_value", "hll_测试添加变量值_9999")
                .post(getActionUrl("addVariableConfigDat")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
        DatabaseWrapper db = new DatabaseWrapper();
        try {
            //是否新增成功
            long number = SqlOperator.queryNumber(db,
                    "select count(*) from " + Dq_sys_cfg.TableName + " where var_name=?", "hll_测试添加变量名_9999")
                    .orElseThrow(() -> (new BusinessException("统计sql执行出错!")));
            assertThat(number, is(1L));
            //检查新增的数据是否正确
            Dq_sys_cfg dq_sys_cfg = SqlOperator.queryOneObject(db, Dq_sys_cfg.class,
                    "select * from " + Dq_sys_cfg.TableName + " where var_name=?",
                    "hll_测试添加变量名_9999").orElseThrow(() -> (new BusinessException("统计sql执行出错!")));
            assertThat(dq_sys_cfg.getVar_value(), is("hll_测试添加变量值_9999"));
        } catch (Exception e) {
            db.rollback();
        } finally {
            db.close();
        }
    }

    @Method(desc = "删除变量配置数据", logicStep = "删除变量配置数据")
    @Test
    public void deleteVariableConfigData() {
        String bodyString;
        ActionResult ar;
        bodyString = new HttpClient()
                .addData("sys_var_id_s", new long[]{-1001L})
                .post(getActionUrl("deleteVariableConfigData")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> (new BusinessException("统计sql执行出错!")));
        assertThat(ar.isSuccess(), is(true));
        DatabaseWrapper db = new DatabaseWrapper();
        try {
            //检查删除的数据
            long number = SqlOperator.queryNumber(db,
                    "select count(*) from " + Dq_sys_cfg.TableName + " where sys_var_id in(-1001)").orElseThrow(()
                    -> (new BusinessException("统计sql执行出错!")));
            assertThat("表 Dq_sys_cfg 数据删除成功", number, is(0L));
        } catch (Exception e) {
            db.rollback();
        } finally {
            db.close();
        }
    }

    @Method(desc = "修改变量配置数据信息", logicStep = "修改变量配置数据信息")
    @Test
    public void updateVariableConfigData() {
        String bodyString;
        ActionResult ar;
        //获取所有变量配置数据信息
        bodyString = new HttpClient()
                .addData("sys_var_id", -1002L)
                .addData("var_name", "hll_测试修改变量名")
                .addData("var_value", "hll_测试修改变量值_修改后")
                .post(getActionUrl("updateVariableConfigData")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
        DatabaseWrapper db = new DatabaseWrapper();
        try {
            //是否修改成功
            long number = SqlOperator.queryNumber(db,
                    "select count(*) from " + Dq_sys_cfg.TableName + " where var_name=?", "hll_测试修改变量名")
                    .orElseThrow(() -> (new BusinessException("统计sql执行出错!")));
            assertThat(number, is(1L));
            //检查新增的数据是否正确
            Dq_sys_cfg dq_sys_cfg = SqlOperator.queryOneObject(db, Dq_sys_cfg.class,
                    "select * from " + Dq_sys_cfg.TableName + " where var_name=?",
                    "hll_测试修改变量名").orElseThrow(() -> (new BusinessException("统计sql执行出错!")));
            assertThat(dq_sys_cfg.getVar_value(), is("hll_测试修改变量值_修改后"));
        } catch (Exception e) {
            db.rollback();
        } finally {
            db.close();
        }
    }

    @Method(desc = "获取所有变量配置数据信息", logicStep = "获取所有变量配置数据信息(变量不会有很多,这里不做分页)")
    @Test
    public void getVariableConfigDataInfos() {
        String bodyString;
        ActionResult ar;
        //获取所有变量配置数据信息
        bodyString = new HttpClient()
                .post(getActionUrl("getVariableConfigDataInfos")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForEntityList(Dq_sys_cfg.class).size(), is(8));
    }

    @Method(desc = "获取变量配置数据信息", logicStep = "获取变量配置数据信息")
    @Test
    public void getVariableConfigDataInfo() {
        String bodyString;
        ActionResult ar;
        //获取变量配置信息
        bodyString = new HttpClient()
                .addData("sys_var_id", -1003L)
                .post(getActionUrl("getVariableConfigDataInfo")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForMap().get("var_name"), is("hll_测试查询变量名"));
        assertThat(ar.getDataForMap().get("var_value"), is("hll_测试查询变量值"));
    }

    @Method(desc = "搜索变量信息", logicStep = "搜索变量信息")
    @Test
    public void searchVariableConfigData() {
        String bodyString;
        ActionResult ar;
        //获取搜索变量信息根据 var_name
        bodyString = new HttpClient()
                .addData("var_name", "测试检索变量名")
                .post(getActionUrl("searchVariableConfigData")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForEntityList(Dq_sys_cfg.class).size(), is(2));
        //获取搜索变量信息根据 var_value
        bodyString = new HttpClient()
                .addData("var_value", "测试检索变量值")
                .post(getActionUrl("searchVariableConfigData")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForEntityList(Dq_sys_cfg.class).size(), is(1));
        //获取搜索变量信息根据 start_date
        bodyString = new HttpClient()
                .addData("start_date", "20200101")
                .post(getActionUrl("searchVariableConfigData")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForEntityList(Dq_sys_cfg.class).size(), is(2));
        //获取搜索变量信息根据 end_date
        bodyString = new HttpClient()
                .addData("end_date", "20200101")
                .post(getActionUrl("searchVariableConfigData")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                "获取返回ActionResult结果失败!"));
        assertThat(ar.isSuccess(), is(true));
        assertThat(ar.getDataForEntityList(Dq_sys_cfg.class).size(), is(7));
    }
}