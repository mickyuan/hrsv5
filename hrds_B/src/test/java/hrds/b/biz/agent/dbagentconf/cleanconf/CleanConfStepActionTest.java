package hrds.b.biz.agent.dbagentconf.cleanconf;

import com.alibaba.fastjson.JSON;
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
import hrds.b.biz.agent.bean.ColumnCleanParam;
import hrds.b.biz.agent.bean.TableCleanParam;
import hrds.b.biz.agent.dbagentconf.BaseInitData;
import hrds.commons.codes.CleanType;
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

@DocClass(desc = "清洗规则Action测试类", author = "WangZhengcheng")
public class CleanConfStepActionTest extends WebBaseTestCase{

	private static final long SYS_USER_TABLE_ID = 7001L;
	private static final long CODE_INFO_TABLE_ID = 7002L;
	private static final long FIRST_DATABASESET_ID = 1001L;
	private static final long SECOND_DATABASESET_ID = 1002L;
	private static final long UNEXPECTED_ID = 999999999L;
	private static final String PRE_COMPLE_FLAG = "1";
	private static final String POST_COMPLE_FLAG = "2";

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
	 *
	 * 12、插入数据
	 *
	 * 测试数据：
	 *      1、默认表清洗优先级为(字符补齐，字符替换，列合并，首尾去空)
	 *      2、默认列清洗优先级为(字符补齐，字符替换，日期格式转换，码值转换，列拆分，首尾去空)
	 *      3、data_source表：有1条数据，source_id为1
	 *      4、agent_info表：有2条数据,全部是数据库采集Agent，agent_id分别为7001，7002,source_id为1
	 *      5、database_set表：有2条数据,database_id为1001,1002, agent_id分别为7001,7002，1001的classifyId是10086，1002的classifyId是10010
	 *      1001设置完成并发送成功(is_sendok)
	 *      6、collect_job_classify表：有2条数据，classify_id为10086L、10010L，agent_id分别为7001L、7002L,user_id为-9997L
	 *      7、table_info表测试数据共4条，databaseset_id为1001
	 *          7-1、table_id:7001,table_name:sys_user,按照画面配置信息进行采集
	 *          7-2、table_id:7002,table_name:code_info,按照画面配置信息进行采集
	 *          7-3、table_id:7003,table_name:agent_info,按照自定义SQL进行采集
	 *          7-4、table_id:7004,table_name:data_source,按照自定义SQL进行采集
	 *      8、table_column表测试数据：只有在画面上进行配置的采集表才会向table_column表中保存数据
	 *          8-1、column_id为2001-2011，模拟采集了sys_user表的前10个列，列名为user_id，create_id，dep_id，role_id，
	 *               user_name，user_password，user_email，user_mobile，useris_admin，user_type，和一个login_date列,设置了remark字段，也就是采集顺序，分别是1、2、3、4、5、6、7、8、9、10、11
	 *          8-2、column_id为3001-3005，模拟采集了code_info表的所有列，列名为ci_sp_code，ci_sp_class，ci_sp_classname，
	 *               ci_sp_name，ci_sp_remark
	 *      9、table_clean表测试数据：
	 *          9-1、对sys_user表设置一个整表字符补齐规则
	 *          9-2、对sys_user表设置一个整表字符替换规则
     *      10、column_clean表测试数据
	 *          10-1、对sys_user表的create_id、dep_id列设置列字符补齐
	 *          10-2、对sys_user表的user_name列设置列字符替换
	 *          10-3、对sys_user表的login_date列设置了日期格式化
     *      11、clean_parameter表测试数据
	 *          11-1、对databaseset_id为1001的数据库直连采集作业设置一个全表的字符替换和字符补齐规则
     *      12、column_split表测试数据
	 *          12-1、对code_info表的ci_sp_name列设置字段拆分，按照偏移量，拆分为ci_s、p_name两列
	 *          12-2、对code_info表的ci_sp_classname列设置字段拆分，按照下划线拆分ci、sp、classname三列
     *      13、由于配置了列拆分，需要把拆分后的列加入到table_column表中
	 *      14、column_merge表测试数据
	 *          14-1、对sys_user表中的user_mobile和useris_admin合并成列，名叫user_mobile_admin
	 *      15、由于配置了列合并，需要把合并后的列入到table_column表中
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Before
	public void before(){
		InitAndDestDataForCleanConf.before();
		//模拟登陆
		ActionResult actionResult = BaseInitData.simulatedLogin();
		assertThat("模拟登陆", actionResult.isSuccess(), is(true));
	}

	@Test
	public void test(){
		System.out.println("---------------------");
	}

