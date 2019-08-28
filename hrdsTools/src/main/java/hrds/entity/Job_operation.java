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
 * 作业运行情况
 */
@Table(tableName = "job_operation")
public class Job_operation extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "job_operation";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 作业运行情况 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("job_rs_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String job_rs_id; //作业执行id
	private String execute_s_date; //运行开始日期
	private String execute_s_time; //运行开始时间
	private String execute_length; //运行总时长
	private String execute_e_date; //运行结束日期
	private String execute_e_time; //运行结束时间
	private String execute_state; //作业运行状态
	private Long job_id; //作业Id
	private String is_again; //是否重跑
	private String comp_id; //组件编号
	private String process_id; //进程号
	private String etl_date; //跑批日期
	private String pro_opertype; //工程操作
	private String task_opertype; //任务操作

	/** 取得：作业执行id */
	public String getJob_rs_id(){
		return job_rs_id;
	}
	/** 设置：作业执行id */
	public void setJob_rs_id(String job_rs_id){
		this.job_rs_id=job_rs_id;
	}
	/** 取得：运行开始日期 */
	public String getExecute_s_date(){
		return execute_s_date;
	}
	/** 设置：运行开始日期 */
	public void setExecute_s_date(String execute_s_date){
		this.execute_s_date=execute_s_date;
	}
	/** 取得：运行开始时间 */
	public String getExecute_s_time(){
		return execute_s_time;
	}
	/** 设置：运行开始时间 */
	public void setExecute_s_time(String execute_s_time){
		this.execute_s_time=execute_s_time;
	}
	/** 取得：运行总时长 */
	public String getExecute_length(){
		return execute_length;
	}
	/** 设置：运行总时长 */
	public void setExecute_length(String execute_length){
		this.execute_length=execute_length;
	}
	/** 取得：运行结束日期 */
	public String getExecute_e_date(){
		return execute_e_date;
	}
	/** 设置：运行结束日期 */
	public void setExecute_e_date(String execute_e_date){
		this.execute_e_date=execute_e_date;
	}
	/** 取得：运行结束时间 */
	public String getExecute_e_time(){
		return execute_e_time;
	}
	/** 设置：运行结束时间 */
	public void setExecute_e_time(String execute_e_time){
		this.execute_e_time=execute_e_time;
	}
	/** 取得：作业运行状态 */
	public String getExecute_state(){
		return execute_state;
	}
	/** 设置：作业运行状态 */
	public void setExecute_state(String execute_state){
		this.execute_state=execute_state;
	}
	/** 取得：作业Id */
	public Long getJob_id(){
		return job_id;
	}
	/** 设置：作业Id */
	public void setJob_id(Long job_id){
		this.job_id=job_id;
	}
	/** 设置：作业Id */
	public void setJob_id(String job_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(job_id)){
			this.job_id=new Long(job_id);
		}
	}
	/** 取得：是否重跑 */
	public String getIs_again(){
		return is_again;
	}
	/** 设置：是否重跑 */
	public void setIs_again(String is_again){
		this.is_again=is_again;
	}
	/** 取得：组件编号 */
	public String getComp_id(){
		return comp_id;
	}
	/** 设置：组件编号 */
	public void setComp_id(String comp_id){
		this.comp_id=comp_id;
	}
	/** 取得：进程号 */
	public String getProcess_id(){
		return process_id;
	}
	/** 设置：进程号 */
	public void setProcess_id(String process_id){
		this.process_id=process_id;
	}
	/** 取得：跑批日期 */
	public String getEtl_date(){
		return etl_date;
	}
	/** 设置：跑批日期 */
	public void setEtl_date(String etl_date){
		this.etl_date=etl_date;
	}
	/** 取得：工程操作 */
	public String getPro_opertype(){
		return pro_opertype;
	}
	/** 设置：工程操作 */
	public void setPro_opertype(String pro_opertype){
		this.pro_opertype=pro_opertype;
	}
	/** 取得：任务操作 */
	public String getTask_opertype(){
		return task_opertype;
	}
	/** 设置：任务操作 */
	public void setTask_opertype(String task_opertype){
		this.task_opertype=task_opertype;
	}
}
