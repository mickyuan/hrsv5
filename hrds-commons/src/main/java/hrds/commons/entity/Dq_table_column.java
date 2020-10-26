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
 * 自定义表字段信息
 */
@Table(tableName = "dq_table_column")
public class Dq_table_column extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dq_table_column";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 自定义表字段信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("field_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="field_id",value="自定义表字段ID:",dataType = Long.class,required = true)
	private Long field_id;
	@DocBean(name ="table_id",value="自定义表ID:",dataType = Long.class,required = true)
	private Long table_id;
	@DocBean(name ="field_ch_name",value="字段中文名称:",dataType = String.class,required = false)
	private String field_ch_name;
	@DocBean(name ="column_name",value="字段名称:",dataType = String.class,required = true)
	private String column_name;
	@DocBean(name ="column_type",value="字段类型:",dataType = String.class,required = true)
	private String column_type;
	@DocBean(name ="column_length",value="字段长度:",dataType = String.class,required = false)
	private String column_length;
	@DocBean(name ="is_null",value="是否可为空(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_null;
	@DocBean(name ="colsourcetab",value="字段来源表名称:",dataType = String.class,required = false)
	private String colsourcetab;
	@DocBean(name ="colsourcecol",value="来源字段:",dataType = String.class,required = false)
	private String colsourcecol;
	@DocBean(name ="dq_remark",value="备注:",dataType = String.class,required = false)
	private String dq_remark;

	/** 取得：自定义表字段ID */
	public Long getField_id(){
		return field_id;
	}
	/** 设置：自定义表字段ID */
	public void setField_id(Long field_id){
		this.field_id=field_id;
	}
	/** 设置：自定义表字段ID */
	public void setField_id(String field_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(field_id)){
			this.field_id=new Long(field_id);
		}
	}
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
	/** 取得：字段中文名称 */
	public String getField_ch_name(){
		return field_ch_name;
	}
	/** 设置：字段中文名称 */
	public void setField_ch_name(String field_ch_name){
		this.field_ch_name=field_ch_name;
	}
	/** 取得：字段名称 */
	public String getColumn_name(){
		return column_name;
	}
	/** 设置：字段名称 */
	public void setColumn_name(String column_name){
		this.column_name=column_name;
	}
	/** 取得：字段类型 */
	public String getColumn_type(){
		return column_type;
	}
	/** 设置：字段类型 */
	public void setColumn_type(String column_type){
		this.column_type=column_type;
	}
	/** 取得：字段长度 */
	public String getColumn_length(){
		return column_length;
	}
	/** 设置：字段长度 */
	public void setColumn_length(String column_length){
		this.column_length=column_length;
	}
	/** 取得：是否可为空 */
	public String getIs_null(){
		return is_null;
	}
	/** 设置：是否可为空 */
	public void setIs_null(String is_null){
		this.is_null=is_null;
	}
	/** 取得：字段来源表名称 */
	public String getColsourcetab(){
		return colsourcetab;
	}
	/** 设置：字段来源表名称 */
	public void setColsourcetab(String colsourcetab){
		this.colsourcetab=colsourcetab;
	}
	/** 取得：来源字段 */
	public String getColsourcecol(){
		return colsourcecol;
	}
	/** 设置：来源字段 */
	public void setColsourcecol(String colsourcecol){
		this.colsourcecol=colsourcecol;
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
