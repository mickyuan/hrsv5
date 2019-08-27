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
@Table(tableName = "collect_case")
public class CollectCase extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "collect_case";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("job_rs_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String job_type;
	private String is_again;
	private BigDecimal collect_set_id;
	private String collet_database_size;
	private String job_rs_id;
	private String execute_state;
	private BigDecimal agent_id;
	private String job_group;
	private BigDecimal colect_record;
	private String collect_e_time;
	private String execute_length;
	private BigDecimal collect_total;
	private BigDecimal again_num;
	private String table_name;
	private String etl_date;
	private String collect_e_date;
	private String collect_type;
	private String collect_s_time;
	private String cc_remark;
	private BigDecimal source_id;
	private String collect_s_date;

	public String getJob_type() { return job_type; }
	public void setJob_type(String job_type) {
		if(job_type==null) addNullValueField("job_type");
		this.job_type = job_type;
	}

	public String getIs_again() { return is_again; }
	public void setIs_again(String is_again) {
		if(is_again==null) throw new BusinessException("Entity : CollectCase.is_again must not null!");
		this.is_again = is_again;
	}

	public BigDecimal getCollect_set_id() { return collect_set_id; }
	public void setCollect_set_id(BigDecimal collect_set_id) {
		if(collect_set_id==null) throw new BusinessException("Entity : CollectCase.collect_set_id must not null!");
		this.collect_set_id = collect_set_id;
	}

	public String getCollet_database_size() { return collet_database_size; }
	public void setCollet_database_size(String collet_database_size) {
		if(collet_database_size==null) addNullValueField("collet_database_size");
		this.collet_database_size = collet_database_size;
	}

	public String getJob_rs_id() { return job_rs_id; }
	public void setJob_rs_id(String job_rs_id) {
		if(job_rs_id==null) throw new BusinessException("Entity : CollectCase.job_rs_id must not null!");
		this.job_rs_id = job_rs_id;
	}

	public String getExecute_state() { return execute_state; }
	public void setExecute_state(String execute_state) {
		if(execute_state==null) throw new BusinessException("Entity : CollectCase.execute_state must not null!");
		this.execute_state = execute_state;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : CollectCase.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public String getJob_group() { return job_group; }
	public void setJob_group(String job_group) {
		if(job_group==null) throw new BusinessException("Entity : CollectCase.job_group must not null!");
		this.job_group = job_group;
	}

	public BigDecimal getColect_record() { return colect_record; }
	public void setColect_record(BigDecimal colect_record) {
		if(colect_record==null) throw new BusinessException("Entity : CollectCase.colect_record must not null!");
		this.colect_record = colect_record;
	}

	public String getCollect_e_time() { return collect_e_time; }
	public void setCollect_e_time(String collect_e_time) {
		if(collect_e_time==null) addNullValueField("collect_e_time");
		this.collect_e_time = collect_e_time;
	}

	public String getExecute_length() { return execute_length; }
	public void setExecute_length(String execute_length) {
		if(execute_length==null) addNullValueField("execute_length");
		this.execute_length = execute_length;
	}

	public BigDecimal getCollect_total() { return collect_total; }
	public void setCollect_total(BigDecimal collect_total) {
		if(collect_total==null) addNullValueField("collect_total");
		this.collect_total = collect_total;
	}

	public BigDecimal getAgain_num() { return again_num; }
	public void setAgain_num(BigDecimal again_num) {
		if(again_num==null) addNullValueField("again_num");
		this.again_num = again_num;
	}

	public String getTable_name() { return table_name; }
	public void setTable_name(String table_name) {
		if(table_name==null) addNullValueField("table_name");
		this.table_name = table_name;
	}

	public String getEtl_date() { return etl_date; }
	public void setEtl_date(String etl_date) {
		if(etl_date==null) addNullValueField("etl_date");
		this.etl_date = etl_date;
	}

	public String getCollect_e_date() { return collect_e_date; }
	public void setCollect_e_date(String collect_e_date) {
		if(collect_e_date==null) addNullValueField("collect_e_date");
		this.collect_e_date = collect_e_date;
	}

	public String getCollect_type() { return collect_type; }
	public void setCollect_type(String collect_type) {
		if(collect_type==null) throw new BusinessException("Entity : CollectCase.collect_type must not null!");
		this.collect_type = collect_type;
	}

	public String getCollect_s_time() { return collect_s_time; }
	public void setCollect_s_time(String collect_s_time) {
		if(collect_s_time==null) throw new BusinessException("Entity : CollectCase.collect_s_time must not null!");
		this.collect_s_time = collect_s_time;
	}

	public String getCc_remark() { return cc_remark; }
	public void setCc_remark(String cc_remark) {
		if(cc_remark==null) addNullValueField("cc_remark");
		this.cc_remark = cc_remark;
	}

	public BigDecimal getSource_id() { return source_id; }
	public void setSource_id(BigDecimal source_id) {
		if(source_id==null) throw new BusinessException("Entity : CollectCase.source_id must not null!");
		this.source_id = source_id;
	}

	public String getCollect_s_date() { return collect_s_date; }
	public void setCollect_s_date(String collect_s_date) {
		if(collect_s_date==null) throw new BusinessException("Entity : CollectCase.collect_s_date must not null!");
		this.collect_s_date = collect_s_date;
	}

}