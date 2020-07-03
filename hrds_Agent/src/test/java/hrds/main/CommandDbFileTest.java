package hrds.main;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.DataFileJobImpl;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
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
	 * 测试最基本的单表db文件采集
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
	 * 测试最基本的多表db文件采集
	 */
	@Test
	public void test2() {
		//先执行数据库抽取
		commandJdbcTest.test2();
		//获取单表的db文件采集的页面配置信息
		SourceDataConfBean singleTableSourceDataConfBean = getMultiTableSourceDataConfBean();
		assertThat("执行成功", executeDbCollect(singleTableSourceDataConfBean), is(true));
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
		String taskInfo = FileUtil.readFile2String(new File(agentInitConfig.
				getString("singleDbTableSourceDataConfPath")));
		//对配置信息解压缩并反序列化为SourceDataConfBean对象
		return JSONObject.parseObject(taskInfo, SourceDataConfBean.class);
	}

	/**
	 * 获取数据库抽取选择多表的配置文件
	 *
	 * @return 数据库抽取源数据读取配置信息
	 */
	private SourceDataConfBean getMultiTableSourceDataConfBean() {
		String taskInfo = FileUtil.readFile2String(new File(agentInitConfig.
				getString("multiDbTableSourceDataConfPath")));
		//对配置信息解压缩并反序列化为SourceDataConfBean对象
		return JSONObject.parseObject(taskInfo, SourceDataConfBean.class);
	}

}
