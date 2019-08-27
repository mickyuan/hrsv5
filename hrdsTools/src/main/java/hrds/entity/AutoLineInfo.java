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
@Table(tableName = "auto_line_info")
public class AutoLineInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_line_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("line_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private Integer y_axis_coord;
	private Integer line_length;
	private String line_type;
	private Integer x_axis_coord;
	private Integer line_weight;
	private Integer serial_number;
	private BigDecimal line_id;
	private String line_color;
	private BigDecimal dashboard_id;

	public Integer getY_axis_coord() { return y_axis_coord; }
	public void setY_axis_coord(Integer y_axis_coord) {
		if(y_axis_coord==null) throw new BusinessException("Entity : AutoLineInfo.y_axis_coord must not null!");
		this.y_axis_coord = y_axis_coord;
	}

	public Integer getLine_length() { return line_length; }
	public void setLine_length(Integer line_length) {
		if(line_length==null) throw new BusinessException("Entity : AutoLineInfo.line_length must not null!");
		this.line_length = line_length;
	}

	public String getLine_type() { return line_type; }
	public void setLine_type(String line_type) {
		if(line_type==null) addNullValueField("line_type");
		this.line_type = line_type;
	}

	public Integer getX_axis_coord() { return x_axis_coord; }
	public void setX_axis_coord(Integer x_axis_coord) {
		if(x_axis_coord==null) throw new BusinessException("Entity : AutoLineInfo.x_axis_coord must not null!");
		this.x_axis_coord = x_axis_coord;
	}

	public Integer getLine_weight() { return line_weight; }
	public void setLine_weight(Integer line_weight) {
		if(line_weight==null) throw new BusinessException("Entity : AutoLineInfo.line_weight must not null!");
		this.line_weight = line_weight;
	}

	public Integer getSerial_number() { return serial_number; }
	public void setSerial_number(Integer serial_number) {
		if(serial_number==null) throw new BusinessException("Entity : AutoLineInfo.serial_number must not null!");
		this.serial_number = serial_number;
	}

	public BigDecimal getLine_id() { return line_id; }
	public void setLine_id(BigDecimal line_id) {
		if(line_id==null) throw new BusinessException("Entity : AutoLineInfo.line_id must not null!");
		this.line_id = line_id;
	}

	public String getLine_color() { return line_color; }
	public void setLine_color(String line_color) {
		if(line_color==null) addNullValueField("line_color");
		this.line_color = line_color;
	}

	public BigDecimal getDashboard_id() { return dashboard_id; }
	public void setDashboard_id(BigDecimal dashboard_id) {
		if(dashboard_id==null) throw new BusinessException("Entity : AutoLineInfo.dashboard_id must not null!");
		this.dashboard_id = dashboard_id;
	}

}