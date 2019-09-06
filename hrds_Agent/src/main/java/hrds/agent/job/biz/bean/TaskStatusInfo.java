package hrds.agent.job.biz.bean;

import java.io.Serializable;
import java.util.List;

public class TaskStatusInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String taskId;
	private int runStatus;
	private String startDate;
	private String startTime;
	private List<JobInfo> jobs;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
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

	public List<JobInfo> getJobs() {
		return jobs;
	}

	public void setJobs(List<JobInfo> jobs) {
		this.jobs = jobs;
	}
}

