package hrds.agent.job.biz.utils;

import fd.ng.core.annotation.DocClass;
import hrds.agent.job.biz.constant.EnumConstantInterface;

@DocClass(desc = "用于枚举类的工具", author = "WangZhengcheng")
public class EnumUtil {

	/**
	 * 根据枚举键值获得枚举对象
	 *
	 * @param enumClass 类令牌
	 * @param code      枚举键值
	 * @return T  类令牌指定的类
	 * @author 13616
	 * @date 2019/8/7 11:40
	 * @note 使用的枚举必须实现EnumConstantInterface接口
	 */
	public static <T extends EnumConstantInterface> T getEnumByCode(Class<T> enumClass, int code) {
		for (T each : enumClass.getEnumConstants()) {
			if (each.getCode() == code) {
				return each;
			}
		}
		return null;
	}
}
