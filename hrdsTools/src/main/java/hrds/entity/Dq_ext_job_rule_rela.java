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
 * 外部检查作业与规则关系
 */
@Table(tableName = "dq_ext_job_rule_rela")
public class Dq_ext_job_rule_rela extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_ext_job_rule_rela";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 外部检查作业与规则关系 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("ext_job_id");
		__tmpPKS.add("task_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long ext_job_id; //外部作业编号
	private String dl_time; //处理时间
	private Long task_id; //任务编号

	/** 取得：外部作业编号 */
	public Long getExt_job_id(){
		return ext_job_id;
	}
	/** 设置：外部作业编号 */
	public void setExt_job_id(Long ext_job_id){
		this.ext_job_id=ext_job_id;
	}
	/** 设置：外部作业编号 */
	public void setExt_job_id(String ext_job_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(ext_job_id)){
			this.ext_job_id=new Long(ext_job_id);
		}
	}
	/** 取得：处理时间 */
	public String getDl_time(){
		return dl_time;
	}
	/** 设置：处理时间 */
	public void setDl_time(String dl_time){
		this.dl_time=dl_time;
	}
	/** 取得：任务编号 */
	public Long getTask_id(){
		return task_id;
	}
	/** 设置：任务编号 */
	public void setTask_id(Long task_id){
		this.task_id=task_id;
	}
	/** 设置：任务编号 */
	public void setTask_id(String task_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(task_id)){
			this.task_id=new Long(task_id);
		}
	}
}
