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
 * 系统操作信息
 */
@Table(tableName = "login_operation_info")
public class Login_operation_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "login_operation_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 系统操作信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("log_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="log_id",value="日志ID:",dataType = Long.class,required = true)
	private Long log_id;
	@DocBean(name ="browser_type",value="浏览器类型:",dataType = String.class,required = false)
	private String browser_type;
	@DocBean(name ="browser_version",value="浏览器版本:",dataType = String.class,required = false)
	private String browser_version;
	@DocBean(name ="system_type",value="系统类型:",dataType = String.class,required = false)
	private String system_type;
	@DocBean(name ="request_mode",value="请求方式:",dataType = String.class,required = false)
	private String request_mode;
	@DocBean(name ="remoteaddr",value="客户端的IP:",dataType = String.class,required = false)
	private String remoteaddr;
	@DocBean(name ="protocol",value="超文本传输协议版本:",dataType = String.class,required = false)
	private String protocol;
	@DocBean(name ="request_date",value="请求日期:",dataType = String.class,required = true)
	private String request_date;
	@DocBean(name ="request_type",value="请求类型:",dataType = String.class,required = false)
	private String request_type;
	@DocBean(name ="user_name",value="用户名称:",dataType = String.class,required = false)
	private String user_name;
	@DocBean(name ="operation_type",value="操作类型:",dataType = String.class,required = false)
	private String operation_type;
	@DocBean(name ="request_time",value="请求时间:",dataType = String.class,required = true)
	private String request_time;
	@DocBean(name ="user_id",value="用户ID:",dataType = Long.class,required = true)
	private Long user_id;

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
	/** 取得：请求日期 */
	public String getRequest_date(){
		return request_date;
	}
	/** 设置：请求日期 */
	public void setRequest_date(String request_date){
		this.request_date=request_date;
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
	/** 取得：操作类型 */
	public String getOperation_type(){
		return operation_type;
	}
	/** 设置：操作类型 */
	public void setOperation_type(String operation_type){
		this.operation_type=operation_type;
	}
	/** 取得：请求时间 */
	public String getRequest_time(){
		return request_time;
	}
	/** 设置：请求时间 */
	public void setRequest_time(String request_time){
		this.request_time=request_time;
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
