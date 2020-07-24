package hrds.k.biz.dm.metadatamanage.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@Table(tableName = "dq_table_column_bean")
public class DqTableColumnBean extends ProjectTableEntity {

    public static final String TableName = "dq_table_column_bean";
    private static final long serialVersionUID = -8972830752961712691L;

    //字段中文名称
    @DocBean(name = "field_ch_name", value = "字段中文名称", dataType = String.class)
    private String field_ch_name;
    //字段名称
    @DocBean(name = "column_name", value = "字段名称", dataType = String.class)
    private String column_name;
    //字段类型
    @DocBean(name = "column_type", value = "字段类型", dataType = String.class)
    private String column_type;
    //字段长度
    @DocBean(name = "column_length", value = "字段长度", dataType = String.class)
    private String column_length;
    //是否可为空
    @DocBean(name = "is_null", value = "是否可为空", dataType = String.class)
    private String is_null;
    //字段来源表名称
    @DocBean(name = "colsourcetab", value = "字段来源表名称", dataType = String.class)
    private String colsourcetab;
    //来源字段
    @DocBean(name = "colsourcecol", value = "来源字段", dataType = String.class)
    private String colsourcecol;
    //备注
    @DocBean(name = "dq_remark", value = "备注", dataType = String.class)
    private String dq_remark;
    //附加属性id数组
    @DocBean(name = "dslad_id_s", value = "附加属性id数组", dataType = Long[].class)
    private Long[] dslad_id_s;

    public String getField_ch_name() {
        return field_ch_name;
    }

    public void setField_ch_name(String field_ch_name) {
        this.field_ch_name = field_ch_name;
    }

    public String getColumn_name() {
        return column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public String getColumn_type() {
        return column_type;
    }

    public void setColumn_type(String column_type) {
        this.column_type = column_type;
    }

    public String getColumn_length() {
        return column_length;
    }

    public void setColumn_length(String column_length) {
        this.column_length = column_length;
    }

    public String getIs_null() {
        return is_null;
    }

    public void setIs_null(String is_null) {
        this.is_null = is_null;
    }

    public String getColsourcetab() {
        return colsourcetab;
    }

    public void setColsourcetab(String colsourcetab) {
        this.colsourcetab = colsourcetab;
    }

    public String getColsourcecol() {
        return colsourcecol;
    }

    public void setColsourcecol(String colsourcecol) {
        this.colsourcecol = colsourcecol;
    }

    public String getDq_remark() {
        return dq_remark;
    }

    public void setDq_remark(String dq_remark) {
        this.dq_remark = dq_remark;
    }

    public Long[] getDslad_id_s() {
        return dslad_id_s;
    }

    public void setDslad_id_s(Long[] dslad_id_s) {
        this.dslad_id_s = dslad_id_s;
    }
}
