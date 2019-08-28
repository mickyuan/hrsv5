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
 * 自定义页面解析方法列表
 */
@Table(tableName = "cm_relation_cp")
public class Cm_relation_cp extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "cm_relation_cp";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 自定义页面解析方法列表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cp_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long cp_id; //自定义解析id
	private String method_name; //方法名称
	private String cp_expressions; //正则表达式
	private String pc_remark; //备注

	/** 取得：自定义解析id */
	public Long getCp_id(){
		return cp_id;
	}
	/** 设置：自定义解析id */
	public void setCp_id(Long cp_id){
		this.cp_id=cp_id;
	}
	/** 设置：自定义解析id */
	public void setCp_id(String cp_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(cp_id)){
			this.cp_id=new Long(cp_id);
		}
	}
	/** 取得：方法名称 */
	public String getMethod_name(){
		return method_name;
	}
	/** 设置：方法名称 */
	public void setMethod_name(String method_name){
		this.method_name=method_name;
	}
	/** 取得：正则表达式 */
	public String getCp_expressions(){
		return cp_expressions;
	}
	/** 设置：正则表达式 */
	public void setCp_expressions(String cp_expressions){
		this.cp_expressions=cp_expressions;
	}
	/** 取得：备注 */
	public String getPc_remark(){
		return pc_remark;
	}
	/** 设置：备注 */
	public void setPc_remark(String pc_remark){
		this.pc_remark=pc_remark;
	}
}
