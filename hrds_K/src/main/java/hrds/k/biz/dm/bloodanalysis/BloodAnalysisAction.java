package hrds.k.biz.dm.bloodanalysis;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Dm_datatable;
import hrds.commons.entity.Dm_datatable_source;
import hrds.commons.entity.Dm_etlmap_info;
import hrds.commons.exception.BusinessException;

import java.util.*;

@DocClass(desc = "数据管控-血缘分析", author = "BY-HLL", createdate = "2020/4/13 0013 上午 11:04")
public class BloodAnalysisAction extends BaseAction {

    @Method(desc = "根据表名称获取表与表之间的血缘关系",
            logicStep = "获取表与表之间的关系")
    @Param(name = "table_name", desc = "表名", range = "String类型")
    @Param(name = "search_type", desc = "搜索类型", range = "String类型, 0:表查看,1:字段查看,IsFlag代码项设置")
    @Param(name = "search_relationship", desc = "搜索关系", range = "String类型, 0:影响,1:血缘,IsFlag代码项设置")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public Map<String, Object> getTableBloodRelationship(String table_name, String search_type,
                                                         String search_relationship) {
        //数据校验
        Validator.notBlank(table_name, "搜索表名称为空!");
        Validator.notBlank(search_type, "搜索类型为空!");
        Validator.notBlank(search_relationship, "搜索关系为空!");
        //搜索关系 0:影响,1:血缘 IsFlag代码项设置
        IsFlag is_sr = IsFlag.ofEnumByCode(search_relationship);
        //初始化返回结果
        Map<String, Object> tableBloodRelationshipMap;
        //IsFlag.Fou 代表0:影响
        if (is_sr == IsFlag.Fou) {
            tableBloodRelationshipMap = influencesDataInfo(table_name, search_type);
        }
        //IsFlag.Shi 代表1:血缘
        else if (is_sr == IsFlag.Shi) {
            tableBloodRelationshipMap = bloodlineDateInfo(table_name, search_type);
        } else {
            //搜索类型不匹配
            throw new BusinessException("搜索类型不匹配! search_type=" + search_relationship);
        }
        return tableBloodRelationshipMap;
    }

