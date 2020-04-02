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
 * 抽数作业关系表
 */
@Table(tableName = "take_relation_etl")
public class Take_relation_etl extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "take_relation_etl";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 抽数作业关系表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ded_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="ded_id",value="数据抽取定义主键:",dataType = Long.class,required = true)
	private Long ded_id;
	@DocBean(name ="etl_sys_cd",value="工程代码:",dataType = String.class,required = true)
	private String etl_sys_cd;
	@DocBean(name ="etl_job",value="作业名:",dataType = String.class,required = true)
	private String etl_job;
	@DocBean(name ="sub_sys_cd",value="子系统代码:",dataType = String.class,required = true)
	private String sub_sys_cd;
	@DocBean(name ="database_id",value="数据库设置id:",dataType = Long.class,required = true)
	private Long database_id;

	/** 取得：数据抽取定义主键 */
	public Long getDed_id(){
		return ded_id;
	}
	/** 设置：数据抽取定义主键 */
	public void setDed_id(Long ded_id){
		this.ded_id=ded_id;
	}
	/** 设置：数据抽取定义主键 */
	public void setDed_id(String ded_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(ded_id)){
			this.ded_id=new Long(ded_id);
		}
	}
	/** 取得：工程代码 */
	public String getEtl_sys_cd(){
		return etl_sys_cd;
	}
	/** 设置：工程代码 */
	public void setEtl_sys_cd(String etl_sys_cd){
		this.etl_sys_cd=etl_sys_cd;
	}
	/** 取得：作业名 */
	public String getEtl_job(){
		return etl_job;
	}
	/** 设置：作业名 */
	public void setEtl_job(String etl_job){
		this.etl_job=etl_job;
	}
	/** 取得：子系统代码 */
	public String getSub_sys_cd(){
		return sub_sys_cd;
	}
	/** 设置：子系统代码 */
	public void setSub_sys_cd(String sub_sys_cd){
		this.sub_sys_cd=sub_sys_cd;
	}
	/** 取得：数据库设置id */
	public Long getDatabase_id(){
		return database_id;
	}
	/** 设置：数据库设置id */
	public void setDatabase_id(Long database_id){
		this.database_id=database_id;
	}
	/** 设置：数据库设置id */
	public void setDatabase_id(String database_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(database_id)){
			this.database_id=new Long(database_id);
		}
	}
}
