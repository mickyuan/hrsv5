package hrds.b.biz.agent.objectcollect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.CollectDataType;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.ObjectCollectType;
import hrds.commons.codes.OperationType;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.AgentActionUtil;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.PackUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;
import java.util.*;

@DocClass(desc = "半结构化采集接口类，处理对象采集的增删改查", author = "zxz", createdate = "2019/9/16 15:02")
public class ObjectCollectAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(ObjectCollectAction.class.getName());

	private static final Type MAPTYPE = new TypeReference<Map<String, Object>>() {
	}.getType();
	private static final Type LISTTYPE = new TypeReference<List<Map<String, Object>>>() {
	}.getType();

	@Method(desc = "获取半结构化采集配置页面初始化的值，当odc_id不为空时，则同时返回object_collect表的值",
			logicStep = "1.根据前端传过来的agent_id获取调用Agent服务的接口" +
					"2.根据url远程调用Agent的后端代码获取采集服务器上的日期、" +
					"时间、操作系统类型和主机名等基本信息" +
					"3.对象采集id不为空则获取对象采集设置表信息")
	@Param(name = "object_collect", desc = "对象采集设置表对象，接收页面传过来的参数Agent_id和odc_id(对象采集id)",
			range = "agent_id不可为空，odc_id可为空", isBean = true)
	@Return(desc = "Agent所在服务器的基本信息、对象采集设置表信息", range = "不会为空")
	public Map<String, Object> searchObjectCollect(Object_collect object_collect) {
		//数据可访问权限处理方式：传入用户需要有Agent信息表对应数据的访问权限
		if (object_collect.getAgent_id() == null) {
			throw new BusinessException("object_collect对象agent_id不能为空");
		}
		//1.根据前端传过来的agent_id获取调用Agent服务的接口
		String url = AgentActionUtil.getUrl(object_collect.getAgent_id(), getUserId()
				, AgentActionUtil.GETSERVERINFO);
		//调用工具类方法给agent发消息，并获取agent响应
		//2.根据url远程调用Agent的后端代码获取采集服务器上的日期、时间、操作系统类型和主机名等基本信息
		HttpClient.ResponseValue resVal = new HttpClient().post(url);
		ActionResult ar = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接远程" + url + "服务异常"));
		if (!ar.isSuccess()) {
			throw new BusinessException("远程连接" + url + "的Agent失败");
		}
		//返回到前端的信息
		Map<String, Object> map = ar.getDataForMap();
		//FIXME 这两个时间和Agent部署主机的时间并不是一个，确定要这样用吗？
		//XXX 这里面是获取本地时间，原先的半结构化采集页面是有服务器时间和本地时间的
		//XXX 服务器时间通过上面得到，本地时间通过下面得到
		map.put("localDate", DateUtil.getSysDate());
		map.put("localtime", DateUtil.getSysTime());
		//3.对象采集id不为空则获取对象采集设置表信息
		//XXX 这里的Odc_id不为空则返回回显数据，为空则不处理
		if (object_collect.getOdc_id() != null) {
			Object_collect object_collect_info = Dbo.queryOneObject(Object_collect.class,
					"SELECT * FROM " + Object_collect.TableName + " WHERE odc_id = ?"
					, object_collect.getOdc_id()).orElseThrow(() -> new BusinessException(
					"根据odc_id" + object_collect.getOdc_id() + "查询不到object_collect表信息"));
			map.put("object_collect_info", object_collect_info);
		}
		return map;
	}

	@Method(desc = "半结构化采集查看表",
			logicStep = "1.数据可访问权限处理方式：通过user_id与agent_id进行访问权限限制" +
					"2.判断当是否存在数据字典选择否的时候数据日前是否为空" +
					"3.获取半结构化与agent服务交互参数" +
					"4.获取解析与agent服务交互返回响应数据" +
					"5.判断当前目录下的数据文件响应信息是否为空" +
					"6.不为空，循环获取当前目录下的数据文件表信息" +
					"7.返回解析后当前目录获取表信息" +
					"8.返回当前目录下的数据文件响应信息")
	@Param(name = "agent_id", desc = "agent信息表主键ID", range = "新增agent时生成")
	@Param(name = "file_path", desc = "文件存储路径", range = "不为空")
	@Param(name = "is_dictionary", desc = "是否存在数据字典", range = "使用（IsFlag）代码项")
	@Param(name = "data_date", desc = "数据日期", range = "是否存在数据字典选择否的时候必选", nullable = true)
	@Param(name = "file_suffix", desc = "文件后缀名", range = "无限制")
	public List<Map<String, Object>> viewTable(String file_path, long agent_id, String is_dictionary,
	                                           String data_date, String file_suffix) {
		// 1.数据可访问权限处理方式：通过user_id与agent_id进行访问权限限制
		// 2.判断当是否存在数据字典选择否的时候数据日前是否为空
		if (IsFlag.Fou == IsFlag.ofEnumByCode(is_dictionary) && StringUtil.isBlank(data_date)) {
			throw new BusinessException("当是否存在数据字典是否的时候，数据日期不能为空");
		}
		// 3.获取半结构化与agent服务交互参数
		String jsonParam = getJsonParamForAgent(file_path, file_suffix, is_dictionary, data_date);
		// 4.获取解析与agent服务交互返回响应数据
		List<Map<String, Object>> tableNames = getJsonDataForAgent(jsonParam, agent_id);
		// 5.判断当前目录下的数据文件响应信息是否为空
		if (!tableNames.isEmpty()) {
			List<Map<String, Object>> tableNameList = new ArrayList<>();
			// 6.不为空，循环获取当前目录下的数据文件表信息
			for (Map<String, Object> tableName : tableNames) {
				Map<String, Object> tableMap = new HashMap<>();
				logger.info(tableName);
				tableMap.put("table_name", tableName.get("table_name").toString());
				tableMap.put("table_ch_name", tableName.get("table_ch_name").toString());
				tableNameList.add(tableMap);
			}
			// 7.返回解析后当前目录获取表信息
			return tableNameList;
		} else {
			// 8.返回当前目录下的数据文件响应信息
			return tableNames;
		}
	}

	@Method(desc = "选择文件路径",
			logicStep = "1.数据可访问权限处理方式：通过user_id与agent_id进行权限控制" +
					"2.根据前端传过来的agent_id获取调用Agent服务的接口" +
					"3.调用工具类方法给agent发消息，并获取agent响应,返回Agent指定目录下的文件及文件夹" +
					"4.返回Agent指定目录下的文件及文件夹")
	@Param(name = "agent_id", desc = "agent信息表主键ID", range = "新增agent时生成")
	@Param(name = "file_path", desc = "文件存储路径", range = "无限制", nullable = true)
	@Return(desc = "返回Agent指定目录下的文件及文件夹", range = "无限制")
	public String selectFilePath(long agent_id, String file_path) {
		// 1.数据可访问权限处理方式：通过user_id与agent_id进行权限控制
		// 2.根据前端传过来的agent_id获取调用Agent服务的接口
		String url = AgentActionUtil.getUrl(agent_id, getUserId(), AgentActionUtil.GETSYSTEMFILEINFO);
		// 3.调用工具类方法给agent发消息，并获取agent响应,返回Agent指定目录下的文件及文件夹
		String bodyString;
		if (StringUtil.isNotBlank(file_path)) {
			bodyString = new HttpClient().addData("pathVal", file_path).post(url).getBodyString();
		} else {
			bodyString = new HttpClient().post(url).getBodyString();
		}
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接远程" + url + "服务异常"));
		if (!ar.isSuccess()) {
			throw new BusinessException("远程连接" + url + "的Agent失败");
		}
		// 4.返回Agent指定目录下的文件及文件夹
		return ar.getData().toString();
	}

	@Method(desc = "保存半结构化文件采集页面信息到对象采集设置表对象，同时返回对象采集id",
			logicStep = "1.数据可访问权限处理方式：该表没有对应的用户访问权限限制" +
					"2.根据obj_number查询半结构化采集任务编号是否重复" +
					"3.保存object_collect表" +
					"4.判断是否存在数据字典选择否的时候数据日期是否为空" +
					"5.保存object_collect表" +
					"6.获取agent解析数据字典返回json格式数据" +
					"7.遍历json对象" +
					"8.object_collect_task表信息入库" +
					"9.获取字段信息" +
					"10.保存对象采集结构信息" +
					"11.保存对象采集数据处理类型对应表信息" +
					"12.保存数据存储表信息入库" +
					"13.返回对象采集ID")
	@Param(name = "object_collect", desc = "对象采集设置表对象，对象中不能为空的字段必须有值",
			range = "不可为空", isBean = true)
	@Return(desc = "对象采集设置表id，新建的id后台生成的所以要返回到前端", range = "不会为空")
	public long addObjectCollect(Object_collect object_collect) {
		// 1.数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//TODO 应该使用一个公共的校验类进行校验

		// 2.根据obj_number查询半结构化采集任务编号是否重复
		long count = Dbo.queryNumber("SELECT count(1) count FROM " + Object_collect.TableName
				+ " WHERE obj_number = ?", object_collect.getObj_number())
				.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if (count > 0) {
			throw new BusinessException("半结构化采集任务编号重复");
		}
		// 3.之前对象采集存在行采集与对象采集两种，目前仅支持行采集,所以默认给
		if (StringUtil.isNotBlank(object_collect.getObject_collect_type())) {
			object_collect.setObject_collect_type(ObjectCollectType.HangCaiJi.getCode());
		}
		// 4.判断是否存在数据字典选择否的时候数据日期是否为空
		if (IsFlag.Fou == IsFlag.ofEnumByCode(object_collect.getIs_dictionary()) &&
				StringUtil.isBlank(object_collect.getData_date())) {
			throw new BusinessException("当是否存在数据字典是否的时候，数据日期不能为空");
		}
		// 如果选择有数据字典数据日期为空
		if (IsFlag.Shi == IsFlag.ofEnumByCode(object_collect.getIs_dictionary())) {
			object_collect.setData_date("");
		}
		// 5.保存object_collect表
		object_collect.setOdc_id(PrimayKeyGener.getNextId());
		object_collect.add(Dbo.db());
		// 6.与agent交互获取agent解析数据字典返回json格式数据
		String jsonParamMap = getJsonParamForAgent(object_collect.getFile_path(),
				object_collect.getFile_suffix(), object_collect.getIs_dictionary(), object_collect.getData_date());
		List<Map<String, Object>> tableNameList = getJsonDataForAgent(jsonParamMap, object_collect.getAgent_id());
		// 7.遍历json对象获取表信息
		for (Map<String, Object> tableNameMap : tableNameList) {
			Object_collect_task objectCollectTask = new Object_collect_task();
			// 8.object_collect_task表信息入库
			addObjectCollectTask(object_collect, tableNameMap, objectCollectTask);
			// 如果没有数据字典，第一次新增则不会加载object_collect_struct，第二次编辑也不会修改库中信息
			boolean isSolr = false;
			if (IsFlag.Shi.getCode().equals(object_collect.getIs_dictionary())) {
				// 9.获取字段信息
				if (tableNameMap.get("column") != null) {
					List<Map<String, Object>> columns = JsonUtil.toObject(tableNameMap.get("column").toString(),
							LISTTYPE);
					if (!columns.isEmpty()) {
						// 10.保存对象采集结构信息
						isSolr = addObjectCollectStruct(objectCollectTask, columns);
					}
				}
				// 如果没有数据字典，第一次新增则不会加载object_handle_type，第二次编辑也不会修改库中信息
				Map<String, String> handleTypeMap = JsonUtil.toObject(tableNameMap.get("handletype").toString()
						, MAPTYPE);
				// 11.保存对象采集数据处理类型对应表信息
				addObjectHandleType(objectCollectTask, handleTypeMap);
			}
			// 12.保存数据存储表信息入库
			addObjectStorage(object_collect, objectCollectTask, isSolr);
		}
		// 13.返回对象采集ID
		return object_collect.getOdc_id();
	}

	@Method(desc = "新增对象采集数据处理类型对应表数据",
			logicStep = "1.数据可访问权限处理方式：该表没有对应的用户访问权限限制" +
					"2.插入insert对应的值" +
					"3.插入update对应的值" +
					"4.插入delete对应的值")
	@Param(name = "objectCollectTask", desc = "对象采集对应信息表对象", range = "不可为空", isBean = true)
	@Param(name = "handleTypeMap", desc = "对象采集数据处理类型对应表信息集合", range = "不可为空")
	private void addObjectHandleType(Object_collect_task objectCollectTask, Map<String, String> handleTypeMap) {
		// 1.数据可访问权限处理方式：该表没有对应的用户访问权限限制
		Object_handle_type object_handle_type = new Object_handle_type();
		// 2.插入insert对应的值
		object_handle_type.setObject_handle_id(PrimayKeyGener.getNextId());
		object_handle_type.setOcs_id(objectCollectTask.getOcs_id());
		object_handle_type.setHandle_type(OperationType.INSERT.getCode());
		object_handle_type.setHandle_value(handleTypeMap.get("insert"));
		object_handle_type.add(Dbo.db());
		// 3.插入update对应的值
		object_handle_type.setObject_handle_id(PrimayKeyGener.getNextId());
		object_handle_type.setHandle_type(OperationType.UPDATE.getCode());
		object_handle_type.setHandle_value(handleTypeMap.get("update"));
		object_handle_type.add(Dbo.db());
		// 4.插入delete对应的值
		object_handle_type.setObject_handle_id(PrimayKeyGener.getNextId());
		object_handle_type.setHandle_type(OperationType.DELETE.getCode());
		object_handle_type.setHandle_value(handleTypeMap.get("delete"));
		object_handle_type.add(Dbo.db());
	}

	@Method(desc = "新增数据存储信息",
			logicStep = "1.数据可访问权限处理方式：该表没有对应的用户访问权限限制" +
					"2.封装数据存储表信息" +
					"3.保存数据存储信息")
	@Param(name = "object_collect", desc = "对象采集设置表对象", range = "不可为空", isBean = true)
	@Param(name = "objectCollectTask", desc = "对象采集对应信息表对象", range = "不可为空", isBean = true)
	@Param(name = "isSolr", desc = "是否入solr标志", range = "不可为空")
	private void addObjectStorage(Object_collect object_collect, Object_collect_task objectCollectTask,
	                              boolean isSolr) {
		// 1.数据可访问权限处理方式：该表没有对应的用户访问权限限制
		// 2.封装数据存储表信息
		Object_storage object_storage = new Object_storage();
		object_storage.setObj_stid(PrimayKeyGener.getNextId());
		object_storage.setOcs_id(objectCollectTask.getOcs_id());
		if (IsFlag.Fou.getCode().equals(object_collect.getIs_dictionary())) {
			object_storage.setIs_solr(IsFlag.Fou.getCode());
		} else if (isSolr) {
			object_storage.setIs_solr(IsFlag.Shi.getCode());
		} else {
			object_storage.setIs_solr(IsFlag.Fou.getCode());
		}
		object_storage.setIs_hbase(IsFlag.Shi.getCode());
		object_storage.setIs_hdfs(IsFlag.Fou.getCode());
		// 3.保存数据存储信息
		object_storage.add(Dbo.db());
	}

	@Method(desc = "新增对象采集对应信息",
			logicStep = "1.数据可访问权限处理方式：该表没有对应的用户访问权限限制" +
					"2.封装对象采集对应信息" +
					"3.判断数据字典是否存在" +
					"4.判断数据更新方式是否存在" +
					"5.object_collect_task表信息入库")
	@Param(name = "object_collect", desc = "对象采集设置表对象", range = "不可为空", isBean = true)
	@Param(name = "tableNameMap", desc = "agent解析数据文件响应表信息", range = "不可为空")
	@Param(name = "objectCollectTask", desc = "对象采集对应信息表对象", range = "不可为空", isBean = true)
	private void addObjectCollectTask(Object_collect object_collect, Map<String, Object> tableNameMap,
	                                  Object_collect_task objectCollectTask) {
		// 1.数据可访问权限处理方式：该表没有对应的用户访问权限限制
		// 2.封装对象采集对应信息
		objectCollectTask.setOcs_id(PrimayKeyGener.getNextId());
		objectCollectTask.setAgent_id(object_collect.getAgent_id());
		objectCollectTask.setOdc_id(object_collect.getOdc_id());
		String tableName = tableNameMap.get("table_name").toString();
		String zh_name = tableNameMap.get("table_ch_name").toString();
		objectCollectTask.setEn_name(tableName != null ? tableName : "");
		objectCollectTask.setZh_name(zh_name != null ? zh_name : "");
		objectCollectTask.setCollect_data_type(CollectDataType.JSON.getCode());
		objectCollectTask.setDatabase_code(object_collect.getDatabase_code());
		// 3.判断数据字典是否存在
		if (tableNameMap.get("everyline") != null) {
			// 数据字典不存在，从agent获取
			objectCollectTask.setFirstline(tableNameMap.get("everyline").toString());
		} else {
			// 数据字典存在，每一行数据为空
			objectCollectTask.setFirstline("");
		}
		// 4.判断数据更新方式是否存在
		if (tableNameMap.get("updatetype") == null) {
			// 无数据字典，数据更新方式为空
			objectCollectTask.setUpdatetype("");
		} else {
			// 有数据字典，从agent获取
			objectCollectTask.setUpdatetype(tableNameMap.get("updatetype").toString());
		}
		// 5.object_collect_task表信息入库
		objectCollectTask.add(Dbo.db());
	}

	@Method(desc = "获取agent解析数据字典返回json格式数据",
			logicStep = "1.数据可访问权限处理方式：该表没有对应的用户访问权限限制" +
					"2.调用工具类获取本次访问的agentserver端url" +
					"3、给agent发消息，并获取agent响应" +
					"4、如果测试连接不成功，则抛异常给前端，说明连接失败，如果成功，则不做任务处理" +
					"5.解析agent返回的json数据")
	@Param(name = "jsonParamMap", desc = "发送到agent的json格式参数", range = "不可为空")
	@Param(name = "agent_id", desc = "agent_info表主键", range = "新增agent时生成")
	@Return(desc = "解析agent返回的json数据", range = "无限制")
	private List<Map<String, Object>> getJsonDataForAgent(String jsonParamMap, long agent_id) {
		// 1.数据可访问权限处理方式：该表没有对应的用户访问权限限制
		// 2.调用工具类获取本次访问的agentserver端url
		String url = AgentActionUtil.getUrl(agent_id, getUserId(), AgentActionUtil.PARSEDATADICTIONARY);
		// 3、给agent发消息，并获取agent响应
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("objectCollectParam", jsonParamMap)
				.post(url);
		// 4、如果测试连接不成功，则抛异常给前端，说明连接失败，如果成功，则不做任务处理
		ActionResult actionResult = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class).
				orElseThrow(() -> new BusinessException("应用管理端与" + url + "服务交互异常"));
		logger.info("=====agent响应信息====" + actionResult.getMessage());
		if (!actionResult.isSuccess()) {
			throw new BusinessException("连接失败," + actionResult.getMessage());
		}
		Object data = actionResult.getData().toString();
		// 5.解析agent返回的json数据
		Map<String, String> jsonMsg = PackUtil.unpackMsg(data.toString());
		Map<String, Object> objectCollectMap = JsonUtil.toObject(jsonMsg.get("msg"), MAPTYPE);
		return JsonUtil.toObject(objectCollectMap.get("tablename").toString(), LISTTYPE);
	}

	@Method(desc = "查询半结构化采集列结构信息(采集列结构）",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.有数据字典，查询对象采集结构信息" +
					"3.没有数据字典查询第一行数据" +
					"4.解析json获取树结构信息并返回" +
					"5.返回半结构化采集列结构信息")
	@Param(name = "ocs_id", desc = "对象采集任务编号(对象采集对应信息表ID）", range = "新增对象采集任务时生成")
	@Return(desc = "返回半结构化采集列结构信息", range = "无限制")
	public Map<String, Object> searchCollectColumnStruct(long ocs_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.有数据字典，查询对象采集结构信息
		List<Map<String, Object>> objectStructList = Dbo.queryList("select * from "
				+ Object_collect_struct.TableName + " where ocs_id=? order by col_seq", ocs_id);
		Map<String, Object> objColStructMap = new HashMap<>();
		objColStructMap.put("objectStructList", objectStructList);
		// 3.没有数据字典查询第一行数据
		List<Object> firstLineList = getFirstLineInfo(ocs_id);
		String isDictionary = IsFlag.Shi.getCode();
		if (!firstLineList.isEmpty()) {
			isDictionary = IsFlag.Fou.getCode();
			// 4.解析json获取树结构信息并返回
			JSONArray treeConstruct = parseEveryFirstLine(firstLineList.get(0).toString(), "");
			objColStructMap.put("treeConstruct", treeConstruct);
		}
		objColStructMap.put("isDictionary", isDictionary);
		// 5.返回半结构化采集列结构信息
		return objColStructMap;
	}

	@Method(desc = "无数据字典时查询第一行数据",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.返回无数据字典时查询第一行数据")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "新增对象采集任务时生成")
	@Return(desc = "返回无数据字典时查询第一行数据", range = "无限制")
	private List<Object> getFirstLineInfo(long ocs_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.返回无数据字典时查询第一行数据
		return Dbo.queryOneColumnList("select firstline from " + Object_collect.TableName +
				" t1 left join " + Object_collect_task.TableName + " t2 on t1.odc_id = t2.odc_id" +
				" where t2.ocs_id = ? and t1.is_dictionary = ?", ocs_id, IsFlag.Fou.getCode());
	}

	@Method(desc = "获取对象采集树节点信息", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.解析json获取树结构信息并返回" +
			"3.获取树信息失败")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "新增对象采集任务时生成")
	@Param(name = "location", desc = "树节点位置，不是根节点则格式如（columns,column_id）", range = "无限制")
	@Return(desc = "获取对象采集树节点信息", range = "无限制")
	public JSONArray getObjectCollectTreeInfo(long ocs_id, String location) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		List<Object> firstLineList = getFirstLineInfo(ocs_id);
		if (!firstLineList.isEmpty() && StringUtil.isNotBlank(location)) {
			// 2.解析json获取树结构信息并返回
			return parseEveryFirstLine(firstLineList.get(0).toString(), location);
		} else {
			// 3.获取树信息失败
			throw new BusinessException("当前对象采集对应的第一行数据不存在，树节点为空，treeId="
					+ location + ",ocs_id=" + ocs_id);
		}
	}

	@Method(desc = "解析没有数据字典的第一行数据",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.解析第一行数据，第一行数据为jsonArray格式，解析错误会跳到6" +
					"3.获取第一层第一个对象，因为每个对象的格式是相同的" +
					"4.判断第一层第一个对象是否为jsonArray格式，如果是，获取第二层第一个对象" +
					"5.判断是否为jsonObject格式" +
					"5.1如果location不为空，则通过当前树节点去查询当前节点下的信息" +
					"5.2根据树节点获取当前树节点信息" +
					"6.解析第一行数据，第一行数据格式为jsonObject" +
					"6.1如果location不为空，则通过当前树节点去查询当前节点下的信息" +
					"6.2根据树节点获取当前树节点信息")
	@Param(name = "firstLine", desc = "第一行数据", range = "无限制")
	@Param(name = "location", desc = "树节点位置，不是根节点则格式如（columns,column_id）", range = "无限制")
	@Return(desc = "返回当前树节点信息", range = "无限制")
	private JSONArray parseEveryFirstLine(String firstLine, String location) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		JSONArray treeInfo;
		String[] treeId = location.split(",");
		try {
			// 2.解析第一行数据，第一行数据为jsonArray格式,解析错误会跳到6
			JSONArray parseArray = JSONArray.parseArray(firstLine);
			// 3.获取第一层第一个对象，因为每个对象的格式是相同的
			Object everyObject = parseArray.getObject(0, Object.class);
			// 4.判断第一层第一个对象是否为jsonArray格式，如果是，获取第二层第一个对象
			if (everyObject instanceof JSONArray) {
				JSONArray jsonarray = (JSONArray) everyObject;
				everyObject = jsonarray.getObject(0, Object.class);
			}
			// 5.判断是否为jsonObject格式
			if (everyObject instanceof JSONObject) {
				JSONObject jsonobject = (JSONObject) everyObject;
				// 5.1如果location不为空，则通过当前树节点去查询当前节点下的信息
				if (StringUtil.isNotBlank(location)) {
					jsonobject = makeJsonFileToJsonObj(jsonobject, treeId[treeId.length - 1]);
				}
				// 5.2根据树节点获取当前树节点信息
				treeInfo = getTree(jsonobject, location);
			} else {
				throw new BusinessException("解析json结构错误 jsonArray下面不存在jsonObject");
			}
		} catch (JSONException e) {
			try {
				// 6.解析第一行数据，第一行数据格式为jsonObject
				JSONObject parseObject = JSONObject.parseObject(firstLine);
				// 6.1如果location不为空，则通过当前树节点去查询当前节点下的信息
				if (StringUtil.isNotBlank(location)) {
					parseObject = makeJsonFileToJsonObj(parseObject, treeId[treeId.length - 1]);
				}
				// 6.2根据树节点获取当前树节点信息
				treeInfo = getTree(parseObject, location);
			} catch (JSONException e2) {
				throw new BusinessException("既不是jsonArray，也不是jsonObject");
			}
		}
		return treeInfo;
	}

	@Method(desc = "获取当前节点树信息", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.判断树节点是否为空" +
			"3.遍历获取当前树节点下的数据" +
			"4.返回当前树节点信息")
	@Param(name = "jsonObject", desc = "当前树节点信息", range = "不为空")
	@Param(name = "keys", desc = "当前树节点位置", range = "不为空")
	@Return(desc = "返回当前节点树信息", range = "不为空")
	private JSONArray getTree(JSONObject jsonObject, String keys) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.判断树节点是否为空
		if (StringUtil.isBlank(keys)) {
			keys = "";
		} else {
			keys += ",";
		}
		JSONArray array = new JSONArray();
		Set<Map.Entry<String, Object>> entrySet = jsonObject.entrySet();
		int rowcount = 0;
		// 3.遍历获取当前树节点下的数据
		for (Map.Entry<String, Object> entry : entrySet) {
			JSONObject resultObject = new JSONObject();
			String key = entry.getKey();
			Object object = jsonObject.get(key);
			boolean isParent;
			isParent = object instanceof JSONObject || object instanceof JSONArray;
			// 字段位置
			resultObject.put("location", keys + key);
			resultObject.put("description", key);
			resultObject.put("id", key);
			resultObject.put("isParent", isParent);
			resultObject.put("name", key);
			resultObject.put("pId", "~" + rowcount);
			resultObject.put("rootName", "~" + rowcount);
			array.add(resultObject);
			rowcount++;
		}
		// 4.返回当前树节点信息
		return array;
	}

	@Method(desc = "获取当前树节点对应信息", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.判断对象是jsonArray还是jsonObject" +
			"3.返回通过树节点获取当前树节点对应信息")
	@Param(name = "jsonObject", desc = "当前树节点对应信息", range = "不为空")
	@Param(name = "nextKey", desc = "当前树节点", range = "不为空")
	@Return(desc = "返回当前树节点对应信息", range = "不为空")
	private JSONObject makeJsonFileToJsonObj(JSONObject jsonObject, String nextKey) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		Object object = jsonObject.get(nextKey);
		JSONObject jsonobject;
		// 2.判断对象是jsonArray还是jsonObject
		if (object instanceof JSONArray) {
			JSONArray jsonarray = (JSONArray) object;
			object = jsonarray.getObject(0, Object.class);
			if (object instanceof JSONObject) {
				jsonobject = (JSONObject) object;
			} else {
				throw new BusinessException("解析json结构错误 jsonArray下面不存在jsonObject");
			}
		} else if (object instanceof JSONObject) {
			jsonobject = (JSONObject) object;
		} else {
			throw new BusinessException("json格式错误，既不是jsonArray也不是jsonObject");
		}
		// 3.返回通过树节点获取当前树节点对应信息
		return jsonobject;
	}

	@Method(desc = "获取当前表的码表信息(操作码表)", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.查询当前表的码表信息")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "新增对象采集任务时生成")
	@Return(desc = "返回当前表的码表信息", range = "无限制")
	public List<Object_handle_type> searchObjectHandleType(long ocs_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.查询当前表的码表信息
		return Dbo.queryList(Object_handle_type.class, "select * from " + Object_handle_type.TableName
				+ " where ocs_id=? order by handle_type", ocs_id);
	}

	@Method(desc = "保存表的码表信息（操作码表）", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.解析json为对象采集结构信息" +
			"3.循环保存对象采集数据处理类型对应表信息")
	@Param(name = "handleType", desc = "jsonArray格式的码表类型信息(handle_type,handle_value为key,对应值为value)",
			range = "无限制")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "新增对象采集任务时生成")
	public void saveHandleType(String handleType, long ocs_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		Type type = new TypeReference<List<Object_handle_type>>() {
		}.getType();
		// 2.解析json为对象采集结构信息
		List<Object_handle_type> handleTypeList = JsonUtil.toObject(handleType, type);
		logger.info("===================" + ocs_id);
		// 3.先删除原来的码表信息
		Dbo.execute("delete from " + Object_handle_type.TableName + " where ocs_id = ?", ocs_id);
		// 3.循环保存对象采集数据处理类型对应表信息
		for (Object_handle_type object_handle_type : handleTypeList) {
			object_handle_type.setObject_handle_id(PrimayKeyGener.getNextId());
			object_handle_type.setOcs_id(ocs_id);
			object_handle_type.add(Dbo.db());
		}
	}

	@Method(desc = "保存对象采集结构信息（采集列结构）", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.解析json为对象采集结构信息" +
			"3.循环保存对象采集结构信息入库，获取结构信息id" +
			"4.删除非新保存结构信息")
	@Param(name = "collectStruct", desc = "jsonArray格式的字符串(is_rowkey,is_solr,is_hbase,is_key,is_operate," +
			"column_name,column_type,col_seq(字段序号）,columnposition(字段位置）为key，对应值为value)",
			range = "无限制")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "新增对象采集任务时生成")
	public void saveCollectColumnStruct(long ocs_id, String collectStruct) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		Type type = new TypeReference<List<Object_collect_struct>>() {
		}.getType();
		// 2.解析json为对象采集结构信息
		List<Object_collect_struct> collectStructList = JsonUtil.toObject(collectStruct, type);
		List<Long> structIdList = new ArrayList<>();
		long struct_id;
		// 3.循环保存对象采集结构信息入库，获取结构信息id
		for (Object_collect_struct object_collect_struct : collectStructList) {
			if (null == object_collect_struct.getStruct_id()) {
				object_collect_struct.setOcs_id(object_collect_struct.getOcs_id());
				struct_id = PrimayKeyGener.getNextId();
				object_collect_struct.setOcs_id(ocs_id);
				object_collect_struct.setStruct_id(struct_id);
				object_collect_struct.setIs_rowkey(IsFlag.Shi.getCode());
				object_collect_struct.setIs_solr(IsFlag.Fou.getCode());
				object_collect_struct.setIs_hbase(IsFlag.Shi.getCode());
				object_collect_struct.add(Dbo.db());
			} else {
				object_collect_struct.update(Dbo.db());
			}
			structIdList.add(object_collect_struct.getStruct_id());
		}
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		// 4.删除非新保存结构信息
		asmSql.addSql("delete from " + Object_collect_struct.TableName + " where ocs_id=? and struct_id not in (");
		asmSql.addParam(ocs_id);
		for (int i = 0; i < structIdList.size(); i++) {
			asmSql.addSql("?");
			asmSql.addParam(structIdList.get(i));
			if (i != structIdList.size() - 1) {
				asmSql.addSql(",");
			}
		}
		asmSql.addSql(")");
		Dbo.execute(asmSql.sql(), asmSql.params());
	}

	@Method(desc = "保存对象文件配置信息时检查字段(采集文件设置)",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.解析json对象为对象采集对应信息" +
					"3.循环检查英文名是否为空" +
					"4.循环检查中文名是否为空" +
					"5.循环检查采集列结构是否为空" +
					"6.循环检查操作码表是否为空" +
					"7.循环检查操作字段是否为1个")
	@Param(name = "objColTask", desc = "jsonArray格式的对象采集对应信息", range = "无限制")
	public void checkFieldsForSaveObjectCollectTask(String objColTask) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		Type type = new TypeReference<List<Object_collect_task>>() {
		}.getType();
		// 2.解析json对象为对象采集对应信息
		List<Object_collect_task> objectCollectTaskList = JsonUtil.toObject(objColTask, type);
		for (int i = 0; i < objectCollectTaskList.size(); i++) {
			// 3.循环检查英文名是否为空
			if (StringUtil.isBlank(objectCollectTaskList.get(i).getEn_name())) {
				throw new BusinessException("第" + (i + 1) + "行表英文名为空，请检查");
			}
			// 4.循环检查中文名是否为空
			if (StringUtil.isBlank(objectCollectTaskList.get(i).getZh_name())) {
				throw new BusinessException("第" + (i + 1) + "行表" + objectCollectTaskList.get(i).getEn_name() +
						"中文名为空，请检查");
			}
			// 5.循环检查采集列结构是否为空
			if (Dbo.queryNumber("select count(*) from " + Object_collect_struct.TableName +
					" where ocs_id=?", objectCollectTaskList.get(i).getOcs_id())
					.orElseThrow(() -> new BusinessException("sql查询错误！")) == 0) {
				throw new BusinessException("第" + (i + 1) + "行表" + objectCollectTaskList.get(i).getEn_name() +
						"采集列结构为空，请检查");
			}
			// 6.循环检查操作码表是否为空
			if (Dbo.queryNumber("select count(*) from " + Object_handle_type.TableName +
					" where ocs_id=?", objectCollectTaskList.get(i).getOcs_id())
					.orElseThrow(() -> new BusinessException("sql查询错误！")) == 0) {
				throw new BusinessException("第" + (i + 1) + "行表" + objectCollectTaskList.get(i).getEn_name() +
						"操作码表为空，请检查");
			}
			// 7.循环检查操作字段是否为1个
			if (Dbo.queryNumber("select count(*) from " + Object_collect_struct.TableName +
							" where ocs_id=? and is_operate=?", objectCollectTaskList.get(i).getOcs_id(),
					IsFlag.Shi.getCode()).orElseThrow(() -> new BusinessException("sql查询错误！")) != 1) {
				throw new BusinessException("第" + (i + 1) + "行表" + objectCollectTaskList.get(i).getEn_name() +
						"操作字段不为1个，请检查");
			}
		}
	}

	@Method(desc = "重写数据字典",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.根据对象采集ID当前半结构化采集任务是否存在数据字典" +
					"3.判断是否数据字典已存在，已存在不重写数据字典")
	@Param(name = "odc_id", desc = "对象采集id", range = "新增对象采集时生成")
	private void rewriteDataDictionary(long odc_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		Map<String, Object> dictionaryMap = new HashMap<>();
		List<Object> dictionaryList = new ArrayList<>();
		// 2.根据对象采集ID当前半结构化采集任务是否存在数据字典
		List<Object> isDictionaryList = Dbo.queryOneColumnList("select is_dictionary from "
				+ Object_collect.TableName + " where odc_id=?", odc_id);
		if (!isDictionaryList.isEmpty()) {
			// 3.判断是否数据字典已存在，已存在不重写数据字典
			if (IsFlag.Shi == IsFlag.ofEnumByCode(isDictionaryList.get(0).toString())) {
				logger.info("已经存在数据字典，不需要重写数据字典");
			} else {
				List<Object> filePathList = Dbo.queryOneColumnList("select file_path from "
						+ Object_collect.TableName + " where odc_id=?", odc_id);
				dictionaryMap.put("file_path", filePathList.get(0));
				List<Map<String, Object>> objCollectTaskList = Dbo.queryList("select * from "
						+ Object_collect_task.TableName + " where odc_id=?", odc_id);
				for (Map<String, Object> objectMap : objCollectTaskList) {
					Map<String, Object> tableMap = new HashMap<>();
					tableMap.put("table_name", objectMap.get("en_name"));
					tableMap.put("table_ch_name", objectMap.get("zh_name"));
					tableMap.put("updatetype", objectMap.get("updatetype"));
					tableMap.put("operationposition", objectMap.get("operationposition"));
					long ocs_id = Long.parseLong(objectMap.get("ocs_id").toString());
					List<Map<String, Object>> objCollStructList = Dbo.queryList("select * from "
							+ Object_collect_struct.TableName + " where ocs_id=?", ocs_id);
					List<Map<String, Object>> columnList = new ArrayList<>();
					for (Map<String, Object> objCollStructMap : objCollStructList) {
						Map<String, Object> columnMap = new HashMap<>();
						columnMap.put("column_type", objCollStructMap.get("column_type"));
						columnMap.put("column_id", objCollStructMap.get("col_seq"));
						columnMap.put("column_name", objCollStructMap.get("column_name"));
						columnMap.put("is_rowkey", objCollStructMap.get("is_rowkey"));
						columnMap.put("is_key", objCollStructMap.get("is_key"));
						columnMap.put("is_hbase", objCollStructMap.get("is_hbase"));
						columnMap.put("is_solr", objCollStructMap.get("is_solr"));
						columnMap.put("is_operate", objCollStructMap.get("is_operate"));
						columnMap.put("columnposition", objCollStructMap.get("columnposition"));
						columnList.add(columnMap);
					}
					tableMap.put("columns", columnList);
					List<Map<String, Object>> objHandleTypeList = Dbo.queryList("select * from "
							+ Object_handle_type.TableName + " where ocs_id=?", ocs_id);
					Map<String, Object> handleTypeMap = new HashMap<>();
					for (Map<String, Object> stringObjectMap : objHandleTypeList) {
						String handle_type = stringObjectMap.get("handle_type").toString();
						handleTypeMap.put(OperationType.ofValueByCode(handle_type),
								stringObjectMap.get("handle_value"));
					}
					tableMap.put("handletype", handleTypeMap);
					dictionaryList.add(tableMap);
				}
				dictionaryMap.put("dictionaryParam", JsonUtil.toJson(dictionaryList));
				// 2.调用工具类获取本次访问的agentserver端url
				List<Object> agentIdList = Dbo.queryOneColumnList("select agent_id from "
						+ Object_collect.TableName + " where odc_id = ?", odc_id);
				String url = AgentActionUtil.getUrl(Long.parseLong(agentIdList.get(0).toString()), getUserId(),
						AgentActionUtil.WRITEDICTIONARY);
				// 3、给agent发消息，并获取agent响应
				HttpClient.ResponseValue resVal = new HttpClient()
						.addData("dictionaryParam", PackUtil.packMsg(JsonUtil.toJson(dictionaryMap)))
						.post(url);
				// 4、如果测试连接不成功，则抛异常给前端，说明连接失败，如果成功，则不做任务处理
				ActionResult actionResult = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class).
						orElseThrow(() -> new BusinessException("应用管理端与" + url + "服务交互异常"));
				if (!actionResult.isSuccess()) {
					throw new BusinessException("半结构化采集重写数据字典连接agent服务失败" + actionResult.getMessage());
				}
			}
		} else {
			throw new BusinessException("记录半结构化首页采集信息丢失");
		}

	}

	@Method(desc = "更新半结构化文件采集页面信息到对象采集设置表对象，同时返回对象采集id",
			logicStep = "1.数据可访问权限处理方式：该表没有对应的用户访问权限限制" +
					"2.判断是否存在数据字典选择否的时候数据日期是否为空" +
					"3.更新object_collect表" +
					"4.获取传递到agent的参数" +
					"5.获取agent解析数据字典返回json格式数据" +
					"6.如果数据字典减少了表，则需要删除之前在数据库中记录的表" +
					"7.遍历数据字典信息循环更新半结构化对应信息" +
					"8.通过对象采集id查询对象采集对应信息" +
					"9.更新保存对象采集对应信息" +
					"10.获取字段信息" +
					"11.保存对象采集结构信息" +
					"12.保存对象采集数据处理类型对应表" +
					"13.返回对象采集ID")
	@Param(name = "object_collect", desc = "对象采集设置表对象", range = "不可为空", isBean = true)
	@Return(desc = "返回对象采集配置ID", range = "不能为空")
	public long updateObjectCollect(Object_collect object_collect) {
		// 1.数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//TODO 应该使用一个公共的校验类进行校验
		if (object_collect.getOdc_id() == null) {
			throw new BusinessException("主键odc_id不能为空");
		}
		// 2.判断是否存在数据字典选择否的时候数据日期是否为空
		if (IsFlag.Fou == IsFlag.ofEnumByCode(object_collect.getIs_dictionary()) &&
				StringUtil.isBlank(object_collect.getData_date())) {
			throw new BusinessException("当是否存在数据字典是否的时候，数据日期不能为空");
		}
		// 3.更新object_collect表
		object_collect.update(Dbo.db());
		// 4.获取传递到agent的参数
		String jsonParam = getJsonParamForAgent(object_collect.getFile_path(), object_collect.getFile_suffix(),
				object_collect.getIs_dictionary(), object_collect.getData_date());
		// 5.获取agent解析数据字典返回json格式数据
		List<Map<String, Object>> tableNames = getJsonDataForAgent(jsonParam, object_collect.getAgent_id());
		List<String> tableNameList = new ArrayList<>();
		// 6.获取所有表集合
		for (Map<String, Object> tableNameMap : tableNames) {
			tableNameList.add(tableNameMap.get("table_name").toString());
		}
		// 7.如果数据字典减少了表，则需要删除之前在数据库中记录的表
		List<Map<String, Object>> objCollectTaskList = Dbo.queryList("select en_name,ocs_id from "
				+ Object_collect_task.TableName + " where odc_id =?", object_collect.getOdc_id());
		for (Map<String, Object> objectMap : objCollectTaskList) {
			String en_name = objectMap.get("en_name").toString();
			// 如果数据库中有但是字典中没有的表，将删除
			if (!tableNameList.contains(en_name)) {
				Long ocs_id = new Long(objectMap.get("ocs_id").toString());
				// 删除对象采集对应信息
				Dbo.execute("delete from " + Object_collect_task.TableName + " where ocs_id=?", ocs_id);
				// 删除对象采集结构信息
				Dbo.execute("delete from " + Object_collect_struct.TableName + " where ocs_id=?", ocs_id);
				// 删除存储目的地
				Dbo.execute("delete from " + Object_storage.TableName + " where ocs_id =?", ocs_id);
				// 删除处理方式
				Dbo.execute("delete from " + Object_handle_type.TableName + " where ocs_id =?", ocs_id);
			}
		}
		// 8.遍历数据字典信息循环更新半结构化对应信息
		for (Map<String, Object> tableNameMap : tableNames) {
			String tableName = tableNameMap.get("table_name").toString();
			// 9.通过对象采集id以及表名查询对象采集对应信息
			Map<String, Object> taskMap = Dbo.queryOneObject("select * from "
							+ Object_collect_task.TableName + " where odc_id = ? and en_name = ?",
					object_collect.getOdc_id(), tableName);
			// agent有，数据库没有直接新增
			Object_collect_task objectCollectTask = new Object_collect_task();
			if (taskMap.isEmpty()) {
				addObjectCollectTask(object_collect, tableNameMap, objectCollectTask);
			} else {
				// 第一行数据只有数据字典不存在的时候才会有值
				if (IsFlag.Fou == IsFlag.ofEnumByCode(object_collect.getIs_dictionary())) {
					objectCollectTask.setFirstline(tableNameMap.get("everyline").toString());
				}
				objectCollectTask.setZh_name(tableNameMap.get("description").toString());
				objectCollectTask.setEn_name(tableName);
				// 如果有数据字典updatetype需要更新
				if (IsFlag.Shi == IsFlag.ofEnumByCode(object_collect.getIs_dictionary())) {
					objectCollectTask.setUpdatetype(tableNameMap.get("updatetype").toString());
				}
				objectCollectTask.setOcs_id(taskMap.get("ocs_id").toString());
				// 10.更新保存对象采集对应信息
				objectCollectTask.update(Dbo.db());
			}
			// 如果没有数据字典，第一次新增则不会加载object_collect_struct，第二次编辑也不会修改库中信息
			if (IsFlag.Shi == IsFlag.ofEnumByCode(object_collect.getIs_dictionary())) {
				// 11.获取字段信息
				List<Map<String, Object>> columns = JsonUtil.toObject(tableNameMap.get("column").toString(),
						LISTTYPE);
				if (!columns.isEmpty()) {
					Dbo.execute("delete from " + Object_collect_struct.TableName + " where ocs_id = ?",
							objectCollectTask.getOcs_id());
					addObjectCollectStruct(objectCollectTask, columns);
				}
				// 如果没有数据字典，第一次新增则不会加载object_handle_type，第二次编辑也不会修改库中信息
				Map<String, String> handleTypMap = JsonUtil.toObject(tableNameMap.get("handletype").toString(),
						MAPTYPE);
				// 13.保存对象采集数据处理类型对应表
				Dbo.execute("delete from " + Object_handle_type.TableName + " where ocs_id=?",
						objectCollectTask.getOcs_id());
				addObjectHandleType(objectCollectTask, handleTypMap);
			}
		}
		// 14.返回对象采集ID
		return object_collect.getOdc_id();
	}

	@Method(desc = "保存对象采集结构信息", logicStep = "1.数据可访问权限处理方式：该方法没有用户访问权限限制" +
			"2.封装对象采集结构信息" +
			"3.保存对象采集结构信息")
	@Param(name = "objectCollectTask", desc = "对象采集对应信息", range = "不为空", isBean = true)
	@Param(name = "columns", desc = "列信息集合", range = "不为空")
	@Param(name = "isSolr", desc = "是否入solr标志", range = "使用（IsFlag）代码项")
	private boolean addObjectCollectStruct(Object_collect_task objectCollectTask,
	                                       List<Map<String, Object>> columns) {
		// 1.数据可访问权限处理方式：该方法没有用户访问权限限制
		boolean isSolr = false;
		for (int j = 0; j < columns.size(); j++) {
			Map<String, Object> columnMap = columns.get(j);
			String is_solr = columnMap.get("is_solr").toString();
			if (!isSolr && IsFlag.Shi == IsFlag.ofEnumByCode(is_solr)) {
				isSolr = true;
			}
			Object_collect_struct object_collect_struct = new Object_collect_struct();
			// 2.封装对象采集结构信息
			object_collect_struct.setStruct_id(PrimayKeyGener.getNextId());
			object_collect_struct.setOcs_id(objectCollectTask.getOcs_id());
			object_collect_struct.setColumn_name(columnMap.get("columnname").toString());
			object_collect_struct.setColumn_type(columnMap.get("columntype").toString());
			object_collect_struct.setIs_key(columnMap.get("is_key").toString());
			object_collect_struct.setIs_hbase(columnMap.get("is_hbase").toString());
			object_collect_struct.setIs_rowkey(columnMap.get("is_rowkey").toString());
			object_collect_struct.setIs_solr(is_solr);
			object_collect_struct.setIs_operate(columnMap.get("is_operate").toString());
			object_collect_struct.setCol_seq(String.valueOf(j));
			object_collect_struct.setColumnposition(columnMap.get("columnposition").toString());
			object_collect_struct.setData_desc(object_collect_struct.getColumn_name());
			// 3.保存对象采集结构信息
			object_collect_struct.add(Dbo.db());
		}
		return isSolr;
	}


	@Method(desc = "获取传递到agent的参数", logicStep = "1.数据可访问权限处理方式：该方法没有用户访问权限限制" +
			"2.封装半结构化采集与agent交互参数" +
			"3.返回半结构化采集与agent交互参数")
	@Param(name = "file_path", desc = "文件存储路径", range = "不为空")
	@Param(name = "is_dictionary", desc = "是否存在数据字典", range = "使用（IsFlag）代码项")
	@Param(name = "data_date", desc = "数据日期", range = "是否存在数据字典选择否的时候必选", nullable = true)
	@Param(name = "file_suffix", desc = "文件后缀名", range = "无限制")
	@Return(desc = "返回半结构化采集与agent交互参数", range = "不能为空")
	private String getJsonParamForAgent(String file_path, String file_suffix, String is_dictionary,
	                                    String data_date) {
		// 1.数据可访问权限处理方式：该方法没有用户访问权限限制
		// 2.封装半结构化采集与agent交互参数
		Map<String, String> jsonParamMap = new HashMap<>();
		jsonParamMap.put("file_suffix", file_suffix);
		jsonParamMap.put("is_dictionary", is_dictionary);
		jsonParamMap.put("data_date", data_date);
		jsonParamMap.put("file_path", file_path);
		// 3.返回半结构化采集与agent交互参数
		return PackUtil.packMsg(JsonUtil.toJson(jsonParamMap));
	}

	@Method(desc = "根据对象采集id与agent id查询对象采集对应信息的合集(采集文件配置）",
			logicStep = "1.根据对象采集id查询对象采集对应信息表返回到前端")
	@Param(name = "odc_id", desc = "对象采集id", range = "不能为空")
	@Param(name = "agent_id", desc = "agent id", range = "新增agent时生成")
	@Return(desc = "对象采集对应信息的合集", range = "可能为空")
	public Result searchObjectCollectTask(long odc_id, long agent_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.根据对象采集id查询对象采集对应信息表返回到前端
		return Dbo.queryResult("SELECT * FROM " + Object_collect_task.TableName
				+ " WHERE odc_id = ? and agent_id = ?", odc_id, agent_id);
	}

	@Method(desc = "对象采集任务编号删除对象采集对应信息表",
			logicStep = "1.根据对象采集任务编号查询对象采集存储设置表是否有数据，有数据不能删除" +
					"2.根据对象采集任务编号查询对象对应的对象采集结构信息表是否有数据，有数据不能删除" +
					"3.根据对象采集任务编号删除对象采集对应信息表")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "不可为空")
	public void deleteObjectCollectTask(long ocs_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.根据对象采集任务编号查询对象采集存储设置表是否有数据，有数据不能删除
		if (Dbo.queryNumber(" SELECT count(1) count FROM " + Object_storage.TableName
				+ " WHERE ocs_id = ?", ocs_id).orElseThrow(()
				-> new BusinessException("查询得到的数据必须有且只有一条")) > 0) {
			// 此对象对应的对象采集存储设置下有数据，不能删除
			throw new BusinessException("此对象对应的对象采集存储设置下有数据，不能删除");
		}
		//2.根据对象采集任务编号查询对象对应的对象采集结构信息表是否有数据，有数据不能删除
		if (Dbo.queryNumber(" SELECT count(1) count FROM " + Object_collect_struct.TableName
				+ " WHERE ocs_id = ?", ocs_id).orElseThrow(()
				-> new BusinessException("查询得到的数据必须有且只有一条")) > 0) {
			// 此对象对应的对象采集结构信息表下有数据，不能删除
			throw new BusinessException("此对象对应的对象采集结构信息表下有数据，不能删除");
		}
		//3.根据对象采集任务编号删除对象采集对应信息表
		DboExecute.deletesOrThrow("删除object_collect_task表信息异常，ocs_id=" + ocs_id,
				"DELETE FROM " + Object_collect_task.TableName
						+ " WHERE ocs_id = ?", ocs_id);
	}

	@Method(desc = "保存采集文件设置信息（需先调保存对象文件配置信息时检查字段方法成功后在调此方法）",
			logicStep = "1.获取json数组转成对象采集对应信息表的集合" +
					"2.获取对象采集对应信息表list进行遍历" +
					"3.根据对象采集对应信息表id判断是新增还是编辑" +
					"4.根据en_name查询对象采集对应信息表的英文名称是否重复")
	@Param(name = "object_collect_task_array", desc = "多条对象采集对应信息表的JSONArray格式的字符串，以en_name，" +
			"zh_name，collect_data_type，database_code，updatetype,ocs_id为key，对应值为value", range = "不能为空")
	@Param(name = "agent_id", desc = "agent ID", range = "新增agent时生成")
	@Param(name = "odc_id", desc = "对象采集设置表主键ID", range = "新增对象采集设置时生成")
	public void saveObjectCollectTask(long agent_id, long odc_id, String object_collect_task_array) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.获取json数组转成对象采集对应信息表的集合
		Type type = new TypeReference<List<Object_collect_task>>() {
		}.getType();
		// 2.解析json为对象采集对应信息
		List<Object_collect_task> objectCollectTaskList = JsonUtil.toObject(object_collect_task_array, type);
		//2.获取对象采集对应信息表list进行遍历
		for (Object_collect_task object_collect_task : objectCollectTaskList) {
			//TODO 使用公共方法校验数据的正确性
			//XXX 这里新增和编辑是放在一起的，因为这里面是保存一个列表的数据，可能为一条或者多条。
			//XXX 这一条或者多条数据会有新增也会有编辑，所以对应在一个方法里面了
			//3.根据对象采集对应信息表id判断是新增还是编辑
			if (object_collect_task.getOcs_id() == null) {
				//新增
				//4.根据en_name查询对象采集对应信息表的英文名称是否重复
				if (Dbo.queryNumber("SELECT count(1) count FROM " + Object_collect_task.TableName
						+ " WHERE en_name = ?", object_collect_task.getEn_name()).orElseThrow(()
						-> new BusinessException("sql查询错误")) > 0) {
					throw new BusinessException("对象采集对应信息表的英文名称重复");
				}
				object_collect_task.setOcs_id(PrimayKeyGener.getNextId());
				object_collect_task.setAgent_id(agent_id);
				object_collect_task.setOdc_id(odc_id);
				object_collect_task.add(Dbo.db());
			} else {
				//更新
				//4.根据en_name查询对象采集对应信息表的英文名称是否重复
				if (Dbo.queryNumber("SELECT count(1) count FROM "
								+ Object_collect_task.TableName + " WHERE en_name = ? AND ocs_id != ?",
						object_collect_task.getEn_name(), object_collect_task.getOcs_id())
						.orElseThrow(() -> new BusinessException("sql查询错误")) > 0) {
					throw new BusinessException("对象采集对应信息表的英文名称重复");
				}
				object_collect_task.update(Dbo.db());
			}
		}
	}

	@Method(desc = "根据ocs_id查询hbase配置信息",
			logicStep = "1.查询对应对象采集结构信息表，返回前端")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "不可为空")
	@Return(desc = "对象采集任务对应对象采集结构信息的集合", range = "可能为空")
	public Result searchHBaseConfInfo(long ocs_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.查询对应对象采集结构信息表，返回前端
		return Dbo.queryResult("SELECT * FROM " + Object_collect_struct.TableName
				+ " where ocs_id=? order by col_seq", ocs_id);
	}

	@Method(desc = "根据结构信息id删除对象采集结构信息表",
			logicStep = "1.获取结构信息id，删除对象采集结构信息表")
	@Param(name = "struct_id", desc = "结构信息id", range = "不可为空")
	public void deleteObjectCollectStruct(long struct_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.获取结构信息id，删除对象采集结构信息表
		DboExecute.deletesOrThrow("删除object_collect_struct表信息异常，struct_id = " + struct_id,
				"DELETE FROM " + Object_collect_struct.TableName
						+ " WHERE struct_id = ?", struct_id);
	}

	@Method(desc = "保存对象采集对应结构信息表(hbase配置信息）",
			logicStep = "1.获取json数组转成对象采集结构信息表的集合" +
					"2.获取对象采集结构信息list进行遍历" +
					"3.同一个对象采集任务下，对象采集结构信息表的coll_name不能重复" +
					"4.如果选了字段作为rowkey，那么必选进入hbase" +
					"5.更新对象采集结构信息")
	@Param(name = "object_collect_struct_array", desc = "多条对象采集对应结构信息表的JSONArray格式的字符串，" +
			"以struct_id，col_seq(序号）,column_name,is_rowkey为key,对应值为value", range = "不可为空")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "不可为空")
	public void saveHBaseConfInfo(long ocs_id, String object_collect_struct_array) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.获取json数组转成对象采集结构信息表的集合
		Type type = new TypeReference<List<Object_collect_struct>>() {
		}.getType();
		List<Object_collect_struct> objectCollectStructList = JsonUtil.toObject(object_collect_struct_array,
				type);
		//2.获取对象采集结构信息list进行遍历
		for (Object_collect_struct object_collect_struct : objectCollectStructList) {
			//TODO 应该使用一个公共的校验类进行校验
			//XXX 这里新增和编辑是放在一起的，因为这里面是保存一个列表的数据，可能为一条或者多条。
			//XXX 这一条或者多条数据会有新增也会有编辑，所以对应在一个方法里面了
			//3.同一个对象采集任务下，对象采集结构信息表的coll_name不能重复
			long count = Dbo.queryNumber("SELECT count(1) count FROM "
							+ Object_collect_struct.TableName + " WHERE column_name = ? AND ocs_id = ? " +
							" AND struct_id != ?", object_collect_struct.getColumn_name()
					, ocs_id, object_collect_struct.getStruct_id())
					.orElseThrow(() -> new BusinessException("有且只有一个返回值"));
			if (count > 0) {
				throw new BusinessException("同一个对象采集任务下，对象采集结构信息表的coll_name不能重复");
			}
			IsFlag.ofEnumByCode(object_collect_struct.getIs_rowkey());
			// 4.如果选了字段作为rowkey，那么必选进入hbase
			if (IsFlag.Shi == IsFlag.ofEnumByCode(object_collect_struct.getIs_rowkey())) {
				object_collect_struct.setIs_hbase(IsFlag.Shi.getCode());
			}
			// 5.更新对象采集结构信息
			object_collect_struct.update(Dbo.db());
		}
	}

	@Method(desc = "保存solr配置信息", logicStep = "1.获取json数组转成对象采集结构信息表的集合" +
			"2.获取对象采集结构信息list进行遍历" +
			"3.同一个对象采集任务下，对象采集结构信息表的coll_name不能重复" +
			"4.更新对象采集结构信息")
	@Param(name = "solrConfInfo", desc = "多条solr配置的JSONArray格式的字符串，以struct_id,is_solr," +
			"column_name为key,对应值为value", range = "无限制")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "不可为空")
	public void saveSolrConfInfo(long ocs_id, String solrConfInfo) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.获取json数组转成对象采集结构信息表的集合
		Type type = new TypeReference<List<Object_collect_struct>>() {
		}.getType();
		List<Object_collect_struct> objectCollectStructList = JsonUtil.toObject(solrConfInfo, type);
		//2.获取对象采集结构信息list进行遍历
		for (Object_collect_struct object_collect_struct : objectCollectStructList) {
			//TODO 应该使用一个公共的校验类进行校验
			//XXX 这里新增和编辑是放在一起的，因为这里面是保存一个列表的数据，可能为一条或者多条。
			//XXX 这一条或者多条数据会有新增也会有编辑，所以对应在一个方法里面了
			//3.同一个对象采集任务下，对象采集结构信息表的coll_name不能重复
			long count = Dbo.queryNumber("SELECT count(1) count FROM "
							+ Object_collect_struct.TableName + " WHERE column_name = ? AND ocs_id = ? " +
							" AND struct_id != ?", object_collect_struct.getColumn_name()
					, ocs_id, object_collect_struct.getStruct_id())
					.orElseThrow(() -> new BusinessException("有且只有一个返回值"));
			if (count > 0) {
				throw new BusinessException("同一个对象采集任务下，对象采集结构信息表的coll_name不能重复");
			}
			// 4.更新对象采集结构信息
			IsFlag.ofEnumByCode(object_collect_struct.getIs_solr());
			object_collect_struct.update(Dbo.db());
		}
	}

	@Method(desc = "根据对象采集id查询对象采集任务存储设置(存储设置展示）",
			logicStep = "1.根据对象采集id，查询对象采集任务及每个任务对象的存储设置")
	@Param(name = "odc_id", desc = "对象采集id", range = "不可为空")
	@Return(desc = "采集任务及每个任务的存储设置", range = "不会为空")
	public Result searchObjectStorage(long odc_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.根据对象采集id，查询对象采集任务及每个任务对象的存储设置
		Result result = Dbo.queryResult("SELECT t1.*,t2.is_hbase,t2.is_hdfs,t2.obj_stid,t2.is_solr FROM "
				+ Object_collect_task.TableName + " t1 left join " + Object_storage.TableName +
				" t2 on t1.ocs_id = t2.ocs_id WHERE odc_id = ?", odc_id);
		if (result.isEmpty()) {
			throw new BusinessException("odc_id =" + odc_id + "查询不到对象采集任务表的数据");
		}
		return result;
	}

	@Method(desc = "查询solr配置信息", logicStep = "1.数据可访问权限处理方式：该表没有对应的用户访问权限限制" +
			"2.查询solr配置信息")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "新增对象采集任务时生成")
	@Return(desc = "返回solr配置信息", range = "无限制")
	public List<Map<String, Object>> searchSolrConfInfo(long ocs_id) {
		// 1.数据可访问权限处理方式：该表没有对应的用户访问权限限制
		// 2.查询solr配置信息
		return Dbo.queryList("select * from " + Object_collect_struct.TableName +
				" where ocs_id = ? and is_hbase = ? order by col_seq", ocs_id, IsFlag.Shi.getCode());
	}

	@Method(desc = "保存对象采集存储设置信息(存储设置)，检查保存对象存储的字段方法成功后调用",
			logicStep = "1.获取json数组转成对象采集结构信息表的集合" +
					"2.根据对象采集存储设置表id是否为空判断是编辑还是新增" +
					"3.保存对象采集存储设置表" +
					"4.更新对象采集设置表的字段是否完成设置并发送成功为是")
	@Param(name = "object_storage_array", desc = "多条对象采集存储设置表的JSONArray格式的字符串，" +
			"obj_stid(可以为空），is_hbase，is_hdfs，is_solr，ocs_id为key，对应值为value", range = "不能为空")
	@Param(name = "odc_id", desc = "对象采集id", range = "新增对象采集时生成")
	public void saveObjectStorage(String object_storage_array, long odc_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.获取json数组转成对象采集结构信息表的集合
		Type type = new TypeReference<List<Object_storage>>() {
		}.getType();
		List<Object_storage> objectStorageList = JsonUtil.toObject(object_storage_array, type);
		for (Object_storage object_storage : objectStorageList) {
			//2.根据对象采集存储设置表id是否为空判断是编辑还是新增
			//TODO 应该使用一个公共的校验类进行校验
			//XXX 这里新增和编辑是放在一起的，因为这里面是保存一个列表的数据，可能为一条或者多条。
			//XXX 这一条或者多条数据会有新增也会有编辑，所以对应在一个方法里面了
			if (object_storage.getObj_stid() == null) {
				//新增
				object_storage.setObj_stid(PrimayKeyGener.getNextId());
				//3.保存对象采集存储设置表
				object_storage.add(Dbo.db());
			} else {
				object_storage.update(Dbo.db());
			}
		}
		// 4.更新对象采集设置表的字段是否完成设置并发送成功为是
		DboExecute.updatesOrThrow("更新表" + Object_collect.TableName + "失败"
				, "UPDATE " + Object_collect.TableName + " SET is_sendok = ?"
						+ " WHERE odc_id = ? ", IsFlag.Shi.getCode(), odc_id);
		// 5.重写数据字典
		rewriteDataDictionary(odc_id);
	}

	@Method(desc = "检查保存对象存储的字段(存储设置),成功后调用保存对象采集存储设置信息方法",
			logicStep = "1.数据可访问权限处理方式：该表没有对应的用户访问权限限制" +
					"2.解析json对象为数据存储实体集合" +
					"3.遍历数据存储实体检查对象存储字段" +
					"4.判断进solr的表是否进了HBase" +
					"5.判断当前数据存储表是否进HBase" +
					"6.判断当前数据存储表是否选择字段进HBase" +
					"7.判断当前数据存储表是否选择字段为rowkey" +
					"8.判断当前存储表是否进solr" +
					"9.判断当前数据存储表字段是否选择字段进solr")
	@Param(name = "object_storage_array", desc = "多条对象采集存储设置表的JSONArray格式的字符串，" +
			"以obj_stid(可以为空），is_hbase，is_hdfs，is_solr，ocs_id为key，对应值为value", range = "不能为空")
	public void checkFieldsForSaveObjectStorage(String object_storage_array) {
		// 1.数据可访问权限处理方式：该表没有对应的用户访问权限限制
		Type type = new TypeReference<List<Object_storage>>() {
		}.getType();
		// 2.解析json对象为数据存储实体集合
		List<Object_storage> objectStorageList = JsonUtil.toObject(object_storage_array, type);
		// 3.遍历数据存储实体检查对象存储字段
		for (int i = 0; i < objectStorageList.size(); i++) {
			Object_storage object_storage = objectStorageList.get(i);
			List<Object> enNameList = Dbo.queryOneColumnList("select en_name from "
					+ Object_collect_task.TableName + " where ocs_id = ?", object_storage.getOcs_id());
			// 4.判断进solr的表是否进了HBase
			if (IsFlag.Fou == IsFlag.ofEnumByCode(object_storage.getIs_hbase()) &&
					IsFlag.Shi == IsFlag.ofEnumByCode(object_storage.getIs_solr())) {
				throw new BusinessException("第" + (i + 1) + "行表" + enNameList.get(0) + "入solr表未选择进入hbase" +
						"，请检查");
			}
			// 5.判断当前数据存储表是否进HBase
			if (IsFlag.Shi == IsFlag.ofEnumByCode(object_storage.getIs_hbase())) {
				// 6.判断当前数据存储表是否选择字段进HBase
				if (Dbo.queryNumber("select count(*) from " + Object_collect_struct.TableName +
						" where ocs_id=? and is_hbase=?", object_storage.getOcs_id(), IsFlag.Shi.getCode())
						.orElseThrow(() -> new BusinessException("sql查询错误")) == 0) {
					throw new BusinessException("第" + (i + 1) + "行表" + enNameList.get(0) + "未选择字段进入hbase，请检查");
				}
				// 7.判断当前数据存储表是否选择字段为rowkey
				if (Dbo.queryNumber("select count(*) from " + Object_collect_struct.TableName +
								" where ocs_id=? and is_hbase=? and is_rowkey=?", object_storage.getOcs_id(),
						IsFlag.Shi.getCode(), IsFlag.Shi.getCode())
						.orElseThrow(() -> new BusinessException("sql查询错误")) == 0) {
					throw new BusinessException("第" + (i + 1) + "行表" + enNameList.get(0) + "未选择字段为rowkey，请检查");
				}
			}
			// 8.判断当前存储表是否进solr
			if (IsFlag.Shi == IsFlag.ofEnumByCode(object_storage.getIs_solr())) {
				// 9.判断当前数据存储表字段是否选择字段进solr
				if (Dbo.queryNumber("select count(*) from " + Object_collect_struct.TableName +
						" where ocs_id=? and is_solr=?", object_storage.getOcs_id(), IsFlag.Shi.getCode())
						.orElseThrow(() -> new BusinessException("sql查询错误")) == 0) {
					throw new BusinessException("第" + (i + 1) + "行表" + enNameList.get(0) + "未选择字段进入solr，请检查");
				}
			}
		}
	}
}
