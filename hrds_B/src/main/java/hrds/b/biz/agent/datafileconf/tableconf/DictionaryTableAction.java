package hrds.b.biz.agent.datafileconf.tableconf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator.Assembler;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.datafileconf.CheckParam;
import hrds.b.biz.agent.tools.SendMsgUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.CleanType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_case;
import hrds.commons.entity.Column_clean;
import hrds.commons.entity.Column_merge;
import hrds.commons.entity.Column_split;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Table_clean;
import hrds.commons.entity.Table_column;
import hrds.commons.entity.Table_info;
import hrds.commons.entity.Table_storage_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.AgentActionUtil;
import hrds.commons.utils.Constant;
import hrds.commons.utils.key.PrimayKeyGener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@DocClass(desc = "数据文件采集表配置", author = "Mr.Lee", createdate = "2020-04-13 14:41")
public class DictionaryTableAction extends BaseAction {

  // 日志打印
  private static final Log logger = LogFactory.getLog(DictionaryTableAction.class);

  // 表的默认清洗顺序
  private static final JSONObject DEFAULT_TABLE_CLEAN_ORDER;
  // 列的默认清洗顺序
  private static final JSONObject DEFAULT_COLUMN_CLEAN_ORDER;

  static {
	DEFAULT_TABLE_CLEAN_ORDER = new JSONObject();
	DEFAULT_TABLE_CLEAN_ORDER.put(CleanType.ZiFuBuQi.getCode(), 1);
	DEFAULT_TABLE_CLEAN_ORDER.put(CleanType.ZiFuTiHuan.getCode(), 2);
	DEFAULT_TABLE_CLEAN_ORDER.put(CleanType.ZiFuHeBing.getCode(), 3);
	DEFAULT_TABLE_CLEAN_ORDER.put(CleanType.ZiFuTrim.getCode(), 4);

	// TODO 按照目前agent程序的逻辑是，列合并永远是排在清洗顺序的最后一个去做，因此这边定义默认的清洗顺序的时候，只定义了6种，而没有定义列合并
	// TODO 所以是否前端需要把列合并去掉
	DEFAULT_COLUMN_CLEAN_ORDER = new JSONObject();
	DEFAULT_COLUMN_CLEAN_ORDER.put(CleanType.ZiFuBuQi.getCode(), 1);
	DEFAULT_COLUMN_CLEAN_ORDER.put(CleanType.ZiFuTiHuan.getCode(), 2);
	DEFAULT_COLUMN_CLEAN_ORDER.put(CleanType.ShiJianZhuanHuan.getCode(), 3);
	DEFAULT_COLUMN_CLEAN_ORDER.put(CleanType.MaZhiZhuanHuan.getCode(), 4);
	DEFAULT_COLUMN_CLEAN_ORDER.put(CleanType.ZiFuChaiFen.getCode(), 5);
	DEFAULT_COLUMN_CLEAN_ORDER.put(CleanType.ZiFuTrim.getCode(), 6);
  }

