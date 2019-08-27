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
@Table(tableName = "field_operation_info")
public class FieldOperationInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "field_operation_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("info_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal info_id;
	private String remark;
	private BigDecimal own_field_id;
	private String operation_type;
	private String operation_detail;

	public BigDecimal getInfo_id() { return info_id; }
	public void setInfo_id(BigDecimal info_id) {
		if(info_id==null) throw new BusinessException("Entity : FieldOperationInfo.info_id must not null!");
		this.info_id = info_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getOwn_field_id() { return own_field_id; }
	public void setOwn_field_id(BigDecimal own_field_id) {
		if(own_field_id==null) throw new BusinessException("Entity : FieldOperationInfo.own_field_id must not null!");
		this.own_field_id = own_field_id;
	}

	public String getOperation_type() { return operation_type; }
	public void setOperation_type(String operation_type) {
		if(operation_type==null) throw new BusinessException("Entity : FieldOperationInfo.operation_type must not null!");
		this.operation_type = operation_type;
	}

	public String getOperation_detail() { return operation_detail; }
	public void setOperation_detail(String operation_detail) {
		if(operation_detail==null) throw new BusinessException("Entity : FieldOperationInfo.operation_detail must not null!");
		this.operation_detail = operation_detail;
	}

}