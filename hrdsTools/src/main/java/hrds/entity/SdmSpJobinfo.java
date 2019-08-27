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
@Table(tableName = "sdm_sp_jobinfo")
public class SdmSpJobinfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_sp_jobinfo";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ssj_job_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String ssj_job_name;
	private String ssj_job_desc;
	private BigDecimal user_id;
	private BigDecimal ssj_job_id;
	private String ssj_strategy;

	public String getSsj_job_name() { return ssj_job_name; }
	public void setSsj_job_name(String ssj_job_name) {
		if(ssj_job_name==null) throw new BusinessException("Entity : SdmSpJobinfo.ssj_job_name must not null!");
		this.ssj_job_name = ssj_job_name;
	}

	public String getSsj_job_desc() { return ssj_job_desc; }
	public void setSsj_job_desc(String ssj_job_desc) {
		if(ssj_job_desc==null) addNullValueField("ssj_job_desc");
		this.ssj_job_desc = ssj_job_desc;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : SdmSpJobinfo.user_id must not null!");
		this.user_id = user_id;
	}

	public BigDecimal getSsj_job_id() { return ssj_job_id; }
	public void setSsj_job_id(BigDecimal ssj_job_id) {
		if(ssj_job_id==null) throw new BusinessException("Entity : SdmSpJobinfo.ssj_job_id must not null!");
		this.ssj_job_id = ssj_job_id;
	}

	public String getSsj_strategy() { return ssj_strategy; }
	public void setSsj_strategy(String ssj_strategy) {
		if(ssj_strategy==null) throw new BusinessException("Entity : SdmSpJobinfo.ssj_strategy must not null!");
		this.ssj_strategy = ssj_strategy;
	}

}