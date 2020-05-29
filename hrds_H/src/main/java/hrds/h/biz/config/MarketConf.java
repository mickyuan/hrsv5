package hrds.h.biz.config;


import hrds.commons.entity.*;

import java.io.Serializable;
import java.util.List;

/**
 * 集市作业配置类
 * @Author: Mick
 */
public class MarketConf implements Serializable {

    /**
     * 集市任务设置id
     */
    private final String datatableId;
    /**
     * 调度日期
     */
    private final String etlDate;
    /**
     * sql动态参数
     */
    private final String sqlParams;
    /**
     * 是否是属于重跑
     * 当属于重跑时，需要将当天已经跑过的数据清除掉
     */
    private boolean rerun;
    /**
     * 目的地表名
     */
    private String tableName;
    /**
     * 需要执行的主sql
     */
    private String completeSql;
    /**
     * 多作业导入同一表
     */
    private boolean multipleInput;
    /**
     * 后置作业sql 可为null 可以是多个sql用;;分割
     */
    private String finalSql;
    /**
     * 数据表信息
     */
    private Dm_datatable dmDatatable = null;
    /**
     * 集市字段信息
     */
    private List<Datatable_field_info> datatableFields;
    /**
     * 集市表存储关系表
     */
    private Dm_relation_datatable dmRelationDatatable = null;
    /**
     * 集市存储层配置表
     */
    private Data_store_layer dataStoreLayer = null;
    /**
     * 数据存储层配置属性表
     */
    private List<Data_store_layer_attr> dataStoreLayerAttrs = null;


    private MarketConf(String datatableId, String etldate, String sqlParams) {
        this.datatableId = datatableId;
        this.etlDate = etldate;
        this.sqlParams = sqlParams;
    }

    /**
     * 获取 集市配置
     * 包括参数检查与初始化
     * @param datatableId 集市主表主键
     * @param etldate 跑批日期
     * @param sqlParams sql动态参数
     * @return 集市配置实体
     */
    public static MarketConf getConf(String datatableId, String etldate, String sqlParams) {

        //验证输入参数合法性
        MarketConfUtils.checkArguments(datatableId, etldate);
        final MarketConf conf = new MarketConf(datatableId, etldate, sqlParams);
        //初始化实体类
        MarketConfUtils.initBeans(conf);
        //验证是否属于重跑
        MarketConfUtils.checkReRun(conf, etldate);

        return conf;
    }

    public boolean isMultipleInput() {
        return multipleInput;
    }

    public void setMultipleInput(boolean multipleInput) {
        this.multipleInput = multipleInput;
    }

    public boolean isRerun() {
        return rerun;
    }

    void setRerun(boolean rerun) {
        this.rerun = rerun;
    }

    public String getDatatableId() {
        return datatableId;
    }

    public String getEtlDate() {
        return etlDate;
    }

    public String getSqlParams() {
        return sqlParams;
    }

    void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getCompleteSql() {
        return completeSql;
    }

    void setCompleteSql(String completeSql) {
        this.completeSql = completeSql;
    }

    public String getFinalSql() {
        return finalSql;
    }

    public void setFinalSql(String finalSql) {
        this.finalSql = finalSql;
    }

    public Dm_datatable getDmDatatable() {
        return dmDatatable;
    }

    void setDmDatatable(Dm_datatable dmDatatable) {
        this.dmDatatable = dmDatatable;
    }

    public List<Datatable_field_info> getDatatableFields() {
        return datatableFields;
    }

    void setDatatableFields(List<Datatable_field_info> datatableFields) {
        this.datatableFields = datatableFields;
    }

    public Dm_relation_datatable getDmRelationDatatable() {
        return dmRelationDatatable;
    }

    void setDmRelationDatatable(Dm_relation_datatable dmRelationDatatable) {
        this.dmRelationDatatable = dmRelationDatatable;
    }

    public Data_store_layer getDataStoreLayer() {
        return dataStoreLayer;
    }

    void setDataStoreLayer(Data_store_layer dataStoreLayer) {
        this.dataStoreLayer = dataStoreLayer;
    }

    public List<Data_store_layer_attr> getDataStoreLayerAttrs() {
        return dataStoreLayerAttrs;
    }

    void setDataStoreLayerAttrs(List<Data_store_layer_attr> dataStoreLayerAttrs) {
        this.dataStoreLayerAttrs = dataStoreLayerAttrs;
    }
}
