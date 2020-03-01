package hrds.agent.job.biz.core.dbstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.SystemUtil;
import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.dbstage.service.ReadFileToDataBase;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.CollectType;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.Store_type;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.hadoop_helper.HdfsOperator;
import hrds.commons.utils.Constant;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@DocClass(desc = "数据库直连采集数据上传阶段", author = "WangZhengcheng")
public class DBUploadStageImpl extends AbstractJobStage {
	private final static Logger LOGGER = LoggerFactory.getLogger(DBUploadStageImpl.class);
	//卸数到本地的文件绝对路径
//	private final String[] localFiles;
	//数据采集表对应的存储的所有信息
	private CollectTableBean collectTableBean;
	//数据库采集表对应的meta信息
//	private TableBean tableBean;

	public DBUploadStageImpl(CollectTableBean collectTableBean) {
		this.collectTableBean = collectTableBean;
//		this.localFiles = localFiles;
//		this.tableBean = tableBean;
	}

	@Method(desc = "数据库直连采集数据上传阶段处理逻辑，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "" +
			"1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间" +
			"2、调用方法，进行文件上传，文件数组和上传目录由构造器传入")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		LOGGER.info("------------------数据库直连采集上传阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, collectTableBean.getTable_id(),
				StageConstant.UPLOAD.getCode());
		//此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。
		//TODO Runtime.getRuntime().availableProcessors()此处不能用这个,因为可能同时有多个数据库采集同时进行
		//这里多个文件，使用多线程读取文件，batch进外部数据库。
		ExecutorService executor = null;
		try {
			List<DataStoreConfBean> dataStoreConfBeanList = collectTableBean.getDataStoreConfBean();
			for (DataStoreConfBean dataStoreConfBean : dataStoreConfBeanList) {
				long count = 0;
				//根据存储类型上传到目的地
				if (Store_type.DATABASE.getCode().equals(dataStoreConfBean.getStore_type())) {
					//数据库类型
					if (IsFlag.Shi.getCode().equals(dataStoreConfBean.getIs_hadoopclient())) {
						//有hadoop客户端，通过直接上传hdfs，映射外部表的方式进hive
						execHDFSShell(dataStoreConfBean, stageParamInfo.getFileArr());
					} else if (IsFlag.Fou.getCode().equals(dataStoreConfBean.getIs_hadoopclient())) {
						//没有hadoop客户端
						executor = Executors.newFixedThreadPool(5);
						exeBatch(dataStoreConfBean, executor, count, stageParamInfo.getFileArr(),
								stageParamInfo.getTableBean());
					} else {
						throw new AppSystemException("错误的是否标识");
					}
				} /*else if (Store_type.HIVE.getCode().equals(dataStoreConfBean.getStore_type())) {
					if (IsFlag.Shi.getCode().equals(dataStoreConfBean.getIs_hadoopclient())) {
						//有hadoop客户端，通过直接上传hdfs，映射外部表的方式进hive
						execHDFSShell(dataStoreConfBean, stageParamInfo.getFileArr());
					} else if (IsFlag.Fou.getCode().equals(dataStoreConfBean.getIs_hadoopclient())) {
						//没有hadoop客户端
						executor = Executors.newFixedThreadPool(5);
						exeBatch(dataStoreConfBean, executor, count, stageParamInfo.getFileArr(),
								stageParamInfo.getTableBean());
					} else {
						throw new AppSystemException("错误的是否标识");
					}
				}*/ else if (Store_type.HBASE.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else if (Store_type.SOLR.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else if (Store_type.ElasticSearch.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else if (Store_type.MONGODB.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else {
					//TODO 上面的待补充。
					throw new AppSystemException("不支持的存储类型");
				}
			}
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------数据库直连采集上传阶段成功------------------");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error("数据库直连采集上传阶段失败：", e);
		} finally {
			if (executor != null)
				executor.shutdown();
		}
		//结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, collectTableBean
				, CollectType.ShuJuKuCaiJi.getCode());
		return stageParamInfo;
	}

	/**
	 * 上传卸数的文件到hdfs
	 */
	private void execHDFSShell(DataStoreConfBean dataStoreConfBean, String[] localFiles) throws Exception {
		//STORECONFIGPATH上传hdfs需要读取的配置文件顶层目录
		String hdfsPath = getUploadHdfsPath(collectTableBean);
		//TODO 需要加一个key为hadoop_user_name的键值对，作为普通属性
		//TODO 需要加一个key为platform的键值对，作为普通属性
		Map<String, String> data_store_connect_attr = dataStoreConfBean.getData_store_connect_attr();
		//TODO 这里需要在部署agent的时候要将目的地属于配置文件的属性部署到这边指定的目录下。
		try (HdfsOperator operator = new HdfsOperator(FileNameUtils.normalize(Constant.STORECONFIGPATH
				+ dataStoreConfBean.getDsl_name() + File.separator, true)
				, data_store_connect_attr.get("platform"), data_store_connect_attr.get("hadoop_user_name"))) {
			//创建hdfs表的文件夹
			if (!operator.exists(hdfsPath)) {
				if (!operator.mkdir(hdfsPath)) {
					throw new AppSystemException("创建hdfs文件夹" + hdfsPath + "失败");
				}
			} else {
				if (!operator.deletePath(hdfsPath)) {
					throw new AppSystemException("删除hdfs文件夹" + hdfsPath + "失败");
				}
				if (!operator.mkdir(hdfsPath)) {
					throw new AppSystemException("创建hdfs文件夹" + hdfsPath + "失败");
				}
			}
			if (SystemUtil.OS_NAME.toLowerCase().contains("windows")) {
				//windows机器上使用api上传hdfs
				for (String localFilePath : localFiles) {
					LOGGER.info("开始上传文件到hdfs");
					if (!operator.upLoad(localFilePath, hdfsPath, true)) {
						throw new AppSystemException("上传文件" + localFilePath + "到hdfs文件夹" + hdfsPath + "失败");
					}
					LOGGER.info("上传文件" + localFilePath + "到hdfs文件夹" + hdfsPath + "结束");
				}
			} else {
				//这里只支持windows和linux，其他机器不支持，linux下使用命令上传hdfs
				StringBuilder fsSql = new StringBuilder();
				//拼接认证
				//TODO 有认证需要加认证文件key必须为keytab_file，需要加认证用户，key必须为keytab_user
				if (!StringUtil.isEmpty(dataStoreConfBean.getData_store_layer_file().get("keytab_file"))) {
					fsSql.append("kinit -k -t ").append(dataStoreConfBean.getData_store_layer_file().get("keytab_file"))
							.append(" ").append(data_store_connect_attr.get("keytab_user"))
							.append(" ").append(System.lineSeparator());
				}
				//拼接上传hdfs命令
				for (String localFilePath : localFiles) {
					fsSql.append("hadoop fs -put -f ").append(localFilePath).append(" ").append(hdfsPath)
							.append(" ").append(System.lineSeparator());
				}
				String hdfsShellFile = FileNameUtils.normalize(Constant.HDFSSHELLFILE + collectTableBean
						.getHbase_name() + ".sh", true);
				//写脚本文件
				FileUtil.createFile(hdfsShellFile, fsSql.toString());
				String command = "sh " + hdfsShellFile;
				LOGGER.info("开始运行(HDFS上传)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + command);
				final CommandLine cmdLine = new CommandLine("sh");
				cmdLine.addArgument(hdfsShellFile);
				DefaultExecutor executor = new DefaultExecutor();
				ExecuteWatchdog watchdog = new ExecuteWatchdog(Integer.MAX_VALUE);
				executor.setWatchdog(watchdog);
				executor.execute(cmdLine);
				LOGGER.info("上传文件到" + hdfsPath + "结束");
			}
		}
	}

	/**
	 * 使用batch方式进数
	 */
	private void exeBatch(DataStoreConfBean dataStoreConfBean, ExecutorService executor, long count,
	                      String[] localFiles, TableBean tableBean) throws Exception {
		List<Future<Long>> list = new ArrayList<>();
		for (String fileAbsolutePath : localFiles) {
			ReadFileToDataBase readFileToDataBase = new ReadFileToDataBase(fileAbsolutePath, tableBean,
					collectTableBean, dataStoreConfBean);
			//TODO 这个状态是不是可以在这里
			Future<Long> submit = executor.submit(readFileToDataBase);
			list.add(submit);
		}
		for (Future<Long> future : list) {
			count += future.get();
		}
		if (count < 0) {
			throw new AppSystemException("数据Batch提交到库" + dataStoreConfBean.getDsl_name() + "异常");
		}
		LOGGER.info("数据成功进入库" + dataStoreConfBean.getDsl_name() + "下的表" + collectTableBean.getHbase_name()
				+ ",总计进数" + count + "条");
	}

	/**
	 * 获取上传到hdfs的文件夹路径
	 */
	public static String getUploadHdfsPath(CollectTableBean collectTableBean) {
		return FileNameUtils.normalize(JobConstant.PREFIX + File.separator + collectTableBean.getDatabase_id()
				+ File.separator + collectTableBean.getHbase_name() + File.separator, true);
	}

	@Override
	public int getStageCode() {
		return StageConstant.UPLOAD.getCode();
	}
}