    @Method(desc = "模糊搜索表名",
            logicStep = "模糊搜索表名")
    @Param(name = "table_name", desc = "表名", range = "String类型", valueIfNull = "")
    @Param(name = "search_relationship", desc = "搜索关系", range = "String类型, 0:影响,1:血缘,IsFlag代码项设置")
    @Return(desc = "搜索结果List", range = "搜索结果List")
    public List<String> fuzzySearchTableName(String table_name, String search_relationship) {
        //数据校验
        Validator.notBlank(search_relationship, "搜索关系为空!");
        //初始化执行sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        //根据搜索类型检索表名 0:影响,1:血缘 IsFlag代码项设置
        IsFlag is_sr = IsFlag.ofEnumByCode(search_relationship);
        //IsFlag.Fou 代表0:影响
        if (is_sr == IsFlag.Fou) {
            asmSql.clean();
            asmSql.addSql("select table_name from (");
            //加工 TODO hrsv5.1
//            asmSql.addSql("select souretabname as table_name from edw_mapping where" +
//                    " end_dt = ?").addParam(Constant.MAXDATE).addSql(" GROUP BY souretabname");
//            asmSql.addSql("union all");
            //集市
            asmSql.addSql("select own_source_table_name as table_name from " + Dm_datatable_source.TableName +
                    " group by own_source_table_name ");
            //数据管控创建表 TODO hrsv5.1
//            asmSql.addSql("union all ");
//            asmSql.addSql("select t1.colsourcetab as table_name  from sys_table_column t1 join " +
//                    "sys_table_info t2 ON t1.info_id = t2.info_id WHERE t2.is_trace = ?").addParam(IsFlag.Shi.getCode());
            asmSql.addSql(") T where ");
            asmSql.addLikeParam("table_name", "%" + table_name.toLowerCase() + "%", "");
            asmSql.addSql(" group by table_name");
        }
        //IsFlag.Shi 代表1:血缘
        else if (is_sr == IsFlag.Shi) {
            //模型表
            asmSql.clean();
            //加工血缘 TODO hrsv5.1
            asmSql.addSql("select table_name from ( ");
//            asmSql.addSql("select table_name from edw_mapping where end_dt = ?")
//                    .addParam(Constant.MAXDATE);
//            asmSql.addLikeParam("table_name", table_name).addSql(" GROUP BY table_name");
//            asmSql.addSql(" union all");
            asmSql.addSql(" select datatable_en_name as table_name from " + Dm_datatable.TableName + " group by " +
                    "datatable_en_name ");
            //数据管控创建表未实现 TODO hrsv5.1
//            asmSql.addSql(" union all");
//            asmSql.addSql(" select table_name as table_name from sys_table_info WHERE is_trace = ? GROUP BY table_name")
//                    .addParam(IsFlag.Shi.getCode());
            asmSql.addSql(" ) aa where ");
            asmSql.addLikeParam("lower(table_name)", "%" + table_name + "%", "");
            asmSql.addSql(" group by table_name");
        } else {
            //搜索类型不匹配
            throw new BusinessException("搜索类型不匹配! search_relationship=" + search_relationship);
        }
        //执行SQL,获取结果
        return Dbo.queryOneColumnList(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "获取影响关系数据",
            logicStep = "获取影响关系数据")
    @Param(name = "table_name", desc = "表名", range = "String类型")
    @Param(name = "search_type", desc = "搜索类型", range = "String类型, 0:表查看,1:字段查看,IsFlag代码项设置")
    @Return(desc = "影响关系数据", range = "影响关系数据")
    private Map<String, Object> influencesDataInfo(String table_name, String search_type) {
        //数据校验
        Validator.notBlank(search_type, "搜索关系为空!");
        //初始化查询Sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select * from (");
        //加工 TODO hrsv5.1
//        asmSql.addSql("select T1.tabname,colname,souretabname,sourecolvalue,mapping from edw_mapping" +
//                " T1 join edw_table T2 on  T1.tabname = T2.tabname where souretabname = ?").addParam(table_name);
//        asmSql.addSql(" and T1.end_dt = ? and T2.end_dt = ? ").addParam(Constant.MAXDATE).addParam(Constant.MAXDATE);
//        asmSql.addSql(" union all");
        //集市
        asmSql.addSql(" select own_source_table_name AS source_table_name,sourcefields_name AS source_fields_name," +
                "datatable_en_name AS table_name,targetfield_name AS target_column_name,'' as mapping" +
                " from " + Dm_datatable.TableName + " dd join " + Dm_datatable_source.TableName + " dds" +
                " on dd.datatable_id = dds.datatable_id join " + Dm_etlmap_info.TableName + " dei on" +
                " dds.own_dource_table_id = dei.own_dource_table_id and dd.datatable_id = dei.datatable_id where" +
                " lower(own_source_table_name) = lower(?)").addParam(table_name);
        //数据管控创建表  TODO hrsv5.1
//        asmSql.addSql(" union all");
//        asmSql.addSql(" SELECT  t2.colsourcetab as souretabname,t2.colsourcecol as sourecolvalue,t1.table_name as" +
//                " table_name,t2.column_name as colname,'' as mapping FROM sys_table_info t1 LEFT JOIN sys_table_column t2" +
//                " on t1.info_id = t2.info_id WHERE t1.is_trace = ?").addParam(IsFlag.Shi.getCode());
        asmSql.addSql(") aa order by source_fields_name");
        //执行sql获取结果
        List<Map<String, Object>> influences_data_s = Dbo.queryList(asmSql.sql(), asmSql.params());
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
                        map.put("name", tableName);
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

    @Method(desc = "获取血缘关系数据",
            logicStep = "获取血缘关系数据")
    @Param(name = "table_name", desc = "表名", range = "String类型")
    @Param(name = "search_type", desc = "搜索类型", range = "String类型, 0:表查看,1:字段查看,IsFlag代码项设置")
    @Return(desc = "血缘关系数据", range = "血缘关系数据")
    private Map<String, Object> bloodlineDateInfo(String table_name, String search_type) {
        //初始化查询Sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select * from (");
        //加工 TODO hrsv5.1
//        asmSql.addSql("select T1.tabname,colname,souretabname,sourecolvalue,mapping from edw_table T1 join" +
//                " edw_mapping T2 on T1.tabname = T2.tabname where lower(T1.tabname) = lower(?)").addParam(table_name);
//        asmSql.addSql("and T1.end_dt = ? and T2.end_dt = ?").addParam(Constant.MAXDATE).addParam(Constant.MAXDATE);
//        asmSql.addSql("union all");
        //集市
        asmSql.addSql("SELECT datatable_en_name AS table_name, targetfield_name AS target_column_name," +
                " own_source_table_name AS source_table_name,sourcefields_name AS source_fields_name, '' AS mapping" +
                " FROM " + Dm_datatable.TableName + " dd JOIN " + Dm_datatable_source.TableName + " dds ON" +
                " dd.datatable_id = dds.datatable_id JOIN " + Dm_etlmap_info.TableName + " dei ON" +
                " dds.own_dource_table_id = dei.own_dource_table_id AND dd.datatable_id = dei.datatable_id" +
                " WHERE LOWER(datatable_en_name) = LOWER(?)").addParam(table_name);
        //数据管控创建表 TODO hrsv5.1
//        asmSql.addSql(" union all");
//        asmSql.addSql(" select t1.table_name tabname,t2.column_name colname,t2.colsourcetab souretabname,t2.colsourcecol" +
//                " sourecolvalue,'' AS MAPPING from sys_table_info t1 join sys_table_column t2 ON t1.info_id = t2.info_id" +
//                " WHERE t1.is_trace = ? AND lower(table_name) = lower(?)")
//                .addParam(IsFlag.Shi.getCode()).addParam(table_name);
        asmSql.addSql(" ) aa order by source_table_name,target_column_name");
        //获取源表名影响的模型表信息
        List<Map<String, Object>> bloodline_data_s = Dbo.queryList(asmSql.sql(), asmSql.params());
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

}
