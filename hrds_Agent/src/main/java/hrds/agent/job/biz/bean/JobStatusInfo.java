package hrds.agent.job.biz.bean;

import java.io.Serializable;

/**
 * ClassName: JobStatusInfo <br/>
 * Function: 作业状态信息 <br/>
 * Reason： 用于记录作业的开始时间、结束时间以及每个步骤的执行状态
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class JobStatusInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String jobId;
	private int runStatus;
	private String startDate;
	private String startTime;
	private String endDate;
	private String endTime;
	//TODO 卸数(开始，成功，失败)，上传，数据加载，增量，数据登记
	private StageStatusInfo unloadDataStatus;
	private StageStatusInfo uploadStatus;
	private StageStatusInfo dataLodingStatus;
	private StageStatusInfo calIncrementStatus;
	private StageStatusInfo dataRegistrationStatus;
	private String exceptionInfo;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public int getRunStatus() {
		return runStatus;
	}

	public void setRunStatus(int runStatus) {
		this.runStatus = runStatus;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public StageStatusInfo getUnloadDataStatus() {
		return unloadDataStatus;
	}

	public void setUnloadDataStatus(StageStatusInfo unloadDataStatus) {
		this.unloadDataStatus = unloadDataStatus;
	}

	public StageStatusInfo getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(StageStatusInfo uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	public StageStatusInfo getDataLodingStatus() {
		return dataLodingStatus;
	}

	public void setDataLodingStatus(StageStatusInfo dataLodingStatus) {
		this.dataLodingStatus = dataLodingStatus;
	}

	public StageStatusInfo getCalIncrementStatus() {
		return calIncrementStatus;
	}

	public void setCalIncrementStatus(StageStatusInfo calIncrementStatus) {
		this.calIncrementStatus = calIncrementStatus;
	}

	public StageStatusInfo getDataRegistrationStatus() {
		return dataRegistrationStatus;
	}

	public void setDataRegistrationStatus(StageStatusInfo dataRegistrationStatus) {
		this.dataRegistrationStatus = dataRegistrationStatus;
	}

	public String getExceptionInfo() {
		return exceptionInfo;
	}

	public void setExceptionInfo(String exceptionInfo) {
		this.exceptionInfo = exceptionInfo;
	}

	@Override
	public String toString() {
		return "JobStatusInfo{" +
				"jobId='" + jobId + '\'' +
				", runStatus=" + runStatus +
				", startDate='" + startDate + '\'' +
				", startTime='" + startTime + '\'' +
				", unloadDataStatus=" + unloadDataStatus +
				", uploadStatus=" + uploadStatus +
				", dataLodingStatus=" + dataLodingStatus +
				", calIncrementStatus=" + calIncrementStatus +
				", dataRegistrationStatus=" + dataRegistrationStatus +
				", exceptionInfo='" + exceptionInfo + '\'' +
				'}';
	}
}

