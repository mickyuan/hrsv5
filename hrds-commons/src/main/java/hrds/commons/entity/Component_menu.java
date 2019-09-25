package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import fd.ng.db.entity.TableEntity;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import fd.ng.core.docannotation.DocBean;
import hrds.commons.exception.BusinessException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * 组件菜单表
 */
@Table(tableName = "component_menu")
public class Component_menu extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "component_menu";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 组件菜单表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("menu_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="menu_id",value="主键菜单id",dataType = Long.class,required = true)
	private Long menu_id;
	@DocBean(name ="menu_path",value="菜单path",dataType = String.class,required = true)
	private String menu_path;
	@DocBean(name ="user_type",value="用户类型",dataType = String.class,required = true)
	private String user_type;
	@DocBean(name ="menu_name",value="菜单名称",dataType = String.class,required = true)
	private String menu_name;
	@DocBean(name ="comp_id",value="组件编号",dataType = String.class,required = true)
	private String comp_id;
	@DocBean(name ="menu_remark",value="备注",dataType = String.class,required = false)
	private String menu_remark;

	/** 取得：主键菜单id */
	public Long getMenu_id(){
		return menu_id;
	}
	/** 设置：主键菜单id */
	public void setMenu_id(Long menu_id){
		this.menu_id=menu_id;
	}
	/** 设置：主键菜单id */
	public void setMenu_id(String menu_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(menu_id)){
			this.menu_id=new Long(menu_id);
		}
	}
	/** 取得：菜单path */
	public String getMenu_path(){
		return menu_path;
	}
	/** 设置：菜单path */
	public void setMenu_path(String menu_path){
		this.menu_path=menu_path;
	}
	/** 取得：用户类型 */
	public String getUser_type(){
		return user_type;
	}
	/** 设置：用户类型 */
	public void setUser_type(String user_type){
		this.user_type=user_type;
	}
	/** 取得：菜单名称 */
	public String getMenu_name(){
		return menu_name;
	}
	/** 设置：菜单名称 */
	public void setMenu_name(String menu_name){
		this.menu_name=menu_name;
	}
	/** 取得：组件编号 */
	public String getComp_id(){
		return comp_id;
	}
	/** 设置：组件编号 */
	public void setComp_id(String comp_id){
		this.comp_id=comp_id;
	}
	/** 取得：备注 */
	public String getMenu_remark(){
		return menu_remark;
	}
	/** 设置：备注 */
	public void setMenu_remark(String menu_remark){
		this.menu_remark=menu_remark;
	}
}
