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
@Table(tableName = "edw_table")
public class EdwTable extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_table";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("tabname");
		__tmpPKS.add("table_id");
		__tmpPKS.add("st_dt");
		__tmpPKS.add("st_time");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String fromsys;
	private String tabname;
	private String end_dt;
	private String st_dt;
	private String st_time;
	private BigDecimal category_id;
	private String ctname;
	private String remark;
	private BigDecimal table_id;
	private String data_in;

	public String getFromsys() { return fromsys; }
	public void setFromsys(String fromsys) {
		if(fromsys==null) addNullValueField("fromsys");
		this.fromsys = fromsys;
	}

	public String getTabname() { return tabname; }
	public void setTabname(String tabname) {
		if(tabname==null) throw new BusinessException("Entity : EdwTable.tabname must not null!");
		this.tabname = tabname;
	}

	public String getEnd_dt() { return end_dt; }
	public void setEnd_dt(String end_dt) {
		if(end_dt==null) addNullValueField("end_dt");
		this.end_dt = end_dt;
	}

	public String getSt_dt() { return st_dt; }
	public void setSt_dt(String st_dt) {
		if(st_dt==null) throw new BusinessException("Entity : EdwTable.st_dt must not null!");
		this.st_dt = st_dt;
	}

	public String getSt_time() { return st_time; }
	public void setSt_time(String st_time) {
		if(st_time==null) throw new BusinessException("Entity : EdwTable.st_time must not null!");
		this.st_time = st_time;
	}

	public BigDecimal getCategory_id() { return category_id; }
	public void setCategory_id(BigDecimal category_id) {
		if(category_id==null) throw new BusinessException("Entity : EdwTable.category_id must not null!");
		this.category_id = category_id;
	}

	public String getCtname() { return ctname; }
	public void setCtname(String ctname) {
		if(ctname==null) throw new BusinessException("Entity : EdwTable.ctname must not null!");
		this.ctname = ctname;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getTable_id() { return table_id; }
	public void setTable_id(BigDecimal table_id) {
		if(table_id==null) throw new BusinessException("Entity : EdwTable.table_id must not null!");
		this.table_id = table_id;
	}

	public String getData_in() { return data_in; }
	public void setData_in(String data_in) {
		if(data_in==null) throw new BusinessException("Entity : EdwTable.data_in must not null!");
		this.data_in = data_in;
	}

}