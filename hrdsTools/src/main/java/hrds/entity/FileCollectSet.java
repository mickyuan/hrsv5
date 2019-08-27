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
@Table(tableName = "file_collect_set")
public class FileCollectSet extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "file_collect_set";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("fcs_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String is_sendok;
	private BigDecimal fcs_id;
	private BigDecimal agent_id;
	private String system_type;
	private String fcs_name;
	private String remark;
	private String is_solr;
	private String host_name;

	public String getIs_sendok() { return is_sendok; }
	public void setIs_sendok(String is_sendok) {
		if(is_sendok==null) throw new BusinessException("Entity : FileCollectSet.is_sendok must not null!");
		this.is_sendok = is_sendok;
	}

	public BigDecimal getFcs_id() { return fcs_id; }
	public void setFcs_id(BigDecimal fcs_id) {
		if(fcs_id==null) throw new BusinessException("Entity : FileCollectSet.fcs_id must not null!");
		this.fcs_id = fcs_id;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) addNullValueField("agent_id");
		this.agent_id = agent_id;
	}

	public String getSystem_type() { return system_type; }
	public void setSystem_type(String system_type) {
		if(system_type==null) addNullValueField("system_type");
		this.system_type = system_type;
	}

	public String getFcs_name() { return fcs_name; }
	public void setFcs_name(String fcs_name) {
		if(fcs_name==null) throw new BusinessException("Entity : FileCollectSet.fcs_name must not null!");
		this.fcs_name = fcs_name;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getIs_solr() { return is_solr; }
	public void setIs_solr(String is_solr) {
		if(is_solr==null) throw new BusinessException("Entity : FileCollectSet.is_solr must not null!");
		this.is_solr = is_solr;
	}

	public String getHost_name() { return host_name; }
	public void setHost_name(String host_name) {
		if(host_name==null) addNullValueField("host_name");
		this.host_name = host_name;
	}

}