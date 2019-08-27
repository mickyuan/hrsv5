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
@Table(tableName = "configure_msg")
public class ConfigureMsg extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "configure_msg";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cmsg_id");
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
	private String agent_time;
	private String create_time;
	private BigDecimal pages_count;
	private String cmsg_remark;
	private String fre_month;
	private String configure_name;
	private BigDecimal url_count;
	private String stop_conditions;
	private String fre_week;
	private BigDecimal cmsg_id;
	private String agent_date;
	private String files_type;
	private String cron_expression;
	private String fre_day;
	private String execute_time;
	private String grab_time;
	private String is_custom;
	private String create_date;
	private BigDecimal download_size;
	private String start_date;

	public String getEnd_date() { return end_date; }
	public void setEnd_date(String end_date) {
		if(end_date==null) throw new BusinessException("Entity : ConfigureMsg.end_date must not null!");
		this.end_date = end_date;
	}

	public String getAgent_time() { return agent_time; }
	public void setAgent_time(String agent_time) {
		if(agent_time==null) throw new BusinessException("Entity : ConfigureMsg.agent_time must not null!");
		this.agent_time = agent_time;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : ConfigureMsg.create_time must not null!");
		this.create_time = create_time;
	}

	public BigDecimal getPages_count() { return pages_count; }
	public void setPages_count(BigDecimal pages_count) {
		if(pages_count==null) addNullValueField("pages_count");
		this.pages_count = pages_count;
	}

	public String getCmsg_remark() { return cmsg_remark; }
	public void setCmsg_remark(String cmsg_remark) {
		if(cmsg_remark==null) addNullValueField("cmsg_remark");
		this.cmsg_remark = cmsg_remark;
	}

	public String getFre_month() { return fre_month; }
	public void setFre_month(String fre_month) {
		if(fre_month==null) addNullValueField("fre_month");
		this.fre_month = fre_month;
	}

	public String getConfigure_name() { return configure_name; }
	public void setConfigure_name(String configure_name) {
		if(configure_name==null) throw new BusinessException("Entity : ConfigureMsg.configure_name must not null!");
		this.configure_name = configure_name;
	}

	public BigDecimal getUrl_count() { return url_count; }
	public void setUrl_count(BigDecimal url_count) {
		if(url_count==null) addNullValueField("url_count");
		this.url_count = url_count;
	}

	public String getStop_conditions() { return stop_conditions; }
	public void setStop_conditions(String stop_conditions) {
		if(stop_conditions==null) addNullValueField("stop_conditions");
		this.stop_conditions = stop_conditions;
	}

	public String getFre_week() { return fre_week; }
	public void setFre_week(String fre_week) {
		if(fre_week==null) addNullValueField("fre_week");
		this.fre_week = fre_week;
	}

	public BigDecimal getCmsg_id() { return cmsg_id; }
	public void setCmsg_id(BigDecimal cmsg_id) {
		if(cmsg_id==null) throw new BusinessException("Entity : ConfigureMsg.cmsg_id must not null!");
		this.cmsg_id = cmsg_id;
	}

	public String getAgent_date() { return agent_date; }
	public void setAgent_date(String agent_date) {
		if(agent_date==null) throw new BusinessException("Entity : ConfigureMsg.agent_date must not null!");
		this.agent_date = agent_date;
	}

	public String getFiles_type() { return files_type; }
	public void setFiles_type(String files_type) {
		if(files_type==null) addNullValueField("files_type");
		this.files_type = files_type;
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
		if(execute_time==null) throw new BusinessException("Entity : ConfigureMsg.execute_time must not null!");
		this.execute_time = execute_time;
	}

	public String getGrab_time() { return grab_time; }
	public void setGrab_time(String grab_time) {
		if(grab_time==null) throw new BusinessException("Entity : ConfigureMsg.grab_time must not null!");
		this.grab_time = grab_time;
	}

	public String getIs_custom() { return is_custom; }
	public void setIs_custom(String is_custom) {
		if(is_custom==null) throw new BusinessException("Entity : ConfigureMsg.is_custom must not null!");
		this.is_custom = is_custom;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : ConfigureMsg.create_date must not null!");
		this.create_date = create_date;
	}

	public BigDecimal getDownload_size() { return download_size; }
	public void setDownload_size(BigDecimal download_size) {
		if(download_size==null) addNullValueField("download_size");
		this.download_size = download_size;
	}

	public String getStart_date() { return start_date; }
	public void setStart_date(String start_date) {
		if(start_date==null) throw new BusinessException("Entity : ConfigureMsg.start_date must not null!");
		this.start_date = start_date;
	}

}