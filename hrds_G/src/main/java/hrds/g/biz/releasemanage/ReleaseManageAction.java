package hrds.g.biz.releasemanage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
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

import java.util.List;

@DocClass(desc = "接口发布管理类", author = "dhw", createdate = "2020/3/25 14:07")
public class ReleaseManageAction extends BaseAction {

	@Method(desc = "搜索发布管理信息(首页展示）",
			logicStep = "1.数据可访问权限处理方式：该方法通过create_id进行访问权限限制" +
					"2.返回接口用户信息")
	@Return(desc = "返回接口用户信息", range = "无限制")
	public Result searchReleaseManageInfo() {
		// 1.数据可访问权限处理方式：该方法通过create_id进行访问权限限制
		// 2.返回接口用户信息
		return Dbo.queryResult("SELECT user_id,user_name,user_email,user_remark FROM "
						+ Sys_user.TableName + " WHERE create_id=? AND user_type=? union all " +
						"select  user_id,user_name,user_email,user_remark from " + Sys_user.TableName +
						" where usertype_group like ?", getUserId(), UserType.RESTYongHu.getCode(),
				"%" + UserType.RESTYongHu.getCode() + "%");
	}

	@Method(desc = "查看接口类型信息", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.验证接口类型是否有效" +
			" 3.返回当前接口类型信息")
	@Param(name = "interface_type", desc = "接口类型", range = "使用（InterfaceType）代码项")
	@Return(desc = "返回当前接口类型信息", range = "无限制")
	public List<Interface_info> viewInterfaceTypeInfo(String interface_type) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.验证接口类型是否有效
		InterfaceType.ofEnumByCode(interface_type);
		// 3.返回当前接口类型信息
		return Dbo.queryList(Interface_info.class, "SELECT * FROM " + Interface_info.TableName +
				" WHERE interface_type=? order by interface_id", interface_type);
	}

	@Method(desc = "保存接口使用信息", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.遍历用户ID，接口ID保存接口使用信息" +
			"3.查询当前分配的用户是否存在使用信息" +
			"4.根据接口ID查找对应的接口信息" +
			"5.判断当前用户接口使用信息是否已存在，如果不存在新增，存在更新" +
			"5.1 新增接口用户信息" +
			"5.2 更新接口用户信息")
	@Param(name = "user_id", desc = "用户ID数组对象", range = "新增用户时生成")
	@Param(name = "interface_id", desc = "接口信息表ID数组对象", range = "初始化表时生成")
	@Param(name = "start_use_date", desc = "开始使用日期数组对象", range = "无限制")
	@Param(name = "use_valid_date", desc = "接口有效日期数组对象", range = "无限制")
	@Param(name = "user_name", desc = "用户名", range = "新增用户时生成")
	@Param(name = "interface_note", desc = "备注", range = "无限制", nullable = true)
	@Param(name = "classify_name", desc = "分类名称", range = "新增分类时生成")
	public void saveInterfaceUserInfo(long[] user_id, String interface_note, String classify_name,
	                                  long[] interface_id, String[] start_use_date, String[] use_valid_date) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		Interface_use interface_use = new Interface_use();

		// 2.遍历用户ID，接口ID保存接口使用信息
		for (long userId : user_id) {
			for (int i = 0; i < interface_id.length; i++) {
				long startUseDate =
						Long.parseLong(DateUtil.parseStr2DateWith8Char(start_use_date[i]).toString());
				long useValidDate =
						Long.parseLong(DateUtil.parseStr2DateWith8Char(use_valid_date[i]).toString());
				long todayDate = Long.parseLong(DateUtil.getSysDate());
				if (startUseDate > useValidDate || useValidDate < todayDate) {
					throw new BusinessException("开始日期不能大于结束日期，结束日期不能小于当天日期");
				}
				// 3.查询当前分配的用户是否存在使用信息
				Result interfaceUseResult = Dbo.queryResult("SELECT * FROM " + Interface_use.TableName
						+ " WHERE user_id=? AND interface_id=?", userId, interface_id[i]);
				// 4.根据接口ID查找对应的接口信息
				Result interfaceInfoResult = Dbo.queryResult("SELECT * FROM " + Interface_info.TableName
						+ " WHERE interface_id=?", interface_id[i]);
				if (interfaceInfoResult.isEmpty()) {
					throw new BusinessException("当前接口ID对应的接口信息不存在，请检查，interface_id=" + interface_id[i]);
				}
				// 5.判断当前用户接口使用信息是否已存在，如果不存在新增，存在更新
				interface_use.setUse_state(interfaceInfoResult.getString(0, "interface_state"));
				interface_use.setStart_use_date(String.valueOf(startUseDate));
				interface_use.setUse_valid_date(String.valueOf(useValidDate));
				interface_use.setUse_valid_date(DateUtil.parseStr2TimeWith6Char(use_valid_date[i]).toString());
				if (interfaceUseResult.isEmpty()) {
					// 5.1 新增接口用户信息
					interface_use.setInterface_use_id(PrimayKeyGener.getNextId());
					List<Object> userNameList = Dbo.queryOneColumnList("SELECT user_name FROM "
							+ Sys_user.TableName + " WHERE user_id = ?", userId);
					if (userNameList.isEmpty()) {
						throw new BusinessException("当前用户对应用户信息已不存在，user_id=" + userId);
					}
					interface_use.setUser_id(userId);
					interface_use.setInterface_id(interface_id[i]);
					interface_use.setUser_name(userNameList.get(0).toString());
					interface_use.setInterface_code(interfaceUseResult.getString(0, "interface_code"));
					interface_use.setUrl(interfaceInfoResult.getString(0, "url"));
					interface_use.setTheir_type(interfaceInfoResult.getString(0, "interface_type"));
					interface_use.setInterface_name(interfaceInfoResult.getString(0, "interface_name"));
					interface_use.setInterface_note(interface_note);
					interface_use.setClassify_name(classify_name);
					interface_use.setCreate_id(getUserId());
					interface_use.add(Dbo.db());
				} else {
					// 5.2更新接口使用信息
					Dbo.execute("UPDATE " + Interface_use.TableName + " SET use_valid_date=?," +
									"start_use_date=?,use_state=?,interface_note=?,create_id=?," +
									"classify_name=? WHERE user_id=? AND interface_id=?",
							interface_use.getUse_valid_date(), interface_use.getStart_use_date(),
							interface_use.getUse_state(), interface_note, getUserId(), classify_name, userId,
							interface_id[i]);
				}
			}
		}
	}

}
