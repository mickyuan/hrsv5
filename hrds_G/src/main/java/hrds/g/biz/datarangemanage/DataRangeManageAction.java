package hrds.g.biz.datarangemanage;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.CollectType;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.TreeNodeInfo;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.commons.TreePageSource;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.commons.utils.tree.Node;
import hrds.commons.utils.tree.NodeDataConvertedTreeList;
import hrds.g.biz.bean.TableDataInfo;
import hrds.g.biz.init.InterfaceManager;
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
	public Object searchDataUsageRangeInfoToTreeData() {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		TreeConf treeConf = new TreeConf();
		// 2.配置树不显示文件采集的数据
		treeConf.setShowFileCollection(Boolean.FALSE);
		// 3.根据源菜单信息获取节点数据列表
		List<Map<String, Object>> dataList = TreeNodeInfo.getTreeNodeInfo(TreePageSource.INTERFACE, getUser(),
				treeConf);
		// 4.转换节点数据列表为分叉树列表
		List<Node> interfaceTreeList = NodeDataConvertedTreeList.dataConversionTreeInfo(dataList);
		return JsonUtil.toObjectSafety(interfaceTreeList.toString(), Object.class).orElseThrow(() ->
				new BusinessException("树数据转换格式失败！"));
	}

	@Method(desc = "保存数据表数据", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制")
	@Param(name = "tableDataInfos", desc = "表数据信息对象数组", range = "无限制", isBean = true)
	@Param(name = "table_note", desc = "表数据信息对象数组", range = "无限制", nullable = true)
	@Param(name = "data_layer", desc = "数据层", range = "无限制")
	@Param(name = "user_id", desc = "用户ID", range = "无限制")
	public void saveTableData(TableDataInfo[] tableDataInfos, String table_note, String data_layer,
	                          long[] user_id) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		for (TableDataInfo tableDataInfo : tableDataInfos) {
			if (DataSourceType.DCL == DataSourceType.ofEnumByCode(data_layer)) {
				// 2.保存贴源层数据
				saveDCLData(table_note, tableDataInfo.getFile_id(), data_layer,
						tableDataInfo.getColumn_name(), user_id);
			} else if (DataSourceType.DML == DataSourceType.ofEnumByCode(data_layer)) {
				// 3.保存集市层数据
				for (long userId : user_id) {
					List<Dm_datatable> dmDataTables = Dbo.queryList(Dm_datatable.class,
							"SELECT datatable_en_name,datatable_cn_name FROM " + Dm_datatable.TableName +
									" WHERE datatable_id = ?", tableDataInfo.getFile_id());
					for (Dm_datatable dmDataTable : dmDataTables) {
						// 删除接口表信息
						deleteInterfaceTableInfo(userId, dmDataTable.getDatatable_en_name());
						Table_use_info tableUseInfo = new Table_use_info();
						tableUseInfo.setTable_blsystem(data_layer);
						// 生成主键,并记录
						String useId = PrimayKeyGener.getNextId();
						tableUseInfo.setUse_id(useId);
						if (StringUtil.isBlank(dmDataTable.getDatatable_en_name())) {
							tableUseInfo.setOriginal_name(dmDataTable.getDatatable_en_name());
						} else {
							tableUseInfo.setOriginal_name(dmDataTable.getDatatable_cn_name());
						}
						if (StringUtil.isBlank(table_note)) {
							tableUseInfo.setTable_note("");
						} else {
							tableUseInfo.setTable_note(table_note);
						}
						tableUseInfo.add(Dbo.db());
						// 获取列信息
						List<Datatable_field_info> fieldInfos = Dbo.queryList(Datatable_field_info.class,
								"SELECT field_en_name,field_type FROM " + Datatable_field_info.TableName
										+ " WHERE datatable_id = ?", tableDataInfo.getFile_id());
						Datatable_field_info field_info = new Datatable_field_info();
						StringBuilder wholeColumns = new StringBuilder();
						StringBuilder wholeTypes = new StringBuilder();
						for (Datatable_field_info fieldInfo : fieldInfos) {
							wholeColumns.append(fieldInfo.getField_en_name()).append(",");
							wholeTypes.append(fieldInfo.getField_type()).append("|");
						}
						Map<String, String> columnType;
						StringBuilder checkedColumns = new StringBuilder();
						if (StringUtil.isBlank(tableDataInfo.getFile_id())) {
							for (Datatable_field_info fieldInfo : fieldInfos) {
								checkedColumns.append(fieldInfo.getField_en_name()).append(",");
							}
							columnType = getColumnType(checkedColumns.toString(), wholeColumns.toString(),
									wholeTypes.toString());
						} else {
							columnType = getColumnType(String.join(",", tableDataInfo.getColumn_name()),
									wholeColumns.toString(), wholeTypes.toString());
						}
						// 保存sysreg信息表
						Sysreg_parameter_info parameter_info = new Sysreg_parameter_info();
						// 主键
						parameter_info.setParameter_id(PrimayKeyGener.getNextId());
						// 使用ID
						parameter_info.setUse_id(useId);
						// 是否标识
						parameter_info.setIs_flag(IsFlag.Fou.getCode());
						// 用户ID
						parameter_info.setUser_id(userId);
						// 获取列信息
						parameter_info.setTable_column_name(checkedColumns.toString().toUpperCase());
						parameter_info.setRemark(JsonUtil.toJson(columnType).toUpperCase());
						parameter_info.add(Dbo.db());
					}
				}
			} else {
				throw new BusinessException("该数据层还未开发，待续。。。" + data_layer);
			}
			InterfaceManager.userTableInfo();
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
	@Param(name = "data_layer", desc = "数据层，树根节点", range = "无限制")
	@Param(name = "column_name", desc = "被选中的文件名称", range = "无限制")
	private void saveDCLData(String table_note, String file_id, String data_layer, String[] column_name,
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
				addTableUseInfo(table_note, data_layer, userId, useId, table_use_info,
						hyren_name, original_name);
				// 10.新增系统登记表参数信息
				addSysRegParameterInfo(tableResult.getString(i, "meta_info"),
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
	private void addSysRegParameterInfo(String meta_info, String[] column_name, String useId, long userId) {
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
	@Param(name = "data_layer", desc = "数据层，树根节点", range = "无限制")
	@Param(name = "userId", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "useId", desc = "表使用ID", range = "新增表使用信息时生成")
	@Param(name = "table_use_info", desc = "表使用信息实体", range = "与数据库对应表规则一致", isBean = true)
	@Param(name = "hyren_name", desc = "原始登记表名", range = "无限制")
	@Param(name = "original_name", desc = "原始文件名", range = "无限制")
	private void addTableUseInfo(String table_note, String data_layer, long userId, String useId,
	                             Table_use_info table_use_info, String hyren_name, String original_name) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.封装表使用信息参数
		table_use_info.setSysreg_name(hyren_name);
		table_use_info.setUser_id(userId);
		table_use_info.setUse_id(useId);
		table_use_info.setTable_blsystem(data_layer);
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
				.orElseThrow(() -> new BusinessException("sql查询错误")) > 0) {
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
	@Param(name = "sysreg_name", desc = "表名", range = "无限制")
	private void deleteInterfaceTableInfo(long user_id, String sysreg_name) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.先删除Sysreg_parameter_info
		List<Long> useIdList = Dbo.queryOneColumnList("select use_id from " + Table_use_info.TableName +
				" where lower(sysreg_name)=lower(?) and user_id=?", sysreg_name, user_id);
		for (Long use_id : useIdList) {
			Dbo.execute("delete from " + Sysreg_parameter_info.TableName + " where use_id =? ", use_id);
		}
		// 3.再删除table_use_info
		Dbo.execute("delete from " + Table_use_info.TableName + " where lower(sysreg_name) = lower(?)" +
				" and user_id = ?", sysreg_name, user_id);
	}

	@Method(desc = "根据ID查询列信息", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.根据不同数据源类型查询表的列信息并返回" +
			"2.1贴源层" +
			"2.2集市层")
	@Param(name = "file_id", desc = "表ID", range = "无限制", nullable = true)
	@Param(name = "data_layer", desc = "数据层，树根节点", range = "无限制")
	@Return(desc = "根据不同数据源类型查询表的列信息并返回", range = "无限制")
	public Result searchFieldById(String file_id, String data_layer) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.根据不同数据源类型查询表的列信息并返回
		if (DataSourceType.DCL == DataSourceType.ofEnumByCode(data_layer)) {
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
		} else if (DataSourceType.DML == DataSourceType.ofEnumByCode(data_layer)) {
			// 2.2集市层
			return Dbo.queryResult("SELECT field_en_name,field_cn_name FROM " + Datatable_field_info.TableName
					+ " WHERE datatable_id = ?");
		} else {
			throw new BusinessException("待开发，目前只支持贴源层与集市层");
		}
	}
}
