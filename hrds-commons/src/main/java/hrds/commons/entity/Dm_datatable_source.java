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
 * 数据表已选数据源信息
 */
@Table(tableName = "dm_datatable_source")
public class Dm_datatable_source extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dm_datatable_source";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据表已选数据源信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("own_dource_table_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="own_dource_table_id",value="已选数据源表id:",dataType = Long.class,required = true)
	private Long own_dource_table_id;
	@DocBean(name ="own_source_table_name",value="已选数据源表名:",dataType = String.class,required = true)
	private String own_source_table_name;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="source_type",value="数据来源类型(DataSourceType):ISL-贴源层_01<ISL> DCL-贴源层<DCL> DPL-加工层<DPL> DML-集市层<DML> SFL-系统层<SFL> AML-AI模型层<AML> DQC-管控层<DQC> UDL-自定义层<UDL> ",dataType = String.class,required = true)
	private String source_type;
	@DocBean(name ="datatable_id",value="数据表id:",dataType = Long.class,required = true)
	private Long datatable_id;

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
	/** 取得：已选数据源表名 */
	public String getOwn_source_table_name(){
		return own_source_table_name;
	}
	/** 设置：已选数据源表名 */
	public void setOwn_source_table_name(String own_source_table_name){
		this.own_source_table_name=own_source_table_name;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：数据来源类型 */
	public String getSource_type(){
		return source_type;
	}
	/** 设置：数据来源类型 */
	public void setSource_type(String source_type){
		this.source_type=source_type;
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
