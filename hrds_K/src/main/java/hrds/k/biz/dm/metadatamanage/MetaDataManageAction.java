package hrds.k.biz.dm.metadatamanage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.Validator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.*;
import hrds.commons.collection.CreateDataTable;
import hrds.commons.collection.DropDataTable;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.tree.background.bean.TreeConf;
import hrds.commons.tree.background.query.*;
import hrds.commons.tree.commons.TreePageSource;
import hrds.commons.utils.BeanUtils;
import hrds.commons.utils.Constant;
import hrds.commons.utils.DataTableFieldUtil;
import hrds.commons.utils.DataTableUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.commons.utils.tree.Node;
import hrds.commons.utils.tree.NodeDataConvertedTreeList;
import hrds.k.biz.dm.metadatamanage.bean.ColumnInfoBean;
import hrds.k.biz.dm.metadatamanage.bean.DqTableColumnBean;
import hrds.k.biz.dm.metadatamanage.bean.DqTableInfoBean;
import hrds.k.biz.dm.metadatamanage.commons.TableMetaInfoTool;
import hrds.k.biz.dm.metadatamanage.drbtree.DRBTreeNodeInfo;
import hrds.k.biz.dm.metadatamanage.drbtree.MDMTreeNodeInfo;
import hrds.k.biz.dm.metadatamanage.query.DRBDataQuery;
import hrds.k.biz.dm.metadatamanage.query.MDMDataQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DocClass(desc = "数据管控-元数据管理", author = "BY-HLL", createdate = "2020/3/27 0027 下午 04:39")
public class MetaDataManageAction extends BaseAction {

    private static final Logger logger = LogManager.getLogger();

    @Method(desc = "数据管控-源数据列表树", logicStep = "获取数据管控-源数据列表树")
    @Return(desc = "源数据列表树", range = "源数据列表树")
    public List<Node> getMDMTreeData() {
        //配置树不显示文件采集的数据
        TreeConf treeConf = new TreeConf();
        treeConf.setShowFileCollection(Boolean.FALSE);
        //根据源菜单信息获取节点数据列表 TreePageSource.DATA_MANAGEMENT 数据管控
        List<Map<String, Object>> dataList = MDMTreeNodeInfo.getTreeNodeInfo(TreePageSource.DATA_MANAGEMENT,
                getUser(), treeConf);
        //转换节点数据列表为分叉树列表
        return NodeDataConvertedTreeList.dataConversionTreeInfo(dataList);
    }

    @Method(desc = "数据管控-数据回收站树", logicStep = "获取数据管控-源数据列表树 DataRecycleBin")
    @Return(desc = "源数据列表树", range = "源数据列表树")
    public List<Node> getDRBTreeData() {
        //配置树不显示文件采集的数据
        TreeConf treeConf = new TreeConf();
        treeConf.setShowFileCollection(Boolean.FALSE);
        List<Map<String, Object>> dataList = DRBTreeNodeInfo.getTreeNodeInfo(TreePageSource.DATA_MANAGEMENT,
                getUser(), treeConf);
        //转换节点数据列表为分叉树列表
        return NodeDataConvertedTreeList.dataConversionTreeInfo(dataList);
    }

    @Method(desc = "数据管控-获取源数据列表下表的字段信息",
            logicStep = "逻辑说明")
    @Param(name = "data_layer", desc = "数据层", range = "String类型,DCL,DML")
    @Param(name = "file_id", desc = "表源属性id", range = "String[]")
    @Return(desc = "表字段信息Map", range = "表字段信息Map")
    public Map<String, Object> getMDMTableColumnInfo(String data_layer, String file_id) {
        //数据校验
        Validator.notBlank(data_layer, "查询数据层为空!");
        Validator.notBlank(file_id, "查询数据表id为空!");
        return DataTableUtil.getTableInfoAndColumnInfo(data_layer, file_id);
    }

