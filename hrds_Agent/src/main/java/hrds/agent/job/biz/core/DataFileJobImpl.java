package hrds.agent.job.biz.core;

import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.dfstage.*;
import hrds.agent.job.biz.utils.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ClassName: DataFileJobImpl <br/>
 * Function: 完成数据文件采集的作业实现. <br/>
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author 13616
 * Version 1.0
 * Since JDK 1.8
 **/
public class DataFileJobImpl implements JobInterface {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataFileJobImpl.class);

	private MetaInfoBean mateInfo = new MetaInfoBean();
	private final JobInfo job;
	private final JobParamBean jobParam;
	private final String statusFilePath;
	private final JobStatusInfo jobStatus;

	/**
	 * 完成数据文件采集的作业实现.
	 *
	 * @param job            JobInfo对象，表示一个作业
	 * @param jobParam       JobParamBean对象，表示作业参数
	 * @param statusFilePath 作业状态文件地址，用于更新运行时状态
	 * @param jobStatus      JobStatusInfo对象，表示一个作业的状态
	 * @author 13616
	 * @date 2019/8/7 11:52
	 */
	DataFileJobImpl(JobInfo job, JobParamBean jobParam, String statusFilePath, JobStatusInfo jobStatus) {

		this.job = job;
		this.jobParam = jobParam;
		this.statusFilePath = statusFilePath;
		this.jobStatus = jobStatus;
	}

	@Override
	public JobStatusInfo runJob() {
		String jobId = job.getJobId();
		LOGGER.info("作业正在运行中，作业编号为{}", jobId);
		String inputFile = jobParam.getFile_path();
		List<String> columns = new ArrayList<>();
		for (ColumnCleanResult column : job.getColumnList()) {
			columns.add(column.getColumnName());
		}
		//TODO 此处肯定要改，所以先写死
		String tableName = job.getTable_name();
		String[] columnAndtypes = StringUtils.splitByWholeSeparatorPreserveAllTokens(
				job.getTablename().getString(tableName), "#");
		List<String> columnTypes = new ArrayList<>();
		for (String colAndType : columnAndtypes) {
			columnTypes.add(StringUtils.splitByWholeSeparatorPreserveAllTokens(colAndType,
					JobConstant.COLUMN_TYPE_SEPARATOR)[1]);
		}
		String startDate = jobParam.getStart_date();
		//目前先按照完整顺序执行，后期可改造为按照配置构建采集阶段
		DFUnloadDataStageImpl unloadData = new DFUnloadDataStageImpl(jobId, inputFile,
				tableName, columns, columnTypes, startDate);
		//TODO hdfs的目录结构如何组织的
		JobStageInterface upload = new DFUploadStageImpl(jobId, unloadData.getOutputFile(), "");
		JobStageInterface dataLoading = new DFDataLoadingStageImpl();
		JobStageInterface calIncrement = new DFCalIncrementStageImpl();
		JobStageInterface dataRegistration = new DFDataRegistrationStageImpl();

		JobStageController manager = new JobStageController();
		manager.registerJobStage(unloadData, upload, dataLoading, calIncrement, dataRegistration);
		JobStatusInfo jobStatusInfo = jobStatus;
		//从第一个阶段开始执行作业
		try {
			jobStatusInfo = manager.handleStageByOrder(statusFilePath, jobStatusInfo);
		} catch (Exception e) {
			jobStatusInfo.setExceptionInfo(e.getMessage());
		}

		mateInfo.setTableName(job.getTable_name());
		mateInfo.setColumnNames(columns);
		mateInfo.setColumnTypes(columnTypes);
		mateInfo.setRowCount(unloadData.getRowCount());
		mateInfo.setFileSize(FileUtil.getFileSize(unloadData.getOutputFile()));

		return jobStatusInfo;
	}

	@Override
	public List<MetaInfoBean> getMetaInfoGroup() {

		return Arrays.asList(mateInfo);
	}

	@Override
	public MetaInfoBean getMetaInfo() {

		return mateInfo;
	}
}