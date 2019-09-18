package hrds.b.biz.datasource;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import fd.ng.web.util.Dbo;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Source_relation_dep;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;
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
	private static final int Init_Rows = 10; // 向表中初始化的数据条数。
	// 初始化登录用户ID
	private static final long UserId = 5555;

	/**
	 * 初始化测试用例数据
	 * <p>
	 * 1.构造data_source表数据
	 * 2.构造source_relation_dep表数据
	 * 3.初始化data_source表信息，批量插入
	 * 4.初始化source_relation_dep表信息，批量插入
	 * 5.模拟用户登录
	 */
	@Before
	public void before() {
		// 初始化测试用例数据
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Object[]> params = new ArrayList<>();
			List<Object[]> srdParams = new ArrayList<>();
			for (long i = -30L; i < -30 + Init_Rows; i++) {
				long source_id = i;
				String datasource_remark = "init" + i;
				String datasource_name = "init" + i;
				String datasource_number = "d" + i;
				String create_date = DateUtil.getSysDate();
				String create_time = DateUtil.getSysTime();
				// 1.构造data_source表数据
				Object[] objects = new Object[]{source_id, datasource_remark, datasource_name,
						datasource_number, create_date, create_time, UserId};
				params.add(objects);
				// 2.构造source_relation_dep表数据
				for (long j = 1; j <= 3; j++) {
					long dep_id = j + Init_Rows + i;
					Object[] srdObjects = {dep_id, source_id};
					srdParams.add(srdObjects);
				}
			}
			// 3.初始化data_source表信息，批量插入
			int[] num = SqlOperator.executeBatch(db,
					"insert into " + Data_source.TableName + "( source_id, datasource_remark, " +
							"datasource_name,datasource_number,create_date, create_time, " +
							"create_user_id) values(?, ?,?,?,?,?,?)",
					params
			);
			assertThat("测试数据初始化", num.length, is(Init_Rows));

			// 4.初始化source_relation_dep表信息，批量插入
			int[] srdNum = SqlOperator.executeBatch(db,
					"insert into " + Source_relation_dep.TableName + "  values(?, ?)",
					srdParams
			);
			assertThat("测试数据初始化", srdNum.length, is(30));

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
	 */
	@After
	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			for (long i = -30L; i < -30 + Init_Rows; i++) {
				// 1.测试完成后删除data_source表测试数据
				SqlOperator.execute(db, "delete from " + Data_source.TableName +
						"  where source_id=?", i);
				// 2.判断data_source数据是否被删除
				long num = SqlOperator.queryNumber(db,
						"select count(1) from " + Data_source.TableName +
								"  where source_id=?", i)
						.orElseThrow(() -> new RuntimeException("count fail!"));
				assertThat("此条数据删除后，记录数应该为0", num, is(0L));

				// 3.测试完成后删除source_relation_dep表测试数据
				SqlOperator.execute(db, "delete from " + Source_relation_dep.TableName +
						"  where source_id=?", i);
				// 4.判断source_relation_dep数据是否被删除
				long srdNum = SqlOperator.queryNumber(db,
						"select count(1) from " + Source_relation_dep.TableName + "  where " +
								"source_id=?", i)
						.orElseThrow(() -> new RuntimeException("count fail!"));
				assertThat("此条数据删除后，记录数应该为0", srdNum, is(0L));
				// 5.单独删除新增数据，因为新增数据主键是自动生成的，所以要通过其他方式删除
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
	 * 参数：source_id:数据源ID,datasource_remark:数据源备注,datasource_number:数据源编号,
	 * datasource_name:数据源名称,depIds:部门ID
	 * 取值范围：除source_id外不能为空以及空格,source_id不能为空格
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
	 */
	@Test
	public void saveDataSource() {

		// 1.测试数据源新增功能,数据都不为空
		String bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + -30)
				.addData("datasource_number", "s" + -30)
				.addData("depIds", "-30,-29,-28")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));

		// 2.测试数据源编辑功能
		bodyString = new HttpClient()
				.addData("source_id", -30L)
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + -30)
				.addData("datasource_number", "s" + -29)
				.addData("depIds", "-30,-29")
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
		assertThat(ar.getMessage(), is("数据源编号重复,datasource_number=d300"));
		// 4.测试数据源新增，数据源名称不能为空
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "")
				.addData("datasource_number", "s" + -28)
				.addData("depIds", "-30")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("数据源名称不能为空以及不能为空格，datasource_name="));

		// 5.测试数据源新增，数据源名称不能为空格
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", " ")
				.addData("datasource_number", "ds" + new Random().nextInt(99))
				.addData("depIds", "-30")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("数据源名称不能为空以及不能为空格，" +
				"datasource_name= "));

		// 6.测试数据源新增，数据源编号不能为空
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + -30)
				.addData("datasource_number", "")
				.addData("depIds", "-30")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat((String) ar.getMessage(), is("数据源编号不能为空以及不能为空格" +
				"或数据源编号长度不能超过四位，datasource_number="));

		// 7.测试数据源新增，数据源编号不能为空格
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + -30)
				.addData("datasource_number", " ")
				.addData("depIds", "-30")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("数据源编号不能为空以及不能为空格" +
				"或数据源编号长度不能超过四位，datasource_number= "));

		// 8.测试数据源新增，数据源编号长度不能超过四位
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + -30)
				.addData("datasource_number", "ds100")
				.addData("depIds", "-30")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("数据源编号不能为空以及不能为空格" +
				"或数据源编号长度不能超过四位，datasource_number=ds100"));

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
		assertThat(ar.getMessage(), is("部门id不能为空格，depIds= "));

		// 11.测试保存数据源，部门id长度不能超过10位（这里指的是分隔后每个部门id的长度）
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + -30)
				.addData("datasource_number", "s" + -25)
				.addData("depIds", "-24,10000000001")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("新增保存数据源与部门关系Source_relation_dep表信息失败," +
				"dep_id=10000000001"));

		// 12.测试数据源编辑功能，source_id不合法（长度超过10）
		bodyString = new HttpClient()
				.addData("source_id", 10000000006L)
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + -30)
				.addData("datasource_number", "s" + -29)
				.addData("depIds", "-30,-26")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("source_id长度不能超过10，source_id=10000000006"));
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
		String bodyString = new HttpClient().addData("source_id", -29)
				.post(getActionUrl("searchDataSource")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));

		// 2.查询数据源，查询不到数据
		bodyString = new HttpClient().addData("source_id", "10000000005")
				.post(getActionUrl("searchDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
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
					.addData("source_id", -27)
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
			assertThat(ar.getMessage(), is("删除数据源信息表data_source失败，数据库里没有此条数据，" +
					"source_id=100006"));
		}
	}

}

