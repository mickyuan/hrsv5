package hrds.b.biz.collectmonitor;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "", author = "Mr.Lee", createdate = "2019-12-30 14:38")
public class CollectMonitorActionTest extends WebBaseTestCase {

  /** user_id : 用户ID */
  private final long user_id = -1234;
  /** password : 用户登陆密码 */
  private final String password = "1";
  /** dep_id : 部门ID */
  private final long dep_id = -111111;
  /** source_id : 数据源ID */
  private final long source_id = -1234;
  /** database_id : 采集任务ID */
  private final Long[] database_id = {-1234L, -12345L};
  /** classify_id : 分类Id */
  private final long classify_id = -1234;
  /** agent_id : 数据库AgentID */
  private final long database_agent_id = -1234;
  /** agent_id : DB文件采集AgentID */
  private final long db_agent_id = -12345;
  /** table_name : 采集表名称,后面生成的表名称前面都是此名称 */
  private final String table_name = "lqcs_table";
  /** agent_name : agent名称 */
  private final String[] agent_name = {"database_lqcs", "db_lqcs"};
  /** agent_type : agent类型 */
  private final String[] agent_type = {AgentType.ShuJuKu.getCode(), AgentType.DBWenJian.getCode()};
  /** agent_port : agent端口 */
  private final String[] agent_port = {"7788", "8899"};
  /** job_type : 采集作业的步骤类型(数据库) */
  private final String[] database_job_type = {"B001", "B002", "UHDFS", "LHIVE", "SOURCE"};
  /** job_type : 采集作业的步骤类型(DB数据文件) */
  private final String[] db_job_type = {"B001", "B101", "UHDFS", "LHIVE", "SOURCE"};
  /** dataBaseSize : 采集数据库文件的大小 */
  private final long dataBaseSize = 1234;
  /** fileSize : 采集文件大小 */
  private final long fileSize = 5678;

