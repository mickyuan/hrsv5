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
 * 组件信息表
 */
@Table(tableName = "component_info")
public class Component_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "component_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 组件信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("comp_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="comp_id",value="组件编号:",dataType = String.class,required = true)
	private String comp_id;
	@DocBean(name ="comp_name",value="组件名称:",dataType = String.class,required = true)
	private String comp_name;
	@DocBean(name ="comp_state",value="组件状态(CompState):1-启用<QiYong> 2-禁用<JinYong> ",dataType = String.class,required = true)
	private String comp_state;
	@DocBean(name ="comp_remark",value="备注:",dataType = String.class,required = false)
	private String comp_remark;
	@DocBean(name ="comp_version",value="组件版本:",dataType = String.class,required = true)
	private String comp_version;
	@DocBean(name ="icon_info",value="图标:",dataType = String.class,required = false)
	private String icon_info;
	@DocBean(name ="color_info",value="颜色:",dataType = String.class,required = false)
	private String color_info;
	@DocBean(name ="comp_type",value="组件类型(CompType):1-系统内置组件<NeiZhiZuJian> 2-系统运行组件<YunXingZuJian> ",dataType = String.class,required = true)
	private String comp_type;

	/** 取得：组件编号 */
	public String getComp_id(){
		return comp_id;
	}
	/** 设置：组件编号 */
	public void setComp_id(String comp_id){
		this.comp_id=comp_id;
	}
	/** 取得：组件名称 */
	public String getComp_name(){
		return comp_name;
	}
	/** 设置：组件名称 */
	public void setComp_name(String comp_name){
		this.comp_name=comp_name;
	}
	/** 取得：组件状态 */
	public String getComp_state(){
		return comp_state;
	}
	/** 设置：组件状态 */
	public void setComp_state(String comp_state){
		this.comp_state=comp_state;
	}
	/** 取得：备注 */
	public String getComp_remark(){
		return comp_remark;
	}
	/** 设置：备注 */
	public void setComp_remark(String comp_remark){
		this.comp_remark=comp_remark;
	}
	/** 取得：组件版本 */
	public String getComp_version(){
		return comp_version;
	}
	/** 设置：组件版本 */
	public void setComp_version(String comp_version){
		this.comp_version=comp_version;
	}
	/** 取得：图标 */
	public String getIcon_info(){
		return icon_info;
	}
	/** 设置：图标 */
	public void setIcon_info(String icon_info){
		this.icon_info=icon_info;
	}
	/** 取得：颜色 */
	public String getColor_info(){
		return color_info;
	}
	/** 设置：颜色 */
	public void setColor_info(String color_info){
		this.color_info=color_info;
	}
	/** 取得：组件类型 */
	public String getComp_type(){
		return comp_type;
	}
	/** 设置：组件类型 */
	public void setComp_type(String comp_type){
		this.comp_type=comp_type;
	}
}
