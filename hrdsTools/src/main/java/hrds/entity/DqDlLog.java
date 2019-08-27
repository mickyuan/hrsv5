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
@Table(tableName = "dq_dl_log")
public class DqDlLog extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_dl_log";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("task_id");
		__tmpPKS.add("dl_time");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String actn;
	private String dl_dscr;
	private BigDecimal reg_num;
	private BigDecimal user_id;
	private String fl_nm;
	private BigDecimal task_id;
	private String prcs_stt;
	private String is_top;
	private String dl_time;
	private byte[] attc;

	public String getActn() { return actn; }
	public void setActn(String actn) {
		if(actn==null) addNullValueField("actn");
		this.actn = actn;
	}

	public String getDl_dscr() { return dl_dscr; }
	public void setDl_dscr(String dl_dscr) {
		if(dl_dscr==null) addNullValueField("dl_dscr");
		this.dl_dscr = dl_dscr;
	}

	public BigDecimal getReg_num() { return reg_num; }
	public void setReg_num(BigDecimal reg_num) {
		if(reg_num==null) throw new BusinessException("Entity : DqDlLog.reg_num must not null!");
		this.reg_num = reg_num;
	}

	public BigDecimal getUser_id() { return user_id; }
	public void setUser_id(BigDecimal user_id) {
		if(user_id==null) throw new BusinessException("Entity : DqDlLog.user_id must not null!");
		this.user_id = user_id;
	}

	public String getFl_nm() { return fl_nm; }
	public void setFl_nm(String fl_nm) {
		if(fl_nm==null) addNullValueField("fl_nm");
		this.fl_nm = fl_nm;
	}

	public BigDecimal getTask_id() { return task_id; }
	public void setTask_id(BigDecimal task_id) {
		if(task_id==null) throw new BusinessException("Entity : DqDlLog.task_id must not null!");
		this.task_id = task_id;
	}

	public String getPrcs_stt() { return prcs_stt; }
	public void setPrcs_stt(String prcs_stt) {
		if(prcs_stt==null) addNullValueField("prcs_stt");
		this.prcs_stt = prcs_stt;
	}

	public String getIs_top() { return is_top; }
	public void setIs_top(String is_top) {
		if(is_top==null) addNullValueField("is_top");
		this.is_top = is_top;
	}

	public String getDl_time() { return dl_time; }
	public void setDl_time(String dl_time) {
		if(dl_time==null) throw new BusinessException("Entity : DqDlLog.dl_time must not null!");
		this.dl_time = dl_time;
	}

	public byte[] getAttc() { return attc; }
	public void setAttc(byte[] attc) {
		if(attc==null) addNullValueField("attc");
		this.attc = attc;
	}

}