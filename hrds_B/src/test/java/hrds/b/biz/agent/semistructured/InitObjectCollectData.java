package hrds.b.biz.agent.semistructured;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileUtil;
import fd.ng.core.yaml.YamlArray;
import fd.ng.core.yaml.YamlMap;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.StorageTypeKey;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.testbase.WebBaseTestCase;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class InitObjectCollectData extends WebBaseTestCase {
	// 已经存在的dep id
	public static final YamlArray TEST_DATABASES = agentInitConfig.getArray("test_databases");
	// agent所在机器的操作系统linux|windows
	public static final String AGENT_OS_NAME = agentInitConfig.getString("agent_os_name");
	// 已经部署过得agent
	public static final long AGENT_ID = agentInitConfig.getLong("agent_id");
	// 数据字典目录
	public final String filepath = FileUtil.getFile(
			"src/test/java/hrds/b/biz/agent/semistructured/dictionary").getAbsolutePath();
	//对象采集设置表id
	public final long ODC_ID = PrimayKeyGener.getNextId();
	// 对象采集任务编号
	public final long OCS_ID = PrimayKeyGener.getNextId();
	// 结构信息id
	public final long STRUCT_ID = PrimayKeyGener.getNextId();
	// 存储层附加属性ID
	public final long Dslad_id = PrimayKeyGener.getNextId();
	//当前线程的id
	public final long THREAD_ID = Thread.currentThread().getId() * 1000000;

	public void initData() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 造Object_collect表数据
			List<Object_collect> objectCollectList = getObject_collects();
			objectCollectList.forEach(object_collect ->
					assertThat(Object_collect.TableName + "表初始化测试数据成功", object_collect.add(db), is(1))
			);
			// 初始化数据存储层配置表测试数据
			List<Data_store_layer> storeLayers = getData_store_layers();
			storeLayers.forEach(data_store_layer ->
					assertThat("初始化data_store_layer表测试数据", data_store_layer.add(db), is(1))
			);
			// 初始化数据存储层配置属性表测试数据
			List<Data_store_layer_attr> dataStoreLayerAttrs = getData_store_layer_attrs();
			dataStoreLayerAttrs.forEach(data_store_layer_attr ->
					assertThat("初始化data_store_layer_attr表测试数据", data_store_layer_attr.add(db), is(1))
			);
			// 初始化data_store_layer_added表测试数据
			List<Data_store_layer_added> dataStoreLayerAddeds = getData_store_layer_addeds();
			dataStoreLayerAddeds.forEach(data_store_layer_added ->
					assertThat("初始化data_store_layer_added表测试数据", data_store_layer_added.add(db), is(1))
			);
			// 初始化dtab_relation_store测试数据
			List<Dtab_relation_store> dtabRelationStores = getDtab_relation_stores();
			dtabRelationStores.forEach(dtab_relation_store ->
					assertThat("初始化dtab_relation_store测试数据", dtab_relation_store.add(db), is(1))
			);
			// 初始化dtab_relation_store测试数据
			List<Dcol_relation_store> dcolRelationStores = getDcol_relation_stores();
			dcolRelationStores.forEach(dcol_relation_store ->
					assertThat("初始化dcol_relation_store测试数据", dcol_relation_store.add(db), is(1))
			);
			// 造object_collect_task表测试数据
			List<Object_collect_task> objectCollectTaskList = getObject_collect_tasks();
			objectCollectTaskList.forEach(object_collect_task ->
					assertThat(Object_collect_task.TableName + "表初始化测试数据成功",
							object_collect_task.add(db), is(1))
			);
			// 造Object_collect_struct表测试数据
			List<Object_collect_struct> objectCollectStructList = getObject_collect_structs(objectCollectTaskList);
			objectCollectStructList.forEach(object_collect_struct ->
					assertThat(Object_collect_struct.TableName + "表初始化测试数据成功",
							object_collect_struct.add(db), is(1))
			);
			// 造Object_handle_type表测试数据
			List<Object_handle_type> objectHandleTypes = getObject_handle_types(objectCollectTaskList);
			objectHandleTypes.forEach(object_handle_type ->
					assertThat(Object_handle_type.TableName + "表初始化测试数据成功",
							object_handle_type.add(db), is(1))
			);

			SqlOperator.commitTransaction(db);
		}
	}

	private List<Dcol_relation_store> getDcol_relation_stores() {
		List<Dcol_relation_store> dcolRelationStores = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Dcol_relation_store dcolRelationStore = new Dcol_relation_store();
			dcolRelationStore.setData_source(StoreLayerDataSource.OBJ.getCode());
			dcolRelationStore.setCsi_number("1");
			dcolRelationStore.setDslad_id(Dslad_id + i);
			dcolRelationStore.setCol_id(STRUCT_ID + i);
			dcolRelationStores.add(dcolRelationStore);
		}
		return dcolRelationStores;
	}

	private List<Object_handle_type> getObject_handle_types(List<Object_collect_task> objectCollectTaskList) {
		List<Object_handle_type> objectHandleTypes = new ArrayList<>();
		for (Object_collect_task object_collect_task : objectCollectTaskList) {
			OperationType[] values = OperationType.values();
			for (OperationType operationType : values) {
				Object_handle_type object_handle_type = new Object_handle_type();
				object_handle_type.setHandle_type(operationType.getCode());
				object_handle_type.setHandle_value(operationType.getValue());
				object_handle_type.setObject_handle_id(PrimayKeyGener.getNextId());
				object_handle_type.setOcs_id(object_collect_task.getOcs_id());
				objectHandleTypes.add(object_handle_type);
			}
		}
		return objectHandleTypes;
	}

	private List<Object_collect_struct> getObject_collect_structs(List<Object_collect_task> objectCollectTaskList) {
		List<Object_collect_struct> objectCollectStructList = new ArrayList<>();
		for (Object_collect_task object_collect_task : objectCollectTaskList) {
			for (int i = 0; i < 4; i++) {
				Object_collect_struct object_collect_struct = new Object_collect_struct();
				object_collect_struct.setStruct_id(PrimayKeyGener.getNextId());
				object_collect_struct.setOcs_id(object_collect_task.getOcs_id());
				if (i == 0) {
					object_collect_struct.setColumn_name("case_number");
					object_collect_struct.setData_desc("case_number");
					object_collect_struct.setIs_operate(IsFlag.Shi.getCode());
					object_collect_struct.setColumnposition("fields,case_number");
				} else if (i == 1) {
					object_collect_struct.setColumn_name("ops_flag");
					object_collect_struct.setData_desc("ops_flag");
					object_collect_struct.setIs_operate(IsFlag.Fou.getCode());
					object_collect_struct.setColumnposition("fields,ops_flag");
				} else if (i == 2) {
					object_collect_struct.setColumn_name("status");
					object_collect_struct.setData_desc("status");
					object_collect_struct.setIs_operate(IsFlag.Fou.getCode());
					object_collect_struct.setColumnposition("fields,status");
				} else {
					object_collect_struct.setColumn_name("operate");
					object_collect_struct.setData_desc("operate");
					object_collect_struct.setIs_operate(IsFlag.Fou.getCode());
					object_collect_struct.setColumnposition("fields,operate");
				}
				object_collect_struct.setColumn_type("decimal(38,18)");
				objectCollectStructList.add(object_collect_struct);
			}
		}
		return objectCollectStructList;
	}

	private List<Data_store_layer_added> getData_store_layer_addeds() {
		List<Data_store_layer_added> dataStoreLayerAddeds = new ArrayList<>();
		for (int i = 0; i < TEST_DATABASES.size(); i++) {
			YamlMap test_database = TEST_DATABASES.getMap(i);
			//存储层设置id
			long dsl_id = test_database.getLong("dsl_id");
			Data_store_layer_added data_store_layer_added = new Data_store_layer_added();
			data_store_layer_added.setDsl_id(dsl_id);
			data_store_layer_added.setDslad_id(Dslad_id + i);
			data_store_layer_added.setDsla_storelayer(StoreLayerAdded.ZhuJian.getCode());
			dataStoreLayerAddeds.add(data_store_layer_added);
		}
		return dataStoreLayerAddeds;
	}

	private List<Object_collect_task> getObject_collect_tasks() {
		List<Object_collect_task> objectCollectTaskList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			Object_collect_task object_collect_task = new Object_collect_task();
			object_collect_task.setOcs_id(OCS_ID + i);
			object_collect_task.setDatabase_code(DataBaseCode.UTF_8.getCode());
			object_collect_task.setCollect_data_type(CollectDataType.JSON.getCode());
			object_collect_task.setUpdatetype(UpdateType.DirectUpdate.getCode());
			if (i == 0) {
				object_collect_task.setOdc_id(ODC_ID);
				object_collect_task.setEn_name("no_dictionary");
				object_collect_task.setZh_name("no_dictionary");
				object_collect_task.setFirstline("[{\"date\":\"2019-08-10 0:01:51\",\"operate\":\"UPDATE\"," +
						"\"pos\":\"330617011-bin.008703-886163518\"," +
						"\"identity\":\"870c0361-c7e7-4e65-9109-6583822f22e2\"," +
						"\"host\":\"10.2.6.151\",\"id\":2191991496,\"pk\":\"_id\"," +
						"\"fields\":[{\"eid\":\"562efa91-798e-48fe-aba6-dce42cf8dbe6\"," +
						"\"created_time\":\"1565147701350\",\"case_relation\":\"0\"," +
						"\"amount\":\"2372862\",\"case_date\":\"2019-08-06\",\"u_tags_update\":\"0\"," +
						"\"p_eid\":\"\",\"pid\":\"\",\"type\":\"E\",\"court\":\"上海市宝山区人民法院\"," +
						"\"url\":\"http://zhixing.court.gov.cn/search/\",\"number\":\"91330421753****961P\"," +
						"\"last_update_time\":\"1565366511596\",\"ename\":\"浙江昱辉阳光能源有限公司\"," +
						"\"u_tags\":\"0\",\"name\":\"浙江昱辉阳光能源有限公司\",\"case_id\":\"53846440\"," +
						"\"row_update_time\":\"2019-08-10 00:01:51\",\"p_ename\":\"\"," +
						"\"case_number\":\"（2019）沪0113执4156号\",\"_id\":\"5d4a4235ebf551133e0042b8\"," +
						"\"ops_flag\":\"8\",\"status\":\"0\"},{\"eid\":\"562efa91-798e-48fe-aba6-dce42cf8dbe6\"," +
						"\"created_time\":\"1565147701350\",\"case_relation\":\"0\",\"amount\":\"2372862\"," +
						"\"case_date\":\"2019-08-06\",\"u_tags_update\":\"0\",\"p_eid\":\"\",\"pid\":\"\"," +
						"\"type\":\"E\",\"court\":\"上海市宝山区人民法院\",\"url\":\"http://zhixing.court.gov.cn/search/\"," +
						"\"number\":\"91330421753****961P\",\"last_update_time\":\"1565366511596\"," +
						"\"ename\":\"浙江昱辉阳光能源有限公司\",\"u_tags\":\"0\",\"name\":\"浙江昱辉阳光能源有限公司\"," +
						"\"case_id\":\"53846440\",\"row_update_time\":\"2019-08-10 00:01:51\",\"p_ename\":\"\"," +
						"\"case_number\":\"（2019）沪0113执4156号\",\"_id\":\"5d4a4235ebf551133e0042b8\"," +
						"\"ops_flag\":\"8\",\"status\":\"0\"}],\"db\":\"db_enterprise_other\"," +
						"\"table\":\"no_dictionary\",\"sql\":\"\"}]");
			} else if (i == 1) {
				object_collect_task.setOdc_id(ODC_ID + 1);
				object_collect_task.setZh_name("t_executedpersons");
				object_collect_task.setEn_name("t_executedpersons");
				object_collect_task.setFirstline("");
			} else {
				object_collect_task.setOdc_id(ODC_ID + 1);
				object_collect_task.setZh_name("t_executedpersons2");
				object_collect_task.setEn_name("t_executedpersons2");
				object_collect_task.setFirstline("");
			}
			object_collect_task.setAgent_id(AGENT_ID);
			objectCollectTaskList.add(object_collect_task);
		}
		return objectCollectTaskList;
	}

	private List<Dtab_relation_store> getDtab_relation_stores() {
		List<Dtab_relation_store> dtab_relation_stores = new ArrayList<>();
		for (int i = 0; i < TEST_DATABASES.size(); i++) {
			YamlMap test_database = TEST_DATABASES.getMap(i);
			//存储层设置id
			long dsl_id = test_database.getLong("dsl_id");
			Dtab_relation_store drt = new Dtab_relation_store();
			drt.setTab_id(OCS_ID + i);
			drt.setDsl_id(dsl_id);
			drt.setData_source(StoreLayerDataSource.OBJ.getCode());
			dtab_relation_stores.add(drt);
		}
		return dtab_relation_stores;
	}

	private List<Object_collect> getObject_collects() {
		List<Object_collect> objectCollectList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Object_collect object_collect = new Object_collect();
			object_collect.setOdc_id(ODC_ID + i);
			object_collect.setObject_collect_type(ObjectCollectType.HangCaiJi.getCode());
			object_collect.setObj_number("dhwtest" + i + THREAD_ID);
			object_collect.setObj_collect_name("测试对象采集任务名称");
			object_collect.setSystem_name(AGENT_OS_NAME);
			object_collect.setHost_name("mine");
			object_collect.setLocal_time(DateUtil.getDateTime());
			object_collect.setServer_date(DateUtil.getSysDate());
			object_collect.setS_date(DateUtil.getSysDate());
			object_collect.setE_date(Constant.MAXDATE);
			object_collect.setDatabase_code(DataBaseCode.UTF_8.getCode());
			object_collect.setFile_path(filepath);
			object_collect.setIs_sendok(IsFlag.Fou.getCode());
			object_collect.setAgent_id(AGENT_ID);
			object_collect.setIs_dictionary(IsFlag.Shi.getCode());
			if (i == 0) {
				// 无数据字典
				object_collect.setIs_dictionary(IsFlag.Fou.getCode());
				// 无数据字典时的数据日期
				object_collect.setData_date("20200601");
				object_collect.setFile_suffix("dat");
			} else {
				// 有数据字典
				object_collect.setIs_dictionary(IsFlag.Shi.getCode());
				object_collect.setFile_path(filepath);
				object_collect.setData_date("");
				object_collect.setFile_suffix("json");
			}
			objectCollectList.add(object_collect);
		}
		return objectCollectList;
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

	public void deleteInitData() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//清理Data_store_layer表中造的数据
			deleteDataStoreLayer(db);
			//清理Data_store_layer_attr表中造的数据
			deleteDataStoreLayerAttr(db);
			//清理Data_store_layer_attr表中造的数据
			deleteDataStoreLayerAdded(db);
			//清理Dtab_relation_store表中造的数据
			deleteDtabRelationStore(db);
			// 删除测试用例造的Object_collect表数据
			deleteObjectCollect(db);
			// 删除测试用例造的Object_collect_task表数据
			deleteObjectCollectTask(db);
			// 删除测试用例造的Object_collect_struct表数据
			deleteObjectCollectStruct(db);
			// 删除测试用例造的Dcol_relation_store表数据
			deleteDcol_relation_store(db);
