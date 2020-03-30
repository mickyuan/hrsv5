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
 * 无效表列信息
 */
@Table(tableName = "dq_failure_column")
public class Dq_failure_column extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_failure_column";
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
	@DocBean(name ="failure_column_id",value="列id:",dataType = Long.class,required = true)
	private Long failure_column_id;
	@DocBean(name ="column_source",value="字段来源(DataSourceType):ISL-贴源层_01<ISL> DCL-贴源层<DCL> DPL-加工层<DPL> DML-集市层<DML> SFL-系统层<SFL> AML-AI模型层<AML> DQC-管控层<DQC> UDL-自定义层<UDL> ",dataType = String.class,required = true)
	private String column_source;
	@DocBean(name ="column_meta_info",value="字段元信息:",dataType = String.class,required = true)
	private String column_meta_info;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="failure_table_id",value="表id:",dataType = Long.class,required = true)
	private Long failure_table_id;

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
