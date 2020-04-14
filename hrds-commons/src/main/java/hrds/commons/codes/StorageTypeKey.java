package hrds.commons.codes;

import java.util.*;

/**
 * 存储层的key常量
 * 所有的存储层的key都应该从这里取
 */
public class StorageTypeKey {

    private static final Map<String, List<String>> FINALLY_STORAGE_KEYS = new HashMap<>();


    public static final String database_driver = "database_driver";
    public static final String jdbc_url = "jdbc_url";
    public static final String user_name = "user_name";
    public static final String database_pwd = "database_pwd";
    public static final String database_type = "database_type";

    public static final String core_site = "core-site.xml";
    public static final String hdfs_site = "hdfs-site.xml";
    public static final String yarn_site = "yarn-site.xml";
    public static final String hbase_site = "hbase-site.xml";
    public static final String mapred_site = "mapred-site.xml";
    public static final String keytab = "keytab";
    public static final String krb5 = "krb5";

    public static final String solr_url = "solr_url";

    public static final String zkhost = "zkhost";

    static {

        List<String> databaseKeys = new ArrayList<>(Arrays.
                asList(database_driver, jdbc_url, user_name, database_pwd, database_type));
        FINALLY_STORAGE_KEYS.put(Store_type.DATABASE.getCode(), databaseKeys);

        List<String> hiveKeys = new ArrayList<>(Arrays.
                asList(database_driver, jdbc_url, user_name, database_pwd, core_site,
                        hdfs_site, yarn_site, hbase_site, mapred_site, keytab, krb5));
        FINALLY_STORAGE_KEYS.put(Store_type.HIVE.getCode(), hiveKeys);

        List<String> hbaseKeys = new ArrayList<>(Arrays.
                asList(zkhost, core_site, hdfs_site, hbase_site, keytab, krb5));
        FINALLY_STORAGE_KEYS.put(Store_type.HIVE.getCode(), hbaseKeys);

        List<String> solrKeys = new ArrayList<>(Collections.singletonList(solr_url));
        FINALLY_STORAGE_KEYS.put(Store_type.HIVE.getCode(), solrKeys);

    }


    public static Map<String, List<String>> getFinallyStorageKeys() {

        return FINALLY_STORAGE_KEYS;
    }
}
