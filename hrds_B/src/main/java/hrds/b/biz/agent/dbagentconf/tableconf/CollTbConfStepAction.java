package hrds.b.biz.agent.dbagentconf.tableconf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.bean.CollTbConfParam;
import hrds.b.biz.agent.tools.SendMsgUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.CleanType;
import hrds.commons.codes.ExecuteState;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.StoreLayerDataSource;
import hrds.commons.codes.UnloadType;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_case;
import hrds.commons.entity.Column_clean;
import hrds.commons.entity.Column_merge;
import hrds.commons.entity.Column_split;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.entity.Data_store_reg;
import hrds.commons.entity.Database_set;
import hrds.commons.entity.Dcol_relation_store;
import hrds.commons.entity.Dtab_relation_store;
import hrds.commons.entity.Table_clean;
import hrds.commons.entity.Table_column;
import hrds.commons.entity.Table_info;
import hrds.commons.entity.Table_storage_info;
import hrds.commons.entity.Take_relation_etl;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.AgentActionUtil;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@DocClass(desc = "定义表抽取属性", author = "WangZhengcheng")
public class CollTbConfStepAction extends BaseAction {

	private static final long DEFAULT_TABLE_ID = 999999L;
	private static final JSONObject DEFAULT_TABLE_CLEAN_ORDER;
	private static final JSONObject DEFAULT_COLUMN_CLEAN_ORDER;
	private static final String VALID_S_DATE = "99991231"; // FIXME 有公共的

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
		desc = "根据数据库采集设置表ID加载页面初始化数据",
		logicStep =
			""
				+ "1、查询表信息数据"
				+ "2: 根据表名检查是否采集过(只要有一个阶段采集是完成或者运行中的,将不再支持编辑),这里的采集指的是发给Agent的"
				+ "3: 返回处理后的表信息")
	@Param(name = "colSetId", desc = "数据库设置ID,源系统数据库设置表主键,数据库对应表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不会为null")
	public List<Map<String, Object>> getInitInfo(long colSetId) {
		// 1、查询表信息数据
		List<Map<String, Object>> tableList =
			Dbo.queryList(
				" SELECT *, "
					+ " (CASE WHEN ( SELECT COUNT(1) FROM table_column WHERE "
					+ " table_id = ti.table_id AND is_primary_key = ? ) > 0 "
					+ " THEN 'true' ELSE 'false' END ) is_primary_key FROM "
					+ Table_info.TableName
					+ " ti "
					+ " WHERE ti.database_id = ? AND ti.is_user_defined = ? ",
				IsFlag.Shi.getCode(),
				colSetId,
				IsFlag.Fou.getCode());
		// 2: 根据表名检查是否采集过(只要有一个阶段采集是完成或者运行中的,将不再支持编辑),这里的采集指的是发给Agent的
		tableList.forEach(
			itemMap -> {
				List<Object> tableStateList = checkTableCollectState(colSetId, itemMap.get("table_name"));
				if (tableStateList.contains(ExecuteState.YunXingWanCheng.getCode())
					|| tableStateList.contains(ExecuteState.KaiShiYunXing.getCode())) {
					itemMap.put("collectState", false);
				} else {
					itemMap.put("collectState", true);
				}
			});

		// 数据可访问权限处理方式
		// 以上table_info表中都没有user_id字段，解决方式待讨论
		//    3: 返回处理后的表信息
		return tableList;
	}

	@Method(
		desc = "根据模糊表名和数据库设置id得到表相关信息，如果搜索框中用户输入了内容，就直接调用该接口",
		logicStep =
			""
				+ "1、根据colSetId去数据库中获取数据库设置相关信息"
				+ "2、和Agent端进行交互，得到Agent返回的数据"
				+ "3、对获取到的数据进行处理，获得模糊查询到的表名"
				+ "4、根据表名和colSetId获取界面需要显示的信息并返回")
	@Param(name = "colSetId", desc = "数据库设置ID,源系统数据库设置表主键,数据库对应表外键", range = "不为空")
	@Param(name = "inputString", desc = "用户界面输入用于模糊查询的关键词", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	// 查询按钮
	public List<Map<String, Object>> getTableInfo(long colSetId, String inputString) {
		// 1、根据colSetId去数据库中获取数据库设置相关信息
		Map<String, Object> databaseInfo = getDatabaseSetInfo(colSetId, getUserId());
		// 数据可访问权限处理方式
		// 以上SQL中使用user_id作为过滤条件，达到了访问权限控制的目的
		if (databaseInfo.isEmpty()) {
			throw new BusinessException("未找到数据库采集任务");
		}
		// 2、与Agent端进行交互，得到Agent返回的数据
		String methodName = AgentActionUtil.GETDATABASETABLE;
		long agentId = (long) databaseInfo.get("agent_id");
		String respMsg =
			SendMsgUtil.searchTableName(agentId, getUserId(), databaseInfo, inputString, methodName);
		// 3、对获取到的数据进行处理，获得模糊查询到的表名
		List<String> rightTables = JSON.parseObject(respMsg, new TypeReference<List<String>>() {
		});
		// 4、根据表名和colSetId获取界面需要显示的信息并返回
		return getTableInfoByTableName(rightTables, colSetId);
	}

	@Method(
		desc = "根据数据库设置id得到目标数据库中所有表相关信息，如果用户没有输入内容就点击查询，那么就调用该接口获取所有表",
		logicStep =
			""
				+ "1、根据colSetId去数据库中获取数据库设置相关信息"
				+ "2、和Agent端进行交互，得到Agent返回的数据"
				+ "3、对获取到的数据进行处理，根据表名和colSetId获取界面需要显示的信息并返回")
	@Param(name = "colSetId", desc = "数据库设置ID,源系统数据库设置表主键,数据库对应表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	public List<Map<String, Object>> getAllTableInfo(long colSetId) {
		// 1、根据colSetId去数据库中获取数据库设置相关信息
		Map<String, Object> databaseInfo = getDatabaseSetInfo(colSetId, getUserId());
		if (databaseInfo.isEmpty()) {
			throw new BusinessException("未找到数据库采集任务");
		}
		// 2、和Agent端进行交互，得到Agent返回的数据
		String methodName = AgentActionUtil.GETDATABASETABLE;
		long agentId = (long) databaseInfo.get("agent_id");
		String respMsg = SendMsgUtil.getAllTableName(agentId, getUserId(), databaseInfo, methodName);
		// 3、对获取到的数据进行处理，根据表名和colSetId获取界面需要显示的信息并返回
		List<String> tableNames = JSON.parseObject(respMsg, new TypeReference<List<String>>() {
		});

		return getTableInfoByTableName(tableNames, colSetId);
	}

	@Method(
		desc = "根据表ID获取给该表定义的分页抽取SQL",
		logicStep = "" + "1、去数据库中根据table_id查出该表定义的分页抽取SQL" + "2、返回")
	@Param(name = "tableId", desc = "数据库对应表ID", range = "不为空")
	@Return(desc = "分页SQL", range = "只要正常返回肯定不是空字符串")
	public Result getPageSQL(long tableId) {
		// 1、去数据库中根据table_id查出该表定义的分页抽取SQL
		Result result =
			Dbo.queryResult(
				"select ti.table_id, ti.page_sql,ti.is_customize_sql, ti.table_count, ti.pageparallels, ti.dataincrement "
					+ " from "
					+ Table_info.TableName
					+ " ti where ti.table_id = ?",
				tableId);
		if (result.isEmpty()) {
			throw new BusinessException("未获取到分页抽取SQL");
		}
		// 2、返回
		return result;
	}

	@Method(
		desc = "保存自定义抽取数据SQL",
		logicStep =
			""
				+ "1、根据colSetId去数据库中查询该数据库采集任务是否存在"
				+ "2、使用colSetId在table_info表中删除所有自定义SQL采集的记录，不关注删除的数目，结果可以是0-N"
				//              + "3、将前端传过来的参数(JSONArray)转为Table_info数组"
				+ "4、如果List集合不为空，遍历list,给每条记录生成ID，设置有效开始日期、有效结束日期、是否自定义SQL采集(是)、是否使用MD5(是)、"
				+ "   是否仅登记(是)"
				+ "5、保存数据进库"
				+ "6、保存数据自定义采集列相关信息进入table_column表"
				+ "7、如果数组为空，表示使用SQL抽取没有设置，那么就要把当前数据库采集任务中所有的自定义抽取设置作为脏数据全部删除")
	@Param(
		name = "tableInfoArray",
		desc = "Table_info的数组字符串",
		range =
			"Table_info 的实体数组字符串"
				+ "如果是新增的采集表，table_id为空，如果是编辑修改采集表，table_id不能为空，一个实体对象中应该包括表名(table_name)、"
				+ "表中文名(table_ch_name)、是否使用MD5(is_md5,请使用IsFlag代码项),卸数方式(unload_type,使用代码项 UnloadType),"
				+ "如果卸数方式是增量卸数,请传递增量SQL,并放入字段sql中( 格式如 : sql : { update:sql, delete:sql, add:sql}),"
				+ "可以忽略(is_parallel,is_customize_sql,table_count,pageparallels,dataincrement)"
				+ "如果卸数方式为全量卸数, 传递是否并行抽取(is_parallel,代码项IsFlag)、"
				+ "如果并行抽取选择,则需要传递 page_sql(这里的SQL指的是页面查询SQL语句),table_count, pageparallels, dataincrement. 反之不需要"
				+ "如果页面上没有数据，则该参数可以不传",
		nullable = true,
		valueIfNull = "")
	@Param(
		name = "tableColumn",
		desc = "表列字符数组集合字符串",
		range =
			"每张表的列信息集合",
		nullable = true,
		valueIfNull = "")
	@Param(name = "colSetId", desc = "数据库设置ID,源系统数据库设置表主键,数据库对应表外键", range = "不为空")
	// 使用SQL抽取数据页面，保存按钮后台方法
	public long saveAllSQL(String tableInfoArray, long colSetId, String tableColumn) {

		// 1、根据databaseId去数据库中查询该数据库采集任务是否存在
		long dbSetCount =
			Dbo.queryNumber(
				"select count(1) from " + Database_set.TableName + " where database_id = ?",
				colSetId)
				.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (dbSetCount != 1) {
			throw new BusinessException("数据库采集任务未找到");
		}

		List<Object> tableIds =
			Dbo.queryOneColumnList(
				"select table_id from "
					+ Table_info.TableName
					+ " where database_id = ? and is_user_defined = ?",
				colSetId,
				IsFlag.Shi.getCode());

		// 2、使用colSetId在table_info表中删除所有自定义SQL采集的记录，不关注删除的数目，结果可以是0-N
		Dbo.execute(
			"delete from " + Table_info.TableName + " where database_id = ? AND is_user_defined = ? ",
			colSetId,
			IsFlag.Shi.getCode());

		// 3、将前端传过来的参数转为List<Table_info>集合
		List<Table_info> tableInfos = JSONArray.parseArray(tableInfoArray, Table_info.class);
		/*
		 * 4、如果List集合不为空，遍历list,给每条记录生成ID，设置有效开始日期、有效结束日期、是否自定义SQL采集(是)、是否使用MD5(是)、
		 *  是否仅登记(是)
		 * */
		if (tableInfos != null && tableInfos.size() != 0) {
			for (int i = 0; i < tableInfos.size(); i++) {
				Table_info tableInfo = tableInfos.get(i);

				// 设置并行抽取自定义SQL的定义方式为否
				tableInfo.setIs_customize_sql(IsFlag.Fou.getCode());

				// 设置分页SQL为空
				tableInfo.setPage_sql("");

				if (StringUtil.isBlank(tableInfo.getTable_name())) {
					throw new BusinessException("保存SQL抽取数据配置，第" + (i + 1) + "条数据表名不能为空!");
				}
				if (StringUtil.isBlank(tableInfo.getTable_ch_name())) {
					throw new BusinessException("保存SQL抽取数据配置，第" + (i + 1) + "条数据表中文名不能为空!");
				}

				if (StringUtil.isBlank(tableInfo.getUnload_type())) {
					throw new BusinessException("保存SQL抽取数据配置，第" + (i + 1) + "条数据卸数方式不能为空!");
				}

				if (StringUtil.isBlank(tableInfo.getIs_md5())) {
					throw new BusinessException("保存SQL抽取数据配置，第" + (i + 1) + "条数据 MD5 不能为空!");
				}

				if (UnloadType.ofEnumByCode(tableInfo.getUnload_type()) == UnloadType.ZengLiangXieShu) {
					if (StringUtil.isBlank(tableInfo.getSql())) {
						throw new BusinessException(
							"保存采集表 " + tableInfo.getTable_name() + " 配置,第 " + (i + 1) + " 条,增量SQL不能为空!");
					}
					// 设置并行抽取方式为否
					tableInfo.setIs_parallel(IsFlag.Fou.getCode());

          /*
            2 : 设置数据总量为空
            3 : 设置每日数据量为空
            4 : 设置分页并行数为空
          */
					tableInfo.setTable_count("");
					tableInfo.setDataincrement("");
					tableInfo.setPageparallels("");
				} else if (UnloadType.ofEnumByCode(tableInfo.getUnload_type())
					== UnloadType.QuanLiangXieShu) {

					if (StringUtil.isBlank(tableInfo.getIs_parallel())) {
						throw new BusinessException(
							"保存采集表" + tableInfo.getTable_name() + "配置，第 " + (i + 1) + "条数据并行方式不能为空!");
					}

					if (StringUtil.isBlank(tableInfo.getSql())) {
						throw new BusinessException(
							"保存采集表 " + tableInfo.getTable_name() + " 配置,第 " + (i + 1) + " 条,全量SQL不能为空!");
					}

					if (IsFlag.ofEnumByCode(tableInfo.getIs_parallel()) == IsFlag.Shi) {

						if (tableInfo.getPageparallels() == null) {
							throw new BusinessException(
								"保存采集表" + tableInfo.getTable_name() + "配置，第 " + (i + 1) + "条数据分页并行数不能为空!");
						}
						if (tableInfo.getDataincrement() == null) {
							throw new BusinessException(
								"保存采集表" + tableInfo.getTable_name() + "配置，第 " + (i + 1) + "条每日数据增量不能为空!");
						}
						if (StringUtil.isBlank(tableInfo.getTable_count())) {
							throw new BusinessException(
								"保存采集表" + tableInfo.getTable_name() + "配置，第 " + (i + 1) + "条数据总量不能为空!");
						}
					}
				} else {
					throw new BusinessException(
						"保存采集表"
							+ tableInfo.getTable_name()
							+ "配置，第 "
							+ (i + 1)
							+ "条数据的卸数方式不存在, 得到的卸数方式代码项值是 :"
							+ tableInfo.getUnload_type());
				}

				if (StringUtil.isBlank(tableInfo.getRec_num_date())) {
					tableInfo.setRec_num_date(DateUtil.getSysDate());
				}
				//        tableInfo.setTable_count(CountNum.YiWan.getCode());
				tableInfo.setDatabase_id(colSetId);
				tableInfo.setValid_s_date(DateUtil.getSysDate());
				tableInfo.setValid_e_date(Constant.MAXDATE);
				// 是否自定义采集为是
				tableInfo.setIs_user_defined(IsFlag.Shi.getCode());
				tableInfo.setIs_register(IsFlag.Fou.getCode());
				// 如果是新增采集表
				if (tableInfo.getTable_id() == null) {
					tableInfo.setTable_id(PrimayKeyGener.getNextId());
					tableInfo.setTi_or(DEFAULT_TABLE_CLEAN_ORDER.toJSONString());
					// 5、保存数据进库
					tableInfo.add(Dbo.db());
					// 6、保存数据自定义采集列相关信息进入table_column表,如果未选择,
					// 则使用sql中查询的列进行保存,并检查是否选有主键(只针对卸数方式为增量时)
					if (StringUtil.isNotBlank(tableColumn)) {
						Map tableColumnMap = JsonUtil.toObjectSafety(tableColumn, Map.class)
							.orElseThrow(() -> new BusinessException("解析自定义列信息失败"));

						//如果不存在用户选择的列信息,则使用JDBC的方式获取列信息,并进行检查
						if (!tableColumnMap.containsKey(tableInfo.getTable_name())) {
							saveCustomSQLColumnInfoForAdd(tableInfo, colSetId);
						} else {
							saveCustomizeColumn(tableColumnMap.get(tableInfo.getTable_name()).toString(), tableInfo);
						}
					} else {
						saveCustomSQLColumnInfoForAdd(tableInfo, colSetId);
					}
				}
				// 如果是修改采集表
				else {
					long oldID = tableInfo.getTable_id();
					long newID = PrimayKeyGener.getNextId();
					tableInfo.setTable_id(newID);
					tableInfo.setTi_or(DEFAULT_TABLE_CLEAN_ORDER.toJSONString());
					// 5、保存数据进库
					tableInfo.add(Dbo.db());
//		  // 调用方法删除脏数据
//		  deleteDirtyDataOfTb(oldID);
					// 6、所有关联了原table_id的表，找到对应的字段，为这些字段设置新的table_id
					updateTableId(newID, oldID);
					// 更新设置的新字段信息
					if (StringUtil.isNotBlank(tableColumn)) {
						Map tableColumnMap = JsonUtil.toObjectSafety(tableColumn, Map.class)
							.orElseThrow(() -> new BusinessException("解析自定义列信息失败"));
						if (tableColumnMap.containsKey(tableInfo.getTable_name())) {
							saveCustomizeColumn(tableColumnMap.get(tableInfo.getTable_name()).toString(), tableInfo);
						}
					} else {
						saveCustomSQLColumnInfoForAdd(tableInfo, colSetId);
					}
				}
			}
		}
		// 7、如果List集合为空，表示使用SQL抽取没有设置，那么就要把当前数据库采集任务中所有的自定义抽取设置作为脏数据全部删除
		else {
			if (!tableIds.isEmpty()) {
				for (Object tableId : tableIds) {
					deleteDirtyDataOfTb((long) tableId);
				}
			}
		}
		return colSetId;
	}

	@Method(desc = "保存用户自定义的列信息", logicStep = "1: 获取到表的列信息转换为 Table_column实体 "
		+ "2: 检查选择的列是否设置了主键 "
		+ "3: 保存列的数据信息")
	@Param(name = "tableColumn", desc = "表的列数据信息", range = "不可为空")
	@Param(name = "tableInfo", desc = "表信息", range = "不可为空", isBean = true)
	private void saveCustomizeColumn(String tableColumn, Table_info tableInfo) {
		List<Table_column> tableColumnList = JSON.parseObject(tableColumn, new TypeReference<List<Table_column>>() {
		});
		//如果表的卸数方式是增量,则校验表的列主键是否选择,否则则抛出异常信息
		if (UnloadType.ofEnumByCode(tableInfo.getUnload_type()) == UnloadType.ZengLiangXieShu) {
			List<Boolean> primary = new ArrayList<>();
			tableColumnList.forEach(table_column -> {
				if (IsFlag.ofEnumByCode(table_column.getIs_primary_key()) == IsFlag.Shi) {
					primary.add(true);
				}
			});
			if (!primary.contains(true)) {
				throw new BusinessException("当前表(" + tableInfo.getTable_name() + ")的卸数方式为增量, 未设置主键,请检查");
			}
		}
		tableColumnList.forEach(table_column -> {
			if (table_column.getColumn_id() != null) {
				Dbo.execute("UPDATE " + Table_column.TableName
						+ " SET is_get = ?, is_primary_key = ?, column_name = ?, column_type = ?, column_ch_name = ?,"
						+ "	table_id = ?, valid_s_date = ?, valid_e_date = ?, is_alive = ?, is_new = ?, tc_or = ?,"
						+ " tc_remark = ? WHERE column_id = ?", table_column.getIs_get(), table_column.getIs_primary_key(),
					table_column.getColumn_name(), table_column.getColumn_type(), table_column.getColumn_ch_name(),
					tableInfo.getTable_id(), table_column.getValid_s_date(), table_column.getValid_e_date(),
					table_column.getIs_alive(), table_column.getIs_new(), table_column.getTc_or(),
					table_column.getTc_remark(),
					table_column.getColumn_id());
			} else {
				table_column.setColumn_id(PrimayKeyGener.getNextId());
				table_column.setTable_id(tableInfo.getTable_id());
				table_column.add(Dbo.db());
			}
		});
	}


	@Method(
		desc = "测试并行抽取SQL",
		logicStep =
			""
				+ "1、根据colSetId在database_set表中获得数据库设置信息"
				+ "2、调用AgentActionUtil工具类获取访问Agent端服务的url"
				+ "3、构建http请求访问Agent端服务,Agent端拿到用户定义的并行抽取SQL会尝试直接在目标数据库中执行该SQL，看是否能获取到数据"
				+ "4、如果响应失败，则抛出异常，这里的响应失败有两种情况，"
				+ "   (1)、和agent交互不成功"
				+ "   (2)、和agent交互成功，但是根据并行抽取SQL没有查到数据")
	@Param(name = "colSetId", desc = "数据库设置ID,源系统数据库设置表主键,数据库对应表外键", range = "不为空")
	@Param(name = "pageSql", desc = "用户设置的并行采集SQL", range = "不为空")
	public void testParallelExtraction(long colSetId, String pageSql) {
		// 1、根据colSetId在database_set表中获得数据库设置信息
		Map<String, Object> resultMap =
			Dbo.queryOneObject(
				"select agent_id, user_name, database_pad, database_drive,"
					+ " database_type, jdbc_url from "
					+ Database_set.TableName
					+ " where database_id = ?",
				colSetId);
		if (resultMap.isEmpty()) {
			throw new BusinessException("未找到数据库采集任务");
		}
		// 2、调用AgentActionUtil工具类获取访问Agent端服务的url
		String url =
			AgentActionUtil.getUrl(
				(Long) resultMap.get("agent_id"), getUserId(), AgentActionUtil.TESTPARALLELSQL);
		// 3、构建http请求访问Agent端服务,Agent端拿到用户定义的并行抽取SQL会尝试直接在目标数据库中执行该SQL，看是否能获取到数据
		HttpClient.ResponseValue resVal =
			new HttpClient()
				.addData("database_drive", (String) resultMap.get("database_drive"))
				.addData("jdbc_url", (String) resultMap.get("jdbc_url"))
				.addData("user_name", (String) resultMap.get("user_name"))
				.addData("database_pad", (String) resultMap.get("database_pad"))
				.addData("database_type", (String) resultMap.get("database_type"))
				.addData("pageSql", pageSql)
				.post(url);
		ActionResult actionResult =
			JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class)
				.orElseThrow(() -> new BusinessException("应用管理端与" + url + "服务交互异常"));
		// 4、如果响应失败，则抛出异常(和agent交互不成功)
		if (!actionResult.isSuccess()) {
			throw new BusinessException("并行抽取SQL(" + pageSql + ")测试失败");
		}
		boolean resultData = (boolean) actionResult.getData();
		// 5、和agent交互成功，但是根据并行抽取SQL没有查到数据,agent返回false，表示根据分页SQL没有取到数据，抛出异常给前端
		if (!resultData) {
			throw new BusinessException("根据并行抽取SQL(" + pageSql + ")未能获取到数据");
		}
	}

	@Method(
		desc = "根据表名和agent端交互，获取该表的数据总条数",
		logicStep =
			""
				+ "1、根据colSetId在database_set表中获得数据库设置信息"
				+ "2、调用AgentActionUtil工具类获取访问Agent端服务的url"
				+ "3、构建http请求访问Agent端服务,Agent端拿到用户定义的并行抽取SQL会尝试直接在目标数据库中执行该SQL，看是否能获取到数据"
				+ "4、如果响应失败，则抛出异常(和agent交互不成功)"
				+ "5、和agent交互成功，但是根据并行抽取SQL没有查到数据,agent返回false，表示根据分页SQL没有取到数据，抛出异常给前端")
	@Param(name = "colSetId", desc = "数据库设置ID,源系统数据库设置表主键,数据库对应表外键", range = "不为空")
	@Param(name = "tableName", desc = "表名", range = "不为空")
	@Return(desc = "数据量", range = "如果和agent端交互正常，并且的确查询到了该表，则返回的就是该表的数据条数")
	public long getTableDataCount(long colSetId, String tableName) {
		// 1、根据colSetId在database_set表中获得数据库设置信息
		Map<String, Object> resultMap =
			Dbo.queryOneObject(
				"select agent_id, user_name, database_pad, database_drive,"
					+ " database_type, jdbc_url from "
					+ Database_set.TableName
					+ " where database_id = ?",
				colSetId);
		if (resultMap.isEmpty()) {
			throw new BusinessException("未找到数据库采集任务");
		}
		// 2、调用AgentActionUtil工具类获取访问Agent端服务的url
		String url =
			AgentActionUtil.getUrl(
				(Long) resultMap.get("agent_id"), getUserId(), AgentActionUtil.GETTABLECOUNT);
		// 3、构建http请求访问Agent端服务,Agent端拿到用户定义的并行抽取SQL会尝试直接在目标数据库中执行该SQL，看是否能获取到数据
		HttpClient.ResponseValue resVal =
			new HttpClient()
				.addData("database_drive", (String) resultMap.get("database_drive"))
				.addData("jdbc_url", (String) resultMap.get("jdbc_url"))
				.addData("user_name", (String) resultMap.get("user_name"))
				.addData("database_pad", (String) resultMap.get("database_pad"))
				.addData("database_type", (String) resultMap.get("database_type"))
				.addData("tableName", tableName)
				.post(url);
		ActionResult actionResult =
			JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class)
				.orElseThrow(() -> new BusinessException("应用管理端与" + url + "服务交互异常"));
		// 4、如果响应失败，则抛出异常(和agent交互不成功)
		if (!actionResult.isSuccess()) {
			throw new BusinessException("获取" + tableName + "表数据量失败");
		}

		// 5、和agent交互成功，返回前端查询数据，进行以下处理的原因是如果直接返回整数类型，getData()方法会报错
		String count = (String) actionResult.getData();
		return Long.parseLong(count);
	}

	@Method(
		desc = "根据数据库设置ID获得用户自定义抽取SQL",
		logicStep =
			""
				+ "1、根据colSetId在table_info表中查询数据"
				+ "2: 根据表名检查是否采集过(只要有一个阶段采集是完成或者运行中的,将不再支持编辑),这里的采集指的是发给Agent的"
				+ "3: 返回处理后的表信息")
	@Param(name = "colSetId", desc = "数据库设置ID,源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空，是否有数据视实际情况而定")
	// 配置采集表页面，使用SQL抽取数据Tab页后台方法，用于回显已经设置的SQL
	public List<Map<String, Object>> getAllSQLs(long colSetId) {
		// 1、根据colSetId在table_info表中查询数据并返回
		List<Map<String, Object>> tableList =
			Dbo.queryList(
				" SELECT * "
					+ " FROM "
					+ Table_info.TableName
					+ " WHERE database_id = ? AND is_user_defined = ? order by table_id",
				colSetId,
				IsFlag.Shi.getCode());
		// 数据可访问权限处理方式
		// 以上table_info表中都没有user_id字段，解决方式待讨论
		// 2: 根据表名检查是否采集过(只要有一个阶段采集是完成或者运行中的,将不再支持编辑),这里的采集指的是发给Agent的
		tableList.forEach(
			itemMap -> {
				List<Object> tableStateList = checkTableCollectState(colSetId, itemMap.get("table_name"));
				if (tableStateList.contains(ExecuteState.YunXingWanCheng.getCode())
					|| tableStateList.contains(ExecuteState.KaiShiYunXing.getCode())) {
					itemMap.put("collectState", false);
				} else {
					itemMap.put("collectState", true);
				}
			});

		// 数据可访问权限处理方式
		// 以上table_info表中都没有user_id字段，解决方式待讨论
		//    3: 返回处理后的表信息
		return tableList;
	}

	@Method(
		desc = "根据数据库设置ID和表名，获取在该表采集时使用的过滤SQL",
		logicStep = "" + "1、根据colSetId和tableName在table_info表中获取数据并返回")
	@Param(name = "colSetId", desc = "数据库设置ID,源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Param(name = "tableName", desc = "表名", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空，是否有数据视实际情况而定")
	// 配置采集表页面,定义过滤按钮后台方法，用于回显已经对单表定义好的SQL
	public Result getSingleTableSQL(long colSetId, String tableName) {
		// 1、根据colSetId和tableName在table_info表中获取数据并返回
		return Dbo.queryResult(
			"SELECT * "
				+ " FROM "
				+ Table_info.TableName
				+ " WHERE database_id = ? AND table_name = ? ",
			colSetId,
			tableName);
		// 数据可访问权限处理方式
		// 以上table_info表中都没有user_id字段，解决方式待讨论
	}

	@Method(
		desc = "根据参数获取表的所有列信息",
		logicStep =
			""
				+ "1、判断tableId的值"
				+ "2、若tableId为999999，表示要获取当前采集任务中不存在的表的所有列，需要和agent进行交互"
				+ "      2-1、根据colSetId去数据库中查出DB连接信息"
				+ "      2-2、和Agent交互，获取表中的列信息"
				+ "3、若tableId不为999999，表示要获取当前采集任务中存在的表的所有列，直接在table_column表中查询即可"
				+ "      3-1、根据tableId在table_column表中获取列信息"
				+ "4、根据数据库设置ID，查询当前数据库采集任务是否被agent执行完了，如果执行完成了，则前端只允许修改列中文名"
				+ "5、返回")
	@Param(name = "tableName", desc = "表名", range = "不为空")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Param(
		name = "tableId",
		desc = "数据库对应表主键,表对应的字段表外键",
		range = "如果要获取不是当前采集中已经存在的表的列信息，这个参数可以不传",
		nullable = true,
		valueIfNull = "999999")
	@Return(
		desc = "key为table_name，value为表名；key为columnInfo，value为List<Table_column> 表示列的信息",
		range =
			"不为空，响应的数据格式有两种"
				+ "(1)、没有column_id，表示列信息是和agent端交互得到的，前端默认全部不勾选"
				+ "(2)、有column_id，表示列信息是在数据库表中查出来的，前端按照is_get字段判断是否勾选")
	// 配置采集表页面,选择列按钮后台方法
	public Map<String, Object> getColumnInfo(String tableName, long colSetId, long tableId) {
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("tableName", tableName);
		// 1、判断tableId的值
		// 2、如果要获取当前采集任务中不存在的表的所有列，需要和agent进行交互
		if (tableId == DEFAULT_TABLE_ID) {
			// 2-1、根据colSetId去数据库中查出DB连接信息
			// 2-2、和Agent交互，获取表中的列信息
			List<Table_column> tableColumns = getColumnInfoByTableName(colSetId, getUserId(), tableName);
			returnMap.put("columnInfo", tableColumns);
		}
		// 3、如果要获取当前采集任务中存在的表的所有列，直接在table_column表中查询即可
		else {
			// 3-1、根据tableId在table_column表中获取列信息
			List<Table_column> tableColumns =
				Dbo.queryList(
					Table_column.class,
					" SELECT * FROM "
						+ Table_column.TableName
						+ " WHERE table_id = ? order by cast(tc_remark as integer)",
					tableId);
			returnMap.put("columnInfo", tableColumns);
		}
		// 4、根据数据库设置ID，查询当前数据库采集任务是否被agent执行完了，如果执行完成了，则前端只允许修改列中文名
		long editCount =
			Dbo.queryNumber(
				"select count(1) from "
					+ Data_store_reg.TableName
					+ " where database_id = ?",
				colSetId)
				.orElseThrow(() -> new BusinessException("SQL查询错误"));
		if (editCount > 0) {
			returnMap.put("editFlag", IsFlag.Fou.getCode());
		} else {
			returnMap.put("editFlag", IsFlag.Shi.getCode());
		}
		// 5、返回
		return returnMap;
	}

	@Method(
		desc = "根据数据库设置ID获取单表采集配置的SQL过滤，分页SQL",
		logicStep =
			""
				+ "1、根据数据库设置ID在数据库设置表中查询是否有这样一个数据库采集任务"
				+ "2、如果没有，或者查询结果大于1，抛出异常给前端"
				+ "3、根据数据库设置ID在数据库采集对应表中获取SQL过滤，是否并行抽取，分页SQL，数据总量，分页并行数，每天数据增量返回给前端")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空，其中sql字段表示过滤SQL，page_sql字段表示分页SQL，" + "is_parallel字段表示是否并行抽取")
	public Result getSQLInfoByColSetId(long colSetId) {
		// 1、根据数据库设置ID在数据库设置表中查询是否有这样一个数据库采集任务
		long count =
			Dbo.queryNumber(
				"select count(1) from " + Database_set.TableName + " where database_id = ?",
				colSetId)
				.orElseThrow(() -> new BusinessException("SQL查询错误"));
		// 2、如果没有，或者查询结果大于1，抛出异常给前端
		if (count != 1) {
			throw new BusinessException("未找到数据库采集任务");
		}
		// 3、根据数据库设置ID在数据库采集对应表中获取SQL过滤，是否并行抽取，分页SQL，数据总量，分页并行数，每天数据增量返回给前端
		return Dbo.queryResult(
			"select * "
				+ " from "
				+ Table_info.TableName
				+ " where database_id = ? and is_user_defined = ?",
			colSetId,
			IsFlag.Fou.getCode());
	}

	@Method(
		desc = "根据数据库设置ID获取表的字段信息",
		logicStep =
			""
				+ "1、根据数据库设置ID在数据库设置表中查询是否有这样一个数据库采集任务"
				+ "2、如果没有，或者查询结果大于1，抛出异常给前端"
				+ "3、根据数据库设置ID查询当前数据库采集任务中所有非自定义采集的table_id<结果集1>"
				+ "4、如果<结果集1>为空，则返回空的集合给前端"
				+ "5、如果<结果集1>不为空，则遍历<结果集1>，用每一个table_id得到列的相关信息<结果集2>"
				+ "6、以table_name为key，以<结果集2>为value，将数据封装起来返回给前端")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空，key为table_id，value为该表下所有字段的信息")
	public Map<String, List<Map<String, Object>>> getColumnInfoByColSetId(long colSetId) {
		// 1、根据数据库设置ID在数据库设置表中查询是否有这样一个数据库采集任务
		long count =
			Dbo.queryNumber(
				"select count(1) from " + Database_set.TableName + " where database_id = ?",
				colSetId)
				.orElseThrow(() -> new BusinessException("SQL查询错误"));
		// 2、如果没有，或者查询结果大于1，抛出异常给前端
		if (count != 1) {
			throw new BusinessException("未找到数据库采集任务");
		}
		// 3、根据数据库设置ID查询当前数据库采集任务中所有非自定义采集的table_id<结果集1>
		Result tableInfos =
			Dbo.queryResult(
				"select table_id, table_name from "
					+ Table_info.TableName
					+ " where database_id = ? and is_user_defined = ?",
				colSetId,
				IsFlag.Fou.getCode());
		// 4、如果<结果集1>为空，则返回空的集合给前端
		if (tableInfos.isEmpty()) {
			return Collections.emptyMap();
		}
		// 5、如果<结果集1>不为空，则遍历<结果集1>，用每一个table_id得到列的相关信息<结果集2>
		Map<String, List<Map<String, Object>>> returnMap = new HashMap<>();
		/*
		 * 6、以table_name为key，以<结果集2>为value，将数据封装起来返回给前端，之所以查询全部的字段是因为该接口是在保存前调用的，
		 * 对于没有修改过的字段，在保存时做的是update操作，所以要返回前端全部数据，前端进行修改后再传给后台全部数据，这样更新字段时才不会丢失数据
		 * */
		for (int i = 0; i < tableInfos.getRowCount(); i++) {
			Result result =
				Dbo.queryResult(
					"select * from " + Table_column.TableName + " where table_id = ?",
					tableInfos.getLong(i, "table_id"));
			if (result.isEmpty()) {
				throw new BusinessException("获取表字段信息失败");
			}
			returnMap.put(tableInfos.getString(i, "table_name"), result.toList());
		}
		return returnMap;
	}

	@Method(
		desc = "处理新增采集表信息时表中列信息的保存",
		logicStep =
			""
				+ "1、判断collColumn参数是否为空字符串"
				+ "   1-1、是，表示用户没有选择采集列，则应用管理端同Agent端交互，获取该表的列信息"
				+ "   1-2、否，表示用户自定义采集列，则解析collColumn为List集合"
				+ "2、设置主键，外键等信息，如果columnSort不为空，则将Table_column对象的remark属性设置为该列的采集顺序"
				+ "3、保存这部分数据")
	@Param(
		name = "tableInfo",
		desc =
			"一个Table_info对象必须包含table_name,table_ch_name和是否并行抽取，"
				+ "如果并行抽取，那么并行抽取SQL也要有；如果是新增的采集表，table_id为空，如果是编辑修改采集表，table_id不能为空"
				+ "过滤的sql页面没定义就是空，页面定义了就不为空",
		range = "不为空，Table_info类的实体类对象")
	@Param(
		name = "collColumn",
		desc = "该表要采集的字段信息",
		range =
			"如果用户没有选择采集列，这个参数可以不传，"
				+ "表示采集这张表的所有字段;"
				+ "参数格式：json"
				+ "内容：是否主键(is_primary_key)"
				+ "      列名(column_name)"
				+ "      字段类型(column_type)"
				+ "      列中文名(column_ch_name)")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Param(name = "columnCleanOrder", desc = "列清洗默认优先级", range = "不为空，json格式的字符串")
	private void saveTableColumnInfoForAdd(
		Table_info tableInfo, String collColumn, long colSetId, String columnCleanOrder) {
		// 1、判断collColumn参数是否为空字符串
		List<Table_column> tableColumns;
		if (StringUtil.isBlank(collColumn)) {
			// 1-1、是，表示用户没有选择采集列，则应用管理端同Agent端交互,获取该表所有列进行采集
			tableColumns = getColumnInfoByTableName(colSetId, getUserId(), tableInfo.getTable_name());
			// 同agent交互得到的数据，is_get字段默认都设置为是,is_primay_key默认设置为否
			if (!tableColumns.isEmpty()) {
				for (Table_column tableColumn : tableColumns) {
					tableColumn.setIs_get(IsFlag.Shi.getCode());
					// 这里取得的列主键根据在源数据库是否为主键进行设置
					if (tableColumn.getIs_primary_key() != null) {
						if (IsFlag.ofEnumByCode(tableColumn.getIs_primary_key()) == IsFlag.Shi) {
							tableColumn.setIs_primary_key(IsFlag.Shi.getCode());
						} else {
							tableColumn.setIs_primary_key(IsFlag.Fou.getCode());
						}
					} else {
						tableColumn.setIs_primary_key(IsFlag.Fou.getCode());
					}
				}
			}
		} else {
			// 1-2、否，表示用户自定义采集列，则解析collColumn为List集合
			tableColumns = JSON.parseArray(collColumn, Table_column.class);
		}
		// 2、设置主键，外键等信息，如果columnSort不为空，则将Table_column对象的remark属性设置为该列的采集顺序
		if (tableColumns != null && !tableColumns.isEmpty()) {
			for (Table_column tableColumn : tableColumns) {
				Validator.notBlank(tableColumn.getColumn_name(), "保存" + tableColumn.getColumn_name() + "采集列时，字段名不能为空");
				Validator.notBlank(tableColumn.getColumn_type(), "保存" + tableColumn.getColumn_type() + "采集列时，字段类型不能为空");
				Validator.notBlank(tableColumn.getIs_get(), "保存" + tableColumn.getIs_get() + "采集列时，是否采集标识位不能为空");

				IsFlag.ofEnumByCode(tableColumn.getIs_get());
				Validator
					.notBlank(tableColumn.getIs_primary_key(), "保存" + tableColumn.getIs_primary_key() + "采集列时，是否主键标识位不能为空");
				// 设置主键
				tableColumn.setColumn_id(PrimayKeyGener.getNextId());
				// 设置外键
				tableColumn.setTable_id(tableInfo.getTable_id());
				// 设置有效开始时间和有效结束时间
				tableColumn.setValid_s_date(DateUtil.getSysDate());
				tableColumn.setValid_e_date(Constant.MAXDATE);
				// 设置清洗列默认优先级
				tableColumn.setTc_or(columnCleanOrder);
				// 3、保存这部分数据
				tableColumn.add(Dbo.db());
			}
		} else {
			throw new BusinessException("保存" + tableInfo.getTable_name() + "的采集字段失败");
		}
	}

	@Method(
		desc = "处理修改采集表信息时表中列信息的保存和更新",
		logicStep =
			""
				+ "1、将collColumn反序列化为Table_column的List集合"
				+ "2、如果List集合为空，抛出异常"
				+ "3、否则，遍历集合，获取每一个Table_column对象，设置新的table_id，并更新到数据库中"
				+ "4、这样做的目的是为了保证清洗页面配置的字段清洗和存储目的地页面定义的特殊用途字段(主键，索引列)不会丢失")
	@Param(
		name = "tableInfo",
		desc = "封装有新的表ID和表名等信息",
		range = "不为空，Table_info类的实体类对象",
		isBean = true)
	@Param(
		name = "collColumn",
		desc = "该表要采集的字段信息",
		range =
			"如果用户没有选择采集列，这个参数可以不传，"
				+ "表示采集这张表的所有字段;"
				+ "参数格式：json"
				+ "内容：是否主键(is_primary_key)"
				+ "      列名(column_name)"
				+ "      字段类型(column_type)"
				+ "      列中文名(column_ch_name)"
				+ "      是否采集(is_get)"
				+ "      表ID(table_id)"
				+ "      有效开始日期(valid_s_date)"
				+ "      有效结束日期(valid_e_date)"
				+ "      是否保留原字段(is_alive)"
				+ "      是否为变化生成(is_new)"
				+ "      清洗顺序(tc_or)"
				+ "      备注(remark)")
	private void saveTableColumnInfoForUpdate(Table_info tableInfo, String collColumn) {
		// 1、将collColumn反序列化为Table_column的List集合
		List<Table_column> tableColumns = JSONArray.parseArray(collColumn, Table_column.class);
		// 2、如果List集合为空，抛出异常
		if (tableColumns == null || tableColumns.isEmpty()) {
			throw new BusinessException("未获取到" + tableInfo.getTable_name() + "表的字段信息");
		}
		// 3、否则，遍历集合，获取每一个Table_column对象，设置新的table_id，并更新到数据库中
		for (Table_column tableColumn : tableColumns) {
			tableColumn.setTable_id(tableInfo.getTable_id());
			tableColumn.update(Dbo.db());
		}
	}

	@Method(
		desc = "根据colSetId, userId和表名与Agent端交互得到该表的列信息",
		logicStep =
			""
				+ "1、根据colSetId和userId去数据库中查出DB连接信息"
				+ "2、封装数据，调用方法和agent交互，获取列信息"
				+ "3、将列信息反序列化为Json数组"
				+ "4、由于agent端返回的信息比较多，而我们前端用到的信息较少，所以在这里重新做封装"
				+ "5、返回List集合")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Param(name = "userId", desc = "当前登录用户ID，sys_user表主键", range = "不为空")
	@Param(name = "tableName", desc = "要获取列的表名", range = "不为空")
	@Return(
		desc = "在Agent端获取到的该表的列信息",
		range = "不为空，" + "一个在Agent端封装好的Table_column对象的is_primary_key,column_name,column_type属性必须有值")
	private List<Table_column> getColumnInfoByTableName(
		long colSetId, long userId, String tableName) {
		// 1、根据colSetId和userId去数据库中查出DB连接信息
		Map<String, Object> databaseInfo = getDatabaseSetInfo(colSetId, userId);
		if (databaseInfo.isEmpty()) {
			throw new BusinessException("未找到数据库采集任务");
		}
		long agentId = (long) databaseInfo.get("agent_id");
		// 2、封装数据，调用方法和agent交互，获取列信息
		String respMsg =
			SendMsgUtil.getColInfoByTbName(
				agentId, getUserId(), databaseInfo, tableName, AgentActionUtil.GETTABLECOLUMN);
		// 3、将列信息反序列化为Json数组
		return JSON.parseObject(respMsg, new TypeReference<List<Table_column>>() {
		});
	}

	@Method(
		desc = "保存单表查询画面配置的所有表采集信息",
		logicStep =
			""
				+ "所有表信息放在一起是为了本次保存在一个事务中，同时成功或同时失败"
				+ "1、不论新增采集表还是编辑采集表，页面上所有的内容都可能被修改，所以直接执行SQL，"
				+ "按database_id删除table_info表中所有非自定义采集SQL的数据"
				+ "2、校验Table_info对象中的信息是否合法"
				+ "3、给Table_info对象设置基本信息(valid_s_date,valid_e_date,is_user_defined,is_register等)"
				+ "4、获取Table_info对象的table_id属性，如果该属性没有值，说明这张采集表是新增的，"
				+ "否则这张采集表在当前采集任务中，已经存在，且有可能经过了修改"
				+ "5、如果是新增采集表"
				+ "       5-1、生成table_id，并存入Table_info对象中"
				+ "       5-2、保存Table_info对象"
				+ "       5-3、保存该表中所有字段的信息进入table_column表"
				+ "6、如果是修改采集表"
				+ "       6-1、保留原有的table_id，为当前数据设置新的table_id"
				+ "       6-2、保存Table_info对象"
				+ "       6-3、所有关联了原table_id的表，找到对应的字段，为这些字段设置新的table_id"
				+ "       6-4、编辑采集表，将该表要采集的列信息保存到相应的表里面"
				+ "7、如果本次接口访问有被删除的表，则调用方法删除该表的脏数据")
	@Param(
		name = "tableInfoString",
		desc = "当前数据库采集任务要采集的所有的表信息组成的json格式的字符串",
		range =
			"Table_info 的实体数组字符串"
				+ "如果是新增的采集表，table_id为空，如果是编辑修改采集表，table_id不能为空，一个实体对象中应该包括表名(table_name)、表中文名(table_ch_name)、"
				+ "是否使用MD5(is_md5,请使用IsFlag代码项),卸数方式(unload_type,使用代码项 UnloadType),"
				+ "如果卸数方式是增量卸数,请传递增量SQL,并放入字段sql中( 格式如 : sql : { update:sql, delete:sql, add:sql}),"
				+ "可以忽略(is_parallel,is_customize_sql,table_count,pageparallels,dataincrement)"
				+ "如果卸数方式为全量卸数, 传递是否并行抽取(is_parallel,代码项IsFlag)、"
				+ "如果用户定义了并行抽取,那么应该还有is_customize_sql(IsFlag代码项),"
				+ "如果is_customize_sql为是,则需要(page_sql)，反之需要table_count, pageparallels, dataincrement. 如果页面上没有数据，则该参数可以不传",
		nullable = true,
		valueIfNull = "")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Param(
		name = "collTbConfParamString",
		desc = "采集表对应采集字段配置参数",
		range =
			""
				+ "key为collColumnString，表示被采集的列，CollTbConfParam 数组格式的字符串"
				+ " 一个json对象中应该包括列名(column_name)、字段类型(column_type)、列中文名(column_ch_name)、是否采集(is_get)"
				+ "如果用户没有选择采集列，则传空字符串，系统默认采集该张表所有列"
				+ "注意：tableInfoString和collTbConfParamString中，对象的顺序和数目要保持一致，比如："
				+ "tableInfoString：[{A表表信息},{B表表信息}]"
				+ "collTbConfParamString：[{A表字段配置参数},{B表字段配置参数}]"
				+ "如果页面上不选择采集表，那么这个参数可以不传",
		nullable = true,
		valueIfNull = "")
	@Param(
		name = "delTbString",
		desc = "本次接口访问被删除的表ID组成的json字符串",
		range = "如果本次访问没有被删除的接口，该参数可以不传",
		nullable = true)
	@Return(desc = "保存成功后返回当前采集任务ID", range = "不为空")
	public long saveCollTbInfo(
		String tableInfoString, long colSetId, String collTbConfParamString, String delTbString) {

		Map<String, Object> resultMap =
			Dbo.queryOneObject(
				"select * from " + Database_set.TableName + " where database_id = ?", colSetId);
		if (resultMap.isEmpty()) {
			throw new BusinessException("未找到数据库采集任务");
		}

		/*
		 * 1、不论新增采集表还是编辑采集表，页面上所有的内容都可能被修改，所以直接执行SQL，
		 * 按database_id删除table_info表中,所有非自定义采集SQL的数据，不关心删除数据的条数
		 * */
		Dbo.execute(
			" DELETE FROM " + Table_info.TableName + " WHERE database_id = ? AND is_user_defined = ? ",
			colSetId,
			IsFlag.Fou.getCode());

		List<Table_info> tableInfos = JSONArray.parseArray(tableInfoString, Table_info.class);
		List<CollTbConfParam> tbConfParams =
			JSONArray.parseArray(collTbConfParamString, CollTbConfParam.class);

		if (tableInfos != null && tbConfParams != null) {
			if (tableInfos.size() != tbConfParams.size()) {
				throw new BusinessException("请在传参时确保采集表数据和配置采集字段信息一一对应");
			}
			for (int i = 0; i < tableInfos.size(); i++) {
				Table_info tableInfo = tableInfos.get(i);
				// 2、校验Table_info对象中的信息是否合法
				Validator.notBlank(tableInfo.getTable_name(),
					"保存采集表" + tableInfo.getTable_name() + "配置，第 " + (i + 1) + "条数据表名不能为空!");
				Validator.notBlank(tableInfo.getTable_ch_name(),
					"保存采集表" + tableInfo.getTable_name() + "配置，第 " + (i + 1) + "条数据表中文名不能为空!");
				Validator.notBlank(tableInfo.getUnload_type(),
					"保存采集表 " + tableInfo.getTable_name() + " 配置,第 " + (i + 1) + " 条,卸数方式不能为空!");

				// 检查是否存在卸数方式,如果存在检查当前的方式是否为增量,如果是增量则判断增量的SQL是否存在
				/*
				 * 如果卸数方式是 增量的形式,则保存增量的SQL, 数据结构为 { update:sql, delete:sql, insert:sql }
				 * update : 更新的SQL语句
				 * delete : 删除的SQL语句
				 * insert : 新增的SQL语句,
				 * 将并行抽取设置为否的状态
				 */
				if (UnloadType.ofEnumByCode(tableInfo.getUnload_type()) == UnloadType.ZengLiangXieShu) {
					Validator.notBlank(tableInfo.getSql(),
						"保存采集表 " + tableInfo.getTable_name() + " 配置,第 " + (i + 1) + " 条,增量SQL不能为空!");
					// 将并行抽取设置为否的状态
					tableInfo.setIs_parallel(IsFlag.Fou.getCode());
					// 设置并行抽取中的自定义SQL字段为否
					tableInfo.setIs_customize_sql(IsFlag.Fou.getCode());
            /*
              1 : 设置分页SQL为空
              2 : 设置数据总量为空
              3 : 设置每日数据量为空
              4 : 设置分页并行数为空
            */
					tableInfo.setPage_sql("");
					tableInfo.setTable_count("");
					tableInfo.setDataincrement("");
					tableInfo.setPageparallels("");
				} else if (UnloadType.ofEnumByCode(tableInfo.getUnload_type())
					== UnloadType.QuanLiangXieShu) {

					Validator.notBlank(tableInfo.getIs_parallel(),
						"保存采集表" + tableInfo.getTable_name() + "配置，第 " + (i + 1) + "条数据并行方式不能为空!");

					IsFlag isFlag = IsFlag.ofEnumByCode(tableInfo.getIs_parallel());
					if (isFlag == IsFlag.Shi) {
              /*
              这里需要注意的是,并行情况下选择的是自定义分页SQL采集还是自定义总量的并行方式采集
              如果选择的是自定义SQL分页方式,则将 数据量,每日数据量,并行数置空, 反之则将分页SQL置空
              */
						Validator.notBlank(tableInfo.getIs_customize_sql(),
							"保存采集表" + tableInfo.getTable_name() + "配置，第 " + (i + 1) + "条数据自定义SQL方式不能为空!");

						if (IsFlag.ofEnumByCode(tableInfo.getIs_customize_sql()) == IsFlag.Shi) {
							tableInfo.setTable_count("");
							tableInfo.setDataincrement("");
							tableInfo.setPageparallels("");
							Validator.notBlank(tableInfo.getPage_sql(),
								"保存采集表"
									+ tableInfo.getTable_name()
									+ "配置，第 "
									+ (i + 1)
									+ "条数据分页抽取SQL不能为空!");
						} else {
							// 将分页SQL置为空
							tableInfo.setPage_sql("");
							if (tableInfo.getPageparallels() == null) {
								throw new BusinessException(
									"保存采集表" + tableInfo.getTable_name() + "配置，第 " + (i + 1) + "条数据分页并行数不能为空!");
							}
							if (tableInfo.getDataincrement() == null) {
								throw new BusinessException(
									"保存采集表" + tableInfo.getTable_name() + "配置，第 " + (i + 1) + "条每日数据增量不能为空!");
							}
							//                  if (StringUtil.isBlank(tableInfo.getTable_count())) {
							//                    throw new BusinessException(
							//                        "保存采集表" + tableInfo.getTable_name() + "配置，第 " + (i + 1)
							// + "条数据总量不能为空!");
							//                  }
						}
					} else {
						// 如果并行抽取的方式是否,则将是否并行抽取中的自定义sql设置为否
						tableInfo.setIs_customize_sql(IsFlag.Fou.getCode());
					}
				} else {
					throw new BusinessException(
						"保存采集表"
							+ tableInfo.getTable_name()
							+ "配置，第 "
							+ (i + 1)
							+ "条数据的卸数方式不存在, 得到的卸数方式是 :"
							+ tableInfo.getUnload_type());
				}

				// 数据获取时间如果为空,则使用当天的时间
				if (StringUtil.isBlank(tableInfo.getRec_num_date())) {
					tableInfo.setRec_num_date(DateUtil.getSysDate());
				}

				// 检查MD5是否有值
				Validator.notBlank(tableInfo.getIs_md5(),
					"保存采集表" + tableInfo.getTable_name() + "配置，第 " + (i + 1) + "条数据是否算MD5不能为空!");
				// 3、给Table_info对象设置基本信息(valid_s_date,valid_e_date,is_user_defined,is_register)
				tableInfo.setValid_s_date(DateUtil.getSysDate());
				tableInfo.setValid_e_date(Constant.MAXDATE);
				// 是否自定义采集为是义采集为否
				tableInfo.setIs_user_defined(IsFlag.Fou.getCode());
				// 数据采集，该字段就是否
				tableInfo.setIs_register(IsFlag.Fou.getCode());

				// 4、获取Table_info对象的table_id属性，如果该属性没有值，说明这张采集表是新增的，否则这张采集表在当前采集任务中
				// 已经存在，且有可能经过了修改
				// 5、如果是新增采集表
				if (tableInfo.getTable_id() == null
					|| StringUtil.isBlank(tableInfo.getTable_id().toString())) {
					// 5-1、生成table_id，并存入Table_info对象中
					tableInfo.setTable_id(PrimayKeyGener.getNextId());
					tableInfo.setTi_or(DEFAULT_TABLE_CLEAN_ORDER.toJSONString());
					// 5-2、保存Table_info对象
					tableInfo.add(Dbo.db());
					// 5-3、新增采集表，将该表要采集的列信息保存到相应的表里面
					saveTableColumnInfoForAdd(
						tableInfo, tbConfParams.get(i).getCollColumnString(), colSetId,
						DEFAULT_COLUMN_CLEAN_ORDER.toJSONString());
				}
				// 6、如果是修改采集表
				else {
					// 6-1、保留原有的table_id，为当前数据设置新的table_id
					long oldID = tableInfo.getTable_id();
					long newID = PrimayKeyGener.getNextId();
					tableInfo.setTable_id(newID);
					tableInfo.setTi_or(DEFAULT_TABLE_CLEAN_ORDER.toJSONString());
					// 6-2、保存Table_info对象
					tableInfo.add(Dbo.db());
					// 6-3、所有关联了原table_id的表，找到对应的字段，为这些字段设置新的table_id
					updateTableId(newID, oldID);
					// 6-4、编辑采集表，将该表要采集的列信息保存到相应的表里面
					saveTableColumnInfoForUpdate(tableInfo, tbConfParams.get(i).getCollColumnString());
				}
			}
		}
		// 7、如果本次接口访问有被删除的表，则调用方法删除该表的脏数据
		List<Table_info> delTables = JSONArray.parseArray(delTbString, Table_info.class);
		if (delTables != null && !delTables.isEmpty()) {
			for (Table_info tableInfo : delTables) {
				deleteDirtyDataOfTb(tableInfo.getTable_id());
			}
		}
		return colSetId;
	}

	@Method(desc = "根据colSetId去数据库中查出DB连接信息", logicStep = "1、根据colSetId和userId去数据库中查出DB连接信息")
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Param(name = "userId", desc = "当前登录用户ID，sys_user表主键", range = "不为空")
	@Return(desc = "查询结果集", range = "不为空")
	private Map<String, Object> getDatabaseSetInfo(long colSetId, long userId) {

		long databaseNum =
			Dbo.queryNumber(
				"SELECT COUNT(1) FROM " + Database_set.TableName + " WHERE database_id = ?",
				colSetId)
				.orElseThrow(() -> new BusinessException("SQL查询异常"));
		if (databaseNum == 0) {
			throw new BusinessException("任务(" + colSetId + ")不存在!!!");
		}
		// 1、根据colSetId和userId去数据库中查出DB连接信息
		return Dbo.queryOneObject(
			" select t1.database_type, t1.database_ip, t1.database_port, t1.database_name, "
				+ " t1.database_pad, t1.user_name, t1.database_drive, t1.jdbc_url, t1.agent_id, t1.db_agent, t1.plane_url"
				+ " from "
				+ Database_set.TableName
				+ " t1"
				+ " join "
				+ Agent_info.TableName
				+ " ai on ai.agent_id = t1.agent_id"
				+ " where t1.database_id = ? and ai.user_id = ? ",
			colSetId,
			userId);
	}

	@Method(
		desc = "根据表名和数据库设置表主键获得表详细信息",
		logicStep =
			""
				+ "1、如果集合为空，则直接返回空集合"
				+ "2、如果集合不是空，则进行如下处理"
				+ "   2-1、根据String的自然顺序(字母a-z)对表名进行升序排序"
				+ "   2-2、tableResult结果可能有数据，也可能没有数据"
				+ "   2-3、有数据的情况：编辑采集任务，模糊查询的这张表已经存在于本次采集任务中"
				+ "   2-4、没有数据的情况：新增采集任务，那么所有的表都无法在table_info中查到数据，给是否并行抽取默认值为否"
				+ "3、返回")
	@Param(name = "tableNames", desc = "需要获取详细信息的表名集合", range = "不限")
	@Param(name = "colSetId", desc = "数据库设置ID", range = "不为空")
	@Return(desc = "存有表详细信息的List集合", range = "查到信息则不为空，没有查到信息则为空")
	private List<Map<String, Object>> getTableInfoByTableName(
		List<String> tableNames, long colSetId) {
		// 1、如果集合为空，则直接返回空集合
		if (tableNames.isEmpty()) {
			return Collections.emptyList();
		}
		// 2、如果集合不是空，则进行如下处理
		List<Map<String, Object>> results = new ArrayList<>();
		// 2-1、根据String的自然顺序(字母a-z)对表名进行升序排序
		Collections.sort(tableNames);
		for (String tableName : tableNames) {
			Map<String, Object> tableResult =
				Dbo.queryOneObject(
					" select *"
						+ " FROM "
						+ Table_info.TableName
						+ " ti "
						+ " WHERE ti.database_id = ? AND ti.table_name = ? "
						+ " AND ti.is_user_defined = ? ",
					colSetId,
					tableName,
					IsFlag.Fou.getCode());

			// 2-2、tableResult结果可能有数据，也可能没有数据
			// 2-3、有数据的情况：编辑采集任务，模糊查询的这张表已经存在于本次采集任务中
			// 2-4、没有数据的情况：新增采集任务，那么所有的表都无法在table_info中查到数据，给是否并行抽取默认值为否
			if (tableResult.isEmpty()) {
				Map<String, Object> map = new HashMap<>();
				map.put("table_name", tableName);
				map.put("table_ch_name", tableName);
        /*
         如果当前表的信息未在我们数据库中出现,
         1: 卸数方式 : 默认全量
         2: 是否使用MD5 : 默认否
         3: 是否并行抽取 : 默认否
         4: 如果表记录不存在则设置当前表为未采集状态
        */
				//        //        1: 卸数方式 : 默认全量
				//        map.put("unload_type", UnloadType.QuanLiangXieShu.getCode());
				//        2: 是否使用MD5 : 默认否
				map.put("is_md5", IsFlag.Fou.getCode());
				//        3: 是否并行抽取 : 默认否
				map.put("is_parallel", IsFlag.Fou.getCode());
				map.put("collectState", true);
				results.add(map);
			} else {
				List<Object> tableStateList = checkTableCollectState(colSetId, tableName);
				if (tableStateList.contains(ExecuteState.YunXingWanCheng.getCode())
					|| tableStateList.contains(ExecuteState.KaiShiYunXing.getCode())) {
					tableResult.put("collectState", false);
				} else {
					tableResult.put("collectState", true);
				}

				results.add(tableResult);
			}
		}
		// 3、返回
		return results;
	}

	@Method(
		desc = "在修改采集表的时候，更新系统中和采集表ID有外键关系的表，用新的tableId更新旧的tableId",
		logicStep =
			""
				+ "1、更新table_storage_info表对应条目的table_id字段,一个table_id在该表中的数据存在情况为0-1"
				+ "2、更新table_clean表对应条目的table_id字段，一个table_id在table_clean中可能有0-N条数据"
				+ "3、更新data_extraction_def表对应条目的table_id，一个table_id在该表中的数据存在情况为0-N"
				+ "4、更新column_merge表对应条目的table_id字段，一个table_id在该表中的数据存在情况为0-N")
	@Param(name = "newID", desc = "新的tableId", range = "不为空")
	@Param(name = "oldID", desc = "旧的tableId", range = "不为空")
	private void updateTableId(long newID, long oldID) {
		// 1、更新table_storage_info表对应条目的table_id字段,一个table_id在该表中的数据存在情况为0-1
		List<Object> storageIdList =
			Dbo.queryOneColumnList(
				" select storage_id from " + Table_storage_info.TableName + " where table_id = ? ",
				oldID);
		if (storageIdList.size() > 1) {
			throw new BusinessException("表存储信息不唯一");
		}
		if (!storageIdList.isEmpty()) {
			long storageId = (long) storageIdList.get(0);
			DboExecute.updatesOrThrow(
				"更新表存储信息失败",
				"update " + Table_storage_info.TableName + " set table_id = ? where storage_id = ?",
				newID,
				storageId);
		}

		// 2、更新table_clean表对应条目的table_id字段，一个table_id在table_clean中可能有0-N条数据
		List<Object> tableCleanIdList =
			Dbo.queryOneColumnList(
				" select table_clean_id from " + Table_clean.TableName + " where table_id = ? ", oldID);
		if (!tableCleanIdList.isEmpty()) {
			StringBuilder tableCleanBuilder =
				new StringBuilder(
					"update " + Table_clean.TableName + " set table_id = ? where table_clean_id in ( ");
			for (int j = 0; j < tableCleanIdList.size(); j++) {
				tableCleanBuilder.append((long) tableCleanIdList.get(j));
				if (j != tableCleanIdList.size() - 1) {
					tableCleanBuilder.append(",");
				}
			}
			tableCleanBuilder.append(" )");
			Dbo.execute(tableCleanBuilder.toString(), newID);
		}

		List<Object> tableColumnIdList =
			Dbo.queryOneColumnList(
				" select column_id from " + Table_column.TableName + " where table_id = ? ", oldID);
		if (!tableColumnIdList.isEmpty()) {
			StringBuilder tableCleanBuilder =
				new StringBuilder(
					"update " + Table_column.TableName + " set table_id = ? where column_id in ( ");
			for (int j = 0; j < tableColumnIdList.size(); j++) {
				tableCleanBuilder.append((long) tableColumnIdList.get(j));
				if (j != tableColumnIdList.size() - 1) {
					tableCleanBuilder.append(",");
				}
			}
			tableCleanBuilder.append(" )");
			Dbo.execute(tableCleanBuilder.toString(), newID);
		}

		// 3、更新data_extraction_def表对应条目的table_id，一个table_id在该表中的数据存在情况为0-1
		List<Object> extractDefIdList =
			Dbo.queryOneColumnList(
				" select ded_id from " + Data_extraction_def.TableName + " where table_id = ? ", oldID);
		//    if (extractDefIdList.size() > 1) {
		//      throw new BusinessException("数据抽取定义信息不唯一");
		//    }

		if (!extractDefIdList.isEmpty()) {

			StringBuilder data_extraction_def =
				new StringBuilder(
					"update " + Data_extraction_def.TableName + " set table_id = ? where ded_id in ( ");

			for (int j = 0; j < extractDefIdList.size(); j++) {
				data_extraction_def.append(extractDefIdList.get(j));
				if (j != extractDefIdList.size() - 1) {
					data_extraction_def.append(",");
				}
			}
			data_extraction_def.append(" )");

			Dbo.execute(data_extraction_def.toString(), newID);
		}

		// 4、更新column_merge表对应条目的table_id字段，一个table_id在该表中的数据存在情况为0-N
		List<Object> colMergeIdList =
			Dbo.queryOneColumnList(
				" select col_merge_id from " + Column_merge.TableName + " where table_id = ? ", oldID);
		if (!colMergeIdList.isEmpty()) {
			StringBuilder colMergeBuilder =
				new StringBuilder(
					"update " + Column_merge.TableName + " set table_id = ? where col_merge_id in ( ");
			for (int j = 0; j < colMergeIdList.size(); j++) {
				colMergeBuilder.append((long) colMergeIdList.get(j));
				if (j != colMergeIdList.size() - 1) {
					colMergeBuilder.append(",");
				}
			}
			colMergeBuilder.append(" )");
			Dbo.execute(colMergeBuilder.toString(), newID);
		}
	}

	@Method(
		desc = "保存新增自定义SQL采集字段信息",
		logicStep =
			""
				+ "1、根据colSetId和userId去数据库中查出DB连接信息"
				+ "2、封装数据，调用方法和agent交互，获取列信息"
				+ "3、将列信息反序列化为List集合"
				+ "4、遍历List集合，给每个Table_column对象设置主键等信息"
				+ "5、保存")
	@Param(
		name = "tableInfo",
		desc = "存有table_id和自定义采集SQL的实体类对象",
		range = "Table_info实体类对象，" + "其中table_id和sql两个属性不能为空",
		isBean = true)
	@Param(name = "colSetId", desc = "数据库设置ID，源系统数据库设置表主键，数据库对应表外键", range = "不为空")
	@Param(name = "userId", desc = "当前登录用户ID，sys_user表主键", range = "不为空")
	public void saveCustomSQLColumnInfoForAdd(Table_info tableInfo, long colSetId) {

    /*
    　1、根据colSetId和userId去数据库中查出DB连接信息
    　2、封装数据，调用方法和agent交互，获取列信息
    　3、将列信息反序列化为List集合
    */
		Set<Table_column> tableColumns =
			getSqlColumnData(colSetId, tableInfo.getUnload_type(), tableInfo.getSql(), tableInfo.getTable_id(),
				tableInfo.getTable_name());

		//如果表的卸数方式是增量,则校验表的列主键是否选择,否则则抛出异常信息
		if (UnloadType.ofEnumByCode(tableInfo.getUnload_type()) == UnloadType.ZengLiangXieShu) {
			List<Boolean> primary = new ArrayList<>();
			tableColumns.forEach(table_column -> {
				if (IsFlag.ofEnumByCode(table_column.getIs_primary_key()) == IsFlag.Shi) {
					primary.add(true);
				}
			});
			if (!primary.contains(true)) {
				throw new BusinessException("当前表(" + tableInfo.getTable_name() + ")的卸数方式为增量, 未设置主键,请检查");
			}
		}
		// 4、遍历List集合，给每个Table_column对象设置主键等信息
		for (Table_column tableColumn : tableColumns) {
			tableColumn.setColumn_id(PrimayKeyGener.getNextId());
			tableColumn.setTable_id(tableInfo.getTable_id());
			//      // 是否采集设置为是
			//      tableColumn.setIs_get(IsFlag.Shi.getCode());
			//      // 是否是主键，默认设置为否
			//      tableColumn.setIs_primary_key(IsFlag.Fou.getCode());
			//      tableColumn.setValid_s_date(DateUtil.getSysDate());
			//      tableColumn.setValid_e_date(Constant.MAXDATE);
			//      tableColumn.setIs_alive(IsFlag.Shi.getCode());
			//      tableColumn.setIs_new(IsFlag.Fou.getCode());
			//      tableColumn.setTc_or(DEFAULT_COLUMN_CLEAN_ORDER.toJSONString());

			// 5、保存
			tableColumn.add(Dbo.db());
		}
	}

	@Method(
		desc = "删除tableId为外键的表脏数据",
		logicStep =
			""
				+ "1、删除column_id做外键的的表脏数据"
				+ "2、删除旧的tableId在采集字段表中做外键的数据，不关注删除的数目"
				+ "3、删除旧的tableId在数据抽取定义表做外键的数据，不关注删除的数目"
				+ "4、删除旧的tableId在存储信息表做外键的数据，不关注删除的数目，同时，其对应的存储目的地关联关系也要删除"
				+ "5、删除旧的tableId在列合并表做外键的数据，不关注删除的数目"
				+ "6、删除旧的tableId在表清洗规则表做外键的数据，不关注删除的数目")
	@Param(
		name = "tableId",
		desc = "数据库对应表ID，" + "数据抽取定义表、表存储信息表、列合并表、表清洗规则表、表对应字段表表外键",
		range = "不为空")
	private void deleteDirtyDataOfTb(long tableId) {
		// 1、删除column_id做外键的的表脏数据
		List<Object> columnIds =
			Dbo.queryOneColumnList(
				"select column_id from " + Table_column.TableName + " WHERE table_id = ?", tableId);
		if (!columnIds.isEmpty()) {
			for (Object columnId : columnIds) {
				deleteDirtyDataOfCol((long) columnId);
			}
		}
		// 2、删除旧的tableId在采集字段表中做外键的数据，不关注删除的数目
		Dbo.execute(" DELETE FROM " + Table_column.TableName + " WHERE table_id = ? ", tableId);
		// 删除表的抽数作业关系表信息
		Dbo.execute(" DELETE FROM " + Take_relation_etl.TableName + " WHERE ded_id in ("
			+ "SELECT ded_id FROM " + Data_extraction_def.TableName + " WHERE table_id =?) ", tableId);
		// 3、删除旧的tableId在数据抽取定义表做外键的数据，不关注删除的数目
		Dbo.execute(" DELETE FROM " + Data_extraction_def.TableName + " WHERE table_id = ? ", tableId);
		// 4、删除旧的tableId在存储信息表做外键的数据，不关注删除的数目，同时，其对应的存储目的地关联关系也要删除
		Dbo.execute(
			" DELETE FROM "
				+ Dtab_relation_store.TableName
				+ " WHERE tab_id = "
				+ "(SELECT storage_id FROM "
				+ Table_storage_info.TableName
				+ " WHERE table_id = ?) AND data_source = ? ",
			tableId, StoreLayerDataSource.DB.getCode());
		Dbo.execute(" DELETE FROM " + Table_storage_info.TableName + " WHERE table_id = ? ", tableId);
		// 5、删除旧的tableId在列合并表做外键的数据，不关注删除的数目
		Dbo.execute(" DELETE FROM " + Column_merge.TableName + " WHERE table_id = ? ", tableId);
		// 6、删除旧的tableId在表清洗规则表做外键的数据，不关注删除的数目
		Dbo.execute(" DELETE FROM " + Table_clean.TableName + " WHERE table_id = ? ", tableId);

	}

	@Method(
		desc = "删除columnId为外键的表脏数据",
		logicStep =
			""
				+ "1、删除旧的columnId在字段存储信息表中做外键的数据，不关注删除的数目"
				+ "2、删除旧的columnId在列清洗信息表做外键的数据，不关注删除的数目"
				+ "3、删除旧的columnId在列拆分信息表做外键的数据，不关注删除的数目")
	@Param(name = "columnId", desc = "表对应字段表ID，" + "字段存储信息表、列清洗信息表、列拆分信息表外键", range = "不为空")
	private void deleteDirtyDataOfCol(long columnId) {
		// 1、删除旧的columnId在字段存储信息表中做外键的数据，不关注删除的数目 StoreLayerDataSource.DB.getCode() AND data_source = ?
		Dbo.execute("delete from " + Dcol_relation_store.TableName + " where col_id = ? ", columnId);
		// 2、删除旧的columnId在列清洗信息表做外键的数据，不关注删除的数目
		Dbo.execute("delete from " + Column_clean.TableName + " where column_id = ?", columnId);
		// 3、删除旧的columnId在列拆分信息表做外键的数据，不关注删除的数目
		Dbo.execute("delete from " + Column_split.TableName + " where column_id = ?", columnId);
	}

	@Method(desc = "根据表ID获取卸数方式的数据信息", logicStep = "" + "1: 返回数据信息")
	@Param(name = "table_id", desc = "表ID", range = "不为空")
	@Return(desc = "返回当前表的卸数信息", range = "可为空,为空表示为配置过")
	public Optional<Table_info> getTableSetUnloadData(long table_id) {
		return Dbo.queryOneObject(
			Table_info.class,
			"SELECT * FROM " + Table_info.TableName + " WHERE table_id = ?",
			table_id);
	}

	@Method(
		desc = "检查每个表的主键是否存在",
		logicStep =
			"1: 循环获取表的列信息"
				+ "2: 根据返回的列信息,进行检查是否存在着主键,如果存在就将此表的值设为true然后进行下个表的检查,反之所有列检查完毕后为出现主键,则设置值为false")
	@Param(name = "colSetId", desc = "采集任务ID", range = "不可为空")
	@Param(name = "tableNames", desc = "采集表集合数组", range = "不可为空")
	@Param(name = "tableIds", desc = "表的ID信息", range = "不可为空", nullable = true)
	@Return(desc = "返回检查后的表数据信息", range = "不可为空")
	public Map<String, Boolean> checkTablePrimary(
		long colSetId, String[] tableNames, String tableIds) {

		List<Table_column> tableColumns = null;
		Map<String, Long> tableIdMap = null;
		if (StringUtil.isNotBlank(tableIds)) {
			tableIdMap = JSON.parseObject(tableIds, new TypeReference<Map<String, Long>>() {
			});
		}
		//    1: 循环获取表的列信息
		Map<String, Boolean> checkPrimaryMap = new HashMap<>();
		for (String table_name : tableNames) {
			if (tableIdMap != null && StringUtil.isNotBlank(String.valueOf(tableIdMap.get(table_name)))) {
				getCheckPrimaryByTableId(colSetId, table_name, tableIdMap.get(table_name), checkPrimaryMap);
			} else {
				tableColumns = getColumnInfoByTableName(colSetId, getUserId(), table_name);
				//      2: 根据返回的列信息,进行检查是否存在着主键,如果存在就将此表的值设为true然后进行下个表的检查,反之检查完毕设置值为false
				for (Table_column tableColumn : tableColumns) {
					if (IsFlag.ofEnumByCode(tableColumn.getIs_primary_key()) == IsFlag.Shi) {
						checkPrimaryMap.put(table_name, true);
						break;
					} else {
						checkPrimaryMap.put(table_name, false);
					}
				}
			}
		}
		return checkPrimaryMap;
	}

	private void getCheckPrimaryByTableId(
		long colSetId, String tableName, long table_id, Map<String, Boolean> checkPrimaryMap) {
		long countNum =
			Dbo.queryNumber(
				"SELECT COUNT(1) FROM "
					+ Table_column.TableName
					+ " t1 JOIN "
					+ Table_info.TableName
					+ " t2 ON t1.table_id = t2.table_id WHERE t2.database_id = ? AND t2.table_name = ? AND t1.table_id = ?",
				colSetId,
				tableName,
				table_id)
				.orElseThrow(() -> new BusinessException("SQL查询异常"));
		if (countNum == 0) {
			throw new BusinessException("任务( " + colSetId + "),不存在表( " + tableName + " )");
		}

		List<Map<String, Object>> list =
			Dbo.queryList(
				"SELECT (case t1.is_primary_key when ? then 'true' else 'false' end) is_primary_key FROM "
					+ Table_column.TableName
					+ " t1 JOIN "
					+ Table_info.TableName
					+ " t2 ON t1.table_id = t2.table_id WHERE t2.database_id = ? AND t2.table_name = ? AND t1.table_id = ?",
				IsFlag.Shi.getCode(),
				colSetId,
				tableName,
				table_id);
		for (Map<String, Object> map : list) {
			if (map.get("is_primary_key").equals("true")) {
				checkPrimaryMap.put(tableName, true);
				break;
			} else {
				checkPrimaryMap.put(tableName, false);
			}
		}
	}

	@Method(
		desc = "根据SQL获取其中的列信息",
		logicStep =
			""
				+ "2: 检查卸数方式是否选择"
				+ "3: 检查SQL是否存在"
				+ "4: 检查卸数方式,这里根据卸数方式来处理SQL, 因为增量的SQL可能存在三种这里需要对SQL的字段进行去重"
				+ "5: 获取SQL中的字段名称"
				+ "6: 返回处理的字段信息")
	@Param(name = "colSetId", desc = "采集任务ID", range = "不可为空")
	@Param(name = "unloadType", desc = "卸数方式(代码项: UnloadType)", range = "不可为空")
	@Param(name = "sql", desc = "获取列的SQL", range = "不可为空")
	@Param(name = "tableId", desc = "表设置ID", range = "可为空,为空表示未设置过", nullable = true, valueIfNull = "0")
	@Param(name = "tableName", desc = "表名称", range = "不可为空,为空表示未设置过")
	@Return(desc = "返回检查后的表数据信息", range = "不可为空")
	public Set<Table_column> getSqlColumnData(long colSetId, String unloadType, String sql, long tableId,
		String tableName) {

		// 2: 检查卸数方式是否选择
		if (StringUtil.isBlank(unloadType)) {
			throw new BusinessException("请指定卸数方式");
		}

		// 3: 检查SQL是否存在
		if (StringUtil.isBlank(sql)) {
			throw new BusinessException("SQL不能为空");
		}

		// 定义存放增量SQL中的列的集合
		Set<Table_column> columnDataSet = new LinkedHashSet<>();
		//      4: 检查卸数方式,这里根据卸数方式来处理SQL, 因为增量的SQL可能存在三种这里需要对SQL的字段进行去重
		if (UnloadType.ofEnumByCode(unloadType) == UnloadType.ZengLiangXieShu) {

			// 解析增量SQL的数据信息
			Map<String, Object> incrementSqlMap =
				JsonUtil.toObjectSafety(sql, Map.class)
					.orElseThrow(() -> new BusinessException("增量SQL解析出现错误"));

			// 找出增量sql中不重复的列
			incrementSqlMap.forEach(
				(k, v) -> {
					if (v != null && StringUtil.isNotBlank(v.toString())) {
						//      5: 获取SQL中的字段名称
						getTableColumns(colSetId, v.toString(), columnDataSet);
					}
				});
		} else {
			getTableColumns(colSetId, sql, columnDataSet);
		}

		if (tableId != 0) {
			Map<String, Object> columnInfo = getColumnInfo(tableName, colSetId, tableId);
			List<Table_column> tableColumnList = (List<Table_column>) columnInfo.get("columnInfo");
			List<String> collect = tableColumnList.stream().map(Table_column::getColumn_name).collect(Collectors.toList());
			columnDataSet.removeIf(item -> collect.contains(item.getColumn_name()));
			columnDataSet.addAll(tableColumnList);
		}

		return columnDataSet;
	}

	@Method(
		desc = "根据SQL获取表的字段信息",
		logicStep =
			""
				+ "1: 根据colSetId和userId去数据库中查出DB连接信息"
				+ "2: 封装数据，调用方法和agent交互，获取列信息"
				+ "3: 将列信息反序列化为List集合")
	@Param(name = "colSetId", desc = "采集任务ID", range = "不可为空")
	@Param(name = "sql", desc = "需要获取列的SQL", range = "不可为空")
	@Return(desc = "返回SQL的列信息", range = "不为空的列集合")
	private void getTableColumns(long colSetId, String sql, Set<Table_column> tableColumnSet) {
		// 1、根据colSetId和userId去数据库中查出DB连接信息
		Map<String, Object> databaseInfo = getDatabaseSetInfo(colSetId, getUserId());

		// 2、封装数据，调用方法和agent交互，获取列信息
		String respMsg =
			SendMsgUtil.getCustColumn(
				((long) databaseInfo.get("agent_id")),
				getUserId(),
				databaseInfo,
				sql,
				AgentActionUtil.GETCUSTCOLUMN);
		// 3、将列信息反序列化为List集合
		List<Table_column> tableColumnList =
			JSON.parseObject(respMsg, new TypeReference<List<Table_column>>() {
			});
		tableColumnList.forEach(
			table_column -> {
				table_column.setIs_get(IsFlag.Shi.getCode());
				table_column.setIs_primary_key(IsFlag.Fou.getCode());
				table_column.setIs_new(IsFlag.Fou.getCode());
				table_column.setTc_or(DEFAULT_COLUMN_CLEAN_ORDER.toJSONString());
				table_column.setIs_alive(IsFlag.Shi.getCode());
				table_column.setIs_new(IsFlag.Fou.getCode());
				table_column.setValid_s_date(DateUtil.getSysDate());
				table_column.setValid_e_date(VALID_S_DATE);
			});
		tableColumnSet.addAll(tableColumnList);
	}

	@Method(desc = "检查表的采集状态信息", logicStep = "1: 检查表名是否存在 2: 返回表的采集信息集合")
	@Param(name = "colSetId", desc = "采集任务ID", range = "不可为空")
	@Param(name = "table_name", desc = "表名称", range = "不可为空")
	@Return(desc = "返回表的采集状态集合", range = "可以为空,为空表示表未采集过")
	private List<Object> checkTableCollectState(long colSetId, Object table_name) {

		//    1: 检查表名是否存在
		if (Dbo.queryNumber(
			"SELECT COUNT(1) FROM "
				+ Table_info.TableName
				+ " WHERE database_id = ? AND table_name = ?",
			colSetId,
			table_name)
			.orElseThrow(() -> new BusinessException("SQL查询错误")) == 0) {
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
