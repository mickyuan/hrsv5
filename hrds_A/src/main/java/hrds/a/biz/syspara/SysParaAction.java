package hrds.a.biz.syspara;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Sys_para;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@DocClass(desc = "系统参数", author = "Mr.Lee")
public class SysParaAction extends BaseAction {

	private static final SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();

	@Method(desc = "模糊查询获取系统参数信息",
			logicStep = "分页获取系统参数信息,查询参数名称为空则查询所有参数")
	@Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", valueIfNull = "10")
	@Param(name = "paraName", desc = "系统参数名称", range = "无限制,参数可为空,为空则查询所有参数信息", nullable = true)
	@Return(desc = "返回系统参数的集合信息", range = "不为null")
	public Map<String, Object> getSysPara(int currPage, int pageSize, String paraName) {
		Map<String, Object> sysParaMap = new HashMap<>();
		//数据权限校验：不做权限检查
		Page page = new DefaultPageImpl(currPage, pageSize);
		asmSql.clean();
		asmSql.addSql("SELECT * FROM " + Sys_para.TableName);
		if (!StringUtil.isBlank(paraName)) {
			asmSql.addSql(" where para_name like ?");
			asmSql.addParam("%" + paraName + "%");
		}
		List<Sys_para> sysParas = Dbo.queryPagedList(Sys_para.class, page, asmSql.sql(), asmSql.params());
		sysParaMap.put("sysParas", sysParas);
		sysParaMap.put("totalSize", page.getTotalSize());
		return sysParaMap;
	}

	@Method(desc = "删除系统参数",
			logicStep = "1 : 先检查当前要删除的参数信息是否存在" +
					"2:删除参数信息")
	@Param(name = "para_id", desc = "系统参数的主键ID", range = "不为空的整数")
	@Param(name = "para_name", desc = "系统参数的名称", range = "不为空的字符串")
	public void deleteSysPara(long para_id, String para_name) {
		//1 : 先检查当前要删除的参数信息是否存在
		checkSysParaIsExist(para_id, para_name);
		//2 : 删除参数信息
		DboExecute.deletesOrThrow("删除系统参数失败！ para_name=" + para_name,
				"DELETE FROM " + Sys_para.TableName + " WHERE para_id = ? AND para_name = ?",
				para_id, para_name);
	}

	@Method(desc = "修改系统参数",
			logicStep = "1 : 先检查当前要删除的参数信息是否存在" +
					"2 : 更新参数信息")
	@Param(name = "sys_para", desc = "sys_para参数信息的实体类", range = "sys_para参数信息的实体类", isBean = true)
	public void updateSysPara(Sys_para sys_para) {
		//1 : 先检查当前要删除的参数信息是否存在
		checkSysParaIsExist(sys_para.getPara_id(), sys_para.getPara_name());
		//2 : 更新参数信息
		sys_para.update(Dbo.db());
	}

	@Method(desc = "新增系统参数",
			logicStep = "1.检查参数合法性" +
					"1-1 : 新增时检查,系统参数名称是否已经存在" +
					"1-2.校验参数传入参数" +
					"2 : 首先生成系统参数的主键" +
					"3 : 然后新增系统参数信息" +
					"4 : 返回新的系统参数信息")
	@Param(name = "sys_para", desc = "sys_para参数信息的实体类容", range = "sys_para参数信息的实体类容", isBean = true)
	public void addSysPara(Sys_para sys_para) {
		//1 : 检查参数合法性
		//1-1 : 新增时检查,系统参数名称是否已经存在
		if (Dbo.queryNumber("SELECT COUNT(para_name) FROM " + Sys_para.TableName + " WHERE para_name = ?",
				sys_para.getPara_name()).orElseThrow(() -> new BusinessException("新增检查参数名称SQL编写错误")
		) != 0) {
			throw new BusinessException(String.format("系统参数名称 %s 已经存在,添加失败", sys_para.getPara_name()));
		}
		//1-2.校验参数传入参数
		if (StringUtil.isBlank(sys_para.getPara_name())) {
			throw new BusinessException("参数名为空!" + sys_para.getPara_name());
		}
		if (StringUtil.isBlank(sys_para.getPara_value())) {
			throw new BusinessException("参数值为空!" + sys_para.getPara_value());
		}
		if (StringUtil.isBlank(sys_para.getPara_type())) {
			throw new BusinessException("参数类型为空！para_name=" + sys_para.getPara_type());
		}
		//2 : 首先生成系统参数的主键
		sys_para.setPara_id(PrimayKeyGener.getNextId());
		//3 : 然后新增系统参数信息
		sys_para.add(Dbo.db());
	}

	@Method(desc = "检查当前的系统参数信息是否存在",
			logicStep = "1 : 根据系统参数的主键和名称查询记录信息是否存在" +
					"(1 : 表示存在, 其他为异常情况,因为根据主键只能查出一条记录信息")
	@Param(name = "para_id", desc = "系统参数主键ID", range = "不能为空的整数,这个是系统参数的主键")
	@Param(name = "para_name", desc = "系统参数名称", range = "不能为空的字符串")
	private void checkSysParaIsExist(long para_id, String para_name) {
		//1:根据系统参数的主键和名称查询记录信息是否存在(1 : 表示存在, 其他为异常情况,因为根据主键只能查出一条记录信息)
		if (Dbo.queryNumber("SELECT COUNT(1) FROM " + Sys_para.TableName
				+ " WHERE para_id = ? AND para_name = ?", para_id, para_name).orElseThrow(
				() -> new BusinessException("检查系统参数是否存在的SQL编写错误")) != 1) {
			throw new BusinessException(String.format("未找到系统参数名称为 : %s 信息", para_name));
		}
	}
}
