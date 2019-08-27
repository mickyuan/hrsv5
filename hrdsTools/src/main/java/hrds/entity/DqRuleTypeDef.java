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
@Table(tableName = "dq_rule_type_def")
public class DqRuleTypeDef extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_rule_type_def";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("case_type");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String case_type;
	private String index_desc2;
	private String remark;
	private String index_desc3;
	private String index_desc1;
	private String case_type_desc;

	public String getCase_type() { return case_type; }
	public void setCase_type(String case_type) {
		if(case_type==null) throw new BusinessException("Entity : DqRuleTypeDef.case_type must not null!");
		this.case_type = case_type;
	}

	public String getIndex_desc2() { return index_desc2; }
	public void setIndex_desc2(String index_desc2) {
		if(index_desc2==null) addNullValueField("index_desc2");
		this.index_desc2 = index_desc2;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getIndex_desc3() { return index_desc3; }
	public void setIndex_desc3(String index_desc3) {
		if(index_desc3==null) addNullValueField("index_desc3");
		this.index_desc3 = index_desc3;
	}

	public String getIndex_desc1() { return index_desc1; }
	public void setIndex_desc1(String index_desc1) {
		if(index_desc1==null) addNullValueField("index_desc1");
		this.index_desc1 = index_desc1;
	}

	public String getCase_type_desc() { return case_type_desc; }
	public void setCase_type_desc(String case_type_desc) {
		if(case_type_desc==null) addNullValueField("case_type_desc");
		this.case_type_desc = case_type_desc;
	}

}