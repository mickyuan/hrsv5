package hrds.commons.tree.background.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.UserType;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.User;

import java.util.List;
import java.util.Map;

@DocClass(desc = "加工层(DML)层数据信息查询类", author = "BY-HLL", createdate = "2020/1/7 0007 上午 11:17")
public class DMLDataQuery {

    @Method(desc = "获取加工信息", logicStep = "1.如果加工名称不为空,模糊查询获取加工信息")
    @Param(name = "user", desc = "User", range = "登录用户信息")
    @Return(desc = "加工信息列表", range = "无限制")
    public static List<Map<String, Object>> getDMLDataInfos(User user) {
        //初始化查询sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT distinct t1.* from " + Dm_info.TableName + " t1 left join " + Sys_user.TableName + " t2 on t1.create_id = t2.user_id ");
        //1.获取数据源下分类信息,如果是系统管理员,则不过滤部门
        if (!UserType.XiTongGuanLiYuan.getCode().equals(user.getUserType())) {
            asmSql.addSql(" where t2.dep_id = ?");
            asmSql.addParam(user.getDepId());
        }
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "获取加工信息", logicStep = "1.如果加工名称不为空,模糊查询获取加工信息")
    @Param(name = "user", desc = "User", range = "登录用户信息")
    @Return(desc = "加工信息列表", range = "无限制")
    public static List<Map<String, Object>> getDMLCategoryInfos(long data_mart_id) {
        return Dbo.queryList("select * from " + Dm_category.TableName + " where data_mart_id=?", data_mart_id);
    }

    @Method(desc = "获取加工下表信息列表", logicStep = "获取加工下表信息列表")
    @Param(name = "category_id", desc = "加工分类id,该值唯一", range = "long类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Map<String, Object>> getDMLTableInfos(long category_id) {
        return Dbo.queryList("select * from " + Dm_datatable.TableName + " where category_id=?", category_id);
    }

    @Method(desc = "获取加工下表信息", logicStep = "获取加工下表信息")
    @Param(name = "datatable_id", desc = "加工表id,唯一", range = "String类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static Dm_datatable getDMLTableInfo(String datatable_id) {
        //设置加工表对象
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        //查询加工表信息
        return Dbo.queryOneObject(Dm_datatable.class, "select * from " + Dm_datatable.TableName + " where datatable_id" +
                "=?", dm_datatable.getDatatable_id()).orElseThrow(() -> new BusinessException("获取加工数据表信息的sql失败!"));
    }

    @Method(desc = "获取加工下表的字段信息", logicStep = "获取加工下表的字段信息")
    @Param(name = "datatable_id", desc = "加工表id,唯一", range = "String类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Map<String, Object>> getDMLTableColumns(String datatable_id) {
        //设置加工表对象
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        //查询表字段信息
        return Dbo.queryList("select datatable_field_id AS column_id,field_en_name as column_name," +
                        " field_cn_name as column_ch_name,concat(field_type,'(',field_length,')') AS column_type," +
                        " '0' AS is_primary_key FROM " + Datatable_field_info.TableName + " WHERE datatable_id= ?",
                dm_datatable.getDatatable_id());
    }
}
