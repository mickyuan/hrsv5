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
 * 作业信息表
 */
@Table(tableName = "job_info")
public class Job_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "job_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 作业信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("job_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long job_id; //作业Id
	private String job_name; //作业名称
	private String job_path; //作业目录
	private String job_desc; //作业描述
	private String pro_para; //作业参数
	private String log_dic; //日志路径
	private String run_way; //启动方式
	private String disp_time; //触发时间
	private String job_priority; //作业优先级
	private String job_eff_flag; //作业有效标志
	private String job_s_date; //开始日期
	private String job_e_date; //结束日期
	private String log_level; //日志等级
	private String excluding_info; //排斥条件
	private Long task_id; //任务ID
	private String rely_job_id; //作业运行依赖作业ID
	private String fre_week; //周
	private String fre_month; //月
	private String fre_day; //天
	private String cron_expression; //quartz执行表达式
	private Long create_id; //创建用户
	private Long project_id; //工程ID
	private Long agent_id; //作业agent
	private Long user_id; //用户ID

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
	/** 取得：作业名称 */
	public String getJob_name(){
		return job_name;
	}
	/** 设置：作业名称 */
	public void setJob_name(String job_name){
		this.job_name=job_name;
	}
	/** 取得：作业目录 */
	public String getJob_path(){
		return job_path;
	}
	/** 设置：作业目录 */
	public void setJob_path(String job_path){
		this.job_path=job_path;
	}
	/** 取得：作业描述 */
	public String getJob_desc(){
		return job_desc;
	}
	/** 设置：作业描述 */
	public void setJob_desc(String job_desc){
		this.job_desc=job_desc;
	}
	/** 取得：作业参数 */
	public String getPro_para(){
		return pro_para;
	}
	/** 设置：作业参数 */
	public void setPro_para(String pro_para){
		this.pro_para=pro_para;
	}
	/** 取得：日志路径 */
	public String getLog_dic(){
		return log_dic;
	}
	/** 设置：日志路径 */
	public void setLog_dic(String log_dic){
		this.log_dic=log_dic;
	}
	/** 取得：启动方式 */
	public String getRun_way(){
		return run_way;
	}
	/** 设置：启动方式 */
	public void setRun_way(String run_way){
		this.run_way=run_way;
	}
	/** 取得：触发时间 */
	public String getDisp_time(){
		return disp_time;
	}
	/** 设置：触发时间 */
	public void setDisp_time(String disp_time){
		this.disp_time=disp_time;
	}
	/** 取得：作业优先级 */
	public String getJob_priority(){
		return job_priority;
	}
	/** 设置：作业优先级 */
	public void setJob_priority(String job_priority){
		this.job_priority=job_priority;
	}
	/** 取得：作业有效标志 */
	public String getJob_eff_flag(){
		return job_eff_flag;
	}
	/** 设置：作业有效标志 */
	public void setJob_eff_flag(String job_eff_flag){
		this.job_eff_flag=job_eff_flag;
	}
	/** 取得：开始日期 */
	public String getJob_s_date(){
		return job_s_date;
	}
	/** 设置：开始日期 */
	public void setJob_s_date(String job_s_date){
		this.job_s_date=job_s_date;
	}
	/** 取得：结束日期 */
	public String getJob_e_date(){
		return job_e_date;
	}
	/** 设置：结束日期 */
	public void setJob_e_date(String job_e_date){
		this.job_e_date=job_e_date;
	}
	/** 取得：日志等级 */
	public String getLog_level(){
		return log_level;
	}
	/** 设置：日志等级 */
	public void setLog_level(String log_level){
		this.log_level=log_level;
	}
	/** 取得：排斥条件 */
	public String getExcluding_info(){
		return excluding_info;
	}
	/** 设置：排斥条件 */
	public void setExcluding_info(String excluding_info){
		this.excluding_info=excluding_info;
	}
	/** 取得：任务ID */
	public Long getTask_id(){
		return task_id;
	}
	/** 设置：任务ID */
	public void setTask_id(Long task_id){
		this.task_id=task_id;
	}
	/** 设置：任务ID */
	public void setTask_id(String task_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(task_id)){
			this.task_id=new Long(task_id);
		}
	}
	/** 取得：作业运行依赖作业ID */
	public String getRely_job_id(){
		return rely_job_id;
	}
	/** 设置：作业运行依赖作业ID */
	public void setRely_job_id(String rely_job_id){
		this.rely_job_id=rely_job_id;
	}
	/** 取得：周 */
	public String getFre_week(){
		return fre_week;
	}
	/** 设置：周 */
	public void setFre_week(String fre_week){
		this.fre_week=fre_week;
	}
	/** 取得：月 */
	public String getFre_month(){
		return fre_month;
	}
	/** 设置：月 */
	public void setFre_month(String fre_month){
		this.fre_month=fre_month;
	}
	/** 取得：天 */
	public String getFre_day(){
		return fre_day;
	}
	/** 设置：天 */
	public void setFre_day(String fre_day){
		this.fre_day=fre_day;
	}
	/** 取得：quartz执行表达式 */
	public String getCron_expression(){
		return cron_expression;
	}
	/** 设置：quartz执行表达式 */
	public void setCron_expression(String cron_expression){
		this.cron_expression=cron_expression;
	}
	/** 取得：创建用户 */
	public Long getCreate_id(){
		return create_id;
	}
	/** 设置：创建用户 */
	public void setCreate_id(Long create_id){
		this.create_id=create_id;
	}
	/** 设置：创建用户 */
	public void setCreate_id(String create_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(create_id)){
			this.create_id=new Long(create_id);
		}
	}
	/** 取得：工程ID */
	public Long getProject_id(){
		return project_id;
	}
	/** 设置：工程ID */
	public void setProject_id(Long project_id){
		this.project_id=project_id;
	}
	/** 设置：工程ID */
	public void setProject_id(String project_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(project_id)){
			this.project_id=new Long(project_id);
		}
	}
	/** 取得：作业agent */
	public Long getAgent_id(){
		return agent_id;
	}
	/** 设置：作业agent */
	public void setAgent_id(Long agent_id){
		this.agent_id=agent_id;
	}
	/** 设置：作业agent */
	public void setAgent_id(String agent_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(agent_id)){
			this.agent_id=new Long(agent_id);
		}
	}
	/** 取得：用户ID */
	public Long getUser_id(){
		return user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(Long user_id){
		this.user_id=user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(String user_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(user_id)){
			this.user_id=new Long(user_id);
		}
	}
}
