package hrds.b.biz.dataquery;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.FileType;
import hrds.commons.entity.*;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "Web服务查询数据界面后台处理测试类", author = "BY-HLL", createdate = "2019/9/3 0003 下午 03:26")
public class DataQueryActionTest extends WebBaseTestCase {
	//测试数据的用户ID
	private static final long USER_ID = 5000L;
	//测试数据的数据源ID
	private static final long SOURCE_ID = 5000000000L;
	//测试数据的部门ID
	private static final long DEP_ID = 5000000000L;
	//测试数据的采集Agent初始化ID，用来初始化数据
	private static final long AGENT_ID = 5000000000L;
	//测试数据的文件采集任务初始化ID，用来初始化数据
	private static final long FCS_ID = 5000000000L;
	//测试数据的文件id
	private static final String FILE_ID = "999999999999999999999999";
	//测试数据 data_auth 的ID
	private static final long DA_ID = 5000000000L;
	//测试数据 search_info 的ID
	private static final long SI_ID = 5000000000L;
	//测试数据 user_fav 的ID
	private static final long FAV_ID = 5000000000L;

	private static String bodyString;
	private static ActionResult ar;

	@Method(desc = "初始化测试用例依赖表数据",
			logicStep = "1.初始化 Data_source 数据" +
					"2.初始化 Source_relation_dep 数据" +
					"3.初始化 Agent_info 数据" +
					"4.初始化 File_collect_set 数据" +
					"5.初始化 Source_file_attribute 数据" +
					"6.初始化 Data_auth 数据" +
					"7.初始化 Search_info 数据" +
					"8.初始化 User_fav 数据" +
					"9.初始化 Sys_user 数据" +
					"10.初始化 Department_info 数据" +
					"11.提交所有数据库执行操作" +
					"12.根据初始化的 Sys_user 用户模拟登陆" +
					"测试数据:" +
					"* 1.data_source表中有1条数据 source_id:5000000000 create_user_id:5000" +
					"* 2.Source_relation_dep表中有1条数据 dep_id:5000000000 source_id:5000000000" +
					"* 3.Agent_info表中有5条数据 agent_id:5000000001-5000000005 agent_type:1-5 source_id:5000000000 " +
					"user_id:5000" +
					"* 4.File_collect_set表中有1条数据 File_collect_set:5000000000 agent_id: 5000000002" +
					"* 5.Source_file_attribute表中有1条数据 file_id: 999999999999999999999999 agent_id:5000000002" +
					"* source_id:5000000000 collect_set_id:5000000000" +
					"* 6.Data_auth表中有1条数据 da_id:5000000000 file_id:999999999999999999999999 user_id:5000" +
					"* dep_id:5000000000 agent_id:5000000002 source_id:5000000000 collect_set_id:5000000000" +
					"* 7.Search_info表中有1条数据 si_id:5000000000 file_id:999999999999999999999999" +
					"* 8.User_fav表中有1条数据 fav_id:5000000000 file_id:999999999999999999999999 user_id:5000" +
					"* 9.Sys_user表中有1条数据 user_id: 5000 dep_id: 5000000000 role_id: 1001" +
					"* 10.Department_info表中有1条数据 dep_id: 5000000000"
	)
	@BeforeClass
	public static void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1.初始化 Data_source 数据
			Data_source dataSource = new Data_source();
			dataSource.setSource_id(SOURCE_ID);
			dataSource.setDatasource_number("init-hll");
			dataSource.setDatasource_name("init-hll");
			dataSource.setSource_remark("init-hll");
			dataSource.setCreate_date(DateUtil.getSysDate());
			dataSource.setCreate_time(DateUtil.getSysTime());
			dataSource.setCreate_user_id(USER_ID);
			dataSource.add(db);
			//2.初始化 Source_relation_dep 数据
			Source_relation_dep sourceRelationDep = new Source_relation_dep();
			sourceRelationDep.setDep_id(DEP_ID);
			sourceRelationDep.setSource_id(SOURCE_ID);
			sourceRelationDep.add(db);
			//3.初始化 Agent_info 数据
			Agent_info agentInfo = new Agent_info();
			for (int i = 1; i <= 5; i++) {
				String agentType = "0";
				long agentId = 0L;
				switch (i) {
					case 1:
						agentType = AgentType.ShuJuKu.getCode();
						agentId = AGENT_ID + 1;
						break;
					case 2:
						agentType = AgentType.WenJianXiTong.getCode();
						agentId = AGENT_ID + 2;
						break;
					case 3:
						agentType = AgentType.FTP.getCode();
						agentId = AGENT_ID + 3;
						break;
					case 4:
						agentType = AgentType.DBWenJian.getCode();
						agentId = AGENT_ID + 4;
						break;
					case 5:
						agentType = AgentType.DuiXiang.getCode();
						agentId = AGENT_ID + 5;
						break;

				}
				agentInfo.setAgent_id(agentId);
				agentInfo.setAgent_name("init-hll" + i);
				agentInfo.setAgent_type(agentType);
				agentInfo.setAgent_ip("127.0.0.1");
				agentInfo.setAgent_port("8888");
				agentInfo.setAgent_status("1");
				agentInfo.setCreate_date(DateUtil.getSysDate());
				agentInfo.setCreate_time(DateUtil.getSysTime());
				agentInfo.setSource_id(SOURCE_ID);
				agentInfo.setUser_id(USER_ID);
				agentInfo.add(db);
			}
			//4.初始化 File_collect_set 数据
			File_collect_set fileCollectSet = new File_collect_set();
			fileCollectSet.setFcs_id(FCS_ID);
			//初始化数据时agent_id是(AGENT_ID + 2)的Agent是文件采集Agent
			fileCollectSet.setAgent_id(AGENT_ID + 2);
			fileCollectSet.setFcs_name("init-hll");
			fileCollectSet.setHost_name("init-hll");
			fileCollectSet.setSystem_type("linux");
			fileCollectSet.setIs_sendok("1");
			fileCollectSet.setIs_solr("1");
			fileCollectSet.setRemark("init-hll");
			fileCollectSet.add(db);
			//5.初始化 Source_file_attribute 数据
			for (int i = 0; i < 5; i++) {
				Source_file_attribute sourceFileAttribute = new Source_file_attribute();
				sourceFileAttribute.setFile_id(FILE_ID + i);
				sourceFileAttribute.setIs_in_hbase("0");
				sourceFileAttribute.setSeqencing(0L);
				sourceFileAttribute.setCollect_type(AgentType.WenJianXiTong.getCode());
				sourceFileAttribute.setOriginal_name("init-hll");
				sourceFileAttribute.setOriginal_update_date(DateUtil.getSysDate());
				sourceFileAttribute.setOriginal_update_time(DateUtil.getSysTime());
				sourceFileAttribute.setTable_name("init-hll");
				sourceFileAttribute.setHbase_name("init-hll");
				sourceFileAttribute.setMeta_info("init-hll");
				sourceFileAttribute.setStorage_date(String.valueOf(20190901 + i));
				sourceFileAttribute.setStorage_time(DateUtil.getSysTime());
				sourceFileAttribute.setFile_size(1024L);
				sourceFileAttribute.setFile_type(FileType.WenDang.getCode());
				sourceFileAttribute.setFile_suffix("init-hll");
				sourceFileAttribute.setSource_path("init-hll");
				sourceFileAttribute.setFile_md5("init-hll");
				sourceFileAttribute.setFile_avro_path("init-hll");
				sourceFileAttribute.setFile_avro_block(1024L);
				sourceFileAttribute.setIs_big_file("0");
				sourceFileAttribute.setIs_cache("0");
				sourceFileAttribute.setFolder_id(10L);
				sourceFileAttribute.setAgent_id(AGENT_ID + 2);
				sourceFileAttribute.setSource_id(SOURCE_ID);
				sourceFileAttribute.setCollect_set_id(FCS_ID);
				sourceFileAttribute.add(db);
			}
			//6.初始化 Data_auth 数据
			Data_auth dataAuth = new Data_auth();
			dataAuth.setDa_id(DA_ID);
			dataAuth.setApply_date(DateUtil.getSysDate());
			dataAuth.setApply_time(DateUtil.getSysTime());
			dataAuth.setApply_type("2");
			dataAuth.setAuth_type("1");
			dataAuth.setAudit_date(DateUtil.getSysDate());
			dataAuth.setAudit_time(DateUtil.getSysTime());
			dataAuth.setAudit_userid(USER_ID);
			dataAuth.setAudit_name("init-hll");
			dataAuth.setFile_id(FILE_ID + 4);
			dataAuth.setUser_id(USER_ID);
			dataAuth.setDep_id(DEP_ID);
			dataAuth.setAgent_id(AGENT_ID + 2);
			dataAuth.setSource_id(SOURCE_ID);
			dataAuth.setCollect_set_id(FCS_ID);
			dataAuth.add(db);
			//7.初始化 Search_info 数据
			Search_info searchInfo = new Search_info();
			searchInfo.setSi_id(SI_ID);
			searchInfo.setFile_id(FILE_ID);
			searchInfo.setWord_name("init-hll");
			searchInfo.setSi_count(0L);
			searchInfo.setSi_remark("init-hll");
			searchInfo.add(db);
			//8.初始化 User_fav 数据
			User_fav userFav = new User_fav();
			userFav.setFav_id(FAV_ID);
			userFav.setOriginal_name("init-hll");
			userFav.setFile_id(FILE_ID);
			userFav.setUser_id(USER_ID);
			userFav.setFav_flag("1");
			userFav.add(db);
			//9.初始化 Sys_user 数据
			Sys_user sysUser = new Sys_user();
			sysUser.setUser_id(USER_ID);
			sysUser.setCreate_id(1000L);
			sysUser.setDep_id(DEP_ID);
			sysUser.setRole_id(1001L);
			sysUser.setUser_name("init-hll");
			sysUser.setUser_password("111111");
			sysUser.setUseris_admin("0");
			sysUser.setUser_state("0");
			sysUser.setCreate_date(DateUtil.getSysDate());
			sysUser.setToken("0");
			sysUser.setValid_time(DateUtil.getSysTime());
			sysUser.add(db);
			//10.初始化 Department_info 数据
			Department_info departmentInfo = new Department_info();
			departmentInfo.setDep_id(DEP_ID);
			departmentInfo.setDep_name("init-hll");
			departmentInfo.setCreate_date(DateUtil.getSysDate());
			departmentInfo.setCreate_time(DateUtil.getSysTime());
			departmentInfo.add(db);
			//11.提交所有数据库执行操作
			SqlOperator.commitTransaction(db);
			//12.根据初始化的 Sys_user 用户模拟登陆
			bodyString = new HttpClient()
					.addData("user_id", USER_ID)
					.addData("password", "111111")
					.post("http://127.0.0.1:8099/A/action/hrds/a/biz/login/login").getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
			assertThat(ar.isSuccess(), is(true));
		}
	}

	@Method(desc = "测试案例执行完成后清理测试数据",
			logicStep = "1.删除 Data_source 表测试数据" +
					"2.删除 Source_relation_dep 测试数据" +
					"3.删除 Agent_info 测试数据" +
					"4.删除 File_collect_set 测试数据" +
					"5.删除 Source_file_attribute 测试数据" +
					"6.删除 Data_auth 测试数据" +
					"7.删除 Search_info 表测试数据" +
					"8.删除 User_fav 表测试数据")
	@AfterClass
	public static void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//1.删除 Data_source 表测试数据
			SqlOperator.execute(db,
					"delete from " + Data_source.TableName + " where source_id=?", SOURCE_ID);
			SqlOperator.commitTransaction(db);
			long dsDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Data_source.TableName + " where source_id =?",
					SOURCE_ID
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("data_source 表此条数据删除后,记录数应该为0", dsDataNum, is(0L));
			//2.删除 Source_relation_dep 测试数据
			SqlOperator.execute(db,
					"delete from " + Source_relation_dep.TableName + " where source_id=?", DEP_ID);
			SqlOperator.commitTransaction(db);
			long srdDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Source_relation_dep.TableName + " where" +
							" source_id=?", DEP_ID
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("source_relation_dep 表此条数据删除后,记录数应该为0", srdDataNum, is(0L));
			//3.删除 Agent_info 测试数据
			for (int i = 1; i <= 5; i++) {
				SqlOperator.execute(db,
						"delete from " + Agent_info.TableName + " where agent_id=?",
						AGENT_ID + i);
				SqlOperator.commitTransaction(db);
				long aiDataNum = SqlOperator.queryNumber(db,
						"select count(1) from " + Agent_info.TableName + " where agent_id=?",
						AGENT_ID + i
				).orElseThrow(() -> new RuntimeException("count fail!"));
				assertThat("agent_info 表此条数据删除后,记录数应该为0", aiDataNum, is(0L));
			}
			//4.删除 File_collect_set 测试数据
			SqlOperator.execute(db,
					"delete from " + File_collect_set.TableName + " where fcs_id=?", FCS_ID);
			SqlOperator.commitTransaction(db);
			long fcsDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + File_collect_set.TableName + " where fcs_id=?", FCS_ID
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("file_collect_set 表此条数据删除后,记录数应该为0", fcsDataNum, is(0L));
			//5.删除 Source_file_attribute 测试数据
			for (int i = 0; i < 5; i++) {
				SqlOperator.execute(db,
						"delete from " + Source_file_attribute.TableName + " where file_id=?",
						FILE_ID + i);
				SqlOperator.commitTransaction(db);
				long sfaDataNum = SqlOperator.queryNumber(db,
						"select count(1) from " + Source_file_attribute.TableName + " where " +
								"file_id=?", FILE_ID + i
				).orElseThrow(() -> new RuntimeException("count fail!"));
				assertThat("source_file_attribute 表此条数据删除后,记录数应该为0", sfaDataNum, is(0L));
			}
			//6.删除 Data_auth 测试数据
			SqlOperator.execute(db,
					"delete from " + Data_auth.TableName + " where da_id=?", DA_ID);
			SqlOperator.commitTransaction(db);
			long daDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Data_auth.TableName + " where da_id=?", DA_ID
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("data_auth 表此条数据删除后,记录数应该为0", daDataNum, is(0L));
			//7.删除 Search_info 表测试数据
			SqlOperator.execute(db,
					"delete from " + Search_info.TableName + " where si_id=?", SI_ID);
			SqlOperator.commitTransaction(db);
			// 测试完成后删除 Search_info 表中生成的废数据
			SqlOperator.execute(db,
					"delete from " + Search_info.TableName + " where file_id=?", FILE_ID);
			SqlOperator.commitTransaction(db);
			long siDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Search_info.TableName + " where si_id=?", SI_ID
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("search_info 表此条数据删除后,记录数应该为0", siDataNum, is(0L));
			//8.删除 User_fav 表测试数据
			SqlOperator.execute(db,
					"delete from " + User_fav.TableName + " where fav_id=?", FAV_ID);
			SqlOperator.commitTransaction(db);
			// 测试完成后删除 User_fav 表中生成的废数据
			SqlOperator.execute(db,
					"delete from " + User_fav.TableName + " where file_id=?", FILE_ID + 0);
			SqlOperator.commitTransaction(db);
			long ufDataNum = SqlOperator.queryNumber(db,
					"select count(1) from " + User_fav.TableName + " where fav_id=?", FAV_ID
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("User_fav 表此条数据删除后,记录数应该为0", ufDataNum, is(0L));
			//9.删除 Sys_user 表测试数据
			SqlOperator.execute(db,
					"delete from " + Sys_user.TableName + " where user_id=?", USER_ID);
			SqlOperator.commitTransaction(db);
			long suNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Sys_user.TableName + " where user_id =?",
					USER_ID
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("sys_user 表此条数据删除后,记录数应该为0", suNum, is(0L));
			//10.删除 Department_info 表测试数据
			SqlOperator.execute(db,
					"delete from " + Department_info.TableName + " where dep_id=?", DEP_ID);
			SqlOperator.commitTransaction(db);
			long depNum = SqlOperator.queryNumber(db,
					"select count(1) from " + Department_info.TableName + " where dep_id =?",
					DEP_ID
			).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("department_info 表此条数据删除后,记录数应该为0", depNum, is(0L));
		}
	}

	@Method(desc = "获取部门的包含文件采集任务的数据源信息的测试方法",
			logicStep = "1.获取登录用户所在部门包含文件采集任务的数据源信息,有返回结果 检查返回结果是否是预期值")
	@Test
	public void getFileDataSource() {
		//1.获取登录用户所在部门包含文件采集任务的数据源信息,有返回结果 检查返回结果是否是预期值
		bodyString = new HttpClient()
				.post(getActionUrl("getFileDataSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		for (int i = 0; i < ar.getDataForResult().getRowCount(); i++) {
			assertThat(ar.getDataForResult().getString(i, "datasource_name"), is("init-hll"));
			assertThat(ar.getDataForResult().getLong(i, "source_id"), is(5000000000L));
		}
	}

	@Method(desc = "根据数据源id获取数据源下所有文件采集任务测试方法",
			logicStep = "正确数据访问:" +
					"1.数据源id存在,有返回结果 检查返回结果是否是预期值" +
					"错误数据访问:" +
					"2-1.数据源id不存在，无返回结果，检查返回结果集为条数为0")
	@Test
	public void getFileCollectionTask() {
		//1-1.数据源id存在,有返回结果 检查返回结果是否是预期值
		bodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("getFileCollectionTask")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		for (int i = 0; i < ar.getDataForResult().getRowCount(); i++) {
			assertThat(ar.getDataForResult().getString(i, "agent_id"), is("5000000002"));
			assertThat(ar.getDataForResult().getString(i, "agent_type"), is("2"));
			assertThat(ar.getDataForResult().getString(i, "agent_ip"), is("127.0.0.1"));
			assertThat(ar.getDataForResult().getString(i, "is_sendok"), is("1"));
			assertThat(ar.getDataForResult().getString(i, "fcs_id"), is("5000000000"));
			assertThat(ar.getDataForResult().getString(i, "user_id"), is("5000"));
			assertThat(ar.getDataForResult().getString(i, "agent_port"), is("8888"));
			assertThat(ar.getDataForResult().getString(i, "source_id"), is("5000000000"));
		}
		//2-1.数据源id不存在，无返回结果，检查返回结果集为条数为0
		bodyString = new HttpClient()
				.addData("sourceId", -5000000000L)
				.post(getActionUrl("getFileCollectionTask")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForResult().getRowCount(), is(0));
	}

	@Method(desc = "根据文件id下载文件的测试方法",
			logicStep = "正确数据访问:" +
					"1.文件id存在,有返回结果 检查返回结果是否是预期值" +
					"错误数据访问:" +
					"2.文件id不存在，无返回结果，检查返回结果集为条数为0")
	@Test
	public void downloadFile() {
		//1.文件id存在,有返回结果 检查返回结果不是空
		bodyString = new HttpClient()
				.addData("fileId", FILE_ID)
				.addData("fileName", "init-hll")
				.addData("queryKeyword", "init-hll")
				.post(getActionUrl("downloadFile")).getBodyString();
		//返回结果是文件内容的byte如何校验 bodyString="999999999999999999999999"
		assertThat(bodyString, is(notNullValue()));
		//2.文件id不存在，无返回结果，检查返回结果集为条数为0
		bodyString = new HttpClient()
				.addData("fileId", "-999999999999999999999999")
				.addData("fileName", "-init-hll")
				.addData("queryKeyword", "-init-hll")
				.post(getActionUrl("downloadFile")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "保存文件收藏方法测试类",
			logicStep = "1.正确数据访问:" +
					"1-1.待收藏的文件id存在 fileId=999999999999999999999999" +
					"2.错误数据访问:" +
					"2-1.待收藏的文件id不存在 fileId=-999999999999999999999999")
	@Test
	public void saveFavoriteFile() {
		//1-1.待收藏的文件id存在
		bodyString = new HttpClient()
				.addData("fileId", FILE_ID + 0)
				.post(getActionUrl("saveFavoriteFile")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//2-1.待收藏的文件id不存在 fileId="-999999999999999999999999"
		bodyString = new HttpClient()
				.addData("fileId", "-" + FILE_ID)
				.post(getActionUrl("saveFavoriteFile")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "取消文件收藏方法测试类",
			logicStep = "正确数据访问：" +
					"1.已经收藏的文件id存在" +
					"错误数据访问：" +
					"2.已经收藏的文件id不存在")
	@Test
	public void cancelFavoriteFile() {
		//1-1.已经收藏的收藏id存在
		bodyString = new HttpClient()
				.addData("favId", FAV_ID)
				.post(getActionUrl("cancelFavoriteFile")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//2-1.已经收藏的收藏id不存在
		bodyString = new HttpClient()
				.addData("favId", "-" + FAV_ID)
				.post(getActionUrl("cancelFavoriteFile")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "文件采集分类统计的测试方法",
			logicStep = "无传入参数,根据测试数据校验结果预期值是否一致" +
					"1.正确数据访问" +
					"1-1.正确数据访问结果" +
					"1-2.正确数据访问结果校验")
	@Test
	public void getFileClassifySum() {
		//1-1.正确数据访问结果
		bodyString = new HttpClient()
				.post(getActionUrl("getFileClassifySum")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//1-2.正确数据访问结果校验
		assertThat(ar.getDataForMap().get(FileType.WenDang.getValue()), is(5));
	}

	@Method(desc = "7天内文件采集统计测试方法，默认统计7天",
			logicStep = "如果查询天数小于1天则显示默认7天，查询条数大于30天则显示30天，否则取传入的查询天数" +
					"1.正确数据访问:" +
					"1-1.int类型值的 queryDays 1-30 之间的整数，取输入的整数" +
					"1-2.int类型值的 queryDays 小于1 的整数，取默认的查询条数7" +
					"1-3.int类型值的 queryDays 大于30 的整数，取最大显示条数30" +
					"2.错误数据访问:" +
					"2-1.非int类型值的 queryDays")
	@Test
	public void getSevenDayCollectFileSum() {
		//1-1.int类型值的 queryDays 1-30 之间的整数，取输入的整数
		bodyString = new HttpClient()
				.addData("queryDays", 5)
				.post(getActionUrl("getSevenDayCollectFileSum")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("20190901"), is(1));
		assertThat(ar.getDataForMap().get("20190902"), is(1));
		assertThat(ar.getDataForMap().get("20190903"), is(1));
		assertThat(ar.getDataForMap().get("20190904"), is(1));
		assertThat(ar.getDataForMap().get("20190905"), is(1));
		//1-2.int类型值的 queryDays 小于1 的整数，取默认的查询条数7
		bodyString = new HttpClient()
				.addData("queryDays", -1)
				.post(getActionUrl("getSevenDayCollectFileSum")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("20190901"), is(1));
		assertThat(ar.getDataForMap().get("20190902"), is(1));
		assertThat(ar.getDataForMap().get("20190903"), is(1));
		assertThat(ar.getDataForMap().get("20190904"), is(1));
		assertThat(ar.getDataForMap().get("20190905"), is(1));
		//1-3.int类型值的 queryDays 大于30 的整数，取最大显示条数30
		bodyString = new HttpClient()
				.addData("queryDays", 31)
				.post(getActionUrl("getSevenDayCollectFileSum")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForMap().get("20190901"), is(1));
		assertThat(ar.getDataForMap().get("20190902"), is(1));
		assertThat(ar.getDataForMap().get("20190903"), is(1));
		assertThat(ar.getDataForMap().get("20190904"), is(1));
		assertThat(ar.getDataForMap().get("20190905"), is(1));
		//2-1.非int类型值的 queryDays
		bodyString = new HttpClient()
				.addData("queryDays", "init-hll")
				.post(getActionUrl("getSevenDayCollectFileSum")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "最近3次内的文件采集统计测试方法，默认统计3次",
			logicStep = "如果查询次数小于1条则显示默认3次，查询次数大于30次则显示最近30次，否则取传入的查询次数" +
					"1.正确数据访问:" +
					"1-1.int类型值的 timesRecently 1-30 之间的整数，取输入的整数" +
					"1-2.int类型值的 timesRecently 小于1 的整数，取默认的查询次数3" +
					"1-3.int类型值的 timesRecently 大于30 的整数，取最大显示次数30" +
					"2.错误数据访问:" +
					"2-1.非int类型值的 timesRecently")
	@Test
	public void getLast3FileCollections() {
		//1-1.int类型值的 timesRecently 1-30 之间的整数，取输入的整数
		bodyString = new HttpClient()
				.addData("timesRecently", 5)
				.post(getActionUrl("getLast3FileCollections")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		for (int i = 0; i < ar.getDataForResult().getRowCount(); i++) {
			assertThat(ar.getDataForResult().getString(i, "collectName"), is("init-hll"));
			assertThat(ar.getDataForResult().getInt(i, "collectSum"), is(1));
		}
		//1-2.int类型值的 timesRecently 小于1 的整数，取默认的查询次数3
		bodyString = new HttpClient()
				.addData("timesRecently", -1)
				.post(getActionUrl("getLast3FileCollections")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		for (int i = 0; i < ar.getDataForResult().getRowCount(); i++) {
			assertThat(ar.getDataForResult().getString(i, "collectName"), is("init-hll"));
			assertThat(ar.getDataForResult().getInt(i, "collectSum"), is(1));
		}
		//1-3.int类型值的 timesRecently 大于30 的整数，取最大显示次数30
		bodyString = new HttpClient()
				.addData("timesRecently", 31)
				.post(getActionUrl("getLast3FileCollections")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		for (int i = 0; i < ar.getDataForResult().getRowCount(); i++) {
			assertThat(ar.getDataForResult().getString(i, "collectName"), is("init-hll"));
			assertThat(ar.getDataForResult().getInt(i, "collectSum"), is(1));
		}
		//2-1.非int类型值的 timesRecently
		bodyString = new HttpClient()
				.addData("timesRecently", "init-hll")
				.post(getActionUrl("getLast3FileCollections")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "根据指定条件查询",
			logicStep = "如果查询次数小于1条则显示默认3次，查询次数大于30次则显示最近30次，否则取传入的查询次数" +
					"1.正确数据访问:" +
					"1-1.无条件查询" +
					"1-2.数据源id查询" +
					"1-3.数据源id和任务id查询" +
					"1-4.数据源id和开始时间查询" +
					"1-5.数据源id和结束时间查询" +
					"1-6.数据源id，任务id和开始时间查询" +
					"1-7.数据源id，任务id和结束时间查询" +
					"1-8.数据源id，任务id，开始时间和结束时间查询" +
					"2.错误的数据访问" +
					"2-1.数据源id不存在" +
					"2-2.数据源id存在,任务id不存在")
	@Test
	public void getConditionalQuery() {
		//1-1.无条件查询
		bodyString = new HttpClient()
				.post(getActionUrl("getConditionalQuery")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//1-2.根据数据源id查询
		bodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.post(getActionUrl("getConditionalQuery")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//1-3.数据源id和任务id查询
		bodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("fcsId", FCS_ID)
				.post(getActionUrl("getConditionalQuery")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//1-4.数据源id和开始时间查询
		bodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("startDate", "20190101")
				.post(getActionUrl("getConditionalQuery")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//1-5.数据源id和结束时间查询
		bodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("endDate", "99991231")
				.post(getActionUrl("getConditionalQuery")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//1-6.数据源id，任务id和开始时间查询
		bodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("fcsId", FCS_ID)
				.addData("startDate", "20190101")
				.post(getActionUrl("getConditionalQuery")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//1-7.数据源id，任务id和开始时间查询
		bodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("fcsId", FCS_ID)
				.addData("endDate", "99991231")
				.post(getActionUrl("getConditionalQuery")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//1-8.数据源id，任务id，开始时间和结束时间查询
		bodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("fcsId", FCS_ID)
				.addData("startDate", "20190101")
				.addData("endDate", "99991231")
				.post(getActionUrl("getConditionalQuery")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//2.错误数据访问
		//2-1.数据源id不存在
		//数据源id不存在
		bodyString = new HttpClient()
				.addData("sourceId", -SOURCE_ID)
				.post(getActionUrl("getConditionalQuery")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//2-2.数据源id存在,任务id不存在
		bodyString = new HttpClient()
				.addData("sourceId", SOURCE_ID)
				.addData("fcsId", -FCS_ID)
				.post(getActionUrl("getConditionalQuery")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
	}


	@Method(desc = "申请信息处理测试类",
			logicStep = "正确数据访问：" +
					"1.已经收藏的文件id存在" +
					"错误数据访问：" +
					"2.已经收藏的文件id不存在")
	@Test
	public void applicationProcessing() {
		//1-1.已经收藏的收藏id存在
		bodyString = new HttpClient()
				.addData("fileId", FILE_ID + "0")
				.addData("applyType", "1")
				.post(getActionUrl("applicationProcessing")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//2-1.已经收藏的收藏id不存在
		bodyString = new HttpClient()
				.addData("fileId", "-" + FILE_ID)
				.addData("applyType", "1")
				.post(getActionUrl("applicationProcessing")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "查看文件测试类",
			logicStep = "正确数据访问：" +
					"1.文件id存在" +
					"错误数据访问：" +
					"2.文件id不存在")
	@Test
	public void viewFile() {
		//1-1.文件id存在
		bodyString = new HttpClient()
				.addData("fileId", FILE_ID + "0")
				.addData("applyType", "1")
				.post(getActionUrl("viewFile")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//2-1.文件id不存在
		bodyString = new HttpClient()
				.addData("fileId", "-" + FILE_ID)
				.addData("applyType", "1")
				.post(getActionUrl("viewFile")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "获取用户申请文件信息测试类",
			logicStep = "正确数据访问：" +
					"1.已存在的申请类型" +
					"错误数据访问：" +
					"2.不存在的申请类型")
	@Test
	public void getApplyData() {
		//1-1.已存在的申请类型
		bodyString = new HttpClient()
				.addData("apply_type", 1)
				.post(getActionUrl("getApplyData")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//2-1.不存在的申请类型
		bodyString = new HttpClient()
				.addData("apply_type", -1)
				.post(getActionUrl("getApplyData")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "取消用户申请文件测试类",
			logicStep = "正确数据访问：" +
					"1.取消用户申请文件id存在" +
					"错误数据访问：" +
					"2.取消用户申请文件id不存在")
	@Test
	public void cancelApply() {
		//1-1.取消用户申请文件id存在
		bodyString = new HttpClient()
				.addData("da_id", DA_ID)
				.post(getActionUrl("cancelApply")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
		//2-1.取消用户申请文件id不存在
		bodyString = new HttpClient()
				.addData("da_id", -DA_ID)
				.post(getActionUrl("cancelApply")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(false));

	}

	@Method(desc = "获取用户申请记录数据",
			logicStep = "正确数据访问：" +
					"1.查询登录用户下所有申请记录")
	@Test
	public void myApplyRecord() {
		//1-1.查询登录用户下所有申请记录
		bodyString = new HttpClient()
				.post(getActionUrl("myApplyRecord")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).get();
		assertThat(ar.isSuccess(), is(true));
	}
}
