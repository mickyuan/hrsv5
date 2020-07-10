package hrds.k.biz.dm.metadatamanage.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.JobExecuteState;
import hrds.commons.codes.StoreLayerDataSource;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;

import java.util.List;
import java.util.Map;

@DocClass(desc = "数据管控-源数据列表树数据查询类", author = "BY-HLL", createdate = "2020/4/1 0001 下午 02:11")
public class MDMDataQuery {

    @Method(desc = "数据管控-源数据列表获取DCL数据存储层信息", logicStep = "数据管控-源数据列表获取数据存储层信息")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Data_store_layer> getDCLExistTableDataStorageLayers() {
        //获取数据存储层信息列表
        return Dbo.queryList(Data_store_layer.class, "SELECT dsl.* FROM " + Table_info.TableName + " ti" +
                        " JOIN " + Table_storage_info.TableName + " tsi ON tsi.table_id = ti.table_id" +
                        " JOIN " + Dtab_relation_store.TableName + " dtrs ON dtrs.tab_id = tsi.storage_id" +
                        " JOIN " + Data_store_layer.TableName + " dsl ON dsl.dsl_id = dtrs.dsl_id" +
                        " WHERE dtrs.data_source in (?,?,?) AND is_successful=? GROUP BY dsl.dsl_id",
                StoreLayerDataSource.DB.getCode(), StoreLayerDataSource.DBA.getCode(),
                StoreLayerDataSource.OBJ.getCode(), JobExecuteState.WanCheng.getCode());
    }

    @Method(desc = "数据管控-源数据列表获取DCL数据存储层下的表信息",
            logicStep = "数据管控-源数据列表获取数据存储层下的表信息")
    @Param(name = "data_store_layer", desc = "Data_store_layer实体对象", range = "Data_store_layer实体对象")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Map<String, Object>> getDCLStorageLayerTableInfos(Data_store_layer data_store_layer) {
        return Dbo.queryList("SELECT dsl.*,dsr.* FROM " + Data_store_layer.TableName + " dsl" +
                        " JOIN " + Dtab_relation_store.TableName + " dtrs ON dsl.dsl_id = dtrs.dsl_id" +
                        " JOIN " + Table_storage_info.TableName + " tsi ON dtrs.tab_id = tsi.storage_id" +
                        " JOIN " + Table_info.TableName + " ti ON ti.table_id = tsi.table_id" +
                        " JOIN " + Data_store_reg.TableName + " dsr ON dsr.table_id = ti.table_id" +
                        " WHERE dsl.dsl_id = ? and dtrs.data_source in (?,?,?)",
                data_store_layer.getDsl_id(), StoreLayerDataSource.DB.getCode(), StoreLayerDataSource.DBA.getCode(),
                StoreLayerDataSource.OBJ.getCode());
    }

    @Method(desc = "根据表id获取DCL层数据表登记信息", logicStep = "根据表id获取DCL层数据表登记信息")
    @Param(name = "file_id", desc = "表登记id", range = "String字符串,唯一")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static Data_store_reg getDCLDataStoreRegInfo(String file_id) {
        Data_store_reg dsr = new Data_store_reg();
        dsr.setFile_id(file_id);
        return Dbo.queryOneObject(Data_store_reg.class, "SELECT * from " + Data_store_reg.TableName + " WHERE file_id =?",
                dsr.getFile_id()).orElseThrow(() -> (new BusinessException("获取数据登记信息的SQL失败!")));
    }

    @Method(desc = "根据表id和存储层id获取DCL层数据表登记关系信息", logicStep = "根据表id和存储层id获取DCL层数据表登记关系信息")
    @Param(name = "dsl_id", desc = "存储层id", range = "long字符串,唯一")
    @Param(name = "dsr", desc = "Data_store_reg实体对象", range = "DCL数据层表登记信息")
    @Return(desc = "数据表登记关系信", range = "数据表登记关系信")
    public static Dtab_relation_store getDCLTableSpecifyStorageRelationship(long dsl_id, Data_store_reg dsr) {
        return Dbo.queryOneObject(Dtab_relation_store.class,
                "SELECT dtrs.* from " + Data_store_layer.TableName + " dsl left join " + Dtab_relation_store.TableName + " dtrs" +
                        " on dsl.dsl_id=dtrs.dsl_id left join " + Table_storage_info.TableName + " tsi on tsi.storage_id=dtrs.tab_id" +
                        " left join " + Table_info.TableName + " ti on ti.table_id=tsi.table_id" +
                        " where dsl.dsl_id=? and ti.table_id=? and dtrs.data_source in (?,?,?)", dsl_id, dsr.getTable_id(),
                StoreLayerDataSource.DB.getCode(), StoreLayerDataSource.DBA.getCode(), StoreLayerDataSource.OBJ.getCode())
                .orElseThrow(() -> (new BusinessException("根据存储层id,表id和 存储层关系-数据来源 获取数据表的存储关系信息的SQL出错!")));
    }

