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
@Table(tableName = "ml_chisquared_column")
public class MlChisquaredColumn extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_chisquared_column";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("chisquatc_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal chisquatc_id;
	private String chisquatestc;
	private BigDecimal chisquared_id;

	public BigDecimal getChisquatc_id() { return chisquatc_id; }
	public void setChisquatc_id(BigDecimal chisquatc_id) {
		if(chisquatc_id==null) throw new BusinessException("Entity : MlChisquaredColumn.chisquatc_id must not null!");
		this.chisquatc_id = chisquatc_id;
	}

	public String getChisquatestc() { return chisquatestc; }
	public void setChisquatestc(String chisquatestc) {
		if(chisquatestc==null) throw new BusinessException("Entity : MlChisquaredColumn.chisquatestc must not null!");
		this.chisquatestc = chisquatestc;
	}

	public BigDecimal getChisquared_id() { return chisquared_id; }
	public void setChisquared_id(BigDecimal chisquared_id) {
		if(chisquared_id==null) throw new BusinessException("Entity : MlChisquaredColumn.chisquared_id must not null!");
		this.chisquared_id = chisquared_id;
	}

}