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
@Table(tableName = "collect_frequency")
public class CollectFrequency extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "collect_frequency";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cf_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String end_date;
	private BigDecimal collect_set_id;
	private String file_path;
	private String agent_time;
	private String create_time;
	private BigDecimal agent_id;
	private BigDecimal rely_job_id;
	private String fre_month;
	private String comp_id;
	private String cf_jobnum;
	private String fre_week;
	private String run_way;
	private String agent_date;
	private BigDecimal cf_id;
	private String collect_type;
	private String cron_expression;
	private String fre_day;
	private String execute_time;
	private String create_date;
	private String start_date;

	public String getEnd_date() { return end_date; }
	public void setEnd_date(String end_date) {
		if(end_date==null) throw new BusinessException("Entity : CollectFrequency.end_date must not null!");
		this.end_date = end_date;
	}

	public BigDecimal getCollect_set_id() { return collect_set_id; }
	public void setCollect_set_id(BigDecimal collect_set_id) {
		if(collect_set_id==null) throw new BusinessException("Entity : CollectFrequency.collect_set_id must not null!");
		this.collect_set_id = collect_set_id;
	}

	public String getFile_path() { return file_path; }
	public void setFile_path(String file_path) {
		if(file_path==null) addNullValueField("file_path");
		this.file_path = file_path;
	}

	public String getAgent_time() { return agent_time; }
	public void setAgent_time(String agent_time) {
		if(agent_time==null) addNullValueField("agent_time");
		this.agent_time = agent_time;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) addNullValueField("create_time");
		this.create_time = create_time;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : CollectFrequency.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public BigDecimal getRely_job_id() { return rely_job_id; }
	public void setRely_job_id(BigDecimal rely_job_id) {
		if(rely_job_id==null) addNullValueField("rely_job_id");
		this.rely_job_id = rely_job_id;
	}

	public String getFre_month() { return fre_month; }
	public void setFre_month(String fre_month) {
		if(fre_month==null) addNullValueField("fre_month");
		this.fre_month = fre_month;
	}

	public String getComp_id() { return comp_id; }
	public void setComp_id(String comp_id) {
		if(comp_id==null) throw new BusinessException("Entity : CollectFrequency.comp_id must not null!");
		this.comp_id = comp_id;
	}

	public String getCf_jobnum() { return cf_jobnum; }
	public void setCf_jobnum(String cf_jobnum) {
		if(cf_jobnum==null) addNullValueField("cf_jobnum");
		this.cf_jobnum = cf_jobnum;
	}

	public String getFre_week() { return fre_week; }
	public void setFre_week(String fre_week) {
		if(fre_week==null) addNullValueField("fre_week");
		this.fre_week = fre_week;
	}

	public String getRun_way() { return run_way; }
	public void setRun_way(String run_way) {
		if(run_way==null) addNullValueField("run_way");
		this.run_way = run_way;
	}

	public String getAgent_date() { return agent_date; }
	public void setAgent_date(String agent_date) {
		if(agent_date==null) throw new BusinessException("Entity : CollectFrequency.agent_date must not null!");
		this.agent_date = agent_date;
	}

	public BigDecimal getCf_id() { return cf_id; }
	public void setCf_id(BigDecimal cf_id) {
		if(cf_id==null) throw new BusinessException("Entity : CollectFrequency.cf_id must not null!");
		this.cf_id = cf_id;
	}

	public String getCollect_type() { return collect_type; }
	public void setCollect_type(String collect_type) {
		if(collect_type==null) throw new BusinessException("Entity : CollectFrequency.collect_type must not null!");
		this.collect_type = collect_type;
	}

	public String getCron_expression() { return cron_expression; }
	public void setCron_expression(String cron_expression) {
		if(cron_expression==null) addNullValueField("cron_expression");
		this.cron_expression = cron_expression;
	}

	public String getFre_day() { return fre_day; }
	public void setFre_day(String fre_day) {
		if(fre_day==null) addNullValueField("fre_day");
		this.fre_day = fre_day;
	}

	public String getExecute_time() { return execute_time; }
	public void setExecute_time(String execute_time) {
		if(execute_time==null) addNullValueField("execute_time");
		this.execute_time = execute_time;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) addNullValueField("create_date");
		this.create_date = create_date;
	}

	public String getStart_date() { return start_date; }
	public void setStart_date(String start_date) {
		if(start_date==null) addNullValueField("start_date");
		this.start_date = start_date;
	}

}