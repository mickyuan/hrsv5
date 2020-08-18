package hrds.commons.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.query.DCLDataQuery;
import hrds.commons.tree.background.query.DMLDataQuery;
import hrds.commons.tree.background.query.DQCDataQuery;
import hrds.commons.tree.background.query.UDLDataQuery;

import java.util.*;

@DocClass(desc = "数据表工具类", author = "BY-HLL", createdate = "2019/11/4 0004 下午 02:35")
public class DataTableUtil {


    @Method(desc = "获取平台登记的所有表信息", logicStep = "获取平台登记的所有表信息")
    @Param(name = "db", desc = "数据库db操作对象", range = "不可为空")
    public static List<String> getAllTableNameByPlatform(DatabaseWrapper db) {
        //初始化查询Sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT * FROM (");
        //DCL层的DB采集
        asmSql.addSql("SELECT hyren_name AS table_name FROM " + Data_store_reg.TableName);
        //DCL层的OBJ采集
        asmSql.addSql("UNION");
        asmSql.addSql("SELECT en_name AS table_name FROM " + Object_collect_task.TableName);
        //DML层表登记信息
        asmSql.addSql("UNION");
        asmSql.addSql("SELECT datatable_en_name AS table_name FROM " + Dm_datatable.TableName + " dmd" +
                " JOIN " + Dtab_relation_store.TableName + " dtab_rs ON dmd.datatable_id=dtab_rs.tab_id" +
                " WHERE dtab_rs.is_successful=?").addParam(JobExecuteState.WanCheng.getCode());
        //DQC层表信息
        asmSql.addSql("UNION");
        asmSql.addSql("SELECT table_name AS table_name FROM " + Dq_index3record.TableName);
        //UDL层表信息
        asmSql.addSql("UNION");
        asmSql.addSql("SELECT table_name AS table_name FROM " + Dq_table_info.TableName);
        //拼接sql结束
        asmSql.addSql(") tmp ");
        //执行查询sql
        return SqlOperator.queryOneColumnList(db, asmSql.sql(), asmSql.params());
    }

    @Method(desc = "获取表信息", logicStep = "获取表信息")
    @Param(name = "data_layer", desc = "数据层", range = "String类型 DCL,DML")
    @Param(name = "file_id", desc = "表源属性id或表id", range = "String")
    @Return(desc = "表信息Map", range = "表信息Map")
    public static Map<String, Object> getTableInfoByFileId(String data_layer, String file_id) {
        //初始化返回结果Map
        Map<String, Object> tableInfoMap = new HashMap<>();
        String table_id, table_name, table_ch_name, create_date;
        //根据数据层获取不同层下的数据
        DataSourceType dataSourceType = DataSourceType.ofEnumByCode(data_layer);
        if (dataSourceType == DataSourceType.ISL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DCL) {
            //获取表信息
            Map<String, Object> table_info = DCLDataQuery.getDCLBatchTableInfo(file_id);
            //校验查询结果集
            if (table_info.isEmpty()) {
                throw new BusinessException("表登记信息已经不存在!");
            }
            table_id = table_info.get("table_id").toString();
            table_name = table_info.get("table_name").toString();
            table_ch_name = table_info.get("table_ch_name").toString();
            create_date = table_info.get("original_update_date").toString();
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
        } else if (dataSourceType == DataSourceType.UDL) {
            //获取UDL表信息
            Dq_table_info dq_table_info = UDLDataQuery.getUDLTableInfo(file_id);
            table_id = dq_table_info.getTable_id().toString();
            table_name = dq_table_info.getTable_name();
            table_ch_name = dq_table_info.getCh_name();
            create_date = dq_table_info.getCreate_date();
        } else {
            throw new BusinessException("未找到匹配的数据层!" + data_layer);
        }
        //设置返回结果map
        tableInfoMap.put("file_id", file_id);
        tableInfoMap.put("table_id", table_id);
        tableInfoMap.put("data_layer", data_layer);
        tableInfoMap.put("table_name", table_name);
        tableInfoMap.put("table_ch_name", table_ch_name);
        tableInfoMap.put("create_date", create_date);
        return tableInfoMap;
    }

