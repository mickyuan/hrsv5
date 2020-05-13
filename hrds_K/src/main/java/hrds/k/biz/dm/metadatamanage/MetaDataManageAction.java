package hrds.k.biz.dm.metadatamanage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Data_store_layer;
import hrds.commons.entity.Data_store_reg;
import hrds.commons.entity.Dq_failure_table;
import hrds.commons.entity.Dq_index3record;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.background.query.DCLDataQuery;
import hrds.commons.tree.background.query.DQCDataQuery;
import hrds.commons.tree.background.query.TreeDataQuery;
import hrds.commons.tree.commons.TreePageSource;
import hrds.commons.utils.tree.Node;
import hrds.commons.utils.tree.NodeDataConvertedTreeList;
import hrds.k.biz.dm.metadatamanage.bean.ColumnInfoBean;
import hrds.k.biz.dm.metadatamanage.commons.TableMetaInfoTool;
import hrds.k.biz.dm.metadatamanage.drbtree.DRBTreeNodeInfo;
import hrds.k.biz.dm.metadatamanage.drbtree.MDMTreeNodeInfo;
import hrds.k.biz.dm.metadatamanage.query.DRBDataQuery;
import hrds.k.biz.dm.metadatamanage.query.MDMDataQuery;
import hrds.commons.utils.DataTableFieldUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据管控-元数据管理", author = "BY-HLL", createdate = "2020/3/27 0027 下午 04:39")
public class MetaDataManageAction extends BaseAction {

    @Method(desc = "数据管控-源数据列表树", logicStep = "获取数据管控-源数据列表树")
    @Return(desc = "源数据列表树", range = "源数据列表树")
    public Map<String, Object> getMDMTreeData() {
        //配置树不显示文件采集的数据
        TreeConf treeConf = new TreeConf();
        treeConf.setShowFileCollection(Boolean.FALSE);
        //根据源菜单信息获取节点数据列表 TreePageSource.DATA_MANAGEMENT 数据管控
        List<Map<String, Object>> dataList =
                MDMTreeNodeInfo.getTreeNodeInfo(TreePageSource.DATA_MANAGEMENT, getUser(), treeConf);
        //转换节点数据列表为分叉树列表
        List<Node> mdmTreeList = NodeDataConvertedTreeList.dataConversionTreeInfo(dataList);
        //定义返回的分叉树结果Map
        Map<String, Object> mdmTreeDataMap = new HashMap<>();
        mdmTreeDataMap.put("mdmTreeList", JsonUtil.toObjectSafety(mdmTreeList.toString(), List.class));
        return mdmTreeDataMap;
    }

    @Method(desc = "数据管控-数据回收站树", logicStep = "获取数据管控-源数据列表树 DataRecycleBin")
    @Return(desc = "源数据列表树", range = "源数据列表树")
    public Map<String, Object> getDRBTreeData() {
        //配置树不显示文件采集的数据
        TreeConf treeConf = new TreeConf();
        treeConf.setShowFileCollection(Boolean.FALSE);
        List<Map<String, Object>> dataList =
                DRBTreeNodeInfo.getTreeNodeInfo(TreePageSource.DATA_MANAGEMENT, getUser(), treeConf);
        //转换节点数据列表为分叉树列表
        List<Node> drbTreeList = NodeDataConvertedTreeList.dataConversionTreeInfo(dataList);
        //定义返回的分叉树结果Map
        Map<String, Object> drbTreeDataMap = new HashMap<>();
        drbTreeDataMap.put("drbTreeList", JsonUtil.toObjectSafety(drbTreeList.toString(), List.class));
        return drbTreeDataMap;
    }

