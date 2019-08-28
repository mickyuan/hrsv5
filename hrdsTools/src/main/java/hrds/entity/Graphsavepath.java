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
 * 图作业保存路劲信息
 */
@Table(tableName = "graphsavepath")
public class Graphsavepath extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "graphsavepath";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 图作业保存路劲信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("graph_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long graph_id; //图作业ID
	private String job_storage_path; //hdfs存储路径
	private String remark; //备注
	private Long arithmetic_id; //图算法作业ID
	private String tablename; //结果表名
	private String table_space; //表空间

	/** 取得：图作业ID */
	public Long getGraph_id(){
		return graph_id;
	}
	/** 设置：图作业ID */
	public void setGraph_id(Long graph_id){
		this.graph_id=graph_id;
	}
	/** 设置：图作业ID */
	public void setGraph_id(String graph_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(graph_id)){
			this.graph_id=new Long(graph_id);
		}
	}
	/** 取得：hdfs存储路径 */
	public String getJob_storage_path(){
		return job_storage_path;
	}
	/** 设置：hdfs存储路径 */
	public void setJob_storage_path(String job_storage_path){
		this.job_storage_path=job_storage_path;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：图算法作业ID */
	public Long getArithmetic_id(){
		return arithmetic_id;
	}
	/** 设置：图算法作业ID */
	public void setArithmetic_id(Long arithmetic_id){
		this.arithmetic_id=arithmetic_id;
	}
	/** 设置：图算法作业ID */
	public void setArithmetic_id(String arithmetic_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(arithmetic_id)){
			this.arithmetic_id=new Long(arithmetic_id);
		}
	}
	/** 取得：结果表名 */
	public String getTablename(){
		return tablename;
	}
	/** 设置：结果表名 */
	public void setTablename(String tablename){
		this.tablename=tablename;
	}
	/** 取得：表空间 */
	public String getTable_space(){
		return table_space;
	}
	/** 设置：表空间 */
	public void setTable_space(String table_space){
		this.table_space=table_space;
	}
}
