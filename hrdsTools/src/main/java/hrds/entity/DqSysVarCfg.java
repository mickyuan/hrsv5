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
@Table(tableName = "dq_sys_var_cfg")
public class DqSysVarCfg extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_sys_var_cfg";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sys_var_id");
		__tmpPKS.add("var_name");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal sys_var_id;
	private String app_updt_ti;
	private String var_value;
	private BigDecimal user_id;
	private String var_name;
	private String app_updt_dt;

	public BigDecimal getSys_var_id() { return sys_var_id; }
	public void setSys_var_id(BigDecimal sys_var_id) {
		if(sys_var_id==null) throw new BusinessException("Entity : DqSysVarCfg.sys_var_id must not null!");
		this.sys_var_id = sys_var_id;
	}

	public String getApp_updt_ti() { return app_updt_ti; }
	public void setApp_updt_ti(String app_updt_ti) {
		if(app_updt_ti==null) throw new BusinessException("Entity : DqSysVarCfg.app_updt_ti must not null!");
		this.app_updt_ti = app_updt_ti;
	}

	public String getVar_value() { return var_value; }
	public void setVar_value(String var_value) {
		if(var_value==null) throw new BusinessException("Entity : DqSysVarCfg.var_value must not null!");
		this.var_value = var_value;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : DqSysVarCfg.user_id must not null!");
		this.user_id = user_id;
	}

	public String getVar_name() { return var_name; }
	public void setVar_name(String var_name) {
		if(var_name==null) throw new BusinessException("Entity : DqSysVarCfg.var_name must not null!");
		this.var_name = var_name;
	}

	public String getApp_updt_dt() { return app_updt_dt; }
	public void setApp_updt_dt(String app_updt_dt) {
		if(app_updt_dt==null) throw new BusinessException("Entity : DqSysVarCfg.app_updt_dt must not null!");
		this.app_updt_dt = app_updt_dt;
	}

}