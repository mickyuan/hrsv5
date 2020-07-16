package hrds.k.biz.dm.metadatamanage.commons;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.JobExecuteState;
import hrds.commons.codes.StoreLayerDataSource;
import hrds.commons.collection.RenameDataTable;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.k.biz.dm.metadatamanage.bean.ColumnInfoBean;
import hrds.k.biz.dm.metadatamanage.query.MDMDataQuery;
import hrds.k.biz.dm.metadatamanage.transctrl.IOnWayCtrl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@DocClass(desc = "数据管控-表元信息工具", author = "BY-HLL", createdate = "2020/4/2 0002 下午 04:16")
public class TableMetaInfoTool {

    private static final Logger logger = LogManager.getLogger();

    @Method(desc = "数据管控-删除 DCL 层批量数据下表元信息", logicStep = "数据管控-恢复 DCL 层批量数据下表元信息")
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Param(name = "dsl_id", desc = "所属存储层id", range = "long 类型")
    @Param(name = "file_id", desc = "表id", range = "表id")
    public static void setDCLTableInvalid(DatabaseWrapper db, long dsl_id, String file_id) {
        //获取 Data_store_reg
        Data_store_reg dsr = MDMDataQuery.getDCLDataStoreRegInfo(file_id);
        //放入回收站前先检查是否被其他表依赖
        IOnWayCtrl.checkExistsTask(dsr.getTable_name(), DataSourceType.DCL.getCode(), db);
        //根据存储层id,表id和 存储层关系-数据来源 获取数据表的存储关系信息
        Dtab_relation_store dtrs = MDMDataQuery.getDCLTableSpecifyStorageRelationship(dsl_id, dsr);
        //添加回收站表
        Dq_failure_table dft = new Dq_failure_table();
        dft.setFailure_table_id(PrimayKeyGener.getNextId());
        dft.setFile_id(String.valueOf(dsr.getTable_id()));
        dft.setTable_cn_name(dsr.getOriginal_name());
        dft.setTable_en_name(dsr.getHyren_name());
        dft.setTable_source(DataSourceType.DCL.getCode());
        dft.setTable_meta_info(JsonUtil.toJson(dsr));
        dft.setDsl_id(dtrs.getDsl_id());
        dft.setData_source(dtrs.getData_source());
        dft.add(db);
        //删除表该存储层的登记关系信息
        dtrs.delete(db);
        //根据表id获取存储层的登记关系信息列表
        List<Dtab_relation_store> dtrs_list = MDMDataQuery.getDCLTableStorageRelationships(dsr);
        //如果表的存储层登记关系信息为空,则删除源表的数据
        if (dtrs_list.isEmpty()) {
            MDMDataQuery.deleteDCLDataStoreRegInfo(file_id);
        }
        //重命名数据层表
        RenameDataTable.renameTableByDataLayer(db, dsl_id, Constant.DM_SET_INVALID_TABLE, "", dsr.getHyren_name());
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
        //根据存储层id,表id和 存储层关系-数据来源 获取数据表的存储关系信息
        Dtab_relation_store dtrs = MDMDataQuery.getDMLTableSpecifyStorageRelationship(dsl_id, dm_datatable);
        //添加到回收站表
        Dq_failure_table dft = new Dq_failure_table();
        dft.setFailure_table_id(PrimayKeyGener.getNextId());
        dft.setFile_id(String.valueOf(dm_datatable.getDatatable_id()));
        dft.setTable_cn_name(dm_datatable.getDatatable_cn_name());
        dft.setTable_en_name(dm_datatable.getDatatable_en_name());
        dft.setTable_source(DataSourceType.DML.getCode());
        dft.setTable_meta_info(JsonUtil.toJson(dm_datatable));
        dft.setDsl_id(dtrs.getDsl_id());
        dft.setData_source(dtrs.getData_source());
        dft.add(db);
        //删除表该存储层的登记关系信息
        dtrs.delete(db);
        //根据表id获取存储层的登记关系信息列表
        List<Dtab_relation_store> dtrs_list = MDMDataQuery.getDMLTableStorageRelationships(dm_datatable);
        //如果表的存储层登记关系信息为空,则删除源表的数据
        if (dtrs_list.isEmpty()) {
            MDMDataQuery.deleteDMLDmDataTable(datatable_id);
        }
        //重命名数据层表
        RenameDataTable.renameTableByDataLayer(db, dsl_id, Constant.DM_SET_INVALID_TABLE, "", dm_datatable.getDatatable_en_name());
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
        //添加到回收站表
        Dq_failure_table dft = new Dq_failure_table();
        dft.setFailure_table_id(PrimayKeyGener.getNextId());
        dft.setFile_id(String.valueOf(di3.getRecord_id()));
        dft.setTable_cn_name(di3.getTable_name());
        dft.setTable_en_name(di3.getTable_name());
        dft.setTable_source(DataSourceType.DQC.getCode());
        dft.setTable_meta_info(JsonUtil.toJson(di3));
        dft.setData_source(StoreLayerDataSource.DQ.getCode());
        dft.setDsl_id(dsl_id);
        dft.add(db);
        //删除源表的数据
        MDMDataQuery.deleteDQCDqIndex3record(String.valueOf(di3.getRecord_id()));
        //重命名数据层表
        RenameDataTable.renameTableByDataLayer(db, dsl_id, Constant.DM_SET_INVALID_TABLE, "", di3.getTable_name());
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
        //根据存储层id,表id和 存储层关系-数据来源 获取数据表的存储关系信息
        Dtab_relation_store dtrs = MDMDataQuery.getUDLTableSpecifyStorageRelationship(dsl_id, dti);
        //添加到回收站表
        Dq_failure_table dft = new Dq_failure_table();
        dft.setFailure_table_id(PrimayKeyGener.getNextId());
        dft.setFile_id(String.valueOf(dti.getTable_id()));
        dft.setTable_cn_name(dti.getCh_name());
        dft.setTable_en_name(dti.getTable_name());
        dft.setTable_source(DataSourceType.UDL.getCode());
        dft.setTable_meta_info(JsonUtil.toJson(dti));
        dft.setDsl_id(dtrs.getDsl_id());
        dft.setData_source(dtrs.getData_source());
        dft.add(db);
        //删除表该存储层的登记关系信息
        dtrs.delete(db);
        //根据表id获取存储层的登记关系信息列表
        List<Dtab_relation_store> dtrs_list = MDMDataQuery.getUDLTableStorageRelationships(dti);
        //如果表的存储层登记关系信息为空,则删除源表的数据
        if (dtrs_list.isEmpty()) {
            MDMDataQuery.deleteUDLDqTableInfo(dti.getTable_id());
        }
        //重命名数据层表
        RenameDataTable.renameTableByDataLayer(db, dsl_id, Constant.DM_SET_INVALID_TABLE, "", dti.getTable_name());
    }

