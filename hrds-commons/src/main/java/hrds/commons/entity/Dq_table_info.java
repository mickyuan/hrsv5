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
 * 自定义表信息
 */
@Table(tableName = "dq_table_info")
public class Dq_table_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_table_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 自定义表信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("table_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="table_id",value="自定义表ID:",dataType = Long.class,required = true)
	private Long table_id;
	@DocBean(name ="create_id",value="用户ID:",dataType = Long.class,required = true)
	private Long create_id;
	@DocBean(name ="table_space",value="表空间名称:",dataType = String.class,required = true)
	private String table_space;
	@DocBean(name ="table_name",value="表名:",dataType = String.class,required = true)
	private String table_name;
	@DocBean(name ="ch_name",value="表中文名称:",dataType = String.class,required = false)
	private String ch_name;
	@DocBean(name ="table_type",value="表的类型:",dataType = String.class,required = false)
	private String table_type;
	@DocBean(name ="create_date",value="开始日期:",dataType = String.class,required = true)
	private String create_date;
	@DocBean(name ="end_date",value="结束日期:",dataType = String.class,required = true)
	private String end_date;
	@DocBean(name ="is_trace",value="是否数据溯源(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_trace;
	@DocBean(name ="dq_remark",value="备注:",dataType = String.class,required = false)
	private String dq_remark;

	/** 取得：自定义表ID */
	public Long getTable_id(){
		return table_id;
	}
	/** 设置：自定义表ID */
	public void setTable_id(Long table_id){
		this.table_id=table_id;
	}
	/** 设置：自定义表ID */
	public void setTable_id(String table_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(table_id)){
			this.table_id=new Long(table_id);
		}
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
	/** 取得：表空间名称 */
	public String getTable_space(){
		return table_space;
	}
	/** 设置：表空间名称 */
	public void setTable_space(String table_space){
		this.table_space=table_space;
	}
	/** 取得：表名 */
	public String getTable_name(){
		return table_name;
	}
	/** 设置：表名 */
	public void setTable_name(String table_name){
		this.table_name=table_name;
	}
	/** 取得：表中文名称 */
	public String getCh_name(){
		return ch_name;
	}
	/** 设置：表中文名称 */
	public void setCh_name(String ch_name){
		this.ch_name=ch_name;
	}
	/** 取得：表的类型 */
	public String getTable_type(){
		return table_type;
	}
	/** 设置：表的类型 */
	public void setTable_type(String table_type){
		this.table_type=table_type;
	}
	/** 取得：开始日期 */
	public String getCreate_date(){
		return create_date;
	}
	/** 设置：开始日期 */
	public void setCreate_date(String create_date){
		this.create_date=create_date;
	}
	/** 取得：结束日期 */
	public String getEnd_date(){
		return end_date;
	}
	/** 设置：结束日期 */
	public void setEnd_date(String end_date){
		this.end_date=end_date;
	}
	/** 取得：是否数据溯源 */
	public String getIs_trace(){
		return is_trace;
	}
	/** 设置：是否数据溯源 */
	public void setIs_trace(String is_trace){
		this.is_trace=is_trace;
	}
	/** 取得：备注 */
	public String getDq_remark(){
		return dq_remark;
	}
	/** 设置：备注 */
	public void setDq_remark(String dq_remark){
		this.dq_remark=dq_remark;
	}
}
