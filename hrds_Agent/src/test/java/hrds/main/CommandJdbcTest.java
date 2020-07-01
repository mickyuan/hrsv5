package hrds.main;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.DataBaseJobImpl;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.*;
import hrds.commons.entity.Column_merge;
import hrds.commons.entity.Column_split;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.utils.Constant;
import hrds.testbase.WebBaseTestCase;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@DocClass(desc = "数据库采集测试用例", createdate = "2020/01/07 09:48", author = "zxz")
public class CommandJdbcTest extends WebBaseTestCase {
	//XXX collect_case 页面展现有点小优化，后台少一个字段
	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test1() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test2() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test3() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		collectTableBean.setIs_md5(IsFlag.Shi.getCode());
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test4() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			collectTableBean.setIs_md5(IsFlag.Shi.getCode());
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、计算md5、指定并行数并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test5() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		collectTableBean.setIs_md5(IsFlag.Shi.getCode());
		collectTableBean.setIs_parallel(IsFlag.Shi.getCode());
		collectTableBean.setPageparallels(5);
		collectTableBean.setRec_num_date(DateUtil.getSysDate());
		collectTableBean.setDataincrement(10);
		collectTableBean.setTable_count("8");
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、计算md5、指定并行数并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test6() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			collectTableBean.setIs_md5(IsFlag.Shi.getCode());
			collectTableBean.setIs_parallel(IsFlag.Shi.getCode());
			collectTableBean.setPageparallels(5);
			collectTableBean.setRec_num_date(DateUtil.getSysDate());
			collectTableBean.setDataincrement(10);
			collectTableBean.setTable_count("100");
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、计算md5、指定并行数并行抽取、添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test7() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		collectTableBean.setIs_md5(IsFlag.Shi.getCode());
		collectTableBean.setIs_parallel(IsFlag.Shi.getCode());
		collectTableBean.setPageparallels(5);
		collectTableBean.setRec_num_date(DateUtil.getSysDate());
		collectTableBean.setDataincrement(10);
		collectTableBean.setTable_count("8");
		collectTableBean.setSql("cc_street_name = 'Pine Ridge'");
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、计算md5、指定并行数并行抽取、添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test8() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			collectTableBean.setIs_md5(IsFlag.Shi.getCode());
			collectTableBean.setIs_parallel(IsFlag.Shi.getCode());
			collectTableBean.setPageparallels(5);
			collectTableBean.setRec_num_date(DateUtil.getSysDate());
			collectTableBean.setDataincrement(10);
			collectTableBean.setTable_count("100");
			if ("call_center".equals(collectTableBean.getTable_name()))
				collectTableBean.setSql("cc_street_name = 'Pine Ridge'");
			else if ("item".equals(collectTableBean.getTable_name()))
				collectTableBean.setSql("i_rec_start_date = '2000-10-27'");
			else if ("reason".equals(collectTableBean.getTable_name()))
				collectTableBean.setSql("r_reason_desc like 'reason%'");
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、计算md5、自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test9() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		collectTableBean.setIs_md5(IsFlag.Shi.getCode());
		collectTableBean.setIs_parallel(IsFlag.Shi.getCode());
		collectTableBean.setIs_customize_sql(IsFlag.Shi.getCode());
		collectTableBean.setPage_sql("select * from call_center where cc_street_name = 'Pine Ridge'" +
				" `@^ select * from call_center where cc_street_name = 'Jefferson Tenth' " +
				" `@^ select * from call_center where cc_street_name = 'Center Hill' ");
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、计算md5、自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test10() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			collectTableBean.setIs_md5(IsFlag.Shi.getCode());
			if ("call_center".equals(collectTableBean.getTable_name())) {
				collectTableBean.setIs_parallel(IsFlag.Shi.getCode());
				collectTableBean.setIs_customize_sql(IsFlag.Shi.getCode());
				collectTableBean.setPage_sql("select * from call_center where cc_street_name = 'Pine Ridge'" +
						" `@^ select * from call_center where cc_street_name = 'Jefferson Tenth' " +
						" `@^ select * from call_center where cc_street_name = 'Center Hill' ");
			} else if ("item".equals(collectTableBean.getTable_name())) {
				collectTableBean.setIs_parallel(IsFlag.Shi.getCode());
				collectTableBean.setIs_customize_sql(IsFlag.Shi.getCode());
				collectTableBean.setPage_sql("select * from item where i_rec_start_date = '2000-10-27'" +
						" `@^ select * from item where i_rec_start_date = '1997-10-27' " +
						" `@^ select * from item where i_rec_start_date = '1999-10-28' " +
						" `@^ select * from item where i_rec_start_date = '2001-10-27' ");
			} else if ("reason".equals(collectTableBean.getTable_name())) {
				collectTableBean.setIs_parallel(IsFlag.Shi.getCode());
				collectTableBean.setIs_customize_sql(IsFlag.Shi.getCode());
				collectTableBean.setPage_sql("select * from reason where r_reason_desc like 'Did not like%'" +
						" `@^ select * from reason where r_reason_desc like 'Found a better%' " +
						" `@^ select * from reason where r_reason_desc like 'reason%' ");
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test11() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		collectTableBean.setIs_md5(IsFlag.Fou.getCode());
		collectTableBean.setIs_parallel(IsFlag.Shi.getCode());
		collectTableBean.setIs_customize_sql(IsFlag.Shi.getCode());
		collectTableBean.setPage_sql("select * from call_center where cc_street_name = 'Pine Ridge'" +
				" `@^ select * from call_center where cc_street_name = 'Jefferson Tenth' " +
				" `@^ select * from call_center where cc_street_name = 'Center Hill' ");
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、不计算md5、自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test12() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			collectTableBean.setIs_md5(IsFlag.Fou.getCode());
			if ("call_center".equals(collectTableBean.getTable_name())) {
				collectTableBean.setIs_parallel(IsFlag.Shi.getCode());
				collectTableBean.setIs_customize_sql(IsFlag.Shi.getCode());
				collectTableBean.setPage_sql("select * from call_center where cc_street_name = 'Pine Ridge'" +
						" `@^ select * from call_center where cc_street_name = 'Jefferson Tenth' " +
						" `@^ select * from call_center where cc_street_name = 'Center Hill' ");
			} else if ("item".equals(collectTableBean.getTable_name())) {
				collectTableBean.setIs_parallel(IsFlag.Shi.getCode());
				collectTableBean.setIs_customize_sql(IsFlag.Shi.getCode());
				collectTableBean.setPage_sql("select * from item where i_rec_start_date = '2000-10-27'" +
						" `@^ select * from item where i_rec_start_date = '1997-10-27' " +
						" `@^ select * from item where i_rec_start_date = '1999-10-28' " +
						" `@^ select * from item where i_rec_start_date = '2001-10-27' ");
			} else if ("reason".equals(collectTableBean.getTable_name())) {
				collectTableBean.setIs_parallel(IsFlag.Shi.getCode());
				collectTableBean.setIs_customize_sql(IsFlag.Shi.getCode());
				collectTableBean.setPage_sql("select * from reason where r_reason_desc like 'Did not like%'" +
						" `@^ select * from reason where r_reason_desc like 'Found a better%' " +
						" `@^ select * from reason where r_reason_desc like 'reason%' ");
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成定长文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test13() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
			data_extraction_def.setDbfile_format(FileFormat.DingChang.getCode());
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成定长文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test14() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				data_extraction_def.setDbfile_format(FileFormat.DingChang.getCode());
				data_extraction_def.setDatabase_separatorr("");
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成Parquet文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test15() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
			data_extraction_def.setDbfile_format(FileFormat.PARQUET.getCode());
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成Parquet文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test16() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				data_extraction_def.setDbfile_format(FileFormat.PARQUET.getCode());
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成Orc文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test17() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
			data_extraction_def.setDbfile_format(FileFormat.ORC.getCode());
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成Orc文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test18() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				data_extraction_def.setDbfile_format(FileFormat.ORC.getCode());
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成SequenceFile文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test19() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
			data_extraction_def.setDbfile_format(FileFormat.SEQUENCEFILE.getCode());
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成SequenceFile文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test20() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				data_extraction_def.setDbfile_format(FileFormat.SEQUENCEFILE.getCode());
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成Csv文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test21() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
			data_extraction_def.setDbfile_format(FileFormat.CSV.getCode());
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成Csv文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test22() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				data_extraction_def.setDbfile_format(FileFormat.CSV.getCode());
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、同时生成六种文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test23() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		String plane_url = collectTableBean.getData_extraction_def_list().get(0).getPlane_url();
		String row_separator = StringUtil.string2Unicode(collectTableBean.getData_extraction_def_list().
				get(0).getRow_separator());
		List<Data_extraction_def> data_extraction_def_list = new ArrayList<>();
		//定长
		Data_extraction_def data_extraction_def_DingChang = new Data_extraction_def();
		data_extraction_def_DingChang.setDbfile_format(FileFormat.DingChang.getCode());
		data_extraction_def_DingChang.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		data_extraction_def_DingChang.setRow_separator(row_separator);
		data_extraction_def_DingChang.setDatabase_separatorr(StringUtil.string2Unicode(Constant.DATADELIMITER));
		data_extraction_def_DingChang.setDatabase_code(DataBaseCode.GBK.getCode());
		data_extraction_def_DingChang.setPlane_url(plane_url);
		data_extraction_def_list.add(data_extraction_def_DingChang);
		//非定长
		Data_extraction_def data_extraction_def_FeiDingChang = new Data_extraction_def();
		data_extraction_def_FeiDingChang.setDbfile_format(FileFormat.FeiDingChang.getCode());
		data_extraction_def_FeiDingChang.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		data_extraction_def_FeiDingChang.setRow_separator(row_separator);
		data_extraction_def_FeiDingChang.setDatabase_code(DataBaseCode.GBK.getCode());
		data_extraction_def_FeiDingChang.setPlane_url(plane_url);
		data_extraction_def_list.add(data_extraction_def_FeiDingChang);
		//CSV
		Data_extraction_def data_extraction_def_csv = new Data_extraction_def();
		data_extraction_def_csv.setDbfile_format(FileFormat.CSV.getCode());
		data_extraction_def_csv.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		data_extraction_def_csv.setDatabase_code(DataBaseCode.GBK.getCode());
		data_extraction_def_csv.setPlane_url(plane_url);
		data_extraction_def_list.add(data_extraction_def_csv);
		//Parquet
		Data_extraction_def data_extraction_def_parquet = new Data_extraction_def();
		data_extraction_def_parquet.setDbfile_format(FileFormat.PARQUET.getCode());
		data_extraction_def_parquet.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		data_extraction_def_parquet.setDatabase_code(DataBaseCode.GBK.getCode());
		data_extraction_def_parquet.setPlane_url(plane_url);
		data_extraction_def_list.add(data_extraction_def_parquet);
		//Orc
		Data_extraction_def data_extraction_def_orc = new Data_extraction_def();
		data_extraction_def_orc.setDbfile_format(FileFormat.ORC.getCode());
		data_extraction_def_orc.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		data_extraction_def_orc.setDatabase_code(DataBaseCode.GBK.getCode());
		data_extraction_def_orc.setPlane_url(plane_url);
		data_extraction_def_list.add(data_extraction_def_orc);
		//SequenceFile
		Data_extraction_def data_extraction_def_sequenceFile = new Data_extraction_def();
		data_extraction_def_sequenceFile.setDbfile_format(FileFormat.SEQUENCEFILE.getCode());
		data_extraction_def_sequenceFile.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		data_extraction_def_sequenceFile.setDatabase_code(DataBaseCode.GBK.getCode());
		data_extraction_def_sequenceFile.setPlane_url(plane_url);
		data_extraction_def_list.add(data_extraction_def_sequenceFile);
		collectTableBean.setData_extraction_def_list(data_extraction_def_list);
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、同时生成六种文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test24() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			String plane_url = collectTableBean.getData_extraction_def_list().get(0).getPlane_url();
			String row_separator = StringUtil.string2Unicode(collectTableBean.getData_extraction_def_list().
					get(0).getRow_separator());
			List<Data_extraction_def> data_extraction_def_list = new ArrayList<>();
			//定长
			Data_extraction_def data_extraction_def_DingChang = new Data_extraction_def();
			data_extraction_def_DingChang.setDbfile_format(FileFormat.DingChang.getCode());
			data_extraction_def_DingChang.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			data_extraction_def_DingChang.setRow_separator(row_separator);
			data_extraction_def_DingChang.setDatabase_separatorr(StringUtil.string2Unicode(Constant.DATADELIMITER));
			data_extraction_def_DingChang.setDatabase_code(DataBaseCode.GBK.getCode());
			data_extraction_def_DingChang.setPlane_url(plane_url);
			data_extraction_def_list.add(data_extraction_def_DingChang);
			//非定长
			Data_extraction_def data_extraction_def_FeiDingChang = new Data_extraction_def();
			data_extraction_def_FeiDingChang.setDbfile_format(FileFormat.FeiDingChang.getCode());
			data_extraction_def_FeiDingChang.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			data_extraction_def_FeiDingChang.setRow_separator(row_separator);
			data_extraction_def_FeiDingChang.setDatabase_code(DataBaseCode.GBK.getCode());
			data_extraction_def_FeiDingChang.setPlane_url(plane_url);
			data_extraction_def_list.add(data_extraction_def_FeiDingChang);
			//CSV
			Data_extraction_def data_extraction_def_csv = new Data_extraction_def();
			data_extraction_def_csv.setDbfile_format(FileFormat.CSV.getCode());
			data_extraction_def_csv.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			data_extraction_def_csv.setDatabase_code(DataBaseCode.GBK.getCode());
			data_extraction_def_csv.setPlane_url(plane_url);
			data_extraction_def_list.add(data_extraction_def_csv);
			//Parquet
			Data_extraction_def data_extraction_def_parquet = new Data_extraction_def();
			data_extraction_def_parquet.setDbfile_format(FileFormat.PARQUET.getCode());
			data_extraction_def_parquet.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			data_extraction_def_parquet.setDatabase_code(DataBaseCode.GBK.getCode());
			data_extraction_def_parquet.setPlane_url(plane_url);
			data_extraction_def_list.add(data_extraction_def_parquet);
			//Orc
			Data_extraction_def data_extraction_def_orc = new Data_extraction_def();
			data_extraction_def_orc.setDbfile_format(FileFormat.ORC.getCode());
			data_extraction_def_orc.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			data_extraction_def_orc.setDatabase_code(DataBaseCode.GBK.getCode());
			data_extraction_def_orc.setPlane_url(plane_url);
			data_extraction_def_list.add(data_extraction_def_orc);
			//SequenceFile
			Data_extraction_def data_extraction_def_sequenceFile = new Data_extraction_def();
			data_extraction_def_sequenceFile.setDbfile_format(FileFormat.SEQUENCEFILE.getCode());
			data_extraction_def_sequenceFile.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
			data_extraction_def_sequenceFile.setDatabase_code(DataBaseCode.GBK.getCode());
			data_extraction_def_sequenceFile.setPlane_url(plane_url);
			data_extraction_def_list.add(data_extraction_def_sequenceFile);
			collectTableBean.setData_extraction_def_list(data_extraction_def_list);
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、生成六种格式的文件、选择不同目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test25() {
		//获取路径
		JSONArray multi_landing_directory = JSONArray.parseArray
				(agentInitConfig.getString("multi_landing_directory"));
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		String row_separator = StringUtil.string2Unicode(collectTableBean.getData_extraction_def_list().
				get(0).getRow_separator());
		List<Data_extraction_def> data_extraction_def_list = new ArrayList<>();
		//定长
		Data_extraction_def data_extraction_def_DingChang = new Data_extraction_def();
		data_extraction_def_DingChang.setDbfile_format(FileFormat.DingChang.getCode());
		data_extraction_def_DingChang.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		data_extraction_def_DingChang.setRow_separator(row_separator);
		data_extraction_def_DingChang.setDatabase_separatorr(StringUtil.string2Unicode(Constant.DATADELIMITER));
		data_extraction_def_DingChang.setDatabase_code(DataBaseCode.GBK.getCode());
		data_extraction_def_DingChang.setPlane_url(multi_landing_directory.getString(0
				% multi_landing_directory.size()));
		data_extraction_def_list.add(data_extraction_def_DingChang);
		//非定长
		Data_extraction_def data_extraction_def_FeiDingChang = new Data_extraction_def();
		data_extraction_def_FeiDingChang.setDbfile_format(FileFormat.FeiDingChang.getCode());
		data_extraction_def_FeiDingChang.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		data_extraction_def_FeiDingChang.setRow_separator(row_separator);
		data_extraction_def_FeiDingChang.setDatabase_code(DataBaseCode.GBK.getCode());
		data_extraction_def_FeiDingChang.setPlane_url(multi_landing_directory.getString(1
				% multi_landing_directory.size()));
		data_extraction_def_list.add(data_extraction_def_FeiDingChang);
		//CSV
		Data_extraction_def data_extraction_def_csv = new Data_extraction_def();
		data_extraction_def_csv.setDbfile_format(FileFormat.CSV.getCode());
		data_extraction_def_csv.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		data_extraction_def_csv.setDatabase_code(DataBaseCode.GBK.getCode());
		data_extraction_def_csv.setPlane_url(multi_landing_directory.getString(2
				% multi_landing_directory.size()));
		data_extraction_def_list.add(data_extraction_def_csv);
		//Parquet
		Data_extraction_def data_extraction_def_parquet = new Data_extraction_def();
		data_extraction_def_parquet.setDbfile_format(FileFormat.PARQUET.getCode());
		data_extraction_def_parquet.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		data_extraction_def_parquet.setDatabase_code(DataBaseCode.GBK.getCode());
		data_extraction_def_parquet.setPlane_url(multi_landing_directory.getString(3
				% multi_landing_directory.size()));
		data_extraction_def_list.add(data_extraction_def_parquet);
		//Orc
		Data_extraction_def data_extraction_def_orc = new Data_extraction_def();
		data_extraction_def_orc.setDbfile_format(FileFormat.ORC.getCode());
		data_extraction_def_orc.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		data_extraction_def_orc.setDatabase_code(DataBaseCode.GBK.getCode());
		data_extraction_def_orc.setPlane_url(multi_landing_directory.getString(4
				% multi_landing_directory.size()));
		data_extraction_def_list.add(data_extraction_def_orc);
		//SequenceFile
		Data_extraction_def data_extraction_def_sequenceFile = new Data_extraction_def();
		data_extraction_def_sequenceFile.setDbfile_format(FileFormat.SEQUENCEFILE.getCode());
		data_extraction_def_sequenceFile.setData_extract_type(DataExtractType.ShuJuKuChouQuLuoDi.getCode());
		data_extraction_def_sequenceFile.setDatabase_code(DataBaseCode.GBK.getCode());
		data_extraction_def_sequenceFile.setPlane_url(multi_landing_directory.getString(5
				% multi_landing_directory.size()));
		data_extraction_def_list.add(data_extraction_def_sequenceFile);
		collectTableBean.setData_extraction_def_list(data_extraction_def_list);
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择不同目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test26() {
		//获取路径
		JSONArray multi_landing_directory = JSONArray.parseArray
				(agentInitConfig.getString("multi_landing_directory"));
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		//获取单表的页面配置基本信息
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (int i = 0; i < collectTableBeanArray.size(); i++) {
			CollectTableBean collectTableBean = collectTableBeanArray.get(i);
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				data_extraction_def.setPlane_url(multi_landing_directory.getString(
						i % multi_landing_directory.size()));
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择同一目的地
	 * 选择windows换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test27() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
			data_extraction_def.setRow_separator(StringUtil.string2Unicode("\\r\\n"));
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择同一目的地
	 * 选择windows换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 */
	@Test
	public void test28() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				data_extraction_def.setRow_separator(StringUtil.string2Unicode("\\r\\n"));
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择同一目的地
	 * 选择windows换行符、列分隔符使用*%#、字符集选择GBK、全量采集
	 */
	@Test
	public void test29() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
			data_extraction_def.setDatabase_separatorr(StringUtil.string2Unicode("*%#"));
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择同一目的地
	 * 选择windows换行符、列分隔符使用*%#、字符集选择GBK、全量采集
	 */
	@Test
	public void test30() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				data_extraction_def.setDatabase_separatorr(StringUtil.string2Unicode("*%#"));
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择UTF-8、全量采集
	 */
	@Test
	public void test31() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
			data_extraction_def.setDatabase_code(DataBaseCode.UTF_8.getCode());
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 列和表都不选择清洗、仅生成非定长文件、选择同一目的地
	 * 选择linux换行符、列分隔符使用`@^、字符集选择UTF-8、全量采集
	 */
	@Test
	public void test32() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				data_extraction_def.setDatabase_code(DataBaseCode.UTF_8.getCode());
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 仅生成非定长文件、选择同一目的地、选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 * 清洗：清洗顺序默认、单表所有字段选择去空
	 */
	@Test
	public void test33() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		for (CollectTableColumnBean collectTableColumnBean : collectTableBean.getCollectTableColumnBeanList()) {
			List<ColumnCleanBean> columnCleanBeans = new ArrayList<>();
			ColumnCleanBean columnCleanBean = new ColumnCleanBean();
			//去空
			columnCleanBean.setClean_type(CleanType.ZiFuTrim.getCode());
			columnCleanBeans.add(columnCleanBean);
			collectTableColumnBean.setColumnCleanBeanList(columnCleanBeans);
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 仅生成非定长文件、选择同一目的地、选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 * 清洗：清洗顺序默认、所有表所有字段选择去空
	 */
	@Test
	public void test34() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			for (CollectTableColumnBean collectTableColumnBean : collectTableBean.getCollectTableColumnBeanList()) {
				List<ColumnCleanBean> columnCleanBeans = new ArrayList<>();
				ColumnCleanBean columnCleanBean = new ColumnCleanBean();
				//去空
				columnCleanBean.setClean_type(CleanType.ZiFuTrim.getCode());
				columnCleanBeans.add(columnCleanBean);
				collectTableColumnBean.setColumnCleanBeanList(columnCleanBeans);
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 仅生成非定长文件、选择同一目的地、选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 * 清洗：清洗顺序默认、仅call_center表的cc_name字段选择去空
	 */
	@Test
	public void test35() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			if ("call_center".equals(collectTableBean.getTable_name())) {
				for (CollectTableColumnBean collectTableColumnBean : collectTableBean.getCollectTableColumnBeanList()) {
					if ("cc_name".equals(collectTableColumnBean.getColumn_name())) {
						List<ColumnCleanBean> columnCleanBeans = new ArrayList<>();
						ColumnCleanBean columnCleanBean = new ColumnCleanBean();
						//去空
						columnCleanBean.setClean_type(CleanType.ZiFuTrim.getCode());
						columnCleanBeans.add(columnCleanBean);
						collectTableColumnBean.setColumnCleanBeanList(columnCleanBeans);
					}
				}
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 仅生成非定长文件、选择同一目的地、选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 * 清洗：清洗顺序默认、选择字符补齐、单表的cc_name字段选择前补齐，cc_class字段选择后补齐
	 */
	@Test
	public void test36() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		for (CollectTableColumnBean collectTableColumnBean : collectTableBean.getCollectTableColumnBeanList()) {
			if ("cc_name".equals(collectTableColumnBean.getColumn_name())) {
				List<ColumnCleanBean> columnCleanBeans = new ArrayList<>();
				ColumnCleanBean columnCleanBean = new ColumnCleanBean();
				//字符补齐
				columnCleanBean.setClean_type(CleanType.ZiFuBuQi.getCode());
				//前补齐
				columnCleanBean.setFilling_type(FillingType.QianBuQi.getCode());
				//补齐长度
				columnCleanBean.setFilling_length(40L);
				//补齐字符
				columnCleanBean.setCharacter_filling(StringUtil.string2Unicode("%"));
				columnCleanBeans.add(columnCleanBean);
				collectTableColumnBean.setColumnCleanBeanList(columnCleanBeans);
			} else if ("cc_class".equals(collectTableColumnBean.getColumn_name())) {
				List<ColumnCleanBean> columnCleanBeans = new ArrayList<>();
				ColumnCleanBean columnCleanBean = new ColumnCleanBean();
				//字符补齐
				columnCleanBean.setClean_type(CleanType.ZiFuBuQi.getCode());
				//后补齐
				columnCleanBean.setFilling_type(FillingType.HouBuQi.getCode());
				//补齐长度
				columnCleanBean.setFilling_length(40L);
				//补齐字符
				columnCleanBean.setCharacter_filling(StringUtil.string2Unicode("&"));
				columnCleanBeans.add(columnCleanBean);
				collectTableColumnBean.setColumnCleanBeanList(columnCleanBeans);
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 仅生成非定长文件、选择同一目的地、选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 * 清洗：清洗顺序默认、选择字符补齐、表call_center的cc_name字段选择前补齐，表catalog_page的cp_type字段选择后补齐
	 */
	@Test
	public void test37() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			if ("call_center".equals(collectTableBean.getTable_name())) {
				for (CollectTableColumnBean collectTableColumnBean : collectTableBean.getCollectTableColumnBeanList()) {
					if ("cc_name".equals(collectTableColumnBean.getColumn_name())) {
						List<ColumnCleanBean> columnCleanBeans = new ArrayList<>();
						ColumnCleanBean columnCleanBean = new ColumnCleanBean();
						//字符补齐
						columnCleanBean.setClean_type(CleanType.ZiFuBuQi.getCode());
						//前补齐
						columnCleanBean.setFilling_type(FillingType.QianBuQi.getCode());
						//补齐长度
						columnCleanBean.setFilling_length(40L);
						//补齐字符
						columnCleanBean.setCharacter_filling(StringUtil.string2Unicode("%"));
						columnCleanBeans.add(columnCleanBean);
						collectTableColumnBean.setColumnCleanBeanList(columnCleanBeans);
					}
				}
			}
			if ("catalog_page".equals(collectTableBean.getTable_name())) {
				for (CollectTableColumnBean collectTableColumnBean : collectTableBean.getCollectTableColumnBeanList()) {
					if ("cp_type".equals(collectTableColumnBean.getColumn_name())) {
						List<ColumnCleanBean> columnCleanBeans = new ArrayList<>();
						ColumnCleanBean columnCleanBean = new ColumnCleanBean();
						//字符补齐
						columnCleanBean.setClean_type(CleanType.ZiFuBuQi.getCode());
						//后补齐
						columnCleanBean.setFilling_type(FillingType.HouBuQi.getCode());
						//补齐长度
						columnCleanBean.setFilling_length(40L);
						//补齐字符
						columnCleanBean.setCharacter_filling(StringUtil.string2Unicode("&"));
						columnCleanBeans.add(columnCleanBean);
						collectTableColumnBean.setColumnCleanBeanList(columnCleanBeans);
					}
				}
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 仅生成非定长文件、选择同一目的地、选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 * 清洗：清洗顺序默认、选择字符替换、单表的cc_manager字段Larry Mccray值替换成Larry Mccray ----------------- zxz
	 */
	@Test
	public void test38() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		for (CollectTableColumnBean collectTableColumnBean : collectTableBean.getCollectTableColumnBeanList()) {
			if ("cc_manager".equals(collectTableColumnBean.getColumn_name())) {
				List<ColumnCleanBean> columnCleanBeans = new ArrayList<>();
				ColumnCleanBean columnCleanBean = new ColumnCleanBean();
				//字符替换
				columnCleanBean.setClean_type(CleanType.ZiFuTiHuan.getCode());
				//原字段
				columnCleanBean.setField(StringUtil.string2Unicode("Larry Mccray"));
				//替换字段
				columnCleanBean.setReplace_feild(StringUtil.string2Unicode("Larry Mccray ----------------- zxz"));
				columnCleanBeans.add(columnCleanBean);
				collectTableColumnBean.setColumnCleanBeanList(columnCleanBeans);
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择多表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 仅生成非定长文件、选择同一目的地、选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 * 清洗：清洗顺序默认、选择字符替换、表call_center的cc_manager字段Larry Mccray值替换成Larry Mccray zxz
	 * 表customer_demographics的cd_education_status字段Primary值替换成LarryMccray---------zxz
	 */
	@Test
	public void test39() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			if ("call_center".equals(collectTableBean.getTable_name())) {
				for (CollectTableColumnBean collectTableColumnBean : collectTableBean.getCollectTableColumnBeanList()) {
					if ("cc_manager".equals(collectTableColumnBean.getColumn_name())) {
						List<ColumnCleanBean> columnCleanBeans = new ArrayList<>();
						ColumnCleanBean columnCleanBean = new ColumnCleanBean();
						//字符替换
						columnCleanBean.setClean_type(CleanType.ZiFuTiHuan.getCode());
						//原字段
						columnCleanBean.setField(StringUtil.string2Unicode("Larry Mccray"));
						//替换字段
						columnCleanBean.setReplace_feild(StringUtil.string2Unicode("Larry Mccray ----------------- zxz"));
						columnCleanBeans.add(columnCleanBean);
						collectTableColumnBean.setColumnCleanBeanList(columnCleanBeans);
					}
				}
			}
			if ("customer_demographics".equals(collectTableBean.getTable_name())) {
				for (CollectTableColumnBean collectTableColumnBean : collectTableBean.getCollectTableColumnBeanList()) {
					if ("cd_education_status".equals(collectTableColumnBean.getColumn_name())) {
						List<ColumnCleanBean> columnCleanBeans = new ArrayList<>();
						ColumnCleanBean columnCleanBean = new ColumnCleanBean();
						//字符替换
						columnCleanBean.setClean_type(CleanType.ZiFuTiHuan.getCode());
						//原字段
						columnCleanBean.setField(StringUtil.string2Unicode("Primary"));
						//替换字段
						columnCleanBean.setReplace_feild(StringUtil.string2Unicode("LarryMccray---------zxz"));
						columnCleanBeans.add(columnCleanBean);
						collectTableColumnBean.setColumnCleanBeanList(columnCleanBeans);
					}
				}
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 仅生成非定长文件、选择同一目的地、选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 * 清洗：清洗顺序默认、选择时间转换 、单表的cc_rec_start_date字段yyyy-mm-dd转换成yyyymmdd
	 */
	@Test
	public void test40() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		for (CollectTableColumnBean collectTableColumnBean : collectTableBean.getCollectTableColumnBeanList()) {
			if ("cc_rec_start_date".equals(collectTableColumnBean.getColumn_name())) {
				List<ColumnCleanBean> columnCleanBeans = new ArrayList<>();
				ColumnCleanBean columnCleanBean = new ColumnCleanBean();
				//时间转换
				columnCleanBean.setClean_type(CleanType.ShiJianZhuanHuan.getCode());
				//原各式
				columnCleanBean.setOld_format("yyyy-mm-dd");
				//转换后的格式
				columnCleanBean.setConvert_format("yyyymmdd");
				columnCleanBeans.add(columnCleanBean);
				collectTableColumnBean.setColumnCleanBeanList(columnCleanBeans);
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 仅生成非定长文件、选择同一目的地、选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 * 清洗：清洗顺序默认、选择码值转换 、单表的cc_street_type字段Boulevard转换成Boulevard1、Way转换成Boulevard2、
	 * RD转换成Boulevard3、Court转换成Boulevard4、Ct.转换成Boulevard5
	 */
	@Test
	public void test41() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		for (CollectTableColumnBean collectTableColumnBean : collectTableBean.getCollectTableColumnBeanList()) {
			if ("cc_street_type".equals(collectTableColumnBean.getColumn_name())) {
				List<ColumnCleanBean> columnCleanBeans = new ArrayList<>();
				ColumnCleanBean columnCleanBean = new ColumnCleanBean();
				//码值转换
				columnCleanBean.setClean_type(CleanType.MaZhiZhuanHuan.getCode());
				JSONArray array = new JSONArray();
				JSONObject object1 = new JSONObject();
				object1.put("orig_value", "Boulevard");
				object1.put("code_value", "Boulevard1");
				array.add(object1);
				JSONObject object2 = new JSONObject();
				object2.put("orig_value", "Way");
				object2.put("code_value", "Boulevard2");
				array.add(object2);
				JSONObject object3 = new JSONObject();
				object3.put("orig_value", "RD");
				object3.put("code_value", "Boulevard3");
				array.add(object3);
				JSONObject object4 = new JSONObject();
				object4.put("orig_value", "Court");
				object4.put("code_value", "Boulevard4");
				array.add(object4);
				JSONObject object5 = new JSONObject();
				object5.put("orig_value", "Ct.");
				object5.put("code_value", "Boulevard5");
				array.add(object5);
				columnCleanBean.setCodeTransform(array.toJSONString());
				columnCleanBeans.add(columnCleanBean);
				collectTableColumnBean.setColumnCleanBeanList(columnCleanBeans);
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 仅生成非定长文件、选择同一目的地、选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 * 清洗：清洗顺序默认、选择列拆分、单表的cc_rec_start_date字段按分隔符-拆分成三个字段年、月、日
	 */
	@Test
	public void test42() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		for (CollectTableColumnBean collectTableColumnBean : collectTableBean.getCollectTableColumnBeanList()) {
			if ("cc_rec_start_date".equals(collectTableColumnBean.getColumn_name())) {
				List<ColumnCleanBean> columnCleanBeans = new ArrayList<>();
				ColumnCleanBean columnCleanBean = new ColumnCleanBean();
				//列拆分
				columnCleanBean.setClean_type(CleanType.ZiFuChaiFen.getCode());
				List<Column_split> column_splitList = new ArrayList<>();
				Column_split column_split1 = new Column_split();
				column_split1.setSplit_type(CharSplitType.ZhiDingFuHao.getCode());
				column_split1.setSplit_sep(StringUtil.string2Unicode("-"));
				column_split1.setCol_type("varchar(10)");
				column_split1.setCol_name("year");
				column_split1.setCol_zhname("年");
				column_split1.setSeq(0L);
				column_splitList.add(column_split1);
				Column_split column_split2 = new Column_split();
				column_split2.setSplit_type(CharSplitType.ZhiDingFuHao.getCode());
				column_split2.setSplit_sep(StringUtil.string2Unicode("-"));
				column_split2.setCol_type("varchar(10)");
				column_split2.setCol_name("month");
				column_split2.setCol_zhname("月");
				column_split2.setSeq(1L);
				column_splitList.add(column_split2);
				Column_split column_split3 = new Column_split();
				column_split3.setSplit_type(CharSplitType.ZhiDingFuHao.getCode());
				column_split3.setSplit_sep(StringUtil.string2Unicode("-"));
				column_split3.setCol_type("varchar(10)");
				column_split3.setCol_name("day");
				column_split3.setCol_zhname("日");
				column_split3.setSeq(2L);
				column_splitList.add(column_split3);
				columnCleanBean.setColumn_split_list(column_splitList);
				columnCleanBeans.add(columnCleanBean);
				collectTableColumnBean.setColumnCleanBeanList(columnCleanBeans);
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 仅生成非定长文件、选择同一目的地、选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 * 清洗：清洗顺序默认、选择列拆分、单表的cc_rec_start_date字段按偏移量-拆分成三个字段年、月、日
	 */
	@Test
	public void test43() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		for (CollectTableColumnBean collectTableColumnBean : collectTableBean.getCollectTableColumnBeanList()) {
			if ("cc_rec_start_date".equals(collectTableColumnBean.getColumn_name())) {
				List<ColumnCleanBean> columnCleanBeans = new ArrayList<>();
				ColumnCleanBean columnCleanBean = new ColumnCleanBean();
				//列拆分
				columnCleanBean.setClean_type(CleanType.ZiFuChaiFen.getCode());
				List<Column_split> column_splitList = new ArrayList<>();
				Column_split column_split1 = new Column_split();
				column_split1.setSplit_type(CharSplitType.PianYiLiang.getCode());
				//这个偏移量是下标从0开始，前包后不包
				column_split1.setCol_offset("0,4");
				column_split1.setCol_type("varchar(10)");
				column_split1.setCol_name("year");
				column_split1.setCol_zhname("年");
				column_splitList.add(column_split1);
				Column_split column_split2 = new Column_split();
				column_split2.setSplit_type(CharSplitType.PianYiLiang.getCode());
				column_split2.setCol_offset("5,7");
				column_split2.setCol_type("varchar(10)");
				column_split2.setCol_name("month");
				column_split2.setCol_zhname("月");
				column_splitList.add(column_split2);
				Column_split column_split3 = new Column_split();
				column_split3.setSplit_type(CharSplitType.PianYiLiang.getCode());
				column_split3.setCol_offset("8,10");
				column_split3.setCol_type("varchar(10)");
				column_split3.setCol_name("day");
				column_split3.setCol_zhname("日");
				column_splitList.add(column_split3);
				columnCleanBean.setColumn_split_list(column_splitList);
				columnCleanBeans.add(columnCleanBean);
				collectTableColumnBean.setColumnCleanBeanList(columnCleanBeans);
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 仅生成非定长文件、选择同一目的地、选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 * 清洗：清洗顺序默认、选择列合并、将表的cc_call_center_sk、cc_call_center_id、cc_rec_end_date字段合并成zzz_column1
	 * cc_open_date_sk、cc_name合并成zzz_column2
	 */
	@Test
	public void test44() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		List<Column_merge> column_mergeList = new ArrayList<>();
		Column_merge column_merge1 = new Column_merge();
		column_merge1.setOld_name("cc_call_center_sk,cc_call_center_id,cc_rec_end_date");
		column_merge1.setCol_name("zzz_column1");
		column_merge1.setCol_zhname("合并的列1");
		column_merge1.setCol_type("varchar(512)");
		column_mergeList.add(column_merge1);
		Column_merge column_merge2 = new Column_merge();
		column_merge2.setOld_name("cc_open_date_sk,cc_name");
		column_merge2.setCol_name("zzz_column2");
		column_merge2.setCol_zhname("合并的列2");
		column_merge2.setCol_type("varchar(512)");
		column_mergeList.add(column_merge2);
		collectTableBean.setColumn_merge_list(column_mergeList);
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表、不计算md5、不并行抽取、不添加sql过滤、不是自定义写sql并行抽取、
	 * 仅生成非定长文件、选择同一目的地、选择linux换行符、列分隔符使用`@^、字符集选择GBK、全量采集
	 * 清洗：设置清洗顺序：时间转换、字符替换、字符补齐、字符拆分、字符trim、码值转换
	 * 选择列合并、将表的cc_call_center_sk、cc_call_center_id、cc_rec_end_date字段合并成zzz_column1
	 * cc_open_date_sk、cc_name、cc_rec_start_date合并成zzz_column2
	 * XXX 列合并的值不受清洗影响，列合并不能合并拆分的字段，列拆分可能会因为清洗顺序导致异常，这一块待完善
	 */
	@Test
	public void test45() {
		//获取多表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		List<Column_merge> column_mergeList = new ArrayList<>();
		Column_merge column_merge1 = new Column_merge();
		column_merge1.setOld_name("cc_call_center_sk,cc_call_center_id,cc_rec_end_date");
		column_merge1.setCol_name("zzz_column1");
		column_merge1.setCol_zhname("合并的列1");
		column_merge1.setCol_type("varchar(512)");
		column_mergeList.add(column_merge1);
		Column_merge column_merge2 = new Column_merge();
		column_merge2.setOld_name("cc_open_date_sk,cc_name,cc_rec_start_date");
		column_merge2.setCol_name("zzz_column2");
		column_merge2.setCol_zhname("合并的列2");
		column_merge2.setCol_type("varchar(512)");
		column_mergeList.add(column_merge2);
		collectTableBean.setColumn_merge_list(column_mergeList);
		for (CollectTableColumnBean collectTableColumnBean : collectTableBean.getCollectTableColumnBeanList()) {
			if ("cc_rec_start_date".equals(collectTableColumnBean.getColumn_name())) {
				//设置清洗顺序
				JSONObject object = new JSONObject();
				//时间转换
				object.put(CleanType.ShiJianZhuanHuan.getCode(), 1);
				//字符替换
				object.put(CleanType.ZiFuTiHuan.getCode(), 2);
				//字符补齐
				object.put(CleanType.ZiFuBuQi.getCode(), 3);
				//字符拆分
				object.put(CleanType.ZiFuChaiFen.getCode(), 4);
				//字符trim
				object.put(CleanType.ZiFuTrim.getCode(), 5);
				//码值转换
				object.put(CleanType.MaZhiZhuanHuan.getCode(), 6);
				collectTableColumnBean.setTc_or(object.toJSONString());
				List<ColumnCleanBean> columnCleanBeans = new ArrayList<>();
				//-----------------------------------------设置列拆分
				ColumnCleanBean columnCleanBean = new ColumnCleanBean();
				columnCleanBean.setClean_type(CleanType.ZiFuChaiFen.getCode());
				List<Column_split> column_splitList = new ArrayList<>();
				Column_split column_split1 = new Column_split();
				column_split1.setSplit_type(CharSplitType.PianYiLiang.getCode());
				//这个偏移量是下标从0开始，前包后不包
				column_split1.setCol_offset("0,4");
				column_split1.setCol_type("varchar(10)");
				column_split1.setCol_name("year");
				column_split1.setCol_zhname("年");
				column_splitList.add(column_split1);
				Column_split column_split2 = new Column_split();
				column_split2.setSplit_type(CharSplitType.PianYiLiang.getCode());
				column_split2.setCol_offset("4,6");
				column_split2.setCol_type("varchar(10)");
				column_split2.setCol_name("month");
				column_split2.setCol_zhname("月");
				column_splitList.add(column_split2);
				Column_split column_split3 = new Column_split();
				column_split3.setSplit_type(CharSplitType.PianYiLiang.getCode());
				column_split3.setCol_offset("6,10");
				column_split3.setCol_type("varchar(10)");
				column_split3.setCol_name("day");
				column_split3.setCol_zhname("日");
				column_splitList.add(column_split3);
				columnCleanBean.setColumn_split_list(column_splitList);
				columnCleanBeans.add(columnCleanBean);
				//-----------------------------------------设置日期转换
				ColumnCleanBean columnCleanBean2 = new ColumnCleanBean();
				//时间转换
				columnCleanBean2.setClean_type(CleanType.ShiJianZhuanHuan.getCode());
				//原各式
				columnCleanBean2.setOld_format("yyyy-mm-dd");
				//转换后的格式
				columnCleanBean2.setConvert_format("yyyymmdd");
				columnCleanBeans.add(columnCleanBean2);
				//-----------------------------------------设置字符替换
				ColumnCleanBean columnCleanBean3 = new ColumnCleanBean();
				//字符替换
				columnCleanBean3.setClean_type(CleanType.ZiFuTiHuan.getCode());
				//原字段
				columnCleanBean3.setField(StringUtil.string2Unicode("1998"));
				//替换字段
				columnCleanBean3.setReplace_feild(StringUtil.string2Unicode("2018"));
				columnCleanBeans.add(columnCleanBean3);
				//-----------------------------------------设置字符trim
				ColumnCleanBean columnCleanBean4 = new ColumnCleanBean();
				//去空
				columnCleanBean4.setClean_type(CleanType.ZiFuTrim.getCode());
				columnCleanBeans.add(columnCleanBean4);
				//-----------------------------------------设置字符补齐
				ColumnCleanBean columnCleanBean5 = new ColumnCleanBean();
				//字符补齐
				columnCleanBean5.setClean_type(CleanType.ZiFuBuQi.getCode());
				//后补齐
				columnCleanBean5.setFilling_type(FillingType.HouBuQi.getCode());
				//补齐长度
				columnCleanBean5.setFilling_length(10L);
				//补齐字符
				columnCleanBean5.setCharacter_filling(StringUtil.string2Unicode(" "));
				columnCleanBeans.add(columnCleanBean5);
				collectTableColumnBean.setColumnCleanBeanList(columnCleanBeans);
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表 增量采集，填写新增、删除、更新的sql
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK
	 */
	@Test
	public void test46() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		//设置增量卸数
		collectTableBean.setUnload_type(UnloadType.ZengLiangXieShu.getCode());
		//设置增量采集的sql
		JSONObject object = new JSONObject();
		object.put("insert", "select * from call_center where cc_market_manager = 'Julius Durham'");
		object.put("delete", "select cc_call_center_sk from call_center where cc_call_center_id = 'AAAAAAAABAAAAAAA'");
		object.put("update", "select * from call_center where cc_market_manager = 'Gary Colburn'");
		collectTableBean.setSql(object.toJSONString());
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表 增量采集，只有新增和删除 不填更新的sql
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK
	 */
	@Test
	public void test47() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		//设置增量卸数
		collectTableBean.setUnload_type(UnloadType.ZengLiangXieShu.getCode());
		//设置增量采集的sql
		JSONObject object = new JSONObject();
		object.put("insert", "select * from call_center where cc_market_manager = 'Julius Durham'");
		object.put("delete", "select cc_call_center_sk from call_center where cc_call_center_id = 'AAAAAAAABAAAAAAA'");
		object.put("update", "");
		collectTableBean.setSql(object.toJSONString());
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表 增量采集，只有更新和删除 不填新增的sql
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK
	 */
	@Test
	public void test48() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		//设置增量卸数
		collectTableBean.setUnload_type(UnloadType.ZengLiangXieShu.getCode());
		//设置增量采集的sql
		JSONObject object = new JSONObject();
		object.put("insert", "");
		object.put("delete", "select cc_call_center_sk from call_center where cc_call_center_id = 'AAAAAAAABAAAAAAA'");
		object.put("update", "select * from call_center where cc_market_manager = 'Gary Colburn'");
		collectTableBean.setSql(object.toJSONString());
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表 增量采集，只有新增和更新 不填删除的sql
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK
	 */
	@Test
	public void test49() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		//设置增量卸数
		collectTableBean.setUnload_type(UnloadType.ZengLiangXieShu.getCode());
		//设置增量采集的sql
		JSONObject object = new JSONObject();
		object.put("insert", "select * from call_center where cc_market_manager = 'Julius Durham'");
		object.put("delete", "");
		object.put("update", "select * from call_center where cc_market_manager = 'Gary Colburn'");
		collectTableBean.setSql(object.toJSONString());
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表 增量采集，只有新增的sql
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK
	 */
	@Test
	public void test50() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		//设置增量卸数
		collectTableBean.setUnload_type(UnloadType.ZengLiangXieShu.getCode());
		//设置增量采集的sql
		JSONObject object = new JSONObject();
		object.put("insert", "select * from call_center where cc_market_manager = 'Julius Durham'");
		object.put("delete", "");
		object.put("update", "");
		collectTableBean.setSql(object.toJSONString());
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表 增量采集，只有更新的sql
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK
	 */
	@Test
	public void test51() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		//设置增量卸数
		collectTableBean.setUnload_type(UnloadType.ZengLiangXieShu.getCode());
		//设置增量采集的sql
		JSONObject object = new JSONObject();
		object.put("insert", "");
		object.put("delete", "");
		object.put("update", "select * from call_center where cc_market_manager = 'Gary Colburn'");
		collectTableBean.setSql(object.toJSONString());
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表 增量采集，只有删除的sql
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK
	 */
	@Test
	public void test52() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		//设置增量卸数
		collectTableBean.setUnload_type(UnloadType.ZengLiangXieShu.getCode());
		//设置增量采集的sql
		JSONObject object = new JSONObject();
		object.put("insert", "");
		object.put("delete", "select cc_call_center_sk from call_center where cc_call_center_id = 'AAAAAAAABAAAAAAA'");
		object.put("update", "");
		collectTableBean.setSql(object.toJSONString());
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 测试数据库抽取选择单表 增量采集，不填写sql
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK
	 */
	@Test
	public void test53() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		//设置增量卸数
		collectTableBean.setUnload_type(UnloadType.ZengLiangXieShu.getCode());
		//设置增量采集的sql
		JSONObject object = new JSONObject();
		object.put("insert", "");
		object.put("delete", "");
		object.put("update", "");
		collectTableBean.setSql(object.toJSONString());
		assertThat("执行失败", executeJdbcCollect(sourceDataConfBean), is(false));
	}

	/**
	 * 测试数据库抽取选择单表 增量采集，填写新增、删除、更新的sql，不选择主键
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK
	 */
	@Test
	public void test54() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = sourceDataConfBean.getCollectTableBeanArray().get(0);
		//设置增量卸数
		collectTableBean.setUnload_type(UnloadType.ZengLiangXieShu.getCode());
		//设置增量采集的sql
		JSONObject object = new JSONObject();
		object.put("insert", "select * from call_center where cc_market_manager = 'Julius Durham'");
		object.put("delete", "select cc_call_center_sk from call_center where cc_call_center_id = 'AAAAAAAABAAAAAAA'");
		object.put("update", "select * from call_center where cc_market_manager = 'Gary Colburn'");
		collectTableBean.setSql(object.toJSONString());
		for (CollectTableColumnBean collectTableColumnBean : collectTableBean.getCollectTableColumnBeanList()) {
			collectTableColumnBean.setIs_primary_key(IsFlag.Fou.getCode());
		}
		assertThat("执行失败", executeJdbcCollect(sourceDataConfBean), is(false));
	}

	/**
	 * 测试数据库抽取选择多表，只有表call_center选择增量采集，填写新增、删除、更新的sql
	 * 选择linux换行符、列分隔符使用`@^、字符集选择GBK
	 * 其他表默认配置执行
	 */
	@Test
	public void test55() {
		//获取单表的页面配置基本信息
		SourceDataConfBean sourceDataConfBean = getMultiTableSourceDataConfBean();
		for(CollectTableBean collectTableBean : sourceDataConfBean.getCollectTableBeanArray()){
			if("call_center".equals(collectTableBean.getTable_name())){
				//设置增量卸数
				collectTableBean.setUnload_type(UnloadType.ZengLiangXieShu.getCode());
				//设置增量采集的sql
				JSONObject object = new JSONObject();
				object.put("insert", "select * from call_center where cc_market_manager = 'Julius Durham'");
				object.put("delete", "select cc_call_center_sk from call_center where cc_call_center_id = 'AAAAAAAABAAAAAAA'");
				object.put("update", "select * from call_center where cc_market_manager = 'Gary Colburn'");
				collectTableBean.setSql(object.toJSONString());
			}
		}
		assertThat("执行成功", executeJdbcCollect(sourceDataConfBean), is(true));
	}

	/**
	 * 执行数据库采集的方法
	 *
	 * @param sourceDataConfBean 任务配置信息
	 * @return 成功失败值
	 */
	private boolean executeJdbcCollect(SourceDataConfBean sourceDataConfBean) {
		ExecutorService executor = null;
		try {
			//初始化当前任务需要保存的文件的根目录
			String[] paths = {Constant.JOBINFOPATH, Constant.DICTIONARY};
			FileUtil.initPath(sourceDataConfBean.getDatabase_id(), paths);
			//1.获取json数组转成File_source的集合
			List<CollectTableBean> collectTableBeanList = sourceDataConfBean.getCollectTableBeanArray();
			//此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
			executor = Executors.newFixedThreadPool(JobConstant.AVAILABLEPROCESSORS);
			List<Future<JobStatusInfo>> list = new ArrayList<>();
			//2.校验对象的值是否正确
			for (CollectTableBean collectTableBean : collectTableBeanList) {
				List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
				for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
					collectTableBean.setEtlDate(DateUtil.getSysDate());
					collectTableBean.setSelectFileFormat(data_extraction_def.getDbfile_format());
					//为了确保多个线程之间的值不互相干涉，复制对象的值。
					SourceDataConfBean sourceDataConfBean1 = JSONObject.parseObject(
							JSONObject.toJSONString(sourceDataConfBean), SourceDataConfBean.class);
					CollectTableBean collectTableBean1 = JSONObject.parseObject(
							JSONObject.toJSONString(collectTableBean), CollectTableBean.class);
					//多线程执行
					DataBaseJobImpl fileCollectJob = new DataBaseJobImpl(sourceDataConfBean1, collectTableBean1);
					Future<JobStatusInfo> submit = executor.submit(fileCollectJob);
					list.add(submit);
				}
			}
			//3.打印每个线程执行情况
			JobStatusInfoUtil.printJobStatusInfo(list);
		} catch (Exception e) {
			return false;
		} finally {
			if (executor != null)
				executor.shutdown();
		}
		return true;
	}

//	/**
//	 * 执行数据库采集的主程序
//	 *
//	 * @param job_id      任务id
//	 * @param collectType 采集类型
//	 */
//	private boolean execute(SourceDataConfBean sourceDataConfBean) {
//		if (AgentType.WenJianXiTong.getCode().equals(collectType)) {
//			return executeFileCollect(taskInfo);
//		} else if (AgentType.ShuJuKu.getCode().equals(collectType)) {
//			return executeJdbcCollect(taskInfo);
//		} else if (AgentType.FTP.getCode().equals(collectType)) {
//			return executeFtpCollect(taskInfo);
//		} else if (AgentType.DBWenJian.getCode().equals(collectType)) {
//			return executeDbFileCollect(taskInfo);
//		} else if (AgentType.DuiXiang.getCode().equals(collectType)) {
//			return executeObjectFileCollect(taskInfo);
//		} else {
//			throw new AppSystemException("采集类型不正确");
//		}
//
//	}

//	/**
//	 * 执行ftp采集的方法
//	 *
//	 * @param taskInfo 任务配置信息
//	 * @return 成功失败值
//	 */
//	private boolean executeFtpCollect(String taskInfo) {
//		try {
//			//对配置信息解压缩并反序列化为Ftp_collect对象
//			Ftp_collect ftp_collect = JSONObject.parseObject(taskInfo, Ftp_collect.class);
//			//1.获取参数，校验对象的值是否正确
//			//此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
//			ExecutorService pool = Executors.newFixedThreadPool(1);
//			JobInterface job = new FtpCollectJobImpl(ftp_collect);
//			Future<JobStatusInfo> statusInfoFuture = pool.submit(job);
//			JobStatusInfo jobStatusInfo = statusInfoFuture.get();
//			System.out.println("作业执行情况" + jobStatusInfo.toString());
//		} catch (Exception e) {
//			return false;
//		}
//		return true;
//	}
//
//	/**
//	 * 执行非结构化文件采集的方法
//	 *
//	 * @param taskInfo 任务配置信息
//	 * @return 成功失败值
//	 */
//	private boolean executeFileCollect(String taskInfo) {
//		FileCollectParamBean fileCollectParamBean = JSONObject.parseObject(
//				taskInfo, FileCollectParamBean.class);
//		ThreadPoolExecutor executor = null;
//		try {
//			//初始化当前任务需要保存的文件的根目录
//			String[] paths = {Constant.MAPDBPATH, Constant.JOBINFOPATH, Constant.FILEUNLOADFOLDER};
//			FileUtil.initPath(fileCollectParamBean.getFcs_id(), paths);
//			//1.获取json数组转成File_source的集合
//			List<File_source> fileSourceList = fileCollectParamBean.getFile_sourceList();
//			//使用多线程按照文件夹采集，核心线程5个，最大线程10个，队列里面50个，超出会报错
//			executor = new ThreadPoolExecutor(5, 10,
//					5L, TimeUnit.MINUTES, new LinkedBlockingQueue<>(50));
//			List<Future<JobStatusInfo>> list = new ArrayList<>();
//			//2.校验对象的值是否正确
//			for (File_source file_source : fileSourceList) {
//				//为了确保两个线程之间的值不互相干涉，复制对象的值。
//				FileCollectParamBean fileCollectParamBean1 = JSONObject.parseObject(
//						JSONObject.toJSONString(fileCollectParamBean), FileCollectParamBean.class);
//				FileCollectJobImpl fileCollectJob = new FileCollectJobImpl(fileCollectParamBean1, file_source);
//				Future<JobStatusInfo> submit = executor.submit(fileCollectJob);
//				list.add(submit);
//			}
//			//3.打印每个线程执行情况
//			JobStatusInfoUtil.printJobStatusInfo(list);
//		} catch (Exception e) {
//			return false;
//		} finally {
//			if (executor != null)
//				executor.shutdown();
//		}
//		return true;
//	}
//
//	/**
//	 * 执行Db文件采集的方法
//	 *
//	 * @param taskInfo 任务配置信息
//	 * @return 成功失败值
//	 */
//	private boolean executeDbFileCollect(String taskInfo) {
//		return true;
//	}
//
//	/**
//	 * 执行对象采集的方法
//	 *
//	 * @param taskInfo 任务配置信息
//	 * @return 成功失败值
//	 */
//	private boolean executeObjectFileCollect(String taskInfo) {
//		return true;
//	}

	/**
	 * 获取数据库抽取只选择单表的页面配置文件
	 *
	 * @return 数据库抽取源数据读取配置信息
	 */
	private SourceDataConfBean getSingleTableSourceDataConfBean() {
		String taskInfo = FileUtil.readFile2String(new File(agentInitConfig.
				getString("singleTableSourceDataConfPath")));
		//对配置信息解压缩并反序列化为SourceDataConfBean对象
		SourceDataConfBean sourceDataConfBean = JSONObject.parseObject(taskInfo, SourceDataConfBean.class);
		return replaceTestInfoConf(sourceDataConfBean);
	}

	/**
	 * 获取数据库抽取选择多表的配置文件
	 *
	 * @return 数据库抽取源数据读取配置信息
	 */
	private SourceDataConfBean getMultiTableSourceDataConfBean() {
		String taskInfo = FileUtil.readFile2String(new File(agentInitConfig.
				getString("multiTableSourceDataConfPath")));
		//对配置信息解压缩并反序列化为SourceDataConfBean对象
		SourceDataConfBean sourceDataConfBean = JSONObject.parseObject(taskInfo, SourceDataConfBean.class);
		return replaceTestInfoConf(sourceDataConfBean);
	}

	/**
	 * 替换掉要采集的源数据库的信息
	 *
	 * @param sourceDataConfBean 数据库抽取源数据读取配置信息
	 * @return 数据库抽取源数据读取配置信息
	 */
	private SourceDataConfBean replaceTestInfoConf(SourceDataConfBean sourceDataConfBean) {
		JSONObject object = JSONObject.parseObject(agentInitConfig.getString("source_database_info"));
		sourceDataConfBean.setDatabase_drive(object.getString("database_drive"));
		sourceDataConfBean.setDatabase_type(object.getString("database_type"));
		sourceDataConfBean.setJdbc_url(object.getString("jdbc_url"));
		sourceDataConfBean.setDatabase_port(object.getString("database_port"));
		sourceDataConfBean.setDatabase_pad(object.getString("database_pad"));
		sourceDataConfBean.setUser_name(object.getString("user_name"));
		sourceDataConfBean.setDatabase_name(object.getString("database_name"));
		sourceDataConfBean.setDatabase_ip(object.getString("database_ip"));
		List<CollectTableBean> collectTableBeanArray = sourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				data_extraction_def.setPlane_url(agentInitConfig.getString("landing_directory"));
			}
		}
		return sourceDataConfBean;
	}
}
