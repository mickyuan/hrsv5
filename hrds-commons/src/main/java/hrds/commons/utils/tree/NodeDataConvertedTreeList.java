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
            node.setId(dataRecord.get("id").toString());
            node.setLabel(dataRecord.get("label").toString());
            node.setParent_id(dataRecord.get("parent_id").toString());
            if (null == dataRecord.get("description")) {
                node.setDescription("");
            } else {
                node.setDescription(dataRecord.get("description").toString());
            }
            if (null == dataRecord.get("data_layer")) {
                node.setData_layer("");
            } else {
                node.setData_layer(dataRecord.get("data_layer").toString());
            }
            if (null == dataRecord.get("data_own_type")) {
                node.setData_own_type("");
            } else {
                node.setData_own_type(dataRecord.get("data_own_type").toString());
            }
            if (null == dataRecord.get("data_source_id")) {
                node.setData_source_id("");
            } else {
                node.setData_source_id(dataRecord.get("data_source_id").toString());
            }
            if (null == dataRecord.get("agent_id")) {
                node.setAgent_id("");
            } else {
                node.setAgent_id(dataRecord.get("agent_id").toString());
            }
            if (null == dataRecord.get("classify_id")) {
                node.setClassify_id("");
            } else {
                node.setClassify_id(dataRecord.get("classify_id").toString());
            }
            if (null == dataRecord.get("file_id")) {
                node.setFile_id("");
            } else {
                node.setFile_id(dataRecord.get("file_id").toString());
            }
            if (null == dataRecord.get("table_name")) {
                node.setTable_name("");
            } else {
                node.setTable_name(dataRecord.get("table_name").toString());
            }
            if (null == dataRecord.get("original_name")) {
                node.setOriginal_name("");
            } else {
                node.setOriginal_name(dataRecord.get("original_name").toString());
            }
            if (null == dataRecord.get("hyren_name")) {
                node.setHyren_name("");
            } else {
                node.setHyren_name(dataRecord.get("hyren_name").toString());
            }
            nodeMap.put(node.getId(), node);
        });
        //对所有节点进行排序
        List<Map.Entry<String, Node>> list = new ArrayList<>(nodeMap.entrySet());
        //升序排序
        list.sort(Comparator.comparing(o -> o.getValue().getId()));
        // 构造无序的多叉树
        List<Node> treeList = new ArrayList<>();
        for (Map.Entry<String, Node> nodeEntry : list) {
            Node treeNodeData;
            Node node = nodeEntry.getValue();
            //当前节点的父id为null或者为"0"是,则说明该节点为根节点
            if (node.getParent_id() == null || "0".equals(node.getParent_id())) {
                treeNodeData = node;
                treeList.add(treeNodeData);
            }
            //设置当前节点到父id一样节点的children子节点下
            else {
                nodeMap.get(node.getParent_id()).addChild(node);
            }
        }
        // 对多叉树进行横向排序
        for (Node node : treeList) {
            node.sortChildren();
        }
        return treeList;
    }
}
