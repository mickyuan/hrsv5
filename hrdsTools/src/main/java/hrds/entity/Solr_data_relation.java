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
 * solr数据关联表
 */
@Table(tableName = "solr_data_relation")
public class Solr_data_relation extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "solr_data_relation";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** solr数据关联表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("field_name");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String field_name; //字段名字

	/** 取得：字段名字 */
	public String getField_name(){
		return field_name;
	}
	/** 设置：字段名字 */
	public void setField_name(String field_name){
		this.field_name=field_name;
	}
}
