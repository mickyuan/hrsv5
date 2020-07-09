package hrds.b.biz.agent.resourcerecod.tableregister;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.web.util.Dbo;
import hrds.b.biz.agent.CheckParam;
import hrds.commons.entity.Database_set;
import hrds.commons.exception.BusinessException;

@DocClass(desc = "表的登记信息管理", author = "Mr.Lee", createdate = "2020-07-07 11:04")
public class TableRegister {

	@Method(desc = "获取全表的信息", logicStep = "")
	@Param(name = "database_id", desc = "采集任务的ID", range = "不可为空")
	@Return(desc = "返回当前任务存储层链接下的表信息", range = "为空表示没有该表信息")
	public void getTableData(long database_id) {
		long countNum = Dbo
			.queryNumber("SELECT COUNT(1) FROM" + Database_set.TableName + " WHERE database_id = ?", database_id)
			.orElseThrow(() -> new BusinessException("SQL查询异常"));
		if (countNum == 0) {
			CheckParam.throwErrorMsg("任务ID(%s)不存在", database_id);
		}

	}
}
