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
 * 数据存储层配置信息表
 */
@Table(tableName = "data_store_layer")
public class Data_store_layer extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "data_store_layer";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据存储层配置信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("datasc_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="datasc_id",value="存储配置主键信息",dataType = Long.class,required = true)
	private Long datasc_id;
	@DocBean(name ="storage_target",value="存储类型",dataType = String.class,required = true)
	private String storage_target;
	@DocBean(name ="storage_property_key",value="属性key",dataType = String.class,required = true)
	private String storage_property_key;
	@DocBean(name ="dsl_remark",value="备注",dataType = String.class,required = false)
	private String dsl_remark;
	@DocBean(name ="storage_property_val",value="属性value",dataType = String.class,required = false)
	private String storage_property_val;

	/** 取得：存储配置主键信息 */
	public Long getDatasc_id(){
		return datasc_id;
	}
	/** 设置：存储配置主键信息 */
	public void setDatasc_id(Long datasc_id){
		this.datasc_id=datasc_id;
	}
	/** 设置：存储配置主键信息 */
	public void setDatasc_id(String datasc_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(datasc_id)){
			this.datasc_id=new Long(datasc_id);
		}
	}
	/** 取得：存储类型 */
	public String getStorage_target(){
		return storage_target;
	}
	/** 设置：存储类型 */
	public void setStorage_target(String storage_target){
		this.storage_target=storage_target;
	}
	/** 取得：属性key */
	public String getStorage_property_key(){
		return storage_property_key;
	}
	/** 设置：属性key */
	public void setStorage_property_key(String storage_property_key){
		this.storage_property_key=storage_property_key;
	}
	/** 取得：备注 */
	public String getDsl_remark(){
		return dsl_remark;
	}
	/** 设置：备注 */
	public void setDsl_remark(String dsl_remark){
		this.dsl_remark=dsl_remark;
	}
	/** 取得：属性value */
	public String getStorage_property_val(){
		return storage_property_val;
	}
	/** 设置：属性value */
	public void setStorage_property_val(String storage_property_val){
		this.storage_property_val=storage_property_val;
	}
}
