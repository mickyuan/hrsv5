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
 * 数据质量指标3数据记录表
 */
@Table(tableName = "dq_index3record")
public class Dq_index3record extends ProjectTableEntity
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
	@DocBean(name ="record_id",value="记录编号:",dataType = Long.class,required = true)
	private Long record_id;
	@DocBean(name ="record_date",value="记录日期:",dataType = String.class,required = true)
	private String record_date;
	@DocBean(name ="record_time",value="记录时间:",dataType = String.class,required = true)
	private String record_time;
	@DocBean(name ="table_name",value="数据表名:",dataType = String.class,required = true)
	private String table_name;
	@DocBean(name ="table_size",value="数据表大小:",dataType = BigDecimal.class,required = false)
	private BigDecimal table_size;
	@DocBean(name ="file_type",value="数据物理文件类型(HdfsFileType):1-csv<Csv> 2-parquet<Parquet> 3-avro<Avro> 4-orcfile<OrcFile> 5-sequencefile<SequenceFile> 6-其他<Other> ",dataType = String.class,required = false)
	private String file_type;
	@DocBean(name ="file_path",value="数据物理文件路径:",dataType = String.class,required = false)
	private String file_path;
	@DocBean(name ="table_col",value="数据表字段:",dataType = String.class,required = false)
	private String table_col;
	@DocBean(name ="dqc_ts",value="表空间名:",dataType = String.class,required = false)
	private String dqc_ts;
	@DocBean(name ="task_id",value="任务编号:",dataType = Long.class,required = true)
	private Long task_id;
	@DocBean(name ="dsl_id",value="存储层配置ID:",dataType = Long.class,required = true)
	private Long dsl_id;

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
	/** 取得：存储层配置ID */
	public Long getDsl_id(){
		return dsl_id;
	}
	/** 设置：存储层配置ID */
	public void setDsl_id(Long dsl_id){
		this.dsl_id=dsl_id;
	}
	/** 设置：存储层配置ID */
	public void setDsl_id(String dsl_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dsl_id)){
			this.dsl_id=new Long(dsl_id);
		}
	}
}
