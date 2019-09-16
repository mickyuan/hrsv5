package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.commons.apiannotation.ApiBean;
import hrds.commons.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 子系统定义表
 */
@Table(tableName = "etl_sub_sys_list")
public class Etl_sub_sys_list extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "etl_sub_sys_list";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 子系统定义表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("sub_sys_cd");
		__tmpPKS.add("etl_sys_cd");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@ApiBean(name ="sub_sys_cd",value="子系统代码",dataType = String.class,required = true)
	private String sub_sys_cd; //子系统代码
	@ApiBean(name ="sub_sys_desc",value="子系统描述",dataType = String.class,required = false)
	private String sub_sys_desc; //子系统描述
	@ApiBean(name ="comments",value="备注信息",dataType = String.class,required = false)
	private String comments; //备注信息
	@ApiBean(name ="etl_sys_cd",value="工程代码",dataType = String.class,required = true)
	private String etl_sys_cd; //工程代码

	/** 取得：子系统代码 */
	public String getSub_sys_cd(){
		return sub_sys_cd;
	}
	/** 设置：子系统代码 */
	public void setSub_sys_cd(String sub_sys_cd){
		this.sub_sys_cd=sub_sys_cd;
	}
	/** 取得：子系统描述 */
	public String getSub_sys_desc(){
		return sub_sys_desc;
	}
	/** 设置：子系统描述 */
	public void setSub_sys_desc(String sub_sys_desc){
		this.sub_sys_desc=sub_sys_desc;
	}
	/** 取得：备注信息 */
	public String getComments(){
		return comments;
	}
	/** 设置：备注信息 */
	public void setComments(String comments){
		this.comments=comments;
	}
	/** 取得：工程代码 */
	public String getEtl_sys_cd(){
		return etl_sys_cd;
	}
	/** 设置：工程代码 */
	public void setEtl_sys_cd(String etl_sys_cd){
		this.etl_sys_cd=etl_sys_cd;
	}
}
