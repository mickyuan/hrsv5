package hrds.k.biz.utils;

import fd.ng.core.utils.StringUtil;
import hrds.commons.utils.PropertyParaValue;
import hrds.k.biz.tdb.bean.AdaptRelationBean;
import hrds.k.biz.tdb.bean.NodeRelationBean;
import org.neo4j.driver.*;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

/**
 * 建立连接，拼接查询语句工具类
 *
 * @author zxz
 */
public class Neo4jUtils implements Closeable {
	Driver driver;
	Session session;

	public Neo4jUtils() {
		String neo4j_uri = PropertyParaValue.getString("neo4jUri", "bolt://172.168.0.60:7687");
		String neo4j_user = PropertyParaValue.getString("neo4j_user", "neo4j");
		String neo4j_password = PropertyParaValue.getString("neo4j_password", "hrsdxg");
		driver = GraphDatabase.driver(neo4j_uri, AuthTokens.basic(neo4j_user, neo4j_password));
		session = driver.session();
	}

	public Neo4jUtils(String uri, String user, String password) {
		driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
		session = driver.session();
	}

	private void addDataToNeo4j(String execute, Map<String, Object> parameters) {
		session.writeTransaction(tx -> tx.run(execute, parameters));
	}

	public Result queryNeo4j(String query) {
		return session.run(query);
	}

	public void executeNeo4j(String execute) {
		session.writeTransaction(tx -> tx.run(execute));
	}

	@Override
	public void close() {
		session.close();
		driver.close();
	}

	/**
	 * 自定义图计算查询语句查询
	 *
	 * @param cypher 查询语句
	 */
	public List<NodeRelationBean> searchFromNeo4j(String cypher) {
		return StandardGraphUtils.getRelationInfo(session.run(cypher));
	}

	/**
	 * 查询所有的表节点
	 * limitNum: 查询前多少条，可为空，为空则表示查询全部数据
	 */
	public Map<Long, Map<String, Object>> searchAllTableOfNodes(String limitNum) {
		String cypher = "MATCH (n:Table) RETURN n";
		if (!StringUtil.isEmpty(limitNum)) {
			cypher += " LIMIT " + limitNum;
		}
		return StandardGraphUtils.getNodeInfo(session.run(cypher));
	}

	/**
	 * 查询所有的字段节点
	 * limitNum: 查询前多少条，可为空，为空则表示查询全部数据
	 */
	public Map<Long, Map<String, Object>> searchAllColumnOfNodes(String limitNum) {
		String cypher = "MATCH (n:Column) RETURN n";
		if (!StringUtil.isEmpty(limitNum)) {
			cypher += " LIMIT " + limitNum;
		}
		return StandardGraphUtils.getNodeInfo(session.run(cypher));
	}

	/**
	 * 查询字段互为函数依赖关系的图
	 * limitNum: 查询前多少条，可为空，为空则表示查询全部数据
	 */
	public List<NodeRelationBean> searchColumnOfBfdRelation(String limitNum) {
		String cypher = "MATCH p=()-[r:BFD]->() RETURN p";
		if (!StringUtil.isEmpty(limitNum)) {
			cypher += " LIMIT " + limitNum;
		}
		return StandardGraphUtils.getRelationInfo(session.run(cypher));
	}

	/**
	 * 查询表和字段的包含关系的图
	 * limitNum: 查询前多少条，可为空，为空则表示查询全部数据
	 */
	public List<NodeRelationBean> searchColumnOfIncludeRelation(String limitNum) {
		String cypher = "MATCH p=()-[r:INCLUDE]->() RETURN p";
		if (!StringUtil.isEmpty(limitNum)) {
			cypher += " LIMIT " + limitNum;
		}
		return StandardGraphUtils.getRelationInfo(session.run(cypher));
	}

