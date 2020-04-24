package hrds.commons.utils;

import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.entity.Sys_para;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本类获取数据库sys_para中的数据，将数据一次性加载到内存中 ，如果配置修改了，必须重新启动
 */
    public class PropertyParaValue {
    private static final Logger logger = LogManager.getLogger(PropertyParaValue.class.getName());
    private static Map<String, String> mapParaType;

    /**
     * 根据类型，将参数表中的数据全部加载到内存中，以key为map的key，因为key不可能重复
     *
     * 1、通过sql查询出参数类型的数据
     * 2、将数据加载到静态块的map中
     */
    static {
        mapParaType = new HashMap<String, String>();
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            //1、通过sql查询出参数类型的数据
            List<Sys_para> sys_paras = SqlOperator.queryList(db, Sys_para.class, "select para_name,para_value from sys_para where para_type = ?", "server.properties");
            for (Sys_para sys_para : sys_paras) {
                String para_name = sys_para.getPara_name();
                String para_value = sys_para.getPara_value();
                //2、将数据加载到静态块的map中
                mapParaType.put(para_name, para_value);
            }
        }
    }

    /**
     * 返回sys_para表中名字为name的字符串值。如果没有找到, 则返回默认值defaultValue.
     *
     * @param name         名字。
     * @param defaultValue 默认值。
     * @return 返回sys_para表中名字为name的字符串值。如果没有找到, 则返回默认值defaultValue
     */
    public static String getString(String name, String defaultValue) {

        try {
            return !StringUtil.isEmpty(mapParaType.get(name)) ? mapParaType.get(name) : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 返回sys_para表中名字为name的字符串值。如果没有找到, 则返回默认值defaultValue.
     *
     * @param name         名字。
     * @param defaultValue 默认值。
     * @return 返回sys_para表中名字为name的字符串值。如果没有找到, 则返回默认值defaultValue.
     */
    public static int getInt(String name, int defaultValue) {

        try {
            return Integer.parseInt(mapParaType.get(name));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 返回sys_para表中名字为name的字符串值。如果没有找到, 则返回默认值defaultValue.
     *
     * @param name         名字。
     * @param defaultValue 默认值。
     * @return 返回sys_para表中名字为name的字符串值。如果没有找到, 则返回默认值defaultValue.
     */
    public static long getLong(String name, long defaultValue) {

        try {
            return Long.parseLong(mapParaType.get(name));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 返回sys_para表中名字为name的字符串值。如果没有找到, 则返回默认值defaultValue.
     *
     * @param name         名字。
     * @param defaultValue 默认值。
     * @return 返回sys_para表中名字为name的字符串值。如果没有找到, 则返回默认值defaultValue.
     */

    public static float getFloat(String name, float defaultValue) {

        try {
            return Double.valueOf(mapParaType.get(name)).floatValue();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 返回sys_para表中名字为name的字符串值。如果没有找到, 则返回默认值defaultValue.
     *
     * @param name         名字。
     * @param defaultValue 默认值。
     * @return 返回sys_para表中名字为name的字符串值。如果没有找到, 则返回默认值defaultValue.
     */
    public static double getDouble(String name, double defaultValue) {

        try {
            return Double.valueOf(mapParaType.get(name)).doubleValue();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 返回sys_para表中名字为name的字符串值。如果没有找到, 则返回默认值defaultValue.
     *
     * @param name         名字。
     * @param defaultValue 默认值。
     * @return 返回sys_para表中名字为name的字符串值。如果没有找到, 则返回默认值defaultValue.
     */
    public static Boolean getBoolean(String name, boolean defaultValue) {

        return StringUtil.isBlank(mapParaType.get(name)) ?
                defaultValue : Boolean.parseBoolean(mapParaType.get(name));
    }
}
