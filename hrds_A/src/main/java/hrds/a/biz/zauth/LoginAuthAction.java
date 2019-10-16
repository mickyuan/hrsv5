package hrds.a.biz.zauth;

import fd.ng.core.annotation.DocClass;
import fd.ng.web.action.ActionResult;
import fd.ng.web.action.ActionResultHelper;
import fd.ng.web.util.RequestUtil;
import hrds.commons.base.BaseAction;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@DocClass(desc = "这个类暂时不修改,后面做成菜单",author = "Mr.Lee")
public class LoginAuthAction extends BaseAction {

	/**
	 * 检测用户是否已经登录成功
	 * @param username
	 *          含义 : 登陆的用户名
	 *          取值范围 : 用户名为long类型的数字
	 * @param password
	 *          含义 : 用户登陆密码
	 *          取值范围 : 一个字符串
	 * @return ActionResult
	 *          含义 : 登陆验证对比信息
	 *          取值范围 : 根据具体情况获取不同的信息
	 */
	private ActionResult loginCheck(long username, String password) {

		if( getUserId() == username && getUser().getUserPassword().equalsIgnoreCase(password) ) {

			return ActionResultHelper.success();
		}
		else
			return ActionResultHelper.bizError("login failed!");
	}
}
