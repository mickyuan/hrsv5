package hrds.k.biz.utils;

import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.web.util.Dbo;
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
	 * 查询所有的表节点
	 */
	public Map<Long, Map<String, Object>> searchAllTableOfNodes(String cypher) {
		//这里可能会改成传一个表名就行了,然后在这个方法里面拼接neo4j的语法
		return StandardGraphUtils.getNodeInfo(session.run(cypher));
	}

	/**
	 * 查询所有的字段节点
	 */
	public Map<Long, Map<String, Object>> searchAllColumnOfNodes(String cypher) {
		//这里可能会改成传一个字段名就行了,然后在这个方法里面拼接neo4j的语法
		return StandardGraphUtils.getNodeInfo(session.run(cypher));
	}

	/**
	 * 查询字段互为函数依赖关系的图
	 */
	public List<NodeRelationBean> searchColumnOfBfdRelation(String cypher) {
		//这里可能会改成传个字段名就行了,然后在这个方法里面拼接neo4j的语法
		return StandardGraphUtils.getRelationInfo(session.run(cypher));
	}

	/**
	 * 查询表和字段的包含关系的图
	 */
	public List<NodeRelationBean> searchColumnOfIncludeRelation(String cypher) {
		//这里可能会改成传表名或者字段名就行了,然后在这个方法里面拼接neo4j的语法
		return StandardGraphUtils.getRelationInfo(session.run(cypher));
	}

	/**
	 * 查询字段外键关系的图
	 */
	public List<NodeRelationBean> searchColumnOfFkRelation(String cypher) {
		//这里可能会改成传个字段名就行了,然后在这个方法里面拼接neo4j的语法
		return StandardGraphUtils.getRelationInfo(session.run(cypher));
	}

	/**
	 * 查询字段函数依赖关系的图
	 */
	public List<NodeRelationBean> searchColumnOfFdRelation(String cypher) {
		//这里可能会改成传个字段名就行了,然后在这个方法里面拼接neo4j的语法
		return StandardGraphUtils.getRelationInfo(session.run(cypher));
	}

	/**
	 * 查询字段相等关系的图
	 */
	public List<NodeRelationBean> searchColumnOfEqualsRelation(String cypher) {
		//这里可能会改成传个字段名就行了,然后在这个方法里面拼接neo4j的语法
		return StandardGraphUtils.getRelationInfo(session.run(cypher));
	}

	/**
	 * 查询字段相同关系的图
	 */
	public List<NodeRelationBean> searchColumnOfSameRelation(String cypher) {
		//这里可能会改成传个字段名就行了,然后在这个方法里面拼接neo4j的语法
		return StandardGraphUtils.getRelationInfo(session.run(cypher));
	}

	public static void main(String... args) {
		//查询
		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			Result result = example.queryNeo4j("MATCH (n:Table) RETURN n LIMIT 25");
			while (result.hasNext()) {
				Record next = result.next();
				System.out.println(next.values().get(0).asMap().toString());
				System.out.println("============================================");
			}
		}
		//删除所有节点的数据
		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			example.executeNeo4j("match (n) detach delete n");
		}
		//加载表节点信息
		try (DatabaseWrapper db = new DatabaseWrapper(); Neo4jUtils neo4jUtils =
				new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			List<Map<String, Object>> maps = Dbo.queryList(db, "select sys_class_code || '_' || table_code " +
					"AS name,sys_class_code,table_code AS tab_name,sys_class_code AS sys_cn_name " +
					"from dbm_mmm_tab_info_tab");
			for (Map<String, Object> map : maps) {
				neo4jUtils.addDataToNeo4j("merge (:Table {name: $name, sys_class_code: $sys_class_code, " +
						"tab_name: $tab_name, sys_cn_name: $sys_cn_name})", map);
			}
		}
		//加载字段节点信息
		try (DatabaseWrapper db = new DatabaseWrapper(); Neo4jUtils neo4jUtils =
				new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			List<Map<String, Object>> maps = Dbo.queryList(db, "select table_code|| '_'||col_code as name,col_code" +
					" as column_name,sys_class_code,table_code as tab_name from dbm_mmm_field_info_tab");
			for (Map<String, Object> map : maps) {
				neo4jUtils.addDataToNeo4j("merge (:Column {name: $name, column_name: $column_name, " +
						"sys_class_code: $sys_class_code, tab_name: $tab_name})", map);
			}
		}
		//加载表和字段的包含关系
		try (DatabaseWrapper db = new DatabaseWrapper(); Neo4jUtils neo4jUtils =
				new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			List<Map<String, Object>> maps = Dbo.queryList(db, "select sys_class_code || '_' || table_code as table," +
					"table_code||'_'|| col_code as column from dbm_mmm_field_info_tab");
			for (Map<String, Object> map : maps) {
				neo4jUtils.addDataToNeo4j("MATCH (table:Table {name: $table}) " +
						"MATCH (column:Column {name: $column}) " +
						"MERGE (table)-[in:INCLUDE]->(column)", map);
			}
		}
		//加载外键关系
		try (DatabaseWrapper db = new DatabaseWrapper(); Neo4jUtils neo4jUtils =
				new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			List<Map<String, Object>> maps = Dbo.queryList(db, "select left_table_code ||'_'||left_col_code as " +
					"main_column ,right_table_code||'_'||right_col_code as sub_column from dbm_field_same_detail where rel_type = 'fk'");
			for (Map<String, Object> map : maps) {
				neo4jUtils.addDataToNeo4j("MATCH (main_column:Column {name: $main_column}) " +
						"MATCH (sub_column:Column {name: $sub_column}) " +
						"MERGE (main_column)-[fk:FK]->(sub_column)", map);
			}
		}
		//加载互为函数依赖关系
		try (DatabaseWrapper db = new DatabaseWrapper(); Neo4jUtils neo4jUtils =
				new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			List<Map<String, Object>> maps = Dbo.queryList(db, "select left_table_code ||'_'||left_col_code as " +
					"main_column,right_table_code||'_'||right_col_code as sub_column from dbm_field_same_detail where rel_type = 'bfd'");
			for (Map<String, Object> map : maps) {
				neo4jUtils.addDataToNeo4j("MATCH (main_column:Column {name: $main_column}) " +
						"MATCH (sub_column:Column {name: $sub_column}) " +
						"MERGE (main_column)-[bfd:BFD]->(sub_column)", map);
			}
		}
		//加载相同类别关系
		try (DatabaseWrapper db = new DatabaseWrapper(); Neo4jUtils neo4jUtils =
				new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			List<Map<String, Object>> maps = Dbo.queryList(db, "select left_table_code ||'_'||left_col_code as " +
					"main_column,right_table_code||'_'||right_col_code as sub_column from dbm_field_same_detail where rel_type = 'same'");
			for (Map<String, Object> map : maps) {
				neo4jUtils.addDataToNeo4j("MATCH (main_column:Column {name: $main_column}) " +
						"MATCH (sub_column:Column {name: $sub_column}) " +
						"MERGE (main_column)-[same:SAME]->(sub_column)", map);
			}
		}
		//加载相等类别关系
		try (DatabaseWrapper db = new DatabaseWrapper(); Neo4jUtils neo4jUtils =
				new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			List<Map<String, Object>> maps = Dbo.queryList(db, "select left_table_code ||'_'||left_col_code as " +
					"main_column,right_table_code||'_'||right_col_code as sub_column from dbm_field_same_detail where rel_type = 'equals'");
			for (Map<String, Object> map : maps) {
				neo4jUtils.addDataToNeo4j("MATCH (main_column:Column {name: $main_column}) " +
						"MATCH (sub_column:Column {name: $sub_column}) " +
						"MERGE (main_column)-[equals:EQUALS]->(sub_column)", map);
			}
		}
		//加载函数依赖关系
		try (DatabaseWrapper db = new DatabaseWrapper(); Neo4jUtils neo4jUtils =
				new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			List<Map<String, Object>> maps = Dbo.queryList(db, "select left_table_code ||'_'||left_col_code as " +
					"main_column,right_table_code||'_'||right_col_code as sub_column from dbm_field_same_detail where rel_type = 'fd'");
			for (Map<String, Object> map : maps) {
				neo4jUtils.addDataToNeo4j("MATCH (main_column:Column {name: $main_column}) " +
						"MATCH (sub_column:Column {name: $sub_column}) " +
						"MERGE (main_column)-[fd:FD]->(sub_column)", map);
			}
		}
		//当既有FK又有BFD时，保留FK，删除BFD
		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			example.executeNeo4j("MATCH (n)-[:FK]->(m) " +
					"MATCH (n)-[r:BFD]->(m) " +
					"delete r");
		}
		//当既有FK又有EQUALS时，保留FK，删除EQUALS
		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			example.executeNeo4j("MATCH (n)-[:FK]->(m) " +
					"MATCH (n)-[r:EQUALS]->(m) " +
					"delete r");
		}
		//当既有BFD又有EQUALS时，保留EQUALS，删除BFD
		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			example.executeNeo4j("MATCH (n)-[:EQUALS]->(m) " +
					"MATCH (n)-[r:BFD]->(m) " +
					"delete r");
		}
		//当EQUALS关系形成了互推，变成了两条关系线，删除一条关系线
		try (Neo4jUtils example = new Neo4jUtils("bolt://172.168.0.60:7687", "neo4j", "hrsdxg")) {
			example.executeNeo4j("MATCH (n)-[:EQUALS]->(m) " +
					"MATCH (n)<-[r:EQUALS]-(m) " +
					"delete r");
		}
	}
}
