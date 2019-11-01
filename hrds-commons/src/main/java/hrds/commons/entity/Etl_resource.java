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
 * 资源登记表
 */
@Table(tableName = "etl_resource")
public class Etl_resource extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etl_resource";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 资源登记表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("resource_type");
		__tmpPKS.add("etl_sys_cd");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="resource_type",value="资源使用类型:",dataType = String.class,required = true)
	private String resource_type;
	@DocBean(name ="resource_max",value="资源阀值:",dataType = Integer.class,required = false)
	private Integer resource_max;
	@DocBean(name ="resource_used",value="已使用数:",dataType = Integer.class,required = false)
	private Integer resource_used;
	@DocBean(name ="main_serv_sync",value="主服务器同步标志(Main_Server_Sync):L-锁定<LOCK> N-不同步<NO> Y-同步<YES> B-备份中<BACKUP> ",dataType = String.class,required = true)
	private String main_serv_sync;
	@DocBean(name ="etl_sys_cd",value="工程代码:",dataType = String.class,required = true)
	private String etl_sys_cd;

	/** 取得：资源使用类型 */
	public String getResource_type(){
		return resource_type;
	}
	/** 设置：资源使用类型 */
	public void setResource_type(String resource_type){
		this.resource_type=resource_type;
	}
	/** 取得：资源阀值 */
	public Integer getResource_max(){
		return resource_max;
	}
	/** 设置：资源阀值 */
	public void setResource_max(Integer resource_max){
		this.resource_max=resource_max;
	}
	/** 设置：资源阀值 */
	public void setResource_max(String resource_max){
		if(!fd.ng.core.utils.StringUtil.isEmpty(resource_max)){
			this.resource_max=new Integer(resource_max);
		}
	}
	/** 取得：已使用数 */
	public Integer getResource_used(){
		return resource_used;
	}
	/** 设置：已使用数 */
	public void setResource_used(Integer resource_used){
		this.resource_used=resource_used;
	}
	/** 设置：已使用数 */
	public void setResource_used(String resource_used){
		if(!fd.ng.core.utils.StringUtil.isEmpty(resource_used)){
			this.resource_used=new Integer(resource_used);
		}
	}
	/** 取得：主服务器同步标志 */
	public String getMain_serv_sync(){
		return main_serv_sync;
	}
	/** 设置：主服务器同步标志 */
	public void setMain_serv_sync(String main_serv_sync){
		this.main_serv_sync=main_serv_sync;
	}
	/** 取得：工程代码 */
	public String getEtl_sys_cd(){
		return etl_sys_cd;
	}
	/** 设置：工程代码 */
	public void setEtl_sys_cd(String etl_sys_cd){
		this.etl_sys_cd=etl_sys_cd;
	}
}
