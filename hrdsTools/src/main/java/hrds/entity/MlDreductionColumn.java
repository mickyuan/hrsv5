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
@Table(tableName = "ml_dreduction_column")
public class MlDreductionColumn extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_dreduction_column";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("drcolumn_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal drcolumn_id;
	private String dimer_column;
	private BigDecimal dimeredu_id;

	public BigDecimal getDrcolumn_id() { return drcolumn_id; }
	public void setDrcolumn_id(BigDecimal drcolumn_id) {
		if(drcolumn_id==null) throw new BusinessException("Entity : MlDreductionColumn.drcolumn_id must not null!");
		this.drcolumn_id = drcolumn_id;
	}

	public String getDimer_column() { return dimer_column; }
	public void setDimer_column(String dimer_column) {
		if(dimer_column==null) throw new BusinessException("Entity : MlDreductionColumn.dimer_column must not null!");
		this.dimer_column = dimer_column;
	}

	public BigDecimal getDimeredu_id() { return dimeredu_id; }
	public void setDimeredu_id(BigDecimal dimeredu_id) {
		if(dimeredu_id==null) throw new BusinessException("Entity : MlDreductionColumn.dimeredu_id must not null!");
		this.dimeredu_id = dimeredu_id;
	}

}