	/**
	 * 查询字段外键关系的图
	 * limitNum: 查询前多少条，可为空，为空则表示查询全部数据
	 */
	public List<NodeRelationBean> searchColumnOfFkRelation(String limitNum) {
		String cypher = "MATCH p=()-[r:INCLUDE]->() RETURN p";
		if (!StringUtil.isEmpty(limitNum)) {
			cypher += " LIMIT " + limitNum;
		}
		return StandardGraphUtils.getRelationInfo(session.run(cypher));
	}

	/**
	 * 查询字段函数依赖关系的图
	 * limitNum: 查询前多少条，可为空，为空则表示查询全部数据
	 */
	public List<NodeRelationBean> searchColumnOfFdRelation(String limitNum) {
		String cypher = "MATCH p=()-[r:FD]->() RETURN p";
		if (!StringUtil.isEmpty(limitNum)) {
			cypher += " LIMIT " + limitNum;
		}
		return StandardGraphUtils.getRelationInfo(session.run(cypher));
	}

	/**
	 * 查询字段相等关系的图
	 * limitNum: 查询前多少条，可为空，为空则表示查询全部数据
	 */
	public List<NodeRelationBean> searchColumnOfEqualsRelation(String limitNum) {
		String cypher = "MATCH p=()-[r:EQUALS]->() RETURN p";
		if (!StringUtil.isEmpty(limitNum)) {
			cypher += " LIMIT " + limitNum;
		}
		return StandardGraphUtils.getRelationInfo(session.run(cypher));
	}

	/**
	 * 查询字段相似关系的图
	 * limitNum: 查询前多少条，可为空，为空则表示查询全部数据
	 */
	public List<NodeRelationBean> searchColumnOfSameRelation(String limitNum) {
		String cypher = "MATCH p=()-[r:SAME]->() RETURN p";
		if (!StringUtil.isEmpty(limitNum)) {
			cypher += " LIMIT " + limitNum;
		}
		return StandardGraphUtils.getRelationInfo(session.run(cypher));
	}

	/**
	 * LPA社区发现算法
	 * relationship：为页面传参边的属性（FK、FD、EQUALS、SAME、BDF）
	 * iterations：为算法迭代次数
	 * limitNum: 查询前多少条，可为空，为空则表示查询全部数据
	 */
	public List<Map<String, Object>> searchLabelPropagation(String relationship, int iterations, String limitNum) {
		String cypher = "CALL algo.labelPropagation.stream('Column', '" + relationship
				+ "',{direction: 'OUTGOING', iterations: " + iterations + "})" +
				" yield  nodeId,label" +
				" return algo.getNodeById(nodeId).name as name ,label";
		if (!StringUtil.isEmpty(limitNum)) {
			cypher += " LIMIT " + limitNum;
		}
		return StandardGraphUtils.getLabelPropagationResult(session.run(cypher));
	}

	/**
	 * LOUVAIN社区发现算法
	 * relationship：为页面传参边的属性（FK、FD、EQUALS、SAME、BDF）
	 * iterations：为算法迭代次数
	 * limitNum: 查询前多少条，可为空，为空则表示查询全部数据
	 */
	public List<Map<String, Object>> searchLouVain(String relationship, int iterations, String limitNum) {
		String cypher = "call algo.louvain.stream('Column','" + relationship + "', {iterations:" + iterations + "})" +
				" YIELD nodeId,community" +
				" return algo.getNodeById(nodeId).name as name ,community";
		if (!StringUtil.isEmpty(limitNum)) {
			cypher += " LIMIT " + limitNum;
		}
		return StandardGraphUtils.getLouVainResult(session.run(cypher));
	}

//	/**
//	 * 求最短路径
//	 * columnNodeName1：第一个字段的节点名称
//	 * columnNodeName2：第二个字段的节点名称
//	 * level：最多找多少层
//	 */
//	public List<NodeRelationBean> searchShorPath(String columnNodeName1, String columnNodeName2, int level) {
//		String cypher = "MATCH (p1:Column {name:'" + columnNodeName1 + "'}),(p2:Column {name:'" + columnNodeName2 + "'})," +
//				" p=shortestpath((p1)-[*.." + level + "]-(p2))" +
//				" RETURN p";
//		return StandardGraphUtils.getRelationInfo(session.run(cypher));
//	}

