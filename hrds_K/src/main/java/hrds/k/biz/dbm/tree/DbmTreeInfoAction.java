package hrds.k.biz.dbm.tree;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.resultset.Result;
import hrds.commons.base.BaseAction;
import hrds.commons.utils.tree.Node;
import hrds.commons.utils.tree.NodeDataConvertedTreeList;
import hrds.k.biz.dbm.tree.query.DbmDataQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "获取数据对标源数据管理树菜单类", author = "BY-HLL", createdate = "2020/2/16 0016 下午 05:33")
public class DbmTreeInfoAction extends BaseAction {

    @Method(desc = "获取数据对标-标准分类的树数据信息", logicStep = "获取数据对标-标准分类的树数据信息")
    @Return(desc = "标准分类的树数据信息", range = "标准分类的树数据信息")
    public Map<String, Object> getDbmSortInfoTreeData() {
        Map<String, Object> dbmSortInfoTreeDataMap = new HashMap<>();
        //获取数据对标-标准分类信息的所有分类信息
        Result dbmSortInfoRs = DbmDataQuery.getDbmSortInfos(getUser());
        //转换查询数据为Node数据
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 0; i < dbmSortInfoRs.getRowCount(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dbmSortInfoRs.getString(i, "sort_id"));
            map.put("label", dbmSortInfoRs.getString(i, "sort_name"));
            map.put("parent_id", dbmSortInfoRs.getString(i, "parent_id"));
            map.put("description", dbmSortInfoRs.getString(i, "sort_name"));
            dataList.add(map);
        }
        //转换Node信息数据为分叉树节点数据List
        List<Node> dbmSortInfoTreeDataList = NodeDataConvertedTreeList.dataConversionTreeInfo(dataList);
        //创建返回结果Map集合
        dbmSortInfoTreeDataMap.put("dbmSortInfoTreeDataList",
                JsonUtil.toObjectSafety(dbmSortInfoTreeDataList.toString(), List.class));
        return dbmSortInfoTreeDataMap;
    }
}
