package hrds.g.biz.datarangemanage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.JsonUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.tree.background.TreeNodeInfo;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.commons.TreePageSource;
import hrds.commons.utils.tree.Node;
import hrds.commons.utils.tree.NodeDataConvertedTreeList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "", author = "dhw", createdate = "2020/3/25 17:53")
public class DataRangeManageAction extends BaseAction {

	@Method(desc = "查询数据使用范围信息", logicStep = "1.数据可访问权限处理方式：该方法不需要进行访问权限限制" +
			"2.配置树不显示文件采集的数据" +
			"3.根据源菜单信息获取节点数据列表" +
			"4.转换节点数据列表为分叉树列表" +
			"5.定义返回的分叉树结果Map")
	@Return(desc = "返回的分叉树结果Map", range = "无限制")
	public Map<String, Object> searchDataUsageRangeInfo() {
		// 1.数据可访问权限处理方式：该方法不需要进行访问权限限制
		TreeConf treeConf = new TreeConf();
		// 2.配置树不显示文件采集的数据
		treeConf.setShowFileCollection(Boolean.FALSE);
		// 3.根据源菜单信息获取节点数据列表
		List<Map<String, Object>> dataList = TreeNodeInfo.getTreeNodeInfo(TreePageSource.INTERFACE, getUser(),
				treeConf);
		// 4.转换节点数据列表为分叉树列表
		List<Node> interfaceTreeList = NodeDataConvertedTreeList.dataConversionTreeInfo(dataList);
		// 5.定义返回的分叉树结果Map
		Map<String, Object> interfaceTreeDataMap = new HashMap<>();
		interfaceTreeDataMap.put("interfaceTreeList", JsonUtil.toObjectSafety(interfaceTreeList.toString(),
				List.class));
		return interfaceTreeDataMap;
	}
}
