package hrds.commons.FTree.commons;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.DataSourceType;
import hrds.commons.utils.CommonVariables;
import hrds.commons.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "自定义(UDL)层数据信息查询类", author = "BY-HLL", createdate = "2020/1/7 0007 上午 11:10")
public class UDLDataQuery {

    private static final SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();

    @Method(desc = "获取自定义层(UDL)数据库信息",
            logicStep = "1.判断是否有hadoop环境,有则添加hadoop数据存储信息,没有则添加海云库信息")
    @Return(desc = "数据库类型信息的list", range = "无限制")
    public static List<Map<String, Object>> getUDLDatabaseInfos() {
        List<Map<String, Object>> udlDataInfos = new ArrayList<>();
        if (CommonVariables.HAS_HADOOP_ENV) {
            for (String dataBaseType : Constant.DATABASE_TYPE) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", dataBaseType);
                map.put("name", dataBaseType);
                map.put("description", dataBaseType);
                map.put("rootName", DataSourceType.UDL.getValue());
                map.put("pId", DataSourceType.UDL.getCode());
                map.put("isParent", true);
                map.put("parent_id", DataSourceType.UDL.getValue());
                udlDataInfos.add(map);
            }
        } else {
            Map<String, Object> map = new HashMap<>();
            String type = "海云库";
            map.put("id", "hyshf_" + DataSourceType.UDL.getValue());
            map.put("name", type);
            map.put("description", type);
            map.put("rootName", DataSourceType.UDL.getValue());
            map.put("pId", DataSourceType.UDL.getCode());
            map.put("isParent", true);
            map.put("parent_id", DataSourceType.UDL.getValue());
            udlDataInfos.add(map);
        }
        return udlDataInfos;
    }

    @Method(desc = "获取本集群自定义层(UDL)数据库下表空间信息信息",
            logicStep = "1.hive库下的表空间" +
                    "2.HBase库下的表空间" +
                    "3.mpp库下的表空间" +
                    "4.carbonData(Spark)库下的表空间")
    @Param(name = "parentId", desc = "父级菜单id", range = "UDL")
    @Param(name = "databaseType", desc = "存储库类型", range = "hive,HBase,mpp,Spark")
    @Return(desc = "表空间信息List", range = "无限制")
    public static List<Map<String, Object>> getUDLDatabaseTableSpaceInfos(String parentId, String databaseType) {
        List<Map<String, Object>> udlDataBaseTableSpaceInfos = new ArrayList<>();
        if (DataSourceType.UDL.getValue().equals(parentId)) {
            if (Constant.HIVE.equals(databaseType) || Constant.HBASE.equals(databaseType)
                    || Constant.MPP.equals(databaseType) || Constant.CARBON_DATA.equals(databaseType)) {
                if ("hive" .equalsIgnoreCase(databaseType)) {
                    udlDataBaseTableSpaceInfos = Dbo.queryList("select *,info_id as id,table_space as space from" +
                            " sys_table_info WHERE table_type='0'");
                } else if ("HBase" .equalsIgnoreCase(databaseType)) {
                    udlDataBaseTableSpaceInfos = Dbo.queryList("select *,info_id as id,table_space as space from" +
                            " sys_table_info WHERE table_type='1'");
                } else if ("mpp" .equalsIgnoreCase(databaseType)) {
                    udlDataBaseTableSpaceInfos = Dbo.queryList("select *,info_id as id,table_space as space from" +
                            " sys_table_info WHERE table_type='2'");
                } else {
                    udlDataBaseTableSpaceInfos = Dbo.queryList("select *,info_id as id,table_space as space from" +
                            " sys_table_info WHERE table_type='3'");
                }
            }
        }
        return udlDataBaseTableSpaceInfos;
    }

    @Method(desc = "获取表空间下的所有表名称",
            logicStep = "1.获取表空间下的所有表名称")
    @Param(name = "databaseType", desc = "数据库类型", range = "hive,HBase,mpp,Spark")
    @Param(name = "tableSpace", desc = "表空间id", range = "default")
    @Return(desc = "表空间下的所有表名称List", range = "无限制")
    public static List<Map<String, Object>> getUDLTableSpaceTableInfos(String databaseType, String tableSpace) {
        //1.获取表空间下的所有表名称
        return getUDLTableSpaceTableInfos(databaseType, tableSpace, null);
    }

    @Method(desc = "获取表空间下的所有表名称,检索名不为空则根据名称模糊查询",
            logicStep = "1.解析获取表空间名称" +
                    "2.获取表空间下的表名称,如果表空间id为空,则根据表名检索")
    @Param(name = "databaseType", desc = "数据库类型", range = "hive,HBase,mpp,Spark")
    @Param(name = "tableSpace", desc = "表空间", range = "default")
    @Param(name = "searchName", desc = "检索名", range = "")
    @Return(desc = "表空间下的所有表名称List", range = "无限制")
    public static List<Map<String, Object>> getUDLTableSpaceTableInfos(String databaseType, String tableSpace,
                                                                       String searchName) {
        //1.解析获取表空间名称
        String space = StringUtil.split(tableSpace, "_").get(1);
        //2.获取表空间下的表名称,如果表空间id为空,则根据表名检索
        asmSql.clean();
        asmSql.addSql("select *,ch_name as table_ch_name from sys_table_info WHERE table_type = ?");
        if (Constant.HIVE.equals(databaseType)) {
            asmSql.addParam("'0'");
        } else if (Constant.HBASE.equals(databaseType)) {
            asmSql.addParam("'1'");
        } else if (Constant.MPP.equals(databaseType)) {
            asmSql.addParam("'2'");
        } else {
            asmSql.addParam("'3'");
        }
        if (StringUtil.isBlank(tableSpace)) {
            asmSql.addSql(" AND ( table_name LIKE ? OR ch_name LIKE ? )").addParam('%' + searchName + '%')
                    .addParam('%' + searchName + '%');
        } else {
            asmSql.addSql("AND table_space = ?").addParam(space);
        }
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }
}
