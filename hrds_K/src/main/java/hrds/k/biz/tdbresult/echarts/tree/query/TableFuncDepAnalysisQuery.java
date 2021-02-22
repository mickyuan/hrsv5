package hrds.k.biz.tdbresult.echarts.tree.query;

import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.entity.Dbm_function_dependency_tab;
import hrds.commons.exception.BusinessException;

import java.util.List;
import java.util.Map;

/**
 * 表内函数依赖分析查询类
 */
public class TableFuncDepAnalysisQuery {

	/**
	 * 表内函数依赖分析检索表名
	 *
	 * @param db         DatabaseWrapper
	 * @param table_code 表名
	 * @return 搜索到的表名
	 */
	public static List<Object> getTableFuncDepTableCode(DatabaseWrapper db, String table_code) {
		// 1.拼接sql,获取一条记录信息
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT DISTINCT(table_code) FROM " + Dbm_function_dependency_tab.TableName);
		if (StringUtil.isNotBlank(table_code)) {
			asmSql.addLikeParam("table_code", table_code, "WHERE");
		}
		return Dbo.queryOneColumnList(db, asmSql.sql(), asmSql.params());
	}


	/**
	 * 表内函数依赖分析检索结果列表
	 *
	 * @param db         DatabaseWrapper
	 * @param table_code 表名
	 * @return 搜索到的表名
	 */
	public static List<Map<String, Object>> getTableFuncDepDataByTableCode(DatabaseWrapper db, String table_code) {
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
			asmSql.addLikeParam("table_code", table_code, "WHERE");
		}
		asmSql.addSql(" GROUP BY" +
			" table_code," +
			" left_columns) temp_dep");
		return Dbo.queryList(db, asmSql.sql(), asmSql.params());
	}
}
