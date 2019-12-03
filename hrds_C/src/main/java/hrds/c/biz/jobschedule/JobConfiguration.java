package hrds.c.biz.jobschedule;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.c.biz.util.ETLJobUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "作业调度配置管理", author = "dhw", createdate = "2019/10/28 11:36")
public class JobConfiguration extends BaseAction {
    private static final SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
    // 作业系统参数变量名称前缀
    private static final String PREFIX = "!";

    @Method(desc = "分页查询作业调度某工程任务信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
                    "2.判断工程是否存在" +
                    "3.获取工程名称" +
                    "4.获取某个工程下任务信息" +
                    "5.判断任务编号是否为空，如果为空则查询所有任务信息，如果不为空则模糊查询任务信息（搜索）" +
                    "6.分页查询任务信息，实体字段基本都需要所以查询所有字段" +
                    "7.创建存放分页查询任务信息、分页查询总记录数、工程编号、工程名称的集合并封装数据" +
                    "8.返回存放分页查询任务信息、分页查询总记录数、工程编号、工程名称的集合")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "sub_sys_cd", desc = "任务编号", range = "新增任务时生成", nullable = true)
    @Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
    @Return(desc = "存放分页额查询任务信息、分页查询总记录数、工程编号、工程名称的集合", range = "无限制")
    public Map<String, Object> searchEtlSubSysByPage(String etl_sys_cd, String sub_sys_cd,
                                                     int currPage, int pageSize) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.判断工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.获取工程名称
        String etl_sys_name = getEtlSysName(etl_sys_cd, getUserId());
        // 4.获取某个工程下任务信息,每次拼接新sql之前清空原来的sql以及参数
        asmSql.clean();
        asmSql.addSql("select distinct * from " + Etl_sub_sys_list.TableName + " where etl_sys_cd = ?");
        asmSql.addParam(etl_sys_cd);
        // 5.判断任务编号是否为空，如果为空则查询所有任务信息，如果不为空则模糊查询任务信息（搜索）
        if (StringUtil.isNotBlank(sub_sys_cd)) {
            asmSql.addLikeParam("sub_sys_cd", "%" + sub_sys_cd + "%");
        }
        asmSql.addSql(" order by etl_sys_cd,sub_sys_cd");
        Page page = new DefaultPageImpl(currPage, pageSize);
        // 6.分页查询任务信息，实体字段基本都需要所以查询所有字段
        List<Map<String, Object>> etlSubSysList = Dbo.queryPagedList(page, asmSql.sql(),
                asmSql.params());
        // 7.创建存放分页查询任务信息、分页查询总记录数、工程编号、工程名称的集合并封装数据
        Map<String, Object> etlSubSysMap = new HashMap<>();
        etlSubSysMap.put("etl_sys_cd", etl_sys_cd);
        etlSubSysMap.put("etl_sys_name", etl_sys_name);
        etlSubSysMap.put("etlSubSysList", etlSubSysList);
        etlSubSysMap.put("totalSize", page.getTotalSize());
        // 8.返回存放分页查询任务信息、分页查询总记录数、工程编号、工程名称的集合
        return etlSubSysMap;
    }

    @Method(desc = "根据工程编号，任务编号查询任务信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
                    "2.判断当前工程是否还存在" +
                    "3.判断当前工程下的任务是否存在" +
                    "4.返回根据工程编号，任务编号查询任务信息,实体字段基本都需要所以查询所有字段")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "sub_sys_cd", desc = "任务编号", range = "新增任务时生成")
    @Return(desc = "返回根据工程编号，任务编号查询任务信息", range = "无限制")
    public Map<String, Object> searchEtlSubSys(String etl_sys_cd, String sub_sys_cd) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.判断当前工程是否还存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.判断当前工程下的任务是否存在
        if (!ETLJobUtil.isEtlSubSysExist(etl_sys_cd, sub_sys_cd)) {
            throw new BusinessException("该工程下任务已不存在，可能被删除！");
        }
        // 4.返回根据工程编号，任务编号查询任务信息,实体字段基本都需要所以查询所有字段
        return Dbo.queryOneObject("select distinct * from "
                + Etl_sub_sys_list.TableName + " where etl_sys_cd=? and sub_sys_cd=? " +
                "order by etl_sys_cd, sub_sys_cd", etl_sys_cd, sub_sys_cd);
    }

    @Method(desc = "根据工程编号查询工程名称",
            logicStep = "1.数据可访问权限处理方式，根据user_id进行权限验证" +
                    "2.根据工程编号查询工程名称")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "user_id", desc = "创建工程用户ID", range = "新增用户时生成")
    @Return(desc = "返回工程名称", range = "不能为空")
    private String getEtlSysName(String etl_sys_cd, long user_id) {
        // 1.数据可访问权限处理方式，根据user_id进行权限验证
        // 2.判断当前工程是否还存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.根据工程编号查询工程名称,工程存在，工程名称肯定存在，所以不需要判断结果集是否为空
        return Dbo.queryOneColumnList("select etl_sys_name from " + Etl_sys.TableName +
                " where etl_sys_cd=? and user_id=?", etl_sys_cd, user_id).get(0).toString();
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
        if (StringUtil.isBlank(etl_sub_sys_list.getEtl_sys_cd())) {
            throw new BusinessException("工程编号不能为空以及不能为空格，etl_sys_cd=" +
                    etl_sub_sys_list.getEtl_sys_cd());
        }
        // 3.验证任务编号的合法性
        if (StringUtil.isBlank(etl_sub_sys_list.getSub_sys_cd())) {
            throw new BusinessException("任务编号不能为空以及不能为空格，sub_sys_cd=" +
                    etl_sub_sys_list.getSub_sys_cd());
        }
        // 3.验证任务名称的合法性
        if (StringUtil.isBlank(etl_sub_sys_list.getSub_sys_desc())) {
            throw new BusinessException("任务编号不能为空以及不能为空格，sub_sys_cd=" +
                    etl_sub_sys_list.getSub_sys_cd());
        }
    }

    @Method(desc = "新增保存任务",
            logicStep = "1.数据可访问权限处理方式，通过user_id关联进行权限控制" +
                    "2.字段合法性验证" +
                    "3.验证当前用户下的工程是否存在" +
                    "4.判断工程对应的任务是否已存在" +
                    "5.新增任务")
    @Param(name = "etl_sub_sys_list", desc = "任务实体对象", range = "与数据库表字段规则一致", isBean = true)
    public void saveEtlSubSys(Etl_sub_sys_list etl_sub_sys_list) {
        // 1.数据可访问权限处理方式，通过user_id关联进行权限控制
        // 2.字段合法性验证
        checkEtlSubSysField(etl_sub_sys_list);
        // 3.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sub_sys_list.getEtl_sys_cd(), getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 4.判断工程对应的任务是否已存在,不存在才添加
        if (ETLJobUtil.isEtlSubSysExist(etl_sub_sys_list.getEtl_sys_cd(), etl_sub_sys_list.getSub_sys_cd())) {
            throw new BusinessException("该工程对应的任务已存在，不能新增！");
        }
        // 5.新增任务
        etl_sub_sys_list.add(Dbo.db());
    }

    @Method(desc = "更新保存任务",
            logicStep = "1.数据可访问权限处理方式，通过user_id关联进行权限控制" +
                    "2.字段合法性验证" +
                    "3.验证当前用户下的工程是否存在" +
                    "4.修改任务信息")
    @Param(name = "etl_sub_sys_list", desc = "任务实体对象", range = "与数据库表字段规则一致", isBean = true)
    public void updateEtlSubSys(Etl_sub_sys_list etl_sub_sys_list) {
        // 1.数据可访问权限处理方式，通过user_id关联进行权限控制
        // 2.字段合法性验证
        checkEtlSubSysField(etl_sub_sys_list);
        // 3.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sub_sys_list.getEtl_sys_cd(), getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 4.修改任务信息
        etl_sub_sys_list.update(Dbo.db());
    }

    @Method(desc = "批量删除任务信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id关联进行权限控制" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.遍历所有批量删除任务编号的数组获取各个任务编号" +
                    "4.判断该工程对应的任务下是否还有作业" +
                    "5.根据工程编号，任务编号循环删除任务信息")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "不为空")
    @Param(name = "sub_sys_cd", desc = "任务编号的数组", range = "不为空")
    public void batchDeleteEtlSubSys(String etl_sys_cd, String[] sub_sys_cd) {
        // 1.数据可访问权限处理方式，通过user_id关联进行权限控制
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.遍历所有批量删除任务编号的数组获取各个任务编号
        for (String subSysCd : sub_sys_cd) {
            // 4.判断该工程对应的任务下是否还有作业
            ETLJobUtil.isEtlJobDefExistUnderEtlSubSys(etl_sys_cd, subSysCd);
            // 5.根据工程编号，任务编号删除任务信息
            DboExecute.deletesOrThrow("删除任务失败，etl_sys_cd=" + etl_sys_cd + ",sub_sys_cd="
                    + subSysCd, "delete from " + Etl_sub_sys_list.TableName + " where etl_sys_cd=? " +
                    " and sub_sys_cd=?", etl_sys_cd, subSysCd);
        }
    }

    @Method(desc = "根据工程编号，任务编号删除任务信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id关联进行权限控制" +
                    "2.判断该工程是否存在" +
                    "3.判断该工程对应的任务下是否还有作业" +
                    "4.根据工程编号，任务编号删除任务信息")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "sub_sys_cd", desc = "任务编号", range = "新增任务时生成")
    public void deleteEtlSubSys(String etl_sys_cd, String sub_sys_cd) {
        // 1.数据可访问权限处理方式，通过user_id关联进行权限控制
        // 2.判断该工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.判断该工程对应的任务下是否还有作业
        ETLJobUtil.isEtlJobDefExistUnderEtlSubSys(etl_sys_cd, sub_sys_cd);
        // 4.根据工程编号，任务编号删除任务信息
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
    public Map<String, Object> searchEtlJobTemplateById(long etl_temp_id) {
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
                    "2.验证当前用户下的工程是否存在" +
                    "3.验证当前用户下的工程对应任务是否存在" +
                    "4.获取并拼接作业程序参数" +
                    "5.拼接作业程序参数" +
                    "6.封装作业实体" +
                    "7.获取作业模板信息封装作业实体对象" +
                    "8.判断作业名称是否已存在，存在不能新增" +
                    "9.保存模板作业")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "sub_sys_cd", desc = "任务编号", range = "新增任务时生成")
    @Param(name = "etl_job", desc = "作业名称", range = "不能重复，新增作业时生成")
    @Param(name = "etl_temp_id", desc = "作业模板ID", range = "无限制")
    @Param(name = "etl_job_temp_para", desc = "作业模板参数", range = "无限制")
    public void saveEtlJobTemp(String etl_sys_cd, String sub_sys_cd, String etl_job, long etl_temp_id,
                               String etl_job_temp_para) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.验证当前用户下的工程对应任务是否存在
        if (!ETLJobUtil.isEtlSubSysExist(etl_sys_cd, sub_sys_cd)) {
            throw new BusinessException("当前工程对应任务已不存在！");
        }
        // 4.验证
        // 4.获取并拼接作业程序参数
        String[] parameterValues = etl_job_temp_para.split(",");
        StringBuilder sb = new StringBuilder();
        // 5.拼接作业程序参数
        for (int i = 0; i < parameterValues.length; i++) {
            String value = parameterValues[i];
            if (i != parameterValues.length - 1) {
                sb.append(value).append("@");
            } else {
                sb.append(value);
            }
        }
        // 6.封装作业实体
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
        // 7.获取作业模板信息封装作业实体对象
        Map<String, Object> jobTemplate = searchEtlJobTemplateById(etl_temp_id);
        if (!jobTemplate.isEmpty()) {
            etl_job_def.setPro_dic(jobTemplate.get("pro_dic").toString());
            etl_job_def.setPro_name(jobTemplate.get("pro_name").toString());
            etl_job_def.setLog_dic(jobTemplate.get("pro_dic").toString());
        }
        // 8.判断作业名称是否已存在，存在不能新增
        if (ETLJobUtil.isEtlJobDefExist(etl_sys_cd, etl_job)) {
            throw new BusinessException("作业名称已存在不能新增!");
        }
        // 9.保存模板作业
        Etl_dependency etlDependency = new Etl_dependency();
        etlDependency.setEtl_sys_cd(etl_sys_cd);
        etlDependency.setPre_etl_sys_cd(etl_sys_cd);
        etlDependency.setEtl_job(etl_job);
        saveEtlJobDef(etl_job_def, etlDependency);
    }

    @Method(desc = "分页查询作业定义信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id关联查询进行权限控制" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.每次拼接新sql之前清理原sql以及参数" +
                    "4.判断作业程序类型是否为空，不为空，加条件查询" +
                    "5.判断作业名称是否为空，不为空，加条件查询" +
                    "6.判断作业程序名称是否为空，不为空，加条件查询" +
                    "7.判断任务编号是否为空，不为空，加条件查询" +
                    "8.分页查询作业定义信息" +
                    "9.创建存放分页查询作业定义信息、分页查询总记录数、工程名称的集合并封装数据" +
                    "10.返回分页查询作业定义信息、分页查询总记录数、工程编号、工程名称")
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
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.获取工程名称
        String etl_sys_name = getEtlSysName(etl_sys_cd, getUserId());
        // 3.每次拼接新sql之前清理原sql以及参数
        asmSql.clean();
        asmSql.addSql("select distinct t1.etl_sys_cd,t1.etl_job,t1.etl_job_desc,t1.pro_name,t1.disp_freq," +
                "t1.disp_type,t1.job_eff_flag,t1.upd_time,t1.today_disp,t1.sub_sys_cd,t1.pro_type," +
                "t2.etl_sys_name FROM " + Etl_job_def.TableName + " t1 left join " + Etl_sys.TableName +
                " t2 on t1.etl_sys_cd=t2.etl_sys_cd where t1.etl_sys_cd=? and t2.user_id=?");
        asmSql.addParam(etl_sys_cd);
        asmSql.addParam(getUserId());
        // 4.判断作业程序类型是否为空，不为空，加条件查询
        if (StringUtil.isNotBlank(pro_type)) {
            asmSql.addSql("AND pro_type = ?");
            asmSql.addParam(pro_type);
        }
        // 5.判断作业名称是否为空，不为空，加条件查询
        if (StringUtil.isNotBlank(etl_job)) {
            asmSql.addLikeParam("etl_job", "%" + etl_job + "%");
        }
        // 6.判断作业程序名称是否为空，不为空，加条件查询
        if (StringUtil.isNotBlank(pro_name)) {
            asmSql.addLikeParam("pro_name", "%" + pro_name + "%");
        }
        // 7.判断任务编号是否为空，不为空，加条件查询
        if (StringUtil.isNotBlank(sub_sys_cd)) {
            asmSql.addLikeParam("sub_sys_cd", "%" + sub_sys_cd + "%");
        }
        // 8.分页查询作业定义信息
        Page page = new DefaultPageImpl(currPage, pageSize);
        List<Map<String, Object>> etlJobDefList = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
        // 9.创建存放分页查询作业定义信息、分页查询总记录数、工程编号、工程名称的集合并封装数据
        Map<String, Object> etlJobDefMap = new HashMap<>();
        etlJobDefMap.put("etlJobDefList", etlJobDefList);
        etlJobDefMap.put("totalSize", page.getTotalSize());
        etlJobDefMap.put("etl_sys_name", etl_sys_name);
        etlJobDefMap.put("etl_sys_cd", etl_sys_cd);
        // 10.返回分页查询作业定义信息、分页查询总记录数、工程编号、工程名称
        return etlJobDefMap;
    }

    @Method(desc = "根据工程编号、作业名称查询作业定义信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.判断当前工程下作业是否存在" +
                    "4.返回根据工程编号、作业名称查询作业定义信息，实体字段基本都需要所以查询所有字段")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
    @Return(desc = "返回根据工程编号、作业名称查询作业定义信息", range = "取值范围")
    public Map<String, Object> searchEtlJobDef(String etl_sys_cd, String etl_job) {
        // 1.数据可访问权限处理方式，通过user_id进行权限验证
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.判断当前工程下作业是否存在
        if (!ETLJobUtil.isEtlJobDefExist(etl_sys_cd, etl_job)) {
            throw new BusinessException("当前工程下作业已不存在！");
        }
        // 4.返回根据工程编号、作业名称查询作业定义信息，实体字段基本都需要所以查询所有字段
        return Dbo.queryOneObject("select * from " + Etl_job_def.TableName + " where etl_sys_cd=?" +
                " AND etl_job=?", etl_sys_cd, etl_job);
    }

    @Method(desc = "新增保存作业信息",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
                    "2.验证作业定义字段合法性" +
                    "3.验证当前用户下的工程是否存在" +
                    "4.判断作业名称是否已存在，存在，不能新增" +
                    "5.判断如果作业程序类型是  Thrift 或者 Yarn.则默认分配一条资源使用信息" +
                    "6.判断调度频率是否为频率，根据调度频率不同封装不同属性" +
                    "7.保存资源分配信息" +
                    "8.如果是依赖作业则保存作业依赖信息")
    @Param(name = "etl_job_def", desc = "作业定义实体对象", range = "与数据库对应表字段规则一致", isBean = true)
    @Param(name = "etl_dependency", desc = "作业依赖实体对象", range = "与数据库对应表字段规则一致", isBean = true)
    public void saveEtlJobDef(Etl_job_def etl_job_def,
                              Etl_dependency etl_dependency) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.验证作业定义字段合法性
        checkEtlJobDefField(etl_job_def);
        // 3.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_job_def.getEtl_sys_cd(), getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 4.判断作业名称是否已存在，存在，不能新增
        if (ETLJobUtil.isEtlJobDefExist(etl_job_def.getEtl_sys_cd(), etl_job_def.getEtl_job())) {
            throw new BusinessException("作业名称已存在不能新增!");
        }
        // 5.判断如果作业程序类型是  Thrift 或者 Yarn.则默认分配一条资源使用信息
        isThriftOrYarnProType(etl_job_def.getEtl_sys_cd(), etl_job_def.getEtl_job(),
                etl_job_def.getPro_type());
        // 6.如果是依赖作业则保存作业依赖信息
        if (Dispatch_Type.DEPENDENCE == Dispatch_Type.ofEnumByCode(etl_job_def.getDisp_type())) {
            dependToEtlJob(etl_dependency);
        }
        // 7.判断调度频率是否为频率，根据调度频率不同封装作业定义实体对象的不同属性
        isDispatchFrequency(etl_job_def);
        // 8.新增作业
        etl_job_def.add(Dbo.db());
    }

    @Method(desc = "验证作业定义字段合法性",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.验证作业名称是否合法" +
                    "3.验证任务编号是否合法" +
                    "4.验证工程编号是否合法" +
                    "5.验证作业程序类型是否合法" +
                    "6.验证调度频率是否合法" +
                    "7.验证调度触发方式是否合法" +
                    "8.验证作业有效标志是否合法" +
                    "9.验证当天是否调度是否合法,可为空")
    @Param(name = "etl_job_def", desc = "作业定义实体对象", range = "与数据库对应表字段规则一致", isBean = true)
    private void checkEtlJobDefField(Etl_job_def etl_job_def) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.验证作业名称是否合法
        if (StringUtil.isBlank(etl_job_def.getEtl_job())) {
            throw new BusinessException("作业名称不能为空以及不能为空格！");
        }
        // 3.验证任务编号是否合法
        if (StringUtil.isBlank(etl_job_def.getSub_sys_cd())) {
            throw new BusinessException("任务编号不能为空以及不能为空格！");
        }
        // 4.验证工程编号是否合法
        if (StringUtil.isBlank(etl_job_def.getEtl_sys_cd())) {
            throw new BusinessException("工程编号不能为空以及不能为空格！");
        }
        // 5.验证作业程序类型是否合法
        Pro_Type.ofEnumByCode(etl_job_def.getPro_type());
        // 6.验证调度频率是否合法
        Dispatch_Frequency.ofEnumByCode(etl_job_def.getDisp_freq());
        // 7.验证调度触发方式是否合法
        Dispatch_Type.ofEnumByCode(etl_job_def.getDisp_type());
        // 8.验证作业有效标志是否合法
        Job_Effective_Flag.ofEnumByCode(etl_job_def.getJob_eff_flag());
        // 9.验证当天是否调度是否合法,可为空
        if (StringUtil.isNotBlank(etl_job_def.getToday_disp())) {
            Today_Dispatch_Flag.ofEnumByCode(etl_job_def.getToday_disp());
        }
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
            if (Dbo.queryNumber("SELECT count(1) FROM " + Etl_job_resource_rela.TableName +
                    " WHERE etl_job=? AND etl_sys_cd=?", etl_job, etl_sys_cd).orElseThrow(() ->
                    new BusinessException("sql查询错误")) == 0) {
                // 不存在资源分配,保存资源分配信息
                saveEtlJobResourceRelation(etlJobResourceRela);
            } else {
                // 存在,则更新资源分配
                updateEtlResourceRelation(etlJobResourceRela);
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
                    "2.判断工程是否存在" +
                    "3.检测当前作业分配的占用资源数是否过大" +
                    "4.更新保存资源分配信息")
    @Param(name = "jobResourceRelation", desc = "资源使用表实体对象", range = "与数据库表定义规则一致", isBean = true)
    public void updateEtlResourceRelation(Etl_job_resource_rela jobResourceRelation) {
        // TODO 字段验证应该有一个统一的工具类
        // 1.数据可访问权限处理方式，通过user_id进行权限验证
        // 2.判断工程是否存在
        if (!ETLJobUtil.isEtlSysExist(jobResourceRelation.getEtl_sys_cd(), getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.检测当前作业分配的占用资源数是否过大
        ETLJobUtil.isResourceDemandTooLarge(jobResourceRelation.getEtl_sys_cd(), jobResourceRelation.getResource_type(),
                jobResourceRelation.getResource_req());
        // 4.更新保存资源分配信息
        jobResourceRelation.update(Dbo.db());
    }

    @Method(desc = "保存资源分配信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
                    "2.判断工程是否存在" +
                    "3.新增时.检测当前作业是否已经分配过资源" +
                    "4.检测当前作业分配的占用资源数是否过大" +
                    "5.新增保存资源分配信息")
    @Param(name = "jobResourceRelation", desc = "资源使用表实体对象", range = "与数据库表定义规则一致", isBean = true)
    public void saveEtlJobResourceRelation(Etl_job_resource_rela jobResourceRelation) {
        // TODO 字段验证应该有一个统一的工具类
        // 1.数据可访问权限处理方式，通过user_id进行权限验证
        // 2.判断工程是否存在
        if (!ETLJobUtil.isEtlSysExist(jobResourceRelation.getEtl_sys_cd(), getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.新增时.检测当前作业是否已经分配过资源
        if (ETLJobUtil.isEtlJobResourceRelationExist(jobResourceRelation.getEtl_sys_cd(),
                jobResourceRelation.getEtl_job())) {
            throw new BusinessException("当前工程对应作业资源分配信息已存在，不能新增！");
        }
        // 4.检测当前作业分配的占用资源数是否过大
        ETLJobUtil.isResourceDemandTooLarge(jobResourceRelation.getEtl_sys_cd(),
                jobResourceRelation.getResource_type(), jobResourceRelation.getResource_req());
        // 5.新增保存资源分配信息
        jobResourceRelation.add(Dbo.db());
    }

    @Method(desc = "分页查询作业资源分配信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
                    "2.判断工程是否存在" +
                    "3.获取工程名称" +
                    "4.判断作业名称是否为空，不为空加条件查询" +
                    "5.判断参数类型是否为空，不为空加条件查询" +
                    "6.分页查询作业资源分配信息" +
                    "7.创建存放分页查询资源分配信息、分页查询总记录数、工程名称的集合并封装数据" +
                    "8.返回分页查询资源分配信息、分页查询总记录数、工程名称")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成", nullable = true)
    @Param(name = "resource_type", desc = "参数类型", range = "新增参数时生成", nullable = true)
    @Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
    @Return(desc = "返回存放分页查询资源分配信息、分页查询总记录数、工程编号、工程名称的集合", range = "无限制")
    public Map<String, Object> searchEtlJobResourceRelaByPage(String etl_sys_cd, String etl_job,
                                                              String resource_type, int currPage,
                                                              int pageSize) {
        // 1.数据可访问权限处理方式，通过user_id进行权限验证
        // 2.判断工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.获取工程名称
        String etl_sys_name = getEtlSysName(etl_sys_cd, getUserId());
        asmSql.clean();
        asmSql.addSql("select distinct * from " + Etl_job_resource_rela.TableName + " where etl_sys_cd=? ");
        asmSql.addParam(etl_sys_cd);
        // 4.判断作业名称是否为空，不为空加条件查询
        if (StringUtil.isNotBlank(etl_job)) {
            asmSql.addLikeParam("etl_job", "%" + etl_job + "%");
        }
        // 5.判断参数类型是否为空，不为空加条件查询
        if (StringUtil.isNotBlank(resource_type)) {
            asmSql.addLikeParam("resource_type", "%" + resource_type + "%");
        }
        asmSql.addSql("order by etl_sys_cd,etl_job");
        Page page = new DefaultPageImpl(currPage, pageSize);
        // 6.分页查询作业资源分配信息
        List<Map<String, Object>> resourceRelation = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
        // 7.创建存放分页查询资源分配信息、分页查询总记录数、工程编号、工程名称的集合并封装数据
        Map<String, Object> resourceRelationMap = new HashMap<>();
        resourceRelationMap.put("jobResourceRelation", resourceRelation);
        resourceRelationMap.put("totalSize", page.getTotalSize());
        resourceRelationMap.put("etl_sys_name", etl_sys_name);
        resourceRelationMap.put("etl_sys_cd", etl_sys_cd);
        // 8.返回分页查询资源分配信息、分页查询总记录数、工程编号、工程名称
        return resourceRelationMap;
    }

    @Method(desc = "根据工程编号、作业名称查询作业资源分配情况",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
                    "2.判断工程是否存在" +
                    "3.判断当前工程下作业资源使用情况是否存在" +
                    "4.返回根据工程编号、作业名称查询作业资源分配情况，实体字段基本都需要所以查询所有字段")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
    @Return(desc = "返回根据工程编号、作业名称查询作业资源分配情况，实体字段基本都需要所以查询所有字段", range = "无限制")
    public Map<String, Object> searchEtlJobResourceRelation(String etl_sys_cd, String etl_job) {
        // 1.数据可访问权限处理方式，通过user_id进行权限验证
        // 2.判断工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.判断当前工程下作业资源使用情况是否存在
        if (!ETLJobUtil.isEtlJobResourceRelationExist(etl_sys_cd, etl_job)) {
            throw new BusinessException("当前工程对应作业资源分配信息不存在！");
        }
        // 4.返回根据工程编号、作业名称查询作业资源分配情况，实体字段基本都需要所以查询所有字段
        return Dbo.queryOneObject("select * from " + Etl_job_resource_rela.TableName +
                " where etl_sys_cd=? AND etl_job=?", etl_sys_cd, etl_job);
    }

    @Method(desc = "保存作业添加时的作业依赖",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
                    "2.判断依赖作业是否存在，存在不能新增，不存在则新增")
    @Param(name = "etl_dependency", desc = "作业依赖实体对象", range = "与数据库对应表字段规则一致", isBean = true)
    private void dependToEtlJob(Etl_dependency etl_dependency) {
        // 1.数据可访问权限处理方式，该方法不需要权限验证
        // 2.判断依赖作业是否存在，存在不能新增，不存在则新增
        if (StringUtil.isNotBlank(etl_dependency.getPre_etl_job())) {
            if (Dbo.queryNumber("select count(1) from " + Etl_dependency.TableName +
                            " where etl_sys_cd=? and etl_job=? and pre_etl_sys_cd=? AND pre_etl_job = ?",
                    etl_dependency.getEtl_sys_cd(), etl_dependency.getEtl_job(),
                    etl_dependency.getPre_etl_sys_cd(), etl_dependency.getPre_etl_job())
                    .orElseThrow(() -> new BusinessException("sql查询错误")) > 0) {
                throw new BusinessException("该工程下该作业对应的依赖作业已存在，不能再次依赖!");
            }
            etl_dependency.add(Dbo.db());
        }
    }

    @Method(desc = "更新作业定义信息并返回更新后的最新的作业信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.判断调度触发方式改变时，修改前的调度触发方式是依赖还是定时" +
                    "3.1修改前的调度触发方式是依赖，判断调度触发方式改变时，修改后的调度触发方式是依赖还是定时" +
                    "3.1.1调度触发方式改变时，修改后的调度触发方式是依赖，依赖关系发生变化，现在只是更改依赖（依赖-依赖）" +
                    "3.1.2调度触发方式改变时，修改后的调度方式是定时（依赖-定时），直接删除原依赖关系" +
                    "3.2调度触发方式改变时，修改前的调度触发方式是定时,判断修改后的调度方式为依赖还是定时" +
                    "3.2.1调度触发方式改变时，修改后的调度触发方式定时  将定时更改为依赖,则新增，（定时---->依赖）" +
                    "4.判断作业程序类型是否为yarn或者thrift类型，如果是，进行资源分配处理" +
                    "5.根据调度频率不同封装作业定义实体对象的不同属性" +
                    "6.保存更新的作业信息")
    @Param(name = "etl_job_def", desc = "作业定义实体对象", range = "与数据库对应表字段规则一致", isBean = true)
    @Param(name = "etl_dependency", desc = "作业依赖实体对象", range = "与数据库对应表字段规则一致", isBean = true)
    @Param(name = "old_pre_etl_job", desc = "作业依赖变动时,修改前的上游作业名称", range = "无限制")
    @Param(name = "old_dispatch_type", desc = "调度触发方式改变时，修改前的调度触发方式",
            range = "使用调度触发方式代码项（DispatchType）")
    public void updateEtlJobDef(Etl_job_def etl_job_def, Etl_dependency etl_dependency,
                                String old_pre_etl_job, String old_dispatch_type) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_job_def.getEtl_sys_cd(), getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.判断调度触发方式改变时，修改前的调度触发方式是依赖还是定时
        if (Dispatch_Type.DEPENDENCE == Dispatch_Type.ofEnumByCode(old_dispatch_type)) {
            // 3.1修改前的调度触发方式是依赖，判断调度触发方式改变时，修改后的调度触发方式是依赖还是定时
            if (Dispatch_Type.DEPENDENCE == Dispatch_Type.ofEnumByCode(etl_job_def.getDisp_type())) {
                // 3.1.1调度触发方式改变时，修改后的调度触发方式是依赖，依赖关系发生变化，现在只是更改依赖（依赖-依赖）
                if (!old_pre_etl_job.equals(etl_dependency.getPre_etl_job())) {
                    DboExecute.updatesOrThrow("更新作业时更新依赖失败！", "update "
                                    + Etl_dependency.TableName + " set etl_job=?,pre_etl_sys_cd=?," +
                                    " pre_etl_job=?,status=? where etl_job=? AND etl_sys_cd=? and " +
                                    " pre_etl_job=?", etl_dependency.getEtl_job(),
                            etl_dependency.getPre_etl_sys_cd(), etl_dependency.getPre_etl_job(),
                            etl_dependency.getStatus(), etl_dependency.getEtl_job(),
                            etl_dependency.getEtl_sys_cd(), old_pre_etl_job);
                }
            } else {
                // 3.1.2调度触发方式改变时，修改后的调度方式是定时（依赖-定时），直接删除原依赖关系
                DboExecute.deletesOrThrow("作业编辑为定时，删除依赖失败!", "DELETE FROM "
                                + Etl_dependency.TableName + " WHERE etl_sys_cd = ? AND etl_job = ? " +
                                "AND pre_etl_job = ?", etl_dependency.getEtl_sys_cd(),
                        etl_dependency.getEtl_job(), old_pre_etl_job);
            }
        } else {
            // 3.2调度触发方式改变时，修改前的调度触发方式是定时,判断修改后的调度方式为依赖还是定时
            if (Dispatch_Type.DEPENDENCE == Dispatch_Type.ofEnumByCode(etl_job_def.getDisp_type())) {
                // 3.2.1调度触发方式改变时，修改后的调度触发方式定时  将定时更改为依赖,则新增，（定时---->依赖）
                dependToEtlJob(etl_dependency);
            }
        }
        // 4.判断作业程序类型是否为yarn或者thrift类型，如果是，进行资源分配处理
        isThriftOrYarnProType(etl_job_def.getEtl_sys_cd(), etl_job_def.getEtl_job(),
                etl_job_def.getPro_type());
        // 5.根据调度频率不同封装作业定义实体对象的不同属性
        isDispatchFrequency(etl_job_def);
        // 6.保存更新的作业信息
        etl_job_def.setUpd_time(DateUtil.parseStr2DateWith8Char(DateUtil.getSysDate()) + " " +
                DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime()));
        etl_job_def.update(Dbo.db());
    }

    @Method(desc = "批量删除Etl作业定义信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.遍历所有要删除的作业名称" +
                    "4.循环删除作业" +
                    "5.作业被删除的同时删除作业的资源分配情况,只有有资源分配才需要删除" +
                    "6.作业被删除的同时删除依赖作业，只有有作业依赖关系才需要删除")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "etl_job", desc = "作业名称的数组", range = "无限制")
    public void batchDeleteEtlJobDef(String etl_sys_cd, String[] etl_job) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.遍历所有要删除的作业名称
        for (String etlJob : etl_job) {
            // 4.循环删除作业
            DboExecute.deletesOrThrow("删除作业信息失败，etl_sys_cd=" + etl_sys_cd + ",etl_job="
                    + etlJob, "delete from " + Etl_job_def.TableName + " where etl_sys_cd=?" +
                    " and etl_job=?", etl_sys_cd, etl_job);
            // 5.作业被删除的同时删除作业的资源分配情况,只有有资源分配才需要删除
            deleteJobResourceRelationIfExist(etl_sys_cd, etlJob);
            // 6.作业被删除的同时删除依赖作业，只有有作业依赖关系才需要删除
            deleteJobDependencyIfExist(etl_sys_cd, etlJob);
        }
    }

    @Method(desc = "删除Etl作业定义信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.删除作业信息" +
                    "4.作业被删除的同时删除作业的资源分配情况，只有有资源分配的作业才需要删除" +
                    "5.作业被删除的同时删除依赖作业，只有有依赖关系的作业才需要删除")
    @Param(name = "etl_sys_cd", desc = "工程代码", range = "新增工程时生成")
    @Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
    @Param(name = "batchEtlJob", desc = "批量作业编号", range = "无限制", nullable = true)
    public void deleteEtlJobDef(String etl_sys_cd, String etl_job) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.删除作业信息
        DboExecute.deletesOrThrow("删除作业信息失败，etl_sys_cd=" + etl_sys_cd + ",etl_job="
                + etl_job, "delete from " + Etl_job_def.TableName + " where etl_sys_cd=?" +
                " and etl_job=?", etl_sys_cd, etl_job);
        // 4.作业被删除的同时删除作业的资源分配情况，只有有资源分配的作业才需要删除
        deleteJobResourceRelationIfExist(etl_sys_cd, etl_job);
        // 5.作业被删除的同时删除依赖作业，只有有依赖关系的作业才需要删除
        deleteJobDependencyIfExist(etl_sys_cd, etl_job);
    }

    @Method(desc = "方法描述",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.判断当前作业是否有作业依赖关系" +
                    "3.删除作业的依赖关系，只有有作业依赖关系的作业才需要删除")
    @Param(name = "etl_sys_cd", desc = "工程代码", range = "新增工程时生成")
    @Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
    private void deleteJobDependencyIfExist(String etl_sys_cd, String etl_job) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.判断当前作业是否有作业依赖关系
        if (Dbo.queryNumber("select count(*) from " + Etl_dependency.TableName + " where etl_sys_cd=?" +
                " and etl_job=?", etl_sys_cd, etl_job).orElseThrow(() ->
                new BusinessException("sql查询错误")) > 0) {
            // 3.删除作业的依赖关系，只有有作业依赖关系的作业才需要删除
            DboExecute.deletesOrThrow("删除作业依赖失败，etl_sys_cd=" + etl_sys_cd +
                    ",etl_job=" + etl_job, "delete from " + Etl_dependency.TableName +
                    " where etl_sys_cd=? AND etl_job=?", etl_sys_cd, etl_job);
        }
    }

    @Method(desc = "方法描述",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限控制" +
                    "2.判断当前作业是否有资源分配情况" +
                    "3.删除作业的资源分配情况，只有有资源分配的作业才需要删除")
    @Param(name = "etl_sys_cd", desc = "工程代码", range = "新增工程时生成")
    @Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
    private void deleteJobResourceRelationIfExist(String etl_sys_cd, String etl_job) {
        // 1.数据可访问权限处理方式，该方法不需要权限控制
        // 2.判断当前作业是否有资源分配情况
        if (Dbo.queryNumber("select count(*) from " + Etl_job_resource_rela.TableName +
                " where etl_sys_cd=? and etl_job=?", etl_sys_cd, etl_job).orElseThrow(() ->
                new BusinessException("sql查询错误")) > 0) {
            // 3.删除作业的资源分配情况，只有有资源分配的作业才需要删除
            DboExecute.deletesOrThrow("删除资源分配信息失败，etl_sys_cd=" + etl_sys_cd +
                    ",etl_job=" + etl_job, "delete from " + Etl_job_resource_rela.TableName +
                    " where etl_sys_cd =? AND etl_job = ?", etl_sys_cd, etl_job);
        }
    }

    @Method(desc = "根据工程编号，作业名称批量删除Etl作业资源关系",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.遍历获取所有作业名称" +
                    "4.循环删除作业资源分配信息")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "etl_job", desc = "作业名称的数组", range = "无限制")
    public void batchDeleteEtlJobResourceRelation(String etl_sys_cd, String[] etl_job) {
        // 1.数据可访问权限处理方式，该方法不需要权限验证
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.遍历获取所有作业名称
        for (String etlJob : etl_job) {
            // 4.循环删除作业资源分配信息
            DboExecute.deletesOrThrow("删除资源分配信息失败，etl_sys_cd=" + etl_sys_cd +
                    ",etl_job=" + etlJob, "delete from " + Etl_job_resource_rela.TableName +
                    " where etl_sys_cd =? and etl_job=?", etl_sys_cd, etlJob);
        }
    }

    @Method(desc = "根据工程编号，作业名称删除Etl作业资源关系",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.删除资源分配信息")
    @Param(name = "etl_sys_cd", desc = "工程代码", range = "新增工程时生成")
    @Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
    public void deleteEtlJobResourceRelation(String etl_sys_cd, String etl_job) {
        // 1.数据可访问权限处理方式，该方法不需要权限验证
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.删除资源分配信息
        DboExecute.deletesOrThrow("删除资源分配信息失败，etl_sys_cd=" + etl_sys_cd +
                ",etl_job=" + etl_job, "delete from " + Etl_job_resource_rela.TableName +
                " where etl_sys_cd =? AND etl_job = ?", etl_sys_cd, etl_job);
    }

    @Method(desc = "分页查询etl资源定义信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.根据工程编号查询工程名称" +
                    "4.判断资源类型是否存在，存在加条件查询" +
                    "5.分页查询资源信息" +
                    "6.创建存放分页查询资源信息、分页查询总记录数、工程编号、工程名称的集合并封装数据" +
                    "7.返回分页查询资源信息、分页查询总记录数、工程编号、工程名称")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "resource_type", desc = "资源类型", range = "无限制", nullable = true)
    @Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
    @Return(desc = "分页查询资源信息、分页查询总记录数、工程编号、工程名称", range = "无限制")
    public Map<String, Object> searchEtlResourceByPage(String etl_sys_cd, String resource_type,
                                                       int currPage, int pageSize) {
        // 1.数据可访问权限处理方式，通过user_id进行权限验证
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.根据工程编号查询工程名称
        String etl_sys_name = getEtlSysName(etl_sys_cd, getUserId());
        asmSql.addSql("select distinct * from " + Etl_resource.TableName + " where etl_sys_cd = ?");
        asmSql.addParam(etl_sys_cd);
        // 4.判断资源类型是否存在，存在加条件查询
        if (StringUtil.isNotBlank(resource_type)) {
            asmSql.addLikeParam("resource_type", "%" + resource_type + "%");
        }
        asmSql.addSql(" order by etl_sys_cd,resource_type");
        // 5.分页查询资源信息
        Page page = new DefaultPageImpl(currPage, pageSize);
        List<Map<String, Object>> etlResourceList = Dbo.queryPagedList(page, asmSql.sql(),
                asmSql.params());
        // 6.创建存放分页查询资源信息、分页查询总记录数，工程编号、工程名称的集合并封装数据
        Map<String, Object> etlResourceMap = new HashMap<>();
        etlResourceMap.put("etlResourceList", etlResourceList);
        etlResourceMap.put("totalSize", page.getTotalSize());
        etlResourceMap.put("etl_sys_name", etl_sys_name);
        etlResourceMap.put("etl_sys_cd", etl_sys_cd);
        // 7.返回分页查询资源信息、分页查询总记录数，工程编号、工程名称
        return etlResourceMap;
    }

    @Method(desc = "根据工程编号、资源类型查询资源定义信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.判断当前工程下资源定义信息是否存在" +
                    "4.返回根据工程编号、资源类型查询资源定义信息,实体字段基本都需要所以查询所有字段")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "resource_type", desc = "资源类型", range = "新增资源时生成")
    @Return(desc = "返回根据工程编号、资源类型查询资源定义信息", range = "无限制")
    public Map<String, Object> searchEtlResource(String etl_sys_cd, String resource_type) {
        // 1.数据可访问权限处理方式，通过user_id进行权限验证
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.判断当前工程下资源定义信息是否存在
        if (ETLJobUtil.isEtlResourceExist(etl_sys_cd, resource_type)) {
            throw new BusinessException("当前工程对应的资源已不存在！");
        }
        // 4.返回根据工程编号、资源类型查询资源定义信息,实体字段基本都需要所以查询所有字段
        return Dbo.queryOneObject("select * from " + Etl_resource.TableName + " where etl_sys_cd=?" +
                " AND resource_type=?", etl_sys_cd, resource_type);
    }

    @Method(desc = "新增保存etl资源定义信息并返回最新资源信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
                    "2.判断当前工程是否还存在" +
                    "3.确认新增的资源不存在，存在不能新增" +
                    "4.新增资源定义信息")
    @Param(name = "etl_resource", desc = "etl_resource表实体对象", range = "与数据库对应表定义规则一致",
            isBean = true)
    public void saveEtlResource(Etl_resource etl_resource) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.判断当前工程是否还存在
        if (!ETLJobUtil.isEtlSysExist(etl_resource.getEtl_sys_cd(), getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.确认新增的资源不存在，存在不能新增
        if (ETLJobUtil.isEtlResourceExist(etl_resource.getEtl_sys_cd(), etl_resource.getResource_type())) {
            throw new BusinessException("当前工程对应的资源已存在,不能新增！");
        }
        // 目前的服务器同步标志先使用默认的同步
        etl_resource.setMain_serv_sync(Main_Server_Sync.YES.getCode());
        // 4.新增资源定义信息
        etl_resource.add(Dbo.db());
    }

    @Method(desc = "更新资源信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
                    "2.判断当前工程是否还存在" +
                    "3.确认要更新的资源存在" +
                    "4.更新资源信息")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "resource_type", desc = "资源类型", range = "新增资源时生成")
    @Param(name = "resource_max", desc = "资源阀值", range = "大于0的正整数")
    public void updateEtlResource(String etl_sys_cd, String resource_type,
                                  String resource_max) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.判断当前工程是否还存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.确认要更新的资源存在
        if (Dbo.queryNumber("SELECT count(1) FROM " + Etl_resource.TableName + " WHERE resource_type=?"
                + " AND etl_sys_cd=?", resource_type, etl_sys_cd).orElseThrow(() ->
                new BusinessException("sql查询错误！")) == 0) {
            throw new BusinessException("当前工程对应的资源已不存在！");
        }
        // 4.更新资源信息
        DboExecute.updatesOrThrow("更新资源失败，etl_sys_cd=" + etl_sys_cd + ",resource_type="
                + resource_type, "update " + Etl_resource.TableName + " set resource_max=? " +
                " where etl_sys_cd=? and resource_type=?", etl_sys_cd, resource_type);
    }

    @Method(desc = "批量删除作业资源定义",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.获取所有要删除的资源类型" +
                    "4.遍历所有资源类型" +
                    "5.循环删除作业资源定义信息")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "resource_type", desc = "资源类型的数组", range = "无限制")
    public void batcheDeleteEtlResource(String etl_sys_cd, String[] resource_type) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.遍历所有资源类型
        for (String resourceType : resource_type) {
            // 4.循环删除作业资源定义信息
            DboExecute.deletesOrThrow("删除作业系统参数失败，etl_sys_cd=" + etl_sys_cd +
                    ",para_cd=" + resourceType, "delete from " + Etl_resource.TableName +
                    " where etl_sys_cd = ? AND resource_type = ?", etl_sys_cd, resourceType);
        }
    }

    @Method(desc = "删除作业资源定义",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限控制" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.删除作业资源定义")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "resource_type", desc = "变量名称", range = "新增作业系统参数时生成")
    public void deleteEtlResource(String etl_sys_cd, String resource_type) {
        // 1.数据可访问权限处理方式，通过user_id进行权限控制
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.删除作业资源定义
        DboExecute.deletesOrThrow("删除作业系统参数失败，etl_sys_cd=" + etl_sys_cd +
                ",resource_type=" + resource_type, "delete from " + Etl_resource.TableName +
                " where etl_sys_cd = ? AND resource_type = ?", etl_sys_cd, resource_type);
    }

    @Method(desc = "分页查询作业调度系统参数信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.根据工程编号查询工程名称" +
                    "4.判断变量名称是否存在，存在加条件查询" +
                    "5.分页查询系统参数信息" +
                    "6.创建存放分页查询系统参数信息、分页查询总记录数、工程编号、工程名称的集合并封装数据" +
                    "7.返回分页查询系统参数信息、分页查询总记录数、工程编号、工程名称")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "para_cd", desc = "变量名称", range = "新增作业系统参数时生成", nullable = true)
    @Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
    @Return(desc = "分页查询系统参数信息、分页查询总记录数、工程编号、工程名称", range = "取值范围")
    public Map<String, Object> searchEtlParaByPage(String etl_sys_cd, String para_cd, int currPage, int pageSize) {
        // 1.数据可访问权限处理方式，通过user_id进行权限验证
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.根据工程编号查询工程名称
        String etl_sys_name = getEtlSysName(etl_sys_cd, getUserId());
        asmSql.clean();
        asmSql.addSql("select distinct * from " + Etl_para.TableName + " where etl_sys_cd IN (?,?)");
        asmSql.addParam(etl_sys_cd);
        // 默认系统参数，目前写死
        asmSql.addParam("SYS");
        // 4.判断变量名称是否存在，存在加条件查询
        if (StringUtil.isNotBlank(para_cd)) {
            asmSql.addLikeParam("para_cd", "%" + para_cd + "%");
        }
        asmSql.addSql(" order by etl_sys_cd,para_cd");
        Page page = new DefaultPageImpl(currPage, pageSize);
        // 5.分页查询系统参数信息
        List<Map<String, Object>> etlParaList = Dbo.queryPagedList(page, asmSql.sql(),
                asmSql.params());
        // 6.创建存放分页查询系统参数信息、分页查询总记录数、工程编号、工程名称的集合并封装数据
        Map<String, Object> etlParaMap = new HashMap<>();
        etlParaMap.put("etlParaList", etlParaList);
        etlParaMap.put("totalSize", page.getTotalSize());
        etlParaMap.put("etl_sys_name", etl_sys_name);
        etlParaMap.put("etl_sys_cd", etl_sys_cd);
        // 7.返回分页查询系统参数信息、分页查询总记录数、工程编号、工程名称
        return etlParaMap;
    }

    @Method(desc = "新增保存作业系统参数",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
                    "2.字段合法性验证" +
                    "3.验证当前用户下的工程是否存在" +
                    "4.系统参数变量名称需要拼接前缀！" +
                    "5.判断作业系统参数变量名称是否已存在" +
                    "6.保存作业系统参数")
    @Param(name = "etl_para", desc = "作业系统参数实体对象", range = "与数据库对应表字段规则一致", isBean = true)
    public void saveEtlPara(Etl_para etl_para) {
        // 1.数据可访问权限处理方式，通过user_id进行权限验证
        // 2.字段合法性验证
        checkEtlParaField(etl_para);
        // 3.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_para.getEtl_sys_cd(), getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 4.系统参数变量名称需要拼接前缀！
        String para_cd = PREFIX + etl_para.getPara_cd();
        // 5.判断作业系统参数变量名称是否已存在
        if (ETLJobUtil.isEtlParaExist(etl_para.getEtl_sys_cd(), para_cd)) {
            throw new BusinessException("作业系统参数变量名称已存在,不能新增！");
        }
        etl_para.setPara_cd(para_cd);
        // 6.保存作业系统参数
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
        if (StringUtil.isBlank(etl_para.getEtl_sys_cd())) {
            throw new BusinessException("etl_sys_cd不能为空以及空格！");
        }
        // 3.验证para_cd合法性
        if (StringUtil.isBlank(etl_para.getPara_cd())) {
            throw new BusinessException("para_cd不能为空以及空格！");
        }
        // 4.验证para_val合法性
        if (StringUtil.isBlank(etl_para.getPara_val())) {
            throw new BusinessException("para_val不能为空以及空格！");
        }
        // 5.验证para_type合法性
        ParamType.ofEnumByCode(etl_para.getPara_type());
    }

    @Method(desc = "更新保存作业系统参数",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
                    "2.字段合法性验证" +
                    "3.验证当前用户下的工程是否存在" +
                    "4.更新作业系统参数")
    @Param(name = "etl_para", desc = "作业系统参数实体对象", range = "与数据库对应表字段规则一致")
    public void updateEtlPara(Etl_para etl_para) {
        // 1.数据可访问权限处理方式，通过user_id进行权限验证
        // 2.字段合法性验证
        checkEtlParaField(etl_para);
        // 3.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_para.getEtl_sys_cd(), getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 4.更新作业系统参数
        etl_para.update(Dbo.db());
    }

    @Method(desc = "根据工程编号、变量名称查询作业系统参数",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.判断当前工程下作业系统参数是否存在" +
                    "4.返回根据工程编号、变量名称查询作业系统参数，实体字段基本都需要所以查询所有字段")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "para_cd", desc = "变量名称", range = "新增作业系统参数时生成")
    @Return(desc = "返回根据工程编号、变量名称查询作业系统参数信息", range = "取值范围")
    public Map<String, Object> searchEtlPara(String etl_sys_cd, String para_cd) {
        // 1.数据可访问权限处理方式，通过user_id进行权限验证
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.判断当前工程下作业系统参数是否存在
        if (!ETLJobUtil.isEtlParaExist(etl_sys_cd, para_cd)) {
            throw new BusinessException("作业系统参数已不存在，可能被删除！");
        }
        // 4.返回根据工程编号、变量名称查询作业系统参数，实体字段基本都需要所以查询所有字段
        return Dbo.queryOneObject("select * from " + Etl_para.TableName + " where etl_sys_cd=?" +
                " AND para_cd=?", etl_sys_cd, para_cd);
    }

    @Method(desc = "批量删除作业系统参数",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.遍历所有系统参数的变量名称" +
                    "4.循环删除作业系统参数")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "para_cd", desc = "变量名称的数组", range = "无限制")
    public void batchDeleteEtlPara(String etl_sys_cd, String[] para_cd) {
        // 1.数据可访问权限处理方式，该方法不需要权限验证
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.遍历所有系统参数的变量名称
        for (String paraCd : para_cd) {
            // 4.循环删除作业系统参数
            DboExecute.deletesOrThrow("删除作业系统参数失败，etl_sys_cd=" + etl_sys_cd +
                    ",para_cd=" + paraCd, "delete from " + Etl_para.TableName +
                    " where etl_sys_cd = ? AND para_cd = ?", etl_sys_cd, paraCd);
        }
    }

    @Method(desc = "删除作业系统参数",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.删除作业系统参数")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "para_cd", desc = "变量名称", range = "新增作业系统参数时生成")
    public void deleteEtlPara(String etl_sys_cd, String para_cd) {
        // 1.数据可访问权限处理方式，该方法不需要权限验证
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.删除作业系统参数
        DboExecute.deletesOrThrow("删除作业系统参数失败，etl_sys_cd=" + etl_sys_cd +
                ",para_cd=" + para_cd, "delete from " + Etl_para.TableName +
                " where etl_sys_cd = ? AND para_cd = ?", etl_sys_cd, para_cd);
    }

    @Method(desc = "分页查询作业依赖信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.获取工程名称" +
                    "4.判断作业名称是否为空，不为空加条件查询" +
                    "5.判断上游作业名称是否为空，不为空加条件查询" +
                    "6.分页查询作业依赖信息" +
                    "7.创建存放分页查询作业依赖信息、分页查询总记录数、工程名称的集合并封装数据" +
                    "8.返回分页查询作业依赖信息、分页查询总记录数、工程编号、工程名称")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成", nullable = true)
    @Param(name = "pre_etl_job", desc = "上游作业名称", range = "新增作业依赖时生成", nullable = true)
    @Param(name = "currPage", desc = "分页查询当前页", range = "大于0的正整数", valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示记录数", range = "大于0的正整数", valueIfNull = "10")
    @Return(desc = "存放分页查询作业依赖信息、分页查询总记录数、工程编号、工程名称的集合", range = "无限制")
    public Map<String, Object> searchEtlDependencyByPage(String etl_sys_cd, String etl_job, String pre_etl_job,
                                                         int currPage, int pageSize) {
        // 1.数据可访问权限处理方式，通过user_id进行权限验证
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.获取工程名称
        String etl_sys_name = getEtlSysName(etl_sys_cd, getUserId());
        asmSql.clean();
        asmSql.addSql("select distinct * from etl_dependency where etl_sys_cd = ? ");
        asmSql.addParam(etl_sys_cd);
        // 4.判断作业名称是否为空，不为空加条件查询
        if (StringUtil.isNotBlank(etl_job)) {
            asmSql.addLikeParam("etl_job", "%" + etl_job + "%");
        }
        // 5.判断上游作业名称是否为空，不为空加条件查询
        if (StringUtil.isNotBlank(pre_etl_job)) {
            asmSql.addLikeParam("pre_etl_job", "%" + etl_job + "%");
        }
        asmSql.addSql(" order by etl_sys_cd,etl_job");
        Page page = new DefaultPageImpl(currPage, pageSize);
        // 6.分页查询作业依赖信息
        List<Map<String, Object>> etlDependencyList = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
        // 7.创建存放分页查询作业依赖信息、分页查询总记录数、工程编号、工程名称的集合并封装数据
        Map<String, Object> etlDependencyMap = new HashMap<>();
        etlDependencyMap.put("etlDependencyList", etlDependencyList);
        etlDependencyMap.put("totalSize", page.getTotalSize());
        etlDependencyMap.put("etl_sys_name", etl_sys_name);
        etlDependencyMap.put("etl_sys_cd", etl_sys_cd);
        // 8.返回分页查询作业依赖信息、分页查询总记录数、工程编号、工程名称
        return etlDependencyMap;
    }

    @Method(desc = "根据工程编号查询作业依赖信息",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.判断当前工程下的依赖作业是否存在" +
                    "4.返回根据工程编号查询作业依赖信息,实体字段基本都需要所以查询所有字段")
    @Param(name = "etl_sys_cd", desc = "工程编号", range = "新增工程时生成")
    @Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
    @Param(name = "pre_etl_job", desc = "上游作业名称", range = "新增作业依赖时生成")
    @Return(desc = "返回根据工程编号查询作业依赖信息，实体字段基本都需要所以查询所有字段", range = "无限制")
    public Map<String, Object> searchEtlDependency(String etl_sys_cd, String etl_job, String pre_etl_job) {
        // 1.数据可访问权限处理方式，通过user_id进行权限验证
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.判断当前工程下的依赖作业是否存在
        if (!ETLJobUtil.isEtlDependencyExist(etl_sys_cd, etl_sys_cd, etl_job, pre_etl_job)) {
            throw new BusinessException("当前工程对应作业的依赖不存在！");
        }
        // 4.返回根据工程编号查询作业依赖信息,实体字段基本都需要所以查询所有字段
        return Dbo.queryOneObject("select * from " + Etl_dependency.TableName + " where etl_sys_cd=?" +
                " AND etl_job=? and pre_etl_job=?", etl_sys_cd, etl_job, pre_etl_job);
    }

    @Method(desc = "新增保存作业依赖",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.判断当前依赖是否已存在，如果存在则不能新增" +
                    "4.将当前作业与上游作业交换，看是否已经存在依赖关系" +
                    "5.新增保存作业依赖")
    @Param(name = "etl_dependency", desc = "作业系统参数实体对象", range = "与数据库对应表字段规则一致", isBean = true)
    public void saveEtlDependency(Etl_dependency etl_dependency) {
        // 1.数据可访问权限处理方式，通过user_id进行权限验证
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_dependency.getEtl_sys_cd(), getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
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
        // 5.新增保存作业依赖
        etl_dependency.add(Dbo.db());
    }

    @Method(desc = "批量新增保存作业依赖",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.获取当前任务下的所有作业" +
                    "4.获取上游任务下的所有作业即上游作业" +
                    "5.双重循环添加不同任务下的所有作业之间的依赖" +
                    "6.判断当前作业下的依赖是否存在，存在就跳过，不存在则依赖" +
                    "7.如果当前作业为定时作业则跳过" +
                    "8.将当前作业与上游作业交换，看是否已经存在依赖关系,存在则跳过" +
                    "9.循环保存作业依赖关系")
    @Param(name = "etl_dependency", desc = "作业系统参数实体对象", range = "与数据库对应表字段规则一致", isBean = true)
    @Param(name = "sub_sys_cd", desc = "任务编号", range = "新增任务时生成")
    @Param(name = "pre_sub_sys_cd", desc = "上游任务编号", range = "新增任务时生成")
    public void batchSaveEtlDependency(Etl_dependency etl_dependency, String sub_sys_cd, String pre_sub_sys_cd) {
        // 1.数据可访问权限处理方式，通过user_id进行权限验证
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_dependency.getEtl_sys_cd(), getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.获取当前任务下的所有作业
        List<Etl_job_def> etlJobList = Dbo.queryList(Etl_job_def.class, "select DISTINCT etl_job,dis_type"
                        + " from " + Etl_job_def.TableName + " where sub_sys_cd=? and etl_sys_cd=?",
                sub_sys_cd, etl_dependency.getEtl_sys_cd());
        // 4.获取上游任务下的所有作业即上游作业
        List<String> preEtlJobList = Dbo.queryOneColumnList("select DISTINCT pre_etl_job from "
                        + Etl_job_def.TableName + " where sub_sys_cd=? and etl_sys_cd=?", pre_sub_sys_cd,
                etl_dependency.getEtl_sys_cd());
        // 5.双重循环添加不同任务下的所有作业之间的依赖
        for (Etl_job_def etl_job_def : etlJobList) {
            for (String pre_etl_job : preEtlJobList) {
                // 6.判断当前作业下的依赖是否存在，存在就跳过，不存在则依赖
                if (ETLJobUtil.isEtlDependencyExist(etl_dependency.getEtl_sys_cd(), etl_dependency.getPre_etl_sys_cd(),
                        etl_job_def.getEtl_job(), pre_etl_job)) {
                    continue;
                }
                // 7.如果当前作业为定时作业则跳过
                if (Dispatch_Type.DEPENDENCE != Dispatch_Type.ofEnumByCode(etl_job_def.getDisp_type())) {
                    continue;
                }
                // 8.将当前作业与上游作业交换，看是否已经存在依赖关系,存在则跳过
                if (ETLJobUtil.isEtlDependencyExist(etl_dependency.getEtl_sys_cd(), etl_dependency.getPre_etl_sys_cd(),
                        pre_etl_job, etl_job_def.getEtl_job())) {
                    continue;
                }
                // 9.循环保存作业依赖关系
                etl_dependency.setEtl_job(etl_job_def.getEtl_job());
                etl_dependency.setEtl_job(pre_etl_job);
                etl_dependency.add(Dbo.db());
            }
        }
    }

    @Method(desc = "更新保存作业依赖",
            logicStep = "1.数据可访问权限处理方式，通过user_id进行权限验证" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.判断作业名称是否发生改变，上游作业名称是否发生改变" +
                    "3.1旧作业名称没有改变，上游名称改变" +
                    "3.2旧作业名称改变，上游名称没有改变" +
                    "3.3旧作业名称改变，上游名称改变" +
                    "3.4旧作业名称没有改变，上游名称没有改变" +
                    "3.5不存在的作业依赖关系" +
                    "4.判断作业依赖是否存在，存在就不需要更新" +
                    "5.更新作业依赖")
    @Param(name = "etlDependency", desc = "作业系统参数实体对象", range = "与数据库对应表字段规则一致", isBean = true)
    @Param(name = "oldEtlJob", desc = "更新前作业名称", range = "新增作业时生成", isBean = true)
    @Param(name = "oldPreEtlJob", desc = "更新前上游作业名称", range = "新增作业时生成", isBean = true)
    public void updateEtlDependency(Etl_dependency etlDependency, String oldEtlJob, String oldPreEtlJob) {
        // 1.数据可访问权限处理方式，通过user_id进行权限验证
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etlDependency.getEtl_sys_cd(), getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.判断作业名称是否发生改变，上游作业名称是否发生改变
        if (etlDependency.getEtl_job().equals(oldEtlJob) &&
                !etlDependency.getPre_etl_job().equals(oldPreEtlJob)) {
            // 3.1旧作业名称没有改变，上游名称改变
            etlDependency.setEtl_job(oldEtlJob);
        } else if (!etlDependency.getEtl_job().equals(oldEtlJob)
                && etlDependency.getPre_etl_job().equals(oldPreEtlJob)) {
            // 3.2旧作业名称改变，上游名称没有改变
            etlDependency.setPre_etl_job(oldPreEtlJob);
        } else if (!etlDependency.getEtl_job().equals(oldEtlJob)
                && !etlDependency.getPre_etl_job().equals(oldPreEtlJob)) {
            // 3.3旧作业名称改变，上游名称改变
            etlDependency.setEtl_job(oldEtlJob);
            etlDependency.setPre_etl_job(oldPreEtlJob);
        } else if (etlDependency.getEtl_job().equals(oldEtlJob) &&
                etlDependency.getPre_etl_job().equals(oldPreEtlJob)) {
            // 3.4旧作业名称没有改变，上游名称没有改变
            throw new BusinessException("旧作业名称等于新作业名称，旧上游作业名称等于新上游作业名称，依赖已存在！");
        } else {
            // 3.5不存在的作业依赖关系
            throw new BusinessException("系统错误，不存在的作业依赖关系！");
        }
        // 4.判断作业依赖是否存在，存在就不需要更新
        if (ETLJobUtil.isEtlDependencyExist(etlDependency.getEtl_sys_cd(), etlDependency.getPre_etl_sys_cd(),
                etlDependency.getEtl_job(), etlDependency.getPre_etl_job())) {
            throw new BusinessException("当前工程对应作业的依赖已存在！");
        }
        // 5.更新作业依赖
        DboExecute.deletesOrThrow("更新作业依赖失败，etl_sys_cd=" + etlDependency.getEtl_sys_cd()
                        + ",pre_etl_sys_cd=" + etlDependency.getPre_etl_sys_cd() +
                        ",etl_job=" + etlDependency.getEtl_job() + ",pre_etl_job=" + etlDependency.getPre_etl_job(),
                "update " + Etl_dependency.TableName + " set etl_job=?,pre_etl_sys_cd=?,pre_etl_job=?,"
                        + "status=? where etl_sys_cd=? and etl_job=? and pre_etl_job=?",
                etlDependency.getEtl_job(), etlDependency.getPre_etl_sys_cd(), etlDependency.getPre_etl_job(),
                etlDependency.getStatus(), etlDependency.getEtl_sys_cd(), oldEtlJob, oldPreEtlJob);
    }

    @Method(desc = "删除作业依赖",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.删除作业依赖")
    @Param(name = "etl_sys_cd", desc = "工程代码", range = "新增工程时生成")
    @Param(name = "etl_job", desc = "作业名称", range = "新增作业时生成")
    @Param(name = "pre_etl_job", desc = "上游作业名称", range = "新增作业时生成")
    public void deleteEtlDependency(String etl_sys_cd, String etl_job, String pre_etl_job) {
        // 1.数据可访问权限处理方式，该方法不需要权限验证
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        // 3.删除作业依赖
        DboExecute.deletesOrThrow("删除作业依赖失败，etl_sys_cd=" + etl_sys_cd +
                ",etl_job=" + etl_job, "delete from " + Etl_dependency.TableName +
                " where etl_sys_cd=? AND etl_job=? AND pre_etl_job=?", etl_sys_cd, etl_job, pre_etl_job);
    }

    @Method(desc = "批量删除作业依赖",
            logicStep = "1.数据可访问权限处理方式，该方法不需要权限验证" +
                    "2.验证当前用户下的工程是否存在" +
                    "3.获取所有要删除的依赖作业名称" +
                    "4.遍历所有的依赖作业名称" +
                    "5.循环删除作业依赖关系")
    @Param(name = "etl_sys_cd", desc = "工程代码", range = "新增工程时生成")
    @Param(name = "batchEtlJob", desc = "批量作业编号", range = "无限制")
    public void batchDeleteEtlDependency(String etl_sys_cd, String batchEtlJob) {
        // 1.数据可访问权限处理方式，该方法不需要权限验证
        // 2.验证当前用户下的工程是否存在
        if (!ETLJobUtil.isEtlSysExist(etl_sys_cd, getUserId())) {
            throw new BusinessException("当前工程已不存在！");
        }
        Type type = new TypeReference<Map<String, Object>>() {
        }.getType();
        // 获取存放作业依赖关系的集合
        Map<String, String> etlJobMap = JsonUtil.toObject(batchEtlJob, type);
        // 4.遍历所有的依赖作业名称,key为作业名称，value为上游作业名称
        for (Map.Entry<String, String> entry : etlJobMap.entrySet()) {
            // 5.循环删除作业依赖关系
            DboExecute.deletesOrThrow("删除作业依赖失败，etl_sys_cd=" + etl_sys_cd + ",etl_job="
                            + entry.getKey() + ",pre_etl_job=" + entry.getValue(), "delete from "
                            + Etl_dependency.TableName + " where etl_sys_cd=? AND etl_job=? and pre_etl_job=?",
                    etl_sys_cd, entry.getKey(), entry.getValue());
        }
    }
}

