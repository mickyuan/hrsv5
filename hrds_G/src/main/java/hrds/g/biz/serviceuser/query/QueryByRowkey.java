package hrds.g.biz.serviceuser.query;

import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.utils.CommonVariables;
import hrds.commons.utils.Constant;
import hrds.g.biz.enumerate.StateType;
import hrds.g.biz.init.InterfaceManager;
import hrds.g.biz.serviceuser.common.InterfaceCommon;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryByRowkey {

	private static final Logger logger = LogManager.getLogger();

	public static Map<String, Object> query(String en_table, String rowkey, String en_column, String versions,
	                                        String dsl_name, String platform, String prncipal_name,
	                                        String hadoop_user_name, long user_id, DatabaseWrapper db) {
		Map<String, Object> responseMap = new HashMap<>();
		Object message;
		if (StringUtil.isBlank(en_table) || StringUtil.isBlank(rowkey)) {
			responseMap = StateType.getResponseInfo(StateType.ARGUMENT_ERROR.name(),
					"表名或者rowkey不能为空");
		} else {
			try (HBaseHelper helper = HBaseHelper.getHelper(ConfigReader.getConfiguration(
					FileNameUtils.normalize(
							Constant.STORECONFIGPATH + dsl_name + File.separator, true),
					platform, prncipal_name, hadoop_user_name)); Table table = helper.getTable(en_table)) {
				Get get = new Get(rowkey.getBytes());
				//自定义搜索方式
				Map<String, Object> map = modifyGet(get, en_column, versions, en_table, user_id, db);
				if (map != null) {
					return map;
				}
				Result result = table.get(get);
				List<Cell> cellList = result.listCells();
				if (cellList != null && cellList.size() > 0) {
					for (Cell cell : cellList) {
						responseMap.put(Bytes.toString(CellUtil.cloneQualifier(cell)).toLowerCase(),
								Bytes.toString(CellUtil.cloneValue(cell)));
					}
					List<Map<String, Object>> jsonList = new ArrayList<>();
					jsonList.add(responseMap);
					message = jsonList;
				} else {
					message = new ArrayList<>();
				}
				responseMap = StateType.getResponseInfo(StateType.NORMAL.name(), message);
			} catch (TableNotFoundException tableNotFoundException) {
				responseMap = StateType.getResponseInfo(StateType.ARGUMENT_ERROR.name(),
						"TableNotFoundException: " + en_table);
				logger.error(tableNotFoundException);
			} catch (Exception e) {
				responseMap = StateType.getResponseInfo(StateType.EXCEPTION.name(), e.getMessage());
				logger.error(e);
			}
		}
		return responseMap;
	}

	/**
	 * @param get          hbase的get对象
	 * @param selectColumn （以'${列族}:'作为前缀）列，多列以|隔开
	 * @param versions     数据版本号
	 * @param en_table     表英文名
	 * @param user_id      用户ID
	 * @param db           数据库连接对象
	 * @return 返回hbase的get对象
	 */
	private static Map<String, Object> modifyGet(Get get, String selectColumn, String versions, String en_table,
	                                             long user_id, DatabaseWrapper db)
			throws NumberFormatException, IOException {

		// set timestamp
		if (StringUtil.isNotBlank(versions)) {
			get.setTimeStamp(Long.parseLong(versions));
		}
		if (StringUtil.isNotBlank(selectColumn)) {
			// 5.从内存中获取当前表的字段信息
			String table_en_column = InterfaceManager.getUserTableInfo(db, user_id,
					en_table).getTable_en_column();
			List<String> columns = StringUtil.split(table_en_column.toLowerCase(), Constant.METAINFOSPLIT);
			Map<String, Object> userColumn = checkRowkeyColumnsIsExist(selectColumn, user_id, columns);
			if (userColumn != null) return userColumn;
			List<String> colList = StringUtil.split(selectColumn, "|");
			for (String colsWithCf : colList) {
				if (!colsWithCf.contains(":")) {
					return StateType.getResponseInfo(StateType.ROWKEY_COLUMN_FORMAT_ERROR);
				}
				List<String> colWithCf = StringUtil.split(colsWithCf, ":");
				get.addColumn(colWithCf.get(0).getBytes(), colWithCf.get(1).getBytes());
			}
		}
		return null;
	}

	@Method(desc = "检查列是否存在", logicStep = "1.数据可访问权限处理方式,该方法不需要进行访问权限限制" +
			"2.如果不是指定用户将进行字段验证" +
			"3.获取用户需要查询的列名的列" +
			"4.判断列当前表对应数据库的列名称集合是否为空，不为空遍历列名称" +
			"5.判断当前列名称是否有权限" +
			"6.查询列存在，返回null")
	@Param(name = "selectColumn", desc = "需要查询的列名", range = "(selectColumn=column1,column2....等,号隔开)"
			+ "，如果没有，查询所有字段")
	@Param(name = "user_id", desc = "用户ID", range = "新增用户时生成")
	@Param(name = "columns", desc = "当前表对应数据库的列名称集合", range = "无限制")
	@Return(desc = "返回接口响应信息", range = "无限制")
	public static Map<String, Object> checkRowkeyColumnsIsExist(String selectColumn, Long user_id,
	                                                            List<String> columns) {
		// 1.数据可访问权限处理方式,该方法不需要进行访问权限限制
		if (StringUtil.isNotBlank(selectColumn)) {
			// 2.如果不是指定用户将进行字段验证
			if (!CommonVariables.AUTHORITY.contains(String.valueOf(user_id))) {
				// 3.获取用户需要查询的列族与列名
				List<String> userColumns = StringUtil.split(selectColumn, "|");
				// 4.判断列当前表对应数据库的列名称集合是否为空，不为空遍历列名称
				if (columns != null && columns.size() != 0) {
					for (String userColumn : userColumns) {
						// 5.判断当前列名称是否有权限,没有返回错误响应信息
						List<String> columnList = StringUtil.split(userColumn, ":");
						if (InterfaceCommon.columnIsExist(columnList.get(1).toLowerCase(), columns)) {
							return StateType.getResponseInfo(StateType.COLUMN_DOES_NOT_EXIST.name(),
									"列名" + columnList.get(1) + "不存在");
						}
					}
				}
			}
		}
		// 6.查询列存在，返回null
		return null;
	}

}
