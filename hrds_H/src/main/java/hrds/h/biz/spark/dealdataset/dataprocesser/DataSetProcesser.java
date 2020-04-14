package hrds.h.biz.spark.dealdataset.dataprocesser;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;


public interface DataSetProcesser {
	/**
	 * 根据业务处理DataSet
	 *  
	 * @return  
	 * @author yuanqi
	 * Date:2018年5月13日下午3:27:32 
	 * @since JDK 1.7
	 */
	Dataset<Row> process(Dataset<Row> dataSet);
}