    @Method(desc = "获取表信息和表的字段信息",
            logicStep = "获取表信息和表的字段信息")
    @Param(name = "data_layer", desc = "数据层", range = "String类型 DCL,DML")
    @Param(name = "file_id", desc = "表源属性id或表id", range = "String")
    @Return(desc = "表字段信息Map", range = "表字段信息Map")
    public static Map<String, Object> getTableInfoAndColumnInfo(String data_layer, String file_id) {
        //初始化返回结果Map
        Map<String, Object> data_meta_info = new HashMap<>();
        //初始化字段解析结果List
        List<Map<String, String>> column_info_list;
        String table_id, table_name, table_ch_name, create_date;
        //根据数据层获取不同层下的数据
        DataSourceType dataSourceType = DataSourceType.ofEnumByCode(data_layer);
        if (dataSourceType == DataSourceType.ISL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DCL) {
            //获取表信息
            Map<String, Object> table_info = DCLDataQuery.getDCLBatchTableInfo(file_id);
            //校验查询结果集
            if (table_info.isEmpty()) {
                throw new BusinessException("表登记信息已经不存在!");
            }
            table_id = table_info.get("table_id").toString();
            table_name = table_info.get("table_name").toString();
            table_ch_name = table_info.get("table_ch_name").toString();
            create_date = table_info.get("original_update_date").toString();
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
            //获取UDL表信息
            Dq_table_info dq_table_info = UDLDataQuery.getUDLTableInfo(file_id);
            table_id = dq_table_info.getTable_id().toString();
            table_name = dq_table_info.getTable_name();
            table_ch_name = dq_table_info.getCh_name();
            create_date = dq_table_info.getCreate_date();
            //获取UDL表字段信息
            List<Map<String, Object>> table_column_list = new ArrayList<>();
            column_info_list = DataTableFieldUtil.metaInfoToList(UDLDataQuery.getUDLTableColumns(table_id));
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
            //获取UDL表字段信息
            col_info_s = UDLDataQuery.getUDLTableColumns(file_id);
        } else {
            throw new BusinessException("未找到匹配的数据层!" + data_layer);
        }
        return col_info_s;
    }

