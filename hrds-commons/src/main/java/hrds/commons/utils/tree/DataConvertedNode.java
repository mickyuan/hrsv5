package hrds.commons.utils.tree;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;

import java.util.*;

@DocClass(desc = "数据对标元管理-数据转树节点类", author = "BY-HLL", createdate = "2020/2/20 0020 下午 09:49")
public class DataConvertedNode {

    @Method(desc = "树数据转分叉树菜单数据", logicStep = "标准分类树数据转分叉树菜单数据")
    @Param(name = "dataList", desc = "数据列表", range = "dataList")
    @Return(desc = "转化成分叉树的树菜单数据List列表", range = "转化成分叉树的树菜单数据List列表")
    public static List<Node> dataConversionTreeInfo(List<Map<String, Object>> dataList) {
        // 节点列表（散列表，用于临时存储节点对象）
        Map<String, Node> nodeMap = new HashMap<>();
        // 根据结果集构造节点列表（存入散列表）
        dataList.forEach(dataRecord -> {
            Node node = new Node();
            node.id = (String) dataRecord.get("id");
            node.label = (String) dataRecord.get("label");
            node.parent_id = (String) dataRecord.get("parent_id");
            nodeMap.put(node.id, node);
        });
        // 构造无序的多叉树
        List<Node> treeNodeDataList = new ArrayList<>();
        for (Map.Entry<String, Node> nodeEntry : nodeMap.entrySet()) {
            Node treeNodeData;
            Node node = nodeEntry.getValue();
            if (node.parent_id == null || "0" .equals(node.parent_id)) {
                treeNodeData = node;
                treeNodeDataList.add(treeNodeData);
            } else {
                nodeMap.get(node.parent_id).addChild(node);
            }
        }
        // 对多叉树进行横向排序
        for (Node node : treeNodeDataList) {
            node.sortChildren();
        }
        return treeNodeDataList;
    }

    @Method(desc = "树数据转分叉树菜单数据", logicStep = "标准分类树数据转分叉树菜单数据")
    @Param(name = "dataList", desc = "数据列表", range = "dataList")
    @Return(desc = "转化成分叉树的树菜单数据List", range = "转化成分叉树的树菜单数据List")
    @Deprecated
    public static List<Map<String, Object>> dataConversionTreeInfoList(
            List<Map<String, Object>> dataList) {
        Map<String, List<Map<String, Object>>> childrenMap = new LinkedHashMap<>();
        // 根据结果集构造节点列表（存入散列表）
        dataList.forEach(dataRecord -> {
            String parent_id = ((String) dataRecord.get("parent_id"));
            if (!childrenMap.containsKey(parent_id)) {
                List<Map<String, Object>> item = new ArrayList<>();
                item.add(dataRecord);
                childrenMap.put(parent_id, item);
            } else {
                childrenMap.get(parent_id).add(dataRecord);
            }
        });
        // 构造多叉树
        List<Map<String, Object>> treeList = new ArrayList<>();
        childrenMap.forEach((key, item) -> item.forEach(itemMap -> {
            String id = (String) itemMap.get("id");
            if (childrenMap.containsKey(id)) {
                itemMap.put("children", childrenMap.get(id));
                treeList.add(itemMap);
            }
        }));
        return treeList;
    }


//    @Deprecated
//    private List<Map<String, Object>> findChild(Map<String, List<Map<String, Object>>> childrenMap,
//                                                List<Map<String, Object>> childrenList) {
//        List<Map<String, Object>> treeList = new ArrayList<>();
//
//        childrenMap.forEach((key, item) -> {
//            item.forEach(itemMap -> {
//                String id = (String) itemMap.get("id");
//                if (childrenMap.containsKey(id)) {
//                    findChild(childrenMap, childrenMap.get(id));
//                } else {
//                    itemMap.put("children", childrenMap.get(id));
//                    treeList.add(itemMap);
//                }
//            });
//        });
//        childrenList.forEach(itme -> {
//            String id = (String) itme.get("id");
//            if (childrenMap.containsKey(id)) {
//                findChild(childrenMap, childrenMap.get(id));
//            } else {
//                itme.put("children", childrenMap.get(id));
//                treeList.add(itme);
//            }
//        });
//        return treeList;
//    }
}
