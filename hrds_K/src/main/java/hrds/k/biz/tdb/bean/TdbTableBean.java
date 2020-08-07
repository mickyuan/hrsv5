package hrds.k.biz.tdb.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@Table(tableName = "tdb_table_bean")
public class TdbTableBean extends ProjectTableEntity {

    public static final String TableName = "tdb_table_bean";
    private static final long serialVersionUID = -380754015950555099L;

    //登记表id
    @DocBean(name = "file_id", value = "登记表id", dataType = String.class)
    private String file_id;
    //来源数据层
    @DocBean(name = "data_layer", value = "来源数据层", dataType = String.class)
    private String data_layer;
    //登记表名
    @DocBean(name = "hyren_name", value = "登记表名", dataType = String.class)
    private String hyren_name;
    //登记表中文名
    @DocBean(name = "table_cn_name", value = "登记表中文名", dataType = String.class)
    private String table_cn_name;

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getData_layer() {
        return data_layer;
    }

    public void setData_layer(String data_layer) {
        this.data_layer = data_layer;
    }

    public String getHyren_name() {
        return hyren_name;
    }

    public void setHyren_name(String hyren_name) {
        this.hyren_name = hyren_name;
    }

    public String getTable_cn_name() {
        return table_cn_name;
    }

    public void setTable_cn_name(String table_cn_name) {
        this.table_cn_name = table_cn_name;
    }
}
