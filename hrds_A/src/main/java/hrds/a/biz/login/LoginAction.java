package hrds.a.biz.login;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.web.action.ActionResult;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.UserType;
import hrds.commons.entity.Component_menu;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.commons.exception.ExceptionEnum;
import hrds.commons.utils.ActionUtil;
import hrds.commons.utils.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@DocClass(desc = "登陆认证", author = "Mr.Lee")
public class LoginAction extends BaseAction {

	@Method(desc = "用户登陆入口",
			logicStep = "1 : 获取并检查,用户名是否为空" +
					"2 : 获取并检查,密码是否做为空" +
					"3 : 检查当前用户的登陆是否正确,并返回cookie数据信息")
	@Param(name = "request", desc = "前端登陆提交的请求", range = "user_id : 登陆用户的ID,password : 登陆用户的密码")
	@Return(desc = "登陆用户的加密信息Cookie", range = "不能为空,为空则表示用户无效")
	public String login(HttpServletRequest request) {

		//1 : 获取并检查,用户名是否为空
		String user_id = request.getParameter("user_id");
		if (StringUtil.isBlank(user_id)) {
			throw new BusinessException(ExceptionEnum.USER_NOT_EMPTY.getMessage());
		}
		//2 : 获取并检查,密码是否做为空
		String password = request.getParameter("password");
		if (StringUtil.isBlank(password)) {
			throw new BusinessException(ExceptionEnum.USER_PWD_EMPTY.getMessage());
		}

		//3 : 检查当前用户的登陆是否正确,并返回cookie数据信息
		return checkLogin(Long.parseLong(user_id), password);

	}

	@Method(desc = "重写登陆时的用户验证情况(这里不希望父类的方法做SESSION的管理认证,因为所有Action中的方法请求时,都会做session的认证,而登陆不需要)",
			logicStep = "重写父类的方法")
	@Return(desc = "返回 null", range = "可以为空,因为此处为登陆验证,不需要取Cookie进行验证")
	@Override
	protected ActionResult _doPreProcess(HttpServletRequest request) {

		return null;
	}

	@Method(desc = "检查当前用户的登陆是否正确",
			logicStep = "1 : 查询当前用户的记录信息" +
					"2 : 如果当前包装的值为 null，那么抛出异常,说明当前用户不存在" +
					"3 : 如果为获取到信息,则判断用户密码是否正确" +
					"4 : 将此次登陆的用户信息,设置到Cookie中" +
					"5 : 设置登陆用户的cookie,并返回加密后Cookie信息")
	@Param(name = "user_id", desc = "用户登陆ID", range = "不能为空的整数")
	@Param(name = "password", desc = "用户登陆密码", range = "不能为空的字符串")
	@Return(desc = "加密后的Cookie信息", range = "不能为空,为空则表示用户无效")
	private String checkLogin(long user_id, String password) {

		//1 : 查询当前用户的记录信息
		Sys_user logInUser = Dbo.queryOneObject(Sys_user.class, "SELECT * FROM " +
				Sys_user.TableName + " WHERE user_id = ?", user_id)
				.orElseThrow(() -> new BusinessException(ExceptionEnum.USER_NOT_EXISTS.getMessage()));

		//3 : 如果为获取到信息,则判断用户密码是否正确
		String user_password = logInUser.getUser_password();
		if (!password.equals(user_password)) {
			throw new BusinessException(ExceptionEnum.PASSWORD_ERROR.getMessage());
		}
//		4 ; 将此次登陆的用户信息,设置到Cookie中
		User user = putUserInfo(logInUser);
		//5 : 设置登陆用户的cookie
		return ActionUtil.setCookieUser(user);
	}

	@Method(desc = "将此次登陆的用户信息,设置到Cookie中",
			logicStep = "1 : 根据登陆成功的用户信息获取部门信息" +
					"2 : 如果当前包装的值为 null，那么抛出异常,说明部门信息不存在" +
					"3 : 组成需要生成的Cookie")
	@Param(name = "logInUser", desc = "用户登陆的实体数据,数据库表结构", range = "不能为空,为空表示为获取到登陆用户信息", isBean = true)
	@Return(desc = "用户的登陆信息", range = "不能为空,为空表示未获取到登陆用户信息")
	private User putUserInfo(Sys_user logInUser) {

		// 1 : 根据登陆成功的用户信息获取部门信息
		Department_info departmentInfo = Dbo.queryOneObject(Department_info.class, "SELECT * FROM " +
				Department_info.TableName + " WHERE dep_id = ?", logInUser.getDep_id())
				.orElseThrow(() -> new BusinessException("部门信息不存在"));

		//3 : 组成需要生成的Cookie
		User user = new User();
		user.setUserId(logInUser.getUser_id());
		user.setUserName(logInUser.getUser_name());
		user.setUserPassword(logInUser.getUser_password());
		user.setUserTypeGroup(logInUser.getUsertype_group());
		user.setUserType(logInUser.getUser_type());
		user.setLoginDate(logInUser.getLogin_date());
		user.setDepId(departmentInfo.getDep_id());
		user.setDepName(departmentInfo.getDep_name());

		return user;
	}

	private static final Logger logger = LogManager.getLogger();

	@Method(desc = "获取登陆用户的菜单信息", logicStep = "1、通过用户组ID获取所有的菜单，2、返回图标、地址、名字")
	@Return(desc = "返回获取的菜单信息", range = "有可能为空")
	public List<Map<String, Object>> getMenu() {
		String userTypeGroup = this.getUser().getUserTypeGroup();
		String userGroup = "'" + StringUtil.replace(userTypeGroup, ",", "','") + "'";
		return Dbo.queryList("select * from component_menu where user_type in(" + userGroup + ")");
	}

	@Method(desc = "用户登录获取默认的页面", logicStep = "1、查询菜单，获取菜单地址")
	@Return(desc = "返回获取的菜单信息", range = "有可能为空")
	public String getDefaultPage() {
		String userType = this.getUser().getUserType();
		if (UserType.XiTongGuanLiYuan == UserType.ofEnumByCode(userType)) {
			userType = UserType.YongHuGuanLi.getCode();
		}
		return Dbo.queryOneObject(Component_menu.class, "select * from component_menu where user_type = ?", userType)
				.orElseThrow(() -> new BusinessException("sql查询错误")).getMenu_path();
	}
}