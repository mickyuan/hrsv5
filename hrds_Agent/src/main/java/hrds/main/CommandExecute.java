package hrds.main;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.FileCollectParamBean;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.DataBaseJobImpl;
import hrds.agent.job.biz.core.DataFileJobImpl;
import hrds.agent.job.biz.core.FileCollectJobImpl;
import hrds.agent.job.biz.core.JdbcDirectJobImpl;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.CollectType;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.entity.File_source;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@DocClass(desc = "作业调度数据库采集、数据库抽取、db文件采集程序入口", author = "zxz", createdate = "2020/1/3 10:38")
public class CommandExecute {
	//打印日志
	private static final Logger log = LogManager.getLogger();

	/**
	 * @param args 主程序入口，获取参数调用采集后台
	 *             参数1：任务ID
	 *             参数2：表名
	 *             参数3：采集类型
	 *             参数4：跑批日期
	 *             参数5：文件格式或存储目的地名称
	 *             参数6-N：sql占位符参数 condition=value
	 */
	public static void main(String[] args) {
		if (args == null || args.length < 5) {
			log.info("请按照规定的格式传入参数，必须参数不能为空");
			log.info("必须参数：参数1：任务ID；参数2：表名；参数3：采集类型；参数4：跑批日期；" +
				"参数5：文件格式或存储目的地名称");
			log.info("非必须参数：参数6-N：sql占位符参数 condition=value");
			System.exit(-1);
		}
		String taskId = args[0];
		String tableName = args[1];
		String collectType = args[2];
		String etlDate = args[3];
		StringBuilder sqlParam = new StringBuilder();
		//获取sql占位符的参数
		if (args.length > 5) {
			for (int i = 5; i < args.length; i++) {
				sqlParam.append(args[i]).append(Constant.SQLDELIMITER);
			}
			sqlParam.delete(sqlParam.length() - Constant.SQLDELIMITER.length(), sqlParam.length());
		}
		try {
			//获取任务信息
			String taskInfo = FileUtil.readFile2String(new File(JobConstant.MESSAGEFILE + taskId));
			//采集类型是非结构化采集
			if (AgentType.WenJianXiTong.getCode().equals(collectType)) {
				startFileCollectJob(taskId, taskInfo);
			}
			//采集类型不是非结构化采集
			else {
				//对配置信息解压缩并反序列化为SourceDataConfBean对象
				SourceDataConfBean sourceDataConfBean = JSONObject.parseObject(taskInfo, SourceDataConfBean.class);
				//1.获取json数组转成File_source的集合
				List<CollectTableBean> collectTableBeanList = sourceDataConfBean.getCollectTableBeanArray();
				//获取需要采集的表对象
				CollectTableBean collectTableBean = getCollectTableBean(collectTableBeanList, tableName);
				//设置跑批日期
				collectTableBean.setEtlDate(etlDate);
				//设置sql占位符参数
				collectTableBean.setSqlParam(sqlParam.toString());
				//判断采集类型，根据采集类型调用对应的方法
				if (AgentType.ShuJuKu.getCode().equals(collectType)) {
					if (CollectType.ShuJuKuCaiJi.getCode().equals(sourceDataConfBean.getCollect_type())) {
						//数据库直连采集
						startJdbcToDatabase(sourceDataConfBean, collectTableBean);
					} else if (CollectType.ShuJuKuChouShu.getCode().equals(sourceDataConfBean.getCollect_type())) {
						String selectFileFormat = args[4];
						//判断存储层配置是否有作业调度传参的存储层配置
						if (!isSupportSelectFormat(selectFileFormat, collectTableBean.getData_extraction_def_list())) {
							throw new AppSystemException("请检查作业调度的参数5(文件格式)和数据库卸数指定的文件格式是否一致");
						}
						//根据作业调度指定的文件格式，本次作业只跑指定卸数的文件格式
						collectTableBean.setSelectFileFormat(selectFileFormat);
						startJdbcToFile(sourceDataConfBean, collectTableBean);
					} else {
						throw new AppSystemException("不支持的数据库采集类型");
					}
				} else if (AgentType.DBWenJian.getCode().equals(collectType)) {
					//TODO 根据作业指定存储目的地名称，本次作业只进数指定存储目的地
					startDbFileCollect(sourceDataConfBean, collectTableBean);
				} else {
					throw new AppSystemException("不支持的采集类型");
				}
			}
		} catch (Exception e) {
			log.error("执行采集失败!", e);
			System.exit(-1);
		}
	}

