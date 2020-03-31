package hrds.b.biz.agent.dbagentconf.stodestconf;

import com.alibaba.fastjson.JSON;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.b.biz.agent.bean.ColStoParam;
import hrds.b.biz.agent.bean.DataStoRelaParam;
import hrds.b.biz.agent.dbagentconf.BaseInitData;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
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

@DocClass(desc = "定义存储目的地配置测试类", author = "WangZhengcheng")
public class StoDestStepConfActionTest extends WebBaseTestCase{

	private static final long FIRST_DATABASESET_ID = 1001L;
	private static final long SECOND_DATABASESET_ID = 1002L;
	private static final long SYS_USER_TABLE_ID = 7001L;
	private static final long AGENT_INFO_TABLE_ID = 7003L;
	private static final long DATA_SOURCE_TABLE_ID = 7004L;
	private static final long TABLE_INFO_TABLE_ID = 7009L;
	private static final long CODE_INFO_TABLE_ID = 7002L;
	private static final long TABLE_COLUMN_TABLE_ID = 7100L;
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
	 *      8、table_column表测试数据：
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
	 *      11、clean_parameter表测试数据
	 *          11-1、对databaseset_id为1001的数据库直连采集作业设置一个全表的字符替换和字符补齐规则
	 *      12、column_split表测试数据
	 *          12-1、对code_info表的ci_sp_name列设置字段拆分，按照偏移量，拆分为ci_s、p_name两列
	 *          12-2、对code_info表的ci_sp_classname列设置字段拆分，按照下划线拆分ci、sp、classname三列
	 *      13、由于配置了列拆分，需要把拆分后的列加入到table_column表中
	 *      14、column_merge表测试数据
	 *          14-1、对sys_user表中的user_mobile和useris_admin合并成列，名叫user_mobile_admin
	 *      15、由于配置了列合并，需要把合并后的列入到table_column表中
	 *      16、构造卸数文件相关数据(data_extraction_def表)
	 *          16-1、构造采集sys_user表，仅数据抽取，存储方式为非定长，换行符回车，列分隔符|，数据字符集为UTF-8
	 *          16-2、构造采集code_info表，仅数据抽取，存储方式为定长，数据字符集为UTF-8
	 *          16-3、构造采集agent_info表，抽取并入库，存储方式为ORC，数据字符集为UTF-8
	 *          16-4、构造采集data_source表，抽取并入库，非定长，换行符为|，列分隔符为空格，数据字符集为UTF-8
	 *      17、构造orig_syso_info表测试数据，里面是当前系统中所有的码值信息,共有三条数据
	 *      18、构造orig_code_info表测试数据,共有三条数据
	 *      19、构造table_storage_info表测试数据
	 *          19-1、构造数据，模拟采集agent_info表，文件格式为ORC，进数方式为增量，做历史拉链，每天存一份，存储期限为7天
	 *          19-2、构造数据，模拟采集data_source表，文件格式为TEXTFILE，进数方式为追加，不做历史拉链，不每天存一份，存储期限为1天
	 *      20、构造column_storage_info表测试数据
	 *          20-1、Agent_id：是关系型数据库主键，是hbase的rowkey
	 *          20-2、agent_name：不是主键，不是cb预聚合列，不是cb的排序列，不是solr的索引列，不是hive的分区列
	 *          20-3、agent_type：不是主键，不是cb预聚合列，不是cb的排序列，不是solr的索引列，不是hive的分区列
	 *          20-4、source_id：是solr索引列
	 *          20-5、datasource_number：不是主键，不是cb预聚合列，不是cb的排序列，不是solr的索引列，不是hive的分区列
	 *          20-6、datasource_name：不是主键，不是cb预聚合列，不是cb的排序列，不是solr的索引列，不是hive的分区列
	 *      21、构造data_store_layer表测试数据
	 *          21-1、存储配置名称为SOLR，存储类型为SOLR
	 *          21-2、存储配置名称为Oralce，存储类型为Oralce
	 *          21-3、存储配置名称为ElasticSearch，存储类型为ElasticSearch
	 *          21-4、存储配置名称为HBASE，存储类型为HBASE
	 *          21-5、存储配置名称为MONGODB，存储类型为MONGODB
	 *      22、构造data_store_layer_attr表测试数据
	 *          22-1、对关系型数据库配置了database_name，database_pwd，database_drive，user_name，database_ip，database_port，jdbc_url等属性
	 *          22-2、对solr配置了SolarUrl属性
	 *          22-3、对Hbase配置了Hbase-site.xml，Core-site.xml，Hdfs-site.xml三个配置文件的路径
	 *      23、构造data_store_layer_added表测试数据
	 *          23-1、关系型数据库主键
	 *          23-2、solr索引列
	 *          23-3、hbase的rowkey
	 *      24、构造data_relation_table表测试数据
	 *          24-1、agent_info表保存进入关系型数据库
	 *          24-2、agent_info表保存进入进入hbase
	 *          24-3、data_source表保存进入solr
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Before
	public void before(){
		InitAndDestDataForStoDest.before();
		//模拟登陆
		ActionResult actionResult = BaseInitData.simulatedLogin();
		assertThat("模拟登陆", actionResult.isSuccess(), is(true));
	}

	/**
	 * 测试根据数据库设置ID获得定义存储目的地页面初始化信息
	 *
	 * 正确数据访问1：使用正确的colSetId访问，应该可以拿到4条数据，表名分别是agent_info、data_source、sys_user、code_info
	 * 错误的数据访问1：使用错误的colSetId访问，访问会失败
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getInitInfo(){
		//正确数据访问1：使用正确的colSetId访问，应该可以拿到4条数据，表名分别是agent_info、data_source、sys_user、code_info
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getInitInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		Result rightData = rightResult.getDataForResult();
		assertThat("获取到的数据有四条", rightData.getRowCount(), is(4));
		for(int i = 0; i < rightData.getRowCount(); i++){
			if(rightData.getString(i, "table_name").equalsIgnoreCase("agent_info")){
				assertThat("获取到表名为agent_info，表中文名为<Agent信息表>", rightData.getString(i, "table_ch_name").equalsIgnoreCase("Agent信息表"), is(true));
				assertThat("获取到表名为agent_info，做拉链存储", rightData.getString(i, "is_zipper"), is(IsFlag.Shi.getCode()));
				assertThat("获取到表名为agent_info，存储方式为<增量>", rightData.getString(i, "storage_type"), is(StorageType.ZengLiang.getCode()));
				assertThat("获取到表名为agent_info，数据保存时间为<7天>", rightData.getLong(i, "storage_time"), is(7L));
				assertThat("获取到表名为agent_info，数据抽取方式为<抽取并入库>", rightData.getString(i, "data_extract_type"), is(DataExtractType.ShuJuKuChouQuLuoDi.getCode()));
				assertThat("获取到表名为agent_info，destflag标识位为<是>", rightData.getString(i, "destflag"), is(IsFlag.Shi.getCode()));
			}else if(rightData.getString(i, "table_name").equalsIgnoreCase("data_source")){
				assertThat("获取到表名为data_source，表中文名为<数据源表>", rightData.getString(i, "table_ch_name").equalsIgnoreCase("数据源表"), is(true));
				assertThat("获取到表名为data_source，不做拉链存储", rightData.getString(i, "is_zipper"), is(IsFlag.Fou.getCode()));
				assertThat("获取到表名为data_source，存储方式为<追加>", rightData.getString(i, "storage_type"), is(StorageType.ZhuiJia.getCode()));
				assertThat("获取到表名为data_source，数据保存时间为<1天>", rightData.getLong(i, "storage_time"), is(1L));
				assertThat("获取到表名为data_source，数据抽取方式为<抽取并入库>", rightData.getString(i, "data_extract_type"), is(DataExtractType.ShuJuKuChouQuLuoDi.getCode()));
				assertThat("获取到表名为data_source，destflag标识位为<是>", rightData.getString(i, "destflag"), is(IsFlag.Shi.getCode()));
			}else if(rightData.getString(i, "table_name").equalsIgnoreCase("sys_user")){
				assertThat("获取到表名为sys_user，表中文名为<用户表>", rightData.getString(i, "table_ch_name").equalsIgnoreCase("用户表"), is(true));
				assertThat("获取到表名为sys_user，数据抽取方式为<仅抽取>", rightData.getString(i, "data_extract_type"), is(DataExtractType.ShuJuKuChouQuLuoDi.getCode()));
				assertThat("获取到表名为sys_user，destflag标识位为<否>", rightData.getString(i, "destflag"), is(IsFlag.Fou.getCode()));
			}else if(rightData.getString(i, "table_name").equalsIgnoreCase("code_info")){
				assertThat("获取到表名为code_info，表中文名为<代码信息表>", rightData.getString(i, "table_ch_name").equalsIgnoreCase("代码信息表"), is(true));
				assertThat("获取到表名为code_info，数据抽取方式为<仅抽取>", rightData.getString(i, "data_extract_type"), is(DataExtractType.ShuJuKuChouQuLuoDi.getCode()));
				assertThat("获取到表名为code_info，destflag标识位为<否>", rightData.getString(i, "destflag"), is(IsFlag.Fou.getCode()));
			}else{
				assertThat("获取到了不符合期望的结果，表名为：" + rightData.getString(i, "table_name"), false, is(true));
			}
		}

		//错误的数据访问1：使用错误的colSetId访问，访问会失败
		String wrongString = new HttpClient()
				.addData("colSetId", SECOND_DATABASESET_ID)
				.post(getActionUrl("getInitInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试根据数据库设置ID获取当前数据库直连采集任务下所有抽取及入库的表ID
	 *
	 * 正确数据访问1：使用正确的colSetId访问，应该可以拿到2条数据，表ID分别是agent_info、data_source两张表的ID
	 * 错误的数据访问1：使用错误的colSetId访问，访问会失败
	 * 错误的测试用例未达到三组: 以上所有测试用例已经可以满足要求
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getTbStoDestByColSetId(){
		//正确数据访问1：使用正确的colSetId访问，应该可以拿到2条数据，表ID分别是agent_info、data_source两张表的ID
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getTbStoDestByColSetId")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		/*
		List<Map> dataForEntityList = rightResult.getDataForEntityList(Map.class);
		assertThat("获得的数据有两条", dataForEntityList.size(), is(2));
		for(Map tableMap : dataForEntityList){
			for(Object key : tableMap.keySet()){
				if(key == AGENT_INFO_TABLE_ID){
					List list = dataForMap.get(key);
					Result result = new Result(list);
					assertThat("给agent_info表定义的存储目的地有两个", result.getRowCount(), is(2));
					for(int i = 0; i < result.getRowCount(); i++){
						long dslId = result.getLong(i, "dsl_id");
						if(dslId == 4400){
							assertThat("agent_info保存进入关系型数据库", true, is(true));
						}else if(dslId == 4402){
							assertThat("agent_info保存进入HBASE", true, is(true));
						}else {
							assertThat("出现了不符合期望的情况,dslId为 : " + dslId, true, is(false));
						}
					}
				}else if(key == DATA_SOURCE_TABLE_ID){
					List list = dataForMap.get(key);
					Result result = new Result(list);
					assertThat("给data_source表定义的存储目的地有1个", result.getRowCount(), is(1));
					long dslId = result.getLong(0, "dsl_id");
					assertThat("data_source保存进入SOLR", dslId, is(4399L));
				}else{
					assertThat("出现了不符合期望的情况,key为 : " + key, false, is(true));
				}
			}
		}
		*/

