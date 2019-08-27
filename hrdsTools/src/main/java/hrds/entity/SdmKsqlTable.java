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
@Table(tableName = "sdm_ksql_table")
public class SdmKsqlTable extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_ksql_table";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sdm_ksql_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String create_time;
	private String table_remark;
	private String execute_sql;
	private String job_desc;
	private String auto_offset;
	private String is_create_sql;
	private String stram_table;
	private String sdm_top_name;
	private BigDecimal sdm_receive_id;
	private String consumer_name;
	private BigDecimal sdm_ksql_id;
	private String create_date;
	private String table_type;

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : SdmKsqlTable.create_time must not null!");
		this.create_time = create_time;
	}

	public String getTable_remark() { return table_remark; }
	public void setTable_remark(String table_remark) {
		if(table_remark==null) addNullValueField("table_remark");
		this.table_remark = table_remark;
	}

	public String getExecute_sql() { return execute_sql; }
	public void setExecute_sql(String execute_sql) {
		if(execute_sql==null) throw new BusinessException("Entity : SdmKsqlTable.execute_sql must not null!");
		this.execute_sql = execute_sql;
	}

	public String getJob_desc() { return job_desc; }
	public void setJob_desc(String job_desc) {
		if(job_desc==null) addNullValueField("job_desc");
		this.job_desc = job_desc;
	}

	public String getAuto_offset() { return auto_offset; }
	public void setAuto_offset(String auto_offset) {
		if(auto_offset==null) addNullValueField("auto_offset");
		this.auto_offset = auto_offset;
	}

	public String getIs_create_sql() { return is_create_sql; }
	public void setIs_create_sql(String is_create_sql) {
		if(is_create_sql==null) throw new BusinessException("Entity : SdmKsqlTable.is_create_sql must not null!");
		this.is_create_sql = is_create_sql;
	}

	public String getStram_table() { return stram_table; }
	public void setStram_table(String stram_table) {
		if(stram_table==null) throw new BusinessException("Entity : SdmKsqlTable.stram_table must not null!");
		this.stram_table = stram_table;
	}

	public String getSdm_top_name() { return sdm_top_name; }
	public void setSdm_top_name(String sdm_top_name) {
		if(sdm_top_name==null) addNullValueField("sdm_top_name");
		this.sdm_top_name = sdm_top_name;
	}

	public BigDecimal getSdm_receive_id() { return sdm_receive_id; }
	public void setSdm_receive_id(BigDecimal sdm_receive_id) {
		if(sdm_receive_id==null) throw new BusinessException("Entity : SdmKsqlTable.sdm_receive_id must not null!");
		this.sdm_receive_id = sdm_receive_id;
	}

	public String getConsumer_name() { return consumer_name; }
	public void setConsumer_name(String consumer_name) {
		if(consumer_name==null) throw new BusinessException("Entity : SdmKsqlTable.consumer_name must not null!");
		this.consumer_name = consumer_name;
	}

	public BigDecimal getSdm_ksql_id() { return sdm_ksql_id; }
	public void setSdm_ksql_id(BigDecimal sdm_ksql_id) {
		if(sdm_ksql_id==null) throw new BusinessException("Entity : SdmKsqlTable.sdm_ksql_id must not null!");
		this.sdm_ksql_id = sdm_ksql_id;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : SdmKsqlTable.create_date must not null!");
		this.create_date = create_date;
	}

	public String getTable_type() { return table_type; }
	public void setTable_type(String table_type) {
		if(table_type==null) throw new BusinessException("Entity : SdmKsqlTable.table_type must not null!");
		this.table_type = table_type;
	}

}