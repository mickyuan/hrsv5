package hrds.b.biz.datasource;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.AgentStatus;
import hrds.commons.codes.AgentType;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Source_relation_dep;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * 数据源增删改，导入、下载测试类
 *
 * @author dhw
 * @date 2019-09-18 10:48:14
 */
public class DataSourceActionTest extends WebBaseTestCase {
	private static final int Init_Rows = 5; // 向表中初始化的数据条数。
	// 初始化登录用户ID
	private static final long UserId = 5555;

	/**
	 * 初始化测试用例数据
	 * <p>
	 * 1.创建data_source表实体对象
	 * 2.创建source_relation_dep表对象实体
	 * 3.封装data_source表数据
	 * 4.初始化data_source表信息，批量插入
	 * 5.构造source_relation_dep表数据并封装数据
	 * 6.初始化source_relation_dep表信息，批量插入
	 * 7.提交事务
	 * 8.模拟用户登录
	 */
	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.创建data_source表实体对象
			Data_source data_source = new Data_source();
			// 2.创建source_relation_dep表实体对象
			Source_relation_dep source_relation_dep = new Source_relation_dep();
			// 3.创建agent_info表实体对象
			Agent_info agent_info = new Agent_info();
			for (long i = 0L; i < Init_Rows; i++) {
				// 3.封装data_source表数据
				data_source.setDatasource_name("init" + i);
				data_source.setSource_id(i - 30 * 10000000);
				data_source.setCreate_user_id(UserId);
				data_source.setCreate_date(DateUtil.getSysDate());
				data_source.setCreate_time(DateUtil.getSysTime());
				data_source.setDatasource_remark("init" + i);
				data_source.setDatasource_number("d" + i);
				// 4.初始化data_source表信息，批量插入
				int num = data_source.add(db);
				assertThat("测试数据初始化", num, is(1));
				// 5.构造source_relation_dep表数据并封装数据
				source_relation_dep.setSource_id(i - 30 * 10000000);
				for (long j = 1; j <= 3; j++) {
					source_relation_dep.setDep_id(-j + i - 30 * 10000000);
					// 6.初始化source_relation_dep表信息，批量插入
					int srdNum = source_relation_dep.add(db);
					assertThat("测试数据初始化", srdNum, is(1));
				}

				agent_info.setAgent_id(i - 30 * 10000000);
				agent_info.setAgent_name("数据库agent");
				agent_info.setSource_id(i - 30 * 10000000);
				agent_info.setCreate_date(DateUtil.getSysDate());
				agent_info.setCreate_time(DateUtil.getSysTime());
				if (i < 2) {
					agent_info.setAgent_type(AgentType.ShuJuKu.getCode());
				} else if (i >= 2 && i < 4) {
					agent_info.setAgent_type(AgentType.DBWenJian.getCode());
				} else if (i >= 4 && i < 6) {
					agent_info.setAgent_type(AgentType.WenJianXiTong.getCode());
				} else if (i >= 6 && i < 8) {
					agent_info.setAgent_type(AgentType.FTP.getCode());
				} else {
					agent_info.setAgent_type(AgentType.DuiXiang.getCode());
				}
				agent_info.setAgent_ip("10.71.4.51");
				agent_info.setAgent_port("34567");
				if (i < 3) {
					agent_info.setAgent_status(AgentStatus.YiLianJie.getCode());
				} else if (i >= 3 && i < 7) {
					agent_info.setAgent_status(AgentStatus.WeiLianJie.getCode());
				} else {
					agent_info.setAgent_status(AgentStatus.ZhengZaiYunXing.getCode());
				}
				int aiNum = agent_info.add(db);
				assertThat("测试数据初始化", aiNum, is(1));
			}
			// 7.提交事务
			SqlOperator.commitTransaction(db);
		}
		// 8.模拟用户登录
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
	 * 5.单独删除新增数据，因为新增数据主键是自动生成的，所以要通过其他方式删除
	 */
	@After
	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			for (long i = 0L; i < Init_Rows; i++) {
				// 1.测试完成后删除data_source表测试数据
				SqlOperator.execute(db, "delete from " + Data_source.TableName +
						"  where source_id=?", i - 30 * 10000000);
				// 2.判断data_source数据是否被删除
				long num = SqlOperator.queryNumber(db,
						"select count(1) from " + Data_source.TableName +
								"  where source_id=?", i - 30 * 10000000)
						.orElseThrow(() -> new RuntimeException("count fail!"));
				assertThat("此条数据删除后，记录数应该为0", num, is(0L));

				// 3.测试完成后删除source_relation_dep表测试数据
				SqlOperator.execute(db, "delete from " + Source_relation_dep.TableName +
						"  where source_id=?", i - 30 * 10000000);
				// 4.判断source_relation_dep数据是否被删除
				long srdNum = SqlOperator.queryNumber(db,
						"select count(1) from " + Source_relation_dep.TableName + "  where " +
								"source_id=?", i - 30 * 10000000)
						.orElseThrow(() -> new RuntimeException("count fail!"));
				assertThat("此条数据删除后，记录数应该为0", srdNum, is(0L));
				// 5.测试完成后删除agent_info表测试数据
				SqlOperator.execute(db, "delete from " + Agent_info.TableName + "  where " +
						"agent_id=?", i - 30 * 10000000);
				SqlOperator.commitTransaction(db);
				Long aiNum = SqlOperator.queryNumber(db, "select count(1) from " +
						Agent_info.TableName + " where agent_id=?", i)
						.orElseThrow(() -> new RuntimeException("count fail!"));
				assertThat("此条记录删除后，数据为0", aiNum, is(0L));
				// 6.单独删除新增数据，因为新增数据主键是自动生成的，所以要通过其他方式删除
				SqlOperator.execute(db, "delete from " + Data_source.TableName + "  where " +
						" datasource_number=?", "s-30");
				SqlOperator.execute(db, "delete from " + Data_source.TableName + "  where " +
						" datasource_number=?", "s-29");
				SqlOperator.execute(db, "delete from " + Data_source.TableName + "  where " +
						" datasource_number=?", "s-28");
				SqlOperator.execute(db, "delete from " + Data_source.TableName + "  where " +
						" datasource_number=?", "s-27");
				SqlOperator.execute(db, "delete from " + Data_source.TableName + "  where " +
						" datasource_number=?", "s-26");
				SqlOperator.execute(db, "delete from " + Data_source.TableName + "  where " +
						" datasource_number=?", "s-25");
				SqlOperator.commitTransaction(db);
			}
		}
	}

	/**
	 * 新增/编辑数据源测试
	 * <p>
	 * 1.测试数据源新增功能,数据都不为空
	 * 2.测试数据源编辑功能
	 * 3.测试数据源新增，数据源编号datasource_number重复
	 * 4.测试数据源新增，数据源名称datasource_name不能为空
	 * 5.测试数据源新增，数据源名称datasource_name不能为空格
	 * 6.测试数据源新增，数据源编号datasource_number不能为空
	 * 7.测试数据源新增，数据源编号datasource_number不能为空格
	 * 8.测试数据源新增，数据源编号datasource_number长度不能超过四位
	 * 9.测试保存数据源，部门id不能为空
	 * 10.测试保存数据源，部门id不能为空格
	 * 11.测试保存数据源，部门id长度不能超过10位（这里指的是分隔后每个部门id的长度）
	 * 12.测试数据源编辑功能，source_id不合法（长度超过10）
	 *
	 * <p>
	 * 参数：source_id:数据源ID,datasource_remark:数据源备注,datasource_number:数据源编号,
	 * datasource_name:数据源名称,depIds:部门ID
	 * 取值范围：除source_id外不能为空以及空格,source_id不能为空格
	 */
	@Test
	public void saveDataSource() {

		// 1.测试数据源新增功能,数据都不为空
		String bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + -30)
				.addData("datasource_number", "s" + -30)
				.addData("depIds", new String[]{"-30,-29,-28"})
				.post(getActionUrl("saveDataSource")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));

		// 2.测试数据源编辑功能
		bodyString = new HttpClient()
				.addData("source_id", -30L)
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + -30)
				.addData("datasource_number", "s" + -29)
				.addData("depIds", new String[]{"-30,-29"})
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));

		// 3.测试数据源新增，数据源编号重复
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + -30)
				.addData("datasource_number", "d300")
				.addData("depIds", "-30")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		// 4.测试数据源新增，数据源名称不能为空
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "")
				.addData("datasource_number", "s" + -28)
				.addData("depIds", new String[]{"-30"})
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 5.测试数据源新增，数据源名称不能为空格
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", " ")
				.addData("datasource_number", "ds" + new Random().nextInt(99))
				.addData("depIds", new String[]{"-30"})
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 6.测试数据源新增，数据源编号不能为空
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + -30)
				.addData("datasource_number", "")
				.addData("depIds", new String[]{"-30"})
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 7.测试数据源新增，数据源编号不能为空格
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + -30)
				.addData("datasource_number", " ")
				.addData("depIds", new String[]{"-30"})
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 8.测试数据源新增，数据源编号长度不能超过四位
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + -30)
				.addData("datasource_number", "ds100")
				.addData("depIds", new String[]{"-30"})
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 9.保存数据源，部门id不能为空
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + -30)
				.addData("datasource_number", "s" + -27)
				.addData("depIds", "")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 10.测试保存数据源，部门id不能为空格
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + -30)
				.addData("datasource_number", "s" + -26)
				.addData("depIds", " ")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 11.测试保存数据源，部门id长度不能超过10位（这里指的是分隔后每个部门id的长度）
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + -30)
				.addData("datasource_number", "s" + -25)
				.addData("depIds", new String[]{"-24,10000000001"})
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 12.测试数据源编辑功能，source_id不合法（长度超过10）
		bodyString = new HttpClient()
				.addData("source_id", 10000000006L)
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + -30)
				.addData("datasource_number", "s" + -29)
				.addData("depIds", new String[]{"-30,-26"})
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
	}

	/**
	 * 查询数据源信息
	 * <p>
	 * 参数：source_id  long
	 * 含义：data_source表主键ID，source_relation_dep表外键
	 * 取值范围：不为空以及不为空格
	 * <p>
	 * 1.查询数据源，source_id不为空也不为空格
	 * 4.查询数据源，source_id不合法（长度超过10）
	 * 5.查询数据源，合法的source_id但是此数据源下没有数据
	 */
	@Test
	public void searchDataSource() {
		// 1.查询数据源
		// 调用action方法返回结果信息
		String bodyString = new HttpClient().addData("source_id", -290000000)
				.post(getActionUrl("searchDataSource")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
		Type type = new TypeReference<List<Map<String, Object>>>() {
		}.getType();
		String data = JsonUtil.getNodeValue(bodyString, "data");
		List<Map<String, Object>> list = JsonUtil.toObject(data, type);
		assertThat(-290000000, is(list.get(0).get("source_id")));

		// 2.查询数据源，查询不到数据
		bodyString = new HttpClient().addData("source_id", "10000000005")
				.post(getActionUrl("searchDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
		// 获取返回数据
		data = JsonUtil.getNodeValue(bodyString, "data");
		list = JsonUtil.toObject(data, type);
		assertThat(list.size(), is(0));
	}

	/**
	 * 删除数据源信息
	 * <p>
	 * 参数：source_id long
	 * 含义：data_source表主键，source_relation_dep表外键
	 * 取值范围：不为空以及不为空格
	 *
	 * <p>
	 * 1.删除数据源信息，source_id不为空也不为空格，正常删除
	 * 5.删除数据源信息，数据源下没有数据
	 */
	@Test
	public void deleteDataSource() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.删除数据源信息，source_id不为空也不为空格，正常删除
			// 访问action，返回结果信息
			String bodyString = new HttpClient()
					.addData("source_id", -2700000000L)
					.post(getActionUrl("deleteDataSource"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
			assertThat(ar.isSuccess(), is(true));

			// 2.删除数据源信息，合法的source_id但是此数据源下没有数据
			bodyString = new HttpClient()
					.addData("source_id", "100006")
					.post(getActionUrl("deleteDataSource"))
					.getBodyString();
			ar = JsonUtil.toObject(bodyString, ActionResult.class);
			assertThat(ar.isSuccess(), is(false));
		}
	}

}

