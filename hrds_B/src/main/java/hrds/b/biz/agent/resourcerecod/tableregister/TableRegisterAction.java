package hrds.b.biz.agent.resourcerecod.tableregister;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.CheckParam;
import hrds.b.biz.agent.tools.SendMsgUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.JobExecuteState;
import hrds.commons.codes.StorageType;
import hrds.commons.codes.StoreLayerDataSource;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Data_store_reg;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Dtab_relation_store;
import hrds.commons.entity.Table_column;
import hrds.commons.entity.Table_info;
import hrds.commons.entity.Table_storage_info;
import hrds.commons.entity.fdentity.ProjectTableEntity.EntityDealZeroException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.AgentActionUtil;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@DocClass(desc = "表的登记信息管理", author = "Mr.Lee", createdate = "2020-07-07 11:04")
public class TableRegisterAction extends BaseAction {

	@Method(desc = "保存选择的表信息", logicStep = ""
		+ "1: 检查认为的信息是否存在 "
		+ "2: 如果自定义的列信息不为空,将表对应的列信息解析出来 "
		+ "3: 循环集合校验不可为空字段信息 "
		+ "4: 如果存在自定义列信息则保存自定义列信息,反之保存表的默认字段信息"
		+ "5; 保存表关联的储存层数据信息"
	)
	@Param(name = "databaseId", desc = "采集任务ID", range = "不可为空")
	@Param(name = "source_id", desc = "数据源ID", range = "不可为空")
	@Param(name = "agent_id", desc = "Agent ID", range = "不可为空")
	@Param(name = "tableInfos", desc = "采集表信息集合", range = "不可为空", isBean = true)
	@Param(name = "tableColumns", desc = "表对应的列字段信息,格式如: {表名称:[{列信息1},{列信息2},......]}", range = "不可为空", nullable = true)
	@Param(name = "dsl_id", desc = "表对应储存层ID", range = "不可为空")
	public void saveTableData(long source_id, long agent_id, long databaseId, Table_info[] tableInfos, String tableColumns,
		long dsl_id) {

		//1: 检查认为的信息是否存在
		checkDatabaseSetExist(databaseId);
		//2: 如果自定义的列信息不为空,将表对应的列信息解析出来,反之通过Agent获取数据库连接下的全部表字段列信息
		JSONObject tableColumnObj = null;
		if (StringUtil.isNotBlank(tableColumns)) {
			tableColumnObj = JSON.parseObject(tableColumns);
		}

		//3: 循环集合校验不可为空字段信息
		for (Table_info tableInfo : tableInfos) {
			//检查表名及中文名
			saveTableInfo(databaseId, tableInfo);
			//4: 如果存在自定义列信息则保存自定义列信息,反之保存表的默认字段信息
			List<Table_column> tableColumnList = null;
			if (tableColumnObj != null && tableColumnObj.containsKey(tableInfo.getTable_name())) {
				if (tableColumnObj.get(tableInfo.getTable_name()) == null) {
					CheckParam.throwErrorMsg("表名称(%s)未设置列信息", tableInfo.getTable_name());
				}
				tableColumnList = JSON
					.parseObject(tableColumnObj.get(tableInfo.getTable_name()).toString(),
						new TypeReference<List<Table_column>>() {
						});
			} else {
				tableColumnList = databaseTableColumnInfo(databaseId, tableInfo.getTable_name());
			}
			//保存表的列信息
			setTableColumnInfo(tableInfo.getTable_id(), tableColumnList);
			//保存表关联的储存层数据信息
			saveStorageData(source_id, agent_id, databaseId, dsl_id, tableInfo, true);

		}
		//修改此次任务的状态信息
		DboExecute
			.updatesOrThrow("更新的数据超出了范围", "UPDATE " + Database_set.TableName + " SET is_sendok = ? WHERE database_id = ?",
				IsFlag.Shi.getCode(),
				databaseId);
	}

	@Method(desc = "获取全表的信息", logicStep = ""
		+ "1: 检查当前任务是否存在 "
		+ "2: 查询任务存在的表信息,返回表集合信息")
	@Param(name = "databaseId", desc = "采集任务的ID", range = "不可为空")
	@Return(desc = "返回当前任务存储层链接下的表信息", range = "为空表示没有该表信息")
	public List<Map<String, Object>> getTableData(long databaseId) {

		//1: 检查当前任务是否存在
		checkDatabaseSetExist(databaseId);

		//2: 查询任务存在的表信息,返回表集合信息
		return Dbo
			.queryList("SELECT * FROM " + Table_info.TableName + " WHERE database_id = ? ",
				databaseId);

	}

	private void checkDatabaseSetExist(long databaseId) {
		//1: 检查当前任务是否存在
		long countNum = Dbo
			.queryNumber("SELECT COUNT(1) FROM " + Database_set.TableName + " WHERE database_id = ? AND is_reg = ?",
				databaseId, IsFlag.Shi.getCode())
			.orElseThrow(() -> new BusinessException("SQL查询异常"));
		if (countNum == 0) {
			CheckParam.throwErrorMsg("任务ID(%s)不存在", databaseId);
		}
	}

