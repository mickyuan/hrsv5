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
 * 爬虫数据源
 */
@Table(tableName = "creeper_source")
public class Creeper_source extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "creeper_source";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 爬虫数据源 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cs_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String fre_week; //周
	private Long cs_id; //爬虫数据源id
	private String fre_month; //月
	private String fre_day; //天
	private String execute_time; //执行时间
	private String cron_expression; //quartz执行表达式
	private String start_date; //开始日期
	private String end_date; //结束日期
	private String agent_date; //数据源日期
	private String agent_time; //数据源时间
	private String cs_remark; //备注
	private String create_date; //创建日期
	private String create_time; //创建时间
	private Long user_id; //用户ID
	private String source_status; //数据源状态
	private String source_ip; //数据源所在服务器IP
	private String source_port; //数据源服务器端口
	private String source_name; //数据源名称
	private String file_path; //文件存储路径
	private Long create_id; //用户ID

	/** 取得：周 */
	public String getFre_week(){
		return fre_week;
	}
	/** 设置：周 */
	public void setFre_week(String fre_week){
		this.fre_week=fre_week;
	}
	/** 取得：爬虫数据源id */
	public Long getCs_id(){
		return cs_id;
	}
	/** 设置：爬虫数据源id */
	public void setCs_id(Long cs_id){
		this.cs_id=cs_id;
	}
	/** 设置：爬虫数据源id */
	public void setCs_id(String cs_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(cs_id)){
			this.cs_id=new Long(cs_id);
		}
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
	/** 取得：数据源日期 */
	public String getAgent_date(){
		return agent_date;
	}
	/** 设置：数据源日期 */
	public void setAgent_date(String agent_date){
		this.agent_date=agent_date;
	}
	/** 取得：数据源时间 */
	public String getAgent_time(){
		return agent_time;
	}
	/** 设置：数据源时间 */
	public void setAgent_time(String agent_time){
		this.agent_time=agent_time;
	}
	/** 取得：备注 */
	public String getCs_remark(){
		return cs_remark;
	}
	/** 设置：备注 */
	public void setCs_remark(String cs_remark){
		this.cs_remark=cs_remark;
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
	/** 取得：用户ID */
	public Long getUser_id(){
		return user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(Long user_id){
		this.user_id=user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(String user_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(user_id)){
			this.user_id=new Long(user_id);
		}
	}
	/** 取得：数据源状态 */
	public String getSource_status(){
		return source_status;
	}
	/** 设置：数据源状态 */
	public void setSource_status(String source_status){
		this.source_status=source_status;
	}
	/** 取得：数据源所在服务器IP */
	public String getSource_ip(){
		return source_ip;
	}
	/** 设置：数据源所在服务器IP */
	public void setSource_ip(String source_ip){
		this.source_ip=source_ip;
	}
	/** 取得：数据源服务器端口 */
	public String getSource_port(){
		return source_port;
	}
	/** 设置：数据源服务器端口 */
	public void setSource_port(String source_port){
		this.source_port=source_port;
	}
	/** 取得：数据源名称 */
	public String getSource_name(){
		return source_name;
	}
	/** 设置：数据源名称 */
	public void setSource_name(String source_name){
		this.source_name=source_name;
	}
	/** 取得：文件存储路径 */
	public String getFile_path(){
		return file_path;
	}
	/** 设置：文件存储路径 */
	public void setFile_path(String file_path){
		this.file_path=file_path;
	}
	/** 取得：用户ID */
	public Long getCreate_id(){
		return create_id;
	}
	/** 设置：用户ID */
	public void setCreate_id(Long create_id){
		this.create_id=create_id;
	}
	/** 设置：用户ID */
	public void setCreate_id(String create_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(create_id)){
			this.create_id=new Long(create_id);
		}
	}
}
