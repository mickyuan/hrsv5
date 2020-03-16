package hrds.commons.tree.background.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.User;
import hrds.commons.tree.background.query.DCLDataQuery;
import hrds.commons.tree.background.query.SFLDataQuery;

import java.util.List;
import java.util.Map;

@DocClass(desc = "树节点数据查询", author = "BY-HLL", createdate = "2020/3/13 0013 上午 09:44")
public class TreeNodeDataQuery {

    @Method(desc = "获取ISL数据层的节点数据", logicStep = "获取ISL数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    public static void getISLDataList(User user, List<Map<String, Object>> dataList) {
        throw new BusinessException("获取ISL层数据失败,暂未配置该存储层!");
    }

    @Method(desc = "获取DCL数据层的节点数据", logicStep = "获取DCL数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    public static void getDCLDataList(User user, List<Map<String, Object>> dataList) {
        //添加DCL层下节点数据
        dataList.addAll(DCLDataQuery.getDCLDataInfos());
        //获取DCL层批量数据下的数据源列表
        List<Map<String, Object>> dclBatchDataInfos = DCLDataQuery.getDCLBatchDataInfos(user);

        //添加DCL层批量数据下数据源节点数据到DCL数据层的层次数据中
        dataList.addAll(DataConvertedNodeData.ConversionDCLBatchDataInfos(dclBatchDataInfos));
        dclBatchDataInfos.forEach(dclBatchDataInfo -> {
            //获取批量数据数据源下分类信息
            //获取数据源id
            String source_id = dclBatchDataInfo.get("source_id").toString();
            //TODO 是否获取文件采集数据 isFileCollection[true:获取,false不获取]
            List<Map<String, Object>> dclBatchClassifyInfos =
                    DCLDataQuery.getDCLBatchClassifyInfos(source_id, false, user);
            if (!dclBatchClassifyInfos.isEmpty()) {
                dataList.addAll(DataConvertedNodeData.ConversionDCLBatchClassifyInfos(dclBatchClassifyInfos));
            }
            //获取批量数据数据源的分类下数据表信息
            dclBatchClassifyInfos.forEach(dclBatchClassifyInfo -> {
                //获取分类id
                String classify_id = dclBatchClassifyInfo.get("classify_id").toString();
                //TODO 数据是否已经入HBase isIntoHBase[1:是,0:否],目前不做这个判断,默认给空字符串
                List<Map<String, Object>> dclBatchTableInfos =
                        DCLDataQuery.getDCLBatchTableInfos(classify_id, user, "");
                if (!dclBatchTableInfos.isEmpty()) {
                    dataList.addAll(DataConvertedNodeData.ConversionDCLBatchTableInfos(dclBatchTableInfos));
                }
            });
        });
    }

    @Method(desc = "获取DPL数据层的节点数据", logicStep = "获取DPL数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    public static void getDPLDataList(User user, List<Map<String, Object>> dataList) {
        throw new BusinessException("获取DPL层数据失败,暂未配置该存储层!");
    }

    @Method(desc = "获取DML数据层的节点数据", logicStep = "获取DML数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    public static void getDMLDataList(User user, List<Map<String, Object>> dataList) {
        throw new BusinessException("获取DML层数据失败,暂未配置该存储层!");
    }

    @Method(desc = "获取SFL数据层的节点数据", logicStep = "获取SFL数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    public static void getSFLDataList(User user, List<Map<String, Object>> dataList) {
        //添加SFL层下节点数据
        dataList.addAll(SFLDataQuery.getSFLDataInfos());
    }

    @Method(desc = "获取AML数据层的节点数据", logicStep = "获取AML数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    public static void getAMLDataList(User user, List<Map<String, Object>> dataList) {
        throw new BusinessException("获取AML层数据失败,暂未配置该存储层!");
    }

    @Method(desc = "获取DQC数据层的节点数据", logicStep = "获取DQC数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    public static void getDQCDataList(User user, List<Map<String, Object>> dataList) {
        throw new BusinessException("获取DQC层数据失败,暂未配置该存储层!");
    }

    @Method(desc = "获取UDL数据层的节点数据", logicStep = "获取UDL数据层的节点数据")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataList", desc = "节点数据List", range = "节点数据List")
    public static void getUDLDataList(User user, List<Map<String, Object>> dataList) {
        throw new BusinessException("获取DML层数据失败,暂未配置该存储层!");
    }
}
