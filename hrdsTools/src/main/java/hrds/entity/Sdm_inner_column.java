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
 * 流数据海云内部字段信息登记表
 */
@Table(tableName = "sdm_inner_column")
public class Sdm_inner_column extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_inner_column";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 流数据海云内部字段信息登记表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("field_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long field_id; //字段Id
	private String field_cn_name; //字段中文名称
	private String field_en_name; //字段英文名称
	private String field_type; //字段类型
	private String field_desc; //字段描述
	private String remark; //备注
	private Long table_id; //表id

	/** 取得：字段Id */
	public Long getField_id(){
		return field_id;
	}
	/** 设置：字段Id */
	public void setField_id(Long field_id){
		this.field_id=field_id;
	}
	/** 设置：字段Id */
	public void setField_id(String field_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(field_id)){
			this.field_id=new Long(field_id);
		}
	}
	/** 取得：字段中文名称 */
	public String getField_cn_name(){
		return field_cn_name;
	}
	/** 设置：字段中文名称 */
	public void setField_cn_name(String field_cn_name){
		this.field_cn_name=field_cn_name;
	}
	/** 取得：字段英文名称 */
	public String getField_en_name(){
		return field_en_name;
	}
	/** 设置：字段英文名称 */
	public void setField_en_name(String field_en_name){
		this.field_en_name=field_en_name;
	}
	/** 取得：字段类型 */
	public String getField_type(){
		return field_type;
	}
	/** 设置：字段类型 */
	public void setField_type(String field_type){
		this.field_type=field_type;
	}
	/** 取得：字段描述 */
	public String getField_desc(){
		return field_desc;
	}
	/** 设置：字段描述 */
	public void setField_desc(String field_desc){
		this.field_desc=field_desc;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：表id */
	public Long getTable_id(){
		return table_id;
	}
	/** 设置：表id */
	public void setTable_id(Long table_id){
		this.table_id=table_id;
	}
	/** 设置：表id */
	public void setTable_id(String table_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(table_id)){
			this.table_id=new Long(table_id);
		}
	}
}
