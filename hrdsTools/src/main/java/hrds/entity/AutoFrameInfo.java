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
@Table(tableName = "auto_frame_info")
public class AutoFrameInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_frame_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("frame_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String border_style;
	private Integer border_width;
	private Integer y_axis_coord;
	private String is_shadow;
	private Integer length;
	private Integer width;
	private Integer x_axis_coord;
	private Integer serial_number;
	private String border_color;
	private BigDecimal frame_id;
	private Integer border_radius;
	private BigDecimal dashboard_id;

	public String getBorder_style() { return border_style; }
	public void setBorder_style(String border_style) {
		if(border_style==null) throw new BusinessException("Entity : AutoFrameInfo.border_style must not null!");
		this.border_style = border_style;
	}

	public Integer getBorder_width() { return border_width; }
	public void setBorder_width(Integer border_width) {
		if(border_width==null) throw new BusinessException("Entity : AutoFrameInfo.border_width must not null!");
		this.border_width = border_width;
	}

	public Integer getY_axis_coord() { return y_axis_coord; }
	public void setY_axis_coord(Integer y_axis_coord) {
		if(y_axis_coord==null) throw new BusinessException("Entity : AutoFrameInfo.y_axis_coord must not null!");
		this.y_axis_coord = y_axis_coord;
	}

	public String getIs_shadow() { return is_shadow; }
	public void setIs_shadow(String is_shadow) {
		if(is_shadow==null) throw new BusinessException("Entity : AutoFrameInfo.is_shadow must not null!");
		this.is_shadow = is_shadow;
	}

	public Integer getLength() { return length; }
	public void setLength(Integer length) {
		if(length==null) addNullValueField("length");
		this.length = length;
	}

	public Integer getWidth() { return width; }
	public void setWidth(Integer width) {
		if(width==null) addNullValueField("width");
		this.width = width;
	}

	public Integer getX_axis_coord() { return x_axis_coord; }
	public void setX_axis_coord(Integer x_axis_coord) {
		if(x_axis_coord==null) throw new BusinessException("Entity : AutoFrameInfo.x_axis_coord must not null!");
		this.x_axis_coord = x_axis_coord;
	}

	public Integer getSerial_number() { return serial_number; }
	public void setSerial_number(Integer serial_number) {
		if(serial_number==null) throw new BusinessException("Entity : AutoFrameInfo.serial_number must not null!");
		this.serial_number = serial_number;
	}

	public String getBorder_color() { return border_color; }
	public void setBorder_color(String border_color) {
		if(border_color==null) throw new BusinessException("Entity : AutoFrameInfo.border_color must not null!");
		this.border_color = border_color;
	}

	public BigDecimal getFrame_id() { return frame_id; }
	public void setFrame_id(BigDecimal frame_id) {
		if(frame_id==null) throw new BusinessException("Entity : AutoFrameInfo.frame_id must not null!");
		this.frame_id = frame_id;
	}

	public Integer getBorder_radius() { return border_radius; }
	public void setBorder_radius(Integer border_radius) {
		if(border_radius==null) addNullValueField("border_radius");
		this.border_radius = border_radius;
	}

	public BigDecimal getDashboard_id() { return dashboard_id; }
	public void setDashboard_id(BigDecimal dashboard_id) {
		if(dashboard_id==null) throw new BusinessException("Entity : AutoFrameInfo.dashboard_id must not null!");
		this.dashboard_id = dashboard_id;
	}

}