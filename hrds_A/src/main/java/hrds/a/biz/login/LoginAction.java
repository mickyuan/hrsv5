package hrds.a.biz.login;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.bcel.internal.generic.ATHROW;
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
import java.util.Map;
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
	 * <p>方法描述: 用户登陆入口</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-09-02</p>
	 * <p>参   数:  request : 页面的请求</p>
	 * <p>return:  </p>
	 */
	public void login(HttpServletRequest request) {

		/*
		 * 1 : 用户名为空
		 * 2 : 密码为空
		 */
		String user_id = request.getParameter("username");

		//1 : 用户名为空
		if( StringUtil.isBlank(user_id) ) {
			throw new BusinessException(ExceptionEnum.USER_NOT_EMPTY.getMessage());
		}
		String pwd = request.getParameter("password");
		//2 : 密码为空
		if( StringUtil.isBlank(pwd) ) {
			throw new BusinessException(ExceptionEnum.USER_PWD_EMPTY.getMessage());
		}
		checkLogin(Integer.parseInt(user_id), pwd);

	}

	/**
	 * <p>方法描述: 重写登陆时的用户验证情况(这里不希望父类的方法做SESSION的管理认证,因为所有Action中的方法请求时,都会做session的认证,而登陆不需要)</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-09-02</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	@Override
	protected ActionResult _doPreProcess(HttpServletRequest request) {

		return null;
	}

	/**
	 * <p>方法描述: 检查当前用户的登陆是否正确</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-09-02</p>
	 * <p>return:  </p>
	 *
	 * @param user_id 用户ID
	 * @param pwd     用户密码
	 */
	private void checkLogin(int user_id, String pwd) {

		/*
		 * 1 : 查询当前用户的记录信息
		 * 2 : 如果封装的实体为null,抛出异常错误信息,说明当前用户不存在
		 * 3 : 如果为获取到信息,则判断用户密码是否正确
		 * 4 : 设置登陆用户的cookie
		 */

		//1 : 查询当前用户的记录信息
		Optional<Sys_user> sys_user = Dbo.queryOneObject(Sys_user.class, "select * from " + Sys_user.TableName + " where user_id = ?", user_id);

		//2 : 如果封装的实体为null,抛出异常错误信息,说明当前用户不存在
		Sys_user logInUser = sys_user.orElseThrow(() -> new BusinessException(ExceptionEnum.USER_NOE_EXISTS));

		//3 : 如果为获取到信息,则判断用户密码是否正确
		String user_password = logInUser.getUser_password();
		if( !pwd.equals(user_password) ) {
			throw new BusinessException(ExceptionEnum.PASSWORD_ERROR);
		}
		else {
			//4 : 设置登陆用户的cookie
			User user = putUserInfo(logInUser);
			ActionUtil.setCookieUser(user);
		}
	}

	/**
	 * <p>方法描述: 将此次登陆的用户信息,设置到Cookie中</p>
	 * <p> 1 : 根据登陆成功的用户信息获取部门信息</p>
	 * <p> 2 : 如果封装的实体为null,抛出异常错误信息,说明部门信息不存在</p>
	 * <p> 3 : 组成需要生成的Cookie</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-09-09</p>
	 * <p>return:  </p>
	 *
	 * @param logInUser : 用户信息
	 */
	private User putUserInfo(Sys_user logInUser) {

		// 1 : 根据登陆成功的用户信息获取部门信息

		Optional<Department_info> department_info = Dbo
						.queryOneObject(Department_info.class, "select * from " + Department_info.TableName + " where dep_id = ?",
										logInUser.getDep_id());

		//2 : 如果封装的实体为null,抛出异常错误信息,说明部门信息不存在
		Department_info departmentInfo = department_info.orElseThrow(() -> new BusinessException("部门信息不存在"));

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
