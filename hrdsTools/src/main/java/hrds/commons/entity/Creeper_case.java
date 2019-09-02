package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.commons.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 爬虫Agent结果情况
 */
@Table(tableName = "creeper_case")
public class Creeper_case extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "creeper_case";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 爬虫Agent结果情况 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cc_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long cc_id; //爬虫情况ID
	private Long agent_id; //Agent_id
	private String start_date; //开始日期
	private String start_time; //开始时间
	private String end_date; //结束日期
	private String end_time; //结束时间
	private Long grab_count; //抓取网页数
	private String execute_length; //运行时长
	private Long download_page; //下载网页数
	private Long threads_count; //启动线程数
	private String cc_remark; //备注
	private String is_grabsucceed; //是否成功抓取
	private String execute_state; //运行状态
	private String start_upload_date; //开始上传日期
	private String start_upload_time; //开始上传时间
	private Long already_count; //已经爬取站点数

	/** 取得：爬虫情况ID */
	public Long getCc_id(){
		return cc_id;
	}
	/** 设置：爬虫情况ID */
	public void setCc_id(Long cc_id){
		this.cc_id=cc_id;
	}
	/** 设置：爬虫情况ID */
	public void setCc_id(String cc_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(cc_id)){
			this.cc_id=new Long(cc_id);
		}
	}
	/** 取得：Agent_id */
	public Long getAgent_id(){
		return agent_id;
	}
	/** 设置：Agent_id */
	public void setAgent_id(Long agent_id){
		this.agent_id=agent_id;
	}
	/** 设置：Agent_id */
	public void setAgent_id(String agent_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(agent_id)){
			this.agent_id=new Long(agent_id);
		}
	}
	/** 取得：开始日期 */
	public String getStart_date(){
		return start_date;
	}
	/** 设置：开始日期 */
	public void setStart_date(String start_date){
		this.start_date=start_date;
	}
	/** 取得：开始时间 */
	public String getStart_time(){
		return start_time;
	}
	/** 设置：开始时间 */
	public void setStart_time(String start_time){
		this.start_time=start_time;
	}
	/** 取得：结束日期 */
	public String getEnd_date(){
		return end_date;
	}
	/** 设置：结束日期 */
	public void setEnd_date(String end_date){
		this.end_date=end_date;
	}
	/** 取得：结束时间 */
	public String getEnd_time(){
		return end_time;
	}
	/** 设置：结束时间 */
	public void setEnd_time(String end_time){
		this.end_time=end_time;
	}
	/** 取得：抓取网页数 */
	public Long getGrab_count(){
		return grab_count;
	}
	/** 设置：抓取网页数 */
	public void setGrab_count(Long grab_count){
		this.grab_count=grab_count;
	}
	/** 设置：抓取网页数 */
	public void setGrab_count(String grab_count){
		if(!fd.ng.core.utils.StringUtil.isEmpty(grab_count)){
			this.grab_count=new Long(grab_count);
		}
	}
	/** 取得：运行时长 */
	public String getExecute_length(){
		return execute_length;
	}
	/** 设置：运行时长 */
	public void setExecute_length(String execute_length){
		this.execute_length=execute_length;
	}
	/** 取得：下载网页数 */
	public Long getDownload_page(){
		return download_page;
	}
	/** 设置：下载网页数 */
	public void setDownload_page(Long download_page){
		this.download_page=download_page;
	}
	/** 设置：下载网页数 */
	public void setDownload_page(String download_page){
		if(!fd.ng.core.utils.StringUtil.isEmpty(download_page)){
			this.download_page=new Long(download_page);
		}
	}
	/** 取得：启动线程数 */
	public Long getThreads_count(){
		return threads_count;
	}
	/** 设置：启动线程数 */
	public void setThreads_count(Long threads_count){
		this.threads_count=threads_count;
	}
	/** 设置：启动线程数 */
	public void setThreads_count(String threads_count){
		if(!fd.ng.core.utils.StringUtil.isEmpty(threads_count)){
			this.threads_count=new Long(threads_count);
		}
	}
	/** 取得：备注 */
	public String getCc_remark(){
		return cc_remark;
	}
	/** 设置：备注 */
	public void setCc_remark(String cc_remark){
		this.cc_remark=cc_remark;
	}
	/** 取得：是否成功抓取 */
	public String getIs_grabsucceed(){
		return is_grabsucceed;
	}
	/** 设置：是否成功抓取 */
	public void setIs_grabsucceed(String is_grabsucceed){
		this.is_grabsucceed=is_grabsucceed;
	}
	/** 取得：运行状态 */
	public String getExecute_state(){
		return execute_state;
	}
	/** 设置：运行状态 */
	public void setExecute_state(String execute_state){
		this.execute_state=execute_state;
	}
	/** 取得：开始上传日期 */
	public String getStart_upload_date(){
		return start_upload_date;
	}
	/** 设置：开始上传日期 */
	public void setStart_upload_date(String start_upload_date){
		this.start_upload_date=start_upload_date;
	}
	/** 取得：开始上传时间 */
	public String getStart_upload_time(){
		return start_upload_time;
	}
	/** 设置：开始上传时间 */
	public void setStart_upload_time(String start_upload_time){
		this.start_upload_time=start_upload_time;
	}
	/** 取得：已经爬取站点数 */
	public Long getAlready_count(){
		return already_count;
	}
	/** 设置：已经爬取站点数 */
	public void setAlready_count(Long already_count){
		this.already_count=already_count;
	}
	/** 设置：已经爬取站点数 */
	public void setAlready_count(String already_count){
		if(!fd.ng.core.utils.StringUtil.isEmpty(already_count)){
			this.already_count=new Long(already_count);
		}
	}
}
