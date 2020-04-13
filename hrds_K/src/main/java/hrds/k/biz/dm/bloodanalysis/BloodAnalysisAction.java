package hrds.k.biz.dm.bloodanalysis;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;

import java.util.*;

@DocClass(desc = "类说明", author = "BY-HLL", createdate = "2020/4/13 0013 上午 11:04")
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
        if (StringUtil.isBlank(table_name)) {
            throw new BusinessException("搜索表名称为空!");
        }
        if (StringUtil.isBlank(search_type)) {
            throw new BusinessException("搜索类型为空!");
        }
        if (StringUtil.isBlank(search_relationship)) {
            throw new BusinessException("搜索关系为空!");
        }
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
    @Param(name = "table_name", desc = "表名", range = "String类型")
    @Param(name = "search_relationship", desc = "搜索关系", range = "String类型, 0:影响,1:血缘,IsFlag代码项设置")
    @Return(desc = "搜索结果List", range = "搜索结果List")
    public List<String> fuzzySearchTableName(String table_name, String search_relationship) {
        //数据校验
        if (StringUtil.isBlank(table_name)) {
            throw new BusinessException("搜索表名为空!");
        }
        if (StringUtil.isBlank(search_relationship)) {
            throw new BusinessException("搜索类型为空!");
        }
        //初始化执行sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        //根据搜索类型检索表名 0:影响,1:血缘 IsFlag代码项设置
        IsFlag is_sr = IsFlag.ofEnumByCode(search_relationship);
        //IsFlag.Fou 代表0:影响
        if (is_sr == IsFlag.Fou) {
            //模型关联的源表信息
            asmSql.clean();
            asmSql.addSql("select tabname from (select souretabname as tabname from edw_mapping where" +
                    " end_dt = ?").addParam(Constant.MAXDATE);
            asmSql.addSql(" GROUP BY souretabname union all select own_source_table_name as tabname from" +
                    " datatable_own_source_info group by own_source_table_name union all" +
                    " select t1.colsourcetab as tabname  from sys_table_column t1 join sys_table_info t2" +
                    " ON t1.info_id = t2.info_id WHERE t2.is_trace = ?").addParam(IsFlag.Shi.getCode());
            asmSql.addSql(" ) aa where 1=1 ");
            asmSql.addLikeParam("tabname", table_name);
            asmSql.addSql(" group by tabname");
        }
        //IsFlag.Shi 代表1:血缘
        else if (is_sr == IsFlag.Shi) {
            //模型表
            asmSql.clean();
            asmSql.addSql("select tabname from ( select tabname from edw_mapping where end_dt = ?")
                    .addParam(Constant.MAXDATE);
            asmSql.addLikeParam("tabname", table_name).addSql(" GROUP BY tabname");
            asmSql.addSql(" union all");
            asmSql.addSql(" select datatable_en_name as tabname from datatable_info group by datatable_en_name ");
            asmSql.addSql(" union all");
            asmSql.addSql(" select table_name as tabname from sys_table_info WHERE is_trace = ? GROUP BY table_name")
                    .addParam(IsFlag.Shi.getCode());
            asmSql.addSql(" ) aa where 1=1 ");
            asmSql.addLikeParam("tabname", table_name);
            asmSql.addSql(" group by tabname");
        } else {
            //搜索类型不匹配
            throw new BusinessException("搜索类型不匹配! search_type=" + search_relationship);
        }
        //执行SQL
        return Dbo.queryOneColumnList(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "获取影响关系数据",
            logicStep = "获取影响关系数据")
    @Param(name = "table_name", desc = "表名", range = "String类型")
    @Param(name = "search_type", desc = "搜索类型", range = "String类型, 0:表查看,1:字段查看,IsFlag代码项设置")
    @Return(desc = "影响关系数据", range = "影响关系数据")
    private Map<String, Object> influencesDataInfo(String table_name, String search_type) {
        //初始化查询Sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select * from (");
        asmSql.addSql("select T1.tabname,colname,souretabname,sourecolvalue,mapping from edw_mapping" +
                " T1 join edw_table T2 on  T1.tabname = T2.tabname where souretabname = ?").addParam(table_name);
        asmSql.addSql(" and T1.end_dt = ? and T2.end_dt = ? ").addParam(Constant.MAXDATE).addParam(Constant.MAXDATE);
        asmSql.addSql(" union all");
        asmSql.addSql(" select own_source_table_name souretabname,sourcefields_name sourecolvalue,datatable_en_name" +
                " tabname,targetfield_name colname,'' as mapping from datatable_info di join datatable_own_source_info" +
                " dosi on di.datatable_id = dosi.datatable_id join etlmap_info ei on" +
                " dosi.own_dource_table_id = ei.own_dource_table_id and di.datatable_id = ei.datatable_id where" +
                " lower(own_source_table_name) = lower(?)").addParam(table_name);
        asmSql.addSql(" union all");
        asmSql.addSql(" SELECT  t2.colsourcetab as souretabname,t2.colsourcecol as sourecolvalue,t1.table_name as" +
                " tabname,t2.column_name as colname,'' as mapping FROM sys_table_info t1 LEFT JOIN sys_table_column t2" +
                " on t1.info_id = t2.info_id WHERE t1.is_trace = ?").addParam(IsFlag.Shi.getCode());
        asmSql.addSql(") aa order by sourecolvalue");
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
                    String tableName = influences_data.get("tabname").toString();
                    //过滤重复的模型表名称
                    if (!set.contains(table_name)) {
                        Map<String, Object> map = new HashMap<>();
                        set.add(tableName);
                        map.put("name", table_name);
                        influencesResult.add(map);
                    }
                });
            }
            //IsFlag.Shi 代表1:字段查看
            else if (is_st == IsFlag.Shi) {
                //初始化映射信息map
                Map<String, List<Map<String, Object>>> map = new HashMap<>();
                influences_data_s.forEach(influences_data -> {
                    //源表列名称
                    String sourecolvalue = influences_data.get("sourecolvalue").toString();
                    //模型表名称
                    String tabname = influences_data.get("tabname").toString();
                    //模型表字段
                    String colname = influences_data.get("colname").toString();
                    //映射规则
                    String mapping = influences_data.get("mapping").toString();
                    //如果有mapping信息就拼上
                    StringBuilder col_mapping = new StringBuilder();
                    col_mapping.append(colname);
                    //处理含有映射关系的数据列
                    if (!StringUtil.isBlank(mapping)) {
                        col_mapping.append('(').append(mapping).append(')');
                    }
                    //过滤相同的列
                    if (!map.containsKey(sourecolvalue)) {
                        //模型列映射源表的列信息 [{name : sourceObj}]
                        List<Map<String, Object>> map_col_list = new ArrayList<>();
                        //模型表的信息 source_table_map:{sourceName : sourceName , children : sourceColArray}
                        Map<String, Object> source_table_map = new HashMap<>();
                        source_table_map.put("name", tabname);
                        //模型表的列信息 [source_table_col_list]
                        List<Map<String, Object>> source_table_col_list = new ArrayList<>();
                        //列信息 source_table_col_map:{name : colName}
                        Map<String, Object> source_table_col_map = new HashMap<>();
                        source_table_col_map.put("name", col_mapping);
                        source_table_col_list.add(source_table_col_map);
                        source_table_map.put("children", source_table_col_list);
                        map_col_list.add(source_table_map);
                        //设置map
                        map.put(sourecolvalue, map_col_list);
                    } else {
                        //模型表的信息 {name : sourceName , children : sourceColArray}
                        Map<String, Object> source_table_info = new HashMap<>();
                        source_table_info.put("name", tabname);
                        //模型表的列信息 source_table_col_info_s:[source_table_col_info]
                        List<Map<String, Object>> source_table_col_info_s = new ArrayList<>();
                        Map<String, Object> source_table_col_info = new HashMap<>();
                        source_table_col_info.put("name", col_mapping);
                        source_table_col_info_s.add(source_table_col_info);
                        source_table_info.put("children", source_table_col_info_s);
                        //将新增的数据放入到源表列信息中
                        map.get(sourecolvalue).add(source_table_info);
                    }
                });
                //循环处理需要的数据结构
                map.forEach((k, v) -> {
                    Set<Map.Entry<String, List<Map<String, Object>>>> entrySet = map.entrySet();
                    for (Map.Entry<String, List<Map<String, Object>>> entry : entrySet) {
                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("name", entry.getKey());
                        map1.put("children", entry.getValue());
                        influencesResult.add(map1);
                    }
                });
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
    public Map<String, Object> bloodlineDateInfo(String table_name, String search_type) {
        //初始化查询Sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select * from (");
        asmSql.addSql("select T1.tabname,colname,souretabname,sourecolvalue,mapping from edw_table T1 join" +
                " edw_mapping T2 on T1.tabname = T2.tabname where lower(T1.tabname) = lower(?)").addParam(table_name);
        asmSql.addSql("and T1.end_dt = ? and T2.end_dt = ?").addParam(Constant.MAXDATE).addParam(Constant.MAXDATE);
        asmSql.addSql("union all");
        asmSql.addSql(" select datatable_en_name tabname,targetfield_name colname,own_source_table_name souretabname," +
                " sourcefields_name sourecolvalue,'' as mapping from datatable_info di join datatable_own_source_info" +
                " dosi on di.datatable_id = dosi.datatable_id join etlmap_info ei on" +
                " dosi.own_dource_table_id = ei.own_dource_table_id and di.datatable_id = ei.datatable_id where" +
                " datatable_en_name = lower(?) ").addParam(table_name);
        asmSql.addSql(" union all");
        asmSql.addSql(" select t1.table_name tabname,t2.column_name colname,t2.colsourcetab souretabname,t2.colsourcecol" +
                " sourecolvalue,'' AS MAPPING from sys_table_info t1 join sys_table_column t2 ON t1.info_id = t2.info_id" +
                " WHERE t1.is_trace = ? AND lower(table_name) = lower(?)")
                .addParam(IsFlag.Shi.getCode()).addParam(table_name);
        asmSql.addSql(" ) aa order by souretabname,colname");
        //获取源表名影响的模型表信息
        List<Map<String, Object>> bloodline_data_s = Dbo.queryList(asmSql.sql(), asmSql.params());
        //表对应列信息
        List<Map<String, Object>> table_col_s = new ArrayList<>();
        if (!bloodline_data_s.isEmpty()) {
            //搜索类型
            IsFlag is_st = IsFlag.ofEnumByCode(search_type);
            //IsFlag.Fou 代表0:表查看
            if (is_st == IsFlag.Fou) {
                Set<String> set = new HashSet<>();
                bloodline_data_s.forEach(bloodline_data -> {
                    //获取模型表名称
                    String tableName = bloodline_data.get("souretabname").toString();
                    //过滤重复的模型表名称
                    if (!set.contains(table_name)) {
                        Map<String, Object> map = new HashMap<>();
                        set.add(tableName);
                        map.put("name", table_name);
                        table_col_s.add(map);
                    }
                });
            } //IsFlag.Shi 代表1:字段查看
            else if (is_st == IsFlag.Shi) {
                //字段对应映射信息map
                Map<String, List<Map<String, Object>>> map = new HashMap<>();
                bloodline_data_s.forEach(bloodline_data -> {
                    //源表名称
                    String souretabname = bloodline_data.get("souretabname").toString();
                    //源表字段
                    String sourecolvalue = bloodline_data.get("sourecolvalue").toString();
                    //模型列名称
                    String colname = bloodline_data.get("colname").toString();
                    //过滤相同的列
                    if (!map.containsKey(colname)) {
                        //模型列映射源表的列信息 [{name : sourceObj}]
                        List<Map<String, Object>> map_col_list = new ArrayList<>();
                        //源表的信息 {sourceName : sourceName , children : sourceColArray}
                        Map<String, Object> source_table_map = new HashMap<>();
                        source_table_map.put("name", souretabname);
                        //源的列信息 [source_table_col_list]
                        List<Map<String, Object>> source_table_col_list = new ArrayList<>();
                        //列信息 source_table_col_map:{name : colName}
                        Map<String, Object> source_table_col_map = new HashMap<>();
                        source_table_col_map.put("name", sourecolvalue);
                        source_table_col_list.add(source_table_col_map);
                        source_table_map.put("children", source_table_col_list);
                        map_col_list.add(source_table_map);
                        map.put(colname, map_col_list);
                    } else {
                        //源表的信息 {sourceName : sourceName , children : sourceColArray}
                        Map<String, Object> source_table_map = new HashMap<>();
                        source_table_map.put("name", souretabname);
                        //源的列信息 [source_table_col_list]
                        List<Map<String, Object>> source_table_col_list = new ArrayList<>();
                        //列信息 source_table_col_map:{name : colName}
                        Map<String, Object> source_table_col_map = new HashMap<>();
                        source_table_col_map.put("name", sourecolvalue);
                        source_table_col_list.add(source_table_col_map);
                        source_table_map.put("children", source_table_col_list);
                        //将新增的数据放入到源表列信息中
                        map.get(colname).add(source_table_map);
                    }
                });
                //循环处理需要的数据结构
                map.forEach((k, v) -> {
                    Set<Map.Entry<String, List<Map<String, Object>>>> entrySet = map.entrySet();
                    for (Map.Entry<String, List<Map<String, Object>>> entry : entrySet) {
                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("name", entry.getKey());
                        map1.put("children", entry.getValue());
                        table_col_s.add(map1);
                    }
                });
            } else {
                throw new BusinessException("搜索类型不匹配! search_type=" + search_type);
            }
        }

        //初始化当前模型表的全部映射关系信息
        Map<String, Object> bloodlineDateInfoMap = new HashMap<>();
        bloodlineDateInfoMap.put("name", table_name);
        bloodlineDateInfoMap.put("children", table_col_s);
        return bloodlineDateInfoMap;
    }

}
