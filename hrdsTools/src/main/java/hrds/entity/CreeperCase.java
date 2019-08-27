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
@Table(tableName = "creeper_case")
public class CreeperCase extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "creeper_case";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cc_id");
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
	private BigDecimal grab_count;
	private BigDecimal download_page;
	private String start_upload_date;
	private BigDecimal agent_id;
	private String execute_state;
	private String end_time;
	private BigDecimal already_count;
	private String execute_length;
	private String is_grabsucceed;
	private String start_time;
	private BigDecimal threads_count;
	private String cc_remark;
	private String start_upload_time;
	private BigDecimal cc_id;
	private String start_date;

	public String getEnd_date() { return end_date; }
	public void setEnd_date(String end_date) {
		if(end_date==null) addNullValueField("end_date");
		this.end_date = end_date;
	}

	public BigDecimal getGrab_count() { return grab_count; }
	public void setGrab_count(BigDecimal grab_count) {
		if(grab_count==null) throw new BusinessException("Entity : CreeperCase.grab_count must not null!");
		this.grab_count = grab_count;
	}

	public BigDecimal getDownload_page() { return download_page; }
	public void setDownload_page(BigDecimal download_page) {
		if(download_page==null) throw new BusinessException("Entity : CreeperCase.download_page must not null!");
		this.download_page = download_page;
	}

	public String getStart_upload_date() { return start_upload_date; }
	public void setStart_upload_date(String start_upload_date) {
		if(start_upload_date==null) addNullValueField("start_upload_date");
		this.start_upload_date = start_upload_date;
	}

	public BigDecimal getAgent_id() { return agent_id; }
	public void setAgent_id(BigDecimal agent_id) {
		if(agent_id==null) throw new BusinessException("Entity : CreeperCase.agent_id must not null!");
		this.agent_id = agent_id;
	}

	public String getExecute_state() { return execute_state; }
	public void setExecute_state(String execute_state) {
		if(execute_state==null) throw new BusinessException("Entity : CreeperCase.execute_state must not null!");
		this.execute_state = execute_state;
	}

	public String getEnd_time() { return end_time; }
	public void setEnd_time(String end_time) {
		if(end_time==null) addNullValueField("end_time");
		this.end_time = end_time;
	}

	public BigDecimal getAlready_count() { return already_count; }
	public void setAlready_count(BigDecimal already_count) {
		if(already_count==null) throw new BusinessException("Entity : CreeperCase.already_count must not null!");
		this.already_count = already_count;
	}

	public String getExecute_length() { return execute_length; }
	public void setExecute_length(String execute_length) {
		if(execute_length==null) addNullValueField("execute_length");
		this.execute_length = execute_length;
	}

	public String getIs_grabsucceed() { return is_grabsucceed; }
	public void setIs_grabsucceed(String is_grabsucceed) {
		if(is_grabsucceed==null) throw new BusinessException("Entity : CreeperCase.is_grabsucceed must not null!");
		this.is_grabsucceed = is_grabsucceed;
	}

	public String getStart_time() { return start_time; }
	public void setStart_time(String start_time) {
		if(start_time==null) addNullValueField("start_time");
		this.start_time = start_time;
	}

	public BigDecimal getThreads_count() { return threads_count; }
	public void setThreads_count(BigDecimal threads_count) {
		if(threads_count==null) throw new BusinessException("Entity : CreeperCase.threads_count must not null!");
		this.threads_count = threads_count;
	}

	public String getCc_remark() { return cc_remark; }
	public void setCc_remark(String cc_remark) {
		if(cc_remark==null) addNullValueField("cc_remark");
		this.cc_remark = cc_remark;
	}

	public String getStart_upload_time() { return start_upload_time; }
	public void setStart_upload_time(String start_upload_time) {
		if(start_upload_time==null) addNullValueField("start_upload_time");
		this.start_upload_time = start_upload_time;
	}

	public BigDecimal getCc_id() { return cc_id; }
	public void setCc_id(BigDecimal cc_id) {
		if(cc_id==null) throw new BusinessException("Entity : CreeperCase.cc_id must not null!");
		this.cc_id = cc_id;
	}

	public String getStart_date() { return start_date; }
	public void setStart_date(String start_date) {
		if(start_date==null) addNullValueField("start_date");
		this.start_date = start_date;
	}

}