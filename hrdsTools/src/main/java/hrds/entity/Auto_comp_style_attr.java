package hrds.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 组件样式属性表
 */
@Table(tableName = "auto_comp_style_attr")
public class Auto_comp_style_attr extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_comp_style_attr";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 组件样式属性表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("component_style_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long component_style_id; //组件样式ID
	private Long component_id; //组件ID
	private String title; //标题
	private String legend; //图例
	private String horizontal_grid_line; //横向网格线
	private String vertical_grid_line; //纵向网格线
	private String axis; //轴线

	/** 取得：组件样式ID */
	public Long getComponent_style_id(){
		return component_style_id;
	}
	/** 设置：组件样式ID */
	public void setComponent_style_id(Long component_style_id){
		this.component_style_id=component_style_id;
	}
	/** 设置：组件样式ID */
	public void setComponent_style_id(String component_style_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(component_style_id)){
			this.component_style_id=new Long(component_style_id);
		}
	}
	/** 取得：组件ID */
	public Long getComponent_id(){
		return component_id;
	}
	/** 设置：组件ID */
	public void setComponent_id(Long component_id){
		this.component_id=component_id;
	}
	/** 设置：组件ID */
	public void setComponent_id(String component_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(component_id)){
			this.component_id=new Long(component_id);
		}
	}
	/** 取得：标题 */
	public String getTitle(){
		return title;
	}
	/** 设置：标题 */
	public void setTitle(String title){
		this.title=title;
	}
	/** 取得：图例 */
	public String getLegend(){
		return legend;
	}
	/** 设置：图例 */
	public void setLegend(String legend){
		this.legend=legend;
	}
	/** 取得：横向网格线 */
	public String getHorizontal_grid_line(){
		return horizontal_grid_line;
	}
	/** 设置：横向网格线 */
	public void setHorizontal_grid_line(String horizontal_grid_line){
		this.horizontal_grid_line=horizontal_grid_line;
	}
	/** 取得：纵向网格线 */
	public String getVertical_grid_line(){
		return vertical_grid_line;
	}
	/** 设置：纵向网格线 */
	public void setVertical_grid_line(String vertical_grid_line){
		this.vertical_grid_line=vertical_grid_line;
	}
	/** 取得：轴线 */
	public String getAxis(){
		return axis;
	}
	/** 设置：轴线 */
	public void setAxis(String axis){
		this.axis=axis;
	}
}
