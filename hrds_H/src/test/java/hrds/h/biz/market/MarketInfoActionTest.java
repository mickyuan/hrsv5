package hrds.h.biz.market;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.entity.fdentity.ProjectTableEntity;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.main.AppMain;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "集市信息查询类", author = "TBH", createdate = "2020年5月21日 16点48分")
/**
 * author:TBH
 * Time:2020.4.10
 */
public class MarketInfoActionTest extends WebBaseTestCase {
	//用户id
	private static final long USER_ID = 9001L;
	//部门ID
	private static final long DEPT_ID = 9002L;
	//数据集市ID
	private static final long DATA_MART_ID = 1000000000L;
	//数据表id
	private static final long DATATABLE_ID = 1200000000L;
	//数据分类表ID
	private static final long CATEGORY_ID = 1200000000L;
	//数据操作信息表(SQL表）ID
	private static final long ID = 1300000000L;
	//前后置处理表ID
	private static final long RELID = 1400000000L;
	//数据字段id（递增）
	private static final long DATATABLE_FIELD_ID = 1400000000L;


	/**
	 * 造sys_user数据用于登录
	 * 造department_info数据用于登录
	 * 造dm_info数据
	 * 造dm_datatable数据
	 * 造dm_category数据
	 * 造dm_operation_info数据
	 * 造datatable_field_info数据
	 * 造dtab_relation_store数据
	 * 造dcol_relation_store数据
	 * 造dm_relevant_info数据
	 */
	@Before
	public void beforeTest() {
		DatabaseWrapper db = null;
		try {
			db = new DatabaseWrapper();
			//造sys_user表数据，用于模拟用户登录
			Sys_user user = new Sys_user();
			user.setUser_id(USER_ID);
			user.setCreate_id(USER_ID);
			user.setRole_id(USER_ID);
			user.setUser_name("测试用户(9001)");
			user.setUser_password("1");
			user.setUseris_admin(IsFlag.Shi.getCode());
			user.setUser_state(IsFlag.Shi.getCode());
			user.setCreate_date(DateUtil.getSysDate());
			user.setCreate_time(DateUtil.getSysTime());
			user.setToken("0");
			user.setValid_time("0");
			user.setDep_id(DEPT_ID);
			assertThat("初始化数据成功", user.add(db), is(1));
			//造部门表数据，用于模拟用户登录
			Department_info deptInfo = new Department_info();
			deptInfo.setDep_id(DEPT_ID);
			deptInfo.setDep_name("测试系统参数类部门init-zxz");
			deptInfo.setCreate_date(DateUtil.getSysDate());
			deptInfo.setCreate_time(DateUtil.getSysTime());
			deptInfo.setDep_remark("测试系统参数类部门init-zxz");
			assertThat("初始化数据成功", deptInfo.add(db), is(1));
			//造数据集市工程表数据
			Dm_info dm_info = new Dm_info();
			dm_info.setData_mart_id(DATA_MART_ID);
			dm_info.setMart_name("Mart_name");
			dm_info.setMart_number("Mart_number");
			dm_info.setMart_desc("Mart_desc");
			dm_info.setMart_storage_path("");
			dm_info.setCreate_date(DateUtil.getSysDate());
			dm_info.setCreate_time(DateUtil.getSysTime());
			dm_info.setCreate_id(USER_ID);
			assertThat("初始化数据成功", dm_info.add(db), is(1));
			//造数据分类表信息
			Dm_category dm_category = new Dm_category();
			dm_category.setCategory_id(CATEGORY_ID);
			dm_category.setCategory_name("Category_name");
			dm_category.setCategory_desc("Category_desc");
			dm_category.setCreate_date(DateUtil.getSysDate());
			dm_category.setCreate_time(DateUtil.getSysTime());
			dm_category.setCategory_seq("1");
			dm_category.setCategory_num("Category_num");
			dm_category.setCreate_id(USER_ID);
			dm_category.setParent_category_id(CATEGORY_ID);
			dm_category.setData_mart_id(DATA_MART_ID);
			assertThat("初始化数据成功", dm_category.add(db), is(1));

			SqlOperator.commitTransaction(db);
			db.close();
			//模拟用户登录
			String responseValue = new HttpClient().buildSession()
					.addData("user_id", USER_ID)
					.addData("password", "1")
					.post("http://10.71.4.57:8888/A/action/hrds/a/biz/login/login").getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败"));
			assertThat(ar.isSuccess(), is(true));
		} catch (Exception e) {
			if (db != null) {
				db.rollback();
			}
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	@AfterClass
	public static void after() {
		DatabaseWrapper db = null;
		try {
			db = new DatabaseWrapper();
			//删除sys_user表
			checkdeletedata(db, Sys_user.TableName, "user_id", USER_ID);
			checkdeletedata(db, Department_info.TableName, "dep_id", DEPT_ID);
			checkdeletedata(db, Dm_info.TableName, "data_mart_id", DATA_MART_ID);
			checkdeletedata(db, Dm_category.TableName, "category_id", CATEGORY_ID);
			checkdeletedata(db, Dm_datatable.TableName, "datatable_id", DATATABLE_ID);
			checkdeletedata(db, Dm_operation_info.TableName, "id", ID);
			checkdeletedata(db, Datatable_field_info.TableName, "datatable_field_id", DATATABLE_FIELD_ID);
			SqlOperator.commitTransaction(db);
			db.close();
		} catch (Exception e) {
			if (db != null) {
				db.rollback();
			}
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	/**
	 * 封装一个方法用于删除数据
	 *
	 * @param db
	 * @param tablename
	 * @param primarykeyname
	 * @param primarykeyvalue
	 */
	private static void checkdeletedata(DatabaseWrapper db, String tablename, String primarykeyname, Long
			primarykeyvalue) {
		SqlOperator.execute(db,
				"delete from " + tablename + " where " + primarykeyname + "= ?", primarykeyvalue);
		long num = SqlOperator.queryNumber(db,
				"select count(1) from " + tablename + " where " + primarykeyname + " = ?",
				primarykeyvalue
		).orElseThrow(() -> new RuntimeException("count fail!"));
		assertThat(tablename + " 表此条数据删除后,记录数应该为0", num, is(0L));

	}

	@Test
	public void login() {
	}


}
