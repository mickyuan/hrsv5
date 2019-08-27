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
@Table(tableName = "department_info")
public class DepartmentInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "department_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dep_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal dep_id;
	private String dep_name;
	private String create_date;
	private String create_time;
	private String dep_remark;

	public BigDecimal getDep_id() { return dep_id; }
	public void setDep_id(BigDecimal dep_id) {
		if(dep_id==null) throw new BusinessException("Entity : DepartmentInfo.dep_id must not null!");
		this.dep_id = dep_id;
	}

	public String getDep_name() { return dep_name; }
	public void setDep_name(String dep_name) {
		if(dep_name==null) throw new BusinessException("Entity : DepartmentInfo.dep_name must not null!");
		this.dep_name = dep_name;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : DepartmentInfo.create_date must not null!");
		this.create_date = create_date;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : DepartmentInfo.create_time must not null!");
		this.create_time = create_time;
	}

	public String getDep_remark() { return dep_remark; }
	public void setDep_remark(String dep_remark) {
		if(dep_remark==null) addNullValueField("dep_remark");
		this.dep_remark = dep_remark;
	}

}