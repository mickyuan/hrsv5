package hrds.g.biz.serviceuser.impl;

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
import hrds.commons.utils.StorageTypeKey;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "服务接口测试类", author = "dhw", createdate = "2020/4/20 16:12")
public class ServiceInterfaceUserImplActionTest extends WebBaseTestCase {

	//请填写测试用户需要做登录验证的A项目的登录验证的接口
	private static final String LOGIN_URL = agentInitConfig.getString("login_url");
	// 已经存在的用户ID,用于模拟登录
	private static final long USER_ID = agentInitConfig.getLong("user_id");
	private static final String PASSWORD = agentInitConfig.getString("password");
	//当前线程的id
	private long THREAD_ID = Thread.currentThread().getId() * 1000000;
	// 接口使用ID
	private static final long INTERFACE_USE_ID = 3331L;
	// 接口使用日志ID
	private static final long LOG_ID = 22221L;
	// 文件ID
	private static final String fileID = "33333";
	// 存储ID
	private static final long StorageId = 10000001L;
	// agent ID
	private static final long AgentId = 44444L;
	// 数据源ID
	private static final long SourceId = 55555L;
	// 数据库设置ID
	private static final long DatabaseId = 66666L;
	private static final long TableId = 77777L;
	// 存储层ID
	private static final long DslId = 20000001L;
	// 存储层属性ID
	private static final long DslaId = 30000001L;

	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.初始化接口使用日志表测试数据
			Interface_use_log interface_use_log = new Interface_use_log();
			interface_use_log.setInterface_use_id(INTERFACE_USE_ID);
			interface_use_log.setLog_id(LOG_ID);
			interface_use_log.setResponse_time(1L);
			interface_use_log.setUser_id(USER_ID);
			interface_use_log.setBrowser_type("chrome");
			interface_use_log.setBrowser_version("80.0.3987.122");
			interface_use_log.setInterface_name("单表普通查询接口");
			interface_use_log.setUser_name("接口测试用户-dhw0");
			interface_use_log.setProtocol("3.13.1");
			interface_use_log.setSystem_type("windows");
			interface_use_log.setRequest_type("httpclient");
			interface_use_log.setRequest_stime(DateUtil.getSysTime());
			interface_use_log.setRequest_etime(DateUtil.getSysTime());
			interface_use_log.setRequest_state("NORMAL");
			interface_use_log.setRequest_mode("post");
			interface_use_log.setRequest_info("");
			interface_use_log.setRemoteaddr("127.0.0.1");
			assertThat("初始化接口使用日志表测试数据", interface_use_log.add(db), is(1));
			// 2.初始化数据存储登记表信息
			Data_store_reg dst = new Data_store_reg();
			for (int i = 0; i < 3; i++) {
				dst.setFile_id(fileID + i);
				dst.setCollect_type(AgentType.DBWenJian.getCode());
				dst.setOriginal_update_date(DateUtil.getSysDate());
				dst.setOriginal_update_time(DateUtil.getSysTime());
				dst.setOriginal_name("sys_user");
				dst.setTable_name("sys_user");
				dst.setHyren_name("sys_user");
				dst.setStorage_date(DateUtil.getSysDate());
				dst.setStorage_time(DateUtil.getSysTime());
				dst.setFile_size(20L);
				dst.setAgent_id(AgentId + i);
				dst.setSource_id(SourceId + i);
				dst.setDatabase_id(DatabaseId + i);
				dst.setTable_id(TableId + i);
				assertThat("初始化数据存储登记表信息", dst.add(db), is(1));
			}
			// 3.初始化表存储信息信息测试数据
			Table_storage_info tsi = new Table_storage_info();
			for (int i = 0; i < 3; i++) {
				tsi.setHyren_name("sys_user");
				tsi.setStorage_id(StorageId + i);
				tsi.setFile_format(FileFormat.FeiDingChang.getCode());
				tsi.setStorage_type(StorageType.ZengLiang.getCode());
				tsi.setIs_zipper(IsFlag.Shi.getCode());
				tsi.setStorage_time("0");
				tsi.setTable_id(TableId + i);
				assertThat("初始化表存储信息信息测试数据", tsi.add(db), is(1));
			}
			// 4.初始化数据表存储关系表测试数据
			Dtab_relation_store drt = new Dtab_relation_store();
			for (int i = 0; i < 3; i++) {
				drt.setData_source(StoreLayerDataSource.DB.getCode());
				drt.setTab_id(TableId + i);
				drt.setDsl_id(DslId + i);
				assertThat("初始化数据表存储关系表测试数据", drt.add(db), is(1));
			}
			// 5.初始化数据存储层配置表测试数据
			Data_store_layer dsl = new Data_store_layer();
			for (int i = 0; i < 3; i++) {
				dsl.setDsl_id(DslId + i);
				dsl.setDsl_name("test_dhw");
				dsl.setStore_type(Store_type.DATABASE.getCode());
				dsl.setIs_hadoopclient(IsFlag.Fou.getCode());
				assertThat("初始化数据存储层配置表测试数据", dsl.add(db), is(1));
			}
			// 6.初始化数据存储层配置属性表测试数据
			Data_store_layer_attr dsla = new Data_store_layer_attr();
			for (int i = 0; i < 6; i++) {
				dsla.setDsla_id(DslaId + i);
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
					dsla.setStorage_property_val("jdbc:postgresql://10.71.4.57:31001/hrsdxg");
				} else {
					dsla.setStorage_property_key(StorageTypeKey.database_name);
					dsla.setStorage_property_val("hrsdxg");
				}
				dsla.setIs_file(IsFlag.Fou.getCode());
				dsla.setDsl_id(DslId);
				assertThat("初始化数据存储层配置属性表测试数据", dsla.add(db), is(1));
			}
			// 提交事务
			SqlOperator.commitTransaction(db);
		}
	}

	@Test
	public void getToken() {
		// 1.正确的数据访问1，数据有效
		String bodyString = new HttpClient().buildSession()
				.addData("user_id", "2001")
				.addData("user_password", "1")
				.post(getActionUrl("getToken")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("token").toString(), is(notNullValue()));
	}

	@Test
	public void tableUsePermissions() {
	}

	@Test
	public void generalQuery() {
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

	@Test
	public void getIpAndPort() {
	}

	@After
	public void after() {
		DatabaseWrapper db = new DatabaseWrapper();
		try {
			//1.清理Interface_use_log表中造的数据
			SqlOperator.execute(db, "DELETE FROM " + Interface_use_log.TableName + " WHERE log_id = ?"
					, LOG_ID);

			SqlOperator.commitTransaction(db);
		} catch (Exception e) {
			db.rollback();
		} finally {
			db.close();
		}
	}
}
