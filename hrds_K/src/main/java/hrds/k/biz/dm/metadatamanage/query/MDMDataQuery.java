package hrds.k.biz.dm.metadatamanage.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.JobExecuteState;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;

import java.util.List;
import java.util.Map;

@DocClass(desc = "数据管控-源数据列表树数据查询类", author = "BY-HLL", createdate = "2020/4/1 0001 下午 02:11")
public class MDMDataQuery {

    @Method(desc = "数据管控-源数据列表获取DCL数据存储层信息", logicStep = "数据管控-源数据列表获取数据存储层信息")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Data_store_layer> getDCLExistTableDataStorageLayers() {
        //获取数据存储层信息列表
        return Dbo.queryList(Data_store_layer.class, "SELECT dsl.* FROM table_info ti" +
                " JOIN table_storage_info dsi ON dsi.table_id = ti.table_id" +
                " JOIN data_relation_table drt ON drt.storage_id = dsi.storage_id" +
                " JOIN data_store_layer dsl ON dsl.dsl_id = drt.dsl_id" +
                " GROUP BY dsl.dsl_id");
    }

    @Method(desc = "数据管控-源数据列表获取DCL数据存储层下的表信息",
            logicStep = "数据管控-源数据列表获取数据存储层下的表信息")
    @Param(name = "data_store_layer", desc = "Data_store_layer实体对象", range = "Data_store_layer实体对象")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Map<String, Object>> getDCLStorageLayerTableInfos(Data_store_layer data_store_layer) {
        return Dbo.queryList("SELECT dsl.*,dsr.* FROM data_store_layer dsl" +
                " JOIN data_relation_table drt ON dsl.dsl_id = drt.dsl_id" +
                " JOIN table_storage_info tsi ON drt.storage_id = tsi.storage_id" +
                " JOIN table_info ti ON ti.table_id = tsi.table_id" +
                " JOIN data_store_reg dsr ON dsr.table_id = ti.table_id" +
                " WHERE dsl.dsl_id = ?", data_store_layer.getDsl_id());
    }

    @Method(desc = "数据管控-源数据列表获取DML数据存储层信息", logicStep = "数据管控-源数据列表获取数据存储层信息")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Data_store_layer> getDMLExistTableDataStorageLayers() {
        //获取数据存储层信息列表
        return Dbo.queryList(Data_store_layer.class, "SELECT dsl.* FROM " + Data_store_layer.TableName + " dsl" +
                " JOIN " + Dm_relation_datatable.TableName + " drd ON drd.dsl_id = dsl.dsl_id" +
                " JOIN " + Dm_datatable.TableName + " dd ON dd.datatable_id = drd.datatable_id" +
                " GROUP BY dsl.dsl_id");
    }

    @Method(desc = "数据管控-源数据列表获取DML数据存储层下的表信息",
            logicStep = "数据管控-源数据列表获取数据存储层下的表信息")
    @Param(name = "data_store_layer", desc = "Data_store_layer实体对象", range = "Data_store_layer实体对象")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Map<String, Object>> getDMLStorageLayerTableInfos(Data_store_layer data_store_layer) {
        return Dbo.queryList("SELECT dsl.*,dd.* FROM " + Dm_datatable.TableName + " dd" +
                        " JOIN " + Dm_relation_datatable.TableName + " drd ON drd.datatable_id = dd.datatable_id" +
                        " JOIN " + Data_store_layer.TableName + " dsl ON dsl.dsl_id = drd.dsl_id" +
                        " WHERE dsl.dsl_id = ? and is_successful = ?",
                data_store_layer.getDsl_id(), JobExecuteState.WanCheng.getCode());
    }

    @Method(desc = "根据表id获取DCL层数据表登记信息", logicStep = "根据表id获取DCL层数据表登记信息")
    @Param(name = "file_id", desc = "表登记id", range = "String字符串,唯一")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static Data_store_reg getDataStoreRegInfo(String file_id) {
        Data_store_reg dsr = new Data_store_reg();
        dsr.setFile_id(file_id);
        return Dbo.queryOneObject(Data_store_reg.class, "SELECT * from data_store_reg WHERE file_id =?",
                dsr.getFile_id()).orElseThrow(() -> (new BusinessException("获取数据登记信息的SQL失败!")));
    }

