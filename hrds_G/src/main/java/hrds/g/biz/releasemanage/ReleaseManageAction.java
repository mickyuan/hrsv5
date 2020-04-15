package hrds.g.biz.releasemanage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	@Param(name = "currPage", desc = "当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "每页显示条数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "返回当前接口类型信息", range = "无限制")
	public Map<String, Object> searchInterfaceInfoByType(String interface_type, int currPage, int pageSize) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		// 2.验证接口类型是否有效
		InterfaceType.ofEnumByCode(interface_type);
		// 3.返回当前接口类型信息
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Interface_info> interfaceInfoList = Dbo.queryPagedList(Interface_info.class, page,
				"SELECT * FROM " + Interface_info.TableName +
						" WHERE interface_type=? order by interface_id", interface_type);
		Map<String, Object> interfaceInfoMap = new HashMap<>();
		interfaceInfoMap.put("interfaceInfoList", interfaceInfoList);
		interfaceInfoMap.put("totalSize", page.getTotalSize());
		return interfaceInfoMap;
	}

	@Method(desc = "保存接口使用信息", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.判断接口ID，用户ID，开始日期，结束日期是否为空" +
			"3.遍历用户ID，接口ID保存接口使用信息" +
			"4.查询当前分配的用户是否存在使用信息" +
			"5.根据接口ID查找对应的接口信息" +
			"6.判断当前用户接口使用信息是否已存在，如果不存在新增，存在更新" +
			"6.1 新增接口用户信息" +
			"6.2 更新接口用户信息")
//	@Param(name = "interfaceInfos", desc = "保存接口使用信息实体对象", range = "无限制")
	@Param(name = "user_id", desc = "保存接口使用信息实体对象", range = "无限制")
	@Param(name = "interface_id", desc = "保存接口使用信息实体对象", range = "无限制")
	@Param(name = "start_use_date", desc = "保存接口使用信息实体对象", range = "无限制")
	@Param(name = "use_valid_date", desc = "保存接口使用信息实体对象", range = "无限制")
	@Param(name = "interface_note", desc = "保存接口使用信息实体对象", range = "无限制",nullable = true)
	@Param(name = "classify_name", desc = "保存接口使用信息实体对象", range = "无限制",nullable = true)
	public void saveInterfaceUseInfo(long[] user_id, long[] interface_id, String[] start_use_date,
	                                 String[] use_valid_date, String interface_note, String classify_name) {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		Interface_use interface_use = new Interface_use();
//		String[] start_use_date = interfaceInfos.getStart_use_date();
//		String[] use_valid_date = interfaceInfos.getUse_valid_date();
//		long[] interface_id = interfaceInfos.getInterface_id();
//		long[] user_id = interfaceInfos.getUser_id();
		// 2.判断接口ID，用户ID，开始日期，结束日期是否为空
		if (start_use_date == null || start_use_date.length == 0) {
			throw new BusinessException("开始日期不能为空");
		}
		if (use_valid_date == null || use_valid_date.length == 0) {
			throw new BusinessException("结束日期不能为空");
		}
		if (interface_id == null || interface_id.length == 0) {
			throw new BusinessException("接口ID不能为空");
		}
		if (user_id == null || user_id.length == 0) {
			throw new BusinessException("用户ID不能为空");
		}
		// 3.遍历用户ID，接口ID保存接口使用信息
		for (long userId : user_id) {
			for (int i = 0; i < interface_id.length; i++) {
				long startUseDate = Long.parseLong(start_use_date[i]);
				long useValidDate = Long.parseLong(use_valid_date[i]);
				long todayDate = Long.parseLong(DateUtil.getSysDate());
				if (startUseDate > useValidDate || useValidDate < todayDate) {
					throw new BusinessException("开始日期不能大于结束日期，结束日期不能小于当天日期");
				}
				// 4.查询当前分配的用户是否存在使用信息
				Result interfaceUseResult = Dbo.queryResult("SELECT * FROM " + Interface_use.TableName
						+ " WHERE user_id=? AND interface_id=?", userId, interface_id[i]);
				// 5.根据接口ID查找对应的接口信息
				Result interfaceInfoResult = Dbo.queryResult("SELECT * FROM " + Interface_info.TableName
						+ " WHERE interface_id=?", interface_id[i]);
				if (interfaceInfoResult.isEmpty()) {
					throw new BusinessException("当前接口ID对应的接口信息不存在，请检查，interface_id=" + interface_id[i]);
				}
				// 6.判断当前用户接口使用信息是否已存在，如果不存在新增，存在更新
				interface_use.setUse_state(interfaceInfoResult.getString(0, "interface_state"));
				interface_use.setStart_use_date(start_use_date[i]);
				interface_use.setUse_valid_date(use_valid_date[i]);
				if (interfaceUseResult.isEmpty()) {
					// 6.1 新增接口用户信息
					interface_use.setInterface_use_id(PrimayKeyGener.getNextId());
					List<Object> userNameList = Dbo.queryOneColumnList("SELECT user_name FROM "
							+ Sys_user.TableName + " WHERE user_id = ?", userId);
					if (userNameList.isEmpty()) {
						throw new BusinessException("当前用户对应用户信息已不存在，user_id=" + userId);
					}
					interface_use.setUser_id(userId);
					interface_use.setInterface_id(interface_id[i]);
					interface_use.setUser_name(userNameList.get(0).toString());
					interface_use.setInterface_code(interfaceInfoResult.getString(0, "interface_code"));
					interface_use.setUrl(interfaceInfoResult.getString(0, "url"));
					interface_use.setTheir_type(interfaceInfoResult.getString(0, "interface_type"));
					interface_use.setInterface_name(interfaceInfoResult.getString(0, "interface_name"));
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
							classify_name, userId, interface_id[i]);
				}
			}
		}
	}

}
