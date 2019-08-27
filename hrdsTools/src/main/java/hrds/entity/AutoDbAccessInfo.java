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
@Table(tableName = "auto_db_access_info")
public class AutoDbAccessInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_db_access_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("access_info_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String db_type;
	private BigDecimal component_id;
	private BigDecimal access_info_id;
	private String db_name;
	private String db_ip;
	private String db_user;
	private String jdbcurl;
	private String db_port;
	private String db_password;

	public String getDb_type() { return db_type; }
	public void setDb_type(String db_type) {
		if(db_type==null) throw new BusinessException("Entity : AutoDbAccessInfo.db_type must not null!");
		this.db_type = db_type;
	}

	public BigDecimal getComponent_id() { return component_id; }
	public void setComponent_id(BigDecimal component_id) {
		if(component_id==null) addNullValueField("component_id");
		this.component_id = component_id;
	}

	public BigDecimal getAccess_info_id() { return access_info_id; }
	public void setAccess_info_id(BigDecimal access_info_id) {
		if(access_info_id==null) throw new BusinessException("Entity : AutoDbAccessInfo.access_info_id must not null!");
		this.access_info_id = access_info_id;
	}

	public String getDb_name() { return db_name; }
	public void setDb_name(String db_name) {
		if(db_name==null) throw new BusinessException("Entity : AutoDbAccessInfo.db_name must not null!");
		this.db_name = db_name;
	}

	public String getDb_ip() { return db_ip; }
	public void setDb_ip(String db_ip) {
		if(db_ip==null) throw new BusinessException("Entity : AutoDbAccessInfo.db_ip must not null!");
		this.db_ip = db_ip;
	}

	public String getDb_user() { return db_user; }
	public void setDb_user(String db_user) {
		if(db_user==null) throw new BusinessException("Entity : AutoDbAccessInfo.db_user must not null!");
		this.db_user = db_user;
	}

	public String getJdbcurl() { return jdbcurl; }
	public void setJdbcurl(String jdbcurl) {
		if(jdbcurl==null) throw new BusinessException("Entity : AutoDbAccessInfo.jdbcurl must not null!");
		this.jdbcurl = jdbcurl;
	}

	public String getDb_port() { return db_port; }
	public void setDb_port(String db_port) {
		if(db_port==null) throw new BusinessException("Entity : AutoDbAccessInfo.db_port must not null!");
		this.db_port = db_port;
	}

	public String getDb_password() { return db_password; }
	public void setDb_password(String db_password) {
		if(db_password==null) addNullValueField("db_password");
		this.db_password = db_password;
	}

}