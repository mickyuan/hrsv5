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
 * 图形属性
 */
@Table(tableName = "auto_graphic_attr")
public class Auto_graphic_attr extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_graphic_attr";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 图形属性 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("graphic_attr_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="graphic_attr_id",value="图形属性id:",dataType = Long.class,required = true)
	private Long graphic_attr_id;
	@DocBean(name ="color",value="图形颜色:",dataType = String.class,required = true)
	private String color;
	@DocBean(name ="size",value="图形大小:",dataType = Integer.class,required = false)
	private Integer size;
	@DocBean(name ="connection",value="图形连线:",dataType = String.class,required = false)
	private String connection;
	@DocBean(name ="label",value="图形标签:",dataType = String.class,required = false)
	private String label;
	@DocBean(name ="prompt",value="图形提示:",dataType = String.class,required = false)
	private String prompt;
	@DocBean(name ="component_id",value="组件ID:",dataType = Long.class,required = false)
	private Long component_id;

	/** 取得：图形属性id */
	public Long getGraphic_attr_id(){
		return graphic_attr_id;
	}
	/** 设置：图形属性id */
	public void setGraphic_attr_id(Long graphic_attr_id){
		this.graphic_attr_id=graphic_attr_id;
	}
	/** 设置：图形属性id */
	public void setGraphic_attr_id(String graphic_attr_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(graphic_attr_id)){
			this.graphic_attr_id=new Long(graphic_attr_id);
		}
	}
	/** 取得：图形颜色 */
	public String getColor(){
		return color;
	}
	/** 设置：图形颜色 */
	public void setColor(String color){
		this.color=color;
	}
	/** 取得：图形大小 */
	public Integer getSize(){
		return size;
	}
	/** 设置：图形大小 */
	public void setSize(Integer size){
		this.size=size;
	}
	/** 设置：图形大小 */
	public void setSize(String size){
		if(!fd.ng.core.utils.StringUtil.isEmpty(size)){
			this.size=new Integer(size);
		}
	}
	/** 取得：图形连线 */
	public String getConnection(){
		return connection;
	}
	/** 设置：图形连线 */
	public void setConnection(String connection){
		this.connection=connection;
	}
	/** 取得：图形标签 */
	public String getLabel(){
		return label;
	}
	/** 设置：图形标签 */
	public void setLabel(String label){
		this.label=label;
	}
	/** 取得：图形提示 */
	public String getPrompt(){
		return prompt;
	}
	/** 设置：图形提示 */
	public void setPrompt(String prompt){
		this.prompt=prompt;
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
}
