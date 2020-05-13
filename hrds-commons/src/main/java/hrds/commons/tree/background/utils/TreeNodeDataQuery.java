package hrds.commons.tree.background.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.background.query.DMLDataQuery;
import hrds.commons.tree.background.query.DQCDataQuery;
import hrds.commons.utils.User;
import hrds.commons.tree.background.query.DCLDataQuery;
import hrds.commons.tree.background.query.SFLDataQuery;

import java.util.List;
import java.util.Map;

@DocClass(desc = "树节点数据查询", author = "BY-HLL", createdate = "2020/3/13 0013 上午 09:44")
public class TreeNodeDataQuery {

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
        //添加DCL层下节点数据
        dataList.addAll(DCLDataQuery.getDCLDataInfos(treeConf));
        //获取DCL层批量数据下的数据源列表
        List<Map<String, Object>> dclBatchDataInfos = DCLDataQuery.getDCLBatchDataInfos(user);
        //添加DCL层批量数据下数据源节点数据到DCL数据层的层次数据中
        dataList.addAll(DataConvertedNodeData.conversionDCLBatchDataInfos(dclBatchDataInfos));
        dclBatchDataInfos.forEach(dclBatchDataInfo -> {
            //获取数据源id
            String source_id = dclBatchDataInfo.get("source_id").toString();
            //获取批量数据数据源下分类信息
            List<Map<String, Object>> dclBatchClassifyInfos =
                    DCLDataQuery.getDCLBatchClassifyInfos(source_id, treeConf.getShowFileCollection(), user);
            if (!dclBatchClassifyInfos.isEmpty()) {
                dataList.addAll(DataConvertedNodeData.conversionDCLBatchClassifyInfos(dclBatchClassifyInfos));
            }
            //获取批量数据数据源的分类下数据表信息
            dclBatchClassifyInfos.forEach(dclBatchClassifyInfo -> {
                //获取分类id
                String classify_id = dclBatchClassifyInfo.get("classify_id").toString();
                //根据分类id获取分类下表信息
                List<Map<String, Object>> dclBatchTableInfos = DCLDataQuery.getDCLBatchTableInfos(classify_id, user);
                if (!dclBatchTableInfos.isEmpty()) {
                    dataList.addAll(DataConvertedNodeData.conversionDCLBatchTableInfos(dclBatchTableInfos));
                }
            });
        });
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
        //获取DML层下集市信息列表
        List<Map<String, Object>> dmlDataInfos = DMLDataQuery.getDMLDataInfos(user);
        //添加DML层下节点数据
        dataList.addAll(DataConvertedNodeData.conversionDMLDataInfos(dmlDataInfos));
        if (!dmlDataInfos.isEmpty()) {
            //获取集市下表信息
            dmlDataInfos.forEach(dmlDataInfo -> {
                String data_mart_id = dmlDataInfo.get("data_mart_id").toString();
                List<Map<String, Object>> dmlTableInfos = DMLDataQuery.getDMLTableInfos(data_mart_id, user);
                if (!dmlTableInfos.isEmpty()) {
                    dataList.addAll(DataConvertedNodeData.conversionDMLTableInfos(dmlTableInfos));
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
        //获取数据管控层下检测表列表
        List<Map<String, Object>> dqcDataInfos = DQCDataQuery.getDQCDataInfos();
        //转换并添加到层次数据中
        dataList.addAll(DataConvertedNodeData.conversionDQCTableInfos(dqcDataInfos));

    }

    @Method(desc = "获取UDL数据层的节点数据", logicStep = "获取UDL数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    @Param(name = "treeConf", desc = "TreeConf树配置信息", range = "TreeConf树配置信息")
    public static void getUDLDataList(User user, List<Map<String, Object>> dataList, TreeConf treeConf) {
        //TODO 暂未配置该存储层
    }
}
