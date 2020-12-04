package hrds.k.biz.utils;

import hrds.k.biz.tdb.bean.NodeRelationBean;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 知识图谱工具类
 *
 * @author zxz
 */
public class StandardGraphUtils {

	private StandardGraphUtils() {
	}

	public static Map<Long, Map<String, Object>> getNodeInfo(Result searchResult) {
		Map<Long, Map<String, Object>> returnMap = new HashMap<>();
		while (searchResult.hasNext()) {
			Node node = searchResult.next().values().get(0).asNode();
			returnMap.put(node.id(), node.asMap());
		}
		return returnMap;
	}

	public static List<NodeRelationBean> getRelationInfo(Result searchResult) {
		List<NodeRelationBean> nodeRelationBeans = new ArrayList<>();
		while (searchResult.hasNext()) {
			NodeRelationBean nodeRelationBean = new NodeRelationBean();
			Map<Long, Map<String, Object>> leftNode = new HashMap<>();
			Map<Long, Map<String, Object>> rightNode = new HashMap<>();
			Path segments = searchResult.next().get(0).asPath();
			Node start = segments.start();
			leftNode.put(start.id(), start.asMap());
			Node end = segments.end();
			rightNode.put(end.id(), end.asMap());
			Iterable<Relationship> relationships = segments.relationships();
			relationships.forEach(relationship -> {
				nodeRelationBean.setRelationType(relationship.type());
				nodeRelationBean.setRelationId(relationship.id());
			});
			nodeRelationBean.setLeftNode(leftNode);
			nodeRelationBean.setRightNode(rightNode);
			nodeRelationBeans.add(nodeRelationBean);
		}
		return nodeRelationBeans;
	}

	public static void main(String[] args) {
		//查询表
		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			Map<Long, Map<String, Object>> setSetMap = example.searchAllTableOfNodes("MATCH (n:Table) RETURN n LIMIT 25");
			System.out.println(setSetMap);
		}
		//查询字段
		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			Map<Long, Map<String, Object>> setSetMap = example.searchAllColumnOfNodes("MATCH (n:Column) RETURN n LIMIT 25");
			System.out.println(setSetMap);
		}
		//查询互为函数依赖的关系
		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			List<NodeRelationBean> nodeRelationBeans = example.searchColumnOfBfdRelation(
					"MATCH p=()-[r:BFD]->() RETURN p LIMIT 25");
			System.out.println(nodeRelationBeans);
		}
		//查询字段外键关系
		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			List<NodeRelationBean> nodeRelationBeans = example.searchColumnOfFkRelation(
					"MATCH p=()-[r:FK]->() RETURN p LIMIT 25");
			System.out.println(nodeRelationBeans);
		}
		//查询字段包含关系
		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			List<NodeRelationBean> nodeRelationBeans = example.searchColumnOfIncludeRelation(
					"MATCH p=()-[r:INCLUDE]->() RETURN p LIMIT 25");
			System.out.println(nodeRelationBeans);
		}
		//查询字段Equals关系
		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			List<NodeRelationBean> nodeRelationBeans = example.searchColumnOfEqualsRelation(
					"MATCH p=()-[r:EQUALS]->() RETURN p LIMIT 25");
			System.out.println(nodeRelationBeans);
		}
		//查询字段Same关系
		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			List<NodeRelationBean> nodeRelationBeans = example.searchColumnOfSameRelation(
					"MATCH p=()-[r:SAME]->() RETURN p LIMIT 25");
			System.out.println(nodeRelationBeans);
		}
		//查询字段函数依赖关系
		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			List<NodeRelationBean> nodeRelationBeans = example.searchColumnOfFdRelation(
					"MATCH p=()-[r:FD]->() RETURN p LIMIT 25");
			System.out.println(nodeRelationBeans);
		}
	}
}