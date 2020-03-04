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
 * 数据对标元管理代码项信息表
 */
@Table(tableName = "dbm_code_item_info")
public class Dbm_code_item_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dbm_code_item_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据对标元管理代码项信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("code_item_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="code_item_id",value="代码项主键:",dataType = Long.class,required = true)
	private Long code_item_id;
	@DocBean(name ="code_encode",value="代码编码:",dataType = String.class,required = false)
	private String code_encode;
	@DocBean(name ="code_item_name",value="代码项名:",dataType = String.class,required = true)
	private String code_item_name;
	@DocBean(name ="code_value",value="代码值:",dataType = String.class,required = false)
	private String code_value;
	@DocBean(name ="dbm_level",value="层级:",dataType = String.class,required = false)
	private String dbm_level;
	@DocBean(name ="code_remark",value="代码描述:",dataType = String.class,required = false)
	private String code_remark;
	@DocBean(name ="code_type_id",value="代码类主键:",dataType = Long.class,required = true)
	private Long code_type_id;

	/** 取得：代码项主键 */
	public Long getCode_item_id(){
		return code_item_id;
	}
	/** 设置：代码项主键 */
	public void setCode_item_id(Long code_item_id){
		this.code_item_id=code_item_id;
	}
	/** 设置：代码项主键 */
	public void setCode_item_id(String code_item_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(code_item_id)){
			this.code_item_id=new Long(code_item_id);
		}
	}
	/** 取得：代码编码 */
	public String getCode_encode(){
		return code_encode;
	}
	/** 设置：代码编码 */
	public void setCode_encode(String code_encode){
		this.code_encode=code_encode;
	}
	/** 取得：代码项名 */
	public String getCode_item_name(){
		return code_item_name;
	}
	/** 设置：代码项名 */
	public void setCode_item_name(String code_item_name){
		this.code_item_name=code_item_name;
	}
	/** 取得：代码值 */
	public String getCode_value(){
		return code_value;
	}
	/** 设置：代码值 */
	public void setCode_value(String code_value){
		this.code_value=code_value;
	}
	/** 取得：层级 */
	public String getDbm_level(){
		return dbm_level;
	}
	/** 设置：层级 */
	public void setDbm_level(String dbm_level){
		this.dbm_level=dbm_level;
	}
	/** 取得：代码描述 */
	public String getCode_remark(){
		return code_remark;
	}
	/** 设置：代码描述 */
	public void setCode_remark(String code_remark){
		this.code_remark=code_remark;
	}
	/** 取得：代码类主键 */
	public Long getCode_type_id(){
		return code_type_id;
	}
	/** 设置：代码类主键 */
	public void setCode_type_id(Long code_type_id){
		this.code_type_id=code_type_id;
	}
	/** 设置：代码类主键 */
	public void setCode_type_id(String code_type_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(code_type_id)){
			this.code_type_id=new Long(code_type_id);
		}
	}
}