	/**
	 * 求全部最短路径
	 * columnNodeName1：第一个字段的节点名称
	 * columnNodeName2：第二个字段的节点名称
	 * level：最多找多少层
	 * limitNum：查询多少条，可为空，为空则表示查询全部数据
	 */
	public List<AdaptRelationBean> searchAllShortPath(String columnNodeName1, String columnNodeName2,
													 int level, String limitNum) {
		String cypher = "MATCH (p1:Column {name:'" + columnNodeName1 + "'})," +
				" (p2:Column {name:'" + columnNodeName2 + "'})," +
				" p=allShortestPaths((p1)-[*.." + level + "]-(p2))" +
				" RETURN p";
		if (!StringUtil.isEmpty(limitNum)) {
			cypher += " LIMIT " + limitNum;
		}
		return StandardGraphUtils.getAdaptRelationInfo(session.run(cypher));
	}


	/**
	 * 求最长路径
	 * columnNodeName1：第一个字段的节点名称
	 * columnNodeName2：第二个字段的节点名称
	 * level：最多找多少层（这个值不能太大，不然查询超级慢）
	 * limitNum：查询多少条，可为空，为空则表示查询全部数据
	 */
	public List<AdaptRelationBean> searchLongestPath(String columnNodeName1, String columnNodeName2,
													 int level, String limitNum) {
		String cypher = "MATCH (a:Column {name:'" + columnNodeName1 + "'})," +
				" (b:Column {name:'" + columnNodeName2 + "'}),\n" +
				" p=(a)-[*.." + level + "]-(b)\n" +
				" RETURN p, length(p) ORDER BY length(p) DESC";
		if (!StringUtil.isEmpty(limitNum)) {
			cypher += " LIMIT " + limitNum;
		}
		return StandardGraphUtils.getAdaptRelationInfo(session.run(cypher));
	}

	/**
	 * 求远近邻关系
	 * columnNodeName：字段的节点名称
	 * level：最多找多少层（这个值不能太大，不然查询超级慢）
	 * limitNum：查询多少条，可为空，为空则表示查询全部数据
	 */
	public List<AdaptRelationBean> searchNeighbors(String columnNodeName, int level, String limitNum) {
		String cypher = "MATCH p=(:Column {name:'" + columnNodeName + "'})-[*.." + level + "]-() return p";
		if (!StringUtil.isEmpty(limitNum)) {
			cypher += " LIMIT " + limitNum;
		}
		return StandardGraphUtils.getAdaptRelationInfo(session.run(cypher));
	}

	/**
	 * 三角关系展示
	 * relationship：为页面传参边的属性（FK、FD、EQUALS、SAME、BDF）
	 * limitNum：查询多少条，可为空，为空则表示查询全部数据
	 */
	public List<AdaptRelationBean> searchTriangleRelation(String relationship, String limitNum) {
		String cypher = "";
		if (!StringUtil.isEmpty(relationship)) {
			cypher = "match b=(a)-[:" + relationship + "]-()-[:" + relationship
					+ "]-()-[:" + relationship + "]-(a) return b";
		} else {
			cypher = "match b=(a)-[]-()-[]-()-[]-(a) return b";
		}
		if (!StringUtil.isEmpty(limitNum)) {
			cypher += " LIMIT " + limitNum;
		}
		return StandardGraphUtils.getAdaptRelationInfo(session.run(cypher));
	}

