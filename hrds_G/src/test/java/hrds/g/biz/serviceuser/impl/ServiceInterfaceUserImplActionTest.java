package hrds.g.biz.serviceuser.impl;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
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
import hrds.commons.utils.StorageTypeKey;
import hrds.g.biz.bean.SingleTable;
import hrds.g.biz.enumerate.DataType;
import hrds.g.biz.enumerate.OutType;
import hrds.g.biz.enumerate.StateType;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "服务接口测试类", author = "dhw", createdate = "2020/4/20 16:12")
public class ServiceInterfaceUserImplActionTest extends WebBaseTestCase {

	// 已经存在的用户ID,用于模拟登录
	private static final long USER_ID = agentInitConfig.getLong("user_id");
	private static final String PASSWORD = agentInitConfig.getString("password");
	// 已经存在的agent id
	private static final long AgentId = agentInitConfig.getLong("agent_id");
	// 已经存在的dep id
	private static final long DEP_ID = agentInitConfig.getLong("dep_id");
	//当前线程的id
	private long THREAD_ID = Thread.currentThread().getId() * 1000000;

	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.初始化数据存储登记表信息
			Data_store_reg dst = new Data_store_reg();
			dst.setFile_id(String.valueOf(THREAD_ID));
			dst.setCollect_type(AgentType.DBWenJian.getCode());
			dst.setOriginal_update_date(DateUtil.getSysDate());
			dst.setOriginal_update_time(DateUtil.getSysTime());
			dst.setOriginal_name("customer");
			dst.setTable_name("customer");
			dst.setHyren_name("customer");
			dst.setStorage_date(DateUtil.getSysDate());
			dst.setStorage_time(DateUtil.getSysTime());
			dst.setFile_size(20L);
			dst.setAgent_id(AgentId);
			dst.setSource_id(THREAD_ID);
			dst.setDatabase_id(THREAD_ID);
			dst.setTable_id(THREAD_ID);
			assertThat("初始化数据存储登记表信息", dst.add(db), is(1));
			// 2.初始化表存储信息信息测试数据
			Table_storage_info tsi = new Table_storage_info();
			for (int i = 0; i < 3; i++) {
				tsi.setHyren_name("sys_user");
				tsi.setStorage_id(THREAD_ID + i);
				tsi.setFile_format(FileFormat.FeiDingChang.getCode());
				tsi.setStorage_type(StorageType.ZengLiang.getCode());
				tsi.setIs_zipper(IsFlag.Shi.getCode());
				tsi.setStorage_time("0");
				tsi.setTable_id(THREAD_ID + i);
				assertThat("初始化表存储信息信息测试数据", tsi.add(db), is(1));
			}
			// 3.初始化数据表存储关系表测试数据
			Dtab_relation_store drt = new Dtab_relation_store();
			for (int i = 0; i < 3; i++) {
				drt.setData_source(StoreLayerDataSource.DB.getCode());
				drt.setTab_id(THREAD_ID + i);
				drt.setDsl_id(THREAD_ID + i);
				assertThat("初始化数据表存储关系表测试数据", drt.add(db), is(1));
			}
			// 4.初始化数据存储层配置表测试数据
			Data_store_layer dsl = new Data_store_layer();
			for (int i = 0; i < 3; i++) {
				dsl.setDsl_id(THREAD_ID + i);
				dsl.setDsl_name("test_dhw");
				dsl.setStore_type(Store_type.DATABASE.getCode());
				dsl.setIs_hadoopclient(IsFlag.Fou.getCode());
				assertThat("初始化数据存储层配置表测试数据", dsl.add(db), is(1));
			}
			// 5.初始化数据存储层配置属性表测试数据
			Data_store_layer_attr dsla = new Data_store_layer_attr();
			for (int i = 0; i < 6; i++) {
				dsla.setDsla_id(THREAD_ID + i);
				if (i == 0) {
					dsla.setStorage_property_key(StorageTypeKey.database_type);
					dsla.setStorage_property_val(DatabaseType.Postgresql.getCode());
				} else if (i == 1) {
					dsla.setStorage_property_key(StorageTypeKey.database_driver);
					dsla.setStorage_property_val("org.postgresql.Driver");
				} else if (i == 2) {
					dsla.setStorage_property_key(StorageTypeKey.user_name);
					dsla.setStorage_property_val("hrsdxg");
				} else if (i == 3) {
					dsla.setStorage_property_key(StorageTypeKey.database_pwd);
					dsla.setStorage_property_val("hrsdxg");
				} else if (i == 4) {
					dsla.setStorage_property_key(StorageTypeKey.jdbc_url);
					dsla.setStorage_property_val("jdbc:postgresql://10.71.4.57:31001/tpcds");
				} else {
					dsla.setStorage_property_key(StorageTypeKey.database_name);
					dsla.setStorage_property_val("hrsdxg");
				}
				dsla.setIs_file(IsFlag.Fou.getCode());
				dsla.setDsl_id(THREAD_ID);
				assertThat("初始化数据存储层配置属性表测试数据", dsla.add(db), is(1));
			}
			for (int i = 0; i < 2; i++) {
				Sys_user sys_user = new Sys_user();
				sys_user.setUser_email("123@qq.com");
				sys_user.setUser_id(THREAD_ID + i);
				sys_user.setUser_name("dhw" + THREAD_ID + i);
				sys_user.setUser_password(PASSWORD);
				sys_user.setRole_id("1001");
				sys_user.setCreate_id(USER_ID);
				sys_user.setUser_state(UserState.ZhengChang.getCode());
				if (i == 0) {
					sys_user.setUser_type(UserType.RESTYongHu.getCode());
					sys_user.setUsertype_group(UserType.CaijiGuanLiYuan.getCode() + "," + UserType.RESTYongHu.getCode());
				} else {
					sys_user.setUser_type(UserType.CaijiGuanLiYuan.getCode());
					sys_user.setUsertype_group(UserType.CaijiGuanLiYuan.getCode() + "," + UserType.ZuoYeGuanLiYuan.getCode());
				}
				sys_user.setDep_id(DEP_ID);
				sys_user.setCreate_date(DateUtil.getSysDate());
				sys_user.setCreate_time(DateUtil.getSysTime());
				assertThat("初始化系统用户表测试数据", sys_user.add(db), is(1));
			}
			// 2.造interface_use表测试数据
			for (int i = 0; i < 3; i++) {
				Interface_use interface_use = new Interface_use();
				for (int j = 0; j < 7; j++) {
					if (j == 0) {
						interface_use.setTheir_type(InterfaceType.ShuJuLei.getCode());
						interface_use.setInterface_code("01-123");
						interface_use.setUrl("tableUsePermissions");
						interface_use.setInterface_name("表使用权限查询接口");
					} else if (j == 1) {
						interface_use.setTheir_type(InterfaceType.ShuJuLei.getCode());
						interface_use.setInterface_code("01-124");
						interface_use.setUrl("generalQuery");
						interface_use.setInterface_name("单表普通查询接口");
					} else if (j == 2) {
						interface_use.setTheir_type(InterfaceType.ShuJuLei.getCode());
						interface_use.setInterface_code("01-130");
						interface_use.setUrl("tableStructureQuery");
						interface_use.setInterface_name("表结构查询接口");
					} else if (j == 3) {
						interface_use.setTheir_type(InterfaceType.ShuJuLei.getCode());
						interface_use.setInterface_code("01-133");
						interface_use.setUrl("fileAttributeSearch");
						interface_use.setInterface_name("文件属性搜索接口");
					} else if (j == 4) {
						interface_use.setTheir_type(InterfaceType.ShuJuLei.getCode());
						interface_use.setInterface_code("01-134");
						interface_use.setUrl("sqlInterfaceSearch");
						interface_use.setInterface_name("sql查询接口");
					} else if (j == 5) {
						interface_use.setTheir_type(InterfaceType.ShuJuLei.getCode());
						interface_use.setInterface_code("01-137");
						interface_use.setUrl("rowKeySearch");
						interface_use.setInterface_name("rowkey查询");
					} else {
						interface_use.setTheir_type(InterfaceType.GongNengLei.getCode());
						interface_use.setInterface_code("01-143");
						interface_use.setUrl("uuidDownload");
						interface_use.setInterface_name("UUID数据下载");
					}
				}
				interface_use.setInterface_use_id(THREAD_ID + i);
				if (i == 0) {
					interface_use.setUser_id(USER_ID);
					interface_use.setUse_state(InterfaceState.QiYong.getCode());
				} else if (i == 1) {
					// 接口状态为禁用
					interface_use.setUser_id(THREAD_ID);
					interface_use.setUse_state(InterfaceState.JinYong.getCode());
				} else {
					// 不是接口用户
					interface_use.setUser_id(THREAD_ID + 1);
					interface_use.setUse_state(InterfaceState.QiYong.getCode());
				}
				interface_use.setUse_valid_date(Constant.MAXDATE);
				interface_use.setClassify_name("dhw" + THREAD_ID);
				interface_use.setInterface_id(104L);
				interface_use.setInterface_note("接口监控测试");
				interface_use.setCreate_id(USER_ID);
				interface_use.setStart_use_date(DateUtil.getSysDate());
				interface_use.setUser_name("接口测试用户-dhw0");
				assertThat("初始化数据存储层配置属性表测试数据", interface_use.add(db), is(1));
			}
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
	}

	@Method(desc = "获取token值测试方法", logicStep = "1.正确的数据访问1，数据有效" +
			"2.错误的数据访问1,该用户非接口用户" +
			"3.错误的数据访问2,账号或密码错误")
	@Test
	public void getToken() {
		// 1.正确的数据访问1，数据有效
		String bodyString = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("user_password", PASSWORD)
				.post(getActionUrl("getToken")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("token"), is(notNullValue()));
		// 2.错误的数据访问1,该用户非接口用户
		bodyString = new HttpClient().buildSession()
				.addData("user_id", THREAD_ID)
				.addData("user_password", PASSWORD)
				.post(getActionUrl("getToken")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("token"), is(nullValue()));
		assertThat(dataForMap.get("message"), is(StateType.NOT_REST_USER.getValue()));
		// 3.错误的数据访问2,账号或密码错误
		bodyString = new HttpClient().buildSession()
				.addData("user_id", "2001")
				.addData("user_password", "aaaa")
				.post(getActionUrl("getToken")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("token"), is(nullValue()));
		assertThat(dataForMap.get("message"), is(StateType.UNAUTHORIZED.getValue()));
	}

	private String getToken(long user_id, String password) {
		String bodyString = new HttpClient().buildSession()
				.addData("user_id", user_id)
				.addData("user_password", password)
				.post(getActionUrl("getToken"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("token"), is(notNullValue()));
		return dataForMap.get("token").toString();
	}

	@Method(desc = "获取token值测试方法", logicStep = "1.正确的数据访问1，token值存在" +
			"2.正确的数据访问2,token值不存在" +
			"3.错误的数据访问1,token值错误" +
			"4.错误的数据访问2，接口状态为禁用" +
			"5.错误的数据访问3，user_password错误" +
			"6.错误的数据访问4，不为接口用户" +
			"7.错误的数据访问5，没有接口使用权限" +
			"8.错误的数据访问6，url不存在")
	@Test
	public void tableUsePermissions() {
		String token = getToken(USER_ID, PASSWORD);
		// 1.正确的数据访问1，token值存在
		String bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "tableUsePermissions")
				.post(getActionUrl("tableUsePermissions"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("status"), is(StateType.NORMAL.getCode()));
		// 2.正确的数据访问2,token值不存在
		bodyString = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("user_password", PASSWORD)
				.addData("url", "tableUsePermissions")
				.post(getActionUrl("tableUsePermissions"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("status"), is(StateType.NORMAL.getCode()));
		// 3.错误的数据访问1,token值错误
		bodyString = new HttpClient().buildSession()
				.addData("token", "1242424242")
				.addData("url", "tableUsePermissions")
				.post(getActionUrl("tableUsePermissions"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("message"), is(StateType.TOKEN_ERROR.getValue()));
		// 4.错误的数据访问2，接口状态为禁用
		bodyString = new HttpClient().buildSession()
				.addData("user_id", THREAD_ID)
				.addData("user_password", PASSWORD)
				.addData("url", "tableUsePermissions")
				.post(getActionUrl("tableUsePermissions"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("message"), is(StateType.INTERFACE_STATE.getValue()));
		// 5.错误的数据访问3，user_password错误
		bodyString = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("user_password", "0000")
				.addData("url", "tableUsePermissions")
				.post(getActionUrl("tableUsePermissions"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("message"), is(StateType.UNAUTHORIZED.getValue()));
		// 6.错误的数据访问4，不为接口用户
		bodyString = new HttpClient().buildSession()
				.addData("user_id", THREAD_ID + 1)
				.addData("user_password", PASSWORD)
				.addData("url", "tableUsePermissions")
				.post(getActionUrl("tableUsePermissions"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("message"), is(StateType.NOT_REST_USER.getValue()));
		// 7.错误的数据访问5，没有接口使用权限
		bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "tableUsePermission")
				.post(getActionUrl("tableUsePermissions"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("message"), is(StateType.NO_PERMISSIONS.getValue()));
		// 8.错误的数据访问6，url不存在
		bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.post(getActionUrl("tableUsePermissions"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("message"), is(StateType.URL_NOT_EXIST.getValue()));
	}

	@Method(desc = "单表普通查询测试", logicStep = "1.正确的数据访问1，")
	@Test
	public void generalQuery() {
		String token = getToken(USER_ID, PASSWORD);
		SingleTable singleTable = new SingleTable();
		singleTable.setTableName("customer");
		singleTable.setDataType(DataType.json.getCode());
		singleTable.setOutType(OutType.STREAM.getCode());
		// 1.正确的数据访问1，token值存在
		String bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "generalQuery")
				.addData("singleTable", singleTable)
				.post(getActionUrl("generalQuery"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("status"), is(StateType.NORMAL.getCode()));
	}

	@Test
	public void tableStructureQuery() {
	}

	@Test
	public void fileAttributeSearch() {
	}

	@Test
	public void sqlInterfaceSearch() {
	}

	@Test
	public void rowKeySearch() {
	}

	@Test
	public void uuidDownload() {
	}

	@After
	public void after() {
		DatabaseWrapper db = new DatabaseWrapper();
		try {
			//1.清理Data_store_reg表中造的数据
			for (int i = 0; i < 3; i++) {
				SqlOperator.execute(db,
						"delete from " + Data_store_reg.TableName + " where file_id=?",
						String.valueOf(THREAD_ID + i));
			}
			//2.清理Table_storage_info表中造的数据
			for (int i = 0; i < 3; i++) {
				SqlOperator.execute(db,
						"delete from " + Table_storage_info.TableName + " where storage_id=?",
						THREAD_ID + i);
			}
			//3.清理Data_store_reg表中造的数据
			for (int i = 0; i < 3; i++) {
				SqlOperator.execute(db,
						"delete from " + Dtab_relation_store.TableName + " where tab_id=?",
						THREAD_ID + i);
			}
			//4.清理Data_store_layer表中造的数据
			for (int i = 0; i < 3; i++) {
				SqlOperator.execute(db,
						"delete from " + Data_store_layer.TableName + " where dsl_id=?",
						THREAD_ID + i);
			}
			//5.清理Data_store_layer_attr表中造的数据
			for (int i = 0; i < 6; i++) {
				SqlOperator.execute(db,
						"delete from " + Data_store_layer_attr.TableName + " where dsla_id=?",
						THREAD_ID + i);
			}
			// 6.清理interface_use表测试数据
			for (int i = 0; i < 3; i++) {
				SqlOperator.execute(db,
						"delete from " + Interface_use.TableName + " where interface_use_id=?",
						THREAD_ID + i);
			}
			for (int i = 0; i < 2; i++) {
				SqlOperator.execute(db,
						"delete from " + Sys_user.TableName + " where user_id=?",
						THREAD_ID + i);
			}
			SqlOperator.commitTransaction(db);
		} catch (Exception e) {
			db.rollback();
		} finally {
			db.close();
		}
	}
}
