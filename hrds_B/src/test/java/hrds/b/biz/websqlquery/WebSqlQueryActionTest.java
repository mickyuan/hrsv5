package hrds.b.biz.websqlquery;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.AgentType;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class WebSqlQueryActionTest extends WebBaseTestCase {
    //部门id
    private static final long DEP_ID = 5000000000L;
    //数据源id
    private static final long SOURCE_ID = 5000000000L;
    //AGENT_ID
    private static final long AGENT_ID = 5000000000L;
    //登录用户id
    private static final long USER_ID = 5000L;
    //数据库任务设置id
    private static final long DATABASE_ID = 5000000000L;

    @Method(desc = "初始化测试用例依赖表数据",
            logicStep = "1.初始化依赖表数据" +
                    "1-1.初始化 Data_source 数据" +
                    "1-2.初始化 Source_relation_dep 数据" +
                    "1-3.初始化 Agent_info 数据" +
                    "1-4.初始化 Database_set 数据" +
                    "1-5.初始化 Data_mart_info 数据" +
                    "1-6.初始化 Datatable_info 数据" +
                    "1-7.初始化 Source_file_attribute 数据" +
                    "2.模拟登陆" +
                    "2-1.初始化模拟登陆了数据" +
                    "2-1-1.初始化模拟登陆用户" +
                    "2-1-2.初始化模拟登陆用户依赖部门" +
                    "3.提交所有数据库执行操作" +
                    "4.模拟登陆")
    @BeforeClass
    public static void before() {
        DatabaseWrapper db = new DatabaseWrapper();
        try {
            //1.初始化依赖表数据
            //1-1.初始化 Data_source 数据
            hrds.commons.entity.Data_source dataSource = new Data_source();
            dataSource.setSource_id(SOURCE_ID);
            dataSource.setDatasource_number("init-hll");
            dataSource.setDatasource_name("测试数据源init-hll");
            dataSource.setSource_remark("测试数据源init-hll");
            dataSource.setCreate_date(DateUtil.getSysDate());
            dataSource.setCreate_time(DateUtil.getSysTime());
            dataSource.setCreate_user_id(USER_ID);
            dataSource.add(db);
            //1-2.初始化 Source_relation_dep 数据
            Source_relation_dep sourceRelationDep = new Source_relation_dep();
            sourceRelationDep.setDep_id(DEP_ID);
            sourceRelationDep.setSource_id(SOURCE_ID);
            sourceRelationDep.add(db);
            //1-3.初始化 Agent_info 数据
            Agent_info agentInfo = new Agent_info();
            agentInfo.setAgent_id(AGENT_ID + 1);
            agentInfo.setAgent_name("数据库Agent-init-hll");
            agentInfo.setAgent_type(AgentType.ShuJuKu.getCode());
            agentInfo.setAgent_ip("127.0.0.1");
            agentInfo.setAgent_port("8888");
            agentInfo.setAgent_status("1");
            agentInfo.setCreate_date(DateUtil.getSysDate());
            agentInfo.setCreate_time(DateUtil.getSysTime());
            agentInfo.setSource_id(SOURCE_ID);
            agentInfo.setUser_id(USER_ID);
            agentInfo.add(db);
            agentInfo.setAgent_id(AGENT_ID + 2);
            agentInfo.setAgent_name("DB文件Agent-init-hll");
            agentInfo.setAgent_type(AgentType.DBWenJian.getCode());
            agentInfo.setAgent_ip("127.0.0.1");
            agentInfo.setAgent_port("8888");
            agentInfo.setAgent_status("1");
            agentInfo.setCreate_date(DateUtil.getSysDate());
            agentInfo.setCreate_time(DateUtil.getSysTime());
            agentInfo.setSource_id(SOURCE_ID);
            agentInfo.setUser_id(USER_ID);
            agentInfo.add(db);
            //1-4.初始化 Database_set 数据
            Database_set databaseSet = new Database_set();
            databaseSet.setDatabase_id(DATABASE_ID + 1);
            databaseSet.setAgent_id(AGENT_ID + 1);
            databaseSet.setHost_name("数据库采集任务测试init-hll");
            databaseSet.setDatabase_number("数据库设置编号1");
            databaseSet.setSystem_type("00");
            databaseSet.setTask_name("数据库采集任务测试init-hll");
            databaseSet.setDatabase_name("postgres");
            databaseSet.setDatabase_pad("postgres");
            databaseSet.setDatabase_drive("数据库驱动");
            databaseSet.setDatabase_type("0");
            databaseSet.setUser_name("登录用户名");
            databaseSet.setDatabase_ip("127.0.0.1");
            databaseSet.setDatabase_port("31001");
            databaseSet.setDb_agent("0");
            databaseSet.setDatabase_separatorr("数据采用分隔符");
            databaseSet.setRow_separator("数据行分隔符");
            databaseSet.setPlane_url("DB文件源数据路径");
            databaseSet.setIs_sendok("1");
            databaseSet.setCp_or("清洗顺序");
            databaseSet.setJdbc_url("数据库连接地址");
            databaseSet.setClassify_id("9999");
            databaseSet.add(db);
            databaseSet.setDatabase_id(DATABASE_ID + 2);
            databaseSet.setAgent_id(AGENT_ID + 2);
            databaseSet.setHost_name("DB文件采集任务测试init-hll");
            databaseSet.setDatabase_number("数据库设置编号2");
            databaseSet.setSystem_type("01");
            databaseSet.setTask_name("DB文件采集任务测试init-hll");
            databaseSet.setDatabase_name("postgres");
            databaseSet.setDatabase_pad("postgres");
            databaseSet.setDatabase_drive("1");
            databaseSet.setDatabase_type("1");
            databaseSet.setUser_name("登录用户名");
            databaseSet.setDatabase_ip("127.0.0.1");
            databaseSet.setDatabase_port("31001");
            databaseSet.setDb_agent("1");
            databaseSet.setDatabase_separatorr("数据采用分隔符");
            databaseSet.setRow_separator("数据行分隔符");
            databaseSet.setPlane_url("DB文件源数据路径");
            databaseSet.setIs_sendok("1");
            databaseSet.setCp_or("清洗顺序");
            databaseSet.setJdbc_url("数据库连接地址");
            databaseSet.setClassify_id("9999");
            databaseSet.add(db);
            //2.模拟登陆
            //2-1.初始化模拟登陆数据
            //2-1-1.初始化模拟登陆用户
            Sys_user sysUser = new Sys_user();
            sysUser.setUser_id(USER_ID);
            sysUser.setCreate_id(1000L);
            sysUser.setDep_id(DEP_ID);
            sysUser.setRole_id(1001L);
            sysUser.setUser_name("模拟登陆测试用户init-hll");
            sysUser.setUser_password("111111");
            sysUser.setUseris_admin("0");
            sysUser.setUser_state("0");
            sysUser.setCreate_date(DateUtil.getSysDate());
            sysUser.setToken("0");
            sysUser.setValid_time(DateUtil.getSysTime());
            sysUser.add(db);
            //2-1-1.初始化模拟登陆用户依赖部门
            Department_info departmentInfo = new Department_info();
            departmentInfo.setDep_id(DEP_ID);
            departmentInfo.setDep_name("init-hll");
            departmentInfo.setCreate_date(DateUtil.getSysDate());
            departmentInfo.setCreate_time(DateUtil.getSysTime());
            departmentInfo.add(db);
            //3.提交所有数据库执行操作
            SqlOperator.commitTransaction(db);
            //4.根据初始化的 Sys_user 用户模拟登陆
            String bodyString = new HttpClient()
                    .addData("user_id", USER_ID)
                    .addData("password", "111111")
                    .post("http://127.0.0.1:8099/A/action/hrds/a/biz/login/login").getBodyString();
            ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() -> new BusinessException(
                    "获取返回的ActionResult信息失败!"));
            assertThat(ar.isSuccess(), is(true));
        } catch (Exception e) {
            db.rollback();
        } finally {
            db.close();
        }
    }

    @Method(desc = "测试案例执行完成后清理测试数据", logicStep = "清理测试数据")
    @AfterClass
    public static void after() {
        DatabaseWrapper db = new DatabaseWrapper();
        try {
            //1.清理测试数据
            //1-1.删除 Data_source 表测试数据
            SqlOperator.execute(db,
                    "delete from " + Data_source.TableName + " where source_id=?", SOURCE_ID);
            SqlOperator.commitTransaction(db);
            long dsDataNum = SqlOperator.queryNumber(db,
                    "select count(1) from " + Data_source.TableName + " where source_id =?",
                    SOURCE_ID
            ).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("data_source 表此条数据删除后,记录数应该为0", dsDataNum, is(0L));
            //1-2.删除 Source_relation_dep 表测试数据
            SqlOperator.execute(db,
                    "delete from " + Source_relation_dep.TableName + " where source_id=?", DEP_ID);
            SqlOperator.commitTransaction(db);
            long srdDataNum = SqlOperator.queryNumber(db,
                    "select count(1) from " + Source_relation_dep.TableName + " where" +
                            " source_id=?", DEP_ID
            ).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("source_relation_dep 表此条数据删除后,记录数应该为0", srdDataNum, is(0L));
            //1-3.删除 Agent_info 表测试数据
            SqlOperator.execute(db,
                    "delete from " + Agent_info.TableName + " where agent_id=?",
                    AGENT_ID + 1);
            SqlOperator.execute(db,
                    "delete from " + Agent_info.TableName + " where agent_id=?",
                    AGENT_ID + 2);
            SqlOperator.commitTransaction(db);
            long aiDataNum;
            aiDataNum = SqlOperator.queryNumber(db,
                    "select count(1) from " + Agent_info.TableName + " where agent_id=?",
                    AGENT_ID + 1
            ).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("agent_info 表此条数据删除后,记录数应该为0", aiDataNum, is(0L));
            aiDataNum = SqlOperator.queryNumber(db,
                    "select count(1) from " + Agent_info.TableName + " where agent_id=?",
                    AGENT_ID + 2
            ).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("agent_info 表此条数据删除后,记录数应该为0", aiDataNum, is(0L));
            //1-4.删除 Sys_user 表测试数据
            SqlOperator.execute(db,
                    "delete from " + Sys_user.TableName + " where user_id=?", USER_ID);
            SqlOperator.commitTransaction(db);
            long suNum = SqlOperator.queryNumber(db,
                    "select count(1) from " + Sys_user.TableName + " where user_id =?",
                    USER_ID
            ).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("sys_user 表此条数据删除后,记录数应该为0", suNum, is(0L));
            //1-5.删除 Department_info 表测试数据
            SqlOperator.execute(db,
                    "delete from " + Department_info.TableName + " where dep_id=?", DEP_ID);
            SqlOperator.commitTransaction(db);
            long depNum = SqlOperator.queryNumber(db,
                    "select count(1) from " + Department_info.TableName + " where dep_id =?",
                    DEP_ID
            ).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("department_info 表此条数据删除后,记录数应该为0", depNum, is(0L));
            //1-6.删除 Database_set 表测试数据
            SqlOperator.execute(db,
                    "delete from " + Database_set.TableName + " where classify_id=?", 9999);
            SqlOperator.commitTransaction(db);
            long dsNum = SqlOperator.queryNumber(db,
                    "select count(1) from " + Database_set.TableName + " where classify_id =?", 9999
            ).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("Database_set 表此条数据删除后,记录数应该为0", dsNum, is(0L));
        } catch (Exception e) {
            db.rollback();
        } finally {
            db.close();
        }
    }

    @Test
    public void queryDataBasedOnTableName() {
        //查询数据存储层数据
    }

    @Test
    public void queryDataBasedOnSql() {
        //查询数据存储层数据
    }
}
