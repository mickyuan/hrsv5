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
@Table(tableName = "ml_custscricol")
public class MlCustscricol extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_custscricol";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("column_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal column_id;
	private String column_name;
	private BigDecimal script_id;
	private String column_type;
	private String create_date;
	private String create_time;

	public BigDecimal getColumn_id() { return column_id; }
	public void setColumn_id(BigDecimal column_id) {
		if(column_id==null) throw new BusinessException("Entity : MlCustscricol.column_id must not null!");
		this.column_id = column_id;
	}

	public String getColumn_name() { return column_name; }
	public void setColumn_name(String column_name) {
		if(column_name==null) throw new BusinessException("Entity : MlCustscricol.column_name must not null!");
		this.column_name = column_name;
	}

	public BigDecimal getScript_id() { return script_id; }
	public void setScript_id(BigDecimal script_id) {
		if(script_id==null) throw new BusinessException("Entity : MlCustscricol.script_id must not null!");
		this.script_id = script_id;
	}

	public String getColumn_type() { return column_type; }
	public void setColumn_type(String column_type) {
		if(column_type==null) throw new BusinessException("Entity : MlCustscricol.column_type must not null!");
		this.column_type = column_type;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlCustscricol.create_date must not null!");
		this.create_date = create_date;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlCustscricol.create_time must not null!");
		this.create_time = create_time;
	}

}