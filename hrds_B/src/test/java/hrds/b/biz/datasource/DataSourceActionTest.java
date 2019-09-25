package hrds.b.biz.datasource;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.AgentStatus;
import hrds.commons.codes.AgentType;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Source_relation_dep;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * 数据源增删改，导入、下载测试类
 *
 * @author dhw
 * @date 2019-09-18 10:48:14
 */
public class DataSourceActionTest extends WebBaseTestCase {
	// 测试登录用户ID
	private static final long UserId = 5555L;
	// 测试数据源 source_id
	private static final long Source_Id = -1000000000L;
	// 测试数据源 source_id,新建数据源，下面没有agent
	private static final long Source_Id2 = -1000000001L;
	// 测试数据库 agent_id
	private static final long DB_Agent_Id = -2000000001L;
	// 测试数据文件 agent_id
	private static final long DF_Agent_Id = -2000000002L;
	// 测试非结构化 agent_id
	private static final long Uns_Agent_Id = -2000000003L;
	// 测试半结构化 agent_id
	private static final long Semi_Agent_Id = -2000000004L;
	// 测试FTP agent_id
	private static final long FTP_Agent_Id = -2000000005L;
	// 测试部门ID dep_id,测试第一部门
	private static final long Dep_Id1 = -3000000001L;
	// 测试部门ID dep_id 测试第二部门
	private static final long Dep_Id2 = -3000000002L;

	/**
	 * 初始化测试用例数据
	 * <p>
	 * 1.构建数据源data_source表测试数据
	 * 2.构造department_info部门表测试数据
	 * 3.构造source_relation_dep表测试数据
	 * 4.构造agent_info表测试数据
	 * 5.提交事务
	 * 6.模拟用户登录
	 * <p>
	 * 测试数据：
	 * 1.data_source表:有2条数据，source_id为-1000000000,-1000000001
	 * 2.source_relation表：有4条数据,source_id为-1000000000，-1000000000,
	 * dep_id为-2000000001，-2000000002
	 * 3.agent_info表：有5条数据,agent_id有五种，数据库agent,数据文件agent,非结构化agent,半结构化agent,
	 * FTP agent,分别为-2000000001到-2000000005
	 * 4.department_info表，有2条数据，dep_id为-3000000001，-3000000002
	 */
	@BeforeClass
	public static void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.构建数据源data_source表测试数据
			// 创建data_source表实体对象
			Data_source data_source = new Data_source();
			// 封装data_source表数据
			for (int i = 0; i < 2; i++) {
				if (i == 0) {
					data_source.setSource_id(Source_Id);
					data_source.setDatasource_number("init");
				} else {
					data_source.setSource_id(Source_Id2);
					data_source.setDatasource_number("ini2");
				}
				data_source.setDatasource_name("init" + i);
				data_source.setCreate_date(DateUtil.getSysDate());
				data_source.setCreate_time(DateUtil.getSysTime());
				data_source.setCreate_user_id(UserId);
				data_source.setSource_remark("数据源详细描述" + i);
				data_source.setDatasource_remark("备注" + i);
				// 初始化data_source表信息
				int num = data_source.add(db);
				assertThat("测试数据data_source初始化", num, is(1));
			}
			// 2.构造department_info部门表测试数据
			// 创建department_info表实体对象
			Department_info department_info = new Department_info();
			for (int i = 0; i < 2; i++) {
				if (i == 0) {
					department_info.setDep_id(Dep_Id1);
					department_info.setDep_name("测试第一部门");
				} else {
					department_info.setDep_id(Dep_Id2);
					department_info.setDep_name("测试第二部门");
				}
				department_info.setCreate_date(DateUtil.getSysDate());
				department_info.setCreate_time(DateUtil.getSysTime());
				department_info.setDep_remark("测试");
				int diNum = department_info.add(db);
				assertThat("测试数据department_info初始化", diNum, is(1));
			}

