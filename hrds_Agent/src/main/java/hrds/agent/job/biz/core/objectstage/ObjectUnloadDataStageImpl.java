package hrds.agent.job.biz.core.objectstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.FileNameUtils;
import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.metaparse.impl.ObjectCollectTableHandleParse;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.AgentType;
import hrds.commons.exception.AppSystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

//XXX 半结构化对象采集要不要加卸数，通过看代码，理解出来的以前的对象采集是没有卸数这一步
@DocClass(desc = "半结构化对象采集卸数实现", author = "zxz", createdate = "2019/10/24 11:43")
public class ObjectUnloadDataStageImpl extends AbstractJobStage {
	//打印日志
	private static final Logger LOGGER = LogManager.getLogger();

	private final ObjectCollectParamBean objectCollectParamBean;
	private final ObjectTableBean objectTableBean;

	public ObjectUnloadDataStageImpl(ObjectCollectParamBean objectCollectParamBean,
									 ObjectTableBean objectTableBean) {
		this.objectCollectParamBean = objectCollectParamBean;
		this.objectTableBean = objectTableBean;
	}

	@Method(desc = "半结构化对象采集，半结构化对象采集卸数实现，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		//开始时间
		long startTime = System.currentTimeMillis();
		LOGGER.info("------------------表" + objectTableBean.getEn_name() + "半结构化对象采集卸数阶段开始---------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, objectTableBean.getOcs_id(),
				StageConstant.UNLOADDATA.getCode());
		try {
			//执行卸数
			TableBean tableBean = new ObjectCollectTableHandleParse()
					.generateTableInfo(objectCollectParamBean, objectTableBean);
			//获取文件所在的路径下，符合正则匹配的文件
			String filePathPattern = objectCollectParamBean.getFile_path() + File.separator + objectTableBean.
					getEtlDate() + File.separator + objectTableBean.getEn_name() + "_" + objectTableBean.getEtlDate()
					+ "." + objectCollectParamBean.getFile_suffix();
			//拿到文件所在的文件夹路径
			String file_path = FileNameUtils.getFullPath(filePathPattern);
			//拿到匹配文件的规则
			String regex = FileNameUtils.getName(filePathPattern);
			String[] file_name_list = new File(file_path).list(new FilenameFilter() {
				private final Pattern pattern = Pattern.compile(regex);
				@Override
				public boolean accept(File dir, String name) {
					return pattern.matcher(name).matches();
				}
			});
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
				throw new AppSystemException("半结构化对象" + objectTableBean.getEn_name()
						+ "数据字典指定目录" + file_path + "下数据文件不存在");
			}
			stageParamInfo.setTableBean(tableBean);
			//不用转存，则跳过db文件卸数，直接进行upload
			LOGGER.info(objectTableBean.getEn_name() + "半结构化对象采集，不需要转存，卸数跳过");
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------表" + objectTableBean.getEn_name()
					+ "半结构化对象采集卸数阶段成功------------------执行时间为："
					+ (System.currentTimeMillis() - startTime) / 1000 + "，秒");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error(objectTableBean.getEn_name() + "半结构化对象采集卸数阶段失败：", e);
		}
		//结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, objectTableBean
				, AgentType.DuiXiang.getCode());
		return stageParamInfo;
	}

	@Override
	public int getStageCode() {
		return StageConstant.UNLOADDATA.getCode();
	}
}
