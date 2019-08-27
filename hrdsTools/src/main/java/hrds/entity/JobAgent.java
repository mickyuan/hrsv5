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
@Table(tableName = "job_agent")
public class JobAgent extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "job_agent";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("agent_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal run_id;
	private BigDecimal agent_id;
	private String agent_name;
	private BigDecimal user_id;
	private String agent_status;
	private String job_agent_port;
	private String agent_remark;

	public BigDecimal getRun_id() { return run_id; }
	public void setRun_id(BigDecimal run_id) {
		if(run_id==null) addNullValueField("run_id");
		this.run_id = run_id;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : JobAgent.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public String getAgent_name() { return agent_name; }
	public void setAgent_name(String agent_name) {
		if(agent_name==null) throw new BusinessException("Entity : JobAgent.agent_name must not null!");
		this.agent_name = agent_name;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : JobAgent.user_id must not null!");
		this.user_id = user_id;
	}

	public String getAgent_status() { return agent_status; }
	public void setAgent_status(String agent_status) {
		if(agent_status==null) throw new BusinessException("Entity : JobAgent.agent_status must not null!");
		this.agent_status = agent_status;
	}

	public String getJob_agent_port() { return job_agent_port; }
	public void setJob_agent_port(String job_agent_port) {
		if(job_agent_port==null) throw new BusinessException("Entity : JobAgent.job_agent_port must not null!");
		this.job_agent_port = job_agent_port;
	}

	public String getAgent_remark() { return agent_remark; }
	public void setAgent_remark(String agent_remark) {
		if(agent_remark==null) addNullValueField("agent_remark");
		this.agent_remark = agent_remark;
	}

}