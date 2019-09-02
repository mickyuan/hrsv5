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
 * 模板信息表
 */
@Table(tableName = "auto_tp_info")
public class Auto_tp_info extends TableEntity
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
	private String template_name; //模板名称
	private String template_desc; //模板描述
	private String data_source; //数据来源
	private String template_sql; //模板sql语句
	private String template_status; //模板状态
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String create_user; //创建用户
	private String last_update_date; //最后更新日期
	private String last_update_time; //最后更新时间
	private String update_user; //更新用户
	private Long template_id; //模板ID

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
	/** 取得：数据来源 */
	public String getData_source(){
		return data_source;
	}
	/** 设置：数据来源 */
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
}
