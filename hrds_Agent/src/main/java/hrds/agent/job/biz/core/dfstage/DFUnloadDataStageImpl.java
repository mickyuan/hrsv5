package hrds.agent.job.biz.core.dfstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.FileNameUtils;
import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.service.CollectTableHandleFactory;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.CollectType;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.exception.AppSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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
		LOGGER.info("------------------DB文件采集卸数阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, collectTableBean.getTable_id(),
				StageConstant.UNLOADDATA.getCode());
		try {
			//获取数据字典里对应的表的meta信息
			TableBean tableBean = CollectTableHandleFactory.getCollectTableHandleInstance(sourceDataConfBean)
					.generateTableInfo(sourceDataConfBean, collectTableBean);
			//判断仅登记
			if (IsFlag.Shi.getCode().equals(collectTableBean.getIs_register())) {
				//文件所在的路径为  根路径+跑批日期+表名+文件格式
				String file_path = FileNameUtils.normalize(tableBean.getRoot_path() + File.separator
						+ collectTableBean.getEtlDate() + File.separator + collectTableBean.getTable_name()
						+ File.separator + FileFormat.ofValueByCode(tableBean.getFile_format()) + File.separator, true);
				//列出文件目录下的文件
				String[] file_name_list = new File(file_path).list(
						(dir, name) -> new File(file_path + name).isFile()
				);
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
					throw new AppSystemException("数据文件不存在");
				}
				//仅登记，则不用转存，则跳过db文件卸数，直接进行upload
				LOGGER.info("Db文件采集，不需要转存，卸数跳过");
			} else if (IsFlag.Fou.getCode().equals(collectTableBean.getIs_register())) {
				Data_extraction_def targetData_extraction_def = collectTableBean.getTargetData_extraction_def();
				//TODO 需要转存，根据文件采集的定义，读取文件，卸数文件到指定的目录
				//将转存之后的文件格式，文件分隔符等重新赋值
				tableBean.setColumn_separator(targetData_extraction_def.getDatabase_separatorr());
				tableBean.setIs_header(targetData_extraction_def.getIs_header());
				tableBean.setRow_separator(targetData_extraction_def.getRow_separator());
				tableBean.setFile_format(targetData_extraction_def.getDbfile_format());
				tableBean.setFile_code(targetData_extraction_def.getDatabase_code());
			} else {
				throw new AppSystemException("是否转存传到后台的参数不正确");
			}
			stageParamInfo.setTableBean(tableBean);
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error("DB文件采集卸数阶段失败：", e);
		}
		LOGGER.info("------------------DB文件采集卸数阶段结束------------------");
		//结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, collectTableBean
				, CollectType.DBWenJianCaiJi.getCode());
		return stageParamInfo;
	}

	@Override
	public int getStageCode() {
		return StageConstant.UNLOADDATA.getCode();
	}

}
