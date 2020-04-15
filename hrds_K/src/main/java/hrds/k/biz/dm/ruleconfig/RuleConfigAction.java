package hrds.k.biz.dm.ruleconfig;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.DqcExecMode;
import hrds.commons.codes.Job_Effective_Flag;
import hrds.commons.entity.Dq_definition;
import hrds.commons.entity.Dq_rule_def;
import hrds.commons.entity.Etl_sub_sys_list;
import hrds.commons.entity.Etl_sys;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.etl.EtlJobUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.k.biz.dm.ruleconfig.bean.RuleConfSearchBean;
import hrds.k.biz.dm.ruleconfig.bean.SysVarCheckBean;
import hrds.k.biz.dm.ruleconfig.commons.DqcExecution;
import hrds.k.biz.utils.CheckBeanUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据管控-规则配置", author = "BY-HLL", createdate = "2020/4/8 0008 上午 09:29")
public class RuleConfigAction extends BaseAction {

    @Method(desc = "添加规则", logicStep = "添加规则")
    @Param(name = "dq_definition", desc = "Dq_definition的实体对象", range = "Dq_definition的实体对象")
    public void addDqDefinition(Dq_definition dq_definition) {
        //数据校验
        if (StringUtil.isBlank(dq_definition.getLoad_strategy())) {
            throw new BusinessException("规则加载策略为空!");
        }
        if (StringUtil.isBlank(dq_definition.getFlags())) {
            throw new BusinessException("规则级别为空!");
        }
        //设置数据对象
        dq_definition.setReg_num(PrimayKeyGener.getNextId());
        dq_definition.setApp_updt_dt(DateUtil.getSysDate());
        dq_definition.setApp_updt_ti(DateUtil.getSysTime());
        dq_definition.setUser_id(getUserId());
        dq_definition.setSpecify_sql(dq_definition.getSpecify_sql().replace("\n", " "));
        dq_definition.setErr_data_sql(dq_definition.getErr_data_sql().replace("\n", " "));
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
        if (StringUtil.isBlank(dq_definition.getReg_num().toString())) {
            throw new BusinessException("修改规则编号为空!");
        }
        if (!checkRegNumIsExist(dq_definition.getReg_num())) {
            throw new BusinessException("修改的规则已经不存在!");
        }
        if (StringUtil.isBlank(dq_definition.getLoad_strategy())) {
            throw new BusinessException("规则加载策略为空!");
        }
        if (StringUtil.isBlank(dq_definition.getFlags())) {
            throw new BusinessException("规则级别为空!");
        }
        //设置数据对象
        dq_definition.setReg_num(PrimayKeyGener.getNextId());
        dq_definition.setApp_updt_dt(DateUtil.getSysDate());
        dq_definition.setApp_updt_ti(DateUtil.getSysTime());
        dq_definition.setUser_id(getUserId());
        dq_definition.setSpecify_sql(dq_definition.getSpecify_sql().replace("\n", " "));
        dq_definition.setErr_data_sql(dq_definition.getErr_data_sql().replace("\n", " "));
        //添加规则
        dq_definition.add(Dbo.db());
    }

    @Method(desc = "获取规则信息列表",
            logicStep = "获取规则信息列表")
    @Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", valueIfNull = "10")
    @Return(desc = "规则信息列", range = "规则信息列")
    public List<Map<String, Object>> getDqDefinitionInfos(int currPage, int pageSize) {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //设置查询sql
            SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
            asmSql.clean();
            asmSql.addSql("select dql.*,? as job_status from " + Dq_definition.TableName + " dql where user_id=?")
                    .addParam(Job_Effective_Flag.NO.getCode()).addParam(getUserId());
            //查询
            Page page = new DefaultPageImpl(currPage, pageSize);
            List<Map<String, Object>> dqd_list = Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
            //处理查询结果
            Dq_definition dq_definition = new Dq_definition();
            for (Map<String, Object> dqd : dqd_list) {
                dq_definition.setReg_num(dqd.get("reg_num").toString());
                if (DqcExecution.getEffDepJobs(db, dq_definition).size() > 0) {
                    dqd.put("job_status", Job_Effective_Flag.YES.getCode());
                }
            }
            return dqd_list;
        }
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

