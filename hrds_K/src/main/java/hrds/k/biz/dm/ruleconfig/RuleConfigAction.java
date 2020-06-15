package hrds.k.biz.dm.ruleconfig;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.DqcExecMode;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.Job_Effective_Flag;
import hrds.commons.collection.ProcessingData;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.TreeNodeInfo;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.commons.TreePageSource;
import hrds.commons.utils.DataTableUtil;
import hrds.commons.utils.etl.EtlJobUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.commons.utils.tree.Node;
import hrds.commons.utils.tree.NodeDataConvertedTreeList;
import hrds.k.biz.dm.ruleconfig.bean.RuleConfSearchBean;
import hrds.k.biz.dm.ruleconfig.bean.SysVarCheckBean;
import hrds.k.biz.dm.ruleconfig.commons.DqcExecution;
import hrds.k.biz.utils.CheckBeanUtil;

import java.util.*;

@DocClass(desc = "数据管控-规则配置", author = "BY-HLL", createdate = "2020/4/8 0008 上午 09:29")
public class RuleConfigAction extends BaseAction {

    @Method(desc = "获取数据源树信息", logicStep = "获取数据源树信息")
    @Return(desc = "数据源树信息", range = "数据源树信息")
    public Object getRuleConfigTreeData() {
        //配置树不显示文件采集的数据
        TreeConf treeConf = new TreeConf();
        treeConf.setShowFileCollection(Boolean.FALSE);
        //根据源菜单信息获取节点数据列表
        List<Map<String, Object>> dataList =
                TreeNodeInfo.getTreeNodeInfo(TreePageSource.DATA_MANAGEMENT, getUser(), treeConf);
        //转换节点数据列表为分叉树列表
        List<Node> ruleConfigTreeList = NodeDataConvertedTreeList.dataConversionTreeInfo(dataList);
        return JsonUtil.toObjectSafety(ruleConfigTreeList.toString(), Object.class).orElseThrow(()
                -> (new BusinessException("数据类型转换失败!")));
    }

    @Method(desc = "添加规则", logicStep = "添加规则")
    @Param(name = "dq_definition", desc = "Dq_definition的实体对象", range = "Dq_definition的实体对象", isBean = true)
    public void addDqDefinition(Dq_definition dq_definition) {
        //数据校验
        Validator.notBlank(dq_definition.getCase_type(), "规则类型为空!");
        //设置数据对象
        dq_definition.setReg_num(PrimayKeyGener.getNextId());
        dq_definition.setApp_updt_dt(DateUtil.getSysDate());
        dq_definition.setApp_updt_ti(DateUtil.getSysTime());
        //是否保存指标数据为空的时候,默认给 0:否
        if (StringUtil.isBlank(dq_definition.getIs_saveindex1())) {
            dq_definition.setIs_saveindex1(IsFlag.Fou.getCode());
        }
        if (StringUtil.isBlank(dq_definition.getIs_saveindex2())) {
            dq_definition.setIs_saveindex2(IsFlag.Fou.getCode());
        }
        if (StringUtil.isBlank(dq_definition.getIs_saveindex3())) {
            dq_definition.setIs_saveindex3(IsFlag.Fou.getCode());
        }
        dq_definition.setUser_id(getUserId());
        dq_definition.setSpecify_sql(dq_definition.getSpecify_sql().replace("\n", " "));
        dq_definition.setSpecify_sql(dq_definition.getSpecify_sql().replace("\t", " "));
        dq_definition.setErr_data_sql(dq_definition.getErr_data_sql().replace("\n", " "));
        dq_definition.setErr_data_sql(dq_definition.getErr_data_sql().replace("\t", " "));
        //添加规则
        dq_definition.add(Dbo.db());
    }

    @Method(desc = "删除规则(编号删除)",
            logicStep = "删除规则(编号删除)")
    @Param(name = "reg_num", desc = "规则编号", range = "long类型,数组")
    public void deleteDqDefinition(long reg_num) {
        //检查数据
        if (checkRegNumIsExist(reg_num)) {
            Dbo.execute("delete from " + Dq_definition.TableName + " where user_id=? and reg_num=?", getUserId(),
                    reg_num);
        }
    }

