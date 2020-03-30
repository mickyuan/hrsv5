package hrds.b.biz.agent.dbagentconf.fileconf;

import com.alibaba.fastjson.JSON;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.b.biz.agent.dbagentconf.BaseInitData;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.DataExtractType;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "FileConfStepAction单元测试类", author = "WangZhengcheng")
public class FileConfStepActionTest extends WebBaseTestCase{

	private static final long FIRST_DATABASESET_ID = 1001L;
	private static final long SYS_USER_TABLE_ID = 7001L;
	private static final long CODE_INFO_TABLE_ID = 7002L;
	private static final long AGENT_INFO_TABLE_ID = 7003L;
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
	 *      16、构造卸数文件相关数据
	 *          16-1、构造采集sys_user表，仅数据抽取，存储方式为非定长，换行符回车，列分隔符|，数据字符集为UTF-8
	 *          16-2、构造采集code_info表，仅数据抽取，存储方式为定长，数据字符集为UTF-8
	 *          16-3、构造采集agent_info表，抽取并入库，存储方式为ORC，数据字符集为UTF-8
	 *          16-4、构造采集data_source表，抽取并入库，非定长，换行符为|，列分隔符为空格，数据字符集为UTF-8
     *      17、构造orig_syso_info表测试数据，里面是当前系统中所有的码值信息,共有三条数据
     *      18、构造orig_code_info表测试数据,共有三条数据
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
	 * 正确数据访问1：使用正确的colSetId访问，应该可以拿到4条数据
	 * 错误的数据访问1：使用错误的colSetId访问，应该拿不到任何数据，但是不会报错，访问正常返回
	 * 错误的测试用例未达到三组:getInitInfo只有一个参数
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getInitInfo(){
		//正确数据访问1：使用正确的colSetId访问，应该可以拿到4条数据
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getInitInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Result rightResultOne = rightResult.getDataForResult();
		assertThat("根据测试数据，输入正确的colSetId查询到的采集表总条数应该有4条", rightResultOne.getRowCount(), is(4));
		for(int i = 0; i < rightResultOne.getRowCount(); i++){
			if(rightResultOne.getString(i, "table_name").equalsIgnoreCase("sys_user")){
				assertThat("获取到的非自定义采集表为sys_user，得到的表中文名为<用户表>", rightResultOne.getString(i, "table_ch_name"), is("用户表"));
				assertThat("获取到的非自定义采集表为sys_user，得到数据抽取方式为<仅数据抽取>", rightResultOne.getString(i, "data_extract_type"), is(DataExtractType.ShuJuKuChouQuLuoDi.getCode()));
				assertThat("获取到的非自定义采集表为sys_user，得到的数据存储格式为<非定长>", rightResultOne.getString(i, "dbfile_format"), is(FileFormat.FeiDingChang.getCode()));
				assertThat("获取到的非自定义采集表为sys_user，得到的行分隔符为<空格符>", rightResultOne.getString(i, "row_separator"), is("\n"));
				assertThat("获取到的非自定义采集表为sys_user，得到的列分隔符为<|>", rightResultOne.getString(i, "database_separatorr"), is("|"));
				assertThat("获取到的非自定义采集表为sys_user，得到的数据字符集为<UTF-8>", rightResultOne.getString(i, "database_code"), is(DataBaseCode.UTF_8.getCode()));
			}else if(rightResultOne.getString(i, "table_name").equalsIgnoreCase("code_info")){
				assertThat("获取到的非自定义采集表为code_info，得到的表中文名为<代码信息表>", rightResultOne.getString(i, "table_ch_name"), is("代码信息表"));
				assertThat("获取到的非自定义采集表为code_info，得到数据抽取方式为<仅数据抽取>", rightResultOne.getString(i, "data_extract_type"), is(DataExtractType.ShuJuKuChouQuLuoDi.getCode()));
				assertThat("获取到的非自定义采集表为code_info，得到的数据存储格式为<定长>", rightResultOne.getString(i, "dbfile_format"), is(FileFormat.DingChang.getCode()));
				assertThat("获取到的非自定义采集表为code_info，没有指定行分隔符", rightResultOne.getString(i, "row_separator"), is(""));
				assertThat("获取到的非自定义采集表为code_info，没有指定列分隔符", rightResultOne.getString(i, "database_separatorr"), is(""));
				assertThat("获取到的非自定义采集表为code_info，得到的数据字符集为<UTF-8>", rightResultOne.getString(i, "database_code"), is(DataBaseCode.UTF_8.getCode()));
			}else if(rightResultOne.getString(i, "table_name").equalsIgnoreCase("agent_info")){
				assertThat("获取到的非自定义采集表为agent_info，得到的表中文名为<Agent信息表>", rightResultOne.getString(i, "table_ch_name"), is("Agent信息表"));
				assertThat("获取到的非自定义采集表为agent_info，得到数据抽取方式为<数据抽取并入库>", rightResultOne.getString(i, "data_extract_type"), is(DataExtractType.ShuJuKuChouQuLuoDi.getCode()));
				assertThat("获取到的非自定义采集表为agent_info，得到的数据存储格式为<非定长>", rightResultOne.getString(i, "dbfile_format"), is(FileFormat.ORC.getCode()));
				assertThat("获取到的非自定义采集表为agent_info，不能指定行分隔符", rightResultOne.getString(i, "row_separator"), is(""));
				assertThat("获取到的非自定义采集表为agent_info，不能指定列分隔符", rightResultOne.getString(i, "database_separatorr"), is(""));
				assertThat("获取到的非自定义采集表为agent_info，得到的数据字符集为<UTF-8>", rightResultOne.getString(i, "database_code"), is(DataBaseCode.UTF_8.getCode()));
			}
			else if(rightResultOne.getString(i, "table_name").equalsIgnoreCase("data_source")){
				assertThat("获取到的非自定义采集表为data_source，得到的表中文名为<用户表>", rightResultOne.getString(i, "table_ch_name"), is("数据源表"));
				assertThat("获取到的非自定义采集表为data_source，得到数据抽取方式为<仅数据抽取>", rightResultOne.getString(i, "data_extract_type"), is(DataExtractType.ShuJuKuChouQuLuoDi.getCode()));
				assertThat("获取到的非自定义采集表为data_source，得到的数据存储格式为<非定长>", rightResultOne.getString(i, "dbfile_format"), is(FileFormat.FeiDingChang.getCode()));
				assertThat("获取到的非自定义采集表为data_source，得到的行分隔符为<空格符>", rightResultOne.getString(i, "row_separator"), is("|"));
				assertThat("获取到的非自定义采集表为data_source，得到的列分隔符为<|>", rightResultOne.getString(i, "database_separatorr"), is(" "));
				assertThat("获取到的非自定义采集表为data_source，得到的数据字符集为<UTF-8>", rightResultOne.getString(i, "database_code"), is(DataBaseCode.UTF_8.getCode()));
			}
			else{
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
		Result wrongData = wrongResult.getDataForResult();
		assertThat("根据测试数据，输入错误的colSetId查询到的非自定义采集表信息应该有0条，但是HTTP访问成功返回", wrongData.isEmpty(), is(true));
	}

	/**
	 * 测试根据数据抽取方式返回卸数文件格式
	 *
	 * 正确数据访问1：数据抽取方式为仅抽取
	 * 正确数据访问2：数据抽取方式为抽取及入库
	 * 错误的数据访问1：传入的数据抽取方式不是从代码项中取值
	 * 错误的测试用例未达到三组: 以上三个测试用例已经足够覆盖所有访问场景
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getFileFormatByExtractType(){
		//正确数据访问1：数据抽取方式为仅抽取
		String rightString = new HttpClient()
				.addData("extractType", DataExtractType.ShuJuKuChouQuLuoDi.getCode())
				.post(getActionUrl("getFileFormatByExtractType")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		Map<String, String> dataOne = rightResult.getDataForMap(String.class, String.class);
		assertThat("共有3对Entry", dataOne.size(), is(3));
		for(Map.Entry<String, String> entry : dataOne.entrySet()){
			if(entry.getKey().equalsIgnoreCase("定长")){
				assertThat("定长格式符合期望", entry.getValue(), is(FileFormat.DingChang.getCode()));
			}else if(entry.getKey().equalsIgnoreCase("非定长")){
				assertThat("非定长格式符合期望", entry.getValue(), is(FileFormat.FeiDingChang.getCode()));
			}else if(entry.getKey().equalsIgnoreCase("CSV")){
				assertThat("CSV格式符合期望", entry.getValue(), is(FileFormat.CSV.getCode()));
			}else {
				assertThat("出现了不符合期望的文件格式，格式为" + entry.getKey(), true, is(false));
			}
		}

		//正确数据访问2：数据抽取方式为抽取及入库
		String rightStringTwo = new HttpClient()
				.addData("extractType", DataExtractType.ShuJuKuChouQuLuoDi.getCode())
				.post(getActionUrl("getFileFormatByExtractType")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		Map<String, String> dataTwo = rightResultTwo.getDataForMap(String.class, String.class);
		assertThat("共有5对Entry", dataTwo.size(), is(5));
		for(Map.Entry<String, String> entry : dataTwo.entrySet()){
			if(entry.getKey().equalsIgnoreCase("ORC")){
				assertThat("ORC格式符合期望", entry.getValue(), is(FileFormat.ORC.getCode()));
			}else if(entry.getKey().equalsIgnoreCase("非定长")){
				assertThat("定长格式符合期望", entry.getValue(), is(FileFormat.FeiDingChang.getCode()));
			}else if(entry.getKey().equalsIgnoreCase("CSV")){
				assertThat("CSV格式符合期望", entry.getValue(), is(FileFormat.CSV.getCode()));
			}else if(entry.getKey().equalsIgnoreCase("PARQUET")){
				assertThat("PARQUET格式符合期望", entry.getValue(), is(FileFormat.PARQUET.getCode()));
			}else if(entry.getKey().equalsIgnoreCase("SEQUENCEFILE")){
				assertThat("SEQUENCEFILE格式符合期望", entry.getValue(), is(FileFormat.SEQUENCEFILE.getCode()));
			}else {
				assertThat("出现了不符合期望的文件格式，格式为" + entry.getKey(), true, is(false));
			}
		}

		//错误的数据访问1：传入的数据抽取方式不是从代码项中取值
		String wrongDataExtractType = "3";
		String wrongString = new HttpClient()
				.addData("extractType", wrongDataExtractType)
				.post(getActionUrl("getFileFormatByExtractType")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试保存定义卸数文件配置
	 *
	 * 正确数据访问1：对sys_user修改保存，将存储目的地从/root，修改为/home/wzc
	 * 正确数据访问2：对code_info和agent_info保存修改，code_info定义为抽取并入库，保存为PARQUET格式，字符集为UTF-8；agent_info定义为仅数据抽取，抽取成CSV格式，不指定行分隔符合列分隔符，字符集为UTF-8，保存目录为/root
	 * 错误的数据访问1：对sys_user修改保存，将存储目的地从/root，修改为/home/wzc,但是保存为非定长文件，没有指定行分隔符
	 * 错误的数据访问2：对sys_user修改保存，将存储目的地从/root，修改为/home/wzc,但是保存为非定长文件，没有指定列分隔符
	 * 错误的数据访问3：将code_info定义为抽取并入库，保存为PARQUET格式，但是指定了行分隔符
	 * 错误的数据访问4：将code_info定义为抽取并入库，保存为PARQUET格式，但是指定了列分隔符
	 * 错误的数据访问5：将code_info定义为抽取并入库，保存为ORC格式，但是指定了行分隔符
	 * 错误的数据访问6：将code_info定义为抽取并入库，保存为ORC格式，但是指定了列分隔符
	 * 错误的数据访问7：将code_info定义为抽取并入库，保存为SEQUENCEFILE格式，但是指定了行分隔符
	 * 错误的数据访问8：将code_info定义为抽取并入库，保存为SEQUENCEFILE格式，但是指定了列分隔符
	 * 错误的数据访问9：将code_info定义为抽取并入库，保存为非定长格式，但是没有指定行分隔符
	 * 错误的数据访问10：将code_info定义为抽取并入库，保存为非定长格式，但是没有指定列分隔符
	 * 错误的数据访问11：将code_info定义为抽取并入库，保存为定长格式
	 * 错误的数据访问12：将code_info定义为仅抽取，保存为ORC格式
	 * 错误的数据访问13：将code_info定义为仅抽取，保存为Parquet格式
	 * 错误的数据访问14：将code_info定义为仅抽取，保存为SEQUENCEFILE格式
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveFileConf(){
		List<Data_extraction_def> extractionDefs = new ArrayList<>();
		//正确数据访问1：对sys_user修改保存，将存储目的地从/root，修改为/home/wzc
		Data_extraction_def sysUserDefs = new Data_extraction_def();
		sysUserDefs.setDed_id(7788L);
		sysUserDefs.setTable_id(SYS_USER_TABLE_ID);
		sysUserDefs.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		sysUserDefs.setIs_header(IsFlag.Shi.getCode());
		sysUserDefs.setDatabase_code(DataBaseCode.UTF_8.getCode());
		sysUserDefs.setRow_separator("\n");
		sysUserDefs.setDatabase_separatorr("|");
		sysUserDefs.setDbfile_format(FileFormat.FeiDingChang.getCode());
		sysUserDefs.setPlane_url("/home/wzc");

		extractionDefs.add(sysUserDefs);

		String rightString = new HttpClient()
				.addData("extractionDefString", JSON.toJSONString(extractionDefs))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveFileConf")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Integer returnValue = (Integer) rightResult.getData();
		assertThat(returnValue == FIRST_DATABASESET_ID, is(true));

		//验证数据库中的数据是否正确
		try(DatabaseWrapper db = new DatabaseWrapper()){
			Result result = SqlOperator.queryResult(db, "select * from " + Data_extraction_def.TableName + " where table_id = ?", SYS_USER_TABLE_ID);
			assertThat("查询得到的数据有一条", result.getRowCount(), is(1));
			assertThat("sys_user表配置的数据抽取定义中，抽取方式为<仅数据抽取>", result.getString(0, "data_extract_type"), is(DataExtractType.ShuJuKuChouQuLuoDi.getCode()));
			assertThat("sys_user表配置的数据抽取定义中，卸数文件格式为<非定长>", result.getString(0, "dbfile_format"), is(FileFormat.FeiDingChang.getCode()));
			assertThat("sys_user表配置的数据抽取定义中，行分隔符为<回车符>", result.getString(0, "row_separator"), is("\n"));
			assertThat("sys_user表配置的数据抽取定义中，列分隔符为<|>", result.getString(0, "database_separatorr"), is(StringUtil.string2Unicode("|")));
			assertThat("sys_user表配置的数据抽取定义中，数据落地目录为</home/wzc>", result.getString(0, "plane_url").equalsIgnoreCase("/home/wzc"), is(true));
			assertThat("sys_user表配置的数据抽取定义中，数据落地编码为<UTF-8>", result.getString(0, "database_code"), is(DataBaseCode.UTF_8.getCode()));
		}

		extractionDefs.clear();

		//正确数据访问2：对code_info和agent_info保存修改，code_info定义为抽取并入库，保存为PARQUET格式，字符集为UTF-8；agent_info定义为仅数据抽取，抽取成CSV格式，不指定行分隔符合列分隔符，字符集为UTF-8，保存目录为/root
		for(int i = 0; i < 2; i++){
			long id;
			long tableId;
			String extractType;
			String rowSeparator;
			String databaseSeparatorr;
			String fileFormat;
			String planeUrl;
			switch (i){
				case 0 :
					id = 7789L;
					tableId = CODE_INFO_TABLE_ID;
					extractType = DataExtractType.ShuJuKuChouQuLuoDi.getCode();
					rowSeparator = "";
					databaseSeparatorr = "";
					fileFormat = FileFormat.PARQUET.getCode();
					planeUrl = "";
					break;
				case 1 :
					id = 7790L;
					tableId = AGENT_INFO_TABLE_ID;
					extractType = DataExtractType.ShuJuKuChouQuLuoDi.getCode();
					rowSeparator = "";
					databaseSeparatorr = "";
					fileFormat = FileFormat.CSV.getCode();
					planeUrl = "/root";
					break;
				default:
					id = UNEXPECTED_ID;
					tableId = UNEXPECTED_ID;
					extractType = "unexpected_extractType";
					rowSeparator = "unexpected_rowSeparator";
					databaseSeparatorr = "unexpected_databaseSeparatorr";
					fileFormat = "unexpected_fileFormat";
					planeUrl = "unexpected_planeUrl";
			}
			Data_extraction_def def = new Data_extraction_def();
			def.setDed_id(id);
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

		String rightStringTwo = new HttpClient()
				.addData("extractionDefString", JSON.toJSONString(extractionDefs))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveFileConf")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		Integer returnValueTwo = (Integer) rightResultTwo.getData();
		assertThat(returnValueTwo == FIRST_DATABASESET_ID, is(true));

		//验证保存后数据库中的内容是否符合预期
		try(DatabaseWrapper db = new DatabaseWrapper()){
			Result result = SqlOperator.queryResult(db, "select * from " + Data_extraction_def.TableName + " where table_id = ?", CODE_INFO_TABLE_ID);
			assertThat("查询得到的数据有一条", result.getRowCount(), is(1));
			assertThat("code_info表配置的数据抽取定义中，抽取方式为<数据抽取并入库>", result.getString(0, "data_extract_type"), is(DataExtractType.ShuJuKuChouQuLuoDi.getCode()));
			assertThat("code_info表配置的数据抽取定义中，卸数文件格式为<PARQUET>", result.getString(0, "dbfile_format"), is(FileFormat.PARQUET.getCode()));
			assertThat("code_info表配置的数据抽取定义中，PARQUET文件不能定义行分隔符", result.getString(0, "row_separator"), is(""));
			assertThat("code_info表配置的数据抽取定义中，PARQUET文件不能定义列分隔符", result.getString(0, "database_separatorr"), is(""));
			assertThat("code_info表配置的数据抽取定义中，PARQUET文件不能定义数据落地目录", result.getString(0, "plane_url").equalsIgnoreCase(""), is(true));
			assertThat("code_info表配置的数据抽取定义中，数据落地编码为<UTF-8>", result.getString(0, "database_code"), is(DataBaseCode.UTF_8.getCode()));

			Result resultTwo = SqlOperator.queryResult(db, "select * from " + Data_extraction_def.TableName + " where table_id = ?", AGENT_INFO_TABLE_ID);
			assertThat("查询得到的数据有一条", resultTwo.getRowCount(), is(1));
			assertThat("agent_info表配置的数据抽取定义中，抽取方式为<仅数据抽取>", resultTwo.getString(0, "data_extract_type"), is(DataExtractType.ShuJuKuChouQuLuoDi.getCode()));
			assertThat("agent_info表配置的数据抽取定义中，卸数文件格式为<CSV>", resultTwo.getString(0, "dbfile_format"), is(FileFormat.CSV.getCode()));
			assertThat("agent_info表配置的数据抽取定义中，CSV文件没有定义行分隔符", resultTwo.getString(0, "row_separator"), is(""));
			assertThat("agent_info表配置的数据抽取定义中，CSV文件没有定义列分隔符", resultTwo.getString(0, "database_separatorr"), is(""));
			assertThat("agent_info表配置的数据抽取定义中，定义数据落地目录为</root>", resultTwo.getString(0, "plane_url").equalsIgnoreCase("/root"), is(true));
			assertThat("agent_info表配置的数据抽取定义中，数据落地编码为<UTF-8>", resultTwo.getString(0, "database_code"), is(DataBaseCode.UTF_8.getCode()));
		}

		extractionDefs.clear();

		//错误的数据访问1：对sys_user修改保存，将存储目的地从/root，修改为/home/wzc,但是保存为非定长文件，没有指定行分隔符
		Data_extraction_def wrongSysUserDefsOne = new Data_extraction_def();
		wrongSysUserDefsOne.setDed_id(7792L);
		wrongSysUserDefsOne.setTable_id(SYS_USER_TABLE_ID);
		wrongSysUserDefsOne.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		wrongSysUserDefsOne.setIs_header(IsFlag.Shi.getCode());
		wrongSysUserDefsOne.setDatabase_code(DataBaseCode.UTF_8.getCode());
		wrongSysUserDefsOne.setDatabase_separatorr("|");
		wrongSysUserDefsOne.setDbfile_format(FileFormat.FeiDingChang.getCode());
		wrongSysUserDefsOne.setPlane_url("/home/wzc");

		extractionDefs.add(wrongSysUserDefsOne);

		String wrongStringOne = new HttpClient()
				.addData("extractionDefString", JSON.toJSONString(extractionDefs))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveFileConf")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));

		extractionDefs.clear();

		//错误的数据访问2：对sys_user修改保存，将存储目的地从/root，修改为/home/wzc,但是保存为非定长文件，没有指定列分隔符
		Data_extraction_def wrongSysUserDefsTwo = new Data_extraction_def();
		wrongSysUserDefsTwo.setDed_id(7793L);
		wrongSysUserDefsTwo.setTable_id(SYS_USER_TABLE_ID);
		wrongSysUserDefsTwo.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		wrongSysUserDefsTwo.setIs_header(IsFlag.Shi.getCode());
		wrongSysUserDefsTwo.setDatabase_code(DataBaseCode.UTF_8.getCode());
		wrongSysUserDefsTwo.setRow_separator("\n");
		wrongSysUserDefsTwo.setDbfile_format(FileFormat.FeiDingChang.getCode());
		wrongSysUserDefsTwo.setPlane_url("/home/wzc");

		extractionDefs.add(wrongSysUserDefsTwo);

		String wrongStringTwo = new HttpClient()
				.addData("extractionDefString", JSON.toJSONString(extractionDefs))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveFileConf")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));

		extractionDefs.clear();

		//错误的数据访问3：将code_info定义为抽取并入库，保存为PARQUET格式，但是指定了行分隔符
		Data_extraction_def wrongSysUserDefsThree = new Data_extraction_def();
		wrongSysUserDefsThree.setDed_id(7794L);
		wrongSysUserDefsThree.setTable_id(CODE_INFO_TABLE_ID);
		wrongSysUserDefsThree.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		wrongSysUserDefsThree.setRow_separator("\n");
		wrongSysUserDefsThree.setDatabase_code(DataBaseCode.UTF_8.getCode());
		wrongSysUserDefsThree.setDbfile_format(FileFormat.PARQUET.getCode());
		wrongSysUserDefsThree.setIs_header(IsFlag.Shi.getCode());

		extractionDefs.add(wrongSysUserDefsThree);

		String wrongStringThree = new HttpClient()
				.addData("extractionDefString", JSON.toJSONString(extractionDefs))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveFileConf")).getBodyString();
		ActionResult wrongResultThree = JsonUtil.toObjectSafety(wrongStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultThree.isSuccess(), is(false));

		extractionDefs.clear();
		//错误的数据访问4：将code_info定义为抽取并入库，保存为PARQUET格式，但是指定了列分隔符
		Data_extraction_def wrongSysUserDefsFour = new Data_extraction_def();
		wrongSysUserDefsFour.setDed_id(7795L);
		wrongSysUserDefsFour.setTable_id(CODE_INFO_TABLE_ID);
		wrongSysUserDefsFour.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		wrongSysUserDefsFour.setDatabase_separatorr("|");
		wrongSysUserDefsFour.setDatabase_code(DataBaseCode.UTF_8.getCode());
		wrongSysUserDefsFour.setDbfile_format(FileFormat.PARQUET.getCode());
		wrongSysUserDefsFour.setIs_header(IsFlag.Shi.getCode());

		extractionDefs.add(wrongSysUserDefsFour);

		String wrongStringFour = new HttpClient()
				.addData("extractionDefString", JSON.toJSONString(extractionDefs))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveFileConf")).getBodyString();
		ActionResult wrongResultFour = JsonUtil.toObjectSafety(wrongStringFour, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultFour.isSuccess(), is(false));

		extractionDefs.clear();

		//错误的数据访问5：将code_info定义为抽取并入库，保存为ORC格式，但是指定了行分隔符
		Data_extraction_def wrongSysUserDefsFive = new Data_extraction_def();
		wrongSysUserDefsFive.setDed_id(7796L);
		wrongSysUserDefsFive.setTable_id(CODE_INFO_TABLE_ID);
		wrongSysUserDefsFive.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		wrongSysUserDefsFive.setRow_separator("\n");
		wrongSysUserDefsFive.setDatabase_code(DataBaseCode.UTF_8.getCode());
		wrongSysUserDefsFive.setDbfile_format(FileFormat.ORC.getCode());
		wrongSysUserDefsFive.setIs_header(IsFlag.Shi.getCode());

		extractionDefs.add(wrongSysUserDefsFive);

		String wrongStringFive = new HttpClient()
				.addData("extractionDefString", JSON.toJSONString(extractionDefs))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveFileConf")).getBodyString();
		ActionResult wrongResultFive = JsonUtil.toObjectSafety(wrongStringFive, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultFive.isSuccess(), is(false));

		extractionDefs.clear();

		//错误的数据访问6：将code_info定义为抽取并入库，保存为ORC格式，但是指定了列分隔符
		Data_extraction_def wrongSysUserDefsSix = new Data_extraction_def();
		wrongSysUserDefsSix.setDed_id(7797L);
		wrongSysUserDefsSix.setTable_id(CODE_INFO_TABLE_ID);
		wrongSysUserDefsSix.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		wrongSysUserDefsSix.setDatabase_separatorr("|");
		wrongSysUserDefsSix.setDatabase_code(DataBaseCode.UTF_8.getCode());
		wrongSysUserDefsSix.setDbfile_format(FileFormat.ORC.getCode());
		wrongSysUserDefsSix.setIs_header(IsFlag.Shi.getCode());

		extractionDefs.add(wrongSysUserDefsSix);

		String wrongStringSix = new HttpClient()
				.addData("extractionDefString", JSON.toJSONString(extractionDefs))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveFileConf")).getBodyString();
		ActionResult wrongResultSix = JsonUtil.toObjectSafety(wrongStringSix, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultSix.isSuccess(), is(false));

		extractionDefs.clear();

		//错误的数据访问7：将code_info定义为抽取并入库，保存为SEQUENCEFILE格式，但是指定了行分隔符
		Data_extraction_def wrongSysUserDefsSenven = new Data_extraction_def();
		wrongSysUserDefsSenven.setDed_id(7798L);
		wrongSysUserDefsSenven.setTable_id(CODE_INFO_TABLE_ID);
		wrongSysUserDefsSenven.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		wrongSysUserDefsSenven.setRow_separator("\n");
		wrongSysUserDefsSenven.setDatabase_code(DataBaseCode.UTF_8.getCode());
		wrongSysUserDefsSenven.setDbfile_format(FileFormat.ORC.getCode());
		wrongSysUserDefsSenven.setIs_header(IsFlag.Shi.getCode());

		extractionDefs.add(wrongSysUserDefsSenven);

		String wrongStringSenven = new HttpClient()
				.addData("extractionDefString", JSON.toJSONString(extractionDefs))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveFileConf")).getBodyString();
		ActionResult wrongResultSenven = JsonUtil.toObjectSafety(wrongStringSenven, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultSenven.isSuccess(), is(false));

		extractionDefs.clear();

		//错误的数据访问8：将code_info定义为抽取并入库，保存为SEQUENCEFILE格式，但是指定了列分隔符
		Data_extraction_def wrongSysUserDefsEight = new Data_extraction_def();
		wrongSysUserDefsEight.setDed_id(7799L);
		wrongSysUserDefsEight.setTable_id(CODE_INFO_TABLE_ID);
		wrongSysUserDefsEight.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		wrongSysUserDefsEight.setDatabase_separatorr("|");
		wrongSysUserDefsEight.setDatabase_code(DataBaseCode.UTF_8.getCode());
		wrongSysUserDefsEight.setDbfile_format(FileFormat.ORC.getCode());
		wrongSysUserDefsEight.setIs_header(IsFlag.Shi.getCode());

		extractionDefs.add(wrongSysUserDefsEight);

		String wrongStringEight = new HttpClient()
				.addData("extractionDefString", JSON.toJSONString(extractionDefs))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveFileConf")).getBodyString();
		ActionResult wrongResultEight = JsonUtil.toObjectSafety(wrongStringEight, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultEight.isSuccess(), is(false));

		extractionDefs.clear();
		//错误的数据访问9：将code_info定义为抽取并入库，保存为非定长格式，但是没有指定行分隔符
		Data_extraction_def wrongSysUserDefsNine = new Data_extraction_def();
		wrongSysUserDefsNine.setDed_id(7800L);
		wrongSysUserDefsNine.setTable_id(CODE_INFO_TABLE_ID);
		wrongSysUserDefsNine.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		wrongSysUserDefsNine.setDatabase_separatorr("|");
		wrongSysUserDefsNine.setDatabase_code(DataBaseCode.UTF_8.getCode());
		wrongSysUserDefsNine.setDbfile_format(FileFormat.FeiDingChang.getCode());
		wrongSysUserDefsNine.setIs_header(IsFlag.Shi.getCode());

		extractionDefs.add(wrongSysUserDefsNine);

		String wrongStringNine = new HttpClient()
				.addData("extractionDefString", JSON.toJSONString(extractionDefs))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveFileConf")).getBodyString();
		ActionResult wrongResultNine = JsonUtil.toObjectSafety(wrongStringNine, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultNine.isSuccess(), is(false));

		extractionDefs.clear();

		//错误的数据访问10：将code_info定义为抽取并入库，保存为非定长格式，但是没有指定列分隔符
		Data_extraction_def wrongSysUserDefsTen = new Data_extraction_def();
		wrongSysUserDefsTen.setDed_id(7801L);
		wrongSysUserDefsTen.setTable_id(CODE_INFO_TABLE_ID);
		wrongSysUserDefsTen.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		wrongSysUserDefsTen.setRow_separator("\n");
		wrongSysUserDefsTen.setDatabase_code(DataBaseCode.UTF_8.getCode());
		wrongSysUserDefsTen.setDbfile_format(FileFormat.FeiDingChang.getCode());
		wrongSysUserDefsTen.setIs_header(IsFlag.Shi.getCode());

		extractionDefs.add(wrongSysUserDefsTen);

		String wrongStringTen = new HttpClient()
				.addData("extractionDefString", JSON.toJSONString(extractionDefs))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveFileConf")).getBodyString();
		ActionResult wrongResultTen = JsonUtil.toObjectSafety(wrongStringTen, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTen.isSuccess(), is(false));

		extractionDefs.clear();

		//错误的数据访问11：将code_info定义为抽取并入库，保存为定长格式
		Data_extraction_def wrongSysUserDefsEle = new Data_extraction_def();
		wrongSysUserDefsEle.setDed_id(7802L);
		wrongSysUserDefsEle.setTable_id(CODE_INFO_TABLE_ID);
		wrongSysUserDefsEle.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		wrongSysUserDefsEle.setRow_separator("\n");
		wrongSysUserDefsEle.setDatabase_code(DataBaseCode.UTF_8.getCode());
		wrongSysUserDefsEle.setDbfile_format(FileFormat.DingChang.getCode());
		wrongSysUserDefsEle.setIs_header(IsFlag.Shi.getCode());

		extractionDefs.add(wrongSysUserDefsEle);

		String wrongStringEle = new HttpClient()
				.addData("extractionDefString", JSON.toJSONString(extractionDefs))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveFileConf")).getBodyString();
		ActionResult wrongResultEle = JsonUtil.toObjectSafety(wrongStringEle, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultEle.isSuccess(), is(false));

		extractionDefs.clear();

		//错误的数据访问12：将code_info定义为仅抽取，保存为ORC格式
		Data_extraction_def wrongSysUserDefsTwe = new Data_extraction_def();
		wrongSysUserDefsTwe.setDed_id(7803L);
		wrongSysUserDefsTwe.setTable_id(CODE_INFO_TABLE_ID);
		wrongSysUserDefsTwe.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		wrongSysUserDefsTwe.setRow_separator("\n");
		wrongSysUserDefsTwe.setDatabase_code(DataBaseCode.UTF_8.getCode());
		wrongSysUserDefsTwe.setDbfile_format(FileFormat.ORC.getCode());
		wrongSysUserDefsTwe.setIs_header(IsFlag.Shi.getCode());

		extractionDefs.add(wrongSysUserDefsTwe);

		String wrongStringTwe = new HttpClient()
				.addData("extractionDefString", JSON.toJSONString(extractionDefs))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveFileConf")).getBodyString();
		ActionResult wrongResultTwe = JsonUtil.toObjectSafety(wrongStringTwe, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwe.isSuccess(), is(false));

		extractionDefs.clear();

		//错误的数据访问13：将code_info定义为仅抽取，保存为Parquet格式
		Data_extraction_def wrongSysUserDefsThi = new Data_extraction_def();
		wrongSysUserDefsThi.setDed_id(7804L);
		wrongSysUserDefsThi.setTable_id(CODE_INFO_TABLE_ID);
		wrongSysUserDefsThi.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		wrongSysUserDefsThi.setRow_separator("\n");
		wrongSysUserDefsThi.setDatabase_code(DataBaseCode.UTF_8.getCode());
		wrongSysUserDefsThi.setDbfile_format(FileFormat.PARQUET.getCode());
		wrongSysUserDefsThi.setIs_header(IsFlag.Shi.getCode());

		extractionDefs.add(wrongSysUserDefsThi);

		String wrongStringThi = new HttpClient()
				.addData("extractionDefString", JSON.toJSONString(extractionDefs))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveFileConf")).getBodyString();
		ActionResult wrongResultThi = JsonUtil.toObjectSafety(wrongStringThi, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultThi.isSuccess(), is(false));

		extractionDefs.clear();

		//错误的数据访问14：将code_info定义为仅抽取，保存为SEQUENCEFILE格式
		Data_extraction_def wrongSysUserDefsFourt = new Data_extraction_def();
		wrongSysUserDefsFourt.setDed_id(7804L);
		wrongSysUserDefsFourt.setTable_id(CODE_INFO_TABLE_ID);
		wrongSysUserDefsFourt.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		wrongSysUserDefsFourt.setRow_separator("\n");
		wrongSysUserDefsFourt.setDatabase_code(DataBaseCode.UTF_8.getCode());
		wrongSysUserDefsFourt.setDbfile_format(FileFormat.PARQUET.getCode());
		wrongSysUserDefsFourt.setIs_header(IsFlag.Shi.getCode());

		extractionDefs.add(wrongSysUserDefsFourt);

		String wrongStringFourt = new HttpClient()
				.addData("extractionDefString", JSON.toJSONString(extractionDefs))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("saveFileConf")).getBodyString();
		ActionResult wrongResultFourt = JsonUtil.toObjectSafety(wrongStringFourt, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultFourt.isSuccess(), is(false));

		extractionDefs.clear();
	}

	@After
	public void after(){
		InitAndDestDataForFileConf.after();
	}

}