  @Method(
	  desc = "获取表信息",
	  logicStep =
		  ""
			  + "1: 检查任务信息是否存在"
			  + "2: 获取任务下的表信息"
			  + "3: 获取数据字典的表信息"
			  + "4: 如果数据库中的表记录是空的,则判断数据字典中是否有表信息,如果有则处理数据字典中的表,并添加一些默认值"
			  + "5: 判断数据字典和数据库中的表进行对比,找出被删除的表,新增的表和还存在的表"
			  + "6: 将删除的表在数据库表信息集合中剔除掉,然后再将数据字典新增的表和数据库的表进行合并"
			  + "   6-1: 在数据库表的集合中剔除被删除的表"
			  + "   6-2: 在数据字典中剔除新增意外的表,然后设置默认的字段信息"
			  + "   6-3: 删除数据字典还存在表.保留数据库的信息"
			  + "   6-4: 将数据字典新增的表和数据库中的表集合进行合并"
			  + "7: 检查表是否被采集过"
			  + "8: 返回表的信息")
  @Param(name = "colSetId", desc = "采集任务ID", range = "不可为空")
  @Return(desc = "返回数据字典和数据库的表信息", range = "可以为空,为空表示数据字典及数据库无表记录信息")
  public List<Table_info> getTableData(long colSetId) {

	//    1: 检查任务信息是否存在
	long countNum =
		Dbo.queryNumber("SELECT count(1) FROM database_set WHERE database_id = ?", colSetId)
			.orElseThrow(() -> new BusinessException("SQL查询异常"));

	if (countNum == 0) {
	  CheckParam.throwErrorMsg("采集任务( %s ),不存在!!!", colSetId);
	}
	//    2: 获取任务下的表信息
	List<Table_info> databaseTableList = getTableInfo(colSetId);

	//    3: 获取数据字典的表信息
	List<Table_info> dirTableList = getDirTableData(colSetId);

	//    4: 如果数据库中的表记录是空的,则判断数据字典中是否有表信息,如果有则处理数据字典中的表,并添加一些默认值
	if (!databaseTableList.isEmpty()) {
	  // 判断数据字典的表是否存在
	  if (dirTableList == null || dirTableList.isEmpty()) {
		CheckParam.throwErrorMsg("数据字典中的表信息为空");
	  }
	  //      5: 判断数据字典和数据库中的表进行对比,找出被删除的表和新增的表
	  Map<String, List<String>> differenceInfo =
		  getDifferenceInfo(getTableName(dirTableList), getTableName(databaseTableList));

	  //        6: 将删除的表在数据库表信息集合中剔除掉,然后再将数据字典新增的表和数据库的表进行合并
	  //      6-1: 在数据库表的集合中剔除被删除的表
	  if (!differenceInfo.get("delete").isEmpty()) {
		removeTableBean(differenceInfo.get("delete"), databaseTableList, true);
	  }

	  //      6-2: 在数据字典中找到还存的表,然后删除掉...因为数据库中有此表信息
	  if (!differenceInfo.get("exists").isEmpty()) {
		removeTableBean(differenceInfo.get("exists"), dirTableList, true);
	  }

	  //      6-3: 在数据字典中找到新增的表,然后设置默认的字段信息
	  if (!differenceInfo.get("add").isEmpty()) {
		removeTableBean(differenceInfo.get("add"), dirTableList, false);
		setDirTableDefaultData(dirTableList, colSetId);
	  }

	  //      6-4: 将数据字典新增的表和数据库中的表集合进行合并
	  dirTableList.addAll(databaseTableList);

	} else {
	  // 判断数据字典的表是否存在
	  if (dirTableList == null || dirTableList.isEmpty()) {
		CheckParam.throwErrorMsg("数据字典与数据库中的表信息为空");
	  }
	  // 设置数据字典表默认数据信息
	  setDirTableDefaultData(dirTableList, colSetId);
	}

	//  7: 检查表是否被采集过
	//    dirTableList.forEach(
	//        table_info -> {
	//          List<Object> tableStateList = checkTableCollectState(colSetId, dirTableList);
	//
	//          if (tableStateList.contains(ExecuteState.YunXingWanCheng.getCode())
	//              || tableStateList.contains(ExecuteState.KaiShiYunXing.getCode())) {
	//            itemMap.put("collectState", false);
	//          } else {
	//            itemMap.put("collectState", true);
	//          }
	//        });

	// 8: 返回表的信息
	return dirTableList;
  }

  @Method(desc = "根据表ID获取列信息", logicStep = "获取列信息")
  @Param(name = "colSetId", desc = "采集任务ID", range = "不可为空")
  @Param(name = "table_id", desc = "表ID", range = "不可为空")
  @Return(desc = "返回表列数据信息", range = "不可为空,为空表示无列信息,那么在保存列的信息时就未做校验")
  public List<Table_column> getTableColumnByTableId(long colSetId, long table_id) {

	long countNum =
		Dbo.queryNumber(
			"SELECT COUNT(1) FROM " + Table_info.TableName + " WHERE database_id = ?", colSetId)
			.orElseThrow(() -> new BusinessException("SQL查询异常"));
	if (countNum == 0) {
	  CheckParam.throwErrorMsg("任务ID( %s )不存在采集表ID(%s)信息!!!", colSetId, table_id);
	}

	return Dbo.queryList(
		Table_column.class,
		"SELECT t1.* from "
			+ Table_column.TableName
			+ " t1 JOIN "
			+ Table_info.TableName
			+ " t2 ON t1.table_id = t2.table_id WHERE t2.database_id = ? AND t1.table_id = ?"
			+ " AND t2.valid_e_date = ? AND t1.valid_e_date = ? ",
		colSetId,
		table_id,
		Constant.MAXDATE,
		Constant.MAXDATE);
  }

