package hrds.a.biz.datastore;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.netclient.http.SubmitMediaType;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
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

	// 初始化数据存储层配置ID
	private static final long dsl_id1 = 100001;
	private static final long dsl_id2 = 100002;
	private static final long dsl_id3 = 100003;
	private static final long dsl_id4 = 100004;
	private static final long dsl_id5 = 100005;
	private static final long dsl_id6 = 100006;
	// 初始化附加信息ID
	private static final long dslad_id1 = 200001;
	private static final long dslad_id2 = 200002;
	private static final long dslad_id3 = 200003;
	private static final long dslad_id4 = 200004;
	private static final long dslad_id5 = 200005;
	private static final long dslad_id6 = 200006;
	// 初始化存储层配置属性信息
	private static final long dsla_id1 = 300001;
	private static final long dsla_id2 = 300002;
	private static final long dsla_id3 = 300003;
	private static final long dsla_id4 = 300004;
	private static final long dsla_id5 = 300005;
	private static final long dsla_id6 = 300006;
	private static final long dsla_id7 = 300007;
	// 初始化存储层数据类型对照主表ID
	private static final long dtcs_id = 400001;
	private static final long dtcs_id2 = 400002;
	// 初始化存储层数据类型长度对照主表ID
	private static final long dlcs_id = 500001;
	private static final long dlcs_id2 = 500002;
	// 初始化存储层数据类型对照表ID
	private static final long dtc_id = 600001;
	private static final long dtc_id2 = 600002;
	// 初始化存储层数据类型长度对照表ID
	private static final long dlc_id = 700001;
	private static final long dlc_id2 = 700002;
	// 初始化登录用户ID
	private static final long UserId = 6666L;
	// 初始化创建用户ID
	private static final long CreateId = 1000L;
	// 测试部门ID dep_id,测试作业调度部门
	private static final long DepId = 1000011L;

	@Method(desc = "构造初始化表测试数据",
			logicStep = "1.构造sys_user表测试数据" +
					"2.构造department_info部门表测试数据" +
					"3.初始化数据存储层配置表数据" +
					"4.初始化数据存储附加信息表数据" +
					"5.初始化数据存储层配置属性表数据" +
					"6.提交事务" +
					"7.模拟用户登录" +
					"测试数据：" +
					"1.sys_user表1条数据，user_id为UserID" +
					"2.department_info表，有1条数据，dep_id为DepID" +
					"3.Data_store_layer表有5条数据，dsl_id为dsl_id1--dsl_id5" +
					"4.Data_store_layer_added有6条数据，dslad_id为dslad_id1--dslad_id6" +
					"5.Data_store_layer_attr表有6条数据，dsla_id为dsla_id1--dsla_id6")
	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.构造sys_user表测试数据
			Sys_user sysUser = new Sys_user();
			sysUser.setUser_id(UserId);
			sysUser.setCreate_id(CreateId);
			sysUser.setDep_id(DepId);
			sysUser.setCreate_date(DateUtil.getSysDate());
			sysUser.setCreate_time(DateUtil.getSysTime());
			sysUser.setRole_id("1001");
			sysUser.setUser_name("作业配置功能测试");
			sysUser.setUser_password("1");
			sysUser.setUser_type(UserType.CaiJiYongHu.getCode());
			sysUser.setUseris_admin("1");
			sysUser.setUsertype_group("02,03,04,08");
			sysUser.setUser_state(IsFlag.Shi.getCode());
			int num = sysUser.add(db);
			assertThat("测试数据sys_user数据初始化", num, is(1));
			// 2.构造department_info部门表测试数据
			Department_info department_info = new Department_info();
			department_info.setDep_id(DepId);
			department_info.setDep_name("测试作业调度部门");
			department_info.setCreate_date(DateUtil.getSysDate());
			department_info.setCreate_time(DateUtil.getSysTime());
			department_info.setDep_remark("测试");
			num = department_info.add(db);
			assertThat("测试数据department_info初始化", num, is(1));
			// 3.初始化数据存储层配置表数据
			Data_store_layer dataStoreLayer = new Data_store_layer();
			for (int i = 1; i <= 6; i++) {
				if (i == 1) {
					dataStoreLayer.setDsl_id(dsl_id1);
					dataStoreLayer.setStore_type(Store_type.DATABASE.getCode());
					dataStoreLayer.setIs_hadoopclient(IsFlag.Fou.getCode());
				} else if (i == 2) {
					dataStoreLayer.setDsl_id(dsl_id2);
					dataStoreLayer.setStore_type(Store_type.HBASE.getCode());
					dataStoreLayer.setIs_hadoopclient(IsFlag.Shi.getCode());
				} else if (i == 3) {
					dataStoreLayer.setDsl_id(dsl_id3);
					dataStoreLayer.setStore_type(Store_type.SOLR.getCode());
					dataStoreLayer.setIs_hadoopclient(IsFlag.Shi.getCode());
				} else if (i == 4) {
					dataStoreLayer.setDsl_id(dsl_id4);
					dataStoreLayer.setStore_type(Store_type.ElasticSearch.getCode());
					dataStoreLayer.setIs_hadoopclient(IsFlag.Shi.getCode());
				} else if (i == 5) {
					dataStoreLayer.setDsl_id(dsl_id5);
					dataStoreLayer.setStore_type(Store_type.MONGODB.getCode());
					dataStoreLayer.setIs_hadoopclient(IsFlag.Shi.getCode());
				} else if (i == 6) {
					dataStoreLayer.setDsl_id(dsl_id6);
					dataStoreLayer.setStore_type(Store_type.DATABASE.getCode());
					dataStoreLayer.setIs_hadoopclient(IsFlag.Shi.getCode());
				}
				dataStoreLayer.setDsl_name("数据存储层配置测试名称" + i);
				dataStoreLayer.setDsl_remark("数据存储层配置测试" + i);
				dataStoreLayer.setDtcs_id(dtcs_id);
				dataStoreLayer.setDlcs_id(dlcs_id);
				dataStoreLayer.add(db);
			}
			// 4.初始化数据存储附加信息表数据
			Data_store_layer_added dataStoreLayerAdded = new Data_store_layer_added();
			for (int i = 1; i <= 6; i++) {
				if (i == 1) {
					dataStoreLayerAdded.setDslad_id(dslad_id1);
					dataStoreLayerAdded.setDsl_id(dsl_id1);
					dataStoreLayerAdded.setDsla_storelayer(StoreLayerAdded.ZhuJian.getCode());
				} else if (i == 2) {
					dataStoreLayerAdded.setDslad_id(dslad_id2);
					dataStoreLayerAdded.setDsl_id(dsl_id1);
					dataStoreLayerAdded.setDsla_storelayer(StoreLayerAdded.RowKey.getCode());
				} else if (i == 3) {
					dataStoreLayerAdded.setDslad_id(dslad_id3);
					dataStoreLayerAdded.setDsl_id(dsl_id3);
					dataStoreLayerAdded.setDsla_storelayer(StoreLayerAdded.SuoYinLie.getCode());
				} else if (i == 4) {
					dataStoreLayerAdded.setDslad_id(dslad_id4);
					dataStoreLayerAdded.setDsl_id(dsl_id4);
					dataStoreLayerAdded.setDsla_storelayer(StoreLayerAdded.YuJuHe.getCode());
				} else if (i == 5) {
					dataStoreLayerAdded.setDslad_id(dslad_id5);
					dataStoreLayerAdded.setDsl_id(dsl_id5);
					dataStoreLayerAdded.setDsla_storelayer(StoreLayerAdded.PaiXuLie.getCode());
				} else if (i == 6) {
					dataStoreLayerAdded.setDslad_id(dslad_id6);
					dataStoreLayerAdded.setDsl_id(dsl_id2);
					dataStoreLayerAdded.setDsla_storelayer(StoreLayerAdded.FenQuLie.getCode());
				}
				dataStoreLayerAdded.setDslad_remark("数据存储附加信息测试" + i);
				dataStoreLayerAdded.add(db);
			}
			// 5.初始化数据存储层配置属性表数据
			Data_store_layer_attr dataStoreLayerAttr = new Data_store_layer_attr();
			for (int i = 1; i <= 7; i++) {
				if (i == 1) {
					dataStoreLayerAttr.setDsla_id(dsla_id1);
					dataStoreLayerAttr.setDsl_id(dsl_id1);
					dataStoreLayerAttr.setStorage_property_key("数据库");
					dataStoreLayerAttr.setStorage_property_val(DatabaseType.Postgresql.getCode());
				} else if (i == 2) {
					dataStoreLayerAttr.setDsla_id(dsla_id2);
					dataStoreLayerAttr.setDsl_id(dsl_id1);
					dataStoreLayerAttr.setStorage_property_key("数据库驱动");
					dataStoreLayerAttr.setStorage_property_val("org.postgresql.Driver");
				} else if (i == 3) {
					dataStoreLayerAttr.setDsla_id(dsla_id3);
					dataStoreLayerAttr.setDsl_id(dsl_id2);
					dataStoreLayerAttr.setStorage_property_key("数据库服务器IP");
					dataStoreLayerAttr.setStorage_property_val("127.0.0.1");
				} else if (i == 4) {
					dataStoreLayerAttr.setDsla_id(dsla_id4);
					dataStoreLayerAttr.setDsl_id(dsl_id3);
					dataStoreLayerAttr.setStorage_property_key("数据库端口");
					dataStoreLayerAttr.setStorage_property_val("5432");
				} else if (i == 5) {
					dataStoreLayerAttr.setDsla_id(dsla_id5);
					dataStoreLayerAttr.setDsl_id(dsl_id4);
					dataStoreLayerAttr.setStorage_property_key("用户名");
					dataStoreLayerAttr.setStorage_property_val("hrsdxg");
				} else if (i == 6) {
					dataStoreLayerAttr.setDsla_id(dsla_id6);
					dataStoreLayerAttr.setDsl_id(dsl_id5);
					dataStoreLayerAttr.setStorage_property_key("密码");
					dataStoreLayerAttr.setStorage_property_val("hrsdxg");
				} else if (i == 7) {
					dataStoreLayerAttr.setDsla_id(dsla_id7);
					dataStoreLayerAttr.setDsl_id(dsl_id6);
					dataStoreLayerAttr.setStorage_property_key("数据库服务器IP");
					dataStoreLayerAttr.setStorage_property_val("10.71.4.51");
				}
				dataStoreLayerAttr.setDsla_remark("数据存储层配置属性测试" + i);
				dataStoreLayerAttr.setIs_file(IsFlag.Fou.getCode());
				dataStoreLayerAttr.add(db);
			}
			// 6.初始化类型对照主表信息
			Type_contrast_sum type_contrast_sum = new Type_contrast_sum();
			for (int i = 1; i < 3; i++) {
				if (i == 1) {
					type_contrast_sum.setDtcs_id(dtcs_id);
					type_contrast_sum.setDtcs_name("MYSQL");
				} else {
					type_contrast_sum.setDtcs_id(dtcs_id2);
					type_contrast_sum.setDtcs_name("ORACLE");
				}
				type_contrast_sum.setDtcs_remark("类型对照主表测试");
				type_contrast_sum.add(db);
			}
			// 7.初始化类型长度对照主表信息
			Length_contrast_sum length_contrast_sum = new Length_contrast_sum();
			for (int i = 1; i < 3; i++) {
				if (i == 1) {
					length_contrast_sum.setDlcs_id(dlcs_id);
					length_contrast_sum.setDlcs_name("length_contrast_sum");
				} else {
					length_contrast_sum.setDlcs_id(dlcs_id2);
					length_contrast_sum.setDlcs_name("length_contrast");
				}
				length_contrast_sum.setDlcs_remark("类型长度对照主表测试");
				length_contrast_sum.add(db);
			}
			// 8.初始化类型对照表信息
			Type_contrast type_contrast = new Type_contrast();
			for (int i = 0; i < 2; i++) {
				if (i == 0) {
					type_contrast.setDtc_id(dtc_id);
					type_contrast.setSource_type("number");
					type_contrast.setTarget_type("decimal");
				} else {
					type_contrast.setDtc_id(dtc_id2);
					type_contrast.setSource_type("timestamp ");
					type_contrast.setTarget_type("datetime");
				}
				type_contrast.setDtcs_id(dtcs_id);
				type_contrast.setDtc_remark("类型对照表测试");
				type_contrast.add(db);
			}
			// 8.初始化类型长度对照表信息
			Length_contrast length_contrast = new Length_contrast();
			for (int i = 0; i < 2; i++) {
				if (i == 0) {
					length_contrast.setDlc_id(dlc_id);
					length_contrast.setDlc_type("string");
					length_contrast.setDlc_length("100");
				} else {
					length_contrast.setDlc_id(dlc_id2);
					length_contrast.setDlc_type("number");
					length_contrast.setDlc_length("20");
				}
				length_contrast.setDlcs_id(dlcs_id);
				length_contrast.setDlc_remark("类型长度对照表测试");
				length_contrast.add(db);
			}
			// 9.提交事务
			SqlOperator.commitTransaction(db);
			// 10.模拟用户登录
			String responseValue = new HttpClient()
					.buildSession()
					.addData("user_id", UserId)
					.addData("password", "1")
					.post("http://127.0.0.1:8888/A/action/hrds/a/biz/login/login")
					.getBodyString();
			Optional<ActionResult> ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class);
			assertThat("用户登录", ar.get().isSuccess(), is(true));
		}
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
			// 1.测试完成后删除sys_user表测试数据
			SqlOperator.execute(db, "delete from " + Sys_user.TableName + " where user_id=?", UserId);
			// 判断sys_user数据是否被删除
			long num = SqlOperator.queryNumber(db, "select count(1) from " + Sys_user.TableName +
					"  where user_id=?", UserId).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 2.测试完删除department_info表测试数据
			SqlOperator.execute(db, "delete from " + Department_info.TableName + " where dep_id=?",
					DepId);
			// 判断department_info数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Department_info.TableName +
					"  where dep_id=?", DepId).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 3.测试完删除Data_store_layer表测试数据
			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id=?",
					dsl_id1);
			// 判断department_info数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer.TableName +
					"  where dsl_id=?", dsl_id1).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id=?",
					dsl_id2);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer.TableName +
					"  where dsl_id=?", dsl_id2).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id=?",
					dsl_id3);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer.TableName +
					"  where dsl_id=?", dsl_id3).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id=?",
					dsl_id4);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer.TableName +
					"  where dsl_id=?", dsl_id4).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id=?",
					dsl_id5);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer.TableName +
					"  where dsl_id=?", dsl_id5).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id=?",
					dsl_id6);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer.TableName +
					"  where dsl_id=?", dsl_id6).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 4.测试完删除Data_store_layer_added表测试数据
			SqlOperator.execute(db, "delete from " + Data_store_layer_added.TableName + " where dsl_id=?",
					dsl_id1);
			// 判断Data_store_layer_added数据是否被删除
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_added.TableName +
					"  where dsl_id=?", dsl_id1).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Data_store_layer_added.TableName + " where dsl_id=?",
					dsl_id2);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_added.TableName +
					"  where dsl_id=?", dsl_id2).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Data_store_layer_added.TableName + " where dsl_id=?",
					dsl_id3);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_added.TableName +
					"  where dsl_id=?", dsl_id3).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Data_store_layer_added.TableName + " where dsl_id=?",
					dsl_id4);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_added.TableName +
					"  where dsl_id=?", dsl_id4).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Data_store_layer_added.TableName + " where dsl_id=?",
					dsl_id5);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_added.TableName +
					"  where dsl_id=?", dsl_id5).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 5.测试完删除Data_store_layer_attr表测试数据
			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName + " where dsl_id=?",
					dsl_id1);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_attr.TableName +
					"  where dsl_id=?", dsl_id1).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName + " where dsl_id=?",
					dsl_id2);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_attr.TableName +
					"  where dsl_id=?", dsl_id2).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName + " where dsl_id=?",
					dsl_id3);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_attr.TableName +
					"  where dsl_id=?", dsl_id3).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName + " where dsl_id=?",
					dsl_id4);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_attr.TableName +
					"  where dsl_id=?", dsl_id4).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName + " where dsl_id=?",
					dsl_id5);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_attr.TableName +
					"  where dsl_id=?", dsl_id5).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName + " where dsl_id=?",
					dsl_id6);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_attr.TableName +
					"  where dsl_id=?", dsl_id6).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 6.测试完删除新增数据存储层配置数据
			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_name=?",
					"addDataStore1");
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer.TableName +
					"  where dsl_name=?", "addDataStore1").orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 7.删除数据存储附件信息数据
			SqlOperator.execute(db, "delete from " + Data_store_layer_added.TableName +
					" where dslad_remark=?", "新增数据存储附加信息");
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_added.TableName +
					"  where dslad_remark=?", "新增数据存储附加信息").orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 8.删除新增数据存储层配置属性信息
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
			// 9.测试完删除类型对照主表信息
			SqlOperator.execute(db, "delete from " + Type_contrast_sum.TableName +
					" where dtcs_id=?", dtcs_id);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Type_contrast_sum.TableName +
					"  where dtcs_id=?", dtcs_id).orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Type_contrast_sum.TableName +
					" where dtcs_id=?", dtcs_id2);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Type_contrast_sum.TableName +
					"  where dtcs_id=?", dtcs_id2).orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 10.测试完删除类型对照表信息
			SqlOperator.execute(db, "delete from " + Type_contrast.TableName +
					" where dtcs_id=?", dtcs_id);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Type_contrast.TableName +
					"  where dtcs_id=?", dtcs_id).orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 11.测试完删除类型长度对照主表信息
			SqlOperator.execute(db, "delete from " + Length_contrast_sum.TableName +
					" where dlcs_id=?", dlcs_id);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Length_contrast_sum.TableName +
					"  where dlcs_id=?", dlcs_id).orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			SqlOperator.execute(db, "delete from " + Length_contrast_sum.TableName +
					" where dlcs_id=?", dlcs_id2);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Length_contrast_sum.TableName +
					"  where dlcs_id=?", dlcs_id2).orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 12.测试完删除类型长度对照表信息
			SqlOperator.execute(db, "delete from " + Length_contrast.TableName +
					" where dlcs_id=?", dlcs_id);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Length_contrast.TableName +
					"  where dlcs_id=?", dlcs_id).orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));
			// 13.删除新增类型对照以及长度对照表信息
			SqlOperator.execute(db, "delete from " + Length_contrast.TableName +
					" where dlc_remark=?", "类型长度对照表备注");
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
			// 13.删除上传配置文件
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
			// 14.提交事务
			SqlOperator.commitTransaction(db);
		}

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
		List<Map<String, Object>> list = new ArrayList<>();
		File file = FileUtil.getFile("src//test//java//hrds//a//biz//datastore//upload");
		for (int i = 0; i < 2; i++) {
			Map<String, Object> map = new HashMap<>();
			if (i == 0) {
				map.put("storage_property_key", "数据库");
				map.put("storage_property_val", DatabaseType.Postgresql.getCode());
				map.put("dsla_remark", "新增数据存储层配置属性信息1");
				map.put("is_file", IsFlag.Fou.getCode());
			} else {
				map.put("storage_property_key", "数据库驱动");
				map.put("storage_property_val", "org.postgresql.Driver");
				map.put("dsla_remark", "新增数据存储层配置属性信息2");
				map.put("is_file", IsFlag.Fou.getCode());
			}
			list.add(map);
		}
		String bodyString = new HttpClient()
				.reset(SubmitMediaType.MULTIPART)
				.addData("dsl_name", "addDataStore1")
				.addData("store_type", Store_type.DATABASE.getCode())
				.addData("dsl_remark", "新增数据存储层配置信息")
				.addData("dsla_storelayer", new String[]{StoreLayerAdded.ZhuJian.getCode(),
						StoreLayerAdded.SuoYinLie.getCode()})
				.addData("dslad_remark", "新增数据存储附加信息")
				.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
				.addData("is_hadoopclient", IsFlag.Shi.getCode())
				.addData("dtcs_id", dtcs_id)
				.addData("dlcs_id", dlcs_id)
				.addFile("files", Objects.requireNonNull(file.listFiles()))
				.addData("dsla_remark", "上传hive配置文件")
				.post(getActionUrl("addDataStore"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Data_store_layer dataStoreLayer = SqlOperator.queryOneObject(db, Data_store_layer.class,
					"select * from " + Data_store_layer.TableName + " where dsl_name=?",
					"addDataStore1").orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(Store_type.DATABASE.getCode(), is(dataStoreLayer.getStore_type()));
			assertThat("新增数据存储层配置信息", is(dataStoreLayer.getDsl_remark()));
			assertThat(IsFlag.Shi.getCode(), is(dataStoreLayer.getIs_hadoopclient()));
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
					assertThat("新增数据存储层配置属性信息1", is(layerAttr.getDsla_remark()));
					assertThat(DatabaseType.Postgresql.getCode(), is(layerAttr.getStorage_property_val()));
				} else if (layerAttr.getStorage_property_key().equals("数据库驱动")) {
					assertThat(IsFlag.Fou.getCode(), is(layerAttr.getIs_file()));
					assertThat("新增数据存储层配置属性信息2", is(layerAttr.getDsla_remark()));
					assertThat("org.postgresql.Driver", is(layerAttr.getStorage_property_val()));
				} else if (layerAttr.getStorage_property_key().equals("core-site.xml")) {
					assertThat(IsFlag.Shi.getCode(), is(layerAttr.getIs_file()));
					assertThat("core-site.xml文件已上传", is(layerAttr.getDsla_remark()));
					Files.delete(new File(layerAttr.getStorage_property_val()).toPath());
				} else if (layerAttr.getStorage_property_key().equals("hbase-site.xml")) {
					assertThat(IsFlag.Shi.getCode(), is(layerAttr.getIs_file()));
					assertThat("hbase-site.xml文件已上传", is(layerAttr.getDsla_remark()));
					Files.delete(new File(layerAttr.getStorage_property_val()).toPath());
				} else if (layerAttr.getStorage_property_key().equals("hdfs-site.xml")) {
					assertThat(IsFlag.Shi.getCode(), is(layerAttr.getIs_file()));
					assertThat("hdfs-site.xml文件已上传", is(layerAttr.getDsla_remark()));
					Files.delete(new File(layerAttr.getStorage_property_val()).toPath());
				}
			}
			// 2.错误的数据访问1，dsl_name为空
			bodyString = new HttpClient()
					.addData("dsl_name", "")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "新增数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
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
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
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
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
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
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
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
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
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
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
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
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
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
			List<Map<String, String>> list = new ArrayList<>();
			for (int i = 0; i < 2; i++) {
				Map<String, String> map = new HashMap<>();
				if (i == 0) {
					map.put("dsla_id", String.valueOf(dsla_id1));
					map.put("storage_property_key", "数据库");
					map.put("storage_property_val", DatabaseType.MYSQL.getCode());
					map.put("dsla_remark", "更新数据存储层配置属性信息1");
				} else {
					map.put("dsla_id", String.valueOf(dsla_id2));
					map.put("storage_property_key", "数据库驱动");
					map.put("storage_property_val", "com.mysql.jdbc.Driver");
					map.put("dsla_remark", "更新数据存储层配置属性信息2");
				}
				map.put("is_file", IsFlag.Fou.getCode());
				list.add(map);
			}
			File file = FileUtil.getFile("src//test//java//hrds//a//biz//datastore//upload");
			String bodyString = new HttpClient()
					.reset(SubmitMediaType.MULTIPART)
					.addData("dsl_id", dsl_id1)
					.addData("dslad_id", dslad_id1)
					.addData("dsla_id", dsla_id1)
					.addData("dsl_name", "upDataStore1")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dsla_storelayer", new String[]{StoreLayerAdded.FenQuLie.getCode(),
							StoreLayerAdded.PaiXuLie.getCode()})
					.addData("dslad_remark", "更新数据存储附加信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addFile("files", Objects.requireNonNull(file.listFiles()))
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
					.addData("dsla_remark", "hbase配置文件上传")
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			Data_store_layer layer = SqlOperator.queryOneObject(db, Data_store_layer.class,
					"select * from " + Data_store_layer.TableName + " where dsl_id=?", dsl_id1)
					.orElseThrow(() -> new BusinessException("sql查询错误"));
			assertThat(Store_type.HBASE.getCode(), is(layer.getStore_type()));
			assertThat("更新数据存储层配置信息", is(layer.getDsl_remark()));
			assertThat(IsFlag.Shi.getCode(), is(layer.getIs_hadoopclient()));
			List<Data_store_layer_added> layerAddeds = SqlOperator.queryList(db, Data_store_layer_added.class,
					"select * from " + Data_store_layer_added.TableName + " where dsl_id=? " +
							"order by dsla_storelayer", dsl_id1);
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
					.addData("dslad_id", dslad_id2)
					.addData("dsla_id", dsla_id2)
					.addData("dsl_name", "upDataStore2")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 3.错误的数据访问2，dsl_id为空格
			bodyString = new HttpClient()
					.addData("dsl_id", " ")
					.addData("dslad_id", dslad_id2)
					.addData("dsla_id", dsla_id2)
					.addData("dsl_name", "upDataStore3")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 4.错误的数据访问3，dsl_id不存在
			bodyString = new HttpClient()
					.addData("dsl_id", 1)
					.addData("dslad_id", dslad_id1)
					.addData("dsla_id", dsla_id1)
					.addData("dsl_name", "upDataStore4")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 5.错误的数据访问4，dslad_id为空
			bodyString = new HttpClient()
					.addData("dsl_id", dsl_id1)
					.addData("dslad_id", "")
					.addData("dsla_id", dsla_id1)
					.addData("dsl_name", "upDataStore5")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 6.错误的数据访问5，dslad_id为空格
			bodyString = new HttpClient()
					.addData("dsl_id", dsl_id1)
					.addData("dslad_id", " ")
					.addData("dsla_id", dsla_id1)
					.addData("dsl_name", "upDataStore6")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 7.错误的数据访问6，dslad_id为不存在
			bodyString = new HttpClient()
					.addData("dsl_id", dsl_id1)
					.addData("dslad_id", 1)
					.addData("dsla_id", dsla_id1)
					.addData("dsl_name", "upDataStore7")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 8.错误的数据访问7，dsla_id为空
			bodyString = new HttpClient()
					.addData("dsl_id", dsl_id1)
					.addData("dslad_id", dslad_id1)
					.addData("dsla_id", "")
					.addData("dsl_name", "upDataStore8")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 9.错误的数据访问8，dslad_id为空格
			bodyString = new HttpClient()
					.addData("dsl_id", dsl_id1)
					.addData("dslad_id", dslad_id1)
					.addData("dsla_id", " ")
					.addData("dsl_name", "upDataStore9")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 10.错误的数据访问9，dslad_id为不存在
			bodyString = new HttpClient()
					.addData("dsl_id", dsl_id1)
					.addData("dslad_id", dslad_id1)
					.addData("dsla_id", 1)
					.addData("dsl_name", "upDataStore10")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 11.错误的数据访问10，dsl_name为空
			bodyString = new HttpClient()
					.addData("dsl_id", dsl_id1)
					.addData("dslad_id", dslad_id1)
					.addData("dsla_id", dsla_id1)
					.addData("dsl_name", "")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 12.错误的数据访问11，dsl_name为空格
			bodyString = new HttpClient()
					.addData("dsl_id", dsl_id1)
					.addData("dslad_id", dslad_id1)
					.addData("dsla_id", dsla_id1)
					.addData("dsl_name", " ")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 13.错误的数据访问12，store_type为空
			bodyString = new HttpClient()
					.addData("dsl_id", dsl_id1)
					.addData("dslad_id", dslad_id1)
					.addData("dsla_id", dsla_id1)
					.addData("dsl_name", "upDataStore13")
					.addData("store_type", "")
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 14.错误的数据访问13，store_type为空格
			bodyString = new HttpClient()
					.addData("dsl_id", dsl_id1)
					.addData("dslad_id", dslad_id1)
					.addData("dsla_id", dsla_id1)
					.addData("dsl_name", "upDataStore14")
					.addData("store_type", " ")
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 15.错误的数据访问14，store_type为不存在
			bodyString = new HttpClient()
					.addData("dsl_id", dsl_id1)
					.addData("dslad_id", dslad_id1)
					.addData("dsla_id", dsla_id1)
					.addData("dsl_name", "upDataStore15")
					.addData("store_type", 6)
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", JsonUtil.toJson(list))
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 16.错误的数据访问15，dataStoreLayerAttr为空
			bodyString = new HttpClient()
					.addData("dsl_id", dsl_id1)
					.addData("dslad_id", dslad_id1)
					.addData("dsla_id", dsla_id1)
					.addData("dsl_name", "upDataStore13")
					.addData("store_type", Store_type.DATABASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", "")
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
					.post(getActionUrl("updateDataStore"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(false));
			// 17.错误的数据访问16，dataStoreLayerAttr为空格
			bodyString = new HttpClient()
					.addData("dsl_id", dsl_id1)
					.addData("dslad_id", dslad_id1)
					.addData("dsla_id", dsla_id1)
					.addData("dsl_name", "upDataStore14")
					.addData("store_type", Store_type.HBASE.getCode())
					.addData("dsl_remark", "更新数据存储层配置信息")
					.addData("dataStoreLayerAttr", " ")
					.addData("is_hadoopclient", IsFlag.Shi.getCode())
					.addData("is_file", IsFlag.Fou.getCode())
					.addData("dtcs_id", dtcs_id)
					.addData("dlcs_id", dlcs_id)
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
					Data_store_layer.TableName + " where dsl_id = ?", dsl_id5);
			assertThat("删除操作前，保证Data_store_layer表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(1L));
			String bodyString = new HttpClient()
					.addData("dsl_id", dsl_id5)
					.post(getActionUrl("deleteDataStore"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期删除的数据存在
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Data_store_layer.TableName + " where dsl_id = ?", dsl_id5);
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
			if (dsl_id == dsl_id1) {
				assertThat(Store_type.DATABASE.getCode(), is(storeLayer.getString(i, "store_type")));
				assertThat("数据存储层配置测试名称1", is(storeLayer.getString(i, "dsl_name")));
				assertThat("数据存储层配置测试1", is(storeLayer.getString(i, "dsl_remark")));
			} else if (dsl_id == dsl_id2) {
				assertThat(Store_type.HBASE.getCode(), is(storeLayer.getString(i, "store_type")));
				assertThat("数据存储层配置测试名称2", is(storeLayer.getString(i, "dsl_name")));
				assertThat("数据存储层配置测试2", is(storeLayer.getString(i, "dsl_remark")));
			} else if (dsl_id == dsl_id3) {
				assertThat(Store_type.SOLR.getCode(), is(storeLayer.getString(i, "store_type")));
				assertThat("数据存储层配置测试名称3", is(storeLayer.getString(i, "dsl_name")));
				assertThat("数据存储层配置测试3", is(storeLayer.getString(i, "dsl_remark")));
			} else if (dsl_id == dsl_id4) {
				assertThat(Store_type.ElasticSearch.getCode(), is(storeLayer.getString(i, "store_type")));
				assertThat("数据存储层配置测试名称4", is(storeLayer.getString(i, "dsl_name")));
				assertThat("数据存储层配置测试4", is(storeLayer.getString(i, "dsl_remark")));
			} else if (dsl_id == dsl_id5) {
				assertThat(Store_type.MONGODB.getCode(), is(storeLayer.getString(i, "store_type")));
				assertThat("数据存储层配置测试名称5", is(storeLayer.getString(i, "dsl_name")));
				assertThat("数据存储层配置测试5", is(storeLayer.getString(i, "dsl_remark")));
			} else if (dsl_id == dsl_id6) {
				assertThat(Store_type.DATABASE.getCode(), is(storeLayer.getString(i, "store_type")));
				assertThat("数据存储层配置测试名称6", is(storeLayer.getString(i, "dsl_name")));
				assertThat("数据存储层配置测试6", is(storeLayer.getString(i, "dsl_remark")));
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
				.addData("dsl_id", dsl_id1)
				.post(getActionUrl("searchDataStoreById"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> dataForMap = ar.getDataForMap();
		List<Map<String, Object>> layerAndAdded = (List<Map<String, Object>>) dataForMap.get("layerAndAdded");
		List<Map<String, Object>> layerAndAttr = (List<Map<String, Object>>) dataForMap.get("layerAndAttr");
		assertThat(dataForMap.get("store_type"), is(Store_type.DATABASE.getCode()));
		assertThat("数据存储层配置测试名称1", is(dataForMap.get("dsl_name")));
		assertThat("数据存储层配置测试1", is(dataForMap.get("dsl_remark")));
		for (Map<String, Object> map : layerAndAdded) {
			String dslad_id = map.get("dslad_id").toString();
			if (dslad_id.equals(String.valueOf(dslad_id1))) {
				assertThat(String.valueOf(dsl_id1), is(map.get("dsl_id").toString()));
				assertThat(StoreLayerAdded.ZhuJian.getCode(), is(map.get("dsla_storelayer")));
				assertThat("数据存储附加信息测试1", is(map.get("dslad_remark")));
			} else if (dslad_id.equals(String.valueOf(dslad_id2))) {
				assertThat(String.valueOf(dsl_id1), is(map.get("dsl_id").toString()));
				assertThat(StoreLayerAdded.RowKey.getCode(), is(map.get("dsla_storelayer")));
				assertThat("数据存储附加信息测试2", is(map.get("dslad_remark")));
			}
		}
		for (Map<String, Object> map : layerAndAttr) {
			String dsla_id = map.get("dsla_id").toString();
			if (dsla_id.equals(String.valueOf(dsla_id1))) {
				assertThat(map.get("dsl_id").toString(), is(String.valueOf(dsl_id1)));
				assertThat(map.get("storage_property_key"), is("数据库"));
				assertThat(map.get("storage_property_val"), is(DatabaseType.Postgresql.getCode()));
				assertThat(map.get("dsla_remark"), is("数据存储层配置属性测试1"));
			} else if (dsla_id.equals(String.valueOf(dsla_id2))) {
				assertThat(map.get("dsl_id").toString(), is(String.valueOf(dsl_id1)));
				assertThat(map.get("storage_property_key"), is("数据库驱动"));
				assertThat(map.get("storage_property_val"), is("org.postgresql.Driver"));
				assertThat(map.get("dsla_remark"), is("数据存储层配置属性测试2"));
			}
		}
		// 2.正确的数据访问2，数据存储属性信息不存在
		bodyString = new HttpClient()
				.addData("dsl_id", dsl_id6)
				.post(getActionUrl("searchDataStoreById"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		layerAndAdded = (List<Map<String, Object>>) dataForMap.get("layerAndAdded");
		layerAndAttr = (List<Map<String, Object>>) dataForMap.get("layerAndAttr");
		assertThat(dataForMap.get("store_type"), is(Store_type.DATABASE.getCode()));
		assertThat("数据存储层配置测试名称6", is(dataForMap.get("dsl_name")));
		assertThat("数据存储层配置测试6", is(dataForMap.get("dsl_remark")));
		assertThat(layerAndAdded.isEmpty(), is(true));
		for (Map<String, Object> map : layerAndAttr) {
			assertThat(map.get("dsla_id").toString(), is(String.valueOf(dsla_id7)));
			assertThat(map.get("dsl_id").toString(), is(String.valueOf(dsl_id6)));
			assertThat(map.get("storage_property_key"), is("数据库服务器IP"));
			assertThat(map.get("storage_property_val"), is("10.71.4.51"));
			assertThat(map.get("dsla_remark"), is("数据存储层配置属性测试7"));
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
					.addData("dtcs_id", dtcs_id)
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
					"select * from " + Type_contrast_sum.TableName + " where dtcs_id=?", dtcs_id)
					.orElseThrow(() -> new BusinessException("sql查询错误！"));
			assertThat(typeContrastSum.getDtcs_name(), is("MYSQL"));
			assertThat(typeContrastSum.getDtcs_remark(), is("更新类型对照主表备注"));
			List<Type_contrast> typeContrastList = SqlOperator.queryList(db, Type_contrast.class,
					"select * from " + Type_contrast.TableName + " where dtcs_id=?", dtcs_id);
			for (Type_contrast type_contrast : typeContrastList) {
				if (type_contrast.getTarget_type().equals("int")) {
					assertThat(type_contrast.getSource_type(), is("number"));
				} else if (type_contrast.getTarget_type().equals("varchar")) {
					assertThat(type_contrast.getSource_type(), is("text"));
				} else {
					assertThat(type_contrast.getSource_type(), is("CHAR"));
				}
				assertThat(type_contrast.getDtcs_id(), is(dtcs_id));
				assertThat(type_contrast.getDtc_remark(), is("更新类型对照表备注"));
			}
			// 2.错误的数据访问1，dtcs_name为空
			bodyString = new HttpClient()
					.addData("dtcs_id", dtcs_id)
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
					.addData("dtcs_id", dtcs_id)
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
					.addData("dtcs_id", dtcs_id)
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
					.addData("dtcs_id", dtcs_id)
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
					Type_contrast_sum.TableName + " where dtcs_id = ?", dtcs_id);
			assertThat("删除操作前，保证Data_store_layer表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(1L));
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Type_contrast.TableName + " where dtcs_id = ?", dtcs_id);
			assertThat("删除操作前，保证Data_store_layer表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(2L));
			String bodyString = new HttpClient()
					.addData("dtcs_id", dtcs_id)
					.post(getActionUrl("deleteDataTypeContrastInfo"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期删除的数据存在
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Type_contrast_sum.TableName + " where dtcs_id = ?", dtcs_id);
			assertThat("删除操作后，确认该条数据被删除", optionalLong.orElse(Long.MIN_VALUE),
					is(0L));
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Type_contrast.TableName + " where dtcs_id = ?", dtcs_id);
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
					.addData("dlcs_id", dlcs_id)
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
					dlcs_id).orElseThrow(() -> new BusinessException("sql查询错误！"));
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
					.addData("dlcs_id", dlcs_id)
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
					.addData("dlcs_id", dlcs_id)
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
					.addData("dlcs_id", dlcs_id)
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
					.addData("dlcs_id", dlcs_id)
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
					Length_contrast_sum.TableName + " where dlcs_id = ?", dlcs_id);
			assertThat("删除操作前，保证Data_store_layer表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(1L));
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Length_contrast.TableName + " where dlcs_id = ?", dlcs_id);
			assertThat("删除操作前，保证Data_store_layer表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(2L));
			String bodyString = new HttpClient()
					.addData("dlcs_id", dlcs_id)
					.post(getActionUrl("deleteTypeLengthContrastInfo"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期删除的数据存在
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Length_contrast_sum.TableName + " where dlcs_id = ?", dlcs_id);
			assertThat("删除操作后，确认该条数据被删除", optionalLong.orElse(Long.MIN_VALUE),
					is(0L));
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Length_contrast.TableName + " where dlcs_id = ?", dlcs_id);
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
			if (String.valueOf(dlcs_id).equals(lengthContrast.get("dlcs_id").toString())) {
				assertThat(lengthContrast.get("dlcs_remark"), is("类型长度对照主表测试"));
				assertThat(lengthContrast.get("dlcs_name"), is("length_contrast_sum"));
			} else if (String.valueOf(dlcs_id2).equals(lengthContrast.get("dlcs_id").toString())) {
				assertThat(lengthContrast.get("dlcs_name"), is("length_contrast"));
				assertThat(lengthContrast.get("dlcs_remark"), is("类型长度对照主表测试"));
			}
		}
		List<Map<String, Object>> typeContrastList = JsonUtil.toObject(map.get("typeContrastSumList").toString(),
				type);
		for (Map<String, Object> typeContrast : typeContrastList) {
			if (String.valueOf(dtcs_id).equals(typeContrast.get("dtcs_id").toString())) {
				assertThat(typeContrast.get("dtcs_remark"), is("类型对照主表测试"));
				assertThat(typeContrast.get("dtcs_name"), is("MYSQL"));
			} else if (String.valueOf(dtcs_id2).equals(typeContrast.get("dtcs_id").toString())) {
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
				.addData("dtcs_id", dtcs_id)
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
			if (dtc_id == result.getLong(i, "dtc_id")) {
				assertThat(result.getString(i, "dtc_remark"), is("类型对照表测试"));
				assertThat(result.getString(i, "dtcs_id"), is(String.valueOf(dtcs_id)));
				assertThat(result.getString(i, "dtcs_name"), is("MYSQL"));
				assertThat(result.getString(i, "source_type"), is("number"));
				assertThat(result.getString(i, "target_type"), is("decimal"));
			} else if (dtc_id2 == result.getLong(i, "dtc_id")) {
				assertThat(result.getString(i, "dtc_remark"), is("类型对照表测试"));
				assertThat(result.getString(i, "dtcs_id"), is(String.valueOf(dtcs_id)));
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
				.addData("dlcs_id", dlcs_id)
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
			if (dlc_id == result.getLong(i, "dlc_id")) {
				assertThat(result.getString(i, "dlc_remark"), is("类型长度对照表测试"));
				assertThat(result.getLong(i, "dlcs_id"), is(dlcs_id));
				assertThat(result.getString(i, "dlcs_name"), is("length_contrast_sum"));
				assertThat(result.getString(i, "dlc_type"), is("string"));
				assertThat(result.getInt(i, "dlc_length"), is(100));
			} else if (dlc_id2 == result.getLong(i, "dlc_id")) {
				assertThat(result.getString(i, "dlc_remark"), is("类型长度对照表测试"));
				assertThat(result.getLong(i, "dlcs_id"), is(dlcs_id));
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
				.addData("dsl_id", dsl_id1)
				.addData("store_type", Store_type.DATABASE.getCode())
				.post(getActionUrl("searchDataStoreLayerAttrByIdAndType"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！！"));
		assertThat(ar.isSuccess(), is(true));
		Result result = ar.getDataForResult();
		assertThat(result.getRowCount(), is(2));
		for (int i = 0; i < result.getRowCount(); i++) {
			assertThat(result.getLong(i, "dsla_id"), is(dsla_id1 + i));
			assertThat(result.getLong(i, "dsl_id"), is(dsl_id1));
			if (i == 0) {
				assertThat(result.getString(i, "storage_property_key"), is("数据库"));
				assertThat(result.getString(i, "storage_property_val"), is("11"));
				assertThat(result.getString(i, "dsla_remark"), is("数据存储层配置属性测试1"));
			} else {
				assertThat(result.getString(i, "storage_property_key"), is("数据库驱动"));
				assertThat(result.getString(i, "storage_property_val"), is("org.postgresql.Driver"));
				assertThat(result.getString(i, "dsla_remark"), is("数据存储层配置属性测试2"));
			}
			assertThat(result.getString(i, "is_file"), is(IsFlag.Fou.getCode()));
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
				.addData("dsl_id", dsl_id1)
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
}
