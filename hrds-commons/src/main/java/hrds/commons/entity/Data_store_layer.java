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
 * 数据存储层配置表
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
	/** 数据存储层配置表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dsl_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="dsl_id",value="存储层配置ID:",dataType = Long.class,required = true)
	private Long dsl_id;
	@DocBean(name ="dsl_name",value="配置属性名称:",dataType = String.class,required = true)
	private String dsl_name;
	@DocBean(name ="store_type",value="存储类型(store_type):1-关系型数据库<DATABASE> 2-Hbase<HBASE> 3-solr<SOLR> 4-ElasticSearch<ElasticSearch> 5-mongodb<MONGODB> ",dataType = String.class,required = true)
	private String store_type;
	@DocBean(name ="dsl_remark",value="备注:",dataType = String.class,required = false)
	private String dsl_remark;

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
	/** 取得：配置属性名称 */
	public String getDsl_name(){
		return dsl_name;
	}
	/** 设置：配置属性名称 */
	public void setDsl_name(String dsl_name){
		this.dsl_name=dsl_name;
	}
	/** 取得：存储类型 */
	public String getStore_type(){
		return store_type;
	}
	/** 设置：存储类型 */
	public void setStore_type(String store_type){
		this.store_type=store_type;
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
