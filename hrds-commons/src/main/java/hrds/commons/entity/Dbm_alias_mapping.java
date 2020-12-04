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
 * 数据对标原始表名映射表
 */
@Table(tableName = "dbm_alias_mapping")
public class Dbm_alias_mapping extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "dbm_alias_mapping";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据对标原始表名映射表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("alias_number");
		__tmpPKS.add("alias_table_code");
		__tmpPKS.add("origin_table_code");
		__tmpPKS.add("sys_class_code");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="alias_number",value="别名序号:",dataType = Integer.class,required = true)
	private Integer alias_number;
	@DocBean(name ="alias_table_code",value="原始表映射名:",dataType = String.class,required = true)
	private String alias_table_code;
	@DocBean(name ="origin_table_code",value="原始表表名:",dataType = String.class,required = true)
	private String origin_table_code;
	@DocBean(name ="sys_class_code",value="系统分类编号:",dataType = String.class,required = true)
	private String sys_class_code;

	/** 取得：别名序号 */
	public Integer getAlias_number(){
		return alias_number;
	}
	/** 设置：别名序号 */
	public void setAlias_number(Integer alias_number){
		this.alias_number=alias_number;
	}
	/** 设置：别名序号 */
	public void setAlias_number(String alias_number){
		if(!fd.ng.core.utils.StringUtil.isEmpty(alias_number)){
			this.alias_number=new Integer(alias_number);
		}
	}
	/** 取得：原始表映射名 */
	public String getAlias_table_code(){
		return alias_table_code;
	}
	/** 设置：原始表映射名 */
	public void setAlias_table_code(String alias_table_code){
		this.alias_table_code=alias_table_code;
	}
	/** 取得：原始表表名 */
	public String getOrigin_table_code(){
		return origin_table_code;
	}
	/** 设置：原始表表名 */
	public void setOrigin_table_code(String origin_table_code){
		this.origin_table_code=origin_table_code;
	}
	/** 取得：系统分类编号 */
	public String getSys_class_code(){
		return sys_class_code;
	}
	/** 设置：系统分类编号 */
	public void setSys_class_code(String sys_class_code){
		this.sys_class_code=sys_class_code;
	}
}
