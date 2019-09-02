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
 * 系统采集作业结果表
 */
@Table(tableName = "sys_exeinfo")
public class Sys_exeinfo extends TableEntity
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
	private Long exe_id; //执行id
	private String job_tablename; //作业表名
	private String etl_date; //执行日期
	private String execute_state; //运行状态
	private String job_name; //作业名称名称
	private Long database_id; //数据库设置id
	private Long agent_id; //Agent_id
	private Long source_id; //数据源ID
	private String exe_parameter; //参数
	private String err_info; //错误信息
	private String st_date; //开始日期
	private String ed_date; //结束日期
	private String is_valid; //作业是否有效

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
}
