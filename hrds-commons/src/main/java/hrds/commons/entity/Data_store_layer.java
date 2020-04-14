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
	@DocBean(name ="store_type",value="存储类型(Store_type):1-关系型数据库<DATABASE> 2-hive<HIVE> 3-Hbase<HBASE> 4-solr<SOLR> 5-ElasticSearch<ElasticSearch> 6-mongodb<MONGODB> ",dataType = String.class,required = true)
	private String store_type;
	@DocBean(name ="dsl_remark",value="备注:",dataType = String.class,required = false)
	private String dsl_remark;
	@DocBean(name ="is_hadoopclient",value="是否支持外部表(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_hadoopclient;
	@DocBean(name ="dtcs_id",value="类型对照ID:",dataType = Long.class,required = false)
	private Long dtcs_id;
	@DocBean(name ="dlcs_id",value="长度对照表ID:",dataType = Long.class,required = false)
	private Long dlcs_id;

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
	/** 取得：是否支持外部表 */
	public String getIs_hadoopclient(){
		return is_hadoopclient;
	}
	/** 设置：是否支持外部表 */
	public void setIs_hadoopclient(String is_hadoopclient){
		this.is_hadoopclient=is_hadoopclient;
	}
	/** 取得：类型对照ID */
	public Long getDtcs_id(){
		return dtcs_id;
	}
	/** 设置：类型对照ID */
	public void setDtcs_id(Long dtcs_id){
		this.dtcs_id=dtcs_id;
	}
	/** 设置：类型对照ID */
	public void setDtcs_id(String dtcs_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dtcs_id)){
			this.dtcs_id=new Long(dtcs_id);
		}
	}
	/** 取得：长度对照表ID */
	public Long getDlcs_id(){
		return dlcs_id;
	}
	/** 设置：长度对照表ID */
	public void setDlcs_id(Long dlcs_id){
		this.dlcs_id=dlcs_id;
	}
	/** 设置：长度对照表ID */
	public void setDlcs_id(String dlcs_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dlcs_id)){
			this.dlcs_id=new Long(dlcs_id);
		}
	}
}
