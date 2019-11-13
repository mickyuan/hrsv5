package hrds.b.biz.agent.dbagentconf.fileconf;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.b.biz.agent.dbagentconf.BaseInitData;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "FileConfStepAction单元测试类", author = "WangZhengcheng")
public class FileConfStepActionTest extends WebBaseTestCase{

	private static final long FIRST_DATABASESET_ID = 1001L;

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
	 *      TODO 被测Action类写完之后，继续构造卸数文件相关数据
	 *      TODO ORC/PARQUET/SEQUENCEFILE不能设置换行符和数据列分隔符
	 *      16、构造卸数文件相关数据
	 *          16-1、构造采集sys_user表的存储方式为ORC，数据字符集为UTF-8
	 *          16-2、构造采集code_info表的存储方式为定长，换行符为|，列分隔符为空格，数据字符集为UTF-8
     *      17、构造所有表分隔符设置数据
	 *          17-1、给所有表设置储存方式为非定长，换行符为回车符，列分隔符为|，数据字符集为UTF-8
 *          18、构造orig_syso_info表测试数据，里面是当前系统中所有的码值信息,共有三条数据
 *          19、构造orig_code_info表测试数据,共有三条数据
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Before
	public void before(){
		InitAndDestDataForFileConf.before();
		//模拟登陆
		ActionResult actionResult = BaseInitData.simulatedLogin();
		assertThat("模拟登陆", actionResult.isSuccess(), is(true));
	}

