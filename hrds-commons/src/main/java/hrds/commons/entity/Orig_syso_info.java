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
 * 源系统信
 */
@Table(tableName = "orig_syso_info")
public class Orig_syso_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "orig_syso_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 源系统信 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("orig_sys_code");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="orig_sys_code",value="码值系统编码:",dataType = String.class,required = true)
	private String orig_sys_code;
	@DocBean(name ="orig_sys_name",value="码值系统名称:",dataType = String.class,required = true)
	private String orig_sys_name;
	@DocBean(name ="orig_sys_remark",value="码值系统描述:",dataType = String.class,required = false)
	private String orig_sys_remark;

	/** 取得：码值系统编码 */
	public String getOrig_sys_code(){
		return orig_sys_code;
	}
	/** 设置：码值系统编码 */
	public void setOrig_sys_code(String orig_sys_code){
		this.orig_sys_code=orig_sys_code;
	}
	/** 取得：码值系统名称 */
	public String getOrig_sys_name(){
		return orig_sys_name;
	}
	/** 设置：码值系统名称 */
	public void setOrig_sys_name(String orig_sys_name){
		this.orig_sys_name=orig_sys_name;
	}
	/** 取得：码值系统描述 */
	public String getOrig_sys_remark(){
		return orig_sys_remark;
	}
	/** 设置：码值系统描述 */
	public void setOrig_sys_remark(String orig_sys_remark){
		this.orig_sys_remark=orig_sys_remark;
	}
}