    @Method(desc = "删除规则(批量)",
            logicStep = "删除规则(批量)")
    @Param(name = "reg_num", desc = "规则编号", range = "long[]类型,数组")
    public void releaseDeleteDqDefinition(Long[] reg_num) {
        //检查
        //初始化sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("delete from " + Dq_definition.TableName + " where user_id=?");
        asmSql.addParam(getUserId().toString());
        asmSql.addORParam("reg_num ", reg_num);
        //删除数据
        Dbo.execute(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "更新规则",
            logicStep = "更新规则")
    @Param(name = "dq_definition", desc = "Dq_definition的实体对象", range = "Dq_definition的实体对象")
    public void updateDqDefinition(Dq_definition dq_definition) {
        //数据校验
        Validator.notBlank(dq_definition.getReg_num().toString(), "修改规则编号为空!");
        if (!checkRegNumIsExist(dq_definition.getReg_num())) {
            throw new BusinessException("修改的规则已经不存在!");
        }
        //设置数据对象
        dq_definition.setApp_updt_dt(DateUtil.getSysDate());
        dq_definition.setApp_updt_ti(DateUtil.getSysTime());
        //是否保存指标数据为空的时候,默认给 0:否
        if (StringUtil.isBlank(dq_definition.getIs_saveindex1())) {
            dq_definition.setIs_saveindex1(IsFlag.Fou.getCode());
        }
        if (StringUtil.isBlank(dq_definition.getIs_saveindex2())) {
            dq_definition.setIs_saveindex2(IsFlag.Fou.getCode());
        }
        if (StringUtil.isBlank(dq_definition.getIs_saveindex3())) {
            dq_definition.setIs_saveindex3(IsFlag.Fou.getCode());
        }
        dq_definition.setUser_id(getUserId());
        dq_definition.setSpecify_sql(dq_definition.getSpecify_sql().replace("\n", " "));
        dq_definition.setSpecify_sql(dq_definition.getSpecify_sql().replace("\t", " "));
        dq_definition.setErr_data_sql(dq_definition.getErr_data_sql().replace("\n", " "));
        dq_definition.setErr_data_sql(dq_definition.getErr_data_sql().replace("\t", " "));
        //添加规则
        dq_definition.update(Dbo.db());
    }

    @Method(desc = "获取规则信息列表",
            logicStep = "获取规则信息列表")
    @Return(desc = "规则信息列表", range = "规则信息列表")
    public Map<String, Object> getDqDefinitionInfos() {
        //设置查询sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select dql.*,? as job_status from " + Dq_definition.TableName + " dql where user_id=?")
                .addParam(Job_Effective_Flag.NO.getCode()).addParam(getUserId());
        List<Map<String, Object>> dqd_list = Dbo.queryList(asmSql.sql(), asmSql.params());
        //处理查询结果
        Dq_definition dq_definition = new Dq_definition();
        for (Map<String, Object> dqd : dqd_list) {
            dq_definition.setReg_num(dqd.get("reg_num").toString());
            if (DqcExecution.getEffDepJobs(dq_definition).size() > 0) {
                dqd.put("job_status", Job_Effective_Flag.YES.getCode());
            }
        }
        Map<String, Object> dqd_map = new HashMap<>();
        dqd_map.put("rule_dqd_data_s", dqd_list);
        dqd_map.put("totalSize", dqd_list.size());
        return dqd_map;
    }

    @Method(desc = "获取规则信息",
            logicStep = "获取规则信息")
    @Param(name = "reg_num", desc = "规则编号", range = "long类型")
    @Return(desc = "规则信息", range = "规则信息")
    public Dq_definition getDqDefinition(long reg_num) {
        //检查规则信息是否存在
        if (!checkRegNumIsExist(reg_num)) {
            throw new BusinessException("查询的规则信息已经不存在!");
        }
        //获取规则信息
        return Dbo.queryOneObject(Dq_definition.class, "select * from " + Dq_definition.TableName +
                " where reg_num=?", reg_num).orElseThrow(() -> (new BusinessException("获取规则信息的SQL错误!")));
    }

    @Method(desc = "获取表字段信息列表", logicStep = "获取表字段信息列表")
    @Param(name = "table_name", desc = "表名", range = "String")
    @Return(desc = "字段信息列表", range = "字段信息列表")
    public List<Map<String, Object>> getColumnsByTableName(String table_name) {
        //数据层获取不同表结构
        Validator.notBlank(table_name, "获取表字段信息的表名为空!");
        return DataTableUtil.getColumnByTableName(table_name);
    }

    @Method(desc = "获取规则类型数据", logicStep = "获取规则类型数据")
    @Return(desc = "规则类型数据", range = "规则类型数据")
    public List<Dq_rule_def> getDqRuleDef() {
        return Dbo.queryList(Dq_rule_def.class, "select * from " + Dq_rule_def.TableName);
    }

    @Method(desc = "获取系统帮助提示信息", logicStep = "获取系统帮助提示信息")
    @Return(desc = "系统帮助提示信息", range = "系统帮助提示信息")
    public List<Dq_help_info> getDqHelpInfo() {
        return Dbo.queryList(Dq_help_info.class, "select * from " + Dq_help_info.TableName);
    }

    @Method(desc = "保存作业信息",
            logicStep = "保存作业信息")
    @Param(name = "pro_id", desc = "调度工程id", range = "long类型,唯一")
    @Param(name = "task_id", desc = "调度任务id", range = "long类型,唯一")
    @Param(name = "reg_num", desc = "规则编号", range = "long类型,唯一")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public void saveETLJob(String pro_id, String task_id, String reg_num) {
        //保存作业调度信息
        int save_status = EtlJobUtil.saveJob(reg_num, DataSourceType.DQC, pro_id, task_id, null);
        if (0 != save_status) {
            throw new BusinessException("保存作业信息失败!");
        }
    }

    @Method(desc = "搜索规则信息", logicStep = "搜索规则信息")
    @Param(name = "ruleConfSearchBean", desc = "自定义RuleConfSearchBean实体", range = "自定义实体bean", isBean = true)
    @Return(desc = "检索结果", range = "检索结果")
    public Map<String, Object> searchDqDefinitionInfos(RuleConfSearchBean ruleConfSearchBean) {
        //数据校验
        if (CheckBeanUtil.checkFullNull(ruleConfSearchBean)) {
            throw new BusinessException("搜索条件全部为空!");
        }
        //初始化执行sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT * FROM " + Dq_definition.TableName + " where user_id = ?").addParam(getUserId());
        if (StringUtil.isNotBlank(ruleConfSearchBean.getReg_num())) {
            asmSql.addLikeParam("cast(reg_num as varchar(10))", '%' + ruleConfSearchBean.getReg_num() + '%');
        }
        if (StringUtil.isNotBlank(ruleConfSearchBean.getTarget_tab())) {
            asmSql.addLikeParam("target_tab", '%' + ruleConfSearchBean.getTarget_tab() + '%');
        }
        if (StringUtil.isNotBlank(ruleConfSearchBean.getRule_tag())) {
            asmSql.addLikeParam("rule_tag", '%' + ruleConfSearchBean.getRule_tag() + '%');
        }
        if (StringUtil.isNotBlank(ruleConfSearchBean.getReg_name())) {
            asmSql.addLikeParam("reg_name", '%' + ruleConfSearchBean.getReg_name() + '%');
        }
        if (StringUtil.isNotBlank(ruleConfSearchBean.getRule_src())) {
            asmSql.addLikeParam("rule_src", '%' + ruleConfSearchBean.getRule_src() + '%');
        }
        if (null != ruleConfSearchBean.getCase_type() && ruleConfSearchBean.getCase_type().length > 0) {
            asmSql.addORParam("case_type", ruleConfSearchBean.getCase_type());
        }
        //获取搜索结果
        List<Map<String, Object>> dqd_list = Dbo.queryList(asmSql.sql(), asmSql.params());
        //根据规则编号查询该规则是否被作业有效的引用
        Dq_definition dq_definition = new Dq_definition();
        for (Map<String, Object> dqd : dqd_list) {
            dqd.put("job_status", Job_Effective_Flag.NO.getCode());
            dq_definition.setReg_num(dqd.get("reg_num").toString());
            //根据规则编号获取有效依赖作业信息
            List<Map<String, Object>> effDepJobs = DqcExecution.getEffDepJobs(dq_definition);
            //如果有效依赖作业大于0,则改规则处于有效调度状态
            if (effDepJobs.size() > 0) {
                dqd.put("job_status", Job_Effective_Flag.YES.getCode());
            }
        }
        //初始化返回结果List
        List<Map<String, Object>> search_data_list = new ArrayList<>();
        //处理检索结果
        dqd_list.forEach(dqd -> {
            //根据调度状态处理检索结果
            Job_Effective_Flag job_flag = Job_Effective_Flag.ofEnumByCode(dqd.get("job_status").toString());
            if (null != ruleConfSearchBean.getJob_status()) {
                for (String job_status : ruleConfSearchBean.getJob_status()) {
                    if (job_flag == Job_Effective_Flag.ofEnumByCode(job_status)) {
                        search_data_list.add(dqd);
                    }
                }
            }
            //根据规则类型处理检索结果
            else if (null != ruleConfSearchBean.getCase_type()) {
                for (String c_type : ruleConfSearchBean.getCase_type()) {
                    if (dqd.get("case_type").toString().equals(c_type)) {
                        search_data_list.add(dqd);
                    }
                }
            }
            //规则类型和调度状态都为空,设置检索到的所有数据
            else {
                search_data_list.add(dqd);
            }
        });
        //检索结果
        Map<String, Object> search_data_map = new HashMap<>();
        search_data_map.put("rule_dqd_data_s", search_data_list);
        search_data_map.put("totalSize", search_data_list.size());
        return search_data_map;
    }

    @Method(desc = "手动执行", logicStep = "手动执行")
    @Param(name = "reg_num", desc = "规则编号", range = "long类型")
    @Param(name = "verify_date", desc = "验证日期", range = "String类型,20200202")
    public long manualExecution(long reg_num, String verify_date) {
        //获取规则配置信息
        Dq_definition dqd = new Dq_definition();
        dqd.setReg_num(reg_num);
        Dq_definition dq_definition = Dbo.queryOneObject(Dq_definition.class, "SELECT * FROM "
                + Dq_definition.TableName + " " + "WHERE reg_num=?", dqd.getReg_num()).orElseThrow(()
                -> (new BusinessException("获取配置信息的SQL失败!")));
        //系统变量对应结果
        Set<SysVarCheckBean> beans = DqcExecution.getSysVarCheckBean(dq_definition);
        //执行规则,返回执行的任务id
        return DqcExecution.executionRule(dq_definition, verify_date, beans, DqcExecMode.ShouGong.getCode());
    }

    @Method(desc = "获取指标3保存的结果数据,只取10条", logicStep = "获取指标3保存的结果数据,只取10条")
    @Param(name = "task_id", desc = "任务标号", range = "long类型")
    @Return(desc = "指标3保存的结果数据,只取10条", range = "指标3保存的结果数据,只取10条")
    public List<Map<String, Object>> getCheckIndex3(long task_id) {
        //设置 Dq_index3record 对象
        Dq_index3record dq_index3record = new Dq_index3record();
        dq_index3record.setTask_id(task_id);
        //数据校验
        if (StringUtil.isBlank(dq_index3record.getTask_id().toString())) {
            throw new BusinessException("获取指标3结果时,任务标号为空!");
        }
        //获取指标3存储记录信息
        dq_index3record = Dbo.queryOneObject(Dq_index3record.class, "select * from " + Dq_index3record.TableName +
                " where task_id=?", dq_index3record.getTask_id()).orElseThrow(() ->
                (new BusinessException("获取任务指标3存储记录的SQL异常!")));
        //设置查询sql
        String sql = "select * from " + dq_index3record.getTable_name();
        //设置查询sql:问题数据明细sql,只取10条
        List<Map<String, Object>> check_index3_list = new ArrayList<>();
        //根据指标3存储记录信息获取数据
        try {
            new ProcessingData() {
                @Override
                public void dealLine(Map<String, Object> map) {

                    check_index3_list.add(map);
                }
            }.getPageDataLayer(sql, Dbo.db(), 1, 10, dq_index3record.getDsl_id());
        } catch (Exception e) {
            throw new BusinessException("获取指标3存储记录数据失败!" + e.getMessage());
        }
        return check_index3_list;
    }

    @Method(desc = "获取作业工程信息", logicStep = "获取作业工程信息")
    @Return(desc = "作业工程信息", range = "作业工程信息")
    public List<Etl_sys> getProInfos() {
        return EtlJobUtil.getProInfo();
    }

    @Method(desc = "获取作业某个工程下的任务信息",
            logicStep = "获取作业某个工程下的任务信息")
    @Param(name = "etl_sys_cd", desc = "工程代码", range = "String类型")
    @Return(desc = "工程下的任务信息", range = "工程下的任务信息")
    public List<Etl_sub_sys_list> getTaskInfo(String etl_sys_cd) {
        //数据校验
        Validator.notBlank(etl_sys_cd, "工程id为空!");
        return EtlJobUtil.getTaskInfo(etl_sys_cd);
    }

    @Method(desc = "查看规则调度状态",
            logicStep = "查看规则调度状态")
    @Param(name = "reg_num", desc = "规则编号", range = "long类型")
    @Return(desc = "规则调度信息列表", range = "规则调度信息列表")
    public List<Map<String, Object>> viewRuleSchedulingStatus(long reg_num) {
        Dq_definition dqd = new Dq_definition();
        dqd.setReg_num(reg_num);
        Dq_definition dq_definition = getDqDefinition(dqd.getReg_num());
        //初始化执行sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT etl_sys_cd,sub_sys_cd,etl_job,job_eff_flag,job_disp_status FROM " + Etl_job_def.TableName +
                " where").addLikeParam("etl_job", '%' + dq_definition.getReg_num().toString() + '%', "");
        asmSql.addSql("UNION");
        asmSql.addSql("SELECT etl_sys_cd,sub_sys_cd,etl_job,job_eff_flag,job_disp_status FROM " + Etl_job_cur.TableName +
                " WHERE").addLikeParam("etl_job", '%' + dq_definition.getReg_num().toString() + '%', "");
        asmSql.addSql("UNION");
        asmSql.addSql("SELECT etl_sys_cd,sub_sys_cd,etl_job,job_eff_flag,job_disp_status FROM " + Etl_job_disp_his.TableName +
                " WHERE").addLikeParam("etl_job", '%' + dq_definition.getReg_num().toString() + '%', "");
        List<Map<String, Object>> queryList = Dbo.queryList(asmSql.sql(), asmSql.params());
        //处理查询结果
        List<Map<String, Object>> ruleSchedulingStatusInfos = new ArrayList<>();
        queryList.forEach(o -> {
            Map<String, Object> map = new HashMap<>();
            map.put("etl_sys_cd", o.get("etl_sys_cd").toString());
            map.put("sub_sys_cd", o.get("sub_sys_cd").toString());
            map.put("etl_job", o.get("etl_job").toString());
            map.put("job_eff_flag", o.get("job_eff_flag"));
            map.put("job_disp_status", o.get("job_disp_status"));
            map.put("reg_num", dq_definition.getReg_num());
            map.put("case_type", dq_definition.getCase_type());
            map.put("target_tab", dq_definition.getTarget_tab());
            ruleSchedulingStatusInfos.add(map);
        });
        return ruleSchedulingStatusInfos;
    }

