package hrds.commons.zTree;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.DataSourceType;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.User;
import hrds.commons.zTree.bean.TreeDataInfo;
import hrds.commons.zTree.utils.DataConvertedToTreeNode;
import hrds.commons.zTree.utils.SearchNode;
import hrds.commons.zTree.utils.TreeDataQuery;
import hrds.commons.zTree.utils.TreeMenuInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DocClass(desc = "树菜单工具类", author = "BY-HLL", createdate = "2019/12/23 0023 下午 02:19")
public class ZTreeUtil {


	@Method(desc = "获取树的数据信息",
			logicStep = "1.获取各层树菜单信息" +
					"2.转换信息为树节点")
	@Param(name = "treeDataInfo", desc = "TreeDataInfo", range = "TreeDataInfo的对象实例")
	@Param(name = "user", desc = "User", range = "登录用户User的对象实例")
	@Return(desc = "返回值说明", range = "返回值取值范围")
	public List<Map<String, Object>> getTreeDataInfo(User user, TreeDataInfo treeDataInfo) {
		List<Map<String, Object>> treeDataList = new ArrayList<>();
		//1.获取各层树菜单信息
		Map<String, Object> treeMenuInfo = TreeMenuInfo.getTreeMenuInfo(user, treeDataInfo);
		//初始化源树菜单
		if (StringUtil.isNotBlank(treeDataInfo.getPage_from())) {
			DataConvertedToTreeNode.ConversionSourceTreeInfos(treeDataInfo.getPage_from(), treeDataList);
		}
		//2.贴源层数据信息转树节点
		if (DataSourceType.DCL.getValue().equals(treeDataInfo.getAgent_layer())) {
			DataConvertedToTreeNode.ConversionDCLDataInfos(treeDataList);
		}
		//3.贴源层下批量数据源信息转树节点
		List<Map<String, Object>> dclBatchDataInfos = (List<Map<String, Object>>) treeMenuInfo.get("dclBatchDataInfos");
		if (null != dclBatchDataInfos && !dclBatchDataInfos.isEmpty()) {
			DataConvertedToTreeNode.ConversionDCLBatchDataInfos(dclBatchDataInfos, treeDataList,
					treeDataInfo.getRootName());
		}
		//4.集市层源信息转树节点
		List<Map<String, Object>> dmlDataInfos = (List<Map<String, Object>>) treeMenuInfo.get("dmlDataInfos");
		if (null != dmlDataInfos && !dmlDataInfos.isEmpty()) {
			DataConvertedToTreeNode.ConversionDMLSourceInfos(dmlDataInfos, treeDataList, treeDataInfo.getRootName(),
					treeDataInfo.getIsShTable());
		}
		//5.加工层源信息转树节点
		List<Map<String, Object>> dplDataInfos = (List<Map<String, Object>>) treeMenuInfo.get("dplDataInfos");
		if (null != dplDataInfos && !dplDataInfos.isEmpty()) {
			DataConvertedToTreeNode.ConversionDPLSourceInfos(dplDataInfos, treeDataList, treeDataInfo.getRootName());
		}
		//6.数据管控层源信息转树节点
		List<Map<String, Object>> dqcDataInfos = (List<Map<String, Object>>) treeMenuInfo.get("dqcDataInfos");
		if (null != dqcDataInfos && !dqcDataInfos.isEmpty()) {
			DataConvertedToTreeNode.ConversionDQCSourceInfos(dqcDataInfos, treeDataList, treeDataInfo.getRootName());
		}
		//7.贴源层分类信息转树节点
		List<Map<String, Object>> dclBatchClassifyRs =
				(List<Map<String, Object>>) treeMenuInfo.get("dclBatchClassifyInfos");
		if (null != dclBatchClassifyRs && !dclBatchClassifyRs.isEmpty()) {
			DataConvertedToTreeNode.ConversionDCLBatchClassifyInfos(dclBatchClassifyRs, treeDataList,
					treeDataInfo.getRootName());
		}
		//8.贴源层分类下的数据信息转树节点
		List<Map<String, Object>> dclTableInfos = (List<Map<String, Object>>) treeMenuInfo.get("dclTableInfos");
		if (null != dclTableInfos && !dclTableInfos.isEmpty()) {
			DataConvertedToTreeNode.ConversionDCLBatchTableInfos(dclTableInfos, treeDataList,
					treeDataInfo.getRootName());
		}
		//8.集市层下的表信息转树节点
		List<Map<String, Object>> dmlTableInfos = (List<Map<String, Object>>) treeMenuInfo.get("dmlTableInfos");
		if (null != dmlTableInfos && !dmlTableInfos.isEmpty()) {
			DataConvertedToTreeNode.ConversionDMLTableInfos(dmlTableInfos, treeDataList, treeDataInfo.getRootName());
		}
		//9.加工层下分类信息转树节点
		List<Map<String, Object>> dplClassifyInfos = (List<Map<String, Object>>) treeMenuInfo.get("dplClassifyInfos");
		if (null != dplClassifyInfos && !dplClassifyInfos.isEmpty()) {
			DataConvertedToTreeNode.ConversionDPLClassifyInfos(dplClassifyInfos, treeDataList,
					treeDataInfo.getRootName(), treeDataInfo.getIsShTable());
		}
		//10.加工层分类下子分类信息转树节点
		List<Map<String, Object>> dplSubClassifyInfos =
				(List<Map<String, Object>>) treeMenuInfo.get("dplSubClassifyInfos");
		if (null != dplSubClassifyInfos && !dplSubClassifyInfos.isEmpty()) {
			DataConvertedToTreeNode.ConversionDPLSubClassifyInfos(dplSubClassifyInfos, treeDataList,
					treeDataInfo.getRootName());
		}
		//11.加工层分类下表信息转树节点
		List<Map<String, Object>> dplTableInfos = (List<Map<String, Object>>) treeMenuInfo.get("dplTableInfos");
		if (null != dplTableInfos && !dplTableInfos.isEmpty()) {
			DataConvertedToTreeNode.ConversionDPLTableInfos(dplTableInfos, treeDataList,
					treeDataInfo.getRootName());
		}
		//12.系统层源信息转树节点
		List<Map<String, Object>> sflDataInfos = (List<Map<String, Object>>) treeMenuInfo.get("sflDataInfos");
		if (null != sflDataInfos && !sflDataInfos.isEmpty()) {
			DataConvertedToTreeNode.ConversionSFLSourceInfos(sflDataInfos, treeDataList);
		}
		//13.系统层数据表信息转树节点
		List sflTableInfos = (List) treeMenuInfo.get("sflTableInfos");
		if (null != sflTableInfos && !sflTableInfos.isEmpty()) {
			DataConvertedToTreeNode.ConversionSFLTableInfos(sflTableInfos, treeDataList);
		}
		//14.系统层数据备份信息转树节点
		List<Map<String, Object>> sflDataBakInfos = (List<Map<String, Object>>) treeMenuInfo.get("sflDataBakInfos");
		if (null != sflDataBakInfos && !sflDataBakInfos.isEmpty()) {
			DataConvertedToTreeNode.ConversionSFLDataBakInfos(sflDataBakInfos, treeDataList);
		}
		//15.无hadoop环境的树展现
		//TODO 无hadoop
		//16.贴源层下实时数据源信息转树节点
		List<Map<String, Object>> dclRealTimeDataInfos =
				(List<Map<String, Object>>) treeMenuInfo.get("dclRealTimeDataInfos");
		if (null != dclRealTimeDataInfos && !dclRealTimeDataInfos.isEmpty()) {
			DataConvertedToTreeNode.ConversionDCLRealTimeDataInfos(dclRealTimeDataInfos, treeDataList,
					treeDataInfo.getRootName());
		}
		//17.贴源层实时数据下Topic信息转树节点
		List<Map<String, Object>> dclRealTimeTopicInfos =
				(List<Map<String, Object>>) treeMenuInfo.get("dclRealTimeTopicInfos");
		if (null != dclRealTimeTopicInfos && !dclRealTimeTopicInfos.isEmpty()) {
			DataConvertedToTreeNode.ConversionDCLRealTimeTopicInfos(dclRealTimeTopicInfos, treeDataList,
					treeDataInfo.getRootName());
		}
		//18.贴源层实时数据下内部表信息转树节点
		List<Map<String, Object>> dclRealTimeInnerTableInfos =
				(List<Map<String, Object>>) treeMenuInfo.get("dclRealTimeInnerTableInfos");
		if (null != dclRealTimeInnerTableInfos && !dclRealTimeInnerTableInfos.isEmpty()) {
			DataConvertedToTreeNode.ConversionDCLRealTimeInnerTableInfos(dclRealTimeInnerTableInfos, treeDataList,
					treeDataInfo.getRootName());
		}
		return treeDataList;
	}

