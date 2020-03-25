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
 * 列合并信息表
 */
@Table(tableName = "column_merge")
public class Column_merge extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "column_merge";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 列合并信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("col_merge_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="col_merge_id",value="字段编号:",dataType = Long.class,required = true)
	private Long col_merge_id;
	@DocBean(name ="col_name",value="合并后字段名称:",dataType = String.class,required = true)
	private String col_name;
	@DocBean(name ="old_name",value="要合并的字段:",dataType = String.class,required = true)
	private String old_name;
	@DocBean(name ="col_zhname",value="中文名称:",dataType = String.class,required = false)
	private String col_zhname;
	@DocBean(name ="col_type",value="字段类型:",dataType = String.class,required = true)
	private String col_type;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="valid_s_date",value="有效开始日期:",dataType = String.class,required = true)
	private String valid_s_date;
	@DocBean(name ="valid_e_date",value="有效结束日期:",dataType = String.class,required = true)
	private String valid_e_date;
	@DocBean(name ="table_id",value="表名ID:",dataType = Long.class,required = true)
	private Long table_id;

	/** 取得：字段编号 */
	public Long getCol_merge_id(){
		return col_merge_id;
	}
	/** 设置：字段编号 */
	public void setCol_merge_id(Long col_merge_id){
		this.col_merge_id=col_merge_id;
	}
	/** 设置：字段编号 */
	public void setCol_merge_id(String col_merge_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(col_merge_id)){
			this.col_merge_id=new Long(col_merge_id);
		}
	}
	/** 取得：合并后字段名称 */
	public String getCol_name(){
		return col_name;
	}
	/** 设置：合并后字段名称 */
	public void setCol_name(String col_name){
		this.col_name=col_name;
	}
	/** 取得：要合并的字段 */
	public String getOld_name(){
		return old_name;
	}
	/** 设置：要合并的字段 */
	public void setOld_name(String old_name){
		this.old_name=old_name;
	}
	/** 取得：中文名称 */
	public String getCol_zhname(){
		return col_zhname;
	}
	/** 设置：中文名称 */
	public void setCol_zhname(String col_zhname){
		this.col_zhname=col_zhname;
	}
	/** 取得：字段类型 */
	public String getCol_type(){
		return col_type;
	}
	/** 设置：字段类型 */
	public void setCol_type(String col_type){
		this.col_type=col_type;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：有效开始日期 */
	public String getValid_s_date(){
		return valid_s_date;
	}
	/** 设置：有效开始日期 */
	public void setValid_s_date(String valid_s_date){
		this.valid_s_date=valid_s_date;
	}
	/** 取得：有效结束日期 */
	public String getValid_e_date(){
		return valid_e_date;
	}
	/** 设置：有效结束日期 */
	public void setValid_e_date(String valid_e_date){
		this.valid_e_date=valid_e_date;
	}
	/** 取得：表名ID */
	public Long getTable_id(){
		return table_id;
	}
	/** 设置：表名ID */
	public void setTable_id(Long table_id){
		this.table_id=table_id;
	}
	/** 设置：表名ID */
	public void setTable_id(String table_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(table_id)){
			this.table_id=new Long(table_id);
		}
	}
}
