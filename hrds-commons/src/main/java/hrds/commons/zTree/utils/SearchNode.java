package hrds.commons.zTree.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.utils.User;
import hrds.commons.zTree.bean.TreeDataInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "树节点数据搜索类", author = "BY-HLL", createdate = "2019/12/31 0031 下午 02:17")
public class SearchNode {

	@Method(desc = "DCL数据层搜索",
			logicStep = "DCL数据层搜索")
	@Param(name = "treeDataInfo", desc = "TreeDataInfo", range = "TreeDataInfo的对象实例")
	@Param(name = "user", desc = "User实体对象", range = "User实体对象")
	@Return(desc = "DCL树节点数据检索结果map", range = "返回值取值范围")
	public static Map<String, Object> searchDCLNodeDataMap(User user, TreeDataInfo treeDataInfo) {
		Map<String, Object> searchDCLNodeDataMap = new HashMap<>();
		//2.检索DCL批量数据下的数据源
		List<Map<String, Object>> dclBatchDataInfos = TreeDataQuery.getDCLBatchDataInfos(user);
		searchDCLNodeDataMap.put("dclBatchDataInfos", dclBatchDataInfos);
		//3.检索DCL数据源下的分类信息
		if (!dclBatchDataInfos.isEmpty()) {
			boolean isFileCo = Boolean.parseBoolean(treeDataInfo.getIsFileCo());
			List<Map<String, Object>> dclBatchClassifyInfos = TreeDataQuery
					.getDCLBatchClassifyInfos(null, isFileCo, user, treeDataInfo.getTableName());
			searchDCLNodeDataMap.put("dclBatchClassifyInfos", dclBatchClassifyInfos);
		}
		//4.检索DCL分类下数据表信息
		List<Map<String, Object>> dclBatchTableInfos = TreeDataQuery.getDCLBatchTableInfos(null,
				treeDataInfo.getTableName(), user, treeDataInfo.getIsIntoHBase());
		if (!dclBatchTableInfos.isEmpty()) {
			searchDCLNodeDataMap.put("dclBatchTableInfos", dclBatchTableInfos);
		}
		//5.获取DCL下实时数据信息
		//TODO 未做
		return searchDCLNodeDataMap;
	}
}