    @Method(desc = "根据表id和存储层id获取DCL层数据表登记关系信息", logicStep = "根据表id和存储层id获取DCL层数据表登记关系信息")
    @Param(name = "dsr", desc = "Data_store_reg实体对象", range = "DCL数据层表登记信息")
    @Return(desc = "数据表登记关系信", range = "数据表登记关系信")
    public static List<Dtab_relation_store> getDCLTableStorageRelationships(Data_store_reg dsr) {
        return Dbo.queryList(Dtab_relation_store.class,
                "SELECT dtrs.* from " + Data_store_layer.TableName + " dsl left join " + Dtab_relation_store.TableName + " dtrs" +
                        " on dsl.dsl_id=dtrs.dsl_id left join " + Table_storage_info.TableName + " tsi on tsi.storage_id=dtrs.tab_id" +
                        " left join " + Table_info.TableName + " ti on ti.table_id=tsi.table_id" +
                        " where ti.table_id=? and dtrs.data_source in (?,?,?)", dsr.getTable_id(),
                StoreLayerDataSource.DB.getCode(), StoreLayerDataSource.DBA.getCode(), StoreLayerDataSource.OBJ.getCode());
    }

    @Method(desc = "数据管控-源数据列表获取DML数据存储层信息", logicStep = "数据管控-源数据列表获取数据存储层信息")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Data_store_layer> getDMLExistTableDataStorageLayers() {
        //获取数据存储层信息列表
        return Dbo.queryList(Data_store_layer.class, "SELECT dsl.* FROM " + Data_store_layer.TableName + " dsl" +
                        " JOIN " + Dtab_relation_store.TableName + " dtrs ON dtrs.dsl_id = dsl.dsl_id" +
                        " JOIN " + Dm_datatable.TableName + " dd ON dd.datatable_id = dtrs.tab_id" +
                        " WHERE dtrs.data_source in (?) AND is_successful=? GROUP BY dsl.dsl_id", StoreLayerDataSource.DM.getCode(),
                JobExecuteState.WanCheng.getCode());
    }

    @Method(desc = "数据管控-源数据列表获取DML数据存储层下的表信息", logicStep = "数据管控-源数据列表获取数据存储层下的表信息")
    @Param(name = "data_store_layer", desc = "Data_store_layer实体对象", range = "Data_store_layer实体对象")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Map<String, Object>> getDMLStorageLayerTableInfos(Data_store_layer data_store_layer) {
        return Dbo.queryList("SELECT dsl.*,dd.* FROM " + Dm_datatable.TableName + " dd" +
                        " JOIN " + Dtab_relation_store.TableName + " dtrs ON dtrs.tab_id = dd.datatable_id" +
                        " JOIN " + Data_store_layer.TableName + " dsl ON dsl.dsl_id = dtrs.dsl_id" +
                        " WHERE dsl.dsl_id = ? and is_successful = ? and dtrs.data_source = ?",
                data_store_layer.getDsl_id(), JobExecuteState.WanCheng.getCode(), StoreLayerDataSource.DM.getCode());
    }

    @Method(desc = "根据表id获取DML层数据表登记信息", logicStep = "根据表id获取DML层数据表登记信息")
    @Param(name = "datatable_id", desc = "表登记id", range = "String字符串,唯一")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static Dm_datatable getDMLDmDatatableInfo(String datatable_id) {
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        return Dbo.queryOneObject(Dm_datatable.class, "SELECT * from " + Dm_datatable.TableName + " WHERE datatable_id =?",
                dm_datatable.getDatatable_id()).orElseThrow(() -> (new BusinessException("获取DML数据登记信息的SQL失败!")));
    }

