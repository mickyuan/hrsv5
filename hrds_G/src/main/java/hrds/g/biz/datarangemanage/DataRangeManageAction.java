package hrds.g.biz.datarangemanage;

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
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.CollectType;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.JobExecuteState;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.TreeNodeInfo;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.commons.TreePageSource;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.commons.utils.tree.Node;
import hrds.commons.utils.tree.NodeDataConvertedTreeList;
import hrds.g.biz.bean.TableDataInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "接口数据范围管理接口", author = "dhw", createdate = "2020/3/25 17:53")
public class DataRangeManageAction extends BaseAction {
	// 有效结束日期
	public static final String END_DATE = "99991231";

	private static final String KAFKA = "kafka";

	private static final Type MAPTYPE = new TypeReference<Map<String, Object>>() {
	}.getType();
	private static final Logger logger = LogManager.getLogger();

	@Method(desc = "查询数据使用范围信息", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.配置树不显示文件采集的数据" +
			"3.根据源菜单信息获取节点数据列表" +
			"4.转换节点数据列表为分叉树列表" +
			"5.定义返回的分叉树结果Map")
	@Return(desc = "返回的分叉树结果Map", range = "无限制")
	public Map<String, Object> searchDataUsageRangeInfoToTreeData() {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		TreeConf treeConf = new TreeConf();
		// 2.配置树不显示文件采集的数据
		treeConf.setShowFileCollection(Boolean.FALSE);
		// 3.根据源菜单信息获取节点数据列表
		List<Map<String, Object>> dataList = TreeNodeInfo.getTreeNodeInfo(TreePageSource.INTERFACE, getUser(),
				treeConf);
		// 4.转换节点数据列表为分叉树列表
		List<Node> interfaceTreeList = NodeDataConvertedTreeList.dataConversionTreeInfo(dataList);
		// 5.定义返回的分叉树结果Map
		Map<String, Object> interfaceTreeDataMap = new HashMap<>();
		interfaceTreeDataMap.put("interfaceTreeList", JsonUtil.toObjectSafety(interfaceTreeList.toString(),
				List.class));
		return interfaceTreeDataMap;
	}

