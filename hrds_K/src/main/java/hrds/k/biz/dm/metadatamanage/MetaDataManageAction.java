package hrds.k.biz.dm.metadatamanage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.DataSourceType;
import hrds.commons.collection.DeleteDataTable;
import hrds.commons.entity.Data_store_layer;
import hrds.commons.entity.Data_store_reg;
import hrds.commons.entity.Dm_datatable;
import hrds.commons.entity.Dq_failure_table;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.background.query.DCLDataQuery;
import hrds.commons.tree.background.query.DMLDataQuery;
import hrds.commons.tree.background.query.TreeDataQuery;
import hrds.commons.tree.commons.TreePageSource;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DataTableFieldUtil;
import hrds.commons.utils.DataTableUtil;
import hrds.commons.utils.tree.Node;
import hrds.commons.utils.tree.NodeDataConvertedTreeList;
import hrds.k.biz.dm.metadatamanage.bean.ColumnInfoBean;
import hrds.k.biz.dm.metadatamanage.commons.TableMetaInfoTool;
import hrds.k.biz.dm.metadatamanage.drbtree.DRBTreeNodeInfo;
import hrds.k.biz.dm.metadatamanage.drbtree.MDMTreeNodeInfo;
import hrds.k.biz.dm.metadatamanage.query.DRBDataQuery;
import hrds.k.biz.dm.metadatamanage.query.MDMDataQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据管控-元数据管理", author = "BY-HLL", createdate = "2020/3/27 0027 下午 04:39")
public class MetaDataManageAction extends BaseAction {

    @Method(desc = "数据管控-源数据列表树", logicStep = "获取数据管控-源数据列表树")
    @Return(desc = "源数据列表树", range = "源数据列表树")
    public List<Node> getMDMTreeData() {
        //配置树不显示文件采集的数据
        TreeConf treeConf = new TreeConf();
        treeConf.setShowFileCollection(Boolean.FALSE);
        //根据源菜单信息获取节点数据列表 TreePageSource.DATA_MANAGEMENT 数据管控
        List<Map<String, Object>> dataList = MDMTreeNodeInfo.getTreeNodeInfo(TreePageSource.DATA_MANAGEMENT,
                getUser(), treeConf);
        //转换节点数据列表为分叉树列表
        return NodeDataConvertedTreeList.dataConversionTreeInfo(dataList);
    }

    @Method(desc = "数据管控-数据回收站树", logicStep = "获取数据管控-源数据列表树 DataRecycleBin")
    @Return(desc = "源数据列表树", range = "源数据列表树")
    public List<Node> getDRBTreeData() {
        //配置树不显示文件采集的数据
        TreeConf treeConf = new TreeConf();
        treeConf.setShowFileCollection(Boolean.FALSE);
        List<Map<String, Object>> dataList = DRBTreeNodeInfo.getTreeNodeInfo(TreePageSource.DATA_MANAGEMENT,
                getUser(), treeConf);
        //转换节点数据列表为分叉树列表
        return NodeDataConvertedTreeList.dataConversionTreeInfo(dataList);
    }

    @Method(desc = "数据管控-获取源数据列表下表的字段信息",
            logicStep = "逻辑说明")
    @Param(name = "data_layer", desc = "数据层", range = "String类型,DCL,DML")
    @Param(name = "file_id", desc = "表源属性id", range = "String[]")
    @Return(desc = "表字段信息Map", range = "表字段信息Map")
    public Map<String, Object> getMDMTableColumnInfo(String data_layer, String file_id) {
        //数据校验
        Validator.notBlank(data_layer, "查询数据层为空!");
        Validator.notBlank(file_id, "查询数据表id为空!");
        return DataTableUtil.getTableInfoAndColumnInfo(data_layer, file_id);
    }

