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
 * Agent下载信息
 */
@Table(tableName = "agent_down_info")
public class Agent_down_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "agent_down_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** Agent下载信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("down_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="down_id",value="下载编号(primary):",dataType = Long.class,required = true)
	private Long down_id;
	@DocBean(name ="agent_name",value="Agent名称:",dataType = String.class,required = true)
	private String agent_name;
	@DocBean(name ="agent_ip",value="Agent IP:",dataType = String.class,required = true)
	private String agent_ip;
	@DocBean(name ="agent_port",value="Agent端口:",dataType = String.class,required = true)
	private String agent_port;
	@DocBean(name ="save_dir",value="存放目录:",dataType = String.class,required = true)
	private String save_dir;
	@DocBean(name ="log_dir",value="日志目录:",dataType = String.class,required = true)
	private String log_dir;
	@DocBean(name ="deploy",value="是否部署(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String deploy;
	@DocBean(name ="ai_desc",value="描述:",dataType = String.class,required = false)
	private String ai_desc;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="agent_id",value="Agent_id:",dataType = Long.class,required = false)
	private Long agent_id;
	@DocBean(name ="user_name",value="用户名:",dataType = String.class,required = false)
	private String user_name;
	@DocBean(name ="passwd",value="密码:",dataType = String.class,required = false)
	private String passwd;
	@DocBean(name ="agent_type",value="agent类别(AgentType):1-数据库Agent<ShuJuKu> 2-文件系统Agent<WenJianXiTong> 3-FtpAgent<FTP> 4-数据文件Agent<DBWenJian> 5-对象Agent<DuiXiang> ",dataType = String.class,required = true)
	private String agent_type;
	@DocBean(name ="user_id",value="用户ID:",dataType = Long.class,required = true)
	private Long user_id;
	@DocBean(name ="agent_context",value="agent的context:",dataType = String.class,required = true)
	private String agent_context;
	@DocBean(name ="agent_pattern",value="agent的访问路径:",dataType = String.class,required = true)
	private String agent_pattern;

	/** 取得：下载编号(primary) */
	public Long getDown_id(){
		return down_id;
	}
	/** 设置：下载编号(primary) */
	public void setDown_id(Long down_id){
		this.down_id=down_id;
	}
	/** 设置：下载编号(primary) */
	public void setDown_id(String down_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(down_id)){
			this.down_id=new Long(down_id);
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
	/** 取得：Agent IP */
	public String getAgent_ip(){
		return agent_ip;
	}
	/** 设置：Agent IP */
	public void setAgent_ip(String agent_ip){
		this.agent_ip=agent_ip;
	}
	/** 取得：Agent端口 */
	public String getAgent_port(){
		return agent_port;
	}
	/** 设置：Agent端口 */
	public void setAgent_port(String agent_port){
		this.agent_port=agent_port;
	}
	/** 取得：存放目录 */
	public String getSave_dir(){
		return save_dir;
	}
	/** 设置：存放目录 */
	public void setSave_dir(String save_dir){
		this.save_dir=save_dir;
	}
	/** 取得：日志目录 */
	public String getLog_dir(){
		return log_dir;
	}
	/** 设置：日志目录 */
	public void setLog_dir(String log_dir){
		this.log_dir=log_dir;
	}
	/** 取得：是否部署 */
	public String getDeploy(){
		return deploy;
	}
	/** 设置：是否部署 */
	public void setDeploy(String deploy){
		this.deploy=deploy;
	}
	/** 取得：描述 */
	public String getAi_desc(){
		return ai_desc;
	}
	/** 设置：描述 */
	public void setAi_desc(String ai_desc){
		this.ai_desc=ai_desc;
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
	/** 取得：用户名 */
	public String getUser_name(){
		return user_name;
	}
	/** 设置：用户名 */
	public void setUser_name(String user_name){
		this.user_name=user_name;
	}
	/** 取得：密码 */
	public String getPasswd(){
		return passwd;
	}
	/** 设置：密码 */
	public void setPasswd(String passwd){
		this.passwd=passwd;
	}
	/** 取得：agent类别 */
	public String getAgent_type(){
		return agent_type;
	}
	/** 设置：agent类别 */
	public void setAgent_type(String agent_type){
		this.agent_type=agent_type;
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
	/** 取得：agent的context */
	public String getAgent_context(){
		return agent_context;
	}
	/** 设置：agent的context */
	public void setAgent_context(String agent_context){
		this.agent_context=agent_context;
	}
	/** 取得：agent的访问路径 */
	public String getAgent_pattern(){
		return agent_pattern;
	}
	/** 设置：agent的访问路径 */
	public void setAgent_pattern(String agent_pattern){
		this.agent_pattern=agent_pattern;
	}
}
