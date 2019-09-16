package hrds.commons.action;

import fd.ng.core.docannotation.*;
import fd.ng.db.resultset.Result;
import fd.ng.web.annotation.RequestBean;
import hrds.commons.utils.User;

/**
 * @program: hrsv5
 * @description: action注释模版
 * @author: xchao
 * @create: 2019-09-16 10:57
 */
@DocClass(describe = "类注释模版",isRest = true,author = "xchao",time = "2019年9月16日 09:21:41")
public class actionTemplate {
	@DocMethod(description = "获取用户",isRest = true)
	@Params({
			@Param(name = "name", intro = "用户名", required = true, range = "任意"),
			@Param(name = "sex", intro = "性别", required = true, range = "男，女"),
			@Param(name = "use", intro = "用户实体", dataType = User.class,isRequestBean = true,range = "实体bean"),
			@Param(name = "age", intro = "年龄", dataType = Long.class,range = "10-100之间")
	}
	)
	public void getUserInfo(String name, String sex, @RequestBean User user, int age) {

	}

	@DocMethod(description = "新增用户",isRest = true)
	@Param(name = "use", intro = "用户实体", dataType = User.class,isRequestBean = true,range = "实体bean")
	@DocReturn(description = "返回是否新增成功的表示",dataType = Boolean.class,range = "true,false")
	public boolean addUser(@RequestBean User user) {

		return false;
	}

	@DocMethod(description = "获取用户信息，返回实体信息",isRest = true)
	@Param(name = "use", intro = "用户实体", dataType = User.class,isRequestBean = true,range = "实体bean")
	@DocReturn(description = "返回用户的基本信息以实体返回",dataType = User.class,range = "User实体ben",isRequestBean=true)
	public User getUser(@RequestBean User user) {

		return new User();
	}


	@DocMethod(description = "新增用户",isRest = true)
	@DocReturn(description = "返回用户的基本信息以result返回",dataType = Result.class,range = "返回用户result")
	public Result getUser() {

		return new Result();
	}
}
