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
@Table(tableName = "ml_sql_record")
public class MlSqlRecord extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_sql_record";

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
	private String table_analyse;
	private BigDecimal sql_id;
	private String create_time;
	private String having_analyse;
	private String groupby_analyse;
	private String sql_original;
	private String select_as_column;
	private String join_analyse;
	private BigDecimal project_id;
	private BigDecimal user_id;
	private String create_date;
	private String orderby_analyse;
	private String limit_analyse;

	public String getCondition_analyse() { return condition_analyse; }
	public void setCondition_analyse(String condition_analyse) {
		if(condition_analyse==null) addNullValueField("condition_analyse");
		this.condition_analyse = condition_analyse;
	}

	public String getTable_analyse() { return table_analyse; }
	public void setTable_analyse(String table_analyse) {
		if(table_analyse==null) addNullValueField("table_analyse");
		this.table_analyse = table_analyse;
	}

	public BigDecimal getSql_id() { return sql_id; }
	public void setSql_id(BigDecimal sql_id) {
		if(sql_id==null) throw new BusinessException("Entity : MlSqlRecord.sql_id must not null!");
		this.sql_id = sql_id;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : MlSqlRecord.create_time must not null!");
		this.create_time = create_time;
	}

	public String getHaving_analyse() { return having_analyse; }
	public void setHaving_analyse(String having_analyse) {
		if(having_analyse==null) addNullValueField("having_analyse");
		this.having_analyse = having_analyse;
	}

	public String getGroupby_analyse() { return groupby_analyse; }
	public void setGroupby_analyse(String groupby_analyse) {
		if(groupby_analyse==null) addNullValueField("groupby_analyse");
		this.groupby_analyse = groupby_analyse;
	}

	public String getSql_original() { return sql_original; }
	public void setSql_original(String sql_original) {
		if(sql_original==null) throw new BusinessException("Entity : MlSqlRecord.sql_original must not null!");
		this.sql_original = sql_original;
	}

	public String getSelect_as_column() { return select_as_column; }
	public void setSelect_as_column(String select_as_column) {
		if(select_as_column==null) addNullValueField("select_as_column");
		this.select_as_column = select_as_column;
	}

	public String getJoin_analyse() { return join_analyse; }
	public void setJoin_analyse(String join_analyse) {
		if(join_analyse==null) addNullValueField("join_analyse");
		this.join_analyse = join_analyse;
	}

	public BigDecimal getProject_id() { return project_id; }
	public void setProject_id(BigDecimal project_id) {
		if(project_id==null) addNullValueField("project_id");
		this.project_id = project_id;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : MlSqlRecord.user_id must not null!");
		this.user_id = user_id;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : MlSqlRecord.create_date must not null!");
		this.create_date = create_date;
	}

	public String getOrderby_analyse() { return orderby_analyse; }
	public void setOrderby_analyse(String orderby_analyse) {
		if(orderby_analyse==null) addNullValueField("orderby_analyse");
		this.orderby_analyse = orderby_analyse;
	}

	public String getLimit_analyse() { return limit_analyse; }
	public void setLimit_analyse(String limit_analyse) {
		if(limit_analyse==null) addNullValueField("limit_analyse");
		this.limit_analyse = limit_analyse;
	}

}