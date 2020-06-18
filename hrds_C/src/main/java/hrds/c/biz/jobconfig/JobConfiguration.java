package hrds.c.biz.jobconfig;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.meta.MetaOperator;
import fd.ng.db.meta.TableMeta;
import fd.ng.db.resultset.Result;
import fd.ng.web.annotation.UploadFile;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.FileUploadUtil;
import hrds.c.biz.util.ConvertColumnNameToChinese;
import hrds.c.biz.util.DownloadLogUtil;
import hrds.c.biz.util.ETLJobUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.ExcelUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

@DocClass(desc = "作业调度配置管理", author = "dhw", createdate = "2019/10/28 11:36")
public class JobConfiguration extends BaseAction {

	private static final Logger logger = LogManager.getLogger();

	// 作业系统参数变量名称前缀
	private static final String PREFIX = "!";
	// 作业调度默认系统参数对应的工程编号
	private static final String DefaultEtlSysCd = "HRSYS";
	// 不同版本excel文件后缀名
	private static final String xlsxSuffix = ".xlsx";
	// 数据库存储代码项对应字段名称，下面是要用这个去匹配是使用哪个代码项获取代码项信息
	private static final String pro_type = "pro_type";
	private static final String disp_freq = "disp_freq";
	private static final String disp_type = "disp_type";
	private static final String job_eff_flag = "job_eff_flag";
	private static final String job_disp_status = "job_disp_status";
	private static final String today_disp = "today_disp";
	private static final String main_serv_sync = "main_serv_sync";
	private static final String status = "status";
	private static final String para_type = "para_type";

