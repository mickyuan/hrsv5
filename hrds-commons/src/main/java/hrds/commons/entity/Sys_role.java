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
 * 角色信息表
 */
@Table(tableName = "sys_role")
public class Sys_role extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sys_role";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 角色信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("role_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="role_id",value="角色ID:",dataType = Long.class,required = true)
	private Long role_id;
	@DocBean(name ="role_name",value="角色名称:",dataType = String.class,required = true)
	private String role_name;
	@DocBean(name ="role_remark",value="备注:",dataType = String.class,required = false)
	private String role_remark;

	/** 取得：角色ID */
	public Long getRole_id(){
		return role_id;
	}
	/** 设置：角色ID */
	public void setRole_id(Long role_id){
		this.role_id=role_id;
	}
	/** 设置：角色ID */
	public void setRole_id(String role_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(role_id)){
			this.role_id=new Long(role_id);
		}
	}
	/** 取得：角色名称 */
	public String getRole_name(){
		return role_name;
	}
	/** 设置：角色名称 */
	public void setRole_name(String role_name){
		this.role_name=role_name;
	}
	/** 取得：备注 */
	public String getRole_remark(){
		return role_remark;
	}
	/** 设置：备注 */
	public void setRole_remark(String role_remark){
		this.role_remark=role_remark;
	}
}
