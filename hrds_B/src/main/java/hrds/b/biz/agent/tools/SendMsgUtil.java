package hrds.b.biz.agent.tools;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Object_collect_struct;
import hrds.commons.entity.Object_collect_task;
import hrds.commons.entity.Object_handle_type;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.AgentActionUtil;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.PackUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;

@DocClass(desc = "海云应用管理端向Agent端发送消息的工具类，该类的职责是配合CollTbConfStepAction完成访问Agent的工作，" +
		"其测试用例在CollTbConfStepActionTest中，没有对它单独定义测试用例", author = "WangZhengcheng")
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
	                                     String inputString, String methodName) {
		//1、对参数合法性进行校验
		if (agentId == null) {
			throw new BusinessException("向Agent发送信息，模糊查询表信息时agentId不能为空");
		}
		if (userId == null) {
			throw new BusinessException("向Agent发送信息，模糊查询表信息时userId不能为空");
		}
		if (databaseInfo.isEmpty()) {
			throw new BusinessException("向Agent发送信息，模糊查询表信息时，请指定数据库连接信息");
		}
		Database_set legalParam = getLegalParam(databaseInfo);
		if (StringUtil.isBlank(inputString)) {
			throw new BusinessException("向Agent发送信息，模糊查询表信息时，请指定模糊查询字段");
		}
		if (StringUtil.isBlank(methodName)) {
			throw new BusinessException("向Agent发送信息，模糊查询表信息时，methodName不能为空");
		}

		//2、由于向Agent请求的数据量较小，所以不需要压缩
		String url = AgentActionUtil.getUrl(agentId, userId, methodName);
		logger.debug("准备建立连接，请求的URL为" + url);

		//3、httpClient发送请求并接收响应
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("database_name", legalParam.getDatabase_name())
				.addData("database_pad", legalParam.getDatabase_pad())
				.addData("database_ip", legalParam.getDatabase_ip())
				.addData("database_port", legalParam.getDatabase_port())
				.addData("user_name", legalParam.getUser_name())
				.addData("database_drive", legalParam.getDatabase_drive())
				.addData("jdbc_url", legalParam.getJdbc_url())
				.addData("database_type", legalParam.getDatabase_type())
				.addData("db_agent", legalParam.getDb_agent())
				.addData("plane_url", legalParam.getPlane_url())
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
	public static String getAllTableName(Long agentId, Long userId, Map<String, Object> databaseInfo,
	                                     String methodName) {
		//1、对参数合法性进行校验
		if (agentId == null) {
			throw new BusinessException("向Agent发送信息，获取目标数据库所有表时，agentId不能为空");
		}
		if (userId == null) {
			throw new BusinessException("向Agent发送信息，获取目标数据库所有表时，userId不能为空");
		}
		if (databaseInfo.isEmpty()) {
			throw new BusinessException("向Agent发送信息，获取目标数据库所有表时，请指定数据库连接信息");
		}
		Database_set legalParam = getLegalParam(databaseInfo);
		if (StringUtil.isBlank(methodName)) {
			throw new BusinessException("向Agent发送信息，获取目标数据库所有表时，methodName不能为空");
		}

		//2、由于向Agent请求的数据量较小，所以不需要压缩
		String url = AgentActionUtil.getUrl(agentId, userId, methodName);
		logger.debug("准备建立连接，请求的URL为" + url);

		//3、httpClient发送请求并接收响应
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("database_name", legalParam.getDatabase_name())
				.addData("database_pad", legalParam.getDatabase_pad())
				.addData("database_ip", legalParam.getDatabase_ip())
				.addData("database_port", legalParam.getDatabase_port())
				.addData("user_name", legalParam.getUser_name())
				.addData("database_drive", legalParam.getDatabase_drive())
				.addData("jdbc_url", legalParam.getJdbc_url())
				.addData("database_type", legalParam.getDatabase_type())
				.addData("db_agent", legalParam.getDb_agent())
				.addData("plane_url", legalParam.getPlane_url())
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
	                                        String tableName, String methodName) {
		//1、对参数合法性进行校验
		if (agentId == null) {
			throw new BusinessException("向Agent发送信息，根据表名查询表字段信息时，agentId不能为空");
		}
		if (userId == null) {
			throw new BusinessException("向Agent发送信息，根据表名查询表字段信息时，userId不能为空");
		}
		if (databaseInfo.isEmpty()) {
			throw new BusinessException("向Agent发送信息，根据表名查询表字段信息时，请指定数据库连接信息");
		}
		Database_set legalParam = getLegalParam(databaseInfo);
		if (StringUtil.isBlank(tableName)) {
			throw new BusinessException("向Agent发送信息，根据表名查询表字段信息时，请填写表名");
		}
		if (StringUtil.isBlank(methodName)) {
			throw new BusinessException("向Agent发送信息，根据表名查询表字段信息时，methodName不能为空");
		}

		//2、由于向Agent请求的数据量较小，所以不需要压缩
		String url = AgentActionUtil.getUrl(agentId, userId, methodName);
		logger.debug("准备建立连接，请求的URL为" + url);

		//3、httpClient发送请求并接收响应
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("database_name", legalParam.getDatabase_name())
				.addData("database_pad", legalParam.getDatabase_pad())
				.addData("database_ip", legalParam.getDatabase_ip())
				.addData("database_port", legalParam.getDatabase_port())
				.addData("user_name", legalParam.getUser_name())
				.addData("database_drive", legalParam.getDatabase_drive())
				.addData("jdbc_url", legalParam.getJdbc_url())
				.addData("database_type", legalParam.getDatabase_type())
				.addData("db_agent", legalParam.getDb_agent())
				.addData("plane_url", legalParam.getPlane_url())
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

	@Method(desc = "海云应用管理端向Agent端发送消息，根据自定义抽取SQL获取该表的字段信息", logicStep = "" +
			"1、对参数合法性进行校验" +
			"2、由于向Agent请求的数据量较小，所以不需要压缩" +
			"3、httpClient发送请求并接收响应" +
			"4、根据响应状态码判断响应是否成功" +
			"5、若响应成功，调用方法解析响应报文，并返回响应数据" +
			"6、若响应不成功，记录日志，并抛出异常告知操作失败")
	@Param(name = "agentId", desc = "agentId，agent_info表主键，agent_down_info表外键", range = "不为空")
	@Param(name = "userId", desc = "当前登录用户Id，sys_user表主键，agent_down_info表外键", range = "不为空")
	@Param(name = "databaseInfo", desc = "目标数据库相关信息", range = "map集合")
	@Param(name = "custSQL", desc = "自定义抽取SQL", range = "不为空")
	@Param(name = "methodName", desc = "Agent端的提供服务的方法的方法名", range = "AgentActionUtil类中的静态常量")
	@Return(desc = "Agent端通过本地http交互得到的响应数据的msg部分，内容是按照自定义抽取SQL得到的该表的字段信息"
			, range = "json格式的字符串")
	public static String getCustColumn(Long agentId, Long userId, Map<String, Object> databaseInfo,
	                                   String custSQL, String methodName) {
		//1、对参数合法性进行校验
		if (agentId == null) {
			throw new BusinessException("向Agent发送信息，根据自定义抽取SQL获取该表的字段信息，agentId不能为空");
		}
		if (userId == null) {
			throw new BusinessException("向Agent发送信息，根据自定义抽取SQL获取该表的字段信息，userId不能为空");
		}
		if (databaseInfo.isEmpty()) {
			throw new BusinessException("向Agent发送信息，根据自定义抽取SQL获取该表的字段信息，请指定数据库连接信息");
		}
		Database_set legalParam = getLegalParam(databaseInfo);
		if (StringUtil.isBlank(custSQL)) {
			throw new BusinessException("向Agent发送信息，根据自定义抽取SQL获取该表的字段信息，自定义抽取SQL不能为空");
		}
		if (StringUtil.isBlank(methodName)) {
			throw new BusinessException("向Agent发送信息，根据自定义抽取SQL获取该表的字段信息，methodName不能为空");
		}

		//2、由于向Agent请求的数据量较小，所以不需要压缩
		String url = AgentActionUtil.getUrl(agentId, userId, methodName);
		logger.debug("准备建立连接，请求的URL为" + url);

		//3、httpClient发送请求并接收响应
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("database_name", legalParam.getDatabase_name())
				.addData("database_pad", legalParam.getDatabase_pad())
				.addData("database_ip", legalParam.getDatabase_ip())
				.addData("database_port", legalParam.getDatabase_port())
				.addData("user_name", legalParam.getUser_name())
				.addData("database_drive", legalParam.getDatabase_drive())
				.addData("jdbc_url", legalParam.getJdbc_url())
				.addData("database_type", legalParam.getDatabase_type())
				.addData("db_agent", legalParam.getDb_agent())
				.addData("plane_url", legalParam.getPlane_url())
				.addData("custSQL", custSQL)
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
		throw new BusinessException("根据自定义抽取SQL获取该表的字段信息失败，详情请查看日志");
	}

	@Method(desc = "海云应用管理端向Agent端发送数据库采集任务信息", logicStep = "" +
			"1、对参数合法性进行校验" +
			"2、由于向Agent请求的数据量较小，所以不需要压缩" +
			"3、httpClient发送请求并接收响应" +
			"4、根据响应状态码判断响应是否成功" +
			"5、若响应不成功，记录日志，并抛出异常告知操作失败")
	@Param(name = "colSetId", desc = "源系统数据库设置表ID", range = "不为空")
	@Param(name = "agentId", desc = "agentId，agent_info表主键，agent_down_info表外键", range = "不为空")
	@Param(name = "userId", desc = "当前登录用户Id，sys_user表主键，agent_down_info表外键", range = "不为空")
	@Param(name = "taskInfo", desc = "数据库采集任务信息", range = "SourceDataConfBean对象json格式字符串")
	@Param(name = "methodName", desc = "Agent端的提供服务的方法的方法名", range = "AgentActionUtil类中的静态常量")
	public static void sendDBCollectTaskInfo(Long colSetId, Long agentId, Long userId, String taskInfo,
	                                         String methodName, String etlDate) {
		//1、对参数合法性进行校验
		if (agentId == null) {
			throw new BusinessException("向Agent发送数据库采集任务信息，agentId不能为空");
		}
		if (userId == null) {
			throw new BusinessException("向Agent发送数据库采集任务信息，userId不能为空");
		}
		if (StringUtil.isBlank(taskInfo)) {
			throw new BusinessException("向Agent发送数据库采集任务信息，任务信息不能为空");
		}
		if (StringUtil.isBlank(methodName)) {
			throw new BusinessException("向Agent发送数据库采集任务信息时，methodName不能为空");
		}
		if (StringUtil.isBlank(etlDate)) {
			throw new BusinessException("向Agent发送数据库采集任务信息时，跑批日期不能为空");
		}
		//2、使用数据压缩工具类，酌情对发送的信息进行压缩
		String url = AgentActionUtil.getUrl(agentId, userId, methodName);
		logger.debug("准备建立连接，请求的URL为" + url);

		//3、httpClient发送请求并接收响应
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("etlDate", etlDate)
				.addData("taskInfo", PackUtil.packMsg(taskInfo))
				.post(url);
		//4、根据响应状态码判断响应是否成功
		ActionResult ar = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接" + url + "服务异常"));
		//5、若响应不成功，记录日志，并抛出异常告知操作失败
		if (!ar.isSuccess()) {
			logger.error(">>>>>>>>>>>>>>>>>>>>>>>>错误信息为：" + ar.getMessage());
			throw new BusinessException("应用管理端向Agent端发送数据库采集任务信息失败，任务ID为"
					+ colSetId + "详情请查看日志");
		} else {
			// 6，这里如果都配置文采则将此次任务的 database_set表中的字段(is_sendok) 更新为是,是表示为当前的配置任务完成
			DboExecute.updatesOrThrow("此次采集任务配置完成,更新状态失败", "UPDATE " + Database_set.TableName + " SET is_sendok = ? WHERE database_id = ?",
					IsFlag.Shi.getCode(), colSetId);
		}
	}

	@Method(desc = "由于源数据库设置表中会保存数据库直连采集和DB文件采集的信息，所以查询得到的某些字段可能为null，" +
			"为了保证传参时合法，不会出现空指针的情况，用该方法对查询得到的数据库设置信息进行重新封装", logicStep = "" +
			"1、判断获取到的数据库连接信息是否有null值，如果有，将null值替换为空字符串然后封装到Database_set对象中")
	@Param(name = "databaseInfo", desc = "查询得到的Map形式的数据库或DB文件采集信息", range = "不为空")
	@Return(desc = "封装好的数据库或DB文件采集信息", range = "Database_set对象")
	private static Database_set getLegalParam(Map<String, Object> databaseInfo) {
		//1、判断获取到的数据库连接信息是否有null值，如果有，将null值替换为空字符串然后封装到Database_set对象中
		Database_set databaseSet = new Database_set();

		databaseSet.setDatabase_name(databaseInfo.get("database_name") == null ? "" :
				(String) databaseInfo.get("database_name"));
		databaseSet.setDatabase_pad(databaseInfo.get("database_pad") == null ? "" :
				(String) databaseInfo.get("database_pad"));
		databaseSet.setDatabase_ip(databaseInfo.get("database_ip") == null ? "" :
				(String) databaseInfo.get("database_ip"));
		databaseSet.setDatabase_port(databaseInfo.get("database_port") == null ? "" :
				(String) databaseInfo.get("database_port"));
		databaseSet.setUser_name(databaseInfo.get("user_name") == null ? "" :
				(String) databaseInfo.get("user_name"));
		databaseSet.setDatabase_drive(databaseInfo.get("database_drive") == null ? "" :
				(String) databaseInfo.get("database_drive"));
		databaseSet.setJdbc_url(databaseInfo.get("jdbc_url") == null ? "" :
				(String) databaseInfo.get("jdbc_url"));
		databaseSet.setDatabase_type(databaseInfo.get("database_type") == null ? "" :
				(String) databaseInfo.get("database_type"));
		databaseSet.setDb_agent(databaseInfo.get("db_agent") == null ? "" :
				(String) databaseInfo.get("db_agent"));
		databaseSet.setPlane_url(databaseInfo.get("plane_url") == null ? "" :
				(String) databaseInfo.get("plane_url"));
		databaseSet.setPlane_url(databaseInfo.get("plane_url") == null ? "" :
				(String) databaseInfo.get("plane_url"));

		return databaseSet;
	}

	@Method(desc = "获取agent解析数据字典表数据",
			logicStep = "1.数据可访问权限处理方式：该表没有对应的用户访问权限限制" +
					"2.调用工具类获取本次访问的agentserver端url" +
					"3、给agent发消息，并获取agent响应" +
					"4.转换agent返回的数据为想要格式")
	@Param(name = "agent_id", desc = "agent信息表主键ID", range = "新增agent时生成")
	@Param(name = "file_path", desc = "采集文件路径", range = "不为空")
	@Param(name = "is_dictionary", desc = "是否存在数据字典", range = "使用（IsFlag）代码项")
	@Param(name = "data_date", desc = "数据日期", range = "是否存在数据字典选择否的时候必选", nullable = true)
	@Param(name = "file_suffix", desc = "文件后缀名", range = "无限制")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Return(desc = "返回agent解析数据字典表数据", range = "无限制")
	public static List<Object_collect_task> getDictionaryTableInfo(long agent_id, String file_path,
	                                                               String is_dictionary, String data_date,
	                                                               String file_suffix, long user_id) {
		// 1.数据可访问权限处理方式：该表没有对应的用户访问权限限制
		// 2.调用工具类获取本次访问的agentserver端url
		String url = AgentActionUtil.getUrl(agent_id, user_id, AgentActionUtil.GETDICTABLE);
		// 3、给agent发消息，并获取agent响应
		String bodyString = new HttpClient()
				.addData("file_suffix", file_suffix)
				.addData("is_dictionary", is_dictionary)
				.addData("data_date", data_date == null ? "" : data_date)
				.addData("file_path", file_path)
				.post(url).getBodyString();
		// 4.转换agent返回的数据为想要格式
		return JsonUtil.toObject(getRespMsg(bodyString, url), new TypeReference<List<Object_collect_task>>() {
		}.getType());
	}

	@Method(desc = "获取agent解析数据字典所有列数据",
			logicStep = "1.数据可访问权限处理方式：该表没有对应的用户访问权限限制" +
					"2.调用工具类获取本次访问的agentserver端url" +
					"3、给agent发消息，并获取agent响应" +
					"4、转换agent返回的数据为想要格式")
	@Param(name = "agent_id", desc = "agent信息表主键ID", range = "新增agent时生成")
	@Param(name = "file_path", desc = "采集文件路径", range = "不为空")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "table_name", desc = "表名称", range = "无限制")
	@Return(desc = "返回agent返回所有列数据", range = "无限制")
	public static List<Object_collect_struct> getDicColumnByTable(long agent_id, String file_path,
	                                                              long user_id, String table_name) {
		// 1.数据可访问权限处理方式：该表没有对应的用户访问权限限制
		// 2.调用工具类获取本次访问的agentserver端url
		String url = AgentActionUtil.getUrl(agent_id, user_id, AgentActionUtil.GETDICCOLUMNBYTABLE);
		// 3、给agent发消息，并获取agent响应
		String bodyString = new HttpClient()
				.addData("file_path", file_path)
				.addData("table_name", table_name)
				.post(url)
				.getBodyString();
		// 4、转换agent返回的数据为想要格式
		return JsonUtil.toObject(getRespMsg(bodyString, url), new TypeReference<List<Object_collect_struct>>() {
		}.getType());
	}

	@Method(desc = "获取agent解析数据字典数据",
			logicStep = "1.数据可访问权限处理方式：该表没有对应的用户访问权限限制" +
					"2.调用工具类获取本次访问的agentserver端url" +
					"3、给agent发消息，并获取agent响应" +
					"4、转换agent返回的数据为想要格式")
	@Param(name = "agent_id", desc = "agent信息表主键ID", range = "新增agent时生成")
	@Param(name = "file_path", desc = "采集文件路径", range = "不为空")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "table_name", desc = "表名称", range = "无限制")
	@Return(desc = "解析agent返回的json数据", range = "无限制")
	public static List<Object_handle_type> getHandleTypeByTable(long agent_id, String file_path,
	                                                            long user_id, String table_name) {
		// 1.数据可访问权限处理方式：该表没有对应的用户访问权限限制
		// 2.调用工具类获取本次访问的agentserver端url
		String url = AgentActionUtil.getUrl(agent_id, user_id,
				AgentActionUtil.GETHANDLETYPEBYTABLE);
		// 3、给agent发消息，并获取agent响应
		String bodyString = new HttpClient()
				.addData("file_path", file_path)
				.addData("table_name", table_name)
				.post(url).getBodyString();
		// 4、转换agent返回的数据为想要格式
		return JsonUtil.toObject(getRespMsg(bodyString, url), new TypeReference<List<Object_handle_type>>() {
		}.getType());
	}

	@Method(desc = "获取与agent交互解析数据字典返回数据", logicStep = "1.数据转换" +
			"2.解包返回数据")
	@Param(name = "bodyString", desc = "agent返回数据对象", range = "无限制")
	@Param(name = "url", desc = "与agent交互地址", range = "无限制")
	@Return(desc = "返回解析后的数据字典数据", range = "无限制")
	private static String getRespMsg(String bodyString, String url) {
		// 1.数据转换
		ActionResult actionResult = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("应用管理端与" + url + "服务交互异常"));
		if (!actionResult.isSuccess()) {
			throw new BusinessException("与agent交互失败，详情请查看agent日志:" + actionResult.getMessage());
		}
		// 2.解包返回数据
		return PackUtil.unpackMsg(actionResult.getData().toString()).get("msg");
	}
}
