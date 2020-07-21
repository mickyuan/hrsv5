package hrds.k.biz.tdb;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import hrds.commons.base.BaseAction;
import hrds.commons.tree.TreeData;
import hrds.commons.tree.commons.TreePageSource;
import hrds.commons.utils.tree.Node;

import java.util.List;

@DocClass(desc = "表数据对标(TableDataBenchmarking)", author = "BY-HLL", createdate = "2020/7/20 0020 上午 09:40")
public class TDBAction extends BaseAction {


    @Method(desc = "获取表数据对标树", logicStep = "获取表数据对标树")
    @Return(desc = "树信息", range = "树信息")
    public List<Node> getTDBTreeData() {
        return TreeData.initTreeData(TreePageSource.DATA_BENCHMARKING, getUser());
    }

}
