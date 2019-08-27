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
@Table(tableName = "edw_hzzy_tableinfo")
public class EdwHzzyTableinfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_hzzy_tableinfo";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("hzzy_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String tabname;
	private String create_time;
	private String st_dt;
	private String st_time;
	private String hzzy_tbname;
	private String remark;
	private String create_date;
	private BigDecimal table_id;
	private BigDecimal hzzy_id;

	public String getTabname() { return tabname; }
	public void setTabname(String tabname) {
		if(tabname==null) throw new BusinessException("Entity : EdwHzzyTableinfo.tabname must not null!");
		this.tabname = tabname;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : EdwHzzyTableinfo.create_time must not null!");
		this.create_time = create_time;
	}

	public String getSt_dt() { return st_dt; }
	public void setSt_dt(String st_dt) {
		if(st_dt==null) throw new BusinessException("Entity : EdwHzzyTableinfo.st_dt must not null!");
		this.st_dt = st_dt;
	}

	public String getSt_time() { return st_time; }
	public void setSt_time(String st_time) {
		if(st_time==null) throw new BusinessException("Entity : EdwHzzyTableinfo.st_time must not null!");
		this.st_time = st_time;
	}

	public String getHzzy_tbname() { return hzzy_tbname; }
	public void setHzzy_tbname(String hzzy_tbname) {
		if(hzzy_tbname==null) throw new BusinessException("Entity : EdwHzzyTableinfo.hzzy_tbname must not null!");
		this.hzzy_tbname = hzzy_tbname;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : EdwHzzyTableinfo.create_date must not null!");
		this.create_date = create_date;
	}

	public BigDecimal getTable_id() { return table_id; }
	public void setTable_id(BigDecimal table_id) {
		if(table_id==null) throw new BusinessException("Entity : EdwHzzyTableinfo.table_id must not null!");
		this.table_id = table_id;
	}

	public BigDecimal getHzzy_id() { return hzzy_id; }
	public void setHzzy_id(BigDecimal hzzy_id) {
		if(hzzy_id==null) throw new BusinessException("Entity : EdwHzzyTableinfo.hzzy_id must not null!");
		this.hzzy_id = hzzy_id;
	}

}