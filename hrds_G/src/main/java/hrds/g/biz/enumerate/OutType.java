package hrds.g.biz.enumerate;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.AppSystemException;

@DocClass(desc = "输出的数据形式", author = "dhw", createdate = "2020/4/2 15:21")
public enum OutType {

	/**
	 * 数据文件
	 */
	FILE("file", "数据文件"),

	/**
	 * 数据流
	 */
	STREAM("stream", "数据流");

	private final String code;

	private final String value;

	OutType(String code, String value) {
		this.code = code;
		this.value = value;
	}

	public String getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}

	/**
	 * 根据指定的代码值转换成对象
	 *
	 * @param code 本代码的代码值
	 * @return
	 */
	public static String ofValueByCode(String code) {
		for (OutType outType : OutType.values()) {
			if (outType.getCode().equals(code)) {
				return outType.value;
			}
		}
		return null;
	}

	/**
	 * 根据指定的代码值转换成对象
	 *
	 * @param code 本代码的代码值
	 * @return
	 */
	public static OutType ofEnumByCode(String code) {
		for (OutType outType : OutType.values()) {
			if (outType.getCode().equals(code)) {
				return outType;
			}
		}
		return null;
	}

	/**
	 * 判断是否输出数据类型所指定值
	 *
	 * @param outType 输出类型
	 * @return
	 */
	public static boolean isOutType(String outType) {
		if (StringUtil.isNotBlank(outType) && (OutType.STREAM == OutType.ofEnumByCode(outType)
				|| OutType.FILE == OutType.ofEnumByCode(outType))) {
			return true;
		} else {
			return false;
		}
	}

}