	private static boolean isSupportSelectFormat(String selectFileFormat,
	                                             List<Data_extraction_def> data_extraction_def_list) {
		for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
			//只执行作业调度指定的文件格式
			if (selectFileFormat.equals(data_extraction_def.getDbfile_format())) {
				return true;
			}
		}
		return false;
	}

	private static void startJdbcToDatabase(SourceDataConfBean sourceDataConfBean, CollectTableBean
		collectTableBean) {
		ExecutorService executor = null;
		try {
			executor = Executors.newFixedThreadPool(1);
			List<Future<JobStatusInfo>> list = new ArrayList<>();
			//2.校验对象的值是否正确，调用数据库直连采集的主程序
			JdbcDirectJobImpl jdbcDirectJob = new JdbcDirectJobImpl(sourceDataConfBean, collectTableBean);
			Future<JobStatusInfo> submit = executor.submit(jdbcDirectJob);
			list.add(submit);
			//3.打印每个线程执行情况
			JobStatusInfoUtil.printJobStatusInfo(list);
		} catch (Exception e) {
			throw new AppSystemException("数据库直连采集" + collectTableBean.getTable_name() + "失败", e);
		} finally {
			if (executor != null)
				executor.shutdown();
		}
	}


	private static void startDbFileCollect(SourceDataConfBean sourceDataConfBean, CollectTableBean collectTableBean) {
		ExecutorService executor = null;
		try {
			executor = Executors.newFixedThreadPool(1);
			List<Future<JobStatusInfo>> list = new ArrayList<>();
			//2.校验对象的值是否正确
			DataFileJobImpl fileCollectJob = new DataFileJobImpl(sourceDataConfBean, collectTableBean);
			Future<JobStatusInfo> submit = executor.submit(fileCollectJob);
			list.add(submit);
			//3.打印每个线程执行情况
			JobStatusInfoUtil.printJobStatusInfo(list);
		} catch (Exception e) {
			throw new AppSystemException("数据库抽取表" + collectTableBean.getTable_name() + "失败", e);
		} finally {
			if (executor != null)
				executor.shutdown();
		}
	}

	private static void startJdbcToFile(SourceDataConfBean sourceDataConfBean, CollectTableBean collectTableBean) {
		ExecutorService executor = null;
		try {
			//初始化当前任务需要保存的文件的根目录
			String[] paths = {JobConstant.DICTIONARY + sourceDataConfBean.getDatabase_id()};
			FileUtil.initPath(paths);
			//此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
			//TODO Runtime.getRuntime().availableProcessors()此处不能用这个,因为可能同时又多个数据库采集同时进行
			executor = Executors.newFixedThreadPool(1);
			List<Future<JobStatusInfo>> list = new ArrayList<>();
			//2.校验对象的值是否正确
			DataBaseJobImpl fileCollectJob = new DataBaseJobImpl(sourceDataConfBean, collectTableBean);
			Future<JobStatusInfo> submit = executor.submit(fileCollectJob);
			list.add(submit);
			//3.打印每个线程执行情况
			JobStatusInfoUtil.printJobStatusInfo(list);
		} catch (Exception e) {
			throw new AppSystemException("数据库抽取表" + collectTableBean.getTable_name() + "失败", e);
		} finally {
			if (executor != null)
				executor.shutdown();
		}
	}

	/**
	 * 开始文件采集作业
	 *
	 * @param taskId 采集任务id
	 */
	private static void startFileCollectJob(String taskId, String taskInfo) {
		//文件采集需要的参数实体bean
		FileCollectParamBean fileCollectParamBean = JSONObject.parseObject(taskInfo, FileCollectParamBean.class);
		//1.将页面传递过来的压缩信息解压写文件
		ExecutorService executor = null;
		try {
			//2.初始化当前任务需要保存的文件的根目录
			String[] paths = {Constant.MAPDBPATH + File.separator + fileCollectParamBean.getFcs_id(),
				Constant.JOBINFOPATH + File.separator + fileCollectParamBean.getFcs_id()
				, Constant.FILEUNLOADFOLDER + File.separator + fileCollectParamBean.getFcs_id()};
			FileUtil.initPath(paths);
			//3.获取json数组转成File_source的集合
			List<File_source> fileSourceList = fileCollectParamBean.getFile_sourceList();
			//此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
			executor = Executors.newFixedThreadPool(JobConstant.AVAILABLEPROCESSORS);
			List<Future<JobStatusInfo>> list = new ArrayList<>();
			//4.多线程执行文件采集
			for (File_source file_source : fileSourceList) {
				//为了确保两个线程之间的值不互相干涉，复制对象的值。
				FileCollectParamBean fileCollectParamBean1 = JSONObject.parseObject(
					JSONObject.toJSONString(fileCollectParamBean), FileCollectParamBean.class);
				//多线程执行
				FileCollectJobImpl fileCollectJob = new FileCollectJobImpl(fileCollectParamBean1, file_source);
				Future<JobStatusInfo> submit = executor.submit(fileCollectJob);
				list.add(submit);
			}
			//5.打印每个线程执行情况
			log.info(list);
		} catch (Exception e) {
			throw new AppSystemException("执行文件采集失败!", e);
		} finally {
			if (executor != null)
				executor.shutdown();
		}
	}

	private static CollectTableBean getCollectTableBean(List<CollectTableBean> collectTableBeanList, String
		tableName) {
		for (CollectTableBean collectTableBean : collectTableBeanList) {
			if (tableName.equals(collectTableBean.getTable_name())) {
				return collectTableBean;
			}
		}
		throw new AppSystemException("根据作业参数传递的表名在任务中查询不到对应的表");
	}
}
