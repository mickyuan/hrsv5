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
import hrds.commons.codes.CharSplitType;
import hrds.commons.codes.CleanType;
import hrds.commons.codes.FillingType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.key.PrimayKeyGener;
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
	 *      6、collect_job_classify表：有3条数据，classify_id为10086L、10010L，12306L，agent_id分别为7001L、7002L,7001L，user_id为9997L
	 *      7、table_info表测试数据共4条，databaseset_id为1001
	 *          7-1、table_id:7001,table_name:sys_user,按照画面配置信息进行采集,按照画面配置信息进行采集，并且配置了单表过滤SQL,select * from sys_user where user_id = 2001，不进行并行抽取
	 *          7-2、table_id:7002,table_name:code_info,按照画面配置信息进行采集,按照画面配置信息进行采集,进行并行抽取，分页SQL为select * from code_info limit 10
	 *          7-3、table_id:7003,table_name:agent_info,按照自定义SQL进行采集,按照自定义SQL进行采集，不进行并行抽取
	 *          7-4、table_id:7004,table_name:data_source,按照自定义SQL进行采集,按照自定义SQL进行采集，不进行并行抽取
	 *      8、table_column表测试数据：只有在画面上进行配置的采集表才会向table_column表中保存数据
	 *          8-1、column_id为2001-2011，模拟采集了sys_user表的前10个列，列名为user_id，create_id，dep_id，role_id，
	 *               user_name，user_password，user_email，user_mobile，useris_admin，user_type，和一个login_date列,设置了remark字段，也就是采集顺序，分别是1、2、3、4、5、6、7、8、9、10、11
	 *          8-2、column_id为3001-3005，模拟采集了code_info表的所有列，列名为ci_sp_code，ci_sp_class，ci_sp_classname，
	 *               ci_sp_name，ci_sp_remark
     *          8-3、模拟自定义采集agent_info表的agent_id，agent_name，agent_type三个字段
	 *          8-4、模拟自定义采集data_source表的source_id，datasource_number，datasource_name三个字段
	 *      9、table_clean表测试数据：
	 *          9-1、对sys_user表设置一个整表字符补齐规则
	 *          9-2、对sys_user表设置一个整表字符替换规则
     *      10、column_clean表测试数据
	 *          10-1、对sys_user表的create_id、dep_id列设置列字符补齐
	 *          10-2、对sys_user表的user_name列设置列字符替换
	 *          10-3、对sys_user表的login_date列设置了日期格式化
	 *          10-4、对sys_user表的user_type列设置码值转换
     *      11、clean_parameter表测试数据
	 *          11-1、对databaseset_id为1001的数据库直连采集作业设置一个全表的字符替换和字符补齐规则
     *      12、column_split表测试数据
	 *          12-1、对code_info表的ci_sp_name列设置字段拆分，按照偏移量，拆分为ci_s、p_name两列
	 *          12-2、对code_info表的ci_sp_classname列设置字段拆分，按照下划线拆分ci、sp、classname三列
     *      13、由于配置了列拆分，需要把拆分后的列加入到table_column表中
	 *      14、column_merge表测试数据
	 *          14-1、对sys_user表中的user_mobile和useris_admin合并成列，名叫user_mobile_admin
	 *      15、由于配置了列合并，需要把合并后的列入到table_column表中
	 *      16、构造orig_syso_info表测试数据，里面是当前系统中所有的码值信息,共有三条数据
	 *      17、构造orig_code_info表测试数据,共有三条数据
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

	/**
	 * 测试根据数据库设置ID获得清洗规则配置页面初始信息
	 *
	 * 正确数据访问1：使用正确的colSetId访问，应该可以拿到4条数据
	 * 错误的数据访问1：使用错误的colSetId访问，应该拿不到任何数据，但是不会报错，访问正常返回
	 * 错误的测试用例未达到三组:getInitInfo只有一个参数
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getCleanConfInfo(){
		//正确数据访问1：使用正确的colSetId访问，应该可以拿到4条数据
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getCleanConfInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Result rightData = rightResult.getDataForResult();
		assertThat("根据测试数据，输入正确的colSetId查询到的非自定义采集表总条数应该有4条", rightData.getRowCount(), is(4));

		//错误的数据访问1：使用错误的colSetId访问，应该拿不到任何数据，但是不会报错，访问正常返回
		long wrongColSetId = 99999L;
		String wrongString = new HttpClient()
				.addData("colSetId", wrongColSetId)
				.post(getActionUrl("getCleanConfInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(true));
		Result wrongData = wrongResult.getDataForResult();
		assertThat("根据测试数据，输入错误的colSetId查询到的非自定义采集表信息应该有0条，但是HTTP访问成功返回", wrongData.getRowCount(), is(0));
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
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from "+ Table_clean.TableName +" where table_id = ? and clean_type = ? and character_filling = ?", SYS_USER_TABLE_ID, CleanType.ZiFuBuQi.getCode(), StringUtil.string2Unicode("wzc")).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("新增之前为sys_user表构造的字符补齐测试数据是存在的", oldCount == 1, is(true));
		}

		String rightStringOne = new HttpClient()
				.addData("table_id", SYS_USER_TABLE_ID)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_length" ,6)
				.addData("filling_type", FillingType.HouBuQi.getCode())
				.post(getActionUrl("saveSingleTbCompletionInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long count = SqlOperator.queryNumber(db, "select count(1) from "+ Table_clean.TableName +" where table_id = ? and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuBuQi.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("新增成功，构造的整表字符补齐测试数据被成功保存", count == 1L, is(true));
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from "+ Table_clean.TableName +" where table_id = ? and clean_type = ? and character_filling = ?", SYS_USER_TABLE_ID, CleanType.ZiFuBuQi.getCode(), "wzc").orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("新增成功，之前为sys_user表构造的字符补齐测试数据已经被删除了", oldCount == 0, is(true));

			//断言该表下所有的字段全部设置了字符补齐
			Result colCompResult = SqlOperator.queryResult(db, "select cc.filling_type, cc.character_filling, cc.filling_length from " +
					Column_clean.TableName + " cc join " + Table_column.TableName + " tc on cc.column_id = tc.column_id " +
					" where tc.table_id = ? and cc.filling_length = ? and cc.clean_type = ?", SYS_USER_TABLE_ID, 6, CleanType.ZiFuBuQi.getCode());

			List<Object> columnIds = SqlOperator.queryOneColumnList(db, "select column_id from " + Table_column.TableName + " tc" +
					" where tc.table_id = ?", SYS_USER_TABLE_ID);

			assertThat("sys_user表下的字段共有" + columnIds.size() + "条", colCompResult.getRowCount() == columnIds.size(), is(true));
			for(int i = 0; i < colCompResult.getRowCount(); i++){
				assertThat("新增单表字符补齐成功后，sys_user表下所有的字段补齐方式为后补齐", colCompResult.getString(i, "filling_type"), is(FillingType.HouBuQi.getCode()));
				assertThat("新增单表字符补齐成功后，sys_user表下所有的字段补齐字符为beyond", colCompResult.getString(i, "character_filling"), is(StringUtil.string2Unicode("beyond")));
			}

			int deleteCount = SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? and clean_type = ? and character_filling = ?", SYS_USER_TABLE_ID, CleanType.ZiFuBuQi.getCode(), StringUtil.string2Unicode("beyond"));
			assertThat("测试完成后，删除新增成功的整表字符补齐测试数据", deleteCount == 1, is(true));

			StringBuilder strSB = new StringBuilder("delete from " + Column_clean.TableName + " where column_id in ( ");
			for(int j = 0; j < columnIds.size(); j++){
				strSB.append((long)columnIds.get(j));
				if (j != columnIds.size() - 1)
					strSB.append(",");
			}
			strSB.append(" ) and clean_type = ?");

			SqlOperator.execute(db, strSB.toString(), CleanType.ZiFuBuQi.getCode());

			SqlOperator.commitTransaction(db);
		}

		//错误的数据访问1：构造没有补齐字符的访问
		String wrongStringOne = new HttpClient()
				.addData("table_id", SYS_USER_TABLE_ID)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("filling_length" ,6)
				.addData("filling_type", FillingType.HouBuQi.getCode())
				.post(getActionUrl("saveSingleTbCompletionInfo")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));

		//错误的数据访问2：构造没有补齐长度的访问
		String wrongStringTwo = new HttpClient()
				.addData("table_id", SYS_USER_TABLE_ID)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_type", FillingType.HouBuQi.getCode())
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
				.addData("filling_type", FillingType.HouBuQi.getCode())
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
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and clean_type = ? and character_filling = ?", 2002L, CleanType.ZiFuBuQi.getCode(), StringUtil.string2Unicode("wzc")).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("新增列字符补齐之前为sys_user表的create_id列构造的字符补齐测试数据是存在的", oldCount == 1, is(true));
		}

		String rightString = new HttpClient()
				.addData("column_id", 2002L)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_length" ,6)
				.addData("filling_type", FillingType.HouBuQi.getCode())
				.post(getActionUrl("saveColCompletionInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long count = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and clean_type = ? and character_filling = ?", 2002L, CleanType.ZiFuBuQi.getCode(), StringUtil.string2Unicode("beyond")).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("新增列字符补齐成功，构造的测试数据被成功保存", count == 1, is(true));
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and clean_type = ? and character_filling = ?", 2002L, CleanType.ZiFuBuQi.getCode(), StringUtil.string2Unicode("wzc")).orElseThrow(() -> new BusinessException("SQL查询错误"));
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
				.addData("filling_type", FillingType.HouBuQi.getCode())
				.post(getActionUrl("saveColCompletionInfo")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));

		//错误的数据访问2：构造没有补齐长度的访问
		String wrongStringTwo = new HttpClient()
				.addData("column_id", SYS_USER_TABLE_ID)
				.addData("clean_type", CleanType.ZiFuBuQi.getCode())
				.addData("character_filling", "beyond")
				.addData("filling_type", FillingType.HouBuQi.getCode())
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
				.addData("filling_type", FillingType.HouBuQi.getCode())
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

		Map<String, Object> rightDataOne = rightResultOne.getDataForMap(String.class, Object.class);
		assertThat("columnId为2003的列字符补齐信息中，col_clean_id为33333", rightDataOne.get("col_clean_id"), is(33333));
		assertThat("columnId为2003的列字符补齐信息中，补齐类型为后补齐", rightDataOne.get("filling_type"), is(FillingType.HouBuQi.getCode()));
		assertThat("columnId为2003的列字符补齐信息中，补齐字符为空格", rightDataOne.get("character_filling"), is(" "));
		assertThat("columnId为2003的列字符补齐信息中，补齐长度为1", rightDataOne.get("filling_length"), is(1));
		assertThat("columnId为2003的列字符补齐信息中，columnId为2003", rightDataOne.get("column_id"), is(2003));

		//正确数据访问2：构造正确的columnId进行测试(2001，对该列没有设置过字符补齐，但是对其所在的表设置过整表字符补齐)
		String rightStringTwo = new HttpClient()
				.addData("columnId", 2001L)
				.post(getActionUrl("getColCompletionInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		Map<String, Object> rightDataTwo = rightResultTwo.getDataForMap(String.class, Object.class);
		assertThat("columnId为2001的字段，没有设置列字符补齐，所以其所在表sys_user的字符补齐信息中，table_clean_id为11111", rightDataTwo.get("table_clean_id"), is(11111));
		assertThat("columnId为2001的字段，没有设置列字符补齐，所以其所在表sys_user的字符补齐信息中，补齐类型为前补齐", rightDataTwo.get("filling_type"), is(FillingType.QianBuQi.getCode()));
		assertThat("columnId为2001的字段，没有设置列字符补齐，所以其所在表sys_user的字符补齐信息中，补齐字符为wzc", rightDataTwo.get("character_filling"), is("wzc"));
		assertThat("columnId为2001的字段，没有设置列字符补齐，所以其所在表sys_user的字符补齐信息中，补齐长度为3", rightDataTwo.get("filling_length"), is(3));

		//正确数据访问3：构造没有设置过列字符补齐，也没有设置过表字符补齐的columnId进行测试(3004)，拿到的应该是空的数据集
		String rightStringThree = new HttpClient()
				.addData("columnId", 3004L)
				.post(getActionUrl("getColCompletionInfo")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));
		Map<String, Object> rightDataThree = rightResultThree.getDataForMap(String.class, Object.class);
		assertThat("columnId为3004的字段，没有设置列字符补齐，其所在表也没有设置字符补齐，所以访问得到的数据集没有数据", rightDataThree.isEmpty(), is(true));
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

		Map<String, Object> rightDataOne = rightResultOne.getDataForMap(String.class, Object.class);
		assertThat("tableId为7001的表字符补齐信息中，table_clean_id为11111", rightDataOne.get("table_clean_id"), is(11111));
		assertThat("tableId为7001的表字符补齐信息中，补齐类型为前补齐", rightDataOne.get("filling_type"), is(FillingType.QianBuQi.getCode()));
		assertThat("tableId为7001的表字符补齐信息中，补齐字符为wzc", rightDataOne.get("character_filling"), is("wzc"));
		assertThat("tableId为7001的表字符补齐信息中，补齐长度为3", rightDataOne.get("filling_length"), is(3));

		//正确数据访问2：构造正确的tableId进行测试(7002，没有对该表设置过整表字符补齐)，得不到数据
		String rightStringTwo = new HttpClient()
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("getTbCompletionInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		Map<String, Object> rightDataTwo = rightResultTwo.getDataForMap(String.class, Object.class);
		assertThat("tableId为7001的表，没有设置字符补齐，所以访问得到的数据集没有数据", rightDataTwo.isEmpty(), is(true));
	}

	/**
	 * 测试保存单个表的字符替换规则
	 *
	 * 正确数据访问1：构造正常的字符替换规则进行保存，由于后端接收json格式字符串，测试用例中使用List集合模拟保存两条字符替换规则
	 * 正确数据访问2：构造特殊字符，如回车
	 * 错误的数据访问1：保存时，不传递原字符
	 * 错误的数据访问2：保存时，不传递替换后字符
	 * 错误的测试用例未达到三组:saveSingleTbReplaceInfo方法目前还没有明确是否要对原字符和替换后的字符进行校验
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveSingleTbReplaceInfo(){
		//正确数据访问1：构造正常的字符替换规则进行保存
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " where table_id = ? and clean_type = ? and field = ? and replace_feild = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode(), StringUtil.string2Unicode("wzc"), StringUtil.string2Unicode("wqp")).orElseThrow(() -> new BusinessException("SQL查询错误"));
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
			long count = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " where table_id = ? and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("新增整表字符替换成功，构造的测试数据被成功保存", count == 2, is(true));
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " where table_id = ? and field = ? and replace_feild = ?", SYS_USER_TABLE_ID, StringUtil.string2Unicode("wzc"), StringUtil.string2Unicode("wqp")).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("新增整表字符替换成功，之前为sys_user表构造的字符替换测试数据已经被删除了", oldCount == 0, is(true));

			//断言该表下所有的字段全部设置了字符替换
			Result colReplaResultOne = SqlOperator.queryResult(db, "select cc.field, cc.replace_feild from " + Column_clean.TableName + " cc join " + Table_column.TableName
					+ " tc on cc.column_id = tc.column_id where tc.table_id = ? and clean_type = ? and cc.field = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode(), StringUtil.string2Unicode("beyond"));

			Result colReplaResultTwo = SqlOperator.queryResult(db, "select cc.field, cc.replace_feild from " + Column_clean.TableName + " cc join " + Table_column.TableName
					+ " tc on cc.column_id = tc.column_id where tc.table_id = ? and clean_type = ? and cc.field = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode(), StringUtil.string2Unicode("alibaba"));

			List<Object> columnIds = SqlOperator.queryOneColumnList(db, "select column_id from " + Table_column.TableName + " where table_id = ?", SYS_USER_TABLE_ID);
			assertThat("该表下所有的字段全部设置了字符替换", colReplaResultOne.getRowCount() == columnIds.size(), is(true));
			assertThat("该表下所有的字段全部设置了字符替换", colReplaResultTwo.getRowCount() == columnIds.size(), is(true));

			for(int i = 0; i < colReplaResultOne.getRowCount(); i++){
				assertThat("该表下所有的字段全部设置了字符替换", colReplaResultOne.getString(i, "replace_feild"), is(StringUtil.string2Unicode("hongzhi")));
			}

			for(int i = 0; i < colReplaResultTwo.getRowCount(); i++){
				assertThat("该表下所有的字段全部设置了字符替换", colReplaResultTwo.getString(i, "replace_feild"), is(StringUtil.string2Unicode("tencent")));
			}

			StringBuilder strSBCol = new StringBuilder("delete from " + Column_clean.TableName + " where column_id in ( ");
			for(int j = 0; j < columnIds.size(); j++){
				strSBCol.append((long)columnIds.get(j));
				if (j != columnIds.size() - 1)
					strSBCol.append(",");
			}
			strSBCol.append(" ) and clean_type = ?");

			SqlOperator.execute(db, strSBCol.toString(), CleanType.ZiFuTiHuan.getCode());

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
			long count = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " where table_id = ? and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("新增整表字符替换成功，构造的测试数据被成功保存", count == 1, is(true));
			Result result = SqlOperator.queryResult(db, "select clean_type, field, replace_feild, table_id from " + Table_clean.TableName + " where table_id = ? and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode());
			assertThat(result.getRowCount() == 1, is(true));
			assertThat(CleanType.ofEnumByCode(result.getString(0, "clean_type")) == CleanType.ZiFuTiHuan, is(true));
			assertThat(result.getString(0, "field").equals(StringUtil.string2Unicode("\n")), is(true));
			assertThat(result.getString(0, "replace_feild").equals(StringUtil.string2Unicode("|")), is(true));

			//断言该表下所有的字段全部设置了字符替换
			Result colReplaResult = SqlOperator.queryResult(db, "select cc.field, cc.replace_feild from " + Column_clean.TableName + " cc join " + Table_column.TableName
					+ " tc on cc.column_id = tc.column_id where tc.table_id = ? and clean_type = ? and cc.replace_feild = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode(), StringUtil.string2Unicode("|"));

			List<Object> columnIds = SqlOperator.queryOneColumnList(db, "select column_id from " + Table_column.TableName + " where table_id = ?", SYS_USER_TABLE_ID);
			assertThat("该表下所有的字段全部设置了字符替换", colReplaResult.getRowCount() == columnIds.size(), is(true));

			for(int i = 0; i < colReplaResult.getRowCount(); i++){
				assertThat("该表下所有的字段全部设置了字符替换", colReplaResult.getString(i, "field"), is(StringUtil.string2Unicode("\n")));
			}

			StringBuilder strSBCol = new StringBuilder("delete from " + Column_clean.TableName + " where column_id in ( ");
			for(int j = 0; j < columnIds.size(); j++){
				strSBCol.append((long)columnIds.get(j));
				if (j != columnIds.size() - 1)
					strSBCol.append(",");
			}
			strSBCol.append(" ) and clean_type = ?");

			SqlOperator.execute(db, strSBCol.toString(), CleanType.ZiFuTiHuan.getCode());

			int deleteCount = SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode());
			assertThat("测试完成后，删除新增成功的整表字符替换测试数据", deleteCount == 1, is(true));

			SqlOperator.commitTransaction(db);
		}

		//错误的数据访问1：保存时，不传递原字符
		replaceList.clear();
		Table_clean wrongTcOne = new Table_clean();
		wrongTcOne.setTable_id(SYS_USER_TABLE_ID);
		wrongTcOne.setClean_type(CleanType.ZiFuTiHuan.getCode());
		wrongTcOne.setReplace_feild("|");
		replaceList.add(wrongTcOne);

		String wrongStringOne = new HttpClient()
				.addData("replaceString", JSON.toJSONString(replaceList))
				.addData("tableId", SYS_USER_TABLE_ID)
				.post(getActionUrl("saveSingleTbReplaceInfo")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));

		//错误的数据访问2：保存时，不传递替换后字符
		replaceList.clear();
		Table_clean wrongTcTwo = new Table_clean();
		wrongTcTwo.setTable_id(SYS_USER_TABLE_ID);
		wrongTcTwo.setField("\n");
		wrongTcTwo.setClean_type(CleanType.ZiFuTiHuan.getCode());
		replaceList.add(wrongTcTwo);

		String wrongStringTwo = new HttpClient()
				.addData("replaceString", JSON.toJSONString(replaceList))
				.addData("tableId", SYS_USER_TABLE_ID)
				.post(getActionUrl("saveSingleTbReplaceInfo")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));
	}

	/**
	 * 测试保存单个字段的字符替换规则
	 *
	 * 正确数据访问1：构造正确的列字符替换规则进行保存
	 * 错误的数据访问1：保存时，不传递原字符
	 * 错误的数据访问2：保存时，不传递替换后字符
	 * 错误的测试用例未达到三组:saveColReplaceInfo方法目前还没有明确是否要对原字符和替换后的字符进行校验
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveColReplaceInfo(){
		//正确数据访问1：构造正确的列字符替换规则进行保存
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and clean_type = ? and field = ? and replace_feild = ?", 2005L, CleanType.ZiFuTiHuan.getCode(), StringUtil.string2Unicode("ceshi"), StringUtil.string2Unicode("test")).orElseThrow(() -> new BusinessException("SQL查询错误"));
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
			long count = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and clean_type = ?", 2005L, CleanType.ZiFuTiHuan.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("新增列字符替换成功，构造的测试数据被成功保存", count == 2, is(true));
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and clean_type = ? and field = ? and replace_feild = ?", 2005L, CleanType.ZiFuTiHuan.getCode(), StringUtil.string2Unicode("ceshi"), StringUtil.string2Unicode("test")).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("新增列字符补齐成功之后，为sys_user表的user_name列构造的字符替换测试数据被删除了", oldCount == 0, is(true));
			Result result = SqlOperator.queryResult(db, "select clean_type, replace_feild from " + Column_clean.TableName + " where column_id = ? and field = ?", 2005L, StringUtil.string2Unicode("alibaba"));
			assertThat(result.getRowCount() == 1, is(true));
			assertThat(CleanType.ofEnumByCode(result.getString(0, "clean_type")) == CleanType.ZiFuTiHuan, is(true));
			assertThat(result.getString(0, "replace_feild").equals(StringUtil.string2Unicode("tencent")), is(true));
			int deleteCount = SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? and clean_type = ?", 2005L, CleanType.ZiFuTiHuan.getCode());
			assertThat("测试完成后，删除新增成功的整表字符替换测试数据", deleteCount == 2, is(true));

			SqlOperator.commitTransaction(db);
		}

		//错误的数据访问1：保存时，不传递原字符
		replaceList.clear();
		Column_clean wrongTcOne = new Column_clean();
		wrongTcOne.setColumn_id(2005L);
		wrongTcOne.setClean_type(CleanType.ZiFuTiHuan.getCode());
		wrongTcOne.setReplace_feild("|");
		replaceList.add(wrongTcOne);

		String wrongStringOne = new HttpClient()
				.addData("replaceString", JSON.toJSONString(replaceList))
				.addData("columnId", 2005L)
				.post(getActionUrl("saveColReplaceInfo")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));

		//错误的数据访问2：保存时，不传递替换后字符
		replaceList.clear();
		Column_clean wrongTcTwo = new Column_clean();
		wrongTcTwo.setColumn_id(SYS_USER_TABLE_ID);
		wrongTcTwo.setField("\n");
		wrongTcTwo.setClean_type(CleanType.ZiFuTiHuan.getCode());
		replaceList.add(wrongTcTwo);

		String wrongStringTwo = new HttpClient()
				.addData("replaceString", JSON.toJSONString(replaceList))
				.addData("columnId", 2005L)
				.post(getActionUrl("saveColReplaceInfo")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));
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

		Result columnInfoOne = rightResult.getDataForResult();

		assertThat("尝试获取tableId为7002的表的所有列，得到的全部数据是11条", columnInfoOne.getRowCount(), is(11));
		assertThat("尝试获取tableId为7002的表的所有列，create_id做了字符补齐", columnInfoOne.getInt(1, "compflag"), is(1));
		assertThat("尝试获取tableId为7002的表的所有列，dep_id做了字符补齐", columnInfoOne.getInt(2, "compflag"), is(1));
		assertThat("尝试获取tableId为7002的表的所有列，user_name做了字符替换", columnInfoOne.getInt(4, "replaceflag"), is(1));

		//错误的数据访问1：尝试获取tableId为7006的表的所有列，由于初始化时没有构造tableId为999999999的数据，所以拿不到数据
		String wrongString = new HttpClient()
				.addData("tableId", UNEXPECTED_ID)
				.post(getActionUrl("getColumnInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(true));

		Result wrongData = wrongResult.getDataForResult();

		assertThat("尝试获取tableId为999999999的表的所有列，得到的结果集为空", wrongData.getRowCount(), is(0));
	}

	/**
	 * 测试保存所有表清洗设置字符补齐和字符替换
	 *
	 * 正确数据访问1：模拟只设置全表字符补齐
	 * 正确数据访问2：模拟只设置全表字符替换(设置两条)
	 * 正确数据访问3：模拟既设置全表字符补齐，又设置全表字符替换
	 * 错误数据访问1：模拟只设置全表字符补齐，但是补齐方式是3，这样访问不会成功
	 * 错误数据访问2：模拟只设置全表字符替换，但是缺少原字符串
	 * 错误数据访问3：模拟只设置全表字符替换，但是缺少替换字符串
	 *
	 * @Param: 无
	 * @return: 无
	 * */
	@Test
	public void saveAllTbCleanConfigInfo(){
		//正确数据访问1：模拟只设置全表字符补齐
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Clean_parameter.TableName + " where database_id = ?", FIRST_DATABASESET_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("新增全表字符补齐之前为database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业构造的全表清洗测试数据是存在的", oldCount == 2, is(true));
		}

		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("compFlag", "1")
				.addData("replaceFlag", "0")
				.addData("compType", "1")
				.addData("compChar", "test_saveAllTbCleanConfigInfo")
				.addData("compLen", "29")
				.post(getActionUrl("saveAllTbCleanConfigInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Clean_parameter.TableName + " where c_id in(666666, 777777)").orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("新增全表字符补齐成功后，为database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业构造的全表清洗测试数据没有了", oldCount == 0, is(true));

			Result compResult = SqlOperator.queryResult(db, "select filling_type, character_filling, filling_length from " + Clean_parameter.TableName + " where database_id = ? and clean_type = ?", FIRST_DATABASESET_ID, CleanType.ZiFuBuQi.getCode());
			assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置字符补齐成功", compResult.getRowCount() == 1, is(true));
			assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的补齐方式为前补齐", compResult.getString(0, "filling_type"), is("1"));
			assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的补齐字符为test_saveAllTbCleanConfigInfo", compResult.getString(0, "character_filling"), is(StringUtil.string2Unicode("test_saveAllTbCleanConfigInfo")));
			assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的补齐长度为29", compResult.getLong(0, "filling_length"), is(29L));

			//断言该数据库采集任务下所有的表全部设置了字符补齐
			Result tbCompResult = SqlOperator.queryResult(db, "select tc.filling_type, tc.character_filling, tc.filling_length from "
							+ Table_clean.TableName + " tc join " + Table_info.TableName + " ti on ti.table_id = tc.table_id where ti.database_id = ? and tc.character_filling = ? and clean_type = ?"
					, FIRST_DATABASESET_ID, StringUtil.string2Unicode("test_saveAllTbCleanConfigInfo"), CleanType.ZiFuBuQi.getCode());

			List<Object> tableIds = SqlOperator.queryOneColumnList(db, "select table_id from " + Table_info.TableName + " where database_id = ?", FIRST_DATABASESET_ID);
			assertThat("该数据库采集任务下的表共有" + tableIds.size() + "张", tbCompResult.getRowCount() == tableIds.size(), is(true));
			for(int i = 0; i < tbCompResult.getRowCount(); i++){
				assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业下所有的表补齐方式为前补齐", tbCompResult.getString(i, "filling_type"), is("1"));
				assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业下所有的表补齐长度为29", tbCompResult.getLong(i, "filling_length"), is(29L));
			}

			//断言该数据库采集任务下所有的字段全部设置了字符补齐
			Result colCompResult = SqlOperator.queryResult(db, "select cc.filling_type, cc.character_filling, cc.filling_length from "
					+ Column_clean.TableName + " cc join " + Table_column.TableName + " tc on cc.column_id = tc.column_id " +
					" join " + Table_info.TableName + " ti on ti.table_id = tc.table_id where ti.database_id = ? and cc.character_filling = ? and clean_type = ?"
					, FIRST_DATABASESET_ID, StringUtil.string2Unicode("test_saveAllTbCleanConfigInfo"), CleanType.ZiFuBuQi.getCode());

			List<Object> columnIds = SqlOperator.queryOneColumnList(db, "select column_id from " + Table_column.TableName + " tc" +
					" join " + Table_info.TableName + " ti on ti.table_id = tc.table_id " +
					" where ti.database_id = ?", FIRST_DATABASESET_ID);

			assertThat("该数据库采集任务下的字段共有" + columnIds.size() + "条", colCompResult.getRowCount() == columnIds.size(), is(true));
			for(int i = 0; i < colCompResult.getRowCount(); i++){
				assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业下所有的字段补齐方式为前补齐", colCompResult.getString(i, "filling_type"), is("1"));
				assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业下所有的字段补齐长度为29", colCompResult.getLong(i, "filling_length"), is(29L));
			}

			//断言完成后，删除本次新增的数据
			SqlOperator.execute(db, "delete from " + Clean_parameter.TableName + " where database_id = ? and clean_type = ?", FIRST_DATABASESET_ID, CleanType.ZiFuBuQi.getCode());

			StringBuilder strSBTb = new StringBuilder("delete from " + Table_clean.TableName + " where table_id in ( ");
			for(int j = 0; j < columnIds.size(); j++){
				strSBTb.append((long)columnIds.get(j));
				if (j != columnIds.size() - 1)
					strSBTb.append(",");
			}
			strSBTb.append(" ) and clean_type = ?");

			StringBuilder strSBCol = new StringBuilder("delete from " + Column_clean.TableName + " where column_id in ( ");
			for(int j = 0; j < columnIds.size(); j++){
				strSBCol.append((long)columnIds.get(j));
				if (j != columnIds.size() - 1)
					strSBCol.append(",");
			}
			strSBCol.append(" ) and clean_type = ?");

			SqlOperator.execute(db, strSBTb.toString(), CleanType.ZiFuBuQi.getCode());

			SqlOperator.execute(db, strSBCol.toString(), CleanType.ZiFuBuQi.getCode());

			SqlOperator.commitTransaction(db);
		}

		//正确数据访问2：模拟只设置全表字符替换(设置两条)
		String[] oriFieldArr = {"zxz", "hx"};
		String[] replaceFeildArr = {"shl", "zq"};
		String rightStringTwo = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("compFlag", "0")
				.addData("replaceFlag", "1")
				.addData("oriFieldArr", oriFieldArr)
				.addData("replaceFeildArr", replaceFeildArr)
				.post(getActionUrl("saveAllTbCleanConfigInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Clean_parameter.TableName + " where c_id in(666666, 777777)").orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("新增全表字符替换成功后，为database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业构造的全表清洗测试数据没有了", oldCount == 0, is(true));

			Result replaceResult = SqlOperator.queryResult(db, "select field, replace_feild from " + Clean_parameter.TableName + " where database_id = ? and clean_type = ?", FIRST_DATABASESET_ID, CleanType.ZiFuTiHuan.getCode());
			assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置字符替换成功", replaceResult.getRowCount() == 2, is(true));
			assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的原字符为zxz", replaceResult.getString(0, "field"), is(StringUtil.string2Unicode("zxz")));
			assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的替换后字符为shl", replaceResult.getString(0, "replace_feild"), is(StringUtil.string2Unicode("shl")));
			assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的原字符为hx", replaceResult.getString(1, "field"), is(StringUtil.string2Unicode("hx")));
			assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业设置的替换后字符为zq", replaceResult.getString(1, "replace_feild"), is(StringUtil.string2Unicode("zq")));

			//断言该数据库采集任务下所有的表全部设置了字符替换
			Result tbReplaResultOne = SqlOperator.queryResult(db, "select tc.field, tc.replace_feild from " + Table_clean.TableName + " tc join "
					+ Table_info.TableName + "  ti on ti.table_id = tc.table_id where ti.database_id = ? and tc.field = ?", FIRST_DATABASESET_ID, StringUtil.string2Unicode("zxz"));

			Result tbReplaResultTwo = SqlOperator.queryResult(db, "select tc.field, tc.replace_feild from " + Table_clean.TableName + " tc join "
					+ Table_info.TableName + "  ti on ti.table_id = tc.table_id where ti.database_id = ? and tc.field = ?", FIRST_DATABASESET_ID, StringUtil.string2Unicode("hx"));

			List<Object> tableIds = SqlOperator.queryOneColumnList(db, "select table_id from " + Table_info.TableName + " where database_id = ?", FIRST_DATABASESET_ID);
			assertThat("该数据库采集任务下的表共有" + tableIds.size() + "条设置的字符替换原字符为zxz", tbReplaResultOne.getRowCount() == tableIds.size(), is(true));
			assertThat("该数据库采集任务下的表共有" + tableIds.size() + "条设置的字符替换原字符为hx", tbReplaResultTwo.getRowCount() == tableIds.size(), is(true));

			for(int i = 0; i < tbReplaResultOne.getRowCount(); i++){
				assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业下所有的表设置的替换后字符为shl", tbReplaResultOne.getString(i, "replace_feild"), is(StringUtil.string2Unicode("shl")));
			}
			for(int i = 0; i < tbReplaResultTwo.getRowCount(); i++){
				assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业下所有的表设置的替换后字符为zq", tbReplaResultTwo.getString(i, "replace_feild"), is(StringUtil.string2Unicode("zq")));
			}

			//断言该数据库采集任务下所有的字段全部设置了字符替换
			Result colReplaResultOne = SqlOperator.queryResult(db, "select cc.field, cc.replace_feild from "
					+ Column_clean.TableName + " cc join " + Table_column.TableName + " tc on cc.column_id = tc.column_id " +
					" join " + Table_info.TableName + " ti on ti.table_id = tc.table_id where ti.database_id = ? and cc.field = ?", FIRST_DATABASESET_ID, StringUtil.string2Unicode("zxz"));

			Result colReplaResultTwo = SqlOperator.queryResult(db, "select cc.field, cc.replace_feild from "
					+ Column_clean.TableName + " cc join " + Table_column.TableName + " tc on cc.column_id = tc.column_id " +
					" join " + Table_info.TableName + " ti on ti.table_id = tc.table_id where ti.database_id = ? and cc.field = ?", FIRST_DATABASESET_ID, StringUtil.string2Unicode("hx"));

			List<Object> columnIds = SqlOperator.queryOneColumnList(db, "select column_id from " + Table_column.TableName + " tc" +
					" join " + Table_info.TableName + " ti on ti.table_id = tc.table_id " +
					" where ti.database_id = ?", FIRST_DATABASESET_ID);

			assertThat("该数据库采集任务下的字段共有" + columnIds.size() + "条设置的字符替换原字符为zxz", colReplaResultOne.getRowCount() == columnIds.size(), is(true));
			assertThat("该数据库采集任务下的字段共有" + columnIds.size() + "条设置的字符替换原字符为hx", colReplaResultTwo.getRowCount() == columnIds.size(), is(true));
			for(int i = 0; i < colReplaResultOne.getRowCount(); i++){
				assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业下所有的字段设置的替换后字符为shl", colReplaResultOne.getString(i, "replace_feild"), is(StringUtil.string2Unicode("shl")));
			}
			for(int i = 0; i < colReplaResultTwo.getRowCount(); i++){
				assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业下所有的字段设置的替换后字符为zq", colReplaResultTwo.getString(i, "replace_feild"), is(StringUtil.string2Unicode("zq")));
			}

			//断言成功后，删除本次访问接口新增的数据
			SqlOperator.execute(db, "delete from " + Clean_parameter.TableName + " where database_id = ? and clean_type = ?", FIRST_DATABASESET_ID, CleanType.ZiFuTiHuan.getCode());

			StringBuilder strSBTb = new StringBuilder("delete from " + Table_clean.TableName + " where table_id in ( ");
			for(int j = 0; j < columnIds.size(); j++){
				strSBTb.append((long)columnIds.get(j));
				if (j != columnIds.size() - 1)
					strSBTb.append(",");
			}
			strSBTb.append(" ) and clean_type = ?");

			StringBuilder strSBCol = new StringBuilder("delete from " + Column_clean.TableName + " where column_id in ( ");
			for(int j = 0; j < columnIds.size(); j++){
				strSBCol.append((long)columnIds.get(j));
				if (j != columnIds.size() - 1)
					strSBCol.append(",");
			}
			strSBCol.append(" ) and clean_type = ?");

			SqlOperator.execute(db, strSBTb.toString(), CleanType.ZiFuTiHuan.getCode());

			SqlOperator.execute(db, strSBCol.toString(), CleanType.ZiFuTiHuan.getCode());

			SqlOperator.commitTransaction(db);
		}

		//正确数据访问3：模拟既设置全表字符补齐，又设置全表字符替换
		String rightStringThree = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("compFlag", "1")
				.addData("replaceFlag", "1")
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
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Clean_parameter.TableName + " where c_id in(666666, 777777)").orElseThrow(() -> new BusinessException("SQL查询错误"));
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

			//断言该数据库采集任务下所有的表全部设置了字符补齐和字符替换
			Result tbCompResult = SqlOperator.queryResult(db, "select tc.filling_type, tc.character_filling, tc.filling_length from "
							+ Table_clean.TableName + " tc join " + Table_info.TableName + " ti on ti.table_id = tc.table_id where ti.database_id = ? and tc.character_filling = ? and clean_type = ?"
					, FIRST_DATABASESET_ID, StringUtil.string2Unicode("test_saveAllTbCleanConfigInfo"), CleanType.ZiFuBuQi.getCode());

			List<Object> tableIds = SqlOperator.queryOneColumnList(db, "select table_id from " + Table_info.TableName + " where database_id = ?", FIRST_DATABASESET_ID);
			assertThat("该数据库采集任务下的表共有" + tableIds.size() + "张", tbCompResult.getRowCount() == tableIds.size(), is(true));
			for(int i = 0; i < tbCompResult.getRowCount(); i++){
				assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业下所有的表补齐方式为前补齐", tbCompResult.getString(i, "filling_type"), is("1"));
				assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业下所有的表补齐长度为29", tbCompResult.getLong(i, "filling_length"), is(29L));
			}

			Result tbReplaResultOne = SqlOperator.queryResult(db, "select tc.field, tc.replace_feild from " + Table_clean.TableName + " tc join "
					+ Table_info.TableName + "  ti on ti.table_id = tc.table_id where ti.database_id = ? and tc.field = ?", FIRST_DATABASESET_ID, StringUtil.string2Unicode("zxz"));

			Result tbReplaResultTwo = SqlOperator.queryResult(db, "select tc.field, tc.replace_feild from " + Table_clean.TableName + " tc join "
					+ Table_info.TableName + "  ti on ti.table_id = tc.table_id where ti.database_id = ? and tc.field = ?", FIRST_DATABASESET_ID, StringUtil.string2Unicode("hx"));

			assertThat("该数据库采集任务下的表共有" + tableIds.size() + "条设置的字符替换原字符为zxz", tbReplaResultOne.getRowCount() == tableIds.size(), is(true));
			assertThat("该数据库采集任务下的表共有" + tableIds.size() + "条设置的字符替换原字符为hx", tbReplaResultTwo.getRowCount() == tableIds.size(), is(true));

			for(int i = 0; i < tbReplaResultOne.getRowCount(); i++){
				assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业下所有的表设置的替换后字符为shl", tbReplaResultOne.getString(i, "replace_feild"), is(StringUtil.string2Unicode("shl")));
			}
			for(int i = 0; i < tbReplaResultTwo.getRowCount(); i++){
				assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业下所有的表设置的替换后字符为zq", tbReplaResultTwo.getString(i, "replace_feild"), is(StringUtil.string2Unicode("zq")));
			}

			//断言该数据库采集任务下所有的字段全部设置了字符补齐和字符替换
			Result colCompResult = SqlOperator.queryResult(db, "select cc.filling_type, cc.character_filling, cc.filling_length from "
					+ Column_clean.TableName + " cc join " + Table_column.TableName + " tc on cc.column_id = tc.column_id " +
					" join " + Table_info.TableName + " ti on ti.table_id = tc.table_id where ti.database_id = ? and cc.character_filling = ? and clean_type = ?", FIRST_DATABASESET_ID, StringUtil.string2Unicode("test_saveAllTbCleanConfigInfo"), CleanType.ZiFuBuQi.getCode());

			List<Object> columnIds = SqlOperator.queryOneColumnList(db, "select column_id from " + Table_column.TableName + " tc" +
					" join " + Table_info.TableName + " ti on ti.table_id = tc.table_id " +
					" where ti.database_id = ?", FIRST_DATABASESET_ID);

			assertThat("该数据库采集任务下的字段共有" + columnIds.size() + "条", colCompResult.getRowCount() == columnIds.size(), is(true));
			for(int i = 0; i < colCompResult.getRowCount(); i++){
				assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业下所有的字段补齐方式为前补齐", colCompResult.getString(i, "filling_type"), is("1"));
				assertThat("新增全表字符补齐成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业下所有的字段补齐长度为29", colCompResult.getLong(i, "filling_length"), is(29L));
			}

			Result colReplaResultOne = SqlOperator.queryResult(db, "select cc.field, cc.replace_feild from "
					+ Column_clean.TableName + " cc join " + Table_column.TableName + " tc on cc.column_id = tc.column_id " +
					" join " + Table_info.TableName + " ti on ti.table_id = tc.table_id where ti.database_id = ? and cc.field = ?", FIRST_DATABASESET_ID, StringUtil.string2Unicode("zxz"));

			Result colReplaResultTwo = SqlOperator.queryResult(db, "select cc.field, cc.replace_feild from "
					+ Column_clean.TableName + " cc join " + Table_column.TableName + " tc on cc.column_id = tc.column_id " +
					" join " + Table_info.TableName + " ti on ti.table_id = tc.table_id where ti.database_id = ? and cc.field = ?", FIRST_DATABASESET_ID, StringUtil.string2Unicode("hx"));

			for(int i = 0; i < colReplaResultOne.getRowCount(); i++){
				assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业下所有的字段设置的替换后字符为shl", colReplaResultOne.getString(i, "replace_feild"), is(StringUtil.string2Unicode("shl")));
			}
			for(int i = 0; i < colReplaResultTwo.getRowCount(); i++){
				assertThat("新增全表字符替换成功后，database_id为" + FIRST_DATABASESET_ID + "数据库直连采集作业下所有的字段设置的替换后字符为zq", colReplaResultTwo.getString(i, "replace_feild"), is(StringUtil.string2Unicode("zq")));
			}

			//断言成功后，删除本次接口访问新增的数据
			SqlOperator.execute(db, "delete from " + Clean_parameter.TableName + " where database_id = ? and clean_type = ?", FIRST_DATABASESET_ID, CleanType.ZiFuTiHuan.getCode());
			SqlOperator.execute(db, "delete from " + Clean_parameter.TableName + " where database_id = ? and clean_type = ?", FIRST_DATABASESET_ID, CleanType.ZiFuBuQi.getCode());

			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where clean_type in (?, ?) ", CleanType.ZiFuBuQi.getCode(), CleanType.ZiFuTiHuan.getCode());
			SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where clean_type in (?, ?) ", CleanType.ZiFuBuQi.getCode(), CleanType.ZiFuTiHuan.getCode());

			SqlOperator.commitTransaction(db);
		}

		//错误数据访问1：模拟值设置全表字符补齐，但是补齐方式是3，这样访问不会成功
		String wrongString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("compFlag", "1")
				.addData("replaceFlag", "0")
				.addData("compType", "3")
				.addData("compChar", "test_saveAllTbCleanConfigInfo")
				.addData("compLen", "29")
				.post(getActionUrl("saveAllTbCleanConfigInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));

		//错误数据访问2：模拟只设置全表字符替换，但是缺少原字符串
		String wrongStringTwo = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("compFlag", "0")
				.addData("replaceFlag", "1")
				.addData("replaceFeildArr", replaceFeildArr)
				.post(getActionUrl("saveAllTbCleanConfigInfo")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));

		//错误数据访问3：模拟只设置全表字符替换，但是缺少补齐字符串
		String wrongStringThree = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("compFlag", "0")
				.addData("replaceFlag", "1")
				.addData("oriFieldArr", oriFieldArr)
				.post(getActionUrl("saveAllTbCleanConfigInfo")).getBodyString();
		ActionResult wrongResultThree = JsonUtil.toObjectSafety(wrongStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultThree.isSuccess(), is(false));

	}

	/**
	 * 测试根据数据库设置ID查询所有表清洗设置字符补齐和字符替换规则
	 *
	 * 正确数据访问1：模拟获取database_id为1001的数据库直连采集作业所有表清洗规则，能够获取到一条字符替换规则
	 * 错误的数据访问1：模拟获取database_id为1002的数据库直连采集作业所有表清洗规则，因为在这个作业中没有配置表，所以获取不到数据
	 * 错误的测试用例未达到三组: getAllTbCleanReplaceInfo不会因为正常情况下，不会因为参数不同而导致访问失败，只会因为参数的不同获取到的数据也不同
	 * @Param: 无
	 * @return: 无
	 * */
	@Test
	public void getAllTbCleanReplaceInfo(){
		//正确数据访问1：模拟获取database_id为1001的数据库直连采集作业所有表清洗规则，能够获取到一条字符替换规则
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getAllTbCleanReplaceInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		Result replaceResult = rightResult.getDataForResult();

		assertThat("模拟获取database_id为1001的数据库直连采集作业所有表清洗规则,其中有一条字符替换规则", replaceResult.getRowCount(), is(1));
		assertThat("模拟获取database_id为1001的数据库直连采集作业所有表清洗规则,其中有一条字符替换规则，原字符为", replaceResult.getString(0, "field"), is("test_orifield"));
		assertThat("模拟获取database_id为1001的数据库直连采集作业所有表清洗规则,其中有一条字符替换规则", replaceResult.getString(0, "replace_feild"), is("test_newField"));


		//错误的数据访问1：模拟获取database_id为1002的数据库直连采集作业所有表清洗规则，因为在这个作业中没有配置表，所以获取不到数据
		String rightStringTwo = new HttpClient()
				.addData("colSetId", SECOND_DATABASESET_ID)
				.post(getActionUrl("getAllTbCleanReplaceInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		Result rightDataTwo = rightResultTwo.getDataForResult();

		assertThat("模拟获取database_id为1002的数据库直连采集作业所有表清洗规则,获取不到数据", rightDataTwo.isEmpty(), is(true));
	}

	/**
	 * 测试根据数据库设置ID查询所有表清洗设置字符补齐和字符替换规则
	 *
	 * 正确数据访问1：模拟获取database_id为1001的数据库直连采集作业所有表清洗规则，能够获取到一条字符补齐规则
	 * 错误的数据访问1：模拟获取database_id为1002的数据库直连采集作业所有表清洗规则，因为在这个作业中没有配置表，所以获取不到数据
	 * 错误的测试用例未达到三组: getAllTbCleanCompInfo不会因为正常情况下，不会因为参数不同而导致访问失败，只会因为参数的不同获取到的数据也不同
	 * @Param: 无
	 * @return: 无
	 * */
	@Test
	public void getAllTbCleanCompInfo(){
		//正确数据访问1：模拟获取database_id为1001的数据库直连采集作业所有表清洗规则，能够获取到一条字符补齐规则
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getAllTbCleanCompInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		Result completionResult = rightResult.getDataForResult();

		assertThat("模拟获取database_id为1001的数据库直连采集作业所有表清洗规则,其中有一条字符补齐规则", completionResult.getRowCount() == 1, is(true));
		assertThat("模拟获取database_id为1001的数据库直连采集作业所有表清洗规则,其中有一条字符补齐规则,补齐字符为 ", completionResult.getString(0, "character_filling"), is("cleanparameter"));
		assertThat("模拟获取database_id为1001的数据库直连采集作业所有表清洗规则,其中有一条字符补齐规则,补齐长度为 ", completionResult.getLong(0, "filling_length"), is(14L));
		assertThat("模拟获取database_id为1001的数据库直连采集作业所有表清洗规则,其中有一条字符补齐规则,补齐方式为 ", completionResult.getString(0, "filling_type"), is("1"));

		//错误的数据访问1：模拟获取database_id为1002的数据库直连采集作业所有表清洗规则，因为在这个作业中没有配置表，所以获取不到数据
		String rightStringTwo = new HttpClient()
				.addData("colSetId", SECOND_DATABASESET_ID)
				.post(getActionUrl("getAllTbCleanCompInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		Result rightDataTwo = rightResultTwo.getDataForResult();

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
	 * 错误数据访问3：模拟修改对sys_user表的login_date字段设置日期格式化，但是保存时没有关联字段ID
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveDateFormatInfo(){
		//正确数据访问1：模拟修改对sys_user表的login_date字段设置日期格式化
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and old_format = ? and convert_format = ?", 2011L, "YYYY-MM-DD", "YYYY-MM").orElseThrow(() -> new BusinessException("SQL查询错误"));
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
			long oldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and old_format = ? and convert_format = ?", 2011L, "YYYY-MM-DD", "YYYY-MM").orElseThrow(() -> new BusinessException("SQL查询错误"));
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

		//错误数据访问3：模拟修改对sys_user表的login_date字段设置日期格式化，但是保存时没有关联字段ID
		String wrongStringThree = new HttpClient()
				.addData("clean_type", CleanType.ShiJianZhuanHuan.getCode())
				.addData("convert_format", "yyyy年MM月dd日 HH:mm:ss")
				.addData("old_format", "YYYY-MM-DD")
				.post(getActionUrl("saveDateFormatInfo")).getBodyString();
		ActionResult wrongResultThree = JsonUtil.toObjectSafety(wrongStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultThree.isSuccess(), is(false));
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
			long columnOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where column_id = ?", 141414L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("进行第一次删除前，拆分为ci的列在table_column表中存在", columnOldCount == 1, is(true));
			long splitOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id = ?", 101010103L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("进行第一次删除前，拆分为ci的列在column_split表中存在", splitOldCount == 1, is(true));
			long cleanOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where col_clean_id = ? and clean_type = ?", 101010102L, CleanType.ZiFuChaiFen.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
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
			long columnOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where column_id = ?", 141414L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("进行第一次删除后，拆分为ci的列在table_column表中被删除了", columnOldCount == 0, is(true));
			long splitOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id = ?", 101010103L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("进行第一次删除后，拆分为ci的列在column_split表中被删除了", splitOldCount == 0, is(true));
			long cleanOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where col_clean_id = ? and clean_type = ?", 101010102L, CleanType.ZiFuChaiFen.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("进行第一次删除后，列ci_sp_classname的列拆分配置在column_clean表中存在", cleanOldCount == 1, is(true));
		}

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long columnOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where column_id = ?", 151515L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("进行第二次删除前，拆分为sp的列在table_column表中存在", columnOldCount == 1, is(true));
			long splitOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id = ?", 101010104L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("进行第二次删除前，拆分为sp的列在column_split表中存在", splitOldCount == 1, is(true));
			long cleanOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where col_clean_id = ? and clean_type = ?", 101010102L, CleanType.ZiFuChaiFen.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
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
			long columnOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where column_id = ?", 151515L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("进行第二次删除后，拆分为sp的列在table_column表中被删除了", columnOldCount == 0, is(true));
			long splitOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id = ?", 101010104L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("进行第二次删除后，拆分为sp的列在column_split表中被删除了", splitOldCount == 0, is(true));
			long cleanOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where col_clean_id = ? and clean_type = ?", 101010102L, CleanType.ZiFuChaiFen.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("进行第二次删除后，列ci_sp_classname的列拆分配置在column_clean表中存在", cleanOldCount == 1, is(true));
		}

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long columnOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where column_id = ?", 161616L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("进行第三次删除前，拆分为classname的列在table_column表中存在", columnOldCount == 1, is(true));
			long splitOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id = ?", 101010105L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("进行第三次删除前，拆分为classname的列在column_split表中存在", splitOldCount == 1, is(true));
			long cleanOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where col_clean_id = ? and clean_type = ?", 101010102L, CleanType.ZiFuChaiFen.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
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
			long columnOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where column_id = ?", 161616L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("进行第三次删除后，拆分为classname的列在table_column表中被删除了", columnOldCount == 0, is(true));
			long splitOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id = ?", 101010105L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("进行第三次删除后，拆分为classname的列在column_split表中被删除了", splitOldCount == 0, is(true));
			long cleanOldCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where col_clean_id = ? and clean_type = ?", 101010102L, CleanType.ZiFuChaiFen.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
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
	 * 错误的数据访问1：模拟新增对code_info表的ci_sp_code设置字段拆分规则，但是保存时没有关联字段
	 * 错误的数据访问2：模拟新增对code_info表的ci_sp_code设置字段拆分规则，但是保存时，由于是按照自定义符号进行拆分，但是缺少了自定义符号
	 * 错误的数据访问3：模拟新增对code_info表的ci_sp_code设置字段拆分规则，但是保存时，由于是按照自定义符号进行拆分，但是缺少了值位置
	 * 错误的数据访问4：模拟新增对code_info表的ci_sp_classname设置字段拆分规则，但是保存时，由于是按照偏移量进行拆分，但是缺少了偏移量
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
			String splitType = CharSplitType.PianYiLiang.getCode();
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
			List<Table_column> tableColumns = SqlOperator.queryList(db, Table_column.class, "select * from " + Table_column.TableName + " where column_name in" +
							" (select t1.column_name from "+ Table_column.TableName +" t1" +
							" JOIN " + Column_split.TableName + " t2 ON t1.column_name = t2.col_name " +
							" JOIN " + Column_clean.TableName + " t3 ON t2.col_clean_id = t3.col_clean_id " +
							" WHERE t2.col_clean_id = ? and t2.column_id = ? and t1.table_id = ? and t1.is_new = ?) ",
					101010102L, 3003L, CODE_INFO_TABLE_ID, IsFlag.Shi.getCode());
			assertThat("进行code_info表的ci_cp_classname列拆分修改保存前，拆分为ci、sp、classname的列在table_column表中存在", tableColumns.size() == 3, is(true));
			for(Table_column tableColumn : tableColumns){
				if(tableColumn.getColumn_id() == 141414L){
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存前，拆分为ci的列在table_column表中存在", tableColumn.getColumn_name().equalsIgnoreCase("ci"), is(true));
				}else if(tableColumn.getColumn_id() == 151515L){
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存前，拆分为sp的列在table_column表中存在", tableColumn.getColumn_name().equalsIgnoreCase("sp"), is(true));
				}else if(tableColumn.getColumn_id() == 161616L){
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存前，拆分为classname的列在table_column表中存在", tableColumn.getColumn_name().equalsIgnoreCase("classname"), is(true));
				}else{
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存前，在table_column表中查询到了不符合预期的列" + tableColumn.getColumn_name(), false, is(true));
				}
			}
			long beforeDelSpCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id in (101010103, 101010104, 101010105) ").orElseThrow(() -> new BusinessException("SQL查询错误"));
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
			List<Table_column> tableColumns = SqlOperator.queryList(db, Table_column.class, "select * from " + Table_column.TableName + " where column_name in" +
							" (select t1.column_name from "+ Table_column.TableName +" t1" +
							" JOIN " + Column_split.TableName + " t2 ON t1.column_name = t2.col_name " +
							" JOIN " + Column_clean.TableName + " t3 ON t2.col_clean_id = t3.col_clean_id " +
							" WHERE t2.col_clean_id = ? and t2.column_id = ? and t1.table_id = ? and t1.is_new = ?) ",
					101010102L, 3003L, CODE_INFO_TABLE_ID, IsFlag.Shi.getCode());
			assertThat("进行code_info表的ci_cp_classname列拆分修改保存后，拆分为ci_s、p_class、name的列在table_column表中存在", tableColumns.size() == 3, is(true));
			for(Table_column tableColumn : tableColumns){
				if(tableColumn.getColumn_name().equalsIgnoreCase("ci_s")){
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存后，拆分为ci_s的列在table_column表中存在", tableColumn.getColumn_name().equalsIgnoreCase("ci_s"), is(true));
				}else if(tableColumn.getColumn_name().equalsIgnoreCase("p_class")){
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存后，拆分为p_class的列在table_column表中存在", tableColumn.getColumn_name().equalsIgnoreCase("p_class"), is(true));
				}else if(tableColumn.getColumn_name().equalsIgnoreCase("name")){
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存后，拆分为name的列在table_column表中存在", tableColumn.getColumn_name().equalsIgnoreCase("name"), is(true));
				}else{
					assertThat("进行code_info表的ci_cp_classname列拆分修改保存后，在table_column表中查询到了不符合预期的列" + tableColumn.getColumn_name(), false, is(true));
				}
			}
			long afterDelSpCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id in (101010103, 101010104, 101010105) ").orElseThrow(() -> new BusinessException("SQL查询错误"));
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
			int execute1 = SqlOperator.execute(db, "delete from " + Table_column.TableName + " where column_name in " +
							" (select t1.column_name from table_column t1 " +
							" JOIN " + Column_split.TableName + " t2 ON t1.column_name = t2.col_name " +
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
			String splitType = CharSplitType.ZhiDingFuHao.getCode();
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
					" where column_name in " +
					" (select t1.column_name from table_column t1" +
					" JOIN " + Column_split.TableName + " t2 ON t1.column_name = t2.col_name " +
					" JOIN " + Column_clean.TableName + " t3 ON t2.col_clean_id = t3.col_clean_id " +
					" WHERE t2.column_id = ? and t1.table_id = ? and t1.is_new = ?) ", 3001L, CODE_INFO_TABLE_ID, IsFlag.Shi.getCode());
			assertThat("模拟新增对code_info表的ci_sp_code设置字段拆分规则，table_column表中按照下划线分拆分为ci、sp、code三列成功", tableColumns.size() == 3, is(true));
			for(Table_column tableColumn : tableColumns){
				if(tableColumn.getColumn_name().equalsIgnoreCase("ci")){
					assertThat("模拟新增对code_info表的ci_sp_code设置字段拆分规则，table_column表中按照下划线分拆分为ci", true, is(true));
				}else if(tableColumn.getColumn_name().equalsIgnoreCase("sp")){
					assertThat("模拟新增对code_info表的ci_sp_code设置字段拆分规则，table_column表中按照下划线分拆分为sp", true, is(true));
				}else if(tableColumn.getColumn_name().equalsIgnoreCase("code")){
					assertThat("模拟新增对code_info表的ci_sp_code设置字段拆分规则，table_column表中按照下划线分拆分为code", true, is(true));
				}else{
					assertThat("模拟新增对code_info表的ci_sp_code设置字段拆分规则，table_column表中出现了不期望出现的数据, 列名为" + tableColumn.getColumn_name(), false, is(true));
				}
			}

			long afterSplitCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where column_id = ?", 3001L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("模拟新增对code_info表的ci_sp_code设置字段拆分规则，column_split表中按照下划线分拆分为ci、sp、code三列成功", afterSplitCount == 3, is(true));

			//删除新增时带来的数据
			int execute1 = SqlOperator.execute(db, "delete from " + Table_column.TableName + " where column_name in " +
							" (select t1.column_name from table_column t1 " +
							" JOIN " + Column_split.TableName + " t2 ON t1.column_name = t2.col_name " +
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

		//错误的数据访问1：模拟新增对code_info表的ci_sp_code设置字段拆分规则，但是保存时没有关联字段
		String wrongStringOne = new HttpClient()
				.addData("clean_type", CleanType.ZiFuChaiFen.getCode())
				.addData("columnSplitString", JSON.toJSONString(splitByUnderLine))
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("saveColSplitInfo")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));
		//错误的数据访问2：模拟新增对code_info表的ci_sp_code设置字段拆分规则，但是保存时，由于是按照自定义符号进行拆分，但是缺少了自定义符号
		List<Column_split> wrongSplitByUnderLineOne = new ArrayList<>();
		for(int i = 0; i < 3; i++){
			String colName = null;
			long seq = 0;
			String splitType = CharSplitType.ZhiDingFuHao.getCode();
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
			columnSplit.setSeq(seq);
			columnSplit.setCol_type(colType);
			columnSplit.setCol_zhname(colChName);
			columnSplit.setSplit_type(splitType);

			wrongSplitByUnderLineOne.add(columnSplit);
		}

		String wrongStringTwo = new HttpClient()
				.addData("clean_type", CleanType.ZiFuChaiFen.getCode())
				.addData("column_id", 3001L)
				.addData("columnSplitString", JSON.toJSONString(wrongSplitByUnderLineOne))
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("saveColSplitInfo")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));

		// 错误的数据访问3：模拟新增对code_info表的ci_sp_code设置字段拆分规则，但是保存时，由于是按照自定义符号进行拆分，但是缺少了值位置
		List<Column_split> wrongSplitByUnderLineTwo = new ArrayList<>();
		for(int i = 0; i < 3; i++){
			String colName = null;
			String splitSep = "_";
			String splitType = CharSplitType.ZhiDingFuHao.getCode();
			String colChName = null;
			String colType = "varchar(512)";
			switch (i){
				case 0 :
					colName = "ci";
					colChName = "ci_ch";
					break;
				case 1 :
					colName = "sp";
					colChName = "sp_ch";
					break;
				case 2 :
					colName = "code";
					colChName = "code_ch";
					break;
			}

			Column_split columnSplit = new Column_split();
			columnSplit.setCol_name(colName);
			columnSplit.setSplit_sep(splitSep);
			columnSplit.setCol_type(colType);
			columnSplit.setCol_zhname(colChName);
			columnSplit.setSplit_type(splitType);

			wrongSplitByUnderLineTwo.add(columnSplit);
		}

		String wrongStringThree = new HttpClient()
				.addData("clean_type", CleanType.ZiFuChaiFen.getCode())
				.addData("column_id", 3001L)
				.addData("columnSplitString", JSON.toJSONString(wrongSplitByUnderLineTwo))
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("saveColSplitInfo")).getBodyString();
		ActionResult wrongResultThree = JsonUtil.toObjectSafety(wrongStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultThree.isSuccess(), is(false));

		// 错误的数据访问3：模拟新增对code_info表的ci_sp_classname设置字段拆分规则，但是保存时，由于是按照偏移量进行拆分，但是缺少了偏移量
		List<Column_split> wrongOffsetSpiltsOne = new ArrayList<>();
		for(int i = 0; i < 3; i++){
			long colSplitId = 0;
			String columnName = null;
			String splitType = CharSplitType.PianYiLiang.getCode();
			String columnChName = null;
			String columnType = "varchar(512)";
			long colCleanId = 101010102L;
			long columnId = 3003L;
			switch (i){
				case 0 :
					colSplitId = 101010103L;
					columnName = "ci_s";
					columnChName = "ci_s_ch";
					break;
				case 1 :
					colSplitId = 101010104L;
					columnName = "p_class";
					columnChName = "p_class_ch";
					break;
				case 2 :
					colSplitId = 101010105L;
					columnName = "name";
					columnChName = "name_ch";
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

			wrongOffsetSpiltsOne.add(columnSplit);
		}

		String rightStringFour = new HttpClient()
				.addData("col_clean_id", 101010102L)
				.addData("clean_type", CleanType.ZiFuChaiFen.getCode())
				.addData("column_id", 3003L)
				.addData("columnSplitString", JSON.toJSONString(wrongOffsetSpiltsOne))
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("saveColSplitInfo")).getBodyString();
		ActionResult rightResultFour = JsonUtil.toObjectSafety(rightStringFour, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultFour.isSuccess(), is(false));
	}

	/**
	 * 测试根据列ID获取该列在列清洗参数表中定义码值系统编码(codesys)的和编码分类(codename)
	 *
	 * 正确数据访问1：获取column_id为2010的列的码值转换信息
	 * 错误的数据访问1：获取column_id为2009的列的码值转换信息，因为在构造数据的时候没有设置过，所以获取不到
	 * 错误的测试用例未达到三组:getCVConversionInfo永远不会因为参数不同而导致访问失败
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getCVConversionInfo(){
		//正确数据访问1：获取column_id为2010的列的码值转换信息
		String rightStringOne = new HttpClient()
				.addData("columnId", 2010L)
				.post(getActionUrl("getCVConversionInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Result rightDataOne = rightResultOne.getDataForResult();
		assertThat("user_type列定义了码值转换信息", rightDataOne.getRowCount(), is(1));
		assertThat("user_type列定义了码值转换信息, 码值所属系统符合预期", rightDataOne.getString(0, "orig_sys_code"), is("origSysCode_one"));
		assertThat("user_type列定义了码值转换信息, 码值名称符合预期", rightDataOne.getString(0, "orig_sys_name"), is("origSysName_one(origSysCode_one)"));
		assertThat("user_type列定义了码值转换信息, 码值名称符合预期", rightDataOne.getString(0, "code_classify"), is("codeClassify_one"));

		//错误的数据访问1：获取column_id为2009的列的码值转换信息，因为在构造数据的时候没有设置过，所以获取不到
		String wrongString = new HttpClient()
				.addData("columnId", 2009L)
				.post(getActionUrl("getCVConversionInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(true));

		Result wrongData = wrongResult.getDataForResult();

		assertThat("user_isadmin列没有定义码值转换信息", wrongData.getRowCount(), is(0));
	}

	/**
	 * 测试获取当前系统中的所有码值信息
	 *
	 * 正确数据访问1：直接访问方法，断言获取到的数据是否正确
	 * 错误的测试用例未达到三组:getSysCVInfo没有传参，只会根据当前系统的实际情况来返回相应的数据条数
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getSysCVInfo(){
		//测试获取当前系统中的所有码值信息
		String rightStringOne = new HttpClient()
				.post(getActionUrl("getSysCVInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		List<Orig_syso_info> data = rightResultOne.getDataForEntityList(Orig_syso_info.class);
		assertThat("系统中定义的码值信息有3条", data.size(), is(3));
		for(Orig_syso_info origSysoInfo : data){
			if(origSysoInfo.getOrig_sys_code().equalsIgnoreCase("origSysCode_one")){
				assertThat(origSysoInfo.getOrig_sys_name(), is("origSysName_one"));
				assertThat(origSysoInfo.getOrig_sys_remark(), is("origSysRemark_one"));
			}else if(origSysoInfo.getOrig_sys_code().equalsIgnoreCase("origSysCode_two")){
				assertThat(origSysoInfo.getOrig_sys_name(), is("origSysName_two"));
				assertThat(origSysoInfo.getOrig_sys_remark(), is("origSysRemark_two"));
			}else if(origSysoInfo.getOrig_sys_code().equalsIgnoreCase("origSysCode_three")){
				assertThat(origSysoInfo.getOrig_sys_name(), is("origSysName_three"));
				assertThat(origSysoInfo.getOrig_sys_remark(), is("origSysRemark_three"));
			}else{
				assertThat("出现了不符合预期的情况" + origSysoInfo.getOrig_sys_name(), true, is(false));
			}
		}
	}

	/**
	 * 测试根据码值系统编码获取编码分类
	 *
	 *
	 * 正确数据访问1：使用origSysCode_one获取编码类型，能够获取到codeClassify_one
	 * 错误的数据访问1：使用codeClassify_four获取编码类型，获取不到数据
	 * 错误的测试用例未达到三组:getCVClassifyBySysCode方法不会因为参数而导致接口访问失败，只会根据实际情况返回不同的数据
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getCVClassifyBySysCode(){
		//正确数据访问1：使用origSysCode_one获取编码类型，能够获取到codeClassify_one
		String rightStringOne = new HttpClient()
				.addData("origSysCode", "origSysCode_one")
				.post(getActionUrl("getCVClassifyBySysCode")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Result rightData = rightResultOne.getDataForResult();
		assertThat(rightData.getRowCount(), is(1));
		assertThat(rightData.getString(0, "code_classify"), is("codeClassify_one"));

		//错误的数据访问1：使用codeClassify_four获取编码类型，获取不到数据
		String wrongString = new HttpClient()
				.addData("origSysCode", "origSysCode_four")
				.post(getActionUrl("getCVClassifyBySysCode")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(true));

		Result wrongData = wrongResult.getDataForResult();
		assertThat(wrongData.getRowCount(), is(0));
	}

	/**
	 * 测试根据码值系统编码和编码分类获得原码值(orig_value)和新码值(code_value)
	 *
	 *
	 * 正确数据访问1：使用code_classify(codeClassify_two)，orig_sys_code(origSysCode_two)查询，能够获得源码值为oriValue_two， 新码值为newValue_two
	 * 错误的数据访问1：使用code_classify(codeClassify_one)，orig_sys_code(origSysCode_two)查询，获取不到数据
	 * 错误的测试用例未达到三组:getCVInfo方法永远不会因为参数传递错误而导致访问失败，只会根据实际情况返回不同的数据
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getCVInfo(){
		//正确数据访问1：使用code_classify(codeClassify_two)，orig_sys_code(origSysCode_two)查询，能够获得源码值为oriValue_two， 新码值为newValue_two
		String rightStringOne = new HttpClient()
				.addData("codeClassify", "codeClassify_two")
				.addData("origSysCode", "origSysCode_two")
				.post(getActionUrl("getCVInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Result rightData = rightResultOne.getDataForResult();
		assertThat("获取到的数据有一条", rightData.getRowCount(), is(1));
		assertThat("获取到的原码值为oriValue_two", rightData.getString(0, "orig_value"), is("oriValue_two"));
		assertThat("获取到的原码值为newValue_two", rightData.getString(0, "code_value"), is("newValue_two"));

		//错误的数据访问1：使用code_classify(codeClassify_one)，orig_sys_code(origSysCode_two)查询，获取不到数据
		String wrongString = new HttpClient()
				.addData("codeClassify", "codeClassify_one")
				.addData("origSysCode", "origSysCode_two")
				.post(getActionUrl("getCVInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(true));

		Result wrongData = wrongResult.getDataForResult();

		assertThat("获取不到数据", wrongData.getRowCount(), is(0));
	}

	/**
	 * 测试保存列码值转换清洗规则
	 *
	 *
	 * 正确数据访问1：构造正确的码值转换新增场景
	 * 正确数据访问2：构造正确的码值转换修改场景
	 * 错误的数据访问1：保存码值转换缺少码值系统类型
	 * 错误的数据访问2：保存码值转换缺少码值系统名称
	 * 错误的数据访问3：保存码值转换缺少列ID
	 * 错误的数据访问4：保存码值转换,码值类型传空字符串
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveCVConversionInfo(){
		//正确数据访问1：构造正确的码值转换新增场景
		String rightStringOne = new HttpClient()
				.addData("clean_type", CleanType.MaZhiZhuanHuan.getCode())
				.addData("codename", "codeClassify_three")
				.addData("codesys", "origSysCode_three")
				.addData("column_id", 2001L)
				.post(getActionUrl("saveCVConversionInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		//查询数据库，确认新增是否成功,并删除新增的数据
		try(DatabaseWrapper db = new DatabaseWrapper()){
			Result result = SqlOperator.queryResult(db, "select codename, codesys from " + Column_clean.TableName + " where column_id = ? and clean_type = ?", 2001L, CleanType.MaZhiZhuanHuan.getCode());
			assertThat("获得到一条数据", result.getRowCount(), is(1));
			assertThat("获得到的码值名称为", result.getString(0, "codename"), is("codeClassify_three"));
			assertThat("获得到的码值所属系统为", result.getString(0, "codesys"), is("origSysCode_three"));

			int count = SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? and clean_type = ?", 2001L, CleanType.MaZhiZhuanHuan.getCode());
			assertThat("删除成功", count, is(1));

			SqlOperator.commitTransaction(db);
		}

		//正确数据访问2：构造正确的码值转换修改场景,对2010L列在构造初始化测试数据的时候就设置了码值转换
		String rightStringTwo = new HttpClient()
				.addData("clean_type", CleanType.MaZhiZhuanHuan.getCode())
				.addData("codename", "codeClassify_two")
				.addData("codesys", "origSysCode_two")
				.addData("column_id", 2010L)
				.post(getActionUrl("saveCVConversionInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		//查询数据库，确认修改是否成功
		try(DatabaseWrapper db = new DatabaseWrapper()){
			Result result = SqlOperator.queryResult(db, "select codename, codesys from " + Column_clean.TableName + " where column_id = ? and clean_type = ?", 2010L, CleanType.MaZhiZhuanHuan.getCode());
			assertThat("获得到一条数据", result.getRowCount(), is(1));
			assertThat("获得到的码值名称为", result.getString(0, "codename"), is("codeClassify_two"));
			assertThat("获得到的码值所属系统为", result.getString(0, "codesys"), is("origSysCode_two"));
		}

		//错误的数据访问1：保存码值转换缺少码值系统类型
		String wrongStringOne = new HttpClient()
				.addData("clean_type", CleanType.MaZhiZhuanHuan.getCode())
				.addData("codename", "codeClassify_two")
				.addData("column_id", 2001L)
				.post(getActionUrl("saveCVConversionInfo")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));

		//错误的数据访问2：保存码值转换缺少码值系统名称
		String wrongStringTwo = new HttpClient()
				.addData("clean_type", CleanType.MaZhiZhuanHuan.getCode())
				.addData("codesys", "origSysCode_three")
				.addData("column_id", 2001L)
				.post(getActionUrl("saveCVConversionInfo")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));

		//错误的数据访问3：保存码值转换缺少列ID
		String wrongStringThree = new HttpClient()
				.addData("clean_type", CleanType.MaZhiZhuanHuan.getCode())
				.addData("codename", "codeClassify_three")
				.addData("codesys", "origSysCode_three")
				.post(getActionUrl("saveCVConversionInfo")).getBodyString();
		ActionResult wrongResultThree = JsonUtil.toObjectSafety(wrongStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultThree.isSuccess(), is(false));

		//错误的数据访问4：保存码值转换,码值类型传空字符串
		String wrongStringFour = new HttpClient()
				.addData("clean_type", CleanType.MaZhiZhuanHuan.getCode())
				.addData("codename", "")
				.addData("codesys", "origSysCode_three")
				.post(getActionUrl("saveCVConversionInfo")).getBodyString();
		ActionResult wrongResultFour = JsonUtil.toObjectSafety(wrongStringFour, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultFour.isSuccess(), is(false));
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
	 * 正确数据访问2：模拟对code_info表设置列合并
	 * 错误的数据访问1：保存列合并时没有填写要合并的字段
	 * 错误的数据访问2：保存列合并时没有填写合并后的字段名称
	 * 错误的数据访问3：保存列合并时没有填写字段类型
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveColMergeInfo(){
		//正确数据访问1：模拟对sys_user表设置好的列合并进行修改
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long beforeColumnCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where column_id = ?", 1717171717L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			long beforeMergeCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_merge.TableName + " where col_merge_id = ?", 16161616L).orElseThrow(() -> new BusinessException("SQL查询错误"));
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
					+ " where column_name in (select t1.column_name from " + Table_column.TableName + " t1 " +
					" JOIN " + Column_merge.TableName + " t2 ON t1.table_id=t2.table_id " +
					" where t2.table_id = ? and t1.is_new = ? )", SYS_USER_TABLE_ID, IsFlag.Shi.getCode());
			long afterColumnCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_column.TableName + " where column_id = ?", 1717171717L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			long afterMergeCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_merge.TableName + " where col_merge_id = ?", 16161616L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("模拟对sys_user表设置好的列合并进行修改成功", tableColumns.size() == 1, is(true));
			assertThat("模拟对sys_user表设置好的列合并进行修改成功", afterColumnCount == 0, is(true));
			assertThat("模拟对sys_user表设置好的列合并进行修改成功", afterMergeCount == 0, is(true));

			int execute = SqlOperator.execute(db, "delete from " + Table_column.TableName + " where column_name in (select t1.column_name from " + Table_column.TableName + " t1 " +
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
					+ " where column_name in (select t1.column_name from " + Table_column.TableName + " t1 " +
					" JOIN " + Column_merge.TableName + " t2 ON t1.table_id = t2.table_id " +
					" and t1.column_name = t2.col_name " +
					" where t2.table_id = ? and t1.is_new = ? )", CODE_INFO_TABLE_ID, IsFlag.Shi.getCode());
			long afterMergeCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_merge.TableName + " where table_id = ?", CODE_INFO_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("模拟对code_info表设置列合并成功", tableColumns.size(), is(1));
			assertThat("模拟对code_info表设置列合并成功", tableColumns.get(0).getColumn_name().equalsIgnoreCase("ci_sp_name_remark"), is(true));
			assertThat("模拟对code_info表设置列合并成功", afterMergeCount == 1, is(true));

			int execute = SqlOperator.execute(db, "delete from " + Table_column.TableName + " where column_name in (select t1.column_name from " + Table_column.TableName + " t1 " +
					" JOIN " + Column_merge.TableName + " t2 ON t1.table_id=t2.table_id " +
					" and t1.column_name = t2.col_name " +
					" where t2.table_id = ? and t1.is_new = ? ) ", CODE_INFO_TABLE_ID, IsFlag.Shi.getCode());
			assertThat("模拟对code_info表设置列合并成功，删除table_column表中新增的数据", execute == 1 ,is(true));

			int execute1 = SqlOperator.execute(db, "delete from " + Column_merge.TableName + " where table_id = ?", CODE_INFO_TABLE_ID);
			assertThat("模拟对code_info表设置列合并成功，删除column_merge表中新增的数据", execute1 ==1 ,is(true));

			SqlOperator.commitTransaction(db);
		}

		//错误的数据访问1：保存列合并时没有填写要合并的字段
		columnMerges.clear();
		Column_merge wrongColumnMergeOne = new Column_merge();
		wrongColumnMergeOne.setCol_name("ci_sp_name_remark");
		wrongColumnMergeOne.setCol_zhname("ci_sp_name_remark_ch");
		wrongColumnMergeOne.setCol_type("varchar(512)");

		columnMerges.add(wrongColumnMergeOne);

		String wrongStringOne = new HttpClient()
				.addData("columnMergeString", JSON.toJSONString(columnMerges))
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("saveColMergeInfo")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));

		//错误的数据访问2：保存列合并时没有填写合并后的字段名称
		columnMerges.clear();
		Column_merge wrongColumnMergeTwo = new Column_merge();
		wrongColumnMergeTwo.setOld_name("ci_sp_name和ci_sp_remark");
		wrongColumnMergeTwo.setCol_zhname("ci_sp_name_remark_ch");
		wrongColumnMergeTwo.setCol_type("varchar(512)");

		columnMerges.add(wrongColumnMergeTwo);

		String wrongStringTwo = new HttpClient()
				.addData("columnMergeString", JSON.toJSONString(columnMerges))
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("saveColMergeInfo")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));

		//错误的数据访问3：保存列合并时没有填写字段类型
		columnMerges.clear();
		Column_merge wrongColumnMergeThree = new Column_merge();
		wrongColumnMergeThree.setOld_name("ci_sp_name和ci_sp_remark");
		wrongColumnMergeThree.setCol_zhname("ci_sp_name_remark_ch");
		wrongColumnMergeThree.setCol_name("ci_sp_name_remark");

		columnMerges.add(wrongColumnMergeThree);

		String wrongStringThree = new HttpClient()
				.addData("columnMergeString", JSON.toJSONString(columnMerges))
				.addData("tableId", CODE_INFO_TABLE_ID)
				.post(getActionUrl("saveColMergeInfo")).getBodyString();
		ActionResult wrongResultThree = JsonUtil.toObjectSafety(wrongStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultThree.isSuccess(), is(false));
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
					" where column_name = " +
					" (select t1.column_name " +
					" from " + Table_column.TableName + " t1 " +
					" JOIN " + Column_merge.TableName + " t2 ON t1.table_id = t2.table_id " +
					" and t1.column_name = t2.col_name " +
					" where t2.col_merge_id = ?) ", 16161616L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			long beforeMergeCount = SqlOperator.queryNumber(db, " select count(1) from " + Column_merge.TableName + " where col_merge_id = ? ", 16161616L).orElseThrow(() -> new BusinessException("SQL查询错误"));

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
					" where column_name =" +
					" (select t1.column_name " +
					" from " + Table_column.TableName + " t1 " +
					" JOIN " + Column_merge.TableName + " t2 ON t1.table_id = t2.table_id " +
					" and t1.column_name = t2.col_name " +
					" where t2.col_merge_id = ?) ", 16161616L).orElseThrow(() -> new BusinessException("SQL查询错误"));
			long beforeMergeCount = SqlOperator.queryNumber(db, " select count(1) from " + Column_merge.TableName + " where col_merge_id = ? ", 16161616L).orElseThrow(() -> new BusinessException("SQL查询错误"));

			assertThat("模拟删除对sys_user表设置的列合并规则之前，table_column表中的测试数据被删除了", beforeColumnCount == 0, is(true));
			assertThat("模拟删除对sys_user表设置的列合并规则之前，column_merge表中的测试数据被删除了", beforeMergeCount == 0, is(true));
		}
	}

	/**
	 * 测试保存所有表清洗优先级
	 *
	 * 正确数据访问1：对ID为FIRST_DATABASESET_ID的数据库采集任务设置任务级别的清洗优先级
	 *                  1、字符替换
	 *                  2、字符补齐
	 *                  3、字符trim
	 *                  4、字符合并
	 * 错误的数据访问1：设置任务级别清洗优先级时，传递错误的colSetId
	 * 错误的测试用例未达到三组: 一个测试用例已经可以覆盖代码中所有可能出现的场景
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveAllTbCleanOrder(){
		//正确数据访问1：将sys_user、code_info、agent_info表、data_source表清洗优先级设置为1、字符替换 2、字符补齐 3、字符trim 4、字符合并
		JSONObject newSort = new JSONObject();
		newSort.put(CleanType.ZiFuTiHuan.getCode(), 1);
		newSort.put(CleanType.ZiFuBuQi.getCode(), 2);
		newSort.put(CleanType.ZiFuTrim.getCode(), 3);
		newSort.put(CleanType.ZiFuHeBing.getCode(), 4);

		try(DatabaseWrapper db = new DatabaseWrapper()){
			Result result = SqlOperator.queryResult(db, "select cp_or from " + Database_set.TableName + " where database_id = ?", FIRST_DATABASESET_ID);
			assertThat("共获得了1条数据", result.getRowCount(), is(1));
			assertThat("未定义全表清洗优先级", result.getString(0, "cp_or"), is(""));
		}

		String rightStringOne = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("sort", newSort.toJSONString())
				.post(getActionUrl("saveAllTbCleanOrder")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			Result result = SqlOperator.queryResult(db, "select cp_or from " + Database_set.TableName + " where database_id = ?", FIRST_DATABASESET_ID);
			assertThat("共获得了1条数据", result.getRowCount(), is(1));
			assertThat("定义全表清洗优先级,结果符合期望", result.getString(0, "cp_or"), is(newSort.toJSONString()));

			//断言ID为1001的数据库采集任务下所有的表的清洗顺序是否全部被更新为了所有表清洗优先级
			List<Object> tbOrders = SqlOperator.queryOneColumnList(db, "select ti_or from " + Table_info.TableName + " where database_id = ?", FIRST_DATABASESET_ID);
			for(Object order : tbOrders){
				assertThat("ID为1001的数据库采集任务下所有的表的清洗顺序全部被更新为了所有表清洗优先级", (String)order, is(newSort.toJSONString()));
			}

			//断言ID为1001的数据库采集任务下所有的字段清洗顺序是否全部被更新为了所有表清洗优先级
			List<Object> colOrders = SqlOperator.queryOneColumnList(db, "select tc_or from " + Table_column.TableName + " tc join "
					+ Table_info.TableName + " ti on ti.table_id = tc.table_id where ti.database_id = ?", FIRST_DATABASESET_ID);
			for(Object order : colOrders){
				assertThat("ID为1001的数据库采集任务下所有的字段清洗顺序全部被更新为了所有表清洗优先级", (String)order, is(newSort.toJSONString()));
			}
		}

		//错误的数据访问1：设置任务级别清洗优先级时，传递的tableId数组为空
		String wrongStringOne = new HttpClient()
				.addData("colSetId", UNEXPECTED_ID)
				.addData("sort", newSort.toJSONString())
				.post(getActionUrl("saveAllTbCleanOrder")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));
	}

	/**
	 * 测试回显全表清洗优先级
	 *
	 * 正确数据访问1：对ID为FIRST_DATABASESET_ID的数据库采集任务设置任务级别的清洗优先级，并尝试获取
	 *                  1、字符替换
	 *                  2、字符补齐
	 *                  3、字符trim
	 *                  4、字符合并
	 * 错误的数据访问1：获取任务级别清洗优先级时，传递错误的colSetId
	 * 错误的测试用例未达到三组: 一个测试用例已经可以覆盖代码中所有可能出现的场景
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getAllTbCleanOrder(){
		//正确数据访问1：对ID为FIRST_DATABASESET_ID的数据库采集任务设置任务级别的清洗优先级，并尝试获取
		JSONObject newSort = new JSONObject();
		newSort.put(CleanType.ZiFuTiHuan.getCode(), 1);
		newSort.put(CleanType.ZiFuBuQi.getCode(), 2);
		newSort.put(CleanType.ZiFuTrim.getCode(), 3);
		newSort.put(CleanType.ZiFuHeBing.getCode(), 4);

		try(DatabaseWrapper db = new DatabaseWrapper()){
			Result result = SqlOperator.queryResult(db, "select cp_or from " + Database_set.TableName + " where database_id = ?", FIRST_DATABASESET_ID);
			assertThat("共获得了1条数据", result.getRowCount(), is(1));
			assertThat("未定义全表清洗优先级", result.getString(0, "cp_or"), is(""));

			int execute = SqlOperator.execute(db, "update " + Database_set.TableName + " set cp_or = ? where database_id = ?", newSort.toJSONString(), FIRST_DATABASESET_ID);
			assertThat("对ID为FIRST_DATABASESET_ID的数据库采集任务设置任务级别的清洗优先级成功", execute, is(1));

			SqlOperator.commitTransaction(db);
		}

		String rightStringOne = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getAllTbCleanOrder")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		List<Map> mapList = rightResultOne.getDataForEntityList(Map.class);
		assertThat("共获得了4条数据", mapList.size(), is(4));
		for(Map map : mapList){
			if(map.get("code").equals(CleanType.ZiFuTiHuan.getCode())){
				assertThat("字符替换的顺序为1", map.get("order").equals(1), is(true));
			}else if(map.get("code").equals(CleanType.ZiFuBuQi.getCode())){
				assertThat("字符补齐的顺序为2", map.get("order").equals(2), is(true));
			}else if(map.get("code").equals(CleanType.ZiFuTrim.getCode())){
				assertThat("首尾去空的顺序为3", map.get("order").equals(3), is(true));
			}else if(map.get("code").equals(CleanType.ZiFuHeBing.getCode())){
				assertThat("字符合并的顺序为1", map.get("order").equals(4), is(true));
			}else{
				assertThat("出现了不符合期望的情况，code为 : " + map.get("code"), true, is(false));
			}
		}

		//错误的数据访问1：获取任务级别清洗优先级时，传递错误的colSetId
		String wrongStringOne = new HttpClient()
				.addData("colSetId", UNEXPECTED_ID)
				.post(getActionUrl("getAllTbCleanOrder")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));
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

			//断言该张表下的所有字段的清洗优先级都被更新了
			List<Object> orders = SqlOperator.queryOneColumnList(db, "select tc_or from " + Table_column.TableName + " where table_id = ?", SYS_USER_TABLE_ID);
			for(Object order : orders){
				assertThat("该表下的所有字段的清洗优先级都被更新了", (String)order, is(newSort.toJSONString()));
			}
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
	 * 测试根据表ID回显整表清洗优先级
	 *
	 * 正确数据访问1：尝试获取在构造初始化数据时对sys_user表定义的整表清洗优先级
	 * 错误的数据访问1：传入错误的colSetId
	 * 错误的测试用例未达到三组:以上两种情况已经可以覆盖getSingleTbCleanOrder表所有可能出现的情况了
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getSingleTbCleanOrder(){
		//正确数据访问1：尝试获取在构造初始化数据时对sys_user表定义的整表清洗优先级
		String rightStringOne = new HttpClient()
				.addData("tableId", SYS_USER_TABLE_ID)
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getSingleTbCleanOrder")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		List<Map> mapList = rightResultOne.getDataForEntityList(Map.class);
		assertThat("获取到了4条数据", mapList.size(), is(4));
		for(Map map : mapList){
			if(map.get("code").equals(CleanType.ZiFuBuQi.getCode())){
				assertThat("字符补齐的顺序为1", map.get("order").equals(1), is(true));
			}else if(map.get("code").equals(CleanType.ZiFuTiHuan.getCode())){
				assertThat("字符替换的顺序为2", map.get("order").equals(2), is(true));
			}else if(map.get("code").equals(CleanType.ZiFuHeBing.getCode())){
				assertThat("字符合并的顺序为3", map.get("order").equals(3), is(true));
			}else if(map.get("code").equals(CleanType.ZiFuTrim.getCode())){
				assertThat("首尾去空的顺序为1", map.get("order").equals(4), is(true));
			}else{
				assertThat("出现了不符合期望的情况，code为 : " + map.get("code"), true, is(false));
			}
		}

		//错误的数据访问1：传入错误的colSetId
		String wrongStringOne = new HttpClient()
				.addData("tableId", SYS_USER_TABLE_ID)
				.addData("colSetId", UNEXPECTED_ID)
				.post(getActionUrl("getSingleTbCleanOrder")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));
	}

	/**
	 * 测试保存单个字段清洗优先级
	 *
	 * 正确数据访问1：尝试对sys_user表的role_id字段设置清洗优先级为
	 *                  1、字符trim
	 *                  2、字符拆分
	 *                  3、码值转换
	 *                  4、时间转换
	 *                  5、字符替换
	 *                  6、字符补齐
	 * 错误的数据访问1：尝试对一个本次采集作业中没有的列设置清洗优先级
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
	 * 测试根据列ID回显列清洗优先级
	 *
	 * 正确数据访问1：尝试获取为sys_user表的role_id字段设置的清洗优先级
	 * 错误的数据访问1：尝试对一个不存在于sys_user表中的字段获取列清洗优先级
	 * 错误的测试用例未达到三组:以上两个测试用例已经可以覆盖getColCleanOrder方法所有可能出现的情况了
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getColCleanOrder(){
		//正确数据访问1：尝试获取为sys_user表的role_id字段设置的清洗优先级
		String rightStringOne = new HttpClient()
				.addData("columnId", 2004L)
				.addData("tableId", SYS_USER_TABLE_ID)
				.post(getActionUrl("getColCleanOrder")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		List<Map> mapList = rightResultOne.getDataForEntityList(Map.class);
		assertThat("获取到6条数据", mapList.size(), is(6));
		for(Map map : mapList){
			if(map.get("code").equals(CleanType.ZiFuTiHuan.getCode())){
				assertThat("字符替换的顺序为2", map.get("order").equals(2), is(true));
			}else if(map.get("code").equals(CleanType.ZiFuBuQi.getCode())){
				assertThat("字符补齐的顺序为1", map.get("order").equals(1), is(true));
			}else if(map.get("code").equals(CleanType.ZiFuTrim.getCode())){
				assertThat("首尾去空的顺序为6", map.get("order").equals(6), is(true));
			}else if(map.get("code").equals(CleanType.ZiFuChaiFen.getCode())){
				assertThat("字符拆分的顺序为5", map.get("order").equals(5), is(true));
			}else if(map.get("code").equals(CleanType.ShiJianZhuanHuan.getCode())){
				assertThat("日期格式化的顺序为3", map.get("order").equals(3), is(true));
			}else if(map.get("code").equals(CleanType.MaZhiZhuanHuan.getCode())){
				assertThat("码值转换的顺序为4", map.get("order").equals(4), is(true));
			}else{
				assertThat("出现了不符合期望的情况，code为 : " + map.get("code"), true, is(false));
			}
		}

		//错误的数据访问1：尝试对一个不存在于sys_user表中的字段获取列清洗优先级
		String wrongStringOne = new HttpClient()
				.addData("columnId", 2014L)
				.addData("tableId", SYS_USER_TABLE_ID)
				.post(getActionUrl("getColCleanOrder")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));
	}

	/**
	 * 测试保存列清洗信息
	 *
	 * 正确数据访问1：columnId为2002L，之前设置了字符补齐，但是保存的时候取消了字符补齐的勾选，同时做首尾去空
	 * 正确数据访问2：columnId为2005L，之前设置了字符替换，但是保存的时候取消了字符替换的勾选，同时做首尾去空
	 * 正确数据访问3：columnId为2011L，之前设置了日期格式化，但是保存的时候取消了日期格式化的勾选，同时做首尾去空
	 * 正确数据访问4：columnId为3003L，之前设置了列拆分，但是保存的时候取消了列拆分的勾选，同时做首尾去空
	 * 正确数据访问5：columnId为2010L，之前设置了码值转换，但是保存的时候取消了列拆分的勾选，同时做首尾去空
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
			long beforeCompCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2002L, CleanType.ZiFuBuQi.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			long beforeTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2002L, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
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
			long afterCompCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2002L, CleanType.ZiFuBuQi.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			long afterTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2002L, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在执行测试用例<正确数据访问1>之后，数据库中的数据符合预期", afterCompCount == 0 && afterTrimCount == 1, is(true));
		}

		//正确数据访问2：columnId为2005L，之前设置了字符替换，但是保存的时候取消了字符替换的勾选，同时做首尾去空
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long beforeReplaceCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2005L, CleanType.ZiFuTiHuan.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			long beforeTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2005L, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
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
			long afterReplaceCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2005L, CleanType.ZiFuTiHuan.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			long afterTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2005L, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在执行测试用例<正确数据访问2>之后，数据库中的数据符合预期", afterReplaceCount == 0 && afterTrimCount == 1, is(true));
		}

		//正确数据访问3：columnId为2011L，之前设置了日期格式化，但是保存的时候取消了日期格式化的勾选，同时做首尾去空
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long beforeFormatCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2011L, CleanType.ShiJianZhuanHuan.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			long beforeTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2011L, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
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
			long afterFormatCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2011L, CleanType.ShiJianZhuanHuan.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			long afterTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2011L, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在执行测试用例<正确数据访问3>之后，数据库中的数据符合预期", afterFormatCount == 0 && afterTrimCount == 1, is(true));
		}

		//正确数据访问4：columnId为3003L，之前设置了列拆分，但是保存的时候取消了列拆分的勾选，同时做首尾去空
		try(DatabaseWrapper db = new DatabaseWrapper()){
			List<Table_column> tableColumns = SqlOperator.queryList(db, Table_column.class, "select * from " + Table_column.TableName + " where column_name in" +
							" (select t1.column_name from "+ Table_column.TableName +" t1" +
							" JOIN " + Column_split.TableName + " t2 ON t1.column_name = t2.col_name " +
							" JOIN " + Column_clean.TableName + " t3 ON t2.col_clean_id = t3.col_clean_id " +
							" WHERE t2.col_clean_id = ? and t2.column_id = ? and t1.table_id = ? and t1.is_new = ?) ",
					101010102L, 3003L, CODE_INFO_TABLE_ID, IsFlag.Shi.getCode());
			assertThat("在执行测试用例<正确数据访问4>之前，拆分为ci、sp、classname的列在table_column表中存在", tableColumns.size() == 3, is(true));
			for(Table_column tableColumn : tableColumns){
				if(tableColumn.getColumn_id() == 141414L){
					assertThat("在执行测试用例<正确数据访问4>之前，拆分为ci的列在table_column表中存在", tableColumn.getColumn_name().equalsIgnoreCase("ci"), is(true));
				}else if(tableColumn.getColumn_id() == 151515L){
					assertThat("在执行测试用例<正确数据访问4>之前，拆分为sp的列在table_column表中存在", tableColumn.getColumn_name().equalsIgnoreCase("sp"), is(true));
				}else if(tableColumn.getColumn_id() == 161616L){
					assertThat("在执行测试用例<正确数据访问4>之前，拆分为classname的列在table_column表中存在", tableColumn.getColumn_name().equalsIgnoreCase("classname"), is(true));
				}else{
					assertThat("在执行测试用例<正确数据访问4>之前，在table_column表中查询到了不符合预期的列" + tableColumn.getColumn_name(), false, is(true));
				}
			}
			long beforeDelSpCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id in (101010103, 101010104, 101010105) ").orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在执行测试用例<正确数据访问4>之前，列拆分信息在column_split表中存在", beforeDelSpCount == 3, is(true));

			long beforeDelColCleanCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and clean_type = ?", 3003L, CleanType.ZiFuChaiFen.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在执行测试用例<正确数据访问4>之前，列拆分信息在column_clean表中不存在", beforeDelColCleanCount == 1, is(true));
			long beforeTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 3003L, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
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
			List<Table_column> tableColumns = SqlOperator.queryList(db, Table_column.class, "select * from " + Table_column.TableName + " where column_name in" +
							" (select t1.column_name from "+ Table_column.TableName +" t1" +
							" JOIN " + Column_split.TableName + " t2 ON t1.column_name = t2.col_name " +
							" JOIN " + Column_clean.TableName + " t3 ON t2.col_clean_id = t3.col_clean_id " +
							" WHERE t2.col_clean_id = ? and t2.column_id = ? and t1.table_id = ? and t1.is_new = ?) ",
					101010102L, 3003L, CODE_INFO_TABLE_ID, IsFlag.Shi.getCode());
			assertThat("在执行测试用例<正确数据访问4>之后，拆分为ci、sp、classname的列在table_column表中不存在", tableColumns.size() == 0, is(true));
			long afterDelSpCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_split.TableName + " where col_split_id in (101010103, 101010104, 101010105) ").orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在执行测试用例<正确数据访问4>之后，列拆分信息在column_split表中不存在", afterDelSpCount == 0, is(true));

			long afterDelColCleanCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " where column_id = ? and clean_type = ?", 3003L, CleanType.ZiFuChaiFen.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在执行测试用例<正确数据访问4>之后，列拆分信息在column_clean表中不存在", afterDelColCleanCount == 0, is(true));
			long afterTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 3003L, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在执行测试用例<正确数据访问4>之后，列首尾去空存在", afterTrimCount == 1, is(true));
		}

		//正确数据访问5：columnId为2010L，之前设置了码值转换，但是保存的时候取消了码值转换的勾选，同时做首尾去空
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long beforeCVCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2010L, CleanType.MaZhiZhuanHuan.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			long beforeTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2010L, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在执行测试用例<正确数据访问5>之前，数据库中的数据符合预期", beforeCVCount == 1 && beforeTrimCount == 0, is(true));
		}

		columnCleanParams.clear();

		ColumnCleanParam cleanParamFive = new ColumnCleanParam();

		cleanParamFive.setColumnId(2010L);
		cleanParamFive.setComplementFlag(false);
		cleanParamFive.setConversionFlag(false);
		cleanParamFive.setFormatFlag(false);
		cleanParamFive.setReplaceFlag(false);
		cleanParamFive.setSpiltFlag(false);
		cleanParamFive.setTrimFlag(true);

		columnCleanParams.add(cleanParamFive);

		String rightStringFive = new HttpClient()
				.addData("colCleanString", JSON.toJSONString(columnCleanParams))
				.post(getActionUrl("saveColCleanConfig")).getBodyString();
		ActionResult rightResultFive = JsonUtil.toObjectSafety(rightStringFive, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultFive.isSuccess(), is(true));

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long afterCVCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2010L, CleanType.MaZhiZhuanHuan.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			long afterTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Column_clean.TableName + " WHERE column_id = ? AND clean_type = ? ", 2010L, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在执行测试用例<正确数据访问5>之后，数据库中的数据符合预期", afterCVCount == 0 && afterTrimCount == 1, is(true));
		}
	}

	/**
	 * 保存配置数据清洗页面信息
	 *
	 * 正确数据访问1：colSetId为1001L，tableId为7001L，之前设置了字符补齐，但是保存的时候取消了字符补齐的勾选，同时做首尾去空
	 * 正确数据访问2：colSetId为1001L，tableId为7001L，之前设置了字符替换，但是保存的时候取消了字符替换的勾选，同时做首尾去空
	 * 正确数据访问3：colSetId为1001L，tableId为7001L，之前设置了字符替换和字符补齐，同时做了表首尾去空，但是在保存的时候，去掉了首尾去空的勾选
	 * 错误的测试用例未达到三组: 上面三组测试用例是结合初始化数据进行的，比较有代表性的。
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveDataCleanConfig(){
		//正确数据访问1：colSetId为1001L，tableId为7001L，之前设置了字符补齐，但是保存的时候取消了字符补齐的勾选，同时做首尾去空
		List<TableCleanParam> tableCleanParams = new ArrayList<>();

		try(DatabaseWrapper db = new DatabaseWrapper()){
			long beforeCompCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " WHERE table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuBuQi.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			long beforeTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " WHERE table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在执行测试用例<正确数据访问1>之前，数据库中的数据符合预期", beforeCompCount == 1 && beforeTrimCount == 0, is(true));
		}

		//因为所有表的清洗最后都落实到了字段上，所以下面给tableId为7001的表的每个字段设置字符补齐，并且在里面掺杂一个和表定义的补齐规则不同的，模拟字段本身自己定义的字符补齐规则
		try(DatabaseWrapper db = new DatabaseWrapper()){
			List<Object> columnIds = SqlOperator.queryOneColumnList(db, "select column_id from " + Table_column.TableName + " where table_id = ?", SYS_USER_TABLE_ID);
			for(Object columnId : columnIds){
				Column_clean columnClean = new Column_clean();
				if((long)columnId == 2002L){
					columnClean.setCol_clean_id(PrimayKeyGener.getNextId());
					columnClean.setColumn_id((long)columnId);
					columnClean.setClean_type(CleanType.ZiFuBuQi.getCode());
					columnClean.setFilling_type(FillingType.HouBuQi.getCode());
					columnClean.setCharacter_filling(StringUtil.string2Unicode("beyond"));
					columnClean.setFilling_length(6L);

					columnClean.add(db);
					break;
				}

				columnClean.setCol_clean_id(PrimayKeyGener.getNextId());
				columnClean.setColumn_id((long)columnId);
				columnClean.setClean_type(CleanType.ZiFuBuQi.getCode());
				columnClean.setFilling_type(FillingType.QianBuQi.getCode());
				columnClean.setCharacter_filling(StringUtil.string2Unicode("wzc"));
				columnClean.setFilling_length(3L);

				columnClean.add(db);
			}
			SqlOperator.commitTransaction(db);
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
			long afterCompCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " WHERE table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuBuQi.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			long afterTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " WHERE table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在执行测试用例<正确数据访问1>之后，数据库中的数据符合预期", afterCompCount == 0 && afterTrimCount == 1, is(true));

			long count = SqlOperator.queryNumber(db, "select count(cc.col_clean_id) from " + Column_clean.TableName + " cc join "
							+ Table_column.TableName + " tc on cc.column_id = tc.column_id where tc.table_id = ? and clean_type = ?",
					SYS_USER_TABLE_ID, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));

			long countCol = SqlOperator.queryNumber(db, "select count(column_id) from " + Table_column.TableName + " where table_id = ?", SYS_USER_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("字符首尾去空保存落实到了列清洗上", count == countCol, is(true));


			//sys_user表的字符补齐内容应该只剩下了column_id为2002的列
			Result result = SqlOperator.queryResult(db, "select filling_type, character_filling, filling_length from " + Column_clean.TableName
					+ " cc join " + Table_column.TableName + " tc on cc.column_id = tc.column_id where tc.table_id = ? and clean_type = ? and character_filling = ?", SYS_USER_TABLE_ID, CleanType.ZiFuBuQi.getCode(), StringUtil.string2Unicode("beyond"));
			assertThat("sys_user表的字符补齐内容应该只剩下了column_id为2002的列", result.getRowCount(), is(1));
			assertThat("sys_user表的字符补齐内容应该只剩下了column_id为2002的列,补齐方式符合期望", result.getString(0, "filling_type"), is(FillingType.HouBuQi.getCode()));
			assertThat("sys_user表的字符补齐内容应该只剩下了column_id为2002的列,补齐长度符合期望", result.getLong(0, "filling_length"), is(6L));

			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? and clean_type = ?", 2002L, CleanType.ZiFuBuQi.getCode());

			int execute = SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuTrim.getCode());
			assertThat("在执行测试用例<正确数据访问1>之后，删除新增的对sys_user表的首尾去空操作", execute == 1, is(true));

			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id in (select column_id from " + Table_column.TableName + " where table_id = ?) and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTrim.getCode());

			SqlOperator.commitTransaction(db);
		}

		//正确数据访问2：colSetId为1001L，tableId为7001L，之前设置了字符替换，但是保存的时候取消了字符替换的勾选，同时做首尾去空
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long beforeReplaceCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " WHERE table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			long beforeTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " WHERE table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在执行测试用例<正确数据访问2>之前，数据库中的数据符合预期", beforeReplaceCount == 1 && beforeTrimCount == 0, is(true));
		}

		//因为所有表的清洗最后都落实到了字段上，所以下面给tableId为7001的表的每个字段设置字符替换，并且在里面掺杂一个和表定义的替换规则不同的，模拟字段本身自己定义的字符替换规则
		try(DatabaseWrapper db = new DatabaseWrapper()){
			List<Object> columnIds = SqlOperator.queryOneColumnList(db, "select column_id from " + Table_column.TableName + " where table_id = ?", SYS_USER_TABLE_ID);
			for(Object columnId : columnIds){
				Column_clean columnClean = new Column_clean();
				if((long)columnId == 2003L){
					columnClean.setCol_clean_id(PrimayKeyGener.getNextId());
					columnClean.setColumn_id((long)columnId);
					columnClean.setClean_type(CleanType.ZiFuTiHuan.getCode());
					columnClean.setField(StringUtil.string2Unicode("shl"));
					columnClean.setReplace_feild(StringUtil.string2Unicode("sw"));

					columnClean.add(db);
					break;
				}

				columnClean.setCol_clean_id(PrimayKeyGener.getNextId());
				columnClean.setColumn_id((long)columnId);
				columnClean.setClean_type(CleanType.ZiFuTiHuan.getCode());
				columnClean.setField(StringUtil.string2Unicode("wzc"));
				columnClean.setReplace_feild(StringUtil.string2Unicode("wqp"));

				columnClean.add(db);
			}
			SqlOperator.commitTransaction(db);
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
			long afterReplaceCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " WHERE table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			long afterTrimCount = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName + " WHERE table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("在执行测试用例<正确数据访问2>之后，数据库中的数据符合预期", afterReplaceCount == 0 && afterTrimCount == 1, is(true));

			long count = SqlOperator.queryNumber(db, "select count(cc.col_clean_id) from " + Column_clean.TableName + " cc join "
							+ Table_column.TableName + " tc on cc.column_id = tc.column_id where tc.table_id = ? and clean_type = ?",
					SYS_USER_TABLE_ID, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));

			long countCol = SqlOperator.queryNumber(db, "select count(column_id) from " + Table_column.TableName + " where table_id = ?", SYS_USER_TABLE_ID).orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("字符首尾去空保存落实到了列清洗上", count == countCol, is(true));

			//sys_user表的字符替换内容应该只剩下了column_id为2003的列
			Result result = SqlOperator.queryResult(db, "select field, replace_feild from " + Column_clean.TableName
					+ " cc join " + Table_column.TableName + " tc on cc.column_id = tc.column_id where tc.table_id = ? and clean_type = ? and field = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTiHuan.getCode(), StringUtil.string2Unicode("shl"));
			assertThat("sys_user表的字符替换内容应该只剩下了column_id为2003的列", result.getRowCount(), is(1));
			assertThat("sys_user表的字符替换内容应该只剩下了column_id为2003的列,替换后字段符合期望", result.getString(0, "replace_feild"), is(StringUtil.string2Unicode("sw")));

			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id = ? and clean_type = ?", 2003L, CleanType.ZiFuTiHuan.getCode());

			SqlOperator.execute(db, "delete from " + Column_clean.TableName + " where column_id in (select column_id from " + Table_column.TableName + " where table_id = ?) and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTrim.getCode());

			int execute = SqlOperator.execute(db, "delete from " + Table_clean.TableName + " where table_id = ? AND clean_type = ? ", SYS_USER_TABLE_ID, CleanType.ZiFuTrim.getCode());
			assertThat("在执行测试用例<正确数据访问2>之后，删除新增的对sys_user表的首尾去空操作", execute == 1, is(true));

			SqlOperator.commitTransaction(db);
		}
	}

	@Test
	public void saveDataCleanConfigTwo(){
		//正确数据访问3：colSetId为1001L，tableId为7001L，之前设置了字符替换和字符补齐，同时做了表首尾去空，但是在保存的时候，去掉了首尾去空的勾选
		List<TableCleanParam> tableCleanParams = new ArrayList<>();
		//构造表清洗首尾去空的数据和列清洗首尾去空的数据
		try(DatabaseWrapper db = new DatabaseWrapper()){
			Table_clean tableClean = new Table_clean();
			tableClean.setClean_type(CleanType.ZiFuTrim.getCode());
			tableClean.setTable_id(SYS_USER_TABLE_ID);
			tableClean.setTable_clean_id(PrimayKeyGener.getNextId());

			tableClean.add(db);

			List<Object> columnIds = SqlOperator.queryOneColumnList(db, "select column_id from " + Table_column.TableName + " where table_id = ?", SYS_USER_TABLE_ID);
			for(Object columnId : columnIds){
				Column_clean columnClean = new Column_clean();
				columnClean.setColumn_id((long) columnId);
				columnClean.setCol_clean_id(PrimayKeyGener.getNextId());
				columnClean.setClean_type(CleanType.ZiFuTrim.getCode());

				columnClean.add(db);
			}

			SqlOperator.commitTransaction(db);
		}

		TableCleanParam cleanParamThree = new TableCleanParam();
		cleanParamThree.setTableId(SYS_USER_TABLE_ID);
		cleanParamThree.setComplementFlag(true);
		cleanParamThree.setReplaceFlag(true);
		cleanParamThree.setTableName("sys_user");
		cleanParamThree.setTrimFlag(false);

		tableCleanParams.add(cleanParamThree);

		String rightStringThree = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("tbCleanString", JSON.toJSONString(tableCleanParams))
				.post(getActionUrl("saveDataCleanConfig")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));
		Integer returnValueThree = (Integer) rightResultThree.getData();
		assertThat(returnValueThree == FIRST_DATABASESET_ID, is(true));

		//断言删除表清洗首尾去空和字段清洗首尾去空是否成功
		try(DatabaseWrapper db = new DatabaseWrapper()){
			long count = SqlOperator.queryNumber(db, "select count(1) from " + Table_clean.TableName
					+ " where table_id = ? and clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));

			long countTwo = SqlOperator.queryNumber(db, "select count(cc.col_clean_id) from " + Column_clean.TableName
					+ " cc join " + Table_column.TableName + " tc on cc.column_id = tc.column_id " +
					" where tc.table_id = ? and cc.clean_type = ?", SYS_USER_TABLE_ID, CleanType.ZiFuTrim.getCode()).orElseThrow(() -> new BusinessException("SQL查询错误"));

			assertThat("删除表清洗首尾去空和字段清洗首尾去空成功", count, is(0L));
			assertThat("删除表清洗首尾去空和字段清洗首尾去空成功", countTwo, is(0L));
		}
	}

	@After
	public void after(){
		InitAndDestDataForCleanConf.after();
	}
}
