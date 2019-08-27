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
@Table(tableName = "sdm_con_sparkd")
public class SdmConSparkd extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_sparkd";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sparkd_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String sparkd_bus_class;
	private String sparkd_bus_type;
	private BigDecimal sdm_des_id;
	private BigDecimal sparkd_id;
	private String table_space;
	private String table_en_name;

	public String getSparkd_bus_class() { return sparkd_bus_class; }
	public void setSparkd_bus_class(String sparkd_bus_class) {
		if(sparkd_bus_class==null) addNullValueField("sparkd_bus_class");
		this.sparkd_bus_class = sparkd_bus_class;
	}

	public String getSparkd_bus_type() { return sparkd_bus_type; }
	public void setSparkd_bus_type(String sparkd_bus_type) {
		if(sparkd_bus_type==null) throw new BusinessException("Entity : SdmConSparkd.sparkd_bus_type must not null!");
		this.sparkd_bus_type = sparkd_bus_type;
	}

	public BigDecimal getSdm_des_id() { return sdm_des_id; }
	public void setSdm_des_id(BigDecimal sdm_des_id) {
		if(sdm_des_id==null) throw new BusinessException("Entity : SdmConSparkd.sdm_des_id must not null!");
		this.sdm_des_id = sdm_des_id;
	}

	public BigDecimal getSparkd_id() { return sparkd_id; }
	public void setSparkd_id(BigDecimal sparkd_id) {
		if(sparkd_id==null) throw new BusinessException("Entity : SdmConSparkd.sparkd_id must not null!");
		this.sparkd_id = sparkd_id;
	}

	public String getTable_space() { return table_space; }
	public void setTable_space(String table_space) {
		if(table_space==null) addNullValueField("table_space");
		this.table_space = table_space;
	}

	public String getTable_en_name() { return table_en_name; }
	public void setTable_en_name(String table_en_name) {
		if(table_en_name==null) throw new BusinessException("Entity : SdmConSparkd.table_en_name must not null!");
		this.table_en_name = table_en_name;
	}

}