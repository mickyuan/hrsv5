package hrds.commons.tree.background.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.UserType;
import hrds.commons.entity.Dm_datatable;
import hrds.commons.entity.Dm_info;
import hrds.commons.entity.Sys_user;
import hrds.commons.utils.User;

import java.util.List;
import java.util.Map;

@DocClass(desc = "贴源层(DCL)层数据信息查询类", author = "BY-HLL", createdate = "2020/1/7 0007 上午 11:10")
public class DMLDataQuery {

    @Method(desc = "获取批量数据的数据源列表",
            logicStep = "1.获取批量数据的数据源列表")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Return(desc = "数据源列表", range = "无限制")
    public static List<Map<String, Object>> getDMLDataInfos(User User) {
        //1.获取登录用户的数据源列表
        return getDMLDataInfos(User, null);
    }

    @Method(desc = "获取批量数据的数据源列表,根据数据源名称模糊查询",
            logicStep = "1.如果数据源名称不为空,模糊查询获取数据源信息" +
                    "2.如果是系统管理员,则不过滤部门" +
                    "3.获取查询结果集")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Param(name = "dataSourceName", desc = "查询数据源的名称", range = "String类型字符,512长度", nullable = true)
    @Return(desc = "数据源列表", range = "无限制")
    //TODO 要修改
    public static List<Map<String, Object>> getDMLDataInfos(User user, String mart_name) {
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT t1.* from "+ Dm_info.TableName+" t1 left join "+Sys_user.TableName+
                " t2 on t1.create_id = t2.user_id");
        //1.如果数据源名称不为空,模糊查询获取数据源信息
        if (StringUtil.isNotBlank(mart_name)) {
            asmSql.addSql(" where mart_name like ? ");
            asmSql.addParam('%' + mart_name + '%');
        }
        //2.如果不是系统管理员,则过滤部门
        UserType userType = UserType.ofEnumByCode(user.getUserType());
        if (StringUtil.isNotBlank(mart_name) && UserType.XiTongGuanLiYuan != userType) {
            asmSql.addSql(" and ");
            asmSql.addSql(" t2.dep_id = ? ");
            asmSql.addParam(user.getDepId());
        }else if(UserType.XiTongGuanLiYuan != userType){
            asmSql.addSql(" where ");
            asmSql.addSql(" t2.dep_id = ?");
            asmSql.addParam(user.getDepId());
        }

        //3.获取查询结果集
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "获取批量数据下数据源下分类信息",
            logicStep = "1.获取批量数据下数据源下分类信息,如果是系统管理员,则不过滤部门")
    @Param(name = "data_mart_id", desc = "数据表id", range = "数据表id,唯一")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Return(desc = "加工信息列表", range = "无限制")
    public static List<Map<String, Object>> getDMLTableInfos(String data_mart_id , User user) {
        return getDMLTableInfos(data_mart_id, user, null);
    }

    @Method(desc = "获取集市工程下的表信息",
            logicStep = "1.获取批量数据下数据源下分类信息,如果是系统管理员,则不过滤部门")
    @Param(name = "source_id", desc = "数据表id", range = "数据表id,唯一")
    @Param(name = "user", desc = "User", range = "登录用户User的对象实例")
    @Return(desc = "加工信息列表", range = "无限制")
    public static List<Map<String, Object>> getDMLTableInfos(String data_mart_id ,
                                                                     User user, String searchName) {
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT t1.* from "+ Dm_datatable.TableName+" t1 left join "+Dm_info.TableName+
                " t2 on t1.data_mart_id = t2.data_mart_id left join "+Sys_user.TableName+
                " t3 on t2.create_id = t3.user_id");
        //1.如果数据源名称不为空,模糊查询获取数据源信息
        if (StringUtil.isNotBlank(searchName)) {
            asmSql.addSql(" where (t1.datatable_en_name like ? OR t1.datatable_cn_name like ? )");
            asmSql.addParam('%' + searchName + '%').addParam('%' + searchName + '%');
        }
        //2.如果不是系统管理员,则过滤部门
        UserType userType = UserType.ofEnumByCode(user.getUserType());
        if (StringUtil.isNotBlank(searchName) && UserType.XiTongGuanLiYuan != userType) {
            asmSql.addSql(" and ");
            asmSql.addSql(" t3.dep_id = ? ");
            asmSql.addParam(user.getDepId());
        }else if(UserType.XiTongGuanLiYuan != userType){
            asmSql.addSql(" where ");
            asmSql.addSql(" t3.dep_id = ?");
            asmSql.addParam(user.getDepId());
        }

        //3.获取查询结果集
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }


}
