package hrds.commons.tree.foreground;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.DataSourceType;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.foreground.bean.TreeDataInfo;
import hrds.commons.utils.User;
import hrds.commons.tree.foreground.utils.DataConvertedToTreeNode;
import hrds.commons.tree.foreground.utils.SearchNode;
import hrds.commons.tree.foreground.query.TreeDataQuery;
import hrds.commons.tree.foreground.utils.TreeMenuInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DocClass(desc = "树菜单工具类", author = "BY-HLL", createdate = "2019/12/23 0023 下午 02:19")
public class ForegroundTreeUtil {

    @Method(desc = "获取树的数据信息",
            logicStep = "获取各层树菜单信息" +
                    "0.初始化源树菜单" +
                    "1.贴源层(DCL)信息转树节点" +
                    "2.集市层(DML)信息转树节点" +
                    "3.加工层(DPL)信息转树节点" +
                    "4.管控层(DQC)信息转树节点" +
                    "5.系统层(SFL)信息转树节点" +
                    "6.自定义层(UDL)信息转树节点" +
                    "7.模型层(AML)信息转树节点" +
                    "8.无大数据环境(NoHadoop)环境树信息转树节点")
    @Param(name = "treeDataInfo", desc = "TreeDataInfo", range = "TreeDataInfo的对象实例")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Return(desc = "树菜单信息", range = "返回值取值范围")
    public List<Map<String, Object>> getTreeDataInfo(User user, TreeDataInfo treeDataInfo) {
        List<Map<String, Object>> treeDataList = new ArrayList<>();
        //获取各层树数据信息
        Map<String, Object> treeInfos = TreeMenuInfo.getTreeMenuInfo(user, treeDataInfo);
        //0.初始化源树菜单
        if (StringUtil.isNotBlank(treeDataInfo.getPage_from())) {
            List<Map<String, Object>> treeMenuInfos = TreeDataQuery.getSourceTreeInfos(treeDataInfo.getPage_from());
            DataConvertedToTreeNode.conversionSourceTreeInfos(treeMenuInfos, treeDataList);
        }
        //1.贴源层(DCL)信息转树节点
        //1-1.贴源层数据信息转树节点
        if (DataSourceType.DCL.getValue().equals(treeDataInfo.getAgent_layer())) {
            List<Map<String, Object>> dclDataInfos = (List<Map<String, Object>>) treeInfos.get("dclDataInfos");
            DataConvertedToTreeNode.conversionDCLSourceInfos(dclDataInfos, treeDataList);
        }
        //1-2.贴源层下批量数据源信息转树节点
        List<Map<String, Object>> dclBatchDataInfos = (List<Map<String, Object>>) treeInfos.get("dclBatchDataInfos");
        if (null != dclBatchDataInfos && !dclBatchDataInfos.isEmpty()) {
            DataConvertedToTreeNode.conversionDCLBatchDataInfos(dclBatchDataInfos, treeDataList,
                    treeDataInfo.getRootName());
        }
        //1-3.贴源层批量数据源分类信息转树节点
        List<Map<String, Object>> dclBatchClassifyRs =
                (List<Map<String, Object>>) treeInfos.get("dclBatchClassifyInfos");
        if (null != dclBatchClassifyRs && !dclBatchClassifyRs.isEmpty()) {
            DataConvertedToTreeNode.conversionDCLBatchClassifyInfos(dclBatchClassifyRs, treeDataList,
                    treeDataInfo.getRootName());
        }
        //1-4.贴源层批量数据源分类信息下表信息转树节点
        List<Map<String, Object>> dclTableInfos = (List<Map<String, Object>>) treeInfos.get("dclBatchTableInfos");
        if (null != dclTableInfos && !dclTableInfos.isEmpty()) {
            DataConvertedToTreeNode.conversionDCLBatchTableInfos(dclTableInfos, treeDataList,
                    treeDataInfo.getRootName());
        }
//		//1-5.贴源层实时数据信息转树节点
//		List<Map<String, Object>> dclRealTimeDataInfos =
//				(List<Map<String, Object>>) treeInfos.get("dclRealTimeDataInfos");
//		if (null != dclRealTimeDataInfos && !dclRealTimeDataInfos.isEmpty()) {
//			DataConvertedToTreeNode.ConversionDCLRealTimeDataInfos(dclRealTimeDataInfos, treeDataList,
//					treeDataInfo.getRootName());
//		}
//		//1-6.贴源层实时数据下Topic信息转树节点
//		List<Map<String, Object>> dclRealTimeTopicInfos =
//				(List<Map<String, Object>>) treeInfos.get("dclRealTimeTopicInfos");
//		if (null != dclRealTimeTopicInfos && !dclRealTimeTopicInfos.isEmpty()) {
//			DataConvertedToTreeNode.ConversionDCLRealTimeTopicInfos(dclRealTimeTopicInfos, treeDataList,
//					treeDataInfo.getRootName());
//		}
//		//1-7.贴源层实时数据下内部表信息转树节点
//		List<Map<String, Object>> dclRealTimeInnerTableInfos =
//				(List<Map<String, Object>>) treeInfos.get("dclRealTimeInnerTableInfos");
//		if (null != dclRealTimeInnerTableInfos && !dclRealTimeInnerTableInfos.isEmpty()) {
//			DataConvertedToTreeNode.ConversionDCLRealTimeInnConversionDMLSourceInfoserTableInfos(dclRealTimeInnerTableInfos, treeDataList,
//					treeDataInfo.getRootName());
//		}
//		//2.集市层(DML)信息转树节点
//		//2-1.集市层信息转树节点
		List<Map<String, Object>> dmlDataInfos = (List<Map<String, Object>>) treeInfos.get("dmlDataInfos");
		if (null != dmlDataInfos && !dmlDataInfos.isEmpty()) {
			DataConvertedToTreeNode.ConversionDMLSourceInfos(dmlDataInfos, treeDataList, treeDataInfo.getRootName());
		}
//		//2-2.集市层集市下的表信息转树节点
		List<Map<String, Object>> dmlTableInfos = (List<Map<String, Object>>) treeInfos.get("dmlTableInfos");
        if (null != dmlTableInfos && !dmlTableInfos.isEmpty()) {
            DataConvertedToTreeNode.ConversionDMLTableInfos(dmlTableInfos, treeDataList, treeDataInfo.getRootName());
        }
//		//3.加工层(DPL)信息转树节点
//		//3-1.加工层源信息转树节点
//		List<Map<String, Object>> dplDataInfos = (List<Map<String, Object>>) treeInfos.get("dplDataInfos");
//		if (null != dplDataInfos && !dplDataInfos.isEmpty()) {
//			DataConvertedToTreeNode.ConversionDPLSourceInfos(dplDataInfos, treeDataList, treeDataInfo.getRootName());
//		}
//		//3-2.加工层工程下分类信息转树节点
//		List<Map<String, Object>> dplClassifyInfos = (List<Map<String, Object>>) treeInfos.get("dplClassifyInfos");
//		if (null != dplClassifyInfos && !dplClassifyInfos.isEmpty()) {
//			DataConvertedToTreeNode.ConversionDPLClassifyInfos(dplClassifyInfos, treeDataList,
//					treeDataInfo.getRootName(), treeDataInfo.getIsShTable());
//		}
//		//3-3.加工层工程分类下子分类信息转树节点
//		List<Map<String, Object>> dplSubClassifyInfos =
//				(List<Map<String, Object>>) treeInfos.get("dplSubClassifyInfos");
//		if (null != dplSubClassifyInfos && !dplSubClassifyInfos.isEmpty()) {
//			DataConvertedToTreeNode.ConversionDPLSubClassifyInfos(dplSubClassifyInfos, treeDataList,
//					treeDataInfo.getRootName());
//		}
//		//3-4.加工层工程分类下表信息转树节点
//		List<Map<String, Object>> dplTableInfos = (List<Map<String, Object>>) treeInfos.get("dplTableInfos");
//		if (null != dplTableInfos && !dplTableInfos.isEmpty()) {
//			DataConvertedToTreeNode.ConversionDPLTableInfos(dplTableInfos, treeDataList,
//					treeDataInfo.getRootName());
//		}
//		//4.管控层(DQC)信息转树节点
//		//4-1.数据管控层信息转树节点
//		List<Map<String, Object>> dqcDataInfos = (List<Map<String, Object>>) treeInfos.get("dqcDataInfos");
//		if (null != dqcDataInfos && !dqcDataInfos.isEmpty()) {
//			DataConvertedToTreeNode.ConversionDQCSourceInfos(dqcDataInfos, treeDataList, treeDataInfo.getRootName());
//		}
//		//5.系统层(SFL)信息转树节点
//		//5-1.系统层信息转树节点
//		List<Map<String, Object>> sflDataInfos = (List<Map<String, Object>>) treeInfos.get("sflDataInfos");
//		if (null != sflDataInfos && !sflDataInfos.isEmpty()) {
//			DataConvertedToTreeNode.ConversionSFLSourceInfos(sflDataInfos, treeDataList);
//		}
//		//5-2.系统层系统数据下表信息转树节点
//		List sflTableInfos = (List) treeInfos.get("sflTableInfos");
//		if (null != sflTableInfos && !sflTableInfos.isEmpty()) {
//			DataConvertedToTreeNode.ConversionSFLTableInfos(sflTableInfos, treeDataList);
//		}
//		//5-3.系统层下数据备份信息转树节点
//		List<Map<String, Object>> sflDataBakInfos = (List<Map<String, Object>>) treeInfos.get("sflDataBakInfos");
//		if (null != sflDataBakInfos && !sflDataBakInfos.isEmpty()) {
//			DataConvertedToTreeNode.ConversionSFLDataBakInfos(sflDataBakInfos, treeDataList);
//		}
//        //6.自定义层(UDL)信息转树节点
//        //6-1.自定义层下数据库信息转树节点
//        List<Map<String, Object>> udlDatabaseInfos = (List<Map<String, Object>>) treeInfos.get("udlDatabaseInfos");
//        if (null != udlDatabaseInfos && !udlDatabaseInfos.isEmpty()) {
//            DataConvertedToTreeNode.ConversionUDLDatabaseInfos(udlDatabaseInfos, treeDataList);
//        }
//        //6-2.自定义层数据库下表空间信息转树节点
//        List<Map<String, Object>> udlDatabaseTableSpaceInfos =
//                (List<Map<String, Object>>) treeInfos.get("udlDatabaseTableSpaceInfos");
//        if (null != udlDatabaseTableSpaceInfos && !udlDatabaseTableSpaceInfos.isEmpty()) {
//            DataConvertedToTreeNode.ConversionUDLDatabaseTableSpaceInfos(udlDatabaseTableSpaceInfos, treeDataList,
//                    treeDataInfo.getRootName(), treeDataInfo.getParent_id());
//        }
//        //6-3.自定义层数据库表空间下数据表信息转树节点
//        List<Map<String, Object>> udlTableSpaceTableInfos =
//                (List<Map<String, Object>>) treeInfos.get("udlTableSpaceTableInfos");
//        if (null != udlTableSpaceTableInfos && !udlTableSpaceTableInfos.isEmpty()) {
//            DataConvertedToTreeNode.ConversionUDLTableSpaceTableInfos(udlTableSpaceTableInfos, treeDataList,
//                    treeDataInfo.getRootName(), treeDataInfo.getParent_id());
//        }
        //7.模型层(AML)信息转树节点
        //8.无大数据环境(NoHadoop)环境树信息转树节点
        return treeDataList;
    }

