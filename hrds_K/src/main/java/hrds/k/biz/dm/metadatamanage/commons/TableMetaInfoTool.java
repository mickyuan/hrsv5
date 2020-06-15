package hrds.k.biz.dm.metadatamanage.commons;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.DataSourceType;
import hrds.commons.collection.RenameDataTable;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.k.biz.dm.metadatamanage.bean.ColumnInfoBean;
import hrds.k.biz.dm.metadatamanage.query.MDMDataQuery;
import hrds.k.biz.dm.metadatamanage.transctrl.IOnWayCtrl;

import java.util.List;

@DocClass(desc = "数据管控-表元信息工具", author = "BY-HLL", createdate = "2020/4/2 0002 下午 04:16")
public class TableMetaInfoTool {

    @Method(desc = "数据管控-删除 DCL 层批量数据下表元信息", logicStep = "数据管控-恢复 DCL 层批量数据下表元信息")
    @Param(name = "file_id", desc = "表id", range = "表id")
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    public static void setDCLTableInvalid(String file_id, DatabaseWrapper db) {
        //获取 Data_store_reg
        Data_store_reg dsr = MDMDataQuery.getDCLDataStoreRegInfo(file_id);
        //放入回收站前先检查是否被其他表依赖
        IOnWayCtrl.checkExistsTask(dsr.getTable_name(), DataSourceType.DCL.getCode(), db);
        //重命名数据层表
        List<String> dsl_id_s = RenameDataTable.renameTableByDataLayer("", dsr.getHyren_name(),
                Constant.DM_SET_INVALID_TABLE, db);
        //添加到回收站表
        Dq_failure_table dq_failure_table = new Dq_failure_table();
        dq_failure_table.setFailure_table_id(PrimayKeyGener.getNextId());
        dq_failure_table.setFile_id(dsr.getTable_id().toString());
        dq_failure_table.setTable_cn_name(dsr.getOriginal_name());
        dq_failure_table.setTable_en_name(dsr.getHyren_name());
        dq_failure_table.setTable_source(DataSourceType.DCL.getCode());
        dq_failure_table.setTable_meta_info(JsonUtil.toJson(dsr));
        //remark字段存储的是该表对应存储层id列表
        dq_failure_table.setRemark(String.join(",", dsl_id_s));
        dq_failure_table.add(db);
        //删除源表的数据
        MDMDataQuery.deleteDCLDataStoreRegInfo(file_id);
    }


    @Method(desc = "数据管控-删除 DCL 层批量数据下表元信息", logicStep = "数据管控-恢复 DCL 层批量数据下表元信息")
    @Param(name = "datatable_id", desc = "集市表id", range = "集市表id")
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    public static void setDMLTableInvalid(String datatable_id, DatabaseWrapper db) {
        //获取 Data_store_reg
        Dm_datatable dm_datatable = MDMDataQuery.getDMLDmDatatableInfo(datatable_id);
        //放入回收站前先检查是否被其他表依赖
        IOnWayCtrl.checkExistsTask(dm_datatable.getDatatable_en_name(), DataSourceType.DML.getCode(), db);
        //重命名数据层表
        List<String> dsl_id_s = RenameDataTable.renameTableByDataLayer("", dm_datatable.getDatatable_en_name(),
                Constant.DM_SET_INVALID_TABLE, db);
        //添加到回收站表
        Dq_failure_table dq_failure_table = new Dq_failure_table();
        dq_failure_table.setFailure_table_id(PrimayKeyGener.getNextId());
        dq_failure_table.setFile_id(datatable_id);
        dq_failure_table.setTable_cn_name(dm_datatable.getDatatable_cn_name());
        dq_failure_table.setTable_en_name(dm_datatable.getDatatable_en_name());
        dq_failure_table.setTable_source(DataSourceType.DML.getCode());
        dq_failure_table.setTable_meta_info(JsonUtil.toJson(dm_datatable));
        //remark字段存储的是该表对应存储层id列表
        dq_failure_table.setRemark(String.join(",", dsl_id_s));
        dq_failure_table.add(db);
        //删除源表的数据
        MDMDataQuery.deleteDMLDataStoreRegInfo(datatable_id);
    }

    @Method(desc = "数据管控-恢复 DCL 层批量数据下表元信息", logicStep = "数据管控-恢复 DCL 层批量数据下表元信息")
    @Param(name = "dq_failure_table", desc = "Dq_failure_table的实体对象", range = "实体对象", isBean = true)
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void restoreDCLTableInfo(Dq_failure_table dq_failure_table, DatabaseWrapper db) {
        //转换Mate信息为Data_store_reg实体对象
        Data_store_reg dsr = JsonUtil.toObjectSafety(dq_failure_table.getTable_meta_info(),
                Data_store_reg.class).orElseThrow(() -> new BusinessException("类型转换错误,检查Meta的正确性!"));
        if (null != dsr) {
            //校验数据DCL层登记信息是否存在
            boolean boo = Dbo.queryNumber(db, "select count(*) from " + Data_store_reg.TableName + " where hyren_name=?",
                    dsr.getHyren_name()).orElseThrow(() -> new BusinessException("校验DCL表层登记信息的SQL错误")) == 1;
            //恢复存储层表信息
            if (boo) {
                throw new BusinessException("恢复的数据表已经存在!");
            }
            //根据数据层id恢复 DCL 数据层对应存储层下的数据表
            restoreTableInfo(dsr.getHyren_name(), dq_failure_table.getRemark().split(","), db);
            //恢复表元信息
            dsr.add(db);
        }
    }

