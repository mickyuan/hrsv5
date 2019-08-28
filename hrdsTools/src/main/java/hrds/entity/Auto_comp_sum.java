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
 * 组件汇总表
 */
@Table(tableName = "auto_comp_sum")
public class Auto_comp_sum extends TableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_comp_sum";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 组件汇总表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("component_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	private String component_name; //组件名称
	private String component_desc; //组件描述
	private String data_source; //数据来源
	private String component_status; //组件状态
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String create_user; //创建用户
	private String last_update_date; //最后更新日期
	private String last_update_time; //最后更新时间
	private String update_user; //更新用户
	private Long component_id; //组件ID
	private String user_id; //用户ID
	private String sources_obj; //数据源对象
	private String exe_sql; //执行sql
	private String chart_type; //图表类型
	private String chart_theme; //图形主题
	private String background; //背景色
	private String component_buffer; //组件缓存
	private String condition_sql; //条件SQL

	/** 取得：组件名称 */
	public String getComponent_name(){
		return component_name;
	}
	/** 设置：组件名称 */
	public void setComponent_name(String component_name){
		this.component_name=component_name;
	}
	/** 取得：组件描述 */
	public String getComponent_desc(){
		return component_desc;
	}
	/** 设置：组件描述 */
	public void setComponent_desc(String component_desc){
		this.component_desc=component_desc;
	}
	/** 取得：数据来源 */
	public String getData_source(){
		return data_source;
	}
	/** 设置：数据来源 */
	public void setData_source(String data_source){
		this.data_source=data_source;
	}
	/** 取得：组件状态 */
	public String getComponent_status(){
		return component_status;
	}
	/** 设置：组件状态 */
	public void setComponent_status(String component_status){
		this.component_status=component_status;
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
	/** 取得：用户ID */
	public String getUser_id(){
		return user_id;
	}
	/** 设置：用户ID */
	public void setUser_id(String user_id){
		this.user_id=user_id;
	}
	/** 取得：数据源对象 */
	public String getSources_obj(){
		return sources_obj;
	}
	/** 设置：数据源对象 */
	public void setSources_obj(String sources_obj){
		this.sources_obj=sources_obj;
	}
	/** 取得：执行sql */
	public String getExe_sql(){
		return exe_sql;
	}
	/** 设置：执行sql */
	public void setExe_sql(String exe_sql){
		this.exe_sql=exe_sql;
	}
	/** 取得：图表类型 */
	public String getChart_type(){
		return chart_type;
	}
	/** 设置：图表类型 */
	public void setChart_type(String chart_type){
		this.chart_type=chart_type;
	}
	/** 取得：图形主题 */
	public String getChart_theme(){
		return chart_theme;
	}
	/** 设置：图形主题 */
	public void setChart_theme(String chart_theme){
		this.chart_theme=chart_theme;
	}
	/** 取得：背景色 */
	public String getBackground(){
		return background;
	}
	/** 设置：背景色 */
	public void setBackground(String background){
		this.background=background;
	}
	/** 取得：组件缓存 */
	public String getComponent_buffer(){
		return component_buffer;
	}
	/** 设置：组件缓存 */
	public void setComponent_buffer(String component_buffer){
		this.component_buffer=component_buffer;
	}
	/** 取得：条件SQL */
	public String getCondition_sql(){
		return condition_sql;
	}
	/** 设置：条件SQL */
	public void setCondition_sql(String condition_sql){
		this.condition_sql=condition_sql;
	}
}
