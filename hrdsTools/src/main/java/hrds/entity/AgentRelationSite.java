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
@Table(tableName = "agent_relation_site")
public class AgentRelationSite extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "agent_relation_site";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("agent_id");
		__tmpPKS.add("site_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal agent_id;
	private BigDecimal site_id;

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : AgentRelationSite.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public BigDecimal getSite_id() { return site_id; }
	public void setSite_id(BigDecimal site_id) {
		if(site_id==null) throw new BusinessException("Entity : AgentRelationSite.site_id must not null!");
		this.site_id = site_id;
	}

}