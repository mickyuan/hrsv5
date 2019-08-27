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
@Table(tableName = "orig_code_info")
public class OrigCodeInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "orig_code_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("orig_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String code_value;
	private String code_classify;
	private String orig_value;
	private BigDecimal orig_id;
	private String orig_sys_code;
	private String code_remark;

	public String getCode_value() { return code_value; }
	public void setCode_value(String code_value) {
		if(code_value==null) throw new BusinessException("Entity : OrigCodeInfo.code_value must not null!");
		this.code_value = code_value;
	}

	public String getCode_classify() { return code_classify; }
	public void setCode_classify(String code_classify) {
		if(code_classify==null) throw new BusinessException("Entity : OrigCodeInfo.code_classify must not null!");
		this.code_classify = code_classify;
	}

	public String getOrig_value() { return orig_value; }
	public void setOrig_value(String orig_value) {
		if(orig_value==null) throw new BusinessException("Entity : OrigCodeInfo.orig_value must not null!");
		this.orig_value = orig_value;
	}

	public BigDecimal getOrig_id() { return orig_id; }
	public void setOrig_id(BigDecimal orig_id) {
		if(orig_id==null) throw new BusinessException("Entity : OrigCodeInfo.orig_id must not null!");
		this.orig_id = orig_id;
	}

	public String getOrig_sys_code() { return orig_sys_code; }
	public void setOrig_sys_code(String orig_sys_code) {
		if(orig_sys_code==null) addNullValueField("orig_sys_code");
		this.orig_sys_code = orig_sys_code;
	}

	public String getCode_remark() { return code_remark; }
	public void setCode_remark(String code_remark) {
		if(code_remark==null) addNullValueField("code_remark");
		this.code_remark = code_remark;
	}

}