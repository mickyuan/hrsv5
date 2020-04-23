package hrds.h.biz.realloader;

import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.entity.Datatable_field_info;
import hrds.h.biz.config.MarketConfUtils;

import java.util.List;
import java.util.stream.Collectors;

import static hrds.commons.utils.Constant.*;
import static hrds.commons.utils.Constant.EDATENAME;

/**
 * 一些共用的方法
 *
 * @Author: Mick Yuan
 * @Date:
 * @Since jdk1.8
 */
public class Utils {

    static String buildCreateTableColumnTypes(List<Datatable_field_info> fields, boolean isDatabase) {

        final StringBuilder createTableColumnTypes = new StringBuilder(300);
        fields.forEach(field -> {

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

        //如果是database类型 则类型为定长char类型，否则为string类型（默认）
        if (isDatabase) {
            String str = MarketConfUtils.DEFAULT_STRING_TYPE;
            String s = createTableColumnTypes.toString();
            s = StringUtil.replaceLast(s, str, "char(32)");
            s = StringUtil.replaceLast(s, str, "char(8)");
            s = StringUtil.replaceLast(s, str, "char(8)");
            return s;
        }
        return createTableColumnTypes.toString();


    }

    /**
     * 不带有 hyren 字段的所有字段，以逗号隔开
     *
     * @return
     */
    static String columnsWithoutHyren(List<Datatable_field_info> fields) {
        //后面的三个hyren字段去掉
        return fields.subList(0, fields.size() - 3)
                .stream()
                .map(Datatable_field_info::getField_en_name)
                .collect(Collectors.joining(","));
    }

    /**
     * 恢复关系型数据库的数据到上次跑批结果
     *
     * @param db
     */
    static void restoreDatabaseData(DatabaseWrapper db, String tableName, String etlDate) {
        db.execute(String.format("DELETE FROM %s WHERE %s = %s",
                tableName, SDATENAME, etlDate));

        db.execute(String.format("UPDATE %s SET %s = %s WHERE %s = %s",
                tableName, EDATENAME, MAXDATE, EDATENAME, etlDate));
    }
}
