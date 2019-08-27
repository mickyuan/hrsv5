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
@Table(tableName = "update_info")
public class UpdateInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "update_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("update_type");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String update_type;
	private String is_update;
	private String remark;

	public String getUpdate_type() { return update_type; }
	public void setUpdate_type(String update_type) {
		if(update_type==null) throw new BusinessException("Entity : UpdateInfo.update_type must not null!");
		this.update_type = update_type;
	}

	public String getIs_update() { return is_update; }
	public void setIs_update(String is_update) {
		if(is_update==null) throw new BusinessException("Entity : UpdateInfo.is_update must not null!");
		this.is_update = is_update;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

}