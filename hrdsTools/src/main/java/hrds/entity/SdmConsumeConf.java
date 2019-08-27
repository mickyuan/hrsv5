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
@Table(tableName = "sdm_consume_conf")
public class SdmConsumeConf extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_consume_conf";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sdm_consum_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private BigDecimal run_time_long;
	private String time_type;
	private String create_time;
	private String consum_thread_cycle;
	private String end_type;
	private String sdm_cons_name;
	private BigDecimal sdm_consum_id;
	private String con_with_par;
	private String consumer_type;
	private String remark;
	private BigDecimal user_id;
	private BigDecimal data_volume;
	private String sdm_cons_describe;
	private String create_date;
	private String deadline;

	public BigDecimal getRun_time_long() { return run_time_long; }
	public void setRun_time_long(BigDecimal run_time_long) {
		if(run_time_long==null) addNullValueField("run_time_long");
		this.run_time_long = run_time_long;
	}

	public String getTime_type() { return time_type; }
	public void setTime_type(String time_type) {
		if(time_type==null) addNullValueField("time_type");
		this.time_type = time_type;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : SdmConsumeConf.create_time must not null!");
		this.create_time = create_time;
	}

	public String getConsum_thread_cycle() { return consum_thread_cycle; }
	public void setConsum_thread_cycle(String consum_thread_cycle) {
		if(consum_thread_cycle==null) addNullValueField("consum_thread_cycle");
		this.consum_thread_cycle = consum_thread_cycle;
	}

	public String getEnd_type() { return end_type; }
	public void setEnd_type(String end_type) {
		if(end_type==null) addNullValueField("end_type");
		this.end_type = end_type;
	}

	public String getSdm_cons_name() { return sdm_cons_name; }
	public void setSdm_cons_name(String sdm_cons_name) {
		if(sdm_cons_name==null) throw new BusinessException("Entity : SdmConsumeConf.sdm_cons_name must not null!");
		this.sdm_cons_name = sdm_cons_name;
	}

	public BigDecimal getSdm_consum_id() { return sdm_consum_id; }
	public void setSdm_consum_id(BigDecimal sdm_consum_id) {
		if(sdm_consum_id==null) throw new BusinessException("Entity : SdmConsumeConf.sdm_consum_id must not null!");
		this.sdm_consum_id = sdm_consum_id;
	}

	public String getCon_with_par() { return con_with_par; }
	public void setCon_with_par(String con_with_par) {
		if(con_with_par==null) throw new BusinessException("Entity : SdmConsumeConf.con_with_par must not null!");
		this.con_with_par = con_with_par;
	}

	public String getConsumer_type() { return consumer_type; }
	public void setConsumer_type(String consumer_type) {
		if(consumer_type==null) throw new BusinessException("Entity : SdmConsumeConf.consumer_type must not null!");
		this.consumer_type = consumer_type;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : SdmConsumeConf.user_id must not null!");
		this.user_id = user_id;
	}

	public BigDecimal getData_volume() { return data_volume; }
	public void setData_volume(BigDecimal data_volume) {
		if(data_volume==null) addNullValueField("data_volume");
		this.data_volume = data_volume;
	}

	public String getSdm_cons_describe() { return sdm_cons_describe; }
	public void setSdm_cons_describe(String sdm_cons_describe) {
		if(sdm_cons_describe==null) addNullValueField("sdm_cons_describe");
		this.sdm_cons_describe = sdm_cons_describe;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : SdmConsumeConf.create_date must not null!");
		this.create_date = create_date;
	}

	public String getDeadline() { return deadline; }
	public void setDeadline(String deadline) {
		if(deadline==null) addNullValueField("deadline");
		this.deadline = deadline;
	}

}