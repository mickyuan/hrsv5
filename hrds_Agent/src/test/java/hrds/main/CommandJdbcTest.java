package hrds.main;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.FileCollectParamBean;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.agent.job.biz.core.DataBaseJobImpl;
import hrds.agent.job.biz.core.FileCollectJobImpl;
import hrds.agent.job.biz.core.FtpCollectJobImpl;
import hrds.agent.job.biz.core.JobInterface;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.CollectType;
import hrds.commons.entity.File_source;
import hrds.commons.entity.Ftp_collect;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

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
		assertThat("执行测试卸数csv文件成功", execute("1002234428",
				CollectType.ShuJuKuCaiJi.getCode()), is(true));
	}

	/**
	 * 测试卸数parquet文件batch进数据库
	 */
	@Test
	public void test2() {
		assertThat("执行测试卸数parquet文件成功", execute("1002234429",
				CollectType.ShuJuKuCaiJi.getCode()), is(true));
	}

	/**
	 * 测试卸数Orc文件batch进数据库
	 */
	@Test
	public void test3() {
		assertThat("执行测试卸数Orc文件成功", execute("1002234430",
				CollectType.ShuJuKuCaiJi.getCode()), is(true));
	}

	/**
	 * 测试卸数SequenceFile文件batch进数据库
	 */
	@Test
	public void test4() {
		assertThat("执行测试卸数SequenceFile文件成功", execute("1002234431",
				CollectType.ShuJuKuCaiJi.getCode()), is(true));
	}

	/**
	 * 测试卸数定长文件batch进数据库
	 */
	@Test
	public void test5() {
		assertThat("执行测试卸数定长文件成功", execute("1002234432",
				CollectType.ShuJuKuCaiJi.getCode()), is(true));
	}

	/**
	 * 测试卸数非定长文件batch进数据库
	 */
	@Test
	public void test6() {
		assertThat("执行测试卸数非定长文件成功", execute("1002234433",
				CollectType.ShuJuKuCaiJi.getCode()), is(true));
	}

	/**
	 * 测试卸数csv文件有部署客户端，进hive库
	 */
	@Test
	public void test7() {
		assertThat("执行测试卸数csv文件进数成功", execute("1002234438",
				CollectType.ShuJuKuCaiJi.getCode()), is(true));
	}

	/**
	 * 测试卸数parquet文件有部署客户端，进hive库
	 */
	@Test
	public void test8() {
		assertThat("执行测试卸数parquet文件成功", execute("1002234439",
				CollectType.ShuJuKuCaiJi.getCode()), is(true));
	}

	/**
	 * 测试卸数Orc文件有部署客户端，进hive库
	 */
	@Test
	public void test9() {
		assertThat("执行测试卸数Orc文件成功", execute("1002234440",
				CollectType.ShuJuKuCaiJi.getCode()), is(true));
	}

	/**
	 * 测试卸数SequenceFile文件有部署客户端，进hive库
	 */
	@Test
	public void test10() {
		assertThat("执行测试卸数SequenceFile文件成功", execute("1002234441",
				CollectType.ShuJuKuCaiJi.getCode()), is(true));
	}

	/**
	 * 测试卸数非定长文件有部署客户端，进hive库
	 */
	@Test
	public void test11() {
		assertThat("执行测试卸数非定长文件成功", execute("1002234443",
				CollectType.ShuJuKuCaiJi.getCode()), is(true));
	}

	@Test
	public void test12() {
		assertThat("执行测试卸数非定长文件成功", execute("1000402881",
				CollectType.ShuJuKuCaiJi.getCode()), is(true));
	}

	@Test
	public void test13() {
		assertThat("执行测试文件采集成功", execute("1000230238",
				CollectType.WenJianCaiJi.getCode()), is(true));
	}

	@Test
	public void test14() {
		assertThat("执行测试文件采集成功", execute("1000230240",
				CollectType.WenJianCaiJi.getCode()), is(true));
	}


	/**
	 * 执行数据库采集的主程序
	 *
	 * @param job_id      任务id
	 * @param collectType 采集类型
	 */
	private boolean execute(String job_id, String collectType) {
		String taskInfo = FileUtil.readFile2String(new File(Constant.MESSAGEFILE + job_id));
		if (CollectType.WenJianCaiJi.getCode().equals(collectType)) {
			return executeFileCollect(taskInfo);
		} else if (CollectType.ShuJuKuCaiJi.getCode().equals(collectType)) {
			return executeJdbcCollect(taskInfo);
		} else if (CollectType.FtpCaiJi.getCode().equals(collectType)) {
			return executeFtpCollect(taskInfo);
		} else if (CollectType.DBWenJianCaiJi.getCode().equals(collectType)) {
			return executeDbFileCollect(taskInfo);
		} else if (CollectType.DuiXiangWenJianCaiJi.getCode().equals(collectType)) {
			return executeObjectFileCollect(taskInfo);
		} else {
			throw new AppSystemException("采集类型不正确");
		}

	}

	/**
	 * 执行数据库采集的方法
	 *
	 * @param taskInfo 任务配置信息
	 * @return 成功失败值
	 */
	private boolean executeJdbcCollect(String taskInfo) {
		//对配置信息解压缩并反序列化为SourceDataConfBean对象
		SourceDataConfBean sourceDataConfBean = JSONObject.parseObject(taskInfo, SourceDataConfBean.class);
		ExecutorService executor = null;
		try {
			//初始化当前任务需要保存的文件的根目录
			String[] paths = {Constant.JOBINFOPATH, Constant.DICTIONARY};
			FileUtil.initPath(sourceDataConfBean.getDatabase_id(), paths);
			//1.获取json数组转成File_source的集合
			List<CollectTableBean> collectTableBeanList = sourceDataConfBean.getCollectTableBeanArray();
			//此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
			//TODO Runtime.getRuntime().availableProcessors()此处不能用这个,因为可能同时又多个数据库采集同时进行
			executor = Executors.newFixedThreadPool(5);
			List<Future<JobStatusInfo>> list = new ArrayList<>();
			//2.校验对象的值是否正确
			for (CollectTableBean collectTableBean : collectTableBeanList) {
				//XXX 测试用例跑批日期就使用当前日期
				collectTableBean.setEtlDate(DateUtil.getSysDate());
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

	/**
	 * 执行ftp采集的方法
	 *
	 * @param taskInfo 任务配置信息
	 * @return 成功失败值
	 */
	private boolean executeFtpCollect(String taskInfo) {
		try {
			//对配置信息解压缩并反序列化为Ftp_collect对象
			Ftp_collect ftp_collect = JSONObject.parseObject(taskInfo, Ftp_collect.class);
			//1.获取参数，校验对象的值是否正确
			//TODO 使用公共方法校验ftp_collect对象的值得合法性
			//此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
			ExecutorService pool = Executors.newFixedThreadPool(1);
			JobInterface job = new FtpCollectJobImpl(ftp_collect);
			Future<JobStatusInfo> statusInfoFuture = pool.submit(job);
			JobStatusInfo jobStatusInfo = statusInfoFuture.get();
			System.out.println("作业执行情况" + jobStatusInfo.toString());
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 执行非结构化文件采集的方法
	 *
	 * @param taskInfo 任务配置信息
	 * @return 成功失败值
	 */
	private boolean executeFileCollect(String taskInfo) {
		FileCollectParamBean fileCollectParamBean = JSONObject.parseObject(
				taskInfo, FileCollectParamBean.class);
		ThreadPoolExecutor executor = null;
		try {
			//初始化当前任务需要保存的文件的根目录
			String[] paths = {Constant.MAPDBPATH, Constant.JOBINFOPATH, Constant.FILEUNLOADFOLDER};
			FileUtil.initPath(fileCollectParamBean.getFcs_id(), paths);
			//1.获取json数组转成File_source的集合
			List<File_source> fileSourceList = fileCollectParamBean.getFile_sourceList();
			//使用多线程按照文件夹采集，核心线程5个，最大线程10个，队列里面50个，超出会报错
			executor = new ThreadPoolExecutor(5, 10,
					5L, TimeUnit.MINUTES, new LinkedBlockingQueue<>(50));
			List<Future<JobStatusInfo>> list = new ArrayList<>();
			//2.校验对象的值是否正确
			for (File_source file_source : fileSourceList) {
				//为了确保两个线程之间的值不互相干涉，复制对象的值。
				FileCollectParamBean fileCollectParamBean1 = JSONObject.parseObject(
						JSONObject.toJSONString(fileCollectParamBean), FileCollectParamBean.class);
				//XXX 多线程执行
				//TODO 使用公共方法校验所有传入参数的对象的值的合法性
				//TODO Agent这个参数该怎么接，是统一封装成工厂需要的参数吗？
				//XXX 程序运行存储信息。
				FileCollectJobImpl fileCollectJob = new FileCollectJobImpl(fileCollectParamBean1, file_source);
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

	/**
	 * 执行Db文件采集的方法
	 *
	 * @param taskInfo 任务配置信息
	 * @return 成功失败值
	 */
	private boolean executeDbFileCollect(String taskInfo) {
		return true;
	}

	/**
	 * 执行对象采集的方法
	 *
	 * @param taskInfo 任务配置信息
	 * @return 成功失败值
	 */
	private boolean executeObjectFileCollect(String taskInfo) {
		return true;
	}
}
