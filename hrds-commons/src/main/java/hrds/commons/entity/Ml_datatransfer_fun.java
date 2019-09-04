package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.commons.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 数据转换函数表
 */
@Table(tableName = "ml_datatransfer_fun")
public class Ml_datatransfer_fun extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_datatransfer_fun";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据转换函数表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("tranfunt_num");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long tranfunt_num; //转换函数类型编号
	private String funtype_name; //函数类型名称

	/** 取得：转换函数类型编号 */
	public Long getTranfunt_num(){
		return tranfunt_num;
	}
	/** 设置：转换函数类型编号 */
	public void setTranfunt_num(Long tranfunt_num){
		this.tranfunt_num=tranfunt_num;
	}
	/** 设置：转换函数类型编号 */
	public void setTranfunt_num(String tranfunt_num){
		if(!fd.ng.core.utils.StringUtil.isEmpty(tranfunt_num)){
			this.tranfunt_num=new Long(tranfunt_num);
		}
	}
	/** 取得：函数类型名称 */
	public String getFuntype_name(){
		return funtype_name;
	}
	/** 设置：函数类型名称 */
	public void setFuntype_name(String funtype_name){
		this.funtype_name=funtype_name;
	}
}
