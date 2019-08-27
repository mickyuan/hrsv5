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
@Table(tableName = "sql_record_table")
public class SqlRecordTable extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sql_record_table";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sql_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String condition_analyse;
	private String select_analyse;
	private String table_analyse;
	private BigDecimal sql_id;
	private String create_time;
	private String update_date;
	private String groupby_analyse;
	private String sql_original;
	private String update_time;
	private String select_as_column;
	private BigDecimal create_id;
	private String clean_sql;
	private String join_analyse;
	private String sql_name;
	private String create_date;
	private String orderby_analyse;
	private String sql_details;
	private String limit_analyse;

	public String getCondition_analyse() { return condition_analyse; }
	public void setCondition_analyse(String condition_analyse) {
		if(condition_analyse==null) addNullValueField("condition_analyse");
		this.condition_analyse = condition_analyse;
	}

	public String getSelect_analyse() { return select_analyse; }
	public void setSelect_analyse(String select_analyse) {
		if(select_analyse==null) addNullValueField("select_analyse");
		this.select_analyse = select_analyse;
	}

	public String getTable_analyse() { return table_analyse; }
	public void setTable_analyse(String table_analyse) {
		if(table_analyse==null) addNullValueField("table_analyse");
		this.table_analyse = table_analyse;
	}

	public BigDecimal getSql_id() { return sql_id; }
	public void setSql_id(BigDecimal sql_id) {
		if(sql_id==null) throw new BusinessException("Entity : SqlRecordTable.sql_id must not null!");
		this.sql_id = sql_id;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : SqlRecordTable.create_time must not null!");
		this.create_time = create_time;
	}

	public String getUpdate_date() { return update_date; }
	public void setUpdate_date(String update_date) {
		if(update_date==null) throw new BusinessException("Entity : SqlRecordTable.update_date must not null!");
		this.update_date = update_date;
	}

	public String getGroupby_analyse() { return groupby_analyse; }
	public void setGroupby_analyse(String groupby_analyse) {
		if(groupby_analyse==null) addNullValueField("groupby_analyse");
		this.groupby_analyse = groupby_analyse;
	}

	public String getSql_original() { return sql_original; }
	public void setSql_original(String sql_original) {
		if(sql_original==null) addNullValueField("sql_original");
		this.sql_original = sql_original;
	}

	public String getUpdate_time() { return update_time; }
	public void setUpdate_time(String update_time) {
		if(update_time==null) throw new BusinessException("Entity : SqlRecordTable.update_time must not null!");
		this.update_time = update_time;
	}

	public String getSelect_as_column() { return select_as_column; }
	public void setSelect_as_column(String select_as_column) {
		if(select_as_column==null) addNullValueField("select_as_column");
		this.select_as_column = select_as_column;
	}

	public BigDecimal getCreate_id() { return create_id; }
	public void setCreate_id(BigDecimal create_id) {
		if(create_id==null) throw new BusinessException("Entity : SqlRecordTable.create_id must not null!");
		this.create_id = create_id;
	}

	public String getClean_sql() { return clean_sql; }
	public void setClean_sql(String clean_sql) {
		if(clean_sql==null) addNullValueField("clean_sql");
		this.clean_sql = clean_sql;
	}

	public String getJoin_analyse() { return join_analyse; }
	public void setJoin_analyse(String join_analyse) {
		if(join_analyse==null) addNullValueField("join_analyse");
		this.join_analyse = join_analyse;
	}

	public String getSql_name() { return sql_name; }
	public void setSql_name(String sql_name) {
		if(sql_name==null) addNullValueField("sql_name");
		this.sql_name = sql_name;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : SqlRecordTable.create_date must not null!");
		this.create_date = create_date;
	}

	public String getOrderby_analyse() { return orderby_analyse; }
	public void setOrderby_analyse(String orderby_analyse) {
		if(orderby_analyse==null) addNullValueField("orderby_analyse");
		this.orderby_analyse = orderby_analyse;
	}

	public String getSql_details() { return sql_details; }
	public void setSql_details(String sql_details) {
		if(sql_details==null) throw new BusinessException("Entity : SqlRecordTable.sql_details must not null!");
		this.sql_details = sql_details;
	}

	public String getLimit_analyse() { return limit_analyse; }
	public void setLimit_analyse(String limit_analyse) {
		if(limit_analyse==null) addNullValueField("limit_analyse");
		this.limit_analyse = limit_analyse;
	}

}