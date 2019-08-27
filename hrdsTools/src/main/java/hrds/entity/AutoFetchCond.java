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
@Table(tableName = "auto_fetch_cond")
public class AutoFetchCond extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_fetch_cond";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("fetch_cond_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String cond_value;
	private BigDecimal template_cond_id;
	private BigDecimal fetch_cond_id;
	private BigDecimal fetch_sum_id;

	public String getCond_value() { return cond_value; }
	public void setCond_value(String cond_value) {
		if(cond_value==null) addNullValueField("cond_value");
		this.cond_value = cond_value;
	}

	public BigDecimal getTemplate_cond_id() { return template_cond_id; }
	public void setTemplate_cond_id(BigDecimal template_cond_id) {
		if(template_cond_id==null) throw new BusinessException("Entity : AutoFetchCond.template_cond_id must not null!");
		this.template_cond_id = template_cond_id;
	}

	public BigDecimal getFetch_cond_id() { return fetch_cond_id; }
	public void setFetch_cond_id(BigDecimal fetch_cond_id) {
		if(fetch_cond_id==null) throw new BusinessException("Entity : AutoFetchCond.fetch_cond_id must not null!");
		this.fetch_cond_id = fetch_cond_id;
	}

	public BigDecimal getFetch_sum_id() { return fetch_sum_id; }
	public void setFetch_sum_id(BigDecimal fetch_sum_id) {
		if(fetch_sum_id==null) throw new BusinessException("Entity : AutoFetchCond.fetch_sum_id must not null!");
		this.fetch_sum_id = fetch_sum_id;
	}

}