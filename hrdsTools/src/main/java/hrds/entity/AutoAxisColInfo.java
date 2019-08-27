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
@Table(tableName = "auto_axis_col_info")
public class AutoAxisColInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_axis_col_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("axis_column_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String column_name;
	private String show_type;
	private Integer serial_number;
	private BigDecimal component_id;
	private BigDecimal axis_column_id;

	public String getColumn_name() { return column_name; }
	public void setColumn_name(String column_name) {
		if(column_name==null) addNullValueField("column_name");
		this.column_name = column_name;
	}

	public String getShow_type() { return show_type; }
	public void setShow_type(String show_type) {
		if(show_type==null) throw new BusinessException("Entity : AutoAxisColInfo.show_type must not null!");
		this.show_type = show_type;
	}

	public Integer getSerial_number() { return serial_number; }
	public void setSerial_number(Integer serial_number) {
		if(serial_number==null) throw new BusinessException("Entity : AutoAxisColInfo.serial_number must not null!");
		this.serial_number = serial_number;
	}

	public BigDecimal getComponent_id() { return component_id; }
	public void setComponent_id(BigDecimal component_id) {
		if(component_id==null) addNullValueField("component_id");
		this.component_id = component_id;
	}

	public BigDecimal getAxis_column_id() { return axis_column_id; }
	public void setAxis_column_id(BigDecimal axis_column_id) {
		if(axis_column_id==null) throw new BusinessException("Entity : AutoAxisColInfo.axis_column_id must not null!");
		this.axis_column_id = axis_column_id;
	}

}