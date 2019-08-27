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
@Table(tableName = "project_info")
public class ProjectInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "project_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("project_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal create_id;
	private String project_name;
	private BigDecimal agent_id;
	private BigDecimal project_id;
	private BigDecimal user_id;
	private String project_desc;

	public BigDecimal getCreate_id() { return create_id; }
	public void setCreate_id(BigDecimal create_id) {
		if(create_id==null) throw new BusinessException("Entity : ProjectInfo.create_id must not null!");
		this.create_id = create_id;
	}

	public String getProject_name() { return project_name; }
	public void setProject_name(String project_name) {
		if(project_name==null) throw new BusinessException("Entity : ProjectInfo.project_name must not null!");
		this.project_name = project_name;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : ProjectInfo.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public BigDecimal getProject_id() { return project_id; }
	public void setProject_id(BigDecimal project_id) {
		if(project_id==null) throw new BusinessException("Entity : ProjectInfo.project_id must not null!");
		this.project_id = project_id;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : ProjectInfo.user_id must not null!");
		this.user_id = user_id;
	}

	public String getProject_desc() { return project_desc; }
	public void setProject_desc(String project_desc) {
		if(project_desc==null) throw new BusinessException("Entity : ProjectInfo.project_desc must not null!");
		this.project_desc = project_desc;
	}

}