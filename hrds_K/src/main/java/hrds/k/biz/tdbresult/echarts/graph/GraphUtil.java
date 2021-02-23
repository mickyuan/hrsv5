package hrds.k.biz.tdbresult.echarts.graph;

import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.IsFlag;
import hrds.k.biz.tdb.bean.AdaptRelationBean;
import hrds.k.biz.tdb.bean.NodeRelationBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 图数据处理类
 */
public class GraphUtil {

	/**
	 * 转化节点信息为图数据信息
	 *
	 * @param category_info_map 节点分类
	 * @param node_info_map_s   节点
	 * @param link_map_s        节点关系
	 * @return 转化成graph数据
	 */
	public static Map<String, Object> dataConvertedGraphData(Map<String, Integer> category_info_map,
	                                                         List<Map<String, Object>> node_info_map_s,
	                                                         List<Map<String, Object>> link_map_s) {
		//初始化需要返回的结果
		Map<String, Object> graph_data_map = new HashMap<>();
		// 转化节点分类信息为 CategoryNode
		List<Category> categories = new ArrayList<>();
		category_info_map.forEach((k, v) -> {
			Category categoryNode = new Category();
			categoryNode.setName(k);
			categories.add(categoryNode);
		});
		graph_data_map.put("categories", categories);
		// 初始化图显示区域
		Map<Integer, Map<String, Integer>> area_map = initDisplayArea(category_info_map);
		// 转化节点信息为 RelationNode
		List<GraphNode> graphNodes = new ArrayList<>();
		for (Map<String, Object> node_info_map : node_info_map_s) {
			GraphNode graphNode = new GraphNode();
			graphNode.setId(node_info_map.get("id").toString());
			graphNode.setName(node_info_map.get("name").toString());
			graphNode.setValue(node_info_map.get("value").toString());
			Object category = node_info_map.get("category");
			graphNode.setCategory((Integer) category);
			//设置节点显示大小,如果节点关系越多,节点显示越大
			int symbolSizeFactor = 1;
			for (Map<String, Object> link_map : link_map_s) {
				if (node_info_map.get("id").toString().equalsIgnoreCase(link_map.get("source").toString())) {
					symbolSizeFactor++;
				}
			}
			graphNode.setSymbolSize(10.00 + symbolSizeFactor);
			//设置节点所在的 x y 轴位置
			Map<String, Integer> xy_map = area_map.get(category);
			graphNode.setX(2 * (Math.floor(Math.random() * 10000 + xy_map.get("x"))));
			graphNode.setY(Math.random() * 10000 + xy_map.get("y"));
			graphNodes.add(graphNode);
		}
		graph_data_map.put("nodes", graphNodes);
		// 转化关系信息为 Link
		List<Link> links = new ArrayList<>();
		for (Map<String, Object> link_map : link_map_s) {
			Link link = new Link();
			link.setSource(link_map.get("source").toString());
			link.setTarget(link_map.get("target").toString());
			links.add(link);
		}
		graph_data_map.put("links", links);
		return graph_data_map;
	}

	/**
	 * 提取分类
	 *
	 * @param tableFkDatas 查询的表外键分析结果数据
	 * @return List<CategoryNode> 分类节点列表
	 */
	public static Map<String, Integer> extractCategoryNode(List<Map<String, Object>> tableFkDatas) {
		//提出分类节点的Map
		Map<String, Integer> category_base_info = new HashMap<>();
		//提出所有分类名
		List<String> category_name_s = new ArrayList<>();
		tableFkDatas.forEach(tableFkData -> {
			//每一行结果的主表表名
			String fk_table_code = tableFkData.get("fk_table_code").toString();
			if (!category_name_s.contains(fk_table_code)) {
				category_name_s.add(fk_table_code);
			}
			//每一行结果的从表名
			String table_code = tableFkData.get("table_code").toString();
			if (!category_name_s.contains(table_code)) {
				category_name_s.add(table_code);
			}
		});
		//设置CategoryNode
		for (int i = 0; i < category_name_s.size(); i++) {
			category_base_info.put(category_name_s.get(i), i);
		}
		return category_base_info;
	}

