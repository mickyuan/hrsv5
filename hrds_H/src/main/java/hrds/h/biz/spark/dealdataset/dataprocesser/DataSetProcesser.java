package hrds.h.biz.spark.dealdataset.dataprocesser;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * dataset对象的处理接口
 * @author mick
 */
public interface DataSetProcesser {
	/**
	 * 根据业务处理DataSet
	 * @param dataSet 需要被处理的dataset对象
	 * @return  处理之后的dataset对象
	 */
	Dataset<Row> process(Dataset<Row> dataSet);
}
