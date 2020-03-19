package hrds.k.biz.tsbenchmarking.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.exception.BusinessException;

import java.util.List;
import java.util.Map;

@DocClass(desc = "表结构对标查询类", author = "BY-HLL", createdate = "2020/3/16 0016 下午 05:54")
public class TSBQuery {

    @Method(desc = "根据表id获取DCL层批量表信息", logicStep = "根据表id获取DCL层批量表信息")
    @Param(name = "file_id", desc = "表源属性id", range = "String字符串,唯一")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static Map<String, Object> gerDCLBatchTableInfoById(String file_id) {
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT ti.table_ch_name,ti.table_name,ti.remark,ti.table_id,sfa.source_id,sfa.agent_id," +
                " ti.database_id FROM source_file_attribute sfa JOIN table_info ti ON sfa.collect_set_id = ti" +
                ".database_id AND sfa.table_name = ti.table_name WHERE sfa.file_id =?").addParam(file_id);
        return Dbo.queryOneObject(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "根据表id获取DCL层批量表字段",
            logicStep = "根据表id获取DCL层批量表字段")
    @Param(name = "file_id", desc = "表源属性id", range = "String字符串,唯一")
    @Return(desc = "表字段信息列表", range = "返回值取值范围")
    public static List<Map<String, Object>> getDCLBatchTableColumnsById(String file_id) {
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT tc.column_ch_name,tc.column_name,tc.tc_remark,tc.column_type,tc.is_primary_key," +
                "tc.column_id,ti.database_id,sfa.agent_id,sfa.source_id FROM source_file_attribute sfa JOIN " +
                "table_info ti ON sfa.collect_set_id = ti.database_id AND sfa.table_name = ti.table_name JOIN " +
                "table_column tc ON ti.table_id = tc.table_id  WHERE sfa.file_id = ?").addParam(file_id);
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "根据表id获取DCL层实时表字段",
            logicStep = "根据表id获取DCL层实时表字段")
    @Param(name = "file_id", desc = "表源属性id", range = "String字符串,唯一")
    @Return(desc = "表字段信息列表", range = "返回值取值范围")
    public static List<Map<String, Object>> getDCLBatchRealTimeTableColumnsById(String file_id) {
        throw new BusinessException("获取实时数据表的字段信息暂未实现!");
    }
}
