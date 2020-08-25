package hrds.commons.systemlog;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.ClassUtil;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.web.annotation.Action;
import fd.ng.web.util.Dbo;
import hrds.commons.entity.Login_operation_info;
import hrds.commons.utils.Constant;
import hrds.commons.utils.key.PrimayKeyGener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@DocClass(desc = "登录操作日志信息（记录日志）", author = "dhw", createdate = "2020/4/24 10:04")
public class LoginOperationLogInfo {

	private static final Logger logger = LogManager.getLogger();

	public static void saveLoginLog(HttpServletRequest request, String user_id, String user_name, String operation_type) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.获取请求头
		String header = request.getHeader("User-Agent");
		// 3.解析请求头
		UserAgent userAgent = UserAgent.parseUserAgentString(header);
		Login_operation_info login_operation_info = new Login_operation_info();
		// 4.获取浏览器类型
		Browser browser = userAgent.getBrowser();
		// 5.获取系统类型
		OperatingSystem operatingSystem = userAgent.getOperatingSystem();
		String headerStr = header.substring(0, header.indexOf('/')).toUpperCase();
		// 6.判断是HttpClient请求还是浏览器请求
		if ("UNKNOWN".equalsIgnoreCase(browser.getName())) {
			// 6.1 HttpClient请求
			login_operation_info.setBrowser_type(headerStr);
			login_operation_info.setSystem_type(headerStr);
		} else {
			// 6.2 浏览器请求
			login_operation_info.setBrowser_type(browser.getName());
			login_operation_info.setSystem_type(operatingSystem.getName());
		}
		// 7.获取浏览器版本
		Version browserVersion = userAgent.getBrowserVersion();
		if (browserVersion == null) {
			login_operation_info.setBrowser_version(headerStr);
		} else {
			login_operation_info.setBrowser_version(browserVersion.getVersion());
		}
		// 8.请求方式
		login_operation_info.setRequest_mode(request.getMethod());
		// 9.客户端的IP
		login_operation_info.setRemoteaddr(request.getRemoteAddr());
		// 10.超文本传输协议版本
		login_operation_info.setProtocol(request.getProtocol());
		// 11.请求日期
		login_operation_info.setRequest_date(DateUtil.getSysDate());
		// 12.请求时间
		login_operation_info.setRequest_time(DateUtil.getSysTime());
		// 13.请求类型
		login_operation_info.setRequest_type(request.getMethod());
		// 13.用户ID
		login_operation_info.setUser_id(user_id);
		// 14.用户名称
		login_operation_info.setUser_name(user_name);
		// 15.操作类型
		if (operation_type.length() > 1024) operation_type = operation_type.substring(0, 1024);
		login_operation_info.setOperation_type(operation_type);
		// 16.保存日志信息
		login_operation_info.setLog_id(PrimayKeyGener.getNextId());
		try {
			login_operation_info.add(Dbo.db());
		}catch (Exception e){
			logger.error("保存日志失敗！");
		}
	}

	public static void saveUserOperationLogInfo(HttpServletRequest request, String user_id, String user_name) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		String uri = request.getPathInfo();
		String action = uri.substring(uri.lastIndexOf('/') + 1);
		try {
			String replace = StringUtil.replace(uri, '/', '.').substring(1, uri.lastIndexOf('/'));
			List<Class<?>> actionClassList = ClassUtil.getClassListByAnnotation(replace, Action.class);
			for (Class<?> actionClass : actionClassList) {
				Method[] actionMethods = actionClass.getDeclaredMethods();
				for (Method actionMethod : actionMethods) {
					fd.ng.core.annotation.Method anMethod = actionMethod.getAnnotation(fd.ng.core.annotation.Method.class);// 得到该类的注解
					if (anMethod == null) continue;
					int methodModifier = actionMethod.getModifiers();
					if (Modifier.isAbstract(methodModifier) || Modifier.isStatic(methodModifier))
						continue; // 抽象和静态方法不处理
					if (!Modifier.isPublic(methodModifier)) continue;
					String curMethodName = actionMethod.getName();
					if (!action.equals(curMethodName)) continue;
					String apiDesc = anMethod.desc();//类的描述
					Map<String, String[]> parameterMap = request.getParameterMap();
					if (parameterMap.keySet().contains("pageSize") || parameterMap.keySet().contains("currPage"))
						continue;
					StringBuilder sb = new StringBuilder();
					for (String key : parameterMap.keySet()) {
						String[] strings = parameterMap.get(key);
						String para = "";
						for (String string : strings) para = para + string;
						sb.append(key).append("=").append(para).append(";");
					}
					saveLoginLog(request, user_id, user_name, apiDesc + ":" + sb.toString());
					return;
				}
			}
		} catch (Exception e) {
			logger.error("保存日志失敗！");
		}
	}
}
