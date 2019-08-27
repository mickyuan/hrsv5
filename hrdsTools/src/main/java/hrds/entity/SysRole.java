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
@Table(tableName = "sys_role")
public class SysRole extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sys_role";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("role_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String role_name;
	private String role_id;
	private String role_remark;

	public String getRole_name() { return role_name; }
	public void setRole_name(String role_name) {
		if(role_name==null) throw new BusinessException("Entity : SysRole.role_name must not null!");
		this.role_name = role_name;
	}

	public String getRole_id() { return role_id; }
	public void setRole_id(String role_id) {
		if(role_id==null) throw new BusinessException("Entity : SysRole.role_id must not null!");
		this.role_id = role_id;
	}

	public String getRole_remark() { return role_remark; }
	public void setRole_remark(String role_remark) {
		if(role_remark==null) addNullValueField("role_remark");
		this.role_remark = role_remark;
	}

}