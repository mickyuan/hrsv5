package hrds.k.biz.dm.metadatamanage.transctrl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.DataSourceType;
import hrds.commons.entity.Dm_datatable;
import hrds.commons.entity.Dm_datatable_source;
import hrds.commons.entity.Dq_definition;
import hrds.commons.entity.Dq_rule_def;
import hrds.commons.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@DocClass(desc = "删除表互斥检查类", author = "BY-HLL", createdate = "2020/5/22 0022 下午 01:19")
public class IOnWayCtrl {

    private static String ERROR_MUTES = "该任务下的数据表有其他任务在使用，请前往先进行删除!";

    @Method(desc = "待删除表的依赖检查",
            logicStep = "待删除表的依赖检查")
    @Param(name = "table", desc = "表名", range = "String")
    public static void checkExistsTask(String table, String _dst, DatabaseWrapper db) {

        List<String> tables = new ArrayList<>();
        tables.add(table);
        checkExistsTask(tables, _dst, db);
    }


    @Method(desc = "待删除表的依赖检查",
            logicStep = "待删除表的依赖检查")
    @Param(name = "tables", desc = "表名列表", range = "String[]")
    public static void checkExistsTask(List<String> tables, String _dst, DatabaseWrapper db) {

        Result rs = new Result();
        try {
            checkDataMartTask(tables, db);
        } catch (MutexException e) {
            rs.add(e.getMutexList());
        }
        try {
            checkDQCTask(tables, db);
        } catch (MutexException e) {
            rs.add(e.getMutexList());
        }
        try {
            checkRESTTask(tables, db);
        } catch (MutexException e) {
            rs.add(e.getMutexList());
        }
        //TODO 后期需要添加其他层的检查DPL,UDL,SFL
        if (!rs.isEmpty()) {
            List<Map<String, Object>> rs_map = rs.toList();
            JSONObject ctrlTable = new JSONObject();
            rs_map.forEach(o -> {
                String souname = o.get("souname").toString();
                if (null == ctrlTable.get(souname)) {
                    JSONArray jsonSource = new JSONArray();
                    jsonSource.add(o);
                    ctrlTable.put(souname, jsonSource);
                } else {
                    JSONArray rsSource = ctrlTable.getJSONArray(souname);
                    rsSource.add(o);
                    ctrlTable.put(souname, rsSource);
                }

            });
            JSONArray array = new JSONArray();
            Set<String> col = ctrlTable.keySet();
            for (String tableName : col) {
                JSONObject obj = new JSONObject();
                JSONArray jsonArray = ctrlTable.getJSONArray(tableName);
                obj.put("tableName", tableName);
                obj.put("info", jsonArray);
                obj.put("len", jsonArray.size());
                array.add(obj);
            }
            throw new BusinessException("该数据表被其他数据表所依赖,请先删除所依赖的表!" + array);
        }
    }


    /**
     * 检查该表是否在集市中存在，如果存在，直接抛出MutexException异常，包含result的异常
     */
    private static void checkDataMartTask(List<String> tables, DatabaseWrapper db) {

        //初始化查询Sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        StringBuffer sb = new StringBuffer();
        /*检查该组表，集市是否使用*/
        sb.append("select datatable_id from " + Dm_datatable_source.TableName + " where " + "own_source_table_name in(");
        for (String table : tables) {
            sb.append("?,");
            asmSql.addParam(table);
        }
        String sql = sb.deleteCharAt(sb.length() - 1).append(") group by datatable_id").toString();
        asmSql.addSql(sql);
        Result rs = Dbo.queryResult(db, asmSql.sql(), asmSql.params());
        if (!rs.isEmpty()) {
            asmSql.clean();
            sb = new StringBuffer();
            sb.append("select datatable_cn_name as cnname,datatable_en_name as enname, '");
            sb.append(DataSourceType.DML.getCode()).append("' as typename ,own_source_table_name as souname ");
            sb.append("  from " + Dm_datatable.TableName + " a join " + Dm_datatable_source.TableName + " b" +
                    " on a.datatable_id = b.datatable_id  ");
            sb.append(" where a.datatable_id in( ");
            for (int i = 0; i < rs.getRowCount(); i++) {
                sb.append("?,");
                asmSql.addParam(rs.getIntDefaultZero(i, "datatable_id"));
            }
            sb.deleteCharAt(sb.length() - 1).append(")");
            sb.append(" group by datatable_cn_name,datatable_en_name,own_source_table_name ");
            asmSql.addSql(sb.toString());
            Result rsMutex = Dbo.queryResult(db, asmSql.sql(), asmSql.params());
            if (!rsMutex.isEmpty()) {
                throw new BusinessException("该数据表被其他数据表所依赖,请先删除所依赖的表!" + rsMutex);
            }
        }
    }

    /**
     * 检查该表是否在数据管控中存在，如果存在，直接抛出MutexException异常，包含result的异常
     */
    private static void checkDQCTask(List<String> tables, DatabaseWrapper db) {

        //初始化查询Sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        StringBuilder sb = new StringBuilder();
        sb.append(" select  reg_name enname,target_tab souname,case_type_desc cnname ,'");
        sb.append(DataSourceType.DQC.getCode()).append("' as typename from ( ");
        sb.append(" select reg_name,target_tab,case_type from " + Dq_definition.TableName + " where target_tab in ( ");
        for (String table : tables) {
            sb.append("?,");
            asmSql.addParam(table);
        }
        sb.deleteCharAt(sb.length() - 1).append(") ");
        sb.append(" union all ");
        sb.append(" select reg_name,opposite_tab,case_type from " + Dq_definition.TableName + " where opposite_tab in(");
        for (String s : tables) {
            sb.append("?,");
            asmSql.addParam(s);
        }
        sb.deleteCharAt(sb.length() - 1).append(") ");
        sb.append(" ) a join " + Dq_rule_def.TableName + " b on a.case_type = b.case_type ");
        asmSql.addSql(sb.toString());
        Result rsMutex = Dbo.queryResult(db, asmSql.sql(), asmSql.params());
        if (!rsMutex.isEmpty()) {
            throw new BusinessException("该数据表被其他数据表所依赖,请先删除所依赖的表!" + rsMutex);
        }
    }

    /**
     * 检查该表是否在接口中存在，如果存在，直接抛出MutexException异常，包含result的异常
     */
    private static void checkRESTTask(List<String> tables, DatabaseWrapper db) {

        //初始化查询Sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        StringBuilder sb = new StringBuilder();
        /*检查该组表，集市是否使用*///TYPE_REST
        sb.append("select sysreg_name souname,a.user_id enname,b.user_name cnname,'接口' as typename ");
        sb.append(" from table_use_info  ");
        sb.append(" a join sys_user b on a.user_id = b.user_id where sysreg_name in ( ");
        for (String table : tables) {
            sb.append("?,");
            asmSql.addParam(table);
        }
        String sql = sb.deleteCharAt(sb.length() - 1).append(") group by sysreg_name,a.user_id,b.user_name").toString();
        asmSql.addSql(sql);
        Result rsMutex = Dbo.queryResult(db, asmSql.sql(), asmSql.params());
        if (!rsMutex.isEmpty()) {
            throw new MutexException(ERROR_MUTES, rsMutex);
        }
    }
}
