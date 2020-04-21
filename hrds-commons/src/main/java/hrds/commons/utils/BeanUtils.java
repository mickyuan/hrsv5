package hrds.commons.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

@DocClass(desc = "实体Bean工具类", author = "BY-HLL", createdate = "2020/4/20 0020 下午 02:58")
public class BeanUtils {


    @Method(desc = "实体Bean通用属性值转换",
            logicStep = "对于所有属性名称都相同的情况,将属性值从原始bean复制到目标bean")
    @Param(name = "orig", desc = "原始实体对象", range = "Object类型")
    @Param(name = "dest", desc = "目标实体对象", range = "Object类型")
    @Return(desc = "返回值说明", range = "返回值取值范围")
    public static void copyProperties(Object orig, Object dest) throws Exception {

        // 获取原始Bean属性
        BeanInfo origBean = Introspector.getBeanInfo(orig.getClass(), Object.class);
        PropertyDescriptor[] origProperty = origBean.getPropertyDescriptors();
        // 获取目标Bean属性
        BeanInfo destBean = Introspector.getBeanInfo(dest.getClass(), java.lang.Object.class);
        PropertyDescriptor[] destProperty = destBean.getPropertyDescriptors();
        //复制属性
        try {
            for (PropertyDescriptor origDescriptor : origProperty) {
                for (PropertyDescriptor destDescriptor : destProperty) {
                    if (origDescriptor.getName().equals(destDescriptor.getName())) {
                        // 调用 orig 的getter方法和 dest 的setter方法
                        destDescriptor.getWriteMethod().invoke(dest, origDescriptor.getReadMethod().invoke(orig));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("属性复制失败:" + e.getMessage());
        }
    }
}
