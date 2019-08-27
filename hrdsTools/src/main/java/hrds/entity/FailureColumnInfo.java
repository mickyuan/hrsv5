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
@Table(tableName = "failure_column_info")
public class FailureColumnInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "failure_column_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("failure_column_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String column_meta_info;
	private BigDecimal failure_table_id;
	private String remark;
	private BigDecimal failure_column_id;
	private String column_source;

	public String getColumn_meta_info() { return column_meta_info; }
	public void setColumn_meta_info(String column_meta_info) {
		if(column_meta_info==null) throw new BusinessException("Entity : FailureColumnInfo.column_meta_info must not null!");
		this.column_meta_info = column_meta_info;
	}

	public BigDecimal getFailure_table_id() { return failure_table_id; }
	public void setFailure_table_id(BigDecimal failure_table_id) {
		if(failure_table_id==null) throw new BusinessException("Entity : FailureColumnInfo.failure_table_id must not null!");
		this.failure_table_id = failure_table_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getFailure_column_id() { return failure_column_id; }
	public void setFailure_column_id(BigDecimal failure_column_id) {
		if(failure_column_id==null) throw new BusinessException("Entity : FailureColumnInfo.failure_column_id must not null!");
		this.failure_column_id = failure_column_id;
	}

	public String getColumn_source() { return column_source; }
	public void setColumn_source(String column_source) {
		if(column_source==null) throw new BusinessException("Entity : FailureColumnInfo.column_source must not null!");
		this.column_source = column_source;
	}

}