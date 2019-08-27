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
@Table(tableName = "datatable_own_source_info")
public class DatatableOwnSourceInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "datatable_own_source_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("own_dource_table_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String source_type;
	private String remark;
	private BigDecimal datatable_id;
	private BigDecimal own_dource_table_id;
	private String own_source_table_name;

	public String getSource_type() { return source_type; }
	public void setSource_type(String source_type) {
		if(source_type==null) throw new BusinessException("Entity : DatatableOwnSourceInfo.source_type must not null!");
		this.source_type = source_type;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getDatatable_id() { return datatable_id; }
	public void setDatatable_id(BigDecimal datatable_id) {
		if(datatable_id==null) throw new BusinessException("Entity : DatatableOwnSourceInfo.datatable_id must not null!");
		this.datatable_id = datatable_id;
	}

	public BigDecimal getOwn_dource_table_id() { return own_dource_table_id; }
	public void setOwn_dource_table_id(BigDecimal own_dource_table_id) {
		if(own_dource_table_id==null) throw new BusinessException("Entity : DatatableOwnSourceInfo.own_dource_table_id must not null!");
		this.own_dource_table_id = own_dource_table_id;
	}

	public String getOwn_source_table_name() { return own_source_table_name; }
	public void setOwn_source_table_name(String own_source_table_name) {
		if(own_source_table_name==null) throw new BusinessException("Entity : DatatableOwnSourceInfo.own_source_table_name must not null!");
		this.own_source_table_name = own_source_table_name;
	}

}