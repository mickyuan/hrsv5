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
@Table(tableName = "table_column")
public class TableColumn extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "table_column";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("column_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String is_pre;
	private String is_primary_key;
	private String tc_or;
	private String is_new;
	private String is_unique_index;
	private String is_partition;
	private String remark;
	private String is_solr;
	private BigDecimal table_id;
	private String is_get;
	private String is_normal_index;
	private BigDecimal column_id;
	private String valid_e_date;
	private String is_alive;
	private String is_sortcolumns;
	private String column_type;
	private String colume_ch_name;
	private String colume_name;
	private String valid_s_date;

	public String getIs_pre() { return is_pre; }
	public void setIs_pre(String is_pre) {
		if(is_pre==null) throw new BusinessException("Entity : TableColumn.is_pre must not null!");
		this.is_pre = is_pre;
	}

	public String getIs_primary_key() { return is_primary_key; }
	public void setIs_primary_key(String is_primary_key) {
		if(is_primary_key==null) throw new BusinessException("Entity : TableColumn.is_primary_key must not null!");
		this.is_primary_key = is_primary_key;
	}

	public String getTc_or() { return tc_or; }
	public void setTc_or(String tc_or) {
		if(tc_or==null) addNullValueField("tc_or");
		this.tc_or = tc_or;
	}

	public String getIs_new() { return is_new; }
	public void setIs_new(String is_new) {
		if(is_new==null) throw new BusinessException("Entity : TableColumn.is_new must not null!");
		this.is_new = is_new;
	}

	public String getIs_unique_index() { return is_unique_index; }
	public void setIs_unique_index(String is_unique_index) {
		if(is_unique_index==null) throw new BusinessException("Entity : TableColumn.is_unique_index must not null!");
		this.is_unique_index = is_unique_index;
	}

	public String getIs_partition() { return is_partition; }
	public void setIs_partition(String is_partition) {
		if(is_partition==null) throw new BusinessException("Entity : TableColumn.is_partition must not null!");
		this.is_partition = is_partition;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getIs_solr() { return is_solr; }
	public void setIs_solr(String is_solr) {
		if(is_solr==null) throw new BusinessException("Entity : TableColumn.is_solr must not null!");
		this.is_solr = is_solr;
	}

	public BigDecimal getTable_id() { return table_id; }
	public void setTable_id(BigDecimal table_id) {
		if(table_id==null) throw new BusinessException("Entity : TableColumn.table_id must not null!");
		this.table_id = table_id;
	}

	public String getIs_get() { return is_get; }
	public void setIs_get(String is_get) {
		if(is_get==null) addNullValueField("is_get");
		this.is_get = is_get;
	}

	public String getIs_normal_index() { return is_normal_index; }
	public void setIs_normal_index(String is_normal_index) {
		if(is_normal_index==null) throw new BusinessException("Entity : TableColumn.is_normal_index must not null!");
		this.is_normal_index = is_normal_index;
	}

	public BigDecimal getColumn_id() { return column_id; }
	public void setColumn_id(BigDecimal column_id) {
		if(column_id==null) throw new BusinessException("Entity : TableColumn.column_id must not null!");
		this.column_id = column_id;
	}

	public String getValid_e_date() { return valid_e_date; }
	public void setValid_e_date(String valid_e_date) {
		if(valid_e_date==null) throw new BusinessException("Entity : TableColumn.valid_e_date must not null!");
		this.valid_e_date = valid_e_date;
	}

	public String getIs_alive() { return is_alive; }
	public void setIs_alive(String is_alive) {
		if(is_alive==null) throw new BusinessException("Entity : TableColumn.is_alive must not null!");
		this.is_alive = is_alive;
	}

	public String getIs_sortcolumns() { return is_sortcolumns; }
	public void setIs_sortcolumns(String is_sortcolumns) {
		if(is_sortcolumns==null) throw new BusinessException("Entity : TableColumn.is_sortcolumns must not null!");
		this.is_sortcolumns = is_sortcolumns;
	}

	public String getColumn_type() { return column_type; }
	public void setColumn_type(String column_type) {
		if(column_type==null) addNullValueField("column_type");
		this.column_type = column_type;
	}

	public String getColume_ch_name() { return colume_ch_name; }
	public void setColume_ch_name(String colume_ch_name) {
		if(colume_ch_name==null) addNullValueField("colume_ch_name");
		this.colume_ch_name = colume_ch_name;
	}

	public String getColume_name() { return colume_name; }
	public void setColume_name(String colume_name) {
		if(colume_name==null) throw new BusinessException("Entity : TableColumn.colume_name must not null!");
		this.colume_name = colume_name;
	}

	public String getValid_s_date() { return valid_s_date; }
	public void setValid_s_date(String valid_s_date) {
		if(valid_s_date==null) throw new BusinessException("Entity : TableColumn.valid_s_date must not null!");
		this.valid_s_date = valid_s_date;
	}

}