  @Method(
      desc = "模拟测试数据",
      logicStep =
          "-- , 初始化测试数据 :"
              + "1 : 创建 department_info 的数据信息"
              + "2 : 创建 sys_user 的数据信息"
              + "3 : 创建 Agent_info 的数据信息"
              + "4 : 创建 Database_set 的数据信息,并将database_set表的agent_id与Agent_info的agent_id关联"
              + "5 : 创建 source_file_attribute 数据,并建立外键关联字段 agent_id,source_id,collect_set_id"
              + "6 : 创建 collect_case 数据,并建立外键关联字段 agent_id,source_id,collect_set_id"
              + "7 : 提交此次数据库操作的事物"
              + "8 :模拟用户的登陆"
              + "-- , 测试数据"
              + "1 : 检查 agent_info,用户的数据是否存在,如果存在,是否符合初始化得数据条数, 数据源信息 1条, Agent 2条"
              + "2 : 检查 database_set, agent_id是否符合初始数据时的条数, 数据库agent采集任务 1条, DB数据文件采集任务 1条"
              + "3 : 检查 source_file_attribute,是否符合初始数据的条数,数据库agent采集任务采集表 1 条,设置文件大小为1234 bytes,"
              + "    DB数据文件采集表 1 条,设置文件大小为 5678 bytes, 统计的时候会把数据库文件采集的大小和DB文件采集的大小相加,所以这里也会相加,"
              + "    文件采集的数据并未初始化,这里默认是 0 "
              + "4 : 检查 collect_case,是否符合初始数据的条数,数据库务采集表(1张表)的步骤数 5条,DB采集表(1张表)的步骤是5条"
              + "-- , 清理测试数据,并验证否被清理掉"
              + "1 : 删除 department_info 的数据信息,并验证删除的条数是否符合...并使用初始化部门(dep_id)进行条件删除"
              + "2 : 删除 sys_user 的数据信息,并验证删除的条数是否符合...并使用初始化用户(user_id)进行条件删除"
              + "3 : 删除 Agent_info 表数据,并验证删除的条数是否符合...并使用初始化用户(user_id)进行条件删除"
              + "4 ; 删除 Database_set 表数据,并验证删除的条数是否符合...并使用 database_id进行条件删除"
              + "5 : 删除 source_file_attribute 表数据,并验证删除的条数是否符合...并经行条件(source_id ,database_id)删除测试数据"
              + "6 : 删除 collect_case 表数据,并验证删除的条数是否符合...并经行条件(source_id ,database_id)删除测试数据"
              + "7 : 如果和预期结果一致, 提交此次事物")
  @Before
  public void before() {

    try (DatabaseWrapper db = new DatabaseWrapper()) {
      /* 1 : 创建 department_info 的数据信息 */
      Department_info department_info = new Department_info();
      department_info.setDep_id(dep_id);
      department_info.setDep_name("Lee-init-部门");
      department_info.setCreate_date(DateUtil.getSysDate());
      department_info.setCreate_time(DateUtil.getSysTime());
      department_info.add(db);

      /* 2 : 创建 sys_user 的数据信息 */
      Sys_user user = new Sys_user();
      user.setUser_id(user_id);
      user.setCreate_id("-11111");
      user.setDep_id(dep_id);
      user.setRole_id("1001");
      user.setUser_name("lqcs_init");
      user.setUser_password(password);
      user.setUseris_admin(IsFlag.Shi.getCode());
      user.setUser_state(UserState.ZhengChang.getCode());
      user.setCreate_date(DateUtil.getSysDate());
      user.setToken("0");
      user.setValid_time("100000");
      user.add(db);

      /* 1 : 创建 Agent_info 的数据信息 */
      Agent_info agent_info = new Agent_info();
      for (int i = 0; i < agent_type.length; i++) {
        if (i == 0) {

          agent_info.setAgent_id(database_agent_id);
        } else {

          agent_info.setAgent_id(db_agent_id);
        }
        agent_info.setAgent_name(agent_name[i]);
        agent_info.setAgent_ip("127.0.0.1");
        agent_info.setAgent_port(agent_port[i]);
        agent_info.setAgent_type(agent_type[i]);
        agent_info.setAgent_status(AgentStatus.YiLianJie.getCode());
        agent_info.setUser_id(user_id);
        agent_info.setCreate_date(DateUtil.getSysDate());
        agent_info.setCreate_time(DateUtil.getSysTime());
        agent_info.setSource_id(source_id);
        agent_info.add(db);
      }

      /* 2 : 创建 Database_set 的数据信息,并将database_set表的agent_id与Agent_info的agent_id关联 */
      Database_set database_set = new Database_set();
      for (int i = 0; i < database_id.length; i++) {
        database_set.setDatabase_id(database_id[i]);
        database_set.setTask_name("lqcs_cscs_" + i);
        if (i == 0) {

          database_set.setAgent_id(database_agent_id);
          database_set.setDb_agent(IsFlag.Fou.getCode());
        } else {

          database_set.setAgent_id(db_agent_id);
          database_set.setDb_agent(IsFlag.Shi.getCode());
        }
        database_set.setDatabase_number("test_lqcs" + i);
        database_set.setDatabase_type(DatabaseType.Postgresql.getCode());
//        database_set.setIs_load(IsFlag.Fou.getCode());
//        database_set.setIs_hidden(IsFlag.Fou.getCode());
        database_set.setIs_sendok(IsFlag.Shi.getCode());
//        database_set.setIs_header(IsFlag.Fou.getCode());
        database_set.setClassify_id(classify_id);
        database_set.add(db);
      }

      /* 3 : 创建 source_file_attribute 数据,并建立外键关联字段 agent_id,source_id,collect_set_id */
      Source_file_attribute attribute = new Source_file_attribute();
      for (int i = 0; i < database_id.length; i++) {
        attribute.setFile_id(UUID.randomUUID().toString());
        attribute.setIs_in_hbase(IsFlag.Shi.getCode());
        attribute.setSeqencing(String.valueOf(i));
        if (i == 0) {
          attribute.setCollect_type(AgentType.ShuJuKu.getCode());
          attribute.setAgent_id(database_agent_id);
          // 数据库采集的文件大小
          attribute.setFile_size(dataBaseSize);
        } else {

          attribute.setCollect_type(AgentType.DBWenJian.getCode());
          attribute.setAgent_id(db_agent_id);
          // 数据文件采集的文件大小
          attribute.setFile_size(fileSize);
        }

        attribute.setOriginal_name("测试表名" + i);
        attribute.setOriginal_update_date(DateUtil.getSysDate());
        attribute.setOriginal_update_time(DateUtil.getSysTime());
        attribute.setTable_name("lqcs_" + i);
        attribute.setHbase_name("test_lqcs_" + i);
        attribute.setStorage_date(DateUtil.getSysDate());
        attribute.setStorage_time(DateUtil.getSysTime());

        attribute.setFile_type("csv");
        attribute.setFile_suffix("csv");
        attribute.setFolder_id(4444L);
        attribute.setSource_id(source_id);
        attribute.setCollect_set_id(database_id[i]);
        attribute.add(db);
      }

      /* 4 : 创建 collect_case 数据,并建立外键关联字段 agent_id,source_id,collect_set_id */
      Collect_case collect_case = new Collect_case();
      // 任务ID
      for (int j = 0; j < database_id.length; j++) {
        collect_case.setTask_classify("lqcs-cscs_" + j);
        for (int i = 0; i < db_job_type.length; i++) {
          collect_case.setJob_rs_id(UUID.randomUUID().toString());
          if (j == 0) {

            collect_case.setCollect_type(AgentType.ShuJuKu.getCode());
            collect_case.setAgent_id(database_agent_id);
            collect_case.setJob_type(database_job_type[i]);
          } else {

            collect_case.setCollect_type(AgentType.DBWenJian.getCode());
            collect_case.setAgent_id(db_agent_id);
            collect_case.setJob_type(db_job_type[i]);
          }

          collect_case.setColect_record(21371L);
          collect_case.setCollect_s_date(DateUtil.getSysDate());
          collect_case.setCollect_s_time(DateUtil.getSysTime());
          collect_case.setCollect_e_date(DateUtil.getSysDate());
          collect_case.setCollect_e_time(DateUtil.getSysTime());
          collect_case.setExecute_state(ExecuteState.YunXingWanCheng.getCode());
          collect_case.setIs_again(IsFlag.Fou.getCode());
          collect_case.setJob_group("-121121");
          collect_case.setSource_id(source_id);
          collect_case.setCollect_set_id(database_id[j]);
          collect_case.add(db);
        }
      }

      /* 5 : 提交此次数据库操作的事物 */
      db.commit();

      /* 6 :模拟用户的登陆 */
      String bodyString =
          new HttpClient()
              .buildSession()
              .addData("user_id", user_id)
              .addData("password", password)
              .post("http://127.0.0.1:8099/A/action/hrds/a/biz/login/login")
              .getBodyString();
      ActionResult ar =
          JsonUtil.toObjectSafety(bodyString, ActionResult.class)
              .orElseThrow(() -> new BusinessException("请求登陆失败"));
      assertThat(ar.isSuccess(), is(true));
    }
  }

