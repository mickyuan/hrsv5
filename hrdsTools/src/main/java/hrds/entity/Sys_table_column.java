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
 * 表字段信息
 */
@Table(tableName = "sys_table_column")
public class Sys_table_column extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sys_table_column";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 表字段信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("field_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long field_id; //字段id
	private String column_name; //字段名称
	private String column_type; //字段类型
	private String column_length; //字段长度
	private String partition_column; //是否为分区列
	private String remark; //备注
	private String field_ch_name; //字段中文名称
	private Long info_id; //信息id
	private String is_null; //是否可为空
	private String is_primatykey; //是否为主键
	private String is_dictionary; //是否启用字典编码
	private String is_invertedindex; //是否禁用倒排索引
	private String is_bucketcolumns; //是否是Bucketing考虑的列
	private String colsourcetab; //字段来源表名称
	private String colsourcecol; //来源字段

	/** 取得：字段id */
	public Long getField_id(){
		return field_id;
	}
	/** 设置：字段id */
	public void setField_id(Long field_id){
		this.field_id=field_id;
	}
	/** 设置：字段id */
	public void setField_id(String field_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(field_id)){
			this.field_id=new Long(field_id);
		}
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
	/** 取得：是否为分区列 */
	public String getPartition_column(){
		return partition_column;
	}
	/** 设置：是否为分区列 */
	public void setPartition_column(String partition_column){
		this.partition_column=partition_column;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：字段中文名称 */
	public String getField_ch_name(){
		return field_ch_name;
	}
	/** 设置：字段中文名称 */
	public void setField_ch_name(String field_ch_name){
		this.field_ch_name=field_ch_name;
	}
	/** 取得：信息id */
	public Long getInfo_id(){
		return info_id;
	}
	/** 设置：信息id */
	public void setInfo_id(Long info_id){
		this.info_id=info_id;
	}
	/** 设置：信息id */
	public void setInfo_id(String info_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(info_id)){
			this.info_id=new Long(info_id);
		}
	}
	/** 取得：是否可为空 */
	public String getIs_null(){
		return is_null;
	}
	/** 设置：是否可为空 */
	public void setIs_null(String is_null){
		this.is_null=is_null;
	}
	/** 取得：是否为主键 */
	public String getIs_primatykey(){
		return is_primatykey;
	}
	/** 设置：是否为主键 */
	public void setIs_primatykey(String is_primatykey){
		this.is_primatykey=is_primatykey;
	}
	/** 取得：是否启用字典编码 */
	public String getIs_dictionary(){
		return is_dictionary;
	}
	/** 设置：是否启用字典编码 */
	public void setIs_dictionary(String is_dictionary){
		this.is_dictionary=is_dictionary;
	}
	/** 取得：是否禁用倒排索引 */
	public String getIs_invertedindex(){
		return is_invertedindex;
	}
	/** 设置：是否禁用倒排索引 */
	public void setIs_invertedindex(String is_invertedindex){
		this.is_invertedindex=is_invertedindex;
	}
	/** 取得：是否是Bucketing考虑的列 */
	public String getIs_bucketcolumns(){
		return is_bucketcolumns;
	}
	/** 设置：是否是Bucketing考虑的列 */
	public void setIs_bucketcolumns(String is_bucketcolumns){
		this.is_bucketcolumns=is_bucketcolumns;
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
}
