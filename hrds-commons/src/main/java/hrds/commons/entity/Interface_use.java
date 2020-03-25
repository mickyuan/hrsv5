package hrds.commons.entity;
/**Auto Created by VBScript Do not modify!*/
import hrds.commons.entity.fdentity.ProjectTableEntity;
import fd.ng.db.entity.anno.Table;
import fd.ng.core.annotation.DocBean;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * 接口使用信息表
 */
@Table(tableName = "interface_use")
public class Interface_use extends ProjectTableEntity
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
	@DocBean(name ="interface_use_id",value="接口使用ID:",dataType = Long.class,required = true)
	private Long interface_use_id;
	@DocBean(name ="interface_name",value="接口名称:",dataType = String.class,required = true)
	private String interface_name;
	@DocBean(name ="their_type",value="接口所属类型(InterfaceType):1-数据类<ShuJuLei> 2-功能类<GongNengLei> 3-报表类<BaoBiaoLei> 4-监控类<JianKongLei> ",dataType = String.class,required = true)
	private String their_type;
	@DocBean(name ="use_state",value="使用状态(InterfaceState):1-启用<QiYong> 2-禁用<JinYong> ",dataType = String.class,required = true)
	private String use_state;
	@DocBean(name ="use_valid_date",value="使用有效日期:",dataType = String.class,required = true)
	private String use_valid_date;
	@DocBean(name ="start_use_date",value="开始使用日期:",dataType = String.class,required = true)
	private String start_use_date;
	@DocBean(name ="interface_note",value="备注:",dataType = String.class,required = false)
	private String interface_note;
	@DocBean(name ="interface_code",value="接口代码:",dataType = String.class,required = true)
	private String interface_code;
	@DocBean(name ="user_name",value="用户名称:",dataType = String.class,required = true)
	private String user_name;
	@DocBean(name ="classify_name",value="分类名称:",dataType = String.class,required = false)
	private String classify_name;
	@DocBean(name ="url",value="请求地址:",dataType = String.class,required = true)
	private String url;
	@DocBean(name ="interface_id",value="接口ID:",dataType = Long.class,required = true)
	private Long interface_id;
	@DocBean(name ="user_id",value="用户ID:",dataType = Long.class,required = true)
	private Long user_id;
	@DocBean(name ="create_id",value="用户ID:",dataType = Long.class,required = true)
	private Long create_id;

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
	/** 取得：用户ID */
	public Long getCreate_id(){
		return create_id;
	}
	/** 设置：用户ID */
	public void setCreate_id(Long create_id){
		this.create_id=create_id;
	}
	/** 设置：用户ID */
	public void setCreate_id(String create_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(create_id)){
			this.create_id=new Long(create_id);
		}
	}
}