    @Method(desc = "数据管控-恢复 DCL 层批量数据下表元信息", logicStep = "数据管控-恢复 DCL 层批量数据下表元信息")
    @Param(name = "dq_failure_table", desc = "Dq_failure_table的实体对象", range = "实体对象", isBean = true)
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void restoreDCLTableInfo(Dq_failure_table dft, DatabaseWrapper db) {
        //转换Mate信息为Data_store_reg实体对象
        Data_store_reg dsr = JsonUtil.toObjectSafety(dft.getTable_meta_info(),
                Data_store_reg.class).orElseThrow(() -> new BusinessException("类型转换错误,检查Meta的正确性!"));
        if (null != dsr) {
            //恢复表存储层登记关系
            Dtab_relation_store dtrs = new Dtab_relation_store();
            dtrs.setTab_id(dft.getFile_id());
            dtrs.setDsl_id(dft.getDsl_id());
            dtrs.setIs_successful(JobExecuteState.WanCheng.getCode());
            dtrs.setData_source(dft.getData_source());
            dtrs.add(db);
            //校验数据DCL层登记信息是否存在
            boolean boo = Dbo.queryNumber(db, "select count(*) from " + Data_store_reg.TableName + " where hyren_name=?",
                    dsr.getHyren_name()).orElseThrow(() -> new BusinessException("校验DCL表层登记信息的SQL错误")) == 1;
            //如果表登记信息不存在,则添加登记信息
            if (!boo) {
                dsr.add(db);
            }
            //根据数据层id恢复 DCL 数据层对应存储层下的数据表
            RenameDataTable.renameTableByDataLayer(db, dft.getDsl_id(), Constant.DM_RESTORE_TABLE, "", dsr.getHyren_name());
        }
    }

