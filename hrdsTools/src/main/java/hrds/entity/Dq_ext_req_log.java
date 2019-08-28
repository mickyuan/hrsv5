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
 * 外部检查申请日志
 */
@Table(tableName = "dq_ext_req_log")
public class Dq_ext_req_log extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_ext_req_log";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 外部检查申请日志 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("req_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String ass_req_id; //关联申请号
	private String req_typ; //申请类型
	private Long req_id; //申请编号
	private Long ext_job_id; //外部作业编号
	private String chk_dt; //检查日期
	private String chk_time; //检查时间
	private String req_re; //受理返回状态
	private String fin_sts; //完成状态
	private String req_tm; //受理时间
	private String fin_tm; //结束时间
	private Long task_id; //任务编号

	/** 取得：关联申请号 */
	public String getAss_req_id(){
		return ass_req_id;
	}
	/** 设置：关联申请号 */
	public void setAss_req_id(String ass_req_id){
		this.ass_req_id=ass_req_id;
	}
	/** 取得：申请类型 */
	public String getReq_typ(){
		return req_typ;
	}
	/** 设置：申请类型 */
	public void setReq_typ(String req_typ){
		this.req_typ=req_typ;
	}
	/** 取得：申请编号 */
	public Long getReq_id(){
		return req_id;
	}
	/** 设置：申请编号 */
	public void setReq_id(Long req_id){
		this.req_id=req_id;
	}
	/** 设置：申请编号 */
	public void setReq_id(String req_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(req_id)){
			this.req_id=new Long(req_id);
		}
	}
	/** 取得：外部作业编号 */
	public Long getExt_job_id(){
		return ext_job_id;
	}
	/** 设置：外部作业编号 */
	public void setExt_job_id(Long ext_job_id){
		this.ext_job_id=ext_job_id;
	}
	/** 设置：外部作业编号 */
	public void setExt_job_id(String ext_job_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(ext_job_id)){
			this.ext_job_id=new Long(ext_job_id);
		}
	}
	/** 取得：检查日期 */
	public String getChk_dt(){
		return chk_dt;
	}
	/** 设置：检查日期 */
	public void setChk_dt(String chk_dt){
		this.chk_dt=chk_dt;
	}
	/** 取得：检查时间 */
	public String getChk_time(){
		return chk_time;
	}
	/** 设置：检查时间 */
	public void setChk_time(String chk_time){
		this.chk_time=chk_time;
	}
	/** 取得：受理返回状态 */
	public String getReq_re(){
		return req_re;
	}
	/** 设置：受理返回状态 */
	public void setReq_re(String req_re){
		this.req_re=req_re;
	}
	/** 取得：完成状态 */
	public String getFin_sts(){
		return fin_sts;
	}
	/** 设置：完成状态 */
	public void setFin_sts(String fin_sts){
		this.fin_sts=fin_sts;
	}
	/** 取得：受理时间 */
	public String getReq_tm(){
		return req_tm;
	}
	/** 设置：受理时间 */
	public void setReq_tm(String req_tm){
		this.req_tm=req_tm;
	}
	/** 取得：结束时间 */
	public String getFin_tm(){
		return fin_tm;
	}
	/** 设置：结束时间 */
	public void setFin_tm(String fin_tm){
		this.fin_tm=fin_tm;
	}
	/** 取得：任务编号 */
	public Long getTask_id(){
		return task_id;
	}
	/** 设置：任务编号 */
	public void setTask_id(Long task_id){
		this.task_id=task_id;
	}
	/** 设置：任务编号 */
	public void setTask_id(String task_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(task_id)){
			this.task_id=new Long(task_id);
		}
	}
}
