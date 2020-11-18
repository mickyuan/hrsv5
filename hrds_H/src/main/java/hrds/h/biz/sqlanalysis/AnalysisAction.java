package hrds.h.biz.sqlanalysis;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.web.action.AbstractWebappBaseAction;
import hrds.commons.utils.DruidParseQuerySql;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@DocClass(desc = "解析SQL中的信息", author = "Mr.Lee", createdate = "2020-11-13 15:24")
public class AnalysisAction extends AbstractWebappBaseAction {

	@Method(desc = "解析传递SQL的信息", logicStep = "获取SQL中的字段,表名,关联条件,过滤条件等信息")
	@Param(name = "sql", desc = "解析的SQL信息", range = "不可为空")
	@Param(name = "dbtype", desc = "SQL的数据库类型(使用Driuid(1.2.3)中DbType类中的数据库类型)", range = "不可为空")
	public Map<String, Object> analysisSqlData(String sql, String dbtype) {

		Map<String, Object> tableMap = DruidParseQuerySql.analysisTableRelation(sql, dbtype);
		JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(tableMap));
		JSONObject targetTableField = jsonObject.getJSONObject("targetTableField");
		if (targetTableField != null) {
			targetTableField.forEach((k, v) -> {
				Set<Object> set = new HashSet<>(targetTableField.getJSONArray(k));
				targetTableField.put(k, set);
			});
		}
		//获取条件信息
		tableMap.put("condition", DruidParseQuerySql.getSqlConditions(sql, dbtype));
		//获取关联信息
		tableMap.put("relation", DruidParseQuerySql.getRelationships(sql, dbtype));
		//获取列信息
		Map<String, List<String>> tableColumns = DruidParseQuerySql.getTableColumns(sql, dbtype);
		tableColumns.remove(tableMap.get("tableName"));
		tableMap.put("tableColumn", tableColumns);

		return tableMap;

	}


}