	@Method(desc = "分页查询作业调度某工程任务信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.获取某个工程下任务信息" +
					"3.判断任务编号是否为空，如果为空则查询所有任务信息，如果不为空则模糊查询任务信息（搜索）" +
					"4.分页查询任务信息，实体字段基本都需要所以查询所有字段" +
					"5.创建存放分页查询任务信息、分页查询总记录数、工程编号、工程名称的集合并封装数据" +
					"6.返回存放分页查询任务信息、分页查询总记录数、工程编号、工程名称的集合")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "sub_sys_cd", desc = "任务编号", range = "新增任务时生成", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "存放分页额查询任务信息、分页查询总记录数、工程编号、工程名称的集合", range = "无限制")
	public Map<String, Object> searchEtlSubSysByPage(String etl_sys_cd, String sub_sys_cd,
	                                                 int currPage, int pageSize) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		// 2.获取某个工程下任务信息,每次拼接新sql之前清空原来的sql以及参数
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("select * from " + Etl_sub_sys_list.TableName + " where etl_sys_cd = ?");
		asmSql.addParam(etl_sys_cd);
		// 3.判断任务编号是否为空，如果为空则查询所有任务信息，如果不为空则模糊查询任务信息（搜索）
		if (StringUtil.isNotBlank(sub_sys_cd)) {
			asmSql.addLikeParam("sub_sys_cd", "%" + sub_sys_cd + "%");
		}
		asmSql.addSql(" order by etl_sys_cd,sub_sys_cd");
		Page page = new DefaultPageImpl(currPage, pageSize);
		// 4.分页查询任务信息，实体字段基本都需要所以查询所有字段
		List<Map<String, Object>> etlSubSysList = Dbo.queryPagedList(page, asmSql.sql(),
				asmSql.params());
		// 5.创建存放分页查询任务信息、分页查询总记录数、工程编号、工程名称的集合并封装数据
		Map<String, Object> etlSubSysMap = new HashMap<>();
		etlSubSysMap.put("etlSubSysList", etlSubSysList);
		etlSubSysMap.put("totalSize", page.getTotalSize());
		// 6.返回存放分页查询任务信息、分页查询总记录数、工程编号、工程名称的集合
		return etlSubSysMap;
	}

	@Method(desc = "查询任务信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.返回任务编号查询任务信息,实体字段基本都需要所以查询所有字段")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Return(desc = "返回查询任务信息", range = "无限制")
	public List<Etl_sub_sys_list> searchEtlSubSys(String etl_sys_cd) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		// 2.返回查询任务信息,实体字段基本都需要所以查询所有字段
		return Dbo.queryList(Etl_sub_sys_list.class, "select * from " + Etl_sub_sys_list.TableName
				+ " where etl_sys_cd=? order by etl_sys_cd, sub_sys_cd", etl_sys_cd);
	}

	@Method(desc = "根据工程编号，任务编号查询任务信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.判断当前工程下的任务是否存在" +
					"3.返回根据工程编号，任务编号查询任务信息,实体字段基本都需要所以查询所有字段")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "sub_sys_cd", desc = "任务编号", range = "新增任务时生成")
	@Return(desc = "返回根据工程编号，任务编号查询任务信息", range = "无限制")
	public Map<String, Object> searchEtlSubSysById(String etl_sys_cd, String sub_sys_cd) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		// 2.判断当前工程下的任务是否存在
		if (!ETLJobUtil.isEtlSubSysExist(etl_sys_cd, sub_sys_cd)) {
			throw new BusinessException("该工程下任务已不存在，可能被删除！");
		}
		// 3.返回根据工程编号，任务编号查询任务信息,实体字段基本都需要所以查询所有字段
		return Dbo.queryOneObject("select  * from "
				+ Etl_sub_sys_list.TableName + " where etl_sys_cd=? and sub_sys_cd=? " +
				"order by etl_sys_cd, sub_sys_cd", etl_sys_cd, sub_sys_cd);
	}

	@Method(desc = "作业调度任务表字段合法性验证",
			logicStep = "1.数据可访问权限处理方式，此方法不需要权限验证，不涉及用户权限控制" +
					"2.验证工程编号合法性" +
					"3.验证任务编号的合法性")
	@Param(name = "etl_sub_sys_list", desc = "作业调度任务表对象", range = "与数据库表字段定义规则一致",
			isBean = true)
	private void checkEtlSubSysField(Etl_sub_sys_list etl_sub_sys_list) {
		// 1.数据可访问权限处理方式，此方法不需要权限验证，不涉及用户权限控制
		// 2.验证工程编号合法性
		Validator.notBlank(etl_sub_sys_list.getEtl_sys_cd(), "工程编号不能为空以及不能为空格");
		// 3.验证任务编号的合法性
		Validator.notBlank(etl_sub_sys_list.getSub_sys_cd(), "任务编号不能为空以及不能为空格");
		// 3.验证任务名称的合法性
		Validator.notBlank(etl_sub_sys_list.getSub_sys_desc(), "任务名称不能为空以及不能为空格");
	}

	@Method(desc = "新增保存任务",
			logicStep = "1.数据可访问权限处理方式，通过user_id关联进行权限控制" +
					"2.字段合法性验证" +
					"3.判断工程对应的任务是否已存在" +
					"4.新增任务")
	@Param(name = "etl_sub_sys_list", desc = "任务实体对象", range = "与数据库表字段规则一致", isBean = true)
	public void saveEtlSubSys(Etl_sub_sys_list etl_sub_sys_list) {
		// 1.数据可访问权限处理方式，通过user_id关联进行权限控制
		// 2.字段合法性验证
		checkEtlSubSysField(etl_sub_sys_list);
		// 3.判断工程对应的任务是否已存在,不存在才添加
		if (ETLJobUtil.isEtlSubSysExist(etl_sub_sys_list.getEtl_sys_cd(), etl_sub_sys_list.getSub_sys_cd())) {
			throw new BusinessException("该工程对应的任务已存在，不能新增！");
		}
		// 4.新增任务
		etl_sub_sys_list.add(Dbo.db());
	}

	@Method(desc = "更新保存任务",
			logicStep = "1.数据可访问权限处理方式，通过user_id关联进行权限控制" +
					"2.字段合法性验证" +
					"3.修改任务信息")
	@Param(name = "etl_sub_sys_list", desc = "任务实体对象", range = "与数据库表字段规则一致", isBean = true)
	public void updateEtlSubSys(Etl_sub_sys_list etl_sub_sys_list) {
		// 1.数据可访问权限处理方式，通过user_id关联进行权限控制
		// 2.字段合法性验证
		checkEtlSubSysField(etl_sub_sys_list);
		// 3.修改任务信息
		etl_sub_sys_list.update(Dbo.db());
	}

	@Method(desc = "批量删除任务信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id关联进行权限控制" +
					"2.遍历所有批量删除任务编号的数组获取各个任务编号" +
					"3.判断该工程对应的任务下是否还有作业" +
					"4.根据工程编号，任务编号循环删除任务信息")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "不为空")
	@Param(name = "sub_sys_cd", desc = "任务编号的数组", range = "不为空")
	public void batchDeleteEtlSubSys(String etl_sys_cd, String[] sub_sys_cd) {
		// 1.数据可访问权限处理方式，通过user_id关联进行权限控制
		// 2.遍历所有批量删除任务编号的数组获取各个任务编号
		for (String subSysCd : sub_sys_cd) {
			// 3.判断该工程对应的任务下是否还有作业
			ETLJobUtil.isEtlJobDefExistUnderEtlSubSys(etl_sys_cd, subSysCd);
			// 4.根据工程编号，任务编号删除任务信息
			DboExecute.deletesOrThrow("删除任务失败，etl_sys_cd=" + etl_sys_cd + ",sub_sys_cd="
					+ subSysCd, "delete from " + Etl_sub_sys_list.TableName + " where etl_sys_cd=? " +
					" and sub_sys_cd=?", etl_sys_cd, subSysCd);
		}
	}

	@Method(desc = "根据工程编号，任务编号删除任务信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id关联进行权限控制" +
					"2.判断该工程对应的任务下是否还有作业" +
					"3.根据工程编号，任务编号删除任务信息")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "sub_sys_cd", desc = "任务编号", range = "新增任务时生成")
	public void deleteEtlSubSys(String etl_sys_cd, String sub_sys_cd) {
		// 1.数据可访问权限处理方式，通过user_id关联进行权限控制
		// 2.判断该工程对应的任务下是否还有作业
		ETLJobUtil.isEtlJobDefExistUnderEtlSubSys(etl_sys_cd, sub_sys_cd);
		// 3.根据工程编号，任务编号删除任务信息
		DboExecute.deletesOrThrow("删除任务失败，etl_sys_cd=" + etl_sys_cd + ",sub_sys_cd="
				+ sub_sys_cd, "delete from " + Etl_sub_sys_list.TableName + " where etl_sys_cd=? " +
				" and sub_sys_cd=?", etl_sys_cd, sub_sys_cd);
	}

	@Method(desc = "获取该工程下素有作业模板信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.返回作业模板相关信息集合")
	@Return(desc = "返回作业模板相关信息集合", range = "无限制")
	public Result searchEtlJobTemplate() {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		// 2.返回作业模板相关信息集合
		return Dbo.queryResult("select * from " + Etl_job_temp.TableName);
	}

	@Method(desc = "通过模板ID获取模板信息",
			logicStep = "1.数据可访问权限处理方式，此方法不需要用户权限控制" +
					"2.根据模板ID查询模板信息")
	@Param(name = "etl_temp_id", desc = "模板作业ID", range = "无限制")
	@Return(desc = "返回根据模板ID查询模板信息", range = "无限制")
	private Map<String, Object> searchEtlJobTemplateById(long etl_temp_id) {
		// 1.数据可访问权限处理方式，此方法不需要用户权限控制
		// 2.根据模板ID查询模板信息
		Map<String, Object> etlJobTemp = Dbo.queryOneObject("select * from " + Etl_job_temp.TableName +
				" where etl_temp_id=?", etl_temp_id);
		if (etlJobTemp.isEmpty()) {
			throw new BusinessException("通过模板ID没有获取到获取模板信息！");
		}
		return etlJobTemp;
	}

	@Method(desc = "关联查询作业模板表和作业模板参数表获取作业模板信息",
			logicStep = "1.数据可访问权限处理方式，此方法不需要用户权限控制" +
					"2.关联查询作业模板表和作业模板参数表获取作业模板信息")
	@Param(name = "etl_temp_id", desc = "作业模板ID", range = "无限制")
	@Return(desc = "返回关联查询作业模板表和作业模板参数表获取作业模板信息", range = "无限制")
	public List<Map<String, Object>> searchEtlJobTempAndParam(long etl_temp_id) {
		// 1.数据可访问权限处理方式，此方法不需要用户权限控制
		// 2.关联查询作业模板表和作业模板参数表获取作业模板信息
		return Dbo.queryList("SELECT * FROM " + Etl_job_temp.TableName + " t1,"
				+ Etl_job_temp_para.TableName + " t2 where t1.etl_temp_id=t2.etl_temp_id " +
				" AND t1.etl_temp_id=? order by etl_pro_para_sort", etl_temp_id);
	}

	@Method(desc = "保存作业模板信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.拼接作业程序参数" +
					"3.封装作业实体" +
					"4.获取作业模板信息封装作业实体对象" +
					"5.判断作业名称是否已存在，存在不能新增" +
					"6.保存模板作业")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "sub_sys_cd", desc = "任务编号", range = "新增任务时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "不能重复，新增作业时生成")
	@Param(name = "etl_temp_id", desc = "作业模板ID", range = "无限制")
	@Param(name = "etl_job_temp_para", desc = "作业模板参数的数组", range = "无限制")
	public void saveEtlJobTemp(String etl_sys_cd, String sub_sys_cd, String etl_job, long etl_temp_id,
	                           String[] etl_job_temp_para) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.拼接作业程序参数
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < etl_job_temp_para.length; i++) {
			String value = etl_job_temp_para[i];
			if (i != etl_job_temp_para.length - 1) {
				sb.append(value).append(Constant.ETLPARASEPARATOR);
			} else {
				sb.append(value);
			}
		}
		// 3.封装作业实体
		Etl_job_def etl_job_def = new Etl_job_def();
		etl_job_def.setEtl_job(etl_job);
		etl_job_def.setEtl_sys_cd(etl_sys_cd);
		etl_job_def.setSub_sys_cd(sub_sys_cd);
		etl_job_def.setPro_para(sb.toString());
		etl_job_def.setPro_type(Pro_Type.SHELL.getCode());
		etl_job_def.setEtl_job_desc(etl_job);
		etl_job_def.setDisp_type(Dispatch_Type.DEPENDENCE.getCode());
		etl_job_def.setJob_eff_flag(Job_Effective_Flag.YES.getCode());
		etl_job_def.setToday_disp(Today_Dispatch_Flag.YES.getCode());
		etl_job_def.setDisp_freq(Dispatch_Frequency.DAILY.getCode());
		etl_job_def.setUpd_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
				+ " " + DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
		etl_job_def.setMain_serv_sync(Main_Server_Sync.YES.getCode());
		// 4.获取作业模板信息封装作业实体对象
		Map<String, Object> jobTemplate = searchEtlJobTemplateById(etl_temp_id);
		if (!jobTemplate.isEmpty()) {
			etl_job_def.setPro_dic(jobTemplate.get("pro_dic").toString());
			etl_job_def.setPro_name(jobTemplate.get("pro_name").toString());
			etl_job_def.setLog_dic(jobTemplate.get("pro_dic").toString());
		}
		// 5.判断作业名称是否已存在，存在不能新增
		if (ETLJobUtil.isEtlJobDefExist(etl_sys_cd, etl_job)) {
			throw new BusinessException("作业名称已存在不能新增!");
		}
		// 6.保存模板作业
		Etl_dependency etlDependency = new Etl_dependency();
		etlDependency.setEtl_sys_cd(etl_sys_cd);
		etlDependency.setPre_etl_sys_cd(etl_sys_cd);
		etlDependency.setEtl_job(etl_job);
		saveEtlJobDef(etl_job_def, etlDependency, new String[]{});
	}

	@Method(desc = "分页查询作业定义信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id关联查询进行权限控制" +
					"2.拼接新sql" +
					"3.判断作业程序类型是否为空，不为空，加条件查询" +
					"4.判断作业名称是否为空，不为空，加条件查询" +
					"5.判断作业程序名称是否为空，不为空，加条件查询" +
					"6.判断任务编号是否为空，不为空，加条件查询" +
					"7.分页查询作业定义信息" +
					"8.创建存放分页查询作业定义信息、分页查询总记录数、工程名称的集合并封装数据" +
					"9.返回分页查询作业定义信息、分页查询总记录数、工程编号、工程名称")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "pro_type", desc = "作业程序类型", range = "使用ProType代码项（ProType）", nullable = true)
	@Param(name = "etl_job", desc = "作业名称", range = "可为空", nullable = true)
	@Param(name = "pro_name", desc = "作业程序名称", range = "可为空", nullable = true)
	@Param(name = "sub_sys_cd", desc = "任务编号", range = "可为空", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "存放分页查询作业定义信息、分页查询总记录数、工程编号、工程名称的集合", range = "无限制")
	public Map<String, Object> searchEtlJobDefByPage(String etl_sys_cd, String pro_type, String etl_job,
	                                                 String pro_name, String sub_sys_cd, int currPage,
	                                                 int pageSize) {
		// 1.数据可访问权限处理方式，通过user_id关联查询进行权限控制
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		// 2.拼接sql
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("select t1.etl_sys_cd,t1.etl_job,t1.etl_job_desc,t1.pro_name,t1.disp_freq," +
				"t1.disp_type,t1.job_eff_flag,t1.upd_time,t1.today_disp,t1.sub_sys_cd,t1.pro_type," +
				"t2.etl_sys_name FROM " + Etl_job_def.TableName + " t1 left join " + Etl_sys.TableName +
				" t2 on t1.etl_sys_cd=t2.etl_sys_cd where t1.etl_sys_cd=? and t2.user_id=?");
		asmSql.addParam(etl_sys_cd);
		asmSql.addParam(getUserId());
		// 3.判断作业程序类型是否为空，不为空，加条件查询
		if (StringUtil.isNotBlank(pro_type)) {
			asmSql.addSql("AND pro_type = ?");
			asmSql.addParam(pro_type);
		}
		// 4.判断作业名称是否为空，不为空，加条件查询
		if (StringUtil.isNotBlank(etl_job)) {
			asmSql.addLikeParam("etl_job", "%" + etl_job + "%");
		}
		// 5.判断作业程序名称是否为空，不为空，加条件查询
		if (StringUtil.isNotBlank(pro_name)) {
			asmSql.addLikeParam("pro_name", "%" + pro_name + "%");
		}
		// 6.判断任务编号是否为空，不为空，加条件查询
		if (StringUtil.isNotBlank(sub_sys_cd)) {
			asmSql.addLikeParam("sub_sys_cd", "%" + sub_sys_cd + "%");
		}
		// 7.分页查询作业定义信息
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> etlJobDefList = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
		// 8.创建存放分页查询作业定义信息、分页查询总记录数、工程编号、工程名称的集合并封装数据
		Map<String, Object> etlJobDefMap = new HashMap<>();
		etlJobDefMap.put("etlJobDefList", etlJobDefList);
		etlJobDefMap.put("totalSize", page.getTotalSize());
		// 9.返回分页查询作业定义信息、分页查询总记录数、工程编号、工程名称
		return etlJobDefMap;
	}

	@Method(desc = "根据工程编号、作业名称查询作业定义信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.判断当前工程下作业是否存在" +
					"3.返回根据工程编号、作业名称查询作业定义信息，实体字段基本都需要所以查询所有字段")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
	@Return(desc = "返回根据工程编号、作业名称查询作业定义信息", range = "取值范围")
	public Map<String, Object> searchEtlJobDefById(String etl_sys_cd, String etl_job) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		// 2.判断当前工程下作业是否存在
		if (!ETLJobUtil.isEtlJobDefExist(etl_sys_cd, etl_job)) {
			throw new BusinessException("当前工程下作业已不存在！");
		}
		// 3.返回根据工程编号、作业名称查询作业定义信息，实体字段基本都需要所以查询所有字段
		Map<String, Object> etlJobDef = ETLJobUtil.getEtlJobByJob(etl_sys_cd, etl_job);
		List<Etl_dependency> dependencyList = Dbo.queryList(Etl_dependency.class, "select pre_etl_sys_cd" +
				",pre_etl_job,status FROM " + Etl_dependency.TableName +
				" WHERE etl_sys_cd=? AND etl_job=?", etl_sys_cd, etl_job);
		etlJobDef.put("dependencyList", dependencyList);
		return etlJobDef;
	}

	@Method(desc = "查询作业名称信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.返回查询作业定义信息，实体字段基本都需要所以查询所有字段")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Return(desc = "返回查询作业名称信息", range = "取值范围")
	public List<String> searchEtlJob(String etl_sys_cd) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		// 2.返回根据工程编号、作业名称查询作业定义信息，实体字段基本都需要所以查询所有字段
		return Dbo.queryOneColumnList("select etl_job from " + Etl_job_def.TableName + " where etl_sys_cd=?" +
				" order by etl_job", etl_sys_cd);
	}

	@Method(desc = "新增保存作业信息",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
					"2.验证作业定义字段合法性" +
					"3.判断作业名称是否已存在，存在，不能新增" +
					"4.判断如果作业程序类型是  Thrift 或者 Yarn.则默认分配一条资源使用信息" +
					"5.如果是依赖作业则保存作业依赖信息,调度频率为频率时不会有调度类型" +
					"5.1判断调度触发方式是否为依赖触发" +
					"5.2判断上游作业名称是否为空，如果不为空判断上游作业名称是否合法" +
					"5.3循环保存作业依赖" +
					"6.保存资源分配信息" +
					"7.如果是依赖作业则保存作业依赖信息")
	@Param(name = "etl_job_def", desc = "作业定义实体对象", range = "与数据库对应表字段规则一致", isBean = true)
	@Param(name = "etl_dependency", desc = "作业依赖实体对象", range = "与数据库对应表字段规则一致", isBean = true)
	@Param(name = "pre_etl_job", desc = "上游作业名称的数组", range = "已存在的作业名称", nullable = true)
	public void saveEtlJobDef(Etl_job_def etl_job_def, Etl_dependency etl_dependency, String[] pre_etl_job) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.验证作业定义字段合法性
		checkEtlJobDefField(etl_job_def);
		// 3.判断作业名称是否已存在，存在，不能新增
		if (ETLJobUtil.isEtlJobDefExist(etl_job_def.getEtl_sys_cd(), etl_job_def.getEtl_job())) {
			throw new BusinessException("作业名称已存在不能新增!");
		}
		// 4.判断如果作业程序类型是  Thrift 或者 Yarn.则默认分配一条资源使用信息
		isThriftOrYarnProType(etl_job_def.getEtl_sys_cd(), etl_job_def.getEtl_job(),
				etl_job_def.getPro_type());
		// 5.如果是依赖作业则保存作业依赖信息,调度频率为频率时不会有调度类型
		if (StringUtil.isNotBlank(etl_job_def.getDisp_type())) {
			// 5.1判断调度触发方式是否为依赖触发
			if (Dispatch_Type.DEPENDENCE == Dispatch_Type.ofEnumByCode(etl_job_def.getDisp_type())) {
				// 5.2判断上游作业名称是否为空，如果不为空判断上游作业名称是否合法
				saveEtlDependencyFromEtlJobDef(etl_dependency, pre_etl_job);
			}
		}
		// 6.判断调度频率是否为频率，根据调度频率不同封装作业定义实体对象的不同属性
		isDispatchFrequency(etl_job_def);
		// 7.新增作业
		etl_job_def.add(Dbo.db());
	}

	@Method(desc = "验证作业定义字段合法性",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
					"2.验证工程编号是否合法" +
					"3.验证作业名称是否合法" +
					"4.验证任务编号是否合法" +
					"5.验证作业程序类型是否合法" +
					"6.验证调度频率是否合法" +
					"7.验证调度触发方式是否合法" +
					"8.验证作业有效标志是否合法" +
					"9.验证当天是否调度是否合法,可为空" +
					"10.验证作业程序名称是否合法" +
					"11.验证作业程序目录是否合法" +
					"12.验证作业程序名称是否合法" +
					"13.日志目录是否合法" +
					"14.作业描述是否合法")
	@Param(name = "etl_job_def", desc = "作业定义实体对象", range = "与数据库对应表字段规则一致", isBean = true)
	private void checkEtlJobDefField(Etl_job_def etl_job_def) {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		// 2.验证工程编号是否合法
		Validator.notBlank(etl_job_def.getEtl_sys_cd(), "工程编号不能为空以及不能为空格！");
		// 3.验证作业名称是否合法
		Validator.notBlank(etl_job_def.getEtl_job(), "作业名称不能为空以及不能为空格！");
		// 4.验证任务编号是否合法
		Validator.notBlank(etl_job_def.getSub_sys_cd(), "任务编号不能为空以及不能为空格！");
		if (!ETLJobUtil.isEtlSubSysExist(etl_job_def.getEtl_sys_cd(), etl_job_def.getSub_sys_cd())) {
			throw new BusinessException("任务编号不存在！");
		}
		// 5.验证作业程序类型是否合法
		Pro_Type.ofEnumByCode(etl_job_def.getPro_type());
		// 6.验证调度频率是否合法
		Dispatch_Frequency.ofEnumByCode(etl_job_def.getDisp_freq());
		// 7.验证调度触发方式是否合法
		if (Dispatch_Frequency.ofEnumByCode(etl_job_def.getDisp_freq()) != Dispatch_Frequency.PinLv) {
			Dispatch_Type.ofEnumByCode(etl_job_def.getDisp_type());
		}
		// 8.验证作业有效标志是否合法
		Job_Effective_Flag.ofEnumByCode(etl_job_def.getJob_eff_flag());
		// 10.验证当天是否调度是否合法,调度频率为频率时可为空
		if (StringUtil.isNotBlank(etl_job_def.getToday_disp())) {
			Today_Dispatch_Flag.ofEnumByCode(etl_job_def.getToday_disp());
		}
		// 11.验证作业程序名称是否合法
		Validator.notBlank(etl_job_def.getPro_name(), "作业程序名称不能为空或空格！");
		// 12.验证作业程序目录是否合法
		Validator.notBlank(etl_job_def.getPro_dic(), "验证作业程序目录不能为空或空格！");
		// 13.日志目录是否合法
		Validator.notBlank(etl_job_def.getLog_dic(), "日志目录不能为空或空格！");
		// 14.作业描述是否合法
		Validator.notBlank(etl_job_def.getEtl_job_desc(), "作业描述不能为空或空格！");
	}

	@Method(desc = "根据调度频率不同封装作业定义实体对象的不同属性",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
					"2.判断调度频率是否为频率，根据调度频率不同封装作业定义实体对象的不同属性" +
					"2.1调度频率为频率" +
					"2.2调度频率不为频率")
	@Param(name = "etl_job_def", desc = "作业定义实体对象", range = "与数据库对应表规则一致", isBean = true)
	private void isDispatchFrequency(Etl_job_def etl_job_def) {
		// 1.数据可访问权限处理方式，该方法不需要权限验证
		// 2.判断调度频率是否为频率，根据调度频率不同封装作业定义实体对象的不同属性
		if (Dispatch_Frequency.PinLv == Dispatch_Frequency.ofEnumByCode(etl_job_def.getDisp_freq())) {
			// 2.1调度频率为频率
			etl_job_def.setDisp_offset("");
			etl_job_def.setDisp_type(Dispatch_Frequency.PinLv.getCode());
			etl_job_def.setDisp_time("");
			etl_job_def.setJob_priority("0");
			etl_job_def.setCom_exe_num(0);
			etl_job_def.setLast_exe_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate())
					+ " " + DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
			Integer exe_num = etl_job_def.getExe_num();
			if (exe_num == null) {
				etl_job_def.setExe_num(Integer.MAX_VALUE);
			}
		} else {
			// 2.2调度频率不为频率
			etl_job_def.setExe_frequency("");
			etl_job_def.setExe_num("");
			etl_job_def.setCom_exe_num(0);
			etl_job_def.setStar_time("");
			etl_job_def.setEnd_time("");
		}
	}

	@Method(desc = "判断作业程序类型是否为Thrift或Yarn，如果是根据修改前和修改后两种不同情况进行处理资源分配情况",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
					"2.资源分配根据修改前和修改后程序类型为yarn或thrift两种不同情况进行处理资源分配情况" +
					"3.作业类型变为 Thrift 或者  Yarn 则添加资源分配信息( 其他 ----> Thrift 或者  Yarn )," +
					"作业类型是 Thrift 或者  Yarn 则2种互相更新.( Thrift <----->  Yarn )" +
					"3.1检查是否存在资源分配，不存在资源分配,保存资源分配信息，存在,则更新资源分配" +
					"4.当作业程序类型由thrift或yarn更改为其他类型时需删除新增时分配的资源" +
					"4.1判断获取修改前的作业程序类型是否不为空，不为空则为更新作业，为空，则为新增作业" +
					"4.2判断更新前作业程序类型是否为thrift或yarn，如果是更改为其他类型时删除新增时分配的资源")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
	@Param(name = "pro_type", desc = "修改后的作业程序类型", range = "使用ProType代码项（ProType）")
	private void isThriftOrYarnProType(String etl_sys_cd, String etl_job, String pro_type) {
		// 1.数据可访问权限处理方式，该方法不需要权限验证
		// 2.资源分配对于编辑作业有俩种情况，根据修改前和修改后程序类型为yarn或thrift两种不同情况进行处理资源分配情况
		if (Pro_Type.Thrift == Pro_Type.ofEnumByCode(pro_type) ||
				Pro_Type.Yarn == Pro_Type.ofEnumByCode(pro_type)) {
			// 3.作业类型变为 Thrift 或者  Yarn 则添加资源分配信息( 其他 ----> Thrift 或者  Yarn ),
			// 作业类型是 Thrift 或者  Yarn 则2种互相更新.( Thrift <----->  Yarn )
			Etl_job_resource_rela etlJobResourceRela = new Etl_job_resource_rela();
			etlJobResourceRela.setEtl_sys_cd(etl_sys_cd);
			etlJobResourceRela.setEtl_job(etl_job);
			etlJobResourceRela.setResource_type(pro_type);
			etlJobResourceRela.setResource_req(1);
			// 3.1检查是否存在资源分配，不存在资源分配,保存资源分配信息，存在,则更新资源分配
			if (!ETLJobUtil.isEtlJobResourceRelaExist(etl_sys_cd, etl_job)) {
				// 不存在资源分配,保存资源分配信息
				saveEtlJobResourceRela(etlJobResourceRela);
			} else {
				// 存在,则更新资源分配
				updateEtlJobResourceRela(etlJobResourceRela);
			}
		} else {
			// 4.当作业程序类型由thrift或yarn更改为其他类型时需删除新增时分配的资源
			// 4.1判断获取修改前的作业程序类型是否不为空，不为空则为更新作业，为空，则为新增作业
			List<String> proTypeList = Dbo.queryOneColumnList("select pro_type from etl_job_def where "
					+ " etl_sys_cd=? and etl_job=?", etl_sys_cd, etl_job);
			if (!proTypeList.isEmpty()) {
				// 4.2判断更新前作业程序类型是否为thrift或yarn，如果是更改为其他类型时删除新增时分配的资源
				if (Pro_Type.Thrift == Pro_Type.ofEnumByCode(proTypeList.get(0)) ||
						Pro_Type.Yarn == Pro_Type.ofEnumByCode(proTypeList.get(0))) {
					DboExecute.deletesOrThrow("当作业程序类型由thrift或yarn更改为其他类型时需删除新增时" +
							"分配的资源，删除资源失败！", "delete from " + Etl_job_resource_rela.TableName
							+ " where etl_sys_cd=? AND etl_job=?", etl_sys_cd, etl_job);
				}
			}
		}
	}

	@Method(desc = "更新资源分配信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.验证etl_job_resource_rela表对应实体字段合法性" +
					"3.检测当前作业分配的占用资源数是否过大" +
					"4.更新保存资源分配信息")
	@Param(name = "jobResourceRelation", desc = "资源使用表实体对象", range = "与数据库表定义规则一致", isBean = true)
	public void updateEtlJobResourceRela(Etl_job_resource_rela jobResourceRelation) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		// 2.验证etl_job_resource_rela表对应实体字段合法性
		checkEtlJobResourceRelaField(jobResourceRelation);
		// 3.检测当前作业分配的占用资源数是否过大
		ETLJobUtil.isResourceDemandTooLarge(jobResourceRelation.getEtl_sys_cd(), jobResourceRelation.getResource_type(),
				jobResourceRelation.getResource_req());
		// 4.更新保存资源分配信息
		jobResourceRelation.update(Dbo.db());
	}

	@Method(desc = "新增保存资源分配信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.验证etl_job_resource_rela表对应实体字段合法性" +
					"3.新增时.检测当前作业是否已经分配过资源" +
					"4.检测当前作业分配的占用资源数是否过大" +
					"5.新增保存资源分配信息")
	@Param(name = "jobResourceRelation", desc = "资源使用表实体对象", range = "与数据库表定义规则一致", isBean = true)
	public void saveEtlJobResourceRela(Etl_job_resource_rela jobResourceRelation) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		// 2.验证etl_job_resource_rela表对应实体字段合法性
		checkEtlJobResourceRelaField(jobResourceRelation);
		// 3.新增时.检测当前作业是否已经分配过资源
		if (ETLJobUtil.isEtlJobResourceRelaExist(jobResourceRelation.getEtl_sys_cd(),
				jobResourceRelation.getEtl_job())) {
			throw new BusinessException("当前工程对应作业资源分配信息已存在，不能新增！");
		}
		// 4.检测当前作业分配的占用资源数是否过大
		ETLJobUtil.isResourceDemandTooLarge(jobResourceRelation.getEtl_sys_cd(),
				jobResourceRelation.getResource_type(), jobResourceRelation.getResource_req());
		// 5.新增保存资源分配信息
		jobResourceRelation.add(Dbo.db());
	}

	@Method(desc = "验证etl_job_resource_rela表对应实体字段合法性",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.验证工程编号是否为空" +
					"3.验证资源类型是否为空" +
					"4.验证作业名称是否为空" +
					"5.验证资源需求数是否为空")
	@Param(name = "jobResourceRelation", desc = "资源使用表实体对象", range = "与数据库表定义规则一致", isBean = true)
	private void checkEtlJobResourceRelaField(Etl_job_resource_rela jobResourceRelation) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		// 2.验证工程编号是否为空
		Validator.notBlank(jobResourceRelation.getEtl_sys_cd(), "工程编号不能为空！");
		// 3.验证资源类型是否为空
		Validator.notBlank(jobResourceRelation.getResource_type(), "资源类型不能为空！");
		// 4.验证作业名称是否为空
		Validator.notBlank(jobResourceRelation.getEtl_job(), "作业名称不能为空！");
		// 5.验证资源需求数是否为空
		Validator.notNull(jobResourceRelation.getResource_req(), "资源需求数不能为空！");
	}

	@Method(desc = "分页查询作业资源分配信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.判断作业名称是否为空，不为空加条件查询" +
					"3.判断参数类型是否为空，不为空加条件查询" +
					"4.分页查询作业资源分配信息" +
					"5.创建存放分页查询资源分配信息、分页查询总记录数的集合并封装数据" +
					"6.返回分页查询资源分配信息、分页查询总记录数")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成", nullable = true)
	@Param(name = "resource_type", desc = "参数类型", range = "新增参数时生成", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "返回存放分页查询资源分配信息、分页查询总记录数的集合", range = "无限制")
	public Map<String, Object> searchEtlJobResourceRelaByPage(String etl_sys_cd, String etl_job,
	                                                          String resource_type, int currPage,
	                                                          int pageSize) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		asmSql.clean();
		asmSql.addSql("select * from " + Etl_job_resource_rela.TableName + " where etl_sys_cd=? ");
		asmSql.addParam(etl_sys_cd);
		// 2.判断作业名称是否为空，不为空加条件查询
		if (StringUtil.isNotBlank(etl_job)) {
			asmSql.addLikeParam("etl_job", "%" + etl_job + "%");
		}
		// 3.判断参数类型是否为空，不为空加条件查询
		if (StringUtil.isNotBlank(resource_type)) {
			asmSql.addLikeParam("resource_type", "%" + resource_type + "%");
		}
		asmSql.addSql("order by etl_sys_cd,etl_job");
		Page page = new DefaultPageImpl(currPage, pageSize);
		// 4.分页查询作业资源分配信息
		List<Map<String, Object>> resourceRelation = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
		// 5.创建存放分页查询资源分配信息、分页查询总记录数的集合并封装数据
		Map<String, Object> resourceRelationMap = new HashMap<>();
		resourceRelationMap.put("jobResourceRelation", resourceRelation);
		resourceRelationMap.put("totalSize", page.getTotalSize());
		// 6.返回分页查询资源分配信息、分页查询总记录数
		return resourceRelationMap;
	}

	@Method(desc = "查询资源类型",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.查询资源类型")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Return(desc = "返回资源类型", range = "无限制")
	public List<String> searchEtlResourceType(String etl_sys_cd) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		// 2.查询资源类型
		return Dbo.queryOneColumnList("select resource_type from " + Etl_resource.TableName +
				" where etl_sys_cd=?", etl_sys_cd);
	}

	@Method(desc = "根据工程编号、作业名称查询作业资源分配情况",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.判断当前工程下作业资源使用情况是否存在" +
					"3.返回根据工程编号、作业名称查询作业资源分配情况，实体字段基本都需要所以查询所有字段")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
	@Return(desc = "返回根据工程编号、作业名称查询作业资源分配情况，实体字段基本都需要所以查询所有字段", range = "无限制")
	public Map<String, Object> searchEtlJobResourceRela(String etl_sys_cd, String etl_job) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		// 2.判断当前工程下作业资源使用情况是否存在
		if (!ETLJobUtil.isEtlJobResourceRelaExist(etl_sys_cd, etl_job)) {
			throw new BusinessException("当前工程对应作业资源分配信息不存在！");
		}
		// 3.返回根据工程编号、作业名称查询作业资源分配情况，实体字段基本都需要所以查询所有字段
		return Dbo.queryOneObject("select * from " + Etl_job_resource_rela.TableName +
				" where etl_sys_cd=? AND etl_job=?", etl_sys_cd, etl_job);
	}

	@Method(desc = "更新作业定义信息并返回更新后的最新的作业信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.检查作业定义字段的合法性" +
					"3.如果更新前调度频率为频率时old_pre_etl_job,old_dispatch_type可以为空，否则不能为空！" +
					"4.判断调度频率是否为频率，如果是频率没有依赖，也没有调度触发方式" +
					"4.1判断修改前的调度触发方式是依赖还是定时" +
					"4.1.1修改前的调度触发方式是依赖，判断修改后的调度触发方式是依赖还是定时" +
					"4.1.1.2修改前上游作业名称不为空，修改后的上游作业名称为空，删除依赖" +
					"4.1.1.3修改前上游作业名称为空，修改后的上游作业名称不为空，新增依赖" +
					"4.1.2调度触发方式改变时，修改后的调度方式是定时（依赖-定时），直接删除原依赖关系" +
					"4.2修改前的调度触发方式是定时,判断修改后的调度方式为依赖还是定时" +
					"4.2.1修改后的调度触发方式定时  将定时更改为依赖,则新增，（定时---->依赖）" +
					"5.判断作业程序类型是否为yarn或者thrift类型，如果是，进行资源分配处理" +
					"6.根据调度频率不同封装作业定义实体对象的不同属性" +
					"7.保存更新的作业信息")
	@Param(name = "etl_job_def", desc = "作业定义实体对象", range = "与数据库对应表字段规则一致", isBean = true)
	@Param(name = "etl_dependency", desc = "作业依赖实体对象", range = "与数据库对应表字段规则一致", isBean = true)
	@Param(name = "old_disp_freq", desc = "修改前的调度频率（使用Dispatch_Frequency）代码项", range = "无限制")
	@Param(name = "old_pre_etl_job", desc = "修改前的上游作业名称的数组", range = "修改前的调度频率为频率时为空",
			nullable = true)
	@Param(name = "old_dispatch_type", desc = "调度触发方式改变时，修改前的调度触发方式",
			range = "使用调度触发方式代码项（DispatchType），修改前的调度频率为频率时为空", nullable = true)
	@Param(name = "pre_etl_job", desc = "修改后上游作业名称的数组", range = "已存在的作业名称", nullable = true)
	public void updateEtlJobDef(Etl_job_def etl_job_def, Etl_dependency etl_dependency, String old_disp_freq,
	                            String[] old_pre_etl_job, String old_dispatch_type, String[] pre_etl_job) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.检查作业定义字段的合法性
		checkEtlJobDefField(etl_job_def);
		// 3.如果更新前调度频率为频率时old_dispatch_type可以为空，否则不能为空！
		if (Dispatch_Frequency.ofEnumByCode(old_disp_freq) != Dispatch_Frequency.PinLv) {
			if (StringUtil.isBlank(old_dispatch_type)) {
				throw new BusinessException("更新前调度频率不是频率时old_dispatch_type不可以为空！");
			}
			// 代码项合法性
			Dispatch_Type.ofEnumByCode(old_dispatch_type);
		}
		// 4.判断调度频率是否为频率，如果是频率没有依赖，也没有调度触发方式
		if (Dispatch_Frequency.ofEnumByCode(etl_job_def.getDisp_freq()) != Dispatch_Frequency.PinLv) {
			// 4.1修改前的调度触发方式是依赖
			if (Dispatch_Type.DEPENDENCE == Dispatch_Type.ofEnumByCode(old_dispatch_type)) {
				if (Dispatch_Type.DEPENDENCE == Dispatch_Type.ofEnumByCode(etl_job_def.getDisp_type())) {
					// 5.1.1修改前后的调度触发方式都是依赖,先删除原依赖关系，再新增依赖
					updateDependencyFromEtlJobDef(etl_dependency, old_pre_etl_job, pre_etl_job);
				} else {
					// 5.1.2修改前的调度触发方式是依赖,修改后的调度方式是定时（依赖-定时），直接删除原依赖关系
					if (old_pre_etl_job != null && old_pre_etl_job.length != 0) {
						deleteOldDependency(etl_dependency, old_pre_etl_job);
					}
				}
			} else {
				// 4.2修改前的调度触发方式是定时,修改后的调度触发方式为依赖,则新增依赖（定时---->依赖）
				if (Dispatch_Type.DEPENDENCE == Dispatch_Type.ofEnumByCode(etl_job_def.getDisp_type())) {
					saveEtlDependencyFromEtlJobDef(etl_dependency, pre_etl_job);
				}
			}
		}
		// 5.判断作业程序类型是否为yarn或者thrift类型，如果是，进行资源分配处理
		isThriftOrYarnProType(etl_job_def.getEtl_sys_cd(), etl_job_def.getEtl_job(),
				etl_job_def.getPro_type());
		// 6.根据调度频率不同封装作业定义实体对象的不同属性
		isDispatchFrequency(etl_job_def);
		// 7.保存更新的作业信息
		etl_job_def.setUpd_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()) + " " +
				DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
		etl_job_def.update(Dbo.db());
	}

	@Method(desc = "更新作业时保存所有依赖",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
					"2.判断修改前的上游作业名称的数组是否为空,如果为空先删除" +
					"3.先删除原依赖关系" +
					"4.循环保存依赖")
	@Param(name = "etl_job_def", desc = "作业定义实体对象", range = "与数据库对应表字段规则一致", isBean = true)
	@Param(name = "etl_dependency", desc = "作业依赖实体对象", range = "与数据库对应表字段规则一致", isBean = true)
	@Param(name = "old_pre_etl_job", desc = "修改前的上游作业名称的数组", range = "修改前的调度频率为频率时为空",
			nullable = true)
	@Param(name = "pre_etl_job", desc = "修改后上游作业名称的数组", range = "已存在的作业名称", nullable = true)
	private void updateDependencyFromEtlJobDef(Etl_dependency etl_dependency,
	                                           String[] old_pre_etl_job, String[] pre_etl_job) {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		// 2.判断修改前的上游作业名称的数组是否为空,如果为空先删除
		if (old_pre_etl_job != null && old_pre_etl_job.length != 0) {
			// 3.先删除原依赖关系
			deleteOldDependency(etl_dependency, old_pre_etl_job);
		}
		// 4.循环保存依赖
		saveEtlDependencyFromEtlJobDef(etl_dependency, pre_etl_job);
	}

	@Method(desc = "新增或修改作业时保存作业依赖",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
					"2.判断修改后的上游作业名称的数组是否为空" +
					"3.如果当前作业与上游作业名称相同则跳过" +
					"4.循环判断修改后的上游作业名称是否已不存在" +
					"5.循环保存作业依赖")
	@Param(name = "etl_dependency", desc = "作业依赖实体对象", range = "与数据库对应表字段规则一致", isBean = true)
	@Param(name = "pre_etl_job", desc = "修改后上游作业名称的数组", range = "已存在的作业名称", nullable = true)
	private void saveEtlDependencyFromEtlJobDef(Etl_dependency etl_dependency, String[] pre_etl_job) {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		// 2.判断修改后的上游作业名称的数组是否为空
		if (pre_etl_job != null && pre_etl_job.length != 0) {
			for (String preEtlJob : pre_etl_job) {
				// 3.如果当前作业与上游作业名称相同则跳过
				if (etl_dependency.getEtl_job().equals(preEtlJob)) {
					continue;
				}
				// 4.循环判断修改后的上游作业名称是否已不存在
				if (!ETLJobUtil.isEtlJobDefExist(etl_dependency.getEtl_sys_cd(), preEtlJob)) {
					throw new BusinessException("修改后的上游作业名称已不存在!");
				}
				// 5.循环保存作业依赖
				etl_dependency.setPre_etl_job(preEtlJob);
				if (!ETLJobUtil.isEtlDependencyExist(etl_dependency.getEtl_sys_cd(),
						etl_dependency.getPre_etl_sys_cd(), etl_dependency.getEtl_job(),
						etl_dependency.getPre_etl_job())) {
					etl_dependency.add(Dbo.db());
				}
			}
		}
	}

	@Method(desc = "更新作业依赖时删除旧的依赖关系",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
					"2.判断修改前的上游作业名称是否已不存在" +
					"3.循环删除旧依赖关系")
	@Param(name = "etl_dependency", desc = "作业依赖实体对象", range = "与数据库对应表字段规则一致", isBean = true)
	@Param(name = "old_pre_etl_job", desc = "修改前的上游作业名称的数组", range = "修改前的调度频率为频率时为空",
			nullable = true)
	private void deleteOldDependency(Etl_dependency etl_dependency, String[] old_pre_etl_job) {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		for (String oldPreEtlJob : old_pre_etl_job) {
			// 2.判断修改前的上游作业名称是否已不存在
			if (!ETLJobUtil.isEtlJobDefExist(etl_dependency.getEtl_sys_cd(), oldPreEtlJob)) {
				throw new BusinessException("修改前的上游作业名称已不存在，pre_etl_job=" + oldPreEtlJob);
			}
			// 3.循环删除旧依赖关系
			deleteEtlDependency(etl_dependency.getEtl_sys_cd(), etl_dependency.getPre_etl_sys_cd(),
					etl_dependency.getEtl_job(), oldPreEtlJob);
		}
	}

	@Method(desc = "批量删除Etl作业定义信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.批量删除作业" +
					"3.遍历所有要删除的作业名称" +
					"4.作业被删除的同时删除作业的资源分配情况,只有有资源分配才需要删除" +
					"5.作业被删除的同时删除依赖作业，只有有作业依赖关系才需要删除" +
					"6.删除抽数作业关系表take_relation_etl数据,不关心删除几条数据")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称的数组", range = "无限制")
	public void batchDeleteEtlJobDef(String etl_sys_cd, String[] etl_job) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		SqlOperator.Assembler assembler = SqlOperator.Assembler.newInstance();
		assembler.clean();
		assembler.addSql("delete from " + Etl_job_def.TableName + " where etl_sys_cd=? ").addParam(etl_sys_cd);
		assembler.addORParam("etl_job", etl_job);
		// 2.批量删除作业
		DboExecute.deletesOrThrow(etl_job.length, "删除作业信息失败",
				assembler.sql(), assembler.params());
		// 3.遍历所有要删除的作业名称
		for (String etlJob : etl_job) {
			// 4.作业被删除的同时删除作业的资源分配情况,只有有资源分配才需要删除
			deleteJobResourceRelationIfExist(etl_sys_cd, etlJob);
			// 5.作业被删除的同时删除依赖作业，只有有作业依赖关系才需要删除
			deleteJobDependencyIfExist(etl_sys_cd, etlJob);
			// 6.删除抽数作业关系表take_relation_etl数据,不关心删除几条数据
			Dbo.execute("delete from " + Take_relation_etl.TableName + " where etl_sys_cd=? and etl_job=?",
					etl_sys_cd, etlJob);
		}
	}

	@Method(desc = "删除Etl作业定义信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.删除作业信息" +
					"3.作业被删除的同时删除作业的资源分配情况，只有有资源分配的作业才需要删除" +
					"4.作业被删除的同时删除依赖作业，只有有依赖关系的作业才需要删除" +
					"5.删除抽数作业关系表take_relation_etl数据,不关心删除几条数据")
	@Param(name = "etl_sys_cd", desc = "工程代码", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
	public void deleteEtlJobDef(String etl_sys_cd, String etl_job) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.删除作业信息
		DboExecute.deletesOrThrow("删除作业信息失败，etl_sys_cd=" + etl_sys_cd + ",etl_job="
				+ etl_job, "delete from " + Etl_job_def.TableName + " where etl_sys_cd=?" +
				" and etl_job=?", etl_sys_cd, etl_job);
		// 3.作业被删除的同时删除作业的资源分配情况，只有有资源分配的作业才需要删除
		deleteJobResourceRelationIfExist(etl_sys_cd, etl_job);
		// 4.作业被删除的同时删除依赖作业，只有有依赖关系的作业才需要删除
		deleteJobDependencyIfExist(etl_sys_cd, etl_job);
		// 5.删除抽数作业关系表take_relation_etl数据,不关心删除几条数据
		Dbo.execute("delete from " + Take_relation_etl.TableName + " where etl_sys_cd=? and etl_job=?",
				etl_sys_cd, etl_job);
	}

	@Method(desc = "如果依赖存在先删除",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
					"2.删除当前作业的依赖关系" +
					"2.1删除作业的依赖关系，只有有作业依赖关系的作业才需要删除" +
					"3.删除当前作业作为上游作业时删除依赖关系" +
					"3.1删除当前作业作为上游作业时的依赖关系，只有有作业依赖关系的作业才需要删除")
	@Param(name = "etl_sys_cd", desc = "工程代码", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
	private void deleteJobDependencyIfExist(String etl_sys_cd, String etl_job) {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		// 2.删除当前作业的依赖关系
		if (Dbo.queryNumber("select count(*) from " + Etl_dependency.TableName + " where etl_sys_cd=?" +
				" and etl_job=?", etl_sys_cd, etl_job).orElseThrow(() ->
				new BusinessException("sql查询错误")) > 0) {
			// 2.1删除作业的依赖关系，只有有作业依赖关系的作业才需要删除，不关心删除几条依赖
			Dbo.execute("delete from " + Etl_dependency.TableName +
					" where etl_sys_cd=? AND etl_job=?", etl_sys_cd, etl_job);
		}
		// 3.删除当前作业作为上游作业时删除依赖关系
		if (Dbo.queryNumber("select count(*) from " + Etl_dependency.TableName + " where etl_sys_cd=?" +
				" and pre_etl_job=?", etl_sys_cd, etl_job).orElseThrow(() ->
				new BusinessException("sql查询错误")) > 0) {
			// 3.3.1删除当前作业作为上游作业时的依赖关系，只有有作业依赖关系的作业才需要删除，不关心删除几条依赖
			Dbo.execute("delete from " + Etl_dependency.TableName +
					" where etl_sys_cd=? AND pre_etl_job=?", etl_sys_cd, etl_job);
		}
	}

	@Method(desc = "删除Etl作业资源关系",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
					"2.判断当前作业是否有资源分配情况" +
					"3.删除作业的资源分配情况，只有有资源分配的作业才需要删除")
	@Param(name = "etl_sys_cd", desc = "工程代码", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
	private void deleteJobResourceRelationIfExist(String etl_sys_cd, String etl_job) {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		// 2.判断当前作业是否有资源分配情况
		if (ETLJobUtil.isEtlJobResourceRelaExist(etl_sys_cd, etl_job)) {
			// 3.删除作业的资源分配情况，只有有资源分配的作业才需要删除
			// 现在一个作业只会分配一个资源，而且资源分配这张表就是以作业名称与作业编号作为主键的
			DboExecute.deletesOrThrow(
					"删除资源分配信息失败，etl_sys_cd=" + etl_sys_cd + ",etl_job=" + etl_job,
					"delete from " + Etl_job_resource_rela.TableName +
							" where etl_sys_cd =? AND etl_job = ?", etl_sys_cd, etl_job);
		}
	}

	@Method(desc = "批量删除Etl作业资源关系",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
					"2.批量删除作业资源分配信息")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称的数组", range = "无限制")
	public void batchDeleteEtlJobResourceRela(String etl_sys_cd, String[] etl_job) {
		// 1.数据可访问权限处理方式，该方法不需要权限验证
		// 2.批量删除作业资源分配信息
		// 目前这张表本身就是以工程编号与作业名称作为主键
		SqlOperator.Assembler assembler = SqlOperator.Assembler.newInstance();
		assembler.clean();
		assembler.addSql("delete from " + Etl_job_resource_rela.TableName + " where etl_sys_cd =? ")
				.addParam(etl_sys_cd);
		assembler.addORParam("etl_job", etl_job);
		DboExecute.deletesOrThrow(etl_job.length, "删除资源分配信息失败",
				assembler.sql(), assembler.params());
	}

	@Method(desc = "根据工程编号，作业名称删除Etl作业资源关系",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
					"2.删除资源分配信息")
	@Param(name = "etl_sys_cd", desc = "工程代码", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
	public void deleteEtlJobResourceRela(String etl_sys_cd, String etl_job) {
		// 1.数据可访问权限处理方式，该方法不需要权限验证
		// 2.删除资源分配信息
		// 目前这张表本身就是以工程编号与作业名称作为主键
		DboExecute.deletesOrThrow("删除资源分配信息失败，etl_sys_cd=" + etl_sys_cd +
				",etl_job=" + etl_job, "delete from " + Etl_job_resource_rela.TableName +
				" where etl_sys_cd =? AND etl_job = ?", etl_sys_cd, etl_job);
	}

	@Method(desc = "分页查询etl资源定义信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.拼接sql" +
					"3.判断资源类型是否存在，存在加条件查询" +
					"4.分页查询资源信息" +
					"5.创建存放分页查询资源信息、分页查询总记录数的集合并封装数据" +
					"6.返回分页查询资源信息、分页查询总记录数")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "resource_type", desc = "资源类型", range = "无限制", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "分页查询资源信息、分页查询总记录数", range = "无限制")
	public Map<String, Object> searchEtlResourceByPage(String etl_sys_cd, String resource_type,
	                                                   int currPage, int pageSize) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		// 2.拼接sql
		asmSql.addSql("select * from " + Etl_resource.TableName + " where etl_sys_cd = ?");
		asmSql.addParam(etl_sys_cd);
		// 3.判断资源类型是否存在，存在加条件查询
		if (StringUtil.isNotBlank(resource_type)) {
			asmSql.addLikeParam("resource_type", "%" + resource_type + "%");
		}
		asmSql.addSql(" order by etl_sys_cd,resource_type");
		// 4.分页查询资源信息
		Page page = new DefaultPageImpl(currPage, pageSize);
		List<Map<String, Object>> etlResourceList = Dbo.queryPagedList(page, asmSql.sql(),
				asmSql.params());
		// 5.创建存放分页查询资源信息、分页查询总记录数，工程编号、工程名称的集合并封装数据
		Map<String, Object> etlResourceMap = new HashMap<>();
		etlResourceMap.put("etlResourceList", etlResourceList);
		etlResourceMap.put("totalSize", page.getTotalSize());
		// 6.返回分页查询资源信息、分页查询总记录数
		return etlResourceMap;
	}

	@Method(desc = "根据工程编号、资源类型查询资源定义信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.判断当前工程下资源定义信息是否存在" +
					"3.返回根据工程编号、资源类型查询资源定义信息,实体字段基本都需要所以查询所有字段")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "resource_type", desc = "资源类型", range = "新增资源时生成")
	@Return(desc = "返回根据工程编号、资源类型查询资源定义信息", range = "无限制")
	public Map<String, Object> searchEtlResource(String etl_sys_cd, String resource_type) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		// 2.判断当前工程下资源定义信息是否存在
		if (!ETLJobUtil.isEtlResourceExist(etl_sys_cd, resource_type)) {
			throw new BusinessException("当前工程对应的资源已不存在！");
		}
		// 3.返回根据工程编号、资源类型查询资源定义信息,实体字段基本都需要所以查询所有字段
		return Dbo.queryOneObject("select * from " + Etl_resource.TableName + " where etl_sys_cd=?" +
				" AND resource_type=?", etl_sys_cd, resource_type);
	}

	@Method(desc = "新增保存etl资源定义信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.验证etl_resource字段合法性" +
					"3.确认新增的资源不存在，存在不能新增" +
					"4.新增资源定义信息")
	@Param(name = "etl_resource", desc = "etl_resource表实体对象", range = "与数据库对应表定义规则一致",
			isBean = true)
	public void saveEtlResource(Etl_resource etl_resource) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		if (!ETLJobUtil.isEtlSysExistById(etl_resource.getEtl_sys_cd(), getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		// 2.验证etl_resource字段合法性
		checkEtlResourceField(etl_resource);
		// 3.确认新增的资源不存在，存在不能新增
		if (ETLJobUtil.isEtlResourceExist(etl_resource.getEtl_sys_cd(), etl_resource.getResource_type())) {
			throw new BusinessException("当前工程对应的资源已存在,不能新增！");
		}
		// 目前的服务器同步标志先使用默认的同步
		etl_resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
		// 4.新增资源定义信息
		etl_resource.add(Dbo.db());
	}

	@Method(desc = "验证etl_resource字段合法性",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
					"2.验证etl_sys_cd合法性" +
					"3.验证resource_type合法性" +
					"4.验证resource_max合法性")
	@Param(name = "etl_resource", desc = "etl_resource表实体对象", range = "与数据库对应表定义规则一致",
			isBean = true)
	private void checkEtlResourceField(Etl_resource etl_resource) {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		// 2.验证etl_sys_cd合法性
		Validator.notBlank(etl_resource.getEtl_sys_cd(), "工程编号不能为空！");
		// 3.验证resource_type合法性
		Validator.notBlank(etl_resource.getResource_type(), "资源类型不能为空！");
		// 4.验证resource_max合法性
		Validator.notNull(etl_resource.getResource_max(), "资源阈值不能为空！");
	}

	@Method(desc = "更新资源信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.确认要更新的资源存在" +
					"3.更新资源信息")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "resource_type", desc = "资源类型", range = "新增资源时生成")
	@Param(name = "resource_max", desc = "资源阀值", range = "大于0的正整数")
	public void updateEtlResource(String etl_sys_cd, String resource_type, long resource_max) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.确认要更新的资源存在
		if (!ETLJobUtil.isEtlResourceExist(etl_sys_cd, resource_type)) {
			throw new BusinessException("当前工程对应的资源已不存在！");
		}
		// 3.更新资源信息
		DboExecute.updatesOrThrow("更新资源失败，etl_sys_cd=" + etl_sys_cd + ",resource_type="
				+ resource_type, "update " + Etl_resource.TableName + " set resource_max=? " +
				" where etl_sys_cd=? and resource_type=?", resource_max, etl_sys_cd, resource_type);
	}

	@Method(desc = "批量删除作业资源定义",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.遍历所有资源类型" +
					"3.循环删除作业资源定义信息")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "resource_type", desc = "资源类型的数组", range = "无限制")
	public void batchDeleteEtlResource(String etl_sys_cd, String[] resource_type) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.遍历所有资源类型
		for (String resourceType : resource_type) {
			// 3.循环删除作业资源定义信息
			DboExecute.deletesOrThrow("删除删除作业资源定义信息失败，etl_sys_cd=" + etl_sys_cd +
					",resource_type=" + resourceType, "delete from " + Etl_resource.TableName +
					" where etl_sys_cd = ? AND resource_type = ?", etl_sys_cd, resourceType);
		}
	}

	@Method(desc = "删除作业资源定义",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.删除作业资源定义")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "resource_type", desc = "变量名称", range = "新增作业系统参数时生成")
	public void deleteEtlResource(String etl_sys_cd, String resource_type) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		// 2.删除作业资源定义
		DboExecute.deletesOrThrow("删除作业系统参数失败，etl_sys_cd=" + etl_sys_cd +
				",resource_type=" + resource_type, "delete from " + Etl_resource.TableName +
				" where etl_sys_cd = ? AND resource_type = ?", etl_sys_cd, resource_type);
	}

	@Method(desc = "分页查询作业调度系统参数信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.拼接sql前清理原sql" +
					"3.判断变量名称是否存在，存在加条件查询" +
					"4.分页查询系统参数信息" +
					"5.创建存放分页查询系统参数信息、分页查询总记录数、工程编号、工程名称的集合并封装数据" +
					"6.返回分页查询系统参数信息、分页查询总记录数、工程编号、工程名称")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "para_cd", desc = "变量名称", range = "新增作业系统参数时生成", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "分页查询系统参数信息、分页查询总记录数、工程编号、工程名称", range = "取值范围")
	public Map<String, Object> searchEtlParaByPage(String etl_sys_cd, String para_cd, int currPage, int pageSize) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		// 2.拼接sql前清理原sql
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("select * from " + Etl_para.TableName + " where etl_sys_cd IN (?,?)");
		asmSql.addParam(etl_sys_cd);
		// 默认系统参数，目前写死
		asmSql.addParam(DefaultEtlSysCd);
		// 3.判断变量名称是否存在，存在加条件查询
		if (StringUtil.isNotBlank(para_cd)) {
			asmSql.addLikeParam("para_cd", "%" + para_cd + "%");
		}
		asmSql.addSql(" order by etl_sys_cd,para_cd");
		Page page = new DefaultPageImpl(currPage, pageSize);
		// 4.分页查询系统参数信息
		List<Map<String, Object>> etlParaList = Dbo.queryPagedList(page, asmSql.sql(),
				asmSql.params());
		// 5.创建存放分页查询系统参数信息、分页查询总记录数、工程编号、工程名称的集合并封装数据
		Map<String, Object> etlParaMap = new HashMap<>();
		etlParaMap.put("etlParaList", etlParaList);
		etlParaMap.put("totalSize", page.getTotalSize());
		// 6.返回分页查询系统参数信息、分页查询总记录数、工程编号、工程名称
		return etlParaMap;
	}

	@Method(desc = "新增保存作业系统参数",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.字段合法性验证" +
					"3.系统参数变量名称需要拼接前缀！" +
					"4.判断作业系统参数变量名称是否已存在" +
					"5.保存作业系统参数")
	@Param(name = "etl_para", desc = "作业系统参数实体对象", range = "与数据库对应表字段规则一致", isBean = true)
	public void saveEtlPara(Etl_para etl_para) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		// 2.字段合法性验证
		checkEtlParaField(etl_para);
		// 3.系统参数变量名称需要拼接前缀！
		String para_cd = PREFIX + etl_para.getPara_cd();
		// 4.判断作业系统参数变量名称是否已存在
		if (ETLJobUtil.isEtlParaExist(etl_para.getEtl_sys_cd(), para_cd)) {
			throw new BusinessException("作业系统参数变量名称已存在,不能新增！");
		}
		etl_para.setPara_cd(para_cd);
		// 5.保存作业系统参数
		etl_para.add(Dbo.db());
	}

	@Method(desc = "验证作业系统参数字段合法性",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
					"2.验证etl_sys_cd合法性" +
					"3.验证para_cd合法性" +
					"4.验证para_val合法性")
	@Param(name = "etl_para", desc = "作业系统参数实体对象", range = "与数据库对应表字段规则一致", isBean = true)
	private void checkEtlParaField(Etl_para etl_para) {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		// 2.验证etl_sys_cd合法性
		Validator.notBlank(etl_para.getEtl_sys_cd(), "etl_sys_cd工程编号不能为空以及空格！");
		// 3.验证para_cd合法性
		Validator.notBlank(etl_para.getPara_cd(), "para_cd变量名称不能为空以及空格！");
		// 4.验证para_val合法性
		Validator.notBlank(etl_para.getPara_val(), "para_val变量值不能为空以及空格！");
		// 5.验证para_type合法性
		ParamType.ofEnumByCode(etl_para.getPara_type());
	}

	@Method(desc = "更新保存作业系统参数",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.字段合法性验证" +
					"3.更新作业系统参数")
	@Param(name = "etl_para", desc = "作业系统参数实体对象", range = "与数据库对应表字段规则一致")
	public void updateEtlPara(Etl_para etl_para) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		// 2.字段合法性验证
		checkEtlParaField(etl_para);
		// 3.更新作业系统参数
		etl_para.update(Dbo.db());
	}

	@Method(desc = "根据工程编号、变量名称查询作业系统参数",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.判断当前工程下作业系统参数是否存在" +
					"3.返回根据工程编号、变量名称查询作业系统参数，实体字段基本都需要所以查询所有字段")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "para_cd", desc = "变量名称", range = "新增作业系统参数时生成")
	@Return(desc = "返回根据工程编号、变量名称查询作业系统参数信息", range = "取值范围")
	public Map<String, Object> searchEtlPara(String etl_sys_cd, String para_cd) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		// 2.判断当前工程下作业系统参数是否存在
		if (!ETLJobUtil.isEtlParaExist(etl_sys_cd, para_cd)) {
			throw new BusinessException("作业系统参数已不存在，可能被删除！");
		}
		// 3.返回根据工程编号、变量名称查询作业系统参数，实体字段基本都需要所以查询所有字段
		return Dbo.queryOneObject("select * from " + Etl_para.TableName + " where etl_sys_cd=?" +
				" AND para_cd=?", etl_sys_cd, para_cd);
	}

	@Method(desc = "批量删除作业系统参数",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
					"2.遍历所有系统参数的变量名称" +
					"3.循环删除作业系统参数")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "para_cd", desc = "变量名称的数组", range = "无限制")
	public void batchDeleteEtlPara(String etl_sys_cd, String[] para_cd) {
		// 1.数据可访问权限处理方式，该方法不需要权限验证
		// 2.遍历所有系统参数的变量名称
		for (String paraCd : para_cd) {
			// 3.循环删除作业系统参数
			DboExecute.deletesOrThrow("删除作业系统参数失败，etl_sys_cd=" + etl_sys_cd +
					",para_cd=" + paraCd, "delete from " + Etl_para.TableName +
					" where etl_sys_cd = ? AND para_cd = ?", etl_sys_cd, paraCd);
		}
	}

	@Method(desc = "删除作业系统参数",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
					"2.删除作业系统参数")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "para_cd", desc = "变量名称", range = "新增作业系统参数时生成")
	public void deleteEtlPara(String etl_sys_cd, String para_cd) {
		// 1.数据可访问权限处理方式，该方法不需要权限验证
		// 2.删除作业系统参数
		DboExecute.deletesOrThrow("删除作业系统参数失败，etl_sys_cd=" + etl_sys_cd +
				",para_cd=" + para_cd, "delete from " + Etl_para.TableName +
				" where etl_sys_cd = ? AND para_cd = ?", etl_sys_cd, para_cd);
	}

	@Method(desc = "分页查询作业依赖信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.判断作业名称是否为空，不为空加条件查询" +
					"3.判断上游作业名称是否为空，不为空加条件查询" +
					"4.分页查询作业依赖信息" +
					"5.创建存放分页查询作业依赖信息、分页查询总记录数的集合并封装数据" +
					"6.返回分页查询作业依赖信息、分页查询总记录数")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成", nullable = true)
	@Param(name = "pre_etl_job", desc = "上游作业名称", range = "新增作业依赖时生成", nullable = true)
	@Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
	@Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
	@Return(desc = "存放分页查询作业依赖信息、分页查询总记录数的集合", range = "无限制")
	public Map<String, Object> searchEtlDependencyByPage(String etl_sys_cd, String etl_job, String pre_etl_job,
	                                                     int currPage, int pageSize) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		asmSql.clean();
		asmSql.addSql("select * from etl_dependency where etl_sys_cd = ? ");
		asmSql.addParam(etl_sys_cd);
		// 2.判断作业名称是否为空，不为空加条件查询
		if (StringUtil.isNotBlank(etl_job)) {
			asmSql.addLikeParam("etl_job", "%" + etl_job + "%");
		}
		// 3.判断上游作业名称是否为空，不为空加条件查询
		if (StringUtil.isNotBlank(pre_etl_job)) {
			asmSql.addLikeParam("pre_etl_job", "%" + pre_etl_job + "%");
		}
		asmSql.addSql(" order by etl_sys_cd,etl_job");
		Page page = new DefaultPageImpl(currPage, pageSize);
		// 4.分页查询作业依赖信息
		List<Map<String, Object>> etlDependencyList = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
		// 5.创建存放分页查询作业依赖信息、分页查询总记录数的集合并封装数据
		Map<String, Object> etlDependencyMap = new HashMap<>();
		etlDependencyMap.put("etlDependencyList", etlDependencyList);
		etlDependencyMap.put("totalSize", page.getTotalSize());
		// 6.返回分页查询作业依赖信息、分页查询总记录数
		return etlDependencyMap;
	}

	@Method(desc = "根据工程编号查询作业依赖信息",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.判断当前工程下的依赖作业是否存在" +
					"3.返回根据工程编号查询作业依赖信息,实体字段基本都需要所以查询所有字段")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
	@Param(name = "pre_etl_job", desc = "上游作业名称", range = "新增作业依赖时生成")
	@Return(desc = "返回根据工程编号查询作业依赖信息，实体字段基本都需要所以查询所有字段", range = "无限制")
	public Map<String, Object> searchEtlDependency(String etl_sys_cd, String etl_job, String pre_etl_job) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		// 2.判断当前工程下的依赖作业是否存在
		if (!ETLJobUtil.isEtlDependencyExist(etl_sys_cd, etl_sys_cd, etl_job, pre_etl_job)) {
			throw new BusinessException("当前工程对应作业的依赖不存在！");
		}
		// 3.返回根据工程编号查询作业依赖信息,实体字段基本都需要所以查询所有字段
		return Dbo.queryOneObject("select * from " + Etl_dependency.TableName + " where etl_sys_cd=?" +
				" AND etl_job=? and pre_etl_job=?", etl_sys_cd, etl_job, pre_etl_job);
	}

	@Method(desc = "新增保存作业依赖",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.验证作业依赖实体字段合法性" +
					"3.判断当前依赖是否已存在，如果存在则不能新增" +
					"4.将当前作业与上游作业交换，看是否已经存在依赖关系" +
					"5.新增依赖当前作业名称与上游作业名称不能相同" +
					"6.新增保存作业依赖")
	@Param(name = "etl_dependency", desc = "作业依赖实体对象", range = "与数据库对应表字段规则一致", isBean = true)
	public void saveEtlDependency(Etl_dependency etl_dependency) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		// 2.验证作业依赖实体字段合法性
		checkEtlDependencyField(etl_dependency);
		// 3.判断当前依赖是否已存在，如果存在则不能新增
		if (ETLJobUtil.isEtlDependencyExist(etl_dependency.getEtl_sys_cd(), etl_dependency.getPre_etl_sys_cd(),
				etl_dependency.getEtl_job(), etl_dependency.getPre_etl_job())) {
			throw new BusinessException("当前工程对应作业的依赖已存在！");
		}
		// 4.将当前作业与上游作业交换，看是否已经存在依赖关系
		if (ETLJobUtil.isEtlDependencyExist(etl_dependency.getEtl_sys_cd(), etl_dependency.getPre_etl_sys_cd(),
				etl_dependency.getPre_etl_job(), etl_dependency.getEtl_job())) {
			throw new BusinessException("当前工程对应作业的依赖已存在！");
		}
		// 5.新增依赖当前作业名称与上游作业名称不能相同
		if (etl_dependency.getEtl_job().equals(etl_dependency.getPre_etl_job())) {
			throw new BusinessException("新增依赖当前作业名称与上游作业名称不能相同！");
		}
		// 6.新增保存作业依赖
		etl_dependency.add(Dbo.db());
	}

	@Method(desc = "验证作业依赖实体字段合法性",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
					"2.验证工程编号合法性" +
					"3.验证上游工程编号合法性" +
					"4.验证作业名称合法性" +
					"5.验证上游作业名称合法性" +
					"6.验证状态合法性" +
					"7.验证上游工程编号是否存在")
	@Param(name = "etl_dependency", desc = "作业依赖实体对象", range = "与数据库对应表字段规则一致", isBean = true)
	private void checkEtlDependencyField(Etl_dependency etl_dependency) {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		// 2.验证工程编号合法性
		Validator.notBlank(etl_dependency.getEtl_sys_cd(), "工程编号不能为空！");
		// 3.验证上游工程编号合法性
		Validator.notBlank(etl_dependency.getPre_etl_sys_cd(), "上游工程编号不能为空！");
		// 4.验证作业名称合法性
		Validator.notBlank(etl_dependency.getEtl_job(), "作业名称不能为空！");
		// 5.验证上游作业名称合法性
		Validator.notBlank(etl_dependency.getPre_etl_job(), "上游作业名称不能为空！");
		// 6.验证状态合法性
		Validator.notBlank(etl_dependency.getStatus(), "状态不能为空！");
		Status.ofEnumByCode(etl_dependency.getStatus());
		// 7.验证上游工程编号是否存在
		if (!ETLJobUtil.isEtlSysExistById(etl_dependency.getPre_etl_sys_cd(), getUserId())) {
			throw new BusinessException("上游工程不存在！");
		}

	}

	@Method(desc = "批量新增保存作业依赖",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.验证status是否合法" +
					"3.验证当前任务是否存在" +
					"4.验证上游任务是否存在" +
					"5.获取当前任务下的所有作业" +
					"6.获取上游任务下的所有作业即上游作业" +
					"7.双重循环添加不同任务下的所有作业之间的依赖" +
					"8.如果当前作业等于上游作业名称则跳过" +
					"9.判断当前作业下的依赖是否存在，存在就跳过，不存在则依赖" +
					"10.如果当前作业为定时作业则跳过" +
					"11.将当前作业与上游作业交换，看是否已经存在依赖关系,存在则跳过" +
					"12.循环保存作业依赖关系")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "pre_etl_sys_cd", desc = "上游工程编号（目前上游工程编号与工程编号相同）", range = "新增工程时生成")
	@Param(name = "status", desc = "依赖是否有效", range = "使用（Status）代码项")
	@Param(name = "sub_sys_cd", desc = "任务编号", range = "新增任务时生成")
	@Param(name = "pre_sub_sys_cd", desc = "上游任务编号", range = "新增任务时生成")
	public void batchSaveEtlDependency(String etl_sys_cd, String pre_etl_sys_cd, String sub_sys_cd,
	                                   String pre_sub_sys_cd, String status) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		if (!ETLJobUtil.isEtlSysExistById(pre_etl_sys_cd, getUserId())) {
			throw new BusinessException("当前工程已不存在");
		}
		// 2.验证status是否合法
		Status.ofEnumByCode(status);
		// 3.验证当前任务是否存在
		if (!ETLJobUtil.isEtlSubSysExist(etl_sys_cd, sub_sys_cd)) {
			throw new BusinessException("当前工程对应任务已不存在！");
		}
		// 4.验证上游任务是否存在
		if (!ETLJobUtil.isEtlSubSysExist(etl_sys_cd, pre_sub_sys_cd)) {
			throw new BusinessException("当前工程对应上游任务已不存在！");
		}
		// 5.获取当前任务下的所有作业
		List<Etl_job_def> etlJobList = Dbo.queryList(Etl_job_def.class, "select DISTINCT etl_job,disp_type"
						+ " ,disp_freq from " + Etl_job_def.TableName + " where sub_sys_cd=? and etl_sys_cd=?",
				sub_sys_cd, etl_sys_cd);
		// 6.获取上游任务下的所有作业即上游作业
		List<String> preEtlJobList = Dbo.queryOneColumnList("select DISTINCT etl_job from "
						+ Etl_job_def.TableName + " where sub_sys_cd=? and etl_sys_cd=?", pre_sub_sys_cd,
				etl_sys_cd);
		Etl_dependency etlDependency = new Etl_dependency();
		// 7.双重循环添加不同任务下的所有作业之间的依赖
		for (Etl_job_def etl_job_def : etlJobList) {
			for (String pre_etl_job : preEtlJobList) {
				// 8.如果当前作业等于上游作业名称则跳过
				if (etl_job_def.getEtl_job().equals(pre_etl_job)) {
					continue;
				}
				// 9.判断当前作业下的依赖是否存在，存在就跳过，不存在则依赖
				if (ETLJobUtil.isEtlDependencyExist(etl_sys_cd, pre_etl_sys_cd,
						etl_job_def.getEtl_job(), pre_etl_job)) {
					continue;
				}
				// 10.如果当前作业为定时作业或频率作业则跳过
				if (Dispatch_Frequency.PinLv == Dispatch_Frequency.ofEnumByCode(etl_job_def.getDisp_freq()) ||
						Dispatch_Type.DEPENDENCE != Dispatch_Type.ofEnumByCode(etl_job_def.getDisp_type())) {
					continue;
				}
				// 11.将当前作业与上游作业交换，看是否已经存在依赖关系,存在则跳过
				if (ETLJobUtil.isEtlDependencyExist(etl_sys_cd, pre_etl_sys_cd, pre_etl_job,
						etl_job_def.getEtl_job())) {
					continue;
				}
				// 12.循环保存作业依赖关系
				etlDependency.setEtl_sys_cd(etl_sys_cd);
				etlDependency.setPre_etl_sys_cd(pre_etl_sys_cd);
				etlDependency.setStatus(status);
				etlDependency.setEtl_job(etl_job_def.getEtl_job());
				etlDependency.setPre_etl_job(pre_etl_job);
				etlDependency.add(Dbo.db());
			}
		}
	}

	@Method(desc = "批量删除作业依赖",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
					"2.循环删除作业依赖关系")
	@Param(name = "etlDependencies", desc = "作业依赖实体对象数组", range = "与数据库对应表字段规则一致",
			isBean = true)
	public void batchDeleteEtlDependency(Etl_dependency[] etlDependencies) {
		// 1.数据可访问权限处理方式，该方法不需要权限验证
		for (Etl_dependency etlDependency : etlDependencies) {
			Validator.notBlank(etlDependency.getEtl_sys_cd());
			Validator.notBlank(etlDependency.getPre_etl_sys_cd());
			Validator.notBlank(etlDependency.getEtl_job());
			Validator.notBlank(etlDependency.getPre_etl_job());
			if (!ETLJobUtil.isEtlSysExistById(etlDependency.getEtl_sys_cd(), getUserId())) {
				throw new BusinessException("当前工程已不存在");
			}
			// 2.循环删除作业依赖关系
			etlDependency.delete(Dbo.db());
		}

	}

	@Method(desc = "更新保存作业依赖",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
					"2.验证作业依赖实体字段的合法性" +
					"3.判断更改依赖后依赖是否已存在" +
					"4.更新作业依赖")
	@Param(name = "etlDependency", desc = "作业依赖实体对象", range = "与数据库对应表字段规则一致", isBean = true)
	@Param(name = "oldEtlJob", desc = "更新前作业名称", range = "新增作业时生成")
	@Param(name = "oldPreEtlJob", desc = "更新前上游作业名称", range = "新增作业时生成")
	public void updateEtlDependency(Etl_dependency etlDependency, String oldEtlJob, String oldPreEtlJob) {
		// 1.数据可访问权限处理方式，通过user_id进行权限验证
		// 2.验证作业依赖实体字段的合法性
		checkEtlDependencyField(etlDependency);
		// 3.判断更新前作业名称对应更新后上游作业名称对应依赖是否已存在
		if (ETLJobUtil.isEtlDependencyExist(etlDependency.getEtl_sys_cd(), etlDependency.getPre_etl_sys_cd(),
				oldEtlJob, etlDependency.getPre_etl_job())) {
			throw new BusinessException("更新前作业名称对应更新后上游作业名称对应依赖已存在");
		}
		// 4.更新作业依赖，用实体更新,实体更新是用该表的联合主键去更新的，只会改非主键字段
		DboExecute.updatesOrThrow("更新作业依赖失败", "update " + Etl_dependency.TableName
						+ " set etl_job=?,pre_etl_sys_cd=?,pre_etl_job=?,"
						+ "status=? where etl_sys_cd=? and etl_job=? and pre_etl_job=?",
				etlDependency.getEtl_job(), etlDependency.getPre_etl_sys_cd(), etlDependency.getPre_etl_job(),
				etlDependency.getStatus(), etlDependency.getEtl_sys_cd(), oldEtlJob, oldPreEtlJob);
	}

	@Method(desc = "删除作业依赖",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
					"2.删除作业依赖")
	@Param(name = "etl_sys_cd", desc = "工程代码", range = "新增工程时生成")
	@Param(name = "pre_etl_sys_cd", desc = "上游工程代码", range = "新增工程时生成")
	@Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
	@Param(name = "pre_etl_job", desc = "上游作业名称", range = "新增作业时生成")
	public void deleteEtlDependency(String etl_sys_cd, String pre_etl_sys_cd, String etl_job, String pre_etl_job) {
		// 1.数据可访问权限处理方式，该方法不需要权限验证
		// 2.删除作业依赖
		DboExecute.deletesOrThrow("删除作业依赖失败", "delete from " + Etl_dependency.TableName +
						" where etl_sys_cd=? AND pre_etl_sys_cd=? AND etl_job=? AND pre_etl_job=?",
				etl_sys_cd, pre_etl_sys_cd, etl_job, pre_etl_job);
	}

	@Method(desc = "上传Excel文件",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
					"2.获取文件" +
					"3.从xlsx/xls文件创建的输入流" +
					"4.根据文件后缀名创建不同的工作薄Workbook" +
					"4.1读取2007版，以.xlsx结尾" +
					"4.2读取2003版，以.xls结尾" +
					"5.获取页数" +
					"6.循环页数" +
					"7.得到工作薄的第N个sheet表" +
					"8.获取不包括那些空行（隔行）的情况的行数" +
					"9.循环行数" +
					"10.存放列数据的集合" +
					"11.获取不为空的列个数" +
					"12.如果获取的列数是-1则表示为无效行的单元格,直接跳过" +
					"13.获取单元格信息，如果为null则设置为空字符串" +
					"14.循环列数" +
					"14.1第一行是表头，获取列名称" +
					"14.2.第二行之后是表的值，如果第二行的列值不存在,则不添加" +
					"15.不为空时放入List" +
					"16.将excel数据导入数据库")
	@Param(name = "file", desc = "上传文件", range = "无限制")
	@Param(name = "table_name", desc = "表名称,任务对应etl_sub_sys_list, 作业对应etl_job_def," +
			"资源定义对应etl_resource,资源分配对应etl_job_resource_rela,系统参数对应etl_para," +
			"作业依赖对应etl_dependency", range = "作业配置每个模块对应表名")
	@UploadFile
	public void uploadExcelFile(String file, String table_name) {
		// 1.数据可访问权限处理方式，该方法不需要权限验证
		Workbook workBook = null;
		try {
			// 2.获取文件
			File uploadedFile = FileUploadUtil.getUploadedFile(file);
			if (!uploadedFile.exists()) {
				throw new BusinessException("上传文件不存在！");
			}
			workBook = ExcelUtil.getWorkbookFromExcel(uploadedFile);
			// 3.获取页数
			int numberOfSheets = Objects.requireNonNull(workBook).getNumberOfSheets();
			List<Map<String, String>> listMap = new ArrayList<>();
			// 4.循环页数
			for (int sheetNum = 0; sheetNum < numberOfSheets; sheetNum++) {
				// 5.得到工作薄的第N个sheet表
				Sheet sheet = workBook.getSheetAt(sheetNum);
				Row row;
				String cellVal;
				List<String> columnList = new ArrayList<>();
				// 6.获取不包括那些空行（隔行）的情况的行数
				int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
				for (int i = sheet.getFirstRowNum(); i < physicalNumberOfRows; i++) {
					// 7.循环行数
					row = sheet.getRow(i);
					// 8.存放列数据的集合
					Map<String, String> map = new HashMap<>();
					// 9.获取不为空的列个数
					int physicalNumberOfCells = row.getPhysicalNumberOfCells();
					// j是从1开始的
					for (int j = row.getFirstCellNum(); j <= physicalNumberOfCells; j++) {
						// 10.如果获取的列数是-1则表示为无效行的单元格,直接跳过
						if (j == -1) {
							continue;
						}
						// 11.获取单元格信息，如果为null则设置为空字符串
						Cell cell = row.getCell(j);
						if (null == cell) {
							cellVal = "";
						} else {
							cellVal = cell.toString();
						}
						// 12.循环列数
						if (i == 0) {
							// 13.1第一行是表头，获取列名称
							String[] columnArray = cellVal.split("-");
							columnList.add(columnArray[0]);
						} else {
							if (physicalNumberOfCells > columnList.size()) {
								throw new BusinessException("excel表格格式有问题，表头单元格个数大于等于与表身有效单元格个数");
							}
							// 13.2.第二行之后是表的值，如果第二行的列值不存在,则不添加
							map.put(columnList.get(j).trim(), cellVal.trim());
						}
					}
					// 14.不为空时放入List
					if (!map.isEmpty()) {
						listMap.add(map);
					}
				}
			}
			// 16.将excel数据导入数据库
			insertData(listMap, table_name);
		} catch (FileNotFoundException e) {
			throw new BusinessException("导入excel文件数据失败！");
		} catch (IOException e) {
			throw new BusinessException("获取excel对象失败，文件类型错误");
		} finally {
			try {
				if (workBook != null) {
					workBook.close();
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	@Method(desc = "将excel表数据导入数据库",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
					"2.根据不同的表处理不同的表数据，循环入库")
	@Param(name = "listMap", desc = "当前表对应的所有列数据", range = "无限制")
	@Param(name = "tableName", desc = "表名称,任务对应etl_sub_sys_list, 作业对应etl_job_def," +
			"资源定义对应etl_resource,资源分配对应etl_job_resource_rela,系统参数对应etl_para," +
			"作业依赖对应etl_dependency", range = "无限制")
	private void insertData(List<Map<String, String>> listMap, String tableName) {
		// 1.数据可访问权限处理方式，该方法不需要权限验证
		// 2.根据不同的表处理不同的表数据，循环入库
		for (Map<String, String> mapInfo : listMap) {
			if (mapInfo != null && !mapInfo.isEmpty()) {
				switch (tableName.toLowerCase()) {
					// 作业表
					case Etl_job_def.TableName:
						// 将map转为对应实体
						Etl_job_def etl_job_def = JSON.parseObject(JSON.toJSONString(mapInfo), Etl_job_def.class);
						// 如果当前作业已存在则不入库，只有库里没有的作业才会入库
						if (!ETLJobUtil.isEtlJobDefExist(etl_job_def.getEtl_sys_cd(),
								etl_job_def.getEtl_job())) {
							etl_job_def.add(Dbo.db());
						}
						break;
					// 任务表
					case Etl_sub_sys_list.TableName:
						Etl_sub_sys_list etl_sub_sys_list = JSON.parseObject(JSON.toJSONString(mapInfo),
								Etl_sub_sys_list.class);
						// 如果当前任务已存在则不入库，只有库里没有的作业才会入库
						if (!ETLJobUtil.isEtlSubSysExist(etl_sub_sys_list.getEtl_sys_cd(),
								etl_sub_sys_list.getSub_sys_cd())) {
							etl_sub_sys_list.add(Dbo.db());
						}
						break;
					// 资源定义表
					case Etl_resource.TableName:
						Etl_resource etl_resource = JSON.parseObject(JSON.toJSONString(mapInfo),
								Etl_resource.class);
						// 如果当前资源已存在则不入库，只有库里没有的作业才会入库
						if (!ETLJobUtil.isEtlResourceExist(etl_resource.getEtl_sys_cd(),
								etl_resource.getResource_type())) {
							etl_resource.add(Dbo.db());
						}
						break;
					// 资源分配表
					case Etl_job_resource_rela.TableName:
						Etl_job_resource_rela etl_job_resource_rela = JSON.parseObject(JSON.toJSONString(mapInfo),
								Etl_job_resource_rela.class);
						// 如果当前资源已存在则不入库，只有库里没有的作业才会入库
						if (!ETLJobUtil.isEtlJobResourceRelaExist(etl_job_resource_rela.getEtl_sys_cd(),
								etl_job_resource_rela.getEtl_job())) {
							etl_job_resource_rela.add(Dbo.db());
						}
						break;
					// 系统参数表
					case Etl_para.TableName:
						Etl_para etl_para = JSON.parseObject(JSON.toJSONString(mapInfo),
								Etl_para.class);
						List<String> paraCdList = Dbo.queryOneColumnList("select para_cd from "
								+ Etl_para.TableName + " where etl_sys_cd=?", DefaultEtlSysCd);
						// 如果当前系统参数已存在且不是默认系统参数则不入库，只有库里没有的作业才会入库
						if (!ETLJobUtil.isEtlParaExist(etl_para.getEtl_sys_cd(), etl_para.getPara_cd()) &&
								!paraCdList.contains(etl_para.getPara_cd())) {
							etl_para.add(Dbo.db());
						}
						break;
					// 作业依赖表
					case Etl_dependency.TableName:
						Etl_dependency etl_dependency = JSON.parseObject(JSON.toJSONString(mapInfo),
								Etl_dependency.class);
						// 如果当前系统参数已存在则不入库，只有库里没有的作业才会入库
						if (!ETLJobUtil.isEtlDependencyExist(etl_dependency.getEtl_sys_cd(),
								etl_dependency.getPre_etl_sys_cd(), etl_dependency.getEtl_job(),
								etl_dependency.getPre_etl_job())) {
							etl_dependency.add(Dbo.db());
							break;
						}
					default:
						throw new BusinessException("导入的数据不知道是什么表的信息，目前只支持数据库表作为表名!");
				}
			}
		}

	}

	@Method(desc = "下载文件",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
					"2.下载文件")
	@Param(name = "fileName", desc = "下载文件名", range = "无限制")
	public void downloadFile(String fileName) {
		// 1.数据可访问权限处理方式，该方法不需要权限验证
		// 2.下载文件
		DownloadLogUtil.downloadFile(fileName);
	}

	@Method(desc = "生成Excel表",
			logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
					"2.验证当前用户下的工程是否存在" +
					"3.创建工作簿对象" +
					"4.创建工作表对象" +
					"5.创建单元格对象,批注插入到一行" +
					"6.得到上传文件的保存目录" +
					"7.判断文件是否存在" +
					"8.创建输出流" +
					"9.获取Excel的头列信息" +
					"10.创建绘图对象" +
					"11.遍历列名设置头信息" +
					"12.设置头单元格信息" +
					"13.根据列名判断是否获取备注信息" +
					"14.表对应所有列的值信息" +
					"15.获取对应表数据" +
					"16.存放表每列信息" +
					"17.设置每列信息" +
					"18.封装每列信息" +
					"19.循环出需要的数据,并添加到excel头下方" +
					"20.写进Excel表格")
	@Param(name = "etl_sys_cd", desc = "工程代码", range = "新增工程时生成")
	@Param(name = "tableName", desc = "表名称,任务对应etl_sub_sys_list, 作业对应etl_job_def," +
			"资源定义对应etl_resource,资源分配对应etl_job_resource_rela,系统参数对应etl_para," +
			"作业依赖对应etl_dependency", range = "下载模块对应的表名称")
	public String generateExcel(String etl_sys_cd, String tableName) {
		// 1.数据可访问权限处理方式，通过user_id进行权限控制
		FileOutputStream out = null;
		XSSFWorkbook workbook = null;
		try {
			if (!ETLJobUtil.isEtlSysExistById(etl_sys_cd, getUserId())) {
				throw new BusinessException("当前工程是否存在");
			}
			// 2.创建工作簿对象
			workbook = new XSSFWorkbook();
			// 3.创建工作表对象
			XSSFSheet sheet = workbook.createSheet("sheet1");
			// 4.创建单元格对象,批注插入到一行
			XSSFRow headRow = sheet.createRow(0);
			// 5.得到上传文件的保存目录
			String savePath = ETLJobUtil.getFilePath(tableName) + xlsxSuffix;
			File file = new File(savePath);
			// 6.判断文件是否存在不存在创建
			if (!file.exists()) {
				if (!file.createNewFile()) {
					throw new BusinessException("创建文件失败，文件目录可能不存在！");
				}
			}
			// 7.创建输出流
			out = new FileOutputStream(file);
			// 8.获取Excel的头列信息
			List<TableMeta> tableMetas = MetaOperator.getTablesWithColumns(Dbo.db(), tableName);
			Set<String> columnNames = tableMetas.get(0).getColumnNames();
			// 9.创建绘图对象
			XSSFDrawing xssfDrawing = sheet.createDrawingPatriarch();
			int cellNum = 0;
			// 10.遍历列名设置头信息
			for (String columnName : columnNames) {
				XSSFCell createCell = headRow.createCell(cellNum);
				// 11.设置头单元格信息
				createCell.setCellValue(columnName +
						"-(" + ConvertColumnNameToChinese.getZh_name(columnName) + ")");
				// 12.根据列名判断是否获取备注信息
				String comments = getCodeValueByColumn(columnName);
				if (StringUtil.isNotBlank(comments)) {
					// 前四个参数是坐标点,后四个参数是编辑和显示批注时的大小.
					XSSFComment comment = xssfDrawing.createCellComment(new XSSFClientAnchor
							(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
					comment.setString(new XSSFRichTextString(comments));
					createCell.setCellComment(comment);
				}
				cellNum++;
			}
			// 13.表对应所有列的值信息
			List<List<String>> columnValList = new ArrayList<>();
			// 14.获取对应表数据
			List<Map<String, Object>> tableInfoList = getTableInfo(etl_sys_cd, tableName);
			if (!tableInfoList.isEmpty()) {
				for (Map<String, Object> tableInfo : tableInfoList) {
					// 15.存放表每列信息
					List<String> columnInfoList = new ArrayList<>();
					for (String columnName : columnNames) {
						// 16.设置每列信息
						if (tableInfo.get(columnName) != null) {
							columnInfoList.add(tableInfo.get(columnName).toString());
						} else {
							columnInfoList.add("");
						}
					}
					// 17.封装每列信息
					columnValList.add(columnInfoList);
				}
			}
			// 18.循环出需要的数据,并添加到excel头下方
			if (!columnValList.isEmpty()) {
				for (int i = 0; i < columnValList.size(); i++) {
					headRow = sheet.createRow(i + 1);
					List<String> valueList = columnValList.get(i);
					for (int j = 0; j < valueList.size(); j++) {
						headRow.createCell(j).setCellValue(valueList.get(j));
					}
				}
			}
			// 19.写进Excel表格
			workbook.write(out);
			return tableName + xlsxSuffix;
		} catch (FileNotFoundException e) {
			logger.error(e);
			throw new BusinessException("文件不存在！");
		} catch (IOException e) {
			logger.error(e);
			throw new BusinessException("生成excel文件失败！");
		} catch (Exception e) {
			throw new AppSystemException(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				logger.error("关闭输出流失败", e);
			}
			ExcelUtil.close(workbook);
		}
	}

	@Method(desc = "获取表数据信息(生成excel信息时查询数据库表数据）",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
					"2.判断是否为作业调度系统参数表，根据不同情况获取表信息" +
					"2.1查询系统参数表信息" +
					"2.2查询表信息")
	@Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
	@Param(name = "tableName", desc = "表名称", range = "对应数据库表名称")
	@Return(desc = "返回对应表数据信息", range = "无限制")
	private List<Map<String, Object>> getTableInfo(String etl_sys_cd, String tableName) {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		// 2.判断是否为作业调度系统参数表，根据不同情况获取表信息
		if (Etl_para.TableName.equalsIgnoreCase(tableName)) {
			// 2.1查询系统参数表信息，因为系统参数区别其他表的是会有默认系统参数
			return Dbo.queryList("select * from " + tableName + " where etl_sys_cd in(?,?)",
					etl_sys_cd, DefaultEtlSysCd);
		} else {
			// 2.2查询表信息
			return Dbo.queryList("select * from " + tableName + " where etl_sys_cd = ?", etl_sys_cd);
		}
	}

	@Method(desc = "根据数据库存储代码项对应字段名称获取对应代码项的说明信息",
			logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
					"2.根据数据库存储代码项对应字段名称去判断使用哪个代码项获取代码项信息将说明信息" +
					"写到excel表头列信息对应代码项字段" +
					"3.返回代码项说明信息")
	@Param(name = "type", desc = "数据库对应代码项字段", range = "无限制")
	@Return(desc = "返回代码项说明信息", range = "无限制")
	private String getCodeValueByColumn(String type) {
		// 1.数据可访问权限处理方式，该方法不需要权限控制
		StringBuilder sb = new StringBuilder();
		// 2.根据数据库存储代码项对应字段名称去判断使用哪个代码项获取代码项信息将说明信息写到excel表头列信息对应代码项字段
		switch (type.toLowerCase()) {
			// 作业程序类型
			case pro_type:
				Pro_Type[] proTypes = Pro_Type.values();
				sb.append("详细说明：");
				for (Pro_Type proType : proTypes) {
					sb.append(System.lineSeparator()).append(proType.getCode()).append(" ：").append(proType.getValue());
				}
				break;
			// 调度频率
			case disp_freq:
				Dispatch_Frequency[] dispatchFrequencies = Dispatch_Frequency.values();
				sb.append("详细说明：");
				for (Dispatch_Frequency frequency : dispatchFrequencies) {
					sb.append(System.lineSeparator()).append(frequency.getCode()).append(" ：")
							.append(frequency.getValue());
				}
				break;
			// 触发方式
			case disp_type:
				Dispatch_Type[] dispatchTypes = Dispatch_Type.values();
				sb.append("详细说明：");
				for (Dispatch_Type dispatchType : dispatchTypes) {
					sb.append(System.lineSeparator()).append(dispatchType.getCode()).append(" ：")
							.append(dispatchType.getValue());
				}
				break;
			// 作业有效标志
			case job_eff_flag:
				Job_Effective_Flag[] effectiveFlags = Job_Effective_Flag.values();
				sb.append("详细说明：");
				for (Job_Effective_Flag effectiveFlag : effectiveFlags) {
					sb.append(System.lineSeparator()).append(effectiveFlag.getCode()).append(" ：")
							.append(effectiveFlag.getValue());
				}
				break;
			// 作业调度状态
			case job_disp_status:
				Job_Status[] jobStatuses = Job_Status.values();
				sb.append("详细说明：");
				for (Job_Status jobStatus : jobStatuses) {
					sb.append(System.lineSeparator()).append(jobStatus.getCode()).append(" ：")
							.append(jobStatus.getValue());
				}
				break;
			// 当天是否调度
			case today_disp:
				Today_Dispatch_Flag[] todayDispatchFlags = Today_Dispatch_Flag.values();
				sb.append("详细说明：");
				for (Today_Dispatch_Flag todayDispatchFlag : todayDispatchFlags) {
					sb.append(System.lineSeparator()).append(todayDispatchFlag.getCode()).append(" ：")
							.append(todayDispatchFlag.getValue());
				}
				break;
			// 主服务器同步标志
			case main_serv_sync:
				Main_Server_Sync[] mainServerSyncs = Main_Server_Sync.values();
				sb.append("详细说明：");
				for (Main_Server_Sync mainServerSync : mainServerSyncs) {
					sb.append(System.lineSeparator()).append(mainServerSync.getCode()).append(" ：")
							.append(mainServerSync.getValue());
				}
				break;
			// 状态
			case status:
				Status[] statuses = Status.values();
				sb.append("详细说明：");
				for (Status status : statuses) {
					sb.append(System.lineSeparator()).append(status.getCode()).append(" ：")
							.append(status.getValue());
				}
				break;
			// 变量类型
			case para_type:
				ParamType[] paramTypes = ParamType.values();
				sb.append("详细说明：");
				for (ParamType paramType : paramTypes) {
					sb.append(System.lineSeparator()).append(paramType.getCode()).append(" ：")
							.append(paramType.getValue());
				}
				break;
		}
		// 3.返回代码项说明信息
		return sb.toString();
	}
}

