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
 * snowflake主键生成表
 */
@Table(tableName = "keytable_snowflake")
public class Keytable_snowflake extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "keytable_snowflake";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** snowflake主键生成表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("project_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="project_id",value="project_id:",dataType = String.class,required = true)
	private String project_id;
	@DocBean(name ="datacenter_id",value="datacenter_id:",dataType = Integer.class,required = false)
	private Integer datacenter_id;
	@DocBean(name ="machine_id",value="machine_id:",dataType = Integer.class,required = false)
	private Integer machine_id;

	/** 取得：project_id */
	public String getProject_id(){
		return project_id;
	}
	/** 设置：project_id */
	public void setProject_id(String project_id){
		this.project_id=project_id;
	}
	/** 取得：datacenter_id */
	public Integer getDatacenter_id(){
		return datacenter_id;
	}
	/** 设置：datacenter_id */
	public void setDatacenter_id(Integer datacenter_id){
		this.datacenter_id=datacenter_id;
	}
	/** 设置：datacenter_id */
	public void setDatacenter_id(String datacenter_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(datacenter_id)){
			this.datacenter_id=new Integer(datacenter_id);
		}
	}
	/** 取得：machine_id */
	public Integer getMachine_id(){
		return machine_id;
	}
	/** 设置：machine_id */
	public void setMachine_id(Integer machine_id){
		this.machine_id=machine_id;
	}
	/** 设置：machine_id */
	public void setMachine_id(String machine_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(machine_id)){
			this.machine_id=new Integer(machine_id);
		}
	}
}