  @Method(desc = "根据表名称获取列信息", logicStep = "获取列信息")
  @Param(name = "colSetId", desc = "采集任务ID", range = "不可为空")
  @Param(name = "table_name", desc = "表名称", range = "不可为空")
  @Return(desc = "返回表列数据信息", range = "可以为空,为空表示数据字典为配置列信息")
  public List<Table_column> getTableColumnByTableName(long colSetId, String table_name) {

	Map<String, List<Table_column>> dicTableAllColumnMap = getDicTableAllColumn(colSetId);

	if (dicTableAllColumnMap == null || dicTableAllColumnMap.isEmpty()) {
	  CheckParam.throwErrorMsg("数据字典中未找到表(%s)列信息", table_name);
	}

	return dicTableAllColumnMap.get(table_name);
  }

  @Method(desc = "根据数据字典表列信息", logicStep = "获取列信息")
  @Param(name = "colSetId", desc = "采集任务ID", range = "不可为空")
  @Return(desc = "返回数据字典的全部列信息", range = "为空表示该字典下午表的列信息存在")
  private Map<String, List<Table_column>> getDicTableAllColumn(long colSetId) {

	// 获取任务配置信息
	Map<String, Object> databaseInfo = getDatabaseSetInfo(colSetId);

	// 获得Agent处理后的数据信息
	String respMsg =
		SendMsgUtil.getAllTableName(
			(Long) databaseInfo.get("agent_id"),
			this.getUserId(),
			databaseInfo,
			AgentActionUtil.GETAlLLTABLECOLUMN);

	// 将数据转换,并返回
	return JSON.parseObject(respMsg, new TypeReference<Map<String, List<Table_column>>>() {
	});
  }

  @Method(desc = "根据表的ID更新列的信息", logicStep = "1: 检查当前表ID的列信息是否存在 " + "2: 更新表的列信息")
  @Param(name = "table_id", desc = "表的ID", range = "不可为空")
  @Param(name = "tableColumns", desc = "表的列信息集合", range = "不可为空", isBean = true)
  public void updateColumnByTableId(long table_id, Table_column[] tableColumns) {

	//    1: 检查当前表ID的列信息是否存在
	long countNum =
		Dbo.queryNumber(
			"SELECT COUNT(1) FROM " + Table_column.TableName + " WHERE table_id = ?", table_id)
			.orElseThrow(() -> new BusinessException("SQL查询异常"));

	if (countNum == 0) {
	  CheckParam.throwErrorMsg("根据表ID(%s),未找到列信息", table_id);
	}

	// 2: 更新表的列信息
	for (Table_column tableColumn : tableColumns) {
	  // 更新表的列信息
	  tableColumn.update(Dbo.db());
	}
  }

