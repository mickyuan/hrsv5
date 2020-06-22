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
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.testbase.WebBaseTestCase;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "集市信息查询类", author = "TBH", createdate = "2020年5月21日 16点48分")
public class MarketInfoActionTest extends WebBaseTestCase {
	private final Long ThreadId = Thread.currentThread().getId();
	//一个已经存在的用户id
	private static final long USER_ID = MarketConfig.getLong("user_id");
	//上面用户id所对应的密码
	private static final String PASSWORD = MarketConfig.getString("password");
	//请填写测试用户需要做登录验证的A项目的登录验证的接口
	private static final String LOGIN_URL = MarketConfig.getString("login_url");
	//数据表存储关系表ID
	private static final String DSL_ID = MarketConfig.getString("dsl_id");
	//SQL
	private static final String SQL = MarketConfig.getString("sql");
	//COLUMN
	private static final List<String> COLUMN = Arrays.asList(MarketConfig.getString("column").split(","));
	//数据集市ID
	private static final long DATA_MART_ID = PrimayKeyGener.getNextId();
	//数据表id
	private static final long DATATABLE_ID = PrimayKeyGener.getNextId();
	//数据分类表ID
	private static final long CATEGORY_ID = PrimayKeyGener.getNextId();
	//数据操作信息表(SQL表）ID
	private static final long ID = PrimayKeyGener.getNextId();
	//前后置处理表ID
//	private  final long RELID = PrimayKeyGener.getNextId();

	//类型对照ID
	private static final long DTCS_ID = PrimayKeyGener.getNextId();
	//长度对照表ID
	private static final long DLCS_ID = PrimayKeyGener.getNextId();
	//数据字段存储关系表ID
//	private  final long DSLAD_ID = PrimayKeyGener.getNextId();
	//定义全局的dm_info
	private final Dm_info dm_info = newdminfo();
	//定义全局的dm_category
	private final Dm_category dm_category = newdmcategory();
	//定义全局的dm_datatable
	private Dm_datatable dm_datatable = newdmdatatable();
	//定义全局的dm_operation_info
	private final Dm_operation_info dm_operation_info = newdmoperationinfo();
	//定义全局的datatable_field_info
	private final Datatable_field_info datatable_field_info1 = newdatatablefieldinfo("A", "0");
	//定义全局的datatable_field_info
	private final Datatable_field_info datatable_field_info2 = newdatatablefieldinfo("B", "1");
	//定义全局的datatable_field_info
	private final Datatable_field_info datatable_field_info3 = newdatatablefieldinfo("C", "2");
	//定义全局的datatable_field_info
	private final Datatable_field_info datatable_field_info4 = newdatatablefieldinfo("D", "3");
	//定义全局的dtab_relation_store
	private Dtab_relation_store dtab_relation_store = newdtabrelationstore();
	private final Data_store_layer data_store_layer = newdatastorelayer();

	//定义全局的sys_user
//	private Sys_user newsysuser() {
//		Sys_user user = new Sys_user();
//		user.setUser_id(USER_ID);
//		user.setCreate_id(USER_ID);
//		user.setRole_id(USER_ID);
//		user.setUser_name("测试用户(9001)");
//		user.setUser_password("1");
//		user.setUseris_admin(IsFlag.Shi.getCode());
//		user.setUser_state(IsFlag.Shi.getCode());
//		user.setCreate_date(DateUtil.getSysDate());
//		user.setCreate_time(DateUtil.getSysTime());
//		user.setToken("0");
//		user.setValid_time("0");
//		user.setDep_id(DEPT_ID);
//		return user;
//	}

//	//定义全局的department_info
//	private Department_info newdepartmeninfo() {
//		Department_info deptInfo = new Department_info();
//		deptInfo.setDep_id(DEPT_ID);
//		deptInfo.setDep_name("测试系统参数类部门init-zxz");
//		deptInfo.setCreate_date(DateUtil.getSysDate());
//		deptInfo.setCreate_time(DateUtil.getSysTime());
//		deptInfo.setDep_remark("测试系统参数类部门init-zxz");
//		return deptInfo;
//	}

