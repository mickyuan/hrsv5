package hrds.a.biz.codemaintenance;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.Validator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Hyren_code_info;
import hrds.commons.entity.Orig_code_info;
import hrds.commons.entity.Orig_syso_info;
import hrds.commons.entity.fdentity.ProjectTableEntity;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.List;
import java.util.Map;

@DocClass(desc = "码值维护类", author = "dhw", createdate = "2020/7/14 16:58")
public class CodeMaintenanceAction extends BaseAction {

	@Method(desc = "查询统一编码信息", logicStep = "1.查询统一编码信息")
	@Return(desc = "返回统一编码信息", range = "无限制")
	public Result getCodeInfo() {
		// 数据可访问权限处理方式：该方法没有访问权限限制
		// 1.查询统一编码信息
		return Dbo.queryResult(
				"select * from " + Hyren_code_info.TableName + " where code_classify in( select " +
						"code_classify from "
						+ Hyren_code_info.TableName + " GROUP BY code_classify)");
	}

	@Method(desc = "新增统一编码信息", logicStep = "1.校验实体字段合法性" +
			"2.判断当前编码分类或者对应的编码类型值是否已存在" +
			"3.新增统一编码信息")
	@Param(name = "hyren_code_infos", desc = "编码信息表实体对象数组", range = "与数据库表对应规则一致", isBean = true)
	public void saveCodeInfo(Hyren_code_info[] hyren_code_infos) {
		// 数据可访问权限处理方式：该方法没有访问权限限制
		for (Hyren_code_info hyren_code_info : hyren_code_infos) {
			// 1.校验实体字段合法性
			checkHyrenCodeInfoFields(hyren_code_info);
			// 2.判断当前编码分类或者对应的编码类型值是否已存在
			if (Dbo.queryNumber(
					"select count(*) from " + Hyren_code_info.TableName
							+ " where code_classify= ? and code_value=?",
					hyren_code_info.getCode_classify(), hyren_code_info.getCode_value())
					.orElseThrow(() -> new BusinessException("sql查询错误")) > 0) {
				throw new BusinessException("当前编码分类已存在或者对应的编码类型值已存在，不能新增");
			}
			// 3.新增统一编码信息
			hyren_code_info.add(Dbo.db());
		}
	}

	@Method(desc = "校验编码信息表实体字段合法性", logicStep = "1.校验编码信息表实体字段合法性")
	@Param(name = "hyren_code_info", desc = "编码信息表实体对象", range = "与数据库表对应规则一致")
	private void checkHyrenCodeInfoFields(Hyren_code_info hyren_code_info) {
		// 1.校验编码信息表实体字段合法性
		Validator.notBlank(hyren_code_info.getCode_classify(), "编码分类不能为空");
		Validator.notBlank(hyren_code_info.getCode_classify_name(), "编码分类名称不能为空");
		Validator.notBlank(hyren_code_info.getCode_type_name(), "编码名称不能为空");
		Validator.notBlank(hyren_code_info.getCode_value(), "编码类型值不能为空");
	}

	@Method(desc = "更新统一编码信息", logicStep = "1.校验实体字段合法性" +
			"2.更新统一编码信息")
	@Param(name = "hyren_code_infos", desc = "编码信息表实体对象数组", range = "与数据库表对应规则一致", isBean = true)
	public void updateCodeInfo(Hyren_code_info[] hyren_code_infos) {
		// 数据可访问权限处理方式：该方法没有访问权限限制
		for (Hyren_code_info hyren_code_info : hyren_code_infos) {
			// 1.校验编码信息表实体字段合法性
			checkHyrenCodeInfoFields(hyren_code_info);
			try {
				// 2.更新统一编码信息
				hyren_code_info.update(Dbo.db());
			} catch (Exception e) {
				if (!(e instanceof ProjectTableEntity.EntityDealZeroException)) {
					throw new BusinessException(e.getMessage());
				}
			}
		}

	}

