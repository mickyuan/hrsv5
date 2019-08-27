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
@Table(tableName = "creeper_source")
public class CreeperSource extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "creeper_source";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cs_id");
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
	private String file_path;
	private BigDecimal cs_id;
	private String agent_time;
	private String create_time;
	private String fre_month;
	private String source_ip;
	private String fre_week;
	private BigDecimal create_id;
	private String agent_date;
	private String cron_expression;
	private BigDecimal user_id;
	private String fre_day;
	private String execute_time;
	private String source_port;
	private String cs_remark;
	private String create_date;
	private String source_name;
	private String source_status;
	private String start_date;

	public String getEnd_date() { return end_date; }
	public void setEnd_date(String end_date) {
		if(end_date==null) throw new BusinessException("Entity : CreeperSource.end_date must not null!");
		this.end_date = end_date;
	}

	public String getFile_path() { return file_path; }
	public void setFile_path(String file_path) {
		if(file_path==null) addNullValueField("file_path");
		this.file_path = file_path;
	}

	public BigDecimal getCs_id() { return cs_id; }
	public void setCs_id(BigDecimal cs_id) {
		if(cs_id==null) throw new BusinessException("Entity : CreeperSource.cs_id must not null!");
		this.cs_id = cs_id;
	}

	public String getAgent_time() { return agent_time; }
	public void setAgent_time(String agent_time) {
		if(agent_time==null) addNullValueField("agent_time");
		this.agent_time = agent_time;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : CreeperSource.create_time must not null!");
		this.create_time = create_time;
	}

	public String getFre_month() { return fre_month; }
	public void setFre_month(String fre_month) {
		if(fre_month==null) addNullValueField("fre_month");
		this.fre_month = fre_month;
	}

	public String getSource_ip() { return source_ip; }
	public void setSource_ip(String source_ip) {
		if(source_ip==null) throw new BusinessException("Entity : CreeperSource.source_ip must not null!");
		this.source_ip = source_ip;
	}

	public String getFre_week() { return fre_week; }
	public void setFre_week(String fre_week) {
		if(fre_week==null) addNullValueField("fre_week");
		this.fre_week = fre_week;
	}

	public BigDecimal getCreate_id() { return create_id; }
	public void setCreate_id(BigDecimal create_id) {
		if(create_id==null) throw new BusinessException("Entity : CreeperSource.create_id must not null!");
		this.create_id = create_id;
	}

	public String getAgent_date() { return agent_date; }
	public void setAgent_date(String agent_date) {
		if(agent_date==null) addNullValueField("agent_date");
		this.agent_date = agent_date;
	}

	public String getCron_expression() { return cron_expression; }
	public void setCron_expression(String cron_expression) {
		if(cron_expression==null) throw new BusinessException("Entity : CreeperSource.cron_expression must not null!");
		this.cron_expression = cron_expression;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : CreeperSource.user_id must not null!");
		this.user_id = user_id;
	}

	public String getFre_day() { return fre_day; }
	public void setFre_day(String fre_day) {
		if(fre_day==null) addNullValueField("fre_day");
		this.fre_day = fre_day;
	}

	public String getExecute_time() { return execute_time; }
	public void setExecute_time(String execute_time) {
		if(execute_time==null) throw new BusinessException("Entity : CreeperSource.execute_time must not null!");
		this.execute_time = execute_time;
	}

	public String getSource_port() { return source_port; }
	public void setSource_port(String source_port) {
		if(source_port==null) throw new BusinessException("Entity : CreeperSource.source_port must not null!");
		this.source_port = source_port;
	}

	public String getCs_remark() { return cs_remark; }
	public void setCs_remark(String cs_remark) {
		if(cs_remark==null) addNullValueField("cs_remark");
		this.cs_remark = cs_remark;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : CreeperSource.create_date must not null!");
		this.create_date = create_date;
	}

	public String getSource_name() { return source_name; }
	public void setSource_name(String source_name) {
		if(source_name==null) throw new BusinessException("Entity : CreeperSource.source_name must not null!");
		this.source_name = source_name;
	}

	public String getSource_status() { return source_status; }
	public void setSource_status(String source_status) {
		if(source_status==null) throw new BusinessException("Entity : CreeperSource.source_status must not null!");
		this.source_status = source_status;
	}

	public String getStart_date() { return start_date; }
	public void setStart_date(String start_date) {
		if(start_date==null) throw new BusinessException("Entity : CreeperSource.start_date must not null!");
		this.start_date = start_date;
	}

}