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
 * 数据加工spark语法提示
 */
@Table(tableName = "edw_sparksql_gram")
public class Edw_sparksql_gram extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_sparksql_gram";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据加工spark语法提示 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("esg_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="esg_id",value="序号:",dataType = Long.class,required = true)
	private Long esg_id;
	@DocBean(name ="function_name",value="函数名称:",dataType = String.class,required = true)
	private String function_name;
	@DocBean(name ="function_example",value="函数例子:",dataType = String.class,required = true)
	private String function_example;
	@DocBean(name ="function_desc",value="函数描述:",dataType = String.class,required = true)
	private String function_desc;
	@DocBean(name ="is_available",value="是否可用(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_available;
	@DocBean(name ="is_udf",value="是否udf(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_udf;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="class_url",value="函数类路径:",dataType = String.class,required = false)
	private String class_url;
	@DocBean(name ="jar_url",value="jar路径:",dataType = String.class,required = false)
	private String jar_url;
	@DocBean(name ="is_sparksql",value="是否同时使用sparksql(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_sparksql;
	@DocBean(name ="hivedb_name",value="hive库名:",dataType = String.class,required = false)
	private String hivedb_name;

	/** 取得：序号 */
	public Long getEsg_id(){
		return esg_id;
	}
	/** 设置：序号 */
	public void setEsg_id(Long esg_id){
		this.esg_id=esg_id;
	}
	/** 设置：序号 */
	public void setEsg_id(String esg_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(esg_id)){
			this.esg_id=new Long(esg_id);
		}
	}
	/** 取得：函数名称 */
	public String getFunction_name(){
		return function_name;
	}
	/** 设置：函数名称 */
	public void setFunction_name(String function_name){
		this.function_name=function_name;
	}
	/** 取得：函数例子 */
	public String getFunction_example(){
		return function_example;
	}
	/** 设置：函数例子 */
	public void setFunction_example(String function_example){
		this.function_example=function_example;
	}
	/** 取得：函数描述 */
	public String getFunction_desc(){
		return function_desc;
	}
	/** 设置：函数描述 */
	public void setFunction_desc(String function_desc){
		this.function_desc=function_desc;
	}
	/** 取得：是否可用 */
	public String getIs_available(){
		return is_available;
	}
	/** 设置：是否可用 */
	public void setIs_available(String is_available){
		this.is_available=is_available;
	}
	/** 取得：是否udf */
	public String getIs_udf(){
		return is_udf;
	}
	/** 设置：是否udf */
	public void setIs_udf(String is_udf){
		this.is_udf=is_udf;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：函数类路径 */
	public String getClass_url(){
		return class_url;
	}
	/** 设置：函数类路径 */
	public void setClass_url(String class_url){
		this.class_url=class_url;
	}
	/** 取得：jar路径 */
	public String getJar_url(){
		return jar_url;
	}
	/** 设置：jar路径 */
	public void setJar_url(String jar_url){
		this.jar_url=jar_url;
	}
	/** 取得：是否同时使用sparksql */
	public String getIs_sparksql(){
		return is_sparksql;
	}
	/** 设置：是否同时使用sparksql */
	public void setIs_sparksql(String is_sparksql){
		this.is_sparksql=is_sparksql;
	}
	/** 取得：hive库名 */
	public String getHivedb_name(){
		return hivedb_name;
	}
	/** 设置：hive库名 */
	public void setHivedb_name(String hivedb_name){
		this.hivedb_name=hivedb_name;
	}
}
