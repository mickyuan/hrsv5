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
 * 仪表板分割线表
 */
@Table(tableName = "auto_line_info")
public class Auto_line_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_line_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 仪表板分割线表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("line_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Integer x_axis_coord; //X轴坐标
	private Integer y_axis_coord; //Y轴坐标
	private Long line_id; //分割线id
	private String line_color; //颜色
	private String line_type; //分割线样式
	private Long dashboard_id; //仪表板id
	private Integer line_length; //分割线长度
	private Integer line_weight; //分割线宽度
	private Integer serial_number; //序号

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
	/** 取得：Y轴坐标 */
	public Integer getY_axis_coord(){
		return y_axis_coord;
	}
	/** 设置：Y轴坐标 */
	public void setY_axis_coord(Integer y_axis_coord){
		this.y_axis_coord=y_axis_coord;
	}
	/** 设置：Y轴坐标 */
	public void setY_axis_coord(String y_axis_coord){
		if(!fd.ng.core.utils.StringUtil.isEmpty(y_axis_coord)){
			this.y_axis_coord=new Integer(y_axis_coord);
		}
	}
	/** 取得：分割线id */
	public Long getLine_id(){
		return line_id;
	}
	/** 设置：分割线id */
	public void setLine_id(Long line_id){
		this.line_id=line_id;
	}
	/** 设置：分割线id */
	public void setLine_id(String line_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(line_id)){
			this.line_id=new Long(line_id);
		}
	}
	/** 取得：颜色 */
	public String getLine_color(){
		return line_color;
	}
	/** 设置：颜色 */
	public void setLine_color(String line_color){
		this.line_color=line_color;
	}
	/** 取得：分割线样式 */
	public String getLine_type(){
		return line_type;
	}
	/** 设置：分割线样式 */
	public void setLine_type(String line_type){
		this.line_type=line_type;
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
	/** 取得：分割线长度 */
	public Integer getLine_length(){
		return line_length;
	}
	/** 设置：分割线长度 */
	public void setLine_length(Integer line_length){
		this.line_length=line_length;
	}
	/** 设置：分割线长度 */
	public void setLine_length(String line_length){
		if(!fd.ng.core.utils.StringUtil.isEmpty(line_length)){
			this.line_length=new Integer(line_length);
		}
	}
	/** 取得：分割线宽度 */
	public Integer getLine_weight(){
		return line_weight;
	}
	/** 设置：分割线宽度 */
	public void setLine_weight(Integer line_weight){
		this.line_weight=line_weight;
	}
	/** 设置：分割线宽度 */
	public void setLine_weight(String line_weight){
		if(!fd.ng.core.utils.StringUtil.isEmpty(line_weight)){
			this.line_weight=new Integer(line_weight);
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
}
