package hrds.g.biz.usermanage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.UserState;
import hrds.commons.codes.UserType;
import hrds.commons.entity.Interface_use;
import hrds.commons.entity.Sys_user;
import hrds.commons.entity.Sysreg_parameter_info;
import hrds.commons.entity.Table_use_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@DocClass(desc = "接口用户管理", author = "dhw", createdate = "2020/3/24 13:34")
public class InterfaceUserManageAction extends BaseAction {

	@Method(desc = "分页查询接口用户信息", logicStep = "1.数据可访问权限处理方式：该方法通过user_id进行权限限制" +
			"2.分页查询接口用户信息" +
			"3.封装并返回接口用户信息与分页总记录数")
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "分页查询返回接口用户信息", range = "无限制")
	public Map<String, Object> selectUserInfoByPage(int currPage, int pageSize) {
		// 1.数据可访问权限处理方式：该方法user_id进行权限限制
		// 2.分页查询接口用户信息
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> userList = Dbo.queryPagedList(page, "select user_name,user_id," +
						"user_password,user_email,user_remark from " + Sys_user.TableName +
						" where create_id=? and user_type=?", getUserId(),
				UserType.RESTYongHu.getCode());
		// 3.封装并返回接口用户信息与分页总记录数
		Map<String, Object> userMap = new HashMap<>();
		userMap.put("userList", userList);
		userMap.put("totalSize", page.getTotalSize());
		return userMap;
	}

	@Method(desc = "根据用户名分页查询接口用户信息",
			logicStep = "1.数据可访问权限处理方式：该方法通过user_id进行权限限制" +
					"2.分页查询接口用户信息" +
					"3.封装并返回接口用户信息与分页总记录数")
	@Param(name = "user_name", desc = "用户名", range = "新增用户时生成")
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "返回分页查询接口用户信息", range = "无限制")
	public Map<String, Object> selectUserInfoByName(String user_name, int currPage, int pageSize) {
		// 1.数据可访问权限处理方式：该方法user_id进行权限限制
		// 2.分页查询接口用户信息
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> userList = Dbo.queryPagedList(page, "select user_name,user_id," +
						"user_password,user_email,user_remark from " + Sys_user.TableName +
						" where create_id=? and user_type=? and user_name=?", getUserId(),
				UserType.RESTYongHu.getCode(), user_name);
		// 3.封装并返回接口用户信息与分页总记录数
		Map<String, Object> userMap = new HashMap<>();
		userMap.put("userList", userList);
		userMap.put("totalSize", page.getTotalSize());
		return userMap;
	}

	@Method(desc = "添加接口用户", logicStep = "1.数据可访问权限处理方式：该方法不需要进行权限限制" +
			"2.检查系统用户字段合法性" +
			"3.封装添加接口用户的一些默认参数" +
			"4.添加接口用户")
	@Param(name = "sys_user", desc = "用户信息表对象", range = "与数据库表定义规则一致", isBean = true)
	public void addUser(Sys_user sys_user) {
		// 1.数据可访问权限处理方式：该方法不需要进行权限限制
		// 2.检查系统用户字段合法性 fixme 应该使用一个公共的校验类进行校验
		checkFieldsForSysUser(sys_user);
		// 3.封装接口用户一些默认参数
		sys_user.setUser_id(PrimayKeyGener.getOperId());
		sys_user.setUser_state(UserState.ZhengChang.getCode());
		sys_user.setCreate_date(DateUtil.getSysDate());
		sys_user.setCreate_time(DateUtil.getSysTime());
		sys_user.setUpdate_date(DateUtil.getSysDate());
		sys_user.setUpdate_time(DateUtil.getSysTime());
		sys_user.setRole_id(getUser().getRoleId());
		sys_user.setDep_id(getUser().getDepId());
		sys_user.setCreate_id(getUserId());
		sys_user.setUser_type(UserType.RESTYongHu.getCode());
		// 4.添加接口用户
		sys_user.add(Dbo.db());
	}

	@Method(desc = "检查系统用户字段合法性", logicStep = "1.数据可访问权限处理方式：该方法不需要进行权限限制")
	@Param(name = "sys_user", desc = "用户信息表对象", range = "与数据库表定义规则一致", isBean = true)
	private void checkFieldsForSysUser(Sys_user sys_user) {
		// 1.数据可访问权限处理方式：该方法不需要进行权限限制
		// 2.判断用户名是否为空
		if (StringUtil.isBlank(sys_user.getUser_name())) {
			throw new BusinessException("用户名不能为空");
		}
		// 3.判断密码是否为空
		if (StringUtil.isBlank(sys_user.getUser_password())) {
			throw new BusinessException("密码不能为空");
		}
		// 4.判断邮箱地址是否合法
		if (StringUtil.isBlank(sys_user.getUser_email())) {
			throw new BusinessException("邮箱地址不能为空");
		}
		Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
		Matcher matcher = pattern.matcher(sys_user.getUser_email());
		if (!matcher.matches()) {
			throw new BusinessException("邮箱地址格式不正确");
		}
	}

	@Method(desc = "更新接口用户", logicStep = "1.数据可访问权限处理方式：该方法通过user_id进行权限限制" +
			"2.判断当前登录用户对应用户是否还存在" +
			"3.创建用户实体对象封装实体参数" +
			"4.检查系统用户字段合法性" +
			"5.更新接口用户信息")
	@Param(name = "user_name", desc = "用户名", range = "新增用户时生成")
	@Param(name = "user_email", desc = "用户邮箱", range = "新增用户时生成")
	@Param(name = "user_password", desc = "用户密码", range = "新增用户时生成")
	@Param(name = "user_id", desc = "用户表主键ID", range = "新增用户时生成")
	@Param(name = "user_remark", desc = "备注", range = "新增用户时生成", nullable = true)
	public void updateUser(String user_name, String user_email, String user_password, long user_id,
	                       String user_remark) {
		// 1.数据可访问权限处理方式：该方法通过user_id进行权限限制
		// 2.判断当前登录用户对应用户是否还存在
		isUserExist(user_id);
		// 3.创建用户实体对象封装实体参数
		Sys_user sys_user = new Sys_user();
		sys_user.setUser_name(user_name);
		sys_user.setUser_email(user_email);
		sys_user.setUser_remark(user_remark);
		sys_user.setUser_password(user_password);
		sys_user.setUser_id(user_id);
		sys_user.setUpdate_time(DateUtil.getSysDate());
		sys_user.setUpdate_time(DateUtil.getSysTime());
		// 4.检查系统用户字段合法性
		checkFieldsForSysUser(sys_user);
		// 5.更新接口用户信息
		sys_user.update(Dbo.db());
	}

	@Method(desc = "判断当前登录用户对应的用户是否还存在",
			logicStep = "1.数据可访问权限处理方式：该方法不需要进行权限限制" +
					"2.判断当前登录用户对应用户信息已不存在")
	@Param(name = "user_id", desc = "用户表主键ID", range = "新增用户时生成")
	private void isUserExist(long user_id) {
		// 1.数据可访问权限处理方式：该方法不需要进行权限限制
		// 2.判断当前登录用户对应用户信息已不存在
		if (Dbo.queryNumber("select count(*) from " + Sys_user.TableName + " where user_id=?",
				user_id).orElseThrow(() -> new BusinessException("sql查询错误")) != 1) {
			throw new BusinessException("当前登录用户对应用户信息已不存在，请检查");
		}
	}

	@Method(desc = "删除接口用户", logicStep = "1.数据可访问权限处理方式：该方法不需要进行权限限制" +
			" 2.删除接口用户信息2.删除接口用户信息" +
			"3.删除该用户下的所有接口使用信息" +
			"4.删除该用户下的所有接口表使用信息" +
			"5.删除该用户下的所有接口系统登记参数信息")
	@Param(name = "user_id", desc = "用户表主键ID", range = "新增用户时生成")
	public void deleteUser(long user_id) {
		// 1.数据可访问权限处理方式：该方法不需要进行权限限制
		// 2.删除接口用户信息2.删除接口用户信息
		DboExecute.deletesOrThrow("必须有一条数据被删除,", "delete from " + Sys_user.TableName
				+ " where user_id=?", user_id);
		// 3.删除该用户下的所有接口使用信息
		Dbo.execute("delete from " + Interface_use.TableName + " where user_id = ?", user_id);
		// 4.删除该用户下的所有接口表使用信息
		Dbo.execute("delete from " + Table_use_info.TableName + " where user_id = ?", user_id);
		// 5.删除该用户下的所有接口系统登记参数信息
		Dbo.execute("delete from " + Sysreg_parameter_info.TableName + " where user_id = ?", user_id);
	}

	@Method(desc = "根据用户ID查询用户信息", logicStep = "1.数据可访问权限处理方式：该方法通过不需要进行权限限制" +
			"2.根据用户ID查询用户信息")
	@Param(name = "user_id", desc = "用户表主键ID", range = "新增用户时生成")
	@Return(desc = "返回根据用户ID查询用户信息", range = "无限制")
	public Map<String, Object> selectUserById(long user_id) {
		// 1.数据可访问权限处理方式：该方法通过不需要进行权限限制
		// 2.根据用户ID查询用户信息
		return Dbo.queryOneObject("select user_id,user_name,user_email,user_remark,user_password from "
				+ Sys_user.TableName + " where user_id=?", user_id);
	}
}
