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
@Table(tableName = "etl_job_def")
public class EtlJobDef extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etl_job_def";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("etl_job");
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
	private String job_process_id;
	private Integer com_exe_num;
	private String job_eff_flag;
	private Integer disp_offset;
	private Integer exe_num;
	private String pro_para;
	private Integer overtime_val;
	private String disp_time;
	private String upd_time;
	private String curr_st_time;
	private String log_dic;
	private String job_disp_status;
	private String comments;
	private String disp_type;
	private Integer job_priority;
	private String star_time;
	private String pro_dic;
	private Integer overlength_val;
	private String end_time;
	private String pro_type;
	private String today_disp;
	private BigDecimal exe_frequency;
	private Integer job_priority_curr;
	private Integer job_return_val;
	private String last_exe_time;
	private String sub_sys_cd;
	private String etl_sys_cd;
	private String etl_job_desc;
	private String disp_freq;
	private String curr_end_time;
	private String curr_bath_date;
	private String etl_job;
	private String pro_name;

	public String getMain_serv_sync() { return main_serv_sync; }
	public void setMain_serv_sync(String main_serv_sync) {
		if(main_serv_sync==null) addNullValueField("main_serv_sync");
		this.main_serv_sync = main_serv_sync;
	}

	public String getJob_process_id() { return job_process_id; }
	public void setJob_process_id(String job_process_id) {
		if(job_process_id==null) addNullValueField("job_process_id");
		this.job_process_id = job_process_id;
	}

	public Integer getCom_exe_num() { return com_exe_num; }
	public void setCom_exe_num(Integer com_exe_num) {
		if(com_exe_num==null) addNullValueField("com_exe_num");
		this.com_exe_num = com_exe_num;
	}

	public String getJob_eff_flag() { return job_eff_flag; }
	public void setJob_eff_flag(String job_eff_flag) {
		if(job_eff_flag==null) addNullValueField("job_eff_flag");
		this.job_eff_flag = job_eff_flag;
	}

	public Integer getDisp_offset() { return disp_offset; }
	public void setDisp_offset(Integer disp_offset) {
		if(disp_offset==null) addNullValueField("disp_offset");
		this.disp_offset = disp_offset;
	}

	public Integer getExe_num() { return exe_num; }
	public void setExe_num(Integer exe_num) {
		if(exe_num==null) addNullValueField("exe_num");
		this.exe_num = exe_num;
	}

	public String getPro_para() { return pro_para; }
	public void setPro_para(String pro_para) {
		if(pro_para==null) addNullValueField("pro_para");
		this.pro_para = pro_para;
	}

	public Integer getOvertime_val() { return overtime_val; }
	public void setOvertime_val(Integer overtime_val) {
		if(overtime_val==null) addNullValueField("overtime_val");
		this.overtime_val = overtime_val;
	}

	public String getDisp_time() { return disp_time; }
	public void setDisp_time(String disp_time) {
		if(disp_time==null) addNullValueField("disp_time");
		this.disp_time = disp_time;
	}

	public String getUpd_time() { return upd_time; }
	public void setUpd_time(String upd_time) {
		if(upd_time==null) addNullValueField("upd_time");
		this.upd_time = upd_time;
	}

	public String getCurr_st_time() { return curr_st_time; }
	public void setCurr_st_time(String curr_st_time) {
		if(curr_st_time==null) addNullValueField("curr_st_time");
		this.curr_st_time = curr_st_time;
	}

	public String getLog_dic() { return log_dic; }
	public void setLog_dic(String log_dic) {
		if(log_dic==null) addNullValueField("log_dic");
		this.log_dic = log_dic;
	}

	public String getJob_disp_status() { return job_disp_status; }
	public void setJob_disp_status(String job_disp_status) {
		if(job_disp_status==null) addNullValueField("job_disp_status");
		this.job_disp_status = job_disp_status;
	}

	public String getComments() { return comments; }
	public void setComments(String comments) {
		if(comments==null) addNullValueField("comments");
		this.comments = comments;
	}

	public String getDisp_type() { return disp_type; }
	public void setDisp_type(String disp_type) {
		if(disp_type==null) addNullValueField("disp_type");
		this.disp_type = disp_type;
	}

	public Integer getJob_priority() { return job_priority; }
	public void setJob_priority(Integer job_priority) {
		if(job_priority==null) addNullValueField("job_priority");
		this.job_priority = job_priority;
	}

	public String getStar_time() { return star_time; }
	public void setStar_time(String star_time) {
		if(star_time==null) addNullValueField("star_time");
		this.star_time = star_time;
	}

	public String getPro_dic() { return pro_dic; }
	public void setPro_dic(String pro_dic) {
		if(pro_dic==null) addNullValueField("pro_dic");
		this.pro_dic = pro_dic;
	}

	public Integer getOverlength_val() { return overlength_val; }
	public void setOverlength_val(Integer overlength_val) {
		if(overlength_val==null) addNullValueField("overlength_val");
		this.overlength_val = overlength_val;
	}

	public String getEnd_time() { return end_time; }
	public void setEnd_time(String end_time) {
		if(end_time==null) addNullValueField("end_time");
		this.end_time = end_time;
	}

	public String getPro_type() { return pro_type; }
	public void setPro_type(String pro_type) {
		if(pro_type==null) throw new BusinessException("Entity : EtlJobDef.pro_type must not null!");
		this.pro_type = pro_type;
	}

	public String getToday_disp() { return today_disp; }
	public void setToday_disp(String today_disp) {
		if(today_disp==null) addNullValueField("today_disp");
		this.today_disp = today_disp;
	}

	public BigDecimal getExe_frequency() { return exe_frequency; }
	public void setExe_frequency(BigDecimal exe_frequency) {
		if(exe_frequency==null) addNullValueField("exe_frequency");
		this.exe_frequency = exe_frequency;
	}

	public Integer getJob_priority_curr() { return job_priority_curr; }
	public void setJob_priority_curr(Integer job_priority_curr) {
		if(job_priority_curr==null) addNullValueField("job_priority_curr");
		this.job_priority_curr = job_priority_curr;
	}

	public Integer getJob_return_val() { return job_return_val; }
	public void setJob_return_val(Integer job_return_val) {
		if(job_return_val==null) addNullValueField("job_return_val");
		this.job_return_val = job_return_val;
	}

	public String getLast_exe_time() { return last_exe_time; }
	public void setLast_exe_time(String last_exe_time) {
		if(last_exe_time==null) addNullValueField("last_exe_time");
		this.last_exe_time = last_exe_time;
	}

	public String getSub_sys_cd() { return sub_sys_cd; }
	public void setSub_sys_cd(String sub_sys_cd) {
		if(sub_sys_cd==null) throw new BusinessException("Entity : EtlJobDef.sub_sys_cd must not null!");
		this.sub_sys_cd = sub_sys_cd;
	}

	public String getEtl_sys_cd() { return etl_sys_cd; }
	public void setEtl_sys_cd(String etl_sys_cd) {
		if(etl_sys_cd==null) throw new BusinessException("Entity : EtlJobDef.etl_sys_cd must not null!");
		this.etl_sys_cd = etl_sys_cd;
	}

	public String getEtl_job_desc() { return etl_job_desc; }
	public void setEtl_job_desc(String etl_job_desc) {
		if(etl_job_desc==null) addNullValueField("etl_job_desc");
		this.etl_job_desc = etl_job_desc;
	}

	public String getDisp_freq() { return disp_freq; }
	public void setDisp_freq(String disp_freq) {
		if(disp_freq==null) addNullValueField("disp_freq");
		this.disp_freq = disp_freq;
	}

	public String getCurr_end_time() { return curr_end_time; }
	public void setCurr_end_time(String curr_end_time) {
		if(curr_end_time==null) addNullValueField("curr_end_time");
		this.curr_end_time = curr_end_time;
	}

	public String getCurr_bath_date() { return curr_bath_date; }
	public void setCurr_bath_date(String curr_bath_date) {
		if(curr_bath_date==null) addNullValueField("curr_bath_date");
		this.curr_bath_date = curr_bath_date;
	}

	public String getEtl_job() { return etl_job; }
	public void setEtl_job(String etl_job) {
		if(etl_job==null) throw new BusinessException("Entity : EtlJobDef.etl_job must not null!");
		this.etl_job = etl_job;
	}

	public String getPro_name() { return pro_name; }
	public void setPro_name(String pro_name) {
		if(pro_name==null) addNullValueField("pro_name");
		this.pro_name = pro_name;
	}

}