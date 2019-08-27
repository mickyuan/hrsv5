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
@Table(tableName = "component_param")
public class ComponentParam extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "component_param";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("param_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String param_value;
	private String comp_id;
	private String param_name;
	private BigDecimal param_id;
	private String is_must;
	private String param_remark;

	public String getParam_value() { return param_value; }
	public void setParam_value(String param_value) {
		if(param_value==null) throw new BusinessException("Entity : ComponentParam.param_value must not null!");
		this.param_value = param_value;
	}

	public String getComp_id() { return comp_id; }
	public void setComp_id(String comp_id) {
		if(comp_id==null) addNullValueField("comp_id");
		this.comp_id = comp_id;
	}

	public String getParam_name() { return param_name; }
	public void setParam_name(String param_name) {
		if(param_name==null) throw new BusinessException("Entity : ComponentParam.param_name must not null!");
		this.param_name = param_name;
	}

	public BigDecimal getParam_id() { return param_id; }
	public void setParam_id(BigDecimal param_id) {
		if(param_id==null) throw new BusinessException("Entity : ComponentParam.param_id must not null!");
		this.param_id = param_id;
	}

	public String getIs_must() { return is_must; }
	public void setIs_must(String is_must) {
		if(is_must==null) throw new BusinessException("Entity : ComponentParam.is_must must not null!");
		this.is_must = is_must;
	}

	public String getParam_remark() { return param_remark; }
	public void setParam_remark(String param_remark) {
		if(param_remark==null) throw new BusinessException("Entity : ComponentParam.param_remark must not null!");
		this.param_remark = param_remark;
	}

}