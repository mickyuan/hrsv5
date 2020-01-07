package hrds.agent.job.biz.core.dfstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import hrds.agent.job.biz.bean.StageParamInfo;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.dfstage.fileparser.CsvFile2Parquet;
import hrds.commons.codes.FileFormat;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

@DocClass(desc = "数据文件采集，数据卸数阶段实现", author = "WangZhengcheng")
public class DFUnloadDataStageImpl extends AbstractJobStage {

	private final static Logger LOGGER = LoggerFactory.getLogger(DFUnloadDataStageImpl.class);

	private final String jobId;
	private final String inputFile;
	private final String tableName;
	private final List<String> columns;
	private final List<String> columnTypes;
	private final String startDate;

	private final String outputFile;
	private long rowCount = 0;

	/**
	 * 数据文件采集，数据卸数阶段实现.
	 *
	 * @param jobId       作业编号
	 * @param inputFile   数据文件，输入文件
	 * @param tableName   名称，可以是数据库的表名或者只是名称
	 * @param columns     字段
	 * @param columnTypes 字段类型
	 * @param startDate   拉链用的开始日期，该数据会额外写入文件为一列
	 * @author 13616
	 * @date 2019/8/7 11:49
	 */
	public DFUnloadDataStageImpl(String jobId, String inputFile, String tableName, List<String> columns,
	                             List<String> columnTypes, String startDate) {
		this.jobId = jobId;
		this.inputFile = inputFile;
		this.tableName = tableName;
		this.columns = columns;
		this.columnTypes = columnTypes;
		this.startDate = startDate;
		//TODO 输出文件待定
		this.outputFile = new File(inputFile).getParent() + File.separatorChar + tableName + "." +
				FileFormat.PARQUET.getValue();
	}

	@Method(desc = "数据文件采集，数据卸数阶段实现，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {

		StageStatusInfo status = new StageStatusInfo();
		status.setJobId(jobId);
		status.setStageNameCode(StageConstant.UNLOADDATA.getCode());
		status.setStartDate(DateUtil.getSysDate());
		status.setStartTime(DateUtil.getSysTime());

		CsvFile2Parquet csvFile2Parquet = new CsvFile2Parquet.Builder(tableName, StringUtils.join(columns,
				JobConstant.COLUMN_SEPARATOR), StringUtils.join(columnTypes, JobConstant.COLUMN_TYPE_SEPARATOR),
				startDate).builCsvFile(inputFile, outputFile).build();

		rowCount = csvFile2Parquet.handle();

		LOGGER.info("------------------数据文件卸数阶段结束------------------");
		status.setEndDate(DateUtil.getSysDate());
		status.setEndTime(DateUtil.getSysTime());
		status.setStatusCode(RunStatusConstant.SUCCEED.getCode());

		stageParamInfo.setStatusInfo(status);
		return stageParamInfo;
	}

	@Override
	public int getStageCode(){
		return StageConstant.UNLOADDATA.getCode();
	}

	public String getOutputFile() {

		return outputFile;
	}

	public long getRowCount() {

		return rowCount;
	}
}