    @Method(desc = "获取表字段信息列表", logicStep = "获取表字段信息列表")
    @Param(name = "table_name", desc = "登记表名", range = "String")
    @Param(name = "db", desc = "数据库db操作对象", range = "不可为空")
    @Return(desc = "字段信息列表", range = "字段信息列表")
    public static List<Map<String, Object>> getColumnByTableName(DatabaseWrapper db, String table_name) {
        //初始化查询Sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT * FROM (");
        //DCL DB文件采集
        asmSql.addSql("SELECT ti.table_id AS table_id,dsr.hyren_name AS table_name,tc.column_name AS column_name," +
                " tc.column_ch_name AS column_ch_name, tc.column_type AS column_type FROM " + Data_store_reg.TableName +
                " dsr JOIN " + Table_info.TableName + " ti ON dsr.database_id = ti.database_id" +
                " AND dsr.table_name = ti.table_name JOIN " + Table_column.TableName + " tc" +
                " ON ti.table_id = tc.table_id  WHERE lower(dsr.hyren_name) = lower(?)").addParam(table_name);
        //DCL OBJ文件采集
        asmSql.addSql("UNION");
        asmSql.addSql(" SELECT oct.ocs_id AS table_id,oct.en_name AS table_name,ocs.column_name AS column_name," +
                " ocs.data_desc AS column_ch_name,ocs.column_type AS column_type FROM " + Object_collect_task.TableName + " oct" +
                " JOIN " + Object_collect_struct.TableName + " ocs ON oct.ocs_id=ocs.ocs_id" +
                " JOIN " + Dtab_relation_store.TableName + " dtab_rs ON dtab_rs.tab_id=oct.ocs_id" +
                " AND oct.en_name=?").addParam(table_name);
        //DML
        asmSql.addSql("UNION");
        asmSql.addSql("SELECT dd.datatable_id AS table_id, dd.datatable_en_name AS table_name, dfi.field_en_name AS" +
                " column_name,dfi.field_cn_name AS column_ch_name,concat(field_type,'(',field_length,')') AS" +
                " column_type FROM " + Datatable_field_info.TableName + " dfi JOIN " + Dm_datatable.TableName + " dd ON" +
                " dd.datatable_id = dfi.datatable_id WHERE LOWER(dd.datatable_en_name) = LOWER(?)").addParam(table_name);
        //UDL
        asmSql.addSql("UNION");
        asmSql.addSql("SELECT dti.table_id AS table_id, dti.table_name AS table_name, dtc.column_name AS column_name," +
                " dtc.field_ch_name AS column_ch_name, dtc.column_type AS column_type FROM " + Dq_table_info.TableName + " dti" +
                " JOIN " + Dq_table_column.TableName + " dtc ON dti.table_id=dtc.table_id WHERE LOWER(dti.table_name) = LOWER(?)");
        asmSql.addParam(table_name);
        asmSql.addSql(") tmp group by table_id,table_name,column_name,column_ch_name,column_type order by table_name");
        List<Map<String, Object>> column_list = SqlOperator.queryList(db, asmSql.sql(), asmSql.params());
        if (!column_list.isEmpty()) {
            return column_list;
        } else {
            //DQC
            asmSql.clean();
            Dq_index3record di3 = SqlOperator.queryOneObject(db, Dq_index3record.class, "SELECT * FROM " + Dq_index3record.TableName + " WHERE" +
                    " table_name = LOWER(?)", table_name).orElseThrow(()
                    -> (new BusinessException("表: " + table_name + " 的字段信息不存在,请检查表是否登记成功!")));
            String table_col_s = di3.getTable_col();
            String[] column_s = table_col_s.split(",");
            for (String column : column_s) {
                Map<String, Object> map = new HashMap<>();
                map.put("table_id", di3.getRecord_id());
                map.put("table_name", di3.getTable_name());
                map.put("column_name", column);
                map.put("column_ch_name", column);
                map.put("column_type", "VARCHAR(--)");
                column_list.add(map);
            }
        }
        return column_list;
    }

    @Method(desc = "获取在所有存储层中是否存在该表", logicStep = "1.根据表名获取在所有存储层中是否存在该表")
    @Param(name = "db", desc = "数据库db操作对象", range = "不可为空")
    @Param(name = "tableName", desc = "表名", range = "String类型,不大于512字符")
    @Return(desc = "boolean", range = "报错(提示在哪个存储层重复) 或者 false: 不存在")
    public static boolean tableIsRepeat(DatabaseWrapper db, String tableName) {
        boolean isRepeat = Boolean.FALSE;
        if (tableIsExistInDCL(db, tableName)) {
            isRepeat = Boolean.TRUE;
        }
        if (tableIsExistInDML(db, tableName)) {
            isRepeat = Boolean.TRUE;
        }
        if (tableIsExistInUDL(db, tableName)) {
            isRepeat = Boolean.TRUE;
        }
        return isRepeat;
    }

