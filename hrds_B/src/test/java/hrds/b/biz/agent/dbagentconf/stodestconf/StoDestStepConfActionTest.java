package hrds.b.biz.agent.dbagentconf.stodestconf;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.b.biz.agent.dbagentconf.BaseInitData;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.StorageType;
import hrds.commons.codes.store_type;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "定义存储目的地配置测试类", author = "WangZhengcheng")
public class StoDestStepConfActionTest extends WebBaseTestCase{

	private static final long FIRST_DATABASESET_ID = 1001L;
	private static final long SECOND_DATABASESET_ID = 1002L;
	private static final long AGENT_INFO_TABLE_ID = 7003L;
	private static final long DATA_SOURCE_TABLE_ID = 7004L;
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
	 *      16、构造卸数文件相关数据
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
	 *          24-1、agent_info表保存进入关系型数据库，进入hbase
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

	@Test
	public void test(){
		System.out.println("-------------------------------");
	}

	/**
	 * 测试根据数据库设置ID获得定义存储目的地页面初始化信息
	 *
	 * 正确数据访问1：使用正确的colSetId访问，应该可以拿到2条数据，表名分别是agent_info和data_source
	 * 错误的数据访问1：使用错误的colSetId访问，应该拿不到任何数据，但是不会报错，访问正常返回
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getInitInfo(){
		//正确数据访问1：使用正确的colSetId访问，应该可以拿到2条数据，表名分别是agent_info和data_source
		String rightString = new HttpClient()
				.addData("colSetId", FIRST_DATABASESET_ID)
				.post(getActionUrl("getInitInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		Result rightData = rightResult.getDataForResult();
		assertThat("获取到的数据有两条", rightData.getRowCount(), is(2));
		for(int i = 0; i < rightData.getRowCount(); i++){
			if(rightData.getString(i, "table_name").equalsIgnoreCase("agent_info")){
				assertThat("获取到表名为agent_info，表中文名为<Agent信息表>", rightData.getString(i, "table_ch_name").equalsIgnoreCase("Agent信息表"), is(true));
				assertThat("获取到表名为agent_info，做拉链存储", rightData.getString(i, "is_zipper"), is(IsFlag.Shi.getCode()));
				assertThat("获取到表名为agent_info，存储方式为<增量>", rightData.getString(i, "storage_type"), is(StorageType.ZengLiang.getCode()));
				assertThat("获取到表名为agent_info，数据保存时间为<7天>", rightData.getLong(i, "storage_time"), is(7L));
			}else if(rightData.getString(i, "table_name").equalsIgnoreCase("data_source")){
				assertThat("获取到表名为data_source，表中文名为<数据源表>", rightData.getString(i, "table_ch_name").equalsIgnoreCase("数据源表"), is(true));
				assertThat("获取到表名为data_source，不做拉链存储", rightData.getString(i, "is_zipper"), is(IsFlag.Fou.getCode()));
				assertThat("获取到表名为data_source，存储方式为<追加>", rightData.getString(i, "storage_type"), is(StorageType.ZhuiJia.getCode()));
				assertThat("获取到表名为data_source，数据保存时间为<1天>", rightData.getLong(i, "storage_time"), is(1L));
			}else{
				assertThat("获取到了不符合期望的结果，表名为：" + rightData.getString(i, "table_name"), false, is(true));
			}
		}

		//错误的数据访问1：使用错误的colSetId访问，应该拿不到任何数据，但是不会报错，访问正常返回
		String wrongString = new HttpClient()
				.addData("colSetId", SECOND_DATABASESET_ID)
				.post(getActionUrl("getInitInfo")).getBodyString();
		ActionResult wrongResult = JsonUtil.toObjectSafety(wrongString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(wrongResult.isSuccess(), is(true));

		Result wrongData = wrongResult.getDataForResult();
		assertThat("使用数据库设置ID为1002进行查询，未能找到数据", wrongData.isEmpty(), is(true));
	}

	/**
	 * 测试获取存储目的地
	 *
	 * 正确数据访问1：尝试获取数据存储目的地，能够获得5条数据存储目的地信息
	 * 错误的测试用例未达到三组: 该方法没有参数，不会出现HTTP访问报错的情况，只会根据不同的结果返回不同的数据
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getStoDestInfo(){
		//正确数据访问1：尝试获取数据存储目的地，能够获得5条数据存储目的地信息
		String rightString = new HttpClient()
				.post(getActionUrl("getStoDestInfo")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		Result rightData = rightResult.getDataForResult();
		assertThat("获取到的数据存储目的地有5条", rightData.getRowCount(), is(5));
		for(int i = 0; i < rightData.getRowCount(); i++){
			if(rightData.getString(i, "dsl_name").equalsIgnoreCase("SOLR")){
				assertThat("获取到的存储层配置名为SOLR， 存储类型为<3>", rightData.getString(i, "store_type"), is(store_type.SOLR.getCode()));
			}else if(rightData.getString(i, "dsl_name").equalsIgnoreCase("Oralce")){
				assertThat("获取到的存储层配置名为Oralce， 存储类型为<1>", rightData.getString(i, "store_type"), is(store_type.DATABASE.getCode()));
			}else if(rightData.getString(i, "dsl_name").equalsIgnoreCase("ElasticSearch")){
				assertThat("获取到的存储层配置名为ElasticSearch， 存储类型为<4>", rightData.getString(i, "store_type"), is(store_type.ElasticSearch.getCode()));
			}else if(rightData.getString(i, "dsl_name").equalsIgnoreCase("HBASE")){
				assertThat("获取到的存储层配置名为HBASE， 存储类型为<2>", rightData.getString(i, "store_type"), is(store_type.HBASE.getCode()));
			}else if(rightData.getString(i, "dsl_name").equalsIgnoreCase("MONGODB")){
				assertThat("获取到的存储层配置名为MONGODB， 存储类型为<5>", rightData.getString(i, "store_type"), is(store_type.MONGODB.getCode()));
			}else{
				assertThat("获取到了不符合期望的存储目的地，名字是" + rightData.getString(i, "dsl_name"), false, is(store_type.MONGODB.getCode()));
			}
		}
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
		assertThat("保存目的地为关系型数据库，获取到的key-value键值对有11对", rightDataOne.getRowCount(),is(11) );
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
		assertThat("获取存储目的地为solr的详细信息，value为<SolarUrl>", rightDataTwo.getString(0, "storage_property_val"), is("https://SolarUrl"));

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
			if(rightDataOne.getString(i, "storage_property_key").equalsIgnoreCase("Hbase-site-path")){
				assertThat("获取存储目的地为hbase的详细信息，key为<Hbase-site-path>, value为<Hbase-site.xml>", rightDataOne.getString(i, "storage_property_val").equalsIgnoreCase("Hbase-site.xml"), is(true));
			}else if(rightDataOne.getString(i, "storage_property_key").equalsIgnoreCase("Core-site-path")){
				assertThat("获取存储目的地为hbase的详细信息，key为<Core-site-path>, value为<Core-site.xml>", rightDataOne.getString(i, "storage_property_val").equalsIgnoreCase("Core-site.xml"), is(true));
			}else if(rightDataOne.getString(i, "storage_property_key").equalsIgnoreCase("Hdfs-site-path")){
				assertThat("获取存储目的地为hbase的详细信息，key为<Hdfs-site-path>, value为<Hdfs-site.xml>", rightDataOne.getString(i, "storage_property_val").equalsIgnoreCase("Hdfs-site.xml"), is(true));
			}else{
				assertThat("获取存储目的地为hbase的详细信息出现了不符合期望的情况，key: " + rightDataOne.getString(i, "storage_property_key"), true, is(false));
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
	 * 测试根据tableId获取该表选择的存储目的地和系统中配置的所有存储目的地
	 *
	 * 正确数据访问1：agent_info表保存进入solr，所以使用agent_info表的table_id访问方法，判断在得到的solr记录上的usedFlag是否为true,而其他的记录上usedFlag为false
	 * 正确数据访问2：data_source表保存进入hbase，所以使用data_source表的table_id访问方法，判断在得到的hbase记录上的usedFlag是否为true,而其他的记录上usedFlag为false
	 * 错误的测试用例未达到三组: 以上所有测试用例已经可以满足要求
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getStoDestByTableId(){
		//正确数据访问1：agent_info表保存进入solr，所以使用agent_info表的table_id访问方法，判断在得到的solr记录上的usedFlag是否为true,而其他的记录上usedFlag为false
		String rightString = new HttpClient()
				.addData("tableId", AGENT_INFO_TABLE_ID)
				.post(getActionUrl("getStoDestByTableId")).getBodyString();
		ActionResult rightResult = JsonUtil.toObjectSafety(rightString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResult.isSuccess(), is(true));

		Result rightDataOne = rightResult.getDataForResult();
		assertThat("由于在构造初始化数据的时候配置了5个存储目的地，所以获取到的数据有5条", rightDataOne.getRowCount(), is(5));
		for(int i = 0; i < rightDataOne.getRowCount(); i++){
			if(rightDataOne.getString(i, "dsl_name").equalsIgnoreCase("SOLR")){
				assertThat("数据存储层配置属性名称为<SOLR>, 存储类型为<SOLR>", rightDataOne.getString(i, "store_type"), is(store_type.SOLR.getCode()));
				assertThat("数据存储层配置属性名称为<SOLR>, usedFlag标识位为<true>", rightDataOne.getBoolean(i, "usedFlag"), is(true));
			}else if(rightDataOne.getString(i, "dsl_name").equalsIgnoreCase("Oralce")){
				assertThat("数据存储层配置属性名称为<Oralce>, 存储类型为<关系型数据库>", rightDataOne.getString(i, "store_type"), is(store_type.DATABASE.getCode()));
				assertThat("数据存储层配置属性名称为<Oralce>, usedFlag标识位为<false>", rightDataOne.getBoolean(i, "usedFlag"), is(false));
			}else if(rightDataOne.getString(i, "dsl_name").equalsIgnoreCase("ElasticSearch")){
				assertThat("数据存储层配置属性名称为<ElasticSearch>, 存储类型为<ElasticSearch>", rightDataOne.getString(i, "store_type"), is(store_type.ElasticSearch.getCode()));
				assertThat("数据存储层配置属性名称为<ElasticSearch>, usedFlag标识位为<false>", rightDataOne.getBoolean(i, "usedFlag"), is(false));
			}else if(rightDataOne.getString(i, "dsl_name").equalsIgnoreCase("HBASE")){
				assertThat("数据存储层配置属性名称为<HBASE>, 存储类型为<HBASE>", rightDataOne.getString(i, "store_type"), is(store_type.HBASE.getCode()));
				assertThat("数据存储层配置属性名称为<HBASE>, usedFlag标识位为<false>", rightDataOne.getBoolean(i, "usedFlag"), is(false));
			}else if(rightDataOne.getString(i, "dsl_name").equalsIgnoreCase("MONGODB")){
				assertThat("数据存储层配置属性名称为<MONGODB>, 存储类型为<MONGODB>", rightDataOne.getString(i, "store_type"), is(store_type.MONGODB.getCode()));
				assertThat("数据存储层配置属性名称为<MONGODB>, usedFlag标识位为<false>", rightDataOne.getBoolean(i, "usedFlag"), is(false));
			}else{
				assertThat("获取数据存储层配置出现了不符合期望的情况，数据存储层配置属性名称为：" + rightDataOne.getString(i, "dsl_name"), true, is(false));
			}
		}

		//正确数据访问2：data_source表保存进入hbase，所以使用data_source表的table_id访问方法，判断在得到的hbase记录上的usedFlag是否为true,而其他的记录上usedFlag为false
		String rightStringTwo = new HttpClient()
				.addData("tableId", DATA_SOURCE_TABLE_ID)
				.post(getActionUrl("getStoDestByTableId")).getBodyString();
		ActionResult rightResultTwo = JsonUtil.toObjectSafety(rightStringTwo, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败!"));
		assertThat(rightResultTwo.isSuccess(), is(true));

		Result rightDataTwo = rightResultTwo.getDataForResult();
		assertThat("由于在构造初始化数据的时候配置了5个存储目的地，所以获取到的数据有5条", rightDataTwo.getRowCount(), is(5));
		for(int i = 0; i < rightDataTwo.getRowCount(); i++){
			if(rightDataTwo.getString(i, "dsl_name").equalsIgnoreCase("SOLR")){
				assertThat("数据存储层配置属性名称为<SOLR>, 存储类型为<SOLR>", rightDataTwo.getString(i, "store_type"), is(store_type.SOLR.getCode()));
				assertThat("数据存储层配置属性名称为<SOLR>, usedFlag标识位为<false>", rightDataTwo.getBoolean(i, "usedFlag"), is(false));
			}else if(rightDataTwo.getString(i, "dsl_name").equalsIgnoreCase("Oralce")){
				assertThat("数据存储层配置属性名称为<Oralce>, 存储类型为<关系型数据库>", rightDataTwo.getString(i, "store_type"), is(store_type.DATABASE.getCode()));
				assertThat("数据存储层配置属性名称为<Oralce>, usedFlag标识位为<false>", rightDataTwo.getBoolean(i, "usedFlag"), is(false));
			}else if(rightDataTwo.getString(i, "dsl_name").equalsIgnoreCase("ElasticSearch")){
				assertThat("数据存储层配置属性名称为<ElasticSearch>, 存储类型为<ElasticSearch>", rightDataTwo.getString(i, "store_type"), is(store_type.ElasticSearch.getCode()));
				assertThat("数据存储层配置属性名称为<ElasticSearch>, usedFlag标识位为<false>", rightDataTwo.getBoolean(i, "usedFlag"), is(false));
			}else if(rightDataTwo.getString(i, "dsl_name").equalsIgnoreCase("HBASE")){
				assertThat("数据存储层配置属性名称为<HBASE>, 存储类型为<HBASE>", rightDataTwo.getString(i, "store_type"), is(store_type.HBASE.getCode()));
				assertThat("数据存储层配置属性名称为<HBASE>, usedFlag标识位为<true>", rightDataTwo.getBoolean(i, "usedFlag"), is(true));
			}else if(rightDataTwo.getString(i, "dsl_name").equalsIgnoreCase("MONGODB")){
				assertThat("数据存储层配置属性名称为<MONGODB>, 存储类型为<MONGODB>", rightDataTwo.getString(i, "store_type"), is(store_type.MONGODB.getCode()));
				assertThat("数据存储层配置属性名称为<MONGODB>, usedFlag标识位为<false>", rightDataTwo.getBoolean(i, "usedFlag"), is(false));
			}else{
				assertThat("获取数据存储层配置出现了不符合期望的情况，数据存储层配置属性名称为：" + rightDataTwo.getString(i, "dsl_name"), true, is(false));
			}
		}
	}

	/**
	 * 测试根据表ID获取该表所有的列信息
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void getColumnStoInfo(){

	}

	/**
	 * 测试保存存储目的地为Hbase的表的字段存储信息
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveColStoInfoForHbase(){

	}

	/**
	 * 测试保存表存储属性配置
	 *
	 * 正确数据访问1：
	 * 错误的数据访问1：
	 * 错误的测试用例未达到三组:
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void saveTbStoInfo(){

	}

	@After
	public void after(){
		InitAndDestDataForStoDest.after();
	}
}
