package hrds.entity;

import fd.ng.db.entity.TableEntity;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 实体类中所有属性都应定义为对象，不要使用int等主类型，方便对null值的操作
 */
@Table(tableName = "task_info")
public class TaskInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "task_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("task_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String task_name;
	private BigDecimal create_id;
	private BigDecimal agent_id;
	private BigDecimal user_id;
	private BigDecimal project_id;
	private BigDecimal task_id;
	private String task_desc;

	public String getTask_name() { return task_name; }
	public void setTask_name(String task_name) {
		if(task_name==null) throw new BusinessException("Entity : TaskInfo.task_name must not null!");
		this.task_name = task_name;
	}

	public BigDecimal getCreate_id() { return create_id; }
	public void setCreate_id(BigDecimal create_id) {
		if(create_id==null) throw new BusinessException("Entity : TaskInfo.create_id must not null!");
		this.create_id = create_id;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : TaskInfo.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : TaskInfo.user_id must not null!");
		this.user_id = user_id;
	}

	public BigDecimal getProject_id() { return project_id; }
	public void setProject_id(BigDecimal project_id) {
		if(project_id==null) throw new BusinessException("Entity : TaskInfo.project_id must not null!");
		this.project_id = project_id;
	}

	public BigDecimal getTask_id() { return task_id; }
	public void setTask_id(BigDecimal task_id) {
		if(task_id==null) throw new BusinessException("Entity : TaskInfo.task_id must not null!");
		this.task_id = task_id;
	}

	public String getTask_desc() { return task_desc; }
	public void setTask_desc(String task_desc) {
		if(task_desc==null) addNullValueField("task_desc");
		this.task_desc = task_desc;
	}

}