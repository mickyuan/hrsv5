package hrds.h.biz.realloader;

import hrds.commons.exception.AppSystemException;
import hrds.h.biz.config.MarketConf;

import java.util.HashMap;
import java.util.Map;

/**
 * 所有Loader实现的基类
 * 目的：
 * 1.基础参数初始化
 * 2.为子类可能无法实现接口的方法做基础实现
 *
 * @Author: Mick Yuan
 */
public abstract class AbstractRealLoader implements Loader {
    /**
     * 最终输出表的存储层配置Map
     */
    protected final Map<String, String> tableLayerAttrs = new HashMap<>();
    /**
     * 集市作业配置类实体
     */
    protected final MarketConf conf;
    /**
     * 最终输出的表名
     */
    protected final String tableName;
    /**
     * 跑批日期
     */
    protected final String etlDate;
    /**
     * 集市配置表主键
     */
    protected final String datatableId;
    /**
     * 用户需要执行的最终sql
     */
    protected final String finalSql;
    /**
     * 是否是多集市输入同表
     */
    protected boolean isMultipleInput;

    protected AbstractRealLoader(MarketConf conf) {
        this.conf = conf;
        tableName = conf.getTableName();
        etlDate = conf.getEtlDate();
        datatableId = conf.getDatatableId();
        finalSql = conf.getFinalSql();
        isMultipleInput = conf.isMultipleInput();
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
