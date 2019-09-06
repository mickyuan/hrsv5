package hrds.agent.job.biz.core;

import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.core.dbstage.*;
import hrds.agent.job.biz.utils.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ClassName: DataBaseJobImpl <br/>
 * Function: 完成数据库直连采集的作业实现. <br/>
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class DataBaseJobImpl implements JobInterface {

	private MetaInfoBean mateInfo = new MetaInfoBean();
	private final JobInfo jobInfo;
	private final DBConfigBean dbInfo;
	private final String statusFilePath;
	private final JobStatusInfo jobStatus;

	public DataBaseJobImpl(JobInfo jobInfo, DBConfigBean dbInfo, String statusFilePath, JobStatusInfo jobStatus) {
		this.jobInfo = jobInfo;
		this.dbInfo = dbInfo;
		this.statusFilePath = statusFilePath;
		this.jobStatus = jobStatus;
	}

	/*
	 * 1、设置作业ID，开始时间
	 * 2、构建每个阶段具体的实现类
	 * 3、构建责任链，串起每个阶段
	 * 4、按照顺序从第一个阶段开始执行作业
	 * 5、阶段执行完成后，写meta信息
	 * */
	@Override
	public JobStatusInfo runJob() {
		JobStatusInfo jobStatusInfo = this.jobStatus;
		//1、设置作业ID
		jobStatusInfo.setJobId(this.jobInfo.getJobId());
		//设置作业开始时间
		String dateString = DateUtil.getLocalDateByChar8();
		String timeString = DateUtil.getLocalTimeByChar6();
		jobStatusInfo.setStartDate(dateString);
		jobStatusInfo.setStartTime(timeString);
		//2、构建每个阶段具体的实现类，目前先按照完整顺序执行(卸数,上传,数据加载,计算增量,数据登记)，后期可改造为按照配置构建采集阶段
		DBUnloadDataStageImpl unloadData = new DBUnloadDataStageImpl(this.dbInfo, this.jobInfo);
		//TODO 数据库直连采集，多文件上传，remoteDir参数待确定，暂时传null
		JobStageInterface upload = new DBUploadStageImpl(this.jobInfo.getJobId(), unloadData.getFileArr(), null);
		//空实现
		JobStageInterface dataLoading = new DBDataLoadingStageImpl();
		//空实现
		JobStageInterface calIncrement = new DBCalIncrementStageImpl();
		//空实现
		JobStageInterface dataRegistration = new DBDataRegistrationStageImpl();
		//利用JobStageController构建本次数据库直连采集作业流程
		JobStageController controller = new JobStageController();
		//TODO 永远保证五个阶段，在每个阶段内部设置更合理的状态，比如直接加载时，unloadData和upload阶段的状态设置为跳过
		//3、构建责任链，串起每个阶段
		controller.registerJobStage(unloadData, upload, dataLoading, calIncrement, dataRegistration);

		//4、按照顺序从第一个阶段开始执行作业
		try {
			jobStatusInfo = controller.handleStageByOrder(this.statusFilePath, jobStatusInfo);
		} catch (Exception e) {
			//TODO 是否记录日志待讨论,因为目前的处理逻辑是数据库直连采集发生的所有checked类型异常全部向上抛，抛到这里统一处理
			e.printStackTrace();
		}

		List<String> columns = new ArrayList<>();
		for (ColumnCleanResult column : jobInfo.getColumnList()) {
			columns.add(column.getColumnName());
		}

		//5、阶段执行完成后，写meta信息
		mateInfo.setTableName(jobInfo.getTable_name());
		mateInfo.setColumnNames(columns);
		mateInfo.setColumnTypes(unloadData.getColumnTypes());
		mateInfo.setRowCount(unloadData.getRowCount());
		//TODO 讨论:数据库多线程采集，每个线程写一个文件，设置文件大小这里应该如何处理，目前暂时得到的是所有文件的总大小
		mateInfo.setFileSize(unloadData.getFileSize());

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
