package hrds.h.biz.realloader;

import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.codes.DatabaseType;
import hrds.commons.codes.StoreLayerAdded;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.entity.Datatable_field_info;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.hadoop_helper.HBaseHelper;
import hrds.commons.hadoop.hbaseindexer.bean.HbaseSolrField;
import hrds.commons.hadoop.solr.utils.CollectionUtil;
import hrds.commons.utils.Constant;
import hrds.commons.utils.StorageTypeKey;
import hrds.h.biz.config.MarketConf;
import hrds.h.biz.spark.running.SparkHandleArgument;
import hrds.h.biz.spark.running.SparkJobRunner;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HbaseOverSolrLoader extends AbstractRealLoader {

    /**
     * spark 作业的配置类
     */
    private final SparkHandleArgument.HbaseSolrArgs hbaseSolrArgs = new SparkHandleArgument.HbaseSolrArgs();
    String prePartitions;

    protected HbaseOverSolrLoader(MarketConf conf) {
        super(conf);
        initArgs();
        prePartitions = conf.getDmDatatable().getPre_partition();
    }

    private void initArgs() {
        hbaseSolrArgs.setHandleType(Store_type.HBASE);
        hbaseSolrArgs.setEtlDate(etlDate);
        hbaseSolrArgs.setTableName(tableName);
        hbaseSolrArgs.setMultipleInput(isMultipleInput);
        hbaseSolrArgs.setDatatableId(datatableId);
        hbaseSolrArgs.setRowkeys(getRowKeys());
        hbaseSolrArgs.setSolrCols(getSolrCols());
        hbaseSolrArgs.setHbaseSolrFields(solrColNameAndTypeConverter(conf.getDatatableFields()));
    }

    static List<HbaseSolrField> solrColNameAndTypeConverter(List<Datatable_field_info> fields) {
        final List<HbaseSolrField> hbaseSolrFields = new ArrayList<>();
        fields.forEach(field -> {
            //根据列名和hbase字段名，solr列名会自动生成
            HbaseSolrField hbaseSolrField = new HbaseSolrField();
            hbaseSolrField.setHbaseColumnName(field.getField_en_name());
            hbaseSolrField.setType(field.getField_type());
            hbaseSolrFields.add(hbaseSolrField);
        });
        return hbaseSolrFields;
    }

    private List<String> getRowKeys() {
        List<String> rowkeys = conf.getAddAttrColMap().get(StoreLayerAdded.RowKey.getCode());
        if (rowkeys == null) {
            logInfo(tableName + " 存储层未选择 rowkey 扩展项.");
            return null;
        }
        if (rowkeys.isEmpty()) {
            logInfo(tableName + " rowkey 未选择.");
            return null;
        }
        return rowkeys;
    }

    private List<String> getSolrCols() {
        List<String> solrCols = conf.getAddAttrColMap().get(StoreLayerAdded.Solr.getCode());
        if (solrCols == null) {
            logInfo("存储层未选择 Solr 扩展项.");
            return null;
        }
        if (solrCols.isEmpty()) {
            logInfo(" solr列未选择.");
            return null;
        }
        return solrCols;
    }

    @Override
    public void ensureRelation() {
        try (HBaseHelper helper = HBaseHelper.getHelper()) {
            if (!helper.existsTable(tableName)) {
                if (StringUtil.isBlank(prePartitions)) {
                    helper.createSimpleTable(tableName);
                } else {
                    helper.createTableWithPartitions(tableName, prePartitions,
                            Bytes.toString(Constant.HBASE_COLUMN_FAMILY));
                }
            }
            hiveMapHBase(tableName, conf.getDatatableFields());
            //solr可能是没有选择的
            if (hbaseSolrArgs.getSolrCols() != null) {
                CollectionUtil.softCreateCollection(CollectionUtil.getCollection(tableName));
            }
        } catch (IOException e) {
            throw new AppSystemException("确认 hbase 表或者 solr collection 报错：", e);
        }
    }

    private DatabaseWrapper getHiveDb() {
        tableLayerAttrs.put(StorageTypeKey.database_type, DatabaseType.Hive.getCode());
        return ConnectionTool.getDBWrapper(tableLayerAttrs);
    }

    /**
     * 创建hbase表的hive映射表
     *
     * @param tableName 表名
     */
    private void hiveMapHBase(String tableName, List<Datatable_field_info> fields) {
        try (DatabaseWrapper db = getHiveDb()) {
            StringBuilder sql = new StringBuilder(1024);
            //表存在则删除，这里外部表删除并不会删除HBase的表
            db.execute("DROP TABLE IF EXISTS " + tableName);
            sql.append("CREATE EXTERNAL TABLE IF NOT EXISTS ").append(tableName).append(" ( ").
                    append(Constant.HIVEMAPPINGROWKEY).append(" string , ");
            for (int i = 0; i < fields.size(); i++) {
                sql.append("`").append(fields.get(i).getField_en_name()).append("` ").append("string").append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(") row format delimited fields terminated by '\\t' ");
            sql.append("STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' ");
            sql.append("WITH SERDEPROPERTIES (\"hbase.columns.mapping\" = \":key , ");

            for (Datatable_field_info field : fields) {
                sql.append(Bytes.toString(Constant.HBASE_COLUMN_FAMILY)).append(":").append(field.getField_en_name()).append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append("\") TBLPROPERTIES (\"hbase.table.name\" = \"").append(tableName).append("\")");
            db.execute(sql.toString());
        } catch (Exception e) {
            throw new AppSystemException("hive映射HBase表失败", e);
        }
    }

    @Override
    public void append() {
        hbaseSolrArgs.setOverWrite(false);
        SparkJobRunner.runJob(hbaseSolrArgs);
    }

    @Override
    public void replace() {
        try (HBaseHelper helper = HBaseHelper.getHelper()) {
            if (helper.existsTable(tableName)) {
                helper.truncateTable(tableName, true);
            }
            //solr可能是没有选择的
            if (hbaseSolrArgs.getSolrCols() != null) {
                CollectionUtil.deleteCollection(CollectionUtil.getCollection(tableName));
                //删除直接创建会报错说collection已存在，所以睡眠两秒
                Thread.sleep(2000);
                CollectionUtil.createCollection(CollectionUtil.getCollection(tableName));
            }

            hbaseSolrArgs.setOverWrite(true);
            hbaseSolrArgs.setTableName(tableName);
            SparkJobRunner.runJob(hbaseSolrArgs);
        } catch (IOException | InterruptedException e) {
            throw new AppSystemException(e);
        }
    }

    @Override
    public void increment() {
        throw new AppSystemException("该 loader 目前不支持增量.");
    }

    @Override
    public void handleException() {
        versionManager.rollBack();
    }

    @Override
    public void restore() {
        //TODO  怎么回滚
//        try (HBaseHelper helper = HBaseHelper.getHelper()) {
//            Scan scan = new Scan();
//            scan.addFamily(Constant.HBASE_COLUMN_FAMILY);
//
//        }
    }

    @Override
    public void finalWork() {
        versionManager.updateSqlVersion();
        if (versionManager.isVersionExpire()) {
            versionManager.updateFieldVersion();
        }
        versionManager.commit();
    }
}
