package hrds.k.biz.utils;

import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.IsFlag;
import hrds.k.biz.tdb.bean.NodeRelationBean;
import hrds.k.biz.tdb.bean.TriangleRelationBean;

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
	 * neo4j数据转换为echarts格式数据
	 *
	 * @param nodeRelationBeans neo4j返回结果数据
	 * @return 返回转换后的echarts格式数据
	 */
	public static Map<String, Object> conversionDataToEcharts(List<NodeRelationBean> nodeRelationBeans,
	                                                          List<Map<String, Object>> dataMapList,
	                                                          String type) {
		List<Map<String, Object>> nodes = new ArrayList<>();
		List<Map<String, Object>> links = new ArrayList<>();
		processData(nodes, links, nodeRelationBeans);
		if (IsFlag.Shi == IsFlag.ofEnumByCode(type)) {
			Map<String, Object> newCategory = new HashMap<>();
			for (int i = 0; i < dataMapList.size(); i++) {
				newCategory.put(dataMapList.get(i).get("label").toString(), i);
			}
			for (Map<String, Object> node : nodes) {
				for (Map<String, Object> map : dataMapList) {
					if (node.get("name").equals(map.get("name"))) {
						node.put("category", newCategory.get(map.get("label").toString()));
					}
				}
			}
		} else {
			for (Map<String, Object> node : nodes) {
				for (Map<String, Object> map : dataMapList) {
					if (node.get("name").equals(map.get("name"))) {
						node.put("category", map.get("community"));
					}
				}
			}
		}
		List<String> categoryList =
				nodes.stream().map(node -> node.get("category").toString()).collect(Collectors.toList());
		for (Map<String, Object> nodeMap : nodes) {
			for (String category : categoryList) {
				if (nodeMap.get("category").equals(category)) {
					nodeMap.put("x", -Math.random() * 2000);
					nodeMap.put("y", Math.random() * 1000);
				}
			}
		}
		Map<String, Object> dataMap = new HashMap<>();
		if (IsFlag.Shi == IsFlag.ofEnumByCode(type)) {
			List<Object> categories = dataMapList.stream().map(data -> data.get("label"))
					.distinct().collect(Collectors.toList());
			dataMap.put("categories", categories);
		} else {
			List<Object> categories = dataMapList.stream().map(data -> data.get("community"))
					.distinct().collect(Collectors.toList());
			dataMap.put("categories", categories);
		}
		dataMap.put("nodes", nodes);
		dataMap.put("links", links);
		return dataMap;
	}

	public static Map<String, Object> convertToEcharts(List<NodeRelationBean> nodeRelationBeans,
	                                                   String columnNodeName1, String columnNodeName2) {
		List<Map<String, Object>> nodes = new ArrayList<>();
		List<Map<String, Object>> links = new ArrayList<>();
		processData(nodes, links, nodeRelationBeans);
		List<Object> categories = new ArrayList<>();
		for (Map<String, Object> node : nodes) {
			if (node.get("name").equals(columnNodeName1)) {
				// 起始点
				node.put("category", 0);
				categories.add(0);
			} else if (node.get("name").equals(columnNodeName2)) {
				// 结束点
				node.put("category", 1);
				categories.add(1);
			} else {
				// 中间点
				node.put("category", 2);
				categories.add(2);
			}
		}
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("nodes", nodes);
		dataMap.put("links", links.stream().distinct().collect(Collectors.toList()));
		dataMap.put("categories", categories);
		return dataMap;

	}

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

	public static Map<String, Object> convertToTriangle(List<TriangleRelationBean> triangleRelationBeans) {
		List<Map<String, Object>> nodes = new ArrayList<>();
		List<Map<String, Object>> links = new ArrayList<>();
		for (TriangleRelationBean triangleRelationBean : triangleRelationBeans) {
			Map<Long, Map<String, Object>> nodeCollection = triangleRelationBean.getNodeCollection();
			for (Map.Entry<Long, Map<String, Object>> entry : nodeCollection.entrySet()) {
				setNode(nodes, null, entry);
			}
			Map<Long, Map<String, Object>> relationCollection = triangleRelationBean.getRelationCollection();
			for (Map.Entry<Long, Map<String, Object>> entry : relationCollection.entrySet()) {
				Map<String, Object> linkMap = new HashMap<>();
				// 这里取出来是因为前端这里需要的是字符串，直接使用entry.getValue返回的是数值
				linkMap.put("source", entry.getValue().get("source").toString());
				linkMap.put("target", entry.getValue().get("target").toString());
				links.add(linkMap);
			}
		}
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("nodes", nodes);
		dataMap.put("links", links);
		return dataMap;
	}

	private static void processData(List<Map<String, Object>> nodes, List<Map<String, Object>> links,
	                                List<NodeRelationBean> nodeRelationBeans) {
		for (NodeRelationBean nodeRelationBean : nodeRelationBeans) {
			Map<Long, Map<String, Object>> leftNode = nodeRelationBean.getLeftNode();
			Map<Long, Map<String, Object>> rightNode = nodeRelationBean.getRightNode();
			String source = null;
			for (Map.Entry<Long, Map<String, Object>> entry : leftNode.entrySet()) {
				setNode(nodes, nodeRelationBean, entry);
				source = entry.getKey().toString();
			}
			String target = null;
			for (Map.Entry<Long, Map<String, Object>> entry : rightNode.entrySet()) {
				setNode(nodes, nodeRelationBean, entry);
				target = entry.getKey().toString();
			}
			if (StringUtil.isNotBlank(source) && StringUtil.isNotBlank(target)) {
				Map<String, Object> linkMap = new HashMap<>();
				linkMap.put("source", source);
				linkMap.put("target", target);
				links.add(linkMap);
			}
		}
	}

	private static void setNode(List<Map<String, Object>> nodes,
	                            NodeRelationBean nodeRelationBean, Map.Entry<Long, Map<String, Object>> entry) {
		Map<String, Object> nodeMap = new HashMap<>();
		nodeMap.put("id", entry.getKey());
		nodeMap.put("category", 0);
		nodeMap.put("x", -Math.random() * 2000);
		nodeMap.put("y", Math.random() * 1000);
		nodeMap.put("name", entry.getValue().get("name"));
		List<String> valueList = new ArrayList<>();
		valueList.add("id:" + entry.getKey());
		for (Map.Entry<String, Object> objectEntry : entry.getValue().entrySet()) {
			valueList.add(objectEntry.getKey() + ":" + objectEntry.getValue());
		}
		if (nodeRelationBean != null) {
			valueList.add("relationId:" + nodeRelationBean.getRelationId());
			valueList.add("relationType:" + nodeRelationBean.getRelationType());
		}
		nodeMap.put("value", valueList);
		if (nodes.isEmpty()) {
			nodes.add(nodeMap);
		} else {
			List<Object> idList = nodes.stream().map(node -> node.get("id")).collect(Collectors.toList());
			if (!idList.contains(entry.getKey())) {
				nodes.add(nodeMap);
			}
		}
	}
}