  @Method(
	  desc = "保存数据文件的表和列信息",
	  logicStep =
		  "1: 检查当前任务是否存在"
			  + "2: 获取数据字典表的全部列信息"
			  + "3: 如果没有table_id的主键,则表示为新增数据信息"
			  + "   3-1: 并在列数据中找出表的列信息,如果没有则表示没有触发列的保存.则使用默认的列信息"
			  + "   3-2: 如果是点击查看列进行了保存,则使用自定义的字段类型进行保存,并设置默认的数据参数"
			  + "4:  如果是点击查看列进行了保存,则进行列的比对检查是否有新增或者删除的字段信息")
  @Param(name = "colSetId", desc = "任务ID", range = "不可为空")
  @Param(name = "tableInfos", desc = "表的数组集合", range = "不可为空", isBean = true)
  @Param(
	  name = "tableColumns",
	  desc = "表的列集合对象字符串,如果列发生了选择必须有,否则可以为空",
	  range = "可为空...如果列发生了选择必须有,否则可以为空,如果为空则使用默认的...每个表名对应的列信息集合",
	  nullable = true,
	  example = "数据格式如 {tableName:[{}]}, tableName:表示为表名称,后面的值数组表示表的列信息集合")
  @Return(desc = "返回采集任务的ID", range = "返回采集任务的ID")
  public long saveTableData(long colSetId, Table_info[] tableInfos, String tableColumns) {

	// 1: 检查当前任务是否存在
	long countNum =
		Dbo.queryNumber(
			"SELECT COUNT(1) FROM " + Database_set.TableName + " WHERE database_id = ?",
			colSetId)
			.orElseThrow(() -> new BusinessException("SQL查询错误"));

	if (countNum == 0) {
	  CheckParam.throwErrorMsg("任务ID(%s),不存在", colSetId);
	}

	for (Table_info tableInfo : tableInfos) {
	  // 3: 如果没有table_id的主键,则表示为新增数据信息
	  if (tableInfo.getTable_id() == null) {
		// 设置新增数据的主键信息
		long table_id = PrimayKeyGener.getNextId();
		tableInfo.setTable_id(table_id);
		// 设置任务主键
		tableInfo.setDatabase_id(colSetId);
		// 设置默认的表清洗顺序
		tableInfo.setTi_or(DEFAULT_TABLE_CLEAN_ORDER.toJSONString());
		// 设置表的有效开始时间为当天入库的时间
		tableInfo.setValid_s_date(DateUtil.getSysDate());
		// 设置表的有效使用时间为 99991231
		tableInfo.setValid_e_date(Constant.MAXDATE);
		// 保存表信息
		tableInfo.add(Dbo.db());
		// 3-1: 并在列数据中找出表的列信息,如果没有则表示没有触发列的保存.则使用默认的列信息
		if (StringUtil.isBlank(tableColumns)) {
		  // 2: 获取数据字典表的全部列信息
		  Map<String, List<Table_column>> dicTableAllColumnMap = getDicTableAllColumn(colSetId);
		  setColumnDefaultData(
			  dicTableAllColumnMap.get(tableInfo.getTable_name()), table_id);
		} else {
		  // 3-2: 如果是点击查看列进行了保存,则使用自定义的字段类型进行保存,并设置默认的数据参数
		  Map<String, List<Table_column>> tableColumnList =
			  JSON.parseObject(
				  tableColumns, new TypeReference<Map<String, List<Table_column>>>() {
				  });
		  if (tableColumnList == null || tableColumnList.isEmpty()) {
			CheckParam.throwErrorMsg("为获取到表的列信息");
		  }
		  setColumnDefaultData(
			  tableColumnList.get(tableInfo.getTable_name()), table_id);
		}
	  } else {

		// 编辑触发了列选择情况
		if (StringUtil.isNotBlank(tableColumns)) {
		  // 如果是编辑点击查看列进行了保存,则进行列的比对检查是否有新增或者删除的字段信息
		  Map<String, List<Table_column>> tableColumnList =
			  JSON.parseObject(
				  tableColumns, new TypeReference<Map<String, List<Table_column>>>() {
				  });
		  if (tableColumnList != null && tableColumnList.size() != 0) {

			tableColumnList.forEach(
				(s, table_columns) -> {
				  // 循环列的集合信息,经行列的数据更新
				  table_columns.forEach(
					  table_column -> {
						if (table_column.getColumn_id() == null) {
						  // 设置列的主键
						  table_column.setColumn_id(PrimayKeyGener.getNextId());
						  // 设置列对应表的主键
						  table_column.setTable_id(tableInfo.getTable_id());
						  // 设置列的有效开始时间为当天入库的时间
						  table_column.setValid_s_date(DateUtil.getSysDate());
						  // 设置列的有效使用时间为 99991231
						  table_column.setValid_e_date(Constant.MAXDATE);
						  // 新增列信息
						  table_column.add(Dbo.db());
						} else {
						  // 这里先将有效的日期更新为当天日期,然后在根据还存在的列主键在更新回来,这样不存在的列信息就将自动变为无效的字段
						  Dbo.execute(
							  " UPDATE "
								  + Table_column.TableName
								  + " SET valid_e_date = ? where table_id = ? and column_id = ?",
							  DateUtil.getSysDate(),
							  table_column.getTable_id(),
							  table_column.getColumn_id());
						  Dbo.execute(
							  "UPDATE "
								  + Table_column.TableName
								  + " SET column_name = ?,colume_ch_name = ?,column_type = ?,is_primary_key = ?,"
								  + "is_get = ?,is_alive = ?,is_new = ?,valid_e_date = ? where table_id = ? and column_id = ?",
							  table_column.getColumn_name(),
							  table_column.getColumn_ch_name(),
							  table_column.getColumn_type(),
							  table_column.getIs_primary_key(),
							  table_column.getIs_get(),
							  table_column.getIs_alive(),
							  table_column.getIs_new(),
							  Constant.MAXDATE,
							  table_column.getTable_id(),
							  table_column.getColumn_id());
						}
					  });
				});
		  }
		}
	  }
	}

	//    2: 获取任务下的表信息
	List<String> databaseTableList =
		getTableInfo(colSetId).stream().map(Table_info::getTable_name).collect(Collectors.toList());

	//    3: 获取数据字典的表信息
	List<String> dirTableList =
		getDirTableData(colSetId).stream()
			.map(Table_info::getTable_name)
			.collect(Collectors.toList());

	//      5: 判断数据字典和数据库中的表进行对比,找出被删除的表
	Map<String, List<String>> differenceInfo = getDifferenceInfo(dirTableList, databaseTableList);

	// 获取删除的表信息
	deleteTableInfoByTableName(colSetId, differenceInfo.get("delete"));

	return colSetId;
  }

