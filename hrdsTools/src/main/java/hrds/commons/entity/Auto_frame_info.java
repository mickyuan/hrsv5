package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.commons.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 仪表板边框组件信息表
 */
@Table(tableName = "auto_frame_info")
public class Auto_frame_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_frame_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 仪表板边框组件信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("frame_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long frame_id; //边框id
	private Integer serial_number; //序号
	private Integer x_axis_coord; //X轴坐标
	private Integer y_axis_coord; //y轴坐标
	private String border_style; //边框风格
	private String border_color; //边框颜色
	private Integer border_width; //边框宽度
	private Integer border_radius; //边框圆角大小
	private String is_shadow; //是否启用阴影效果
	private Long dashboard_id; //仪表板id
	private Integer length; //组件长度
	private Integer width; //组件宽度

	/** 取得：边框id */
	public Long getFrame_id(){
		return frame_id;
	}
	/** 设置：边框id */
	public void setFrame_id(Long frame_id){
		this.frame_id=frame_id;
	}
	/** 设置：边框id */
	public void setFrame_id(String frame_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(frame_id)){
			this.frame_id=new Long(frame_id);
		}
	}
	/** 取得：序号 */
	public Integer getSerial_number(){
		return serial_number;
	}
	/** 设置：序号 */
	public void setSerial_number(Integer serial_number){
		this.serial_number=serial_number;
	}
	/** 设置：序号 */
	public void setSerial_number(String serial_number){
		if(!fd.ng.core.utils.StringUtil.isEmpty(serial_number)){
			this.serial_number=new Integer(serial_number);
		}
	}
	/** 取得：X轴坐标 */
	public Integer getX_axis_coord(){
		return x_axis_coord;
	}
	/** 设置：X轴坐标 */
	public void setX_axis_coord(Integer x_axis_coord){
		this.x_axis_coord=x_axis_coord;
	}
	/** 设置：X轴坐标 */
	public void setX_axis_coord(String x_axis_coord){
		if(!fd.ng.core.utils.StringUtil.isEmpty(x_axis_coord)){
			this.x_axis_coord=new Integer(x_axis_coord);
		}
	}
	/** 取得：y轴坐标 */
	public Integer getY_axis_coord(){
		return y_axis_coord;
	}
	/** 设置：y轴坐标 */
	public void setY_axis_coord(Integer y_axis_coord){
		this.y_axis_coord=y_axis_coord;
	}
	/** 设置：y轴坐标 */
	public void setY_axis_coord(String y_axis_coord){
		if(!fd.ng.core.utils.StringUtil.isEmpty(y_axis_coord)){
			this.y_axis_coord=new Integer(y_axis_coord);
		}
	}
	/** 取得：边框风格 */
	public String getBorder_style(){
		return border_style;
	}
	/** 设置：边框风格 */
	public void setBorder_style(String border_style){
		this.border_style=border_style;
	}
	/** 取得：边框颜色 */
	public String getBorder_color(){
		return border_color;
	}
	/** 设置：边框颜色 */
	public void setBorder_color(String border_color){
		this.border_color=border_color;
	}
	/** 取得：边框宽度 */
	public Integer getBorder_width(){
		return border_width;
	}
	/** 设置：边框宽度 */
	public void setBorder_width(Integer border_width){
		this.border_width=border_width;
	}
	/** 设置：边框宽度 */
	public void setBorder_width(String border_width){
		if(!fd.ng.core.utils.StringUtil.isEmpty(border_width)){
			this.border_width=new Integer(border_width);
		}
	}
	/** 取得：边框圆角大小 */
	public Integer getBorder_radius(){
		return border_radius;
	}
	/** 设置：边框圆角大小 */
	public void setBorder_radius(Integer border_radius){
		this.border_radius=border_radius;
	}
	/** 设置：边框圆角大小 */
	public void setBorder_radius(String border_radius){
		if(!fd.ng.core.utils.StringUtil.isEmpty(border_radius)){
			this.border_radius=new Integer(border_radius);
		}
	}
	/** 取得：是否启用阴影效果 */
	public String getIs_shadow(){
		return is_shadow;
	}
	/** 设置：是否启用阴影效果 */
	public void setIs_shadow(String is_shadow){
		this.is_shadow=is_shadow;
	}
	/** 取得：仪表板id */
	public Long getDashboard_id(){
		return dashboard_id;
	}
	/** 设置：仪表板id */
	public void setDashboard_id(Long dashboard_id){
		this.dashboard_id=dashboard_id;
	}
	/** 设置：仪表板id */
	public void setDashboard_id(String dashboard_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dashboard_id)){
			this.dashboard_id=new Long(dashboard_id);
		}
	}
	/** 取得：组件长度 */
	public Integer getLength(){
		return length;
	}
	/** 设置：组件长度 */
	public void setLength(Integer length){
		this.length=length;
	}
	/** 设置：组件长度 */
	public void setLength(String length){
		if(!fd.ng.core.utils.StringUtil.isEmpty(length)){
			this.length=new Integer(length);
		}
	}
	/** 取得：组件宽度 */
	public Integer getWidth(){
		return width;
	}
	/** 设置：组件宽度 */
	public void setWidth(Integer width){
		this.width=width;
	}
	/** 设置：组件宽度 */
	public void setWidth(String width){
		if(!fd.ng.core.utils.StringUtil.isEmpty(width)){
			this.width=new Integer(width);
		}
	}
}
