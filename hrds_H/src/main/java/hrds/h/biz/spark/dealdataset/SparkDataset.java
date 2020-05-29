package hrds.h.biz.spark.dealdataset;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * 
 * Description: 将数据源中的数据通过查询转换成dataset
 *
 * Date:2018年5月13日下午3:21:11 
 * Copyright (c) 2018, yuanqi@beyondsoft.com All Rights Reserved.
 * 
 * @author yuanqi 
 * @version  
 * @since JDK 1.7
 */
public interface SparkDataset {
	/**
	 * 获取处理后的dataset对象
	 * @return 处理后的dataset对象
	 */
	Dataset<Row> getDataset();

	/**
	 * 获取SparkSession对象
	 * @return SparkSession对象
	 */
	SparkSession getSparkSession();
}