	/**
	 * 提取节点
	 *
	 * @param tableFkDatas      查询的表外键分析结果数据
	 * @param category_info_map 节点分类信息
	 * @return List<RelationNode> 提取的节点列表
	 */
	public static List<Map<String, Object>> extractRelationNode(List<Map<String, Object>> tableFkDatas,
	                                                            Map<String, Integer> category_info_map) {
		List<Map<String, Object>> node_info_map_s = new ArrayList<>();
		//提出所有节点的基本信息(节点id,name)
		for (Map<String, Object> tableFkData : tableFkDatas) {
			Map<String, Object> map;
			//设置节点基本信息
			// id为主表加主表字段,category为主表表名对应的下标
			String id = tableFkData.get("fk_table_code") + "_" + tableFkData.get("fk_col_code").toString();
			String name = tableFkData.get("fk_col_code").toString();
			String value = tableFkData.get("fk_table_code").toString();
			int category = category_info_map.get(tableFkData.get("fk_table_code").toString());
			map = new HashMap<>();
			map.put("id", id);
			map.put("name", name);
			map.put("value", value);
			map.put("category", category);
			node_info_map_s.add(map);
			// id为从表加从表字段,category为从表表名对应下标
			id = tableFkData.get("table_code") + "_" + tableFkData.get("col_code").toString();
			name = tableFkData.get("col_code").toString();
			value = tableFkData.get("table_code").toString();
			category = category_info_map.get(tableFkData.get("table_code").toString());
			map = new HashMap<>();
			map.put("id", id);
			map.put("name", name);
			map.put("value", value);
			map.put("category", category);
			node_info_map_s.add(map);
		}
		return node_info_map_s.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * 提出所有节点的关系信息
	 *
	 * @param tableFkDatas 查询的表外键分析结果数据
	 * @return 节点的关系信息
	 */
	public static List<Map<String, Object>> extractLink(List<Map<String, Object>> tableFkDatas) {
		List<Map<String, Object>> link_map_s = new ArrayList<>();
		//提出所有节点的关系信息(source->target)
		for (Map<String, Object> tableFkData : tableFkDatas) {
			Map<String, Object> map = new HashMap<>();
			//设置关系基本信息
			String source = tableFkData.get("fk_table_code") + "_" + tableFkData.get("fk_col_code");
			String target = tableFkData.get("table_code") + "_" + tableFkData.get("col_code");
			map.put("source", source);
			map.put("target", target);
			link_map_s.add(map);
		}
		return link_map_s;
	}

	/**
	 * 初始化节点区域
	 *
	 * @param category_info_map 节点分类信息
	 * @return 节点展示的 X Y 轴位置
	 */
	public static Map<Integer, Map<String, Integer>> initDisplayArea(Map<String, Integer> category_info_map) {
		//设置区域大小,每个分类显示区域的大小,区域值越大重叠几率越小,该值必须大于分类内节点数量,否则会出现重复的点
		int area_size = 10000;
		//获取区域衡量值,该值的平方就是区域能存放节点区域的最小值,必然大于分类的个数
		int area_measure = 0;
		for (int i = 1; i <= category_info_map.size(); i++) {
			if (Math.pow(i, 2) <= category_info_map.size()) {
				area_measure = i + 1;
			}
		}
		//初始化区域list,x=y=area_measure 表示当x y 等于衡量值时 根据x y划分的区域刚好能存放所有分类
		List<Map<String, Integer>> area_map_list = new ArrayList<>();
		for (int i = 1; i <= area_measure; i++) {
			for (int j = 1; j <= area_measure; j++) {
				Map<String, Integer> map = new HashMap<>();
				map.put("x", i * area_size);
				map.put("y", j * area_size);
				area_map_list.add(map);
			}
		}
		//设置区域每个分类区域的 x y 值
		Map<Integer, Map<String, Integer>> area_map = new HashMap<>();
		category_info_map.forEach((k, v) -> area_map.put(v, area_map_list.get(v)));
		return area_map;
	}

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
		// 获取有分类的节点
		List<Object> names = dataMapList.stream().map(data -> data.get("name"))
			.distinct().collect(Collectors.toList());
		// 提取节点以及节点关系
		extractNodesAndRelationships(nodes, links, nodeRelationBeans);
		// 过滤掉没有分类的节点
		nodes = nodes.stream().filter(node -> names.contains(node.get("name"))).collect(Collectors.toList());
		// 过滤没有分类的节点关系
		links = links.stream()
			.filter(link -> names.contains(link.get("source")) && names.contains(link.get("target")))
			.collect(Collectors.toList());
		// 获取有分类的节点名称
		List<Object> nodeNames = nodes.stream().map(node -> node.get("name")).collect(Collectors.toList());
		List<Object> categoryList;
		// 提取节点分类信息
		if (IsFlag.Shi == IsFlag.ofEnumByCode(type)) {
			// lpa社区算法
			categoryList = dataMapList.stream().filter(data -> nodeNames.contains(data.get("name")))
				.map(o -> o.get("label")).distinct().collect(Collectors.toList());
		} else {
			// louvain社区算法
			categoryList = dataMapList.stream().filter(data -> nodeNames.contains(data.get("name")))
				.map(o -> o.get("community")).distinct().collect(Collectors.toList());
		}
		// 重新划分分类使从0开始
		Map<String, Integer> categoryMap = new HashMap<>();
		for (int i = 0; i < categoryList.size(); i++) {
			categoryMap.put(categoryList.get(i).toString(), i);
		}
		// 给节点设置分类
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
		for (Map<String, Object> node : nodes) {
			Map<String, Integer> xyMap = displayAreaMap.get(Integer.parseInt(node.get("category").toString()));
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

	/**
	 * @param nodeRelationBeans nodeRelationBeans neo4j 所有列节点关系数据
	 * @return 返回转换后的echarts格式数据
	 */
	public static Map<String, Object> convertToGraphData(List<NodeRelationBean> nodeRelationBeans) {
		List<Map<String, Object>> nodes = new ArrayList<>();
		List<Map<String, Object>> links = new ArrayList<>();
		extractNodesAndRelationships(nodes, links, nodeRelationBeans);
		Map<String, Object> dataMap = new HashMap<>();
		for (Map<String, Object> node : nodes) {
			node.put("x", Math.random() * 2000000);
			node.put("y", Math.random() * 1000000);
		}
		//设置节点显示大小,如果节点关系越多,节点显示越大
		double symbolSize = 10;
		for (Map<String, Object> node : nodes) {
			for (Map<String, Object> link_map : links) {
				if (node.get("id").toString().equalsIgnoreCase(link_map.get("source").toString())) {
					symbolSize = symbolSize > 30 ? 30 : symbolSize + 0.1;
				}
			}
			node.put("symbolSize", symbolSize);
		}
		dataMap.put("nodes", nodes);
		dataMap.put("links", links);
		return dataMap;
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

	private static void extractNodesAndRelationships(List<Map<String, Object>> nodes,
	                                                 List<Map<String, Object>> links,
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

	private static void setNode(List<Map<String, Object>> nodes, NodeRelationBean nodeRelationBean,
	                            Map.Entry<Long, Map<String, Object>> entry) {
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
