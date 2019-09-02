package hrds.agent.job.biz.utils;

import hrds.agent.job.biz.constant.EnumConstantInterface;

/**
 * ClassName: EnumUtil <br/>
 * Function: 用于枚举类的工具 <br/>
 * Date: 2019/8/5 11:01 <br/>
 * <p>
 * Author 13616
 * Version 1.0
 * Since JDK 1.8
 **/
public class EnumUtil {

    /**
     * 根据枚举键值获得枚举对象
     * @author   13616
     * @date     2019/8/7 11:40
     * @note    使用的枚举必须实现EnumConstantInterface接口
     *
     * @param enumClass 类令牌
     * @param code  枚举键值
     * @return   T  类令牌指定的类
     */
    public static <T extends EnumConstantInterface> T getEnumByCode(Class<T> enumClass, int code) {
        for (T each : enumClass.getEnumConstants()) {
            if(each.getCode() == code){
                return each;
            }
        }
        return null;
    }
}
