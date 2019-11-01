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
 * 主键生成表
 */
@Table(tableName = "keytable")
public class Keytable extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "keytable";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 主键生成表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("key_name");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="key_name",value="key_name:",dataType = String.class,required = true)
	private String key_name;
	@DocBean(name ="key_value",value="value:",dataType = Integer.class,required = false)
	private Integer key_value;

	/** 取得：key_name */
	public String getKey_name(){
		return key_name;
	}
	/** 设置：key_name */
	public void setKey_name(String key_name){
		this.key_name=key_name;
	}
	/** 取得：value */
	public Integer getKey_value(){
		return key_value;
	}
	/** 设置：value */
	public void setKey_value(Integer key_value){
		this.key_value=key_value;
	}
	/** 设置：value */
	public void setKey_value(String key_value){
		if(!fd.ng.core.utils.StringUtil.isEmpty(key_value)){
			this.key_value=new Integer(key_value);
		}
	}
}
