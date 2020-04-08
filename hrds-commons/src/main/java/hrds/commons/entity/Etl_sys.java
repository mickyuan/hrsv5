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
 * 作业工程登记表
 */
@Table(tableName = "etl_sys")
public class Etl_sys extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etl_sys";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 作业工程登记表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("etl_sys_cd");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="etl_sys_cd",value="工程代码:",dataType = String.class,required = true)
	private String etl_sys_cd;
	@DocBean(name ="etl_sys_name",value="工程名称:",dataType = String.class,required = true)
	private String etl_sys_name;
	@DocBean(name ="etl_serv_ip",value="etl服务器ip:",dataType = String.class,required = false)
	private String etl_serv_ip;
	@DocBean(name ="etl_serv_port",value="etl服务器端口:",dataType = String.class,required = false)
	private String etl_serv_port;
	@DocBean(name ="contact_person",value="联系人:",dataType = String.class,required = false)
	private String contact_person;
	@DocBean(name ="contact_phone",value="联系电话:",dataType = String.class,required = false)
	private String contact_phone;
	@DocBean(name ="comments",value="备注信息:",dataType = String.class,required = false)
	private String comments;
	@DocBean(name ="curr_bath_date",value="当前批量日期:",dataType = String.class,required = false)
	private String curr_bath_date;
	@DocBean(name ="bath_shift_time",value="系统日切时间:",dataType = String.class,required = false)
	private String bath_shift_time;
	@DocBean(name ="main_serv_sync",value="主服务器同步标志(Main_Server_Sync):L-锁定<LOCK> N-不同步<NO> Y-同步<YES> B-备份中<BACKUP> ",dataType = String.class,required = false)
	private String main_serv_sync;
	@DocBean(name ="sys_run_status",value="系统状态(Job_Status):D-完成<DONE> E-错误<ERROR> P-挂起<PENDING> R-运行<RUNNING> S-停止<STOP> W-等待<WAITING> ",dataType = String.class,required = false)
	private String sys_run_status;
	@DocBean(name ="serv_file_path",value="部署服务器路径:",dataType = String.class,required = false)
	private String serv_file_path;
	@DocBean(name ="user_name",value="主机服务器用户名:",dataType = String.class,required = false)
	private String user_name;
	@DocBean(name ="user_pwd",value="主机用户密码:",dataType = String.class,required = false)
	private String user_pwd;
	@DocBean(name ="remarks",value="备注:",dataType = String.class,required = false)
	private String remarks;
	@DocBean(name ="user_id",value="用户ID:",dataType = Long.class,required = true)
	private Long user_id;
	@DocBean(name ="etl_context",value="访问根:",dataType = String.class,required = false)
	private String etl_context;
	@DocBean(name ="etl_pattern",value="访问路径:",dataType = String.class,required = false)
	private String etl_pattern;

	/** 取得：工程代码 */
	public String getEtl_sys_cd(){
		return etl_sys_cd;
	}
	/** 设置：工程代码 */
	public void setEtl_sys_cd(String etl_sys_cd){
		this.etl_sys_cd=etl_sys_cd;
	}
	/** 取得：工程名称 */
	public String getEtl_sys_name(){
		return etl_sys_name;
	}
	/** 设置：工程名称 */
	public void setEtl_sys_name(String etl_sys_name){
		this.etl_sys_name=etl_sys_name;
	}
	/** 取得：etl服务器ip */
	public String getEtl_serv_ip(){
		return etl_serv_ip;
	}
	/** 设置：etl服务器ip */
	public void setEtl_serv_ip(String etl_serv_ip){
		this.etl_serv_ip=etl_serv_ip;
	}
	/** 取得：etl服务器端口 */
	public String getEtl_serv_port(){
		return etl_serv_port;
	}
	/** 设置：etl服务器端口 */
	public void setEtl_serv_port(String etl_serv_port){
		this.etl_serv_port=etl_serv_port;
	}
	/** 取得：联系人 */
	public String getContact_person(){
		return contact_person;
	}
	/** 设置：联系人 */
	public void setContact_person(String contact_person){
		this.contact_person=contact_person;
	}
	/** 取得：联系电话 */
	public String getContact_phone(){
		return contact_phone;
	}
	/** 设置：联系电话 */
	public void setContact_phone(String contact_phone){
		this.contact_phone=contact_phone;
	}
	/** 取得：备注信息 */
	public String getComments(){
		return comments;
	}
	/** 设置：备注信息 */
	public void setComments(String comments){
		this.comments=comments;
	}
	/** 取得：当前批量日期 */
	public String getCurr_bath_date(){
		return curr_bath_date;
	}
	/** 设置：当前批量日期 */
	public void setCurr_bath_date(String curr_bath_date){
		this.curr_bath_date=curr_bath_date;
	}
	/** 取得：系统日切时间 */
	public String getBath_shift_time(){
		return bath_shift_time;
	}
	/** 设置：系统日切时间 */
	public void setBath_shift_time(String bath_shift_time){
		this.bath_shift_time=bath_shift_time;
	}
	/** 取得：主服务器同步标志 */
	public String getMain_serv_sync(){
		return main_serv_sync;
	}
	/** 设置：主服务器同步标志 */
	public void setMain_serv_sync(String main_serv_sync){
		this.main_serv_sync=main_serv_sync;
	}
	/** 取得：系统状态 */
	public String getSys_run_status(){
		return sys_run_status;
	}
	/** 设置：系统状态 */
	public void setSys_run_status(String sys_run_status){
		this.sys_run_status=sys_run_status;
	}
	/** 取得：部署服务器路径 */
	public String getServ_file_path(){
		return serv_file_path;
	}
	/** 设置：部署服务器路径 */
	public void setServ_file_path(String serv_file_path){
		this.serv_file_path=serv_file_path;
	}
	/** 取得：主机服务器用户名 */
	public String getUser_name(){
		return user_name;
	}
	/** 设置：主机服务器用户名 */
	public void setUser_name(String user_name){
		this.user_name=user_name;
	}
	/** 取得：主机用户密码 */
	public String getUser_pwd(){
		return user_pwd;
	}
	/** 设置：主机用户密码 */
	public void setUser_pwd(String user_pwd){
		this.user_pwd=user_pwd;
	}
	/** 取得：备注 */
	public String getRemarks(){
		return remarks;
	}
	/** 设置：备注 */
	public void setRemarks(String remarks){
		this.remarks=remarks;
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
	/** 取得：访问根 */
	public String getEtl_context(){
		return etl_context;
	}
	/** 设置：访问根 */
	public void setEtl_context(String etl_context){
		this.etl_context=etl_context;
	}
	/** 取得：访问路径 */
	public String getEtl_pattern(){
		return etl_pattern;
	}
	/** 设置：访问路径 */
	public void setEtl_pattern(String etl_pattern){
		this.etl_pattern=etl_pattern;
	}
}
