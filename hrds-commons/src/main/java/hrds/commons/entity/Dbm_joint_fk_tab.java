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
 * 数据对标联合外键关系表
 */
@Table(tableName = "dbm_joint_fk_tab")
public class Dbm_joint_fk_tab extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dbm_joint_fk_tab";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据对标联合外键关系表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("fk_sys_class_code");
		__tmpPKS.add("fk_table_code");
		__tmpPKS.add("fk_col_code");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="fk_sys_class_code",value="主表所在系统分类编码:",dataType = String.class,required = true)
	private String fk_sys_class_code;
	@DocBean(name ="fk_table_code",value="主表表编码:",dataType = String.class,required = true)
	private String fk_table_code;
	@DocBean(name ="fk_col_code",value="主键字段编码:",dataType = String.class,required = true)
	private String fk_col_code;
	@DocBean(name ="group_code",value="联合外键分组编码:",dataType = String.class,required = true)
	private String group_code;
	@DocBean(name ="sys_class_code",value="从表所在系统分类编码:",dataType = String.class,required = true)
	private String sys_class_code;
	@DocBean(name ="table_code",value="从表表编码:",dataType = String.class,required = false)
	private String table_code;
	@DocBean(name ="col_code",value="外键字段编码:",dataType = String.class,required = false)
	private String col_code;

	/** 取得：主表所在系统分类编码 */
	public String getFk_sys_class_code(){
		return fk_sys_class_code;
	}
	/** 设置：主表所在系统分类编码 */
	public void setFk_sys_class_code(String fk_sys_class_code){
		this.fk_sys_class_code=fk_sys_class_code;
	}
	/** 取得：主表表编码 */
	public String getFk_table_code(){
		return fk_table_code;
	}
	/** 设置：主表表编码 */
	public void setFk_table_code(String fk_table_code){
		this.fk_table_code=fk_table_code;
	}
	/** 取得：主键字段编码 */
	public String getFk_col_code(){
		return fk_col_code;
	}
	/** 设置：主键字段编码 */
	public void setFk_col_code(String fk_col_code){
		this.fk_col_code=fk_col_code;
	}
	/** 取得：联合外键分组编码 */
	public String getGroup_code(){
		return group_code;
	}
	/** 设置：联合外键分组编码 */
	public void setGroup_code(String group_code){
		this.group_code=group_code;
	}
	/** 取得：从表所在系统分类编码 */
	public String getSys_class_code(){
		return sys_class_code;
	}
	/** 设置：从表所在系统分类编码 */
	public void setSys_class_code(String sys_class_code){
		this.sys_class_code=sys_class_code;
	}
	/** 取得：从表表编码 */
	public String getTable_code(){
		return table_code;
	}
	/** 设置：从表表编码 */
	public void setTable_code(String table_code){
		this.table_code=table_code;
	}
	/** 取得：外键字段编码 */
	public String getCol_code(){
		return col_code;
	}
	/** 设置：外键字段编码 */
	public void setCol_code(String col_code){
		this.col_code=col_code;
	}
}
