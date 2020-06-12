//package hrds.b.biz.agent.semistructured.collectconf;
//
//import com.alibaba.fastjson.TypeReference;
//import fd.ng.core.annotation.DocClass;
//import fd.ng.core.annotation.Method;
//import fd.ng.core.annotation.Param;
//import fd.ng.core.annotation.Return;
//import fd.ng.core.utils.DateUtil;
//import fd.ng.core.utils.JsonUtil;
//import fd.ng.core.utils.StringUtil;
//import fd.ng.core.utils.Validator;
//import fd.ng.netclient.http.HttpClient;
//import fd.ng.web.action.ActionResult;
//import fd.ng.web.util.Dbo;
//import hrds.b.biz.agent.datafileconf.CheckParam;
//import hrds.b.biz.agent.tools.SendMsgUtil;
//import hrds.commons.base.BaseAction;
//import hrds.commons.codes.IsFlag;
//import hrds.commons.codes.ObjectCollectType;
//import hrds.commons.entity.Object_collect;
//import hrds.commons.entity.Object_collect_struct;
//import hrds.commons.entity.Object_collect_task;
//import hrds.commons.entity.Object_handle_type;
//import hrds.commons.exception.BusinessException;
//import hrds.commons.utils.AgentActionUtil;
//import hrds.commons.utils.key.PrimayKeyGener;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@DocClass(desc = "半结构化采集配置类", author = "dhw", createdate = "2020/6/9 10:36")
//public class SemiStructuredConfAction extends BaseAction {
//
//	private static final Logger logger = LogManager.getLogger();
//
//	private static final Type MAPTYPE = new TypeReference<Map<String, Object>>() {
//	}.getType();
//	private static final Type LISTTYPE = new TypeReference<List<Map<String, Object>>>() {
//	}.getType();
//
//	@Method(desc = "获取新增半结构化采集配置信息",
//			logicStep = "1.根据agent_id获取调用Agent服务的接口" +
//					"2.根据url远程调用Agent的后端代码获取采集服务器上的日期、" +
//					"时间、操作系统类型和主机名等基本信息" +
//					"3.返回新增半结构化采集配置信息")
//	@Param(name = "agent_id", desc = "采集agent主键ID", range = "不为空")
//	@Return(desc = "返回新增半结构化采集配置信息", range = "不会为空")
//	public Map<String, Object> getAddObjectCollectConf(long agent_id) {
//		// 1.根据agent_id获取调用Agent服务的接口
//		String url = AgentActionUtil.getUrl(agent_id, getUserId(), AgentActionUtil.GETSERVERINFO);
//		//2.根据url远程调用Agent的后端代码获取采集服务器上的日期、时间、操作系统类型和主机名等基本信息
//		HttpClient.ResponseValue resVal = new HttpClient().post(url);
//		ActionResult ar = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class)
//				.orElseThrow(() -> new BusinessException("连接远程" + url + "服务异常"));
//		if (!ar.isSuccess()) {
//			throw new BusinessException("远程连接" + url + "的Agent失败");
//		}
//		// 3.返回新增半结构化采集配置信息
//		Map<String, Object> map = ar.getDataForMap();
//		map.put("localDate", DateUtil.getSysDate());
//		map.put("localTime", DateUtil.getSysTime());
//		return map;
//	}
//
//	@Method(desc = "根据对象采集id获取半结构化采集配置信息（编辑任务时数据回显）",
//			logicStep = "1.数据可访问权限处理方式：通过agent_id进行访问权限限制" +
//					"2.检查当前任务是否存在" +
//					"3.根据对象采集id查询半结构化采集配置首页数据")
//	@Param(name = "odc_id", desc = "对象采集id", range = "不为空")
//	@Return(desc = "返回对象采集id查询半结构化采集配置首页数据", range = "不为空")
//	public Map<String, Object> getObjectCollectConfById(long odc_id) {
//		// 1.数据可访问权限处理方式：通过agent_id进行访问权限限制
//		// 2.检查当前任务是否存在
//		long countNum = Dbo.queryNumber(
//				"SELECT COUNT(1) FROM  " + Object_collect.TableName + " WHERE odc_id = ?",
//				odc_id).orElseThrow(() -> new BusinessException("SQL查询错误"));
//		if (countNum == 0) {
//			CheckParam.throwErrorMsg("任务( %s )不存在!!!", odc_id);
//		}
//		// 3.根据对象采集id查询半结构化采集配置首页数据
//		return Dbo.queryOneObject(
//				"SELECT * FROM " + Object_collect.TableName + " WHERE odc_id = ?", odc_id);
//	}
//
//	@Method(desc = "半结构化采集查看表",
//			logicStep = "1.数据可访问权限处理方式：通过user_id与agent_id进行访问权限限制" +
//					"2.判断当是否存在数据字典选择否的时候数据日前是否为空" +
//					"3.获取半结构化与agent服务交互参数" +
//					"4.获取解析与agent服务交互返回响应数据" +
//					"5.判断当前目录下的数据文件响应信息是否为空" +
//					"6.不为空，循环获取当前目录下的数据文件表信息" +
//					"7.返回解析后当前目录获取表信息" +
//					"8.返回当前目录下的数据文件响应信息")
//	@Param(name = "agent_id", desc = "agent信息表主键ID", range = "新增agent时生成")
//	@Param(name = "file_path", desc = "采集文件路径", range = "不为空")
//	@Param(name = "is_dictionary", desc = "是否存在数据字典", range = "使用（IsFlag）代码项")
//	@Param(name = "data_date", desc = "数据日期", range = "是否存在数据字典选择否的时候必选", nullable = true)
//	@Param(name = "file_suffix", desc = "文件后缀名", range = "无限制")
//	@Return(desc = "返回解析数据字典后的表数据", range = "无限制")
//	public List<Map<String, Object>> viewTable(String file_path, long agent_id, String is_dictionary,
//	                                           String data_date, String file_suffix) {
//		// 1.数据可访问权限处理方式：通过user_id与agent_id进行访问权限限制
//		// 2.判断当是否存在数据字典选择否的时候数据日期是否为空
//		if (IsFlag.Fou == IsFlag.ofEnumByCode(is_dictionary) && StringUtil.isBlank(data_date)) {
//			throw new BusinessException("当是否存在数据字典选择否，数据日期不能为空");
//		}
//		// 4.获取解析与agent服务交互返回响应数据
//		List<Map<String, Object>> tableNames = SendMsgUtil.getDictionaryDataFromAgent(file_path, file_suffix,
//				is_dictionary, data_date, agent_id, getUserId());
//		// 5.判断当前目录下的数据文件响应信息是否为空
//		if (!tableNames.isEmpty()) {
//			List<Map<String, Object>> tableNameList = new ArrayList<>();
//			// 6.不为空，循环获取当前目录下的数据文件表信息
//			for (Map<String, Object> tableMap : tableNames) {
//				Map<String, Object> tableNameMap = new HashMap<>();
//				tableMap.put("table_name", tableMap.get("table_name").toString());
//				tableMap.put("table_ch_name", tableMap.get("table_ch_name").toString());
//				tableNameList.add(tableNameMap);
//			}
//			// 7.返回解析后当前目录获取表信息
//			return tableNameList;
//		} else {
//			// 8.返回当前目录下的数据文件响应信息
//			return tableNames;
//		}
//	}
//
//	@Method(desc = "保存半结构化文件采集页面信息到对象采集设置表对象，同时返回对象采集id",
//			logicStep = "1.数据可访问权限处理方式：该表没有对应的用户访问权限限制" +
//					"2.根据obj_number查询半结构化采集任务编号是否重复" +
//					"3.保存object_collect表" +
//					"4.判断是否存在数据字典选择否的时候数据日期是否为空" +
//					"5.保存object_collect表" +
//					"6.获取agent解析数据字典返回json格式数据" +
//					"7.遍历json对象" +
//					"8.object_collect_task表信息入库" +
//					"9.获取字段信息" +
//					"10.保存对象采集结构信息" +
//					"11.保存对象采集数据处理类型对应表信息" +
//					"12.保存数据存储表信息入库" +
//					"13.返回对象采集ID")
//	@Param(name = "object_collect", desc = "对象采集设置表对象，对象中不能为空的字段必须有值",
//			range = "不可为空", isBean = true)
//	@Return(desc = "对象采集设置表id，新建的id后台生成的所以要返回到前端", range = "不会为空")
//	public long addObjectCollect(Object_collect object_collect) {
//		// 1.数据可访问权限处理方式：该表没有对应的用户访问权限限制
//		// 2.根据obj_number查询半结构化采集任务编号是否重复
//		long count = Dbo.queryNumber(
//				"SELECT count(1) count FROM " + Object_collect.TableName + " WHERE obj_number = ?",
//				object_collect.getObj_number()).orElseThrow(() -> new BusinessException("sql查询错误"));
//		if (count > 0) {
//			throw new BusinessException("半结构化采集任务编号重复");
//		}
//		// 3.之前对象采集存在行采集与对象采集两种，目前仅支持行采集,所以默认给
//		object_collect.setObject_collect_type(ObjectCollectType.HangCaiJi.getCode());
//		// 4.判断是否存在数据字典选择否的时候数据日期是否为空
//		if (IsFlag.Fou == IsFlag.ofEnumByCode(object_collect.getIs_dictionary()) &&
//				StringUtil.isBlank(object_collect.getData_date())) {
//			throw new BusinessException("当是否存在数据字典选择否的时候，数据日期不能为空");
//		}
//		// 如果选择有数据字典数据日期为空
//		if (IsFlag.Shi == IsFlag.ofEnumByCode(object_collect.getIs_dictionary())) {
//			object_collect.setData_date("");
//		}
//		// 5.保存object_collect表
//		object_collect.setOdc_id(PrimayKeyGener.getNextId());
//		object_collect.add(Dbo.db());
//		// 12.返回对象采集ID
//		return object_collect.getOdc_id();
//	}
//
//	@Method(desc = "更新半结构化文件采集页面信息到对象采集设置表对象，同时返回对象采集id",
//			logicStep = "1.数据可访问权限处理方式：该表没有对应的用户访问权限限制" +
//					"2.判断是否存在数据字典选择否的时候数据日期是否为空" +
//					"3.更新object_collect表" +
//					"4.获取传递到agent的参数" +
//					"5.获取agent解析数据字典返回json格式数据" +
//					"6.如果数据字典减少了表，则需要删除之前在数据库中记录的表" +
//					"7.遍历数据字典信息循环更新半结构化对应信息" +
//					"8.通过对象采集id查询对象采集对应信息" +
//					"9.更新保存对象采集对应信息" +
//					"10.获取字段信息" +
//					"11.保存对象采集结构信息" +
//					"12.保存对象采集数据处理类型对应表" +
//					"13.返回对象采集ID")
//	@Param(name = "object_collect", desc = "对象采集设置表对象", range = "不可为空", isBean = true)
//	@Return(desc = "返回对象采集配置ID", range = "不能为空")
//	public long updateObjectCollect(Object_collect object_collect) {
//		// 1.数据可访问权限处理方式：该表没有对应的用户访问权限限制
//		Validator.notNull(object_collect.getOdc_id(), "对象采集ID不能为空");
//		// 2.判断是否存在数据字典选择否的时候数据日期是否为空
//		if (IsFlag.Fou == IsFlag.ofEnumByCode(object_collect.getIs_dictionary()) &&
//				StringUtil.isBlank(object_collect.getData_date())) {
//			throw new BusinessException("当是否存在数据字典是否的时候，数据日期不能为空");
//		}
//		// 3.更新object_collect表
//		object_collect.update(Dbo.db());
//		// 5.获取agent解析数据字典返回json格式数据
//		List<Map<String, Object>> tableNames =
//				SendMsgUtil.getDictionaryDataFromAgent(object_collect.getFile_path(),
//						object_collect.getFile_suffix(), object_collect.getIs_dictionary(),
//						object_collect.getData_date(), object_collect.getAgent_id(), getUserId());
//		List<String> tableNameList = new ArrayList<>();
//		// 6.获取所有表集合
//		for (Map<String, Object> tableNameMap : tableNames) {
//			tableNameList.add(tableNameMap.get("table_name").toString());
//		}
//		// 7.如果数据字典减少了表，则需要删除之前在数据库中记录的表
//		List<Map<String, Object>> objCollectTaskList = Dbo.queryList("select en_name,ocs_id from "
//				+ Object_collect_task.TableName + " where odc_id =?", object_collect.getOdc_id());
//		for (Map<String, Object> objectMap : objCollectTaskList) {
//			String en_name = objectMap.get("en_name").toString();
//			// 如果数据库中有但是字典中没有的表，将删除
//			if (!tableNameList.contains(en_name)) {
//				Long ocs_id = new Long(objectMap.get("ocs_id").toString());
//				// 删除对象采集对应信息
//				Dbo.execute("delete from " + Object_collect_task.TableName + " where ocs_id=?", ocs_id);
//				// 删除对象采集结构信息
//				Dbo.execute("delete from " + Object_collect_struct.TableName + " where ocs_id=?", ocs_id);
//				// 删除处理方式
//				Dbo.execute("delete from " + Object_handle_type.TableName + " where ocs_id =?", ocs_id);
//			}
//		}
////		// 8.遍历数据字典信息循环更新半结构化对应信息
////		for (Map<String, Object> tableNameMap : tableNames) {
////			String tableName = tableNameMap.get("table_name").toString();
////			// 9.通过对象采集id以及表名查询对象采集对应信息
////			Map<String, Object> taskMap = Dbo.queryOneObject(
////					"select * from " + Object_collect_task.TableName + " where odc_id = ? and en_name = ?",
////					object_collect.getOdc_id(), tableName);
////			// agent有，数据库没有直接新增
////			Object_collect_task objectCollectTask = new Object_collect_task();
////			if (taskMap.isEmpty()) {
////				addObjectCollectTask(object_collect, tableNameMap, objectCollectTask);
////			} else {
////				// 第一行数据只有数据字典不存在的时候才会有值
////				if (IsFlag.Fou == IsFlag.ofEnumByCode(object_collect.getIs_dictionary())) {
////					objectCollectTask.setFirstline(tableNameMap.get("firstLine").toString());
////				}
////				objectCollectTask.setZh_name(tableNameMap.get("table_ch_name").toString());
////				objectCollectTask.setEn_name(tableName);
////				// 如果有数据字典updatetype需要更新
////				if (IsFlag.Shi == IsFlag.ofEnumByCode(object_collect.getIs_dictionary())) {
////					objectCollectTask.setUpdatetype(tableNameMap.get("update_type").toString());
////				}
////				objectCollectTask.setOcs_id(taskMap.get("ocs_id").toString());
////				// 10.更新保存对象采集对应信息
////				objectCollectTask.update(Dbo.db());
////			}
////			// 如果没有数据字典，第一次新增则不会加载object_collect_struct，第二次编辑也不会修改库中信息
////			if (IsFlag.Shi == IsFlag.ofEnumByCode(object_collect.getIs_dictionary())) {
////				// 11.获取字段信息
////				List<Map<String, String>> columns = JsonUtil.toObject(tableNameMap.get("columns").toString(),
////						LISTTYPE);
////				if (!columns.isEmpty()) {
////					Dbo.execute("delete from " + Object_collect_struct.TableName + " where ocs_id = ?",
////							objectCollectTask.getOcs_id());
////					addObjectCollectStruct(objectCollectTask, columns);
////				}
////				// 如果没有数据字典，第一次新增则不会加载object_handle_type，第二次编辑也不会修改库中信息
////				Map<String, String> handleTypMap =
////						JsonUtil.toObject(tableNameMap.get("handle_type").toString(), MAPTYPE);
////				// 13.保存对象采集数据处理类型对应表
////				Dbo.execute("delete from " + Object_handle_type.TableName + " where ocs_id=?",
////						objectCollectTask.getOcs_id());
////				addObjectHandleType(objectCollectTask, handleTypMap);
////			}
////		}
//		// 14.返回对象采集ID
//		return object_collect.getOdc_id();
//	}
//
//}