    @Method(desc = "数据管控-恢复 DML 层批量数据下表元信息", logicStep = "数据管控-恢复 DML 层批量数据下表元信息")
    @Param(name = "dq_failure_table", desc = "Dq_failure_table的实体对象", range = "实体对象", isBean = true)
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void restoreDMLTableInfo(Dq_failure_table dft, DatabaseWrapper db) {
        //转换Mate信息为 Dm_datatable 实体对象
        Dm_datatable dmd = JsonUtil.toObjectSafety(dft.getTable_meta_info(),
                Dm_datatable.class).orElseThrow(() -> new BusinessException("类型转换错误,检查Meta的正确性!"));
        if (null != dmd) {
            //恢复表存储层登记关系
            Dtab_relation_store dtrs = new Dtab_relation_store();
            dtrs.setTab_id(dft.getFile_id());
            dtrs.setDsl_id(dft.getDsl_id());
            dtrs.setIs_successful(JobExecuteState.WanCheng.getCode());
            dtrs.setData_source(dft.getData_source());
            dtrs.add(db);
            //校验数据DML层登记信息是否存在
            boolean boo = Dbo.queryNumber(db, "select count(*) from " + Dm_datatable.TableName + " where datatable_en_name=?",
                    dmd.getDatatable_en_name()).orElseThrow(() -> new BusinessException("校验DML表层登记信息的SQL错误")) == 1;
            //如果表登记信息不存在,则添加登记信息
            if (!boo) {
                dmd.add(db);
            }
            //根据数据层id恢复 DML 数据层对应存储层下的数据表
            RenameDataTable.renameTableByDataLayer(db, dft.getDsl_id(), Constant.DM_RESTORE_TABLE, "", dmd.getDatatable_en_name());
        }
    }

    @Method(desc = "数据管控-恢复 DQC 层批量数据下表元信息", logicStep = "数据管控-恢复 DQC 层批量数据下表元信息")
    @Param(name = "dq_failure_table", desc = "Dq_failure_table的实体对象", range = "实体对象", isBean = true)
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void restoreDQCTableInfo(Dq_failure_table dft, DatabaseWrapper db) {
        //转换Mate信息为 Data_store_reg 实体对象
        Dq_index3record di3 = JsonUtil.toObjectSafety(dft.getTable_meta_info(),
                Dq_index3record.class).orElseThrow(() -> new BusinessException("类型转换错误,检查Meta的正确性!"));
        if (null != di3) {
            //校验数据DQC层登记信息是否存在
            boolean boo = Dbo.queryNumber(db, "select count(*) from " + Dq_index3record.TableName + " where " +
                    "table_name=?", di3.getTable_name())
                    .orElseThrow(() -> new BusinessException("校验DQC表层登记信息的SQL错误")) == 1;
            //如果表登记信息不存在,则添加登记信息
            if (!boo) {
                di3.add(db);
            }
            //根据数据层id恢复 DQC 数据层对应存储层下的数据表
            RenameDataTable.renameTableByDataLayer(db, dft.getDsl_id(), Constant.DM_RESTORE_TABLE, "", di3.getTable_name());
        }
    }

