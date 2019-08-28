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
 * 仪表板信息表
 */
@Table(tableName = "auto_dashboard_info")
public class Auto_dashboard_info extends TableEntity
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
	private Long dashboard_id; //仪表板id
	private String user_id; //创建用户
	private String dashboard_name; //仪表板名称
	private String dashboard_desc; //仪表板描述
	private String create_date; //创建日期
	private String create_time; //创建时间
	private String last_update_date; //最后更新日期
	private String last_update_time; //最后更新时间
	private String update_user; //更新用户
	private String dashboard_theme; //仪表板主题
	private String bordertype; //边框类型
	private String bordercolor; //边框颜色
	private String borderwidth; //边框宽度
	private String dashboard_status; //仪表盘发布状态
	private String background; //背景色

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
	/** 取得：创建用户 */
	public String getUser_id(){
		return user_id;
	}
	/** 设置：创建用户 */
	public void setUser_id(String user_id){
		this.user_id=user_id;
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
	/** 取得：更新用户 */
	public String getUpdate_user(){
		return update_user;
	}
	/** 设置：更新用户 */
	public void setUpdate_user(String update_user){
		this.update_user=update_user;
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
}
