package hrds.g.biz.interfaceusemonitor.interfaceuseinfo;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.InterfaceState;
import hrds.commons.entity.Interface_use;
import hrds.commons.entity.Interface_use_log;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.g.biz.init.InterfaceManager;

import java.util.Map;

@DocClass(desc = "查询接口监控信息类接口（接口使用信息）", author = "dhw", createdate = "2020/3/30 9:20")
public class InterfaceUseInfoAction extends BaseAction {

	private static final String REQUEST_STATE = "NORMAL";

	@Method(desc = "查询接口监控信息（接口使用监控）", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.判断用户是否为空，不为空加条件查询" +
			"3.判断有效截止日期是否为空，不为空加条件查询" +
			"4.查询接口使用信息" +
			"5.遍历接口使用信息封装响应时间参数" +
			"6.返回接口使用信息")
	@Param(name = "use_valid_date", desc = "有效截至日期", range = "yyyy-MM-dd格式", nullable = true)
	@Param(name = "user_id", desc = "接口所属用户ID", range = "无限制", nullable = true)
	@Return(desc = "返回接口使用信息", range = "无限制")
	private Result searchInterfaceUseInfo(String use_valid_date, Long user_id) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		SqlOperator.Assembler assembler = SqlOperator.Assembler.newInstance();
		assembler.clean();
		assembler.addSql("select interface_name,interface_code,user_name,start_use_date,use_valid_date," +
				"interface_use_id,use_state from " + Interface_use.TableName + " WHERE create_id = ?")
				.addParam(getUserId());
		// 2.判断用户是否为空，不为空加条件查询
		if (user_id != null) {
			assembler.addSql(" AND user_id = ?").addParam(user_id);
		}
		// 3.判断有效截止日期是否为空，不为空加条件查询
		if (StringUtil.isNotBlank(use_valid_date)) {
			assembler.addSql("AND use_valid_date = ?").addParam(use_valid_date);
		}
		assembler.addSql(" order by interface_use_id");
		// 4.查询接口使用信息
		Result infoResult = Dbo.queryResult(assembler.sql(), assembler.params());
		// 5.遍历接口使用信息封装响应时间参数
		for (int i = 0; i < infoResult.getRowCount(); i++) {
			Result response = Dbo.queryResult("SELECT round(avg(response_time)) avg,MIN(response_time) min,"
							+ "MAX(response_time) max FROM " + Interface_use_log.TableName +
							" WHERE request_state = ? AND interface_use_id=?", REQUEST_STATE,
					infoResult.getLong(i, "interface_use_id"));
			String avg = response.getString(0, "avg");
			String min = response.getString(0, "min");
			String max = response.getString(0, "max");
			if (StringUtil.isBlank(avg)) {
				avg = "0";
			}
			if (StringUtil.isBlank(min)) {
				min = "0";
			}
			if (StringUtil.isBlank(max)) {
				max = "0";
			}
			infoResult.setObject(i, "min", min);
			infoResult.setObject(i, "avg", avg);
			infoResult.setObject(i, "max", max);
		}
		// 6.返回接口使用信息
		return infoResult;
	}

	@Method(desc = "接口禁用启用（接口使用监控）", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.更新接口状态" +
			"3.重新初始化接口使用信息")
	@Param(name = "use_state", desc = "接口状态", range = "使用（InterfaceState）代码项")
	@Param(name = "interface_use_id", desc = "接口使用用户ID", range = "新增接口使用信息时生成")
	public void interfaceDisableEnable(Long interface_use_id, String use_state) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		try {
			InterfaceState.ofEnumByCode(use_state);
		} catch (Exception e) {
			throw new BusinessException("根据use_state=" + use_state + "没有找到对应代码项值");
		}
		// 2.更新接口状态
		DboExecute.updatesOrThrow("更新接口状态失败", "UPDATE " + Interface_use.TableName
				+ " set use_state = ? WHERE interface_use_id = ?", use_state, interface_use_id);
		// 3.重新初始化接口使用信息
		InterfaceManager.initInterface(Dbo.db());
	}

	@Method(desc = "查询接口监控信息（接口使用监控）", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.判断用户是否为空，不为空加条件查询" +
			"3.判断有效截止日期是否为空，不为空加条件查询" +
			"4.查询接口使用信息" +
			"5.遍历接口使用信息封装响应时间参数" +
			"6.返回接口使用信息")
	@Return(desc = "返回接口使用信息", range = "无限制")
	public Result searchInterfaceInfo() {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.返回查询接口监控数据表信息
		return searchInterfaceUseInfo("", null);
	}

	@Method(desc = "根据用户ID或有效日期查询接口监控信息（接口使用监控）",
			logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
					"2.判断用户是否为空，不为空加条件查询" +
					"3.判断有效截止日期是否为空，不为空加条件查询" +
					"4.查询接口使用信息" +
					"5.遍历接口使用信息封装响应时间参数" +
					"6.返回接口使用信息")
	@Param(name = "use_valid_date", desc = "有效截至日期", range = "yyyy-MM-dd格式", nullable = true)
	@Param(name = "user_id", desc = "接口所属用户ID", range = "无限制", nullable = true)
	@Return(desc = "返回接口使用信息", range = "无限制")
	public Result searchInterfaceInfoByIdOrDate(Long user_id, String use_valid_date) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.返回查询接口监控数据表信息
		return searchInterfaceUseInfo(use_valid_date, user_id);
	}

	@Method(desc = "删除接口使用信息（接口使用监控）", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.删除接口使用信息" +
			"3.重新初始化接口使用信息")
	@Param(name = "interface_use_id", desc = "接口使用用户ID", range = "新增接口使用信息时生成")
	public void deleteInterfaceUseInfo(Long interface_use_id) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.删除接口使用信息
		DboExecute.deletesOrThrow("当前接口ID对应接口信息不存在", "DELETE FROM "
				+ Interface_use.TableName + " WHERE interface_use_id = ?", interface_use_id);
		// 3.重新初始化接口使用信息
		InterfaceManager.initInterface(Dbo.db());
	}

	@Method(desc = " 根据接口使用ID获取相应的信息（接口使用监控）",
			logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
					"2.根据接口使用ID查询接口使用信息")
	@Param(name = "interface_use_id", desc = "接口使用用户ID", range = "新增接口使用信息时生成")
	@Return(desc = "返回根据接口使用ID获取相应的信息", range = "无限制")
	public Map<String, Object> searchInterfaceUseInfoById(Long interface_use_id) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.根据接口使用ID查询接口使用信息
		return Dbo.queryOneObject("SELECT interface_use_id,start_use_date,use_valid_date FROM "
				+ Interface_use.TableName + " WHERE interface_use_id = ?", interface_use_id);
	}

	@Method(desc = "更新接口使用信息（接口使用监控）", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.更新接口使用信息" +
			"3.重新初始化接口使用信息")
	@Param(name = "interface_use_id", desc = "接口使用用户ID", range = "新增接口使用信息时生成")
	@Param(name = "use_valid_date", desc = "有效截至日期", range = "yyyyMMdd格式,如：20200501")
	@Param(name = "start_use_date", desc = "有效截至日期", range = "yyyyMMdd格式，如：20200501")
	public void updateInterfaceUseInfo(Long interface_use_id, String start_use_date, String use_valid_date) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		if (start_use_date.contains("-") && start_use_date.length() == 10) {
			start_use_date = StringUtil.replace(start_use_date, "-", "");
		}
		if (use_valid_date.contains("-") && use_valid_date.length() == 10) {
			use_valid_date = StringUtil.replace(use_valid_date, "-", "");
		}
		// 2.更新接口使用信息
		DboExecute.updatesOrThrow("更新接口使用信息失败", " update " + Interface_use.TableName
						+ " set start_use_date=?,use_valid_date=? where interface_use_id = ?",
				start_use_date, use_valid_date, interface_use_id);
		// 3.重新初始化接口使用信息
		InterfaceManager.initInterface(Dbo.db());
	}

}
