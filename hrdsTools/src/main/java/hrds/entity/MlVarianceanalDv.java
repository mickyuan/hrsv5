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
@Table(tableName = "ml_varianceanal_dv")
public class MlVarianceanalDv extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_varianceanal_dv";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("varianaly_dv_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal varianaly_dv_id;
	private String dv_column;
	private BigDecimal varianal_iv_id;

	public BigDecimal getVarianaly_dv_id() { return varianaly_dv_id; }
	public void setVarianaly_dv_id(BigDecimal varianaly_dv_id) {
		if(varianaly_dv_id==null) throw new BusinessException("Entity : MlVarianceanalDv.varianaly_dv_id must not null!");
		this.varianaly_dv_id = varianaly_dv_id;
	}

	public String getDv_column() { return dv_column; }
	public void setDv_column(String dv_column) {
		if(dv_column==null) throw new BusinessException("Entity : MlVarianceanalDv.dv_column must not null!");
		this.dv_column = dv_column;
	}

	public BigDecimal getVarianal_iv_id() { return varianal_iv_id; }
	public void setVarianal_iv_id(BigDecimal varianal_iv_id) {
		if(varianal_iv_id==null) throw new BusinessException("Entity : MlVarianceanalDv.varianal_iv_id must not null!");
		this.varianal_iv_id = varianal_iv_id;
	}

}