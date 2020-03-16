package hrds.commons.tree.foreground.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;

import java.util.List;
import java.util.Map;

@DocClass(desc = "模型层(AML)层数据信息查询类", author = "BY-HLL", createdate = "2020/1/7 0007 上午 11:17")
public class AMLDataQuery {

	private static final SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();

	@Method(desc = "获取机器学习项目信息（模型层）",
			logicStep = "1.获取机器学习项目信息（模型层）" +
					"注意:只查询机器学习项目状态为'已发布'的项目数据")
	@Return(desc = "模型信息列表", range = "无限制")
	@Deprecated
	static List<Map<String, Object>> getModelProjectInfos() {
		//TODO 参数 publish_status要使用 PublishStatus 代码项 0:未发布,1:已发布,2:已下线,3:已删除
		return Dbo.queryList("SELECT project_id,project_name,project_desc FROM ml_project_info WHERE" +
				" publish_status = ?", 1);
	}
}
