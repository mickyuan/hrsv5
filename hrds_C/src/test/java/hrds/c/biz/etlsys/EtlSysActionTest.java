package hrds.c.biz.etlsys;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "作业调度工程测试", author = "dhw", createdate = "2019/11/25 17:22")
public class EtlSysActionTest extends WebBaseTestCase {
	// 初始化登录用户ID
	private static final long UserId = 6666L;
	// 初始化创建用户ID
	private static final long CreateId = 1000L;
	// 测试部门ID dep_id,测试作业调度部门
	private static final long DepId = 1000011L;
	// 初始化工程编号
	private static final String EtlSysCd = "zypzglcs_dhw";
	// 初始化任务编号
	private static final String SubSysCd = "zypzglrwcs_dhw";

	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.构造sys_user表测试数据
			Sys_user sysUser = new Sys_user();
			sysUser.setUser_id(UserId);
			sysUser.setCreate_id(CreateId);
			sysUser.setDep_id(DepId);
			sysUser.setCreate_date(DateUtil.getSysDate());
			sysUser.setCreate_time(DateUtil.getSysTime());
			sysUser.setRole_id("1001");
			sysUser.setUser_name("作业配置功能测试");
			sysUser.setUser_password("1");
			sysUser.setUser_type(UserType.CaiJiYongHu.getCode());
			sysUser.setUseris_admin("1");
			sysUser.setUsertype_group("02,03,04,08");
			sysUser.setUser_state(IsFlag.Shi.getCode());
			int num = sysUser.add(db);
			assertThat("测试数据sys_user数据初始化", num, is(1));
			// 2.构造department_info部门表测试数据
			Department_info department_info = new Department_info();
			department_info.setDep_id(DepId);
			department_info.setDep_name("测试作业调度部门");
			department_info.setCreate_date(DateUtil.getSysDate());
			department_info.setCreate_time(DateUtil.getSysTime());
			department_info.setDep_remark("测试");
			num = department_info.add(db);
			assertThat("测试数据department_info初始化", num, is(1));
			// 3.构造etl_sys表测试数据
			Etl_sys etl_sys = new Etl_sys();
			for (int i = 0; i < 4; i++) {
				etl_sys.setEtl_sys_cd(EtlSysCd + i);
				etl_sys.setEtl_sys_name("gccs" + i);
				etl_sys.setComments("工程测试" + i);
				etl_sys.setEtl_sys_name("dhwcs" + i);
				etl_sys.setCurr_bath_date(DateUtil.getSysDate());
				etl_sys.setSys_run_status(Job_Status.STOP.getCode());
				etl_sys.setComments("作业调度工程测试" + i);
				etl_sys.setEtl_serv_ip("10.71.4.51");
				etl_sys.setEtl_serv_port(Constant.SFTP_PORT);
				etl_sys.setUser_name("hyshf");
				etl_sys.setUser_pwd("hyshf");
				etl_sys.setServ_file_path("/home/hyshf/etl/");
				etl_sys.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				etl_sys.setRemarks("10.71.4.52:56379");
				etl_sys.setBath_shift_time(DateUtil.getSysDate());
				etl_sys.setUser_id(UserId);
				num = etl_sys.add(db);
				assertThat("测试数据etl_sys初始化", num, is(1));
			}
			Etl_sub_sys_list etl_sub_sys_list = new Etl_sub_sys_list();
			etl_sub_sys_list.setEtl_sys_cd(EtlSysCd + 1);
			etl_sub_sys_list.setSub_sys_cd(SubSysCd);
			etl_sub_sys_list.setSub_sys_desc("任务1");
			etl_sub_sys_list.setComments("作业调度删除工程测试");
			num = etl_sub_sys_list.add(db);
			assertThat("测试数据etl_sub_sys_list初始化", num, is(1));
			String[] paraType = {Pro_Type.Thrift.getCode(), Pro_Type.Yarn.getCode()};
			for (String para_type : paraType) {
				Etl_resource resource = new Etl_resource();
				resource.setEtl_sys_cd(etl_sys.getEtl_sys_cd());
				resource.setResource_type(para_type);
				resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
				resource.setResource_max(10);
				resource.setResource_used(0);
				resource.add(db);
				assertThat("测试数据etl_sub_sys_list初始化", num, is(1));
			}
			// 12.提交事务
			SqlOperator.commitTransaction(db);
		}
		// 13.模拟用户登录
		String responseValue = new HttpClient()
				.buildSession()
				.addData("user_id", UserId)
				.addData("password", "1")
				.post("http://127.0.0.1:8888/A/action/hrds/a/biz/login/login")
				.getBodyString();
		Optional<ActionResult> ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class);
		assertThat("用户登录", ar.get().isSuccess(), is(true));
	}

	@After
	public void after() {
		DatabaseWrapper db = new DatabaseWrapper();
		try {
			// 1.测试完成后删除sys_user表测试数据
			SqlOperator.execute(db, "delete from " + Sys_user.TableName + " where user_id=?", UserId);
			// 2.测试完成后删除etl_sys表测试数据
			for (int i = 0; i < 4; i++) {
				SqlOperator.execute(db, "delete from " + Etl_sys.TableName + " where etl_sys_cd=?",
						EtlSysCd + i);
			}
			SqlOperator.execute(db, "delete from " + Etl_sys.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			// 3.测试完删除department_info表测试数据
			SqlOperator.execute(db, "delete from " + Department_info.TableName + " where dep_id=?",
					DepId);
			// 4.删除任务数据
			SqlOperator.execute(db, "delete from " + Etl_sub_sys_list.TableName + " where sub_sys_cd=?",
					SubSysCd);
			// 5.删除资源定义数据
			SqlOperator.execute(db, "delete from " + Etl_resource.TableName + " where etl_sys_cd=?",
					EtlSysCd);
			SqlOperator.execute(db, "delete from " + Etl_resource.TableName + " where resource_type in(?,?)",
					Pro_Type.Thrift.getCode(), Pro_Type.Yarn.getCode());
			// 测试完删除新增数据
			SqlOperator.execute(db, "delete from " + Etl_sys.TableName + " where etl_sys_cd=?",
					"addCs1");
			// 4.提交事务
			SqlOperator.commitTransaction(db);
		} catch (Exception e) {
			db.rollback();
		} finally {
			db.close();
		}
	}

	@Method(desc = "测试查询所有作业调度工程信息", logicStep = "1.正常的数据访问1，数据都正常，该方法只有一种情况")
	@Test
	public void searchEtlSys() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.post(getActionUrl("searchEtlSys"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证数据正确性
		Result etlSys = ar.getDataForResult();
		for (int i = 0; i < etlSys.getRowCount(); i++) {
			assertThat(etlSys.getString(i, "etl_sys_cd"), is(EtlSysCd + i));
			assertThat(etlSys.getString(i, "etl_sys_name"), is("dhwcs" + i));
			assertThat(etlSys.getString(i, "curr_bath_date"), is(DateUtil.getSysDate()));
			assertThat(etlSys.getString(i, "sys_run_status"), is(Job_Status.STOP.getCode()));
			assertThat(etlSys.getString(i, "comments"), is("作业调度工程测试" + i));
		}
	}

	@Method(desc = "根据工程编号查询工程信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd不存在,该方法只有两种情况")
	@Test
	public void searchEtlSysById() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd + 0)
				.post(getActionUrl("searchEtlSysById"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("son对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> map = ar.getDataForMap();
		assertThat(map.get("etl_sys_cd"), is(EtlSysCd + 0));
		assertThat(map.get("etl_sys_name"), is("dhwcs0"));
		assertThat(map.get("comments"), is("作业调度工程测试0"));
		assertThat(map.get("user_pwd"), is("hyshf"));
		assertThat(map.get("user_pwd"), is("hyshf"));
		assertThat(map.get("serv_file_path"), is("/home/hyshf/etl/"));
		assertThat(map.get("etl_serv_ip"), is("10.71.4.51"));
		// 2.错误的数据访问1，etl_sys_cd不存在
		bodyString = new HttpClient().addData("etl_sys_cd", "000")
				.post(getActionUrl("searchEtlSysById"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "测试新增作业调度工程",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，etl_sys_cd为空" +
					"3.错误的数据访问2，etl_sys_cd为空格" +
					"4.错误的数据访问3，etl_sys_cd为不合法（不为数字字母下划线）" +
					"5.错误的数据访问4，etl_sys_name为空" +
					"6.错误的数据访问5，etl_sys_name为空格")
	@Test
	public void addEtlSys() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", "addCs1")
				.addData("etl_sys_name", "工程测试1")
				.addData("comments", "新增作业调度工程测试1")
				.post(getActionUrl("addEtlSys"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证数据正确性
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Etl_sys etl_sys = SqlOperator.queryOneObject(db, Etl_sys.class, "select * from "
					+ Etl_sys.TableName + " where etl_sys_cd=?", "addCs1").orElseThrow(() ->
					new BusinessException("sql查询错误或者映射实体错误！"));
			assertThat(etl_sys.getSys_run_status(), is(Job_Status.STOP.getCode()));
			assertThat(etl_sys.getBath_shift_time(), is(DateUtil.getSysDate()));
			assertThat(etl_sys.getEtl_sys_name(), is("工程测试1"));
			assertThat(etl_sys.getComments(), is("新增作业调度工程测试1"));
			assertThat(etl_sys.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
			assertThat(etl_sys.getUser_id(), is(UserId));
		}
		// 2.错误的数据访问1，etl_sys_cd为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "")
				.addData("etl_sys_name", "工程测试1")
				.addData("comments", "新增作业调度工程测试1")
				.post(getActionUrl("addEtlSys"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，etl_sys_cd为空格
		bodyString = new HttpClient()
				.addData("etl_sys_cd", " ")
				.addData("etl_sys_name", "工程测试1")
				.addData("comments", "新增作业调度工程测试1")
				.post(getActionUrl("addEtlSys"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，etl_sys_cd为不合法（不为数字字母下划线）
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "etl-sys-cd")
				.addData("etl_sys_name", "工程测试1")
				.addData("comments", "新增作业调度工程测试1")
				.post(getActionUrl("addEtlSys"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问4，etl_sys_name为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "addCs2")
				.addData("etl_sys_name", "")
				.addData("comments", "新增作业调度工程测试1")
				.post(getActionUrl("addEtlSys"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 6.错误的数据访问5，etl_sys_name为空格
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "addCs2")
				.addData("etl_sys_name", " ")
				.addData("comments", "新增作业调度工程测试1")
				.post(getActionUrl("addEtlSys"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "更新工程信息", logicStep = "1.正常的数据访问1，数据都正常" +
			"2.错误的数据访问1，etl_sys_cd为空" +
			"3.错误的数据访问2，etl_sys_cd为空格" +
			"4.错误的数据访问3，etl_sys_cd不存在" +
			"5.错误的数据访问4，etl_sys_name为空" +
			"6.错误的数据访问5，etl_sys_name为空格")
	@Test
	public void updateEtlSys() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd + 0)
				.addData("etl_sys_name", "更新工程测试1")
				.addData("comments", "更新作业调度工程测试1")
				.post(getActionUrl("updateEtlSys"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证数据正确性
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Etl_sys etl_sys = SqlOperator.queryOneObject(db, Etl_sys.class, "select * from "
					+ Etl_sys.TableName + " where etl_sys_cd=?", EtlSysCd + 0).orElseThrow(() ->
					new BusinessException("sql查询错误或者映射实体错误！"));
			assertThat(etl_sys.getSys_run_status(), is(Job_Status.STOP.getCode()));
			assertThat(etl_sys.getBath_shift_time(), is(DateUtil.getSysDate()));
			assertThat(etl_sys.getEtl_sys_name(), is("更新工程测试1"));
			assertThat(etl_sys.getComments(), is("更新作业调度工程测试1"));
			assertThat(etl_sys.getMain_serv_sync(), is(Main_Server_Sync.YES.getCode()));
			assertThat(etl_sys.getUser_id(), is(UserId));
		}
		// 2.错误的数据访问1，etl_sys_cd为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "")
				.addData("etl_sys_name", "更新测试2")
				.addData("comments", "更新作业调度工程测试2")
				.post(getActionUrl("updateEtlSys"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "aaa")
				.addData("etl_sys_name", "更新工程测试4")
				.addData("comments", "更新作业调度工程测试4")
				.post(getActionUrl("updateEtlSys"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问4，etl_sys_name为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "upCs4")
				.addData("etl_sys_name", "")
				.addData("comments", "更新作业调度工程测试1")
				.post(getActionUrl("updateEtlSys"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 6.错误的数据访问5，etl_sys_name为空格
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "upCs5")
				.addData("etl_sys_name", " ")
				.addData("comments", "更新作业调度工程测试5")
				.post(getActionUrl("updateEtlSys"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

//	@Method(desc = "读取trigger日志", logicStep = "1.正常的数据访问1，数据都正常")
//	@Test
//	public void downloadControlOrTriggerLog() {
//		// 1.正常的数据访问1，数据都正常
//		String bodyString = new HttpClient()
//				.addData("etl_sys_cd", EtlSysCd + 0)
//				.addData("curr_bath_date", DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
//						.toString())
//				.addData("isControl", IsFlag.Shi.getCode())
//				.post(getActionUrl("downloadControlOrTriggerLog"))
//				.getBodyString();
//		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
//				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
//		assertThat(ar.isSuccess(), is(true));
//	}
//
	@Method(desc = "读取control日志", logicStep = "1.正常的数据访问1，数据都正常")
	@Test
	public void readControlOrTriggerLog() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd + 0)
				.addData("readNum", 100)
				.addData("isControl", IsFlag.Shi.getCode())
				.post(getActionUrl("readControlOrTriggerLog"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
	}

	@Method(desc = "停止工程", logicStep = "1.正常的数据访问1，数据都正常" +
			"2.正常的数据访问1，etl_sys_cd为空" +
			"3.正常的数据访问2，etl_sys_cd不存在")
	@Test
	public void stopEtlProject() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("etl_sys_cd", EtlSysCd + 0)
				.post(getActionUrl("stopEtlProject"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Etl_sys etl_sys = SqlOperator.queryOneObject(db, Etl_sys.class, "select sys_run_status from "
					+ Etl_sys.TableName + " where etl_sys_cd=?", EtlSysCd + 0)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(etl_sys.getSys_run_status(), is(Job_Status.STOP.getCode()));
		}
		// 2.正常的数据访问1，etl_sys_cd为空
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "")
				.post(getActionUrl("stopEtlProject"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.正常的数据访问2，etl_sys_cd不存在
		bodyString = new HttpClient()
				.addData("etl_sys_cd", "aaaaaaaaa")
				.post(getActionUrl("stopEtlProject"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "停止工程", logicStep = "1.正常的数据访问1，数据都正常" +
			"2.正常的数据访问1，etl_sys_cd为空" +
			"3.正常的数据访问2，etl_sys_cd不存在")
	@Test
	public void deleteEtlProject() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			// 删除前查询数据库，确认预期删除的数据存在
			long num = SqlOperator.queryNumber(db, "select count(1) from " +
					Etl_sys.TableName + " where etl_sys_cd=?", EtlSysCd + 3)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat("删除操作前，Etl_sys表中的确存在这样一条数据", num, is(1L));
			num = SqlOperator.queryNumber(db, "select count(1) from " +
					Etl_resource.TableName + " where etl_sys_cd=?", EtlSysCd + 3)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat("删除操作前，Etl_resource表中的确存在这样一条数据", num, is(2L));
			String bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd+3)
					.post(getActionUrl("deleteEtlProject"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期数据已删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_sys.TableName
					+ " where etl_sys_cd=?", EtlSysCd + 3)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat("删除操作后，确认这条数据已删除", num, is(0L));
			num = SqlOperator.queryNumber(db, "select count(1) from " + Etl_resource.TableName
					+ " where etl_sys_cd=?", EtlSysCd + 3).orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat("删除操作后，确认这条数据已删除", num, is(0L));
			// 2.错误的数据访问1，etl_sys_cd为空
			bodyString = new HttpClient().addData("etl_sys_cd", "")
					.post(getActionUrl("deleteEtlProject"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，etl_sys_cd不存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.post(getActionUrl("deleteEtlProject"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，工程下还有任务或者作业存在
			bodyString = new HttpClient().addData("etl_sys_cd", EtlSysCd)
					.post(getActionUrl("deleteEtlProject"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}
}
