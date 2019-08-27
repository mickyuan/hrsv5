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
@Table(tableName = "tellers")
public class Tellers extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "tellers";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("te_operid");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String te_operid;
	private String te_name;
	private String te_dept_type;
	private String te_role_id;

	public String getTe_operid() { return te_operid; }
	public void setTe_operid(String te_operid) {
		if(te_operid==null) throw new BusinessException("Entity : Tellers.te_operid must not null!");
		this.te_operid = te_operid;
	}

	public String getTe_name() { return te_name; }
	public void setTe_name(String te_name) {
		if(te_name==null) throw new BusinessException("Entity : Tellers.te_name must not null!");
		this.te_name = te_name;
	}

	public String getTe_dept_type() { return te_dept_type; }
	public void setTe_dept_type(String te_dept_type) {
		if(te_dept_type==null) throw new BusinessException("Entity : Tellers.te_dept_type must not null!");
		this.te_dept_type = te_dept_type;
	}

	public String getTe_role_id() { return te_role_id; }
	public void setTe_role_id(String te_role_id) {
		if(te_role_id==null) throw new BusinessException("Entity : Tellers.te_role_id must not null!");
		this.te_role_id = te_role_id;
	}

}