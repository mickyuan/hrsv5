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
 * 无效表信息
 */
@Table(tableName = "failure_table_info")
public class Failure_table_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "failure_table_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 无效表信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("failure_table_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long failure_table_id; //表id
	private String table_cn_name; //表中文名
	private String table_en_name; //表英文名
	private String table_source; //表来源
	private String table_meta_info; //表元信息
	private String remark; //备注

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
	/** 取得：表中文名 */
	public String getTable_cn_name(){
		return table_cn_name;
	}
	/** 设置：表中文名 */
	public void setTable_cn_name(String table_cn_name){
		this.table_cn_name=table_cn_name;
	}
	/** 取得：表英文名 */
	public String getTable_en_name(){
		return table_en_name;
	}
	/** 设置：表英文名 */
	public void setTable_en_name(String table_en_name){
		this.table_en_name=table_en_name;
	}
	/** 取得：表来源 */
	public String getTable_source(){
		return table_source;
	}
	/** 设置：表来源 */
	public void setTable_source(String table_source){
		this.table_source=table_source;
	}
	/** 取得：表元信息 */
	public String getTable_meta_info(){
		return table_meta_info;
	}
	/** 设置：表元信息 */
	public void setTable_meta_info(String table_meta_info){
		this.table_meta_info=table_meta_info;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
}