    @Method(desc = "数据管控-获取源数据列表下表的字段信息",
            logicStep = "逻辑说明")
    @Param(name = "data_layer", desc = "数据层", range = "String类型,DCL,DML")
    @Param(name = "file_id", desc = "表源属性id", range = "String[]")
    @Return(desc = "表字段信息Map", range = "表字段信息Map")
    public Map<String, Object> getMDMTableColumnInfo(String data_layer, String file_id) {
        //初始化返回结果Map
        Map<String, Object> data_meta_info = new HashMap<>();
        //初始化表信息
        Map<String, Object> table_info_map;
        //初始化字段解析结果List
        List<Map<String, String>> column_info_list;
        String table_id, table_name, table_ch_name, create_date;
        //根据数据层获取不同层下的数据
        switch (data_layer) {
            case "DCL":
                //获取表信息
                table_info_map = DCLDataQuery.getDCLBatchTableInfo(file_id);
                //校验查询结果集
                if (table_info_map.isEmpty()) {
                    throw new BusinessException("表登记信息已经不存在!");
                }
                table_id = table_info_map.get("table_id").toString();
                table_name = table_info_map.get("table_name").toString();
                table_ch_name = table_info_map.get("table_ch_name").toString();
                create_date = table_info_map.get("original_update_date").toString();
                //获取并转换字段信息List
                column_info_list = DataTableFieldUtil.metaInfoToList(DCLDataQuery.getDCLBatchTableColumns(file_id));
                break;
            case "ISL":
            case "DPL":
            case "DML":
            case "SFL":
            case "AML":
            case "DQC":
                //获取表信息
                Dq_index3record dq_index3record = DQCDataQuery.getDQCTableInfo(file_id);
                table_id = dq_index3record.getRecord_id().toString();
                table_name = dq_index3record.getTable_name();
                table_ch_name = dq_index3record.getTable_name();
                create_date = dq_index3record.getRecord_date();
                List<Map<String, Object>> table_column_list = new ArrayList<>();
                String[] columns = dq_index3record.getTable_col().split(",");
                for (String column : columns) {
                    Map<String, Object> map = new HashMap<>();
                    String is_primary_key = IsFlag.Fou.getCode();
                    map.put("column_id", table_id);
                    map.put("column_name", column);
                    map.put("column_ch_name", column);
                    map.put("column_type", "varchar(--)");
                    map.put("is_primary_key", is_primary_key);
                    table_column_list.add(map);
                }
                column_info_list = DataTableFieldUtil.metaInfoToList(table_column_list);
                break;
            case "UDL":
                throw new BusinessException(data_layer + "层暂未实现!");
            default:
                throw new BusinessException("未找到匹配的存储层!");
        }
        //设置返回结果map
        data_meta_info.put("file_id", file_id);
        data_meta_info.put("table_id", table_id);
        data_meta_info.put("data_layer", data_layer);
        data_meta_info.put("table_name", table_name);
        data_meta_info.put("table_ch_name", table_ch_name);
        data_meta_info.put("create_date", create_date);
        data_meta_info.put("column_info_list", column_info_list);
        return data_meta_info;
    }

