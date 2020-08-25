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
 * 组件汇总表
 */
@Table(tableName = "auto_comp_sum")
public class Auto_comp_sum extends ProjectTableEntity
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
	@DocBean(name ="component_name",value="组件名称:",dataType = String.class,required = false)
	private String component_name;
	@DocBean(name ="component_desc",value="组件描述:",dataType = String.class,required = false)
	private String component_desc;
	@DocBean(name ="data_source",value="数据来源(AutoSourceObject):01-自主数据数据集<ZiZhuShuJuShuJuJi> 02-系统级数据集<XiTongJiShuJuJi> 03-数据组件数据集<ShuJuZuJianShuJuJi> ",dataType = String.class,required = true)
	private String data_source;
	@DocBean(name ="component_status",value="组件状态:",dataType = String.class,required = true)
	private String component_status;
	@DocBean(name ="create_date",value="创建日期:",dataType = String.class,required = true)
	private String create_date;
	@DocBean(name ="create_time",value="时间:",dataType = String.class,required = true)
	private String create_time;
	@DocBean(name ="last_update_date",value="最后更新日期:",dataType = String.class,required = true)
	private String last_update_date;
	@DocBean(name ="last_update_time",value="最后更新时间:",dataType = String.class,required = true)
	private String last_update_time;
	@DocBean(name ="component_id",value="组件ID:",dataType = Long.class,required = true)
	private Long component_id;
	@DocBean(name ="sources_obj",value="数据源对象(AutoSourceObject):01-自主数据数据集<ZiZhuShuJuShuJuJi> 02-系统级数据集<XiTongJiShuJuJi> 03-数据组件数据集<ShuJuZuJianShuJuJi> ",dataType = String.class,required = false)
	private String sources_obj;
	@DocBean(name ="exe_sql",value="执行sql:",dataType = String.class,required = false)
	private String exe_sql;
	@DocBean(name ="chart_type",value="图表类型:",dataType = String.class,required = false)
	private String chart_type;
	@DocBean(name ="chart_theme",value="图形主题:",dataType = String.class,required = false)
	private String chart_theme;
	@DocBean(name ="background",value="背景色:",dataType = String.class,required = false)
	private String background;
	@DocBean(name ="component_buffer",value="组件缓存:",dataType = String.class,required = false)
	private String component_buffer;
	@DocBean(name ="condition_sql",value="条件SQL:",dataType = String.class,required = true)
	private String condition_sql;
	@DocBean(name ="user_id",value="用户ID:",dataType = Long.class,required = true)
	private Long user_id;
	@DocBean(name ="create_user",value="用户ID:",dataType = Long.class,required = true)
	private Long create_user;
	@DocBean(name ="update_user",value="用户ID:",dataType = Long.class,required = true)
	private Long update_user;

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
	/** 取得：时间 */
	public String getCreate_time(){
		return create_time;
	}
	/** 设置：时间 */
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
}
