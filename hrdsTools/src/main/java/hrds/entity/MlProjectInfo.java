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
@Table(tableName = "ml_project_info")
public class MlProjectInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_project_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("project_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String create_time;
	private String remark;
	private String project_name;
	private String now_exec_date;
	private String project_desc;
	private String last_exec_date;
	private String now_exec_time;
	private BigDecimal project_id;
	private BigDecimal user_id;
	private String last_exec_time;
	private String goodness_of_fit;
	private String create_date;
	private String publish_status;

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlProjectInfo.create_time must not null!");
		this.create_time = create_time;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getProject_name() { return project_name; }
	public void setProject_name(String project_name) {
		if(project_name==null) throw new BusinessException("Entity : MlProjectInfo.project_name must not null!");
		this.project_name = project_name;
	}

	public String getNow_exec_date() { return now_exec_date; }
	public void setNow_exec_date(String now_exec_date) {
		if(now_exec_date==null) throw new BusinessException("Entity : MlProjectInfo.now_exec_date must not null!");
		this.now_exec_date = now_exec_date;
	}

	public String getProject_desc() { return project_desc; }
	public void setProject_desc(String project_desc) {
		if(project_desc==null) throw new BusinessException("Entity : MlProjectInfo.project_desc must not null!");
		this.project_desc = project_desc;
	}

	public String getLast_exec_date() { return last_exec_date; }
	public void setLast_exec_date(String last_exec_date) {
		if(last_exec_date==null) throw new BusinessException("Entity : MlProjectInfo.last_exec_date must not null!");
		this.last_exec_date = last_exec_date;
	}

	public String getNow_exec_time() { return now_exec_time; }
	public void setNow_exec_time(String now_exec_time) {
		if(now_exec_time==null) throw new BusinessException("Entity : MlProjectInfo.now_exec_time must not null!");
		this.now_exec_time = now_exec_time;
	}

	public BigDecimal getProject_id() { return project_id; }
	public void setProject_id(BigDecimal project_id) {
		if(project_id==null) throw new BusinessException("Entity : MlProjectInfo.project_id must not null!");
		this.project_id = project_id;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : MlProjectInfo.user_id must not null!");
		this.user_id = user_id;
	}

	public String getLast_exec_time() { return last_exec_time; }
	public void setLast_exec_time(String last_exec_time) {
		if(last_exec_time==null) throw new BusinessException("Entity : MlProjectInfo.last_exec_time must not null!");
		this.last_exec_time = last_exec_time;
	}

	public String getGoodness_of_fit() { return goodness_of_fit; }
	public void setGoodness_of_fit(String goodness_of_fit) {
		if(goodness_of_fit==null) addNullValueField("goodness_of_fit");
		this.goodness_of_fit = goodness_of_fit;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlProjectInfo.create_date must not null!");
		this.create_date = create_date;
	}

	public String getPublish_status() { return publish_status; }
	public void setPublish_status(String publish_status) {
		if(publish_status==null) throw new BusinessException("Entity : MlProjectInfo.publish_status must not null!");
		this.publish_status = publish_status;
	}

}