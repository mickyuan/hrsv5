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
@Table(tableName = "ml_efsearch_iv")
public class MlEfsearchIv extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_efsearch_iv";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("exhafs_iv_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal exhafeats_id;
	private BigDecimal exhafs_iv_id;
	private String iv_column;

	public BigDecimal getExhafeats_id() { return exhafeats_id; }
	public void setExhafeats_id(BigDecimal exhafeats_id) {
		if(exhafeats_id==null) throw new BusinessException("Entity : MlEfsearchIv.exhafeats_id must not null!");
		this.exhafeats_id = exhafeats_id;
	}

	public BigDecimal getExhafs_iv_id() { return exhafs_iv_id; }
	public void setExhafs_iv_id(BigDecimal exhafs_iv_id) {
		if(exhafs_iv_id==null) throw new BusinessException("Entity : MlEfsearchIv.exhafs_iv_id must not null!");
		this.exhafs_iv_id = exhafs_iv_id;
	}

	public String getIv_column() { return iv_column; }
	public void setIv_column(String iv_column) {
		if(iv_column==null) throw new BusinessException("Entity : MlEfsearchIv.iv_column must not null!");
		this.iv_column = iv_column;
	}

}