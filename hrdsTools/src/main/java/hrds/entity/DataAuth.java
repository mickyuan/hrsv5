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
@Table(tableName = "data_auth")
public class DataAuth extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "data_auth";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("da_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal collect_set_id;
	private String auth_type;
	private BigDecimal dep_id;
	private BigDecimal agent_id;
	private String audit_date;
	private String apply_time;
	private String audit_time;
	private BigDecimal audit_userid;
	private String audit_name;
	private String apply_date;
	private BigDecimal da_id;
	private BigDecimal user_id;
	private String file_id;
	private BigDecimal source_id;
	private String apply_type;

	public BigDecimal getCollect_set_id() { return collect_set_id; }
	public void setCollect_set_id(BigDecimal collect_set_id) {
		if(collect_set_id==null) throw new BusinessException("Entity : DataAuth.collect_set_id must not null!");
		this.collect_set_id = collect_set_id;
	}

	public String getAuth_type() { return auth_type; }
	public void setAuth_type(String auth_type) {
		if(auth_type==null) throw new BusinessException("Entity : DataAuth.auth_type must not null!");
		this.auth_type = auth_type;
	}

	public BigDecimal getDep_id() { return dep_id; }
	public void setDep_id(BigDecimal dep_id) {
		if(dep_id==null) throw new BusinessException("Entity : DataAuth.dep_id must not null!");
		this.dep_id = dep_id;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : DataAuth.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public String getAudit_date() { return audit_date; }
	public void setAudit_date(String audit_date) {
		if(audit_date==null) addNullValueField("audit_date");
		this.audit_date = audit_date;
	}

	public String getApply_time() { return apply_time; }
	public void setApply_time(String apply_time) {
		if(apply_time==null) throw new BusinessException("Entity : DataAuth.apply_time must not null!");
		this.apply_time = apply_time;
	}

	public String getAudit_time() { return audit_time; }
	public void setAudit_time(String audit_time) {
		if(audit_time==null) addNullValueField("audit_time");
		this.audit_time = audit_time;
	}

	public BigDecimal getAudit_userid() { return audit_userid; }
	public void setAudit_userid(BigDecimal audit_userid) {
		if(audit_userid==null) addNullValueField("audit_userid");
		this.audit_userid = audit_userid;
	}

	public String getAudit_name() { return audit_name; }
	public void setAudit_name(String audit_name) {
		if(audit_name==null) addNullValueField("audit_name");
		this.audit_name = audit_name;
	}

	public String getApply_date() { return apply_date; }
	public void setApply_date(String apply_date) {
		if(apply_date==null) throw new BusinessException("Entity : DataAuth.apply_date must not null!");
		this.apply_date = apply_date;
	}

	public BigDecimal getDa_id() { return da_id; }
	public void setDa_id(BigDecimal da_id) {
		if(da_id==null) throw new BusinessException("Entity : DataAuth.da_id must not null!");
		this.da_id = da_id;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : DataAuth.user_id must not null!");
		this.user_id = user_id;
	}

	public String getFile_id() { return file_id; }
	public void setFile_id(String file_id) {
		if(file_id==null) throw new BusinessException("Entity : DataAuth.file_id must not null!");
		this.file_id = file_id;
	}

	public BigDecimal getSource_id() { return source_id; }
	public void setSource_id(BigDecimal source_id) {
		if(source_id==null) throw new BusinessException("Entity : DataAuth.source_id must not null!");
		this.source_id = source_id;
	}

	public String getApply_type() { return apply_type; }
	public void setApply_type(String apply_type) {
		if(apply_type==null) throw new BusinessException("Entity : DataAuth.apply_type must not null!");
		this.apply_type = apply_type;
	}

}