package hrds.k.biz.algorithms.main;

import com.alibaba.fastjson.JSONObject;
import hrds.commons.utils.Constant;
import hrds.k.biz.algorithms.conf.AlgorithmsConf;
import hrds.k.biz.algorithms.impl.DistributedHyFD;
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


public class HyFDMain {
	//打印日志
	private static final Logger LOGGER = LogManager.getLogger();

	public static void main(String[] args) throws IOException {
		String table_name = args[0];
		String data = FileUtils.readFileToString(new File(Constant.ALGORITHMS_CONF_SERIALIZE_PATH
				+ table_name), StandardCharsets.UTF_8);
//		Conf conf = Conf.getConf(args);
		AlgorithmsConf algorithmsConf = JSONObject.parseObject(data, AlgorithmsConf.class);
		executeFd(algorithmsConf);
	}

	public static void executeFd(AlgorithmsConf algorithmsConf) throws IOException {
		SparkConf sparkConf = new SparkConf().setAppName("DistributedHybridFD").setMaster("local[*]");
		sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
		sparkConf.set("spark.kryoserializer.buffer.mb", "1024");

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
				LOGGER.info("读取字段" + Arrays.toString(algorithmsConf.getSelectColumnArray()));
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
				LOGGER.info("读取字段" + Arrays.toString(algorithmsConf.getSelectColumnArray()));
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

		// lmPDP
		DistributedHyFD.sc = sc;
		DistributedHyFD.df = df;
		DistributedHyFD.columnNames = DistributedHyFD.df.columns();
		DistributedHyFD.numberAttributes = DistributedHyFD.columnNames.length;
		DistributedHyFD.datasetFile = algorithmsConf.getInputFilePath();
		DistributedHyFD.outputFile = algorithmsConf.getOutputFilePath() + Constant.HYFD_RESULT_PATH_NAME;
		;

		DistributedHyFD.numPartitions = algorithmsConf.getNumPartition();
		DistributedHyFD.batchSize = algorithmsConf.getBatchSize();
		DistributedHyFD.validationBatchSize = algorithmsConf.getValidationBatchSize();
		if (algorithmsConf.getMaxDepth() > 0) DistributedHyFD.maxLhsSize = algorithmsConf.getMaxDepth();

		LOGGER.info("\n  ======= Starting HyFD =======");
		LOGGER.info("datasetFile: " + algorithmsConf.getInputFilePath());
		LOGGER.info(DistributedHyFD.sc.sc().applicationId());
		LOGGER.info("numPartitions: " + algorithmsConf.getNumPartition());
		LOGGER.info("batchSize: " + algorithmsConf.getBatchSize());
		LOGGER.info("validationBatchSize: " + algorithmsConf.getValidationBatchSize());
		LOGGER.info("numberAttributes: " + DistributedHyFD.numberAttributes);

		DistributedHyFD.execute();
		DistributedHyFD.sc.stop();
	}
}

