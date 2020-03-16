package hrds.commons.tree.background;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.codes.DataSourceType;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.utils.TreeNodeDataQuery;
import hrds.commons.utils.User;
import hrds.commons.tree.background.query.TreeDataQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DocClass(desc = "树节点信息查询", author = "BY-HLL", createdate = "2020/3/13 0013 上午 10:24")
public class TreeNodeInfo {

    @Method(desc = "根据页面来源获取树节点信息", logicStep = "根据页面来源获取树节点信息")
    @Param(name = "tree_source", desc = "树菜单来源", range = "String类型", nullable = true)
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Return(desc = "树节点数据列表", range = "树节点数据列表")
    public static List<Map<String, Object>> getTreeNodeInfo(String tree_source, User user) {
        //根据树来源页面获取树菜单信息
        List<Map<String, Object>> sourceTreeInfos = TreeDataQuery.getSourceTreeInfos(tree_source);
        return getDataList(sourceTreeInfos, user);
    }

    @Method(desc = "获取自定义的树节点信息", logicStep = "获取自定义的树节点信息")
    @Param(name = "tree_source", desc = "树菜单来源", range = "String类型", nullable = true)
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Return(desc = "树节点数据列表", range = "树节点数据列表")
    public static List<Map<String, Object>> getTreeNodeInfo(DataSourceType[] dataLayers, User user) {
        //根据树来源页面获取树菜单信息
        List<Map<String, Object>> sourceTreeInfos = TreeDataQuery.getSourceTreeInfos(dataLayers);
        return getDataList(sourceTreeInfos, user);
    }

    @Method(desc = "获取自定义树节点信息", logicStep = "获取自定义树节点信息")
    @Param(name = "sourceTreeInfos", desc = "源树信息列表", range = "源树信息列表")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Return(desc = "树节点数据列表", range = "树节点数据列表")
    private static List<Map<String, Object>> getDataList(List<Map<String, Object>> sourceTreeInfos, User user) {
        //初始化节点数据List并添加数据层节点数据
        List<Map<String, Object>> dataList = new ArrayList<>(sourceTreeInfos);
        //获取各数据层下的节点数据添加到结果List
        sourceTreeInfos.forEach(sourceTreeInfo -> {
            Object id = sourceTreeInfo.get("id");
            if (DataSourceType.ISL.getCode().equals(id)) {
                TreeNodeDataQuery.getISLDataList(user, dataList);
            } else if (DataSourceType.DCL.getCode().equals(id)) {
                TreeNodeDataQuery.getDCLDataList(user, dataList);
            } else if (DataSourceType.DPL.getCode().equals(id)) {
                TreeNodeDataQuery.getDPLDataList(user, dataList);
            } else if (DataSourceType.DML.getCode().equals(id)) {
                TreeNodeDataQuery.getDMLDataList(user, dataList);
            } else if (DataSourceType.SFL.getCode().equals(id)) {
                TreeNodeDataQuery.getSFLDataList(user, dataList);
            } else if (DataSourceType.AML.getCode().equals(id)) {
                TreeNodeDataQuery.getAMLDataList(user, dataList);
            } else if (DataSourceType.DQC.getCode().equals(id)) {
                TreeNodeDataQuery.getDQCDataList(user, dataList);
            } else if (DataSourceType.UDL.getCode().equals(id)) {
                TreeNodeDataQuery.getUDLDataList(user, dataList);
            } else {
                throw new BusinessException("未找到匹配的数据层!" + id);
            }
        });
        return dataList;
    }
}
