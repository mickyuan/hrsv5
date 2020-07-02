package hrds.a.biz.datastore;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.FileUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.netclient.http.SubmitMediaType;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.StoreLayerAdded;
import hrds.commons.codes.Store_type;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.StorageTypeKey;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class DataStoreActionTest extends WebBaseTestCase {

	//请填写测试用户需要做登录验证的A项目的登录验证的接口
	private static final String LOGIN_URL = testInfoConfig.getString("login_url");
	// 已经存在的用户ID,用于模拟登录
	private static final long USER_ID = testInfoConfig.getLong("user_id");
	private static final String PASSWORD = testInfoConfig.getString("password");
	//当前线程的id
	private long THREAD_ID = Thread.currentThread().getId() * 1000000;
	// 初始化数据存储层配置ID
	private final long DSL_ID = PrimayKeyGener.getNextId();
	// 初始化存储层数据类型对照主表ID
	private static final long DTCS_ID = PrimayKeyGener.getNextId();
	// 初始化存储层数据类型长度对照主表ID
	private static final long DLCS_ID = PrimayKeyGener.getNextId();
	// 初始化附加信息ID
	private static final long DSLAD_ID = PrimayKeyGener.getNextId();
	// 初始化存储层配置属性信息
	private static final long DSLA_ID = PrimayKeyGener.getNextId();
	// 初始化存储层数据类型对照表ID
	private static final long DTC_ID = PrimayKeyGener.getNextId();
	// 初始化存储层数据类型长度对照表ID
	private static final long DLC_ID = PrimayKeyGener.getNextId();

	@Method(desc = "构造初始化表测试数据", logicStep = "初始化存储层测试数据")
	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 初始化数据存储层配置表数据
			List<Data_store_layer> dataStoreLayerList = getData_store_layers();
			dataStoreLayerList.forEach(data_store_layer ->
					assertThat("测试数据etl_sys初始化", data_store_layer.add(db), is(1))
			);
			// 初始化数据存储附加信息表数据
			List<Data_store_layer_added> dataStoreLayerAddeds = getData_store_layer_addeds();
			dataStoreLayerAddeds.forEach(data_store_layer_added ->
					assertThat("测试数据data_store_layer_added初始化", data_store_layer_added.add(db), is(1))
			);
			// 初始化数据存储层配置属性表数据
			List<Data_store_layer_attr> storeLayerAttrList = getData_store_layer_attrs();
			storeLayerAttrList.forEach(data_store_layer_attr ->
					assertThat("测试数据data_store_layer_attr初始化", data_store_layer_attr.add(db), is(1))
			);
			List<Data_store_layer_attr> storeLayerAttrList2 = getData_store_layer_attrsHbase();
			storeLayerAttrList2.forEach(data_store_layer_attr ->
					assertThat("测试数据data_store_layer_attr初始化", data_store_layer_attr.add(db), is(1))
			);
			// 初始化类型对照主表信息
			List<Type_contrast_sum> typeContrastSumList = getType_contrast_sums();
			typeContrastSumList.forEach(type_contrast_sum ->
					assertThat("测试数据type_contrast_sum初始化", type_contrast_sum.add(db), is(1))
			);
			// 初始化类型长度对照主表信息
			List<Length_contrast_sum> length_contrast_sums = getLength_contrast_sums();
			length_contrast_sums.forEach(length_contrast_sum ->
					assertThat("测试数据length_contrast_sum初始化", length_contrast_sum.add(db), is(1))
			);
			// 初始化类型对照表信息
			List<Type_contrast> type_contrasts = getType_contrasts();
			type_contrasts.forEach(type_contrast ->
					assertThat("测试数据type_contrast初始化", type_contrast.add(db), is(1))
			);
			// 初始化类型长度对照表信息
			List<Length_contrast> lengthContrastList = getLength_contrasts();
			lengthContrastList.forEach(length_contrast ->
					assertThat("测试数据length_contrast初始化", length_contrast.add(db), is(1))
			);
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
		// 模拟用户登录
		String bodyString = new HttpClient().buildSession()
				.addData("user_id", USER_ID)
				.addData("password", PASSWORD)
				.post(LOGIN_URL)
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接失败"));
		assertThat("用户登录", ar.isSuccess(), is(true));
	}

	private List<Type_contrast_sum> getType_contrast_sums() {
		List<Type_contrast_sum> typeContrastSumList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Type_contrast_sum type_contrast_sum = new Type_contrast_sum();
			type_contrast_sum.setDtcs_id(DTCS_ID + i);
			if (i == 0) {
				type_contrast_sum.setDtcs_name("MYSQL");
			} else {
				type_contrast_sum.setDtcs_name("ORACLE");
			}
			type_contrast_sum.setDtcs_remark("类型对照主表测试");
			typeContrastSumList.add(type_contrast_sum);
		}
		return typeContrastSumList;
	}

	private List<Length_contrast_sum> getLength_contrast_sums() {
		List<Length_contrast_sum> length_contrast_sums = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Length_contrast_sum length_contrast_sum = new Length_contrast_sum();
			length_contrast_sum.setDlcs_id(DLCS_ID + i);
			if (i == 0) {
				length_contrast_sum.setDlcs_name("length_contrast_sum");
			} else {
				length_contrast_sum.setDlcs_name("length_contrast");
			}
			length_contrast_sum.setDlcs_remark("类型长度对照主表测试");
			length_contrast_sums.add(length_contrast_sum);
		}
		return length_contrast_sums;
	}

	private List<Type_contrast> getType_contrasts() {
		List<Type_contrast> type_contrasts = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Type_contrast type_contrast = new Type_contrast();
			type_contrast.setDtc_id(DTC_ID + i);
			if (i == 0) {
				type_contrast.setSource_type("number");
				type_contrast.setTarget_type("decimal");
			} else {
				type_contrast.setSource_type("timestamp ");
				type_contrast.setTarget_type("datetime");
			}
			type_contrast.setDtcs_id(DTCS_ID);
			type_contrast.setDtc_remark("类型对照表测试");
			type_contrasts.add(type_contrast);
		}
		return type_contrasts;
	}

	private List<Length_contrast> getLength_contrasts() {
		List<Length_contrast> lengthContrastList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Length_contrast length_contrast = new Length_contrast();
			length_contrast.setDlc_id(DLC_ID + i);
			if (i == 0) {
				length_contrast.setDlc_type("string");
				length_contrast.setDlc_length("100");
			} else {
				length_contrast.setDlc_type("number");
				length_contrast.setDlc_length("20");
			}
			length_contrast.setDlcs_id(DLCS_ID);
			length_contrast.setDlc_remark("类型长度对照表测试");
			lengthContrastList.add(length_contrast);
		}
		return lengthContrastList;
	}

	private List<Data_store_layer_attr> getData_store_layer_attrs() {
		List<Data_store_layer_attr> storeLayerAttrList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Data_store_layer_attr dataStoreLayerAttr = new Data_store_layer_attr();
			dataStoreLayerAttr.setDsla_id(DSLA_ID + i);
			dataStoreLayerAttr.setDsl_id(DSL_ID);
			if (i == 0) {
				dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.database_type);
				dataStoreLayerAttr.setStorage_property_val(DatabaseType.Postgresql.getCode());
			} else if (i == 1) {
				dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.database_driver);
				dataStoreLayerAttr.setStorage_property_val("org.postgresql.Driver");
			} else if (i == 2) {
				dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.database_name);
				dataStoreLayerAttr.setStorage_property_val("hrsdxg");
			} else if (i == 3) {
				dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.database_pwd);
				dataStoreLayerAttr.setStorage_property_val("hrsdxg");
			} else {
				dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.jdbc_url);
				dataStoreLayerAttr.setStorage_property_val("jdbc:postgresql://10.71.4.57:31001/hrsdxg");
			}
			dataStoreLayerAttr.setDsla_remark("数据存储层配置属性测试");
			dataStoreLayerAttr.setIs_file(IsFlag.Fou.getCode());
			storeLayerAttrList.add(dataStoreLayerAttr);
		}
		return storeLayerAttrList;
	}

	private List<Data_store_layer_attr> getData_store_layer_attrsHbase() {
		List<Data_store_layer_attr> storeLayerAttrList = new ArrayList<>();
		for (int i = 100; i < 106; i++) {
			Data_store_layer_attr dataStoreLayerAttr = new Data_store_layer_attr();
			dataStoreLayerAttr.setDsla_id(DSLA_ID + i);
			dataStoreLayerAttr.setDsl_id(DSL_ID + 1);
			dataStoreLayerAttr.setIs_file(IsFlag.Fou.getCode());
			if (i == 100) {
				dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.database_driver);
				dataStoreLayerAttr.setStorage_property_val("org.apache.hive.jdbc.HiveDriver");
			} else if (i == 101) {
				dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.database_name);
				dataStoreLayerAttr.setStorage_property_val("hyshf");
			} else if (i == 102) {
				dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.database_pwd);
				dataStoreLayerAttr.setStorage_property_val("hyshf");
			} else if (i == 103) {
				dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.jdbc_url);
				dataStoreLayerAttr.setStorage_property_val("jdbc:hive2://10.71.4.57:10000/hyshf");
			} else if (i == 104) {
				dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.hdfs_site);
				dataStoreLayerAttr.setStorage_property_val("E:\\tmp\\upfiles\\temp\\a\\9876543210.xml");
			} else {
				dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.hbase_site);
				dataStoreLayerAttr.setStorage_property_val("E:\\tmp\\upfiles\\temp\\a\\1234567890.xml");
				dataStoreLayerAttr.setIs_file(IsFlag.Shi.getCode());
			}
			dataStoreLayerAttr.setDsla_remark("数据存储层配置属性测试");
			storeLayerAttrList.add(dataStoreLayerAttr);
		}
		return storeLayerAttrList;
	}

	private List<Data_store_layer_added> getData_store_layer_addeds() {
		List<Data_store_layer_added> dataStoreLayerAddeds = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			Data_store_layer_added dataStoreLayerAdded = new Data_store_layer_added();
			dataStoreLayerAdded.setDslad_id(DSLAD_ID + i);
			if (i == 0) {
				dataStoreLayerAdded.setDsl_id(DSL_ID);
				dataStoreLayerAdded.setDsla_storelayer(StoreLayerAdded.ZhuJian.getCode());
			} else if (i == 1) {
				dataStoreLayerAdded.setDsl_id(DSL_ID + 2);
				dataStoreLayerAdded.setDsla_storelayer(StoreLayerAdded.RowKey.getCode());
			} else if (i == 2) {
				dataStoreLayerAdded.setDsl_id(DSL_ID + 2);
				dataStoreLayerAdded.setDsla_storelayer(StoreLayerAdded.SuoYinLie.getCode());
			} else if (i == 3) {
				dataStoreLayerAdded.setDsl_id(DSL_ID + 1);
				dataStoreLayerAdded.setDsla_storelayer(StoreLayerAdded.YuJuHe.getCode());
			} else if (i == 4) {
				dataStoreLayerAdded.setDsl_id(DSL_ID + 3);
				dataStoreLayerAdded.setDsla_storelayer(StoreLayerAdded.PaiXuLie.getCode());
			} else {
				dataStoreLayerAdded.setDsl_id(DSL_ID + 5);
				dataStoreLayerAdded.setDsla_storelayer(StoreLayerAdded.FenQuLie.getCode());
			}
			dataStoreLayerAdded.setDslad_remark("数据存储附加信息测试" + i);
			dataStoreLayerAddeds.add(dataStoreLayerAdded);
		}
		return dataStoreLayerAddeds;
	}

	private List<Data_store_layer> getData_store_layers() {
		List<Data_store_layer> dataStoreLayerList = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			Data_store_layer dataStoreLayer = new Data_store_layer();
			dataStoreLayer.setDsl_id(DSL_ID + i);
			if (i == 0) {
				dataStoreLayer.setStore_type(Store_type.DATABASE.getCode());
				dataStoreLayer.setIs_hadoopclient(IsFlag.Fou.getCode());
			} else if (i == 1) {
				dataStoreLayer.setStore_type(Store_type.HIVE.getCode());
				dataStoreLayer.setIs_hadoopclient(IsFlag.Shi.getCode());
			} else if (i == 2) {
				dataStoreLayer.setStore_type(Store_type.HBASE.getCode());
				dataStoreLayer.setIs_hadoopclient(IsFlag.Shi.getCode());
			} else if (i == 3) {
				dataStoreLayer.setStore_type(Store_type.SOLR.getCode());
				dataStoreLayer.setIs_hadoopclient(IsFlag.Shi.getCode());
			} else if (i == 4) {
				dataStoreLayer.setStore_type(Store_type.ElasticSearch.getCode());
				dataStoreLayer.setIs_hadoopclient(IsFlag.Shi.getCode());
			} else {
				dataStoreLayer.setStore_type(Store_type.MONGODB.getCode());
				dataStoreLayer.setIs_hadoopclient(IsFlag.Shi.getCode());
			}
			dataStoreLayer.setDsl_name("数据存储层配置测试名称" + i);
			dataStoreLayer.setDsl_remark("数据存储层配置测试" + i);
			dataStoreLayer.setDtcs_id(DTCS_ID);
			dataStoreLayer.setDlcs_id(DLCS_ID);
			dataStoreLayerList.add(dataStoreLayer);
		}
		return dataStoreLayerList;
	}

	@Method(desc = "新增数据存储层、数据存储附加、数据存储层配置属性信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，dsl_name为空" +
					"3.错误的数据访问2，dsl_name为空格" +
					"4.错误的数据访问3，store_type为空" +
					"5.错误的数据访问4，store_type为空格" +
					"6.错误的数据访问5，store_type为不存在" +
					"7.错误的数据访问6，dataStoreLayerAttr为空" +
					"8.错误的数据访问7，dataStoreLayerAttr为空格")
	@Test
	public void addDataStore() {
		// 1.正常的数据访问1，数据都正常
		File file = FileUtil.getFile("src//test//java//hrds//a//biz//datastore//upload");
		List<Data_store_layer_attr> storeLayerAttrList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Data_store_layer_attr dataStoreLayerAttr = new Data_store_layer_attr();
			dataStoreLayerAttr.setDsla_id(PrimayKeyGener.getNextId());
			dataStoreLayerAttr.setDsl_id(DSL_ID);
			if (i == 0) {
				dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.database_type);
				dataStoreLayerAttr.setStorage_property_val(DatabaseType.Postgresql.getCode());
			} else if (i == 1) {
				dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.database_driver);
				dataStoreLayerAttr.setStorage_property_val("org.postgresql.Driver");
			} else if (i == 2) {
				dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.database_name);
				dataStoreLayerAttr.setStorage_property_val("hrsdxg");
			} else if (i == 3) {
				dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.database_pwd);
				dataStoreLayerAttr.setStorage_property_val("hrsdxg");
			} else {
				dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.jdbc_url);
				dataStoreLayerAttr.setStorage_property_val("jdbc:postgresql://10.71.4.57:31001/hrsdxg");
			}
			dataStoreLayerAttr.setDsla_remark("数据存储层配置属性测试");
			dataStoreLayerAttr.setIs_file(IsFlag.Fou.getCode());
			storeLayerAttrList.add(dataStoreLayerAttr);
		}
		String bodyString = new HttpClient()
				.reset(SubmitMediaType.MULTIPART)
				.addData("dsl_name", "addDataStore1")
				.addData("store_type", Store_type.HIVE.getCode())
				.addData("dsl_remark", "新增数据存储层配置信息")
				.addData("dsla_storelayer", new String[]{StoreLayerAdded.ZhuJian.getCode(),
						StoreLayerAdded.SuoYinLie.getCode()})
				.addData("dslad_remark", "新增数据存储附加信息")
				.addData("dataStoreLayerAttr", JsonUtil.toJson(storeLayerAttrList))
				.addData("is_hadoopclient", IsFlag.Fou.getCode())
				.addData("dtcs_id", DTCS_ID)
				.addData("dlcs_id", DLCS_ID)
				.addFile("files", Objects.requireNonNull(file.listFiles()))
				.addData("dsla_remark", "上传配置文件")
				.post(getActionUrl("addDataStore"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Data_store_layer dataStoreLayer = SqlOperator.queryOneObject(db, Data_store_layer.class,
					"select * from " + Data_store_layer.TableName + " where dsl_name=?",
					"addDataStore1").orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(Store_type.HIVE.getCode(), is(dataStoreLayer.getStore_type()));
			assertThat(IsFlag.Fou.getCode(), is(dataStoreLayer.getIs_hadoopclient()));
			assertThat(DLCS_ID, is(dataStoreLayer.getDlcs_id()));
			assertThat(DTCS_ID, is(dataStoreLayer.getDtcs_id()));
			assertThat("新增数据存储层配置信息", is(dataStoreLayer.getDsl_remark()));
			assertThat(IsFlag.Fou.getCode(), is(dataStoreLayer.getIs_hadoopclient()));
			List<Data_store_layer_added> layerAddeds = SqlOperator.queryList(db, Data_store_layer_added.class,
					"select * from " + Data_store_layer_added.TableName + " where dsl_id=? " +
							"order by dsla_storelayer", dataStoreLayer.getDsl_id());
			for (Data_store_layer_added layerAdded : layerAddeds) {
				if (StoreLayerAdded.ZhuJian == StoreLayerAdded.ofEnumByCode(layerAdded.getDsla_storelayer())) {
					assertThat("新增数据存储附加信息", is(layerAdded.getDslad_remark()));
				} else {
					assertThat("新增数据存储附加信息", is(layerAdded.getDslad_remark()));

				}
			}
			List<Data_store_layer_attr> layerAttrs = SqlOperator.queryList(db, Data_store_layer_attr.class,
					"select * from " + Data_store_layer_attr.TableName + " where dsl_id=?",
					dataStoreLayer.getDsl_id());
			for (Data_store_layer_attr layerAttr : layerAttrs) {
				if (layerAttr.getStorage_property_key().equals("数据库")) {
					assertThat(IsFlag.Fou.getCode(), is(layerAttr.getIs_file()));
					assertThat(DatabaseType.Postgresql.getCode(), is(layerAttr.getStorage_property_val()));
				} else if (layerAttr.getStorage_property_key().equals("数据库驱动")) {
					assertThat(IsFlag.Fou.getCode(), is(layerAttr.getIs_file()));
					assertThat("org.postgresql.Driver", is(layerAttr.getStorage_property_val()));
				} else if (layerAttr.getStorage_property_key().equals("core-site.xml")) {
					assertThat(IsFlag.Shi.getCode(), is(layerAttr.getIs_file()));
					Files.delete(new File(layerAttr.getStorage_property_val()).toPath());
				} else if (layerAttr.getStorage_property_key().equals("hbase-site.xml")) {
					assertThat(IsFlag.Shi.getCode(), is(layerAttr.getIs_file()));
					Files.delete(new File(layerAttr.getStorage_property_val()).toPath());
				} else if (layerAttr.getStorage_property_key().equals("hdfs-site.xml")) {
					assertThat(IsFlag.Shi.getCode(), is(layerAttr.getIs_file()));
					Files.delete(new File(layerAttr.getStorage_property_val()).toPath());
				}
			}
			// 2.错误的数据访问1，dsl_name为空
			bodyString = new HttpClient()
					.addData("dsl_name", "")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "新增数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(storeLayerAttrList))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", DTCS_ID)
					.addData("dlcs_id", DLCS_ID)
					.post(getActionUrl("addDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，dsl_name为空格
			bodyString = new HttpClient()
					.addData("dsl_name", " ")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "新增数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(storeLayerAttrList))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", DTCS_ID)
					.addData("dlcs_id", DLCS_ID)
					.post(getActionUrl("addDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，store_type为空
			bodyString = new HttpClient()
					.addData("dsl_name", "addDataStore13")
					.addData("store_type", "")
					.addData("dsl_remark", "新增数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(storeLayerAttrList))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", DTCS_ID)
					.addData("dlcs_id", DLCS_ID)
					.post(getActionUrl("addDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 5.错误的数据访问4，store_type为空格
			bodyString = new HttpClient()
					.addData("dsl_name", "addDataStore14")
					.addData("store_type", " ")
					.addData("dsl_remark", "新增数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(storeLayerAttrList))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", DTCS_ID)
					.addData("dlcs_id", DLCS_ID)
					.post(getActionUrl("addDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 6.错误的数据访问5，store_type为不存在
			bodyString = new HttpClient()
					.addData("dsl_name", "addDataStore15")
					.addData("store_type", 100)
					.addData("dsl_remark", "新增数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(storeLayerAttrList))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", DTCS_ID)
					.addData("dlcs_id", DLCS_ID)
					.post(getActionUrl("addDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 7.错误的数据访问6，dataStoreLayerAttr为空
			bodyString = new HttpClient()
					.addData("dsl_name", "addDataStore13")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "新增数据存储层配置信息")
					.addData("dataStoreLayerAttr", "")
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", DTCS_ID)
					.addData("dlcs_id", DLCS_ID)
					.post(getActionUrl("addDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 8.错误的数据访问7，dataStoreLayerAttr为空格
			bodyString = new HttpClient()
					.addData("dsl_name", "addDataStore14")
					.addData("store_type", Store_type.DATABASE.getCode())
					.addData("dsl_remark", "新增数据存储层配置信息")
					.addData("dataStoreLayerAttr", " ")
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", DTCS_ID)
					.addData("dlcs_id", DLCS_ID)
					.post(getActionUrl("addDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		} catch (IOException e) {
			throw new BusinessException("删除文件失败");
		}
	}

	@Method(desc = "更新保存数据存储层信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，dsl_id为空" +
					"3.错误的数据访问2，dsl_id为空格" +
					"4.错误的数据访问3，dsl_id不存在" +
					"5.错误的数据访问4，dslad_id为空" +
					"6.错误的数据访问5，dslad_id为空格" +
					"7.错误的数据访问6，dslad_id不存在" +
					"8.错误的数据访问7，dsla_id为空" +
					"9.错误的数据访问8，dsla_id为空格" +
					"10.错误的数据访问9，dsla_id不存在" +
					"11.错误的数据访问10，dsl_name为空" +
					"12.错误的数据访问11，dsl_name为空格" +
					"13.错误的数据访问12,store_type为空" +
					"14.错误的数据访问13,store_type为空格" +
					"15.错误的数据访问14，store_type不存在" +
					"16.错误的数据访问15，dataStoreLayerAttr为空" +
					"17.错误的数据访问16，dataStoreLayerAttr为空格")
	@Test
	public void updateDataStore() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			List<Data_store_layer_attr> storeLayerAttrList = new ArrayList<>();
			for (int i = 0; i < 5; i++) {
				Data_store_layer_attr dataStoreLayerAttr = new Data_store_layer_attr();
				dataStoreLayerAttr.setDsla_id(DSLA_ID + i);
				dataStoreLayerAttr.setDsl_id(DSL_ID);
				if (i == 0) {
					dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.database_type);
					dataStoreLayerAttr.setStorage_property_val(DatabaseType.Oracle10g.getCode());
				} else if (i == 1) {
					dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.database_driver);
					dataStoreLayerAttr.setStorage_property_val("oracle.jdbc.OracleDriver");
				} else if (i == 2) {
					dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.database_name);
					dataStoreLayerAttr.setStorage_property_val("hyshf");
				} else if (i == 3) {
					dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.database_pwd);
					dataStoreLayerAttr.setStorage_property_val("hyshf");
				} else {
					dataStoreLayerAttr.setStorage_property_key(StorageTypeKey.jdbc_url);
					dataStoreLayerAttr.setStorage_property_val("jdbc:oracle:thin:@47.103.83.2:1521:hyshf");
				}
				dataStoreLayerAttr.setDsla_remark("数据存储层配置属性测试");
				dataStoreLayerAttr.setIs_file(IsFlag.Fou.getCode());
				storeLayerAttrList.add(dataStoreLayerAttr);
			}
			File file = FileUtil.getFile("src//test//java//hrds//a//biz//datastore//upload");
			String bodyString = new HttpClient()
					.reset(SubmitMediaType.MULTIPART)
					.addData("dsl_id", DSL_ID)
					.addData("dsl_name", "upDataStore1")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dsla_storelayer", new String[]{StoreLayerAdded.FenQuLie.getCode(),
							StoreLayerAdded.PaiXuLie.getCode()})
					.addData("dslad_remark", "更新数据存储附加信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(storeLayerAttrList))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("dtcs_id", DTCS_ID)
					.addData("dlcs_id", DLCS_ID)
					.addData("dsla_remark", "hbase配置文件上传")
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			Data_store_layer layer = SqlOperator.queryOneObject(db, Data_store_layer.class,
					"select * from " + Data_store_layer.TableName + " where dsl_id=?", DSL_ID)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(Store_type.HBASE.getCode(), is(layer.getStore_type()));
			assertThat("更新数据存储层配置信息", is(layer.getDsl_remark()));
			assertThat(IsFlag.Shi.getCode(), is(layer.getIs_hadoopclient()));
			List<Data_store_layer_added> layerAddeds = SqlOperator.queryList(db, Data_store_layer_added.class,
					"select * from " + Data_store_layer_added.TableName + " where dsl_id=? " +
							"order by dsla_storelayer", DSL_ID);
			for (Data_store_layer_added layerAdded : layerAddeds) {
				if (StoreLayerAdded.PaiXuLie == StoreLayerAdded.ofEnumByCode(layerAdded.getDsla_storelayer())) {
					assertThat("更新数据存储附加信息", is(layerAdded.getDslad_remark()));
				} else {
					assertThat("更新数据存储附加信息", is(layerAdded.getDslad_remark()));
				}
			}
			List<Data_store_layer_attr> layerAttrs = SqlOperator.queryList(db, Data_store_layer_attr.class,
					"select * from " + Data_store_layer_attr.TableName + " where dsl_id=? " +
							" order by storage_property_key", layer.getDsl_id());
			for (Data_store_layer_attr layerAttr : layerAttrs) {
				if (layerAttr.getStorage_property_key().equals("数据库")) {
					assertThat(IsFlag.Fou.getCode(), is(layerAttr.getIs_file()));
					assertThat("更新数据存储层配置属性信息1", is(layerAttr.getDsla_remark()));
					assertThat(DatabaseType.MYSQL.getCode(), is(layerAttr.getStorage_property_val()));
				} else if (layerAttr.getStorage_property_key().equals("数据库驱动")) {
					assertThat(IsFlag.Fou.getCode(), is(layerAttr.getIs_file()));
					assertThat("更新数据存储层配置属性信息2", is(layerAttr.getDsla_remark()));
				} else if (layerAttr.getStorage_property_key().equals("core-site.xml")) {
					assertThat(IsFlag.Shi.getCode(), is(layerAttr.getIs_file()));
					assertThat("core-site.xml文件已上传", is(layerAttr.getDsla_remark()));
				} else if (layerAttr.getStorage_property_key().equals("hdfs-site.xml")) {
					assertThat(IsFlag.Shi.getCode(), is(layerAttr.getIs_file()));
					assertThat("hdfs-site.xml文件已上传", is(layerAttr.getDsla_remark()));
				} else if (layerAttr.getStorage_property_key().equals("hbase-site.xml")) {
					assertThat(IsFlag.Shi.getCode(), is(layerAttr.getIs_file()));
					assertThat("hbase-site.xml文件已上传", is(layerAttr.getDsla_remark()));
				}
			}
			// 2.错误的数据访问1，dsl_id为空
			bodyString = new HttpClient()
					.addData("dsl_id", "")
					.addData("dsl_name", "upDataStore2")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(storeLayerAttrList))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", DTCS_ID)
					.addData("dlcs_id", DLCS_ID)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，dsl_id为空格
			bodyString = new HttpClient()
					.addData("dsl_id", " ")
					.addData("dsl_name", "upDataStore3")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(storeLayerAttrList))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", DTCS_ID)
					.addData("dlcs_id", DLCS_ID)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，dsl_id不存在
			bodyString = new HttpClient()
					.addData("dsl_id", 1)
					.addData("dsl_name", "upDataStore4")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(storeLayerAttrList))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", DTCS_ID)
					.addData("dlcs_id", DLCS_ID)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 11.错误的数据访问10，dsl_name为空
			bodyString = new HttpClient()
					.addData("dsl_id", DSL_ID)
					.addData("dsl_name", "")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(storeLayerAttrList))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", DTCS_ID)
					.addData("dlcs_id", DLCS_ID)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 13.错误的数据访问12，store_type为空
			bodyString = new HttpClient()
					.addData("dsl_id", DSL_ID)
					.addData("dsl_name", "upDataStore13")
					.addData("store_type", "")
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(storeLayerAttrList))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", DTCS_ID)
					.addData("dlcs_id", DLCS_ID)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 15.错误的数据访问14，store_type为不存在
			bodyString = new HttpClient()
					.addData("dsl_id", DSL_ID)
					.addData("dsl_name", "upDataStore15")
					.addData("store_type", 6)
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(storeLayerAttrList))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", DTCS_ID)
					.addData("dlcs_id", DLCS_ID)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 16.错误的数据访问15，dataStoreLayerAttr为空
			bodyString = new HttpClient()
					.addData("dsl_id", DSL_ID)
					.addData("dsl_name", "upDataStore13")
					.addData("store_type", Store_type.DATABASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", "")
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", DTCS_ID)
					.addData("dlcs_id", DLCS_ID)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "删除数据存储层信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，dsl_id不存在")
	@Test
	public void deleteDataStore() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			// 删除前查询数据库，确认预期删除的数据存在
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Data_store_layer.TableName + " where dsl_id = ?", DSL_ID);
			assertThat("删除操作前，保证Data_store_layer表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(1L));
			String bodyString = new HttpClient()
					.addData("dsl_id", DSL_ID)
					.post(getActionUrl("deleteDataStore"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期删除的数据存在
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Data_store_layer.TableName + " where dsl_id = ?", DSL_ID);
			assertThat("删除操作后，确认该条数据被删除", optionalLong.orElse(Long.MIN_VALUE),
					is(0L));
			// 2.错误的数据访问1，dsl_id不存在
			bodyString = new HttpClient()
					.addData("dsl_id", 1)
					.post(getActionUrl("deleteDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));

		}
	}

	@Method(desc = "查询数据存储层配置信息", logicStep = "1.正常的数据访问1，数据都正常,该方法只有一种情况")
	@Test
	public void searchDataStore() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.post(getActionUrl("searchDataStore"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Result storeLayer = ar.getDataForResult();
		for (int i = 0; i < storeLayer.getRowCount(); i++) {
			long dsl_id = storeLayer.getLong(i, "dsl_id");
			if (dsl_id == DSL_ID) {
				assertThat(Store_type.DATABASE.getCode(), is(storeLayer.getString(i, "store_type")));
				assertThat("数据存储层配置测试名称0", is(storeLayer.getString(i, "dsl_name")));
				assertThat(IsFlag.Fou.getCode(), is(storeLayer.getString(i, "is_hadoopclient")));
			} else if (dsl_id == DSL_ID + 1) {
				assertThat(Store_type.HIVE.getCode(), is(storeLayer.getString(i, "store_type")));
				assertThat("数据存储层配置测试名称1", is(storeLayer.getString(i, "dsl_name")));
				assertThat(IsFlag.Shi.getCode(), is(storeLayer.getString(i, "is_hadoopclient")));
			} else if (dsl_id == DSL_ID + 2) {
				assertThat(Store_type.HBASE.getCode(), is(storeLayer.getString(i, "store_type")));
				assertThat("数据存储层配置测试名称2", is(storeLayer.getString(i, "dsl_name")));
				assertThat(IsFlag.Shi.getCode(), is(storeLayer.getString(i, "is_hadoopclient")));
			} else if (dsl_id == DSL_ID + 3) {
				assertThat(Store_type.SOLR.getCode(), is(storeLayer.getString(i, "store_type")));
				assertThat("数据存储层配置测试名称3", is(storeLayer.getString(i, "dsl_name")));
				assertThat(IsFlag.Shi.getCode(), is(storeLayer.getString(i, "is_hadoopclient")));
			} else if (dsl_id == DSL_ID + 4) {
				assertThat(Store_type.ElasticSearch.getCode(), is(storeLayer.getString(i, "store_type")));
				assertThat("数据存储层配置测试名称4", is(storeLayer.getString(i, "dsl_name")));
				assertThat(IsFlag.Shi.getCode(), is(storeLayer.getString(i, "is_hadoopclient")));
			} else if (dsl_id == DSL_ID + 5) {
				assertThat(Store_type.MONGODB.getCode(), is(storeLayer.getString(i, "store_type")));
				assertThat("数据存储层配置测试名称5", is(storeLayer.getString(i, "dsl_name")));
				assertThat(IsFlag.Shi.getCode(), is(storeLayer.getString(i, "is_hadoopclient")));
			}
		}
	}

	@Method(desc = "根据权限数据存储层配置ID关联查询数据存储层信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.正确的数据访问2，数据存储属性信息不存在" +
					"3.错误的数据访问1，dsl_id不存在")
	@Test
	public void searchDataStoreById() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("dsl_id", DSL_ID)
				.post(getActionUrl("searchDataStoreById"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> dataForMap = ar.getDataForMap();
		List<Map<String, Object>> layerAndAdded = (List<Map<String, Object>>) dataForMap.get("layerAndAdded");
		List<Map<String, Object>> layerAndAttr = (List<Map<String, Object>>) dataForMap.get("layerAndAttr");
		assertThat(dataForMap.get("store_type"), is(Store_type.DATABASE.getCode()));
		assertThat("数据存储层配置测试名称0", is(dataForMap.get("dsl_name")));
		for (Map<String, Object> map : layerAndAdded) {
			assertThat(map.get("dslad_id").toString(), is(String.valueOf(DSLAD_ID)));
			assertThat(String.valueOf(DSL_ID), is(map.get("dsl_id").toString()));
			assertThat(StoreLayerAdded.ZhuJian.getCode(), is(map.get("dsla_storelayer")));
		}
		for (Map<String, Object> map : layerAndAttr) {
			String dsla_id = map.get("dsla_id").toString();
			assertThat(map.get("dsl_id").toString(), is(String.valueOf(DSL_ID)));
			if (dsla_id.equals(String.valueOf(DSLA_ID))) {
				assertThat(map.get("storage_property_key"), is(StorageTypeKey.database_type));
				assertThat(map.get("storage_property_val"), is(DatabaseType.Postgresql.getCode()));
			} else if (dsla_id.equals(String.valueOf(DSLA_ID + 1))) {
				assertThat(map.get("storage_property_key"), is(StorageTypeKey.database_driver));
				assertThat(map.get("storage_property_val"), is("org.postgresql.Driver"));
			} else if (dsla_id.equals(String.valueOf(DSLA_ID + 2))) {
				assertThat(map.get("storage_property_key"), is(StorageTypeKey.database_name));
				assertThat(map.get("storage_property_val"), is("hrsdxg"));
			} else if (dsla_id.equals(String.valueOf(DSLA_ID + 3))) {
				assertThat(map.get("storage_property_key"), is(StorageTypeKey.database_pwd));
				assertThat(map.get("storage_property_val"), is("hrsdxg"));
			} else {
				assertThat(map.get("storage_property_key"), is(StorageTypeKey.jdbc_url));
				assertThat(map.get("storage_property_val"), is("jdbc:postgresql://10.71.4.57:31001/hrsdxg"));
			}
		}
		// 2.正确的数据访问2，带上传文件的
		bodyString = new HttpClient()
				.addData("dsl_id", DSL_ID + 1)
				.post(getActionUrl("searchDataStoreById"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		layerAndAdded = (List<Map<String, Object>>) dataForMap.get("layerAndAdded");
		layerAndAttr = (List<Map<String, Object>>) dataForMap.get("layerAndAttr");
		assertThat(dataForMap.get("store_type"), is(Store_type.HIVE.getCode()));
		assertThat("数据存储层配置测试名称1", is(dataForMap.get("dsl_name")));
		for (Map<String, Object> map : layerAndAdded) {
			assertThat(map.get("dsl_id").toString(), is(String.valueOf(DSL_ID + 1)));
			assertThat(map.get("dslad_id").toString(), is(String.valueOf(DSLAD_ID + 3)));
			assertThat(StoreLayerAdded.YuJuHe.getCode(), is(map.get("dsla_storelayer")));
		}
		for (Map<String, Object> map : layerAndAttr) {
			String dsla_id = map.get("dsla_id").toString();
			assertThat(map.get("dsl_id").toString(), is(String.valueOf(DSL_ID + 1)));
			if (dsla_id.equals(String.valueOf(DSLA_ID + 100))) {
				assertThat(map.get("storage_property_key"), is(StorageTypeKey.database_driver));
				assertThat(map.get("storage_property_val"), is("org.apache.hive.jdbc.HiveDriver"));
			} else if (dsla_id.equals(String.valueOf(DSLA_ID + 101))) {
				assertThat(map.get("storage_property_key"), is(StorageTypeKey.database_name));
				assertThat(map.get("storage_property_val"), is("hyshf"));
			} else if (dsla_id.equals(String.valueOf(DSLA_ID + 102))) {
				assertThat(map.get("storage_property_key"), is(StorageTypeKey.database_pwd));
				assertThat(map.get("storage_property_val"), is("hyshf"));
			} else if (dsla_id.equals(String.valueOf(DSLA_ID + 103))) {
				assertThat(map.get("storage_property_key"), is(StorageTypeKey.jdbc_url));
				assertThat(map.get("storage_property_val"), is("jdbc:hive2://10.71.4.57:10000/hyshf"));
			} else if (dsla_id.equals(String.valueOf(DSLA_ID + 104))) {
				assertThat(map.get("storage_property_key"), is(StorageTypeKey.hdfs_site));
				assertThat(map.get("storage_property_val"), is("E:\\tmp\\upfiles\\temp\\a\\9876543210.xml"));
			} else {
				assertThat(map.get("storage_property_key"), is(StorageTypeKey.hbase_site));
				assertThat(map.get("storage_property_val"), is("E:\\tmp\\upfiles\\temp\\a\\1234567890.xml"));
			}
		}
		// 3.错误的数据访问1，dsl_id不存在
		bodyString = new HttpClient()
				.addData("dsl_id", 1)
				.post(getActionUrl("searchDataStoreById"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		layerAndAdded = (List<Map<String, Object>>) dataForMap.get("layerAndAdded");
		layerAndAttr = (List<Map<String, Object>>) dataForMap.get("layerAndAttr");
		assertThat(layerAndAdded.isEmpty(), is(true));
		assertThat(layerAndAttr.isEmpty(), is(true));
	}

	@Method(desc = "新增存储层数据类型对比信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，dtcs_name为空" +
					"3.错误的数据访问2，dtcs_name为空格" +
					"4.错误的数据访问3，dtcs_name已存在" +
					"5.错误的数据访问4，typeContrast为空" +
					"6.错误的数据访问5，typeContrast为空格")
	@Test
	public void addDataTypeContrastInfo() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Map<String, String>> list = new ArrayList<>();
			for (int i = 0; i < 2; i++) {
				Map<String, String> map = new HashMap<>();
				if (i == 0) {
					map.put("source_type", "varchar");
					map.put("target_type", "varchar2");
				} else {
					map.put("source_type", "text");
					map.put("target_type", "CLOB");
				}
				map.put("dtc_remark", "类型对照表备注");
				list.add(map);
			}
			// 1.正常的数据访问1，数据都正常
			String bodyString = new HttpClient()
					.addData("typeContrast", JsonUtil.toJson(list))
					.addData("dtcs_name", "oracle")
					.addData("dtcs_remark", "类型对照主表备注")
					.post(getActionUrl("addDataTypeContrastInfo"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 验证数据正确性
			Type_contrast_sum typeContrastSum = SqlOperator.queryOneObject(db, Type_contrast_sum.class,
					"select * from " + Type_contrast_sum.TableName + " where dtcs_name=?", "oracle")
					.orElseThrow(() -> new BusinessException("sql查询错误！"));
			assertThat(typeContrastSum.getDtcs_name(), is("oracle"));
			assertThat(typeContrastSum.getDtcs_remark(), is("类型对照主表备注"));
			List<Type_contrast> typeContrastList = SqlOperator.queryList(db, Type_contrast.class,
					"select * from " + Type_contrast.TableName + " where dtcs_id=?",
					typeContrastSum.getDtcs_id());
			for (Type_contrast type_contrast : typeContrastList) {
				if (type_contrast.getTarget_type().equals("varchar2")) {
					assertThat(type_contrast.getSource_type(), is("varchar"));
					assertThat(type_contrast.getDtc_remark(), is("类型对照表备注"));
				} else if (type_contrast.getTarget_type().equals("CLOB")) {
					assertThat(type_contrast.getSource_type(), is("text"));
					assertThat(type_contrast.getDtc_remark(), is("类型对照表备注"));
				}
				assertThat(type_contrast.getDtcs_id(), is(typeContrastSum.getDtcs_id()));
			}
			// 2.错误的数据访问1，dtcs_name为空
			bodyString = new HttpClient()
					.addData("typeContrast", JsonUtil.toJson(list))
					.addData("dtcs_name", "")
					.addData("dtcs_remark", "类型对照主表备注")
					.addData("dtc_remark", "类型对照表备注")
					.post(getActionUrl("addDataTypeContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，dtcs_name为空格
			bodyString = new HttpClient()
					.addData("typeContrast", JsonUtil.toJson(list))
					.addData("dtcs_name", " ")
					.addData("dtcs_remark", "类型对照主表备注")
					.addData("dtc_remark", "类型对照表备注")
					.post(getActionUrl("addDataTypeContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，dtcs_name已存在
			bodyString = new HttpClient()
					.addData("typeContrast", JsonUtil.toJson(list))
					.addData("dtcs_name", "oracle")
					.addData("dtcs_remark", "类型对照主表备注")
					.addData("dtc_remark", "类型对照表备注")
					.post(getActionUrl("addDataTypeContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 5.错误的数据访问4，typeContrast为空
			bodyString = new HttpClient()
					.addData("typeContrast", "")
					.addData("dtcs_name", "MYSQL")
					.addData("dtcs_remark", "类型对照主表备注")
					.addData("dtc_remark", "类型对照表备注")
					.post(getActionUrl("addDataTypeContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 6.错误的数据访问5，typeContrast为空格
			bodyString = new HttpClient()
					.addData("typeContrast", " ")
					.addData("dtcs_name", "MYSQL")
					.addData("dtcs_remark", "类型对照主表备注")
					.addData("dtc_remark", "类型对照表备注")
					.post(getActionUrl("addDataTypeContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "更新存储层数据类型对比信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，dtcs_name为空" +
					"3.错误的数据访问2，dtcs_name为空格" +
					"4.错误的数据访问4，typeContrast为空" +
					"5.错误的数据访问5，typeContrast为空格")
	@Test
	public void updateDataTypeContrastInfo() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Map<String, String>> list = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				Map<String, String> map = new HashMap<>();
				if (i == 0) {
					map.put("source_type", "number");
					map.put("target_type", "int");
				} else if (i == 1) {
					map.put("source_type", "text");
					map.put("target_type", "varchar");
				} else {
					map.put("source_type", "CHAR");
					map.put("target_type", "NCHAR");
				}
				map.put("dtc_remark", "更新类型对照表备注");
				list.add(map);
			}
			// 1.正常的数据访问1，数据都正常
			String bodyString = new HttpClient()
					.addData("dtcs_id", DTCS_ID)
					.addData("typeContrast", JsonUtil.toJson(list))
					.addData("dtcs_name", "MYSQL")
					.addData("dtcs_remark", "更新类型对照主表备注")
					.post(getActionUrl("updateDataTypeContrastInfo"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 验证数据正确性
			Type_contrast_sum typeContrastSum = SqlOperator.queryOneObject(db, Type_contrast_sum.class,
					"select * from " + Type_contrast_sum.TableName + " where dtcs_id=?", DTCS_ID)
					.orElseThrow(() -> new BusinessException("sql查询错误！"));
			assertThat(typeContrastSum.getDtcs_name(), is("MYSQL"));
			assertThat(typeContrastSum.getDtcs_remark(), is("更新类型对照主表备注"));
			List<Type_contrast> typeContrastList = SqlOperator.queryList(db, Type_contrast.class,
					"select * from " + Type_contrast.TableName + " where dtcs_id=?", DTCS_ID);
			for (Type_contrast type_contrast : typeContrastList) {
				if (type_contrast.getTarget_type().equals("int")) {
					assertThat(type_contrast.getSource_type(), is("number"));
				} else if (type_contrast.getTarget_type().equals("varchar")) {
					assertThat(type_contrast.getSource_type(), is("text"));
				} else {
					assertThat(type_contrast.getSource_type(), is("CHAR"));
				}
				assertThat(type_contrast.getDtcs_id(), is(DTCS_ID));
				assertThat(type_contrast.getDtc_remark(), is("更新类型对照表备注"));
			}
			// 2.错误的数据访问1，dtcs_name为空
			bodyString = new HttpClient()
					.addData("dtcs_id", DTCS_ID)
					.addData("typeContrast", JsonUtil.toJson(list))
					.addData("dtcs_name", "")
					.addData("dtcs_remark", "类型对照主表备注")
					.addData("dtc_remark", "类型对照表备注")
					.post(getActionUrl("updateDataTypeContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，dtcs_name为空格
			bodyString = new HttpClient()
					.addData("dtcs_id", DTCS_ID)
					.addData("typeContrast", JsonUtil.toJson(list))
					.addData("dtcs_name", " ")
					.addData("dtcs_remark", "类型对照主表备注")
					.addData("dtc_remark", "类型对照表备注")
					.post(getActionUrl("updateDataTypeContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，typeContrast为空
			bodyString = new HttpClient()
					.addData("dtcs_id", DTCS_ID)
					.addData("typeContrast", "")
					.addData("dtcs_name", "MYSQL")
					.addData("dtcs_remark", "类型对照主表备注")
					.addData("dtc_remark", "类型对照表备注")
					.post(getActionUrl("updateDataTypeContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 5.错误的数据访问4，typeContrast为空格
			bodyString = new HttpClient()
					.addData("dtcs_id", DTCS_ID)
					.addData("typeContrast", " ")
					.addData("dtcs_name", "MYSQL")
					.addData("dtcs_remark", "类型对照主表备注")
					.addData("dtc_remark", "类型对照表备注")
					.post(getActionUrl("updateDataTypeContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "删除数据类型对照信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，dtcs_id不存在")
	@Test
	public void deleteDataTypeContrastInfo() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			// 删除前查询数据库，确认预期删除的数据存在
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Type_contrast_sum.TableName + " where dtcs_id = ?", DTCS_ID);
			assertThat("删除操作前，保证Data_store_layer表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(1L));
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Type_contrast.TableName + " where dtcs_id = ?", DTCS_ID);
			assertThat("删除操作前，保证Data_store_layer表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(2L));
			String bodyString = new HttpClient()
					.addData("dtcs_id", DTCS_ID)
					.post(getActionUrl("deleteDataTypeContrastInfo"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期删除的数据存在
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Type_contrast_sum.TableName + " where dtcs_id = ?", DTCS_ID);
			assertThat("删除操作后，确认该条数据被删除", optionalLong.orElse(Long.MIN_VALUE),
					is(0L));
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Type_contrast.TableName + " where dtcs_id = ?", DTCS_ID);
			assertThat("删除操作后，确认该条数据被删除", optionalLong.orElse(Long.MIN_VALUE),
					is(0L));
			// 2.错误的数据访问1，dtcs_id不存在
			bodyString = new HttpClient()
					.addData("dtcs_id", 100)
					.post(getActionUrl("deleteDataTypeContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "新增存储层数据类型对比信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错6误的数据访问1，lengthInfo为空" +
					"3.错误的数据访问2，lengthInfo为空格" +
					"4.错误的数据访问4，dlcs_name为空" +
					"5.错误的数据访问5，dlcs_name为空格" +
					"6.错误的数据访问3，dlcs_name已存在")
	@Test
	public void addTypeLengthContrastInfo() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Map<String, String>> list = new ArrayList<>();
			for (int i = 0; i < 2; i++) {
				Map<String, String> map = new HashMap<>();
				if (i == 0) {
					map.put("dlc_type", "varchar");
					map.put("dlc_length", "256");
				} else {
					map.put("dlc_type", "int");
					map.put("dlc_length", "10");
				}
				map.put("dlc_remark", "类型长度对照表备注");
				list.add(map);
			}
			// 1.正确的数据访问1，数据都正确
			String bodyString = new HttpClient()
					.addData("lengthInfo", JsonUtil.toJson(list))
					.addData("dlcs_name", "type_contrast")
					.addData("dlcs_remark", "类型长度对照主表备注")
					.post(getActionUrl("addTypeLengthContrastInfo"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			Length_contrast_sum lengthContrastSum = SqlOperator.queryOneObject(db, Length_contrast_sum.class,
					"select * from " + Length_contrast_sum.TableName + " where dlcs_name=?",
					"type_contrast").orElseThrow(() -> new BusinessException("sql查询错误！"));
			assertThat(lengthContrastSum.getDlcs_name(), is("type_contrast"));
			assertThat(lengthContrastSum.getDlcs_remark(), is("类型长度对照主表备注"));
			List<Length_contrast> lengthContrastList = SqlOperator.queryList(db, Length_contrast.class,
					"select * from " + Length_contrast.TableName + " where dlcs_id=?",
					lengthContrastSum.getDlcs_id());
			for (Length_contrast length_contrast : lengthContrastList) {
				assertThat(length_contrast.getDlc_remark(), is("类型长度对照表备注"));
				if (length_contrast.getDlc_type().equals("varchar")) {
					assertThat(length_contrast.getDlc_length(), is(256));
				} else if (length_contrast.getDlc_type().equals("int")) {
					assertThat(length_contrast.getDlc_length(), is(10));
				}
			}
			// 2.错误的数据访问1，lengthInfo为空
			bodyString = new HttpClient()
					.addData("lengthInfo", "")
					.addData("dlc_remark", "类型长度对照表备注")
					.addData("dlcs_name", "type_contrast")
					.addData("dlcs_remark", "类型长度对照主表备注")
					.post(getActionUrl("addTypeLengthContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，lengthInfo为空格
			bodyString = new HttpClient()
					.addData("lengthInfo", " ")
					.addData("dlc_remark", "类型长度对照表备注")
					.addData("dlcs_name", "type_contrast_sum")
					.addData("dlcs_remark", "类型长度对照主表备注")
					.post(getActionUrl("addTypeLengthContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，dlcs_name为空
			bodyString = new HttpClient()
					.addData("lengthInfo", JsonUtil.toJson(list))
					.addData("dlc_remark", "类型长度对照表备注")
					.addData("dlcs_name", "")
					.addData("dlcs_remark", "类型长度对照主表备注")
					.post(getActionUrl("addTypeLengthContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 5.错误的数据访问4，dlcs_name为空格
			bodyString = new HttpClient()
					.addData("lengthInfo", JsonUtil.toJson(list))
					.addData("dlc_remark", "类型长度对照表备注")
					.addData("dlcs_name", " ")
					.addData("dlcs_remark", "类型长度对照主表备注")
					.post(getActionUrl("addTypeLengthContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 6.错误的数据访问5，dlcs_name已存在
			bodyString = new HttpClient()
					.addData("lengthInfo", JsonUtil.toJson(list))
					.addData("dlc_remark", "类型长度对照表备注")
					.addData("dlcs_name", "type_contrast")
					.addData("dlcs_remark", "类型长度对照主表备注")
					.post(getActionUrl("addTypeLengthContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "新增存储层数据类型对比信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错6误的数据访问1，lengthInfo为空" +
					"3.错误的数据访问2，lengthInfo为空格" +
					"4.错误的数据访问4，dlcs_name为空" +
					"5.错误的数据访问5，dlcs_name为空格")
	@Test
	public void updateTypeLengthContrastInfo() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Map<String, String>> list = new ArrayList<>();
			for (int i = 0; i < 2; i++) {
				Map<String, String> map = new HashMap<>();
				if (i == 0) {
					map.put("dlc_type", "varchar2");
					map.put("dlc_length", "512");
				} else {
					map.put("dlc_type", "long");
					map.put("dlc_length", "20");
				}
				map.put("dlc_remark", "更新类型长度对照表备注");
				list.add(map);
			}
			// 1.正确的数据访问1，数据都正确
			String bodyString = new HttpClient()
					.addData("dlcs_id", DLCS_ID)
					.addData("lengthInfo", JsonUtil.toJson(list))
					.addData("dlcs_name", "dlcs_name")
					.addData("dlcs_remark", "更新类型长度对照主表备注")
					.post(getActionUrl("updateTypeLengthContrastInfo"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			Length_contrast_sum lengthContrastSum = SqlOperator.queryOneObject(db, Length_contrast_sum.class,
					"select * from " + Length_contrast_sum.TableName + " where dlcs_id=?",
					DLCS_ID).orElseThrow(() -> new BusinessException("sql查询错误！"));
			assertThat(lengthContrastSum.getDlcs_name(), is("dlcs_name"));
			assertThat(lengthContrastSum.getDlcs_remark(), is("更新类型长度对照主表备注"));
			List<Length_contrast> lengthContrastList = SqlOperator.queryList(db, Length_contrast.class,
					"select * from " + Length_contrast.TableName + " where dlcs_id=?",
					lengthContrastSum.getDlcs_id());
			for (Length_contrast length_contrast : lengthContrastList) {
				assertThat(length_contrast.getDlc_remark(), is("更新类型长度对照表备注"));
				if (length_contrast.getDlc_type().equals("varchar2")) {
					assertThat(length_contrast.getDlc_length(), is(512));
				} else if (length_contrast.getDlc_type().equals("long")) {
					assertThat(length_contrast.getDlc_length(), is(20));
				}
			}
			// 2.错误的数据访问1，lengthInfo为空
			bodyString = new HttpClient()
					.addData("dlcs_id", DLCS_ID)
					.addData("lengthInfo", "")
					.addData("dlc_remark", "类型长度对照表备注")
					.addData("dlcs_name", "type_contrast")
					.addData("dlcs_remark", "类型长度对照主表备注")
					.post(getActionUrl("updateTypeLengthContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，lengthInfo为空格
			bodyString = new HttpClient()
					.addData("dlcs_id", DLCS_ID)
					.addData("lengthInfo", " ")
					.addData("dlc_remark", "类型长度对照表备注")
					.addData("dlcs_name", "type_contrast_sum")
					.addData("dlcs_remark", "类型长度对照主表备注")
					.post(getActionUrl("updateTypeLengthContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，dlcs_name为空
			bodyString = new HttpClient()
					.addData("dlcs_id", DLCS_ID)
					.addData("lengthInfo", JsonUtil.toJson(list))
					.addData("dlc_remark", "类型长度对照表备注")
					.addData("dlcs_name", "")
					.addData("dlcs_remark", "类型长度对照主表备注")
					.post(getActionUrl("updateTypeLengthContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 5.错误的数据访问4，dlcs_name为空格
			bodyString = new HttpClient()
					.addData("dlcs_id", DLCS_ID)
					.addData("lengthInfo", JsonUtil.toJson(list))
					.addData("dlc_remark", "类型长度对照表备注")
					.addData("dlcs_name", " ")
					.addData("dlcs_remark", "类型长度对照主表备注")
					.post(getActionUrl("updateTypeLengthContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "删除数据类型长度对照信息",
			logicStep = "1.正常的数据访问1，数据都正常" +
					"2.错误的数据访问1，dtcs_id不存在")
	@Test
	public void deleteTypeLengthContrastInfo() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.正常的数据访问1，数据都正常
			// 删除前查询数据库，确认预期删除的数据存在
			OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Length_contrast_sum.TableName + " where dlcs_id = ?", DLCS_ID);
			assertThat("删除操作前，保证Data_store_layer表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(1L));
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Length_contrast.TableName + " where dlcs_id = ?", DLCS_ID);
			assertThat("删除操作前，保证Data_store_layer表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(2L));
			String bodyString = new HttpClient()
					.addData("dlcs_id", DLCS_ID)
					.post(getActionUrl("deleteTypeLengthContrastInfo"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期删除的数据存在
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Length_contrast_sum.TableName + " where dlcs_id = ?", DLCS_ID);
			assertThat("删除操作后，确认该条数据被删除", optionalLong.orElse(Long.MIN_VALUE),
					is(0L));
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Length_contrast.TableName + " where dlcs_id = ?", DLCS_ID);
			assertThat("删除操作后，确认该条数据被删除", optionalLong.orElse(Long.MIN_VALUE),
					is(0L));
			// 2.错误的数据访问1，dtcs_id不存在
			bodyString = new HttpClient()
					.addData("dlcs_id", 100)
					.post(getActionUrl("deleteTypeLengthContrastInfo"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "查询数据存储层数据类型对照以及长度对照主表信息",
			logicStep = "1.正常的数据访问1，数据都正常,该方法只有一种情况")
	@Test
	public void searchDataTypeMasterTableInfo() {
		String bodyString = new HttpClient()
				.post(getActionUrl("searchDataTypeMasterTableInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> map = ar.getDataForMap();
		Type type = new TypeReference<List<Map<String, Object>>>() {
		}.getType();
		List<Map<String, Object>> lengthContrastList = JsonUtil.toObject(map.get("lengthContrastSumList").toString(),
				type);
		for (Map<String, Object> lengthContrast : lengthContrastList) {
			if (String.valueOf(DLCS_ID).equals(lengthContrast.get("dlcs_id").toString())) {
				assertThat(lengthContrast.get("dlcs_remark"), is("类型长度对照主表测试"));
				assertThat(lengthContrast.get("dlcs_name"), is("length_contrast_sum"));
			} else if (String.valueOf(DLCS_ID + 1).equals(lengthContrast.get("dlcs_id").toString())) {
				assertThat(lengthContrast.get("dlcs_name"), is("length_contrast"));
				assertThat(lengthContrast.get("dlcs_remark"), is("类型长度对照主表测试"));
			}
		}
		List<Map<String, Object>> typeContrastList = JsonUtil.toObject(map.get("typeContrastSumList").toString(),
				type);
		for (Map<String, Object> typeContrast : typeContrastList) {
			if (String.valueOf(DTCS_ID).equals(typeContrast.get("dtcs_id").toString())) {
				assertThat(typeContrast.get("dtcs_remark"), is("类型对照主表测试"));
				assertThat(typeContrast.get("dtcs_name"), is("MYSQL"));
			} else if (String.valueOf(DTCS_ID + 1).equals(typeContrast.get("dtcs_id").toString())) {
				assertThat(typeContrast.get("dtcs_name"), is("ORACLE"));
				assertThat(typeContrast.get("dtcs_remark"), is("类型对照主表测试"));
			}
		}
	}

	@Method(desc = "查询数据存储层数据类型对照以及长度对照表信息",
			logicStep = "1.正常的数据访问1，数据都正常,该方法只有一种情况")
	@Test
	public void searchDataLayerDataTypeInfo() {
		String bodyString = new HttpClient()
				.post(getActionUrl("searchDataLayerDataTypeInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		checkTypeContrastData(result);
		// 2.正确的数据访问2，dtcs_id不为空
		bodyString = new HttpClient()
				.addData("dtcs_id", DTCS_ID)
				.post(getActionUrl("searchDataLayerDataTypeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		result = ar.getDataForResult();
		checkTypeContrastData(result);
	}

	@Method(desc = "校验类型对照数据正确性", logicStep = "1.循环遍历结果集校验数据")
	private void checkTypeContrastData(Result result) {
		// 1.循环遍历结果集校验数据
		for (int i = 0; i < result.getRowCount(); i++) {
			if (DTC_ID == result.getLong(i, "dtc_id")) {
				assertThat(result.getString(i, "dtc_remark"), is("类型对照表测试"));
				assertThat(result.getString(i, "dtcs_id"), is(String.valueOf(DTCS_ID)));
				assertThat(result.getString(i, "dtcs_name"), is("MYSQL"));
				assertThat(result.getString(i, "source_type"), is("number"));
				assertThat(result.getString(i, "target_type"), is("decimal"));
			} else if (DTC_ID + 1 == result.getLong(i, "dtc_id")) {
				assertThat(result.getString(i, "dtc_remark"), is("类型对照表测试"));
				assertThat(result.getString(i, "dtcs_id"), is(String.valueOf(DTCS_ID)));
				assertThat(result.getString(i, "dtcs_name"), is("MYSQL"));
				assertThat(result.getString(i, "source_type"), is("timestamp "));
				assertThat(result.getString(i, "target_type"), is("datetime"));
			}
		}
	}

	@Method(desc = "查询数据存储层数据类型对照以及长度对照表信息",
			logicStep = "1.正常的数据访问1，数据都正常,该方法只有一种情况")
	@Test
	public void searchDataLayerDataTypeLengthInfo() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.post(getActionUrl("searchDataLayerDataTypeLengthInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		checkLengthContrastData(result);
		// 2.正确的数据访问2，dlcs_id
		bodyString = new HttpClient()
				.addData("dlcs_id", DLCS_ID)
				.post(getActionUrl("searchDataLayerDataTypeLengthInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		result = ar.getDataForResult();
		checkLengthContrastData(result);

	}

	@Method(desc = "校验长度类型对照数据正确性", logicStep = "1.循环遍历结果集校验数据")
	private void checkLengthContrastData(Result result) {
		// 1.循环遍历结果集校验数据
		for (int i = 0; i < result.getRowCount(); i++) {
			if (DLC_ID == result.getLong(i, "dlc_id")) {
				assertThat(result.getString(i, "dlc_remark"), is("类型长度对照表测试"));
				assertThat(result.getLong(i, "dlcs_id"), is(DLCS_ID));
				assertThat(result.getString(i, "dlcs_name"), is("length_contrast_sum"));
				assertThat(result.getString(i, "dlc_type"), is("string"));
				assertThat(result.getInt(i, "dlc_length"), is(100));
			} else if (DLC_ID + 1 == result.getLong(i, "dlc_id")) {
				assertThat(result.getString(i, "dlc_remark"), is("类型长度对照表测试"));
				assertThat(result.getLong(i, "dlcs_id"), is(DLCS_ID));
				assertThat(result.getString(i, "dlcs_name"), is("length_contrast_sum"));
				assertThat(result.getString(i, "dlc_type"), is("number"));
				assertThat(result.getInt(i, "dlc_length"), is(20));
			}
		}
	}

	@Method(desc = "根据存储层定义表主键ID与存储层配置存储类型查询存储层属性信息",
			logicStep = "1.正确的数据访问1，数据都有效" +
					"2.错误的数据访问1，dsl_id不存在" +
					"3.错误的数据访问2，store_type不存在")
	@Test
	public void searchDataStoreLayerAttrByIdAndType() {
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("dsl_id", DSL_ID)
				.addData("store_type", Store_type.DATABASE.getCode())
				.post(getActionUrl("searchDataStoreLayerAttrByIdAndType"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		for (int i = 0; i < result.getRowCount(); i++) {
			String dsla_id = result.getString(i, "dsla_id");
			assertThat(result.getString(i, "dsl_id"), is(String.valueOf(DSL_ID)));
			if (dsla_id.equals(String.valueOf(DSLA_ID))) {
				assertThat(result.getString(i, "storage_property_key"), is(StorageTypeKey.database_type));
				assertThat(result.getString(i, "storage_property_val"), is(DatabaseType.Postgresql.getCode()));
			} else if (dsla_id.equals(String.valueOf(DSLA_ID + 1))) {
				assertThat(result.getString(i, "storage_property_key"), is(StorageTypeKey.database_driver));
				assertThat(result.getString(i, "storage_property_val"), is("org.postgresql.Driver"));
			} else if (dsla_id.equals(String.valueOf(DSLA_ID + 2))) {
				assertThat(result.getString(i, "storage_property_key"), is(StorageTypeKey.database_name));
				assertThat(result.getString(i, "storage_property_val"), is("hrsdxg"));
			} else if (dsla_id.equals(String.valueOf(DSLA_ID + 3))) {
				assertThat(result.getString(i, "storage_property_key"), is(StorageTypeKey.database_pwd));
				assertThat(result.getString(i, "storage_property_val"), is("hrsdxg"));
			} else {
				assertThat(result.getString(i, "storage_property_key"), is(StorageTypeKey.jdbc_url));
				assertThat(result.getString(i, "storage_property_val"), is("jdbc:postgresql://10.71.4.57:31001/hrsdxg"));
			}
		}
		// 2.错误的数据访问1，dsl_id不存在
		bodyString = new HttpClient()
				.addData("dsl_id", "100")
				.addData("store_type", Store_type.DATABASE.getCode())
				.post(getActionUrl("searchDataStoreLayerAttrByIdAndType"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForResult().getRowCount(), is(0));
		// 3.错误的数据访问2，store_type不存在
		bodyString = new HttpClient()
				.addData("dsl_id", DSL_ID)
				.addData("store_type", "9")
				.post(getActionUrl("searchDataStoreLayerAttrByIdAndType"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Test
	public void downloadConfFile() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("fileName", "hbase-site.xml")
				.addData("filePath", "E:\\tmp\\upfiles\\temp\\fb642ed8095e4ea0be671c39aa074646.xml")
				.post(getActionUrl("downloadConfFile"))
				.getBodyString();
		assertThat(bodyString, is(notNullValue()));
	}

	@Method(desc = "根据存储层类型获取数据存储层配置属性key",
			logicStep = "1.正确的数据访问1，数据都有效" +
					"2.错误的数据访问1，store_type为空" +
					"3.错误的数据访问2，store_type不存在")
	@Test
	public void getDataLayerAttrKey() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("store_type", Store_type.ElasticSearch.getCode())
				.post(getActionUrl("getDataLayerAttrKey"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		bodyString = new HttpClient()
				.addData("store_type", Store_type.HIVE.getCode())
				.post(getActionUrl("getDataLayerAttrKey"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		bodyString = new HttpClient()
				.addData("store_type", Store_type.DATABASE.getCode())
				.post(getActionUrl("getDataLayerAttrKey"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		bodyString = new HttpClient()
				.addData("store_type", Store_type.HIVE.getCode())
				.post(getActionUrl("getDataLayerAttrKey"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		// 2.错误的数据访问1，store_type为空
		bodyString = new HttpClient()
				.addData("store_type", "")
				.post(getActionUrl("getDataLayerAttrKey"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，store_type不存在
		bodyString = new HttpClient()
				.addData("store_type", "11")
				.post(getActionUrl("getDataLayerAttrKey"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Test
	public void getAttrKeyIsSupportExternalTable() {
		// 1.正常的数据访问1，数据都正常
		String bodyString = new HttpClient()
				.addData("store_type", Store_type.DATABASE.getCode())
				.addData("is_hadoopclient", IsFlag.Shi.getCode())
				.addData("database_type", DatabaseType.Postgresql.getCode())
				.post(getActionUrl("getAttrKeyIsSupportExternalTable"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
//		bodyString = new HttpClient()
//				.addData("store_type", Store_type.HIVE.getCode())
//				.post(getActionUrl("getAttrKeyIsSupportExternalTable"))
//				.getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
//				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
//		assertThat(ar.isSuccess(), is(true));
//		bodyString = new HttpClient()
//				.addData("store_type", Store_type.DATABASE.getCode())
//				.post(getActionUrl("getAttrKeyIsSupportExternalTable"))
//				.getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
//				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
//		assertThat(ar.isSuccess(), is(true));
//		bodyString = new HttpClient()
//				.addData("store_type", Store_type.HIVE.getCode())
//				.post(getActionUrl("getAttrKeyIsSupportExternalTable"))
//				.getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
//				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
//		assertThat(ar.isSuccess(), is(true));
//		// 2.错误的数据访问1，store_type为空
//		bodyString = new HttpClient()
//				.addData("store_type", "")
//				.post(getActionUrl("getAttrKeyIsSupportExternalTable"))
//				.getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
//				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
//		assertThat(ar.isSuccess(), is(false));
//		// 3.错误的数据访问2，store_type不存在
//		bodyString = new HttpClient()
//				.addData("store_type", "11")
//				.post(getActionUrl("getAttrKeyIsSupportExternalTable"))
//				.getBodyString();
//		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
//				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
//		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "测试完删除测试数据",
			logicStep = "1.测试完成后删除sys_user表测试数据" +
					"2.测试完删除department_info表测试数据" +
					"3.测试完删除Data_store_layer表测试数据" +
					"4.测试完删除Data_store_layer_added表测试数据" +
					"5.测试完删除Data_store_layer_attr表测试数据" +
					"6.测试完删除新增数据存储层配置数据" +
					"7.删除数据存储附件信息数据" +
					"8.删除新增数据存储层配置属性信息" +
					"9.提交事务")
	@After
	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 测试完删除Data_store_layer表测试数据
			SqlOperator.execute(db,
					"delete from " + Data_store_layer.TableName + " where dsl_id in (?,?,?,?,?,?)",
					DSL_ID, DSL_ID + 1, DSL_ID + 2, DSL_ID + 3, DSL_ID + 4, DSL_ID + 5);
			// 判断department_info数据是否被删除
			long num = SqlOperator.queryNumber(db,
					"select count(1) from " + Data_store_layer.TableName + " where dsl_id in (?,?,?,?,?,?)",
					DSL_ID, DSL_ID + 1, DSL_ID + 2, DSL_ID + 3, DSL_ID + 4, DSL_ID + 5)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除Data_store_layer_added表测试数据
			SqlOperator.execute(db,
					"delete from " + Data_store_layer_added.TableName + " where dsl_id in (?,?,?,?,?,?)",
					DSL_ID, DSL_ID + 1, DSL_ID + 2, DSL_ID + 3, DSL_ID + 4, DSL_ID + 5);
			// 判断Data_store_layer_added数据是否被删除
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Data_store_layer_added.TableName + " where dsl_id in (?,?,?,?,?,?)",
					DSL_ID, DSL_ID + 1, DSL_ID + 2, DSL_ID + 3, DSL_ID + 4, DSL_ID + 5)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName + " where dsl_id in (?,?,?,?,?,?)",
					DSL_ID, DSL_ID + 1, DSL_ID + 2, DSL_ID + 3, DSL_ID + 4, DSL_ID + 5);
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Data_store_layer_attr.TableName + " where dsl_id in (?,?,?,?,?,?)",
					DSL_ID, DSL_ID + 1, DSL_ID + 2, DSL_ID + 3, DSL_ID + 4, DSL_ID + 5)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除新增数据存储层配置数据
			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_name=?",
					"addDataStore1");
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer.TableName +
					"  where dsl_name=?", "addDataStore1").orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 删除数据存储附件信息数据
			SqlOperator.execute(db, "delete from " + Data_store_layer_added.TableName +
					" where dslad_remark=?", "新增数据存储附加信息");
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_added.TableName +
					"  where dslad_remark=?", "新增数据存储附加信息").orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 删除新增数据存储层配置属性信息
			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName +
					" where dsla_remark=?", "新增数据存储层配置属性信息1");
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_attr.TableName +
					"  where dsla_remark=?", "新增数据存储层配置属性信息1").orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName + " where dsla_remark=?",
					"新增数据存储层配置属性信息2");
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_attr.TableName +
					"  where dsla_remark=?", "新增数据存储层配置属性信息2").orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除类型对照主表信息
			SqlOperator.execute(db,
					"delete from " + Type_contrast_sum.TableName + " where dtcs_id in(?,?)",
					DTCS_ID, DTCS_ID + 1);
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Type_contrast_sum.TableName + " where dtcs_id in(?,?)",
					DTCS_ID, DTCS_ID + 1)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除类型对照表信息
			SqlOperator.execute(db,
					"delete from " + Type_contrast.TableName + " where dtcs_id in(?,?)",
					DTCS_ID, DTCS_ID + 1);
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Type_contrast.TableName + " where dtcs_id in(?,?)",
					DTCS_ID, DTCS_ID + 1)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除类型长度对照主表信息
			SqlOperator.execute(db,
					"delete from " + Length_contrast_sum.TableName + " where dlcs_id in (?,?)",
					DLCS_ID, DLCS_ID + 1);
			num = SqlOperator.queryNumber(db,
					"select count(1) from " + Length_contrast_sum.TableName + " where dlcs_id in (?,?)",
					DLCS_ID, DLCS_ID + 1)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 测试完删除类型长度对照表信息
			SqlOperator.execute(db,
					"delete from " + Length_contrast.TableName + " where dlcs_id=?", DLCS_ID);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Length_contrast.TableName +
					"  where dlcs_id=?", DLCS_ID).orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 删除新增类型对照以及长度对照表信息
			SqlOperator.execute(db,
					"delete from " + Length_contrast.TableName + " where dlc_remark=?",
					"类型长度对照表备注");
			num = SqlOperator.queryNumber(db, "select count(1) from " + Length_contrast.TableName +
					"  where dlc_remark=?", "类型长度对照表备注").orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Length_contrast_sum.TableName +
					" where dlcs_remark=?", "类型长度对照主表备注");
			num = SqlOperator.queryNumber(db, "select count(1) from " + Length_contrast_sum.TableName +
					"  where dlcs_remark=?", "类型长度对照主表备注").orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Type_contrast_sum.TableName +
					" where dtcs_remark=?", "类型对照主表备注");
			num = SqlOperator.queryNumber(db, "select count(1) from " + Type_contrast_sum.TableName +
					"  where dtcs_remark=?", "类型对照主表测试").orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Type_contrast.TableName +
					" where dtc_remark=?", "类型对照表备注");
			num = SqlOperator.queryNumber(db, "select count(1) from " + Type_contrast.TableName +
					"  where dtc_remark=?", "类型对照表备注").orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 删除上传配置文件
			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName +
					" where storage_property_key=?", "hdfs-site.xml");
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_attr.TableName +
					"  where storage_property_key=?", "hdfs-site.xml").orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName +
					" where storage_property_key=?", "hbase-site.xml");
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_attr.TableName +
					"  where storage_property_key=?", "hbase-site.xml").orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName +
					" where storage_property_key=?", "core-site.xml");
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_attr.TableName +
					"  where storage_property_key=?", "core-site.xml").orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 提交事务
			SqlOperator.commitTransaction(db);
		}

	}
}
