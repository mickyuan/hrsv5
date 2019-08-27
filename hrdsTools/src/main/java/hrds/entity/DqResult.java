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
@Table(tableName = "dq_result")
public class DqResult extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_result";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("task_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String end_date;
	private String dl_stat;
	private String verify_result;
	private String task_id;
	private String remark;
	private String is_saveindex3;
	private String is_saveindex2;
	private String is_saveindex1;
	private String target_key_fields;
	private Integer elapsed_ms;
	private String start_date;
	private String err_dtl_file_name;
	private String target_tab;
	private String errno;
	private BigDecimal reg_num;
	private String end_time;
	private String err_dtl_sql;
	private String case_type;
	private String verify_date;
	private String exec_mode;
	private String start_time;
	private String index_desc2;
	private String index_desc3;
	private Integer check_index2;
	private Integer check_index1;
	private String index_desc1;
	private String verify_sql;
	private String check_index3;

	public String getEnd_date() { return end_date; }
	public void setEnd_date(String end_date) {
		if(end_date==null) addNullValueField("end_date");
		this.end_date = end_date;
	}

	public String getDl_stat() { return dl_stat; }
	public void setDl_stat(String dl_stat) {
		if(dl_stat==null) addNullValueField("dl_stat");
		this.dl_stat = dl_stat;
	}

	public String getVerify_result() { return verify_result; }
	public void setVerify_result(String verify_result) {
		if(verify_result==null) addNullValueField("verify_result");
		this.verify_result = verify_result;
	}

	public String getTask_id() { return task_id; }
	public void setTask_id(String task_id) {
		if(task_id==null) throw new BusinessException("Entity : DqResult.task_id must not null!");
		this.task_id = task_id;
	}

	public String getRemark() { return remark; }
	public void setRemark(String remark) {
		if(remark==null) addNullValueField("remark");
		this.remark = remark;
	}

	public String getIs_saveindex3() { return is_saveindex3; }
	public void setIs_saveindex3(String is_saveindex3) {
		if(is_saveindex3==null) addNullValueField("is_saveindex3");
		this.is_saveindex3 = is_saveindex3;
	}

	public String getIs_saveindex2() { return is_saveindex2; }
	public void setIs_saveindex2(String is_saveindex2) {
		if(is_saveindex2==null) addNullValueField("is_saveindex2");
		this.is_saveindex2 = is_saveindex2;
	}

	public String getIs_saveindex1() { return is_saveindex1; }
	public void setIs_saveindex1(String is_saveindex1) {
		if(is_saveindex1==null) addNullValueField("is_saveindex1");
		this.is_saveindex1 = is_saveindex1;
	}

	public String getTarget_key_fields() { return target_key_fields; }
	public void setTarget_key_fields(String target_key_fields) {
		if(target_key_fields==null) addNullValueField("target_key_fields");
		this.target_key_fields = target_key_fields;
	}

	public Integer getElapsed_ms() { return elapsed_ms; }
	public void setElapsed_ms(Integer elapsed_ms) {
		if(elapsed_ms==null) addNullValueField("elapsed_ms");
		this.elapsed_ms = elapsed_ms;
	}

	public String getStart_date() { return start_date; }
	public void setStart_date(String start_date) {
		if(start_date==null) addNullValueField("start_date");
		this.start_date = start_date;
	}

	public String getErr_dtl_file_name() { return err_dtl_file_name; }
	public void setErr_dtl_file_name(String err_dtl_file_name) {
		if(err_dtl_file_name==null) addNullValueField("err_dtl_file_name");
		this.err_dtl_file_name = err_dtl_file_name;
	}

	public String getTarget_tab() { return target_tab; }
	public void setTarget_tab(String target_tab) {
		if(target_tab==null) addNullValueField("target_tab");
		this.target_tab = target_tab;
	}

	public String getErrno() { return errno; }
	public void setErrno(String errno) {
		if(errno==null) addNullValueField("errno");
		this.errno = errno;
	}

	public BigDecimal getReg_num() { return reg_num; }
	public void setReg_num(BigDecimal reg_num) {
		if(reg_num==null) throw new BusinessException("Entity : DqResult.reg_num must not null!");
		this.reg_num = reg_num;
	}

	public String getEnd_time() { return end_time; }
	public void setEnd_time(String end_time) {
		if(end_time==null) addNullValueField("end_time");
		this.end_time = end_time;
	}

	public String getErr_dtl_sql() { return err_dtl_sql; }
	public void setErr_dtl_sql(String err_dtl_sql) {
		if(err_dtl_sql==null) addNullValueField("err_dtl_sql");
		this.err_dtl_sql = err_dtl_sql;
	}

	public String getCase_type() { return case_type; }
	public void setCase_type(String case_type) {
		if(case_type==null) throw new BusinessException("Entity : DqResult.case_type must not null!");
		this.case_type = case_type;
	}

	public String getVerify_date() { return verify_date; }
	public void setVerify_date(String verify_date) {
		if(verify_date==null) addNullValueField("verify_date");
		this.verify_date = verify_date;
	}

	public String getExec_mode() { return exec_mode; }
	public void setExec_mode(String exec_mode) {
		if(exec_mode==null) addNullValueField("exec_mode");
		this.exec_mode = exec_mode;
	}

	public String getStart_time() { return start_time; }
	public void setStart_time(String start_time) {
		if(start_time==null) addNullValueField("start_time");
		this.start_time = start_time;
	}

	public String getIndex_desc2() { return index_desc2; }
	public void setIndex_desc2(String index_desc2) {
		if(index_desc2==null) addNullValueField("index_desc2");
		this.index_desc2 = index_desc2;
	}

	public String getIndex_desc3() { return index_desc3; }
	public void setIndex_desc3(String index_desc3) {
		if(index_desc3==null) addNullValueField("index_desc3");
		this.index_desc3 = index_desc3;
	}

	public Integer getCheck_index2() { return check_index2; }
	public void setCheck_index2(Integer check_index2) {
		if(check_index2==null) addNullValueField("check_index2");
		this.check_index2 = check_index2;
	}

	public Integer getCheck_index1() { return check_index1; }
	public void setCheck_index1(Integer check_index1) {
		if(check_index1==null) addNullValueField("check_index1");
		this.check_index1 = check_index1;
	}

	public String getIndex_desc1() { return index_desc1; }
	public void setIndex_desc1(String index_desc1) {
		if(index_desc1==null) addNullValueField("index_desc1");
		this.index_desc1 = index_desc1;
	}

	public String getVerify_sql() { return verify_sql; }
	public void setVerify_sql(String verify_sql) {
		if(verify_sql==null) addNullValueField("verify_sql");
		this.verify_sql = verify_sql;
	}

	public String getCheck_index3() { return check_index3; }
	public void setCheck_index3(String check_index3) {
		if(check_index3==null) addNullValueField("check_index3");
		this.check_index3 = check_index3;
	}

}