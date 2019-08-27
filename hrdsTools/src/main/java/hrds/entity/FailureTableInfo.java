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
@Table(tableName = "failure_table_info")
public class FailureTableInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "failure_table_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("failure_table_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal failure_table_id;
	private String remark;
	private String table_cn_name;
	private String table_source;
	private String table_meta_info;
	private String table_en_name;

	public BigDecimal getFailure_table_id() { return failure_table_id; }
	public void setFailure_table_id(BigDecimal failure_table_id) {
		if(failure_table_id==null) throw new BusinessException("Entity : FailureTableInfo.failure_table_id must not null!");
		this.failure_table_id = failure_table_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getTable_cn_name() { return table_cn_name; }
	public void setTable_cn_name(String table_cn_name) {
		if(table_cn_name==null) addNullValueField("table_cn_name");
		this.table_cn_name = table_cn_name;
	}

	public String getTable_source() { return table_source; }
	public void setTable_source(String table_source) {
		if(table_source==null) throw new BusinessException("Entity : FailureTableInfo.table_source must not null!");
		this.table_source = table_source;
	}

	public String getTable_meta_info() { return table_meta_info; }
	public void setTable_meta_info(String table_meta_info) {
		if(table_meta_info==null) throw new BusinessException("Entity : FailureTableInfo.table_meta_info must not null!");
		this.table_meta_info = table_meta_info;
	}

	public String getTable_en_name() { return table_en_name; }
	public void setTable_en_name(String table_en_name) {
		if(table_en_name==null) throw new BusinessException("Entity : FailureTableInfo.table_en_name must not null!");
		this.table_en_name = table_en_name;
	}

}