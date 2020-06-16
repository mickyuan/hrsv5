package hrds.a.biz.login;

import fd.ng.core.conf.AppinfoConf;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.test.junit.TestCaseLog;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LoginActionTest extends WebBaseTestCase {

  private final long testUser = 10L;
  private final String testUserPwd = "1";

  /**
   * 方法描述: 测试前插入需要的数据
   *
   * <p>1 : 插入用户信息数据
   *
   * <p>2 : 插入部门信息数据
   *
   * <p>3 : 提交此次数据
   *
   * <p>@author: Mr.Lee
   *
   * <p>创建时间: 2019-09-12
   */
  @Before
  public void before() {
    //	  1 : 插入用户信息数据
    try (DatabaseWrapper db = new DatabaseWrapper()) {
      int num =
          SqlOperator.execute(
              db,
              "insert into "
                  + Sys_user.TableName
                  + " (user_id, create_id,role_id,user_name,user_password,"
                  + "useris_admin,user_state,create_date,token,valid_time,dep_id) "
                  + "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
              testUser,
              testUser,
              testUser,
              "测试用户(-1000)",
              testUserPwd,
              IsFlag.Shi.getCode(),
              IsFlag.Shi.getCode(),
              DateUtil.getSysDate(),
              "0",
              "0",
              testUser);
      assertThat("用户测试数据初始化失败", num, is(1));

      //	    2 : 插入部门信息数据
      int depNum =
          SqlOperator.execute(
              db,
              "insert into "
                  + Department_info.TableName
                  + " (dep_id, dep_name,create_date,create_time) values"
                  + "(?, ?, ?, ?)",
              testUser,
              "测试部门(-1000)",
              DateUtil.getSysDate(),
              DateUtil.getSysTime());
      assertThat("部门测试数据初始化失败", depNum, is(1));

      //	    3 : 提交此次数据
      SqlOperator.commitTransaction(db);
    }
  }

  /**
   * 方法描述: 测试完成后需要自动测试删除数据
   *
   * <p>1 : 先删除测试的用户
   *
   * <p>2 : 检查测试的用户数据是否被删除掉
   *
   * <p>3 : 检查测试的部门数据是否被删除掉
   *
   * <p>@author: Mr.Lee
   *
   * <p>创建时间: 2019-09-10
   *
   * <p>return:
   *
   * @param
   */
  @After
  public void after() {

    TestCaseLog.println("开始清理初始化的测试数据 ... ...");
    try (DatabaseWrapper db = new DatabaseWrapper()) {

      // 1 : 先删除测试的用户
      SqlOperator.execute(db, "delete from " + Sys_user.TableName + " WHERE user_id = ?", testUser);
      SqlOperator.execute(
          db, "delete from " + Department_info.TableName + " WHERE dep_id = ?", testUser);
      SqlOperator.commitTransaction(db);

      // 2 : 检查测试的用户是否被删除掉
      long userNum =
          SqlOperator.queryNumber(
                  db, "select count(1) from " + Sys_user.TableName + " WHERE user_id = ?", testUser)
              .orElseThrow(() -> new RuntimeException("初始测试数据未删除掉,请检查"));
      assertThat("用户测试数据已被删除", userNum, is(0L));

      // 3 : 检查测试的部门数据是否被删除掉
      long depNum =
          SqlOperator.queryNumber(
                  db,
                  "select count(1) from " + Department_info.TableName + " WHERE dep_id = ?",
                  testUser)
              .orElseThrow(() -> new RuntimeException("初始测试数据未删除掉,请检查"));
      assertThat("部门测试数据已被删除", depNum, is(0L));
    }
  }

  /**
   * 方法描述: 这里测试模拟登陆的情况
   *
   * <p>1 : 模拟用户名为空的情况
   *
   * <p>2 : 模拟密码为空的情况
   *
   * <p>3 : 模拟用户名错误的情况
   *
   * <p>4 : 模拟密码错误的情况
   *
   * <p>5 : 模拟部门数据信息不存在
   *
   * <p>5-1 : 先将部门信息清除,进行部门的信息获取..此时会抛出部门信息不存在情况
   *
   * <p>5-2 : 再将部门信息还原
   *
   * <p>5-3 : 发送正确的数据请求
   *
   * <p>6 : 检查Cookie是否和登陆用户匹配
   *
   * <p>@author: Mr.Lee
   *
   * <p>创建时间: 2019-09-10
   */
  @Test
  public void login() {

    // 1 : 模拟用户名为空的情况
    String userEmptyMsg =
        new HttpClient()
            .addData("user_id", "")
            .addData("password", "11111")
            .post(getActionUrl("login"))
            .getBodyString();
    ActionResult userEmpty = JsonUtil.toObject(userEmptyMsg, ActionResult.class);
    assertThat(userEmpty.getMessage(), is("用户名不能为空"));

    //		//2 : 模拟密码为空的情况
    String pwdErrorMsg =
        new HttpClient()
            .addData("user_id", "-1000")
            .addData("password", "")
            .post(getActionUrl("login"))
            .getBodyString();
    ActionResult pwdError = JsonUtil.toObject(pwdErrorMsg, ActionResult.class);
    assertThat(pwdError.getMessage(), is("密码不能为空"));

    //		* <p>3 : 模拟用户名错误的情况</p>
    String userErrorMsg =
        new HttpClient()
            .addData("user_id", "-1")
            .addData("password", "111")
            .post(getActionUrl("login"))
            .getBodyString();
    ActionResult userError = JsonUtil.toObject(userErrorMsg, ActionResult.class);
    assertThat(userError.getMessage(), is("用户不存在"));

    //	    * <p>4 : 模拟密码错误的情况</p>
    String userPwdErrorMsg =
        new HttpClient()
            .addData("user_id", testUser)
            .addData("password", "111")
            .post(getActionUrl("login"))
            .getBodyString();
    ActionResult userPwdError = JsonUtil.toObject(userPwdErrorMsg, ActionResult.class);
    assertThat(userPwdError.getMessage(), is("密码错误"));

    //	    * <p>5 : 模拟部门数据信息不存在</p>
    try (DatabaseWrapper db = new DatabaseWrapper()) {

      //		5-1 : 先将部门信息清除,进行部门的信息获取..此时会抛出部门信息不存在情况
      SqlOperator.execute(
          db, "delete from " + Department_info.TableName + " WHERE dep_id = ?", testUser);
      SqlOperator.commitTransaction(db);
      // 检查测试的部门数据是否被删除掉
      long depNum =
          SqlOperator.queryNumber(
                  db,
                  "select count(1) from " + Department_info.TableName + " WHERE dep_id = ?",
                  testUser)
              .orElseThrow(() -> new RuntimeException("模拟测试部门信息不存在的数据未删除掉,请检查"));
      // 预言是否和预料的结果一致
      assertThat(depNum, is(0L));

      // 开始使用HttpClient的方式进行数据的请求
      String deptErrorMsg =
          new HttpClient()
              .addData("user_id", testUser)
              .addData("password", testUserPwd)
              .post(getActionUrl("login"))
              .getBodyString();
      ActionResult deptError = JsonUtil.toObject(deptErrorMsg, ActionResult.class);
      assertThat(deptError.getMessage(), is("部门信息不存在"));

      //		5-2 : 再将部门信息还原
      int addDdepNum =
          SqlOperator.execute(
              db,
              "insert into "
                  + Department_info.TableName
                  + "(dep_id, dep_name,create_date,create_time) values"
                  + "(?, ?, ?, ?)",
              testUser,
              "测试部门(-1000)",
              DateUtil.getSysDate(),
              DateUtil.getSysTime());
      assertThat(addDdepNum, is(1));
      SqlOperator.commitTransaction(db);

      // 5-3 : 发送正确的数据请求
      String successRequestMsg =
          new HttpClient()
              .addData("user_id", testUser)
              .addData("password", testUserPwd)
              .post(getActionUrl("login"))
              .getBodyString();
      ActionResult successMsg = JsonUtil.toObject(successRequestMsg, ActionResult.class);
      assertThat(successMsg.getMessage(), is("OK"));
    }

    //    	    * <p>6 : 检查Cookie是否和登陆用户匹配</p>
    String responseValue =
        new HttpClient()
            .buildSession()
            .addData("user_id", testUser)
            .addData("password", testUserPwd)
            .post("http://127.0.0.1:8888/A/action/hrds/a/biz/login/login")
            .getBodyString();
    ActionResult ar =
        JsonUtil.toObjectSafety(responseValue, ActionResult.class)
            .orElseThrow(() -> new BusinessException("连接失败"));
    assertThat(ar.isSuccess(), is(true));
  }
}
