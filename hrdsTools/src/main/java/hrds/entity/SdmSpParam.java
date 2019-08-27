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
@Table(tableName = "sdm_sp_param")
public class SdmSpParam extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_sp_param";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ssp_param_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String ssp_param_key;
	private BigDecimal ssp_param_id;
	private String is_customize;
	private BigDecimal ssj_job_id;
	private String ssp_param_value;

	public String getSsp_param_key() { return ssp_param_key; }
	public void setSsp_param_key(String ssp_param_key) {
		if(ssp_param_key==null) throw new BusinessException("Entity : SdmSpParam.ssp_param_key must not null!");
		this.ssp_param_key = ssp_param_key;
	}

	public BigDecimal getSsp_param_id() { return ssp_param_id; }
	public void setSsp_param_id(BigDecimal ssp_param_id) {
		if(ssp_param_id==null) throw new BusinessException("Entity : SdmSpParam.ssp_param_id must not null!");
		this.ssp_param_id = ssp_param_id;
	}

	public String getIs_customize() { return is_customize; }
	public void setIs_customize(String is_customize) {
		if(is_customize==null) throw new BusinessException("Entity : SdmSpParam.is_customize must not null!");
		this.is_customize = is_customize;
	}

	public BigDecimal getSsj_job_id() { return ssj_job_id; }
	public void setSsj_job_id(BigDecimal ssj_job_id) {
		if(ssj_job_id==null) throw new BusinessException("Entity : SdmSpParam.ssj_job_id must not null!");
		this.ssj_job_id = ssj_job_id;
	}

	public String getSsp_param_value() { return ssp_param_value; }
	public void setSsp_param_value(String ssp_param_value) {
		if(ssp_param_value==null) addNullValueField("ssp_param_value");
		this.ssp_param_value = ssp_param_value;
	}

}