  @Method(desc = "设置列信息的默认值", logicStep = "设置列信息的默认值")
  @Param(name = "tableColumnList", desc = "列的实体对象数据集合", range = "不可为空", isBean = true)
  @Param(name = "table_id", desc = "表的主键ID", range = "不可为空")
  private void setColumnDefaultData(List<Table_column> tableColumnList, long table_id) {

	tableColumnList.forEach(
		table_column -> {
		  // 设置列对应外键(表ID)
		  table_column.setTable_id(table_id);
		  // 设置表的列主键
		  table_column.setColumn_id(PrimayKeyGener.getNextId());
		  // 设置默认清洗顺序
		  table_column.setTc_or(DEFAULT_COLUMN_CLEAN_ORDER.toJSONString());
		  // 设置表的有效开始日期为当天
		  table_column.setValid_s_date(DateUtil.getSysDate());
		  // 设置列的有效结束日期为 99991231
		  table_column.setValid_e_date(Constant.MAXDATE);
		  // 保存
		  table_column.add(Dbo.db());
		});
  }

  @Method(desc = "根据colSetId去数据库中查出DB连接信息", logicStep = "1、根据colSetId和userId去数据库中查出DB连接信息")
  @Param(name = "colSetId", desc = "采集任务ID", range = "不为空")
  @Return(desc = "查询结果集", range = "不为空")
  private List<Table_info> getDirTableData(long colSetId) {

	Map<String, Object> databaseInfo = getDatabaseSetInfo(colSetId);

	String respMsg =
		SendMsgUtil.getAllTableName(
			(long) databaseInfo.get("agent_id"),
			getUserId(),
			databaseInfo,
			AgentActionUtil.GETDATABASETABLE);

	return JSON.parseObject(respMsg, new TypeReference<List<Table_info>>() {
	});
  }

  @Param(name = "dirTableList", desc = "数据字典的表信息集合", range = "不为空", isBean = true)
  @Param(name = "colSetId", desc = "采集任务ID", range = "不为空")
  private void setDirTableDefaultData(List<Table_info> dirTableList, long colSetId) {
	dirTableList.forEach(
		(table_info) -> {
		  table_info.setDatabase_id(colSetId);
		  table_info.setRec_num_date(DateUtil.getSysDate());
		  table_info.setValid_s_date(DateUtil.getSysDate());
		  table_info.setIs_md5(IsFlag.Fou.getCode());
		  table_info.setIs_register(IsFlag.Shi.getCode());
		  table_info.setIs_customize_sql(IsFlag.Fou.getCode());
		  table_info.setIs_parallel(IsFlag.Fou.getCode());
		  table_info.setIs_user_defined(IsFlag.Fou.getCode());
		});
  }

