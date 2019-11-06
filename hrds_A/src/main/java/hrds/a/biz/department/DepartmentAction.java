package hrds.a.biz.department;

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
import hrds.commons.entity.Department_info;
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "部门管理", author = "BY-HLL", createdate = "2019/10/18 0018 下午 03:02")
public class DepartmentAction extends BaseAction {

	@Method(desc = "新增部门",
			logicStep = "1.设置部门属性信息(非页面传入的值)" +
					"2.新增部门信息")
	@Param(name = "departmentInfo", desc = "Department_info的实体",
			range = "Department_info的实体,其中dep_name不能为空", isBean = true)
	public void addDepartmentInfo(Department_info departmentInfo) {
		//数据权限校验：不做权限检查
		//1.设置部门属性信息(非页面传入的值)
		if (checkDepNameIsRepeat(departmentInfo.getDep_name())) {
			throw new BusinessException("部门名称重复!" + departmentInfo.getDep_name());
		}
		departmentInfo.setDep_id(PrimayKeyGener.getNextId());
		departmentInfo.setCreate_date(DateUtil.getSysDate());
		departmentInfo.setCreate_time(DateUtil.getSysTime());
		//2.新增部门信息
		departmentInfo.add(Dbo.db());
	}

	@Method(desc = "删除部门", logicStep = "1.检查待删除的部门是否存在，存在则根据 dep_id 删除")
	@Param(name = "dep_id", desc = "部门id", range = "long类型，长度为10，此id唯一", example = "1000000001")
	public void deleteDepartmentInfo(long dep_id) {
		//数据权限校验：不做权限检查
		//1.检查待删除的部门是否存在，存在则根据 dep_id 删除
		if (checkExistDataUnderTheDep(dep_id)) {
			throw new BusinessException("部门下还存在用户!");
		}
		if (checkDepIdIsExist(dep_id)) {
			DboExecute.deletesOrThrow("删除部门失败!" + dep_id,
					"DELETE FROM " + Department_info.TableName + " WHERE dep_id = ? ", dep_id);
		}
	}

	@Method(desc = "修改部门信息", logicStep = "1.校验数据合法性")
	@Param(name = "departmentInfo", desc = "Department_info的实体", range = "Department_info的实体不能为null",
			example = "departmentInfo", isBean = true)
	public void updateDepartmentInfo(Department_info departmentInfo) {
		//数据权限校验：不做权限检查
		//1.校验数据合法性
		if (!checkDepIdIsExist(departmentInfo.getDep_id())) {
			throw new BusinessException("修改的部门id不存在!");
		}
		if (StringUtil.isBlank(departmentInfo.getDep_name())) {
			throw new BusinessException("部门名称不能为空!" + departmentInfo.getDep_name());
		}
		if (checkDepNameIsRepeat(departmentInfo.getDep_name())) {
			throw new BusinessException("部门名称重复!" + departmentInfo.getDep_name());
		}
		departmentInfo.update(Dbo.db());
	}

	@Method(desc = "获取所有部门信息", logicStep = "获取所有部门信息")
	@Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", nullable = true, valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", nullable = true,
			valueIfNull = "10")
	@Return(desc = "所有部门信息的List集合", range = "List集合")
	public Map<String, Object> getDepartmentInfo(int currPage, int pageSize) {
		Map<String, Object> departmentInfoMap = new HashMap<>();
		//数据权限校验：不做权限检查
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Department_info> departmentInfos = Dbo.queryPagedList(Department_info.class, page,
				"select * from " + Department_info.TableName);
		departmentInfoMap.put("departmentInfos", departmentInfos);
		departmentInfoMap.put("totalSize", page.getTotalSize());
		return departmentInfoMap;
	}

	@Method(desc = "根据部门id检查部门是否已经存在",
			logicStep = "1.根据 dep_id 检查部门是否存在(1 : 表示存在, 其他为异常情况,因为根据主键只能查出一条记录信息)")
	@Param(name = "dep_id", desc = "用户登录id", range = "Integer类型，长度为10，此id唯一", example = "1000000001")
	@Return(desc = "部门是否已经存在", range = "true 或者 false")
	public boolean checkDepIdIsExist(long dep_id) {
		//数据权限校验：不做权限检查
		//1.根据 dep_id 检查部门是否存在(1 : 表示存在, 其他为异常情况,因为根据主键只能查出一条记录信息)
		return Dbo.queryNumber("SELECT COUNT(dep_id) FROM " + Department_info.TableName + " WHERE dep_id = ?", dep_id)
				.orElseThrow(() -> new BusinessException("检查部门否存在的SQL编写错误")) == 1;
	}

	@Method(desc = "检查部门名称是否重复",
			logicStep = "1.根据 dep_name 检查部门名称是否重复")
	@Param(name = "dep_name", desc = "部门名称", range = "String类型，长度为512，该值唯一", example = "业务部门")
	@Return(desc = "部门名称是否重复", range = "true：重复，false：不重复")
	private boolean checkDepNameIsRepeat(String dep_name) {
		//1.根据 dep_name 检查部门名称是否重复
		return Dbo.queryNumber("select count(dep_name) count from " + Department_info.TableName +
						" WHERE dep_name =?",
				dep_name).orElseThrow(() -> new BusinessException("检查部门名称否重复的SQL编写错误")) != 0;
	}

	@Method(desc = "检查部门下是否存在用户",
			logicStep = "1.根据 dep_id 检查部门下是否存在用户")
	@Param(name = "dep_id", desc = "部门id", range = "long类型，长度限制19，该值唯一", example = "5000000000")
	@Return(desc = "部门下是否存在用户", range = "true：有，false：没有")
	private boolean checkExistDataUnderTheDep(long dep_id) {
		//1.根据 data_mart_id 检查集市下是否存在数据表
		return Dbo.queryNumber("select count(dep_id) count from " + Sys_user.TableName + " WHERE " +
						"dep_id =?",
				dep_id).orElseThrow(() -> new BusinessException("检查部门下是否存在用户的SQL编写错误")) > 0;
	}

}
