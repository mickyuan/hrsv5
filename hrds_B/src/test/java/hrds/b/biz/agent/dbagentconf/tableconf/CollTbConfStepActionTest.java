package hrds.b.biz.agent.dbagentconf.tableconf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.b.biz.agent.bean.CollTbConfParam;
import hrds.b.biz.agent.dbagentconf.BaseInitData;
import hrds.commons.codes.*;
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
	private static final long SYS_USER_TABLE_ID = 7001L;
	private static final long AGENT_INFO_TABLE_ID = 7003L;
	private static final long DATA_SOURCE_TABLE_ID = 7004L;
	private static final long FIRST_DATABASESET_ID = 1001L;
	private static final long SECOND_DATABASESET_ID = 1002L;
	private static final long UNEXPECTED_ID = 999999;
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
	 *          7-1、table_id:7001,table_name:sys_user,按照画面配置信息进行采集，并且配置了单表过滤SQL,select * from sys_user where user_id = 2001，不进行并行抽取
	 *          7-2、table_id:7002,table_name:code_info,按照画面配置信息进行采集,进行并行抽取，分页SQL为select * from code_info limit 10,设置数据量为10万，并行抽取线程数为6，每日增量数据为1000条
	 *          7-3、table_id:7003,table_name:agent_info,按照自定义SQL进行采集，不进行并行抽取
	 *          7-4、table_id:7004,table_name:data_source,按照自定义SQL进行采集，不进行并行抽取
	 *      8、table_column表测试数据：只有在画面上进行配置的采集表才会向table_column表中保存数据
	 *          8-1、column_id为2001-2010，模拟采集了sys_user表的前10个列，列名为user_id，create_id，dep_id，role_id，
	 *               user_name，user_password，user_email，user_mobile，useris_admin，user_type
     *          8-2、column_id为3001-3005，模拟采集了code_info表的所有列，列名为ci_sp_code，ci_sp_class，ci_sp_classname，
	 *               ci_sp_name，ci_sp_remark
	 *          8-3、模拟自定义采集agent_info表的agent_id，agent_name，agent_type三个字段
	 *          8-4、模拟自定义采集data_source表的source_id，datasource_number，datasource_name三个字段
	 *      9、table_storage_info表测试数据：
	 *          9-1、storage_id为1234，文件格式为CSV，存储方式为替换，table_id为sys_user表的ID
	 *          9-2、storage_id为5678，文件格式为定长文件，存储方式为追加，table_id为code_info表的ID
	 *          9-3、storage_id为1273，文件格式为CSV，存储方式为替换，table_id为data_source表的ID
	 *          9-4、storage_id为4288，文件格式为定长文件，存储方式为追加，table_id为agent_info表的ID
	 *      10、table_clean表测试数据：
	 *          10-1、模拟数据对sys_user表进行整表清洗，分别做了列合并和首尾去空
	 *          10-2、模拟数据对code_info表进行了整表清洗，分别做了字符替换字符补齐，将所有列值的abc全部替换为def，将所有列值的前面补上beyond字符串
	 *          10-3、模拟数据对agent_info表进行了整表清洗，分别做了字符替换字符补齐，将所有列值的qwe全部替换为asd，将所有列值的前面补上hongzhi字符串
	 *          10-4、模拟数据对data_source表进行了整表清洗，分别做了字符替换字符补齐，将所有列值的uio全部替换为jkl，将所有列值的前面补上beyond_hongzhi字符串
	 *      11、column_merge表测试数据：对sys_user设置了两个列合并，对code_info表设置了一个列合并
	 *          11-1、模拟数据将sys_user表的user_id和create_id两列合并为user_create_id
	 *          11-2、模拟数据将sys_user表的user_name和user_password两列合并为user_name_password
	 *          11-3、模拟数据将code_info表的ci_sp_classname和ci_sp_name两列合并为ci_sp_classname_name
	 *      12、data_extraction_def表测试数据：
	 *          12-1、sys_user:仅抽取，需要表头，落地编码为UTF-8，数据落地格式为ORC，数据落地目录为/root
	 *          12-2、code_info:仅抽取，不需要表头，落地编码为UTF-8，数据落地格式为PARQUET，数据落地目录为/home/hyshf
	 *          12-3、data_source:抽取并入库，不需要表头，落地编码为UTF-8，数据落地格式为CSV
	 *          12-4、agent_info:抽取并入库，不需要表头，落地编码为UTF-8，数据落地格式为非定长，列分隔符为"\r"，行分隔符为"|"
	 *      13、data_relation_table表测试数据：
	 *          13-1、sys_user表保存进入关系型数据库
	 *          13-2、code_info表保存进入关系型数据库
	 *          13-3、data_source表保存进入关系型数据库
	 *          13-4、agent_info表保存进入关系型数据库
	 *      14、由于该Action类的测试连接功能需要与agent端交互，所以需要配置一条agent_down_info表的记录，用于找到http访问的完整url
	 *      15、column_merge表测试数据:
	 *          15-1、给datasource_number设置字符前补齐，补齐字符为wzc
	 *          15-2、给agent_name设置字符后补齐，补齐字符为空格
	 *          15-3、给user_mobile设置字符前补齐，补齐字符为wzc
	 *          15-4、给ci_sp_code设置字符后补齐，补齐字符为空格
	 *      16、column_storage_info表测试数据:
	 *          16-1、data_source表中的source_id字段作为关系型数据库主键
	 *          16-2、sys_user表中的user_id字段作为关系型数据库主键
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
		assertThat("根据测试数据，输入正确的colSetId查询到的非自定义采集表应该有" + rightData.getRowCount() + "条", rightData.getRowCount(), is(2));

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
	 * TODO 目前测试用例只判断和agent能够调通，并且获取到的list有值，因为目前我们自己的测试用数据库是处于变化当中的,不能准确确定值的数量
	 * 正确数据访问1：构造colSetId为1002，inputString为code的测试数据
	 * 正确数据访问2：构造colSetId为1002，inputString为sys的测试数据
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
		//正确数据访问1：构造colSetId为1002，inputString为code的测试数据
		String rightStringOne = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("inputString", "code")
				.post(getActionUrl("getTableInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Result rightDataOne = rightResultOne.getDataForResult();
		assertThat("使用code做模糊查询得到的表信息", rightDataOne.isEmpty(), is(false));

		/*
		List<Result> rightDataOne = rightResultOne.getDataForEntityList(Result.class);

		assertThat("使用code做模糊查询得到的表信息有1条", rightDataOne.size(), is(1));
		assertThat("使用code做模糊查询得到的表名为code_info", rightDataOne.get(0).getString(0, "table_name"), is("code_info"));
		*/

		//正确数据访问2：构造colSetId为1002，inputString为sys的测试数据
		String rightStringTwo = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("inputString", "sys")
				.post(getActionUrl("getTableInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		Result rightDataTwo = rightResultTwo.getDataForResult();
		assertThat("使用sys做模糊查询得到的表信息", rightDataTwo.isEmpty(), is(false));

		/*
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
		*/

		//正确数据访问3：构造colSetId为1002，inputString为sys|code的测试数据
		String rightStringThree = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("inputString", "sys|code")
				.post(getActionUrl("getTableInfo")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));
		Result rightDataThree = rightResultThree.getDataForResult();
		assertThat("使用sys|code做模糊查询得到的表信息", rightDataThree.isEmpty(), is(false));
		/*
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
		*/
		//正确数据访问4：构造colSetId为1002，inputString为wzc的测试数据
		String rightStringFour = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("inputString", "wzc")
				.post(getActionUrl("getTableInfo")).getBodyString();
		ActionResult rightResultFour = JsonUtil.toObjectSafety(rightStringFour, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultFour.isSuccess(), is(true));
		Result rightDataFour = rightResultFour.getDataForResult();
		assertThat("使用wzc做模糊查询得到的表信息有0条", rightDataFour.isEmpty(), is(true));
		/*
		List<Result> rightDataFour = rightResultFour.getDataForEntityList(Result.class);
		assertThat("使用wzc做模糊查询得到的表信息有0条", rightDataFour.isEmpty(), is(true));
		*/
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
	 * TODO 由于目前测试用的数据库是我们的测试库，所以表的数量不固定
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
		Result rightData = rightResult.getDataForResult();
		assertThat("截止2019.11.27，IP为47.103.83.1的测试库上有72张表",rightData.getRowCount(), is(72));

		//错误的数据访问1：构造错误的colSetId进行测试
		long wrongColSetId = 1003L;
		String wrongString = new HttpClient()
				.addData("colSetId", wrongColSetId)
				.post(getActionUrl("getAllTableInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试根据table_id获取对该表定义的分页SQL
	 * 正确数据访问1：使用设置了分页SQL的tableId进行查询(code_info)
	 * 错误的数据访问1：使用未设置分页SQL的tableId进行查询(sys_user)
	 * 错误的数据访问2：使用不存在的tableId进行查询
	 * 错误的测试用例未达到三组:getPageSQL方法只有一个参数,上述测试用例已经可以覆盖所有可能出现的情况
	 * @Param: 无
	 * @return: 无
	 * */
	@Test
	public void getPageSQL(){
		//正确数据访问1：使用设置了分页SQL的tableId进行查询
		String rightString = new HttpClient()
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("getPageSQL")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Result rightData = rightResult.getDataForResult();
		assertThat("获得了一条数据", rightData.getRowCount(), is(1));
		assertThat("获得的<分页抽取SQL>是select * from code_info limit 10", rightData.getString(0, "page_sql"), is("select * from code_info limit 10"));
		assertThat("获得的<数据量>是100000", rightData.getString(0, "table_count"), is("100000"));
		assertThat("获得的<并行抽取线程数>是6", rightData.getInt(0, "pageparallels"), is(6));
		assertThat("获得的<每日数据增量>是1000", rightData.getInt(0, "dataincrement"), is(1000));

		//错误的数据访问1：使用未设置分页SQL的tableId进行查询
		String wrongStringOne = new HttpClient()
				.addData("tableId", SYS_USER_TABLE_ID)
				.post(getActionUrl("getPageSQL")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(true));
		Result wrongDataOne = wrongResultOne.getDataForResult();
		assertThat("获得了一条数据", wrongDataOne.getRowCount(), is(1));
		assertThat("获得的分页抽取SQL是空字符串", wrongDataOne.getString(0, "page_sql"), is(""));

		//错误的数据访问2：使用不存在的tableId进行查询
		long wrongTableId = 12138L;
		String wrongStringTwo = new HttpClient()
				.addData("tableId", wrongTableId)
				.post(getActionUrl("getPageSQL")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));
	}

	/**
	 * 测试并行采集SQL测试功能
	 * 正确数据访问1：构建正确的colSetId和SQL语句
	 * 错误的数据访问1：构建错误的colSetId和正确的SQL语句
	 * 错误的数据访问2：构建正确的colSetId和错误SQL语句
	 * 错误的数据访问3：构建正确的colSetId和正确的SQL语句，但是SQL语句查不到数据
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void testParallelExtraction(){
		//正确数据访问1：构建正确的colSetId和SQL语句
		String pageSQL = "select * from sys_user limit 2 offset 0";
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("pageSql", pageSQL)
				.post(getActionUrl("testParallelExtraction")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		//错误的数据访问1：构建错误的colSetId和正确的SQL语句
		long wrongColSetId = 1003L;
		String wrongColSetIdString = new HttpClient()
				.addData("colSetId", wrongColSetId)
				.addData("pageSql", pageSQL)
				.post(getActionUrl("testParallelExtraction")).getBodyString();
		ActionResult wrongColSetIdResult = JsonUtil.toObjectSafety(wrongColSetIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongColSetIdResult.isSuccess(), is(false));

		//错误的数据访问2：构建正确的colSetId和错误SQL语句
		String wrongSQL = "select * from sys_user limit 10,20";
		String wrongSQLString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("pageSql", wrongSQL)
				.post(getActionUrl("testParallelExtraction")).getBodyString();
		ActionResult wrongSQLResult = JsonUtil.toObjectSafety(wrongSQLString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongSQLResult.isSuccess(), is(false));

		//错误的数据访问3：构建正确的colSetId和正确的SQL语句，但是SQL语句查不到数据
		String wrongSQLTwo = "select * from collect_case";
		String wrongSQLStringTwo = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("pageSql", wrongSQLTwo)
				.post(getActionUrl("testParallelExtraction")).getBodyString();
		ActionResult wrongSQLResultTwo = JsonUtil.toObjectSafety(wrongSQLStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongSQLResultTwo.isSuccess(), is(false));
	}

	/**
	 * 根据表名和agent端交互，获取该表的数据总条数
	 * 正确数据访问1：构建正确的colSetId和表名，表中有数据
	 * 错误的数据访问1：构建错误的colSetId和正确的表名
	 * 错误的数据访问2：构建正确的colSetId和错误的表名
	 * 错误的测试用例不足三条：以上场景足以覆盖代码中所有的分支
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getTableDataCount(){
		//正确数据访问1：构建正确的colSetId和表名，表中有数据
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("tableName", "table_info")
				.post(getActionUrl("getTableDataCount")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Integer tableInfoCount = (Integer) rightResult.getData();
		assertThat("table_info表的数据有" + tableInfoCount.toString() + "条", true , is(true));

		//错误的数据访问1：构建错误的colSetId和正确的表名
		String wrongStringOne = new HttpClient()
				.addData("colSetId", UNEXPECTED_ID)
				.addData("tableName", "table_info")
				.post(getActionUrl("getTableDataCount")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));

		//错误的数据访问2：构建正确的colSetId和错误的表名
		String wrongStringTwo = new HttpClient()
				.addData("colSetId", UNEXPECTED_ID)
				.addData("tableName", "wzc")
				.post(getActionUrl("getTableDataCount")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));
	}

	/**
	 * 测试SQL查询设置页面，保存按钮后台方法功能
	 *
	 * 正确数据访问1：构造两条自定义SQL查询设置数据，测试保存功能
	 * 正确数据访问2：模拟修改agent_info和data_source表的自定义SQL查询,则之前为agent_info和data_source表定义的脏数据都会被删除
	 * 正确数据访问3：初始化数据中已经存在agent_info表和data_source表的自定义查询SQL，构造什么都不传的一次数据访问，
	 * 应该把和agent_ifno、data_source的相关脏数据在其他表中删除
	 * 错误的数据访问1：构造两条自定义SQL查询设置数据，第一条数据的表名为空
	 * 错误的数据访问2：构造两条自定义SQL查询设置数据，第二条数据的中文名为空
	 * 错误的数据访问3：构造两条自定义SQL查询设置数据，第一条数据的sql为空
	 * 错误的数据访问4：构造不存在于测试用例模拟数据中的databaseId
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
					tableName = "getHalfStructTask";
					tableChName = "获得半结构化采集任务";
					customizeSQL = "SELECT odc_id, object_collect_type, obj_number FROM "+ Object_collect.TableName;
					break;
				case 2 :
					tableName = "getFTPTask";
					tableChName = "获得FTP采集任务";
					customizeSQL = "SELECT ftp_id, ftp_number, ftp_name FROM "+ Ftp_collect.TableName;
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
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveAllSQL")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Integer returnValue = (Integer) rightResult.getData();
		assertThat(returnValue == FIRST_DATABASESET_ID, is(true));

		List<Table_info> expectedList;

		//保存成功，验证数据库中的记录是否符合预期
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			expectedList = SqlOperator.queryList(db, Table_info.class, "select * from " + Table_info.TableName + " where database_id = ? AND is_user_defined = ?", FIRST_DATABASESET_ID, IsFlag.Shi.getCode());
			assertThat("保存成功后，table_info表中的用户自定义SQL查询数目应该有2条", expectedList.size(), is(2));
			for(Table_info tableInfo : expectedList){
				if(tableInfo.getTable_name().equalsIgnoreCase("getHalfStructTask")){
					assertThat("保存成功后，自定义SQL查询getHalfStructTask的中文名应该是<获得半结构化采集任务>", tableInfo.getTable_ch_name(), is("获得半结构化采集任务"));
					Result resultOne = SqlOperator.queryResult(db, "select is_get, is_primary_key, column_name, column_type, column_ch_name, is_alive, is_new, tc_or from " + Table_column.TableName + " where table_id = ?", tableInfo.getTable_id());
					assertThat("保存成功后，自定义SQL采集的三列数据被保存到了table_column表中", resultOne.getRowCount(), is(3));
					for(int i = 0; i < resultOne.getRowCount(); i++){
						if(resultOne.getString(i, "column_name").equalsIgnoreCase("odc_id")){
							assertThat("采集列名为odc_id，is_get字段符合预期", resultOne.getString(i, "is_get"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为odc_id，is_primary_key字段符合预期", resultOne.getString(i, "is_primary_key"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为odc_id，column_type字段符合预期", resultOne.getString(i, "column_type").equalsIgnoreCase("int8"), is(true));
							assertThat("采集列名为odc_id，column_ch_name字段符合预期", resultOne.getString(i, "column_ch_name"), is("odc_id"));
							assertThat("采集列名为odc_id，is_alive字段符合预期", resultOne.getString(i, "is_alive"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为odc_id，is_new字段符合预期", resultOne.getString(i, "is_new"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为odc_id，tc_or字段符合预期", resultOne.getString(i, "tc_or"), is(columnCleanOrder.toJSONString()));
						}else if(resultOne.getString(i, "column_name").equalsIgnoreCase("object_collect_type")){
							assertThat("采集列名为object_collect_type，is_get字段符合预期", resultOne.getString(i, "is_get"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为object_collect_type，is_primary_key字段符合预期", resultOne.getString(i, "is_primary_key"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为object_collect_type，column_type字段符合预期", resultOne.getString(i, "column_type").equalsIgnoreCase("bpchar(1)"), is(true));
							assertThat("采集列名为object_collect_type，column_ch_name字段符合预期", resultOne.getString(i, "column_ch_name"), is("object_collect_type"));
							assertThat("采集列名为object_collect_type，is_alive字段符合预期", resultOne.getString(i, "is_alive"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为object_collect_type，is_new字段符合预期", resultOne.getString(i, "is_new"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为object_collect_type，tc_or字段符合预期", resultOne.getString(i, "tc_or"), is(columnCleanOrder.toJSONString()));
						}else if(resultOne.getString(i, "column_name").equalsIgnoreCase("obj_number")){
							assertThat("采集列名为obj_number，is_get字段符合预期", resultOne.getString(i, "is_get"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为obj_number，is_primary_key字段符合预期", resultOne.getString(i, "is_primary_key"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为obj_number，column_type字段符合预期", resultOne.getString(i, "column_type").equalsIgnoreCase("varchar(200)"), is(true));
							assertThat("采集列名为obj_number，column_ch_name字段符合预期", resultOne.getString(i, "column_ch_name"), is("obj_number"));
							assertThat("采集列名为obj_number，is_alive字段符合预期", resultOne.getString(i, "is_alive"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为obj_number，is_new字段符合预期", resultOne.getString(i, "is_new"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为obj_number，tc_or字段符合预期", resultOne.getString(i, "tc_or"), is(columnCleanOrder.toJSONString()));
						}else{
							assertThat("出现了不符合预期的情况，采集列名为：" + resultOne.getString(i, "column_name"), true, is(false));
						}
					}
				}else if(tableInfo.getTable_name().equalsIgnoreCase("getFTPTask")){
					assertThat("保存成功后，自定义SQL查询getFTPTask的中文名应该是<获得FTP采集任务>", tableInfo.getTable_ch_name(), is("获得FTP采集任务"));
					Result resultTwo = SqlOperator.queryResult(db, "select is_get, is_primary_key, column_name, column_type, column_ch_name, is_alive, is_new, tc_or from " + Table_column.TableName + " where table_id = ?", tableInfo.getTable_id());
					assertThat("保存成功后，自定义SQL采集的三列数据被保存到了table_column表中", resultTwo.getRowCount(), is(3));
					for(int i = 0; i < resultTwo.getRowCount(); i++){
						if(resultTwo.getString(i, "column_name").equalsIgnoreCase("ftp_id")){
							assertThat("采集列名为ftp_id，is_get字段符合预期", resultTwo.getString(i, "is_get"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为ftp_id，is_primary_key字段符合预期", resultTwo.getString(i, "is_primary_key"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为ftp_id，column_type字段符合预期", resultTwo.getString(i, "column_type").equalsIgnoreCase("int8"), is(true));
							assertThat("采集列名为ftp_id，column_ch_name字段符合预期", resultTwo.getString(i, "column_ch_name"), is("ftp_id"));
							assertThat("采集列名为ftp_id，is_alive字段符合预期", resultTwo.getString(i, "is_alive"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为ftp_id，is_new字段符合预期", resultTwo.getString(i, "is_new"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为ftp_id，tc_or字段符合预期", resultTwo.getString(i, "tc_or"), is(columnCleanOrder.toJSONString()));
						}else if(resultTwo.getString(i, "column_name").equalsIgnoreCase("ftp_number")){
							assertThat("采集列名为ftp_number，is_get字段符合预期", resultTwo.getString(i, "is_get"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为ftp_number，is_primary_key字段符合预期", resultTwo.getString(i, "is_primary_key"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为ftp_number，column_type字段符合预期", resultTwo.getString(i, "column_type").equalsIgnoreCase("varchar(200)"), is(true));
							assertThat("采集列名为ftp_number，column_ch_name字段符合预期", resultTwo.getString(i, "column_ch_name"), is("ftp_number"));
							assertThat("采集列名为ftp_number，is_alive字段符合预期", resultTwo.getString(i, "is_alive"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为ftp_number，is_new字段符合预期", resultTwo.getString(i, "is_new"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为ftp_number，tc_or字段符合预期", resultTwo.getString(i, "tc_or"), is(columnCleanOrder.toJSONString()));
						}else if(resultTwo.getString(i, "column_name").equalsIgnoreCase("ftp_name")){
							assertThat("采集列名为ftp_name，is_get字段符合预期", resultTwo.getString(i, "is_get"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为ftp_name，is_primary_key字段符合预期", resultTwo.getString(i, "is_primary_key"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为ftp_name，column_type字段符合预期", resultTwo.getString(i, "column_type").equalsIgnoreCase("varchar(512)"), is(true));
							assertThat("采集列名为ftp_name，column_ch_name字段符合预期", resultTwo.getString(i, "column_ch_name"), is("ftp_name"));
							assertThat("采集列名为ftp_name，is_alive字段符合预期", resultTwo.getString(i, "is_alive"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为ftp_name，is_new字段符合预期", resultTwo.getString(i, "is_new"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为ftp_name，tc_or字段符合预期", resultTwo.getString(i, "tc_or"), is(columnCleanOrder.toJSONString()));
						}else{
							assertThat("出现了不符合预期的情况，采集列名为：" + resultTwo.getString(i, "column_name"), true, is(false));
						}
					}
				}else{
					assertThat("保存出错，出现了不希望出现的数据，表id为" + tableInfo.getTable_id(), true, is(false));
				}
			}



			//验证完毕后，将自己在本方法中构造的数据删除掉(table_info表)
			int firCount = SqlOperator.execute(db, "delete from " + Table_info.TableName + " WHERE table_name = ?", "getHalfStructTask");
			assertThat("测试完成后，table_name为getHalfStructTask的测试数据被删除了", firCount, is(1));
			int secCount = SqlOperator.execute(db, "delete from " + Table_info.TableName + " WHERE table_name = ?", "getFTPTask");
			assertThat("测试完成后，table_name为getFTPTask的测试数据被删除了", secCount, is(1));

			//验证完毕后，将自己在本方法中构造的数据删除掉(table_column表)
			for(Table_info tableInfo : expectedList){
				SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ?", tableInfo.getTable_id());
			}
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
				.addData("colSetId", FIRST_DATABASESET_ID)
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
				.addData("colSetId", FIRST_DATABASESET_ID)
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
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveAllSQL")).getBodyString();
		ActionResult errorResultThree = JsonUtil.toObjectSafety(errorStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(errorResultThree.isSuccess(), is(false));

		//错误的数据访问4：构造不存在于测试用例模拟数据中的databaseId
		long wrongDatabaseId = 8888888L;
		String errorDatabaseId = new HttpClient()
				.addData("tableInfoArray", array.toJSONString())
				.addData("colSetId", wrongDatabaseId)
				.post(getActionUrl("saveAllSQL")).getBodyString();
		ActionResult errorDatabaseIdResult = JsonUtil.toObjectSafety(errorDatabaseId, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(errorDatabaseIdResult.isSuccess(), is(false));
	}

	/**
	 * 测试SQL查询设置页面，保存按钮后台方法功能
	 *
	 * 正确数据访问2：模拟修改agent_info和data_source表的自定义SQL查询
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveAllSQLTwo(){
		List<Table_info> tableInfos = new ArrayList<>();
		for(int i = 1; i <= 2; i++){
			long tableId;
			String tableName;
			String tableChName;
			String customizeSQL;
			switch (i) {
				case 1 :
					tableId = AGENT_INFO_TABLE_ID;
					tableName = "agent_info_customize";
					tableChName = "agent信息表自定义";
					customizeSQL = "SELECT agent_ip, agent_port, agent_status FROM "+ Agent_info.TableName;
					break;
				case 2 :
					tableId = DATA_SOURCE_TABLE_ID;
					tableName = "data_source_customize";
					tableChName = "数据源表自定义";
					customizeSQL = "SELECT source_remark, create_date, create_time FROM "+ Data_source.TableName;
					break;
				default:
					tableId = UNEXPECTED_ID;
					tableName = "unexpected_tableName";
					tableChName = "unexpected_tableChName";
					customizeSQL = "unexpected_customizeSQL";
			}
			Table_info tableInfo = new Table_info();
			tableInfo.setTable_name(tableName);
			tableInfo.setTable_ch_name(tableChName);
			tableInfo.setSql(customizeSQL);
			tableInfo.setTable_id(tableId);

			tableInfos.add(tableInfo);
		}

		String rightString = new HttpClient()
				.addData("tableInfoArray", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveAllSQL")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Integer returnValue = (Integer) rightResult.getData();
		assertThat(returnValue == FIRST_DATABASESET_ID, is(true));

		List<Table_info> expectedList;
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			expectedList = SqlOperator.queryList(db, Table_info.class, "select * from " + Table_info.TableName + " where database_id = ? AND is_user_defined = ?", FIRST_DATABASESET_ID, IsFlag.Shi.getCode());
			assertThat("保存成功后，table_info表中的用户自定义SQL查询数目应该有2条", expectedList.size(), is(2));
			for(Table_info tableInfo : expectedList){
				if(tableInfo.getTable_name().equalsIgnoreCase("agent_info_customize")){
					assertThat("保存成功后，自定义SQL查询agent_info_customize的中文名应该是<agent信息表自定义>", tableInfo.getTable_ch_name(), is("agent信息表自定义"));
					Result resultOne = SqlOperator.queryResult(db, "select is_get, is_primary_key, column_name, column_type, column_ch_name, is_alive, is_new, tc_or from " + Table_column.TableName + " where table_id = ?", tableInfo.getTable_id());
					assertThat("保存成功后，自定义SQL采集的三列数据被保存到了table_column表中", resultOne.getRowCount(), is(3));
					for(int i = 0; i < resultOne.getRowCount(); i++){
						if(resultOne.getString(i, "column_name").equalsIgnoreCase("agent_ip")){
							assertThat("采集列名为agent_ip，is_get字段符合预期", resultOne.getString(i, "is_get"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为agent_ip，is_primary_key字段符合预期", resultOne.getString(i, "is_primary_key"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为agent_ip，column_type字段符合预期", resultOne.getString(i, "column_type").equalsIgnoreCase("varchar(50)"), is(true));
							assertThat("采集列名为agent_ip，column_ch_name字段符合预期", resultOne.getString(i, "column_ch_name"), is("agent_ip"));
							assertThat("采集列名为agent_ip，is_alive字段符合预期", resultOne.getString(i, "is_alive"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为agent_ip，is_new字段符合预期", resultOne.getString(i, "is_new"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为agent_ip，tc_or字段符合预期", resultOne.getString(i, "tc_or"), is(columnCleanOrder.toJSONString()));
						}else if(resultOne.getString(i, "column_name").equalsIgnoreCase("agent_port")){
							assertThat("采集列名为agent_port，is_get字段符合预期", resultOne.getString(i, "is_get"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为agent_port，is_primary_key字段符合预期", resultOne.getString(i, "is_primary_key"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为agent_port，column_type字段符合预期", resultOne.getString(i, "column_type").equalsIgnoreCase("varchar(10)"), is(true));
							assertThat("采集列名为agent_port，column_ch_name字段符合预期", resultOne.getString(i, "column_ch_name"), is("agent_port"));
							assertThat("采集列名为agent_port，is_alive字段符合预期", resultOne.getString(i, "is_alive"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为agent_port，is_new字段符合预期", resultOne.getString(i, "is_new"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为agent_port，tc_or字段符合预期", resultOne.getString(i, "tc_or"), is(columnCleanOrder.toJSONString()));
						}else if(resultOne.getString(i, "column_name").equalsIgnoreCase("agent_status")){
							assertThat("采集列名为agent_status，is_get字段符合预期", resultOne.getString(i, "is_get"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为agent_status，is_primary_key字段符合预期", resultOne.getString(i, "is_primary_key"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为agent_status，column_type字段符合预期", resultOne.getString(i, "column_type").equalsIgnoreCase("bpchar(1)"), is(true));
							assertThat("采集列名为agent_status，column_ch_name字段符合预期", resultOne.getString(i, "column_ch_name"), is("agent_status"));
							assertThat("采集列名为agent_status，is_alive字段符合预期", resultOne.getString(i, "is_alive"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为agent_status，is_new字段符合预期", resultOne.getString(i, "is_new"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为agent_status，tc_or字段符合预期", resultOne.getString(i, "tc_or"), is(columnCleanOrder.toJSONString()));
						}else{
							assertThat("出现了不符合预期的情况，采集列名为：" + resultOne.getString(i, "column_name"), true, is(false));
						}
					}
				}else if(tableInfo.getTable_name().equalsIgnoreCase("data_source_customize")){
					assertThat("保存成功后，自定义SQL查询data_source_customize的中文名应该是<数据源表自定义>", tableInfo.getTable_ch_name(), is("数据源表自定义"));
					Result resultTwo = SqlOperator.queryResult(db, "select is_get, is_primary_key, column_name, column_type, column_ch_name, is_alive, is_new, tc_or from " + Table_column.TableName + " where table_id = ?", tableInfo.getTable_id());
					assertThat("保存成功后，自定义SQL采集的三列数据被保存到了table_column表中", resultTwo.getRowCount(), is(3));
					for(int i = 0; i < resultTwo.getRowCount(); i++){
						if(resultTwo.getString(i, "column_name").equalsIgnoreCase("source_remark")){
							assertThat("采集列名为source_remark，is_get字段符合预期", resultTwo.getString(i, "is_get"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为source_remark，is_primary_key字段符合预期", resultTwo.getString(i, "is_primary_key"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为source_remark，column_type字段符合预期", resultTwo.getString(i, "column_type").equalsIgnoreCase("varchar(512)"), is(true));
							assertThat("采集列名为source_remark，column_ch_name字段符合预期", resultTwo.getString(i, "column_ch_name"), is("source_remark"));
							assertThat("采集列名为source_remark，is_alive字段符合预期", resultTwo.getString(i, "is_alive"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为source_remark，is_new字段符合预期", resultTwo.getString(i, "is_new"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为source_remark，tc_or字段符合预期", resultTwo.getString(i, "tc_or"), is(columnCleanOrder.toJSONString()));
						}else if(resultTwo.getString(i, "column_name").equalsIgnoreCase("create_date")){
							assertThat("采集列名为create_date，is_get字段符合预期", resultTwo.getString(i, "is_get"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为create_date，is_primary_key字段符合预期", resultTwo.getString(i, "is_primary_key"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为create_date，column_type字段符合预期", resultTwo.getString(i, "column_type").equalsIgnoreCase("bpchar(8)"), is(true));
							assertThat("采集列名为create_date，column_ch_name字段符合预期", resultTwo.getString(i, "column_ch_name"), is("create_date"));
							assertThat("采集列名为create_date，is_alive字段符合预期", resultTwo.getString(i, "is_alive"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为create_date，is_new字段符合预期", resultTwo.getString(i, "is_new"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为create_date，tc_or字段符合预期", resultTwo.getString(i, "tc_or"), is(columnCleanOrder.toJSONString()));
						}else if(resultTwo.getString(i, "column_name").equalsIgnoreCase("create_time")){
							assertThat("采集列名为create_time，is_get字段符合预期", resultTwo.getString(i, "is_get"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为create_time，is_primary_key字段符合预期", resultTwo.getString(i, "is_primary_key"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为create_time，column_type字段符合预期", resultTwo.getString(i, "column_type").equalsIgnoreCase("bpchar(6)"), is(true));
							assertThat("采集列名为create_time，column_ch_name字段符合预期", resultTwo.getString(i, "column_ch_name"), is("create_time"));
							assertThat("采集列名为create_time，is_alive字段符合预期", resultTwo.getString(i, "is_alive"), is(IsFlag.Shi.getCode()));
							assertThat("采集列名为create_time，is_new字段符合预期", resultTwo.getString(i, "is_new"), is(IsFlag.Fou.getCode()));
							assertThat("采集列名为create_time，tc_or字段符合预期", resultTwo.getString(i, "tc_or"), is(columnCleanOrder.toJSONString()));
						}else{
							assertThat("出现了不符合预期的情况，采集列名为：" + resultTwo.getString(i, "column_name"), true, is(false));
						}
					}
				}else{
					assertThat("保存出错，出现了不希望出现的数据，表id为" + tableInfo.getTable_id(), true, is(false));
				}
			}

			//验证在table_info中，原有的table_id做主键的数据已经不存在了
			long countOne = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " where table_id = ?", AGENT_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在table_info表中查不到数据", countOne, is(0L));
			//验证在table_info中，原有的table_id做主键的数据已经不存在了
			long countTwo = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " where table_id = ?", DATA_SOURCE_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在table_info表中查不到数据", countTwo, is(0L));
			//验证在table_column中，原有的table_id做外键的数据已经不存在了
			long countThree = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where table_id = ?", DATA_SOURCE_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在table_column表中查不到数据", countThree, is(0L));
			//验证在table_column中，原有的table_id做外键的数据已经不存在了
			long countFour = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where table_id = ?", AGENT_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在table_column表中查不到数据", countFour, is(0L));

			//验证在table_storage_info中，原有的table_id做外键的数据已经不存在了
			long countFive = SqlOperator.queryNumber(db, "select count(1) from " + Table_storage_info.TableName + " where table_id = ?", AGENT_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在table_storage_info表中查不到数据", countFive, is(0L));
			long countSix = SqlOperator.queryNumber(db, "select count(1) from " + Table_storage_info.TableName + " where table_id = ?", DATA_SOURCE_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在table_storage_info表中查不到数据", countSix, is(0L));
			//验证在data_relation_table中，已经没有data_source表定义的数据存储关系信息了
			long countEle = SqlOperator.queryNumber(db, "select count(1) from " + Data_relation_table.TableName + " where storage_id = ( select storage_id from " + Table_storage_info.TableName + " where table_id = ?)", DATA_SOURCE_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在data_relation_table中，已经没有<data_source>表定义的数据存储关系信息了", countEle, is(0L));
			//验证在data_relation_table中，已经没有agent_info表定义的数据存储关系信息了
			long countTwe = SqlOperator.queryNumber(db, "select count(1) from " + Data_relation_table.TableName + " where storage_id = ( select storage_id from " + Table_storage_info.TableName + " where table_id = ?)", AGENT_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在data_relation_table中，已经没有<data_source>表定义的数据存储关系信息了", countTwe, is(0L));
			//验证在table_clean中，原有的table_id做外键的数据已经不存在了
			long countSeven = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " where table_id = ?", AGENT_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在table_clean表中查不到数据", countSeven, is(0L));
			long countEight = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " where table_id = ?", DATA_SOURCE_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在table_clean表中查不到数据", countEight, is(0L));
			//验证在data_extraction_def中，原有的table_id做外键的数据已经不存在了
			long countNine = SqlOperator.queryNumber(db, "select count(1) from " + Data_extraction_def.TableName + " where table_id = ?", AGENT_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在data_extraction_def表中查不到数据", countNine, is(0L));
			long countTen = SqlOperator.queryNumber(db, "select count(1) from " + Data_extraction_def.TableName + " where table_id = ?", DATA_SOURCE_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在data_extraction_def表中查不到数据", countTen, is(0L));

			//验证在column_storage_info表中，被修改的字段的column_id做外键的数据已经不存在了
			long countThi = SqlOperator.queryNumber(db, "select count(1) from " + Column_storage_info.TableName + " where column_id = ?", 5112L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在column_storage_info表中，<source_id>字段的column_id做外键的数据已经不存在了", countThi, is(0L));
			//验证在column_clean表中，被修改的字段的column_id做外键的数据已经不存在了
			long countFou = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ?", 5113L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在column_clean表中，<datasource_number>字段的column_id做外键的数据已经不存在了", countFou, is(0L));
			long countFif = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ?", 3113L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在column_clean表中，<agent_name>字段的column_id做外键的数据已经不存在了", countFif, is(0L));

			//验证完毕后，将自己在本方法中构造的数据删除掉(table_info表)
			int firCount = SqlOperator.execute(db, "delete from " + Table_info.TableName + " WHERE table_name = ?", "agent_info_customize");
			assertThat("测试完成后，table_name为agent_info_customize的测试数据被删除了", firCount, is(1));
			int secCount = SqlOperator.execute(db, "delete from " + Table_info.TableName + " WHERE table_name = ?", "data_source_customize");
			assertThat("测试完成后，table_name为data_source_customize的测试数据被删除了", secCount, is(1));

			//验证完毕后，将自己在本方法中构造的数据删除掉(table_column表)
			for(Table_info tableInfo : expectedList){
				SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ?", tableInfo.getTable_id());
			}
			SqlOperator.commitTransaction(db);
		}

	}

	/**
	 * 测试SQL查询设置页面，保存按钮后台方法功能
	 *
	 * 正确数据访问3：正确数据访问3：初始化数据中已经存在agent_info表和data_source表的自定义查询SQL，构造什么都不传的一次数据访问，
	 * 应该把和agent_ifno、data_source的相关脏数据在其他表中删除
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveAllSQLThree(){
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveAllSQL")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Integer returnValue = (Integer) rightResult.getData();
		assertThat(returnValue == FIRST_DATABASESET_ID, is(true));

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//验证在table_info中，原有的table_id做主键的数据已经不存在了
			long countOne = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " where table_id = ?", AGENT_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在table_info表中查不到数据", countOne, is(0L));
			//验证在table_info中，原有的table_id做主键的数据已经不存在了
			long countTwo = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " where table_id = ?", DATA_SOURCE_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在table_info表中查不到数据", countTwo, is(0L));
			//验证在table_column中，原有的table_id做外键的数据已经不存在了
			long countThree = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where table_id = ?", DATA_SOURCE_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在table_column表中查不到数据", countThree, is(0L));
			//验证在table_column中，原有的table_id做外键的数据已经不存在了
			long countFour = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where table_id = ?", AGENT_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在table_column表中查不到数据", countFour, is(0L));

			//验证在table_storage_info中，原有的table_id做外键的数据已经不存在了
			long countFive = SqlOperator.queryNumber(db, "select count(1) from " + Table_storage_info.TableName + " where table_id = ?", AGENT_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在table_storage_info表中查不到数据", countFive, is(0L));
			long countSix = SqlOperator.queryNumber(db, "select count(1) from " + Table_storage_info.TableName + " where table_id = ?", DATA_SOURCE_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在table_storage_info表中查不到数据", countSix, is(0L));
			//验证在data_relation_table中，已经没有data_source表定义的数据存储关系信息了
			long countEle = SqlOperator.queryNumber(db, "select count(1) from " + Data_relation_table.TableName + " where storage_id = ( select storage_id from " + Table_storage_info.TableName + " where table_id = ?)", DATA_SOURCE_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在data_relation_table中，已经没有<data_source>表定义的数据存储关系信息了", countEle, is(0L));
			//验证在data_relation_table中，已经没有agent_info表定义的数据存储关系信息了
			long countTwe = SqlOperator.queryNumber(db, "select count(1) from " + Data_relation_table.TableName + " where storage_id = ( select storage_id from " + Table_storage_info.TableName + " where table_id = ?)", AGENT_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在data_relation_table中，已经没有<data_source>表定义的数据存储关系信息了", countTwe, is(0L));
			//验证在table_clean中，原有的table_id做外键的数据已经不存在了
			long countSeven = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " where table_id = ?", AGENT_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在table_clean表中查不到数据", countSeven, is(0L));
			long countEight = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " where table_id = ?", DATA_SOURCE_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在table_clean表中查不到数据", countEight, is(0L));
			//验证在data_extraction_def中，原有的table_id做外键的数据已经不存在了
			long countNine = SqlOperator.queryNumber(db, "select count(1) from " + Data_extraction_def.TableName + " where table_id = ?", AGENT_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在data_extraction_def表中查不到数据", countNine, is(0L));
			long countTen = SqlOperator.queryNumber(db, "select count(1) from " + Data_extraction_def.TableName + " where table_id = ?", DATA_SOURCE_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("使用老的table_id在data_extraction_def表中查不到数据", countTen, is(0L));

			//验证在column_storage_info表中，被修改的字段的column_id做外键的数据已经不存在了
			long countThi = SqlOperator.queryNumber(db, "select count(1) from " + Column_storage_info.TableName + " where column_id = ?", 5112L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在column_storage_info表中，<source_id>字段的column_id做外键的数据已经不存在了", countThi, is(0L));
			//验证在column_clean表中，被修改的字段的column_id做外键的数据已经不存在了
			long countFou = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ?", 5113L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在column_clean表中，<datasource_number>字段的column_id做外键的数据已经不存在了", countFou, is(0L));
			long countFif = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ?", 3113L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在column_clean表中，<ci_sp_code>字段的column_id做外键的数据已经不存在了", countFif, is(0L));
		}
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
				assertThat("在table_id为" + AGENT_INFO_TABLE_ID + "的自定义采集SQL中，自定义SQL为", tableInfo.getSql(), is("select agent_id, agent_name, agent_type from agent_info where source_id = 1"));
			}else if(tableInfo.getTable_id() == DATA_SOURCE_TABLE_ID){
				assertThat("在table_id为" + DATA_SOURCE_TABLE_ID + "的自定义采集SQL中，自定义SQL为", tableInfo.getSql(), is("select source_id, datasource_number, datasource_name from data_source where source_id = 1"));
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
		assertThat("回显table_name为sys_user表定义的过滤SQL", rightDataOne.getString(0, "sql"), is("select * from sys_user where user_id = 9997"));

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
			}else if(key.equalsIgnoreCase("editFlag")){
				assertThat("返回的结果中，有一对Entry的key为editFlag", key, is("editFlag"));
				assertThat("返回的结果中，key为editFlag的Entry，value为1", entry.getValue(), is(IsFlag.Shi.getCode()));
			}else{
				assertThat("返回的结果中，出现了不期望出现的内容", true, is(false));
			}
		}

		//正确数据访问2：构造tableName为ftp_collect，tableId为999999，colSetId为1001的测试数据
		String tableNameTwo = "ftp_collect";
		String rightStringTwo = new HttpClient()
				.addData("tableName", tableNameTwo)
				.addData("colSetId", FIRST_DATABASESET_ID)
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
				assertThat("返回的结果中，key为columnInfo的Entry，value为List<Table_column>,ftp_collect表中有24列", tableColumns.size(), is(24));
			}else if(key.equalsIgnoreCase("editFlag")){
				assertThat("返回的结果中，有一对Entry的key为editFlag", key, is("editFlag"));
				assertThat("返回的结果中，key为editFlag的Entry，value为1", entry.getValue(), is(IsFlag.Shi.getCode()));
			}else{
				assertThat("返回的结果中，出现了不期望出现的内容", true, is(false));
			}
		}

		//错误的数据访问1：构造tableName为ftp_collect，tableId为999999，colSetId为1003的测试数据
		long wrongColSetId = 1003L;
		String wrongColSetIdString = new HttpClient()
				.addData("tableName", tableNameTwo)
				.addData("colSetId", wrongColSetId)
				.addData("collColumnArray", "")
				.addData("columnSortArray", "")
				.post(getActionUrl("getColumnInfo")).getBodyString();
		ActionResult wrongColSetIdResult = JsonUtil.toObjectSafety(wrongColSetIdString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongColSetIdResult.isSuccess(), is(false));

		//错误的数据访问2：构造tableName为wzc_collect，tableId为999999，colSetId为1001的测试数据
		String notExistTableName = "wzc_collect";
		String wrongTableNameString = new HttpClient()
				.addData("tableName", notExistTableName)
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getColumnInfo")).getBodyString();
		ActionResult wrongTableNameResult = JsonUtil.toObjectSafety(wrongTableNameString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongTableNameResult.isSuccess(), is(true));
		Map<Object, Object> wrongTableNameData = wrongTableNameResult.getDataForMap();
		for(Map.Entry<Object, Object> entry : wrongTableNameData.entrySet()){
			String key = (String) entry.getKey();
			if(key.equalsIgnoreCase("tableName")){
				assertThat("返回的结果中，有一对Entry的key为tableName", key, is("tableName"));
				assertThat("返回的结果中，key为tableName的Entry，value为wzc_collect", entry.getValue(), is(notExistTableName));
			}else if(key.equalsIgnoreCase("columnInfo")){
				assertThat("返回的结果中，有一对Entry的key为columnInfo", key, is("columnInfo"));
				List<Table_column> tableColumns = (List<Table_column>) entry.getValue();
				assertThat("返回的结果中，key为columnInfo的Entry，value为List<Table_column>,没有wzc_collect这张表", tableColumns.isEmpty(), is(true));
			}else if(key.equalsIgnoreCase("editFlag")){
				assertThat("返回的结果中，有一对Entry的key为editFlag", key, is("editFlag"));
				assertThat("返回的结果中，key为editFlag的Entry，value为1", entry.getValue(), is(IsFlag.Shi.getCode()));
			}else{
				assertThat("返回的结果中，出现了不期望出现的内容", true, is(false));
			}
		}
	}

	/**
	 * 测试保存单个表的采集信息功能
	 * 正确数据访问1：在database_id为1001的数据库采集任务下构造新增采集ftp_collect表的数据，不选择采集列和列排序，设置并行抽取SQL为select * from ftp_collect limit 10;(需要和agent进行交互获取该表的字段)
	 * 正确数据访问2：在database_id为1001的数据库采集任务下构造新增采集object_collect表的数据，选择采集列和列排序,不设置并行抽取(不需要和agent进行交互)
	 * 正确数据访问3：在database_id为1001的数据库采集任务下构造修改采集code_info表的数据，选择采集列和列排序，不设置并行抽取，
	 * 注意，由于给code_info表构造了data_extraction_def表、table_clean表、table_srorage_info表、column_merge表、table_column表信息
	 * 因此对这个表的采集数据进行修改，要断言修改成功后这5张表的数据是否都被修改了。(不需要和agent进行交互)
	 * 正确数据访问4：在database_id为1001的数据库采集任务下构造新增采集ftp_collect表和object_collect表的数据，不选择采集列和列排序，不设置并行抽取(需要和agent交互)
	 * 正确数据访问5：在database_id为1001的数据库采集任务,不传tableInfoString和collTbConfParamString
	 * 错误的数据访问1：构造缺少表名的采集数据
	 * 错误的数据访问2：构造缺少表中文名的采集数据
	 * 错误的数据访问3：构造设置了并行抽取，但没有设置并行抽取SQL的访问方式
	 * 错误的数据访问4：构造在不存在的数据库采集任务中保存采集ftp_collect表数据
	 * 错误的数据访问5：构造tableInfoString参数是空字符串的情况
	 * 错误的数据访问6：构造collTbConfParamString参数是空字符串的情况
	 * 错误的数据访问7：构造tableInfoString和collTbConfParamString解析成的list集合大小不同的情况
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveCollTbInfoOne(){
		List<Table_info> tableInfos = new ArrayList<>();
		List<CollTbConfParam> tbConfParams = new ArrayList<>();

		//正确数据访问1：在database_id为1001的数据库采集任务下构造新增采集ftp_collect表的数据，不选择采集列和列排序，设置并行抽取SQL为select * from ftp_collect limit 10;(需要和agent进行交互获取该表的字段)
		try(DatabaseWrapper db = new DatabaseWrapper()){
			//在新增前，查询数据库，table_info表中应该没有采集ftp_collect表的信息
			long count = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " where table_name = ?", "ftp_collect").orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在新增前，查询数据库，table_info表中应该没有采集ftp_collect表的信息", count, is(0L));
		}

		Table_info FTPInfo = new Table_info();
		FTPInfo.setTable_name("ftp_collect");
		FTPInfo.setTable_ch_name("ftp采集设置表");
		FTPInfo.setDatabase_id(FIRST_DATABASESET_ID);
		FTPInfo.setIs_parallel(IsFlag.Shi.getCode());
		FTPInfo.setPage_sql("select * from ftp_collect limit 10;");
		FTPInfo.setPageparallels(6);
		FTPInfo.setDataincrement(100000);
		FTPInfo.setTable_count("1000000000");

		tableInfos.add(FTPInfo);

		CollTbConfParam FTPParam = new CollTbConfParam();
		FTPParam.setCollColumnString("");

		tbConfParams.add(FTPParam);

		String rightStringOne = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));
		Integer returnValue = (Integer) rightResultOne.getData();
		assertThat(returnValue == FIRST_DATABASESET_ID, is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			//新增成功后，断言数据库中的数据是否符合期望，如果符合期望，则删除本次新增带来的数据
			Result afterTableInfo = SqlOperator.queryResult(db, "select * from " + Table_info.TableName + " where table_name = ?", "ftp_collect");
			assertThat("<正确的测试用例1>执行成功后，table_info表中出现了采集ftp_collect表的配置", afterTableInfo.getRowCount(), is(1));
			assertThat("<正确的测试用例1>执行成功后，采集ftp_collect表配置，<清洗顺序>符合期望", afterTableInfo.getString(0, "ti_or"), is(tableCleanOrder.toJSONString()));
			assertThat("<正确的测试用例1>执行成功后，采集ftp_collect表配置，<是否使用MD5>符合期望", afterTableInfo.getString(0, "is_md5"), is(IsFlag.Shi.getCode()));
			assertThat("<正确的测试用例1>执行成功后，采集ftp_collect表配置，<是否仅登记>符合期望", afterTableInfo.getString(0, "is_register"), is(IsFlag.Fou.getCode()));
			assertThat("<正确的测试用例1>执行成功后，采集ftp_collect表配置，<是否并行抽取>符合期望", afterTableInfo.getString(0, "is_parallel"), is(IsFlag.Shi.getCode()));
			assertThat("<正确的测试用例1>执行成功后，采集ftp_collect表配置，<分页SQL>符合期望", afterTableInfo.getString(0, "page_sql"), is("select * from ftp_collect limit 10;"));

			Result afterTableColumn = SqlOperator.queryResult(db, "select * from " + Table_column.TableName + " where table_id = ?", afterTableInfo.getLong(0, "table_id"));
			assertThat("<正确的测试用例1>执行成功后，table_column表中有关ftp_collect表的列应该有<24>列", afterTableColumn.getRowCount(), is(24));
			for(int i = 0; i < afterTableColumn.getRowCount(); i++){
				if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_id")){
					assertThat("<正确的测试用例1>执行成功后, <ftp_id>字段的类型为<int8>", afterTableColumn.getString(i, "column_type"), is("int8"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_number")){
					assertThat("<正确的测试用例1>执行成功后, <ftp_number>字段的类型为<varchar(200)>", afterTableColumn.getString(i, "column_type"), is("varchar(200)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_name")){
					assertThat("<正确的测试用例1>执行成功后, <ftp_name>字段的类型为<varchar(512)>", afterTableColumn.getString(i, "column_type"), is("varchar(512)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("start_date")){
					assertThat("<正确的测试用例1>执行成功后, <start_date>字段的类型为<bpchar(8)>", afterTableColumn.getString(i, "column_type"), is("bpchar(8)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("end_date")){
					assertThat("<正确的测试用例1>执行成功后, <end_date>字段的类型为<bpchar(8)>", afterTableColumn.getString(i, "column_type"), is("bpchar(8)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_ip")){
					assertThat("<正确的测试用例1>执行成功后, <ftp_ip>字段的类型为<varchar(50)>", afterTableColumn.getString(i, "column_type"), is("varchar(50)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_port")){
					assertThat("<正确的测试用例1>执行成功后, <ftp_port>字段的类型为<varchar(10)>", afterTableColumn.getString(i, "column_type"), is("varchar(10)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_username")){
					assertThat("<正确的测试用例1>执行成功后, <ftp_username>字段的类型为<varchar(512)>", afterTableColumn.getString(i, "column_type"), is("varchar(512)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_password")){
					assertThat("<正确的测试用例1>执行成功后, <ftp_password>字段的类型为<varchar(100)>", afterTableColumn.getString(i, "column_type"), is("varchar(100)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_dir")){
					assertThat("<正确的测试用例1>执行成功后, <ftp_dir>字段的类型为<varchar(512)>", afterTableColumn.getString(i, "column_type"), is("varchar(512)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("local_path")){
					assertThat("<正确的测试用例1>执行成功后, <local_path>字段的类型为<varchar(512)>", afterTableColumn.getString(i, "column_type"), is("varchar(512)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_rule_path")){
					assertThat("<正确的测试用例1>执行成功后, <ftp_rule_path>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("child_file_path")){
					assertThat("<正确的测试用例1>执行成功后, <child_file_path>字段的类型为<varchar(512)>", afterTableColumn.getString(i, "column_type"), is("varchar(512)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("child_time")){
					assertThat("<正确的测试用例1>执行成功后, <child_time>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("file_suffix")){
					assertThat("<正确的测试用例1>执行成功后, <file_suffix>字段的类型为<varchar(200)>", afterTableColumn.getString(i, "column_type"), is("varchar(200)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_model")){
					assertThat("<正确的测试用例1>执行成功后, <ftp_model>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("run_way")){
					assertThat("<正确的测试用例1>执行成功后, <run_way>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("remark")){
					assertThat("<正确的测试用例1>执行成功后, <remark>字段的类型为<varchar(512)>", afterTableColumn.getString(i, "column_type"), is("varchar(512)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("is_sendok")){
					assertThat("<正确的测试用例1>执行成功后, <is_sendok>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("is_unzip")){
					assertThat("<正确的测试用例1>执行成功后, <is_unzip>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("reduce_type")){
					assertThat("<正确的测试用例1>执行成功后, <reduce_type>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("is_read_realtime")){
					assertThat("<正确的测试用例1>执行成功后, <is_read_realtime>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("realtime_interval")){
					assertThat("<正确的测试用例1>执行成功后, <realtime_interval>字段的类型为<int8>", afterTableColumn.getString(i, "column_type"), is("int8"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("agent_id")){
					assertThat("<正确的测试用例1>执行成功后, <agent_id>字段的类型为<int8>", afterTableColumn.getString(i, "column_type"), is("int8"));
				}else{
					assertThat("<正确的测试用例1>执行完成之后，采集ftp_collect表的所有字段，出现了不符合期望的字段，字段名为 : " + afterTableColumn.getString(i, "column_name"), true, is(false));
				}
			}

			//以上断言都测试成功后，删除<正确数据访问1>生成的数据
			int tableCount = SqlOperator.execute(db, "delete from " + Table_info.TableName + " where table_id = ?", afterTableInfo.getLong(0, "table_id"));
			int columnCount = SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ?", afterTableInfo.getLong(0, "table_id"));
			assertThat("删除<正确数据访问1>生成的数据成功<table_info>", tableCount, is(1));
			assertThat("删除<正确数据访问1>生成的数据成功<table_column>", columnCount, is(24));

			SqlOperator.commitTransaction(db);
		}
	}

	/*
	 * 正确数据访问2：
	 * 在database_id为1001的数据库采集任务下构造新增采集object_collect表的数据，选择采集列和列排序,不设置并行抽取(不需要和agent进行交互)
	 * 模拟采集object_collect表的odc_id、object_collect_type、obj_number三列
	 * */
	@Test
	public void saveCollTbInfoTwo(){
		List<Table_info> tableInfos = new ArrayList<>();
		List<CollTbConfParam> tbConfParams = new ArrayList<>();

		try(DatabaseWrapper db = new DatabaseWrapper()){
			//在新增前，查询数据库，table_info表中应该没有采集object_collect表的信息
			long count = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " where table_name = ?", "object_collect").orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在新增前，查询数据库，table_info表中应该没有采集object_collect表的信息", count, is(0L));
		}
		Table_info objInfo = new Table_info();
		objInfo.setTable_name("object_collect");
		objInfo.setTable_ch_name("半结构化文件采集设置表");
		objInfo.setDatabase_id(FIRST_DATABASESET_ID);
		objInfo.setIs_parallel(IsFlag.Fou.getCode());

		tableInfos.add(objInfo);

		List<Table_column> objColumn = new ArrayList<>();
		for(int i = 0; i < 3; i++){
			String primayKeyFlag;
			String columnName;
			String columnChName;
			String columnType;
			String sort;
			switch (i) {
				case 0 :
					primayKeyFlag = IsFlag.Shi.getCode();
					columnName = "odc_id";
					columnChName = "对象采集id";
					columnType = "int8";
					sort = "1";
					break;
				case 1 :
					primayKeyFlag = IsFlag.Fou.getCode();
					columnName = "object_collect_type";
					columnChName = "对象采集方式";
					columnType = "bpchar(1)";
					sort = "2";
					break;
				case 2 :
					primayKeyFlag = IsFlag.Fou.getCode();
					columnName = "obj_number";
					columnChName = "对象采集设置编号";
					columnType = "varchar(200)";
					sort = "3";
					break;
				default:
					primayKeyFlag = "unexpected_primayKeyFlag";
					columnName = "unexpected_columnName";
					columnChName = "unexpected_columnChName";
					columnType = "unexpected_columnType";
					sort = "unexpected_sort";
			}
			Table_column tableColumn = new Table_column();
			tableColumn.setColumn_name(columnName);
			tableColumn.setColumn_ch_name(columnChName);
			tableColumn.setColumn_type(columnType);
			tableColumn.setIs_get(IsFlag.Shi.getCode());
			tableColumn.setIs_primary_key(primayKeyFlag);
			tableColumn.setTc_remark(sort);

			objColumn.add(tableColumn);
		}


		CollTbConfParam objParam = new CollTbConfParam();
		objParam.setCollColumnString(JSON.toJSONString(objColumn));

		tbConfParams.add(objParam);

		String rightStringTwo = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		Integer returnValueTwo = (Integer) rightResultTwo.getData();
		assertThat(returnValueTwo == FIRST_DATABASESET_ID, is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			//新增成功后，断言数据库中的数据是否符合期望，如果符合期望，则删除本次新增带来的数据
			Result afterTableInfo = SqlOperator.queryResult(db, "select * from " + Table_info.TableName + " where table_name = ?", "object_collect");
			assertThat("<正确的测试用例2>执行成功后，table_info表中出现了采集object_collect表的配置", afterTableInfo.getRowCount(), is(1));
			assertThat("<正确的测试用例2>执行成功后，采集object_collect表配置，<清洗顺序>符合期望", afterTableInfo.getString(0, "ti_or"), is(tableCleanOrder.toJSONString()));
			assertThat("<正确的测试用例2>执行成功后，采集object_collect表配置，<是否使用MD5>符合期望", afterTableInfo.getString(0, "is_md5"), is(IsFlag.Shi.getCode()));
			assertThat("<正确的测试用例2>执行成功后，采集object_collect表配置，<是否仅登记>符合期望", afterTableInfo.getString(0, "is_register"), is(IsFlag.Fou.getCode()));
			assertThat("<正确的测试用例2>执行成功后，采集object_collect表配置，<是否并行抽取>符合期望", afterTableInfo.getString(0, "is_parallel"), is(IsFlag.Fou.getCode()));

			Result afterTableColumn = SqlOperator.queryResult(db, "select * from " + Table_column.TableName + " where table_id = ?", afterTableInfo.getLong(0, "table_id"));
			assertThat("<正确的测试用例2>执行成功后，table_column表中有关object_collect表的列应该有<3>列", afterTableColumn.getRowCount(), is(3));
			for(int i = 0; i < afterTableColumn.getRowCount(); i++){
				if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("odc_id")){
					assertThat("<正确的测试用例2>执行成功后, <odc_id>字段的类型为<int8>", afterTableColumn.getString(i, "column_type"), is("int8"));
					assertThat("<正确的测试用例2>执行成功后, <odc_id>字段的采集顺序为<1>", afterTableColumn.getString(i, "tc_remark"), is("1"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("object_collect_type")){
					assertThat("<正确的测试用例2>执行成功后, <object_collect_type>字段的类型为<int8>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
					assertThat("<正确的测试用例2>执行成功后, <object_collect_type>字段的采集顺序为<2>", afterTableColumn.getString(i, "tc_remark"), is("2"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("obj_number")){
					assertThat("<正确的测试用例2>执行成功后, <obj_number>字段的类型为<int8>", afterTableColumn.getString(i, "column_type"), is("varchar(200)"));
					assertThat("<正确的测试用例2>执行成功后, <obj_number>字段的采集顺序为<3>", afterTableColumn.getString(i, "tc_remark"), is("3"));
				}else{
					assertThat("<正确的测试用例2>执行成功后, 再次查询table_column表，出现了不符合期望的情况，表名为：" + afterTableColumn.getString(i, "column_name"), true, is(false));
				}
			}

			//以上断言都测试成功后，删除<正确数据访问1>生成的数据
			int tableCount = SqlOperator.execute(db, "delete from " + Table_info.TableName + " where table_id = ?", afterTableInfo.getLong(0, "table_id"));
			int columnCount = SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ?", afterTableInfo.getLong(0, "table_id"));
			assertThat("删除<正确数据访问2>生成的数据成功<table_info>", tableCount, is(1));
			assertThat("删除<正确数据访问2>生成的数据成功<table_column>", columnCount, is(3));

			SqlOperator.commitTransaction(db);
		}
	}

	/*
	 * 正确数据访问3：在database_id为1001的数据库采集任务下构造修改采集code_info表的数据，选择采集列和列排序，不设置并行抽取，
	 * 注意，由于给code_info表构造了data_extraction_def表、table_clean表、table_srorage_info表、column_merge表、table_column表信息
	 * 因此对这个表的采集数据进行修改，要断言修改成功后这5张表的数据是否都被修改了。(不需要和agent进行交互)
	 * */
	@Test
	public void saveCollTbInfoThree(){
		List<Table_info> tableInfos = new ArrayList<>();
		List<CollTbConfParam> tbConfParams = new ArrayList<>();

		//注意：由于这里是对code_info表的采集字段进行修改，所以必须要传table_id
		Table_info codeInfo = new Table_info();
		codeInfo.setTable_id(CODE_INFO_TABLE_ID);
		codeInfo.setTable_name("code_info");
		codeInfo.setTable_ch_name("代码信息表");
		codeInfo.setDatabase_id(FIRST_DATABASESET_ID);
		codeInfo.setIs_parallel(IsFlag.Fou.getCode());

		tableInfos.add(codeInfo);

		List<Table_column> codeColumn = new ArrayList<>();
		for(int i = 0; i < 5; i++){
			long columnId;
			String columnChName;
			String primaryKeyFlag;
			String columnName;
			String columnType;
			String sort;
			switch (i) {
				case 0 :
					columnId = 3001L;
					columnChName = "ci_sp_code_ch";
					primaryKeyFlag = IsFlag.Shi.getCode();
					columnName = "ci_sp_code";
					columnType = "varchar";
					sort = "1";
					break;
				case 1 :
					columnId = 3002L;
					columnChName = "ci_sp_class_ch";
					primaryKeyFlag = IsFlag.Shi.getCode();
					columnName = "ci_sp_class";
					columnType = "varchar";
					sort = "2";
					break;
				case 2 :
					columnId = 3003L;
					columnChName = "ci_sp_classname_ch";
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "ci_sp_classname";
					columnType = "varchar";
					sort = "3";
					break;
				case 3 :
					columnId = 3004L;
					columnChName = "ci_sp_name_ch";
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "ci_sp_name";
					columnType = "varchar";
					sort = "4";
					break;
				case 4 :
					columnId = 3005L;
					columnChName = "ci_sp_remark_ch";
					primaryKeyFlag = IsFlag.Fou.getCode();
					columnName = "ci_sp_remark";
					columnType = "varchar";
					sort = "5";
					break;
				default:
					columnId = UNEXPECTED_ID;
					columnChName = "unexpected_columnChName";
					primaryKeyFlag = "unexpected_primaryKeyFlag";
					columnName = "unexpected_columnName";
					columnType = "unexpected_columnType";
					sort = "unexpected_sort";
			}
			Table_column tableColumn = new Table_column();
			tableColumn.setColumn_id(columnId);
			tableColumn.setColumn_ch_name(columnChName);
			tableColumn.setColumn_name(columnName);
			tableColumn.setColumn_type(columnType);
			tableColumn.setIs_primary_key(primaryKeyFlag);
			tableColumn.setTc_remark(sort);

			tableColumn.setTable_id(CODE_INFO_TABLE_ID);
			tableColumn.setValid_s_date(DateUtil.getSysDate());
			tableColumn.setValid_e_date(Constant.MAXDATE);
			tableColumn.setIs_alive(IsFlag.Shi.getCode());
			tableColumn.setIs_new(IsFlag.Fou.getCode());
			tableColumn.setTc_or(columnCleanOrder.toJSONString());

			codeColumn.add(tableColumn);
		}

		CollTbConfParam codeParam = new CollTbConfParam();
		codeParam.setCollColumnString(JSON.toJSONString(codeColumn));

		tbConfParams.add(codeParam);

		String rightStringThree = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));
		Integer returnValueThree = (Integer) rightResultThree.getData();
		assertThat(returnValueThree == FIRST_DATABASESET_ID, is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			//断言table_info表中的内容是否符合期望
			long count = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " where table_id = ?", CODE_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			Result tableInfo = SqlOperator.queryResult(db, "select * from " + Table_info.TableName + " where table_name = ?", "code_info");
			assertThat("code_info表在table_info表中有且只有一条数据，但是该条数据的id和构造初始化数据时不一致，导致这个事情的原因是保存操作全部都是按照先删除后新增的逻辑执行的", count, is(0L));
			assertThat("code_info表在table_info表中有且只有一条数据，但是该条数据的id和构造初始化数据时不一致，导致这个事情的原因是保存操作全部都是按照先删除后新增的逻辑执行的", tableInfo.getRowCount(), is(1));

			long tableId = tableInfo.getLong(0, "table_id");

			//断言table_column表中的内容是否符合期望
			long tbColCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where table_id = ?", CODE_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			Result tableColumn = SqlOperator.queryResult(db, "select * from " + Table_column.TableName + " where table_id = ?", tableId);
			assertThat("code_info表的采集列在table_column表中有数据，数据有5条，并且用构造初始化数据是使用的CODE_INFO_TABLE_ID在table_column表中已经查不到数据了", tbColCount, is(0L));
			assertThat("code_info表的采集列在table_column表中有数据，数据有5条，并且用构造初始化数据是使用的CODE_INFO_TABLE_ID在table_column表中已经查不到数据了", tableColumn.getRowCount(), is(5));
			for(int i = 0; i < tableColumn.getRowCount(); i++){
				if(tableColumn.getString(i, "column_name").equalsIgnoreCase("ci_sp_code")){
					assertThat("采集列名为<ci_sp_code>,该列的数据类型为<varchar>", tableColumn.getString(i, "column_type"), is("varchar"));
					assertThat("采集列名为<ci_sp_code>,该列的ID为<3001>", tableColumn.getLong(i, "column_id"), is(3001L));
					assertThat("采集列名为<ci_sp_code>,该列的中文名为<ci_sp_code_ch>", tableColumn.getString(i, "column_ch_name"), is("ci_sp_code_ch"));
				}else if(tableColumn.getString(i, "column_name").equalsIgnoreCase("ci_sp_class")){
					assertThat("采集列名为<ci_sp_class>,该列的数据类型为<varchar>", tableColumn.getString(i, "column_type"), is("varchar"));
					assertThat("采集列名为<ci_sp_class>,该列的ID为<3002>", tableColumn.getLong(i, "column_id"), is(3002L));
					assertThat("采集列名为<ci_sp_class>,该列的中文名为<ci_sp_class_ch>", tableColumn.getString(i, "column_ch_name"), is("ci_sp_class_ch"));
				}else if(tableColumn.getString(i, "column_name").equalsIgnoreCase("ci_sp_classname")){
					assertThat("采集列名为<ci_sp_classname>,该列的数据类型为<varchar>", tableColumn.getString(i, "column_type"), is("varchar"));
					assertThat("采集列名为<ci_sp_classname>,该列的ID为<3003>", tableColumn.getLong(i, "column_id"), is(3003L));
					assertThat("采集列名为<ci_sp_classname>,该列的中文名为<ci_sp_classname_ch>", tableColumn.getString(i, "column_ch_name"), is("ci_sp_classname_ch"));
				}else if(tableColumn.getString(i, "column_name").equalsIgnoreCase("ci_sp_name")){
					assertThat("采集列名为<ci_sp_name>,该列的数据类型为<varchar>", tableColumn.getString(i, "column_type"), is("varchar"));
					assertThat("采集列名为<ci_sp_name>,该列的ID为<3004>", tableColumn.getLong(i, "column_id"), is(3004L));
					assertThat("采集列名为<ci_sp_name>,该列的中文名为<ci_sp_name_ch>", tableColumn.getString(i, "column_ch_name"), is("ci_sp_name_ch"));
				}else if(tableColumn.getString(i, "column_name").equalsIgnoreCase("ci_sp_remark")){
					assertThat("采集列名为<ci_sp_remark>,该列的数据类型为<varchar>", tableColumn.getString(i, "column_type"), is("varchar"));
					assertThat("采集列名为<ci_sp_remark>,该列的ID为<3005>", tableColumn.getLong(i, "column_id"), is(3005L));
					assertThat("采集列名为<ci_sp_remark>,该列的中文名为<ci_sp_remark_ch>", tableColumn.getString(i, "column_ch_name"), is("ci_sp_remark_ch"));
				}else {
					assertThat("设置采集code_info表的所有列，但是出现了不符合期望的列，列名为 : " + tableColumn.getString(i, "column_name"), true, is(false));
				}
			}
			//断言data_extraction_def表中的内容是否符合期望
			long defCount = SqlOperator.queryNumber(db, "select count(1) from " + Data_extraction_def.TableName + " where table_id = ?", CODE_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			Result dataExtractionDef = SqlOperator.queryResult(db, "select * from " + Data_extraction_def.TableName + " where table_id = ?", tableId);
			assertThat("code_info表的数据抽取定义信息在Data_extraction_def表中有数据，数据有1条，并且用构造初始化数据是使用的CODE_INFO_TABLE_ID在data_extraction_def表中已经查不到数据了", defCount, is(0L));
			assertThat("code_info表的数据抽取定义信息在Data_extraction_def表中有数据，数据有1条，并且用构造初始化数据是使用的CODE_INFO_TABLE_ID在data_extraction_def表中已经查不到数据了", dataExtractionDef.getRowCount(), is(1));
			assertThat("code_info表的数据抽取定义信息在Data_extraction_def表中有数据，<数据抽取方式>符合预期", dataExtractionDef.getString(0, "data_extract_type"), is(DataExtractType.ShuJuKuChouQuLuoDi.getCode()));
			assertThat("code_info表的数据抽取定义信息在Data_extraction_def表中有数据，<是否表头>符合预期", dataExtractionDef.getString(0, "is_header"), is(IsFlag.Fou.getCode()));
			assertThat("code_info表的数据抽取定义信息在Data_extraction_def表中有数据，<落地文件编码>符合预期", dataExtractionDef.getString(0, "database_code"), is(DataBaseCode.UTF_8.getCode()));
			assertThat("code_info表的数据抽取定义信息在Data_extraction_def表中有数据，<落地文件格式>符合预期", dataExtractionDef.getString(0, "dbfile_format"), is(FileFormat.PARQUET.getCode()));
			assertThat("code_info表的数据抽取定义信息在Data_extraction_def表中有数据，<落地存储目录>符合预期", dataExtractionDef.getString(0, "plane_url"), is("/home/hyshf"));

			//断言table_clean表中的内容是否符合期望
			long cleanCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " where table_id = ?", CODE_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			Result tableClean = SqlOperator.queryResult(db, "select * from " + Table_clean.TableName + " where table_id = ?", tableId);
			assertThat("code_info表的表清洗信息在Table_clean表中有数据，数据有2条，并且用构造初始化数据是使用的CODE_INFO_TABLE_ID在Table_clean表中已经查不到数据了", cleanCount, is(0L));
			assertThat("code_info表的表清洗信息在Table_clean表中有数据，数据有2条，并且用构造初始化数据是使用的CODE_INFO_TABLE_ID在Table_clean表中已经查不到数据了", tableClean.getRowCount(), is(2));
			for(int i = 0; i < tableClean.getRowCount(); i++){
				if(tableClean.getString(i, "clean_type").equalsIgnoreCase(CleanType.ZiFuTiHuan.getCode())){
					assertThat("字符替换，原字符串符合预期", tableClean.getString(i, "field"), is("abc"));
					assertThat("字符替换，目标字符串符合预期", tableClean.getString(i, "replace_feild"), is("def"));
				}else if(tableClean.getString(i, "clean_type").equalsIgnoreCase(CleanType.ZiFuBuQi.getCode())){
					assertThat("字符补齐，补齐长度符合预期", tableClean.getInt(i, "filling_length"), is(6));
					assertThat("字符补齐，补齐字符串符合预期", tableClean.getString(i, "character_filling"), is("beyond"));
				}else{
					assertThat("修改成功后，code_info表在table_clean表中定义的表清洗方式出现了不符合预期的情况,清洗方式为 : " + tableClean.getString(i, "clean_type"), true, is(false));
				}
			}

			//断言table_srorage_info表中的内容是否符合期望
			long storageCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_storage_info.TableName + " where table_id = ?", CODE_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			Result tableStorage = SqlOperator.queryResult(db, "select * from " + Table_storage_info.TableName + " where table_id = ?", tableId);
			assertThat("code_info表的表存储信息在Table_storage_info表中有数据，数据有1条，并且用构造初始化数据是使用的CODE_INFO_TABLE_ID在Table_storage_info表中已经查不到数据了", storageCount, is(0L));
			assertThat("code_info表的表存储信息在Table_storage_info表中有数据，数据有1条，并且用构造初始化数据是使用的CODE_INFO_TABLE_ID在Table_storage_info表中已经查不到数据了", tableStorage.getRowCount(), is(1));
			assertThat("code_info表的表存储信息在Table_storage_info表中有数据，数据有1条，存储格式为<定长>", tableStorage.getString(0, "file_format"), is(FileFormat.DingChang.getCode()));
			assertThat("code_info表的表存储信息在Table_storage_info表中有数据，数据有1条，进数方式为<追加>", tableStorage.getString(0, "storage_type"), is(StorageType.ZhuiJia.getCode()));


			//断言column_merge表中的内容是否符合期望
			long mergeCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_merge.TableName + " where table_id = ?", CODE_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			Result columnMerge = SqlOperator.queryResult(db, "select * from " + Column_merge.TableName + " where table_id = ?", tableId);
			assertThat("code_info表的列合并信息在column_merge表中有数据，数据有1条，并且用构造初始化数据是使用的CODE_INFO_TABLE_ID在column_merge表中已经查不到数据了", mergeCount, is(0L));
			assertThat("code_info表的列合并信息在column_merge表中有数据，数据有1条，并且用构造初始化数据是使用的CODE_INFO_TABLE_ID在column_merge表中已经查不到数据了", columnMerge.getRowCount(), is(1));
			assertThat("ode_info表的列合并信息在column_merge表中有数据，数据有1条,<要合并的字段>符合预期", columnMerge.getString(0, "col_name"), is("ci_sp_classname_name"));
			assertThat("ode_info表的列合并信息在column_merge表中有数据，数据有1条,<要合并的字段>符合预期", columnMerge.getString(0, "old_name"), is("ci_sp_classname|ci_sp_name"));

			//删除测试数据
			SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ? ", tableId);
			SqlOperator.execute(db, "delete from " + Table_storage_info.TableName + " where table_id = ? ", tableId);
			SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? ", tableId);
			SqlOperator.execute(db, "delete from " + Column_merge.TableName + " where table_id = ? ", tableId);
			SqlOperator.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ? ", tableId);

			SqlOperator.commitTransaction(db);
		}
	}

	/*
	* 正确数据访问4：在database_id为1001的数据库采集任务下构造新增采集ftp_collect表和object_collect表的数据，
	* 两张表都不选择采集列和列排序，都不设置并行抽取(需要和agent交互)
	* */
	@Test
	public void saveCollTbInfoFour(){
		List<Table_info> tableInfos = new ArrayList<>();
		List<CollTbConfParam> tbConfParams = new ArrayList<>();

		try(DatabaseWrapper db = new DatabaseWrapper()){
			//在新增前，查询数据库，table_info表中应该没有采集ftp_collect表的信息
			long count = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " where table_name = ?", "ftp_collect").orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在新增前，查询数据库，table_info表中应该没有采集ftp_collect表的信息", count, is(0L));
			//在新增前，查询数据库，table_info表中应该没有采集object_collect表的信息
			long countTwo = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " where table_name = ?", "object_collect").orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在新增前，查询数据库，table_info表中应该没有采集object_collect表的信息", countTwo, is(0L));
		}

		Table_info FTPInfo = new Table_info();
		FTPInfo.setTable_name("ftp_collect");
		FTPInfo.setTable_ch_name("ftp采集设置表");
		FTPInfo.setDatabase_id(FIRST_DATABASESET_ID);
		FTPInfo.setIs_parallel(IsFlag.Fou.getCode());

		Table_info objInfo = new Table_info();
		objInfo.setTable_name("object_collect");
		objInfo.setTable_ch_name("半结构化文件采集设置表");
		objInfo.setDatabase_id(FIRST_DATABASESET_ID);
		objInfo.setIs_parallel(IsFlag.Fou.getCode());

		tableInfos.add(FTPInfo);
		tableInfos.add(objInfo);

		CollTbConfParam FTPParam = new CollTbConfParam();
		FTPParam.setCollColumnString("");

		CollTbConfParam objParam = new CollTbConfParam();
		objParam.setCollColumnString("");

		tbConfParams.add(FTPParam);
		tbConfParams.add(objParam);

		String rightStringFour = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult rightResultFour = JsonUtil.toObjectSafety(rightStringFour, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultFour.isSuccess(), is(true));
		Integer returnValue = (Integer) rightResultFour.getData();
		assertThat(returnValue == FIRST_DATABASESET_ID, is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			//新增成功后，断言数据库中的数据是否符合期望，如果符合期望，则删除本次新增带来的数据
			Result afterTableInfo = SqlOperator.queryResult(db, "select * from " + Table_info.TableName + " where table_name = ?", "ftp_collect");
			assertThat("<正确的测试用例4>执行成功后，table_info表中出现了采集ftp_collect表的配置", afterTableInfo.getRowCount(), is(1));
			assertThat("<正确的测试用例4>执行成功后，采集ftp_collect表配置，<清洗顺序>符合期望", afterTableInfo.getString(0, "ti_or"), is(tableCleanOrder.toJSONString()));
			assertThat("<正确的测试用例4>执行成功后，采集ftp_collect表配置，<是否使用MD5>符合期望", afterTableInfo.getString(0, "is_md5"), is(IsFlag.Shi.getCode()));
			assertThat("<正确的测试用例4>执行成功后，采集ftp_collect表配置，<是否仅登记>符合期望", afterTableInfo.getString(0, "is_register"), is(IsFlag.Fou.getCode()));
			assertThat("<正确的测试用例4>执行成功后，采集ftp_collect表配置，<是否并行抽取>符合期望", afterTableInfo.getString(0, "is_parallel"), is(IsFlag.Fou.getCode()));

			Result afterTableColumn = SqlOperator.queryResult(db, "select * from " + Table_column.TableName + " where table_id = ?", afterTableInfo.getLong(0, "table_id"));
			assertThat("<正确的测试用例4>执行成功后，table_column表中有关ftp_collect表的列应该有<24>列", afterTableColumn.getRowCount(), is(24));
			for(int i = 0; i < afterTableColumn.getRowCount(); i++){
				if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_id")){
					assertThat("<正确的测试用例4>执行成功后, <ftp_id>字段的类型为<int8>", afterTableColumn.getString(i, "column_type"), is("int8"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_number")){
					assertThat("<正确的测试用例4>执行成功后, <ftp_number>字段的类型为<varchar(200)>", afterTableColumn.getString(i, "column_type"), is("varchar(200)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_name")){
					assertThat("<正确的测试用例4>执行成功后, <ftp_name>字段的类型为<varchar(512)>", afterTableColumn.getString(i, "column_type"), is("varchar(512)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("start_date")){
					assertThat("<正确的测试用例4>执行成功后, <start_date>字段的类型为<bpchar(8)>", afterTableColumn.getString(i, "column_type"), is("bpchar(8)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("end_date")){
					assertThat("<正确的测试用例4>执行成功后, <end_date>字段的类型为<bpchar(8)>", afterTableColumn.getString(i, "column_type"), is("bpchar(8)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_ip")){
					assertThat("<正确的测试用例4>执行成功后, <ftp_ip>字段的类型为<varchar(50)>", afterTableColumn.getString(i, "column_type"), is("varchar(50)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_port")){
					assertThat("<正确的测试用例4>执行成功后, <ftp_port>字段的类型为<varchar(10)>", afterTableColumn.getString(i, "column_type"), is("varchar(10)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_username")){
					assertThat("<正确的测试用例4>执行成功后, <ftp_username>字段的类型为<varchar(512)>", afterTableColumn.getString(i, "column_type"), is("varchar(512)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_password")){
					assertThat("<正确的测试用例4>执行成功后, <ftp_password>字段的类型为<varchar(100)>", afterTableColumn.getString(i, "column_type"), is("varchar(100)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_dir")){
					assertThat("<正确的测试用例4>执行成功后, <ftp_dir>字段的类型为<varchar(512)>", afterTableColumn.getString(i, "column_type"), is("varchar(512)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("local_path")){
					assertThat("<正确的测试用例4>执行成功后, <local_path>字段的类型为<varchar(512)>", afterTableColumn.getString(i, "column_type"), is("varchar(512)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_rule_path")){
					assertThat("<正确的测试用例4>执行成功后, <ftp_rule_path>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("child_file_path")){
					assertThat("<正确的测试用例4>执行成功后, <child_file_path>字段的类型为<varchar(512)>", afterTableColumn.getString(i, "column_type"), is("varchar(512)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("child_time")){
					assertThat("<正确的测试用例4>执行成功后, <child_time>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("file_suffix")){
					assertThat("<正确的测试用例4>执行成功后, <file_suffix>字段的类型为<varchar(200)>", afterTableColumn.getString(i, "column_type"), is("varchar(200)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("ftp_model")){
					assertThat("<正确的测试用例4>执行成功后, <ftp_model>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("run_way")){
					assertThat("<正确的测试用例4>执行成功后, <run_way>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("remark")){
					assertThat("<正确的测试用例4>执行成功后, <remark>字段的类型为<varchar(512)>", afterTableColumn.getString(i, "column_type"), is("varchar(512)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("is_sendok")){
					assertThat("<正确的测试用例4>执行成功后, <is_sendok>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("is_unzip")){
					assertThat("<正确的测试用例4>执行成功后, <is_unzip>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("reduce_type")){
					assertThat("<正确的测试用例4>执行成功后, <reduce_type>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("is_read_realtime")){
					assertThat("<正确的测试用例4>执行成功后, <is_read_realtime>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("realtime_interval")){
					assertThat("<正确的测试用例4>执行成功后, <realtime_interval>字段的类型为<int8>", afterTableColumn.getString(i, "column_type"), is("int8"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("agent_id")){
					assertThat("<正确的测试用例4>执行成功后, <agent_id>字段的类型为<int8>", afterTableColumn.getString(i, "column_type"), is("int8"));
				}else{
					assertThat("<正确的测试用例4>执行完成之后，采集ftp_collect表的所有字段，出现了不符合期望的字段，字段名为 : " + afterTableColumn.getString(i, "column_name"), true, is(false));
				}
			}

			//以上断言都测试成功后，删除<正确数据访问4>生成的数据
			int tableCount = SqlOperator.execute(db, "delete from " + Table_info.TableName + " where table_id = ?", afterTableInfo.getLong(0, "table_id"));
			int columnCount = SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ?", afterTableInfo.getLong(0, "table_id"));
			assertThat("删除<正确数据访问4>生成的数据成功<table_info>", tableCount, is(1));
			assertThat("删除<正确数据访问4>生成的数据成功<table_column>", columnCount, is(24));

			SqlOperator.commitTransaction(db);
		}

		try(DatabaseWrapper db = new DatabaseWrapper()){
			//新增成功后，断言数据库中的数据是否符合期望，如果符合期望，则删除本次新增带来的数据
			Result afterTableInfo = SqlOperator.queryResult(db, "select * from " + Table_info.TableName + " where table_name = ?", "object_collect");
			assertThat("<正确的测试用例4>执行成功后，table_info表中出现了采集object_collect表的配置", afterTableInfo.getRowCount(), is(1));
			assertThat("<正确的测试用例4>执行成功后，采集object_collect表配置，<清洗顺序>符合期望", afterTableInfo.getString(0, "ti_or"), is(tableCleanOrder.toJSONString()));
			assertThat("<正确的测试用例4>执行成功后，采集object_collect表配置，<是否使用MD5>符合期望", afterTableInfo.getString(0, "is_md5"), is(IsFlag.Shi.getCode()));
			assertThat("<正确的测试用例4>执行成功后，采集object_collect表配置，<是否仅登记>符合期望", afterTableInfo.getString(0, "is_register"), is(IsFlag.Fou.getCode()));
			assertThat("<正确的测试用例4>执行成功后，采集object_collect表配置，<是否并行抽取>符合期望", afterTableInfo.getString(0, "is_parallel"), is(IsFlag.Fou.getCode()));

			Result afterTableColumn = SqlOperator.queryResult(db, "select * from " + Table_column.TableName + " where table_id = ?", afterTableInfo.getLong(0, "table_id"));
			assertThat("<正确的测试用例4>执行成功后，table_column表中有关object_collect表的列应该有<16>列", afterTableColumn.getRowCount(), is(16));
			for(int i = 0; i < afterTableColumn.getRowCount(); i++){
				if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("odc_id")){
					assertThat("<正确的测试用例4>执行成功后, <odc_id>字段的类型为<int8>", afterTableColumn.getString(i, "column_type"), is("int8"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("object_collect_type")){
					assertThat("<正确的测试用例4>执行成功后, <object_collect_type>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("obj_number")){
					assertThat("<正确的测试用例4>执行成功后, <obj_number>字段的类型为<varchar(200)>", afterTableColumn.getString(i, "column_type"), is("varchar(200)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("obj_collect_name")){
					assertThat("<正确的测试用例4>执行成功后, <obj_collect_name>字段的类型为<varchar(512)>", afterTableColumn.getString(i, "column_type"), is("varchar(512)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("system_name")){
					assertThat("<正确的测试用例4>执行成功后, <system_name>字段的类型为<varchar(512)>", afterTableColumn.getString(i, "column_type"), is("varchar(512)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("host_name")){
					assertThat("<正确的测试用例4>执行成功后, <host_name>字段的类型为<varchar(512)>", afterTableColumn.getString(i, "column_type"), is("varchar(512)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("local_time")){
					assertThat("<正确的测试用例4>执行成功后, <local_time>字段的类型为<bpchar(20)>", afterTableColumn.getString(i, "column_type"), is("bpchar(20)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("server_date")){
					assertThat("<正确的测试用例4>执行成功后, <server_date>字段的类型为<bpchar(20)>", afterTableColumn.getString(i, "column_type"), is("bpchar(20)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("s_date")){
					assertThat("<正确的测试用例4>执行成功后, <s_date>字段的类型为<bpchar(8)>", afterTableColumn.getString(i, "column_type"), is("bpchar(8)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("e_date")){
					assertThat("<正确的测试用例4>执行成功后, <e_date>字段的类型为<bpchar(8)>", afterTableColumn.getString(i, "column_type"), is("bpchar(8)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("database_code")){
					assertThat("<正确的测试用例4>执行成功后, <database_code>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("run_way")){
					assertThat("<正确的测试用例4>执行成功后, <run_way>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("file_path")){
					assertThat("<正确的测试用例4>执行成功后, <file_path>字段的类型为<varchar(512)>", afterTableColumn.getString(i, "column_type"), is("varchar(512)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("is_sendok")){
					assertThat("<正确的测试用例4>执行成功后, <is_sendok>字段的类型为<bpchar(1)>", afterTableColumn.getString(i, "column_type"), is("bpchar(1)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("remark")){
					assertThat("<正确的测试用例4>执行成功后, <remark>字段的类型为<varchar(512)>", afterTableColumn.getString(i, "column_type"), is("varchar(512)"));
				}else if(afterTableColumn.getString(i, "column_name").equalsIgnoreCase("agent_id")){
					assertThat("<正确的测试用例4>执行成功后, <agent_id>字段的类型为<int8>", afterTableColumn.getString(i, "column_type"), is("int8"));
				}else{
					assertThat("<正确的测试用例4>执行成功后, 再次查询table_column表，出现了不符合期望的情况，表名为：" + afterTableColumn.getString(i, "column_name"), true, is(false));
				}
			}

			//以上断言都测试成功后，删除<正确数据访问4>生成的数据
			int tableCount = SqlOperator.execute(db, "delete from " + Table_info.TableName + " where table_id = ?", afterTableInfo.getLong(0, "table_id"));
			int columnCount = SqlOperator.execute(db, "delete from " + Table_column.TableName + " where table_id = ?", afterTableInfo.getLong(0, "table_id"));
			assertThat("删除<正确数据访问4>生成的数据成功<table_info>", tableCount, is(1));
			assertThat("删除<正确数据访问4>生成的数据成功<table_column>", columnCount, is(16));

			SqlOperator.commitTransaction(db);
		}
	}

	/*
	 * 正确数据访问5：在database_id为1001的数据库采集任务中，模拟取消所有采集表的情况
	 * */
	@Test
	public void saveCollTbInfoFive(){
		List<Table_info> tableInfos = new ArrayList<>();

		Table_info tableInfo = new Table_info();
		tableInfo.setTable_id(SYS_USER_TABLE_ID);

		Table_info tableInfoTwo = new Table_info();
		tableInfoTwo.setTable_id(CODE_INFO_TABLE_ID);

		tableInfos.add(tableInfo);
		tableInfos.add(tableInfoTwo);

		String rightStringFive = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("delTbString", JSON.toJSONString(tableInfos))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult rightResultFive = JsonUtil.toObjectSafety(rightStringFive, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultFive.isSuccess(), is(true));
		Integer returnValue = (Integer) rightResultFive.getData();
		assertThat(returnValue == FIRST_DATABASESET_ID, is(true));

		//断言由于取消所有已经存在的采集表，脏数据是否被删除了
		try(DatabaseWrapper db = new DatabaseWrapper()){
			//断言table_info表是否还存在脏数据
			long countOne = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " where table_id = ?", SYS_USER_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<sys_user表>在table_info表中已经不存在数据了", countOne, is(0L));
			long countTwo = SqlOperator.queryNumber(db, "select count(1) from " + Table_info.TableName + " where table_id = ?", CODE_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<code_info表>在table_info表中已经不存在数据了", countTwo, is(0L));
			//断言table_column表是否还存在脏数据
			long countThree = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where table_id = ?", SYS_USER_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<sys_user表>在table_column表中已经不存在数据了", countThree, is(0L));
			long countFour = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where table_id = ?", CODE_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<code_info表>在table_column表中已经不存在数据了", countFour, is(0L));
			//断言table_storage_info表是否还存在脏数据，并且数据存储关系也应该相应的删除
			long countFive = SqlOperator.queryNumber(db, "select count(1) from " + Table_storage_info.TableName + " where table_id = ?", SYS_USER_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<sys_user表>在table_storage_info表中已经不存在数据了", countFive, is(0L));
			long countSix = SqlOperator.queryNumber(db, "select count(1) from " + Table_storage_info.TableName + " where table_id = ?", CODE_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<code_info表>在table_storage_info表中已经不存在数据了", countSix, is(0L));
			long countThi = SqlOperator.queryNumber(db, "select count(1) from " + Data_relation_table.TableName + " where storage_id = ( select storage_id from " + Table_storage_info.TableName + " where table_id = ?)", CODE_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<code_info表>的数据存储关系已经不存在数据了", countThi, is(0L));
			long countFou = SqlOperator.queryNumber(db, "select count(1) from " + Data_relation_table.TableName + " where storage_id = ( select storage_id from " + Table_storage_info.TableName + " where table_id = ?)", SYS_USER_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<sys_user表>的数据存储关系已经不存在数据了", countFou, is(0L));
			//断言table_clean表是否还存在脏数据
			long countSeven = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " where table_id = ?", SYS_USER_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<sys_user表>在table_clean表中已经不存在数据了", countSeven, is(0L));
			long countEight = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " where table_id = ?", CODE_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<code_info表>在table_clean表中已经不存在数据了", countEight, is(0L));
			//断言column_merge表是否还存在脏数据
			long countNine = SqlOperator.queryNumber(db, "select count(1) from " + Column_merge.TableName + " where table_id = ?", SYS_USER_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<sys_user表>在column_merge表中已经不存在数据了", countNine, is(0L));
			long countTen = SqlOperator.queryNumber(db, "select count(1) from " + Column_merge.TableName + " where table_id = ?", CODE_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<code_info表>在column_merge表中已经不存在数据了", countTen, is(0L));
			//断言data_extraction_def表是否还存在脏数据
			long countEle = SqlOperator.queryNumber(db, "select count(1) from " + Data_extraction_def.TableName + " where table_id = ?", SYS_USER_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<sys_user表>在column_merge表中已经不存在数据了", countEle, is(0L));
			long countTwe = SqlOperator.queryNumber(db, "select count(1) from " + Data_extraction_def.TableName + " where table_id = ?", CODE_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<code_info表>在column_merge表中已经不存在数据了", countTwe, is(0L));
			//断言column_clean表中是否还存在脏数据
			long countFif = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ?", 2008).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<sys_user表中的字段>在column_clean表中已经不存在数据了", countFif, is(0L));
			long countSixt = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ?", 3001).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<code_info表中的字段>在column_clean表中已经不存在数据了", countSixt, is(0L));
			//断言column_storage_info表中是否还存在脏数据
			long countSet = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ?", 2001).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<sys_user表中的字段>在column_storage_info表中已经不存在数据了", countSet, is(0L));
		}
	}

	/*
	 * 错误的数据访问1：构造缺少表名的采集数据
	 * 错误的数据访问2：构造缺少表中文名的采集数据
	 * 错误的数据访问3：构造设置了并行抽取，但没有设置并行抽取SQL的访问方式
	 * 错误的数据访问4：构造在不存在的数据库采集任务中保存采集ftp_collect表数据
	 * 错误的数据访问5：构造tableInfoString参数是空字符串的情况
	 * 错误的数据访问6：构造collTbConfParamString参数是空字符串的情况
	 * 错误的数据访问7：构造tableInfoString和collTbConfParamString解析成的list集合大小不同的情况
	 * 错误的数据访问8：构造设置了并行抽取，但没有设置并行抽取数的访问方式
	 * 错误的数据访问9：构造设置了并行抽取，但没有设置每日数据增量的访问方式
	 * 错误的数据访问10：构造设置了并行抽取，但没有设置数据总量的访问方式
	 * 错误的数据访问11：新增采集表，选择采集字段，但是缺少字段名
	 * 错误的数据访问12：新增采集表，选择采集字段，但是缺少字段类型
	 * 错误的数据访问13：新增采集表，选择采集字段，但是缺少是否采集标识位
	 * 错误的数据访问14：新增采集表，选择采集字段，但是缺少是否主键标识位
	 * 错误的数据访问15：新增采集表，选择采集字段，但是是否主键标识位取值错误
	 * 错误的数据访问16：新增采集表，选择采集字段，但是是否采集标识位取值错误
	 * */
	@Test
	public void saveCollTbInfoSix(){
		List<Table_info> tableInfos = new ArrayList<>();
		List<CollTbConfParam> tbConfParams = new ArrayList<>();
		//错误的数据访问1：构造缺少表名的采集数据
		Table_info FTPInfo = new Table_info();
		FTPInfo.setTable_ch_name("ftp采集设置表");
		FTPInfo.setDatabase_id(FIRST_DATABASESET_ID);
		FTPInfo.setIs_parallel(IsFlag.Fou.getCode());

		tableInfos.add(FTPInfo);

		CollTbConfParam FTPParam = new CollTbConfParam();
		FTPParam.setCollColumnString("");

		tbConfParams.add(FTPParam);

		String wrongStringOne = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));

		tableInfos.clear();
		tbConfParams.clear();

		//错误的数据访问2：构造缺少表中文名的采集数据
		Table_info FTPInfoTwo = new Table_info();
		FTPInfoTwo.setTable_name("ftp_collect");
		FTPInfoTwo.setDatabase_id(FIRST_DATABASESET_ID);
		FTPInfoTwo.setIs_parallel(IsFlag.Fou.getCode());

		tableInfos.add(FTPInfoTwo);

		CollTbConfParam FTPParamTwo = new CollTbConfParam();
		FTPParamTwo.setCollColumnString("");

		tbConfParams.add(FTPParamTwo);

		String wrongStringTwo = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));

		tableInfos.clear();
		tbConfParams.clear();

		//错误的数据访问3：构造设置了并行抽取，但没有设置并行抽取SQL的访问方式
		Table_info FTPInfoThree = new Table_info();
		FTPInfoThree.setTable_name("ftp_collect");
		FTPInfoThree.setTable_ch_name("ftp采集设置表");
		FTPInfoThree.setDatabase_id(FIRST_DATABASESET_ID);
		FTPInfoThree.setIs_parallel(IsFlag.Shi.getCode());

		tableInfos.add(FTPInfoThree);

		CollTbConfParam FTPParamThree = new CollTbConfParam();
		FTPParamThree.setCollColumnString("");

		tbConfParams.add(FTPParamThree);

		String wrongStringThree = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult wrongResultThree = JsonUtil.toObjectSafety(wrongStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultThree.isSuccess(), is(false));

		tableInfos.clear();
		tbConfParams.clear();

		//错误的数据访问4：构造在不存在的数据库采集任务中保存采集ftp_collect表数据
		Table_info FTPInfoFour = new Table_info();
		FTPInfoFour.setTable_name("ftp_collect");
		FTPInfoFour.setTable_ch_name("ftp采集设置表");
		FTPInfoFour.setDatabase_id(UNEXPECTED_ID);
		FTPInfoFour.setIs_parallel(IsFlag.Fou.getCode());

		tableInfos.add(FTPInfoFour);

		CollTbConfParam FTPParamFour = new CollTbConfParam();
		FTPParamFour.setCollColumnString("");

		tbConfParams.add(FTPParamFour);

		String wrongStringFour = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", UNEXPECTED_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult wrongResultFour = JsonUtil.toObjectSafety(wrongStringFour, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultFour.isSuccess(), is(false));

		tableInfos.clear();
		FTPInfoFour.setDatabase_id(FIRST_DATABASESET_ID);
		tableInfos.add(FTPInfoFour);
		tbConfParams.clear();

		//错误的数据访问5：构造tableInfoString参数是空字符串的情况
		String wrongStringFive = new HttpClient()
				.addData("tableInfoString", "")
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult wrongResultFive = JsonUtil.toObjectSafety(wrongStringFive, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultFive.isSuccess(), is(false));

		//错误的数据访问6：构造collTbConfParamString参数是空字符串的情况
		String wrongStringSix = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", "")
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult wrongResultSix = JsonUtil.toObjectSafety(wrongStringSix, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultSix.isSuccess(), is(false));

		tableInfos.clear();
		tbConfParams.clear();

		//错误的数据访问7：构造tableInfoString和collTbConfParamString解析成的list集合大小不同的情况
		Table_info FTPInfoSeven = new Table_info();
		FTPInfoSeven.setTable_name("ftp_collect");
		FTPInfoSeven.setTable_ch_name("ftp采集设置表");
		FTPInfoSeven.setDatabase_id(FIRST_DATABASESET_ID);
		FTPInfoSeven.setIs_parallel(IsFlag.Fou.getCode());

		Table_info objInfoSeven = new Table_info();
		objInfoSeven.setTable_name("object_collect");
		objInfoSeven.setTable_ch_name("半结构化文件采集设置表");
		objInfoSeven.setDatabase_id(FIRST_DATABASESET_ID);
		objInfoSeven.setIs_parallel(IsFlag.Fou.getCode());

		tableInfos.add(FTPInfoSeven);
		tableInfos.add(objInfoSeven);

		CollTbConfParam FTPParamSeven = new CollTbConfParam();
		FTPParamSeven.setCollColumnString("");

		tbConfParams.add(FTPParamSeven);

		String wrongStringSeven = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult wrongResultSeven = JsonUtil.toObjectSafety(wrongStringSeven, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultSeven.isSuccess(), is(false));

		tableInfos.clear();
		tbConfParams.clear();

		//错误的数据访问8：构造设置了并行抽取，但没有设置并行抽取数的访问方式
		Table_info FTPInfoEight = new Table_info();
		FTPInfoEight.setTable_name("ftp_collect");
		FTPInfoEight.setTable_ch_name("ftp采集设置表");
		FTPInfoEight.setDatabase_id(FIRST_DATABASESET_ID);
		FTPInfoEight.setIs_parallel(IsFlag.Shi.getCode());
		FTPInfoEight.setPage_sql("select * from ftp_collect limit 1");
		FTPInfoEight.setTable_count("100000000");
		FTPInfoEight.setDataincrement(10);

		tableInfos.add(FTPInfoEight);

		CollTbConfParam FTPParamEight = new CollTbConfParam();
		FTPParamEight.setCollColumnString("");

		tbConfParams.add(FTPParamEight);

		String wrongStringEight = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult wrongResultEight = JsonUtil.toObjectSafety(wrongStringEight, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultEight.isSuccess(), is(false));

		tableInfos.clear();
		tbConfParams.clear();

		//错误的数据访问9：构造设置了并行抽取，但没有设置每日数据增量的访问方式
		Table_info FTPInfoNine = new Table_info();
		FTPInfoNine.setTable_name("ftp_collect");
		FTPInfoNine.setTable_ch_name("ftp采集设置表");
		FTPInfoNine.setDatabase_id(FIRST_DATABASESET_ID);
		FTPInfoNine.setIs_parallel(IsFlag.Shi.getCode());
		FTPInfoNine.setPage_sql("select * from ftp_collect limit 1");
		FTPInfoNine.setTable_count("100000000");
		FTPInfoNine.setPageparallels(6);

		tableInfos.add(FTPInfoNine);

		CollTbConfParam FTPParamNine = new CollTbConfParam();
		FTPParamNine.setCollColumnString("");

		tbConfParams.add(FTPParamNine);

		String wrongStringNine = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult wrongResultNine = JsonUtil.toObjectSafety(wrongStringNine, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultNine.isSuccess(), is(false));

		tableInfos.clear();
		tbConfParams.clear();

		//错误的数据访问10：构造设置了并行抽取，但没有设置数据总量的访问方式
		Table_info FTPInfoTen = new Table_info();
		FTPInfoTen.setTable_name("ftp_collect");
		FTPInfoTen.setTable_ch_name("ftp采集设置表");
		FTPInfoTen.setDatabase_id(FIRST_DATABASESET_ID);
		FTPInfoTen.setIs_parallel(IsFlag.Shi.getCode());
		FTPInfoTen.setPage_sql("select * from ftp_collect limit 1");
		FTPInfoTen.setTable_count("100000000");
		FTPInfoTen.setPageparallels(6);

		tableInfos.add(FTPInfoTen);

		CollTbConfParam FTPParamTen = new CollTbConfParam();
		FTPParamTen.setCollColumnString("");

		tbConfParams.add(FTPParamTen);

		String wrongStringTen = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult wrongResultTen = JsonUtil.toObjectSafety(wrongStringTen, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTen.isSuccess(), is(false));

		tableInfos.clear();
		tbConfParams.clear();

		//错误的数据访问11：新增采集表，选择采集字段，但是缺少字段名
		Table_info objInfo = new Table_info();
		objInfo.setTable_name("object_collect");
		objInfo.setTable_ch_name("半结构化文件采集设置表");
		objInfo.setDatabase_id(FIRST_DATABASESET_ID);
		objInfo.setIs_parallel(IsFlag.Fou.getCode());

		tableInfos.add(objInfo);

		List<Table_column> objColumn = new ArrayList<>();
		for(int i = 0; i < 3; i++){
			String primayKeyFlag;
			String columnName;
			String columnChName;
			String columnType;
			switch (i) {
				case 0 :
					primayKeyFlag = IsFlag.Shi.getCode();
					columnName = "odc_id";
					columnChName = "对象采集id";
					columnType = "int8";
					break;
				case 1 :
					primayKeyFlag = IsFlag.Fou.getCode();
					columnName = "object_collect_type";
					columnChName = "对象采集方式";
					columnType = "bpchar(1)";
					break;
				case 2 :
					primayKeyFlag = IsFlag.Fou.getCode();
					columnName = "obj_number";
					columnChName = "对象采集设置编号";
					columnType = "varchar(200)";
					break;
				default:
					primayKeyFlag = "unexpected_primayKeyFlag";
					columnName = "unexpected_columnName";
					columnChName = "unexpected_columnChName";
					columnType = "unexpected_columnType";
			}
			Table_column tableColumn = new Table_column();
			tableColumn.setColumn_name(columnName);
			tableColumn.setColumn_ch_name(columnChName);
			tableColumn.setColumn_type(columnType);
			tableColumn.setIs_get(IsFlag.Shi.getCode());
			tableColumn.setIs_primary_key(primayKeyFlag);

			objColumn.add(tableColumn);
		}

		CollTbConfParam objParam = new CollTbConfParam();
		//缺少采集字段名
		objColumn.get(0).setColumn_name("");
		objParam.setCollColumnString(JSON.toJSONString(objColumn));

		tbConfParams.add(objParam);

		String wrongStringEle = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult wrongResultEle = JsonUtil.toObjectSafety(wrongStringEle, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultEle.isSuccess(), is(false));

		//错误的数据访问12：新增采集表，选择采集字段，但是缺少字段类型
		objColumn.get(0).setColumn_name("odc_id");
		objColumn.get(0).setColumn_type("");

		String wrongStringTwe = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult wrongResultTwe = JsonUtil.toObjectSafety(wrongStringTwe, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwe.isSuccess(), is(false));

		//错误的数据访问13：新增采集表，选择采集字段，但是缺少是否采集标识位
		objColumn.get(0).setColumn_type("int8");
		objColumn.get(0).setIs_get("");

		String wrongStringThi = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult wrongResultThi = JsonUtil.toObjectSafety(wrongStringThi, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultThi.isSuccess(), is(false));

		//错误的数据访问14：新增采集表，选择采集字段，但是缺少是否主键标识位
		objColumn.get(0).setIs_get(IsFlag.Shi.getCode());
		objColumn.get(0).setIs_primary_key("");

		String wrongStringFou = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult wrongResultFou = JsonUtil.toObjectSafety(wrongStringFou, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultFou.isSuccess(), is(false));

		//错误的数据访问15：新增采集表，选择采集字段，但是是否主键标识位取值错误
		objColumn.get(0).setIs_primary_key("3");
		String wrongStringFif = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult wrongResultFif = JsonUtil.toObjectSafety(wrongStringFif, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultFif.isSuccess(), is(false));

		//错误的数据访问16：新增采集表，选择采集字段，但是是否采集标识位取值错误
		objColumn.get(0).setIs_primary_key(IsFlag.Shi.getCode());
		objColumn.get(0).setIs_get("3");
		String wrongStringSixt = new HttpClient()
				.addData("tableInfoString", JSON.toJSONString(tableInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("collTbConfParamString", JSON.toJSONString(tbConfParams))
				.post(getActionUrl("saveCollTbInfo")).getBodyString();
		ActionResult wrongResultSixt = JsonUtil.toObjectSafety(wrongStringSixt, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultSixt.isSuccess(), is(false));
	}

	/*
	 * 测试根据数据库设置ID获取对采集表配置的SQL过滤，分页SQL
	 *
	 * 正确的数据访问1：传入正确的数据库设置ID，获取数据并断言数据是否正确
	 * 错误的数据访问1：传入错误的数据库设置ID，应该导致访问失败
	 *
	 * */
	@Test
	public void getSQLInfoByColSetId(){
		//正确的数据访问1：传入正确的数据库设置ID，获取数据并断言数据是否正确
		String rightStringOne = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getSQLInfoByColSetId")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Result rightData = rightResultOne.getDataForResult();
		assertThat("获取到了两条数据", rightData.getRowCount(), is(2));
		for(int i = 0; i < rightData.getRowCount(); i++){
			if(rightData.getLong(i, "table_id") == SYS_USER_TABLE_ID){
				assertThat("sys_user表定义了过滤SQL，过滤SQL为<select * from sys_user where user_id = 9997>", rightData.getString(i, "sql"), is("select * from sys_user where user_id = 9997"));
				assertThat("sys_user表没有定义分页抽取SQL", rightData.getString(i ,"is_parallel"), is(IsFlag.Fou.getCode()));
				assertThat("sys_user表没有定义分页抽取SQL", rightData.getString(i ,"page_sql"), is(""));
			}else if(rightData.getLong(i, "table_id") == CODE_INFO_TABLE_ID){
				assertThat("code_info表没有设置过滤SQL", rightData.getString(i, "sql"), is(""));
				assertThat("code_info表设置了并行抽取，并且分页抽取SQL为<select * from code_info limit 10>", rightData.getString(i, "is_parallel"), is(IsFlag.Shi.getCode()));
				assertThat("code_info表设置了并行抽取，并且分页抽取SQL为<select * from code_info limit 10>", rightData.getString(i, "page_sql"), is("select * from code_info limit 10"));
				assertThat("code_info表设置了并行抽取，并且数据量为100000", rightData.getString(i, "table_count"), is("100000"));
				assertThat("code_info表设置了并行抽取，并且并行采集线程数为6", rightData.getInt(i, "pageparallels"), is(6));
				assertThat("code_info表设置了并行抽取，并且每日数据增量为1000条", rightData.getInt(i, "dataincrement"), is(1000));
			}else{
				assertThat("获取到了不符合期望的数据,table_id为：" + rightData.getLong(i, "table_id"), true, is(false));
			}
		}

		//错误的数据访问1：传入错误的数据库设置ID，应该导致访问失败
		String wrongStringOne = new HttpClient()
				.addData("colSetId", UNEXPECTED_ID)
				.post(getActionUrl("getSQLInfoByColSetId")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));
	}

	/*
	 * 测试根据数据库设置ID获取表的字段信息
	 *
	 * 正确的数据访问1：传入正确的数据库设置ID，获取数据并断言数据是否正确
	 * 错误的数据访问1：传入错误的数据库设置ID，应该导致访问失败
	 *
	 * */
	@Test
	public void getColumnInfoByColSetId(){
		//正确的数据访问1：传入正确的数据库设置ID，获取数据并断言数据是否正确
		String rightStringOne = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getColumnInfoByColSetId")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Map<String, List> dataForMap = rightResultOne.getDataForMap(String.class, List.class);
		for(String key : dataForMap.keySet()){
			if(key.equalsIgnoreCase("sys_user")){
				List list = dataForMap.get(key);
				Result result = new Result((List<Map<String, Object>>)list);
				assertThat("采集sys_user的10个列", result.getRowCount(), is(10));
				for(int i = 0; i < result.getRowCount(); i++){
					if(result.getString(i, "column_name").equalsIgnoreCase("user_id")){
						assertThat("<user_id>列中文名为<主键>", result.getString(i, "column_ch_name"), is("主键"));
					}else if(result.getString(i, "column_name").equalsIgnoreCase("create_id")){
						assertThat("<create_id>列中文名为<创建用户者ID>", result.getString(i, "column_ch_name"), is("创建用户者ID"));
					}else if(result.getString(i, "column_name").equalsIgnoreCase("dep_id")){
						assertThat("<dep_id>列中文名为<部门ID>", result.getString(i, "column_ch_name"), is("部门ID"));
					}else if(result.getString(i, "column_name").equalsIgnoreCase("role_id")){
						assertThat("<role_id>列中文名为<角色ID>", result.getString(i, "column_ch_name"), is("角色ID"));
					}else if(result.getString(i, "column_name").equalsIgnoreCase("user_name")){
						assertThat("<user_name>列中文名为<用户名>", result.getString(i, "column_ch_name"), is("用户名"));
					}else if(result.getString(i, "column_name").equalsIgnoreCase("user_password")){
						assertThat("<user_password>列中文名为<密码>", result.getString(i, "column_ch_name"), is("密码"));
					}else if(result.getString(i, "column_name").equalsIgnoreCase("user_email")){
						assertThat("<user_email>列中文名为<邮箱>", result.getString(i, "column_ch_name"), is("邮箱"));
					}else if(result.getString(i, "column_name").equalsIgnoreCase("user_mobile")){
						assertThat("<user_mobile>列中文名为<电话>", result.getString(i, "column_ch_name"), is("电话"));
					}else if(result.getString(i, "column_name").equalsIgnoreCase("useris_admin")){
						assertThat("<useris_admin>列中文名为<是否管理员>", result.getString(i, "column_ch_name"), is("是否管理员"));
					}else if(result.getString(i, "column_name").equalsIgnoreCase("user_type")){
						assertThat("<user_type>列中文名为<用户类型>", result.getString(i, "column_ch_name"), is("用户类型"));
					}else{
						assertThat("sys_user获取到了不符合期望的采集列,column_name为：" + result.getString(i, "column_name"), true, is(false));
					}
				}
			}else if(key.equalsIgnoreCase("code_info")){
				List list = dataForMap.get(key);
				Result result = new Result((List<Map<String, Object>>)list);
				assertThat("采集code_info的5个列", result.getRowCount(), is(5));
				for(int i = 0; i < result.getRowCount(); i++){
					if(result.getString(i, "column_name").equalsIgnoreCase("ci_sp_code")){
						assertThat("<ci_sp_code>列中文名为<ci_sp_code>", result.getString(i, "column_ch_name"), is("ci_sp_code"));
					}else if(result.getString(i, "column_name").equalsIgnoreCase("ci_sp_class")){
						assertThat("<ci_sp_class>列中文名为<ci_sp_class>", result.getString(i, "column_ch_name"), is("ci_sp_class"));
					}else if(result.getString(i, "column_name").equalsIgnoreCase("ci_sp_classname")){
						assertThat("<ci_sp_classname>列中文名为<ci_sp_classname>", result.getString(i, "column_ch_name"), is("ci_sp_classname"));
					}else if(result.getString(i, "column_name").equalsIgnoreCase("ci_sp_name")){
						assertThat("<ci_sp_name>列中文名为<ci_sp_name>", result.getString(i, "column_ch_name"), is("ci_sp_name"));
					}else if(result.getString(i, "column_name").equalsIgnoreCase("ci_sp_remark")){
						assertThat("<ci_sp_remark>列中文名为<ci_sp_remark>", result.getString(i, "column_ch_name"), is("ci_sp_remark"));
					}else{
						assertThat("code_info获取到了不符合期望的采集列,列名为：" + result.getString(i, "column_name"), true, is(false));
					}
				}
			}else{
				assertThat("获取到了不符合期望的数据,table_name为：" + key, true, is(false));
			}
		}

		//错误的数据访问1：传入错误的数据库设置ID，应该导致访问失败
		String wrongStringOne = new HttpClient()
				.addData("colSetId", UNEXPECTED_ID)
				.post(getActionUrl("getColumnInfoByColSetId")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));
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
