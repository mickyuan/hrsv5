package hrds.g.biz.releasemanage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.InterfaceType;
import hrds.commons.codes.UserType;
import hrds.commons.entity.Interface_info;
import hrds.commons.entity.Interface_use;
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.g.biz.init.InterfaceManager;

import java.util.List;

@DocClass(desc = "接口发布管理类", author = "dhw", createdate = "2020/3/25 14:07")
public class ReleaseManageAction extends BaseAction {

	@Method(desc = "搜索管理用户信息",
			logicStep = "1.数据可访问权限处理方式：该方法通过create_id进行访问权限限制" +
					"查询返回接口用户信息,这里使用union因为取并集不包括重复行")
	@Return(desc = "返回接口用户信息", range = "无限制")
	public Result searchUserInfo() {
		// 1.数据可访问权限处理方式：该方法通过create_id进行访问权限限制
		// 2.查询返回接口用户信息,这里使用union因为取并集不包括重复行
		return Dbo.queryResult("SELECT user_id,user_name,user_email,user_remark FROM "
						+ Sys_user.TableName + " WHERE create_id=? AND user_type=? union " +
						"select  user_id,user_name,user_email,user_remark from " + Sys_user.TableName +
						" where usertype_group like ?", getUserId(), UserType.RESTYongHu.getCode(),
				"%" + UserType.RESTYongHu.getCode() + "%");
	}

	@Method(desc = "根据接口类型分页查询接口信息", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.验证接口类型是否有效" +
			" 3.返回当前接口类型信息")
	@Param(name = "interface_type", desc = "接口类型", range = "使用（InterfaceType）代码项")
	@Return(desc = "返回当前接口类型信息", range = "无限制")
	public List<Interface_info> searchInterfaceInfoByType(String interface_type) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.验证接口类型是否有效
		InterfaceType.ofEnumByCode(interface_type);
		// 3.返回当前接口类型信息
		return Dbo.queryList(Interface_info.class, "SELECT * FROM " + Interface_info.TableName +
				" WHERE interface_type=? order by interface_id", interface_type);
	}

	@Method(desc = "保存接口使用信息", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.判断接口ID，用户ID，开始日期，结束日期是否为空" +
			"3.遍历用户ID，接口ID保存接口使用信息" +
			"4.查询当前分配的用户是否存在使用信息" +
			"5.根据接口ID查找对应的接口信息" +
			"6.判断当前用户接口使用信息是否已存在，如果不存在新增，存在更新" +
			"6.1 新增接口用户信息" +
			"6.2 更新接口用户信息" +
			"7.重新更新接口使用信息")
	@Param(name = "interfaceUses", desc = "接口使用信息实体对象数组", range = "与数据库表字段对应规则一致",
			isBean = true)
	@Param(name = "userIds", desc = "保存接口使用信息实体对象", range = "无限制")
	@Param(name = "interface_note", desc = "保存接口使用信息实体对象", range = "无限制", nullable = true)
	@Param(name = "classify_name", desc = "保存接口使用信息实体对象", range = "无限制", nullable = true)
	public void saveInterfaceUseInfo(Interface_use[] interfaceUses, long[] userIds,
	                                 String interface_note, String classify_name) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		if (userIds == null || userIds.length == 0) {
			throw new BusinessException("用户ID不能为空");
		}
		// 3.遍历用户ID，接口ID保存接口使用信息
		for (long userId : userIds) {
			for (Interface_use interface_use : interfaceUses) {
				String start_use_date = interface_use.getStart_use_date();
				String use_valid_date = interface_use.getUse_valid_date();
				Long interface_id = interface_use.getInterface_id();
				// 2.判断接口ID，用户ID，开始日期，结束日期是否为空
				Validator.notBlank(start_use_date, "开始日期不能为空");
				Validator.notBlank(use_valid_date, "结束日期不能为空");
				Validator.notNull(interface_use.getInterface_id(), "接口ID不能为空");
				Validator.notNull(interface_use.getUrl(), "接口地址不能为空");
				Validator.notNull(interface_use.getInterface_name(), "接口名称不能为空");
				long startUseDate = Long.parseLong(start_use_date);
				long useValidDate = Long.parseLong(use_valid_date);
				long todayDate = Long.parseLong(DateUtil.getSysDate());
				if (startUseDate > useValidDate || useValidDate < todayDate) {
					throw new BusinessException("开始日期不能大于结束日期，结束日期不能小于当天日期");
				}
				// 4.查询当前分配的用户是否存在使用信息
				Result interfaceUseResult = Dbo.queryResult("SELECT * FROM " + Interface_use.TableName
						+ " WHERE user_id=? AND interface_id=?", userId, interface_id);
				// 5.根据接口ID查找对应的接口信息
				Result interfaceInfoResult = Dbo.queryResult("SELECT * FROM " + Interface_info.TableName
						+ " WHERE interface_id=?", interface_id);
				if (interfaceInfoResult.isEmpty()) {
					throw new BusinessException("当前接口ID对应的接口信息不存在，请检查，interface_id=" + interface_id);
				}
				// 6.判断当前用户接口使用信息是否已存在，如果不存在新增，存在更新
				interface_use.setUse_state(interfaceInfoResult.getString(0, "interface_state"));
				interface_use.setStart_use_date(start_use_date);
				interface_use.setUse_valid_date(use_valid_date);
				if (interfaceUseResult.isEmpty()) {
					// 6.1 新增接口用户信息
					interface_use.setInterface_use_id(PrimayKeyGener.getNextId());
					List<String> userNameList = Dbo.queryOneColumnList("SELECT user_name FROM "
							+ Sys_user.TableName + " WHERE user_id = ?", userId);
					if (userNameList.isEmpty()) {
						throw new BusinessException("当前用户对应用户信息已不存在，user_id=" + userId);
					}
					interface_use.setUser_id(userId);
					interface_use.setTheir_type(interfaceInfoResult.getString(0, "interface_type"));
					interface_use.setInterface_id(interface_id);
					interface_use.setUser_name(userNameList.get(0));
					interface_use.setInterface_note(interface_note);
					interface_use.setClassify_name(classify_name);
					interface_use.setCreate_id(getUserId());
					interface_use.add(Dbo.db());
				} else {
					// 6.2更新接口使用信息
					Dbo.execute("UPDATE " + Interface_use.TableName + " SET use_valid_date=?," +
									"start_use_date=?,use_state=?,interface_note=?,create_id=?," +
									"classify_name=? WHERE user_id=? AND interface_id=?",
							interface_use.getUse_valid_date(), interface_use.getStart_use_date(),
							interface_use.getUse_state(), interface_note, getUserId(),
							classify_name, userId, interface_id);
				}
			}
		}
		// 7.重新更新接口使用信息
		InterfaceManager.initInterface(Dbo.db());
	}
}
