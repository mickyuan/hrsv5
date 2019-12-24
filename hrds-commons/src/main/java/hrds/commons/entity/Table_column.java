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
 * 表对应的字段
 */
@Table(tableName = "table_column")
public class Table_column extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "table_column";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 表对应的字段 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("column_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="column_id",value="字段ID:",dataType = Long.class,required = true)
	private Long column_id;
	@DocBean(name ="is_primary_key",value="是否为主键(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_primary_key;
	@DocBean(name ="column_name",value="列名:",dataType = String.class,required = true)
	private String column_name;
	@DocBean(name ="column_ch_name",value="列中文名称:",dataType = String.class,required = false)
	private String column_ch_name;
	@DocBean(name ="valid_s_date",value="有效开始日期:",dataType = String.class,required = true)
	private String valid_s_date;
	@DocBean(name ="valid_e_date",value="有效结束日期:",dataType = String.class,required = true)
	private String valid_e_date;
	@DocBean(name ="is_get",value="是否采集(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = false)
	private String is_get;
	@DocBean(name ="column_type",value="列字段类型:",dataType = String.class,required = false)
	private String column_type;
	@DocBean(name ="table_id",value="表名ID:",dataType = Long.class,required = true)
	private Long table_id;
	@DocBean(name ="tc_remark",value="备注:",dataType = String.class,required = false)
	private String tc_remark;
	@DocBean(name ="is_alive",value="是否保留原字段(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_alive;
	@DocBean(name ="is_new",value="是否为变化生成(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String is_new;
	@DocBean(name ="tc_or",value="清洗顺序:",dataType = String.class,required = false)
	private String tc_or;

	/** 取得：字段ID */
	public Long getColumn_id(){
		return column_id;
	}
	/** 设置：字段ID */
	public void setColumn_id(Long column_id){
		this.column_id=column_id;
	}
	/** 设置：字段ID */
	public void setColumn_id(String column_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(column_id)){
			this.column_id=new Long(column_id);
		}
	}
	/** 取得：是否为主键 */
	public String getIs_primary_key(){
		return is_primary_key;
	}
	/** 设置：是否为主键 */
	public void setIs_primary_key(String is_primary_key){
		this.is_primary_key=is_primary_key;
	}
	/** 取得：列名 */
	public String getColumn_name(){
		return column_name;
	}
	/** 设置：列名 */
	public void setColumn_name(String column_name){
		this.column_name=column_name;
	}
	/** 取得：列中文名称 */
	public String getColumn_ch_name(){
		return column_ch_name;
	}
	/** 设置：列中文名称 */
	public void setColumn_ch_name(String column_ch_name){
		this.column_ch_name=column_ch_name;
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
	/** 取得：是否采集 */
	public String getIs_get(){
		return is_get;
	}
	/** 设置：是否采集 */
	public void setIs_get(String is_get){
		this.is_get=is_get;
	}
	/** 取得：列字段类型 */
	public String getColumn_type(){
		return column_type;
	}
	/** 设置：列字段类型 */
	public void setColumn_type(String column_type){
		this.column_type=column_type;
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
	/** 取得：备注 */
	public String getTc_remark(){
		return tc_remark;
	}
	/** 设置：备注 */
	public void setTc_remark(String tc_remark){
		this.tc_remark=tc_remark;
	}
	/** 取得：是否保留原字段 */
	public String getIs_alive(){
		return is_alive;
	}
	/** 设置：是否保留原字段 */
	public void setIs_alive(String is_alive){
		this.is_alive=is_alive;
	}
	/** 取得：是否为变化生成 */
	public String getIs_new(){
		return is_new;
	}
	/** 设置：是否为变化生成 */
	public void setIs_new(String is_new){
		this.is_new=is_new;
	}
	/** 取得：清洗顺序 */
	public String getTc_or(){
		return tc_or;
	}
	/** 设置：清洗顺序 */
	public void setTc_or(String tc_or){
		this.tc_or=tc_or;
	}
}
