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
 * 作业历史表
 */
@Table(tableName = "etl_job_disp_his")
public class Etl_job_disp_his extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etl_job_disp_his";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 作业历史表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("curr_bath_date");
		__tmpPKS.add("etl_job");
		__tmpPKS.add("etl_sys_cd");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="etl_job_desc",value="作业描述:",dataType = String.class,required = false)
	private String etl_job_desc;
	@DocBean(name ="pro_type",value="作业程序类型(Pro_Type):SHELL-SHELL<SHELL> PERL-PERL<PERL> BAT-BAT<BAT> JAVA-JAVA<JAVA> PYTHON-PYTHON<PYTHON> WF-WF<WF> DBTRAN-DBTRAN<DBTRAN> DBJOB-DBJOB<DBJOB> Yarn-Yarn<Yarn> Thrift-Thrift<Thrift> ",dataType = String.class,required = false)
	private String pro_type;
	@DocBean(name ="pro_dic",value="作业程序目录:",dataType = String.class,required = false)
	private String pro_dic;
	@DocBean(name ="pro_name",value="作业程序名称:",dataType = String.class,required = false)
	private String pro_name;
	@DocBean(name ="pro_para",value="作业程序参数:",dataType = String.class,required = false)
	private String pro_para;
	@DocBean(name ="log_dic",value="日志目录:",dataType = String.class,required = false)
	private String log_dic;
	@DocBean(name ="disp_freq",value="调度频率(Dispatch_Frequency):D-天(D)<DAILY> M-月(M)<MONTHLY> W-周(W)<WEEKLY> X-旬(X)<TENDAYS> Y-年(Y)<YEARLY> F-频率(F)<PinLv> ",dataType = String.class,required = false)
	private String disp_freq;
	@DocBean(name ="disp_offset",value="调度时间位移:",dataType = Integer.class,required = false)
	private Integer disp_offset;
	@DocBean(name ="disp_type",value="调度触发方式(Dispatch_Type):B-批前(B)<BEFORE> D-依赖触发(D)<DEPENDENCE> T-定时T+1触发(T)<TPLUS1> Z-定时T+0触发(Z)<TPLUS0> A-批后(A)<AFTER> ",dataType = String.class,required = false)
	private String disp_type;
	@DocBean(name ="disp_time",value="调度触发时间:",dataType = String.class,required = false)
	private String disp_time;
	@DocBean(name ="job_eff_flag",value="作业有效标志(Job_Effective_Flag):Y-有效(Y)<YES> N-无效(N)<NO> V-空跑(V)<VIRTUAL> ",dataType = String.class,required = false)
	private String job_eff_flag;
	@DocBean(name ="job_priority",value="作业优先级:",dataType = Integer.class,required = false)
	private Integer job_priority;
	@DocBean(name ="job_disp_status",value="作业调度状态(Job_Status):D-完成<DONE> E-错误<ERROR> P-挂起<PENDING> R-运行<RUNNING> S-停止<STOP> W-等待<WAITING> ",dataType = String.class,required = false)
	private String job_disp_status;
	@DocBean(name ="curr_st_time",value="开始时间:",dataType = String.class,required = false)
	private String curr_st_time;
	@DocBean(name ="curr_end_time",value="结束时间:",dataType = String.class,required = false)
	private String curr_end_time;
	@DocBean(name ="overlength_val",value="超长阀值:",dataType = Integer.class,required = false)
	private Integer overlength_val;
	@DocBean(name ="overtime_val",value="超时阀值:",dataType = Integer.class,required = false)
	private Integer overtime_val;
	@DocBean(name ="comments",value="备注信息:",dataType = String.class,required = false)
	private String comments;
	@DocBean(name ="today_disp",value="当天是否调度(Today_Dispatch_Flag):Y-是(Y)<YES> N-否(N)<NO> ",dataType = String.class,required = false)
	private String today_disp;
	@DocBean(name ="main_serv_sync",value="主服务器同步标志(Main_Server_Sync):L-锁定<LOCK> N-不同步<NO> Y-同步<YES> B-备份中<BACKUP> ",dataType = String.class,required = false)
	private String main_serv_sync;
	@DocBean(name ="job_process_id",value="作业进程号:",dataType = String.class,required = false)
	private String job_process_id;
	@DocBean(name ="job_priority_curr",value="作业当前优先级:",dataType = Integer.class,required = false)
	private Integer job_priority_curr;
	@DocBean(name ="job_return_val",value="作业返回值:",dataType = Integer.class,required = false)
	private Integer job_return_val;
	@DocBean(name ="curr_bath_date",value="当前批量日期:",dataType = String.class,required = true)
	private String curr_bath_date;
	@DocBean(name ="etl_job",value="作业名:",dataType = String.class,required = true)
	private String etl_job;
	@DocBean(name ="sub_sys_cd",value="子系统代码:",dataType = String.class,required = true)
	private String sub_sys_cd;
	@DocBean(name ="exe_frequency",value="每隔(分钟)执行	exe_frequency:",dataType = Long.class,required = false)
	private Long exe_frequency;
	@DocBean(name ="exe_num",value="执行次数:",dataType = Integer.class,required = false)
	private Integer exe_num;
	@DocBean(name ="com_exe_num",value="已经执行次数:",dataType = Integer.class,required = false)
	private Integer com_exe_num;
	@DocBean(name ="last_exe_time",value="上次执行时间:",dataType = String.class,required = false)
	private String last_exe_time;
	@DocBean(name ="star_time",value="开始执行时间:",dataType = String.class,required = false)
	private String star_time;
	@DocBean(name ="end_time",value="结束执行时间:",dataType = String.class,required = false)
	private String end_time;
	@DocBean(name ="etl_sys_cd",value="工程代码:",dataType = String.class,required = true)
	private String etl_sys_cd;

	/** 取得：作业描述 */
	public String getEtl_job_desc(){
		return etl_job_desc;
	}
	/** 设置：作业描述 */
	public void setEtl_job_desc(String etl_job_desc){
		this.etl_job_desc=etl_job_desc;
	}
	/** 取得：作业程序类型 */
	public String getPro_type(){
		return pro_type;
	}
	/** 设置：作业程序类型 */
	public void setPro_type(String pro_type){
		this.pro_type=pro_type;
	}
	/** 取得：作业程序目录 */
	public String getPro_dic(){
		return pro_dic;
	}
	/** 设置：作业程序目录 */
	public void setPro_dic(String pro_dic){
		this.pro_dic=pro_dic;
	}
	/** 取得：作业程序名称 */
	public String getPro_name(){
		return pro_name;
	}
	/** 设置：作业程序名称 */
	public void setPro_name(String pro_name){
		this.pro_name=pro_name;
	}
	/** 取得：作业程序参数 */
	public String getPro_para(){
		return pro_para;
	}
	/** 设置：作业程序参数 */
	public void setPro_para(String pro_para){
		this.pro_para=pro_para;
	}
	/** 取得：日志目录 */
	public String getLog_dic(){
		return log_dic;
	}
	/** 设置：日志目录 */
	public void setLog_dic(String log_dic){
		this.log_dic=log_dic;
	}
	/** 取得：调度频率 */
	public String getDisp_freq(){
		return disp_freq;
	}
	/** 设置：调度频率 */
	public void setDisp_freq(String disp_freq){
		this.disp_freq=disp_freq;
	}
	/** 取得：调度时间位移 */
	public Integer getDisp_offset(){
		return disp_offset;
	}
	/** 设置：调度时间位移 */
	public void setDisp_offset(Integer disp_offset){
		this.disp_offset=disp_offset;
	}
	/** 设置：调度时间位移 */
	public void setDisp_offset(String disp_offset){
		if(!fd.ng.core.utils.StringUtil.isEmpty(disp_offset)){
			this.disp_offset=new Integer(disp_offset);
		}
	}
	/** 取得：调度触发方式 */
	public String getDisp_type(){
		return disp_type;
	}
	/** 设置：调度触发方式 */
	public void setDisp_type(String disp_type){
		this.disp_type=disp_type;
	}
	/** 取得：调度触发时间 */
	public String getDisp_time(){
		return disp_time;
	}
	/** 设置：调度触发时间 */
	public void setDisp_time(String disp_time){
		this.disp_time=disp_time;
	}
	/** 取得：作业有效标志 */
	public String getJob_eff_flag(){
		return job_eff_flag;
	}
	/** 设置：作业有效标志 */
	public void setJob_eff_flag(String job_eff_flag){
		this.job_eff_flag=job_eff_flag;
	}
	/** 取得：作业优先级 */
	public Integer getJob_priority(){
		return job_priority;
	}
	/** 设置：作业优先级 */
	public void setJob_priority(Integer job_priority){
		this.job_priority=job_priority;
	}
	/** 设置：作业优先级 */
	public void setJob_priority(String job_priority){
		if(!fd.ng.core.utils.StringUtil.isEmpty(job_priority)){
			this.job_priority=new Integer(job_priority);
		}
	}
	/** 取得：作业调度状态 */
	public String getJob_disp_status(){
		return job_disp_status;
	}
	/** 设置：作业调度状态 */
	public void setJob_disp_status(String job_disp_status){
		this.job_disp_status=job_disp_status;
	}
	/** 取得：开始时间 */
	public String getCurr_st_time(){
		return curr_st_time;
	}
	/** 设置：开始时间 */
	public void setCurr_st_time(String curr_st_time){
		this.curr_st_time=curr_st_time;
	}
	/** 取得：结束时间 */
	public String getCurr_end_time(){
		return curr_end_time;
	}
	/** 设置：结束时间 */
	public void setCurr_end_time(String curr_end_time){
		this.curr_end_time=curr_end_time;
	}
	/** 取得：超长阀值 */
	public Integer getOverlength_val(){
		return overlength_val;
	}
	/** 设置：超长阀值 */
	public void setOverlength_val(Integer overlength_val){
		this.overlength_val=overlength_val;
	}
	/** 设置：超长阀值 */
	public void setOverlength_val(String overlength_val){
		if(!fd.ng.core.utils.StringUtil.isEmpty(overlength_val)){
			this.overlength_val=new Integer(overlength_val);
		}
	}
	/** 取得：超时阀值 */
	public Integer getOvertime_val(){
		return overtime_val;
	}
	/** 设置：超时阀值 */
	public void setOvertime_val(Integer overtime_val){
		this.overtime_val=overtime_val;
	}
	/** 设置：超时阀值 */
	public void setOvertime_val(String overtime_val){
		if(!fd.ng.core.utils.StringUtil.isEmpty(overtime_val)){
			this.overtime_val=new Integer(overtime_val);
		}
	}
	/** 取得：备注信息 */
	public String getComments(){
		return comments;
	}
	/** 设置：备注信息 */
	public void setComments(String comments){
		this.comments=comments;
	}
	/** 取得：当天是否调度 */
	public String getToday_disp(){
		return today_disp;
	}
	/** 设置：当天是否调度 */
	public void setToday_disp(String today_disp){
		this.today_disp=today_disp;
	}
	/** 取得：主服务器同步标志 */
	public String getMain_serv_sync(){
		return main_serv_sync;
	}
	/** 设置：主服务器同步标志 */
	public void setMain_serv_sync(String main_serv_sync){
		this.main_serv_sync=main_serv_sync;
	}
	/** 取得：作业进程号 */
	public String getJob_process_id(){
		return job_process_id;
	}
	/** 设置：作业进程号 */
	public void setJob_process_id(String job_process_id){
		this.job_process_id=job_process_id;
	}
	/** 取得：作业当前优先级 */
	public Integer getJob_priority_curr(){
		return job_priority_curr;
	}
	/** 设置：作业当前优先级 */
	public void setJob_priority_curr(Integer job_priority_curr){
		this.job_priority_curr=job_priority_curr;
	}
	/** 设置：作业当前优先级 */
	public void setJob_priority_curr(String job_priority_curr){
		if(!fd.ng.core.utils.StringUtil.isEmpty(job_priority_curr)){
			this.job_priority_curr=new Integer(job_priority_curr);
		}
	}
	/** 取得：作业返回值 */
	public Integer getJob_return_val(){
		return job_return_val;
	}
	/** 设置：作业返回值 */
	public void setJob_return_val(Integer job_return_val){
		this.job_return_val=job_return_val;
	}
	/** 设置：作业返回值 */
	public void setJob_return_val(String job_return_val){
		if(!fd.ng.core.utils.StringUtil.isEmpty(job_return_val)){
			this.job_return_val=new Integer(job_return_val);
		}
	}
	/** 取得：当前批量日期 */
	public String getCurr_bath_date(){
		return curr_bath_date;
	}
	/** 设置：当前批量日期 */
	public void setCurr_bath_date(String curr_bath_date){
		this.curr_bath_date=curr_bath_date;
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
	/** 取得：每隔(分钟)执行	exe_frequency */
	public Long getExe_frequency(){
		return exe_frequency;
	}
	/** 设置：每隔(分钟)执行	exe_frequency */
	public void setExe_frequency(Long exe_frequency){
		this.exe_frequency=exe_frequency;
	}
	/** 设置：每隔(分钟)执行	exe_frequency */
	public void setExe_frequency(String exe_frequency){
		if(!fd.ng.core.utils.StringUtil.isEmpty(exe_frequency)){
			this.exe_frequency=new Long(exe_frequency);
		}
	}
	/** 取得：执行次数 */
	public Integer getExe_num(){
		return exe_num;
	}
	/** 设置：执行次数 */
	public void setExe_num(Integer exe_num){
		this.exe_num=exe_num;
	}
	/** 设置：执行次数 */
	public void setExe_num(String exe_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(exe_num)){
			this.exe_num=new Integer(exe_num);
		}
	}
	/** 取得：已经执行次数 */
	public Integer getCom_exe_num(){
		return com_exe_num;
	}
	/** 设置：已经执行次数 */
	public void setCom_exe_num(Integer com_exe_num){
		this.com_exe_num=com_exe_num;
	}
	/** 设置：已经执行次数 */
	public void setCom_exe_num(String com_exe_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(com_exe_num)){
			this.com_exe_num=new Integer(com_exe_num);
		}
	}
	/** 取得：上次执行时间 */
	public String getLast_exe_time(){
		return last_exe_time;
	}
	/** 设置：上次执行时间 */
	public void setLast_exe_time(String last_exe_time){
		this.last_exe_time=last_exe_time;
	}
	/** 取得：开始执行时间 */
	public String getStar_time(){
		return star_time;
	}
	/** 设置：开始执行时间 */
	public void setStar_time(String star_time){
		this.star_time=star_time;
	}
	/** 取得：结束执行时间 */
	public String getEnd_time(){
		return end_time;
	}
	/** 设置：结束执行时间 */
	public void setEnd_time(String end_time){
		this.end_time=end_time;
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