  @Method(desc = "根据colSetId去数据库中查出DB连接信息", logicStep = "1、根据colSetId和userId去数据库中查出DB连接信息")
  @Param(name = "colSetId", desc = "采集任务ID", range = "不为空")
  @Return(desc = "查询结果集", range = "不为空")
  private Map<String, Object> getDatabaseSetInfo(long colSetId) {

	long databaseNum =
		Dbo.queryNumber(
			"SELECT COUNT(1) FROM " + Database_set.TableName + " WHERE database_id = ?",
			colSetId)
			.orElseThrow(() -> new BusinessException("SQL查询异常"));
	if (databaseNum == 0) {
	  CheckParam.throwErrorMsg("任务(%s)不存在!!!", colSetId);
	}
	// 1、根据colSetId和userId去数据库中查出DB连接信息
	return Dbo.queryOneObject(
		" select t1.database_type, t1.database_ip, t1.database_port, t1.database_name, "
			+ " t1.database_pad, t1.user_name, t1.database_drive, t1.jdbc_url, t1.agent_id, t1.db_agent, t1.plane_url"
			+ " from "
			+ Database_set.TableName
			+ " t1"
			+ " left join "
			+ Agent_info.TableName
			+ " ai on ai.agent_id = t1.agent_id"
			+ " where t1.database_id = ? and ai.user_id = ? ",
		colSetId,
		getUserId());
  }

  @Method(desc = "获取集合Bean中的表名称", logicStep = "获取表名称")
  @Param(name = "tableBeanList", desc = "集合Table_info数据集合", range = "可以为空")
  @Return(desc = "返回处理后的数据信息集合,只要表的名称", range = "可以为空")
  private List<String> getTableName(List<Table_info> tableBeanList) {
	List<String> tableNameList = new ArrayList<>();
	tableBeanList.forEach(
		table_info -> {
		  tableNameList.add(table_info.getTable_name());
		});
	return tableNameList;
  }

  @Method(desc = "找出数据库和数据字典的差异表信息", logicStep = "获取表名称")
  @Param(name = "dicTableList", desc = "数据字典表集合信息", range = "可以为空")
  @Param(name = "databaseTableNames", desc = "数据库表集合信息", range = "可以为空")
  @Return(desc = "返回还存在和已删除的表信息", range = "可以为空")
  private Map<String, List<String>> getDifferenceInfo(
	  List<String> dicTableList, List<String> databaseTableNames) {

	logger.info("数据字典的 " + dicTableList);
	logger.info("数据库的 " + databaseTableNames);
	List<String> exists = new ArrayList<String>(); // 存在的信息
	List<String> delete = new ArrayList<String>(); // 不存在的信息
	Map<String, List<String>> differenceMap = new HashedMap();
	for (String databaseTableName : databaseTableNames) {
	  /*
	   * 如果数据字典中包含数据库中的表,则检查表字段信息是否被更改
	   * 然后将其删除掉进行后面的检查
	   */
	  if (dicTableList.contains(databaseTableName)) {
		exists.add(databaseTableName);
		dicTableList.remove(databaseTableName);
	  } else {
		delete.add(databaseTableName);
	  }
	}

	logger.info("数据字典存在的===>" + exists);
	differenceMap.put("exists", exists);
	logger.info("数据字典删除的===>" + delete);
	differenceMap.put("delete", delete);
	logger.info("数据字典新增的===>" + dicTableList);
	differenceMap.put("add", dicTableList);
	return differenceMap;
  }

  @Method(desc = "根据不同情况删除表的信息", logicStep = "删除表信息")
  @Param(name = "tableNameList", desc = "被依据的表名称集合", range = "可以为空")
  @Param(name = "tableList", desc = "需要处理的表集合", range = "可以为空")
  @Param(name = "deleteType", desc = "区分是数据库的表集合处理还是数据字典的表集合处理", range = "可以为空")
  private void removeTableBean(
	  List<String> tableNameList, List<Table_info> tableList, boolean deleteType) {

	Iterator<Table_info> iterator = tableList.iterator();

	while (iterator.hasNext()) {
	  Table_info table_info = iterator.next();
	  if (deleteType) {
		// 如果删除的表名称和数据库中记录的表名一样,则删除(数据库的)
		if (tableNameList.contains(table_info.getTable_name())) {
		  iterator.remove();
		}
	  } else {
		// 删除还存在的表信息,保留新增的表信息(数据字典的)
		if (!tableNameList.contains(table_info.getTable_name())) {
		  iterator.remove();
		}
	  }
	}
  }

