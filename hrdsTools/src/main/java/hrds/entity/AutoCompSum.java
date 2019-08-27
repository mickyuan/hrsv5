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
@Table(tableName = "auto_comp_sum")
public class AutoCompSum extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_comp_sum";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("component_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal component_id;
	private String create_time;
	private String component_name;
	private String component_buffer;
	private String data_source;
	private String last_update_time;
	private String update_user;
	private String component_status;
	private String sources_obj;
	private String chart_type;
	private String chart_theme;
	private String user_id;
	private String component_desc;
	private String exe_sql;
	private String background;
	private String create_user;
	private String create_date;
	private String condition_sql;
	private String last_update_date;

	public BigDecimal getComponent_id() { return component_id; }
	public void setComponent_id(BigDecimal component_id) {
		if(component_id==null) throw new BusinessException("Entity : AutoCompSum.component_id must not null!");
		this.component_id = component_id;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : AutoCompSum.create_time must not null!");
		this.create_time = create_time;
	}

	public String getComponent_name() { return component_name; }
	public void setComponent_name(String component_name) {
		if(component_name==null) throw new BusinessException("Entity : AutoCompSum.component_name must not null!");
		this.component_name = component_name;
	}

	public String getComponent_buffer() { return component_buffer; }
	public void setComponent_buffer(String component_buffer) {
		if(component_buffer==null) addNullValueField("component_buffer");
		this.component_buffer = component_buffer;
	}

	public String getData_source() { return data_source; }
	public void setData_source(String data_source) {
		if(data_source==null) throw new BusinessException("Entity : AutoCompSum.data_source must not null!");
		this.data_source = data_source;
	}

	public String getLast_update_time() { return last_update_time; }
	public void setLast_update_time(String last_update_time) {
		if(last_update_time==null) addNullValueField("last_update_time");
		this.last_update_time = last_update_time;
	}

	public String getUpdate_user() { return update_user; }
	public void setUpdate_user(String update_user) {
		if(update_user==null) addNullValueField("update_user");
		this.update_user = update_user;
	}

	public String getComponent_status() { return component_status; }
	public void setComponent_status(String component_status) {
		if(component_status==null) throw new BusinessException("Entity : AutoCompSum.component_status must not null!");
		this.component_status = component_status;
	}

	public String getSources_obj() { return sources_obj; }
	public void setSources_obj(String sources_obj) {
		if(sources_obj==null) addNullValueField("sources_obj");
		this.sources_obj = sources_obj;
	}

	public String getChart_type() { return chart_type; }
	public void setChart_type(String chart_type) {
		if(chart_type==null) throw new BusinessException("Entity : AutoCompSum.chart_type must not null!");
		this.chart_type = chart_type;
	}

	public String getChart_theme() { return chart_theme; }
	public void setChart_theme(String chart_theme) {
		if(chart_theme==null) addNullValueField("chart_theme");
		this.chart_theme = chart_theme;
	}

	public String getUser_id() { return user_id; }
	public void setUser_id(String user_id) {
		if(user_id==null) addNullValueField("user_id");
		this.user_id = user_id;
	}

	public String getComponent_desc() { return component_desc; }
	public void setComponent_desc(String component_desc) {
		if(component_desc==null) addNullValueField("component_desc");
		this.component_desc = component_desc;
	}

	public String getExe_sql() { return exe_sql; }
	public void setExe_sql(String exe_sql) {
		if(exe_sql==null) addNullValueField("exe_sql");
		this.exe_sql = exe_sql;
	}

	public String getBackground() { return background; }
	public void setBackground(String background) {
		if(background==null) addNullValueField("background");
		this.background = background;
	}

	public String getCreate_user() { return create_user; }
	public void setCreate_user(String create_user) {
		if(create_user==null) addNullValueField("create_user");
		this.create_user = create_user;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : AutoCompSum.create_date must not null!");
		this.create_date = create_date;
	}

	public String getCondition_sql() { return condition_sql; }
	public void setCondition_sql(String condition_sql) {
		if(condition_sql==null) addNullValueField("condition_sql");
		this.condition_sql = condition_sql;
	}

	public String getLast_update_date() { return last_update_date; }
	public void setLast_update_date(String last_update_date) {
		if(last_update_date==null) addNullValueField("last_update_date");
		this.last_update_date = last_update_date;
	}

}