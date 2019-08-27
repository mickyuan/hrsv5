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
@Table(tableName = "hyren_code_info")
public class HyrenCodeInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "hyren_code_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("code_value");
		__tmpPKS.add("code_classify");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String code_type_name;
	private String code_value;
	private String code_classify;
	private String code_classify_name;
	private String code_remark;

	public String getCode_type_name() { return code_type_name; }
	public void setCode_type_name(String code_type_name) {
		if(code_type_name==null) throw new BusinessException("Entity : HyrenCodeInfo.code_type_name must not null!");
		this.code_type_name = code_type_name;
	}

	public String getCode_value() { return code_value; }
	public void setCode_value(String code_value) {
		if(code_value==null) throw new BusinessException("Entity : HyrenCodeInfo.code_value must not null!");
		this.code_value = code_value;
	}

	public String getCode_classify() { return code_classify; }
	public void setCode_classify(String code_classify) {
		if(code_classify==null) throw new BusinessException("Entity : HyrenCodeInfo.code_classify must not null!");
		this.code_classify = code_classify;
	}

	public String getCode_classify_name() { return code_classify_name; }
	public void setCode_classify_name(String code_classify_name) {
		if(code_classify_name==null) throw new BusinessException("Entity : HyrenCodeInfo.code_classify_name must not null!");
		this.code_classify_name = code_classify_name;
	}

	public String getCode_remark() { return code_remark; }
	public void setCode_remark(String code_remark) {
		if(code_remark==null) addNullValueField("code_remark");
		this.code_remark = code_remark;
	}

}