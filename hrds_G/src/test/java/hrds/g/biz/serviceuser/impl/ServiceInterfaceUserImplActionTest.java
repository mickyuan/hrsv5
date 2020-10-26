package hrds.g.biz.serviceuser.impl;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.yaml.YamlArray;
import fd.ng.core.yaml.YamlMap;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.ParallerTestUtil;
import hrds.commons.utils.StorageTypeKey;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.g.biz.enumerate.AsynType;
import hrds.g.biz.enumerate.DataType;
import hrds.g.biz.enumerate.OutType;
import hrds.g.biz.enumerate.StateType;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "服务接口测试类", author = "dhw", createdate = "2020/4/20 16:12")
public class ServiceInterfaceUserImplActionTest extends WebBaseTestCase {

	// 已经存在的用户ID,用于模拟登录
	private static final long SYS_USER_ID = ParallerTestUtil.TESTINITCONFIG.getLong("user_id");
	private static final String PASSWORD = ParallerTestUtil.TESTINITCONFIG.getString("password");
	// 已经存在的agent id
	private static final long AgentId = ParallerTestUtil.TESTINITCONFIG.getLong("agent_id");
	// 已经存在的dep id
	private static final long DEP_ID = ParallerTestUtil.TESTINITCONFIG.getLong("dep_id");
	private static final YamlArray TEST_DATABASES = ParallerTestUtil.TESTINITCONFIG.getArray("test_databases");
	// 已经存在的表名
	private final String TABLE_NAME = "customer";
	// 要查询列
	private final String SELECTCOLUMN = "c_customer_id,c_customer_sk,c_current_cdemo_sk,c_current_hdemo_sk,c_current_addr_sk";