    @Method(desc = "数据管控-恢复 UDL 层批量数据下表元信息", logicStep = "数据管控-恢复 UDL 层批量数据下表元信息")
    @Param(name = "dq_failure_table", desc = "Dq_failure_table的实体对象", range = "实体对象", isBean = true)
    @Param(name = "db", desc = "DatabaseWrapper对象", range = "DatabaseWrapper对象")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void restoreUDLTableInfo(Dq_failure_table dft, DatabaseWrapper db) {
        //转换Mate信息为 Dq_table_info 实体对象
        Dq_table_info dti = JsonUtil.toObjectSafety(dft.getTable_meta_info(),
                Dq_table_info.class).orElseThrow(() -> new BusinessException("类型转换错误,检查Meta的正确性!"));
        if (null != dti) {
            //恢复表存储层登记关系
            Dtab_relation_store dtrs = new Dtab_relation_store();
            dtrs.setTab_id(dft.getFile_id());
            dtrs.setDsl_id(dft.getDsl_id());
            dtrs.setIs_successful(JobExecuteState.WanCheng.getCode());
            dtrs.setData_source(dft.getData_source());
            dtrs.add(db);
            //校验数据UDL层登记信息是否存在
            boolean boo = Dbo.queryNumber(db, "select count(*) from " + Dq_table_info.TableName + " where " +
                    "table_name=?", dti.getTable_name())
                    .orElseThrow(() -> new BusinessException("校验UDL表层登记信息的SQL错误")) == 1;
            //如果表登记信息不存在,则添加登记信息
            if (!boo) {
                dti.add(db);
            }
            //根据数据层id恢复 DML 数据层对应存储层下的数据表
            RenameDataTable.renameTableByDataLayer(db, dft.getDsl_id(), Constant.DM_RESTORE_TABLE, "", dti.getTable_name());
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
    public static void updateUDLTableMetaInfo(String table_id, String table_ch_name, ColumnInfoBean[] columnInfoBeans, DatabaseWrapper db) {
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

    @Method(desc = "删除表注册记录数据", logicStep = "删除表注册记录数据")
    @Param(name = "dataSource", desc = "StoreLayerDataSource枚举类对象", range = "DBA")
    @Param(name = "table_id", desc = "表id", range = "long类型,表id")
    public static void deleteTableRegistrationRecordData(StoreLayerDataSource dataSource, long table_id) {
        //db采集
        if (dataSource == StoreLayerDataSource.DB) {
            //获取表信息
            Table_info table_info = new Table_info();
            table_info.setTable_id(table_id);
            Table_info ti = Dbo.queryOneObject(Table_info.class, "select * from " + Table_info.TableName + " where table_id=?",
                    table_info.getTable_id()).orElseThrow(() -> new BusinessException("获取表信息的SQL失败!"));
            ti.delete(Dbo.db());
            //删除表存储信息
            DboExecute.deletesOrThrow("删除表存储信息数据失败",
                    "delete from " + Table_storage_info.TableName + " where table_id=?", ti.getTable_id());
            //删除表清洗参数信息
            Dbo.execute("delete from " + Table_clean.TableName + " where table_id=?", ti.getTable_id());
            //删除表对应的字段表及相关表数据
            List<Table_column> tcs = Dbo.queryList(Table_column.class,
                    "select * from " + Table_column.TableName + " where table_id=?", ti.getTable_id());
            if (!tcs.isEmpty()) {
                tcs.forEach(tc -> {
                    //删除数据字段存储关系表
                    Dbo.execute("delete from " + Dcol_relation_store.TableName + " where col_id=?", tc.getColumn_id());
                    //删除列拆分信息表
                    Dbo.execute("delete from " + Column_split.TableName + " where column_id=?", tc.getColumn_id());
                    //删除列清洗参数信息
                    Dbo.execute("delete from " + Column_clean.TableName + " where column_id=?", tc.getColumn_id());
                    //删除表对应字段
                    tc.delete(Dbo.db());
                });
            }
            //删除表列合并信息
            Dbo.execute("delete from " + Column_merge.TableName + " where table_id=?", ti.getTable_id());
            //删除数据抽取定义及相关表数据
            List<Data_extraction_def> deds = Dbo.queryList(Data_extraction_def.class,
                    "select * from " + Data_extraction_def.TableName + " where table_id=?",
                    ti.getTable_id());
            if (!deds.isEmpty()) {
                deds.forEach(ded -> {
                    //删除抽数作业关系表
                    Dbo.execute("delete from " + Take_relation_etl.TableName + " where ded_id=?", ded.getDed_id());
                    //删除数据抽取定义
                    Dbo.execute("delete from " + Data_extraction_def.TableName + " where table_id=?", ti.getTable_id());
                });
            }
            logger.info("删除" + StoreLayerDataSource.DB.getValue() + "登记的相关的表数据完成!");
        }
        //对象采集
        else if (dataSource == StoreLayerDataSource.OBJ) {
            //获取对象采集对应信息
            Object_collect_task object_collect_task = new Object_collect_task();
            object_collect_task.setOcs_id(table_id);
            Object_collect_task oct = Dbo.queryOneObject(Object_collect_task.class, "select * from " +
                    Object_collect_task.TableName + " where ocs_id=?", object_collect_task.getOcs_id()).orElseThrow(()
                    -> new BusinessException("获取表信息的SQL失败!"));
            //删除对象采集对应信息
            oct.delete(Dbo.db());
            //删除对象采集数据处理类型对应表
            Dbo.execute("delete from " + Object_handle_type.TableName + " where ocs_id=?", oct.getOcs_id());
            //删除对象作业关系表
            Dbo.execute("delete from " + Obj_relation_etl.TableName + " where ocs_id=?", oct.getOcs_id());
            //删除对象采集结构信息及相关表数据
            List<Object_collect_struct> ocss = Dbo.queryList(Object_collect_struct.class,
                    "select * from " + Object_collect_struct.TableName + " where ocs_id=?", oct.getOcs_id());
            if (!ocss.isEmpty()) {
                ocss.forEach(ocs -> {
                    //删除数据字段存储关系表
                    Dbo.execute("delete from " + Dcol_relation_store.TableName + " where col_id=?", ocs.getStruct_id());
                    //删除对象采集结构信息
                    Dbo.execute("delete from " + Object_collect_struct.TableName + " where ocs_id=?", oct.getOcs_id());
                });
            }
            logger.info("删除" + StoreLayerDataSource.OBJ.getValue() + "登记的相关的表数据完成!");
        }
        //数据集市
        else if (dataSource == StoreLayerDataSource.DM) {
            //设置数据表信息
            Dm_datatable dm_datatable = new Dm_datatable();
            dm_datatable.setDatatable_id(table_id);
            //获取数据表已选数据源信息
            List<Dm_datatable_source> ddss = Dbo.queryList(Dm_datatable_source.class,
                    "select * from " + Dm_datatable_source.TableName + " where datatable_id=?", dm_datatable.getDatatable_id());
            //删除数据表已选数据源信息表数据及相关表数据
            if (!ddss.isEmpty()) {
                ddss.forEach(dds -> {
                    //删除数据源表字段数据
                    Dbo.execute("delete from " + Own_source_field.TableName + " where own_dource_table_id=?",
                            dds.getOwn_dource_table_id());
                    //删除结果映射信息表数据
                    Dbo.execute("delete from " + Dm_etlmap_info.TableName + " where own_dource_table_id=? and datatable_id=?",
                            dds.getOwn_dource_table_id(), dds.getDatatable_id());
                    //删除数据表已选数据源信息
                    dds.delete(Dbo.db());
                });
            }
            //删除集市表前置后置作业表数据
            Dbo.execute("delete from " + Dm_relevant_info.TableName + " where datatable_id=?", dm_datatable.getDatatable_id());
            //获取数据表字段信息
            List<Datatable_field_info> dtfis = Dbo.queryList(Datatable_field_info.class,
                    "select * from " + Datatable_field_info.TableName + " where datatable_id=?", dm_datatable.getDatatable_id());
            //删除数据表字段信息表数据及相关表数据
            if (!dtfis.isEmpty()) {
                dtfis.forEach(dtfi -> {
                    //删除数据字段存储关系表
                    Dbo.execute("delete from " + Dcol_relation_store.TableName + " where col_id=?", dtfi.getDatatable_field_id());
                    //删除数据表字段信息
                    dtfi.delete(Dbo.db());
                });
            }
            logger.info("删除" + StoreLayerDataSource.DM.getValue() + "登记的相关的表数据完成!");
        }
        //数据管控
        else if (dataSource == StoreLayerDataSource.DQ) {
            logger.info(StoreLayerDataSource.DQ.getValue() + "没有登记的相关表的数据,跳过!");
        }
        //自定义层
        else if (dataSource == StoreLayerDataSource.UD) {
            //设置自定义表信息
            Dq_table_info dq_table_info = new Dq_table_info();
            dq_table_info.setTable_id(table_id);
            //获取自定义表字段信息
            List<Dq_table_column> dtcs = Dbo.queryList(Dq_table_column.class,
                    "select * from " + Dq_table_column.TableName + " where table_id=?", dq_table_info.getTable_id());
            //删除自定义表字段信息表数据及相关表数据
            if (!dtcs.isEmpty()) {
                dtcs.forEach(dtc -> {
                    //删除数据字段存储关系表
                    Dbo.execute("delete from " + Dcol_relation_store.TableName + " where col_id=?", dtc.getField_id());
                    //删除自定义表字段信息
                    dtc.delete(Dbo.db());
                });
            }
        } else {
            throw new BusinessException("不支持的数据来源! dataSource=" + dataSource.getCode());
        }

    }
}
