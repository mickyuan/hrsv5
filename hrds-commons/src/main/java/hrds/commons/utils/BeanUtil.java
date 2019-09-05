package hrds.commons.utils;

import fd.ng.db.resultset.Result;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>标    题: 核心框架</p>
 * <p>描    述: BEAN操作实用类</p>
 * <p>版    权: Copyright (c) 2010</p>
 * <p>公    司: 上海泓智信息科技有限公司</p>
 * <p>创建时间: 2010-12-13 上午11:48:57</p>
 *
 * @author 产品开发部
 * @version 2.0
 * BeanUtil
 */
public class BeanUtil {
    private static final Logger logger = LogManager.getLogger();
    /**
     * 比较日期、字符串、包装类等对象值是否相等
     * @param valueObj1
     * @param valueObj2
     * @return
     */
    private static boolean compareValue(Object valueObj1, Object valueObj2) {

        if (valueObj1 instanceof Integer) {
            if (!(valueObj2 instanceof Integer))
                return false;
            int value1 = ((Integer) valueObj1).intValue();
            int value2 = ((Integer) valueObj2).intValue();
            return (value1 == value2);
        } else if (valueObj1 instanceof Short) {
            if (!(valueObj2 instanceof Short))
                return false;
            short sh1 = ((Short) valueObj1).shortValue();
            short sh2 = ((Short) valueObj2).shortValue();
            return (sh1 == sh2);
        } else if (valueObj1 instanceof String) {
            if (!(valueObj2 instanceof String))
                return false;
            String value1 = ((String) valueObj1);
            String value2 = ((String) valueObj2);
            return (value1.equals(value2));
        } else if (valueObj1 instanceof java.math.BigDecimal) {
            if (!(valueObj2 instanceof java.math.BigDecimal))
                return false;
            java.math.BigDecimal value1 = ((java.math.BigDecimal) valueObj1);
            java.math.BigDecimal value2 = ((java.math.BigDecimal) valueObj2);
            return ((value1.compareTo(value2)) == 0);
        } else if (valueObj1 instanceof Double) {
            if (!(valueObj2 instanceof Double))
                return false;
            Double value1 = ((Double) valueObj1);
            Double value2 = ((Double) valueObj2);
            return ((value1.compareTo(value2)) == 0);
        } else if (valueObj1 instanceof Float) {
            if (!(valueObj2 instanceof Float))
                return false;
            Float value1 = ((Float) valueObj1);
            Float value2 = ((Float) valueObj2);
            return ((value1.compareTo(value2)) == 0);
        } else if (valueObj1 instanceof Long) {
            if (!(valueObj2 instanceof Long))
                return false;
            Long value1 = ((Long) valueObj1);
            Long value2 = ((Long) valueObj2);
            return ((value1.compareTo(value2)) == 0);
        } else if (valueObj1 instanceof Boolean) {
            if (!(valueObj2 instanceof Boolean))
                return false;
            boolean value1 = ((Boolean) valueObj1).booleanValue();
            boolean value2 = ((Boolean) valueObj2).booleanValue();
            return (value1 == value2);
        } else if (valueObj1 instanceof java.sql.Date) {
            throw new RuntimeException("Unspuly type!");
        } else {
            logger.info("valueObj1=" + valueObj1);
            throw new RuntimeException("Unknown value type!");
        }
    }

    /**
     * 比较日期、字符串、包装类等对象值的大小关系
     *
     * @param valueObj1
     * @param valueObj2
     * @return 0:相等,-1:valueObj1<valueObj2,1:valueObj1>valueObj2
     */
    private static int compareTo(Object valueObj1, Object valueObj2) {

        if (valueObj1 instanceof Integer) {
            if (!(valueObj2 instanceof Integer))
                return -1;
            int value1 = ((Integer) valueObj1).intValue();
            int value2 = ((Integer) valueObj2).intValue();
            if (value1 < value2)
                return -1;
            else if (value1 == value2)
                return 0;
            else
                return 1;
        } else if (valueObj1 instanceof Short) {
            if (!(valueObj2 instanceof Short))
                return -1;
            short value1 = ((Short) valueObj1).shortValue();
            short value2 = ((Short) valueObj2).shortValue();
            if (value1 < value2)
                return -1;
            else if (value1 == value2)
                return 0;
            else
                return 1;
        } else if (valueObj1 instanceof String) {
            if (!(valueObj2 instanceof String))
                return -1;
            String value1 = ((String) valueObj1);
            String value2 = ((String) valueObj2);
            return (value1.compareTo(value2));
        } else if (valueObj1 instanceof java.math.BigDecimal) {
            if (!(valueObj2 instanceof java.math.BigDecimal))
                return -1;
            java.math.BigDecimal value1 = ((java.math.BigDecimal) valueObj1);
            java.math.BigDecimal value2 = ((java.math.BigDecimal) valueObj2);
            return ((value1.compareTo(value2)));
        } else if (valueObj1 instanceof Double) {
            if (!(valueObj2 instanceof Double))
                return -1;
            Double value1 = ((Double) valueObj1);
            Double value2 = ((Double) valueObj2);
            return ((value1.compareTo(value2)));
        } else if (valueObj1 instanceof Float) {
            if (!(valueObj2 instanceof Float))
                return -1;
            Float value1 = ((Float) valueObj1);
            Float value2 = ((Float) valueObj2);
            return ((value1.compareTo(value2)));
        } else if (valueObj1 instanceof Long) {
            if (!(valueObj2 instanceof Long))
                return -1;
            Long value1 = ((Long) valueObj1);
            Long value2 = ((Long) valueObj2);
            return ((value1.compareTo(value2)));
        } else if (valueObj1 instanceof Boolean) {
            if (!(valueObj2 instanceof Boolean))
                return -1;
            boolean value1 = ((Boolean) valueObj1).booleanValue();
            boolean value2 = ((Boolean) valueObj2).booleanValue();
            if (value1 == value2)
                return 0;
            else
                return -1;
        } else if (valueObj1 instanceof java.sql.Date) {
            throw new RuntimeException("Unspuly type!");
        } else {
            logger.info("valueObj1=" + valueObj1);
            throw new RuntimeException("Unknown value type!");
        }
    }

    public static void setBeanProperty(Object bean, String propertyName, String propertyValue) {

        String errorInfo = "class=" + bean.getClass().getName() + "，name=" + propertyName;
        PropertyDescriptor descriptor;
        try {
            descriptor = new PropertyDescriptor(propertyName, bean.getClass());
        } catch (IntrospectionException e) {
            throw new RuntimeException("没有此Bean属性：" + errorInfo, e);
        }

        Method setter = descriptor.getWriteMethod();
        Class paramClazz = setter.getParameterTypes()[0];
        Object paramValue;
        try {
            Constructor paramCtor = paramClazz.getConstructor(new Class[]{String.class});
            paramValue = paramCtor.newInstance(new Object[]{propertyValue});
        } catch (Exception e) {
            throw new RuntimeException("无法将String转换成Bean属性类型：" + errorInfo, e);
        }
        try {
            setter.invoke(bean, new Object[]{paramValue});
        } catch (Exception e) {
            throw new RuntimeException("无法将属性值赋入Bean：" + errorInfo, e);
        }
    }

    public static void setMapProperty(Map bean, String propertyName, String[] propertyValue) {

        if (propertyValue.length == 1) {
            bean.put(propertyName, propertyValue[0]);
        } else {
            bean.put(propertyName, propertyValue);
        }
    }
}
