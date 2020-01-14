package hrds.main;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.agent.job.biz.core.DataBaseJobImpl;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.utils.Constant;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@DocClass(desc = "数据库采集测试用例", createdate = "2020/01/07 09:48", author = "zxz")
public class CommandJdbcTest {
	//前提条件，前端配置信息生成的文件，清先检查当前程序运行的根目录下的messageFile是否有对应的配置文件

	/**
	 * 测试卸数csv文件batch进数据库
	 */
	@Test
	public void test1() {
		assertThat("执行测试卸数csv文件成功", execute("1002234428"), is(true));
	}

	/**
	 * 测试卸数parquet文件batch进数据库
	 */
	@Test
	public void test2() {
		assertThat("执行测试卸数parquet文件成功", execute("1002234429"), is(true));
	}

	/**
	 * 测试卸数Orc文件batch进数据库
	 */
	@Test
	public void test3() {
		assertThat("执行测试卸数Orc文件成功", execute("1002234430"), is(true));
	}

	/**
	 * 测试卸数SequenceFile文件batch进数据库
	 */
	@Test
	public void test4() {
		assertThat("执行测试卸数SequenceFile文件成功", execute("1002234431"), is(true));
	}

	/**
	 * 测试卸数定长文件batch进数据库
	 */
	@Test
	public void test5() {
		assertThat("执行测试卸数定长文件成功", execute("1002234432"), is(true));
	}

	/**
	 * 测试卸数非定长文件batch进数据库
	 */
	@Test
	public void test6() {
		assertThat("执行测试卸数非定长文件成功", execute("1002234433"), is(true));
	}

	/**
	 * 测试卸数csv文件有部署客户端，进hive库
	 */
	@Test
	public void test7() {
		assertThat("执行测试卸数csv文件进数成功", execute("1002234438"), is(true));
	}

	/**
	 * 执行数据库采集的主程序
	 *
	 * @param database_id 任务id
	 */
	private boolean execute(String database_id) {
		String taskInfo;
		try {
			taskInfo = FileUtil.readFile2String(new File(Constant.MESSAGEFILE
					+ database_id));
		} catch (IOException e) {
			return false;
		}
		//对配置信息解压缩并反序列化为SourceDataConfBean对象
		SourceDataConfBean sourceDataConfBean = JSONObject.parseObject(taskInfo, SourceDataConfBean.class);
		ExecutorService executor = null;
		try {
			//初始化当前任务需要保存的文件的根目录
			String[] paths = {Constant.JOBINFOPATH, Constant.JDBCUNLOADFOLDER};
			FileUtil.initPath(sourceDataConfBean.getDatabase_id(), paths);
			//1.获取json数组转成File_source的集合
			List<CollectTableBean> collectTableBeanList = sourceDataConfBean.getCollectTableBeanArray();
			//此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
			//TODO Runtime.getRuntime().availableProcessors()此处不能用这个,因为可能同时又多个数据库采集同时进行
			executor = Executors.newFixedThreadPool(1);
			List<Future<JobStatusInfo>> list = new ArrayList<>();
			//2.校验对象的值是否正确
			for (CollectTableBean collectTableBean : collectTableBeanList) {
				//为了确保多个线程之间的值不互相干涉，复制对象的值。
				SourceDataConfBean sourceDataConfBean1 = JSONObject.parseObject(
						JSONObject.toJSONString(sourceDataConfBean), SourceDataConfBean.class);
				//XXX 多线程执行
				//TODO 使用公共方法校验所有传入参数的对象的值的合法性
				//TODO Agent这个参数该怎么接，是统一封装成工厂需要的参数吗？
				//XXX 程序运行存储信息。
				DataBaseJobImpl fileCollectJob = new DataBaseJobImpl(sourceDataConfBean1, collectTableBean);
				//TODO 这个状态是不是可以在这里
				Future<JobStatusInfo> submit = executor.submit(fileCollectJob);
				list.add(submit);
			}
			//3.打印每个线程执行情况
			JobStatusInfoUtil.printJobStatusInfo(list);
		} catch (RejectedExecutionException e) {
			return false;
		} finally {
			if (executor != null)
				executor.shutdown();
		}
		return true;
	}
}
