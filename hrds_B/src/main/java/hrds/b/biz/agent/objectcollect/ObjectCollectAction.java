package hrds.b.biz.agent.objectcollect;

import com.alibaba.fastjson.*;
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
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.AgentActionUtil;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.PackUtil;
import hrds.commons.utils.key.PrimayKeyGener;

import java.lang.reflect.Type;
import java.util.*;

@DocClass(desc = "对象采集接口类，处理对象采集的增删改查", author = "zxz", createdate = "2019/9/16 15:02")
public class ObjectCollectAction extends BaseAction {

	@Method(desc = "获取半结构化采集配置页面初始化的值，当odc_id不为空时，则同时返回object_collect表的值",
			logicStep = "1.根据前端传过来的agent_id获取调用Agent服务的接口" +
					"2.根据url远程调用Agent的后端代码获取采集服务器上的日期、" +
					"时间、操作系统类型和主机名等基本信息" +
					"3.对象采集id不为空则获取对象采集设置表信息")
	@Param(name = "object_collect", desc = "对象采集设置表对象，接收页面传过来的参数Agent_id和odc_id(对象采集id)",
			range = "agent_id不可为空，odc_id可为空", isBean = true)
	@Return(desc = "Agent所在服务器的基本信息、对象采集设置表信息", range = "不会为空")
	public Map<String, Object> searchObjectCollect(Object_collect object_collect) {
		if (object_collect.getAgent_id() == null) {
			throw new BusinessException("object_collect对象agent_id不能为空");
		}
		//数据可访问权限处理方式：传入用户需要有Agent信息表对应数据的访问权限
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
		map.put("localdate", DateUtil.getSysDate());
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

	@Method(desc = "保存半结构化文件采集页面信息到对象采集设置表对象，同时返回对象采集id",
			logicStep = "1.数据可访问权限处理方式：该表没有对应的用户访问权限限制" +
					"2.根据obj_collect_name查询半结构化任务名称是否重复" +
					"3.保存object_collect表" +
					"4.保存object_collect表" +
					"5.获取agent解析数据字典返回json格式数据" +
					"6.遍历json对象" +
					"7.object_collect_task表信息入库" +
					"8.获取字段信息" +
					"9.保存对象采集结构信息" +
					"10.保存对象采集数据处理类型对应表信息" +
					"11.保存数据存储表信息入库" +
					"12.返回对象采集ID")
	@Param(name = "object_collect", desc = "对象采集设置表对象，对象中不能为空的字段必须有值",
			range = "不可为空", isBean = true)
	@Return(desc = "对象采集设置表id，新建的id后台生成的所以要返回到前端", range = "不会为空")
	public long addObjectCollect(Object_collect object_collect) {
		// 1.数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//TODO 应该使用一个公共的校验类进行校验

		// 2.根据obj_collect_name查询半结构化任务名称是否重复
		long count = Dbo.queryNumber("SELECT count(1) count FROM " + Object_collect.TableName
				+ " WHERE obj_collect_name = ?", object_collect.getObj_collect_name())
				.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if (count > 0) {
			throw new BusinessException("半结构化采集任务名称重复");
		}
		object_collect.setOdc_id(PrimayKeyGener.getNextId());
		// 3.之前对象采集存在行采集与对象采集两种，目前仅支持行采集 所以默认给
		if (StringUtil.isNotBlank(object_collect.getObject_collect_type())) {
			object_collect.setObject_collect_type(ObjectCollectType.HangCaiJi.getCode());
		}
		// 4.保存object_collect表
		object_collect.add(Dbo.db());
		String jsonParamMap = getJsonParamForAgent(object_collect);
		// 5.获取agent解析数据字典返回json格式数据
		Map<String, String> jsonMsg = getJsonDataForAgent(jsonParamMap, object_collect.getAgent_id());
		Type type = new TypeReference<Map<String, String>>() {
		}.getType();
		Map<String, String> objectCollectMap = JsonUtil.toObject(jsonMsg.get("msg"), type);
		Type type2 = new TypeReference<List<Map<String, String>>>() {
		}.getType();
		List<Map<String, String>> tableNameList = JsonUtil.toObject(objectCollectMap.get("tablename"), type2);
		// 6.遍历json对象
		for (Map<String, String> tableNameMap : tableNameList) {
			String tableName = tableNameMap.get("tableName");
			String zh_name = tableNameMap.get("description");
			String updateType = tableNameMap.get("updatetype");
			String firstLine = tableNameMap.get("everyline");
			Object_collect_task objectCollectTask = new Object_collect_task();
			String ocs_id = PrimayKeyGener.getNextId();
			objectCollectTask.setOcs_id(ocs_id);
			objectCollectTask.setUpdatetype(updateType);
			objectCollectTask.setAgent_id(object_collect.getAgent_id());
			objectCollectTask.setEn_name(tableName != null ? tableName : "");
			objectCollectTask.setZh_name(zh_name != null ? zh_name : "");
			objectCollectTask.setCollect_data_type(CollectDataType.JSON.getCode());
			objectCollectTask.setFirstline(firstLine != null ? firstLine : "");
			objectCollectTask.setOdc_id(object_collect.getOdc_id());
			if (StringUtil.isBlank(tableNameMap.get("database_code"))) {
				objectCollectTask.setDatabase_code(DataBaseCode.UTF_8.getCode());
			}
			if (StringUtil.isBlank(tableNameMap.get("update_type"))) {
				objectCollectTask.setUpdatetype(UpdateType.DirectUpdate.getCode());
			}
			// 7.object_collect_task表信息入库
			objectCollectTask.add(Dbo.db());

			// 8.获取字段信息
			List<Map<String, String>> columnList = JsonUtil.toObject(tableNameMap.get("column"), type2);
			// 如果没有数据字典，第一次新增则不会加载object_collect_struct，第二次编辑也不会修改库中信息
			boolean isSolr = false;
			if (IsFlag.Shi.getCode().equals(object_collect.getIs_dictionary())) {
				if (!columnList.isEmpty()) {
					for (int i = 0; i < columnList.size(); i++) {
						Map<String, String> columnMap = columnList.get(i);
						if (!isSolr && columnMap.get("is_solr").equals(IsFlag.Shi.getCode())) {
							isSolr = true;
						}
						Object_collect_struct object_collect_struct = new Object_collect_struct();
						// 9.保存对象采集结构信息
						addObjectCollectStruct(objectCollectTask, i, columnMap, object_collect_struct);
					}
				}
				// 如果没有数据字典，第一次新增则不会加载object_handle_type，第二次编辑也不会修改库中信息
				Map<String, String> handleTypeMap = JsonUtil.toObject(tableNameMap.get("handletype"), type);
				Object_handle_type object_handle_type = new Object_handle_type();
				// 10.保存对象采集数据处理类型对应表信息
				if (!handleTypeMap.isEmpty()) {
					// 插入insert对应的值
					object_handle_type.setObject_handle_id(PrimayKeyGener.getNextId());
					object_handle_type.setOcs_id(ocs_id);
					object_handle_type.setHandle_type(OperationType.INSERT.getCode());
					object_handle_type.setHandle_value(handleTypeMap.get("insert"));
					object_handle_type.add(Dbo.db());
					// 插入delete对应的值
					object_handle_type.setObject_handle_id(PrimayKeyGener.getNextId());
					object_handle_type.setHandle_type(OperationType.DELETE.getCode());
					object_handle_type.setHandle_value(handleTypeMap.get("delete"));
					object_handle_type.add(Dbo.db());
					// 插入update对应的值
					object_handle_type.setObject_handle_id(PrimayKeyGener.getNextId());
					object_handle_type.setHandle_type(OperationType.UPDATE.getCode());
					object_handle_type.setHandle_value(handleTypeMap.get("update"));
					object_handle_type.add(Dbo.db());
				} else {
					// 插入操作码表对应的值
					OperationType[] operationTypes = OperationType.values();
					for (OperationType operationType : operationTypes) {
						object_handle_type.setObject_handle_id(PrimayKeyGener.getNextId());
						object_handle_type.setOcs_id(ocs_id);
						object_handle_type.setHandle_type(operationType.getCode());
						object_handle_type.setHandle_value(operationType.getValue());
						object_handle_type.add(Dbo.db());
					}
				}
			}
			// 11.保存数据存储表信息入库
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
			object_storage.add(Dbo.db());
		}
		// 12.返回对象采集ID
		return object_collect.getOdc_id();
	}

	@Method(desc = "获取agent解析数据字典返回json格式数据",
			logicStep = "1.数据可访问权限处理方式：该表没有对应的用户访问权限限制" +
					"2.调用工具类获取本次访问的agentserver端url" +
					"3、给agent发消息，并获取agent响应" +
					"4、如果测试连接不成功，则抛异常给前端，说明连接失败，如果成功，则不做任务处理" +
					"5.解析agent返回的json数据")
	@Param(name = "json", desc = "发送到agent的josn格式参数", range = "不可为空")
	@Param(name = "agent_id", desc = "agent_info表主键", range = "新增agent时生成")
	@Return(desc = "解析agent返回的json数据", range = "无限制")
	private Map<String, String> getJsonDataForAgent(String jsonParamMap, long agent_id) {
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
		if (!actionResult.isSuccess()) {
			throw new BusinessException("连接失败");
		}
		Object data = actionResult.getData().toString();
		// 5.解析agent返回的json数据
		return PackUtil.unpackMsg(data.toString());
	}

	@Method(desc = "采集文件配置", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.查询采集文件配置信息")
	@Param(name = "odc_id", desc = "对象采集id", range = "object_collect表主鍵，新增时生成")
	@Param(name = "agent_id", desc = "agent id", range = "agent_info表主键，新增时生成")
	@Return(desc = "返回对象采集对应信息", range = "无限制")
	public List<Map<String, Object>> collectFileConfig(long odc_id, long agent_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.查询采集文件配置信息
		return Dbo.queryList("SELECT * from " + Object_collect_task.TableName +
				" where agent_id=? and odc_id=? order by ocs_id", agent_id, odc_id);
	}

	@Method(desc = "采集列结构", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.有数据字典，查询对象采集结构信息" +
			"3.没有数据字典查询第一行数据" +
			"4.解析json获取树结构信息并返回" +
			"5.返回解析json获取树结构信息与是否是数据字典")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "新增对象采集任务时生成")
	@Return(desc = "", range = "")
	public Map<String, Object> collectColumnStructure(long ocs_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.有数据字典，查询对象采集结构信息
		Map<String, Object> objColStructList = Dbo.queryOneObject("select * from "
				+ Object_collect_struct.TableName + " where ocs_id=? order by col_seq", ocs_id);
		// 3.没有数据字典查询第一行数据
		List<Object> firstLineList = getFirstLineInfo(ocs_id);
		String isDictionary = IsFlag.Shi.getCode();
		Map<String, Object> structMap = new HashMap<String, Object>();
		if (!firstLineList.isEmpty()) {
			isDictionary = IsFlag.Fou.getCode();
			// 4.解析json获取树结构信息并返回
			JSONArray treeConstruct = jsonFirstLine(firstLineList.get(0).toString(), "");
			structMap.put("treeConstruct", treeConstruct);
		}
		structMap.put("isDictionary", isDictionary);
		structMap.put("taskResult", objColStructList);
		// 5.返回解析json获取树结构信息与是否是数据字典
		return structMap;
	}

	private List<Object> getFirstLineInfo(long ocs_id) {
		return Dbo.queryOneColumnList("select firstline from "
						+ Object_collect.TableName + " t1 left join " + Object_collect_task.TableName +
						" t2 on t1.odc_id = t2.odc_id where t2.ocs_id = ? and t1.is_dictionary = ?",
				ocs_id, IsFlag.Fou.getCode());
	}

	@Method(desc = "获取对象采集树节点信息", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.解析json获取树结构信息并返回" +
			"3.获取树信息失败")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "新增对象采集任务时生成")
	@Param(name = "treeId", desc = "树节点", range = "无限制")
	@Return(desc = "获取对象采集树节点信息", range = "无限制")
	public JSONArray getObjectCollectTreeInfo(long ocs_id, String treeId) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		List<Object> firstLineList = getFirstLineInfo(ocs_id);
		if (!firstLineList.isEmpty() && StringUtil.isNotBlank(treeId)) {
			// 2.解析json获取树结构信息并返回
			return jsonFirstLine(firstLineList.get(0).toString(), treeId);
		} else {
			// 3.获取树信息失败
			throw new BusinessException("当前对象采集对应的第一行数据不存在，树节点为空，treeId="
					+ treeId + ",ocs_id=" + ocs_id);
		}


	}

	@Method(desc = "解析没有数据字典的第一行数据", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制")
	@Param(name = "firstLine", desc = "第一行数据", range = "无限制")
	@Param(name = "treeId", desc = "树节点", range = "无限制")
	@Return(desc = "", range = "")
	private JSONArray jsonFirstLine(String firstLine, String treeId) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		JSONArray array;
		try {
			JSONArray parseArray = JSONArray.parseArray(firstLine);
			Object everyObject = parseArray.getObject(0, Object.class);
			while (everyObject instanceof JSONArray) {
				JSONArray jsonarray = (JSONArray) everyObject;
				everyObject = jsonarray.getObject(0, Object.class);
			}
			if (everyObject instanceof JSONObject) {
				JSONObject jsonobject = (JSONObject) everyObject;
				if (StringUtil.isNotBlank(treeId)) {
					for (String key : treeId.split(",")) {
						jsonobject = makeJsonFileToJsonObj(jsonobject, key);
					}
				}
				array = getTree(jsonobject, treeId);
			} else {
				throw new BusinessException("解析json结构错误 jsonarray下面不存在jsonobject");
			}
		} catch (JSONException e) {
			try {
				JSONObject jsonobject = JSONObject.parseObject(firstLine);
				if (StringUtil.isNotBlank(treeId)) {
					for (String key : treeId.split(",")) {
						jsonobject = makeJsonFileToJsonObj(jsonobject, key);
					}
				}
				array = getTree(jsonobject, treeId);
			} catch (JSONException e2) {
				throw new BusinessException("既不是jsonarray，也不是jsonobject");
			}
		}
		return array;
	}

	private JSONArray getTree(JSONObject jsonobject, String keys) {
		if (keys == null || keys == "") {
			keys = "";
		} else {
			keys += ",";
		}
		JSONArray array = new JSONArray();
		Set<Map.Entry<String, Object>> entrySet = jsonobject.entrySet();
		int rowcount = 0;
		for (Map.Entry<String, Object> everyentrySet : entrySet) {
			JSONObject resultobject = new JSONObject();
			String key = everyentrySet.getKey();
			Object object = jsonobject.get(key);
			boolean isParent;
			if (object instanceof JSONObject || object instanceof JSONArray) {
				isParent = true;
			} else {
				isParent = false;
			}
			resultobject.put("location", keys + key);
			resultobject.put("description", key);
			resultobject.put("id", key);
			resultobject.put("isParent", isParent);
			resultobject.put("name", key);
			resultobject.put("pId", "~" + rowcount);
			resultobject.put("rootName", "~" + rowcount);
			array.add(resultobject);
			rowcount++;
		}
		return array;
	}

	@Method(desc = "", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制")
	@Param(name = "", desc = "", range = "")
	@Return(desc = "", range = "")
	private JSONObject makeJsonFileToJsonObj(JSONObject JsonObject, String nextKey) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		Object object = JsonObject.get(nextKey);
		JSONObject jsonobject = new JSONObject();
		if (object instanceof JSONArray) {
			while (object instanceof JSONArray) {
				JSONArray jsonarray = (JSONArray) object;
				object = jsonarray.getObject(0, Object.class);
			}
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
		return jsonobject;
	}

	@Method(desc = "获取当前表的码表信息(操作码表)", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.查询当前表的码表信息")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "新增对象采集任务时生成")
	@Return(desc = "返回当前表的码表信息", range = "无限制")
	public List<Object> searchOperateCodeTableInfo(long ocs_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.查询当前表的码表信息
		return Dbo.queryOneColumnList("select handle_type from "
				+ Object_handle_type.TableName + " where ocs_id=?", ocs_id);
	}

	@Method(desc = "保存表的码表信息", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.解析json为对象采集结构信息" +
			"3.循环保存对象采集数据处理类型对应表信息")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "新增对象采集任务时生成")
	@Param(name = "handleType", desc = "jsonArray格式的码表类型信息", range = "无限制")
	public void saveHandleType(long ocs_id, String handleType) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		Dbo.execute("delete from " + Object_handle_type.TableName + " where ocs_id = ?", ocs_id);
		Type type = new TypeReference<List<Object_collect_struct>>() {
		}.getType();
		// 2.解析json为对象采集结构信息
		List<Object_handle_type> handleTypeList = JsonUtil.toObject(handleType, type);
		// 3.循环保存对象采集数据处理类型对应表信息
		for (Object_handle_type object_handle_type : handleTypeList) {
			object_handle_type.setObject_handle_id(PrimayKeyGener.getNextId());
			object_handle_type.add(Dbo.db());
		}
	}

	@Method(desc = "保存对象采集结构信息（采集列结构）", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.解析json为对象采集结构信息" +
			"3.循环保存对象采集结构信息入库，获取结构信息id" +
			"4.删除非新保存结构信息")
	@Param(name = "collectStruct", desc = "jsonArray格式的字符串", range = "无限制", nullable = false)
	public void saveCollectColumnStruct(String collectStruct) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		Type type = new TypeReference<List<Object_collect_struct>>() {
		}.getType();
		// 2.解析json为对象采集结构信息
		List<Object_collect_struct> collectStructList = JsonUtil.toObject(collectStruct, type);
		List<String> structIdList = new ArrayList<String>();
		String struct_id;
		// 3.循环保存对象采集结构信息入库，获取结构信息id
		for (Object_collect_struct object_collect_struct : collectStructList) {
			if (null == object_collect_struct.getStruct_id()) {
				object_collect_struct.setOcs_id(object_collect_struct.getOcs_id());
				struct_id = PrimayKeyGener.getNextId();
				object_collect_struct.setStruct_id(struct_id);
				object_collect_struct.add(Dbo.db());
			} else {
				struct_id = String.valueOf(object_collect_struct.getStruct_id());
				object_collect_struct.update(Dbo.db());
			}
			structIdList.add(struct_id);
		}
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		// 4.删除非新保存结构信息
		asmSql.addSql("delete from " + Object_collect_struct.TableName + " where ocs_id=? and struct_id not in (");
		asmSql.addParam(collectStructList.get(0).getOcs_id());
		for (int i = 0; i < structIdList.size(); i++) {
			asmSql.addSql("?");
			asmSql.addParam(Long.valueOf(structIdList.get(i)));
			if (i != structIdList.size() - 1) {
				asmSql.addSql(",");
			}
		}
		asmSql.addSql(")");
		Dbo.execute(asmSql.sql(), asmSql.params());
	}

	@Method(desc = "保存对象文件配置信息时检查字段", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.解析json对象为对象采集对应信息" +
			"3.循环检查英文名是否为空" +
			"4.循环检查中文名是否为空" +
			"5.循环检查采集列结构是否为空" +
			"6.循环检查操作码表是否为空" +
			"7.循环检查操作字段是否为1个")
	@Param(name = "objColTask", desc = "jsonArray格式的对象采集对应信息", range = "无限制")
	public void checkFieldsToSaveObjectFileConf(String objColTask) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		Type type = new TypeReference<List<Object_collect_task>>() {
		}.getType();
		// 2.解析json对象为对象采集对应信息
		List<Object_collect_task> objectCollectTaskList = JsonUtil.toObject(objColTask, type);
		Map<String, Object> objectMap = new HashMap<String, Object>();
		int count = 1;
		for (Object_collect_task objectCollectTask : objectCollectTaskList) {
			// 3.循环检查英文名是否为空
			if (StringUtil.isBlank(objectCollectTask.getEn_name())) {
				throw new BusinessException("第" + count + "行表英文名为空，请检查");
			}
			// 4.循环检查中文名是否为空
			if (StringUtil.isBlank(objectCollectTask.getZh_name())) {
				throw new BusinessException("第" + count + "行表" + objectCollectTask.getEn_name() +
						"中文名为空，请检查");
			}
			// 5.循环检查采集列结构是否为空
			List<Map<String, Object>> objColStructList = Dbo.queryList("select * from " +
					Object_collect_struct.TableName + " where ocs_id=?", objectCollectTask.getOcs_id());
			if (objColStructList.isEmpty()) {
				throw new BusinessException("第" + count + "行表" + objectCollectTask.getEn_name() +
						"采集列结构为空，请检查");
			}
			// 6.循环检查操作码表是否为空
			List<Map<String, Object>> objHandleTypeList = Dbo.queryList("select * from " +
					Object_handle_type.TableName + " where ocs_id=?", objectCollectTask.getOcs_id());
			if (objHandleTypeList.isEmpty()) {
				throw new BusinessException("第" + count + "行表" + objectCollectTask.getEn_name() +
						"操作码表为空，请检查");
			}
			// 7.循环检查操作字段是否为1个
			List<Map<String, Object>> objColStructList2 = Dbo.queryList("select * from "
							+ Object_collect_struct.TableName + " where ocs_id=? and is_operate=?",
					objectCollectTask.getOcs_id(), IsFlag.Shi.getCode());
			if (objColStructList2.size() != 1) {
				throw new BusinessException("第" + count + "行表" + objectCollectTask.getEn_name() +
						"操作字段不为1个，请检查");
			}
			count++;
		}
	}

	@Method(desc = "保存对象任务采集的文件信息并重写数据字典",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.解析json为对象采集对应信息" +
					"3.保存对象任务采集的文件信息" +
					"4.重写数据字典")
	@Param(name = "agent_id", desc = "agent id", range = "新增agent时生成")
	@Param(name = "odc_id", desc = "对象采集id", range = "新增对象采集时生成")
	@Param(name = "source_id", desc = "数据源ID", range = "新增数据源时生成")
	@Param(name = "objColTask", desc = "jsonArray格式的对象采集对应信息", range = "无限制")
	public void saveObjectCollectTask(long agent_id, long odc_id, long source_id, String objColTask) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		Type type = new TypeReference<List<Object_collect_task>>() {
		}.getType();
		// 2.解析json为对象采集对应信息
		List<Object_collect_task> collectList = JsonUtil.toObject(objColTask, type);
		// 3.保存对象任务采集的文件信息
		saveObjectCollectFileInfo(collectList, agent_id, odc_id);
		// 4.重写数据字典
		rewriteDataDictionary(odc_id, agent_id);
	}

	@Method(desc = "保存对象任务采集的文件信息", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.遍历新增或更新对象采集文件信息")
	@Param(name = "collectList", desc = "对象采集对应信息的集合", range = "无限制")
	@Param(name = "agent_id", desc = "agent id", range = "新增agent时生成")
	@Param(name = "odc_id", desc = "对象采集id", range = "新增对象采集时生成")
	private void saveObjectCollectFileInfo(List<Object_collect_task> collectList, long agent_id, long odc_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.遍历新增或更新对象采集文件信息
		for (Object_collect_task collect_task : collectList) {
			if (null == collect_task.getOcs_id()) {
				collect_task.setOcs_id(PrimayKeyGener.getNextId());
				collect_task.setAgent_id(agent_id);
				collect_task.setOdc_id(odc_id);
				collect_task.add(Dbo.db());
			} else {
				collect_task.setOcs_id(collect_task.getOcs_id());
				collect_task.update(Dbo.db());
			}
		}

	}

	@Method(desc = "重写数据字典", logicStep = "")
	@Param(name = "odc_id", desc = "对象采集id", range = "新增对象采集时生成")
	@Param(name = "agent_id", desc = "agent id", range = "新增agent时生成")
	private void rewriteDataDictionary(long odc_id, long agent_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		Map<String, Object> dictionaryMap = new HashMap<String, Object>();
		List<Object> dictionaryList = new ArrayList<Object>();
		List<Object> isDictionaryList = Dbo.queryOneColumnList("select is_dictionary from "
				+ Object_collect.TableName + " where odc_id=?", odc_id);
		if (!isDictionaryList.isEmpty()) {
			if (IsFlag.Fou == IsFlag.ofEnumByCode(isDictionaryList.get(0).toString())) {
				throw new BusinessException("已经存在数据字典，不重写数据字典");
			} else {
				List<Object> agentIdList = Dbo.queryOneColumnList("select agent_id from "
						+ Object_collect.TableName + " where odc_id = ?", odc_id);
				Map<String, Object> agentInfo = Dbo.queryOneObject("select * from "
						+ Agent_info.TableName + " where agent_id=?", agentIdList.get(0));
				List<Object> filePathList = Dbo.queryOneColumnList("select file_path from "
						+ Object_collect.TableName + " where odc_id=?", odc_id);
				dictionaryMap.put("file_path", filePathList.get(0));
				List<Map<String, Object>> objCollectTaskList = Dbo.queryList("select * from "
						+ Object_collect_task.TableName + " where odc_id=?", odc_id);
				for (Map<String, Object> objectMap : objCollectTaskList) {
					Map<String, Object> tableMap = new HashMap<String, Object>();
					tableMap.put("table_name", objectMap.get("en_name"));
					tableMap.put("table_cn_name", objectMap.get("zh_name"));
					tableMap.put("updatetype", objectMap.get("updatetype"));
					tableMap.put("operationposition", objectMap.get("operationposition"));
					long ocs_id = Long.parseLong(objectMap.get("ocs_id").toString());
					List<Map<String, Object>> objCollStructList = Dbo.queryList("select * from "
							+ Object_collect_struct.TableName + " where ocs_id=?", ocs_id);
					List<Map<String, Object>> columnList = new ArrayList<Map<String, Object>>();
					for (Map<String, Object> objCollStructMap : objCollStructList) {
						Map<String, Object> columnMap = new HashMap<String, Object>();
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
						handleTypeMap.put(OperationType.valueOf(handle_type).getValue(),
								stringObjectMap.get("handle_value"));
					}
					tableMap.put("handletype", handleTypeMap);
					dictionaryList.add(tableMap);
				}
				dictionaryMap.put("jsonarray", JsonUtil.toJson(dictionaryList));
				// 2.调用工具类获取本次访问的agentserver端url
				String url = AgentActionUtil.getUrl(agent_id, getUserId(),
						AgentActionUtil.WRITEDICTIONARY);
				// 3、给agent发消息，并获取agent响应
				HttpClient.ResponseValue resVal = new HttpClient()
						.addData("dictionaryParam", PackUtil.packMsg(JsonUtil.toJson(dictionaryMap)))
						.post(url);
				// 4、如果测试连接不成功，则抛异常给前端，说明连接失败，如果成功，则不做任务处理
				ActionResult actionResult = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class).
						orElseThrow(() -> new BusinessException("应用管理端与" + url + "服务交互异常"));
				if (!actionResult.isSuccess()) {
					throw new BusinessException("连接失败");
				}
			}
		} else {
			throw new BusinessException("记录半结构化首页采集信息丢失");
		}

	}

	@Method(desc = "更新半结构化文件采集页面信息到对象采集设置表对象，同时返回对象采集id",
			logicStep = "1.数据可访问权限处理方式：该表没有对应的用户访问权限限制" +
					"2.根据obj_collect_name查询半结构化任务名称是否与其他采集任务名称重复" +
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
		// 2.根据obj_collect_name查询半结构化任务名称是否与其他采集任务名称重复
		long count = Dbo.queryNumber("SELECT count(1) count FROM " + Object_collect.TableName
						+ " WHERE obj_collect_name = ? AND odc_id != ?",
				object_collect.getObj_collect_name(), object_collect.getOdc_id())
				.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if (count > 0) {
			throw new BusinessException("半结构化采集任务名称重复");
		}
		// 3.更新object_collect表
		object_collect.update(Dbo.db());
		// 4.获取传递到agent的参数
		String jsonParam = getJsonParamForAgent(object_collect);
		// 5.获取agent解析数据字典返回json格式数据
		Type type = new TypeReference<Map<String, String>>() {
		}.getType();
		Type type2 = new TypeReference<List<Map<String, String>>>() {
		}.getType();
		Map<String, String> jsonMsg = getJsonDataForAgent(jsonParam, object_collect.getAgent_id());
		Map<String, String> objectCollectMap = JsonUtil.toObject(jsonMsg.get("msg"), type);
		List<Map<String, String>> tableNames = JsonUtil.toObject(objectCollectMap.get("tablename"), type2);
		List<String> tableNameList = new ArrayList<String>();
		for (Map<String, String> tableNameMap : tableNames) {
			tableNameList.add(tableNameMap.get("tableName"));
		}
		// 6.如果数据字典减少了表，则需要删除之前在数据库中记录的表
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
		// 7.遍历数据字典信息循环更新半结构化对应信息
		for (Map<String, String> tableNameMap : tableNames) {
			String tableName = tableNameMap.get("tableName");
			String zh_name = tableNameMap.get("description");
			String updateType = tableNameMap.get("updatetype");
			String firstLine = tableNameMap.get("everyline");
			// 8.通过对象采集id查询对象采集对应信息
			Map<String, Object> taskMap = Dbo.queryOneObject("select * from "
							+ Object_collect_task.TableName + " where odc_id = ? and en_name = ?",
					object_collect.getOdc_id(), tableName);
			if (taskMap.isEmpty()) {
				throw new BusinessException("当前半结构化采集对象采集对应表信息为空，odc_id=" + object_collect.getOdc_id());
			}
			// 9.更新保存对象采集对应信息
			Object_collect_task objectCollectTask = new Object_collect_task();
			objectCollectTask.setAgent_id(object_collect.getAgent_id());
			objectCollectTask.setCollect_data_type(CollectDataType.JSON.getCode());
			objectCollectTask.setFirstline(firstLine != null ? firstLine : "");
			objectCollectTask.setOdc_id(object_collect.getOdc_id());
			objectCollectTask.setOcs_id(taskMap.get("ocs_id").toString());
			if (IsFlag.Fou == (IsFlag.ofEnumByCode(object_collect.getIs_dictionary()))) {
				if (!taskMap.isEmpty()) {
					objectCollectTask.setOcs_id(taskMap.get("ocs_id").toString());
					tableName = taskMap.get("en_name").toString();
					zh_name = taskMap.get("zh_name").toString();
					updateType = taskMap.get("updatetype").toString();
					objectCollectTask.setDatabase_code(taskMap.get("database_code").toString());
				}
			}
			objectCollectTask.setZh_name(zh_name != null ? zh_name : "");
			objectCollectTask.setUpdatetype(updateType);
			objectCollectTask.setEn_name(tableName != null ? tableName : "");
			objectCollectTask.update(Dbo.db());
			// 10.获取字段信息
			List<Map<String, String>> columns = JsonUtil.toObject(tableNameMap.get("column"), type2);
			// 如果没有数据字典，第一次新增则不会加载object_collect_struct，第二次编辑也不会修改库中信息
			boolean isSolr = false;
			if (IsFlag.Shi == IsFlag.ofEnumByCode(object_collect.getIs_dictionary())) {
				if (!columns.isEmpty()) {
					Dbo.execute("delete from " + Object_collect_struct.TableName + " where ocs_id = ?",
							objectCollectTask.getOcs_id());
					for (int j = 0; j < columns.size(); j++) {
						Map<String, String> columnMap = columns.get(j);
						if (!isSolr && IsFlag.ofEnumByCode(columnMap.get("is_solr")) == IsFlag.Shi) {
							isSolr = true;
						}
						Object_collect_struct object_collect_struct = new Object_collect_struct();
						// 11.保存对象采集结构信息
						addObjectCollectStruct(objectCollectTask, j, columnMap, object_collect_struct);
					}
				}
				// 如果没有数据字典，第一次新增则不会加载object_handle_type，第二次编辑也不会修改库中信息
				Map<String, String> handleTypMap = JsonUtil.toObject(tableNameMap.get("handletype"), type);
				if (handleTypMap.isEmpty()) {
					throw new BusinessException("有数据字典存在的时候，操作码表类型不能为空");
				} else {
					// 12.保存对象采集数据处理类型对应表
					Dbo.execute("delete from " + Object_handle_type.TableName + " where ocs_id=?",
							objectCollectTask.getOcs_id());
					Object_handle_type object_handle_type = new Object_handle_type();
					// 插入insert对应的值
					object_handle_type.setObject_handle_id(PrimayKeyGener.getNextId());
					object_handle_type.setOcs_id(objectCollectTask.getOcs_id());
					object_handle_type.setHandle_type(OperationType.INSERT.getCode());
					object_handle_type.setHandle_value(handleTypMap.get("insert"));
					object_handle_type.add(Dbo.db());
					// 插入update对应的值
					object_handle_type.setObject_handle_id(PrimayKeyGener.getNextId());
					object_handle_type.setHandle_type(OperationType.UPDATE.getCode());
					object_handle_type.setHandle_value(handleTypMap.get("update"));
					object_handle_type.add(Dbo.db());
					// 插入delete对应的值
					object_handle_type.setObject_handle_id(PrimayKeyGener.getNextId());
					object_handle_type.setHandle_type(OperationType.DELETE.getCode());
					object_handle_type.setHandle_value(handleTypMap.get("delete"));
					object_handle_type.add(Dbo.db());
				}
			}
		}
		// 13.返回对象采集ID
		return object_collect.getOdc_id();
	}

	@Method(desc = "保存对象采集结构信息", logicStep = "1.数据可访问权限处理方式：该方法没有用户访问权限限制" +
			"2.封装对象采集结构信息" +
			"3.保存对象采集结构信息")
	@Param(name = "objectCollectTask", desc = "对象采集对应信息", range = "不为空", isBean = true)
	@Param(name = "j", desc = "字段序号", range = "不为空")
	@Param(name = "columnMap", desc = "列信息集合", range = "不为空")
	@Param(name = "object_collect_struct", desc = "对象采集结构信息", range = "不为空", isBean = true)
	private void addObjectCollectStruct(Object_collect_task objectCollectTask, int j, Map<String, String> columnMap,
	                                    Object_collect_struct object_collect_struct) {
		// 1.数据可访问权限处理方式：该方法没有用户访问权限限制
		// 2.封装对象采集结构信息
		object_collect_struct.setStruct_id(PrimayKeyGener.getNextId());
		object_collect_struct.setOcs_id(objectCollectTask.getOcs_id());
		object_collect_struct.setColumn_name(columnMap.get("columnname"));
		object_collect_struct.setColumn_type(columnMap.get("columntype"));
		object_collect_struct.setIs_key(columnMap.get("is_key"));
		object_collect_struct.setIs_hbase(columnMap.get("is_hbase"));
		object_collect_struct.setIs_rowkey(columnMap.get("is_rowkey"));
		object_collect_struct.setIs_solr(columnMap.get("is_solr"));
		object_collect_struct.setIs_operate(columnMap.get("is_operate"));
		object_collect_struct.setCol_seq(String.valueOf(j));
		object_collect_struct.setColumnposition(columnMap.get("columnposition"));
		object_collect_struct.setData_desc(object_collect_struct.getColumn_name());
		// 3.保存对象采集结构信息
		object_collect_struct.add(Dbo.db());
	}

	@Method(desc = "获取传递到agent的参数", logicStep = "1.数据可访问权限处理方式：该方法没有用户访问权限限制" +
			"2.封装半结构化采集与agent交互参数" +
			"3.返回半结构化采集与agent交互参数")
	@Param(name = "object_collect", desc = "对象采集设置表信息", range = "不能为空", isBean = true)
	@Return(desc = "返回半结构化采集与agent交互参数", range = "不能为空")
	private String getJsonParamForAgent(Object_collect object_collect) {
		// 1.数据可访问权限处理方式：该方法没有用户访问权限限制
		// 2.封装半结构化采集与agent交互参数
		Map<String, String> jsonParamMap = new HashMap<String, String>();
		jsonParamMap.put("file_suffix", object_collect.getFile_suffix());
		jsonParamMap.put("is_dictionary", object_collect.getIs_dictionary());
		jsonParamMap.put("data_date", object_collect.getData_date());
		jsonParamMap.put("file_path", object_collect.getFile_path());
		jsonParamMap.put("dbtype", "1");
		// 3.返回半结构化采集与agent交互参数
		return PackUtil.packMsg(JsonUtil.toJson(jsonParamMap));
	}

	@Method(desc = "根据对象采集id查询对象采集对应信息的合集",
			logicStep = "1.根据对象采集id查询对象采集对应信息表返回到前端")
	@Param(name = "odc_id", desc = "对象采集id", range = "不能为空")
	@Return(desc = "对象采集对应信息的合集", range = "可能为空")
	public Result searchObjectCollectTask(long odc_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.根据对象采集id查询对象采集对应信息表返回到前端
		return Dbo.queryResult("SELECT * FROM " + Object_collect_task.TableName
				+ " WHERE odc_id = ?", odc_id);
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

	@Method(desc = "保存对象采集对应信息表",
			logicStep = "1.获取json数组转成对象采集对应信息表的集合" +
					"2.获取对象采集对应信息表list进行遍历" +
					"3.根据对象采集对应信息表id判断是新增还是编辑" +
					"4.根据en_name查询对象采集对应信息表的英文名称是否重复")
	@Param(name = "object_collect_task_array", desc = "多条对象采集对应信息表的JSONArray格式的字符串，" +
			"其中object_collect_task表不能为空的列所对应的值不能为空", range = "不能为空")
	public void saveObjectCollectTaskInfo(String object_collect_task_array) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.获取json数组转成对象采集对应信息表的集合
		List<Object_collect_task> object_collect_tasks = JSONArray
				.parseArray(object_collect_task_array, Object_collect_task.class);
		//2.获取对象采集对应信息表list进行遍历
		for (Object_collect_task object_collect_task : object_collect_tasks) {
			//TODO 使用公共方法校验数据的正确性
			//XXX 这里新增和编辑是放在一起的，因为这里面是保存一个列表的数据，可能为一条或者多条。
			//XXX 这一条或者多条数据会有新增也会有编辑，所以对应在一个方法里面了
			//3.根据对象采集对应信息表id判断是新增还是编辑
			if (object_collect_task.getOcs_id() == null) {
				//新增
				//4.根据en_name查询对象采集对应信息表的英文名称是否重复
				long count = Dbo.queryNumber("SELECT count(1) count FROM " + Object_collect_task.TableName
						+ " WHERE en_name = ?", object_collect_task.getEn_name()).orElseThrow(()
						-> new BusinessException("查询得到的数据必须有且只有一条"));
				if (count > 0) {
					throw new BusinessException("对象采集对应信息表的英文名称重复");
				}
				object_collect_task.setOcs_id(PrimayKeyGener.getNextId());
				object_collect_task.add(Dbo.db());
			} else {
				//更新
				//4.根据en_name查询对象采集对应信息表的英文名称是否重复
				long count = Dbo.queryNumber("SELECT count(1) count FROM "
								+ Object_collect_task.TableName + " WHERE en_name = ? AND ocs_id != ?",
						object_collect_task.getEn_name(), object_collect_task.getOcs_id())
						.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
				if (count > 0) {
					throw new BusinessException("对象采集对应信息表的英文名称重复");
				}
				object_collect_task.update(Dbo.db());
			}
		}
	}

	@Method(desc = "根据ocs_idc查询对象采集任务对应对象采集结构信息",
			logicStep = "1.查询对应对象采集结构信息表，返回前端")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "不可为空")
	@Return(desc = "对象采集任务对应对象采集结构信息的集合", range = "可能为空")
	public Result searchObjectCollectStruct(long ocs_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.查询对应对象采集结构信息表，返回前端
		return Dbo.queryResult("SELECT * FROM " + Object_collect_struct.TableName
				+ "order by col_seq", ocs_id);
	}

	@Method(desc = "根据结构信息id删除对象采集结构信息表",
			logicStep = "1.获取结构信息id，删除对象采集结构信息表")
	@Param(name = "struct_id", desc = "结构信息id", range = "不可为空")
	public void deleteObject_collect_struct(long struct_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.获取结构信息id，删除对象采集结构信息表
		DboExecute.deletesOrThrow("删除object_collect_struct表信息异常，struct_id = " + struct_id,
				"DELETE FROM " + Object_collect_struct.TableName
						+ " WHERE struct_id = ?", struct_id);
	}


	@Method(desc = "保存对象采集对应结构信息表",
			logicStep = "1.获取json数组转成对象采集结构信息表的集合" +
					"2.获取对象采集结构信息list进行遍历" +
					"3.根据对象采集结构信息id判断是新增还是编辑" +
					"4.判断同一个对象采集任务下，对象采集结构信息表的coll_name有没有重复" +
					"5.新增或更新数据库")
	@Param(name = "objectCollectStruct", desc = "多条对象采集对应结构信息表的JSONArray格式的字符串，" +
			"其中object_collect_struct表不能为空的列所对应的值不能为空", range = "不可为空")
	public void saveObjectCollectStruct(String objectCollectStruct) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.获取json数组转成对象采集结构信息表的集合
		Type type = new TypeReference<List<Object_collect_struct>>() {
		}.getType();
		List<Object_collect_struct> objectCollectStructList = JsonUtil.toObject(objectCollectStruct, type);
		//2.获取对象采集结构信息list进行遍历
		for (Object_collect_struct object_collect_struct : objectCollectStructList) {
			//TODO 应该使用一个公共的校验类进行校验
			//XXX 这里新增和编辑是放在一起的，因为这里面是保存一个列表的数据，可能为一条或者多条。
			//XXX 这一条或者多条数据会有新增也会有编辑，所以对应在一个方法里面了
			//3.根据对象采集结构信息id判断是新增还是编辑
			if (object_collect_struct.getStruct_id() == null) {
				//4.判断同一个对象采集任务下，对象采集结构信息表的coll_name有没有重复
				long count = Dbo.queryNumber("SELECT count(1) count FROM "
								+ Object_collect_struct.TableName + " WHERE column_name = ? AND ocs_id = ?"
						, object_collect_struct.getColumn_name(), object_collect_struct.getOcs_id())
						.orElseThrow(() -> new BusinessException("有且只有一个返回值"));
				if (count > 0) {
					throw new BusinessException("同一个对象采集任务下，对象采集结构信息表的coll_name不能重复");
				}
				//新增
				object_collect_struct.setStruct_id(PrimayKeyGener.getNextId());
				//5.新增或更新数据库
				object_collect_struct.add(Dbo.db());
			} else {
				long count = Dbo.queryNumber("SELECT count(1) count FROM "
								+ Object_collect_struct.TableName + " WHERE coll_name = ? AND ocs_id = ? " +
								" AND struct_id != ?", object_collect_struct.getColumn_name()
						, object_collect_struct.getOcs_id(), object_collect_struct.getStruct_id())
						.orElseThrow(() -> new BusinessException("有且只有一个返回值"));
				if (count > 0) {
					throw new BusinessException("同一个对象采集任务下，对象采集结构信息表的coll_name不能重复");
				}
				//更新
				object_collect_struct.update(Dbo.db());
			}
		}
	}

	@Method(desc = "根据对象采集id查询对象采集任务存储设置",
			logicStep = "1.根据对象采集id，查询对象采集任务及每个任务对象的存储设置")
	@Param(name = "odc_id", desc = "对象采集id", range = "不可为空")
	@Return(desc = "采集任务及每个任务的存储设置", range = "不会为空")
	public Result searchObjectStorage(long odc_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.根据对象采集id，查询对象采集任务及每个任务对象的存储设置
		Result result = Dbo.queryResult("SELECT * FROM " + Object_collect_task.TableName + " t1 left join "
				+ Object_storage.TableName + " t2 on t1.ocs_id = t2.ocs_id WHERE odc_id = ?", odc_id);
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

	@Method(desc = "保存对象采集存储设置表",
			logicStep = "1.获取json数组转成对象采集结构信息表的集合" +
					"2.根据对象采集存储设置表id是否为空判断是编辑还是新增" +
					"3.保存对象采集存储设置表" +
					"4.更新对象采集设置表的字段是否完成设置并发送成功为是")
	@Param(name = "object_storage_array", desc = "多条对象采集存储设置表的JSONArray格式的字符串，" +
			"其中object_storage表不能为空的列所对应的值不能为空", range = "不能为空")
	@Param(name = "odc_id", desc = "对象采集id", range = "不能为空")
	public void saveObjectStorage(String object_storage_array, long odc_id) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//1.获取json数组转成对象采集结构信息表的集合
		Type type = new TypeReference<List<Object_storage>>() {
		}.getType();
		List<Object_storage> object_storage_list = JsonUtil.toObject(object_storage_array, type);
		for (Object_storage object_storage : object_storage_list) {
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
		//4.更新对象采集设置表的字段是否完成设置并发送成功为是
		DboExecute.updatesOrThrow("更新表" + Object_collect.TableName + "失败"
				, "UPDATE " + Object_collect.TableName + " SET is_sendok = ?"
						+ " WHERE odc_id = ? ", IsFlag.Shi.getCode(), odc_id);
	}
}
