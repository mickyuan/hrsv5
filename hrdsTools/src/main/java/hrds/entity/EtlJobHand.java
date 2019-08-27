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
@Table(tableName = "etl_job_hand")
public class EtlJobHand extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etl_job_hand";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("etl_job");
		__tmpPKS.add("event_id");
		__tmpPKS.add("etl_sys_cd");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String main_serv_sync;
	private String pro_para;
	private String event_id;
	private String etl_sys_cd;
	private String st_time;
	private String hand_status;
	private String etl_hand_type;
	private String end_time;
	private String warning;
	private String etl_job;

	public String getMain_serv_sync() { return main_serv_sync; }
	public void setMain_serv_sync(String main_serv_sync) {
		if(main_serv_sync==null) addNullValueField("main_serv_sync");
		this.main_serv_sync = main_serv_sync;
	}

	public String getPro_para() { return pro_para; }
	public void setPro_para(String pro_para) {
		if(pro_para==null) addNullValueField("pro_para");
		this.pro_para = pro_para;
	}

	public String getEvent_id() { return event_id; }
	public void setEvent_id(String event_id) {
		if(event_id==null) throw new BusinessException("Entity : EtlJobHand.event_id must not null!");
		this.event_id = event_id;
	}

	public String getEtl_sys_cd() { return etl_sys_cd; }
	public void setEtl_sys_cd(String etl_sys_cd) {
		if(etl_sys_cd==null) throw new BusinessException("Entity : EtlJobHand.etl_sys_cd must not null!");
		this.etl_sys_cd = etl_sys_cd;
	}

	public String getSt_time() { return st_time; }
	public void setSt_time(String st_time) {
		if(st_time==null) addNullValueField("st_time");
		this.st_time = st_time;
	}

	public String getHand_status() { return hand_status; }
	public void setHand_status(String hand_status) {
		if(hand_status==null) addNullValueField("hand_status");
		this.hand_status = hand_status;
	}

	public String getEtl_hand_type() { return etl_hand_type; }
	public void setEtl_hand_type(String etl_hand_type) {
		if(etl_hand_type==null) addNullValueField("etl_hand_type");
		this.etl_hand_type = etl_hand_type;
	}

	public String getEnd_time() { return end_time; }
	public void setEnd_time(String end_time) {
		if(end_time==null) addNullValueField("end_time");
		this.end_time = end_time;
	}

	public String getWarning() { return warning; }
	public void setWarning(String warning) {
		if(warning==null) addNullValueField("warning");
		this.warning = warning;
	}

	public String getEtl_job() { return etl_job; }
	public void setEtl_job(String etl_job) {
		if(etl_job==null) throw new BusinessException("Entity : EtlJobHand.etl_job must not null!");
		this.etl_job = etl_job;
	}

}