	/**
	 * 测试根据数据库设置ID获得清洗规则配置页面初始信息
	 *
	 * 正确数据访问1：使用正确的colSetId访问，应该可以拿到两条数据
	 * 错误的数据访问1：使用错误的colSetId访问，应该拿不到任何数据，但是不会报错，访问正常返回
	 * 错误的测试用例未达到三组:getInitInfo只有一个参数
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getCleanConfInfo(){
		//正确数据访问1：使用正确的colSetId访问，应该可以拿到两条数据
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getCleanConfInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Result rightData = rightResult.getDataForResult();
		assertThat("根据测试数据，输入正确的colSetId查询到的非自定义采集表信息应该有" + rightData.getRowCount() + "条", rightData.getRowCount(), is(2));

		//错误的数据访问1：使用错误的colSetId访问，应该拿不到任何数据，但是不会报错，访问正常返回
		long wrongColSetId = 99999L;
		String wrongString = new HttpClient()
				.addData("colSetId", wrongColSetId)
				.post(getActionUrl("getCleanConfInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(true));
		Result wrongData = wrongResult.getDataForResult();
		assertThat("根据测试数据，输入错误的colSetId查询到的非自定义采集表信息应该有，但是HTTP访问成功返回" + wrongData.getRowCount() + "条", wrongData.getRowCount(), is(0));
	}

	/**
	 * 测试保存单表字符补齐规则
	 *
	 * 正确数据访问1：构造合法的修改数据进行访问，应该可以正确保存(对该表之前设置过字符补齐)
	 * 错误的数据访问1：构造没有补齐字符的访问
	 * 错误的数据访问2：构造没有补齐长度的访问
	 * 错误的数据访问3：构造没有补齐方式的访问
	 * 错误的数据访问4：构造没有关联表信息的访问
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveSingleTbCompletionInfo(){
		//正确数据访问1：构造合法的修改数据进行访问，应该可以正确保存(对该表之前设置过字符补齐)
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from "+ Table_clean.TableName +" where table_id = ? and clean_type = ? and character_filling = ?", SYS_USER_TABLE_ID, CleanType.ZiFuBuQi.getCode(), StringUtil.string2Unicode("wzc")).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增之前为sys_user表构造的字符补齐测试数据是存在的", oldCount == 1, is(true));
		}

		String rightStringOne = new HttpClient()
				.addData("table_id", SYS_USER_TABLE_ID)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_length" ,6)
				.addData("filling_type", POST_COMPLE_FLAG)
				.post(getActionUrl("saveSingleTbCompletionInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long count = SqlOperator.queryNumber(db, "select count(1) from "+ Table_clean.TableName +" where table_id = ? and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuBuQi.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增成功，构造的整表字符补齐测试数据被成功保存", count == 1L, is(true));
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from "+ Table_clean.TableName +" where table_id = ? and clean_type = ? and character_filling = ?", SYS_USER_TABLE_ID, CleanType.ZiFuBuQi.getCode(), "wzc").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增成功，之前为sys_user表构造的字符补齐测试数据已经被删除了", oldCount == 0, is(true));
			int deleteCount = SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? and clean_type = ? and character_filling = ?", SYS_USER_TABLE_ID, CleanType.ZiFuBuQi.getCode(), StringUtil.string2Unicode("beyond"));
			assertThat("测试完成后，删除新增成功的整表字符补齐测试数据", deleteCount == 1, is(true));

			SqlOperator.commitTransaction(db);
		}

		//错误的数据访问1：构造没有补齐字符的访问
		String wrongStringOne = new HttpClient()
				.addData("table_id", SYS_USER_TABLE_ID)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("filling_length" ,6)
				.addData("filling_type", POST_COMPLE_FLAG)
				.post(getActionUrl("saveSingleTbCompletionInfo")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));

		//错误的数据访问2：构造没有补齐长度的访问
		String wrongStringTwo = new HttpClient()
				.addData("table_id", SYS_USER_TABLE_ID)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_type", POST_COMPLE_FLAG)
				.post(getActionUrl("saveSingleTbCompletionInfo")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));

		//错误的数据访问3：构造没有补齐方式的访问
		String wrongStringThree = new HttpClient()
				.addData("table_id", SYS_USER_TABLE_ID)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_length" ,6)
				.post(getActionUrl("saveSingleTbCompletionInfo")).getBodyString();
		ActionResult wrongResultThree = JsonUtil.toObjectSafety(wrongStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultThree.isSuccess(), is(false));

		//错误的数据访问4：构造没有关联表信息的访问
		String wrongStringFour = new HttpClient()
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_length" ,6)
				.addData("filling_type", POST_COMPLE_FLAG)
				.post(getActionUrl("saveSingleTbCompletionInfo")).getBodyString();
		ActionResult wrongResultFour = JsonUtil.toObjectSafety(wrongStringFour, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultFour.isSuccess(), is(false));
	}

	/**
	 * 测试保存一列的字符补齐规则
	 *
	 * 正确数据访问1：构造合法的新增数据进行访问，应该可以正确保存(对该列之前没有设置过字符补齐)
	 * 错误的数据访问1：构造没有补齐字符的访问
	 * 错误的数据访问2：构造没有补齐长度的访问
	 * 错误的数据访问3：构造没有补齐方式的访问
	 * 错误的数据访问4：构造没有关联列信息的访问
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveColCompletionInfo(){
		//正确数据访问1：构造合法的新增数据进行访问，应该可以正确保存(对该列之前没有设置过字符补齐)
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and clean_type = ? and character_filling = ?", 2002L, CleanType.ZiFuBuQi.getCode(), StringUtil.string2Unicode("wzc")).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增列字符补齐之前为sys_user表的create_id列构造的字符补齐测试数据是存在的", oldCount == 1, is(true));
		}

		String rightString = new HttpClient()
				.addData("column_id", 2002L)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_length" ,6)
				.addData("filling_type", POST_COMPLE_FLAG)
				.post(getActionUrl("saveColCompletionInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long count = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and clean_type = ? and character_filling = ?", 2002L, CleanType.ZiFuBuQi.getCode(), StringUtil.string2Unicode("beyond")).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增列字符补齐成功，构造的测试数据被成功保存", count == 1, is(true));
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and clean_type = ? and character_filling = ?", 2002L, CleanType.ZiFuBuQi.getCode(), StringUtil.string2Unicode("wzc")).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增列字符补齐成功，之前为sys_user表的create_id列构造的字符补齐测试数据已经被删除了", oldCount == 0, is(true));
			int deleteCount = SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? and clean_type = ? and character_filling = ?", 2002L, CleanType.ZiFuBuQi.getCode(), StringUtil.string2Unicode("beyond"));
			assertThat("测试完成后，删除新增成功的列字符补齐测试数据", deleteCount == 1, is(true));

			SqlOperator.commitTransaction(db);
		}

		//错误的数据访问1：构造没有补齐字符的访问
		String wrongStringOne = new HttpClient()
				.addData("column_id", SYS_USER_TABLE_ID)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("filling_length" ,6)
				.addData("filling_type", POST_COMPLE_FLAG)
				.post(getActionUrl("saveColCompletionInfo")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));

		//错误的数据访问2：构造没有补齐长度的访问
		String wrongStringTwo = new HttpClient()
				.addData("column_id", SYS_USER_TABLE_ID)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_type", POST_COMPLE_FLAG)
				.post(getActionUrl("saveColCompletionInfo")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));

		//错误的数据访问3：构造没有补齐方式的访问
		String wrongStringThree = new HttpClient()
				.addData("column_id", SYS_USER_TABLE_ID)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_length" ,6)
				.post(getActionUrl("saveColCompletionInfo")).getBodyString();
		ActionResult wrongResultThree = JsonUtil.toObjectSafety(wrongStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultThree.isSuccess(), is(false));

		//错误的数据访问4：构造没有关联列信息的访问
		String wrongStringFour = new HttpClient()
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_length" ,6)
				.addData("filling_type", POST_COMPLE_FLAG)
				.post(getActionUrl("saveColCompletionInfo")).getBodyString();
		ActionResult wrongResultFour = JsonUtil.toObjectSafety(wrongStringFour, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultFour.isSuccess(), is(false));
	}

	/**
	 * 测试根据列ID获得列字符补齐信息
	 *
	 * 正确数据访问1：构造正确的columnId进行测试(2003，对该列本身就设置过列字符补齐)
	 * 正确数据访问2：构造正确的columnId进行测试(2001，对该列没有设置过字符补齐，但是对其所在的表设置过整表字符补齐)
	 * 正确数据访问3：构造没有设置过列字符补齐，也没有设置过表字符补齐的columnId进行测试(3004)，拿到的应该是空的数据集
	 * 错误的测试用例未达到三组:getColCompletionInfo方法的访问永远不会因为参数而导致访问失败，只会根据实际情况，不同的参数返回不同的值
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getColCompletionInfo(){
		//正确数据访问1：构造正确的columnId进行测试(2003，对该列本身就设置过列字符补齐)
		String rightStringOne = new HttpClient()
				.addData("columnId", 2003L)
				.post(getActionUrl("getColCompletionInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Result rightDataOne = rightResultOne.getDataForResult();
		assertThat("columnId为2003的列字符补齐信息中，col_clean_id为33333", rightDataOne.getLong(0, "col_clean_id"), is(33333L));
		assertThat("columnId为2003的列字符补齐信息中，补齐类型为后补齐", rightDataOne.getInt(0, "filling_type"), is(Integer.parseInt(POST_COMPLE_FLAG)));
		assertThat("columnId为2003的列字符补齐信息中，补齐字符为空格", rightDataOne.getString(0, "character_filling"), is(" "));
		assertThat("columnId为2003的列字符补齐信息中，补齐长度为1", rightDataOne.getLong(0, "filling_length"), is(1L));
		assertThat("columnId为2003的列字符补齐信息中，columnId为2003", rightDataOne.getLong(0, "column_id"), is(2003L));

		//正确数据访问2：构造正确的columnId进行测试(2001，对该列没有设置过字符补齐，但是对其所在的表设置过整表字符补齐)
		String rightStringTwo = new HttpClient()
				.addData("columnId", 2001L)
				.post(getActionUrl("getColCompletionInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		Result rightDataTwo = rightResultTwo.getDataForResult();
		assertThat("columnId为2001的字段，没有设置列字符补齐，所以其所在表sys_user的字符补齐信息中，table_clean_id为11111", rightDataTwo.getLong(0, "table_clean_id"), is(11111L));
		assertThat("columnId为2001的字段，没有设置列字符补齐，所以其所在表sys_user的字符补齐信息中，补齐类型为前补齐", rightDataTwo.getInt(0, "filling_type"), is(Integer.parseInt(PRE_COMPLE_FLAG)));
		assertThat("columnId为2001的字段，没有设置列字符补齐，所以其所在表sys_user的字符补齐信息中，补齐字符为wzc", rightDataTwo.getString(0, "character_filling"), is("wzc"));
		assertThat("columnId为2001的字段，没有设置列字符补齐，所以其所在表sys_user的字符补齐信息中，补齐长度为3", rightDataTwo.getLong(0, "filling_length"), is(3L));

		//正确数据访问3：构造没有设置过列字符补齐，也没有设置过表字符补齐的columnId进行测试(3004)，拿到的应该是空的数据集
		String rightStringThree = new HttpClient()
				.addData("columnId", 3004L)
				.post(getActionUrl("getColCompletionInfo")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));
		assertThat("columnId为3004的字段，没有设置列字符补齐，其所在表也没有设置字符补齐，所以访问得到的数据集没有数据", rightResultThree.getDataForResult().getRowCount(), is(0));
	}

	/**
	 * 测试根据表ID获取该表的字符补齐信息
	 *
	 * 正确数据访问1：构造正确的tableId进行测试(7001，对该表设置过整表字符补齐)，应该能够得到数据
	 * 正确数据访问2：构造正确的tableId进行测试(7002，没有对该表设置过整表字符补齐)，得不到数据
	 * 错误的测试用例未达到三组:getTbCompletionInfo方法的访问永远不会因为参数而导致访问失败，只会根据实际情况，不同的参数返回不同的值
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getTbCompletionInfo(){
		//正确数据访问1：构造正确的tableId进行测试(7001，对该列本身就设置过表字符补齐)
		String rightStringOne = new HttpClient()
				.addData("tableId", SYS_USER_TABLE_ID)
				.post(getActionUrl("getTbCompletionInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Result rightDataOne = rightResultOne.getDataForResult();
		assertThat("tableId为7001的表字符补齐信息中，table_clean_id为11111", rightDataOne.getLong(0, "table_clean_id"), is(11111L));
		assertThat("tableId为7001的表字符补齐信息中，补齐类型为前补齐", rightDataOne.getInt(0, "filling_type"), is(Integer.parseInt(PRE_COMPLE_FLAG)));
		assertThat("tableId为7001的表字符补齐信息中，补齐字符为wzc", rightDataOne.getString(0, "character_filling"), is("wzc"));
		assertThat("tableId为7001的表字符补齐信息中，补齐长度为3", rightDataOne.getLong(0, "filling_length"), is(3L));

		//正确数据访问2：构造正确的tableId进行测试(7002，没有对该表设置过整表字符补齐)，得不到数据
		String rightStringTwo = new HttpClient()
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("getTbCompletionInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		assertThat("tableId为7001的表，没有设置字符补齐，所以访问得到的数据集没有数据", rightResultTwo.getDataForResult().getRowCount(), is(0));
	}

	/**
	 * 测试保存单个表的字符替换规则
	 *
	 * 正确数据访问1：构造正常的字符替换规则进行保存，由于后端接收json格式字符串，测试用例中使用List集合模拟保存两条字符替换规则
	 * 正确数据访问2：构造特殊字符，如回车
	 * 错误的测试用例未达到三组:saveSingleTbReplaceInfo方法目前还没有明确是否要对原字符和替换后的字符进行校验
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveSingleTbReplaceInfo(){
		//正确数据访问1：构造正常的字符替换规则进行保存
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " where table_id = ? and clean_type = ? and field = ? and replace_feild = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode(), StringUtil.string2Unicode("wzc"), StringUtil.string2Unicode("wqp")).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增整表字符替换之前为sys_user表构造的字符替换测试数据是存在的", oldCount == 1, is(true));
		}
		List<Table_clean> replaceList = new ArrayList<>();
		for(int i = 1; i <= 2; i++){
			String cleanType;
			String oriField;
			String newField;
			switch (i){
				case 1:
					cleanType = CleanType.ZiFuTiHuan.getCode();
					oriField = "beyond";
					newField = "hongzhi";
					break;
				case 2:
					cleanType = CleanType.ZiFuTiHuan.getCode();
					oriField = "alibaba";
					newField = "tencent";
					break;
				default:
					cleanType = "unexpected_cleanType";
					oriField = "unexpected_oriField";
					newField = "unexpected_newField";
			}
			Table_clean replace = new Table_clean();
			replace.setTable_id(SYS_USER_TABLE_ID);
			replace.setField(oriField);
			replace.setReplace_feild(newField);
			replace.setClean_type(cleanType);

			replaceList.add(replace);
		}

		String rightStringOne = new HttpClient()
				.addData("replaceString", JSON.toJSONString(replaceList))
				.addData("tableId", SYS_USER_TABLE_ID)
				.post(getActionUrl("saveSingleTbReplaceInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long count = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " where table_id = ? and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增整表字符替换成功，构造的测试数据被成功保存", count == 2, is(true));
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " where table_id = ? and field = ? and replace_feild = ?", SYS_USER_TABLE_ID, StringUtil.string2Unicode("wzc"), StringUtil.string2Unicode("wqp")).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增整表字符替换成功，之前为sys_user表构造的字符替换测试数据已经被删除了", oldCount == 0, is(true));
			int deleteCount = SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode());
			assertThat("测试完成后，删除新增成功的整表字符替换测试数据", deleteCount == 2, is(true));

			SqlOperator.commitTransaction(db);
		}

		//正确数据访问2：构造特殊字符，如回车
		replaceList.clear();
		Table_clean enter = new Table_clean();
		enter.setTable_id(SYS_USER_TABLE_ID);
		enter.setClean_type(CleanType.ZiFuTiHuan.getCode());
		enter.setField("\n");
		enter.setReplace_feild("|");
		replaceList.add(enter);

		String rightStringTwo = new HttpClient()
				.addData("replaceString", JSON.toJSONString(replaceList))
				.addData("tableId", SYS_USER_TABLE_ID)
				.post(getActionUrl("saveSingleTbReplaceInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long count = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " where table_id = ? and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增整表字符替换成功，构造的测试数据被成功保存", count == 1, is(true));
			Result result = SqlOperator.queryResult(db, "select clean_type, field, replace_feild, table_id from " + Table_clean.TableName + " where table_id = ? and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode());
			assertThat(result.getRowCount() == 1, is(true));
			assertThat(CleanType.ofEnumByCode(result.getString(0, "clean_type")) == CleanType.ZiFuTiHuan, is(true));
			assertThat(result.getString(0, "field").equals(StringUtil.string2Unicode("\n")), is(true));
			assertThat(result.getString(0, "replace_feild").equals(StringUtil.string2Unicode("|")), is(true));
			int deleteCount = SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode());
			assertThat("测试完成后，删除新增成功的整表字符替换测试数据", deleteCount == 1, is(true));

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 测试保存单个字段的字符替换规则
	 *
	 * 正确数据访问1：构造正确的列字符替换规则进行保存
	 * 错误的测试用例未达到三组:saveColReplaceInfo方法目前还没有明确是否要对原字符和替换后的字符进行校验
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveColReplaceInfo(){
		//正确数据访问1：构造正确的列字符替换规则进行保存
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and clean_type = ? and field = ? and replace_feild = ?", 2005L, CleanType.ZiFuTiHuan.getCode(), StringUtil.string2Unicode("ceshi"), StringUtil.string2Unicode("test")).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增列字符补齐之前为sys_user表的user_name列构造的字符替换测试数据是存在的", oldCount == 1, is(true));
		}

		List<Column_clean> replaceList = new ArrayList<>();
		for(int i = 1; i <= 2; i++){
			String cleanType;
			String oriField;
			String newField;
			switch (i){
				case 1:
					cleanType = CleanType.ZiFuTiHuan.getCode();
					oriField = "beyond";
					newField = "hongzhi";
					break;
				case 2:
					cleanType = CleanType.ZiFuTiHuan.getCode();
					oriField = "alibaba";
					newField = "tencent";
					break;
				default:
					cleanType = "unexpected_cleanType";
					oriField = "unexpected_oriField";
					newField = "unexpected_newField";
			}
			Column_clean replace = new Column_clean();
			replace.setColumn_id(2005L);
			replace.setField(oriField);
			replace.setReplace_feild(newField);
			replace.setClean_type(cleanType);

			replaceList.add(replace);
		}

		String rightStringOne = new HttpClient()
				.addData("replaceString", JSON.toJSONString(replaceList))
				.addData("columnId", 2005L)
				.post(getActionUrl("saveColReplaceInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long count = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and clean_type = ?", 2005L, CleanType.ZiFuTiHuan.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增列字符替换成功，构造的测试数据被成功保存", count == 2, is(true));
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and clean_type = ? and field = ? and replace_feild = ?", 2005L, CleanType.ZiFuTiHuan.getCode(), StringUtil.string2Unicode("ceshi"), StringUtil.string2Unicode("test")).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增列字符补齐成功之后，为sys_user表的user_name列构造的字符替换测试数据被删除了", oldCount == 0, is(true));
			Result result = SqlOperator.queryResult(db, "select clean_type, replace_feild from " + Column_clean.TableName + " where column_id = ? and field = ?", 2005L, StringUtil.string2Unicode("alibaba"));
			assertThat(result.getRowCount() == 1, is(true));
			assertThat(CleanType.ofEnumByCode(result.getString(0, "clean_type")) == CleanType.ZiFuTiHuan, is(true));
			assertThat(result.getString(0, "replace_feild").equals(StringUtil.string2Unicode("tencent")), is(true));
			int deleteCount = SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? and clean_type = ?", 2005L, CleanType.ZiFuTiHuan.getCode());
			assertThat("测试完成后，删除新增成功的整表字符替换测试数据", deleteCount == 2, is(true));

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 测试根据表ID获取针对该表定义的字符替换规则
	 *
	 * 正确数据访问1：尝试获取对sys_user表设置的字符替换规则，能够获取到一条数据
	 * 正确数据访问2：尝试获取对code_info表设置的字符替换规则，由于初始化测试数据中没有对code_info表设置字符替换，所以获取不到
	 * 错误的测试用例未达到三组:getSingleTbReplaceInfo方法的访问永远不会因为参数而导致访问失败，只会根据实际情况，不同的参数返回不同的值
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getSingleTbReplaceInfo(){
		//正确数据访问1：尝试获取对sys_user表设置的字符替换规则，能够获取到一条数据
		String rightStringOne = new HttpClient()
				.addData("tableId", SYS_USER_TABLE_ID)
				.post(getActionUrl("getSingleTbReplaceInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Result rightDataOne = rightResultOne.getDataForResult();
		assertThat("tableId为7001的表字符替换信息有一条", rightDataOne.getRowCount(), is(1));
		assertThat("tableId为7001的表字符替换信息中，table_clean_id为111111", rightDataOne.getLong(0, "table_clean_id"), is(111111L));
		assertThat("tableId为7001的表字符替换信息中，原字符为wzc", rightDataOne.getString(0, "field"), is("wzc"));
		assertThat("tableId为7001的表字符替换信息中，替换后字符为wqp", rightDataOne.getString(0, "replace_feild"), is("wqp"));

		//正确数据访问2：尝试获取对code_info表设置的字符替换规则，由于初始化测试数据中没有对code_info表设置字符替换，所以获取不到
		String rightStringTwo = new HttpClient()
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("getSingleTbReplaceInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		Result rightDataTwo = rightResultTwo.getDataForResult();
		assertThat("tableId为7002的表没有字符替换信息", rightDataTwo.getRowCount(), is(0));
	}

	/**
	 * 测试根据列ID获得列字符替换信息
	 *
	 * 正确数据访问1：尝试获取对sys_user表中，user_name列设置的字符替换规则，能够获取到一条数据
	 * 正确数据访问2：尝试获取对sys_user表中，user_pwd列设置的字符替换规则，由于没有对该列设置过字符替换规则，所以获取不到数据
	 * 正确数据访问3：尝试获取对code_info表中，ci_sp_remark列的字符替换规则，由于在初始化数据中，没有对ci_sp_remark列和code_info表设置字符替换规则，所以无法拿不到任何数据
	 * 错误的测试用例未达到三组:getColReplaceInfo方法的访问永远不会因为参数而导致访问失败，只会根据实际情况，不同的参数返回不同的值
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getColReplaceInfo(){
		//正确数据访问1：尝试获取对sys_user表中，user_name列设置的字符替换规则，能够获取到一条数据
		String rightStringOne = new HttpClient()
				.addData("columnId", 2005L)
				.post(getActionUrl("getColReplaceInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Result rightDataOne = rightResultOne.getDataForResult();
		assertThat("columnId为2005的列字符替换信息有一条", rightDataOne.getRowCount(), is(1));
		assertThat("columnId为2005的列字符替换信息中，col_clean_id为555555", rightDataOne.getLong(0, "col_clean_id"), is(555555L));
		assertThat("columnId为2005的列字符替换信息中，原字符为ceshi", rightDataOne.getString(0, "field"), is("ceshi"));
		assertThat("columnId为2005的列字符替换信息中，替换后字符为test", rightDataOne.getString(0, "replace_feild"), is("test"));
		assertThat("columnId为2005的列字符替换信息中，column_id为2005L", rightDataOne.getLong(0, "column_id"), is(2005L));

		//正确数据访问2：尝试获取对sys_user表中，user_pwd列设置的字符替换规则，由于没有对该列设置过字符替换规则，但是对sys_user表设置过字符替换，所以能够拿到字符替换规则
		String rightStringTwo = new HttpClient()
				.addData("columnId", 2006L)
				.post(getActionUrl("getColReplaceInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		Result rightDataTwo = rightResultTwo.getDataForResult();
		assertThat("尝试获取columnId为2006的列字符替换信息有一条", rightDataTwo.getRowCount(), is(1));
		assertThat("尝试获取columnId为2006的列字符替换信息中，table_clean_id为111111", rightDataTwo.getLong(0, "table_clean_id"), is(111111L));
		assertThat("尝试获取columnId为2006的列字符替换信息中，原字符为wzc", rightDataTwo.getString(0, "field"), is("wzc"));
		assertThat("尝试获取columnId为2006的列字符替换信息中，替换后字符为wqp", rightDataTwo.getString(0, "replace_feild"), is("wqp"));

		//正确数据访问3：尝试获取对code_info表中，ci_sp_remark列的字符替换规则，由于在初始化数据中，没有对ci_sp_remark列和code_info表设置字符替换规则，所以无法拿不到任何数据
		String rightStringThree = new HttpClient()
				.addData("columnId", 3005L)
				.post(getActionUrl("getColReplaceInfo")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));

		Result rightDataThree = rightResultThree.getDataForResult();
		assertThat("尝试获取columnId为3005的列字符替换信息，获取不到", rightDataThree.getRowCount(), is(0));
	}

	/**
	 * 测试根据表ID获取该表所有的列清洗信息
	 *
	 * 正确数据访问1：尝试获取tableId为7002的表的所有列
	 * 错误的数据访问1：尝试获取tableId为999999999的表的所有列，由于初始化时没有构造tableId为7006的数据，所以拿不到数据
	 * 错误的测试用例未达到三组: getColumnInfo方法的访问永远不会因为参数而导致访问失败，只会根据实际情况，不同的参数返回不同的值
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getColumnInfo(){
		//正确数据访问1：尝试获取tableId为7002的表的所有列
		String rightString = new HttpClient()
				.addData("tableId", SYS_USER_TABLE_ID)
				.post(getActionUrl("getColumnInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		Result rightData = rightResult.getDataForResult();
		assertThat("尝试获取tableId为7002的表的所有列，得到的结果集中有11条数据", rightData.getRowCount(), is(11));
		assertThat("尝试获取tableId为7002的表的所有列，create_id做了字符补齐", rightData.getInt(1, "compflag"), is(1));
		assertThat("尝试获取tableId为7002的表的所有列，dep_id做了字符补齐", rightData.getInt(2, "compflag"), is(1));
		assertThat("尝试获取tableId为7002的表的所有列，user_name做了字符替换", rightData.getInt(4, "replaceflag"), is(1));

		//错误的数据访问1：尝试获取tableId为7006的表的所有列，由于初始化时没有构造tableId为999999999的数据，所以拿不到数据
		String wrongString = new HttpClient()
				.addData("tableId", UNEXPECTED_ID)
				.post(getActionUrl("getColumnInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(true));

		Result wrongData = wrongResult.getDataForResult();
		assertThat("尝试获取tableId为999999999的表的所有列，得到的结果集为空", wrongData.isEmpty(), is(true));
	}

	/**
	 * 测试保存所有表清洗设置字符补齐和字符替换
	 *
	 * 正确数据访问1：模拟只设置全表字符补齐
	 * 正确数据访问2：模拟只设置全表字符替换(设置两条)
	 * 正确数据访问3：模拟既设置全表字符补齐，又设置全表字符替换
	 * 错误数据访问1：模拟值设置全表字符补齐，但是补齐方式是3，这样访问不会成功
	 *
	 * 错误的测试用例未达到三组: saveAllTbCleanConfigInfo是一个保存操作，上述三个测试用例已经可以覆盖所有的情况
	 * @Param: 无
	 * @return: 无
	 * TODO compFlag和replaceFlag由于addData不能传boolean类型，所以在测试的时候需要注意，修改被测方法代码
	 * */
	@Test
	public void saveAllTbCleanConfigInfo(){
		//正确数据访问1：模拟只设置全表字符补齐
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Clean_parameter.TableName + " where database_id = ?", FIRST_DATABASESET_ID).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增全表字符补齐之前为database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业构造的全表清洗测试数据是存在的", oldCount == 2, is(true));
		}

		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("compFlag", "true")
				.addData("replaceFlag", "false")
				.addData("compType", "1")
				.addData("compChar", "test_saveAllTbCleanConfigInfo")
				.addData("compLen", "29")
				.post(getActionUrl("saveAllTbCleanConfigInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Clean_parameter.TableName + " where c_id in(666666, 777777)").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增全表字符补齐成功后，为database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业构造的全表清洗测试数据没有了", oldCount == 0, is(true));

			Result compResult = SqlOperator.queryResult(db, "select filling_type, character_filling, filling_length from " + Clean_parameter.TableName + " where database_id = ? and clean_type = ?", FIRST_DATABASESET_ID, CleanType.ZiFuBuQi.getCode());
			assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置字符补齐成功", compResult.getRowCount() == 1, is(true));
			assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的补齐方式为前补齐", compResult.getString(0, "filling_type"), is("1"));
			assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的补齐字符为test_saveAllTbCleanConfigInfo", compResult.getString(0, "character_filling"), is(StringUtil.string2Unicode("test_saveAllTbCleanConfigInfo")));
			assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的补齐长度为29", compResult.getLong(0, "filling_length"), is(29L));

			SqlOperator.execute(db, "delete from " + Clean_parameter.TableName + " where database_id = ? and clean_type = ?", FIRST_DATABASESET_ID, CleanType.ZiFuBuQi.getCode());

			SqlOperator.commitTransaction(db);
		}

		//正确数据访问2：模拟只设置全表字符替换(设置两条)
		String[] oriFieldArr = {"zxz", "hx"};
		String[] replaceFeildArr = {"shl", "zq"};
		String rightStringTwo = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("compFlag", "false")
				.addData("replaceFlag", "true")
				.addData("oriFieldArr", oriFieldArr)
				.addData("replaceFeildArr", replaceFeildArr)
				.post(getActionUrl("saveAllTbCleanConfigInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Clean_parameter.TableName + " where c_id in(666666, 777777)").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增全表字符替换成功后，为database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业构造的全表清洗测试数据没有了", oldCount == 0, is(true));

			Result replaceResult = SqlOperator.queryResult(db, "select field, replace_feild from " + Clean_parameter.TableName + " where database_id = ? and clean_type = ?", FIRST_DATABASESET_ID, CleanType.ZiFuTiHuan.getCode());
			assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置字符替换成功", replaceResult.getRowCount() == 2, is(true));
			assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的原字符为zxz", replaceResult.getString(0, "field"), is(StringUtil.string2Unicode("zxz")));
			assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的替换后字符为shl", replaceResult.getString(0, "replace_feild"), is(StringUtil.string2Unicode("shl")));
			assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的原字符为hx", replaceResult.getString(1, "field"), is(StringUtil.string2Unicode("hx")));
			assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的替换后字符为zq", replaceResult.getString(1, "replace_feild"), is(StringUtil.string2Unicode("zq")));

			SqlOperator.execute(db, "delete from " + Clean_parameter.TableName + " where database_id = ? and clean_type = ?", FIRST_DATABASESET_ID, CleanType.ZiFuTiHuan.getCode());

			SqlOperator.commitTransaction(db);
		}

		//正确数据访问3：模拟既设置全表字符补齐，又设置全表字符替换
		String rightStringThree = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("compFlag", "true")
				.addData("replaceFlag", "true")
				.addData("compType", "1")
				.addData("compChar", "test_saveAllTbCleanConfigInfo")
				.addData("compLen", "29")
				.addData("oriFieldArr", oriFieldArr)
				.addData("replaceFeildArr", replaceFeildArr)
				.post(getActionUrl("saveAllTbCleanConfigInfo")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Clean_parameter.TableName + " where c_id in(666666, 777777)").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("新增全表字符替换成功后，为database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业构造的全表清洗测试数据没有了", oldCount == 0, is(true));

			Result result = SqlOperator.queryResult(db, "select * from " + Clean_parameter.TableName + " where database_id = ?", FIRST_DATABASESET_ID);
			assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置全表清洗成功", result.getRowCount() == 3, is(true));
			for(int i = 0; i < result.getRowCount(); i++){
				if(CleanType.ofEnumByCode(result.getString(i, "clean_type")) == CleanType.ZiFuBuQi){
					assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的补齐方式为前补齐", result.getString(i, "filling_type"), is("1"));
					assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的补齐字符为test_saveAllTbCleanConfigInfo", result.getString(i, "character_filling"), is(StringUtil.string2Unicode("test_saveAllTbCleanConfigInfo")));
					assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的补齐长度为29", result.getLong(i, "filling_length"), is(29L));
				}else if(CleanType.ofEnumByCode(result.getString(i, "clean_type")) == CleanType.ZiFuTiHuan && result.getString(i, "field").equalsIgnoreCase(StringUtil.string2Unicode("zxz"))){
					assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的替换后字符为shl", result.getString(i, "replace_feild"), is(StringUtil.string2Unicode("shl")));
				}else if(CleanType.ofEnumByCode(result.getString(i, "clean_type")) == CleanType.ZiFuTiHuan && result.getString(i, "field").equalsIgnoreCase(StringUtil.string2Unicode("hx"))){
					assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的替换后字符为zq", result.getString(i, "replace_feild"), is(StringUtil.string2Unicode("zq")));
				}else{
					assertThat("本测试用例出现了不符合预期的结果", true, is(false));
				}
			}

			SqlOperator.execute(db, "delete from " + Clean_parameter.TableName + " where database_id = ? and clean_type = ?", FIRST_DATABASESET_ID, CleanType.ZiFuTiHuan.getCode());
			SqlOperator.execute(db, "delete from " + Clean_parameter.TableName + " where database_id = ? and clean_type = ?", FIRST_DATABASESET_ID, CleanType.ZiFuBuQi.getCode());

			SqlOperator.commitTransaction(db);
		}

		//错误数据访问1：模拟值设置全表字符补齐，但是补齐方式是3，这样访问不会成功
		String wrongString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("compFlag", "true")
				.addData("replaceFlag", "false")
				.addData("compType", "3")
				.addData("compChar", "test_saveAllTbCleanConfigInfo")
				.addData("compLen", "29")
				.post(getActionUrl("saveAllTbCleanConfigInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试根据数据库设置ID查询所有表清洗设置字符补齐和字符替换规则
	 *
	 * 正确数据访问1：模拟获取database_id为1001的数据库直连采集作业所有表清洗规则，能够获取到两条，一条字符补齐规则，一条字符替换规则
	 * 错误的数据访问1：模拟获取database_id为1002的数据库直连采集作业所有表清洗规则，因为在这个作业中没有配置表，所以获取不到数据
	 * 错误的测试用例未达到三组: getAllTbCleanConfInfo不会因为正常情况下，不会因为参数不同而导致访问失败，只会因为参数的不同获取到的数据也不同
	 * @Param: 无
	 * @return: 无
	 * TODO getDataForMap()泛型没用
	 * */
	@Test
	public void getAllTbCleanConfInfo(){
		//正确数据访问1：模拟获取database_id为1001的数据库直连采集作业所有表清洗规则，能够获取到两条，一条字符补齐规则，一条字符替换规则
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getAllTbCleanConfInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		Map<Object, Object> rightData = rightResult.getDataForMap();
		Result replaceResult =  (Result)rightData.get("replace");
		Result completionResult =  (Result)rightData.get("completion");
		assertThat("模拟获取database_id为1001的数据库直连采集作业所有表清洗规则,其中有一条字符替换规则", replaceResult.getRowCount() == 1, is(true));
		assertThat("模拟获取database_id为1001的数据库直连采集作业所有表清洗规则,其中有一条字符替换规则，原字符为", replaceResult.getString(0, "field"), is("test_orifield"));
		assertThat("模拟获取database_id为1001的数据库直连采集作业所有表清洗规则,其中有一条字符替换规则", replaceResult.getString(0, "replace_feild"), is("test_newField"));
		assertThat("模拟获取database_id为1001的数据库直连采集作业所有表清洗规则,其中有一条字符补齐规则", completionResult.getRowCount() == 1, is(true));
		assertThat("模拟获取database_id为1001的数据库直连采集作业所有表清洗规则,其中有一条字符补齐规则,补齐字符为 ", completionResult.getString(0, "character_filling"), is("cleanparameter"));
		assertThat("模拟获取database_id为1001的数据库直连采集作业所有表清洗规则,其中有一条字符补齐规则,补齐长度为 ", completionResult.getLong(0, "filling_length"), is("14"));
		assertThat("模拟获取database_id为1001的数据库直连采集作业所有表清洗规则,其中有一条字符补齐规则,补齐方式为 ", completionResult.getString(0, "filling_type"), is("1"));

		//错误的数据访问1：模拟获取database_id为1002的数据库直连采集作业所有表清洗规则，因为在这个作业中没有配置表，所以获取不到数据
		String rightStringTwo = new HttpClient()
				.addData("colSetId", SECOND_DATABASESET_ID)
				.post(getActionUrl("getAllTbCleanConfInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		Map<Object, Object> rightDataTwo = rightResultTwo.getDataForMap();

		assertThat("模拟获取database_id为1002的数据库直连采集作业所有表清洗规则,获取不到数据", rightDataTwo.isEmpty(), is(true));
	}

	/**
	 * 测试根据列ID获取针对该列设置的日期格式化规则
	 *
	 * 正确数据访问1：模拟获取sys_user表的login_date字段的日期格式化规则，由于之前在构造了初始化数据，所以可以查到
	 * 正确数据访问2：模拟获取sys_user表的user_email字段的日期格式化规则，由于没有构造初始化数据，所以查不到
	 * 错误的测试用例未达到三组:getDateFormatInfo()方法永远不会因为参数而导致方法访问失败，只会根据实际情况，根据不同的参数返回不同的结果集
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getDateFormatInfo(){
		//正确数据访问1：模拟获取sys_user表的login_date字段的日期格式化规则，由于之前在构造了初始化数据，所以可以查到
		String rightStringOne = new HttpClient()
				.addData("columnId", 2011L)
				.post(getActionUrl("getDateFormatInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Result rightDataOne = rightResultOne.getDataForResult();
		assertThat("模拟获取sys_user表的login_date字段的日期格式化规则，由于之前在构造了初始化数据，所以可以查到1条数据", rightDataOne.getRowCount() == 1, is(true));
		assertThat("模拟获取sys_user表的login_date字段的日期格式化规则，原始格式为YYYY-MM-DD", rightDataOne.getString(0, "old_format").equalsIgnoreCase("YYYY-MM-DD"), is(true));
		assertThat("模拟获取sys_user表的login_date字段的日期格式化规则，转换格式为YYYY-MM", rightDataOne.getString(0, "convert_format").equalsIgnoreCase("YYYY-MM"), is(true));

		//正确数据访问2：模拟获取sys_user表的user_email字段的日期格式化规则，由于没有构造初始化数据，所以查不到
		String rightStringTwo = new HttpClient()
				.addData("columnId", 2007L)
				.post(getActionUrl("getDateFormatInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		Result rightDataTwo = rightResultTwo.getDataForResult();
		assertThat("模拟获取sys_user表的user_email字段的日期格式化规则，由于没有构造初始化数据，所以查不到数据", rightDataTwo.getRowCount() == 0, is(true));
	}

	/**
	 * 测试保存列清洗日期格式化
	 *
	 * 正确数据访问1：模拟修改对sys_user表的login_date字段设置日期格式化
	 * 错误数据访问1：模拟修改对sys_user表的login_date字段设置日期格式化，但是缺少原日期格式
	 * 错误数据访问2：模拟修改对sys_user表的login_date字段设置日期格式化，但是缺少转换后日期格式
	 * 错误的测试用例未达到三组:saveDateFormatInfo方法上述测试用例就可以覆盖所有可能出现的情况
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveDateFormatInfo(){
		//正确数据访问1：模拟修改对sys_user表的login_date字段设置日期格式化
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and old_format = ? and convert_format = ?", 2011L, "YYYY-MM-DD", "YYYY-MM").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("模拟修改对sys_user表的login_date字段设置日期格式化之前，原数据存在", oldCount == 1, is(true));
		}

		String rightStringOne = new HttpClient()
				.addData("clean_type", CleanType.ShiJianZhuanHuan.getCode())
				.addData("convert_format", "yyyy年MM月dd日 HH:mm:ss")
				.addData("old_format", "YYYY-MM-DD")
				.addData("column_id", 2011L)
				.post(getActionUrl("saveDateFormatInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and old_format = ? and convert_format = ?", 2011L, "YYYY-MM-DD", "YYYY-MM").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("模拟修改对sys_user表的login_date字段设置日期格式化之后，原数据被删除了", oldCount == 0, is(true));
			Result result = SqlOperator.queryResult(db, "select convert_format, old_format from " + Column_clean.TableName + " where column_id = ?", 2011L);
			assertThat("模拟修改对sys_user表的login_date字段设置日期格式化之后，转换日期格式被改为yyyy年MM月dd日 HH:mm:ss", result.getString(0, "convert_format").equalsIgnoreCase("yyyy年MM月dd日 HH:mm:ss"), is(true));
			assertThat("模拟修改对sys_user表的login_date字段设置日期格式化之后，原日期格式被改为YYYY-MM-DD HH:mm:ss", result.getString(0, "old_format").equalsIgnoreCase("YYYY-MM-DD"), is(true));

			int count = SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ?", 2011L);
			assertThat(count == 1, is(true));

			SqlOperator.commitTransaction(db);
		}

		//错误数据访问1：模拟修改对sys_user表的login_date字段设置日期格式化，但是缺少原日期格式
		String wrongStringOne = new HttpClient()
				.addData("clean_type", CleanType.ShiJianZhuanHuan.getCode())
				.addData("convert_format", "yyyy年MM月dd日 HH:mm:ss")
				.addData("column_id", 2011L)
				.post(getActionUrl("saveDateFormatInfo")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));

		//错误数据访问2：模拟修改对sys_user表的login_date字段设置日期格式化，但是缺少转换后日期格式
		String wrongStringTwo = new HttpClient()
				.addData("clean_type", CleanType.ShiJianZhuanHuan.getCode())
				.addData("old_format", "YYYY-MM-DD")
				.addData("column_id", 2011L)
				.post(getActionUrl("saveDateFormatInfo")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));
	}

	/**
	 * 测试根据columnId查询列拆分信息
	 *
	 * 正确数据访问1：模拟查询为code_info表的ci_sp_classname(3003)字段设置的列拆分信息，应该能查到三条数据
	 * 正确数据访问2：模拟查询为code_info表的ci_sp_class(3002)字段设置的列拆分信息，由于没有为其构造初始化数据，所以查不到数据
	 * 错误的测试用例未达到三组:getColSplitInfo方法永远不会因为传递的参数导致访问失败，只会根据实际情况，返回不同的查询结果
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getColSplitInfo(){
		//正确数据访问1：模拟查询为code_info表的ci_sp_classname字段设置的列拆分信息，应该能查到三条数据
		String rightStringOne = new HttpClient()
				.addData("columnId", 3003L)
				.post(getActionUrl("getColSplitInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Result rightDataOne = rightResultOne.getDataForResult();
		assertThat("模拟查询为code_info表的ci_sp_classname(3003)字段设置的列拆分信息，应该能查到三条数据", rightDataOne.getRowCount() == 3, is(true));
		for(int i = 0; i < rightDataOne.getRowCount(); i++){
			if(rightDataOne.getLong(i, "col_split_id") == 101010103L){
				assertThat("模拟查询为code_info表的ci_sp_classname(3003)字段设置的列拆分信息，有一列拆分为了ci", rightDataOne.getString(i, "col_name").equalsIgnoreCase("ci"), is(true));
			}else if(rightDataOne.getLong(i, "col_split_id") == 101010104L){
				assertThat("模拟查询为code_info表的ci_sp_classname(3003)字段设置的列拆分信息，有一列拆分为了sp", rightDataOne.getString(i, "col_name").equalsIgnoreCase("sp"), is(true));
			}else if(rightDataOne.getLong(i, "col_split_id") == 101010105L){
				assertThat("模拟查询为code_info表的ci_sp_classname(3003)字段设置的列拆分信息，有一列拆分为了classname", rightDataOne.getString(i, "col_name").equalsIgnoreCase("classname"), is(true));
			}else{
				assertThat("模拟查询为code_info表的ci_sp_classname(3003)字段设置的列拆分信息，出现了不符合期望的情况，列名为" + rightDataOne.getString(i, "col_name"), false, is(true));
			}
		}

		//正确数据访问2：模拟查询为code_info表的ci_sp_class字段设置的列拆分信息，由于没有为其构造初始化数据，所以查不到数据
		String rightStringTwo = new HttpClient()
				.addData("columnId", 3002L)
				.post(getActionUrl("getColSplitInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		List<Column_split> rightDataTwo = rightResultTwo.getDataForEntityList(Column_split.class);
		assertThat("模拟查询为code_info表的ci_sp_class(3002)字段设置的列拆分信息，应该查不到数据", rightDataTwo.isEmpty(), is(true));
	}

	/**
	 * 测试删除一条列拆分规则
	 *
	 * 正确数据访问1：由于columnId为3003的列被拆分成了三列，所以进行三次删除，最后一次删除之后，由于该列已经在column_split表中已经没有数据了，所以应该在column_clean表中，把这一列的字段拆分清洗规则删除掉
	 * 错误的数据访问1：传入错误的colSplitId
	 * 错误的数据访问2：传入错误的colCleanId
	 * 错误的数据访问3：传入错误的colSplitId和colCleanId
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteColSplitInfo(){
		//正确数据访问1：由于columnId为3003的列被拆分成了三列，所以进行三次删除，最后一次删除之后，由于该列已经在column_split表中已经没有数据了，所以应该在column_clean表中，把这一列的字段拆分清洗规则删除掉
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long columnOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where column_id = ?", 141414L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行第一次删除前，拆分为ci的列在table_column表中存在", columnOldCount == 1, is(true));
			long splitOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id = ?", 101010103L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行第一次删除前，拆分为ci的列在column_split表中存在", splitOldCount == 1, is(true));
			long cleanOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where col_clean_id = ? and clean_type = ?", 101010102L, CleanType.ZiFuChaiFen.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行第一次删除前，列ci_sp_classname的列拆分配置在column_clean表中存在", cleanOldCount == 1, is(true));
		}

		String rightStringOne = new HttpClient()
				.addData("colSplitId", 101010103L)
				.addData("colCleanId", 101010102L)
				.post(getActionUrl("deleteColSplitInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long columnOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where column_id = ?", 141414L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行第一次删除后，拆分为ci的列在table_column表中被删除了", columnOldCount == 0, is(true));
			long splitOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id = ?", 101010103L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行第一次删除后，拆分为ci的列在column_split表中被删除了", splitOldCount == 0, is(true));
			long cleanOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where col_clean_id = ? and clean_type = ?", 101010102L, CleanType.ZiFuChaiFen.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行第一次删除后，列ci_sp_classname的列拆分配置在column_clean表中存在", cleanOldCount == 1, is(true));
		}

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long columnOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where column_id = ?", 151515L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行第二次删除前，拆分为sp的列在table_column表中存在", columnOldCount == 1, is(true));
			long splitOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id = ?", 101010104L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行第二次删除前，拆分为sp的列在column_split表中存在", splitOldCount == 1, is(true));
			long cleanOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where col_clean_id = ? and clean_type = ?", 101010102L, CleanType.ZiFuChaiFen.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行第二次删除前，列ci_sp_classname的列拆分配置在column_clean表中存在", cleanOldCount == 1, is(true));
		}

		String rightStringTwo = new HttpClient()
				.addData("colSplitId", 101010104L)
				.addData("colCleanId", 101010102L)
				.post(getActionUrl("deleteColSplitInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long columnOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where column_id = ?", 151515L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行第二次删除后，拆分为sp的列在table_column表中被删除了", columnOldCount == 0, is(true));
			long splitOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id = ?", 101010104L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行第二次删除后，拆分为sp的列在column_split表中被删除了", splitOldCount == 0, is(true));
			long cleanOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where col_clean_id = ? and clean_type = ?", 101010102L, CleanType.ZiFuChaiFen.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行第二次删除后，列ci_sp_classname的列拆分配置在column_clean表中存在", cleanOldCount == 1, is(true));
		}

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long columnOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where column_id = ?", 161616L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行第三次删除前，拆分为classname的列在table_column表中存在", columnOldCount == 1, is(true));
			long splitOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id = ?", 101010105L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行第三次删除前，拆分为classname的列在column_split表中存在", splitOldCount == 1, is(true));
			long cleanOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where col_clean_id = ? and clean_type = ?", 101010102L, CleanType.ZiFuChaiFen.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行第三次删除前，列ci_sp_classname的列拆分配置在column_clean表中存在", cleanOldCount == 1, is(true));
		}

		String rightStringThree = new HttpClient()
				.addData("colSplitId", 101010105L)
				.addData("colCleanId", 101010102L)
				.post(getActionUrl("deleteColSplitInfo")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long columnOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where column_id = ?", 161616L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行第三次删除后，拆分为classname的列在table_column表中被删除了", columnOldCount == 0, is(true));
			long splitOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id = ?", 101010105L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行第三次删除后，拆分为classname的列在column_split表中被删除了", splitOldCount == 0, is(true));
			long cleanOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where col_clean_id = ? and clean_type = ?", 101010102L, CleanType.ZiFuChaiFen.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行第三次删除后，列ci_sp_classname的列拆分配置在column_clean表中被删除了", cleanOldCount == 0, is(true));
		}

		//错误的数据访问1：传入错误的colSplitId
		String wrongStringOne = new HttpClient()
				.addData("colSplitId", 999999999999L)
				.addData("colCleanId", 101010101L)
				.post(getActionUrl("deleteColSplitInfo")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));

		//错误的数据访问2：传入错误的colCleanId
		String wrongStringTwo = new HttpClient()
				.addData("colSplitId", 1111111L)
				.addData("colCleanId", 101010199L)
				.post(getActionUrl("deleteColSplitInfo")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));

		//错误的数据访问3：传入错误的colSplitId和colCleanId
		String wrongStringThree = new HttpClient()
				.addData("colSplitId", 999999999999L)
				.addData("colCleanId", 101010199L)
				.post(getActionUrl("deleteColSplitInfo")).getBodyString();
		ActionResult wrongResultThree = JsonUtil.toObjectSafety(wrongStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultThree.isSuccess(), is(false));
	}

	/**
	 * 测试保存列拆分规则
	 *
	 * 正确数据访问1：模拟修改对code_info表的ci_sp_classname设置的字段拆分规则，由按照下划线分隔拆分，变为按照偏移量拆分为ci_s、p_class、name
	 * 正确数据访问2：模拟新增对code_info表的ci_sp_code设置字段拆分规则，按照下划线分拆分为ci、sp、code三列
	 * 错误的测试用例未达到三组:上述测试用例可以覆盖saveColSplitInfo()方法的所有场景
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveColSplitInfo(){
		//正确数据访问1：模拟修改对code_info表的ci_sp_classname设置的字段拆分规则，由按照下划线分隔拆分，变为按照偏移量拆分为ci_s、p_class、name
		List<Column_split> offsetSpilts = new ArrayList<>();
		for(int i = 0; i < 3; i++){
			long colSplitId = 0;
			String columnName = null;
			String splitType = "1";
			String columnChName = null;
			String columnType = "varchar(512)";
			long colCleanId = 101010102L;
			long columnId = 3003L;
			String offset = null;
			switch (i){
				case 0 :
					colSplitId = 101010103L;
					columnName = "ci_s";
					columnChName = "ci_s_ch";
					offset = "3";
					break;
				case 1 :
					colSplitId = 101010104L;
					columnName = "p_class";
					columnChName = "p_class_ch";
					offset = "10";
					break;
				case 2 :
					colSplitId = 101010105L;
					columnName = "name";
					columnChName = "name_ch";
					offset = "14";
					break;
			}
			Column_split columnSplit = new Column_split();
			columnSplit.setCol_split_id(colSplitId);
			columnSplit.setCol_name(columnName);
			columnSplit.setSplit_type(splitType);
			columnSplit.setCol_zhname(columnChName);
			columnSplit.setCol_type(columnType);
			columnSplit.setCol_clean_id(colCleanId);
			columnSplit.setColumn_id(columnId);
			columnSplit.setValid_e_date(Constant.MAXDATE);
			columnSplit.setValid_s_date(DateUtil.getSysDate());
			columnSplit.setCol_offset(offset);

			offsetSpilts.add(columnSplit);
		}

		try(DatabaseWrapper db = new DatabaseWrapper()){
			List<Table_column> tableColumns = SqlOperator.queryList(db, Table_column.class, "select * from " + Table_column.TableName + " where colume_name in" +
							" (select t1.colume_name from "+ Table_column.TableName +" t1" +
							" JOIN " + Column_split.TableName + " t2 ON t1.colume_name = t2.col_name " +
							" JOIN " + Column_clean.TableName + " t3 ON t2.col_clean_id = t3.col_clean_id " +
							" WHERE t2.col_clean_id = ? and t2.column_id = ? and t1.table_id = ? and t1.is_new = ?) ",
					101010102L, 3003L, CODE_INFO_TABLE_ID, IsFlag.Shi.getCode());
			assertThat("进行code_info表的ci_cp_classname列拆分修改保存前，拆分为ci、sp、classname的列在table_column表中存在", tableColumns.size() == 3, is(true));
			for(Table_column tableColumn : tableColumns){
				if(tableColumn.getColumn_id() == 141414L){
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存前，拆分为ci的列在table_column表中存在", tableColumn.getColume_name().equalsIgnoreCase("ci"), is(true));
				}else if(tableColumn.getColumn_id() == 151515L){
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存前，拆分为sp的列在table_column表中存在", tableColumn.getColume_name().equalsIgnoreCase("sp"), is(true));
				}else if(tableColumn.getColumn_id() == 161616L){
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存前，拆分为classname的列在table_column表中存在", tableColumn.getColume_name().equalsIgnoreCase("classname"), is(true));
				}else{
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存前，在table_column表中查询到了不符合预期的列" + tableColumn.getColume_name(), false, is(true));
				}
			}
			long beforeDelSpCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id in (101010103, 101010104, 101010105) ").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行code_info表的ci_cp_classname列拆分修改保存前，列拆分信息在column_split表中存在", beforeDelSpCount == 3, is(true));
		}

		String rightStringOne = new HttpClient()
				.addData("col_clean_id", 101010102L)
				.addData("clean_type", CleanType.ZiFuChaiFen.getCode())
				.addData("column_id", 3003L)
				.addData("columnSplitString", JSON.toJSONString(offsetSpilts))
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("saveColSplitInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			List<Table_column> tableColumns = SqlOperator.queryList(db, Table_column.class, "select * from " + Table_column.TableName + " where colume_name in" +
							" (select t1.colume_name from "+ Table_column.TableName +" t1" +
							" JOIN " + Column_split.TableName + " t2 ON t1.colume_name = t2.col_name " +
							" JOIN " + Column_clean.TableName + " t3 ON t2.col_clean_id = t3.col_clean_id " +
							" WHERE t2.col_clean_id = ? and t2.column_id = ? and t1.table_id = ? and t1.is_new = ?) ",
					101010102L, 3003L, CODE_INFO_TABLE_ID, IsFlag.Shi.getCode());
			assertThat("进行code_info表的ci_cp_classname列拆分修改保存后，拆分为ci_s、p_class、name的列在table_column表中存在", tableColumns.size() == 3, is(true));
			for(Table_column tableColumn : tableColumns){
				if(tableColumn.getColume_name().equalsIgnoreCase("ci_s")){
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存后，拆分为ci_s的列在table_column表中存在", tableColumn.getColume_name().equalsIgnoreCase("ci_s"), is(true));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("p_class")){
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存后，拆分为p_class的列在table_column表中存在", tableColumn.getColume_name().equalsIgnoreCase("p_class"), is(true));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("name")){
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存后，拆分为name的列在table_column表中存在", tableColumn.getColume_name().equalsIgnoreCase("name"), is(true));
				}else{
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存后，在table_column表中查询到了不符合预期的列" + tableColumn.getColume_name(), false, is(true));
				}
			}
			long afterDelSpCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id in (101010103, 101010104, 101010105) ").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("进行code_info表的ci_cp_classname列拆分修改保存后，原来对该列定义的列拆分信息在column_split表中不存在", afterDelSpCount == 0, is(true));

			Result result = SqlOperator.queryResult(db, "select * from " + Column_split.TableName + " where column_id = ?", 3003L);
			assertThat("进行code_info表的ci_cp_classname列拆分修改保存后，对该列定义的列拆分信息ci_s、p_class、name在column_split表中存在", result.getRowCount() == 3, is(true));

			for(int i = 0; i < result.getRowCount(); i++){
				if(result.getString(i, "col_name").equalsIgnoreCase("ci_s")){
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存后，对该列定义的列拆分信息ci_s在column_split表中存在", result.getString(i, "col_offset").equalsIgnoreCase("3"), is(true));
				}else if(result.getString(i, "col_name").equalsIgnoreCase("p_class")){
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存后，对该列定义的列拆分信息p_class在column_split表中存在", result.getString(i, "col_offset").equalsIgnoreCase("10"), is(true));
				}else if(result.getString(i, "col_name").equalsIgnoreCase("name")){
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存后，对该列定义的列拆分信息name在column_split表中存在", result.getString(i, "col_offset").equalsIgnoreCase("14"), is(true));
				}else{
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存后，对该列定义的列拆分信息在column_split表中出现不符合期望的情况" + result.getString(i, "col_name"), false, is(true));
				}
			}

			//删除新增时带来的数据
			int execute1 = SqlOperator.execute(db, "delete from " + Table_column.TableName + " where colume_name in " +
							" (select t1.colume_name from table_column t1 " +
							" JOIN " + Column_split.TableName + " t2 ON t1.colume_name = t2.col_name " +
							" JOIN " + Column_clean.TableName + " t3 ON t2.col_clean_id = t3.col_clean_id " +
							" WHERE t2.col_clean_id = ? and t2.column_id = ? and t1.table_id = ? and t1.is_new = ?)",
					101010102, 3003, CODE_INFO_TABLE_ID, IsFlag.Shi.getCode());
			assertThat("删除新增列拆分时table_column表的测试数据", execute1 == 3, is(true));

			int execute = SqlOperator.execute(db, "delete from " + Column_split.TableName + " where column_id = ? and col_clean_id = ?", 3003L, 101010102L);
			assertThat("删除新增列拆分时column_split表的测试数据", execute == 3, is(true));

			SqlOperator.commitTransaction(db);
		}

		//正确数据访问2：模拟新增对code_info表的ci_sp_code设置字段拆分规则，按照下划线分拆分为ci、sp、code三列
		List<Column_split> splitByUnderLine = new ArrayList<>();
		for(int i = 0; i < 3; i++){
			String colName = null;
			String splitSep = "_";
			long seq = 0;
			String splitType = "2";
			String colChName = null;
			String colType = "varchar(512)";
			switch (i){
				case 0 :
					colName = "ci";
					seq = 1;
					colChName = "ci_ch";
					break;
				case 1 :
					colName = "sp";
					seq = 2;
					colChName = "sp_ch";
					break;
				case 2 :
					colName = "code";
					seq = 3;
					colChName = "code_ch";
					break;
			}

			Column_split columnSplit = new Column_split();
			columnSplit.setCol_name(colName);
			columnSplit.setSplit_sep(splitSep);
			columnSplit.setSeq(seq);
			columnSplit.setCol_type(colType);
			columnSplit.setCol_zhname(colChName);
			columnSplit.setSplit_type(splitType);

			splitByUnderLine.add(columnSplit);
		}

		String rightStringTwo = new HttpClient()
				.addData("clean_type", CleanType.ZiFuChaiFen.getCode())
				.addData("column_id", 3001L)
				.addData("columnSplitString", JSON.toJSONString(splitByUnderLine))
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("saveColSplitInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			List<Table_column> tableColumns = SqlOperator.queryList(db, Table_column.class, "select * from " + Table_column.TableName +
					" where colume_name in " +
					" (select t1.colume_name from table_column t1" +
					" JOIN " + Column_split.TableName + " t2 ON t1.colume_name = t2.col_name " +
					" JOIN " + Column_clean.TableName + " t3 ON t2.col_clean_id = t3.col_clean_id " +
					" WHERE t2.column_id = ? and t1.table_id = ? and t1.is_new = ?) ", 3001L, CODE_INFO_TABLE_ID, IsFlag.Shi.getCode());
			assertThat("模拟新增对code_info表的ci_sp_code设置字段拆分规则，table_column表中按照下划线分拆分为ci、sp、code三列成功", tableColumns.size() == 3, is(true));
			for(Table_column tableColumn : tableColumns){
				if(tableColumn.getColume_name().equalsIgnoreCase("ci")){
					assertThat("模拟新增对code_info表的ci_sp_code设置字段拆分规则，table_column表中按照下划线分拆分为ci", true, is(true));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("sp")){
					assertThat("模拟新增对code_info表的ci_sp_code设置字段拆分规则，table_column表中按照下划线分拆分为sp", true, is(true));
				}else if(tableColumn.getColume_name().equalsIgnoreCase("code")){
					assertThat("模拟新增对code_info表的ci_sp_code设置字段拆分规则，table_column表中按照下划线分拆分为code", true, is(true));
				}else{
					assertThat("模拟新增对code_info表的ci_sp_code设置字段拆分规则，table_column表中出现了不期望出现的数据, 列名为" + tableColumn.getColume_name(), false, is(true));
				}
			}

			long afterSplitCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where column_id = ?", 3001L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("模拟新增对code_info表的ci_sp_code设置字段拆分规则，column_split表中按照下划线分拆分为ci、sp、code三列成功", afterSplitCount == 3, is(true));

			//删除新增时带来的数据
			int execute1 = SqlOperator.execute(db, "delete from " + Table_column.TableName + " where colume_name in " +
							" (select t1.colume_name from table_column t1 " +
							" JOIN " + Column_split.TableName + " t2 ON t1.colume_name = t2.col_name " +
							" JOIN " + Column_clean.TableName + " t3 ON t2.col_clean_id = t3.col_clean_id " +
							" WHERE t2.column_id = ? and t1.table_id = ? and t1.is_new = ?)",
					3001L, CODE_INFO_TABLE_ID, IsFlag.Shi.getCode());
			assertThat("删除新增列拆分时table_column表的测试数据", execute1 == 3, is(true));

			int execute = SqlOperator.execute(db, "delete from " + Column_split.TableName + " where column_id = ?", 3001L);
			assertThat("删除新增列拆分时column_split表的测试数据", execute == 3, is(true));

			int execute2 = SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ?", 3001L);
			assertThat("删除新增列拆分时column_clean表的测试数据", execute2 == 1, is(true));

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 测试获取列码值转换清洗规则
	 *
	 * TODO 被测方式未完成
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getCVConversionInfo(){

	}

	/**
	 * 测试保存列码值转换清洗规则
	 *
	 * TODO 被测方式未完成
	 *
	 * 正确数据访问1：
	 * 正确数据访问2：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveCVConversionInfo(){

	}

	/**
	 * 测试根据表ID查询针对该表设置的列合并信息
	 *
	 * 正确数据访问1：模拟查询为sys_user表设置的列合并信息
	 * 正确数据访问2：模拟查询为code_info表设置的列合并信息
	 * 错误的测试用例未达到三组: getColMergeInfo不会因为参数而导致访问失败，只会根据实际情况，按照不同的参数返回不同的值
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getColMergeInfo(){
		//正确数据访问1：模拟查询为sys_user表设置的列合并信息
		String rightStringOne = new HttpClient()
				.addData("tableId", SYS_USER_TABLE_ID)
				.post(getActionUrl("getColMergeInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Result rightData = rightResultOne.getDataForResult();
		assertThat("模拟查询为sys_user表设置的列合并信息，得到的结果有一条", rightData.getRowCount(), is(1));
		assertThat("模拟查询为sys_user表设置的列合并信息，合并后字段名称是user_mobile_admin", rightData.getString(0, "col_name").equalsIgnoreCase("user_mobile_admin"), is(true));
		assertThat("模拟查询为sys_user表设置的列合并信息，要合并的字段是user_mobile和useris_admin", rightData.getString(0, "old_name").equalsIgnoreCase("user_mobile和useris_admin"), is(true));
		assertThat("模拟查询为sys_user表设置的列合并信息，合并后的字段类型是varchar(512)", rightData.getString(0, "col_type").equalsIgnoreCase("varchar(512)"), is(true));

		//正确数据访问2：模拟查询为code_info表设置的列合并信息
		String rightStringTwo = new HttpClient()
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("getColMergeInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		Result rightDataTwo = rightResultTwo.getDataForResult();
		assertThat("模拟查询为code_info表设置的列合并信息，查询不到结果", rightDataTwo.isEmpty(), is(true));
	}

	/**
	 * 测试保存列合并信息
	 *
	 * 正确数据访问1：模拟对sys_user表设置好的列合并进行修改
	 * 正确数据访问2：正确数据访问2：模拟对code_info表设置列合并
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:saveColMergeInfo方法两个测试用例可以覆盖被测方法中所有的分支
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveColMergeInfo(){
		//正确数据访问1：模拟对sys_user表设置好的列合并进行修改
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long beforeColumnCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where column_id = ?", 1717171717L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			long beforeMergeCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_merge.TableName + " where col_merge_id = ?", 16161616L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("模拟对sys_user表设置好的列合并进行修改前，构造的初始化数据在table_column表中存在", beforeColumnCount == 1, is(true));
			assertThat("模拟对sys_user表设置好的列合并进行修改前，构造的初始化数据在column_merge表中存在", beforeMergeCount == 1, is(true));
		}

		List<Column_merge> columnMerges = new ArrayList<>();

		Column_merge columnMerge = new Column_merge();
		columnMerge.setCol_name("user_name_pwd");
		columnMerge.setOld_name("user_name和user_pwd");
		columnMerge.setCol_zhname("user_name_pwd_ch");
		columnMerge.setCol_type("varchar(512)");

		columnMerges.add(columnMerge);

		String rightStringOne = new HttpClient()
				.addData("columnMergeString", JSON.toJSONString(columnMerges))
				.addData("tableId", SYS_USER_TABLE_ID)
				.post(getActionUrl("saveColMergeInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			List<Table_column> tableColumns = SqlOperator.queryList(db, Table_column.class, "select * from " + Table_column.TableName
					+ " where colume_name in (select t1.colume_name from " + Table_column.TableName + " t1 " +
					" JOIN " + Column_merge.TableName + " t2 ON t1.table_id=t2.table_id " +
					" where t2.table_id = ? and t1.is_new = ? )", SYS_USER_TABLE_ID, IsFlag.Shi.getCode());
			long afterColumnCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where column_id = ?", 1717171717L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			long afterMergeCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_merge.TableName + " where col_merge_id = ?", 16161616L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("模拟对sys_user表设置好的列合并进行修改成功", tableColumns.size() == 1, is(true));
			assertThat("模拟对sys_user表设置好的列合并进行修改成功", afterColumnCount == 0, is(true));
			assertThat("模拟对sys_user表设置好的列合并进行修改成功", afterMergeCount == 0, is(true));

			int execute = SqlOperator.execute(db, "delete from " + Table_column.TableName + " where colume_name in (select t1.colume_name from " + Table_column.TableName + " t1 " +
					" JOIN " + Column_merge.TableName + " t2 ON t1.table_id=t2.table_id " +
					" where t2.table_id = ? and t1.is_new = ? ) ", SYS_USER_TABLE_ID, IsFlag.Shi.getCode());

			int execute1 = SqlOperator.execute(db, "delete from " + Column_merge.TableName + " where table_id = ?", SYS_USER_TABLE_ID);
			assertThat("模拟对sys_user表设置好的列合并进行修改成功，删除table_column表中新增的数据", execute ==1 ,is(true));
			assertThat("模拟对sys_user表设置好的列合并进行修改成功，删除column_merge表中新增的数据", execute1 ==1 ,is(true));

			SqlOperator.commitTransaction(db);
		}

		//正确数据访问2：模拟对code_info表设置列合并
		columnMerges.clear();
		Column_merge columnMergeTwo = new Column_merge();
		columnMergeTwo.setCol_name("ci_sp_name_remark");
		columnMergeTwo.setOld_name("ci_sp_name和ci_sp_remark");
		columnMergeTwo.setCol_zhname("ci_sp_name_remark_ch");
		columnMergeTwo.setCol_type("varchar(512)");

		columnMerges.add(columnMergeTwo);

		String rightStringTwo = new HttpClient()
				.addData("columnMergeString", JSON.toJSONString(columnMerges))
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("saveColMergeInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			List<Table_column> tableColumns = SqlOperator.queryList(db, Table_column.class, "select * from " + Table_column.TableName
					+ " where colume_name in (select t1.colume_name from " + Table_column.TableName + " t1 " +
					" JOIN " + Column_merge.TableName + " t2 ON t1.table_id = t2.table_id " +
					" and t1.colume_name = t2.col_name " +
					" where t2.table_id = ? and t1.is_new = ? )", CODE_INFO_TABLE_ID, IsFlag.Shi.getCode());
			long afterMergeCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_merge.TableName + " where table_id = ?", CODE_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("模拟对code_info表设置列合并成功", tableColumns.size(), is(1));
			assertThat("模拟对code_info表设置列合并成功", tableColumns.get(0).getColume_name().equalsIgnoreCase("ci_sp_name_remark"), is(true));
			assertThat("模拟对code_info表设置列合并成功", afterMergeCount == 1, is(true));

			int execute = SqlOperator.execute(db, "delete from " + Table_column.TableName + " where colume_name in (select t1.colume_name from " + Table_column.TableName + " t1 " +
					" JOIN " + Column_merge.TableName + " t2 ON t1.table_id=t2.table_id " +
					" and t1.colume_name = t2.col_name " +
					" where t2.table_id = ? and t1.is_new = ? ) ", CODE_INFO_TABLE_ID, IsFlag.Shi.getCode());
			assertThat("模拟对code_info表设置列合并成功，删除table_column表中新增的数据", execute == 1 ,is(true));

			int execute1 = SqlOperator.execute(db, "delete from " + Column_merge.TableName + " where table_id = ?", CODE_INFO_TABLE_ID);
			assertThat("模拟对code_info表设置列合并成功，删除column_merge表中新增的数据", execute1 ==1 ,is(true));

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 测试删除一条列合并信息
	 *
	 * 正确数据访问1：模拟删除对sys_user表设置的列合并规则
	 * 错误的测试用例未达到三组:deleteColMergeInfo一条测试用例已经可以覆盖代码中的所有分支
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void deleteColMergeInfo(){
		//模拟删除对sys_user表设置的列合并规则
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long beforeColumnCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName +
					" where colume_name = " +
					" (select t1.colume_name " +
					" from " + Table_column.TableName + " t1 " +
					" JOIN " + Column_merge.TableName + " t2 ON t1.table_id = t2.table_id " +
					" and t1.colume_name = t2.col_name " +
					" where t2.col_merge_id = ?) ", 16161616L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			long beforeMergeCount = SqlOperator.queryNumber(db, " select count(1) from " + Column_merge.TableName + " where col_merge_id = ? ", 16161616L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));

			assertThat("模拟删除对sys_user表设置的列合并规则之前，table_column表中的测试数据存在", beforeColumnCount == 1, is(true));
			assertThat("模拟删除对sys_user表设置的列合并规则之前，column_merge表中的测试数据存在", beforeMergeCount == 1, is(true));
		}

		String rightStringOne = new HttpClient()
				.addData("colMergeId", 16161616L)
				.post(getActionUrl("deleteColMergeInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long beforeColumnCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName +
					" where colume_name =" +
					" (select t1.colume_name " +
					" from " + Table_column.TableName + " t1 " +
					" JOIN " + Column_merge.TableName + " t2 ON t1.table_id = t2.table_id " +
					" and t1.colume_name = t2.col_name " +
					" where t2.col_merge_id = ?) ", 16161616L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			long beforeMergeCount = SqlOperator.queryNumber(db, " select count(1) from " + Column_merge.TableName + " where col_merge_id = ? ", 16161616L).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));

			assertThat("模拟删除对sys_user表设置的列合并规则之前，table_column表中的测试数据被删除了", beforeColumnCount == 0, is(true));
			assertThat("模拟删除对sys_user表设置的列合并规则之前，column_merge表中的测试数据被删除了", beforeMergeCount == 0, is(true));
		}
	}

	/**
	 * 测试保存所有表清洗优先级
	 *
	 * 正确数据访问1：将sys_user和code_info表的表清洗优先级设置为
	 *                  1、字符替换
	 *                  2、字符补齐
	 *                  3、字符trim
	 *                  4、字符合并
	 * 错误的测试用例未达到三组: 一个测试用例已经可以覆盖代码中所有可能出现的场景
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveAllTbCleanOrder(){
		//正确数据访问1：将sys_user和code_info表的表清洗优先级设置为1、字符替换 2、字符补齐 3、字符trim 4、字符合并
		JSONObject oriSort = new JSONObject();
		oriSort.put(CleanType.ZiFuBuQi.getCode(), 1);
		oriSort.put(CleanType.ZiFuTiHuan.getCode(), 2);
		oriSort.put(CleanType.ZiFuHeBing.getCode(), 3);
		oriSort.put(CleanType.ZiFuTrim.getCode(), 4);

		JSONObject newSort = new JSONObject();
		newSort.put(CleanType.ZiFuTiHuan.getCode(), 1);
		newSort.put(CleanType.ZiFuBuQi.getCode(), 2);
		newSort.put(CleanType.ZiFuTrim.getCode(), 3);
		newSort.put(CleanType.ZiFuHeBing.getCode(), 4);

		try(DatabaseWrapper db = new DatabaseWrapper()){
			List<Object> list = SqlOperator.queryOneColumnList(db, "select ti_or from " + Table_info.TableName + " where table_id in (?, ?) order by table_id", SYS_USER_TABLE_ID, CODE_INFO_TABLE_ID);
			assertThat("查询获得了两条数据", list.size() == 2, is(true));
			assertThat("第一条数据获取的是sys_user表的默认表清洗规则，结果符合预期", list.get(0).toString().equalsIgnoreCase(oriSort.toJSONString()), is(true));
			assertThat("第一条数据获取的是code_info表的默认表清洗规则，结果符合预期", list.get(1).toString().equalsIgnoreCase(oriSort.toJSONString()), is(true));
		}

		long[] tableIds = {SYS_USER_TABLE_ID, CODE_INFO_TABLE_ID};

		String rightStringOne = new HttpClient()
				.addData("tableIds", tableIds)
				.addData("sort", newSort.toJSONString())
				.post(getActionUrl("saveAllTbCleanOrder")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			List<Object> list = SqlOperator.queryOneColumnList(db, "select ti_or from " + Table_info.TableName + " where table_id in (?, ?) order by table_id", SYS_USER_TABLE_ID, CODE_INFO_TABLE_ID);
			assertThat("查询获得了两条数据", list.size() == 2, is(true));
			assertThat("第一条数据获取的是sys_user表修改后的清洗规则，结果符合预期", list.get(0).toString().equalsIgnoreCase(newSort.toJSONString()), is(true));
			assertThat("第一条数据获取的是code_info表修改后的清洗规则，结果符合预期", list.get(1).toString().equalsIgnoreCase(newSort.toJSONString()), is(true));
		}
	}

	/**
	 * 测试保存整表清洗优先级
	 *
	 * 正确数据访问1：构造sys_user表的全表清洗规则并保存
	 * 错误的数据访问1：尝试保存agent_info表的全表清洗规则，但是本次采集作业并没有配置采集agent_info表
	 * 错误的测试用例未达到三组:以上两种情况已经可以覆盖saveSingleTbCleanOrder表所有可能出现的情况了
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveSingleTbCleanOrder(){
		//正确数据访问1：构造sys_user表的全表清洗规则并保存
		JSONObject oriSort = new JSONObject();
		oriSort.put(CleanType.ZiFuBuQi.getCode(), 1);
		oriSort.put(CleanType.ZiFuTiHuan.getCode(), 2);
		oriSort.put(CleanType.ZiFuHeBing.getCode(), 3);
		oriSort.put(CleanType.ZiFuTrim.getCode(), 4);

		JSONObject newSort = new JSONObject();
		newSort.put(CleanType.ZiFuTiHuan.getCode(), 1);
		newSort.put(CleanType.ZiFuBuQi.getCode(), 2);
		newSort.put(CleanType.ZiFuTrim.getCode(), 3);
		newSort.put(CleanType.ZiFuHeBing.getCode(), 4);

		try(DatabaseWrapper db = new DatabaseWrapper()){
			List<Object> list = SqlOperator.queryOneColumnList(db, "select ti_or from " + Table_info.TableName + " where table_id = ?", SYS_USER_TABLE_ID);
			assertThat("查询获得了一条数据", list.size() == 1, is(true));
			assertThat("第一条数据获取的是sys_user表的默认表清洗规则，结果符合预期", list.get(0).toString().equalsIgnoreCase(oriSort.toJSONString()), is(true));
		}

		String rightStringOne = new HttpClient()
				.addData("tableId", SYS_USER_TABLE_ID)
				.addData("sort", newSort.toJSONString())
				.post(getActionUrl("saveSingleTbCleanOrder")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			List<Object> list = SqlOperator.queryOneColumnList(db, "select ti_or from " + Table_info.TableName + " where table_id = ?", SYS_USER_TABLE_ID);
			assertThat("查询获得了一条数据", list.size() == 1, is(true));
			assertThat("第一条数据获取的是sys_user表的默认表清洗规则，结果符合预期", list.get(0).toString().equalsIgnoreCase(newSort.toJSONString()), is(true));
		}

		//错误的数据访问1：尝试保存agent_info表的全表清洗规则，但是本次采集作业并没有配置采集agent_info表
		String wrongString = new HttpClient()
				.addData("tableId", UNEXPECTED_ID)
				.addData("sort", newSort.toJSONString())
				.post(getActionUrl("saveSingleTbCleanOrder")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试保存单个字段清洗优先级
	 *
	 * 正确数据访问1：尝试对sys_user表的role_id字段设置清洗规则为
	 *                  1、字符trim
	 *                  2、字符拆分
	 *                  3、码值转换
	 *                  4、时间转换
	 *                  5、字符替换
	 *                  6、字符补齐
	 * 错误的数据访问1：尝试对一个本次采集作业中没有的列设置清洗规则
	 * 错误的测试用例未达到三组:以上两个测试用例已经可以覆盖saveColCleanOrder方法所有可能出现的情况了
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveColCleanOrder(){
		//正确数据访问1：尝试对sys_user表的role_id字段设置清洗规则为
		JSONObject oriSort = new JSONObject();
		oriSort.put(CleanType.ZiFuBuQi.getCode(), 1);
		oriSort.put(CleanType.ZiFuTiHuan.getCode(), 2);
		oriSort.put(CleanType.ShiJianZhuanHuan.getCode(), 3);
		oriSort.put(CleanType.MaZhiZhuanHuan.getCode(), 4);
		oriSort.put(CleanType.ZiFuChaiFen.getCode(), 5);
		oriSort.put(CleanType.ZiFuTrim.getCode(), 6);

		JSONObject newSort = new JSONObject();
		newSort.put(CleanType.ZiFuTrim.getCode(), 1);
		newSort.put(CleanType.ZiFuChaiFen.getCode(), 2);
		newSort.put(CleanType.MaZhiZhuanHuan.getCode(), 3);
		newSort.put(CleanType.ShiJianZhuanHuan.getCode(), 4);
		newSort.put(CleanType.ZiFuTiHuan.getCode(), 5);
		newSort.put(CleanType.ZiFuBuQi.getCode(), 6);

		try(DatabaseWrapper db = new DatabaseWrapper()){
			List<Object> list = SqlOperator.queryOneColumnList(db, "select tc_or from " + Table_column.TableName + " where column_id = ?", 2004L);
			assertThat("查询获得了一条数据", list.size() == 1, is(true));
			assertThat("第一条数据获取的是sys_user表的role_id字段默认清洗规则，结果符合预期", list.get(0).toString().equalsIgnoreCase(oriSort.toJSONString()), is(true));
		}

		String rightStringOne = new HttpClient()
				.addData("columnId", 2004L)
				.addData("sort", newSort.toJSONString())
				.post(getActionUrl("saveColCleanOrder")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			List<Object> list = SqlOperator.queryOneColumnList(db, "select tc_or from " + Table_column.TableName + " where column_id = ?", 2004L);
			assertThat("查询获得了一条数据", list.size() == 1, is(true));
			assertThat("第一条数据获取的是sys_user表的role_id字段修改后的清洗规则，结果符合预期", list.get(0).toString().equalsIgnoreCase(newSort.toJSONString()), is(true));
		}

		//尝试对一个本次采集作业中没有的列设置清洗规则
		String wrongString = new HttpClient()
				.addData("columnId", 3008L)
				.addData("sort", newSort.toJSONString())
				.post(getActionUrl("saveColCleanOrder")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试保存列清洗信息
	 *
	 * 正确数据访问1：columnId为2002L，之前设置了字符补齐，但是保存的时候取消了字符补齐的勾选，同时做首尾去空
	 * 正确数据访问2：columnId为2005L，之前设置了字符替换，但是保存的时候取消了字符替换的勾选，同时做首尾去空
	 * 正确数据访问3：columnId为2011L，之前设置了日期格式化，但是保存的时候取消了日期格式化的勾选，同时做首尾去空
	 * 正确数据访问4：columnId为3003L，之前设置了列拆分，但是保存的时候取消了列拆分的勾选，同时做首尾去空
	 *
	 * 错误的测试用例未达到三组:上面三组测试用例是结合初始化数据进行的，比较有代表性的。
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveColCleanConfig(){
		List<ColumnCleanParam> columnCleanParams = new ArrayList<>();
		//正确数据访问1：columnId为2002L，之前设置了字符补齐，但是保存的时候取消了字符补齐的勾选，同时做首尾去空
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long beforeCompCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2002L, CleanType.ZiFuBuQi.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			long beforeTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2002L, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("在执行测试用例<正确数据访问1>之前，数据库中的数据符合预期", beforeCompCount == 1 && beforeTrimCount == 0, is(true));
		}

		ColumnCleanParam cleanParamOne = new ColumnCleanParam();

		cleanParamOne.setColumnId(2002L);
		cleanParamOne.setComplementFlag(false);
		cleanParamOne.setConversionFlag(false);
		cleanParamOne.setFormatFlag(false);
		cleanParamOne.setReplaceFlag(false);
		cleanParamOne.setSpiltFlag(false);
		cleanParamOne.setTrimFlag(true);

		columnCleanParams.add(cleanParamOne);

		String rightStringOne = new HttpClient()
				.addData("colCleanString", JSON.toJSONString(columnCleanParams))
				.post(getActionUrl("saveColCleanConfig")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long afterCompCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2002L, CleanType.ZiFuBuQi.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			long afterTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2002L, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("在执行测试用例<正确数据访问1>之后，数据库中的数据符合预期", afterCompCount == 0 && afterTrimCount == 1, is(true));
		}

		//正确数据访问2：columnId为2005L，之前设置了字符替换，但是保存的时候取消了字符替换的勾选，同时做首尾去空
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long beforeReplaceCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2005L, CleanType.ZiFuTiHuan.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			long beforeTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2005L, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("在执行测试用例<正确数据访问2>之前，数据库中的数据符合预期", beforeReplaceCount == 1 && beforeTrimCount == 0, is(true));
		}

		columnCleanParams.clear();

		ColumnCleanParam cleanParamTwo = new ColumnCleanParam();

		cleanParamTwo.setColumnId(2005L);
		cleanParamTwo.setComplementFlag(false);
		cleanParamTwo.setConversionFlag(false);
		cleanParamTwo.setFormatFlag(false);
		cleanParamTwo.setReplaceFlag(false);
		cleanParamTwo.setSpiltFlag(false);
		cleanParamTwo.setTrimFlag(true);

		columnCleanParams.add(cleanParamTwo);

		String rightStringTwo = new HttpClient()
				.addData("colCleanString", JSON.toJSONString(columnCleanParams))
				.post(getActionUrl("saveColCleanConfig")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long afterReplaceCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2005L, CleanType.ZiFuTiHuan.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			long afterTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2005L, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("在执行测试用例<正确数据访问2>之后，数据库中的数据符合预期", afterReplaceCount == 0 && afterTrimCount == 1, is(true));
		}

		//正确数据访问3：columnId为2011L，之前设置了日期格式化，但是保存的时候取消了日期格式化的勾选，同时做首尾去空
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long beforeFormatCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2011L, CleanType.ShiJianZhuanHuan.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			long beforeTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2011L, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("在执行测试用例<正确数据访问3>之前，数据库中的数据符合预期", beforeFormatCount == 1 && beforeTrimCount == 0, is(true));
		}

		columnCleanParams.clear();

		ColumnCleanParam cleanParamThree = new ColumnCleanParam();

		cleanParamThree.setColumnId(2011L);
		cleanParamThree.setComplementFlag(false);
		cleanParamThree.setConversionFlag(false);
		cleanParamThree.setFormatFlag(false);
		cleanParamThree.setReplaceFlag(false);
		cleanParamThree.setSpiltFlag(false);
		cleanParamThree.setTrimFlag(true);

		columnCleanParams.add(cleanParamThree);

		String rightStringThree = new HttpClient()
				.addData("colCleanString", JSON.toJSONString(columnCleanParams))
				.post(getActionUrl("saveColCleanConfig")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long afterFormatCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2011L, CleanType.ShiJianZhuanHuan.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			long afterTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2011L, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("在执行测试用例<正确数据访问3>之后，数据库中的数据符合预期", afterFormatCount == 0 && afterTrimCount == 1, is(true));
		}

		//正确数据访问4：columnId为3003L，之前设置了列拆分，但是保存的时候取消了列拆分的勾选，同时做首尾去空
		try(DatabaseWrapper db = new DatabaseWrapper()){
			List<Table_column> tableColumns = SqlOperator.queryList(db, Table_column.class, "select * from " + Table_column.TableName + " where colume_name in" +
							" (select t1.colume_name from "+ Table_column.TableName +" t1" +
							" JOIN " + Column_split.TableName + " t2 ON t1.colume_name = t2.col_name " +
							" JOIN " + Column_clean.TableName + " t3 ON t2.col_clean_id = t3.col_clean_id " +
							" WHERE t2.col_clean_id = ? and t2.column_id = ? and t1.table_id = ? and t1.is_new = ?) ",
					101010102L, 3003L, CODE_INFO_TABLE_ID, IsFlag.Shi.getCode());
			assertThat("在执行测试用例<正确数据访问4>之前，拆分为ci、sp、classname的列在table_column表中存在", tableColumns.size() == 3, is(true));
			for(Table_column tableColumn : tableColumns){
				if(tableColumn.getColumn_id() == 141414L){
					assertThat("在执行测试用例<正确数据访问4>之前，拆分为ci的列在table_column表中存在", tableColumn.getColume_name().equalsIgnoreCase("ci"), is(true));
				}else if(tableColumn.getColumn_id() == 151515L){
					assertThat("在执行测试用例<正确数据访问4>之前，拆分为sp的列在table_column表中存在", tableColumn.getColume_name().equalsIgnoreCase("sp"), is(true));
				}else if(tableColumn.getColumn_id() == 161616L){
					assertThat("在执行测试用例<正确数据访问4>之前，拆分为classname的列在table_column表中存在", tableColumn.getColume_name().equalsIgnoreCase("classname"), is(true));
				}else{
					assertThat("在执行测试用例<正确数据访问4>之前，在table_column表中查询到了不符合预期的列" + tableColumn.getColume_name(), false, is(true));
				}
			}
			long beforeDelSpCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id in (101010103, 101010104, 101010105) ").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("在执行测试用例<正确数据访问4>之前，列拆分信息在column_split表中存在", beforeDelSpCount == 3, is(true));

			long beforeDelColCleanCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and clean_type = ?", 3003L, CleanType.ZiFuChaiFen.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("在执行测试用例<正确数据访问4>之前，列拆分信息在column_clean表中不存在", beforeDelColCleanCount == 1, is(true));
			long beforeTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 3003L, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("在执行测试用例<正确数据访问4>之前，列首尾去空存在", beforeTrimCount == 0, is(true));
		}

		columnCleanParams.clear();

		ColumnCleanParam cleanParamFour = new ColumnCleanParam();

		cleanParamFour.setColumnId(3003L);
		cleanParamFour.setComplementFlag(false);
		cleanParamFour.setConversionFlag(false);
		cleanParamFour.setFormatFlag(false);
		cleanParamFour.setReplaceFlag(false);
		cleanParamFour.setSpiltFlag(false);
		cleanParamFour.setTrimFlag(true);

		columnCleanParams.add(cleanParamFour);

		String rightStringFour = new HttpClient()
				.addData("colCleanString", JSON.toJSONString(columnCleanParams))
				.post(getActionUrl("saveColCleanConfig")).getBodyString();
		ActionResult rightResultFour = JsonUtil.toObjectSafety(rightStringFour, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultFour.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			List<Table_column> tableColumns = SqlOperator.queryList(db, Table_column.class, "select * from " + Table_column.TableName + " where colume_name in" +
							" (select t1.colume_name from "+ Table_column.TableName +" t1" +
							" JOIN " + Column_split.TableName + " t2 ON t1.colume_name = t2.col_name " +
							" JOIN " + Column_clean.TableName + " t3 ON t2.col_clean_id = t3.col_clean_id " +
							" WHERE t2.col_clean_id = ? and t2.column_id = ? and t1.table_id = ? and t1.is_new = ?) ",
					101010102L, 3003L, CODE_INFO_TABLE_ID, IsFlag.Shi.getCode());
			assertThat("在执行测试用例<正确数据访问4>之后，拆分为ci、sp、classname的列在table_column表中不存在", tableColumns.size() == 0, is(true));
			long afterDelSpCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id in (101010103, 101010104, 101010105) ").orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("在执行测试用例<正确数据访问4>之后，列拆分信息在column_split表中不存在", afterDelSpCount == 0, is(true));

			long afterDelColCleanCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and clean_type = ?", 3003L, CleanType.ZiFuChaiFen.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("在执行测试用例<正确数据访问4>之后，列拆分信息在column_clean表中不存在", afterDelColCleanCount == 0, is(true));
			long afterTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 3003L, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("在执行测试用例<正确数据访问4>之后，列首尾去空存在", afterTrimCount == 1, is(true));
		}
	}

	/**
	 * 保存配置数据清洗页面信息
	 *
	 * 正确数据访问1：colSetId为1001L，tableId为7001L，之前设置了字符补齐，但是保存的时候取消了字符补齐的勾选，同时做首尾去空
	 * 正确数据访问2：colSetId为1001L，tableId为7001L，之前设置了字符替换，但是保存的时候取消了字符替换的勾选，同时做首尾去空
	 * 错误的测试用例未达到三组: 上面两组测试用例是结合初始化数据进行的，比较有代表性的。
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveDataCleanConfig(){
		//正确数据访问1：colSetId为1001L，tableId为7001L，之前设置了字符补齐，但是保存的时候取消了字符补齐的勾选，同时做首尾去空
		List<TableCleanParam> tableCleanParams = new ArrayList<>();

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long beforeCompCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " WHERE table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuBuQi.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			long beforeTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " WHERE table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("在执行测试用例<正确数据访问1>之前，数据库中的数据符合预期", beforeCompCount == 1 && beforeTrimCount == 0, is(true));
		}

		TableCleanParam cleanParamOne = new TableCleanParam();
		cleanParamOne.setTableId(SYS_USER_TABLE_ID);
		cleanParamOne.setComplementFlag(false);
		cleanParamOne.setReplaceFlag(true);
		cleanParamOne.setTableName("sys_user");
		cleanParamOne.setTrimFlag(true);

		tableCleanParams.add(cleanParamOne);

		String rightStringOne = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("tbCleanString", JSON.toJSONString(tableCleanParams))
				.post(getActionUrl("saveDataCleanConfig")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));
		Integer returnValueOne = (Integer) rightResultOne.getData();
		assertThat(returnValueOne == FIRST_DATABASESET_ID, is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long afterCompCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " WHERE table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuBuQi.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			long afterTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " WHERE table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("在执行测试用例<正确数据访问1>之后，数据库中的数据符合预期", afterCompCount == 0 && afterTrimCount == 1, is(true));

			int execute = SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuTrim.getCode());
			assertThat("在执行测试用例<正确数据访问1>之后，删除新增的对sys_user表的首尾去空操作", execute == 1, is(true));

			SqlOperator.commitTransaction(db);
		}

		//正确数据访问2：colSetId为1001L，tableId为7001L，之前设置了字符替换，但是保存的时候取消了字符替换的勾选，同时做首尾去空
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long beforeReplaceCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " WHERE table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			long beforeTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " WHERE table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("在执行测试用例<正确数据访问2>之前，数据库中的数据符合预期", beforeReplaceCount == 1 && beforeTrimCount == 0, is(true));
		}

		tableCleanParams.clear();

		TableCleanParam cleanParamTwo = new TableCleanParam();
		cleanParamTwo.setTableId(SYS_USER_TABLE_ID);
		cleanParamTwo.setComplementFlag(true);
		cleanParamTwo.setReplaceFlag(false);
		cleanParamTwo.setTableName("sys_user");
		cleanParamTwo.setTrimFlag(true);

		tableCleanParams.add(cleanParamTwo);

		String rightStringTwo = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("tbCleanString", JSON.toJSONString(tableCleanParams))
				.post(getActionUrl("saveDataCleanConfig")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		Integer returnValueTwo = (Integer) rightResultTwo.getData();
		assertThat(returnValueTwo == FIRST_DATABASESET_ID, is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long afterReplaceCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " WHERE table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			long afterTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " WHERE table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
			assertThat("在执行测试用例<正确数据访问2>之后，数据库中的数据符合预期", afterReplaceCount == 0 && afterTrimCount == 1, is(true));

			int execute = SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuTrim.getCode());
			assertThat("在执行测试用例<正确数据访问2>之后，删除新增的对sys_user表的首尾去空操作", execute == 1, is(true));

			SqlOperator.commitTransaction(db);
		}
	}

	@After
	public void after(){
		InitAndDestDataForCleanConf.after();
	}
}
