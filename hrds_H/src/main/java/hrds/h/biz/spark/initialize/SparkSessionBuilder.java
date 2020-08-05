package hrds.h.biz.spark.initialize;

import hrds.commons.codes.SqlEngine;
import hrds.h.biz.config.MarketConf;
import hrds.h.biz.spark.function.Function;
import hrds.h.biz.spark.function.FunctionsReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.SparkSession.Builder;
import org.apache.spark.sql.UDFRegistration;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;

import java.util.Iterator;

/**
 * 初始化SparkSession对象
 * @Author: mick
 */
public class SparkSessionBuilder {

    private static final Log logger = LogFactory.getLog(SparkSessionBuilder.class);
    //hadoop配置文件所放的位置
    private static final String SPARK_CONF_DIR = System.getProperty("user.dir") + Path.SEPARATOR + "conf";

    static {
        ClassPathResLoader.loadResourceDir(SPARK_CONF_DIR);//加载hadoop配置文件到classpath中
    }

    public static SparkSession build(MarketConf conf) {
        System.setProperty("HADOOP_USER_NAME", "hyshf");
        Logger.getLogger("org").setLevel(Level.ERROR);
        logger.debug("Initializing SparkSession with configuration: ");
        Builder builder = SparkSession.builder()
                .appName("Market_Spark_" + conf.getDmDatatable().getDatatable_en_name());

        //TODO 引擎有两种，一种是 spark，一种是 spark[local]
        if (SqlEngine.SPARK.getCode().equals(conf.getDmDatatable().getSql_engine())) {
            builder = builder.master("yarn-client")
                    .enableHiveSupport();
            logger.info("spark yarn-client enableHiveSupport.");
        } else {
            builder = builder.master("local[*]");
            logger.info("spark local[*]");
        }
        //TODO 设置租户，需配置动态资源池 名称为 root.${租户名称}
//		builder.config("spark.yarn.queue", StringUtils.isBlank(lessor)?"root.default":"root."+lessor);
        SparkSession sparkSession = builder.getOrCreate();
        //注册函数
        registerUdf(sparkSession.udf());
        logger.info(" SparkSession initialization is completed.");
        return sparkSession;
    }

    /**
     * 注册临时的自定义函数
     *
     * @param udfRegister
     * @author yuanqi
     * Date:2018年6月12日下午4:35:33
     * @since JDK 1.7
     */
    public static void registerUdf(UDFRegistration udfRegister) {

        FunctionsReader fr = new FunctionsReader();
        Iterator<Function> iterator = fr.iterator();
        while (iterator.hasNext()) {
            Function next = iterator.next();
            udfRegister.registerJava(next.getName(), next.getClassName(), transformType(next.getDateType()));
            logger.info("Function registered: " + next.getName());
        }
    }

    private static DataType transformType(String type) {

        switch (type) {
            case "string":
                return DataTypes.StringType;
            case "binary":
                return DataTypes.BinaryType;
            case "boolean":
                return DataTypes.BooleanType;
            case "byte":
                return DataTypes.ByteType;
            case "date":
                return DataTypes.DateType;
            case "double":
                return DataTypes.DoubleType;
            case "integer":
                return DataTypes.IntegerType;
            case "long":
                return DataTypes.LongType;
            case "short":
                return DataTypes.ShortType;
            case "float":
                return DataTypes.FloatType;
            case "null":
                return DataTypes.NullType;
            default:
                logger.info("不支持类型：" + type + " ,使用默认类型： string");
                return DataTypes.StringType;
        }
    }
}
