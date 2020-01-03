package hrds.commons.zTree.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.DataSourceType;
import hrds.commons.utils.Constant;
import hrds.commons.utils.User;
import hrds.commons.zTree.bean.TreeDataInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "获取树菜单信息", author = "BY-HLL", createdate = "2019/12/23 0023 下午 03:42")
public class TreeMenuInfo {

	@Method(desc = "获取树菜单信息",
			logicStep = "1.获取Public和Customize" +
					"2.获取数据源信息" +
					"3.获取采集过来的贴源数据" +
					"4.获取集市信息" +
					"5.获取加工信息" +
					"TODO: 是否保留机器学习6.获取机器学习项目信息（模型层）" +
					"7.获取数据管控信息" +
					"8.获取系统层信息" +
					"9.获取系统表信息" +
					"TODO: 10.暂未做 无hadoop下的自定义层" +
					"11.根据数据源id获取数据源下的全部分类信息" +
					"12.根据分类id获取分类信息" +
					"13.根据集市id获取集市下数据表信息" +
					"14.如果模型id不为空的话，获取到模型下的分类信息" +
					"15.如果category_id 不为空的话，获取到模型分类下的表信息" +
					"16.如果kafka_id不为空,获取流数据管理的groupId信息" +
					"17.如果group_id不为空,获取kafka_topic信息" +
					"18.如果消费id不为空,获取流数据消费到海云内部的表的信息")
	@Param(name = "user", desc = "User", range = "登录用户User的对象实例")
	@Param(name = "treeDataInfo", desc = "TreeDataInfo", range = "TreeDataInfo的对象实例")
	@Return(desc = "树菜单信息List", range = "无限制")
	public static Map<String, Object> getTreeMenuInfo(User user, TreeDataInfo treeDataInfo) {
		Map<String, Object> treeMenuInfoMap = new HashMap<>();
		//1.获取Public和 Customize
//		if ("true".equalsIgnoreCase(treeDataInfo.getIsPublic())) {
//			PublicAndCustomize.getPublicAndCustomize(treeMenuInfoMap, treeDataInfo);
//		}
		//2.获取贴源层数据源信息
		//see DataConvertedToTreeNode.ConversionDCLDataInfos()
		//3.获取采集过来的批量贴源数据
		if (StringUtil.isNotBlank(treeDataInfo.getBatch_id())) {
			List<Map<String, Object>> dclBatchDataInfos = TreeDataQuery.getDCLBatchDataInfos(user);
			treeMenuInfoMap.put("dclBatchDataInfos", dclBatchDataInfos);
		}
		//4.获取集市信息
		if (DataSourceType.DML.getValue().equals(treeDataInfo.getAgent_layer())) {
			List<Map<String, Object>> dmlDataInfos = TreeDataQuery.getDMLDataInfos();
			treeMenuInfoMap.put("dmlDataInfos", dmlDataInfos);
		}
		//5.获取加工信息
		if (DataSourceType.DPL.getValue().equals(treeDataInfo.getAgent_layer())) {
			List<Map<String, Object>> dplDataInfos = TreeDataQuery.getDPLDataInfos(user);
			treeMenuInfoMap.put("dplDataInfos", dplDataInfos);
		}
		//6.获取机器学习项目信息（模型层）
		//TODO 注释:不保留机器学习,随后删除
		//7.获取数据管控信息
		if (DataSourceType.DQC.getValue().equals(treeDataInfo.getAgent_layer())) {
			List<Map<String, Object>> dqcDataInfos = TreeDataQuery.getDQCDataInfos();
			treeMenuInfoMap.put("dqcDataInfos", dqcDataInfos);
		}
		//8.获取系统层信息
		if (DataSourceType.SFL.getValue().equals(treeDataInfo.getAgent_layer())) {
			List<Map<String, Object>> sflDataInfos = TreeDataQuery.getSFLDataInfos();
			treeMenuInfoMap.put("sflDataInfos", sflDataInfos);
		}
		//9.获取系统表信息
		if (Constant.SYS_DATA_TABLE.equalsIgnoreCase(treeDataInfo.getSystemDataType())) {
			List<String> sflTableInfos = TreeDataQuery.getSFLTableInfos();
			treeMenuInfoMap.put("sflTableInfos", sflTableInfos);
		}
		//9.获取系统备份表信息
		if (Constant.SYS_DATA_BAK.equalsIgnoreCase(treeDataInfo.getSystemDataType())) {
			List<Map<String, Object>> sflDataBakInfos = TreeDataQuery.getSFLDataBakInfos();
			treeMenuInfoMap.put("sflDataBakInfos", sflDataBakInfos);
		}
		//10.无hadoop下的自定义层
		//TODO 无hadoop的自定义层

		//11.根据数据源id获取数据源下的全部分类信息
		boolean isFileCo = Boolean.parseBoolean(treeDataInfo.getIsFileCo());
		if (!StringUtil.isBlank(treeDataInfo.getSource_id())) {
			List<Map<String, Object>> dclBatchClassifyInfos =
					TreeDataQuery.getDCLBatchClassifyInfos(treeDataInfo.getSource_id(),
							isFileCo, user);
			treeMenuInfoMap.put("dclBatchClassifyInfos", dclBatchClassifyInfos);
		}
		//12.根据分类id获取分类下数据信息
		if (StringUtil.isNotBlank(treeDataInfo.getClassify_id())) {
			List<Map<String, Object>> dclBatchTableInfos =
					TreeDataQuery.getDCLBatchTableInfos(treeDataInfo.getClassify_id(), null, user,
							treeDataInfo.getIsIntoHBase());
			treeMenuInfoMap.put("dclBatchTableInfos", dclBatchTableInfos);
		}
		//13.根据集市id获取集市下数据表信息
//		if (StringUtil.isNotBlank(treeDataInfo.getData_mart_id())) {
//			List<Map<String, Object>> dmlTableInfos = TreeDataQuery
//					.getDMLTableInfos(treeDataInfo.getData_mart_id(), treeDataInfo.getPage_from());
//			treeMenuInfoMap.put("dmlTableInfos", dmlTableInfos);
//		}
		//14.如果模型id不为空的话，获取到模型下的分类信息
		if (StringUtil.isNotBlank(treeDataInfo.getModal_pro_id())) {
			List<Map<String, Object>> dplClassifyInfos = TreeDataQuery
					.getDPLClassifyInfos(treeDataInfo.getModal_pro_id());
			treeMenuInfoMap.put("dplClassifyInfos", dplClassifyInfos);
		}
		//15.如果category_id 不为空的话，获取到模型分类下子分类信息和表信息
//		if (StringUtil.isNotBlank(treeDataInfo.getCategory_id())) {
//			//获取分类下子分类信息
//			Result dplSubClassifyInfos = TreeDataQuery.getDPLTableInfos(treeDataInfo.getCategory_id(),
//					treeDataInfo.getPage_from()).get("edw_modal_category");
//			//获取分类下表信息
//			Result dplTableInfos = TreeDataQuery.getDPLTableInfos(treeDataInfo.getCategory_id(),
//					treeDataInfo.getPage_from()).get("edw_table");
//			treeMenuInfoMap.put("dplSubClassifyInfos", dplSubClassifyInfos);
//			treeMenuInfoMap.put("dplTableInfos", dplTableInfos);
//		}
		//16.如果kafka_id不为空,获取流数据管理的groupId信息
		if (StringUtil.isNotBlank(treeDataInfo.getKafka_id())) {
			List<Map<String, Object>> dclRealTimeDataInfos = TreeDataQuery.getDCLRealTimeDataInfos();
			treeMenuInfoMap.put("dclRealTimeDataInfos", dclRealTimeDataInfos);
		}
		//17.如果group_id不为空,获取kafka_topic信息
		if (StringUtil.isNotBlank(treeDataInfo.getGroupid())) {
			List<Map<String, Object>> dclRealTimeTopicInfos =
					TreeDataQuery.getDCLRealTimeTopicInfos(treeDataInfo.getGroupid());
			treeMenuInfoMap.put("dclRealTimeTopicInfos", dclRealTimeTopicInfos);
		}
		//18.如果消费id不为空,获取流数据消费到海云内部的表的信息
		if (StringUtil.isNotBlank(treeDataInfo.getSdm_consum_id())) {
			List<Map<String, Object>> dclRealTimeInnerTableInfos = TreeDataQuery
					.getDCLRealTimeInnerTableInfos(treeDataInfo.getSdm_consum_id());
			treeMenuInfoMap.put("dclRealTimeInnerTableInfos", dclRealTimeInnerTableInfos);
		}
		return treeMenuInfoMap;
	}
}