	@Method(desc = "删除统一编码信息", logicStep = "1.判断当前分类是否被使用" +
			"2.删除编码信息表数据")
	@Param(name = "code_classify", desc = "编码分类", range = "无限制")
	public void deleteCodeInfo(String code_classify) {
		// 数据可访问权限处理方式：该方法没有访问权限限制
		// 1.判断当前分类是否被使用
		if (Dbo.queryNumber("select count(*) from " + Orig_code_info.TableName + " where code_classify = ?",
				code_classify)
				.orElseThrow(() -> new BusinessException("sql查询错误")) > 0) {
			throw new BusinessException("当前编码分类正在被使用，不能删除！");
		}
		// 2.删除编码信息表数据
		Dbo.execute("delete from " + Hyren_code_info.TableName + " where code_classify=?"
				, code_classify);
	}

	@Method(desc = "查询源系统信息", logicStep = "1.查询源系统信息")
	@Return(desc = "返回源系统信息", range = "无限制")
	public Result getOrigSysInfo() {
		// 数据可访问权限处理方式：该方法没有访问权限限制
		// 1.查询源系统信息
		return Dbo.queryResult("select * from " + Orig_syso_info.TableName);
	}

	@Method(desc = "新增源系统信息", logicStep = "1.校验源系统信息表字段合法性验证" +
			"2.检查源系统信息是否存在" +
			"3.新增源系统信息")
	@Param(name = "orig_syso_info", desc = "源系统信息表实体对象", range = "与数据库对应规则一致", isBean = true)
	public void addOrigSysInfo(Orig_syso_info orig_syso_info) {
		// 1.校验源系统信息表字段合法性验证
		Validator.notBlank(orig_syso_info.getOrig_sys_code(), "码值系统编码不能为空");
		Validator.notBlank(orig_syso_info.getOrig_sys_name(), "码值系统名称不能为空");
		// 2.检查源系统信息是否存在
		if (Dbo.queryNumber(
				"select count(*) from " + Orig_syso_info.TableName + " where orig_sys_code=?",
				orig_syso_info.getOrig_sys_code())
				.orElseThrow(() -> new BusinessException("sql查询错误")) > 0) {
			throw new BusinessException("源系统编号已存在，不能新增");
		}
		// 3.新增源系统信息
		orig_syso_info.add(Dbo.db());
	}

	@Method(desc = "查询初始化源系统编码信息", logicStep = "1.检查源系统信息是否存在" +
			"2.查询初始化源系统编码信息并返回")
	@Param(name = "orig_sys_code", desc = "码值系统编码", range = "无限制")
	@Return(desc = "返回源系统编码信息", range = "无限制")
	public Result getOrigCodeInfo(String orig_sys_code) {
		// 数据可访问权限处理方式：该方法没有访问权限限制
		// 1.检查源系统信息是否存在
		isOrigSysoInfoExist(orig_sys_code);
		// 2.查询初始化源系统编码信息并返回
		return Dbo.queryResult(
				"select t1.*,t2.code_classify_name,t2.code_type_name from "
						+ Orig_code_info.TableName + " t1," + Hyren_code_info.TableName + " t2"
						+ " where t1.code_classify=t2.code_classify AND t1.code_value=t2.code_value"
						+ " AND t1.orig_sys_code=? AND t2.code_classify in ("
						+ " select code_classify from " + Hyren_code_info.TableName + " GROUP BY code_classify)",
				orig_sys_code);

	}

	@Method(desc = "根据分类编码查询统一编码信息", logicStep = "1.查询新增源系统编码信息")
	@Param(name = "code_classify", desc = "编码分类", range = "无限制")
	@Return(desc = "返回源系统编码信息", range = "无限制")
	public List<Map<String, Object>> getCodeInfoByCodeClassify(String code_classify) {
		// 数据可访问权限处理方式：该方法没有访问权限限制
		// 1.新增查询源系统编码信息
		return Dbo.queryList(
				"select * from " + Hyren_code_info.TableName + " where code_classify=?",
				code_classify);
	}


