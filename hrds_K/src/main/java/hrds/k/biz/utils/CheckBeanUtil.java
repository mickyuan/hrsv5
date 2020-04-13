package hrds.k.biz.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.exception.BusinessException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@DocClass(desc = "用于检查bean的工具类", author = "BY-HLL", createdate = "2020/4/10 0010 上午 10:43")
public class CheckBeanUtil {

    @Method(desc = "检查bean中属性值是否全为null",
            logicStep = "检查bean中属性值是否全为null")
    @Param(name = "object", desc = "bean对象", range = "bean对象")
    @Return(desc = "全为null为true否则为false", range = "全为null为true否则为false")
    public static boolean checkFullNull(Object object) {
        java.lang.reflect.Method[] methods = object.getClass().getMethods();
        for (java.lang.reflect.Method method : methods) {
            String methodName = method.getName();
            if (methodName.startsWith("get") && !methodName.equals("getClass")) {
                try {
                    Object check = method.invoke(object);
                    if (null != check) {
                        return false;
                    }
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    @Method(desc = "根据正则表达式提取bean中的value值",
            logicStep = "根据正则表达式提取bean中的value值" +
                    "1.匹配的是value值，而不是bean的属性名" +
                    "2.匹配并提取符合正则表达式的部分字符串，而不是整个value值" +
                    "3.返回符合正则表达式的部分字符串数组")
    @Param(name = "object", desc = "bean对象", range = "bean对象")
    @Param(name = "regEx", desc = "正则表达式（如：(?=#\\{)(.*?)(?>\\})，匹配#{}", range = "String类型")
    @Return(desc = "提取符合正则表达式的部分字符串数组", range = "提取符合正则表达式的部分字符串数组")
    public static List<String> getBeanValueWithPattern(Object object, String regEx) {
        //初始化结果集
        List<String> str_s = new ArrayList<>();
        java.lang.reflect.Method[] methods = object.getClass().getMethods();
        for (java.lang.reflect.Method method : methods) {
            String methodName = method.getName();
            if (methodName.startsWith("get") && !methodName.equals("getClass")) {
                try {
                    Object check = method.invoke(object);
                    if (null != check) {
                        Pattern pattern = Pattern.compile(regEx);
                        Matcher matcher = pattern.matcher(check.toString());
                        while (matcher.find()) {
                            str_s.add(matcher.group());
                        }
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    throw new BusinessException("通过正则表达式提取字符串数组失败!");
                }
            }
        }
        return str_s;
    }
}
