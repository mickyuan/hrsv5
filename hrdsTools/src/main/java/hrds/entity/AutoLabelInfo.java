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
@Table(tableName = "auto_label_info")
public class AutoLabelInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_label_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("label_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String label_size;
	private Integer y_axis_coord;
	private String label_color;
	private Integer length;
	private Integer width;
	private Integer x_axis_coord;
	private Integer serial_number;
	private String label_content;
	private String label_id;
	private BigDecimal dashboard_id;

	public String getLabel_size() { return label_size; }
	public void setLabel_size(String label_size) {
		if(label_size==null) addNullValueField("label_size");
		this.label_size = label_size;
	}

	public Integer getY_axis_coord() { return y_axis_coord; }
	public void setY_axis_coord(Integer y_axis_coord) {
		if(y_axis_coord==null) throw new BusinessException("Entity : AutoLabelInfo.y_axis_coord must not null!");
		this.y_axis_coord = y_axis_coord;
	}

	public String getLabel_color() { return label_color; }
	public void setLabel_color(String label_color) {
		if(label_color==null) addNullValueField("label_color");
		this.label_color = label_color;
	}

	public Integer getLength() { return length; }
	public void setLength(Integer length) {
		if(length==null) throw new BusinessException("Entity : AutoLabelInfo.length must not null!");
		this.length = length;
	}

	public Integer getWidth() { return width; }
	public void setWidth(Integer width) {
		if(width==null) throw new BusinessException("Entity : AutoLabelInfo.width must not null!");
		this.width = width;
	}

	public Integer getX_axis_coord() { return x_axis_coord; }
	public void setX_axis_coord(Integer x_axis_coord) {
		if(x_axis_coord==null) throw new BusinessException("Entity : AutoLabelInfo.x_axis_coord must not null!");
		this.x_axis_coord = x_axis_coord;
	}

	public Integer getSerial_number() { return serial_number; }
	public void setSerial_number(Integer serial_number) {
		if(serial_number==null) throw new BusinessException("Entity : AutoLabelInfo.serial_number must not null!");
		this.serial_number = serial_number;
	}

	public String getLabel_content() { return label_content; }
	public void setLabel_content(String label_content) {
		if(label_content==null) addNullValueField("label_content");
		this.label_content = label_content;
	}

	public String getLabel_id() { return label_id; }
	public void setLabel_id(String label_id) {
		if(label_id==null) throw new BusinessException("Entity : AutoLabelInfo.label_id must not null!");
		this.label_id = label_id;
	}

	public BigDecimal getDashboard_id() { return dashboard_id; }
	public void setDashboard_id(BigDecimal dashboard_id) {
		if(dashboard_id==null) addNullValueField("dashboard_id");
		this.dashboard_id = dashboard_id;
	}

}