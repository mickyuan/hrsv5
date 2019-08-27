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
@Table(tableName = "ml_gbrtfeat")
public class MlGbrtfeat extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_gbrtfeat";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("feature_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal feature_id;
	private String iv_column;
	private BigDecimal model_id;

	public BigDecimal getFeature_id() { return feature_id; }
	public void setFeature_id(BigDecimal feature_id) {
		if(feature_id==null) throw new BusinessException("Entity : MlGbrtfeat.feature_id must not null!");
		this.feature_id = feature_id;
	}

	public String getIv_column() { return iv_column; }
	public void setIv_column(String iv_column) {
		if(iv_column==null) throw new BusinessException("Entity : MlGbrtfeat.iv_column must not null!");
		this.iv_column = iv_column;
	}

	public BigDecimal getModel_id() { return model_id; }
	public void setModel_id(BigDecimal model_id) {
		if(model_id==null) throw new BusinessException("Entity : MlGbrtfeat.model_id must not null!");
		this.model_id = model_id;
	}

}