package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import hrds.commons.entity.fdentity.ProjectTableEntity;
import fd.ng.db.entity.anno.Table;
import fd.ng.core.docannotation.DocBean;
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
	@DocBean(name ="storage_property",value="存储属性信息",dataType = String.class,required = true)
	private String storage_property;
	@DocBean(name ="dsl_remark",value="备注",dataType = String.class,required = false)
	private String dsl_remark;

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
	/** 取得：存储属性信息 */
	public String getStorage_property(){
		return storage_property;
	}
	/** 设置：存储属性信息 */
	public void setStorage_property(String storage_property){
		this.storage_property=storage_property;
	}
	/** 取得：备注 */
	public String getDsl_remark(){
		return dsl_remark;
	}
	/** 设置：备注 */
	public void setDsl_remark(String dsl_remark){
		this.dsl_remark=dsl_remark;
	}
}
