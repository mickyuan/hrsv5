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
@Table(tableName = "auto_fetch_sum")
public class AutoFetchSum extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_fetch_sum";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("fetch_sum_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String fetch_desc;
	private String create_time;
	private String fetch_name;
	private String fetch_sql;
	private String fetch_status;
	private String last_update_time;
	private String update_user;
	private String user_id;
	private BigDecimal template_id;
	private String create_user;
	private String create_date;
	private BigDecimal fetch_sum_id;
	private String last_update_date;

	public String getFetch_desc() { return fetch_desc; }
	public void setFetch_desc(String fetch_desc) {
		if(fetch_desc==null) addNullValueField("fetch_desc");
		this.fetch_desc = fetch_desc;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) addNullValueField("create_time");
		this.create_time = create_time;
	}

	public String getFetch_name() { return fetch_name; }
	public void setFetch_name(String fetch_name) {
		if(fetch_name==null) addNullValueField("fetch_name");
		this.fetch_name = fetch_name;
	}

	public String getFetch_sql() { return fetch_sql; }
	public void setFetch_sql(String fetch_sql) {
		if(fetch_sql==null) addNullValueField("fetch_sql");
		this.fetch_sql = fetch_sql;
	}

	public String getFetch_status() { return fetch_status; }
	public void setFetch_status(String fetch_status) {
		if(fetch_status==null) throw new BusinessException("Entity : AutoFetchSum.fetch_status must not null!");
		this.fetch_status = fetch_status;
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

	public String getUser_id() { return user_id; }
	public void setUser_id(String user_id) {
		if(user_id==null) addNullValueField("user_id");
		this.user_id = user_id;
	}

	public BigDecimal getTemplate_id() { return template_id; }
	public void setTemplate_id(BigDecimal template_id) {
		if(template_id==null) throw new BusinessException("Entity : AutoFetchSum.template_id must not null!");
		this.template_id = template_id;
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

	public BigDecimal getFetch_sum_id() { return fetch_sum_id; }
	public void setFetch_sum_id(BigDecimal fetch_sum_id) {
		if(fetch_sum_id==null) throw new BusinessException("Entity : AutoFetchSum.fetch_sum_id must not null!");
		this.fetch_sum_id = fetch_sum_id;
	}

	public String getLast_update_date() { return last_update_date; }
	public void setLast_update_date(String last_update_date) {
		if(last_update_date==null) addNullValueField("last_update_date");
		this.last_update_date = last_update_date;
	}

}