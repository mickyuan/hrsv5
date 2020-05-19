package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;

@DocClass(desc = "查询接口信息实体类", author = "dhw", createdate = "2020/3/30 16:12")
@Table(tableName = "query_interface_info")
public class QueryInterfaceInfo {

	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "query_interface_info";

	@DocBean(name = "token", value = "token值:", dataType = String.class, required = true)
	private String token;
	@DocBean(name = "user_id", value = "用户ID:", dataType = Long.class, required = true)
	private Long user_id;
	@DocBean(name = "user_password", value = "密码:", dataType = String.class, required = true)
	private String user_password;
	@DocBean(name = "user_name", value = "用户名:", dataType = String.class, required = true)
	private String user_name;
	@DocBean(name = "url", value = "接口请求地址:", dataType = String.class, required = true)
	private String url;
	@DocBean(name = "start_use_date", value = "接口开始使用日期:yyyyMMdd格式", dataType = String.class,
			required = true)
	private String start_use_date;
	@DocBean(name = "use_valid_date", value = "接口有效日期:yyyyMMdd格式", dataType = String.class, required = true)
	private String use_valid_date;
	@DocBean(name = "use_state", value = "接口状态:", dataType = String.class, required = true)
	private String use_state;
	@DocBean(name = "sysreg_name", value = "原始登记表名称:", dataType = String.class, required = true)
	private String sysreg_name;
	@DocBean(name = "table_ch_column", value = "表字段列中文名称:", dataType = String.class, required = true)
	private String table_ch_column;
	@DocBean(name = "table_en_column", value = "表字段列英文名称:", dataType = String.class, required = true)
	private String table_en_column;
	@DocBean(name = "table_type_name", value = "表remark列(其实存的是字段类型对应的json字符串):", dataType = String.class, required = true)
	private String table_type_name;
	@DocBean(name = "original_name", value = "表中文名:", dataType = String.class, required = true)
	private String original_name;
	@DocBean(name = "table_blsystem", value = "表来源:", dataType = String.class, required = true)
	private String table_blsystem;
	@DocBean(name = "interface_name", value = "接口名称:", dataType = String.class, required = true)
	private String interface_name;
	@DocBean(name = "interface_id", value = "接口ID:", dataType = String.class, required = true)
	private String interface_id;
	@DocBean(name = "use_id", value = "表使用ID:", dataType = String.class, required = true)
	private String use_id;
	@DocBean(name = "interface_use_id", value = "接口使用ID:", dataType = String.class, required = true)
	private String interface_use_id;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getUser_password() {
		return user_password;
	}

	public void setUser_password(String user_password) {
		this.user_password = user_password;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getStart_use_date() {
		return start_use_date;
	}

	public void setStart_use_date(String start_use_date) {
		this.start_use_date = start_use_date;
	}

	public String getUse_valid_date() {
		return use_valid_date;
	}

	public void setUse_valid_date(String use_valid_date) {
		this.use_valid_date = use_valid_date;
	}

	public String getUse_state() {
		return use_state;
	}

	public void setUse_state(String use_state) {
		this.use_state = use_state;
	}

	public String getSysreg_name() {
		return sysreg_name;
	}

	public void setSysreg_name(String sysreg_name) {
		this.sysreg_name = sysreg_name;
	}

	public String getTable_ch_column() {
		return table_ch_column;
	}

	public void setTable_ch_column(String table_ch_column) {
		this.table_ch_column = table_ch_column;
	}

	public String getTable_en_column() {
		return table_en_column;
	}

	public void setTable_en_column(String table_en_column) {
		this.table_en_column = table_en_column;
	}

	public String getTable_type_name() {
		return table_type_name;
	}

	public void setTable_type_name(String table_type_name) {
		this.table_type_name = table_type_name;
	}

	public String getOriginal_name() {
		return original_name;
	}

	public void setOriginal_name(String original_name) {
		this.original_name = original_name;
	}

	public String getTable_blsystem() {
		return table_blsystem;
	}

	public void setTable_blsystem(String table_blsystem) {
		this.table_blsystem = table_blsystem;
	}

	public String getInterface_name() {
		return interface_name;
	}

	public void setInterface_name(String interface_name) {
		this.interface_name = interface_name;
	}

	public String getInterface_id() {
		return interface_id;
	}

	public void setInterface_id(String interface_id) {
		this.interface_id = interface_id;
	}

	public String getUse_id() {
		return use_id;
	}

	public void setUse_id(String use_id) {
		this.use_id = use_id;
	}

	public String getInterface_use_id() {
		return interface_use_id;
	}

	public void setInterface_use_id(String interface_use_id) {
		this.interface_use_id = interface_use_id;
	}

}
