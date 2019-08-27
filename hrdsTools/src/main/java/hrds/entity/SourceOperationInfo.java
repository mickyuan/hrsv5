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
@Table(tableName = "source_operation_info")
public class SourceOperationInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "source_operation_info";

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

	private String remark;
	private BigDecimal id;
	private String search_name;
	private BigDecimal datatable_id;
	private String execute_sql;

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getId() { return id; }
	public void setId(BigDecimal id) {
		if(id==null) throw new BusinessException("Entity : SourceOperationInfo.id must not null!");
		this.id = id;
	}

	public String getSearch_name() { return search_name; }
	public void setSearch_name(String search_name) {
		if(search_name==null) addNullValueField("search_name");
		this.search_name = search_name;
	}

	public BigDecimal getDatatable_id() { return datatable_id; }
	public void setDatatable_id(BigDecimal datatable_id) {
		if(datatable_id==null) throw new BusinessException("Entity : SourceOperationInfo.datatable_id must not null!");
		this.datatable_id = datatable_id;
	}

	public String getExecute_sql() { return execute_sql; }
	public void setExecute_sql(String execute_sql) {
		if(execute_sql==null) throw new BusinessException("Entity : SourceOperationInfo.execute_sql must not null!");
		this.execute_sql = execute_sql;
	}

}