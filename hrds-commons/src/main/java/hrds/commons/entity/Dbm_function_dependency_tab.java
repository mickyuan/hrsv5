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
 * 数据对标单表函数依赖关系表
 */
@Table(tableName = "dbm_function_dependency_tab")
public class Dbm_function_dependency_tab extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dbm_function_dependency_tab";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据对标单表函数依赖关系表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sys_class_code");
		__tmpPKS.add("table_schema");
		__tmpPKS.add("table_code");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="sys_class_code",value="系统分类编码:",dataType = String.class,required = true)
	private String sys_class_code;
	@DocBean(name ="table_schema",value="表所属schema:",dataType = String.class,required = true)
	private String table_schema;
	@DocBean(name ="table_code",value="关系编码:",dataType = String.class,required = true)
	private String table_code;
	@DocBean(name ="left_columns",value="函数依赖关系左部:",dataType = String.class,required = false)
	private String left_columns;
	@DocBean(name ="right_columns",value="函数依赖关系右部:",dataType = String.class,required = true)
	private String right_columns;
	@DocBean(name ="proc_dt",value="计算日期:",dataType = String.class,required = false)
	private String proc_dt;
	@DocBean(name ="fd_level",value="函数依赖关系级别:",dataType = Integer.class,required = false)
	private Integer fd_level;

	/** 取得：系统分类编码 */
	public String getSys_class_code(){
		return sys_class_code;
	}
	/** 设置：系统分类编码 */
	public void setSys_class_code(String sys_class_code){
		this.sys_class_code=sys_class_code;
	}
	/** 取得：表所属schema */
	public String getTable_schema(){
		return table_schema;
	}
	/** 设置：表所属schema */
	public void setTable_schema(String table_schema){
		this.table_schema=table_schema;
	}
	/** 取得：关系编码 */
	public String getTable_code(){
		return table_code;
	}
	/** 设置：关系编码 */
	public void setTable_code(String table_code){
		this.table_code=table_code;
	}
	/** 取得：函数依赖关系左部 */
	public String getLeft_columns(){
		return left_columns;
	}
	/** 设置：函数依赖关系左部 */
	public void setLeft_columns(String left_columns){
		this.left_columns=left_columns;
	}
	/** 取得：函数依赖关系右部 */
	public String getRight_columns(){
		return right_columns;
	}
	/** 设置：函数依赖关系右部 */
	public void setRight_columns(String right_columns){
		this.right_columns=right_columns;
	}
	/** 取得：计算日期 */
	public String getProc_dt(){
		return proc_dt;
	}
	/** 设置：计算日期 */
	public void setProc_dt(String proc_dt){
		this.proc_dt=proc_dt;
	}
	/** 取得：函数依赖关系级别 */
	public Integer getFd_level(){
		return fd_level;
	}
	/** 设置：函数依赖关系级别 */
	public void setFd_level(Integer fd_level){
		this.fd_level=fd_level;
	}
	/** 设置：函数依赖关系级别 */
	public void setFd_level(String fd_level){
		if(!fd.ng.core.utils.StringUtil.isEmpty(fd_level)){
			this.fd_level=new Integer(fd_level);
		}
	}
}
