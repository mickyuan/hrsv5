package hrds.entity;

import fd.ng.db.entity.TableEntity;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 实体类中所有属性都应定义为对象，不要使用int等主类型，方便对null值的操作
 */
@Table(tableName = "job_info")
public class JobInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "job_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("job_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String log_dic;
	private String job_s_date;
	private String job_priority;
	private BigDecimal agent_id;
	private String rely_job_id;
	private String job_desc;
	private String log_level;
	private String excluding_info;
	private String fre_month;
	private BigDecimal task_id;
	private String job_eff_flag;
	private String fre_week;
	private String pro_para;
	private String job_e_date;
	private String run_way;
	private BigDecimal create_id;
	private String job_name;
	private String cron_expression;
	private BigDecimal project_id;
	private BigDecimal user_id;
	private BigDecimal job_id;
	private String fre_day;
	private String disp_time;
	private String job_path;

	public String getLog_dic() { return log_dic; }
	public void setLog_dic(String log_dic) {
		if(log_dic==null) addNullValueField("log_dic");
		this.log_dic = log_dic;
	}

	public String getJob_s_date() { return job_s_date; }
	public void setJob_s_date(String job_s_date) {
		if(job_s_date==null) throw new BusinessException("Entity : JobInfo.job_s_date must not null!");
		this.job_s_date = job_s_date;
	}

	public String getJob_priority() { return job_priority; }
	public void setJob_priority(String job_priority) {
		if(job_priority==null) addNullValueField("job_priority");
		this.job_priority = job_priority;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : JobInfo.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public String getRely_job_id() { return rely_job_id; }
	public void setRely_job_id(String rely_job_id) {
		if(rely_job_id==null) addNullValueField("rely_job_id");
		this.rely_job_id = rely_job_id;
	}

	public String getJob_desc() { return job_desc; }
	public void setJob_desc(String job_desc) {
		if(job_desc==null) addNullValueField("job_desc");
		this.job_desc = job_desc;
	}

	public String getLog_level() { return log_level; }
	public void setLog_level(String log_level) {
		if(log_level==null) addNullValueField("log_level");
		this.log_level = log_level;
	}

	public String getExcluding_info() { return excluding_info; }
	public void setExcluding_info(String excluding_info) {
		if(excluding_info==null) addNullValueField("excluding_info");
		this.excluding_info = excluding_info;
	}

	public String getFre_month() { return fre_month; }
	public void setFre_month(String fre_month) {
		if(fre_month==null) addNullValueField("fre_month");
		this.fre_month = fre_month;
	}

	public BigDecimal getTask_id() { return task_id; }
	public void setTask_id(BigDecimal task_id) {
		if(task_id==null) throw new BusinessException("Entity : JobInfo.task_id must not null!");
		this.task_id = task_id;
	}

	public String getJob_eff_flag() { return job_eff_flag; }
	public void setJob_eff_flag(String job_eff_flag) {
		if(job_eff_flag==null) throw new BusinessException("Entity : JobInfo.job_eff_flag must not null!");
		this.job_eff_flag = job_eff_flag;
	}

	public String getFre_week() { return fre_week; }
	public void setFre_week(String fre_week) {
		if(fre_week==null) addNullValueField("fre_week");
		this.fre_week = fre_week;
	}

	public String getPro_para() { return pro_para; }
	public void setPro_para(String pro_para) {
		if(pro_para==null) addNullValueField("pro_para");
		this.pro_para = pro_para;
	}

	public String getJob_e_date() { return job_e_date; }
	public void setJob_e_date(String job_e_date) {
		if(job_e_date==null) throw new BusinessException("Entity : JobInfo.job_e_date must not null!");
		this.job_e_date = job_e_date;
	}

	public String getRun_way() { return run_way; }
	public void setRun_way(String run_way) {
		if(run_way==null) throw new BusinessException("Entity : JobInfo.run_way must not null!");
		this.run_way = run_way;
	}

	public BigDecimal getCreate_id() { return create_id; }
	public void setCreate_id(BigDecimal create_id) {
		if(create_id==null) throw new BusinessException("Entity : JobInfo.create_id must not null!");
		this.create_id = create_id;
	}

	public String getJob_name() { return job_name; }
	public void setJob_name(String job_name) {
		if(job_name==null) throw new BusinessException("Entity : JobInfo.job_name must not null!");
		this.job_name = job_name;
	}

	public String getCron_expression() { return cron_expression; }
	public void setCron_expression(String cron_expression) {
		if(cron_expression==null) addNullValueField("cron_expression");
		this.cron_expression = cron_expression;
	}

	public BigDecimal getProject_id() { return project_id; }
	public void setProject_id(BigDecimal project_id) {
		if(project_id==null) throw new BusinessException("Entity : JobInfo.project_id must not null!");
		this.project_id = project_id;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : JobInfo.user_id must not null!");
		this.user_id = user_id;
	}

	public BigDecimal getJob_id() { return job_id; }
	public void setJob_id(BigDecimal job_id) {
		if(job_id==null) throw new BusinessException("Entity : JobInfo.job_id must not null!");
		this.job_id = job_id;
	}

	public String getFre_day() { return fre_day; }
	public void setFre_day(String fre_day) {
		if(fre_day==null) addNullValueField("fre_day");
		this.fre_day = fre_day;
	}

	public String getDisp_time() { return disp_time; }
	public void setDisp_time(String disp_time) {
		if(disp_time==null) throw new BusinessException("Entity : JobInfo.disp_time must not null!");
		this.disp_time = disp_time;
	}

	public String getJob_path() { return job_path; }
	public void setJob_path(String job_path) {
		if(job_path==null) addNullValueField("job_path");
		this.job_path = job_path;
	}

}