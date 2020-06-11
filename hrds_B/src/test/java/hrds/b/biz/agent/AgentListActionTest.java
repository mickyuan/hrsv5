package hrds.b.biz.agent;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.b.biz.agent.dbagentconf.BaseInitData;
import hrds.commons.codes.AgentStatus;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.CleanType;
import hrds.commons.codes.CountNum;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.DataExtractType;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.FillingType;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.ObjectCollectType;
import hrds.commons.codes.StorageType;
import hrds.commons.codes.StoreLayerAdded;
import hrds.commons.codes.Store_type;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_job_classify;
import hrds.commons.entity.Column_clean;
import hrds.commons.entity.Column_merge;
import hrds.commons.entity.Column_split;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Data_store_layer;
import hrds.commons.entity.Data_store_layer_added;
import hrds.commons.entity.Data_store_layer_attr;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Dcol_relation_store;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Dtab_relation_store;
import hrds.commons.entity.File_collect_set;
import hrds.commons.entity.File_source;
import hrds.commons.entity.Ftp_collect;
import hrds.commons.entity.Object_collect;
import hrds.commons.entity.Orig_code_info;
import hrds.commons.entity.Sys_user;
import hrds.commons.entity.Table_column;
import hrds.commons.entity.Table_info;
import hrds.commons.entity.Table_storage_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.testbase.WebBaseTestCase;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//TODO 日志文件下载的测试用例暂无
//TODO 调用工具类生成作业/发送任务测试用例暂无
@DocClass(desc = "AgentListAction的单元测试类", author = "WangZhengcheng")
public class AgentListActionTest extends WebBaseTestCase {

	//测试数据用户ID
	private static final long TEST_USER_ID = 9991L;
	//测试用户密码
	private static final String TEST_USER_PASSWORD = "test_user";
	//测试部门ID
	private static final long TEST_DEPT_ID = 9987L;
	//source_id
	private static final long SOURCE_ID = 1L;
	//数据库agent_id
	private static final long DB_AGENT_ID = 7001L;
	//数据文件agent_id
	private static final long DF_AGENT_ID = 7002L;
	//FTPagent_id
	private static final long FTP_AGENT_ID = 7003L;
	//半结构化agent_id
	private static final long HALF_STRUCT_AGENT_ID = 7004L;
	//非结构化agent_id
	private static final long NON_STRUCT_AGENT_ID = 7005L;
	//数据库直连采集表id
	private static final long TABLE_ID = 100201L;
	private static final long FIRST_CLASSIFY_ID = 10086L;
	private static final long SECOND_CLASSIFY_ID = 10010L;

	private static final long SYS_USER_TABLE_ID = 7001L;
	private static final long CODE_INFO_TABLE_ID = 7002L;

	private static final JSONObject tableCleanOrder = BaseInitData.initTableCleanOrder();
	private static final JSONObject columnCleanOrder = BaseInitData.initColumnCleanOrder();

	private static final long BASE_SYS_USER_PRIMARY = 2000L;

	private static final long BASE_EXTRACTION_DEF_ID = 7788L;

	private static final long BASE_TB_STORAGE_ID = 10669588L;

	private static final long BASE_LAYER_ID = 4399L;

	private static final long UNEXPECTED_ID = 999999999L;

	private static final long BASE_LAYER_ARR_ID = 43999L;

	private static final long DATA_STORE_LAYER_ADDED_ID = 439999L;


