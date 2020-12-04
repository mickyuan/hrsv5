package hrds.k.biz.algorithms.main;

import com.alibaba.fastjson.JSONObject;
import hrds.commons.utils.Constant;
import hrds.k.biz.algorithms.conf.AlgorithmsConf;
import hrds.k.biz.algorithms.impl.DistributedHyUCC;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;


public class HyUCCMain {
	//打印日志
	private static final Logger LOGGER = LogManager.getLogger();
	public static void main(String[] args) throws IOException {
		String table_name = args[0];
		String data = FileUtils.readFileToString(new File(Constant.ALGORITHMS_CONF_SERIALIZE_PATH
				+ table_name), StandardCharsets.UTF_8);
//		Conf conf = Conf.getConf(args);
		AlgorithmsConf algorithmsConf = JSONObject.parseObject(data, AlgorithmsConf.class);
		executeUCC(algorithmsConf);
	}

	public static void executeUCC(AlgorithmsConf algorithmsConf) throws IOException {
		SparkConf sparkConf = new SparkConf().setAppName("DistributedHybridUCC").setMaster("local[*]");
		sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
		sparkConf.set("spark.kryoserializer.buffer.mb","1024");
		JavaSparkContext sc = new JavaSparkContext(sparkConf);
		SparkSession spark = SparkSession
				.builder()
				.getOrCreate();
		Dataset<Row> df;
		if (algorithmsConf.getUseParquet()) {
			df = spark.read().parquet(algorithmsConf.getInputFilePath());
		} else if (algorithmsConf.getUseCsv()) {
			df = spark.read()
					.option("header", algorithmsConf.getInputFileHasHeader())
					.option("delimiter", algorithmsConf.getInputFileSeparator())
					.csv(algorithmsConf.getInputFilePath());
		} else {
			Properties properties = new Properties();
			properties.setProperty("driver", algorithmsConf.getDriver());
			properties.setProperty("user", algorithmsConf.getUser());
			properties.setProperty("password", algorithmsConf.getPassword());
			if (algorithmsConf.getPredicates() != null) {
				//默认值读取100万条进行数据分析
				LOGGER.info("读取字段"+ Arrays.toString(algorithmsConf.getSelectColumnArray()));
				if(algorithmsConf.getSelectColumnArray().length>100){
					//如果字段数大于100列，读取20万条分析
					algorithmsConf.setJdbcLimit(200000);
				}else if (algorithmsConf.getSelectColumnArray().length > 30) {
					//如果字段数大于30列，读取40万条分析
					algorithmsConf.setJdbcLimit(400000);
				}else if(algorithmsConf.getSelectColumnArray().length > 20){
					//如果字段数大于20列小于30列，读取60万条分析
					algorithmsConf.setJdbcLimit(600000);
				}else if(algorithmsConf.getSelectColumnArray().length > 15){
					//如果字段数大于15列小于20列，读取80万条分析
					algorithmsConf.setJdbcLimit(800000);
				}
				df = spark.read().jdbc(algorithmsConf.getJdbcUrl(), algorithmsConf.getTable_name(), algorithmsConf.getPredicates(),
						properties).selectExpr(algorithmsConf.getSelectColumnArray()).limit(algorithmsConf.getJdbcLimit());
			} else {
				//默认值读取100万条进行数据分析
				LOGGER.info("读取字段"+ Arrays.toString(algorithmsConf.getSelectColumnArray()));
				if(algorithmsConf.getSelectColumnArray().length>100){
					//如果字段数大于100列，读取20万条分析
					algorithmsConf.setJdbcLimit(200000);
				}else if (algorithmsConf.getSelectColumnArray().length > 30) {
					//如果字段数大于30列，读取40万条分析
					algorithmsConf.setJdbcLimit(400000);
				}else if(algorithmsConf.getSelectColumnArray().length > 20){
					//如果字段数大于20列小于30列，读取60万条分析
					algorithmsConf.setJdbcLimit(600000);
				}else if(algorithmsConf.getSelectColumnArray().length > 15){
					//如果字段数大于15列小于20列，读取80万条分析
					algorithmsConf.setJdbcLimit(800000);
				}
				df = spark.read().jdbc(algorithmsConf.getJdbcUrl(), algorithmsConf.getTable_name(),
						properties).selectExpr(algorithmsConf.getSelectColumnArray()).limit(algorithmsConf.getJdbcLimit());
			}
		}

		DistributedHyUCC.sc = sc;
		DistributedHyUCC.df = df;
		DistributedHyUCC.columnNames = DistributedHyUCC.df.columns();
		DistributedHyUCC.numberAttributes = DistributedHyUCC.columnNames.length;
		DistributedHyUCC.datasetFile = algorithmsConf.getInputFilePath();
		DistributedHyUCC.outputFile = algorithmsConf.getOutputFilePath() + Constant.HYUCC_RESULT_PATH_NAME ;

		DistributedHyUCC.numPartitions = algorithmsConf.getNumPartition();
		DistributedHyUCC.batchSize = algorithmsConf.getBatchSize();
		DistributedHyUCC.validationBatchSize = algorithmsConf.getValidationBatchSize();
		if (algorithmsConf.getMaxDepth() > 0) DistributedHyUCC.maxLhsSize = algorithmsConf.getMaxDepth();

		LOGGER.info("\n  ======= Starting HyUCC =======");
		LOGGER.info("datasetFile: " + algorithmsConf.getInputFilePath());
		LOGGER.info(DistributedHyUCC.sc.sc().applicationId());
		LOGGER.info("numPartitions: " + algorithmsConf.getNumPartition());
		LOGGER.info("batchSize: " + algorithmsConf.getBatchSize());
		LOGGER.info("validationBatchSize: " + algorithmsConf.getValidationBatchSize());
		LOGGER.info("numberAttributes: " + DistributedHyUCC.numberAttributes);

		DistributedHyUCC.execute();
		DistributedHyUCC.sc.stop();
	}
}
