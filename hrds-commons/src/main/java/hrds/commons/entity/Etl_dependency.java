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
 * 作业依赖关系表
 */
@Table(tableName = "etl_dependency")
public class Etl_dependency extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etl_dependency";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 作业依赖关系表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("pre_etl_job");
		__tmpPKS.add("etl_job");
		__tmpPKS.add("pre_etl_sys_cd");
		__tmpPKS.add("etl_sys_cd");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="pre_etl_job",value="上游作业名:",dataType = String.class,required = true)
	private String pre_etl_job;
	@DocBean(name ="status",value="状态(Status):T-有效(T)<TRUE> F-失效(F)<FALSE> ",dataType = String.class,required = false)
	private String status;
	@DocBean(name ="main_serv_sync",value="主服务器同步标志(Main_Server_Sync):L-锁定<LOCK> N-不同步<NO> Y-同步<YES> B-备份中<BACKUP> ",dataType = String.class,required = false)
	private String main_serv_sync;
	@DocBean(name ="etl_job",value="作业名:",dataType = String.class,required = true)
	private String etl_job;
	@DocBean(name ="pre_etl_sys_cd",value="上游系统代码:",dataType = String.class,required = true)
	private String pre_etl_sys_cd;
	@DocBean(name ="etl_sys_cd",value="工程代码:",dataType = String.class,required = true)
	private String etl_sys_cd;

	/** 取得：上游作业名 */
	public String getPre_etl_job(){
		return pre_etl_job;
	}
	/** 设置：上游作业名 */
	public void setPre_etl_job(String pre_etl_job){
		this.pre_etl_job=pre_etl_job;
	}
	/** 取得：状态 */
	public String getStatus(){
		return status;
	}
	/** 设置：状态 */
	public void setStatus(String status){
		this.status=status;
	}
	/** 取得：主服务器同步标志 */
	public String getMain_serv_sync(){
		return main_serv_sync;
	}
	/** 设置：主服务器同步标志 */
	public void setMain_serv_sync(String main_serv_sync){
		this.main_serv_sync=main_serv_sync;
	}
	/** 取得：作业名 */
	public String getEtl_job(){
		return etl_job;
	}
	/** 设置：作业名 */
	public void setEtl_job(String etl_job){
		this.etl_job=etl_job;
	}
	/** 取得：上游系统代码 */
	public String getPre_etl_sys_cd(){
		return pre_etl_sys_cd;
	}
	/** 设置：上游系统代码 */
	public void setPre_etl_sys_cd(String pre_etl_sys_cd){
		this.pre_etl_sys_cd=pre_etl_sys_cd;
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
