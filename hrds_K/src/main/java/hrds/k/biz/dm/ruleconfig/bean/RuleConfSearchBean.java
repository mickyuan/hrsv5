package hrds.k.biz.dm.ruleconfig.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.core.annotation.Param;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@Table(tableName = "rule_conf_search_bean")
public class RuleConfSearchBean extends ProjectTableEntity {

    private static final long serialVersionUID = 3923798876127740050L;
    public static final String TableName = "rule_conf_search_bean";

    //规则编号
    @DocBean(name = "reg_num", value = "规则编号", dataType = String.class, required = false)
    private String reg_num;
    //目标表名
    @DocBean(name = "target_tab", value = "目标表名", dataType = String.class, required = false)
    private String target_tab;
    //规则标签
    @DocBean(name = "rule_tag", value = "规则标签", dataType = String.class, required = false)
    private String rule_tag;
    //规则名称
    @DocBean(name = "reg_name", value = "规则名称", dataType = String.class, required = false)
    private String reg_name;
    //规则来源
    @DocBean(name = "rule_src", value = "规则来源", dataType = String.class, required = false)
    private String rule_src;
    //规则类型
    @DocBean(name = "case_type", value = "规则类型", dataType = String.class, required = false)
    private String[] case_type;
    //调度状态
    @DocBean(name = "job_status", value = "调度状态", dataType = String.class, required = false)
    private String[] job_status;

    public String getReg_num() {
        return reg_num;
    }

    public void setReg_num(String reg_num) {
        this.reg_num = reg_num;
    }

    public String getTarget_tab() {
        return target_tab;
    }

    public void setTarget_tab(String target_tab) {
        this.target_tab = target_tab;
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

    public String getRule_src() {
        return rule_src;
    }

    public void setRule_src(String rule_src) {
        this.rule_src = rule_src;
    }

    public String[] getCase_type() {
        return case_type;
    }

    public void setCase_type(String[] case_type) {
        this.case_type = case_type;
    }

    public String[] getJob_status() {
        return job_status;
    }

    public void setJob_status(String[] job_status) {
        this.job_status = job_status;
    }
}