	//定义全局的dm_info
	private Dm_info newdminfo() {
		Dm_info dm_info = new Dm_info();
		dm_info.setData_mart_id(DATA_MART_ID);
		dm_info.setMart_name(ThreadId + "Mart_name");
		dm_info.setMart_number(ThreadId + "Mart_number");
		dm_info.setMart_desc("Mart_desc");
		dm_info.setMart_storage_path("");
		dm_info.setCreate_date(DateUtil.getSysDate());
		dm_info.setCreate_time(DateUtil.getSysTime());
		dm_info.setCreate_id(USER_ID);
		dm_info.setDm_remark("Dm_remark");
		return dm_info;
	}

	//定义全局的dm_category
	private Dm_category newdmcategory() {
		Dm_category dm_category = new Dm_category();
		dm_category.setCategory_id(CATEGORY_ID);
		dm_category.setCategory_name(ThreadId + "Category_name");
		dm_category.setCategory_desc(ThreadId + "Category_desc");
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
	private Dm_datatable newdmdatatable() {
		Dm_datatable dm_datatable = new Dm_datatable();
		dm_datatable.setDatatable_id(DATATABLE_ID);
		dm_datatable.setData_mart_id(DATA_MART_ID);
		dm_datatable.setDatatable_cn_name(ThreadId + "集市表中文名");
		dm_datatable.setDatatable_en_name(ThreadId + "datatable_en_name");
		dm_datatable.setSql_engine(SqlEngine.MOREN.getCode());
		dm_datatable.setDatatable_desc("");
		dm_datatable.setRemark("");
		dm_datatable.setDatatable_create_date(DateUtil.getSysDate());
		dm_datatable.setDatatable_create_time(DateUtil.getSysTime());
		dm_datatable.setDatatable_due_date(Constant.MAXDATE);
		dm_datatable.setDdlc_date(DateUtil.getSysDate());
		dm_datatable.setDdlc_time(DateUtil.getSysTime());
		dm_datatable.setDatac_date(DateUtil.getSysDate());
		dm_datatable.setDatac_time(DateUtil.getSysTime());
		dm_datatable.setDatatable_lifecycle(TableLifeCycle.YongJiu.getCode());
		dm_datatable.setSoruce_size("9999999999.00");
		dm_datatable.setEtl_date(Constant.MAXDATE);
		dm_datatable.setStorage_type(StorageType.ZengLiang.getCode());
		dm_datatable.setTable_storage(TableStorage.ShuJuBiao.getCode());
		dm_datatable.setRepeat_flag(IsFlag.Fou.getCode());
		dm_datatable.setCategory_id(CATEGORY_ID);
		return dm_datatable;
	}

	//定义全局的dm_operation_info
	private Dm_operation_info newdmoperationinfo() {
		Dm_operation_info dm_operation_info = new Dm_operation_info();
		dm_operation_info.setId(ID);
		dm_operation_info.setDatatable_id(DATATABLE_ID);
		dm_operation_info.setExecute_sql("select A,B,C,D from datatable_en_name");
		return dm_operation_info;
	}

	//定义全局的datatable_field_info
	private Datatable_field_info newdatatablefieldinfo(String name, String count) {
		Datatable_field_info datatable_field_info = new Datatable_field_info();
		datatable_field_info.setDatatable_field_id(PrimayKeyGener.getNextId());
		datatable_field_info.setDatatable_id(DATATABLE_ID);
		datatable_field_info.setField_en_name(name);
		datatable_field_info.setField_cn_name(name);
		datatable_field_info.setField_type("varchar");
		datatable_field_info.setField_length("100");
		datatable_field_info.setField_desc("");
		datatable_field_info.setField_process(ProcessType.YingShe.getCode());
		datatable_field_info.setProcess_para(count);
		datatable_field_info.setField_seq(count);
		datatable_field_info.setRemark("");
		return datatable_field_info;
	}

	//定义全局的dtab_relation_store
	private Dtab_relation_store newdtabrelationstore() {
		Dtab_relation_store dtab_relation_store = new Dtab_relation_store();
		dtab_relation_store.setDsl_id(DSL_ID);
		dtab_relation_store.setTab_id(DATATABLE_ID);
		dtab_relation_store.setData_source(StoreLayerDataSource.DM.getCode());
		dtab_relation_store.setIs_successful(JobExecuteState.DengDai.getCode());
		return dtab_relation_store;
	}

	//
	//定义全局的dtab_relation_store
	private Data_store_layer newdatastorelayer() {
		DatabaseWrapper db = null;
		Data_store_layer data_store_layer = new Data_store_layer();
		data_store_layer.setDsl_id(DSL_ID);
		try {
			db = new DatabaseWrapper();
			data_store_layer = SqlOperator.queryOneObject(db, Data_store_layer.class, "select * from " + Data_store_layer.TableName + " where dsl_id = ?",
					data_store_layer.getDsl_id()).orElseThrow(() -> new BusinessException("查询" + Dm_datatable.TableName + "失败"));
			return data_store_layer;
		} catch (Exception e) {
			if (db != null) {
				db.rollback();
			}
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return data_store_layer;
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
	@Before
	public void before() {
		DatabaseWrapper db = null;
		try {
			after();
			db = new DatabaseWrapper();
			//造数据集市工程表数据
			assertThat("初始化数据成功", dm_info.add(db), is(1));
			//造数据分类表信息
			assertThat("初始化数据成功", dm_category.add(db), is(1));
			//造数据表信息
			assertThat("初始化数据成功", dm_datatable.add(db), is(1));
			//初始化数据SQL表
			assertThat("初始化数据成功", dm_operation_info.add(db), is(1));
			//初始化数据表字段信息
			assertThat("初始化数据成功", datatable_field_info1.add(db), is(1));
			//初始化数据表字段信息
			assertThat("初始化数据成功", datatable_field_info2.add(db), is(1));
			//初始化数据表字段信息
			assertThat("初始化数据成功", datatable_field_info3.add(db), is(1));
			//初始化数据表字段信息
			assertThat("初始化数据成功", datatable_field_info4.add(db), is(1));
			//初始化数据表存储关系表
			assertThat("初始化数据成功", dtab_relation_store.add(db), is(1));
			SqlOperator.commitTransaction(db);
			db.close();
			//模拟用户登录
			String responseValue = new HttpClient().buildSession()
					.addData("user_id", USER_ID)
					.addData("password", PASSWORD)
					.post(LOGIN_URL).getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败"));
			assertThat(ar.isSuccess(), is(true));
		} catch (Exception e) {
			if (db != null) {
				db.rollback();
			}
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	@After
	public void after() {
		DatabaseWrapper db = null;
		try {
			db = new DatabaseWrapper();
			checkdeletedata(db, Dm_info.TableName, "data_mart_id", dm_info.getData_mart_id());
			checkdeletedata(db, Dm_category.TableName, "category_id", dm_category.getCategory_id());
			checkdeletedata(db, Dm_datatable.TableName, "datatable_id", dm_datatable.getDatatable_id());
			checkdeletedata(db, Dm_operation_info.TableName, "id", dm_operation_info.getId());
			checkdeletedata(db, Datatable_field_info.TableName, "datatable_id", datatable_field_info1.getDatatable_id());
//			checkdeletedata(db, Data_store_layer.TableName, "dsl_id", DSL_ID);
			checkdeletedata(db, Dtab_relation_store.TableName, "tab_id", dtab_relation_store.getTab_id());
			checkdeletedata(db, Dcol_relation_store.TableName, "col_id", datatable_field_info1.getDatatable_field_id());
			checkdeletedata(db, Dcol_relation_store.TableName, "col_id", datatable_field_info2.getDatatable_field_id());
			checkdeletedata(db, Dcol_relation_store.TableName, "col_id", datatable_field_info3.getDatatable_field_id());
			checkdeletedata(db, Dcol_relation_store.TableName, "col_id", datatable_field_info4.getDatatable_field_id());
			SqlOperator.commitTransaction(db);
			db.close();
		} catch (Exception e) {
			if (db != null) {
				db.rollback();
			}
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	/**
	 * 封装一个方法用于删除数据
	 *
	 * @param db              ff
	 * @param tablename       ff
	 * @param primarykeyname  fff
	 * @param primarykeyvalue fff
	 */
	private void checkdeletedata(DatabaseWrapper db, String tablename, String primarykeyname, Object
			primarykeyvalue) {
		SqlOperator.execute(db,
				"delete from " + tablename + " where " + primarykeyname + "= ?", primarykeyvalue);
		long num = SqlOperator.queryNumber(db,
				"select count(1) from " + tablename + " where " + primarykeyname + " = ?",
				primarykeyvalue
		).orElseThrow(() -> new RuntimeException("count fail!"));
		assertThat(tablename + " 表此条数据删除后,记录数应该为0", num, is(0L));

	}

	//封装一个map转bean的方法
	private <T> T map2bean(Map<String, Object> map, Class<T> classType) {
		return JSON.parseObject(JSON.toJSONString(map), classType);
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
			if (map.get("data_mart_id") == null) {
				List<Object> list = new ArrayList<>();
				map.put("data_mart_id", list);
			}
			if (map.get("mart_name") == null) {
				List<Object> list = new ArrayList<>();
				map.put("mart_name", list);
			}
			map.get("data_mart_id").add(dm_info.getData_mart_id());
			map.get("mart_name").add(dm_info.getMart_name());
		}
		assertThat(map.get("data_mart_id").contains(dm_info.getData_mart_id()), is(true));
		assertThat(map.get("mart_name").contains(dm_info.getMart_name()), is(true));
	}

	@Test
	public void addMarket() {
		//第一遍插入 能成功
		String rightString = new HttpClient()
				.addData("mart_name", ThreadId + "TestMart_name")
				.addData("mart_number", ThreadId + "TestMart_number")
				.post(getActionUrl("addMarket")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		//第二遍插入,应该判断出存在重复的情况，抛出异常
		rightString = new HttpClient()
				.addData("mart_name", ThreadId + "TestMart_name")
				.addData("mart_number", ThreadId + "TestMart_number")
				.post(getActionUrl("addMarket")).getBodyString();
		rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(false));
		DatabaseWrapper db = null;
		try {
			db = new DatabaseWrapper();
			//删除addMarket方法的数据
			checkdeletedata(db, Dm_info.TableName, "mart_name", ThreadId + "TestMart_name");
			SqlOperator.commitTransaction(db);
		} catch (Exception e) {
			if (db != null) {
				db.rollback();
			}
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}

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
		assertThat(dm_info1.equals(dm_info), is(true));
	}

	@Test
	public void queryDMDataTableByDataMartID() {
		String rightString = new HttpClient()
				.addData("data_mart_id", dm_info.getData_mart_id())
				.post(getActionUrl("queryDMDataTableByDataMartID")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		List<Map<String, Object>> maps = JSON.parseObject(rightResult.getData().toString(), new TypeReference<List<Map<String, Object>>>() {
		});
		assertThat(maps.size() == 1, is(true));
		Map<String, Object> stringObjectMap = maps.get(0);
		assertThat(stringObjectMap.get("isadd").equals(true), is(true));
		Dm_datatable dm_datatable1 = map2bean(stringObjectMap, Dm_datatable.class);
		assertThat(dm_datatable1.equals(dm_datatable), is(true));
	}

	@Test
	public void deleteDMDataTable() {
		String rightString = new HttpClient()
				.addData("datatable_id", dm_datatable.getDatatable_id())
				.post(getActionUrl("deleteDMDataTable")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		DatabaseWrapper db = null;
		try {
			after();
			db = new DatabaseWrapper();
			long longnum = querylong(db, Dm_info.TableName, "data_mart_id", dm_info.getData_mart_id());
			assertThat(longnum == 0L, is(true));
			longnum = querylong(db, Dm_datatable.TableName, "datatable_id", dm_datatable.getDatatable_id());
			assertThat(longnum == 0L, is(true));
			longnum = querylong(db, Dm_operation_info.TableName, "id", dm_operation_info.getId());
			assertThat(longnum == 0L, is(true));
			longnum = querylong(db, Dtab_relation_store.TableName, "datatable_id", dm_datatable.getDatatable_id());
			assertThat(longnum == 0L, is(true));
			longnum = querylong(db, Datatable_field_info.TableName, "datatable_id", dm_datatable.getDatatable_id());
			assertThat(longnum == 0L, is(true));
			longnum = querylong(db, Dcol_relation_store.TableName, "col_id", datatable_field_info1.getDatatable_field_id());
			assertThat(longnum == 0L, is(true));
			longnum = querylong(db, Dcol_relation_store.TableName, "col_id", datatable_field_info2.getDatatable_field_id());
			assertThat(longnum == 0L, is(true));
			longnum = querylong(db, Dcol_relation_store.TableName, "col_id", datatable_field_info3.getDatatable_field_id());
			assertThat(longnum == 0L, is(true));
			longnum = querylong(db, Dcol_relation_store.TableName, "col_id", datatable_field_info4.getDatatable_field_id());
			assertThat(longnum == 0L, is(true));
			//TODO 缺失三张血缘和前后置处理表的检测
		} catch (Exception e) {
			if (db != null) {
				db.rollback();
			}
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
			//将删除后的数据补充回来
			before();
		}
	}

	//封装一个查询count的方法
	private long querylong(DatabaseWrapper db, String tablename, String key, Object value) {
		return SqlOperator.queryNumber(db, "select count(*) from " + tablename + " where " + key + " = ?", value)
				.orElseThrow(() -> new BusinessException("查询" + Dm_datatable.TableName + "失败"));
	}

	@Test
	public void searchDataStore() {
		String rightString = new HttpClient()
				.post(getActionUrl("searchDataStore")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		List<Data_store_layer> data_store_layers = JSON.parseObject(rightResult.getData().toString(), new TypeReference<List<Data_store_layer>>() {
		});
		assertThat(data_store_layers.contains(data_store_layer), is(true));
	}

	@Test
	public void searchDataStoreByFuzzyQuery() {
		String rightString = new HttpClient()
				.addData("fuzzyqueryitem", "ORACLE")
				.post(getActionUrl("searchDataStoreByFuzzyQuery")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		List<Data_store_layer> data_store_layers = JSON.parseObject(rightResult.getData().toString(), new TypeReference<List<Data_store_layer>>() {
		});
		assertThat(data_store_layers.contains(data_store_layer), is(true));
	}

	@Test
	public void addDMDataTable() {
		DatabaseWrapper db = null;
		try {
			db = new DatabaseWrapper();
			dm_datatable.delete(db);
			dtab_relation_store.delete(db);
			SqlOperator.commitTransaction(db);
			String rightString = new HttpClient()
					.addData("dm_datatable", dm_datatable)
					.addData("dsl_id", DSL_ID)
					.post(getActionUrl("addDMDataTable")).getBodyString();
			ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败!"));
			assertThat(rightResult.isSuccess(), is(true));
			Map<String, Object> stringObjectMap = JSON.parseObject(rightResult.getData().toString(), new TypeReference<Map<String, Object>>() {
			});
			//检测dm_datatable
			dm_datatable.setDatatable_id(stringObjectMap.get("datatable_id").toString());
			Dm_datatable dm_datatable2 = SqlOperator.queryOneObject(db, Dm_datatable.class,
					"select * from " + Dm_datatable.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id())
					.orElseThrow(() -> new BusinessException("查询" + Dm_datatable.TableName + "失败"));
			assertThat(dm_datatable2.getDatatable_en_name().equals(dm_datatable.getDatatable_en_name()), is(true));
			assertThat(dm_datatable2.getDatatable_cn_name().equals(dm_datatable.getDatatable_cn_name()), is(true));
			assertThat(dm_datatable2.getDatatable_lifecycle().equals(dm_datatable.getDatatable_lifecycle()), is(true));
			assertThat(dm_datatable2.getRepeat_flag().equals(dm_datatable.getRepeat_flag()), is(true));
			assertThat(dm_datatable2.getSql_engine().equals(dm_datatable.getSql_engine()), is(true));
			assertThat(dm_datatable2.getStorage_type().equals(dm_datatable.getStorage_type()), is(true));
			assertThat(dm_datatable2.getTable_storage().equals(dm_datatable.getTable_storage()), is(true));
			assertThat(dm_datatable2.getDatatable_id().equals(dm_datatable.getDatatable_id()), is(true));
			//检测dtab_relation_store
			dtab_relation_store.setTab_id(stringObjectMap.get("datatable_id").toString());
			Dtab_relation_store dtab_relation_store1 = SqlOperator.queryOneObject(db, Dtab_relation_store.class,
					"select * from " + Dtab_relation_store.TableName + " where tab_id = ?", dtab_relation_store.getTab_id())
					.orElseThrow(() -> new BusinessException("查询" + Dm_datatable.TableName + "失败"));
			assertThat(dtab_relation_store1.equals(dtab_relation_store), is(true));
			SqlOperator.commitTransaction(db);
		} catch (Exception e) {
			if (db != null) {
				db.rollback();
			}
			e.printStackTrace();
		} finally {
			if (db == null) {
				db.close();
			}
		}
	}

	@Test
	public void getAllDatatable_En_Name() {
		String rightString = new HttpClient()
				.post(getActionUrl("getAllDatatable_En_Name")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		List<Dm_datatable> dm_datatables = JSON.parseObject(rightResult.getData().toString(), new TypeReference<List<Dm_datatable>>() {
		});
		List<String> getDatatable_en_namelist = new ArrayList<>();
		for (Dm_datatable dm_datatable2 : dm_datatables) {
			getDatatable_en_namelist.add(String.valueOf(dm_datatable2.getDatatable_en_name()));
		}
		assertThat(getDatatable_en_namelist.contains(String.valueOf(dm_datatable.getDatatable_en_name())), is(true));
	}

	@Test
	public void getTableIdFromSameNameTableId() {
		DatabaseWrapper db = null;
		try {
			db = new DatabaseWrapper();
			Dm_datatable dm_datatable1 = new Dm_datatable();
			BeanUtils.copyProperties(dm_datatable1, dm_datatable);
			dm_datatable1.setDatatable_id(PrimayKeyGener.getNextId());
			dm_datatable1.add(db);
			SqlOperator.commitTransaction(db);
			String rightString = new HttpClient()
					.addData("datatable_id", dm_datatable1.getDatatable_id())
					.post(getActionUrl("getTableIdFromSameNameTableId")).getBodyString();
			ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败!"));
			assertThat(rightResult.isSuccess(), is(true));
			List<Dm_datatable> dm_datatables = JSON.parseObject(rightResult.getData().toString(), new TypeReference<List<Dm_datatable>>() {
			});
			List<String> datatableidlist = new ArrayList<>();
			for (Dm_datatable dm_datatable2 : dm_datatables) {
				datatableidlist.add(String.valueOf(dm_datatable2.getDatatable_id()));
			}
			assertThat(datatableidlist.contains(String.valueOf(dm_datatable.getDatatable_id())), is(true));
			dm_datatable1.delete(db);
			SqlOperator.commitTransaction(db);
		} catch (Exception e) {
			if (db != null) {
				db.rollback();
			}
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	@Test
	public void updateDMDataTable() {
		dm_datatable.setDatatable_en_name(dm_datatable.getDatatable_en_name() + "_testupdate");
		dm_datatable.setDatatable_cn_name(dm_datatable.getDatatable_cn_name() + "_testupdate");
		String rightString = new HttpClient()
				.addData("dm_datatable", dm_datatable)
				.addData("dsl_id", DSL_ID)
				.post(getActionUrl("updateDMDataTable")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Map<String, Object> stringObjectMap = JSON.parseObject(rightResult.getData().toString(), new TypeReference<Map<String, Object>>() {
		});
		assertThat(stringObjectMap.containsKey("datatable_id"), is(true));
		assertThat(stringObjectMap.get("datatable_id").toString().equals(dm_datatable.getDatatable_id().toString()), is(true));
		DatabaseWrapper db = null;
		try {
			db = new DatabaseWrapper();
			Dm_datatable dm_datatable2 = SqlOperator.queryOneObject(db, Dm_datatable.class, "select * from dm_datatable where datatable_id = ?", dm_datatable.getDatatable_id())
					.orElseThrow(() -> new BusinessException("连接失败"));
			assertThat(dm_datatable2.equals(dm_datatable), is(true));
			//还原数据
			dm_datatable.delete(db);
			dm_datatable = newdmdatatable();
			SqlOperator.commitTransaction(db);
		} catch (Exception e) {
			if (db != null) {
				db.rollback();
			}
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	@Test
	public void queryDMDataTableByDataTableId() {
		String rightString = new HttpClient()
				.addData("datatable_id", dm_datatable.getDatatable_id())
				.post(getActionUrl("queryDMDataTableByDataTableId")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		List<Map<String, Object>> maps = JSON.parseObject(rightResult.getData().toString(), new TypeReference<List<Map<String, Object>>>() {
		});
		assertThat(maps.size() == 1, is(true));
		Map<String, Object> stringObjectMap = maps.get(0);
		Dm_datatable dm_datatable1 = map2bean(stringObjectMap, Dm_datatable.class);
		assertThat(dm_datatable1.equals(dm_datatable), is(true));
		Dtab_relation_store dtab_relation_store1 = map2bean(stringObjectMap, Dtab_relation_store.class);
		assertThat(dtab_relation_store1.equals(dtab_relation_store), is(true));
	}


	@Test
	public void queryTableNameIfRepeat() {
		//有id为编辑其他表
		String rightString = new HttpClient()
				.addData("datatable_id", PrimayKeyGener.getNextId())
				.addData("datatable_en_name", dm_datatable.getDatatable_en_name())
				.post(getActionUrl("queryTableNameIfRepeat")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Map<String, Object> stringObjectMap = JSON.parseObject(rightResult.getData().toString(), new TypeReference<Map<String, Object>>() {
		});
		assertThat(stringObjectMap.containsKey("result"), is(true));
		assertThat(stringObjectMap.get("result").equals(true), is(true));

		//编辑表但是表名不同
		rightString = new HttpClient()
				.addData("datatable_id", PrimayKeyGener.getNextId())
				.addData("datatable_en_name", dm_datatable.getDatatable_en_name() + "_absolutenotrepeattablename")
				.post(getActionUrl("queryTableNameIfRepeat")).getBodyString();
		rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		stringObjectMap = JSON.parseObject(rightResult.getData().toString(), new TypeReference<Map<String, Object>>() {
		});
		assertThat(stringObjectMap.containsKey("result"), is(true));
		assertThat(stringObjectMap.get("result").equals(false), is(true));

		//新增表
		rightString = new HttpClient()
				.addData("datatable_en_name", dm_datatable.getDatatable_en_name())
				.post(getActionUrl("queryTableNameIfRepeat")).getBodyString();
		rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		stringObjectMap = JSON.parseObject(rightResult.getData().toString(), new TypeReference<Map<String, Object>>() {
		});
		assertThat(stringObjectMap.containsKey("result"), is(true));
		assertThat(stringObjectMap.get("result").equals(true), is(true));

		//新增表，但是表名不同
		rightString = new HttpClient()
				.addData("datatable_en_name", dm_datatable.getDatatable_en_name() + "_absolutenotrepeattablename")
				.post(getActionUrl("queryTableNameIfRepeat")).getBodyString();
		rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		stringObjectMap = JSON.parseObject(rightResult.getData().toString(), new TypeReference<Map<String, Object>>() {
		});
		assertThat(stringObjectMap.containsKey("result"), is(true));
		assertThat(stringObjectMap.get("result").equals(false), is(true));
	}

	@Test
	public void getDataBySQL() {

		String rightString = new HttpClient()
				.addData("querysql", SQL)
				.post(getActionUrl("getDataBySQL")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		List<Map<String, Object>> maps = JSON.parseObject(rightResult.getData().toString(), new TypeReference<List<Map<String, Object>>>() {
		});
		assertThat(maps.size() == 10, is(true));
		//不允许使用*
		String tmpsql = "select * from LQCS_lqcs_tpcds_promotion";
		rightString = new HttpClient()
				.addData("querysql", tmpsql)
				.post(getActionUrl("getDataBySQL")).getBodyString();
		rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(false));
		//不允许没有别名
		tmpsql = "select count(*) from LQCS_lqcs_tpcds_promotion";
		rightString = new HttpClient()
				.addData("querysql", tmpsql)
				.post(getActionUrl("getDataBySQL")).getBodyString();
		rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(false));
	}


	@Test
	public void getColumnMore() {
		//有id为编辑其他表
		String rightString = new HttpClient()
				.addData("datatable_id", dm_datatable.getDatatable_id())
				.post(getActionUrl("getColumnMore")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		List<Map<String, Object>> maps = JSON.parseObject(rightResult.getData().toString(), new TypeReference<List<Map<String, Object>>>() {
		});
		List<String> dsla_storelayers = new ArrayList<>();
		for (Map<String, Object> map : maps) {
			assertThat(map.containsKey("dslad_id"), is(true));
			assertThat(map.containsKey("dsla_storelayer"), is(true));
			dsla_storelayers.add(map.get("dsla_storelayer").toString());
		}
		assertThat(dsla_storelayers.contains(StoreLayerAdded.ZhuJian.getCode()), is(true));
	}

	@Test
	public void getColumnBySql() {
		//有id为编辑其他表
		String rightString = new HttpClient()
				.addData("querysql", SQL)
				.addData("datatable_id", dm_datatable.getDatatable_id())
				.post(getActionUrl("getColumnBySql")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Map<String, Object> stringObjectMap = JSON.parseObject(rightResult.getData().toString(), new TypeReference<Map<String, Object>>() {
		});
		assertThat(stringObjectMap.containsKey("columnlist"), is(true));
		List<Map<String, Object>> columnlist = (List<Map<String, Object>>) stringObjectMap.get("columnlist");
		assertThat(columnlist.size() == COLUMN.size(), is(true));
		for (int i = 0; i < columnlist.size(); i++) {
			Map<String, Object> stringObjectMap1 = columnlist.get(i);
			assertThat(stringObjectMap1.get("value").toString().equalsIgnoreCase(COLUMN.get(i)),is(true));
		}
	}

	@Test
	public void getColumnFromDatabase() {
		//有id为编辑其他表
		String rightString = new HttpClient()
				.addData("datatable_id", dm_datatable.getDatatable_id())
				.post(getActionUrl("getColumnFromDatabase")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
	}

}
