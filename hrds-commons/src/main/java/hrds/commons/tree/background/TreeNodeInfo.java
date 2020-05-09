package hrds.commons.tree.background;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.codes.DataSourceType;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.background.utils.TreeNodeDataQuery;
import hrds.commons.utils.User;
import hrds.commons.tree.background.query.TreeDataQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DocClass(desc = "树节点信息查询", author = "BY-HLL", createdate = "2020/3/13 0013 上午 10:24")
public class TreeNodeInfo {

    @Method(desc = "根据页面来源获取树节点信息", logicStep = "根据页面来源获取树节点信息")
    @Param(name = "tree_source", desc = "树菜单来源", range = "String类型")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "treeConf", desc = "TreeConf树配置信息", range = "TreeConf树配置信息")
    @Return(desc = "树节点数据列表", range = "树节点数据列表")
    public static List<Map<String, Object>> getTreeNodeInfo(String tree_source, User user, TreeConf treeConf) {
        //根据树来源页面获取树菜单信息
        List<Map<String, Object>> sourceTreeInfos = TreeDataQuery.getSourceTreeInfos(tree_source);
        return getDataList(sourceTreeInfos, user, treeConf);
    }

    @Method(desc = "获取自定义的树节点信息", logicStep = "获取自定义的树节点信息")
    @Param(name = "dataLayers", desc = "树菜单来源", range = "String类型")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "treeConf", desc = "TreeConf树配置信息", range = "TreeConf树配置信息")
    @Return(desc = "树节点数据列表", range = "树节点数据列表")
    public static List<Map<String, Object>> getTreeNodeInfo(DataSourceType[] dataLayers, User user, TreeConf treeConf) {
        //根据树来源页面获取树菜单信息
        List<Map<String, Object>> sourceTreeInfos = TreeDataQuery.getSourceTreeInfos(dataLayers);
        return getDataList(sourceTreeInfos, user, treeConf);
    }

    @Method(desc = "获取自定义树节点信息", logicStep = "获取自定义树节点信息")
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
                TreeNodeDataQuery.getISLDataList(user, dataList, treeConf);
            } else if (dataSourceType == DataSourceType.DCL) {
                TreeNodeDataQuery.getDCLDataList(user, dataList, treeConf);
            } else if (dataSourceType == DataSourceType.DPL) {
                TreeNodeDataQuery.getDPLDataList(user, dataList, treeConf);
            } else if (dataSourceType == DataSourceType.DML) {
                TreeNodeDataQuery.getDMLDataList(user, dataList, treeConf);
            } else if (dataSourceType == DataSourceType.SFL) {
                TreeNodeDataQuery.getSFLDataList(user, dataList, treeConf);
            } else if (dataSourceType == DataSourceType.AML) {
                TreeNodeDataQuery.getAMLDataList(user, dataList, treeConf);
            } else if (dataSourceType == DataSourceType.DQC) {
                TreeNodeDataQuery.getDQCDataList(user, dataList, treeConf);
            } else if (dataSourceType == DataSourceType.UDL) {
                TreeNodeDataQuery.getUDLDataList(user, dataList, treeConf);
            } else {
                throw new BusinessException("未找到匹配的数据层!" + sourceTreeInfo.get("id").toString());
            }
        });
        return dataList;
    }
}
