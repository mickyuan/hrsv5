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
@Table(tableName = "keytable")
public class Keytable extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "keytable";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("key_name");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String key_name;
	private Integer key_value;

	public String getKey_name() { return key_name; }
	public void setKey_name(String key_name) {
		if(key_name==null) throw new BusinessException("Entity : Keytable.key_name must not null!");
		this.key_name = key_name;
	}

	public Integer getKey_value() { return key_value; }
	public void setKey_value(Integer key_value) {
		if(key_value==null) addNullValueField("key_value");
		this.key_value = key_value;
	}

}