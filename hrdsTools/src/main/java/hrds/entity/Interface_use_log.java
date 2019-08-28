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
 * 接口使用信息日志表
 */
@Table(tableName = "interface_use_log")
public class Interface_use_log extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "interface_use_log";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 接口使用信息日志表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("log_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long log_id; //日志ID
	private String interface_name; //接口名称
	private String request_state; //请求状态
	private Long response_time; //响应时间
	private Long interface_use_id; //接口使用ID
	private Long user_id; //用户ID
	private String browser_type; //浏览器类型
	private String browser_version; //浏览器版本
	private String system_type; //系统类型
	private String request_mode; //请求方式
	private String remoteaddr; //客户端的IP
	private String protocol; //超文本传输协议版本
	private String request_info; //请求信息
	private String request_stime; //请求起始时间
	private String request_etime; //请求结束时间
	private String request_type; //请求类型
	private String user_name; //用户名称

	/** 取得：日志ID */
	public Long getLog_id(){
		return log_id;
	}
	/** 设置：日志ID */
	public void setLog_id(Long log_id){
		this.log_id=log_id;
	}
	/** 设置：日志ID */
	public void setLog_id(String log_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(log_id)){
			this.log_id=new Long(log_id);
		}
	}
	/** 取得：接口名称 */
	public String getInterface_name(){
		return interface_name;
	}
	/** 设置：接口名称 */
	public void setInterface_name(String interface_name){
		this.interface_name=interface_name;
	}
	/** 取得：请求状态 */
	public String getRequest_state(){
		return request_state;
	}
	/** 设置：请求状态 */
	public void setRequest_state(String request_state){
		this.request_state=request_state;
	}
	/** 取得：响应时间 */
	public Long getResponse_time(){
		return response_time;
	}
	/** 设置：响应时间 */
	public void setResponse_time(Long response_time){
		this.response_time=response_time;
	}
	/** 设置：响应时间 */
	public void setResponse_time(String response_time){
		if(!fd.ng.core.utils.StringUtil.isEmpty(response_time)){
			this.response_time=new Long(response_time);
		}
	}
	/** 取得：接口使用ID */
	public Long getInterface_use_id(){
		return interface_use_id;
	}
	/** 设置：接口使用ID */
	public void setInterface_use_id(Long interface_use_id){
		this.interface_use_id=interface_use_id;
	}
	/** 设置：接口使用ID */
	public void setInterface_use_id(String interface_use_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(interface_use_id)){
			this.interface_use_id=new Long(interface_use_id);
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
	/** 取得：浏览器类型 */
	public String getBrowser_type(){
		return browser_type;
	}
	/** 设置：浏览器类型 */
	public void setBrowser_type(String browser_type){
		this.browser_type=browser_type;
	}
	/** 取得：浏览器版本 */
	public String getBrowser_version(){
		return browser_version;
	}
	/** 设置：浏览器版本 */
	public void setBrowser_version(String browser_version){
		this.browser_version=browser_version;
	}
	/** 取得：系统类型 */
	public String getSystem_type(){
		return system_type;
	}
	/** 设置：系统类型 */
	public void setSystem_type(String system_type){
		this.system_type=system_type;
	}
	/** 取得：请求方式 */
	public String getRequest_mode(){
		return request_mode;
	}
	/** 设置：请求方式 */
	public void setRequest_mode(String request_mode){
		this.request_mode=request_mode;
	}
	/** 取得：客户端的IP */
	public String getRemoteaddr(){
		return remoteaddr;
	}
	/** 设置：客户端的IP */
	public void setRemoteaddr(String remoteaddr){
		this.remoteaddr=remoteaddr;
	}
	/** 取得：超文本传输协议版本 */
	public String getProtocol(){
		return protocol;
	}
	/** 设置：超文本传输协议版本 */
	public void setProtocol(String protocol){
		this.protocol=protocol;
	}
	/** 取得：请求信息 */
	public String getRequest_info(){
		return request_info;
	}
	/** 设置：请求信息 */
	public void setRequest_info(String request_info){
		this.request_info=request_info;
	}
	/** 取得：请求起始时间 */
	public String getRequest_stime(){
		return request_stime;
	}
	/** 设置：请求起始时间 */
	public void setRequest_stime(String request_stime){
		this.request_stime=request_stime;
	}
	/** 取得：请求结束时间 */
	public String getRequest_etime(){
		return request_etime;
	}
	/** 设置：请求结束时间 */
	public void setRequest_etime(String request_etime){
		this.request_etime=request_etime;
	}
	/** 取得：请求类型 */
	public String getRequest_type(){
		return request_type;
	}
	/** 设置：请求类型 */
	public void setRequest_type(String request_type){
		this.request_type=request_type;
	}
	/** 取得：用户名称 */
	public String getUser_name(){
		return user_name;
	}
	/** 设置：用户名称 */
	public void setUser_name(String user_name){
		this.user_name=user_name;
	}
}
