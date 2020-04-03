package hrds.k.biz.dm.metadatamanage.drbtree;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.codes.DataSourceType;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.background.query.TreeDataQuery;
import hrds.commons.utils.User;
import hrds.k.biz.dm.metadatamanage.drbtree.utils.DRBTreeNodeDataQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据回收站树节点信息类", author = "BY-HLL", createdate = "2020/3/30 0030 上午 09:25")
public class DRBTreeNodeInfo {

    @Method(desc = "获取数据管控-数据回收站树节点信息", logicStep = "获取数据管控-数据回收站树节点信息")
    @Param(name = "tree_source", desc = "树菜单来源", range = "String类型")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "treeConf", desc = "TreeConf树配置信息", range = "TreeConf树配置信息")
    @Return(desc = "树节点数据列表", range = "树节点数据列表")
    public static List<Map<String, Object>> getTreeNodeInfo(String tree_source, User user, TreeConf treeConf) {
        //根据树来源页面获取树菜单信息
        List<Map<String, Object>> sourceTreeInfos = TreeDataQuery.getSourceTreeInfos(tree_source);
        return getDataList(sourceTreeInfos, user, treeConf);
    }

    @Method(desc = "数据回收站树节点信息", logicStep = "数据回收站树节点信息")
    @Param(name = "sourceTreeInfos", desc = "源树信息列表", range = "源树信息列表")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "treeConf", desc = "TreeConf树配置信息", range = "TreeConf树配置信息")
    @Return(desc = "树节点数据列表", range = "树节点数据列表")
    private static List<Map<String, Object>> getDataList(List<Map<String, Object>> sourceTreeInfos, User user,
                                                         TreeConf treeConf) {
        //初始化节点数据List并添加数据层节点数据
        List<Map<String, Object>> dataList = new ArrayList<>(sourceTreeInfos);
        //获取各数据层下的节点数据添加到结果List
        sourceTreeInfos.forEach(sourceTreeInfo -> {
            DataSourceType dataSourceType = DataSourceType.ofEnumByCode(sourceTreeInfo.get("id").toString());
            if (dataSourceType == DataSourceType.ISL) {
                DRBTreeNodeDataQuery.getISLDataList(user, dataList, treeConf);
            } else if (dataSourceType == DataSourceType.DCL) {
                DRBTreeNodeDataQuery.getDCLDataList(user, dataList, treeConf);
            } else if (dataSourceType == DataSourceType.DPL) {
                DRBTreeNodeDataQuery.getDPLDataList(user, dataList, treeConf);
            } else if (dataSourceType == DataSourceType.DML) {
                DRBTreeNodeDataQuery.getDMLDataList(user, dataList, treeConf);
            } else if (dataSourceType == DataSourceType.SFL) {
                DRBTreeNodeDataQuery.getSFLDataList(user, dataList, treeConf);
            } else if (dataSourceType == DataSourceType.AML) {
                DRBTreeNodeDataQuery.getAMLDataList(user, dataList, treeConf);
            } else if (dataSourceType == DataSourceType.DQC) {
                DRBTreeNodeDataQuery.getDQCDataList(user, dataList, treeConf);
            } else if (dataSourceType == DataSourceType.UDL) {
                DRBTreeNodeDataQuery.getUDLDataList(user, dataList, treeConf);
            } else {
                throw new BusinessException("未找到匹配的数据层!" + sourceTreeInfo.get("id").toString());
            }
        });
        return dataList;
    }
}
