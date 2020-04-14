package hrds.h.biz.spark.running;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * @author mick
 * @title: Handle
 * @projectName hrsv5
 * @description: TODO
 * @date 20-4-13下午6:00
 */
public abstract class Handle {
    Dataset<Row> dataset;
    String tableName;

    Handle(Dataset<Row> dataset, String tableName) {
        this.dataset = dataset;
        this.tableName = tableName;
    }

    public abstract void handle();
}
