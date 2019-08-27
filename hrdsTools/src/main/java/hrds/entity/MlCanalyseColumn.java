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
@Table(tableName = "ml_canalyse_column")
public class MlCanalyseColumn extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_canalyse_column";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("corranalc_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal correlation_id;
	private String corranal_colu;
	private BigDecimal corranalc_id;

	public BigDecimal getCorrelation_id() { return correlation_id; }
	public void setCorrelation_id(BigDecimal correlation_id) {
		if(correlation_id==null) throw new BusinessException("Entity : MlCanalyseColumn.correlation_id must not null!");
		this.correlation_id = correlation_id;
	}

	public String getCorranal_colu() { return corranal_colu; }
	public void setCorranal_colu(String corranal_colu) {
		if(corranal_colu==null) throw new BusinessException("Entity : MlCanalyseColumn.corranal_colu must not null!");
		this.corranal_colu = corranal_colu;
	}

	public BigDecimal getCorranalc_id() { return corranalc_id; }
	public void setCorranalc_id(BigDecimal corranalc_id) {
		if(corranalc_id==null) throw new BusinessException("Entity : MlCanalyseColumn.corranalc_id must not null!");
		this.corranalc_id = corranalc_id;
	}

}