    @Method(desc = "获取规则类型数据", logicStep = "获取规则类型数据")
    @Return(desc = "规则类型数据", range = "规则类型数据")
    public List<Dq_rule_def> getDqRuleDef() {
//        List<Map<String, Object>> dq_rule_def_s = Dbo.queryList("select * from " + Dq_rule_def.TableName);
//        return listToMap(dq_rule_def_s, "case_type", "case_type_desc");
        return Dbo.queryList(Dq_rule_def.class, "select * from " + Dq_rule_def.TableName);
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
    public List<Map<String, Object>> searchDqDefinitionInfos(RuleConfSearchBean ruleConfSearchBean) {
        //数据校验
        if (CheckBeanUtil.checkFullNull(ruleConfSearchBean)) {
            throw new BusinessException("搜索条件全部为空!");
        }
        //初始化执行sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT * FROM " + Dq_definition.TableName + " where user_id = ?").addParam(getUserId());
        if (StringUtil.isNotBlank(ruleConfSearchBean.getReg_num())) {
            asmSql.addLikeParam(" cast(reg_num as varchar(10))",
                    '%' + ruleConfSearchBean.getReg_num() + '%');
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
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //获取搜索结果
            List<Map<String, Object>> dqd_list = Dbo.queryList(db, asmSql.sql(), asmSql.params());
            //根据规则编号查询该规则是否被作业有效的引用
            Dq_definition dq_definition = new Dq_definition();
            for (Map<String, Object> dqd : dqd_list) {
                dqd.put("job_status", Job_Effective_Flag.NO.getCode());
                dq_definition.setReg_num(dqd.get("reg_num").toString());
                //根据规则编号获取有效依赖作业信息
                List<Map<String, Object>> effDepJobs = DqcExecution.getEffDepJobs(db, dq_definition);
                //如果有效依赖作业大于0,则改规则处于有效调度状态
                if (effDepJobs.size() > 0) {
                    dqd.put("job_status", Job_Effective_Flag.YES.getCode());
                }
            }
            //提交数据库操作
            db.commit();
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
            //返回检索结果
            return search_data_list;
        }
    }

    @Method(desc = "手动执行", logicStep = "手动执行")
    @Param(name = "reg_num", desc = "规则编号", range = "long类型")
    @Param(name = "verify_date", desc = "验证日期", range = "String类型,20200202")
    public void manualExecution(long reg_num, String verify_date) {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //获取规则配置信息
            Dq_definition dqd = new Dq_definition();
            dqd.setReg_num(reg_num);
            Dq_definition dq_definition = Dbo.queryOneObject(Dq_definition.class, "SELECT * FROM dq_list WHERE" +
                    " reg_num=?", dqd.getReg_num()).orElseThrow(() -> (new BusinessException("获取配置信息的SQL失败!")));
            //系统变量对应结果
            List<SysVarCheckBean> beans = DqcExecution.getSysVarCheckBean(dq_definition);
            //执行规则
            DqcExecution.executionRule(db, dq_definition, verify_date, beans, DqcExecMode.ShouGong.getCode());
            //提交数据库操作
            db.commit();
        }
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
    public static List<Etl_sub_sys_list> getTaskInfo(String etl_sys_cd) {
        return EtlJobUtil.getTaskInfo(etl_sys_cd);
    }

    @Method(desc = "查看规则调度状态",
            logicStep = "查看规则调度状态")
    @Param(name = "reg_num", desc = "规则编号", range = "long类型")
    @Return(desc = "规则调度信息列表", range = "规则调度信息列表")
    public List<Map<String, Object>> viewRuleSchedulingStatus(String reg_num) {
        Dq_definition dqd = new Dq_definition();
        dqd.setReg_num(reg_num);
        Dq_definition dq_definition = getDqDefinition(dqd.getReg_num());
        //初始化执行sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT etl_sys_cd,sub_sys_cd,etl_job,job_eff_flag FROM etl_job_def where");
        asmSql.addLikeParam("etl_job", dq_definition.getReg_num().toString(), "");
        asmSql.addSql("UNION");
        asmSql.addSql("SELECT etl_sys_cd,sub_sys_cd,etl_job,job_eff_flag FROM etl_job WHERE");
        asmSql.addLikeParam("etl_job", dq_definition.getReg_num().toString(), "");
        asmSql.addSql("UNION");
        asmSql.addSql("SELECT etl_sys_cd,sub_sys_cd,etl_job,job_eff_flag FROM etl_job_disp_his WHERE");
        asmSql.addLikeParam("etl_job", dq_definition.getReg_num().toString(), "");
        List<Map<String, Object>> queryList = Dbo.queryList(asmSql.sql(), asmSql.params());
        //处理查询结果
        List<Map<String, Object>> ruleSchedulingStatusInfos = new ArrayList<>();
        queryList.forEach(o -> {
            Map<String, Object> map = new HashMap<>();
            map.put("reg_num", dq_definition.getReg_num());
            map.put("case_type", dq_definition.getCase_type());
            map.put("target_tab", dq_definition.getTarget_tab());
            map.put("job_eff_flag", o.get("job_eff_flag"));
            map.put("job_disp_status", o.get("job_disp_status"));
            ruleSchedulingStatusInfos.add(map);
        });
        return ruleSchedulingStatusInfos;
    }

    @Method(desc = "检查规则reg_num是否存在", logicStep = "检查规则reg_num是否存在")
    @Param(name = "reg_num", desc = "规则编号", range = "long类型")
    @Return(desc = "规则否存在", range = "true：不存在，false：存在")
    private boolean checkRegNumIsExist(long reg_num) {
        return Dbo.queryNumber("SELECT COUNT(reg_num) FROM " + Dq_definition.TableName + " WHERE reg_num = ?",
                reg_num).orElseThrow(() -> new BusinessException("检查规则reg_num否存在的SQL错误")) == 1;
    }

    @Method(desc = "list转Map", logicStep = "result转Map")
    @Param(name = "list", desc = "结果list", range = "list类型")
    @Param(name = "colOfKey", desc = "作为key的字段名", range = "String类型")
    @Param(name = "colOfValue", desc = "作为value的字段名", range = "String类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    private Map<String, Object> listToMap(List<Map<String, Object>> list, String colOfKey, String colOfValue) {
        Map<String, Object> map = new HashMap<>();
        list.forEach(l -> map.put(l.get(colOfKey).toString(), l.get(colOfValue).toString()));
        return map;
    }
}
