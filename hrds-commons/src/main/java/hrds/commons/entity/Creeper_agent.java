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
 * 爬虫agent
 */
@Table(tableName = "creeper_agent")
public class Creeper_agent extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "creeper_agent";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 爬虫agent */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("agent_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long agent_id; //Agent_id
	private String agent_name; //Agent名称
	private String agent_ip; //Agent所在服务器IP
	private String agent_port; //agent服务器端口
	private String agent_status; //agent状态
	private String create_date; //创建日期
	private String create_time; //创建时间
	private Long user_id; //用户ID
	private Long cmsg_id; //配置ID
	private String file_path; //文件存储路径
	private Long create_id; //用户ID

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
	/** 取得：Agent名称 */
	public String getAgent_name(){
		return agent_name;
	}
	/** 设置：Agent名称 */
	public void setAgent_name(String agent_name){
		this.agent_name=agent_name;
	}
	/** 取得：Agent所在服务器IP */
	public String getAgent_ip(){
		return agent_ip;
	}
	/** 设置：Agent所在服务器IP */
	public void setAgent_ip(String agent_ip){
		this.agent_ip=agent_ip;
	}
	/** 取得：agent服务器端口 */
	public String getAgent_port(){
		return agent_port;
	}
	/** 设置：agent服务器端口 */
	public void setAgent_port(String agent_port){
		this.agent_port=agent_port;
	}
	/** 取得：agent状态 */
	public String getAgent_status(){
		return agent_status;
	}
	/** 设置：agent状态 */
	public void setAgent_status(String agent_status){
		this.agent_status=agent_status;
	}
	/** 取得：创建日期 */
	public String getCreate_date(){
		return create_date;
	}
	/** 设置：创建日期 */
	public void setCreate_date(String create_date){
		this.create_date=create_date;
	}
	/** 取得：创建时间 */
	public String getCreate_time(){
		return create_time;
	}
	/** 设置：创建时间 */
	public void setCreate_time(String create_time){
		this.create_time=create_time;
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
	/** 取得：配置ID */
	public Long getCmsg_id(){
		return cmsg_id;
	}
	/** 设置：配置ID */
	public void setCmsg_id(Long cmsg_id){
		this.cmsg_id=cmsg_id;
	}
	/** 设置：配置ID */
	public void setCmsg_id(String cmsg_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(cmsg_id)){
			this.cmsg_id=new Long(cmsg_id);
		}
	}
	/** 取得：文件存储路径 */
	public String getFile_path(){
		return file_path;
	}
	/** 设置：文件存储路径 */
	public void setFile_path(String file_path){
		this.file_path=file_path;
	}
	/** 取得：用户ID */
	public Long getCreate_id(){
		return create_id;
	}
	/** 设置：用户ID */
	public void setCreate_id(Long create_id){
		this.create_id=create_id;
	}
	/** 设置：用户ID */
	public void setCreate_id(String create_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(create_id)){
			this.create_id=new Long(create_id);
		}
	}
}
