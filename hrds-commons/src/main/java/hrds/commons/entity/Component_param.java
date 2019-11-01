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
 * 组件参数
 */
@Table(tableName = "component_param")
public class Component_param extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "component_param";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 组件参数 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("param_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="param_id",value="主键参数id:",dataType = Long.class,required = true)
	private Long param_id;
	@DocBean(name ="param_name",value="参数名称:",dataType = String.class,required = true)
	private String param_name;
	@DocBean(name ="param_value",value="参数value:",dataType = String.class,required = true)
	private String param_value;
	@DocBean(name ="is_must",value="是否必要(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_must;
	@DocBean(name ="param_remark",value="备注:",dataType = String.class,required = true)
	private String param_remark;
	@DocBean(name ="comp_id",value="组件编号:",dataType = String.class,required = false)
	private String comp_id;

	/** 取得：主键参数id */
	public Long getParam_id(){
		return param_id;
	}
	/** 设置：主键参数id */
	public void setParam_id(Long param_id){
		this.param_id=param_id;
	}
	/** 设置：主键参数id */
	public void setParam_id(String param_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(param_id)){
			this.param_id=new Long(param_id);
		}
	}
	/** 取得：参数名称 */
	public String getParam_name(){
		return param_name;
	}
	/** 设置：参数名称 */
	public void setParam_name(String param_name){
		this.param_name=param_name;
	}
	/** 取得：参数value */
	public String getParam_value(){
		return param_value;
	}
	/** 设置：参数value */
	public void setParam_value(String param_value){
		this.param_value=param_value;
	}
	/** 取得：是否必要 */
	public String getIs_must(){
		return is_must;
	}
	/** 设置：是否必要 */
	public void setIs_must(String is_must){
		this.is_must=is_must;
	}
	/** 取得：备注 */
	public String getParam_remark(){
		return param_remark;
	}
	/** 设置：备注 */
	public void setParam_remark(String param_remark){
		this.param_remark=param_remark;
	}
	/** 取得：组件编号 */
	public String getComp_id(){
		return comp_id;
	}
	/** 设置：组件编号 */
	public void setComp_id(String comp_id){
		this.comp_id=comp_id;
	}
}
