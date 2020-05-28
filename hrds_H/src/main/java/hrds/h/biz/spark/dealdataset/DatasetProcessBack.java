package hrds.h.biz.spark.dealdataset;

import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.ProcessingData;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.DruidParseQuerySql;
import hrds.commons.utils.StorageTypeKey;
import hrds.h.biz.config.MarketConf;
import hrds.h.biz.spark.dealdataset.dataprocesser.AddColumnsForDataSet;
import hrds.h.biz.spark.dealdataset.dataprocesser.DataSetProcesser;
import hrds.h.biz.spark.initialize.SparkSessionBuilder;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalog.Catalog;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

/**
 * Description: 通过spark的方式将查询出的结果落地到hdfs上（parquet）
 * <p>
 * Date:2018年5月13日下午8:12:30
 * Copyright (c) 2018, yuanqi@beyondsoft.com All Rights Reserved.
 *
 * @author yuanqi
 * @since JDK 1.7
 */
public class DatasetProcessBack implements SparkDataset, Closeable {

    private SparkSession sparkSession;
    private Dataset<Row> dataset = null;
    private MarketConf conf;

    private boolean dataSetAlreadyProcessed = false;//dataset是否已经被处理过

    /*
     * 数据处理器
     */
    private DataSetProcesser processer;

    public DatasetProcessBack(MarketConf marketConf) {

        this(marketConf, new AddColumnsForDataSet(marketConf));
    }

    public DatasetProcessBack(MarketConf conf, DataSetProcesser processer) {

        this.conf = conf;
        this.sparkSession = SparkSessionBuilder.build(conf);
        this.processer = processer;
    }

    /**
     * 获取到spark的DataSet对象
     *
     * @return
     * @author yuanqi
     * Date:2018年5月13日下午3:19:59
     * @since JDK 1.7
     */
    private Dataset<Row> getProcessedDataSet() {

        dataset = dataframeFromSql(conf.getCompleteSql());
        dataset = processDataSet(dataset);
        dataSetAlreadyProcessed = true;
        return dataset;
    }

    /**
     * 获取已经做过业务处理的dataset
     *
     * @return
     * @author yuanqi
     * Date:2018年5月13日下午9:09:17
     * @since JDK 1.7
     */
    @Override
    public Dataset<Row> getDataset() {

        if (!dataSetAlreadyProcessed) {
            dataset = getProcessedDataSet();
        }
        return dataset;
    }


    private Dataset<Row> processDataSet(Dataset<Row> dataSet) {

        if (processer == null) {
            return dataSet;
        }
        return processer.process(dataSet);

    }

    @Override
    public void close() {

        if (sparkSession != null) {
            sparkSession.close();
        }

    }

    @Override
    public SparkSession getSparkSession() {

        return sparkSession;
    }

    /**
     * 把包含多数据存储层的表的sql查询转换成 dataframe
     * <p>
     * 我们需要将无法用 sparksql 查询到的表
     * 注册成 sparksql 中的临时视图 {@link Dataset#createOrReplaceGlobalTempView(java.lang.String)}
     * <p>
     * <p>
     * hive,hbase 的表是可以直接查询到的
     * jdbc 的表是需要注册临时视图的
     * 其他存储层的暂时不看
     * <p>
     * 所以现在如果表不属于hbase,hive,jdbc层的就报错
     *
     * @param sql 用户给定的 sql
     * @return sql转换成的 dataframe
     */
    private Dataset<Row> dataframeFromSql(String sql) {
        Catalog catalog = sparkSession.catalog();
        if (catalog.databaseExists("hyshf")) {
            catalog.setCurrentDatabase("hyshf");
        }

        List<String> listTable = DruidParseQuerySql.parseSqlTableToList(sql);
        try (DatabaseWrapper db = new DatabaseWrapper()) {

            for (String tableName : listTable) {
                List<LayerBean> layerByTable = ProcessingData.getLayerByTable(tableName, db);
                if (!validTableLayer(layerByTable)) {
                    throw new AppSystemException("表 " + tableName + " 不属于Database,Hive,Hbase中的任意一层");
                }
                if (needCreateTempView(layerByTable)) {
                    //如果表存在于多个关系型数据库，就随便读个表吧
                    Map<String, String> layerAttr = layerByTable.get(0).getLayerAttr();
                    createJdbcTempView(tableName, layerAttr);
                }
            }
        }
        return sparkSession.sql(sql);
    }

    private static boolean needCreateTempView(List<LayerBean> layerByTable) {
        for (LayerBean layerBean : layerByTable) {
            if (Store_type.HIVE.getCode().equals(layerBean.getStore_type()) ||
                    Store_type.HBASE.getCode().equals(layerBean.getStore_type())) {
                return false;
            }
        }
        return true;
    }

    private static boolean validTableLayer(List<LayerBean> layerByTable) {
        if (layerByTable == null) {
            return false;
        }
        for (LayerBean layerBean : layerByTable) {
            if (Store_type.HIVE.getCode().equals(layerBean.getStore_type()) ||
                    Store_type.HBASE.getCode().equals(layerBean.getStore_type()) ||
                    Store_type.DATABASE.getCode().equals(layerBean.getStore_type())) {
                return true;
            }
        }
        return false;
    }

    private void createJdbcTempView(String tableName, Map<String, String> layerAttr) {
        sparkSession.read()
                .format("jdbc")
                .option("url", layerAttr.get(StorageTypeKey.jdbc_url))
                .option("dbtable", tableName)
                .option("user", layerAttr.get(StorageTypeKey.user_name))
                .option("password", layerAttr.get(StorageTypeKey.database_pwd))
                .load()
                .createOrReplaceTempView(tableName);
    }

}