			// 3.构造source_relation_dep数据源与部门关系表测试数据
			// 创建source_relation_dep表实体对象
			Source_relation_dep source_relation_dep = new Source_relation_dep();
			for (long i = 0; i < 4; i++) {
				if (i < 2) {
					source_relation_dep.setSource_id(Source_Id);
					if (i == 0) {
						source_relation_dep.setDep_id(Dep_Id1);
					} else {
						source_relation_dep.setDep_id(Dep_Id2);
					}
				} else {
					// 封装source_relation_dep表数据
					source_relation_dep.setSource_id(Source_Id2);
					if (i == 2) {
						source_relation_dep.setDep_id(Dep_Id1);
					} else {
						source_relation_dep.setDep_id(Dep_Id2);
					}
				}
				// 初始化source_relation_dep表信息
				int srdNum = source_relation_dep.add(db);
				assertThat("测试source_relation_dep数据初始化", srdNum, is(1));
			}
			// 4.构造agent_info表测试数据
			// 创建agent_info表实体对象
			Agent_info agent_info = new Agent_info();
			for (int i = 0; i < 5; i++) {
				// 封装agent_info数据
				agent_info.setSource_id(Source_Id);
				agent_info.setCreate_date(DateUtil.getSysDate());
				agent_info.setCreate_time(DateUtil.getSysTime());
				agent_info.setAgent_ip("10.71.4.51");
				agent_info.setUser_id(UserId);
				agent_info.setAgent_port("34567");
				// 初始化不同类型的agent
				if (i == 1) {
					// 数据库 agent
					agent_info.setAgent_id(DB_Agent_Id);
					agent_info.setAgent_type(AgentType.ShuJuKu.getCode());
					agent_info.setAgent_name("sjkAgent");
				} else if (i == 2) {
					// 数据文件 Agent
					agent_info.setAgent_id(DF_Agent_Id);
					agent_info.setAgent_type(AgentType.DBWenJian.getCode());
					agent_info.setAgent_name("DFAgent");

				} else if (i == 3) {
					// 非结构化 Agent
					agent_info.setAgent_id(Uns_Agent_Id);
					agent_info.setAgent_type(AgentType.WenJianXiTong.getCode());
					agent_info.setAgent_name("UnsAgent");
				} else if (i == 4) {
					// 半结构化 Agent
					agent_info.setAgent_id(Semi_Agent_Id);
					agent_info.setAgent_type(AgentType.FTP.getCode());
					agent_info.setAgent_name("SemiAgent");
				} else {
					// FTP Agent
					agent_info.setAgent_id(FTP_Agent_Id);
					agent_info.setAgent_type(AgentType.DuiXiang.getCode());
					agent_info.setAgent_name("FTPAgent");
				}
				// 初始化agent不同的连接状态
				if (i < 2) {
					// 已连接
					agent_info.setAgent_status(AgentStatus.YiLianJie.getCode());
				} else if (i >= 2 && i < 4) {
					// 未连接
					agent_info.setAgent_status(AgentStatus.WeiLianJie.getCode());
				} else {
					// 正在运行
					agent_info.setAgent_status(AgentStatus.ZhengZaiYunXing.getCode());
				}
				// 初始化agent_info数据
				int aiNum = agent_info.add(db);
				assertThat("测试agent_info数据初始化", aiNum, is(1));
			}
			// 4.提交事务
			SqlOperator.commitTransaction(db);
		}
		// 5.模拟用户登录
		//String responseValue = new HttpClient()
		//		.buildSession()
		//		.addData("username", UserId)
		//		.addData("password", "111111")
		//		.post("http://127.0.0.1:8099/A/action/hrds/a/biz/login/login")
		//		.getBodyString();
		//ActionResult ar = JsonUtil.toObject(responseValue, ActionResult.class);
		//assertThat("用户登录", ar.getCode(), is(220));
	}

	/**
	 * 测试完删除测试数据
	 * <p>
	 * 1.测试完成后删除data_source表测试数据
	 * 2.判断data_source数据是否被删除
	 * 3.测试完成后删除source_relation_dep表测试数据
	 * 4.判断source_relation_dep数据是否被删除
	 * 5.测试完成后删除agent_info表测试数据
	 * 6.判断agent_info数据是否被删除
	 * 7.测试完成后删除department_info表测试数据
	 * 8.判断department_info表数据是否被删除
	 * 9.单独删除新增数据，因为新增数据主键是自动生成的，所以要通过其他方式删除
	 * 10.提交事务
	 */
	@AfterClass
	public static void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.测试完成后删除data_source表测试数据
			SqlOperator.execute(db, "delete from data_source where source_id=?",
					Source_Id);
			SqlOperator.execute(db, "delete from data_source where source_id=?",
					Source_Id2);
			// 2.判断data_source数据是否被删除
			long num = SqlOperator.queryNumber(db,
					"select count(1) from data_source where source_id=?", Source_Id)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			long num2 = SqlOperator.queryNumber(db,
					"select count(1) from data_source where source_id=?", Source_Id2)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num2, is(0L));

			// 3.测试完成后删除source_relation_dep表测试数据
			SqlOperator.execute(db,
					"delete from source_relation_dep where dep_id=?", Dep_Id1);
			SqlOperator.execute(db,
					"delete from source_relation_dep where dep_id=?", Dep_Id2);
			// 4.判断source_relation_dep数据是否被删除
			long srdNum = SqlOperator.queryNumber(db, "select count(1) from " +
					"source_relation_dep where dep_id=?", Dep_Id1)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", srdNum, is(0L));
			long srdNum2 = SqlOperator.queryNumber(db, "select count(1) from " +
					"source_relation_dep where dep_id=?", Dep_Id2)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", srdNum2, is(0L));

			// 5.测试完成后删除agent_info表数据库agent测试数据
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", DB_Agent_Id);
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", DF_Agent_Id);
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", Uns_Agent_Id);
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", Semi_Agent_Id);
			SqlOperator.execute(db, "delete from agent_info where agent_id=?", FTP_Agent_Id);
			// 6.判断agent_info表数据是否被删除
			long DBNum = SqlOperator.queryNumber(db, "select count(1) from  agent_info " +
					" where  agent_id=?", DB_Agent_Id).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", DBNum, is(0L));
			long DFNum = SqlOperator.queryNumber(db, "select count(1) from agent_info" +
					" where  agent_id=?", DF_Agent_Id).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", DFNum, is(0L));
			long UnsNum = SqlOperator.queryNumber(db, "select count(1) from agent_info " +
					" where agent_id=?", Uns_Agent_Id).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", UnsNum, is(0L));
			long SemiNum = SqlOperator.queryNumber(db, "select count(1) from agent_info " +
					" where agent_id=?", Semi_Agent_Id).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", SemiNum, is(0L));
			long FTPNum = SqlOperator.queryNumber(db, "select count(1) from agent_info " +
					" where agent_id=?", FTP_Agent_Id).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", FTPNum, is(0L));
			// 7.测试完成后删除department_info表测试数据
			SqlOperator.execute(db, "delete from department_info where dep_id=?", Dep_Id1);
			SqlOperator.execute(db, "delete from department_info where dep_id=?", Dep_Id2);
			// 8.判断department_info表数据是否被删除
			long diNum = SqlOperator.queryNumber(db, "select count(1) from department_info "
					+ " where dep_id=?", Dep_Id1).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			long diNum2 = SqlOperator.queryNumber(db, "select count(1) from department_info "
					+ " where dep_id=?", Dep_Id2).orElseThrow(() -> new RuntimeException(
					"count fail!"));
			assertThat("此条记录删除后，数据为0", diNum, is(0L));
			assertThat("此条记录删除后，数据为0", diNum2, is(0L));
			// 9.单独删除新增数据，因为新增数据主键是自动生成的，所以要通过其他方式删除
			SqlOperator.execute(db, "delete from data_source where datasource_number=?",
					"cs01");
			SqlOperator.execute(db, "delete from data_source where datasource_number=?",
					"cs02");
			SqlOperator.execute(db, "delete from data_source where datasource_number=?",
					"cs03");
			SqlOperator.execute(db, "delete from data_source where datasource_number=?",
					"cs04");
			SqlOperator.execute(db, "delete from data_source where datasource_number=?",
					"cs05");
			SqlOperator.execute(db, "delete from data_source where datasource_number=?",
					"cs06");
			// 10.提交事务
			SqlOperator.commitTransaction(db);
		}

	}

	/**
	 * 新增/编辑数据源测试
	 * <p>
	 * 1.正确的数组访问1，测试数据源新增功能,数据都有效
	 * 2.错误的数据访问1，新增数据源信息,数据源名称不能为空
	 * 3.错误的数据访问2，新增数据源信息,数据源名称不能为空格
	 * 4.错误的数据访问3，新增数据源信息,数据源编号不能为空
	 * 5.错误的数据访问4，新增数据源信息,数据源编号不能为空格
	 * 6.错误的数据访问5，新增数据源信息,数据源编号长度不能超过四位
	 * 7.错误的数据访问6，新增数据源信息,部门id不能为空,创建部门表department_info时通过主键自动生成
	 * 8.错误的数据访问7，新增数据源信息,部门id不能为空格，创建部门表department_info时通过主键自动生成
	 */
	@Test
	public void saveDataSource() {
		// 1.正确的数组访问1，新增数据源信息,数据都有效
		String bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName01")
				.addData("datasource_number", "cs01")
				.addData("depIds", new String[]{"-3000000001", "-3000000002"})
				.post(getActionUrl("saveDataSource")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
		// 验证新增数据是否成功
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 判断data_source表数据是否新增成功
			OptionalLong number = SqlOperator.queryNumber(db, "select count(*) from" +
					" data_source where datasource_number=?", "cs01");
			assertThat("添加data_source数据成功", number.getAsLong(), is(1L));
			// 判断source_relation_dep表数据是否新增成功，初始化2条，新增1条，source_id不同
			OptionalLong srdNumber = SqlOperator.queryNumber(db, "select count(*) from" +
					" source_relation_dep where dep_id=?", Dep_Id1);
			assertThat("添加data_source数据成功", srdNumber.getAsLong(), is(3L));
			//判断source_relation_dep表数据是否新增成功，初始化2条，新增1条，source_id不同
			OptionalLong srdNumber2 = SqlOperator.queryNumber(db, "select count(*) from" +
					" source_relation_dep where dep_id=?", Dep_Id2);
			assertThat("添加data_source数据成功", srdNumber2.getAsLong(), is(3L));
		}
		// 2.错误的数据访问1，新增数据源信息,数据源名称不能为空
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "")
				.addData("datasource_number", "cs02")
				.addData("depIds", new String[]{"-3000000001"})
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，新增数据源信息,数据源名称不能为空格
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", " ")
				.addData("datasource_number", "cs03")
				.addData("depIds", new String[]{"-3000000001"})
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，新增数据源信息,数据源编号不能为空
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName02")
				.addData("datasource_number", "")
				.addData("depIds", new String[]{"-3000000001"})
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问4，新增数据源信息,数据源编号不能为空格
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName03")
				.addData("datasource_number", " ")
				.addData("depIds", new String[]{"-3000000001"})
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		// 6.错误的数据访问5，新增数据源信息,数据源编号长度不能超过四位
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName04")
				.addData("datasource_number", "cs100")
				.addData("depIds", new String[]{"-3000000001"})
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		// 7.错误的数据访问6，新增数据源信息,部门id不能为空,创建部门表department_info时通过主键自动生成
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName05")
				.addData("datasource_number", "cs05")
				.addData("depIds", "")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		// 8.错误的数据访问7，新增数据源信息,部门id不能为空格，创建部门表department_info时通过主键自动生成
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName06")
				.addData("datasource_number", "cs06")
				.addData("depIds", " ")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
	}

	/**
	 * 更新数据源data_source,source_relation_dep表信息
	 * <p>
	 * 1.正确的数据访问1，更新数据源信息，所有数据都有效
	 * 2.错误的数据访问1，更新数据源信息，数据源名称不能为空
	 * 3.错误的数据访问2，更新数据源信息，数据源名称不能为空格
	 * 4.错误的数据访问3，更新数据源信息，数据源编号不能为空
	 * 5.错误的数据访问4，更新数据源信息，数据源编号不能为空格
	 * 6.错误的数据访问5，更新数据源信息，数据源编号长度不能超过四位
	 * 7.错误的数据访问6，更新数据源信息，部门id不能为空,创建部门表department_info时通过主键自动生成
	 * 8.错误的数据访问7，更新数据源信息，部门id不能为空格，创建部门表department_info时通过主键自动生成
	 */
	@Test
	public void updateDataSource() {
		// 1.正确的数据访问1，更新数据源信息，所有数据都有效
		String bodyString = new HttpClient()
				.addData("source_id", Source_Id)
				.addData("source_remark", "测试update")
				.addData("datasource_name", "updateName")
				.addData("datasource_number", "up01")
				.addData("depIds", new String[]{"-3000000001"})
				.post(getActionUrl("updateDataSource")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
		// 验证更新数据是否成功
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 判断data_source表数据是否更新成功,这里指定类型返回会报错
			Optional<Data_source> data_source = SqlOperator.queryOneObject(db, Data_source.class,
					"select * from data_source where source_id=?", Source_Id);
			assertThat("更新data_source数据成功", data_source.get().getSource_id(),
					is(Source_Id));
			assertThat("更新data_source数据成功", data_source.get().getDatasource_name(),
					is("updateName"));
			assertThat("更新data_source数据成功", data_source.get().getDatasource_number(),
					is("up01"));
			assertThat("更新data_source数据成功", data_source.get().getSource_remark(),
					is("测试update"));
			// 判断source_relation_dep表数据是否更新成功
			Optional<Source_relation_dep> source_relation_dep = SqlOperator.queryOneObject(db,
					Source_relation_dep.class, "select * from source_relation_dep where " +
							"source_id=?", Source_Id);
			assertThat("更新source_relation_dep数据成功", source_relation_dep.get().
					getSource_id(), is(Source_Id));
			assertThat("更新source_relation_dep数据成功", source_relation_dep.get().getDep_id(),
					is(-3000000001L));

		}
		// 2.错误的数据访问1，更新数据源信息，数据源名称不能为空
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "")
				.addData("datasource_number", "up02")
				.addData("depIds", new String[]{"-1000000001"})
				.post(getActionUrl("updateDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，更新数据源信息，数据源名称不能为空格
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", " ")
				.addData("datasource_number", "up03")
				.addData("depIds", new String[]{"-3000000001"})
				.post(getActionUrl("updateDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，更新数据源信息，数据源编号不能为空
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName02")
				.addData("datasource_number", "")
				.addData("depIds", new String[]{"-3000000001"})
				.post(getActionUrl("updateDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问4，更新数据源信息，数据源编号不能为空格
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName03")
				.addData("datasource_number", " ")
				.addData("depIds", new String[]{"-3000000001"})
				.post(getActionUrl("updateDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		// 6.错误的数据访问5，更新数据源信息，数据源编号长度不能超过四位
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName04")
				.addData("datasource_number", "up100")
				.addData("depIds", new String[]{"-3000000001"})
				.post(getActionUrl("updateDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		// 7.错误的数据访问6，更新数据源信息，部门id不能为空,创建部门表department_info时通过主键自动生成
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName05")
				.addData("datasource_number", "up05")
				.addData("depIds", "")
				.post(getActionUrl("updateDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		// 8.错误的数据访问7，更新数据源信息，部门id不能为空格，创建部门表department_info时通过主键自动生成
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName06")
				.addData("datasource_number", "up06")
				.addData("depIds", " ")
				.post(getActionUrl("updateDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

	}

	/**
	 * 查询数据源信息,该测试方法只会返回两种情况，能查到数据或者查不到数据
	 * <p>
	 * 1.正确的数据访问1，查询数据源信息,正常返回数据
	 * 2.错误的数据访问1，查询数据源信息，查询不到数据
	 */
	@Test
	public void searchDataSource() {
		// 1.正确的数据访问1，查询数据源信息,正常返回数据
		String bodyString = new HttpClient().addData("source_id", Source_Id)
				.post(getActionUrl("searchDataSource")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
		// 获取返回结果
		Result dataForResult = ar.getDataForResult();
		// 循环验证查询结果的正确性
		for (int i = 0; i < dataForResult.getRowCount(); i++) {
			assertThat(Source_Id, is(dataForResult.getLong(i, "source_id")));
			assertThat(UserId, is(dataForResult.getLong(i, "create_user_id")));
			assertThat("init0", is(dataForResult.getString(i, "datasource_name")));
			assertThat("init", is(dataForResult.getString(i, "datasource_number")));
			assertThat("数据源详细描述0", is(dataForResult.getString(i, "source_remark")));
			if (i == 0) {
				assertThat(Dep_Id1, is(dataForResult.getLong(i, "dep_id")));
			} else {
				assertThat(Dep_Id2, is(dataForResult.getLong(i, "dep_id")));
			}
		}
		// 2.错误的数据访问1，查询数据源信息，此数据源下没有数据
		bodyString = new HttpClient().addData("source_id", -1000000009L)
				.post(getActionUrl("searchDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForResult().getRowCount(), is(0));
	}

	/**
	 * 删除数据源信息，该测试方法只有三种情况
	 * <p>
	 * 1.正确的数据访问1，删除数据源信息，数据为有效数据
	 * 2.错误的数据访问1，删除数据源信息，数据源下有agent，不能删除
	 * 3.错误的数据访问2，删除数据源信息，此数据源下没有数据
	 */
	@Test
	public void deleteDataSource() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正确的数据访问1，删除数据源信息，数据为有效数据
			// 删除前查询数据库，确认预期删除的数据存在
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					" data_source where source_id = ?", Source_Id2);
			assertThat("删除操作前，data_source表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(1L));
			String bodyString = new HttpClient()
					.addData("source_id", Source_Id2)
					.post(getActionUrl("deleteDataSource"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期数据已删除
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					" data_source where source_id = ?", Source_Id2);
			assertThat("删除操作前，data_source表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(0L));

			// 2.错误的数据访问1，删除数据源信息，数据源下有agent，不能删除
			bodyString = new HttpClient()
					.addData("source_id", Source_Id)
					.post(getActionUrl("deleteDataSource"))
					.getBodyString();
			ar = JsonUtil.toObject(bodyString, ActionResult.class);
			assertThat(ar.isSuccess(), is(false));

			// 3.错误的数据访问2，删除数据源信息，此数据源下没有数据
			bodyString = new HttpClient()
					.addData("source_id", -1000000009L)
					.post(getActionUrl("deleteDataSource"))
					.getBodyString();
			ar = JsonUtil.toObject(bodyString, ActionResult.class);
			assertThat(ar.isSuccess(), is(false));
		}
	}

}