    @Method(desc = "获取影响关系数据", logicStep = "获取影响关系数据")
    @Param(name = "db", desc = "数据库db操作对象", range = "不可为空")
    @Param(name = "table_name", desc = "表名", range = "String类型")
    @Param(name = "search_type", desc = "搜索类型", range = "String类型, 0:表查看,1:字段查看,IsFlag代码项设置")
    @Return(desc = "影响关系数据", range = "影响关系数据")
    public static Map<String, Object> influencesDataInfo(DatabaseWrapper db, String table_name, String search_type) {
        //数据校验
        Validator.notBlank(search_type, "搜索关系为空!");
        //初始化查询Sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select * from (");
        //加工
        asmSql.addSql(" select own_source_table_name AS source_table_name,sourcefields_name AS source_fields_name," +
                "datatable_en_name AS table_name,targetfield_name AS target_column_name,'' as mapping" +
                " from " + Dm_datatable.TableName + " dd join " + Dm_datatable_source.TableName + " dds" +
                " on dd.datatable_id = dds.datatable_id join " + Dm_etlmap_info.TableName + " dei on" +
                " dds.own_dource_table_id = dei.own_dource_table_id and dd.datatable_id = dei.datatable_id where" +
                " lower(own_source_table_name) = lower(?)").addParam(table_name);
        asmSql.addSql(") aa order by source_fields_name");
        //执行sql获取结果
        List<Map<String, Object>> influences_data_s = SqlOperator.queryList(db, asmSql.sql(), asmSql.params());
        //初始化影响结果信息
        List<Map<String, Object>> influencesResult = new ArrayList<>();
        if (!influences_data_s.isEmpty()) {
            //搜索类型
            IsFlag is_st = IsFlag.ofEnumByCode(search_type);
            //IsFlag.Fou 代表0:表查看
            if (is_st == IsFlag.Fou) {
                Set<String> set = new HashSet<>();
                influences_data_s.forEach(influences_data -> {
                    //获取模型表名
                    String tableName = influences_data.get("table_name").toString();
                    //过滤重复的模型表名称
                    if (!set.contains(tableName)) {
                        Map<String, Object> map = new HashMap<>();
                        set.add(tableName);
                        map.put("id", tableName);
                        map.put("name", tableName);
                        map.put("direction", "right");
                        map.put("topic", tableName);
                        influencesResult.add(map);
                    }
                });
            }
            //IsFlag.Shi 代表1:字段查看
            else if (is_st == IsFlag.Shi) {
                //初始化映射信息map
                Map<String, List<Map<String, Object>>> children_map = new HashMap<>();
                influences_data_s.forEach(influences_data -> {
                    //源表列名称
                    String source_fields_name = influences_data.get("source_fields_name").toString();
                    //模型表名称
                    String tableName = influences_data.get("table_name").toString();
                    //模型表字段
                    String target_column_name = influences_data.get("target_column_name").toString();
                    //映射规则
                    String mapping = influences_data.get("mapping").toString();
                    //如果有mapping信息就拼上
                    StringBuilder col_mapping = new StringBuilder();
                    col_mapping.append(target_column_name);
                    //处理含有映射关系的数据列
                    if (!StringUtil.isBlank(mapping)) {
                        col_mapping.append('(').append(mapping).append(')');
                    }
                    //过滤相同的列
                    if (!children_map.containsKey(source_fields_name)) {
                        //模型列映射源表的列信息
                        List<Map<String, Object>> map_col_list = new ArrayList<>();
                        //模型表的信息
                        Map<String, Object> source_table_map = new HashMap<>();
                        source_table_map.put("name", tableName);
                        //模型表的列信息
                        List<Map<String, Object>> source_table_col_list = new ArrayList<>();
                        //列信息
                        Map<String, Object> source_table_col_map = new HashMap<>();
                        source_table_col_map.put("name", col_mapping);
                        source_table_col_list.add(source_table_col_map);
                        source_table_map.put("children", source_table_col_list);
                        map_col_list.add(source_table_map);
                        //设置map
                        children_map.put(source_fields_name, map_col_list);
                    } else {
                        //模型表的信息
                        Map<String, Object> source_table_info = new HashMap<>();
                        source_table_info.put("name", tableName);
                        //模型表的列信息
                        List<Map<String, Object>> source_table_col_info_s = new ArrayList<>();
                        Map<String, Object> source_table_col_info = new HashMap<>();
                        source_table_col_info.put("name", col_mapping);
                        source_table_col_info_s.add(source_table_col_info);
                        source_table_info.put("children", source_table_col_info_s);
                        //将新增的数据放入到源表列信息中
                        children_map.get(source_fields_name).add(source_table_info);
                    }
                });
                //循环处理需要的数据结构
                if (!children_map.isEmpty()) {
                    Set<Map.Entry<String, List<Map<String, Object>>>> entrySet = children_map.entrySet();
                    for (Map.Entry<String, List<Map<String, Object>>> entry : entrySet) {
                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("name", entry.getKey());
                        map1.put("children", entry.getValue());
                        influencesResult.add(map1);
                    }
                }
            } else {
                throw new BusinessException("搜索类型不匹配! search_type=" + search_type);
            }
        }
        //初始化返回结果
        Map<String, Object> influencesDataInfoMap = new HashMap<>();
        influencesDataInfoMap.put("name", table_name);
        influencesDataInfoMap.put("children", influencesResult);
        return influencesDataInfoMap;
    }

