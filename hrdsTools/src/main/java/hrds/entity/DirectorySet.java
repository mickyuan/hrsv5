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
@Table(tableName = "directory_set")
public class DirectorySet extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "directory_set";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("set_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal set_id;
	private String remark;
	private String watch_type;
	private String watch_dir;

	public BigDecimal getSet_id() { return set_id; }
	public void setSet_id(BigDecimal set_id) {
		if(set_id==null) throw new BusinessException("Entity : DirectorySet.set_id must not null!");
		this.set_id = set_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getWatch_type() { return watch_type; }
	public void setWatch_type(String watch_type) {
		if(watch_type==null) throw new BusinessException("Entity : DirectorySet.watch_type must not null!");
		this.watch_type = watch_type;
	}

	public String getWatch_dir() { return watch_dir; }
	public void setWatch_dir(String watch_dir) {
		if(watch_dir==null) throw new BusinessException("Entity : DirectorySet.watch_dir must not null!");
		this.watch_dir = watch_dir;
	}

}