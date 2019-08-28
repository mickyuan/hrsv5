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
 * 分类变量字段重编码表
 */
@Table(tableName = "ml_cv_column_recode")
public class Ml_cv_column_recode extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_cv_column_recode";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 分类变量字段重编码表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("columnreco_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long columnreco_id; //字段重编码编号
	private Long recode_id; //重编码编号
	private String newcolumn_name; //新字段名称
	private String value_recode; //编码前的值

	/** 取得：字段重编码编号 */
	public Long getColumnreco_id(){
		return columnreco_id;
	}
	/** 设置：字段重编码编号 */
	public void setColumnreco_id(Long columnreco_id){
		this.columnreco_id=columnreco_id;
	}
	/** 设置：字段重编码编号 */
	public void setColumnreco_id(String columnreco_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(columnreco_id)){
			this.columnreco_id=new Long(columnreco_id);
		}
	}
	/** 取得：重编码编号 */
	public Long getRecode_id(){
		return recode_id;
	}
	/** 设置：重编码编号 */
	public void setRecode_id(Long recode_id){
		this.recode_id=recode_id;
	}
	/** 设置：重编码编号 */
	public void setRecode_id(String recode_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(recode_id)){
			this.recode_id=new Long(recode_id);
		}
	}
	/** 取得：新字段名称 */
	public String getNewcolumn_name(){
		return newcolumn_name;
	}
	/** 设置：新字段名称 */
	public void setNewcolumn_name(String newcolumn_name){
		this.newcolumn_name=newcolumn_name;
	}
	/** 取得：编码前的值 */
	public String getValue_recode(){
		return value_recode;
	}
	/** 设置：编码前的值 */
	public void setValue_recode(String value_recode){
		this.value_recode=value_recode;
	}
}
