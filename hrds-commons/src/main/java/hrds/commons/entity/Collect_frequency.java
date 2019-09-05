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
 * 卸数作业参数表
 */
@Table(tableName = "collect_frequency")
public class Collect_frequency extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "collect_frequency";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 卸数作业参数表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cf_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long cf_id; //采集频率信息
	private String fre_week; //周
	private String fre_day; //天
	private String file_path; //文件存储路径
	private String run_way; //启动方式
	private String execute_time; //执行时间
	private String start_date; //开始日期
	private String end_date; //结束日期
	private String agent_date; //数据采集服务器日期
	private String agent_time; //数据采集服务器时间
	private String create_date; //创建日期
	private String create_time; //创建时间
	private Long collect_set_id; //数据库设置id
	private String collect_type; //采集类型
	private Long agent_id; //Agent_id
	private String fre_month; //月
	private String cron_expression; //quartz执行表达式
	private String comp_id; //组件编号
	private Long rely_job_id; //依赖作业id
	private String cf_jobnum; //自定义作业编号

	/** 取得：采集频率信息 */
	public Long getCf_id(){
		return cf_id;
	}
	/** 设置：采集频率信息 */
	public void setCf_id(Long cf_id){
		this.cf_id=cf_id;
	}
	/** 设置：采集频率信息 */
	public void setCf_id(String cf_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(cf_id)){
			this.cf_id=new Long(cf_id);
		}
	}
	/** 取得：周 */
	public String getFre_week(){
		return fre_week;
	}
	/** 设置：周 */
	public void setFre_week(String fre_week){
		this.fre_week=fre_week;
	}
	/** 取得：天 */
	public String getFre_day(){
		return fre_day;
	}
	/** 设置：天 */
	public void setFre_day(String fre_day){
		this.fre_day=fre_day;
	}
	/** 取得：文件存储路径 */
	public String getFile_path(){
		return file_path;
	}
	/** 设置：文件存储路径 */
	public void setFile_path(String file_path){
		this.file_path=file_path;
	}
	/** 取得：启动方式 */
	public String getRun_way(){
		return run_way;
	}
	/** 设置：启动方式 */
	public void setRun_way(String run_way){
		this.run_way=run_way;
	}
	/** 取得：执行时间 */
	public String getExecute_time(){
		return execute_time;
	}
	/** 设置：执行时间 */
	public void setExecute_time(String execute_time){
		this.execute_time=execute_time;
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
	/** 取得：数据采集服务器日期 */
	public String getAgent_date(){
		return agent_date;
	}
	/** 设置：数据采集服务器日期 */
	public void setAgent_date(String agent_date){
		this.agent_date=agent_date;
	}
	/** 取得：数据采集服务器时间 */
	public String getAgent_time(){
		return agent_time;
	}
	/** 设置：数据采集服务器时间 */
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
	/** 取得：数据库设置id */
	public Long getCollect_set_id(){
		return collect_set_id;
	}
	/** 设置：数据库设置id */
	public void setCollect_set_id(Long collect_set_id){
		this.collect_set_id=collect_set_id;
	}
	/** 设置：数据库设置id */
	public void setCollect_set_id(String collect_set_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(collect_set_id)){
			this.collect_set_id=new Long(collect_set_id);
		}
	}
	/** 取得：采集类型 */
	public String getCollect_type(){
		return collect_type;
	}
	/** 设置：采集类型 */
	public void setCollect_type(String collect_type){
		this.collect_type=collect_type;
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
	/** 取得：月 */
	public String getFre_month(){
		return fre_month;
	}
	/** 设置：月 */
	public void setFre_month(String fre_month){
		this.fre_month=fre_month;
	}
	/** 取得：quartz执行表达式 */
	public String getCron_expression(){
		return cron_expression;
	}
	/** 设置：quartz执行表达式 */
	public void setCron_expression(String cron_expression){
		this.cron_expression=cron_expression;
	}
	/** 取得：组件编号 */
	public String getComp_id(){
		return comp_id;
	}
	/** 设置：组件编号 */
	public void setComp_id(String comp_id){
		this.comp_id=comp_id;
	}
	/** 取得：依赖作业id */
	public Long getRely_job_id(){
		return rely_job_id;
	}
	/** 设置：依赖作业id */
	public void setRely_job_id(Long rely_job_id){
		this.rely_job_id=rely_job_id;
	}
	/** 设置：依赖作业id */
	public void setRely_job_id(String rely_job_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(rely_job_id)){
			this.rely_job_id=new Long(rely_job_id);
		}
	}
	/** 取得：自定义作业编号 */
	public String getCf_jobnum(){
		return cf_jobnum;
	}
	/** 设置：自定义作业编号 */
	public void setCf_jobnum(String cf_jobnum){
		this.cf_jobnum=cf_jobnum;
	}
}
