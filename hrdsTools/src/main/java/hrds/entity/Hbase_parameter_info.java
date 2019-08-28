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
 * HBase表参数信息
 */
@Table(tableName = "hbase_parameter_info")
public class Hbase_parameter_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "hbase_parameter_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** HBase表参数信息 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("parameter_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long parameter_id; //参数ID
	private String is_flag; //是否标识
	private String remark; //备注
	private String table_column_name; //表列名称
	private Long user_id; //用户ID
	private Long use_id; //表使用ID

	/** 取得：参数ID */
	public Long getParameter_id(){
		return parameter_id;
	}
	/** 设置：参数ID */
	public void setParameter_id(Long parameter_id){
		this.parameter_id=parameter_id;
	}
	/** 设置：参数ID */
	public void setParameter_id(String parameter_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(parameter_id)){
			this.parameter_id=new Long(parameter_id);
		}
	}
	/** 取得：是否标识 */
	public String getIs_flag(){
		return is_flag;
	}
	/** 设置：是否标识 */
	public void setIs_flag(String is_flag){
		this.is_flag=is_flag;
	}
	/** 取得：备注 */
	public String getRemark(){
		return remark;
	}
	/** 设置：备注 */
	public void setRemark(String remark){
		this.remark=remark;
	}
	/** 取得：表列名称 */
	public String getTable_column_name(){
		return table_column_name;
	}
	/** 设置：表列名称 */
	public void setTable_column_name(String table_column_name){
		this.table_column_name=table_column_name;
	}
	/** 取得：用户ID */
	public Long getUser_id(){
		return user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(Long user_id){
		this.user_id=user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(String user_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(user_id)){
			this.user_id=new Long(user_id);
		}
	}
	/** 取得：表使用ID */
	public Long getUse_id(){
		return use_id;
	}
	/** 设置：表使用ID */
	public void setUse_id(Long use_id){
		this.use_id=use_id;
	}
	/** 设置：表使用ID */
	public void setUse_id(String use_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(use_id)){
			this.use_id=new Long(use_id);
		}
	}
}
