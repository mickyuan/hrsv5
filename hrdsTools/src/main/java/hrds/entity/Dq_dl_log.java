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
 * 校验结果处理日志
 */
@Table(tableName = "dq_dl_log")
public class Dq_dl_log extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_dl_log";
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
	private Long reg_num; //规则编号
	private String dl_time; //处理时间
	private String prcs_stt; //流程状态
	private String actn; //动作
	private String dl_dscr; //处理描述
	private String attc; //附件
	private String fl_nm; //文件名
	private String is_top; //是否置顶
	private Long task_id; //任务编号
	private Long user_id; //用户ID

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
	public String getActn(){
		return actn;
	}
	/** 设置：动作 */
	public void setActn(String actn){
		this.actn=actn;
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
	public String getAttc(){
		return attc;
	}
	/** 设置：附件 */
	public void setAttc(String attc){
		this.attc=attc;
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
}
