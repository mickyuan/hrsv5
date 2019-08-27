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
@Table(tableName = "own_source_field")
public class OwnSourceField extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "own_source_field";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("own_field_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String remark;
	private BigDecimal own_field_id;
	private BigDecimal own_dource_table_id;
	private String field_type;
	private String field_name;

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getOwn_field_id() { return own_field_id; }
	public void setOwn_field_id(BigDecimal own_field_id) {
		if(own_field_id==null) throw new BusinessException("Entity : OwnSourceField.own_field_id must not null!");
		this.own_field_id = own_field_id;
	}

	public BigDecimal getOwn_dource_table_id() { return own_dource_table_id; }
	public void setOwn_dource_table_id(BigDecimal own_dource_table_id) {
		if(own_dource_table_id==null) throw new BusinessException("Entity : OwnSourceField.own_dource_table_id must not null!");
		this.own_dource_table_id = own_dource_table_id;
	}

	public String getField_type() { return field_type; }
	public void setField_type(String field_type) {
		if(field_type==null) throw new BusinessException("Entity : OwnSourceField.field_type must not null!");
		this.field_type = field_type;
	}

	public String getField_name() { return field_name; }
	public void setField_name(String field_name) {
		if(field_name==null) throw new BusinessException("Entity : OwnSourceField.field_name must not null!");
		this.field_name = field_name;
	}

}