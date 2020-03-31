package hrds.b.biz.websqlquery;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.AgentType;
import hrds.commons.entity.*;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class WebSqlQueryActionTest extends WebBaseTestCase {
	//部门id
	private static final long DEP_ID = 5000000000L;
	//数据源id
	private static final long SOURCE_ID = 5000000000L;
	//AGENT_ID
	private static final long AGENT_ID = 5000000000L;
	//登录用户id
	private static final long USER_ID = 5000L;
	//数据库任务设置id
	private static final long DATABASE_ID = 5000000000L;
	//集市id
	private static final long DATA_MART_ID = 5000000000L;
	//集市数据表id
	private static final long DATATABLE_ID = 5000000000L;
	//FILE_ID
	private static final String FILE_ID = "5000000000";

	private static String bodyString;
	private static ActionResult ar;

	@Method(desc = "初始化测试用例依赖表数据",
			logicStep = "1.初始化依赖表数据" +
					"1-1.初始化 Data_source 数据" +
					"1-2.初始化 Source_relation_dep 数据" +
					"1-3.初始化 Agent_info 数据" +
					"1-4.初始化 Database_set 数据" +
					"1-5.初始化 Data_mart_info 数据" +
					"1-6.初始化 Datatable_info 数据" +
					"1-7.初始化 Source_file_attribute 数据" +
					"2.模拟登陆" +
					"2-1.初始化模拟登陆了数据" +
					"2-1-1.初始化模拟登陆用户" +
					"2-1-2.初始化模拟登陆用户依赖部门" +
					"3.提交所有数据库执行操作" +
					"4.模拟登陆")
	@BeforeClass
	public static void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1.初始化依赖表数据
			//1-1.初始化 Data_source 数据
			hrds.commons.entity.Data_source dataSource = new Data_source();
			dataSource.setSource_id(SOURCE_ID);
			dataSource.setDatasource_number("init-hll");
			dataSource.setDatasource_name("测试数据源init-hll");
			dataSource.setSource_remark("测试数据源init-hll");
			dataSource.setCreate_date(DateUtil.getSysDate());
			dataSource.setCreate_time(DateUtil.getSysTime());
			dataSource.setCreate_user_id(USER_ID);
			dataSource.add(db);
			//1-2.初始化 Source_relation_dep 数据
			Source_relation_dep sourceRelationDep = new Source_relation_dep();
			sourceRelationDep.setDep_id(DEP_ID);
			sourceRelationDep.setSource_id(SOURCE_ID);
			sourceRelationDep.add(db);
			//1-3.初始化 Agent_info 数据
			Agent_info agentInfo = new Agent_info();
			agentInfo.setAgent_id(AGENT_ID + 1);
			agentInfo.setAgent_name("数据库Agent-init-hll");
			agentInfo.setAgent_type(AgentType.ShuJuKu.getCode());
			agentInfo.setAgent_ip("127.0.0.1");
			agentInfo.setAgent_port("8888");
			agentInfo.setAgent_status("1");
			agentInfo.setCreate_date(DateUtil.getSysDate());
			agentInfo.setCreate_time(DateUtil.getSysTime());
			agentInfo.setSource_id(SOURCE_ID);
			agentInfo.setUser_id(USER_ID);
			agentInfo.add(db);
			agentInfo.setAgent_id(AGENT_ID + 2);
			agentInfo.setAgent_name("DB文件Agent-init-hll");
			agentInfo.setAgent_type(AgentType.DBWenJian.getCode());
			agentInfo.setAgent_ip("127.0.0.1");
			agentInfo.setAgent_port("8888");
			agentInfo.setAgent_status("1");
			agentInfo.setCreate_date(DateUtil.getSysDate());
			agentInfo.setCreate_time(DateUtil.getSysTime());
			agentInfo.setSource_id(SOURCE_ID);
			agentInfo.setUser_id(USER_ID);
			agentInfo.add(db);
			//1-4.初始化 Database_set 数据
			Database_set databaseSet = new Database_set();
			databaseSet.setDatabase_id(DATABASE_ID + 1);
			databaseSet.setAgent_id(AGENT_ID + 1);
			databaseSet.setHost_name("数据库采集任务测试init-hll");
			databaseSet.setDatabase_number("数据库设置编号1");
			databaseSet.setSystem_type("00");
			databaseSet.setTask_name("数据库采集任务测试init-hll");
			databaseSet.setDatabase_name("postgres");
			databaseSet.setDatabase_pad("postgres");
			databaseSet.setDatabase_drive("数据库驱动");
			databaseSet.setDatabase_type("0");
			databaseSet.setUser_name("登录用户名");
			databaseSet.setDatabase_ip("127.0.0.1");
			databaseSet.setDatabase_port("31001");
			databaseSet.setDb_agent("0");
//			databaseSet.setDbfile_format("0");
//			databaseSet.setFile_suffix("文件后缀名");
//			databaseSet.setIs_load("0");
//			databaseSet.setDatabase_code("0");
			databaseSet.setDatabase_separatorr("数据采用分隔符");
			databaseSet.setRow_separator("数据行分隔符");
//			databaseSet.setIs_hidden("0");
			databaseSet.setPlane_url("DB文件源数据路径");
			databaseSet.setIs_sendok("1");
//			databaseSet.setIs_header("0");
			databaseSet.setCp_or("清洗顺序");
			databaseSet.setJdbc_url("数据库连接地址");
			databaseSet.setClassify_id("9999");
			databaseSet.add(db);
			databaseSet.setDatabase_id(DATABASE_ID + 2);
			databaseSet.setAgent_id(AGENT_ID + 2);
			databaseSet.setHost_name("DB文件采集任务测试init-hll");
			databaseSet.setDatabase_number("数据库设置编号2");
			databaseSet.setSystem_type("01");
			databaseSet.setTask_name("DB文件采集任务测试init-hll");
			databaseSet.setDatabase_name("postgres");
			databaseSet.setDatabase_pad("postgres");
			databaseSet.setDatabase_drive("1");
			databaseSet.setDatabase_type("1");
			databaseSet.setUser_name("登录用户名");
			databaseSet.setDatabase_ip("127.0.0.1");
			databaseSet.setDatabase_port("31001");
			databaseSet.setDb_agent("1");
//			databaseSet.setDbfile_format("1");
//			databaseSet.setFile_suffix("文件后缀名");
//			databaseSet.setIs_load("1");
//			databaseSet.setDatabase_code("1");
			databaseSet.setDatabase_separatorr("数据采用分隔符");
			databaseSet.setRow_separator("数据行分隔符");
//			databaseSet.setIs_hidden("1");
			databaseSet.setPlane_url("DB文件源数据路径");
			databaseSet.setIs_sendok("1");
//			databaseSet.setIs_header("1");
			databaseSet.setCp_or("清洗顺序");
			databaseSet.setJdbc_url("数据库连接地址");
			databaseSet.setClassify_id("9999");
			databaseSet.add(db);
			//1-5.初始化 Data_mart_info 数据
//			Data_mart_info dataMartInfo = new Data_mart_info();
//			dataMartInfo.setData_mart_id(DATA_MART_ID);
//			dataMartInfo.setMart_name("测试集市名称init-hll");
//			dataMartInfo.setMart_number("测试集市编号init-hll");
//			dataMartInfo.setMart_desc("测试集市说明init-hll");
//			dataMartInfo.setMart_storage_path("数据集市存储路径");
//			dataMartInfo.setCreate_date(DateUtil.getSysDate());
//			dataMartInfo.setCreate_time(DateUtil.getSysTime());
//			dataMartInfo.setCreate_id(USER_ID);
//			dataMartInfo.setRemark("测试集市备注init-hll");
//			dataMartInfo.add(db);
//			//1-6.初始化 Datatable_info 数据
//			Datatable_info datatableInfo = new Datatable_info();
//			datatableInfo.setDatatable_id(DATATABLE_ID);
//			datatableInfo.setData_mart_id(DATA_MART_ID);
//			datatableInfo.setDatatable_cn_name("数据表中文名称");
//			datatableInfo.setDatatable_en_name("sjb_init_hll");
//			datatableInfo.setDatatable_desc("数据表描述");
//			datatableInfo.setDatatable_create_date(DateUtil.getSysDate());
//			datatableInfo.setDatatable_create_time(DateUtil.getSysTime());
//			datatableInfo.setDatatable_due_date(DateUtil.getSysDate());
//			datatableInfo.setDdlc_date(DateUtil.getSysDate());
//			datatableInfo.setDdlc_time(DateUtil.getSysTime());
//			datatableInfo.setDatac_date(DateUtil.getSysDate());
//			datatableInfo.setDatac_time(DateUtil.getSysTime());
//			datatableInfo.setDatatable_lifecycle("0");
//			datatableInfo.setIs_partition("0");
//			datatableInfo.setSoruce_size("1");
//			datatableInfo.setIs_hyren_db("0");
//			datatableInfo.setHy_success("0");
//			datatableInfo.setIs_kv_db("0");
//			datatableInfo.setKv_success("0");
//			datatableInfo.setIs_solr_db("0");
//			datatableInfo.setSolr_success("0");
//			datatableInfo.setIs_elk_db("0");
//			datatableInfo.setElk_success("0");
//			datatableInfo.setIs_solr_hbase("0");
//			datatableInfo.setSolrbase_success("0");
//			datatableInfo.setIs_carbondata_db("0");
//			datatableInfo.setCarbondata_success("0");
//			datatableInfo.setRowkey_separator("_");
//			datatableInfo.setPre_partition("col1");
//			datatableInfo.setEtl_date(DateUtil.getSysDate());
//			datatableInfo.setSql_engine("0");
//			datatableInfo.setDatatype("0");
//			datatableInfo.setIs_append("0");
//			datatableInfo.setIs_current_cluster("0");
//			datatableInfo.setIs_data_file("0");
//			datatableInfo.setExfile_success("0");
//			datatableInfo.setRemark("0");
//			datatableInfo.add(db);
			//1-7.初始化 Source_file_attribute 数据
			Source_file_attribute sourceFileAttribute = new Source_file_attribute();
			sourceFileAttribute.setFile_id(FILE_ID);
			sourceFileAttribute.setIs_in_hbase("0");
			sourceFileAttribute.setSeqencing(1L);
			sourceFileAttribute.setCollect_type("0");
			sourceFileAttribute.setOriginal_name("原始文件名或表中文名称");
			sourceFileAttribute.setOriginal_update_date(DateUtil.getSysDate());
			sourceFileAttribute.setOriginal_update_time(DateUtil.getSysTime());
			sourceFileAttribute.setTable_name("采集的原始表名");
			sourceFileAttribute.setHbase_name("系统内对应表名");
			sourceFileAttribute.setMeta_info("META元信息");
			sourceFileAttribute.setStorage_date(DateUtil.getSysDate());
			sourceFileAttribute.setStorage_time(DateUtil.getSysTime());
			sourceFileAttribute.setFile_size("1");
			sourceFileAttribute.setFile_type("文件类型");
			sourceFileAttribute.setFile_suffix("文件后缀");
			sourceFileAttribute.setFile_md5("MD5");

			sourceFileAttribute.setFile_avro_path("所在avro文件地址");
			sourceFileAttribute.setFile_avro_block(1024L);
			sourceFileAttribute.setIs_big_file("0");
			sourceFileAttribute.setIs_cache("0");
			sourceFileAttribute.setFolder_id(10L);
			sourceFileAttribute.setAgent_id(AGENT_ID);
			sourceFileAttribute.setSource_id(SOURCE_ID);
			sourceFileAttribute.setCollect_set_id(DATABASE_ID + 1);
			sourceFileAttribute.add(db);
			//2.模拟登陆
			//2-1.初始化模拟登陆数据
			//2-1-1.初始化模拟登陆用户
			Sys_user sysUser = new Sys_user();
			sysUser.setUser_id(USER_ID);
			sysUser.setCreate_id(1000L);
			sysUser.setDep_id(DEP_ID);
			sysUser.setRole_id(1001L);
			sysUser.setUser_name("模拟登陆测试用户init-hll");
			sysUser.setUser_password("111111");
			sysUser.setUseris_admin("0");
			sysUser.setUser_state("0");
			sysUser.setCreate_date(DateUtil.getSysDate());
			sysUser.setToken("0");
			sysUser.setValid_time(DateUtil.getSysTime());
			sysUser.add(db);
			//2-1-1.初始化模拟登陆用户依赖部门
			Department_info departmentInfo = new Department_info();
			departmentInfo.setDep_id(DEP_ID);
			departmentInfo.setDep_name("init-hll");
			departmentInfo.setCreate_date(DateUtil.getSysDate());
			departmentInfo.setCreate_time(DateUtil.getSysTime());
			departmentInfo.add(db);
			//3.提交所有数据库执行操作
			SqlOperator.commitTransaction(db);
			//4.根据初始化的 Sys_user 用户模拟登陆
			bodyString = new HttpClient()
					.addData("user_id", USER_ID)
					.addData("password", "111111")
					.post("http://127.0.0.1:8099/A/action/hrds/a/biz/login/login").getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
			assertThat(ar.isSuccess(), is(true));
		}
	}

	@Method(desc = "测试案例执行完成后清理测试数据",
			logicStep = "1.清理测试数据" +
					"1-1.删除 Data_source 表测试数据" +
					"1-2.删除 Source_relation_dep 测试数据" +
					"1-3.删除 Agent_info 测试数据" +
					"1-4.删除 Sys_user 表测试数据" +
					"1-5.删除 Department_info 表测试数据" +
					"1-6.删除 Database_set 表测试数据" +
					"1-7.删除 Data_mart_info 数据" +
					"1-8.删除 Datatable_info 数据")
	@AfterClass
	public static void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1.清理测试数据
			//1-1.删除 Data_source 表测试数据
			SqlOperator.execute(db,
					"delete from " + Data_source.TableName + " where source_id=?", SOURCE_ID);
			SqlOperator.commitTransaction(db);
			long dsDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Data_source.TableName + " where source_id =?",
					SOURCE_ID
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("data_source 表此条数据删除后,记录数应该为0", dsDataNum, is(0L));
			//1-2.删除 Source_relation_dep 表测试数据
			SqlOperator.execute(db,
					"delete from " + Source_relation_dep.TableName + " where source_id=?", DEP_ID);
			SqlOperator.commitTransaction(db);
			long srdDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Source_relation_dep.TableName + " where" +
							" source_id=?", DEP_ID
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("source_relation_dep 表此条数据删除后,记录数应该为0", srdDataNum, is(0L));
			//1-3.删除 Agent_info 表测试数据
			SqlOperator.execute(db,
					"delete from " + Agent_info.TableName + " where agent_id=?",
					AGENT_ID + 1);
			SqlOperator.execute(db,
					"delete from " + Agent_info.TableName + " where agent_id=?",
					AGENT_ID + 2);
			SqlOperator.commitTransaction(db);
			long aiDataNum;
			aiDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Agent_info.TableName + " where agent_id=?",
					AGENT_ID + 1
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("agent_info 表此条数据删除后,记录数应该为0", aiDataNum, is(0L));
			aiDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Agent_info.TableName + " where agent_id=?",
					AGENT_ID + 2
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("agent_info 表此条数据删除后,记录数应该为0", aiDataNum, is(0L));
			//1-4.删除 Sys_user 表测试数据
			SqlOperator.execute(db,
					"delete from " + Sys_user.TableName + " where user_id=?", USER_ID);
			SqlOperator.commitTransaction(db);
			long suNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Sys_user.TableName + " where user_id =?",
					USER_ID
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("sys_user 表此条数据删除后,记录数应该为0", suNum, is(0L));
			//1-5.删除 Department_info 表测试数据
			SqlOperator.execute(db,
					"delete from " + Department_info.TableName + " where dep_id=?", DEP_ID);
			SqlOperator.commitTransaction(db);
			long depNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Department_info.TableName + " where dep_id =?",
					DEP_ID
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("department_info 表此条数据删除后,记录数应该为0", depNum, is(0L));
			//1-6.删除 Database_set 表测试数据
			SqlOperator.execute(db,
					"delete from " + Database_set.TableName + " where classify_id=?", 9999);
			SqlOperator.commitTransaction(db);
			long dsNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Database_set.TableName + " where classify_id =?",
					9999
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("Database_set 表此条数据删除后,记录数应该为0", dsNum, is(0L));
			//1-7.删除 Data_mart_info 数据