    @Method(desc = "根据表id删除DCL层数据表登记信息", logicStep = "根据表id删除DCL层数据表登记信息")
    @Param(name = "file_id", desc = "表登记id", range = "String字符串,唯一")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void deleteDataStoreRegInfo(String file_id) {
        Data_store_reg dsr = new Data_store_reg();
        dsr.setFile_id(file_id);
        int execute = Dbo.execute("DELETE FROM data_store_reg WHERE file_id =?", dsr.getFile_id());
        if (execute != 1) {
            throw new BusinessException("删除表登记信息失败! file_id=" + file_id);
        }
    }

    @Method(desc = "获取表存在的存储层信息", logicStep = "获取表存在的存储层信息,一张表有可能存在多个存储层下")
    @Param(name = "dsr", desc = "Data_store_reg实体对象", range = "Data_store_reg实体对象")
    @Return(desc = "表对应的存储层信息", range = "表对应的存储层信息")
    public static List<Data_store_layer> getTableStorageLayers(Data_store_reg dsr) {
        return Dbo.queryList(Data_store_layer.class, "SELECT dsl.* FROM data_store_layer dsl" +
                " JOIN data_relation_table drt ON dsl.dsl_id = drt.dsl_id" +
                " JOIN table_storage_info tsi ON drt.storage_id = tsi.storage_id" +
                " JOIN table_info ti ON ti.table_id = tsi.table_id" +
                " JOIN data_store_reg dsr ON dsr.table_id = ti.table_id" +
                " WHERE dsr.file_id = ?", dsr.getFile_id());
    }

    @Method(desc = "获取存储层链接配置信息", logicStep = "获取存储层链接配置信息")
    @Param(name = "data_store_layer", desc = "Data_store_layer实体对象", range = "Data_store_layer实体对象")
    @Return(desc = "存储层链接配置信息", range = "存储层链接配置信息")
    public static List<Map<String, Object>> getDataStoreLayerAttrList(Data_store_layer data_store_layer) {
        return Dbo.queryList("select storage_property_key,storage_property_val from "
                + Data_store_layer_attr.TableName + " where dsl_id = ?", data_store_layer.getDsl_id());
    }

    @Method(desc = "数据库表重命名", logicStep = "数据库表重命名")
    @Param(name = "attrList", desc = "存储层访问链接配置信息", range = "List<Map<String, Object>>类型")
    @Param(name = "tableSpace", desc = "表空间", range = "String类型")
    @Param(name = "oldTableName", desc = "旧表名", range = "String类型")
    @Param(name = "newTableName", desc = "新表名", range = "String类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void dataBaseTableRename(List<Map<String, Object>> attrList, String tableSpace, String oldTableName,
                                           String newTableName) {
        //根据配置信息创建连接
        try (DatabaseWrapper db = ConnectionTool.getDBWrapper(attrList)) {
            //如果表空间不为空,需要先进入表
            //TODO 如果添加了表空间,不同关系型数据库设置表空间语法不同,此处需要修改
            if (StringUtil.isNotBlank(tableSpace)) {
                Dbo.execute(db, "use " + tableSpace);
            }
            //修改表名
            int execute = Dbo.execute(db, "alter table " + oldTableName + " rename to " + newTableName);
            if (execute != 0) {
                throw new BusinessException("修改关系型数据库表失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("根据存储层配置的链接信息创建关系型数据库链接失败!");
        }
    }

    @Method(desc = "数据库表删除", logicStep = "数据库表删除")
    @Param(name = "attrList", desc = "存储层访问链接配置信息", range = "List<Map<String, Object>>类型")
    @Param(name = "tableSpace", desc = "表空间", range = "String类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void dataBaseTableRemove(List<Map<String, Object>> attrList, String tableSpace, String tableName) {
        //根据配置信息创建连接
        try (DatabaseWrapper db = ConnectionTool.getDBWrapper(attrList)) {
            //如果表空间不为空,需要先进入表
            //TODO 如果添加了表空间,不同关系型数据库设置表空间语法不同,此处需要修改
            if (StringUtil.isNotBlank(tableSpace)) {
                Dbo.execute(db, "use " + tableSpace);
            }
            //删除表
            int execute = Dbo.execute(db, "DROP TABLE " + tableName);
            if (execute != 0) {
                throw new BusinessException("修改关系型数据库表失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("根据存储层配置的链接信息创建关系型数据库链接失败!");
        }
    }
}
