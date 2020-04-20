package hrds.h.biz.realloader;

import fd.ng.core.utils.StringUtil;
import hrds.commons.entity.Datatable_field_info;
import hrds.h.biz.config.MarketConf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    String buildCreateTableColumnTypes() {

        final StringBuilder createTableColumnTypes = new StringBuilder(300);
        conf.getDatatableFields().forEach(field -> {

            createTableColumnTypes
                    .append(field.getField_en_name())
                    .append(" ").append(field.getField_type());

            String fieldLength = field.getField_length();
            if (StringUtil.isNotBlank(fieldLength)) {
                createTableColumnTypes.append("(")
                        .append(fieldLength).append(")");
            }

            createTableColumnTypes.append(",");

        });
        //把最后一个逗号给删除掉
        createTableColumnTypes.deleteCharAt(createTableColumnTypes.length() - 1);

        String s = createTableColumnTypes.toString();
        StringUtil.replaceLast(s,"string","char(32)");
        StringUtil.replaceLast(s,"string","char(8)");
        StringUtil.replaceLast(s,"string","char(8)");

        return s;

    }

    /**
     * 不带有 hyren 字段的所有字段，以逗号隔开
     *
     * @return
     */
    String columnsWithoutHyren() {
                //后面的三个hyren字段去掉
        return conf.getDatatableFields().subList(0, conf.getDatatableFields().size() - 4)
                .stream()
                .map(Datatable_field_info::getField_en_name)
                .collect(Collectors.joining(","));
    }

}
