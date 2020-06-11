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
                        " WHERE dtrs.data_source in (?,?,?) GROUP BY dsl.dsl_id",
                StoreLayerDataSource.DB.getCode(), StoreLayerDataSource.DBA.getCode(),
                StoreLayerDataSource.OBJ.getCode());
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

    @Method(desc = "数据管控-源数据列表获取DML数据存储层信息", logicStep = "数据管控-源数据列表获取数据存储层信息")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Data_store_layer> getDMLExistTableDataStorageLayers() {
        //获取数据存储层信息列表
        return Dbo.queryList(Data_store_layer.class, "SELECT dsl.* FROM " + Data_store_layer.TableName + " dsl" +
                " JOIN " + Dtab_relation_store.TableName + " dtrs ON dtrs.dsl_id = dsl.dsl_id" +
                " JOIN " + Dm_datatable.TableName + " dd ON dd.datatable_id = dtrs.tab_id" +
                " WHERE dtrs.data_source in (?) GROUP BY dsl.dsl_id", StoreLayerDataSource.DM.getCode());
    }

    @Method(desc = "数据管控-源数据列表获取DML数据存储层下的表信息",
            logicStep = "数据管控-源数据列表获取数据存储层下的表信息")
    @Param(name = "data_store_layer", desc = "Data_store_layer实体对象", range = "Data_store_layer实体对象")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Map<String, Object>> getDMLStorageLayerTableInfos(Data_store_layer data_store_layer) {
        return Dbo.queryList("SELECT dsl.*,dd.* FROM " + Dm_datatable.TableName + " dd" +
                        " JOIN " + Dtab_relation_store.TableName + " dtrs ON dtrs.tab_id = dd.datatable_id" +
                        " JOIN " + Data_store_layer.TableName + " dsl ON dsl.dsl_id = dtrs.dsl_id" +
                        " WHERE dsl.dsl_id = ? and is_successful = ? and dtrs.data_source = ?",
                data_store_layer.getDsl_id(), JobExecuteState.WanCheng.getCode(), StoreLayerDataSource.DM.getCode());
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

    @Method(desc = "根据表id获取DML层数据表登记信息", logicStep = "根据表id获取DML层数据表登记信息")
    @Param(name = "file_id", desc = "表登记id", range = "String字符串,唯一")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static Dm_datatable getDMLDmDatatableInfo(String datatable_id) {
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        return Dbo.queryOneObject(Dm_datatable.class, "SELECT * from " + Dm_datatable.TableName + " WHERE datatable_id =?",
                dm_datatable.getDatatable_id()).orElseThrow(() -> (new BusinessException("获取DML数据登记信息的SQL失败!")));
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
    public static void deleteDMLDataStoreRegInfo(String datatable_id) {
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        int execute = Dbo.execute("DELETE FROM " + Dm_datatable.TableName + " WHERE datatable_id =?",
                dm_datatable.getDatatable_id());
        if (execute != 1) {
            throw new BusinessException("删除集市表登记信息失败! datatable_id=" + dm_datatable.getDatatable_id());
        }
    }
}