  @Method(desc = "根据任务ID获取任务下的表信息", logicStep = "1: 检查当前任务是否存在" + "2: 获取表信息")
  @Param(name = "colSetId", desc = "任务ID", range = "不可为空")
  @Return(desc = "返回任务下的表信息", range = "可以为空,为空表示没有表信息存在")
  private List<Table_info> getTableInfo(long colSetId) {

	//    1: 检查当前任务是否存在
	long countNum =
		Dbo.queryNumber(
			"SELECT COUNT(1) FROM " + Database_set.TableName + " WHERE database_id = ?",
			colSetId)
			.orElseThrow(() -> new BusinessException("SQL查询错误"));
	if (countNum == 0) {
	  CheckParam.throwErrorMsg("当前任务(%s),不存在!!!");
	}

	//    2: 获取表信息.
	return Dbo.queryList(
		Table_info.class,
		"SELECT * FROM " + Table_info.TableName + " WHERE database_id = ? AND valid_e_date = ?",
		colSetId,
		Constant.MAXDATE);

	//    tableList.forEach(
	//        itemMap -> {
	//          List<Object> tableStateList =
	//              checkTableCollectState(colSetId, itemMap.get("table_name").toString());
	//
	//          if (tableStateList.contains(ExecuteState.YunXingWanCheng.getCode())
	//              || tableStateList.contains(ExecuteState.KaiShiYunXing.getCode())) {
	//            itemMap.put("collectState", false);
	//          } else {
	//            itemMap.put("collectState", true);
	//          }
	//        });
	//    return tableList;
  }

