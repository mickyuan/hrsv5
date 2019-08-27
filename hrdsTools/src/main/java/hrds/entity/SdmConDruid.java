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
@Table(tableName = "sdm_con_druid")
public class SdmConDruid extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_druid";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("druid_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal druid_id;
	private String table_cname;
	private String data_columns;
	private String druid_servtype;
	private String is_topicasdruid;
	private String table_name;
	private String data_fun;
	private String timestamp_format;
	private String data_pattern;
	private String timestamp_pat;
	private String data_type;
	private BigDecimal sdm_des_id;
	private String timestamp_colum;

	public BigDecimal getDruid_id() { return druid_id; }
	public void setDruid_id(BigDecimal druid_id) {
		if(druid_id==null) throw new BusinessException("Entity : SdmConDruid.druid_id must not null!");
		this.druid_id = druid_id;
	}

	public String getTable_cname() { return table_cname; }
	public void setTable_cname(String table_cname) {
		if(table_cname==null) throw new BusinessException("Entity : SdmConDruid.table_cname must not null!");
		this.table_cname = table_cname;
	}

	public String getData_columns() { return data_columns; }
	public void setData_columns(String data_columns) {
		if(data_columns==null) addNullValueField("data_columns");
		this.data_columns = data_columns;
	}

	public String getDruid_servtype() { return druid_servtype; }
	public void setDruid_servtype(String druid_servtype) {
		if(druid_servtype==null) throw new BusinessException("Entity : SdmConDruid.druid_servtype must not null!");
		this.druid_servtype = druid_servtype;
	}

	public String getIs_topicasdruid() { return is_topicasdruid; }
	public void setIs_topicasdruid(String is_topicasdruid) {
		if(is_topicasdruid==null) throw new BusinessException("Entity : SdmConDruid.is_topicasdruid must not null!");
		this.is_topicasdruid = is_topicasdruid;
	}

	public String getTable_name() { return table_name; }
	public void setTable_name(String table_name) {
		if(table_name==null) throw new BusinessException("Entity : SdmConDruid.table_name must not null!");
		this.table_name = table_name;
	}

	public String getData_fun() { return data_fun; }
	public void setData_fun(String data_fun) {
		if(data_fun==null) addNullValueField("data_fun");
		this.data_fun = data_fun;
	}

	public String getTimestamp_format() { return timestamp_format; }
	public void setTimestamp_format(String timestamp_format) {
		if(timestamp_format==null) throw new BusinessException("Entity : SdmConDruid.timestamp_format must not null!");
		this.timestamp_format = timestamp_format;
	}

	public String getData_pattern() { return data_pattern; }
	public void setData_pattern(String data_pattern) {
		if(data_pattern==null) addNullValueField("data_pattern");
		this.data_pattern = data_pattern;
	}

	public String getTimestamp_pat() { return timestamp_pat; }
	public void setTimestamp_pat(String timestamp_pat) {
		if(timestamp_pat==null) addNullValueField("timestamp_pat");
		this.timestamp_pat = timestamp_pat;
	}

	public String getData_type() { return data_type; }
	public void setData_type(String data_type) {
		if(data_type==null) throw new BusinessException("Entity : SdmConDruid.data_type must not null!");
		this.data_type = data_type;
	}

	public BigDecimal getSdm_des_id() { return sdm_des_id; }
	public void setSdm_des_id(BigDecimal sdm_des_id) {
		if(sdm_des_id==null) throw new BusinessException("Entity : SdmConDruid.sdm_des_id must not null!");
		this.sdm_des_id = sdm_des_id;
	}

	public String getTimestamp_colum() { return timestamp_colum; }
	public void setTimestamp_colum(String timestamp_colum) {
		if(timestamp_colum==null) throw new BusinessException("Entity : SdmConDruid.timestamp_colum must not null!");
		this.timestamp_colum = timestamp_colum;
	}

}