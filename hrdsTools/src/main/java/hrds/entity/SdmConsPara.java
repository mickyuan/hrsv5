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
@Table(tableName = "sdm_cons_para")
public class SdmConsPara extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_cons_para";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sdm_conf_para_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String remark;
	private String sdm_cons_para_val;
	private BigDecimal sdm_conf_para_id;
	private String sdm_conf_para_na;
	private BigDecimal sdm_consum_id;

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getSdm_cons_para_val() { return sdm_cons_para_val; }
	public void setSdm_cons_para_val(String sdm_cons_para_val) {
		if(sdm_cons_para_val==null) throw new BusinessException("Entity : SdmConsPara.sdm_cons_para_val must not null!");
		this.sdm_cons_para_val = sdm_cons_para_val;
	}

	public BigDecimal getSdm_conf_para_id() { return sdm_conf_para_id; }
	public void setSdm_conf_para_id(BigDecimal sdm_conf_para_id) {
		if(sdm_conf_para_id==null) throw new BusinessException("Entity : SdmConsPara.sdm_conf_para_id must not null!");
		this.sdm_conf_para_id = sdm_conf_para_id;
	}

	public String getSdm_conf_para_na() { return sdm_conf_para_na; }
	public void setSdm_conf_para_na(String sdm_conf_para_na) {
		if(sdm_conf_para_na==null) throw new BusinessException("Entity : SdmConsPara.sdm_conf_para_na must not null!");
		this.sdm_conf_para_na = sdm_conf_para_na;
	}

	public BigDecimal getSdm_consum_id() { return sdm_consum_id; }
	public void setSdm_consum_id(BigDecimal sdm_consum_id) {
		if(sdm_consum_id==null) addNullValueField("sdm_consum_id");
		this.sdm_consum_id = sdm_consum_id;
	}

}