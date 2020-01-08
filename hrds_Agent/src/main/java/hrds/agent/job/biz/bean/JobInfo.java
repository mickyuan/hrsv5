package hrds.agent.job.biz.bean;

import java.io.Serializable;
import java.util.List;

public class JobInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	//任务编号
	private String taskId;
	//作业编号
	private String jobId;
	private List<ColumnCleanBean> columnList;

	public String getTaskId() {

		return taskId;
	}

	public void setTaskId(String taskId) {

		this.taskId = taskId;
	}

	public String getJobId() {

		return jobId;
	}

	public void setJobId(String jobId) {

		this.jobId = jobId;
	}

	public List<ColumnCleanBean> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<ColumnCleanBean> columnList) {
		this.columnList = columnList;
	}
}
