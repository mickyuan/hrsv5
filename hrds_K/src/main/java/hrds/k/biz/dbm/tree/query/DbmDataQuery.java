package hrds.k.biz.dbm.tree.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.UserType;
import hrds.commons.entity.Dbm_sort_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.User;

@DocClass(desc = "数据对标源数据管理树数据查询类", author = "BY-HLL", createdate = "2020/2/16 0016 下午 05:42")
public class DbmDataQuery {

    @Method(desc = "获取标准分类的所有分类信息",
            logicStep = "1.获取标准分类的所有分类信息")
    @Return(desc = "分类信息列表", range = "无限制")
    public static Result getDbmSortInfos(User user) {
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("select * from " + Dbm_sort_info.TableName + " where ");
        if (user.getUserTypeGroup().contains(UserType.ShuJuDuiBiaoGuanLi.getCode())) {
            asmSql.addSql("create_user=?").addParam(user.getUserId().toString());
        }
        else if (user.getUserTypeGroup().contains(UserType.ShuJuDuiBiaoCaoZuo.getCode())) {
            asmSql.addSql("sort_status=?").addParam(IsFlag.Shi.getCode());
        } else {
            throw new BusinessException("登录用户没有查询对标-分类数据权限!");
        }
        return Dbo.queryResult(asmSql.sql(), asmSql.params());
    }
}
