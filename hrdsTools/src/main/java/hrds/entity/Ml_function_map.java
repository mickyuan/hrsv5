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
 * 函数对应表
 */
@Table(tableName = "ml_function_map")
public class Ml_function_map extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "ml_function_map";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 函数对应表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("funmap_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String specific_fun; //具体函数
	private Long tranfunt_num; //转换函数类型编号
	private Long funmap_id; //函数对应编码
	private String transfer_method; //转换方式

	/** 取得：具体函数 */
	public String getSpecific_fun(){
		return specific_fun;
	}
	/** 设置：具体函数 */
	public void setSpecific_fun(String specific_fun){
		this.specific_fun=specific_fun;
	}
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
	/** 取得：函数对应编码 */
	public Long getFunmap_id(){
		return funmap_id;
	}
	/** 设置：函数对应编码 */
	public void setFunmap_id(Long funmap_id){
		this.funmap_id=funmap_id;
	}
	/** 设置：函数对应编码 */
	public void setFunmap_id(String funmap_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(funmap_id)){
			this.funmap_id=new Long(funmap_id);
		}
	}
	/** 取得：转换方式 */
	public String getTransfer_method(){
		return transfer_method;
	}
	/** 设置：转换方式 */
	public void setTransfer_method(String transfer_method){
		this.transfer_method=transfer_method;
	}
}
