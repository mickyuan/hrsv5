package hrds.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 运行主机
 */
@Table(tableName = "run_host")
public class Run_host extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "run_host";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 运行主机 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("run_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long run_id; //运行主机ID
	private String run_host_ip; //运行主机IP
	private Long big_jobcount; //最大同时作业数
	private String longest_runtime; //最长运行时间
	private String runhost_remark; //备注

	/** 取得：运行主机ID */
	public Long getRun_id(){
		return run_id;
	}
	/** 设置：运行主机ID */
	public void setRun_id(Long run_id){
		this.run_id=run_id;
	}
	/** 设置：运行主机ID */
	public void setRun_id(String run_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(run_id)){
			this.run_id=new Long(run_id);
		}
	}
	/** 取得：运行主机IP */
	public String getRun_host_ip(){
		return run_host_ip;
	}
	/** 设置：运行主机IP */
	public void setRun_host_ip(String run_host_ip){
		this.run_host_ip=run_host_ip;
	}
	/** 取得：最大同时作业数 */
	public Long getBig_jobcount(){
		return big_jobcount;
	}
	/** 设置：最大同时作业数 */
	public void setBig_jobcount(Long big_jobcount){
		this.big_jobcount=big_jobcount;
	}
	/** 设置：最大同时作业数 */
	public void setBig_jobcount(String big_jobcount){
		if(!fd.ng.core.utils.StringUtil.isEmpty(big_jobcount)){
			this.big_jobcount=new Long(big_jobcount);
		}
	}
	/** 取得：最长运行时间 */
	public String getLongest_runtime(){
		return longest_runtime;
	}
	/** 设置：最长运行时间 */
	public void setLongest_runtime(String longest_runtime){
		this.longest_runtime=longest_runtime;
	}
	/** 取得：备注 */
	public String getRunhost_remark(){
		return runhost_remark;
	}
	/** 设置：备注 */
	public void setRunhost_remark(String runhost_remark){
		this.runhost_remark=runhost_remark;
	}
}
