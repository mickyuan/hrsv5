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
@Table(tableName = "directory_info")
public class DirectoryInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "directory_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String dir_status;
	private String dir_time;
	private String dir_desc;
	private String dir_date;
	private String dir_name;
	private BigDecimal set_id;
	private String remark;
	private BigDecimal id;
	private String dir_path;

	public String getDir_status() { return dir_status; }
	public void setDir_status(String dir_status) {
		if(dir_status==null) throw new BusinessException("Entity : DirectoryInfo.dir_status must not null!");
		this.dir_status = dir_status;
	}

	public String getDir_time() { return dir_time; }
	public void setDir_time(String dir_time) {
		if(dir_time==null) throw new BusinessException("Entity : DirectoryInfo.dir_time must not null!");
		this.dir_time = dir_time;
	}

	public String getDir_desc() { return dir_desc; }
	public void setDir_desc(String dir_desc) {
		if(dir_desc==null) addNullValueField("dir_desc");
		this.dir_desc = dir_desc;
	}

	public String getDir_date() { return dir_date; }
	public void setDir_date(String dir_date) {
		if(dir_date==null) throw new BusinessException("Entity : DirectoryInfo.dir_date must not null!");
		this.dir_date = dir_date;
	}

	public String getDir_name() { return dir_name; }
	public void setDir_name(String dir_name) {
		if(dir_name==null) addNullValueField("dir_name");
		this.dir_name = dir_name;
	}

	public BigDecimal getSet_id() { return set_id; }
	public void setSet_id(BigDecimal set_id) {
		if(set_id==null) throw new BusinessException("Entity : DirectoryInfo.set_id must not null!");
		this.set_id = set_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getId() { return id; }
	public void setId(BigDecimal id) {
		if(id==null) throw new BusinessException("Entity : DirectoryInfo.id must not null!");
		this.id = id;
	}

	public String getDir_path() { return dir_path; }
	public void setDir_path(String dir_path) {
		if(dir_path==null) throw new BusinessException("Entity : DirectoryInfo.dir_path must not null!");
		this.dir_path = dir_path;
	}

}