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
@Table(tableName = "sys_recover")
public class SysRecover extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sys_recover";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("re_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String length;
	private String re_time;
	private BigDecimal re_id;
	private String remark;
	private String re_date;
	private BigDecimal dump_id;

	public String getLength() { return length; }
	public void setLength(String length) {
		if(length==null) throw new BusinessException("Entity : SysRecover.length must not null!");
		this.length = length;
	}

	public String getRe_time() { return re_time; }
	public void setRe_time(String re_time) {
		if(re_time==null) throw new BusinessException("Entity : SysRecover.re_time must not null!");
		this.re_time = re_time;
	}

	public BigDecimal getRe_id() { return re_id; }
	public void setRe_id(BigDecimal re_id) {
		if(re_id==null) throw new BusinessException("Entity : SysRecover.re_id must not null!");
		this.re_id = re_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getRe_date() { return re_date; }
	public void setRe_date(String re_date) {
		if(re_date==null) throw new BusinessException("Entity : SysRecover.re_date must not null!");
		this.re_date = re_date;
	}

	public BigDecimal getDump_id() { return dump_id; }
	public void setDump_id(BigDecimal dump_id) {
		if(dump_id==null) throw new BusinessException("Entity : SysRecover.dump_id must not null!");
		this.dump_id = dump_id;
	}

}