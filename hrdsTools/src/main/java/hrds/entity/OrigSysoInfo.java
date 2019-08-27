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
@Table(tableName = "orig_syso_info")
public class OrigSysoInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "orig_syso_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("orig_sys_code");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String orig_sys_name;
	private String orig_sys_remark;
	private String orig_sys_code;

	public String getOrig_sys_name() { return orig_sys_name; }
	public void setOrig_sys_name(String orig_sys_name) {
		if(orig_sys_name==null) throw new BusinessException("Entity : OrigSysoInfo.orig_sys_name must not null!");
		this.orig_sys_name = orig_sys_name;
	}

	public String getOrig_sys_remark() { return orig_sys_remark; }
	public void setOrig_sys_remark(String orig_sys_remark) {
		if(orig_sys_remark==null) addNullValueField("orig_sys_remark");
		this.orig_sys_remark = orig_sys_remark;
	}

	public String getOrig_sys_code() { return orig_sys_code; }
	public void setOrig_sys_code(String orig_sys_code) {
		if(orig_sys_code==null) throw new BusinessException("Entity : OrigSysoInfo.orig_sys_code must not null!");
		this.orig_sys_code = orig_sys_code;
	}

}