package hrds.k.biz.dbm.tree;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.JsonUtil;
import hrds.commons.utils.tree.NodeDataConvertedTreeList;
import hrds.commons.utils.tree.Node;
import hrds.testbase.WebBaseTestCase;
import org.junit.Test;

import java.util.*;

@DocClass(desc = "数据对标树菜单测试", author = "BY-HLL", createdate = "2020/3/12 0012 上午 11:02")
public class DbmTreeInfoActionTest extends WebBaseTestCase {

    @Method(desc = "获取数据对标-标准分类的树数据信息测试方法",
            logicStep = "获取数据对标-标准分类的树数据信息")
    @Test
    public void getDbmSortInfoTreeData() {
        //读取层次数据结果集列表(虚拟数据)
        List<Map<String, Object>> dataList = getVirtualResult();
        //转化数据列表为分叉树
        List<Node> dbmTreeDataList = NodeDataConvertedTreeList.dataConversionTreeInfo(dataList);
        assert JsonUtil.toObjectSafety(dbmTreeDataList.toString(), List.class).orElse(null) != null;
    }

    @Method(desc = "构造虚拟的层次数据",
            logicStep = "构造虚拟的层次数据")
    @Return(desc = "虚拟的层次数据列表", range = "虚拟的层次数据列表")
    private static List<Map<String, Object>> getVirtualResult() {
        List<Map<String, Object>> dataList = new ArrayList<>();
        //第一个树数据
        Map<String, Object> dataRecord1 = new HashMap<>();
        dataRecord1.put("id", "100000");
        dataRecord1.put("label", "廊坊银行总行");
        dataRecord1.put("parent_id", "0");
        dataRecord1.put("description", "0");
        Map<String, Object> dataRecord2 = new HashMap<>();
        dataRecord2.put("id", "110000");
        dataRecord2.put("label", "廊坊分行1");
        dataRecord2.put("parent_id", "100000");
        dataRecord2.put("description", "0");
        Map<String, Object> dataRecord3 = new HashMap<>();
        dataRecord3.put("id", "111000");
        dataRecord3.put("label", "廊坊分行1金光道支行");
        dataRecord3.put("parent_id", "110000");
        dataRecord3.put("description", "0");
        Map<String, Object> dataRecord4 = new HashMap<>();
        dataRecord4.put("id", "120000");
        dataRecord4.put("label", "廊坊分行2");
        dataRecord4.put("parent_id", "100000");
        dataRecord4.put("description", "0");
        //第二个树数据
        Map<String, Object> dataRecord5 = new HashMap<>();
        dataRecord5.put("id", "200000");
        dataRecord5.put("label", "西安银行");
        dataRecord5.put("parent_id", "0");
        dataRecord5.put("description", "0");
        Map<String, Object> dataRecord6 = new HashMap<>();
        dataRecord6.put("id", "210000");
        dataRecord6.put("label", "高新分行");
        dataRecord6.put("parent_id", "200000");
        dataRecord6.put("description", "0");
        Map<String, Object> dataRecord8 = new HashMap<>();
        dataRecord8.put("id", "211000");
        dataRecord8.put("label", "宝德云谷支行");
        dataRecord8.put("parent_id", "210000");
        dataRecord8.put("description", "0");
        Map<String, Object> dataRecord7 = new HashMap<>();
        dataRecord7.put("id", "220000");
        dataRecord7.put("label", "雁塔分行");
        dataRecord7.put("parent_id", "200000");
        dataRecord7.put("description", "0");
        Map<String, Object> dataRecord9 = new HashMap<>();
        dataRecord9.put("id", "221000");
        dataRecord9.put("label", "西沣三路支行");
        dataRecord9.put("parent_id", "220000");
        dataRecord9.put("description", "0");
        //添加树数据到List
        dataList.add(dataRecord1);
        dataList.add(dataRecord2);
        dataList.add(dataRecord3);
        dataList.add(dataRecord4);
        dataList.add(dataRecord5);
        dataList.add(dataRecord6);
        dataList.add(dataRecord7);
        dataList.add(dataRecord8);
        dataList.add(dataRecord9);
        //返回树数据列表
        return dataList;
    }
}
