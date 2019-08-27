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
@Table(tableName = "auto_tp_info")
public class AutoTpInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_tp_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("template_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String template_sql;
	private String last_update_time;
	private String template_name;
	private String template_status;
	private String update_user;
	private String create_time;
	private BigDecimal template_id;
	private String template_desc;
	private String create_user;
	private String create_date;
	private String data_source;
	private String last_update_date;

	public String getTemplate_sql() { return template_sql; }
	public void setTemplate_sql(String template_sql) {
		if(template_sql==null) addNullValueField("template_sql");
		this.template_sql = template_sql;
	}

	public String getLast_update_time() { return last_update_time; }
	public void setLast_update_time(String last_update_time) {
		if(last_update_time==null) addNullValueField("last_update_time");
		this.last_update_time = last_update_time;
	}

	public String getTemplate_name() { return template_name; }
	public void setTemplate_name(String template_name) {
		if(template_name==null) addNullValueField("template_name");
		this.template_name = template_name;
	}

	public String getTemplate_status() { return template_status; }
	public void setTemplate_status(String template_status) {
		if(template_status==null) throw new BusinessException("Entity : AutoTpInfo.template_status must not null!");
		this.template_status = template_status;
	}

	public String getUpdate_user() { return update_user; }
	public void setUpdate_user(String update_user) {
		if(update_user==null) addNullValueField("update_user");
		this.update_user = update_user;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) addNullValueField("create_time");
		this.create_time = create_time;
	}

	public BigDecimal getTemplate_id() { return template_id; }
	public void setTemplate_id(BigDecimal template_id) {
		if(template_id==null) throw new BusinessException("Entity : AutoTpInfo.template_id must not null!");
		this.template_id = template_id;
	}

	public String getTemplate_desc() { return template_desc; }
	public void setTemplate_desc(String template_desc) {
		if(template_desc==null) addNullValueField("template_desc");
		this.template_desc = template_desc;
	}

	public String getCreate_user() { return create_user; }
	public void setCreate_user(String create_user) {
		if(create_user==null) addNullValueField("create_user");
		this.create_user = create_user;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) addNullValueField("create_date");
		this.create_date = create_date;
	}

	public String getData_source() { return data_source; }
	public void setData_source(String data_source) {
		if(data_source==null) throw new BusinessException("Entity : AutoTpInfo.data_source must not null!");
		this.data_source = data_source;
	}

	public String getLast_update_date() { return last_update_date; }
	public void setLast_update_date(String last_update_date) {
		if(last_update_date==null) addNullValueField("last_update_date");
		this.last_update_date = last_update_date;
	}

}