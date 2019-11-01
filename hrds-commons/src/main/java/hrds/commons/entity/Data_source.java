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
 * 数据源
 */
@Table(tableName = "data_source")
public class Data_source extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "data_source";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据源 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("source_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="source_id",value="数据源ID:",dataType = Long.class,required = true)
	private Long source_id;
	@DocBean(name ="datasource_name",value="数据源名称:",dataType = String.class,required = true)
	private String datasource_name;
	@DocBean(name ="datasource_number",value="数据源编号:",dataType = String.class,required = false)
	private String datasource_number;
	@DocBean(name ="source_remark",value="数据源详细描述:",dataType = String.class,required = false)
	private String source_remark;
	@DocBean(name ="create_date",value="创建日期:",dataType = String.class,required = true)
	private String create_date;
	@DocBean(name ="create_time",value="创建时间:",dataType = String.class,required = true)
	private String create_time;
	@DocBean(name ="create_user_id",value="用户ID:",dataType = Long.class,required = true)
	private Long create_user_id;
	@DocBean(name ="datasource_remark",value="备注:",dataType = String.class,required = false)
	private String datasource_remark;

	/** 取得：数据源ID */
	public Long getSource_id(){
		return source_id;
	}
	/** 设置：数据源ID */
	public void setSource_id(Long source_id){
		this.source_id=source_id;
	}
	/** 设置：数据源ID */
	public void setSource_id(String source_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(source_id)){
			this.source_id=new Long(source_id);
		}
	}
	/** 取得：数据源名称 */
	public String getDatasource_name(){
		return datasource_name;
	}
	/** 设置：数据源名称 */
	public void setDatasource_name(String datasource_name){
		this.datasource_name=datasource_name;
	}
	/** 取得：数据源编号 */
	public String getDatasource_number(){
		return datasource_number;
	}
	/** 设置：数据源编号 */
	public void setDatasource_number(String datasource_number){
		this.datasource_number=datasource_number;
	}
	/** 取得：数据源详细描述 */
	public String getSource_remark(){
		return source_remark;
	}
	/** 设置：数据源详细描述 */
	public void setSource_remark(String source_remark){
		this.source_remark=source_remark;
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
	/** 取得：用户ID */
	public Long getCreate_user_id(){
		return create_user_id;
	}
	/** 设置：用户ID */
	public void setCreate_user_id(Long create_user_id){
		this.create_user_id=create_user_id;
	}
	/** 设置：用户ID */
	public void setCreate_user_id(String create_user_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(create_user_id)){
			this.create_user_id=new Long(create_user_id);
		}
	}
	/** 取得：备注 */
	public String getDatasource_remark(){
		return datasource_remark;
	}
	/** 设置：备注 */
	public void setDatasource_remark(String datasource_remark){
		this.datasource_remark=datasource_remark;
	}
}
