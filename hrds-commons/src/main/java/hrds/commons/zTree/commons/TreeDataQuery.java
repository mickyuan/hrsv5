package hrds.commons.zTree.commons;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.zTree.bean.TreePageSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "树数据查询类", author = "BY-HLL", createdate = "2019/12/24 0024 上午 10:26")
public class TreeDataQuery {

	@Method(desc = "根据web类型获取源树菜单",
			logicStep = "根据web类型获取源树菜单")
	@Param(name = "treeSource", desc = "树来源", range = "树来源页面TreePageSource.TREESOURCE")
	@Return(desc = "源树菜单数据的List", range = "无限制")
	public static List<Map<String, Object>> getTreeMenuInfos(String treeSource) {
		List<Map<String, Object>> sourceTreeMenuInfos = new ArrayList<>();
		String[] layerName = TreePageSource.TREE_SOURCE.get(treeSource);
		for (int i = 0; i < layerName.length; i++) {
			Map<String, Object> map = new HashMap<>();
			map.put("name", layerName[i]);
			map.put("agent_layer", layerName[i]);
			map.put("isParent", true);
			map.put("rootName", layerName[i]);
			map.put("id", layerName[i]);
			map.put("pId", "~" + i);
			map.put("description", layerName[i]);
			sourceTreeMenuInfos.add(map);
		}
		return sourceTreeMenuInfos;
	}
}
