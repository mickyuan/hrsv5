package hrds.commons.tree.background.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.entity.Dq_table_column;
import hrds.commons.entity.Dq_table_info;
import hrds.commons.exception.BusinessException;

import java.util.List;
import java.util.Map;

@DocClass(desc = "自定义层(UDL)数据信息查询类", author = "BY-HLL", createdate = "2020/7/7 0007 上午 11:14")
public class UDLDataQuery {

    @Method(desc = "获取自定义层(UDL)下表信息", logicStep = "获取自定义层(UDL)下表信息")
    @Param(name = "table_id", desc = "表id,唯一", range = "long类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static Dq_table_info getUDLTableInfo(String table_id) {
        //设置自定义层数据表
        Dq_table_info dq_table_info = new Dq_table_info();
        dq_table_info.setTable_id(table_id);
        //查询自定义层数据表信息
        return Dbo.queryOneObject(Dq_table_info.class, "select * from " + Dq_table_info.TableName + " where table_id" +
                "=?", dq_table_info.getTable_id()).orElseThrow(() -> new BusinessException("获取UDL数据表信息的sql失败!"));
    }

    @Method(desc = "获取自定义层(UDL)下表字段信息", logicStep = "获取自定义层(UDL)下表字段信息")
    @Param(name = "table_id", desc = "表id,唯一", range = "long类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Map<String, Object>> getUDLTableColumns(String table_id) {
        //初始化sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        //设置自定义层数据表
        Dq_table_info dq_table_info = new Dq_table_info();
        dq_table_info.setTable_id(table_id);
        //查询表字段信息
        asmSql.addSql("select field_id AS column_id,column_name as column_name," +
                " field_ch_name as column_ch_name,concat(column_type,'(',column_length,')') AS column_type," +
                " '0' AS is_primary_key FROM " + Dq_table_column.TableName + " WHERE table_id=?");
        asmSql.addParam(dq_table_info.getTable_id());
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }
}
