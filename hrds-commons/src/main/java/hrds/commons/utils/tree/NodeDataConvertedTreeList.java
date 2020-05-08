package hrds.commons.utils.tree;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;

import java.util.*;

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
            if (null == dataRecord.get("table_name")) {
                node.table_name = "";
            } else {
                node.table_name = dataRecord.get("table_name").toString();
            }
            if (null == dataRecord.get("original_name")) {
                node.original_name = "";
            } else {
                node.original_name = dataRecord.get("original_name").toString();
            }
            if (null == dataRecord.get("hyren_name")) {
                node.hyren_name = "";
            } else {
                node.hyren_name = dataRecord.get("hyren_name").toString();
            }
            nodeMap.put(node.id, node);
        });
        //对所有节点进行排序
        List<Map.Entry<String, Node>> list = new ArrayList<>(nodeMap.entrySet());
        //升序排序
        list.sort(Comparator.comparing(o -> o.getValue().id));
        // 构造无序的多叉树
        List<Node> treeList = new ArrayList<>();
        for (Map.Entry<String, Node> nodeEntry : list) {
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
