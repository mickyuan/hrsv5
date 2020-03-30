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
 * 外部检查申请执行日志
 */
@Table(tableName = "dq_exe_log")
public class Dq_exe_log extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_exe_log";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 外部检查申请执行日志 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("req_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="req_id",value="申请编号:",dataType = Long.class,required = true)
	private Long req_id;
	@DocBean(name ="dl_time",value="处理时间:",dataType = String.class,required = true)
	private String dl_time;
	@DocBean(name ="task_id",value="任务编号:",dataType = Long.class,required = true)
	private Long task_id;

	/** 取得：申请编号 */
	public Long getReq_id(){
		return req_id;
	}
	/** 设置：申请编号 */
	public void setReq_id(Long req_id){
		this.req_id=req_id;
	}
	/** 设置：申请编号 */
	public void setReq_id(String req_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(req_id)){
			this.req_id=new Long(req_id);
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
