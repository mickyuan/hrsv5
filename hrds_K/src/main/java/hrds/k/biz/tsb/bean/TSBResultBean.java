package hrds.k.biz.tsb.bean;

import fd.ng.core.annotation.DocBean;
import fd.ng.db.entity.anno.Table;
import hrds.commons.entity.fdentity.ProjectTableEntity;

@Table(tableName = "tsb_result_bean")
public class TSBResultBean extends ProjectTableEntity {

    private static final long serialVersionUID = -2279717234760388402L;
    public static final String TableName = "tsb_result_bean";

    //字段id
    @DocBean(name = "col_id", value = "字段id", dataType = String.class)
    private String col_id;
    //结果id 如果人工对标标记是1,result_id是页面选中标准的basic_id
    @DocBean(name = "result_id", value = "result_id", dataType = String.class)
    private String result_id;
    //是否人工对标
    @DocBean(name = "is_artificial", value = "是否人工对标", dataType = String.class)
    private String is_artificial;
    //字段id
    @DocBean(name = "col_ename", value = "字段id", dataType = String.class)
    private String col_ename;

    public String getCol_id() {
        return col_id;
    }

    public void setCol_id(String col_id) {
        this.col_id = col_id;
    }

    public String getResult_id() {
        return result_id;
    }

    public void setResult_id(String result_id) {
        this.result_id = result_id;
    }

    public String getIs_artificial() {
        return is_artificial;
    }

    public void setIs_artificial(String is_artificial) {
        this.is_artificial = is_artificial;
    }

    public String getCol_ename() {
        return col_ename;
    }

    public void setCol_ename(String col_ename) {
        this.col_ename = col_ename;
    }
}
