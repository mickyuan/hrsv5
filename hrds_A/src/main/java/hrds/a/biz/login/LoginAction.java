package hrds.a.biz.login;

import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.web.action.ActionResult;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.commons.exception.ExceptionEnum;
import hrds.commons.utils.ActionUtil;
import hrds.commons.utils.User;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>标    题: 海云数服 V5.0</p>
 * <p>描    述: 登陆认证</p>
 * <p>版    权: Copyright (c) 2019</p>
 * <p>公    司: 博彦科技(上海)有限公司</p>
 * <p>@author : Mr.Lee</p>
 * <p>创建时间 : 2019-09-02 14:16</p>
 * <p>version: JDK 1.8</p>
 */
public class LoginAction extends BaseAction {

	/**
	 * 用户登陆入口
	 * 1 : 获取并检查,用户名是否为空
	 * 2 : 获取并检查,密码是否做为空
	 * 3 : 检查当前用户的登陆是否正确,并返回cookie数据信息
	 *
	 * @param request 含义 : 页面的请求Http对象 取值范围 : 从中获取需要的数据,也就是页面的name对应的属性
	 * @return
	 */
	public String login(HttpServletRequest request) {

		//1 : 获取并检查,用户名是否为空
		String user_id = request.getParameter("username");
		if (StringUtil.isBlank(user_id)) {
			throw new BusinessException(ExceptionEnum.USER_NOT_EMPTY);
		}
		//2 : 获取并检查,密码是否做为空
		String pwd = request.getParameter("password");
		if (StringUtil.isBlank(pwd)) {
			throw new BusinessException(ExceptionEnum.USER_PWD_EMPTY);
		}

		//3 : 检查当前用户的登陆是否正确,并返回cookie数据信息
		return checkLogin(Long.parseLong(user_id), pwd);

	}

	/**
	 * 重写登陆时的用户验证情况(这里不希望父类的方法做SESSION的管理认证,
	 * 因为所有Action中的方法请求时,都会做session的认证,而登陆不需要)
	 *
	 * @param request 含义 : 页面的请求Http对象
	 *                取值范围 :  从中获取需要的数据,也就是页面的name对应的属性
	 * @return ActionResult
	 * 含义 : 返回验证的结果信息,
	 * 取值范围  :  null--表示为正常通过,其他情况则会抛出异常
	 */
	@Override
	protected ActionResult _doPreProcess(HttpServletRequest request) {

		return null;
	}

	/**
	 * 检查当前用户的登陆是否正确
	 * <p>
	 * 1 : 查询当前用户的记录信息
	 * 2 : 如果当前包装的值为 null，那么抛出异常,说明当前用户不存在
	 * 3 : 如果为获取到信息,则判断用户密码是否正确
	 * 4 : 将此次登陆的用户信息,设置到Cookie中
	 * 5 : 设置登陆用户的cookie
	 *
	 * @param user_id long
	 *                含义 : 用户ID
	 *                取值范围 : 不可为空的长整型, 用于用户登陆的身份认证
	 * @param pwd     String
	 *                含义 :  用户密码
	 * @return String
	 * 含义 :  当前用户的信息
	 * 取值返回 : 不能为空,为空表示用户无效
	 */
	private String checkLogin(long user_id, String pwd) {

		//1 : 查询当前用户的记录信息
		Sys_user logInUser = Dbo.queryOneObject(Sys_user.class, "select * from " +
				Sys_user.TableName + " where user_id = ?", user_id)
				.orElseThrow(() -> new BusinessException(ExceptionEnum.USER_NOT_EXISTS));

		//3 : 如果为获取到信息,则判断用户密码是否正确
		String user_password = logInUser.getUser_password();
		if (!pwd.equals(user_password)) {
			throw new BusinessException(ExceptionEnum.PASSWORD_ERROR.getMessage());
		}
//		4 ; 将此次登陆的用户信息,设置到Cookie中
		User user = putUserInfo(logInUser);
		//5 : 设置登陆用户的cookie
		return ActionUtil.setCookieUser(user);
	}

	/**
	 * 将此次登陆的用户信息,设置到Cookie中
	 * 1 : 根据登陆成功的用户信息获取部门信息
	 * 2 : 如果当前包装的值为 null，那么抛出异常,说明部门信息不存在
	 * 3 : 组成需要生成的Cookie
	 *
	 * @param logInUser 含义 : 登陆用户的信息实体
	 *                  取值范围 : 用户的具体信息
	 * @return User
	 * 含义 : 返回Cookie需要的数据信息
	 * 取值范围 : 不会为空
	 */
	private User putUserInfo(Sys_user logInUser) {

		// 1 : 根据登陆成功的用户信息获取部门信息
		Department_info departmentInfo = Dbo.queryOneObject(Department_info.class, "select * from " +
				Department_info.TableName + " where dep_id = ?", logInUser.getDep_id())
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

}