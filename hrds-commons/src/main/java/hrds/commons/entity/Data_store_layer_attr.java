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
 * 数据存储层配置属性表
 */
@Table(tableName = "data_store_layer_attr")
public class Data_store_layer_attr extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "data_store_layer_attr";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据存储层配置属性表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dsla_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="dsla_id",value="存储配置主键信息:",dataType = Long.class,required = true)
	private Long dsla_id;
	@DocBean(name ="storage_property_key",value="属性key:",dataType = String.class,required = true)
	private String storage_property_key;
	@DocBean(name ="dsla_remark",value="备注:",dataType = String.class,required = false)
	private String dsla_remark;
	@DocBean(name ="storage_property_val",value="属性value:",dataType = String.class,required = true)
	private String storage_property_val;
	@DocBean(name ="dsl_id",value="存储层配置ID:",dataType = Long.class,required = true)
	private Long dsl_id;
	@DocBean(name ="is_file",value="是否为配置文件(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_file;

	/** 取得：存储配置主键信息 */
	public Long getDsla_id(){
		return dsla_id;
	}
	/** 设置：存储配置主键信息 */
	public void setDsla_id(Long dsla_id){
		this.dsla_id=dsla_id;
	}
	/** 设置：存储配置主键信息 */
	public void setDsla_id(String dsla_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dsla_id)){
			this.dsla_id=new Long(dsla_id);
		}
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
	public String getDsla_remark(){
		return dsla_remark;
	}
	/** 设置：备注 */
	public void setDsla_remark(String dsla_remark){
		this.dsla_remark=dsla_remark;
	}
	/** 取得：属性value */
	public String getStorage_property_val(){
		return storage_property_val;
	}
	/** 设置：属性value */
	public void setStorage_property_val(String storage_property_val){
		this.storage_property_val=storage_property_val;
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
	/** 取得：是否为配置文件 */
	public String getIs_file(){
		return is_file;
	}
	/** 设置：是否为配置文件 */
	public void setIs_file(String is_file){
		this.is_file=is_file;
	}
}
