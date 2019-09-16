package hrds.commons.action;

import fd.ng.web.annotation.RequestBean;
import hrds.commons.apiannotation.DocClass;
import hrds.commons.apiannotation.DocMethod;
import hrds.commons.apiannotation.Param;
import hrds.commons.apiannotation.Params;
import hrds.commons.utils.User;

/**
 * @program: hrsv5
 * @description: action注释模版
 * @author: xchao
 * @create: 2019-09-16 10:57
 */
@DocClass(describe = "类注释模版",isApi = true,author = "xchao",time = "2019年9月16日 09:21:41")
public class actionTemplate {
	@DocMethod(description = "获取用户", version = "v5",isApi = true)
	@Params({
			@Param(name = "name", intro = "用户名", required = true, range = "任意"),
			@Param(name = "sex", intro = "性别", required = true, range = "男，女"),
			@Param(name = "use", intro = "用户实体", dataType = User.class,isRequestBean = true),
			@Param(name = "age", intro = "年龄", dataType = Long.class)
	}
	)
	public void getUserInfo(String name, String sex, @RequestBean User user, int age) {

	}

	@DocMethod(description = "新增用户", version = "v5",isApi = true)
	@Param(name = "use", intro = "用户实体", dataType = User.class,isRequestBean = true)
	public void addUser(@RequestBean User user) {

	}
}
