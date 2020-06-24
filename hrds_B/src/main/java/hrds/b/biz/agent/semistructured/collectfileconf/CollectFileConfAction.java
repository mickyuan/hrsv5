package hrds.b.biz.agent.semistructured.collectfileconf;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.tools.SendMsgUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.CollectDataType;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.OperationType;
import hrds.commons.entity.Object_collect;
import hrds.commons.entity.Object_collect_struct;
import hrds.commons.entity.Object_collect_task;
import hrds.commons.entity.Object_handle_type;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.AgentActionUtil;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.PackUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

@DocClass(desc = "半结构化采集文件配置类", author = "dhw", createdate = "2020/6/10 14:29")
public class CollectFileConfAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger();

	@Method(desc = "根据对象采集id查询对象采集对应信息的合集(采集文件配置）",
			logicStep = "1.根据对象采集id查询对象采集对应信息表返回到前端" +
					"2.判断当前半结构化采集任务是否已存在" +
					"3.获取解析数据字典向agent发送请求所需参数" +
					"4.解析数据字典获取数据字典表信息并返回")
	@Param(name = "odc_id", desc = "对象采集id", range = "不能为空")
	@Return(desc = "对象采集对应信息的合集", range = "可能为空")
	public Map<String, Object> searchObjectCollectTask(long odc_id) {
		// 1.数据可访问权限处理方式：该表没有对应的用户访问权限限制
		// 2.判断当前半结构化采集任务是否已存在
		isObjectCollectExist(odc_id);
		// 3.获取解析数据字典向agent发送请求所需参数
		Object_collect object_collect = getObjectCollect(odc_id);
		object_collect.setOdc_id(odc_id);
		// 4.解析数据字典获取数据字典表信息并返回
		List<Object_collect_task> tableInfo = getTableInfo(object_collect);
		Map<String, Object> tableMap = new HashMap<>();
		tableMap.put("is_dictionary", object_collect.getIs_dictionary());
		tableMap.put("tableInfo", tableInfo);
		return tableMap;
	}

	@Method(desc = "获取对象采集配置信息", logicStep = "1.获取对象采集配置信息")
	@Param(name = "odc_id", desc = "对象采集id", range = "不能为空")
	@Return(desc = "返回对象采集配置信息", range = "无限制")
	private Object_collect getObjectCollect(long odc_id) {
		// 1.获取对象采集配置信息
		return Dbo.queryOneObject(Object_collect.class,
				"select * from " + Object_collect.TableName + " where odc_id=?", odc_id)
				.orElseThrow(() -> new BusinessException("sql查询错误或者映射实体失败"));
	}

	@Method(desc = "解析数据字典获取数据字典表信息",
			logicStep = "1.数据可访问权限处理方式：该表没有对应的用户访问权限限制" +
					"2.与agent交互获取agent解析数据字典获取数据字典表数据" +
					"3.获取数据库当前任务下的表集合" +
					"4.获取数据字典与数据库表集合" +
					"5.如果数据库为空则说明是第一次直接返回数据字典表数据" +
					"6.数据字典没有，数据库有的移除" +
					"7.数据字典有，数据库没有（新增）" +
					"8.获取数据库与数据字典并集并返回")
	@Param(name = "object_collect", desc = "半结构化采集设置实体对象", range = "与数据库对应字段规则一致",
			isBean = true)
	private List<Object_collect_task> getTableInfo(Object_collect object_collect) {
		// 1.数据可访问权限处理方式：该表没有对应的用户访问权限限制
		// 2.与agent交互获取agent解析数据字典获取数据字典表数据
		List<Object_collect_task> dicTableList = getDictionaryTableInfo(object_collect);
		// 3.获取数据库当前任务下的表集合
		List<Object_collect_task> objCollectTaskList = getObjectCollectTaskList(object_collect.getOdc_id());
		// 4.获取数据字典与数据库表集合
		List<String> dicTableNameList = getTableName(dicTableList);
		List<String> tableNameList = getTableName(objCollectTaskList);
		// 5.如果数据库为空则说明是第一次直接返回数据字典表数据
		if (tableNameList.isEmpty()) {
			return dicTableList;
		}
		// 6.数据字典没有，数据库有的移除
		List<String> deleteList =
				tableNameList.stream().filter(item -> !dicTableNameList.contains(item))
						.collect(Collectors.toList());
		objCollectTaskList.removeIf(object_collect_task -> deleteList.contains(object_collect_task.getEn_name()));
		// 7.数据字典有，数据库没有（新增）
		List<String> addList =
				dicTableNameList.stream().filter(item -> !tableNameList.contains(item))
						.collect(Collectors.toList());
		dicTableList.removeIf(object_collect_task -> !addList.contains(object_collect_task.getEn_name()));
		// 8.获取数据库与数据字典并集并返回
		dicTableList.addAll(objCollectTaskList);
		return dicTableList;
	}

	@Method(desc = "获取数据库半结构化采集对应表数据", logicStep = "1.获取数据库半结构化采集对应表数据")
	@Param(name = "odc_id", desc = "对象采集id", range = "不能为空")
	@Return(desc = "返回数据库半结构化采集对应表数据", range = "无限制")
	private List<Object_collect_task> getObjectCollectTaskList(long odc_id) {
		// 1.获取数据库半结构化采集对应表数据
		return Dbo.queryList(Object_collect_task.class,
				"select * from " + Object_collect_task.TableName + " where odc_id =?",
				odc_id);
	}

	@Method(desc = "获取数据字典半结构化采集对应表数据", logicStep = "1.获取数据字典半结构化采集对应表数据")
	@Param(name = "object_collect", desc = "半结构化采集设置实体对象", range = "与数据库对应字段规则一致",
			isBean = true)
	@Return(desc = "返回获取数据字典半结构化采集对应表数据", range = "无限制")
	private List<Object_collect_task> getDictionaryTableInfo(Object_collect object_collect) {
		// 1.获取数据字典半结构化采集对应表数据
		return SendMsgUtil.getDictionaryTableInfo(
				object_collect.getAgent_id(), object_collect.getFile_path(),
				object_collect.getIs_dictionary(), object_collect.getData_date(),
				object_collect.getFile_suffix(), getUserId());
	}

	@Method(desc = "获取集合Bean中的表名称", logicStep = "获取表名称")
	@Param(name = "tableBeanList", desc = "集合Object_collect_task数据集合", range = "可以为空")
	@Return(desc = "返回处理后的数据信息集合,只要表的名称", range = "可以为空")
	private List<String> getTableName(List<Object_collect_task> tableBeanList) {
		List<String> tableNameList = new ArrayList<>();
		tableBeanList.forEach(
				object_collect_task -> tableNameList.add(object_collect_task.getEn_name()));
		return tableNameList;
	}

	@Method(desc = "获取集合Bean中的表名称", logicStep = "获取表名称")
	@Param(name = "tableBeanList", desc = "集合Object_collect_struct数据集合", range = "可以为空")
	@Return(desc = "返回处理后的数据信息集合,只要表的名称", range = "可以为空")
	private List<String> getColumnName(List<Object_collect_struct> tableBeanList) {
		List<String> tableNameList = new ArrayList<>();
		tableBeanList.forEach(
				Object_collect_struct -> tableNameList.add(Object_collect_struct.getColumn_name()));
		return tableNameList;
	}

	@Method(desc = "数据字典表新增入库", logicStep = "1.object_collect_task表信息循环入库")
	@Param(name = "object_collect", desc = "对象采集配置表实体对象", range = "不为空", isBean = true)
	@Param(name = "dicTableList", desc = "数据字典表集合", range = "不为空")
	private void addDicTable(Object_collect object_collect, List<Object_collect_task> dicTableList) {
		// 1.object_collect_task表信息循环入库
		for (Object_collect_task object_collect_task : dicTableList) {
			object_collect_task.setOcs_id(PrimayKeyGener.getNextId());
			object_collect_task.setDatabase_code(DataBaseCode.UTF_8.getCode());
			object_collect_task.setOdc_id(object_collect.getOdc_id());
			object_collect_task.setAgent_id(object_collect.getAgent_id());
			object_collect_task.setCollect_data_type(CollectDataType.JSON.getCode());
			object_collect_task.add(Dbo.db());
		}
	}

	@Method(desc = "删除表的信息", logicStep = "1.删除对象采集对应信息" +
			"2.删除对象采集结构信息" +
			"3.删除采集数据处理类型对应表信息")
	@Param(name = "odc_id", desc = "对象采集id", range = "不能为空")
	@Param(name = "deleteNameList", desc = "数据库有数据字典没有的表集合", range = "可以为空")
	private void deleteTable(long odc_id, List<String> deleteNameList) {
		if (deleteNameList != null && deleteNameList.size() != 0) {
			SqlOperator.Assembler assembler = SqlOperator.Assembler.newInstance();
			assembler.addSql(
					"select ocs_id from " + Object_collect_task.TableName
							+ " oct join " + Object_collect.TableName + " oc on oct.odc_id=oc.odc_id" +
							" where odc_id=? ").addParam(odc_id)
					.addORParam("en_name", deleteNameList.toArray());
			// 获取表名称对应对象采集任务编号集合
			List<Long> ocsIdList = Dbo.queryOneColumnList(assembler.sql(), assembler.params());
			for (Long ocs_id : ocsIdList) {
				// 1.删除对象采集对应信息
				DboExecute.deletesOrThrow("删除表失败",
						"delete from " + Object_collect_task.TableName + " where ocs_id=?",
						ocs_id);
				// 2.删除对象采集结构信息
				Dbo.execute("delete from " + Object_collect_struct.TableName + " where ocs_id=?",
						ocs_id);
				// 3.删除采集数据处理类型对应表
				Dbo.execute("delete from " + Object_handle_type.TableName + " where ocs_id =?",
						ocs_id);
			}
		}
	}

	@Method(desc = "根据表名查询半结构化采集列结构信息(有数据字典）",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.判断当前半结构化采集任务是否还存在" +
					"3.获取当前对象采集任务配置信息" +
					"4.判断数据字典是否存在，不存在就抛异常" +
					"5.获取数据字典所有表对应列信息并判断表对应列信息是否存在")
	@Param(name = "odc_id", desc = "对象采集id", range = "不能为空")
	@Param(name = "en_name", desc = "表名称", range = "无限制")
	@Return(desc = "返回半结构化采集列结构信息", range = "无限制")
	public List<Object_collect_struct> getObjectCollectStructByTableName(long odc_id, String en_name) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.判断当前半结构化采集任务是否还存在
		isObjectCollectExist(odc_id);
		// 3.获取当前对象采集任务配置信息
		Object_collect object_collect = getObjectCollect(odc_id);
		// 4.判断数据字典是否存在，不存在就抛异常
		if (IsFlag.Fou == IsFlag.ofEnumByCode(object_collect.getIs_dictionary())) {
			throw new BusinessException("该采集任务的是否数据字典应为是,实际为否，请检查");
		}
		// 5.获取数据字典所有表对应列信息并判断表对应列信息是否存在
		return getDicColumnsByTableName(object_collect, en_name);
	}

	@Method(desc = "根据对象采集任务编号查询半结构化采集列结构信息(有数据字典)",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.判断当前半结构化采集任务是否还存在" +
					"3.获取当前对象采集任务配置信息" +
					"4.判断数据字典是否存在，不存在就抛异常" +
					"5.获取数据字典所有表对应列信息并判断表对应列信息是否存在" +
					"6.如果列结构信息ID为空说明是数据字典新增的表，这时候直接返回数据字典对应列信息即可" +
					"7.查询数据库表对应列信息" +
					"8.获取数据字典以及数据库表对应列名称" +
					"9.数据字典没有，数据库有的移除" +
					"10.数据字典有，数据库没有（新增）" +
					"11.获取数据库与数据字典并集并返回")
	@Param(name = "odc_id", desc = "对象采集id", range = "不能为空")
	@Param(name = "ocs_id", desc = "对象采集任务编号(对象采集对应信息表ID）", range = "新增对象采集任务时生成",
			nullable = true)
	@Param(name = "en_name", desc = "表名称", range = "无限制")
	@Return(desc = "返回半结构化采集列结构信息", range = "无限制")
	public List<Object_collect_struct> getObjectCollectStructById(long odc_id, long ocs_id, String en_name) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.判断当前半结构化采集任务是否还存在
		isObjectCollectExist(odc_id);
		// 3.获取当前对象采集任务配置信息
		Object_collect object_collect = getObjectCollect(odc_id);
		// 4.判断数据字典是否存在，不存在就抛异常
		if (IsFlag.Fou == IsFlag.ofEnumByCode(object_collect.getIs_dictionary())) {
			throw new BusinessException("该采集任务的是否数据字典应为是,实际为否，请检查");
		}
		// 5.获取数据字典所有表对应列信息并判断表对应列信息是否存在
		List<Object_collect_struct> dicColumnByTable = getDicColumnsByTableName(object_collect, en_name);
		// 7.查询数据库表对应列信息
		List<Object_collect_struct> objectCollectStructList = getObjectCollectStructList(ocs_id);
		// 8.获取数据字典以及数据库表对应列名称
		List<String> dicColumnNameList = getColumnName(dicColumnByTable);
		List<String> columnNameList = getColumnName(objectCollectStructList);
		// 9.数据字典没有，数据库有的移除
		List<String> deleteList =
				columnNameList.stream().filter(item -> !dicColumnNameList.contains(item))
						.collect(Collectors.toList());
		objectCollectStructList.removeIf(
				object_collect_struct -> deleteList.contains(object_collect_struct.getColumn_name()));
		// 10.数据字典有，数据库没有（新增）
		List<String> addList =
				dicColumnNameList.stream().filter(item -> !columnNameList.contains(item))
						.collect(Collectors.toList());
		dicColumnByTable.removeIf(
				object_collect_struct -> !addList.contains(object_collect_struct.getColumn_name()));
		// 11.获取数据库与数据字典并集并返回
		dicColumnByTable.addAll(objectCollectStructList);
		return dicColumnByTable;
	}

	@Method(desc = "没有数据字典时获取采集列结构", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.判断当前半结构化采集任务是否还存在" +
			"3.获取当前任务表对应第一行数据" +
			"4.返回没有数据字典解析后的第一行数据的采集列信息")
	@Param(name = "odc_id", desc = "对象采集id", range = "新增对应采集配置信息时生成")
	@Param(name = "ocs_id", desc = "对象采集任务编号(对象采集对应信息表ID）", range = "新增对象采集任务时生成")
	@Return(desc = "返回没有数据字典解析后的第一行数据的采集列信息", range = "无限制")
	public JSONArray getFirstLineInfo(long odc_id, long ocs_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.判断当前半结构化采集任务是否还存在
		isObjectCollectExist(odc_id);
		// 3.获取当前任务表对应第一行数据
		String firstLine = getFirstLineInfo(ocs_id);
		// 4.返回没有数据字典解析后的第一行数据的采集列信息
		return parseFirstLine(firstLine, "");
	}

	@Method(desc = "查询对象采集结构表信息(采集列结构与getFirstLineInfo一起调用）",
			logicStep = "1.查询对象采集结构表信息")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "新增对象采集任务时生成")
	@Return(desc = "返回象采集结构表信息", range = "无限制")
	public List<Object_collect_struct> getObjectCollectStructList(long ocs_id) {
		// 1.查询对象采集结构表信息
		return Dbo.queryList(Object_collect_struct.class,
				"select * from " + Object_collect_struct.TableName + " where ocs_id=?",
				ocs_id);
	}

	@Method(desc = "获取对象采集结构信息", logicStep = "1.获取数据字典所有表对应列信息" +
			"2.判断表对应列信息是否存在" +
			"3.根据表名获取表对应列信息并返回")
	@Param(name = "object_collect", desc = "对象采集设置表实体对象", range = "与数据库对应字段规则一致", isBean = true)
	@Param(name = "en_name", desc = "表英文名称", range = "无限制")
	@Return(desc = "根据表名获取表对应列信息并返回", range = "无限制")
	private List<Object_collect_struct> getDicColumnsByTableName(Object_collect object_collect,
	                                                             String en_name) {
		Validator.notBlank(object_collect.getFile_path(), "采集文件路径不能为空");
		// 1.获取数据字典所有表对应列信息
		Map<String, List<Object_collect_struct>> allDicColumns = SendMsgUtil.getAllDicColumns(
				object_collect.getAgent_id(), object_collect.getFile_path(), getUserId());
		// 2.判断表对应列信息是否存在
		if (allDicColumns == null || allDicColumns.isEmpty()) {
			throw new BusinessException("数据字典中未找到表对应列信息");
		}
		// 3.根据表名获取表对应列信息并返回
		return allDicColumns.get(en_name);
	}

	@Method(desc = "获取对象采集对应表信息", logicStep = "1.获取对象采集对应表信息")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "新增对象采集任务时生成")
	@Return(desc = "返回对象采集对应表信息", range = "无限制")
	private Object_collect_task getCollectTask(long ocs_id) {
		// 1.获取对象采集对应表信息
		return Dbo.queryOneObject(Object_collect_task.class,
				"select * from " + Object_collect_task.TableName + " where ocs_id=?", ocs_id)
				.orElseThrow(() -> new BusinessException("sql查询错误或者映射实体失败"));
	}

	@Method(desc = "判断当前半结构化采集任务是否还存在", logicStep = "1.判断当前半结构化采集任务是否还存在")
	@Param(name = "odc_id", desc = "对象采集id", range = "新增对象采集配置信息时生成")
	private void isObjectCollectExist(long odc_id) {
		// 1.判断当前半结构化采集任务是否还存在
		if (Dbo.queryNumber(
				"select count(*) from " + Object_collect.TableName + " where odc_id=?",
				odc_id).orElseThrow(() -> new BusinessException("sql查询错误！")) == 0) {
			throw new BusinessException("任务" + odc_id + "已不存在，请检查");
		}
	}

	@Method(desc = "新增数据字典多了的列数据入半结构化采集列结构表",
			logicStep = "1.新增数据字典多了的列数据入半结构化采集列结构表")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "新增对象采集任务时生成")
	@Param(name = "addList", desc = "数据字典多了的列信息集合", range = "无限制")
	private void addColumns(long ocs_id, List<Object_collect_struct> addList) {
		// 1.新增数据字典多了的列数据入半结构化采集列结构表
		addList.forEach(object_collect_struct -> {
			object_collect_struct.setOcs_id(ocs_id);
			object_collect_struct.add(Dbo.db());
		});
	}

	@Method(desc = "获取有数据字典时操作码表（采集数据处理类型对应表）信息）",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.根据对象采集任务编号查询对象采集任务信息" +
					"3.获取当前对象采集任务配置信息" +
					"4.获取所有数据字典表对应数据处理方式信息" +
					"5.如果数据字典不存在，给默认处理方式")
	@Param(name = "odc_id", desc = "对象采集id", range = "新增对象采集配置信息时生成")
	@Param(name = "en_name", desc = "表英文名称", range = "无限制")
	@Return(desc = "返回采集数据处理类型对应表信息", range = "无限制")
	public List<Object_handle_type> searchObjectHandleType(long odc_id, String en_name) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.获取当前对象采集任务配置信息
		Object_collect object_collect = getObjectCollect(odc_id);
		// 4.判断数据字典是否存在，不存在就抛异常
		if (IsFlag.Fou == IsFlag.ofEnumByCode(object_collect.getIs_dictionary())) {
			throw new BusinessException("该采集任务的是否数据字典应为是,实际为否，请检查");
		}
		// 3.从数据字典获取采集数据处理类型对应表信息
		return getObjectHandleTypeList(object_collect, en_name);
	}

	@Method(desc = "获取数据字典对象采集数据处理类型对应表信息",
			logicStep = "1.获取数据字典对象采集数据处理类型对应表信息" +
					"2.数据字典存在时，处理方式是否为空")
	@Param(name = "object_collect", desc = "对象采集设置表实体对象", range = "与数据库对应表规则一致",
			isBean = true)
	@Param(name = "en_name", desc = "表英文名称", range = "无限制")
	@Return(desc = "返回对象采集数据处理类型对应表信息", range = "无限制")
	private List<Object_handle_type> getObjectHandleTypeList(Object_collect object_collect,
	                                                         String en_name) {
		// 1.获取数据字典对象采集数据处理类型对应表信息
		Validator.notBlank(object_collect.getFile_path(), "采集文件路径不能为空");
		Validator.notNull(object_collect.getAgent_id(), "agent ID不能为空");
		Map<String, List<Object_handle_type>> allHandleType = SendMsgUtil.getAllHandleType(
				object_collect.getAgent_id(), object_collect.getFile_path(), getUserId());
		// 2.数据字典存在时，处理方式是否为空
		if (allHandleType == null || allHandleType.isEmpty()) {
			throw new BusinessException("数据字典存在时，处理方式不能为空，请检查数据字典");
		}
		return allHandleType.get(en_name);
	}

	@Method(desc = "保存操作码表（采集数据处理类型对应表）信息",
			logicStep = "1.数据可访问权限处理方式：该方法没有用户访问权限限制" +
					"2.循环保存半结构化数据处理类型表信息")
	@Param(name = "ocs_id", desc = "对象采集任务编号(对象采集对应信息表ID）", range = "新增对象采集任务时生成")
	@Param(name = "objectHandleTypes", desc = "采集数据处理类型对应表实体对象数组", range = "新增对象采集任务时生成"
			, isBean = true)
	private void saveObjectHandleType(long ocs_id, Object_handle_type[] objectHandleTypes) {
		// 1.数据可访问权限处理方式：该方法没有用户访问权限限制
		// 2.循环保存半结构化数据处理类型表信息
		for (Object_handle_type objectHandleType : objectHandleTypes) {
			if (objectHandleType.getObject_handle_id() != null) {
				// 更新
				objectHandleType.update(Dbo.db());
			} else {
				// 新增
				objectHandleType.setObject_handle_id(PrimayKeyGener.getNextId());
				objectHandleType.setOcs_id(ocs_id);
				objectHandleType.add(Dbo.db());
			}
		}
	}

	@Method(desc = "无数据字典时查询第一行数据",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.返回无数据字典时查询第一行数据")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "新增对象采集任务时生成")
	@Return(desc = "返回无数据字典时查询第一行数据", range = "无限制")
	private String getFirstLineInfo(long ocs_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.返回无数据字典时查询第一行数据
		List<String> firstLine = Dbo.queryOneColumnList(
				"select firstline from " + Object_collect.TableName + " t1 left join "
						+ Object_collect_task.TableName + " t2 on t1.odc_id = t2.odc_id" +
						" where t2.ocs_id = ? and t1.is_dictionary = ?",
				ocs_id, IsFlag.Fou.getCode());
		Validator.notEmpty(firstLine, "没有数据字典时第一行数据不能为空");
		return firstLine.get(0);
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
	private JSONArray parseFirstLine(String firstLine, String location) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		JSONArray treeInfo;
		List<String> treeId = StringUtil.split(location, ",");
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
					jsonobject = makeJsonFileToJsonObj(jsonobject, treeId.get(treeId.size() - 1));
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
					parseObject = makeJsonFileToJsonObj(parseObject, treeId.get(treeId.size() - 1));
				}
				// 6.2根据树节点获取当前树节点信息
				treeInfo = getTree(parseObject, location);
			} catch (JSONException e2) {
				throw new BusinessException("既不是jsonArray，也不是jsonObject");
			}
		}
		return treeInfo;
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

	@Method(desc = "获取对象采集树节点信息", logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
			"2.解析json获取树结构信息并返回" +
			"3.获取树信息失败")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "新增对象采集任务时生成")
	@Param(name = "location", desc = "树节点位置，不是根节点则格式如（columns,column_id）", range = "无限制")
	@Return(desc = "获取对象采集树节点信息", range = "无限制")
	public JSONArray getObjectCollectTreeInfo(long ocs_id, String location) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		String firstLine = getFirstLineInfo(ocs_id);
		// 2.解析json获取树结构信息并返回
		return parseFirstLine(firstLine, location);
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

	@Method(desc = "保存对象采集结构信息（采集列结构）",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.判断当前半结构化采集任务是否还存在" +
					"3.先删除原来的列结构信息" +
					"4.循环保存对象采集结构信息入库")
	@Param(name = "objectCollectStructs", desc = "半结构化采集结构表实体对象数组", range = "与数据库对象字段规则一致",
			isBean = true)
	@Param(name = "odc_id", desc = "对象采集id", range = "新增对象采集配置信息时生成")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "新增对象采集任务时生成")
	private void saveObjectCollectStruct(long odc_id, long ocs_id, Object_collect_struct[] objectCollectStructs) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.判断当前半结构化采集任务是否还存在
		isObjectCollectExist(odc_id);
		// 3.先删除原来的列结构信息
		// 因为没有数据字典时数据文件如果少了字段，回显时无法确定删了哪个字段，agent执行时就会因找不到字段而报错
		Dbo.execute("delete from " + Object_collect_struct.TableName + " where ocs_id=?", ocs_id);
		// 4.循环保存对象采集结构信息入库
		for (Object_collect_struct object_collect_struct : objectCollectStructs) {
			object_collect_struct.setOcs_id(ocs_id);
			object_collect_struct.setStruct_id(PrimayKeyGener.getNextId());
			object_collect_struct.add(Dbo.db());
		}
	}

	@Method(desc = "获取对象采集结构表信息", logicStep = "1.获取对象采集结构表信息")
	@Param(name = "ocs_id", desc = "对象采集任务编号", range = "新增对象采集任务时生成")
	@Return(desc = "返回对象采集结构表信息", range = "无限制")
	private Object_collect_task getObjectCollectTask(long ocs_id) {
		// 1.获取对象采集结构表信息
		return Dbo.queryOneObject(Object_collect_task.class,
				"select * from " + Object_collect_task.TableName + " where ocs_id=?",
				ocs_id).orElseThrow(() -> new BusinessException("sql查询错误或实体映射失败"));
	}

	@Method(desc = "删除数据字典少了的列信息", logicStep = "1.判断要删除的列信息集合是否为空，不为空删除" +
			"2.获取所有要删除的结构信息id" +
			"3.删除说有数据库多余的表对应列信息")
	@Param(name = "deleteNameList", desc = "数据字典少了的列信息集合", range = "无限制")
	private void deleteColumns(long ocs_id, List<String> deleteNameList) {
		// 1.判断要删除的列信息集合是否为空，不为空删除
		if (deleteNameList != null && deleteNameList.size() != 0) {
			SqlOperator.Assembler assembler = SqlOperator.Assembler.newInstance();
			assembler.addSql(
					"select struct_id from " + Object_collect_struct.TableName
							+ " ocs join " + Object_collect_task.TableName + " oct on ocs.ocs_id=oct.ocs_id" +
							" where ocs_id=? ").addParam(ocs_id)
					.addORParam("oct.en_name", deleteNameList.toArray());
			// 2.获取所有要删除的结构信息id
			List<Long> structIdList = Dbo.queryOneColumnList(assembler.sql(), assembler.params());
			assembler.clean();
			assembler.addSql("delete from " + Object_collect_struct.TableName + " where ocs_id=?")
					.addParam(ocs_id)
					.addORParam("struct_id", structIdList.toArray());
			// 3.删除说有数据库多余的表对应列信息
			Dbo.execute(assembler.sql(), assembler.params());
		}
	}

	@Method(desc = "保存对象文件配置信息时检查字段(采集文件设置)",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.循环检查英文名是否为空" +
					"3.循环检查中文名是否为空" +
					"4.循环检查采集列结构是否为空" +
					"5.循环检查操作码表是否为空" +
					"6.循环检查操作字段是否为1个")
	@Param(name = "objectCollectTasks", desc = "半结构化采集表实体对象数组", range = "与数组库表字段规则一致",
			isBean = true)
	public void checkFieldsForSaveObjectCollectTask(Object_collect_task[] objectCollectTasks) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		for (int i = 0; i < objectCollectTasks.length; i++) {
			// 2.循环检查英文名是否为空
			Validator.notBlank(objectCollectTasks[i].getEn_name(),
					"第" + (i + 1) + "行表" + objectCollectTasks[i].getEn_name() + "英文名为空，请检查");
			// 3.循环检查中文名是否为空
			Validator.notBlank(objectCollectTasks[i].getZh_name(),
					"第" + (i + 1) + "行表" + objectCollectTasks[i].getZh_name() + "中文名为空，请检查");

			// 4.循环检查采集列结构是否为空
			if (Dbo.queryNumber("select count(*) from " + Object_collect_struct.TableName +
					" where ocs_id=?", objectCollectTasks[i].getOcs_id())
					.orElseThrow(() -> new BusinessException("sql查询错误！")) == 0) {
				throw new BusinessException("第" + (i + 1) + "行表" + objectCollectTasks[i].getEn_name() +
						"采集列结构为空，请检查");
			}
			// 5.循环检查操作码表是否为空
			if (Dbo.queryNumber("select count(*) from " + Object_handle_type.TableName +
					" where ocs_id=?", objectCollectTasks[i].getOcs_id())
					.orElseThrow(() -> new BusinessException("sql查询错误！")) == 0) {
				throw new BusinessException("第" + (i + 1) + "行表" + objectCollectTasks[i].getEn_name() +
						"操作码表为空，请检查");
			}
			// 6.循环检查操作字段是否为1个
			if (Dbo.queryNumber("select count(*) from " + Object_collect_struct.TableName +
							" where ocs_id=? and is_operate=?", objectCollectTasks[i].getOcs_id(),
					IsFlag.Shi.getCode()).orElseThrow(() -> new BusinessException("sql查询错误！")) != 1) {
				throw new BusinessException("第" + (i + 1) + "行表" + objectCollectTasks[i].getEn_name() +
						"操作字段不为1个，请检查");
			}
		}
	}

	@Method(desc = "重写数据字典",
			logicStep = "1.数据可访问权限处理方式：该方法没有访问权限限制" +
					"2.判断当前采集任务是否存在" +
					"3.根据对象采集ID当前半结构化采集任务是否存在数据字典" +
					"4.数据字典已存在，不重写数据字典" +
					"5.数据字典不存在" +
					"6.查询半结构化采集对应表信息" +
					"7.查询半结构化采集结构信息" +
					"8.查询半结构化对象采集数据处理类型对应表信息" +
					"9.封装重写数据字典所需数据" +
					"10.根据对象采集ID获取当前任务对应agent_id以及采集文件路径" +
					"11.调用工具类获取本次访问的agentserver端url" +
					"12、给agent发消息，重写数据字典并获取agent响应" +
					"13、如果测试连接不成功，则抛异常给前端，说明连接失败，如果成功，则不做任务处理")
	@Param(name = "odc_id", desc = "对象采集id", range = "新增对象采集时生成")
	private void rewriteDataDictionary(long odc_id) {
		// 1.数据可访问权限处理方式：该方法没有访问权限限制
		// 2.判断当前采集任务是否存在
		isObjectCollectExist(odc_id);
		// 重写数据字典集合
		List<Object> dictionaryList = new ArrayList<>();
		// 3.根据对象采集ID当前半结构化采集任务是否存在数据字典
		List<Object> isDictionaryList = Dbo.queryOneColumnList("select is_dictionary from "
				+ Object_collect.TableName + " where odc_id=?", odc_id);
		if (!isDictionaryList.isEmpty()) {
			if (IsFlag.Shi == IsFlag.ofEnumByCode(isDictionaryList.get(0).toString())) {
				// 4.数据字典已存在，不重写数据字典
				logger.info("已经存在数据字典，不需要重写数据字典");
			} else {
				// 5.数据字典不存在
				// 6.查询半结构化采集对应表信息
				List<Object_collect_task> objCollectTaskList = Dbo.queryList(Object_collect_task.class,
						"select * from " + Object_collect_task.TableName + " where odc_id=?", odc_id);
				for (Object_collect_task objectCollectTask : objCollectTaskList) {
					Map<String, Object> tableMap = new HashMap<>();
					tableMap.put("table_name", objectCollectTask.getEn_name());
					tableMap.put("table_ch_name", objectCollectTask.getZh_name());
					tableMap.put("updatetype", objectCollectTask.getUpdatetype());
					// 7.查询半结构化采集结构信息
					List<Object_collect_struct> objCollStructList =
							getObjectCollectStructList(objectCollectTask.getOcs_id());
					List<Map<String, Object>> columnList = new ArrayList<>();
					for (Object_collect_struct object_collect_struct : objCollStructList) {
						Map<String, Object> columnMap = new HashMap<>();
						columnMap.put("column_name", object_collect_struct.getColumn_name());
						columnMap.put("column_type", object_collect_struct.getColumn_type());
						columnMap.put("columnposition", object_collect_struct.getColumnposition());
						columnMap.put("is_operate", object_collect_struct.getIs_operate());
						columnList.add(columnMap);
					}
					tableMap.put("columns", columnList);
					// 8.查询半结构化对象采集数据处理类型对应表信息
					List<Object_handle_type> objHandleTypeList = Dbo.queryList(Object_handle_type.class,
							"select * from " + Object_handle_type.TableName + " where ocs_id=?",
							objectCollectTask.getOcs_id());
					Map<String, Object> handleTypeMap = new HashMap<>();
					for (Object_handle_type object_handle_type : objHandleTypeList) {
						String handle_type = object_handle_type.getHandle_type();
						handleTypeMap.put(OperationType.ofValueByCode(handle_type),
								object_handle_type.getHandle_value());
					}
					tableMap.put("handle_type", handleTypeMap);
					// 9.封装重写数据字典所需数据
					dictionaryList.add(tableMap);
				}
				// 10.根据对象采集ID获取当前任务对应agent_id以及采集文件路径
				Object_collect object_collect = Dbo.queryOneObject(Object_collect.class,
						"select agent_id,file_path from " + Object_collect.TableName + " where odc_id = ?",
						odc_id).orElseThrow(() -> new BusinessException("sql查询错误！"));
				// 11.调用工具类获取本次访问的agentserver端url
				String url = AgentActionUtil.getUrl(object_collect.getAgent_id(), getUserId(),
						AgentActionUtil.WRITEDICTIONARY);
				// 12、给agent发消息，重写数据字典并获取agent响应
				HttpClient.ResponseValue resVal = new HttpClient()
						.addData("file_path", object_collect.getFile_path())
						.addData("dictionaryParam", PackUtil.packMsg(JsonUtil.toJson(dictionaryList)))
						.post(url);
				// 13、如果测试连接不成功，则抛异常给前端，说明连接失败，如果成功，则不做任务处理
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

	@Method(desc = "保存采集文件设置信息（需先调保存对象文件配置信息时检查字段方法成功后在调此方法）",
			logicStep = "1.获取json数组转成对象采集对应信息表的集合" +
					"2.判断当前半结构化采集任务是否还存在" +
					"3.获取对象采集对应信息表list进行遍历" +
					"4.根据对象采集对应信息表id判断是新增还是编辑" +
					"4.1根据en_name查询对象采集对应信息表的英文名称是否重复，不重复新增表数据" +
					"4.2.更新对象采集对应信息表数据" +
					"4.3保存半结构化采集结构表信息" +
					"4.4保存采集数据处理类型对应表信息" +
					"5.获取任务下的表信息" +
					"6.获取当前任务配置信息" +
					"7.获取数据字典的表信息" +
					"8.获取数据字典没有而数据字典有的表数据" +
					"9.删除数据库中多余的表数据")
	@Param(name = "objectCollectTasks", desc = "对象采集结构实体对象数组", range = "与数据库对象字段规则一致",
			isBean = true)
	@Param(name = "objectCollectStructs", desc = "半结构化采集结构表实体对象数组", range = "与数据库对象字段规则一致",
			isBean = true)
	@Param(name = "objectHandleTypes", desc = "采集数据处理类型对应表实体对象数组", range = "新增对象采集任务时生成"
			, isBean = true)
	@Param(name = "agent_id", desc = "agent ID", range = "新增agent时生成")
	@Param(name = "odc_id", desc = "对象采集设置表主键ID", range = "新增对象采集设置时生成")
	public void saveObjectCollectTask(long agent_id, long odc_id, Object_collect_task[] objectCollectTasks,
	                                  Object_collect_struct[] objectCollectStructs,
	                                  Object_handle_type[] objectHandleTypes) {
		// 1.数据可访问权限处理方式：该表没有对应的用户访问权限限制
		// 2.判断当前半结构化采集任务是否还存在
		isObjectCollectExist(odc_id);
		// 3.获取对象采集对应信息表list进行遍历
		for (Object_collect_task object_collect_task : objectCollectTasks) {
			/*
				这里新增和编辑是放在一起的，因为这里面是保存一个列表的数据，可能为一条或者多条，
				这一条或者多条数据会有新增也会有编辑，所以对应在一个方法里面了
			*/
			// 4.根据对象采集对应信息表id判断是新增还是编辑
			if (object_collect_task.getOcs_id() == null) {
				//新增
				// 4.1根据en_name查询对象采集对应信息表的英文名称是否重复，不重复新增表数据
				if (Dbo.queryNumber("SELECT count(1) FROM " + Object_collect_task.TableName
						+ " WHERE en_name = ?", object_collect_task.getEn_name()).orElseThrow(()
						-> new BusinessException("sql查询错误")) > 0) {
					throw new BusinessException("对象采集对应信息表的英文名称重复");
				}
				object_collect_task.setOcs_id(PrimayKeyGener.getNextId());
				object_collect_task.setAgent_id(agent_id);
				object_collect_task.setOdc_id(odc_id);
				object_collect_task.add(Dbo.db());
			} else {
				// 4.2.更新对象采集对应信息表数据
				object_collect_task.update(Dbo.db());
			}
			// 4.3保存半结构化采集结构表信息
			saveObjectCollectStruct(odc_id, object_collect_task.getOcs_id(), objectCollectStructs);
			// 4.4保存采集数据处理类型对应表信息
			saveObjectHandleType(object_collect_task.getOcs_id(), objectHandleTypes);
		}
		// 5.获取任务下的表信息
		List<String> databaseTableList = getObjectCollectTaskList(odc_id)
				.stream().map(Object_collect_task::getEn_name).collect(Collectors.toList());
		// 6.获取当前任务配置信息
		Object_collect objectCollect = getObjectCollect(odc_id);
		// 7.获取数据字典的表信息
		List<String> dicTableList = getDictionaryTableInfo(objectCollect)
				.stream().map(Object_collect_task::getEn_name).collect(Collectors.toList());
		// 8.获取数据字典没有而数据字典有的表数据
		List<String> deleteNameList = dicTableList.stream().filter(item -> !databaseTableList.contains(item))
				.collect(Collectors.toList());
		// 9.删除数据库中多余的表数据
		deleteTable(odc_id, deleteNameList);
	}
}
