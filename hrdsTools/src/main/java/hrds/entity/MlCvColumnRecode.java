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
@Table(tableName = "ml_cv_column_recode")
public class MlCvColumnRecode extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_cv_column_recode";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("columnreco_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal recode_id;
	private String newcolumn_name;
	private BigDecimal columnreco_id;
	private String value_recode;

	public BigDecimal getRecode_id() { return recode_id; }
	public void setRecode_id(BigDecimal recode_id) {
		if(recode_id==null) throw new BusinessException("Entity : MlCvColumnRecode.recode_id must not null!");
		this.recode_id = recode_id;
	}

	public String getNewcolumn_name() { return newcolumn_name; }
	public void setNewcolumn_name(String newcolumn_name) {
		if(newcolumn_name==null) throw new BusinessException("Entity : MlCvColumnRecode.newcolumn_name must not null!");
		this.newcolumn_name = newcolumn_name;
	}

	public BigDecimal getColumnreco_id() { return columnreco_id; }
	public void setColumnreco_id(BigDecimal columnreco_id) {
		if(columnreco_id==null) throw new BusinessException("Entity : MlCvColumnRecode.columnreco_id must not null!");
		this.columnreco_id = columnreco_id;
	}

	public String getValue_recode() { return value_recode; }
	public void setValue_recode(String value_recode) {
		if(value_recode==null) addNullValueField("value_recode");
		this.value_recode = value_recode;
	}

}