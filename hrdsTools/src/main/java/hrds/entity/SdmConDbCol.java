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
@Table(tableName = "sdm_con_db_col")
public class SdmConDbCol extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_db_col";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sdm_col_id");
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
	private BigDecimal consumer_id;
	private String is_send;
	private BigDecimal rowkey_seq;
	private BigDecimal num;
	private String sdm_col_name_cn;
	private BigDecimal sdm_col_id;
	private String remark;
	private String sdm_var_type;
	private String sdm_col_name_en;
	private String is_rowkey;

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

	public BigDecimal getConsumer_id() { return consumer_id; }
	public void setConsumer_id(BigDecimal consumer_id) {
		if(consumer_id==null) throw new BusinessException("Entity : SdmConDbCol.consumer_id must not null!");
		this.consumer_id = consumer_id;
	}

	public String getIs_send() { return is_send; }
	public void setIs_send(String is_send) {
		if(is_send==null) throw new BusinessException("Entity : SdmConDbCol.is_send must not null!");
		this.is_send = is_send;
	}

	public BigDecimal getRowkey_seq() { return rowkey_seq; }
	public void setRowkey_seq(BigDecimal rowkey_seq) {
		if(rowkey_seq==null) addNullValueField("rowkey_seq");
		this.rowkey_seq = rowkey_seq;
	}

	public BigDecimal getNum() { return num; }
	public void setNum(BigDecimal num) {
		if(num==null) throw new BusinessException("Entity : SdmConDbCol.num must not null!");
		this.num = num;
	}

	public String getSdm_col_name_cn() { return sdm_col_name_cn; }
	public void setSdm_col_name_cn(String sdm_col_name_cn) {
		if(sdm_col_name_cn==null) throw new BusinessException("Entity : SdmConDbCol.sdm_col_name_cn must not null!");
		this.sdm_col_name_cn = sdm_col_name_cn;
	}

	public BigDecimal getSdm_col_id() { return sdm_col_id; }
	public void setSdm_col_id(BigDecimal sdm_col_id) {
		if(sdm_col_id==null) throw new BusinessException("Entity : SdmConDbCol.sdm_col_id must not null!");
		this.sdm_col_id = sdm_col_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getSdm_var_type() { return sdm_var_type; }
	public void setSdm_var_type(String sdm_var_type) {
		if(sdm_var_type==null) throw new BusinessException("Entity : SdmConDbCol.sdm_var_type must not null!");
		this.sdm_var_type = sdm_var_type;
	}

	public String getSdm_col_name_en() { return sdm_col_name_en; }
	public void setSdm_col_name_en(String sdm_col_name_en) {
		if(sdm_col_name_en==null) throw new BusinessException("Entity : SdmConDbCol.sdm_col_name_en must not null!");
		this.sdm_col_name_en = sdm_col_name_en;
	}

	public String getIs_rowkey() { return is_rowkey; }
	public void setIs_rowkey(String is_rowkey) {
		if(is_rowkey==null) throw new BusinessException("Entity : SdmConDbCol.is_rowkey must not null!");
		this.is_rowkey = is_rowkey;
	}

}