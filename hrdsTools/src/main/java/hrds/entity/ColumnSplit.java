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
@Table(tableName = "column_split")
public class ColumnSplit extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "column_split";

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

	private String split_type;
	private BigDecimal col_id;
	private String col_zhname;
	private String split_sep;
	private String remark;
	private String col_name;
	private String col_offset;
	private BigDecimal column_id;
	private String valid_e_date;
	private BigDecimal c_id;
	private String col_type;
	private BigDecimal seq;
	private String valid_s_date;

	public String getSplit_type() { return split_type; }
	public void setSplit_type(String split_type) {
		if(split_type==null) throw new BusinessException("Entity : ColumnSplit.split_type must not null!");
		this.split_type = split_type;
	}

	public BigDecimal getCol_id() { return col_id; }
	public void setCol_id(BigDecimal col_id) {
		if(col_id==null) throw new BusinessException("Entity : ColumnSplit.col_id must not null!");
		this.col_id = col_id;
	}

	public String getCol_zhname() { return col_zhname; }
	public void setCol_zhname(String col_zhname) {
		if(col_zhname==null) addNullValueField("col_zhname");
		this.col_zhname = col_zhname;
	}

	public String getSplit_sep() { return split_sep; }
	public void setSplit_sep(String split_sep) {
		if(split_sep==null) addNullValueField("split_sep");
		this.split_sep = split_sep;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getCol_name() { return col_name; }
	public void setCol_name(String col_name) {
		if(col_name==null) throw new BusinessException("Entity : ColumnSplit.col_name must not null!");
		this.col_name = col_name;
	}

	public String getCol_offset() { return col_offset; }
	public void setCol_offset(String col_offset) {
		if(col_offset==null) addNullValueField("col_offset");
		this.col_offset = col_offset;
	}

	public BigDecimal getColumn_id() { return column_id; }
	public void setColumn_id(BigDecimal column_id) {
		if(column_id==null) throw new BusinessException("Entity : ColumnSplit.column_id must not null!");
		this.column_id = column_id;
	}

	public String getValid_e_date() { return valid_e_date; }
	public void setValid_e_date(String valid_e_date) {
		if(valid_e_date==null) throw new BusinessException("Entity : ColumnSplit.valid_e_date must not null!");
		this.valid_e_date = valid_e_date;
	}

	public BigDecimal getC_id() { return c_id; }
	public void setC_id(BigDecimal c_id) {
		if(c_id==null) throw new BusinessException("Entity : ColumnSplit.c_id must not null!");
		this.c_id = c_id;
	}

	public String getCol_type() { return col_type; }
	public void setCol_type(String col_type) {
		if(col_type==null) throw new BusinessException("Entity : ColumnSplit.col_type must not null!");
		this.col_type = col_type;
	}

	public BigDecimal getSeq() { return seq; }
	public void setSeq(BigDecimal seq) {
		if(seq==null) addNullValueField("seq");
		this.seq = seq;
	}

	public String getValid_s_date() { return valid_s_date; }
	public void setValid_s_date(String valid_s_date) {
		if(valid_s_date==null) throw new BusinessException("Entity : ColumnSplit.valid_s_date must not null!");
		this.valid_s_date = valid_s_date;
	}

}