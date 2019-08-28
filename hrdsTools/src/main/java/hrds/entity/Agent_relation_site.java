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
 * agent和站点关系表
 */
@Table(tableName = "agent_relation_site")
public class Agent_relation_site extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "agent_relation_site";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** agent和站点关系表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("agent_id");
		__tmpPKS.add("site_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long agent_id; //Agent_id
	private Long site_id; //站点ID

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
	/** 取得：站点ID */
	public Long getSite_id(){
		return site_id;
	}
	/** 设置：站点ID */
	public void setSite_id(Long site_id){
		this.site_id=site_id;
	}
	/** 设置：站点ID */
	public void setSite_id(String site_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(site_id)){
			this.site_id=new Long(site_id);
		}
	}
}