  @Method(
	  desc = "删除已经不存在的表信息,不考虑删除的条数",
	  logicStep =
		  ""
			  + "1: 先获取表的ID(table_id)信息,在删除表信息(table_info)"
			  + "2: 先获取列的ID(column_id)信息,列信息(table_column)"
			  + "3: 列拆分(column_split)"
			  + "4: 列清洗(column_clean)"
			  + "5: 数据抽取定义(data_extraction_def)"
			  + "6: 表存储信息(table_storage_info)"
			  + "7: 表清洗参数信息(table_clean)"
			  + "8: 列合并信息表(column_merge)")
  @Param(name = "colSetId", desc = "任务ID", range = "不可为空")
  @Param(name = "deleteTableName", desc = "要删除的表集合信息", range = "可为空")
  private void deleteTableInfoByTableName(long colSetId, List<String> deleteTableName) {

	if (deleteTableName == null || deleteTableName.size() == 0) {
	  return;
	}

	// 获取要删除的表的ID
	Assembler assembler =
		Assembler.newInstance()
			.addSql(
				"SELECT table_id FROM table_info t1 JOIN database_set t2 ON t1.database_id = t2.database_id WHERE t1.database_id = ?")
			.addParam(colSetId)
			.addORParam("table_name", deleteTableName.toArray(new String[deleteTableName.size()]));

	List<Object> deleteTableId = Dbo.queryOneColumnList(assembler.sql(), assembler.params());

	// 查询要删除的表对应列ID
	assembler.clean();
	assembler =
		Assembler.newInstance()
			.addSql(
				"SELECT t2.column_id FROM "
					+ Table_info.TableName
					+ " t1 LEFT JOIN "
					+ Table_column.TableName
					+ " t2 ON t1.table_id = t2.table_id "
					+ "WHERE t1.database_id = ?")
			.addParam(colSetId)
			.addORParam("t1.table_id", deleteTableId.toArray(new Object[deleteTableId.size()]));
	List<Object> deleteTableColumnId = Dbo.queryOneColumnList(assembler.sql(), assembler.params());

	// 1: 先获取表的ID(table_id)信息,在删除表信息(table_info)
	assembler.clean();
	assembler
		.addSql("DELETE FROM " + Table_info.TableName)
		.addORParam("table_id", deleteTableId.toArray(new Object[deleteTableId.size()]), "WHERE");
	execute(assembler.sql(), assembler.params());

	// 2: 先获取列的ID(column_id)信息,列信息(table_column)
	assembler.clean();
	assembler
		.addSql("DELETE FROM " + Table_column.TableName)
		.addORParam("column_id", deleteTableId.toArray(new Object[deleteTableId.size()]), "WHERE");
	execute(assembler.sql(), assembler.params());
	// 3: 列拆分(column_split)
	assembler.clean();
	assembler
		.addSql("DELETE FROM " + Column_split.TableName)
		.addORParam(
			"column_id", deleteTableColumnId.toArray(new Object[deleteTableId.size()]), "WHERE");
	execute(assembler.sql(), assembler.params());

	// 4: 列清洗(column_clean)
	assembler.clean();
	assembler
		.addSql("DELETE FROM " + Column_clean.TableName)
		.addORParam(
			"column_id", deleteTableColumnId.toArray(new Object[deleteTableId.size()]), "WHERE");
	execute(assembler.sql(), assembler.params());

	// 5: 数据抽取定义(data_extraction_def)
	assembler.clean();
	assembler
		.addSql("DELETE FROM " + Data_extraction_def.TableName)
		.addORParam(
			"table_id", deleteTableColumnId.toArray(new Object[deleteTableId.size()]), "WHERE");
	execute(assembler.sql(), assembler.params());

	// 6: 表存储信息(table_storage_info)
	assembler.clean();
	assembler
		.addSql("DELETE FROM " + Table_storage_info.TableName)
		.addORParam(
			"table_id", deleteTableColumnId.toArray(new Object[deleteTableId.size()]), "WHERE");
	execute(assembler.sql(), assembler.params());

	// 7: 表清洗参数信息(table_clean)
	assembler.clean();
	assembler
		.addSql("DELETE FROM " + Table_clean.TableName)
		.addORParam(
			"table_id", deleteTableColumnId.toArray(new Object[deleteTableId.size()]), "WHERE");
	execute(assembler.sql(), assembler.params());

	// 8: 列合并信息表(column_merge)")
	assembler.clean();
	assembler
		.addSql("DELETE FROM " + Column_merge.TableName)
		.addORParam(
			"table_id", deleteTableColumnId.toArray(new Object[deleteTableId.size()]), "WHERE");
	execute(assembler.sql(), assembler.params());
  }

  @Method(desc = "执行SQL操作", logicStep = "执行SQL操作")
  @Param(name = "sql", desc = "需要执行SQL", range = "不可为空")
  @Param(name = "parasList", desc = "需要执行SQL", range = "不可为空")
  private void execute(String sql, List<Object> parasList) {
	Dbo.execute(sql, parasList);
  }

  @Method(desc = "检查表的采集状态信息", logicStep = "1: 检查表名是否存在 2: 返回表的采集信息集合")
  @Param(name = "colSetId", desc = "采集任务ID", range = "不可为空")
  @Param(name = "table_name", desc = "表名称", range = "不可为空")
  @Return(desc = "返回表的采集状态集合", range = "可以为空,为空表示表未采集过")
  private List<Object> checkTableCollectState(long colSetId, String table_name) {
	//    1: 检查表名是否存在
	long checkTableNum =
		Dbo.queryNumber(
			"SELECT COUNT(1) FROM "
				+ Table_info.TableName
				+ " WHERE database_id = ? AND table_name = ?",
			colSetId,
			table_name)
			.orElseThrow(() -> new BusinessException("SQL查询错误"));

	if (checkTableNum == 0) {
	  throw new BusinessException("任务(" + colSetId + ")下不存在表 (" + table_name + ")");
	}
	//    2: 返回表的采集信息集合
	return Dbo.queryOneColumnList(
		"SELECT t1.execute_state FROM "
			+ Collect_case.TableName
			+ " t1 JOIN "
			+ Table_info.TableName
			+ " t2 ON t1.collect_set_id = t2.database_id "
			+ "AND t1.task_classify = t2.table_name  WHERE t1.collect_set_id = ? AND t2.table_name = ?",
		colSetId,
		table_name);
  }
}
