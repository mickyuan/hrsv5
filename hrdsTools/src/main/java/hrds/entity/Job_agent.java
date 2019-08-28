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
 * 作业agent
 */
@Table(tableName = "job_agent")
public class Job_agent extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "job_agent";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 作业agent */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("agent_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long agent_id; //作业agent
	private String agent_name; //作业agent名称
	private String agent_remark; //备注
	private Long run_id; //运行主机ID
	private String job_agent_port; //agent服务器端口
	private String agent_status; //agent状态
	private Long user_id; //用户ID

	/** 取得：作业agent */
	public Long getAgent_id(){
		return agent_id;
	}
	/** 设置：作业agent */
	public void setAgent_id(Long agent_id){
		this.agent_id=agent_id;
	}
	/** 设置：作业agent */
	public void setAgent_id(String agent_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(agent_id)){
			this.agent_id=new Long(agent_id);
		}
	}
	/** 取得：作业agent名称 */
	public String getAgent_name(){
		return agent_name;
	}
	/** 设置：作业agent名称 */
	public void setAgent_name(String agent_name){
		this.agent_name=agent_name;
	}
	/** 取得：备注 */
	public String getAgent_remark(){
		return agent_remark;
	}
	/** 设置：备注 */
	public void setAgent_remark(String agent_remark){
		this.agent_remark=agent_remark;
	}
	/** 取得：运行主机ID */
	public Long getRun_id(){
		return run_id;
	}
	/** 设置：运行主机ID */
	public void setRun_id(Long run_id){
		this.run_id=run_id;
	}
	/** 设置：运行主机ID */
	public void setRun_id(String run_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(run_id)){
			this.run_id=new Long(run_id);
		}
	}
	/** 取得：agent服务器端口 */
	public String getJob_agent_port(){
		return job_agent_port;
	}
	/** 设置：agent服务器端口 */
	public void setJob_agent_port(String job_agent_port){
		this.job_agent_port=job_agent_port;
	}
	/** 取得：agent状态 */
	public String getAgent_status(){
		return agent_status;
	}
	/** 设置：agent状态 */
	public void setAgent_status(String agent_status){
		this.agent_status=agent_status;
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
