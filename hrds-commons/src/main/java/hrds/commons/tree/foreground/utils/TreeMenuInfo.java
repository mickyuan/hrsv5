package hrds.commons.tree.foreground.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.DataSourceType;
import hrds.commons.tree.foreground.bean.TreeDataInfo;
import hrds.commons.tree.foreground.query.DCLDataQuery;
import hrds.commons.tree.foreground.query.DMLDataQuery;
import hrds.commons.utils.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "获取树菜单信息", author = "BY-HLL", createdate = "2019/12/23 0023 下午 03:42")
public class TreeMenuInfo {

	@Method(desc = "获取树菜单信息",
			logicStep = "1.DCL 贴源层" +
					"2.DML 集市层" +
					"3.DPL 加工层" +
					"4.DQC 管控层" +
					"5.SFL 系统层" +
					"6.UDL 自定义层")
	@Param(name = "user", desc = "User", range = "登录用户User的对象实例")
	@Param(name = "treeDataInfo", desc = "TreeDataInfo", range = "TreeDataInfo的对象实例")
	@Return(desc = "树菜单信息List", range = "无限制")
	public static Map<String, Object> getTreeMenuInfo(User user, TreeDataInfo treeDataInfo) {
		Map<String, Object> treeMenuInfoMap = new HashMap<>();
		//1.DCL 贴源层
		//1-1.获取贴源层下数据信息
		if (DataSourceType.DCL.getValue().equals(treeDataInfo.getAgent_layer())) {
			List<Map<String, Object>> dclDataInfos = DCLDataQuery.getDCLDataInfos();
			treeMenuInfoMap.put("dclDataInfos", dclDataInfos);
		}
		//1-2.获取贴源层批量数据下数据源信息
		if (StringUtil.isNotBlank(treeDataInfo.getBatch_id())) {
			List<Map<String, Object>> dclBatchDataInfos = DCLDataQuery.getDCLBatchDataInfos(user);
			treeMenuInfoMap.put("dclBatchDataInfos", dclBatchDataInfos);
		}
		//1-3.获取贴源层批量数据数据源下分类信息
//		boolean isFileCo = Boolean.parseBoolean(treeDataInfo.getIsFileCo());
		if (!StringUtil.isBlank(treeDataInfo.getSource_id())) {
			List<Map<String, Object>> dclBatchClassifyInfos =
					DCLDataQuery.getDCLBatchClassifyInfos(treeDataInfo.getSource_id(), user);
			treeMenuInfoMap.put("dclBatchClassifyInfos", dclBatchClassifyInfos);
		}
		//1-4.获取贴源层批量数据数据源分类下表信息
		if (StringUtil.isNotBlank(treeDataInfo.getClassify_id())) {
			List<Map<String, Object>> dclBatchTableInfos =
					DCLDataQuery.getDCLBatchTableInfos(treeDataInfo.getClassify_id(), null, user);
			treeMenuInfoMap.put("dclBatchTableInfos", dclBatchTableInfos);
		}
		//TODO 实时数据功能添加后放开
//		//1-5.获取贴源层实时数据下实时数据组信息(groupId)
//		if (StringUtil.isNotBlank(treeDataInfo.getKafka_id())) {
//			List<Map<String, Object>> dclRealTimeDataInfos = DCLDataQuery.getDCLRealTimeDataInfos();
//			treeMenuInfoMap.put("dclRealTimeDataInfos", dclRealTimeDataInfos);
//		}
//		//1-6.获取贴源层实时数据组下数据信息(kafkaTopic)
//		if (StringUtil.isNotBlank(treeDataInfo.getGroupId())) {
//			List<Map<String, Object>> dclRealTimeTopicInfos =
//					DCLDataQuery.getDCLRealTimeTopicInfos(treeDataInfo.getGroupId());
//			treeMenuInfoMap.put("dclRealTimeTopicInfos", dclRealTimeTopicInfos);
//		}
//		//1-7.获取贴源层实时数据组下流数据消费到平台内部的表的信息
//		if (StringUtil.isNotBlank(treeDataInfo.getSdm_consumer_id())) {
//			List<Map<String, Object>> dclRealTimeInnerTableInfos = DCLDataQuery
//					.getDCLRealTimeInnerTableInfos(treeDataInfo.getSdm_consumer_id());
//			treeMenuInfoMap.put("dclRealTimeInnerTableInfos", dclRealTimeInnerTableInfos);
//		}
//		//2.DML 集市层
//		//2-1.获取集市层下集市信息
		if (DataSourceType.DML.getValue().equals(treeDataInfo.getAgent_layer())) {
			List<Map<String, Object>> dmlDataInfos = DMLDataQuery.getDMLDataInfos(null,user);
			treeMenuInfoMap.put("dmlDataInfos", dmlDataInfos);
		}
//		//2-2.获取集市层集市下表数据信息
		if (StringUtil.isNotBlank(treeDataInfo.getData_mart_id())) {
			List<Map<String, Object>> dmlTableInfos = DMLDataQuery.getDMLTableInfos(treeDataInfo.getData_mart_id(),user,null);
			treeMenuInfoMap.put("dmlTableInfos", dmlTableInfos);
		}
		//TODO 加工功能添加后放开
//		//3.DPL 加工层
//		//3-1.获取加工层下工程信息
//		if (DataSourceType.DPL.getValue().equals(treeDataInfo.getAgent_layer())) {
//			List<Map<String, Object>> dplDataInfos = DPLDataQuery.getDPLDataInfos(user);
//			treeMenuInfoMap.put("dplDataInfos", dplDataInfos);
//		}
//		//3-2.获取加工层工程下分类信息
//		if (StringUtil.isNotBlank(treeDataInfo.getModal_pro_id())) {
//			List<Map<String, Object>> dplClassifyInfos =
//					DPLDataQuery.getDPLClassifyInfos(treeDataInfo.getModal_pro_id());
//			treeMenuInfoMap.put("dplClassifyInfos", dplClassifyInfos);
//		}
//		//3-3.获取加工层工程分类下子分类和表数信息
//		if (StringUtil.isNotBlank(treeDataInfo.getCategory_id())) {
//			//获取分类下子分类信息
//			Result dplSubClassifyInfos = DPLDataQuery.getDPLTableInfos(treeDataInfo.getCategory_id(),
//					treeDataInfo.getPage_from()).get("edw_modal_category");
//			//获取分类下表信息
//			Result dplTableInfos = DPLDataQuery.getDPLTableInfos(treeDataInfo.getCategory_id(),
//					treeDataInfo.getPage_from()).get("edw_table");
//			treeMenuInfoMap.put("dplSubClassifyInfos", dplSubClassifyInfos);
//			treeMenuInfoMap.put("dplTableInfos", dplTableInfos);
//		}
		//TODO 数据管控功能添加后放开
//		//4.DQC 管控层
//		//4-1.获取数据管控层下数据信息
//		if (DataSourceType.DQC.getValue().equals(treeDataInfo.getAgent_layer())) {
//			List<Map<String, Object>> dqcDataInfos = DQCDataQuery.getDQCDataInfos();
//			treeMenuInfoMap.put("dqcDataInfos", dqcDataInfos);
//		}
		//TODO 系统层数据管理功能添加后放开
//		//5.SFL 系统层
//		//5-1.获取系统层下数据信息
//		if (DataSourceType.SFL.getValue().equals(treeDataInfo.getAgent_layer())) {
//			List<Map<String, Object>> sflDataInfos = SFLDataQuery.getSFLDataInfos();
//			treeMenuInfoMap.put("sflDataInfos", sflDataInfos);
//		}
//		//5-2.获取系统层下系统表信息
//		if (Constant.SYS_DATA_TABLE.equalsIgnoreCase(treeDataInfo.getSystemDataType())) {
//			List<String> sflTableInfos = SFLDataQuery.getSFLTableInfos();
//			treeMenuInfoMap.put("sflTableInfos", sflTableInfos);
//		}
//		//5-3.获取系统层下数据备份信息
//		if (Constant.SYS_DATA_BAK.equalsIgnoreCase(treeDataInfo.getSystemDataType())) {
//			List<Map<String, Object>> sflDataBakInfos = SFLDataQuery.getSFLDataBakInfos();
//			treeMenuInfoMap.put("sflDataBakInfos", sflDataBakInfos);
//		}
		//TODO 自定义层管理功能添加后放开
//		//6.UDL 自定义层
//		//6-1.获取自定义层下数据信息
//		if (DataSourceType.UDL.getValue().equals(treeDataInfo.getAgent_layer())) {
//			List<Map<String, Object>> udlDatabaseInfos = UDLDataQuery.getUDLDatabaseInfos();
//			treeMenuInfoMap.put("udlDatabaseInfos", udlDatabaseInfos);
//		}
//		//6-2.获取自定义层数据库下表空间信息
//		if (!StringUtil.isBlank(treeDataInfo.getParent_id()) && StringUtil.isNotBlank(treeDataInfo.getDatabaseType())) {
//			List<Map<String, Object>> udlDatabaseTableSpaceInfos = UDLDataQuery
//					.getUDLDatabaseTableSpaceInfos(treeDataInfo.getParent_id(), treeDataInfo.getDatabaseType());
//			treeMenuInfoMap.put("udlDatabaseTableSpaceInfos", udlDatabaseTableSpaceInfos);
//		}
//		//6-3.获取自定义层数据库表空间下表信息
//		if (!StringUtil.isBlank(treeDataInfo.getDatabaseType()) && StringUtil.isNotBlank(treeDataInfo.getTableSpace())) {
//			List<Map<String, Object>> udlTableSpaceTableInfos = UDLDataQuery
//					.getUDLTableSpaceTableInfos(treeDataInfo.getDatabaseType(), treeDataInfo.getTableSpace());
//			treeMenuInfoMap.put("udlTableSpaceTableInfos", udlTableSpaceTableInfos);
//		}

		//7.AML 模型层
		//TODO 注释:不保留机器学习,删除
		//8.无hadoop下的自定义层
		//TODO 无hadoop的自定义层,暂未做
		return treeMenuInfoMap;
	}
}
