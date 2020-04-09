package hrds.g.biz.interfaceusemonitor.datatableuseinfo;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Sys_user;
import hrds.commons.entity.Sysreg_parameter_info;
import hrds.commons.entity.Table_use_info;

import java.util.List;

@DocClass(desc = "查询接口监控信息类接口", author = "dhw", createdate = "2020/3/30 9:20")
public class DataTableUseInfoAction extends BaseAction {

	@Method(desc = "查询数据表信息（接口使用监控）",
			logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
					"2.判断用户ID是否为空，不为空增加调教查询" +
					"3.返回查询接口监控数据表信息")
	@Param(name = "user_id", desc = "接口所属用户ID", range = "无限制", nullable = true)
	@Return(desc = "返回查询接口监控数据表信息", range = "无限制")
	public Result searchTableDataInfo(Long user_id) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		SqlOperator.Assembler assembler = SqlOperator.Assembler.newInstance();
		assembler.addSql("SELECT distinct t1.use_id,t1.original_name,t1.sysreg_name,t2.table_column_name," +
				"t3.user_name FROM " + Table_use_info.TableName + " t1," + Sysreg_parameter_info.TableName +
				" t2," + Sys_user.TableName + " t3 WHERE t1.use_id = t2.use_id AND t1.user_id = t3.user_id");
		// 2.判断用户ID是否为空，不为空增加调教查询
		if (user_id != null) {
			assembler.addSql(" AND t1.user_id = ?").addParam(user_id);
		}
		assembler.addSql(" order by t3.use_id");
		// 3.返回查询接口监控数据表信息
		return Dbo.queryResult(assembler.sql(), assembler.params());
	}

	@Method(desc = "查看字段信息（接口使用监控）",
			logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
					"2.返回查询字段信息")
	@Param(name = "use_id", desc = "接口使用ID", range = "新增接口使用信息时生成")
	@Return(desc = "返回查询字段信息", range = "无限制")
	public List<Object> searchFieldInfoById(Long use_id) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.返回查询字段信息
		return Dbo.queryOneColumnList("SELECT table_column_name FROM " + Sysreg_parameter_info.TableName +
				" WHERE use_id = ?", use_id);
	}

	@Method(desc = "删除数据表信息（接口使用监控）",
			logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
					"2.删除系统登记表参数信息" +
					"3.删除表使用信息")
	@Param(name = "use_id", desc = "接口使用ID", range = "新增接口使用信息时生成")
	@Return(desc = "删除数据表信息", range = "无限制")
	public void deleteDataTableUseInfo(Long use_id) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.删除系统登记表参数信息
		Dbo.execute("delete from " + Sysreg_parameter_info.TableName + " where use_id = ?", use_id);
		// 3.删除表使用信息
		Dbo.execute("delete from " + Table_use_info.TableName + " where use_id = ?", use_id);
	}
}
