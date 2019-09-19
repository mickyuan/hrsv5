package hrds.b.biz.agent;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @description: AgentListAction的单元测试类
 * @author: WangZhengcheng
 * @create: 2019-09-05 17:41
 **/
//TODO 日志文件下载的测试用例暂无
//TODO 调用工具类生成作业/发送任务测试用例暂无
public class AgentListActionTest extends WebBaseTestCase {

	//测试数据用户ID
	private static final long TEST_USER_ID = -9997L;
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

	/**
	 * 为每个方法的单元测试初始化测试数据，该方法在测试用例中值执行一次
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
	 *      7、table_info表：有一条数据，table_id为100201L，database_id为1002
	 *      8、table_column表：有10条数据，column_id为100200-100209，table_id为100201L
	 *      9、file_source表：有2条数据，file_source_id为400100,400101，fcs_id为4001
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@BeforeClass
	public static void before() {
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
					break; //FIXME 为什么直接break了，已修复
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
			long classifyId = i % 2 == 0 ? 1 : 2;
			long id = i % 2 == 0 ? 1001L : 1002L;
			String databaseType = i % 2 == 0 ? DatabaseType.Postgresql.getCode() : DatabaseType.DB2.getCode();
			Database_set databaseSet = new Database_set();
			databaseSet.setDatabase_id(id);
			databaseSet.setAgent_id(agentId);
			databaseSet.setDatabase_number("dbtest" + i);
			databaseSet.setDb_agent(IsFlag.Shi.getCode());
			databaseSet.setIs_load(IsFlag.Shi.getCode());
			databaseSet.setIs_hidden(IsFlag.Shi.getCode());
			databaseSet.setIs_sendok(IsFlag.Shi.getCode());
			databaseSet.setData_extract_type(DataExtractType.ShuJuChouQuJiRuKu.getCode());
			databaseSet.setIs_header(IsFlag.Shi.getCode());
			databaseSet.setClassify_id(classifyId);
			databaseSet.setTask_name("wzcTaskName" + i);
			databaseSet.setDatabase_type(databaseType);

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
			assertThat("数据库直连采集表对应字段信息测试数据初始化", tableInfoCount, is(1));

			//插入file_source表测试数据
			for(File_source fileSource : fileSources){
				fileSource.add(db);
			}
			assertThat("非结构化文件采集文件源设置测试数据初始化", fileSources.size(), is(2));

			SqlOperator.commitTransaction(db);
		}
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
	 * 4、提交事务后，对数据表中的数据进行检查，断言删除是否成功
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@AfterClass
	public static void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1、删除数据源Agent列表信息测试数据
			//1-1、删除数据源表(data_source)测试数据
			int deleteSourceNum = SqlOperator.execute(db, "delete from " + Data_source.TableName + " WHERE create_user_id = ?", TEST_USER_ID);
			//1-2、删除Agent信息表(agent_info)测试数据
			int deleteAgentNum = SqlOperator.execute(db, "delete from " + Agent_info.TableName + " WHERE user_id = ?", TEST_USER_ID);
			//2、删除agent下任务的信息测试数据
			//2-1、删除database_set表测试数据
			int deleteDsNumOne = SqlOperator.execute(db, "delete from " + Database_set.TableName + " WHERE agent_id = ?", DB_AGENT_ID);
			int deleteDsNumTwo = SqlOperator.execute(db, "delete from " + Database_set.TableName + " WHERE agent_id = ?", DF_AGENT_ID);
			//2-2、删除object_collect表测试数据
			int deleteOcNumOne = SqlOperator.execute(db, "delete from " + Object_collect.TableName + " WHERE agent_id = ?", HALF_STRUCT_AGENT_ID);
			//2-3、删除ftp_collect表测试数据
			int deleteFcNumOne = SqlOperator.execute(db, "delete from " + Ftp_collect.TableName + " WHERE agent_id = ?", FTP_AGENT_ID);
			//2-4、删除file_collect_set表测试数据
			int deleteFcsNumOne = SqlOperator.execute(db, "delete from " + File_collect_set.TableName + " WHERE agent_id = ?", NON_STRUCT_AGENT_ID);
			//3、删除各种采集任务相关测试数据
			//3-1、删除数据库直连采集测试数据
			//3-1-1、删除table_column(数据库对应表)测试数据
			int deleteTableColumnNum = SqlOperator.execute(db, "delete from " + Table_column.TableName + " WHERE table_id = ?", TABLE_ID);
			//3-1-2、删除table_info(表对应字段)测试数据
			int deleteTableInfoNum = SqlOperator.execute(db, "delete from " + Table_info.TableName + " WHERE table_id = ?", TABLE_ID);
			//3-2、删除非结构化文件采集测试数据
			//3-2-1、构建file_source(文件源设置)测试数据
			int deleteFileSourceNum = SqlOperator.execute(db, "delete from " + File_source.TableName + " WHERE agent_id = ?", NON_STRUCT_AGENT_ID);

			SqlOperator.commitTransaction(db);

			//4、提交事务后，对数据表中的数据进行检查，断言删除是否成功
			long dataSources = SqlOperator.queryNumber(db, "select count(1) from " + Data_source.TableName + " WHERE create_user_id = ?", TEST_USER_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的数据源数据有:" + deleteSourceNum + "条", dataSources, is(0L));

			long agents = SqlOperator.queryNumber(db, "select count(1) from " + Agent_info.TableName + " WHERE user_id = ?", TEST_USER_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的Agent数据有:" + deleteAgentNum + "条", agents, is(0L));

			long dataSourceSetsOne = SqlOperator.queryNumber(db, "select count(1) from " + Database_set.TableName + " WHERE agent_id = ?", DB_AGENT_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			long dataSourceSetsTwo = SqlOperator.queryNumber(db, "select count(1) from " + Database_set.TableName + " WHERE agent_id = ?", DF_AGENT_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的数据库设置表数据有:" + (deleteDsNumOne + deleteDsNumTwo) + "条", dataSourceSetsOne + dataSourceSetsTwo, is(0L));

			long objCollectsOne = SqlOperator.queryNumber(db, "select count(1) from " + Object_collect.TableName + " WHERE agent_id = ?", HALF_STRUCT_AGENT_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的对象采集表数据有:" + deleteOcNumOne + "条", objCollectsOne, is(0L));

			long ftpCollectsOne = SqlOperator.queryNumber(db, "select count(1) from " + Ftp_collect.TableName + " WHERE agent_id = ?", FTP_AGENT_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的FTP采集表数据有:" + deleteFcNumOne + "条", ftpCollectsOne, is(0L));

			long fileCollectsOne = SqlOperator.queryNumber(db, "select count(1) from " + File_collect_set.TableName + " WHERE agent_id = ?", NON_STRUCT_AGENT_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的非结构化采集表数据有:" + deleteFcsNumOne + "条", fileCollectsOne, is(0L));

			long tableColumnCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " WHERE table_id = ?", TABLE_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的数据库对应表数据有:" + deleteTableColumnNum + "条", tableColumnCount, is(0L));

			long tableInfoCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " WHERE table_id = ?", TABLE_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的表对应字段表数据有:" + deleteTableInfoNum + "条", tableInfoCount, is(0L));

			long fileSourceCount = SqlOperator.queryNumber(db, "select count(1) from " + File_source.TableName + " WHERE agent_id = ?", NON_STRUCT_AGENT_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("测试完成后删除的数据库对应表数据有:" + deleteFileSourceNum + "条", fileSourceCount, is(0L));
		}
	}

	/**
	 * 测试获取数据源Agent列表信息
	 *
	 * 1、使用正确的userId,http请求访问被测试方法,得到响应，判断结果是否正确
	 * 2、使用错误的userId,http请求访问被测试方法,得到响应，判断结果是否正确
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getAgentInfoList() {
		//1、使用正确的userId,http请求访问被测试方法,得到响应，判断结果是否正确
		String rightString = new HttpClient()
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getAgentInfoList")).getBodyString();

		ActionResult rightResult = JsonUtil.toObject(rightString, ActionResult.class);
		assertThat(rightResult.isSuccess(), is(true));
		List<Object> rightData = (List<Object>) rightResult.getData();
		assertThat("根据测试数据，查询到的数据源信息应该有" + rightData.size() + "条", rightData.size(), is(1));

		//2、使用错误的userId,http请求访问被测试方法,得到响应，判断结果是否正确
		//FIXME 讨论：先清空表，还是判断这个数据存在再换一个（循环N次后抛异常）
		long wrongUserId = 1000L;
		String wrongString = new HttpClient()
				.addData("userId", wrongUserId)
				.post(getActionUrl("getAgentInfoList")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObject(wrongString, ActionResult.class);
		assertThat(wrongResult.isSuccess(), is(true));
		List<Object> wrongData = (List<Object>) wrongResult.getData(); //FIXME 讨论： 改成 ActionResult<T> ~ T getData()？
		assertThat("根据测试数据，查询到的数据源信息应该有" + wrongData.size() + "条", wrongData.size(), is(0));
	}

	/**
	 * 测试根据sourceId和agentType获取相应信息
	 *
	 * 1、http请求访问被测试方法
	 *      1-1、构建请求获取数据库Agent
	 *      1-2、构建请求获取非结构化采集Agent
	 *      1-3、构建请求获取FtpAgent
	 *      1-4、构建请求获取数据文件Agent
	 *      1-5、构建请求获取半结构化Agent
	 * 2、得到响应，判断结果是否正确
	 *
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
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getAgentInfo")).getBodyString();
		//1-2、构建请求获取非结构化采集Agent
		String nonStructResp = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentType", AgentType.WenJianXiTong.getCode())
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getAgentInfo")).getBodyString();
		//1-3、构建请求获取FtpAgent
		String ftpResp = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentType", AgentType.FTP.getCode())
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getAgentInfo")).getBodyString();
		//1-4、构建请求获取数据文件Agent
		String dataFileResp = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentType", AgentType.DBWenJian.getCode())
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getAgentInfo")).getBodyString();
		//1-5、构建请求获取半结构化Agent
		String halfStructResp = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentType", AgentType.DuiXiang.getCode())
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getAgentInfo")).getBodyString();

		//2、得到响应，判断结果是否正确
		ActionResult dbAgent = JsonUtil.toObject(dbAgentResp, ActionResult.class);
		assertThat(dbAgent.isSuccess(), is(true));
		List<Object> dbData = (List<Object>) dbAgent.getData();
		assertThat("根据测试数据，在该数据源下共有" + dbData.size() + "条数据库Agent数据", dbData.size(), is(1));

		ActionResult nonStructAgent = JsonUtil.toObject(nonStructResp, ActionResult.class);
		assertThat(nonStructAgent.isSuccess(), is(true));
		List<Object> nonStructData = (List<Object>) nonStructAgent.getData();
		assertThat("根据测试数据，在该数据源下共有" + nonStructData.size() + "条非结构化Agent数据", nonStructData.size(), is(1));

		ActionResult ftpAgent = JsonUtil.toObject(ftpResp, ActionResult.class);
		assertThat(ftpAgent.isSuccess(), is(true));
		List<Object> ftpData = (List<Object>) ftpAgent.getData();
		assertThat("根据测试数据，在该数据源下共有" + ftpData.size() + "条FTPAgent数据", ftpData.size(), is(1));

		ActionResult dataFileAgent = JsonUtil.toObject(dataFileResp, ActionResult.class);
		assertThat(dataFileAgent.isSuccess(), is(true));
		List<Object> dataFileData = (List<Object>) dataFileAgent.getData();
		assertThat("根据测试数据，在该数据源下共有" + dataFileData.size() + "条FTPAgent数据", dataFileData.size(), is(1));

		ActionResult halfStructAgent = JsonUtil.toObject(halfStructResp, ActionResult.class);
		assertThat(halfStructAgent.isSuccess(), is(true));
		List<Object> halfStructData = (List<Object>) halfStructAgent.getData();
		assertThat("根据测试数据，在该数据源下共有" + halfStructData.size() + "条半结构化Agent数据", halfStructData.size(), is(1));
	}

	/**
	 * 测试根据sourceId和agentId获取某agent下所有任务的信息
	 *
	 * 1、http请求访问被测试方法，agentId传入一个errorAgentId，判断getData()拿到的是否是空字符串
	 * 2、http请求访问被测试方法，agentId传入DB_AGENT_ID，判断结果是否正确
	 * 3、http请求访问被测试方法，agentId传入DF_AGENT_ID，判断结果是否正确
	 * 4、http请求访问被测试方法，agentId传入FTP_AGENT_ID，判断结果是否正确
	 * 5、http请求访问被测试方法，agentId传入HALF_STRUCT_AGENT_ID，判断结果是否正确
	 * 6、http请求访问被测试方法，agentId传入NON_STRUCT_AGENT_ID，判断结果是否正确
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getTaskInfo() {
		//1、http请求访问被测试方法，agentId传入一个errorAgentId，判断getData()拿到的是否是空字符串
		long errorAgentId = 1000L;
		String bodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentId", errorAgentId)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getTaskInfo")).getBodyString();
		ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
		assertThat(ar.isSuccess(), is(false));
		//FIXME 应该是判断 getData 是否为空，而不是判断方法抛出的异常文字。这种文字经常会变，比如做国际化了怎么办，已修复
		assertThat(ar.getData(), is(""));

		//2、http请求访问被测试方法，agentId传入DB_AGENT_ID，判断结果是否正确
		String dbBodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentId", DB_AGENT_ID)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getTaskInfo")).getBodyString();

		ActionResult dbResult = JsonUtil.toObject(dbBodyString, ActionResult.class);
		assertThat(dbResult.isSuccess(), is(true));
		List<Object> firResult = (List<Object>)dbResult.getData();
		assertThat("根据测试数据，查询得到的数据库采集任务有" + firResult.size() + "项", firResult.size(), is(1));

		//3、http请求访问被测试方法，agentId传入DF_AGENT_ID，判断结果是否正确
		String dfBodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentId", DF_AGENT_ID)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getTaskInfo")).getBodyString();

		ActionResult dfResult = JsonUtil.toObject(dfBodyString, ActionResult.class);
		assertThat(dfResult.isSuccess(), is(true));
		List<Object> secResult = (List<Object>)dfResult.getData();
		assertThat("根据测试数据，查询得到的数据库采集任务有" + secResult.size() + "项", secResult.size(), is(1));

		//4、http请求访问被测试方法，agentId传入FTP_AGENT_ID，判断结果是否正确
		String ftpBodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentId", FTP_AGENT_ID)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getTaskInfo")).getBodyString();

		ActionResult ftpResult = JsonUtil.toObject(ftpBodyString, ActionResult.class);
		assertThat(ftpResult.isSuccess(), is(true));
		List<Object> thrResult = (List<Object>)ftpResult.getData();
		assertThat("根据测试数据，查询得到的数据库采集任务有" + thrResult.size() + "项", thrResult.size(), is(2));

		//5、http请求访问被测试方法，agentId传入HALF_STRUCT_AGENT_ID，判断结果是否正确
		String halfBodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentId", HALF_STRUCT_AGENT_ID)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getTaskInfo")).getBodyString();

		ActionResult halfResult = JsonUtil.toObject(halfBodyString, ActionResult.class);
		assertThat(halfResult.isSuccess(), is(true));
		List<Object> fouResult = (List<Object>)halfResult.getData();
		assertThat("根据测试数据，查询得到的数据库采集任务有" + fouResult.size() + "项", fouResult.size(), is(2));

		//6、http请求访问被测试方法，agentId传入NON_STRUCT_AGENT_ID，判断结果是否正确
		String nonBodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("agentId", NON_STRUCT_AGENT_ID)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getTaskInfo")).getBodyString();

		ActionResult nonResult = JsonUtil.toObject(nonBodyString, ActionResult.class);
		assertThat(nonResult.isSuccess(), is(true));
		List<Object> fifResult = (List<Object>)nonResult.getData();
		assertThat("根据测试数据，查询得到的数据库采集任务有" + fifResult.size() + "项", fifResult.size(), is(2));
	}

	/**
	 * 测试根据ID删除半结构化采集任务数据
	 *
	 * 1、删除前查询数据库，确认预期删除的数据存在
	 * 2、http请求逻辑处理方法，删除数据,得到响应，判断删除是否成功
	 *      2-1、构造正确的collectSetId和userId，断言删除是否成功
	 *      2-2、构造正确的collectSetId和错误的userId，断言响应是否失败，获取到的数据是否是空字符串
	 *      2-3、构造错误的collectSetId和正确的userId，断言响应是否失败，获取到的数据是否是空字符串
	 * 3、删除后，确认数据是否被真正删除
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteHalfStructTask() {
		try(DatabaseWrapper db = new DatabaseWrapper()){
			//1、删除前查询数据库，确认预期删除的数据存在
			OptionalLong before = SqlOperator.queryNumber(db, "select count(1) from object_collect where odc_id = ?", 2001L);
			assertThat("删除操作前，object_collect表中的确存在这样一条数据", before.orElse(Long.MIN_VALUE), is(1L));

			//2、http请求逻辑处理方法，删除数据,得到响应，判断删除是否成功
			//2-1、构造正确的collectSetId和userId，断言删除是否成功
			String rightString = new HttpClient()
					.addData("collectSetId", 2001L)
					.addData("userId", TEST_USER_ID)
					.post(getActionUrl("deleteHalfStructTask")).getBodyString();
			ActionResult rightResult = JsonUtil.toObject(rightString, ActionResult.class);
			assertThat(rightResult.isSuccess(), is(true));

			//2-2、构造正确的collectSetId和错误的userId，断言响应是否失败，获取到的数据是否是空字符串
			long wrongUserId = 1000L;
			String wrongUserIdString = new HttpClient()
					.addData("collectSetId", 2001L)
					.addData("userId", wrongUserId)
					.post(getActionUrl("deleteHalfStructTask")).getBodyString();
			ActionResult wrongUserIdResult = JsonUtil.toObject(wrongUserIdString, ActionResult.class);
			assertThat(wrongUserIdResult.isSuccess(), is(false));
			assertThat(wrongUserIdResult.getData(), is(""));

			//2-3、构造错误的collectSetId和正确的userId，断言响应是否失败，获取到的数据是否是空字符串
			long wrongCollectSetId = 2003L;
			String wrongCollectSetIdString = new HttpClient()
					.addData("collectSetId", wrongCollectSetId)
					.addData("userId", TEST_USER_ID)
					.post(getActionUrl("deleteHalfStructTask")).getBodyString();
			ActionResult wrongCollectSetIdResult = JsonUtil.toObject(wrongCollectSetIdString, ActionResult.class);
			assertThat(wrongCollectSetIdResult.isSuccess(), is(false));
			assertThat(wrongCollectSetIdResult.getData(), is(""));

			//3、删除后，确认数据是否被真正删除
			OptionalLong after = SqlOperator.queryNumber(db, "select count(1) from object_collect where odc_id = ?", 2001L);
			assertThat("删除操作后，object_collect表中这样一条数据没有了", after.orElse(Long.MIN_VALUE), is(0L));
		}
	}

	/**
	 * 测试根据ID删除FTP采集任务数据
	 *
	 * 1、删除前查询数据库，确认预期删除的数据存在
	 * 2、http请求逻辑处理方法，删除数据,得到响应，判断删除是否成功
	 *      2-1、构造正确的collectSetId和userId，断言删除是否成功
	 *      2-2、构造正确的collectSetId和错误的userId，断言异常信息是否抛出
	 *      2-3、构造错误的collectSetId和正确的userId，断言异常信息是否抛出
	 * 3、删除后，确认数据是否被真正删除
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteFTPTask() {
		try(DatabaseWrapper db = new DatabaseWrapper()){
			//1、删除前查询数据库，确认预期删除的数据存在
			OptionalLong before = SqlOperator.queryNumber(db, "select count(1) from ftp_collect where ftp_id = ?", 3001L);
			assertThat("删除操作前，ftp_collect表中的确存在这样一条数据", before.orElse(Long.MIN_VALUE), is(1L));

			//2、http请求逻辑处理方法，删除数据，得到响应，判断删除是否成功
			//2-1、构造正确的collectSetId和userId，断言删除是否成功
			String bodyString = new HttpClient()
					.addData("collectSetId", 3001L)
					.addData("userId", TEST_USER_ID)
					.post(getActionUrl("deleteFTPTask")).getBodyString();
			ActionResult ar = JsonUtil.toObject(bodyString, ActionResult.class);
			assertThat(ar.isSuccess(), is(true));

			//2-2、构造正确的collectSetId和错误的userId，断言响应是否失败，获取到的数据是否是空字符串
			long wrongUserId = 1000L;
			String wrongUserIdString = new HttpClient()
					.addData("collectSetId", 3001L)
					.addData("userId", wrongUserId)
					.post(getActionUrl("deleteFTPTask")).getBodyString();
			ActionResult wrongUserIdResult = JsonUtil.toObject(wrongUserIdString, ActionResult.class);
			assertThat(wrongUserIdResult.isSuccess(), is(false));
			assertThat(wrongUserIdResult.getData(), is(""));

			//2-3、构造错误的collectSetId和正确的userId，断言响应是否失败，获取到的数据是否是空字符串
			long wrongCollectSetId = 2003L;
			String wrongCollectSetIdString = new HttpClient()
					.addData("collectSetId", wrongCollectSetId)
					.addData("userId", TEST_USER_ID)
					.post(getActionUrl("deleteFTPTask")).getBodyString();
			ActionResult wrongCollectSetIdResult = JsonUtil.toObject(wrongCollectSetIdString, ActionResult.class);
			assertThat(wrongCollectSetIdResult.isSuccess(), is(false));
			assertThat(wrongCollectSetIdResult.getData(), is(""));

			//3、删除后，确认数据是否被真正删除
			OptionalLong after = SqlOperator.queryNumber(db, "select count(1) from ftp_collect where ftp_id = ?", 3001L);
			assertThat("删除操作后，ftp_collect表中这样一条数据没有了", after.orElse(Long.MIN_VALUE), is(0L));
		}
	}

	/**
	 * 测试根据ID删除数据库直连采集任务
	 *
	 * 1、删除前查询数据库，确认预期删除的数据存在
	 * 2、http请求逻辑处理方法，删除数据,得到响应，判断删除是否成功
	 *      2-1、构造正确的collectSetId和userId，断言删除是否成功
	 *      2-2、构造正确的collectSetId和错误的userId，断言响应是否失败，获取到的数据是否是空字符串
	 *      2-3、构造错误的collectSetId和正确的userId，断言响应是否失败，获取到的数据是否是空字符串
	 * 3、删除后，确认数据是否被真正删除
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteDBTask() {
		try(DatabaseWrapper db = new DatabaseWrapper()){
			//1、删除前查询数据库，确认预期删除的数据存在
			OptionalLong firBefore = SqlOperator.queryNumber(db, "select count(1) from database_set where database_id = ?", 1002L);
			assertThat("删除操作前，database_set表中的确存在这样一条数据", firBefore.orElse(Long.MIN_VALUE), is(1L));

			OptionalLong secBefore = SqlOperator.queryNumber(db, "select count(1) from table_column where table_id = ?", TABLE_ID);
			assertThat("删除操作前，table_column表中的确存在" + secBefore.getAsLong() + "条数据", secBefore.orElse(Long.MIN_VALUE), is(10L));

			OptionalLong thiBefore = SqlOperator.queryNumber(db, "select count(1) from table_info where table_id = ?", TABLE_ID);
			assertThat("删除操作前，table_info表中的确存在一条数据", thiBefore.orElse(Long.MIN_VALUE), is(1L));

			//2、http请求逻辑处理方法，删除数据，得到响应，判断删除是否成功
			//2-1、构造正确的collectSetId和userId，断言删除是否成功
			String rightString = new HttpClient()
					.addData("collectSetId", 1002L)
					.addData("userId", TEST_USER_ID)
					.post(getActionUrl("deleteDBTask")).getBodyString();
			ActionResult ar = JsonUtil.toObject(rightString, ActionResult.class);
			assertThat(ar.isSuccess(), is(true));

			//2-2、构造正确的collectSetId和错误的userId，断言响应是否失败，获取到的数据是否是空字符串
			long wrongUserId = 1000L;
			String wrongUserIdString = new HttpClient()
					.addData("collectSetId", 1002L)
					.addData("userId", wrongUserId)
					.post(getActionUrl("deleteDBTask")).getBodyString();
			ActionResult wrongUserIdResult = JsonUtil.toObject(wrongUserIdString, ActionResult.class);
			assertThat(wrongUserIdResult.isSuccess(), is(false));
			assertThat(wrongUserIdResult.getData(), is(""));

			//2-3、构造错误的collectSetId和正确的userId，断言响应是否失败，获取到的数据是否是空字符串
			long wrongCollectSetId = 2003L;
			String wrongCollectSetIdString = new HttpClient()
					.addData("collectSetId", wrongCollectSetId)
					.addData("userId", TEST_USER_ID)
					.post(getActionUrl("deleteDBTask")).getBodyString();
			ActionResult wrongCollectSetIdResult = JsonUtil.toObject(wrongCollectSetIdString, ActionResult.class);
			assertThat(wrongCollectSetIdResult.isSuccess(), is(false));
			assertThat(wrongCollectSetIdResult.getData(), is(""));

			//3、删除后，确认数据是否被真正删除
			OptionalLong firAfter = SqlOperator.queryNumber(db, "select count(1) from database_set where database_id = ?", 1002L);
			assertThat("删除操作后，database_set表中指定数据没有了", firAfter.orElse(Long.MIN_VALUE), is(0L));

			OptionalLong secAfter = SqlOperator.queryNumber(db, "select count(1) from table_column where table_id = ?", TABLE_ID);
			assertThat("删除操作后，table_column表中指定数据没有了", secAfter.orElse(Long.MIN_VALUE), is(0L));

			OptionalLong thiAfter = SqlOperator.queryNumber(db, "select count(1) from table_info where table_id = ?", TABLE_ID);
			assertThat("删除操作后，table_info表中指定数据没有了", thiAfter.orElse(Long.MIN_VALUE), is(0L));
		}
	}

	/**
	 * 测试根据ID删除数据文件采集任务
	 *
	 * 1、删除前查询数据库，确认预期删除的数据存在
	 * 2、http请求逻辑处理方法，删除数据,得到响应，判断删除是否成功
	 *      2-1、构造正确的collectSetId和userId，断言删除是否成功
	 *      2-2、构造正确的collectSetId和错误的userId，断言响应是否失败，获取到的数据是否是空字符串
	 *      2-3、构造错误的collectSetId和正确的userId，断言响应是否失败，获取到的数据是否是空字符串
	 * 3、删除后，确认数据是否被真正删除
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteDFTask() {
		try(DatabaseWrapper db = new DatabaseWrapper()){
			//1、删除前查询数据库，确认预期删除的数据存在
			OptionalLong firBefore = SqlOperator.queryNumber(db, "select count(1) from database_set where database_id = ?", 1001L);
			assertThat("删除操作前，database_set表中的确存在这样一条数据", firBefore.orElse(Long.MIN_VALUE), is(1L));

			//2、http请求逻辑处理方法，删除数据，得到响应，判断删除是否成功
			//2-1、构造正确的collectSetId和userId，断言删除是否成功
			String rightString = new HttpClient()
					.addData("collectSetId", 1001L)
					.addData("userId", TEST_USER_ID)
					.post(getActionUrl("deleteDFTask")).getBodyString();
			ActionResult ar = JsonUtil.toObject(rightString, ActionResult.class);
			assertThat(ar.isSuccess(), is(true));

			//2-2、构造正确的collectSetId和错误的userId，断言响应是否失败，获取到的数据是否是空字符串
			long wrongUserId = 1000L;
			String wrongUserIdString = new HttpClient()
					.addData("collectSetId", 1001L)
					.addData("userId", wrongUserId)
					.post(getActionUrl("deleteDFTask")).getBodyString();
			ActionResult wrongUserIdResult = JsonUtil.toObject(wrongUserIdString, ActionResult.class);
			assertThat(wrongUserIdResult.isSuccess(), is(false));
			assertThat(wrongUserIdResult.getData(), is(""));

			//2-3、构造错误的collectSetId和正确的userId，断言响应是否失败，获取到的数据是否是空字符串
			long wrongCollectSetId = 2003L;
			String wrongCollectSetIdString = new HttpClient()
					.addData("collectSetId", wrongCollectSetId)
					.addData("userId", TEST_USER_ID)
					.post(getActionUrl("deleteDFTask")).getBodyString();
			ActionResult wrongCollectSetIdResult = JsonUtil.toObject(wrongCollectSetIdString, ActionResult.class);
			assertThat(wrongCollectSetIdResult.isSuccess(), is(false));
			assertThat(wrongCollectSetIdResult.getData(), is(""));

			//3、删除后，确认数据是否被真正删除
			OptionalLong firAfter = SqlOperator.queryNumber(db, "select count(1) from database_set where database_id = ?", 1001L);
			assertThat("删除操作后，database_set表中指定数据没有了", firAfter.orElse(Long.MIN_VALUE), is(0L));
		}
	}

	/**
	 * 测试根据ID删除非结构化文件采集任务
	 *
	 * 1、删除前查询数据库，确认预期删除的数据存在
	 * 2、http请求逻辑处理方法，删除数据,得到响应，判断删除是否成功
	 *      2-1、构造正确的collectSetId和userId，断言删除是否成功
	 *      2-2、构造正确的collectSetId和错误的userId，断言响应是否失败，获取到的数据是否是空字符串
	 *      2-3、构造错误的collectSetId和正确的userId，断言响应是否失败，获取到的数据是否是空字符串
	 * 3、删除后，确认数据是否被真正删除
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteNonStructTask() {
		try(DatabaseWrapper db = new DatabaseWrapper()){
			//1、删除前查询数据库，确认预期删除的数据存在
			OptionalLong firBefore = SqlOperator.queryNumber(db, "select count(1) from file_collect_set where fcs_id = ?", 4001L);
			assertThat("删除操作前，file_collect_set表中的确存在这样一条数据", firBefore.orElse(Long.MIN_VALUE), is(1L));

			OptionalLong secBefore = SqlOperator.queryNumber(db, "select count(1) from file_source where fcs_id = ?", 4001L);
			assertThat("删除操作前，file_collect_set表中的确存在这样一条数据", secBefore.orElse(Long.MIN_VALUE), is(2L));

			//2、http请求逻辑处理方法，删除数据，得到响应，判断数据删除是否成功
			//2-1、构造正确的collectSetId和userId，断言删除是否成功
			String rightString = new HttpClient()
					.addData("collectSetId", 4001L)
					.addData("userId", TEST_USER_ID)
					.post(getActionUrl("deleteNonStructTask")).getBodyString();
			ActionResult ar = JsonUtil.toObject(rightString, ActionResult.class);
			assertThat(ar.isSuccess(), is(true));

			//2-2、构造正确的collectSetId和错误的userId，断言响应是否失败，获取到的数据是否是空字符串
			long wrongUserId = 1000L;
			String wrongUserIdString = new HttpClient()
					.addData("collectSetId", 1001L)
					.addData("userId", wrongUserId)
					.post(getActionUrl("deleteNonStructTask")).getBodyString();
			ActionResult wrongUserIdResult = JsonUtil.toObject(wrongUserIdString, ActionResult.class);
			assertThat(wrongUserIdResult.isSuccess(), is(false));
			assertThat(wrongUserIdResult.getData(), is(""));

			//2-3、构造错误的collectSetId和正确的userId，断言响应是否失败，获取到的数据是否是空字符串
			long wrongCollectSetId = 2003L;
			String wrongCollectSetIdString = new HttpClient()
					.addData("collectSetId", wrongCollectSetId)
					.addData("userId", TEST_USER_ID)
					.post(getActionUrl("deleteNonStructTask")).getBodyString();
			ActionResult wrongCollectSetIdResult = JsonUtil.toObject(wrongCollectSetIdString, ActionResult.class);
			assertThat(wrongCollectSetIdResult.isSuccess(), is(false));
			assertThat(wrongCollectSetIdResult.getData(), is(""));

			//3、删除后，确认数据是否被真正删除
			OptionalLong firAfter = SqlOperator.queryNumber(db, "select count(1) from file_collect_set where fcs_id = ?", 4001L);
			assertThat("删除操作后，file_collect_set表中这样一条数据没有了", firAfter.orElse(Long.MIN_VALUE), is(0L));

			OptionalLong secAfter = SqlOperator.queryNumber(db, "select count(1) from file_source where fcs_id = ?", 4001L);
			assertThat("删除操作后，file_collect_set表中这样一条数据没有了", secAfter.orElse(Long.MIN_VALUE), is(0L));
		}
	}

	/**
	 * 测试根据sourceId查询出设置完成的数据库采集任务和DB文件采集任务的任务ID
	 *
	 * 1、使用正确的userId,http请求访问被测试方法,得到响应，判断结果是否正确
	 * 2、使用错误的userId,http请求访问被测试方法,得到响应，判断结果是否正确
	 * 3、使用错误的sourceId，http请求访问被测试方法,得到响应，判断结果是否正确
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getDBAndDFTaskBySourceId() {
		//1、使用正确的userId,http请求访问被测试方法,得到响应，判断结果是否正确
		String databaseSetString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getDBAndDFTaskBySourceId")).getBodyString();

		ActionResult databaseSetResult = JsonUtil.toObject(databaseSetString, ActionResult.class);
		assertThat(databaseSetResult.isSuccess(), is(true));
		List<Object> firResult = (List<Object>)databaseSetResult.getData();
		assertThat("根据测试数据，查询得到的数据库采集任务和数据文件采集任务有" + firResult.size() + "项", firResult.size(), is(2));

		//2、使用错误的userId,http请求访问被测试方法,得到响应，判断结果是否正确
		long wrongUserId = 1003L;
		String wrongUserIdString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("userId", wrongUserId)
				.post(getActionUrl("getDBAndDFTaskBySourceId")).getBodyString();

		ActionResult wrongUserIdResult = JsonUtil.toObject(wrongUserIdString, ActionResult.class);
		assertThat(wrongUserIdResult.isSuccess(), is(true));
		List<Object> wrongUserIdResultData = (List<Object>)wrongUserIdResult.getData();
		assertThat("根据测试数据，查询得到的数据库采集任务和数据文件采集任务有" + wrongUserIdResultData.size() + "项", wrongUserIdResultData.size(), is(0));

		//3、使用错误的sourceId，http请求访问被测试方法,得到响应，判断结果是否正确
		long wrongSourceId = 2L;
		String wrongSourceIdString = new HttpClient()
				.addData("sourceId", wrongSourceId)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getDBAndDFTaskBySourceId")).getBodyString();

		ActionResult wrongDatabaseSetResult = JsonUtil.toObject(wrongSourceIdString, ActionResult.class);
		assertThat(wrongDatabaseSetResult.isSuccess(), is(true));
		List<Object> wrongDatabaseSetData = (List<Object>)wrongDatabaseSetResult.getData();
		assertThat("根据测试数据，查询得到的数据库采集任务和数据文件采集任务有" + wrongDatabaseSetData.size() + "项", wrongDatabaseSetData.size(), is(0));
	}

	/**
	 * 测试根据sourceId查询出设置完成的非结构化文件采集任务的任务ID
	 *
	 * 1、使用正确的userId,http请求访问被测试方法,得到响应，判断结果是否正确
	 * 2、使用错误的userId,http请求访问被测试方法,得到响应，判断结果是否正确
	 * 3、使用错误的sourceId，http请求访问被测试方法,得到响应，判断结果是否正确
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getNonStructTaskBySourceId() {
		//1、使用正确的userId,http请求访问被测试方法,得到响应，判断结果是否正确
		String nonStructString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getNonStructTaskBySourceId")).getBodyString();

		ActionResult nonStructResult = JsonUtil.toObject(nonStructString, ActionResult.class);
		assertThat(nonStructResult.isSuccess(), is(true));
		List<Object> firResult = (List<Object>)nonStructResult.getData();
		assertThat("根据测试数据，查询得到的非结构化采集任务有" + firResult.size() + "项", firResult.size(), is(2));

		//2、使用错误的userId,http请求访问被测试方法,得到响应，判断结果是否正确
		long wrongUserId = 1003L;
		String wrongUserIdString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("userId", wrongUserId)
				.post(getActionUrl("getNonStructTaskBySourceId")).getBodyString();

		ActionResult wrongUserIdResult = JsonUtil.toObject(wrongUserIdString, ActionResult.class);
		assertThat(wrongUserIdResult.isSuccess(), is(true));
		List<Object> wrongUserIdResultData = (List<Object>)wrongUserIdResult.getData();
		assertThat("根据测试数据，查询得到的非结构化采集任务有" + wrongUserIdResultData.size() + "项", wrongUserIdResultData.size(), is(0));

		//3、使用错误的sourceId，http请求访问被测试方法,得到响应，判断结果是否正确
		long wrongSourceId = 2L;
		String wrongSourceIdString = new HttpClient()
				.addData("sourceId", wrongSourceId)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getNonStructTaskBySourceId")).getBodyString();

		ActionResult wrongDatabaseSetResult = JsonUtil.toObject(wrongSourceIdString, ActionResult.class);
		assertThat(wrongDatabaseSetResult.isSuccess(), is(true));
		List<Object> wrongDatabaseSetData = (List<Object>)wrongDatabaseSetResult.getData();
		assertThat("根据测试数据，查询得到的非结构化采集任务有" + wrongDatabaseSetData.size() + "项", wrongDatabaseSetData.size(), is(0));
	}

	/**
	 * 测试根据sourceId查询出设置完成的半结构化文件采集任务的任务ID
	 *
	 * 1、使用正确的userId,http请求访问被测试方法,得到响应，判断结果是否正确
	 * 2、使用错误的userId,http请求访问被测试方法,得到响应，判断结果是否正确
	 * 3、使用错误的sourceId，http请求访问被测试方法,得到响应，判断结果是否正确
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getHalfStructTaskBySourceId() {
		//1、使用正确的userId,http请求访问被测试方法,得到响应，判断结果是否正确
		String halfStructString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getHalfStructTaskBySourceId")).getBodyString();

		ActionResult halfStructResult = JsonUtil.toObject(halfStructString, ActionResult.class);
		assertThat(halfStructResult.isSuccess(), is(true));
		List<Object> firResult = (List<Object>)halfStructResult.getData();
		assertThat("根据测试数据，查询得到的半结构化采集任务有" + firResult.size() + "项", firResult.size(), is(2));

		//2、使用错误的userId,http请求访问被测试方法,得到响应，判断结果是否正确
		long wrongUserId = 1003L;
		String wrongUserIdString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("userId", wrongUserId)
				.post(getActionUrl("getHalfStructTaskBySourceId")).getBodyString();

		ActionResult wrongUserIdResult = JsonUtil.toObject(wrongUserIdString, ActionResult.class);
		assertThat(wrongUserIdResult.isSuccess(), is(true));
		List<Object> wrongUserIdResultData = (List<Object>)wrongUserIdResult.getData();
		assertThat("根据测试数据，查询得到的半结构化采集任务有" + wrongUserIdResultData.size() + "项", wrongUserIdResultData.size(), is(0));

		//3、使用错误的sourceId，http请求访问被测试方法,得到响应，判断结果是否正确
		long wrongSourceId = 2L;
		String wrongSourceIdString = new HttpClient()
				.addData("sourceId", wrongSourceId)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getHalfStructTaskBySourceId")).getBodyString();

		ActionResult wrongDatabaseSetResult = JsonUtil.toObject(wrongSourceIdString, ActionResult.class);
		assertThat(wrongDatabaseSetResult.isSuccess(), is(true));
		List<Object> wrongDatabaseSetData = (List<Object>)wrongDatabaseSetResult.getData();
		assertThat("根据测试数据，查询得到的半结构化采集任务有" + wrongDatabaseSetData.size() + "项", wrongDatabaseSetData.size(), is(0));
	}

	/**
	 * 测试根据sourceId查询出设置完成的FTP采集任务的任务ID
	 *
	 * 1、使用正确的userId,http请求访问被测试方法,得到响应，判断结果是否正确
	 * 2、使用错误的userId,http请求访问被测试方法,得到响应，判断结果是否正确
	 * 3、使用错误的sourceId，http请求访问被测试方法,得到响应，判断结果是否正确
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getFTPTaskBySourceId() {
		//1、使用正确的userId,http请求访问被测试方法,得到响应，判断结果是否正确
		String ftpString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getFTPTaskBySourceId")).getBodyString();

		ActionResult ftpResult = JsonUtil.toObject(ftpString, ActionResult.class);
		assertThat(ftpResult.isSuccess(), is(true));
		List<Object> firResult = (List<Object>)ftpResult.getData();
		assertThat("根据测试数据，查询得到的FTP采集任务有" + firResult.size() + "项", firResult.size(), is(2));

		//2、使用错误的userId,http请求访问被测试方法,得到响应，判断结果是否正确
		long wrongUserId = 1003L;
		String wrongUserIdString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("userId", wrongUserId)
				.post(getActionUrl("getFTPTaskBySourceId")).getBodyString();

		ActionResult wrongUserIdResult = JsonUtil.toObject(wrongUserIdString, ActionResult.class);
		assertThat(wrongUserIdResult.isSuccess(), is(true));
		List<Object> wrongUserIdResultData = (List<Object>)wrongUserIdResult.getData();
		assertThat("根据测试数据，查询得到的FTP采集任务有" + wrongUserIdResultData.size() + "项", wrongUserIdResultData.size(), is(0));

		//3、使用错误的sourceId，http请求访问被测试方法,得到响应，判断结果是否正确
		long wrongSourceId = 2L;
		String wrongSourceIdString = new HttpClient()
				.addData("sourceId", wrongSourceId)
				.addData("userId", TEST_USER_ID)
				.post(getActionUrl("getFTPTaskBySourceId")).getBodyString();

		ActionResult wrongDatabaseSetResult = JsonUtil.toObject(wrongSourceIdString, ActionResult.class);
		assertThat(wrongDatabaseSetResult.isSuccess(), is(true));
		List<Object> wrongDatabaseSetData = (List<Object>)wrongDatabaseSetResult.getData();
		assertThat("根据测试数据，查询得到的FTP采集任务有" + wrongDatabaseSetData.size() + "项", wrongDatabaseSetData.size(), is(0));
	}

}
