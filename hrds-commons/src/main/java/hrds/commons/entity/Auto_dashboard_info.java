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
 * 仪表板信息表
 */
@Table(tableName = "auto_dashboard_info")
public class Auto_dashboard_info extends ProjectTableEntity
{
	private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_dashboard_info";
	/**
	* 检查给定的名字，是否为主键中的字段
	* @param name String 检验是否为主键的名字
	* @return
	*/
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); } 
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; } 
	/** 仪表板信息表 */
	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dashboard_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	@DocBean(name ="dashboard_id",value="仪表板id:",dataType = Long.class,required = true)
	private Long dashboard_id;
	@DocBean(name ="dashboard_name",value="仪表板名称:",dataType = String.class,required = false)
	private String dashboard_name;
	@DocBean(name ="dashboard_desc",value="仪表板描述:",dataType = String.class,required = false)
	private String dashboard_desc;
	@DocBean(name ="create_date",value="创建日期:",dataType = String.class,required = true)
	private String create_date;
	@DocBean(name ="create_time",value="创建时间:",dataType = String.class,required = true)
	private String create_time;
	@DocBean(name ="last_update_date",value="最后更新日期:",dataType = String.class,required = true)
	private String last_update_date;
	@DocBean(name ="last_update_time",value="最后更新时间:",dataType = String.class,required = true)
	private String last_update_time;
	@DocBean(name ="dashboard_theme",value="仪表板主题:",dataType = String.class,required = false)
	private String dashboard_theme;
	@DocBean(name ="bordertype",value="边框类型:",dataType = String.class,required = false)
	private String bordertype;
	@DocBean(name ="bordercolor",value="边框颜色:",dataType = String.class,required = false)
	private String bordercolor;
	@DocBean(name ="borderwidth",value="边框宽度:",dataType = String.class,required = false)
	private String borderwidth;
	@DocBean(name ="dashboard_status",value="仪表盘发布状态:",dataType = String.class,required = false)
	private String dashboard_status;
	@DocBean(name ="background",value="背景色:",dataType = String.class,required = false)
	private String background;
	@DocBean(name ="user_id",value="用户ID:",dataType = Long.class,required = true)
	private Long user_id;
	@DocBean(name ="update_user",value="用户ID:",dataType = Long.class,required = true)
	private Long update_user;

	/** 取得：仪表板id */
	public Long getDashboard_id(){
		return dashboard_id;
	}
	/** 设置：仪表板id */
	public void setDashboard_id(Long dashboard_id){
		this.dashboard_id=dashboard_id;
	}
	/** 设置：仪表板id */
	public void setDashboard_id(String dashboard_id){
		if(!fd.ng.core.utils.StringUtil.isEmpty(dashboard_id)){
			this.dashboard_id=new Long(dashboard_id);
		}
	}
	/** 取得：仪表板名称 */
	public String getDashboard_name(){
		return dashboard_name;
	}
	/** 设置：仪表板名称 */
	public void setDashboard_name(String dashboard_name){
		this.dashboard_name=dashboard_name;
	}
	/** 取得：仪表板描述 */
	public String getDashboard_desc(){
		return dashboard_desc;
	}
	/** 设置：仪表板描述 */
	public void setDashboard_desc(String dashboard_desc){
		this.dashboard_desc=dashboard_desc;
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
	/** 取得：仪表板主题 */
	public String getDashboard_theme(){
		return dashboard_theme;
	}
	/** 设置：仪表板主题 */
	public void setDashboard_theme(String dashboard_theme){
		this.dashboard_theme=dashboard_theme;
	}
	/** 取得：边框类型 */
	public String getBordertype(){
		return bordertype;
	}
	/** 设置：边框类型 */
	public void setBordertype(String bordertype){
		this.bordertype=bordertype;
	}
	/** 取得：边框颜色 */
	public String getBordercolor(){
		return bordercolor;
	}
	/** 设置：边框颜色 */
	public void setBordercolor(String bordercolor){
		this.bordercolor=bordercolor;
	}
	/** 取得：边框宽度 */
	public String getBorderwidth(){
		return borderwidth;
	}
	/** 设置：边框宽度 */
	public void setBorderwidth(String borderwidth){
		this.borderwidth=borderwidth;
	}
	/** 取得：仪表盘发布状态 */
	public String getDashboard_status(){
		return dashboard_status;
	}
	/** 设置：仪表盘发布状态 */
	public void setDashboard_status(String dashboard_status){
		this.dashboard_status=dashboard_status;
	}
	/** 取得：背景色 */
	public String getBackground(){
		return background;
	}
	/** 设置：背景色 */
	public void setBackground(String background){
		this.background=background;
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
