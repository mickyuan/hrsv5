package hrds.agent.job.biz.core.dbstage.dbdialect.strategy;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;

@DocClass(desc = "数据库分页方言策略接口", author = "WangZhengcheng")
public interface DataBaseDialectStrategy {

	@Method(desc = "根据对应的数据库分页类型，获取分页SQL", logicStep = "")
	@Param(name = "strSql", desc = "采集SQL", range = "不为空")
	@Param(name = "start", desc = "当前页开始条数", range = "不限")
	@Param(name = "limit", desc = "当前页结束条数", range = "不限")
	@Return(desc = "根据参数组装成的用于分页查询的SQL", range = "不会为null")
	String createPageSql(String strSql, int start, int limit);
}
