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
 * 任务信息表
 */
@Table(tableName = "task_info")
public class Task_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "task_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 任务信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("task_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long task_id; //任务ID
	private String task_name; //任务名称
	private String task_desc; //任务描述
	private Long create_id; //创建用户
	private Long project_id; //工程ID
	private Long agent_id; //作业agent
	private Long user_id; //用户ID

	/** 取得：任务ID */
	public Long getTask_id(){
		return task_id;
	}
	/** 设置：任务ID */
	public void setTask_id(Long task_id){
		this.task_id=task_id;
	}
	/** 设置：任务ID */
	public void setTask_id(String task_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(task_id)){
			this.task_id=new Long(task_id);
		}
	}
	/** 取得：任务名称 */
	public String getTask_name(){
		return task_name;
	}
	/** 设置：任务名称 */
	public void setTask_name(String task_name){
		this.task_name=task_name;
	}
	/** 取得：任务描述 */
	public String getTask_desc(){
		return task_desc;
	}
	/** 设置：任务描述 */
	public void setTask_desc(String task_desc){
		this.task_desc=task_desc;
	}
	/** 取得：创建用户 */
	public Long getCreate_id(){
		return create_id;
	}
	/** 设置：创建用户 */
	public void setCreate_id(Long create_id){
		this.create_id=create_id;
	}
	/** 设置：创建用户 */
	public void setCreate_id(String create_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(create_id)){
			this.create_id=new Long(create_id);
		}
	}
	/** 取得：工程ID */
	public Long getProject_id(){
		return project_id;
	}
	/** 设置：工程ID */
	public void setProject_id(Long project_id){
		this.project_id=project_id;
	}
	/** 设置：工程ID */
	public void setProject_id(String project_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(project_id)){
			this.project_id=new Long(project_id);
		}
	}
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
