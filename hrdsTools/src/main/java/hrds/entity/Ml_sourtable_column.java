package hrds.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 机器学习数据表字段信息表
 */
@Table(tableName = "ml_sourtable_column")
public class Ml_sourtable_column extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_sourtable_column";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 机器学习数据表字段信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("table_column_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long table_column_id; //数据源表字段编号
	private String ocolumn_name; //原始字段名称
	private String column_cn_name; //字段中文名
	private String column_en_name; //字段英文名
	private String column_type; //字段类型
	private String column_desc; //字段描述
	private Long dtable_info_id; //数据表信息编号

	/** 取得：数据源表字段编号 */
	public Long getTable_column_id(){
		return table_column_id;
	}
	/** 设置：数据源表字段编号 */
	public void setTable_column_id(Long table_column_id){
		this.table_column_id=table_column_id;
	}
	/** 设置：数据源表字段编号 */
	public void setTable_column_id(String table_column_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(table_column_id)){
			this.table_column_id=new Long(table_column_id);
		}
	}
	/** 取得：原始字段名称 */
	public String getOcolumn_name(){
		return ocolumn_name;
	}
	/** 设置：原始字段名称 */
	public void setOcolumn_name(String ocolumn_name){
		this.ocolumn_name=ocolumn_name;
	}
	/** 取得：字段中文名 */
	public String getColumn_cn_name(){
		return column_cn_name;
	}
	/** 设置：字段中文名 */
	public void setColumn_cn_name(String column_cn_name){
		this.column_cn_name=column_cn_name;
	}
	/** 取得：字段英文名 */
	public String getColumn_en_name(){
		return column_en_name;
	}
	/** 设置：字段英文名 */
	public void setColumn_en_name(String column_en_name){
		this.column_en_name=column_en_name;
	}
	/** 取得：字段类型 */
	public String getColumn_type(){
		return column_type;
	}
	/** 设置：字段类型 */
	public void setColumn_type(String column_type){
		this.column_type=column_type;
	}
	/** 取得：字段描述 */
	public String getColumn_desc(){
		return column_desc;
	}
	/** 设置：字段描述 */
	public void setColumn_desc(String column_desc){
		this.column_desc=column_desc;
	}
	/** 取得：数据表信息编号 */
	public Long getDtable_info_id(){
		return dtable_info_id;
	}
	/** 设置：数据表信息编号 */
	public void setDtable_info_id(Long dtable_info_id){
		this.dtable_info_id=dtable_info_id;
	}
	/** 设置：数据表信息编号 */
	public void setDtable_info_id(String dtable_info_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dtable_info_id)){
			this.dtable_info_id=new Long(dtable_info_id);
		}
	}
}
