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
 * 数据映射关系表
 */
@Table(tableName = "edw_mapping")
public class Edw_mapping extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_mapping";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据映射关系表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("jobname");
		__tmpPKS.add("tabname");
		__tmpPKS.add("colname");
		__tmpPKS.add("st_dt");
		__tmpPKS.add("st_time");
		__tmpPKS.add("mapping_job_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String jobname; //作业名称
	private String tabname; //模型表名称
	private String colname; //模型字段名称
	private String souretabname; //源表名称
	private String sourecolvalue; //源表字段名称
	private String mapping; //映射规则
	private String st_dt; //模型开始日期
	private String end_dt; //结束日期
	private String usercode; //用户编号
	private String st_time; //模型开始时间
	private String processtype; //处理方式
	private String remark; //备注
	private Long mapping_job_id; //映射作业ID
	private String tabalias; //表别名

	/** 取得：作业名称 */
	public String getJobname(){
		return jobname;
	}
	/** 设置：作业名称 */
	public void setJobname(String jobname){
		this.jobname=jobname;
	}
	/** 取得：模型表名称 */
	public String getTabname(){
		return tabname;
	}
	/** 设置：模型表名称 */
	public void setTabname(String tabname){
		this.tabname=tabname;
	}
	/** 取得：模型字段名称 */
	public String getColname(){
		return colname;
	}
	/** 设置：模型字段名称 */
	public void setColname(String colname){
		this.colname=colname;
	}
	/** 取得：源表名称 */
	public String getSouretabname(){
		return souretabname;
	}
	/** 设置：源表名称 */
	public void setSouretabname(String souretabname){
		this.souretabname=souretabname;
	}
	/** 取得：源表字段名称 */
	public String getSourecolvalue(){
		return sourecolvalue;
	}
	/** 设置：源表字段名称 */
	public void setSourecolvalue(String sourecolvalue){
		this.sourecolvalue=sourecolvalue;
	}
	/** 取得：映射规则 */
	public String getMapping(){
		return mapping;
	}
	/** 设置：映射规则 */
	public void setMapping(String mapping){
		this.mapping=mapping;
	}
	/** 取得：模型开始日期 */
	public String getSt_dt(){
		return st_dt;
	}
	/** 设置：模型开始日期 */
	public void setSt_dt(String st_dt){
		this.st_dt=st_dt;
	}
	/** 取得：结束日期 */
	public String getEnd_dt(){
		return end_dt;
	}
	/** 设置：结束日期 */
	public void setEnd_dt(String end_dt){
		this.end_dt=end_dt;
	}
	/** 取得：用户编号 */
	public String getUsercode(){
		return usercode;
	}
	/** 设置：用户编号 */
	public void setUsercode(String usercode){
		this.usercode=usercode;
	}
	/** 取得：模型开始时间 */
	public String getSt_time(){
		return st_time;
	}
	/** 设置：模型开始时间 */
	public void setSt_time(String st_time){
		this.st_time=st_time;
	}
	/** 取得：处理方式 */
	public String getProcesstype(){
		return processtype;
	}
	/** 设置：处理方式 */
	public void setProcesstype(String processtype){
		this.processtype=processtype;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：映射作业ID */
	public Long getMapping_job_id(){
		return mapping_job_id;
	}
	/** 设置：映射作业ID */
	public void setMapping_job_id(Long mapping_job_id){
		this.mapping_job_id=mapping_job_id;
	}
	/** 设置：映射作业ID */
	public void setMapping_job_id(String mapping_job_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(mapping_job_id)){
			this.mapping_job_id=new Long(mapping_job_id);
		}
	}
	/** 取得：表别名 */
	public String getTabalias(){
		return tabalias;
	}
	/** 设置：表别名 */
	public void setTabalias(String tabalias){
		this.tabalias=tabalias;
	}
}
