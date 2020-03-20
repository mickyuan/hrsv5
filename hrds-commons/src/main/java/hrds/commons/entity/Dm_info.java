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
 * 数据集市信息表
 */
@Table(tableName = "dm_info")
public class Dm_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dm_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据集市信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("data_mart_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="data_mart_id",value="数据集市id:",dataType = Long.class,required = true)
	private Long data_mart_id;
	@DocBean(name ="mart_name",value="数据集市名称:",dataType = String.class,required = true)
	private String mart_name;
	@DocBean(name ="mart_desc",value="数据集市描述:",dataType = String.class,required = false)
	private String mart_desc;
	@DocBean(name ="mart_storage_path",value="数据集市存储路径:",dataType = String.class,required = true)
	private String mart_storage_path;
	@DocBean(name ="dm_remark",value="备注:",dataType = String.class,required = false)
	private String dm_remark;
	@DocBean(name ="create_date",value="创建日期:",dataType = String.class,required = true)
	private String create_date;
	@DocBean(name ="create_time",value="创建时间:",dataType = String.class,required = true)
	private String create_time;
	@DocBean(name ="mart_number",value="数据库编号:",dataType = String.class,required = true)
	private String mart_number;
	@DocBean(name ="create_id",value="用户ID:",dataType = Long.class,required = true)
	private Long create_id;

	/** 取得：数据集市id */
	public Long getData_mart_id(){
		return data_mart_id;
	}
	/** 设置：数据集市id */
	public void setData_mart_id(Long data_mart_id){
		this.data_mart_id=data_mart_id;
	}
	/** 设置：数据集市id */
	public void setData_mart_id(String data_mart_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(data_mart_id)){
			this.data_mart_id=new Long(data_mart_id);
		}
	}
	/** 取得：数据集市名称 */
	public String getMart_name(){
		return mart_name;
	}
	/** 设置：数据集市名称 */
	public void setMart_name(String mart_name){
		this.mart_name=mart_name;
	}
	/** 取得：数据集市描述 */
	public String getMart_desc(){
		return mart_desc;
	}
	/** 设置：数据集市描述 */
	public void setMart_desc(String mart_desc){
		this.mart_desc=mart_desc;
	}
	/** 取得：数据集市存储路径 */
	public String getMart_storage_path(){
		return mart_storage_path;
	}
	/** 设置：数据集市存储路径 */
	public void setMart_storage_path(String mart_storage_path){
		this.mart_storage_path=mart_storage_path;
	}
	/** 取得：备注 */
	public String getDm_remark(){
		return dm_remark;
	}
	/** 设置：备注 */
	public void setDm_remark(String dm_remark){
		this.dm_remark=dm_remark;
	}
	/** 取得：创建日期 */
	public String getCreate_date(){
		return create_date;
	}
	/** 设置：创建日期 */
	public void setCreate_date(String create_date){
		this.create_date=create_date;
	}
	/** 取得：创建时间 */
	public String getCreate_time(){
		return create_time;
	}
	/** 设置：创建时间 */
	public void setCreate_time(String create_time){
		this.create_time=create_time;
	}
	/** 取得：数据库编号 */
	public String getMart_number(){
		return mart_number;
	}
	/** 设置：数据库编号 */
	public void setMart_number(String mart_number){
		this.mart_number=mart_number;
	}
	/** 取得：用户ID */
	public Long getCreate_id(){
		return create_id;
	}
	/** 设置：用户ID */
	public void setCreate_id(Long create_id){
		this.create_id=create_id;
	}
	/** 设置：用户ID */
	public void setCreate_id(String create_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(create_id)){
			this.create_id=new Long(create_id);
		}
	}
}
