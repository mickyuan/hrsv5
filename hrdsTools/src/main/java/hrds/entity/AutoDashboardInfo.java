package hrds.entity;

import fd.ng.db.entity.TableEntity;
import fd.ng.db.entity.anno.Column;
import fd.ng.db.entity.anno.Table;
import hrds.exception.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 实体类中所有属性都应定义为对象，不要使用int等主类型，方便对null值的操作
 */
@Table(tableName = "auto_dashboard_info")
public class AutoDashboardInfo extends TableEntity {
    private static final long serialVersionUID = 321566870187324L;
	private transient static final Set<String> __PrimaryKeys;
	public static final String TableName = "auto_dashboard_info";

	static {
		Set<String> __tmpPKS = new HashSet<>();
		__tmpPKS.add("dashboard_id");
		__PrimaryKeys = Collections.unmodifiableSet(__tmpPKS);
	}
	/**
	 * 检查给定的名字，是否为主键中的字段
	 * @param name String 检验是否为主键的名字
	 * @return
	 */
	public static boolean isPrimaryKey(String name) { return __PrimaryKeys.contains(name); }
	public static Set<String> getPrimaryKeyNames() { return __PrimaryKeys; }

	private String dashboard_theme;
	private String dashboard_name;
	private String create_time;
	private String bordercolor;
	private String last_update_time;
	private String dashboard_desc;
	private String update_user;
	private String bordertype;
	private String user_id;
	private String borderwidth;
	private String background;
	private String dashboard_status;
	private String create_date;
	private BigDecimal dashboard_id;
	private String last_update_date;

	public String getDashboard_theme() { return dashboard_theme; }
	public void setDashboard_theme(String dashboard_theme) {
		if(dashboard_theme==null) addNullValueField("dashboard_theme");
		this.dashboard_theme = dashboard_theme;
	}

	public String getDashboard_name() { return dashboard_name; }
	public void setDashboard_name(String dashboard_name) {
		if(dashboard_name==null) throw new BusinessException("Entity : AutoDashboardInfo.dashboard_name must not null!");
		this.dashboard_name = dashboard_name;
	}

	public String getCreate_time() { return create_time; }
	public void setCreate_time(String create_time) {
		if(create_time==null) throw new BusinessException("Entity : AutoDashboardInfo.create_time must not null!");
		this.create_time = create_time;
	}

	public String getBordercolor() { return bordercolor; }
	public void setBordercolor(String bordercolor) {
		if(bordercolor==null) addNullValueField("bordercolor");
		this.bordercolor = bordercolor;
	}

	public String getLast_update_time() { return last_update_time; }
	public void setLast_update_time(String last_update_time) {
		if(last_update_time==null) addNullValueField("last_update_time");
		this.last_update_time = last_update_time;
	}

	public String getDashboard_desc() { return dashboard_desc; }
	public void setDashboard_desc(String dashboard_desc) {
		if(dashboard_desc==null) addNullValueField("dashboard_desc");
		this.dashboard_desc = dashboard_desc;
	}

	public String getUpdate_user() { return update_user; }
	public void setUpdate_user(String update_user) {
		if(update_user==null) addNullValueField("update_user");
		this.update_user = update_user;
	}

	public String getBordertype() { return bordertype; }
	public void setBordertype(String bordertype) {
		if(bordertype==null) addNullValueField("bordertype");
		this.bordertype = bordertype;
	}

	public String getUser_id() { return user_id; }
	public void setUser_id(String user_id) {
		if(user_id==null) throw new BusinessException("Entity : AutoDashboardInfo.user_id must not null!");
		this.user_id = user_id;
	}

	public String getBorderwidth() { return borderwidth; }
	public void setBorderwidth(String borderwidth) {
		if(borderwidth==null) addNullValueField("borderwidth");
		this.borderwidth = borderwidth;
	}

	public String getBackground() { return background; }
	public void setBackground(String background) {
		if(background==null) addNullValueField("background");
		this.background = background;
	}

	public String getDashboard_status() { return dashboard_status; }
	public void setDashboard_status(String dashboard_status) {
		if(dashboard_status==null) addNullValueField("dashboard_status");
		this.dashboard_status = dashboard_status;
	}

	public String getCreate_date() { return create_date; }
	public void setCreate_date(String create_date) {
		if(create_date==null) throw new BusinessException("Entity : AutoDashboardInfo.create_date must not null!");
		this.create_date = create_date;
	}

	public BigDecimal getDashboard_id() { return dashboard_id; }
	public void setDashboard_id(BigDecimal dashboard_id) {
		if(dashboard_id==null) throw new BusinessException("Entity : AutoDashboardInfo.dashboard_id must not null!");
		this.dashboard_id = dashboard_id;
	}

	public String getLast_update_date() { return last_update_date; }
	public void setLast_update_date(String last_update_date) {
		if(last_update_date==null) addNullValueField("last_update_date");
		this.last_update_date = last_update_date;
	}

}