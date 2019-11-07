package hrds.b.biz.agent.dbagentconf.tableconf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.b.biz.agent.dbagentconf.BaseInitData;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "CollTbConfStepAction单元测试类", author = "WangZhengcheng")
public class CollTbConfStepActionTest extends WebBaseTestCase{

	private static final long CODE_INFO_TABLE_ID = 7002L;
	private static final long AGENT_INFO_TABLE_ID = 7003L;
	private static final long DATA_SOURCE_TABLE_ID = 7004L;
	private static final long FIRST_DATABASESET_ID = 1001L;
	private static final long SECOND_DATABASESET_ID = 1002L;
	private static final long DEFAULT_TABLE_ID = 999999L;
	private static final JSONObject tableCleanOrder = BaseInitData.initTableCleanOrder();
	private static final JSONObject columnCleanOrder = BaseInitData.initColumnCleanOrder();

	/**
	 * 为每个方法的单元测试初始化测试数据
	 *
	 * 1、构造默认表清洗优先级
	 * 2、构造默认列清洗优先级
	 * 3、构造data_source表测试数据
	 * 4、构造agent_info表测试数据
	 * 5、构造database_set表测试数据
	 * 6、构造Collect_job_classify表测试数据
	 * 7、构建table_info测试数据
	 * 8、构建table_column表测试数据
	 * 9、构造table_storage_info表测试数据
	 * 10、构造table_clean表测试数据
	 * 11、构造column_merge表测试数据
	 * 12、插入数据
	 *
	 * 测试数据：
	 *      1、默认表清洗优先级为(字符补齐，字符替换，列合并，首尾去空)
	 *      2、默认列清洗优先级为(字符补齐，字符替换，日期格式转换，码值转换，列拆分，首尾去空)
	 *      3、data_source表：有1条数据，source_id为1
	 *      4、agent_info表：有2条数据,全部是数据库采集Agent，agent_id分别为7001，7002,source_id为1
	 *      5、database_set表：有2条数据,database_id为1001,1002, agent_id分别为7001,7002，1001的classifyId是10086，1002的classifyId是10010
	 *      1001设置完成并发送成功(is_sendok)
	 *      6、collect_job_classify表：有2条数据，classify_id为10086L、10010L，agent_id分别为7001L、7002L,user_id为9997L
	 *      7、table_info表测试数据共4条，databaseset_id为1001
	 *          7-1、table_id:7001,table_name:sys_user,按照画面配置信息进行采集，并且配置了单表过滤SQL,select * from sys_user where user_id = 2001
	 *          7-2、table_id:7002,table_name:code_info,按照画面配置信息进行采集
	 *          7-3、table_id:7003,table_name:agent_info,按照自定义SQL进行采集
	 *          7-4、table_id:7004,table_name:data_source,按照自定义SQL进行采集
	 *      8、table_column表测试数据：只有在画面上进行配置的采集表才会向table_column表中保存数据
	 *          8-1、column_id为2001-2010，模拟采集了sys_user表的前10个列，列名为user_id，create_id，dep_id，role_id，
	 *               user_name，user_password，user_email，user_mobile，useris_admin，user_type
     *          8-2、column_id为3001-3005，模拟采集了code_info表的所有列，列名为ci_sp_code，ci_sp_class，ci_sp_classname，
	 *               ci_sp_name，ci_sp_remark
	 *      9、table_storage_info表测试数据：
	 *          9-1、storage_id为1234，文件格式为CSV，存储方式为替换，table_id为sys_user表的ID
	 *          9-2、storage_id为5678，文件格式为定长文件，存储方式为追加，table_id为code_info表的ID
	 *      10、table_clean表测试数据：
	 *          10-1、模拟数据对sys_user表进行整表清洗，分别做了列合并和首尾去空
	 *          10-2、模拟数据对code_info表进行了整表清洗，分别做了字符替换字符补齐，将所有列值的abc全部替换为def，将所有列值的前面补上beyond字符串
	 *      11、column_merge表测试数据：对sys_user设置了两个列合并，对code_info表设置了一个列合并
	 *          11-1、模拟数据将sys_user表的user_id和create_id两列合并为user_create_id
	 *          11-2、模拟数据将sys_user表的user_name和user_password两列合并为user_name_password
	 *          11-3、模拟数据将code_info表的ci_sp_classname和ci_sp_name两列合并为ci_sp_classname_name
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Before
	public void before() {
		InitAndDestDataForCollTb.before();
		//模拟登陆
		ActionResult actionResult = BaseInitData.simulatedLogin();
		assertThat("模拟登陆", actionResult.isSuccess(), is(true));
	}

	/**
	 * 测试根据colSetId加载页面初始化数据
	 *
	 * 正确数据访问1：构造正确的colSetId(FIRST_DATABASESET_ID)
	 * 错误的数据访问1：构造错误的colSetId
	 * 错误的测试用例未达到三组:getInitInfo方法只有一个参数
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getInitInfo(){
		//正确数据访问1：构造正确的colSetId(FIRST_DATABASESET_ID)
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getInitInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Result rightData = rightResult.getDataForResult();
		assertThat("根据测试数据，输入正确的colSetId查询到的非自定义采集表信息应该有" + rightData.getRowCount() + "条", rightData.getRowCount(), is(2));

		//错误的数据访问1：构造错误的colSetId
		long wrongColSetId = 99999L;
		String wrongString = new HttpClient()
				.addData("colSetId", wrongColSetId)
				.post(getActionUrl("getInitInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(true));
		Result wrongData = wrongResult.getDataForResult();
		assertThat("根据测试数据，输入错误的colSetId查询到的非自定义采集表信息应该有" + wrongData.getRowCount() + "条", wrongData.getRowCount(), is(0));
	}

	/**
	 * 测试根据数据库设置id得到所有表相关信息功能
	 * TODO 被测方法暂未完成
	 * 正确数据访问1：构造colSetId为1001，inputString为code的测试数据
	 * 正确数据访问2：构造colSetId为1001，inputString为sys的测试数据
	 * 正确数据访问3：构造colSetId为1001，inputString为sys|code的测试数据
	 * 正确数据访问4：构造colSetId为1001，inputString为wzc的测试数据
	 * 错误的数据访问1：构造colSetId为1003的测试数据
	 * 错误的测试用例未达到三组:已经测试用例已经可以覆盖程序中所有的分支
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getTableInfo(){
		//正确数据访问1：构造colSetId为1001，inputString为code的测试数据
		String rightStringOne = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("inputString", "code")
				.post(getActionUrl("getTableInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));
		List<Result> rightDataOne = rightResultOne.getDataForEntityList(Result.class);
		assertThat("使用code做模糊查询得到的表信息有1条", rightDataOne.size(), is(1));
		assertThat("使用code做模糊查询得到的表名为code_info", rightDataOne.get(0).getString(0, "table_name"), is("code_info"));

		//正确数据访问2：构造colSetId为1001，inputString为sys的测试数据
		String rightStringTwo = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("inputString", "sys")
				.post(getActionUrl("getTableInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		List<Result> rightDataTwo = rightResultTwo.getDataForEntityList(Result.class);
		assertThat("使用sys做模糊查询得到的表信息有6条", rightDataTwo.size(), is(6));
		for(Result result : rightDataTwo){
			String tableName = result.getString(0, "table_name");
			if(tableName.equalsIgnoreCase("sys_dump")){
				assertThat("使用sys做模糊查询得到的表名有sys_dump", tableName, is("sys_dump"));
			}else if(tableName.equalsIgnoreCase("sys_exeinfo")){
				assertThat("使用sys做模糊查询得到的表名有sys_exeinfo", tableName, is("sys_exeinfo"));
			}else if(tableName.equalsIgnoreCase("sys_para")){
				assertThat("使用sys做模糊查询得到的表名有sys_para", tableName, is("sys_para"));
			}else if(tableName.equalsIgnoreCase("sys_recover")){
				assertThat("使用sys做模糊查询得到的表名有sys_recover", tableName, is("sys_recover"));
			}else if(tableName.equalsIgnoreCase("sys_role")){
				assertThat("使用sys做模糊查询得到的表名有sys_role", tableName, is("sys_role"));
			}else{
				assertThat("使用sys做模糊查询得到的表名有不符合期望的情况，表名为" + tableName, true, is(false));
			}
		}

		//正确数据访问3：构造colSetId为1001，inputString为sys|code的测试数据
		String rightStringThree = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("inputString", "sys|code")
				.post(getActionUrl("getTableInfo")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));
		List<Result> rightDataThree = rightResultThree.getDataForEntityList(Result.class);
		assertThat("使用sys|code做模糊查询得到的表信息有7条", rightDataThree.size(), is(7));
		for(Result result : rightDataThree){
			String tableName = result.getString(0, "table_name");
			if(tableName.equalsIgnoreCase("sys_dump")){
				assertThat("使用sys|code做模糊查询得到的表名有sys_dump", tableName, is("sys_dump"));
			}else if(tableName.equalsIgnoreCase("sys_exeinfo")){
				assertThat("使用sys|code做模糊查询得到的表名有sys_exeinfo", tableName, is("sys_exeinfo"));
			}else if(tableName.equalsIgnoreCase("sys_para")){
				assertThat("使用sys|code做模糊查询得到的表名有sys_para", tableName, is("sys_para"));
			}else if(tableName.equalsIgnoreCase("sys_recover")){
				assertThat("使用sys|code做模糊查询得到的表名有sys_recover", tableName, is("sys_recover"));
			}else if(tableName.equalsIgnoreCase("sys_role")){
				assertThat("使用sys|code做模糊查询得到的表名有sys_role", tableName, is("sys_role"));
			}else if(tableName.equalsIgnoreCase("code_info")){
				assertThat("使用sys|code做模糊查询得到的表名有code_info", tableName, is("code_info"));
			}else{
				assertThat("使用sys|code做模糊查询得到的表名有不符合期望的情况，表名为" + tableName, true, is(false));
			}
		}

		//正确数据访问4：构造colSetId为1001，inputString为wzc的测试数据
		String rightStringFour = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("inputString", "wzc")
				.post(getActionUrl("getTableInfo")).getBodyString();
		ActionResult rightResultFour = JsonUtil.toObjectSafety(rightStringFour, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultFour.isSuccess(), is(true));
		List<Result> rightDataFour = rightResultFour.getDataForEntityList(Result.class);
		assertThat("使用wzc做模糊查询得到的表信息有7条", rightDataFour.isEmpty(), is(true));

		//错误的数据访问1：构造colSetId为1003的测试数据
		long wrongColSetId = 1003L;
		String wrongColSetIdString = new HttpClient()
				.addData("colSetId", wrongColSetId)
				.addData("inputString", "code")
				.post(getActionUrl("getTableInfo")).getBodyString();
		ActionResult wrongColSetIdResult = JsonUtil.toObjectSafety(wrongColSetIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongColSetIdResult.isSuccess(), is(false));
	}

	/**
	 * 测试根据数据库设置id得到所有表相关信息
	 * 正确数据访问1：构造正确的colSetId进行测试
	 * 错误的数据访问1：构造错误的colSetId进行测试
	 * 错误的测试用例未达到三组:getAllTableInfo方法只有一个参数
	 * @Param: 无
	 * @return: 无
	 * TODO 由于目前测试用的数据库是我们的测试库，所以表的数量不固定，且被测方法未完成
	 * */
	@Test
	public void getAllTableInfo(){
		//正确数据访问1：构造正确的colSetId进行测试
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getAllTableInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		List<Result> rightData = rightResult.getDataForEntityList(Result.class);
		assertThat("截止2019.10.15，IP为47.103.83.1的测试库上有70张表",rightData.size(), is(70));

		//错误的数据访问1：构造错误的colSetId进行测试
		long wrongColSetId = 1003L;
		String wrongString = new HttpClient()
				.addData("colSetId", wrongColSetId)
				.post(getActionUrl("getAllTableInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));
	}

	@Test
	public void getPageSQL(){

	}

	/**
	 * 测试并行采集SQL测试功能
	 * TODO 被测方法未完成
	 * 正确数据访问1：构建正确的colSetId和SQL语句
	 * 错误的数据访问1：构建错误的colSetId和正确的SQL语句
	 * 错误的数据访问2：构建正确的colSetId和错误SQL语句
	 * 错误的测试用例未达到三组:testParallelExtraction只有两个参数
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void testParallelExtraction(){
		//正确数据访问1：构建正确的colSetId和SQL语句
		String pageSQL = "select * from sys_user limit 2 offset 1";
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("sql", pageSQL)
				.post(getActionUrl("testParallelExtraction")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		//错误的数据访问1：构建错误的colSetId和正确的SQL语句
		long wrongColSetId = 1003L;
		String wrongColSetIdString = new HttpClient()
				.addData("colSetId", wrongColSetId)
				.addData("sql", pageSQL)
				.post(getActionUrl("testParallelExtraction")).getBodyString();
		ActionResult wrongColSetIdResult = JsonUtil.toObjectSafety(wrongColSetIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongColSetIdResult.isSuccess(), is(false));

		//错误的数据访问2：构建正确的colSetId和错误SQL语句
		String wrongSQL = "select * from sys_user limit 10,20";
		String wrongSQLString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("sql", wrongSQL)
				.post(getActionUrl("testParallelExtraction")).getBodyString();
		ActionResult wrongSQLResult = JsonUtil.toObjectSafety(wrongSQLString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongSQLResult.isSuccess(), is(false));
	}

	/**
	 * 测试SQL查询设置页面，保存按钮后台方法功能
	 *
	 * 正确数据访问1：构造两条自定义SQL查询设置数据，测试保存功能
	 * 错误的数据访问1：构造两条自定义SQL查询设置数据，第一条数据的表名为空
	 * 错误的数据访问2：构造两条自定义SQL查询设置数据，第二条数据的中文名为空
	 * 错误的数据访问3：构造两条自定义SQL查询设置数据，第一条数据的sql为空
	 * 错误的数据访问4：构造不存在与测试用例模拟数据中的databaseId
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveAllSQL(){
		//正确数据访问1：构造两条自定义SQL查询设置数据，测试保存功能
		List<Table_info> tableInfos = new ArrayList<>();
		for(int i = 1; i <= 2; i++){
			String tableName;
			String tableChName;
			String customizeSQL;
			switch (i) {
				case 1 :
					tableName = "getHalfStructTaskBySourceId";
					tableChName = "通过数据源ID获得半结构化采集任务";
					customizeSQL = "SELECT fcs.odc_id " +
							"FROM "+ Data_source.TableName +" ds " +
							"JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
							"JOIN "+ Object_collect.TableName +" fcs ON ai.agent_id = fcs.agent_id " +
							"WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ds.create_user_id = ?";
					break;
				case 2 :
					tableName = "getFTPTaskBySourceId";
					tableChName = "通过数据源ID获得FTP采集任务";
					customizeSQL = "SELECT fcs.ftp_id " +
							"FROM "+ Data_source.TableName +" ds " +
							"JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
							"JOIN "+ Ftp_collect.TableName +" fcs ON ai.agent_id = fcs.agent_id " +
							"WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ds.create_user_id = ? ";
					break;
				default:
					tableName = "unexpected_tableName";
					tableChName = "unexpected_tableChName";
					customizeSQL = "unexpected_customizeSQL";
			}
			Table_info tableInfo = new Table_info();
			tableInfo.setTable_name(tableName);
			tableInfo.setTable_ch_name(tableChName);
			tableInfo.setSql(customizeSQL);

			tableInfos.add(tableInfo);
		}
		JSONArray array= JSONArray.parseArray(JSON.toJSONString(tableInfos));
		String rightString = new HttpClient()
				.addData("tableInfoArray", array.toJSONString())
				.addData("databaseId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveAllSQL")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		//保存成功，验证数据库中的记录是否符合预期
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Table_info> expectedList = SqlOperator.queryList(db, Table_info.class, "select * from " + Table_info.TableName + " where database_id = ? AND is_user_defined = ?", FIRST_DATABASESET_ID, IsFlag.Shi.getCode());
			assertThat("保存成功后，table_info表中的用户自定义SQL查询数目应该有2条", expectedList.size(), is(2));
			for(Table_info tableInfo : expectedList){
				if(tableInfo.getTable_name().equalsIgnoreCase("getHalfStructTaskBySourceId")){
					assertThat("保存成功后，自定义SQL查询getHalfStructTaskBySourceId的中文名应该是<通过数据源ID获得半结构化采集任务>", tableInfo.getTable_ch_name(), is("通过数据源ID获得半结构化采集任务"));
				}else if(tableInfo.getTable_name().equalsIgnoreCase("getFTPTaskBySourceId")){
					assertThat("保存成功后，自定义SQL查询getFTPTaskBySourceId的中文名应该是<通过数据源ID获得FTP采集任务>", tableInfo.getTable_ch_name(), is("通过数据源ID获得FTP采集任务"));
				}else{
					assertThat("保存出错，出现了不希望出现的数据，表id为" + tableInfo.getTable_id(), true, is(false));
				}
			}
			//验证完毕后，将自己在本方法中构造的数据删除掉
			int firCount = SqlOperator.execute(db, "delete from " + Table_info.TableName + " WHERE table_name = ?", "getHalfStructTaskBySourceId");
			assertThat("测试完成后，table_name为getHalfStructTaskBySourceId的测试数据被删除了", firCount, is(1));
			int secCount = SqlOperator.execute(db, "delete from " + Table_info.TableName + " WHERE table_name = ?", "getFTPTaskBySourceId");
			assertThat("测试完成后，table_name为getFTPTaskBySourceId的测试数据被删除了", secCount, is(1));

			SqlOperator.commitTransaction(db);
		}

		//错误的数据访问1：构造两条自定义SQL查询设置数据，第一条数据的表名为空
		List<Table_info> errorTableInfosOne = new ArrayList<>();
		for(int i = 1; i <= 2; i++){
			String tableName;
			String tableChName;
			String customizeSQL;
			switch (i) {
				case 1 :
					tableName = null;
					tableChName = "通过数据源ID获得半结构化采集任务";
					customizeSQL = "SELECT fcs.odc_id " +
							"FROM "+ Data_source.TableName +" ds " +
							"JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
							"JOIN "+ Object_collect.TableName +" fcs ON ai.agent_id = fcs.agent_id " +
							"WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ds.create_user_id = ?";
					break;
				case 2 :
					tableName = "getFTPTaskBySourceId";
					tableChName = "通过数据源ID获得FTP采集任务";
					customizeSQL = "SELECT fcs.ftp_id " +
							"FROM "+ Data_source.TableName +" ds " +
							"JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
							"JOIN "+ Ftp_collect.TableName +" fcs ON ai.agent_id = fcs.agent_id " +
							"WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ds.create_user_id = ? ";
					break;
				default:
					tableName = "unexpected_tableName";
					tableChName = "unexpected_tableChName";
					customizeSQL = "unexpected_customizeSQL";
			}
			Table_info tableInfo = new Table_info();
			tableInfo.setTable_name(tableName);
			tableInfo.setTable_ch_name(tableChName);
			tableInfo.setSql(customizeSQL);

			errorTableInfosOne.add(tableInfo);
		}
		JSONArray errorArrayOne= JSONArray.parseArray(JSON.toJSONString(errorTableInfosOne));
		String errorStringOne = new HttpClient()
				.addData("tableInfoArray", errorArrayOne.toJSONString())
				.addData("databaseId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveAllSQL")).getBodyString();
		ActionResult errorResultOne = JsonUtil.toObjectSafety(errorStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(errorResultOne.isSuccess(), is(false));

		//错误的数据访问2：构造两条自定义SQL查询设置数据，第二条数据的中文名为空
		List<Table_info> errorTableInfosTwo = new ArrayList<>();
		for(int i = 1; i <= 2; i++){
			String tableName;
			String tableChName;
			String customizeSQL;
			switch (i) {
				case 1 :
					tableName = "getHalfStructTaskBySourceId";
					tableChName = "通过数据源ID获得半结构化采集任务";
					customizeSQL = "SELECT fcs.odc_id " +
							"FROM "+ Data_source.TableName +" ds " +
							"JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
							"JOIN "+ Object_collect.TableName +" fcs ON ai.agent_id = fcs.agent_id " +
							"WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ds.create_user_id = ?";
					break;
				case 2 :
					tableName = "getFTPTaskBySourceId";
					tableChName = null;
					customizeSQL = "SELECT fcs.ftp_id " +
							"FROM "+ Data_source.TableName +" ds " +
							"JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
							"JOIN "+ Ftp_collect.TableName +" fcs ON ai.agent_id = fcs.agent_id " +
							"WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ds.create_user_id = ? ";
					break;
				default:
					tableName = "unexpected_tableName";
					tableChName = "unexpected_tableChName";
					customizeSQL = "unexpected_customizeSQL";
			}
			Table_info tableInfo = new Table_info();
			tableInfo.setTable_name(tableName);
			tableInfo.setTable_ch_name(tableChName);
			tableInfo.setSql(customizeSQL);

			errorTableInfosTwo.add(tableInfo);
		}
		JSONArray errorArrayTwo= JSONArray.parseArray(JSON.toJSONString(errorTableInfosTwo));
		String errorStringTwo = new HttpClient()
				.addData("tableInfoArray", errorArrayTwo.toJSONString())
				.addData("databaseId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveAllSQL")).getBodyString();
		ActionResult errorResultTwo = JsonUtil.toObjectSafety(errorStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(errorResultTwo.isSuccess(), is(false));
		//错误的数据访问3：构造两条自定义SQL查询设置数据，第一条数据的sql为空
		List<Table_info> errortableInfoThree = new ArrayList<>();
		for(int i = 1; i <= 2; i++){
			String tableName;
			String tableChName;
			String customizeSQL;
			switch (i) {
				case 1 :
					tableName = "getHalfStructTaskBySourceId";
					tableChName = "通过数据源ID获得半结构化采集任务";
					customizeSQL = null;
					break;
				case 2 :
					tableName = "getFTPTaskBySourceId";
					tableChName = "通过数据源ID获得FTP采集任务";
					customizeSQL = "SELECT fcs.ftp_id " +
							"FROM "+ Data_source.TableName +" ds " +
							"JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
							"JOIN "+ Ftp_collect.TableName +" fcs ON ai.agent_id = fcs.agent_id " +
							"WHERE ds.source_id = ? AND fcs.is_sendok = ? AND ds.create_user_id = ? ";
					break;
				default:
					tableName = "unexpected_tableName";
					tableChName = "unexpected_tableChName";
					customizeSQL = "unexpected_customizeSQL";
			}
			Table_info tableInfo = new Table_info();
			tableInfo.setTable_name(tableName);
			tableInfo.setTable_ch_name(tableChName);
			tableInfo.setSql(customizeSQL);

			errortableInfoThree.add(tableInfo);
		}
		JSONArray errorArrayThree= JSONArray.parseArray(JSON.toJSONString(errortableInfoThree));
		String errorStringThree = new HttpClient()
				.addData("tableInfoArray", errorArrayThree.toJSONString())
				.addData("databaseId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveAllSQL")).getBodyString();
		ActionResult errorResultThree = JsonUtil.toObjectSafety(errorStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(errorResultThree.isSuccess(), is(false));

		//错误的数据访问4：构造不存在于测试用例模拟数据中的databaseId
		long wrongDatabaseId = 8888888L;
		String errorDatabaseId = new HttpClient()
				.addData("tableInfoArray", array.toJSONString())
				.addData("databaseId", wrongDatabaseId)
				.post(getActionUrl("saveAllSQL")).getBodyString();
		ActionResult errorDatabaseIdResult = JsonUtil.toObjectSafety(errorDatabaseId, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(errorDatabaseIdResult.isSuccess(), is(false));
	}

	/**
	 * 测试SQL查询设置页面操作栏，删除按钮后台方法功能
	 *
	 * 正确数据访问1：模拟删除table_id为7003的自定义SQL采集数据
	 * 错误的数据访问1：模拟删除一个不存在的table_id的自定义SQL采集数据
	 * 错误的测试用例未达到三组: deleteSQLConf()方法只有一个参数
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteSQLConf(){
		//正确数据访问1：模拟删除table_id为7003的自定义SQL采集数据
		//删除前，确认待删除数据是否存在
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long beforeCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " where table_id = ?", AGENT_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("删除前，table_id为" + AGENT_INFO_TABLE_ID + "的数据确实存在", beforeCount, is(1L));
		}
		//构造正确的数据进行删除
		String rightString = new HttpClient()
				.addData("tableId", AGENT_INFO_TABLE_ID)
				.post(getActionUrl("deleteSQLConf")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		//删除后，确认数据是否真的被删除了
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long afterCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " where table_id = ?", AGENT_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("删除后，table_id为" + AGENT_INFO_TABLE_ID + "的数据不存在了", afterCount, is(0L));
		}

		//错误的数据访问1：模拟删除一个不存在的table_id的自定义SQL采集数据
		long errorTableId = 88888L;
		String wrongString = new HttpClient()
				.addData("tableId", errorTableId)
				.post(getActionUrl("deleteSQLConf")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试配置采集表页面，SQL设置按钮后台方法功能，用于回显已经设置的SQL
	 *
	 * 正确数据访问1：构造正确的，且有数据的colSetId(FIRST_DATABASESET_ID)
	 * 正确的数据访问2：构造正确的，但没有数据的colSetId(SECOND_DATABASESET_ID)
	 *
	 * 错误的测试用例未达到三组:getAllSQL()只有一个参数，且只要用户登录，能查到数据就是能查到，查不到就是查不到
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getAllSQLs(){
		//正确数据访问1：构造正确的，且有数据的colSetId(FIRST_DATABASESET_ID)
		String rightStringOne = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getAllSQLs")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));
		List<Table_info> rightDataOne = rightResultOne.getDataForEntityList(Table_info.class);
		assertThat("在ID为" + FIRST_DATABASESET_ID + "的数据库采集任务下，有2条自定义采集SQL", rightDataOne.size(), is(2));
		for(Table_info tableInfo : rightDataOne){
			if(tableInfo.getTable_id() == AGENT_INFO_TABLE_ID){
				assertThat("在table_id为" + AGENT_INFO_TABLE_ID + "的自定义采集SQL中，自定义SQL为", tableInfo.getSql(), is("select * from agent_info"));
			}else if(tableInfo.getTable_id() == DATA_SOURCE_TABLE_ID){
				assertThat("在table_id为" + DATA_SOURCE_TABLE_ID + "的自定义采集SQL中，自定义SQL为", tableInfo.getSql(), is("select * from data_source"));
			}else{
				assertThat("获取到了不期望获取的数据，该条数据的table_name为" + tableInfo.getTable_name(), true, is(false));
			}
		}

		//正确的数据访问2：构造正确的，但没有数据的colSetId(SECOND_DATABASESET_ID)
		String rightStringTwo = new HttpClient()
				.addData("colSetId", SECOND_DATABASESET_ID)
				.post(getActionUrl("getAllSQLs")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		List<Table_info> rightDataTwo = rightResultTwo.getDataForEntityList(Table_info.class);
		assertThat("在ID为" + SECOND_DATABASESET_ID + "的数据库采集任务下，有0条自定义采集SQL", rightDataTwo.size(), is(0));
	}

	/**
	 * 测试配置采集表页面,定义过滤按钮后台方法，用于回显已经对单表定义好的SQL功能
	 *
	 * 正确的数据访问1：模拟回显table_name为sys_user的表定义的对sys_user表的过滤SQL，可以拿到设置的SQL语句select * from sys_user where user_id = 2001
	 * 正确的数据访问2：模拟回显table_name为code_info的过滤SQL，因为测试数据没有设置，所以得到的结果是空字符串
	 * 错误的数据访问1：查询database_id为1002的数据，应该查不到结果，因为在这个数据库采集任务中，没有配置采集表
	 * 错误的测试用例未达到三组:getSingleTableSQL()只有一个参数，且只要用户登录，能查到数据就是能查到，查不到就是查不到
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getSingleTableSQL(){
		//正确的数据访问1：模拟回显table_name为sys_user的表定义的对sys_user表的过滤SQL，可以拿到设置的SQL语句select * from sys_user where user_id = 2001
		String rightTableNameOne = "sys_user";
		String rightStringOne = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("tableName", rightTableNameOne)
				.post(getActionUrl("getSingleTableSQL")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));
		Result rightDataOne = rightResultOne.getDataForResult();
		assertThat("使用database_id为" + FIRST_DATABASESET_ID + "和table_name为" + rightTableNameOne + "得到1条数据", rightDataOne.getRowCount(), is(1));
		assertThat("回显table_name为sys_user表定义的过滤SQL", rightDataOne.getString(0, "sql"), is("select * from sys_user where user_id = 2001"));

		//正确的数据访问2：模拟回显table_name为code_info的过滤SQL，因为测试数据没有设置，所以拿不到
		String rightTableNameTwo = "code_info";
		String rightStringTwo = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("tableName", rightTableNameTwo)
				.post(getActionUrl("getSingleTableSQL")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		Result rightDataTwo = rightResultTwo.getDataForResult();
		assertThat("使用database_id为" + FIRST_DATABASESET_ID + "和table_name为" + rightTableNameTwo + "得到1条数据", rightDataTwo.getRowCount(), is(1));
		assertThat("code_info表没有定义过滤SQL",  rightDataTwo.getString(0, "sql"), is(""));

		//错误的数据访问1：查询database_id为1002的数据，应该查不到结果，因为在这个数据库采集任务中，没有配置采集表
		String wrongString = new HttpClient()
				.addData("colSetId", SECOND_DATABASESET_ID)
				.addData("tableName", rightTableNameTwo)
				.post(getActionUrl("getSingleTableSQL")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(true));
		Result wrongData = wrongResult.getDataForResult();
		assertThat("使用database_id为" + SECOND_DATABASESET_ID + "和table_name为" + rightTableNameTwo + "得到0条数据", wrongData.getRowCount(), is(0));
	}

	/**
	 * 测试配置采集表页面,选择列按钮后台功能
	 * TODO 被测方法未完成
	 * 正确数据访问1：构造tableName为code_info，tableId为7002，colSetId为1001的测试数据
	 * 正确数据访问2：构造tableName为ftp_collect，tableId为999999，colSetId为1001的测试数据
	 * 错误的数据访问1：构造tableName为ftp_collect，tableId为999999，colSetId为1003的测试数据
	 * 错误的数据访问2：构造tableName为wzc_collect，tableId为999999，colSetId为1001的测试数据
	 * 错误的测试用例未达到三组:以上所有测试用例已经可以覆盖处理逻辑中所有的分支和错误处理了
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getColumnInfo(){
		//正确数据访问1：构造tableName为code_info，tableId为7002，colSetId为1001的测试数据
		String tableNameOne = "code_info";
		String rightStringOne = new HttpClient()
				.addData("tableName", tableNameOne)
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("getColumnInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));
		Map<Object, Object> rightDataOne = rightResultOne.getDataForMap();
		for(Map.Entry<Object, Object> entry : rightDataOne.entrySet()){
			String key = (String) entry.getKey();
			if(key.equalsIgnoreCase("tableName")){
				assertThat("返回的结果中，有一对Entry的key为tableName", key, is("tableName"));
				assertThat("返回的结果中，key为tableName的Entry，value为code_info", entry.getValue(), is(tableNameOne));
			}else if(key.equalsIgnoreCase("columnInfo")){
				assertThat("返回的结果中，有一对Entry的key为columnInfo", key, is("columnInfo"));
				List<Table_column> tableColumns = (List<Table_column>) entry.getValue();
				assertThat("返回的结果中，key为columnInfo的Entry，value为List<Table_column>,code_info表中有5列", tableColumns.size(), is(5));
			}else{
				assertThat("返回的结果中，出现了不期望出现的内容", true, is(false));
			}
		}

		//正确数据访问2：构造tableName为ftp_collect，tableId为999999，colSetId为1001的测试数据
		String tableNameTwo = "ftp_collect";
		String rightStringTwo = new HttpClient()
				.addData("tableName", tableNameTwo)
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("tableId", DEFAULT_TABLE_ID)
				.post(getActionUrl("getColumnInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		Map<Object, Object> rightDataTwo = rightResultTwo.getDataForMap();
		for(Map.Entry<Object, Object> entry : rightDataTwo.entrySet()){
			String key = (String) entry.getKey();
			if(key.equalsIgnoreCase("tableName")){
				assertThat("返回的结果中，有一对Entry的key为tableName", key, is("tableName"));
				assertThat("返回的结果中，key为tableName的Entry，value为ftp_collect", entry.getValue(), is(tableNameTwo));
			}else if(key.equalsIgnoreCase("columnInfo")){
				assertThat("返回的结果中，有一对Entry的key为columnInfo", key, is("columnInfo"));
				List<Table_column> tableColumns = (List<Table_column>) entry.getValue();
				assertThat("返回的结果中，key为columnInfo的Entry，value为List<Table_column>,ftp_collect表中有22列", tableColumns.size(), is(22));
			}else{
				assertThat("返回的结果中，出现了不期望出现的内容", true, is(false));
			}
		}

		//错误的数据访问1：构造tableName为ftp_collect，tableId为999999，colSetId为1003的测试数据
		long wrongColSetId = 1003L;
		String wrongColSetIdString = new HttpClient()
				.addData("tableName", tableNameTwo)
				.addData("colSetId", wrongColSetId)
				.addData("tableId", DEFAULT_TABLE_ID)
				.post(getActionUrl("getColumnInfo")).getBodyString();
		ActionResult wrongColSetIdResult = JsonUtil.toObjectSafety(wrongColSetIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongColSetIdResult.isSuccess(), is(false));

		//错误的数据访问2：构造tableName为wzc_collect，tableId为999999，colSetId为1001的测试数据
		String notExistTableName = "wzc_collect";
		String wrongTableNameString = new HttpClient()
				.addData("tableName", notExistTableName)
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("tableId", DEFAULT_TABLE_ID)
				.post(getActionUrl("getColumnInfo")).getBodyString();
		ActionResult wrongTableNameResult = JsonUtil.toObjectSafety(wrongTableNameString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongTableNameResult.isSuccess(), is(true));
		Map<Object, Object> wrongTableNameData = wrongTableNameResult.getDataForMap();
		for(Map.Entry<Object, Object> entry : wrongTableNameData.entrySet()){
			String key = (String) entry.getKey();
			if(key.equalsIgnoreCase("tableName")){
				assertThat("返回的结果中，有一对Entry的key为tableName", key, is("tableName"));
				assertThat("返回的结果中，key为tableName的Entry，value为ftp_collect", entry.getValue(), is(tableNameTwo));
			}else if(key.equalsIgnoreCase("columnInfo")){
				assertThat("返回的结果中，有一对Entry的key为columnInfo", key, is("columnInfo"));
				List<Table_column> tableColumns = (List<Table_column>) entry.getValue();
				assertThat("返回的结果中，key为columnInfo的Entry，value为List<Table_column>,没有wzc_collect这张表", tableColumns.isEmpty(), is(true));
			}else{
				assertThat("返回的结果中，出现了不期望出现的内容", true, is(false));
			}
		}
	}

	/**
	 * 测试保存单个表的采集信息功能
	 * TODO 被测方法未完成
	 * TODO 后面table_info表中加上是否并行抽取和分页SQL字段后，这个测试用例还要继续优化，在构造HTTP请求的时候加上这两个字段作为参数
	 * 正确数据访问1：在database_id为7001的数据库采集任务下构造新增采集ftp_collect表的数据，不选择采集列和列排序
	 * 正确数据访问2：在database_id为7001的数据库采集任务下构造新增采集object_collect表的数据，选择采集列和列排序
	 * 正确数据访问3：在database_id为7001的数据库采集任务下构造修改采集code_info表的数据，选择采集列和列排序
	 * 错误的数据访问1：构造缺少表名的采集数据
	 * 错误的数据访问2：构造缺少表中文名的采集数据
	 * 错误的数据访问3：构造在不存在的数据库采集任务中保存采集ftp_collect表数据
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveCollSingleTbInfo(){
		//正确数据访问1：在database_id为7001的数据库采集任务下构造新增采集ftp_collect表的数据，不选择采集列和列排序，这样就会按照默认的顺序采集所有的列
		String tableNameOne = "ftp_collect";
		String tableChNameOne = "FTP采集任务表";
		//TODO 设置是否并行抽取
		//TODO 如果并行抽取，设置抽取SQL

		//新增逻辑的第一个保存信息是将画面配置信息，保存进入table_info表
		//模拟HTTP访问
		String rightStringOne = new HttpClient()
				.addData("table_name", tableNameOne)
				.addData("table_ch_name", tableChNameOne)
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveCollSingleTbInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));
		Long returnValueOne = (Long) rightResultOne.getData();
		assertThat(returnValueOne == FIRST_DATABASESET_ID, is(true));

		//断言table_info表中出现了这样一条数据
		Table_info tableInfo;
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			tableInfo = SqlOperator.queryOneObject(db, Table_info.class, "select * from " + Table_info.class + "where table_name = ?", tableNameOne).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("模拟保存新增ftp_collect表，新增成功后，得到的表中文名为<FTP采集任务表>", tableInfo.getTable_ch_name(), is(tableChNameOne));
			assertThat("模拟保存新增ftp_collect表，新增成功后，得到的有效结束日期为<99991231>", tableInfo.getValid_e_date(), is(Constant.MAXDATE));
			assertThat("模拟保存新增ftp_collect表，新增成功后，得到的是否自定义sql采集为<否>", IsFlag.ofEnumByCode(tableInfo.getIs_user_defined()), is(IsFlag.Fou));
			assertThat("模拟保存新增ftp_collect表，新增成功后，得到的是否仅登记为<是>", IsFlag.ofEnumByCode(tableInfo.getIs_register()), is(IsFlag.Shi));
			assertThat("模拟保存新增ftp_collect表，新增成功后，得到的表清洗顺序符合预期", tableInfo.getTi_or(), is(tableCleanOrder.toJSONString()));
		}
		//第二个保存信息是将画面配置信息保存进入table_column表，得到table_info表中刚刚保存的那条数据的table_id，table_column表中出现了外键为table_id的数据，并且条数一致
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Table_column> tableColumns = SqlOperator.queryList(db, Table_column.class, "select * from " + Table_column.TableName + "where table_id = ?", tableInfo.getTable_id());
			assertThat("模拟保存新增ftp_collect表，新增成功后，由于采集的是所有字段，所以得到的查询结果集有22条数据", tableColumns.size(), is(22));
			for(Table_column tableColumn : tableColumns){
				if(tableColumn.getColume_name().equalsIgnoreCase("ftp_id")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_id字段", tableColumn.getColume_name().equalsIgnoreCase("ftp_id"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_id字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("ftp_number")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_number字段", tableColumn.getColume_name().equalsIgnoreCase("ftp_number"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_number字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("ftp_name")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_name字段", tableColumn.getColume_name().equalsIgnoreCase("ftp_name"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_name字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("start_date")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集start_date字段", tableColumn.getColume_name().equalsIgnoreCase("start_date"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集start_date字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("end_date")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集end_date字段", tableColumn.getColume_name().equalsIgnoreCase("end_date"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集end_date字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("ftp_ip")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_ip字段", tableColumn.getColume_name().equalsIgnoreCase("ftp_ip"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_ip字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("ftp_port")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_port字段", tableColumn.getColume_name().equalsIgnoreCase("ftp_port"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_port字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("ftp_username")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_username字段", tableColumn.getColume_name().equalsIgnoreCase("ftp_username"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_username字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("ftp_password")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_password字段", tableColumn.getColume_name().equalsIgnoreCase("ftp_password"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_password字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("ftp_dir")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_dir字段", tableColumn.getColume_name().equalsIgnoreCase("ftp_dir"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_dir字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("local_path")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集local_path字段", tableColumn.getColume_name().equalsIgnoreCase("local_path"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集local_path字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("ftp_rule_path")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_rule_path字段", tableColumn.getColume_name().equalsIgnoreCase("ftp_rule_path"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_rule_path字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("child_file_path")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集child_file_path字段", tableColumn.getColume_name().equalsIgnoreCase("child_file_path"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集child_file_path字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("child_time")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集child_time字段", tableColumn.getColume_name().equalsIgnoreCase("child_time"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集child_time字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("file_suffix")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集file_suffix字段", tableColumn.getColume_name().equalsIgnoreCase("file_suffix"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集file_suffix字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("ftp_model")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_model字段", tableColumn.getColume_name().equalsIgnoreCase("ftp_model"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集ftp_model字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("run_way")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集run_way字段", tableColumn.getColume_name().equalsIgnoreCase("run_way"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集run_way字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("remark")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集remark字段", tableColumn.getColume_name().equalsIgnoreCase("remark"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集remark字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("is_sendok")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集is_sendok字段", tableColumn.getColume_name().equalsIgnoreCase("is_sendok"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集is_sendok字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("is_unzip")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集is_unzip字段", tableColumn.getColume_name().equalsIgnoreCase("is_unzip"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集is_unzip字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("reduce_type")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集reduce_type字段", tableColumn.getColume_name().equalsIgnoreCase("reduce_type"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集reduce_type字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("agent_id")){
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集agent_id字段", tableColumn.getColume_name().equalsIgnoreCase("agent_id"), is(true));
					assertThat("模拟保存新增ftp_collect表，新增成功后，成功配置采集agent_id字段，该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else{
					assertThat("模拟保存新增ftp_collect表，新增成功后，发现采集了期望以外的字段，字段名为：" + tableColumn.getColume_name(), true, is(false));
				}
			}
		}

		//正确数据访问2：在database_id为7001的数据库采集任务下构造新增采集object_collect表的数据，选择采集列和列排序
		String tableNameTwo = "object_collect";
		String tableChNameTwo = "半结构化采集任务表";
		//object_collect一共有16列，只采集前5列
		List<Table_column> tableColumns = new ArrayList<>();
		for(int i = 1; i <= 5; i++){
			String columnName;
			String columnType;
			String columnChName;
			String pkFlag;
			switch (i){
				case 1 :
					columnName = "odc_id";
					columnType = "BIGINT";
					columnChName = "对象采集id";
					pkFlag = IsFlag.Shi.getCode();
					break;
				case 2 :
					columnName = "object_collect_type";
					columnType = "CHAR(1)";
					columnChName = "对象采集方式";
					pkFlag = IsFlag.Fou.getCode();
					break;
				case 3 :
					columnName = "obj_number";
					columnType = "VARCHAR(200)";
					columnChName = "对象采集设置编号";
					pkFlag = IsFlag.Fou.getCode();
					break;
				case 4 :
					columnName = "obj_collect_name";
					columnType = "VARCHAR(512)";
					columnChName = "对象采集任务名称";
					pkFlag = IsFlag.Fou.getCode();
					break;
				case 5 :
					columnName = "system_name";
					columnType = "VARCHAR(512)";
					columnChName = "操作系统类型";
					pkFlag = IsFlag.Fou.getCode();
					break;
				default:
					columnName = "unexpected_columnName";
					columnType = "unexpected_columnType";
					columnChName = "unexpected_columnChName";
					pkFlag = "unexpected_pkFlag";
			}
			Table_column tableColumn = new Table_column();
			tableColumn.setColume_name(columnName);
			tableColumn.setColume_ch_name(columnChName);
			tableColumn.setColumn_type(columnType);
			tableColumn.setIs_primary_key(pkFlag);

			tableColumns.add(tableColumn);
		}
		//构造采集顺序
		JSONObject columnSort = new JSONObject();
		columnSort.put("odc_id", 1);
		columnSort.put("object_collect_type", 2);
		columnSort.put("obj_number", 3);
		columnSort.put("obj_collect_name", 4);
		columnSort.put("system_name", 5);

		String rightStringTwo = new HttpClient()
				.addData("table_name", tableNameTwo)
				.addData("table_ch_name", tableChNameTwo)
				.addData("collColumn", JSON.toJSONString(tableColumns))
				.addData("columnSort", columnSort.toJSONString())
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveCollSingleTbInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		Long returnValueTwo = (Long) rightResultTwo.getData();
		assertThat(returnValueTwo == FIRST_DATABASESET_ID, is(true));

		Table_info tableInfoTwo;
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			tableInfoTwo = SqlOperator.queryOneObject(db, Table_info.class, "select * from " + Table_info.class + "where table_name = ?", tableNameTwo).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("模拟保存新增object_collect表，新增成功后，得到的表中文名为<半结构化采集任务表>", tableInfoTwo.getTable_ch_name(), is(tableNameTwo));
			assertThat("模拟保存新增object_collect表，新增成功后，得到的有效结束日期为<99991231>", tableInfoTwo.getValid_e_date(), is(Constant.MAXDATE));
			assertThat("模拟保存新增object_collect表，新增成功后，得到的是否自定义sql采集为<否>", IsFlag.ofEnumByCode(tableInfoTwo.getIs_user_defined()), is(IsFlag.Fou));
			assertThat("模拟保存新增object_collect表，新增成功后，得到的是否仅登记为<是>", IsFlag.ofEnumByCode(tableInfoTwo.getIs_register()), is(IsFlag.Shi));
			assertThat("模拟保存新增object_collect表，新增成功后，得到的表清洗顺序符合预期", tableInfoTwo.getTi_or(), is(tableCleanOrder.toJSONString()));
		}

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Table_column> tableColumnsTwo = SqlOperator.queryList(db, Table_column.class, "select * from " + Table_column.TableName + "where table_id = ?", tableInfoTwo.getTable_id());
			assertThat("模拟保存新增object_collect表，新增成功后，由于只采集了5个字段，所以得到的查询结果集有5条数据", tableColumns.size(), is(5));
			for(Table_column tableColumn : tableColumnsTwo){
				if(tableColumn.getColume_name().equalsIgnoreCase("odc_id")){
					assertThat("模拟保存新增object_collect表，新增成功后，成功配置采集odc_id字段", tableColumn.getColume_name().equalsIgnoreCase("odc_id"), is(true));
					assertThat("模拟保存新增object_collect表，新增成功后，成功配置采集odc_id字段,该字段的采集顺序为1", tableColumn.getRemark(), is("1"));
					assertThat("模拟保存新增object_collect表，新增成功后，成功配置采集odc_id字段,该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("object_collect_type")){
					assertThat("模拟保存新增object_collect表，新增成功后，成功配置采集object_collect_type字段", tableColumn.getColume_name().equalsIgnoreCase("object_collect_type"), is(true));
					assertThat("模拟保存新增object_collect表，新增成功后，成功配置采集object_collect_type字段,该字段的采集顺序为2", tableColumn.getRemark(), is("2"));
					assertThat("模拟保存新增object_collect表，新增成功后，成功配置采集object_collect_type字段,该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("obj_number")){
					assertThat("模拟保存新增object_collect表，新增成功后，成功配置采集obj_number字段", tableColumn.getColume_name().equalsIgnoreCase("obj_number"), is(true));
					assertThat("模拟保存新增object_collect表，新增成功后，成功配置采集obj_number字段,该字段的采集顺序为3", tableColumn.getRemark(), is("3"));
					assertThat("模拟保存新增object_collect表，新增成功后，成功配置采集obj_number字段,该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("obj_collect_name")){
					assertThat("模拟保存新增object_collect表，新增成功后，成功配置采集obj_collect_name字段", tableColumn.getColume_name().equalsIgnoreCase("obj_collect_name"), is(true));
					assertThat("模拟保存新增object_collect表，新增成功后，成功配置采集obj_collect_name字段,该字段的采集顺序为4", tableColumn.getRemark(), is("4"));
					assertThat("模拟保存新增object_collect表，新增成功后，成功配置采集obj_collect_name字段,该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("system_name")){
					assertThat("模拟保存新增object_collect表，新增成功后，成功配置采集obj_collect_name字段", tableColumn.getColume_name().equalsIgnoreCase("system_name"), is(true));
					assertThat("模拟保存新增object_collect表，新增成功后，成功配置采集obj_collect_name字段,该字段的采集顺序为5", tableColumn.getRemark(), is("5"));
					assertThat("模拟保存新增object_collect表，新增成功后，成功配置采集obj_collect_name字段,该字段的清洗规则符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else{
					assertThat("模拟保存新增object_collect表，新增成功后，发现采集了期望以外的字段，字段名为：" + tableColumn.getColume_name(), true, is(false));
				}
			}
		}

		//正确数据访问3：在database_id为7001的数据库采集任务下构造修改采集code_info表的数据，选择采集列和列排序，原来构造的模式数据是模拟采集code_info表下面所有的字段，现在只采集前2个字段
		List<Table_column> codeInfos = new ArrayList<>();
		for(int i = 1; i<= 2; i++){
			String columnName;
			String columnType;
			String columnChName;
			String pkFlag;
			switch (i){
				case 1 :
					columnName = "ci_sp_code";
					columnType = "VARCHAR(20)";
					columnChName = "代码值";
					pkFlag = IsFlag.Shi.getCode();
					break;
				case 2 :
					columnName = "ci_sp_class";
					columnType = "VARCHAR(20)";
					columnChName = "所属类别号";
					pkFlag = IsFlag.Shi.getCode();
					break;
				default:
					columnName = "unexpected_columnName";
					columnType = "unexpected_columnType";
					columnChName = "unexpected_columnChName";
					pkFlag = "unexpected_pkFlag";
			}
			Table_column tableColumn = new Table_column();
			tableColumn.setColume_name(columnName);
			tableColumn.setColume_ch_name(columnChName);
			tableColumn.setColumn_type(columnType);
			tableColumn.setIs_primary_key(pkFlag);

			codeInfos.add(tableColumn);
		}

		JSONObject codeInfoSort = new JSONObject();
		codeInfoSort.put("ci_sp_code", 1);
		codeInfoSort.put("ci_sp_class", 2);

		String rightStringThree = new HttpClient()
				.addData("table_id", CODE_INFO_TABLE_ID)
				.addData("table_name", "code_info")
				.addData("table_ch_name", "代码信息表")
				.addData("collColumn", JSON.toJSONString(codeInfos))
				.addData("columnSort", codeInfoSort.toJSONString())
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveCollSingleTbInfo")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));
		Long returnValueThree = (Long) rightResultThree.getData();
		assertThat(returnValueThree == FIRST_DATABASESET_ID, is(true));

		Table_info tableInfoThree;
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			tableInfoThree = SqlOperator.queryOneObject(db, Table_info.class, "select * from " + Table_info.class + "where table_name = ?", "code_info").orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("模拟保存修改code_info表，修改成功后，得到的表中文名为<代码信息表>", tableInfoThree.getTable_ch_name(), is("code_info"));
			assertThat("模拟保存修改code_info表，修改成功后，得到的有效结束日期为<99991231>", tableInfoThree.getValid_e_date(), is(Constant.MAXDATE));
			assertThat("模拟保存修改code_info表，修改成功后，得到的是否自定义sql采集为<否>", IsFlag.ofEnumByCode(tableInfoThree.getIs_user_defined()), is(IsFlag.Fou));
			assertThat("模拟保存修改code_info表，修改成功后，得到的是否仅登记为<是>", IsFlag.ofEnumByCode(tableInfoThree.getIs_register()), is(IsFlag.Shi));
			assertThat("模拟保存修改code_info表，修改成功后，得到的表清洗顺序符合预期", tableInfoThree.getTi_or(), is(tableCleanOrder.toJSONString()));
		}

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Table_column> tableColumnsThree = SqlOperator.queryList(db, Table_column.class, "select * from " + Table_column.TableName + "where table_id = ?", tableInfoThree.getTable_id());
			assertThat("模拟保存修改code_info表，修改成功后，由于只采集了2个字段，所以得到的查询结果集有2条数据", tableColumns.size(), is(2));
			for(Table_column tableColumn : tableColumnsThree){
				if(tableColumn.getColume_name().equalsIgnoreCase("ci_sp_code")){
					assertThat("模拟保存修改code_info表，修改成功后，成功配置采集ci_sp_code字段", tableColumn.getColume_name().equalsIgnoreCase("ci_sp_code"), is(true));
					assertThat("模拟保存修改code_info表，修改成功后，成功配置采集ci_sp_code字段,该字段的采集顺序为1", tableColumn.getRemark(), is("1"));
					assertThat("模拟保存修改code_info表，修改成功后，成功配置采集ci_sp_code字段,该字段的清洗顺序符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("ci_sp_class")){
					assertThat("模拟保存修改code_info表，修改成功后，成功配置采集ci_sp_class字段", tableColumn.getColume_name().equalsIgnoreCase("ci_sp_class"), is(true));
					assertThat("模拟保存修改code_info表，修改成功后，成功配置采集ci_sp_class字段,该字段的采集顺序为2", tableColumn.getRemark(), is("2"));
					assertThat("模拟保存修改code_info表，修改成功后，成功配置采集ci_sp_class字段,该字段的采集顺序符合预期", tableColumn.getTc_or(), is(columnCleanOrder.toJSONString()));
				}else{
					assertThat("模拟保存修改code_info表，修改成功后，发现采集了期望以外的字段，字段名为：" + tableColumn.getColume_name(), true, is(false));
				}
			}
		}

		//验证table_storage_info表、table_clean表、column_merge表数据在修改逻辑执行后结果是否符合期望
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//使用修改之前的table_id分别查询三张表，应该查不到结果
			long storageInfoCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_storage_info.TableName + "where table_id = ?", CODE_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("结果必须有且只有一条"));
			long tableCountCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + "where table_id = ?", CODE_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("结果必须有且只有一条"));
			long columnMergeCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_merge.TableName + "where table_id = ?", CODE_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("结果必须有且只有一条"));

			assertThat("模拟保存修改code_info表，修改成功后，table_storage_info表的table_id字段被更新了，旧的table_id字段没有了", storageInfoCount == 0, is(true));
			assertThat("模拟保存修改code_info表，修改成功后，table_clean表的table_id字段被更新了，旧的table_id字段没有了", tableCountCount == 0, is(true));
			assertThat("模拟保存修改code_info表，修改成功后，column_merge表的table_id字段被更新了，旧的table_id字段没有了", columnMergeCount == 0, is(true));
			//使用修改之后的table_id分别查询三张表，应该有结果
			long storageInfoCountAfter = SqlOperator.queryNumber(db, "select count(1) from " + Table_storage_info.TableName + "where table_id = ?", tableInfoThree.getTable_id()).orElseThrow(() -> new BusinessException("结果必须有且只有一条"));
			long tableCountCountAfter = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + "where table_id = ?", tableInfoThree.getTable_id()).orElseThrow(() -> new BusinessException("结果必须有且只有一条"));
			long columnMergeCountAfter = SqlOperator.queryNumber(db, "select count(1) from " + Column_merge.TableName + "where table_id = ?", tableInfoThree.getTable_id()).orElseThrow(() -> new BusinessException("结果必须有且只有一条"));

			assertThat("模拟保存修改code_info表，修改成功后，table_storage_info表的table_id字段被更新了", storageInfoCountAfter == 1, is(true));
			assertThat("模拟保存修改code_info表，修改成功后，table_clean表的table_id字段被更新了", tableCountCountAfter == 2, is(true));
			assertThat("模拟保存修改code_info表，修改成功后，column_merge表的table_id字段被更新了", columnMergeCountAfter == 1, is(true));
		}

		//错误的数据访问1：构造缺少表名的采集数据
		String wrongStringOne = new HttpClient()
				.addData("table_id", CODE_INFO_TABLE_ID)
				.addData("table_ch_name", "代码信息表")
				.addData("collColumn", JSON.toJSONString(codeInfos))
				.addData("columnSort", codeInfoSort.toJSONString())
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveCollSingleTbInfo")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));

		//错误的数据访问2：构造缺少表中文名的采集数据
		String wrongStringTwo = new HttpClient()
				.addData("table_id", CODE_INFO_TABLE_ID)
				.addData("table_name", "code_info")
				.addData("collColumn", JSON.toJSONString(codeInfos))
				.addData("columnSort", codeInfoSort.toJSONString())
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveCollSingleTbInfo")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));

		//错误的数据访问3：构造在不存在的数据库采集任务中保存采集ftp_collect表数据
		long wrongColSetId = 1003L;
		String wrongStringThree = new HttpClient()
				.addData("table_id", CODE_INFO_TABLE_ID)
				.addData("table_name", "ftp_collect")
				.addData("table_ch_name", "FTP采集任务表")
				.addData("collColumn", JSON.toJSONString(codeInfos))
				.addData("columnSort", codeInfoSort.toJSONString())
				.addData("colSetId", wrongColSetId)
				.post(getActionUrl("saveCollSingleTbInfo")).getBodyString();
		ActionResult wrongResultThree = JsonUtil.toObjectSafety(wrongStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultThree.isSuccess(), is(false));

		//删除因为测试而添加的数据
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			SqlOperator.execute(db, "delete from " + Table_info.TableName + "where table_name = ?", "ftp_collect");
			SqlOperator.execute(db, "delete from " + Table_column.TableName + "where table_id = ?", tableInfo.getTable_id());
			SqlOperator.execute(db, "delete from " + Table_info.TableName + "where table_name = ?", "object_collect");
			SqlOperator.execute(db, "delete from " + Table_column.TableName + "where table_id = ?", tableInfoTwo.getTable_id());
			SqlOperator.execute(db, "delete from " + Table_info.TableName + "where table_id = ?", tableInfoThree.getTable_id());
			SqlOperator.execute(db, "delete from " + Table_column.TableName + "where table_id = ?", tableInfoThree.getTable_id());
			SqlOperator.execute(db, "delete from " + Table_storage_info.TableName + "where table_id = ?", tableInfoThree.getTable_id());
			SqlOperator.execute(db, "delete from " + Table_clean.TableName + "where table_id = ?", tableInfoThree.getTable_id());
			SqlOperator.execute(db, "delete from " + Column_merge.TableName + "where table_id = ?", tableInfoThree.getTable_id());

			SqlOperator.commitTransaction(db);
		}

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			long ftpAfterDeleteCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + "where table_name = ?", "ftp_collect").orElseThrow(() -> new BusinessException("结果必须有且只有一条数据"));
			long ftpColumnAfterDeleteCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + "where table_id = ?", tableInfo.getTable_id()).orElseThrow(() -> new BusinessException("结果必须有且只有一条数据"));
			assertThat("正确的数据访问1测试成功后，删除插入的数据成功", ftpAfterDeleteCount == 0 && ftpColumnAfterDeleteCount ==0, is(true));

			long objectAfterDeleteCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + "where table_name = ?", "object_collect").orElseThrow(() -> new BusinessException("结果必须有且只有一条数据"));
			long objectColumnAfterDeleteCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + "where table_id = ?", tableInfoTwo.getTable_id()).orElseThrow(() -> new BusinessException("结果必须有且只有一条数据"));
			assertThat("正确的数据访问2测试成功后，删除插入的数据成功", objectAfterDeleteCount == 0 && objectColumnAfterDeleteCount ==0, is(true));

			long codeInfoAfterDeleteCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + "where table_id = ?", tableInfoThree.getTable_id()).orElseThrow(() -> new BusinessException("结果必须有且只有一条数据"));
			long codeInfoColumnAfterDeleteCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + "where table_id = ?", tableInfoThree.getTable_id()).orElseThrow(() -> new BusinessException("结果必须有且只有一条数据"));
			long storageAfterDeleteCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_storage_info.TableName + "where table_id = ?", tableInfoThree.getTable_id()).orElseThrow(() -> new BusinessException("结果必须有且只有一条数据"));
			long cleanAfterDeleteCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + "where table_id = ?", tableInfoThree.getTable_id()).orElseThrow(() -> new BusinessException("结果必须有且只有一条数据"));
			long columnMergeAfterDeleteCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_merge.TableName + "where table_id = ?", tableInfoThree.getTable_id()).orElseThrow(() -> new BusinessException("结果必须有且只有一条数据"));

			assertThat("正确的数据访问2测试成功后，删除table_info表插入的数据成功", codeInfoAfterDeleteCount == 0, is(true));
			assertThat("正确的数据访问2测试成功后，删除table_column表插入的数据成功", codeInfoColumnAfterDeleteCount == 0, is(true));
			assertThat("正确的数据访问2测试成功后，删除table_storage_info表被修改table_id后的数据成功", storageAfterDeleteCount == 0, is(true));
			assertThat("正确的数据访问2测试成功后，删除table_clean表被修改table_id后的数据成功", cleanAfterDeleteCount == 0, is(true));
			assertThat("正确的数据访问2测试成功后，删除column_merger表被修改table_id后的数据成功", columnMergeAfterDeleteCount == 0, is(true));
		}
	}

	/**
	 * 测试如果页面只有自定义SQL查询采集，保存该界面配置的所有信息功能
	 *
	 * 正确的数据访问1：模拟这样一种情况，在构造的测试数据的基础上，不采集页面上配置的两张表，然后点击下一步，也就是说现在datavase_id为FIRST_DATABASESET_ID的数据库采集任务全部是采集自定义SQL
	 * 错误的测试用例未达到三组:因为自定义表已经入库了，所以要在table_info表中删除不是自定义SQL的表信息，删除的条数可能为0-N，不关注是否删除了数据和删除的数目
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveCustomizeCollTbInfo(){
		//正确的数据访问1：模拟这样一种情况，在构造的测试数据的基础上，不采集页面上配置的两张表，然后点击下一步，也就是说现在datavase_id为FIRST_DATABASESET_ID的数据库采集任务全部是采集自定义SQL
		//删除前，确认待删除数据是否存在
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long beforeCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " WHERE database_id = ? AND valid_e_date = ? AND is_user_defined = ?", FIRST_DATABASESET_ID, Constant.MAXDATE, IsFlag.Fou.getCode()).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("方法调用前，table_info表中的非用户自定义采集有2条", beforeCount, is(2L));
		}

		//构造正确的数据访问
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveCustomizeCollTbInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Integer returnValue = (Integer) rightResult.getData();
		assertThat(returnValue == FIRST_DATABASESET_ID, is(true));

		//删除后，确认数据是否真的被删除了
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long afterCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " WHERE database_id = ? AND valid_e_date = ? AND is_user_defined = ?", FIRST_DATABASESET_ID, Constant.MAXDATE, IsFlag.Fou.getCode()).orElseThrow(() -> new BusinessException("必须有且只有一条数据"));
			assertThat("方法调用后，table_info表中的非用户自定义采集有0条", afterCount, is(0L));
		}
	}

	/**
	 * 在测试用例执行完之后，删除测试数据
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@After
	public void after(){
		InitAndDestDataForCollTb.after();
	}
}
