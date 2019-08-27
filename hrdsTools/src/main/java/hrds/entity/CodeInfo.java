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
@Table(tableName = "code_info")
public class CodeInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "code_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ci_sp_code");
		__tmpPKS.add("ci_sp_class");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String ci_sp_code;
	private String ci_sp_remark;
	private String ci_sp_name;
	private String ci_sp_class;
	private String ci_sp_classname;

	public String getCi_sp_code() { return ci_sp_code; }
	public void setCi_sp_code(String ci_sp_code) {
		if(ci_sp_code==null) throw new BusinessException("Entity : CodeInfo.ci_sp_code must not null!");
		this.ci_sp_code = ci_sp_code;
	}

	public String getCi_sp_remark() { return ci_sp_remark; }
	public void setCi_sp_remark(String ci_sp_remark) {
		if(ci_sp_remark==null) addNullValueField("ci_sp_remark");
		this.ci_sp_remark = ci_sp_remark;
	}

	public String getCi_sp_name() { return ci_sp_name; }
	public void setCi_sp_name(String ci_sp_name) {
		if(ci_sp_name==null) throw new BusinessException("Entity : CodeInfo.ci_sp_name must not null!");
		this.ci_sp_name = ci_sp_name;
	}

	public String getCi_sp_class() { return ci_sp_class; }
	public void setCi_sp_class(String ci_sp_class) {
		if(ci_sp_class==null) throw new BusinessException("Entity : CodeInfo.ci_sp_class must not null!");
		this.ci_sp_class = ci_sp_class;
	}

	public String getCi_sp_classname() { return ci_sp_classname; }
	public void setCi_sp_classname(String ci_sp_classname) {
		if(ci_sp_classname==null) throw new BusinessException("Entity : CodeInfo.ci_sp_classname must not null!");
		this.ci_sp_classname = ci_sp_classname;
	}

}