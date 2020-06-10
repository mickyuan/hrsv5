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
 * 对象作业关系表
 */
@Table(tableName = "obj_relation_etl")
public class Obj_relation_etl extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "obj_relation_etl";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 对象作业关系表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ocs_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="etl_sys_cd",value="工程代码:",dataType = String.class,required = true)
	private String etl_sys_cd;
	@DocBean(name ="etl_job",value="作业名:",dataType = String.class,required = true)
	private String etl_job;
	@DocBean(name ="sub_sys_cd",value="子系统代码:",dataType = String.class,required = true)
	private String sub_sys_cd;
	@DocBean(name ="ocs_id",value="对象采集任务编号:",dataType = Long.class,required = true)
	private Long ocs_id;
	@DocBean(name ="odc_id",value="对象采集id:",dataType = Long.class,required = true)
	private Long odc_id;

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
	/** 取得：对象采集任务编号 */
	public Long getOcs_id(){
		return ocs_id;
	}
	/** 设置：对象采集任务编号 */
	public void setOcs_id(Long ocs_id){
		this.ocs_id=ocs_id;
	}
	/** 设置：对象采集任务编号 */
	public void setOcs_id(String ocs_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(ocs_id)){
			this.ocs_id=new Long(ocs_id);
		}
	}
	/** 取得：对象采集id */
	public Long getOdc_id(){
		return odc_id;
	}
	/** 设置：对象采集id */
	public void setOdc_id(Long odc_id){
		this.odc_id=odc_id;
	}
	/** 设置：对象采集id */
	public void setOdc_id(String odc_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(odc_id)){
			this.odc_id=new Long(odc_id);
		}
	}
}
