package hrds.a.biz.syspara;

import fd.ng.core.utils.StringUtil;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.annotation.RequestParam;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Sys_para;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.List;

/**
 * <p>标    题: 海云数服 V5.0</p>
 * <p>描    述: 系统参数</p>
 * <p>版    权: Copyright (c) 2019</p>
 * <p>公    司: 博彦科技(上海)有限公司</p>
 * <p>@author : Mr.Lee</p>
 * <p>创建时间 : 2019-09-19 09:57</p>
 * <p>version: JDK 1.8</p>
 */
public class SysParaAction extends BaseAction {

	/**
	 * 根据用户的模糊查询获取系统参数信息
	 * 1 : 根据输入的系统参数名称进行模糊查询
	 * 2 : 没有输入的系统参数名称
	 *
	 * @param paraName 含义 : 系统参数名称
	 *                 取值范围 : 可以为空,这个参数是搜索的才会有
	 * @return List<Sys_para>
	 * 含义 : 返回系统参数的集合
	 * 取值范围 : 允许为空,为空表示没有系统参数信息
	 */
	public List<Sys_para> getSysPara(@RequestParam(valueIfNull = "") String paraName) {

		//1 : 根据输入的系统参数名称进行模糊查询
		if( !StringUtil.isBlank(paraName) ) {
			return Dbo.queryList(Sys_para.class, "SELECT * FROM " + Sys_para.TableName
							+ " WHERE para_name like '%?%'", paraName);
		}
		else {
			//2 : 没有输入的系统参数名称
			return Dbo.queryList(Sys_para.class, "SELECT * FROM " + Sys_para.TableName);
		}
	}

	/**
	 * 删除系统参数
	 * 1 : 先检查当前要删除的参数信息是否存在
	 * 2 : 删除参数信息
	 * 3 : 返回新的系统参数信息
	 *
	 * @param para_id   含义 : 系统参数ID
	 *                  取值范围 : 不能为空,这个是主键
	 * @param para_name 含义 : 系统参数名称
	 *                  取值范围 : 不能为空,系统参数的名称
	 * @return List<Sys_para>
	 * 含义 : 系统参数的集合
	 * 取值范围 : 允许为空,为空表示没有系统参数信息
	 */
	public List<Sys_para> deleteSysPara(long para_id, String para_name) {

		//1 : 先检查当前要删除的参数信息是否存在
		checkSysParaIsExist(para_id, para_name);

		//2 : 删除参数信息
		Dbo.execute("DELETE FROM " + Sys_para.TableName + " WHERE para_id = ? AND para_name = ?",
						para_id, para_name);

		//3 : 返回新的系统参数信息
		return getSysPara("");
	}

	/**
	 * 编辑系统参数
	 * 1 : 先检查当前要删除的参数信息是否存在
	 * 2 : 更新参数信息
	 * 3 : 返回新的系统参数信息
	 *
	 * @param sys_para 含义 : 系统参数信息实体类信息
	 *                 取值范围 : 不能为空
	 * @return List<Sys_para>
	 * 含义 : 系统参数的集合
	 * 取值范围 : 允许为空,为空表示没有系统参数信息
	 */
	public List<Sys_para> editorSysPara(@RequestBean Sys_para sys_para) {

		//1 : 先检查当前要删除的参数信息是否存在
		checkSysParaIsExist(sys_para.getPara_id(), sys_para.getPara_name());

		//2 : 更新参数信息
		int update = sys_para.update(Dbo.db());
		if( update != 1 ) {
			throw new BusinessException(String.format("系统参数 %s 更新失败", sys_para.getPara_name()));
		}

		//3 : 返回新的系统参数信息
		return getSysPara("");
	}

	/**
	 * 新增系统参数
	 * 1 : 首先生成系统参数的主键
	 * 2 : 然后新增系统参数信息
	 * 3 : 返回新的系统参数信息
	 *
	 * @param sys_para 含义 : 系统参数信息实体类信息
	 *                 取值范围 : 不能为空
	 * @return List<Sys_para>
	 * 含义 : 系统参数的集合
	 * 取值范围 : 允许为空,为空表示没有系统参数信息
	 */
	public List<Sys_para> addSysPara(@RequestBean Sys_para sys_para) {

		//1 : 首先生成系统参数的主键
		sys_para.setPara_id(PrimayKeyGener.getNextId());

		//2 : 然后新增系统参数信息
		int add = sys_para.add(Dbo.db());
		if( add != 1 ) {
			throw new BusinessException(String.format("系统参数 %s 新增失败", sys_para.getPara_name()));
		}

		//3 : 返回新的系统参数信息
		return getSysPara("");
	}

	/**
	 * 检查当前的系统参数信息是否存在
	 * 1 : 根据系统参数的主键和名称查询记录信息是否存在(1 : 表示存在, 其他为异常情况,因为根据主键只能查出一条记录信息)
	 * 2 : 判断查询的记录信息是否符合
	 *
	 * @param para_id   含义 : 系统参数ID
	 *                  取值范围 : 不能为空,这个是主键
	 * @param para_name 含义 : 系统参数名称
	 *                  取值范围 : 不能为空,系统参数的名称
	 */
	private void checkSysParaIsExist(long para_id, String para_name) {

		//1 : 根据系统参数的主键和名称查询记录信息是否存在(1 : 表示存在, 其他为异常情况,因为根据主键只能查出一条记录信息)
		long queryNum = Dbo.queryNumber("SELECT count(1) FROM " + Sys_para.TableName
						+ " WHERE para_id = ? AND para_name = ?", para_id, para_name).
						orElseThrow(() -> new BusinessException(
										String.format("未找到系统参数名称为 :  %s 信息", para_name)));

		//2 : 判断查询的记录信息是否符合
		if( queryNum != 1 ) {
			throw new BusinessException(
							String.format("未找到系统参数名称为 : %s 信息", para_name));
		}
	}
}
