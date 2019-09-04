package hrds.entity;

import fd.ng.db.entity.TableEntity;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 实体类中所有属性都应定义为对象，不要使用int等主类型，方便对null值的操作
 */
@Table(tableName = "agent_down_info")
public class AgentDownInfo extends TableEntity {
    private static final long serialVersionUID = 321566460595860L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "agent_down_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("down_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal down_id;
	private String agent_name;
	private BigDecimal agent_id;
	private String user_name;
	private String remark;
	private String deploy;
	private String agent_ip;
	private String save_dir;
	private String agent_type;
	private String log_dir;
	private String passwd;
	private BigDecimal user_id;
	private String agent_port;
	private String ai_desc;

	public BigDecimal getDown_id() { return down_id; }
	public void setDown_id(BigDecimal down_id) {
		if(down_id==null) throw new BusinessException("Entity : AgentDownInfo.down_id must not null!");
		this.down_id = down_id;
	}

	public String getAgent_name() { return agent_name; }
	public void setAgent_name(String agent_name) {
		if(agent_name==null) throw new BusinessException("Entity : AgentDownInfo.agent_name must not null!");
		this.agent_name = agent_name;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) addNullValueField("agent_id");
		this.agent_id = agent_id;
	}

	public String getUser_name() { return user_name; }
	public void setUser_name(String user_name) {
		if(user_name==null) addNullValueField("user_name");
		this.user_name = user_name;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getDeploy() { return deploy; }
	public void setDeploy(String deploy) {
		if(deploy==null) throw new BusinessException("Entity : AgentDownInfo.deploy must not null!");
		this.deploy = deploy;
	}

	public String getAgent_ip() { return agent_ip; }
	public void setAgent_ip(String agent_ip) {
		if(agent_ip==null) throw new BusinessException("Entity : AgentDownInfo.agent_ip must not null!");
		this.agent_ip = agent_ip;
	}

	public String getSave_dir() { return save_dir; }
	public void setSave_dir(String save_dir) {
		if(save_dir==null) throw new BusinessException("Entity : AgentDownInfo.save_dir must not null!");
		this.save_dir = save_dir;
	}

	public String getAgent_type() { return agent_type; }
	public void setAgent_type(String agent_type) {
		if(agent_type==null) throw new BusinessException("Entity : AgentDownInfo.agent_type must not null!");
		this.agent_type = agent_type;
	}

	public String getLog_dir() { return log_dir; }
	public void setLog_dir(String log_dir) {
		if(log_dir==null) throw new BusinessException("Entity : AgentDownInfo.log_dir must not null!");
		this.log_dir = log_dir;
	}

	public String getPasswd() { return passwd; }
	public void setPasswd(String passwd) {
		if(passwd==null) addNullValueField("passwd");
		this.passwd = passwd;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : AgentDownInfo.user_id must not null!");
		this.user_id = user_id;
	}

	public String getAgent_port() { return agent_port; }
	public void setAgent_port(String agent_port) {
		if(agent_port==null) throw new BusinessException("Entity : AgentDownInfo.agent_port must not null!");
		this.agent_port = agent_port;
	}

	public String getAi_desc() { return ai_desc; }
	public void setAi_desc(String ai_desc) {
		if(ai_desc==null) addNullValueField("ai_desc");
		this.ai_desc = ai_desc;
	}

}