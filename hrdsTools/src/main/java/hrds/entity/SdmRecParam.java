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
@Table(tableName = "sdm_rec_param")
public class SdmRecParam extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_rec_param";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("rec_param_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal rec_param_id;
	private BigDecimal sdm_receive_id;
	private String sdm_param_value;
	private String sdm_param_key;

	public BigDecimal getRec_param_id() { return rec_param_id; }
	public void setRec_param_id(BigDecimal rec_param_id) {
		if(rec_param_id==null) throw new BusinessException("Entity : SdmRecParam.rec_param_id must not null!");
		this.rec_param_id = rec_param_id;
	}

	public BigDecimal getSdm_receive_id() { return sdm_receive_id; }
	public void setSdm_receive_id(BigDecimal sdm_receive_id) {
		if(sdm_receive_id==null) addNullValueField("sdm_receive_id");
		this.sdm_receive_id = sdm_receive_id;
	}

	public String getSdm_param_value() { return sdm_param_value; }
	public void setSdm_param_value(String sdm_param_value) {
		if(sdm_param_value==null) throw new BusinessException("Entity : SdmRecParam.sdm_param_value must not null!");
		this.sdm_param_value = sdm_param_value;
	}

	public String getSdm_param_key() { return sdm_param_key; }
	public void setSdm_param_key(String sdm_param_key) {
		if(sdm_param_key==null) throw new BusinessException("Entity : SdmRecParam.sdm_param_key must not null!");
		this.sdm_param_key = sdm_param_key;
	}

}