		//错误的数据访问1：使用错误的colSetId访问，访问会失败
		String wrongString = new HttpClient()
				.addData("colSetId", UNEXPECTED_ID)
				.post(getActionUrl("getTbStoDestByColSetId")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试根据存储配置主键信息获取存储目的地详细信息
	 *
	 * 正确数据访问1：尝试获取存储目的地为关系型数据库的详细信息
	 * 正确数据访问2：尝试获取存储目的地为solr的详细信息
	 * 正确数据访问3：尝试获取存储目的地为hbase的详细信息
	 * 错误的数据访问1：尝试获取存储目的地为ES的详细信息，因为系统里没有进行配置，所以获取不到数据
	 * 错误的测试用例未达到三组: 以上所有测试用例已经可以满足要求
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getStoDestDetail(){
		//正确数据访问1：尝试获取存储目的地为关系型数据库的详细信息
		String rightString = new HttpClient()
				.addData("dslId", 4400L)
				.post(getActionUrl("getStoDestDetail")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Result rightDataOne = rightResult.getDataForResult();
		assertThat("保存目的地为关系型数据库，获取到的key-value键值对有7对", rightDataOne.getRowCount(),is(7) );
		for(int i = 0; i < rightDataOne.getRowCount(); i++){
			if(rightDataOne.getString(i, "storage_property_key").equalsIgnoreCase("database_name")){
				assertThat("保存目的地是关系型数据库, database_name为<coll_sto_dest_test_dbname>", rightDataOne.getString(i, "storage_property_val").equalsIgnoreCase("coll_sto_dest_test_dbname"), is(true));
			}else if(rightDataOne.getString(i, "storage_property_key").equalsIgnoreCase("database_pwd")){
				assertThat("保存目的地是关系型数据库, database_pwd为<coll_sto_dest_test_pwd>", rightDataOne.getString(i, "storage_property_val").equalsIgnoreCase("coll_sto_dest_test_pwd"), is(true));
			}else if(rightDataOne.getString(i, "storage_property_key").equalsIgnoreCase("database_drive")){
				assertThat("保存目的地是关系型数据库, database_drive为<coll_sto_dest_test_driver>", rightDataOne.getString(i, "storage_property_val").equalsIgnoreCase("coll_sto_dest_test_driver"), is(true));
			}else if(rightDataOne.getString(i, "storage_property_key").equalsIgnoreCase("user_name")){
				assertThat("保存目的地是关系型数据库, user_name为<coll_sto_dest_test_username>", rightDataOne.getString(i, "storage_property_val").equalsIgnoreCase("coll_sto_dest_test_username"), is(true));
			}else if(rightDataOne.getString(i, "storage_property_key").equalsIgnoreCase("database_ip")){
				assertThat("保存目的地是关系型数据库, database_ip为<coll_sto_dest_test_ip>", rightDataOne.getString(i, "storage_property_val").equalsIgnoreCase("coll_sto_dest_test_ip"), is(true));
			}else if(rightDataOne.getString(i, "storage_property_key").equalsIgnoreCase("database_port")){
				assertThat("保存目的地是关系型数据库, database_port为<coll_sto_dest_test_port>", rightDataOne.getString(i, "storage_property_val").equalsIgnoreCase("coll_sto_dest_test_port"), is(true));
			}else if(rightDataOne.getString(i, "storage_property_key").equalsIgnoreCase("jdbc_url")){
				assertThat("保存目的地是关系型数据库, jdbc_url为<coll_sto_dest_test_jdbc_url>", rightDataOne.getString(i, "storage_property_val").equalsIgnoreCase("coll_sto_dest_test_jdbc_url"), is(true));
			}else{
				assertThat("保存目的地是关系型数据库，获取到了不符合期望的key为" + rightDataOne.getString(i, "storage_property_key"), is(true));
			}
		}

		//正确数据访问2：尝试获取存储目的地为solr的详细信息
		String rightStringTwo = new HttpClient()
				.addData("dslId", 4399L)
				.post(getActionUrl("getStoDestDetail")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		Result rightDataTwo = rightResultTwo.getDataForResult();
		assertThat("获取存储目的地为solr的详细信息有1条", rightDataTwo.getRowCount(), is(1));
		assertThat("获取存储目的地为solr的详细信息，key为<SolarUrl>", rightDataTwo.getString(0, "storage_property_key"), is("SolarUrl"));
		assertThat("获取存储目的地为solr的详细信息，value为<https://SolarUrl>", rightDataTwo.getString(0, "storage_property_val"), is("https://SolarUrl"));

		//正确数据访问3：尝试获取存储目的地为hbase的详细信息
		String rightStringThree = new HttpClient()
				.addData("dslId", 4402L)
				.post(getActionUrl("getStoDestDetail")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));

		Result rightDataThree = rightResultThree.getDataForResult();
		assertThat("获取存储目的地为hbase的详细信息,获取到了3条", rightDataThree.getRowCount(), is(3));
		for(int i = 0; i < rightDataThree.getRowCount(); i++){
			if(rightDataThree.getString(i, "storage_property_key").equalsIgnoreCase("Hbase-site-path")){
				assertThat("获取存储目的地为hbase的详细信息，key为<Hbase-site-path>, value为<Hbase-site.xml>", rightDataThree.getString(i, "storage_property_val").equalsIgnoreCase("Hbase-site.xml"), is(true));
			}else if(rightDataThree.getString(i, "storage_property_key").equalsIgnoreCase("Core-site-path")){
				assertThat("获取存储目的地为hbase的详细信息，key为<Core-site-path>, value为<Core-site.xml>", rightDataThree.getString(i, "storage_property_val").equalsIgnoreCase("Core-site.xml"), is(true));
			}else if(rightDataThree.getString(i, "storage_property_key").equalsIgnoreCase("Hdfs-site-path")){
				assertThat("获取存储目的地为hbase的详细信息，key为<Hdfs-site-path>, value为<Hdfs-site.xml>", rightDataThree.getString(i, "storage_property_val").equalsIgnoreCase("Hdfs-site.xml"), is(true));
			}else{
				assertThat("获取存储目的地为hbase的详细信息出现了不符合期望的情况，key: " + rightDataThree.getString(i, "storage_property_key"), true, is(false));
			}
		}

		//错误的数据访问1：尝试获取存储目的地为ES的详细信息，因为系统里没有进行配置，所以获取不到数据
		String wrongStringOne = new HttpClient()
				.addData("dslId", 4401L)
				.post(getActionUrl("getStoDestDetail")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(true));
		Result wrongData = wrongResultOne.getDataForResult();
		assertThat("尝试获取存储目的地为ES的详细信息，因为系统里没有进行配置，所以获取不到数据", wrongData.isEmpty(), is(true));
	}

	/**
	 * 测试对仅做数据抽取的表回显定义好的存储目的地
	 *
	 * 正确数据访问1：对sys_user表的采集仅做抽取，存储目的地为/root目录，尝试获取
	 * 错误的数据访问1：传入错误的表ID，访问失败
	 * 错误的测试用例未达到三组: getStoDestForOnlyExtract只有一个参数，以上所有测试用例已经可以满足要求
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getStoDestForOnlyExtract(){
		//正确数据访问1：对sys_user表的采集仅做抽取，存储目的地为/root目录
		String rightString = new HttpClient()
				.addData("tableId", SYS_USER_TABLE_ID)
				.post(getActionUrl("getStoDestForOnlyExtract")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		Result rightData = rightResult.getDataForResult();
		assertThat("访问方法获取到的数据有一条", rightData.getRowCount(), is(1));
		assertThat("sys_user表仅做数据抽取，存储目的地为/root目录", rightData.getString(0, "plane_url"), is("/root"));

		//错误的数据访问1：传入错误的表ID，访问失败
		String wrongString = new HttpClient()
				.addData("tableId", UNEXPECTED_ID)
				.post(getActionUrl("getStoDestForOnlyExtract")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试对仅做数据抽取的表保存定义好的存储目的地
	 *
	 * 正确数据访问1：模拟修改对sys_user表的存储目的地，原有的存储目的地为/root，现在把存储目的地改为/root/test
	 * 错误的数据访问1：传入错误的表ID，访问失败
	 * 错误的测试用例未达到三组: 以上所有测试用例已经可以满足要求
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveStoDestForOnlyExtract(){
		//正确数据访问1：模拟修改对sys_user表的存储目的地，原有的存储目的地为/root，现在把存储目的地改为/root/test
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//在进行修改之前，查询数据库，确认修改前数据库的情况符合预期
			Result result = SqlOperator.queryResult(db, " select plane_url from " + Data_extraction_def.TableName + " where table_id = ? and data_extract_type = ?"
					, SYS_USER_TABLE_ID, DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			assertThat("查询到的数据有一条", result.getRowCount(), is(1));
			assertThat("得到的sys_user表的存储目的地为/root", result.getString(0 ,"plane_url"), is("/root"));
		}
		String rightString = new HttpClient()
				.addData("tableId", SYS_USER_TABLE_ID)
				.addData("stoDest", "/root/test")
				.post(getActionUrl("saveStoDestForOnlyExtract")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));
		//判断修改后数据库中的结果是否符合期望
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//在进行修改之后，查询数据库，确认修改前数据库的情况符合预期
			Result result = SqlOperator.queryResult(db, " select plane_url from " + Data_extraction_def.TableName + " where table_id = ? and data_extract_type = ?"
					, SYS_USER_TABLE_ID, DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			assertThat("查询到的数据有一条", result.getRowCount(), is(1));
			assertThat("得到的sys_user表的存储目的地为/root/test", result.getString(0 ,"plane_url"), is("/root/test"));
		}

		//错误的数据访问1：传入错误的表ID，访问失败
		String wrongString = new HttpClient()
				.addData("tableId", UNEXPECTED_ID)
				.addData("stoDest", "/root/test")
				.post(getActionUrl("saveStoDestForOnlyExtract")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试根据tableId获取该表选择的存储目的地和系统中配置的所有存储目的地
	 *
	 * 正确数据访问1：data_source表保存进入solr，所以使用data_source表的table_id访问方法，判断在得到的solr记录上的usedFlag是否为1,而其他的记录上usedflag为0
	 * 正确数据访问2：agent_info表保存进入hbase和关系型数据库，所以使用agent_info表的table_id访问方法，判断在得到的hbase和关系型数据库记录上的usedflag是否为1,而其他的记录上usedflag为0
	 * 错误的测试用例未达到三组: 以上所有测试用例已经可以满足要求
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getStoDestByTableId(){
		//正确数据访问1：data_source表保存进入solr，所以使用data_source表的table_id访问方法，判断在得到的solr记录上的usedflag是否为"1",而其他的记录上usedflag为"0"
		String rightString = new HttpClient()
				.addData("tableId", DATA_SOURCE_TABLE_ID)
				.post(getActionUrl("getStoDestByTableId")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		Result rightDataOne = rightResult.getDataForResult();
		assertThat("由于在构造初始化数据的时候配置了5个存储目的地，所以获取到的数据有5条", rightDataOne.getRowCount(), is(5));
		for(int i = 0; i < rightDataOne.getRowCount(); i++){
			if(rightDataOne.getString(i, "dsl_name").equalsIgnoreCase("SOLR")){
				assertThat("<data_source>数据存储层配置属性名称为<SOLR>, 存储类型为<SOLR>", rightDataOne.getString(i, "store_type"), is(Store_type.SOLR.getCode()));
				assertThat("<data_source>数据存储层配置属性名称为<SOLR>, usedflag标识位为<1>", rightDataOne.getString(i, "usedflag"), is(IsFlag.Shi.getCode()));
			}else if(rightDataOne.getString(i, "dsl_name").equalsIgnoreCase("Oralce")){
				assertThat("<data_source>数据存储层配置属性名称为<Oralce>, 存储类型为<关系型数据库>", rightDataOne.getString(i, "store_type"), is(Store_type.DATABASE.getCode()));
				assertThat("<data_source>数据存储层配置属性名称为<Oralce>, usedflag标识位为<0>", rightDataOne.getString(i, "usedflag"), is(IsFlag.Fou.getCode()));
			}else if(rightDataOne.getString(i, "dsl_name").equalsIgnoreCase("ElasticSearch")){
				assertThat("<data_source>数据存储层配置属性名称为<ElasticSearch>, 存储类型为<ElasticSearch>", rightDataOne.getString(i, "store_type"), is(Store_type.ElasticSearch.getCode()));
				assertThat("<data_source>数据存储层配置属性名称为<ElasticSearch>, usedflag标识位为<0>", rightDataOne.getString(i, "usedflag"), is(IsFlag.Fou.getCode()));
			}else if(rightDataOne.getString(i, "dsl_name").equalsIgnoreCase("HBASE")){
				assertThat("<data_source>数据存储层配置属性名称为<HBASE>, 存储类型为<HBASE>", rightDataOne.getString(i, "store_type"), is(Store_type.HBASE.getCode()));
				assertThat("<data_source>数据存储层配置属性名称为<HBASE>, usedflag标识位为<0>", rightDataOne.getString(i, "usedflag"), is(IsFlag.Fou.getCode()));
			}else if(rightDataOne.getString(i, "dsl_name").equalsIgnoreCase("MONGODB")){
				assertThat("<data_source>数据存储层配置属性名称为<MONGODB>, 存储类型为<MONGODB>", rightDataOne.getString(i, "store_type"), is(Store_type.MONGODB.getCode()));
				assertThat("<data_source>数据存储层配置属性名称为<MONGODB>, usedflag标识位为<0>", rightDataOne.getString(i, "usedflag"), is(IsFlag.Fou.getCode()));
			}else{
				assertThat("<data_source>获取数据存储层配置出现了不符合期望的情况，数据存储层配置属性名称为：" + rightDataOne.getString(i, "dsl_name"), true, is(false));
			}
		}

		//正确数据访问2：agent_info表保存进入hbase和关系型数据库，所以使用agent_info表的table_id访问方法，判断在得到的hbase和关系型数据库记录上的usedflag是否为true,而其他的记录上usedflag为false
		String rightStringTwo = new HttpClient()
				.addData("tableId", AGENT_INFO_TABLE_ID)
				.post(getActionUrl("getStoDestByTableId")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		Result rightDataTwo = rightResultTwo.getDataForResult();
		assertThat("由于在构造初始化数据的时候配置了5个存储目的地，所以获取到的数据有5条", rightDataTwo.getRowCount(), is(5));
		for(int i = 0; i < rightDataTwo.getRowCount(); i++){
			if(rightDataTwo.getString(i, "dsl_name").equalsIgnoreCase("SOLR")){
				assertThat("<agent_info>数据存储层配置属性名称为<SOLR>, 存储类型为<SOLR>", rightDataTwo.getString(i, "store_type"), is(Store_type.SOLR.getCode()));
				assertThat("<agent_info>数据存储层配置属性名称为<SOLR>, usedflag标识位为<0>", rightDataTwo.getString(i, "usedflag"), is(IsFlag.Fou.getCode()));
			}else if(rightDataTwo.getString(i, "dsl_name").equalsIgnoreCase("Oralce")){
				assertThat("<agent_info>数据存储层配置属性名称为<Oralce>, 存储类型为<关系型数据库>", rightDataTwo.getString(i, "store_type"), is(Store_type.DATABASE.getCode()));
				assertThat("<agent_info>数据存储层配置属性名称为<Oralce>, usedflag标识位为<1>", rightDataTwo.getString(i, "usedflag"), is(IsFlag.Shi.getCode()));
			}else if(rightDataTwo.getString(i, "dsl_name").equalsIgnoreCase("ElasticSearch")){
				assertThat("<agent_info>数据存储层配置属性名称为<ElasticSearch>, 存储类型为<ElasticSearch>", rightDataTwo.getString(i, "store_type"), is(Store_type.ElasticSearch.getCode()));
				assertThat("<agent_info>数据存储层配置属性名称为<ElasticSearch>, usedflag标识位为<0>", rightDataTwo.getString(i, "usedflag"), is(IsFlag.Fou.getCode()));
			}else if(rightDataTwo.getString(i, "dsl_name").equalsIgnoreCase("HBASE")){
				assertThat("<agent_info>数据存储层配置属性名称为<HBASE>, 存储类型为<HBASE>", rightDataTwo.getString(i, "store_type"), is(Store_type.HBASE.getCode()));
				assertThat("<agent_info>数据存储层配置属性名称为<HBASE>, usedflag标识位为<1>", rightDataTwo.getString(i, "usedflag"), is(IsFlag.Shi.getCode()));
			}else if(rightDataTwo.getString(i, "dsl_name").equalsIgnoreCase("MONGODB")){
				assertThat("<agent_info>数据存储层配置属性名称为<MONGODB>, 存储类型为<MONGODB>", rightDataTwo.getString(i, "store_type"), is(Store_type.MONGODB.getCode()));
				assertThat("<agent_info>数据存储层配置属性名称为<MONGODB>, usedflag标识位为<0>", rightDataTwo.getString(i, "usedflag"), is(IsFlag.Fou.getCode()));
			}else{
				assertThat("<agent_info>获取数据存储层配置出现了不符合期望的情况，数据存储层配置属性名称为：" + rightDataTwo.getString(i, "dsl_name"), true, is(false));
			}
		}
	}

	/**
	 * 测试根据表ID获取该表所有的列信息
	 *
	 * 正确数据访问1：agent_info表保存进入关系型数据库，所以构造获取agent_info表的三个采集列的选择列信息情况
	 * 正确数据访问2：agent_info表保存进入HBASE，所以构造获取agent_info表的三个采集列的选择列信息情况
	 * 正确数据访问3：data_source表保存进入SOLR，所以构造获取data_source表的三个采集列的选择列信息情况
	 * 正确数据访问4：agent_info没有进入SOLR，所以构造获取当用户将agent_info表保存进入SOLR时的结果集情况
	 * 错误的测试用例未达到三组: 该方法永远不会因为参数而抛出异常导致访问失败，只会根据实际情况返回不同样式的结果集
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getColumnStoInfo(){
		//正确数据访问1：agent_info表保存进入关系型数据库，所以构造获取agent_info表的三个采集列的选择列信息情况
		String rightStringOne = new HttpClient()
				.addData("tableId", AGENT_INFO_TABLE_ID)
				.addData("dslId", 4400L)
				.post(getActionUrl("getColumnStoInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Result rightDataOne = rightResultOne.getDataForResult();
		assertThat("该方法测试用例1获取到了3条数据", rightDataOne.getRowCount(), is(3));
		for(int i = 0; i < rightDataOne.getRowCount(); i++){
			if(rightDataOne.getString(i, "column_name").equalsIgnoreCase("agent_id")){
				assertThat("获取到的列中文名为agent_id", rightDataOne.getString(i, "column_ch_name"), is("agent_id"));
				assertThat("agent_id是主键", rightDataOne.getString(i, "主键"), is(IsFlag.Shi.getCode()));
			}else if(rightDataOne.getString(i, "column_name").equalsIgnoreCase("agent_name")){
				assertThat("获取到的列中文名为Agent名称", rightDataOne.getString(i, "column_ch_name"), is("Agent名称"));
				assertThat("agent_name不是主键", rightDataOne.getString(i, "主键"), is(IsFlag.Fou.getCode()));
			}else if(rightDataOne.getString(i, "column_name").equalsIgnoreCase("agent_type")){
				assertThat("获取到的列中文名为agent类别", rightDataOne.getString(i, "column_ch_name"), is("agent类别"));
				assertThat("agent_type不是主键", rightDataOne.getString(i, "主键"), is(IsFlag.Fou.getCode()));
			}else {
				assertThat("获取到了不符合期望的数据，列名为" + rightDataOne.getString(i, "column_name"), false, is(true));
			}
		}

		//正确数据访问2：agent_info表保存进入HBASE，所以构造获取agent_info表的三个采集列的选择列信息情况
		String rightStringTwo = new HttpClient()
				.addData("tableId", AGENT_INFO_TABLE_ID)
				.addData("dslId", 4402L)
				.post(getActionUrl("getColumnStoInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		Result rightDataTwo = rightResultTwo.getDataForResult();
		assertThat("该方法测试用例2获取到了3条数据", rightDataTwo.getRowCount(), is(3));
		for(int i = 0; i < rightDataTwo.getRowCount(); i++){
			if(rightDataTwo.getString(i, "column_name").equalsIgnoreCase("agent_id")){
				assertThat("获取到的列中文名为agent_id", rightDataTwo.getString(i, "column_ch_name"), is("agent_id"));
				assertThat("agent_id是rowkey", rightDataTwo.getString(i, "rowkey"), is(IsFlag.Shi.getCode()));
			}else if(rightDataTwo.getString(i, "column_name").equalsIgnoreCase("agent_name")){
				assertThat("获取到的列中文名为Agent名称", rightDataTwo.getString(i, "column_ch_name"), is("Agent名称"));
				assertThat("agent_name不是rowkey", rightDataTwo.getString(i, "rowkey"), is(IsFlag.Fou.getCode()));
			}else if(rightDataTwo.getString(i, "column_name").equalsIgnoreCase("agent_type")){
				assertThat("获取到的列中文名为agent类别", rightDataTwo.getString(i, "column_ch_name"), is("agent类别"));
				assertThat("agent_type不是rowkey", rightDataTwo.getString(i, "rowkey"), is(IsFlag.Fou.getCode()));
			}else {
				assertThat("获取到了不符合期望的数据，列名为" + rightDataTwo.getString(i, "column_name"), false, is(true));
			}
		}

		//正确数据访问3：data_source表保存进入SOLR，所以构造获取data_source表的三个采集列的选择列信息情况
		String rightStringThree = new HttpClient()
				.addData("tableId", DATA_SOURCE_TABLE_ID)
				.addData("dslId", 4399L)
				.post(getActionUrl("getColumnStoInfo")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));

		Result rightDataThree = rightResultThree.getDataForResult();
		assertThat("该方法测试用例3获取到了3条数据", rightDataThree.getRowCount(), is(3));
		for(int i = 0; i < rightDataThree.getRowCount(); i++){
			if(rightDataThree.getString(i, "column_name").equalsIgnoreCase("source_id")){
				assertThat("获取到的列中文名为数据源ID", rightDataThree.getString(i, "column_ch_name"), is("数据源ID"));
				assertThat("source_id是索引列", rightDataThree.getString(i, "索引列"), is(IsFlag.Shi.getCode()));
			}else if(rightDataThree.getString(i, "column_name").equalsIgnoreCase("datasource_number")){
				assertThat("获取到的列中文名为数据源编号", rightDataThree.getString(i, "column_ch_name"), is("数据源编号"));
				assertThat("datasource_number不是索引列", rightDataThree.getString(i, "索引列"), is(IsFlag.Fou.getCode()));
			}else if(rightDataThree.getString(i, "column_name").equalsIgnoreCase("datasource_name")){
				assertThat("获取到的列中文名为数据源名称", rightDataThree.getString(i, "column_ch_name"), is("数据源名称"));
				assertThat("datasource_name不是索引列", rightDataThree.getString(i, "索引列"), is(IsFlag.Fou.getCode()));
			}else {
				assertThat("获取到了不符合期望的数据，列名为" + rightDataThree.getString(i, "column_name"), false, is(true));
			}
		}

		//正确数据访问4：agent_info没有进入SOLR，所以构造获取当用户将agent_info表保存进入SOLR时的结果集情况
		String rightStringFour = new HttpClient()
				.addData("tableId", AGENT_INFO_TABLE_ID)
				.addData("dslId", 4399L)
				.post(getActionUrl("getColumnStoInfo")).getBodyString();
		ActionResult rightResultFour = JsonUtil.toObjectSafety(rightStringFour, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultFour.isSuccess(), is(true));

		Result rightDataFour = rightResultFour.getDataForResult();
		assertThat("该方法测试用例4获取到了3条数据", rightDataFour.getRowCount(), is(3));

		for(int i = 0; i < rightDataFour.getRowCount(); i++){
			if(rightDataFour.getString(i, "column_name").equalsIgnoreCase("agent_id")){
				assertThat("获取到的列中文名为agent_id", rightDataFour.getString(i, "column_ch_name"), is("agent_id"));
				assertThat("agent_id不是索引列", rightDataFour.getString(i, "索引列"), is(IsFlag.Fou.getCode()));
			}else if(rightDataFour.getString(i, "column_name").equalsIgnoreCase("agent_name")){
				assertThat("获取到的列中文名为Agent名称", rightDataFour.getString(i, "column_ch_name"), is("Agent名称"));
				assertThat("agent_name不是索引列", rightDataFour.getString(i, "索引列"), is(IsFlag.Fou.getCode()));
			}else if(rightDataFour.getString(i, "column_name").equalsIgnoreCase("agent_type")){
				assertThat("获取到的列中文名为agent类别", rightDataFour.getString(i, "column_ch_name"), is("agent类别"));
				assertThat("agent_type不是索引列", rightDataFour.getString(i, "索引列"), is(IsFlag.Fou.getCode()));
			}else {
				assertThat("获取到了不符合期望的数据，列名为" + rightDataFour.getString(i, "column_name"), false, is(true));
			}
		}
	}

	/**
	 * 测试保存表的字段存储信息
	 *
	 * 正确数据访问1：agent_info保存进入关系型数据库，现在不用agent_id做主键了，改用agent_name做主键
	 * 正确数据访问2：agent_info原来还要保存进入Hbase，现在不进入Hbase了
	 * 正确数据访问3：data_source表source_id保存进入关系型数据库做主键，保存进入hbase做rowkey，不再进solr做索引列
	 * 错误的数据访问1：保存字段特殊属性的数组为空
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveColStoInfo(){
		List<ColStoParam> columnStorageInfos = new ArrayList<>();
		//正确数据访问1：agent_info保存进入关系型数据库，现在不用agent_id做主键了，改用agent_name做主键
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//在保存前，先检查数据库中的数据是否符合期望
			long countOne = SqlOperator.queryNumber(db, "select count(1) from " + Column_storage_info.TableName + " csi" +
					" left join " + Data_store_layer_added.TableName + " dsld" +
					" on dsld.dslad_id = csi.dslad_id" +
					" left join " + Data_store_layer.TableName + " dsl" +
					" on dsl.dsl_id = dsld.dsl_id where csi.column_id = ? and dsl.store_type = ?", 3112L, Store_type.DATABASE.getCode())
					.orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("agent_info表中的agent_id字段保存进入到关系型数据库做主键", countOne, is(1L));

			long countTwo = SqlOperator.queryNumber(db, "select count(1) from " + Column_storage_info.TableName + " csi" +
					" left join " + Data_store_layer_added.TableName + " dsld" +
					" on dsld.dslad_id = csi.dslad_id" +
					" left join " + Data_store_layer.TableName + " dsl" +
					" on dsl.dsl_id = dsld.dsl_id where csi.column_id = ? and dsl.store_type = ?", 3113L, Store_type.DATABASE.getCode())
					.orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("agent_info表中的agent_name字段没有保存进入到关系型数据库做主键", countTwo, is(0L));
		}

		ColStoParam primayKeyParam = new ColStoParam();
		primayKeyParam.setColumnId(3113L);
		long[] dsladIds = {439999L};
		primayKeyParam.setDsladIds(dsladIds);

		//这样做的目的是继续保证agent_id列在hbase中做rowkey,维持构造的测试数据的原有的状态，保证能够顺利执行saveColStoInfo<正确的数据访问2>测试用例
		ColStoParam rowKeyParam = new ColStoParam();
		rowKeyParam.setColumnId(3112L);
		long[] dsladIdsTwo = {440001L};
		rowKeyParam.setDsladIds(dsladIdsTwo);
		rowKeyParam.setCsiNumber(1L);

		columnStorageInfos.add(primayKeyParam);
		columnStorageInfos.add(rowKeyParam);

		String rightStringOne = new HttpClient()
				.addData("colStoInfoString", JSON.toJSONString(columnStorageInfos))
				.addData("tableId", AGENT_INFO_TABLE_ID)
				.post(getActionUrl("saveColStoInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//在保存后，检查数据库中的数据是否符合期望
			long countOne = SqlOperator.queryNumber(db, "select count(1) from " + Column_storage_info.TableName + " csi" +
					" left join " + Data_store_layer_added.TableName + " dsld" +
					" on dsld.dslad_id = csi.dslad_id" +
					" left join " + Data_store_layer.TableName + " dsl" +
					" on dsl.dsl_id = dsld.dsl_id where csi.column_id = ? and dsl.store_type = ?", 3112L, Store_type.DATABASE.getCode())
					.orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("agent_info表中的agent_id字段没有保存进入到关系型数据库做主键", countOne, is(0L));

			long countTwo = SqlOperator.queryNumber(db, "select count(1) from " + Column_storage_info.TableName + " csi" +
					" left join " + Data_store_layer_added.TableName + " dsld" +
					" on dsld.dslad_id = csi.dslad_id" +
					" left join " + Data_store_layer.TableName + " dsl" +
					" on dsl.dsl_id = dsld.dsl_id where csi.column_id = ? and dsl.store_type = ?", 3113L, Store_type.DATABASE.getCode())
					.orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("agent_info表中的agent_name字段保存进入到关系型数据库做主键", countTwo, is(1L));

			Result result = SqlOperator.queryResult(db, "select csi.csi_number from " + Column_storage_info.TableName + " csi" +
					" left join " + Data_store_layer_added.TableName + " dsld" +
					" on dsld.dslad_id = csi.dslad_id" +
					" left join " + Data_store_layer.TableName + " dsl" +
					" on dsl.dsl_id = dsld.dsl_id where csi.column_id = ? and dsl.store_type = ?", 3112L, Store_type.HBASE.getCode());
			assertThat("agent_info表中的agent_id字段保存进入到HBASE做主键rowkey", result.getRowCount(), is(1));
			assertThat("agent_info表中的agent_id字段保存进入到HBASE做主键rowkey，序号为1", result.getLong(0, "csi_number"), is(1L));

			//删除因执行测试用例而新增的数据
			int count = SqlOperator.execute(db, "delete from " + Column_storage_info.TableName + " where column_id = ?", 3113L);
			assertThat("删除因执行saveColStoInfo<正确的数据访问2>测试用例而新增的数据", count, is(1));

			SqlOperator.commitTransaction(db);
		}

		columnStorageInfos.clear();

		//正确数据访问2：agent_info原来还要保存进入Hbase，现在不进入Hbase了
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//在保存前，先检查数据库中的数据是否符合期望
			long countOne = SqlOperator.queryNumber(db, "select count(1) from " + Column_storage_info.TableName + " csi" +
					" left join " + Data_store_layer_added.TableName + " dsld" +
					" on dsld.dslad_id = csi.dslad_id" +
					" left join " + Data_store_layer.TableName + " dsl" +
					" on dsl.dsl_id = dsld.dsl_id where csi.column_id = ? and dsl.store_type = ?", 3112L, Store_type.HBASE.getCode())
					.orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("agent_info表中的agent_id字段保存进入到HBASE做rowkey", countOne, is(1L));
		}

		String rightStringTwo = new HttpClient()
				.addData("colStoInfoString", JSON.toJSONString(columnStorageInfos))
				.addData("tableId", AGENT_INFO_TABLE_ID)
				.post(getActionUrl("saveColStoInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//在保存后，检查数据库中的数据是否符合期望
			long countOne = SqlOperator.queryNumber(db, "select count(1) from " + Column_storage_info.TableName + " csi" +
					" left join " + Data_store_layer_added.TableName + " dsld" +
					" on dsld.dslad_id = csi.dslad_id" +
					" left join " + Data_store_layer.TableName + " dsl" +
					" on dsl.dsl_id = dsld.dsl_id where csi.column_id = ? and dsl.store_type = ?", 3112L, Store_type.HBASE.getCode())
					.orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("agent_info表中的agent_id字段没有保存进入到HBASE做rowkey", countOne, is(0L));
		}

		columnStorageInfos.clear();

		//正确数据访问3：data_source表source_id保存进入关系型数据库做主键，保存进入hbase做rowkey，不再进solr做索引列
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//在保存前，先检查数据库中的数据是否符合期望
			long countOne = SqlOperator.queryNumber(db, "select count(1) from " + Column_storage_info.TableName + " csi" +
					" left join " + Data_store_layer_added.TableName + " dsld" +
					" on dsld.dslad_id = csi.dslad_id" +
					" left join " + Data_store_layer.TableName + " dsl" +
					" on dsl.dsl_id = dsld.dsl_id where csi.column_id = ? and dsl.store_type = ?", 5112L, Store_type.SOLR.getCode())
					.orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("data_source表中的source_id字段保存进入到solr做索引列", countOne, is(1L));
		}
		ColStoParam sourceIdParam = new ColStoParam();
		sourceIdParam.setColumnId(5112L);
		long[] dsladIdsThree = {439999L, 440001L};
		sourceIdParam.setDsladIds(dsladIdsThree);
		sourceIdParam.setCsiNumber(3L);

		columnStorageInfos.add(sourceIdParam);

		String rightStringThree = new HttpClient()
				.addData("colStoInfoString", JSON.toJSONString(columnStorageInfos))
				.addData("tableId", DATA_SOURCE_TABLE_ID)
				.post(getActionUrl("saveColStoInfo")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//在保存前，先检查数据库中的数据是否符合期望
			long countOne = SqlOperator.queryNumber(db, "select count(1) from " + Column_storage_info.TableName + " csi" +
					" left join " + Data_store_layer_added.TableName + " dsld" +
					" on dsld.dslad_id = csi.dslad_id" +
					" left join " + Data_store_layer.TableName + " dsl" +
					" on dsl.dsl_id = dsld.dsl_id where csi.column_id = ? and dsl.store_type = ?", 5112L, Store_type.SOLR.getCode())
					.orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<正确的数据访问3>执行成功后，data_source表中的source_id字段不再进入到solr做索引列", countOne, is(0L));

			long countTwo = SqlOperator.queryNumber(db, "select count(1) from " + Column_storage_info.TableName + " csi" +
					" left join " + Data_store_layer_added.TableName + " dsld" +
					" on dsld.dslad_id = csi.dslad_id" +
					" left join " + Data_store_layer.TableName + " dsl" +
					" on dsl.dsl_id = dsld.dsl_id where csi.column_id = ? and dsl.store_type = ?", 5112L, Store_type.DATABASE.getCode())
					.orElseThrow(() -> new BusinessException("SQL查询错误"));
			assertThat("<正确的数据访问3>执行成功后，data_source表中的source_id字段保存进入到关系型数据库做主键", countTwo, is(1L));

			Result result = SqlOperator.queryResult(db, "select csi.csi_number from " + Column_storage_info.TableName + " csi" +
					" left join " + Data_store_layer_added.TableName + " dsld" +
					" on dsld.dslad_id = csi.dslad_id" +
					" left join " + Data_store_layer.TableName + " dsl" +
					" on dsl.dsl_id = dsld.dsl_id where csi.column_id = ? and dsl.store_type = ?", 5112L, Store_type.HBASE.getCode());
			assertThat("<正确的数据访问3>执行成功后，data_source表中的source_id字段保存进入到Hbase做rowkey", result.getRowCount(), is(1));
			assertThat("<正确的数据访问3>执行成功后，data_source表中的source_id字段保存进入到Hbase做rowkey，序号为3", result.getLong(0, "csi_number"), is(3L));

			//执行成功后，删除新增的数据
			int count = SqlOperator.execute(db, "delete from " + Column_storage_info.TableName + " where column_id = ?", 5112L);
			assertThat("删除因执行saveColStoInfo<正确的数据访问3>测试用例而新增的数据", count, is(2));

			SqlOperator.commitTransaction(db);
		}

		columnStorageInfos.clear();

		//错误的数据访问1：保存字段特殊属性的数组为空
		ColStoParam wrongParam = new ColStoParam();
		wrongParam.setColumnId(5112L);
		wrongParam.setCsiNumber(3L);

		columnStorageInfos.add(wrongParam);

		String wrongString = new HttpClient()
				.addData("colStoInfoString", JSON.toJSONString(columnStorageInfos))
				.addData("tableId", DATA_SOURCE_TABLE_ID)
				.post(getActionUrl("saveColStoInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));
	}

	/**
	 * 测试在配置字段存储信息时，更新字段中文名
	 *
	 * 正确数据访问1：更新data_source表在table_column表中的数据，并断言是否修改成功
	 * 错误的数据访问1：更新agent_info表在table_column表中的数据，但是传错一个column_id，使其故意更新不成功
	 * 错误的数据访问2：更新agent_info表在table_column表中的数据，但是故意不关联某个字段的ID
	 * 错误的场景未满三种：updateColumnZhName方法只有一个参数
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void updateColumnZhName(){
		//正确数据访问1：更新data_source表在table_column表中的数据，并断言是否修改成功
		List<Table_column> dataSources = new ArrayList<>();
		for(int i = 0; i < 3; i++){
			long columnId;
			String columnZhName;
			switch (i){
				case 0 :
					columnId = 5112L;
					columnZhName = "数据源ID_update";
					break;
				case 1 :
					columnId = 5113L;
					columnZhName = "数据源编号_update";
					break;
				case 2 :
					columnId = 5114L;
					columnZhName = "数据源名称_update";
					break;
				default:
					columnId = UNEXPECTED_ID;
					columnZhName = "unexpected_columnZhName";
			}
			Table_column tableColumn = new Table_column();
			tableColumn.setColumn_id(columnId);
			tableColumn.setColumn_ch_name(columnZhName);

			dataSources.add(tableColumn);
		}

		String rightString = new HttpClient()
				.addData("columnString", JSON.toJSONString(dataSources))
				.post(getActionUrl("updateColumnZhName")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Object> list = SqlOperator.queryOneColumnList(db, "select column_ch_name from " + Table_column.TableName + " where table_id = ?", DATA_SOURCE_TABLE_ID);
			assertThat("查询到的数据有3条", list.size(), is(3));
			for(Object obj : list){
				String str = (String) obj;
				if(str.equalsIgnoreCase("数据源ID_update")){
					assertThat(true, is(true));
				}else if(str.equalsIgnoreCase("数据源编号_update")){
					assertThat(true, is(true));
				}else if(str.equalsIgnoreCase("数据源名称_update")){
					assertThat(true, is(true));
				}else{
					assertThat("出现了不符合期望的情况，列中文名为:" + str, false, is(true));
				}
			}
		}

		//错误的数据访问1：更新agent_info表在table_column表中的数据，但是传错一个column_id，使其故意更新不成功
		List<Table_column> agentInfos = new ArrayList<>();
		for(int i = 0; i < 3; i++){
			long columnId;
			String columnZhName;
			switch (i){
				case 0 :
					columnId = 3112L;
					columnZhName = "agent_id_update";
					break;
				case 1 :
					columnId = 3113L;
					columnZhName = "Agent名称_update";
					break;
				case 2 :
					//故意构造错误一条数据的column_id，让校验程序报错
					columnId = UNEXPECTED_ID;
					columnZhName = "agent类别_update";
					break;
				default:
					columnId = UNEXPECTED_ID;
					columnZhName = "unexpected_columnZhName";
			}
			Table_column tableColumn = new Table_column();
			tableColumn.setColumn_id(columnId);
			tableColumn.setColumn_ch_name(columnZhName);

			agentInfos.add(tableColumn);
		}

		String wrongString = new HttpClient()
				.addData("columnString", JSON.toJSONString(agentInfos))
				.post(getActionUrl("updateColumnZhName")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));

		agentInfos.clear();

		//错误的数据访问2：更新agent_info表在table_column表中的数据，但是故意不关联某个字段的ID
		Table_column wrongTableColumn = new Table_column();
		wrongTableColumn.setColumn_ch_name("Agent名称_update");
		agentInfos.add(wrongTableColumn);

		String wrongStringTwo = new HttpClient()
				.addData("columnString", JSON.toJSONString(agentInfos))
				.post(getActionUrl("updateColumnZhName")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));
	}

	/**
	 * 测试保存表存储属性配置
	 *
	 * 正确数据访问1：修改agent_info表的保存目的地，由关系型数据库和hbase修改为仅保存到关系型数据库,进数方式为替换，不进行拉链存储，只保存一天
	 * 正确数据访问2：修改data_source表的存储目的地，由SOLR变为进入MongoDB和关系型数据库
	 * 正确数据访问3：新增采集table_info表的存储目的地，目的地为关系型数据库和SOLR(注意构造数据抽取定义表中table_info表的相关数据，测试通过后，将新增进入的相关数据全部删除)
	 * 正确数据访问4：新增采集table_column表的存储目的地，目的地为关系型数据库和SOLR，不做拉链存储(注意构造数据抽取定义表中table_column表的相关数据，测试通过后，将新增进入的相关数据全部删除)
	 * 错误的数据访问1：新增采集table_column表的存储目的地，但是没有传入任何一个目的地
	 * 错误的数据访问2：新增采集table_column表的存储目的地，但是在数据抽取定义表中没有构造table_column表的抽取信息
	 * 错误的数据访问3：新增采集table_column表的存储目的地，但是未关联表
	 * 错误的数据访问4：新增采集table_column表的存储目的地，但是未选择是否拉链存储
	 * 错误的数据访问5：新增采集table_column表的存储目的地，但是未填写存储期限
	 * 错误的数据访问6：新增采集table_column表的存储目的地，选择了拉链存储但是没有选择存储方式
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveTbStoInfo(){
		//正确数据访问1：修改agent_info表的保存目的地，由关系型数据库和hbase修改为仅保存到关系型数据库,进数方式为替换，不进行拉链存储，只保存一天
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//在保存前，确认Table_storage_info表中的数据符合期望
			Result storageInfo = SqlOperator.queryResult(db, "select * from " + Table_storage_info.TableName + " where table_id = ?", AGENT_INFO_TABLE_ID);
			assertThat("查询到的agent_info表在table_storage_info中的数据有一条，符合期望", storageInfo.getRowCount(), is(1));
			assertThat("查询到的agent_info表在table_storage_info中的数据有一条，并且<文件格式>符合期望", storageInfo.getString(0, "file_format"), is(FileFormat.ORC.getCode()));
			assertThat("查询到的agent_info表在table_storage_info中的数据有一条，并且<进数方式>符合期望", storageInfo.getString(0, "storage_type"), is(StorageType.ZengLiang.getCode()));
			assertThat("查询到的agent_info表在table_storage_info中的数据有一条，并且<是否拉链存储>符合期望", storageInfo.getString(0, "is_zipper"), is(IsFlag.Shi.getCode()));
			assertThat("查询到的agent_info表在table_storage_info中的数据有一条，并且<存储期限>符合期望", storageInfo.getLong(0, "storage_time"), is(7L));
			//在保存前，确认Data_relation_table表中的数据符合期望
			Result relationTable = SqlOperator.queryResult(db, "select dsl_id from " + Data_relation_table.TableName + " where storage_id in ( select storage_id from " + Table_storage_info.TableName + " where table_id = ?)", AGENT_INFO_TABLE_ID);
			assertThat("查询到的agent_info表在data_relation_table中的数据有两条，符合期望", relationTable.getRowCount(), is(2));
			for(int i = 0; i < relationTable.getRowCount(); i++){
				if(relationTable.getLong(i, "dsl_id") == 4400L){
					assertThat("查询到的agent_info表在data_relation_table中的数据有两条，符合期望", true, is(true));
				}else if(relationTable.getLong(i, "dsl_id") == 4402L){
					assertThat("查询到的agent_info表在data_relation_table中的数据有两条，符合期望", true, is(true));
				}else{
					assertThat("查询到的agent_info表在data_relation_table表中出现了不符合期望的数据", true, is(false));
				}
			}
			//在保存前，确认Data_extraction_def表中的数据符合期望
			Result result = SqlOperator.queryResult(db, "select dbfile_format from " + Data_extraction_def.TableName + " where table_id = ?", AGENT_INFO_TABLE_ID);
			assertThat("查询到agent_info在data_extraction_def表中的数据符合有一条，符合期望", result.getRowCount(), is(1));
			assertThat("查询到agent_info在data_extraction_def表中的数据符合有一条，符合期望", result.getString(0, "dbfile_format"), is(FileFormat.ORC.getCode()));
		}

		//构造访问被测方法的参数
		List<Table_storage_info> tableStorageInfos = new ArrayList<>();
		List<DataStoRelaParam> dataStoRelaParams = new ArrayList<>();

		Table_storage_info storageInfo = new Table_storage_info();
		storageInfo.setTable_id(AGENT_INFO_TABLE_ID);
		storageInfo.setStorage_type(StorageType.TiHuan.getCode());
		storageInfo.setStorage_time(1L);
		storageInfo.setIs_zipper(IsFlag.Fou.getCode());

		tableStorageInfos.add(storageInfo);

		DataStoRelaParam paramOne = new DataStoRelaParam();
		paramOne.setTableId(AGENT_INFO_TABLE_ID);
		long[] dslIds = {4400L};
		paramOne.setDslIds(dslIds);

		dataStoRelaParams.add(paramOne);

		String rightStringOne = new HttpClient()
				.addData("tbStoInfoString", JSON.toJSONString(tableStorageInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("dslIdString", JSON.toJSONString(dataStoRelaParams))
				.post(getActionUrl("saveTbStoInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));
		Integer returnValue = (Integer) rightResultOne.getData();
		assertThat(returnValue == FIRST_DATABASESET_ID, is(true));

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//在保存后，确认Table_storage_info表中的数据符合期望
			Result afterStorageInfo = SqlOperator.queryResult(db, "select * from " + Table_storage_info.TableName + " where table_id = ?", AGENT_INFO_TABLE_ID);
			assertThat("查询到的agent_info表在table_storage_info中的数据有一条，符合期望", afterStorageInfo.getRowCount(), is(1));
			assertThat("查询到的agent_info表在table_storage_info中的数据有一条，并且<文件格式>符合期望", afterStorageInfo.getString(0, "file_format"), is(FileFormat.ORC.getCode()));
			assertThat("查询到的agent_info表在table_storage_info中的数据有一条，并且<进数方式>符合期望", afterStorageInfo.getString(0, "storage_type"), is(StorageType.TiHuan.getCode()));
			assertThat("查询到的agent_info表在table_storage_info中的数据有一条，并且<是否拉链存储>符合期望", afterStorageInfo.getString(0, "is_zipper"), is(IsFlag.Fou.getCode()));
			assertThat("查询到的agent_info表在table_storage_info中的数据有一条，并且<存储期限>符合期望", afterStorageInfo.getLong(0, "storage_time"), is(1L));
			//在保存后，确认Data_relation_table表中的数据符合期望
			Result afterRelationTable = SqlOperator.queryResult(db, "select dsl_id from " + Data_relation_table.TableName + " where storage_id in ( select storage_id from " + Table_storage_info.TableName + " where table_id = ?)", AGENT_INFO_TABLE_ID);
			assertThat("查询到的agent_info表在data_relation_table中的数据有一条，符合期望", afterRelationTable.getRowCount(), is(1));
			assertThat("查询到的agent_info表在data_relation_table中的数据有一条，存储目的地为关系型数据库", afterRelationTable.getLong(0, "dsl_id"), is(4400L));
		}

		tableStorageInfos.clear();
		dataStoRelaParams.clear();

		//正确数据访问2：修改data_source表的存储目的地，由SOLR变为进入MongoDB和关系型数据库, 并且进数方式为增量，保存7天，做拉链存储
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//在保存前，确认Table_storage_info表中的数据符合期望
			Result beforeStorageInfo = SqlOperator.queryResult(db, "select * from " + Table_storage_info.TableName + " where table_id = ?", DATA_SOURCE_TABLE_ID);
			assertThat("查询到的data_source表在table_storage_info中的数据有一条，符合期望", beforeStorageInfo.getRowCount(), is(1));
			assertThat("查询到的data_source表在table_storage_info中的数据有一条，并且<文件格式>符合期望", beforeStorageInfo.getString(0, "file_format"), is(FileFormat.FeiDingChang.getCode()));
			assertThat("查询到的data_source表在table_storage_info中的数据有一条，并且<进数方式>符合期望", beforeStorageInfo.getString(0, "storage_type"), is(StorageType.ZhuiJia.getCode()));
			assertThat("查询到的data_source表在table_storage_info中的数据有一条，并且<是否拉链存储>符合期望", beforeStorageInfo.getString(0, "is_zipper"), is(IsFlag.Fou.getCode()));
			assertThat("查询到的data_source表在table_storage_info中的数据有一条，并且<存储期限>符合期望", beforeStorageInfo.getLong(0, "storage_time"), is(1L));
			//在保存前，确认Data_relation_table表中的数据符合期望
			Result relationTable = SqlOperator.queryResult(db, "select dsl_id from " + Data_relation_table.TableName + " where storage_id in ( select storage_id from " + Table_storage_info.TableName + " where table_id = ?)", DATA_SOURCE_TABLE_ID);
			assertThat("查询到的data_source表在data_relation_table中的数据有1条，符合期望", relationTable.getRowCount(), is(1));
			assertThat("查询到的data_source表在data_relation_table中的数据有1条，符合期望", relationTable.getLong(0, "dsl_id"), is(4399L));
			//在保存前，确认Data_extraction_def表中的数据符合期望
			Result result = SqlOperator.queryResult(db, "select dbfile_format from " + Data_extraction_def.TableName + " where table_id = ?", DATA_SOURCE_TABLE_ID);
			assertThat("查询到data_source在data_extraction_def表中的数据符合有一条，符合期望", result.getRowCount(), is(1));
			assertThat("查询到data_source在data_extraction_def表中的数据符合有一条，符合期望", result.getString(0, "dbfile_format"), is(FileFormat.FeiDingChang.getCode()));
		}

		Table_storage_info storageInfoTwo = new Table_storage_info();
		storageInfoTwo.setTable_id(DATA_SOURCE_TABLE_ID);
		storageInfoTwo.setStorage_type(StorageType.ZengLiang.getCode());
		storageInfoTwo.setStorage_time(7L);
		storageInfoTwo.setIs_zipper(IsFlag.Shi.getCode());

		tableStorageInfos.add(storageInfoTwo);

		DataStoRelaParam paramTwo = new DataStoRelaParam();
		paramTwo.setTableId(DATA_SOURCE_TABLE_ID);
		long[] dslIdsTwo = {4400L, 4403L};
		paramTwo.setDslIds(dslIdsTwo);

		dataStoRelaParams.add(paramTwo);

		String rightStringTwo = new HttpClient()
				.addData("tbStoInfoString", JSON.toJSONString(tableStorageInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("dslIdString", JSON.toJSONString(dataStoRelaParams))
				.post(getActionUrl("saveTbStoInfo")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));
		Integer returnValueTwo = (Integer) rightResultTwo.getData();
		assertThat(returnValueTwo == FIRST_DATABASESET_ID, is(true));

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//在保存后，确认Table_storage_info表中的数据符合期望
			Result beforeStorageInfo = SqlOperator.queryResult(db, "select * from " + Table_storage_info.TableName + " where table_id = ?", DATA_SOURCE_TABLE_ID);
			assertThat("查询到的data_source表在table_storage_info中的数据有一条，符合期望", beforeStorageInfo.getRowCount(), is(1));
			assertThat("查询到的data_source表在table_storage_info中的数据有一条，并且<文件格式>符合期望", beforeStorageInfo.getString(0, "file_format"), is(FileFormat.FeiDingChang.getCode()));
			assertThat("查询到的data_source表在table_storage_info中的数据有一条，并且<进数方式>符合期望", beforeStorageInfo.getString(0, "storage_type"), is(StorageType.ZengLiang.getCode()));
			assertThat("查询到的data_source表在table_storage_info中的数据有一条，并且<是否拉链存储>符合期望", beforeStorageInfo.getString(0, "is_zipper"), is(IsFlag.Shi.getCode()));
			assertThat("查询到的data_source表在table_storage_info中的数据有一条，并且<存储期限>符合期望", beforeStorageInfo.getLong(0, "storage_time"), is(7L));
			//在保存后，确认Data_relation_table表中的数据符合期望
			Result relationTable = SqlOperator.queryResult(db, "select dsl_id from " + Data_relation_table.TableName + " where storage_id in ( select storage_id from " + Table_storage_info.TableName + " where table_id = ?)", DATA_SOURCE_TABLE_ID);
			assertThat("查询到的data_source表在data_relation_table中的数据有2条，符合期望", relationTable.getRowCount(), is(2));
			for(int i = 0; i < relationTable.getRowCount(); i++){
				if(relationTable.getLong(i, "dsl_id") == 4400L){
					assertThat("查询到的data_source表在data_relation_table中的数据有两条，符合期望", true, is(true));
				}else if(relationTable.getLong(i, "dsl_id") == 4403L){
					assertThat("查询到的data_source表在data_relation_table中的数据有两条，符合期望", true, is(true));
				}else{
					assertThat("查询到的data_source表在data_relation_table表中出现了不符合期望的数据", true, is(false));
				}
			}

			//在保存后，将因执行saveTbStoInfo<正确的数据访问2>而新增的测试数据删除掉
			int count = SqlOperator.execute(db, "delete from " + Data_relation_table.TableName + " where dsl_id = ?", 4403L);
			assertThat("将因执行saveTbStoInfo<正确的数据访问2>而新增的测试数据删除掉", count, is(1));

			SqlOperator.commitTransaction(db);
		}

		tableStorageInfos.clear();
		dataStoRelaParams.clear();

		//正确数据访问3：新增采集table_info表的存储目的地，目的地为关系型数据库和SOLR(注意构造数据抽取定义表中table_info表的相关数据，测试通过后，将新增进入的相关数据全部删除)
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//由于这是模拟新增table_info表采集，保存存储目的地，所以table_storage_info表和data_relation_table表肯定没有相关数据，但是需要构造table_info表在data_extraction_def中的数据
			Data_extraction_def tableInfoDef = new Data_extraction_def();
			tableInfoDef.setDed_id(TABLE_INFO_TABLE_ID * 2);
			tableInfoDef.setTable_id(TABLE_INFO_TABLE_ID);
			tableInfoDef.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			tableInfoDef.setIs_header(IsFlag.Shi.getCode());
			tableInfoDef.setDatabase_code(DataBaseCode.UTF_8.getCode());
			tableInfoDef.setDbfile_format(FileFormat.PARQUET.getCode());

			int count = tableInfoDef.add(db);
			assertThat("为saveTbStoInfo<正确的数据访问3>而插入data_extraction_def表中的数据成功", count, is(1));
			SqlOperator.commitTransaction(db);
		}

		Table_storage_info storageInfoThree = new Table_storage_info();
		storageInfoThree.setTable_id(TABLE_INFO_TABLE_ID);
		storageInfoThree.setStorage_type(StorageType.ZhuiJia.getCode());
		storageInfoThree.setStorage_time(14L);
		storageInfoThree.setIs_zipper(IsFlag.Shi.getCode());

		tableStorageInfos.add(storageInfoThree);

		DataStoRelaParam paramThree = new DataStoRelaParam();
		paramThree.setTableId(TABLE_INFO_TABLE_ID);
		long[] dslIdsThree = {4400L, 4399L};
		paramThree.setDslIds(dslIdsThree);

		dataStoRelaParams.add(paramThree);

		String rightStringThree = new HttpClient()
				.addData("tbStoInfoString", JSON.toJSONString(tableStorageInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("dslIdString", JSON.toJSONString(dataStoRelaParams))
				.post(getActionUrl("saveTbStoInfo")).getBodyString();
		ActionResult rightResultThree = JsonUtil.toObjectSafety(rightStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultThree.isSuccess(), is(true));
		Integer returnValueThree = (Integer) rightResultThree.getData();
		assertThat(returnValueThree == FIRST_DATABASESET_ID, is(true));

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//在保存后，确认Table_storage_info表中的数据符合期望
			Result beforeStorageInfo = SqlOperator.queryResult(db, "select * from " + Table_storage_info.TableName + " where table_id = ?", TABLE_INFO_TABLE_ID);
			assertThat("查询到的table_info表在table_storage_info中的数据有一条，符合期望", beforeStorageInfo.getRowCount(), is(1));
			assertThat("查询到的table_info表在table_storage_info中的数据有一条，并且<文件格式>符合期望", beforeStorageInfo.getString(0, "file_format"), is(FileFormat.PARQUET.getCode()));
			assertThat("查询到的table_info表在table_storage_info中的数据有一条，并且<进数方式>符合期望", beforeStorageInfo.getString(0, "storage_type"), is(StorageType.ZhuiJia.getCode()));
			assertThat("查询到的table_info表在table_storage_info中的数据有一条，并且<是否拉链存储>符合期望", beforeStorageInfo.getString(0, "is_zipper"), is(IsFlag.Shi.getCode()));
			assertThat("查询到的table_info表在table_storage_info中的数据有一条，并且<存储期限>符合期望", beforeStorageInfo.getLong(0, "storage_time"), is(14L));
			//在保存后，确认Data_relation_table表中的数据符合期望
			Result relationTable = SqlOperator.queryResult(db, "select dsl_id from " + Data_relation_table.TableName + " where storage_id in ( select storage_id from " + Table_storage_info.TableName + " where table_id = ?)", TABLE_INFO_TABLE_ID);
			assertThat("查询到的table_info表在data_relation_table中的数据有两条，符合期望", relationTable.getRowCount(), is(2));
			for(int i = 0; i < relationTable.getRowCount(); i++){
				if(relationTable.getLong(i, "dsl_id") == 4400L){
					assertThat("查询到的table_info表在data_relation_table中的数据有两条，符合期望", true, is(true));
				}else if(relationTable.getLong(i, "dsl_id") == 4399L){
					assertThat("查询到的table_info表在data_relation_table中的数据有两条，符合期望", true, is(true));
				}else{
					assertThat("查询到的table_info表在data_relation_table表中出现了不符合期望的数据", true, is(false));
				}
			}

			//删除新增的测试数据
			int countOne = SqlOperator.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ?", TABLE_INFO_TABLE_ID);
			int countTwo = SqlOperator.execute(db, "delete from " + Data_relation_table.TableName +
					" where storage_id in (select storage_id from " + Table_storage_info.TableName + " where table_id = ?)", TABLE_INFO_TABLE_ID);
			int countThree = SqlOperator.execute(db, "delete from " + Table_storage_info.TableName + " where table_id = ?", TABLE_INFO_TABLE_ID);

			assertThat("saveTbStoInfo<正确的数据访问3>执行完毕后，删除新增的数据成功", countOne, is(1));
			assertThat("saveTbStoInfo<正确的数据访问3>执行完毕后，删除新增的数据成功", countTwo, is(2));
			assertThat("saveTbStoInfo<正确的数据访问3>执行完毕后，删除新增的数据成功", countThree, is(1));
			SqlOperator.commitTransaction(db);
		}

		tableStorageInfos.clear();
		dataStoRelaParams.clear();

		//正确数据访问4：新增采集table_column表的存储目的地，目的地为关系型数据库和SOLR，不做拉链存储(注意构造数据抽取定义表中table_column表的相关数据，测试通过后，将新增进入的相关数据全部删除)
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//由于这是模拟新增table_column表采集，保存存储目的地，所以table_storage_info表和data_relation_table表肯定没有相关数据，但是需要构造table_column表在data_extraction_def中的数据
			Data_extraction_def tableInfoDef = new Data_extraction_def();
			tableInfoDef.setDed_id(TABLE_COLUMN_TABLE_ID * 2);
			tableInfoDef.setTable_id(TABLE_COLUMN_TABLE_ID);
			tableInfoDef.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			tableInfoDef.setIs_header(IsFlag.Shi.getCode());
			tableInfoDef.setDatabase_code(DataBaseCode.UTF_8.getCode());
			tableInfoDef.setDbfile_format(FileFormat.PARQUET.getCode());

			int count = tableInfoDef.add(db);
			assertThat("为saveTbStoInfo<正确数据访问4>而插入data_extraction_def表中的数据成功", count, is(1));
			SqlOperator.commitTransaction(db);
		}

		Table_storage_info storageInfoFour = new Table_storage_info();
		storageInfoFour.setTable_id(TABLE_COLUMN_TABLE_ID);
		storageInfoFour.setStorage_time(14L);
		storageInfoFour.setIs_zipper(IsFlag.Fou.getCode());

		tableStorageInfos.add(storageInfoFour);

		DataStoRelaParam paramFour = new DataStoRelaParam();
		paramFour.setTableId(TABLE_COLUMN_TABLE_ID);
		long[] dslIdsFour = {4400L, 4399L};
		paramFour.setDslIds(dslIdsFour);

		dataStoRelaParams.add(paramFour);

		String rightStringFour = new HttpClient()
				.addData("tbStoInfoString", JSON.toJSONString(tableStorageInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("dslIdString", JSON.toJSONString(dataStoRelaParams))
				.post(getActionUrl("saveTbStoInfo")).getBodyString();
		ActionResult rightResultFour = JsonUtil.toObjectSafety(rightStringFour, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultFour.isSuccess(), is(true));
		Integer returnValueFour = (Integer) rightResultFour.getData();
		assertThat(returnValueFour == FIRST_DATABASESET_ID, is(true));

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//在保存后，确认Table_storage_info表中的数据符合期望
			Result beforeStorageInfo = SqlOperator.queryResult(db, "select * from " + Table_storage_info.TableName + " where table_id = ?", TABLE_COLUMN_TABLE_ID);
			assertThat("查询到的table_column表在table_storage_info中的数据有一条，符合期望", beforeStorageInfo.getRowCount(), is(1));
			assertThat("查询到的table_column表在table_storage_info中的数据有一条，并且<文件格式>符合期望", beforeStorageInfo.getString(0, "file_format"), is(FileFormat.PARQUET.getCode()));
			assertThat("查询到的table_column表在table_storage_info中的数据有一条，并且<进数方式>符合期望", beforeStorageInfo.getString(0, "storage_type"), is(StorageType.TiHuan.getCode()));
			assertThat("查询到的table_column表在table_storage_info中的数据有一条，并且<是否拉链存储>符合期望", beforeStorageInfo.getString(0, "is_zipper"), is(IsFlag.Fou.getCode()));
			assertThat("查询到的table_column表在table_storage_info中的数据有一条，并且<存储期限>符合期望", beforeStorageInfo.getLong(0, "storage_time"), is(14L));
			//在保存后，确认Data_relation_table表中的数据符合期望
			Result relationTable = SqlOperator.queryResult(db, "select dsl_id from " + Data_relation_table.TableName + " where storage_id in ( select storage_id from " + Table_storage_info.TableName + " where table_id = ?)", TABLE_COLUMN_TABLE_ID);
			assertThat("查询到的table_info表在data_relation_table中的数据有两条，符合期望", relationTable.getRowCount(), is(2));
			for(int i = 0; i < relationTable.getRowCount(); i++){
				if(relationTable.getLong(i, "dsl_id") == 4400L){
					assertThat("查询到的table_column表在data_relation_table中的数据有两条，符合期望", true, is(true));
				}else if(relationTable.getLong(i, "dsl_id") == 4399L){
					assertThat("查询到的table_column表在data_relation_table中的数据有两条，符合期望", true, is(true));
				}else{
					assertThat("查询到的table_column表在data_relation_table表中出现了不符合期望的数据", true, is(false));
				}
			}

			//删除新增的测试数据
			int countOne = SqlOperator.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ?", TABLE_COLUMN_TABLE_ID);
			int countTwo = SqlOperator.execute(db, "delete from " + Data_relation_table.TableName +
					" where storage_id in (select storage_id from " + Table_storage_info.TableName + " where table_id = ?)", TABLE_COLUMN_TABLE_ID);
			int countThree = SqlOperator.execute(db, "delete from " + Table_storage_info.TableName + " where table_id = ?", TABLE_COLUMN_TABLE_ID);

			assertThat("saveTbStoInfo<正确的数据访问4>执行完毕后，删除新增的数据成功", countOne, is(1));
			assertThat("saveTbStoInfo<正确的数据访问4>执行完毕后，删除新增的数据成功", countTwo, is(2));
			assertThat("saveTbStoInfo<正确的数据访问4>执行完毕后，删除新增的数据成功", countThree, is(1));
			SqlOperator.commitTransaction(db);
		}

		tableStorageInfos.clear();
		dataStoRelaParams.clear();

		//错误的数据访问1：新增采集table_column表的存储目的地，但是没有传入任何一个目的地
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//由于这是模拟新增table_column表采集，保存存储目的地，所以table_storage_info表和data_relation_table表肯定没有相关数据，但是需要构造table_column表在data_extraction_def中的数据
			Data_extraction_def tableInfoDef = new Data_extraction_def();
			tableInfoDef.setDed_id(TABLE_COLUMN_TABLE_ID * 2);
			tableInfoDef.setTable_id(TABLE_COLUMN_TABLE_ID);
			tableInfoDef.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			tableInfoDef.setIs_header(IsFlag.Shi.getCode());
			tableInfoDef.setDatabase_code(DataBaseCode.UTF_8.getCode());
			tableInfoDef.setDbfile_format(FileFormat.PARQUET.getCode());

			int count = tableInfoDef.add(db);
			assertThat("为saveTbStoInfo<错误的数据访问1>而插入data_extraction_def表中的数据成功", count, is(1));
			SqlOperator.commitTransaction(db);
		}

		Table_storage_info storageInfoSeven = new Table_storage_info();
		storageInfoSeven.setTable_id(TABLE_COLUMN_TABLE_ID);
		storageInfoSeven.setStorage_type(StorageType.ZhuiJia.getCode());
		storageInfoSeven.setStorage_time(14L);
		storageInfoSeven.setIs_zipper(IsFlag.Shi.getCode());

		tableStorageInfos.add(storageInfoSeven);

		DataStoRelaParam paramSeven = new DataStoRelaParam();
		paramSeven.setTableId(TABLE_COLUMN_TABLE_ID);
		long[] dslIdsSeven = {};
		paramSeven.setDslIds(dslIdsSeven);

		dataStoRelaParams.add(paramSeven);

		String wrongStringOne = new HttpClient()
				.addData("tbStoInfoString", JSON.toJSONString(tableStorageInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("dslIdString", JSON.toJSONString(dataStoRelaParams))
				.post(getActionUrl("saveTbStoInfo")).getBodyString();
		ActionResult wrongResultOne = JsonUtil.toObjectSafety(wrongStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultOne.isSuccess(), is(false));

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			int countOne = SqlOperator.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ?", TABLE_COLUMN_TABLE_ID);
			assertThat("saveTbStoInfo<错误的数据访问1>执行完毕后，删除新增的数据成功", countOne, is(1));
			SqlOperator.commitTransaction(db);
		}

		dataStoRelaParams.clear();

		//错误的数据访问2：新增采集table_column表的存储目的地，但是在数据抽取定义表中没有构造table_column表的抽取信息
		DataStoRelaParam param = new DataStoRelaParam();
		param.setTableId(TABLE_COLUMN_TABLE_ID);
		long[] dslIdes = {4399L, 4400L};
		param.setDslIds(dslIdes);

		dataStoRelaParams.add(param);

		String wrongStringTwo = new HttpClient()
				.addData("tbStoInfoString", JSON.toJSONString(tableStorageInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("dslIdString", JSON.toJSONString(dataStoRelaParams))
				.post(getActionUrl("saveTbStoInfo")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));

		tableStorageInfos.clear();
		dataStoRelaParams.clear();

		//错误的数据访问3：新增采集table_column表的存储目的地，但是未关联表
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//由于这是模拟新增table_column表采集，保存存储目的地，所以table_storage_info表和data_relation_table表肯定没有相关数据，但是需要构造table_column表在data_extraction_def中的数据
			Data_extraction_def tableInfoDef = new Data_extraction_def();
			tableInfoDef.setDed_id(TABLE_COLUMN_TABLE_ID * 2);
			tableInfoDef.setTable_id(TABLE_COLUMN_TABLE_ID);
			tableInfoDef.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			tableInfoDef.setIs_header(IsFlag.Shi.getCode());
			tableInfoDef.setDatabase_code(DataBaseCode.UTF_8.getCode());
			tableInfoDef.setDbfile_format(FileFormat.PARQUET.getCode());

			int count = tableInfoDef.add(db);
			assertThat("为saveTbStoInfo<错误的数据访问3>而插入data_extraction_def表中的数据成功", count, is(1));
			SqlOperator.commitTransaction(db);
		}

		Table_storage_info storageInfoFive = new Table_storage_info();
		storageInfoFive.setStorage_type(StorageType.ZhuiJia.getCode());
		storageInfoFive.setStorage_time(14L);
		storageInfoFive.setIs_zipper(IsFlag.Shi.getCode());

		tableStorageInfos.add(storageInfoFive);

		DataStoRelaParam paramFive = new DataStoRelaParam();
		paramFive.setTableId(TABLE_COLUMN_TABLE_ID);
		long[] dslIdsFive = {4399};
		paramFive.setDslIds(dslIdsFive);

		dataStoRelaParams.add(paramFive);

		String wrongStringThree = new HttpClient()
				.addData("tbStoInfoString", JSON.toJSONString(tableStorageInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("dslIdString", JSON.toJSONString(dataStoRelaParams))
				.post(getActionUrl("saveTbStoInfo")).getBodyString();
		ActionResult wrongResultThree = JsonUtil.toObjectSafety(wrongStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultThree.isSuccess(), is(false));

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			int countOne = SqlOperator.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ?", TABLE_COLUMN_TABLE_ID);
			assertThat("saveTbStoInfo<错误的数据访问3>执行完毕后，删除新增的数据成功", countOne, is(1));
			SqlOperator.commitTransaction(db);
		}

		tableStorageInfos.clear();

		//错误的数据访问4：新增采集table_column表的存储目的地，但是未选择是否拉链存储
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//由于这是模拟新增table_column表采集，保存存储目的地，所以table_storage_info表和data_relation_table表肯定没有相关数据，但是需要构造table_column表在data_extraction_def中的数据
			Data_extraction_def tableInfoDef = new Data_extraction_def();
			tableInfoDef.setDed_id(TABLE_COLUMN_TABLE_ID * 2);
			tableInfoDef.setTable_id(TABLE_COLUMN_TABLE_ID);
			tableInfoDef.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			tableInfoDef.setIs_header(IsFlag.Shi.getCode());
			tableInfoDef.setDatabase_code(DataBaseCode.UTF_8.getCode());
			tableInfoDef.setDbfile_format(FileFormat.PARQUET.getCode());

			int count = tableInfoDef.add(db);
			assertThat("为saveTbStoInfo<错误的数据访问4>而插入data_extraction_def表中的数据成功", count, is(1));
			SqlOperator.commitTransaction(db);
		}

		Table_storage_info storageInfoSenven = new Table_storage_info();
		storageInfoSenven.setTable_id(TABLE_COLUMN_TABLE_ID);
		storageInfoSenven.setStorage_type(StorageType.ZhuiJia.getCode());
		storageInfoSenven.setStorage_time(14L);

		tableStorageInfos.add(storageInfoSenven);

		String wrongStringFive = new HttpClient()
				.addData("tbStoInfoString", JSON.toJSONString(tableStorageInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("dslIdString", JSON.toJSONString(dataStoRelaParams))
				.post(getActionUrl("saveTbStoInfo")).getBodyString();
		ActionResult wrongResultFive = JsonUtil.toObjectSafety(wrongStringFive, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultFive.isSuccess(), is(false));

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			int countOne = SqlOperator.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ?", TABLE_COLUMN_TABLE_ID);
			assertThat("saveTbStoInfo<错误的数据访问4>执行完毕后，删除新增的数据成功", countOne, is(1));
			SqlOperator.commitTransaction(db);
		}

		tableStorageInfos.clear();

		//错误的数据访问5：新增采集table_column表的存储目的地，但是未填写存储期限
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//由于这是模拟新增table_column表采集，保存存储目的地，所以table_storage_info表和data_relation_table表肯定没有相关数据，但是需要构造table_column表在data_extraction_def中的数据
			Data_extraction_def tableInfoDef = new Data_extraction_def();
			tableInfoDef.setDed_id(TABLE_COLUMN_TABLE_ID * 2);
			tableInfoDef.setTable_id(TABLE_COLUMN_TABLE_ID);
			tableInfoDef.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			tableInfoDef.setIs_header(IsFlag.Shi.getCode());
			tableInfoDef.setDatabase_code(DataBaseCode.UTF_8.getCode());
			tableInfoDef.setDbfile_format(FileFormat.PARQUET.getCode());

			int count = tableInfoDef.add(db);
			assertThat("为saveTbStoInfo<错误的数据访问5>而插入data_extraction_def表中的数据成功", count, is(1));
			SqlOperator.commitTransaction(db);
		}

		Table_storage_info storageInfoEight = new Table_storage_info();
		storageInfoEight.setTable_id(TABLE_COLUMN_TABLE_ID);
		storageInfoEight.setStorage_type(StorageType.ZhuiJia.getCode());
		storageInfoEight.setIs_zipper(IsFlag.Shi.getCode());

		tableStorageInfos.add(storageInfoEight);

		String wrongStringSix = new HttpClient()
				.addData("tbStoInfoString", JSON.toJSONString(tableStorageInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("dslIdString", JSON.toJSONString(dataStoRelaParams))
				.post(getActionUrl("saveTbStoInfo")).getBodyString();
		ActionResult wrongResultSix = JsonUtil.toObjectSafety(wrongStringSix, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultSix.isSuccess(), is(false));

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			int countOne = SqlOperator.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ?", TABLE_COLUMN_TABLE_ID);
			assertThat("saveTbStoInfo<错误的数据访问5>执行完毕后，删除新增的数据成功", countOne, is(1));
			SqlOperator.commitTransaction(db);
		}

		//错误的数据访问6：新增采集table_column表的存储目的地，选择了拉链存储但是没有选择存储方式
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//由于这是模拟新增table_column表采集，保存存储目的地，所以table_storage_info表和data_relation_table表肯定没有相关数据，但是需要构造table_column表在data_extraction_def中的数据
			Data_extraction_def tableInfoDef = new Data_extraction_def();
			tableInfoDef.setDed_id(TABLE_COLUMN_TABLE_ID * 2);
			tableInfoDef.setTable_id(TABLE_COLUMN_TABLE_ID);
			tableInfoDef.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			tableInfoDef.setIs_header(IsFlag.Shi.getCode());
			tableInfoDef.setDatabase_code(DataBaseCode.UTF_8.getCode());
			tableInfoDef.setDbfile_format(FileFormat.PARQUET.getCode());

			int count = tableInfoDef.add(db);
			assertThat("为saveTbStoInfo<错误的数据访问6>而插入data_extraction_def表中的数据成功", count, is(1));
			SqlOperator.commitTransaction(db);
		}

		Table_storage_info storageInfoNine = new Table_storage_info();
		storageInfoNine.setTable_id(TABLE_COLUMN_TABLE_ID);
		storageInfoNine.setIs_zipper(IsFlag.Shi.getCode());
		storageInfoNine.setStorage_time(14L);

		tableStorageInfos.add(storageInfoNine);

		String wrongStringEight = new HttpClient()
				.addData("tbStoInfoString", JSON.toJSONString(tableStorageInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("dslIdString", JSON.toJSONString(dataStoRelaParams))
				.post(getActionUrl("saveTbStoInfo")).getBodyString();
		ActionResult wrongResultEight = JsonUtil.toObjectSafety(wrongStringEight, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultEight.isSuccess(), is(false));

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			int countOne = SqlOperator.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ?", TABLE_COLUMN_TABLE_ID);
			assertThat("saveTbStoInfo<错误的数据访问6>执行完毕后，删除新增的数据成功", countOne, is(1));
			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 测试保存表存储属性配置
	 *
	 * 正确数据访问4: 修改agent_info表的保存目的地，修改data_source表的存储目的地，新增采集table_info表的存储目的地
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveTbStoInfoTwo(){
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//由于这是模拟新增table_info表采集，保存存储目的地，所以table_storage_info表和data_relation_table表肯定没有相关数据，但是需要构造table_info表在data_extraction_def中的数据
			Data_extraction_def tableInfoDef = new Data_extraction_def();
			tableInfoDef.setDed_id(TABLE_INFO_TABLE_ID * 2);
			tableInfoDef.setTable_id(TABLE_INFO_TABLE_ID);
			tableInfoDef.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			tableInfoDef.setIs_header(IsFlag.Shi.getCode());
			tableInfoDef.setDatabase_code(DataBaseCode.UTF_8.getCode());
			tableInfoDef.setDbfile_format(FileFormat.PARQUET.getCode());

			int count = tableInfoDef.add(db);
			assertThat("为saveTbStoInfo<正确的数据访问3>而插入data_extraction_def表中的数据成功", count, is(1));
			SqlOperator.commitTransaction(db);
		}
		//构造访问被测方法的参数
		List<Table_storage_info> tableStorageInfos = new ArrayList<>();
		List<DataStoRelaParam> dataStoRelaParams = new ArrayList<>();

		Table_storage_info storageInfo = new Table_storage_info();
		storageInfo.setTable_id(AGENT_INFO_TABLE_ID);
		storageInfo.setStorage_type(StorageType.TiHuan.getCode());
		storageInfo.setStorage_time(1L);
		storageInfo.setIs_zipper(IsFlag.Fou.getCode());

		tableStorageInfos.add(storageInfo);

		DataStoRelaParam paramOne = new DataStoRelaParam();
		paramOne.setTableId(AGENT_INFO_TABLE_ID);
		long[] dslIds = {4400L};
		paramOne.setDslIds(dslIds);

		dataStoRelaParams.add(paramOne);

		Table_storage_info storageInfoTwo = new Table_storage_info();
		storageInfoTwo.setTable_id(DATA_SOURCE_TABLE_ID);
		storageInfoTwo.setStorage_type(StorageType.ZengLiang.getCode());
		storageInfoTwo.setStorage_time(7L);
		storageInfoTwo.setIs_zipper(IsFlag.Shi.getCode());

		tableStorageInfos.add(storageInfoTwo);

		DataStoRelaParam paramTwo = new DataStoRelaParam();
		paramTwo.setTableId(DATA_SOURCE_TABLE_ID);
		long[] dslIdsTwo = {4400L, 4403L};
		paramTwo.setDslIds(dslIdsTwo);

		dataStoRelaParams.add(paramTwo);

		Table_storage_info storageInfoThree = new Table_storage_info();
		storageInfoThree.setTable_id(TABLE_INFO_TABLE_ID);
		storageInfoThree.setStorage_type(StorageType.ZhuiJia.getCode());
		storageInfoThree.setStorage_time(14L);
		storageInfoThree.setIs_zipper(IsFlag.Shi.getCode());

		tableStorageInfos.add(storageInfoThree);

		DataStoRelaParam paramThree = new DataStoRelaParam();
		paramThree.setTableId(TABLE_INFO_TABLE_ID);
		long[] dslIdsThree = {4400L, 4399L};
		paramThree.setDslIds(dslIdsThree);

		dataStoRelaParams.add(paramThree);

		String rightStringOne = new HttpClient()
				.addData("tbStoInfoString", JSON.toJSONString(tableStorageInfos))
				.addData("colSetId", FIRST_DATABASESET_ID)
				.addData("dslIdString", JSON.toJSONString(dataStoRelaParams))
				.post(getActionUrl("saveTbStoInfo")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));
		Integer returnValue = (Integer) rightResultOne.getData();
		assertThat(returnValue == FIRST_DATABASESET_ID, is(true));

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			int count = SqlOperator.execute(db, "delete from " + Data_relation_table.TableName + " where dsl_id = ?", 4403L);
			assertThat("将因执行saveTbStoInfoTwo新增的测试数据删除掉", count, is(1));

			int countOne = SqlOperator.execute(db, "delete from " + Data_extraction_def.TableName + " where table_id = ?", TABLE_INFO_TABLE_ID);
			int countTwo = SqlOperator.execute(db, "delete from " + Data_relation_table.TableName +
					" where storage_id in (select storage_id from " + Table_storage_info.TableName + " where table_id = ?)", TABLE_INFO_TABLE_ID);
			int countThree = SqlOperator.execute(db, "delete from " + Table_storage_info.TableName + " where table_id = ?", TABLE_INFO_TABLE_ID);

			assertThat("saveTbStoInfoTwo执行完毕后，删除新增的数据成功", countOne, is(1));
			assertThat("saveTbStoInfoTwo执行完毕后，删除新增的数据成功", countTwo, is(2));
			assertThat("saveTbStoInfoTwo执行完毕后，删除新增的数据成功", countThree, is(1));

			SqlOperator.commitTransaction(db);
		}
	}

	/**
	 * 测试根据存储目的地ID获取选择列画面需要展示的表头信息
	 *
	 * 正确数据访问1：测试存储目的地为<关系型数据库>的表头信息
	 * 正确数据访问2：测试存储目的地为<SOLR>的表头信息
	 * 错误的数据访问1：测试存储目的地为<MONGODB>的表头信息
	 * 错误的测试用例未达到三组：以上所有测试用例已经足够覆盖所有可能出现的情况了
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getColumnHeader(){
		//正确数据访问1：测试存储目的地为<关系型数据库>的表头信息
		String rightStringOne = new HttpClient()
				.addData("dslId", 4400L)
				.post(getActionUrl("getColumnHeader")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Map<String, String> oracleMap = rightResultOne.getDataForMap(String.class, String.class);
		for(String key : oracleMap.keySet()){
			if(key.equalsIgnoreCase("column_name")){
				assertThat(oracleMap.get(key).equalsIgnoreCase("列名"), is(true));
			}else if(key.equalsIgnoreCase("column_ch_name")){
				assertThat(oracleMap.get(key).equalsIgnoreCase("列中文名"), is(true));
			}else if(key.equalsIgnoreCase("主键")){
				assertThat(oracleMap.get(key).equalsIgnoreCase("主键"), is(true));
			}else{
				assertThat("出现了不符合期望的情况,key为" + key, true, is(false));
			}
		}

		//正确数据访问2：测试存储目的地为<SOLR>的表头信息
		String rightStringTwo = new HttpClient()
				.addData("dslId", 4399L)
				.post(getActionUrl("getColumnHeader")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		Map<String, String> solrMap = rightResultTwo.getDataForMap(String.class, String.class);
		for(String key : solrMap.keySet()){
			if(key.equalsIgnoreCase("column_name")){
				assertThat(solrMap.get(key).equalsIgnoreCase("列名"), is(true));
			}else if(key.equalsIgnoreCase("column_ch_name")){
				assertThat(solrMap.get(key).equalsIgnoreCase("列中文名"), is(true));
			}else if(key.equalsIgnoreCase("索引列")){
				assertThat(solrMap.get(key).equalsIgnoreCase("索引列"), is(true));
			}else{
				assertThat("出现了不符合期望的情况,key为" + key, true, is(false));
			}
		}

		//错误的数据访问1：测试存储目的地为<MONGODB>的表头信息
		String wrongString = new HttpClient()
				.addData("dslId", 4403L)
				.post(getActionUrl("getColumnHeader")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(true));

		Map<String, String> mongoMap = wrongResult.getDataForMap(String.class, String.class);
		for(String key : mongoMap.keySet()){
			if(key.equalsIgnoreCase("column_name")){
				assertThat(mongoMap.get(key).equalsIgnoreCase("列名"), is(true));
			}else if(key.equalsIgnoreCase("column_ch_name")){
				assertThat(mongoMap.get(key).equalsIgnoreCase("列中文名"), is(true));
			}else{
				assertThat("出现了不符合期望的情况,key为" + key, true, is(false));
			}
		}
	}

	/**
	 * 测试根据存储目的地ID获取选择列画面需要的附加属性信息ID
	 *
	 * 正确数据访问1：测试获取存储目的地为<关系型数据库>的附加属性信息ID
	 * 正确数据访问2：测试存储目的地为<SOLR>的附加属性信息ID
	 * 错误的数据访问1：测试存储目的地为<MONGODB>的附加属性信息ID
	 * 错误的测试用例未达到三组：以上所有测试用例已经足够覆盖所有可能出现的情况了
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getDataStoreLayerAddedId(){
		//正确数据访问1：测试获取存储目的地为<关系型数据库>的附加属性信息ID
		String rightStringOne = new HttpClient()
				.addData("dslId", 4400L)
				.post(getActionUrl("getDataStoreLayerAddedId")).getBodyString();
		ActionResult rightResultOne = JsonUtil.toObjectSafety(rightStringOne, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultOne.isSuccess(), is(true));

		Map<String, Long> oracleMap = rightResultOne.getDataForMap(String.class, Long.class);
		assertThat(oracleMap.get(StoreLayerAdded.ZhuJian.getCode()), is(439999L));

		//正确数据访问2：测试存储目的地为<SOLR>的附加属性信息ID
		String rightStringTwo = new HttpClient()
				.addData("dslId", 4399L)
				.post(getActionUrl("getDataStoreLayerAddedId")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		Map<String, Long> solrMap = rightResultTwo.getDataForMap(String.class, Long.class);
		assertThat(solrMap.get(StoreLayerAdded.SuoYinLie.getCode()), is(440000L));

		//错误的数据访问1：测试存储目的地为<MONGODB>的附加属性信息ID
		String wrongString = new HttpClient()
				.addData("dslId", 4403L)
				.post(getActionUrl("getDataStoreLayerAddedId")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(true));

		Map<String, Long> mongoMap = wrongResult.getDataForMap(String.class, Long.class);
		assertThat(mongoMap.isEmpty(), is(true));
	}

	/**
	 * 测试根据在配置表存储信息时，更新表中文名和表名
	 *
	 * 正确数据访问1：模拟保存sys_user和code_info的表名和表中文名
	 * 错误的数据访问1：在模拟保存sys_user和code_info的表名和表中文名时，传入错的sys_user表ID
	 * 错误的数据访问2：在模拟保存sys_user和code_info的表名和表中文名时，未传入sys_user表的表ID
	 * 错误的数据访问3：在模拟保存sys_user和code_info的表名和表中文名时，未传入sys_user表的表名
	 * 错误的数据访问4：在模拟保存sys_user和code_info的表名和表中文名时，未传入sys_user表的表中文名
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void updateTableName(){
		//正确数据访问1：模拟保存sys_user和code_info的表名和表中文名
		List<Table_info> tableInfos = new ArrayList<>();
		for(int i = 0; i < 2; i++){
			long tableId = i % 2 == 0 ? SYS_USER_TABLE_ID : CODE_INFO_TABLE_ID;
			String tableName = i % 2 == 0 ? "sys_user_update" : "code_info_update";
			String tableZhName = i % 2 == 0 ? "用户表_update" : "代码信息表_update";

			Table_info tableInfo = new Table_info();
			tableInfo.setTable_id(tableId);
			tableInfo.setTable_name(tableName);
			tableInfo.setTable_ch_name(tableZhName);

			tableInfos.add(tableInfo);
		}

		String rightString = new HttpClient()
				.addData("tableString", JSON.toJSONString(tableInfos))
				.post(getActionUrl("updateTableName")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Result result = SqlOperator.queryResult(db, "select table_name, table_ch_name from " + Table_info.TableName + " where database_id = ?", FIRST_DATABASESET_ID);
			assertThat("查询获得的结果一共有四条", result.getRowCount(), is(4));
			for(int i = 0; i < result.getRowCount(); i++){
				if(result.getString(i ,"table_name").equalsIgnoreCase("sys_user_update")){
					assertThat("表名被更新为<sys_user_update>，表中文名被更新为<用户表_update>", result.getString(i ,"table_ch_name").equalsIgnoreCase("用户表_update"), is(true));
				}else if(result.getString(i ,"table_name").equalsIgnoreCase("code_info_update")){
					assertThat("表名被更新为<code_info_update>，表中文名被更新为<代码信息表_update>", result.getString(i ,"table_ch_name").equalsIgnoreCase("代码信息表_update"), is(true));
				}else if(result.getString(i ,"table_name").equalsIgnoreCase("agent_info")){
					assertThat("agent_info表和data_source表不受影响", result.getString(i ,"table_ch_name").equalsIgnoreCase("Agent信息表"), is(true));
				}else if(result.getString(i ,"table_name").equalsIgnoreCase("data_source")){
					assertThat("agent_info表和data_source表不受影响", result.getString(i ,"table_ch_name").equalsIgnoreCase("数据源表"), is(true));
				}else{
					assertThat("更新表的中文名和表名，出现了不符合预期的情况", true, is(false));
				}
			}
		}

		tableInfos.clear();

		//错误的数据访问1：在模拟保存sys_user和code_info的表名和表中文名时，传入错的sys_user表ID
		for(int i = 0; i < 2; i++){
			//这里故意构造错误的sys_user表的ID
			long tableId = i % 2 == 0 ? UNEXPECTED_ID : CODE_INFO_TABLE_ID;
			String tableName = i % 2 == 0 ? "sys_user_update" : "code_info_update";
			String tableZhName = i % 2 == 0 ? "用户表_update" : "代码信息表_update";

			Table_info tableInfo = new Table_info();
			tableInfo.setTable_id(tableId);
			tableInfo.setTable_name(tableName);
			tableInfo.setTable_ch_name(tableZhName);

			tableInfos.add(tableInfo);
		}

		String wrongString = new HttpClient()
				.addData("tableString", JSON.toJSONString(tableInfos))
				.post(getActionUrl("updateTableName")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(false));

		tableInfos.clear();

		//错误的数据访问2：在模拟保存sys_user和code_info的表名和表中文名时，未传入sys_user表的表ID
		Table_info tableInfoOne = new Table_info();
		tableInfoOne.setTable_name("sys_user_update");
		tableInfoOne.setTable_ch_name("用户表_update");

		Table_info tableInfoTwo = new Table_info();
		tableInfoTwo.setTable_id(CODE_INFO_TABLE_ID);
		tableInfoTwo.setTable_name("code_info_update");
		tableInfoTwo.setTable_ch_name("代码信息表_update");

		tableInfos.add(tableInfoOne);
		tableInfos.add(tableInfoTwo);

		String wrongStringTwo = new HttpClient()
				.addData("tableString", JSON.toJSONString(tableInfos))
				.post(getActionUrl("updateTableName")).getBodyString();
		ActionResult wrongResultTwo = JsonUtil.toObjectSafety(wrongStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultTwo.isSuccess(), is(false));

		tableInfos.clear();

		//错误的数据访问3：在模拟保存sys_user和code_info的表名和表中文名时，未传入sys_user表的表名
		for(int i = 0; i < 2; i++){
			long tableId = i % 2 == 0 ? SYS_USER_TABLE_ID : CODE_INFO_TABLE_ID;
			String tableName = i % 2 == 0 ? "" : "code_info_update";
			String tableZhName = i % 2 == 0 ? "用户表_update" : "代码信息表_update";

			Table_info tableInfo = new Table_info();
			tableInfo.setTable_id(tableId);
			tableInfo.setTable_name(tableName);
			tableInfo.setTable_ch_name(tableZhName);

			tableInfos.add(tableInfo);
		}

		String wrongStringThree = new HttpClient()
				.addData("tableString", JSON.toJSONString(tableInfos))
				.post(getActionUrl("updateTableName")).getBodyString();
		ActionResult wrongResultThree = JsonUtil.toObjectSafety(wrongStringThree, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultThree.isSuccess(), is(false));

		tableInfos.clear();

		//错误的数据访问4：在模拟保存sys_user和code_info的表名和表中文名时，未传入sys_user表的表中文名
		for(int i = 0; i < 2; i++){
			long tableId = i % 2 == 0 ? SYS_USER_TABLE_ID : CODE_INFO_TABLE_ID;
			String tableName = i % 2 == 0 ? "sys_user_update" : "code_info_update";
			String tableZhName = i % 2 == 0 ? "" : "代码信息表_update";

			Table_info tableInfo = new Table_info();
			tableInfo.setTable_id(tableId);
			tableInfo.setTable_name(tableName);
			tableInfo.setTable_ch_name(tableZhName);

			tableInfos.add(tableInfo);
		}

		String wrongStringFour = new HttpClient()
				.addData("tableString", JSON.toJSONString(tableInfos))
				.post(getActionUrl("updateTableName")).getBodyString();
		ActionResult wrongResultFour = JsonUtil.toObjectSafety(wrongStringFour, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResultFour.isSuccess(), is(false));
	}

	@After
	public void after(){
		InitAndDestDataForStoDest.after();
	}
}
