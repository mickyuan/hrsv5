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
 * 模板信息表
 */
@Table(tableName = "auto_tp_info")
public class Auto_tp_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_tp_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 模板信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("template_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="template_name",value="模板名称:",dataType = String.class,required = true)
	private String template_name;
	@DocBean(name ="template_desc",value="模板描述:",dataType = String.class,required = false)
	private String template_desc;
	@DocBean(name ="data_source",value="是否为外部数据(IsFlag):1-是<Shi> 0-否<Fou> ",dataType = String.class,required = true)
	private String data_source;
	@DocBean(name ="template_sql",value="模板sql语句:",dataType = String.class,required = true)
	private String template_sql;
	@DocBean(name ="template_status",value="模板状态(AutoTemplateStatus):01-编辑<BianJi> 04-发布<FaBu> 05-注销<ZhuXiao> ",dataType = String.class,required = true)
	private String template_status;
	@DocBean(name ="create_date",value="创建日期:",dataType = String.class,required = true)
	private String create_date;
	@DocBean(name ="create_time",value="创建时间:",dataType = String.class,required = true)
	private String create_time;
	@DocBean(name ="last_update_date",value="最后更新日期:",dataType = String.class,required = false)
	private String last_update_date;
	@DocBean(name ="last_update_time",value="最后更新时间:",dataType = String.class,required = false)
	private String last_update_time;
	@DocBean(name ="template_id",value="模板ID:",dataType = Long.class,required = true)
	private Long template_id;
	@DocBean(name ="update_user",value="用户ID:",dataType = Long.class,required = false)
	private Long update_user;
	@DocBean(name ="create_user",value="用户ID:",dataType = Long.class,required = true)
	private Long create_user;

	/** 取得：模板名称 */
	public String getTemplate_name(){
		return template_name;
	}
	/** 设置：模板名称 */
	public void setTemplate_name(String template_name){
		this.template_name=template_name;
	}
	/** 取得：模板描述 */
	public String getTemplate_desc(){
		return template_desc;
	}
	/** 设置：模板描述 */
	public void setTemplate_desc(String template_desc){
		this.template_desc=template_desc;
	}
	/** 取得：是否为外部数据 */
	public String getData_source(){
		return data_source;
	}
	/** 设置：是否为外部数据 */
	public void setData_source(String data_source){
		this.data_source=data_source;
	}
	/** 取得：模板sql语句 */
	public String getTemplate_sql(){
		return template_sql;
	}
	/** 设置：模板sql语句 */
	public void setTemplate_sql(String template_sql){
		this.template_sql=template_sql;
	}
	/** 取得：模板状态 */
	public String getTemplate_status(){
		return template_status;
	}
	/** 设置：模板状态 */
	public void setTemplate_status(String template_status){
		this.template_status=template_status;
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
	/** 取得：模板ID */
	public Long getTemplate_id(){
		return template_id;
	}
	/** 设置：模板ID */
	public void setTemplate_id(Long template_id){
		this.template_id=template_id;
	}
	/** 设置：模板ID */
	public void setTemplate_id(String template_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(template_id)){
			this.template_id=new Long(template_id);
		}
	}
	/** 取得：用户ID */
	public Long getUpdate_user(){
		return update_user;
	}
	/** 设置：用户ID */
	public void setUpdate_user(Long update_user){
		this.update_user=update_user;
	}
	/** 设置：用户ID */
	public void setUpdate_user(String update_user){
		if(!fd.ng.core.utils.StringUtil.isEmpty(update_user)){
			this.update_user=new Long(update_user);
		}
	}
	/** 取得：用户ID */
	public Long getCreate_user(){
		return create_user;
	}
	/** 设置：用户ID */
	public void setCreate_user(Long create_user){
		this.create_user=create_user;
	}
	/** 设置：用户ID */
	public void setCreate_user(String create_user){
		if(!fd.ng.core.utils.StringUtil.isEmpty(create_user)){
			this.create_user=new Long(create_user);
		}
	}
}
