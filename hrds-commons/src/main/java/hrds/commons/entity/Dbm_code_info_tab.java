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
 * 数据对标码值信息表
 */
@Table(tableName = "dbm_code_info_tab")
public class Dbm_code_info_tab extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dbm_code_info_tab";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据对标码值信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sys_class_code");
		__tmpPKS.add("table_schema");
		__tmpPKS.add("table_code");
		__tmpPKS.add("column_code");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="sys_class_code",value="系统分类编码:",dataType = String.class,required = true)
	private String sys_class_code;
	@DocBean(name ="table_schema",value="系统schema:",dataType = String.class,required = true)
	private String table_schema;
	@DocBean(name ="table_code",value="原始表编码:",dataType = String.class,required = true)
	private String table_code;
	@DocBean(name ="column_code",value="被引用字段编码:",dataType = String.class,required = true)
	private String column_code;
	@DocBean(name ="cdval_type",value="码值类别:",dataType = String.class,required = false)
	private String cdval_type;
	@DocBean(name ="code_cate_name",value="代码分类名称:",dataType = String.class,required = false)
	private String code_cate_name;
	@DocBean(name ="code_value",value="代码取值:",dataType = String.class,required = false)
	private String code_value;
	@DocBean(name ="st_tm",value="开始时间:",dataType = String.class,required = false)
	private String st_tm;
	@DocBean(name ="code_content",value="代码内容:",dataType = String.class,required = false)
	private String code_content;

	/** 取得：系统分类编码 */
	public String getSys_class_code(){
		return sys_class_code;
	}
	/** 设置：系统分类编码 */
	public void setSys_class_code(String sys_class_code){
		this.sys_class_code=sys_class_code;
	}
	/** 取得：系统schema */
	public String getTable_schema(){
		return table_schema;
	}
	/** 设置：系统schema */
	public void setTable_schema(String table_schema){
		this.table_schema=table_schema;
	}
	/** 取得：原始表编码 */
	public String getTable_code(){
		return table_code;
	}
	/** 设置：原始表编码 */
	public void setTable_code(String table_code){
		this.table_code=table_code;
	}
	/** 取得：被引用字段编码 */
	public String getColumn_code(){
		return column_code;
	}
	/** 设置：被引用字段编码 */
	public void setColumn_code(String column_code){
		this.column_code=column_code;
	}
	/** 取得：码值类别 */
	public String getCdval_type(){
		return cdval_type;
	}
	/** 设置：码值类别 */
	public void setCdval_type(String cdval_type){
		this.cdval_type=cdval_type;
	}
	/** 取得：代码分类名称 */
	public String getCode_cate_name(){
		return code_cate_name;
	}
	/** 设置：代码分类名称 */
	public void setCode_cate_name(String code_cate_name){
		this.code_cate_name=code_cate_name;
	}
	/** 取得：代码取值 */
	public String getCode_value(){
		return code_value;
	}
	/** 设置：代码取值 */
	public void setCode_value(String code_value){
		this.code_value=code_value;
	}
	/** 取得：开始时间 */
	public String getSt_tm(){
		return st_tm;
	}
	/** 设置：开始时间 */
	public void setSt_tm(String st_tm){
		this.st_tm=st_tm;
	}
	/** 取得：代码内容 */
	public String getCode_content(){
		return code_content;
	}
	/** 设置：代码内容 */
	public void setCode_content(String code_content){
		this.code_content=code_content;
	}
}
