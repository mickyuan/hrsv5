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
 * 接口使用信息表
 */
@Table(tableName = "interface_use")
public class Interface_use extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "interface_use";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 接口使用信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("interface_use_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private Long user_id; //用户ID
	private String classify_name; //分类名称
	private String url; //请求地址
	private Long create_id; //创建者id
	private Long interface_use_id; //接口使用ID
	private String interface_name; //接口名称
	private String their_type; //接口所属类型
	private String use_state; //使用状态
	private String use_valid_date; //使用有效日期
	private String start_use_date; //开始使用日期
	private String interface_note; //备注
	private String interface_code; //接口代码
	private String user_name; //用户名称
	private Long interface_id; //接口ID

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
	/** 取得：分类名称 */
	public String getClassify_name(){
		return classify_name;
	}
	/** 设置：分类名称 */
	public void setClassify_name(String classify_name){
		this.classify_name=classify_name;
	}
	/** 取得：请求地址 */
	public String getUrl(){
		return url;
	}
	/** 设置：请求地址 */
	public void setUrl(String url){
		this.url=url;
	}
	/** 取得：创建者id */
	public Long getCreate_id(){
		return create_id;
	}
	/** 设置：创建者id */
	public void setCreate_id(Long create_id){
		this.create_id=create_id;
	}
	/** 设置：创建者id */
	public void setCreate_id(String create_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(create_id)){
			this.create_id=new Long(create_id);
		}
	}
	/** 取得：接口使用ID */
	public Long getInterface_use_id(){
		return interface_use_id;
	}
	/** 设置：接口使用ID */
	public void setInterface_use_id(Long interface_use_id){
		this.interface_use_id=interface_use_id;
	}
	/** 设置：接口使用ID */
	public void setInterface_use_id(String interface_use_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(interface_use_id)){
			this.interface_use_id=new Long(interface_use_id);
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
	/** 取得：接口所属类型 */
	public String getTheir_type(){
		return their_type;
	}
	/** 设置：接口所属类型 */
	public void setTheir_type(String their_type){
		this.their_type=their_type;
	}
	/** 取得：使用状态 */
	public String getUse_state(){
		return use_state;
	}
	/** 设置：使用状态 */
	public void setUse_state(String use_state){
		this.use_state=use_state;
	}
	/** 取得：使用有效日期 */
	public String getUse_valid_date(){
		return use_valid_date;
	}
	/** 设置：使用有效日期 */
	public void setUse_valid_date(String use_valid_date){
		this.use_valid_date=use_valid_date;
	}
	/** 取得：开始使用日期 */
	public String getStart_use_date(){
		return start_use_date;
	}
	/** 设置：开始使用日期 */
	public void setStart_use_date(String start_use_date){
		this.start_use_date=start_use_date;
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
	/** 取得：用户名称 */
	public String getUser_name(){
		return user_name;
	}
	/** 设置：用户名称 */
	public void setUser_name(String user_name){
		this.user_name=user_name;
	}
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
}
