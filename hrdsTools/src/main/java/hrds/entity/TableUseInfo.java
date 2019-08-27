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
@Table(tableName = "table_use_info")
public class TableUseInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "table_use_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("use_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal use_id;
	private String table_blsystem;
	private String table_note;
	private BigDecimal user_id;
	private String hbase_name;
	private String original_name;

	public BigDecimal getUse_id() { return use_id; }
	public void setUse_id(BigDecimal use_id) {
		if(use_id==null) throw new BusinessException("Entity : TableUseInfo.use_id must not null!");
		this.use_id = use_id;
	}

	public String getTable_blsystem() { return table_blsystem; }
	public void setTable_blsystem(String table_blsystem) {
		if(table_blsystem==null) throw new BusinessException("Entity : TableUseInfo.table_blsystem must not null!");
		this.table_blsystem = table_blsystem;
	}

	public String getTable_note() { return table_note; }
	public void setTable_note(String table_note) {
		if(table_note==null) addNullValueField("table_note");
		this.table_note = table_note;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : TableUseInfo.user_id must not null!");
		this.user_id = user_id;
	}

	public String getHbase_name() { return hbase_name; }
	public void setHbase_name(String hbase_name) {
		if(hbase_name==null) throw new BusinessException("Entity : TableUseInfo.hbase_name must not null!");
		this.hbase_name = hbase_name;
	}

	public String getOriginal_name() { return original_name; }
	public void setOriginal_name(String original_name) {
		if(original_name==null) throw new BusinessException("Entity : TableUseInfo.original_name must not null!");
		this.original_name = original_name;
	}

}