	private final long nextId = PrimayKeyGener.getNextId();
	//当前线程的id
	private final long THREAD_ID = nextId - Thread.currentThread().getId() * 1000000;
	private long USER_ID = SYS_USER_ID + THREAD_ID;

	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 初始化数据存储登记表信息
			Data_store_reg dst = getData_store_reg();
			assertThat("初始化数据存储登记表信息", dst.add(db), is(1));
			// 初始化表存储信息测试数据
			Table_storage_info tsi = getTable_storage_info();
			assertThat("初始化表存储信息测试数据", tsi.add(db), is(1));
			// 初始化数据表存储关系表测试数据
			List<Dtab_relation_store> dtabRelationStores = getDtab_relation_stores();
			dtabRelationStores.forEach(dtab_relation_store ->
					assertThat("初始化数据表存储关系表测试数据", dtab_relation_store.add(db), is(1))
			);
			// 初始化数据存储层配置表测试数据
			List<Data_store_layer> storeLayers = getData_store_layers();
			storeLayers.forEach(data_store_layer ->
					assertThat("初始化数据存储层配置表测试数据", data_store_layer.add(db), is(1))
			);
			// 初始化数据存储层配置属性表测试数据
			List<Data_store_layer_attr> dataStoreLayerAttrs = getData_store_layer_attrs();
			dataStoreLayerAttrs.forEach(data_store_layer_attr ->
					assertThat("初始化数据存储层配置属性表测试数据", data_store_layer_attr.add(db), is(1))
			);
			// 初始化系统用户表测试数据
			List<Sys_user> sys_users = getSys_users();
			sys_users.forEach(sys_user ->
					assertThat("初始化系统用户表测试数据", sys_user.add(db), is(1)));
			// 造interface_use表测试数据
			List<Interface_info> interfaceInfoList = getInterfaceInfoList(db);
			List<Interface_use> interfaceUses = getInterface_uses(interfaceInfoList);
			interfaceUses.forEach(interface_use ->
					assertThat("初始化interface_use表测试数据", interface_use.add(db), is(1)));
			// 初始化table_use_info表测试数据
			Table_use_info table_use_info = getTable_use_info();
			assertThat("初始化table_use_info表测试数据", table_use_info.add(db), is(1));
			// 初始化table_use_info表测试数据
			List<Sysreg_parameter_info> parameterInfos = getSysreg_parameter_infos();
			parameterInfos.forEach(sysreg_parameter_info ->
					assertThat("初始化Sysreg_parameter_info表测试数据", sysreg_parameter_info.add(db), is(1))
			);
			// 初始化table_info表信息
			Table_info table_info = getTable_info();
			assertThat("初始化Sysreg_parameter_info表测试数据", table_info.add(db), is(1));
			// 初始化table_column表信息
			List<Table_column> columnList = getTable_columns(table_info);
			columnList.forEach(table_column ->
					assertThat("初始化Sysreg_parameter_info表测试数据", table_column.add(db), is(1))
			);
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
	}

	private List<Table_column> getTable_columns(Table_info table_info) {
		List<Table_column> columnList = new ArrayList<>();
		for (int i = 0; i < 19; i++) {
			Table_column table_column = new Table_column();
			table_column.setColumn_id(PrimayKeyGener.getNextId());
			table_column.setTable_id(table_info.getTable_id());
			table_column.setIs_primary_key(IsFlag.Fou.getCode());
			table_column.setValid_s_date(DateUtil.getSysDate());
			table_column.setValid_e_date(Constant.MAXDATE);
			table_column.setIs_new(IsFlag.Fou.getCode());
			table_column.setIs_alive(IsFlag.Shi.getCode());
			table_column.setIs_get(IsFlag.Shi.getCode());
			if (i == 0) {
				table_column.setColumn_name("c_customer_sk");
				table_column.setColumn_ch_name("c_customer_sk");
				table_column.setIs_primary_key(IsFlag.Shi.getCode());
				table_column.setColumn_type("number");
			} else if (i == 1) {
				table_column.setColumn_name("c_last_name");
				table_column.setColumn_ch_name("c_last_name");
				table_column.setColumn_type("varchar(30)");
			} else if (i == 2) {
				table_column.setColumn_name("c_customer_id");
				table_column.setColumn_ch_name("c_customer_id");
				table_column.setColumn_type("number");
			} else if (i == 3) {
				table_column.setColumn_name("c_current_cdemo_sk");
				table_column.setColumn_ch_name("c_current_cdemo_sk");
				table_column.setColumn_type("number");
			} else if (i == 4) {
				table_column.setColumn_name("c_current_hdemo_sk");
				table_column.setColumn_ch_name("c_current_hdemo_sk");
				table_column.setColumn_type("number");
			} else if (i == 5) {
				table_column.setColumn_name("c_current_addr_sk");
				table_column.setColumn_ch_name("c_current_addr_sk");
				table_column.setColumn_type("number");
			} else if (i == 6) {
				table_column.setColumn_name("c_first_shipto_date_sk");
				table_column.setColumn_ch_name("c_first_shipto_date_sk");
				table_column.setColumn_type("number");
			} else if (i == 7) {
				table_column.setColumn_name("c_first_sales_date_sk");
				table_column.setColumn_ch_name("c_first_sales_date_sk");
				table_column.setColumn_type("number");
			} else if (i == 8) {
				table_column.setColumn_name("c_salutation");
				table_column.setColumn_ch_name("c_salutation");
				table_column.setColumn_type("varchar(10)");
			} else if (i == 9) {
				table_column.setColumn_name("c_first_name");
				table_column.setColumn_ch_name("c_first_name");
				table_column.setColumn_type("varchar(20)");
			} else if (i == 10) {
				table_column.setColumn_name("c_preferred_cust_flag");
				table_column.setColumn_ch_name("c_preferred_cust_flag");
				table_column.setColumn_type("varchar(1)");
			} else if (i == 11) {
				table_column.setColumn_name("c_birth_day");
				table_column.setColumn_ch_name("c_birth_day");
				table_column.setColumn_type("number");
			} else if (i == 12) {
				table_column.setColumn_name("c_birth_month");
				table_column.setColumn_ch_name("c_birth_month");
				table_column.setColumn_type("number");
			} else if (i == 13) {
				table_column.setColumn_name("c_birth_year");
				table_column.setColumn_ch_name("c_birth_year");
				table_column.setColumn_type("number");
			} else if (i == 14) {
				table_column.setColumn_name("c_birth_country");
				table_column.setColumn_ch_name("c_birth_country");
				table_column.setColumn_type("varchar(20)");
			} else if (i == 15) {
				table_column.setColumn_name("c_login");
				table_column.setColumn_ch_name("c_login");
				table_column.setColumn_type("varchar(13)");
			} else if (i == 16) {
				table_column.setColumn_name("c_email_address");
				table_column.setColumn_ch_name("c_email_address");
				table_column.setColumn_type("varchar(50)");
			} else if (i == 17) {
				table_column.setColumn_name("c_last_review_date");
				table_column.setColumn_ch_name("c_last_review_date");
				table_column.setColumn_type("varchar(10)");
			} else {
				table_column.setColumn_name("remark");
				table_column.setColumn_ch_name("remark");
				table_column.setColumn_type("varchar(100)");
			}
			columnList.add(table_column);
		}
		return columnList;
	}

	private Table_info getTable_info() {
		Table_info table_info = new Table_info();
		table_info.setTable_id(THREAD_ID);
		table_info.setTable_name(TABLE_NAME);
		table_info.setTable_ch_name(TABLE_NAME);
		table_info.setRec_num_date(DateUtil.getSysDate());
		table_info.setDatabase_id(THREAD_ID);
		table_info.setValid_s_date(DateUtil.getSysDate());
		table_info.setValid_e_date(Constant.MAXDATE);
		table_info.setIs_md5(IsFlag.Shi.getCode());
		table_info.setIs_register(IsFlag.Fou.getCode());
		table_info.setIs_customize_sql(IsFlag.Fou.getCode());
		table_info.setIs_parallel(IsFlag.Fou.getCode());
		table_info.setIs_user_defined(IsFlag.Fou.getCode());
		table_info.setUnload_type(UnloadType.QuanLiangXieShu.getCode());
		return table_info;
	}

	private List<Sysreg_parameter_info> getSysreg_parameter_infos() {
		List<Sysreg_parameter_info> parameterInfos = new ArrayList<>();
		for (String column : StringUtil.split(SELECTCOLUMN, ",")) {
			Sysreg_parameter_info sysreg_parameter_info = new Sysreg_parameter_info();
			sysreg_parameter_info.setParameter_id(PrimayKeyGener.getNextId());
			sysreg_parameter_info.setUser_id(USER_ID);
			sysreg_parameter_info.setTable_en_column(column);
			sysreg_parameter_info.setTable_ch_column(column);
			sysreg_parameter_info.setIs_flag(IsFlag.Fou.getCode());
			sysreg_parameter_info.setUse_id(THREAD_ID);
			parameterInfos.add(sysreg_parameter_info);
		}
		return parameterInfos;
	}

	private Table_use_info getTable_use_info() {
		Table_use_info table_use_info = new Table_use_info();
		table_use_info.setUse_id(THREAD_ID);
		table_use_info.setUser_id(USER_ID);
		table_use_info.setSysreg_name("customer");
		table_use_info.setOriginal_name("customer");
		table_use_info.setTable_blsystem(DataSourceType.DCL.getCode());
		return table_use_info;
	}

	private List<Interface_info> getInterfaceInfoList(DatabaseWrapper db) {
		return SqlOperator.queryList(db, Interface_info.class,
				"select * from " + Interface_info.TableName);
	}

	private List<Interface_use> getInterface_uses(List<Interface_info> interfaceInfoList) {
		List<Interface_use> interfaceUses = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			for (Interface_info interface_info : interfaceInfoList) {
				Interface_use interface_use = new Interface_use();
				interface_use.setInterface_use_id(PrimayKeyGener.getNextId());
				interface_use.setInterface_id(interface_info.getInterface_id());
				interface_use.setTheir_type(interface_info.getInterface_type());
				interface_use.setInterface_code(interface_info.getInterface_code());
				interface_use.setUrl(interface_info.getUrl());
				interface_use.setInterface_name(interface_info.getInterface_name());
				if (i == 0) {
					// 接口状态为启用
					interface_use.setUser_id(USER_ID);
					interface_use.setUser_name("全功能操作员");
					interface_use.setCreate_id(THREAD_ID);
					interface_use.setUse_state(InterfaceState.QiYong.getCode());
				} else if (i == 1) {
					// 接口状态为禁用
					interface_use.setUser_id(THREAD_ID);
					interface_use.setUser_name("dhw" + THREAD_ID + 0);
					interface_use.setCreate_id(THREAD_ID);
					interface_use.setUse_state(InterfaceState.JinYong.getCode());
				} else {
					// 不是接口用户
					interface_use.setUser_id(THREAD_ID + 1);
					interface_use.setUser_name("dhw" + THREAD_ID + 1);
					interface_use.setCreate_id(THREAD_ID);
					interface_use.setUse_state(InterfaceState.QiYong.getCode());
				}
				interface_use.setUse_valid_date(Constant.MAXDATE);
				interface_use.setClassify_name("dhwcs");
				interface_use.setInterface_note("接口监控测试");
				interface_use.setStart_use_date(DateUtil.getSysDate());
				interfaceUses.add(interface_use);
			}
		}
		return interfaceUses;
	}

	private List<Sys_user> getSys_users() {
		List<Sys_user> sysUsers = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Sys_user sys_user = new Sys_user();
			sys_user.setUser_id(THREAD_ID + i);
			sys_user.setUser_email("123@qq.com");
			sys_user.setUser_name("dhw" + THREAD_ID + i);
			sys_user.setUser_password(PASSWORD);
			sys_user.setRole_id("1001");
			sys_user.setCreate_id("1000");
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
			sysUsers.add(sys_user);
		}
		return sysUsers;
	}

	private List<Data_store_layer_attr> getData_store_layer_attrs() {
		List<Data_store_layer_attr> storeLayerAttrs = new ArrayList<>();
		for (int i = 0; i < TEST_DATABASES.size(); i++) {
			YamlMap test_database = TEST_DATABASES.getMap(i);
			//存储层设置id
			long DSL_ID = test_database.getLong("dsl_id");
			//设置数据库类型
			Data_store_layer_attr dsla = new Data_store_layer_attr();
			dsla.setDsla_id(PrimayKeyGener.getNextId());
			dsla.setStorage_property_key(StorageTypeKey.database_type);
			dsla.setStorage_property_val(test_database.getString(StorageTypeKey.database_code));
			dsla.setIs_file(IsFlag.Fou.getCode());
			dsla.setDsl_id(DSL_ID);
			storeLayerAttrs.add(dsla);
			//设置驱动名称
			dsla = new Data_store_layer_attr();
			dsla.setDsla_id(PrimayKeyGener.getNextId());
			dsla.setStorage_property_key(StorageTypeKey.database_driver);
			dsla.setStorage_property_val(test_database.getString(StorageTypeKey.database_driver));
			dsla.setIs_file(IsFlag.Fou.getCode());
			dsla.setDsl_id(DSL_ID);
			storeLayerAttrs.add(dsla);
			//设置URL
			dsla = new Data_store_layer_attr();
			dsla.setDsla_id(PrimayKeyGener.getNextId());
			dsla.setStorage_property_key(StorageTypeKey.jdbc_url);
			dsla.setStorage_property_val(test_database.getString(StorageTypeKey.jdbc_url));
			dsla.setIs_file(IsFlag.Fou.getCode());
			dsla.setDsl_id(DSL_ID);
			storeLayerAttrs.add(dsla);
			//设置用户名
			dsla = new Data_store_layer_attr();
			dsla.setDsla_id(PrimayKeyGener.getNextId());
			dsla.setStorage_property_key(StorageTypeKey.user_name);
			dsla.setStorage_property_val(test_database.getString(StorageTypeKey.user_name));
			dsla.setIs_file(IsFlag.Fou.getCode());
			dsla.setDsl_id(DSL_ID);
			storeLayerAttrs.add(dsla);
			//设置用户密码
			dsla = new Data_store_layer_attr();
			dsla.setDsla_id(PrimayKeyGener.getNextId());
			dsla.setStorage_property_key(StorageTypeKey.database_pwd);
			dsla.setStorage_property_val(test_database.getString(StorageTypeKey.database_pwd));
			dsla.setIs_file(IsFlag.Fou.getCode());
			dsla.setDsl_id(DSL_ID);
			storeLayerAttrs.add(dsla);
			//设置数据库名称
			dsla = new Data_store_layer_attr();
			dsla.setDsla_id(PrimayKeyGener.getNextId());
			dsla.setStorage_property_key(StorageTypeKey.database_name);
			dsla.setStorage_property_val(test_database.getString(StorageTypeKey.database_name));
			dsla.setIs_file(IsFlag.Fou.getCode());
			dsla.setDsl_id(DSL_ID);
			storeLayerAttrs.add(dsla);
		}
		return storeLayerAttrs;
	}

	private List<Data_store_layer> getData_store_layers() {
		List<Data_store_layer> storeLayers = new ArrayList<>();
		for (int i = 0; i < TEST_DATABASES.size(); i++) {
			YamlMap test_database = TEST_DATABASES.getMap(i);
			//存储层设置id
			long dsl_id = test_database.getLong("dsl_id");
			Data_store_layer data_store_layer = new Data_store_layer();
			data_store_layer.setDsl_id(dsl_id);
			data_store_layer.setDsl_name("test_dhw" + THREAD_ID);
			data_store_layer.setStore_type(Store_type.DATABASE.getCode());
			data_store_layer.setIs_hadoopclient(IsFlag.Fou.getCode());
			storeLayers.add(data_store_layer);
		}
		return storeLayers;
	}

	private List<Dtab_relation_store> getDtab_relation_stores() {
		List<Dtab_relation_store> dtab_relation_stores = new ArrayList<>();
		for (int i = 0; i < TEST_DATABASES.size(); i++) {
			YamlMap test_database = TEST_DATABASES.getMap(i);
			//存储层设置id
			long dsl_id = test_database.getLong("dsl_id");
			Dtab_relation_store drt = new Dtab_relation_store();
			drt.setData_source(StoreLayerDataSource.DB.getCode());
			drt.setTab_id(THREAD_ID + i);
			drt.setDsl_id(dsl_id);
			dtab_relation_stores.add(drt);
		}
		return dtab_relation_stores;
	}

	private Table_storage_info getTable_storage_info() {
		Table_storage_info tsi = new Table_storage_info();
		tsi.setHyren_name(TABLE_NAME);
		tsi.setStorage_id(THREAD_ID);
		tsi.setFile_format(FileFormat.FeiDingChang.getCode());
		tsi.setStorage_type(StorageType.QuanLiang.getCode());
		tsi.setIs_zipper(IsFlag.Shi.getCode());
		tsi.setStorage_time("0");
		tsi.setTable_id(THREAD_ID);
		return tsi;
	}

	private Data_store_reg getData_store_reg() {
		Data_store_reg dst = new Data_store_reg();
		dst.setFile_id(String.valueOf(THREAD_ID));
		dst.setCollect_type(AgentType.DBWenJian.getCode());
		dst.setOriginal_update_date(DateUtil.getSysDate());
		dst.setOriginal_update_time(DateUtil.getSysTime());
		dst.setOriginal_name(TABLE_NAME);
		dst.setTable_name(TABLE_NAME);
		dst.setHyren_name(TABLE_NAME);
		dst.setStorage_date(DateUtil.getSysDate());
		dst.setStorage_time(DateUtil.getSysTime());
		dst.setFile_size(20L);
		dst.setAgent_id(AgentId);
		dst.setSource_id(THREAD_ID);
		dst.setDatabase_id(THREAD_ID);
		dst.setTable_id(THREAD_ID);
		return dst;
	}

	@Method(desc = "获取token值测试方法", logicStep = "1.正确的数据访问1，数据有效" +
			"2.错误的数据访问1,该用户非接口用户" +
			"3.错误的数据访问2,密码错误")
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
				.addData("user_id", THREAD_ID + 1)
				.addData("user_password", PASSWORD)
				.post(getActionUrl("getToken")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("token"), is(nullValue()));
		assertThat(dataForMap.get("message"), is(StateType.NOT_REST_USER.getValue()));
		// 3.错误的数据访问2,密码错误
		bodyString = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("user_password", "aaaaa")
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
			"2.正确的数据访问2,token值存在,接口状态启用" +
			"3.错误的数据访问1,token值不存在,使用user_id,password方式调用接口" +
			"4.错误的数据访问2，接口状态为禁用" +
			"5.错误的数据访问3，user_password错误" +
			"6.错误的数据访问4，不为接口用户" +
			"7.错误的数据访问5，没有接口使用权限" +
			"8.错误的数据访问6，url不存在")
	@Test
	public void tableUsePermissions() {
		String token = getToken(USER_ID, PASSWORD);
		// 1.正确的数据访问1，token值存在,接口状态启用
		String bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "tableUsePermissions")
				.post(getActionUrl("tableUsePermissions"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("status"), is(StateType.NORMAL.getCode()));
		// 2.正确的数据访问2,token值不存在,使用user_id,password方式调用接口
		bodyString = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("user_password", PASSWORD)
				.addData("url", "tableUsePermissions")
				.post(getActionUrl("tableUsePermissions"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("status"), is(StateType.NORMAL.getCode()));
		// 3.错误的数据访问1,token值错误
		bodyString = new HttpClient().buildSession()
				.addData("token", "1242424242")
				.addData("url", "tableUsePermissions")
				.post(getActionUrl("tableUsePermissions"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("message"), is(StateType.TOKEN_ERROR.getValue()));
		token = getToken(THREAD_ID, PASSWORD);
		// 4.错误的数据访问2，接口状态为禁用
		bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "tableUsePermissions")
				.post(getActionUrl("tableUsePermissions"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("message"), is(StateType.INTERFACE_STATE_ERROR.getValue()));
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
		assertThat(ar.getDataForMap().get("message"), is(StateType.UNAUTHORIZED.getValue()));
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
		assertThat(ar.getDataForMap().get("message"), is(StateType.NOT_REST_USER.getValue()));
		// 7.错误的数据访问5，没有接口使用权限
		bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "tableUsePermission")
				.post(getActionUrl("tableUsePermissions"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("message"), is(StateType.NO_USR_PERMISSIONS.getValue()));
		// 8.错误的数据访问6，url不存在
		bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.post(getActionUrl("tableUsePermissions"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("message"), is(StateType.URL_NOT_EXIST.getValue()));
	}

	@Method(desc = "单表普通查询测试", logicStep = "1.正确的数据访问1，使用token查询" +
			"2.正确的数据访问2，使用user_id,password查询" +
			"3.正确的数据访问3，返回固定数量" +
			"4.正确的数据访问4，根据条件查询选择列" +
			"5.正确的数据访问5，异步轮询" +
			"6.错误的数据访问1，tableName没有表使用权限" +
			"7.错误的数据访问2，tableName不存在" +
			"8.错误的数据访问3，dataType不存在" +
			"9.错误的数据访问4，outType不存在" +
			"10.正确的数据访问5，asynType不存在" +
			"11.正确的数据访问6，filepath为空" +
			"12.正确的数据访问7，filename为空")
	@Test
	public void generalQuery() {
		String token = getToken(USER_ID, PASSWORD);
		// 1.正确的数据访问1，使用token查询
		String bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "generalQuery")
				.addData("tableName", TABLE_NAME)
				.addData("dataType", DataType.json.getCode())
				.addData("outType", OutType.STREAM.getCode())
				.post(getActionUrl("generalQuery"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("status"), is(StateType.NORMAL.getCode()));
		List<Object> list = JsonUtil.toObject(ar.getDataForMap().get("message").toString(),
				new TypeReference<List<Object>>() {
				}.getType());
		assertThat("默认返回10条", list.size(), is(10));
		// 2.正确的数据访问2，使用user_id,password查询
		bodyString = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("user_password", PASSWORD)
				.addData("url", "generalQuery")
				.addData("tableName", TABLE_NAME)
				.addData("dataType", DataType.json.getCode())
				.addData("outType", OutType.STREAM.getCode())
				.post(getActionUrl("generalQuery"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("status"), is(StateType.NORMAL.getCode()));
		list = JsonUtil.toObject(ar.getDataForMap().get("message").toString(),
				new TypeReference<List<Object>>() {
				}.getType());
		assertThat("默认返回10条", list.size(), is(10));
		// 3.正确的数据访问3，返回固定数量
		bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "generalQuery")
				.addData("tableName", TABLE_NAME)
				.addData("num", 20)
				.addData("dataType", DataType.json.getCode())
				.addData("outType", OutType.STREAM.getCode())
				.post(getActionUrl("generalQuery"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("status"), is(StateType.NORMAL.getCode()));
		list = JsonUtil.toObject(ar.getDataForMap().get("message").toString(),
				new TypeReference<List<Object>>() {
				}.getType());
		assertThat("默认返回20条", list.size(), is(20));
		// 4.正确的数据访问4，根据条件查询选择列
		bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "generalQuery")
				.addData("tableName", TABLE_NAME)
				.addData("selectColumn", SELECTCOLUMN)
				.addData("whereColumn", "c_customer_sk=1")
				.addData("dataType", DataType.json.getCode())
				.addData("outType", OutType.STREAM.getCode())
				.post(getActionUrl("generalQuery"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("status"), is(StateType.NORMAL.getCode()));
		list = JsonUtil.toObject(ar.getDataForMap().get("message").toString(),
				new TypeReference<List<Object>>() {
				}.getType());
		assertThat(list.size(), is(1));
		// 5.正确的数据访问5，异步轮询
		File download = FileUtil.getFile("src/test/java/download");
		String filename = TABLE_NAME + THREAD_ID;
		bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "generalQuery")
				.addData("tableName", TABLE_NAME)
				.addData("asynType", AsynType.ASYNPOLLING.getCode())
				.addData("filename", filename)
				.addData("filepath", download.getAbsolutePath())
				.addData("dataType", DataType.json.getCode())
				.addData("outType", OutType.FILE.getCode())
				.post(getActionUrl("generalQuery"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("status"), is(StateType.NORMAL.getCode()));
		assertThat(FileUtil.getFile("src/test/java/download/" + filename).exists(), is(true));
		// 6.错误的数据访问1，tableName没有表使用权限
		bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "generalQuery")
				.addData("tableName", "aaa")
				.addData("dataType", DataType.json.getCode())
				.addData("outType", OutType.STREAM.getCode())
				.post(getActionUrl("generalQuery"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("message"), is(StateType.NO_USR_PERMISSIONS.getValue()));
		// 7.错误的数据访问2，tableName不存在
		bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "generalQuery")
				.addData("tableName", "")
				.addData("dataType", DataType.json.getCode())
				.addData("outType", OutType.STREAM.getCode())
				.post(getActionUrl("generalQuery"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("message"), is(StateType.TABLE_NOT_EXISTENT.getValue()));
		// 8.错误的数据访问3，dataType不存在
		bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "generalQuery")
				.addData("tableName", TABLE_NAME)
				.addData("dataType", "a")
				.addData("outType", OutType.STREAM.getCode())
				.post(getActionUrl("generalQuery"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("message"), is(StateType.DATA_TYPE_ERROR.getValue()));
		// 9.错误的数据访问4，outType不存在
		bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "generalQuery")
				.addData("tableName", TABLE_NAME)
				.addData("dataType", DataType.json.getCode())
				.addData("outType", "a")
				.post(getActionUrl("generalQuery"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("message"), is(StateType.OUT_TYPE_ERROR.getValue()));
		// 10.正确的数据访问5，asynType不存在
		bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "generalQuery")
				.addData("tableName", TABLE_NAME)
				.addData("asynType", "5")
				.addData("filename", filename)
				.addData("filepath", download.getAbsolutePath())
				.addData("dataType", DataType.json.getCode())
				.addData("outType", OutType.FILE.getCode())
				.post(getActionUrl("generalQuery"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("message"), is(StateType.ASYNTYPE_ERROR.getValue()));
		// 11.正确的数据访问6，filepath为空
		bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "generalQuery")
				.addData("tableName", TABLE_NAME)
				.addData("asynType", AsynType.ASYNPOLLING.getCode())
				.addData("filename", filename)
				.addData("filepath", "")
				.addData("dataType", DataType.json.getCode())
				.addData("outType", OutType.FILE.getCode())
				.post(getActionUrl("generalQuery"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("message"), is(StateType.FILEPARH_ERROR.getValue()));
		// 12.正确的数据访问7，filename为空
		bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "generalQuery")
				.addData("tableName", TABLE_NAME)
				.addData("asynType", AsynType.ASYNPOLLING.getCode())
				.addData("filename", "")
				.addData("filepath", download.getAbsolutePath())
				.addData("dataType", DataType.json.getCode())
				.addData("outType", OutType.FILE.getCode())
				.post(getActionUrl("generalQuery"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("message"), is(StateType.FILENAME_ERROR.getValue()));
	}

	@Method(desc = "表结构查询", logicStep = "1.正确的数据访问1，使用token查询" +
			"2.错误的数据访问1，tableName为空")
	@Test
	public void tableStructureQuery() {
		String token = getToken(USER_ID, PASSWORD);
		// 1.正确的数据访问1，使用token查询
		String bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "tableStructureQuery")
				.addData("tableName", TABLE_NAME)
				.post(getActionUrl("tableStructureQuery"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("status"), is(StateType.NORMAL.getCode()));
		// 2.错误的数据访问1，tableName为空
		bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "tableStructureQuery")
				.addData("tableName", "")
				.post(getActionUrl("tableStructureQuery"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("status"), is(StateType.TABLE_NOT_EXISTENT.getCode()));
	}

	@Test
	public void fileAttributeSearch() {
	}

	@Method(desc = "sql查询接口测试", logicStep = "1.正确的数据访问1，使用token查询" +
			"2.正确的数据访问2，使用user_id,password查询" +
			"3.正确的数据访问3，dataType为csv" +
			"4.正确的数据访问4，异步轮询" +
			"")
	@Test
	public void sqlInterfaceSearch() {
		String token = getToken(USER_ID, PASSWORD);
		String sql_all = "select * from " + TABLE_NAME;
		String sql = "select " + SELECTCOLUMN + " from " + TABLE_NAME + " where c_customer_sk=2";
		// 1.正确的数据访问1，使用token查询
		String bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "sqlInterfaceSearch")
				.addData("sql", sql_all)
				.addData("dataType", DataType.json.getCode())
				.addData("outType", OutType.STREAM.getCode())
				.post(getActionUrl("sqlInterfaceSearch"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("status"), is(StateType.NORMAL.getCode()));
		List<Object> list = JsonUtil.toObject(ar.getDataForMap().get("message").toString(),
				new TypeReference<List<Object>>() {
				}.getType());
		assertThat("该表总共有100条数据", list.size(), is(100));
		// 2.正确的数据访问2，使用user_id,password查询
		bodyString = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("user_password", PASSWORD)
				.addData("url", "sqlInterfaceSearch")
				.addData("sql", sql)
				.addData("dataType", DataType.json.getCode())
				.addData("outType", OutType.STREAM.getCode())
				.post(getActionUrl("sqlInterfaceSearch"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("status"), is(StateType.NORMAL.getCode()));
		list = JsonUtil.toObject(ar.getDataForMap().get("message").toString(),
				new TypeReference<List<Object>>() {
				}.getType());
		assertThat("根据条件查询只有1条", list.size(), is(1));
		// 3.正确的数据访问3，dataType为csv
		bodyString = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("user_password", PASSWORD)
				.addData("url", "sqlInterfaceSearch")
				.addData("sql", sql)
				.addData("dataType", DataType.csv.getCode())
				.addData("outType", OutType.STREAM.getCode())
				.post(getActionUrl("sqlInterfaceSearch"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("status"), is(StateType.NORMAL.getCode()));
		// 4.正确的数据访问4，异步轮询
		File download = FileUtil.getFile("src/test/java/download");
		String filename = TABLE_NAME + THREAD_ID;
		bodyString = new HttpClient().buildSession()
				.addData("token", token)
				.addData("url", "sqlInterfaceSearch")
				.addData("sql", sql_all)
				.addData("asynType", 2)
				.addData("filename", filename)
				.addData("filepath", download.getAbsolutePath())
				.addData("dataType", DataType.json.getCode())
				.addData("outType", OutType.FILE.getCode())
				.post(getActionUrl("sqlInterfaceSearch"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("status"), is(StateType.NORMAL.getCode()));
		assertThat(FileUtil.getFile("src/test/java/download/" + filename).exists(), is(true));
	}

	@Test
	public void rowKeySearch() {
	}

	@Test
	public void uuidDownload() {
	}

	@After
	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//清理Data_store_reg表中造的数据
			deleteDataStoreReg(db);
			//清理Table_storage_info表中造的数据
			deleteTableStorageInfo(db);
			//清理Dtab_relation_store表中造的数据
			deleteDtabRelationStore(db);
			//清理Data_store_layer表中造的数据
			deleteDataStoreLayer(db);
			//清理Data_store_layer_attr表中造的数据
			deleteDataStoreLayerAttr(db);
			// 清理interface_use表测试数据
			deleteInterfaceUse(db);
			// 清理sys_user表测试数据
			deleteSysUser(db);
			// 删除Table_use_info表测试数据
			deleteTableUseInfo(db);
			// 删除Sysreg_parameter_info表测试数据
			deleteSysreg_parameter_info(db);
			// 删除Table_info表测试数据
			deleteTable_info(db);
			// 删除Table_column表测试数据
			deleteTable_column(db);

			// 提交事务
			SqlOperator.commitTransaction(db);
		}
	}

	private void deleteTable_column(DatabaseWrapper db) {
		SqlOperator.execute(db,
				"delete from " + Table_column.TableName + " where table_id=?",
				THREAD_ID);
		long num = SqlOperator.queryNumber(db,
				"select count(*) from " + Table_column.TableName + " where table_id=?",
				THREAD_ID)
				.orElseThrow(() -> new BusinessException("sql查询错误"));
		assertThat("删除" + Table_info.TableName + "表测试数据成功", num, is(0L));
	}

	private void deleteTable_info(DatabaseWrapper db) {
		SqlOperator.execute(db,
				"delete from " + Table_info.TableName + " where table_id=?",
				THREAD_ID);
		long num = SqlOperator.queryNumber(db,
				"select count(*) from " + Table_info.TableName + " where table_id=?",
				THREAD_ID)
				.orElseThrow(() -> new BusinessException("sql查询错误"));
		assertThat("删除" + Table_info.TableName + "表测试数据成功", num, is(0L));
	}

	private void deleteSysreg_parameter_info(DatabaseWrapper db) {
		SqlOperator.execute(db,
				"delete from " + Sysreg_parameter_info.TableName + " where use_id=?",
				THREAD_ID);
		long num = SqlOperator.queryNumber(db,
				"select count(*) from " + Sysreg_parameter_info.TableName + " where use_id=?",
				THREAD_ID)
				.orElseThrow(() -> new BusinessException("sql查询错误"));
		assertThat("删除" + Sysreg_parameter_info.TableName + "表测试数据成功", num, is(0L));
	}

	private void deleteTableUseInfo(DatabaseWrapper db) {
		SqlOperator.execute(db,
				"delete from " + Table_use_info.TableName + " where use_id=?",
				THREAD_ID);
		long num = SqlOperator.queryNumber(db,
				"select count(*) from " + Table_use_info.TableName + " where use_id=?",
				THREAD_ID)
				.orElseThrow(() -> new BusinessException("sql查询错误"));
		assertThat("删除" + Table_use_info.TableName + "表测试数据成功", num, is(0L));
	}

	private void deleteSysUser(DatabaseWrapper db) {
		for (Sys_user sys_user : getSys_users()) {
			SqlOperator.execute(db,
					"delete from " + Sys_user.TableName + " where user_id=?",
					sys_user.getUser_id());
			long num = SqlOperator.queryNumber(db,
					"select count(*) from " + Sys_user.TableName + " where user_id=?",
					sys_user.getUser_id())
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat("删除" + Sys_user.TableName + "表测试数据成功", num, is(0L));
		}
	}

	private void deleteDataStoreLayerAttr(DatabaseWrapper db) {
		for (Data_store_layer data_store_layer : getData_store_layers()) {
			SqlOperator.execute(db,
					"delete from " + Data_store_layer_attr.TableName + " where dsl_id=?",
					data_store_layer.getDsl_id());
			long num = SqlOperator.queryNumber(db,
					"select count(*) from " + Data_store_layer_attr.TableName + " where dsl_id=?",
					data_store_layer.getDsl_id())
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat("删除" + Data_store_layer_attr.TableName + "表测试数据成功", num, is(0L));
		}
	}

	private void deleteInterfaceUse(DatabaseWrapper db) {
		SqlOperator.execute(db,
				"delete from " + Interface_use.TableName + " where create_id=?",
				THREAD_ID);
		long num = SqlOperator.queryNumber(db,
				"select count(*) from " + Interface_use.TableName + " where create_id=?",
				THREAD_ID)
				.orElseThrow(() -> new BusinessException("sql查询错误"));
		assertThat("删除" + Interface_use.TableName + "表测试数据成功", num, is(0L));
	}

	private void deleteDataStoreLayer(DatabaseWrapper db) {
		// 确认测试数据删除
		for (Data_store_layer data_store_layer : getData_store_layers()) {
			SqlOperator.execute(db,
					"delete from " + Data_store_layer.TableName + " where dsl_id=?",
					data_store_layer.getDsl_id());
			long num = SqlOperator.queryNumber(db,
					"select count(*) from " + Data_store_layer.TableName + " where dsl_id=?",
					data_store_layer.getDsl_id())
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat("删除" + Data_store_layer.TableName + "表测试数据成功", num, is(0L));
		}
	}

	private void deleteDtabRelationStore(DatabaseWrapper db) {
		for (Data_store_layer data_store_layer : getData_store_layers()) {
			SqlOperator.execute(db,
					"delete from " + Dtab_relation_store.TableName + " where dsl_id=?",
					data_store_layer.getDsl_id());
			// 确认测试数据删除
			long num = SqlOperator.queryNumber(db,
					"select count(*) from " + Dtab_relation_store.TableName + " where dsl_id=?",
					data_store_layer.getDsl_id())
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat("删除" + Dtab_relation_store.TableName + "表测试数据成功", num, is(0L));
		}
	}

	private void deleteTableStorageInfo(DatabaseWrapper db) {
		SqlOperator.execute(db,
				"delete from " + Table_storage_info.TableName + " where storage_id=?",
				THREAD_ID);
		// 确认测试数据删除
		long num = SqlOperator.queryNumber(db,
				"select count(*) from " + Table_storage_info.TableName + " where storage_id=?",
				THREAD_ID)
				.orElseThrow(() -> new BusinessException("sql查询错误"));
		assertThat("删除" + Table_storage_info.TableName + "表测试数据成功", num, is(0L));
	}

	private void deleteDataStoreReg(DatabaseWrapper db) {
		SqlOperator.execute(db,
				"delete from " + Data_store_reg.TableName + " where file_id=?",
				String.valueOf(THREAD_ID));
		long num = SqlOperator.queryNumber(db,
				"select count(*) from " + Data_store_reg.TableName + " where file_id=?",
				String.valueOf(THREAD_ID))
				.orElseThrow(() -> new BusinessException("sql查询错误"));
		assertThat("删除" + Data_store_reg.TableName + "表测试数据成功", num, is(0L));
	}

}