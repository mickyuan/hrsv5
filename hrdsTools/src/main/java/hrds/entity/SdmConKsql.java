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
@Table(tableName = "sdm_con_ksql")
public class SdmConKsql extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_ksql";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sdm_col_ksql");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String is_timestamp;
	private String is_key;
	private BigDecimal sdm_ksql_id;
	private String sdm_remark;
	private String column_name;
	private String column_cname;
	private BigDecimal sdm_col_ksql;
	private String column_type;
	private String column_hy;

	public String getIs_timestamp() { return is_timestamp; }
	public void setIs_timestamp(String is_timestamp) {
		if(is_timestamp==null) throw new BusinessException("Entity : SdmConKsql.is_timestamp must not null!");
		this.is_timestamp = is_timestamp;
	}

	public String getIs_key() { return is_key; }
	public void setIs_key(String is_key) {
		if(is_key==null) throw new BusinessException("Entity : SdmConKsql.is_key must not null!");
		this.is_key = is_key;
	}

	public BigDecimal getSdm_ksql_id() { return sdm_ksql_id; }
	public void setSdm_ksql_id(BigDecimal sdm_ksql_id) {
		if(sdm_ksql_id==null) throw new BusinessException("Entity : SdmConKsql.sdm_ksql_id must not null!");
		this.sdm_ksql_id = sdm_ksql_id;
	}

	public String getSdm_remark() { return sdm_remark; }
	public void setSdm_remark(String sdm_remark) {
		if(sdm_remark==null) addNullValueField("sdm_remark");
		this.sdm_remark = sdm_remark;
	}

	public String getColumn_name() { return column_name; }
	public void setColumn_name(String column_name) {
		if(column_name==null) throw new BusinessException("Entity : SdmConKsql.column_name must not null!");
		this.column_name = column_name;
	}

	public String getColumn_cname() { return column_cname; }
	public void setColumn_cname(String column_cname) {
		if(column_cname==null) throw new BusinessException("Entity : SdmConKsql.column_cname must not null!");
		this.column_cname = column_cname;
	}

	public BigDecimal getSdm_col_ksql() { return sdm_col_ksql; }
	public void setSdm_col_ksql(BigDecimal sdm_col_ksql) {
		if(sdm_col_ksql==null) throw new BusinessException("Entity : SdmConKsql.sdm_col_ksql must not null!");
		this.sdm_col_ksql = sdm_col_ksql;
	}

	public String getColumn_type() { return column_type; }
	public void setColumn_type(String column_type) {
		if(column_type==null) throw new BusinessException("Entity : SdmConKsql.column_type must not null!");
		this.column_type = column_type;
	}

	public String getColumn_hy() { return column_hy; }
	public void setColumn_hy(String column_hy) {
		if(column_hy==null) addNullValueField("column_hy");
		this.column_hy = column_hy;
	}

}