package hrds.k.biz.tdb;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.Validator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.DbmMode;
import hrds.commons.codes.DbmState;
import hrds.commons.codes.IsFlag;
import hrds.commons.collection.ProcessingData;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.entity.Dbm_dtable_info;
import hrds.commons.entity.Dbm_normbm_detect;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.TreeData;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.commons.TreePageSource;
import hrds.commons.utils.DataTableUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.commons.utils.tree.Node;
import hrds.k.biz.tdb.bean.TdbTableBean;

import java.util.List;
import java.util.Map;

@DocClass(desc = "表数据对标(TableDataBenchmarking)", author = "BY-HLL", createdate = "2020/7/20 0020 上午 09:40")
public class TDBAction extends BaseAction {

    @Method(desc = "获取表数据对标树", logicStep = "获取表数据对标树")
    @Return(desc = "树信息", range = "树信息")
    public List<Node> getTDBTreeData() {
        //配置树不显示文件采集的数据
        TreeConf treeConf = new TreeConf();
        treeConf.setShowFileCollection(Boolean.FALSE);
        //返回分叉树列表
        return TreeData.initTreeData(TreePageSource.DATA_BENCHMARKING, treeConf, getUser());
    }

    @Method(desc = "保存此次对标记录对应的表信息", logicStep = "保存此次对标记录对应的表信息")
    @Param(name = "tdb_table_bean_s", desc = "待保存的表信息Bean数据", range = "TdbTableBean[]", isBean = true)
    @Return(desc = "对标记录id", range = "long类型,唯一")
    public long saveTDBTable(TdbTableBean[] tdb_table_bean_s) {
        //数据校验
        if (null == tdb_table_bean_s || tdb_table_bean_s.length == 0) {
            throw new BusinessException("表信息列表不能为空!");
        }
        //设置对标检测记录
        Dbm_normbm_detect dbm_normbm_detect = setDbmNormbmDetect();
        dbm_normbm_detect.add(Dbo.db());
        //设置并保存对标检测表信息
        for (TdbTableBean tdb_table_bean : tdb_table_bean_s) {
            String file_id = tdb_table_bean.getFile_id();
            String data_layer = tdb_table_bean.getData_layer();
            //设置对标检测表信息
            Dbm_dtable_info dbm_dtable_info = setDbmDtableInfo(dbm_normbm_detect.getDetect_id(), file_id, data_layer);
            //保存对标检测表信息
            dbm_dtable_info.add(Dbo.db());
        }
        return dbm_normbm_detect.getDetect_id();
    }

    @Method(desc = "设置对标检测记录", logicStep = "设置对标检测记录")
    private Dbm_normbm_detect setDbmNormbmDetect() {
        //设置 Dbm_normbm_detect
        Dbm_normbm_detect dbm_normbm_detect = new Dbm_normbm_detect();
        dbm_normbm_detect.setDetect_id(PrimayKeyGener.getNextId());
        dbm_normbm_detect.setDetect_name(String.valueOf(dbm_normbm_detect.getDetect_id()));
        dbm_normbm_detect.setDetect_status(DbmState.NotRuning.getCode());
        dbm_normbm_detect.setDbm_mode(DbmMode.BiaoJieGouDuiBiao.getCode());
        dbm_normbm_detect.setCreate_user(getUserId().toString());
        dbm_normbm_detect.setDetect_sdate(DateUtil.getSysDate());
        dbm_normbm_detect.setDetect_stime(DateUtil.getSysTime());
        dbm_normbm_detect.setDetect_edate(DateUtil.getSysDate());
        dbm_normbm_detect.setDetect_etime(DateUtil.getSysTime());
        dbm_normbm_detect.setDnd_remark("");
        return dbm_normbm_detect;
    }

    @Method(desc = "设置对标检测表信息表", logicStep = "设置对标检测表信息表")
    @Param(name = "detect_id", desc = "检测主键id", range = "long类型")
    @Param(name = "file_id", desc = "表源属性id", range = "String类型")
    @Param(name = "data_layer", desc = "数据层", range = "String类型,DCL,DML")
    private Dbm_dtable_info setDbmDtableInfo(long detect_id, String file_id, String data_layer) {
        //数据校验
        Validator.notBlank(data_layer, "表来源数据层信息不能为空");
        //根据表源属性id获取表信息
        Map<String, Object> tableInfo = DataTableUtil.getTableInfoByFileId(data_layer, file_id);
        if (tableInfo.isEmpty()) {
            throw new BusinessException("查询的表信息已经不存在!");
        }
        //设置 Dbm_dtable_info
        Dbm_dtable_info dbm_dtable_info = new Dbm_dtable_info();
        dbm_dtable_info.setDbm_tableid(PrimayKeyGener.getNextId());
        dbm_dtable_info.setTable_cname(tableInfo.get("table_ch_name").toString());
        dbm_dtable_info.setTable_ename(tableInfo.get("table_name").toString());
        DataSourceType dataSourceType = DataSourceType.ofEnumByCode(data_layer);
        dbm_dtable_info.setSource_type(dataSourceType.getCode());
        //TODO 是否外部表预留,默认给 0: 否
        dbm_dtable_info.setIs_external(IsFlag.Fou.getCode());
        //如果源表的描述为null,则设置为""
        if (null == tableInfo.get("remark")) {
            dbm_dtable_info.setTable_remark("");
        } else {
            dbm_dtable_info.setTable_remark(tableInfo.get("remark").toString());
        }
        dbm_dtable_info.setDetect_id(detect_id);
        dbm_dtable_info.setTable_id(tableInfo.get("table_id").toString());
        //获取表存储的存储层信息
        List<LayerBean> layerBeans = ProcessingData.getLayerByTable(dbm_dtable_info.getTable_ename(), Dbo.db());
        //TODO 如果有多个存储层,去查询结果的第一条
        long dsl_id = layerBeans.get(0).getDsl_id();
        dbm_dtable_info.setDsl_id(dsl_id);
        return dbm_dtable_info;
    }
}
