package hrds.k.biz.tdbresult.echarts.tree.util;

import fd.ng.core.utils.JsonUtil;
import hrds.commons.exception.BusinessException;

import java.util.*;

/**
 * echars tree 节点数据转树数据
 */
public class NodeDataConvertedTreeList {

	/**
	 * @param nodeList 节点信息列表
	 * @return 树列表
	 */
	public static List<EcharsTreeNode> echarsTreeNodesConversionTreeInfo(List<Map<String, Object>> nodeList) {
		// 节点列表（散列表，用于临时存储节点对象）
		Map<String, EcharsTreeNode> nodeMap = new HashMap<>();
		// 根据结果集构造节点列表（存入散列表）
		nodeList.forEach(dataRecord -> {
			EcharsTreeNode echarsTreeNode = new EcharsTreeNode();
			echarsTreeNode.setId(dataRecord.get("id").toString());
			echarsTreeNode.setName(dataRecord.get("name").toString());
			echarsTreeNode.setValue(dataRecord.get("value").toString());
			echarsTreeNode.setParent_id(dataRecord.get("parent_id").toString());
			nodeMap.put(echarsTreeNode.getId(), echarsTreeNode);
		});
		//对所有节点进行排序
		List<Map.Entry<String, EcharsTreeNode>> list = new ArrayList<>(nodeMap.entrySet());
		//升序排序
		list.sort(Comparator.comparing(o -> o.getValue().getId()));
		// 构造无序的多叉树
		List<EcharsTreeNode> treeList = new ArrayList<>();
		for (Map.Entry<String, EcharsTreeNode> nodeEntry : list) {
			EcharsTreeNode treeEcharsTreeNodeData;
			EcharsTreeNode echarsTreeNode = nodeEntry.getValue();
			try {
				//当前节点的父id为null或者为"0"是,则说明该节点为根节点
				if (echarsTreeNode.getParent_id() == null || "0".equals(echarsTreeNode.getParent_id())) {
					treeEcharsTreeNodeData = echarsTreeNode;
					treeList.add(treeEcharsTreeNodeData);
				}
				//否则设置当前节点到父id一样节点的children子节点下
				else {
					//如果当前节点的父节点不为null,则添加当前节点到父节点下
					if (null != nodeMap.get(echarsTreeNode.getParent_id())) {
						nodeMap.get(echarsTreeNode.getParent_id()).addChild(echarsTreeNode);
					}
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
				throw new BusinessException("当前节点信息 node: " + JsonUtil.toJson(echarsTreeNode) + " 所属的父id parent_id: " + echarsTreeNode.getParent_id());
			}
		}
		return treeList;
	}
}
