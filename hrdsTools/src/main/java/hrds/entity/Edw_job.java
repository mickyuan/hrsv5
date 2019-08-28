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
 * 模型作业信息表
 */
@Table(tableName = "edw_job")
public class Edw_job extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_job";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 模型作业信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("jobcode");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String jobcode; //作业编号
	private String jobname; //作业名称
	private String jobtype; //作业类型
	private String algorithmcode; //算法类型
	private String oper_cycle; //作业执行周期
	private String job_template_code; //作业模板
	private String job_state; //作业运行状态

	/** 取得：作业编号 */
	public String getJobcode(){
		return jobcode;
	}
	/** 设置：作业编号 */
	public void setJobcode(String jobcode){
		this.jobcode=jobcode;
	}
	/** 取得：作业名称 */
	public String getJobname(){
		return jobname;
	}
	/** 设置：作业名称 */
	public void setJobname(String jobname){
		this.jobname=jobname;
	}
	/** 取得：作业类型 */
	public String getJobtype(){
		return jobtype;
	}
	/** 设置：作业类型 */
	public void setJobtype(String jobtype){
		this.jobtype=jobtype;
	}
	/** 取得：算法类型 */
	public String getAlgorithmcode(){
		return algorithmcode;
	}
	/** 设置：算法类型 */
	public void setAlgorithmcode(String algorithmcode){
		this.algorithmcode=algorithmcode;
	}
	/** 取得：作业执行周期 */
	public String getOper_cycle(){
		return oper_cycle;
	}
	/** 设置：作业执行周期 */
	public void setOper_cycle(String oper_cycle){
		this.oper_cycle=oper_cycle;
	}
	/** 取得：作业模板 */
	public String getJob_template_code(){
		return job_template_code;
	}
	/** 设置：作业模板 */
	public void setJob_template_code(String job_template_code){
		this.job_template_code=job_template_code;
	}
	/** 取得：作业运行状态 */
	public String getJob_state(){
		return job_state;
	}
	/** 设置：作业运行状态 */
	public void setJob_state(String job_state){
		this.job_state=job_state;
	}
}
