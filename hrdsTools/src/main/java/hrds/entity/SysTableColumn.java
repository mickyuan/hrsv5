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
@Table(tableName = "sys_table_column")
public class SysTableColumn extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sys_table_column";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("field_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal field_id;
	private String field_ch_name;
	private String is_dictionary;
	private String is_null;
	private String colsourcetab;
	private String column_name;
	private String is_invertedindex;
	private String remark;
	private String is_bucketcolumns;
	private String column_length;
	private BigDecimal info_id;
	private String colsourcecol;
	private String partition_column;
	private String column_type;
	private String is_primatykey;

	public BigDecimal getField_id() { return field_id; }
	public void setField_id(BigDecimal field_id) {
		if(field_id==null) throw new BusinessException("Entity : SysTableColumn.field_id must not null!");
		this.field_id = field_id;
	}

	public String getField_ch_name() { return field_ch_name; }
	public void setField_ch_name(String field_ch_name) {
		if(field_ch_name==null) addNullValueField("field_ch_name");
		this.field_ch_name = field_ch_name;
	}

	public String getIs_dictionary() { return is_dictionary; }
	public void setIs_dictionary(String is_dictionary) {
		if(is_dictionary==null) addNullValueField("is_dictionary");
		this.is_dictionary = is_dictionary;
	}

	public String getIs_null() { return is_null; }
	public void setIs_null(String is_null) {
		if(is_null==null) addNullValueField("is_null");
		this.is_null = is_null;
	}

	public String getColsourcetab() { return colsourcetab; }
	public void setColsourcetab(String colsourcetab) {
		if(colsourcetab==null) addNullValueField("colsourcetab");
		this.colsourcetab = colsourcetab;
	}

	public String getColumn_name() { return column_name; }
	public void setColumn_name(String column_name) {
		if(column_name==null) throw new BusinessException("Entity : SysTableColumn.column_name must not null!");
		this.column_name = column_name;
	}

	public String getIs_invertedindex() { return is_invertedindex; }
	public void setIs_invertedindex(String is_invertedindex) {
		if(is_invertedindex==null) addNullValueField("is_invertedindex");
		this.is_invertedindex = is_invertedindex;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getIs_bucketcolumns() { return is_bucketcolumns; }
	public void setIs_bucketcolumns(String is_bucketcolumns) {
		if(is_bucketcolumns==null) addNullValueField("is_bucketcolumns");
		this.is_bucketcolumns = is_bucketcolumns;
	}

	public String getColumn_length() { return column_length; }
	public void setColumn_length(String column_length) {
		if(column_length==null) addNullValueField("column_length");
		this.column_length = column_length;
	}

	public BigDecimal getInfo_id() { return info_id; }
	public void setInfo_id(BigDecimal info_id) {
		if(info_id==null) throw new BusinessException("Entity : SysTableColumn.info_id must not null!");
		this.info_id = info_id;
	}

	public String getColsourcecol() { return colsourcecol; }
	public void setColsourcecol(String colsourcecol) {
		if(colsourcecol==null) addNullValueField("colsourcecol");
		this.colsourcecol = colsourcecol;
	}

	public String getPartition_column() { return partition_column; }
	public void setPartition_column(String partition_column) {
		if(partition_column==null) throw new BusinessException("Entity : SysTableColumn.partition_column must not null!");
		this.partition_column = partition_column;
	}

	public String getColumn_type() { return column_type; }
	public void setColumn_type(String column_type) {
		if(column_type==null) throw new BusinessException("Entity : SysTableColumn.column_type must not null!");
		this.column_type = column_type;
	}

	public String getIs_primatykey() { return is_primatykey; }
	public void setIs_primatykey(String is_primatykey) {
		if(is_primatykey==null) addNullValueField("is_primatykey");
		this.is_primatykey = is_primatykey;
	}

}