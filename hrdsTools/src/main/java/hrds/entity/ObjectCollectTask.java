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
@Table(tableName = "object_collect_task")
public class ObjectCollectTask extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "object_collect_task";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ocs_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String zh_name;
	private BigDecimal agent_id;
	private BigDecimal odc_id;
	private String database_code;
	private BigDecimal ocs_id;
	private String en_name;
	private String collect_data_type;
	private String remark;

	public String getZh_name() { return zh_name; }
	public void setZh_name(String zh_name) {
		if(zh_name==null) throw new BusinessException("Entity : ObjectCollectTask.zh_name must not null!");
		this.zh_name = zh_name;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : ObjectCollectTask.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public BigDecimal getOdc_id() { return odc_id; }
	public void setOdc_id(BigDecimal odc_id) {
		if(odc_id==null) throw new BusinessException("Entity : ObjectCollectTask.odc_id must not null!");
		this.odc_id = odc_id;
	}

	public String getDatabase_code() { return database_code; }
	public void setDatabase_code(String database_code) {
		if(database_code==null) throw new BusinessException("Entity : ObjectCollectTask.database_code must not null!");
		this.database_code = database_code;
	}

	public BigDecimal getOcs_id() { return ocs_id; }
	public void setOcs_id(BigDecimal ocs_id) {
		if(ocs_id==null) throw new BusinessException("Entity : ObjectCollectTask.ocs_id must not null!");
		this.ocs_id = ocs_id;
	}

	public String getEn_name() { return en_name; }
	public void setEn_name(String en_name) {
		if(en_name==null) throw new BusinessException("Entity : ObjectCollectTask.en_name must not null!");
		this.en_name = en_name;
	}

	public String getCollect_data_type() { return collect_data_type; }
	public void setCollect_data_type(String collect_data_type) {
		if(collect_data_type==null) throw new BusinessException("Entity : ObjectCollectTask.collect_data_type must not null!");
		this.collect_data_type = collect_data_type;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

}