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
@Table(tableName = "cm_relation_cp")
public class CmRelationCp extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "cm_relation_cp";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cp_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal cp_id;
	private String cp_expressions;
	private String pc_remark;
	private String method_name;

	public BigDecimal getCp_id() { return cp_id; }
	public void setCp_id(BigDecimal cp_id) {
		if(cp_id==null) throw new BusinessException("Entity : CmRelationCp.cp_id must not null!");
		this.cp_id = cp_id;
	}

	public String getCp_expressions() { return cp_expressions; }
	public void setCp_expressions(String cp_expressions) {
		if(cp_expressions==null) addNullValueField("cp_expressions");
		this.cp_expressions = cp_expressions;
	}

	public String getPc_remark() { return pc_remark; }
	public void setPc_remark(String pc_remark) {
		if(pc_remark==null) addNullValueField("pc_remark");
		this.pc_remark = pc_remark;
	}

	public String getMethod_name() { return method_name; }
	public void setMethod_name(String method_name) {
		if(method_name==null) throw new BusinessException("Entity : CmRelationCp.method_name must not null!");
		this.method_name = method_name;
	}

}