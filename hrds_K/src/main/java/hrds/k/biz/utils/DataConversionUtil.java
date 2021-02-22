package hrds.k.biz.utils;

import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.IsFlag;
import hrds.k.biz.tdb.bean.AdaptRelationBean;
import hrds.k.biz.tdb.bean.NodeRelationBean;
import hrds.k.biz.tdbresult.echarts.graph.GraphUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Echarts数据格式转换工具类
 */
public class DataConversionUtil {

	/**
	 * LPA/LOUVAIN算法数据格式转换
	 *
	 * @param nodeRelationBeans neo4j所有结果数据
	 * @param dataMapList       neo4j lpa/louvain算法结果数据
	 * @param type              lpa还是louvain算法
	 * @return 返回转换后的echarts格式数据
	 */
	public static Map<String, Object> lpaOrLouvainConversion(List<NodeRelationBean> nodeRelationBeans,
	                                                         List<Map<String, Object>> dataMapList,
	                                                         String type) {
		List<Map<String, Object>> nodes = new ArrayList<>();
		List<Map<String, Object>> links = new ArrayList<>();
		processData(nodes, links, nodeRelationBeans);
		List<Object> categoryList = new ArrayList<>();
		for (Map<String, Object> node : nodes) {
			for (Map<String, Object> map : dataMapList) {
				if (node.get("name").equals(map.get("name"))) {
					if (IsFlag.Shi == IsFlag.ofEnumByCode(type)) {
						if (!categoryList.contains(map.get("label"))) {
							categoryList.add(map.get("label"));
						}
					} else {
						if (!categoryList.contains(map.get("community"))) {
							categoryList.add(map.get("community"));
						}
					}
				}
			}
		}
		Map<String, Integer> categoryMap = new HashMap<>();
		int size = categoryList.size();
		for (int i = 0; i < size; i++) {
			categoryMap.put(categoryList.get(i).toString(), i);
		}
		if (IsFlag.Shi == IsFlag.ofEnumByCode(type)) {
			for (Map<String, Object> node : nodes) {
				for (Map<String, Object> map : dataMapList) {
					if (node.get("name").equals(map.get("name"))) {
						node.put("category", categoryMap.get(map.get("label").toString()));
					}
				}
			}
		} else {
			for (Map<String, Object> node : nodes) {
				for (Map<String, Object> map : dataMapList) {
					if (node.get("name").equals(map.get("name"))) {
						node.put("category", categoryMap.get(map.get("community").toString()));
					}
				}
			}
		}
		Map<Integer, Map<String, Integer>> displayAreaMap = GraphUtil.initDisplayArea(categoryMap);
		long count = dataMapList.stream().map(data -> data.get("name")).distinct().count();
		if (count < nodes.size()) {
			// 如果有节点没分类，单独给这些节点划分为一个分类，并设置特定的区域
			Map<String, Integer> xyMap = new HashMap<>();
			int i = new Double(Math.ceil(Math.sqrt(count))).intValue() + 1;
			xyMap.put("x", (i + new Double(Math.random()).intValue()) * 12000);
			xyMap.put("y", (i + new Double(Math.random()).intValue()) * 7000);
			displayAreaMap.put(categoryList.size(), xyMap);
			categoryList.add(size);
			for (Map<String, Object> node : nodes) {
				node.putIfAbsent("category", size);
			}
		}
		for (Map<String, Object> node : nodes) {
			int i = Integer.parseInt(node.get("category").toString());
			Map<String, Integer> xyMap = displayAreaMap.get(i);
			node.put("x", (xyMap.get("x") + Math.random() * 10000) * 2);
			node.put("y", xyMap.get("y") + Math.random() * 10000);
		}
		Map<String, Object> dataMap = new HashMap<>();
		List<Object> categories = nodes.stream().map(data -> data.get("category"))
				.distinct().collect(Collectors.toList());
		dataMap.put("categories", categories.stream().sorted().collect(Collectors.toList()));
		dataMap.put("nodes", nodes);
		dataMap.put("links", links);
		return dataMap;
	}

	/**
	 * 最长最短数据转换
	 *
	 * @param adaptRelationBeans neo4j返回结果数据
	 * @param columnNodeName1    第一节点名称
	 * @param columnNodeName2    第二节点名称
	 * @return 返回转换后的echarts格式数据
	 */
	public static Map<String, Object> longestAndShortestDataConversion(List<AdaptRelationBean> adaptRelationBeans,
	                                                                   String columnNodeName1, String columnNodeName2) {
		List<Map<String, Object>> nodes = new ArrayList<>();
		Map<String, Object> dataMap = getEchartsData(adaptRelationBeans, nodes);
		for (Map<String, Object> node : nodes) {
			if (node.get("name").equals(columnNodeName1)) {
				// 起始点
				node.put("category", 0);
			} else if (node.get("name").equals(columnNodeName2)) {
				// 结束点
				node.put("category", 1);
			} else {
				// 中间点
				node.put("category", 2);
			}
		}
		return dataMap;
	}