//			SqlOperator.execute(db,
//					"delete from " + Data_mart_info.TableName + " where data_mart_id=?", DATA_MART_ID);
//			SqlOperator.commitTransaction(db);
//			long dmiNum = SqlOperator.queryNumber(db,
//					"select count(1) from " + Data_mart_info.TableName + " where data_mart_id =?",
//					DATA_MART_ID
//			).orElseThrow(() -> new RuntimeException("count fail!"));
//			assertThat("Data_mart_info 表此条数据删除后,记录数应该为0", dmiNum, is(0L));
//			//1-8.删除 Datatable_info 数据
//			SqlOperator.execute(db,
//					"delete from " + Datatable_info.TableName + " where datatable_id=?", DATATABLE_ID);
//			SqlOperator.commitTransaction(db);
//			long dtNum = SqlOperator.queryNumber(db,
//					"select count(1) from " + Datatable_info.TableName + " where datatable_id =?",
//					DATATABLE_ID
//			).orElseThrow(() -> new RuntimeException("count fail!"));
//			assertThat("Datatable_info 表此条数据删除后,记录数应该为0", dtNum, is(0L));
			//1-9.删除 Source_file_attribute 数据
			SqlOperator.execute(db,
					"delete from " + Source_file_attribute.TableName + " where file_id=?", FILE_ID);
			SqlOperator.commitTransaction(db);
			long sfaNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Source_file_attribute.TableName + " where file_id =?",
					FILE_ID
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("Source_file_attribute 表此条数据删除后,记录数应该为0", sfaNum, is(0L));
		}
	}

	@Method(desc = "获取登录用户部门下的数据库采集和DB文件采集所有任务信息",
			logicStep = "1.正确数据访问" +
					"1-1.无参数访问,校验返回结果")
	@Test
	public void getCollectionTaskInfo() {
		//1.正确数据访问
		//1-1.无参数访问.校验返回结果
		bodyString = new HttpClient()
				.post(getActionUrl("getCollectionTaskInfo")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		for (int i = 0; i < ar.getDataForResult().getRowCount(); i++) {
			assertThat(ar.getDataForResult().getString(i, "datasource_name"),
					is("测试数据源init-hll"));
			assertThat(ar.getDataForResult().getLong(i, "source_id"), is(SOURCE_ID));
			List<Map<String, Object>> databaseCollectionTaskRs =
					(List<Map<String, Object>>) ar.getDataForResult().getObject(i,
							"databaseCollectionTaskRs");
			for (int i1 = 0; i1 < databaseCollectionTaskRs.size(); i1++) {
				assertThat(databaseCollectionTaskRs.get(i1).get("task_name"),
						is("数据库采集任务测试init-hll"));
				assertThat(databaseCollectionTaskRs.get(i1).get("database_id"), is(5000000001L));
			}
			List<Map<String, Object>> dbFileCollectionTaskRs = (List<Map<String, Object>>) ar.getDataForResult()
					.getObject(i, "dbFileCollectionTaskRs");
			for (int i1 = 0; i1 < dbFileCollectionTaskRs.size(); i1++) {
				assertThat(dbFileCollectionTaskRs.get(i1).get("task_name"), is("DB文件采集任务测试init-hll"));
				assertThat(dbFileCollectionTaskRs.get(i1).get("database_id"), is(5000000002L));
			}
		}
	}

	@Method(desc = "获取登录用户部门下的数据集市表信息",
			logicStep = "1.正确数据访问" +
					"1-1.无参数访问,校验返回结果")
	@Test
	public void getTablesFromMarket() {
		//1.正确数据访问
		//1-1.无参数访问.校验返回结果
		bodyString = new HttpClient()
				.post(getActionUrl("getTablesFromMarket")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		for (int i = 0; i < ar.getDataForResult().getRowCount(); i++) {
			assertThat(ar.getDataForResult().getLong(i, "data_mart_id"), is(5000000000L));
			List<Map<String, Object>> marketTables = (List<Map<String, Object>>) ar.getDataForResult().getObject(i,
					"marketTables");
			for (int i1 = 0; i1 < marketTables.size(); i1++) {
				assertThat(marketTables.get(i1).get("datatable_id"), is(5000000000L));
				assertThat(marketTables.get(i1).get("datatable_en_name"), is("sjb_init_hll"));
				assertThat(marketTables.get(i1).get("datatable_cn_name"), is("数据表中文名称"));
			}
		}
	}

	@Method(desc = "根据数据库设置id获取表信息",
			logicStep = "1.正确数据访问" +
					"1-1.集市任务id存在" +
					"2.错误数据访问" +
					"2-1.集市任务id不存在")
	@Test
	public void getTableInfoByCollectSetId() {
		//1.正确数据访问
		//1-1.集市任务id存在
		bodyString = new HttpClient()
				.addData("collect_set_id", DATABASE_ID + 1)
				.post(getActionUrl("getTableInfoByCollectSetId")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		for (int i = 0; i < ar.getDataForEntityList(Source_file_attribute.class).size(); i++) {
			assertThat(ar.getDataForEntityList(Source_file_attribute.class).get(i).getFile_id(), is(FILE_ID));
			assertThat(ar.getDataForEntityList(Source_file_attribute.class).get(i).getAgent_id(), is(AGENT_ID));
			assertThat(ar.getDataForEntityList(Source_file_attribute.class).get(i).getSource_id(), is(SOURCE_ID));
			assertThat(ar.getDataForEntityList(Source_file_attribute.class).get(i).getCollect_set_id(),
					is(DATABASE_ID + 1));
		}

	}
}
