package hrds.commons.tree.foreground.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.utils.User;
import hrds.commons.tree.foreground.bean.TreeDataInfo;
import hrds.commons.tree.foreground.query.DCLDataQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "树节点数据搜索类", author = "BY-HLL", createdate = "2019/12/31 0031 下午 02:17")
public class SearchNode {

    @Method(desc = "贴源层(DCL)数据层搜索",
            logicStep = "DCL数据层搜索")
    @Param(name = "treeDataInfo", desc = "TreeDataInfo", range = "TreeDataInfo的对象实例")
    @Param(name = "user", desc = "User实体对象", range = "User实体对象")
    @Return(desc = "DCL树节点数据检索结果map", range = "返回值取值范围")
    public static Map<String, Object> searchDCLNodeDataMap(User user, TreeDataInfo treeDataInfo) {
        Map<String, Object> searchDCLNodeDataMap = new HashMap<>();
        //1-1.获取DCL下数据信息
        List<Map<String, Object>> dclDataInfos = DCLDataQuery.getDCLDataInfos();
        searchDCLNodeDataMap.put("dclDataInfos", dclDataInfos);
        //1-2.获取DCL批量数据下的数据源
        List<Map<String, Object>> dclBatchDataInfos = DCLDataQuery.getDCLBatchDataInfos(user);
        searchDCLNodeDataMap.put("dclBatchDataInfos", dclBatchDataInfos);
        //1-2.获取DCL数据源下的分类信息
        if (!dclBatchDataInfos.isEmpty()) {
            boolean isFileCo = Boolean.parseBoolean(treeDataInfo.getIsFileCo());
            List<Map<String, Object>> dclBatchClassifyInfos = DCLDataQuery
                    .getDCLBatchClassifyInfos(null, user, treeDataInfo.getTableName());
            searchDCLNodeDataMap.put("dclBatchClassifyInfos", dclBatchClassifyInfos);
        }
        //1-3.获取DCL分类下数据表信息
        List<Map<String, Object>> dclBatchTableInfos = DCLDataQuery.getDCLBatchTableInfos(null,
                treeDataInfo.getTableName(), user);
        if (!dclBatchTableInfos.isEmpty()) {
            searchDCLNodeDataMap.put("dclBatchTableInfos", dclBatchTableInfos);
        }
        //2.DCL实时数据
        //TODO 未做
        return searchDCLNodeDataMap;
    }

    @Method(desc = "集市层(DML)数据层搜索",
            logicStep = "集市层(DML)数据层搜索")
    @Param(name = "treeDataInfo", desc = "TreeDataInfo", range = "TreeDataInfo的对象实例")
    @Return(desc = "DCL树节点数据检索结果map", range = "返回值取值范围")
    public static Map<String, Object> searchDMLNodeDataMap(TreeDataInfo treeDataInfo) {
        Map<String, Object> searchDMLNodeDataMap = new HashMap<>();
        //TODO 未做
        return searchDMLNodeDataMap;
    }

    @Method(desc = "加工层(DPL)数据层搜索",
            logicStep = "加工层(DPL)数据层搜索")
    @Param(name = "treeDataInfo", desc = "TreeDataInfo", range = "TreeDataInfo的对象实例")
    @Param(name = "user", desc = "User实体对象", range = "User实体对象")
    @Return(desc = "DCL树节点数据检索结果map", range = "返回值取值范围")
    public static Map<String, Object> searchDPLNodeDataMap(User user, TreeDataInfo treeDataInfo) {
        Map<String, Object> searchDPLNodeDataMap = new HashMap<>();
        //TODO 未做
        return searchDPLNodeDataMap;
    }

    @Method(desc = "管控层(DQC)数据层搜索",
            logicStep = "管控层(DQC)数据层搜索")
    @Param(name = "treeDataInfo", desc = "TreeDataInfo", range = "TreeDataInfo的对象实例")
    @Param(name = "user", desc = "User实体对象", range = "User实体对象")
    @Return(desc = "DCL树节点数据检索结果map", range = "返回值取值范围")
    public static Map<String, Object> searchDQCNodeDataMap(User user, TreeDataInfo treeDataInfo) {
        Map<String, Object> searchDQCNodeDataMap = new HashMap<>();
        //TODO 未做
        return searchDQCNodeDataMap;
    }
}
