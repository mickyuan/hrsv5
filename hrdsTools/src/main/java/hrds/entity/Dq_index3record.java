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
 * 数据质量指标3数据记录表
 */
@Table(tableName = "dq_index3record")
public class Dq_index3record extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_index3record";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据质量指标3数据记录表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("record_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long record_id; //记录编号
	private String record_date; //记录日期
	private String record_time; //记录时间
	private String table_name; //数据表名
	private BigDecimal table_size; //数据表大小
	private String file_type; //数据物理文件类型
	private String task_id; //任务编号
	private String file_path; //数据物理文件路径
	private String table_col; //数据表字段
	private String dqc_ts; //表空间名

	/** 取得：记录编号 */
	public Long getRecord_id(){
		return record_id;
	}
	/** 设置：记录编号 */
	public void setRecord_id(Long record_id){
		this.record_id=record_id;
	}
	/** 设置：记录编号 */
	public void setRecord_id(String record_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(record_id)){
			this.record_id=new Long(record_id);
		}
	}
	/** 取得：记录日期 */
	public String getRecord_date(){
		return record_date;
	}
	/** 设置：记录日期 */
	public void setRecord_date(String record_date){
		this.record_date=record_date;
	}
	/** 取得：记录时间 */
	public String getRecord_time(){
		return record_time;
	}
	/** 设置：记录时间 */
	public void setRecord_time(String record_time){
		this.record_time=record_time;
	}
	/** 取得：数据表名 */
	public String getTable_name(){
		return table_name;
	}
	/** 设置：数据表名 */
	public void setTable_name(String table_name){
		this.table_name=table_name;
	}
	/** 取得：数据表大小 */
	public BigDecimal getTable_size(){
		return table_size;
	}
	/** 设置：数据表大小 */
	public void setTable_size(BigDecimal table_size){
		this.table_size=table_size;
	}
	/** 设置：数据表大小 */
	public void setTable_size(String table_size){
		if(!fd.ng.core.utils.StringUtil.isEmpty(table_size)){
			this.table_size=new BigDecimal(table_size);
		}
	}
	/** 取得：数据物理文件类型 */
	public String getFile_type(){
		return file_type;
	}
	/** 设置：数据物理文件类型 */
	public void setFile_type(String file_type){
		this.file_type=file_type;
	}
	/** 取得：任务编号 */
	public String getTask_id(){
		return task_id;
	}
	/** 设置：任务编号 */
	public void setTask_id(String task_id){
		this.task_id=task_id;
	}
	/** 取得：数据物理文件路径 */
	public String getFile_path(){
		return file_path;
	}
	/** 设置：数据物理文件路径 */
	public void setFile_path(String file_path){
		this.file_path=file_path;
	}
	/** 取得：数据表字段 */
	public String getTable_col(){
		return table_col;
	}
	/** 设置：数据表字段 */
	public void setTable_col(String table_col){
		this.table_col=table_col;
	}
	/** 取得：表空间名 */
	public String getDqc_ts(){
		return dqc_ts;
	}
	/** 设置：表空间名 */
	public void setDqc_ts(String dqc_ts){
		this.dqc_ts=dqc_ts;
	}
}
