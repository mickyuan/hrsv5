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
 * 结果映射信息表
 */
@Table(tableName = "dm_etlmap_info")
public class Dm_etlmap_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dm_etlmap_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 结果映射信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("etl_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="etl_id",value="表id:",dataType = Long.class,required = true)
	private Long etl_id;
	@DocBean(name ="targetfield_name",value="目标字段名称:",dataType = String.class,required = false)
	private String targetfield_name;
	@DocBean(name ="sourcefields_name",value="来源字段名称:",dataType = String.class,required = true)
	private String sourcefields_name;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="own_dource_table_id",value="已选数据源表id:",dataType = Long.class,required = true)
	private Long own_dource_table_id;
	@DocBean(name ="datatable_id",value="数据表id:",dataType = Long.class,required = true)
	private Long datatable_id;

	/** 取得：表id */
	public Long getEtl_id(){
		return etl_id;
	}
	/** 设置：表id */
	public void setEtl_id(Long etl_id){
		this.etl_id=etl_id;
	}
	/** 设置：表id */
	public void setEtl_id(String etl_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(etl_id)){
			this.etl_id=new Long(etl_id);
		}
	}
	/** 取得：目标字段名称 */
	public String getTargetfield_name(){
		return targetfield_name;
	}
	/** 设置：目标字段名称 */
	public void setTargetfield_name(String targetfield_name){
		this.targetfield_name=targetfield_name;
	}
	/** 取得：来源字段名称 */
	public String getSourcefields_name(){
		return sourcefields_name;
	}
	/** 设置：来源字段名称 */
	public void setSourcefields_name(String sourcefields_name){
		this.sourcefields_name=sourcefields_name;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：已选数据源表id */
	public Long getOwn_dource_table_id(){
		return own_dource_table_id;
	}
	/** 设置：已选数据源表id */
	public void setOwn_dource_table_id(Long own_dource_table_id){
		this.own_dource_table_id=own_dource_table_id;
	}
	/** 设置：已选数据源表id */
	public void setOwn_dource_table_id(String own_dource_table_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(own_dource_table_id)){
			this.own_dource_table_id=new Long(own_dource_table_id);
		}
	}
	/** 取得：数据表id */
	public Long getDatatable_id(){
		return datatable_id;
	}
	/** 设置：数据表id */
	public void setDatatable_id(Long datatable_id){
		this.datatable_id=datatable_id;
	}
	/** 设置：数据表id */
	public void setDatatable_id(String datatable_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(datatable_id)){
			this.datatable_id=new Long(datatable_id);
		}
	}
}