  @Method(
      desc = " 清理测试数据,并验证否被清理掉",
      logicStep =
          " 1 : 删除 Agent_info 表数据,并验证删除的条数是否符合...并使用初始化用户(user_id)进行条件删除"
              + " 2 ; 删除 Database_set 表数据,并验证删除的条数是否符合...并使用 database_id进行条件删除"
              + " 3 : 删除 source_file_attribute 表数据,并验证删除的条数是否符合...并经行条件(source_id ,database_id)删除测试数据"
              + " 4 : 删除 collect_case 表数据,并验证删除的条数是否符合...并经行条件(source_id ,database_id)删除测试数据"
              + " 5 : 如果和预期结果一致, 提交此次事物")
  @After
  public void deleteData() {

    try (DatabaseWrapper db = new DatabaseWrapper()) {
      /* 1 : 删除 department_info 的数据信息,并验证删除的条数是否符合...并使用初始化部门(dep_id)进行条件删除 */
      assertThat(
          "删除测试数据表 department_info",
          SqlOperator.execute(
              db, "DELETE FROM " + Department_info.TableName + " WHERE dep_id = ?", dep_id),
          is(1));

      /* 2 : 删除 sys_user 的数据信息,并验证删除的条数是否符合...并使用初始化用户(user_id)进行条件删除 */
      assertThat(
          "删除测试数据表 Sys_user",
              SqlOperator.execute(
              db, "DELETE FROM " + Sys_user.TableName + " WHERE user_id = ?", user_id),
          is(1));

      /*3 : 删除 Agent_info 表数据,并验证删除的条数是否符合...并使用初始化用户(user_id)进行条件删除*/
      int deleteAgentNUm =
          SqlOperator.execute(
              db, "DELETE FROM " + Agent_info.TableName + " WHERE user_id = ?", user_id);
      assertThat("删除测试数据表 Agent_info", deleteAgentNUm, is(agent_type.length));

      /*4 ; 删除 Database_set 表数据,并验证删除的条数是否符合...并使用 database_id进行条件删除*/
      SqlOperator.Assembler dbo =
          SqlOperator.Assembler.newInstance()
              .addSql("DELETE FROM " + Database_set.TableName + " WHERE")
              .addORParam(" database_id", database_id, "");

      assertThat(
          "删除测试数据表 Database_set",
          SqlOperator.execute(db, dbo.sql(), dbo.params()),
          is(database_id.length));

      /*5 : 删除 source_file_attribute 表数据,并验证删除的条数是否符合...并经行条件(source_id ,database_id)删除测试数据*/
      dbo.clean();
      dbo.addSql("DELETE FROM " + Source_file_attribute.TableName + " WHERE source_id = ? ")
          .addParam(source_id)
          .addORParam("collect_set_id", database_id);

      assertThat(
          "删除测试数据表 Database_set",
          SqlOperator.execute(db, dbo.sql(), dbo.params()),
          is(database_id.length));

      /*6 : 删除 collect_case 表数据,并验证删除的条数是否符合...并经行条件(source_id ,database_id)删除测试数据*/
      dbo.clean();
      dbo.addSql("DELETE FROM " + Collect_case.TableName + " WHERE source_id = ? ")
          .addParam(source_id)
          .addORParam("collect_set_id", database_id);

      assertThat(
          "删除测试数据表 Database_set",
          SqlOperator.execute(db, dbo.sql(), dbo.params()),
          is(database_id.length * database_job_type.length));

      /*7 : 如果和预期结果一致, 提交此次事物*/
      db.commit();
    }
  }