	@Method(desc = "", logicStep = ""
		+ "1: 保存表存储信息"
		+ "2: 保存表的储存关系信息"
		+ "3: 记录数据表的存储登记")
	private void saveStorageData(long source_id, long agent_id, long databaseId, long dsl_id, Table_info tableInfo,
		boolean isCheckTableName) {

		long countNum = Dbo.queryNumber("SELECT COUNT(1) FROM " + Data_store_reg.TableName
				+ " WHERE hyren_name = ? AND agent_id = ? AND source_id = ? AND database_id = ?", tableInfo.getTable_name(),
			agent_id, source_id, databaseId)
			.orElseThrow(() -> new BusinessException("SQL查询异常"));
		if (countNum == 0) {
			//5: 保存表存储信息
			Table_storage_info table_storage_info = new Table_storage_info();
			Long storage_id = PrimayKeyGener.getNextId();
			table_storage_info.setStorage_id(storage_id);
			table_storage_info.setFile_format(FileFormat.CSV.getCode());
			table_storage_info.setStorage_type(StorageType.ZhuiJia.getCode());
			table_storage_info.setIs_zipper(IsFlag.Fou.getCode());
			table_storage_info.setStorage_time("1");
			table_storage_info.setHyren_name(tableInfo.getTable_name());
			table_storage_info.setTable_id(tableInfo.getTable_id());
			table_storage_info.add(Dbo.db());
			//6: 保存表的储存关系信息
			Dtab_relation_store dtab_relation_store = new Dtab_relation_store();
			dtab_relation_store.setDsl_id(dsl_id);
			dtab_relation_store.setTab_id(storage_id);
			dtab_relation_store.setData_source(StoreLayerDataSource.DBA.getCode());
			dtab_relation_store.setIs_successful(JobExecuteState.WanCheng.getCode());
			dtab_relation_store.add(Dbo.db());
			//7: 记录数据表的存储登记,并检查表名是否已经
			Data_store_reg data_store_reg = new Data_store_reg();
			data_store_reg.setFile_id(UUID.randomUUID().toString());
			data_store_reg.setCollect_type(AgentType.ShuJuKu.getCode());
			data_store_reg.setOriginal_update_date(DateUtil.getSysDate());
			data_store_reg.setOriginal_update_time(DateUtil.getSysTime());
			data_store_reg.setOriginal_name(tableInfo.getTable_name());
			data_store_reg.setTable_name(tableInfo.getTable_name());
			data_store_reg.setHyren_name(tableInfo.getTable_name());
			data_store_reg.setStorage_date(DateUtil.getSysDate());
			data_store_reg.setStorage_time(DateUtil.getSysTime());
			data_store_reg.setFile_size(0L);
			data_store_reg.setAgent_id(agent_id);
			data_store_reg.setDatabase_id(databaseId);
			data_store_reg.setSource_id(source_id);
			data_store_reg.setTable_id(tableInfo.getTable_id());
			data_store_reg.add(Dbo.db());
		}
	}

	@Method(desc = "检查表的设置信息", logicStep = "1: 检查表的名称及中文名称")
	@Param(name = "table_info", desc = "表数据信息", range = "不可为空", isBean = true)
	void checklistInformation(Table_info table_info) {
		Validator.notBlank(table_info.getTable_name(), "表名称不能为空");
		Validator.notBlank(table_info.getTable_ch_name(), String.format("表(%s)中文名称不能为空", table_info.getTable_name()));

	}

	@Method(desc = "根据databaseId去数据库中查出DB连接信息", logicStep = "1、根据databaseId和userId去数据库中查出DB连接信息")
	@Param(name = "databaseId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	private List<Table_column> databaseTableColumnInfo(long databaseId, String table_name) {

		long databaseNum =
			Dbo.queryNumber(
				"SELECT COUNT(1) FROM " + Database_set.TableName + " WHERE database_id = ?",
				databaseId)
				.orElseThrow(() -> new BusinessException("SQL查询异常"));
		if (databaseNum == 0) {
			throw new BusinessException("任务(" + databaseId + ")不存在!!!");
		}
		// 1、根据colSetId和userId去数据库中查出DB连接信息
		Map<String, Object> databaseSetInfo = Dbo.queryOneObject(
			" select t1.database_type, t1.database_ip, t1.database_port, t1.database_name, "
				+ " t1.database_pad, t1.user_name, t1.database_drive, t1.jdbc_url, t1.agent_id, t1.db_agent, t1.plane_url"
				+ " from "
				+ Database_set.TableName
				+ " t1 "
				+ " join "
				+ Agent_info.TableName
				+ " ai on ai.agent_id = t1.agent_id"
				+ " where t1.database_id = ? and ai.user_id = ? ",
			databaseId,
			getUserId());
		long agent_id = Long.parseLong(databaseSetInfo.get("agent_id").toString());
		// 2、封装数据，调用方法和agent交互，获取列信息
		String respMsg =
			SendMsgUtil.getColInfoByTbName(
				agent_id, getUserId(), databaseSetInfo, table_name, AgentActionUtil.GETTABLECOLUMN);
		// 3、将列信息反序列化为Json数组
		return JSON.parseObject(respMsg, new TypeReference<List<Table_column>>() {
		});
	}