	@Method(desc = "新增源系统编码信息", logicStep = "1.检查源系统信息是否存在" +
			"2.检查源系统编码实体字段合法性" +
			"3.检查当前编码分类对应的源系统编码信息是否存在,存在就不添加，不存在则添加" +
			"4.新增源系统编码信息")
	@Param(name = "orig_code_infos", desc = "源系统编码信息表实体数组", range = "与数据库对应表规则一致", isBean = true)
	@Param(name = "orig_sys_code", desc = "码值系统编码", range = "无限制")
	public void addOrigCodeInfo(Orig_code_info[] orig_code_infos, String orig_sys_code) {
		// 数据可访问权限处理方式：该方法没有访问权限限制
		// 1.检查源系统信息是否存在
		isOrigSysoInfoExist(orig_sys_code);
		for (Orig_code_info orig_code_info : orig_code_infos) {
			// 2.检查源系统编码实体字段合法性
			checkOrigCodeInfoFields(orig_code_info);
			// 3.检查当前编码分类对应的源系统编码信息是否存在,存在就不添加，不存在则添加
			if (Dbo.queryNumber(
					"select count(*) from " + Orig_code_info.TableName + " where code_classify=?",
					orig_code_info.getCode_classify())
					.orElseThrow(() -> new BusinessException("sql查询错误")) == 0) {
				orig_code_info.setOrig_sys_code(orig_sys_code);
				orig_code_info.setOrig_id(PrimayKeyGener.getNextId());
				// 4.新增源系统编码信息
				orig_code_info.add(Dbo.db());
			}
		}
	}

	@Method(desc = "检查源系统信息是否存在", logicStep = "1.检查源系统信息是否存在")
	@Param(name = "orig_sys_code", desc = "码值系统编码", range = "无限制")
	private void isOrigSysoInfoExist(String orig_sys_code) {
		// 1.检查源系统信息是否存在
		if (Dbo.queryNumber(
				"select count(*) from " + Orig_syso_info.TableName + " where orig_sys_code=?",
				orig_sys_code)
				.orElseThrow(() -> new BusinessException("sql查询错误")) == 0) {
			throw new BusinessException(orig_sys_code + "对应的源系统信息已不存在，请检查");
		}
	}

	@Method(desc = "检查源系统编码实体字段合法性", logicStep = "1.检查源系统编码实体字段合法性" +
			"2.检查当前编码分类对应的源系统编码信息是否存在")
	@Param(name = "orig_code_info", desc = "源系统编码信息表实体", range = "与数据库对应表规则一致")
	private void checkOrigCodeInfoFields(Orig_code_info orig_code_info) {
		// 1.检查源系统编码实体字段合法性
		Validator.notBlank(orig_code_info.getCode_classify(), "编码分类不能为空");
		Validator.notBlank(orig_code_info.getCode_value(), "编码类型值不能为空");
		Validator.notBlank(orig_code_info.getOrig_value(), "源系统编码值不能为空");
		isHyrenCodeInfoExist(orig_code_info.getCode_classify());
	}

	@Method(desc = "检查统一编码信息是否存在", logicStep = "")
	@Param(name = "code_classify", desc = "编码分类", range = "无限制")
	private void isHyrenCodeInfoExist(String code_classify) {
		// 1.检查统一编码信息是否存在
		if (Dbo.queryNumber(
				"select count(*) from " + Hyren_code_info.TableName + " where code_classify=?",
				code_classify)
				.orElseThrow(() -> new BusinessException("sql查询错误")) == 0) {
			throw new BusinessException(code_classify + "对应的统一编码信息已不存在，请检查");
		}
	}

