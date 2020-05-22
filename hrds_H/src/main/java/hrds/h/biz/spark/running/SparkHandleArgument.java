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
    /**
     * 处理类型，如 database,hive,hbase,solr.etc
     */
    private Store_type handleType;
    private boolean overWrite;
    /**
     * spark 任务是否为增量， 默认为 false
     */
    private boolean increment = false;
    private boolean isMultipleInput = false;
    private String tableName;
    private String datatableId;
    private String etlDate;

    public String getDatatableId() {
        return datatableId;
    }

    public void setDatatableId(String datatableId) {
        this.datatableId = datatableId;
    }

    public String getEtlDate() {
        return etlDate;
    }

    public void setEtlDate(String etlDate) {
        this.etlDate = etlDate;
    }

    public void setHandleType(Store_type handleType) {
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

    public boolean isMultipleInput() {
        return isMultipleInput;
    }

    public void setMultipleInput(boolean multipleInput) {
        isMultipleInput = multipleInput;
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

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }

    public static SparkHandleArgument fromString(String handleArgs, Class<? extends SparkHandleArgument> aClass) {

        return JsonUtil.toObjectSafety(handleArgs, aClass)
                .orElseThrow(() -> new AppSystemException(String.format("handle string转对象失败：[%s] -> [%s].",
                        handleArgs, aClass.getSimpleName())));
    }

    /**
     * 数据库相关参数实体
     */
    public static class DatabaseArgs extends SparkHandleArgument {

        String driver;
        String url;
        String user;
        String password;
        String databaseType;

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

        public String getDatabaseType() {
            return databaseType;
        }

        public void setDatabaseType(String databaseType) {
            this.databaseType = databaseType;
        }
    }
}