  @Method(desc = "获取Agent,数据源数量", logicStep = "查询Agent,数据源配置数量(数据源存在着Agent的时候才会计数)")
  @Test
  public void getAgentNumAndSourceNum() {

    ActionResult ar =
        JsonUtil.toObjectSafety(
                new HttpClient().post(getActionUrl("getAgentNumAndSourceNum")).getBodyString(),
                ActionResult.class)
            .orElseThrow(() -> new BusinessException("连接失败"));
    assertThat(ar.isSuccess(), is(true));
    Map<Object, Object> dataForMap = ar.getDataForMap();

    /* 1 : 检查 agent_info,用户的数据是否存在,如果存在,是否符合初始化得数据条数, 数据源信息 1条, Agent 2条 */
    assertThat(dataForMap.get("agentnum"), is(2));
    assertThat(dataForMap.get("sourcenum"), is(1));
  }

  @Method(desc = "获取当前用户采集的任务信息", logicStep = "获取当前用户采集的任务信息")
  @Test
  public void getDatabaseSet() {

    ActionResult ar =
        JsonUtil.toObjectSafety(
                new HttpClient().post(getActionUrl("getDatabaseSet")).getBodyString(),
                ActionResult.class)
            .orElseThrow(() -> new BusinessException("连接失败"));
    List<Map> dataForEntityList = ar.getDataForEntityList(Map.class);
    /* 检查 database_set, agent_id是否符合初始数据时的条数, 数据库agent采集任务 1条, DB数据文件采集任务 1条 */
    assertThat(dataForEntityList.size(), is(2));
  }

  @Test
  public void getDataCollectInfo() {

    ActionResult ar =
        JsonUtil.toObjectSafety(
                new HttpClient().post(getActionUrl("getDataCollectInfo")).getBodyString(),
                ActionResult.class)
            .orElseThrow(() -> new BusinessException("连接失败"));
    Result dataForResult = ar.getDataForResult();
    /* 3 : 检查 source_file_attribute,是否符合初始数据的条数,数据库agent采集任务采集表 1 条,设置文件大小为1234 bytes,
    DB数据文件采集表 1 条,设置文件大小为 5678 bytes, 统计的时候会把数据库文件采集的大小和DB文件采集的大小相加,所以这里也会相加,
     文件采集的数据并未初始化,这里默认是 0 */
    assertThat(dataForResult.getLongDefaultZero(0, "dbcollectsize"), is(dataBaseSize + fileSize));
    assertThat(dataForResult.getLongDefaultZero(0, "filecollectsize"), is(0L));
    assertThat(dataForResult.getIntDefaultZero(0, "taskNum"), is(2));
  }

  @Test
  public void getCurrentTaskJob() {

    ActionResult ar =
        JsonUtil.toObjectSafety(
                new HttpClient()
                    .addData("database_id", database_id[0])
                    .post(getActionUrl("getCurrentTaskJob"))
                    .getBodyString(),
                ActionResult.class)
            .orElseThrow(() -> new BusinessException("连接失败"));

    List<Map> dataForEntityList = ar.getDataForEntityList(Map.class);
    /* 4 : 检查 collect_case,是否符合初始数据的条数,数据库务采集表(1张表)的步骤数 5条,DB采集表(1张表)的步骤是5条,这里只看了一种所以是5 */
    assertThat(dataForEntityList.size(), is(5));
  }


}