	@Method(desc = "更新源系统编码信息", logicStep = "1.检查源系统信息是否存在" +
			"2.检查源系统编码实体字段合法性" +
			"3.检查统一编码信息是否存在" +
			"4.更新源系统编码信息")
	@Param(name = "orig_code_infos", desc = "源系统编码信息表实体数组", range = "与数据库对应表规则一致", isBean = true)
	@Param(name = "orig_sys_code", desc = "码值系统编码", range = "无限制")
	public void updateOrigCodeInfo(Orig_code_info[] orig_code_infos, String orig_sys_code) {
		// 数据可访问权限处理方式：该方法没有访问权限限制
		// 1.检查源系统信息是否存在
		isOrigSysoInfoExist(orig_sys_code);
		for (Orig_code_info orig_code_info : orig_code_infos) {
			// 2.检查源系统编码实体字段合法性
			Validator.notNull(orig_code_info.getOrig_id(), "更新源系统编码信息时源系统编码主键不能为空");
			Validator.notBlank(orig_code_info.getCode_classify(), "编码分类不能为空");
			Validator.notBlank(orig_code_info.getOrig_value(), "源系统编码值不能为空");
			// 3.检查统一编码信息是否存在
			isHyrenCodeInfoExist(orig_code_info.getCode_classify());
			orig_code_info.setOrig_sys_code(orig_sys_code);
			// 4.更新源系统编码信息
			orig_code_info.update(Dbo.db());
		}
	}

	@Method(desc = "删除源系统编码信息", logicStep = "1.检查源系统信息是否存在" +
			"2.检查统一编码信息是否存在" +
			"3.删除源系统编码信息")
	@Param(name = "orig_sys_code", desc = "码值系统编码", range = "无限制")
	@Param(name = "code_classify", desc = "编码分类", range = "无限制")
	public void deleteOrigCodeInfo(String orig_sys_code, String code_classify) {
		// 数据可访问权限处理方式：该方法没有访问权限限制
		// 1.检查源系统信息是否存在
		isOrigSysoInfoExist(orig_sys_code);
		// 2.检查统一编码信息是否存在
		isHyrenCodeInfoExist(code_classify);
		// 3.删除源系统编码信息
		Dbo.execute(
				"delete from " + Orig_code_info.TableName + " where code_classify = ? and orig_sys_code=?"
				, code_classify, orig_sys_code);
	}

	@Method(desc = "查询所有统一编码分类", logicStep = "1.查询所有统一编码分类")
	@Return(desc = "返回所有统一编码分类", range = "无限制")
	public List<String> getAllCodeClassify() {
		// 1.查询所有统一编码分类
		return Dbo.queryOneColumnList(
				"select code_classify from " + Hyren_code_info.TableName + " group by code_classify");
	}

	@Method(desc = "根据码值系统编码与编码分类获取源系统编码信息", logicStep = "1.检查源系统信息是否存在" +
			"2.检查统一编码信息是否存在" +
			"3.3.根据码值系统编码与编码分类获取源系统编码信息")
	@Param(name = "orig_sys_code", desc = "码值系统编码", range = "无限制")
	@Param(name = "code_classify", desc = "编码分类", range = "无限制")
	@Return(desc = "返回源系统编码信息", range = "无限制")
	public Result getOrigCodeInfoByCode(String orig_sys_code, String code_classify) {
		// 1.检查源系统信息是否存在
		isOrigSysoInfoExist(orig_sys_code);
		// 2.检查统一编码信息是否存在
		isHyrenCodeInfoExist(code_classify);
		// 3.根据码值系统编码与编码分类获取源系统编码信息
		return Dbo.queryResult(
				"select t1.*,t2.code_classify_name,t2.code_type_name from "
						+ Orig_code_info.TableName + " t1," + Hyren_code_info.TableName + " t2"
						+ " where t1.code_classify=t2.code_classify AND t1.code_value=t2.code_value"
						+ " AND t1.orig_sys_code=? AND t2.code_classify =?",
				orig_sys_code, code_classify);
	}
}
