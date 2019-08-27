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
@Table(tableName = "cp_relation_cm")
public class CpRelationCm extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "cp_relation_cm";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cmsg_id");
		__tmpPKS.add("cp_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal cmsg_id;
	private BigDecimal cp_id;

	public BigDecimal getCmsg_id() { return cmsg_id; }
	public void setCmsg_id(BigDecimal cmsg_id) {
		if(cmsg_id==null) throw new BusinessException("Entity : CpRelationCm.cmsg_id must not null!");
		this.cmsg_id = cmsg_id;
	}

	public BigDecimal getCp_id() { return cp_id; }
	public void setCp_id(BigDecimal cp_id) {
		if(cp_id==null) throw new BusinessException("Entity : CpRelationCm.cp_id must not null!");
		this.cp_id = cp_id;
	}

}