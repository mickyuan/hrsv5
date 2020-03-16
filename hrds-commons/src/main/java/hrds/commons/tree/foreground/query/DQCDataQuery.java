package hrds.commons.tree.foreground.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;

import java.util.List;
import java.util.Map;

@DocClass(desc = "管控层(DQC)层数据信息查询类", author = "BY-HLL", createdate = "2020/1/7 0007 上午 11:17")
public class DQCDataQuery {

	private static final SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();

	@Method(desc = "获取数据管控信息",
			logicStep = "1.获取数据管控信息")
	@Return(desc = "数据管控信息列表", range = "无限制")
	public static List<Map<String, Object>> getDQCDataInfos() {
		return getDQCDataInfos(null);
	}

	@Method(desc = "获取数据管控信息",
			logicStep = "1.获取数据管控信息" +
					"2.如果查询表名不为空,模糊检索数据管控表信息")
	@Param(name = "tableName", desc = "数据管控表名", range = "String类型,长度64")
	@Return(desc = "数据管控信息列表", range = "无限制")
	public static List<Map<String, Object>> getDQCDataInfos(String tableName) {
		//1.获取数据管控信息
		asmSql.clean();
		asmSql.addSql("SELECT * FROM dq_index3Record");
		//2.如果查询表名不为空,模糊检索数据管控表信息
		if (!StringUtil.isBlank(tableName)) {
			asmSql.addSql(" WHERE lower(table_name) LIKE lower(?)").addParam('%' + tableName + '%');
		}
		return Dbo.queryList(asmSql.sql(), asmSql.params());
	}
}
