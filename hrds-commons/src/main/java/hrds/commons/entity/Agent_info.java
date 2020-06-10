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
 * Agent信息表
 */
@Table(tableName = "agent_info")
public class Agent_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "agent_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** Agent信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("agent_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="agent_id",value="Agent_id:",dataType = Long.class,required = true)
	private Long agent_id;
	@DocBean(name ="agent_name",value="Agent名称:",dataType = String.class,required = true)
	private String agent_name;
	@DocBean(name ="agent_ip",value="Agent所在服务器IP:",dataType = String.class,required = true)
	private String agent_ip;
	@DocBean(name ="agent_port",value="agent服务器端口:",dataType = String.class,required = false)
	private String agent_port;
	@DocBean(name ="agent_status",value="agent状态(AgentStatus):1-已连接<YiLianJie> 2-未连接<WeiLianJie> 3-正在运行<ZhengZaiYunXing> ",dataType = String.class,required = true)
	private String agent_status;
	@DocBean(name ="agent_type",value="agent类别(AgentType):1-数据库Agent<ShuJuKu> 2-文件系统Agent<WenJianXiTong> 3-FtpAgent<FTP> 4-数据文件Agent<DBWenJian> 5-对象Agent<DuiXiang> ",dataType = String.class,required = true)
	private String agent_type;
	@DocBean(name ="create_date",value="创建日期:",dataType = String.class,required = true)
	private String create_date;
	@DocBean(name ="create_time",value="创建时间:",dataType = String.class,required = true)
	private String create_time;
	@DocBean(name ="source_id",value="数据源ID:",dataType = Long.class,required = true)
	private Long source_id;
	@DocBean(name ="user_id",value="用户ID:",dataType = Long.class,required = true)
	private Long user_id;

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
	/** 取得：agent类别 */
	public String getAgent_type(){
		return agent_type;
	}
	/** 设置：agent类别 */
	public void setAgent_type(String agent_type){
		this.agent_type=agent_type;
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
	/** 取得：数据源ID */
	public Long getSource_id(){
		return source_id;
	}
	/** 设置：数据源ID */
	public void setSource_id(Long source_id){
		this.source_id=source_id;
	}
	/** 设置：数据源ID */
	public void setSource_id(String source_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(source_id)){
			this.source_id=new Long(source_id);
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
