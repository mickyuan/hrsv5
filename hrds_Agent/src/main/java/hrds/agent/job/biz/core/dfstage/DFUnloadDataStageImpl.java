package hrds.agent.job.biz.core.dfstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.dbstage.DBUnloadDataStageImpl;
import hrds.agent.job.biz.core.dfstage.service.FileConversionThread;
import hrds.agent.job.biz.core.metaparse.CollectTableHandleFactory;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.StorageTypeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

@DocClass(desc = "数据文件采集，数据卸数阶段实现", author = "WangZhengcheng")
public class DFUnloadDataStageImpl extends AbstractJobStage {

	private final static Logger LOGGER = LoggerFactory.getLogger(DFUnloadDataStageImpl.class);
	private SourceDataConfBean sourceDataConfBean;
	private CollectTableBean collectTableBean;

	/**
	 * 数据文件采集，数据卸数阶段实现.
	 */
	public DFUnloadDataStageImpl(SourceDataConfBean sourceDataConfBean, CollectTableBean collectTableBean) {
		this.collectTableBean = collectTableBean;
		this.sourceDataConfBean = sourceDataConfBean;
	}

	@Method(desc = "数据文件采集，数据卸数阶段实现，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		long startTime = System.currentTimeMillis();
		LOGGER.info("------------------表" + collectTableBean.getHbase_name()
				+ "DB文件采集卸数阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, collectTableBean.getTable_id(),
				StageConstant.UNLOADDATA.getCode());
		ExecutorService executorService = null;
		try {
			//获取数据字典里对应的表的meta信息
			TableBean tableBean = CollectTableHandleFactory.getCollectTableHandleInstance(sourceDataConfBean)
					.generateTableInfo(sourceDataConfBean, collectTableBean);
			//文件所在的路径为  根路径+跑批日期+表名+文件格式
//			String file_path = FileNameUtils.normalize(tableBean.getRoot_path() + File.separator
//					+ collectTableBean.getEtlDate() + File.separator + collectTableBean.getTable_name()
//					+ File.separator + JobConstant.fileFormatMap.get(tableBean.getFile_format())
//					+ File.separator, true);
			//获取文件所在的路径下，符合正则匹配的文件
			String filePathPattern = getFilePathPattern(tableBean.getRoot_path(), collectTableBean.getEtlDate(),
					collectTableBean.getTable_name(), tableBean.getFile_format());
			//拿到文件所在的文件夹路径
			String file_path = FileNameUtils.getFullPath(filePathPattern);
			//拿到匹配文件的规则
			String regex = FileNameUtils.getName(filePathPattern);
			String[] file_name_list = new File(file_path).list(new FilenameFilter() {
				private Pattern pattern = Pattern.compile(regex);

				@Override
				public boolean accept(File dir, String name) {
					return pattern.matcher(name).matches();
				}
			});
			//列出文件目录下的文件
//			String[] file_name_list = new File(file_path).list(
//					(dir, name) -> (name.contains(collectTableBean.getTable_name()) &&
//							!name.startsWith("."))
//			);
			//判断是否转存
			if (IsFlag.Fou.getCode().equals(tableBean.getIs_archived()) || UnloadType.ZengLiangXieShu.getCode().
					equals(collectTableBean.getUnload_type())) {
				//不转存
				//获得本次采集生成的数据文件的总大小
				if (file_name_list != null && file_name_list.length > 0) {
					long fileSize = 0;
					String[] file_path_list = new String[file_name_list.length];
					for (int i = 0; i < file_name_list.length; i++) {
						file_path_list[i] = file_path + file_name_list[i];
						//判断文件是否存在，如果某个文件存在，则计算大小，若不存在，记录日志并继续运行
						if (FileUtil.decideFileExist(file_path_list[i])) {
							long singleFileSize = FileUtil.getFileSize(file_path_list[i]);
							fileSize += singleFileSize;
						} else {
							throw new AppSystemException(file_path_list[i] + "文件不存在");
						}
					}
					stageParamInfo.setFileArr(file_path_list);
					stageParamInfo.setFileSize(fileSize);
					stageParamInfo.setFileNameArr(file_name_list);
				} else {
					throw new AppSystemException("表" + collectTableBean.getHbase_name()
							+ "数据字典指定目录" + file_path + "下数据文件不存在");
				}
				//不用转存，则跳过db文件卸数，直接进行upload
				LOGGER.info("表" + collectTableBean.getHbase_name()
						+ "Db文件采集，不需要转存或者增量采集，卸数跳过");
			} else if (IsFlag.Shi.getCode().equals(tableBean.getIs_archived())) {
				//获取db文件采集转存的文件编码，
				// XXX 主要涉及到oracle数据库如果用外部表进数，字符集必须跟文件字符集一致的问题
				tableBean.setDbFileArchivedCode(getDbFileArchivedCode(collectTableBean, tableBean.getFile_code()));
				//Data_extraction_def targetData_extraction_def = collectTableBean.getTargetData_extraction_def();
				//TODO 需要转存，根据文件采集的定义，读取文件，卸数文件到指定的目录
				//根据源定义读取文件，将读取到的文件统一转为List<List>每5000行统一处理一次
				// 此处不会有海量的任务需要执行，不会出现队列中等待的任务对象过多的OOM事件。XXX 默认五个线程读取文件
				if (file_name_list != null && file_name_list.length > 0) {
					//主线程创建文件夹
					String unloadFileAbsolutePath = FileNameUtils.normalize(Constant.DBFILEUNLOADFOLDER +
							collectTableBean.getDatabase_id() + File.separator + collectTableBean.getHbase_name() +
							File.separator + collectTableBean.getEtlDate() + File.separator, true);
					File dir = new File(unloadFileAbsolutePath);
					//这里要考虑重跑的问题
					if (dir.exists()) {
						fd.ng.core.utils.FileUtil.cleanDirectory(dir);
					} else {
						fd.ng.core.utils.FileUtil.forceMkdir(dir);
					}
					LOGGER.info(FileFormat.ofValueByCode(tableBean.getFile_format()) + "文件开始转存");
					executorService = Executors.newFixedThreadPool(JobConstant.AVAILABLEPROCESSORS);
					List<Future<String>> futures = new ArrayList<>();
					for (String fileName : file_name_list) {
						FileConversionThread thread = new FileConversionThread(tableBean, collectTableBean,
								file_path + fileName);
						Future<String> future = executorService.submit(thread);
						futures.add(future);
					}
					//fileResult中是生成的所有数据文件的路径，用于判断卸数阶段结果
					List<String> fileResult = new ArrayList<>();
					//pageCountResult是本次采集作业每个线程采集到的数据量，用于写meta文件
					List<Long> pageCountResult = new ArrayList<>();
					//5、获得结果,用于校验多线程采集的结果和写Meta文件
					for (Future<String> future : futures) {
						String parseResult = future.get();
						List<String> split = StringUtil.split(parseResult, Constant.METAINFOSPLIT);
						fileResult.add(split.get(0));
						pageCountResult.add(Long.parseLong(split.get(1)));
						LOGGER.info("---------------" + parseResult + "---------------");
					}
					LOGGER.info("表" + collectTableBean.getHbase_name()
							+ FileFormat.ofValueByCode(tableBean.getFile_format()) + "文件转存结束");
					//统计的结果
					DBUnloadDataStageImpl.countResult(fileResult, pageCountResult, stageParamInfo);
					stageParamInfo.setFileNameArr(file_name_list);
					//将转存之后的文件格式，文件分隔符等重新赋值,给上传阶段的程序使用
					//列分隔符为默认值
					tableBean.setColumn_separator(Constant.DATADELIMITER);
					tableBean.setIs_header(IsFlag.Fou.getCode());
					//XXX 换行符默认使用linux的换行符
					tableBean.setRow_separator(Constant.DEFAULTLINESEPARATOR);
					tableBean.setFile_format(FileFormat.FeiDingChang.getCode());
					tableBean.setFile_code(tableBean.getDbFileArchivedCode());
				} else {
					throw new AppSystemException("表" + collectTableBean.getHbase_name()
							+ "数据字典指定目录下" + file_path + "数据文件不存在");
				}
			} else {
				throw new AppSystemException("表" + collectTableBean.getHbase_name()
						+ "是否转存传到后台的参数不正确");
			}
			stageParamInfo.setTableBean(tableBean);
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error("表" + collectTableBean.getHbase_name()
					+ "DB文件采集卸数阶段失败：", e);
		} finally {
			if (executorService != null)
				executorService.shutdown();
		}
		LOGGER.info("------------------表" + collectTableBean.getHbase_name()
				+ "DB文件采集卸数阶段结束------------------执行时间为："
				+ (System.currentTimeMillis() - startTime) / 1000 + "，秒");
		//结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, collectTableBean
				, AgentType.DBWenJian.getCode());
		return stageParamInfo;
	}

	/**
	 * 替换文件全路径的占位符
	 *
	 * @param root_path   文件全路径
	 * @param etlDate     跑批日期
	 * @param table_name  表名
	 * @param file_format 文件格式
	 * @return 返回替换占位符后的路径
	 */
	private String getFilePathPattern(String root_path, String etlDate, String table_name, String file_format) {
		//替换跑批日期
		root_path = root_path.replace("#{date}", etlDate);
		//替换表名
		root_path = root_path.replace("#{table}", table_name);
		//替换文件格式
		root_path = root_path.replace("#{文件格式}", Constant.fileFormatMap.get(file_format));
		return root_path;
	}

	/**
	 * 获取文件转存的默认编码，目的地有oracle数据库时，会主动去取oracle数据库键值对下配置的编码，
	 * 没有设置，取读文件的编码为写文件的编码
	 *
	 * @param collectTableBean 采集的表属性实体
	 * @param fileCode         db文件编码
	 */
	private String getDbFileArchivedCode(CollectTableBean collectTableBean, String fileCode) {
		List<DataStoreConfBean> dataStoreConfBean = collectTableBean.getDataStoreConfBean();
		for (DataStoreConfBean bean : dataStoreConfBean) {
			if (Store_type.DATABASE.getCode().equals(bean.getStore_type())) {
				Map<String, String> data_store_connect_attr = bean.getData_store_connect_attr();
//				if(DatabaseType.Oracle10g.getCode().equals(data_store_connect_attr.get(StorageTypeKey.database_type)))
				if (!StringUtil.isEmpty(data_store_connect_attr.get(StorageTypeKey.database_code))) {
					for (DataBaseCode typeCode : DataBaseCode.values()) {
						if (typeCode.getValue().equalsIgnoreCase(data_store_connect_attr.
								get(StorageTypeKey.database_code))) {
							return typeCode.getCode();
						}
					}
				}
			}
		}
		return fileCode;
	}

	@Override
	public int getStageCode() {
		return StageConstant.UNLOADDATA.getCode();
	}
}
