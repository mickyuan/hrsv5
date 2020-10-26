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
 * 组件样式属性表
 */
@Table(tableName = "auto_comp_style_attr")
public class Auto_comp_style_attr extends ProjectTableEntity
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
	@DocBean(name ="component_style_id",value="组件样式ID:",dataType = Long.class,required = true)
	private Long component_style_id;
	@DocBean(name ="component_id",value="组件ID:",dataType = Long.class,required = false)
	private Long component_id;
	@DocBean(name ="title",value="标题:",dataType = String.class,required = true)
	private String title;
	@DocBean(name ="legend",value="图例:",dataType = String.class,required = false)
	private String legend;
	@DocBean(name ="horizontal_grid_line",value="横向网格线:",dataType = String.class,required = false)
	private String horizontal_grid_line;
	@DocBean(name ="vertical_grid_line",value="纵向网格线:",dataType = String.class,required = false)
	private String vertical_grid_line;
	@DocBean(name ="axis",value="轴线:",dataType = String.class,required = false)
	private String axis;

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
