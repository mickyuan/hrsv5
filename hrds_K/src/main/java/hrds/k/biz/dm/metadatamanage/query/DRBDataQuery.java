package hrds.k.biz.dm.metadatamanage.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.web.util.Dbo;
import hrds.commons.entity.Data_store_layer;
import hrds.commons.entity.Dq_failure_table;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.Constant;

import java.util.List;
import java.util.Map;

@DocClass(desc = "数据管控-数据回收站数据查询类", author = "BY-HLL", createdate = "2020/1/7 0007 上午 11:10")
public class DRBDataQuery {

    @Method(desc = "数据管控-源数据列表获取数据存储层信息", logicStep = "数据管控-源数据列表获取数据存储层信息")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Data_store_layer> getDCLExistTableDataStorageLayers() {

        //获取数据存储层信息列表
        return Dbo.queryList(Data_store_layer.class, "SELECT dsl.* FROM dq_failure_table dft" +
                " JOIN data_store_reg dsr ON dsr.file_id = dft.file_id" +
                " JOIN table_info ti ON ti.table_id = dsr.table_id" +
                " JOIN table_storage_info tsi ON tsi.table_id = ti.table_id" +
                " JOIN data_relation_table drt ON drt.storage_id = tsi.storage_id" +
                " JOIN data_store_layer dsl ON dsl.dsl_id = drt.dsl_id" +
                " GROUP BY dsl.dsl_id");
    }

    @Method(desc = "数据管控-数据回收站获取所有表信息",
            logicStep = "数据管控-数据回收站获取所有表信息")
    @Return(desc = "回收站所有表信息", range = "回收站所有表信息")
    public static List<Dq_failure_table> getAllTableInfos() {
        return Dbo.queryList(Dq_failure_table.class, "SELECT * FROM " + Dq_failure_table.TableName);
    }

    @Method(desc = "数据管控-数据回收站获取 DCL 层下表信息",
            logicStep = "1.获取 DCL 数据存储层下表信息")
    @Param(name = "dataSourceType", desc = "DataSourceType数据源类型", range = "DataSourceType数据源类型")
    @Return(desc = "数据源列表", range = "无限制")
    public static List<Map<String, Object>> getDCLTableInfos() {
        return Dbo.queryList("SELECT * FROM " + Dq_failure_table.TableName + " WHERE table_source in ( ?, ?)",
                Constant.DCL_BATCH, Constant.DCL_REALTIME);
    }

    @Method(desc = "数据管控-数据回收站根据表的id获取表信息",
            logicStep = "数据管控-数据回收站根据表的id获取表信息")
    @Param(name = "failure_table_id", desc = "回收站的表id", range = "long类型,该值唯一")
    @Return(desc = "表的元信息", range = "返回值取值范围")
    public static Dq_failure_table getDRBTableInfo(long failure_table_id) {
        //设置回收站表信息
        Dq_failure_table dft = new Dq_failure_table();
        dft.setFailure_table_id(failure_table_id);
        return Dbo.queryOneObject(Dq_failure_table.class, "SELECT * FROM " + Dq_failure_table.TableName +
                " WHERE failure_table_id = ?", dft.getFailure_table_id()).orElseThrow(()
                -> (new BusinessException("根据id获取回收站表信息的SQL失败!")));
    }

    @Method(desc = "根据回收站表id删除回收站表信息", logicStep = "根据回收站表id删除回收站表信息")
    @Param(name = "file_id", desc = "表登记id", range = "Long类型,唯一")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void deleteDqFailureTableInfo(long failure_table_id) {
        Dq_failure_table dft = new Dq_failure_table();
        dft.setFailure_table_id(failure_table_id);
        int execute = Dbo.execute("DELETE FROM data_store_reg WHERE failure_table_id=?", dft.getFailure_table_id());
        if (execute != 1) {
            throw new BusinessException("删除回收站表登记信息失败! failure_table_id=" + failure_table_id);
        }
    }
}
