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
@Table(tableName = "ml_feat_filt_column")
public class MlFeatFiltColumn extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_feat_filt_column";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("featfiltc_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String featf_column;
	private BigDecimal featfilt_id;
	private BigDecimal featfiltc_id;

	public String getFeatf_column() { return featf_column; }
	public void setFeatf_column(String featf_column) {
		if(featf_column==null) throw new BusinessException("Entity : MlFeatFiltColumn.featf_column must not null!");
		this.featf_column = featf_column;
	}

	public BigDecimal getFeatfilt_id() { return featfilt_id; }
	public void setFeatfilt_id(BigDecimal featfilt_id) {
		if(featfilt_id==null) throw new BusinessException("Entity : MlFeatFiltColumn.featfilt_id must not null!");
		this.featfilt_id = featfilt_id;
	}

	public BigDecimal getFeatfiltc_id() { return featfiltc_id; }
	public void setFeatfiltc_id(BigDecimal featfiltc_id) {
		if(featfiltc_id==null) throw new BusinessException("Entity : MlFeatFiltColumn.featfiltc_id must not null!");
		this.featfiltc_id = featfiltc_id;
	}

}