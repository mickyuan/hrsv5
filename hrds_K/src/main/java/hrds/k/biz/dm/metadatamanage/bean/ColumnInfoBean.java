package hrds.k.biz.dm.metadatamanage.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@Table(tableName = "column_info_bean")
public class ColumnInfoBean extends ProjectTableEntity {

    public static final String TableName = "column_info_bean";
    private static final long serialVersionUID = -6173825527384242061L;

    //字段id
    @DocBean(name = "column_id", value = "字段id", dataType = Long.class)
    private String column_id;
    //字段名
    @DocBean(name = "column_name", value = "字段名", dataType = String.class)
    private String column_name;
    //字段中文名
    @DocBean(name = "column_ch_name", value = "字段中文名", dataType = String.class, required = false)
    private String column_ch_name;
    //字段类型
    @DocBean(name = "data_type", value = "数据类型", dataType = String.class, required = false)
    private String data_type;
    //字段长度
    @DocBean(name = "data_len", value = "字段长度", dataType = String.class, required = false)
    private String data_len;
    //小数长度
    @DocBean(name = "decimal_point", value = "小数长度", dataType = String.class, required = false)
    private String decimal_point;
    //是否主键
    @DocBean(name = "is_primary_key", value = "是否主键", dataType = String.class, required = false)
    private String is_primary_key;

    public String getColumn_id() {
        return column_id;
    }

    public void setColumn_id(String column_id) {
        this.column_id = column_id;
    }

    public String getColumn_name() {
        return column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public String getColumn_ch_name() {
        return column_ch_name;
    }

    public void setColumn_ch_name(String column_ch_name) {
        this.column_ch_name = column_ch_name;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getData_len() {
        return data_len;
    }

    public void setData_len(String data_len) {
        this.data_len = data_len;
    }

    public String getDecimal_point() {
        return decimal_point;
    }

    public void setDecimal_point(String decimal_point) {
        this.decimal_point = decimal_point;
    }

    public String getIs_primary_key() {
        return is_primary_key;
    }

    public void setIs_primary_key(String is_primary_key) {
        this.is_primary_key = is_primary_key;
    }
}