	@Method(desc = "查询Agent采集的所有表信息", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.判断ID与table_id是否都为空" +
			"3.判断属于哪个层的数据，做不同的查询" +
			"3.1贴源层" +
			"3.2集市层" +
			"3.3加工层" +
			"3.4自定义层" +
			"3.5系统层" +
			"4.返回表数据信息结果集")
	@Param(name = "id", desc = "table_id上一级", range = "无限制", nullable = true)
	@Param(name = "file_id", desc = "树最后一层节点", range = "无限制", nullable = true)
	@Param(name = "dataSourceType", desc = "数据源类型(使用DataSourceType）代码项，树根节点", range = "无限制")
	@Param(name = "table_space", desc = "自定义层查询时传递", range = "无限制", nullable = true)
	@Return(desc = "返回查询表信息", range = "无限制")
	public Result searchCollectTableInfo(Long id, String file_id, String dataSourceType
			, String table_space) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.判断ID与table_id是否都为空
		if (id == null && file_id == null) {
			throw new BusinessException("classify_id与collect_set_id不能都为空");
		}
		Result tableResult;
		// 3.判断属于哪个层的数据，做不同的查询
		if (DataSourceType.ofEnumByCode(dataSourceType) == DataSourceType.DCL) {
			// 3.1贴源层
			tableResult = getDCLResult(id, file_id, dataSourceType);
		} else if (DataSourceType.ofEnumByCode(dataSourceType) == DataSourceType.DML) {
			// 3.2集市层
//			getDMLResult(id, table_id, dataSourceType);
			throw new BusinessException("暂时未开发此功能，待续...");
		} else if (DataSourceType.ofEnumByCode(dataSourceType) == DataSourceType.DPL) {
			// 3.3加工层
//			getDPLResult(id, table_id, dataSourceType);
			throw new BusinessException("暂时未开发此功能，待续...");
		} else if (DataSourceType.ofEnumByCode(dataSourceType) == DataSourceType.UDL) {
			// 3.4自定义层
//			getUDLResult(id, table_id, dataSourceType, table_space);
			throw new BusinessException("暂时未开发此功能，待续...");
		} else if (DataSourceType.ofEnumByCode(dataSourceType) == DataSourceType.SFL) {
			// 3.5系统层
			throw new BusinessException("暂时未开发此功能，待续...");
		} else {
			throw new BusinessException("暂时未开发此功能，待续...");
		}
		// 4.返回表数据信息结果集
		return tableResult;
	}

	@Method(desc = "获取自定义层表信息结果集", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.判断table_id是否为空，为空判断表空间是否为空，不为空根据表空间查询，不为空根据table_id查询" +
			"3.查询自定义层表信息" +
			"4.封装表数据到结果集" +
			"5.返回自定义层表信息结果集")
	@Param(name = "id", desc = "table_id上一级", range = "无限制", nullable = true)
	@Param(name = "table_id", desc = "树最后一层节点", range = "无限制", nullable = true)
	@Param(name = "dataSourceType", desc = "数据源类型(使用DataSourceType）代码项，树根节点", range = "无限制")
	private Result getUDLResult(Long id, Long table_id, String dataSourceType, String table_space) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		SqlOperator.Assembler assembler = SqlOperator.Assembler.newInstance();
		assembler.clean();
		assembler.addSql("select table_name,info_id as file_id,ch_name as original_name," +
				"'" + dataSourceType + "' as source_type,'" + table_space + "' as table_space '"
				+ " from sys_table_info WHERE table_type = ?").addParam(id);
		// 2.判断table_id是否为空，为空判断表空间是否为空，不为空根据表空间查询，不为空根据table_id查询
		if (table_id == null) {
			if (StringUtil.isNotBlank(table_space)) {
				assembler.addSql(" and table_space=?").addParam(table_space);
			}
		} else {
			assembler.addSql(" and info_id=?").addParam(table_id);
		}
		// 3.查询自定义层表信息
		Result tableResult = Dbo.queryResult(assembler.sql(), assembler.params());
		// 4.封装表数据到结果集
		setTableParamToResult(tableResult, dataSourceType);
		// 5.返回自定义层表信息结果集
		return tableResult;
	}

	@Method(desc = "获取加工层表信息结果集", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.判断ID是否为空，为空则为分类触发，不为空为表触发" +
			"3.查询加工层表信息" +
			"4.封装表数据到结果集" +
			"5.返回加工层表信息结果集")
	@Param(name = "id", desc = "table_id上一级", range = "无限制", nullable = true)
	@Param(name = "table_id", desc = "树最后一层节点", range = "无限制", nullable = true)
	@Param(name = "dataSourceType", desc = "数据源类型(使用DataSourceType）代码项，树根节点", range = "无限制")
	private Result getDPLResult(Long id, Long table_id, String dataSourceType) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		SqlOperator.Assembler assembler = SqlOperator.Assembler.newInstance();
		assembler.clean();
		Result tableResult;
		assembler.addSql("SELECT table_id as file_id,ctname as original_name," +
				"ST_DT as original_update_date,st_time as original_update_time,tabname as table_name " +
				" FROM edw_table WHERE end_dt =?").addParam(END_DATE);
		// 2.判断ID是否为空，为空则为分类触发，不为空为表触发
		if (id != null) {
			assembler.addSql("  and category_id=?").addParam(id);
		} else {
			assembler.addSql("  and table_id=?").addParam(table_id);
		}
		// 3.查询加工层表信息
		tableResult = Dbo.queryResult(assembler.sql(), assembler.params());
		// 4.封装表数据到结果集
		setTableParamToResult(tableResult, dataSourceType);
		// 5.返回加工层表信息结果集
		return tableResult;
	}

	@Method(desc = "获取集市层表信息结果集", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.判断ID是否为空，为空则为分类触发，不为空为表触发" +
			"3.查询集市层表信息" +
			"4.封装表数据到结果集" +
			"5.返回集市层表信息结果集")
	@Param(name = "id", desc = "table_id上一级", range = "无限制", nullable = true)
	@Param(name = "table_id", desc = "树最后一层节点", range = "无限制", nullable = true)
	@Param(name = "dataSourceType", desc = "数据源类型(使用DataSourceType）代码项，树根节点", range = "无限制")
	private Result getDMLResult(Long id, Long table_id, String dataSourceType) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		SqlOperator.Assembler assembler = SqlOperator.Assembler.newInstance();
		assembler.clean();
		assembler.addSql("SELECT datatable_id as file_id,datatable_en_name as table_name, " +
				"datatable_create_date as original_update_date,datatable_create_time as original_update_time," +
				"datatable_cn_name as sysreg_name,datatable_en_name as hbase_name  from "
				+ Dm_datatable.TableName + " WHERE datatable_due_date >= ? " +
				" AND (? IN (hy_success,elk_success,kv_success,solr_success,solrbase_success," +
				"carbondata_success)) and datatype=?").addParam(DateUtil.getSysDate())
				.addParam(JobExecuteState.WanCheng.getCode()).addParam(IsFlag.Shi.getCode());
		// 2.判断ID是否为空，为空则为分类触发，不为空为表触发
		if (id != null) {
			// 分类触发
			assembler.addSql(" and data_mart_id=?").addParam(id);
		} else {
			// 表触发
			assembler.addSql(" where datatable_id=?").addParam(table_id);
		}
		// 3.查询集市层表信息
		Result tableResult = Dbo.queryResult(assembler.sql(), assembler.params());
		// 4.封装表数据到结果集
		setTableParamToResult(tableResult, dataSourceType);
		// 5.返回集市层表信息结果集
		return tableResult;
	}

	@Method(desc = "获取贴源层表信息结果集", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.判断ID是否为空，为空则为分类触发，不为空为表触发" +
			"3.查询贴源层表信息" +
			"4.封装表数据到结果集" +
			"5.返回贴源层表信息结果集")
	@Param(name = "id", desc = "table_id上一级", range = "无限制", nullable = true)
	@Param(name = "file_id", desc = "树最后一层节点", range = "无限制", nullable = true)
	@Param(name = "dataSourceType", desc = "数据源类型(使用DataSourceType）代码项，树根节点", range = "无限制")
	private Result getDCLResult(Long id, String file_id, String dataSourceType) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		SqlOperator.Assembler assembler = SqlOperator.Assembler.newInstance();
		assembler.clean();
		assembler.addSql("SELECT t1.file_id,t1.original_name,t1.original_update_date," +
				"t1.original_update_time,t1.table_name,t1.hyren_name from "
				+ Data_store_reg.TableName + " t1 ");
		// 2.判断ID是否为空，为空则为分类触发，不为空为表触发
		if (id == null && file_id != null) {
			// 表触发
			assembler.addSql(" WHERE t1.file_id = ? ").addParam(file_id);
		} else {
			// 分类触发
			assembler.addSql(" join " + Database_set.TableName + " t2 ON t1.database_id=t2.database_id" +
					" where t2.classify_id = ?").addParam(id);
		}
		// 3.查询贴源层表信息
		Result tableResult = Dbo.queryResult(assembler.sql(), assembler.params());
		// 4.封装表数据到结果集
		setTableParamToResult(tableResult, dataSourceType);
		// 5.返回贴源层表信息结果集
		return tableResult;
	}

	@Method(desc = "封装表数据到结果集", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.封装表数据到结果集")
	@Param(name = "tableResult", desc = "表信息结果集", range = "无限制")
	@Param(name = "dataSourceType", desc = "数据源类型(使用DataSourceType）代码项，树根节点", range = "无限制")
	private void setTableParamToResult(Result tableResult, String dataSourceType) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.封装表数据到结果集
		for (int i = 0; i < tableResult.getRowCount(); i++) {
			tableResult.setObject(i, "original_update_date", DateUtil.parseStr2DateWith8Char(
					tableResult.getString(i, "original_update_date")));
			tableResult.setObject(i, "original_update_time", DateUtil.parseStr2TimeWith6Char(
					tableResult.getString(i, "original_update_time")));
			tableResult.setObject(i, "dataSourceType", dataSourceType);
		}
	}

	@Method(desc = "保存数据表数据", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制")
	@Param(name = "tableDataInfos", desc = "表数据信息对象数组", range = "无限制", isBean = true)
	@Param(name = "table_note", desc = "表数据信息对象数组", range = "无限制", nullable = true)
	@Param(name = "dataSourceType", desc = "数据源类型(使用DataSourceType）代码项", range = "无限制")
	@Param(name = "user_id", desc = "用户ID", range = "无限制")
	public void saveTableData(TableDataInfo[] tableDataInfos, String table_note, String dataSourceType,
	                          long[] user_id) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		for (TableDataInfo tableDataInfo : tableDataInfos) {
			if (DataSourceType.DCL == DataSourceType.ofEnumByCode(dataSourceType)) {
				saveDCLData(table_note, tableDataInfo.getFile_id(), dataSourceType,
						tableDataInfo.getColumn_name(), user_id);
			} else if (DataSourceType.DML == DataSourceType.ofEnumByCode(dataSourceType)) {
				throw new BusinessException("该数据源类型还未开发，待续。。。" + dataSourceType);
			} else if (DataSourceType.DPL == DataSourceType.ofEnumByCode(dataSourceType)) {
				throw new BusinessException("该数据源类型还未开发，待续。。。" + dataSourceType);
			} else if (DataSourceType.SFL == DataSourceType.ofEnumByCode(dataSourceType)) {
				throw new BusinessException("该数据源类型还未开发，待续。。。" + dataSourceType);
			} else if (DataSourceType.UDL == DataSourceType.ofEnumByCode(dataSourceType)) {
				throw new BusinessException("该数据源类型还未开发，待续。。。" + dataSourceType);
			} else {
				throw new BusinessException("该数据源类型还未开发，待续。。。" + dataSourceType);
			}
		}
	}

	@Method(desc = "保存贴源层数据", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.查询贴源层表数据信息" +
			"3.遍历贴源层表数据信息保存表使用信息以及系统登记表参数信息" +
			"4.获取原始登记表名称" +
			"5.获取原始文件名称" +
			"6.根据用户ID、表名查询当前表是否已登记" +
			"7.生成表使用ID" +
			"8.判断当前用户对应表是否已登记做不同处理" +
			"8.1根据用户ID、表名删除接口表数据" +
			"9.新增表使用信息" +
			"10.保存系统登记表参数信息")
	@Param(name = "userId", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "table_note", desc = "备注", range = "无限制")
	@Param(name = "file_id", desc = "文件ID", range = "无限制")
	@Param(name = "dataSourceType", desc = "数据源类型(使用DataSourceType）代码项，树根节点", range = "无限制")
	@Param(name = "column_name", desc = "被选中的文件名称", range = "无限制")
	private void saveDCLData(String table_note, String file_id, String dataSourceType, String[] column_name,
	                         long[] user_id) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.查询贴源层表数据信息
		Result tableResult = Dbo.queryResult("SELECT hyren_name,meta_info,original_name FROM " +
				Data_store_reg.TableName + " WHERE file_id = ?", file_id);
		for (long userId : user_id) {
			Table_use_info table_use_info = new Table_use_info();
			// 3.遍历贴源层表数据信息保存表使用信息以及系统登记表参数信息
			for (int i = 0; i < tableResult.getRowCount(); i++) {
				// 4.获取系统内对应表名
				String hyren_name = tableResult.getString(i, "hyren_name");
				// 5.获取原始文件名称
				String original_name = tableResult.getString(i, "original_name");
				// 6.根据用户ID、表名查询当前表是否已登记
				boolean flag = getUserTableInfo(userId, hyren_name);
				// 7.生成表使用ID
				String useId = PrimayKeyGener.getNextId();
				// 8.判断当前用户对应表是否已登记做不同处理
				if (flag) {
					// 8.1已登记,根据用户ID、表名删除接口表数据
					deleteInterfaceTableInfo(userId, hyren_name);
				}
				// 9.新增表使用信息
				addTableUseInfo(table_note, dataSourceType, userId, useId, table_use_info,
						hyren_name, original_name);
				// 10.新增系统登记表参数信息
				addSysregParameterInfo(tableResult.getString(i, "meta_info"),
						column_name, useId, userId);
			}
		}
	}

	@Method(desc = "新增系统登记参数表数据", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			" 2.封装系统登记参数表信息" +
			"3.元数据并解析" +
			"4.获取相同的列类型参数信息" +
			"5.保存系统登记表参数信息")
	@Param(name = "meta_info", desc = "元数据信息", range = "无限制")
	@Param(name = "column_name", desc = "被选中的文件名称", range = "无限制")
	@Param(name = "useId", desc = "表使用ID", range = "新增表使用信息时生成")
	@Param(name = "userId", desc = "用户ID", range = "新增用户时生成")
	private void addSysregParameterInfo(String meta_info, String[] column_name, String useId, long userId) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.封装系统登记参数表信息
		Sysreg_parameter_info sysreg_parameter_info = new Sysreg_parameter_info();
		if (column_name != null && column_name.length != 0) {
			for (String columnName : column_name) {
				sysreg_parameter_info.setParameter_id(PrimayKeyGener.getNextId());
				sysreg_parameter_info.setUse_id(useId);
				sysreg_parameter_info.setIs_flag(IsFlag.Fou.getCode());
				sysreg_parameter_info.setUser_id(userId);
				// 3.元数据并解析
				if (StringUtil.isBlank(meta_info)) {
					throw new BusinessException("当前表对应的meta信息为空");
				}
				Map<String, Object> metaInfoMap = JsonUtil.toObject(meta_info, MAPTYPE);
				Object column = metaInfoMap.get("column");
				Object type = metaInfoMap.get("type");
				if (StringUtil.isBlank(columnName)) {
					if (column == null) {
						throw new BusinessException("当前表对应的meta信息没有列信息");
					}
					columnName = column.toString();
					sysreg_parameter_info.setTable_column_name(column.toString());
				} else {
					sysreg_parameter_info.setTable_column_name(columnName.toUpperCase());
				}
				// 4.获取相同的列类型参数信息
				if (type != null) {
					Map<String, String> columnTypeMap = getColumnType(columnName, column.toString(), type.toString());
					sysreg_parameter_info.setRemark(JsonUtil.toJson(columnTypeMap).toUpperCase());
				}
				// 5.保存系统登记表参数信息
				sysreg_parameter_info.add(Dbo.db());
			}
		}
	}

	@Method(desc = "新增表使用信息", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.封装表使用信息参数" +
			"3.新增保存表使用信息")
	@Param(name = "table_note", desc = "备注", range = "无限制")
	@Param(name = "dataSourceType", desc = "数据源类型(使用DataSourceType）代码项，树根节点", range = "无限制")
	@Param(name = "userId", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "useId", desc = "表使用ID", range = "新增表使用信息时生成")
	@Param(name = "table_use_info", desc = "表使用信息实体", range = "与数据库对应表规则一致", isBean = true)
	@Param(name = "hyren_name", desc = "原始登记表名", range = "")
	@Param(name = "original_name", desc = "原始文件名", range = "")
	private void addTableUseInfo(String table_note, String dataSourceType, long userId, String useId,
	                             Table_use_info table_use_info, String hyren_name, String original_name) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.封装表使用信息参数
		table_use_info.setSysreg_name(hyren_name);
		table_use_info.setUser_id(userId);
		table_use_info.setUse_id(useId);
		table_use_info.setTable_blsystem(dataSourceType);
		if (StringUtil.isBlank(original_name)) {
			table_use_info.setOriginal_name(hyren_name);
		} else {
			table_use_info.setOriginal_name(original_name);
		}
		if (StringUtil.isBlank(table_note)) {
			table_use_info.setTable_note("");
		} else {
			table_use_info.setTable_note(table_note);
		}
		// 3.新增保存表使用信息
		table_use_info.add(Dbo.db());
	}

	@Method(desc = "获取列类型", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.遍历获取新增的列类型" +
			"3.返回新增的列类型")
	@Param(name = "checkedColumns", desc = "已选择的column，‘|’连接", range = "无限制")
	@Param(name = "columns", desc = "所有的column，逗号连接", range = "无限制")
	@Param(name = "type", desc = "所有的type，逗号连接", range = "无限制")
	@Return(desc = "返回列类型集合数据", range = "无限制")
	private Map<String, String> getColumnType(String column, String columns, String type) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		Map<String, String> columnTypeMap = new HashMap<>();
		String[] checkedColumnsSplit = column.split(",");
		String[] wholeColumnsSplit = columns.split(",");
		String[] wholeTypesSplit = type.split("\\|");
		// 2.遍历获取新增的列类型
		for (String checkedColumn : checkedColumnsSplit) {
			for (int i = 0; i < wholeColumnsSplit.length; i++) {
				if (checkedColumn.equalsIgnoreCase(wholeColumnsSplit[i])) {
					columnTypeMap.put(checkedColumn, wholeTypesSplit[i]);
					break;
				}
			}
		}
		// 3.返回新增的列类型
		return columnTypeMap;
	}

	@Method(desc = "根据ID与数据源类型获取结果集(实时数据）",
			logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制")
	@Param(name = "fileId", desc = "文件ID", range = "无限制")
	@Param(name = "dataSourceType", desc = "数据源类型(使用DataSourceType）代码项，树根节点", range = "无限制")
	@Return(desc = "", range = "")
	private Result getResult(long fileId, String dataSourceType) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		Map<String, String> columnMap = new HashMap<>();
		Result tableResult;
		if (KAFKA.equalsIgnoreCase(dataSourceType)) {
			// 实时数据
			tableResult = Dbo.queryResult("select table_en_name as hbase_name,table_cn_name as " +
					"original_name" +
					" from sdm_inner_table t1  where t1.table_id = ?", fileId);
			Result columnResult = Dbo.queryResult("select t1.field_en_name,t1.field_cn_name from " +
					"sdm_inner_column t1 " +
					" where t1.table_id = ?", fileId);
			if (!columnResult.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < columnResult.getRowCount(); i++) {
					builder.append(columnResult.getString(i, "field_en_name")).append(',');
				}
				builder.deleteCharAt(builder.length() - 1);
				columnMap.put("column", builder.toString());
			}
			tableResult.setObject(0, "meta_info", columnMap);
			return tableResult;
		} else {
			throw new BusinessException("暂时不支持此数据源类型的数据，dataSourceType=" + dataSourceType);
		}
	}

	@Method(desc = "根据用户ID、表名查询当前表是否已登记",
			logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
					"2.根据用户ID、表名查询表使用信息是否存在")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "sysreg_name", desc = "系统登记表名", range = "无限制")
	@Return(desc = "返回当前用户对应的表是否已登记标志", range = "false代表未登记，true代表已登记")
	public boolean getUserTableInfo(long user_id, String sysreg_name) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.根据用户ID、表名查询当前表是否已登记
		if (Dbo.queryNumber("select count(1) from " + Table_use_info.TableName
				+ " where user_id = ? and sysreg_name = ?", user_id, sysreg_name)
				.orElseThrow(() -> new BusinessException("sql查询错误")) > 1) {
			// 已登记
			logger.info("此表已登记");
			return true;
		} else {
			// 未登记
			logger.info("此表未登记");
			return false;
		}
	}

	@Method(desc = "根据用户ID、表名删除接口表数据",
			logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
					"2.先删除Sysreg_parameter_info" +
					"3.再删除table_use_info")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "hbase_name", desc = "表名", range = "无限制")
	public void deleteInterfaceTableInfo(long user_id, String hbase_name) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.先删除Sysreg_parameter_info
		Dbo.execute("delete from " + Sysreg_parameter_info.TableName + " where use_id = " +
						"(select use_id from  table_use_info where lower(hbase_name)=lower(?) and user_id=?)"
				, hbase_name, user_id);
		// 3.再删除table_use_info
		Dbo.execute("delete from " + Table_use_info.TableName + " where lower(hbase_name) = lower(?)" +
				" and user_id = ?", hbase_name, user_id);
	}

	@Method(desc = "根据ID查询列信息", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.根据不同数据源类型查询表的列信息并返回" +
			"2.1贴源层" +
			"2.2集市层")
	@Param(name = "file_id", desc = "表ID", range = "无限制", nullable = true)
	@Param(name = "dataSourceType", desc = "数据源类型(使用DataSourceType）代码项，树根节点", range = "无限制")
	@Return(desc = "根据不同数据源类型查询表的列信息并返回", range = "无限制")
	public Result searchFieldById(String file_id, String dataSourceType) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.根据不同数据源类型查询表的列信息并返回
		if (DataSourceType.DCL == DataSourceType.ofEnumByCode(dataSourceType)) {
			// 2.1贴源层
			Map<String, Object> dataStoreMap = Dbo.queryOneObject("SELECT hyren_name,collect_type," +
					" database_id FROM " + Data_store_reg.TableName + " WHERE file_id = ?", file_id);
			String hyren_name = dataStoreMap.get("hyren_name").toString();
			String collect_type = dataStoreMap.get("collect_type").toString();
			String database_id = dataStoreMap.get("database_id").toString();
			Result columnResult = Dbo.queryResult("SELECT column_name as field_en_name,column_ch_name as " +
							" field_cn_name,ti.table_id,tc.column_id,dsr.file_id FROM " + Table_column.TableName +
							"  tc join " + Table_info.TableName + " ti ON tc.table_id = ti.table_id " +
							" join " + Data_store_reg.TableName + " dsr ON dsr.table_name= ti.table_name "
							+ " WHERE dsr.database_id = ti.database_id and lower(dsr.hyren_name) = lower(?) "
							+ "  and ti.valid_e_date = ? AND tc.is_get = ? and tc.is_alive = ?", hyren_name,
					END_DATE, IsFlag.Shi.getCode(), IsFlag.Shi.getCode());
			if (CollectType.DuiXiangWenJianCaiJi == CollectType.ofEnumByCode(collect_type)) {
				columnResult = Dbo.queryResult("SELECT coll_name AS field_en_name,data_desc AS field_cn_name FROM " +
						Object_collect_struct.TableName + " c JOIN " + Object_collect_task.TableName +
						" t ON c.ocs_id = t.ocs_id WHERE t.odc_id = ?", database_id);
			}
			return columnResult;
		} else if (DataSourceType.DML == DataSourceType.ofEnumByCode(dataSourceType)) {
			// 2.2集市层
			return Dbo.queryResult("SELECT field_en_name,field_cn_name FROM " + Datatable_field_info.TableName
					+ " WHERE datatable_id = ?");
		} else {
			throw new BusinessException("待开发，目前只支持贴源层与集市层");
		}
	}
}
