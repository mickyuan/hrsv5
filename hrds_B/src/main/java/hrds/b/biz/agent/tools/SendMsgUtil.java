package hrds.b.biz.agent.tools;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.AgentActionUtil;
import hrds.commons.utils.PackUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

@DocClass(desc = "海云应用管理端向Agent端发送消息的工具类", author = "WangZhengcheng")
public class SendMsgUtil {

	private static final Log logger = LogFactory.getLog(SendMsgUtil.class);

	@Method(desc = "海云应用管理端向Agent端发送消息，根据模糊字段查询表名", logicStep = "" +
			"1、对参数合法性进行校验" +
			"2、由于向Agent请求的数据量较小，所以不需要压缩" +
			"3、httpClient发送请求并接收响应" +
			"4、根据响应状态码判断响应是否成功" +
			"5、若响应成功，调用方法解析响应报文，并返回响应数据" +
			"6、若响应不成功，记录日志，并抛出异常告知操作失败")
	@Param(name = "agentId", desc = "agentId，agent_info表主键，agent_down_info表外键", range = "不为空")
	@Param(name = "userId", desc = "当前登录用户Id，sys_user表主键，agent_down_info表外键", range = "不为空")
	@Param(name = "databaseInfo", desc = "目标数据库相关信息", range = "map集合")
	@Param(name = "inputString", desc = "模糊查询使用的字符串", range = "如果是多个关键字，中间用|隔开")
	@Param(name = "methodName", desc = "Agent端的提供服务的方法的方法名", range = "AgentActionUtil类中的静态常量")
	@Return(desc = "Agent端通过本地http交互得到的响应数据的msg部分，内容是模糊查询得到的表名"
			, range = "json格式的字符串")
	public static String searchTableName(Long agentId, Long userId, Map<String, Object> databaseInfo,
	                                     String inputString, String methodName){
		//1、对参数合法性进行校验
		if(agentId == null){
			throw new BusinessException("向Agent发送信息，模糊查询表信息时agentId不能为空");
		}
		if(userId == null){
			throw new BusinessException("向Agent发送信息，模糊查询表信息时userId不能为空");
		}
		if(databaseInfo.isEmpty()){
			throw new BusinessException("向Agent发送信息，模糊查询表信息时，请指定数据库连接信息");
		}
		if(StringUtil.isBlank(inputString)){
			throw new BusinessException("向Agent发送信息，模糊查询表信息时，请指定模糊查询字段");
		}
		if(StringUtil.isBlank(methodName)){
			throw new BusinessException("向Agent发送信息时methodName不能为空");
		}

		//2、由于向Agent请求的数据量较小，所以不需要压缩
		String url = AgentActionUtil.getUrl(agentId, userId, methodName);
		logger.debug("准备建立连接，请求的URL为" + url);

		//3、httpClient发送请求并接收响应
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("database_name", (String) databaseInfo.get("database_name"))
				.addData("database_pad", (String) databaseInfo.get("database_pad"))
				.addData("database_ip", (String) databaseInfo.get("database_ip"))
				.addData("database_port", (String) databaseInfo.get("database_port"))
				.addData("user_name", (String) databaseInfo.get("user_name"))
				.addData("database_drive", (String) databaseInfo.get("database_drive"))
				.addData("jdbc_url", (String) databaseInfo.get("jdbc_url"))
				.addData("database_type", (String) databaseInfo.get("database_type"))
				.addData("db_agent", (String) databaseInfo.get("db_agent"))
				.addData("plane_url", (String) databaseInfo.get("plane_url"))
				.addData("search", inputString)
				.post(url);

		ActionResult ar = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接" + url + "服务异常"));
		if (ar.isSuccess()) {
			//5、若响应成功，调用方法解析响应报文，并返回响应数据
			String msg = PackUtil.unpackMsg((String) ar.getData()).get("msg");
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>返回消息为：" + msg);
			return msg;
		}
		//6、若响应不成功，记录日志，并抛出异常告知操作失败
		logger.error(">>>>>>>>>>>>>>>>>>>>>>>>错误信息为：" + ar.getMessage());
		throw new BusinessException("根据输入的字符查询表失败，详情请查看日志");
	}

