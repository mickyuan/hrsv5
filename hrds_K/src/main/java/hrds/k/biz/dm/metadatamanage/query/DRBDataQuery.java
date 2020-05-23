package hrds.k.biz.dm.metadatamanage.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.web.util.Dbo;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;

import java.util.List;
import java.util.Map;

@DocClass(desc = "数据管控-数据回收站数据查询类", author = "BY-HLL", createdate = "2020/1/7 0007 上午 11:10")
public class DRBDataQuery {

    @Method(desc = "数据管控-数据回收站获取所有表信息",
            logicStep = "数据管控-数据回收站获取所有表信息")
    @Return(desc = "回收站所有表信息", range = "回收站所有表信息")
    public static List<Dq_failure_table> getAllTableInfos() {
        return Dbo.queryList(Dq_failure_table.class, "SELECT * FROM " + Dq_failure_table.TableName);
    }

    @Method(desc = "数据管控-数据回收站获取存储层下表信息",
            logicStep = "数据回收站获取存储层下表信息")
    @Return(desc = "指定数据存储下的无效表的列表", range = "无限制")
    public static List<Map<String, Object>> getDCLStorageLayerTableInfos() {
        return Dbo.queryList("SELECT dsl.*,dft.* FROM " + Dq_failure_table.TableName + " dft" +
                " JOIN " + Table_info.TableName + " ti ON CAST(ti.table_id AS VARCHAR(40)) = dft.file_id" +
                " JOIN " + Table_storage_info.TableName + " tsi ON tsi.table_id = ti.table_id" +
                " JOIN " + Data_relation_table.TableName + " drt ON drt.storage_id = tsi.storage_id" +
                " JOIN " + Data_store_layer.TableName + " dsl ON dsl.dsl_id = drt.dsl_id");
    }

    @Method(desc = "数据管控-数据回收站获取存储层下表信息",
            logicStep = "数据回收站获取存储层下表信息")
    @Return(desc = "指定数据存储下的无效表的列表", range = "无限制")
    public static List<Map<String, Object>> getDMLStorageLayerTableInfos() {
        return Dbo.queryList("SELECT dsl.*,dft.* FROM " + Dq_failure_table.TableName + " dft" +
                " JOIN " + Dm_relation_datatable.TableName + " drd ON CAST(drd.datatable_id AS VARCHAR(40)) = dft.file_id" +
                " JOIN " + Data_store_layer.TableName + " dsl ON dsl.dsl_id = drd.dsl_id ");
    }

    @Method(desc = "数据管控-源数据列表获取数据存储层信息", logicStep = "数据管控-源数据列表获取数据存储层信息")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Data_store_layer> getDCLExistTableDataStorageLayers() {
        return Dbo.queryList(Data_store_layer.class, "SELECT dsl.* FROM " + Data_store_layer.TableName + " dsl" +
                " JOIN " + Data_relation_table.TableName + " drt ON dsl.dsl_id = drt.dsl_id" +
                " JOIN " + Table_storage_info.TableName + " tsi ON tsi.storage_id = drt.storage_id" +
                " JOIN " + Table_info.TableName + " ti ON ti.table_id = tsi.table_id" +
                " JOIN " + Dq_failure_table.TableName + " dft ON dft.file_id = CAST(ti.table_id AS VARCHAR(40))" +
                " GROUP BY dsl.dsl_id");
    }

    @Method(desc = "数据管控-源数据列表获取数据存储层信息", logicStep = "数据管控-源数据列表获取数据存储层信息")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Data_store_layer> getDMLExistTableDataStorageLayers() {
        return Dbo.queryList(Data_store_layer.class, "SELECT dsl.* FROM " + Dq_failure_table.TableName + " dft" +
                " JOIN " + Dm_relation_datatable.TableName + " drd" +
                " ON cast(drd.datatable_id as varchar(40)) = dft.file_id" +
                " JOIN " + Data_store_layer.TableName + " dsl ON dsl.dsl_id = drd.dsl_id" +
                " GROUP BY dsl.dsl_id");
    }

    @Method(desc = "数据管控-数据回收站根据表的id获取表信息",
            logicStep = "数据管控-数据回收站根据表的id获取表信息")
    @Param(name = "failure_table_id", desc = "回收站的表id", range = "long类型,该值唯一")
    @Return(desc = "表的元信息", range = "返回值取值范围")
    public static Dq_failure_table getDRBTableInfo(long failure_table_id) {
        //设置回收站表信息
        Dq_failure_table dft = new Dq_failure_table();
        dft.setFailure_table_id(failure_table_id);
        return Dbo.queryOneObject(Dq_failure_table.class, "SELECT * FROM " + Dq_failure_table.TableName +
                " WHERE failure_table_id = ?", dft.getFailure_table_id()).orElseThrow(()
                -> (new BusinessException("根据id获取回收站表信息的SQL失败!")));
    }

    @Method(desc = "根据回收站表id删除回收站表信息", logicStep = "根据回收站表id删除回收站表信息")
    @Param(name = "file_id", desc = "表登记id", range = "Long类型,唯一")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void deleteDqFailureTableInfo(long failure_table_id) {
        Dq_failure_table dft = new Dq_failure_table();
        dft.setFailure_table_id(failure_table_id);
        int execute = Dbo.execute("DELETE FROM " + Dq_failure_table.TableName + " WHERE failure_table_id=?",
                dft.getFailure_table_id());
        if (execute != 1) {
            throw new BusinessException("删除回收站表登记信息失败! failure_table_id=" + failure_table_id);
        }
    }
}
