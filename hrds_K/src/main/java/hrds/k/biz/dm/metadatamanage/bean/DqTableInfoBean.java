package hrds.k.biz.dm.metadatamanage.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@Table(tableName = "dq_table_info_bean")
public class DqTableInfoBean extends ProjectTableEntity {

    public static final String TableName = "dq_table_info_bean";
    private static final long serialVersionUID = 3054079742119923746L;

    //表空间
    @DocBean(name = "table_space", value = "表空间", dataType = String.class)
    private String table_space;
    //表名
    @DocBean(name = "table_name", value = "表名", dataType = String.class)
    private String table_name;
    //表中文名
    @DocBean(name = "ch_name", value = "表中文名", dataType = String.class)
    private String ch_name;
    //是否数据溯源
    @DocBean(name = "is_trace", value = "是否数据溯源", dataType = String.class)
    private String is_trace;
    //备注
    @DocBean(name = "dq_remark", value = "备注", dataType = String.class)
    private String dq_remark;
    //HBase的RowKey排序信息
    @DocBean(name = "hbase_sort_columns", value = "HBase的RowKey排序信息", dataType = String[].class)
    private String[] hbase_sort_columns;
    //是否外部表
    @DocBean(name = "is_external", value = "是否外部表", dataType = String.class)
    private String is_external;
    //存储目录
    @DocBean(name = "storage_path", value = "存储目录", dataType = String.class)
    private String storage_path;
    //存储类型
    @DocBean(name = "storage_type", value = "存储类型", dataType = String.class)
    private String storage_type;
    //行分隔符
    @DocBean(name = "line_separator", value = "行分隔符", dataType = String.class)
    private String line_separator;
    //列分隔符
    @DocBean(name = "column_separator", value = "列分隔符", dataType = String.class)
    private String column_separator;
    //转义符
    @DocBean(name = "escape_character", value = "转义符", dataType = String.class)
    private String escape_character;
    //是否包含表头
    @DocBean(name = "is_header", value = "是否包含表头", dataType = String.class)
    private String is_header;


    public String getTable_space() {
        return table_space;
    }

    public void setTable_space(String table_space) {
        this.table_space = table_space;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getCh_name() {
        return ch_name;
    }

    public void setCh_name(String ch_name) {
        this.ch_name = ch_name;
    }

    public String getIs_trace() {
        return is_trace;
    }

    public void setIs_trace(String is_trace) {
        this.is_trace = is_trace;
    }

    public String getDq_remark() {
        return dq_remark;
    }

    public void setDq_remark(String dq_remark) {
        this.dq_remark = dq_remark;
    }

    public String[] getHbase_sort_columns() {
        return hbase_sort_columns;
    }

    public void setHbase_sort_columns(String[] hbase_sort_columns) {
        this.hbase_sort_columns = hbase_sort_columns;
    }

    public String getIs_external() {
        return is_external;
    }

    public void setIs_external(String is_external) {
        this.is_external = is_external;
    }

    public String getStorage_path() {
        return storage_path;
    }

    public void setStorage_path(String storage_path) {
        this.storage_path = storage_path;
    }

    public String getStorage_type() {
        return storage_type;
    }

    public void setStorage_type(String storage_type) {
        this.storage_type = storage_type;
    }

    public String getLine_separator() {
        return line_separator;
    }

    public void setLine_separator(String line_separator) {
        this.line_separator = line_separator;
    }

    public String getColumn_separator() {
        return column_separator;
    }

    public void setColumn_separator(String column_separator) {
        this.column_separator = column_separator;
    }

    public String getEscape_character() {
        return escape_character;
    }

    public void setEscape_character(String escape_character) {
        this.escape_character = escape_character;
    }

    public String getIs_header() {
        return is_header;
    }

    public void setIs_header(String is_header) {
        this.is_header = is_header;
    }
}