    @Method(desc = "数据管控-获取数据回收站的表结构信息",
            logicStep = "数据管控-获取数据回收站的表结构信息")
    @Param(name = "failure_table_id", desc = "回收站的表id", range = "long类型,该值唯一")
    @Return(desc = "回收站的表结构信息", range = "回收站的表结构信息")
    public Map<String, Object> getDRBTableColumnInfo(long failure_table_id) {
        //初始化返回结果Map
        Map<String, Object> data_meta_info = new HashMap<>();
        //id获取回收站表实体对象
        Dq_failure_table dq_failure_table = DRBDataQuery.getDRBTableInfo(failure_table_id);
        //获取表的meta源信息
        String table_meta_info = dq_failure_table.getTable_meta_info();
        //初始化字段解析结果List
        List<Map<String, String>> column_info_list;
        //表id
        String table_id, create_date;
        //获取表的来源数据类型(DCL,DPL,实时数据暂不考虑!),并根据数据源类型处理不同数据源下的表信息
        String table_source = dq_failure_table.getTable_source();
        DataSourceType dataSourceType = DataSourceType.ofEnumByCode(table_source);
        if (dataSourceType == DataSourceType.ISL) {
            throw new BusinessException(table_source + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DCL) {
            //转化成 Data_store_reg 对象
            Data_store_reg dsr = JsonUtil.toObjectSafety(table_meta_info, Data_store_reg.class)
                    .orElseThrow(() -> new BusinessException("类型转换错误,请检查meta格式的正确性!"));
            //设置表id
            table_id = dq_failure_table.getFailure_table_id().toString();
            //设置表创建日期
            create_date = dsr.getOriginal_update_date();
            //获取字段信息列表
            List<Map<String, Object>> dclBatchTableColumns = DCLDataQuery.getDCLBatchTableColumns(dsr.getTable_id());
            //转化字段信息列表为meta_list
            column_info_list = DataTableFieldUtil.metaInfoToList(dclBatchTableColumns);
        } else if (dataSourceType == DataSourceType.DPL) {
            throw new BusinessException(table_source + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DML) {
            //转 Dm_datatable 对象
            Dm_datatable dm_datatable = JsonUtil.toObjectSafety(table_meta_info, Dm_datatable.class)
                    .orElseThrow(() -> new BusinessException("类型转换错误,请检查meta格式的正确性!"));
            //设置表id
            table_id = dq_failure_table.getFailure_table_id().toString();
            //设置表创建日期
            create_date = dm_datatable.getDatatable_create_date();
            //获取字段信息列表
            List<Map<String, Object>> dmlTableColumns =
                    DMLDataQuery.getDMLTableColumns(dm_datatable.getDatatable_id().toString());
            //转化字段信息列表为meta_list
            column_info_list = DataTableFieldUtil.metaInfoToList(dmlTableColumns);
        } else if (dataSourceType == DataSourceType.SFL) {
            throw new BusinessException(table_source + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.AML) {
            throw new BusinessException(table_source + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DQC) {
            //获取表信息
            Dq_index3record dq_index3record = JsonUtil.toObjectSafety(table_meta_info, Dq_index3record.class)
                    .orElseThrow(() -> new BusinessException("类型转换错误,请检查meta格式的正确性!"));
            //设置表id
            table_id = dq_failure_table.getFailure_table_id().toString();
            //设置表创建日期
            create_date = dq_index3record.getRecord_date();
            //获取字段信息列表
            List<Map<String, Object>> table_column_list = new ArrayList<>();
            String[] columns = dq_index3record.getTable_col().split(",");
            for (String column : columns) {
                Map<String, Object> map = new HashMap<>();
                String is_primary_key = IsFlag.Fou.getCode();
                map.put("column_id", table_id);
                map.put("column_name", column);
                map.put("column_ch_name", column);
                map.put("column_type", "varchar(--)");
                map.put("is_primary_key", is_primary_key);
                table_column_list.add(map);
            }
            //转化字段信息列表为meta_list
            column_info_list = DataTableFieldUtil.metaInfoToList(table_column_list);
        } else if (dataSourceType == DataSourceType.UDL) {
            //转 Dq_table_info
            Dq_table_info dq_table_info = JsonUtil.toObjectSafety(table_meta_info, Dq_table_info.class)
                    .orElseThrow(() -> new BusinessException("类型转换错误,请检查meta格式的正确性!"));
            //设置表id
            table_id = dq_failure_table.getFailure_table_id().toString();
            //设置表创建日期
            create_date = dq_table_info.getCreate_date();
            //获取字段信息列表
            List<Map<String, Object>> udlTableColumns =
                    UDLDataQuery.getUDLTableColumns(dq_table_info.getTable_id().toString());
            //转化字段信息列表为meta_list
            column_info_list = DataTableFieldUtil.metaInfoToList(udlTableColumns);
        } else {
            throw new BusinessException("未找到匹配的数据层!" + table_source);
        }
        Validator.notEmpty(column_info_list, "该表的meta信息已经不存在!" + dq_failure_table.getTable_cn_name());
        //设置返回结果map
        data_meta_info.put("file_id", failure_table_id);
        data_meta_info.put("table_id", table_id);
        data_meta_info.put("data_layer", table_source);
        data_meta_info.put("table_name", dq_failure_table.getTable_en_name());
        data_meta_info.put("table_ch_name", dq_failure_table.getTable_cn_name());
        //如果表的创建日期为空,默认设置为99991231
        if (StringUtil.isNotBlank(create_date)) {
            data_meta_info.put("create_date", create_date);
        } else {
            data_meta_info.put("create_date", "99991231");
        }
        data_meta_info.put("column_info_list", column_info_list);
        return data_meta_info;
    }

    @Method(desc = "数据管控保存元数据",
            logicStep = "逻辑说明")
    @Param(name = "data_layer", desc = "数据层", range = "String类型,DCL")
    @Param(name = "file_id", desc = "数据表登记信息id", range = "String类型")
    @Param(name = "table_id", desc = "数据表id", range = "String类型")
    @Param(name = "table_ch_name", desc = "表中文名", range = "String类型", nullable = true)
    @Param(name = "columnInfoBeans", desc = "自定义实体ColumnInfoBean的对象", range = "ColumnInfoBean", isBean = true)
    public void saveMetaData(String data_layer, String file_id, String table_id, String table_ch_name,
                             ColumnInfoBean[] columnInfoBeans) {
        //数据校验
        Validator.notBlank(data_layer, "数据层为空!");
        Validator.notBlank(file_id, "编辑的元数据信息id为空!");
        Validator.notBlank(table_id, "编辑数据表id为空!");
        Validator.notNull(columnInfoBeans, "自定义实体ColumnInfoBean的对象为空!");
        //根据数据层修改不同层下的数据
        DataSourceType dataSourceType = DataSourceType.ofEnumByCode(data_layer);
        if (dataSourceType == DataSourceType.ISL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DCL) {
            TableMetaInfoTool.updateDCLTableMetaInfo(file_id, table_id, table_ch_name, columnInfoBeans, Dbo.db());
        } else if (dataSourceType == DataSourceType.DPL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DML) {
            TableMetaInfoTool.updateDMLTableMetaInfo(table_id, table_ch_name, columnInfoBeans, Dbo.db());
        } else if (dataSourceType == DataSourceType.SFL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.AML) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DQC) {
            throw new BusinessException(data_layer + "层表结构不允许编辑!");
        } else if (dataSourceType == DataSourceType.UDL) {
            TableMetaInfoTool.updateUDLTableMetaInfo(table_id, table_ch_name, columnInfoBeans, Dbo.db());
        } else {
            throw new BusinessException("未找到匹配的数据层!" + data_layer);
        }
    }

    @Method(desc = "恢复回收站表", logicStep = "恢复回收站表")
    @Param(name = "data_layer", desc = "所属数据层", range = "String 类型")
    @Param(name = "file_id", desc = "回收站表id", range = "long 类型")
    public void restoreDRBTable(String data_layer, long file_id) {
        //获取回收站表信息
        Dq_failure_table dq_failure_table = DRBDataQuery.getDRBTableInfo(file_id);
        //校验待恢复表的元信息
        if (null == dq_failure_table) {
            throw new BusinessException("待恢复表的对象已经不存在!");
        }
        if (StringUtil.isBlank(dq_failure_table.getTable_meta_info())) {
            throw new BusinessException("待恢复表的元信息已经不存在! table_id=" + dq_failure_table.getFailure_table_id());
        }
        //根据数据层和表类型恢复数据
        DataSourceType dataSourceType = DataSourceType.ofEnumByCode(data_layer);
        if (dataSourceType == DataSourceType.ISL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DCL) {
            TableMetaInfoTool.restoreDCLTableInfo(dq_failure_table, Dbo.db());
        } else if (dataSourceType == DataSourceType.DPL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DML) {
            TableMetaInfoTool.restoreDMLTableInfo(dq_failure_table, Dbo.db());
        } else if (dataSourceType == DataSourceType.SFL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.AML) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DQC) {
            TableMetaInfoTool.restoreDQCTableInfo(dq_failure_table, Dbo.db());
        } else if (dataSourceType == DataSourceType.UDL) {
            TableMetaInfoTool.restoreUDLTableInfo(dq_failure_table, Dbo.db());
        } else {
            throw new BusinessException("未找到匹配的数据层!" + data_layer);
        }
        //删除失效登记表的数据
        int execute = Dbo.execute("DELETE FROM " + Dq_failure_table.TableName + " WHERE failure_table_id = ?",
                dq_failure_table.getFailure_table_id());
        if (execute != 1) {
            throw new BusinessException("删除回收站数据失败! failure_table_id=" + dq_failure_table.getFailure_table_id());
        }
    }

    @Method(desc = "恢复回收站数据层下所有表", logicStep = "恢复回收站所有表")
    public void restoreDRBAllTable() {
        //获取回收站所有表信息
        List<Dq_failure_table> dq_failure_table_s = DRBDataQuery.getAllTableInfos();
        //循环每一张表逐条恢复
        dq_failure_table_s.forEach(
                dq_failure_table -> restoreDRBTable(dq_failure_table.getTable_source(), dq_failure_table.getFailure_table_id()));
    }

    @Method(desc = "表放入回收站(表设置为无效)", logicStep = "表放入回收站(表设置为无效)")
    @Param(name = "data_layer", desc = "所属数据层", range = "String 类型")
    @Param(name = "dsl_id", desc = "所属存储层id", range = "long 类型")
    @Param(name = "file_id", desc = "回收站表id", range = "String 类型")
    public void tableSetToInvalid(String data_layer, long dsl_id, String file_id) {
        //根据数据层和表id放入回收站
        DataSourceType dataSourceType = DataSourceType.ofEnumByCode(data_layer);
        if (dataSourceType == DataSourceType.ISL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DCL) {
            TableMetaInfoTool.setDCLTableInvalid(Dbo.db(), dsl_id, file_id);
        } else if (dataSourceType == DataSourceType.DPL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DML) {
            TableMetaInfoTool.setDMLTableInvalid(Dbo.db(), dsl_id, file_id);
        } else if (dataSourceType == DataSourceType.SFL) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.AML) {
            throw new BusinessException(data_layer + "层暂未实现!");
        } else if (dataSourceType == DataSourceType.DQC) {
            TableMetaInfoTool.setDQCTableInvalid(Dbo.db(), dsl_id, file_id);
        } else if (dataSourceType == DataSourceType.UDL) {
            TableMetaInfoTool.setUDLTableInvalid(Dbo.db(), dsl_id, file_id);
        } else {
            throw new BusinessException("未找到匹配的数据层!" + data_layer);
        }
    }

    @Method(desc = "表放入回收站(所有表设置为无效)", logicStep = "表放入回收站(所有表设置为无效)")
    public void allTableSetToInvalid() {
        //获取回收站源树菜单列表
        List<Map<String, Object>> sourceTreeInfos = TreeDataQuery.getSourceTreeInfos(TreePageSource.DATA_MANAGEMENT);
        //根据源树菜单逐层处理
        sourceTreeInfos.forEach(sourceTreeInfo -> {
            //获取当前数据层信息
            DataSourceType dataSourceType = DataSourceType.ofEnumByCode(sourceTreeInfo.get("id").toString());
            //根据数据层处理对应层数据表
            if (dataSourceType == DataSourceType.ISL) {
                throw new BusinessException(dataSourceType.getCode() + "层表放入回收站未实现!");
            } else if (dataSourceType == DataSourceType.DCL) {
                //获取DCL下存在表的所有数据存储层
                List<Data_store_layer> dataStorageLayers = MDMDataQuery.getDCLExistTableDataStorageLayers();
                //获取所有表的信息
                dataStorageLayers.forEach(data_store_layer -> {
                    //获取存储层下表信息
                    MDMDataQuery.getDCLStorageLayerTableInfos(data_store_layer).forEach(table_info -> {
                        //根据查询到的表信息将表放入回收站
                        tableSetToInvalid(dataSourceType.getCode(), data_store_layer.getDsl_id(),
                                table_info.get("file_id").toString());
                    });
                });
            } else if (dataSourceType == DataSourceType.DPL) {
                throw new BusinessException(dataSourceType.getCode() + "层表放入回收站未实现!");
            } else if (dataSourceType == DataSourceType.DML) {
                //获取DCL下存在表的所有数据存储层
                List<Data_store_layer> dataStorageLayers = MDMDataQuery.getDMLExistTableDataStorageLayers();
                //获取所有表的信息
                dataStorageLayers.forEach(data_store_layer -> {
                    //获取存储层下表信息
                    MDMDataQuery.getDMLStorageLayerTableInfos(data_store_layer).forEach(dm_datatable -> {
                        //根据查询到的表信息将表放入回收站
                        tableSetToInvalid(dataSourceType.getCode(), data_store_layer.getDsl_id(),
                                dm_datatable.get("datatable_id").toString());
                    });
                });
                throw new BusinessException(dataSourceType.getCode() + "层表放入回收站未实现!");
            } else if (dataSourceType == DataSourceType.SFL) {
                throw new BusinessException(dataSourceType.getCode() + "层表放入回收站未实现!");
            } else if (dataSourceType == DataSourceType.AML) {
                throw new BusinessException(dataSourceType.getCode() + "层表放入回收站未实现!");
            } else if (dataSourceType == DataSourceType.DQC) {
                //获取DQC下存在表的所有数据存储层
                List<Data_store_layer> dataStorageLayers = DQCDataQuery.getDQCDataInfos();
                //获取所有表的信息
                dataStorageLayers.forEach(data_store_layer -> {
                    //获取存储层下表信息
                    DQCDataQuery.getDQCTableInfos(data_store_layer.getDsl_id()).forEach(di3 -> {
                        //根据查询到的表信息将表放入回收站
                        tableSetToInvalid(dataSourceType.getCode(), data_store_layer.getDsl_id(),
                                di3.get("record_id").toString());
                    });
                });
            } else if (dataSourceType == DataSourceType.UDL) {
                //获取UDL下存在表的所有数据存储层
                List<Data_store_layer> dataStorageLayers = MDMDataQuery.getUDLDataStorageLayers();
                //获取所有表的信息
                dataStorageLayers.forEach(data_store_layer -> {
                    //获取存储层下表信息
                    MDMDataQuery.getUDLStorageLayerTableInfos(data_store_layer).forEach(dq_table_info -> {
                        //根据查询到的表信息将表放入回收站
                        tableSetToInvalid(dataSourceType.getCode(), data_store_layer.getDsl_id(),
                                dq_table_info.get("table_id").toString());
                    });
                });
            } else {
                throw new BusinessException("未找到匹配的存储层!");
            }
        });
    }

    @Method(desc = "数据管控-彻底删除表", logicStep = "数据管控-彻底删除表")
    @Param(name = "file_id", desc = "回收站表id", range = "long 类型")
    public void removeCompletelyTable(long file_id) {
        //获取 Dq_failure_table
        Dq_failure_table dft = DRBDataQuery.getDRBTableInfo(file_id);
        //数据校验
        if (StringUtil.isBlank(dft.getFailure_table_id().toString())) {
            throw new BusinessException("回收站的该表已经不存在!");
        }
        //设置失效表的表名
        String invalid_table_name = Constant.DQC_INVALID_TABLE + dft.getTable_en_name();
        //将失效登记表的数据删除
        DRBDataQuery.deleteDqFailureTableInfo(dft.getFailure_table_id());
        //表是否在配置库登记记录中存在
        boolean isExist = DataTableUtil.tableIsRepeat(dft.getTable_en_name());
        //表是否在回收站登记记录中存在
        long count = Dbo.queryNumber("SELECT COUNT(table_en_name) FROM " + Dq_failure_table.TableName + " WHERE table_en_name=?",
                dft.getTable_en_name()).orElseThrow(() -> new BusinessException("检查规则reg_num否存在的SQL错误"));
        //如果该表的登记记录在配置库中不存在,并且该表信息在回收站登记信息中不存在,则删除该表对应的存储信息(对应的关联表记录)
        if (!isExist && count == 0) {
            //删除对应的关联表记录
            StoreLayerDataSource dataSource = StoreLayerDataSource.ofEnumByCode(dft.getData_source());
            TableMetaInfoTool.deleteTableRegistrationRecordData(dataSource, dft.getFile_id());
        }
        //彻底删除存储层中的数表
        DropDataTable.dropTableByDataLayer(invalid_table_name, Dbo.db(), dft.getDsl_id());
        logger.info("彻底删除存储层中相关的数表完成! table_name=" + invalid_table_name);
    }

    @Method(desc = "数据管控-彻底删除所有表",
            logicStep = "数据管控-彻底删除所有表")
    public void removeCompletelyAllTable() {
        //获取回收站所有表信息
        List<Dq_failure_table> allTableInfos = DRBDataQuery.getAllTableInfos();
        //循环每一张表逐条删除
        allTableInfos.forEach(dft -> removeCompletelyTable(dft.getFailure_table_id()));
    }

    @Method(desc = "根据存储层id获取存储层配置信息", logicStep = "根据存储层id获取存储层配置信息")
    @Param(name = "dsl_id", desc = "存储层id", range = "参数例子")
    @Return(desc = "存储层配置信息", range = "dsl:存储层信息,dsl_attr:存储层配置信息,dsl_added:存储层附加信息,slfti:存储层支持的类型列表信息")
    public Map<String, Object> getStorageLayerConfInfo(long dsl_id) {
        //数据校验
        Validator.notEmpty(String.valueOf(dsl_id), "存储层配置id不能为空! dsl_id=" + dsl_id);
        //初始化返回结果
        Map<String, Object> slci = new HashMap<>();
        //获取存储层信息
        Data_store_layer dsl = Dbo.queryOneObject(Data_store_layer.class,
                "select * from " + Data_store_layer.TableName + " where dsl_id=?",
                dsl_id).orElseThrow(() -> new BusinessException("获取存储层信息失败! dsl_id=" + dsl_id));
        slci.put("dsl", dsl);
        //获取存储层配置信息
        List<Data_store_layer_attr> dsl_attr_s = Dbo.queryList(Data_store_layer_attr.class, "select * from " +
                Data_store_layer_attr.TableName + " where dsl_id=?", dsl_id);
        slci.put("dsl_attr_s", dsl_attr_s);
        //获取存储层附加信息
        List<Data_store_layer_added> dsl_added_s = Dbo.queryList(Data_store_layer_added.class, "select * from " +
                Data_store_layer_added.TableName + " where dsl_id=?", dsl_id);
        slci.put("dsl_added_s", dsl_added_s);
        //存储层支持的类型列表信息 storageLayerFieldTypeInfos
        List<String> slfti = Dbo.queryOneColumnList("select distinct tc.target_type from " +
                Data_store_layer.TableName + " dsl join " + Type_contrast_sum.TableName + " tcs" +
                " on dsl.dtcs_id = tcs.dtcs_id join " + Type_contrast.TableName + " tc" +
                " on tcs.dtcs_id = tc.dtcs_id where dsl_id = ?", dsl_id);
        //处理查询到的类型列表信息,去除括号
        List<String> filedTypes = new ArrayList<>();
        if (slfti.isEmpty()) {
            throw new BusinessException("存储层: " + dsl.getDsl_name() + "对应支持的数据类型为空,请先配置的类型配置信息!");
        }
        for (String s : slfti) {
            if (s.contains(Constant.LXKH) && s.contains(Constant.RXKH)) {
                filedTypes.add(s.substring(0, s.indexOf(Constant.LXKH)));
            } else {
                filedTypes.add(s);
            }
        }
        filedTypes = filedTypes.stream().distinct().collect(Collectors.toList());
        slci.put("filedTypes", filedTypes);
        return slci;
    }

    @Method(desc = "数据管控-创建表", logicStep = "数据管控-创建表")
    @Param(name = "dsl_id", desc = "配置存储层id", range = "long 类型")
    @Param(name = "dqTableInfoBean", desc = "自定义实体DqTableInfoBean", range = "DqTableInfoBean", isBean = true)
    @Param(name = "dqTableColumnBeans", desc = "自定义实体DqTableColumnBean数组", range = "DqTableColumnBean[]", isBean = true)
    public void createTable(long dsl_id, DqTableInfoBean dqTableInfoBean, DqTableColumnBean[] dqTableColumnBeans) {
        //数据校验
        Data_store_layer dsl = Dbo.queryOneObject(Data_store_layer.class, "SELECT * FROM " + Data_store_layer.TableName +
                " WHERE dsl_id=?", dsl_id).orElseThrow(() -> new BusinessException("获取存储层信息失败! dsl_id=" + dsl_id));
        Validator.notNull(dqTableInfoBean, "表存储实体Bean不能为空! dqTableInfoBean");
        Validator.notNull(dqTableColumnBeans, "表字段存储实体Bean[]不能为空! dqTableColumnBeans");
        Validator.notBlank(dqTableInfoBean.getTable_name(), "表名不能为空!");
        //获取HBase表设置为RowKey的字段排序信息
        String[] hbase_sort_columns = dqTableInfoBean.getHbase_sort_columns();
        //设置表信息
        Dq_table_info dqTableInfo = new Dq_table_info();
        BeanUtils.copyProperties(dqTableInfoBean, dqTableInfo);
        dqTableInfo.setTable_id(PrimayKeyGener.getNextId());
        if (StringUtil.isBlank(dqTableInfo.getIs_trace())) {
            dqTableInfo.setIs_trace(IsFlag.Fou.getCode());
        }
        dqTableInfo.setCreate_date(DateUtil.getSysDate());
        dqTableInfo.setEnd_date(Constant.MAXDATE);
        dqTableInfo.setCreate_id(getUserId());
        //设置列信息列表
        List<Dq_table_column> dqTableColumns = new ArrayList<>();
        for (int i = 0; i < dqTableColumnBeans.length; i++) {
            //获取表字段自定义实体信息
            DqTableColumnBean dqTableColumnBean = dqTableColumnBeans[i];
            //初始化表字段实体
            Dq_table_column dqTableColumn = new Dq_table_column();
            //如果表的字段名和字段类型不为空,则设置字段实体信息和字段存储附加信息关系
            if (StringUtil.isNotBlank(dqTableColumnBean.getColumn_name()) && StringUtil.isNotBlank(dqTableColumnBean.getColumn_type())) {
                //实体转换,设置字段实体
                BeanUtils.copyProperties(dqTableColumnBean, dqTableColumn);
                dqTableColumn.setField_id(PrimayKeyGener.getNextId());
                dqTableColumn.setTable_id(dqTableInfo.getTable_id());
                dqTableColumns.add(dqTableColumn);
                //设置数据字段存储关系表
                Dcol_relation_store dcol_relation_store;
                if (null != dqTableColumnBean.getDslad_id_s() && dqTableColumnBean.getDslad_id_s().length > 0) {
                    for (long dslad_id : dqTableColumnBean.getDslad_id_s()) {
                        //获取数据存储附加信息
                        Data_store_layer_added dsla = Dbo.queryOneObject(Data_store_layer_added.class,
                                "SELECT * FROM " + Data_store_layer_added.TableName + " WHERE dslad_id=?",
                                dslad_id).orElseThrow(() -> new BusinessException("获取数据存储附加信息失败!"));
                        StoreLayerAdded storeLayerAdded = StoreLayerAdded.ofEnumByCode(dsla.getDsla_storelayer());
                        //设置字段存储序号位置,字段序号
                        long csi_number = i;
                        //如果字段附加信息是rowkey,则设置字段的序号位置,
                        if (storeLayerAdded == StoreLayerAdded.RowKey) {
                            for (int j = 0; j < hbase_sort_columns.length; j++) {
                                //如果当前字段和排序一样,则取排序字段的顺序做序号位置
                                if (dqTableColumnBean.getColumn_name().equalsIgnoreCase(hbase_sort_columns[j])) {
                                    csi_number = j;
                                }
                            }
                        }
                        //设置存储关系信息
                        dcol_relation_store = new Dcol_relation_store();
                        dcol_relation_store.setCol_id(dqTableColumn.getField_id());
                        dcol_relation_store.setDslad_id(dslad_id);
                        dcol_relation_store.setData_source(StoreLayerDataSource.UD.getCode());
                        dcol_relation_store.setCsi_number(csi_number);
                        dcol_relation_store.add(Dbo.db());
                    }
                }
            }
        }

        //校验表名在系统中是否存在,存在则直接终止创建表
        boolean isRepeat = DataTableUtil.tableIsRepeat(dqTableInfo.getTable_name());
        if (isRepeat) {
            throw new BusinessException("表: " + dqTableInfo.getTable_name() + ",已经在系统中存在,请更换表名!" + dqTableInfo.getTable_name());
        }
        //设置数据表存储关系
        Dtab_relation_store dtab_relation_store = new Dtab_relation_store();
        dtab_relation_store.setDsl_id(dsl_id);
        dtab_relation_store.setTab_id(dqTableInfo.getTable_id());
        dtab_relation_store.setData_source(StoreLayerDataSource.UD.getCode());
        dtab_relation_store.setIs_successful(JobExecuteState.WanCheng.getCode());
        //保存表源信息和表存储关系信息到配置库
        dqTableInfo.add(Dbo.db());
        dqTableColumns.forEach(dq_table_column -> dq_table_column.add(Dbo.db()));
        dtab_relation_store.add(Dbo.db());
        //创建表到指定的存储层下
        CreateDataTable.createDataTableByStorageLayer(Dbo.db(), dqTableInfo, dqTableColumns, dsl_id);
        logger.info("保存表源信息到配置库成功! table_name: " + dqTableInfo.getTable_name());
    }
}
