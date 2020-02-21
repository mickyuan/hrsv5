package hrds.k.biz.dbmtree;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.JsonUtil;
import hrds.commons.base.BaseAction;
import hrds.k.biz.dbmtree.commons.DbmDataQuery;
import hrds.k.biz.dbmtree.util.DataConvertedNode;
import hrds.k.biz.dbmtree.util.Node;

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
        List<Map<String, Object>> dbmSortInfos = DbmDataQuery.getDbmSortInfos();
        //转换所有标准分类信息数据为树节点信息数据
        List<Map<String, Object>> dbmSortInfoTreeDataList = DataConvertedNode.DataConversionTreeInfoList(dbmSortInfos);
        dbmSortInfoTreeDataMap.put("dbmSortInfoTreeDataList", dbmSortInfoTreeDataList);
        return dbmSortInfoTreeDataMap;
    }
}
