package hrds.commons.tree.background.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.entity.Data_store_layer;
import hrds.commons.entity.Dq_index3record;
import hrds.commons.exception.BusinessException;

import java.util.List;
import java.util.Map;

@DocClass(desc = "管控层(DQC)层数据信息查询类", author = "BY-HLL", createdate = "2020/1/7 0007 上午 11:17")
public class DQCDataQuery {

    @Method(desc = "获取数据管控信息", logicStep = "1.获取数据管控信息")
    @Param(name = "tableName", desc = "数据管控表名", range = "String类型,长度64")
    @Return(desc = "数据管控信息列表", range = "无限制")
    public static List<Data_store_layer> getDQCDataInfos() {
        //1.获取DQC数据层下存在数据表的存储层信息
        return Dbo.queryList(Data_store_layer.class, "SELECT DISTINCT dsl.* FROM " + Dq_index3record.TableName +
                " di3 LEFT JOIN " + Data_store_layer.TableName + " dsl ON dsl.dsl_id = di3.dsl_id");
    }

    @Method(desc = "获取DQC数据存储层下表信息", logicStep = "1.获取DQC数据存储层下表信息")
    @Param(name = "dsl_id", desc = "存储层id", range = "long类型")
    @Return(desc = "数据管控信息列表", range = "无限制")
    public static List<Map<String, Object>> getDQCTableInfos(long dsl_id) {
        //1.获取DQC数据存储层下表信息
        return Dbo.queryList("SELECT dsl.*,di3.* FROM " + Dq_index3record.TableName + " di3 LEFT JOIN " +
                Data_store_layer.TableName + " dsl ON dsl.dsl_id = di3.dsl_id where di3.dsl_id=?", dsl_id);
    }

    @Method(desc = "根据表id获取DQC层表信息", logicStep = "根据表id获取DQC层表信息")
    @Param(name = "file_id", desc = "表记录id", range = "String字符串,唯一")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static Dq_index3record getDQCTableInfo(String file_id) {
        //设置 Dq_index3record 对象
        Dq_index3record dq_index3record = new Dq_index3record();
        dq_index3record.setRecord_id(file_id);
        //初始化sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT * FROM " + Dq_index3record.TableName + " WHERE record_id = ?");
        asmSql.addParam(dq_index3record.getRecord_id());
        return Dbo.queryOneObject(Dq_index3record.class, asmSql.sql(), asmSql.params())
                .orElseThrow(() -> (new BusinessException("获取DQC层表信息的sql失败")));
    }
}
