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
 * 数据仓库接口参数信息管理
 */
@Table(tableName = "edw_interface_params")
public class Edw_interface_params extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "edw_interface_params";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 数据仓库接口参数信息管理 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("src_table");
		__tmpPKS.add("src_column");
		__tmpPKS.add("src_cd");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String src_system; //系统名称
	private String src_table; //源表名
	private String src_column; //源字段名
	private String src_cd; //源代码
	private String src_desc; //源代码描述
	private String dest_table; //目标表名
	private String dest_cd; //目标代码
	private String dest_desc; //目标代码描述

	/** 取得：系统名称 */
	public String getSrc_system(){
		return src_system;
	}
	/** 设置：系统名称 */
	public void setSrc_system(String src_system){
		this.src_system=src_system;
	}
	/** 取得：源表名 */
	public String getSrc_table(){
		return src_table;
	}
	/** 设置：源表名 */
	public void setSrc_table(String src_table){
		this.src_table=src_table;
	}
	/** 取得：源字段名 */
	public String getSrc_column(){
		return src_column;
	}
	/** 设置：源字段名 */
	public void setSrc_column(String src_column){
		this.src_column=src_column;
	}
	/** 取得：源代码 */
	public String getSrc_cd(){
		return src_cd;
	}
	/** 设置：源代码 */
	public void setSrc_cd(String src_cd){
		this.src_cd=src_cd;
	}
	/** 取得：源代码描述 */
	public String getSrc_desc(){
		return src_desc;
	}
	/** 设置：源代码描述 */
	public void setSrc_desc(String src_desc){
		this.src_desc=src_desc;
	}
	/** 取得：目标表名 */
	public String getDest_table(){
		return dest_table;
	}
	/** 设置：目标表名 */
	public void setDest_table(String dest_table){
		this.dest_table=dest_table;
	}
	/** 取得：目标代码 */
	public String getDest_cd(){
		return dest_cd;
	}
	/** 设置：目标代码 */
	public void setDest_cd(String dest_cd){
		this.dest_cd=dest_cd;
	}
	/** 取得：目标代码描述 */
	public String getDest_desc(){
		return dest_desc;
	}
	/** 设置：目标代码描述 */
	public void setDest_desc(String dest_desc){
		this.dest_desc=dest_desc;
	}
}
