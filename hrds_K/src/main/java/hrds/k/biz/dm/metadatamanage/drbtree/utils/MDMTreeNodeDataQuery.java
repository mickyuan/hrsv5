package hrds.k.biz.dm.metadatamanage.drbtree.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import hrds.commons.codes.DataSourceType;
import hrds.commons.entity.Data_store_layer;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.background.utils.TreeNodeDataQuery;
import hrds.commons.utils.User;
import hrds.k.biz.dm.metadatamanage.query.MDMDataQuery;

import java.util.List;
import java.util.Map;

@DocClass(desc = "数据管控-数据源列表树节点查询类", author = "BY-HLL", createdate = "2020/4/1 0001 下午 02:49")
public class MDMTreeNodeDataQuery {

    @Method(desc = "获取ISL数据层的节点数据", logicStep = "获取ISL数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    @Param(name = "treeConf", desc = "TreeConf树配置信息", range = "TreeConf树配置信息")
    public static void getISLDataList(User user, List<Map<String, Object>> dataList, TreeConf treeConf) {
        //TODO 暂未配置该存储层
    }

    @Method(desc = "获取DCL数据层的节点数据", logicStep = "获取DCL数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    @Param(name = "treeConf", desc = "TreeConf树配置信息", range = "TreeConf树配置信息")
    public static void getDCLDataList(User user, List<Map<String, Object>> dataList, TreeConf treeConf) {
        //设置DCL层下数据存储层信息
        List<Data_store_layer> dataStorageLayers = MDMDataQuery.getDCLExistTableDataStorageLayers();
        if (!dataStorageLayers.isEmpty()) {
            dataList.addAll(StorageLayerConvertedNodeData.conversionStorageLayers(dataStorageLayers, DataSourceType.DCL));
            //获取并设置各存储层下的表信息
            dataStorageLayers.forEach(data_store_layer -> {
                //获取存储层下表信息
                List<Map<String, Object>> dclStorageLayerTableInfos =
                        MDMDataQuery.getDCLStorageLayerTableInfos(data_store_layer);
                if (!dclStorageLayerTableInfos.isEmpty()) {
                    //设置表信息
                    dataList.addAll(MDMDataConvertedNodeData.conversionDCLStorageLayerTableInfos(dclStorageLayerTableInfos));
                }
            });
        }
    }

    @Method(desc = "获取DPL数据层的节点数据", logicStep = "获取DPL数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    @Param(name = "treeConf", desc = "TreeConf树配置信息", range = "TreeConf树配置信息")
    public static void getDPLDataList(User user, List<Map<String, Object>> dataList, TreeConf treeConf) {
        //TODO 暂未配置该存储层
    }

    @Method(desc = "获取DML数据层的节点数据", logicStep = "获取DML数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    @Param(name = "treeConf", desc = "TreeConf树配置信息", range = "TreeConf树配置信息")
    public static void getDMLDataList(User user, List<Map<String, Object>> dataList, TreeConf treeConf) {
        //设置DML层下数据存储层信息
        List<Data_store_layer> dataStorageLayers = MDMDataQuery.getDMLExistTableDataStorageLayers();
        if (!dataStorageLayers.isEmpty()) {
            dataList.addAll(StorageLayerConvertedNodeData.conversionStorageLayers(dataStorageLayers, DataSourceType.DML));
            dataStorageLayers.forEach(data_store_layer -> {
                List<Map<String, Object>> dmlStorageLayerTableInfos =
                        MDMDataQuery.getDMLStorageLayerTableInfos(data_store_layer);
                if (!dmlStorageLayerTableInfos.isEmpty()) {
                    //设置表信息
                    dataList.addAll(MDMDataConvertedNodeData.conversionDMLStorageLayerTableInfos(dmlStorageLayerTableInfos));
                }
            });
        }
    }

    @Method(desc = "获取SFL数据层的节点数据", logicStep = "获取SFL数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    @Param(name = "treeConf", desc = "TreeConf树配置信息", range = "TreeConf树配置信息")
    public static void getSFLDataList(User user, List<Map<String, Object>> dataList, TreeConf treeConf) {
        //TODO 暂未配置该存储层
        //添加SFL层下节点数据
//        dataList.addAll(SFLDataQuery.getSFLDataInfos());
    }

    @Method(desc = "获取AML数据层的节点数据", logicStep = "获取AML数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    @Param(name = "treeConf", desc = "TreeConf树配置信息", range = "TreeConf树配置信息")
    public static void getAMLDataList(User user, List<Map<String, Object>> dataList, TreeConf treeConf) {
        //TODO 暂未配置该存储层
    }

    @Method(desc = "获取DQC数据层的节点数据", logicStep = "获取DQC数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    @Param(name = "treeConf", desc = "TreeConf树配置信息", range = "TreeConf树配置信息")
    public static void getDQCDataList(User user, List<Map<String, Object>> dataList, TreeConf treeConf) {
        TreeNodeDataQuery.getDQCDataList(user, dataList, treeConf);
    }

    @Method(desc = "获取UDL数据层的节点数据", logicStep = "获取UDL数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    @Param(name = "treeConf", desc = "TreeConf树配置信息", range = "TreeConf树配置信息")
    public static void getUDLDataList(User user, List<Map<String, Object>> dataList, TreeConf treeConf) {
        //TODO 暂未配置该存储层
    }
}
