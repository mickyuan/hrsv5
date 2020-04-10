package hrds.g.biz;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Interface_use_log;


@DocClass(desc = "接口响应时间类", author = "dhw", createdate = "2020/3/19 9:28")
public class InterfaceIndexAction extends BaseAction {

	@Method(desc = "查询接口响应时间", logicStep = "1.数据可访问权限处理方式：该方法user_id进行权限限制" +
			"2.返回接口响应时间信息")
	@Return(desc = "返回接口响应时间信息", range = "无限制")
	public Result interfaceResponseTime() {
		// 1.数据可访问权限处理方式：该方法user_id进行权限限制
		// 2.返回接口响应时间信息
		return Dbo.queryResult("select avg(response_time) avg,max(response_time) max," +
				"min(response_time) min,interface_name,interface_use_id from "
				+ Interface_use_log.TableName + " where request_state=? " +
				" group by interface_name,interface_use_id", "NORMAL");
	}

}
