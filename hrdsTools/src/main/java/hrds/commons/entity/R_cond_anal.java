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
 * 报表SQL条件详细分析表
 */
@Table(tableName = "r_cond_anal")
public class R_cond_anal extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "r_cond_anal";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 报表SQL条件详细分析表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("cond_anal_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long cond_anal_id; //条件分析编号
	private String column_name; //字段名
	private String column_value; //字段值
	private String column_cond; //字段条件
	private String incidence_rela; //关联关系
	private String sql_cond_type; //sql条件类型
	private Long sql_id; //SQL编号

	/** 取得：条件分析编号 */
	public Long getCond_anal_id(){
		return cond_anal_id;
	}
	/** 设置：条件分析编号 */
	public void setCond_anal_id(Long cond_anal_id){
		this.cond_anal_id=cond_anal_id;
	}
	/** 设置：条件分析编号 */
	public void setCond_anal_id(String cond_anal_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(cond_anal_id)){
			this.cond_anal_id=new Long(cond_anal_id);
		}
	}
	/** 取得：字段名 */
	public String getColumn_name(){
		return column_name;
	}
	/** 设置：字段名 */
	public void setColumn_name(String column_name){
		this.column_name=column_name;
	}
	/** 取得：字段值 */
	public String getColumn_value(){
		return column_value;
	}
	/** 设置：字段值 */
	public void setColumn_value(String column_value){
		this.column_value=column_value;
	}
	/** 取得：字段条件 */
	public String getColumn_cond(){
		return column_cond;
	}
	/** 设置：字段条件 */
	public void setColumn_cond(String column_cond){
		this.column_cond=column_cond;
	}
	/** 取得：关联关系 */
	public String getIncidence_rela(){
		return incidence_rela;
	}
	/** 设置：关联关系 */
	public void setIncidence_rela(String incidence_rela){
		this.incidence_rela=incidence_rela;
	}
	/** 取得：sql条件类型 */
	public String getSql_cond_type(){
		return sql_cond_type;
	}
	/** 设置：sql条件类型 */
	public void setSql_cond_type(String sql_cond_type){
		this.sql_cond_type=sql_cond_type;
	}
	/** 取得：SQL编号 */
	public Long getSql_id(){
		return sql_id;
	}
	/** 设置：SQL编号 */
	public void setSql_id(Long sql_id){
		this.sql_id=sql_id;
	}
	/** 设置：SQL编号 */
	public void setSql_id(String sql_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(sql_id)){
			this.sql_id=new Long(sql_id);
		}
	}
}
