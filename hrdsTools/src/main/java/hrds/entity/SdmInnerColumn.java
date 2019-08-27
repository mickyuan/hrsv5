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
@Table(tableName = "sdm_inner_column")
public class SdmInnerColumn extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_inner_column";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("field_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal field_id;
	private String field_cn_name;
	private String field_en_name;
	private String remark;
	private String field_desc;
	private BigDecimal table_id;
	private String field_type;

	public BigDecimal getField_id() { return field_id; }
	public void setField_id(BigDecimal field_id) {
		if(field_id==null) throw new BusinessException("Entity : SdmInnerColumn.field_id must not null!");
		this.field_id = field_id;
	}

	public String getField_cn_name() { return field_cn_name; }
	public void setField_cn_name(String field_cn_name) {
		if(field_cn_name==null) addNullValueField("field_cn_name");
		this.field_cn_name = field_cn_name;
	}

	public String getField_en_name() { return field_en_name; }
	public void setField_en_name(String field_en_name) {
		if(field_en_name==null) throw new BusinessException("Entity : SdmInnerColumn.field_en_name must not null!");
		this.field_en_name = field_en_name;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getField_desc() { return field_desc; }
	public void setField_desc(String field_desc) {
		if(field_desc==null) addNullValueField("field_desc");
		this.field_desc = field_desc;
	}

	public BigDecimal getTable_id() { return table_id; }
	public void setTable_id(BigDecimal table_id) {
		if(table_id==null) throw new BusinessException("Entity : SdmInnerColumn.table_id must not null!");
		this.table_id = table_id;
	}

	public String getField_type() { return field_type; }
	public void setField_type(String field_type) {
		if(field_type==null) throw new BusinessException("Entity : SdmInnerColumn.field_type must not null!");
		this.field_type = field_type;
	}

}