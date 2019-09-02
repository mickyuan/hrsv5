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
 * 组件分组表
 */
@Table(tableName = "auto_comp_group")
public class Auto_comp_group extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_comp_group";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 组件分组表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("component_group_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String column_name; //字段名
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String create_user; //创建用户
	private String last_update_date; //最后更新日期
	private String last_update_time; //最后更新时间
	private String update_user; //更新用户
	private Long component_group_id; //分组ID
	private Long component_id; //组件ID

	/** 取得：字段名 */
	public String getColumn_name(){
		return column_name;
	}
	/** 设置：字段名 */
	public void setColumn_name(String column_name){
		this.column_name=column_name;
	}
	/** 取得：创建日期 */
	public String getCreate_date(){
		return create_date;
	}
	/** 设置：创建日期 */
	public void setCreate_date(String create_date){
		this.create_date=create_date;
	}
	/** 取得：创建时间 */
	public String getCreate_time(){
		return create_time;
	}
	/** 设置：创建时间 */
	public void setCreate_time(String create_time){
		this.create_time=create_time;
	}
	/** 取得：创建用户 */
	public String getCreate_user(){
		return create_user;
	}
	/** 设置：创建用户 */
	public void setCreate_user(String create_user){
		this.create_user=create_user;
	}
	/** 取得：最后更新日期 */
	public String getLast_update_date(){
		return last_update_date;
	}
	/** 设置：最后更新日期 */
	public void setLast_update_date(String last_update_date){
		this.last_update_date=last_update_date;
	}
	/** 取得：最后更新时间 */
	public String getLast_update_time(){
		return last_update_time;
	}
	/** 设置：最后更新时间 */
	public void setLast_update_time(String last_update_time){
		this.last_update_time=last_update_time;
	}
	/** 取得：更新用户 */
	public String getUpdate_user(){
		return update_user;
	}
	/** 设置：更新用户 */
	public void setUpdate_user(String update_user){
		this.update_user=update_user;
	}
	/** 取得：分组ID */
	public Long getComponent_group_id(){
		return component_group_id;
	}
	/** 设置：分组ID */
	public void setComponent_group_id(Long component_group_id){
		this.component_group_id=component_group_id;
	}
	/** 设置：分组ID */
	public void setComponent_group_id(String component_group_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(component_group_id)){
			this.component_group_id=new Long(component_group_id);
		}
	}
	/** 取得：组件ID */
	public Long getComponent_id(){
		return component_id;
	}
	/** 设置：组件ID */
	public void setComponent_id(Long component_id){
		this.component_id=component_id;
	}
	/** 设置：组件ID */
	public void setComponent_id(String component_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(component_id)){
			this.component_id=new Long(component_id);
		}
	}
}
