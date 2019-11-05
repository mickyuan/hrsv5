package hrds.b.biz.agent;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
			databaseSet.setIs_load(IsFlag.Shi.getCode());
			databaseSet.setIs_hidden(isHidden);
			databaseSet.setIs_sendok(IsFlag.Fou.getCode());
			databaseSet.setIs_header(IsFlag.Shi.getCode());
			databaseSet.setClassify_id(classifyId);
			databaseSet.setTask_name("wzcTaskName" + i);
			databaseSet.setDatabase_type(databaseType);
			databaseSet.setDatabase_name(databaseName);
			databaseSet.setDatabase_drive(driver);
			databaseSet.setDatabase_ip(ip);
			databaseSet.setDatabase_port(port);
			databaseSet.setDatabase_code(databaseCode);
			databaseSet.setJdbc_url(url);
			databaseSet.setDbfile_format(dbfileFormat);
			databaseSet.setFile_suffix(fileSuffix);
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
			objectCollect.setRun_way(runWay);
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
		//3-1-2、构建table_column(表对应字段)测试数据
		List<Table_column> tableColumns = new ArrayList<>();
		for(int i = 0; i < 10; i++){
			Table_column column = new Table_column();
			column.setColumn_id(100200L + i);
			String isPK = i == 0 ? IsFlag.Shi.getCode() : IsFlag.Fou.getCode();
			column.setIs_primary_key(isPK);
			column.setColume_name("wzcTestColumnName" + i);
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
			for(Agent_info agentInfo : agents){
				agentInfo.add(db);
			}
			assertThat("Agent测试数据初始化", agents.size(), is(5));

			//插入database_set表测试数据
			for(Database_set databaseSet : databases){
				databaseSet.add(db);
			}
			assertThat("数据库设置测试数据初始化", databases.size(), is(2));

			//插入object_collect表测试数据
			for(Object_collect objectCollect : objectCollects){
				objectCollect.add(db);
			}
			assertThat("半结构化文件采集设置测试数据初始化", objectCollects.size(), is(2));

			//插入ftp_collect表测试数据
			for(Ftp_collect ftpCollect : ftps){
				ftpCollect.add(db);
			}
			assertThat("FTP采集设置测试数据初始化", ftps.size(), is(2));

			//插入file_collect_set表测试数据
			for(File_collect_set fileCollectSet : fileCollectSets){
				fileCollectSet.add(db);
			}
			assertThat("非结构化文件采集设置测试数据初始化", fileCollectSets.size(), is(2));

			//插入table_info表测试数据
			int tableInfoCount = tableInfo.add(db);
			assertThat("数据库直连采集数据库对应表信息测试数据初始化", tableInfoCount, is(1));

			//插入table_column表测试数据
			for(Table_column column : tableColumns){
				column.add(db);
			}
			assertThat("数据库直连采集表对应字段信息测试数据初始化", tableColumns.size(), is(10));

			//插入file_source表测试数据
			for(File_source fileSource : fileSources){
				fileSource.add(db);
			}
			assertThat("非结构化文件采集文件源设置测试数据初始化", fileSources.size(), is(2));

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
			OptionalLong before = SqlOperator.queryNumber(db, "select count(1) from object_collect where odc_id = ?", 2001L);
			assertThat("删除操作前，object_collect表中的确存在这样一条数据", before.orElse(Long.MIN_VALUE), is(1L));

			//2、构造正确的collectSetId，http请求逻辑处理方法，删除数据,得到响应，判断删除是否成功
			String rightString = new HttpClient()
					.addData("collectSetId", 2001L)
					.post(getActionUrl("deleteHalfStructTask")).getBodyString();
			ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败!"));
			assertThat(rightResult.isSuccess(), is(true));

			//3、删除后，确认数据是否被真正删除
			OptionalLong after = SqlOperator.queryNumber(db, "select count(1) from object_collect where odc_id = ?", 2001L);
			assertThat("删除操作后，object_collect表中这样一条数据没有了", after.orElse(Long.MIN_VALUE), is(0L));

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
			OptionalLong before = SqlOperator.queryNumber(db, "select count(1) from ftp_collect where ftp_id = ?", 3001L);
			assertThat("删除操作前，ftp_collect表中的确存在这样一条数据", before.orElse(Long.MIN_VALUE), is(1L));

			//2、构造正确的collectSetId，http请求逻辑处理方法，删除数据,得到响应，判断删除是否成功
			String bodyString = new HttpClient()
					.addData("collectSetId", 3001L)
					.post(getActionUrl("deleteFTPTask")).getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("连接失败!"));
			assertThat(ar.isSuccess(), is(true));

			//3、删除后，确认数据是否被真正删除
			OptionalLong after = SqlOperator.queryNumber(db, "select count(1) from ftp_collect where ftp_id = ?", 3001L);
			assertThat("删除操作后，ftp_collect表中这样一条数据没有了", after.orElse(Long.MIN_VALUE), is(0L));

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
			OptionalLong firAfter = SqlOperator.queryNumber(db, "select count(1) from database_set where database_id = ?", 1002L);
			assertThat("删除操作后，database_set表中指定数据没有了", firAfter.orElse(Long.MIN_VALUE), is(0L));

			OptionalLong secAfter = SqlOperator.queryNumber(db, "select count(1) from table_column where table_id = ?", TABLE_ID);
			assertThat("删除操作后，table_column表中指定数据没有了", secAfter.orElse(Long.MIN_VALUE), is(0L));

			OptionalLong thiAfter = SqlOperator.queryNumber(db, "select count(1) from table_info where table_id = ?", TABLE_ID);
			assertThat("删除操作后，table_info表中指定数据没有了", thiAfter.orElse(Long.MIN_VALUE), is(0L));

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
