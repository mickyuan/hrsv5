package hrds.commons.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.query.DCLDataQuery;
import hrds.commons.tree.background.query.DMLDataQuery;
import hrds.commons.tree.background.query.DQCDataQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据表工具类", author = "BY-HLL", createdate = "2019/11/4 0004 下午 02:35")
public class DataTableUtil {

    @Method(desc = "获取表信息和表的字段信息",
            logicStep = "获取表信息和表的字段信息")
    @Param(name = "data_layer", desc = "数据层", range = "String类型 DCL,DML")
    @Param(name = "file_id", desc = "表源属性id或表id", range = "String")
    @Return(desc = "表字段信息Map", range = "表字段信息Map")
    public static Map<String, Object> getTableInfoAndColumnInfo(String data_layer, String file_id) {
        //初始化返回结果Map
        Map<String, Object> data_meta_info = new HashMap<>();
        //初始化表信息
        Map<String, Object> table_info_map;
        //初始化字段解析结果List
        List<Map<String, String>> column_info_list;
        String table_id, table_name, table_ch_name, create_date;
        //根据数据层获取不同层下的数据
        DataSourceType dataSourceType = DataSourceType.ofEnumByCode(data_layer);
        if (dataSourceType == DataSourceType.ISL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DCL) {
            //获取表信息
            table_info_map = DCLDataQuery.getDCLBatchTableInfo(file_id);
            //校验查询结果集
            if (table_info_map.isEmpty()) {
                throw new BusinessException("表登记信息已经不存在!");
            }
            table_id = table_info_map.get("table_id").toString();
            table_name = table_info_map.get("table_name").toString();
            table_ch_name = table_info_map.get("table_ch_name").toString();
            create_date = table_info_map.get("original_update_date").toString();
            //获取并转换字段信息List
            column_info_list = DataTableFieldUtil.metaInfoToList(DCLDataQuery.getDCLBatchTableColumns(file_id));
        } else if (dataSourceType == DataSourceType.DPL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DML) {
            //获取表信息
            Dm_datatable dm_datatable = DMLDataQuery.getDMLTableInfo(file_id);
            //校验查询结果集
            if (StringUtil.isBlank(dm_datatable.getDatatable_id().toString())) {
                throw new BusinessException("表登记信息已经不存在!");
            }
            table_id = dm_datatable.getDatatable_id().toString();
            table_name = dm_datatable.getDatatable_en_name();
            table_ch_name = dm_datatable.getDatatable_cn_name();
            create_date = dm_datatable.getDatatable_create_date();
            column_info_list = DataTableFieldUtil.metaInfoToList(DMLDataQuery.getDMLTableColumns(table_id));
        } else if (dataSourceType == DataSourceType.SFL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.AML) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DQC) {
            //获取表信息
            Dq_index3record dq_index3record = DQCDataQuery.getDQCTableInfo(file_id);
            table_id = dq_index3record.getRecord_id().toString();
            table_name = dq_index3record.getTable_name();
            table_ch_name = dq_index3record.getTable_name();
            create_date = dq_index3record.getRecord_date();
            List<Map<String, Object>> table_column_list = new ArrayList<>();
            String[] columns = dq_index3record.getTable_col().split(",");
            for (String column : columns) {
                Map<String, Object> map = new HashMap<>();
                String is_primary_key = IsFlag.Fou.getCode();
                map.put("column_id", table_id);
                map.put("column_name", column);
                map.put("column_ch_name", column);
                map.put("column_type", "varchar(--)");
                map.put("is_primary_key", is_primary_key);
                table_column_list.add(map);
            }
            column_info_list = DataTableFieldUtil.metaInfoToList(table_column_list);
        } else if (dataSourceType == DataSourceType.UDL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else {
            throw new BusinessException("未找到匹配的数据层!" + data_layer);
        }
        //设置返回结果map
        data_meta_info.put("file_id", file_id);
        data_meta_info.put("table_id", table_id);
        data_meta_info.put("data_layer", data_layer);
        data_meta_info.put("table_name", table_name);
        data_meta_info.put("table_ch_name", table_ch_name);
        data_meta_info.put("create_date", create_date);
        data_meta_info.put("column_info_list", column_info_list);
        return data_meta_info;
    }