    @Method(desc = "获取树数据检索数据信息",
            logicStep = "1.获取各层树菜单信息" +
                    "2.获取并转换树节点信息")
    @Param(name = "treeDataInfo", desc = "TreeDataInfo", range = "TreeDataInfo的对象实例")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Return(desc = "树数据检索数据信息List", range = "返回值取值范围")
    public List<Map<String, Object>> getTreeNodeSearchInfo(User user, TreeDataInfo treeDataInfo) {
        List<Map<String, Object>> treeNodeInfoList = new ArrayList<>();
        //1.获取各层树菜单信息转树节点
        List<Map<String, Object>> treeMenuInfos = new ArrayList<>();
        if (StringUtil.isNotBlank(treeDataInfo.getPage_from())) {
            treeMenuInfos = TreeDataQuery.getSourceTreeInfos(treeDataInfo.getPage_from());
            DataConvertedToTreeNode.conversionSourceTreeInfos(treeMenuInfos, treeNodeInfoList);
        }
        //2.获取树数据并转换树节点信息
        if (!treeMenuInfos.isEmpty()) {
            for (Map<String, Object> treeMenuInfo : treeMenuInfos) {
                //所属层
                String owningLayer = treeMenuInfo.get("agent_layer").toString();
                //DCL
                if (DataSourceType.DCL.getValue().equals(owningLayer)) {
                    Map<String, Object> searchDCLNodeDataMap = SearchNode.searchDCLNodeDataMap(user, treeDataInfo);
                    //获取DCL贴源层下数据
                    List<Map<String, Object>> dclDataInfos =
                            (List<Map<String, Object>>) searchDCLNodeDataMap.get("dclDataInfos");
                    if (null != dclDataInfos && !dclDataInfos.isEmpty()) {
                        DataConvertedToTreeNode.conversionDCLSourceInfos(dclDataInfos, treeNodeInfoList);
                    }
                    //获取DCL批量数据下数据源信息,转树节点
                    List<Map<String, Object>> dclBatchDataInfos =
                            (List<Map<String, Object>>) searchDCLNodeDataMap.get("dclBatchDataInfos");
                    if (null != dclBatchDataInfos && !dclBatchDataInfos.isEmpty()) {
                        DataConvertedToTreeNode.conversionDCLBatchDataInfos(dclBatchDataInfos, treeNodeInfoList,
                                owningLayer);
                    }
                    //获取DCL批量数据数据源下分类信息,转树节点
                    List<Map<String, Object>> dclBatchClassifyInfos =
                            (List<Map<String, Object>>) searchDCLNodeDataMap.get("dclBatchClassifyInfos");
                    if (null != dclBatchClassifyInfos && !dclBatchClassifyInfos.isEmpty()) {
                        DataConvertedToTreeNode.conversionDCLBatchClassifyInfos(dclBatchClassifyInfos,
                                treeNodeInfoList, owningLayer);
                    }
                    //获取DCL批量数据数据源分类下表信息,转树节点
                    List<Map<String, Object>> dclBatchTableInfos =
                            (List<Map<String, Object>>) searchDCLNodeDataMap.get("dclBatchTableInfos");
                    if (null != dclBatchTableInfos && !dclBatchTableInfos.isEmpty()) {
                        DataConvertedToTreeNode.conversionDCLBatchTableInfos(dclBatchTableInfos, treeNodeInfoList,
                                owningLayer);
                    }
                }
                //DML
                if (DataSourceType.DML.getValue().equals(owningLayer)) {
                    throw new BusinessException("DML层检索未做!");
                }
                //DPL
                if (DataSourceType.DPL.getValue().equals(owningLayer)) {
                    throw new BusinessException("DPL层检索未做!");
                }
                //DQC
                if (DataSourceType.DQC.getValue().equals(owningLayer)) {
                    throw new BusinessException("DQC层检索未做!");
                }
            }
        }
        return treeNodeInfoList;
    }
}
