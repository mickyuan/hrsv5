package hrds.agent.job.biz.core.objectstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.SystemUtil;
import hrds.agent.job.biz.bean.DataStoreConfBean;
import hrds.agent.job.biz.bean.ObjectTableBean;
import hrds.agent.job.biz.bean.StageParamInfo;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.objectstage.service.ObjectProcessInterface;
import hrds.agent.job.biz.core.objectstage.service.impl.HiveTableProcessImpl;
import hrds.agent.job.biz.core.objectstage.service.impl.MppTableProcessImpl;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.Store_type;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.hadoop_helper.HdfsOperator;
import hrds.commons.utils.Constant;
import hrds.commons.utils.StorageTypeKey;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;

@DocClass(desc = "半结构化对象采集数据上传阶段", author = "zxz")
public class ObjectUploadStageImpl extends AbstractJobStage {
	//打印日志
	private static final Logger LOGGER = LogManager.getLogger();
	//卸数到本地的文件绝对路径
	//数据采集表对应的存储的所有信息
	private final ObjectTableBean objectTableBean;

	public ObjectUploadStageImpl(ObjectTableBean objectTableBean) {
		this.objectTableBean = objectTableBean;
	}

	@Method(desc = "半结构化对象采集数据上传阶段处理逻辑，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "" +
			"1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间" +
			"2、调用方法，进行文件上传，文件数组和上传目录由构造器传入")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		long startTime = System.currentTimeMillis();
		LOGGER.info("------------------表" + objectTableBean.getEn_name()
				+ "半结构化对象采集上传阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, objectTableBean.getOcs_id(),
				StageConstant.UPLOAD.getCode());
		boolean flag = true;
		try {
			ObjectProcessInterface processInterface = null;
			try {
				List<DataStoreConfBean> dataStoreConfBeanList = objectTableBean.getDataStoreConfBean();
				for (DataStoreConfBean dataStoreConfBean : dataStoreConfBeanList) {
					//这边做一个接口多实现，目前只实现传统数据库的增量更新接口
					if (Store_type.DATABASE.getCode().equals(dataStoreConfBean.getStore_type())) {
						//关系型数据库
						processInterface = new MppTableProcessImpl(stageParamInfo.getTableBean(),
								objectTableBean, dataStoreConfBean);
					} else if (Store_type.HIVE.getCode().equals(dataStoreConfBean.getStore_type())) {
						//默认为
						//关系型数据库
						processInterface = new HiveTableProcessImpl(stageParamInfo.getTableBean(),
								objectTableBean);
					} else {
						throw new AppSystemException("半结构化对象采集目前不支持入" + dataStoreConfBean.getDsl_name());
					}
					//执行入库或者转存
					for (String readFile : stageParamInfo.getFileArr()) {
						processInterface.parserFileToTable(readFile);
					}
					//如果是hive或者Hbase，将转换之后的数据上传到hdfs
					if (Store_type.HIVE.getCode().equals(dataStoreConfBean.getStore_type())
							|| Store_type.HBASE.getCode().equals(dataStoreConfBean.getStore_type()) && flag) {
						String unloadFileAbsolutePath = FileNameUtils.normalize(Constant.DBFILEUNLOADFOLDER +
								objectTableBean.getOdc_id() + File.separator + objectTableBean.getEn_name() +
								File.separator + objectTableBean.getEtlDate() + File.separator +
								objectTableBean.getEn_name() + ".dat", true);
						//直接上传hdfs，映射外部表的方式进hive
						execHDFSShell(dataStoreConfBean, unloadFileAbsolutePath, objectTableBean);
						flag = false;
					}
				}
			} catch (Exception e) {
				throw new AppSystemException("表" + objectTableBean.getEn_name()
						+ "db文件采集增量上传失败", e);
			} finally {
				if (processInterface != null) {
					processInterface.close();
				}
			}
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------表" + objectTableBean.getEn_name()
					+ "半结构化对象采集上传阶段成功------------------执行时间为："
					+ (System.currentTimeMillis() - startTime) / 1000 + "，秒");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error("半结构对象" + objectTableBean.getEn_name() + "上传阶段失败：", e);
		}
		//结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, objectTableBean
				, AgentType.DuiXiang.getCode());
		return stageParamInfo;
	}

	/**
	 * 上传卸数的文件到hdfs
	 */
	private void execHDFSShell(DataStoreConfBean dataStoreConfBean, String localFilePath,
							   ObjectTableBean objectTableBean) throws Exception {
		//STORECONFIGPATH上传hdfs需要读取的配置文件顶层目录
		String hdfsPath = FileNameUtils.normalize(JobConstant.PREFIX + File.separator
				+ objectTableBean.getOdc_id() + File.separator + objectTableBean.getEn_name()
				+ File.separator, true);
		//TODO 需要加一个key为hadoop_user_name的键值对，作为普通属性
		//TODO 需要加一个key为platform的键值对，作为普通属性
		Map<String, String> data_store_connect_attr = dataStoreConfBean.getData_store_connect_attr();
		//TODO 这里需要在部署agent的时候要将目的地属于配置文件的属性部署到这边指定的目录下。
		try (HdfsOperator operator = new HdfsOperator(FileNameUtils.normalize(Constant.STORECONFIGPATH
				+ dataStoreConfBean.getDsl_name() + File.separator, true)
				, data_store_connect_attr.get(StorageTypeKey.platform),
				data_store_connect_attr.get(StorageTypeKey.prncipal_name),
				data_store_connect_attr.get(StorageTypeKey.hadoop_user_name))) {
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
				LOGGER.info("开始上传文件到hdfs");
				if (!operator.upLoad(localFilePath, hdfsPath, true)) {
					throw new AppSystemException("上传文件" + localFilePath + "到hdfs文件夹" + hdfsPath + "失败");
				}
				LOGGER.info("上传文件" + localFilePath + "到hdfs文件夹" + hdfsPath + "结束");
			} else {
				//这里只支持windows和linux，其他机器不支持，linux下使用命令上传hdfs
				StringBuilder fsSql = new StringBuilder();
				fsSql.append("source /etc/profile;source ~/.bashrc;");
				//拼接认证
				//TODO 有认证需要加认证文件key必须为keytab_file，需要加认证用户，key必须为keytab_user
				if (!StringUtil.isEmpty(dataStoreConfBean.getData_store_layer_file().get(StorageTypeKey.keytab_file))) {
					fsSql.append("kinit -k -t ").append(dataStoreConfBean.getData_store_layer_file()
							.get(StorageTypeKey.keytab_file)).append(" ").append(data_store_connect_attr
							.get(StorageTypeKey.keytab_user)).append(" ").append(System.lineSeparator());
				}
				//拼接上传hdfs命令
				fsSql.append("hadoop fs -put -f ").append(localFilePath).append(" ").append(hdfsPath)
						.append(" ").append(System.lineSeparator());
				String hdfsShellFile = FileNameUtils.normalize(Constant.HDFSSHELLFILE + objectTableBean
						.getEn_name() + ".sh", true);
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

	@Override
	public int getStageCode() {
		return StageConstant.UPLOAD.getCode();
	}
}
