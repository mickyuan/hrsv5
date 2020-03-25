package hrds.k.biz.dbm.dataimport;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.netclient.http.SubmitMediaType;
import fd.ng.web.action.ActionResult;
import hrds.commons.entity.*;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "数据导入测试类", author = "BY-HLL", createdate = "2020/3/11 0011 下午 03:24")
public class DbmDataImportActionTest extends WebBaseTestCase {

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
            sysUser.setUser_type("02");
            sysUser.setUsertype_group("37");
            sysUser.setUser_state("1");
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
            //删除测试导入的数据
            SqlOperator.execute(db, "delete from " + Dbm_sort_info.TableName + " where create_user='-1000'");
            SqlOperator.execute(db, "delete from " + Dbm_normbasic.TableName + " where create_user='-1000'");
            SqlOperator.execute(db, "delete from " + Dbm_code_type_info.TableName + " where create_user='-1000'");
            SqlOperator.execute(db, "delete from " + Dbm_code_item_info.TableName +
                    " where code_remark='代码项测试方法测试导入'");
            SqlOperator.commitTransaction(db);
            long num;
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_sort_info.TableName +
                    " where create_user='-1000'").orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_sort_info 表此条数据删除后,记录数应该为0", num, is(0L));
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_normbasic.TableName +
                    " where create_user='-1000'").orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_normbasic 表此条数据删除后,记录数应该为0", num, is(0L));
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_type_info.TableName +
                    " where create_user='-1000'").orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_code_type_info 表此条数据删除后,记录数应该为0", num, is(0L));
            num = SqlOperator.queryNumber(db, "select count(1) from " + Dbm_code_item_info.TableName +
                    " where code_remark='代码项测试方法测试导入'").orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Dbm_code_item_info 表此条数据删除后,记录数应该为0", num, is(0L));
        }
    }

    @Method(desc = "批量导入标准元数据信息测试方法",
            logicStep = "批量导入标准元数据信息")
    @Test
    public void importExcelData() {
        //测试导入元数据
        String filePath = FileUtil.getFile("src/test/java/hrds/k/biz/dbmdataimport/upload/" +
                "dbm_test_import.xlsx").getAbsolutePath();
        bodyString = new HttpClient()
                .reset(SubmitMediaType.MULTIPART)
                .addFile("pathName", new File(filePath))
                .post(getActionUrl("importExcelData")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElse(null);
        if (ar != null) {
            assertThat(ar.isSuccess(), is(true));
            try (DatabaseWrapper db = new DatabaseWrapper()) {
                OptionalLong number;
                //检查 Dbm_sort_info
                number = SqlOperator.queryNumber(db, "select count(*) from " + Dbm_sort_info.TableName +
                        " where create_user='-1000'");
                assertThat("表 Dbm_sort_info 数据删除成功", number.orElse(-1), is(19L));
                //检查 Dbm_normbasic
                number = SqlOperator.queryNumber(db, "select count(*) from " +
                        Dbm_normbasic.TableName + " where create_user='-1000'");
                assertThat("表 Dbm_sort_info 数据删除成功", number.orElse(-1), is(17L));
                //检查 Dbm_code_type_info
                number = SqlOperator.queryNumber(db, "select count(*) from " +
                        Dbm_code_type_info.TableName + " where create_user='-1000'");
                assertThat("表 Dbm_sort_info 数据删除成功", number.orElse(-1), is(6L));
                //检查 Dbm_code_item_info
                number = SqlOperator.queryNumber(db, "select count(*) from " +
                        Dbm_code_item_info.TableName + " where code_remark='代码项测试方法测试导入'");
                assertThat("表 Dbm_sort_info 数据删除成功", number.orElse(-1), is(48L));
            }
        }
    }
}
