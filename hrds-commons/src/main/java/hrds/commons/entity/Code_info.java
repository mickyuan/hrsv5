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
 * 代码信息表
 */
@Table(tableName = "code_info")
public class Code_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "code_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 代码信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ci_sp_code");
		__tmpPKS.add("ci_sp_class");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="ci_sp_code",value="代码值:",dataType = String.class,required = true)
	private String ci_sp_code;
	@DocBean(name ="ci_sp_class",value="所属类别号:",dataType = String.class,required = true)
	private String ci_sp_class;
	@DocBean(name ="ci_sp_classname",value="类别名称:",dataType = String.class,required = true)
	private String ci_sp_classname;
	@DocBean(name ="ci_sp_name",value="代码名称:",dataType = String.class,required = true)
	private String ci_sp_name;
	@DocBean(name ="ci_sp_remark",value="备注:",dataType = String.class,required = false)
	private String ci_sp_remark;

	/** 取得：代码值 */
	public String getCi_sp_code(){
		return ci_sp_code;
	}
	/** 设置：代码值 */
	public void setCi_sp_code(String ci_sp_code){
		this.ci_sp_code=ci_sp_code;
	}
	/** 取得：所属类别号 */
	public String getCi_sp_class(){
		return ci_sp_class;
	}
	/** 设置：所属类别号 */
	public void setCi_sp_class(String ci_sp_class){
		this.ci_sp_class=ci_sp_class;
	}
	/** 取得：类别名称 */
	public String getCi_sp_classname(){
		return ci_sp_classname;
	}
	/** 设置：类别名称 */
	public void setCi_sp_classname(String ci_sp_classname){
		this.ci_sp_classname=ci_sp_classname;
	}
	/** 取得：代码名称 */
	public String getCi_sp_name(){
		return ci_sp_name;
	}
	/** 设置：代码名称 */
	public void setCi_sp_name(String ci_sp_name){
		this.ci_sp_name=ci_sp_name;
	}
	/** 取得：备注 */
	public String getCi_sp_remark(){
		return ci_sp_remark;
	}
	/** 设置：备注 */
	public void setCi_sp_remark(String ci_sp_remark){
		this.ci_sp_remark=ci_sp_remark;
	}
}
