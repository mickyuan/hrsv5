package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import hrds.commons.entity.fdentity.ProjectTableEntity;
import fd.ng.db.entity.anno.Table;
import fd.ng.core.annotation.DocBean;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * 仪表板边框组件信息表
 */
@Table(tableName = "auto_frame_info")
public class Auto_frame_info extends ProjectTableEntity
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
	@DocBean(name ="frame_id",value="边框id:",dataType = Long.class,required = true)
	private Long frame_id;
	@DocBean(name ="serial_number",value="序号:",dataType = Integer.class,required = true)
	private Integer serial_number;
	@DocBean(name ="x_axis_coord",value="X轴坐标:",dataType = Integer.class,required = true)
	private Integer x_axis_coord;
	@DocBean(name ="y_axis_coord",value="y轴坐标:",dataType = Integer.class,required = true)
	private Integer y_axis_coord;
	@DocBean(name ="border_style",value="边框风格:",dataType = String.class,required = false)
	private String border_style;
	@DocBean(name ="border_color",value="边框颜色:",dataType = String.class,required = false)
	private String border_color;
	@DocBean(name ="border_width",value="边框宽度:",dataType = Long.class,required = true)
	private Long border_width;
	@DocBean(name ="border_radius",value="边框圆角大小:",dataType = Long.class,required = true)
	private Long border_radius;
	@DocBean(name ="is_shadow",value="是否启用阴影效果(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_shadow;
	@DocBean(name ="dashboard_id",value="仪表板id:",dataType = Long.class,required = true)
	private Long dashboard_id;
	@DocBean(name ="length",value="组件长度:",dataType = Long.class,required = true)
	private Long length;
	@DocBean(name ="width",value="组件宽度:",dataType = Long.class,required = true)
	private Long width;

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
	public Long getBorder_width(){
		return border_width;
	}
	/** 设置：边框宽度 */
	public void setBorder_width(Long border_width){
		this.border_width=border_width;
	}
	/** 设置：边框宽度 */
	public void setBorder_width(String border_width){
		if(!fd.ng.core.utils.StringUtil.isEmpty(border_width)){
			this.border_width=new Long(border_width);
		}
	}
	/** 取得：边框圆角大小 */
	public Long getBorder_radius(){
		return border_radius;
	}
	/** 设置：边框圆角大小 */
	public void setBorder_radius(Long border_radius){
		this.border_radius=border_radius;
	}
	/** 设置：边框圆角大小 */
	public void setBorder_radius(String border_radius){
		if(!fd.ng.core.utils.StringUtil.isEmpty(border_radius)){
			this.border_radius=new Long(border_radius);
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
	public Long getLength(){
		return length;
	}
	/** 设置：组件长度 */
	public void setLength(Long length){
		this.length=length;
	}
	/** 设置：组件长度 */
	public void setLength(String length){
		if(!fd.ng.core.utils.StringUtil.isEmpty(length)){
			this.length=new Long(length);
		}
	}
	/** 取得：组件宽度 */
	public Long getWidth(){
		return width;
	}
	/** 设置：组件宽度 */
	public void setWidth(Long width){
		this.width=width;
	}
	/** 设置：组件宽度 */
	public void setWidth(String width){
		if(!fd.ng.core.utils.StringUtil.isEmpty(width)){
			this.width=new Long(width);
		}
	}
}
