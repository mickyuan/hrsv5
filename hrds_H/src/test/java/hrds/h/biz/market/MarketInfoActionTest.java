package hrds.h.biz.market;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.StorageType;
import hrds.commons.codes.TableLifeCycle;
import hrds.commons.codes.TableStorage;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;

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
	//定义全局的sys_user
	private static final Sys_user sys_user = newsysuser();
	//定义全局的department_info
	private static final Department_info department_info = newdepartmeninfo();
	//定义全局的dm_info
	private static final Dm_info dm_info = newdminfo();
	//定义全局的dm_category
	private static final Dm_category dm_category = newdmcategory();
	//定义全局的dm_datatable
	private static final Dm_datatable dm_datatable = newdmdatatable();
	//定义全局的dm_operation_info
	private static final Dm_operation_info dm_operation_info = newdmoperationinfo();

	//定义全局的sys_user
	private static Sys_user newsysuser() {
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
		return user;
	}

	//定义全局的department_info
	private static Department_info newdepartmeninfo() {
		Department_info deptInfo = new Department_info();
		deptInfo.setDep_id(DEPT_ID);
		deptInfo.setDep_name("测试系统参数类部门init-zxz");
		deptInfo.setCreate_date(DateUtil.getSysDate());
		deptInfo.setCreate_time(DateUtil.getSysTime());
		deptInfo.setDep_remark("测试系统参数类部门init-zxz");
		return deptInfo;
	}

	//定义全局的dm_info
	private static Dm_info newdminfo() {
		Dm_info dm_info = new Dm_info();
		dm_info.setData_mart_id(DATA_MART_ID);
		dm_info.setMart_name("Mart_name");
		dm_info.setMart_number("Mart_number");
		dm_info.setMart_desc("Mart_desc");
		dm_info.setMart_storage_path("");
		dm_info.setCreate_date(DateUtil.getSysDate());
		dm_info.setCreate_time(DateUtil.getSysTime());
		dm_info.setCreate_id(USER_ID);
		dm_info.setDm_remark("Dm_remark");
		return dm_info;
	}

	//定义全局的dm_category
	private static Dm_category newdmcategory() {
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
		return dm_category;
	}

	//定义全局的dm_datatable
	private static Dm_datatable newdmdatatable() {
		Dm_datatable dm_datatable = new Dm_datatable();
		dm_datatable.setDatatable_id(DATATABLE_ID);
		dm_datatable.setData_mart_id(DATA_MART_ID);
		dm_datatable.setDatatable_cn_name("集市表中文名");
		dm_datatable.setDatatable_en_name("datatable_en_name");
		dm_datatable.setDatatable_create_date(DateUtil.getSysDate());
		dm_datatable.setDatatable_create_time(DateUtil.getSysTime());
		dm_datatable.setDatatable_due_date(Constant.MAXDATE);
		dm_datatable.setDdlc_date(DateUtil.getSysDate());
		dm_datatable.setDdlc_time(DateUtil.getSysTime());
		dm_datatable.setDatac_date(DateUtil.getSysDate());
		dm_datatable.setDatac_time(DateUtil.getSysTime());
		dm_datatable.setDatatable_lifecycle(TableLifeCycle.YongJiu.getCode());
		dm_datatable.setSoruce_size("9999999999");
		dm_datatable.setEtl_date(Constant.MAXDATE);
		dm_datatable.setStorage_type(StorageType.ZengLiang.getCode());
		dm_datatable.setTable_storage(TableStorage.ShuJuBiao.getCode());
		dm_datatable.setRepeat_flag(IsFlag.Fou.getCode());
		dm_datatable.setCategory_id(CATEGORY_ID);
		return dm_datatable;
	}

	//定义全局的dm_operation_info
	private static Dm_operation_info newdmoperationinfo() {
		Dm_operation_info dm_operation_info = new Dm_operation_info();
		dm_operation_info.setId(ID);
		dm_operation_info.setDatatable_id(DATATABLE_ID);
		dm_operation_info.setExecute_sql("select A,B,C,D from datatable_en_name");
		return dm_operation_info;
	}

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
	@BeforeClass
	public static void before() {
		DatabaseWrapper db = null;
		try {
			after();
			db = new DatabaseWrapper();
			//造sys_user表数据，用于模拟用户登录
			assertThat("初始化数据成功", sys_user.add(db), is(1));
			//造部门表数据，用于模拟用户登录
			assertThat("初始化数据成功", department_info.add(db), is(1));
			//造数据集市工程表数据
			assertThat("初始化数据成功", dm_info.add(db), is(1));
			//造数据分类表信息
			assertThat("初始化数据成功", dm_category.add(db), is(1));
			//造数据表信息
			assertThat("初始化数据成功", dm_datatable.add(db), is(1));
			//初始化数据SQL表
			assertThat("初始化数据成功", dm_operation_info.add(db), is(1));
			SqlOperator.commitTransaction(db);
			db.close();
			//模拟用户登录
			String responseValue = new HttpClient().buildSession()
					.addData("user_id", USER_ID)
					.addData("password", "1")
					.post("http://127.0.0.1:8888/A/action/hrds/a/biz/login/login").getBodyString();
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
			checkdeletedata(db, Datatable_field_info.TableName, "datatable_id", DATATABLE_ID);
			//删除addMarket方法的数据
			checkdeletedata(db, Dm_info.TableName, "mart_name", "TestMart_name");
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
	private static void checkdeletedata(DatabaseWrapper db, String tablename, String primarykeyname, Object
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
	public void getAllDslInMart() {
		String rightString = new HttpClient()
				.post(getActionUrl("getAllDslInMart")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		//TODO 需要造数据
	}

	@Test
	public void getTableTop5InDsl() {
		String rightString = new HttpClient()
				.post(getActionUrl("getTableTop5InDsl")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		//TODO 需要造数据
	}

	@Test
	public void getMarketInfo() {
		String rightString = new HttpClient()
				.post(getActionUrl("getMarketInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		List<Dm_info> dm_infos = JSON.parseObject(rightResult.getData().toString(), new TypeReference<List<Dm_info>>() {
		});
		Map<String, List<Object>> map = new HashMap<>();
		for (int i = 0; i < dm_infos.size(); i++) {
			Dm_info dm_info = dm_infos.get(i);
			if (map.get("data_mart_id") == null) {
				List<Object> list = new ArrayList<>();
				map.put("data_mart_id", list);
			}
			if (map.get("mart_name") == null) {
				List<Object> list = new ArrayList<>();
				map.put("mart_name", list);
			}
			map.get("data_mart_id").add(MarketInfoActionTest.dm_info.getData_mart_id());
			map.get("mart_name").add(MarketInfoActionTest.dm_info.getMart_name());
		}
		assertThat(map.get("data_mart_id").contains(dm_info.getData_mart_id()), is(true));
		assertThat(map.get("mart_name").contains(dm_info.getMart_name()), is(true));
	}

	@Test
	public void addMarket() {
		//第一遍插入 能成功
		String rightString = new HttpClient()
				.addData("mart_name", "TestMart_name")
				.addData("mart_number", "TestMart_number")
				.post(getActionUrl("addMarket")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		//第一遍插入,应该判断出存在重复的情况，抛出异常
		rightString = new HttpClient()
				.addData("mart_name", "TestMart_name")
				.addData("mart_number", "TestMart_number")
				.post(getActionUrl("addMarket")).getBodyString();
		rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(false));
	}

	@Test
	public void getdminfo() {
		String rightString = new HttpClient()
				.addData("data_mart_id", dm_info.getData_mart_id())
				.post(getActionUrl("getdminfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Dm_info dm_info1 = JSON.parseObject(rightResult.getData().toString(), new TypeReference<Dm_info>() {
		});
		assertThat(dm_info1.equals(dm_info),is(true));
	}

	@Test
	public void queryDMDataTableByDataMartID() {
		String rightString = new HttpClient()
				.addData("data_mart_id", dm_info.getData_mart_id())
				.post(getActionUrl("queryDMDataTableByDataMartID")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
//		Dm_info dm_info1 = JSON.parseObject(rightResult.getData().toString(), new TypeReference<Dm_info>() {
//		});
//		assertThat(dm_info1.equals(dm_info),is(true));
	}

}
