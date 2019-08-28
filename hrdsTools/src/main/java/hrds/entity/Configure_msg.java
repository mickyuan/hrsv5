package hrds.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 抓取配置管理
 */
@Table(tableName = "configure_msg")
public class Configure_msg extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "configure_msg";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 抓取配置管理 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cmsg_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long cmsg_id; //配置ID
	private String files_type; //下载文件类型
	private String configure_name; //配置名称
	private String stop_conditions; //停止抓取条件
	private Long pages_count; //抓取批次（网页层数）
	private Long url_count; //总URL数据
	private BigDecimal download_size; //总共下载页面大小
	private String grab_time; //抓取停止时间
	private String fre_week; //周
	private String fre_month; //月
	private String fre_day; //天
	private String execute_time; //执行时间
	private String cron_expression; //quartz执行表达式
	private String start_date; //开始日期
	private String end_date; //结束日期
	private String agent_date; //agent日期
	private String agent_time; //agent时间
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String cmsg_remark; //备注
	private String is_custom; //是否需要自定义解析方法

	/** 取得：配置ID */
	public Long getCmsg_id(){
		return cmsg_id;
	}
	/** 设置：配置ID */
	public void setCmsg_id(Long cmsg_id){
		this.cmsg_id=cmsg_id;
	}
	/** 设置：配置ID */
	public void setCmsg_id(String cmsg_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(cmsg_id)){
			this.cmsg_id=new Long(cmsg_id);
		}
	}
	/** 取得：下载文件类型 */
	public String getFiles_type(){
		return files_type;
	}
	/** 设置：下载文件类型 */
	public void setFiles_type(String files_type){
		this.files_type=files_type;
	}
	/** 取得：配置名称 */
	public String getConfigure_name(){
		return configure_name;
	}
	/** 设置：配置名称 */
	public void setConfigure_name(String configure_name){
		this.configure_name=configure_name;
	}
	/** 取得：停止抓取条件 */
	public String getStop_conditions(){
		return stop_conditions;
	}
	/** 设置：停止抓取条件 */
	public void setStop_conditions(String stop_conditions){
		this.stop_conditions=stop_conditions;
	}
	/** 取得：抓取批次（网页层数） */
	public Long getPages_count(){
		return pages_count;
	}
	/** 设置：抓取批次（网页层数） */
	public void setPages_count(Long pages_count){
		this.pages_count=pages_count;
	}
	/** 设置：抓取批次（网页层数） */
	public void setPages_count(String pages_count){
		if(!fd.ng.core.utils.StringUtil.isEmpty(pages_count)){
			this.pages_count=new Long(pages_count);
		}
	}
	/** 取得：总URL数据 */
	public Long getUrl_count(){
		return url_count;
	}
	/** 设置：总URL数据 */
	public void setUrl_count(Long url_count){
		this.url_count=url_count;
	}
	/** 设置：总URL数据 */
	public void setUrl_count(String url_count){
		if(!fd.ng.core.utils.StringUtil.isEmpty(url_count)){
			this.url_count=new Long(url_count);
		}
	}
	/** 取得：总共下载页面大小 */
	public BigDecimal getDownload_size(){
		return download_size;
	}
	/** 设置：总共下载页面大小 */
	public void setDownload_size(BigDecimal download_size){
		this.download_size=download_size;
	}
	/** 设置：总共下载页面大小 */
	public void setDownload_size(String download_size){
		if(!fd.ng.core.utils.StringUtil.isEmpty(download_size)){
			this.download_size=new BigDecimal(download_size);
		}
	}
	/** 取得：抓取停止时间 */
	public String getGrab_time(){
		return grab_time;
	}
	/** 设置：抓取停止时间 */
	public void setGrab_time(String grab_time){
		this.grab_time=grab_time;
	}
	/** 取得：周 */
	public String getFre_week(){
		return fre_week;
	}
	/** 设置：周 */
	public void setFre_week(String fre_week){
		this.fre_week=fre_week;
	}
	/** 取得：月 */
	public String getFre_month(){
		return fre_month;
	}
	/** 设置：月 */
	public void setFre_month(String fre_month){
		this.fre_month=fre_month;
	}
	/** 取得：天 */
	public String getFre_day(){
		return fre_day;
	}
	/** 设置：天 */
	public void setFre_day(String fre_day){
		this.fre_day=fre_day;
	}
	/** 取得：执行时间 */
	public String getExecute_time(){
		return execute_time;
	}
	/** 设置：执行时间 */
	public void setExecute_time(String execute_time){
		this.execute_time=execute_time;
	}
	/** 取得：quartz执行表达式 */
	public String getCron_expression(){
		return cron_expression;
	}
	/** 设置：quartz执行表达式 */
	public void setCron_expression(String cron_expression){
		this.cron_expression=cron_expression;
	}
	/** 取得：开始日期 */
	public String getStart_date(){
		return start_date;
	}
	/** 设置：开始日期 */
	public void setStart_date(String start_date){
		this.start_date=start_date;
	}
	/** 取得：结束日期 */
	public String getEnd_date(){
		return end_date;
	}
	/** 设置：结束日期 */
	public void setEnd_date(String end_date){
		this.end_date=end_date;
	}
	/** 取得：agent日期 */
	public String getAgent_date(){
		return agent_date;
	}
	/** 设置：agent日期 */
	public void setAgent_date(String agent_date){
		this.agent_date=agent_date;
	}
	/** 取得：agent时间 */
	public String getAgent_time(){
		return agent_time;
	}
	/** 设置：agent时间 */
	public void setAgent_time(String agent_time){
		this.agent_time=agent_time;
	}
	/** 取得：创建日期 */
	public String getCreate_date(){
		return create_date;
	}
	/** 设置：创建日期 */
	public void setCreate_date(String create_date){
		this.create_date=create_date;
	}
	/** 取得：创建时间 */
	public String getCreate_time(){
		return create_time;
	}
	/** 设置：创建时间 */
	public void setCreate_time(String create_time){
		this.create_time=create_time;
	}
	/** 取得：备注 */
	public String getCmsg_remark(){
		return cmsg_remark;
	}
	/** 设置：备注 */
	public void setCmsg_remark(String cmsg_remark){
		this.cmsg_remark=cmsg_remark;
	}
	/** 取得：是否需要自定义解析方法 */
	public String getIs_custom(){
		return is_custom;
	}
	/** 设置：是否需要自定义解析方法 */
	public void setIs_custom(String is_custom){
		this.is_custom=is_custom;
	}
}
