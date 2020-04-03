package hrds.k.biz.tsb.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@Table(tableName = "dbm_col_info")
public class DbmColInfo extends ProjectTableEntity {

    private static final long serialVersionUID = 321561870187364L;
    public static final String TableName = "dbm_col_info";

    //字段id
    @DocBean(name = "column_id", value = "字段id(primary):", dataType = Long.class)
    private Long column_id;
    //字段描述
    @DocBean(name = "tc_remark", value = "字段描述(primary):", dataType = String.class)
    private String tc_remark;
    //是否主键标识
    @DocBean(name = "is_primary_key", value = "是否主键标识:", dataType = String.class)
    private String is_primary_key;
    //字段所属agent_id
    @DocBean(name = "agent_id", value = "字段所属agent_id:", dataType = Long.class)
    private Long agent_id;
    //字段所属数据库设置
    @DocBean(name = "database_id", value = "字段所属 database_id:", dataType = Long.class)
    private Long database_id;
    //字段名
    @DocBean(name = "column_name", value = "字段英文名:", dataType = String.class)
    private String column_name;
    //字段中文名
    @DocBean(name = "column_ch_name", value = "字段中文名:", dataType = String.class)
    private String column_ch_name;
    //字段所属数据源id
    @DocBean(name = "source_id", value = "字段所属 source_id:", dataType = Long.class)
    private Long source_id;
    //字段类型
    @DocBean(name = "column_type", value = "字段类型:", dataType = String.class)
    private String column_type;

    public Long getColumn_id() {
        return column_id;
    }

    public void setColumn_id(Long column_id) {
        this.column_id = column_id;
    }

    public String getTc_remark() {
        return tc_remark;
    }

    public void setTc_remark(String tc_remark) {
        this.tc_remark = tc_remark;
    }

    public String getIs_primary_key() {
        return is_primary_key;
    }

    public void setIs_primary_key(String is_primary_key) {
        this.is_primary_key = is_primary_key;
    }

    public Long getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(Long agent_id) {
        this.agent_id = agent_id;
    }

    public Long getDatabase_id() {
        return database_id;
    }

    public void setDatabase_id(Long database_id) {
        this.database_id = database_id;
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

    public Long getSource_id() {
        return source_id;
    }

    public void setSource_id(Long source_id) {
        this.source_id = source_id;
    }

    public String getColumn_type() {
        return column_type;
    }

    public void setColumn_type(String column_type) {
        this.column_type = column_type;
    }
}
