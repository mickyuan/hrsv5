package hrds.g.biz.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.DocClass;
import fd.ng.db.entity.anno.Table;

@DocClass(desc = "token实体对象", author = "dhw", createdate = "2020/3/30 16:50")
@Table(tableName = "token_model")
public class TokenModel {

	private static final long serialVersionUID = 321566870187324L;

	public static final String TableName = "table_data_info";

	public TokenModel(Long user_id, String token) {
		this.user_id = user_id;
		this.token = token;
	}

	@DocBean(name = "user_id", value = "用户ID:", dataType = Long.class, required = true)
	private Long user_id;
	@DocBean(name = "user_id", value = "随机生成的token:", dataType = String.class, required = true)
	private String token;

	public Long getUserId() {
		return user_id;
	}

	public void setUserId(Long user_id) {
		this.user_id = user_id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}

