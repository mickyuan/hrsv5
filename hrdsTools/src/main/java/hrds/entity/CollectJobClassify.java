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
@Table(tableName = "collect_job_classify")
public class CollectJobClassify extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "collect_job_classify";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("classify_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String classify_num;
	private String remark;
	private BigDecimal agent_id;
	private BigDecimal user_id;
	private String classify_name;
	private BigDecimal classify_id;

	public String getClassify_num() { return classify_num; }
	public void setClassify_num(String classify_num) {
		if(classify_num==null) throw new BusinessException("Entity : CollectJobClassify.classify_num must not null!");
		this.classify_num = classify_num;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : CollectJobClassify.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : CollectJobClassify.user_id must not null!");
		this.user_id = user_id;
	}

	public String getClassify_name() { return classify_name; }
	public void setClassify_name(String classify_name) {
		if(classify_name==null) throw new BusinessException("Entity : CollectJobClassify.classify_name must not null!");
		this.classify_name = classify_name;
	}

	public BigDecimal getClassify_id() { return classify_id; }
	public void setClassify_id(BigDecimal classify_id) {
		if(classify_id==null) throw new BusinessException("Entity : CollectJobClassify.classify_id must not null!");
		this.classify_id = classify_id;
	}

}