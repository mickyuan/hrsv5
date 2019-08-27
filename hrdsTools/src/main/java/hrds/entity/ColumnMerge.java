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
@Table(tableName = "column_merge")
public class ColumnMerge extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "column_merge";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("col_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal col_id;
	private String valid_e_date;
	private String old_name;
	private String col_zhname;
	private String remark;
	private String col_name;
	private BigDecimal table_id;
	private String col_type;
	private String valid_s_date;

	public BigDecimal getCol_id() { return col_id; }
	public void setCol_id(BigDecimal col_id) {
		if(col_id==null) throw new BusinessException("Entity : ColumnMerge.col_id must not null!");
		this.col_id = col_id;
	}

	public String getValid_e_date() { return valid_e_date; }
	public void setValid_e_date(String valid_e_date) {
		if(valid_e_date==null) throw new BusinessException("Entity : ColumnMerge.valid_e_date must not null!");
		this.valid_e_date = valid_e_date;
	}

	public String getOld_name() { return old_name; }
	public void setOld_name(String old_name) {
		if(old_name==null) throw new BusinessException("Entity : ColumnMerge.old_name must not null!");
		this.old_name = old_name;
	}

	public String getCol_zhname() { return col_zhname; }
	public void setCol_zhname(String col_zhname) {
		if(col_zhname==null) addNullValueField("col_zhname");
		this.col_zhname = col_zhname;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getCol_name() { return col_name; }
	public void setCol_name(String col_name) {
		if(col_name==null) throw new BusinessException("Entity : ColumnMerge.col_name must not null!");
		this.col_name = col_name;
	}

	public BigDecimal getTable_id() { return table_id; }
	public void setTable_id(BigDecimal table_id) {
		if(table_id==null) throw new BusinessException("Entity : ColumnMerge.table_id must not null!");
		this.table_id = table_id;
	}

	public String getCol_type() { return col_type; }
	public void setCol_type(String col_type) {
		if(col_type==null) throw new BusinessException("Entity : ColumnMerge.col_type must not null!");
		this.col_type = col_type;
	}

	public String getValid_s_date() { return valid_s_date; }
	public void setValid_s_date(String valid_s_date) {
		if(valid_s_date==null) throw new BusinessException("Entity : ColumnMerge.valid_s_date must not null!");
		this.valid_s_date = valid_s_date;
	}

}