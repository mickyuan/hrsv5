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
@Table(tableName = "sdm_con_hbase")
public class SdmConHbase extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_hbase";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("hbase_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String hbase_bus_class;
	private String hbase_family;
	private BigDecimal hbase_id;
	private String hbase_bus_type;
	private String rowkey_separator;
	private BigDecimal sdm_des_id;
	private String remark;
	private String pre_partition;
	private String hbase_name;

	public String getHbase_bus_class() { return hbase_bus_class; }
	public void setHbase_bus_class(String hbase_bus_class) {
		if(hbase_bus_class==null) addNullValueField("hbase_bus_class");
		this.hbase_bus_class = hbase_bus_class;
	}

	public String getHbase_family() { return hbase_family; }
	public void setHbase_family(String hbase_family) {
		if(hbase_family==null) throw new BusinessException("Entity : SdmConHbase.hbase_family must not null!");
		this.hbase_family = hbase_family;
	}

	public BigDecimal getHbase_id() { return hbase_id; }
	public void setHbase_id(BigDecimal hbase_id) {
		if(hbase_id==null) throw new BusinessException("Entity : SdmConHbase.hbase_id must not null!");
		this.hbase_id = hbase_id;
	}

	public String getHbase_bus_type() { return hbase_bus_type; }
	public void setHbase_bus_type(String hbase_bus_type) {
		if(hbase_bus_type==null) throw new BusinessException("Entity : SdmConHbase.hbase_bus_type must not null!");
		this.hbase_bus_type = hbase_bus_type;
	}

	public String getRowkey_separator() { return rowkey_separator; }
	public void setRowkey_separator(String rowkey_separator) {
		if(rowkey_separator==null) addNullValueField("rowkey_separator");
		this.rowkey_separator = rowkey_separator;
	}

	public BigDecimal getSdm_des_id() { return sdm_des_id; }
	public void setSdm_des_id(BigDecimal sdm_des_id) {
		if(sdm_des_id==null) throw new BusinessException("Entity : SdmConHbase.sdm_des_id must not null!");
		this.sdm_des_id = sdm_des_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getPre_partition() { return pre_partition; }
	public void setPre_partition(String pre_partition) {
		if(pre_partition==null) addNullValueField("pre_partition");
		this.pre_partition = pre_partition;
	}

	public String getHbase_name() { return hbase_name; }
	public void setHbase_name(String hbase_name) {
		if(hbase_name==null) throw new BusinessException("Entity : SdmConHbase.hbase_name must not null!");
		this.hbase_name = hbase_name;
	}

}