	/**
	 * 测试根据数据库设置ID获得定义卸数文件页面初始信息
	 *
	 * 正确数据访问1：使用正确的colSetId访问，应该可以拿到两条数据
	 * 错误的数据访问1：使用错误的colSetId访问，应该拿不到任何数据，但是不会报错，访问正常返回
	 * 错误的测试用例未达到三组:getInitInfo只有一个参数
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getInitInfo(){
		//正确数据访问1：使用正确的colSetId访问，应该可以拿到两条数据
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getInitInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Map<String, Object> rightData = rightResult.getDataForMap(String.class, Object.class);
		Result rightResultOne = new Result((List<Map<String, Object>>)rightData.get("fileConf"));
		assertThat("根据测试数据，输入正确的colSetId查询到的非自定义采集表总条数应该有2条", rightData.get("totalSize"), is(2));
		for(int i = 0; i < rightResultOne.getRowCount(); i++){
			if(rightResultOne.getString(i, "table_name").equalsIgnoreCase("sys_user")){
				assertThat("获取到的非自定义采集表为sys_user，得到的表中文名为", rightResultOne.getString(i, "table_ch_name"), is("用户表"));
				//TODO 下面这个断言未完成，需要根据被测方法写完之后构造的测试数据把is()里面的期待值补全
				assertThat("获取到的非自定义采集表为sys_user，得到的数据存储方式为ORC", rightResultOne.getString(i, "dbfile_format"), is(""));
				assertThat("获取到的非自定义采集表为sys_user，得到的数据字符集为UTF-8", rightResultOne.getString(i, "database_code"), is(""));
			}else if(rightResultOne.getString(i, "table_name").equalsIgnoreCase("code_info")){
				assertThat("获取到的非自定义采集表为code_info，得到的数据存储方式为定长", rightResultOne.getString(i, "dbfile_format"), is(""));
				assertThat("获取到的非自定义采集表为code_info，得到的换行符为|", rightResultOne.getString(i, "row_separator"), is(""));
				assertThat("获取到的非自定义采集表为code_info，得到的列分隔符为空格", rightResultOne.getString(i, "database_separatorr"), is(""));
				assertThat("获取到的非自定义采集表为code_info，得到的数据字符集为UTF-8", rightResultOne.getString(i, "database_code"), is(""));
			}else{
				assertThat("获取到了不符合期望的数据，表名为" + rightResultOne.getString(i, "table_name"), true, is(false));
			}
		}

		//错误的数据访问1：使用错误的colSetId访问，应该拿不到任何数据，但是不会报错，访问正常返回
		long wrongColSetId = 99999L;
		String wrongString = new HttpClient()
				.addData("colSetId", wrongColSetId)
				.post(getActionUrl("getInitInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(true));
		Map<String, Object> wrongData = wrongResult.getDataForMap(String.class, Object.class);
		assertThat("根据测试数据，输入错误的colSetId查询到的非自定义采集表信息应该有0条，但是HTTP访问成功返回", wrongData.get("totalSize"), is(0));
	}

	/**
	 * 测试根据数据库设置ID获取所有表分隔符设置
	 *
	 * 正确数据访问1：使用正确的colSetId访问，应该可以拿到一条数据
	 * 错误的数据访问1：使用错误的colSetId访问，应该拿不到任何数据，但是不会报错，访问正常返回
	 * 错误的测试用例未达到三组:getAllTbSepConf()方法只有一个参数
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getAllTbSepConf(){
		//正确数据访问1：使用正确的colSetId访问，应该可以拿到一条数据
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getAllTbSepConf")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Result rightData = rightResult.getDataForResult();
		//TODO 下面的断言要等被测方法写好之后再进行补充
		assertThat("使用正确的colSetId访问，应该可以拿到一条数据", rightData.getRowCount(), is(1));
		assertThat("使用正确的colSetId访问，获得的抽取数据存储格式为非定长", rightData.getString(0, ""), is(""));
		assertThat("使用正确的colSetId访问，获得的换行符为回车符", rightData.getString(0, ""), is(""));
		assertThat("使用正确的colSetId访问，获得的列分隔符为|", rightData.getString(0, ""), is(""));
		assertThat("使用正确的colSetId访问，获得的字符集为UTF-8", rightData.getString(0, ""), is(""));

		//错误的数据访问1：使用错误的colSetId访问，应该拿不到任何数据，但是不会报错，访问正常返回
		long wrongColSetId = 99999L;
		String wrongString = new HttpClient()
				.addData("colSetId", wrongColSetId)
				.post(getActionUrl("getAllTbSepConf")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(true));
		Result wrongData = wrongResult.getDataForResult();
		assertThat("使用错误的colSetId访问，应该拿不到数据", wrongData.isEmpty(), is(true));
	}

	/**
	 * 测试保存所有表分隔符设置
	 *
	 * 正确数据访问1：构建数据存储方式为ORC，行分隔符和列分隔符不传，字符编码为UTF-8，数据库设置ID为1001的数据访问
	 * 正确数据访问2：构建数据存储方式为定长，行分隔符为|，列分隔符为/，字符编码为UTF-8，数据库设置ID为1001的数据访问
	 * 错误的数据访问1：构建数据存储方式为定长，不传字符编码
	 * 错误的数据访问2：构建数据存储方式为PARQUET，不传字符编码
	 * 错误的数据访问3：构建数据存储方式为SEQUENCEFILE，传了行分隔符
	 * 错误的数据访问4：构建数据存储方式为ORC，传了列分隔符
	 * 错误的数据访问5：构建数据存储方式为定长，行分隔符为|，列分隔符为/，字符编码为UTF-8，但是不传数据库设置ID
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveAllTbSepConf(){
		//正确数据访问1：构建数据存储方式为ORC，行分隔符和列分隔符不传，字符编码为UTF-8，数据库设置ID为1001的数据访问
		String rightStringOne = new HttpClient()
				.addData("", "")
				.addData("", "")
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveAllTbSepConf")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));
		try(DatabaseWrapper db = new DatabaseWrapper()){
			//保存后，校验数据库中新增是否成功
			//将新增的数据删除掉
			SqlOperator.commitTransaction(db);
		}

		//正确数据访问2：构建数据存储方式为定长，行分隔符为|，列分隔符为/，字符编码为UTF-8，数据库设置ID为1001的数据访问
		String rightStringTwo = new HttpClient()
				.addData("", "")
				.addData("", "|")
				.addData("", "/")
				.addData("", "")
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveAllTbSepConf")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		try(DatabaseWrapper db = new DatabaseWrapper()){
			//保存后，校验数据库中新增是否成功
			//将新增的数据删除掉
			SqlOperator.commitTransaction(db);
		}

		//错误的数据访问1：构建数据存储方式为定长，传行分隔符、列分隔符和数据库设置ID，但是不传字符编码
		String wrongStringOne = new HttpClient()
				.addData("", "")
				.addData("", "")
				.addData("", "")
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getAllTbSepConf")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));

		//错误的数据访问2：构建数据存储方式为PARQUET，传数据库设置ID，不传字符编码、行分隔符和列分隔符
		String wrongStringTwo = new HttpClient()
				.addData("", "")
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getAllTbSepConf")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));

		//错误的数据访问3：构建数据存储方式为SEQUENCEFILE，传了行分隔符和字符编码
		String wrongStringThree = new HttpClient()
				.addData("", "")
				.addData("", "")
				.addData("", "")
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getAllTbSepConf")).getBodyString();
		ActionResult wrongResultThree = JsonUtil.toObjectSafety(wrongStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultThree.isSuccess(), is(false));

		//错误的数据访问4：构建数据存储方式为ORC，传了列分隔符和字符编码
		String wrongStringFour = new HttpClient()
				.addData("", "")
				.addData("", "")
				.addData("", "")
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getAllTbSepConf")).getBodyString();
		ActionResult wrongResultFour = JsonUtil.toObjectSafety(wrongStringFour, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultFour.isSuccess(), is(false));

		//错误的数据访问5：构建数据存储方式为定长，行分隔符为|，列分隔符为/，字符编码为UTF-8，但是不传数据库设置ID
		String wrongStringFive = new HttpClient()
				.addData("", "")
				.addData("", "")
				.addData("", "")
				.addData("", "")
				.post(getActionUrl("getAllTbSepConf")).getBodyString();
		ActionResult wrongResultFive = JsonUtil.toObjectSafety(wrongStringFive, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultFive.isSuccess(), is(false));
	}

	/**
	 * 测试保存定义卸数文件配置
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveFileConf(){

	}

	@After
	public void after(){
		InitAndDestDataForFileConf.after();
	}

}
