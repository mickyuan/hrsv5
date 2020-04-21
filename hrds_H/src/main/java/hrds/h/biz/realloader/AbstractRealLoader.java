package hrds.h.biz.realloader;

import hrds.h.biz.config.MarketConf;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Mick Yuan
 * @Date:
 * @Since jdk1.8
 */
public abstract class AbstractRealLoader implements Loader {
    final Map<String, String> tableLayerAttrs = new HashMap<>();
    protected final MarketConf conf;
    final String tableName;

    protected AbstractRealLoader(MarketConf conf) {
        this.conf = conf;
        tableName = conf.getTableName();
        initTableLayerProperties();
    }

    /**
     * 将存储层的配置的（k,v）初始化到 Map 中
     */
    private void initTableLayerProperties() {

        conf.getDataStoreLayerAttrs().forEach(propertyRecord ->
                tableLayerAttrs.put(propertyRecord.getStorage_property_key()
                        , propertyRecord.getStorage_property_val()));
    }
}
