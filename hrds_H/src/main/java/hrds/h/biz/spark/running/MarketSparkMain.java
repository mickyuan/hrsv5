package hrds.h.biz.spark.running;

import hrds.commons.codes.Store_type;
import hrds.commons.exception.AppSystemException;
import hrds.h.biz.config.MarketConf;
import hrds.h.biz.config.MarketConfUtils;
import hrds.h.biz.spark.dealdataset.DatasetProcessBack;
import hrds.h.biz.spark.dealdataset.SparkDataset;
import org.apache.spark.sql.SparkSession;

import static hrds.h.biz.spark.running.SparkHandleArgument.DatabaseArgs;
import static hrds.h.biz.spark.running.SparkHandleArgument.HiveArgs;

/**
 * spark 任务提交
 * 通过 java -jar 方式提交的主类
 *
 * @Author: Mick Yuan
 * @Date:
 * @Since jdk1.8
 */
public class MarketSparkMain {

    private static void handle(final Handler handler) {
        if (handler.getArgs().isIncrement()) {
            handler.increment();
        } else {
            handler.insert();
        }
    }

    public static void main(String[] args) {
        String datatableId = args[0];
        String handleArgs = args[1];

        //根据 datatable_id 将文件序列化成对象
        MarketConf conf = MarketConfUtils.deserialize(datatableId);
        //实例化dataset处理对象
        SparkDataset sparkDataset = new DatasetProcessBack(conf);

        try (SparkSession spark = sparkDataset.getSparkSession()) {

            handleArgs = SparkJobRunner.obtainSatisfyShellString(handleArgs);

            //获取此次spark提交的处理类型
            Store_type handleType = SparkHandleArgument.fromString(handleArgs,
                    SparkHandleArgument.class).getHandleType();

            //根据处理类型获取对应类型处理器
            Handler handler;
            if (Store_type.DATABASE.equals(handleType)) {
                handler = new DatabaseHandler(spark, sparkDataset.getDataset(),
                        (DatabaseArgs) SparkHandleArgument.fromString(handleArgs, DatabaseArgs.class));
            } else if (Store_type.HIVE.equals(handleType)) {
                handler = new HiveHandler(spark, sparkDataset.getDataset(),
                        (HiveArgs) SparkHandleArgument.fromString(handleArgs, HiveArgs.class));
            } else {
                throw new AppSystemException("无法处理类型：" + handleType.getValue());
            }
            //开始处理
            handle(handler);
        }
    }

}
