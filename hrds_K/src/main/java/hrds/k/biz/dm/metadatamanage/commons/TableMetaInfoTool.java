package hrds.k.biz.dm.metadatamanage.commons;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.DataSourceType;
import hrds.commons.entity.Data_store_reg;
import hrds.commons.entity.Dq_failure_table;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.k.biz.dm.metadatamanage.bean.MetaDataInfo;
import hrds.k.biz.dm.metadatamanage.query.DRBDataQuery;
import hrds.k.biz.dm.metadatamanage.query.MDMDataQuery;

@DocClass(desc = "数据管控-表元信息工具", author = "BY-HLL", createdate = "2020/4/2 0002 下午 04:16")
public class TableMetaInfoTool {

    @Method(desc = "数据管控-删除 DCL 层批量数据下表元信息", logicStep = "数据管控-恢复 DCL 层批量数据下表元信息")
    @Param(name = "file_id", desc = "表id", range = "表id")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void setDCLTableInvalid(String file_id) {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //获取 Data_store_reg
            Data_store_reg dsr = MDMDataQuery.getDataStoreRegInfo(file_id);
            //放入回收站前先检查是否被其他表依赖
            checkDependencies(dsr.getTable_name(), DataSourceType.DCL.getCode());
            //添加到回收站表
            Dq_failure_table dq_failure_table = new Dq_failure_table();
            dq_failure_table.setFailure_table_id(PrimayKeyGener.getNextId());
            dq_failure_table.setTable_cn_name(dsr.getOriginal_name());
            dq_failure_table.setTable_en_name(dsr.getTable_name());
            dq_failure_table.setTable_source(DataSourceType.DCL.getCode());
            dq_failure_table.setTable_meta_info(JsonUtil.toJson(dsr.toString()));
            dq_failure_table.add(db);
            //删除源表的数据
            MDMDataQuery.deleteDataStoreRegInfo(file_id);
            //重命名数据层表
            DataLayerTableOperation.renameDCLDataLayerTable(dsr, Constant.DM_SET_INVALID_TABLE);
            //提交数据库操作
            SqlOperator.commitTransaction(db);
        }
    }

    @Method(desc = "数据管控-恢复 DCL 层批量数据下表元信息", logicStep = "数据管控-恢复 DCL 层批量数据下表元信息")
    @Param(name = "dq_failure_table", desc = "Dq_failure_table的实体对象", range = "实体对象", isBean = true)
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void restoreDCLTableInfo(Dq_failure_table dq_failure_table) {
        //转换Mate信息为Data_store_reg实体对象
        Data_store_reg dsr = JsonUtil.toObjectSafety(dq_failure_table.getTable_meta_info(),
                Data_store_reg.class).orElseThrow(() -> new BusinessException("类型转换错误,检查Meta的正确性!"));
        if (null != dsr) {
            //恢复表元信息
            DataLayerTableOperation.renameDCLDataLayerTable(dsr, Constant.DM_RESTORE_TABLE);
        }
    }

    @Method(desc = "修改DCL层下的表元信息",
            logicStep = "修改DCL层批量数据下的表元信息")
    @Param(name = "metaDataInfo", desc = "自定义实体MetaDataInfo的对象", range = "MetaDataInfo", isBean = true)
    public static void updateDCLTableMetaInfo(MetaDataInfo metaDataInfo) {
        //TODO 修改DCL层批量数据下的表元信息
        System.out.println(metaDataInfo);
    }

    @Method(desc = "完全删除DCL层下表信息", logicStep = "完全删除DCL层下表元信息")
    @Param(name = "file_id", desc = "回收站表id", range = "long类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void removeDCLTableInfo(long file_id) {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //获取 Dq_failure_table
            Dq_failure_table dft = DRBDataQuery.getDRBTableInfo(file_id);
            //数据校验
            if (StringUtil.isBlank(dft.getFailure_table_id().toString())) {
                throw new BusinessException("回收站的该表已经不存在!");
            }
            //放入回收站前先检查是否被其他表依赖
            checkDependencies(dft.getTable_en_name(), dft.getTable_source());
            //转换Mate信息为Data_store_reg实体对象
            Data_store_reg dsr = JsonUtil.toObjectSafety(dft.getTable_meta_info(),
                    Data_store_reg.class).orElseThrow(() -> new BusinessException("类型转换错误,检查Meta的正确性!"));
            if (null != dsr) {
                //彻底删除各存储层中表
                DataLayerTableOperation.removeDCLDataLayerTable(dsr);
            }
            //将失效登记表的数据删除
            DRBDataQuery.deleteDqFailureTableInfo(dft.getFailure_table_id());
            //提交数据库操作
            SqlOperator.commitTransaction(db);
        }
    }

    @Method(desc = "依赖表检查", logicStep = "依赖表检查")
    @Param(name = "tableName", desc = "表英文名", range = "String类型")
    @Param(name = "data_layer", desc = "所属数据层", range = "String 类型")
    private static void checkDependencies(String tableName, String data_layer) {
        System.out.println("表放入回收站时,依赖表检查暂未实现!");
    }
}
