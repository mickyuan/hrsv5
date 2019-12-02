package hrds.b.biz.agent.dbagentconf.startwayconf;


import fd.ng.core.annotation.DocClass;
import fd.ng.web.action.ActionResult;
import hrds.b.biz.agent.dbagentconf.BaseInitData;
import hrds.b.biz.agent.dbagentconf.stodestconf.InitAndDestDataForStoDest;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "定义启动方式Action测试类", author = "WangZhengcheng")
public class StartWayConfActionTest extends WebBaseTestCase{

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
		InitAndDestDataForStartWay.before();
		//模拟登陆
		ActionResult actionResult = BaseInitData.simulatedLogin();
		assertThat("模拟登陆", actionResult.isSuccess(), is(true));
	}

	@After
	public void after(){
		InitAndDestDataForStartWay.after();
	}
}