// 删除测试用例造的Object_collect_struct表数据

			// 删除测试用例造的Object_handle_type表数据
			deleteObjectHandleType(db);
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
	}

	private void deleteObjectHandleType(DatabaseWrapper db) {
		SqlOperator.execute(db,
				"DELETE FROM " + Object_handle_type.TableName + " WHERE ocs_id in(?,?,?)"
				, OCS_ID, OCS_ID + 1, OCS_ID + 2);
		long num = SqlOperator.queryNumber(db,
				"select count (*) from " + Object_handle_type.TableName + " where ocs_id in(?,?,?)",
				OCS_ID, OCS_ID + 1, OCS_ID + 2)
				.orElseThrow(() -> new BusinessException("sql查询错误"));
		assertThat(Object_handle_type.TableName + "表测试数据已删除", num, is(0L));
	}

	private void deleteDcol_relation_store(DatabaseWrapper db) {
		// 删除测试用例造的Object_collect_task表数据
		SqlOperator.execute(db,
				"DELETE FROM " + Dcol_relation_store.TableName + " WHERE dslad_id in(?,?)"
				, Dslad_id, Dslad_id + 1);
		long num = SqlOperator.queryNumber(db,
				"select count (*) from " + Dcol_relation_store.TableName + " where dslad_id in(?,?)",
				Dslad_id, Dslad_id + 1)
				.orElseThrow(() -> new BusinessException("sql查询错误"));
		assertThat(Dcol_relation_store.TableName + "表测试数据已删除", num, is(0L));
	}

	private void deleteObjectCollectStruct(DatabaseWrapper db) {
		SqlOperator.execute(db,
				"DELETE FROM " + Object_collect_struct.TableName + " WHERE ocs_id in(?,?,?)"
				, OCS_ID, OCS_ID + 1, OCS_ID + 2);
		long num = SqlOperator.queryNumber(db,
				"select count (*) from " + Object_collect_struct.TableName + " where ocs_id in(?,?,?)",
				OCS_ID, OCS_ID + 1, OCS_ID + 2)
				.orElseThrow(() -> new BusinessException("sql查询错误"));
		assertThat(Object_collect_struct.TableName + "表测试数据已删除", num, is(0L));
	}

	private void deleteObjectCollectTask(DatabaseWrapper db) {
		// 删除测试用例造的Object_collect_task表数据
		SqlOperator.execute(db,
				"DELETE FROM " + Object_collect_task.TableName + " WHERE odc_id in(?,?)"
				, ODC_ID, ODC_ID + 1);
		long num = SqlOperator.queryNumber(db,
				"select count (*) from " + Object_collect_task.TableName + " where odc_id in(?,?)",
				ODC_ID, ODC_ID + 1)
				.orElseThrow(() -> new BusinessException("sql查询错误"));
		assertThat(Object_collect_task.TableName + "表测试数据已删除", num, is(0L));
	}

	private void deleteObjectCollect(DatabaseWrapper db) {
		SqlOperator.execute(db,
				"DELETE FROM " + Object_collect.TableName + " WHERE agent_id = ?"
				, AGENT_ID);
		long num = SqlOperator.queryNumber(db,
				"select count (*) from " + Object_collect.TableName + " where agent_id=?",
				AGENT_ID)
				.orElseThrow(() -> new BusinessException("sql查询错误"));
		assertThat(Object_collect.TableName + "表测试数据已删除", num, is(0L));
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

	private void deleteDataStoreLayerAdded(DatabaseWrapper db) {
		for (Data_store_layer data_store_layer : getData_store_layers()) {
			SqlOperator.execute(db,
					"delete from " + Data_store_layer_added.TableName + " where dsl_id=?",
					data_store_layer.getDsl_id());
			long num = SqlOperator.queryNumber(db,
					"select count(*) from " + Data_store_layer_added.TableName + " where dsl_id=?",
					data_store_layer.getDsl_id())
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat("删除" + Data_store_layer_added.TableName + "表测试数据成功", num, is(0L));
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
}
