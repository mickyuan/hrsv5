package hrds.commons.utils.tree;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "节点数据转化为分叉树列表", author = "BY-HLL", createdate = "2020/2/20 0020 下午 09:49")
public class NodeDataConvertedTreeList {

    @Method(desc = "节点数据转化为分叉树列表", logicStep = "节点数据转化为分叉树列表,节点数据根节点数据的 parent_id 必须为0")
    @Param(name = "nodeDataList", desc = "节点数据列表", range = "nodeDataList")
    @Return(desc = "分叉树列表", range = "分叉树列表")
    public static List<Node> dataConversionTreeInfo(List<Map<String, Object>> nodeDataList) {
        // 节点列表（散列表，用于临时存储节点对象）
        Map<String, Node> nodeMap = new HashMap<>();
        // 根据结果集构造节点列表（存入散列表）
        nodeDataList.forEach(dataRecord -> {
            Node node = new Node();
            node.id = dataRecord.get("id").toString();
            node.label = dataRecord.get("label").toString();
            node.parent_id = dataRecord.get("parent_id").toString();
            if (null == dataRecord.get("description")) {
                node.description = "";
            } else {
                node.description = dataRecord.get("description").toString();
            }
            if (null == dataRecord.get("data_layer")) {
                node.data_layer = "";
            } else {
                node.data_layer = dataRecord.get("data_layer").toString();
            }
            if (null == dataRecord.get("data_own_type")) {
                node.data_own_type = "";
            } else {
                node.data_own_type = dataRecord.get("data_own_type").toString();
            }
            if (null == dataRecord.get("data_source_id")) {
                node.data_source_id = "";
            } else {
                node.data_source_id = dataRecord.get("data_source_id").toString();
            }
            if (null == dataRecord.get("agent_id")) {
                node.agent_id = "";
            } else {
                node.agent_id = dataRecord.get("agent_id").toString();
            }
            if (null == dataRecord.get("classify_id")) {
                node.classify_id = "";
            } else {
                node.classify_id = dataRecord.get("classify_id").toString();
            }
            if (null == dataRecord.get("file_id")) {
                node.file_id = "";
            } else {
                node.file_id = dataRecord.get("file_id").toString();
            }
            nodeMap.put(node.id, node);
        });
        // 构造无序的多叉树
        List<Node> treeList = new ArrayList<>();
        for (Map.Entry<String, Node> nodeEntry : nodeMap.entrySet()) {
            Node treeNodeData;
            Node node = nodeEntry.getValue();
            if (node.parent_id == null || "0".equals(node.parent_id)) {
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