    @Method(desc = "数据管控-获取数据回收站的表结构信息",
            logicStep = "数据管控-获取数据回收站的表结构信息")
    @Param(name = "failure_table_id", desc = "回收站的表id", range = "long类型,该值唯一")
    @Return(desc = "回收站的表结构信息", range = "回收站的表结构信息")
    public Map<String, Object> getDRBTableColumnInfo(long failure_table_id) {
        //初始化返回结果Map
        Map<String, Object> data_meta_info = new HashMap<>();
        //id获取回收站表实体对象
        Dq_failure_table dq_failure_table = DRBDataQuery.getDRBTableInfo(failure_table_id);
        //获取表的meta源信息
        String table_meta_info = dq_failure_table.getTable_meta_info();
        //初始化字段解析结果List
        List<Map<String, String>> column_info_list;
        //表id
        String table_id, create_date;
        //获取表的来源数据类型(DCL,DPL,实时数据暂不考虑!),并根据数据源类型处理不同数据源下的表信息
        String table_source = dq_failure_table.getTable_source();
        DataSourceType dataSourceType = DataSourceType.ofEnumByCode(table_source);
        if (dataSourceType == DataSourceType.ISL) {
            throw new BusinessException(table_source + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DCL) {
            //转化成 Data_store_reg 对象
            Data_store_reg dsr = JsonUtil.toObjectSafety(table_meta_info, Data_store_reg.class)
                    .orElseThrow(() -> new BusinessException("类型转换错误,请检查meta格式的正确性!"));
            //设置表id
            table_id = dq_failure_table.getFailure_table_id().toString();
            //设置表创建日期
            create_date = dsr.getOriginal_update_date();
            //获取字段信息列表
            List<Map<String, Object>> dclBatchTableColumns = DCLDataQuery.getDCLBatchTableColumns(dsr.getTable_id());
            //转化字段信息列表为meta_list
            column_info_list = DataTableFieldUtil.metaInfoToList(dclBatchTableColumns);
        } else if (dataSourceType == DataSourceType.DPL) {
            throw new BusinessException(table_source + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DML) {
            //转 Dm_datatable 对象
            Dm_datatable dm_datatable = JsonUtil.toObjectSafety(table_meta_info, Dm_datatable.class)
                    .orElseThrow(() -> new BusinessException("类型转换错误,请检查meta格式的正确性!"));
            //设置表id
            table_id = dq_failure_table.getFailure_table_id().toString();
            //设置表创建日期
            create_date = dm_datatable.getDatatable_create_date();
            //获取字段信息列表
            List<Map<String, Object>> dmlTableColumns =
                    DMLDataQuery.getDMLTableColumns(dm_datatable.getDatatable_id().toString());
            //转化字段信息列表为meta_list
            column_info_list = DataTableFieldUtil.metaInfoToList(dmlTableColumns);
        } else if (dataSourceType == DataSourceType.SFL) {
            throw new BusinessException(table_source + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.AML) {
            throw new BusinessException(table_source + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DQC) {
            throw new BusinessException(table_source + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.UDL) {
            throw new BusinessException(table_source + "层暂未实现!");
        } else {
            throw new BusinessException("未找到匹配的数据层!" + table_source);
        }
        Validator.notEmpty(column_info_list, "该表的meta信息已经不存在!" + dq_failure_table.getTable_cn_name());
        //设置返回结果map
        data_meta_info.put("file_id", failure_table_id);
        data_meta_info.put("table_id", table_id);
        data_meta_info.put("data_layer", table_source);
        data_meta_info.put("table_name", dq_failure_table.getTable_en_name());
        data_meta_info.put("table_ch_name", dq_failure_table.getTable_cn_name());
        //如果表的创建日期为空,默认设置为99991231
        if (StringUtil.isNotBlank(create_date)) {
            data_meta_info.put("create_date", create_date);
        } else {
            data_meta_info.put("create_date", "99991231");
        }
        data_meta_info.put("column_info_list", column_info_list);
        return data_meta_info;
    }

    @Method(desc = "数据管控保存元数据",
            logicStep = "逻辑说明")
    @Param(name = "data_layer", desc = "数据层", range = "String类型,DCL")
    @Param(name = "file_id", desc = "数据表登记信息id", range = "String类型")
    @Param(name = "table_id", desc = "数据表id", range = "String类型")
    @Param(name = "table_ch_name", desc = "表中文名", range = "String类型", nullable = true)
    @Param(name = "columnInfoBeans", desc = "自定义实体ColumnInfoBean的对象", range = "ColumnInfoBean", isBean = true)
    public void saveMetaData(String data_layer, String file_id, String table_id, String table_ch_name,
                             ColumnInfoBean[] columnInfoBeans) {
        //数据校验
        if (StringUtil.isBlank(table_id)) {
            throw new BusinessException("编辑的元数据信息id为空!");
        }
        //根据数据层修改不同层下的数据
        DataSourceType dataSourceType = DataSourceType.ofEnumByCode(data_layer);
        if (dataSourceType == DataSourceType.ISL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DCL) {
            TableMetaInfoTool.updateDCLTableMetaInfo(file_id, table_id, table_ch_name, columnInfoBeans, Dbo.db());
        } else if (dataSourceType == DataSourceType.DPL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DML) {
            TableMetaInfoTool.updateDMLTableMetaInfo(table_id, table_ch_name, columnInfoBeans, Dbo.db());
        } else if (dataSourceType == DataSourceType.SFL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.AML) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DQC) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.UDL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else {
            throw new BusinessException("未找到匹配的数据层!" + data_layer);
        }
    }

    @Method(desc = "恢复回收站表", logicStep = "恢复回收站表")
    @Param(name = "data_layer", desc = "所属数据层", range = "String 类型")
    @Param(name = "file_id", desc = "回收站表id", range = "long 类型")
    public void restoreDRBTable(String data_layer, long file_id) {
        //获取回收站表信息
        Dq_failure_table dq_failure_table = DRBDataQuery.getDRBTableInfo(file_id);
        //校验待恢复表的元信息
        if (null == dq_failure_table) {
            throw new BusinessException("待恢复表的对象已经不存在!");
        }
        if (StringUtil.isBlank(dq_failure_table.getTable_meta_info())) {
            throw new BusinessException("待恢复表的元信息已经不存在! table_id=" + dq_failure_table.getFailure_table_id());
        }
        //根据数据层和表类型恢复数据
        DataSourceType dataSourceType = DataSourceType.ofEnumByCode(data_layer);
        if (dataSourceType == DataSourceType.ISL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DCL) {
            TableMetaInfoTool.restoreDCLTableInfo(dq_failure_table, Dbo.db());
        } else if (dataSourceType == DataSourceType.DPL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DML) {
            TableMetaInfoTool.restoreDMLTableInfo(dq_failure_table, Dbo.db());
        } else if (dataSourceType == DataSourceType.SFL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.AML) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DQC) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.UDL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else {
            throw new BusinessException("未找到匹配的数据层!" + data_layer);
        }
        //删除失效登记表的数据
        int execute = Dbo.execute("DELETE FROM " + Dq_failure_table.TableName + " WHERE failure_table_id = ?",
                dq_failure_table.getFailure_table_id());
        if (execute != 1) {
            throw new BusinessException("删除回收站数据失败! failure_table_id=" + dq_failure_table.getFailure_table_id());
        }
    }

    @Method(desc = "恢复回收站数据层下所有表", logicStep = "恢复回收站所有表")
    private void restoreDRBAllTable() {
        //获取回收站所有表信息
        List<Dq_failure_table> allTableInfos = DRBDataQuery.getAllTableInfos();
        //循环每一张表逐条恢复
        allTableInfos.forEach(
                tableInfo -> restoreDRBTable(tableInfo.getTable_source(), tableInfo.getFailure_table_id()));
    }

    @Method(desc = "表放入回收站(表设置为无效)", logicStep = "表放入回收站(表设置为无效)")
    @Param(name = "data_layer", desc = "所属数据层", range = "String 类型")
    @Param(name = "file_id", desc = "回收站表id", range = "long 类型")
    public void tableSetToInvalid(String data_layer, String file_id) {
        //根据数据层和表id放入回收站
        DataSourceType dataSourceType = DataSourceType.ofEnumByCode(data_layer);
        if (dataSourceType == DataSourceType.ISL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DCL) {
            TableMetaInfoTool.setDCLTableInvalid(file_id, Dbo.db());
        } else if (dataSourceType == DataSourceType.DPL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DML) {
            TableMetaInfoTool.setDMLTableInvalid(file_id, Dbo.db());
        } else if (dataSourceType == DataSourceType.SFL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.AML) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DQC) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.UDL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else {
            throw new BusinessException("未找到匹配的数据层!" + data_layer);
        }
    }

    @Method(desc = "表放入回收站(所有表设置为无效)", logicStep = "表放入回收站(所有表设置为无效)")
    private void allTableSetToInvalid() {
        //TODO hrsv5.1
        //获取回收站源树菜单列表
        List<Map<String, Object>> sourceTreeInfos = TreeDataQuery.getSourceTreeInfos(TreePageSource.DATA_MANAGEMENT);
        //根据源树菜单逐层处理
        sourceTreeInfos.forEach(sourceTreeInfo -> {
            //获取当前数据层信息
            DataSourceType dataSourceType = DataSourceType.ofEnumByCode(sourceTreeInfo.get("id").toString());
            //根据数据层处理对应层数据表
            if (dataSourceType == DataSourceType.ISL) {
                throw new BusinessException(dataSourceType.getCode() + "层表放入回收站未实现!");
            } else if (dataSourceType == DataSourceType.DCL) {
                //获取DCL下存在表的所有数据存储层
                List<Data_store_layer> dataStorageLayers = MDMDataQuery.getDCLExistTableDataStorageLayers();
                //获取所有表的信息
                dataStorageLayers.forEach(data_store_layer -> {
                    //获取存储层下表信息
                    MDMDataQuery.getDCLStorageLayerTableInfos(data_store_layer).forEach(table_info -> {
                        //根据查询到的表信息将表放入回收站
                        tableSetToInvalid(dataSourceType.getCode(), table_info.get("file_id").toString());
                    });
                });
            } else if (dataSourceType == DataSourceType.DPL) {
                throw new BusinessException(dataSourceType.getCode() + "层表放入回收站未实现!");
            } else if (dataSourceType == DataSourceType.DML) {
                throw new BusinessException(dataSourceType.getCode() + "层表放入回收站未实现!");
            } else if (dataSourceType == DataSourceType.SFL) {
                throw new BusinessException(dataSourceType.getCode() + "层表放入回收站未实现!");
            } else if (dataSourceType == DataSourceType.AML) {
                throw new BusinessException(dataSourceType.getCode() + "层表放入回收站未实现!");
            } else if (dataSourceType == DataSourceType.DQC) {
                throw new BusinessException(dataSourceType.getCode() + "层表放入回收站未实现!");
            } else if (dataSourceType == DataSourceType.UDL) {
                throw new BusinessException(dataSourceType.getCode() + "层表放入回收站未实现!");
            } else {
                throw new BusinessException("未找到匹配的存储层!");
            }
        });
    }

    @Method(desc = "数据管控-彻底删除表", logicStep = "数据管控-彻底删除表")
    @Param(name = "file_id", desc = "回收站表id", range = "long 类型")
    public void removeCompletelyTable(long file_id) {
        //获取 Dq_failure_table
        Dq_failure_table dft = DRBDataQuery.getDRBTableInfo(file_id);
        //数据校验
        if (StringUtil.isBlank(dft.getFailure_table_id().toString())) {
            throw new BusinessException("回收站的该表已经不存在!");
        }
        //彻底删除各存储层中表
        String invalid_table_name = Constant.DQC_INVALID_TABLE + dft.getTable_en_name();
        for (String dsl_id : dft.getRemark().split(",")) {
            DeleteDataTable.dropTableByDataLayer(invalid_table_name, Dbo.db(), dsl_id);
        }
        //将失效登记表的数据删除
        DRBDataQuery.deleteDqFailureTableInfo(dft.getFailure_table_id());
    }

    @Method(desc = "数据管控-彻底删除所有表",
            logicStep = "数据管控-彻底删除所有表")
    private void removeCompletelyAllTable() {
        //TODO hrsv5.1
        //获取回收站所有表信息
        List<Dq_failure_table> allTableInfos = DRBDataQuery.getAllTableInfos();
        //循环每一张表逐条删除
        allTableInfos.forEach(dft -> {
            //删除数据层下数据表
            DeleteDataTable.dropTableByDataLayer(dft.getTable_en_name(), Dbo.db(), dft.getRemark());
            //将失效登记表的数据删除
            DRBDataQuery.deleteDqFailureTableInfo(dft.getFailure_table_id());
        });
    }

    @Method(desc = "数据管控-创建表", logicStep = "数据管控-创建表")
    @Param(name = "data_layer", desc = "所属数据层", range = "String 类型")
    @Param(name = "dsl_id", desc = "配置存储层id", range = "long 类型")
    private void createTable(String data_layer, long dsl_id) {
        //TODO hrsv5.1
        //根据所属数据层创建表
        DataSourceType dataSourceType = DataSourceType.ofEnumByCode(data_layer);
        if (dataSourceType == DataSourceType.ISL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DCL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DPL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DML) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.SFL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.AML) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DQC) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.UDL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else {
            throw new BusinessException("未找到匹配的数据层!" + data_layer);
        }
    }
}
