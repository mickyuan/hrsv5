package hrds.main;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.DataStoreConfBean;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.DataFileJobImpl;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.utils.ParallerTestUtil;
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
public class CommandDbFileTest extends WebBaseTestCase {
	private CommandJdbcTest commandJdbcTest = new CommandJdbcTest();

	/**
	 * 测试db文件单表采集使用配置文件初始化默认配置
	 * 不选择转存、文件格式为jdbc抽取的非定长、linux换行符、`@^分隔符、GBK字符集
	 * 存储目的地使用配置文件中的配置、拉链存储、存储方式为替换、保留天数为1天
	 */
	@Test
	public void test1() {
		//先执行数据库抽取
		commandJdbcTest.test1();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean singleTableSourceDataConfBean = getSingleTableSourceDataConfBean();
		assertThat("执行成功", executeDbCollect(singleTableSourceDataConfBean), is(true));
	}

	/**
	 * 测试db文件多表采集使用配置文件初始化默认配置
	 * 不选择转存、文件格式为jdbc抽取的非定长、linux换行符、`@^分隔符、GBK字符集
	 * 存储目的地使用配置文件中的配置、拉链存储、存储方式为替换、数据保留天数为1天
	 */
	@Test
	public void test2() {
		//先执行数据库抽取
		commandJdbcTest.test2();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean multiTableSourceDataConfBean = getMultiTableSourceDataConfBean();
		assertThat("执行成功", executeDbCollect(multiTableSourceDataConfBean), is(true));
	}