	/**
	 * 远近邻数据格式转换
	 *
	 * @param nodeRelationBeans neo4j 远近邻结果数据
	 * @return 返回转换后的echarts格式数据
	 */
	public static Map<String, Object> convertToEchartsTree(List<NodeRelationBean> nodeRelationBeans) {
		Map<String, Object> treeMap = new HashMap<>();
		Map<Long, Map<String, Object>> leftNode = nodeRelationBeans.get(0).getLeftNode();
		for (Map.Entry<Long, Map<String, Object>> entry : leftNode.entrySet()) {
			treeMap.put("name", entry.getValue().get("name"));
		}
		List<Map<String, Object>> childrenList = new ArrayList<>();
		for (NodeRelationBean nodeRelationBean : nodeRelationBeans) {
			Map<String, Object> childNodeMap = new HashMap<>();
			Map<Long, Map<String, Object>> rightNode = nodeRelationBean.getRightNode();
			for (Map.Entry<Long, Map<String, Object>> entry : rightNode.entrySet()) {
				StringBuilder sb = new StringBuilder();
				for (Map.Entry<String, Object> objectEntry : entry.getValue().entrySet()) {
					sb.append(objectEntry.getKey()).append(":").append(objectEntry.getValue())
							.append(System.lineSeparator());
				}
				childNodeMap.put("name", entry.getValue().get("name"));
				childNodeMap.put("value", sb.toString());
			}
			childrenList.add(childNodeMap);
		}
		treeMap.put("children", childrenList);
		return treeMap;
	}

	/**
	 * 三角关系数据格式转换
	 *
	 * @param adaptRelationBeans neo4j 三角关系结果数据
	 * @return 返回转换后的echarts格式数据
	 */
	public static Map<String, Object> convertToTriangle(List<AdaptRelationBean> adaptRelationBeans) {
		List<Map<String, Object>> nodes = new ArrayList<>();
		return getEchartsData(adaptRelationBeans, nodes);
	}

	private static Map<String, Object> getEchartsData(List<AdaptRelationBean> adaptRelationBeans,
	                                                  List<Map<String, Object>> nodes) {
		List<Map<String, Object>> links = new ArrayList<>();
		for (AdaptRelationBean adaptRelationBean : adaptRelationBeans) {
			Map<Long, Map<String, Object>> nodeCollection = adaptRelationBean.getNodeCollection();
			for (Map.Entry<Long, Map<String, Object>> entry : nodeCollection.entrySet()) {
				setNode(nodes, null, entry);
			}
			Map<Long, Map<String, Object>> relationCollection = adaptRelationBean.getRelationCollection();
			for (Map.Entry<Long, Map<String, Object>> entry : relationCollection.entrySet()) {
				Map<String, Object> linkMap = new HashMap<>();
				// 这里取出来是因为前端这里需要的是字符串，直接使用entry.getValue返回的是数值
				long source = Long.parseLong(entry.getValue().get("source").toString());
				long target = Long.parseLong(entry.getValue().get("target").toString());
				linkMap.put("source", adaptRelationBean.getNodeCollection().get(source).get("name").toString());
				linkMap.put("target", adaptRelationBean.getNodeCollection().get(target).get("name").toString());
				links.add(linkMap);
			}
		}
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("nodes", nodes);
		dataMap.put("links", links.stream().distinct().collect(Collectors.toList()));
		return dataMap;
	}

	private static void processData(List<Map<String, Object>> nodes, List<Map<String, Object>> links,
	                                List<NodeRelationBean> nodeRelationBeans) {
		for (NodeRelationBean nodeRelationBean : nodeRelationBeans) {
			Map<Long, Map<String, Object>> leftNode = nodeRelationBean.getLeftNode();
			Map<Long, Map<String, Object>> rightNode = nodeRelationBean.getRightNode();
			// 起始节点
			String source = null;
			for (Map.Entry<Long, Map<String, Object>> entry : leftNode.entrySet()) {
				setNode(nodes, nodeRelationBean, entry);
				source = entry.getValue().get("name").toString();
			}
			// 目标节点
			String target = null;
			for (Map.Entry<Long, Map<String, Object>> entry : rightNode.entrySet()) {
				setNode(nodes, nodeRelationBean, entry);
				target = entry.getValue().get("name").toString();
			}
			if (StringUtil.isNotBlank(source) && StringUtil.isNotBlank(target)) {
				Map<String, Object> linkMap = new HashMap<>();
				linkMap.put("source", source);
				linkMap.put("target", target);
				linkMap.put("relationType", nodeRelationBean.getRelationType());
				links.add(linkMap);
			}
		}
	}

	private static void setNode(List<Map<String, Object>> nodes,
	                            NodeRelationBean nodeRelationBean, Map.Entry<Long, Map<String, Object>> entry) {
		Map<String, Object> nodeMap = new HashMap<>();
		nodeMap.put("id", entry.getValue().get("name"));
		nodeMap.put("name", entry.getValue().get("name"));
		List<String> valueList = new ArrayList<>();
		valueList.add("id:" + entry.getKey());
		for (Map.Entry<String, Object> objectEntry : entry.getValue().entrySet()) {
			valueList.add(objectEntry.getKey() + ":" + objectEntry.getValue());
		}
		if (nodeRelationBean != null) {
			valueList.add("relationId:" + nodeRelationBean.getRelationId());
		}
		nodeMap.put("value", valueList);
		if (nodes.isEmpty()) {
			nodes.add(nodeMap);
		} else {
			List<Object> nameList = nodes.stream().map(node -> node.get("name")).collect(Collectors.toList());
			if (!nameList.contains(entry.getValue().get("name"))) {
				nodes.add(nodeMap);
			}
		}
	}
}
