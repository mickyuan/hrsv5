package hrds.h.biz.spark.running;

import fd.ng.core.utils.StringUtil;
import org.apache.spark.sql.DataFrameWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;

import java.util.Properties;

/**
 * @Author: Mick Yuan
 * @Date:
 * @Since jdk1.8
 */
public class DatabaseHandle extends Handle {

    private SparkHandleArgument.DatabaseArgs databaseArgs;
    private Properties connProperties = new Properties();

    DatabaseHandle(Dataset<Row> dataset, SparkHandleArgument.DatabaseArgs databaseArgs, String tableName) {
        super(dataset, tableName);
        this.databaseArgs = databaseArgs;
        initProperties();
    }

    private void initProperties() {
        connProperties.setProperty("user", databaseArgs.getUser());
        connProperties.setProperty("password", databaseArgs.getPassword());
    }

    public void handle() {
        DataFrameWriter<Row> dataFrameWriter;
        if (databaseArgs.isOverWrite()) {
            dataFrameWriter = dataset.write().mode(SaveMode.Overwrite);
        } else {
            dataFrameWriter = dataset.write().mode(SaveMode.Append);
        }
        if(StringUtil.isNotBlank(databaseArgs.createTableColumnTypes)){
            dataFrameWriter = dataFrameWriter.option("createTableColumnTypes",databaseArgs.createTableColumnTypes);
        }
        dataFrameWriter.jdbc(databaseArgs.getUrl(), tableName, connProperties);
    }
}
