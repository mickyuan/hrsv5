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
	/*
	 * 获取处理过的dataset
	 */
	Dataset<Row> getDataset();
	
	SparkSession getSparkSession();
}
