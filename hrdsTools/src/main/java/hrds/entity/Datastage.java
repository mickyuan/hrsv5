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
@Table(tableName = "datastage")
public class Datastage extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "datastage";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("tablename");
		__tmpPKS.add("stage");
		__tmpPKS.add("jobkey");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String stage;
	private String remark;
	private String state;
	private String tablename;
	private String nextstage;
	private BigDecimal jobkey;
	private String previousstage;

	public String getStage() { return stage; }
	public void setStage(String stage) {
		if(stage==null) throw new BusinessException("Entity : Datastage.stage must not null!");
		this.stage = stage;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getState() { return state; }
	public void setState(String state) {
		if(state==null) throw new BusinessException("Entity : Datastage.state must not null!");
		this.state = state;
	}

	public String getTablename() { return tablename; }
	public void setTablename(String tablename) {
		if(tablename==null) throw new BusinessException("Entity : Datastage.tablename must not null!");
		this.tablename = tablename;
	}

	public String getNextstage() { return nextstage; }
	public void setNextstage(String nextstage) {
		if(nextstage==null) addNullValueField("nextstage");
		this.nextstage = nextstage;
	}

	public BigDecimal getJobkey() { return jobkey; }
	public void setJobkey(BigDecimal jobkey) {
		if(jobkey==null) throw new BusinessException("Entity : Datastage.jobkey must not null!");
		this.jobkey = jobkey;
	}

	public String getPreviousstage() { return previousstage; }
	public void setPreviousstage(String previousstage) {
		if(previousstage==null) addNullValueField("previousstage");
		this.previousstage = previousstage;
	}

}