    @Method(desc = "数据管控-恢复 DCL 层批量数据下表元信息", logicStep = "数据管控-恢复 DCL 层批量数据下表元信息")
    @Param(name = "dq_failure_table", desc = "Dq_failure_table的实体对象", range = "实体对象", isBean = true)
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void restoreDMLTableInfo(Dq_failure_table dq_failure_table, DatabaseWrapper db) {
        //转换Mate信息为Data_store_reg实体对象
        Dm_datatable dm_datatable = JsonUtil.toObjectSafety(dq_failure_table.getTable_meta_info(),
                Dm_datatable.class).orElseThrow(() -> new BusinessException("类型转换错误,检查Meta的正确性!"));
        if (null != dm_datatable) {
            //校验数据DCL层登记信息是否存在
            boolean boo = Dbo.queryNumber(db, "select count(*) from " + Dm_datatable.TableName + " where " +
                    "datatable_en_name=?", dm_datatable.getDatatable_en_name())
                    .orElseThrow(() -> new BusinessException("校验DML表层登记信息的SQL错误")) == 1;
            //恢复存储层表信息
            if (boo) {
                throw new BusinessException("恢复的数据表已经存在!");
            }
            //根据数据层id恢复 DML 数据层对应存储层下的数据表
            restoreTableInfo(dm_datatable.getDatatable_en_name(), dq_failure_table.getRemark().split(","), db);
            //恢复表元信息
            dm_datatable.add(db);
        }
    }

    @Method(desc = "修改DCL层下的表元信息",
            logicStep = "修改DCL层批量数据下的表元信息")
    @Param(name = "file_id", desc = "数据表登记信息id", range = "String类型")
    @Param(name = "table_id", desc = "数据表id", range = "String类型")
    @Param(name = "table_ch_name", desc = "表中文名", range = "String类型", nullable = true)
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Param(name = "columnInfoBeans", desc = "自定义实体ColumnInfoBean[]", range = "ColumnInfoBean", isBean = true)
    public static void updateDCLTableMetaInfo(String file_id, String table_id, String table_ch_name,
                                              ColumnInfoBean[] columnInfoBeans, DatabaseWrapper db) {
        //设置 Data_store_reg 对象
        Data_store_reg dsr = new Data_store_reg();
        dsr.setFile_id(file_id);
        dsr.setTable_id(table_id);
        dsr.setOriginal_name(table_ch_name);
        //修改登记表
        dsr.update(db);
        //设置 Table_info 对象
        Table_info table_info = new Table_info();
        table_info.setTable_id(table_id);
        table_info.setTable_ch_name(table_ch_name);
        //修改表
        table_info.update(db);
        //修改表字段信息
        for (ColumnInfoBean columnInfoBean : columnInfoBeans) {
            //设置 Table_column 对象
            Table_column table_column = new Table_column();
            table_column.setColumn_id(columnInfoBean.getColumn_id());
            table_column.setColumn_ch_name(columnInfoBean.getColumn_ch_name());
            //修改表字段
            table_column.update(db);
        }
    }

    @Method(desc = "修改DML层下的表元信息",
            logicStep = "修改DML层批量数据下的表元信息")
    @Param(name = "table_id", desc = "数据表id", range = "String类型")
    @Param(name = "table_ch_name", desc = "表中文名", range = "String类型", nullable = true)
    @Param(name = "columnInfoBeans", desc = "自定义实体ColumnInfoBean[]", range = "ColumnInfoBean", isBean = true)
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    public static void updateDMLTableMetaInfo(String table_id, String table_ch_name,
                                              ColumnInfoBean[] columnInfoBeans, DatabaseWrapper db) {
        //设置 Dm_datatable 对象
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(table_id);
        dm_datatable.setDatatable_cn_name(table_ch_name);
        //修改集市数据表
        dm_datatable.update(db);
        //修改表字段信息
        for (ColumnInfoBean columnInfoBean : columnInfoBeans) {
            //设置 Table_column 对象
            Datatable_field_info dfi = new Datatable_field_info();
            dfi.setDatatable_field_id(columnInfoBean.getColumn_id());
            dfi.setField_cn_name(columnInfoBean.getColumn_ch_name());
            //修改表字段
            dfi.update(db);
        }
    }

    @Method(desc = "根据数据层id列表和表名恢复数据表信息", logicStep = "根据数据层id和表名恢复数据表信息")
    @Param(name = "tableName", desc = "需要重命名的表名", range = "String类型")
    @Param(name = "dsl_id_s", desc = "数据层id列表", range = "String[]类型")
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    private static void restoreTableInfo(String tableName, String[] dsl_id_s, DatabaseWrapper db) {
        for (String dsl_id : dsl_id_s) {
            Data_store_layer dsl = new Data_store_layer();
            dsl.setDsl_id(dsl_id);
            LayerBean intoLayerBean = Dbo.queryOneObject(db, LayerBean.class, "select * from " +
                    Data_store_layer.TableName + " where dsl_id = ?", dsl.getDsl_id())
                    .orElseThrow(() -> new BusinessException("恢复表时,获取存储层信息的SQL失败!"));
            RenameDataTable.renameTableByDataLayer("", tableName, Constant.DM_RESTORE_TABLE, db, intoLayerBean);
        }
    }
}
