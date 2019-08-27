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
@Table(tableName = "sys_user")
public class SysUser extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sys_user";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("user_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal maximum_job;
	private String user_password;
	private String user_email;
	private BigDecimal dep_id;
	private String create_time;
	private String user_name;
	private String quota_space;
	private String login_date;
	private String login_ip;
	private String update_date;
	private String useris_admin;
	private String user_state;
	private String token;
	private String user_remark;
	private String update_time;
	private String user_type;
	private String user_priority;
	private BigDecimal create_id;
	private BigDecimal user_id;
	private String role_id;
	private String user_mobile;
	private String usertype_group;
	private String valid_time;
	private String create_date;

	public BigDecimal getMaximum_job() { return maximum_job; }
	public void setMaximum_job(BigDecimal maximum_job) {
		if(maximum_job==null) addNullValueField("maximum_job");
		this.maximum_job = maximum_job;
	}

	public String getUser_password() { return user_password; }
	public void setUser_password(String user_password) {
		if(user_password==null) throw new BusinessException("Entity : SysUser.user_password must not null!");
		this.user_password = user_password;
	}

	public String getUser_email() { return user_email; }
	public void setUser_email(String user_email) {
		if(user_email==null) addNullValueField("user_email");
		this.user_email = user_email;
	}

	public BigDecimal getDep_id() { return dep_id; }
	public void setDep_id(BigDecimal dep_id) {
		if(dep_id==null) addNullValueField("dep_id");
		this.dep_id = dep_id;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) addNullValueField("create_time");
		this.create_time = create_time;
	}

	public String getUser_name() { return user_name; }
	public void setUser_name(String user_name) {
		if(user_name==null) throw new BusinessException("Entity : SysUser.user_name must not null!");
		this.user_name = user_name;
	}

	public String getQuota_space() { return quota_space; }
	public void setQuota_space(String quota_space) {
		if(quota_space==null) addNullValueField("quota_space");
		this.quota_space = quota_space;
	}

	public String getLogin_date() { return login_date; }
	public void setLogin_date(String login_date) {
		if(login_date==null) addNullValueField("login_date");
		this.login_date = login_date;
	}

	public String getLogin_ip() { return login_ip; }
	public void setLogin_ip(String login_ip) {
		if(login_ip==null) addNullValueField("login_ip");
		this.login_ip = login_ip;
	}

	public String getUpdate_date() { return update_date; }
	public void setUpdate_date(String update_date) {
		if(update_date==null) addNullValueField("update_date");
		this.update_date = update_date;
	}

	public String getUseris_admin() { return useris_admin; }
	public void setUseris_admin(String useris_admin) {
		if(useris_admin==null) throw new BusinessException("Entity : SysUser.useris_admin must not null!");
		this.useris_admin = useris_admin;
	}

	public String getUser_state() { return user_state; }
	public void setUser_state(String user_state) {
		if(user_state==null) throw new BusinessException("Entity : SysUser.user_state must not null!");
		this.user_state = user_state;
	}

	public String getToken() { return token; }
	public void setToken(String token) {
		if(token==null) throw new BusinessException("Entity : SysUser.token must not null!");
		this.token = token;
	}

	public String getUser_remark() { return user_remark; }
	public void setUser_remark(String user_remark) {
		if(user_remark==null) addNullValueField("user_remark");
		this.user_remark = user_remark;
	}

	public String getUpdate_time() { return update_time; }
	public void setUpdate_time(String update_time) {
		if(update_time==null) addNullValueField("update_time");
		this.update_time = update_time;
	}

	public String getUser_type() { return user_type; }
	public void setUser_type(String user_type) {
		if(user_type==null) addNullValueField("user_type");
		this.user_type = user_type;
	}

	public String getUser_priority() { return user_priority; }
	public void setUser_priority(String user_priority) {
		if(user_priority==null) addNullValueField("user_priority");
		this.user_priority = user_priority;
	}

	public BigDecimal getCreate_id() { return create_id; }
	public void setCreate_id(BigDecimal create_id) {
		if(create_id==null) throw new BusinessException("Entity : SysUser.create_id must not null!");
		this.create_id = create_id;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : SysUser.user_id must not null!");
		this.user_id = user_id;
	}

	public String getRole_id() { return role_id; }
	public void setRole_id(String role_id) {
		if(role_id==null) throw new BusinessException("Entity : SysUser.role_id must not null!");
		this.role_id = role_id;
	}

	public String getUser_mobile() { return user_mobile; }
	public void setUser_mobile(String user_mobile) {
		if(user_mobile==null) addNullValueField("user_mobile");
		this.user_mobile = user_mobile;
	}

	public String getUsertype_group() { return usertype_group; }
	public void setUsertype_group(String usertype_group) {
		if(usertype_group==null) addNullValueField("usertype_group");
		this.usertype_group = usertype_group;
	}

	public String getValid_time() { return valid_time; }
	public void setValid_time(String valid_time) {
		if(valid_time==null) throw new BusinessException("Entity : SysUser.valid_time must not null!");
		this.valid_time = valid_time;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : SysUser.create_date must not null!");
		this.create_date = create_date;
	}

}