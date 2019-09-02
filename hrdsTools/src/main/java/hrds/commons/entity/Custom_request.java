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
 * 定制爬取要求表
 */
@Table(tableName = "custom_request")
public class Custom_request extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "custom_request";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 定制爬取要求表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cr_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String fre_week; //周
	private Long cr_id; //定制爬取要求id
	private String fre_month; //月
	private String fre_day; //天
	private String execute_time; //执行时间
	private String cron_expression; //quartz执行表达式
	private String start_date; //开始日期
	private String end_date; //结束日期
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String url; //URL地址
	private Long cs_id; //爬虫数据源id
	private Long create_id; //用户ID

	/** 取得：周 */
	public String getFre_week(){
		return fre_week;
	}
	/** 设置：周 */
	public void setFre_week(String fre_week){
		this.fre_week=fre_week;
	}
	/** 取得：定制爬取要求id */
	public Long getCr_id(){
		return cr_id;
	}
	/** 设置：定制爬取要求id */
	public void setCr_id(Long cr_id){
		this.cr_id=cr_id;
	}
	/** 设置：定制爬取要求id */
	public void setCr_id(String cr_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(cr_id)){
			this.cr_id=new Long(cr_id);
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
	/** 取得：URL地址 */
	public String getUrl(){
		return url;
	}
	/** 设置：URL地址 */
	public void setUrl(String url){
		this.url=url;
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