    @Method(desc = "获取血缘关系数据", logicStep = "获取血缘关系数据")
    @Param(name = "db", desc = "数据库db操作对象", range = "不可为空")
    @Param(name = "table_name", desc = "表名", range = "String类型")
    @Param(name = "search_type", desc = "搜索类型", range = "String类型, 0:表查看,1:字段查看,IsFlag代码项设置")
    @Return(desc = "血缘关系数据", range = "血缘关系数据")
    public static Map<String, Object> bloodlineDateInfo(DatabaseWrapper db, String table_name, String search_type) {
        //初始化查询Sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select * from (");
        //加工
        asmSql.addSql("SELECT datatable_en_name AS table_name, targetfield_name AS target_column_name," +
                " own_source_table_name AS source_table_name,sourcefields_name AS source_fields_name, '' AS mapping" +
                " FROM " + Dm_datatable.TableName + " dd JOIN " + Dm_datatable_source.TableName + " dds ON" +
                " dd.datatable_id = dds.datatable_id JOIN " + Dm_etlmap_info.TableName + " dei ON" +
                " dds.own_dource_table_id = dei.own_dource_table_id AND dd.datatable_id = dei.datatable_id" +
                " WHERE LOWER(datatable_en_name) = LOWER(?)").addParam(table_name);
        asmSql.addSql(" ) aa order by source_table_name,target_column_name");
        //获取源表名影响的模型表信息
        List<Map<String, Object>> bloodline_data_s = SqlOperator.queryList(db, asmSql.sql(), asmSql.params());
        //表对应列信息
        List<Map<String, Object>> children_s = new ArrayList<>();
        if (!bloodline_data_s.isEmpty()) {
            //搜索类型
            IsFlag is_st = IsFlag.ofEnumByCode(search_type);
            //IsFlag.Fou 代表0:表查看
            if (is_st == IsFlag.Fou) {
                Set<String> set = new HashSet<>();
                bloodline_data_s.forEach(bloodline_data -> {
                    //获取模型表名称
                    String tableName = bloodline_data.get("source_table_name").toString();
                    //过滤重复的模型表名称
                    if (!set.contains(tableName)) {
                        Map<String, Object> map = new HashMap<>();
                        set.add(tableName);
                        map.put("name", tableName);
                        children_s.add(map);
                    }
                });
            } //IsFlag.Shi 代表1:字段查看
            else if (is_st == IsFlag.Shi) {
                //字段对应映射信息map
                Map<String, List<Map<String, Object>>> children_map = new HashMap<>();
                bloodline_data_s.forEach(bloodline_data -> {
                    //源表名称
                    String source_table_name = bloodline_data.get("source_table_name").toString();
                    //源表字段
                    String source_fields_name = bloodline_data.get("source_fields_name").toString();
                    //模型列名称
                    String target_column_name = bloodline_data.get("target_column_name").toString();
                    //过滤相同的列
                    if (!children_map.containsKey(target_column_name)) {
                        //模型列映射源表的列信息 [{name : sourceObj}]
                        List<Map<String, Object>> map_col_list = new ArrayList<>();
                        //源表的信息 {sourceName : sourceName , children : sourceColArray}
                        Map<String, Object> source_table_map = new HashMap<>();
                        source_table_map.put("name", source_table_name);
                        //源的列信息 [source_table_col_list]
                        List<Map<String, Object>> source_table_col_list = new ArrayList<>();
                        //列信息 source_table_col_map:{name : colName}
                        Map<String, Object> source_table_col_map = new HashMap<>();
                        source_table_col_map.put("name", source_fields_name);
                        source_table_col_list.add(source_table_col_map);
                        source_table_map.put("children", source_table_col_list);
                        map_col_list.add(source_table_map);
                        children_map.put(target_column_name, map_col_list);
                    } else {
                        //源表的信息 {sourceName : sourceName , children : sourceColArray}
                        Map<String, Object> source_table_map = new HashMap<>();
                        source_table_map.put("name", source_table_name);
                        //源的列信息 [source_table_col_list]
                        List<Map<String, Object>> source_table_col_list = new ArrayList<>();
                        //列信息 source_table_col_map:{name : colName}
                        Map<String, Object> source_table_col_map = new HashMap<>();
                        source_table_col_map.put("name", source_fields_name);
                        source_table_col_list.add(source_table_col_map);
                        source_table_map.put("children", source_table_col_list);
                        //将新增的数据放入到源表列信息中
                        children_map.get(target_column_name).add(source_table_map);
                    }
                });
                //处理成需要的数据结构
                if (!children_map.isEmpty()) {
                    Set<Map.Entry<String, List<Map<String, Object>>>> entrySet = children_map.entrySet();
                    for (Map.Entry<String, List<Map<String, Object>>> entry : entrySet) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", entry.getKey());
                        map.put("children", entry.getValue());
                        children_s.add(map);
                    }
                }
            } else {
                throw new BusinessException("搜索类型不匹配! search_type=" + search_type);
            }
        }
        //初始化当前模型表的全部映射关系信息
        Map<String, Object> bloodlineDateInfoMap = new HashMap<>();
        bloodlineDateInfoMap.put("name", table_name);
        bloodlineDateInfoMap.put("children", children_s);
        return bloodlineDateInfoMap;
    }

    @Method(desc = "判断表是否在源文件信息表存在", logicStep = "1.判断表是否在源文件信息表存在")
    @Param(name = "db", desc = "数据库db操作对象", range = "不可为空")
    @Param(name = "tableName", desc = "表名", range = "String类型,不大于512字符")
    @Return(desc = "boolean", range = "true: 存在 或者 false: 不存在")
    private static boolean tableIsExistInDCL(DatabaseWrapper db, String tableName) {
        //1.判断表是否在源文件信息表存在
        return SqlOperator.queryNumber(db, "SELECT count(1) count FROM " + Data_store_reg.TableName +
                        " WHERE lower(hyren_name) = ? AND collect_type IN (?,?)", tableName.toLowerCase(),
                AgentType.ShuJuKu.getCode(), AgentType.DBWenJian.getCode()).orElseThrow(()
                -> new BusinessException("检查表名称否重复在源文件信息表的SQL编写错误")) != 0;
    }

    @Method(desc = "判断表是否在集市数据表存在", logicStep = "1.判断表是否在集市数据表存在")
    @Param(name = "db", desc = "数据库db操作对象", range = "不可为空")
    @Param(name = "tableName", desc = "表名", range = "String类型,不大于512字符")
    @Return(desc = "boolean", range = "true: 存在 或者 false: 不存在")
    private static boolean tableIsExistInDML(DatabaseWrapper db, String tableName) {
        //1.判断表是否在集市数据表存在
        return SqlOperator.queryNumber(db, "SELECT count(1) count FROM " + Dm_datatable.TableName +
                " WHERE lower(datatable_en_name) = lower(?)", tableName.toLowerCase()).orElseThrow(()
                -> new BusinessException("检查表名称否重复在集市数据表的SQL编写错误")) != 0;
    }

    @Method(desc = "判断表是否在系统表创建信息表存在", logicStep = "1.判断表是否在系统表创建信息表存在")
    @Param(name = "db", desc = "数据库db操作对象", range = "不可为空")
    @Param(name = "tableName", desc = "表名", range = "String类型,不大于512字符")
    @Return(desc = "boolean", range = "true: 存在 或者 false: 不存在")
    private static boolean tableIsExistInUDL(DatabaseWrapper db, String tableName) {
        //1.判断表是否在集市数据表存在
        return SqlOperator.queryNumber(db, "SELECT count(1) count FROM " + Dq_table_info.TableName +
                " WHERE lower(table_name) = lower(?)", tableName.toLowerCase()).orElseThrow(()
                -> new BusinessException("检查表名称否重复在系统表创建信息表的SQL编写错误")) != 0;
    }

}
