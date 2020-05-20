package hrds.commons.tree.background.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.UserType;
import hrds.commons.entity.Datatable_field_info;
import hrds.commons.entity.Dm_datatable;
import hrds.commons.entity.Dm_info;
import hrds.commons.entity.Sys_user;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.User;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

@DocClass(desc = "集市层(DML)层数据信息查询类", author = "BY-HLL", createdate = "2020/1/7 0007 上午 11:17")
public class DMLDataQuery {

    @Method(desc = "获取集市信息",
            logicStep = "1.如果集市名称不为空,模糊查询获取集市信息")
    @Param(name = "user", desc = "User", range = "登录用户信息")
    @Return(desc = "集市信息列表", range = "无限制")
    public static List<Map<String, Object>> getDMLDataInfos(User user) {
        return getDMLDataInfos(null, user);
    }

    @Method(desc = "获取集市信息",
            logicStep = "1.如果集市名称不为空,模糊查询获取集市信息")
    @Param(name = "marketName", desc = "集市名称", range = "marketName")
    @Param(name = "user", desc = "User", range = "登录用户信息")
    @Return(desc = "集市信息列表", range = "无限制")
    public static List<Map<String, Object>> getDMLDataInfos(String marketName, User user) {
        //初始化查询sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT distinct t1.* from " + Dm_info.TableName + " t1 left join " + Sys_user.TableName + " t2 on t1.create_id = t2.user_id ");
        //1.获取数据源下分类信息,如果是系统管理员,则不过滤部门
        if (!UserType.XiTongGuanLiYuan.getCode().equals(user.getUserType())) {
            asmSql.addSql(" where t2.dep_id = ?");
            asmSql.addParam(user.getDepId());
        }
        //1.如果集市名称不为空,模糊查询获取集市信息
        if (!StringUtil.isBlank(marketName)) {
            if (!UserType.XiTongGuanLiYuan.getCode().equals(user.getUserType())) {
                asmSql.addSql(" and ");
            } else {
                asmSql.addSql(" where ");
            }
            asmSql.addSql(" lower(mart_name) like ?").addParam('%' + marketName.toLowerCase() + '%');
        }
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "获取集市下表信息列表",
            logicStep = "获取集市下表信息列表")
    @Param(name = "data_mart_id", desc = "集市id,该值唯一", range = "String类型")
    @Param(name = "user", desc = "User", range = "登录用户信息")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Map<String, Object>> getDMLTableInfos(String data_mart_id, User user) {
        return getDMLTableInfos(data_mart_id, user, null);
    }

    @Method(desc = "获取集市下表信息列表",
            logicStep = "获取集市下表信息列表")
    @Param(name = "data_mart_id", desc = "集市id,该值唯一", range = "String类型")
    @Param(name = "user", desc = "User", range = "登录用户信息")
    @Param(name = "table_name", desc = "集市表名", range = "String类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Map<String, Object>> getDMLTableInfos(String data_mart_id, User user, String table_name) {
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        asmSql.addSql("SELECT distinct t1.* from " + Dm_datatable.TableName + " t1 left join " + Dm_info.TableName +
                " t2 on t1.data_mart_id = t2.data_mart_id left join " + Sys_user.TableName + " t3 on t2.create_id = t3.user_id");

        if (!UserType.XiTongGuanLiYuan.getCode().equals(user.getUserType())) {
            asmSql.addSql(" where t3.dep_id = ?");
            asmSql.addParam(user.getDepId());
        }
        if (!StringUtils.isEmpty(data_mart_id)) {
            Dm_datatable dm_datatable = new Dm_datatable();
            dm_datatable.setData_mart_id(data_mart_id);
            if (!UserType.XiTongGuanLiYuan.getCode().equals(user.getUserType())) {
                asmSql.addSql(" and ");
            } else {
                asmSql.addSql(" where ");
            }
            asmSql.addSql(" t1.data_mart_id = ?");
            asmSql.addParam(dm_datatable.getData_mart_id());
        } else if (!StringUtil.isEmpty(table_name)) {
            if (!UserType.XiTongGuanLiYuan.getCode().equals(user.getUserType())) {
                asmSql.addSql(" and ");
            } else {
                asmSql.addSql(" where ");
            }
            asmSql.addSql(" lower(t1.datatable_en_name) like ?").addParam('%' + table_name.toLowerCase() + '%');

        }
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }

    @Method(desc = "获取集市下表信息",
            logicStep = "获取集市下表信息")
    @Param(name = "datatable_id", desc = "集市表id,唯一", range = "String类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static Dm_datatable getDMLTableInfo(String datatable_id) {
        //设置集市表对象
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        //查询集市表信息
        return Dbo.queryOneObject(Dm_datatable.class, "select * from " + Dm_datatable.TableName + " where datatable_id" +
                "=?", dm_datatable.getDatatable_id()).orElseThrow(() -> new BusinessException("获取集市数据表信息的sql失败!"));
    }

    @Method(desc = "获取集市下表的字段信息",
            logicStep = "获取集市下表的字段信息")
    @Param(name = "datatable_id", desc = "集市表id,唯一", range = "String类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Map<String, Object>> getDMLTableColumns(String datatable_id) {
        //初始化sql
        SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
        asmSql.clean();
        //设置集市表对象
        Dm_datatable dm_datatable = new Dm_datatable();
        dm_datatable.setDatatable_id(datatable_id);
        //查询表字段信息
        asmSql.addSql("select datatable_field_id AS column_id,field_en_name as column_name," +
                " field_cn_name as column_ch_name,concat(field_type,'(',field_length,')') AS column_type," +
                " '0' AS is_primary_key FROM " + Datatable_field_info.TableName + " WHERE datatable_id= ?");
        asmSql.addParam(dm_datatable.getDatatable_id());
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }
}