	@Method(desc = "海云应用管理端向Agent端发送消息，获取所有表名", logicStep = "" +
			"1、对参数合法性进行校验" +
			"2、由于向Agent请求的数据量较小，所以不需要压缩" +
			"3、httpClient发送请求并接收响应" +
			"4、根据响应状态码判断响应是否成功" +
			"5、若响应成功，调用方法解析响应报文，并返回响应数据" +
			"6、若响应不成功，记录日志，并抛出异常告知操作失败")
	@Param(name = "agentId", desc = "agentId，agent_info表主键，agent_down_info表外键", range = "不为空")
	@Param(name = "userId", desc = "当前登录用户Id，sys_user表主键，agent_down_info表外键", range = "不为空")
	@Param(name = "databaseInfo", desc = "目标数据库相关信息", range = "map集合")
	@Param(name = "methodName", desc = "Agent端的提供服务的方法的方法名", range = "AgentActionUtil类中的静态常量")
	@Return(desc = "Agent端通过本地http交互得到的响应数据的msg部分，内容是目标数据库所有表的表名"
			, range = "json格式的字符串")
	public static String getAllTableName(Long agentId, Long userId, Map<String, Object> databaseInfo, String methodName){
		//1、对参数合法性进行校验
		if(agentId == null){
			throw new BusinessException("向Agent发送信息，获取目标数据库所有表时，agentId不能为空");
		}
		if(userId == null){
			throw new BusinessException("向Agent发送信息，获取目标数据库所有表时，userId不能为空");
		}
		if(databaseInfo.isEmpty()){
			throw new BusinessException("向Agent发送信息，获取目标数据库所有表时，请指定数据库连接信息");
		}
		if(StringUtil.isBlank(methodName)){
			throw new BusinessException("向Agent发送信息时methodName不能为空");
		}

		//2、由于向Agent请求的数据量较小，所以不需要压缩
		String url = AgentActionUtil.getUrl(agentId, userId, methodName);
		logger.debug("准备建立连接，请求的URL为" + url);

		//3、httpClient发送请求并接收响应
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("database_name", (String) databaseInfo.get("database_name"))
				.addData("database_pad", (String) databaseInfo.get("database_pad"))
				.addData("database_ip", (String) databaseInfo.get("database_ip"))
				.addData("database_port", (String) databaseInfo.get("database_port"))
				.addData("user_name", (String) databaseInfo.get("user_name"))
				.addData("database_drive", (String) databaseInfo.get("database_drive"))
				.addData("jdbc_url", (String) databaseInfo.get("jdbc_url"))
				.addData("database_type", (String) databaseInfo.get("database_type"))
				.addData("db_agent", (String) databaseInfo.get("db_agent"))
				.addData("plane_url", (String) databaseInfo.get("plane_url"))
				.post(url);

		ActionResult ar = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接" + url + "服务异常"));
		if (ar.isSuccess()) {
			//5、若响应成功，调用方法解析响应报文，并返回响应数据
			String msg = PackUtil.unpackMsg((String) ar.getData()).get("msg");
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>返回消息为：" + msg);
			return msg;
		}
		//6、若响应不成功，记录日志，并抛出异常告知操作失败
		logger.error(">>>>>>>>>>>>>>>>>>>>>>>>错误信息为：" + ar.getMessage());
		throw new BusinessException("获取目标数据库所有表失败，详情请查看日志");
	}

	@Method(desc = "海云应用管理端向Agent端发送消息，根据表名获取该表的相关信息", logicStep = "" +
			"1、对参数合法性进行校验" +
			"2、由于向Agent请求的数据量较小，所以不需要压缩" +
			"3、httpClient发送请求并接收响应" +
			"4、根据响应状态码判断响应是否成功" +
			"5、若响应成功，调用方法解析响应报文，并返回响应数据" +
			"6、若响应不成功，记录日志，并抛出异常告知操作失败")
	@Param(name = "agentId", desc = "agentId，agent_info表主键，agent_down_info表外键", range = "不为空")
	@Param(name = "userId", desc = "当前登录用户Id，sys_user表主键，agent_down_info表外键", range = "不为空")
	@Param(name = "databaseInfo", desc = "目标数据库相关信息", range = "map集合")
	@Param(name = "tableName", desc = "表名", range = "不为空")
	@Param(name = "methodName", desc = "Agent端的提供服务的方法的方法名", range = "AgentActionUtil类中的静态常量")
	@Return(desc = "Agent端通过本地http交互得到的响应数据的msg部分，内容是按照表名得到的该表的字段信息"
			, range = "json格式的字符串")
	public static String getColInfoByTbName(Long agentId, Long userId, Map<String, Object> databaseInfo,
	                                        String tableName, String methodName){
		//1、对参数合法性进行校验
		if(agentId == null){
			throw new BusinessException("向Agent发送信息，根据表名查询表字段信息时，agentId不能为空");
		}
		if(userId == null){
			throw new BusinessException("向Agent发送信息，根据表名查询表字段信息时，userId不能为空");
		}
		if(databaseInfo.isEmpty()){
			throw new BusinessException("向Agent发送信息，根据表名查询表字段信息时，请指定数据库连接信息");
		}
		if(StringUtil.isBlank(tableName)){
			throw new BusinessException("向Agent发送信息，根据表名查询表字段信息时，请填写表名");
		}
		if(StringUtil.isBlank(methodName)){
			throw new BusinessException("向Agent发送信息时methodName不能为空");
		}

		//2、由于向Agent请求的数据量较小，所以不需要压缩
		String url = AgentActionUtil.getUrl(agentId, userId, methodName);
		logger.debug("准备建立连接，请求的URL为" + url);

		//3、httpClient发送请求并接收响应
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("database_name", (String) databaseInfo.get("database_name"))
				.addData("database_pad", (String) databaseInfo.get("database_pad"))
				.addData("database_ip", (String) databaseInfo.get("database_ip"))
				.addData("database_port", (String) databaseInfo.get("database_port"))
				.addData("user_name", (String) databaseInfo.get("user_name"))
				.addData("database_drive", (String) databaseInfo.get("database_drive"))
				.addData("jdbc_url", (String) databaseInfo.get("jdbc_url"))
				.addData("database_type", (String) databaseInfo.get("database_type"))
				.addData("db_agent", (String) databaseInfo.get("db_agent"))
				.addData("plane_url", (String) databaseInfo.get("plane_url"))
				.addData("tableName", tableName)
				.post(url);

		ActionResult ar = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接" + url + "服务异常"));
		if (ar.isSuccess()) {
			//5、若响应成功，调用方法解析响应报文，并返回响应数据
			String msg = PackUtil.unpackMsg((String) ar.getData()).get("msg");
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>返回消息为：" + msg);
			return msg;
		}

		//6、若响应不成功，记录日志，并抛出异常告知操作失败
		logger.error(">>>>>>>>>>>>>>>>>>>>>>>>错误信息为：" + ar.getMessage());
		throw new BusinessException("根据表名获取该表的字段信息失败，详情请查看日志");
	}
}
