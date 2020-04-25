package hrds.k.biz.dm.ruleresults.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@Table(tableName = "rule_result_search_bean")
public class RuleResultSearchBean extends ProjectTableEntity {

    private static final long serialVersionUID = -4272971336553207489L;
    public static final String TableName = "rule_result_search_bean";

    @DocBean(name = "verify_date", value = "检查日期", dataType = String.class, required = false)
    private String verify_date; //检查日期
    @DocBean(name = "start_date", value = "执行开始日期", dataType = String.class, required = false)
    private String start_date; //执行开始日期
    @DocBean(name = "rule_src", value = "规则来源", dataType = String.class, required = false)
    private String rule_src; //规则来源
    @DocBean(name = "rule_tag", value = "规则标签", dataType = String.class, required = false)
    private String rule_tag; //规则标签
    @DocBean(name = "reg_name", value = "规则名称", dataType = String.class, required = false)
    private String reg_name; //规则名称
    @DocBean(name = "reg_num", value = "规则编号", dataType = String.class, required = false)
    private Long reg_num; //规则编号
    @DocBean(name = "exec_mode", value = "执行方式", dataType = String.class, required = false)
    private String[] exec_mode; //执行方式
    @DocBean(name = "verify_result", value = "检查结果", dataType = String.class, required = false)
    private String[] verify_result; //检查结果

    public String getVerify_date() {
        return verify_date;
    }

    public void setVerify_date(String verify_date) {
        this.verify_date = verify_date;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getRule_src() {
        return rule_src;
    }

    public void setRule_src(String rule_src) {
        this.rule_src = rule_src;
    }

    public String getRule_tag() {
        return rule_tag;
    }

    public void setRule_tag(String rule_tag) {
        this.rule_tag = rule_tag;
    }

    public String getReg_name() {
        return reg_name;
    }

    public void setReg_name(String reg_name) {
        this.reg_name = reg_name;
    }

    public Long getReg_num() {
        return reg_num;
    }

    public void setReg_num(Long reg_num) {
        this.reg_num = reg_num;
    }

    public String[] getExec_mode() {
        return exec_mode;
    }

    public void setExec_mode(String[] exec_mode) {
        this.exec_mode = exec_mode;
    }

    public String[] getVerify_result() {
        return verify_result;
    }

    public void setVerify_result(String[] verify_result) {
        this.verify_result = verify_result;
    }
}
