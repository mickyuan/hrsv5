package hrds.agent.job.biz.bean;

import java.io.Serializable;

/**
 * ClassName: JobParamBean <br/>
 * Function: 作业配置信息 <br/>
 * Reason： 海云向agent发送，agent解析生成实体
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class JobParamBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String end_date;
	private String collect_set_id;
	private String file_path;
	private String agent_time;
	private String create_time;
	private String agent_id;
	private String rely_job_id;
	private String database_number;
	private String fre_month;
	private String comp_id;
	private String cf_jobnum;
	private String fre_week;
	private String component;
	private String run_way;
	private String agent_date;
	private String cf_id;
	private String collect_type;
	private String cron_expression;
	private String fre_day;
	private String execute_time;
	private String create_date;
	private String start_date;

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getCollect_set_id() {
		return collect_set_id;
	}

	public void setCollect_set_id(String collect_set_id) {
		this.collect_set_id = collect_set_id;
	}

	public String getFile_path() {
		return file_path;
	}

	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}

	public String getAgent_time() {
		return agent_time;
	}

	public void setAgent_time(String agent_time) {
		this.agent_time = agent_time;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}

	public String getRely_job_id() {
		return rely_job_id;
	}

	public void setRely_job_id(String rely_job_id) {
		this.rely_job_id = rely_job_id;
	}

	public String getDatabase_number() {
		return database_number;
	}

	public void setDatabase_number(String database_number) {
		this.database_number = database_number;
	}

	public String getFre_month() {
		return fre_month;
	}

	public void setFre_month(String fre_month) {
		this.fre_month = fre_month;
	}

	public String getComp_id() {
		return comp_id;
	}

	public void setComp_id(String comp_id) {
		this.comp_id = comp_id;
	}

	public String getCf_jobnum() {
		return cf_jobnum;
	}

	public void setCf_jobnum(String cf_jobnum) {
		this.cf_jobnum = cf_jobnum;
	}

	public String getFre_week() {
		return fre_week;
	}

	public void setFre_week(String fre_week) {
		this.fre_week = fre_week;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getRun_way() {
		return run_way;
	}

	public void setRun_way(String run_way) {
		this.run_way = run_way;
	}

	public String getAgent_date() {
		return agent_date;
	}

	public void setAgent_date(String agent_date) {
		this.agent_date = agent_date;
	}

	public String getCf_id() {
		return cf_id;
	}

	public void setCf_id(String cf_id) {
		this.cf_id = cf_id;
	}

	public String getCron_expression() {
		return cron_expression;
	}

	public void setCron_expression(String cron_expression) {
		this.cron_expression = cron_expression;
	}

	public String getCollect_type() {
		return collect_type;
	}

	public void setCollect_type(String collect_type) {
		this.collect_type = collect_type;
	}

	public String getFre_day() {
		return fre_day;
	}

	public void setFre_day(String fre_day) {
		this.fre_day = fre_day;
	}

	public String getExecute_time() {
		return execute_time;
	}

	public void setExecute_time(String execute_time) {
		this.execute_time = execute_time;
	}

	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	@Override
	public String toString() {
		return "JobParamBean [end_date=" + end_date + ", collect_set_id=" + collect_set_id + ", file_path=" + file_path
				+ ", agent_time=" + agent_time + ", create_time=" + create_time + ", agent_id=" + agent_id
				+ ", rely_job_id=" + rely_job_id + ", database_number=" + database_number + ", fre_month=" + fre_month
				+ ", comp_id=" + comp_id + ", cf_jobnum=" + cf_jobnum + ", fre_week=" + fre_week + ", component="
				+ component + ", run_way=" + run_way + ", agent_date=" + agent_date + ", cf_id=" + cf_id
				+ ", cron_expression=" + cron_expression + ", fre_day=" + fre_day + ", execute_time=" + execute_time
				+ ", create_date=" + create_date + ", start_date=" + start_date + "]";
	}
}
