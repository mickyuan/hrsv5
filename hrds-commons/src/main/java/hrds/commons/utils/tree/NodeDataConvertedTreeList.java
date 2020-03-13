package hrds.commons.utils.tree;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;

import java.util.*;

@DocClass(desc = "节点数据转化为分叉树列表", author = "BY-HLL", createdate = "2020/2/20 0020 下午 09:49")
public class NodeDataConvertedTreeList {

    @Method(desc = "节点数据转化为分叉树列表", logicStep = "节点数据转化为分叉树列表")
    @Param(name = "nodeDataList", desc = "节点数据列表", range = "nodeDataList")
    @Return(desc = "分叉树列表", range = "分叉树列表")
    public static List<Node> dataConversionTreeInfo(List<Map<String, Object>> nodeDataList) {
        // 节点列表（散列表，用于临时存储节点对象）
        Map<String, Node> nodeMap = new HashMap<>();
        // 根据结果集构造节点列表（存入散列表）
        nodeDataList.forEach(dataRecord -> {
            Node node = new Node();
            node.id = (String) dataRecord.get("id");
            node.label = (String) dataRecord.get("label");
            node.parent_id = (String) dataRecord.get("parent_id");
            node.description = (String) dataRecord.get("description");
            nodeMap.put(node.id, node);
        });
        // 构造无序的多叉树
        List<Node> treeList = new ArrayList<>();
        for (Map.Entry<String, Node> nodeEntry : nodeMap.entrySet()) {
            Node treeNodeData;
            Node node = nodeEntry.getValue();
            if (node.parent_id == null || "0" .equals(node.parent_id)) {
                treeNodeData = node;
                treeList.add(treeNodeData);
            } else {
                nodeMap.get(node.parent_id).addChild(node);
            }
        }
        // 对多叉树进行横向排序
        for (Node node : treeList) {
            node.sortChildren();
        }
        return treeList;
    }
}
