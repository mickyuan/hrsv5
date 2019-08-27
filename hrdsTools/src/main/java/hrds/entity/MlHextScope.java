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
@Table(tableName = "ml_hext_scope")
public class MlHextScope extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_hext_scope";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("hextscope_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String extr_value;
	private BigDecimal hextscope_id;
	private BigDecimal hierextr_id;

	public String getExtr_value() { return extr_value; }
	public void setExtr_value(String extr_value) {
		if(extr_value==null) throw new BusinessException("Entity : MlHextScope.extr_value must not null!");
		this.extr_value = extr_value;
	}

	public BigDecimal getHextscope_id() { return hextscope_id; }
	public void setHextscope_id(BigDecimal hextscope_id) {
		if(hextscope_id==null) throw new BusinessException("Entity : MlHextScope.hextscope_id must not null!");
		this.hextscope_id = hextscope_id;
	}

	public BigDecimal getHierextr_id() { return hierextr_id; }
	public void setHierextr_id(BigDecimal hierextr_id) {
		if(hierextr_id==null) throw new BusinessException("Entity : MlHextScope.hierextr_id must not null!");
		this.hierextr_id = hierextr_id;
	}

}