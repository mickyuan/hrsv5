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
 * druid字段配置表
 */
@Table(tableName = "sdm_con_druid_col")
public class Sdm_con_druid_col extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "sdm_con_druid_col";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** druid字段配置表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("druid_col_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long druid_col_id; //druid字段编号
	private String column_name; //字段英文名称
	private String column_tyoe; //字段类型
	private Long druid_id; //druid编号
	private String column_cname; //字段中文名称

	/** 取得：druid字段编号 */
	public Long getDruid_col_id(){
		return druid_col_id;
	}
	/** 设置：druid字段编号 */
	public void setDruid_col_id(Long druid_col_id){
		this.druid_col_id=druid_col_id;
	}
	/** 设置：druid字段编号 */
	public void setDruid_col_id(String druid_col_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(druid_col_id)){
			this.druid_col_id=new Long(druid_col_id);
		}
	}
	/** 取得：字段英文名称 */
	public String getColumn_name(){
		return column_name;
	}
	/** 设置：字段英文名称 */
	public void setColumn_name(String column_name){
		this.column_name=column_name;
	}
	/** 取得：字段类型 */
	public String getColumn_tyoe(){
		return column_tyoe;
	}
	/** 设置：字段类型 */
	public void setColumn_tyoe(String column_tyoe){
		this.column_tyoe=column_tyoe;
	}
	/** 取得：druid编号 */
	public Long getDruid_id(){
		return druid_id;
	}
	/** 设置：druid编号 */
	public void setDruid_id(Long druid_id){
		this.druid_id=druid_id;
	}
	/** 设置：druid编号 */
	public void setDruid_id(String druid_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(druid_id)){
			this.druid_id=new Long(druid_id);
		}
	}
	/** 取得：字段中文名称 */
	public String getColumn_cname(){
		return column_cname;
	}
	/** 设置：字段中文名称 */
	public void setColumn_cname(String column_cname){
		this.column_cname=column_cname;
	}
}
