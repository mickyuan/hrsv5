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
@Table(tableName = "sys_para2")
public class SysPara2 extends TableEntity {
    private static final long serialVersionUID = 321566460595860L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sys_para2";

	static {
		__PrimaryKeys = Collections.emptySet();
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String para_value;
	private String para_name;

	public String getPara_value() { return para_value; }
	public void setPara_value(String para_value) {
		if(para_value==null) addNullValueField("para_value");
		this.para_value = para_value;
	}

	public String getPara_name() { return para_name; }
	public void setPara_name(String para_name) {
		if(para_name==null) addNullValueField("para_name");
		this.para_name = para_name;
	}

}