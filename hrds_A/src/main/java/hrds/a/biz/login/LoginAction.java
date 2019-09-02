package hrds.a.biz.login;

import fd.ng.core.utils.StringUtil;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.commons.exception.ExceptionEnum;
import hrds.commons.utils.ActionUtil;
import hrds.commons.utils.User;

import java.util.Optional;

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
	 * <p>方法描述: 检查当前用户的登陆是否正确</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-09-02</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	public void checkLogin(int user_id, String pwd) {

		/*
		 * 1 : 用户名为空
		 * 2 : 密码为空
		 * 3 : 查询当前用户的记录信息
		 * 4 : 如果为获取到信息,则表示当前用户的信息不存在
		 * 5 : 设置登陆用户的cookie
		 */
		//1 : 用户名为空
		if( StringUtil.isBlank(user_id + "") ) {
			throw new BusinessException(ExceptionEnum.USER_NOT_EMPTY.getMessage());
		}

		//2 : 密码为空
		if( StringUtil.isBlank(pwd) ) {
			throw new BusinessException(ExceptionEnum.USER_PWD_EMPTY.getMessage());
		}

		//3 : 查询当前用户的记录信息
		Optional<Sys_user> sys_user = Dbo.queryOneObject(Sys_user.class, "select * from " + Sys_user.TableName + " where user_id = ?", user_id);
		Sys_user logInUser = sys_user.get();

		//4 : 如果为获取到信息,则表示当前用户的信息不存在
		if( StringUtil.isBlank(logInUser.getUser_id() + "") ) {
			throw new BusinessException(ExceptionEnum.USER_NOE_EXISTS.getMessage());
		}
		else {
			String user_password = logInUser.getUser_password();
			if( pwd.equals(user_password) ) {
				throw new BusinessException(ExceptionEnum.PASSWORD_ERROR.getMessage());
			}
			else {
				//5 : 设置登陆用户的cookie
				User user = putUserInfo(logInUser);
				ActionUtil.setCookieUser(user);
			}
		}
	}

	public User putUserInfo(Sys_user logInUser) {


		/*
		 * 1 : 根据登陆成功的用户信息获取部门信息
		 * 2 : 组成需要生成的Cookie
		 */

		// 1 : 根据登陆成功的用户信息获取部门信息
		Optional<Department_info> department = Dbo
						.queryOneObject(Department_info.class, "select * from " + Department_info.TableName + " where dep_id = ?;",
										logInUser.getDep_id());
		Department_info department_info = department.get();
		//2 : 组成需要生成的Cookie
		User user = new User();
		user.setUserId(logInUser.getUser_id());
		user.setUserName(logInUser.getUser_name());
		user.setUserPassword(logInUser.getUser_password());
		user.setUserTypeGroup(logInUser.getUsertype_group());
		user.setUserType(logInUser.getUser_type());
		user.setLoginDate(logInUser.getLogin_date());
		user.setDepId(department_info.getDep_id());
		user.setDepName(department_info.getDep_name());

		return user;
	}

}