    @Method(desc = "获取表字段信息列表", logicStep = "获取表字段信息列表")
    @Param(name = "data_layer", desc = "数据层", range = "String类型,DCL,DML")
    @Param(name = "data_own_type", desc = "类型标识", range = "dcl_batch:批量数据,dcl_realtime:实时数据", nullable = true)
    @Param(name = "file_id", desc = "表源属性id", range = "String")
    @Return(desc = "字段信息列表", range = "字段信息列表")
    public static List<Map<String, Object>> getColumnByFileId(String data_layer, String data_own_type, String file_id) {
        //数据层获取不同表结构
        List<Map<String, Object>> col_info_s;
        DataSourceType dataSourceType = DataSourceType.ofEnumByCode(data_layer);
        if (dataSourceType == DataSourceType.ISL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DCL) {
            //如果数据表所属层是DCL层,判断表类型是批量还是实时
            if (Constant.DCL_BATCH.equals(data_own_type)) {
                col_info_s = DCLDataQuery.getDCLBatchTableColumns(file_id);
            } else if (Constant.DCL_REALTIME.equals(data_own_type)) {
                throw new BusinessException("获取实时数据表的字段信息暂未实现!");
            } else {
                throw new BusinessException("数据表类型错误! dcl_batch:批量数据,dcl_realtime:实时数据");
            }
        } else if (dataSourceType == DataSourceType.DPL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DML) {
            col_info_s = DMLDataQuery.getDMLTableColumns(file_id);
        } else if (dataSourceType == DataSourceType.SFL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.AML) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DQC) {
            //获取表信息
            Dq_index3record dq_index3record = DQCDataQuery.getDQCTableInfo(file_id);
            List<Map<String, Object>> table_column_list = new ArrayList<>();
            String[] columns = dq_index3record.getTable_col().split(",");
            for (String column : columns) {
                Map<String, Object> map = new HashMap<>();
                String is_primary_key = IsFlag.Fou.getCode();
                map.put("column_name", column);
                map.put("column_ch_name", column);
                map.put("column_type", "varchar(--)");
                map.put("is_primary_key", is_primary_key);
                table_column_list.add(map);
            }
            col_info_s = table_column_list;
        } else if (dataSourceType == DataSourceType.UDL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else {
            throw new BusinessException("未找到匹配的数据层!" + data_layer);
        }
        return col_info_s;
    }

    @Method(desc = "获取表字段信息列表", logicStep = "获取表字段信息列表")
    @Param(name = "table_name", desc = "登记表名", range = "String")
    @Return(desc = "字段信息列表", range = "字段信息列表")
    public static List<Map<String, Object>> getColumnByTableName(String table_name) {
        //初始化查询Sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        //DCL
        asmSql.addSql("SELECT ti.table_id AS table_id,dsr.hyren_name AS table_name,tc.column_name AS column_name," +
                " tc.column_ch_name AS column_ch_name, tc.column_type AS column_type FROM " + Data_store_reg.TableName +
                " dsr JOIN " + Table_info.TableName + " ti ON dsr.database_id = ti.database_id" +
                " AND dsr.table_name = ti.table_name JOIN " + Table_column.TableName + " tc" +
                " ON ti.table_id = tc.table_id  WHERE lower(dsr.hyren_name) = lower(?)").addParam(table_name);
        //DML
        asmSql.addSql("UNION");
        asmSql.addSql("SELECT dd.datatable_id AS table_id, dd.datatable_en_name AS table_name, dfi.field_en_name AS" +
                " column_name,dfi.field_cn_name AS column_ch_name,concat(field_type,'(',field_length,')') AS" +
                " column_type FROM " + Datatable_field_info.TableName + " dfi JOIN " + Dm_datatable.TableName + " dd ON" +
                " dd.datatable_id = dfi.datatable_id WHERE LOWER(dd.datatable_en_name) = LOWER(?)").addParam(table_name);
        //DQC
        //TODO DQC数据管控层5.1版本创建表开发完成后,添加管控表查询关联
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "获取在所有存储层中是否存在该表",
            logicStep = "1.根据表名获取在所有存储层中是否存在该表")
    @Param(name = "tableName", desc = "表名", range = "String类型,不大于512字符")
    @Return(desc = "boolean", range = "报错(提示在哪个存储层重复) 或者 false: 不存在")
    public static boolean tableIsRepeat(String tableName) {
        if (tableIsExistInDataStoreReg(tableName)) {
            throw new BusinessException("表在源文件表中已经存在!" + tableName);
        }
//		if (tableIsExistInDatatableInfo(tableName)) {
//			throw new BusinessException("表在集市数据表中已经存在!" + tableName);
//		}
//		if (tableIsExistInEdwTable(tableName)) {
//			throw new BusinessException("表在数据仓库表中已经存在!" + tableName);
//		}
//		if (tableIsExistInSdmInnerTable(tableName)) {
//			throw new BusinessException("表在流数据内部消费信息登记表中已经存在!" + tableName);
//		}
//		if (tableIsExistInMlDatatableInfo(tableName)) {
//			throw new BusinessException("表在机器学习数据信息表中已经存在!" + tableName);
//		}
//		if (tableIsExistInSysTableInfo(tableName)) {
//			throw new BusinessException("表在系统表创建信息表中已经存在!" + tableName);
//		}
        return false;
    }

