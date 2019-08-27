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
@Table(tableName = "edw_relevant_info")
public class EdwRelevantInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_relevant_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("rel_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String pre_work;
	private String jobcode;
	private String remark;
	private BigDecimal rel_id;
	private String post_work;

	public String getPre_work() { return pre_work; }
	public void setPre_work(String pre_work) {
		if(pre_work==null) addNullValueField("pre_work");
		this.pre_work = pre_work;
	}

	public String getJobcode() { return jobcode; }
	public void setJobcode(String jobcode) {
		if(jobcode==null) throw new BusinessException("Entity : EdwRelevantInfo.jobcode must not null!");
		this.jobcode = jobcode;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getRel_id() { return rel_id; }
	public void setRel_id(BigDecimal rel_id) {
		if(rel_id==null) throw new BusinessException("Entity : EdwRelevantInfo.rel_id must not null!");
		this.rel_id = rel_id;
	}

	public String getPost_work() { return post_work; }
	public void setPost_work(String post_work) {
		if(post_work==null) addNullValueField("post_work");
		this.post_work = post_work;
	}

}