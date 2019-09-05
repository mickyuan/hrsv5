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
 * 接口信息表
 */
@Table(tableName = "interface_info")
public class Interface_info extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "interface_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 接口信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("interface_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long interface_id; //接口ID
	private String interface_name; //接口名称
	private String interface_type; //接口类型
	private String interface_state; //接口状态
	private String interface_note; //备注
	private String interface_code; //接口代码
	private Long user_id; //用户ID
	private String url; //请求地址

	/** 取得：接口ID */
	public Long getInterface_id(){
		return interface_id;
	}
	/** 设置：接口ID */
	public void setInterface_id(Long interface_id){
		this.interface_id=interface_id;
	}
	/** 设置：接口ID */
	public void setInterface_id(String interface_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(interface_id)){
			this.interface_id=new Long(interface_id);
		}
	}
	/** 取得：接口名称 */
	public String getInterface_name(){
		return interface_name;
	}
	/** 设置：接口名称 */
	public void setInterface_name(String interface_name){
		this.interface_name=interface_name;
	}
	/** 取得：接口类型 */
	public String getInterface_type(){
		return interface_type;
	}
	/** 设置：接口类型 */
	public void setInterface_type(String interface_type){
		this.interface_type=interface_type;
	}
	/** 取得：接口状态 */
	public String getInterface_state(){
		return interface_state;
	}
	/** 设置：接口状态 */
	public void setInterface_state(String interface_state){
		this.interface_state=interface_state;
	}
	/** 取得：备注 */
	public String getInterface_note(){
		return interface_note;
	}
	/** 设置：备注 */
	public void setInterface_note(String interface_note){
		this.interface_note=interface_note;
	}
	/** 取得：接口代码 */
	public String getInterface_code(){
		return interface_code;
	}
	/** 设置：接口代码 */
	public void setInterface_code(String interface_code){
		this.interface_code=interface_code;
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
	/** 取得：请求地址 */
	public String getUrl(){
		return url;
	}
	/** 设置：请求地址 */
	public void setUrl(String url){
		this.url=url;
	}
}
