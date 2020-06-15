package hrds.commons.systemlog;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.DateUtil;
import fd.ng.web.util.Dbo;
import hrds.commons.entity.Login_operation_info;
import hrds.commons.utils.Constant;
import hrds.commons.utils.key.PrimayKeyGener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

@DocClass(desc = "登录操作日志信息（记录日志）", author = "dhw", createdate = "2020/4/24 10:04")
public class LoginOperationLogInfo {

	private static final Logger logger = LogManager.getLogger();

	@Method(desc = "保存用户登录日志信息", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.获取请求头" +
			"3.解析请求头" +
			"4.获取浏览器类型" +
			"5.获取系统类型" +
			"6.判断是HttpClient请求还是浏览器请求" +
			"6.1 HttpClient请求" +
			"6.2 浏览器请求" +
			"7.获取浏览器版本" +
			"8.请求方式" +
			"9.客户端的IP" +
			"10.超文本传输协议版本" +
			"11.请求日期" +
			"12.请求时间" +
			"13.系统类型" +
			"14.用户ID" +
			"15.用户名称" +
			"16.操作类型" +
			"17.保存日志信息")
	@Param(name = "operation_type", desc = "操作类型", range = "无限制")
	@Param(name = "request", desc = "请求参数", range = "无限制")
	@Param(name = "user_id", desc = "用户登陆ID", range = "不能为空的整数")
	@Param(name = "user_name", desc = "用户名", range = "不能为空的字符串")
	public static void saveLoginLog(HttpServletRequest request, String user_id, String user_name,
	                                String operation_type) {
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
		login_operation_info.setOperation_type(operation_type);
		// 16.保存日志信息
		login_operation_info.setLog_id(PrimayKeyGener.getNextId());
		login_operation_info.add(Dbo.db());
	}

	@Method(desc = "保存用户操作日志的信息(比如用户的登入,登出(在登陆,登出生成数据),数据的删除,新增)",
			logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
					"2.获取请求路径" +
					"3 : 采集数据库直连任务的删除记录" +
					"4 : 采集db文件任务的删除记录" +
					"5 : 采集半结构任务的删除记录" +
					"6 : 采集非结构任务的删除记录" +
					"7 : 采集ftp任务的删除记录" +
					"8 : 集市新建数据表" +
					"9 : 集市删除数据表")
	@Param(name = "request", desc = "请求参数", range = "无限制")
	@Param(name = "user_id", desc = "用户登陆ID", range = "不能为空的整数")
	@Param(name = "user_name", desc = "用户名", range = "不能为空的字符串")
	public static void saveUserOperationLogInfo(HttpServletRequest request, String user_id,
	                                            String user_name) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		String uri = request.getPathInfo();
		// 2.获取请求路径
		String action = uri.substring(uri.lastIndexOf('/') + 1);
		switch (action) {
			case "deleteDBTask":
				// 3 : 采集数据库直连任务的删除记录
				saveLoginLog(request, user_id, user_name, Constant.DELETEDBTASK);
				break;
			case "deleteDFTask":
				// 4 : 采集db文件任务的删除记录
				saveLoginLog(request, user_id, user_name, Constant.DELETEDFTASK);
				break;
			case "deleteHalfStructTask":
				// 5 : 采集半结构任务的删除记录
				saveLoginLog(request, user_id, user_name, Constant.DELETEHALFSTRUCTTASK);
				break;
			case "deleteNonStructTask":
				// 6 : 采集非结构任务的删除记录
				saveLoginLog(request, user_id, user_name, Constant.DELETENONSTRUCTTASK);
				break;
			case "deleteFTPTask":
				// 7 : 采集ftp任务的删除记录
				saveLoginLog(request, user_id, user_name, Constant.DELETEFTPTASK);
				break;
			case "addDMDataTable":
				// 8 : 集市新建数据表
				saveLoginLog(request, user_id, user_name, Constant.ADDDMDATATABLE);
				break;
			case "deleteDMDataTable":
				// 9 : 集市删除数据表
				saveLoginLog(request, user_id, user_name, Constant.DELETEDMDATATABLE);
				break;
			default:
				logger.info("暂不保存此类型日志。。。");
				break;
		}
	}
}
