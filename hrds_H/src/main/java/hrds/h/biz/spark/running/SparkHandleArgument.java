package hrds.h.biz.spark.running;

import fd.ng.core.utils.JsonUtil;
import hrds.commons.codes.Store_type;
import hrds.commons.exception.AppSystemException;

/**
 * @Author: Mick Yuan
 * @Date:
 * @Since jdk1.8
 */
public class SparkHandleArgument {
    /** 处理类型，如 database,hive,hbase,solr.etc */
    private final Store_type handleType;
    private boolean overWrite;
    /** spark 任务是否为增量， 默认为 false */
    private boolean increment = false;
    private String tableName;

    public SparkHandleArgument(Store_type handleType) {
        this.handleType = handleType;
    }

    public final Store_type getHandleType() {
        return handleType;
    }

    public boolean isOverWrite() {
        return overWrite;
    }

    public void setOverWrite(boolean overWrite) {
        this.overWrite = overWrite;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isIncrement() {
        return increment;
    }

    public void setIncrement(boolean increment) {
        this.increment = increment;
    }

    public static class DatabaseArgs extends SparkHandleArgument {

        String driver;
        String url;
        String user;
        String password;
        String createTableColumnTypes;
        String databaseType;

        public DatabaseArgs(Store_type handleType) {
            super(handleType);
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getCreateTableColumnTypes() {
            return createTableColumnTypes;
        }

        public void setCreateTableColumnTypes(String createTableColumnTypes) {
            this.createTableColumnTypes = createTableColumnTypes;
        }

        public String getDatabaseType() {
            return databaseType;
        }

        public void setDatabaseType(String databaseType) {
            this.databaseType = databaseType;
        }
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }

    public static SparkHandleArgument fromString(String handleArgs) {

        return JsonUtil.toObjectSafety(handleArgs, SparkHandleArgument.class)
                .orElseThrow(() -> new AppSystemException("解析参数失败：" + handleArgs));
    }
}
