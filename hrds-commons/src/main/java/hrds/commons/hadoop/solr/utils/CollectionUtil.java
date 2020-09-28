package hrds.commons.hadoop.solr.utils;

import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.PropertyParaValue;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class CollectionUtil {

    private static final Logger log = LogManager.getLogger();

    /**
     * 任意一个 Solr Server 的url，通过此url可以操作collection
     * collection的创建节点与 Solr Server 的url在哪个节点无关
     */
    public static final String solrUrl = PropertyParaValue.getString("solrUrl", "");
    /**
     * 默认的collection配置名称，在初始化平台时，就应该上传到zk上
     */
    public static final String DEFAULT_COLLECTION_CONFIG_NAME = PropertyParaValue.getString("solr.collection.config.name", "HrdsHbaseOverSolr");
    /**
     * 默认三个shards，两个replica
     */
    public static final int DEFAULT_NUM_SHARDS = 3;
    public static final int DEFAULT_REPLICATION_FACTOR = 2;

    public static final int DEFAULT_MAX_SHARDS_PER_NODE = DEFAULT_NUM_SHARDS;


    private static String doCollectionOperation(String operationUrl) throws IOException {
        HttpGet get = new HttpGet(operationUrl);
        String EntityStr;
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(get)) {
            EntityStr = EntityUtils.toString(response.getEntity());
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new AppSystemException("操作collection错误:\r\n" + EntityStr);
            }
        }
        return EntityStr;
    }

    public static String getCreateCollectionUrl(String collectionName, int numShards, int replicationFactor,
                                                int maxShardsPerNode, String collectionConfigName) {
        return String.format("%s/admin/collections?" +
                        "action=create" +
                        "&name=%s" +
                        "&numShards=%d" +
                        "&replicationFactor=%d" +
                        "&maxShardsPerNode=%d" +
                        "&collection.configName=%s", solrUrl, collectionName, numShards, replicationFactor,
                maxShardsPerNode, collectionConfigName);
    }

    public static void createCollection(String collectionName) throws IOException {
        String createUrl = getCreateCollectionUrl(collectionName, DEFAULT_NUM_SHARDS,
                DEFAULT_REPLICATION_FACTOR, DEFAULT_MAX_SHARDS_PER_NODE, DEFAULT_COLLECTION_CONFIG_NAME);
        doCollectionOperation(createUrl);
        log.info("创建collection {} 成功！", collectionName);
    }

    public static void deleteCollection(String collectionName) throws IOException {
        String deleteUrl = String.format("%s/admin/collections?action=delete&name=%s", solrUrl, collectionName);
        doCollectionOperation(deleteUrl);
        log.info("删除collection {} 成功！", collectionName);
    }

    public static void softCreateCollection(String collectionName) throws IOException {
        if (!collectionExists(collectionName)) {
            createCollection(collectionName);
        } else {
            log.info("collection 已存在，无需创建： {}", collectionName);
        }
    }

    public static void renameCollection(String srcName, String destName) {

    }

    public static void softDeleteCollection(String collectionName) throws IOException {
        if (collectionExists(collectionName)) {
            deleteCollection(collectionName);
        } else {
            log.info("collection 不存在，无需删除：{}.", collectionName);
        }
    }

    public static boolean collectionExists(String collectionName) throws IOException {

        String listUrl = String.format("%s/admin/collections?action=list", solrUrl);
        String listContent = doCollectionOperation(listUrl);
        return listContent.contains("<str>" + collectionName.trim() + "</str>");
    }

    public static void createCollectionAlias(String collectionAlias, String collectionName) throws IOException {
        String url = String.format("%s/admin/collections?action=createalias&name=%s&collections=%s",
                solrUrl, collectionAlias, collectionName);
        doCollectionOperation(url);
    }

    /**
     * @param tableName
     * @return
     */
    public static String getCollection(String tableName) {
        return tableName.toUpperCase();
    }

}
