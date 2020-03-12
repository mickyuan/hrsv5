package hrds.k.biz.dbmtree.commons;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.entity.Dbm_sort_info;
import hrds.commons.utils.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据对标源数据管理树数据查询类", author = "BY-HLL", createdate = "2020/2/16 0016 下午 05:42")
public class DbmDataQuery {

    @Method(desc = "获取标准分类的所有分类信息",
            logicStep = "1.获取标准分类的所有分类信息")
    @Return(desc = "分类信息列表", range = "无限制")
    public static Result getDbmSortInfos(User user) {
        return Dbo.queryResult("select * from " + Dbm_sort_info.TableName + " where create_user=?"
                , user.getUserId().toString());
    }
}
