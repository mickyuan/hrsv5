package hrds.k.biz.tdb;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.DbmMode;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Dbm_normbm_detect;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.TreeData;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.commons.TreePageSource;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.commons.utils.tree.Node;

import java.util.List;

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
    @Param(name = "data_layer", desc = "待保存字段信息集合", range = "DbmColInfo自定义实体类型")
    @Param(name = "tableNames", desc = "待保存字段信息集合", range = "DbmColInfo自定义实体类型")
    @Return(desc = "对标记录id", range = "long类型,唯一")
    public long saveTDBTable(String[] tableNames) {
        //数据校验
        if (tableNames.length <= 0) {
            throw new BusinessException("表名列表不能为空!");
        }
        //
        //设置对标
        return 0;
    }


    @Method(desc = "设置对标检测记录", logicStep = "设置对标检测记录")
    private Dbm_normbm_detect setDbmNormbmDetect() {
        //设置 Dbm_normbm_detect
        Dbm_normbm_detect dbm_normbm_detect = new Dbm_normbm_detect();
        dbm_normbm_detect.setDetect_id(String.valueOf(PrimayKeyGener.getNextId()));
        dbm_normbm_detect.setDetect_name(String.valueOf(dbm_normbm_detect.getDetect_id()));
        dbm_normbm_detect.setDetect_status(IsFlag.Shi.getCode());
        dbm_normbm_detect.setDbm_mode(DbmMode.BiaoJieGouDuiBiao.getCode());
        dbm_normbm_detect.setCreate_user(getUserId().toString());
        dbm_normbm_detect.setDetect_sdate(DateUtil.getSysDate());
        dbm_normbm_detect.setDetect_stime(DateUtil.getSysTime());
        return dbm_normbm_detect;
    }
}
