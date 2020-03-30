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
 * 校验结果处理日志
 */
@Table(tableName = "dq_req_log")
public class Dq_req_log extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_req_log";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 校验结果处理日志 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dl_time");
		__tmpPKS.add("task_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="dl_time",value="处理时间:",dataType = String.class,required = true)
	private String dl_time;
	@DocBean(name ="prcs_stt",value="流程状态:",dataType = String.class,required = false)
	private String prcs_stt;
	@DocBean(name ="dl_actn",value="动作:",dataType = String.class,required = false)
	private String dl_actn;
	@DocBean(name ="dl_dscr",value="处理描述:",dataType = String.class,required = false)
	private String dl_dscr;
	@DocBean(name ="dl_attc",value="附件:",dataType = String.class,required = false)
	private String dl_attc;
	@DocBean(name ="fl_nm",value="文件名:",dataType = String.class,required = false)
	private String fl_nm;
	@DocBean(name ="is_top",value="是否置顶(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_top;
	@DocBean(name ="task_id",value="任务编号:",dataType = Long.class,required = true)
	private Long task_id;
	@DocBean(name ="user_id",value="用户ID:",dataType = Long.class,required = true)
	private Long user_id;
	@DocBean(name ="reg_num",value="规则编号:",dataType = Long.class,required = true)
	private Long reg_num;

	/** 取得：处理时间 */
	public String getDl_time(){
		return dl_time;
	}
	/** 设置：处理时间 */
	public void setDl_time(String dl_time){
		this.dl_time=dl_time;
	}
	/** 取得：流程状态 */
	public String getPrcs_stt(){
		return prcs_stt;
	}
	/** 设置：流程状态 */
	public void setPrcs_stt(String prcs_stt){
		this.prcs_stt=prcs_stt;
	}
	/** 取得：动作 */
	public String getDl_actn(){
		return dl_actn;
	}
	/** 设置：动作 */
	public void setDl_actn(String dl_actn){
		this.dl_actn=dl_actn;
	}
	/** 取得：处理描述 */
	public String getDl_dscr(){
		return dl_dscr;
	}
	/** 设置：处理描述 */
	public void setDl_dscr(String dl_dscr){
		this.dl_dscr=dl_dscr;
	}
	/** 取得：附件 */
	public String getDl_attc(){
		return dl_attc;
	}
	/** 设置：附件 */
	public void setDl_attc(String dl_attc){
		this.dl_attc=dl_attc;
	}
	/** 取得：文件名 */
	public String getFl_nm(){
		return fl_nm;
	}
	/** 设置：文件名 */
	public void setFl_nm(String fl_nm){
		this.fl_nm=fl_nm;
	}
	/** 取得：是否置顶 */
	public String getIs_top(){
		return is_top;
	}
	/** 设置：是否置顶 */
	public void setIs_top(String is_top){
		this.is_top=is_top;
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
	/** 取得：规则编号 */
	public Long getReg_num(){
		return reg_num;
	}
	/** 设置：规则编号 */
	public void setReg_num(Long reg_num){
		this.reg_num=reg_num;
	}
	/** 设置：规则编号 */
	public void setReg_num(String reg_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(reg_num)){
			this.reg_num=new Long(reg_num);
		}
	}
}
