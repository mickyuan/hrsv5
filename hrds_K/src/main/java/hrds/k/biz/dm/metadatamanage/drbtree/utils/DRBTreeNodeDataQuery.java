package hrds.k.biz.dm.metadatamanage.drbtree.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import hrds.commons.entity.Data_store_layer;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.background.query.SFLDataQuery;
import hrds.commons.utils.User;
import hrds.k.biz.dm.metadatamanage.query.DRBDataQuery;

import java.util.List;
import java.util.Map;

@DocClass(desc = "数据管控-数据回收站树节点查询类", author = "BY-HLL", createdate = "2020/3/30 0030 上午 10:00")
public class DRBTreeNodeDataQuery {

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
        //设置DCL层下节点数据
        List<Data_store_layer> dataStorageLayers = DRBDataQuery.getDCLExistTableDataStorageLayers();
        dataList.addAll(DRBDataConvertedNodeData.conversionDCLDataStorageLayers(dataStorageLayers));
        //获取并设置回收站DCL层下表信息
        List<Map<String, Object>> tableInfos = DRBDataQuery.getDCLTableInfos();
        dataList.addAll(DRBDataConvertedNodeData.conversionDCLStorageLayerTableInfos(tableInfos));
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
        //TODO 暂未配置该存储层
    }

    @Method(desc = "获取SFL数据层的节点数据", logicStep = "获取SFL数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    @Param(name = "treeConf", desc = "TreeConf树配置信息", range = "TreeConf树配置信息")
    public static void getSFLDataList(User user, List<Map<String, Object>> dataList, TreeConf treeConf) {
        //TODO 暂未配置该存储层
        //添加SFL层下节点数据
        //dataList.addAll(SFLDataQuery.getSFLDataInfos());
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
        //TODO 暂未配置该存储层
    }

    @Method(desc = "获取UDL数据层的节点数据", logicStep = "获取UDL数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    @Param(name = "treeConf", desc = "TreeConf树配置信息", range = "TreeConf树配置信息")
    public static void getUDLDataList(User user, List<Map<String, Object>> dataList, TreeConf treeConf) {
        //TODO 暂未配置该存储层
    }
}
