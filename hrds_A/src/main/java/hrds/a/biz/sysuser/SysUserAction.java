package hrds.a.biz.sysuser;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.a.biz.department.DepartmentAction;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.UserState;
import hrds.commons.codes.UserType;
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@DocClass(desc = "系统用户管理", author = "BY-HLL", createdate = "2019/10/17 0017")
public class SysUserAction extends BaseAction {

	//role_id未启用，设置不为空给定一个固定的值 1001
	private static final long ROLE_ID = 1001;
	private static final SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();

	@Method(desc = "新增系统用户",
			logicStep = "1.校验输入参数正确性" +
					"2.设置用户属性信息(非页面传入的值)" +
					"3.新增系统用户信息")
	@Param(name = "sysUser", desc = "Sys_user的实体", range = "Sys_user的实体", example = "sysUser", isBean = true)
	@Return(desc = "void 无返回结果", range = "无")
	public void addSysUser(Sys_user sysUser) {
		//数据权限校验：根据登录用户的 user_id 进行权限校验
		//1.校验输入参数正确性
		if (StringUtil.isBlank(sysUser.getDep_id().toString())) {
			throw new BusinessException("部门不能为空，dep_id=" + sysUser.getDep_id());
		}
		DepartmentAction dep = new DepartmentAction();
		if (!dep.checkDepIdIsExist(sysUser.getDep_id())) {
			throw new BusinessException("部门不存在，dep_id=" + sysUser.getDep_id());
		}
		if (StringUtil.isBlank(sysUser.getUseris_admin())) {
			throw new BusinessException("用户类型不能为空，useris_admin=" + sysUser.getUseris_admin());
		}

		//2.设置用户属性信息(非页面传入的值)
		sysUser.setUser_id(PrimayKeyGener.getOperId());
		sysUser.setCreate_id(getUserId());
		sysUser.setRole_id(ROLE_ID);
		sysUser.setUser_state(UserState.ZhengChang.getCode());
		sysUser.setCreate_date(DateUtil.getSysDate());
		sysUser.setCreate_time(DateUtil.getSysTime());
		sysUser.setUpdate_date(DateUtil.getSysDate());
		sysUser.setUpdate_time(DateUtil.getSysTime());
		//2.新增系统用户信息
		sysUser.add(Dbo.db());
	}

	@Method(desc = "修改系统用户信息", logicStep = "1.检查待修改的的系统用户是否存在，存在则修改")
	@Param(name = "sys_user", desc = "Sys_user的实体", range = "取值范围说明", example = "sys_user", isBean = true)
	@Return(desc = "void 无返回结果", range = "无")
	public void updateSysUser(Sys_user sys_user) {
		//数据权限校验：根据登录用户的 user_id 进行权限校验
		//1.检查待修改的的系统用户是否存在，存在则修改
		if (checkSysUserIsExist(sys_user.getUser_id())) {
			throw new BusinessException("修改不存在的用户id，user_id=" + sys_user.getUser_id());
		}
		sys_user.setRole_id(ROLE_ID);
		sys_user.setUpdate_date(DateUtil.getSysDate());
		sys_user.setUpdate_time(DateUtil.getSysTime());
		sys_user.update(Dbo.db());
	}

