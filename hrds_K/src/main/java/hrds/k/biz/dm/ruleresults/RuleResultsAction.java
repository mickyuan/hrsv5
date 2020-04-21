package hrds.k.biz.dm.ruleresults;

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
import hrds.commons.entity.Dq_result;
import hrds.commons.exception.BusinessException;
import hrds.k.biz.dm.ruleresults.bean.RuleResultSearchBean;
import hrds.k.biz.utils.CheckBeanUtil;

import java.util.List;
import java.util.Map;

@DocClass(desc = "数据管控-规则结果", author = "BY-HLL", createdate = "2020/4/11 0013 上午 09:39")
public class RuleResultsAction extends BaseAction {

    @Method(desc = "获取规则检查结果信息", logicStep = "获取规则检查结果信息")
    @Param(name = "currPage", desc = "分页当前页", range = "大于0的正整数", valueIfNull = "1")
    @Param(name = "pageSize", desc = "分页查询每页显示条数", range = "大于0的正整数", valueIfNull = "10")
    @Return(desc = "规则检查结果信息", range = "规则检查结果信息")
    public List<Map<String, Object>> getRuleResultInfos(int currPage, int pageSize) {
        //初始化执行sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT t1.verify_date, t1.start_date, t1.start_time, t1.verify_result,t1.exec_mode,t1.dl_stat," +
                " t1.task_id, t2.target_tab, t2.reg_name,t2.reg_num, t2.flags, t2.rule_src, t2.rule_tag FROM" +
                " dq_result t1 JOIN dq_list t2 ON t1.reg_num = t2.reg_num ORDER BY t1.verify_date DESC");
        Page page = new DefaultPageImpl(currPage, pageSize);
        return Dbo.queryPagedList(page, asmSql.sql(), asmSql.params());
    }

    @Method(desc = "检索规则结果信息",
            logicStep = "检索规则结果信息")
    @Param(name = "ruleResultSearchBean", desc = "自定义Bean", range = "ruleResultSearchBean")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public List<Map<String, Object>> searchRuleResultInfos(RuleResultSearchBean ruleResultSearchBean) {
        //数据校验
        if (CheckBeanUtil.checkFullNull(ruleResultSearchBean)) {
            throw new BusinessException("检索条件全为空!");
        }
        //初始化执行sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT t1.verify_date, t1.start_date, t1.start_time, t1.verify_result,t1.exec_mode, t1.dl_stat," +
                " t1.task_id, t2.target_tab, t2.reg_name,t2.reg_num, t2.flags, t2.rule_src, t2.rule_tag FROM" +
                " dq_result t1 JOIN dq_list t2 ON t1.reg_num = t2.reg_num where t2.user_id=?").addParam(getUserId());
        if (StringUtil.isNotBlank(ruleResultSearchBean.getVerify_date())) {
            asmSql.addSql(" and t1.verify_date = ?").addParam(ruleResultSearchBean.getVerify_date());
        }
        if (StringUtil.isNotBlank(ruleResultSearchBean.getStart_date())) {
            asmSql.addSql(" and t1.start_date = ?").addParam(ruleResultSearchBean.getStart_date());
        }
        if (StringUtil.isNotBlank(ruleResultSearchBean.getRule_src())) {
            asmSql.addLikeParam(" t2.rule_src", ruleResultSearchBean.getRule_src());
        }
        if (StringUtil.isNotBlank(ruleResultSearchBean.getRule_tag())) {
            asmSql.addLikeParam(" t2.rule_tag", ruleResultSearchBean.getRule_tag());
        }
        if (StringUtil.isNotBlank(ruleResultSearchBean.getReg_name())) {
            asmSql.addLikeParam(" t2.reg_name", ruleResultSearchBean.getReg_name());
        }
        if (StringUtil.isNotBlank(ruleResultSearchBean.getReg_num().toString())) {
            asmSql.addSql(" and t1.reg_num = ?").addParam(ruleResultSearchBean.getReg_num());
        }
        if (StringUtil.isNotBlank(ruleResultSearchBean.getExec_mode())) {
            asmSql.addORParam("t1.exec_mode", ruleResultSearchBean.getExec_mode().split(","));
        }
        if (StringUtil.isNotBlank(ruleResultSearchBean.getDl_stat())) {
            asmSql.addSql(" and t1.dl_stat = ?").addParam(ruleResultSearchBean.getDl_stat());
        }
        if (StringUtil.isNotBlank(ruleResultSearchBean.getVerify_result())) {
            asmSql.addORParam("t1.verify_result", ruleResultSearchBean.getVerify_result().split(","));
        }
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "规则执行详细信息",
            logicStep = "1.规则执行详细信息")
    @Param(name = "task_id", desc = "任务编号", range = "String类型")
    @Return(desc = "规则执行详细信息", range = "规则执行详细信息")
    public Dq_result getRuleDetectDetail(String task_id) {
        //数据校验
        if (StringUtil.isBlank(task_id)) {
            throw new BusinessException("查看的任务编号为空!");
        }
        //获取本任务的详细信息
        return Dbo.queryOneObject(Dq_result.class, "SELECT * FROM dq_result WHERE task_id = ?", task_id)
                .orElseThrow(() -> (new BusinessException("任务执行详细信息的SQL失败!")));
    }

    @Method(desc = "规则执行历史信息",
            logicStep = "规则执行历史信息")
    @Param(name = "reg_num", desc = "规则编号", range = "long类型")
    @Return(desc = "规则执行历史信息", range = "规则执行历史信息")
    public List<Dq_result> getRuleExecuteHistoryInfo(long reg_num) {
        //数据校验
        if (StringUtil.isBlank(String.valueOf(reg_num))) {
            throw new BusinessException("规则编号为空!");
        }
        //获取该规则相关执行的历史信息
        return Dbo.queryList(Dq_result.class, "SELECT * FROM dq_result WHERE reg_num = ? ORDER BY verify_date DESC",
                reg_num);
    }
}