	/**
	 * 测试db文件单表采集使用配置文件初始化默认配置
	 * 不选择转存、文件格式为jdbc抽取的Parquet、GBK字符集
	 * 存储目的地使用配置文件中的配置、拉链存储、存储方式为替换、保留天数为1天
	 */
	@Test
	public void test3() {
		//先执行数据库抽取
		commandJdbcTest.test15();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean singleTableSourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = singleTableSourceDataConfBean.getCollectTableBeanArray().get(0);
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
			data_extraction_def.setDbfile_format(FileFormat.PARQUET.getCode());
		}
		assertThat("执行成功", executeDbCollect(singleTableSourceDataConfBean), is(true));
	}

	/**
	 * 测试db文件多表采集使用配置文件初始化默认配置
	 * 不选择转存、文件格式为jdbc抽取的Parquet、GBK字符集
	 * 存储目的地使用配置文件中的配置、拉链存储、存储方式为替换、数据保留天数为1天
	 */
	@Test
	public void test4() {
		//先执行数据库抽取
		commandJdbcTest.test16();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean multiTableSourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanList = multiTableSourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanList) {
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				data_extraction_def.setDbfile_format(FileFormat.PARQUET.getCode());
			}
		}
		assertThat("执行成功", executeDbCollect(multiTableSourceDataConfBean), is(true));
	}

	/**
	 * 测试db文件单表采集使用配置文件初始化默认配置
	 * 不选择转存、文件格式为jdbc抽取的定长、linux换行符、`@^分隔符、GBK字符集
	 * 存储目的地使用配置文件中的配置、拉链存储、存储方式为替换、保留天数为1天
	 */
	@Test
	public void test5() {
		//先执行数据库抽取
		commandJdbcTest.test13();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean singleTableSourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = singleTableSourceDataConfBean.getCollectTableBeanArray().get(0);
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
			data_extraction_def.setDbfile_format(FileFormat.DingChang.getCode());
		}
		assertThat("执行成功", executeDbCollect(singleTableSourceDataConfBean), is(true));
	}

	/**
	 * 测试db文件多表采集使用配置文件初始化默认配置
	 * 不选择转存、文件格式为jdbc抽取的定长、linux换行符、没有列分隔符、GBK字符集
	 * 存储目的地使用配置文件中的配置、拉链存储、存储方式为替换、数据保留天数为1天
	 */
	@Test
	public void test6() {
		//先执行数据库抽取
		commandJdbcTest.test14();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean multiTableSourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanList = multiTableSourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanList) {
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				data_extraction_def.setDbfile_format(FileFormat.DingChang.getCode());
				data_extraction_def.setDatabase_separatorr("");
			}
		}
		assertThat("执行成功", executeDbCollect(multiTableSourceDataConfBean), is(true));
	}

	/**
	 * 测试db文件单表采集使用配置文件初始化默认配置
	 * 不选择转存、文件格式为jdbc抽取的Csv、GBK字符集
	 * 存储目的地使用配置文件中的配置、拉链存储、存储方式为替换、保留天数为1天
	 */
	@Test
	public void test7() {
		//先执行数据库抽取
		commandJdbcTest.test21();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean singleTableSourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = singleTableSourceDataConfBean.getCollectTableBeanArray().get(0);
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
			data_extraction_def.setDbfile_format(FileFormat.CSV.getCode());
		}
		assertThat("执行成功", executeDbCollect(singleTableSourceDataConfBean), is(true));
	}

	/**
	 * 测试db文件多表采集使用配置文件初始化默认配置
	 * 不选择转存、文件格式为jdbc抽取的Csv、GBK字符集
	 * 存储目的地使用配置文件中的配置、拉链存储、存储方式为替换、数据保留天数为1天
	 */
	@Test
	public void test8() {
		//先执行数据库抽取
		commandJdbcTest.test22();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean multiTableSourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = multiTableSourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				data_extraction_def.setDbfile_format(FileFormat.CSV.getCode());
			}
		}
		assertThat("执行成功", executeDbCollect(multiTableSourceDataConfBean), is(true));
	}

	/**
	 * 测试db文件单表采集使用配置文件初始化默认配置
	 * 不选择转存、文件格式为jdbc抽取的SequenceFile、GBK字符集
	 * 存储目的地使用配置文件中的配置、拉链存储、存储方式为替换、保留天数为1天
	 */
	@Test
	public void test9() {
		//先执行数据库抽取
		commandJdbcTest.test19();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean singleTableSourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = singleTableSourceDataConfBean.getCollectTableBeanArray().get(0);
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
			data_extraction_def.setDbfile_format(FileFormat.SEQUENCEFILE.getCode());
		}
		assertThat("执行成功", executeDbCollect(singleTableSourceDataConfBean), is(true));
	}

	/**
	 * 测试db文件多表采集使用配置文件初始化默认配置
	 * 不选择转存、文件格式为jdbc抽取的SequenceFile、GBK字符集
	 * 存储目的地使用配置文件中的配置、拉链存储、存储方式为替换、数据保留天数为1天
	 */
	@Test
	public void test10() {
		//先执行数据库抽取
		commandJdbcTest.test20();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean multiTableSourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = multiTableSourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				data_extraction_def.setDbfile_format(FileFormat.SEQUENCEFILE.getCode());
			}
		}
		assertThat("执行成功", executeDbCollect(multiTableSourceDataConfBean), is(true));
	}

	/**
	 * 测试db文件单表采集使用配置文件初始化默认配置
	 * 不选择转存、文件格式为jdbc抽取的Orc、GBK字符集
	 * 存储目的地使用配置文件中的配置、拉链存储、存储方式为替换、保留天数为1天
	 */
	@Test
	public void test11() {
		//先执行数据库抽取
		commandJdbcTest.test17();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean singleTableSourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = singleTableSourceDataConfBean.getCollectTableBeanArray().get(0);
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
			data_extraction_def.setDbfile_format(FileFormat.ORC.getCode());
		}
		assertThat("执行成功", executeDbCollect(singleTableSourceDataConfBean), is(true));
	}

	/**
	 * 测试db文件多表采集使用配置文件初始化默认配置
	 * 不选择转存、文件格式为jdbc抽取的Orc、GBK字符集
	 * 存储目的地使用配置文件中的配置、拉链存储、存储方式为替换、数据保留天数为1天
	 */
	@Test
	public void test12() {
		//先执行数据库抽取
		commandJdbcTest.test18();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean multiTableSourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = multiTableSourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				data_extraction_def.setDbfile_format(FileFormat.ORC.getCode());
			}
		}
		assertThat("执行成功", executeDbCollect(multiTableSourceDataConfBean), is(true));
	}

	/**
	 * 测试db文件单表采集使用配置文件初始化默认配置
	 * 不选择转存、文件格式为jdbc抽取的Csv、GBK字符集、包含表头
	 * 存储目的地使用配置文件中的配置、拉链存储、存储方式为替换、保留天数为1天
	 */
	@Test
	public void test13() {
		//先执行数据库抽取
		commandJdbcTest.test56();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean singleTableSourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = singleTableSourceDataConfBean.getCollectTableBeanArray().get(0);
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
			data_extraction_def.setDbfile_format(FileFormat.CSV.getCode());
			data_extraction_def.setIs_header(IsFlag.Shi.getCode());
		}
		assertThat("执行成功", executeDbCollect(singleTableSourceDataConfBean), is(true));
	}

	/**
	 * 测试db文件多表采集使用配置文件初始化默认配置
	 * 不选择转存、文件格式为jdbc抽取的Csv、GBK字符集、包含表头
	 * 存储目的地使用配置文件中的配置、拉链存储、存储方式为替换、数据保留天数为1天
	 */
	@Test
	public void test14() {
		//先执行数据库抽取
		commandJdbcTest.test57();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean multiTableSourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = multiTableSourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				data_extraction_def.setDbfile_format(FileFormat.CSV.getCode());
				data_extraction_def.setIs_header(IsFlag.Shi.getCode());
			}
		}
		assertThat("执行成功", executeDbCollect(multiTableSourceDataConfBean), is(true));
	}

	/**
	 * 测试db文件单表采集使用配置文件初始化默认配置
	 * 不选择转存、文件格式为jdbc抽取的非定长、linux换行符、`@^分隔符、GBK字符集、包含表头
	 * 存储目的地使用配置文件中的配置、拉链存储、存储方式为替换、保留天数为1天
	 */
	@Test
	public void test15() {
		//先执行数据库抽取
		commandJdbcTest.test58();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean singleTableSourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = singleTableSourceDataConfBean.getCollectTableBeanArray().get(0);
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
			data_extraction_def.setIs_header(IsFlag.Shi.getCode());
		}
		assertThat("执行成功", executeDbCollect(singleTableSourceDataConfBean), is(true));
	}

	/**
	 * 测试db文件多表采集使用配置文件初始化默认配置
	 * 不选择转存、文件格式为jdbc抽取的非定长、linux换行符、`@^分隔符、GBK字符集、包含表头
	 * 存储目的地使用配置文件中的配置、拉链存储、存储方式为替换、数据保留天数为1天
	 */
	@Test
	public void test16() {
		//先执行数据库抽取
		commandJdbcTest.test59();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean multiTableSourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanArray = multiTableSourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanArray) {
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				data_extraction_def.setIs_header(IsFlag.Shi.getCode());
			}
		}
		assertThat("执行成功", executeDbCollect(multiTableSourceDataConfBean), is(true));
	}

	/**
	 * 测试db文件单表采集使用配置文件初始化默认配置
	 * 不选择转存、文件格式为jdbc抽取的定长、linux换行符、`@^分隔符、GBK字符集
	 * 存储目的地使用配置文件中的配置、拉链存储、存储方式为替换、保留天数为1天
	 */
	@Test
	public void test17() {
		//先执行数据库抽取
		commandJdbcTest.test60();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean singleTableSourceDataConfBean = getSingleTableSourceDataConfBean();
		CollectTableBean collectTableBean = singleTableSourceDataConfBean.getCollectTableBeanArray().get(0);
		List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
		for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
			data_extraction_def.setDbfile_format(FileFormat.DingChang.getCode());
			data_extraction_def.setIs_header(IsFlag.Shi.getCode());
		}
		assertThat("执行成功", executeDbCollect(singleTableSourceDataConfBean), is(true));
	}

	/**
	 * 测试db文件多表采集使用配置文件初始化默认配置
	 * 不选择转存、文件格式为jdbc抽取的定长、linux换行符、没有列分隔符、GBK字符集
	 * 存储目的地使用配置文件中的配置、拉链存储、存储方式为替换、数据保留天数为1天
	 */
	@Test
	public void test18() {
		//先执行数据库抽取
		commandJdbcTest.test61();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean multiTableSourceDataConfBean = getMultiTableSourceDataConfBean();
		List<CollectTableBean> collectTableBeanList = multiTableSourceDataConfBean.getCollectTableBeanArray();
		for (CollectTableBean collectTableBean : collectTableBeanList) {
			List<Data_extraction_def> data_extraction_def_list = collectTableBean.getData_extraction_def_list();
			for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
				data_extraction_def.setDbfile_format(FileFormat.DingChang.getCode());
				data_extraction_def.setDatabase_separatorr("");
				data_extraction_def.setIs_header(IsFlag.Shi.getCode());
			}
		}
		assertThat("执行成功", executeDbCollect(multiTableSourceDataConfBean), is(true));
	}

	/**
	 * 执行db文件采集的方法
	 *
	 * @param sourceDataConfBean 任务配置信息
	 * @return 成功失败值
	 */
	private boolean executeDbCollect(SourceDataConfBean sourceDataConfBean) {
		ExecutorService executor = null;
		try {
			//1.获取json数组转成File_source的集合
			List<CollectTableBean> collectTableBeanList = sourceDataConfBean.getCollectTableBeanArray();
			executor = Executors.newFixedThreadPool(JobConstant.AVAILABLEPROCESSORS);
			List<Future<JobStatusInfo>> list = new ArrayList<>();
			//2.校验对象的值是否正确
			for (CollectTableBean collectTableBean : collectTableBeanList) {
				//设置跑批日期
				collectTableBean.setEtlDate(DateUtil.getSysDate());
				//为了确保多个线程之间的值不互相干涉，复制对象的值。
				SourceDataConfBean sourceDataConfBean1 = JSONObject.parseObject(
						JSONObject.toJSONString(sourceDataConfBean), SourceDataConfBean.class);
				DataFileJobImpl fileCollectJob = new DataFileJobImpl(sourceDataConfBean1, collectTableBean);
				Future<JobStatusInfo> submit = executor.submit(fileCollectJob);
				list.add(submit);
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

	/**
	 * 获取数据库抽取只选择单表的页面配置文件
	 *
	 * @return 数据库抽取源数据读取配置信息
	 */
	private SourceDataConfBean getSingleTableSourceDataConfBean() {
		String taskInfo = FileUtil.readFile2String(new File(ParallerTestUtil.TESTINITCONFIG.
				getString("singleDbTableSourceDataConfPath")));
		//对配置信息解压缩并反序列化为SourceDataConfBean对象
		return replaceTestInfoConf(JSONObject.parseObject(taskInfo, SourceDataConfBean.class));
	}

	/**
	 * 获取数据库抽取选择多表的配置文件
	 *
	 * @return 数据库抽取源数据读取配置信息
	 */
	private SourceDataConfBean getMultiTableSourceDataConfBean() {
		String taskInfo = FileUtil.readFile2String(new File(ParallerTestUtil.TESTINITCONFIG.
				getString("multiDbTableSourceDataConfPath")));
		//对配置信息解压缩并反序列化为SourceDataConfBean对象
		return replaceTestInfoConf(JSONObject.parseObject(taskInfo, SourceDataConfBean.class));
	}

	/**
	 * 替换掉采集进库的存储层配置
	 *
	 * @param sourceDataConfBean 数据库抽取源数据读取配置信息
	 * @return 数据库抽取源数据读取配置信息
	 */
	private SourceDataConfBean replaceTestInfoConf(SourceDataConfBean sourceDataConfBean) {
		List<DataStoreConfBean> dataStoreConfBean = JSONArray.parseArray(ParallerTestUtil.TESTINITCONFIG.
				getString("dataStoreConfBean"), DataStoreConfBean.class);
		for (CollectTableBean collectTableBean : sourceDataConfBean.getCollectTableBeanArray()) {
			collectTableBean.setDataStoreConfBean(dataStoreConfBean);
		}
		return sourceDataConfBean;
	}

}
