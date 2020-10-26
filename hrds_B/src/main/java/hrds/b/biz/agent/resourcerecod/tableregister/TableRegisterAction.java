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
import hrds.commons.codes.CollectType;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.DataExtractType;
import hrds.commons.codes.FileFormat;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.JobExecuteState;
import hrds.commons.codes.StorageType;
import hrds.commons.codes.StoreLayerDataSource;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_job_classify;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.entity.Data_source;
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
		Object collect_type = checkDatabaseSetExist(databaseId);
		//2: 如果自定义的列信息不为空,将表对应的列信息解析出来,反之通过Agent获取数据库连接下的全部表字段列信息
		JSONObject tableColumnObj = null;
		if (StringUtil.isNotBlank(tableColumns)) {
			tableColumnObj = JSON.parseObject(tableColumns);
		}

		//3: 循环集合校验不可为空字段信息
		for (Table_info tableInfo : tableInfos) {
			//检查表名及中文名
			saveTableInfo(databaseId, tableInfo, collect_type);
			//4: 如果存在自定义列信息则保存自定义列信息,反之保存表的默认字段信息
			List<Table_column> tableColumnList;
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
			setTableColumnInfo(tableInfo.getTable_id(), tableInfo.getTable_name(), tableColumnList);
			//保存表关联的储存层数据信息
			if (!collect_type.equals(CollectType.ShuJuKuCaiJi.getCode())) {
				saveStorageData(source_id, agent_id, databaseId, dsl_id, tableInfo);
			}
		}
		//修改此次任务的状态信息
		if (!collect_type.equals(CollectType.ShuJuKuCaiJi.getCode())) {
			DboExecute
				.updatesOrThrow("更新的数据超出了范围",
					"UPDATE " + Database_set.TableName + " SET is_sendok = ? WHERE database_id = ?",
					IsFlag.Shi.getCode(),
					databaseId);
		}
	}

	@Method(desc = "获取全表的信息", logicStep = ""
		+ "1: 检查当前任务是否存在 "
		+ "2: 查询任务存在的表信息,返回表集合信息")
	@Param(name = "databaseId", desc = "采集任务的ID", range = "不可为空")
	@Return(desc = "返回当前任务存储层链接下的表信息", range = "为空表示没有该表信息")
	public List<Table_info> getTableData(long databaseId) {

		//1: 检查当前任务是否存在
		checkDatabaseSetExist(databaseId);

		//2: 查询任务存在的表信息,返回表集合信息
		return Dbo
			.queryList(Table_info.class, "SELECT * FROM " + Table_info.TableName + " WHERE database_id = ? ",
				databaseId);

	}

	private Object checkDatabaseSetExist(long databaseId) {
		//1: 检查当前任务是否存在
		long countNum = Dbo
			.queryNumber("SELECT COUNT(1) FROM " + Database_set.TableName + " WHERE database_id = ?",
				databaseId)
			.orElseThrow(() -> new BusinessException("SQL查询异常"));
		if (countNum == 0) {
			CheckParam.throwErrorMsg("任务ID(%s)不存在", databaseId);
		}

		return Dbo.queryOneObject("SELECT collect_type FROM " + Database_set.TableName + " WHERE database_id = ?",
			databaseId).get("collect_type");
	}

	@Method(desc = "", logicStep = ""
		+ "1: 保存表存储信息"
		+ "2: 保存表的储存关系信息"
		+ "3: 记录数据表的存储登记")
	private void saveStorageData(long source_id, long agent_id, long databaseId, long dsl_id, Table_info tableInfo) {

		//获取任务的分类和数据源编号信息
		Map<String, Object> classifyAndSourceNum = getClassifyAndSourceNum(databaseId);
		String hyren_name =
			String
				.format("%s_%s_%s",
					classifyAndSourceNum.get("datasource_number"), classifyAndSourceNum.get("classify_num"),
					tableInfo.getTable_name());
		long countNum = Dbo
			.queryNumber("SELECT COUNT(1) FROM " + Data_store_reg.TableName + " WHERE hyren_name = ? AND database_id = ?",
				hyren_name, databaseId).orElseThrow(() -> new BusinessException("SQL查询异常"));
		if (countNum != 0) {
			CheckParam.throwErrorMsg("数据源(%s),分类(%s)下已存在当前表(%s)", classifyAndSourceNum.get("datasource_number"),
				classifyAndSourceNum.get("classify_num"),
				tableInfo.getTable_name());
		}
		//5: 保存表存储信息
		Table_storage_info table_storage_info = new Table_storage_info();
		Long storage_id = PrimayKeyGener.getNextId();
		table_storage_info.setStorage_id(storage_id);
		table_storage_info.setFile_format(FileFormat.CSV.getCode());
		table_storage_info.setStorage_type(StorageType.ZhuiJia.getCode());
		table_storage_info.setIs_zipper(IsFlag.Fou.getCode());
		table_storage_info.setStorage_time("1");
		table_storage_info.setHyren_name(hyren_name);
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
		data_store_reg.setHyren_name(hyren_name);
		data_store_reg.setStorage_date(DateUtil.getSysDate());
		data_store_reg.setStorage_time(DateUtil.getSysTime());
		data_store_reg.setFile_size(0L);
		data_store_reg.setAgent_id(agent_id);
		data_store_reg.setDatabase_id(databaseId);
		data_store_reg.setSource_id(source_id);
		data_store_reg.setTable_id(tableInfo.getTable_id());
		data_store_reg.add(Dbo.db());
	}

	@Method(desc = "检查表的设置信息", logicStep = "1: 检查表的名称及中文名称")
	@Param(name = "table_info", desc = "表数据信息", range = "不可为空", isBean = true)
	void checklistInformation(Table_info table_info) {
		Validator.notBlank(table_info.getTable_name(), "表名称不能为空");
		Validator.notBlank(table_info.getTable_ch_name(), String.format("表(%s)中文名称不能为空", table_info.getTable_name()));

	}

	@Method(desc = "检查表的设置信息", logicStep = "1: 检查表的名称及中文名称")
	@Param(name = "table_info", desc = "表数据信息", range = "不可为空", isBean = true)
	void checkColumnInformation(Table_column table_column) {
		Validator.notBlank(table_column.getColumn_name(), "列名称不能为空");
		Validator
			.notBlank(table_column.getColumn_ch_name(), String.format("列(%s)中文名称不能为空", table_column.getColumn_name()));
		Validator
			.notBlank(table_column.getIs_primary_key(), String.format("列(%s)主键信息不能为空", table_column.getColumn_name()));

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
	private void setTableColumnInfo(long table_id, String table_name, List<Table_column> tableColumnList) {
		tableColumnList.forEach(table_column -> {
			Validator.notBlank(table_column.getColumn_name(), String.format("表(%s)的列名称未设置", table_name));
			Validator.notBlank(table_column.getColumn_ch_name(),
				String.format("表(%s)的列(%s)中文名称未设置", table_name, table_column.getColumn_name()));
			//设置默认数据信息
			setTableColumnDefaultData(table_id, table_column);
			//新增进去
			table_column.add(Dbo.db());
		});
	}

	void setTableColumnDefaultData(long table_id, Table_column table_column) {
		table_column.setColumn_id(PrimayKeyGener.getNextId());
		table_column.setTable_id(table_id);
		table_column.setValid_s_date(DateUtil.getSysDate());
		table_column.setValid_e_date(Constant.MAXDATE);
		table_column.setIs_alive(IsFlag.Shi.getCode());
		table_column.setIs_new(IsFlag.Fou.getCode());
		table_column.setTc_or(Constant.DEFAULT_COLUMN_CLEAN_ORDER.toJSONString());
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

		//1: 检查认为的信息是否存在
		Object collect_type = checkDatabaseSetExist(databaseId);

		if (!collect_type.equals(CollectType.ShuJuKuCaiJi.getCode())) {
			//删除数据表存储关系表
			Dbo.execute("DELETE FROM " + Dtab_relation_store.TableName + " WHERE tab_id in (SELECT storage_id FROM "
				+ Table_storage_info.TableName
				+ " WHERE table_id in (SELECT table_id FROM "
				+ Table_info.TableName
				+ " WHERE database_id = ?))", databaseId);
			//删除表存储信息
			Dbo.execute("DELETE FROM " + Table_storage_info.TableName + " WHERE table_id in (SELECT table_id FROM "
				+ Table_info.TableName + " WHERE database_id = ?)", databaseId);
		}
		//删除表信息
		Dbo.execute("DELETE FROM " + Table_info.TableName + " WHERE database_id = ?", databaseId);
		//数据存储登记信息
		Dbo.execute("DELETE FROM " + Data_store_reg.TableName + " WHERE database_id = ?", databaseId);
		//2: 如果自定义的列信息不为空,将表对应的列信息解析出来,反之通过Agent获取数据库连接下的全部表字段列信息
		JSONObject tableColumnObj = null;
		if (StringUtil.isNotBlank(tableColumns)) {
			tableColumnObj = JSON.parseObject(tableColumns);
		}

		//列集合信息
		List<Table_column> tableColumnList;

		//3: 循环集合校验不可为空字段信息
		for (Table_info tableInfo : tableInfos) {
			//检查表名及中文名
			checklistInformation(tableInfo);
			if (tableInfo.getTable_id() == null) {
				//如果存在自定义列信息则保存自定义列信息,反之保存表的默认字段信息
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
				//保存表信息
				saveTableInfo(databaseId, tableInfo, collect_type);
				//保存列信息
				setTableColumnInfo(tableInfo.getTable_id(), tableInfo.getTable_name(), tableColumnList);
				//保存表关联的储存层数据信息,如果不是数据采集在保存此信息(也就意味着是贴源登记)
				if (!collect_type.equals(CollectType.ShuJuKuCaiJi.getCode())) {
					saveStorageData(source_id, agent_id, databaseId, dsl_id, tableInfo);
				}

			} else {
				try {
					//保存表信息
					tableInfo.add(Dbo.db());
					//如果没有修改自定义的列信息,则不做列的变动
					if (tableColumnObj != null && tableColumnObj.containsKey(tableInfo.getTable_name())) {
						//直接解析列信息,如果有重新定义则能解析出新的,否则不做列的任何更改
						tableColumnList = JSON
							.parseObject(tableColumnObj.get(tableInfo.getTable_name()).toString(),
								new TypeReference<List<Table_column>>() {
								});
						updateTableColumn(tableInfo.getTable_id(), tableColumnList);
					}
					long countNum = Dbo
						.queryNumber("SELECT COUNT(1) FROM " + Data_extraction_def.TableName + " WHERE table_id = ?",
							tableInfo.getTable_id()).orElseThrow(() -> new BusinessException("SQL错误"));
					//如果是数据采集的表保存,则需要默认增加一个抽取定义(因为这里和贴源登记是公用的)
					if (countNum == 0) {
						Data_extraction_def extraction_def = new Data_extraction_def();
						extraction_def.setDed_id(PrimayKeyGener.getNextId());
						extraction_def.setTable_id(tableInfo.getTable_id());
						extraction_def.setData_extract_type(DataExtractType.YuanShuJuGeShi.getCode());
						extraction_def.setIs_header(IsFlag.Fou.getCode());
						extraction_def.setDatabase_code(DataBaseCode.UTF_8.getCode());
						extraction_def.setDbfile_format(FileFormat.PARQUET.getCode());
						extraction_def.setIs_archived(IsFlag.Fou.getCode());

						extraction_def.add(Dbo.db());
					}
					if (!collect_type.equals(CollectType.ShuJuKuCaiJi.getCode())) {
						//保存表关联的储存层数据信息
						saveStorageData(source_id, agent_id, databaseId, dsl_id, tableInfo);
					}
				} catch (Exception e) {
					if (!(e instanceof EntityDealZeroException)) {
						throw new BusinessException(e.getMessage());
					}
				}
			}
		}

	}

	private void saveTableInfo(long databaseId, Table_info tableInfo, Object collect_type) {
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

		if (collect_type.equals(CollectType.ShuJuKuCaiJi.getCode())) {
			Data_extraction_def extraction_def = new Data_extraction_def();
			extraction_def.setDed_id(PrimayKeyGener.getNextId());
			extraction_def.setTable_id(tableInfo.getTable_id());
			extraction_def.setData_extract_type(DataExtractType.YuanShuJuGeShi.getCode());
			extraction_def.setIs_header(IsFlag.Fou.getCode());
			extraction_def.setDatabase_code(DataBaseCode.UTF_8.getCode());
			extraction_def.setDbfile_format(FileFormat.PARQUET.getCode());
			extraction_def.setIs_archived(IsFlag.Fou.getCode());

			extraction_def.add(Dbo.db());
		}
	}

	@Method(desc = "根据表的ID更新表的列信息", logicStep = ""
		+ "1: 更新时,先删除上次的字段信息 "
		+ "2: 将此次新的数据信息增加进来,如果新的字段没有column_id则表示为新的字段,则设置默认的数据信息")
	@Param(name = "tableColumns", desc = "列的数据信息", range = "列信息的实体集合信息", isBean = true)
	@Param(name = "table_id", desc = "表的ID", range = "表主键信息，不可为空")
	void updateTableColumn(long table_id, List<Table_column> tableColumnList) {
		//1: 更新时,先删除上次的字段信息
		Dbo.execute("DELETE FROM " + Table_column.TableName + " WHERE table_id = ?", table_id);
		//使用旧的数据新增进来
		tableColumnList.forEach(table_column -> {
			//2: 将此次新的数据信息增加进来,如果新的字段没有column_id则表示为新的字段,则设置默认的数据信息
			if (table_column.getColumn_id() == null) {
				setTableColumnDefaultData(table_id, table_column);
			}
			//检查列的必要信息是否为空
			checkColumnInformation(table_column);
			//增加列信息
			table_column.add(Dbo.db());
		});
	}

	Map<String, Object> getClassifyAndSourceNum(long database_id) {

		return Dbo.queryOneObject("SELECT t2.classify_num,t4.datasource_number FROM "
			+ Database_set.TableName
			+ " t1 JOIN "
			+ Collect_job_classify.TableName
			+ " t2 ON t1.classify_id = t2.classify_id JOIN "
			+ Agent_info.TableName
			+ " t3 ON t1.agent_id = t3.agent_id JOIN "
			+ Data_source.TableName
			+ " t4 ON t3.source_id = t4.source_id WHERE t1.database_id = ?", database_id);
	}
}
