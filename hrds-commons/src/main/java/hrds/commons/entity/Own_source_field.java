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
 * 数据源表字段
 */
@Table(tableName = "own_source_field")
public class Own_source_field extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "own_source_field";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据源表字段 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("own_field_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="own_field_id",value="字段id:",dataType = Long.class,required = true)
	private Long own_field_id;
	@DocBean(name ="field_name",value="字段名称:",dataType = String.class,required = true)
	private String field_name;
	@DocBean(name ="field_type",value="字段类型:",dataType = String.class,required = true)
	private String field_type;
	@DocBean(name ="remark",value="备注:",dataType = String.class,required = false)
	private String remark;
	@DocBean(name ="own_dource_table_id",value="已选数据源表id:",dataType = Long.class,required = true)
	private Long own_dource_table_id;

	/** 取得：字段id */
	public Long getOwn_field_id(){
		return own_field_id;
	}
	/** 设置：字段id */
	public void setOwn_field_id(Long own_field_id){
		this.own_field_id=own_field_id;
	}
	/** 设置：字段id */
	public void setOwn_field_id(String own_field_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(own_field_id)){
			this.own_field_id=new Long(own_field_id);
		}
	}
	/** 取得：字段名称 */
	public String getField_name(){
		return field_name;
	}
	/** 设置：字段名称 */
	public void setField_name(String field_name){
		this.field_name=field_name;
	}
	/** 取得：字段类型 */
	public String getField_type(){
		return field_type;
	}
	/** 设置：字段类型 */
	public void setField_type(String field_type){
		this.field_type=field_type;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：已选数据源表id */
	public Long getOwn_dource_table_id(){
		return own_dource_table_id;
	}
	/** 设置：已选数据源表id */
	public void setOwn_dource_table_id(Long own_dource_table_id){
		this.own_dource_table_id=own_dource_table_id;
	}
	/** 设置：已选数据源表id */
	public void setOwn_dource_table_id(String own_dource_table_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(own_dource_table_id)){
			this.own_dource_table_id=new Long(own_dource_table_id);
		}
	}
}
