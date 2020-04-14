package hrds.h.biz.spark.running;

import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.Store_type;
import hrds.commons.exception.AppSystemException;
import hrds.h.biz.config.MarketConf;
import hrds.h.biz.config.MarketConfUtils;
import hrds.h.biz.spark.dealdataset.DatasetProcessBack;
import hrds.h.biz.spark.dealdataset.SparkDataset;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * @Author: Mick Yuan
 * @Date:
 * @Since jdk1.8
 */
public class MarketSparkMain {
    private MarketConf conf;
    private SparkSession sparkSession;
    private Dataset<Row> dataset;

    public MarketSparkMain(String datatableId) {

        //根据 datatable_id 将文件序列化成对象
        conf = MarketConfUtils.deserialize(datatableId);
        //实例化dataset处理对象
        SparkDataset sparkDataset = new DatasetProcessBack(conf);

        dataset = sparkDataset.getDataset();
        sparkSession = sparkDataset.getSparkSession();
    }

    private void handleJdbc(final SparkHandleArgument.DatabaseArgs databaseArgs) {
        new DatabaseHandle(dataset, databaseArgs, conf.getTableName())
                .handle();


    }

    public static void main(String[] args) {
        String datatableId = args[0];
        if (StringUtil.isBlank(datatableId)) {
            throw new AppSystemException("Spark runner 主类参数不可为空: " + datatableId);
        }

        MarketSparkMain main = new MarketSparkMain(datatableId);

        String handleArgs = args[1];
        SparkHandleArgument sparkHandleArgument = SparkHandleArgument.fromString(handleArgs);

        Store_type handleType = sparkHandleArgument.getHandleType();

        if (Store_type.DATABASE.equals(handleType)) {
            main.handleJdbc((SparkHandleArgument.DatabaseArgs) sparkHandleArgument);
        } else {
            throw new AppSystemException("无法处理类型：" + handleType.getValue());
        }


    }

}
