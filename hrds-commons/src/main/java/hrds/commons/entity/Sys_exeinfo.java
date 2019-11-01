package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import hrds.commons.entity.fdentity.ProjectTableEntity;
import fd.ng.db.entity.anno.Table;
import fd.ng.core.annotation.DocBean;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * 系统采集作业结果表
 */
@Table(tableName = "sys_exeinfo")
public class Sys_exeinfo extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sys_exeinfo";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 系统采集作业结果表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("exe_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="exe_id",value="执行id:",dataType = Long.class,required = true)
	private Long exe_id;
	@DocBean(name ="job_tablename",value="作业表名:",dataType = String.class,required = false)
	private String job_tablename;
	@DocBean(name ="etl_date",value="执行日期:",dataType = String.class,required = true)
	private String etl_date;
	@DocBean(name ="execute_state",value="运行状态(ExecuteState):01-开始运行<KaiShiYunXing> 02-运行完成<YunXingWanCheng> 99-运行失败<YunXingShiBai> 20-通知成功<TongZhiChengGong> 21-通知失败<TongZhiShiBai> 30-暂停运行<ZanTingYunXing> ",dataType = String.class,required = true)
	private String execute_state;
	@DocBean(name ="job_name",value="作业名称名称:",dataType = String.class,required = true)
	private String job_name;
	@DocBean(name ="exe_parameter",value="参数:",dataType = String.class,required = true)
	private String exe_parameter;
	@DocBean(name ="err_info",value="错误信息:",dataType = String.class,required = true)
	private String err_info;
	@DocBean(name ="st_date",value="开始日期:",dataType = String.class,required = true)
	private String st_date;
	@DocBean(name ="ed_date",value="结束日期:",dataType = String.class,required = true)
	private String ed_date;
	@DocBean(name ="is_valid",value="作业是否有效(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_valid;
	@DocBean(name ="source_id",value="数据源ID:",dataType = Long.class,required = true)
	private Long source_id;
	@DocBean(name ="agent_id",value="Agent_id:",dataType = Long.class,required = true)
	private Long agent_id;
	@DocBean(name ="database_id",value="数据库设置id:",dataType = Long.class,required = true)
	private Long database_id;

	/** 取得：执行id */
	public Long getExe_id(){
		return exe_id;
	}
	/** 设置：执行id */
	public void setExe_id(Long exe_id){
		this.exe_id=exe_id;
	}
	/** 设置：执行id */
	public void setExe_id(String exe_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(exe_id)){
			this.exe_id=new Long(exe_id);
		}
	}
	/** 取得：作业表名 */
	public String getJob_tablename(){
		return job_tablename;
	}
	/** 设置：作业表名 */
	public void setJob_tablename(String job_tablename){
		this.job_tablename=job_tablename;
	}
	/** 取得：执行日期 */
	public String getEtl_date(){
		return etl_date;
	}
	/** 设置：执行日期 */
	public void setEtl_date(String etl_date){
		this.etl_date=etl_date;
	}
	/** 取得：运行状态 */
	public String getExecute_state(){
		return execute_state;
	}
	/** 设置：运行状态 */
	public void setExecute_state(String execute_state){
		this.execute_state=execute_state;
	}
	/** 取得：作业名称名称 */
	public String getJob_name(){
		return job_name;
	}
	/** 设置：作业名称名称 */
	public void setJob_name(String job_name){
		this.job_name=job_name;
	}
	/** 取得：参数 */
	public String getExe_parameter(){
		return exe_parameter;
	}
	/** 设置：参数 */
	public void setExe_parameter(String exe_parameter){
		this.exe_parameter=exe_parameter;
	}
	/** 取得：错误信息 */
	public String getErr_info(){
		return err_info;
	}
	/** 设置：错误信息 */
	public void setErr_info(String err_info){
		this.err_info=err_info;
	}
	/** 取得：开始日期 */
	public String getSt_date(){
		return st_date;
	}
	/** 设置：开始日期 */
	public void setSt_date(String st_date){
		this.st_date=st_date;
	}
	/** 取得：结束日期 */
	public String getEd_date(){
		return ed_date;
	}
	/** 设置：结束日期 */
	public void setEd_date(String ed_date){
		this.ed_date=ed_date;
	}
	/** 取得：作业是否有效 */
	public String getIs_valid(){
		return is_valid;
	}
	/** 设置：作业是否有效 */
	public void setIs_valid(String is_valid){
		this.is_valid=is_valid;
	}
	/** 取得：数据源ID */
	public Long getSource_id(){
		return source_id;
	}
	/** 设置：数据源ID */
	public void setSource_id(Long source_id){
		this.source_id=source_id;
	}
	/** 设置：数据源ID */
	public void setSource_id(String source_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(source_id)){
			this.source_id=new Long(source_id);
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
	/** 取得：数据库设置id */
	public Long getDatabase_id(){
		return database_id;
	}
	/** 设置：数据库设置id */
	public void setDatabase_id(Long database_id){
		this.database_id=database_id;
	}
	/** 设置：数据库设置id */
	public void setDatabase_id(String database_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(database_id)){
			this.database_id=new Long(database_id);
		}
	}
}
