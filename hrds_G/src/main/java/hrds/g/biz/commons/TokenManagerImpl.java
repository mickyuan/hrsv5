package hrds.g.biz.commons;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.g.biz.bean.TokenModel;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;
import java.util.Map;

@DocClass(desc = "token管理实现类", author = "dhw", createdate = "2020/3/30 17:51")
public class TokenManagerImpl implements TokenManager {

	private static final Long ENDTIME = System.currentTimeMillis();

	@Method(desc = "创建token", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.使用用户名和密码的MD5作为源token" +
			"3.判断数据库中token值是否为默认值,如果是创建token并更新数据库" +
			"4.返回token实体对象")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "token", desc = "根据生成token接口获取", range = "不为空")
	@Override
	public TokenModel createToken(DatabaseWrapper db, Long user_id, String pwd) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.使用用户名和密码的MD5作为源token
		String defaultToken = TokenManagerImpl.getDefaultToken(db, user_id, pwd);
		TokenModel model;
		// 3.判断数据库中token值是否为默认值,如果是创建token并更新数据库,这里不能用代码项的方式去判断
		if (defaultToken.equals(IsFlag.Fou.getCode())) {
			String token = DigestUtils.md5Hex(user_id + pwd + System.currentTimeMillis());
			model = new TokenModel(user_id, token);
			// 3.1创建token
			create(db, user_id, token);
		} else {
			model = new TokenModel(user_id, defaultToken);
		}
		// 4.返回token实体对象
		return model;
	}

	@Method(desc = "判断token是否还有效", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.判断token值是否存在" +
			"3.获取token有效期" +
			"4.判断token是否还有效，有效更新并返回true，无效返回false" +
			"5.更新插入库")
	@Param(name = "token", desc = "根据生成token接口获取", range = "不为空")
	@Return(desc = "返回token是否还有效标志", range = "true有效，false无效")
	public boolean checkToken(DatabaseWrapper db, String token) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.判断token值是否存在
		TokenManagerImpl.isTokenExist(db, token);
		// 3.获取token有效期
		String oldValidTime = TokenManagerImpl.getValidTime(db, token);
		// 4.判断token是否还有效，有效更新并返回true，无效返回false
		if (ENDTIME < Long.parseLong(oldValidTime)) {
			// 5.更新插入库
			TokenManagerImpl.updateValidTime(db, token);
			return true;
		} else {
			return false;
		}
	}

	@Method(desc = "删除token", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.将token更新为默认值")
	@Param(name = "token", desc = "根据生成token接口获取", range = "不为空")
	@Override
	public void deleteToken(DatabaseWrapper db, String token) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.将token更新为默认值
		updateTokenToDefault(db, token);

	}

	@Method(desc = "更新token", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.更新token")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "token", desc = "根据生成token接口获取", range = "不为空")
	public void create(DatabaseWrapper db, Long user_id, String token) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		Sys_user user = new Sys_user();
		user.setToken(token);
		user.setValid_time(String.valueOf(System.currentTimeMillis() + 2 * 60 * 60 * 1000));
		user.setUser_id(user_id);
		// 2.更新token
		user.update(db);
	}

	@Method(desc = "更新token有效时间", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.更新token有效时间")
	@Param(name = "token", desc = "根据生成token接口获取", range = "不为空")
	private static void updateValidTime(DatabaseWrapper db, String token) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.更新token有效时间
		SqlOperator.execute(db, "update " + Sys_user.TableName + "  set valid_time=? " +
				"WHERE token=?", System.currentTimeMillis() + 2 * 60 * 60 * 1000, token);
	}

	@Method(desc = "将token更新为默认值", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.2.更新token值为默认值")
	@Param(name = "token", desc = "根据生成token接口获取", range = "不为空")
	private static void updateTokenToDefault(DatabaseWrapper db, String token) {
		// 1.数据可访问权限处理方式：该方法通不需要进行访问权限限制
		// 2.更新token值为默认值
		SqlOperator.execute(db, "update " + Sys_user.TableName + " set token = ?" +
				" WHERE token = ?", IsFlag.Fou.getCode(), token);
	}

	@Method(desc = "判断token值是否存在", logicStep = "1.数据可访问权限处理方式：该方法通不需要进行访问权限限制" +
			"2.判断token值是否存在")
	@Param(name = "token", desc = "通过生成token接口获得", range = "无限制")
	private static void isTokenExist(DatabaseWrapper db, String token) {
		// 1.数据可访问权限处理方式：该方法通不需要进行访问权限限制
		// 2.判断token值是否存在
		if (SqlOperator.queryNumber(db, "select count(*) FROM "
				+ Sys_user.TableName + " WHERE  token = ?", token)
				.orElseThrow(() -> new BusinessException("sql查询错误")) == 0) {
			throw new BusinessException("token值不能为空");
		}
	}

	@Method(desc = "获取默认token值",
			logicStep = "1.数据可访问权限处理方式：该方法通不需要进行访问权限限制" +
					"2.2.查询token值与token有效时间" +
					"3.判断token值与token有效时间是否为空，不为空返回查询token值，为空给默认值")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "user_password", desc = "密码", range = "新增用户时生成")
	@Return(desc = "返回token值", range = "无限制")
	private static String getDefaultToken(DatabaseWrapper db, Long user_id, String user_password) {
		// 1.数据可访问权限处理方式：该方法通不需要进行访问权限限制
		// 2.查询token值与token有效时间
		Map<String, Object> userMap =
				SqlOperator.queryOneObject(db, "select token,valid_time FROM " + Sys_user.TableName +
						" WHERE user_id=? and user_password=?", user_id, user_password);
		// 3.判断token值与token有效时间是否为空，不为空返回查询token值，为空给默认值
		if (!userMap.isEmpty()) {
			String token = userMap.get("token").toString();
			// token有效期
			String valid_time = userMap.get("valid_time").toString();
			// 如果当期时间大于有效期，重新赋值
			if (System.currentTimeMillis() > Long.parseLong(valid_time)) {
				return IsFlag.Fou.getCode();
			}
			return token;
		} else {
			return IsFlag.Fou.getCode();
		}
	}

	@Method(desc = "查询token有效时间",
			logicStep = "1.数据可访问权限处理方式：该方法通不需要进行访问权限限制" +
					"2.查询token有效时间" +
					"3.判断token有效时间是否为空，为空返回空字符串，不为空返回查询结果")
	@Param(name = "token", desc = "通过生成token接口获得", range = "无限制")
	@Return(desc = "返回查询token有效时间", range = "无限制")
	private static String getValidTime(DatabaseWrapper db, String token) {
		// 1.数据可访问权限处理方式：该方法通不需要进行访问权限限制
		// 2.查询token有效时间
		List<Object> columnList = SqlOperator.queryOneColumnList(db, "select valid_time FROM "
				+ Sys_user.TableName
				+ "  WHERE token = ?", token);
		// 3.判断token有效时间是否为空，为空返回空字符串，不为空返回查询结果
		if (!columnList.isEmpty()) {
			return columnList.get(0).toString();
		} else {
			return "";
		}
	}
}
