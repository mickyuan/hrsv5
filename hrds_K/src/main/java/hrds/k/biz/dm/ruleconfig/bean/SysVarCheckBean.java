package hrds.k.biz.dm.ruleconfig.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@Table(tableName = "sys_var_check_bean")
public class SysVarCheckBean extends ProjectTableEntity {

    private static final long serialVersionUID = -6775651305689004626L;
    public static final String TableName = "sys_var_check_bean";

    //变量名
    @DocBean(name = "name", value = "变量名", dataType = String.class, required = false)
    private String name;
    //变量值
    @DocBean(name = "value", value = "变量值", dataType = String.class, required = false)
    private String value;
    //是否有效
    @DocBean(name = "isEff", value = "是否有效", dataType = String.class, required = false)
    private String isEff;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getIsEff() {
        return isEff;
    }

    public void setIsEff(String isEff) {
        this.isEff = isEff;
    }
}
