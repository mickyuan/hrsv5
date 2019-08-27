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
@Table(tableName = "edw_table_join")
public class EdwTableJoin extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_table_join";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("jobcode");
		__tmpPKS.add("st_dt");
		__tmpPKS.add("st_time");
		__tmpPKS.add("serial_num");
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
	private String tabalias;
	private String end_dt;
	private String st_dt;
	private String st_time;
	private String jointype;
	private String join_condition;
	private String jobcode;
	private String usercode;
	private Integer serial_num;

	public String getTabname() { return tabname; }
	public void setTabname(String tabname) {
		if(tabname==null) throw new BusinessException("Entity : EdwTableJoin.tabname must not null!");
		this.tabname = tabname;
	}

	public String getTabalias() { return tabalias; }
	public void setTabalias(String tabalias) {
		if(tabalias==null) throw new BusinessException("Entity : EdwTableJoin.tabalias must not null!");
		this.tabalias = tabalias;
	}

	public String getEnd_dt() { return end_dt; }
	public void setEnd_dt(String end_dt) {
		if(end_dt==null) addNullValueField("end_dt");
		this.end_dt = end_dt;
	}

	public String getSt_dt() { return st_dt; }
	public void setSt_dt(String st_dt) {
		if(st_dt==null) throw new BusinessException("Entity : EdwTableJoin.st_dt must not null!");
		this.st_dt = st_dt;
	}

	public String getSt_time() { return st_time; }
	public void setSt_time(String st_time) {
		if(st_time==null) throw new BusinessException("Entity : EdwTableJoin.st_time must not null!");
		this.st_time = st_time;
	}

	public String getJointype() { return jointype; }
	public void setJointype(String jointype) {
		if(jointype==null) addNullValueField("jointype");
		this.jointype = jointype;
	}

	public String getJoin_condition() { return join_condition; }
	public void setJoin_condition(String join_condition) {
		if(join_condition==null) addNullValueField("join_condition");
		this.join_condition = join_condition;
	}

	public String getJobcode() { return jobcode; }
	public void setJobcode(String jobcode) {
		if(jobcode==null) throw new BusinessException("Entity : EdwTableJoin.jobcode must not null!");
		this.jobcode = jobcode;
	}

	public String getUsercode() { return usercode; }
	public void setUsercode(String usercode) {
		if(usercode==null) addNullValueField("usercode");
		this.usercode = usercode;
	}

	public Integer getSerial_num() { return serial_num; }
	public void setSerial_num(Integer serial_num) {
		if(serial_num==null) throw new BusinessException("Entity : EdwTableJoin.serial_num must not null!");
		this.serial_num = serial_num;
	}

}