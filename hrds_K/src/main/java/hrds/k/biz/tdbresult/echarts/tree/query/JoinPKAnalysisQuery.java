package hrds.k.biz.tdbresult.echarts.tree.query;

import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.entity.Dbm_joint_pk_tab;
import hrds.commons.exception.BusinessException;

import java.util.List;
import java.util.Map;

/**
 * 联合主键分析查询类
 */
public class JoinPKAnalysisQuery {

	/**
	 * 获取联合外键分析表名
	 *
	 * @param db         DatabaseWrapper
	 * @param table_code 表名
	 * @return 搜索到的表名
	 */
	public static List<Object> getJoinPKAnalysisTableCode(DatabaseWrapper db, String table_code) {
		// 1.拼接sql,获取一条记录信息
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("SELECT DISTINCT(table_code) FROM " + Dbm_joint_pk_tab.TableName);
		if (StringUtil.isNotBlank(table_code)) {
			asmSql.addLikeParam("table_code", table_code, "WHERE");
		}
		return Dbo.queryOneColumnList(db, asmSql.sql(), asmSql.params());
	}

	/**
	 * 获取表的联合主键分析结果列表
	 *
	 * @param db         DatabaseWrapper
	 * @param table_code 表名
	 * @return 搜索到的表名
	 */
	public static List<Map<String, Object>> getJoinPkDataByTableCode(DatabaseWrapper db, String table_code) {
		//获取表的联合主键分析结果列表
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
		return Dbo.queryList(asmSql.sql(), asmSql.params());
	}

}
