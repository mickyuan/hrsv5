package hrds.commons.utils;


import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.IsFlag;
import hrds.commons.exception.BusinessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据表字段工具", author = "BY-HLL", createdate = "2020/3/30 0030 下午 03:23")
public class DataTableFieldUtil {

    @Method(desc = "解析字段类型", logicStep = "解析字段类型")
    @Param(name = "col_type", desc = "字段类型", range = "String类型字符串")
    @Return(desc = "字段类型的Map", range = "data_type:数据类型,data_len:数据长度,decimal_point:数据小数长度")
    public static Map<String, String> parsingFiledType(String col_type) {
        Map<String, String> map = new HashMap<>();
        if (col_type.indexOf('(') != -1) {
            //数据类型
            String data_type = col_type.substring(0, col_type.indexOf('('));
            map.put("data_type", data_type);
            //数据长度
            String substring = col_type.substring(col_type.indexOf('(') + 1, col_type.lastIndexOf(')'));
            map.put("data_len", substring.split(",")[0]);
            //小数长度
            if (substring.split(",").length == 1) {
                map.put("decimal_point", "0");
            } else {
                map.put("decimal_point", substring.split(",")[1]);
            }
        } else {
            map.put("data_type", col_type);
            map.put("data_len", "0");
            map.put("decimal_point", "0");
        }
        if (map.isEmpty()) {
            throw new BusinessException("字段类型解析失败!");
        }
        return map;
    }

    @Method(desc = "字段的mate信息转换为List列表",
            logicStep = "字段的mate信息转换为List列表")
    @Param(name = "table_column_list", desc = "Table_column的List集合", range = "Table_column的对象List")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static List<Map<String, String>> metaInfoToList(List<Map<String, Object>> table_column_list) {
        //初始化返回的字段List
        List<Map<String, String>> column_list = new ArrayList<>();
        //转换mate信息为List列表
        table_column_list.forEach(table_column -> {
            Map<String, String> map = new HashMap<>();
            map.put("column_id", table_column.get("column_id").toString());
            map.put("column_name", table_column.get("column_name").toString());
            map.put("column_ch_name", table_column.get("column_ch_name").toString());
            //解析字段类型
            String column_type = table_column.get("column_type").toString();
            map.put("data_type", parsingFiledType(column_type).get("data_type"));
            map.put("data_len", parsingFiledType(column_type).get("data_len"));
            map.put("decimal_point", parsingFiledType(column_type).get("decimal_point"));
            //校验字段是否为主键信息,如果为空,默认设置为0:否
            String is_primary_key = table_column.get("is_primary_key").toString();
            if (StringUtil.isBlank(is_primary_key)) {
                is_primary_key = IsFlag.Fou.getCode();
            }
            map.put("is_primary_key", is_primary_key);
            column_list.add(map);
        });
        return column_list;
    }
}