    @Method(desc = "指定SQL（校验SQL）检查，检查所有的系统变量是否合法以及sql是否能运行",
            logicStep = "指定SQL（校验SQL）检查，检查所有的系统变量是否合法以及sql是否能运行")
    @Param(name = "dq_definition", desc = "Dq_definition的实体对象", range = "Dq_definition的实体对象", isBean = true)
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public Map<String, Object> specifySqlCheck(Dq_definition dq_definition) {
        //数据校验
        if (StringUtil.isBlank(dq_definition.getSpecify_sql())) {
            throw new BusinessException("指定SQL为空!");
        }
        //系统变量对应结果
        Set<SysVarCheckBean> beans = DqcExecution.getSysVarCheckBean(dq_definition);
        //获取检查结果 成功:success,失败:具体报错信息
        String check_is_success = DqcExecution.executionSqlCheck(beans, dq_definition.getSpecify_sql().split(";"));
        //初始化检查结果
        Map<String, Object> specifySqlCheckMap = new HashMap<>();
        specifySqlCheckMap.put("sysVarCheckBean", beans);
        specifySqlCheckMap.put("check_is_success", check_is_success);
        return specifySqlCheckMap;
    }

    @Method(desc = "异常数据sql检查，检查所有的系统变量是否合法以及sql是否能运行",
            logicStep = "异常数据sql检查，检查所有的系统变量是否合法以及sql是否能运行")
    @Param(name = "dq_definition", desc = "Dq_definition的实体对象", range = "Dq_definition的实体对象", isBean = true)
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public Map<String, Object> errDataSqlCheck(Dq_definition dq_definition) {
        //数据校验
        if (StringUtil.isBlank(dq_definition.getErr_data_sql())) {
            throw new BusinessException("异常数据SQL为空!");
        }
        //系统变量对应结果
        Set<SysVarCheckBean> beans = DqcExecution.getSysVarCheckBean(dq_definition);
        //获取检查结果 成功:success,失败:具体报错信息
        String check_is_success = DqcExecution.executionSqlCheck(beans, dq_definition.getErr_data_sql());
        //初始化检查结果
        Map<String, Object> specifySqlCheckMap = new HashMap<>();
        specifySqlCheckMap.put("sysVarCheckBean", beans);
        specifySqlCheckMap.put("check_is_success", check_is_success);
        return specifySqlCheckMap;
    }

    @Method(desc = "检查规则reg_num是否存在", logicStep = "检查规则reg_num是否存在")
    @Param(name = "reg_num", desc = "规则编号", range = "long类型")
    @Return(desc = "规则否存在", range = "true：不存在，false：存在")
    private boolean checkRegNumIsExist(long reg_num) {
        return Dbo.queryNumber("SELECT COUNT(reg_num) FROM " + Dq_definition.TableName + " WHERE reg_num = ?",
                reg_num).orElseThrow(() -> new BusinessException("检查规则reg_num否存在的SQL错误")) == 1;
    }
}
