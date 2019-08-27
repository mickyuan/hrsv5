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
@Table(tableName = "edw_column")
public class EdwColumn extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_column";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("tabname");
		__tmpPKS.add("table_id");
		__tmpPKS.add("st_dt");
		__tmpPKS.add("st_time");
		__tmpPKS.add("colno");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String coltype;
	private String end_dt;
	private String st_dt;
	private String st_time;
	private String ccolname;
	private Integer colno;
	private String is_unique_index;
	private String is_partition;
	private String ifpk;
	private String remark;
	private BigDecimal table_id;
	private String colformat;
	private String is_normal_index;
	private String tabname;
	private String colname;
	private String is_diccol;
	private String tratype;
	private String ifnull;
	private BigDecimal column_sort;
	private String is_sortcolumns;
	private String coltra;

	public String getColtype() { return coltype; }
	public void setColtype(String coltype) {
		if(coltype==null) addNullValueField("coltype");
		this.coltype = coltype;
	}

	public String getEnd_dt() { return end_dt; }
	public void setEnd_dt(String end_dt) {
		if(end_dt==null) throw new BusinessException("Entity : EdwColumn.end_dt must not null!");
		this.end_dt = end_dt;
	}

	public String getSt_dt() { return st_dt; }
	public void setSt_dt(String st_dt) {
		if(st_dt==null) throw new BusinessException("Entity : EdwColumn.st_dt must not null!");
		this.st_dt = st_dt;
	}

	public String getSt_time() { return st_time; }
	public void setSt_time(String st_time) {
		if(st_time==null) throw new BusinessException("Entity : EdwColumn.st_time must not null!");
		this.st_time = st_time;
	}

	public String getCcolname() { return ccolname; }
	public void setCcolname(String ccolname) {
		if(ccolname==null) addNullValueField("ccolname");
		this.ccolname = ccolname;
	}

	public Integer getColno() { return colno; }
	public void setColno(Integer colno) {
		if(colno==null) throw new BusinessException("Entity : EdwColumn.colno must not null!");
		this.colno = colno;
	}

	public String getIs_unique_index() { return is_unique_index; }
	public void setIs_unique_index(String is_unique_index) {
		if(is_unique_index==null) throw new BusinessException("Entity : EdwColumn.is_unique_index must not null!");
		this.is_unique_index = is_unique_index;
	}

	public String getIs_partition() { return is_partition; }
	public void setIs_partition(String is_partition) {
		if(is_partition==null) throw new BusinessException("Entity : EdwColumn.is_partition must not null!");
		this.is_partition = is_partition;
	}

	public String getIfpk() { return ifpk; }
	public void setIfpk(String ifpk) {
		if(ifpk==null) throw new BusinessException("Entity : EdwColumn.ifpk must not null!");
		this.ifpk = ifpk;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getTable_id() { return table_id; }
	public void setTable_id(BigDecimal table_id) {
		if(table_id==null) throw new BusinessException("Entity : EdwColumn.table_id must not null!");
		this.table_id = table_id;
	}

	public String getColformat() { return colformat; }
	public void setColformat(String colformat) {
		if(colformat==null) addNullValueField("colformat");
		this.colformat = colformat;
	}

	public String getIs_normal_index() { return is_normal_index; }
	public void setIs_normal_index(String is_normal_index) {
		if(is_normal_index==null) throw new BusinessException("Entity : EdwColumn.is_normal_index must not null!");
		this.is_normal_index = is_normal_index;
	}

	public String getTabname() { return tabname; }
	public void setTabname(String tabname) {
		if(tabname==null) throw new BusinessException("Entity : EdwColumn.tabname must not null!");
		this.tabname = tabname;
	}

	public String getColname() { return colname; }
	public void setColname(String colname) {
		if(colname==null) throw new BusinessException("Entity : EdwColumn.colname must not null!");
		this.colname = colname;
	}

	public String getIs_diccol() { return is_diccol; }
	public void setIs_diccol(String is_diccol) {
		if(is_diccol==null) throw new BusinessException("Entity : EdwColumn.is_diccol must not null!");
		this.is_diccol = is_diccol;
	}

	public String getTratype() { return tratype; }
	public void setTratype(String tratype) {
		if(tratype==null) addNullValueField("tratype");
		this.tratype = tratype;
	}

	public String getIfnull() { return ifnull; }
	public void setIfnull(String ifnull) {
		if(ifnull==null) throw new BusinessException("Entity : EdwColumn.ifnull must not null!");
		this.ifnull = ifnull;
	}

	public BigDecimal getColumn_sort() { return column_sort; }
	public void setColumn_sort(BigDecimal column_sort) {
		if(column_sort==null) addNullValueField("column_sort");
		this.column_sort = column_sort;
	}

	public String getIs_sortcolumns() { return is_sortcolumns; }
	public void setIs_sortcolumns(String is_sortcolumns) {
		if(is_sortcolumns==null) throw new BusinessException("Entity : EdwColumn.is_sortcolumns must not null!");
		this.is_sortcolumns = is_sortcolumns;
	}

	public String getColtra() { return coltra; }
	public void setColtra(String coltra) {
		if(coltra==null) addNullValueField("coltra");
		this.coltra = coltra;
	}

}