	@Method(desc = "获取树数据检索信息",
			logicStep = "1.获取各层树菜单信息" +
					"2.获取并转换树节点信息")
	@Param(name = "treeDataInfo", desc = "TreeDataInfo", range = "TreeDataInfo的对象实例")
	@Param(name = "user", desc = "User", range = "登录用户User的对象实例")
	@Return(desc = "返回值说明", range = "返回值取值范围")
	public List<Map<String, Object>> getTreeNodeSearchInfo(User user, TreeDataInfo treeDataInfo) {
		List<Map<String, Object>> treeNodeInfoList = new ArrayList<>();
		//1.获取各层树菜单信息
		if (StringUtil.isNotBlank(treeDataInfo.getPage_from())) {
			DataConvertedToTreeNode.ConversionSourceTreeInfos(treeDataInfo.getPage_from(), treeNodeInfoList);
		}
		//2.获取并转换树节点信息
		List<Map<String, Object>> treeMenuInfos = TreeDataQuery.getTreeMenuInfos(treeDataInfo.getPage_from());
		if (!treeMenuInfos.isEmpty()) {
			for (Map<String, Object> treeMenuInfo : treeMenuInfos) {
				//所属层
				String owningLayer = treeMenuInfo.get("agent_layer").toString();
				//DCL
				if (DataSourceType.DCL.getValue().equals(owningLayer)) {
					Map<String, Object> searchDCLNodeDataMap = SearchNode.searchDCLNodeDataMap(user, treeDataInfo);
					//获取贴源层下数据检索结果
					DataConvertedToTreeNode.ConversionDCLDataInfos(treeNodeInfoList);
					//获取DCL批量数据下源检索结果,转树节点
					List<Map<String, Object>> dclBatchDataInfos =
							(List<Map<String, Object>>) searchDCLNodeDataMap.get("dclBatchDataInfos");
					if (null != dclBatchDataInfos && !dclBatchDataInfos.isEmpty()) {
						DataConvertedToTreeNode.ConversionDCLBatchDataInfos(dclBatchDataInfos, treeNodeInfoList,
								owningLayer);
					}
					//获取DCL批量数据源下分类的检索结果,转树节点
					List<Map<String, Object>> dclBatchClassifyInfos =
							(List<Map<String, Object>>) searchDCLNodeDataMap.get("dclBatchClassifyInfos");
					if (null != dclBatchClassifyInfos && !dclBatchClassifyInfos.isEmpty()) {
						DataConvertedToTreeNode.ConversionDCLBatchClassifyInfos(dclBatchClassifyInfos,
								treeNodeInfoList, owningLayer);
					}
					//获取DCL批量数据源分类下表信息的检索结果,转树节点
					List<Map<String, Object>> dclBatchTableInfos =
							(List<Map<String, Object>>) searchDCLNodeDataMap.get("dclBatchTableInfos");
					if (null != dclBatchTableInfos && !dclBatchTableInfos.isEmpty()) {
						DataConvertedToTreeNode.ConversionDCLBatchTableInfos(dclBatchTableInfos, treeNodeInfoList,
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
