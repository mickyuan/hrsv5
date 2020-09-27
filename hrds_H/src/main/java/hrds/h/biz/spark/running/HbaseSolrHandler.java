package hrds.h.biz.spark.running;

import fd.ng.core.utils.MD5Util;
import hrds.commons.exception.AppSystemException;
import hrds.commons.hadoop.hadoop_helper.HdfsOperator;
import hrds.commons.hadoop.hbaseindexer.bean.HbaseSolrField;
import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.hadoop.solr.utils.CollectionUtil;
import hrds.commons.utils.CommonVariables;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.storage.StorageLevel;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HbaseSolrHandler extends Handler {
    private static final Logger logger = LogManager.getLogger();

    private final SparkHandleArgument.HbaseSolrArgs hbaseSolrArgs;
    final List<String> allColName = new ArrayList<>();
    List<String> solrColName;
    List<HbaseSolrField> solrColFields = new ArrayList<>();

    HbaseSolrHandler(SparkSession spark, Dataset<Row> dataset,
                     SparkHandleArgument.HbaseSolrArgs args) {
        super(spark, dataset, args);
        this.hbaseSolrArgs = args;

        for (HbaseSolrField hbaseSolrField : hbaseSolrArgs.getHbaseSolrFields()) {
            allColName.add(hbaseSolrField.getHbaseColumnName());
        }
        Collections.sort(allColName);
        logger.info(allColName);
        solrColName = hbaseSolrArgs.getSolrCols();
        if (solrColName != null) {
            solrColFields.addAll(hbaseSolrArgs.getHbaseSolrFields().stream()
                    .filter(a -> solrColName.contains(a.getHbaseColumnName())).collect(Collectors.toList()));
        }
    }

    @Override
    public void insert() throws Exception {
        // HBase 插入
        String bulkloadTempDirectory = "/hyren/tmp/bulkload/" + System.currentTimeMillis();
        Configuration conf = ConfigReader.getConfiguration();

        List<String> rowkeys = hbaseSolrArgs.getRowkeys();
        boolean isRowkeyMd5 = rowkeys == null;
        //TODO 这里需要改 逻辑不对
        int needComputeMd5ColNumber =
                hbaseSolrArgs.isMultipleInput() ? allColName.size() - 2 : allColName.size() - 1;

        if (isRowkeyMd5) {
            dataset = dataset.distinct();
        } else {
            dataset = dataset.dropDuplicates(rowkeys.toArray(new String[0]));
        }
        JavaRDD<Row> javaRDD = dataset.javaRDD().persist(StorageLevel.MEMORY_AND_DISK());

        javaRDD.flatMapToPair(row -> {
            List<Tuple2<ImmutableBytesWritable, KeyValue>> arr = new ArrayList<>();
            //判断rowkey：1.MD5. 2.给定的列
            String rowkey = uniqueKeyGen(isRowkeyMd5, needComputeMd5ColNumber, rowkeys, row);

            byte[] rowkeyBytes = Bytes.toBytes(rowkey);
            ImmutableBytesWritable ibw = new ImmutableBytesWritable(rowkeyBytes);
            for (String colName : allColName) {

                arr.add(new Tuple2<>(ibw, new KeyValue(rowkeyBytes, "F".getBytes(),
                        Bytes.toBytes(colName), Bytes.toBytes(String.valueOf(row.get(row.fieldIndex(colName.toUpperCase())))))));
            }
            return arr.iterator();
        })
                .coalesce(1)
                .sortByKey()
                .saveAsNewAPIHadoopFile(bulkloadTempDirectory, ImmutableBytesWritable.class,
                        KeyValue.class, HFileOutputFormat2.class, conf);

        //启动 bulkload 任务
        try (Connection conn = ConnectionFactory.createConnection(conf);
             Table table = conn.getTable(TableName.valueOf(tableName))) {
            // 设置job 对象
            Job job = Job.getInstance(conf, "bulkload_" + tableName);
            job.setMapOutputKeyClass(ImmutableBytesWritable.class);
            job.setMapOutputValueClass(KeyValue.class);
            job.setOutputFormatClass(HFileOutputFormat2.class);

            HFileOutputFormat2.configureIncrementalLoad(job, table,
                    conn.getRegionLocator(TableName.valueOf(tableName)));

            new LoadIncrementalHFiles(conf)
                    .doBulkLoad(new Path(bulkloadTempDirectory), (HTable) table);
        }
        //删除bulkload临时文件
        try (HdfsOperator operator = new HdfsOperator()) {
            operator.deletePath(bulkloadTempDirectory);
        }

        //处理solr进数逻辑
        if (!solrColFields.isEmpty()) {

            String solrZkHost = CommonVariables.ZK_HOST;
            String collectionName = CollectionUtil.getCollection(tableName);
            logger.info("zookeeper address:" + CommonVariables.ZK_HOST);
            logger.info("collection's name:" + collectionName);

            logger.info("solr-partitions-size:"+javaRDD.partitions().size());
            javaRDD.foreachPartition(rows -> {
                //防止分区任务空跑
                if(!rows.hasNext()){
                    return;
                }
                try (CloudSolrClient server = new CloudSolrClient(solrZkHost)) {
                    server.setDefaultCollection(collectionName);
                    server.connect();
                    List<SolrInputDocument> solrDocuments = new ArrayList<>();
                    while (rows.hasNext()) {
                        Row row = rows.next();
                        SolrInputDocument solrDocument = new SolrInputDocument();
                        String rowkey = uniqueKeyGen(isRowkeyMd5, needComputeMd5ColNumber, rowkeys, row);
                        solrDocument.setField("id", rowkey);
                        solrColFields.forEach(field ->
                                solrDocument.setField(field.getSolrColumnName(),
                                        row.get(row.fieldIndex(field.getHbaseColumnName().toUpperCase()))));
                        solrDocuments.add(solrDocument);
                    }
                    server.add(solrDocuments);
                    server.commit();
                }
            });
        }


    }

    /**
     * 设置 bulkload job 的处理过程
     */
    private String uniqueKeyGen(boolean isRowkeyMd5, int needComputeMd5ColNumber, List<String> rowkeys, Row row) {
        StringBuilder uniqueKey = new StringBuilder();
        //判断rowkey：1.MD5. 2.给定的列
        if (isRowkeyMd5) {
            for (int i = 0; i < needComputeMd5ColNumber; i++) {
                uniqueKey.append(row.get(i));
            }
            return MD5Util.md5String(uniqueKey.toString());
        } else {
            rowkeys.forEach(rowkeyName ->
                    uniqueKey.append(row.get(row.fieldIndex(rowkeyName.toUpperCase()))));
            return uniqueKey.toString();
        }


    }

    @Override
    public void increment() {
        throw new AppSystemException("hbase 或者 solr 任务不支持增量过程.");
    }
}
