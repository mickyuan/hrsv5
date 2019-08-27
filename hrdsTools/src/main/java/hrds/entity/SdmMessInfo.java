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
@Table(tableName = "sdm_mess_info")
public class SdmMessInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_mess_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("mess_info_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String sdm_describe;
	private BigDecimal sdm_receive_id;
	private String sdm_var_name_en;
	private String sdm_is_send;
	private String sdm_var_name_cn;
	private String num;
	private BigDecimal mess_info_id;
	private String remark;
	private String sdm_var_type;

	public String getSdm_describe() { return sdm_describe; }
	public void setSdm_describe(String sdm_describe) {
		if(sdm_describe==null) addNullValueField("sdm_describe");
		this.sdm_describe = sdm_describe;
	}

	public BigDecimal getSdm_receive_id() { return sdm_receive_id; }
	public void setSdm_receive_id(BigDecimal sdm_receive_id) {
		if(sdm_receive_id==null) addNullValueField("sdm_receive_id");
		this.sdm_receive_id = sdm_receive_id;
	}

	public String getSdm_var_name_en() { return sdm_var_name_en; }
	public void setSdm_var_name_en(String sdm_var_name_en) {
		if(sdm_var_name_en==null) throw new BusinessException("Entity : SdmMessInfo.sdm_var_name_en must not null!");
		this.sdm_var_name_en = sdm_var_name_en;
	}

	public String getSdm_is_send() { return sdm_is_send; }
	public void setSdm_is_send(String sdm_is_send) {
		if(sdm_is_send==null) throw new BusinessException("Entity : SdmMessInfo.sdm_is_send must not null!");
		this.sdm_is_send = sdm_is_send;
	}

	public String getSdm_var_name_cn() { return sdm_var_name_cn; }
	public void setSdm_var_name_cn(String sdm_var_name_cn) {
		if(sdm_var_name_cn==null) throw new BusinessException("Entity : SdmMessInfo.sdm_var_name_cn must not null!");
		this.sdm_var_name_cn = sdm_var_name_cn;
	}

	public String getNum() { return num; }
	public void setNum(String num) {
		if(num==null) throw new BusinessException("Entity : SdmMessInfo.num must not null!");
		this.num = num;
	}

	public BigDecimal getMess_info_id() { return mess_info_id; }
	public void setMess_info_id(BigDecimal mess_info_id) {
		if(mess_info_id==null) throw new BusinessException("Entity : SdmMessInfo.mess_info_id must not null!");
		this.mess_info_id = mess_info_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getSdm_var_type() { return sdm_var_type; }
	public void setSdm_var_type(String sdm_var_type) {
		if(sdm_var_type==null) throw new BusinessException("Entity : SdmMessInfo.sdm_var_type must not null!");
		this.sdm_var_type = sdm_var_type;
	}

}