	@Method(desc = "保存数据表的列信息", logicStep = "1: 设置表字段的默认字段")
	@Param(name = "table_id", desc = "表的ID", range = "不可为空")
	private void setTableColumnInfo(long table_id, List<Table_column> tableColumnList) {
		tableColumnList.forEach(table_column -> {
			table_column.setColumn_id(PrimayKeyGener.getNextId());
			table_column.setTable_id(table_id);
			table_column.setIs_get(IsFlag.Shi.getCode());
			table_column.setValid_s_date(DateUtil.getSysDate());
			table_column.setValid_e_date(Constant.MAXDATE);
			table_column.setIs_alive(IsFlag.Shi.getCode());
			table_column.setIs_new(IsFlag.Fou.getCode());
			table_column.setTc_or(Constant.DEFAULT_COLUMN_CLEAN_ORDER.toJSONString());
			table_column.add(Dbo.db());
		});
	}


	@Method(desc = "保存编辑选择的表信息", logicStep = ""
		+ "1: 检查认为的信息是否存在 "
		+ "2: 如果自定义的列信息不为空,将表对应的列信息解析出来 "
		+ "3: 循环集合校验不可为空字段信息 "
		+ "4: 保存表的信息")
	@Param(name = "databaseId", desc = "采集任务ID", range = "不可为空")
	@Param(name = "tableInfos", desc = "采集表信息集合", range = "不可为空", isBean = true)
	@Param(name = "source_id", desc = "数据源ID", range = "不可为空")
	@Param(name = "agent_id", desc = "Agent ID", range = "不可为空")
	@Param(name = "dsl_id", desc = "表对应储存层ID", range = "不可为空")
	@Param(name = "tableColumns", desc = "表对应的列字段信息", range = "不可为空", nullable = true)
	public void updateTableData(long source_id, long agent_id, long databaseId, long dsl_id, Table_info[] tableInfos,
		String tableColumns) {

		Dbo.execute("DELETE FROM " + Table_info.TableName + " WHERE database_id = ?", databaseId);

		//1: 检查认为的信息是否存在
		checkDatabaseSetExist(databaseId);
		//2: 如果自定义的列信息不为空,将表对应的列信息解析出来,反之通过Agent获取数据库连接下的全部表字段列信息
		JSONObject tableColumnObj = null;
		if (StringUtil.isBlank(tableColumns)) {
			tableColumnObj = JSON.parseObject(tableColumns);
		}

		//3: 循环集合校验不可为空字段信息
		for (Table_info tableInfo : tableInfos) {
			//检查表名及中文名
			checklistInformation(tableInfo);
			if (tableInfo.getTable_id() == null) {
				saveTableInfo(databaseId, tableInfo);
				//如果存在自定义列信息则保存自定义列信息,反之保存表的默认字段信息
				List<Table_column> tableColumnList = null;
				if (tableColumnObj != null && tableColumnObj.containsKey(tableInfo.getTable_name())) {
					if (tableColumnObj.get(tableInfo.getTable_name()) != null) {
						CheckParam.throwErrorMsg("表名称(%s)未设置列信息", tableInfo.getTable_name());
					}
					tableColumnList = JSON
						.parseObject(tableColumnObj.get(tableInfo.getTable_name()).toString(),
							new TypeReference<List<Table_column>>() {
							});
				} else {
					tableColumnList = databaseTableColumnInfo(databaseId, tableInfo.getTable_name());
				}
				setTableColumnInfo(tableInfo.getTable_id(), tableColumnList);
				//保存表关联的储存层数据信息
				saveStorageData(source_id, agent_id, databaseId, dsl_id, tableInfo, false);
			} else {
				try {
					tableInfo.update(Dbo.db());
				} catch (Exception e) {
					if (!(e instanceof EntityDealZeroException)) {
						throw new BusinessException(e.getMessage());
					}
				}
			}
		}

	}

	private void saveTableInfo(long databaseId, Table_info tableInfo) {
		//检查表名及中文名
		checklistInformation(tableInfo);
		//设置表的主键信息
		tableInfo.setTable_id(PrimayKeyGener.getNextId());
		//设置任务ID
		tableInfo.setDatabase_id(databaseId);
		tableInfo.setValid_s_date(DateUtil.getSysDate());
		tableInfo.setValid_e_date(Constant.MAXDATE);
		tableInfo.setIs_md5(IsFlag.Fou.getCode());
		tableInfo.setIs_register(IsFlag.Fou.getCode());
		tableInfo.setIs_customize_sql(IsFlag.Fou.getCode());
		tableInfo.setIs_parallel(IsFlag.Fou.getCode());
		tableInfo.setIs_user_defined(IsFlag.Fou.getCode());
		tableInfo.setTi_or(Constant.DEFAULT_TABLE_CLEAN_ORDER.toJSONString());
		tableInfo.setRec_num_date(DateUtil.getSysDate());
		//4: 保存表的信息
		tableInfo.add(Dbo.db());
	}
}