	/**
	 * 为每个方法的单元测试初始化测试数据
	 *
	 * 1、构建数据源Agent列表信息测试数据
	 *      1-1、构建数据源表(data_source)测试数据
	 *      1-2、构建Agent信息表(agent_info)测试数据
	 * 2、构建agent下任务的信息测试数据
	 *      2-1、构建database_set表测试数据
	 *      2-2、构建object_collect表测试数据
	 *      2-3、构建ftp_collect表测试数据
	 *      2-4、构建file_collect_set表测试数据
	 *      2-5、插入数据
	 * 3、构建各种采集任务相关测试数据
	 *      3-1、构建数据库直连采集测试数据
	 *          3-1-1、构建table_info(数据库对应表)测试数据
	 *          3-1-2、构建table_column(表对应字段)测试数据
	 *      3-2、构建非结构化文件采集测试数据
	 *          3-2-1、构建file_source(文件源设置)测试数据
	 * 测试数据：
	 *      1、data_source表：有1条数据，source_id为1
	 *      2、Agent_info表：有5条数据,代表5中不同的Agent类型，agent_id分别为7001-7005,source_id为1
	 *      3、database_set表：有2条数据,database_id为1001,1002, agent_id分别为7001,7002，即1001是数据库采集，1002是数据文件采集
	 *      4、object_collect表：有2条数据,oc_id为2001,2002, agent_id为7004
	 *      5、ftp_collect表：有2条数据,ftp_id为3001,3002, agent_id为7003
	 *      6、file_collect_set表：有2条数据,fcs_id为4001,4002, agent_id为7005
	 *      7、table_info表：有一条数据，table_id为100201，database_id为1002
	 *      8、table_column表：有10条数据，column_id为100200-100209，table_id为100201
	 *      9、file_source表：有2条数据，file_source_id为400100,400101，fcs_id为4001
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Before
	public void before() {

		//构建用户登录信息
		Sys_user user = new Sys_user();
		user.setUser_id(TEST_USER_ID);
		user.setCreate_id(TEST_USER_ID);
		user.setDep_id(TEST_DEPT_ID);
		user.setRole_id("1001");
		user.setUser_name("超级管理员init-wzc");
		user.setUser_password(TEST_USER_PASSWORD);
		// 0：管理员，1：操作员
		user.setUseris_admin("0");
		user.setUser_type("00");
		user.setUsertype_group(null);
		user.setLogin_ip("127.0.0.1");
		user.setLogin_date("20191001");
		user.setUser_state("1");
		user.setCreate_date(DateUtil.getSysDate());
		user.setCreate_time(DateUtil.getSysTime());
		user.setUpdate_date(DateUtil.getSysDate());
		user.setUpdate_time(DateUtil.getSysTime());
		user.setToken("0");
		user.setValid_time("0");

		//构建测试部门信息
		Department_info deptInfo = new Department_info();
		deptInfo.setDep_id(TEST_DEPT_ID);
		deptInfo.setDep_name("测试系统参数类部门init-wzc");
		deptInfo.setCreate_date(DateUtil.getSysDate());
		deptInfo.setCreate_time(DateUtil.getSysTime());
		deptInfo.setDep_remark("测试系统参数类部门init-wzc");

		//1、构建数据源Agent列表信息测试数据
		//1-1、构建数据源表(data_source)测试数据
		Data_source dataSource = new Data_source();
		dataSource.setSource_id(SOURCE_ID);
		dataSource.setDatasource_number("ds_");
		dataSource.setDatasource_name("wzctest_");
		dataSource.setDatasource_remark("wzctestremark_");
		dataSource.setCreate_date(DateUtil.getSysDate());
		dataSource.setCreate_time(DateUtil.getSysTime());
		dataSource.setCreate_user_id(TEST_USER_ID);

		//1-2、构建Agent信息表(agent_info)测试数据，包括5种类型的Agent
		List<Agent_info> agents = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			String agentType = null;
			long agentId = 0L;
			switch (i) {
				case 1:
					agentType = AgentType.ShuJuKu.getCode();
					agentId = DB_AGENT_ID;
					break;
				case 2:
					agentType = AgentType.DBWenJian.getCode();
					agentId = DF_AGENT_ID;
					break;
				case 3:
					agentType = AgentType.FTP.getCode();
					agentId = FTP_AGENT_ID;
					break;
				case 4:
					agentType = AgentType.DuiXiang.getCode();
					agentId = HALF_STRUCT_AGENT_ID;
					break;
				case 5:
					agentType = AgentType.WenJianXiTong.getCode();
					agentId = NON_STRUCT_AGENT_ID;
					break;
			}
			Agent_info agentInfo = new Agent_info();
			agentInfo.setAgent_id(agentId);
			agentInfo.setAgent_name("agent_" + i);
			agentInfo.setAgent_type(agentType);
			agentInfo.setAgent_ip("127.0.0.1");
			agentInfo.setAgent_port("55555");
			agentInfo.setAgent_status(AgentStatus.WeiLianJie.getCode());
			agentInfo.setCreate_date(DateUtil.getSysDate());
			agentInfo.setCreate_time(DateUtil.getSysTime());
			agentInfo.setUser_id(TEST_USER_ID);
			agentInfo.setSource_id(SOURCE_ID);

			agents.add(agentInfo);
		}

		//2、构建agent下任务的信息测试数据
		//2-1、构建database_set表测试数据
		List<Database_set> databases = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			long agentId = i % 2 == 0 ? DF_AGENT_ID : DB_AGENT_ID;
			long classifyId = i % 2 == 0 ? FIRST_CLASSIFY_ID : SECOND_CLASSIFY_ID;
			long id = i % 2 == 0 ? 1001L : 1002L;
			String databaseType = i % 2 == 0 ? DatabaseType.DB2.getCode() : DatabaseType.Postgresql.getCode();
			String databaseName = i % 2 == 0 ? "" : "postgresql";
			String databasePwd = i % 2 == 0 ? "" : "postgresql";
			String driver = i % 2 == 0 ? "" : "org.postgresql.Driver";
			String ip = i % 2 == 0 ? "" : "127.0.0.1";
			String port = i % 2 == 0 ? "" : "8888";
			String databaseCode = i % 2 == 0 ? "" : "1";
			String url = i % 2 == 0 ? "" : "jdbc:postgresql://127.0.0.1:8888/postgresql";
			String dbfileFormat = i % 2 == 0 ? "1" : "";
			String isHidden = i % 2 == 0 ? IsFlag.Fou.getCode() : IsFlag.Shi.getCode();
			String fileSuffix = i % 2 == 0 ? "dat" : "";
			String planeUrl = i % 2 == 0 ? "/home/hyrenshufu/wzc/test/data" : "";
			String rowSeparator = i % 2 == 0 ? "|" : "";
			String dbFlag = i % 2 == 0 ? IsFlag.Shi.getCode() : IsFlag.Fou.getCode();
			String userName = i % 2 == 0 ? "" : "hrsdxg";
			Database_set databaseSet = new Database_set();
			databaseSet.setDatabase_id(id);
			databaseSet.setAgent_id(agentId);
			databaseSet.setDatabase_number("dbtest" + i);
			databaseSet.setDb_agent(dbFlag);
//			databaseSet.setIs_load(IsFlag.Shi.getCode());
//			databaseSet.setIs_hidden(isHidden);
			databaseSet.setIs_sendok(IsFlag.Fou.getCode());
//			databaseSet.setIs_header(IsFlag.Shi.getCode());
			databaseSet.setClassify_id(classifyId);
			databaseSet.setTask_name("wzcTaskName" + i);
			databaseSet.setDatabase_type(databaseType);
			databaseSet.setDatabase_name(databaseName);
			databaseSet.setDatabase_drive(driver);
			databaseSet.setDatabase_ip(ip);
			databaseSet.setDatabase_port(port);
//			databaseSet.setDatabase_code(databaseCode);
			databaseSet.setJdbc_url(url);
//			databaseSet.setDbfile_format(dbfileFormat);
//			databaseSet.setFile_suffix(fileSuffix);
			databaseSet.setPlane_url(planeUrl);
			databaseSet.setRow_separator(rowSeparator);
			databaseSet.setDatabase_pad(databasePwd);
			databaseSet.setUser_name(userName);

			databases.add(databaseSet);
		}

		//2-2、构建object_collect表测试数据
		List<Object_collect> objectCollects = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			long id = i % 2 == 0 ? 2001L : 2002L;
			String runWay = i % 2 == 0 ? "1" : "2";
			Object_collect objectCollect = new Object_collect();
			objectCollect.setOdc_id(id);
			objectCollect.setObject_collect_type(ObjectCollectType.DuiXiangCaiJi.getCode());
			objectCollect.setObj_number("wzcTestNumber" + i);
			objectCollect.setObj_collect_name("wzcTestCollectName" + i);
			objectCollect.setSystem_name("wzcTestSystemName" + i);
			objectCollect.setHost_name("wzcTestHostName" + i);
			objectCollect.setLocal_time(DateUtil.getSysTime());
			objectCollect.setServer_date(DateUtil.getDateTime());
			objectCollect.setS_date("20190918");
			objectCollect.setE_date("20190918");
			objectCollect.setDatabase_code(DataBaseCode.UTF_8.getCode());
//			objectCollect.setRun_way(runWay);
			objectCollect.setFile_path("wzcTestFilePath" + i);
			objectCollect.setIs_sendok(IsFlag.Shi.getCode());
			objectCollect.setAgent_id(HALF_STRUCT_AGENT_ID);

			objectCollects.add(objectCollect);
		}

		//2-3、构建ftp_collect表测试数据
		List<Ftp_collect> ftps = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			String port = String.valueOf(31001 + i);
			String runWay = i % 2 == 0 ? "1" : "2";
			long id = i % 2 == 0 ? 3001L : 3002L;
			Ftp_collect ftpCollect = new Ftp_collect();
			ftpCollect.setFtp_id(id);
			ftpCollect.setFtp_number("wzcTestFTPNumber" + i);
			ftpCollect.setFtp_name("wzcTestTPName" + i);
			ftpCollect.setStart_date("20190918");
			ftpCollect.setEnd_date("20190918");
			ftpCollect.setFtp_ip("127.0.0.1");
			ftpCollect.setFtp_port(port);
			ftpCollect.setFtp_username("wzcTestFTPUserName" + i);
			ftpCollect.setFtp_password("wzcTestFTPPassWord" + i);
			ftpCollect.setFtp_dir("wzcTestFTPDir" + i);
			ftpCollect.setLocal_path("wzcTestLocalPath" + i);
			ftpCollect.setFtp_rule_path(IsFlag.Shi.getCode());
			ftpCollect.setFtp_model(IsFlag.Shi.getCode());
			ftpCollect.setRun_way(runWay);
			ftpCollect.setIs_sendok(IsFlag.Shi.getCode());
			ftpCollect.setIs_unzip(IsFlag.Shi.getCode());
			ftpCollect.setAgent_id(FTP_AGENT_ID);
			ftpCollect.setIs_read_realtime(IsFlag.Shi.getCode());
			ftpCollect.setRealtime_interval(10L);

			ftps.add(ftpCollect);
		}

		//2-4、构建file_collect_set表测试数据
		List<File_collect_set> fileCollectSets = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			long id = i % 2 == 0 ? 4001L : 4002L;
			File_collect_set fileCollectSet = new File_collect_set();
			fileCollectSet.setFcs_id(id);
			fileCollectSet.setAgent_id(NON_STRUCT_AGENT_ID);
			fileCollectSet.setFcs_name("wzcTestFcsName" + i);
			fileCollectSet.setIs_sendok(IsFlag.Shi.getCode());
			fileCollectSet.setIs_solr(IsFlag.Shi.getCode());

			fileCollectSets.add(fileCollectSet);
		}

		//3、构建各种采集任务相关测试数据
		//3-1、构建数据库直连采集测试数据
		//3-1-1、构建table_info(数据库对应表)测试数据
		Table_info tableInfo = new Table_info();
		tableInfo.setTable_name("wzc_test_database_collect_table_name");
		tableInfo.setTable_ch_name("数据库直连采集测试用表名");
		tableInfo.setDatabase_id(1002L);
		tableInfo.setValid_s_date("20190918");
		tableInfo.setValid_e_date("20190918");
		tableInfo.setIs_user_defined(IsFlag.Shi.getCode());
		tableInfo.setIs_md5(IsFlag.Shi.getCode());
		tableInfo.setIs_register(IsFlag.Shi.getCode());
		tableInfo.setTable_id(TABLE_ID);
		tableInfo.setIs_parallel(IsFlag.Fou.getCode());
		//3-1-2、构建table_column(表对应字段)测试数据
		List<Table_column> tableColumns = new ArrayList<>();
		for(int i = 0; i < 10; i++){
			Table_column column = new Table_column();
			column.setColumn_id(100200L + i);
			String isPK = i == 0 ? IsFlag.Shi.getCode() : IsFlag.Fou.getCode();
			column.setIs_primary_key(isPK);
			column.setColumn_name("wzcTestColumnName" + i);
			column.setTable_id(TABLE_ID);
			column.setValid_s_date("20190918");
			column.setValid_e_date("20190918");
			column.setIs_alive(IsFlag.Shi.getCode());
			column.setIs_new(IsFlag.Shi.getCode());

			tableColumns.add(column);
		}
		//3-2、构建非结构化文件采集测试数据
		//3-2-1、构建file_source(文件源设置)测试数据
		List<File_source> fileSources = new ArrayList<>();
		for(int i = 0; i < 2; i++){
			File_source fileSource = new File_source();
			fileSource.setFcs_id(4001L);
			fileSource.setAgent_id(NON_STRUCT_AGENT_ID);
			fileSource.setFile_source_id(400100L + i);
			fileSource.setFile_source_path("wzc_test_non_struct_collect_file_source_path");
			fileSource.setIs_pdf(IsFlag.Shi.getCode());
			fileSource.setIs_office(IsFlag.Fou.getCode());
			fileSource.setIs_audio(IsFlag.Fou.getCode());
			fileSource.setIs_image(IsFlag.Fou.getCode());
			fileSource.setIs_other(IsFlag.Fou.getCode());
			fileSource.setIs_text(IsFlag.Fou.getCode());
			fileSource.setIs_video(IsFlag.Fou.getCode());
			fileSource.setIs_compress(IsFlag.Fou.getCode());

			fileSources.add(fileSource);
		}

		//2-5、插入数据
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//插入用户数据
			int userCount = user.add(db);
			assertThat("用户测试数据初始化", userCount, is(1));
			//插入部门数据
			int deptCount = deptInfo.add(db);
			assertThat("部门测试数据初始化", deptCount, is(1));

			//插入数据源表(data_source)测试数据
			int dataSourceCount = dataSource.add(db);
			assertThat("数据源测试数据初始化", dataSourceCount, is(1));

			//插入Agent信息表(agent_info)测试数据
			int agentInfoCount = 0;
			for(Agent_info agentInfo : agents){
				int count = agentInfo.add(db);
				agentInfoCount += count;
			}
			assertThat("Agent测试数据初始化", agentInfoCount, is(5));

			//插入database_set表测试数据
			int databaseSetCount = 0;
			for(Database_set databaseSet : databases){
				int count = databaseSet.add(db);
				databaseSetCount += count;
			}
			assertThat("数据库设置测试数据初始化", databaseSetCount, is(2));

			//插入object_collect表测试数据
			int objectCollectCount = 0;
			for(Object_collect objectCollect : objectCollects){
				int count = objectCollect.add(db);
				objectCollectCount += count;
			}
			assertThat("半结构化文件采集设置测试数据初始化", objectCollectCount, is(2));

			//插入ftp_collect表测试数据
			int ftpCollectCount = 0;
			for(Ftp_collect ftpCollect : ftps){
				int count = ftpCollect.add(db);
				ftpCollectCount += count;
			}
			assertThat("FTP采集设置测试数据初始化", ftpCollectCount, is(2));

			//插入file_collect_set表测试数据
			int fileCollectSetCount = 0;
			for(File_collect_set fileCollectSet : fileCollectSets){
				int count = fileCollectSet.add(db);
				fileCollectSetCount += count;
			}
			assertThat("非结构化文件采集设置测试数据初始化", fileCollectSetCount, is(2));

			//插入table_info表测试数据
			int tableInfoCount = tableInfo.add(db);
			assertThat("数据库直连采集数据库对应表信息测试数据初始化", tableInfoCount, is(1));

			//插入table_column表测试数据
			int tableColumnCount = 0;
			for(Table_column column : tableColumns){
				int count = column.add(db);
				tableColumnCount += count;
			}
			assertThat("数据库直连采集表对应字段信息测试数据初始化", tableColumnCount, is(10));

			//插入file_source表测试数据
			int fileSourceCount = 0;
			for(File_source fileSource : fileSources){
				int count = fileSource.add(db);
				fileSourceCount += count;
			}
			assertThat("非结构化文件采集文件源设置测试数据初始化", fileSourceCount, is(2));

			SqlOperator.commitTransaction(db);
		}

		//模拟用户登录
		String responseValue = new HttpClient().buildSession()
				.addData("user_id", TEST_USER_ID)
				.addData("password", TEST_USER_PASSWORD)
				.post("http://127.0.0.1:8099/A/action/hrds/a/biz/login/login").getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class).orElseThrow(() -> new BusinessException("连接失败"));
		assertThat(ar.isSuccess(), is(true));
	}

	/**
	 * 测试获取数据源Agent列表信息
	 *
	 * 正确数据访问1：使用正确的userId模拟登陆,http请求访问被测试方法,得到响应，判断结果是否正确
	 * 未达到三组：Action中getAgentInfoList方法没有参数，没有if()else分支
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getAgentInfoList() {
		//1、正确数据访问：模拟正确的用户登录,http请求访问被测试方法,得到响应，判断结果是否正确
		String rightString = new HttpClient()
				.post(getActionUrl("getAgentInfoList")).getBodyString();

		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Result data = rightResult.getDataForResult();
		assertThat("根据测试数据，查询到的数据源信息应该有" + data.getRowCount() + "条", data.getRowCount(), is(1));
		assertThat("根据测试数据，查询到的数据源ID为" + data.getLong(0, "source_id"), data.getLong(0, "source_id"), is(SOURCE_ID));
		assertThat("根据测试数据，查询到的数据源名称为" + data.getString(0, "datasource_name"), data.getString(0, "datasource_name"), is("wzctest_"));
		assertThat("根据测试数据，查询该数据源下有一个数据库采集Agent" + data.getInt(0, "dbflag"), data.getInt(0, "dbflag"), is(1));
		assertThat("根据测试数据，查询该数据源下有一个数据文件采集Agent" + data.getInt(0, "dfflag"), data.getInt(0, "dfflag"), is(1));
		assertThat("根据测试数据，查询该数据源下有一个非结构化采集Agent" + data.getInt(0, "nonstructflag"), data.getInt(0, "nonstructflag"), is(1));
		assertThat("根据测试数据，查询该数据源下有一个半结构化采集Agent" + data.getInt(0, "halfstructflag"), data.getInt(0, "halfstructflag"), is(1));
		assertThat("根据测试数据，查询该数据源下有一个FTP采集Agent" + data.getInt(0, "ftpflag"), data.getInt(0, "ftpflag"), is(1));
	}

	/**
	 * 测试根据sourceId和agentType获取相应信息
	 *
	 * 正确数据访问：http请求访问被测试方法，得到响应，判断结果是否正确
	 *      1-1、构建请求获取数据库Agent
	 *      1-2、构建请求获取非结构化采集Agent
	 *      1-3、构建请求获取FtpAgent
	 *      1-4、构建请求获取数据文件Agent
	 *      1-5、构建请求获取半结构化Agent
	 * 错误数据访问1：构建错误的sourceId，判断拿到的数据是否为空
	 * 错误数据访问2：构建错误的agentType，判断拿到的数据是否为空
	 * 错误的测试用例未达到三组: getAgentInfo()方法只有两个参数
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getAgentInfo() {
		//1、http请求访问被测试方法
		//1-1、构建请求获取数据库Agent
		String dbAgentResp = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentType", AgentType.ShuJuKu.getCode())
				.post(getActionUrl("getAgentInfo")).getBodyString();
		//1-2、构建请求获取非结构化采集Agent
		String nonStructResp = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentType", AgentType.WenJianXiTong.getCode())
				.post(getActionUrl("getAgentInfo")).getBodyString();
		//1-3、构建请求获取FtpAgent
		String ftpResp = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentType", AgentType.FTP.getCode())
				.post(getActionUrl("getAgentInfo")).getBodyString();
		//1-4、构建请求获取数据文件Agent
		String dataFileResp = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentType", AgentType.DBWenJian.getCode())
				.post(getActionUrl("getAgentInfo")).getBodyString();
		//1-5、构建请求获取半结构化Agent
		String halfStructResp = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentType", AgentType.DuiXiang.getCode())
				.post(getActionUrl("getAgentInfo")).getBodyString();

		//2、得到响应，判断结果是否正确
		ActionResult dbAgent = JsonUtil.toObjectSafety(dbAgentResp, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(dbAgent.isSuccess(), is(true));
		Result dbData = dbAgent.getDataForResult();
		assertThat("根据测试数据，在该数据源下共有" + dbData.getRowCount() + "条数据库Agent数据", dbData.getRowCount(), is(1));

		ActionResult nonStructAgent = JsonUtil.toObjectSafety(nonStructResp, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(nonStructAgent.isSuccess(), is(true));
		Result nonStructData = nonStructAgent.getDataForResult();
		assertThat("根据测试数据，在该数据源下共有" + nonStructData.getRowCount() + "条非结构化Agent数据", nonStructData.getRowCount(), is(1));

		ActionResult ftpAgent = JsonUtil.toObjectSafety(ftpResp, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(ftpAgent.isSuccess(), is(true));
		Result ftpData = ftpAgent.getDataForResult();
		assertThat("根据测试数据，在该数据源下共有" + ftpData.getRowCount() + "条FTPAgent数据", ftpData.getRowCount(), is(1));


		ActionResult dataFileAgent = JsonUtil.toObjectSafety(dataFileResp, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(dataFileAgent.isSuccess(), is(true));
		Result dataFileData = dataFileAgent.getDataForResult();
		assertThat("根据测试数据，在该数据源下共有" + dataFileData.getRowCount() + "条FTPAgent数据", dataFileData.getRowCount(), is(1));

		ActionResult halfStructAgent = JsonUtil.toObjectSafety(halfStructResp, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(halfStructAgent.isSuccess(), is(true));
		Result halfStructData = halfStructAgent.getDataForResult();
		assertThat("根据测试数据，在该数据源下共有" + halfStructData.getRowCount() + "条半结构化Agent数据", halfStructData.getRowCount(), is(1));

		//错误数据访问1：构建错误的sourceId，判断拿到的数据是否为空
		long wrongSourceId = 2L;
		String wrongSourceIdResp = new HttpClient()
				.addData("sourceId", wrongSourceId)
				.addData("agentType", AgentType.ShuJuKu.getCode())
				.post(getActionUrl("getAgentInfo")).getBodyString();
		ActionResult wrongSourceIdResult = JsonUtil.toObjectSafety(wrongSourceIdResp, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongSourceIdResult.isSuccess(), is(true));
		Result wrongSourceIdData = wrongSourceIdResult.getDataForResult();
		assertThat("根据测试数据，构造错误的source_id，应该获得" + wrongSourceIdData.getRowCount() + "条Agent数据", wrongSourceIdData.getRowCount(), is(0));

		//错误数据访问2：构建错误的agentType，判断拿到的数据是否为空
		String wrongAgentType = "6";
		String wrongAgentTypeResp = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentType", wrongAgentType)
				.post(getActionUrl("getAgentInfo")).getBodyString();
		ActionResult wrongAgentTypeResult = JsonUtil.toObjectSafety(wrongAgentTypeResp, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongAgentTypeResult.isSuccess(), is(true));
		Result wrongAgentTypeData = wrongAgentTypeResult.getDataForResult();
		assertThat("根据测试数据，构造错误的agent_type，应该获得" + wrongAgentTypeData.getRowCount() + "条Agent数据", wrongAgentTypeData.getRowCount(), is(0));

	}

	/**
	 * 测试根据sourceId和agentId获取某agent下所有任务的信息
	 *
	 * 正确数据访问1：http请求访问被测试方法，agentId传入DB_AGENT_ID，判断结果是否正确
	 * 正确数据访问2：http请求访问被测试方法，agentId传入DF_AGENT_ID，判断结果是否正确
	 * 正确数据访问3：http请求访问被测试方法，agentId传入FTP_AGENT_ID，判断结果是否正确
	 * 正确数据访问4：http请求访问被测试方法，agentId传入HALF_STRUCT_AGENT_ID，判断结果是否正确
	 * 正确数据访问5：http请求访问被测试方法，agentId传入NON_STRUCT_AGENT_ID，判断结果是否正确
	 *
	 * 错误数据访问1：agentId传入一个errorAgentId，判断ar.isSuccess()是否为false
	 * 错误数据访问2：sourceId传入一个errorSourceId，判断ar.isSuccess()是否为false
	 * 错误的测试用例未达到三组:getTaskInfo()方法只有两个入参
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getTaskInfo() {
		//正确数据访问1：http请求访问被测试方法，agentId传入DB_AGENT_ID，判断结果是否正确
		String dbBodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentId", DB_AGENT_ID)
				.post(getActionUrl("getTaskInfo")).getBodyString();

		ActionResult dbResult = JsonUtil.toObjectSafety(dbBodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(dbResult.isSuccess(), is(true));
		Result firResult = dbResult.getDataForResult();
		assertThat("根据测试数据，查询得到的数据库采集任务有" + firResult.getRowCount() + "项", firResult.getRowCount(), is(1));

		//正确数据访问2：http请求访问被测试方法，agentId传入DF_AGENT_ID，判断结果是否正确
		String dfBodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentId", DF_AGENT_ID)
				.post(getActionUrl("getTaskInfo")).getBodyString();

		ActionResult dfResult = JsonUtil.toObjectSafety(dfBodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(dfResult.isSuccess(), is(true));
		Result secResult = dfResult.getDataForResult();
		assertThat("根据测试数据，查询得到的数据库采集任务有" + secResult.getRowCount() + "项", secResult.getRowCount(), is(1));

		//正确数据访问3：http请求访问被测试方法，agentId传入FTP_AGENT_ID，判断结果是否正确
		String ftpBodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentId", FTP_AGENT_ID)
				.post(getActionUrl("getTaskInfo")).getBodyString();

		ActionResult ftpResult = JsonUtil.toObjectSafety(ftpBodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(ftpResult.isSuccess(), is(true));
		Result thrResult = ftpResult.getDataForResult();
		assertThat("根据测试数据，查询得到的数据库采集任务有" + thrResult.getRowCount() + "项", thrResult.getRowCount(), is(2));

		//正确数据访问4：http请求访问被测试方法，agentId传入HALF_STRUCT_AGENT_ID，判断结果是否正确
		String halfBodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentId", HALF_STRUCT_AGENT_ID)
				.post(getActionUrl("getTaskInfo")).getBodyString();

		ActionResult halfResult = JsonUtil.toObjectSafety(halfBodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(halfResult.isSuccess(), is(true));
		Result fouResult = halfResult.getDataForResult();
		assertThat("根据测试数据，查询得到的数据库采集任务有" + fouResult.getRowCount() + "项", fouResult.getRowCount(), is(2));

		//正确数据访问5：http请求访问被测试方法，agentId传入NON_STRUCT_AGENT_ID，判断结果是否正确
		String nonBodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentId", NON_STRUCT_AGENT_ID)
				.post(getActionUrl("getTaskInfo")).getBodyString();

		ActionResult nonResult = JsonUtil.toObjectSafety(nonBodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(nonResult.isSuccess(), is(true));
		Result fifResult = nonResult.getDataForResult();
		assertThat("根据测试数据，查询得到的数据库采集任务有" + fifResult.getRowCount() + "项", fifResult.getRowCount(), is(2));


		//错误数据访问1：agentId传入一个errorAgentId，判断isSuccess()是false
		long errorAgentId = 1000L;
		String errorAgentIdString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentId", errorAgentId)
				.post(getActionUrl("getTaskInfo")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(errorAgentIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(ar.isSuccess(), is(false));

		//错误数据访问2：sourceId传入一个errorSourceId，判断ar.isSuccess()是否为false
		long errorSourceId = 2L;
		String errorSourceIdString = new HttpClient()
				.addData("sourceId", errorSourceId)
				.addData("agentId", DB_AGENT_ID)
				.post(getActionUrl("getTaskInfo")).getBodyString();
		ActionResult errorSourceIdResult = JsonUtil.toObjectSafety(errorSourceIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(errorSourceIdResult.isSuccess(), is(false));
	}

	/**
	 * 测试根据ID删除半结构化采集任务数据
	 *
	 * 正确数据访问1：
	 *      1、删除前查询数据库，确认预期删除的数据存在
	 *      2、构造正确的collectSetId，http请求逻辑处理方法，删除数据,得到响应，判断删除是否成功
	 *      3、删除后，确认数据是否被真正删除
	 *
	 * 错误的数据访问1：构造错误的collectSetId，断言响应是否失败
	 * 错误的测试用例未达到三组:deleteHalfStructTask()方法只有一个入参
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteHalfStructTask() {
		try(DatabaseWrapper db = new DatabaseWrapper()){
			//正确数据访问1：
			//1、删除前查询数据库，确认预期删除的数据存在
			long before = SqlOperator.queryNumber(db, "select count(1) from object_collect where odc_id = ?", 2001L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("删除操作前，object_collect表中的确存在这样一条数据", before, is(1L));

			//2、构造正确的collectSetId，http请求逻辑处理方法，删除数据,得到响应，判断删除是否成功
			String rightString = new HttpClient()
					.addData("collectSetId", 2001L)
					.post(getActionUrl("deleteHalfStructTask")).getBodyString();
			ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败!"));
			assertThat(rightResult.isSuccess(), is(true));

			//3、删除后，确认数据是否被真正删除
			long after = SqlOperator.queryNumber(db, "select count(1) from object_collect where odc_id = ?", 2001L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("删除操作后，object_collect表中这样一条数据没有了", after, is(0L));

			//错误的数据访问1：构造错误的collectSetId，断言响应是否失败
			long wrongCollectSetId = 2003L;
			String wrongCollectSetIdString = new HttpClient()
					.addData("collectSetId", wrongCollectSetId)
					.post(getActionUrl("deleteHalfStructTask")).getBodyString();
			ActionResult wrongCollectSetIdResult = JsonUtil.toObjectSafety(wrongCollectSetIdString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败!"));
			assertThat(wrongCollectSetIdResult.isSuccess(), is(false));
		}
	}

	/**
	 * 测试根据ID删除FTP采集任务数据
	 *
	 * 正确数据访问1：
	 *      1、删除前查询数据库，确认预期删除的数据存在
	 *      2、构造正确的collectSetId，http请求逻辑处理方法，删除数据,得到响应，判断删除是否成功
	 *      3、删除后，确认数据是否被真正删除
	 * 错误数据访问2：构造错误的collectSetId，断言响应是否失败
	 * 错误的测试用例未达到三组:deleteFTPTask()方法只有一个入参
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteFTPTask() {
		try(DatabaseWrapper db = new DatabaseWrapper()){
			//正确数据访问1
			//1、删除前查询数据库，确认预期删除的数据存在
			long before = SqlOperator.queryNumber(db, "select count(1) from ftp_collect where ftp_id = ?", 3001L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("删除操作前，ftp_collect表中的确存在这样一条数据", before, is(1L));

			//2、构造正确的collectSetId，http请求逻辑处理方法，删除数据,得到响应，判断删除是否成功
			String bodyString = new HttpClient()
					.addData("collectSetId", 3001L)
					.post(getActionUrl("deleteFTPTask")).getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败!"));
			assertThat(ar.isSuccess(), is(true));

			//3、删除后，确认数据是否被真正删除
			long after = SqlOperator.queryNumber(db, "select count(1) from ftp_collect where ftp_id = ?", 3001L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("删除操作后，ftp_collect表中这样一条数据没有了", after, is(0L));

			//错误数据访问1：构造错误的collectSetId，断言响应是否失败
			long wrongCollectSetId = 2003L;
			String wrongCollectSetIdString = new HttpClient()
					.addData("collectSetId", wrongCollectSetId)
					.post(getActionUrl("deleteFTPTask")).getBodyString();
			ActionResult wrongCollectSetIdResult = JsonUtil.toObjectSafety(wrongCollectSetIdString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败!"));
			assertThat(wrongCollectSetIdResult.isSuccess(), is(false));
		}
	}

	/**
	 * 测试根据ID删除数据库直连采集任务
	 *
	 * 正确数据访问1：
	 *      1、删除前查询数据库，确认预期删除的数据存在
	 *      2、构造正确的collectSetId，http请求逻辑处理方法，删除数据,得到响应，判断删除是否成功
	 *      3、删除后，确认数据是否被真正删除
	 * 错误数据访问1：构造错误的collectSetId，断言响应是否失败
	 * 错误的测试用例未达到三组:deleteDBTask()方法只有一个入参
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteDBTask() {
		try(DatabaseWrapper db = new DatabaseWrapper()){
			//正确数据访问1：
			//1、删除前查询数据库，确认预期删除的数据存在
			long firCount = SqlOperator.queryNumber(db, "select count(1) from database_set where database_id = ?", 1002L).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("删除操作前，database_set表中的确存在这样一条数据", firCount, is(1L));

			long secCount = SqlOperator.queryNumber(db, "select count(1) from table_column where table_id = ?", TABLE_ID).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("删除操作前，table_column表中的确存在" + secCount + "条数据", secCount, is(10L));

			long tirCount = SqlOperator.queryNumber(db, "select count(1) from table_info where table_id = ?", TABLE_ID).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("删除操作前，table_info表中的确存在一条数据", tirCount, is(1L));

			//2、构造正确的collectSetId，http请求逻辑处理方法，删除数据,得到响应，判断删除是否成功
			String rightString = new HttpClient()
					.addData("collectSetId", 1002L)
					.post(getActionUrl("deleteDBTask")).getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败!"));
			assertThat(ar.isSuccess(), is(true));

			//3、删除后，确认数据是否被真正删除
			long firAfter = SqlOperator.queryNumber(db, "select count(1) from database_set where database_id = ?", 1002L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("删除操作后，database_set表中指定数据没有了", firAfter, is(0L));

			long secAfter = SqlOperator.queryNumber(db, "select count(1) from table_column where table_id = ?", TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("删除操作后，table_column表中指定数据没有了", secAfter, is(0L));

			long thiAfter = SqlOperator.queryNumber(db, "select count(1) from table_info where table_id = ?", TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("删除操作后，table_info表中指定数据没有了", thiAfter, is(0L));

			//错误数据访问1：构造错误的collectSetId，断言响应是否失败
			long wrongCollectSetId = 2003L;
			String wrongCollectSetIdString = new HttpClient()
					.addData("collectSetId", wrongCollectSetId)
					.post(getActionUrl("deleteDBTask")).getBodyString();
			ActionResult wrongCollectSetIdResult = JsonUtil.toObjectSafety(wrongCollectSetIdString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败!"));
			assertThat(wrongCollectSetIdResult.isSuccess(), is(false));

		}
	}

	/**
	 * 测试根据ID删除数据文件采集任务
	 *
	 * 正确数据访问1：
	 *      1、删除前查询数据库，确认预期删除的数据存在
	 *      2、构造正确的collectSetId，http请求逻辑处理方法，删除数据,得到响应，判断删除是否成功
	 *      3、删除后，确认数据是否被真正删除
	 *
	 * 错误数据访问1：构造错误的collectSetId，断言响应是否失败
	 * 错误的测试用例未达到三组:deleteDFTask()方法只有一个入参
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteDFTask() {
		try(DatabaseWrapper db = new DatabaseWrapper()){
			//正确数据访问1：
			//1、删除前查询数据库，确认预期删除的数据存在
			long firCountBefore = SqlOperator.queryNumber(db, "select count(1) from database_set where database_id = ?", 1001L).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("删除操作前，database_set表中的确存在这样一条数据", firCountBefore, is(1L));

			//2、构造正确的collectSetId，http请求逻辑处理方法，删除数据,得到响应，判断删除是否成功
			String rightString = new HttpClient()
					.addData("collectSetId", 1001L)
					.post(getActionUrl("deleteDFTask")).getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败!"));
			assertThat(ar.isSuccess(), is(true));

			//3、删除后，确认数据是否被真正删除
			long firCountAfter = SqlOperator.queryNumber(db, "select count(1) from database_set where database_id = ?", 1001L).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("删除操作后，database_set表中指定数据没有了", firCountAfter, is(0L));

			//错误数据访问1：构造错误的collectSetId，断言响应是否失败
			long wrongCollectSetId = 2003L;
			String wrongCollectSetIdString = new HttpClient()
					.addData("collectSetId", wrongCollectSetId)
					.post(getActionUrl("deleteDFTask")).getBodyString();
			ActionResult wrongCollectSetIdResult = JsonUtil.toObjectSafety(wrongCollectSetIdString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败!"));
			assertThat(wrongCollectSetIdResult.isSuccess(), is(false));

		}
	}

	/**
	 * 测试根据ID删除非结构化文件采集任务
	 *
	 * 正确数据访问1：
	 *      1、删除前查询数据库，确认预期删除的数据存在
	 *      2、构造正确的collectSetId，http请求逻辑处理方法，删除数据,得到响应，判断删除是否成功
	 *      3、删除后，确认数据是否被真正删除
	 * 错误数据访问1：构造错误的collectSetId，断言响应是否失败
	 * 错误的测试用例未达到三组:deleteNonStructTask()方法只有一个入参
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteNonStructTask() {
		try(DatabaseWrapper db = new DatabaseWrapper()){
			//正确数据访问1：
			//1、删除前查询数据库，确认预期删除的数据存在
			long firCountBefore = SqlOperator.queryNumber(db, "select count(1) from file_collect_set where fcs_id = ?", 4001L).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("删除操作前，file_collect_set表中的确存在这样一条数据", firCountBefore, is(1L));

			long secCountBefore = SqlOperator.queryNumber(db, "select count(1) from file_source where fcs_id = ?", 4001L).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("删除操作前，file_collect_set表中的确存在这样一条数据", secCountBefore, is(2L));

			//2、构造正确的collectSetId，http请求逻辑处理方法，删除数据,得到响应，判断删除是否成功
			String rightString = new HttpClient()
					.addData("collectSetId", 4001L)
					.post(getActionUrl("deleteNonStructTask")).getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败!"));
			assertThat(ar.isSuccess(), is(true));

			//3、删除后，确认数据是否被真正删除
			long firCountAfter = SqlOperator.queryNumber(db, "select count(1) from file_collect_set where fcs_id = ?", 4001L).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("删除操作后，file_collect_set表中这样一条数据没有了", firCountAfter, is(0L));

			long secCountAfter = SqlOperator.queryNumber(db, "select count(1) from file_source where fcs_id = ?", 4001L).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("删除操作后，file_collect_set表中这样一条数据没有了", secCountAfter, is(0L));


			//错误数据访问1：构造错误的collectSetId，断言响应是否失败，获取到的数据是否是空字符串
			long wrongCollectSetId = 2003L;
			String wrongCollectSetIdString = new HttpClient()
					.addData("collectSetId", wrongCollectSetId)
					.post(getActionUrl("deleteNonStructTask")).getBodyString();
			ActionResult wrongCollectSetIdResult = JsonUtil.toObjectSafety(wrongCollectSetIdString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败!"));
			assertThat(wrongCollectSetIdResult.isSuccess(), is(false));

		}
	}

	/**
	 * 测试根据sourceId查询出设置完成的数据库采集任务和DB文件采集任务的任务ID
	 *
	 * 正确的数据访问1：使用正确的sourceId,http请求访问被测试方法,得到响应，判断结果是否正确
	 * 错误的数据访问1：使用错误的sourceId,http请求访问被测试方法,得到响应，判断结果集中没有数据
	 * 错误的测试用例未达到三组:getDBAndDFTaskBySourceId()方法只有一个入参
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getDBAndDFTaskBySourceId() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			SqlOperator.execute(db, "update " + Database_set.TableName + " set is_sendok = ?", IsFlag.Shi.getCode());
			SqlOperator.commitTransaction(db);
		}
		//正确的数据访问1：使用正确的sourceId,http请求访问被测试方法,得到响应，判断结果是否正确
		String databaseSetString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("getDBAndDFTaskBySourceId")).getBodyString();

		ActionResult databaseSetResult = JsonUtil.toObjectSafety(databaseSetString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(databaseSetResult.isSuccess(), is(true));
		Result firResult = databaseSetResult.getDataForResult();
		assertThat("根据测试数据，使用正确的sourceId查询得到的数据库采集任务和数据文件采集任务有" + firResult.getRowCount() + "项", firResult.getRowCount(), is(2));

		//错误的数据访问1：使用错误的sourceId,http请求访问被测试方法,得到响应，判断结果集中没有数据
		long wrongSourceId = 2L;
		String wrongSourceIdString = new HttpClient()
				.addData("sourceId", wrongSourceId)
				.post(getActionUrl("getDBAndDFTaskBySourceId")).getBodyString();

		ActionResult wrongDatabaseSetResult = JsonUtil.toObjectSafety(wrongSourceIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongDatabaseSetResult.isSuccess(), is(true));
		Result wrongDatabaseSetData = wrongDatabaseSetResult.getDataForResult();
		assertThat("根据测试数据，使用和错误的sourceId查询得到的数据库采集任务和数据文件采集任务有" + wrongDatabaseSetData.getRowCount() + "项", wrongDatabaseSetData.getRowCount(), is(0));
	}

	/**
	 * 测试根据sourceId查询出设置完成的非结构化文件采集任务的任务ID
	 *
	 * 正确的数据访问1:使用正确的sourceId,http请求访问被测试方法,得到响应，判断结果是否正确
	 * 错误的数据访问1:使用错误的sourceId,http请求访问被测试方法,得到响应，判断结果集中没有数据
	 * 错误的测试用例未达到三组:getNonStructTaskBySourceId()方法只有一个入参
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getNonStructTaskBySourceId() {
		//正确的数据访问1:使用正确的sourceId,http请求访问被测试方法,得到响应，判断结果是否正确
		String nonStructString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("getNonStructTaskBySourceId")).getBodyString();

		ActionResult nonStructResult = JsonUtil.toObjectSafety(nonStructString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(nonStructResult.isSuccess(), is(true));
		Result firResult = nonStructResult.getDataForResult();
		assertThat("根据测试数据，使用和正确的sourceId查询得到的非结构化采集任务有" + firResult.getRowCount() + "项", firResult.getRowCount(), is(2));

		//错误的数据访问1:使用错误的sourceId,http请求访问被测试方法,得到响应，判断结果集中没有数据
		long wrongSourceId = 2L;
		String wrongSourceIdString = new HttpClient()
				.addData("sourceId", wrongSourceId)
				.post(getActionUrl("getNonStructTaskBySourceId")).getBodyString();

		ActionResult wrongDatabaseSetResult = JsonUtil.toObjectSafety(wrongSourceIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongDatabaseSetResult.isSuccess(), is(true));
		Result wrongDatabaseSetData = wrongDatabaseSetResult.getDataForResult();
		assertThat("根据测试数据，使用和错误的sourceId查询得到的非结构化采集任务有" + wrongDatabaseSetData.getRowCount() + "项", wrongDatabaseSetData.getRowCount(), is(0));

	}

	/**
	 * 测试根据sourceId查询出设置完成的半结构化文件采集任务的任务ID
	 *
	 * 正确的数据访问1：使用正确的sourceId,http请求访问被测试方法,得到响应，判断结果集中没有数据
	 * 错误的数据访问2：使用错误的sourceId,http请求访问被测试方法,得到响应，判断结果集中没有数据
	 * 错误的测试用例未达到三组:getHalfStructTaskBySourceId()方法只有一个入参
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getHalfStructTaskBySourceId() {
		//正确的数据访问1：使用和正确的sourceId,http请求访问被测试方法,得到响应，判断结果是否正确
		String halfStructString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("getHalfStructTaskBySourceId")).getBodyString();

		ActionResult halfStructResult = JsonUtil.toObjectSafety(halfStructString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(halfStructResult.isSuccess(), is(true));
		Result firResult = halfStructResult.getDataForResult();
		assertThat("根据测试数据，使用正确的sourceId查询得到的半结构化采集任务有" + firResult.getRowCount() + "项", firResult.getRowCount(), is(2));

		//错误的数据访问1：使用错误的sourceId,http请求访问被测试方法,得到响应，判断结果集中没有数据
		long wrongSourceId = 2L;
		String wrongSourceIdString = new HttpClient()
				.addData("sourceId", wrongSourceId)
				.post(getActionUrl("getHalfStructTaskBySourceId")).getBodyString();

		ActionResult wrongDatabaseSetResult = JsonUtil.toObjectSafety(wrongSourceIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongDatabaseSetResult.isSuccess(), is(true));
		Result wrongDatabaseSetData = wrongDatabaseSetResult.getDataForResult();
		assertThat("根据测试数据，使用错误的sourceId查询得到的半结构化采集任务有" + wrongDatabaseSetData.getRowCount() + "项", wrongDatabaseSetData.getRowCount(), is(0));
	}

	/**
	 * 测试根据sourceId查询出设置完成的FTP采集任务的任务ID
	 *
	 * 正确的数据访问1：使用正确的sourceId,http请求访问被测试方法,得到响应，判断结果集中没有数据
	 * 错误的数据访问2：使用错误的sourceId,http请求访问被测试方法,得到响应，判断结果集中没有数据
	 * 错误的测试用例未达到三组:getFTPTaskBySourceId()方法只有一个入参
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getFTPTaskBySourceId() {
		//正确的数据访问1：使用正确的sourceId,http请求访问被测试方法,得到响应，判断结果集中没有数据
		String ftpString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("getFTPTaskBySourceId")).getBodyString();

		ActionResult ftpResult = JsonUtil.toObjectSafety(ftpString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(ftpResult.isSuccess(), is(true));
		Result firResult = ftpResult.getDataForResult();
		assertThat("根据测试数据，使用正确的sourceId查询得到的FTP采集任务有" + firResult.getRowCount() + "项", firResult.getRowCount(), is(2));

		//错误的数据访问1：使用错误的sourceId,http请求访问被测试方法,得到响应，判断结果是否正确
		long wrongSourceId = 2L;
		String wrongSourceIdString = new HttpClient()
				.addData("sourceId", wrongSourceId)
				.post(getActionUrl("getFTPTaskBySourceId")).getBodyString();

		ActionResult wrongDatabaseSetResult = JsonUtil.toObjectSafety(wrongSourceIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongDatabaseSetResult.isSuccess(), is(true));
		Result wrongDatabaseSetData = wrongDatabaseSetResult.getDataForResult();
		assertThat("根据测试数据，使用错误的sourceId查询得到的FTP采集任务有" + wrongDatabaseSetData.getRowCount() + "项", wrongDatabaseSetData.getRowCount(), is(0));
	}

	/**
	 * 测试根据数据库设置ID查询数据，向agent端发送任务信息
	 *
	 * 正确的数据访问1：构造测试数据，在1002的数据库采集任务中配置采集sys_user表和code_info表的配置信息，测试完成后删除信息
	 * 错误的测试用例未达到三组: 自行在数据库中构造数据，测试完成后删除
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void sendDBCollctTaskById(){
		//正确的数据访问1：构造测试数据，在1002的数据库采集任务中配置采集sys_user表和code_info表的配置信息，测试完成后删除信息
		buildTestDataForSendTask();
		/*
		 * 调用方法进行测试，执行本测试用例的方式是，修改被测方法，改为返回发送到agent端的json串，
		 * 本类拿到数据之后，反序列化为JSONObject,断言里面每一部分的内容是否符合期望
		 */
		long databaseSetId = 1002L;
		String rightString = new HttpClient()
				.addData("colSetId", databaseSetId)
				.post(getActionUrl("sendDBCollctTaskById")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		String resultData = (String) rightResult.getData();

		JSONObject resultObj = JSONObject.parseObject(resultData);
		assertThat("获得到了数据，并且不为空", resultObj.isEmpty(), is(false));

		//获取数据库设置基本信息，并断言是否符合期望
		assertThat("agent_id为<7001>", resultObj.getLong("agent_id"), is(DB_AGENT_ID));
		assertThat("database_id为<1002>", resultObj.getLong("database_id"), is(1002L));
		assertThat("task_name为<wzcTaskName1>", resultObj.getString("task_name"), is("wzcTaskName1"));
		assertThat("database_name为<postgresql>", resultObj.getString("database_name"), is("postgresql"));
		assertThat("database_pad为<postgresql>", resultObj.getString("database_pad"), is("postgresql"));
		assertThat("database_drive为<org.postgresql.Driver>", resultObj.getString("database_drive"), is("org.postgresql.Driver"));
		assertThat("database_type为<Postgresql>", resultObj.getString("database_type"), is(DatabaseType.Postgresql.getCode()));
		assertThat("user_name为<hrsdxg>", resultObj.getString("user_name"), is("hrsdxg"));
		assertThat("database_ip为<127.0.0.1>", resultObj.getString("database_ip"), is("127.0.0.1"));
		assertThat("database_port为<8888>", resultObj.getString("database_port"), is("8888"));
		assertThat("host_name为空", resultObj.getString("host_name") == null, is(true));
		assertThat("system_type为空", resultObj.getString("system_type") == null, is(true));
		assertThat("is_sendok为<否>", resultObj.getString("is_sendok"), is(IsFlag.Fou.getCode()));
		assertThat("db_agent为<否>", resultObj.getString("db_agent"), is(IsFlag.Fou.getCode()));
		assertThat("plane_url为空", resultObj.getString("plane_url"), is(""));
		assertThat("database_separatorr为空", resultObj.getString("database_separatorr") == null, is(true));
		assertThat("database_code为UTF-8", resultObj.getString("database_code"), is(DataBaseCode.UTF_8.getCode()));
		assertThat("dbfile_format为空", StringUtil.isBlank(resultObj.getString("dbfile_format")), is(true));
		assertThat("is_hidden为是", resultObj.getString("is_hidden"), is(IsFlag.Shi.getCode()));
		assertThat("file_suffix为空", resultObj.getString("file_suffix"), is(""));
		assertThat("is_load为<是>", resultObj.getString("is_load"), is(IsFlag.Shi.getCode()));
		assertThat("row_separator为空", resultObj.getString("row_separator"), is(""));
		assertThat("classify_id为<10010>", resultObj.getLong("classify_id"), is(SECOND_CLASSIFY_ID));
		assertThat("is_header为<是>", resultObj.getString("is_header"), is(IsFlag.Shi.getCode()));
		assertThat("jdbc_url为<jdbc:postgresql://127.0.0.1:8888/postgresql>", resultObj.getString("jdbc_url"), is("jdbc:postgresql://127.0.0.1:8888/postgresql"));
		assertThat("datasource_number为<ds_>", resultObj.getString("datasource_number"), is("ds_"));
		assertThat("classify_num为<wzc_test_classify_num1>", resultObj.getString("classify_num"), is("wzc_test_classify_num1"));

		//获取信号文件信息，并断言是否符合期望
		JSONArray collectTableBeanArray = resultObj.getJSONArray("collectTableBeanArray");
		assertThat("采集表配置信息数组长度为<2>", collectTableBeanArray.size(), is(2));
		for(int i = 0; i < collectTableBeanArray.size(); i++){
			JSONObject collectTableBean = collectTableBeanArray.getJSONObject(i);
			Long tableId = collectTableBean.getLong("table_id");
			if(tableId == SYS_USER_TABLE_ID){
				assertThat("<sys_user表>database_id为<1002>", collectTableBean.getLong("database_id"), is(1002L));
				assertThat("<sys_user表>table_name为<sys_user>", collectTableBean.getString("table_name"), is("sys_user"));
				assertThat("<sys_user表>table_ch_name为<用户表>", collectTableBean.getString("table_ch_name"), is("用户表"));
				assertThat("<sys_user表>table_count为空", collectTableBean.getString("table_count"), is(""));
				assertThat("<sys_user表>source_tableid为空", collectTableBean.getString("source_tableid") == null, is(true));
				assertThat("<sys_user表>sql为<select * from sys_user where user_id = 9991>", collectTableBean.getString("sql"), is("select * from sys_user where user_id = 9991"));
				assertThat("<sys_user表>remark为空", collectTableBean.getString("remark") == null, is(true));
				assertThat("<sys_user表>is_user_defined为<否>", collectTableBean.getString("is_user_defined"), is(IsFlag.Fou.getCode()));
				assertThat("<sys_user表>is_md5为<是>", collectTableBean.getString("is_md5"), is(IsFlag.Shi.getCode()));
				assertThat("<sys_user表>is_register为<否>", collectTableBean.getString("is_register"), is(IsFlag.Fou.getCode()));
				assertThat("<sys_user表>is_parallel为<否>", collectTableBean.getString("is_parallel"), is(IsFlag.Fou.getCode()));
				assertThat("<sys_user表>page_sql为<空>", collectTableBean.getString("page_sql"), is(""));
				assertThat("<sys_user表>pageparallels为<5>", collectTableBean.getInteger("pageparallels"), is(5));
				assertThat("<sys_user表>dataincrement为<0>", collectTableBean.getInteger("dataincrement"), is(0));
				assertThat("<sys_user表>is_header为<是>", collectTableBean.getString("is_header"), is(IsFlag.Shi.getCode()));
				assertThat("<sys_user表>data_extract_type为<数据抽取及入库>", collectTableBean.getString("data_extract_type"), is(DataExtractType.ShuJuKuChouQuLuoDi.getCode()));
				assertThat("<sys_user表>database_code为<UTF-8>", collectTableBean.getString("database_code"), is(DataBaseCode.UTF_8.getCode()));
				assertThat("<sys_user表>row_separator为<空>", collectTableBean.getString("row_separator"), is(""));
				assertThat("<sys_user表>database_separatorr为<空>", collectTableBean.getString("database_separatorr"), is(""));
				assertThat("<sys_user表>ded_remark为<空>", collectTableBean.getString("ded_remark") == null, is(true));
				assertThat("<sys_user表>dbfile_format为<>", collectTableBean.getString("dbfile_format"), is(FileFormat.ORC.getCode()));
				assertThat("<sys_user表>plane_url为<空>", collectTableBean.getString("plane_url") == null, is(true));
				assertThat("<sys_user表>file_suffix为<空>", collectTableBean.getString("file_suffix") == null, is(true));
				assertThat("<sys_user表>storage_type为<增量>", collectTableBean.getString("storage_type"), is(StorageType.ZengLiang.getCode()));
				assertThat("<sys_user表>storage_time为<7>", collectTableBean.getLong("storage_time"), is(7L));
				assertThat("<sys_user表>is_zipper为<是>", collectTableBean.getString("is_zipper"), is(IsFlag.Shi.getCode()));
				//注意：hbase_name是多个字段拼接而成的
				assertThat("<sys_user表>hbase_name为<ds__wzc_test_classify_num1_sys_user>", collectTableBean.getString("hbase_name"), is("ds__wzc_test_classify_num1_sys_user"));
				//eltDate是获取的系统当前时间，没法断言
				assertThat("<sys_user表>datasource_name为<wzctest_>", collectTableBean.getString("datasource_name"), is("wzctest_"));
				assertThat("<sys_user表>agent_name为<agent_1>", collectTableBean.getString("agent_name"), is("agent_1"));
				assertThat("<sys_user表>agent_id为<7001>", collectTableBean.getLong("agent_id"), is(DB_AGENT_ID));
				assertThat("<sys_user表>source_id为<1>", collectTableBean.getLong("source_id"), is(SOURCE_ID));

				//获取表采集字段集合
				JSONArray collectTableColumnBeanList = collectTableBean.getJSONArray("collectTableColumnBeanList");
				assertThat("<sys_user表>采集的字段共有<11>个", collectTableColumnBeanList.size(), is(11));
				for(int j = 0; j < collectTableColumnBeanList.size(); j++){
					JSONObject collectTableColumn = collectTableColumnBeanList.getJSONObject(j);
					Long columnId = collectTableColumn.getLong("column_id");
					if(columnId == 2001L){
						assertThat("<sys_user表>采集的字段为<user_id>", collectTableColumn.getString("column_name"), is("user_id"));
						assertThat("<sys_user表>采集的字段为<user_id>，是<主键>", collectTableColumn.getString("is_primary_key"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<user_id>，<字段中文名>为主键", collectTableColumn.getString("column_ch_name"), is("主键"));
						assertThat("<sys_user表>采集的字段为<user_id>，<字段类型>为int8", collectTableColumn.getString("column_type"), is("int8"));
						assertThat("<sys_user表>采集的字段为<user_id>，<是否采集>为是", collectTableColumn.getString("is_get"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<user_id>，<是否保留原字段>为是", collectTableColumn.getString("is_alive"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<user_id>，<清洗顺序>符合期望", collectTableColumn.getString("tc_or"), is(columnCleanOrder.toJSONString()));

						//获取字段清洗信息
						JSONArray columnCleanBeanList = collectTableColumn.getJSONArray("columnCleanBeanList");
						assertThat("<sys_user表>采集的字段为<user_id>，没有设置字段清洗", columnCleanBeanList.size() ,is(0));
					}else if(columnId == 2002L){
						assertThat("<sys_user表>采集的字段为<create_id>", collectTableColumn.getString("column_name"), is("create_id"));
						assertThat("<sys_user表>采集的字段为<create_id>，不是<主键>", collectTableColumn.getString("is_primary_key"), is(IsFlag.Fou.getCode()));
						assertThat("<sys_user表>采集的字段为<create_id>，<字段中文名>为创建用户者ID", collectTableColumn.getString("column_ch_name"), is("创建用户者ID"));
						assertThat("<sys_user表>采集的字段为<create_id>，<字段类型>为int8", collectTableColumn.getString("column_type"), is("int8"));
						assertThat("<sys_user表>采集的字段为<create_id>，<是否采集>为是", collectTableColumn.getString("is_get"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<create_id>，<是否保留原字段>为是", collectTableColumn.getString("is_alive"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<create_id>，<清洗顺序>符合期望", collectTableColumn.getString("tc_or"), is(columnCleanOrder.toJSONString()));

						//获取字段清洗信息
						JSONArray columnCleanBeanList = collectTableColumn.getJSONArray("columnCleanBeanList");
						assertThat("<sys_user表>采集的字段为<create_id>，设置了一个字段清洗", columnCleanBeanList.size() ,is(1));
						JSONObject compObject = columnCleanBeanList.getJSONObject(0);
						assertThat("<sys_user表>采集的字段为<create_id>，设置了一个字段清洗，是字符补齐", compObject.getString("clean_type") ,is(CleanType.ZiFuBuQi.getCode()));
						assertThat("<sys_user表>采集的字段为<create_id>，设置了一个字段清洗，补齐方式为<前补齐>", compObject.getString("filling_type") ,is(FillingType.QianBuQi.getCode()));
						assertThat("<sys_user表>采集的字段为<create_id>，设置了一个字段清洗，补齐字符为<wzc>", compObject.getString("character_filling") ,is(StringUtil.string2Unicode("wzc")));
						assertThat("<sys_user表>采集的字段为<create_id>，设置了一个字段清洗，补齐长度为<3>", compObject.getString("filling_length") ,is("3"));

					}else if(columnId == 2003L){
						assertThat("<sys_user表>采集的字段为<dep_id>", collectTableColumn.getString("column_name"), is("dep_id"));
						assertThat("<sys_user表>采集的字段为<dep_id>，不是<主键>", collectTableColumn.getString("is_primary_key"), is(IsFlag.Fou.getCode()));
						assertThat("<sys_user表>采集的字段为<dep_id>，<字段中文名>为部门ID", collectTableColumn.getString("column_ch_name"), is("部门ID"));
						assertThat("<sys_user表>采集的字段为<dep_id>，<字段类型>为int8", collectTableColumn.getString("column_type"), is("int8"));
						assertThat("<sys_user表>采集的字段为<dep_id>，<是否采集>为是", collectTableColumn.getString("is_get"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<dep_id>，<是否保留原字段>为是", collectTableColumn.getString("is_alive"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<dep_id>，<清洗顺序>符合期望", collectTableColumn.getString("tc_or"), is(columnCleanOrder.toJSONString()));

						//获取字段清洗信息
						JSONArray columnCleanBeanList = collectTableColumn.getJSONArray("columnCleanBeanList");
						assertThat("<sys_user表>采集的字段为<dep_id>，设置了一个字段清洗", columnCleanBeanList.size() ,is(1));
						JSONObject compObject = columnCleanBeanList.getJSONObject(0);
						assertThat("<sys_user表>采集的字段为<dep_id>，设置了一个字段清洗，是字符补齐", compObject.getString("clean_type") ,is(CleanType.ZiFuBuQi.getCode()));
						assertThat("<sys_user表>采集的字段为<dep_id>，设置了一个字段清洗，补齐方式为<后补齐>", compObject.getString("filling_type") ,is(FillingType.HouBuQi.getCode()));
						assertThat("<sys_user表>采集的字段为<dep_id>，设置了一个字段清洗，补齐字符为<空格>", compObject.getString("character_filling") ,is(StringUtil.string2Unicode(" ")));
						assertThat("<sys_user表>采集的字段为<dep_id>，设置了一个字段清洗，补齐长度为<1>", compObject.getString("filling_length") ,is("1"));

					}else if(columnId == 2004L){
						assertThat("<sys_user表>采集的字段为<role_id>", collectTableColumn.getString("column_name"), is("role_id"));
						assertThat("<sys_user表>采集的字段为<role_id>，不是<主键>", collectTableColumn.getString("is_primary_key"), is(IsFlag.Fou.getCode()));
						assertThat("<sys_user表>采集的字段为<role_id>，<字段中文名>为角色ID", collectTableColumn.getString("column_ch_name"), is("角色ID"));
						assertThat("<sys_user表>采集的字段为<role_id>，<字段类型>为int8", collectTableColumn.getString("column_type"), is("int8"));
						assertThat("<sys_user表>采集的字段为<role_id>，<是否采集>为是", collectTableColumn.getString("is_get"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<role_id>，<是否保留原字段>为是", collectTableColumn.getString("is_alive"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<role_id>，<清洗顺序>符合期望", collectTableColumn.getString("tc_or"), is(columnCleanOrder.toJSONString()));

						//获取字段清洗信息
						JSONArray columnCleanBeanList = collectTableColumn.getJSONArray("columnCleanBeanList");
						assertThat("<sys_user表>采集的字段为<role_id>，没有设置字段清洗", columnCleanBeanList.size() ,is(0));
					}else if(columnId == 2005L){
						assertThat("<sys_user表>采集的字段为<user_name>", collectTableColumn.getString("column_name"), is("user_name"));
						assertThat("<sys_user表>采集的字段为<user_name>，不是<主键>", collectTableColumn.getString("is_primary_key"), is(IsFlag.Fou.getCode()));
						assertThat("<sys_user表>采集的字段为<user_name>，<字段中文名>为用户名", collectTableColumn.getString("column_ch_name"), is("用户名"));
						assertThat("<sys_user表>采集的字段为<user_name>，<字段类型>为varchar", collectTableColumn.getString("column_type"), is("varchar"));
						assertThat("<sys_user表>采集的字段为<user_name>，<是否采集>为是", collectTableColumn.getString("is_get"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<user_name>，<是否保留原字段>为是", collectTableColumn.getString("is_alive"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<user_name>，<清洗顺序>符合期望", collectTableColumn.getString("tc_or"), is(columnCleanOrder.toJSONString()));

						//获取字段清洗信息
						JSONArray columnCleanBeanList = collectTableColumn.getJSONArray("columnCleanBeanList");
						assertThat("<sys_user表>采集的字段为<user_name>，设置了一个字段清洗", columnCleanBeanList.size() ,is(1));
						JSONObject compObject = columnCleanBeanList.getJSONObject(0);
						assertThat("<sys_user表>采集的字段为<user_name>，设置了一个字段替换，是字符补齐", compObject.getString("clean_type") ,is(CleanType.ZiFuTiHuan.getCode()));
						assertThat("<sys_user表>采集的字段为<user_name>，设置了一个字段清洗，原字符为<ceshi>", compObject.getString("field") ,is(StringUtil.string2Unicode("ceshi")));
						assertThat("<sys_user表>采集的字段为<user_name>，设置了一个字段清洗，替换字符为<test>", compObject.getString("replace_feild") ,is(StringUtil.string2Unicode("test")));
					}else if(columnId == 2006L){
						assertThat("<sys_user表>采集的字段为<user_password>", collectTableColumn.getString("column_name"), is("user_password"));
						assertThat("<sys_user表>采集的字段为<user_password>，不是<主键>", collectTableColumn.getString("is_primary_key"), is(IsFlag.Fou.getCode()));
						assertThat("<sys_user表>采集的字段为<user_password>，<字段中文名>为密码", collectTableColumn.getString("column_ch_name"), is("密码"));
						assertThat("<sys_user表>采集的字段为<user_password>，<字段类型>为varchar", collectTableColumn.getString("column_type"), is("varchar"));
						assertThat("<sys_user表>采集的字段为<user_password>，<是否采集>为是", collectTableColumn.getString("is_get"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<user_password>，<是否保留原字段>为是", collectTableColumn.getString("is_alive"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<user_password>，<清洗顺序>符合期望", collectTableColumn.getString("tc_or"), is(columnCleanOrder.toJSONString()));

						//获取字段清洗信息
						JSONArray columnCleanBeanList = collectTableColumn.getJSONArray("columnCleanBeanList");
						assertThat("<sys_user表>采集的字段为<user_password>，没有设置字段清洗", columnCleanBeanList.size() ,is(0));
					}else if(columnId == 2007L){
						assertThat("<sys_user表>采集的字段为<user_email>", collectTableColumn.getString("column_name"), is("user_email"));
						assertThat("<sys_user表>采集的字段为<user_email>，不是<主键>", collectTableColumn.getString("is_primary_key"), is(IsFlag.Fou.getCode()));
						assertThat("<sys_user表>采集的字段为<user_email>，<字段中文名>为邮箱", collectTableColumn.getString("column_ch_name"), is("邮箱"));
						assertThat("<sys_user表>采集的字段为<user_email>，<字段类型>为varchar", collectTableColumn.getString("column_type"), is("varchar"));
						assertThat("<sys_user表>采集的字段为<user_email>，<是否采集>为是", collectTableColumn.getString("is_get"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<user_email>，<是否保留原字段>为是", collectTableColumn.getString("is_alive"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<user_email>，<清洗顺序>符合期望", collectTableColumn.getString("tc_or"), is(columnCleanOrder.toJSONString()));

						//获取字段清洗信息
						JSONArray columnCleanBeanList = collectTableColumn.getJSONArray("columnCleanBeanList");
						assertThat("<sys_user表>采集的字段为<user_email>，没有设置字段清洗", columnCleanBeanList.size() ,is(0));
					}else if(columnId == 2008L){
						assertThat("<sys_user表>采集的字段为<user_mobile>", collectTableColumn.getString("column_name"), is("user_mobile"));
						assertThat("<sys_user表>采集的字段为<user_mobile>，不是<主键>", collectTableColumn.getString("is_primary_key"), is(IsFlag.Fou.getCode()));
						assertThat("<sys_user表>采集的字段为<user_mobile>，<字段中文名>为电话", collectTableColumn.getString("column_ch_name"), is("电话"));
						assertThat("<sys_user表>采集的字段为<user_mobile>，<字段类型>为varchar", collectTableColumn.getString("column_type"), is("varchar"));
						assertThat("<sys_user表>采集的字段为<user_mobile>，<是否采集>为是", collectTableColumn.getString("is_get"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<user_mobile>，<是否保留原字段>为是", collectTableColumn.getString("is_alive"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<user_mobile>，<清洗顺序>符合期望", collectTableColumn.getString("tc_or"), is(columnCleanOrder.toJSONString()));

						//获取字段清洗信息
						JSONArray columnCleanBeanList = collectTableColumn.getJSONArray("columnCleanBeanList");
						assertThat("<sys_user表>采集的字段为<user_mobile>，没有设置字段清洗", columnCleanBeanList.size() ,is(0));
					}else if(columnId == 2009L){
						assertThat("<sys_user表>采集的字段为<useris_admin>", collectTableColumn.getString("column_name"), is("useris_admin"));
						assertThat("<sys_user表>采集的字段为<useris_admin>，不是<主键>", collectTableColumn.getString("is_primary_key"), is(IsFlag.Fou.getCode()));
						assertThat("<sys_user表>采集的字段为<useris_admin>，<字段中文名>为是否管理员", collectTableColumn.getString("column_ch_name"), is("是否管理员"));
						assertThat("<sys_user表>采集的字段为<useris_admin>，<字段类型>为char", collectTableColumn.getString("column_type"), is("char"));
						assertThat("<sys_user表>采集的字段为<useris_admin>，<是否采集>为是", collectTableColumn.getString("is_get"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<useris_admin>，<是否保留原字段>为是", collectTableColumn.getString("is_alive"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<useris_admin>，<清洗顺序>符合期望", collectTableColumn.getString("tc_or"), is(columnCleanOrder.toJSONString()));

						//获取字段清洗信息
						JSONArray columnCleanBeanList = collectTableColumn.getJSONArray("columnCleanBeanList");
						assertThat("<sys_user表>采集的字段为<useris_admin>，没有设置字段清洗", columnCleanBeanList.size() ,is(0));
					}else if(columnId == 2010L){
						assertThat("<sys_user表>采集的字段为<user_type>", collectTableColumn.getString("column_name"), is("user_type"));
						assertThat("<sys_user表>采集的字段为<user_type>，不是<主键>", collectTableColumn.getString("is_primary_key"), is(IsFlag.Fou.getCode()));
						assertThat("<sys_user表>采集的字段为<user_type>，<字段中文名>为用户类型", collectTableColumn.getString("column_ch_name"), is("用户类型"));
						assertThat("<sys_user表>采集的字段为<user_type>，<字段类型>为char", collectTableColumn.getString("column_type"), is("char"));
						assertThat("<sys_user表>采集的字段为<user_type>，<是否采集>为是", collectTableColumn.getString("is_get"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<user_type>，<是否保留原字段>为是", collectTableColumn.getString("is_alive"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<user_type>，<清洗顺序>符合期望", collectTableColumn.getString("tc_or"), is(columnCleanOrder.toJSONString()));

						//获取字段清洗信息
						JSONArray columnCleanBeanList = collectTableColumn.getJSONArray("columnCleanBeanList");
						assertThat("<sys_user表>采集的字段为<user_type>，设置了一个字段清洗", columnCleanBeanList.size() ,is(1));
						JSONObject CVCObject = columnCleanBeanList.getJSONObject(0);
						assertThat("<sys_user表>采集的字段为<user_type>，设置了一个字段清洗，是码值转换", CVCObject.getString("clean_type") ,is(CleanType.MaZhiZhuanHuan.getCode()));
						JSONArray array = new JSONArray();
						JSONObject expectedCV = new JSONObject();
						expectedCV.put("code_value", "newValue_one");
						expectedCV.put("orig_value", "oriValue_one");
						array.add(expectedCV);
						assertThat("<sys_user表>采集的字段为<user_type>，设置了一个字段清洗，原码值和新码值符合期望", CVCObject.getString("codeTransform") ,is(array.toJSONString()));

					}else if(columnId == 2011L){
						assertThat("<sys_user表>采集的字段为<login_date>", collectTableColumn.getString("column_name"), is("login_date"));
						assertThat("<sys_user表>采集的字段为<login_date>，不是<主键>", collectTableColumn.getString("is_primary_key"), is(IsFlag.Fou.getCode()));
						assertThat("<sys_user表>采集的字段为<login_date>，<字段中文名>为登录日期", collectTableColumn.getString("column_ch_name"), is("登录日期"));
						assertThat("<sys_user表>采集的字段为<login_date>，<字段类型>为char", collectTableColumn.getString("column_type"), is("char"));
						assertThat("<sys_user表>采集的字段为<login_date>，<是否采集>为是", collectTableColumn.getString("is_get"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<login_date>，<是否保留原字段>为是", collectTableColumn.getString("is_alive"), is(IsFlag.Shi.getCode()));
						assertThat("<sys_user表>采集的字段为<login_date>，<清洗顺序>符合期望", collectTableColumn.getString("tc_or"), is(columnCleanOrder.toJSONString()));

						//获取字段清洗信息
						JSONArray columnCleanBeanList = collectTableColumn.getJSONArray("columnCleanBeanList");
						assertThat("<sys_user表>采集的字段为<login_date>，设置了一个字段清洗", columnCleanBeanList.size() ,is(1));
						JSONObject dateObject = columnCleanBeanList.getJSONObject(0);
						assertThat("<sys_user表>采集的字段为<login_date>，设置了一个字段清洗，是日期格式化", dateObject.getString("clean_type") ,is(CleanType.ShiJianZhuanHuan.getCode()));
						assertThat("<sys_user表>采集的字段为<login_date>，设置了一个字段清洗，是日期格式化，<原始格式为>", dateObject.getString("old_format") ,is("YYYY-MM-DD"));
						assertThat("<sys_user表>采集的字段为<login_date>，设置了一个字段清洗，是日期格式化, <转换格式为>", dateObject.getString("convert_format") ,is("YYYY-MM"));
					}else{
						assertThat("sys_user的采集字段中出现了不符合期望的字段，字段ID为 : " + columnId, true, is(false));
					}
				}
				//获取列合并参数信息
				JSONArray columnMergeList = collectTableBean.getJSONArray("column_merge_list");
				assertThat("给sys_user的采集配置了一条列合并设置", columnMergeList.size(), is(1));
				JSONObject columnMerge = columnMergeList.getJSONObject(0);
				assertThat("给sys_user的采集配置了一条列合并设置, 合并后字段名称为<user_mobile_admin>", columnMerge.getString("col_name"), is("user_mobile_admin"));
				assertThat("给sys_user的采集配置了一条列合并设置, 要合并的字段为<user_mobile和useris_admin>", columnMerge.getString("old_name"), is("user_mobile和useris_admin"));
				assertThat("给sys_user的采集配置了一条列合并设置, 中文名称为<user_mobile_admin_ch>", columnMerge.getString("col_zhname"), is("user_mobile_admin_ch"));
				assertThat("给sys_user的采集配置了一条列合并设置, 备注为空", columnMerge.getString("remark") == null, is(true));
				assertThat("给sys_user的采集配置了一条列合并设置, 字段类型为<varchar(512)>", columnMerge.getString("col_type"), is("varchar(512)"));
				assertThat("给sys_user的采集配置了一条列合并设置, 表ID为<7001>", columnMerge.getLong("table_id"), is(SYS_USER_TABLE_ID));

				//获取表存储配置信息
				JSONArray dataStoreConfBeanArray = collectTableBean.getJSONArray("dataStoreConfBean");
				assertThat("sys_user表只有一个存储目的地", dataStoreConfBeanArray.size(), is(1));
				JSONObject dataStoreConfBean = dataStoreConfBeanArray.getJSONObject(0);
				assertThat("sys_user表只有一个存储目的地, <配置属性名称>为PgSQL", dataStoreConfBean.getString("dsl_name"), is("PgSQL"));
				assertThat("sys_user表只有一个存储目的地, <存储类型>为关系型数据库", dataStoreConfBean.getString("store_type"), is(Store_type.DATABASE.getCode()));
				assertThat("sys_user表只有一个存储目的地, <类型对照名称>为空", dataStoreConfBean.getString("dtcs_name") == null, is(true));
				assertThat("sys_user表只有一个存储目的地, <长度对照名称>为空", dataStoreConfBean.getString("dlcs_name") == null, is(true));

				//获取json对象中的map
				Map<String, Object> innerMap = dataStoreConfBean.getInnerMap();

				//获取data_store_connect_attr，最终获取到的是json格式字符串
				Map<String, String> dataStoreConnectAttr = (Map) innerMap.get("data_store_connect_attr");
				assertThat("由于构造的初始化数据，没有配置文件，所以该集合不为空", dataStoreConnectAttr.size(), is(7));
				for(String key : dataStoreConnectAttr.keySet()){
					if(key.equals("database_name")){
						assertThat("database_name为", dataStoreConnectAttr.get(key), is("hrsdxg"));
					}else if(key.equals("database_pwd")){
						assertThat("database_pwd为", dataStoreConnectAttr.get(key), is("hrsdxg"));
					}else if(key.equals("database_drive")){
						assertThat("database_drive为", dataStoreConnectAttr.get(key), is("org.postgresql.Driver"));
					}else if(key.equals("user_name")){
						assertThat("user_name为", dataStoreConnectAttr.get(key), is("hrsdxg"));
					}else if(key.equals("database_ip")){
						assertThat("database_ip为", dataStoreConnectAttr.get(key), is("47.103.83.1"));
					}else if(key.equals("database_port")){
						assertThat("database_port为", dataStoreConnectAttr.get(key), is("32001"));
					}else if(key.equals("jdbc_url")){
						assertThat("jdbc_url为", dataStoreConnectAttr.get(key), is("jdbc:postgresql://47.103.83.1:32001/hrsdxgtest"));
					}else{
						assertThat("配置关系型数据库配置属性信息出现了不符合期望的情况,key为 : " + key, true, is(false));
					}
				}
				//获取additInfoFieldMap，最终获取到的是json格式字符串
				Map<String, Map<String, Integer>> additInfoFieldMap = (Map) innerMap.get("additInfoFieldMap");
				assertThat("关系型数据库附加信息为主键", additInfoFieldMap.size(), is(1));
				Map<String, Integer> map = additInfoFieldMap.get(StoreLayerAdded.ZhuJian.getCode());
				assertThat("关系型数据库附加信息为主键", map.size(), is(1));
				Integer integer = map.get("user_id");
				assertThat("关系型数据库附加信息为主键, user_id为主键，序号为0", integer, is(0));
				//获取data_store_layer_file，最终获取到的是json格式字符串
				Map<String, String> dataStoreLayerFile = (Map) innerMap.get("data_store_layer_file");
				assertThat("由于构造的初始化数据，没有配置文件，所以该集合为空", dataStoreLayerFile.isEmpty(), is(true));
			}else if(tableId == CODE_INFO_TABLE_ID){
				assertThat("<code_info表>database_id为<1002>", collectTableBean.getLong("database_id"), is(1002L));
				assertThat("<code_info表>table_name为<code_info>", collectTableBean.getString("table_name"), is("code_info"));
				assertThat("<code_info表>table_ch_name为<代码信息表>", collectTableBean.getString("table_ch_name"), is("代码信息表"));
				assertThat("<code_info表>table_count为<100000>", collectTableBean.getString("table_count"), is("100000"));
				assertThat("<code_info表>source_tableid为空", collectTableBean.getString("source_tableid") == null, is(true));
				assertThat("<code_info表>sql为空", collectTableBean.getString("sql"), is(""));
				assertThat("<code_info表>remark为空", collectTableBean.getString("remark") == null, is(true));
				assertThat("<code_info表>is_user_defined为<否>", collectTableBean.getString("is_user_defined"), is(IsFlag.Fou.getCode()));
				assertThat("<code_info表>is_md5为<是>", collectTableBean.getString("is_md5"), is(IsFlag.Shi.getCode()));
				assertThat("<code_info表>is_register为<否>", collectTableBean.getString("is_register"), is(IsFlag.Fou.getCode()));
				assertThat("<code_info表>is_parallel为<是>", collectTableBean.getString("is_parallel"), is(IsFlag.Shi.getCode()));
				assertThat("<code_info表>page_sql为<select * from code_info limit 10>", collectTableBean.getString("page_sql"), is("select * from code_info limit 10"));
				assertThat("<code_info表>pageparallels为<6>", collectTableBean.getInteger("pageparallels"), is(6));
				assertThat("<code_info表>dataincrement为<1000>", collectTableBean.getInteger("dataincrement"), is(1000));
				assertThat("<code_info表>is_header为<是>", collectTableBean.getString("is_header"), is(IsFlag.Shi.getCode()));
				assertThat("<code_info表>data_extract_type为<数据抽取及入库>", collectTableBean.getString("data_extract_type"), is(DataExtractType.ShuJuKuChouQuLuoDi.getCode()));
				assertThat("<code_info表>database_code为<UTF-8>", collectTableBean.getString("database_code"), is(DataBaseCode.UTF_8.getCode()));
				assertThat("<code_info表>row_separator为<空>", collectTableBean.getString("row_separator"), is(""));
				assertThat("<code_info表>database_separatorr为<空>", collectTableBean.getString("database_separatorr"), is(""));
				assertThat("<code_info表>ded_remark为<空>", collectTableBean.getString("ded_remark") == null, is(true));
				assertThat("<code_info表>dbfile_format为<>", collectTableBean.getString("dbfile_format"), is(FileFormat.PARQUET.getCode()));
				assertThat("<code_info表>plane_url为<空>", collectTableBean.getString("plane_url") == null, is(true));
				assertThat("<code_info表>file_suffix为<空>", collectTableBean.getString("file_suffix") == null, is(true));
				assertThat("<code_info表>storage_type为<追加>", collectTableBean.getString("storage_type"), is(StorageType.ZhuiJia.getCode()));
				assertThat("<code_info表>storage_time为<1>", collectTableBean.getLong("storage_time"), is(1L));
				assertThat("<code_info表>is_zipper为<否>", collectTableBean.getString("is_zipper"), is(IsFlag.Fou.getCode()));
				//注意：hbase_name是多个字段拼接而成的
				assertThat("<code_info表>hbase_name为<ds__wzc_test_classify_num1_code_info>", collectTableBean.getString("hbase_name"), is("ds__wzc_test_classify_num1_code_info"));
				//eltDate是获取的系统当前时间，没法断言
				assertThat("<code_info表>datasource_name为<wzctest_>", collectTableBean.getString("datasource_name"), is("wzctest_"));
				assertThat("<code_info表>agent_name为<agent_1>", collectTableBean.getString("agent_name"), is("agent_1"));
				assertThat("<code_info表>agent_id为<7001>", collectTableBean.getLong("agent_id"), is(DB_AGENT_ID));
				assertThat("<code_info表>source_id为<1>", collectTableBean.getLong("source_id"), is(SOURCE_ID));

				//获取表采集字段集合
				JSONArray collectTableColumnBeanList = collectTableBean.getJSONArray("collectTableColumnBeanList");
				assertThat("<sys_user表>采集的字段共有<11>个", collectTableColumnBeanList.size(), is(5));
				for(int j = 0; j < collectTableColumnBeanList.size(); j++){
					JSONObject collectTableColumn = collectTableColumnBeanList.getJSONObject(j);
					Long columnId = collectTableColumn.getLong("column_id");

					if(columnId == 3001L){
						assertThat("<code_info表>采集的字段为<ci_sp_code>", collectTableColumn.getString("column_name"), is("ci_sp_code"));
						assertThat("<code_info表>采集的字段为<ci_sp_code>，是<主键>", collectTableColumn.getString("is_primary_key"), is(IsFlag.Shi.getCode()));
						assertThat("<code_info表>采集的字段为<ci_sp_code>，<字段中文名>为ci_sp_code", collectTableColumn.getString("column_ch_name"), is("ci_sp_code"));
						assertThat("<code_info表>采集的字段为<ci_sp_code>，<字段类型>为varchar", collectTableColumn.getString("column_type"), is("varchar"));
						assertThat("<code_info表>采集的字段为<ci_sp_code>，<是否采集>为是", collectTableColumn.getString("is_get"), is(IsFlag.Shi.getCode()));
						assertThat("<code_info表>采集的字段为<ci_sp_code>，<是否保留原字段>为是", collectTableColumn.getString("is_alive"), is(IsFlag.Shi.getCode()));
						assertThat("<code_info表>采集的字段为<ci_sp_code>，<清洗顺序>符合期望", collectTableColumn.getString("tc_or"), is(columnCleanOrder.toJSONString()));

						//获取字段清洗信息
						JSONArray columnCleanBeanList = collectTableColumn.getJSONArray("columnCleanBeanList");
						assertThat("<code_info表>采集的字段为<ci_sp_code>，没有设置字段清洗", columnCleanBeanList.size() ,is(0));
					}else if(columnId == 3002L){
						assertThat("<code_info表>采集的字段为<ci_sp_class>", collectTableColumn.getString("column_name"), is("ci_sp_class"));
						assertThat("<code_info表>采集的字段为<ci_sp_class>，是<主键>", collectTableColumn.getString("is_primary_key"), is(IsFlag.Shi.getCode()));
						assertThat("<code_info表>采集的字段为<ci_sp_class>，<字段中文名>为ci_sp_class", collectTableColumn.getString("column_ch_name"), is("ci_sp_class"));
						assertThat("<code_info表>采集的字段为<ci_sp_class>，<字段类型>为varchar", collectTableColumn.getString("column_type"), is("varchar"));
						assertThat("<code_info表>采集的字段为<ci_sp_class>，<是否采集>为是", collectTableColumn.getString("is_get"), is(IsFlag.Shi.getCode()));
						assertThat("<code_info表>采集的字段为<ci_sp_class>，<是否保留原字段>为是", collectTableColumn.getString("is_alive"), is(IsFlag.Shi.getCode()));
						assertThat("<code_info表>采集的字段为<ci_sp_class>，<清洗顺序>符合期望", collectTableColumn.getString("tc_or"), is(columnCleanOrder.toJSONString()));

						//获取字段清洗信息
						JSONArray columnCleanBeanList = collectTableColumn.getJSONArray("columnCleanBeanList");
						assertThat("<code_info表>采集的字段为<ci_sp_class>，没有设置字段清洗", columnCleanBeanList.size() ,is(0));
					}else if(columnId == 3003L){
						assertThat("<code_info表>采集的字段为<ci_sp_classname>", collectTableColumn.getString("column_name"), is("ci_sp_classname"));
						assertThat("<code_info表>采集的字段为<ci_sp_classname>，不是<主键>", collectTableColumn.getString("is_primary_key"), is(IsFlag.Fou.getCode()));
						assertThat("<code_info表>采集的字段为<ci_sp_classname>，<字段中文名>为ci_sp_classname", collectTableColumn.getString("column_ch_name"), is("ci_sp_classname"));
						assertThat("<code_info表>采集的字段为<ci_sp_classname>，<字段类型>为varchar", collectTableColumn.getString("column_type"), is("varchar"));
						assertThat("<code_info表>采集的字段为<ci_sp_classname>，<是否采集>为是", collectTableColumn.getString("is_get"), is(IsFlag.Shi.getCode()));
						assertThat("<code_info表>采集的字段为<ci_sp_classname>，<是否保留原字段>为是", collectTableColumn.getString("is_alive"), is(IsFlag.Shi.getCode()));
						assertThat("<code_info表>采集的字段为<ci_sp_classname>，<清洗顺序>符合期望", collectTableColumn.getString("tc_or"), is(columnCleanOrder.toJSONString()));

						//获取字段清洗信息
						JSONArray columnCleanBeanList = collectTableColumn.getJSONArray("columnCleanBeanList");
						assertThat("<code_info表>采集的字段为<ci_sp_classname>，设置了一个列拆分", columnCleanBeanList.size() ,is(1));
						JSONObject columnCleanBean = columnCleanBeanList.getJSONObject(0);
						assertThat("<code_info表>采集的字段为<ci_sp_classname>，设置了一个列拆分", columnCleanBean.getString("clean_type") ,is(CleanType.ZiFuChaiFen.getCode()));
						JSONArray columnSplitList = columnCleanBean.getJSONArray("column_split_list");
						assertThat("给<ci_sp_classname>设置列拆分，按照下划线拆分为三个字段", columnSplitList.size(), is(3));
					}else if(columnId == 3004L){
						assertThat("<code_info表>采集的字段为<ci_sp_name>", collectTableColumn.getString("column_name"), is("ci_sp_name"));
						assertThat("<code_info表>采集的字段为<ci_sp_name>，不是<主键>", collectTableColumn.getString("is_primary_key"), is(IsFlag.Fou.getCode()));
						assertThat("<code_info表>采集的字段为<ci_sp_name>，<字段中文名>为ci_sp_name", collectTableColumn.getString("column_ch_name"), is("ci_sp_name"));
						assertThat("<code_info表>采集的字段为<ci_sp_name>，<字段类型>为varchar", collectTableColumn.getString("column_type"), is("varchar"));
						assertThat("<code_info表>采集的字段为<ci_sp_name>，<是否采集>为是", collectTableColumn.getString("is_get"), is(IsFlag.Shi.getCode()));
						assertThat("<code_info表>采集的字段为<ci_sp_name>，<是否保留原字段>为是", collectTableColumn.getString("is_alive"), is(IsFlag.Shi.getCode()));
						assertThat("<code_info表>采集的字段为<ci_sp_name>，<清洗顺序>符合期望", collectTableColumn.getString("tc_or"), is(columnCleanOrder.toJSONString()));

						//获取字段清洗信息
						JSONArray columnCleanBeanList = collectTableColumn.getJSONArray("columnCleanBeanList");
						assertThat("<code_info表>采集的字段为<ci_sp_name>，设置字段清洗", columnCleanBeanList.size() ,is(1));
						JSONObject columnCleanBean = columnCleanBeanList.getJSONObject(0);
						assertThat("<code_info表>采集的字段为<ci_sp_name>，设置了一个列拆分", columnCleanBean.getString("clean_type") ,is(CleanType.ZiFuChaiFen.getCode()));
						JSONArray columnSplitList = columnCleanBean.getJSONArray("column_split_list");
						assertThat("给<ci_sp_name>设置列拆分，按照偏移量拆分为2个字段", columnSplitList.size(), is(2));
					}else if(columnId == 3005L){
						assertThat("<code_info表>采集的字段为<ci_sp_remark>", collectTableColumn.getString("column_name"), is("ci_sp_remark"));
						assertThat("<code_info表>采集的字段为<ci_sp_remark>，不是<主键>", collectTableColumn.getString("is_primary_key"), is(IsFlag.Fou.getCode()));
						assertThat("<code_info表>采集的字段为<ci_sp_remark>，<字段中文名>ci_sp_remark", collectTableColumn.getString("column_ch_name"), is("ci_sp_remark"));
						assertThat("<code_info表>采集的字段为<ci_sp_remark>，<字段类型>为varchar", collectTableColumn.getString("column_type"), is("varchar"));
						assertThat("<code_info表>采集的字段为<ci_sp_remark>，<是否采集>为是", collectTableColumn.getString("is_get"), is(IsFlag.Shi.getCode()));
						assertThat("<code_info表>采集的字段为<ci_sp_remark>，<是否保留原字段>为是", collectTableColumn.getString("is_alive"), is(IsFlag.Shi.getCode()));
						assertThat("<code_info表>采集的字段为<ci_sp_remark>，<清洗顺序>符合期望", collectTableColumn.getString("tc_or"), is(columnCleanOrder.toJSONString()));

						//获取字段清洗信息
						JSONArray columnCleanBeanList = collectTableColumn.getJSONArray("columnCleanBeanList");
						assertThat("<code_info表>采集的字段为<ci_sp_remark>，设置了一个字段清洗", columnCleanBeanList.size() ,is(0));
					}else{
						assertThat("code_info的采集字段中出现了不符合期望的字段，字段ID为 : " + columnId, true, is(false));
					}
				}
				//获取列合并参数信息
				JSONArray columnMergeList = collectTableBean.getJSONArray("column_merge_list");
				assertThat("给code_info的采集没有配置列合并设置", columnMergeList.size(), is(0));

				//获取表存储配置信息
				JSONArray dataStoreConfBeanArray = collectTableBean.getJSONArray("dataStoreConfBean");
				assertThat("code_info表只有一个存储目的地", dataStoreConfBeanArray.size(), is(1));
				JSONObject dataStoreConfBean = dataStoreConfBeanArray.getJSONObject(0);
				assertThat("code_info表只有一个存储目的地, <配置属性名称>为PgSQL", dataStoreConfBean.getString("dsl_name"), is("PgSQL"));
				assertThat("code_info表只有一个存储目的地, <存储类型>为关系型数据库", dataStoreConfBean.getString("store_type"), is(Store_type.DATABASE.getCode()));
				assertThat("code_info表只有一个存储目的地, <类型对照名称>为空", dataStoreConfBean.getString("dtcs_name") == null, is(true));
				assertThat("code_info表只有一个存储目的地, <长度对照名称>为空", dataStoreConfBean.getString("dlcs_name") == null, is(true));

				//获取json对象中的map
				Map<String, Object> innerMap = dataStoreConfBean.getInnerMap();

				//获取data_store_connect_attr，最终获取到的是json格式字符串
				Map<String, String> dataStoreConnectAttr = (Map) innerMap.get("data_store_connect_attr");
				assertThat("由于构造的初始化数据，没有配置文件，所以该集合不为空", dataStoreConnectAttr.size(), is(7));
				for(String key : dataStoreConnectAttr.keySet()){
					if(key.equals("database_name")){
						assertThat("database_name为", dataStoreConnectAttr.get(key), is("hrsdxg"));
					}else if(key.equals("database_pwd")){
						assertThat("database_pwd为", dataStoreConnectAttr.get(key), is("hrsdxg"));
					}else if(key.equals("database_drive")){
						assertThat("database_drive为", dataStoreConnectAttr.get(key), is("org.postgresql.Driver"));
					}else if(key.equals("user_name")){
						assertThat("user_name为", dataStoreConnectAttr.get(key), is("hrsdxg"));
					}else if(key.equals("database_ip")){
						assertThat("database_ip为", dataStoreConnectAttr.get(key), is("47.103.83.1"));
					}else if(key.equals("database_port")){
						assertThat("database_port为", dataStoreConnectAttr.get(key), is("32001"));
					}else if(key.equals("jdbc_url")){
						assertThat("jdbc_url为", dataStoreConnectAttr.get(key), is("jdbc:postgresql://47.103.83.1:32001/hrsdxgtest"));
					}else{
						assertThat("配置关系型数据库配置属性信息出现了不符合期望的情况,key为 : " + key, true, is(false));
					}
				}
				//获取additInfoFieldMap，最终获取到的是json格式字符串
				Map<String, Map<String, Integer>> additInfoFieldMap = (Map) innerMap.get("additInfoFieldMap");
				assertThat("关系型数据库附加信息为主键", additInfoFieldMap.size(), is(1));
				Map<String, Integer> map = additInfoFieldMap.get(StoreLayerAdded.ZhuJian.getCode());
				assertThat("关系型数据库附加信息为主键", map.size(), is(2));
				for(String key : map.keySet()){
					if(key.equalsIgnoreCase("ci_sp_code")){
						assertThat(map.get(key), is(0));
					}else if(key.equalsIgnoreCase("ci_sp_class")){
						assertThat(map.get(key), is(0));
					}else{
						assertThat("code_info表中只有两个字段做联合主键，但是出现了不符合期望的情况, 字段名为 ： " + key, true, is(false));
					}
				}
				//获取data_store_layer_file，最终获取到的是json格式字符串
				Map<String, String> dataStoreLayerFile = (Map) innerMap.get("data_store_layer_file");
				assertThat("由于构造的初始化数据，没有配置文件，所以该集合为空", dataStoreLayerFile.isEmpty(), is(true));
			}else{
				assertThat("构造数据采集sys_user和code_info两张表，出现了不符合期望的情况，表ID为：" + tableId, true, is(false));
			}
		}

		//获取采集表配置信息，并断言是否符合期望
		JSONArray signalFileList = resultObj.getJSONArray("signal_file_list");
		assertThat("由于编写该部分代码时还没有信号文件，所以信号文件数组为空", signalFileList.isEmpty(), is(true));

		//测试完成后，删除测试数据
		try (DatabaseWrapper db = new DatabaseWrapper()) {

			SqlOperator.execute(db, "delete from " + Collect_job_classify.TableName + " where user_id = ?", TEST_USER_ID);
			SqlOperator.execute(db, "delete from " + Table_info.TableName + " where database_id = ?", 1002L);
			SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ?", SYS_USER_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ?", CODE_INFO_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Orig_code_info.TableName + " where orig_id = ? ", 6001L);
			SqlOperator.execute(db, "delete from " + Orig_code_info.TableName + " where orig_id = ? ", 6002L);
			SqlOperator.execute(db, "delete from " + Orig_code_info.TableName + " where orig_id = ? ", 6003L);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ?", 2002L);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ?", 2003L);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ?", 2005L);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ?", 2011L);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ?", 2010L);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ?", 3003L);
			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ?", 3004L);
			SqlOperator.execute(db, "delete from " + Column_merge.TableName + " where col_merge_id = ?", 16161616L);
			SqlOperator.execute(db, "delete from " + Column_split.TableName + " where column_id = ?", 3003L);
			SqlOperator.execute(db, "delete from " + Column_split.TableName + " where column_id = ?", 3004L);
			SqlOperator.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ?", SYS_USER_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ?", CODE_INFO_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_storage_info.TableName + " where table_id = ?", SYS_USER_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Table_storage_info.TableName + " where table_id = ?", CODE_INFO_TABLE_ID);
			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id = ?", 4399L);
			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id = ?", 4400L);
			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id = ?", 4401L);
			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id = ?", 4402L);
			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id = ?", 4403L);
			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName + " where dsl_id = ? ", 4400L);
			SqlOperator.execute(db, "delete from " + Data_store_layer_added.TableName + " where dsl_id = ? ", 4399L);
			SqlOperator.execute(db, "delete from " + Data_store_layer_added.TableName + " where dsl_id = ? ", 4400L);
			SqlOperator.execute(db, "delete from " + Data_store_layer_added.TableName + " where dsl_id = ? ", 4402L);
			SqlOperator.execute(db, "delete from " + Dtab_relation_store.TableName + " where dsl_id = ? ", 4400L);
			SqlOperator.execute(db, "delete from " + Dcol_relation_store.TableName + " where column_id = ?", 2001);
			SqlOperator.execute(db, "delete from " + Dcol_relation_store.TableName + " where column_id = ?", 3001);
			SqlOperator.execute(db, "delete from " + Dcol_relation_store.TableName + " where column_id = ?", 3002);

			SqlOperator.commitTransaction(db);
		}
	}

	private void buildTestDataForSendTask(){
		try (DatabaseWrapper db = new DatabaseWrapper()) {

			SqlOperator.execute(db, "delete from " + Table_info.TableName + " where table_name = ?", "wzc_test_database_collect_table_name");

			//构造Collect_job_classify表数据
			List<Collect_job_classify> classifies = buildClassifyData();
			//构造table_info表数据
			List<Table_info> tableInfos = new ArrayList<>();
			for(int i = 1; i <= 2; i++){
				long tableId = i % 2 == 0 ? SYS_USER_TABLE_ID : CODE_INFO_TABLE_ID;
				String tableName = i % 2 == 0 ? "sys_user" : "code_info";
				String tableChName = i % 2 == 0 ? "用户表" : "代码信息表";
				String customizeSQL = i % 2 == 0 ? "select * from sys_user where user_id = " + TEST_USER_ID : "";
				String customizFlag = IsFlag.Fou.getCode();
				String parallelFlag = i % 2 == 0 ? IsFlag.Fou.getCode() : IsFlag.Shi.getCode();
				String pageSql = i % 2 == 0 ? "" : "select * from code_info limit 10";
				String tableCount = i % 2 == 0 ? "" : "100000";
				int dataIncrement = i % 2 == 0 ? 0 : 1000;
				int pageParallels = i % 2 == 0 ? 5 : 6;

				Table_info tableInfo = new Table_info();
				tableInfo.setTable_id(tableId);
				tableInfo.setTable_name(tableName);
				tableInfo.setTable_ch_name(tableChName);
				tableInfo.setTable_count(CountNum.ShiWan.getCode());
				tableInfo.setDatabase_id(1002L);
				tableInfo.setValid_s_date(DateUtil.getSysDate());
				tableInfo.setValid_e_date(Constant.MAXDATE);
				tableInfo.setSql(customizeSQL);
				tableInfo.setIs_user_defined(customizFlag);
				tableInfo.setTi_or(tableCleanOrder.toJSONString());
				tableInfo.setIs_md5(IsFlag.Shi.getCode());
				tableInfo.setIs_register(IsFlag.Fou.getCode());
				tableInfo.setIs_parallel(parallelFlag);
				tableInfo.setPage_sql(pageSql);
				tableInfo.setTable_count(tableCount);
				tableInfo.setDataincrement(dataIncrement);
				tableInfo.setPageparallels(pageParallels);

				tableInfos.add(tableInfo);
			}
			//构造table_column表数据
			List<Table_column> sysUsers = new ArrayList<>();
			for(int i = 1; i <= 11; i++){
				String primaryKeyFlag;
				String columnName;
				String columnType;
				String columnChName;
				String remark;
				switch (i){
					case 1 :
						primaryKeyFlag = IsFlag.Shi.getCode();
						columnName = "user_id";
						columnType = "int8";
						columnChName = "主键";
						remark = "1";
						break;
					case 2 :
						primaryKeyFlag = IsFlag.Fou.getCode();
						columnName = "create_id";
						columnType = "int8";
						columnChName = "创建用户者ID";
						remark = "2";
						break;
					case 3 :
						primaryKeyFlag = IsFlag.Fou.getCode();
						columnName = "dep_id";
						columnType = "int8";
						columnChName = "部门ID";
						remark = "3";
						break;
					case 4 :
						primaryKeyFlag = IsFlag.Fou.getCode();
						columnName = "role_id";
						columnType = "int8";
						columnChName = "角色ID";
						remark = "4";
						break;
					case 5 :
						primaryKeyFlag = IsFlag.Fou.getCode();
						columnName = "user_name";
						columnType = "varchar";
						columnChName = "用户名";
						remark = "5";
						break;
					case 6 :
						primaryKeyFlag = IsFlag.Fou.getCode();
						columnName = "user_password";
						columnType = "varchar";
						columnChName = "密码";
						remark = "6";
						break;
					case 7 :
						primaryKeyFlag = IsFlag.Fou.getCode();
						columnName = "user_email";
						columnType = "varchar";
						columnChName = "邮箱";
						remark = "7";
						break;
					case 8 :
						primaryKeyFlag = IsFlag.Fou.getCode();
						columnName = "user_mobile";
						columnType = "varchar";
						columnChName = "电话";
						remark = "8";
						break;
					case 9 :
						primaryKeyFlag = IsFlag.Fou.getCode();
						columnName = "useris_admin";
						columnType = "char";
						columnChName = "是否管理员";
						remark = "9";
						break;
					case 10 :
						primaryKeyFlag = IsFlag.Fou.getCode();
						columnName = "user_type";
						columnType = "char";
						columnChName = "用户类型";
						remark = "10";
						break;
					case 11 :
						primaryKeyFlag = IsFlag.Fou.getCode();
						columnName = "login_date";
						columnType = "char";
						columnChName = "登录日期";
						remark = "11";
						break;
					default:
						primaryKeyFlag = "unexpected_primaryKeyFlag";
						columnName = "unexpected_columnName";
						columnType = "unexpected_columnType";
						columnChName = "unexpected_columnChName";
						remark = "unexpected_remark";
				}
				Table_column sysUserColumn = new Table_column();
				sysUserColumn.setColumn_id(BASE_SYS_USER_PRIMARY + i);
				sysUserColumn.setIs_get(IsFlag.Shi.getCode());
				sysUserColumn.setIs_primary_key(primaryKeyFlag);
				sysUserColumn.setColumn_name(columnName);
				sysUserColumn.setColumn_type(columnType);
				sysUserColumn.setColumn_ch_name(columnChName);
				sysUserColumn.setTable_id(SYS_USER_TABLE_ID);
				sysUserColumn.setValid_s_date(DateUtil.getSysDate());
				sysUserColumn.setValid_e_date(Constant.MAXDATE);
				sysUserColumn.setIs_alive(IsFlag.Shi.getCode());
				sysUserColumn.setIs_new(IsFlag.Fou.getCode());
				sysUserColumn.setTc_or(columnCleanOrder.toJSONString());
				sysUserColumn.setTc_remark(remark);

				sysUsers.add(sysUserColumn);
			}

			List<Table_column> codeInfos = BaseInitData.buildCodeInfoTbColData();

			//构造Orig_code_info表数据
			List<Orig_code_info> origCodeInfos = BaseInitData.buildOrigCodeInfo();

			/*
			 * 构造column_clean表数据
			 * 1、给create_id和dep_id设置字符补齐
			 * 2、给user_name设置字符替换
			 * 3、给user_type设置码值转换
			 * 4、给login_date设置日期格式化
			 * 5、给ci_sp_name（3004L）设置列拆分
			 * 6、给ci_sp_classname（3003L）设置列拆分
			 * */
			List<Column_clean> colComples = new ArrayList<>();
			for(int i = 0; i < 2; i++){
				long colCleanId = i % 2 == 0 ? 22222L : 33333L;
				String cleanType = CleanType.ZiFuBuQi.getCode();
				String compleType = i % 2 == 0 ? FillingType.QianBuQi.getCode() : FillingType.HouBuQi.getCode();
				String compleChar = i % 2 == 0 ? StringUtil.string2Unicode("wzc") : StringUtil.string2Unicode(" ");
				long length = i % 2 == 0 ? 3 : 1;
				long columnId = i % 2 == 0 ? 2002L : 2003L;

				Column_clean colComple = new Column_clean();
				colComple.setCol_clean_id(colCleanId);
				colComple.setClean_type(cleanType);
				colComple.setFilling_type(compleType);
				colComple.setCharacter_filling(compleChar);
				colComple.setFilling_length(length);
				colComple.setColumn_id(columnId);

				colComples.add(colComple);
			}

			Column_clean replace = new Column_clean();
			replace.setCol_clean_id(555555L);
			replace.setColumn_id(2005L);
			replace.setClean_type(CleanType.ZiFuTiHuan.getCode());
			replace.setField(StringUtil.string2Unicode("ceshi"));
			replace.setReplace_feild(StringUtil.string2Unicode("test"));

			Column_clean dateFormat = new Column_clean();
			dateFormat.setCol_clean_id(999999L);
			dateFormat.setColumn_id(2011L);
			dateFormat.setClean_type(CleanType.ShiJianZhuanHuan.getCode());
			dateFormat.setOld_format("YYYY-MM-DD");
			dateFormat.setConvert_format("YYYY-MM");

			Column_clean codeValue = new Column_clean();
			codeValue.setCol_clean_id(999989L);
			codeValue.setColumn_id(2010L);
			codeValue.setCodename("codeClassify_one");
			codeValue.setCodesys("origSysCode_one");
			codeValue.setClean_type(CleanType.MaZhiZhuanHuan.getCode());

			Column_clean spilt = new Column_clean();
			spilt.setCol_clean_id(101010101L);
			spilt.setColumn_id(3004L);
			spilt.setClean_type(CleanType.ZiFuChaiFen.getCode());

			Column_clean spiltTwo = new Column_clean();
			spiltTwo.setCol_clean_id(101010102L);
			spiltTwo.setColumn_id(3003L);
			spiltTwo.setClean_type(CleanType.ZiFuChaiFen.getCode());

			/*
			 * 构造column_merge表数据
			 * 1、对sys_user表中的user_mobile和useris_admin合并成列，名叫user_mobile_admin
			 * 2、由于配置了列合并，需要把合并后的列入到table_column表中
			 * */
			Column_merge columnMerge = new Column_merge();
			columnMerge.setCol_merge_id(16161616L);
			columnMerge.setTable_id(SYS_USER_TABLE_ID);
			columnMerge.setCol_name("user_mobile_admin");
			columnMerge.setOld_name("user_mobile和useris_admin");
			columnMerge.setCol_zhname("user_mobile_admin_ch");
			columnMerge.setCol_type("varchar(512)");
			columnMerge.setValid_s_date(DateUtil.getSysDate());
			columnMerge.setValid_e_date(Constant.MAXDATE);

			Table_column mergeColumn = new Table_column();
			mergeColumn.setColumn_id(1717171717L);
			mergeColumn.setTable_id(SYS_USER_TABLE_ID);
			mergeColumn.setIs_new(IsFlag.Shi.getCode());
			mergeColumn.setIs_primary_key(IsFlag.Fou.getCode());
			mergeColumn.setColumn_name("user_mobile_admin");
			mergeColumn.setColumn_type("varchar(512)");
			mergeColumn.setColumn_ch_name("user_mobile_admin_ch");
			mergeColumn.setValid_s_date(DateUtil.getSysDate());
			mergeColumn.setValid_e_date(Constant.MAXDATE);

			/*
			 * 构造column_split表数据
			 * 1、按照偏移量拆分ci_sp_name
			 * 2、按照下划线拆分ci_sp_classname
			 * 3、由于配置了列拆分，所以要构造模拟数据将拆分后的列加入Table_column表中
			 * */
			List<Column_split> offsetSpilts = new ArrayList<>();
			for(int i = 0; i < 2; i++){
				long colSplitId = i % 2 == 0 ? 1111111L : 2222222L;
				String offset = i % 2 == 0 ? "3" : "0";
				String columnName = i % 2 == 0 ? "ci_sp" : "_name";
				String spiltType = "1";
				String columnChName = i % 2 == 0 ? "ci_sp_ch" : "_name_ch";
				String columnType = "varchar(512)";
				long colCleanId = 101010101L;
				long columnId = 3004L;

				Column_split columnSplit = new Column_split();
				columnSplit.setCol_split_id(colSplitId);
				columnSplit.setCol_offset(offset);
				columnSplit.setCol_name(columnName);
				columnSplit.setSplit_type(spiltType);
				columnSplit.setCol_zhname(columnChName);
				columnSplit.setCol_type(columnType);
				columnSplit.setCol_clean_id(colCleanId);
				columnSplit.setColumn_id(columnId);
				columnSplit.setValid_e_date(Constant.MAXDATE);
				columnSplit.setValid_s_date(DateUtil.getSysDate());

				offsetSpilts.add(columnSplit);
			}

			List<Column_split> underLintSpilts = new ArrayList<>();
			for(int i = 0; i < 3; i++){
				long colSplitId = 0;
				String columnName = null;
				String spiltType = "2";
				String columnChName = null;
				String columnType = "varchar(512)";
				long colCleanId = 101010102L;
				long columnId = 3003L;
				String splitSep = "_";
				long seq = 0;
				switch (i){
					case 0 :
						colSplitId = 101010103L;
						columnName = "ci";
						columnChName = "ci_ch";
						seq = 1;
						break;
					case 1 :
						colSplitId = 101010104L;
						columnName = "sp";
						columnChName = "sp_ch";
						seq = 2;
						break;
					case 2 :
						colSplitId = 101010105L;
						columnName = "classname";
						columnChName = "classname_ch";
						seq = 3;
						break;
				}
				Column_split columnSplit = new Column_split();
				columnSplit.setCol_split_id(colSplitId);
				columnSplit.setCol_name(columnName);
				columnSplit.setSplit_type(spiltType);
				columnSplit.setCol_zhname(columnChName);
				columnSplit.setCol_type(columnType);
				columnSplit.setCol_clean_id(colCleanId);
				columnSplit.setColumn_id(columnId);
				columnSplit.setValid_e_date(Constant.MAXDATE);
				columnSplit.setValid_s_date(DateUtil.getSysDate());
				columnSplit.setSeq(seq);
				columnSplit.setSplit_sep(splitSep);

				underLintSpilts.add(columnSplit);
			}

			List<Table_column> splitOne = new ArrayList<>();
			for(int i = 0; i < 2; i++){
				long columnId = i % 2 == 0 ? 121212L : 232323L;
				String columnName = i % 2 == 0 ? "ci_sp" : "_name";
				String columnChName = i % 2 == 0 ? "ci_sp_ch" : "_name_ch";

				Table_column tableColumn = new Table_column();
				tableColumn.setTable_id(CODE_INFO_TABLE_ID);
				tableColumn.setIs_new(IsFlag.Shi.getCode());
				tableColumn.setColumn_id(columnId);
				tableColumn.setIs_primary_key(IsFlag.Fou.getCode());
				tableColumn.setColumn_name(columnName);
				tableColumn.setColumn_type("varchar(512)");
				tableColumn.setColumn_ch_name(columnChName);
				tableColumn.setValid_s_date(DateUtil.getSysDate());
				tableColumn.setValid_e_date(Constant.MAXDATE);

				splitOne.add(tableColumn);
			}

			List<Table_column> splitTwo = new ArrayList<>();
			for(int i = 0; i < 3; i++){
				long columnId = 0;
				String columnName = null;
				String columnChName = null;

				switch (i){
					case 0 :
						columnId = 141414L;
						columnName = "ci";
						columnChName = "ci_ch";
						break;
					case 1 :
						columnId = 151515L;
						columnName = "sp";
						columnChName = "sp_ch";
						break;
					case 2 :
						columnId = 161616L;
						columnName = "classname";
						columnChName = "classname_ch";
						break;
				}

				Table_column tableColumn = new Table_column();
				tableColumn.setTable_id(CODE_INFO_TABLE_ID);
				tableColumn.setIs_new(IsFlag.Shi.getCode());
				tableColumn.setColumn_id(columnId);
				tableColumn.setIs_primary_key(IsFlag.Fou.getCode());
				tableColumn.setColumn_name(columnName);
				tableColumn.setColumn_type("varchar(512)");
				tableColumn.setColumn_ch_name(columnChName);
				tableColumn.setValid_s_date(DateUtil.getSysDate());
				tableColumn.setValid_e_date(Constant.MAXDATE);

				splitTwo.add(tableColumn);
			}

			//构造Data_extraction_def表数据
			List<Data_extraction_def> extractionDefs = new ArrayList<>();
			for(int i = 0; i < 2; i++){
				long tableId = i % 2 == 0 ? SYS_USER_TABLE_ID : CODE_INFO_TABLE_ID;
				String extractType = DataExtractType.ShuJuKuChouQuLuoDi.getCode();
				String rowSeparator = i % 2 == 0 ? "" : "";
				String databaseSeparatorr = i % 2 == 0 ? "" : "";
				String fileFormat = i % 2 == 0 ? FileFormat.ORC.getCode() : FileFormat.PARQUET.getCode();
				String planeUrl = i % 2 == 0 ? "" : "";

				Data_extraction_def def = new Data_extraction_def();
				def.setDed_id(BASE_EXTRACTION_DEF_ID + i);
				def.setTable_id(tableId);
				def.setData_extract_type(extractType);
				def.setRow_separator(rowSeparator);
				def.setDatabase_separatorr(databaseSeparatorr);
				def.setDatabase_code(DataBaseCode.UTF_8.getCode());
				def.setDbfile_format(fileFormat);
				def.setPlane_url(planeUrl);
				def.setIs_header(IsFlag.Shi.getCode());

				extractionDefs.add(def);
			}

			//构造Table_storage_info表数据
			List<Table_storage_info> tableStorageInfos = new ArrayList<>();
			for(int i = 0; i < 2; i++){
				long id = i % 2 == 0 ? BASE_TB_STORAGE_ID : BASE_TB_STORAGE_ID + 1;
				String fileFormat = i % 2 == 0 ? FileFormat.ORC.getCode() : FileFormat.PARQUET.getCode();
				String storageType = i % 2 == 0 ? StorageType.ZengLiang.getCode() : StorageType.ZhuiJia.getCode();
				String zipperFlag = i % 2 == 0 ? IsFlag.Shi.getCode() : IsFlag.Fou.getCode();
				long tableId = i % 2 == 0 ? SYS_USER_TABLE_ID : CODE_INFO_TABLE_ID;
				long time = i % 2 == 0 ? 7L : 1L;

				Table_storage_info storageInfo = new Table_storage_info();
				storageInfo.setStorage_id(id);
				storageInfo.setFile_format(fileFormat);
				storageInfo.setStorage_type(storageType);
				storageInfo.setIs_zipper(zipperFlag);
				storageInfo.setStorage_time(time);
				storageInfo.setTable_id(tableId);

				tableStorageInfos.add(storageInfo);
			}

			//构造data_store_layer表数据
			List<Data_store_layer> storeLayers = new ArrayList<>();
			for(int i = 0; i < 5; i++){
				String dslName;
				String storeType;
				switch (i){
					case 0 :
						dslName = "SOLR";
						storeType = Store_type.SOLR.getCode();
						break;
					case 1 :
						dslName = "PgSQL";
						storeType = Store_type.DATABASE.getCode();
						break;
					case 2 :
						dslName = "ElasticSearch";
						storeType = Store_type.ElasticSearch.getCode();
						break;
					case 3 :
						dslName = "HBASE";
						storeType = Store_type.HBASE.getCode();
						break;
					case 4 :
						dslName = "MONGODB";
						storeType = Store_type.MONGODB.getCode();
						break;
					default:
						dslName = "unexpected_dslName";
						storeType = "unexpected_storeType";
				}
				Data_store_layer layer = new Data_store_layer();
				layer.setDsl_id(BASE_LAYER_ID + i);
				layer.setDsl_name(dslName);
				layer.setStore_type(storeType);
				layer.setIs_hadoopclient(IsFlag.Fou.getCode());

				storeLayers.add(layer);
			}

			//构造data_store_layer_attr表数据
			List<Data_store_layer_attr> layerAttrs = new ArrayList<>();
			for(int i = 0; i < 7; i++){
				String propertyKey;
				String propertyVal;
				long dslId;
				switch (i){
					case 0 :
						propertyKey = "database_name";
						propertyVal = "hrsdxg";
						dslId = 4400L;
						break;
					case 1 :
						propertyKey = "database_pwd";
						propertyVal = "hrsdxg";
						dslId = 4400L;
						break;
					case 2 :
						propertyKey = "database_drive";
						propertyVal = "org.postgresql.Driver";
						dslId = 4400L;
						break;
					case 3 :
						propertyKey = "user_name";
						propertyVal = "hrsdxg";
						dslId = 4400L;
						break;
					case 4 :
						propertyKey = "database_ip";
						propertyVal = "47.103.83.1";
						dslId = 4400L;
						break;
					case 5 :
						propertyKey = "database_port";
						propertyVal = "32001";
						dslId = 4400L;
						break;
					case 6 :
						propertyKey = "jdbc_url";
						propertyVal = "jdbc:postgresql://47.103.83.1:32001/hrsdxgtest";
						dslId = 4400L;
						break;
					default:
						propertyKey = "unexpected_propertyKey";
						propertyVal = "unexpected_propertyVal";
						dslId = UNEXPECTED_ID;
				}
				Data_store_layer_attr layerAttr = new Data_store_layer_attr();
				layerAttr.setDsla_id(BASE_LAYER_ARR_ID + i);
				layerAttr.setDsl_id(dslId);
				layerAttr.setStorage_property_key(propertyKey);
				layerAttr.setStorage_property_val(propertyVal);
				layerAttr.setIs_file(IsFlag.Fou.getCode());

				layerAttrs.add(layerAttr);
			}

			//构造data_store_layer_added表数据
			List<Data_store_layer_added> layerAddeds = new ArrayList<>();
			for(int i = 0; i < 3; i++){
				long dslId;
				String dslaStorelayer;
				switch (i) {
					case 0 :
						dslId = 4400L;
						dslaStorelayer = StoreLayerAdded.ZhuJian.getCode();
						break;
					case 1 :
						dslId = 4399L;
						dslaStorelayer = StoreLayerAdded.SuoYinLie.getCode();
						break;
					case 2 :
						dslId = 4402L;
						dslaStorelayer = StoreLayerAdded.RowKey.getCode();
						break;
					default:
						dslId = UNEXPECTED_ID;
						dslaStorelayer = "unexpected_dslaStorelayer";
				}
				Data_store_layer_added added = new Data_store_layer_added();
				added.setDslad_id(DATA_STORE_LAYER_ADDED_ID + i);
				added.setDsla_storelayer(dslaStorelayer);
				added.setDsl_id(dslId);

				layerAddeds.add(added);
			}

			//构造data_relation_table表数据，构造user_id、ci_sp_code、ci_sp_class为主键
			List<Dtab_relation_store> relationTables = new ArrayList<>();
			for(int i = 0; i < 2; i++){
				long storageId;
				long dslId;
				switch (i) {
					case 0 :
						storageId = 10669588L;
						dslId = 4400L;
						break;
					case 1 :
						storageId = 10669589L;
						dslId = 4400L;
						break;
					default:
						storageId = UNEXPECTED_ID;
						dslId = UNEXPECTED_ID;
				}
				Dtab_relation_store relationTable = new Dtab_relation_store();
				relationTable.setTab_id(storageId);
				relationTable.setDsl_id(dslId);

				relationTables.add(relationTable);
			}

			//构造column_storage_info表数据
			List<Dcol_relation_store> columnStorageInfos = new ArrayList<>();
			for(int i = 0; i < 3; i++){
				long dsladId;
				long columnId;
				switch (i) {
					case 0 :
						dsladId = 439999L;
						columnId = 2001L;
						break;
					case 1 :
						dsladId = 439999L;
						columnId = 3001L;
						break;
					case 2 :
						dsladId = 439999L;
						columnId = 3002L;
						break;
					default:
						dsladId = UNEXPECTED_ID;
						columnId = UNEXPECTED_ID;
				}
				Dcol_relation_store storageInfo = new Dcol_relation_store();
				storageInfo.setCol_id(columnId);
				storageInfo.setDslad_id(dsladId);

				columnStorageInfos.add(storageInfo);
			}

			int classifyCount = 0;
			for(Collect_job_classify collectJobClassify : classifies){
				int count = collectJobClassify.add(db);
				classifyCount += count;
			}
			assertThat("采集任务分类表测试数据初始化", classifyCount, is(2));

			int tableInfosCount = 0;
			for(Table_info tableInfo : tableInfos){
				int count = tableInfo.add(db);
				tableInfosCount += count;
			}
			assertThat("数据库对应表测试数据初始化", tableInfosCount, is(2));

			int sysUsersCount = 0;
			for(Table_column tableColumn : sysUsers){
				int count = tableColumn.add(db);
				sysUsersCount += count;
			}
			assertThat("<sys_user>表对应的字段测试数据初始化", sysUsersCount, is(11));

			int codeInfosCount = 0;
			for(Table_column tableColumn : codeInfos){
				int count = tableColumn.add(db);
				codeInfosCount += count;
			}
			assertThat("<code_info>表对应的字段测试数据初始化", codeInfosCount, is(5));

			int origCodeInfosCount = 0;
			for(Orig_code_info origCodeInfo : origCodeInfos){
				int count = origCodeInfo.add(db);
				origCodeInfosCount += count;
			}
			assertThat("源系统编码信息测试数据初始化", origCodeInfosCount, is(3));

			int columnCleanCount = 0;
			for(Column_clean columnClean : colComples){
				int count = columnClean.add(db);
				columnCleanCount += count;
			}
			int countSeven = replace.add(db);
			int countOne = dateFormat.add(db);
			int countTwo = codeValue.add(db);
			int countThree = spilt.add(db);
			int countFour = spiltTwo.add(db);

			int countFive = columnMerge.add(db);

			int countSix = mergeColumn.add(db);

			columnCleanCount = columnCleanCount + countSeven + countOne + countTwo + countThree + countFour + countFive + countSix;
			assertThat("列清洗参数信息测试数据初始化", columnCleanCount, is(9));

			int offsetSpiltsCount = 0;
			for(Column_split columnSplit : offsetSpilts){
				int count = columnSplit.add(db);
				offsetSpiltsCount += count;
			}
			assertThat("<按照偏移量>列拆分信息表测试数据初始化", offsetSpiltsCount, is(2));

			int underLintSpiltsCount = 0;
			for(Column_split columnSplit : underLintSpilts){
				int count = columnSplit.add(db);
				underLintSpiltsCount += count;
			}
			assertThat("<按照下划线>列拆分信息表测试数据初始化", underLintSpiltsCount, is(3));

			int splitOneCount = 0;
			for(Table_column tableColumn : splitOne){
				int count = tableColumn.add(db);
				splitOneCount += count;
			}
			assertThat("<按照偏移量>表对应的字段测试数据初始化", splitOneCount, is(2));

			int splitTwoCount = 0;
			for(Table_column tableColumn : splitTwo){
				int count = tableColumn.add(db);
				splitTwoCount += count;
			}
			assertThat("<按照下划线>表对应的字段测试数据初始化", splitTwoCount, is(3));

			int extractionDefsCount = 0;
			for(Data_extraction_def def : extractionDefs){
				int count = def.add(db);
				extractionDefsCount += count;
			}
			assertThat("数据抽取定义测试数据初始化", extractionDefsCount, is(2));

			int tableStorageInfosCount = 0;
			for(Table_storage_info storageInfo : tableStorageInfos){
				int count = storageInfo.add(db);
				tableStorageInfosCount += count;
			}
			assertThat("表存储信息测试数据初始化", tableStorageInfosCount, is(2));

			int storeLayersCount = 0;
			for(Data_store_layer dataStoreLayer : storeLayers){
				int count = dataStoreLayer.add(db);
				storeLayersCount += count;
			}
			assertThat("数据存储层配置表测试数据初始化", storeLayersCount, is(5));

			int layerAttrsCount = 0;
			for(Data_store_layer_attr layerAttr : layerAttrs){
				int count = layerAttr.add(db);
				layerAttrsCount += count;
			}
			assertThat("数据存储层配置属性表测试数据初始化", layerAttrsCount, is(7));

			int layerAddedsCount = 0;
			for(Data_store_layer_added dataStoreLayerAdded : layerAddeds){
				int count = dataStoreLayerAdded.add(db);
				layerAddedsCount += count;
			}
			assertThat("数据存储附加信息表测试数据初始化", layerAddedsCount, is(3));

			int relationTablesCount = 0;
			for(Dtab_relation_store relationTable : relationTables){
				int count = relationTable.add(db);
				relationTablesCount += count;
			}
			assertThat("数据存储关系表测试数据初始化", relationTablesCount, is(2));

			int columnStorageInfosCount = 0;
			for(Dcol_relation_store storageInfo : columnStorageInfos){
				int count = storageInfo.add(db);
				columnStorageInfosCount += count;
			}
			assertThat("字段存储信息测试数据初始化", columnStorageInfosCount, is(3));

			SqlOperator.commitTransaction(db);
		}
	}

	private List<Collect_job_classify> buildClassifyData(){
		List<Collect_job_classify> classifies = new ArrayList<>();
		for(int i = 0; i < 2; i++){
			Collect_job_classify classify = new Collect_job_classify();
			long classifyId = i % 2 == 0 ? FIRST_CLASSIFY_ID : SECOND_CLASSIFY_ID;
			long agentId = i % 2 == 0 ? DF_AGENT_ID : DB_AGENT_ID;
			String remark = "remark" + String.valueOf(classifyId);
			classify.setClassify_id(classifyId);
			classify.setClassify_num("wzc_test_classify_num" + i);
			classify.setClassify_name("wzc_test_classify_name" + i);
			classify.setUser_id(TEST_USER_ID);
			classify.setAgent_id(agentId);
			classify.setRemark(remark);

			classifies.add(classify);
		}

		return classifies;
	}

	/**
	 * 在测试用例执行完之后，删除测试数据
	 *
	 * 1、删除数据源Agent列表信息测试数据
	 *      1-1、删除数据源表(data_source)测试数据
	 *      1-2、删除Agent信息表(agent_info)测试数据
	 * 2、删除agent下任务的信息测试数据
	 *      2-1、删除database_set表测试数据
	 *      2-2、删除object_collect表测试数据
	 *      2-3、删除ftp_collect表测试数据
	 *      2-4、删除file_collect_set表测试数据
	 *      2-5、插入数据
	 * 3、删除各种采集任务相关测试数据
	 *      3-1、删除数据库直连采集测试数据
	 *          3-1-1、删除table_cloumn(数据库对应表)测试数据
	 *          3-1-2、删除table_info(表对应字段)测试数据
	 *          3-2、删除非结构化文件采集测试数据
	 *          3-2-1、构建file_source(文件源设置)测试数据
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@After
	public void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//删除用户信息
			SqlOperator.execute(db, "delete from " + Sys_user.TableName + " WHERE user_id = ?", TEST_USER_ID);
			//删除部门信息
			SqlOperator.execute(db, "delete from " + Department_info.TableName + " WHERE dep_id = ?", TEST_DEPT_ID);
			//1、删除数据源Agent列表信息测试数据
			//1-1、删除数据源表(data_source)测试数据
			SqlOperator.execute(db, "delete from " + Data_source.TableName + " WHERE create_user_id = ?", TEST_USER_ID);
			//1-2、删除Agent信息表(agent_info)测试数据
			SqlOperator.execute(db, "delete from " + Agent_info.TableName + " WHERE user_id = ?", TEST_USER_ID);
			//2、删除agent下任务的信息测试数据
			//2-1、删除database_set表测试数据
			SqlOperator.execute(db, "delete from " + Database_set.TableName + " WHERE agent_id = ?", DB_AGENT_ID);
			SqlOperator.execute(db, "delete from " + Database_set.TableName + " WHERE agent_id = ?", DF_AGENT_ID);
			//2-2、删除object_collect表测试数据
			SqlOperator.execute(db, "delete from " + Object_collect.TableName + " WHERE agent_id = ?", HALF_STRUCT_AGENT_ID);
			//2-3、删除ftp_collect表测试数据
			SqlOperator.execute(db, "delete from " + Ftp_collect.TableName + " WHERE agent_id = ?", FTP_AGENT_ID);
			//2-4、删除file_collect_set表测试数据
			SqlOperator.execute(db, "delete from " + File_collect_set.TableName + " WHERE agent_id = ?", NON_STRUCT_AGENT_ID);
			//3、删除各种采集任务相关测试数据
			//3-1、删除数据库直连采集测试数据
			//3-1-1、删除table_column(数据库对应表)测试数据
			SqlOperator.execute(db, "delete from " + Table_column.TableName + " WHERE table_id = ?", TABLE_ID);
			//3-1-2、删除table_info(表对应字段)测试数据
			SqlOperator.execute(db, "delete from " + Table_info.TableName + " WHERE table_id = ?", TABLE_ID);
			//3-2、删除非结构化文件采集测试数据
			SqlOperator.execute(db, "delete from " + File_source.TableName + " WHERE agent_id = ?", NON_STRUCT_AGENT_ID);

			SqlOperator.commitTransaction(db);
		}
	}
}
