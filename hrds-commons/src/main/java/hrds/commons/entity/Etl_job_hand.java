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
 * 作业干预表
 */
@Table(tableName = "etl_job_hand")
public class Etl_job_hand extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etl_job_hand";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 作业干预表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("event_id");
		__tmpPKS.add("etl_job");
		__tmpPKS.add("etl_sys_cd");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="event_id",value="干预发生时间:",dataType = String.class,required = true)
	private String event_id;
	@DocBean(name ="etl_hand_type",value="干预类型(Meddle_type):GR-分组级续跑<GRP_RESUME> GP-分组级暂停<GRP_PAUSE> GO-分组级重跑，从源头开始<GRP_ORIGINAL> JT-作业直接跑<JOB_TRIGGER> JS-作业停止<JOB_STOP> JR-作业重跑<JOB_RERUN> JP-作业临时调整优先级<JOB_PRIORITY> JJ-作业跳过<JOB_JUMP> SF-系统日切<SYS_SHIFT> SS-系统停止<SYS_STOP> SP-系统级暂停<SYS_PAUSE> SO-系统级重跑，从源头开始<SYS_ORIGINAL> SR-系统级续跑<SYS_RESUME> ",dataType = String.class,required = false)
	private String etl_hand_type;
	@DocBean(name ="pro_para",value="干预参数:",dataType = String.class,required = false)
	private String pro_para;
	@DocBean(name ="hand_status",value="干预状态(Meddle_status):D-完成<DONE> E-异常<ERROR> F-失效<FALSE> T-有效<TRUE> R-干预中<RUNNING> ",dataType = String.class,required = false)
	private String hand_status;
	@DocBean(name ="st_time",value="开始时间:",dataType = String.class,required = false)
	private String st_time;
	@DocBean(name ="end_time",value="结束时间:",dataType = String.class,required = false)
	private String end_time;
	@DocBean(name ="warning",value="错误信息:",dataType = String.class,required = false)
	private String warning;
	@DocBean(name ="main_serv_sync",value="同步标志位(Main_Server_Sync):L-锁定<LOCK> N-不同步<NO> Y-同步<YES> B-备份中<BACKUP> ",dataType = String.class,required = false)
	private String main_serv_sync;
	@DocBean(name ="etl_job",value="作业名:",dataType = String.class,required = true)
	private String etl_job;
	@DocBean(name ="etl_sys_cd",value="工程代码:",dataType = String.class,required = true)
	private String etl_sys_cd;

	/** 取得：干预发生时间 */
	public String getEvent_id(){
		return event_id;
	}
	/** 设置：干预发生时间 */
	public void setEvent_id(String event_id){
		this.event_id=event_id;
	}
	/** 取得：干预类型 */
	public String getEtl_hand_type(){
		return etl_hand_type;
	}
	/** 设置：干预类型 */
	public void setEtl_hand_type(String etl_hand_type){
		this.etl_hand_type=etl_hand_type;
	}
	/** 取得：干预参数 */
	public String getPro_para(){
		return pro_para;
	}
	/** 设置：干预参数 */
	public void setPro_para(String pro_para){
		this.pro_para=pro_para;
	}
	/** 取得：干预状态 */
	public String getHand_status(){
		return hand_status;
	}
	/** 设置：干预状态 */
	public void setHand_status(String hand_status){
		this.hand_status=hand_status;
	}
	/** 取得：开始时间 */
	public String getSt_time(){
		return st_time;
	}
	/** 设置：开始时间 */
	public void setSt_time(String st_time){
		this.st_time=st_time;
	}
	/** 取得：结束时间 */
	public String getEnd_time(){
		return end_time;
	}
	/** 设置：结束时间 */
	public void setEnd_time(String end_time){
		this.end_time=end_time;
	}
	/** 取得：错误信息 */
	public String getWarning(){
		return warning;
	}
	/** 设置：错误信息 */
	public void setWarning(String warning){
		this.warning=warning;
	}
	/** 取得：同步标志位 */
	public String getMain_serv_sync(){
		return main_serv_sync;
	}
	/** 设置：同步标志位 */
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
	/** 取得：工程代码 */
	public String getEtl_sys_cd(){
		return etl_sys_cd;
	}
	/** 设置：工程代码 */
	public void setEtl_sys_cd(String etl_sys_cd){
		this.etl_sys_cd=etl_sys_cd;
	}
}
