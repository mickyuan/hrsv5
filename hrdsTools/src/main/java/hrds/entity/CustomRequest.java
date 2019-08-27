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
@Table(tableName = "custom_request")
public class CustomRequest extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "custom_request";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cr_id");
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
	private BigDecimal cs_id;
	private BigDecimal cr_id;
	private String create_time;
	private String fre_month;
	private String url;
	private String fre_week;
	private BigDecimal create_id;
	private String cron_expression;
	private String fre_day;
	private String execute_time;
	private String create_date;
	private String start_date;

	public String getEnd_date() { return end_date; }
	public void setEnd_date(String end_date) {
		if(end_date==null) throw new BusinessException("Entity : CustomRequest.end_date must not null!");
		this.end_date = end_date;
	}

	public BigDecimal getCs_id() { return cs_id; }
	public void setCs_id(BigDecimal cs_id) {
		if(cs_id==null) throw new BusinessException("Entity : CustomRequest.cs_id must not null!");
		this.cs_id = cs_id;
	}

	public BigDecimal getCr_id() { return cr_id; }
	public void setCr_id(BigDecimal cr_id) {
		if(cr_id==null) throw new BusinessException("Entity : CustomRequest.cr_id must not null!");
		this.cr_id = cr_id;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : CustomRequest.create_time must not null!");
		this.create_time = create_time;
	}

	public String getFre_month() { return fre_month; }
	public void setFre_month(String fre_month) {
		if(fre_month==null) addNullValueField("fre_month");
		this.fre_month = fre_month;
	}

	public String getUrl() { return url; }
	public void setUrl(String url) {
		if(url==null) addNullValueField("url");
		this.url = url;
	}

	public String getFre_week() { return fre_week; }
	public void setFre_week(String fre_week) {
		if(fre_week==null) addNullValueField("fre_week");
		this.fre_week = fre_week;
	}

	public BigDecimal getCreate_id() { return create_id; }
	public void setCreate_id(BigDecimal create_id) {
		if(create_id==null) throw new BusinessException("Entity : CustomRequest.create_id must not null!");
		this.create_id = create_id;
	}

	public String getCron_expression() { return cron_expression; }
	public void setCron_expression(String cron_expression) {
		if(cron_expression==null) throw new BusinessException("Entity : CustomRequest.cron_expression must not null!");
		this.cron_expression = cron_expression;
	}

	public String getFre_day() { return fre_day; }
	public void setFre_day(String fre_day) {
		if(fre_day==null) addNullValueField("fre_day");
		this.fre_day = fre_day;
	}

	public String getExecute_time() { return execute_time; }
	public void setExecute_time(String execute_time) {
		if(execute_time==null) throw new BusinessException("Entity : CustomRequest.execute_time must not null!");
		this.execute_time = execute_time;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : CustomRequest.create_date must not null!");
		this.create_date = create_date;
	}

	public String getStart_date() { return start_date; }
	public void setStart_date(String start_date) {
		if(start_date==null) throw new BusinessException("Entity : CustomRequest.start_date must not null!");
		this.start_date = start_date;
	}

}