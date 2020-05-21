package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@DocClass(desc = "接口检查参数实体", author = "dhw", createdate = "2020/4/1 15:36")
@Table(tableName = "check_param")
public class CheckParam extends ProjectTableEntity {
	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "check_param";

	@DocBean(name = "token", value = "token值:与user_id,user_password二选一", dataType = String.class,
			required = false)
	private String token;
	@DocBean(name = "user_id", value = "用户ID:与user_password同选", dataType = Long.class, required = false)
	private Long user_id;
	@DocBean(name = "user_password", value = "密码:与user_id同选", dataType = String.class, required = false)
	private String user_password;
	@DocBean(name = "url", value = "请求url地址:", dataType = String.class,required = false)
	private String url;
	@DocBean(name = "interface_code", value = "接口代码:", dataType = String.class, required = false)
	private String interface_code;

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getInterface_code() {
		return interface_code;
	}

	public void setInterface_code(String interface_code) {
		this.interface_code = interface_code;
	}
}
