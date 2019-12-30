package hrds.agent.job.biz.bean;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.List;

public class JobInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	//任务编号
	private String taskId;
	//作业编号
	private String jobId;
	private List<ColumnCleanBean> columnList;
	private String table_name;
	private String sql;
	//TODO 这个是海云用于记录字段顺序及字段类型
	private JSONObject tablename;

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

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}



	public JSONObject getTablename() {
		return tablename;
	}

	public void setTablename(JSONObject tablename) {
		this.tablename = tablename;
	}

	@Override
	public String toString() {
		return "JobInfo{" +
				"taskId='" + taskId + '\'' +
				", jobId='" + jobId + '\'' +
				", columnList=" + columnList +
				", table_name='" + table_name + '\'' +
				", sql='" + sql + '\'' +
				", tablename=" + tablename +
				'}';
	}
}