	public static void main(String... args) {
		try (Neo4jUtils neo4jUtils = new Neo4jUtils()) {
//			System.out.println(neo4jUtils.searchAllShortPath("S10_I_PRO_ACCT_PA_INTEREST",
//					"S10_I_CPA_CPA_NAME", 10, "100"));
//			System.out.println(neo4jUtils.searchAllShortPath("S10_I_PRO_ACCT_PA_INTEREST",
//					"S10_I_CPA_CPA_NAME", 10, ""));
			System.out.println(neo4jUtils.searchAllShortPath("S10_I_CHOU_ACCT_CAT_HOU_KIND",
					"S10_I_CHOU_ACCT_CAT_OWN_NAME", 5, "10"));
		}
//		//查询
//		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
//			Result result = example.queryNeo4j("MATCH (n:Table) RETURN n LIMIT 25");
//			while (result.hasNext()) {
//				Record next = result.next();
//				System.out.println(next.values().get(0).asMap().toString());
//				System.out.println("============================================");
//			}
//		}
//		//删除所有节点的数据
//		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
//			example.executeNeo4j("match (n) detach delete n");
//		}
//		//加载表节点信息
//		try (DatabaseWrapper db = new DatabaseWrapper(); Neo4jUtils neo4jUtils =
//				new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
//			List<Map<String, Object>> maps = Dbo.queryList(db, "select sys_class_code || '_' || table_code " +
//					"AS name,sys_class_code,table_code AS tab_name,sys_class_code AS sys_cn_name " +
//					"from dbm_mmm_tab_info_tab");
//			for (Map<String, Object> map : maps) {
//				neo4jUtils.addDataToNeo4j("merge (:Table {name: $name, sys_class_code: $sys_class_code, " +
//						"tab_name: $tab_name, sys_cn_name: $sys_cn_name})", map);
//			}
//		}
//		//加载字段节点信息
//		try (DatabaseWrapper db = new DatabaseWrapper(); Neo4jUtils neo4jUtils =
//				new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
//			List<Map<String, Object>> maps = Dbo.queryList(db, "select table_code|| '_'||col_code as name,col_code" +
//					" as column_name,sys_class_code,table_code as tab_name from dbm_mmm_field_info_tab");
//			for (Map<String, Object> map : maps) {
//				neo4jUtils.addDataToNeo4j("merge (:Column {name: $name, column_name: $column_name, " +
//						"sys_class_code: $sys_class_code, tab_name: $tab_name})", map);
//			}
//		}
//		//加载表和字段的包含关系
//		try (DatabaseWrapper db = new DatabaseWrapper(); Neo4jUtils neo4jUtils =
//				new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
//			List<Map<String, Object>> maps = Dbo.queryList(db, "select sys_class_code || '_' || table_code as table," +
//					"table_code||'_'|| col_code as column from dbm_mmm_field_info_tab");
//			for (Map<String, Object> map : maps) {
//				neo4jUtils.addDataToNeo4j("MATCH (table:Table {name: $table}) " +
//						"MATCH (column:Column {name: $column}) " +
//						"MERGE (table)-[in:INCLUDE]->(column)", map);
//			}
//		}
//		//加载外键关系
//		try (DatabaseWrapper db = new DatabaseWrapper(); Neo4jUtils neo4jUtils =
//				new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
//			List<Map<String, Object>> maps = Dbo.queryList(db, "select left_table_code ||'_'||left_col_code as " +
//					"main_column ,right_table_code||'_'||right_col_code as sub_column from dbm_field_same_detail where rel_type = 'fk'");
//			for (Map<String, Object> map : maps) {
//				neo4jUtils.addDataToNeo4j("MATCH (main_column:Column {name: $main_column}) " +
//						"MATCH (sub_column:Column {name: $sub_column}) " +
//						"MERGE (main_column)-[fk:FK]->(sub_column)", map);
//			}
//		}
//		//加载互为函数依赖关系
//		try (DatabaseWrapper db = new DatabaseWrapper(); Neo4jUtils neo4jUtils =
//				new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
//			List<Map<String, Object>> maps = Dbo.queryList(db, "select left_table_code ||'_'||left_col_code as " +
//					"main_column,right_table_code||'_'||right_col_code as sub_column from dbm_field_same_detail where rel_type = 'bfd'");
//			for (Map<String, Object> map : maps) {
//				neo4jUtils.addDataToNeo4j("MATCH (main_column:Column {name: $main_column}) " +
//						"MATCH (sub_column:Column {name: $sub_column}) " +
//						"MERGE (main_column)-[bfd:BFD]->(sub_column)", map);
//			}
//		}
//		//加载相同类别关系
//		try (DatabaseWrapper db = new DatabaseWrapper(); Neo4jUtils neo4jUtils =
//				new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
//			List<Map<String, Object>> maps = Dbo.queryList(db, "select left_table_code ||'_'||left_col_code as " +
//					"main_column,right_table_code||'_'||right_col_code as sub_column from dbm_field_same_detail where rel_type = 'same'");
//			for (Map<String, Object> map : maps) {
//				neo4jUtils.addDataToNeo4j("MATCH (main_column:Column {name: $main_column}) " +
//						"MATCH (sub_column:Column {name: $sub_column}) " +
//						"MERGE (main_column)-[same:SAME]->(sub_column)", map);
//			}
//		}
//		//加载相等类别关系
//		try (DatabaseWrapper db = new DatabaseWrapper(); Neo4jUtils neo4jUtils =
//				new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
//			List<Map<String, Object>> maps = Dbo.queryList(db, "select left_table_code ||'_'||left_col_code as " +
//					"main_column,right_table_code||'_'||right_col_code as sub_column from dbm_field_same_detail where rel_type = 'equals'");
//			for (Map<String, Object> map : maps) {
//				neo4jUtils.addDataToNeo4j("MATCH (main_column:Column {name: $main_column}) " +
//						"MATCH (sub_column:Column {name: $sub_column}) " +
//						"MERGE (main_column)-[equals:EQUALS]->(sub_column)", map);
//			}
//		}
//		//加载函数依赖关系
//		try (DatabaseWrapper db = new DatabaseWrapper(); Neo4jUtils neo4jUtils =
//				new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
//			List<Map<String, Object>> maps = Dbo.queryList(db, "select left_table_code ||'_'||left_col_code as " +
//					"main_column,right_table_code||'_'||right_col_code as sub_column from dbm_field_same_detail where rel_type = 'fd'");
//			for (Map<String, Object> map : maps) {
//				neo4jUtils.addDataToNeo4j("MATCH (main_column:Column {name: $main_column}) " +
//						"MATCH (sub_column:Column {name: $sub_column}) " +
//						"MERGE (main_column)-[fd:FD]->(sub_column)", map);
//			}
//		}
//		//当既有FK又有BFD时，保留FK，删除BFD
//		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
//			example.executeNeo4j("MATCH (n)-[:FK]->(m) " +
//					"MATCH (n)-[r:BFD]->(m) " +
//					"delete r");
//		}
//		//当既有FK又有EQUALS时，保留FK，删除EQUALS
//		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
//			example.executeNeo4j("MATCH (n)-[:FK]->(m) " +
//					"MATCH (n)-[r:EQUALS]->(m) " +
//					"delete r");
//		}
//		//当既有BFD又有EQUALS时，保留EQUALS，删除BFD
//		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
//			example.executeNeo4j("MATCH (n)-[:EQUALS]->(m) " +
//					"MATCH (n)-[r:BFD]->(m) " +
//					"delete r");
//		}
//		//当EQUALS关系形成了互推，变成了两条关系线，删除一条关系线
//		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
//			example.executeNeo4j("MATCH (n)-[:EQUALS]->(m) " +
//					"MATCH (n)<-[r:EQUALS]-(m) " +
//					"delete r");
//		}
	}

}
