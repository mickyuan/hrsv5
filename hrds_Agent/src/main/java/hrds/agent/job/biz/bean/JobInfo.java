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
	private String jobFilePath;
	private String storage_type;
	private String table_count;
	private String up_source_name;
	private String indexinfo;
	private String is_md5;
	private List<ColumnCleanBean> columnList;
	private String pageColumn;
	private String table_name;
	private String is_solr_hbase;
	private String sql;
	private TableCleanResult tbCleanResult;
	private String file_format;
	private String is_into_hbase;
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

	public String getJobFilePath() {

		return jobFilePath;
	}

	public void setJobFilePath(String jobFilePath) {

		this.jobFilePath = jobFilePath;
	}

	public String getStorage_type() {
		return storage_type;
	}

	public void setStorage_type(String storage_type) {
		this.storage_type = storage_type;
	}

	public String getTable_count() {
		return table_count;
	}

	public void setTable_count(String table_count) {
		this.table_count = table_count;
	}

	public String getUp_source_name() {
		return up_source_name;
	}

	public void setUp_source_name(String up_source_name) {
		this.up_source_name = up_source_name;
	}

	public String getIndexinfo() {
		return indexinfo;
	}

	public void setIndexinfo(String indexinfo) {
		this.indexinfo = indexinfo;
	}

	public String getIs_md5() {
		return is_md5;
	}

	public void setIs_md5(String is_md5) {
		this.is_md5 = is_md5;
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

	public String getIs_solr_hbase() {
		return is_solr_hbase;
	}

	public void setIs_solr_hbase(String is_solr_hbase) {
		this.is_solr_hbase = is_solr_hbase;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public TableCleanResult getTbCleanResult() {
		return tbCleanResult;
	}

	public void setTbCleanResult(TableCleanResult tbCleanResult) {
		this.tbCleanResult = tbCleanResult;
	}

	public String getFile_format() {
		return file_format;
	}

	public void setFile_format(String file_format) {
		this.file_format = file_format;
	}

	public String getIs_into_hbase() {
		return is_into_hbase;
	}

	public void setIs_into_hbase(String is_into_hbase) {
		this.is_into_hbase = is_into_hbase;
	}

	public String getPageColumn() {
		return pageColumn;
	}

	public void setPageColumn(String pageColumn) {
		this.pageColumn = pageColumn;
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
				", jobFilePath='" + jobFilePath + '\'' +
				", storage_type='" + storage_type + '\'' +
				", table_count='" + table_count + '\'' +
				", up_source_name='" + up_source_name + '\'' +
				", indexinfo='" + indexinfo + '\'' +
				", is_md5='" + is_md5 + '\'' +
				", columnList=" + columnList +
				", pageColumn='" + pageColumn + '\'' +
				", table_name='" + table_name + '\'' +
				", is_solr_hbase='" + is_solr_hbase + '\'' +
				", sql='" + sql + '\'' +
				", tbCleanResult=" + tbCleanResult +
				", file_format='" + file_format + '\'' +
				", is_into_hbase='" + is_into_hbase + '\'' +
				", tablename=" + tablename +
				'}';
	}
}
