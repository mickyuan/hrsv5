package hrds.k.biz.tdbresult;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.k.biz.tdb.bean.NodeRelationBean;
import hrds.k.biz.tdb.bean.TriangleRelationBean;
import hrds.k.biz.utils.DataConversionUtil;
import hrds.k.biz.utils.Neo4jUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "表数据对标结果(TableDataBenchmarkingResult)", author = "zxz,BY-HLL", createdate = "2021/2/2 0000 上午 09:49")
public class TDBResultAction extends BaseAction {

	@Method(desc = "获取数据对标分析的表主键信息", logicStep = "获取数据对标分析的表主键信息")
	@Param(name = "table_code", desc = "表名", range = "可为空", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "表主键信息", range = "表主键信息")
	public Map<String, Object> getPageTablePkData(String table_code, int currPage, int pageSize) {
		// 1.拼接sql
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT " +
				"  table_code, " +
				"  col_code, " +
				"  col_type, " +
				"  case col_nullable when '0' then '否' else '是' end as col_nullable, " +
				"  case col_pk when '0' then '否' else '是' end as col_pk, " +
				" row_number() over (partition BY table_code ORDER BY col_num) as col_num " +
				" FROM " + Dbm_mmm_field_info_tab.TableName);
		if (StringUtil.isNotBlank(table_code)) {
			asmSql.addLikeParam("table_code", "%" + table_code + "%", "WHERE");
		}
		// 2.分页查询作业定义信息
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> tablePkData = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
		// 3.创建存放分页查询作业定义信息、分页查询总记录数集合并封装数据
		Map<String, Object> tablePkDataMap = new HashMap<>();
		tablePkDataMap.put("tablePkData", tablePkData);
		tablePkDataMap.put("totalSize", page.getTotalSize());
		// 4.返回分页查询信息
		return tablePkDataMap;
	}

	@Method(desc = "获取数据对标分析的表联合主键信息", logicStep = "获取数据对标分析的表联合主键信息")
	@Param(name = "table_code", desc = "表名", range = "可为空", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "表联合主键信息", range = "表联合主键信息")
	public Map<String, Object> getPageTableJoinPkData(String table_code, int currPage, int pageSize) {
		// 1.拼接sql
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT" +
				" row_number() over(partition BY table_code ORDER BY group_code) col_num," +
				" table_code," +
				" string_agg(col_code,',') AS join_pk_col_code," +
				" group_code" +
				" FROM " + Dbm_joint_pk_tab.TableName);
		if (StringUtil.isNotBlank(table_code)) {
			asmSql.addLikeParam("table_code", "%" + table_code + "%", "WHERE");
		}
		asmSql.addSql(" GROUP BY table_code,group_code");
		// 2.分页查询作业定义信息
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> tableJoinPkData = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
		// 3.创建存放分页查询作业定义信息、分页查询总记录数集合并封装数据
		Map<String, Object> tableJoinPkDataMap = new HashMap<>();
		tableJoinPkDataMap.put("tableJoinPkData", tableJoinPkData);
		tableJoinPkDataMap.put("totalSize", page.getTotalSize());
		// 4.返回分页查询信息
		return tableJoinPkDataMap;
	}

	@Method(desc = "获取数据对标分析的表函数依赖的信息", logicStep = "获取数据对标分析的表函数依赖的信息")
	@Param(name = "table_code", desc = "表名", range = "可为空", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "表函数依赖的信息", range = "表函数依赖的信息")
	public Map<String, Object> getPageTableFuncDepData(String table_code, int currPage, int pageSize) {
		// 1.拼接sql
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT" +
				" row_number() over(partition BY table_code ORDER BY LENGTH(right_columns)-LENGTH(REPLACE" +
				" (right_columns,',','')) DESC,LENGTH(left_columns)-LENGTH(REPLACE(left_columns,',','')) ) AS" +
				" row_num," +
				" table_code," +
				" left_columns," +
				" right_columns" +
				" FROM" +
				" (" +
				" SELECT" +
				" string_agg(right_columns,',') AS right_columns," +
				" table_code," +
				" left_columns" +
				" FROM " + Dbm_function_dependency_tab.TableName);
		if (StringUtil.isNotBlank(table_code)) {
			asmSql.addLikeParam("table_code", "%" + table_code + "%", "WHERE");
		}
		asmSql.addSql(" GROUP BY" +
				" table_code," +
				" left_columns) temp_dep");
		// 2.分页查询作业定义信息
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> tableFuncDepData = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
		// 3.创建存放分页查询作业定义信息、分页查询总记录数集合并封装数据
		Map<String, Object> tableFuncDepDataMap = new HashMap<>();
		tableFuncDepDataMap.put("tableFuncDepData", tableFuncDepData);
		tableFuncDepDataMap.put("totalSize", page.getTotalSize());
		// 4.返回分页查询信息
		return tableFuncDepDataMap;
	}

	@Method(desc = "获取数据对标分析的表字段外键信息", logicStep = "获取数据对标分析的表字段外键信息")
	@Param(name = "fk_table_code", desc = "主表表名", range = "可为空", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "表字段外键信息", range = "表字段外键信息")
	public Map<String, Object> getPageTableFkData(String fk_table_code, int currPage, int pageSize) {
		// 1.拼接sql
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT" +
				" fk_table_code ," +
				" fk_col_code ," +
				" table_code," +
				" col_code," +
				" row_number() over(partition BY fk_table_code ORDER BY col_code) row_num" +
				" FROM " + Dbm_fk_info_tab.TableName);
		if (StringUtil.isNotBlank(fk_table_code)) {
			asmSql.addLikeParam("fk_table_code", "%" + fk_table_code + "%", "WHERE");
		}
		// 2.分页查询作业定义信息
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> tableFkData = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
		// 3.创建存放分页查询作业定义信息、分页查询总记录数集合并封装数据
		Map<String, Object> tableFkDataMap = new HashMap<>();
		tableFkDataMap.put("tableFkData", tableFkData);
		tableFkDataMap.put("totalSize", page.getTotalSize());
		// 4.返回分页查询信息
		return tableFkDataMap;
	}

	@Method(desc = "获取数据对标字段相等类别分析结果", logicStep = "获取数据对标字段相等类别分析结果")
	@Param(name = "table_code", desc = "表名", range = "可为空", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "字段相等类别分析结果", range = "字段相等类别分析结果")
	public Map<String, Object> getPageFieldSameResult(String table_code, int currPage, int pageSize) {
		// 1.拼接sql
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT" +
				" dim_order," +
				" table_code," +
				" col_code," +
				" category_same," +
				" rel_type" +
				" FROM " + Dbm_field_same_result.TableName
		);
		if (StringUtil.isNotBlank(table_code)) {
			if (table_code.contains("=")) {
				List<String> split = StringUtil.split(table_code, "=");
				if ("class".equals(split.get(0).trim()) && StringUtil.isNotBlank(split.get(1))) {
					asmSql.addSql("WHERE category_same = " + split.get(1));
				} else if (StringUtil.isNotBlank(split.get(1))) {
					asmSql.addLikeParam("table_code", "%" + split.get(1) + "%", "WHERE");
				}
			} else {
				asmSql.addLikeParam("table_code", "%" + table_code + "%", "WHERE");
			}
		}
		asmSql.addSql(" ORDER BY" +
				" category_same," +
				" dim_order");
		// 2.分页查询作业定义信息
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> fieldSameResult = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
		// 3.创建存放分页查询作业定义信息、分页查询总记录数集合并封装数据
		Map<String, Object> fieldSameResultMap = new HashMap<>();
		fieldSameResultMap.put("fieldSameResult", fieldSameResult);
		fieldSameResultMap.put("totalSize", page.getTotalSize());
		// 4.返回分页查询信息
		return fieldSameResultMap;
	}

	@Method(desc = "获取数据对标字段特征分析结果", logicStep = "获取数据对标字段特征分析结果")
	@Param(name = "table_code", desc = "表名", range = "可为空", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "字段特征分析结果", range = "字段特征分析结果")
	public Map<String, Object> getColumnFeatureAnalysisResult(String table_code, int currPage, int pageSize) {
		// 1.拼接sql
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT" +
				" table_code," +
				" col_code," +
				" col_records," +
				" col_distinct," +
				" max_len," +
				" min_len," +
				" avg_len," +
				" skew_len," +
				" kurt_len," +
				" median_len," +
				" var_len," +
				"case when has_chinese = '0' then '否' else '是' end as has_chinese," +
				"case when tech_cate = '1' then '日期' when tech_cate = '2' then '金额' when tech_cate = '3' then " +
				"'码值' when tech_cate = '4' then '数值' when tech_cate = '5' then '费率' else 'UNK' end as tech_cate" +
				" FROM " + Dbm_feature_tab.TableName
		);
		if (StringUtil.isNotBlank(table_code)) {
			asmSql.addLikeParam("table_code", "%" + table_code + "%", "WHERE");
		}
		asmSql.addSql(" ORDER BY table_code");
		// 2.分页查询作业定义信息
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> columnFeatureAnalysisResult = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
		// 3.创建存放分页查询作业定义信息、分页查询总记录数集合并封装数据
		Map<String, Object> columnFeatureAnalysisResultMap = new HashMap<>();
		columnFeatureAnalysisResultMap.put("columnFeatureAnalysisResult", columnFeatureAnalysisResult);
		columnFeatureAnalysisResultMap.put("totalSize", page.getTotalSize());
		// 4.返回分页查询信息
		return columnFeatureAnalysisResultMap;
	}

	@Method(desc = "自定义图计算查询语句查询", logicStep = "")
	@Param(name = "cypher", desc = "查询语句", range = "不能为空")
	@Return(desc = "", range = "")
	public Map<String, Object> searchFromNeo4j(String cypher) {
		Validator.notBlank(cypher, "查询语句不能为空");
		try (Neo4jUtils example = new Neo4jUtils()) {
			List<NodeRelationBean> nodeRelationBeans = example.searchFromNeo4j(cypher);
			return null;
//			return nodeRelationBeans;
		}
	}

	@Method(desc = "LPA社区发现算法", logicStep = "")
	@Param(name = "relationship", desc = "页面传参边的属性", range = "FK、FD、EQUALS、SAME、BDF")
	@Param(name = "iterations", desc = "算法迭代次数", range = "不能为空")
	@Param(name = "limitNum", desc = "查询前多少条", range = "可为空，为空则表示查询全部数据", nullable = true)
	@Return(desc = "", range = "")
	public Map<String, Object> searchLabelPropagation(String relationship, int iterations, String limitNum) {
		Validator.notBlank(relationship, "页面传参边的属性不能为空");
		Validator.notNull(iterations, "算法迭代次数不能为空");
		try (Neo4jUtils example = new Neo4jUtils()) {
			List<Map<String, Object>> mapList = example.searchLabelPropagation(relationship, iterations, limitNum);
			List<NodeRelationBean> nodeRelationBeans = getNodeRelationBeans(relationship, limitNum, example);
			return DataConversionUtil.conversionDataToEcharts(nodeRelationBeans, mapList, IsFlag.Shi.getCode());
		}
	}

	private List<NodeRelationBean> getNodeRelationBeans(String relationship, String limitNum, Neo4jUtils example) {
		List<NodeRelationBean> nodeRelationBeans;
		switch (relationship) {
			case "FK":
				nodeRelationBeans = example.searchColumnOfFkRelation(limitNum);
				break;
			case "FD":
				nodeRelationBeans = example.searchColumnOfFdRelation(limitNum);
				break;
			case "EQUALS":
				nodeRelationBeans = example.searchColumnOfEqualsRelation(limitNum);
				break;
			case "SAME":
				nodeRelationBeans = example.searchColumnOfSameRelation(limitNum);
				break;
			case "BFD":
				nodeRelationBeans = example.searchColumnOfBfdRelation(limitNum);
				break;
			default:
				throw new BusinessException("暂不支持边的属性为" + relationship);
		}
		return nodeRelationBeans;
	}

	@Method(desc = "LOUVAIN社区发现算法", logicStep = "")
	@Param(name = "relationship", desc = "页面传参边的属性", range = "FK、FD、EQUALS、SAME、BDF")
	@Param(name = "iterations", desc = "算法迭代次数", range = "不能为空")
	@Param(name = "limitNum", desc = "查询前多少条", range = "可为空，为空则表示查询全部数据", nullable = true)
	@Return(desc = "", range = "")
	public Map<String, Object> searchLouVain(String relationship, int iterations, String limitNum) {
		Validator.notBlank(relationship, "页面传参边的属性不能为空");
		Validator.notNull(iterations, "算法迭代次数不能为空");
		try (Neo4jUtils example = new Neo4jUtils()) {
			List<Map<String, Object>> mapList = example.searchLouVain(relationship, iterations, limitNum);
			List<NodeRelationBean> nodeRelationBeans = getNodeRelationBeans(relationship, limitNum, example);
			return DataConversionUtil.conversionDataToEcharts(nodeRelationBeans, mapList, IsFlag.Fou.getCode());
		}
	}

	@Method(desc = "求全部最短路径", logicStep = "")
	@Param(name = "columnNodeName1", desc = "第一个字段的节点名称", range = "不为空")
	@Param(name = "columnNodeName2", desc = "第二个字段的节点名称", range = "不为空")
	@Param(name = "level", desc = "最多找多少层", range = "不能为空")
	@Param(name = "limitNum", desc = "查询前多少条", range = "可为空，为空则表示查询全部数据", nullable = true)
	@Return(desc = "", range = "")
	public Map<String, Object> searchAllShortPath(String columnNodeName1, String columnNodeName2,
	                                              int level, String limitNum) {
		Validator.notBlank(columnNodeName1, "第一个字段的节点名称不能为空");
		Validator.notNull(columnNodeName2, "第二个字段的节点名称不能为空");
		Validator.notNull(level, "最多找多少层不能为空");
		try (Neo4jUtils example = new Neo4jUtils()) {
			List<NodeRelationBean> nodeRelationBeans =
					example.searchAllShortPath(columnNodeName1, columnNodeName2, level, limitNum);
			return DataConversionUtil.convertToEcharts(nodeRelationBeans, columnNodeName1, columnNodeName2);
		}
	}

	@Method(desc = "求最长路径", logicStep = "")
	@Param(name = "columnNodeName1", desc = "第一个字段的节点名称", range = "不为空")
	@Param(name = "columnNodeName2", desc = "第二个字段的节点名称", range = "不为空")
	@Param(name = "level", desc = "最多找多少层", range = "这个值不能太大，不然查询超级慢")
	@Param(name = "limitNum", desc = "查询前多少条", range = "可为空，为空则表示查询全部数据", nullable = true)
	@Return(desc = "", range = "")
	public Map<String, Object> searchLongestPath(String columnNodeName1, String columnNodeName2,
	                                             int level, String limitNum) {
		Validator.notBlank(columnNodeName1, "第一个字段的节点名称不能为空");
		Validator.notNull(columnNodeName2, "第二个字段的节点名称不能为空");
		Validator.notNull(level, "最多找多少层不能为空");
		try (Neo4jUtils example = new Neo4jUtils()) {
			List<NodeRelationBean> nodeRelationBeans = example.searchLongestPath(columnNodeName1, columnNodeName2, level, limitNum);
			return DataConversionUtil.convertToEcharts(nodeRelationBeans, columnNodeName1, columnNodeName2);
		}
	}

	@Method(desc = "求远近邻关系", logicStep = "")
	@Param(name = "columnNodeName", desc = "字段的节点名称", range = "不为空")
	@Param(name = "level", desc = "最多找多少层", range = "这个值不能太大，不然查询超级慢")
	@Param(name = "limitNum", desc = "查询前多少条", range = "可为空，为空则表示查询全部数据", nullable = true)
	@Return(desc = "", range = "")
	public Map<String, Object> searchNeighbors(String columnNodeName, int level, String limitNum) {
		Validator.notBlank(columnNodeName, "字段节点名称不能为空");
		Validator.notNull(level, "最多找多少层不能为空");
		try (Neo4jUtils example = new Neo4jUtils()) {
			List<NodeRelationBean> nodeRelationBeans = example.searchNeighbors(columnNodeName, level, limitNum);
			return DataConversionUtil.convertToEchartsTree(nodeRelationBeans);
		}
	}

	@Method(desc = "三角关系展示", logicStep = "")
	@Param(name = "relationship", desc = "为页面传参边的属性", range = "FK、FD、EQUALS、SAME、BDF")
	@Param(name = "limitNum", desc = "查询前多少条", range = "可为空，为空则表示查询全部数据", nullable = true)
	@Return(desc = "", range = "")
	public Map<String, Object> searchTriangleRelation(String relationship, String limitNum) {
		Validator.notBlank(relationship, "字段节点名称不能为空");
		try (Neo4jUtils example = new Neo4jUtils()) {
			List<TriangleRelationBean> triangleRelationBeans = example.searchTriangleRelation(relationship, limitNum);
			return DataConversionUtil.convertToTriangle(triangleRelationBeans);
		}
	}
}