    @Method(desc = "数据管控-获取数据回收站的表结构信息",
            logicStep = "数据管控-获取数据回收站的表结构信息")
    @Param(name = "file_id", desc = "回收站的表id", range = "long类型,该值唯一")
    @Return(desc = "回收站的表结构信息", range = "回收站的表结构信息")
    public Map<String, Object> getDRBTableColumnInfo(long file_id) {
        //初始化返回结果Map
        Map<String, Object> data_meta_info = new HashMap<>();
        //id获取回收站表实体对象
        Dq_failure_table dq_failure_table = DRBDataQuery.getDRBTableInfo(file_id);
        //获取表的meta源信息
        String table_meta_info = dq_failure_table.getTable_meta_info();
        //初始化字段解析结果List
        List<Map<String, String>> column_info_list;
        //表id
        String table_id;
        //获取表的来源数据类型(DCL,DPL,实时数据暂不考虑!),并根据数据源类型处理不同数据源下的表信息
        String table_source = dq_failure_table.getTable_source();
        String create_date;
        switch (table_source) {
            //table_source存储表存在的数据层,如果是DCL层,则存储的是 dcl_batch 或 dcl_realtime
            case "DCL":
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
                break;
            case "ISL":
            case "DPL":
            case "DML":
            case "SFL":
            case "AML":
            case "DQC":
            case "UDL":
                throw new BusinessException(table_source + "层暂未实现!");
            default:
                throw new BusinessException("未找到匹配的存储层!");
        }
        if (column_info_list.isEmpty()) {
            throw new BusinessException("该表的meta信息已经不存在!" + dq_failure_table.getTable_cn_name());
        }
        //设置返回结果map
        data_meta_info.put("file_id", file_id);
        data_meta_info.put("table_id", table_id);
        data_meta_info.put("data_layer", table_source);
        //如果表类型为空,设置表类型为来源数据层,不为空代表表来源是DCL层下的 dcl_batch:批量数据或 dcl_realtime:实时数据
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
        //TODO 随后再做
        //数据校验
        if (StringUtil.isBlank(table_id)) {
            throw new BusinessException("编辑的元数据信息id为空!");
        }
        //根据数据层修改不同层下的数据
        switch (data_layer) {
            case "DCL":
                //修改DCL层批量数据下的表元信息
                TableMetaInfoTool.updateDCLTableMetaInfo(file_id, table_id, table_ch_name, columnInfoBeans);
                break;
            case "ISL":
            case "DPL":
            case "DML":
            case "SFL":
            case "AML":
            case "DQC":
            case "UDL":
                throw new BusinessException("修改" + data_layer + "层表元信息暂未实现!");
            default:
                throw new BusinessException("未找到匹配的存储层!");
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
        switch (data_layer) {
            case "DCL":
                TableMetaInfoTool.restoreDCLTableInfo(dq_failure_table);
                break;
            case "ISL":
            case "DPL":
            case "DML":
            case "SFL":
            case "AML":
            case "DQC":
            case "UDL":
                throw new BusinessException("恢复" + data_layer + "层表元信息暂未实现!");
            default:
                throw new BusinessException("未找到匹配的存储层!");
        }
        //删除失效登记表的数据
        int execute = Dbo.execute("DELETE FROM " + Dq_failure_table.TableName + " WHERE failure_table_id = ?",
                dq_failure_table.getFailure_table_id());
        if (execute != 1) {
            throw new BusinessException("删除回收站数据失败! failure_table_id=" + dq_failure_table.getFailure_table_id());
        }
    }

    @Method(desc = "恢复回收站数据层下所有表", logicStep = "恢复回收站所有表")
    public void restoreDRBAllTable() {
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
        switch (data_layer) {
            case "DCL":
                TableMetaInfoTool.setDCLTableInvalid(file_id);
                break;
            case "ISL":
            case "DPL":
            case "DML":
            case "SFL":
            case "AML":
            case "DQC":
            case "UDL":
                throw new BusinessException("将" + data_layer + "层表放入回收站暂未实现!");
            default:
                throw new BusinessException("未找到匹配的存储层!");
        }
    }

    @Method(desc = "表放入回收站(所有表设置为无效)", logicStep = "表放入回收站(所有表设置为无效)")
    public void allTableSetToInvalid() {
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
    @Param(name = "data_layer", desc = "所属数据层", range = "String 类型")
    @Param(name = "file_id", desc = "回收站表id", range = "long 类型")
    public void removeCompletelyTable(String data_layer, long file_id) {
        //根据数据层和表id删除表
        switch (data_layer) {
            case "DCL":
                TableMetaInfoTool.removeDCLTableInfo(file_id);
                break;
            case "ISL":
            case "DPL":
            case "DML":
            case "SFL":
            case "AML":
            case "DQC":
            case "UDL":
                throw new BusinessException("彻底删除" + data_layer + "层表信息暂未实现!");
            default:
                throw new BusinessException("未找到匹配的存储层!");
        }
    }

    @Method(desc = "数据管控-彻底删除所有表",
            logicStep = "数据管控-彻底删除所有表")
    public void removeCompletelyAllTable() {
        //获取回收站所有表信息
        List<Dq_failure_table> allTableInfos = DRBDataQuery.getAllTableInfos();
        //循环每一张表逐条删除
        allTableInfos.forEach(tableInfo ->
                removeCompletelyTable(tableInfo.getTable_source(), tableInfo.getFailure_table_id()));
    }


    @Method(desc = "数据管控-创建表", logicStep = "数据管控-创建表")
    @Param(name = "data_layer", desc = "所属数据层", range = "String 类型")
    @Param(name = "dsl_id", desc = "配置存储层id", range = "long 类型")
    public void createTable(String data_layer, long dsl_id) {
        //TODO 随后再做
        //根据所属数据层创建表
        switch (data_layer) {
            case "DCL":
                TableMetaInfoTool.createDCLTable(dsl_id);
                break;
            case "ISL":
            case "DPL":
            case "DML":
            case "SFL":
            case "AML":
            case "DQC":
            case "UDL":
                throw new BusinessException("彻底删除" + data_layer + "层表信息暂未实现!");
            default:
                throw new BusinessException("未找到匹配的存储层!");
        }

    }
}
