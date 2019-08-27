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
@Table(tableName = "table_info")
public class TableInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "table_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("table_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String storage_type;
	private String table_ch_name;
	private String table_count;
	private String source_tableid;
	private String is_md5;
	private String remark;
	private BigDecimal table_id;
	private String is_user_defined;
	private String table_name;
	private String sql;
	private String valid_e_date;
	private BigDecimal database_id;
	private String valid_s_date;
	private String ti_or;

	public String getStorage_type() { return storage_type; }
	public void setStorage_type(String storage_type) {
		if(storage_type==null) throw new BusinessException("Entity : TableInfo.storage_type must not null!");
		this.storage_type = storage_type;
	}

	public String getTable_ch_name() { return table_ch_name; }
	public void setTable_ch_name(String table_ch_name) {
		if(table_ch_name==null) throw new BusinessException("Entity : TableInfo.table_ch_name must not null!");
		this.table_ch_name = table_ch_name;
	}

	public String getTable_count() { return table_count; }
	public void setTable_count(String table_count) {
		if(table_count==null) addNullValueField("table_count");
		this.table_count = table_count;
	}

	public String getSource_tableid() { return source_tableid; }
	public void setSource_tableid(String source_tableid) {
		if(source_tableid==null) addNullValueField("source_tableid");
		this.source_tableid = source_tableid;
	}

	public String getIs_md5() { return is_md5; }
	public void setIs_md5(String is_md5) {
		if(is_md5==null) throw new BusinessException("Entity : TableInfo.is_md5 must not null!");
		this.is_md5 = is_md5;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getTable_id() { return table_id; }
	public void setTable_id(BigDecimal table_id) {
		if(table_id==null) throw new BusinessException("Entity : TableInfo.table_id must not null!");
		this.table_id = table_id;
	}

	public String getIs_user_defined() { return is_user_defined; }
	public void setIs_user_defined(String is_user_defined) {
		if(is_user_defined==null) throw new BusinessException("Entity : TableInfo.is_user_defined must not null!");
		this.is_user_defined = is_user_defined;
	}

	public String getTable_name() { return table_name; }
	public void setTable_name(String table_name) {
		if(table_name==null) throw new BusinessException("Entity : TableInfo.table_name must not null!");
		this.table_name = table_name;
	}

	public String getSql() { return sql; }
	public void setSql(String sql) {
		if(sql==null) addNullValueField("sql");
		this.sql = sql;
	}

	public String getValid_e_date() { return valid_e_date; }
	public void setValid_e_date(String valid_e_date) {
		if(valid_e_date==null) throw new BusinessException("Entity : TableInfo.valid_e_date must not null!");
		this.valid_e_date = valid_e_date;
	}

	public BigDecimal getDatabase_id() { return database_id; }
	public void setDatabase_id(BigDecimal database_id) {
		if(database_id==null) throw new BusinessException("Entity : TableInfo.database_id must not null!");
		this.database_id = database_id;
	}

	public String getValid_s_date() { return valid_s_date; }
	public void setValid_s_date(String valid_s_date) {
		if(valid_s_date==null) throw new BusinessException("Entity : TableInfo.valid_s_date must not null!");
		this.valid_s_date = valid_s_date;
	}

	public String getTi_or() { return ti_or; }
	public void setTi_or(String ti_or) {
		if(ti_or==null) addNullValueField("ti_or");
		this.ti_or = ti_or;
	}

}