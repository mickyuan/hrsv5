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
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Param(name = "dsl_id", desc = "所属存储层id", range = "long 类型")
    @Param(name = "file_id", desc = "表id", range = "表id")
    public static void setDCLTableInvalid(DatabaseWrapper db, long dsl_id, String file_id) {
        //获取 Data_store_reg
        Data_store_reg dsr = MDMDataQuery.getDCLDataStoreRegInfo(file_id);
        //放入回收站前先检查是否被其他表依赖
        IOnWayCtrl.checkExistsTask(dsr.getTable_name(), DataSourceType.DCL.getCode(), db);
        //重命名数据层表
        RenameDataTable.renameTableByDataLayer(db, dsl_id, Constant.DM_SET_INVALID_TABLE, "", dsr.getHyren_name());
        //根据存储层id,表id和 存储层关系-数据来源 获取数据表的存储关系信息
        Dtab_relation_store dtrs = MDMDataQuery.getDCLTableSpecifyStorageRelationship(dsl_id, dsr);
        //添加回收站表
        Dq_failure_table dq_failure_table = new Dq_failure_table();
        dq_failure_table.setFailure_table_id(PrimayKeyGener.getNextId());
        dq_failure_table.setFile_id(dsr.getTable_id().toString());
        dq_failure_table.setTable_cn_name(dsr.getOriginal_name());
        dq_failure_table.setTable_en_name(dsr.getHyren_name());
        dq_failure_table.setTable_source(DataSourceType.DCL.getCode());
        dq_failure_table.setTable_meta_info(JsonUtil.toJson(dsr));
        dq_failure_table.setDsl_id(dsl_id);
        dq_failure_table.add(db);
        //删除表该存储层的登记关系信息
        dtrs.delete(db);
        //根据表id获取存储层的登记关系信息列表
        List<Dtab_relation_store> dtrs_list = MDMDataQuery.getDCLTableStorageRelationships(dsr);
        //如果表的存储层登记关系信息为空,则删除源表的数据
        if (dtrs_list.isEmpty()) {
            MDMDataQuery.deleteDCLDataStoreRegInfo(file_id);
        }
    }

    @Method(desc = "数据管控-删除 DML 层批量数据下表元信息", logicStep = "数据管控-恢复 DML 层批量数据下表元信息")
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Param(name = "dsl_id", desc = "所属存储层id", range = "long 类型")
    @Param(name = "datatable_id", desc = "集市表id", range = "集市表id")
    public static void setDMLTableInvalid(DatabaseWrapper db, long dsl_id, String datatable_id) {
        //获取 Dm_datatable
        Dm_datatable dm_datatable = MDMDataQuery.getDMLDmDatatableInfo(datatable_id);
        //放入回收站前先检查是否被其他表依赖
        IOnWayCtrl.checkExistsTask(dm_datatable.getDatatable_en_name(), DataSourceType.DML.getCode(), db);
        //重命名数据层表
        RenameDataTable.renameTableByDataLayer(db, dsl_id, Constant.DM_SET_INVALID_TABLE, "", dm_datatable.getDatatable_en_name());
        //根据存储层id,表id和 存储层关系-数据来源 获取数据表的存储关系信息
        Dtab_relation_store dtrs = MDMDataQuery.getDMLTableSpecifyStorageRelationship(dsl_id, dm_datatable);
        //添加到回收站表
        Dq_failure_table dq_failure_table = new Dq_failure_table();
        dq_failure_table.setFailure_table_id(PrimayKeyGener.getNextId());
        dq_failure_table.setFile_id(datatable_id);
        dq_failure_table.setTable_cn_name(dm_datatable.getDatatable_cn_name());
        dq_failure_table.setTable_en_name(dm_datatable.getDatatable_en_name());
        dq_failure_table.setTable_source(DataSourceType.DML.getCode());
        dq_failure_table.setTable_meta_info(JsonUtil.toJson(dm_datatable));
        dq_failure_table.setDsl_id(dsl_id);
        dq_failure_table.add(db);
        //删除表该存储层的登记关系信息
        dtrs.delete(db);
        //根据表id获取存储层的登记关系信息列表
        List<Dtab_relation_store> dtrs_list = MDMDataQuery.getDMLTableStorageRelationships(dm_datatable);
        //如果表的存储层登记关系信息为空,则删除源表的数据
        if (dtrs_list.isEmpty()) {
            MDMDataQuery.deleteDMLDmDataTable(datatable_id);
        }
    }

    @Method(desc = "数据管控-删除 DQC 层批量数据下表元信息", logicStep = "数据管控-恢复 DQC 层批量数据下表元信息")
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Param(name = "dsl_id", desc = "所属存储层id", range = "long 类型")
    @Param(name = "file_id", desc = "集市表id", range = "集市表id")
    public static void setDQCTableInvalid(DatabaseWrapper db, long dsl_id, String file_id) {
        //获取 Data_store_reg
        Dq_index3record di3 = MDMDataQuery.getDQCDqIndex3record(file_id);
        //放入回收站前先检查是否被其他表依赖
        IOnWayCtrl.checkExistsTask(di3.getTable_name(), DataSourceType.DQC.getCode(), db);
        //重命名数据层表
        RenameDataTable.renameTableByDataLayer(db, dsl_id, Constant.DM_SET_INVALID_TABLE, "", di3.getTable_name());
        //添加到回收站表
        Dq_failure_table dq_failure_table = new Dq_failure_table();
        dq_failure_table.setFailure_table_id(PrimayKeyGener.getNextId());
        dq_failure_table.setFile_id(String.valueOf(di3.getRecord_id()));
        dq_failure_table.setTable_cn_name(di3.getTable_name());
        dq_failure_table.setTable_en_name(di3.getTable_name());
        dq_failure_table.setTable_source(DataSourceType.DQC.getCode());
        dq_failure_table.setTable_meta_info(JsonUtil.toJson(di3));
        dq_failure_table.setDsl_id(dsl_id);
        dq_failure_table.add(db);
        //删除源表的数据
        MDMDataQuery.deleteDQCDqIndex3record(String.valueOf(di3.getRecord_id()));
    }

    @Method(desc = "数据管控-删除 UDL 层批量数据下表元信息", logicStep = "数据管控-恢复 DCL 层批量数据下表元信息")
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Param(name = "dsl_id", desc = "所属存储层id", range = "long 类型")
    @Param(name = "table_id", desc = "UDL表id", range = "集市表id")
    public static void setUDLTableInvalid(DatabaseWrapper db, long dsl_id, String table_id) {
        //获取 Dq_table_info
        Dq_table_info dti = MDMDataQuery.getUDLTableInfo(table_id);
        //放入回收站前先检查是否被其他表依赖
        IOnWayCtrl.checkExistsTask(dti.getTable_name(), DataSourceType.UDL.getCode(), db);
        //重命名数据层表
        RenameDataTable.renameTableByDataLayer(db, dsl_id, Constant.DM_SET_INVALID_TABLE, "", dti.getTable_name());
        //添加到回收站表
        Dq_failure_table dq_failure_table = new Dq_failure_table();
        dq_failure_table.setFailure_table_id(PrimayKeyGener.getNextId());
        dq_failure_table.setFile_id(String.valueOf(dti.getTable_id()));
        dq_failure_table.setTable_cn_name(dti.getCh_name());
        dq_failure_table.setTable_en_name(dti.getTable_name());
        dq_failure_table.setTable_source(DataSourceType.UDL.getCode());
        dq_failure_table.setTable_meta_info(JsonUtil.toJson(dti));
        dq_failure_table.setDsl_id(dsl_id);
        dq_failure_table.add(db);
        //删除源表的数据
        MDMDataQuery.deleteUDLDqTableInfo(dti.getTable_id());
    }

    @Method(desc = "数据管控-恢复 DCL 层批量数据下表元信息", logicStep = "数据管控-恢复 DCL 层批量数据下表元信息")
    @Param(name = "dq_failure_table", desc = "Dq_failure_table的实体对象", range = "实体对象", isBean = true)
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void restoreDCLTableInfo(Dq_failure_table dqt, DatabaseWrapper db) {
        //转换Mate信息为Data_store_reg实体对象
        Data_store_reg dsr = JsonUtil.toObjectSafety(dqt.getTable_meta_info(),
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
            RenameDataTable.renameTableByDataLayer(db, dqt.getDsl_id(), Constant.DM_RESTORE_TABLE, "", dsr.getHyren_name());
            //恢复表元信息
            dsr.add(db);
        }
    }

    @Method(desc = "数据管控-恢复 DML 层批量数据下表元信息", logicStep = "数据管控-恢复 DML 层批量数据下表元信息")
    @Param(name = "dq_failure_table", desc = "Dq_failure_table的实体对象", range = "实体对象", isBean = true)
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void restoreDMLTableInfo(Dq_failure_table dqt, DatabaseWrapper db) {
        //转换Mate信息为 Dm_datatable 实体对象
        Dm_datatable dmd = JsonUtil.toObjectSafety(dqt.getTable_meta_info(),
                Dm_datatable.class).orElseThrow(() -> new BusinessException("类型转换错误,检查Meta的正确性!"));
        if (null != dmd) {
            //校验数据DCL层登记信息是否存在
            boolean boo = Dbo.queryNumber(db, "select count(*) from " + Dm_datatable.TableName + " where " +
                    "datatable_en_name=?", dmd.getDatatable_en_name())
                    .orElseThrow(() -> new BusinessException("校验DML表层登记信息的SQL错误")) == 1;
            //恢复存储层表信息
            if (boo) {
                throw new BusinessException("恢复的数据表已经存在!");
            }
            //根据数据层id恢复 DML 数据层对应存储层下的数据表
            RenameDataTable.renameTableByDataLayer(db, dqt.getDsl_id(), Constant.DM_RESTORE_TABLE, "", dmd.getDatatable_en_name());
            //恢复表元信息
            dmd.add(db);
        }
    }

    @Method(desc = "数据管控-恢复 DQC 层批量数据下表元信息", logicStep = "数据管控-恢复 DQC 层批量数据下表元信息")
    @Param(name = "dq_failure_table", desc = "Dq_failure_table的实体对象", range = "实体对象", isBean = true)
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void restoreDQCTableInfo(Dq_failure_table dqt, DatabaseWrapper db) {
        //转换Mate信息为 Data_store_reg 实体对象
        Dq_index3record di3 = JsonUtil.toObjectSafety(dqt.getTable_meta_info(),
                Dq_index3record.class).orElseThrow(() -> new BusinessException("类型转换错误,检查Meta的正确性!"));
        if (null != di3) {
            //校验数据DQC层登记信息是否存在
            boolean boo = Dbo.queryNumber(db, "select count(*) from " + Dq_index3record.TableName + " where " +
                    "table_name=?", di3.getTable_name())
                    .orElseThrow(() -> new BusinessException("校验DQC表层登记信息的SQL错误")) == 1;
            //恢复存储层表信息
            if (boo) {
                throw new BusinessException("恢复的数据表已经存在!");
            }
            //根据数据层id恢复 DQC 数据层对应存储层下的数据表
            RenameDataTable.renameTableByDataLayer(db, dqt.getDsl_id(), Constant.DM_RESTORE_TABLE, "", di3.getTable_name());
            //恢复表元信息
            di3.add(db);
        }
    }

    @Method(desc = "数据管控-恢复 UDL 层批量数据下表元信息", logicStep = "数据管控-恢复 UDL 层批量数据下表元信息")
    @Param(name = "dq_failure_table", desc = "Dq_failure_table的实体对象", range = "实体对象", isBean = true)
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void restoreUDLTableInfo(Dq_failure_table dqt, DatabaseWrapper db) {
        //转换Mate信息为 Dq_table_info 实体对象
        Dq_table_info dti = JsonUtil.toObjectSafety(dqt.getTable_meta_info(),
                Dq_table_info.class).orElseThrow(() -> new BusinessException("类型转换错误,检查Meta的正确性!"));
        if (null != dti) {
            //校验数据UDL层登记信息是否存在
            boolean boo = Dbo.queryNumber(db, "select count(*) from " + Dq_table_info.TableName + " where " +
                    "table_name=?", dti.getTable_name())
                    .orElseThrow(() -> new BusinessException("校验UDL表层登记信息的SQL错误")) == 1;
            //恢复存储层表信息
            if (boo) {
                throw new BusinessException("恢复的数据表已经存在!");
            }
            //根据数据层id恢复 DML 数据层对应存储层下的数据表
            RenameDataTable.renameTableByDataLayer(db, dqt.getDsl_id(), Constant.DM_RESTORE_TABLE, "", dti.getTable_name());
            //恢复表元信息
            dti.add(db);
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


    @Method(desc = "修改DML层下的表元信息",
            logicStep = "修改DML层批量数据下的表元信息")
    @Param(name = "table_id", desc = "数据表id", range = "String类型")
    @Param(name = "table_ch_name", desc = "表中文名", range = "String类型", nullable = true)
    @Param(name = "columnInfoBeans", desc = "自定义实体ColumnInfoBean[]", range = "ColumnInfoBean", isBean = true)
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    public static void updateUDLTableMetaInfo(String table_id, String table_ch_name,
                                              ColumnInfoBean[] columnInfoBeans, DatabaseWrapper db) {
        //设置 Dm_datatable 对象
        Dq_table_info dq_table_info = new Dq_table_info();
        dq_table_info.setTable_id(table_id);
        dq_table_info.setCh_name(table_ch_name);
        //修改集市数据表
        dq_table_info.update(db);
        //修改表字段信息
        for (ColumnInfoBean columnInfoBean : columnInfoBeans) {
            //设置 Table_column 对象
            Dq_table_column dq_table_column = new Dq_table_column();
            dq_table_column.setField_id(columnInfoBean.getColumn_id());
            dq_table_column.setField_ch_name(columnInfoBean.getColumn_ch_name());
            //修改表字段
            dq_table_column.update(db);
        }
    }
}