    @Method(desc = "判断表是否在源文件信息表存在",
            logicStep = "1.判断表是否在源文件信息表存在")
    @Param(name = "tableName", desc = "表名", range = "String类型,不大于512字符")
    @Return(desc = "boolean", range = "true: 存在 或者 false: 不存在")
    private static boolean tableIsExistInDataStoreReg(String tableName) {
        //1.判断表是否在源文件信息表存在
        return Dbo.queryNumber("SELECT count(1) count FROM " + Data_store_reg.TableName +
                        " WHERE lower(hyren_name) = ? AND collect_type IN (?,?)", tableName.toLowerCase(),
                AgentType.ShuJuKu.getCode(), AgentType.DBWenJian.getCode()).orElseThrow(()
                -> new BusinessException("检查表名称否重复在源文件信息表的SQL编写错误")) != 0;
    }

//	@Method(desc = "判断表是否在集市数据表存在",
//			logicStep = "1.判断表是否在集市数据表存在")
//	@Param(name = "tableName", desc = "表名", range = "String类型,不大于512字符")
//	@Return(desc = "boolean", range = "true: 存在 或者 false: 不存在")
//	private static boolean tableIsExistInDatatableInfo(String tableName) {
//		//1.判断表是否在集市数据表存在
//		return Dbo.queryNumber("SELECT count(1) count FROM " + Datatable_info.TableName +
//				" WHERE lower(datatable_en_name) = lower(?)", tableName.toLowerCase()).orElseThrow(()
//				-> new BusinessException("检查表名称否重复在集市数据表的SQL编写错误")) != 0;
//	}
//
//	@Method(desc = "判断表是否在数据仓库表存在",
//			logicStep = "1.判断表是否在数据仓库表存在")
//	@Param(name = "tableName", desc = "表名", range = "String类型,不大于512字符")
//	@Return(desc = "boolean", range = "true: 存在 或者 false: 不存在")
//	private static boolean tableIsExistInEdwTable(String tableName) {
//		//1.判断表是否在集市数据表存在
//		return Dbo.queryNumber("SELECT count(1) count FROM " + Edw_table.TableName +
//				" WHERE lower(tabname) = lower(?)", tableName.toLowerCase()).orElseThrow(()
//				-> new BusinessException("检查表名称否重复在数据仓库表的SQL编写错误")) != 0;
//	}
//
//	@Method(desc = "判断表是否在流数据内部消费信息登记表存在",
//			logicStep = "1.判断表是否在流数据内部消费信息登记表存在")
//	@Param(name = "tableName", desc = "表名", range = "String类型,不大于512字符")
//	@Return(desc = "boolean", range = "true: 存在 或者 false: 不存在")
//	private static boolean tableIsExistInSdmInnerTable(String tableName) {
//		//1.判断表是否在集市数据表存在
//		return Dbo.queryNumber("SELECT count(1) count FROM " + Sdm_inner_table.TableName +
//				" WHERE lower(table_en_name) = lower(?)", tableName.toLowerCase()).orElseThrow(()
//				-> new BusinessException("检查表名称否重复在流数据内部消费信息登记表的SQL编写错误")) != 0;
//	}
//
//	@Method(desc = "判断表是否在机器学习数据信息表存在",
//			logicStep = "1.判断表是否在机器学习数据信息表存在")
//	@Param(name = "tableName", desc = "表名", range = "String类型,不大于512字符")
//	@Return(desc = "boolean", range = "true: 存在 或者 false: 不存在")
//	private static boolean tableIsExistInMlDatatableInfo(String tableName) {
//		//1.判断表是否在集市数据表存在
//		return Dbo.queryNumber("SELECT count(1) count FROM " + Ml_datatable_info.TableName +
//				" WHERE lower(stable_en_name) = lower(?)", tableName.toLowerCase()).orElseThrow(()
//				-> new BusinessException("检查表名称否重复在机器学习数据信息表的SQL编写错误")) != 0;
//	}
//
//	@Method(desc = "判断表是否在系统表创建信息表存在",
//			logicStep = "1.判断表是否在系统表创建信息表存在")
//	@Param(name = "tableName", desc = "表名", range = "String类型,不大于512字符")
//	@Return(desc = "boolean", range = "true: 存在 或者 false: 不存在")
//	private static boolean tableIsExistInSysTableInfo(String tableName) {
//		//1.判断表是否在集市数据表存在
//		return Dbo.queryNumber("SELECT count(1) count FROM " + Sys_table_info.TableName +
//				" WHERE lower(table_name) = lower(?)", tableName.toLowerCase()).orElseThrow(()
//				-> new BusinessException("检查表名称否重复在系统表创建信息表的SQL编写错误")) != 0;
//	}
}
