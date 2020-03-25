package hrds.k.biz.tsb.bean;

import fd.ng.core.annotation.DocClass;
import hrds.commons.entity.*;

import java.io.Serializable;
import java.util.List;

@DocClass(desc = "数据对标-表结构对标序列化", author = "BY-HLL", createdate = "2020/3/17 0017 上午 10:19")
public class TSBConf implements Serializable {

    //序列化标记UID,保证结果的一致性
    private static final long serialVersionUID = 1L;

    //对标检测记录表
    private Dbm_normbm_detect dbm_normbm_detect = null;
    //对标检测表信息表
    private Dbm_dtable_info dbm_dtable_info = null;
    //对标检测字段信息表
    private List<Dbm_dtcol_info> dbm_dtcol_info_list = null;
    //表结构对标-对标检测结果表
    private List<Dbm_normbmd_result> dbm_normbmd_result_list = null;

    public Dbm_normbm_detect getDbm_normbm_detect() {
        return dbm_normbm_detect;
    }

    public void setDbm_normbm_detect(Dbm_normbm_detect dbm_normbm_detect) {
        this.dbm_normbm_detect = dbm_normbm_detect;
    }

    public Dbm_dtable_info getDbm_dtable_info() {
        return dbm_dtable_info;
    }

    public void setDbm_dtable_info(Dbm_dtable_info dbm_dtable_info) {
        this.dbm_dtable_info = dbm_dtable_info;
    }

    public List<Dbm_dtcol_info> getDbm_dtcol_info_list() {
        return dbm_dtcol_info_list;
    }

    public void setDbm_dtcol_info_list(List<Dbm_dtcol_info> dbm_dtcol_info_list) {
        this.dbm_dtcol_info_list = dbm_dtcol_info_list;
    }

    public List<Dbm_normbmd_result> getDbm_normbmd_result_list() {
        return dbm_normbmd_result_list;
    }

    public void setDbm_normbmd_result_list(List<Dbm_normbmd_result> dbm_normbmd_result_list) {
        this.dbm_normbmd_result_list = dbm_normbmd_result_list;
    }
}
