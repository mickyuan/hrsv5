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
@Table(tableName = "edw_hzzy_field")
public class EdwHzzyField extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_hzzy_field";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("hzf_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal hzf_id;
	private String remark;
	private String field_cn_name;
	private String field_type;
	private String field_name;
	private BigDecimal hzzy_id;

	public BigDecimal getHzf_id() { return hzf_id; }
	public void setHzf_id(BigDecimal hzf_id) {
		if(hzf_id==null) throw new BusinessException("Entity : EdwHzzyField.hzf_id must not null!");
		this.hzf_id = hzf_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getField_cn_name() { return field_cn_name; }
	public void setField_cn_name(String field_cn_name) {
		if(field_cn_name==null) addNullValueField("field_cn_name");
		this.field_cn_name = field_cn_name;
	}

	public String getField_type() { return field_type; }
	public void setField_type(String field_type) {
		if(field_type==null) throw new BusinessException("Entity : EdwHzzyField.field_type must not null!");
		this.field_type = field_type;
	}

	public String getField_name() { return field_name; }
	public void setField_name(String field_name) {
		if(field_name==null) throw new BusinessException("Entity : EdwHzzyField.field_name must not null!");
		this.field_name = field_name;
	}

	public BigDecimal getHzzy_id() { return hzzy_id; }
	public void setHzzy_id(BigDecimal hzzy_id) {
		if(hzzy_id==null) throw new BusinessException("Entity : EdwHzzyField.hzzy_id must not null!");
		this.hzzy_id = hzzy_id;
	}

}