    @Method(desc = "根据表id和存储层id获取DML层数据表登记关系信息", logicStep = "根据表id和存储层id获取DML层数据表登记关系信息")
    @Param(name = "dsl_id", desc = "存储层id", range = "long字符串,唯一")
    @Param(name = "dsr", desc = "Data_store_reg实体对象", range = "DCL数据层表登记信息")
    @Return(desc = "数据表登记关系信", range = "数据表登记关系信")
    public static Dtab_relation_store getDMLTableSpecifyStorageRelationship(long dsl_id, Dm_datatable dm_datatable) {
        return Dbo.queryOneObject(Dtab_relation_store.class, "SELECT dtrs.* from " + Data_store_layer.TableName + " dsl" +
                        " left join " + Dtab_relation_store.TableName + " dtrs on dsl.dsl_id=dtrs.dsl_id" +
                        " left join " + Dm_datatable.TableName + " dmd on dmd.datatable_id=dtrs.tab_id" +
                        " where dsl.dsl_id=? and dmd.datatable_id=? and dtrs.data_source in (?)",
                dsl_id, dm_datatable.getDatatable_id(), StoreLayerDataSource.DM.getCode())
                .orElseThrow(() -> (new BusinessException("根据存储层id,表id和 存储层关系-数据来源 获取数据表的存储关系信息的SQL出错!")));
    }

    @Method(desc = "根据表id和存储层id获取DCL层数据表登记关系信息", logicStep = "根据表id和存储层id获取DCL层数据表登记关系信息")
    @Param(name = "dsr", desc = "Data_store_reg实体对象", range = "DCL数据层表登记信息")
    @Return(desc = "数据表登记关系信", range = "数据表登记关系信")
    public static List<Dtab_relation_store> getDMLTableStorageRelationships(Dm_datatable dm_datatable) {
        return Dbo.queryList(Dtab_relation_store.class, "SELECT dtrs.* from " + Data_store_layer.TableName + " dsl" +
                        " left join " + Dtab_relation_store.TableName + " dtrs on dsl.dsl_id=dtrs.dsl_id" +
                        " left join " + Dm_datatable.TableName + " dmd on dmd.datatable_id=dtrs.tab_id" +
                        " where dmd.datatable_id=? and dtrs.data_source in (?)",
                dm_datatable.getDatatable_id(), StoreLayerDataSource.DM.getCode()
        );
    }

    @Method(desc = "根据表id获取DQC层数据表信息", logicStep = "根据表id获取DQC层数据表信息")
    @Param(name = "file_id", desc = "表登记id", range = "String字符串,唯一")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static Dq_index3record getDQCDqIndex3record(String file_id) {
        Dq_index3record dq_index3record = new Dq_index3record();
        dq_index3record.setRecord_id(file_id);
        return Dbo.queryOneObject(Dq_index3record.class, "SELECT * from " + Dq_index3record.TableName + " WHERE " +
                        "record_id=?",
                dq_index3record.getRecord_id()).orElseThrow(() -> (new BusinessException("获取DQC数据登记信息的SQL失败!")));
    }

    @Method(desc = "UDL数据层下,获取配置登记的所有数据存储层信息", logicStep = "UDL数据层下,获取配置登记的所有数据存储层信息")
    @Return(desc = "所有存储层登记信息列表", range = "所有存储层登记信息列表")
    public static List<Data_store_layer> getUDLDataStorageLayers() {
        return Dbo.queryList(Data_store_layer.class, "SELECT * from " + Data_store_layer.TableName);
    }

    @Method(desc = "数据管控-源数据列表获取UDL数据存储层下的表信息", logicStep = "数据管控-源数据列表获取UDL数据存储层下的表信息")
    @Param(name = "data_store_layer", desc = "Data_store_layer实体对象", range = "Data_store_layer实体对象")
    public static List<Map<String, Object>> getUDLStorageLayerTableInfos(Data_store_layer data_store_layer) {
        return Dbo.queryList("select dsl.*,dqt.* from " + Dq_table_info.TableName + " dqt" +
                        " JOIN " + Dtab_relation_store.TableName + " dtrs ON dtrs.tab_id = dqt.table_id" +
                        " JOIN " + Data_store_layer.TableName + " dsl ON dsl.dsl_id = dtrs.dsl_id" +
                        " WHERE dsl.dsl_id=? and is_successful=? and dtrs.data_source=?", data_store_layer.getDsl_id(),
                JobExecuteState.WanCheng.getCode(), StoreLayerDataSource.UD.getCode());
    }

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

