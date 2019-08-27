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
@Table(tableName = "etlmap_info")
public class EtlmapInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etlmap_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("etl_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String remark;
	private BigDecimal etl_id;
	private BigDecimal datatable_id;
	private String targetfield_name;
	private BigDecimal own_dource_table_id;
	private String sourcefields_name;

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getEtl_id() { return etl_id; }
	public void setEtl_id(BigDecimal etl_id) {
		if(etl_id==null) throw new BusinessException("Entity : EtlmapInfo.etl_id must not null!");
		this.etl_id = etl_id;
	}

	public BigDecimal getDatatable_id() { return datatable_id; }
	public void setDatatable_id(BigDecimal datatable_id) {
		if(datatable_id==null) throw new BusinessException("Entity : EtlmapInfo.datatable_id must not null!");
		this.datatable_id = datatable_id;
	}

	public String getTargetfield_name() { return targetfield_name; }
	public void setTargetfield_name(String targetfield_name) {
		if(targetfield_name==null) addNullValueField("targetfield_name");
		this.targetfield_name = targetfield_name;
	}

	public BigDecimal getOwn_dource_table_id() { return own_dource_table_id; }
	public void setOwn_dource_table_id(BigDecimal own_dource_table_id) {
		if(own_dource_table_id==null) throw new BusinessException("Entity : EtlmapInfo.own_dource_table_id must not null!");
		this.own_dource_table_id = own_dource_table_id;
	}

	public String getSourcefields_name() { return sourcefields_name; }
	public void setSourcefields_name(String sourcefields_name) {
		if(sourcefields_name==null) throw new BusinessException("Entity : EtlmapInfo.sourcefields_name must not null!");
		this.sourcefields_name = sourcefields_name;
	}

}