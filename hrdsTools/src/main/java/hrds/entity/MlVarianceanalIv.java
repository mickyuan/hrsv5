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
@Table(tableName = "ml_varianceanal_iv")
public class MlVarianceanalIv extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_varianceanal_iv";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("varianal_iv_id");
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
	private BigDecimal dtable_info_id;
	private BigDecimal varianal_iv_id;
	private String iv_column;
	private String create_date;
	private String create_time;

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getDtable_info_id() { return dtable_info_id; }
	public void setDtable_info_id(BigDecimal dtable_info_id) {
		if(dtable_info_id==null) throw new BusinessException("Entity : MlVarianceanalIv.dtable_info_id must not null!");
		this.dtable_info_id = dtable_info_id;
	}

	public BigDecimal getVarianal_iv_id() { return varianal_iv_id; }
	public void setVarianal_iv_id(BigDecimal varianal_iv_id) {
		if(varianal_iv_id==null) throw new BusinessException("Entity : MlVarianceanalIv.varianal_iv_id must not null!");
		this.varianal_iv_id = varianal_iv_id;
	}

	public String getIv_column() { return iv_column; }
	public void setIv_column(String iv_column) {
		if(iv_column==null) throw new BusinessException("Entity : MlVarianceanalIv.iv_column must not null!");
		this.iv_column = iv_column;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlVarianceanalIv.create_date must not null!");
		this.create_date = create_date;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlVarianceanalIv.create_time must not null!");
		this.create_time = create_time;
	}

}