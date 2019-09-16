package hrds.b.biz.datasource;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
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

public class DataSourceActionTest extends WebBaseTestCase {
	private static final int Init_Rows = 10; // 向表中初始化的数据条数。

	private static final long UserId = 5555;

	@Before
	public void before() {
		// 初始化测试用例数据
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Object[]> params = new ArrayList<>();
			List<Object[]> srdParams = new ArrayList<>();
			// 初始化data_source表信息
			for (long i = -300L; i < -300 + Init_Rows; i++) {
				long source_id = i;
				String datasource_remark = "init" + i;
				String datasource_name = "init" + i;
				String datasource_number = "d" + i;
				String create_date = DateUtil.getSysDate();
				String create_time = DateUtil.getSysTime();
				Object[] objects = new Object[]{source_id, datasource_remark, datasource_name,
						datasource_number, create_date, create_time, UserId};
				params.add(objects);
				// source_relation_dep表信息
				for (long j = 1; j <= 3; j++) {
					long dep_id = j + Init_Rows + i;
					Object[] srdObjects = {dep_id, source_id};
					srdParams.add(srdObjects);
				}
			}
			int[] num = SqlOperator.executeBatch(db,
					"insert into " + Data_source.TableName + "( source_id, datasource_remark, " +
							"datasource_name,datasource_number,create_date, create_time, " +
							"create_user_id) values(?, ?,?,?,?,?,?)",
					params
			);
			assertThat("测试数据初始化", num.length, is(Init_Rows));

			// 初始化source_relation_dep表信息
			int[] srdNum = SqlOperator.executeBatch(db,
					"insert into " + Source_relation_dep.TableName + "  values(?, ?)",
					srdParams
			);
			assertThat("测试数据初始化", srdNum.length, is(30));

			SqlOperator.commitTransaction(db);
		}
		// 用户登录
		String responseValue = new HttpClient()
				.buildSession()
				.addData("username", UserId)
				.addData("password", "111111")
				.post("http://127.0.0.1:8099/A/action/hrds/a/biz/login/login")
				.getBodyString();
		ActionResult ar = JsonUtil.toObject(responseValue, ActionResult.class);
		assertThat("用户登录", ar.getCode(), is(220));
	}

	@After
	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			for (long i = -300L; i < -300 + Init_Rows; i++) {
				// 测试完成后删除data_source表测试数据
				SqlOperator.execute(db, "delete from " + Data_source.TableName +
						"  where source_id=?", i);
				long num = SqlOperator.queryNumber(db,
						"select count(1) from " + Data_source.TableName +
								"  where source_id=?", i)
						.orElseThrow(() -> new RuntimeException("count fail!"));
				assertThat("此条数据删除后，记录数应该为0", num, is(0L));

				// 测试完成后删除source_relation_dep表测试数据
				SqlOperator.execute(db, "delete from " + Source_relation_dep.TableName +
						"  where source_id=?", i);
				long srdNum = SqlOperator.queryNumber(db,
						"select count(1) from " + Source_relation_dep.TableName + "  where " +
								"source_id=?", i)
						.orElseThrow(() -> new RuntimeException("count fail!"));
				assertThat("此条数据删除后，记录数应该为0", srdNum, is(0L));
				SqlOperator.commitTransaction(db);
			}
		}
	}

	/**
	 * 新增/编辑数据源测试
	 * 参数：source_id:数据源ID,datasource_remark:数据源备注,datasource_number:数据源编号,
	 * datasource_name:数据源名称,depIds:部门ID
	 * 取值范围：除source_id外不能为空以及空格
	 * <p>
	 * 1.测试数据源新增功能,数据都不为空
	 * 2.测试数据源编辑功能
	 * 3.测试数据源新增，数据源编号重复
	 * 4.测试数据源新增，数据源名称不能为空
	 * 5.测试数据源新增，数据源名称不能为空格
	 * 6.测试数据源新增，数据源编号不能为空
	 * 7.测试数据源新增，数据源编号不能为空格
	 * 8.测试数据源新增，数据源编号长度不能超过四位
	 * 9.测试保存数据源，部门id不能为空
	 * 10.测试保存数据源，部门id不能为空格
	 */
	@Test
	public void saveDataSource() {
		// 1.测试数据源新增功能,数据都不为空
		String bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + new Random().nextInt(99))
				.addData("datasource_number", "ds" + new Random().nextInt(99))
				.addData("depIds", "-300,-299,-298")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));

		// 2.测试数据源编辑功能
		bodyString = new HttpClient()
				.addData("source_id", -300L)
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + new Random().nextInt(99))
				.addData("datasource_number", "ds" + new Random().nextInt(99))
				.addData("depIds", "-300,-299")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));

		// 3.测试数据源新增，数据源编号重复
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + new Random().nextInt(99))
				.addData("datasource_number", "d300")
				.addData("depIds", "-300")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat((String) ar.getMessage(), is("数据源编号重复,datasource_number=d300"));
		// 4.测试数据源新增，数据源名称不能为空
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "")
				.addData("datasource_number", "ds" + new Random().nextInt(99))
				.addData("depIds", "-300")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat((String) ar.getMessage(), is("数据源名称不能为空以及不能为空格，datasource_name="));

		// 5.测试数据源新增，数据源名称不能为空格
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", " ")
				.addData("datasource_number", "ds" + new Random().nextInt(99))
				.addData("depIds", "-300")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat((String) ar.getMessage(), is("数据源名称不能为空以及不能为空格，" +
				"datasource_name= "));

		// 6.测试数据源新增，数据源编号不能为空
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + new Random().nextInt(99))
				.addData("datasource_number", "")
				.addData("depIds", "-300")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat((String) ar.getMessage(), is("数据源编号不能为空以及不能为空格" +
				"或数据源编号长度不能超过四位，datasource_number="));

		// 7.测试数据源新增，数据源编号不能为空格
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + new Random().nextInt(99))
				.addData("datasource_number", " ")
				.addData("depIds", "-300")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat((String) ar.getMessage(), is("数据源编号不能为空以及不能为空格" +
				"或数据源编号长度不能超过四位，datasource_number= "));

		// 8.测试数据源新增，数据源编号长度不能超过四位
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + new Random().nextInt(99))
				.addData("datasource_number", "ds100")
				.addData("depIds", "-300")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat((String) ar.getMessage(), is("数据源编号不能为空以及不能为空格" +
				"或数据源编号长度不能超过四位，datasource_number=ds100"));

		// 9.保存数据源，部门id不能为空
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + new Random().nextInt(99))
				.addData("datasource_number", "ds" + new Random().nextInt(99))
				.addData("depIds", "")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));

		// 10.测试保存数据源，部门id不能为空格
		bodyString = new HttpClient()
				.addData("datasource_remark", "测试")
				.addData("datasource_name", "cs" + new Random().nextInt(99))
				.addData("datasource_number", "ds" + new Random().nextInt(99))
				.addData("depIds", " ")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		assertThat(ar.getMessage(), is("部门id不能为空格，depIds= "));
	}

	@Test
	public void searchDataSource() {
		long source_id = -299L;
		String bodyString = new HttpClient().addData("source_id", source_id)
				.post(getActionUrl("searchDataSource")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(true));
	}

	@Test
	public void deleteDataSource() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long source_id = -298L;
			// 验证DB里面预期被删除的数据是存在的
			OptionalLong result = SqlOperator.queryNumber(db,
					"select count(1) from " + Data_source.TableName
							+ " where source_id=?", source_id);
			assertThat(result.orElse(Long.MIN_VALUE), is(1L)); // 被删除前为1

			// 业务处理
			String responseValue = new HttpClient()
					.addData("source_id", source_id)
					.post(getActionUrl("deleteDataSource"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObject(responseValue, ActionResult.class);
			assertThat(ar.isSuccess(), is(true));
			assertThat(ar.isSuccess(), is(true));

			// 验证DB里面的数据是否正确
			result = SqlOperator.queryNumber(db,
					"select count(1) from " + Data_source.TableName +
							" where source_id=?", source_id);
			assertThat(result.orElse(Long.MIN_VALUE), is(0L)); // 被删除了所以为0
		}
	}

	@Test
	public void deleteSourceRelationDep() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long source_id = -297L;
			// 验证DB里面预期被删除的数据是存在的
			OptionalLong result = SqlOperator.queryNumber(db,
					"select count(1) from " + Source_relation_dep.TableName +
							" where source_id=?", source_id);
			assertThat(result.orElse(Long.MIN_VALUE), is(1L)); // 被删除前为1

			// 业务处理
			String responseValue = new HttpClient()
					.addData("source_id", source_id)
					.post(getActionUrl("deleteSourceRelationDep"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObject(responseValue, ActionResult.class);
			assertThat(ar.isSuccess(), is(true));

			// 验证DB里面的数据是否正确
			result = SqlOperator.queryNumber(db,
					"select count(1) from " + Source_relation_dep.TableName +
							" where source_id=?", source_id);
			assertThat(result.orElse(Long.MIN_VALUE), is(0L)); // 被删除了所以为0
		}
	}

}
