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
@Table(tableName = "num_info")
public class NumInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "num_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("icm_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal icm_id;
	private String maintype;
	private BigDecimal decrease_num;
	private String hbase_name;
	private String exec_date;
	private BigDecimal increase_num;

	public BigDecimal getIcm_id() { return icm_id; }
	public void setIcm_id(BigDecimal icm_id) {
		if(icm_id==null) throw new BusinessException("Entity : NumInfo.icm_id must not null!");
		this.icm_id = icm_id;
	}

	public String getMaintype() { return maintype; }
	public void setMaintype(String maintype) {
		if(maintype==null) throw new BusinessException("Entity : NumInfo.maintype must not null!");
		this.maintype = maintype;
	}

	public BigDecimal getDecrease_num() { return decrease_num; }
	public void setDecrease_num(BigDecimal decrease_num) {
		if(decrease_num==null) throw new BusinessException("Entity : NumInfo.decrease_num must not null!");
		this.decrease_num = decrease_num;
	}

	public String getHbase_name() { return hbase_name; }
	public void setHbase_name(String hbase_name) {
		if(hbase_name==null) throw new BusinessException("Entity : NumInfo.hbase_name must not null!");
		this.hbase_name = hbase_name;
	}

	public String getExec_date() { return exec_date; }
	public void setExec_date(String exec_date) {
		if(exec_date==null) throw new BusinessException("Entity : NumInfo.exec_date must not null!");
		this.exec_date = exec_date;
	}

	public BigDecimal getIncrease_num() { return increase_num; }
	public void setIncrease_num(BigDecimal increase_num) {
		if(increase_num==null) throw new BusinessException("Entity : NumInfo.increase_num must not null!");
		this.increase_num = increase_num;
	}

}