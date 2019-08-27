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
@Table(tableName = "r_cond_anal")
public class RCondAnal extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "r_cond_anal";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cond_anal_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal cond_anal_id;
	private BigDecimal sql_id;
	private String column_name;
	private String sql_cond_type;
	private String incidence_rela;
	private String column_cond;
	private String column_value;

	public BigDecimal getCond_anal_id() { return cond_anal_id; }
	public void setCond_anal_id(BigDecimal cond_anal_id) {
		if(cond_anal_id==null) throw new BusinessException("Entity : RCondAnal.cond_anal_id must not null!");
		this.cond_anal_id = cond_anal_id;
	}

	public BigDecimal getSql_id() { return sql_id; }
	public void setSql_id(BigDecimal sql_id) {
		if(sql_id==null) throw new BusinessException("Entity : RCondAnal.sql_id must not null!");
		this.sql_id = sql_id;
	}

	public String getColumn_name() { return column_name; }
	public void setColumn_name(String column_name) {
		if(column_name==null) throw new BusinessException("Entity : RCondAnal.column_name must not null!");
		this.column_name = column_name;
	}

	public String getSql_cond_type() { return sql_cond_type; }
	public void setSql_cond_type(String sql_cond_type) {
		if(sql_cond_type==null) throw new BusinessException("Entity : RCondAnal.sql_cond_type must not null!");
		this.sql_cond_type = sql_cond_type;
	}

	public String getIncidence_rela() { return incidence_rela; }
	public void setIncidence_rela(String incidence_rela) {
		if(incidence_rela==null) throw new BusinessException("Entity : RCondAnal.incidence_rela must not null!");
		this.incidence_rela = incidence_rela;
	}

	public String getColumn_cond() { return column_cond; }
	public void setColumn_cond(String column_cond) {
		if(column_cond==null) throw new BusinessException("Entity : RCondAnal.column_cond must not null!");
		this.column_cond = column_cond;
	}

	public String getColumn_value() { return column_value; }
	public void setColumn_value(String column_value) {
		if(column_value==null) throw new BusinessException("Entity : RCondAnal.column_value must not null!");
		this.column_value = column_value;
	}

}