	@Method(desc = "获取所有系统用户", logicStep = "获取所有系统用户")
	@Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "所有系统用户的List集合", range = "List集合")
	@Deprecated
	public List<Sys_user> getSysUserAll(int currPage, int pageSize) {
		//数据权限校验：根据登录用户的 user_id 进行权限校验
		return Dbo.queryPagedList(Sys_user.class, new DefaultPageImpl(currPage, pageSize),
				"select * from " + Sys_user.TableName + "where create_id=? order by user_id,create_date asc," +
						"create_time asc", getUserId());
	}

	@Method(desc = "获取单个用户信息",
			logicStep = "1.根据用户id查询，返回查询结果")
	@Param(name = "user_id", desc = "用户id", range = "自动生成的id")
	@Return(desc = "单个用户的对象", range = "无限制")
	public Optional<Sys_user> getUserByUserId(long user_id) {
		//1.根据用户id查询，返回查询结果
		if (checkSysUserIsExist(user_id)) {
			throw new BusinessException("查询不存在的用户id，user_id=" + user_id);
		}
		return Dbo.queryOneObject(Sys_user.class, "select * from sys_user where user_id = ?", user_id);
	}

	@Method(desc = "获取所有系统用户列表（不包含超级管理员）",
			logicStep = "1.查询管理员用户")
	@Return(desc = "用户列表", range = "不包含超级管理员的系统用户列表")
	public Result getSysUserInfo() {
		//1.查询管理员用户
		String[] str = new String[]{
				UserType.CaijiGuanLiYuan.getCode(), UserType.ZuoYeGuanLiYuan.getCode(),
				UserType.ShuJuKSHSJY.getCode(), UserType.JianKongGuanLiYuan.getCode(),
				UserType.RESTJieKouGuanLiYuan.getCode(), UserType.FenCiQiGuanLiYuan.getCode(),
				UserType.JiQiXueXiGuanLiYuan.getCode(), UserType.LiuShuJuGuanLiYuan.getCode(),
				UserType.ShuJuKuPeiZhi.getCode(), UserType.CaiJiYongHu.getCode(), UserType.YeWuYongHu.getCode(),
				UserType.ShuJuKSHBianJI.getCode(), UserType.ShuJuKSHChaKan.getCode(),
				UserType.RESTYongHu.getCode(), UserType.JiShiGuanLiYuan.getCode(),
				UserType.JiShiJiaGongGuanLiYuan.getCode(), UserType.LiuShuJuShengChanYongHu.getCode(),
				UserType.BaoBiaoChuanJian.getCode(), UserType.BaoBiaoChaKan.getCode(),
				UserType.LiuShuJuXiaoFeiYongHu.getCode(), UserType.ShuJuGuanKongGuanLiYuan.getCode(),
				UserType.ZiZhuFenXiGuanLi.getCode(), UserType.ZiZhuFenXiCaoZuo.getCode(),
				UserType.ZuoYeCaoZuoYuan.getCode(), UserType.ShuJuKSHGuanLiYuan.getCode(),
				UserType.JiQiXueXiYongHu.getCode()
		};
		asmSql.clean();
		asmSql.addSql("select * from sys_user where create_id=?");
		asmSql.addParam(getUserId());
		asmSql.addORParam("user_type", str);
		asmSql.addSql(" order by user_id,create_date asc,create_time asc");
		return Dbo.queryResult(asmSql.sql(), asmSql.params());
	}

	@Method(desc = "删除系统用户", logicStep = "1.检查待删除的系统用户是否存在，存在则根据 user_id 删除")
	@Param(name = "user_id", desc = "用户登录id", range = "自动生成的id", example = "5000")
	@Return(desc = "void 无返回结果", range = "无")
	public void deleteSysUser(long user_id) {
		//数据权限校验：根据登录用户的 user_id 进行权限校验
		//1.检查待删除的系统用户是否存在，存在则根据 user_id 删除
		if (checkSysUserIsExist(user_id)) {
			throw new BusinessException("删除不存在的用户id，user_id=" + user_id);
		}
		DboExecute.deletesOrThrow("删除系统用户失败!，user_id=" + user_id,
				"DELETE FROM " + Sys_user.TableName + " WHERE user_id = ? ", user_id);
	}

	@Method(desc = "获取用户功能菜单",
			logicStep = "1.管理员功能菜单" +
					"2.操作员功能菜单" +
					"3.返回功能菜单列表")
	@Param(name = "user_is_admin", desc = "用户类别", range = "0：管理员功能菜单，1：操作员功能菜单", example = "1", valueIfNull = "0")
	@Return(desc = "用户功能菜单集合", range = "userFunctionMap：用户功能菜单")
	public Map getUserFunctionMenu(String user_is_admin) {
		Map<String, String> userFunctionMenuMap = new HashMap<>();
		//0：管理员功能菜单
		if (IsFlag.Fou.getCode().equalsIgnoreCase(user_is_admin)) {
			//1.管理员功能菜单
			userFunctionMenuMap.put(UserType.CaijiGuanLiYuan.getCode(), UserType.CaijiGuanLiYuan.getValue());
			userFunctionMenuMap.put(UserType.ZuoYeGuanLiYuan.getCode(), UserType.ZuoYeGuanLiYuan.getValue());
			userFunctionMenuMap.put(UserType.ShuJuKSHSJY.getCode(), UserType.ShuJuKSHSJY.getValue());
			userFunctionMenuMap.put(UserType.JianKongGuanLiYuan.getCode(), UserType.JianKongGuanLiYuan.getValue());
			userFunctionMenuMap.put(UserType.RESTJieKouGuanLiYuan.getCode(), UserType.RESTJieKouGuanLiYuan.getValue());
			userFunctionMenuMap.put(UserType.FenCiQiGuanLiYuan.getCode(), UserType.FenCiQiGuanLiYuan.getValue());
			userFunctionMenuMap.put(UserType.JiQiXueXiGuanLiYuan.getCode(), UserType.JiQiXueXiGuanLiYuan.getValue());
			userFunctionMenuMap.put(UserType.LiuShuJuGuanLiYuan.getCode(), UserType.LiuShuJuGuanLiYuan.getValue());
			userFunctionMenuMap.put(UserType.ShuJuKuPeiZhi.getCode(), UserType.ShuJuKuPeiZhi.getValue());
			userFunctionMenuMap.put(UserType.ZiZhuFenXiGuanLi.getCode(), UserType.ZiZhuFenXiGuanLi.getValue());
		}
		//1：操作员功能菜单
		else if (IsFlag.Shi.getCode().equalsIgnoreCase(user_is_admin)) {
			//2.操作员功能菜单
			userFunctionMenuMap.put(UserType.CaiJiYongHu.getCode(), UserType.CaiJiYongHu.getValue());
			userFunctionMenuMap.put(UserType.YeWuYongHu.getCode(), UserType.YeWuYongHu.getValue());
			userFunctionMenuMap.put(UserType.ZuoYeGuanLiYuan.getCode(), UserType.ZuoYeGuanLiYuan.getValue());
			userFunctionMenuMap.put(UserType.ShuJuKSHBianJI.getCode(), UserType.ShuJuKSHBianJI.getValue());
			userFunctionMenuMap.put(UserType.ShuJuKSHChaKan.getCode(), UserType.ShuJuKSHChaKan.getValue());
			userFunctionMenuMap.put(UserType.RESTYongHu.getCode(), UserType.RESTYongHu.getValue());
			userFunctionMenuMap.put(UserType.JiShiGuanLiYuan.getCode(), UserType.JiShiGuanLiYuan.getValue());
			userFunctionMenuMap.put(UserType.JiShiJiaGongGuanLiYuan.getCode(), UserType.JiShiJiaGongGuanLiYuan.getValue());
			userFunctionMenuMap.put(UserType.LiuShuJuShengChanYongHu.getCode(), UserType.LiuShuJuShengChanYongHu.getValue());
			userFunctionMenuMap.put(UserType.BaoBiaoChuanJian.getCode(), UserType.BaoBiaoChuanJian.getValue());
			userFunctionMenuMap.put(UserType.BaoBiaoChaKan.getCode(), UserType.BaoBiaoChaKan.getValue());
			userFunctionMenuMap.put(UserType.LiuShuJuXiaoFeiYongHu.getCode(), UserType.LiuShuJuXiaoFeiYongHu.getValue());
			userFunctionMenuMap.put(UserType.ShuJuGuanKongGuanLiYuan.getCode(), UserType.ShuJuGuanKongGuanLiYuan.getValue());
			userFunctionMenuMap.put(UserType.ZiZhuFenXiCaoZuo.getCode(), UserType.ZiZhuFenXiCaoZuo.getValue());
		}
		//3.返回功能菜单列表
		return userFunctionMenuMap;
	}

	@Method(desc = "获取部门信息",
			logicStep = "获取部门信息" +
					"如果部门id为空则获取所有部门信息")
	@Param(name = "dep_id", desc = "部门id", range = "Integer类型，长度为10，此id唯一", example = "1000000001")
	@Return(desc = "部门信息的Result", range = "无限制")
	public Result getDepartmentInfo(String dep_id) {
		//获取部门信息
		asmSql.clean();
		asmSql.addSql("select * from department_info");
		//如果部门id为空则获取所有部门信息
		if (!StringUtil.isBlank(dep_id)) {
			asmSql.addSql(" where dep_id=?");
			asmSql.addParam(Long.valueOf(dep_id));
		}
		asmSql.addParam(" order by create_date asc,create_time asc");
		return Dbo.queryResult(asmSql.sql(), asmSql.params());
	}

	@Method(desc = "根据用户id检查用户是否已经存在",
			logicStep = "1.根据user_id检查用户是否存在(查询结果为1:表示存在, 其他为异常情况,因为根据主键只能查出一条记录信息)")
	@Param(name = "user_id", desc = "用户登录id", range = "自动生成的id", example = "5000")
	@Return(desc = "用户id是否已经存在", range = "true 或者 false")
	private boolean checkSysUserIsExist(long user_id) {
		//1.根据user_id检查用户是否存在(查询结果为1:表示存在, 其他为异常情况,因为根据主键只能查出一条记录信息)
		return Dbo.queryNumber("SELECT COUNT(1) FROM " + Sys_user.TableName + " WHERE user_id = ?", user_id)
				.orElseThrow(() -> new BusinessException("检查系统用户否存在的SQL编写错误")) != 1;
	}
}
