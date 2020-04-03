package hrds.k.biz.dm.metadatamanage.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@Table(tableName = "meta_data_info")
public class MetaDataInfo extends ProjectTableEntity {

    private static final long serialVersionUID = 321561870187361L;
    public static final String TableName = "meta_data_info";

    //数据层
    @DocBean(name = "data_layer", value = "数据层", dataType = String.class)
    private String data_layer;
    //表id
    @DocBean(name = "table_id", value = "表id", dataType = String.class)
    private String table_id;
    //创建时间
    @DocBean(name = "create_date", value = "创建时间", dataType = String.class)
    private String create_date;
    //表英文名
    @DocBean(name = "table_name", value = "表英文名", dataType = String.class)
    private String table_name;
    //表中文名
    @DocBean(name = "table_ch_name", value = "表中文名", dataType = String.class)
    private String table_ch_name;
    //表类型
    @DocBean(name = "table_type", value = "表类型", dataType = String.class, required = false)
    private String table_type;
    //表字段信息List
    @DocBean(name = "meta_list", value = "表字段信息List", dataType = String.class)
    private String[] meta_list;

    public String getData_layer() {
        return data_layer;
    }

    public void setData_layer(String data_layer) {
        this.data_layer = data_layer;
    }

    public String getTable_id() {
        return table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getTable_ch_name() {
        return table_ch_name;
    }

    public void setTable_ch_name(String table_ch_name) {
        this.table_ch_name = table_ch_name;
    }

    public String getTable_type() {
        return table_type;
    }

    public void setTable_type(String table_type) {
        this.table_type = table_type;
    }

    public String[] getMeta_list() {
        return meta_list;
    }

    public void setMeta_list(String[] meta_list) {
        this.meta_list = meta_list;
    }
}
