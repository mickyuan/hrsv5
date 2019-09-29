package hrds.commons.action;

import fd.ng.core.docannotation.*;
import fd.ng.core.exception.internal.FrameworkRuntimeException;
import fd.ng.db.resultset.Result;
import fd.ng.web.annotation.RequestBean;
import hrds.commons.codes.AgentStatus;
import hrds.commons.utils.User;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: hrsv5
 * @description: action注释模版
 * @author: xchao
 * @create: 2019-09-16 10:57
 */
@DocClass(describe = "类注释模版",author = "xchao",time = "2019年9月16日 09:21:41")
public class actionTemplate {
	@DocMethod(description = "获取用户",isRest = true)
	@Params({
			@Param(name = "name", intro = "用户名", required = true, range = "任意",example = "李强"),
			@Param(name = "sex", intro = "性别", required = true, range = "男，女",example = "女"),
			@Param(name = "use", intro = "用户实体", dataType = User.class,isRequestBean = true,range = "实体bean"),
			@Param(name = "age", intro = "年龄", dataType = Long.class,range = "10-100之间"),
			@Param(name = "ip", intro = "agentIp信息", dataType = String.class,range = "任意",example = "10.71.9.100")
	}
	)
	public void getUserInfo(String name, String sex, @RequestBean User user, int age,String ip) {

	}

	@DocMethod(description = "新增用户",isRest = true)
	@Param(name = "user", intro = "用户实体", dataType = User.class,isRequestBean = true,range = "实体bean")
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
		List aa = new ArrayList<Map<String,Object>>();
		Map map = new HashMap();
		map.put("abc","sdfdsfsd");
		aa.add(map);
		Result a = new Result();
		a.add(aa);

		a.getString(0,"abc");
		return new Result();
	}
}
