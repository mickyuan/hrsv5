package hrds.commons.tree;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.base.BaseAction;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.TreeNodeInfo;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.commons.TreePageSource;
import hrds.commons.utils.User;
import hrds.commons.utils.tree.Node;
import hrds.commons.utils.tree.NodeDataConvertedTreeList;

import java.util.List;
import java.util.Map;

@DocClass(desc = "初始化树数据类", author = "dhw", createdate = "2020/5/27 14:58")
public class TreeData extends BaseAction {

    @Method(desc = "初始化树数据", logicStep = "1.配置树不显示文件采集的数据" +
            "2.判断树来源是否合法" +
            "3.根据源菜单信息获取节点数据列表" +
            "4.转换节点数据列表为分叉树列表")
    @Param(name = "tree_source", desc = "树菜单来源", range = "无限制")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Return(desc = "返回初始化树数据", range = "无限制")
    public static List<Node> initTreeData(String tree_source, User user) {
        //初始化树配置文件
        TreeConf treeConf = new TreeConf();
        // 1.配置树不显示文件采集的数据
        treeConf.setShowFileCollection(Boolean.FALSE);
        // 2.判断树来源是否合法
        if (!TreePageSource.treeSourceList.contains(tree_source)) {
            throw new BusinessException("tree_source=" + tree_source + "不合法，请检查！");
        }
        // 3.根据源菜单信息获取节点数据列表
        List<Map<String, Object>> dataList = TreeNodeInfo.getTreeNodeInfo(tree_source, user, treeConf);
        return NodeDataConvertedTreeList.dataConversionTreeInfo(dataList);
    }
}

