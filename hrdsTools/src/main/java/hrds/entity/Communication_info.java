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
 * 通讯监控信息表
 */
@Table(tableName = "communication_info")
public class Communication_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "communication_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 通讯监控信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("com_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long com_id; //通讯id
	private String name; //agent名称
	private String agent_type; //agent类型
	private String agent_ip; //AgentIP
	private String agent_port; //agent服务器端口
	private String is_normal; //通讯是否正常
	private String remark; //备注
	private String com_date; //通讯日期
	private String com_time; //通讯时间

	/** 取得：通讯id */
	public Long getCom_id(){
		return com_id;
	}
	/** 设置：通讯id */
	public void setCom_id(Long com_id){
		this.com_id=com_id;
	}
	/** 设置：通讯id */
	public void setCom_id(String com_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(com_id)){
			this.com_id=new Long(com_id);
		}
	}
	/** 取得：agent名称 */
	public String getName(){
		return name;
	}
	/** 设置：agent名称 */
	public void setName(String name){
		this.name=name;
	}
	/** 取得：agent类型 */
	public String getAgent_type(){
		return agent_type;
	}
	/** 设置：agent类型 */
	public void setAgent_type(String agent_type){
		this.agent_type=agent_type;
	}
	/** 取得：AgentIP */
	public String getAgent_ip(){
		return agent_ip;
	}
	/** 设置：AgentIP */
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
	/** 取得：通讯是否正常 */
	public String getIs_normal(){
		return is_normal;
	}
	/** 设置：通讯是否正常 */
	public void setIs_normal(String is_normal){
		this.is_normal=is_normal;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：通讯日期 */
	public String getCom_date(){
		return com_date;
	}
	/** 设置：通讯日期 */
	public void setCom_date(String com_date){
		this.com_date=com_date;
	}
	/** 取得：通讯时间 */
	public String getCom_time(){
		return com_time;
	}
	/** 设置：通讯时间 */
	public void setCom_time(String com_time){
		this.com_time=com_time;
	}
}
