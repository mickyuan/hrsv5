package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.commons.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * Ftp采集设置
 */
@Table(tableName = "ftp_collect")
public class Ftp_collect extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ftp_collect";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** Ftp采集设置 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ftp_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long ftp_id; //ftp采集id
	private String ftp_number; //ftp任务编号
	private String ftp_name; //ftp采集任务名称
	private String start_date; //开始日期
	private String end_date; //结束日期
	private String ftp_ip; //ftp服务IP
	private String ftp_port; //ftp服务器端口
	private String ftp_username; //ftp用户名
	private String ftp_password; //用户密码
	private String ftp_dir; //ftp服务器目录
	private String local_path; //本地路径
	private String file_suffix; //获取文件后缀
	private String run_way; //启动方式
	private String remark; //备注
	private Long agent_id; //Agent_id
	private String ftp_rule_path; //下级目录规则
	private String child_file_path; //下级文件路径
	private String child_time; //下级文件时间
	private String is_sendok; //是否完成
	private String is_unzip; //是否解压
	private String reduce_type; //解压格式
	private String ftp_model; //FTP推拉模式是为推模式

	/** 取得：ftp采集id */
	public Long getFtp_id(){
		return ftp_id;
	}
	/** 设置：ftp采集id */
	public void setFtp_id(Long ftp_id){
		this.ftp_id=ftp_id;
	}
	/** 设置：ftp采集id */
	public void setFtp_id(String ftp_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(ftp_id)){
			this.ftp_id=new Long(ftp_id);
		}
	}
	/** 取得：ftp任务编号 */
	public String getFtp_number(){
		return ftp_number;
	}
	/** 设置：ftp任务编号 */
	public void setFtp_number(String ftp_number){
		this.ftp_number=ftp_number;
	}
	/** 取得：ftp采集任务名称 */
	public String getFtp_name(){
		return ftp_name;
	}
	/** 设置：ftp采集任务名称 */
	public void setFtp_name(String ftp_name){
		this.ftp_name=ftp_name;
	}
	/** 取得：开始日期 */
	public String getStart_date(){
		return start_date;
	}
	/** 设置：开始日期 */
	public void setStart_date(String start_date){
		this.start_date=start_date;
	}
	/** 取得：结束日期 */
	public String getEnd_date(){
		return end_date;
	}
	/** 设置：结束日期 */
	public void setEnd_date(String end_date){
		this.end_date=end_date;
	}
	/** 取得：ftp服务IP */
	public String getFtp_ip(){
		return ftp_ip;
	}
	/** 设置：ftp服务IP */
	public void setFtp_ip(String ftp_ip){
		this.ftp_ip=ftp_ip;
	}
	/** 取得：ftp服务器端口 */
	public String getFtp_port(){
		return ftp_port;
	}
	/** 设置：ftp服务器端口 */
	public void setFtp_port(String ftp_port){
		this.ftp_port=ftp_port;
	}
	/** 取得：ftp用户名 */
	public String getFtp_username(){
		return ftp_username;
	}
	/** 设置：ftp用户名 */
	public void setFtp_username(String ftp_username){
		this.ftp_username=ftp_username;
	}
	/** 取得：用户密码 */
	public String getFtp_password(){
		return ftp_password;
	}
	/** 设置：用户密码 */
	public void setFtp_password(String ftp_password){
		this.ftp_password=ftp_password;
	}
	/** 取得：ftp服务器目录 */
	public String getFtp_dir(){
		return ftp_dir;
	}
	/** 设置：ftp服务器目录 */
	public void setFtp_dir(String ftp_dir){
		this.ftp_dir=ftp_dir;
	}
	/** 取得：本地路径 */
	public String getLocal_path(){
		return local_path;
	}
	/** 设置：本地路径 */
	public void setLocal_path(String local_path){
		this.local_path=local_path;
	}
	/** 取得：获取文件后缀 */
	public String getFile_suffix(){
		return file_suffix;
	}
	/** 设置：获取文件后缀 */
	public void setFile_suffix(String file_suffix){
		this.file_suffix=file_suffix;
	}
	/** 取得：启动方式 */
	public String getRun_way(){
		return run_way;
	}
	/** 设置：启动方式 */
	public void setRun_way(String run_way){
		this.run_way=run_way;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：Agent_id */
	public Long getAgent_id(){
		return agent_id;
	}
	/** 设置：Agent_id */
	public void setAgent_id(Long agent_id){
		this.agent_id=agent_id;
	}
	/** 设置：Agent_id */
	public void setAgent_id(String agent_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(agent_id)){
			this.agent_id=new Long(agent_id);
		}
	}
	/** 取得：下级目录规则 */
	public String getFtp_rule_path(){
		return ftp_rule_path;
	}
	/** 设置：下级目录规则 */
	public void setFtp_rule_path(String ftp_rule_path){
		this.ftp_rule_path=ftp_rule_path;
	}
	/** 取得：下级文件路径 */
	public String getChild_file_path(){
		return child_file_path;
	}
	/** 设置：下级文件路径 */
	public void setChild_file_path(String child_file_path){
		this.child_file_path=child_file_path;
	}
	/** 取得：下级文件时间 */
	public String getChild_time(){
		return child_time;
	}
	/** 设置：下级文件时间 */
	public void setChild_time(String child_time){
		this.child_time=child_time;
	}
	/** 取得：是否完成 */
	public String getIs_sendok(){
		return is_sendok;
	}
	/** 设置：是否完成 */
	public void setIs_sendok(String is_sendok){
		this.is_sendok=is_sendok;
	}
	/** 取得：是否解压 */
	public String getIs_unzip(){
		return is_unzip;
	}
	/** 设置：是否解压 */
	public void setIs_unzip(String is_unzip){
		this.is_unzip=is_unzip;
	}
	/** 取得：解压格式 */
	public String getReduce_type(){
		return reduce_type;
	}
	/** 设置：解压格式 */
	public void setReduce_type(String reduce_type){
		this.reduce_type=reduce_type;
	}
	/** 取得：FTP推拉模式是为推模式 */
	public String getFtp_model(){
		return ftp_model;
	}
	/** 设置：FTP推拉模式是为推模式 */
	public void setFtp_model(String ftp_model){
		this.ftp_model=ftp_model;
	}
}
