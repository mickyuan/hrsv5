package hrds.k.biz.tdbresult.echarts.graph;

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
		// 转化节点信息为 RelationNode
		List<GraphNode> graphNodes = new ArrayList<>();
		double x = 10, y = 10;
		for (Map<String, Object> node_info_map : node_info_map_s) {
			GraphNode graphNode = new GraphNode();
			graphNode.setId(node_info_map.get("id").toString());
			graphNode.setName(node_info_map.get("name").toString());
			graphNode.setValue(node_info_map.get("value").toString());
			graphNode.setCategory((Integer) node_info_map.get("category"));
			//设置节点显示大小,如果节点关系越多,节点显示越大
			int symbolSizeFactor = 1;
			for (Map<String, Object> link_map : link_map_s) {
				if (node_info_map.get("id").toString().equalsIgnoreCase(link_map.get("source").toString())) {
					symbolSizeFactor++;
				}
			}
			graphNode.setSymbolSize(10.00 + symbolSizeFactor);
			//设置节点所在的 x y 轴位置
//			getXYPosition(node_info_map, category_info_map);
			graphNode.setX(Math.random() * 500);
			graphNode.setY(Math.random() * 500);
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
		List<Category> category_node_s = new ArrayList<>();
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
	 * 获取节点展示的 X Y 轴位置
	 *
	 * @param node_info_map     单个节点信息
	 * @param category_info_map 节点分类信息
	 * @return 节点展示的 X Y 轴位置
	 */
	private static Map<String, Integer> getXYPosition(Map<String, Object> node_info_map, Map<String, Integer> category_info_map) {
		//设置区域大小
		Integer area_size = 100;
		//初始化 X Y 轴
		int x = 1, y = 1;
		for (int i = 1; i <= category_info_map.size(); i++) {
			if ((Integer) node_info_map.get("category") == i) {
				//如果 y 小于 x 轴,y+1
				if (y < x) {
					y++;
				}
				//如果 x y 相等,优先 x+1
				if (x * y == i * i) {
					x++;
				}
				System.out.println("x: " + x + "\t" + "y: " + y);
			}
		}
		return null;
	}
}
