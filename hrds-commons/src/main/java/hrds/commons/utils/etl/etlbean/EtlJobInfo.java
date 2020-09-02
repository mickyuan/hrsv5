package hrds.commons.utils.etl.etlbean;

import fd.ng.core.annotation.DocClass;
import hrds.commons.entity.Etl_job_def;

@DocClass(desc = "生成作业调度时的实体类", author = "Mr.Lee", createdate = "2020-05-29 17:30")
public class EtlJobInfo {

	/**
	 * 任务ID
	 */
	private long colSetId;
	/**
	 * 数据源ID
	 */
	private long source_id;
	/**
	 * 作业工程编号
	 */
	private String etl_sys_cd;
	/**
	 * 作业任务编号
	 */
	private String sub_sys_cd;
	/**
	 * 作业程序目录
	 */
	private String pro_dic;
	/**
	 * 作业日志目录
	 */
	private String log_dic;
	/**
	 * 作业信息
	 */
	private Etl_job_def[] etlJobs;
	/**
	 * 卸数文件的ID
	 */
	private String ded_arr;
	/**
	 * 作业依赖信息
	 */
	private String jobRelations;

	public long getColSetId() {
		return colSetId;
	}

	public void setColSetId(long colSetId) {
		this.colSetId = colSetId;
	}

	public long getSource_id() {
		return source_id;
	}

	public void setSource_id(long source_id) {
		this.source_id = source_id;
	}

	public String getEtl_sys_cd() {
		return etl_sys_cd;
	}

	public void setEtl_sys_cd(String etl_sys_cd) {
		this.etl_sys_cd = etl_sys_cd;
	}

	public String getSub_sys_cd() {
		return sub_sys_cd;
	}

	public void setSub_sys_cd(String sub_sys_cd) {
		this.sub_sys_cd = sub_sys_cd;
	}

	public String getPro_dic() {
		return pro_dic;
	}

	public void setPro_dic(String pro_dic) {
		this.pro_dic = pro_dic;
	}

	public String getLog_dic() {
		return log_dic;
	}

	public void setLog_dic(String log_dic) {
		this.log_dic = log_dic;
	}

	public Etl_job_def[] getEtlJobs() {
		return etlJobs;
	}

	public void setEtlJobs(Etl_job_def[] etlJobs) {
		this.etlJobs = etlJobs;
	}

	public String getDed_arr() {
		return ded_arr;
	}

	public void setDed_arr(String ded_arr) {
		this.ded_arr = ded_arr;
	}

	public String getJobRelations() {
		return jobRelations;
	}

	public void setJobRelations(String jobRelations) {
		this.jobRelations = jobRelations;
	}
}
