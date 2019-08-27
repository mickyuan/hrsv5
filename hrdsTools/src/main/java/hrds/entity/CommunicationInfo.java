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
@Table(tableName = "communication_info")
public class CommunicationInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "communication_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("com_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String agent_type;
	private String agent_ip;
	private String com_time;
	private String is_normal;
	private BigDecimal com_id;
	private String name;
	private String agent_port;
	private String remark;
	private String com_date;

	public String getAgent_type() { return agent_type; }
	public void setAgent_type(String agent_type) {
		if(agent_type==null) throw new BusinessException("Entity : CommunicationInfo.agent_type must not null!");
		this.agent_type = agent_type;
	}

	public String getAgent_ip() { return agent_ip; }
	public void setAgent_ip(String agent_ip) {
		if(agent_ip==null) throw new BusinessException("Entity : CommunicationInfo.agent_ip must not null!");
		this.agent_ip = agent_ip;
	}

	public String getCom_time() { return com_time; }
	public void setCom_time(String com_time) {
		if(com_time==null) throw new BusinessException("Entity : CommunicationInfo.com_time must not null!");
		this.com_time = com_time;
	}

	public String getIs_normal() { return is_normal; }
	public void setIs_normal(String is_normal) {
		if(is_normal==null) throw new BusinessException("Entity : CommunicationInfo.is_normal must not null!");
		this.is_normal = is_normal;
	}

	public BigDecimal getCom_id() { return com_id; }
	public void setCom_id(BigDecimal com_id) {
		if(com_id==null) throw new BusinessException("Entity : CommunicationInfo.com_id must not null!");
		this.com_id = com_id;
	}

	public String getName() { return name; }
	public void setName(String name) {
		if(name==null) throw new BusinessException("Entity : CommunicationInfo.name must not null!");
		this.name = name;
	}

	public String getAgent_port() { return agent_port; }
	public void setAgent_port(String agent_port) {
		if(agent_port==null) throw new BusinessException("Entity : CommunicationInfo.agent_port must not null!");
		this.agent_port = agent_port;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getCom_date() { return com_date; }
	public void setCom_date(String com_date) {
		if(com_date==null) throw new BusinessException("Entity : CommunicationInfo.com_date must not null!");
		this.com_date = com_date;
	}

}