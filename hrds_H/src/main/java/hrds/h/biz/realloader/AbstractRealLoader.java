package hrds.h.biz.realloader;

import hrds.commons.exception.AppSystemException;
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
    protected final String tableName;
    protected final String etlDate;
    protected final String datatableId;
    protected final String preSql;
    protected final String finalSql;

    protected AbstractRealLoader(MarketConf conf) {
        this.conf = conf;
        tableName = conf.getTableName();
        etlDate = conf.getEtlDate();
        datatableId = conf.getDatatableId();
        preSql = conf.getPreSql();
        finalSql = conf.getFinalSql();
        initTableLayerProperties();
    }

    @Override
    public MarketConf getConf() {
        return conf;
    }

    /**
     * 子类无法实现该接口时，当调用该接口会抛出异常
     * 防止同一天重跑时出现数据错误问题
     */
    @Override
    public void restore() {
        throw new AppSystemException(this.getClass().getSimpleName() +
                " 不支持恢复数据至上次跑批结果");
    }

    @Override
    public void finalWork() {
        logDebug("该loader不兼容后置作业");
    }

    /**
     * 将存储层的配置的（k,v）初始化到 Map 中
     */
    private void initTableLayerProperties() {

        conf.getDataStoreLayerAttrs().forEach(propertyRecord ->
                tableLayerAttrs.put(propertyRecord.getStorage_property_key()
                        , propertyRecord.getStorage_property_val()));
    }

    @Override
    public void close() {
    }
}
