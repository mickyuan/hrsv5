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
 * 无效表列信息
 */
@Table(tableName = "failure_column_info")
public class Failure_column_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "failure_column_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 无效表列信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("failure_column_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long failure_column_id; //列id
	private String column_source; //字段来源
	private String column_meta_info; //字段元信息
	private String remark; //备注
	private Long failure_table_id; //表id

	/** 取得：列id */
	public Long getFailure_column_id(){
		return failure_column_id;
	}
	/** 设置：列id */
	public void setFailure_column_id(Long failure_column_id){
		this.failure_column_id=failure_column_id;
	}
	/** 设置：列id */
	public void setFailure_column_id(String failure_column_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(failure_column_id)){
			this.failure_column_id=new Long(failure_column_id);
		}
	}
	/** 取得：字段来源 */
	public String getColumn_source(){
		return column_source;
	}
	/** 设置：字段来源 */
	public void setColumn_source(String column_source){
		this.column_source=column_source;
	}
	/** 取得：字段元信息 */
	public String getColumn_meta_info(){
		return column_meta_info;
	}
	/** 设置：字段元信息 */
	public void setColumn_meta_info(String column_meta_info){
		this.column_meta_info=column_meta_info;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：表id */
	public Long getFailure_table_id(){
		return failure_table_id;
	}
	/** 设置：表id */
	public void setFailure_table_id(Long failure_table_id){
		this.failure_table_id=failure_table_id;
	}
	/** 设置：表id */
	public void setFailure_table_id(String failure_table_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(failure_table_id)){
			this.failure_table_id=new Long(failure_table_id);
		}
	}
}
