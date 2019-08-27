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
@Table(tableName = "r_filter_result")
public class RFilterResult extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "r_filter_result";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("filterresu_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal filterresu_id;
	private String newcolumn_cond;
	private BigDecimal cond_anal_id;
	private BigDecimal filter_id;
	private String newcolumn_value;
	private String newinci_rela;

	public BigDecimal getFilterresu_id() { return filterresu_id; }
	public void setFilterresu_id(BigDecimal filterresu_id) {
		if(filterresu_id==null) throw new BusinessException("Entity : RFilterResult.filterresu_id must not null!");
		this.filterresu_id = filterresu_id;
	}

	public String getNewcolumn_cond() { return newcolumn_cond; }
	public void setNewcolumn_cond(String newcolumn_cond) {
		if(newcolumn_cond==null) throw new BusinessException("Entity : RFilterResult.newcolumn_cond must not null!");
		this.newcolumn_cond = newcolumn_cond;
	}

	public BigDecimal getCond_anal_id() { return cond_anal_id; }
	public void setCond_anal_id(BigDecimal cond_anal_id) {
		if(cond_anal_id==null) throw new BusinessException("Entity : RFilterResult.cond_anal_id must not null!");
		this.cond_anal_id = cond_anal_id;
	}

	public BigDecimal getFilter_id() { return filter_id; }
	public void setFilter_id(BigDecimal filter_id) {
		if(filter_id==null) throw new BusinessException("Entity : RFilterResult.filter_id must not null!");
		this.filter_id = filter_id;
	}

	public String getNewcolumn_value() { return newcolumn_value; }
	public void setNewcolumn_value(String newcolumn_value) {
		if(newcolumn_value==null) throw new BusinessException("Entity : RFilterResult.newcolumn_value must not null!");
		this.newcolumn_value = newcolumn_value;
	}

	public String getNewinci_rela() { return newinci_rela; }
	public void setNewinci_rela(String newinci_rela) {
		if(newinci_rela==null) throw new BusinessException("Entity : RFilterResult.newinci_rela must not null!");
		this.newinci_rela = newinci_rela;
	}

}