    @Method(desc = "根据表id和存储层id获取DCL层数据表登记关系信息", logicStep = "根据表id和存储层id获取DCL层数据表登记关系信息")
    @Param(name = "dsl_id", desc = "存储层id", range = "long字符串,唯一")
    @Param(name = "dti", desc = "Dq_table_info实体对象", range = "DCL数据层表登记信息")
    @Return(desc = "数据表登记关系信", range = "数据表登记关系信")
    public static Dtab_relation_store getUDLTableSpecifyStorageRelationship(long dsl_id, Dq_table_info dti) {
        return Dbo.queryOneObject(Dtab_relation_store.class, "SELECT dtrs.* from " + Data_store_layer.TableName + " dsl" +
                        " left join " + Dtab_relation_store.TableName + " dtrs on dsl.dsl_id=dtrs.dsl_id" +
                        " left join " + Dq_table_info.TableName + " dti on dti.table_id=dtrs.tab_id" +
                        " where dsl.dsl_id=? and dti.table_id and dtrs.data_source in (?)",
                dsl_id, dti.getTable_name(), StoreLayerDataSource.UD.getCode())
                .orElseThrow(() -> (new BusinessException("根据存储层id,表id和 存储层关系-数据来源 获取数据表的存储关系信息的SQL出错!")));
    }

    @Method(desc = "根据表id和存储层id获取DCL层数据表登记关系信息", logicStep = "根据表id和存储层id获取DCL层数据表登记关系信息")
    @Param(name = "dsr", desc = "Data_store_reg实体对象", range = "DCL数据层表登记信息")
    @Return(desc = "数据表登记关系信", range = "数据表登记关系信")
    public static List<Dtab_relation_store> getUDLTableStorageRelationships(Dq_table_info dti) {
        return Dbo.queryList(Dtab_relation_store.class, "SELECT dtrs.* from " + Data_store_layer.TableName + " dsl" +
                        " left join " + Dtab_relation_store.TableName + " dtrs on dsl.dsl_id=dtrs.dsl_id" +
                        " left join " + Dq_table_info.TableName + " dti on dti.table_id=dtrs.tab_id" +
                        " where dti.table_id and dtrs.data_source in (?)",
                dti.getTable_id(), StoreLayerDataSource.UD.getCode()
        );
    }

    @Method(desc = "根据表id删除DCL层数据表登记信息", logicStep = "根据表id删除DCL层数据表登记信息")
    @Param(name = "file_id", desc = "表登记id", range = "String字符串,唯一")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void deleteDCLDataStoreRegInfo(String file_id) {
        Data_store_reg dsr = new Data_store_reg();
        dsr.setFile_id(file_id);
        int execute = Dbo.execute("DELETE FROM " + Data_store_reg.TableName + " WHERE file_id =?", dsr.getFile_id());
        if (execute != 1) {
            throw new BusinessException("删除采集表登记信息失败! file_id=" + file_id);
        }
    }

    @Method(desc = "根据表id删除DML层数据表登记信息", logicStep = "根据表id删除DML层数据表登记信息")
    @Param(name = "datatable_id", desc = "表登记id", range = "String字符串,唯一")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void deleteDMLDmDataTable(String datatable_id) {
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        int execute = Dbo.execute("DELETE FROM " + Dm_datatable.TableName + " WHERE datatable_id =?",
                dm_datatable.getDatatable_id());
        if (execute != 1) {
            throw new BusinessException("删除集市表登记信息失败! datatable_id=" + dm_datatable.getDatatable_id());
        }
    }

    @Method(desc = "根据表id删除DQC层数据表登记信息", logicStep = "根据表id删除DQC层数据表登记信息")
    @Param(name = "record_id", desc = "表登记id", range = "String字符串,唯一")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void deleteDQCDqIndex3record(String record_id) {
        Dq_index3record dq_index3record = new Dq_index3record();
        dq_index3record.setRecord_id(record_id);
        DboExecute.deletesOrThrow("删除DQC登记信息失败!", "DELETE FROM " + Dq_index3record.TableName +
                " WHERE record_id=?", dq_index3record.getRecord_id());

    }

    @Method(desc = "根据表id删除DML层数据表登记信息", logicStep = "根据表id删除DML层数据表登记信息")
    @Param(name = "table_id", desc = "表登记id", range = "String字符串,唯一")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void deleteUDLDqTableInfo(long table_id) {
        DboExecute.deletesOrThrow("删除UDL登记信息失败!", "DELETE FROM " + Dq_table_info.TableName +
                " WHERE table_id=?", table_id);
    }
}
