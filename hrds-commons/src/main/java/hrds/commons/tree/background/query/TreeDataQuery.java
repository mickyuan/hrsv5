package hrds.commons.tree.background.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.codes.DataSourceType;
import hrds.commons.tree.commons.TreePageSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "树数据查询类", author = "BY-HLL", createdate = "2019/12/24 0024 上午 10:26")
public class TreeDataQuery {

    @Method(desc = "根据web类型获取源树菜单",
            logicStep = "根据web类型获取源树菜单")
    @Param(name = "tree_source", desc = "树来源", range = "树来源页面TreePageSource.TREE_SOURCE")
    @Return(desc = "源树菜单数据的List", range = "无限制")
    public static List<Map<String, Object>> getSourceTreeInfos(String tree_source) {
        List<Map<String, Object>> sourceTreeMenuInfos = new ArrayList<>();
        DataSourceType[] dataSourceTypes = TreePageSource.TREE_SOURCE.get(tree_source);
        for (DataSourceType dataSourceType : dataSourceTypes) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dataSourceType.getCode());
            map.put("label", dataSourceType.getValue());
            map.put("parent_id", "0");
            map.put("description", dataSourceType.getValue());
            map.put("data_layer", "0");
            sourceTreeMenuInfos.add(map);
        }
        return sourceTreeMenuInfos;
    }

    @Method(desc = "获取自定义源树菜单",
            logicStep = "获取自定义源树菜单")
    @Param(name = "dataLayers", desc = "源树菜单数组", range = "DataSourceType[]类型: [DCL,DPL]")
    @Return(desc = "源树菜单数据的List", range = "无限制")
    public static List<Map<String, Object>> getSourceTreeInfos(DataSourceType[] dataSourceTypes) {
        List<Map<String, Object>> sourceTreeMenuInfos = new ArrayList<>();
        for (DataSourceType dataSourceType : dataSourceTypes) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dataSourceType.getCode());
            map.put("label", dataSourceType.getValue());
            map.put("parent_id", "0");
            map.put("description", dataSourceType.getValue());
            sourceTreeMenuInfos.add(map);
        }
        return sourceTreeMenuInfos;
    }
}
